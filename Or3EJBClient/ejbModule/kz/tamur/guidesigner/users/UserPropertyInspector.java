package kz.tamur.guidesigner.users;

import kz.tamur.rt.Utils;

import javax.swing.*;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.client.*;
import org.jdom.Element;

public class UserPropertyInspector extends JPanel {

	private static final long serialVersionUID = 1L;
	private UserPropertyTableModel model;
    private JTable table = new JTable();
    private UserPropertyEditor editor;
    private UserPropertyRenderer renderer;

    public UserPropertyInspector() throws HeadlessException {
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
        editor = new UserPropertyEditor();
        valueCol.setCellEditor(editor);
        renderer = new UserPropertyRenderer();
        valueCol.setCellRenderer(renderer);
        cm.addColumn(nameCol);
        cm.addColumn(valueCol);
        table.setColumnModel(cm);
        table.setTableHeader(new JTableHeader(cm));
        model = new UserPropertyTableModel(this);
        table.setModel(model);
        table.setFont(Utils.getDefaultFont());
    }

    public void setNode(UserNode node) {
        if(table!=null && table.getCellEditor()!=null){
            table.getCellEditor().stopCellEditing();
        }
        model.setNode(node);
    }

    public UserPropertyTableModel getModel() {
        return model;
    }

    public void setEditorMode(int mode) {
        editor.setMode(mode);
        renderer.setMode(mode);
    }

    class UserPropertyRenderer implements TableCellRenderer {

        JLabel label = Utils.createLabel("");
        JCheckBox checkbox = Utils.createCheckBox("", false);
        private int mode;

        public UserPropertyRenderer() {
            checkbox.setOpaque(false);
        }

/*
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
*/


        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected,
                                                       boolean hasFocus, int row,
                                                       int column) {
            if (mode == UserPropertyEditor.POLICY_MODE) {
                if (value instanceof Long) {
                    label.setText(value.toString());
                } else {
                    label.setText("0");
                }
            } else {
                label.setText("");
                    switch(row) {
                        case 0:
                            if (value != null) {
                            	label.setText(value.toString());
                            }
                            break;
                        case 1:
                            if (value != null) {
                                if (!"Пункты гиперменю".equals(table.getValueAt(row, 0))) {
                                    if (!isSelected || table.getSelectedColumn() == 0) label.setText("******");
                                } else if (value instanceof KrnObject[]) {
                                    label.setText(kz.tamur.comps.Utils.parseObjectsToTitle((KrnObject[])value));
                                }
                            } else {
                                label.setText("");
                            }
                            break;
                        case 2:
                            if (value instanceof String) {
                                label.setText(value.toString());
                            } else if (value instanceof Boolean) {
                                checkbox.setSelected(((Boolean)value).booleanValue());
                                return checkbox;
                            } else {
                                checkbox.setSelected(false);
                                return checkbox;
                            }
                            break;
                        case 3:
                            if (value instanceof String)
                                label.setText(value.toString());
                            else if (value instanceof KrnObject[]){
                                //значит это помощь
                                //NoteNode node = new NoteNode();
                                StringBuffer str = new StringBuffer();
                                for (int i = 0; i<((KrnObject[])value).length; i++) {
                                    if (i == 0) str.append(parseObjectToTitle(((KrnObject[])value)[i],"help"));
                                    else str.append("," + parseObjectToTitle(((KrnObject[])value)[i],"help"));
                                }
                                label.setText(str.toString());
//                                    label.setText(parseObjectToTitle((KrnObject)value,"help"));
                            }
                            break;
                        case 4:
                            if(value instanceof KrnObject && ((KrnObject)value).classId==Kernel.SC_PROCESS_DEF.id) {
                                label.setText(parseObjectToTitle((KrnObject)value, "process"));
                            } else if (value != null) {
                                label.setText(value.toString());
                            }
                            break;
                        case 5:
                            if (value instanceof KrnObject) {
                                label.setText(parseObjectToTitle((KrnObject)value, "base"));
                            } else if (value instanceof Element) {
                                label.setText(Or3RightsNode.calculate((Element)value));
                            }
                            break;
                        case 6:
                            if (value != null) {
                                label.setText(parseObjectToTitle((KrnObject)value,
                                        "data language"));
                            }
                            break;
                        case 7:
                            if (value != null) {
                                label.setText(parseObjectToTitle((KrnObject)value,
                                        "interface language"));
                            }
                            break;
                        case 8:
                            if (value != null) {
                                label.setText(parseObjectToTitle((KrnObject)value, "interface"));
                            }
                            break;
                        case 9:
                            if (value instanceof Boolean) {
                                    checkbox.setSelected(((Boolean)value).booleanValue());
                                    return checkbox;
                            }
                            break;
                        case 10:
                            if (value instanceof Boolean) {
                                    checkbox.setSelected(((Boolean)value).booleanValue());
                                    return checkbox;
                            }
                        case 11:
                            if (value instanceof Boolean) {
                                    checkbox.setSelected(((Boolean)value).booleanValue());
                                    return checkbox;
                            }
                            break;
                    }
            }
            return label;
        }

        public void setMode(int mode) {
            this.mode = mode;
        }
    }


    public String parseObjectToTitle(KrnObject obj, String attrName) {
        String title = "";
        if (obj != null) {
            Kernel krn = Kernel.instance();
            try {
                long langId = com.cifs.or2.client.Utils.getInterfaceLangId();
                if ("interface".equals(attrName)) {
                    String[] strs = krn.getStrings(obj, "title",
                            com.cifs.or2.client.Utils.getInterfaceLangId(), 0);
                    if (strs.length > 0) {
                        title = strs[0];
                    } else {
                        title = "Значение не присвоено";
                    }
                }
                if ("base".equals(attrName)) {
                    String[] strs = krn.getStrings(obj, "наименование", 0, 0);
                    if (strs.length > 0) {
                        title = strs[0];
                    } else {
                        title = "Значение не присвоено";
                    }
                }
                if ("data language".equals(attrName) ||
                        "interface language".equals(attrName)) {
                    String[] strs = krn.getStrings(obj, "name", 0, 0);
                    if (strs.length > 0) {
                        title = strs[0];
                    } else {
                        title = "Значение не присвоено";
                    }
                }
                if ("help".equals(attrName)) {
                    String[] strs = krn.getStrings(obj,"title",langId,0);
                    if (strs.length > 0) {
                        title = strs[0];
                    } else {
                        title = "Значение не присвоено";
                    }
                }
                if ("process".equals(attrName)) {
                    String[] strs = krn.getStrings(obj,"title",langId,0);
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


}
