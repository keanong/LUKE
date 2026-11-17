package com.xdel.luke.debugtools;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * R7 Stage 1: local JVM tests for the Debug-only FakeEmailTransport.
 * Uses only the test constructor (explicit base directory, injected process killer) inside a JUnit
 * TemporaryFolder, so nothing touches the network, Mail, objGlobalMethods, Android APIs or a real process kill.
 */
public class FakeEmailTransportTest {

    private static final String[] TO = {"it@xdel.com"};
    /** yyyyMMdd_HHmmss_SSS_<6-digit seq>_<uuid>.txt */
    private static final String RECORD_NAME =
            "\\d{8}_\\d{6}_\\d{3}_\\d{6}_[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\\.txt";
    /** SHA-256 of the three ASCII bytes "abc". */
    private static final String SHA256_ABC = "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad";

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    private File baseDir;
    private AtomicInteger kills;
    private FakeEmailTransport transport;

    @Before
    public void setUp() throws IOException {
        baseDir = tmp.newFolder(FakeEmailTransport.DEBUG_DIR);
        kills = new AtomicInteger();
        transport = new FakeEmailTransport(baseDir, countingKiller(kills));
    }

    // ---------------- modes ----------------

    @Test
    public void defaultModeIsAcceptWhenNoModeFileExists() {
        assertEquals(FakeEmailTransport.MODE_ACCEPT, transport.readMode());
    }

    @Test
    public void modeTextIsTrimmedAndCaseInsensitiveAndUnknownTextIsFail() throws IOException {
        writeMode("");
        assertEquals(FakeEmailTransport.MODE_ACCEPT, transport.readMode());
        writeMode("  fail \n");
        assertEquals(FakeEmailTransport.MODE_FAIL, transport.readMode());
        writeMode("accept_then_kill");
        assertEquals(FakeEmailTransport.MODE_ACCEPT_THEN_KILL, transport.readMode());
        writeMode("BOGUS");
        assertEquals(FakeEmailTransport.MODE_FAIL, transport.readMode());
    }

    @Test
    public void acceptRecordsMessageAndReportsAccepted() throws IOException {
        assertTrue(transport.send(TO, "Test subject", "Line one\nLine two", "", null));

        List<File> records = transport.listRecords();
        assertEquals(1, records.size());
        String rec = read(records.get(0));
        assertEquals("ACCEPT", field(rec, "mode"));
        assertEquals("ACCEPTED", field(rec, "result"));
        assertEquals("[it@xdel.com]", field(rec, "to"));
        assertEquals("Test subject", field(rec, "subject"));
        assertEquals(String.valueOf("Line one\nLine two".length()), field(rec, "bodyChars"));
        assertTrue(rec.contains("body:\nLine one\nLine two\n"));
        assertEquals("false", field(rec, "attachmentExists"));
        assertEquals(0, kills.get());
    }

    @Test
    public void failRecordsAttemptAndReportsFailure() throws IOException {
        writeMode(FakeEmailTransport.MODE_FAIL);

        assertFalse(transport.send(TO, "Fail subject", "body", "", null));

        List<File> records = transport.listRecords();
        assertEquals(1, records.size());
        String rec = read(records.get(0));
        assertEquals("FAIL", field(rec, "mode"));
        assertEquals("FAILED", field(rec, "result"));
        assertEquals("Fail subject", field(rec, "subject"));
        assertEquals(0, kills.get());
    }

    @Test
    public void unknownModeIsTreatedAsFail() throws IOException {
        writeMode("SOMETHING_ELSE");

        assertFalse(transport.send(TO, "s", "b", "", null));
        assertEquals("FAILED", field(read(transport.listRecords().get(0)), "result"));
        assertEquals(0, kills.get());
    }

    @Test
    public void acceptThenKillPublishesCompleteRecordBeforeKillerRuns() throws IOException {
        writeMode(FakeEmailTransport.MODE_ACCEPT_THEN_KILL);
        final FakeEmailTransport reader = new FakeEmailTransport(baseDir, countingKiller(new AtomicInteger()));
        final int[] recordsAtKill = {-1};
        final boolean[] completeAtKill = {false};
        final AtomicInteger killerRuns = new AtomicInteger();
        FakeEmailTransport killing = new FakeEmailTransport(baseDir, new Runnable() {
            @Override
            public void run() {
                //stands in for android.os.Process.killProcess; inspects what is on disk at that moment
                killerRuns.incrementAndGet();
                List<File> records = reader.listRecords();
                recordsAtKill[0] = records.size();
                try {
                    completeAtKill[0] = records.size() == 1 && endsWithMarker(read(records.get(0)));
                } catch (IOException e) {
                    completeAtKill[0] = false;
                }
            }
        });

        boolean result = killing.send(TO, "Kill subject", "body", "", null);

        assertEquals(1, killerRuns.get());
        assertEquals(1, recordsAtKill[0]);
        assertTrue("record must be complete when the process would be killed", completeAtKill[0]);
        //only reached because the injected killer does not end the JVM; on a device send() never returns here
        assertTrue(result);
        assertEquals("ACCEPTED_THEN_KILL", field(read(reader.listRecords().get(0)), "result"));
    }

    @Test
    public void unrecordableMessageIsReportedFailedAndKillerNotRun() throws IOException {
        writeMode(FakeEmailTransport.MODE_ACCEPT_THEN_KILL);
        //a regular file where the outbox directory should be makes recording impossible
        assertTrue(new File(baseDir, FakeEmailTransport.OUTBOX_DIR).createNewFile());

        assertFalse(transport.send(TO, "s", "b", "", null));
        assertEquals(0, kills.get());
    }

    // ---------------- attachment metadata ----------------

    @Test
    public void attachmentFileExistenceSizeAndSha256AreRecorded() throws IOException {
        File att = tmp.newFile("att.txt");
        Files.write(att.toPath(), "abc".getBytes(StandardCharsets.US_ASCII));

        assertTrue(transport.send(TO, "s", "b", att.getAbsolutePath(), att));

        String rec = read(transport.listRecords().get(0));
        assertEquals(att.getAbsolutePath(), field(rec, "attachmentPath"));
        assertEquals("true", field(rec, "attachmentExists"));
        assertEquals("3", field(rec, "attachmentBytes"));
        assertEquals(SHA256_ABC, field(rec, "attachmentSha256"));
    }

    @Test
    public void attachmentPathAloneIsResolvedWhenFileArgumentIsNull() throws IOException {
        File att = tmp.newFile("att2.txt");
        Files.write(att.toPath(), "abc".getBytes(StandardCharsets.US_ASCII));

        assertTrue(transport.send(TO, "s", "b", att.getAbsolutePath(), null));

        String rec = read(transport.listRecords().get(0));
        assertEquals("true", field(rec, "attachmentExists"));
        assertEquals(SHA256_ABC, field(rec, "attachmentSha256"));
    }

    @Test
    public void missingAttachmentIsRecordedAsAbsent() throws IOException {
        File missing = new File(tmp.getRoot(), "does_not_exist.txt");

        assertTrue(transport.send(TO, "s", "b", missing.getAbsolutePath(), missing));

        String rec = read(transport.listRecords().get(0));
        assertEquals("false", field(rec, "attachmentExists"));
        assertEquals("-1", field(rec, "attachmentBytes"));
        assertEquals("", field(rec, "attachmentSha256"));
        assertFalse(missing.exists());
    }

    // ---------------- record files ----------------

    @Test
    public void everyRecordHasAUniqueNameAndNoTemporaryFileRemains() throws IOException {
        int n = 50;
        for (int i = 0; i < n; i++) {
            assertTrue(transport.send(TO, "s" + i, "b" + i, "", null));
        }

        List<File> records = transport.listRecords();
        assertEquals(n, records.size());
        Set<String> names = new HashSet<>();
        for (File f : records) {
            assertTrue("unexpected name " + f.getName(), f.getName().matches(RECORD_NAME));
            assertTrue(names.add(f.getName()));
            assertTrue(endsWithMarker(read(f)));
        }
        assertEquals(0, countWithSuffix(".tmp"));
    }

    @Test
    public void existingRecordsAndForeignFilesAreNeverOverwrittenOrDeleted() throws IOException {
        assertTrue(transport.send(TO, "first", "first body", "", null));
        File first = transport.listRecords().get(0);
        byte[] firstBytes = Files.readAllBytes(first.toPath());

        File outbox = new File(baseDir, FakeEmailTransport.OUTBOX_DIR);
        File foreign = new File(outbox, "foreign_not_from_transport.txt");
        byte[] foreignBytes = "not a transport record".getBytes(StandardCharsets.UTF_8);
        Files.write(foreign.toPath(), foreignBytes);

        //a second instance stands in for another process or an application restart
        FakeEmailTransport restarted = new FakeEmailTransport(baseDir, countingKiller(new AtomicInteger()));
        assertTrue(restarted.send(TO, "second", "second body", "", null));
        assertTrue(transport.send(TO, "third", "third body", "", null));

        assertArrayEquals(firstBytes, Files.readAllBytes(first.toPath()));
        assertArrayEquals(foreignBytes, Files.readAllBytes(foreign.toPath()));
        assertEquals(4, transport.listRecords().size());
    }

    @Test
    public void temporaryFilesAreNotListedAndAreNotTouched() throws IOException {
        File outbox = new File(baseDir, FakeEmailTransport.OUTBOX_DIR);
        assertTrue(outbox.mkdirs());
        File partial = new File(outbox, "20260101_000000_000_000001_00000000-0000-0000-0000-000000000000.tmp");
        byte[] partialBytes = "time: cut short".getBytes(StandardCharsets.UTF_8);
        Files.write(partial.toPath(), partialBytes);

        assertTrue(transport.listRecords().isEmpty());
        assertTrue(transport.send(TO, "s", "b", "", null));

        assertEquals(1, transport.listRecords().size());
        assertTrue(partial.exists());
        assertArrayEquals(partialBytes, Files.readAllBytes(partial.toPath()));
        assertEquals(1, countWithSuffix(".tmp"));
    }

    @Test
    public void everyRecordEndsWithEndMarker() throws IOException {
        assertTrue(transport.send(TO, "s", "body without trailing newline", "", null));
        assertTrue(endsWithMarker(read(transport.listRecords().get(0))));
    }

    @Test
    public void nullArgumentsAreRecordedWithoutFailing() throws IOException {
        assertTrue(transport.send(null, null, null, null, null));

        String rec = read(transport.listRecords().get(0));
        assertEquals("null", field(rec, "to"));
        assertEquals("null", field(rec, "subject"));
        assertEquals("-1", field(rec, "bodyChars"));
        assertEquals("false", field(rec, "attachmentExists"));
    }

    @Test
    public void constructorRejectsNullArguments() {
        try {
            new FakeEmailTransport(null, countingKiller(new AtomicInteger()));
            fail("null baseDir must be rejected");
        } catch (IllegalArgumentException expected) {
            //expected
        }
        try {
            new FakeEmailTransport(baseDir, null);
            fail("null processKiller must be rejected");
        } catch (IllegalArgumentException expected) {
            //expected
        }
    }

    // ---------------- helpers ----------------

    private static Runnable countingKiller(final AtomicInteger counter) {
        return new Runnable() {
            @Override
            public void run() {
                counter.incrementAndGet();
            }
        };
    }

    private void writeMode(String text) throws IOException {
        Files.write(new File(baseDir, FakeEmailTransport.MODE_FILE).toPath(), text.getBytes(StandardCharsets.UTF_8));
    }

    private int countWithSuffix(String suffix) {
        File[] files = new File(baseDir, FakeEmailTransport.OUTBOX_DIR).listFiles();
        int count = 0;
        if (files != null) {
            for (File f : files) {
                if (f.getName().endsWith(suffix)) {
                    count++;
                }
            }
        }
        return count;
    }

    private static String read(File f) throws IOException {
        return new String(Files.readAllBytes(f.toPath()), StandardCharsets.UTF_8);
    }

    /** Value of a "key: value" header line; header lines are those before the "body:" line. */
    private static String field(String record, String key) {
        for (String line : record.split("\n", -1)) {
            if (line.equals("body:")) {
                break;
            }
            if (line.startsWith(key + ": ")) {
                return line.substring(key.length() + 2);
            }
        }
        fail("field not found: " + key);
        return null;
    }

    private static boolean endsWithMarker(String record) {
        return record.endsWith(FakeEmailTransport.END_MARKER + "\n");
    }
}
