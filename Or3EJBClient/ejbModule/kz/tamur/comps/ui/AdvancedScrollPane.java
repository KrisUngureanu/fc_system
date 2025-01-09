package kz.tamur.comps.ui;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.*;

import kz.tamur.comps.AdvancedScrollPaneLayout;

/**
 * Created by IntelliJ IDEA.
 * User: Berik
 * Date: 29.01.2004
 * Time: 9:35:35
 * To change this template use Options | File Templates.
 */
public class AdvancedScrollPane extends JScrollPane {

    public static final String COLUMN_FOOTER = "COLUMN_FOOTER";
    protected JViewport columnFooter;

    public AdvancedScrollPane(Component view) {
        super(view);
        AdvancedScrollPaneLayout l = new AdvancedScrollPaneLayout();
        setLayout(l);
        l.syncWithScrollPane(this);
    }

    public JViewport getColumnFooter() {
        return columnFooter;
    }

    public void setColumnFooterView(Component view) {
        if (getColumnFooter() == null) {
            setColumnFooter(createViewport());
        }
        getColumnFooter().setView(view);
        getViewport().addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                Point p = columnFooter.getViewPosition();
                p.x = viewport.getViewPosition().x;
                columnFooter.setViewPosition(p);
            }
        });
   }

    public void setColumnFooter(JViewport columnFooter) {
        JViewport old = getColumnFooter();
        this.columnFooter = columnFooter;
        if (columnFooter != null) {
            add(columnFooter, COLUMN_FOOTER);
        } else if (old != null) {
            remove(old);
        }
        firePropertyChange("columnFooter", old, columnFooter);
        revalidate();
        repaint();
    }

}
