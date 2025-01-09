package kz.tamur.util.colorchooser;


import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.CENTER;
import static javax.swing.BorderFactory.createTitledBorder;
import static javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION;
import static javax.swing.border.TitledBorder.DEFAULT_POSITION;
import static kz.tamur.rt.Utils.getImageIcon;
import static kz.tamur.rt.Utils.getImageIconJpg;
import static kz.tamur.rt.Utils.getDarkShadowSysColor;
import static kz.tamur.rt.Utils.getDefaultFont;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import kz.tamur.comps.Constants;
import kz.tamur.comps.ui.tabbedPane.OrBasicTabbedPane;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.Utils;

/**
 * Created by Vitaly A. Pronin
 * Date: 17.02.2004
 * Time: 20:27:58
 */
public class OrColorChooser extends JPanel {


    protected Border b = BorderFactory.createLineBorder(Utils.getDarkShadowSysColor());
    private ImageIcon imColor = getImageIconJpg("ColorChooser");
    private ImageIcon imPalitra = getImageIcon("PalitraTab");
    private ImageIcon imHSB = getImageIcon("HSB");
    private ImageIcon imRGB = getImageIcon("RGB");
    private JLabel imageLab = new JLabel();
    private JLabel resultLab = new JLabel(" ");

    private OrBasicTabbedPane contentTabPane;

    private JPanel previewPanel = new JPanel(new GridLayout(1, 3));

    private JPanel textPrevPanel = new JPanel(new BorderLayout());
    private JPanel backgroundPrevPanel = new JPanel(new BorderLayout());
    private JPanel borderPrevPanel = new JPanel(new BorderLayout());

    private JLabel backLabel = new JLabel("");
    private JLabel textLabel = new JLabel("Пример");
    private JLabel borderLabel = new JLabel("");

    private Color currentColor_;

    private SwatchChooserPanel swatchPanel;
    private HSBChooserPanel hsbPanel;
    private RGBChooserPanel rgbPanel;

    public Color currentColor;
   // private int prevIndex = 0;
    protected boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    
    public OrColorChooser(Color curColor) {
        super(new GridBagLayout());
        contentTabPane = new OrBasicTabbedPane();
        setCurrentColor(curColor);
        init();
        setPreferredSize(new Dimension(600, 320));
        if (curColor != null) {
            setCurrentColor(curColor);
        }
    }

    void init() {
        imageLab.setIcon(imColor);
        add(imageLab, new GridBagConstraints(0, 0, 1, 2, 0, 1, CENTER, BOTH, Constants.INSETS_1, 0, 0));
        buildTabPane();
        buildPreviewPanel();

        resultLab.setBackground(Color.white);
        resultLab.setOpaque(true);
        
        previewPanel.setOpaque(isOpaque);
        textPrevPanel.setOpaque(isOpaque);
        backgroundPrevPanel.setOpaque(isOpaque);
        borderPrevPanel.setOpaque(isOpaque);
    }

    void buildTabPane() {
        swatchPanel = new SwatchChooserPanel(this);
        swatchPanel.buildChooser();
        rgbPanel = new RGBChooserPanel(this);
        rgbPanel.buildChooser();
        hsbPanel = new HSBChooserPanel(this);
        hsbPanel.buildChooser();
        contentTabPane.setFont(Utils.getDefaultFont());
        contentTabPane.setOpaque(isOpaque);
        contentTabPane.addTab("Палитра", imPalitra, swatchPanel);
        contentTabPane.addTab("Оттенки", imHSB, hsbPanel);
        contentTabPane.addTab("RGB", imRGB, rgbPanel);
        
        contentTabPane.setSelectedIndex(0);
      //  contentTabPane.setForegroundAt(0, contentTabPane.getSelectedForegroundTab());
      
        
        contentTabPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                OrBasicTabbedPane tab = (OrBasicTabbedPane) e.getSource();
                switch (tab.getSelectedIndex()) {
                case 1:
                    hsbPanel.updateChooser();
                    break;
                case 2:
                    rgbPanel.updateChooser();
                    break;
                case 0:
                    swatchPanel.updateChooser();
                    break;
                }
            }
        });
        add(contentTabPane, new GridBagConstraints(1, 0, 1, 1, 1, 0, CENTER, BOTH, Constants.INSETS_1, 0, 0));
    }

    void buildPreviewPanel() {
        previewPanel.setBorder(Utils.createTitledBorder(b, "Просмотр"));
        backgroundPrevPanel.setBorder(Utils.createTitledBorder(b, "Фон"));
        textPrevPanel.setBorder(Utils.createTitledBorder(b, "Текст"));
        borderPrevPanel.setBorder(Utils.createTitledBorder(b, "Бордюр"));

        backLabel.setBackground(Color.white);
        backLabel.setOpaque(true);
        backLabel.setHorizontalAlignment(JLabel.CENTER);
        backLabel.setForeground(Color.black);

        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setForeground(Color.red);

        borderLabel.setBorder(BorderFactory.createLineBorder(Color.black, 4));
        borderLabel.setHorizontalAlignment(JLabel.CENTER);

        backgroundPrevPanel.add(backLabel, BorderLayout.CENTER);
        textPrevPanel.add(textLabel, BorderLayout.CENTER);
        borderPrevPanel.add(borderLabel, BorderLayout.CENTER);

        previewPanel.add(backgroundPrevPanel);
        previewPanel.add(textPrevPanel);
        previewPanel.add(borderPrevPanel);
        add(previewPanel, new GridBagConstraints(1, 1, 1, 1, 1, 0, CENTER, BOTH, Constants.INSETS_1, 0, 0));
    }

    public void setCurrentColor(Color color) {
        if (color == null) {
            color = Color.black;
        }
        currentColor_ = color;
        updatePreviewPanel();
    }

    public Color getCurrentColor() {
        return currentColor_;
    }

    void updatePreviewPanel() {
        backLabel.setBackground(currentColor_);
        textLabel.setForeground(currentColor_);
        borderLabel.setBorder(BorderFactory.createLineBorder(currentColor_, 4));
        resultLab.setBackground(currentColor_);
    }

    public Color getColor() {
        return currentColor_;
    }
    
    
    public Border getBorder() {
        return b;
    }

}
