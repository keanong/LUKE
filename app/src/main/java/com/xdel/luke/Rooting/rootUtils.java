package com.xdel.luke.Rooting;

import com.xdel.luke.objClasses.objGlobalMethods;

import java.io.File;

/**
 * Compatibility wrapper. Root detection and enforcement are handled by RootGuard.
 * The old warning-only CheckIsRooted dialog task had no callers and has been retired.
 */
public class rootUtils {

    /** Legacy su-path check, kept for API compatibility. Not used by RootGuard. */
    public static boolean findBinary(String binaryName) {
        boolean found = false;
        try {
            if (!found) {
                String[] places = { "/sbin/", "/system/bin/", "/system/xbin/",
                        "/data/local/xbin/", "/data/local/bin/",
                        "/system/sd/xbin/", "/system/bin/failsafe/", "/data/local/", "/su/xbin" };
                for (String where : places) {
                    if (new File(where + binaryName).exists()) {
                        found = true;

                        break;
                    }
                }
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at rootUtils findBinary. Exception: " + e.getMessage(),"","","","");
        }
        return found;
    }

    public static boolean isRooted() {
        return RootGuard.isRooted();
    }

}
