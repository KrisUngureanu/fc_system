package kz.tamur.comps.ui.accordion;

import javax.swing.*;

import kz.tamur.comps.ui.ext.StyleConstants;
import kz.tamur.comps.ui.ext.utils.ImageUtils;

/**
 * The Class OrAccordionStyle.
 *
 * @author Lebedev Sergey
 */
public final class OrAccordionStyle {
    
    /** Animate transition between states. */
    public static boolean animate = StyleConstants.animate;

    /** Collapsed state icon. */
    public static int orientation = SwingConstants.VERTICAL;

    /** Collapsed state icon. */
    public static ImageIcon expandIcon = kz.tamur.rt.Utils.getImageIconFull("arrow.png");

    /** Expanded state icon. */
    public static ImageIcon collapseIcon = ImageUtils.rotateImage90CW(expandIcon);

    /** Accordion style type. */
    public static AccordionStyle accordionStyle = AccordionStyle.united;

    /** Fill the whole available for accordion space with expanded panes. */
    public static boolean fillSpace = true;

    /** Allow multiply expanded panes. */
    public static boolean multiplySelectionAllowed = true;

    /** Gap between panes. */
    public static int gap = 0;
}
