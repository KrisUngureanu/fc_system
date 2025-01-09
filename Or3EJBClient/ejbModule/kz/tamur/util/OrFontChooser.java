package kz.tamur.util;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.Serializable;
import java.text.DecimalFormat;

import kz.tamur.comps.Constants;
//import kz.tamur.comps.Utils;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.Utils;

/**
 * Created by Vitaly A. Pronin
 * Date: 24.05.2004
 * Time: 17:20:55
 */
public class OrFontChooser extends JPanel implements Serializable, ItemListener {

    private JLabel imageLabel = new JLabel(kz.tamur.rt.Utils.getImageIconJpg("FontChooser"));
    private JTextArea prevText = new JTextArea();

    private String name = "Tahoma";
    private int size = 8;


    JCheckBox normalCheck = Utils.createCheckBox("Обычный", true);
    JCheckBox italicCheck = Utils.createCheckBox("Курсив", false);
    JCheckBox boldCheck = Utils.createCheckBox("Полужирный", false);
    JCheckBox boldItalicCheck = Utils.createCheckBox("Полужирный курсив", false);
    ButtonGroup bg = new ButtonGroup();

    private JLabel fontLabel = Utils.createLabel("Шрифт: ");
    private JLabel sizeLabel = Utils.createLabel("Размер: ");

    private JComboBox fontCombo;
    private JSlider sizeSlider = new JSlider(JSlider.HORIZONTAL, 8, 48, 8);
    private JFormattedTextField sizeViewer = new JFormattedTextField(
            new NumberFormatter(new DecimalFormat("#")));


    private String[] fontNames_;
    private Font defFont;
    private Font resultFont;
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    
    public OrFontChooser(Font selectedFont) {
        setLayout(new GridBagLayout());
        defFont = selectedFont;
        resultFont = defFont;
        init();
        setPreferredSize(new Dimension(500, 300));
        setSelectedFont();
    }



    void init() {
        normalCheck.setOpaque(isOpaque);
        italicCheck.setOpaque(isOpaque);
        boldCheck.setOpaque(isOpaque);
        boldItalicCheck.setOpaque(isOpaque);
        sizeSlider.setOpaque(isOpaque);
        setOpaque(isOpaque);
        add(imageLabel, new GridBagConstraints(0, 0, 1, 4, 0, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                Constants.INSETS_1, 0, 0));
        fontsInit();
        stylePanelInit();
    }

    void fontsInit() {
        fontNames_ =
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        fontCombo = new JComboBox(fontNames_);
        fontCombo.setFont(Utils.getDefaultFont());
        fontCombo.setBackground(Utils.getLightSysColor());
        fontCombo.setRenderer(new CellRend());
        fontCombo.addItemListener(this);
        add(fontLabel, new GridBagConstraints(1, 0, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                Constants.INSETS_1, 0, 0));
        add(fontCombo, new GridBagConstraints(2, 0, 2, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                Constants.INSETS_1, 0, 0));

        add(sizeLabel, new GridBagConstraints(1, 1, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                Constants.INSETS_1, 0, 0));

        sizeSlider.setMajorTickSpacing(4);
        sizeSlider.setMinorTickSpacing(1);
        sizeSlider.setPaintTicks(true);
        sizeSlider.setPaintLabels(true);
        sizeSlider.setPaintTicks(true);
        sizeSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                sizeViewer.setText(String.valueOf(sizeSlider.getValue()));
                size = sizeSlider.getValue();
                constructFont();
            }
        });
        sizeSlider.setFont(Utils.getDefaultFont());
        add(sizeSlider, new GridBagConstraints(2, 1, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                Constants.INSETS_1, 0, 0));
        sizeViewer.setPreferredSize(new Dimension(30, 25));
        sizeViewer.setMaximumSize(new Dimension(30, 25));
        sizeViewer.setMinimumSize(new Dimension(30, 25));
        sizeViewer.setText(String.valueOf(sizeSlider.getValue()));
        sizeViewer.setHorizontalAlignment(JTextField.CENTER);
        add(sizeViewer, new GridBagConstraints(3, 1, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                Constants.INSETS_1, 0, 0));
        sizeViewer.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if ( Character.isDigit(e.getKeyChar())) {
                    size = Integer.decode(sizeViewer.getText()).intValue();
                    sizeSlider.setValue(size);
                    constructFont();
                }
            }
        });
    }

    void stylePanelInit() {
        JPanel panel = new JPanel();
        BoxLayout bl = new BoxLayout(panel, BoxLayout.X_AXIS);
        panel.setLayout(bl);
        Border b = BorderFactory.createLineBorder(Utils.getDarkShadowSysColor());
        Border tb = Utils.createTitledBorder(b, "Стиль");
        panel.setBorder(tb);
        bg.add(normalCheck);
        bg.add(italicCheck);
        bg.add(boldCheck);
        bg.add(boldItalicCheck);
        panel.add(Box.createHorizontalGlue());
        panel.add(normalCheck);
        panel.add(Box.createHorizontalGlue());
        panel.add(italicCheck);
        panel.add(Box.createHorizontalGlue());
        panel.add(boldCheck);
        panel.add(Box.createHorizontalGlue());
        panel.add(boldItalicCheck);
        panel.add(Box.createHorizontalGlue());
        add(panel, new GridBagConstraints(1, 2, 3, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                Constants.INSETS_1, 0, 0));
        normalCheck.addItemListener(this);
        italicCheck.addItemListener(this);
        boldCheck.addItemListener(this);
        boldItalicCheck.addItemListener(this);
        prevText.setBorder(BorderFactory.createLineBorder(Utils.getDarkShadowSysColor()));
        prevText.setLineWrap(true);
        prevText.setWrapStyleWord(true);
        prevText.setText("АБВГВЕЁЖЗИ абвгдеёжзи ABCDEFGHIJ abcdefghij 0123456789 ?!<>[]{}:;%@$");
        if (defFont != null) {
            prevText.setFont(defFont);
        }
        add(prevText, new GridBagConstraints(1, 3, 3, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                Constants.INSETS_1, 0, 0));
    }


    private class CellRend extends JLabel implements ListCellRenderer {


        public CellRend() {
            setOpaque(true);
            setVerticalAlignment(CENTER);
        }

        public Component getListCellRendererComponent(
                JList list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {
            setFont(list.getFont());
            setText((String)value);
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            return this;
        }
    }

    public void itemStateChanged(ItemEvent e) {
        Object src = e.getSource();
        if (src instanceof JComboBox) {
            JComboBox cb = (JComboBox)src;
            name = cb.getSelectedItem().toString();
            constructFont();
        } else if (src instanceof JCheckBox) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                constructFont();
            }
        }
    }

    private void constructFont() {
        String fstyle = "PLAIN";
        if (normalCheck.isSelected()) {
            fstyle = "PLAIN";
        }
        if (italicCheck.isSelected()) {
            fstyle = "ITALIC";
        }
        if (boldCheck.isSelected()) {
            fstyle = "BOLD";
        }
        if (boldItalicCheck.isSelected()) {
            fstyle = "BOLDITALIC";
        }
        String fontStr = name + "-" + fstyle + "-" + size;
        resultFont = Font.decode(fontStr);
        prevText.setFont(resultFont);
    }

    public Font getChooserFont() {
        return resultFont;
    }

    private void setSelectedFont() {
        if (defFont != null) {
            switch(defFont.getStyle()) {
                case Font.ITALIC:
                    italicCheck.setSelected(true);
                    break;
                case Font.BOLD:
                    boldCheck.setSelected(true);
                    break;
                case Font.BOLD + Font.ITALIC:
                    boldItalicCheck.setSelected(true);
                    break;
            }
            sizeSlider.setValue(defFont.getSize());
            size = defFont.getSize();
            name = defFont.getFamily();
            for (int i = 0; i < fontCombo.getItemCount(); i++) {
                String s = fontCombo.getItemAt(i).toString();
                if (defFont.getFamily().equals(s)) {
                    fontCombo.setSelectedIndex(i);
                    break;
                }
            }
            constructFont();
        }
    }

    public String getFontToString() {
        if (resultFont != null) {
            String styleStr = "PLAIN";
            switch(defFont.getStyle()) {
                case Font.ITALIC:
                    styleStr = "ITALIC";
                    italicCheck.setSelected(true);
                    break;
                case Font.BOLD:
                    styleStr = "BOLD";
                    boldCheck.setSelected(true);
                    break;
                case Font.BOLD + Font.ITALIC:
                    styleStr = "BOLDITALIC";
                    boldItalicCheck.setSelected(true);
                    break;
            }
            return name + "-" + styleStr + "-" + size;
        }
        return "";
    }
}

