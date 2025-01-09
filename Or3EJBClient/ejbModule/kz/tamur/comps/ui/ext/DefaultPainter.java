package kz.tamur.comps.ui.ext;

import java.awt.*;

/**
 * 
 * @author Sergey Lebedev
 * 
 * @param <E>
 */
public abstract class DefaultPainter<E extends Component> implements Painter<E> {
    protected boolean opaque = false;
    protected Dimension preferredSize = new Dimension(0, 0);
    protected Insets margin = new Insets(0, 0, 0, 0);

    public DefaultPainter() {
        super();
    }

    public boolean isOpaque(E c) {
        return opaque;
    }

    public void setOpaque(boolean opaque) {
        this.opaque = opaque;
    }

    public Dimension getPreferredSize(E c) {
        return preferredSize;
    }

    public void setPreferredSize(Dimension preferredSize) {
        this.preferredSize = preferredSize;
    }

    public Insets getMargin(E c) {
        return margin;
    }

    public void setMargin(Insets margin) {
        this.margin = margin;
    }

    public void setMargin(int top, int left, int bottom, int right) {
        setMargin(new Insets(top, left, bottom, right));
    }

    public void setMargin(int margin) {
        setMargin(margin, margin, margin, margin);
    }
}