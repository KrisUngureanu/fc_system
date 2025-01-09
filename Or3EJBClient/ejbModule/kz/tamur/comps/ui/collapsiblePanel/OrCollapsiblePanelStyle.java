package kz.tamur.comps.ui.collapsiblePanel;

import javax.swing.*;

import java.awt.*;

import kz.tamur.comps.ui.ext.StyleConstants;
import kz.tamur.comps.ui.ext.utils.ImageUtils;

/**
 * The Class OrCollapsiblePanelStyle.
 *
 * @author Lebedev Sergey
 */
public final class OrCollapsiblePanelStyle {
    
    /** Animate transition between states. */
    public static boolean animate = StyleConstants.animate;

    /** Collapsed state icon. */
    public static ImageIcon expandIcon = kz.tamur.rt.Utils.getImageIconFull("arrow.png");

    /** Expanded state icon. */
    public static ImageIcon collapseIcon = ImageUtils.rotateImage180(expandIcon);

    /** State icon margin. */
    public static Insets stateIconMargin = new Insets(5, 5, 5, 5);

    /** Rotate state icon according to title pane position. */
    public static boolean rotateStateIcon = true;

    /** Display state icon in title pane. */
    public static boolean showStateIcon = true;

    /**
     * State icon position (SwingConstants.LEFT/RIGHT)
     */
    public static int stateIconPostion = SwingConstants.RIGHT;

    /**
     * Title pane position (SwingConstants.TOP/LEFT/BOTTOM/RIGHT)
     */
    public static int titlePanePostion = SwingConstants.TOP;
    
    /** Default content margin. */
    public static Insets contentMargin = new Insets(0, 0, 0, 0);
}
