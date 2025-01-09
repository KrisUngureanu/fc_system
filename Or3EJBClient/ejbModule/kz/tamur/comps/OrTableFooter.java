package kz.tamur.comps;


import kz.tamur.rt.adapters.ColumnAdapter;
import kz.tamur.rt.adapters.IntColumnAdapter;
import kz.tamur.rt.adapters.FloatColumnAdapter;
import kz.tamur.rt.adapters.CheckBoxColumnAdapter;

import javax.swing.table.*;
import javax.swing.*;
import java.util.List;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import static kz.tamur.rt.Utils.createMenuItem;

public class OrTableFooter  extends JTableHeader {

    private JTable table;
    private List columns;
    private JPopupMenu popupSummary = new JPopupMenu();
    private JMenuItem noMenuItem = createMenuItem("Отсутствует");
    private JMenuItem sumMenuItem = createMenuItem("Сумма");
    private JMenuItem averageMenuItem = createMenuItem("Среднее");
    private JMenuItem maxMenuItem = createMenuItem("Максимальное");
    private JMenuItem minMenuItem = createMenuItem("Минимальное");
    private JMenuItem trueMenuItem = createMenuItem("Кол-во выборов", "trueCount");
    private JMenuItem countMenuItem = createMenuItem("Количество");
    private ColumnAdapter lastSelectedColumn = null;

    public OrTableFooter(JTable table, List columns) {
        super();
        this.table = table;
        this.columns = columns;
        setColumnModel(table.getColumnModel());
        initializeLocalVars();
        this.updateUI(table, columns);
        init();
    }

    private void init() {
        PopupActionListener actionListener = new PopupActionListener();
        noMenuItem.addActionListener(actionListener);
        sumMenuItem.addActionListener(actionListener);
        averageMenuItem.addActionListener(actionListener);
        maxMenuItem.addActionListener(actionListener);
        minMenuItem.addActionListener(actionListener);
        trueMenuItem.addActionListener(actionListener);
        countMenuItem.addActionListener(actionListener);

        popupSummary.add(noMenuItem);
        popupSummary.addSeparator();
        popupSummary.add(sumMenuItem);
        popupSummary.add(averageMenuItem);
        popupSummary.add(maxMenuItem);
        popupSummary.add(minMenuItem);
        popupSummary.add(trueMenuItem);
        popupSummary.add(countMenuItem);
        addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    TableColumnModel tcm = table.getColumnModel();
                    int colIdx = tcm.getColumnIndexAtX(e.getX());
                    ColumnAdapter colAdapt = (ColumnAdapter)columns.get(colIdx);
                    if (colAdapt != null) {
                        if (colAdapt instanceof IntColumnAdapter ||
                            colAdapt instanceof FloatColumnAdapter) {

                            sumMenuItem.setVisible(true);
                            averageMenuItem.setVisible(true);
                            maxMenuItem.setVisible(true);
                            minMenuItem.setVisible(true);
                            trueMenuItem.setVisible(false);
                            countMenuItem.setVisible(false);
                        }
                        else if (colAdapt instanceof CheckBoxColumnAdapter) {
                            trueMenuItem.setVisible(true);
                            sumMenuItem.setVisible(false);
                            averageMenuItem.setVisible(false);
                            maxMenuItem.setVisible(false);
                            minMenuItem.setVisible(false);
                            countMenuItem.setVisible(false);
                        } else {
                            trueMenuItem.setVisible(false);
                            sumMenuItem.setVisible(false);
                            averageMenuItem.setVisible(false);
                            maxMenuItem.setVisible(false);
                            minMenuItem.setVisible(false);
                            countMenuItem.setVisible(true);
                        }
                        lastSelectedColumn = colAdapt;
                        popupSummary.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
        });
    }


    public void updateUI(JTable table, List columns) {
        setUI(OrTableFooterUI.createUI(this,  table, columns));
        resizeAndRepaint();
        invalidate();//PENDING
        repaint();
    }

    public void refresh() {
        updateUI(table, columns);
    }

    public Rectangle getHeaderRect(int column) {
        Rectangle r = new Rectangle();
	TableColumnModel cm = getColumnModel();
	r.height = getHeight();
	if (column < 0) {
	    // x = width = 0;
	}
	else if (column >= cm.getColumnCount()) {
	    r.x = getWidth();
	}
	else {
	    for(int i = 0; i < column; i++) {
		r.x += cm.getColumn(i).getWidth();
	    }
	    r.width = cm.getColumn(column).getWidth();
	}
	return r;
    }

    public void setResizingColumn(TableColumn aColumn) {
	    resizingColumn = aColumn;
    }

    public TableColumn getResizingColumn() {
	    return resizingColumn;
    }

    private class PopupActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            JMenuItem src = (JMenuItem)e.getSource();
            if (lastSelectedColumn != null) {
                if (src == noMenuItem) {
                    lastSelectedColumn.setSummaryType(Constants.SUMMARY_NO);
                } else if (src == sumMenuItem) {
                    lastSelectedColumn.setSummaryType(Constants.SUMMARY_SUMM);
                } else if (src == averageMenuItem) {
                    lastSelectedColumn.setSummaryType(Constants.SUMMARY_AVERAGE);
                } else if (src == maxMenuItem) {
                    lastSelectedColumn.setSummaryType(Constants.SUMMARY_MAX);
                } else if (src == minMenuItem) {
                    lastSelectedColumn.setSummaryType(Constants.SUMMARY_MIN);                    
                } else if (src == trueMenuItem) {
                    lastSelectedColumn.setSummaryType(Constants.SUMMARY_TRUE_COUNT);
                } else if (src == countMenuItem) {
                    lastSelectedColumn.setSummaryType(Constants.SUMMARY_COUNT);
                }
                updateUI(table, columns);
            }
        }
    }


}
