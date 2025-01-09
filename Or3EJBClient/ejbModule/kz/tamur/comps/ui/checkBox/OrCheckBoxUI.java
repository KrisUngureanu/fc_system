package kz.tamur.comps.ui.checkBox;

import static kz.tamur.comps.Constants.DONT_CARE;
import static kz.tamur.comps.Constants.NOT_SELECTED;
import static kz.tamur.comps.Constants.SELECTED;
import static kz.tamur.rt.Utils.getImageIconFull;
import static kz.tamur.comps.ui.ext.StyleConstants.EMPTY_ICON;
import kz.tamur.comps.ui.ext.StyleConstants;
import kz.tamur.comps.ui.ext.ShapeProvider;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicCheckBoxUI;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

import kz.tamur.comps.ui.ext.Timer;
import kz.tamur.comps.ui.ext.utils.ColorUtils;
import kz.tamur.comps.ui.ext.utils.ImageUtils;
import kz.tamur.comps.ui.ext.utils.LafUtils;
import kz.tamur.comps.ui.ext.utils.SwingUtils;

/**
 * Класс реализует флажки для UI.
 * 
 * @author Sergey Lebedev
 */
public class OrCheckBoxUI extends BasicCheckBoxUI implements ShapeProvider {

    /** The border color. */
    private Color borderColor = OrCheckBoxStyle.borderColor;

    /** The dark border color. */
    private Color darkBorderColor = OrCheckBoxStyle.darkBorderColor;

    /** The disabled border color. */
    private Color disabledBorderColor = OrCheckBoxStyle.disabledBorderColor;

    /** The top bg color. */
    private Color topBgColor = OrCheckBoxStyle.topBgColor;

    /** The bottom bg color. */
    private Color bottomBgColor = OrCheckBoxStyle.bottomBgColor;

    /** The top selected bg color. */
    private Color topSelectedBgColor = OrCheckBoxStyle.topSelectedBgColor;

    /** The bottom selected bg color. */
    private Color bottomSelectedBgColor = OrCheckBoxStyle.bottomSelectedBgColor;

    /** The round. */
    private int round = OrCheckBoxStyle.round;

    /** The shade width. */
    private int shadeWidth = OrCheckBoxStyle.shadeWidth;

    /** The icon width. */
    private int iconWidth = 16;

    /** The icon height. */
    private int iconHeight = 16;

    /** The bg darkness. */
    private int bgDarkness = 0;

    /** The indx d c_ ns. */
    private int indxDC_NS = dc_nc.size() - 1;

    /** The indx n s_ s. */
    private int indxNS_S = ns_s.size() - 1;

    /** The indx s_ dc. */
    private int indxS_DC = s_dc.size() - 1;

    /** Константа maxDarkness. */
    private static final int maxDarkness = 5;

    /** The animated. */
    private boolean animated = OrCheckBoxStyle.animated;

    /** The rollover dark border only. */
    private boolean rolloverDarkBorderOnly = OrCheckBoxStyle.rolloverDarkBorderOnly;

    /** The rollover. */
    private boolean rollover;

    /** The checking. */
    private boolean checking;

    /** The set state. */
    private boolean setState = false;

    /** The check timer. */
    private Timer checkTimer;

    /** The bg timer. */
    private Timer bgTimer;

    /** The margin. */
    private Insets margin = OrCheckBoxStyle.margin;

    /** The check box. */
    private JCheckBox checkBox = null;

    /** The mouse adapter. */
    private MouseAdapter mouseAdapter;

    /** The item listener. */
    private ItemListener itemListener;

    /** The tri state. */
    private OrTristateCheckBox triState = null;

    /** The border stroke. */
    private Stroke borderStroke = new BasicStroke(1.5f);

    /** The update delay. */
    private static int updateDelay = 40;

    /** The disabled check. */
    private static ImageIcon DISABLED_CHECK = null;

    /** Набор иконок для отрисовки смены состояния из "не выбран" в "выбран". */
    private static List<ImageIcon> ns_s = new ArrayList<ImageIcon>();

    /** Набор иконок для отрисовки смены состояния из "выбран" в "не определён". */
    private static List<ImageIcon> s_dc = new ArrayList<ImageIcon>();

    /** Набор иконок для отрисовки смены состояния из "не определён" в "не выбран". */
    private static List<ImageIcon> dc_nc = new ArrayList<ImageIcon>();

    /** The ch1. */
    private static ImageIcon ch1 = getImageIconFull("checkboxUI1.png");

    /** The ch2. */
    private static ImageIcon ch2 = getImageIconFull("checkboxUI2.png");

    /** The ch3. */
    private static ImageIcon ch3 = getImageIconFull("checkboxUI3.png");

    /** The ch4. */
    private static ImageIcon ch4 = getImageIconFull("checkboxUI4.png");

    /** The dc1. */
    private static ImageIcon dc1 = getImageIconFull("dontCareUI1.png");

    /** The dc2. */
    private static ImageIcon dc2 = getImageIconFull("dontCareUI2.png");

    /** The dc3. */
    private static ImageIcon dc3 = getImageIconFull("dontCareUI3.png");

    /** The dc4. */
    private static ImageIcon dc4 = getImageIconFull("dontCareUI4.png");

    static {
        ns_s.add(EMPTY_ICON);
        ns_s.add(ch1);
        ns_s.add(ch2);
        ns_s.add(ch3);
        ns_s.add(ch4);

        s_dc.add(ch4);
        s_dc.add(ch3);
        s_dc.add(ch2);
        s_dc.add(ch1);
        s_dc.add(EMPTY_ICON);
        s_dc.add(dc1);
        s_dc.add(dc2);
        s_dc.add(dc3);
        s_dc.add(dc4);

        dc_nc.add(dc4);
        dc_nc.add(dc3);
        dc_nc.add(dc2);
        dc_nc.add(dc1);
        dc_nc.add(EMPTY_ICON);

        DISABLED_CHECK = ImageUtils.getDisabledCopy("JCheckBox.disabled.check", ns_s.get(ns_s.size() - 1));
    }

    /**
     * Создание UI.
     * 
     * @param c
     *            Компонент для которого необходим UI
     * @return component новый экземпляр UI
     */
    public static ComponentUI createUI(JComponent c) {
        return new OrCheckBoxUI();
    }

    /**
     * Установка UI
     * 
     * @see javax.swing.plaf.basic.BasicButtonUI#installUI(javax.swing.JComponent)
     */
    public void installUI(final JComponent c) {
        super.installUI(c);
        // Сохранить компонент в локальной переменной
        checkBox = (JCheckBox) c;

        // Если ГШ применяется для трёхпозиционного чекбокса, то нужно запомнить компонент в переменную
        if (c instanceof OrTristateCheckBox) {
            triState = (OrTristateCheckBox) c;
        }
        // Инициализация состояния компонента
        indxNS_S = checkBox.isSelected() ? ns_s.size() - 1 : 0;

        // Конфигурация по умолчанию
        SwingUtils.setOrientation(checkBox);
        checkBox.setOpaque(false);

        // Обновление рамки и иконки компонента
        updateBorder();
        updateIcon(checkBox);

        // Анимация
        bgTimer = new Timer("OrCheckBoxUI.bgUpdater", updateDelay, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (rollover && bgDarkness < maxDarkness) {
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
                if (isAnimated() && c.isEnabled()) {
                    bgTimer.start();
                } else {
                    bgDarkness = maxDarkness;
                    c.repaint();
                }
            }

            public void mouseExited(MouseEvent e) {
                rollover = false;
                if (isAnimated() && c.isEnabled()) {
                    bgTimer.start();
                } else {
                    bgDarkness = 0;
                    c.repaint();
                }
            }
        };
        checkBox.addMouseListener(mouseAdapter);

        checkTimer = new Timer("OrCheckBoxUI.iconUpdater", updateDelay, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (triState == null) { // обычный чекбокс
                    if (checking && indxNS_S < ns_s.size() - 1) {
                        indxNS_S++;
                        c.repaint();
                    } else if (!checking && indxNS_S > 0) {
                        indxNS_S--;
                        c.repaint();
                    } else {
                        checkTimer.stop();
                    }
                } else { // трёхпозиционный чекбокс
                    switch (triState.getState()) {
                    case NOT_SELECTED:
                        if (setState) {
                            setState = false;
                            indxDC_NS = -1;
                        }
                        if (indxDC_NS < dc_nc.size() - 1) {
                            indxDC_NS++;
                            c.repaint();
                        } else {
                            checkTimer.stop();
                        }
                        break;
                    case SELECTED:
                        if (setState) {
                            setState = false;
                            indxNS_S = -1;
                        }
                        if (indxNS_S < ns_s.size() - 1) {
                            indxNS_S++;
                            c.repaint();
                        } else {
                            checkTimer.stop();
                        }
                        break;
                    case DONT_CARE:
                        if (setState) {
                            setState = false;
                            indxS_DC = -1;
                        }
                        if (indxS_DC < s_dc.size() - 1) {
                            indxS_DC++;
                            c.repaint();
                        } else {
                            checkTimer.stop();
                        }
                        break;
                    default:
                        break;
                    }
                }
            }
        });

        itemListener = new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (triState == null) { // обычный чекбокс
                    if (isAnimated() && c.isEnabled()) {
                        if (checkBox.isSelected()) {// срабатывает на Selected и Armed
                            checking = true;
                            checkTimer.start();
                        } else {
                            checking = false;
                            checkTimer.start();
                        }
                    } else {
                        checkTimer.stop();
                        indxNS_S = checkBox.isSelected() ? ns_s.size() - 1 : 0;
                        c.repaint();
                    }
                } else {
                    // срабатывает на Selected и Armed
                    if (isAnimated() && c.isEnabled()) {
                        setState = true;
                        checkTimer.start();
                    } else {
                        checkTimer.stop();
                        switch (triState.getState()) {
                        case NOT_SELECTED:
                            if (triState.isAllowedSelectTri()) {
                                indxDC_NS = dc_nc.size() - 1;
                            }
                            break;
                        case SELECTED:
                            indxNS_S = ns_s.size() - 1;
                            break;
                        case DONT_CARE:
                            indxS_DC = s_dc.size() - 1;
                            break;
                        }
                        c.repaint();
                    }
                }
            }
        };
        checkBox.addItemListener(itemListener);
    }

    /**
     * Удаление UI
     * 
     * @see javax.swing.plaf.basic.BasicButtonUI#uninstallUI(javax.swing.JComponent)
     */
    public void uninstallUI(JComponent c) {
        checkBox.removeMouseListener(mouseAdapter);
        checkBox.removeItemListener(itemListener);
        checkTimer.stop();
        bgTimer.stop();
        super.uninstallUI(c);
    }

    public Shape provideShape() {
        return LafUtils.getWebBorderShape(checkBox, shadeWidth, round);
    }

    /**
     * Update border.
     */
    private void updateBorder() {
        checkBox.setBorder(BorderFactory.createEmptyBorder(margin.top, margin.left, margin.bottom, margin.right));
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
        return animated && checkBox instanceof OrBasicCheckBox ? ((OrBasicCheckBox) checkBox).isAnimate() : true;
    }

    /**
     * Обновление иконки, происходит при вызове repaint компонента.
     * 
     * @param checkBox
     *            the check box
     */
    private void updateIcon(final JCheckBox checkBox) {
        checkBox.setIcon(new Icon() {
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g;
                Object aa = LafUtils.setupAntialias(g2d);

                // Размер и форма кнопки
                Rectangle iconRect = new Rectangle(x + shadeWidth, y + shadeWidth, iconWidth - shadeWidth * 2 - 1, iconHeight
                        - shadeWidth * 2 - 1);
                RoundRectangle2D shape = new RoundRectangle2D.Double(iconRect.x, iconRect.y, iconRect.width, iconRect.height,
                        round * 2, round * 2);

                // Тень
                if (c.isEnabled()) {
                    LafUtils.drawShade(g2d, shape, c.isEnabled() && c.isFocusOwner() ? StyleConstants.fieldFocusColor
                            : StyleConstants.shadeColor, shadeWidth);
                }

                // Фон
                int radius = Math.round((float) Math.sqrt(iconRect.width * iconRect.width / 2));
                g2d.setPaint(new RadialGradientPaint(iconRect.x + iconRect.width / 2, iconRect.y + iconRect.height / 2, radius,
                        new float[] { 0f, 1f }, getBgColors(checkBox)));
                g2d.fill(shape);

                // Рамка
                Stroke os = LafUtils.setupStroke(g2d, borderStroke);
                g2d.setPaint(c.isEnabled() ? (rolloverDarkBorderOnly ? ColorUtils.getProgress(borderColor, darkBorderColor,
                        getProgress()) : darkBorderColor) : disabledBorderColor);
                g2d.draw(shape);
                LafUtils.restoreStroke(g2d, os);
                ImageIcon icon = null;
                if (triState != null) {
                    if (triState.isEnabled()) {
                        switch (triState.getState()) {
                        case NOT_SELECTED:
                            if (setState) {
                                setState = false;
                                indxDC_NS = 0;
                            }
                            icon = triState.isAllowedSelectTri() ? dc_nc.get(indxDC_NS) : ns_s.get(4 - indxDC_NS);
                            break;
                        case SELECTED:
                            if (setState) {
                                setState = false;
                                indxNS_S = 0;
                            }
                            icon = ns_s.get(indxNS_S);
                            break;
                        case DONT_CARE:
                            if (setState) {
                                setState = false;
                                indxS_DC = 0;
                            }
                            icon = s_dc.get(indxS_DC);
                            break;
                        }
                    } else {
                        icon = DISABLED_CHECK;
                    }
                    g2d.drawImage(icon.getImage(), x + iconWidth / 2 - icon.getIconWidth() / 2,
                            y + iconHeight / 2 - icon.getIconHeight() / 2, checkBox);
                } else if (indxNS_S > 0) {
                    icon = checkBox.isEnabled() ? ns_s.get(indxNS_S) : DISABLED_CHECK;
                    g2d.drawImage(icon.getImage(), x + iconWidth / 2 - icon.getIconWidth() / 2,
                            y + iconHeight / 2 - icon.getIconHeight() / 2, checkBox);
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
     * Получить фоновые цвета
     * 
     * @param checkBox
     *            компонент
     * @return массив цветов
     */
    private Color[] getBgColors(JCheckBox checkBox) {
        if (checkBox.isEnabled()) {
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
        return (float) bgDarkness / maxDarkness;
    }
}
