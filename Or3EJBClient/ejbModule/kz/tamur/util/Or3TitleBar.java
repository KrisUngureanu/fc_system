package kz.tamur.util;

import kz.tamur.comps.Constants;
import kz.tamur.rt.Utils;
import kz.tamur.guidesigner.ButtonsFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

/**
 * User: vital
 * Date: 26.11.2004
 * Time: 18:13:17
 */
public class Or3TitleBar extends JPanel implements MouseListener, MouseMotionListener {

    private Point clickPoint;
    //Frame buttons
    private JButton frameIconBtn;
    private JButton frameResMaxBtn;
    private JButton frameCloseBtn;


    private JFrame frame;
    private String title = "";
    private JLabel titleLab = new JLabel();
    private JPanel titleBarLab = new JPanel();
    private JMenuBar menuBar;


    public Or3TitleBar(JFrame frame, String title, JMenuBar menuBar) {
        super(new GridBagLayout());
        this.frame= frame;
        this.title = title;
        titleLab.setText(title);
        this.menuBar = menuBar;
        initButtons();
        init();
    }

    private void initButtons() {
        frameIconBtn =
                ButtonsFactory.createFrameButton(ButtonsFactory.MINIMIZE, frame);
        frameResMaxBtn =
                ButtonsFactory.createFrameButton(ButtonsFactory.RESTORE, frame);
        frameCloseBtn =
                ButtonsFactory.createFrameButton(ButtonsFactory.CLOSE, frame);
    }

    private void init() {
        setBackground(Utils.getDarkShadowSysColor());
        setPreferredSize(new Dimension(100, 40));
        addMouseListener(this);
        addMouseMotionListener(this);
        menuBar.setOpaque(false);
        titleLab.setForeground(Utils.getMidSysColor());
        titleLab.setFont(Utils.getAppTitleFont());
        titleLab.setIcon(kz.tamur.rt.Utils.getImageIcon("mainIcon"));
        titleLab.setIconTextGap(5);
        titleBarLab.setLayout(new GridBagLayout());
        titleBarLab.setOpaque(false);
        JLabel imageLabel = new JLabel(kz.tamur.rt.Utils.getImageIconJpg("or3"));
        imageLabel.setLayout(new GridBagLayout());
        imageLabel.add(frameIconBtn,
                new GridBagConstraints(0, 0, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 8), 0, 0));
        imageLabel.add(frameResMaxBtn,
                new GridBagConstraints(1, 0, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 8), 0, 0));
        imageLabel.add(frameCloseBtn,
                new GridBagConstraints(2, 0, 1, 1, 0, 0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 55), 0, 0));

        titleBarLab.add(titleLab, new GridBagConstraints(0, 0, 3, 1, 1, 0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                Constants.INSETS_0, 0, 0));
        add(titleBarLab, new GridBagConstraints(0, 0, 3, 1, 1, 0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(1, 1, 0, 0), 0, 0));
        add(menuBar, new GridBagConstraints(0, 1, 3, 1, 1, 0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 1, 1, 0), 0, 0));
        add(imageLabel,
                new GridBagConstraints(4, 0, 1, 2, 0, 1,
                GridBagConstraints.NORTHEAST, GridBagConstraints.HORIZONTAL,
                Constants.INSETS_0, 0, 0));
    }

    public void setMenuBar(JMenuBar menuBar) {
        remove(this.menuBar);
        this.menuBar = menuBar;
        add(this.menuBar, new GridBagConstraints(0, 1, 3, 1, 1, 0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 1, 1, 0), 0, 0));
        validate();
        repaint();
    }

    public void setTitle(String title) {
        this.title = title;
        titleLab.setText(title);
    }

    public String getTitle() {
        return title;
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
        setCursor(Constants.DEFAULT_CURSOR);
    }

    public void mouseDragged(MouseEvent e) {
        if (frame instanceof FrameTemplate) {
/*
            if (((FrameTemplate)frame).getCurrentState() !=
                    FrameTemplate.MAXIMIZED_STATE) {
                setCursor(new Cursor(Cursor.MOVE_CURSOR));
                Point p = frame.getLocation();
                frame.setLocation(p.x + (e.getPoint().x - clickPoint.x),
                        p.y + (e.getPoint().y - clickPoint.y));
            }
*/
        }
    }

    public void mouseMoved(MouseEvent e) {

    }

}
