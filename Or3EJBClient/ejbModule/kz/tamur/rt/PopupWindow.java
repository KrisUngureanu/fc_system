package kz.tamur.rt;

import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static kz.tamur.rt.Utils.getImageIconFull;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JLabel;

import kz.tamur.comps.models.GradientColor;
import kz.tamur.comps.ui.GradientPanel;

/**
 * Класс реализует окошко всплывающего уведомления.
 * @author Sergey Lebedev
 * 
 */
public class PopupWindow extends JDialog implements MouseListener {

    /** The title l. */
    private JLabel titleL = Utils.createLabel();
    
    public static List<PopupWindow> alarms = new ArrayList<PopupWindow>();
    private int indexThis=0;

    /** The main p. */
    private GradientPanel mainP = new GradientPanel();

    /**
     * Создание нового popup window.
     * 
     * @param title
     *            the title
     */
    PopupWindow(String title) {
        indexThis = alarms.size();
        alarms.add(this);
        add(mainP);
        setUndecorated(true);
        setSize(200, 50);
        setAlwaysOnTop(true);
        titleL.addMouseListener(this);
        titleL.setText("<html>"+title.replaceAll("@", "</br>"));
        mainP.setGradient(new GradientColor("-3355393, -10066177, 3, 0, 0, 98, 1"));
        mainP.setLayout(new GridBagLayout());
        mainP.add(new JLabel(getImageIconFull("info.png")), new GridBagConstraints(0, 0, 1, 1, 1, 0, CENTER, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        mainP.add(titleL, new GridBagConstraints(1, 0, 1, 1, 1, 0, CENTER, HORIZONTAL, new Insets(10, 10, 10, 10), 0, 0));
    }

    public void setVisible(boolean b) {
        if (b) {
            Point cp = kz.tamur.comps.Utils.getSouthEastLocationPoint(getSize());
            // сдвиг по количеству уведомлений
            cp.y = cp.y -(getSize().height*indexThis+2);
            setLocation(cp);
            toFront();
        }
        super.setVisible(b);
    }

    public void mouseClicked(MouseEvent e) {}

    public void mousePressed(MouseEvent e) {

        setVisible(false);
        alarms.remove(this);
        if(alarms.size()==0) {
            TaskTable.instance(true).getFrame().hideAlarm();
        }
        synchronized (alarms) {
            Point cp = kz.tamur.comps.Utils.getSouthEastLocationPoint(getSize());
            for(int i=0;i<alarms.size();i++) {
                PopupWindow obj = alarms.get(i);
                cp.y = cp.y -(obj.getSize().height*i+2);
                obj.setLocation(cp);
            }
        }
    
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }
}
