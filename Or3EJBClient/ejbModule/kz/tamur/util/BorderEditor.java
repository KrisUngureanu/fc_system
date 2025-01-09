package kz.tamur.util;

import kz.tamur.rt.Utils;
import kz.tamur.comps.Constants;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.Or3Frame;
import kz.tamur.rt.MainFrame;
import kz.tamur.util.colorchooser.OrColorChooser;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Enumeration;


/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 17.04.2006
 * Time: 9:19:37
 */
public class BorderEditor extends JPanel {

    private Border lastBorder;

    private BorderChooser borderChooser = new BorderChooser();
    private JPanel fontChooser = new JPanel(new GridBagLayout());
    private JPanel borderColorPanel = new JPanel(new GridBagLayout());
    private JTextField fontText = Utils.createDesignerTextField();
    private JSpinner borderWidthField = new JSpinner();
    private JButton browseFontBtn = ButtonsFactory.createToolButton("editor",
            "Выбрать", true);
    private JButton browseColorBtn = ButtonsFactory.createToolButton("editor",
            "Выбрать", true);
    private JButton browseColorBorderBtn = ButtonsFactory.createToolButton("editor",
            "Выбрать", true);
    private JLabel colorLab = new JLabel("          ");
    private JLabel colorBorderLab = new JLabel("        ");

    private JPanel alignPanel = new JPanel();
    JRadioButton leftAlignRadio = Utils.createRadioButton("Слева         ");
    JRadioButton centerAlignRadio = Utils.createRadioButton("В центре        ");
    JRadioButton rightAlignRadio = Utils.createRadioButton("Справа         ");
    private ButtonGroup bgAlign = new ButtonGroup();

    private JPanel justPanel = new JPanel();
    JRadioButton upJustRadio = Utils.createRadioButton("Над         ");
    JRadioButton centerJustRadio = Utils.createRadioButton("В центре         ");
    JRadioButton downJustRadio = Utils.createRadioButton("Под   ");
    private ButtonGroup bgJust = new ButtonGroup();
    private JPanel titlePanel = new JPanel(new GridBagLayout());

    private Font chooseFont;
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    
    public BorderEditor(Border b) {
        super(new GridBagLayout());
        lastBorder = b;
        init();
    }

    private void init() {
        fontChooser.setOpaque(isOpaque);
        borderColorPanel.setOpaque(isOpaque);
        borderWidthField.setOpaque(isOpaque);
        leftAlignRadio.setOpaque(isOpaque);
        centerAlignRadio.setOpaque(isOpaque);
        rightAlignRadio.setOpaque(isOpaque);
        Border b = BorderFactory.createLineBorder(Utils.getDarkShadowSysColor());
        TitledBorder tb = Utils.createTitledBorder(b, "Стиль");
        borderChooser.setBorder(tb);
        tb = Utils.createTitledBorder(b, "Заголовок");
        titlePanel.setBorder(tb);

        tb = Utils.createTitledBorder(b, "Выравнивание");
        alignPanel.setBorder(tb);
        alignPanel.add(leftAlignRadio);
        alignPanel.add(centerAlignRadio);
        alignPanel.add(rightAlignRadio);
        bgAlign.add(leftAlignRadio);
        bgAlign.add(centerAlignRadio);
        bgAlign.add(rightAlignRadio);
        leftAlignRadio.setSelected(true);
        BoxLayout bl = new BoxLayout(alignPanel, BoxLayout.Y_AXIS);
        alignPanel.setLayout(bl);

        tb = Utils.createTitledBorder(b, "Положение");
        justPanel.setBorder(tb);
        justPanel.add(centerJustRadio);
        justPanel.add(upJustRadio);
        justPanel.add(downJustRadio);
        bgJust.add(centerJustRadio);
        bgJust.add(upJustRadio);
        bgJust.add(downJustRadio);
        centerJustRadio.setSelected(true);
        bl = new BoxLayout(justPanel, BoxLayout.Y_AXIS);
        justPanel.setLayout(bl);

        colorLab.setOpaque(true);
        colorLab.setBackground(Color.black);

        colorBorderLab.setOpaque(true);
        colorBorderLab.setBackground(Utils.getDarkShadowSysColor());

        browseFontBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Font lastFont = (chooseFont != null) ? chooseFont : Utils.getDefaultFont();
                OrFontChooser fChooser = new OrFontChooser(lastFont);
                DesignerDialog dlg = new DesignerDialog(Or3Frame.instance(),
                        "Выбор шрифта", fChooser, false, true);
                dlg.show();
                if (dlg.isOK()) {
                    chooseFont = fChooser.getChooserFont();
                    fontText.setText(Utils.getFontToString(fChooser.getChooserFont()));
                } else if (dlg.getResult() == ButtonsFactory.BUTTON_DEFAULT) {
                    Font fnt = Utils.getDefaultFont();
                    if (fnt != null) {
                        chooseFont = fnt;
                        fontText.setText(Utils.getFontToString(fChooser.getChooserFont()));
                    }
                }

            }
        });

        browseColorBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                OrColorChooser cch = new OrColorChooser(colorLab.getBackground());
                DesignerDialog dlg = new DesignerDialog(Or3Frame.instance(), "Выбор цвета", cch,
                        false, true);
                dlg.show();
                if (dlg.isOK()) {
                    colorLab.setBackground(cch.getColor());
                } else if (dlg.getResult() == ButtonsFactory.BUTTON_DEFAULT) {
                    colorLab.setBackground(Color.black);
                }

            }
        });

        browseColorBorderBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                OrColorChooser cch = new OrColorChooser(colorBorderLab.getBackground());
                DesignerDialog dlg = new DesignerDialog(Or3Frame.instance(), "Выбор цвета", cch,
                        false, true);
                dlg.show();
                if (dlg.isOK()) {
                    colorBorderLab.setBackground(cch.getColor());
                } else if (dlg.getResult() == ButtonsFactory.BUTTON_DEFAULT) {
                    colorBorderLab.setBackground(Color.black);
                }
            }
        });

        fontChooser.add(Utils.createLabel("Шрифт", JLabel.RIGHT),
                new GridBagConstraints(0, 2, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 3, 0, 0), 0, 0));
        fontChooser.add(fontText, new GridBagConstraints(1, 2, 2, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 3, 0, 3), 0, 0));
        fontChooser.add(browseFontBtn, new GridBagConstraints(3, 2, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(5, 0, 0, 3), 0, 0));
        fontChooser.add(Utils.createLabel("Цвет шрифта", JLabel.RIGHT),
                new GridBagConstraints(0, 3, 2, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 3, 0, 0), 0, 0));
        fontChooser.add(colorLab, new GridBagConstraints(2, 3, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 3, 0, 5), 0, 0));
        fontChooser.add(browseColorBtn, new GridBagConstraints(3, 3, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(5, 0, 5, 3), 0, 0));

        add(borderChooser, new GridBagConstraints(0, 0, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                Constants.INSETS_0, 0, 0));
        add(borderColorPanel, new GridBagConstraints(0, 1, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                Constants.INSETS_0, 0, 0));
        add(titlePanel, new GridBagConstraints(1, 0, 2, 2, 0, 1,
                GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
                Constants.INSETS_0, 0, 0));
        titlePanel.add(fontChooser, new GridBagConstraints(0, 0, 2, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                Constants.INSETS_0, 0, 0));
        titlePanel.add(alignPanel, new GridBagConstraints(0, 1, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 0, 20, 0), 0, 0));
        titlePanel.add(justPanel, new GridBagConstraints(1, 1, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 0, 20, 0), 0, 0));

        borderColorPanel.add(Utils.createLabel("Цвет бордюра", JLabel.RIGHT),
                new GridBagConstraints(0, 0, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                Constants.INSETS_0, 0, 0));
        borderColorPanel.add(colorBorderLab, new GridBagConstraints(1, 0, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 5, 0, 0), 0, 0));
        borderColorPanel.add(browseColorBorderBtn, new GridBagConstraints(2, 0, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 5, 0, 5), 0, 0));
        borderColorPanel.add(Utils.createLabel("Толщина бордюра", JLabel.RIGHT),
                new GridBagConstraints(0, 1, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(10, 5, 0, 3), 0, 0));
        borderColorPanel.add(borderWidthField,
                new GridBagConstraints(1, 1, 2, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(10, 0, 0, 5), 0, 0));
        setLastBorder();
    }

    private void setLastBorder() {
        if (lastBorder != null) {
            if (!(lastBorder instanceof TitledBorder)) {
                borderChooser.setSelectedStyle(lastBorder);
                if (lastBorder instanceof LineBorder) {
                    borderWidthField.setValue(
                            String.valueOf(((LineBorder)lastBorder).getThickness()));
                    colorBorderLab.setBackground(((LineBorder)lastBorder).getLineColor());
                }
            } else {
                TitledBorder b = (TitledBorder)lastBorder;
                Border bord = b.getBorder();
                if (bord instanceof LineBorder) {
                    borderWidthField.setValue(
                            ((LineBorder)bord).getThickness());
                    colorBorderLab.setBackground(((LineBorder)bord).getLineColor());
                }
                borderChooser.setSelectedStyle(bord);
                fontText.setText(Utils.getFontToString(b.getTitleFont()));
                colorLab.setBackground(b.getTitleColor());
                int just = b.getTitleJustification();
                if (just == TitledBorder.CENTER) {
                    centerAlignRadio.setSelected(true);
                } else if (just == TitledBorder.LEFT) {
                    leftAlignRadio.setSelected(true);
                } else if (just == TitledBorder.RIGHT) {
                    rightAlignRadio.setSelected(true);
                }
                int pos = b.getTitlePosition();
                if (pos == TitledBorder.DEFAULT_POSITION) {
                    centerJustRadio.setSelected(true);
                } else if (just == TitledBorder.ABOVE_TOP) {
                    upJustRadio.setSelected(true);
                } else if (just == TitledBorder.BELOW_TOP) {
                    downJustRadio.setSelected(true);
                }
            }
        }
    }

    public Border getResultBorder() {
        Border b = borderChooser.getResultBorder();
        if (b != null) {
            if (b instanceof LineBorder) {
                Object thick = borderWidthField.getValue();
                int th = 1;
                if (thick != null) {
                    try {
                        th = new Integer(String.valueOf(thick)).intValue();
                    } catch (Exception e) {
                        MessagesFactory.showMessageDialog(Or3Frame.instance(),
                                MessagesFactory.ERROR_MESSAGE, "Неверный формат!");
                        borderWidthField.setValue(1);
                        th = 1;
                    }
                }
                b = BorderFactory.createLineBorder(
                        colorBorderLab.getBackground(), th);
            }
            int align = TitledBorder.DEFAULT_POSITION;
            int justif = TitledBorder.DEFAULT_JUSTIFICATION;
            JRadioButton rb = null;
            Enumeration en = bgAlign.getElements();
            while (en.hasMoreElements()) {
                rb = (JRadioButton)en.nextElement();
                if (rb.isSelected()) {
                    break;
                }
            }
            if (rb == centerAlignRadio) {
                align = TitledBorder.CENTER;
            } else if (rb == leftAlignRadio) {
                align = TitledBorder.LEFT;
            } else if (rb == rightAlignRadio) {
                align = TitledBorder.RIGHT;
            }
            en = bgJust.getElements();
            while (en.hasMoreElements()) {
                rb = (JRadioButton)en.nextElement();
                if (rb.isSelected()) {
                    break;
                }
            }
            if (rb == centerJustRadio) {
                justif = TitledBorder.DEFAULT_JUSTIFICATION;
            } else if (rb == upJustRadio) {
                justif = TitledBorder.ABOVE_TOP;
            } else if (rb == downJustRadio) {
                justif = TitledBorder.BELOW_TOP;
            }
            TitledBorder tb = new TitledBorder(b, "", align, justif,
                    chooseFont, colorLab.getBackground());
            return tb;
        }
        return b;
    }

}
