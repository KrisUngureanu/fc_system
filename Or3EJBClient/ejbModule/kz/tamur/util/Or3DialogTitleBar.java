package kz.tamur.util;

import kz.tamur.rt.Utils;
import kz.tamur.comps.Constants;
import kz.tamur.guidesigner.ButtonsFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * User: vital
 * Date: 26.11.2004
 * Time: 18:13:17
 */
public class Or3DialogTitleBar extends JPanel implements
        MouseListener, MouseMotionListener {

    private Point clickPoint;
    //Frame buttons
    private JButton frameCloseBtn;
    private Window dialog;
    private String title = "";
    private JLabel titleLab = new JLabel();
    private JPanel titleBarLab = new JPanel();


    public Or3DialogTitleBar(JDialog dialog, String title) {
        super(new GridBagLayout());
        this.dialog = dialog;
        this.title = title;
        titleLab.setText(title);
        initButtons();
        init();
    }

    public Or3DialogTitleBar(JFrame dialog, String title) {
        super(new GridBagLayout());
        this.dialog = dialog;
        this.title = title;
        titleLab.setText(title);
        initButtons();
        init();
    }

    private void initButtons() {
        frameCloseBtn =
                ButtonsFactory.createFrameButton(ButtonsFactory.CLOSE, dialog);
    }

    private void init() {
        setBackground(Utils.getDarkShadowSysColor());
        setPreferredSize(new Dimension(100, 20));
        addMouseListener(this);
        addMouseMotionListener(this);
        titleLab.setForeground(Utils.getMidSysColor());
        titleLab.setFont(Utils.getAppTitleFont());
        titleLab.setIcon(kz.tamur.rt.Utils.getImageIcon("mainIcon"));
        titleLab.setIconTextGap(5);
        titleBarLab.setLayout(new GridBagLayout());
        titleBarLab.setOpaque(false);
        JLabel imageLabel = new JLabel(kz.tamur.rt.Utils.getImageIcon("dialog1"));

        titleBarLab.add(titleLab, new GridBagConstraints(0, 0, 4, 1, 1, 0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                Constants.INSETS_0, 0, 0));
        titleBarLab.add(frameCloseBtn,
                new GridBagConstraints(4, 0, 1, 1, 0, 0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(2, 0, 0, 0), 0, 0));
        add(titleBarLab, new GridBagConstraints(0, 0, 4, 1, 1, 0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(1, 1, 0, 0), 0, 0));
        add(imageLabel,
                new GridBagConstraints(4, 0, 1, 2, 0, 1,
                GridBagConstraints.NORTHEAST, GridBagConstraints.HORIZONTAL,
                Constants.INSETS_0, 0, 0));
    }

    public void setTitle(String title) {
        this.title = title;
        titleLab.setText(title);
    }

    public String getTitle() {
        return title;
    }

    public void mouseDragged(MouseEvent e) {
        Point p = dialog.getLocation();
        dialog.setLocation(p.x + (e.getPoint().x - clickPoint.x),
                p.y + (e.getPoint().y - clickPoint.y));
    }

    public void mouseMoved(MouseEvent e) {

    }

    public void mouseClicked(MouseEvent e) {

    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e) {
        clickPoint = e.getPoint();
    }

    public void mouseReleased(MouseEvent e) {

    }

}
