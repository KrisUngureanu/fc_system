package kz.tamur.comps.ui.ext.utils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

/**
 * 
 * @author Sergey Lebedev
 * 
 */
public class TextUtils {
    /**
     * Retrieves full word at location in text
     */

    public static Integer findFirstNumber(String text) {
        Integer number = null;
        int start = -1;
        for (int j = 0; j < text.length(); j++) {

            try {
                // Trying to parse text into int
                number = Integer.parseInt(text.substring(start != -1 ? start : j, j + 1));
                if (start == -1) {
                    start = j;
                }
            } catch (Throwable ex) {
                start = -1;
                if (number != null) {
                    // Fount int
                    break;
                }
            }
        }
        return number;
    }

    /**
     * Returns the word from a text at the specified location
     */

    private static final List<String> textSeparators = Arrays.asList(" ", ".", ",", ":", ";", "/", "\\", "\n", "\t", "|", "{",
            "}", "[", "]", "(", ")", "<", ">", "-", "+", "\"", "'", "*", "%", "$", "#", "@", "!", "~", "^", "&", "?");

    public static String getWord(String text, int location) {
        int wordStart = location;
        int wordEnd = location;

        // At the word start
        while (wordEnd < text.length() - 1 && !textSeparators.contains(text.substring(wordEnd, wordEnd + 1))) {
            wordEnd++;
        }

        // At the word end
        while (wordStart > 0 && !textSeparators.contains(text.substring(wordStart - 1, wordStart))) {
            wordStart--;
        }

        return wordStart == wordEnd ? null : text.substring(wordStart, wordEnd);
    }

    /**
     * Removes first lines from text (lines are determined by "\n" separator)
     */

    public static String removeFirstLines(String text, int numberOfLines) {
        int removed = 0;
        while (removed < numberOfLines) {
            int index = text.indexOf("\n");
            if (index != -1) {
                text = text.substring(index + "\n".length());
                removed++;
            } else {
                return "";
            }
        }
        return text;
    }

    /**
     * Splits single String into list by a common separator
     */

    public static List<String> split(String text, String separator) {
        return Arrays.asList(text.split(separator));
    }

    /**
     * Parses point from text value
     */

    public static Point parsePoint(String text) {
        text = text.trim();
        StringTokenizer st = new StringTokenizer(text, ",", false);
        if (st.countTokens() == 2) {
            return new Point(Integer.parseInt(st.nextToken().trim()), Integer.parseInt(st.nextToken().trim()));
        } else {
            return null;
        }
    }

    /**
     * Removes all control symbols from string (fast example of replaceAll("\\p{Cntrl}", ""))
     */

    private static final char[] buf = new char[1024];

    public static String removeControlSymbols(String text) {
        int length = text.length();
        char[] oldChars = (length < 1024) ? buf : new char[length];
        text.getChars(0, length, oldChars, 0);
        int newLen = 0;
        for (int j = 0; j < length; j++) {
            char ch = oldChars[j];
            if (ch >= ' ') {
                oldChars[newLen] = ch;
                newLen++;
            }
        }
        return text;
    }

    /**
     * Creates shortened text
     */

    public static String shortenText(String text, int maxLength, boolean addDots) {
        if (text.length() <= maxLength) {
            return text;
        } else {
            return text.substring(0, maxLength > 3 && addDots ? maxLength - 3 : maxLength) + (addDots ? "..." : "");
        }
    }

    /**
     * Converts string to list
     */

    public static List<String> stringToList(String string, String separator) {
        List<String> imageTags = new ArrayList<String>();
        if (string != null) {
            StringTokenizer tokenizer = new StringTokenizer(string, separator, false);
            while (tokenizer.hasMoreTokens()) {
                imageTags.add(tokenizer.nextToken().trim());
            }
        }
        return imageTags;
    }

    /**
     * Converts list to string
     */

    public static String listToString(List list, String separator) {
        StringBuilder stringBuilder = new StringBuilder();
        if (list != null) {
            int end = list.size() - 1;
            for (int i = 0; i <= end; i++) {
                stringBuilder.append(list.get(i).toString());
                stringBuilder.append(i != end ? separator : "");
            }
        }
        return stringBuilder.toString();
    }

    /**
     * Creates unique IDs
     */

    private static final int partLength = 5;
    private static final String prefix = "WebLaF";
    private static final String suffix = "ID";
    private static final String tooltipPrefix = "TT";
    private static final String checkboxListPrefix = "CBL";
    private static final String webGlassPanePrefix = "WGP";
    private static final String webFileListPrefix = "WFL";
    private static final String webSettingsPrefix = "WS";

    public static String generateTooltipId() {
        return generateId(tooltipPrefix, null);
    }

    public static String generateCheckboxListId() {
        return generateId(checkboxListPrefix, null);
    }

    public static String generateWebGlassPaneId() {
        return generateId(webGlassPanePrefix, null);
    }

    public static String generateWebFileListId() {
        return generateId(webFileListPrefix, null);
    }

    public static String generateWebSettingsId() {
        return generateId(webSettingsPrefix, null);
    }

    public static String generateId(String prefix) {
        return generateId(prefix, null);
    }

    public static String generateId(String prefix, String suffix) {
        return (prefix == null ? TextUtils.prefix : prefix) + "-" + generateIdBody() + "-"
                + (suffix == null ? TextUtils.suffix : suffix);
    }

    private static String generateIdBody() {
        return generateId(partLength) + "-" + generateId(partLength) + "-" + generateId(partLength) + "-"
                + generateId(partLength);
    }

    private static String generateId(int length) {
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char next = 0;
            int range = 10;
            switch (MathUtils.random(3)) {
            case 0: {
                next = '0';
                range = 10;
            }
                break;
            case 1: {
                next = 'a';
                range = 26;
            }
                break;
            case 2: {
                next = 'A';
                range = 26;
            }
                break;
            }
            stringBuilder.append((char) ((MathUtils.random(range)) + next));
        }
        return stringBuilder.toString();
    }
}
