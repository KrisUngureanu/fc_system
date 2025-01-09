package kz.tamur.comps.ui.button;

import static kz.tamur.comps.Utils.getFullPathComponent;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToggleButtonUI;
import kz.tamur.comps.Constants;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.rt.Utils;

/**
 * Класс реализует кнопки для UI
 * 
 * @author Sergey Lebedev
 * 
 */
public class OrButtonUI extends BasicToggleButtonUI {

    /** Константа defaultMouseoverTransparency. */
    public static final float defaultMouseoverTransparency = 0.4f;

    /** Константа maxRounding. */
    public static final int maxRounding = 8;

    /** Константа midRounding. */
    public static final int midRounding = 6;

    /** Константа focusStroke. */
    public static final Stroke focusStroke = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 5);

    /** Цвет подсветки кнопки при наведении мыши. */
    public static Color buttonBg = Utils.getLightSysColor();

    /** The button. */
    private AbstractButton button;

    /** The top bg. */
    private Color topBg = new Color(255, 255, 255, 128);

    private Color selBg = new Color(97, 153, 255, 255);

    /** The bg. */
    private Color bg = Color.WHITE;

    /** The always draw focus. */
    private boolean alwaysDrawFocus = false;

    /** The border color. */
    private Color borderColor = Color.LIGHT_GRAY;

    /** The selected border color. */
    private Color selectedBorderColor = Color.GRAY;

    /** The always draw background. */
    private boolean alwaysDrawBackground = true;

    /** The static top bg. */
    private Color staticTopBg = Color.WHITE;

    /** The disabled static top bg. */
    private Color disabledStaticTopBg = Color.WHITE;

    /** The static bottom bg. */
    private Color staticBottomBg = Utils.getSysColor();

    /** The disabled static bottom bg. */
    private Color disabledStaticBottomBg = Utils.getSilverColor();

    /** The static border color. */
    private Color staticBorderColor = Utils.getDarkShadowSysColor();

    /** The static disabled border color. */
    private Color staticDisabledBorderColor = Utils.getMidSysColor();

    final Color foreground = Color.BLACK;

    /** Константа maxFadeTimes. */
    private static final int maxFadeTimes = 16;

    /** The mouse over. */
    private boolean mouseOver = false;

    /** The mouseover transparency. */
    private float mouseoverTransparency = defaultMouseoverTransparency;

    /** The fade out timer. */
    private Timer fadeOutTimer;

    /** The fade time. */
    private float fadeTime = maxFadeTimes;

    /** The sharp top left. */
    private boolean sharpTopLeft = false;

    /** The sharp top right. */
    private boolean sharpTopRight = false;

    /** The sharp bottom left. */
    private boolean sharpBottomLeft = false;

    /** The sharp bottom right. */
    private boolean sharpBottomRight = false;
    private boolean isSetOpaque = true;

    private boolean isTransparent;

    /**
     * Создание нового экземпляра button UI.
     * 
     * @param button
     *            кнопка
     */
    public OrButtonUI(final AbstractButton button) {
        this(button, true, defaultMouseoverTransparency);
    }

    /**
     * Создание нового экземпляра button UI.
     * 
     * @param button
     *            кнопка
     * @param changeForeground
     *            задать цвет первого плана
     */
    public OrButtonUI(final AbstractButton button, final boolean changeForeground) {
        this(button, changeForeground, defaultMouseoverTransparency);
    }

    /**
     * Создание нового кземпляра button UI.
     * 
     * @param button
     *            кнопка
     * @param mouseoverTransparency
     *            the mouseover transparency
     */
    public OrButtonUI(final AbstractButton button, float mouseoverTransparency) {
        this(button, true, mouseoverTransparency);
    }

    /**
     * Создание нового экземпляра button UI.
     * 
     * @param button
     *            кнопка
     * @param changeForeground
     *            задать цвет первого плана
     * @param mouseoverTransparency
     *            the mouseover transparency
     */
    public OrButtonUI(final AbstractButton button, final boolean changeForeground, float mouseoverTransparency) {
        super();
    }

    /**
     * В данном методе весьма желательно удалять все добавленные на компонент слушатели, а также очищать память от ненужных более данных.
     * Вызывается этот метод из setUI ( ComponentUI newUI ) при смене у компонента его текущего UI, или же при полном удалении компонента
     * и «очищении» его зависимостей.
     * 
     * @see javax.swing.plaf.basic.BasicButtonUI#uninstallUI(javax.swing.JComponent)
     */
    public void uninstallUI(JComponent c) {
        fadeOutTimer.stop();
        fadeOutTimer = null;
        for (ItemListener l : button.getItemListeners()) {
            button.removeItemListener(l);
        }
        for (MouseListener l : button.getMouseListeners()) {
            button.removeMouseListener(l);
        }
    }

    /**
     * Определить поведение и свойства компонента
     */
    protected void init(final AbstractButton button_, float mouseoverTransparency_) {
        button = button_;
        mouseoverTransparency = mouseoverTransparency_;
        button.setBorderPainted(false);
        button.setForeground(foreground);
        isTransparent = button_ instanceof OrTransparentButton;
        fadeOutTimer = new Timer(1000 / 24, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fadeTime++;
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        button.repaint();
                    }
                });

                if (fadeTime == maxFadeTimes) {
                    fadeOutTimer.stop();
                }
            }
        });
        if (!isTransparent) {
            button.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if (button.isSelected()) {
                        fadeOutTimer.stop();
                        fadeTime = 1;
                    } else {
                        fadeTime = 1;
                        fadeOutTimer.restart();
                    }
                }
            });

            button.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    mouseOver = true;
                    fadeOutTimer.stop();
                    fadeTime = 1;
                    button.repaint();
                }

                public void mouseExited(MouseEvent e) {
                    dropMouseOver();
                }
            });
        }
    }

    /**
     * Creates the UI.
     * Данный метод вызывается при создании UI в UIDefaults классе через Reflection
     * 
     * @param c
     *            the c
     * @return component UI
     */
    public static ComponentUI createUI(JComponent c) {
        return new OrButtonUI((AbstractButton) c);
    }

    /**
     * Данный метод предназначен для «инсталляции» UI-класса на определённый J-компонент.
     * 
     * @see javax.swing.plaf.basic.BasicButtonUI#installUI(javax.swing.JComponent)
     */
    public void installUI(JComponent c) {
        super.installUI(c);
        AbstractButton button = (AbstractButton) c;
        init(button, defaultMouseoverTransparency);
        setColor(buttonBg, 128);
        setRoundedSides(Arrays.asList(-1));
        button.setBorder(BorderFactory.createEmptyBorder(Constants.INSETS_4.top, Constants.INSETS_4.left,
                Constants.INSETS_4.bottom, Constants.INSETS_4.right));
    }

    /**
     * Drop mouse over.
     */
    public void dropMouseOver() {
        mouseOver = false;
        fadeTime = 1;
        fadeOutTimer.restart();
    }

    /**
     * Sets the color.
     * 
     * @param color
     *            the color
     * @param alpha
     *            the alpha
     */
    public void setColor(Color color, int alpha) {
        topBg = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
        bg = color;
    }

    /**
     * Sets the static color.
     * 
     * @param topBg
     *            the top bg
     * @param bottomBg
     *            the bottom bg
     */
    public void setStaticColor(Color topBg, Color bottomBg) {
        staticTopBg = topBg;
        staticBottomBg = bottomBg;
        disabledStaticTopBg = topBg;
        disabledStaticBottomBg = new Color(Math.min(255, bottomBg.getRed() + 5), Math.min(255, bottomBg.getGreen() + 5),
                Math.min(255, bottomBg.getBlue() + 5));
    }

    /**
     * Sets the static border color.
     * 
     * @param staticBorderColor
     *            the static border color
     * @param staticDisabledBorderColor
     *            the static disabled border color
     */
    public void setStaticBorderColor(Color staticBorderColor, Color staticDisabledBorderColor) {
        this.staticBorderColor = staticBorderColor;
        this.staticDisabledBorderColor = staticDisabledBorderColor;
    }

    /**
     * Sets the border color.
     * 
     * @param borderColor
     *            the border color
     * @param selectedBorderColor
     *            the selected border color
     */
    public void setBorderColor(Color borderColor, Color selectedBorderColor) {
        this.borderColor = borderColor;
        this.selectedBorderColor = selectedBorderColor;
    }

    /**
     * Sets the inner border color.
     * 
     * @param borderColor
     *            the border color
     * @param selectedBorderColor
     *            the selected border color
     */
    public void setInnerBorderColor(Color borderColor, Color selectedBorderColor) {
        this.borderColor = borderColor;
        this.selectedBorderColor = selectedBorderColor;
    }

    /**
     * Получить top bg.
     * 
     * @return the top bg
     */
    public Color getTopBg() {
        return topBg;
    }

    /**
     * Получить bg.
     * 
     * @return the bg
     */
    public Color getBg() {
        return bg;
    }

    /**
     * Установить rounded sides.
     * 
     * @param rounded
     *            the new rounded sides
     */
    public void setRoundedSides(java.util.List<Integer> rounded) {
        setSharpTopLeft(!rounded.contains(1) && !rounded.contains(-1));
        setSharpTopRight(!rounded.contains(2) && !rounded.contains(-1));
        setSharpBottomLeft(!rounded.contains(3) && !rounded.contains(-1));
        setSharpBottomRight(!rounded.contains(4) && !rounded.contains(-1));
    }

    /**
     * Получить rounded sides.
     * 
     * @return the rounded sides
     */
    public java.util.List<Integer> getRoundedSides() {
        java.util.List<Integer> rounded = new ArrayList<Integer>();
        if (!isSharpTopLeft() && !isSharpBottomRight() && !isSharpBottomLeft() && !isSharpBottomRight()) {
            rounded.add(-1);
        } else if (isSharpTopLeft() && isSharpBottomRight() && isSharpBottomLeft() && isSharpBottomRight()) {
            rounded.add(0);
        } else {
            if (!isSharpTopLeft()) {
                rounded.add(1);
            }
            if (!isSharpTopRight()) {
                rounded.add(2);
            }
            if (!isSharpBottomLeft()) {
                rounded.add(3);
            }
            if (!isSharpBottomRight()) {
                rounded.add(4);
            }
        }
        return rounded;
    }

    /**
     * Проверяет, является ли sharp bottom left.
     * 
     * @return true, если sharp bottom left
     */
    public boolean isSharpBottomLeft() {
        return sharpBottomLeft;
    }

    /**
     * Установить sharp bottom left.
     * 
     * @param sharpBottomLeft
     *            the new sharp bottom left
     */
    public void setSharpBottomLeft(boolean sharpBottomLeft) {
        this.sharpBottomLeft = sharpBottomLeft;
    }

    /**
     * Проверяет, является ли sharp bottom right.
     * 
     * @return true, если sharp bottom right
     */
    public boolean isSharpBottomRight() {
        return sharpBottomRight;
    }

    /**
     * Установить sharp bottom right.
     * 
     * @param sharpBottomRight
     *            the new sharp bottom right
     */
    public void setSharpBottomRight(boolean sharpBottomRight) {
        this.sharpBottomRight = sharpBottomRight;
    }

    /**
     * Проверяет, является ли sharp top left.
     * 
     * @return true, если sharp top left
     */
    public boolean isSharpTopLeft() {
        return sharpTopLeft;
    }

    /**
     * Установить sharp top left.
     * 
     * @param sharpTopLeft
     *            the new sharp top left
     */
    public void setSharpTopLeft(boolean sharpTopLeft) {
        this.sharpTopLeft = sharpTopLeft;
    }

    /**
     * Проверяет, является ли sharp top right.
     * 
     * @return true, если sharp top right
     */
    public boolean isSharpTopRight() {
        return sharpTopRight;
    }

    /**
     * Установить sharp top right.
     * 
     * @param sharpTopRight
     *            the new sharp top right
     */
    public void setSharpTopRight(boolean sharpTopRight) {
        this.sharpTopRight = sharpTopRight;
    }

    /**
     * Установить all sharp.
     * 
     * @param sharp
     *            the new all sharp
     */
    public void setAllSharp(boolean sharp) {
        sharpTopLeft = sharp;
        sharpTopRight = sharp;
        sharpBottomLeft = sharp;
        sharpBottomRight = sharp;
    }

    /**
     * Проверяет, является ли always draw focus.
     * 
     * @return true, если always draw focus
     */
    public boolean isAlwaysDrawFocus() {
        return alwaysDrawFocus;
    }

    /**
     * Установить always draw focus.
     * 
     * @param alwaysDrawFocus
     *            the new always draw focus
     */
    public void setAlwaysDrawFocus(boolean alwaysDrawFocus) {
        this.alwaysDrawFocus = alwaysDrawFocus;
    }

    /**
     * Проверяет, является ли always draw background.
     * 
     * @return true, если always draw background
     */
    public boolean isAlwaysDrawBackground() {
        return alwaysDrawBackground;
    }

    /**
     * Установить always draw background.
     * 
     * @param alwaysDrawBackground
     *            the new always draw background
     */
    public void setAlwaysDrawBackground(boolean alwaysDrawBackground) {
        this.alwaysDrawBackground = alwaysDrawBackground;
    }

    public void paint(Graphics g, JComponent c) {
        if (!isTransparent) {

            if (isSetOpaque) {
                isSetOpaque = false;
                if (c.isOpaque()) {
                    c.repaint();
                } else {
                    setAlwaysDrawBackground(false);
                }
            }
            if (c.isOpaque()) {
                c.setOpaque(false);
            }

            Graphics2D g2d = (Graphics2D) g;
            Object aa = g2d.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            ButtonModel model = ((AbstractButton) c).getModel();

            // Создать три полигона бордеров
            GeneralPath gp0 = new GeneralPath();
            GeneralPath gp1 = new GeneralPath();
            GeneralPath gp2 = new GeneralPath();
            if (!sharpTopLeft) {
                gp0.moveTo(0, maxRounding / 2);
                gp0.quadTo(0, 0, maxRounding / 2, 0);
                gp1.moveTo(1, 1 + midRounding / 2);
                gp1.quadTo(1, 1, 1 + midRounding / 2, 1);
                gp2.moveTo(2, 2 + midRounding / 2);
                gp2.quadTo(2, 2, 2 + midRounding / 2, 2);
            } else {
                gp0.moveTo(0, 0);
                gp1.moveTo(1, 1);
                gp2.moveTo(2, 2);
            }
            if (!sharpTopRight) {
                gp0.lineTo(c.getWidth() - maxRounding / 2 - 1, 0);
                gp0.quadTo(c.getWidth() - 1, 0, c.getWidth() - 1, maxRounding / 2);
                gp1.lineTo(c.getWidth() - midRounding / 2 - 2, 1);
                gp1.quadTo(c.getWidth() - 2, 1, c.getWidth() - 2, 1 + midRounding / 2);
                gp2.lineTo(c.getWidth() - midRounding / 2 - 3, 2);
                gp2.quadTo(c.getWidth() - 3, 2, c.getWidth() - 3, 2 + midRounding / 2);
            } else {
                gp0.lineTo(c.getWidth() - 1, 0);
                gp1.lineTo(c.getWidth() - 2, 1);
                gp2.lineTo(c.getWidth() - 3, 2);
            }
            if (!sharpBottomRight) {
                gp0.lineTo(c.getWidth() - 1, c.getHeight() - maxRounding / 2 - 1);
                gp0.quadTo(c.getWidth() - 1, c.getHeight() - 1, c.getWidth() - maxRounding / 2 - 1, c.getHeight() - 1);
                gp1.lineTo(c.getWidth() - 2, c.getHeight() - midRounding / 2 - 2);
                gp1.quadTo(c.getWidth() - 2, c.getHeight() - 2, c.getWidth() - midRounding / 2 - 2, c.getHeight() - 2);
                gp2.lineTo(c.getWidth() - 3, c.getHeight() - midRounding / 2 - 3);
                gp2.quadTo(c.getWidth() - 3, c.getHeight() - 3, c.getWidth() - midRounding / 2 - 3, c.getHeight() - 3);
            } else {
                gp0.lineTo(c.getWidth() - 1, c.getHeight() - 1);
                gp1.lineTo(c.getWidth() - 2, c.getHeight() - 2);
                gp2.lineTo(c.getWidth() - 3, c.getHeight() - 3);
            }
            if (!sharpBottomLeft) {
                gp0.lineTo(maxRounding / 2, c.getHeight() - 1);
                gp0.quadTo(0, c.getHeight() - 1, 0, c.getHeight() - maxRounding / 2 - 1);
                gp1.lineTo(1 + midRounding / 2, c.getHeight() - 2);
                gp1.quadTo(1, c.getHeight() - 2, 1, c.getHeight() - midRounding / 2 - 2);
                gp2.lineTo(2 + midRounding / 2, c.getHeight() - 3);
                gp2.quadTo(2, c.getHeight() - 3, 2, c.getHeight() - midRounding / 2 - 3);
            } else {
                gp0.lineTo(0, c.getHeight() - 1);
                gp1.lineTo(1, c.getHeight() - 2);
                gp2.lineTo(2, c.getHeight() - 3);
            }
            if (!sharpTopLeft) {
                gp0.lineTo(0, maxRounding / 2);
                gp1.lineTo(1, 1 + midRounding / 2);
                gp2.lineTo(2, 2 + midRounding / 2);
            } else {
                gp0.lineTo(0, 0);
                gp1.lineTo(1, 1);
                gp2.lineTo(2, 2);
            }

            // Статичный фон
            if (alwaysDrawBackground) {
                g2d.setPaint(new GradientPaint(0, 0, c.isEnabled() ? staticTopBg : disabledStaticTopBg, 0, c.getHeight(), c
                        .isEnabled() ? staticBottomBg : disabledStaticBottomBg));
                g2d.fill(gp0);
                g2d.setPaint(c.isEnabled() ? staticBorderColor : staticDisabledBorderColor);
                g2d.draw(gp0);
            }

            if (c.isEnabled()) {
                // Рисуем фон
                Composite composite = g2d.getComposite();
                if (model.isSelected() || model.isArmed()) {
                    int startV = (int) (c.getHeight() / 100f * 1);
                    int endV = (int) (c.getHeight() / 100f * 100);
                    g2d.setPaint(new GradientPaint(0, startV, topBg, 0, endV, selBg));
                    g2d.fill(gp0);
                } else if (mouseOver || fadeTime != maxFadeTimes) {
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, mouseoverTransparency
                            * (maxFadeTimes - fadeTime) / maxFadeTimes));
                    g2d.setPaint(bg);
                    g2d.fill(gp0);
                    if (mouseOver) {
                        g2d.setComposite(composite);
                    } else {
                        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f * (maxFadeTimes - fadeTime)
                                / maxFadeTimes));
                    }
                }

                // Рисуем фокус
                if (c.isFocusOwner() || alwaysDrawFocus) {
                    Composite cc = g2d.getComposite();
                    g2d.setComposite(composite);
                    Stroke s = g2d.getStroke();

                    g2d.setStroke(focusStroke);
                    g2d.setPaint(c.isFocusOwner() ? Color.GRAY : bg);

                    // Внутренний бордер
                    g2d.draw(gp2);

                    // Внешний бордер
                    if (alwaysDrawFocus) {
                        g2d.setStroke(s);
                    }
                    g2d.draw(gp0);

                    if (!alwaysDrawFocus) {
                        g2d.setStroke(s);
                    }
                    g2d.setComposite(cc);
                }

                // Рисуем кайму
                if (model.isSelected() || model.isArmed() || mouseOver || fadeTime != maxFadeTimes) {
                    g2d.setPaint(model.isSelected() ? selectedBorderColor : borderColor);
                    g2d.draw(gp0);
                    g2d.setPaint(model.isPressed() ? bg : Color.WHITE);
                    g2d.draw(gp1);
                }

                if (button instanceof ButtonsFactory.FunctionToolButton
                        && ((ButtonsFactory.FunctionToolButton) button).isMarked()) {
                    int r_ = Utils.getLightRedColor().getRed();
                    int g_ = Utils.getLightRedColor().getGreen();
                    int b_ = Utils.getLightRedColor().getBlue();
                    g2d.setPaint(new Color(r_, g_, b_, 50));
                    g2d.draw(gp0);
                    g2d.setPaint(new Color(r_, g_, b_, 150));
                    g2d.draw(gp1);
                    g2d.setPaint(Utils.getLightRedColor());
                    g2d.draw(gp2);
                }
                // Возвращаем исходный композит
                g2d.setComposite(composite);
            }

            // Возвращаем исходный антиалиасинг
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, aa);

            // Для отрисовки эффекта нажатия
            if (model.isPressed()) {
                g2d.translate(1, 1);
            }
        }
        // Отрисовка текста и изображения
        super.paint(g, c);
    }

    /*
     * Методы для упрощения стилизации кнопок
     */

    /**
     * Setup button ui.
     * 
     * @param button
     *            the button
     * @return or button ui
     */
    public static OrButtonUI setupButtonUI(AbstractButton button) {
        return setupButtonUI(button, -1);
    }

    /**
     * Setup button ui.
     * 
     * @param button
     *            the button
     * @param insets
     *            the insets
     * @return or button ui
     */
    public static OrButtonUI setupButtonUI(AbstractButton button, Insets insets) {
        return setupButtonUI(button, -1, insets);
    }

    /**
     * Setup button ui.
     * 
     * @param button
     *            the button
     * @param rounded
     *            the rounded
     * @return or button ui
     */
    public static OrButtonUI setupButtonUI(AbstractButton button, int rounded) {
        return setupButtonUI(button, Arrays.asList(rounded));
    }

    /**
     * Setup button ui.
     * 
     * @param button
     *            the button
     * @param rounded
     *            the rounded
     * @param insets
     *            the insets
     * @return or button ui
     */
    public static OrButtonUI setupButtonUI(AbstractButton button, int rounded, Insets insets) {
        return setupButtonUI(button, Arrays.asList(rounded), insets);
    }

    /**
     * Setup button ui.
     * 
     * @param button
     *            the button
     * @param rounded
     *            the rounded
     * @return or button ui
     */
    public static OrButtonUI setupButtonUI(AbstractButton button, java.util.List<Integer> rounded) {
        return setupButtonUI(button, rounded, Constants.INSETS_4);
    }

    /**
     * Setup button ui.
     * 
     * @param button
     *            the button
     * @param rounded
     *            the rounded
     * @param insets
     *            the insets
     * @return or button ui
     */
    public static OrButtonUI setupButtonUI(AbstractButton button, java.util.List<Integer> rounded, Insets insets) {
        OrButtonUI stbui = new OrButtonUI(button, false);
        stbui.setColor(buttonBg, 128);
        stbui.setRoundedSides(rounded);
        button.setUI(stbui);
        button.setMargin(insets);
        button.setBorder(BorderFactory.createEmptyBorder(insets.top, insets.left, insets.bottom, insets.right));
        return stbui;
    }

    /**
     * Setup dialog button ui.
     * 
     * @param button
     *            the button
     * @param rounded
     *            the rounded
     * @return or button ui
     */
    public static OrButtonUI setupDialogButtonUI(AbstractButton button, int rounded) {
        return setupDialogButtonUI(button, Arrays.asList(rounded));
    }

    /**
     * Setup dialog button ui.
     * 
     * @param button
     *            the button
     * @param rounded
     *            the rounded
     * @return or button ui
     */
    public static OrButtonUI setupDialogButtonUI(AbstractButton button, java.util.List<Integer> rounded) {
        return setupDialogButtonUI(button, rounded, new Insets(4, 12, 4, 12));
    }

    /**
     * Setup dialog button ui.
     * 
     * @param button
     *            the button
     * @param rounded
     *            the rounded
     * @param insets
     *            the insets
     * @return or button ui
     */
    public static OrButtonUI setupDialogButtonUI(AbstractButton button, int rounded, Insets insets) {
        return setupDialogButtonUI(button, Arrays.asList(rounded), insets);
    }

    /**
     * Setup dialog button ui.
     * 
     * @param button
     *            the button
     * @param rounded
     *            the rounded
     * @param insets
     *            the insets
     * @return or button ui
     */
    public static OrButtonUI setupDialogButtonUI(AbstractButton button, java.util.List<Integer> rounded, Insets insets) {
        OrButtonUI gbui = setupButtonUI(button, rounded, insets);
        gbui.setStaticBorderColor(new Color(155, 155, 155), new Color(175, 175, 175));
        gbui.setStaticColor(new Color(210, 210, 210), new Color(175, 175, 175));
        return gbui;
    }

    /**
     * @param isTransparent the isTransparent to set
     */
    public void setTransparent(boolean isTransparent) {
        this.isTransparent = isTransparent;
    }

    /**
     * @return the staticBottomBg
     */
    public Color getStaticBottomBg() {
        return staticBottomBg;
    }

    /**
     * @param staticBottomBg the staticBottomBg to set
     */
    public void setStaticBottomBg(Color staticBottomBg) {
        this.staticBottomBg = staticBottomBg;
        button.repaint();
    }
    
    @Override
    public Dimension getMinimumSize(JComponent c) {
        try {
            return super.getMinimumSize(c);
        } catch (Exception e) {
            if(c instanceof OrGuiComponent) {
                System.out.println("Invalid property interface! UUID: "+((OrGuiComponent)c).getUUID()+" Name сomponent '" + ((OrGuiComponent)c).getVarName() + "' Path: " + getFullPathComponent(c));
            }else{
                e.printStackTrace();
            }
            return new Dimension(100,20);
        }
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        try {
            return super.getPreferredSize(c);
        } catch (Exception e) {
            if (c instanceof OrGuiComponent) {
                System.out.println("Invalid property interface! UUID: " + ((OrGuiComponent) c).getUUID() + " Name сomponent '"
                        + ((OrGuiComponent) c).getVarName() + "' Path: " + getFullPathComponent(c));
            } else {
                e.printStackTrace();
            }
            return new Dimension(100, 20);
        }
    }

    @Override
    public Dimension getMaximumSize(JComponent c) {
        try {
            return super.getMaximumSize(c);
        } catch (Exception e) {
            if (c instanceof OrGuiComponent) {
                System.out.println("Invalid property interface! UUID: " + ((OrGuiComponent) c).getUUID() + " Name сomponent '"
                        + ((OrGuiComponent) c).getVarName() + "' Path: " + getFullPathComponent(c));
            } else {
                e.printStackTrace();
            }
            return new Dimension(100, 20);
        }
    }
    
    public Color getStaticTopBg() {
        return staticTopBg;
    }

    public void setStaticTopBg(Color staticTopBg) {
        this.staticTopBg = staticTopBg;
    }
}
