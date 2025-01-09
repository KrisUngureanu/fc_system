package kz.tamur.comps;

import kz.tamur.rt.adapters.TableAdapter;
import kz.tamur.guidesigner.MessagesFactory;

import java.util.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

public abstract class OrCellEditor extends AbstractCellEditor
        implements TableCellEditor, ActionListener {

    private int unique;
    private JTable table;

    public abstract Object getValueFor(Object obj);

    public boolean isCellEditable(EventObject e) {
        if (e instanceof MouseEvent) {
            return (((MouseEvent) e).getClickCount() > 1);
        }
        return true;
    }

    public void actionPerformed(ActionEvent e) {
        if (checkUnique()) {
            stopCellEditing();
        } else {
            Container parent = table.getParent();
            while (parent != null && !(parent instanceof OrTable)) {
                parent = parent.getParent();
            }
            ResourceBundle res;
            if (parent instanceof OrTable) {
                OrFrame frame = ((OrTable)parent).getFrame();
                res = frame.getResourceBundle();
            } else {
                res = ResourceBundle.getBundle(Constants.NAME_RESOURCES, new Locale("ru"));
            }
            MessagesFactory.showMessageDialog(table.getTopLevelAncestor(), 4, res.getString("duplicateData"));
            cancelCellEditing();
            table.requestFocusInWindow();
        }

    }

    public boolean stopCellEdit() {
        if (checkUnique()) {
            stopCellEditing();
        } else {
        	return showDuplicate();
        }
        return true;
    }
    
    public boolean showDuplicate() {
        Container parent = table.getParent();
        while (parent != null && !(parent instanceof OrTable)) {
            parent = parent.getParent();
        }
        ResourceBundle res;
        if (parent instanceof OrTable) {
            OrFrame frame = ((OrTable)parent).getFrame();
            res = frame.getResourceBundle();
        } else {
            res = ResourceBundle.getBundle(Constants.NAME_RESOURCES, new Locale("ru"));
        }
        MessagesFactory.showMessageDialog(table.getTopLevelAncestor(), 4, res.getString("duplicateData"));
        cancelCellEditing();
        return false;
    }

    public void setUniqueIndex(int uniqueIndex) {
        unique = uniqueIndex;
    }

    public int getUin() {
        return unique;
    }

    public boolean checkUnique() {
        if (table != null) {
                if (table.getModel() instanceof TableAdapter.RtTableModel) {
                    Map uinMap = ((TableAdapter.RtTableModel)table.getModel()).getUniqueMap();
                    if (uinMap != null) {
                        Integer uin = new Integer(getUin());
                        if (uinMap.containsKey(uin)) {
                            ArrayList cls = (ArrayList) uinMap.get(uin);
                            int rows = table.getRowCount();
                            String[] values = new String[rows];
                            int row = table.getEditingRow();
                            int column = table.getEditingColumn();
                            for (int r=0; r < rows;r++) {
                                values[r]="";
                                for (int i=0; i < cls.size(); i++) {
                                    int c = ((Integer)cls.get(i)).intValue();
                                    if (row == r && c == column) {
                                        OrCellEditor ed = (OrCellEditor) table.getCellEditor(r,c);
                                        values[r] += ed.getCellEditorValue();
                                    } else {
                                        Object val = table.getValueAt(r, c);
                                        values[r] += val;
                                    }

                                }
                            }
                            if (row != -1) {
                            String control_str = values[row];
                            for (int r=0; r < rows;r++) {
                                if (r == row) continue;
                                if (values[r] == null || values[r].equals("null") || values[r].equals("")) continue;
                                if (values[r].equals(control_str))
                                    return false;
                            }
                            return true;
                        }}
                    }
                }
        }
        return true;
    }


    public void setEditingTable(JTable tb) {
        table = tb;
    }

    public boolean hasEditingTable() {
        return (table != null);
    }
}