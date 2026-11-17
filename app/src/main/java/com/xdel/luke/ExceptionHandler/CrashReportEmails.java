package com.xdel.luke.ExceptionHandler;

import com.xdel.luke.ExceptionHandler.CrashReportRecovery.Snapshot;
import com.xdel.luke.ExceptionHandler.CrashReportStore.Report;
import com.xdel.luke.ExceptionHandler.CrashReportStore.SummaryEntry;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * R7 A7: subjects and bodies of the crash-report and weekly-digest emails (approved design
 * section 5). Text only: nothing here sends, reads credentials, or deletes a log file. The
 * recipient stays CrashReportRecovery.RECIPIENTS, and the report subject keeps the existing
 * wording from DefaultExceptionHandler.getLogFiles().
 */
final class CrashReportEmails {

    static final String REPORT_TITLE = "Luke UNCAUGHT ERROR report";
    static final String DIGEST_TITLE = "Luke UNDELIVERED crash reports";
    static final int GAP_LIST_MAX = 20;
    static final int LIST_EXC_MAX_CHARS = 200;

    private CrashReportEmails() {
    }

    /** The crash-day log saved in the report while it is still a file, else null. */
    static String attachmentPath(Report r) {
        String p = r.logPath;
        return p != null && !p.isEmpty() && new File(p).isFile() ? p : null;
    }

    /** The existing subject: "Luke UNCAUGHT ERROR report from <FullName>. Version <v>". */
    static String reportSubject(String fullName, Report r, String currentVersion) {
        String v = r.version.isEmpty() ? CrashReportStore.nz(currentVersion) : r.version;
        return REPORT_TITLE + " from " + CrashReportStore.nz(fullName) + ". Version " + v;
    }

    static String digestSubject(String fullName, String currentVersion) {
        return DIGEST_TITLE + " from " + CrashReportStore.nz(fullName) + ". Version "
                + CrashReportStore.nz(currentVersion);
    }

    /** The crash-report body; its first line is the existing body text. */
    static String reportBody(Report r, Snapshot s, long errors, boolean logAttached) {
        StringBuilder b = new StringBuilder(REPORT_TITLE).append('\n');
        b.append("Report ID: ").append(r.id).append('\n');
        if (r.seq != null) {
            b.append("Crash sequence: ").append(r.seq)
                    .append(duplicateNote(r.seq, r.id, s, null)).append('\n');
            if (!r.seqNote.isEmpty()) {
                b.append("Counter note: ").append(r.seqNote).append('\n');
            }
        } else {
            b.append("Crash sequence: unknown");
            if (!r.seqNote.isEmpty()) {
                b.append(" (").append(r.seqNote).append(')');
            }
            b.append('\n');
        }
        b.append("Last delivered sequence: ").append(lastSentText(s)).append('\n');
        if (r.suppressedBefore > 0) {
            b.append("Suppressed by 60-second rule since previous report: ")
                    .append(r.suppressedBefore).append('\n');
        }
        appendGap(b, r, s);
        appendUndelivered(b, s, r);
        if (errors > 0) {
            b.append("Recovery errors since last report: ").append(errors).append('\n');
        }
        if (!logAttached) {
            b.append("Log file not available\n");
        }
        return b.toString();
    }

    /** The digest body: Digest ID, last delivered sequence and every undelivered report. */
    static String digestBody(String digestId, Snapshot s, long errors) {
        StringBuilder b = new StringBuilder(DIGEST_TITLE).append('\n');
        b.append("Digest ID: ").append(digestId).append('\n');
        b.append("Last delivered sequence: ").append(lastSentText(s)).append('\n');
        if (!appendUndelivered(b, s, null)) {
            b.append("Undelivered crash reports on this device: none\n");
        }
        if (errors > 0) {
            b.append("Recovery errors since last report: ").append(errors).append('\n');
        }
        return b.toString();
    }

    /** HELD (<reason>, since <heldAt>); an expired window also shows its original deadline. */
    static String heldStatus(Report h) {
        if (CrashReportRecovery.HELD_EXPIRED.equals(h.heldReason)) {
            return "HELD (" + h.heldReason + " " + time(h.expiresAt) + ", detected "
                    + time(h.heldAt) + ")";
        }
        String reason = h.heldReason.isEmpty() ? "no reason recorded" : h.heldReason;
        return "HELD (" + reason + ", since " + time(h.heldAt) + ")";
    }

    /** "Sequence gap" or "Sequence restarted", comparing this report with last_sent. */
    private static void appendGap(StringBuilder b, Report r, Snapshot s) {
        if (r.seq == null) {
            return;
        }
        long n = r.seq;
        long m = s.lastSent;
        if (m > 0 && n <= m) {
            b.append("Sequence restarted at ").append(n).append(" (previous last delivered ")
                    .append(m).append(")\n");
            return;
        }
        long from = m + 1;
        long to = n - 1;
        if (from > to) {
            return;
        }
        b.append("Sequence gap: ").append(from == to ? String.valueOf(from) : from + "-" + to)
                .append('\n');
        long count = to - from + 1;
        long listed = 0;
        for (long q = from; q <= to && listed < GAP_LIST_MAX; q++) {
            b.append("  ").append(q).append(": ")
                    .append(hasRecord(q, s) ? "listed below" : "no record on device")
                    .append('\n');
            listed++;
        }
        if (count > listed) {
            b.append("  ... ").append(count - listed).append(" more not itemised\n");
        }
    }

    /**
     * Appends the undelivered list (pending, held, summary) under its heading and returns true,
     * or returns false if there is nothing to list. self is the report being sent, if any; it
     * is not listed but counts when duplicate sequence numbers are marked.
     */
    private static boolean appendUndelivered(StringBuilder b, Snapshot s, Report self) {
        List<String> lines = new ArrayList<>();
        for (Report p : s.pending) {
            lines.add(reportLine(p, "PENDING", s, self));
        }
        for (Report h : s.held) {
            lines.add(reportLine(h, heldStatus(h), s, self));
        }
        if (s.summary != null) {
            if (s.summary.omitted > 0) {
                lines.add("  OMITTED: " + s.summary.omitted + " older reports, seq "
                        + s.summary.omittedMinSeq + "-" + s.summary.omittedMaxSeq + ", "
                        + time(s.summary.omittedFirstTime) + " to "
                        + time(s.summary.omittedLastTime));
            }
            for (SummaryEntry e : s.summary.entries) {
                lines.add("  " + CrashReportStore.seqText(e.seq)
                        + duplicateNote(e.seq, e.id, s, self) + " | " + e.id + " | "
                        + time(e.time) + " | " + oneLine(e.exc) + " | " + e.attempts
                        + " | SUMMARY-ONLY (" + e.status + ")");
            }
        }
        if (!s.summaryIssue.isEmpty()) {
            lines.add("  " + s.summaryIssue);
        }
        if (lines.isEmpty()) {
            return false;
        }
        b.append("Undelivered crash reports on this device:\n");
        for (String line : lines) {
            b.append(line).append('\n');
        }
        return true;
    }

    private static String reportLine(Report x, String status, Snapshot s, Report self) {
        return "  " + CrashReportStore.seqText(x.seq) + duplicateNote(x.seq, x.id, s, self)
                + " | " + x.id + " | " + time(x.time) + " | " + oneLine(x.exc) + " | "
                + x.attempts + " | " + status;
    }

    /** " (duplicate sequence n)" if another record with a different id has the same seq. */
    private static String duplicateNote(Long seq, String id, Snapshot s, Report self) {
        if (seq == null) {
            return "";
        }
        List<Report> reports = new ArrayList<>(s.pending);
        reports.addAll(s.held);
        if (self != null) {
            reports.add(self);
        }
        for (Report x : reports) {
            if (seq.equals(x.seq) && !x.id.equals(id)) {
                return " (duplicate sequence " + seq + ")";
            }
        }
        if (s.summary != null) {
            for (SummaryEntry e : s.summary.entries) {
                if (seq.equals(e.seq) && !e.id.equals(id)) {
                    return " (duplicate sequence " + seq + ")";
                }
            }
        }
        return "";
    }

    /** True if a pending, held or summary record, or the omitted range, has sequence q. */
    private static boolean hasRecord(long q, Snapshot s) {
        for (Report x : s.pending) {
            if (x.seq != null && x.seq == q) {
                return true;
            }
        }
        for (Report x : s.held) {
            if (x.seq != null && x.seq == q) {
                return true;
            }
        }
        if (s.summary != null) {
            if (s.summary.omitted > 0 && q >= s.summary.omittedMinSeq
                    && q <= s.summary.omittedMaxSeq) {
                return true;
            }
            for (SummaryEntry e : s.summary.entries) {
                if (e.seq != null && e.seq == q) {
                    return true;
                }
            }
        }
        return false;
    }

    private static String lastSentText(Snapshot s) {
        return s.lastSent > 0 ? String.valueOf(s.lastSent) : "none";
    }

    /** The exception summary on one line, at most LIST_EXC_MAX_CHARS characters. */
    private static String oneLine(String exc) {
        String t = CrashReportStore.nz(exc).replace('\r', ' ').replace('\n', ' ');
        return CrashReportStore.truncate(t, LIST_EXC_MAX_CHARS);
    }

    /** Device-local time as yyyy-MM-dd HH:mm:ss, or "unknown" for 0 or less. */
    private static String time(long t) {
        if (t <= 0) {
            return "unknown";
        }
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date(t));
    }
}
