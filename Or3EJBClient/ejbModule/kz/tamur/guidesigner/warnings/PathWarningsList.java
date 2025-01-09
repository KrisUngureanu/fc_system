package kz.tamur.guidesigner.warnings;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;

import kz.tamur.guidesigner.DesignerModalFrame;
import kz.tamur.rt.MainFrame;

public class PathWarningsList extends JPanel {

    private JList list = new JList();
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    private DesignerModalFrame parent;
    
    public PathWarningsList() {
        super();
        setLayout(new BorderLayout());
        init();
    }
    
    public void setParent(DesignerModalFrame dialog) {
        parent = dialog;
    }
    
    void init() {
        setOpaque(isOpaque);
        list.setBackground(Color.lightGray);
        list.setCellRenderer(new PathWarningsListCellRenderer());
        list.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.getClickCount() == 2) {
        	        parent.processCancelClicked();
                }   	
            }
        });
        
        JScrollPane scroll =  new JScrollPane(list);
        list.setOpaque(isOpaque);
        scroll.setOpaque(isOpaque);
        scroll.getViewport().setOpaque(isOpaque);
        add(scroll, BorderLayout.CENTER);
        setPreferredSize(new Dimension(800, 400));
    }

    public int getListSize() {
        return list.getModel().getSize();
    }


    public void addToList(Object[] items) {
        list.setListData(items);
    }

    public JList getList() {
        return list;
    }
    
    public void setList(JList list) {
        this.list = list;
    }

    class PathWarningsListCellRenderer extends JLabel implements ListCellRenderer {

        ImageIcon imOptional = kz.tamur.rt.Utils.getImageIcon("optional");
    	
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            setFont(new Font("Dialog", Font.PLAIN, 12));
            setForeground(Color.black);
            setBackground(isSelected ? Color.white : Color.lightGray);
            setOpaque(isSelected || isOpaque);
            setText(value.toString());
            setIcon(imOptional);
            return this;
        }
    }
}