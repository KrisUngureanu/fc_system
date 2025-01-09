package kz.tamur.rt.adapters;

import java.util.*;
import java.util.List;
import java.awt.*;
import javax.swing.*;

import kz.tamur.rt.MainFrame;
import kz.tamur.util.Pair;

public class SelectObjectPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JList objects_;
    DefaultListModel lm = new DefaultListModel();
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    
    public SelectObjectPanel(List items) {
        setOpaque(isOpaque);
        setLayout(new BorderLayout());
        objects_ = new JList(lm);
        JScrollPane sp = new JScrollPane(objects_);
        add(sp, BorderLayout.CENTER);
        for (Iterator it = items.iterator(); it.hasNext();)
            lm.addElement(it.next());
    }

    public Pair getSelectedObject() {
        return (Pair) objects_.getSelectedValue();
    }

}