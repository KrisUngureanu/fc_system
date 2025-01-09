package kz.tamur.guidesigner.hypers;

import kz.tamur.rt.Utils;

import javax.swing.*;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.client.*;

public class HyperPropertyInspector extends JPanel {

    private HyperPropertyTableModel model;
    private JTable table = new JTable();

    public HyperPropertyInspector(boolean canEdit) throws HeadlessException {
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
        valueCol.setCellEditor(new HyperPropertyEditor());
        valueCol.setCellRenderer(new HyperPropertyRenderer());
        cm.addColumn(nameCol);
        cm.addColumn(valueCol);
        table.setColumnModel(cm);
        table.setTableHeader(new JTableHeader(cm));
        model = new HyperPropertyTableModel(canEdit);
        table.setModel(model);
        table.setFont(Utils.getDefaultFont());
    }

    public void setNode(HyperNode node) {
        if(table!=null && table.getCellEditor()!=null){
            table.getCellEditor().stopCellEditing();
        }
        model.setNode(node);
    }

    public HyperPropertyTableModel getModel() {
        return model;
    }

    class HyperPropertyRenderer implements TableCellRenderer {

        JLabel label = Utils.createLabel("");
        JCheckBox checkBox = Utils.createCheckBox("", false);

        public HyperPropertyRenderer() {
            checkBox.setOpaque(false);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected,
                                                       boolean hasFocus, int row,
                                                       int column) {
            label.setText("");
            if (row == 0 || row == 1 || row == 2) {
                if (value != null) {
                   label.setText(value.toString());
                }
            } else if (row == 3) {
                if (value != null) {
                    label.setText(parseIfcObject((KrnObject)value));
                }
            } else if (row == 4 || row == 5) {
                checkBox.setSelected(((Boolean)value).booleanValue());
                return checkBox;
            }
            return label;
        }
    }


    public String parseIfcObject(KrnObject obj) {
        String title = "";
        if (obj != null) {
            Kernel krn = Kernel.instance();
            try {
                String[] str = krn.getStrings(obj, "title", com.cifs.or2.client.Utils.getInterfaceLangId(), 0);
                if (str.length > 0)
                    title = str[0];
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return title;
    }


}
