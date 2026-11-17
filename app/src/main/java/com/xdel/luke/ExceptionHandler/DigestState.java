package com.xdel.luke.ExceptionHandler;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * R7 A7: r7_crash/digest.state, kept apart from every report file: the weekly digest times,
 * the last Digest ID and the count of recovery errors not yet reported. Saved atomically with
 * a CRC like every store file. Read and write it only while holding the store lock.
 */
final class DigestState {

    static final String FILE = "digest.state";
    static final String CORRUPT_FILE = "digest.state.corrupt";
    /** Digest IDs are UUID.randomUUID().toString(); lastId is "" until the first attempt. */
    private static final Pattern DIGEST_ID =
            Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");

    long lastAttemptAt;
    long lastSentAt;
    String lastId = "";
    long recoveryErrors;

    /**
     * The saved state, or an empty one if the file is missing. A damaged file is kept as
     * digest.state.corrupt* (never overwritten), described in issue, and not trusted.
     * Other I/O failures are thrown, so the caller can skip this run and keep the file.
     */
    static DigestState read(CrashReportStore store, StringBuilder issue) throws IOException {
        File f = new File(store.dir(), FILE);
        if (!f.exists()) {
            return new DigestState();
        }
        try {
            if (!f.isFile()) {
                throw new CrashReportStore.CorruptFileException("digest.state is not a file");
            }
            Map<String, String> m = store.readKv(f);
            DigestState s = new DigestState();
            s.lastAttemptAt = nonNegative(m.get("lastAttemptAt"), "lastAttemptAt");
            s.lastSentAt = nonNegative(m.get("lastSentAt"), "lastSentAt");
            s.lastId = validId(m.get("lastId"));
            s.recoveryErrors = nonNegative(m.get("recoveryErrors"), "recoveryErrors");
            return s;
        } catch (CrashReportStore.CorruptFileException e) {
            File to = new File(store.dir(), CORRUPT_FILE);
            for (int i = 1; to.exists(); i++) {
                to = new File(store.dir(), CORRUPT_FILE + "-" + i);
            }
            CrashReportStore.moveAtomic(f, to);
            if (issue != null) {
                issue.append("digest.state corrupt (").append(e.getMessage())
                        .append("); kept as ").append(to.getName());
            }
            return new DigestState();
        }
    }

    /** Saves the state atomically; the caller must hold the store lock. */
    void write(CrashReportStore store) throws IOException {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("lastAttemptAt", String.valueOf(lastAttemptAt));
        m.put("lastSentAt", String.valueOf(lastSentAt));
        m.put("lastId", CrashReportStore.nz(lastId));
        m.put("recoveryErrors", String.valueOf(recoveryErrors));
        CrashReportStore.writeAtomic(new File(store.dir(), FILE), CrashReportStore.encode(m));
    }

    /** Lowers the error count by the number already reported, never below zero. */
    void consumeErrors(long reported) {
        if (reported > 0) {
            recoveryErrors = Math.max(0, recoveryErrors - reported);
        }
    }

    /** Counts one more recovery error, stopping at Long.MAX_VALUE. */
    void addError() {
        if (recoveryErrors < Long.MAX_VALUE) {
            recoveryErrors++;
        }
    }

    private static long nonNegative(String s, String key)
            throws CrashReportStore.CorruptFileException {
        long v = CrashReportStore.parseLong(s, key);
        if (v < 0) {
            throw new CrashReportStore.CorruptFileException("negative " + key);
        }
        return v;
    }

    /** "" before the first attempt, else a Digest ID; missing or other values are corrupt. */
    private static String validId(String s) throws CrashReportStore.CorruptFileException {
        if (s == null) {
            throw new CrashReportStore.CorruptFileException("missing lastId");
        }
        if (!s.isEmpty() && !DIGEST_ID.matcher(s).matches()) {
            throw new CrashReportStore.CorruptFileException("invalid lastId");
        }
        return s;
    }
}
