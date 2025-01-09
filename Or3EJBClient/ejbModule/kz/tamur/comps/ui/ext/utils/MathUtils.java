package kz.tamur.comps.ui.ext.utils;

import java.security.SecureRandom;

/**
 * 
 * @author Sergey Lebedev
 * 
 */
public class MathUtils {
    private static SecureRandom random = new SecureRandom();

    /**
     * Returns random int
     */

    public static int random() {
        return random.nextInt();
    }

    public static int random(int n) {
        return random.nextInt(n);
    }

    /**
     * Returns squared value
     */

    public static int sqr(int x) {
        return x * x;
    }

    /**
     * Returns rounded square root
     */

    public static int sqrt(double x) {
        return (int) Math.round(Math.sqrt(x));
    }

    /**
     * Determines max value
     */

    public static int max(int... values) {
        int max = values[0];
        for (int i = 1; i < values.length; i++) {
            max = Math.max(max, values[i]);
        }
        return max;
    }
}