package kz.tamur.comps.ui.ext.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 
 * @author Sergey Lebedev
 * 
 */
public class SystemUtils {
    public static final String WINDOWS = "win";
    public static final String MAC = "mac";
    public static final String UNIX = "unix";
    public static final String SOLARIS = "solaris";

    /**
     * Copies text to system clipboard
     */

    public static void copyToClipboard(String text) {
        try {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(new StringSelection(text), null);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns used java version
     */

    public static String getJavaVersion() {
        return System.getProperty("java.version");
    }

    /**
     * Returns used java full name
     */

    public static String getJavaName() {
        return System.getProperty("java.vm.name");
    }

    /**
     * Returns used java vendor
     */

    public static String getJavaVendor() {
        return System.getProperty("java.vm.vendor");
    }

    /**
     * Returns short system name
     */

    public static String getShortSystemName() {
        if (isWindows()) {
            return WINDOWS;
        } else if (isMac()) {
            return MAC;
        } else if (isUnix()) {
            return UNIX;
        } else if (isSolaris()) {
            return SOLARIS;
        } else {
            return null;
        }
    }

    /**
     * Returns system icon
     */

    public static ImageIcon getOsIcon() {
        return getOsIcon(16);
    }

    public static ImageIcon getOsIcon(int size) {
        if (size != 16 && size != 32) {
            size = 16;
        }
        String os = getShortSystemName();
        return os != null ? new ImageIcon(SystemUtils.class.getResource("icons/os/" + size + "/" + os + ".png")) : null;
    }

    /**
     * Is system allows advanced window options
     */

    public static boolean isWindowTransparencyAllowed() {
        return isWindows() || isMac();
    }

    /**
     * Identifies current system
     */

    public static boolean isWindows() {
        return getOsName().toLowerCase(Locale.ROOT).contains("win");
    }

    public static boolean isMac() {
        String os = getOsName().toLowerCase(Locale.ROOT);
        return os.contains("mac") || os.contains("darwin");
    }

    public static boolean isUnix() {
        String os = getOsName().toLowerCase(Locale.ROOT);
        return os.contains("nix") || os.contains("nux");
    }

    public static boolean isSolaris() {
        return getOsName().toLowerCase(Locale.ROOT).contains("sunos");
    }

    /**
     * Operating system information
     */

    public static String getOsArch() {
        return ManagementFactory.getOperatingSystemMXBean().getArch();
    }

    public static String getOsName() {
        return ManagementFactory.getOperatingSystemMXBean().getName();
    }

    public static String getOsVersion() {
        return ManagementFactory.getOperatingSystemMXBean().getVersion();
    }

    public static int getOsProcessors() {
        return ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();
    }

    /**
     * JRE architecture
     */

    public static String getJreArch() {
        return System.getProperty("sun.arch.data.model").contains("64") ? "64" : "32";
    }

    /**
     * Identifies if Caps Lock is on
     */

    public static boolean isCapsLock() {
        return Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK);
    }

    /**
     * Identifies if Num Lock is on
     */

    public static boolean isNumLock() {
        return Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_NUM_LOCK);
    }

    /**
     * Identifies if Scroll Lock is on
     */

    public static boolean isScrollLock() {
        return Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_SCROLL_LOCK);
    }

    /**
     * Returns default GraphicsConfiguration for main screen
     */

    public static GraphicsConfiguration getGraphicsConfiguration() {
        return getGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
    }

    /**
     * Returns default GraphicsEnvironment
     */

    private static GraphicsEnvironment getGraphicsEnvironment() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment();
    }

    /**
     * Returns list of available screen devices
     */

    public static List<GraphicsDevice> getGraphicsDevices() {
        // Retrieving system devices
        GraphicsEnvironment graphicsEnvironment = getGraphicsEnvironment();
        GraphicsDevice[] screenDevices = graphicsEnvironment.getScreenDevices();
        GraphicsDevice defaultScreenDevice = graphicsEnvironment.getDefaultScreenDevice();

        // Collecting devices into list
        List<GraphicsDevice> devices = new ArrayList<GraphicsDevice>();
        for (GraphicsDevice gd : screenDevices) {
            if (gd.getType() == GraphicsDevice.TYPE_RASTER_SCREEN) {
                if (gd == defaultScreenDevice) {
                    // Add default device to list start
                    devices.add(0, gd);
                } else {
                    devices.add(gd);
                }
            }
        }
        return devices;
    }
}
