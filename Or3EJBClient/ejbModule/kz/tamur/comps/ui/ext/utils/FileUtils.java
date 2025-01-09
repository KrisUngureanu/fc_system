package kz.tamur.comps.ui.ext.utils;

/**
 * 
 * @author Sergey Lebedev
 * 
 */
public class FileUtils {
    // В класс будут перенесены утилиты после расформирования класса OrLafUtil

    public static String getFileExtPart(String file, boolean withDot) {
        int i = file.lastIndexOf(".");
        return i == -1 ? "" : withDot ? new String(file.substring(i)) : new String(file.substring(i + 1));
    }
}
