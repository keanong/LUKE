package com.xdel.luke.ExceptionHandler;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * R7 A7: exclusive, non-blocking lock on r7_crash/.send_<key>.lock for the whole of one send.
 * A key already held in this process is refused before its file is opened, because closing any
 * other channel on a locked file can release the lock on Linux. Lock files are never deleted:
 * deleting a locked file would let another process lock a new file at the same path. The OS
 * releases the file lock when the process ends.
 */
final class SendLock {

    static final String PREFIX = ".send_";
    static final String EXT = ".lock";
    private static final Pattern KEY = Pattern.compile("[0-9A-Za-z-]{1,64}");

    //keys held by this process; guarded by HELD_KEYS itself
    private static final Set<String> HELD_KEYS = new HashSet<>();

    private final String key;
    private final RandomAccessFile raf;
    private final FileLock lock;
    private boolean released;

    private SendLock(String key, RandomAccessFile raf, FileLock lock) {
        this.key = key;
        this.raf = raf;
        this.lock = lock;
    }

    static File fileFor(File dir, String key) {
        return new File(dir, PREFIX + key + EXT);
    }

    /**
     * The lock, or null if this process or another one holds it, it cannot be taken, or dir or
     * key is invalid. On every failure the file is closed and the key is forgotten.
     */
    static SendLock tryAcquire(File dir, String key) {
        if (dir == null || key == null || !KEY.matcher(key).matches()) {
            return null;
        }
        synchronized (HELD_KEYS) {
            if (!HELD_KEYS.add(key)) {
                return null;
            }
        }
        RandomAccessFile raf = null;
        FileLock l = null;
        try {
            if (!dir.isDirectory() && !dir.mkdirs() && !dir.isDirectory()) {
                return null;
            }
            raf = new RandomAccessFile(fileFor(dir, key), "rw");
            l = raf.getChannel().tryLock();
        } catch (OverlappingFileLockException e) {
            l = null;
        } catch (Exception e) {
            l = null;
        } finally {
            if (l == null) {
                closeQuietly(raf);
                forget(key);
            }
        }
        return l == null ? null : new SendLock(key, raf, l);
    }

    /** Releases the lock, closes the file and forgets the key; safe to call repeatedly. */
    synchronized void release() {
        if (released) {
            return;
        }
        released = true;
        try {
            lock.release();
        } catch (Exception ignored) {
            //closing the file below releases it as well
        }
        closeQuietly(raf);
        forget(key);
    }

    private static void forget(String key) {
        synchronized (HELD_KEYS) {
            HELD_KEYS.remove(key);
        }
    }

    private static void closeQuietly(RandomAccessFile raf) {
        if (raf != null) {
            try {
                raf.close();
            } catch (Exception ignored) {
                //nothing else to release
            }
        }
    }
}
