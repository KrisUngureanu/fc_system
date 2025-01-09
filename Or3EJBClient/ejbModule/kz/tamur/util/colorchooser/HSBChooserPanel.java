package kz.tamur.util.colorchooser;

import javax.swing.*;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.awt.image.*;
import kz.tamur.comps.Constants;
import kz.tamur.rt.Utils;
import kz.tamur.rt.MainFrame;
import static java.awt.GridBagConstraints.*;

class HSBChooserPanel extends AbstractColorChooserPanel {

    private AbstractHSBImage palette;
    private AbstractHSBImage sliderPalette;
    private JSlider slider;
    private JTextField hField;
    private JTextField sField;
    private JTextField bField;
    private JTextField redField;
    private JTextField greenField;
    private JTextField blueField;
    private Point paletteSelection = new Point();
    private JLabel paletteLabel;
    private JLabel sliderPaletteLabel;
    private JCheckBox hRadio;
    private JCheckBox sRadio;
    private JCheckBox bRadio;
    private Image paletteImage;
    private Image sliderImage;

    private float[] hsb = new float[3];
    private boolean isAdjusting = false;
    private boolean isUpdatingOften = false;
    private static final int PALETTE_DIMENSION = 200;
    private static final int MAX_HUE_VALUE = 359;
    private static final int MAX_SATURATION_VALUE = 99;
    private static final int MAX_BRIGHTNESS_VALUE = 99;
    private static final int HUE_SLIDER = 0;
    private static final int SATURATION_SLIDER = 1;
    private static final int BRIGHTNESS_SLIDER = 2;
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    private OrColorChooser orColorChooser;
    private int sliderType = HUE_SLIDER;

    public HSBChooserPanel(OrColorChooser orColorChooser) {
        super();
        setOpaque(isOpaque);
        this.orColorChooser = orColorChooser;
    }

    protected void repaintPaletteSelection() {
        paletteLabel.repaint();
    }

    protected void addPaletteListeners() {
        paletteLabel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                palette.getHSBForLocation(e.getX(), e.getY(), hsb);
                updateHSB(hsb[0], hsb[1], hsb[2]);
            }
        });

        paletteLabel.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                int labelWidth = paletteLabel.getWidth();

                int labelHeight = paletteLabel.getHeight();
                int x = e.getX();
                int y = e.getY();

                if (x >= labelWidth) {
                    x = labelWidth - 1;
                }

                if (y >= labelHeight) {
                    y = labelHeight - 1;
                }

                if (x < 0) {
                    x = 0;
                }

                if (y < 0) {
                    y = 0;
                }
                palette.getHSBForLocation(x, y, hsb);
                updateHSB(hsb[0], hsb[1], hsb[2]);
            }
        });
    }

    protected void addSliderListeners() {
        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                boolean modelIsAdjusting = slider.getModel().getValueIsAdjusting();
                if ((modelIsAdjusting && isUpdatingOften) || !modelIsAdjusting) {

                    int sliderValue = slider.getValue();
                    int sliderRange = slider.getMaximum() + 1;
                    float value = (float) sliderValue / (float) sliderRange;

                    int x = paletteSelection.x;
                    int y = paletteSelection.y;
                    palette.getHSBForLocation(x, y, hsb);

                    switch (sliderType) {
                    case HUE_SLIDER:
                        updateHSB(value, hsb[1], hsb[2]);
                        break;
                    case SATURATION_SLIDER:
                        updateHSB(hsb[0], value, hsb[2]);
                        break;
                    case BRIGHTNESS_SLIDER:
                        updateHSB(hsb[0], hsb[1], value);
                        break;
                    }
                }
            }
        });
    }

    protected void updatePalette(float h, float s, float b) {
        int x = 0;
        int y = 0;

        switch (sliderType) {
        case HUE_SLIDER:
            if (h != palette.getHue()) {
                palette.setHue(h);
                palette.nextFrame(0);
            }
            x = PALETTE_DIMENSION - (int) (s * PALETTE_DIMENSION);
            y = PALETTE_DIMENSION - (int) (b * PALETTE_DIMENSION);
            break;
        case SATURATION_SLIDER:
            if (s != palette.getSaturation()) {
                palette.setSaturation(s);
                palette.nextFrame(0);
            }
            x = (int) (h * PALETTE_DIMENSION);
            y = PALETTE_DIMENSION - (int) (b * PALETTE_DIMENSION);
            break;
        case BRIGHTNESS_SLIDER:
            if (b != palette.getBrightness()) {
                palette.setBrightness(b);
                palette.nextFrame(0);
            }
            x = (int) (h * PALETTE_DIMENSION);
            y = PALETTE_DIMENSION - (int) (s * PALETTE_DIMENSION);
            break;
        }

        paletteSelection.setLocation(x, y);
        repaintPaletteSelection();
    }

    protected void updateSliderPalette(float h, float s, float b) {
        if (sliderType != HUE_SLIDER && h != sliderPalette.getHue()) {
            sliderPalette.setHue(h);
            sliderPalette.nextFrame(0);
        }
    }

    protected void updateSlider(float h, float s, float b) {
        float value = 0f;

        switch (sliderType) {
        case HUE_SLIDER:
            value = h;
            break;
        case SATURATION_SLIDER:
            value = s;
            break;
        case BRIGHTNESS_SLIDER:
            value = b;
            break;
        }

        slider.setValue(Math.round(value * (slider.getMaximum() + 1)));
    }

    protected void updateHSBTextFields(float hue, float saturation, float brightness) {
        int h = Math.round(hue * 359);
        int s = Math.round(saturation * 100);
        int b = Math.round(brightness * 100);
        hField.setText(String.valueOf(h));
        sField.setText(String.valueOf(s));
        bField.setText(String.valueOf(b));
    }

    protected void updateRGBTextFields(Color color) {
        redField.setText(String.valueOf(color.getRed()));
        greenField.setText(String.valueOf(color.getGreen()));
        blueField.setText(String.valueOf(color.getBlue()));
    }

    protected void updateHSB(float h, float s, float b) {
        if (!isAdjusting) {
            isAdjusting = true;

            updatePalette(h, s, b);
            updateSliderPalette(h, s, b);
            updateSlider(h, s, b);
            updateHSBTextFields(h, s, b);

            Color color = new Color(palette.getRGBForLocation(paletteSelection.x, paletteSelection.y));

            updateRGBTextFields(color);
            orColorChooser.setCurrentColor(color);
            isAdjusting = false;
        }
    }

    public void updateChooser() {
        if (!isAdjusting) {
            Color color = orColorChooser.getCurrentColor();
            Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
            updateHSB(hsb[0], hsb[1], hsb[2]);
        }
    }

    protected void buildChooser() {
        Color color = orColorChooser.getCurrentColor();
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);

        setLayout(new BorderLayout());
        JComponent spp = buildSliderPalettePanel();
        add(spp, BorderLayout.WEST);

        JPanel controlHolder = new JPanel(new GridLayout(1, 3));
        JComponent hsbControls = buildHSBControls();
        controlHolder.add(hsbControls);

        JComponent rgbControls = buildRGBControls();
        controlHolder.add(rgbControls);
        controlHolder.setOpaque(isOpaque);
        add(controlHolder, BorderLayout.CENTER);
    }

    protected JComponent buildRGBControls() {

        JPanel panel = new JPanel(new GridBagLayout());

        Color color = orColorChooser.getCurrentColor();
        redField = new JTextField(String.valueOf(color.getRed()));
        redField.setEditable(false);
        redField.setPreferredSize(new Dimension(30, 25));

        greenField = new JTextField(String.valueOf(color.getGreen()));
        greenField.setEditable(false);
        greenField.setPreferredSize(new Dimension(30, 25));

        blueField = new JTextField(String.valueOf(color.getBlue()));
        blueField.setEditable(false);
        blueField.setPreferredSize(new Dimension(30, 25));

        JLabel redLab = Utils.createLabel("R");
        redLab.setHorizontalAlignment(JLabel.RIGHT);
        JLabel greenLab = Utils.createLabel("G");
        greenLab.setHorizontalAlignment(JLabel.RIGHT);
        JLabel blueLab = Utils.createLabel("B");
        blueLab.setHorizontalAlignment(JLabel.RIGHT);
        panel.setOpaque(isOpaque);

        panel.add(redLab, new GridBagConstraints(0, 0, 1, 1, 1, 1, CENTER, HORIZONTAL, Constants.INSETS_1, 0, 0));
        panel.add(redField, new GridBagConstraints(1, 0, 1, 1, 1, 1, CENTER, HORIZONTAL, Constants.INSETS_1, 0, 0));
        panel.add(greenLab, new GridBagConstraints(0, 1, 1, 1, 1, 1, CENTER, HORIZONTAL, Constants.INSETS_1, 0, 0));
        panel.add(greenField, new GridBagConstraints(1, 1, 1, 1, 1, 1, CENTER, HORIZONTAL, Constants.INSETS_1, 0, 0));
        panel.add(blueLab, new GridBagConstraints(0, 2, 1, 1, 1, 1, CENTER, HORIZONTAL, Constants.INSETS_1, 0, 0));
        panel.add(blueField, new GridBagConstraints(1, 2, 1, 1, 1, 1, CENTER, HORIZONTAL, Constants.INSETS_1, 0, 0));

        return panel;
    }

    protected JComponent buildHSBControls() {

        String hueString = "H";
        String saturationString = "S";
        String brightnessString = "B";

        JPanel panel = new JPanel(new GridBagLayout());

        hField = new JTextField(new Integer((int) hsb[0] * 359).toString());
        sField = new JTextField(new Integer((int) (hsb[1] * 100)).toString());
        bField = new JTextField(new Integer((int) (hsb[2] * 100)).toString());

        hField.setPreferredSize(new Dimension(30, 25));
        sField.setPreferredSize(new Dimension(30, 25));
        bField.setPreferredSize(new Dimension(30, 25));

        hField.setEditable(false);
        sField.setEditable(false);
        bField.setEditable(false);

        ButtonGroup group = new ButtonGroup();
        hRadio = new JCheckBox(hueString);
        hRadio.setFont(Utils.getDefaultFont());
        group.add(hRadio);
        hRadio.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setHueMode();
            }
        });

        sRadio = new JCheckBox(saturationString);
        sRadio.setFont(Utils.getDefaultFont());
        group.add(sRadio);
        sRadio.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setSaturationMode();
            }
        });

        bRadio = new JCheckBox(brightnessString);
        group.add(bRadio);
        bRadio.setFont(Utils.getDefaultFont());
        bRadio.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setBrightnessMode();
            }
        });

        hRadio.setSelected(true);

        panel.setOpaque(isOpaque);
        hRadio.setOpaque(isOpaque);
        sRadio.setOpaque(isOpaque);
        bRadio.setOpaque(isOpaque);

        panel.add(hRadio, new GridBagConstraints(0, 0, 1, 1, 1, 1, CENTER, HORIZONTAL, Constants.INSETS_1, 0, 0));
        panel.add(hField, new GridBagConstraints(1, 0, 1, 1, 1, 1, CENTER, HORIZONTAL, Constants.INSETS_1, 0, 0));
        panel.add(sRadio, new GridBagConstraints(0, 1, 1, 1, 1, 1, CENTER, HORIZONTAL, Constants.INSETS_1, 0, 0));
        panel.add(sField, new GridBagConstraints(1, 1, 1, 1, 1, 1, CENTER, HORIZONTAL, Constants.INSETS_1, 0, 0));
        panel.add(bRadio, new GridBagConstraints(0, 2, 1, 1, 1, 1, CENTER, HORIZONTAL, Constants.INSETS_1, 0, 0));
        panel.add(bField, new GridBagConstraints(1, 2, 1, 1, 1, 1, CENTER, HORIZONTAL, Constants.INSETS_1, 0, 0));

        return panel;

    }

    protected void setHueMode() {
        sliderType = HUE_SLIDER;
        slider.setInverted(true);
        slider.setMaximum(MAX_HUE_VALUE);
        palette = new SaturationBrightnessImage(PALETTE_DIMENSION, PALETTE_DIMENSION, hsb[0]);
        sliderPalette = new HueImage(16, PALETTE_DIMENSION);

        paletteImage.flush();
        sliderImage.flush();
        paletteImage = Toolkit.getDefaultToolkit().createImage(palette);
        sliderImage = Toolkit.getDefaultToolkit().createImage(sliderPalette);
        paletteLabel.setIcon(new ImageIcon(paletteImage));
        sliderPaletteLabel.setIcon(new ImageIcon(sliderImage));

        updateHSB(hsb[0], hsb[1], hsb[2]);

        repaint();

    }

    protected void setSaturationMode() {
        sliderType = SATURATION_SLIDER;
        slider.setInverted(false);
        slider.setMaximum(MAX_SATURATION_VALUE);
        palette = new HueBrightnessImage(PALETTE_DIMENSION, PALETTE_DIMENSION, hsb[0], hsb[1]);
        sliderPalette = new SaturationImage(16, PALETTE_DIMENSION, hsb[0]);

        paletteImage.flush();
        sliderImage.flush();
        paletteImage = Toolkit.getDefaultToolkit().createImage(palette);
        sliderImage = Toolkit.getDefaultToolkit().createImage(sliderPalette);
        paletteLabel.setIcon(new ImageIcon(paletteImage));
        sliderPaletteLabel.setIcon(new ImageIcon(sliderImage));

        updateHSB(hsb[0], hsb[1], hsb[2]);

        repaint();

    }

    protected void setBrightnessMode() {
        sliderType = BRIGHTNESS_SLIDER;
        slider.setInverted(false);
        slider.setMaximum(MAX_BRIGHTNESS_VALUE);
        palette = new HueSaturationImage(PALETTE_DIMENSION, PALETTE_DIMENSION, hsb[0], hsb[2]);
        sliderPalette = new BrightnessImage(16, PALETTE_DIMENSION, hsb[0]);

        paletteImage.flush();
        sliderImage.flush();
        paletteImage = Toolkit.getDefaultToolkit().createImage(palette);
        sliderImage = Toolkit.getDefaultToolkit().createImage(sliderPalette);
        paletteLabel.setIcon(new ImageIcon(paletteImage));
        sliderPaletteLabel.setIcon(new ImageIcon(sliderImage));

        updateHSB(hsb[0], hsb[1], hsb[2]);

        repaint();

    }

    protected JComponent buildSliderPalettePanel() {

        JPanel panel = new JPanel();
        palette = new SaturationBrightnessImage(PALETTE_DIMENSION, PALETTE_DIMENSION, hsb[0]);
        sliderPalette = new HueImage(16, PALETTE_DIMENSION);

        // This slider has to have a minimum of 0. A lot of math in this file is simplified due to this.
        slider = new JSlider(JSlider.VERTICAL, 0, MAX_HUE_VALUE, 0);
        slider.setPaintTrack(false);
        Dimension prefSize = slider.getPreferredSize();
        slider.setPreferredSize(new Dimension(prefSize.width, PALETTE_DIMENSION + 15));
        slider.setInverted(true);
        addSliderListeners();

        paletteLabel = createPaletteLabel();
        paletteImage = Toolkit.getDefaultToolkit().createImage(palette);
        paletteLabel.setIcon(new ImageIcon(paletteImage));
        addPaletteListeners();

        panel.add(paletteLabel);
        panel.add(slider);

        sliderPaletteLabel = new JLabel();
        sliderImage = Toolkit.getDefaultToolkit().createImage(sliderPalette);
        sliderPaletteLabel.setIcon(new ImageIcon(sliderImage));

        panel.add(sliderPaletteLabel);
        slider.setOpaque(isOpaque);
        panel.setOpaque(isOpaque);
        return panel;
    }

    protected JLabel createPaletteLabel() {
        return new JLabel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.white);
                g.drawOval(paletteSelection.x - 4, paletteSelection.y - 4, 8, 8);
            }
        };
    }

    public String getDisplayName() {
        return UIManager.getString("ColorChooser.hsbNameText");
    }

    public Icon getSmallDisplayIcon() {
        return null;
    }

    public Icon getLargeDisplayIcon() {
        return null;
    }

    class NumberListener implements DocumentListener {
        public void insertUpdate(DocumentEvent e) {
            updatePanel(e);
        }

        public void removeUpdate(DocumentEvent e) {
            updatePanel(e);
        }

        public void changedUpdate(DocumentEvent e) {
        }

        private void updatePanel(DocumentEvent e) {

            String hStr = hField.getText();
            String sStr = sField.getText();
            String bStr = bField.getText();

            int hF = (hStr != null) ? (new Integer(hStr)).intValue() : 0;
            int sF = (sStr != null) ? (new Integer(sStr)).intValue() : 0;
            int bF = (bStr != null) ? (new Integer(bStr)).intValue() : 0;

            float hue = (float) hF / 359f;
            float saturation = (float) sF / 100f;
            float brightness = (float) bF / 100f;

            updateHSB(hue, saturation, brightness);
        }
    }

    abstract class AbstractHSBImage extends SyntheticImage {
        protected float h = .0f;
        protected float s = .0f;
        protected float b = .0f;
        protected float[] hsb = new float[3];
        protected boolean isDirty = true;

        protected AbstractHSBImage(int width, int height, float h, float s, float b) {
            super(width, height);
            setHSB(h, s, b);
        }

        public void setHSB(float h, float s, float b) {
            setHue(h);
            setSaturation(s);
            setBrightness(b);
        }

        public final void setHue(float hue) {
            h = hue;
        }

        public final void setSaturation(float saturation) {
            s = saturation;
        }

        public final void setBrightness(float brightness) {
            b = brightness;
        }

        public final float getHue() {
            return h;
        }

        public final float getSaturation() {
            return s;
        }

        public final float getBrightness() {
            return b;
        }

        protected boolean isStatic() {
            return false;
        }

        public synchronized void nextFrame(int param) {
            isDirty = true;
            notifyAll();
        }

        public int getRGBForMouseEvent(MouseEvent e) {
            return getRGBForLocation(e.getX(), e.getY());
        }

        public int getRGBForLocation(int x, int y) {
            getHSBForLocation(x, y, hsb);
            return HSBtoRGB(hsb[0], hsb[1], hsb[2]);
        }

        public abstract void getHSBForLocation(int x, int y, float[] hsbArray);

        public synchronized void addConsumer(ImageConsumer ic) {
            isDirty = true;
            super.addConsumer(ic);
        }

        protected void computeRow(int y, int[] row) {
            if (y == 0) {
                synchronized (this) {
                    try {
                        while (!isDirty) {
                            wait();
                        }
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                    isDirty = false;
                }
            }
            for (int i = 0; i < row.length; ++i) {
                row[i] = getRGBForLocation(i, y);
            }

        }

        // An optimized version of Color.HSBtoRGB
        protected int HSBtoRGB(float hue, float saturation, float brightness) {
            int r = 0, g = 0, b = 0;
            if (saturation == 0) {
                r = g = b = (int) (brightness * 255);
            } else {
                double scaledBrightness = brightness * 255.0;
                int iScaledBrightness = (int) scaledBrightness;
                double h = hue >= 0 ? (hue - (int) hue) : (hue - Math.floor(hue));
                h *= 6.0;
                int ih = (int) h; // floor not necessary because h>=0
                double f = h - ih;
                int p = (int) (scaledBrightness * (1.0 - saturation));
                int q = (int) (scaledBrightness * (1.0 - saturation * f));
                int t = (int) (scaledBrightness * (1.0 - (saturation * (1.0 - f))));
                switch (ih) {
                case 0:
                    r = iScaledBrightness;
                    g = t;
                    b = p;
                    break;
                case 1:
                    r = q;
                    g = iScaledBrightness;
                    b = p;
                    break;
                case 2:
                    r = p;
                    g = iScaledBrightness;
                    b = t;
                    break;
                case 3:
                    r = p;
                    g = q;
                    b = iScaledBrightness;
                    break;
                case 4:
                    r = t;
                    g = p;
                    b = iScaledBrightness;
                    break;
                case 5:
                    r = iScaledBrightness;
                    g = p;
                    b = q;
                    break;
                }
            }
            return 0xff000000 | (r << 16) | (g << 8) | (b << 0);
        }
    }

    // Square for H
    class SaturationBrightnessImage extends AbstractHSBImage {
        public SaturationBrightnessImage(int width, int height, float hue) {
            super(width, height, hue, 1.0f, 1.0f);
        }

        public void getHSBForLocation(int x, int y, float[] hsbArray) {
            float saturationStep = ((float) x) / width;
            float brightnessStep = ((float) y) / height;
            hsbArray[0] = h;
            hsbArray[1] = s - saturationStep;
            hsbArray[2] = b - brightnessStep;
        }
    }

    // Square for S
    class HueBrightnessImage extends AbstractHSBImage {
        public HueBrightnessImage(int width, int height, float h, float s) {
            super(width, height, h, s, 1.0f);
        }

        public void getHSBForLocation(int x, int y, float[] hsbArray) {
            float brightnessStep = ((float) y) / height;
            float step = 1.0f / ((float) width);
            hsbArray[0] = x * step;
            hsbArray[1] = s;
            hsbArray[2] = 1.0f - brightnessStep;
        }
    }

    // Square for B
    class HueSaturationImage extends AbstractHSBImage {
        public HueSaturationImage(int width, int height, float h, float b) {
            super(width, height, h, 1.0f, b);
        }

        public void getHSBForLocation(int x, int y, float[] hsbArray) {
            float saturationStep = ((float) y) / height;
            float step = 1.0f / ((float) width);
            hsbArray[0] = x * step;
            hsbArray[1] = 1.0f - saturationStep;
            hsbArray[2] = b;
        }
    }

    // Slider for B
    class BrightnessImage extends AbstractHSBImage {
        protected int cachedY = -1;
        protected int cachedColor = 0;

        public BrightnessImage(int width, int height, float hue) {
            super(width, height, hue, 1.0f, 1.0f);
        }

        public int getRGBForLocation(int x, int y) {
            if (y == cachedY) {
            } else {
                cachedY = y;
                cachedColor = super.getRGBForLocation(x, y);
            }

            return cachedColor;
        }

        public void getHSBForLocation(int x, int y, float[] hsbArray) {
            float brightnessStep = ((float) y) / height;
            hsbArray[0] = h;
            hsbArray[1] = s;
            hsbArray[2] = b - brightnessStep;
        }
    }

    // Slider for S
    class SaturationImage extends AbstractHSBImage {
        protected int cachedY = -1;
        protected int cachedColor = 0;

        public SaturationImage(int width, int height, float hue) {
            super(width, height, hue, 1.0f, 1.0f);
        }

        public int getRGBForLocation(int x, int y) {
            if (y == cachedY) {
            } else {
                cachedY = y;
                cachedColor = super.getRGBForLocation(x, y);
            }

            return cachedColor;
        }

        public void getHSBForLocation(int x, int y, float[] hsbArray) {
            float saturationStep = ((float) y) / height;
            hsbArray[0] = h;
            hsbArray[1] = s - saturationStep;
            hsbArray[2] = b;
        }
    }

    // Slider for H
    class HueImage extends AbstractHSBImage {
        public HueImage(int width, int height) {
            super(width, height, 0f, 1.0f, 1.0f);
        }

        protected boolean isStatic() {
            return true;
        }

        public void getHSBForLocation(int x, int y, float[] hsbArray) {
            float step = 1.0f / ((float) height);
            hsbArray[0] = y * step;
            hsbArray[1] = s;
            hsbArray[2] = b;
        }
    }

}
