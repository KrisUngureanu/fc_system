package kz.tamur.guidesigner.bases;

import javax.swing.*;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import kz.tamur.rt.Utils;

public class BasePropertyInspector extends JPanel {

    private BasePropertyTableModel model = new BasePropertyTableModel();
    private JTable table = new JTable();

    public BasePropertyInspector() throws HeadlessException {
        super();
        setLayout(new BorderLayout());
        add(new JScrollPane(table), BorderLayout.CENTER);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
        table.setAutoCreateColumnsFromModel(false);
        DefaultTableColumnModel cm = new DefaultTableColumnModel();
        TableColumn nameCol = new TableColumn(0);
        nameCol.setHeaderValue("Свойство");
        nameCol.setMaxWidth(250);
        nameCol.setMinWidth(120);
        nameCol.setPreferredWidth(120);
        TableColumn valueCol = new TableColumn(1);
        valueCol.setHeaderValue("Значение");
        valueCol.setCellEditor(new BasePropertyEditor());
        valueCol.setCellRenderer(new BasePropertyRenderer());
        cm.addColumn(nameCol);
        cm.addColumn(valueCol);
        table.setColumnModel(cm);
        table.setTableHeader(new JTableHeader(cm));
        table.setModel(model);
        table.setFont(Utils.getDefaultFont());
    }

    public void setNode(BaseNode node) {
        if (table != null && table.getCellEditor() != null){
            table.getCellEditor().stopCellEditing();
        }
        model.setNode(node);
    }

    public BasePropertyTableModel getModel() {
        return model;
    }

    class BasePropertyRenderer implements TableCellRenderer {

        JLabel label = Utils.createLabel("");
        JCheckBox checkbox = Utils.createCheckBox("", false);
        public BasePropertyRenderer() {
            checkbox.setOpaque(false);
        }
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected,
                                                       boolean hasFocus, int row,
                                                       int column) {
            label.setText("");
            if (value != null) {
                switch(row) {
                    case 0:
                        label.setText(value.toString());
                        break;
                    case 1:
                        label.setText(value.toString());
                        break;
                    case 2:
                        label.setText(value.toString());
                        break;
                    case 3:
                        if (value instanceof Boolean) {
                            checkbox.setSelected(((Boolean)value).booleanValue());
                            return checkbox;
                        }	
                        break;
                }
            } 
            return label;
        }
    }


/*
    public String parseObjectToTitle(KrnObject obj, String attrName) {
        String title = "";
        if (obj != null) {
            Kernel krn = Kernel.instance();
            try {
                if ("interface".equals(attrName)) {
                    title = krn.getStrings(obj, "title",
                            com.cifs.or2.client.Utils.getInterfaceLangId(), 0)[0];
                }
                if ("base".equals(attrName)) {
                    title = krn.getStrings(obj, "наименование", 0, 0)[0];
                }
                if ("data language".equals(attrName) ||
                        "interface language".equals(attrName)) {
                    title = krn.getStrings(obj, "name", 0, 0)[0];
                }
            } catch (KrnException e) {
                e.printStackTrace();
            }
        }
        return title;
    }
*/


}
