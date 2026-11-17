package com.xdel.luke.debugtools;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import static com.xdel.luke.debugtools.FakeEmailTransport.Outcome.ACCEPTED;
import static com.xdel.luke.debugtools.FakeEmailTransport.Outcome.ATTACHMENT_MISSING;
import static com.xdel.luke.debugtools.FakeEmailTransport.Outcome.FAILED_MODE;
import static com.xdel.luke.debugtools.FakeEmailTransport.Outcome.RECORD_FAILED;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * R7 Stage 1.5: local JVM tests for FakeEmailTransport.deliver(...) and claimKillMode(), called directly.
 * FakeSmtpTransportTest reaches deliver(...) only through JavaMail Transport.send with failOnMissingAttachment=true;
 * these tests cover every Outcome, non-strict delivery, record publication failures, the per-attachment record
 * fields and the ACCEPT_THEN_KILL claim file. Everything runs inside a JUnit TemporaryFolder with an injected
 * counting process killer: no network, SMTP, Mail, Android APIs or real process kill.
 */
public class FakeEmailTransportDeliverTest {

    private static final String[] TO = {"r7-a@example.invalid", "r7-b@example.invalid"};
    /** yyyyMMdd_HHmmss_SSS_<6-digit seq>_<uuid>.txt */
    private static final String RECORD_NAME =
            "\\d{8}_\\d{6}_\\d{3}_\\d{6}_[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\\.txt";
    /** fake_transport_mode.used.<uuid> */
    private static final String USED_MODE_NAME = Pattern.quote(FakeEmailTransport.USED_MODE_FILE)
            + "\\.[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";
    /** SHA-256 of the three ASCII bytes "abc". */
    private static final String SHA256_ABC = "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad";
    /** SHA-256 of zero bytes. */
    private static final String SHA256_EMPTY = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";

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

    // ---------------- outcomes ----------------

    @Test
    public void acceptModeReturnsAcceptedAndPublishesOneCompleteRecord() throws IOException {
        assertEquals(ACCEPTED, transport.deliver(TO, "Deliver subject", "Line one\nLine two", null, null, true));

        List<File> records = transport.listRecords();
        assertEquals(1, records.size());
        assertTrue("unexpected name " + records.get(0).getName(), records.get(0).getName().matches(RECORD_NAME));
        String rec = read(records.get(0));
        assertEquals("ACCEPT", field(rec, "mode"));
        assertEquals("ACCEPTED", field(rec, "result"));
        assertEquals("[r7-a@example.invalid, r7-b@example.invalid]", field(rec, "to"));
        assertEquals("Deliver subject", field(rec, "subject"));
        assertEquals("0", field(rec, "attachmentCount"));
        assertEquals(String.valueOf("Line one\nLine two".length()), field(rec, "bodyChars"));
        assertTrue(rec.contains("body:\nLine one\nLine two\n"));
        assertTrue(endsWithMarker(rec));
        //the record is the only entry in the outbox: no temporary file remains
        assertEquals(1, outboxEntries().length);
        assertEquals(0, kills.get());
    }

    @Test
    public void failAndUnknownModesReturnFailedModeAfterRecordingAndKeepTheModeFile() throws IOException {
        writeMode("fail");
        List<File> before = transport.listRecords();
        assertEquals(FAILED_MODE, transport.deliver(TO, "s1", "b", null, null, true));
        String rec = read(onlyNewRecord(before));
        assertEquals("FAIL", field(rec, "mode"));
        assertEquals("FAILED", field(rec, "result"));
        assertTrue(endsWithMarker(rec));

        writeMode("BOGUS");
        before = transport.listRecords();
        assertEquals(FAILED_MODE, transport.deliver(TO, "s2", "b", null, null, false));
        rec = read(onlyNewRecord(before));
        assertEquals("FAIL", field(rec, "mode"));
        assertEquals("FAILED", field(rec, "result"));

        //FAIL never claims the mode file
        assertEquals("BOGUS", read(modeFile()));
        assertEquals(0, usedModeFiles().size());
        assertEquals(0, kills.get());
    }

    @Test
    public void strictMissingAttachmentReturnsAttachmentMissingAndRecordsEveryAttachment() throws IOException {
        File present = newFileWith("present.txt", ascii("abc"));
        File missing = new File(tmp.getRoot(), "missing.pdf");

        assertEquals(ATTACHMENT_MISSING, transport.deliver(TO, "s", "b",
                Arrays.asList(present, missing), Arrays.asList("present.txt", "missing.pdf"), true));

        String rec = read(onlyRecord());
        assertEquals("ACCEPT", field(rec, "mode"));
        assertEquals("FAILED_ATTACHMENT_MISSING", field(rec, "result"));
        assertEquals("2", field(rec, "attachmentCount"));
        assertAttachment(rec, 0, "present.txt", present, "3", SHA256_ABC);
        assertEquals("missing.pdf", field(rec, "attachment.1.name"));
        assertEquals(missing.getPath(), field(rec, "attachment.1.path"));
        assertEquals("false", field(rec, "attachment.1.exists"));
        assertEquals("-1", field(rec, "attachment.1.bytes"));
        assertEquals("", field(rec, "attachment.1.sha256"));
        assertTrue(endsWithMarker(rec));
        assertFalse(missing.exists());
        assertEquals(0, kills.get());
    }

    @Test
    public void strictMissingAttachmentTakesPrecedenceOverFailMode() throws IOException {
        writeMode(FakeEmailTransport.MODE_FAIL);
        File missing = new File(tmp.getRoot(), "missing.pdf");

        assertEquals(ATTACHMENT_MISSING, transport.deliver(TO, "s", "b", Collections.singletonList(missing), null, true));

        String rec = read(onlyRecord());
        assertEquals("FAIL", field(rec, "mode"));
        assertEquals("FAILED_ATTACHMENT_MISSING", field(rec, "result"));
        assertEquals(0, kills.get());
    }

    @Test
    public void strictMissingAttachmentInAcceptThenKillModeNeitherClaimsNorKills() throws IOException {
        writeMode(FakeEmailTransport.MODE_ACCEPT_THEN_KILL);
        File missing = new File(tmp.getRoot(), "missing.pdf");

        assertEquals(ATTACHMENT_MISSING, transport.deliver(TO, "s", "b", Collections.singletonList(missing), null, true));

        String rec = read(onlyRecord());
        assertEquals("ACCEPT_THEN_KILL", field(rec, "mode"));
        assertEquals("FAILED_ATTACHMENT_MISSING", field(rec, "result"));
        assertEquals(0, kills.get());
        //the one-shot mode stays armed for a later complete delivery
        assertEquals(FakeEmailTransport.MODE_ACCEPT_THEN_KILL, read(modeFile()));
        assertEquals(0, usedModeFiles().size());
    }

    @Test
    public void nonStrictMissingAttachmentIsRecordedAsAbsentAndAccepted() throws IOException {
        File missing = new File(tmp.getRoot(), "missing.pdf");

        assertEquals(ACCEPTED, transport.deliver(TO, "s", "b",
                Collections.singletonList(missing), Collections.singletonList("missing.pdf"), false));

        String rec = read(onlyRecord());
        assertEquals("ACCEPTED", field(rec, "result"));
        assertEquals(missing.getPath(), field(rec, "attachmentPath"));
        assertEquals("false", field(rec, "attachmentExists"));
        assertEquals("-1", field(rec, "attachmentBytes"));
        assertEquals("", field(rec, "attachmentSha256"));
        assertEquals("1", field(rec, "attachmentCount"));
        assertEquals("missing.pdf", field(rec, "attachment.0.name"));
        assertEquals("false", field(rec, "attachment.0.exists"));
        assertFalse(missing.exists());
        assertEquals(0, kills.get());
    }

    @Test
    public void directoriesAndNullEntriesCountAsMissingAttachments() throws IOException {
        File dir = tmp.newFolder("a_directory");

        List<File> before = transport.listRecords();
        assertEquals(ATTACHMENT_MISSING, transport.deliver(TO, "s", "b", Collections.singletonList(dir), null, true));
        String rec = read(onlyNewRecord(before));
        assertEquals(dir.getPath(), field(rec, "attachment.0.path"));
        assertEquals("false", field(rec, "attachment.0.exists"));

        before = transport.listRecords();
        assertEquals(ATTACHMENT_MISSING,
                transport.deliver(TO, "s", "b", Collections.singletonList((File) null), null, true));
        rec = read(onlyNewRecord(before));
        assertEquals("", field(rec, "attachmentPath"));
        assertEquals("null", field(rec, "attachment.0.path"));
        assertEquals("false", field(rec, "attachment.0.exists"));

        before = transport.listRecords();
        assertEquals(ACCEPTED, transport.deliver(TO, "s", "b", Arrays.asList((File) null, dir), null, false));
        rec = read(onlyNewRecord(before));
        assertEquals("ACCEPTED", field(rec, "result"));
        assertEquals("2", field(rec, "attachmentCount"));
        assertEquals("null", field(rec, "attachment.0.path"));
        assertEquals(dir.getPath(), field(rec, "attachment.1.path"));
        assertEquals("false", field(rec, "attachment.1.exists"));
        assertTrue(dir.isDirectory());
        assertEquals(0, kills.get());
    }

    // ---------------- record publication failures ----------------

    @Test
    public void unwritableOutboxReturnsRecordFailed() throws IOException {
        blockOutbox();

        assertEquals(RECORD_FAILED, transport.deliver(TO, "s", "b", null, null, true));

        //the blocking file is left as it was
        assertTrue(outbox().isFile());
        assertEquals(0, outbox().length());
        assertTrue(transport.listRecords().isEmpty());
        assertEquals(0, kills.get());
    }

    @Test
    public void recordFailedTakesPrecedenceOverFailMode() throws IOException {
        writeMode(FakeEmailTransport.MODE_FAIL);
        blockOutbox();

        assertEquals(RECORD_FAILED, transport.deliver(TO, "s", "b", null, null, true));
        assertEquals(0, kills.get());
    }

    @Test
    public void recordFailedInAcceptThenKillModeNeitherClaimsNorKills() throws IOException {
        writeMode(FakeEmailTransport.MODE_ACCEPT_THEN_KILL);
        blockOutbox();

        assertEquals(RECORD_FAILED, transport.deliver(TO, "s", "b", null, null, true));

        assertEquals(0, kills.get());
        assertEquals(FakeEmailTransport.MODE_ACCEPT_THEN_KILL, read(modeFile()));
        assertEquals(0, usedModeFiles().size());
    }

    @Test
    public void strictMissingAttachmentIsReportedEvenWhenItsRecordCannotBePublished() throws IOException {
        //deliver(...) returns ATTACHMENT_MISSING whether or not the failure record was published
        blockOutbox();
        File missing = new File(tmp.getRoot(), "missing.pdf");

        assertEquals(ATTACHMENT_MISSING, transport.deliver(TO, "s", "b", Collections.singletonList(missing), null, true));

        assertTrue(transport.listRecords().isEmpty());
        assertEquals(0, kills.get());
    }

    @Test
    public void baseDirectoryThatIsARegularFileReturnsRecordFailed() throws IOException {
        File notADirectory = tmp.newFile("not_a_directory");
        FakeEmailTransport t = new FakeEmailTransport(notADirectory, countingKiller(kills));

        assertEquals(FakeEmailTransport.MODE_ACCEPT, t.readMode());
        assertEquals(RECORD_FAILED, t.deliver(TO, "s", "b", null, null, true));

        assertTrue(notADirectory.isFile());
        assertEquals(0, notADirectory.length());
        assertTrue(t.listRecords().isEmpty());
        assertEquals(0, kills.get());
    }

    @Test
    public void missingBaseAndOutboxDirectoriesAreCreatedOnFirstDelivery() throws IOException {
        File fresh = new File(new File(tmp.getRoot(), "fresh"), FakeEmailTransport.DEBUG_DIR);
        assertFalse(fresh.exists());
        FakeEmailTransport t = new FakeEmailTransport(fresh, countingKiller(kills));

        assertEquals(ACCEPTED, t.deliver(TO, "s", "b", null, null, true));

        assertTrue(new File(fresh, FakeEmailTransport.OUTBOX_DIR).isDirectory());
        List<File> records = t.listRecords();
        assertEquals(1, records.size());
        assertTrue(endsWithMarker(read(records.get(0))));
        assertEquals(0, kills.get());
    }

    // ---------------- attachment metadata ----------------

    @Test
    public void multipleAttachmentsAreRecordedInOrderWithNameSizeAndSha256() throws IOException {
        //larger than the 8192-byte read buffer, so the hash covers more than one read
        byte[] large = new byte[10000];
        for (int i = 0; i < large.length; i++) {
            large[i] = (byte) (i * 31 + 7);
        }
        File a = newFileWith("a.txt", ascii("abc"));
        File b = newFileWith("b.bin", new byte[0]);
        File c = newFileWith("c.bin", large);

        assertEquals(ACCEPTED, transport.deliver(TO, "s", "b",
                Arrays.asList(a, b, c), Arrays.asList("A.txt", "B.bin", "C.bin"), true));

        String rec = read(onlyRecord());
        //the Stage 1 header fields describe the first attachment
        assertEquals(a.getPath(), field(rec, "attachmentPath"));
        assertEquals("true", field(rec, "attachmentExists"));
        assertEquals("3", field(rec, "attachmentBytes"));
        assertEquals(SHA256_ABC, field(rec, "attachmentSha256"));
        assertEquals("3", field(rec, "attachmentCount"));
        assertAttachment(rec, 0, "A.txt", a, "3", SHA256_ABC);
        assertAttachment(rec, 1, "B.bin", b, "0", SHA256_EMPTY);
        assertAttachment(rec, 2, "C.bin", c, "10000", sha256Hex(large));
        assertFalse(hasField(rec, "attachment.3.name"));
        assertTrue(rec.indexOf("attachment.0.name: ") < rec.indexOf("attachment.1.name: "));
        assertTrue(rec.indexOf("attachment.1.name: ") < rec.indexOf("attachment.2.name: "));
        //attachments are only read
        assertArrayEquals(ascii("abc"), Files.readAllBytes(a.toPath()));
        assertArrayEquals(new byte[0], Files.readAllBytes(b.toPath()));
        assertArrayEquals(large, Files.readAllBytes(c.toPath()));
    }

    @Test
    public void nullAndEmptyAttachmentListsRecordNoAttachments() throws IOException {
        List<File> before = transport.listRecords();
        assertEquals(ACCEPTED, transport.deliver(TO, "s", "b", null, null, true));
        assertNoAttachments(read(onlyNewRecord(before)));

        before = transport.listRecords();
        assertEquals(ACCEPTED, transport.deliver(TO, "s", "b",
                Collections.<File>emptyList(), Collections.<String>emptyList(), true));
        assertNoAttachments(read(onlyNewRecord(before)));

        //a name without an attachment is not recorded
        before = transport.listRecords();
        assertEquals(ACCEPTED, transport.deliver(TO, "s", "b", null, Collections.singletonList("orphan.pdf"), true));
        String rec = read(onlyNewRecord(before));
        assertNoAttachments(rec);
        assertFalse(rec.contains("orphan.pdf"));
    }

    @Test
    public void absentOrShortFileNameListsRecordNullNames() throws IOException {
        File a = newFileWith("a.txt", ascii("abc"));
        File b = newFileWith("b.txt", ascii("abc"));
        List<File> files = Arrays.asList(a, b);

        List<File> before = transport.listRecords();
        assertEquals(ACCEPTED, transport.deliver(TO, "s", "b", files, null, true));
        String rec = read(onlyNewRecord(before));
        assertEquals("null", field(rec, "attachment.0.name"));
        assertEquals("null", field(rec, "attachment.1.name"));

        before = transport.listRecords();
        assertEquals(ACCEPTED, transport.deliver(TO, "s", "b", files, Collections.singletonList("only.pdf"), true));
        rec = read(onlyNewRecord(before));
        assertEquals("only.pdf", field(rec, "attachment.0.name"));
        assertEquals("null", field(rec, "attachment.1.name"));

        before = transport.listRecords();
        assertEquals(ACCEPTED, transport.deliver(TO, "s", "b", files, Arrays.asList("x.pdf", null, "extra.pdf"), true));
        rec = read(onlyNewRecord(before));
        assertEquals("x.pdf", field(rec, "attachment.0.name"));
        assertEquals("null", field(rec, "attachment.1.name"));
        assertFalse(hasField(rec, "attachment.2.name"));
        assertFalse(rec.contains("extra.pdf"));
    }

    @Test
    public void pathLikeFileNamesAreRecordedOnlyAsText() throws IOException {
        File att = newFileWith("att.txt", ascii("abc"));
        File decoy = newFileWith("decoy.txt", ascii("zzz"));
        List<String> fileNames = Arrays.asList(decoy.getAbsolutePath(), "../../escape.txt", "C:\\Windows\\win.ini", "");

        assertEquals(ACCEPTED, transport.deliver(TO, "s", "b", Arrays.asList(att, att, att, att), fileNames, true));

        File record = onlyRecord();
        String rec = read(record);
        for (int i = 0; i < fileNames.size(); i++) {
            //the name is copied verbatim; size and hash always come from the attachment File itself
            assertAttachment(rec, i, fileNames.get(i), att, "3", SHA256_ABC);
        }
        //no name influences the record file name
        assertTrue(record.getName().matches(RECORD_NAME));
        assertArrayEquals(ascii("abc"), Files.readAllBytes(att.toPath()));
        assertArrayEquals(ascii("zzz"), Files.readAllBytes(decoy.toPath()));
        //nothing was created by name: the only files under the temporary root are the two inputs and the record
        List<String> found = regularFileNamesUnder(tmp.getRoot());
        Collections.sort(found);
        List<String> expected = new ArrayList<>(Arrays.asList("att.txt", "decoy.txt", record.getName()));
        Collections.sort(expected);
        assertEquals(expected, found);
    }

    // ---------------- ACCEPT_THEN_KILL ----------------

    @Test
    public void acceptThenKillPublishesTheRecordAndClaimsTheModeBeforeTheKillerRuns() throws IOException {
        writeMode(FakeEmailTransport.MODE_ACCEPT_THEN_KILL);
        final FakeEmailTransport reader = new FakeEmailTransport(baseDir, countingKiller(new AtomicInteger()));
        final AtomicInteger killerRuns = new AtomicInteger();
        final List<String> atKill = new ArrayList<>();
        FakeEmailTransport killing = new FakeEmailTransport(baseDir, new Runnable() {
            @Override
            public void run() {
                //stands in for android.os.Process.killProcess; notes what is on disk at that moment
                killerRuns.incrementAndGet();
                List<File> records = reader.listRecords();
                atKill.add("records=" + records.size());
                try {
                    String rec = records.size() == 1 ? read(records.get(0)) : "";
                    atKill.add("complete=" + endsWithMarker(rec));
                    atKill.add("result=" + (records.size() == 1 ? field(rec, "result") : "?"));
                } catch (IOException e) {
                    atKill.add("unreadable");
                }
                atKill.add("modeFile=" + modeFile().exists());
                atKill.add("claims=" + usedModeFiles().size());
            }
        });
        File att = newFileWith("att.txt", ascii("abc"));

        FakeEmailTransport.Outcome outcome = killing.deliver(TO, "Kill subject", "body",
                Collections.singletonList(att), Collections.singletonList("att.txt"), true);

        assertEquals(1, killerRuns.get());
        assertEquals(Arrays.asList("records=1", "complete=true", "result=ACCEPTED_THEN_KILL", "modeFile=false", "claims=1"),
                atKill);
        //only reached because the injected killer does not end the JVM; on a device deliver() never returns here
        assertEquals(ACCEPTED, outcome);
        assertEquals(FakeEmailTransport.MODE_ACCEPT_THEN_KILL, read(usedModeFiles().get(0)));
    }

    @Test
    public void acceptThenKillIsOneShotAcrossCallsAndTransportInstances() throws IOException {
        writeMode(FakeEmailTransport.MODE_ACCEPT_THEN_KILL);

        List<File> before = transport.listRecords();
        assertEquals(ACCEPTED, transport.deliver(TO, "first", "b", null, null, true));
        assertEquals("ACCEPTED_THEN_KILL", field(read(onlyNewRecord(before)), "result"));
        assertEquals(1, kills.get());

        before = transport.listRecords();
        assertEquals(ACCEPTED, transport.deliver(TO, "second", "b", null, null, true));
        String rec = read(onlyNewRecord(before));
        assertEquals("ACCEPT", field(rec, "mode"));
        assertEquals("ACCEPTED", field(rec, "result"));

        //a second instance stands in for the restarted process
        AtomicInteger restartedKills = new AtomicInteger();
        FakeEmailTransport restarted = new FakeEmailTransport(baseDir, countingKiller(restartedKills));
        before = transport.listRecords();
        assertEquals(ACCEPTED, restarted.deliver(TO, "third", "b", null, null, true));
        assertEquals("ACCEPTED", field(read(onlyNewRecord(before)), "result"));

        assertEquals(1, kills.get());
        assertEquals(0, restartedKills.get());
        assertFalse(modeFile().exists());
        assertEquals(1, usedModeFiles().size());
    }

    // ---------------- claimKillMode ----------------

    @Test
    public void claimKillModeWithoutAModeFileReturnsFalseAndCreatesNothing() {
        assertFalse(transport.claimKillMode());

        assertArrayEquals(new String[0], baseDir.list());
        assertEquals(0, kills.get());
    }

    @Test
    public void claimKillModeClaimsAcceptThenKillTextExactlyOnce() throws IOException {
        byte[] text = " accept_then_kill \n".getBytes(StandardCharsets.UTF_8);
        Files.write(modeFile().toPath(), text);

        assertTrue(transport.claimKillMode());

        assertFalse(modeFile().exists());
        List<File> claims = usedModeFiles();
        assertEquals(1, claims.size());
        assertTrue("unexpected name " + claims.get(0).getName(), claims.get(0).getName().matches(USED_MODE_NAME));
        assertArrayEquals(text, Files.readAllBytes(claims.get(0).toPath()));

        assertFalse(transport.claimKillMode());
        assertEquals(1, usedModeFiles().size());
        //claimKillMode only claims; running the killer is left to deliver(...) and send(...)
        assertEquals(0, kills.get());
    }

    @Test
    public void claimKillModePutsOtherModeTextBack() throws IOException {
        for (String text : new String[] {"FAIL", "ACCEPT", "", "BOGUS"}) {
            byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
            Files.write(modeFile().toPath(), bytes);

            assertFalse("mode text '" + text + "'", transport.claimKillMode());

            assertArrayEquals(bytes, Files.readAllBytes(modeFile().toPath()));
            assertEquals(0, usedModeFiles().size());
        }
        assertEquals(0, kills.get());
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

    private File modeFile() {
        return new File(baseDir, FakeEmailTransport.MODE_FILE);
    }

    private File outbox() {
        return new File(baseDir, FakeEmailTransport.OUTBOX_DIR);
    }

    private void writeMode(String text) throws IOException {
        Files.write(modeFile().toPath(), text.getBytes(StandardCharsets.UTF_8));
    }

    /** A regular file where the outbox directory should be makes publishing impossible. */
    private void blockOutbox() throws IOException {
        assertTrue(outbox().createNewFile());
    }

    private File newFileWith(String name, byte[] content) throws IOException {
        File f = tmp.newFile(name);
        Files.write(f.toPath(), content);
        return f;
    }

    private File onlyRecord() {
        List<File> records = transport.listRecords();
        assertEquals(1, records.size());
        return records.get(0);
    }

    /** The single record published since before was listed; independent of record name ordering. */
    private File onlyNewRecord(List<File> before) {
        List<File> added = new ArrayList<>(transport.listRecords());
        added.removeAll(before);
        assertEquals("exactly one new record expected", 1, added.size());
        return added.get(0);
    }

    private File[] outboxEntries() {
        File[] files = outbox().listFiles();
        return files == null ? new File[0] : files;
    }

    private List<File> usedModeFiles() {
        List<File> used = new ArrayList<>();
        File[] files = baseDir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.getName().startsWith(FakeEmailTransport.USED_MODE_FILE + ".")) {
                    used.add(f);
                }
            }
        }
        return used;
    }

    private static List<String> regularFileNamesUnder(File dir) {
        List<String> names = new ArrayList<>();
        File[] children = dir.listFiles();
        if (children != null) {
            for (File c : children) {
                if (c.isDirectory()) {
                    names.addAll(regularFileNamesUnder(c));
                } else {
                    names.add(c.getName());
                }
            }
        }
        return names;
    }

    private static void assertAttachment(String rec, int i, String name, File f, String bytes, String sha256) {
        String prefix = "attachment." + i + ".";
        assertEquals(name, field(rec, prefix + "name"));
        assertEquals(f.getPath(), field(rec, prefix + "path"));
        assertEquals("true", field(rec, prefix + "exists"));
        assertEquals(bytes, field(rec, prefix + "bytes"));
        assertEquals(sha256, field(rec, prefix + "sha256"));
    }

    private static void assertNoAttachments(String rec) {
        assertEquals("ACCEPTED", field(rec, "result"));
        assertEquals("", field(rec, "attachmentPath"));
        assertEquals("false", field(rec, "attachmentExists"));
        assertEquals("-1", field(rec, "attachmentBytes"));
        assertEquals("", field(rec, "attachmentSha256"));
        assertEquals("0", field(rec, "attachmentCount"));
        assertFalse(hasField(rec, "attachment.0.name"));
    }

    private static byte[] ascii(String s) {
        return s.getBytes(StandardCharsets.US_ASCII);
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

    private static boolean hasField(String record, String key) {
        for (String line : record.split("\n", -1)) {
            if (line.equals("body:")) {
                return false;
            }
            if (line.startsWith(key + ": ")) {
                return true;
            }
        }
        return false;
    }

    private static boolean endsWithMarker(String record) {
        return record.endsWith(FakeEmailTransport.END_MARKER + "\n");
    }

    private static String sha256Hex(byte[] data) {
        try {
            StringBuilder hex = new StringBuilder();
            for (byte b : MessageDigest.getInstance("SHA-256").digest(data)) {
                hex.append(String.format(Locale.US, "%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        }
    }
}
