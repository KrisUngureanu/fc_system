package kz.tamur.admin;

import java.awt.*;
import javax.swing.*;
import javax.swing.tree.TreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.event.*;
import java.util.List;

import com.cifs.or2.kernel.*;
import com.cifs.or2.client.*;
import kz.tamur.rt.Utils;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.rt.MainFrame;

public class RAttrPropPanel extends JPanel implements ActionListener {
    
	private static final Kernel krn_ = Kernel.instance();

    JTextField attrNameField = Utils.createDesignerTextField();//new JTextField();
    
    JTextField classNameField = Utils.createDesignerTextField();//new JTextField();
    JButton selectClassBtn = ButtonsFactory.createToolButton("editor", "", true);

    JTextField typeNameField = Utils.createDesignerTextField();//new JTextField();
    JButton selectTypeBtn = ButtonsFactory.createToolButton("editor", "", true);

    private KrnAttribute attr_;
    private KrnClass cls_;
    private KrnClass type_;

    JPanel jPanel1 = new JPanel();
    BorderLayout borderLayout1 = new BorderLayout();

    Container cont = null;

    private ClassNode lastSelectedClass;
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    
    public RAttrPropPanel(KrnClass cls,
                         KrnAttribute attr,
                         KrnClass type) {
        cls_ = cls;
        attr_ = attr;
        type_ = type;
        try {
            jbInit();

            if (attr != null) {
                populateRevAttributes();
            }
            classNameField.setText(cls.name);
            attrNameField.setText((attr_ == null) ? "NewAttribute" : attr_.name);
            typeNameField.setText((type_ == null) ? null : type_.name);
            if (type_ != null) {
                lastSelectedClass = Kernel.instance().getClassNodeByName(type_.name);
            }
            long flag = (attr_ != null) ? attr_.flags : 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public KrnClass getAttrClass() {
        return cls_;
    }

    public String getAttrName() {
        return attrNameField.getText();
    }

    public KrnClass getType() {
        return type_;
    }

    // implemnting ActionListener interface
    public void actionPerformed(ActionEvent e) {
        try {
            final Object src = e.getSource();
            if (src == selectTypeBtn) {
                KrnClass cls = selectClass("Выберите тип атрибута");
                if (cls != null) {
                    type_ = cls;
                    lastSelectedClass = Kernel.instance().getClassNode(type_.id);
                    typeNameField.setText(type_.name);
                    populateRevAttributes();
                }
            } else if (src == selectClassBtn) {
                KrnClass cls = selectClass("Выберите класс атрибута");
                if (cls != null) {
                    cls_ = cls;
                    classNameField.setText(cls_.name);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void populateRevAttributes() throws KrnException {
        if (type_ != null) {
            List<KrnAttribute> attrs = krn_.getAttributes(type_);
            AttrItem[] items = new AttrItem[attrs.size()];
            for (int i = 0; i < attrs.size(); i++) {
                items[i] = new AttrItem(attrs.get(i));
            }
        } else {
        }
    }

    private KrnClass selectClass(String title) {
        ClassTree classTree = new ClassTree();
        JScrollPane sp = new JScrollPane(classTree);
        sp.setPreferredSize(new Dimension(600, 400));
        sp.setOpaque(isOpaque);
        sp.getViewport().setOpaque(isOpaque);
        DesignerDialog dlg = null;
        if (cont instanceof Dialog) {
            dlg = new DesignerDialog((Dialog)cont, title, sp);
        } else {
            dlg = new DesignerDialog((Frame)cont, title, sp);
        }
        try {
            if (lastSelectedClass != null) {
                TreeNode root = Kernel.instance().getClassNodeByName("Объект");
                classTree.setModel(new DefaultTreeModel(root));
                classTree.setSelectedPath(lastSelectedClass);
            } else {
                TreeNode root = Kernel.instance().getClassNodeByName("Объект");
                classTree.setModel(new DefaultTreeModel(root));
            }
        } catch (KrnException e) {
            e.printStackTrace();
        }

        dlg.show();
        if (dlg.isOK()) {
            KrnClass cls = classTree.getSelectedClass();
            return cls;
        }
        return null;
    }

    private void jbInit() throws Exception {
        cont = getTopLevelAncestor();
        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(500, 400));

        JLabel lb = Utils.createLabel("Наименование");
        lb.setHorizontalAlignment(SwingConstants.RIGHT);
        add(lb, new GridBagConstraints(0, 0, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 5, 5, 0), 0, 0));
        add(attrNameField, new GridBagConstraints(1, 0, 3, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 10, 5, 5), 0, 0));

        lb = Utils.createLabel("Класс");
        lb.setHorizontalAlignment(SwingConstants.RIGHT);
        add(lb, new GridBagConstraints(0, 1, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 5, 5, 0), 0, 0));
        add(classNameField, new GridBagConstraints(1, 1, 2, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 10, 5, 0), 0, 0));
        add(selectClassBtn, new GridBagConstraints(3, 1, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 10, 5, 5), 0, 0));
        lb = Utils.createLabel("Тип");
        lb.setHorizontalAlignment(SwingConstants.RIGHT);
        add(lb, new GridBagConstraints(0, 2, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 5, 5, 0), 0, 0));
        add(typeNameField, new GridBagConstraints(1, 2, 2, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 10, 5, 0), 0, 0));
        add(selectTypeBtn, new GridBagConstraints(3, 2, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 10, 5, 5), 0, 0));

        JPanel pan = new JPanel(new GridBagLayout());
        Border bord = BorderFactory.createLineBorder(Utils.getMidSysColor());
        Border titleBorder = Utils.createTitledBorder(bord, "Свойства");
        pan.setBorder(titleBorder);
        add(pan, new GridBagConstraints(0, 3, 4, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 5, 5, 5), 0, 0));

        classNameField.setEditable(false);
        typeNameField.setEditable(false);

        selectTypeBtn.addActionListener(this);
        selectClassBtn.addActionListener(this);
    }

    private static class AttrItem {
        private KrnAttribute attr;

        public AttrItem(KrnAttribute attr) {
            this.attr = attr;
        }

        public KrnAttribute getKrnAttribute() {
            return attr;
        }

        public String toString() {
            return attr.name;
        }

        public boolean equals(Object obj) {
            if (obj instanceof AttrItem) {
                return (attr.id == ((AttrItem) obj).attr.id);
            }
            return false;
        }
    }

    public ClassNode getLastSelectedClass() {
        return lastSelectedClass;
    }

    public void setLastSelectedClass(ClassNode lastSelectedClass) {
        if (this.lastSelectedClass == null) {
            this.lastSelectedClass = lastSelectedClass;
        }
    }

    public String getEmptyMessage() {
        StringBuffer sb = new StringBuffer("");
        if (attrNameField.getText().length() == 0 ||
                "".equals(attrNameField.getText()) ||
                attrNameField.getText() == null) {
            sb.append("Не заполнено имя атрибута!\n");
        }
        if (classNameField.getText().length() == 0 ||
                "".equals(classNameField.getText()) ||
                classNameField.getText() == null) {
            sb.append("Не заполнено имя класса атрибута!\n");
        }
        if (typeNameField.getText().length() == 0 ||
                "".equals(typeNameField.getText()) ||
                typeNameField.getText() == null) {
            sb.append("Не заполнен тип атрибута!\n");
        }
        return sb.toString();
    }

    class CollectionType {
        int col_type;
        String col_name;

        public CollectionType(int col_type, String col_name) {
            this.col_type = col_type;
            this.col_name = col_name;
        }

        public String toString() {
            return col_name;
        }
    }

}