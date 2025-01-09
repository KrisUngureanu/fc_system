package kz.tamur.comps.ui.ext.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;

import javax.swing.FocusManager;
import javax.swing.SwingUtilities;

/**
 * The Class SwingUtils.
 * 
 * @author Lebedev Sergey
 */
public class SwingUtils {
    public static final String HANDLES_ENABLE_STATE = "HANDLES_ENABLE_STATE";
    
    
    public static void setOrientation(Component component) {
        setOrientation(component, false);
    }

    public static void setOrientation(Component component, boolean forced) {
        ComponentOrientation orientation = getOrientation();
        if (forced || orientation.isLeftToRight() != component.getComponentOrientation().isLeftToRight()) {
            component.setComponentOrientation(orientation);
        }
    }

    public static ComponentOrientation getOrientation() {

        return ComponentOrientation.getOrientation(Locale.getDefault());

    }

    /**
     * Creates full component size
     */

    public static Rectangle size(Component component) {
        return new Rectangle(0, 0, component.getWidth(), component.getHeight());
    }

    public static Insets max(Insets insets1, Insets insets2) {
        return new Insets(Math.max(insets1.top, insets2.top), Math.max(insets1.left, insets2.left), Math.max(insets1.bottom,
                insets2.bottom), Math.max(insets1.right, insets2.right));
    }

    /**
     * Gets min Insets out of two
     */

    public static Insets min(Insets insets1, Insets insets2) {
        return new Insets(Math.min(insets1.top, insets2.top), Math.min(insets1.left, insets2.left), Math.min(insets1.bottom,
                insets2.bottom), Math.min(insets1.right, insets2.right));
    }

    /**
     * Gets max Dimension out of two
     */

    public static Dimension max(Component component1, Component component2) {
        return max(component1.getPreferredSize(), component2.getPreferredSize());
    }

    public static Dimension max(Dimension dimension1, Dimension dimension2) {
        if (dimension1 == null && dimension2 == null) {
            return null;
        } else if (dimension1 == null) {
            return dimension2;
        } else if (dimension2 == null) {
            return dimension1;
        } else {
            return new Dimension(Math.max(dimension1.width, dimension2.width), Math.max(dimension1.height, dimension2.height));
        }
    }

    /**
     * Gets min Dimension out of two
     */
    public static Dimension min(Dimension dimension1, Dimension dimension2) {
        if (dimension1 == null || dimension2 == null) {
            return null;
        } else {
            return new Dimension(Math.min(dimension1.width, dimension2.width), Math.min(dimension1.height, dimension2.height));
        }
    }
    /**
     * Returns true if first component is equal to second one or any of its childs Otherwise returns false
     */

    public static boolean isEqualOrChild(Component component, Component compared) {
        if (component == compared) {
            return true;
        } else {
            if (component instanceof Container) {
                for (Component c : ((Container) component).getComponents()) {
                    if (isEqualOrChild(c, compared)) {
                        return true;
                    }
                }
                return false;
            } else {
                return false;
            }
        }
    }
    

    /**
     * Returns true if component or any of its childs has focus Otherwise returns false
     */
    public static boolean hasFocusOwner(Component component) {
        Component focusOwner = FocusManager.getCurrentManager().getFocusOwner();
        return component == focusOwner || component instanceof Container && ((Container) component).isAncestorOf(focusOwner);
    }
    
    public static void invokeAndWaitSafely(Runnable runnable) {
        try {
            invokeAndWait(runnable);
        } catch (Throwable e) {
            //
        }
    }

    public static void invokeAndWait(Runnable runnable) throws InterruptedException, InvocationTargetException {
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        } else {
            SwingUtilities.invokeAndWait(runnable);
        }
    }
    
    public static Dimension copy(Dimension dimension) {
        return new Dimension(dimension);
    }

    public static Point copy(Point point) {
        return new Point(point);
    }

    public static Insets copy(Insets insets) {
        return new Insets(insets.top, insets.left, insets.bottom, insets.right);
    }

    public static Color copy(Color color) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }
}
