package kz.tamur.comps.ui.comboBox;

import sun.swing.DefaultLookup;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ScrollBarUI;
import javax.swing.plaf.ScrollPaneUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import kz.tamur.comps.ui.OrLookAndFeel;
import kz.tamur.comps.ui.button.OrTransparentButton;
import kz.tamur.comps.ui.ext.OrDefaultCellEditor;
import kz.tamur.comps.ui.ext.ShapeProvider;
import kz.tamur.comps.ui.ext.StyleConstants;
import kz.tamur.comps.ui.ext.utils.LafUtils;
import kz.tamur.comps.ui.ext.utils.SwingUtils;
import kz.tamur.comps.ui.scrollbar.OrScrollBarUI;
import kz.tamur.comps.ui.scrollbar.OrScrollPaneUI;
import kz.tamur.comps.ui.textField.OrTextFieldUI;


/**
 * Класс реализует выпадающий список для UI.
 * 
 * @author Lebedev Sergey
 *
 */
public class OrComboBoxUI extends BasicComboBoxUI implements ShapeProvider {
    private ImageIcon expandIcon = OrComboBoxStyle.expandIcon;
    private ImageIcon collapseIcon = OrComboBoxStyle.collapseIcon;
    private int iconSpacing = OrComboBoxStyle.iconSpacing;
    private boolean drawBorder = OrComboBoxStyle.drawBorder;
    private int round = OrComboBoxStyle.round;
    private int shadeWidth = OrComboBoxStyle.shadeWidth;
    private boolean drawFocus = OrComboBoxStyle.drawFocus;
    private boolean mouseWheelScrollingEnabled = OrComboBoxStyle.mouseWheelScrollingEnabled;

    private MouseWheelListener mwl = null;
    private OrTransparentButton arrow = null;
    /** Показывать выпадающий список при получении фокуса редактором*/
    private boolean isShowInFocus = false;
    
    public static ComponentUI createUI(JComponent c) {
        return new OrComboBoxUI();
    }

    public void installUI(JComponent c) {
        super.installUI(c);

        final JComboBox comboBox = (JComboBox) c;

        // Настройки "по умолчанию"
        SwingUtils.setOrientation(comboBox);
        comboBox.setFocusable(true);
        comboBox.setOpaque(false);
        // Updating border
        updateBorder();
        // Отрисовщик "по умолчанию"
        if (!(comboBox.getRenderer() instanceof OrComboBoxCellRenderer)) {
            comboBox.setRenderer(new OrComboBoxCellRenderer(comboBox));
        }

        // Rollover scrolling listener
        mwl = new MouseWheelListener() {
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (mouseWheelScrollingEnabled && comboBox.isEnabled()) {
                    comboBox.setSelectedIndex(Math.min(Math.max(0, comboBox.getSelectedIndex() + e.getWheelRotation()), comboBox
                            .getModel().getSize() - 1));
                }
            }
        };
        comboBox.addMouseWheelListener(mwl);
    }

    public void uninstallUI(JComponent c) {
        c.removeMouseWheelListener(mwl);
        arrow = null;
        super.uninstallUI(c);
    }

    private void updateBorder() {
        if (drawBorder) {
            comboBox.setBorder(BorderFactory.createEmptyBorder(shadeWidth + 1, shadeWidth + 1, shadeWidth + 1, shadeWidth + 1));
        } else {
            comboBox.setBorder(null);
        }
    }

    protected void installComponents() {
        comboBox.setLayout(createLayoutManager());

        arrowButton = createArrowButton();
        comboBox.add(arrowButton, "1,0");
        if (arrowButton != null) {
            configureArrowButton();
        }

        if (comboBox.isEditable()) {
            addEditor();
        }

        comboBox.add(currentValuePane, "0,0");
    }

    protected ComboBoxEditor createEditor() {
        final ComboBoxEditor editor = super.createEditor();
        Component e = editor.getEditorComponent();
        e.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                comboBox.repaint();
                if(isShowInFocus) {
                    EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            comboBox.showPopup();
                        }
                    });
                }
            }

            public void focusLost(FocusEvent e) {
                comboBox.repaint();
            }
        });
        if (e instanceof JComponent) {
            ((JComponent) e).setOpaque(false);
        }
        if (e instanceof JTextField) {
            JTextField textField = (JTextField) e;
            if(textField.getUI() instanceof OrTextFieldUI) {
               ((OrTextFieldUI) textField.getUI()).setDrawBorder(false);
            }
            textField.setMargin(new Insets(1, 3, 1, 1));
        }
        return editor;
    }

    protected JButton createArrowButton() {
        arrow = new OrTransparentButton();
        arrow.setName("ComboBox.arrowButton");
        arrow.setIcon(expandIcon);
        return arrow;
    }

    public void configureArrowButton() {
        super.configureArrowButton();
        if (arrowButton != null) {
            arrowButton.setFocusable(false);
        }
    }

    protected ComboPopup createPopup() {
        return new BasicComboPopup(comboBox) {
            protected JScrollPane createScroller() {
                JScrollPane scroll = super.createScroller();
                if (OrLookAndFeel.isInstalled()) {
                    scroll.setOpaque(false);
                    scroll.getViewport().setOpaque(false);
                }

                ScrollPaneUI scrollPaneUI = scroll.getUI();
                if (scrollPaneUI instanceof OrScrollPaneUI) {
                    OrScrollPaneUI webScrollPaneUI = (OrScrollPaneUI) scrollPaneUI;
                    webScrollPaneUI.setDrawBorder(false);

                    ScrollBarUI scrollBarUI = scroll.getVerticalScrollBar().getUI();
                    if (scrollBarUI instanceof OrScrollBarUI) {
                        OrScrollBarUI webScrollBarUI = (OrScrollBarUI) scrollBarUI;
                        webScrollBarUI.setScrollBorder(webScrollPaneUI.getDarkBorder());
                    }
                }

                return scroll;
            }

            protected JList createList() {
                JList list = super.createList();
                list.setOpaque(false);
                return list;
            }

            public void show() {
                // informing listeners
                comboBox.firePopupMenuWillBecomeVisible();

                // Updating list selection
                setListSelection(comboBox.getSelectedIndex());

                // Updating popup size
                boolean cellEditor = isComboboxCellEditor();
                setupPopupSize(cellEditor);

                // Button updater
                addPopupMenuListener(new PopupMenuListener() {
                    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                        arrow.setIcon(collapseIcon);
                    }

                    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                        arrow.setIcon(expandIcon);
                    }

                    public void popupMenuCanceled(PopupMenuEvent e) {
                        arrow.setIcon(expandIcon);
                    }
                });

                // Displaying popup
                ComponentOrientation orientation = comboBox.getComponentOrientation();
                int sideShear = (drawBorder ? shadeWidth : (cellEditor ? -1 : 0));
                int topShear = (drawBorder ? shadeWidth : 0) - (cellEditor ? 0 : 1);
                show(comboBox, orientation.isLeftToRight() ? sideShear : comboBox.getWidth() - getWidth() - sideShear,
                        comboBox.getHeight() - topShear);
            }

            private void setupPopupSize(boolean cellEditor) {
                Dimension popupSize = comboBox.getSize();
                if (drawBorder) {
                    popupSize.width -= shadeWidth * 2;
                }
                if (cellEditor) {
                    popupSize.width += 2;
                }

                Insets insets = getInsets();
                popupSize.setSize(popupSize.width - (insets.right + insets.left),
                        getPopupHeightForRowCount(comboBox.getMaximumRowCount()));

                Rectangle popupBounds = computePopupBounds(0, comboBox.getBounds().height, popupSize.width, popupSize.height);
                Dimension scrollSize = popupBounds.getSize();

                scroller.setMaximumSize(scrollSize);
                scroller.setPreferredSize(scrollSize);
                scroller.setMinimumSize(scrollSize);

                list.revalidate();
            }

            private void setListSelection(int selectedIndex) {
                if (selectedIndex == -1) {
                    list.clearSelection();
                } else {
                    list.setSelectedIndex(selectedIndex);
                    list.ensureIndexIsVisible(selectedIndex);
                }
            }
        };
    }

    public boolean isComboboxCellEditor() {
        if (comboBox != null) {
            Object cellEditor = comboBox.getClientProperty(OrDefaultCellEditor.COMBOBOX_CELL_EDITOR);
            return cellEditor != null && (Boolean) cellEditor;
        } else {
            return false;
        }
    }

    public Shape provideShape() {
        if (drawBorder) {
            return LafUtils.getWebBorderShape(comboBox, shadeWidth, round);
        } else {
            return SwingUtils.size(comboBox);
        }
    }

    public ImageIcon getExpandIcon() {
        return expandIcon;
    }

    public void setExpandIcon(ImageIcon expandIcon) {
        this.expandIcon = expandIcon;
        if (arrow != null && !isPopupVisible(comboBox)) {
            arrow.setIcon(expandIcon);
        }
    }

    public ImageIcon getCollapseIcon() {
        return collapseIcon;
    }

    public void setCollapseIcon(ImageIcon collapseIcon) {
        this.collapseIcon = collapseIcon;
        if (arrow != null && isPopupVisible(comboBox)) {
            arrow.setIcon(collapseIcon);
        }
    }

    public int getIconSpacing() {
        return iconSpacing;
    }

    public void setIconSpacing(int iconSpacing) {
        this.iconSpacing = iconSpacing;
        if (arrow != null) {
// TODO            arrow.setLeftRightSpacing(iconSpacing);
        }
    }

    public boolean isDrawBorder() {
        return drawBorder;
    }

    public void setDrawBorder(boolean drawBorder) {
        this.drawBorder = drawBorder;
        updateBorder();
    }

    public boolean isDrawFocus() {
        return drawFocus;
    }

    public void setDrawFocus(boolean drawFocus) {
        this.drawFocus = drawFocus;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public int getShadeWidth() {
        return shadeWidth;
    }

    public void setShadeWidth(int shadeWidth) {
        this.shadeWidth = shadeWidth;
        updateBorder();
    }

    public boolean isMouseWheelScrollingEnabled() {
        return mouseWheelScrollingEnabled;
    }

    public void setMouseWheelScrollingEnabled(boolean enabled) {
        this.mouseWheelScrollingEnabled = enabled;
    }

    public void paint(Graphics g, JComponent c) {
        hasFocus = comboBox.hasFocus();
        Rectangle r = rectangleForCurrentValue();

        // Background
        paintCurrentValueBackground(g, r, hasFocus);

        // Selected uneditable value
        if (!comboBox.isEditable()) {
            paintCurrentValue(g, r, hasFocus);
        }
    }

    public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
        Graphics2D g2d = (Graphics2D) g;

        if (drawBorder) {
            // Border and background
            comboBox.setBackground(StyleConstants.selectedBgColor);
            LafUtils.drawWebStyle(g2d, comboBox, drawFocus && SwingUtils.hasFocusOwner(comboBox) ? StyleConstants.fieldFocusColor
                    : StyleConstants.shadeColor, shadeWidth, round, true, !isPopupVisible(comboBox));
        } else {
            // Simple background
            boolean pressed = isPopupVisible(comboBox);
            Rectangle cb = SwingUtils.size(comboBox);
            g2d.setPaint(new GradientPaint(0, shadeWidth,
                    pressed ? StyleConstants.topSelectedBgColor : StyleConstants.topBgColor, 0,
                    comboBox.getHeight() - shadeWidth, pressed ? StyleConstants.bottomSelectedBgColor
                            : StyleConstants.bottomBgColor));
            g2d.fillRect(cb.x, cb.y, cb.width, cb.height);
        }
    }

    public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
        // Selected element font color fix method

        ListCellRenderer renderer = comboBox.getRenderer();
        Component c;

        if (hasFocus && !isPopupVisible(comboBox)) {
            c = renderer.getListCellRendererComponent(listBox, comboBox.getSelectedItem(), -1, true, false);
        } else {
            c = renderer.getListCellRendererComponent(listBox, comboBox.getSelectedItem(), -1, false, false);
            c.setBackground(UIManager.getColor("ComboBox.background"));
        }
        c.setFont(comboBox.getFont());
   //     c.setBackground(UIManager.getColor("ComboBox.background"));
        // if ( hasFocus && !isPopupVisible(comboBox) ) {
        // c.setForeground(listBox.getSelectionForeground());
        // c.setBackground(listBox.getSelectionBackground());
        // }
        // else {
        if (comboBox.isEnabled()) {
            c.setForeground(comboBox.getForeground());
            c.setBackground(comboBox.getBackground());
        } else {
            c.setForeground(DefaultLookup.getColor(comboBox, this, "ComboBox.disabledForeground", null));
            c.setBackground(DefaultLookup.getColor(comboBox, this, "ComboBox.disabledBackground", null));
        }
        // }

        // Fix for 4238829: should lay out the JPanel.
        boolean shouldValidate = false;
        if (c instanceof JPanel) {
            shouldValidate = true;
        }

        int x = bounds.x;
        int y = bounds.y;
        int w = bounds.width;
        int h = bounds.height;
        // Insets padding = getInsets ();
        // if ( padding != null )
        // {
        // x = bounds.x + padding.left;
        // y = bounds.y + padding.top;
        // w = bounds.width - ( padding.left + padding.right );
        // h = bounds.height - ( padding.top + padding.bottom );
        // }

        currentValuePane.paintComponent(g, c, comboBox, x, y, w, h, shouldValidate);
    }

    /**
     * @param isShowInFocus the isShowInFocus to set
     */
    public void setShowInFocus(boolean isShowInFocus) {
        this.isShowInFocus = isShowInFocus;
    }
}
