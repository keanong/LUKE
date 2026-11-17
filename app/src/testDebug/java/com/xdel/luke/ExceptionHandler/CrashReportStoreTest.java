package com.xdel.luke.ExceptionHandler;

import com.xdel.luke.ExceptionHandler.CrashReportStore.CorruptFileException;
import com.xdel.luke.ExceptionHandler.CrashReportStore.Report;
import com.xdel.luke.ExceptionHandler.CrashReportStore.SeqAllocation;
import com.xdel.luke.ExceptionHandler.CrashReportStore.SeqRecovery;
import com.xdel.luke.ExceptionHandler.CrashReportStore.Summary;
import com.xdel.luke.ExceptionHandler.CrashReportStore.SummaryEntry;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * R7 A7 Stage 1: local JVM tests for CrashReportStore.
 * Every test uses its own JUnit TemporaryFolder as the store's base directory, so nothing touches
 * Android APIs, the device, the network, SMTP or real crash data. The store lock is static, so
 * each test that takes it releases it in the same test.
 */
public class CrashReportStoreTest {

    private static final long LOCK_MS = 2000;
    private static final long DAY_MS = TimeUnit.DAYS.toMillis(1);

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    private File dir;
    private CrashReportStore store;

    @Before
    public void setUp() {
        dir = CrashReportStore.storeDir(tmp.getRoot());
        store = new CrashReportStore(dir);
    }

    // ---------------- helpers ----------------

    private interface Body {
        void run() throws Exception;
    }

    private static <T extends Throwable> T expect(Class<T> type, Body body) {
        try {
            body.run();
        } catch (Throwable t) {
            if (type.isInstance(t)) {
                return type.cast(t);
            }
            throw new AssertionError("expected " + type.getSimpleName() + " but got " + t, t);
        }
        fail("expected " + type.getSimpleName());
        return null;
    }

    private File file(String name) {
        return new File(dir, name);
    }

    private static void write(File f, String content) throws IOException {
        File parent = f.getParentFile();
        assertTrue(parent.isDirectory() || parent.mkdirs());
        Files.write(f.toPath(), content.getBytes(StandardCharsets.UTF_8));
    }

    private static String text(File f) throws IOException {
        return new String(Files.readAllBytes(f.toPath()), StandardCharsets.UTF_8);
    }

    private static Report report(String id, Long seq) {
        return report(id, seq, 1000L);
    }

    private static Report report(String id, Long seq, long time) {
        return Report.newReport(id, seq, "", 0, time, "5.0.8", "log.txt",
                "java.lang.RuntimeException: test");
    }

    private static <T> T onOtherThread(Callable<T> task) throws Exception {
        ExecutorService ex = Executors.newSingleThreadExecutor();
        try {
            return ex.submit(task).get(10, TimeUnit.SECONDS);
        } finally {
            ex.shutdownNow();
        }
    }

    /**
     * Tries the store lock on another thread and, if it was acquired, releases it on that same
     * thread, so a lock owned by a finished executor thread can never be left behind.
     */
    private boolean lockOnOtherThread(long timeoutMs) throws Exception {
        return onOtherThread(() -> {
            boolean ok = store.lock(timeoutMs);
            if (ok) {
                store.unlock();
            }
            return ok;
        });
    }

    // ---------------- store setup ----------------

    @Test
    public void storeDirIsR7CrashInsideTheBaseDirectory() {
        File base = tmp.getRoot();
        assertEquals(new File(base, "r7_crash"), CrashReportStore.storeDir(base));
        assertEquals(dir, store.dir());
    }

    @Test
    public void constructorRejectsNullDirectory() {
        expect(IllegalArgumentException.class, () -> new CrashReportStore(null));
    }

    @Test
    public void storeDirectoryIsCreatedOnFirstWrite() throws IOException {
        assertFalse(dir.exists());
        store.savePending(report("a", 1L));
        assertTrue(dir.isDirectory());
    }

    // ---------------- versioned key=value format ----------------

    @Test
    public void encodeDecodeRoundTripKeepsOrderAndEscapes() throws IOException {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("b", "back\\slash\nnew line\rreturn");
        m.put("a", "");
        m.put("c", "pipes|stay|as|they|are");
        Map<String, String> back = CrashReportStore.decode(CrashReportStore.encode(m));
        assertEquals(m, back);
        assertEquals(new ArrayList<>(m.keySet()), new ArrayList<>(back.keySet()));
    }

    @Test
    public void decodeRejectsTamperedContent() {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("a", "1");
        String good = new String(CrashReportStore.encode(m), StandardCharsets.UTF_8);
        byte[] bad = good.replace("a=1", "a=2").getBytes(StandardCharsets.UTF_8);
        expect(CorruptFileException.class, () -> CrashReportStore.decode(bad));
    }

    @Test
    public void decodeRejectsWrongVersionDuplicateKeyBadEscapeAndEmptyInput() {
        String[] bodies = {"v=2\na=1\n", "v=1\na=1\na=2\n", "v=1\na=\\x\n", "v=1\n=1\n"};
        for (String body : bodies) {
            byte[] data = (body + "crc32=" + CrashReportStore.crcHex(body) + "\n")
                    .getBytes(StandardCharsets.UTF_8);
            expect(CorruptFileException.class, () -> CrashReportStore.decode(data));
        }
        expect(CorruptFileException.class, () -> CrashReportStore.decode(new byte[0]));
        expect(CorruptFileException.class,
                () -> CrashReportStore.decode("v=1\na=1\n".getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    public void encodeRejectsReservedAndInvalidKeys() {
        for (String key : new String[] {"v", "crc32", "1abc", "has space"}) {
            Map<String, String> m = new LinkedHashMap<>();
            m.put(key, "x");
            expect(IllegalArgumentException.class, () -> CrashReportStore.encode(m));
        }
    }

    // ---------------- atomic file I/O ----------------

    @Test
    public void writeAtomicReplacesTheTargetAndLeavesNoTempFile() throws IOException {
        File target = new File(tmp.getRoot(), "target");
        CrashReportStore.writeAtomic(target, "one".getBytes(StandardCharsets.UTF_8));
        CrashReportStore.writeAtomic(target, "two".getBytes(StandardCharsets.UTF_8));
        assertEquals("two", text(target));
        assertFalse(new File(tmp.getRoot(), "target" + CrashReportStore.TMP_EXT).exists());
    }

    @Test
    public void writeAtomicNewNeverReplacesAnExistingFile() throws IOException {
        File target = new File(tmp.getRoot(), "target");
        write(target, "original");
        expect(FileAlreadyExistsException.class, () -> CrashReportStore.writeAtomicNew(
                target, "new".getBytes(StandardCharsets.UTF_8)));
        assertEquals("original", text(target));
    }

    @Test
    public void readBoundedRejectsFilesLargerThanTheLimit() throws IOException {
        File big = new File(tmp.getRoot(), "big");
        Files.write(big.toPath(), new byte[(int) CrashReportStore.MAX_FILE_BYTES + 1]);
        expect(CorruptFileException.class, () -> CrashReportStore.readBounded(big));
    }

    // ---------------- reports ----------------

    @Test
    public void newReportStartsCycleOneAndTruncatesTheException() {
        StringBuilder exc = new StringBuilder();
        for (int i = 0; i < CrashReportStore.EXC_MAX_CHARS + 500; i++) {
            exc.append('x');
        }
        Report r = Report.newReport("id1", 3L, null, -4, 5000L, null, null, exc.toString());
        assertEquals(1, r.cycle);
        assertEquals(5000L, r.cycleStart);
        assertEquals(5000L + CrashReportStore.WINDOW_MS, r.expiresAt);
        assertEquals(0, r.suppressedBefore);
        assertEquals("", r.seqNote);
        assertEquals(CrashReportStore.EXC_MAX_CHARS, r.exc.length());
    }

    @Test
    public void newReportRejectsInvalidIds() {
        for (String id : new String[] {null, "", "has space", "under_score"}) {
            expect(IllegalArgumentException.class,
                    () -> Report.newReport(id, 1L, "", 0, 0L, "", "", ""));
        }
    }

    @Test
    public void savedPendingReportReadsBackWithEveryField() throws IOException {
        Report r = Report.newReport("abc-123", 42L, "[R7N:x] note\nsecond line", 2, 7000L,
                "5.0.8", "/logs/x.txt", "boom|with pipe");
        r.attempts = 3;
        r.lastAttemptAt = 8000L;
        r.heldAt = 9000L;
        r.heldReason = "reason";
        File f = store.savePending(r);
        assertEquals(store.file(CrashReportStore.PENDING, "abc-123"), f);
        Report back = store.read(f);
        assertEquals(r.id, back.id);
        assertEquals(r.seq, back.seq);
        assertEquals(r.seqNote, back.seqNote);
        assertEquals(r.suppressedBefore, back.suppressedBefore);
        assertEquals(r.time, back.time);
        assertEquals(r.cycleStart, back.cycleStart);
        assertEquals(r.expiresAt, back.expiresAt);
        assertEquals(r.cycle, back.cycle);
        assertEquals(r.attempts, back.attempts);
        assertEquals(r.lastAttemptAt, back.lastAttemptAt);
        assertEquals(r.version, back.version);
        assertEquals(r.logPath, back.logPath);
        assertEquals(r.exc, back.exc);
        assertEquals(r.heldAt, back.heldAt);
        assertEquals(r.heldReason, back.heldReason);
    }

    @Test
    public void unknownSequenceIsStoredAsUnknownAndReadBackAsNull() throws IOException {
        File f = store.savePending(report("u", null));
        assertTrue(text(f).contains("\nseq=unknown\n"));
        assertNull(store.read(f).seq);
    }

    @Test
    public void savePendingNeverReplacesAnExistingReport() throws IOException {
        store.savePending(report("a", 1L));
        expect(FileAlreadyExistsException.class, () -> store.savePending(report("a", 2L)));
        assertEquals(Long.valueOf(1), store.read(store.file(CrashReportStore.PENDING, "a")).seq);
    }

    @Test
    public void rewriteUpdatesAnExistingReportAndRejectsAMissingOne() throws IOException {
        Report r = report("a", 1L);
        store.savePending(r);
        r.attempts = 4;
        store.rewrite(CrashReportStore.PENDING, r);
        assertEquals(4, store.read(store.file(CrashReportStore.PENDING, "a")).attempts);
        expect(NoSuchFileException.class, () -> store.rewrite(CrashReportStore.HELD, r));
    }

    @Test
    public void readRejectsAReportWhoseIdDoesNotMatchItsFileName() throws IOException {
        File a = store.savePending(report("a", 1L));
        File b = store.file(CrashReportStore.PENDING, "b");
        Files.copy(a.toPath(), b.toPath());
        expect(CorruptFileException.class, () -> store.read(b));
    }

    @Test
    public void readRejectsATamperedReport() throws IOException {
        File f = store.savePending(report("a", 1L));
        write(f, text(f).replace("\nattempts=0\n", "\nattempts=9\n"));
        expect(CorruptFileException.class, () -> store.read(f));
    }

    @Test
    public void listIsSortedByNameAndIgnoresTempAndOtherStates() throws IOException {
        store.savePending(report("b", 2L));
        store.savePending(report("a", 1L));
        write(file("pending_c.rec" + CrashReportStore.TMP_EXT), "partial");
        write(file("held_d.rec"), "other state");
        List<File> pending = store.list(CrashReportStore.PENDING);
        assertEquals(2, pending.size());
        assertEquals("pending_a.rec", pending.get(0).getName());
        assertEquals("pending_b.rec", pending.get(1).getName());
    }

    @Test
    public void moveKeepsTheIdAndNeverReplacesAnExistingFile() throws IOException {
        File p = store.savePending(report("a", 1L));
        File held = store.move(p, CrashReportStore.HELD);
        assertEquals(store.file(CrashReportStore.HELD, "a"), held);
        assertFalse(p.exists());
        assertEquals(Long.valueOf(1), store.read(held).seq);
        File p2 = store.savePending(report("a", 2L));
        expect(FileAlreadyExistsException.class, () -> store.move(p2, CrashReportStore.HELD));
        assertTrue(p2.exists());
        assertEquals(Long.valueOf(1), store.read(held).seq);
    }

    @Test
    public void markCorruptNumbersNamesAndNeverOverwritesEvidence() throws IOException {
        File p = file("pending_a.rec");
        write(p, "first damaged");
        File c1 = store.markCorrupt(p);
        assertEquals("corrupt_a.rec", c1.getName());
        assertFalse(p.exists());
        write(p, "second damaged");
        File c2 = store.markCorrupt(p);
        assertEquals("corrupt_a-1.rec", c2.getName());
        assertEquals("first damaged", text(c1));
        assertEquals("second damaged", text(c2));
    }

    @Test
    public void cleanupTempsDeletesOnlyOldTempFiles() throws IOException {
        long now = System.currentTimeMillis();
        File old = file("old" + CrashReportStore.TMP_EXT);
        File young = file("young" + CrashReportStore.TMP_EXT);
        File keep = file("pending_k.rec");
        write(old, "x");
        write(young, "x");
        write(keep, "x");
        assertTrue(old.setLastModified(now - CrashReportStore.TMP_MIN_AGE_MS - 60000));
        assertTrue(young.setLastModified(now));
        assertTrue(keep.setLastModified(now - CrashReportStore.TMP_MIN_AGE_MS - 60000));
        assertEquals(1, store.cleanupTemps(now));
        assertFalse(old.exists());
        assertTrue(young.exists());
        assertTrue(keep.exists());
    }

    @Test
    public void idOfAcceptsOnlyKnownStoreNames() {
        assertEquals("abc", CrashReportStore.idOf("pending_abc.rec"));
        assertEquals("abc", CrashReportStore.idOf("held_abc.rec"));
        assertEquals("abc", CrashReportStore.idOf("retry_abc.rec"));
        assertEquals("a-1", CrashReportStore.idOf("corrupt_a-1.rec"));
        assertEquals("x", CrashReportStore.idOf("sent_x.ok"));
        assertNull(CrashReportStore.idOf("pending_.rec"));
        assertNull(CrashReportStore.idOf("pending_a b.rec"));
        assertNull(CrashReportStore.idOf("pending_a.rec.tmp"));
        assertNull(CrashReportStore.idOf("other.txt"));
    }

    // ---------------- receipts ----------------

    @Test
    public void writtenReceiptIsFoundAndListed() throws IOException {
        store.writeReceipt("r1", 7L, 1000L);
        assertTrue(store.hasReceipt("r1"));
        assertFalse(store.hasReceipt("r2"));
        assertEquals(1, store.listReceipts().size());
        Map<String, String> m = store.readKv(store.receiptFile("r1"));
        assertEquals("r1", m.get("id"));
        assertEquals("7", m.get("seq"));
        assertEquals("1000", m.get("sentAt"));
    }

    @Test
    public void pruneReceiptsUsesSentAtOrFileTimeForUnreadableReceipts() throws IOException {
        long now = System.currentTimeMillis();
        long maxAge = 30 * DAY_MS;
        store.writeReceipt("old", 1L, now - 31 * DAY_MS);
        store.writeReceipt("new", 2L, now - DAY_MS);
        File badOld = file("sent_badold.ok");
        File badNew = file("sent_badnew.ok");
        write(badOld, "damaged");
        write(badNew, "damaged");
        assertTrue(badOld.setLastModified(now - 40 * DAY_MS));
        assertTrue(badNew.setLastModified(now));
        assertEquals(2, store.pruneReceipts(now, maxAge));
        assertFalse(store.hasReceipt("old"));
        assertTrue(store.hasReceipt("new"));
        assertFalse(badOld.exists());
        assertTrue(badNew.exists());
    }

    // ---------------- sequence counter ----------------

    @Test
    public void firstAllocationIsOneWithANewCounterNote() throws IOException {
        SeqAllocation a = store.allocateSeq(LOCK_MS);
        assertEquals(Long.valueOf(1), a.seq);
        assertEquals(0, a.suppressedBefore);
        assertTrue(a.note, a.note.startsWith("[R7N:"));
        assertTrue(a.note, a.note.contains(
                "new counter (first report on this install or app data cleared)"));
        assertEquals(1, store.readCounter().seq);
    }

    @Test
    public void allocationsIncreaseByOne() {
        assertEquals(Long.valueOf(1), store.allocateSeq(LOCK_MS).seq);
        assertEquals(Long.valueOf(2), store.allocateSeq(LOCK_MS).seq);
        assertEquals(Long.valueOf(3), store.allocateSeq(LOCK_MS).seq);
    }

    @Test
    public void allocationNeverGoesBelowLastSent() {
        assertEquals(Long.valueOf(1), store.allocateSeq(LOCK_MS).seq);
        assertTrue(store.updateLastSent(20, LOCK_MS));
        assertEquals(Long.valueOf(21), store.allocateSeq(LOCK_MS).seq);
    }

    @Test
    public void missingCounterIsRebuiltFromLastSent() {
        assertTrue(store.updateLastSent(10, LOCK_MS));
        SeqAllocation a = store.allocateSeq(LOCK_MS);
        assertEquals(Long.valueOf(11), a.seq);
        assertTrue(a.note, a.note.contains("counter missing (max 10 from last_sent); possible gap"));
    }

    @Test
    public void missingCounterIsRebuiltFromTheSummary() throws IOException {
        store.appendSummary(report("s40", 40L), "held: test", 1L, LOCK_MS);
        SeqAllocation a = store.allocateSeq(LOCK_MS);
        assertEquals(Long.valueOf(41), a.seq);
        assertTrue(a.note, a.note.contains("counter missing (max 40 from summary); possible gap"));
    }

    @Test
    public void corruptCounterIsPreservedAndRecoveredWithAHint() throws IOException {
        store.savePending(report("r5", 5L));
        String damaged = "v=1\nseq=7\nsuppressed=3\ncrc32=0\n";
        write(file(CrashReportStore.COUNTER_FILE), damaged);
        SeqAllocation a = store.allocateSeq(LOCK_MS);
        assertEquals(Long.valueOf(8), a.seq);
        assertEquals(0, a.suppressedBefore);
        assertTrue(a.note, a.note.contains("counter recovered (max 7 from corrupt counter, "
                + "includes hint from corrupt record); possible gap"));
        assertTrue(a.note, a.note.contains("suppressed count lost because the counter was corrupt"));
        assertTrue(a.note, a.note.contains("recovery found: counter corrupt"));
        assertEquals(damaged, text(file(CrashReportStore.COUNTER_CORRUPT_FILE)));
        assertEquals(8, store.readCounter().seq);
    }

    @Test
    public void corruptCounterWithNoEvidenceRestartsAndSaysSo() throws IOException {
        write(file(CrashReportStore.COUNTER_FILE), "damaged");
        SeqAllocation a = store.allocateSeq(LOCK_MS);
        assertEquals(Long.valueOf(1), a.seq);
        assertTrue(a.note, a.note.contains("counter reset; no prior values found"));
        assertTrue(a.note, a.note.contains("suppressed count lost"));
    }

    @Test
    public void repeatedCounterCorruptionNeverOverwritesEarlierEvidence() throws IOException {
        write(file(CrashReportStore.COUNTER_FILE), "first damaged");
        store.allocateSeq(LOCK_MS);
        write(file(CrashReportStore.COUNTER_FILE), "second damaged");
        store.allocateSeq(LOCK_MS);
        assertEquals("first damaged", text(file("seq.corrupt")));
        assertEquals("second damaged", text(file("seq.corrupt-1")));
    }

    @Test
    public void suppressedCrashesAreReportedOnceThenReset() {
        assertEquals(Long.valueOf(1), store.allocateSeq(LOCK_MS).seq);
        assertTrue(store.incrementSuppressed(LOCK_MS));
        assertTrue(store.incrementSuppressed(LOCK_MS));
        assertTrue(store.incrementSuppressed(LOCK_MS));
        SeqAllocation a = store.allocateSeq(LOCK_MS);
        assertEquals(Long.valueOf(2), a.seq);
        assertEquals(3, a.suppressedBefore);
        assertEquals(0, store.allocateSeq(LOCK_MS).suppressedBefore);
    }

    @Test
    public void incrementSuppressedOnAFreshStoreCreatesTheCounter() throws IOException {
        assertTrue(store.incrementSuppressed(LOCK_MS));
        assertEquals(0, store.readCounter().seq);
        assertEquals(1, store.readCounter().suppressed);
        SeqAllocation a = store.allocateSeq(LOCK_MS);
        assertEquals(Long.valueOf(1), a.seq);
        assertEquals(1, a.suppressedBefore);
    }

    @Test
    public void raiseCounterToNeverLowersAndKeepsTheSuppressedCount() {
        assertEquals(Long.valueOf(1), store.allocateSeq(LOCK_MS).seq);
        assertTrue(store.incrementSuppressed(LOCK_MS));
        assertTrue(store.raiseCounterTo(50, LOCK_MS));
        assertTrue(store.raiseCounterTo(10, LOCK_MS));
        assertFalse(store.raiseCounterTo(0, LOCK_MS));
        assertFalse(store.raiseCounterTo(-5, LOCK_MS));
        SeqAllocation a = store.allocateSeq(LOCK_MS);
        assertEquals(Long.valueOf(51), a.seq);
        assertEquals(1, a.suppressedBefore);
    }

    @Test
    public void allocationStopsAtLongMaxValueWithoutOverflow() throws IOException {
        assertTrue(store.raiseCounterTo(Long.MAX_VALUE, LOCK_MS));
        SeqAllocation a = store.allocateSeq(LOCK_MS);
        assertNull(a.seq);
        assertEquals("counter exhausted at Long.MAX_VALUE", a.note);
        assertEquals(0, a.suppressedBefore);
        assertEquals(Long.MAX_VALUE, store.readCounter().seq);
    }

    @Test
    public void readCounterTreatsANonRegularFileAsCorrupt() {
        assertTrue(file(CrashReportStore.COUNTER_FILE).mkdirs());
        expect(CorruptFileException.class, () -> store.readCounter());
    }

    // ---------------- recovery scan ----------------

    @Test
    public void recoverMaxSeqOnAnEmptyStoreFindsNothing() {
        SeqRecovery r = store.recoverMaxSeq();
        assertEquals(0, r.max);
        assertEquals("none", r.source);
        assertFalse(r.usedHint);
        assertEquals("", r.issues);
    }

    @Test
    public void hintMoreThanTheLimitAboveTrustedIsIgnored() throws IOException {
        store.savePending(report("r5", 5L));
        write(file("corrupt_x.rec"), "seq=" + (5 + CrashReportStore.HINT_MAX_ABOVE_TRUSTED + 1) + "\n");
        SeqRecovery r = store.recoverMaxSeq();
        assertEquals(5, r.max);
        assertEquals("report", r.source);
        assertFalse(r.usedHint);
    }

    @Test
    public void hintWithinTheLimitAboveTrustedIsUsed() throws IOException {
        store.savePending(report("r5", 5L));
        long hint = 5 + CrashReportStore.HINT_MAX_ABOVE_TRUSTED;
        write(file("corrupt_x.rec"), "seq=" + hint + "\n");
        SeqRecovery r = store.recoverMaxSeq();
        assertEquals(hint, r.max);
        assertEquals("corrupt_x.rec", r.source);
        assertTrue(r.usedHint);
    }

    @Test
    public void invalidReceiptIsOnlyAHintAndIsListedAsAnIssue() throws IOException {
        store.writeReceipt("a", 12L, 1000L);
        store.writeReceipt("c", 90L, 0L);
        SeqRecovery r = store.recoverMaxSeq();
        assertEquals(90, r.max);
        assertTrue(r.usedHint);
        assertEquals("corrupt sent_c.ok", r.source);
        assertTrue(r.issues, r.issues.contains("corrupt sent_c.ok"));
    }

    @Test
    public void corruptLastSentIsListedAsAnIssueButMissingIsNot() throws IOException {
        assertEquals("", store.recoverMaxSeq().issues);
        write(file(CrashReportStore.LAST_SENT_FILE), "damaged");
        SeqRecovery r = store.recoverMaxSeq();
        assertTrue(r.issues, r.issues.contains("last_sent corrupt"));
    }

    @Test
    public void corruptSummaryGivesAHintAndAnIssue() throws IOException {
        write(file(CrashReportStore.SUMMARY_FILE), "v=1\ne1=33|id|0|0|0|s|x\ncrc32=0\n");
        SeqRecovery r = store.recoverMaxSeq();
        assertEquals(33, r.max);
        assertTrue(r.usedHint);
        assertEquals("corrupt summary", r.source);
        assertTrue(r.issues, r.issues.contains("summary corrupt"));
    }

    // ---------------- recovery notes ----------------

    @Test
    public void pendingNotesRepeatUntilClearedThenStop() {
        SeqAllocation a1 = store.allocateSeq(LOCK_MS);
        SeqAllocation a2 = store.allocateSeq(LOCK_MS);
        assertFalse(a1.note.isEmpty());
        assertEquals(a1.note, a2.note);
        assertTrue(store.clearRecoveryNotes(a1.note, LOCK_MS));
        assertEquals("", store.allocateSeq(LOCK_MS).note);
        assertFalse(file("seq.note").exists());
    }

    @Test
    public void clearingAnOlderNoteKeepsANewerNote() throws IOException {
        SeqAllocation a1 = store.allocateSeq(LOCK_MS);
        write(file(CrashReportStore.LAST_SENT_FILE), "damaged");
        assertTrue(store.updateLastSent(1, LOCK_MS));
        assertTrue(store.clearRecoveryNotes(a1.note, LOCK_MS));
        SeqAllocation a2 = store.allocateSeq(LOCK_MS);
        assertTrue(a2.note, a2.note.contains("last_sent corrupt"));
        assertFalse(a2.note, a2.note.contains("new counter"));
    }

    @Test
    public void clearRecoveryNotesWithEmptyOrUnrelatedTextKeepsEveryNote() {
        SeqAllocation a1 = store.allocateSeq(LOCK_MS);
        assertTrue(store.clearRecoveryNotes(null, LOCK_MS));
        assertTrue(store.clearRecoveryNotes("", LOCK_MS));
        assertTrue(store.clearRecoveryNotes("unrelated text", LOCK_MS));
        assertEquals(a1.note, store.allocateSeq(LOCK_MS).note);
    }

    @Test
    public void corruptNoteFileIsPreservedAndTheLossIsReported() throws IOException {
        store.allocateSeq(LOCK_MS);
        write(file("seq.note"), "damaged notes");
        SeqAllocation a = store.allocateSeq(LOCK_MS);
        assertTrue(a.note, a.note.contains(
                "earlier recovery notes unreadable; kept as seq.note.corrupt"));
        assertFalse(a.note, a.note.contains("new counter"));
        assertEquals("damaged notes", text(file("seq.note.corrupt")));
    }

    // ---------------- last_sent ----------------

    @Test
    public void lastSentIsZeroWhenMissingAndNeverLowered() {
        assertEquals(0, store.readLastSentOrZero());
        assertTrue(store.updateLastSent(5, LOCK_MS));
        assertTrue(store.updateLastSent(3, LOCK_MS));
        assertEquals(5, store.readLastSentOrZero());
        assertFalse(store.updateLastSent(0, LOCK_MS));
        assertFalse(store.updateLastSent(-1, LOCK_MS));
        assertEquals(5, store.readLastSentOrZero());
    }

    @Test
    public void lastSentIsRebuiltOnlyFromValidReceipts() throws IOException {
        store.writeReceipt("a", 12L, 1000L);
        store.writeReceipt("x", 50L, 1000L);
        Files.move(store.receiptFile("x").toPath(), store.receiptFile("b").toPath());
        store.writeReceipt("c", 90L, 0L);
        assertTrue(store.updateLastSent(1, LOCK_MS));
        assertEquals(12, store.readLastSentOrZero());
    }

    @Test
    public void lastSentIsNeverRaisedFromPendingOrHeldReports() throws IOException {
        store.savePending(report("p", 50L));
        store.move(store.savePending(report("h", 60L)), CrashReportStore.HELD);
        assertTrue(store.updateLastSent(2, LOCK_MS));
        assertEquals(2, store.readLastSentOrZero());
    }

    @Test
    public void corruptLastSentIsPreservedRebuiltAndNoted() throws IOException {
        store.writeReceipt("r9", 9L, 1000L);
        write(file(CrashReportStore.LAST_SENT_FILE), "damaged");
        assertEquals(0, store.readLastSentOrZero());
        assertTrue(store.updateLastSent(4, LOCK_MS));
        assertEquals(9, store.readLastSentOrZero());
        assertEquals("damaged", text(file("last_sent.corrupt")));
        SeqAllocation a = store.allocateSeq(LOCK_MS);
        assertTrue(a.note, a.note.contains(
                "last_sent corrupt (kept as last_sent.corrupt*); rebuilt from receipts"));
    }

    // ---------------- evidence preservation ----------------

    @Test
    public void preserveCorruptNumbersNamesAndNeverOverwrites() throws IOException {
        File f = file("thing");
        write(f, "first");
        assertEquals("thing.corrupt", store.preserveCorrupt(f, "thing.corrupt").getName());
        write(f, "second");
        assertEquals("thing.corrupt-1", store.preserveCorrupt(f, "thing.corrupt").getName());
        assertFalse(f.exists());
        assertEquals("first", text(file("thing.corrupt")));
        assertEquals("second", text(file("thing.corrupt-1")));
    }

    // ---------------- summary model ----------------

    @Test
    public void summaryEntryRoundTripKeepsPipesInTheExceptionText() throws IOException {
        Report r = Report.newReport("id1", 7L, "", 0, 1234L, "v", "l", "boom|x\ny");
        r.attempts = 6;
        SummaryEntry e = SummaryEntry.of(r, "held: a|b", 99L);
        assertEquals("held: a/b", e.status);
        SummaryEntry d = SummaryEntry.decodeValue(e.encodeValue());
        assertEquals(Long.valueOf(7), d.seq);
        assertEquals("id1", d.id);
        assertEquals(1234L, d.time);
        assertEquals(6, d.attempts);
        assertEquals(99L, d.condensedAt);
        assertEquals("held: a/b", d.status);
        assertEquals("boom|x\ny", d.exc);
    }

    @Test
    public void summaryEntryKeepsAnUnknownSequenceAndTruncatesTheException() throws IOException {
        StringBuilder exc = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            exc.append('e');
        }
        Report r = Report.newReport("u", null, "", 0, 1L, "", "", exc.toString());
        SummaryEntry e = SummaryEntry.of(r, null, 2L);
        assertEquals(CrashReportStore.SUMMARY_EXC_MAX_CHARS, e.exc.length());
        assertEquals("", e.status);
        assertTrue(e.encodeValue().startsWith("unknown|u|"));
        assertNull(SummaryEntry.decodeValue(e.encodeValue()).seq);
    }

    @Test
    public void summaryEntryDecodeRejectsInvalidValues() {
        String[] bad = {null, "", "1|id|0|0|0|s", "1|bad id|0|0|0|s|x", "0|id|0|0|0|s|x",
                "1|id|t|0|0|s|x", "1|id|0|-1|0|s|x"};
        for (String v : bad) {
            expect(CorruptFileException.class, () -> SummaryEntry.decodeValue(v));
        }
    }

    private static SummaryEntry entry(String id, Long seq, long time) {
        return SummaryEntry.of(report(id, seq, time), "held", 0L);
    }

    @Test
    public void foldMovesTheOldestEntriesIntoTheOmittedRange() {
        Summary s = new Summary();
        int n = CrashReportStore.SUMMARY_MAX_ENTRIES + 3;
        for (int i = 1; i <= n; i++) {
            s.entries.add(entry("e" + i, (long) i, i * 10L));
        }
        s.fold();
        assertEquals(CrashReportStore.SUMMARY_MAX_ENTRIES, s.entries.size());
        assertEquals(3, s.omitted);
        assertEquals(1, s.omittedMinSeq);
        assertEquals(3, s.omittedMaxSeq);
        assertEquals(10, s.omittedFirstTime);
        assertEquals(30, s.omittedLastTime);
        assertEquals(Long.valueOf(4), s.entries.get(0).seq);
        assertEquals(n, s.maxSeq());
    }

    @Test
    public void foldCountsUnknownSequencesWithoutWideningTheRange() {
        Summary s = new Summary();
        s.entries.add(entry("u", null, 10L));
        for (int i = 1; i <= CrashReportStore.SUMMARY_MAX_ENTRIES; i++) {
            s.entries.add(entry("e" + i, (long) i, 20L));
        }
        s.fold();
        assertEquals(1, s.omitted);
        assertEquals(0, s.omittedMinSeq);
        assertEquals(0, s.omittedMaxSeq);
        assertEquals(10, s.omittedFirstTime);
        assertEquals(10, s.omittedLastTime);
    }

    @Test
    public void maxSeqIncludesTheOmittedRangeAndIgnoresUnknownEntries() {
        Summary s = new Summary();
        assertEquals(0, s.maxSeq());
        s.entries.add(entry("u", null, 1L));
        assertEquals(0, s.maxSeq());
        s.entries.add(entry("a", 20L, 1L));
        assertEquals(20, s.maxSeq());
        s.omittedMaxSeq = 50;
        assertEquals(50, s.maxSeq());
    }

    // ---------------- summary file ----------------

    @Test
    public void appendSummaryCreatesTheFileAndReadsBack() throws IOException {
        store.appendSummary(report("a", 5L), "held: retention overflow", 123L, LOCK_MS);
        Summary s = store.readSummary();
        assertEquals(1, s.entries.size());
        assertEquals(0, s.omitted);
        SummaryEntry e = s.entries.get(0);
        assertEquals("a", e.id);
        assertEquals(Long.valueOf(5), e.seq);
        assertEquals("held: retention overflow", e.status);
        assertEquals(123L, e.condensedAt);
        assertFalse(text(file(CrashReportStore.SUMMARY_FILE)).contains("omitted="));
    }

    @Test
    public void appendSummaryFoldsPastTheEntryLimitAndWritesTheOmittedRecord() throws IOException {
        int n = CrashReportStore.SUMMARY_MAX_ENTRIES + 3;
        for (int i = 1; i <= n; i++) {
            store.appendSummary(report("e" + i, (long) i, i * 10L), "held", i, LOCK_MS);
        }
        Summary s = store.readSummary();
        assertEquals(CrashReportStore.SUMMARY_MAX_ENTRIES, s.entries.size());
        assertEquals(3, s.omitted);
        assertEquals(1, s.omittedMinSeq);
        assertEquals(3, s.omittedMaxSeq);
        assertEquals(n, s.maxSeq());
        assertTrue(text(file(CrashReportStore.SUMMARY_FILE)).contains("\nomitted=3|1|3|10|30\n"));
    }

    @Test
    public void corruptSummaryIsPreservedWithoutOverwritingAndANewOneStarted() throws IOException {
        File summary = file(CrashReportStore.SUMMARY_FILE);
        write(summary, "first damaged");
        store.appendSummary(report("a", 1L), "held", 1L, LOCK_MS);
        write(summary, "second damaged");
        store.appendSummary(report("b", 2L), "held", 2L, LOCK_MS);
        assertEquals("first damaged", text(file("undelivered_summary.corrupt")));
        assertEquals("second damaged", text(file("undelivered_summary.corrupt-1")));
        Summary s = store.readSummary();
        assertEquals(1, s.entries.size());
        assertEquals("b", s.entries.get(0).id);
    }

    @Test
    public void appendSummaryRejectsAnInvalidReportWithoutWriting() {
        expect(IllegalArgumentException.class,
                () -> store.appendSummary(null, "held", 1L, LOCK_MS));
        Report r = report("ok", 1L);
        r.id = "bad id";
        expect(IllegalArgumentException.class, () -> store.appendSummary(r, "held", 1L, LOCK_MS));
        assertFalse(file(CrashReportStore.SUMMARY_FILE).exists());
    }

    @Test
    public void readSummaryDistinguishesMissingFromCorrupt() throws IOException {
        expect(NoSuchFileException.class, () -> store.readSummary());
        assertTrue(file(CrashReportStore.SUMMARY_FILE).mkdirs());
        expect(CorruptFileException.class, () -> store.readSummary());
    }

    @Test
    public void readSummaryRejectsUnexpectedKeysAndInvalidOmittedRecords() throws IOException {
        String value = entry("a", 1L, 1L).encodeValue();
        String[][] cases = {
                {"e2", value},
                {"x1", value},
                {"omitted", "0|0|0|0|0"},
                {"omitted", "1|0|0|0"},
                {"omitted", "2|5|3|0|0"},
        };
        for (String[] c : cases) {
            Map<String, String> m = new LinkedHashMap<>();
            m.put(c[0], c[1]);
            CrashReportStore.writeAtomic(file(CrashReportStore.SUMMARY_FILE), CrashReportStore.encode(m));
            expect(CorruptFileException.class, () -> store.readSummary());
        }
    }

    // ---------------- store lock ----------------

    @Test
    public void lockIsReentrantAndUnlockWithoutAHoldIsHarmless() throws Exception {
        store.unlock();
        assertTrue(store.lock(0));
        try {
            assertTrue(store.lock(0));
            store.unlock();
            assertFalse(lockOnOtherThread(100));
        } finally {
            store.unlock();
        }
        assertTrue(lockOnOtherThread(LOCK_MS));
    }

    @Test
    public void otherThreadsTimeOutSafelyWhileTheLockIsHeld() throws Exception {
        assertTrue(store.lock(LOCK_MS));
        try {
            assertFalse(lockOnOtherThread(100));
            SeqAllocation a = onOtherThread(() -> store.allocateSeq(100));
            assertNull(a.seq);
            assertEquals("counter lock not acquired within 100 ms", a.note);
            assertFalse(onOtherThread(() -> store.incrementSuppressed(100)));
            assertFalse(onOtherThread(() -> store.updateLastSent(5, 100)));
            assertFalse(onOtherThread(() -> store.clearRecoveryNotes("x", 100)));
            String error = onOtherThread(() -> {
                try {
                    store.appendSummary(report("a", 1L), "held", 1L, 100);
                    return null;
                } catch (IOException e) {
                    return e.getMessage();
                }
            });
            assertEquals("store lock not acquired within 100 ms", error);
        } finally {
            store.unlock();
        }
        assertFalse(file(CrashReportStore.COUNTER_FILE).exists());
        assertFalse(file(CrashReportStore.LAST_SENT_FILE).exists());
        assertFalse(file(CrashReportStore.SUMMARY_FILE).exists());
    }

    @Test
    public void interruptedThreadFailsToLockAndKeepsItsInterruptFlag() {
        Thread.currentThread().interrupt();
        boolean locked = store.lock(LOCK_MS);
        if (locked) {
            store.unlock();
        }
        boolean stillInterrupted = Thread.interrupted();
        assertFalse(locked);
        assertTrue(stillInterrupted);
        assertTrue(store.lock(0));
        store.unlock();
    }
}
