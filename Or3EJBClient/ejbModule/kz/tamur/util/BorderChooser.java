package kz.tamur.util;

import kz.tamur.comps.Constants;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.Utils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.HORIZONTAL;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 26.05.2004
 * Time: 16:35:48
 * To change this template use File | Settings | File Templates.
 */
public class BorderChooser extends JPanel implements ItemListener {

    private JToggleButton etchedBtn = ButtonsFactory.createCompButton("",
            kz.tamur.rt.Utils.getImageIcon("EtchedBorder"));
    private JToggleButton bevelRBtn = ButtonsFactory.createCompButton("",
            kz.tamur.rt.Utils.getImageIcon("BevelRized"));
    private JToggleButton bevelLBtn = ButtonsFactory.createCompButton("",
            kz.tamur.rt.Utils.getImageIcon("BevelLowred"));
    private JToggleButton lineBtn = ButtonsFactory.createCompButton("",
            kz.tamur.rt.Utils.getImageIcon("LineBorder"));

    private ButtonGroup bg = new ButtonGroup();

    private Border resultBorder;
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    
    public BorderChooser() {
        super(new GridBagLayout());
        setOpaque(isOpaque);
        setPreferredSize(new Dimension(135, 135));
        init();
    }

    void init() {
        etchedBtn.setBorder(null);
        bevelRBtn.setBorder(null);
        bevelLBtn.setBorder(null);
        lineBtn.setBorder(null);

        Dimension sz = new Dimension(50, 50);
        Utils.setAllSize(etchedBtn, sz);
        Utils.setAllSize(bevelRBtn, sz);
        Utils.setAllSize(bevelLBtn, sz);
        Utils.setAllSize(lineBtn, sz);

        bg.add(lineBtn);
        bg.add(etchedBtn);
        bg.add(bevelRBtn);
        bg.add(bevelLBtn);
        
        etchedBtn.addItemListener(this);
        bevelLBtn.addItemListener(this);
        bevelRBtn.addItemListener(this);
        lineBtn.addItemListener(this);

        add(etchedBtn, new GridBagConstraints(0, 0, 1, 1, 0, 0, CENTER, HORIZONTAL, Constants.INSETS_3, 0, 0));
        add(lineBtn, new GridBagConstraints(1, 0, 1, 1, 0, 0, CENTER, HORIZONTAL, Constants.INSETS_3, 0, 0));
        add(bevelRBtn, new GridBagConstraints(0, 1, 1, 1, 0, 0, CENTER, HORIZONTAL, Constants.INSETS_3, 0, 0));
        add(bevelLBtn, new GridBagConstraints(1, 1, 1, 1, 0, 0, CENTER, HORIZONTAL, Constants.INSETS_3, 0, 0));

        revalidate();
        repaint();
    }

    public Border getResultBorder() {
        return resultBorder;
    }

    public void itemStateChanged(ItemEvent e) {
        JToggleButton src = (JToggleButton)e.getSource();
        if (e.getStateChange() == ItemEvent.SELECTED) {
            if (src == etchedBtn) {
                resultBorder = BorderFactory.createEtchedBorder();
            } else if (src == bevelRBtn) {
                resultBorder = BorderFactory.createBevelBorder(BevelBorder.RAISED);
            } else if (src == bevelLBtn) {
                resultBorder = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
            } else if (src == lineBtn) {
                resultBorder = BorderFactory.createLineBorder(Color.black);
            }
        }
    }

    public void setSelectedStyle(Border b) {
        if (b instanceof EtchedBorder) {
            etchedBtn.setSelected(true);
        } else if (b instanceof LineBorder) {
            lineBtn.setSelected(true);
        } else if (b instanceof BevelBorder) {
            int st = ((BevelBorder)b).getBevelType();
            if (st == BevelBorder.RAISED) {
                bevelRBtn.setSelected(true);
            } else {
                bevelLBtn.setSelected(true);
            }
        }
    }
}
