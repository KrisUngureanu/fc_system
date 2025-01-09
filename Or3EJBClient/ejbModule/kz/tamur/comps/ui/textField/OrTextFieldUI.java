package kz.tamur.comps.ui.textField;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextFieldUI;
import javax.swing.text.JTextComponent;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import kz.tamur.comps.ui.OrLookAndFeel;
import kz.tamur.comps.ui.ext.Painter;
import kz.tamur.comps.ui.ext.ShapeProvider;
import kz.tamur.comps.ui.ext.StyleConstants;
import kz.tamur.comps.ui.ext.utils.LafUtils;
import kz.tamur.comps.ui.ext.utils.SwingUtils;

/**
 * Класс реализует текстовое поле для UI.
 * 
 * @author Lebedev Sergey
 *
 */
public class OrTextFieldUI extends BasicTextFieldUI implements ShapeProvider, SwingConstants {
    private JTextField textField = null;

    private boolean drawBorder = OrTextFieldStyle.drawBorder;
    private boolean drawFocus = OrTextFieldStyle.drawFocus;
    private int round = OrTextFieldStyle.round;
    private int shadeWidth = OrTextFieldStyle.shadeWidth;
    private boolean drawBackground = OrTextFieldStyle.drawBackground;
    private boolean webColored = OrTextFieldStyle.webColored;
    private Painter painter = OrTextFieldStyle.painter;
    private Insets fieldMargin = OrTextFieldStyle.fieldMargin;
    private String inputPrompt = OrTextFieldStyle.inputPrompt;
    private Font inputPromptFont = OrTextFieldStyle.inputPromptFont;
    private Color inputPromptForeground = OrTextFieldStyle.inputPromptForeground;
    private int inputPromptPosition = OrTextFieldStyle.inputPromptPosition;
    private boolean hideInputPromptOnFocus = OrTextFieldStyle.hideInputPromptOnFocus;

    private JComponent leadingComponent = null;
    private JComponent trailingComponent = null;

    private FocusListener focusListener;
    private PropertyChangeListener accessibleChangeListener;
    private PropertyChangeListener orientationChangeListener;
    private PropertyChangeListener marginChangeListener;
    private ComponentAdapter componentResizeListener;

    public static ComponentUI createUI(JComponent c) {
        return new OrTextFieldUI((JTextField) c);
    }

    public OrTextFieldUI(final JTextField textField) {
        this(textField, true);
    }

    public OrTextFieldUI(final JTextField textField, boolean drawBorder) {
        super();
        this.textField = textField;
        this.drawBorder = drawBorder;
    }

    public void installUI(JComponent c) {
        super.installUI(c);

        // Настройки по умолчанию
        SwingUtils.setOrientation(textField);
        textField.putClientProperty(SwingUtils.HANDLES_ENABLE_STATE, true);
        textField.setFocusable(true);
        textField.setOpaque(false);
        textField.setMargin(OrTextFieldStyle.margin);
        textField.setBackground(Color.WHITE);
        textField.setSelectionColor(StyleConstants.textSelectionColor);
        textField.setForeground(Color.BLACK);
        textField.setSelectedTextColor(Color.BLACK);
        textField.setCaretColor(Color.GRAY);
        textField.setLayout(new TextComponentLayout(textField));

        // Обновление рамки
        updateBorder();

        focusListener = new FocusListener() {
            public void focusLost(FocusEvent e) {
                textField.repaint();
            }

            public void focusGained(FocusEvent e) {
                textField.repaint();
            }
        };
        textField.addFocusListener(focusListener);

        accessibleChangeListener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                updateInnerComponents();
            }
        };
        textField.addPropertyChangeListener(OrLookAndFeel.COMPONENT_ENABLED_PROPERTY, accessibleChangeListener);

        orientationChangeListener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                updateBorder();
            }
        };
        textField.addPropertyChangeListener(OrLookAndFeel.COMPONENT_ORIENTATION_PROPERTY, orientationChangeListener);

        marginChangeListener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                updateBorder();
            }
        };
        textField.addPropertyChangeListener(OrLookAndFeel.COMPONENT_MARGIN_PROPERTY, marginChangeListener);

        componentResizeListener = new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                updateBorder();
            }
        };
    }

    public void uninstallUI(JComponent c) {
        textField.putClientProperty(SwingUtils.HANDLES_ENABLE_STATE, null);

        textField.removeFocusListener(focusListener);
        textField.removePropertyChangeListener(OrLookAndFeel.COMPONENT_ENABLED_PROPERTY, accessibleChangeListener);
        textField.removePropertyChangeListener(OrLookAndFeel.COMPONENT_ORIENTATION_PROPERTY, orientationChangeListener);
        textField.removePropertyChangeListener(OrLookAndFeel.COMPONENT_MARGIN_PROPERTY, marginChangeListener);

        cleanupLeadingComponent();
        cleanupTrailingComponent();
        textField.setLayout(null);

        super.uninstallUI(c);
    }

    public Shape provideShape() {
        if (drawBorder) {
            return LafUtils.getWebBorderShape(textField, shadeWidth, round);
        } else {
            return SwingUtils.size(textField);
        }
    }

    private void updateInnerComponents() {
        if (leadingComponent != null) {
            leadingComponent.setEnabled(textField.isEnabled());
        }
        if (trailingComponent != null) {
            trailingComponent.setEnabled(textField.isEnabled());
        }
    }

    public JComponent getLeadingComponent() {
        return leadingComponent;
    }

    public void setLeadingComponent(JComponent leadingComponent) {
        // Removing old leading component
        cleanupLeadingComponent();

        // New leading component
        if (leadingComponent != null) {
            this.leadingComponent = leadingComponent;

            // Registering resize listener
            this.leadingComponent.addComponentListener(componentResizeListener);

            // Adding component
            textField.add(leadingComponent, TextComponentLayout.LEADING);
            updateBorder();

            // Updating components state
            updateInnerComponents();
        }
    }

    private void cleanupLeadingComponent() {
        if (this.leadingComponent != null) {
            this.leadingComponent.removeComponentListener(componentResizeListener);
            textField.remove(this.leadingComponent);
            this.leadingComponent = null;
        }
    }

    public JComponent getTrailingComponent() {
        return trailingComponent;
    }

    public void setTrailingComponent(JComponent trailingComponent) {
        // Removing old trailing component
        cleanupTrailingComponent();

        // New trailing component
        if (trailingComponent != null) {
            this.trailingComponent = trailingComponent;

            // Registering resize listener
            this.trailingComponent.addComponentListener(componentResizeListener);

            // Adding component
            textField.add(trailingComponent, TextComponentLayout.TRAILING);
            updateBorder();

            // Updating components state
            updateInnerComponents();
        }
    }

    private void cleanupTrailingComponent() {
        if (this.trailingComponent != null) {
            this.trailingComponent.removeComponentListener(componentResizeListener);
            textField.remove(this.trailingComponent);
            this.trailingComponent = null;
        }
    }

    public void setFieldMargin(Insets margin) {
        this.fieldMargin = margin;
        updateBorder();
    }

    public Insets getFieldMargin() {
        return fieldMargin;
    }

    public String getInputPrompt() {
        return inputPrompt;
    }

    public void setInputPrompt(String inputPrompt) {
        this.inputPrompt = inputPrompt;
        updateView();
    }

    public Font getInputPromptFont() {
        return inputPromptFont;
    }

    public void setInputPromptFont(Font inputPromptFont) {
        this.inputPromptFont = inputPromptFont;
        updateView();
    }

    public Color getInputPromptForeground() {
        return inputPromptForeground;
    }

    public void setInputPromptForeground(Color inputPromptForeground) {
        this.inputPromptForeground = inputPromptForeground;
        updateView();
    }

    public int getInputPromptPosition() {
        return inputPromptPosition;
    }

    public void setInputPromptPosition(int inputPromptPosition) {
        this.inputPromptPosition = inputPromptPosition;
        updateView();
    }

    public boolean isHideInputPromptOnFocus() {
        return hideInputPromptOnFocus;
    }

    public void setHideInputPromptOnFocus(boolean hideInputPromptOnFocus) {
        this.hideInputPromptOnFocus = hideInputPromptOnFocus;
        updateView();
    }

    public int getShadeWidth() {
        return shadeWidth;
    }

    public void setShadeWidth(int shadeWidth) {
        this.shadeWidth = shadeWidth;
        updateBorder();
    }

    public boolean isDrawBackground() {
        return drawBackground;
    }

    public void setDrawBackground(boolean drawBackground) {
        this.drawBackground = drawBackground;
    }

    public boolean isWebColored() {
        return webColored;
    }

    public void setWebColored(boolean webColored) {
        this.webColored = webColored;
        updateView();
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
        updateView();
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
        updateView();
    }

    public Painter getPainter() {
        return painter;
    }

    public void setPainter(Painter painter) {
        this.painter = painter;
        getComponent().setOpaque(painter == null || painter.isOpaque(textField));
        updateBorder();
    }

    private void updateView() {
        if (textField != null) {
            textField.repaint();
        }
    }

    private void updateBorder() {
        if (textField != null) {
            // Стиль рамки
            Insets b;
            if (painter != null) {
                b = painter.getMargin(getComponent());
            } else if (drawBorder) {
                b = new Insets(shadeWidth + 1, shadeWidth + 1, shadeWidth + 1, shadeWidth + 1);
            } else {
                b = new Insets(0, 0, 0, 0);
            }
            
            // Taking margins into account
            Insets margin = textField.getMargin();
            if (margin != null) {
                b.top += margin.top;
                b.left += margin.left;
                b.bottom += margin.bottom;
                b.right += margin.right;
            }
            if (fieldMargin != null) {
                b.top += fieldMargin.top;
                b.left += fieldMargin.left;
                b.bottom += fieldMargin.bottom;
                b.right += fieldMargin.right;
            }

            // Adding component sizes into border
            if (leadingComponent != null) {
                b.left += leadingComponent.getPreferredSize().width;
            }
            if (trailingComponent != null) {
                b.right += trailingComponent.getPreferredSize().width;
            }

            // Final border
            boolean ltr = textField.getComponentOrientation().isLeftToRight();
            textField.setBorder(BorderFactory.createEmptyBorder(b.top, ltr ? b.left : b.right, b.bottom, ltr ? b.right : b.left));
        }
    }

    protected void paintSafely(Graphics g) {
        JTextComponent c = getComponent();
        Graphics2D g2d = (Graphics2D) g;

        if (c.isOpaque() && (painter == null || !painter.isOpaque(textField))) {
            // Paint default background
            g.setColor(c.getBackground());
            g.fillRect(0, 0, c.getWidth(), c.getHeight());
        }

        if (painter != null || drawBorder) {
            Object aa = LafUtils.setupAntialias(g2d);

            if (painter != null) {
                // Use background painter instead of default UI graphics
                painter.paint(g2d, SwingUtils.size(c), c);
            } else if (drawBorder) {
                // Border, background and shade
                LafUtils.drawWebStyle(g2d, c, drawFocus && c.isFocusOwner() ? StyleConstants.fieldFocusColor
                        : StyleConstants.shadeColor, shadeWidth, round, drawBackground, webColored);
            }
            LafUtils.restoreAntialias(g2d, aa);
        }

        super.paintSafely(g);

        if (inputPrompt != null && c.isEditable() && c.isEnabled() && (!hideInputPromptOnFocus || !c.isFocusOwner())
                && c.getText().equals("")) {
            boolean ltr = c.getComponentOrientation().isLeftToRight();
            Rectangle b = getVisibleEditorRect();
            Shape oc = LafUtils.intersectClip(g2d, b);
            g2d.setFont(inputPromptFont != null ? inputPromptFont : c.getFont());
            g2d.setPaint(inputPromptForeground != null ? inputPromptForeground : c.getForeground());

            FontMetrics fm = g2d.getFontMetrics();
            int x;
            if (inputPromptPosition == CENTER) {
                x = b.x + b.width / 2 - fm.stringWidth(inputPrompt) / 2;
            } else if (ltr && inputPromptPosition == LEADING || !ltr && inputPromptPosition == TRAILING) {
                x = b.x;
            } else {
                x = b.x + b.width - fm.stringWidth(inputPrompt);
            }
            g2d.drawString(inputPrompt, x, b.y + b.height / 2 + (fm.getAscent() - fm.getDescent()) / 2);

            g2d.setClip(oc);
        }
    }

    protected void paintBackground(Graphics g) {
        //
    }

    public Dimension getPreferredSize(JComponent c) {
        Dimension ps = super.getPreferredSize(c);

        // Fix for Swing bug with pointless scrolling when field's default preferred size is already reached
        ps.width += 1;

        // Height might be changed due to inner components
        if (leadingComponent != null || trailingComponent != null) {
            Dimension lps = c.getLayout().preferredLayoutSize(c);
            ps.height = Math.max(ps.height, lps.height);
        }

        // Background painter preferred size
        if (painter != null) {
            ps = SwingUtils.max(ps, painter.getPreferredSize(c));
        }

        return ps;
    }
}
