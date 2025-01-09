package kz.tamur.comps.ui.radiobutton;

import static kz.tamur.rt.Utils.getImageIconFull;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicRadioButtonUI;

import kz.tamur.comps.ui.ext.ShapeProvider;
import kz.tamur.comps.ui.ext.StyleConstants;
import kz.tamur.comps.ui.ext.Timer;
import kz.tamur.comps.ui.ext.utils.ColorUtils;
import kz.tamur.comps.ui.ext.utils.ImageUtils;
import kz.tamur.comps.ui.ext.utils.LafUtils;
import kz.tamur.comps.ui.ext.utils.SwingUtils;

/**
 * The Class OrRadioButtonUI.
 * 
 * @author Sergey Lebedev
 */
public class OrRadioButtonUI extends BasicRadioButtonUI implements ShapeProvider {

    /** Константа MAX_DARKNESS. */
    public static final int MAX_DARKNESS = 5;

    /** The check states. */
    public static List<ImageIcon> CHECK_STATES = new ArrayList<ImageIcon>();

    /** The disabled check. */
    public static ImageIcon DISABLED_CHECK = null;

    static {
        CHECK_STATES.add(StyleConstants.EMPTY_ICON);
        CHECK_STATES.add(getImageIconFull("radiobtnUI1.png"));
        CHECK_STATES.add(getImageIconFull("radiobtnUI2.png"));
        CHECK_STATES.add(getImageIconFull("radiobtnUI3.png"));
        DISABLED_CHECK = ImageUtils.getDisabledCopy("JRadioButton.disabled.check", CHECK_STATES.get(CHECK_STATES.size() - 1));
    }

    /** The border color. */
    private Color borderColor = OrRadioButtonStyle.borderColor;

    /** The dark border color. */
    private Color darkBorderColor = OrRadioButtonStyle.darkBorderColor;

    /** The disabled border color. */
    private Color disabledBorderColor = OrRadioButtonStyle.disabledBorderColor;

    /** The top bg color. */
    private Color topBgColor = OrRadioButtonStyle.topBgColor;

    /** The bottom bg color. */
    private Color bottomBgColor = OrRadioButtonStyle.bottomBgColor;

    /** The top selected bg color. */
    private Color topSelectedBgColor = OrRadioButtonStyle.topSelectedBgColor;

    /** The bottom selected bg color. */
    private Color bottomSelectedBgColor = OrRadioButtonStyle.bottomSelectedBgColor;

    /** The shade width. */
    private int shadeWidth = OrRadioButtonStyle.shadeWidth;

    /** The margin. */
    private Insets margin = OrRadioButtonStyle.margin;

    /** The animated. */
    private boolean animated = OrRadioButtonStyle.animated;

    /** The rollover dark border only. */
    private boolean rolloverDarkBorderOnly = OrRadioButtonStyle.rolloverDarkBorderOnly;

    /** The border stroke. */
    public Stroke borderStroke = new BasicStroke(1.5f);

    /** The icon width. */
    private int iconWidth = 16;

    /** The icon height. */
    private int iconHeight = 16;

    /** The bg darkness. */
    private int bgDarkness = 0;

    /** The rollover. */
    private boolean rollover;

    /** The bg timer. */
    private Timer bgTimer;

    /** The check icon. */
    private int checkIcon;

    /** The checking. */
    private boolean checking;

    /** The check timer. */
    private Timer checkTimer;

    /** The radio button. */
    private JRadioButton radioButton;

    /** The mouse adapter. */
    private MouseAdapter mouseAdapter;

    /** The item listener. */
    private ItemListener itemListener;

    /**
     * Creates the ui.
     * 
     * @param c
     *            the c
     * @return component ui
     */
    public static ComponentUI createUI(JComponent c) {
        return new OrRadioButtonUI();
    }

    
    public void installUI(final JComponent c) {
        super.installUI(c);

        radioButton = (JRadioButton) c;

        // Настройки по умолчанию 
        SwingUtils.setOrientation(c);
        radioButton.setOpaque(false);

        // Инициализация начального значения
        checkIcon = radioButton.isSelected() ? CHECK_STATES.size() - 1 : 0;

        // Обновление рамки и иконки
        updateBorder();
        updateIcon(radioButton);

        // Animation timers and listeners
        bgTimer = new Timer("OrRadioButtonUI.bgUpdater", 40, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (rollover && bgDarkness < MAX_DARKNESS) {
                    bgDarkness++;
                    c.repaint();
                } else if (!rollover && bgDarkness > 0) {
                    bgDarkness--;
                    c.repaint();
                } else {
                    bgTimer.stop();
                }
            }
        });
        mouseAdapter = new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                rollover = true;
                if (isAnimated()) {
                    bgTimer.start();
                } else {
                    bgDarkness = MAX_DARKNESS;
                    c.repaint();
                }
            }

            public void mouseExited(MouseEvent e) {
                rollover = false;
                if (isAnimated()) {
                    bgTimer.start();
                } else {
                    bgDarkness = 0;
                    c.repaint();
                }
            }
        };
        radioButton.addMouseListener(mouseAdapter);

        checkTimer = new Timer("OrRadioButtonUI.iconUpdater", 40, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (checking && checkIcon < CHECK_STATES.size() - 1) {
                    checkIcon++;
                    c.repaint();
                } else if (!checking && checkIcon > 0) {
                    checkIcon--;
                    c.repaint();
                } else {
                    checkTimer.stop();
                }
            }
        });
        itemListener = new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (animated) {
                    if (radioButton.isSelected()) {
                        checking = true;
                        checkTimer.start();
                    } else {
                        checking = false;
                        checkTimer.start();
                    }
                } else {
                    checkTimer.stop();
                    checkIcon = radioButton.isSelected() ? CHECK_STATES.size() - 1 : 0;
                    c.repaint();
                }
            }
        };
        radioButton.addItemListener(itemListener);
    }

    
    public void uninstallUI(JComponent c) {
        radioButton.removeMouseListener(mouseAdapter);
        radioButton.removeItemListener(itemListener);

        super.uninstallUI(c);
    }

    
    public Shape provideShape() {
        return LafUtils.getWebBorderShape(radioButton, getShadeWidth(), getRound());
    }

    /**
     * Update border.
     */
    private void updateBorder() {
        radioButton.setBorder(BorderFactory.createEmptyBorder(margin.top, margin.left, margin.bottom, margin.right));
    }

    /**
     * Получить margin.
     * 
     * @return the margin
     */
    public Insets getMargin() {
        return margin;
    }

    /**
     * Установить margin.
     * 
     * @param margin
     *            the new margin
     */
    public void setMargin(Insets margin) {
        this.margin = margin;
        updateBorder();
    }

    /**
     * Проверяет, является ли animated.
     * 
     * @return true, если animated
     */
    public boolean isAnimated() {
        return animated;
        // return animated && ( radioButton == null || radioButton.getParent () == null || !( radioButton.getParent () instanceof WebListElement || radioButton.getParent () instanceof WebTreeElement ) );
    }

    /**
     * Установить animated.
     * 
     * @param animated
     *            the new animated
     */
    public void setAnimated(boolean animated) {
        this.animated = animated;
    }

    /**
     * Проверяет, является ли rollover dark border only.
     * 
     * @return true, если rollover dark border only
     */
    public boolean isRolloverDarkBorderOnly() {
        return rolloverDarkBorderOnly;
    }

    /**
     * Установить rollover dark border only.
     * 
     * @param rolloverDarkBorderOnly
     *            the new rollover dark border only
     */
    public void setRolloverDarkBorderOnly(boolean rolloverDarkBorderOnly) {
        this.rolloverDarkBorderOnly = rolloverDarkBorderOnly;
    }

    /**
     * Получить border color.
     * 
     * @return the border color
     */
    public Color getBorderColor() {
        return borderColor;
    }

    /**
     * Установить border color.
     * 
     * @param borderColor
     *            the new border color
     */
    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    /**
     * Получить dark border color.
     * 
     * @return the dark border color
     */
    public Color getDarkBorderColor() {
        return darkBorderColor;
    }

    /**
     * Установить dark border color.
     * 
     * @param darkBorderColor
     *            the new dark border color
     */
    public void setDarkBorderColor(Color darkBorderColor) {
        this.darkBorderColor = darkBorderColor;
    }

    /**
     * Получить disabled border color.
     * 
     * @return the disabled border color
     */
    public Color getDisabledBorderColor() {
        return disabledBorderColor;
    }

    /**
     * Установить disabled border color.
     * 
     * @param disabledBorderColor
     *            the new disabled border color
     */
    public void setDisabledBorderColor(Color disabledBorderColor) {
        this.disabledBorderColor = disabledBorderColor;
    }

    /**
     * Получить top bg color.
     * 
     * @return the top bg color
     */
    public Color getTopBgColor() {
        return topBgColor;
    }

    /**
     * Установить top bg color.
     * 
     * @param topBgColor
     *            the new top bg color
     */
    public void setTopBgColor(Color topBgColor) {
        this.topBgColor = topBgColor;
    }

    /**
     * Получить bottom bg color.
     * 
     * @return the bottom bg color
     */
    public Color getBottomBgColor() {
        return bottomBgColor;
    }

    /**
     * Установить bottom bg color.
     * 
     * @param bottomBgColor
     *            the new bottom bg color
     */
    public void setBottomBgColor(Color bottomBgColor) {
        this.bottomBgColor = bottomBgColor;
    }

    /**
     * Получить top selected bg color.
     * 
     * @return the top selected bg color
     */
    public Color getTopSelectedBgColor() {
        return topSelectedBgColor;
    }

    /**
     * Установить top selected bg color.
     * 
     * @param topSelectedBgColor
     *            the new top selected bg color
     */
    public void setTopSelectedBgColor(Color topSelectedBgColor) {
        this.topSelectedBgColor = topSelectedBgColor;
    }

    /**
     * Получить bottom selected bg color.
     * 
     * @return the bottom selected bg color
     */
    public Color getBottomSelectedBgColor() {
        return bottomSelectedBgColor;
    }

    /**
     * Установить bottom selected bg color.
     * 
     * @param bottomSelectedBgColor
     *            the new bottom selected bg color
     */
    public void setBottomSelectedBgColor(Color bottomSelectedBgColor) {
        this.bottomSelectedBgColor = bottomSelectedBgColor;
    }

    /**
     * Получить round.
     * 
     * @return the round
     */
    public int getRound() {
        return 6;
    }

    /**
     * Установить round.
     * 
     * @param round
     *            the new round
     */
    public void setRound(int round) {
        //
    }

    /**
     * Получить shade width.
     * 
     * @return the shade width
     */
    public int getShadeWidth() {
        return shadeWidth;
    }

    /**
     * Установить shade width.
     * 
     * @param shadeWidth
     *            the new shade width
     */
    public void setShadeWidth(int shadeWidth) {
        this.shadeWidth = shadeWidth;
    }

    /**
     * Update icon.
     * 
     * @param radioButton
     *            the radio button
     */
    private void updateIcon(final JRadioButton radioButton) {
        radioButton.setIcon(new Icon() {
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g;
                Object aa = LafUtils.setupAntialias(g2d);

                // Button size and shape
                int round = iconWidth - shadeWidth * 2 - 2;
                Rectangle iconRect = new Rectangle(x + shadeWidth, y + shadeWidth, iconWidth - shadeWidth * 2 - 1, iconHeight
                        - shadeWidth * 2 - 1);
                RoundRectangle2D shape = new RoundRectangle2D.Double(iconRect.x, iconRect.y, iconRect.width, iconRect.height,
                        round, round);

                // Shade
                if (c.isEnabled()) {
                    LafUtils.drawShade(g2d, shape, c.isEnabled() && c.isFocusOwner() ? StyleConstants.fieldFocusColor
                            : StyleConstants.shadeColor, shadeWidth);
                }

                // Background
                int radius = Math.round((float) Math.sqrt(iconRect.width * iconRect.width / 2));
                g2d.setPaint(new RadialGradientPaint(iconRect.x + iconRect.width / 2, iconRect.y + iconRect.height / 2, radius,
                        new float[] { 0f, 1f }, getBgColors(radioButton)));
                g2d.fill(shape);

                // Border
                Stroke os = LafUtils.setupStroke(g2d, borderStroke);
                g2d.setPaint(c.isEnabled() ? (rolloverDarkBorderOnly ? ColorUtils.getProgress(borderColor, darkBorderColor,
                        getProgress()) : darkBorderColor) : disabledBorderColor);
                g2d.draw(shape);
                LafUtils.restoreStroke(g2d, os);

                // Check icon
                if (checkIcon > 0) {
                    ImageIcon icon = radioButton.isEnabled() ? CHECK_STATES.get(checkIcon) : DISABLED_CHECK;
                    g2d.drawImage(icon.getImage(), x + iconWidth / 2 - icon.getIconWidth() / 2,
                            y + iconHeight / 2 - icon.getIconHeight() / 2, radioButton);
                }

                LafUtils.restoreAntialias(g2d, aa);
            }

            public int getIconWidth() {
                return iconWidth;
            }

            public int getIconHeight() {
                return iconHeight;
            }
        });
    }

    /**
     * Получить bg colors.
     * 
     * @param radioButton
     *            the radio button
     * @return the bg colors
     */
    private Color[] getBgColors(JRadioButton radioButton) {
        if (radioButton.isEnabled()) {
            float progress = getProgress();
            if (progress < 1f) {
                return new Color[] { ColorUtils.getProgress(topBgColor, topSelectedBgColor, progress),
                        ColorUtils.getProgress(bottomBgColor, bottomSelectedBgColor, progress) };
            } else {
                return new Color[] { topSelectedBgColor, bottomSelectedBgColor };
            }
        } else {
            return new Color[] { topBgColor, bottomBgColor };
        }
    }

    /**
     * Получить progress.
     * 
     * @return the progress
     */
    private float getProgress() {
        return (float) bgDarkness / MAX_DARKNESS;
    }
}
