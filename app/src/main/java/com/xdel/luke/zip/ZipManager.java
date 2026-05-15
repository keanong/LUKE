package com.xdel.luke.zip;

import com.xdel.luke.JobsHandler;
import com.xdel.luke.objClasses.objGlobalMethods;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipManager {
    private static int BUFFER_SIZE = 6 * 1024;

    public static void zip(ArrayList<String> files, String zipFile) {
        try {
            BufferedInputStream origin = null;
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));

            String file = "";
            try {
                byte data[] = new byte[BUFFER_SIZE];
                for (int i = 0; i < files.size(); i++) {
                    FileInputStream fi = new FileInputStream(files.get(i));
                    origin = new BufferedInputStream(fi, BUFFER_SIZE);
                    try {
                        file = files.get(i);
                        ZipEntry entry = new ZipEntry(file.substring(file.lastIndexOf("/") + 1));
                        out.putNextEntry(entry);
                        int count;
                        while ((count = origin.read(data, 0, BUFFER_SIZE)) != -1) {
                            out.write(data, 0, count);
                        }
                    } finally {
                        origin.close();
                    }
                }
            } finally {
                out.close();
            }
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            String s = e.getMessage();
            objGlobalMethods.writeToFile("Exception at ZipManager zip. Exception: " + s + "\nStack:\n" + stack, "", "", "", "");
        }
    }
}
