package kz.tamur.util.colorchooser;

import javax.swing.*;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;

import kz.tamur.comps.Constants;
import kz.tamur.rt.Utils;
import kz.tamur.rt.MainFrame;
import static java.awt.GridBagConstraints.*;

class RGBChooserPanel extends AbstractColorChooserPanel implements ChangeListener {

    private JSlider redSlider;
    private JSlider greenSlider;
    private JSlider blueSlider;
    private JTextField redField;
    private JTextField blueField;
    private JTextField greenField;
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    private OrColorChooser orColorChooser;
    private boolean isAdjusting = false;

    public RGBChooserPanel(OrColorChooser orColorChooser) {
        super();
        this.orColorChooser = orColorChooser;
    }

    public void setColor(Color newColor) {
        redSlider.setValue(newColor.getRed());
        greenSlider.setValue(newColor.getGreen());
        blueSlider.setValue(newColor.getBlue());
    }

    public String getDisplayName() {
        return UIManager.getString("ColorChooser.rgbNameText");
    }

    public Icon getSmallDisplayIcon() {
        return null;
    }

    public Icon getLargeDisplayIcon() {
        return null;
    }

    /**
     * The background color, foreground color, and font are already set to the
     * defaults from the defaults table before this method is called.
     */
    public void installChooserPanel(JColorChooser enclosingChooser) {
        super.installChooserPanel(enclosingChooser);
    }

    protected void buildChooser() {

        setLayout(new BorderLayout());
        Color color = Color.white;

        JPanel enclosure = new JPanel(new GridBagLayout());
        JPanel valuesPanel = new JPanel();
        BoxLayout bl = new BoxLayout(valuesPanel, BoxLayout.X_AXIS);
        valuesPanel.setLayout(bl);

        valuesPanel.setPreferredSize(new Dimension(10, 21));

        JLabel l = Utils.createLabel("Красный");
        l.setForeground(Utils.getDarkShadowSysColor());
        enclosure.add(l, new GridBagConstraints(0, 0, 1, 1, 0, 0, CENTER, HORIZONTAL, Constants.INSETS_1, 0, 0));
        redSlider = new RGBSlider(color.getRed());
        enclosure.add(redSlider, new GridBagConstraints(1, 0, 1, 1, 1, 0, CENTER, HORIZONTAL, Constants.INSETS_1, 0, 0));

        // The row for the green value
        l = Utils.createLabel("Зеленый");
        enclosure.add(l, new GridBagConstraints(0, 1, 1, 1, 0, 0, CENTER, HORIZONTAL, Constants.INSETS_1, 0, 0));
        greenSlider = new RGBSlider(color.getGreen());
        enclosure.add(greenSlider, new GridBagConstraints(1, 1, 1, 1, 1, 0, CENTER, HORIZONTAL, Constants.INSETS_1, 0, 0));
        
        // The slider for the blue value
        l = Utils.createLabel("Синий");
        enclosure.add(l, new GridBagConstraints(0, 2, 1, 1, 0, 0, CENTER, HORIZONTAL, Constants.INSETS_1, 0, 0));
        blueSlider = new RGBSlider(color.getBlue());
        enclosure.add(blueSlider, new GridBagConstraints(1, 2, 1, 1, 1, 0, CENTER, HORIZONTAL, Constants.INSETS_1, 0, 0));

        JLabel vLab = Utils.createLabel("Красный ");
        vLab.setForeground(Color.red);
        valuesPanel.add(Box.createHorizontalGlue());

        valuesPanel.add(vLab);
        
        Dimension fSize = new Dimension(35, 25);
        redField = new JTextField(color.getRed());
        Utils.setAllSize(redField, fSize);
        valuesPanel.add(redField);

        valuesPanel.add(Box.createHorizontalGlue());

        vLab = Utils.createLabel("Зеленый ");
        vLab.setForeground(Color.green);
        valuesPanel.add(vLab);
        greenField = new JTextField(color.getGreen());
        Utils.setAllSize(greenField, fSize);
        valuesPanel.add(greenField);

        valuesPanel.add(Box.createHorizontalGlue());

        vLab = Utils.createLabel("Синий ");
        vLab.setForeground(Color.blue);
        valuesPanel.add(vLab);
        blueField = new JTextField(color.getBlue());
        Utils.setAllSize(blueField, fSize);
        valuesPanel.add(blueField);

        valuesPanel.add(Box.createHorizontalGlue());

        enclosure.add(valuesPanel, new GridBagConstraints(0, 3, 2, 1, 1, 0, CENTER, HORIZONTAL, new Insets(3, 1, 1, 1), 0, 0));

        redSlider.addChangeListener(this);
        greenSlider.addChangeListener(this);
        blueSlider.addChangeListener(this);

        redSlider.putClientProperty("JSlider.isFilled", Boolean.TRUE);
        greenSlider.putClientProperty("JSlider.isFilled", Boolean.TRUE);
        blueSlider.putClientProperty("JSlider.isFilled", Boolean.TRUE);
        add(enclosure, BorderLayout.CENTER);

        enclosure.setOpaque(isOpaque);
        valuesPanel.setOpaque(isOpaque);
        redSlider.setOpaque(isOpaque);
        greenSlider.setOpaque(isOpaque);
        blueSlider.setOpaque(isOpaque);
        setOpaque(isOpaque);
        
        
        redField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // если в поле число
                String text = redField.getText();
                if (text.replaceAll("\\d", "").isEmpty()) {
                    int value = Integer.parseInt(text);
                    if (value > -1 && value < 256) {
                        redSlider.setValue(value);
                        orColorChooser.setCurrentColor(new Color(redSlider.getValue(), greenSlider.getValue(), blueSlider.getValue()));
                    }
                }

            }
        });
        
        greenField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // если в поле число
                String text = greenField.getText();
                if (text.replaceAll("\\d", "").isEmpty()) {
                    int value = Integer.parseInt(text);
                    if (value > -1 && value < 256) {
                        greenSlider.setValue(value);
                        orColorChooser.setCurrentColor(new Color(redSlider.getValue(), greenSlider.getValue(), blueSlider.getValue()));
                    }
                }

            }
        });
        
        blueField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // если в поле число
                String text = blueField.getText();
                if (text.replaceAll("\\d", "").isEmpty()) {
                    int value = Integer.parseInt(text);
                    if (value > -1 && value < 256) {
                        blueSlider.setValue(value);
                        orColorChooser.setCurrentColor(new Color(redSlider.getValue(), greenSlider.getValue(), blueSlider.getValue()));
                    }
                }

            }
        });
    }

    public void uninstallChooserPanel(JColorChooser enclosingChooser) {
        super.uninstallChooserPanel(enclosingChooser);
    }

    public void updateChooser() {
        if (isAdjusting) {
            return;
        }
        isAdjusting = true;

        Color color = orColorChooser.getCurrentColor();
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();

        redSlider.setValue(red);
        blueSlider.setValue(blue);
        greenSlider.setValue(green);

        redField.setText(String.valueOf(red));
        greenField.setText(String.valueOf(green));
        blueField.setText(String.valueOf(blue));

        isAdjusting = false;
    }

    public void stateChanged(ChangeEvent e) {
        if (e.getSource() instanceof JSlider) {
            int red = redSlider.getValue();
            int green = greenSlider.getValue();
            int blue = blueSlider.getValue();
            redField.setText(String.valueOf(red));
            greenField.setText(String.valueOf(green));
            blueField.setText(String.valueOf(blue));
            Color color = new Color(red, green, blue);
            orColorChooser.setCurrentColor(color);
        }
    }

    class NumberListener implements DocumentListener, Serializable {
        public void insertUpdate(DocumentEvent e) {
            updatePanel(e);
        }

        public void removeUpdate(DocumentEvent e) {
            updatePanel(e);
        }

        public void changedUpdate(DocumentEvent e) {
        }

        private void updatePanel(DocumentEvent e) {
            String rStr = redField.getText();
            String gStr = greenField.getText();
            String bStr = blueField.getText();
            int red = (rStr != null) ? new Integer(rStr).intValue() : 0;
            int green = (gStr != null) ? new Integer(gStr).intValue() : 0;
            int blue = (bStr != null) ? new Integer(bStr).intValue() : 0;
            Color color = new Color(red, green, blue);
            orColorChooser.setCurrentColor(color);
        }
    }
    
/**
 * Класс для удобной инициализации свойств компонента <code>JSlider</code>
 * @author Sergey Lebedev
 *
 */
    class RGBSlider extends JSlider{
        
        /**
         * Создание нового экземпляра класса.
         *
         * @param initValue значение "по умолчанию"
         */
        RGBSlider(int initValue){
            super(JSlider.HORIZONTAL, 0, 255, initValue);
            setMajorTickSpacing(85);
            setMinorTickSpacing(17);
            setPaintTicks(true);
            setPaintLabels(true);
        }
    }
}
