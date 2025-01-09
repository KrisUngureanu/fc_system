package kz.tamur.comps.ui.ext.utils;

import java.util.Calendar;
import java.util.Date;

public class TimeUtils {
    /**
     * Time measure methods
     */

    private static Long pinnedTime = null;
    private static Long lastTime = null;

    public static Long getPinnedTime() {
        return pinnedTime;
    }

    public static Long getLastTime() {
        return lastTime;
    }

    public static void pinTime() {
        pinnedTime = currentTime();
        lastTime = pinnedTime;
    }

    public static long getPassedTime() {
        return getPassedTime(false);
    }

    public static long getPassedTime(boolean total) {
        long time = currentTime();
        long passedTime = total ? time - pinnedTime : time - lastTime;
        lastTime = time;
        return passedTime;
    }

    public static void showPassedTime() {
        showPassedTime(false);
    }

    public static void showPassedTime(String prefix) {
        showPassedTime(false, prefix);
    }

    public static void showPassedTime(boolean total) {
        showPassedTime(total, "");
    }

    public static void showPassedTime(boolean total, String prefix) {
        long time = currentTime();
        System.out.println(prefix + (total ? time - pinnedTime : time - lastTime));
        lastTime = time;
    }

    public static void resetTime() {
        pinnedTime = null;
        lastTime = null;
    }

    /**
     * Nano time measure methods
     */

    private static Long pinnedNanoTime = null;
    private static Long lastNanoTime = null;

    public static Long getPinnedNanoTime() {
        return pinnedNanoTime;
    }

    public static Long getLastNanoTime() {
        return lastNanoTime;
    }

    public static void pinNanoTime() {
        pinnedNanoTime = currentNanoTime();
        lastNanoTime = pinnedNanoTime;
    }

    public static long getPassedNanoTime() {
        return getPassedNanoTime(false);
    }

    public static long getPassedNanoTime(boolean total) {
        long time = currentNanoTime();
        long passedTime = total ? time - pinnedNanoTime : time - lastNanoTime;
        lastNanoTime = time;
        return passedTime;
    }

    public static void showPassedNanoTime() {
        showPassedNanoTime(false);
    }

    public static void showPassedNanoTime(String prefix) {
        showPassedNanoTime(false, prefix);
    }

    public static void showPassedNanoTime(boolean total) {
        showPassedNanoTime(total, "");
    }

    public static void showPassedNanoTime(boolean total, String prefix) {
        long time = currentNanoTime();
        System.out.println(prefix + (total ? time - pinnedNanoTime : time - lastNanoTime));
        lastNanoTime = time;
    }

    public static void resetNanoTime() {
        pinnedNanoTime = null;
        lastNanoTime = null;
    }

    /**
     * Returns current system time
     */

    public static long currentTime() {
        return System.currentTimeMillis();
    }

    /**
     * Returns current system nano time
     */

    public static long currentNanoTime() {
        return System.nanoTime();
    }

    /**
     * Returns current system date
     */

    public static Date currentDate() {
        return new Date(currentTime());
    }

    /**
     * Compares two specified dates if they are representing the same day
     */

    public static boolean isSameDay(Date date1, Date date2) {
        return isSameDay(date1.getTime(), date2.getTime());
    }

    public static boolean isSameDay(Long date1, Long date2) {
        Calendar calendar = Calendar.getInstance();

        // Saving calendar time
        Date tmp = calendar.getTime();

        calendar.setTimeInMillis(date1);
        boolean same = isSameDay(calendar, date2);

        // Restoring calendar time
        calendar.setTime(tmp);

        return same;
    }

    /**
     * Compares calendar date with specified one if they are representing the same day
     */

    public static boolean isSameDay(Calendar calendar, Date date) {
        return isSameDay(calendar, date.getTime());
    }

    public static boolean isSameDay(Calendar calendar, Long date) {
        // Saving calendar time
        long time = calendar.getTimeInMillis();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        calendar.setTimeInMillis(date);
        boolean sameDay = year == calendar.get(Calendar.YEAR) && month == calendar.get(Calendar.MONTH)
                && day == calendar.get(Calendar.DAY_OF_MONTH);

        // Restoring calendar time
        calendar.setTimeInMillis(time);

        return sameDay;
    }

    /**
     * Changes calendar date by days
     */

    public static void increaseByDay(Calendar calendar) {
        changeByDays(calendar, 1);
    }

    public static void decreaseByDay(Calendar calendar) {
        changeByDays(calendar, -1);
    }

    public static void changeByDays(Calendar calendar, int days) {
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + days);
    }
}
