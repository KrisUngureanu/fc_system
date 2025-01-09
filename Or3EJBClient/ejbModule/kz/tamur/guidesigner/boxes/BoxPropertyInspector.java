package kz.tamur.guidesigner.boxes;

import kz.tamur.rt.Utils;

import javax.swing.*;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.client.Kernel;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 07.05.2005
 * Time: 16:28:40
 * To change this template use File | Settings | File Templates.
 */
public class BoxPropertyInspector extends JPanel {

    private BoxPropertyTableModel model = new BoxPropertyTableModel();
    private JTable table = new JTable();

    public BoxPropertyInspector() throws HeadlessException {
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
        valueCol.setCellEditor(new BoxPropertyEditor());
        valueCol.setCellRenderer(new BoxPropertyRenderer());
        cm.addColumn(nameCol);
        cm.addColumn(valueCol);
        table.setColumnModel(cm);
        table.setTableHeader(new JTableHeader(cm));
        table.setModel(model);
        table.setFont(Utils.getDefaultFont());
    }

    public void setNode(BoxNode node) {
        if(table!=null && table.getCellEditor()!=null){
            table.getCellEditor().stopCellEditing();
        }
        model.setNode(node);
    }

    public BoxPropertyTableModel getModel() {
        return model;
    }

    class BoxPropertyRenderer implements TableCellRenderer {

        JLabel label = Utils.createLabel("");

        public BoxPropertyRenderer() {
        }

        public String parseObjectsToTitle(KrnObject[] objs) {
            String title = "";
            if (objs != null && objs.length > 0) {
                Kernel krn = Kernel.instance();
                try {
                    for (int i = 0; i < objs.length; i++) {
                        KrnObject obj = objs[i];
                        String[] strs = krn.getStrings(obj, "title",
                                com.cifs.or2.client.Utils.getInterfaceLangId(), 0);
                        if (strs.length > 0) {
                            title = ("".equals(title)) ? strs[0] : title + "," +strs[0];
                        }
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
            return title;
        }

        public String parseObjectToTitle(KrnObject obj, String attrName) {
            String title = "";
            if (obj != null) {
                Kernel krn = Kernel.instance();
                try {
                    if ("base".equals(attrName)) {
                        String[] strs = krn.getStrings(obj, "наименование", 0, 0);
                        if (strs.length > 0) {
                            title = strs[0];
                        } else {
                            title = "Значение не присвоено";
                        }
                    }
                } catch (KrnException e) {
                    e.printStackTrace();
                }
            }
            return title;
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected,
                                                       boolean hasFocus, int row,
                                                       int column) {
            label.setText("");
            if (value != null) {
                if (row != 1) {
                    label.setText(value.toString());
                } else {
                    if (value != null && value instanceof KrnObject) {
                        label.setText(parseObjectToTitle((KrnObject)value, "base"));
                    }
                }
            }
            return label;
        }
    }


}
