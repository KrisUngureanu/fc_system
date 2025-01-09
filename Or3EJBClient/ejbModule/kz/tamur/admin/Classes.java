package kz.tamur.admin;

import static kz.tamur.rt.Utils.createLabel;
import static kz.tamur.rt.Utils.getImageIcon;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnMethod;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.client.*;
import com.cifs.or2.client.User;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;

import kz.tamur.Or3Frame;
import kz.tamur.admin.clsbrow.ObjectBrowser;
import kz.tamur.comps.models.Types;
import kz.tamur.comps.models.EnumValue;
import kz.tamur.rt.Utils;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.DesignerStatusBar;
import kz.tamur.guidesigner.serviceControl.ControlTabbedContent;
import kz.tamur.rt.MainFrame;

/**
 * User: vital
 * Date: 30.01.2005
 * Time: 13:48:48
 */
public class Classes extends JPanel /*implements TreeSelectionListener*/ {

    private JSplitPane splitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    private ClassBrowser classBrowser = new ClassBrowser(null, false);
//    private ClassPropertyTableModel model = new ClassPropertyTableModel();
//    private JTable table = new JTable(model);
    private KrnAttribute prevAttr;

    private EnumValue[] copyEnum = new EnumValue[] {
                    new EnumValue(0, "Копировать ссылку"),
                    new EnumValue(1, "Создать новый объект"),
                    new EnumValue(2, "Игнорировать")};
    private JLabel iconLabel = Utils.createLabel();
    private JLabel subIconLabel = Utils.createLabel();
    private JTextField selNodeField = Utils.createDesignerTextField();
    private JTextField idField = Utils.createDesignerTextField();
    private DesignerStatusBar statusPanel = new DesignerStatusBar();
    ControlTabbedContent tabbedContent = ControlTabbedContent.instance();
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    
    private JLabel serverLabel = createLabel("");
    private JLabel dsLabel = createLabel("");
    private JLabel currentDbName = createLabel("");
    private JLabel currentUserLable = createLabel("");

    public Classes() {
        super(new BorderLayout());
        add(splitter, BorderLayout.CENTER);
        add(statusPanel,BorderLayout.SOUTH);
        init();
        initStatusBar();        
    }

    public void setDividerLocation() {
        classBrowser.setSplitLocation();
    }

    private void init() {
        splitter.setLeftComponent(classBrowser);
        splitter.setDividerLocation(1.0);
        splitter.setDividerSize(0);
        splitter.validate();
        setOpaque(isOpaque); 
        splitter.setOpaque(isOpaque); 
    }
    private void initStatusBar(){
    	//Выводит иконку раздела "Классы"
    	iconLabel.setIcon(kz.tamur.rt.Utils.getImageIcon("classes"));    	
    	statusPanel.addAnyComponent(iconLabel);
    	
    	//Показать информацию о выбранном элементе в дереве
    	selNodeField.setText("");    	
    	statusPanel.addTextField(selNodeField);
    	statusPanel.addSeparator();
    	
    	//Показать суб-иконку    	
    	subIconLabel.setIcon(null);
    	statusPanel.addAnyComponent(subIconLabel);    	
    	    	
    	statusPanel.addTextField(idField, 0.5);
    	
    	
    	//Пустое место
    	//statusPanel.addEmptySpace();
    	statusPanel.addSeparator();
    	
        updateStatusBar();

        statusPanel.addAnyComponent(currentDbName);
        statusPanel.addSeparator();
        currentUserLable.setIcon(getImageIcon("User"));
        statusPanel.addAnyComponent(currentUserLable);
        statusPanel.addSeparator();
        dsLabel.setIcon(getImageIcon("HostConn"));
        dsLabel.setIconTextGap(10);
        statusPanel.addAnyComponent(dsLabel);
        statusPanel.addSeparator();
        serverLabel.setIcon(getImageIcon("PortConn"));
        serverLabel.setIconTextGap(10);
        statusPanel.addAnyComponent(serverLabel);
        statusPanel.addSeparator();
                
        statusPanel.addCorner();

    }
    public DesignerStatusBar getStatusBar() {
    	//Обновляем информацию в StatusBar
    	classBrowser.assemlyInfoToStatusBar();
        return statusPanel;
    }
    
    public void setSelNodeInfo(String text){
    	selNodeField.setText(text);    	
    }
    
    public void setSubIcon(Icon icon){
    	subIconLabel.setIcon(icon);
    }
    
    public void setIdField(String text){
    	idField.setText(text);
    }
        
    public void setSplitLocation() {
        splitter.setDividerLocation(1.0);
        splitter.setDividerSize(0);
        setDividerLocation();
    }

/*    public void valueChanged(TreeSelectionEvent e) {
        Object src = e.getSource();
        if (src instanceof AttributeTree) {
            Object o = e.getPath().getLastPathComponent();
            if (!(o instanceof Kernel.MethodNode)) {
                KrnAttribute attr = ((Kernel.AttrNode)o).getKrnAttribute();
                if (prevAttr == null ||
                        (prevAttr != null && prevAttr.id != attr.id)) {
                    model.setObject(attr);
                    prevAttr = attr;
                } else {
                    model.setObject(null);
                }
            } else {
                model.setObject(null);
            }
        } else {
            model.setObject(null);
        }
    }
  */
    public void applyRights(User user) {
        classBrowser.applyRights(user);
    }

    class PropertyRecord {
        String propertyName;
        int propertyType;
        Object propertyVal;

        public PropertyRecord(String propertyName, int propertyType, Object propertyVal) {
            this.propertyName = propertyName;
            this.propertyType = propertyType;
            this.propertyVal = propertyVal;
        }

        public String toString() {
            return propertyVal.toString();
        }
    }

    class ClassPropertyTableModel extends AbstractTableModel {

        private final String[] COL_NAMES = {"Свойство", "Значение"};
        private List<PropertyRecord> values = new ArrayList<PropertyRecord>();
        private Kernel krn = Kernel.instance();

        public void setObject(KrnAttribute val) {
            if (val != null && val instanceof KrnAttribute) {
                values.clear();
                PropertyRecord record = new PropertyRecord("Наименование",
                        Types.STRING, val.name);
                values.add(record);
                record = new PropertyRecord("Флаги",
                        Types.ENUM, new Integer(0));
                values.add(record);
                record = new PropertyRecord("Удаление",
                        Types.ENUM, new Integer(0));
                values.add(record);
                record = new PropertyRecord("Множественный",
                        Types.INTEGER, new Integer(val.collectionType));
                values.add(record);
                fireTableDataChanged();
//                if (classBrowser.inspBtn.isSelected()) {
//                    splitter.setDividerSize(3);
//                    splitter.setDividerLocation(0.9);
//                }
            } else {
                splitter.setDividerSize(0);
                splitter.setDividerLocation(1.0);
            }
        }


        public int getColumnCount() {
            return COL_NAMES.length;
        }

        public String getColumnName(int column) {
            return COL_NAMES[column];
        }

        public int getRowCount() {
            return (values.size() > 0) ? values.size() : 0;
        }

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return (columnIndex == 1) ? true : false;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            PropertyRecord pr = values.get(rowIndex);
            if (columnIndex == 0) {
                return pr.propertyName;
            } else {
                return pr;
            }
        }

        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (prevAttr != null && aValue != null && aValue instanceof PropertyRecord) {
                int oldArray = prevAttr.collectionType;
                try {
                    KrnClass typeClass = krn.getClassNode(prevAttr.typeClassId).getKrnClass();
                    PropertyRecord rec = (PropertyRecord)aValue;
                    if ("Наименование".equals(rec.propertyName) ) {
                        if (!prevAttr.name.equals(rec.propertyVal)) {
                            krn.changeAttribute(prevAttr, typeClass,
                                    rec.propertyVal.toString(), oldArray,
                                    prevAttr.isUnique, prevAttr.isIndexed,
                                    prevAttr.isMultilingual, prevAttr.isRepl,
                                    prevAttr.size, prevAttr.flags,
                                    prevAttr.rAttrId, prevAttr.sAttrId,
                                    prevAttr.sDesc, prevAttr.tname, prevAttr.accessModifierType);
                        }
                    } else if ("Множественный".equals(rec.propertyName)) {
                        int v = ((Integer)rec.propertyVal).intValue();
                        if (oldArray != v) {
                            krn.changeAttribute(
                            	prevAttr, typeClass,
                            	prevAttr.name, v,
                            	prevAttr.isUnique, prevAttr.isIndexed,
                            	prevAttr.isMultilingual, prevAttr.isRepl,
                            	prevAttr.size, prevAttr.flags,
                            	prevAttr.rAttrId, prevAttr.sAttrId,
                            	prevAttr.sDesc, prevAttr.tname, prevAttr.accessModifierType);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            super.setValueAt(aValue, rowIndex, columnIndex);
        }
    }
    
    public void load(KrnMethod method) {
        try {
            classBrowser.classTree.setSelectedPath(Kernel.instance().getClassNode(method.classId));
            classBrowser.attrTree.setSelectedPath(method);
            classBrowser.showAttrtreeNodeProperties();
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }
    
    public void load(String uid, int ownerType, int triggerType) {
        try {
        	Kernel krn = Kernel.instance();
        	if (ownerType == 0) {
        		KrnClass cls = krn.getClassByUid(uid);
        		classBrowser.classTree.setSelectedPath(krn.getClassNode(cls.id));
        		classBrowser.setClsTriggerEventExpression(triggerType);
        	} else {
        		KrnAttribute attr = krn.getAttributeByUid(uid);
                classBrowser.classTree.setSelectedPath(krn.getClassNode(attr.classId));
                classBrowser.attrTree.setSelectedPath(attr);
        		classBrowser.setAttrTriggerEventExpression(triggerType);
        	}
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }
    
    public void load(KrnObject object, String attrID) {
    	try {
    		classBrowser.classTree.setSelectedPath(Kernel.instance().getClassNode(object.classId));
    		Container cont = classBrowser.getTopLevelAncestor();
    		DesignerDialog dlg;
    		KrnClass cls = classBrowser.classTree.getSelectedClass();
            ObjectBrowser ob = new ObjectBrowser(cls, false);
            ob.setSelectedObject(object, attrID);
            if (cont instanceof Dialog) {
                dlg = new DesignerDialog((Dialog)cont,
                        "Объекты класса [" + cls.name + "]", ob);
            } else {
                dlg = new DesignerDialog((Frame)cont,
                        "Объекты класса [" + cls.name + "]", ob);
            }
            dlg.show();
    	} catch (KrnException e) {
    		e.printStackTrace();
    	}
    }

    public void load(KrnClass cls) {
    	try {
    		classBrowser.classTree.setSelectedPath(Kernel.instance().getClassNode(cls.id));
    		classBrowser.clsPropBtn.doClick();
    	} catch (KrnException e) {
    		e.printStackTrace();
    	}
    }

    public void load(KrnAttribute attr) {
    	try {
            classBrowser.classTree.setSelectedPath(Kernel.instance().getClassNode(attr.classId));
    		classBrowser.attrTree.setSelectedPath(attr);
    		classBrowser.attrPropBtn.doClick();
    	} catch (KrnException e) {
    		e.printStackTrace();
    	}
    }

    class PropertyCellRenderer implements TableCellRenderer {

        private JLabel lab = Utils.createLabel("");
        private JCheckBox checkBox = Utils.createCheckBox("", false);

        public PropertyCellRenderer() {
            checkBox.setOpaque(false);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected,
                                                       boolean hasFocus, int row,
                                                       int column) {

            if (value != null && value instanceof PropertyRecord) {
                PropertyRecord pr = (PropertyRecord)value;
                if (pr.propertyType == Types.STRING||pr.propertyType == Types.VIEW_STRING) {
                    lab.setText(value.toString());
                    return lab;
                } else if (pr.propertyType == Types.ENUM) {
                    int v = (new Integer(value.toString())).intValue();
                    for (int i = 0; i < copyEnum.length; i++) {
                        EnumValue enumValue = copyEnum[i];
                        if (enumValue.code == v) {
                            lab.setText(enumValue.name);
                        }
                    }
                    return lab;
                } else if (pr.propertyType == Types.BOOLEAN) {
                    checkBox.setSelected(((Boolean)pr.propertyVal).booleanValue());
                    return checkBox;
                } else {
                    lab.setText(value.toString());
                    return lab;
                }
            }
            return null;
        }
    }

    class PropertyCellEditor extends DefaultCellEditor {

        private JTextField field = Utils.createDesignerTextField();
        private JComboBox enumField = Utils.createCombo();
        private JCheckBox checkBox = Utils.createCheckBox("", false);
        private PropertyRecord val = null;

        public PropertyCellEditor() {
            super(new JTextField());
            checkBox.setOpaque(false);
            checkBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    stopCellEditing();
                }
            });
            if (enumField.getItemCount() <= 0) {
                for (int i = 0; i < copyEnum.length; i++) {
                    EnumValue enumValue = copyEnum[i];
                    enumField.addItem(enumValue);
                }
            }
            enumField.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    stopCellEditing();
                }
            });
        }

        public Object getCellEditorValue() {
            if (val != null) {
                if (val.propertyType == Types.STRING || val.propertyType == Types.VIEW_STRING) {
                    val.propertyVal = field.getText();
                } else if (val.propertyType == Types.ENUM) {
                    val.propertyVal = new Integer(
                            ((EnumValue)enumField.getSelectedItem()).code);
                } else if (val.propertyType == Types.BOOLEAN) {
                    val.propertyVal = new Boolean(checkBox.isSelected());
                }
                return val;
            }
            return super.getCellEditorValue();
        }


        public Component getTableCellEditorComponent(JTable table, Object value,
                                                       boolean isSelected,
                                                       int row,
                                                       int column) {

            if (value != null && value instanceof PropertyRecord) {
                PropertyRecord pr = (PropertyRecord)value;
                val = pr;
                if (pr.propertyType == Types.STRING||pr.propertyType == Types.VIEW_STRING) {
                    field.setText(value.toString());
                    return field;
                } else if (pr.propertyType == Types.ENUM) {
                    int v = (new Integer(value.toString())).intValue();
                    for (int i = 0; i < enumField.getItemCount(); i++) {
                        EnumValue enumValue = (EnumValue)enumField.getItemAt(i);
                        if (v == enumValue.code) {
                            enumField.setSelectedIndex(i);
                            break;
                        }
                    }
                    return enumField;
                } else if (pr.propertyType==Types.BOOLEAN) {
                    checkBox.setSelected(((Boolean)pr.propertyVal).booleanValue());
                    return checkBox;
                } else {
                    field.setText(value.toString());
                    return field;
                }
            }
            return null;
        }
    }

    public void updateStatusBar() {
        dsLabel.setText(Or3Frame.getBaseName());
        serverLabel.setText(Or3Frame.getServerType());
        currentDbName.setText(Or3Frame.getCurrentDbName());
        currentUserLable.setText(Or3Frame.getCurrentUserName());
    }
    public ClassBrowser getClassBrouser(){
    	return classBrowser;
    }

}
