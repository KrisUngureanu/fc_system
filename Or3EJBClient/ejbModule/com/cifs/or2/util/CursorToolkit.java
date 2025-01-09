package com.cifs.or2.util;

import java.awt.event.*;
import java.awt.*;

import javax.swing.*;

/** Basic CursorToolkit that swallows mouseclicks */
public class CursorToolkit implements Cursors {
    static RootPaneContainer root;
    private final static MouseAdapter mouseAdapter = new MouseAdapter() {
    };
    static Cursor rt;
    RootPaneContainer whole;

    private CursorToolkit() {
    }

    public static void startWaitCursor(JDialog dialog) {
        if (dialog != null) {
            startWaitCursor((JComponent) dialog.getContentPane());
        }
    }

    public static void startWaitCursor(JFrame frame) {
        if (frame != null) {
            startWaitCursor((JComponent) frame.getContentPane());
        }
    }

    public static void startWaitCursor(Container component) {
        if (component != null) {
            startWaitCursor((RootPaneContainer) component);
        }
    }

    /** Sets cursor for specified component to Wait cursor */
    public static void startWaitCursor(JComponent component) {
        if (component != null) {
            RootPaneContainer root = ((RootPaneContainer) component.getTopLevelAncestor());
            startWaitCursor(root);
        }
    }

    public static void startWaitCursor(RootPaneContainer root) {
        try {
            root.getGlassPane().setCursor(WAIT_CURSOR);
            root.getGlassPane().addMouseListener(mouseAdapter);
            root.getGlassPane().setVisible(true);
        } catch (Exception e) {
        }
    }

    /** Sets cursor for specified component to normal cursor */
    public static void stopWaitCursor(JComponent component) {
        if (component != null) {
            RootPaneContainer root = ((RootPaneContainer) component.getTopLevelAncestor());
            stopWaitCursor(root);
        }
    }

    public static void stopWaitCursor(Container container) {
        if (container != null) {
            stopWaitCursor((RootPaneContainer) container);
        }
    }

    public static void stopWaitCursor(JFrame frame) {
        if (frame != null) {

            stopWaitCursor((JComponent) frame.getContentPane());
        }
    }

    public static void stopWaitCursor(JDialog dialog) {
        if (dialog != null) {
            stopWaitCursor((JComponent) dialog.getContentPane());
        }
    }

    public static void stopWaitCursor(RootPaneContainer root) {
        try {
            root.getGlassPane().setCursor(DEFAULT_CURSOR);
            root.getGlassPane().removeMouseListener(mouseAdapter);
            root.getGlassPane().setVisible(false);
        } catch (Exception e) {
        }

    }

    public static void stopHelpCursor(JComponent component) {
        if (component != null) {
            RootPaneContainer root = ((RootPaneContainer) component.getTopLevelAncestor());
            root.getContentPane().setCursor(DEFAULT_CURSOR);
        }

    }

    public static void startHelpCursor(JComponent component) {
        if (component != null) {
            RootPaneContainer root = ((RootPaneContainer) component.getTopLevelAncestor());
            startHelpCursor(root);
        }
    }

    public static void startHelpCursor(RootPaneContainer root) {
        try {
            rt = Cursor.getSystemCustomCursor("MyHelp_red.32x32");
            root.getGlassPane().setCursor(rt);
            root.getGlassPane().addMouseListener(mouseAdapter);
            root.getRootPane().setCursor(rt);
        } catch (AWTException he) {
            he.printStackTrace();
        }
    }

    public static void startCursor(JComponent component, Cursor c) {
        if (component != null) {
            RootPaneContainer root = ((RootPaneContainer) component.getTopLevelAncestor());
            startCursor(root, c);
        }
    }

    public static void startCursor(RootPaneContainer root, Cursor c) {
        root.getGlassPane().setCursor(c);
        root.getRootPane().setCursor(c);
        root.getGlassPane().setVisible(true);
    }

    public static void stopCursor(RootPaneContainer root) {
        root.getGlassPane().setCursor(null);
        root.getRootPane().setCursor(null);
        root.getGlassPane().setVisible(false);
    }
}
