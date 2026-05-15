package com.xdel.luke.objClasses;

import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;

public class NameUtils {
    public static double nameSimilarity(String name1, String name2) {
        List<String> parts1 = normalize(name1);
        List<String> parts2 = normalize(name2);

        int matches = 0;
        boolean[] used = new boolean[parts1.size()];

        for (String p2 : parts2) {
            boolean matched = false;

            for (int i = 0; i < parts1.size(); i++) {
                if (used[i]) continue;

                // Full word match
                if (parts1.get(i).equals(p2)) {
                    matched = true;
                    used[i] = true;
                    break;
                }

                // Initials check (e.g. "AK" vs "Ah Kow")
                if (isInitials(p2) && matchInitials(parts1, p2, i, used)) {
                    matched = true;
                    break;
                }
                if (isInitials(parts1.get(i)) && matchInitials(parts2, parts1.get(i), 0, null)) {
                    matched = true;
                    used[i] = true;
                    break;
                }
            }

            if (matched) matches++;
        }

        double coverage1 = (double) matches / parts1.size();
        double coverage2 = (double) matches / parts2.size();
        return Math.round(((coverage1 + coverage2) / 2 * 100) * 10.0) / 10.0; // round to 1 dp
    }

    public static List<String> normalize(String name) {
        // Remove all non-letter/digit characters → handles punctuation and symbols
        String cleaned = name.replaceAll("[^a-zA-Z0-9\\s]", " ");
        String[] split = cleaned.trim().split("\\s+");

        List<String> result = new ArrayList<>();
        for (String s : split) {
            result.add(s.trim().toLowerCase(Locale.ROOT));
        }
        return result;
    }

    public static boolean isInitials(String part) {
        if (part.length() <= 1) return false;
        for (char c : part.toCharArray()) {
            if (!Character.isLetter(c)) return false;
        }
        return part.equals(part.toUpperCase(Locale.ROOT));
    }

    public static boolean matchInitials(List<String> parts, String initials, int startIndex, boolean[] used) {
        int length = initials.length();
        List<Integer> available = new ArrayList<>();

        for (int i = startIndex; i < parts.size(); i++) {
            if (used != null && used[i]) continue;
            available.add(i);
        }

        if (available.size() < length) return false;

        StringBuilder derived = new StringBuilder();
        for (int j = 0; j < length; j++) {
            derived.append(Character.toUpperCase(parts.get(available.get(j)).charAt(0)));
        }

        if (derived.toString().equalsIgnoreCase(initials)) {
            if (used != null) {
                for (int j = 0; j < length; j++) {
                    used[available.get(j)] = true;
                }
            }
            return true;
        }

        return false;
    }
}
