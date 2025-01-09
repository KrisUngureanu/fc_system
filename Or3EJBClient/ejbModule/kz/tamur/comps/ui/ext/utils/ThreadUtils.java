package kz.tamur.comps.ui.ext.utils;

/**
 * 
 * @author Sergey Lebedev
 * 
 */

public class ThreadUtils {
    /**
     * Causes caller thread to sleep ignoring InterruptedException
     */

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            //
        }
    }
}