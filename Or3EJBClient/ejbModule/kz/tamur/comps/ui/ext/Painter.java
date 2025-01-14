package kz.tamur.comps.ui.ext;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;

public interface Painter<E extends Component>
{
    /**
     * Returned value will affect component's (which uses this painter) opacity, which means that if "true" is returned - the component will
     * be also made opaque when this painter is set for some specific component
     */
    public boolean isOpaque ( E c );

    /**
     * Preferred background size in which background can be properly painted
     */
    public Dimension getPreferredSize ( E c );

    /**
     * Preferred background margin which could be used by components to set their additional margins, this value is usually added to the
     * component's margin value if it has one
     */
    public Insets getMargin ( E c );

    /**
     * Main background painting method
     * <p/>
     * Graphics and JComponent are transferred directly from component UI paint method
     * <p/>
     * Bounds could be sometimes specified by the component UI, but in most cases it fits full component size and generated by
     * SwingUtils.size(component) method
     */
    public void paint ( Graphics2D g2d, Rectangle bounds, E c );
}
