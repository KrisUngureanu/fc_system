package kz.tamur.comps.ui.panel;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPanelUI;

import kz.tamur.comps.ui.OrLookAndFeel;
import kz.tamur.comps.ui.ext.Painter;
import kz.tamur.comps.ui.ext.ShapeProvider;
import kz.tamur.comps.ui.ext.StyleConstants;
import kz.tamur.comps.ui.ext.focus.FocusTracker;
import kz.tamur.comps.ui.ext.utils.LafUtils;
import kz.tamur.comps.ui.ext.utils.SwingUtils;

/**
 * The Class OrPanelUI.
 *
 * @author Lebedev Sergey
 */
public class OrPanelUI extends BasicPanelUI implements ShapeProvider, FocusTracker {
    
    /** undecorated. */
    private boolean undecorated = OrPanelStyle.undecorated;
    
    /** draw focus. */
    private boolean drawFocus = OrPanelStyle.drawFocus;
    
    /** round. */
    private int round = OrPanelStyle.round;
    
    /** shade width. */
    private int shadeWidth = OrPanelStyle.shadeWidth;
    
    /** margin. */
    private Insets margin = OrPanelStyle.margin;
    
    /** draw background. */
    private boolean drawBackground = OrPanelStyle.drawBackground;
    
    /** web colored. */
    private boolean webColored = OrPanelStyle.webColored;
    
    /** painter. */
    private Painter painter = OrPanelStyle.painter;
    
    /** clip provider. */
    private ShapeProvider clipProvider = OrPanelStyle.clipProvider;

    /** draw top. */
    private boolean drawTop = OrPanelStyle.drawTop;
    
    /** draw left. */
    private boolean drawLeft = OrPanelStyle.drawLeft;
    
    /** draw bottom. */
    private boolean drawBottom = OrPanelStyle.drawBottom;
    
    /** draw right. */
    private boolean drawRight = OrPanelStyle.drawRight;

    /** panel. */
    private JPanel panel = null;

    /** property change listener. */
    private PropertyChangeListener propertyChangeListener;

    /** focused. */
    private boolean focused = false;

    /**
     * Creates the ui.
     *
     * @param c the c
     * @return component ui
     */
    public static ComponentUI createUI(JComponent c) {
        return new OrPanelUI();
    }

    
    public void installUI(JComponent c) {
        super.installUI(c);

        // Saving panel to local variable
        panel = (JPanel) c;

        // Default settings
        SwingUtils.setOrientation(panel);
        panel.setOpaque(true);
        panel.setBackground(OrPanelStyle.backgroundColor);

        // Updating border
        updateBorder(panel);

        // Orientation change listener
        propertyChangeListener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                updateBorder(panel);
            }
        };
        panel.addPropertyChangeListener(OrLookAndFeel.COMPONENT_ORIENTATION_PROPERTY, propertyChangeListener);

        // Focus tracker
        kz.tamur.comps.ui.ext.focus.FocusManager.registerFocusTracker(OrPanelUI.this);
    }

    
    public void uninstallUI(JComponent c) {
        panel.removePropertyChangeListener(OrLookAndFeel.COMPONENT_ORIENTATION_PROPERTY, propertyChangeListener);

        kz.tamur.comps.ui.ext.focus.FocusManager.unregisterFocusTracker(OrPanelUI.this);

        super.uninstallUI(c);
    }

    
    public Shape provideShape() {
        if (painter != null || undecorated) {
            return SwingUtils.size(panel);
        } else {
            return getPanelShape(panel, true);
        }
    }

    
    public boolean isTrackingEnabled() {
        return !undecorated && drawFocus;
    }

    
    public Component getTrackedComponent() {
        return panel;
    }

    
    public boolean isUniteWithChilds() {
        return true;
    }

    
    public boolean isListenGlobalChange() {
        return false;
    }

    
    public void focusChanged(boolean focused) {
        this.focused = focused;
        panel.repaint();
    }

    /**
     * Update border.
     *
     * @param component the component
     */
    private void updateBorder(JComponent component) {
        // Component orientation
        boolean ltr = panel.getComponentOrientation().isLeftToRight();

        // Actual margin
        Insets m = new Insets(margin.top, ltr ? margin.left : margin.right, margin.bottom, ltr ? margin.right : margin.left);

        // Applying border
        if (painter != null) {
            // Background insets
            Insets bi = painter.getMargin(component);
            component.setBorder(BorderFactory.createEmptyBorder(m.top + bi.top, m.left + bi.left, m.bottom + bi.bottom, m.right
                    + bi.right));
        } else if (!undecorated) {
            // Changing draw marks in case of RTL orientation
            boolean actualDrawLeft = ltr ? drawLeft : drawRight;
            boolean actualDrawRight = ltr ? drawRight : drawLeft;

            // Web-style insets
            int top = m.top + (drawTop ? shadeWidth + 1 : 0);
            int left = m.left + (actualDrawLeft ? shadeWidth + 1 : 0);
            int bottom = m.bottom + (drawBottom ? shadeWidth + 1 : 0);
            int right = m.right + (actualDrawRight ? shadeWidth + 1 : 0);
            component.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
        } else {
            // Empty insets
            component.setBorder(BorderFactory.createEmptyBorder(m.top, m.left, m.bottom, m.right));
        }
    }

    /**
     * Update opacity.
     */
    private void updateOpacity() {
        if (painter != null) {
            panel.setOpaque(painter.isOpaque(panel));
        }
    }

    /**
     * Проверяет, является ли undecorated.
     *
     * @return <code>true</code>, если undecorated
     */
    public boolean isUndecorated() {
        return undecorated;
    }

    /**
     * Установить undecorated.
     *
     * @param undecorated новое значение undecorated
     */
    public void setUndecorated(boolean undecorated) {
        this.undecorated = undecorated;

        // Updating border
        updateBorder(panel);

        // Updating opaque value
        if (painter == null && !undecorated) {
            panel.setOpaque(false);
        }
    }

    /**
     * Проверяет, является ли draw focus.
     *
     * @return <code>true</code>, если draw focus
     */
    public boolean isDrawFocus() {
        return drawFocus;
    }

    /**
     * Установить draw focus.
     *
     * @param drawFocus новое значение draw focus
     */
    public void setDrawFocus(boolean drawFocus) {
        this.drawFocus = drawFocus;
    }

    /**
     * Получить painter.
     *
     * @return painter
     */
    public Painter getPainter() {
        return painter;
    }

    /**
     * Установить painter.
     *
     * @param painter новое значение painter
     */
    public void setPainter(Painter painter) {
        this.painter = painter;
        updateBorder(panel);
        updateOpacity();
    }

    /**
     * Получить clip provider.
     *
     * @return clip provider
     */
    public ShapeProvider getClipProvider() {
        return clipProvider;
    }

    /**
     * Установить clip provider.
     *
     * @param clipProvider новое значение clip provider
     */
    public void setClipProvider(ShapeProvider clipProvider) {
        this.clipProvider = clipProvider;
    }

    /**
     * Получить round.
     *
     * @return round
     */
    public int getRound() {
        if (undecorated) {
            return 0;
        } else {
            return round;
        }
    }

    /**
     * Установить round.
     *
     * @param round новое значение round
     */
    public void setRound(int round) {
        this.round = round;
    }

    /**
     * Получить shade width.
     *
     * @return shade width
     */
    public int getShadeWidth() {
        if (undecorated) {
            return 0;
        } else {
            return shadeWidth;
        }
    }

    /**
     * Установить shade width.
     *
     * @param shadeWidth новое значение shade width
     */
    public void setShadeWidth(int shadeWidth) {
        this.shadeWidth = shadeWidth;
        updateBorder(panel);
    }

    /**
     * Получить margin.
     *
     * @return margin
     */
    public Insets getMargin() {
        return margin;
    }

    /**
     * Установить margin.
     *
     * @param margin новое значение margin
     */
    public void setMargin(Insets margin) {
        this.margin = margin;
        updateBorder(panel);
    }

    /**
     * Проверяет, является ли draw background.
     *
     * @return <code>true</code>, если draw background
     */
    public boolean isDrawBackground() {
        return drawBackground;
    }

    /**
     * Установить draw background.
     *
     * @param drawBackground новое значение draw background
     */
    public void setDrawBackground(boolean drawBackground) {
        this.drawBackground = drawBackground;
    }

    /**
     * Проверяет, является ли web colored.
     *
     * @return <code>true</code>, если web colored
     */
    public boolean isWebColored() {
        return webColored;
    }

    /**
     * Установить web colored.
     *
     * @param webColored новое значение web colored
     */
    public void setWebColored(boolean webColored) {
        this.webColored = webColored;
    }

    /**
     * Проверяет, является ли draw bottom.
     *
     * @return <code>true</code>, если draw bottom
     */
    public boolean isDrawBottom() {
        return drawBottom;
    }

    /**
     * Установить draw bottom.
     *
     * @param drawBottom новое значение draw bottom
     */
    public void setDrawBottom(boolean drawBottom) {
        this.drawBottom = drawBottom;
        updateBorder(panel);
    }

    /**
     * Проверяет, является ли draw left.
     *
     * @return <code>true</code>, если draw left
     */
    public boolean isDrawLeft() {
        return drawLeft;
    }

    /**
     * Установить draw left.
     *
     * @param drawLeft новое значение draw left
     */
    public void setDrawLeft(boolean drawLeft) {
        this.drawLeft = drawLeft;
        updateBorder(panel);
    }

    /**
     * Проверяет, является ли draw right.
     *
     * @return <code>true</code>, если draw right
     */
    public boolean isDrawRight() {
        return drawRight;
    }

    /**
     * Установить draw right.
     *
     * @param drawRight новое значение draw right
     */
    public void setDrawRight(boolean drawRight) {
        this.drawRight = drawRight;
        updateBorder(panel);
    }

    /**
     * Проверяет, является ли draw top.
     *
     * @return <code>true</code>, если draw top
     */
    public boolean isDrawTop() {
        return drawTop;
    }

    /**
     * Установить draw top.
     *
     * @param drawTop новое значение draw top
     */
    public void setDrawTop(boolean drawTop) {
        this.drawTop = drawTop;
        updateBorder(panel);
    }

    /**
     * Sets the draw sides.
     *
     * @param top the top
     * @param left the left
     * @param bottom the bottom
     * @param right the right
     */
    public void setDrawSides(boolean top, boolean left, boolean bottom, boolean right) {
        this.drawTop = top;
        this.drawLeft = left;
        this.drawBottom = bottom;
        this.drawRight = right;
        updateBorder(panel);
    }

    
    public void paint(Graphics g, JComponent c) {
        // To be applied for all childs painting
        LafUtils.setupSystemTextHints(g);

        Shape clip = clipProvider != null ? clipProvider.provideShape() : null;
        Shape oldClip = null;
        if (clip != null) {
            oldClip = LafUtils.intersectClip((Graphics2D) g, clip);
        }
        if (painter != null) {
            // Use background painter instead of default UI graphics
            painter.paint((Graphics2D) g, SwingUtils.size(c), c);
        } else if (!undecorated) {
            // Checking need of painting
            boolean anyBorder = drawTop || drawRight || drawBottom || drawLeft;
            if (anyBorder || drawBackground) {
                Graphics2D g2d = (Graphics2D) g;
                Object aa = LafUtils.setupAntialias(g2d);

                // Border shape
                Shape borderShape = getPanelShape(c, false);

                // Outer shadow
                if (anyBorder && c.isEnabled()) {
                    LafUtils.drawShade(g2d, borderShape, drawFocus && focused ? StyleConstants.fieldFocusColor
                            : StyleConstants.shadeColor, shadeWidth);
                }

                // Background
                if (drawBackground) {
                    // Bg shape
                    Shape bgShape = getPanelShape(c, true);

                    // Draw bg
                    if (webColored) {
                        // Setup cached gradient paint
                        Rectangle bgBounds = bgShape.getBounds();
                        g2d.setPaint(LafUtils.getWebGradientPaint(0, bgBounds.y, 0, bgBounds.y + bgBounds.height));
                    } else {
                        // Setup single color paint
                        g2d.setPaint(c.getBackground());
                    }
                    g2d.fill(bgShape);
                }

                // Border
                if (anyBorder) {
                    g2d.setPaint(c.isEnabled() ? StyleConstants.darkBorderColor : StyleConstants.disabledBorderColor);
                    g2d.draw(borderShape);
                }

                LafUtils.restoreAntialias(g2d, aa);
            }
        }
        if (clip != null) {
            LafUtils.restoreClip(g, oldClip);
        }
    }

    /**
     * Получить panel shape.
     *
     * @param c the c
     * @param bg the bg
     * @return panel shape
     */
    private Shape getPanelShape(JComponent c, boolean bg) {
        // Changing draw marks in case of RTL orientation
        boolean ltr = c.getComponentOrientation().isLeftToRight();
        boolean actualDrawLeft = ltr ? drawLeft : drawRight;
        boolean actualDrawRight = ltr ? drawRight : drawLeft;

        // Width and height
        int w = c.getWidth();
        int h = c.getHeight();

        if (bg) {
            Point[] corners = new Point[4];
            boolean[] rounded = new boolean[4];

            corners[0] = p(actualDrawLeft ? shadeWidth : 0, drawTop ? shadeWidth : 0);
            rounded[0] = actualDrawLeft && drawTop;

            corners[1] = p(actualDrawRight ? w - shadeWidth : w, drawTop ? shadeWidth : 0);
            rounded[1] = actualDrawRight && drawTop;

            corners[2] = p(actualDrawRight ? w - shadeWidth : w, drawBottom ? h - shadeWidth : h);
            rounded[2] = actualDrawRight && drawBottom;

            corners[3] = p(actualDrawLeft ? shadeWidth : 0, drawBottom ? h - shadeWidth : h);
            rounded[3] = actualDrawLeft && drawBottom;

            return LafUtils.createRoundedShape(round > 0 ? round + 1 : 0, corners, rounded);
        } else {
            GeneralPath shape = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
            boolean connect = false;
            boolean moved = false;
            if (drawTop) {
                shape.moveTo(actualDrawLeft ? shadeWidth + round : 0, shadeWidth);
                if (actualDrawRight) {
                    shape.lineTo(w - shadeWidth - round - 1, shadeWidth);
                    shape.quadTo(w - shadeWidth - 1, shadeWidth, w - shadeWidth - 1, shadeWidth + round);
                } else {
                    shape.lineTo(w - 1, shadeWidth);
                }
                connect = true;
            }
            if (actualDrawRight) {
                if (!connect) {
                    shape.moveTo(w - shadeWidth - 1, drawTop ? shadeWidth + round : 0);
                    moved = true;
                }
                if (drawBottom) {
                    shape.lineTo(w - shadeWidth - 1, h - shadeWidth - round - 1);
                    shape.quadTo(w - shadeWidth - 1, h - shadeWidth - 1, w - shadeWidth - round - 1, h - shadeWidth - 1);
                } else {
                    shape.lineTo(w - shadeWidth - 1, h - 1);
                }
                connect = true;
            } else {
                connect = false;
            }
            if (drawBottom) {
                if (!connect) {
                    shape.moveTo(actualDrawRight ? w - shadeWidth - round - 1 : w - 1, h - shadeWidth - 1);
                    moved = true;
                }
                if (actualDrawLeft) {
                    shape.lineTo(shadeWidth + round, h - shadeWidth - 1);
                    shape.quadTo(shadeWidth, h - shadeWidth - 1, shadeWidth, h - shadeWidth - round - 1);
                } else {
                    shape.lineTo(0, h - shadeWidth - 1);
                }
                connect = true;
            } else {
                connect = false;
            }
            if (actualDrawLeft) {
                if (!connect) {
                    shape.moveTo(shadeWidth, drawBottom ? h - shadeWidth - round - 1 : h - 1);
                    moved = true;
                }
                if (drawTop) {
                    shape.lineTo(shadeWidth, shadeWidth + round);
                    shape.quadTo(shadeWidth, shadeWidth, shadeWidth + round, shadeWidth);
                    if (!moved) {
                        shape.closePath();
                    }
                } else {
                    shape.lineTo(shadeWidth, 0);
                }
            }
            return shape;
        }
    }

    /**
     * P.
     *
     * @param x the x
     * @param y the y
     * @return point
     */
    private Point p(int x, int y) {
        return new Point(x, y);
    }

    
    public Dimension getPreferredSize(JComponent c) {
        Dimension ps = c.getLayout() != null ? c.getLayout().preferredLayoutSize(c) : null;
        if (painter != null) {
            ps = SwingUtils.max(ps, painter.getPreferredSize(c));
        }
        return ps;
    }

    
    public Dimension getMaximumSize(JComponent c) {
        // Fix for some of the Swing layouts
        return null;
    }
}
