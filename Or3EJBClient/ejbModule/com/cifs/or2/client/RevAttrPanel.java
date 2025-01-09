package com.cifs.or2.client;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.EAST;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.WEST;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import kz.tamur.admin.ClassTree;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.or3.client.util.GuiUtil;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.Utils;
import kz.tamur.util.Funcs;
import kz.tamur.util.KrnAttributeItem;

import com.cifs.or2.client.ClassNode;
import com.cifs.or2.client.util.CnrBuilder;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;

@SuppressWarnings({"rawtypes", "unchecked"})
public class RevAttrPanel extends JPanel implements ActionListener, ItemListener, DocumentListener {
	
	private final Kernel kernel = Kernel.instance();

	private JLabel nameLabel = Utils.createLabel("Наименование");
	private JLabel classLabel = Utils.createLabel("Класс");
	private JLabel typeLabel = Utils.createLabel("Тип");
	private JLabel revAttrLabel = Utils.createLabel("Обратный атрибут");
	private JLabel colTypeLabel = Utils.createLabel("Коллекция");
	private JLabel sortAttrLabel = Utils.createLabel("Сортировка");
	private JLabel idAttrLabel = Utils.createLabel();
	private JTextField nameField = Utils.createDesignerTextField();
	private JTextField classField = Utils.createDesignerTextField();
	private JTextField typeField = Utils.createDesignerTextField();;
	private JTextField idField = Utils.createDesignerTextField();
	private JComboBox revAttrCombo = Utils.createCombo();
	private JComboBox colTypeCombo = Utils.createCombo();
	private JComboBox sortAttrCombo = Utils.createCombo();
	private JCheckBox sortDescCheck = Utils.createCheckBox("По убыванию", false);
	private JCheckBox aggregateCheck = Utils.createCheckBox("Агрегация", false);
	
	//private JLabel tnameLabel = Utils.createLabel("Имя таблици/столбца");
	private JTextField tnameField = Utils.createDesignerTextField();

	private JTextArea commentArea = new JTextArea();
	
	private JButton typeButton = ButtonsFactory.createToolButton("editor", "", true);

	private ClassNode cls;
	private ClassNode type;
	private ClassNode lastSelectedClass;
	
	private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
	
    private Version version;
	
	public RevAttrPanel(ClassNode cls, KrnAttribute attr, ClassNode lastSelectedClass, String comment) throws KrnException {
		jbInit();
		this.cls = cls;
		classField.setText(cls.getKrnClass().name);
		this.lastSelectedClass = lastSelectedClass;
		if (attr != null) {
			nameField.setText(attr.name);
			tnameField.setText(kz.tamur.or3.util.Tname.getAttrTableName(attr));
			type = kernel.getClassNode(attr.typeClassId);
			typeField.setText(type.getKrnClass().name);
			populateCombos();
			if (attr.rAttrId != 0) {
				revAttrCombo.setSelectedItem(new KrnAttributeItem(kernel.getAttributeById(attr.rAttrId)));
			}
			if (attr.sAttrId != 0) {
				sortAttrCombo.setSelectedItem(new KrnAttributeItem(kernel.getAttributeById(attr.sAttrId)));
			} else {
				sortDescCheck.setEnabled(false);
			}
			sortDescCheck.setSelected(attr.sDesc);
			if (attr.collectionType == 2) {
				colTypeCombo.setSelectedIndex(1);
			} else {
				colTypeCombo.setSelectedIndex(attr.collectionType);
			}
			aggregateCheck.setSelected(attr.isAggregate());
			idField.setText(" ID=" + attr.id + "  UID=" + attr.uid);
			commentArea.setText(comment);
			nameField.getDocument().addDocumentListener(this);
			tnameField.getDocument().addDocumentListener(this);
			typeField.getDocument().addDocumentListener(this);
			revAttrCombo.addItemListener(this);
			colTypeCombo.addItemListener(this);
			sortAttrCombo.addItemListener(this);
			sortDescCheck.addItemListener(this);
			aggregateCheck.addItemListener(this);
			commentArea.getDocument().addDocumentListener(this);
			version = new Version(nameField.getText(), classField.getText(), typeField.getText(), revAttrCombo.getSelectedIndex(), colTypeCombo.getSelectedIndex(),
					sortAttrCombo.getSelectedIndex(), sortDescCheck.isSelected(), aggregateCheck.isSelected(), commentArea.getText(), tnameField.getText());
		}
	}
	
	public void changedUpdate(DocumentEvent e) {}
	
    public void removeUpdate(DocumentEvent e) {
		checkForModification();
	}
	
	public void insertUpdate(DocumentEvent e) {
		checkForModification();
	}
	
	private void checkForModification() {
    	DesignerDialog dialog = (DesignerDialog) getTopLevelAncestor();
    	if (version.equals(new Version(Funcs.normalizeInput(nameField.getText()), Funcs.normalizeInput(classField.getText()), Funcs.normalizeInput(typeField.getText()),
    			revAttrCombo.getSelectedIndex(), colTypeCombo.getSelectedIndex(), sortAttrCombo.getSelectedIndex(), sortDescCheck.isSelected(), aggregateCheck.isSelected(),
    			Funcs.normalizeInput(commentArea.getText()), Funcs.normalizeInput(tnameField.getText())))) {
            dialog.setOkEnabled(false);
    	} else {
            dialog.setOkEnabled(true);
    	}
    }
	
	private void populateCombos() throws KrnException {
		if (type == null) {
			revAttrCombo.removeAllItems();
			sortAttrCombo.removeAllItems();
		} else {
			List<ClassNode> supers = new ArrayList<ClassNode>();
			cls.getSuperClasses(supers);
			Set<Long> superIds = new HashSet<Long>();
			for (ClassNode cn : supers) {
				superIds.add(cn.getKrnClass().id);
			}
			sortAttrCombo.addItem(null);
			List<KrnAttribute> attrs = type.getAttributes();
			Collections.sort(attrs, new Comparator<KrnAttribute>() {
				public int compare(KrnAttribute attribute_1, KrnAttribute attribute_2) {
				    return attribute_1.name.compareTo(attribute_2.name);
				}
			});
			for (KrnAttribute attr : attrs) {
				KrnAttributeItem item = new KrnAttributeItem(attr);
				if (superIds.contains(attr.typeClassId)) {
					revAttrCombo.addItem(item);
				}
				sortAttrCombo.addItem(item);
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		if (src == typeButton) {
			try {
	            ClassNode cnode = selectClass("Выберите тип атрибута");
	            if (cnode != null) {
	            	lastSelectedClass = cnode;
	                type = cnode;
	                typeField.setText(type.getKrnClass().name);
	                populateCombos();
	            }
			} catch (KrnException ex) {
	            MessagesFactory.showMessageDialog(getTopLevelAncestor(), MessagesFactory.ERROR_MESSAGE, ex.getMessage());
			}
		}
	}

	public void itemStateChanged(ItemEvent e) {
		Object source = e.getSource();
		if (source == sortAttrCombo) {
			boolean isEnable = e.getStateChange() == ItemEvent.SELECTED ? e.getItem() != null : false;
			sortDescCheck.setEnabled(isEnable);
			checkForModification();
		} else if (source == revAttrCombo) {
			checkForModification();
		} else if (source == colTypeCombo) {
			checkForModification();
		} else if (source == sortDescCheck) {
			checkForModification();
		} else if (source == aggregateCheck) {
			checkForModification();
		}
	}
	
    private ClassNode selectClass(String title) {
        ClassTree classTree = new ClassTree();
        JScrollPane sp = new JScrollPane(classTree);
        sp.setPreferredSize(new Dimension(600, 400));
        DesignerDialog dlg = GuiUtil.createDesignerDialog(
        	getTopLevelAncestor(), title, sp);
        try {
            TreeNode root = Kernel.instance().getClassNodeByName("Объект");
            classTree.setModel(new DefaultTreeModel(root));
            if (type != null) {
            	classTree.setSelectedPath(type);
            }
        } catch (KrnException e) {
            e.printStackTrace();
        }

        dlg.show();
        if (dlg.isOK()) {
            return classTree.getSelectedNode();
        }
        return null;
    }

    public String getErrorMessage() {
        StringBuffer sb = new StringBuffer("");
        String str = nameField.getText();
        if (str == null || str.length() == 0) {
            sb.append("Не заполнено имя обратного атрибута!\n");
        }
        if (type == null) {
            sb.append("Не задан тип обратного атрибута!\n");
        }
        Object item = revAttrCombo.getSelectedItem();
        if (item == null) {
            sb.append("Не задан обратный атрибут!\n");
        }
        return sb.toString();
    }
    
    public String getName() {
    	return Funcs.normalizeInput(nameField.getText());
    }
    
    public ClassNode getType() {
    	return type;
    }

    public KrnAttribute getRevAttribute() {
    	KrnAttributeItem item = (KrnAttributeItem)revAttrCombo.getSelectedItem();
    	return (item != null) ? item.attr : null;
    }

    public int getColType() {
    	int ret = colTypeCombo.getSelectedIndex();
    	if(ret == 1){
    		ret = 2;
    	}
    	return ret;
    }

    public KrnAttribute getSortAttribute() {
    	KrnAttributeItem item = (KrnAttributeItem)sortAttrCombo.getSelectedItem();
    	return (item != null) ? item.attr : null;
    }
    
    public boolean isSortDesc() {
    	return sortDescCheck.isSelected();
    }
    
    public String getComment() {
    	return Funcs.normalizeInput(commentArea.getText());
    }
    
    public ClassNode getLastSelectedClass() {
    	return lastSelectedClass;
    }
    
    public boolean isAggregate() {
    	return aggregateCheck.isSelected();
    }

	private void jbInit() {
		setLayout(new GridBagLayout());
		idAttrLabel.setIcon(kz.tamur.admin.AttributeTreeIconLoader.getIcon(false, false, false, false, true, false));
		int y = 1;

		add(nameLabel, new CnrBuilder().anchor(EAST).ins(10, 5, 0, 5).build());
		add(nameField, new CnrBuilder().x(1).w(2).wtx(1).fill(HORIZONTAL).ins(10,0,0,5).build()); 
		
//		add(tnameLabel, new CnrBuilder().y(y).anchor(EAST).ins(10, 5, 0, 5).build());
//		add(tnameField, new CnrBuilder().y(y).x(1).w(2).wtx(1).fill(HORIZONTAL).ins(10,0,0,5).build()); 
//		y++;
		add(classLabel, new CnrBuilder().y(y).anchor(EAST).ins(5, 5, 0, 5).build());
		classField.setEditable(false);
		add(classField, new CnrBuilder().x(1).y(y).w(2).wtx(1).fill(HORIZONTAL).ins(5, 0, 0, 5).build());
		y++;
		add(typeLabel, new CnrBuilder().y(y).anchor(EAST).ins(10, 5, 0, 5).build());
		typeField.setEditable(false);
		add(typeField, new CnrBuilder().x(1).y(y).wtx(1).fill(HORIZONTAL).ins(10,0,0,5).build());
		typeButton.addActionListener(this);
		add(typeButton, new CnrBuilder().x(2).y(y).ins(10,0,0,5).build());
		y++;
		add(revAttrLabel, new CnrBuilder().y(y).anchor(EAST).ins(10, 5, 0, 5).build());
		add(revAttrCombo, new CnrBuilder().x(1).y(y).w(3).wtx(1).fill(HORIZONTAL).ins(10, 0, 0, 5).build());
		y++;
		add(colTypeLabel, new CnrBuilder().y(y).anchor(EAST).ins(5, 5, 0, 5).build());
		colTypeCombo.addItem("Нет");
		colTypeCombo.addItem("Набор");
		add(colTypeCombo, new CnrBuilder().x(1).y(y).w(2).wtx(1).fill(HORIZONTAL).ins(5, 0, 0, 5).build());
		y++;
		add(sortAttrLabel, new CnrBuilder().y(y).anchor(EAST).ins(10, 5, 0, 5).build());
		add(sortAttrCombo, new CnrBuilder().x(1).y(y).w(2).wtx(1).fill(HORIZONTAL).ins(10, 0, 0, 5).build());
		y++;
		add(sortDescCheck, new CnrBuilder().x(1).y(y).anchor(WEST).build());
		y++;
		add(aggregateCheck, new CnrBuilder().x(1).y(y).anchor(WEST).build());
		y++;
		add(idAttrLabel,new CnrBuilder().y(y).anchor(EAST).ins(10, 5, 0, 5).build());
		idField.setEditable(false);
		add(idField, new CnrBuilder().x(1).y(y).w(2).wtx(1).fill(HORIZONTAL).ins(10, 0, 0, 5).build());
		y++;
		JScrollPane commentScroll = new JScrollPane(commentArea);
        commentScroll.setPreferredSize(new Dimension(500, 150));
		add(commentScroll, new CnrBuilder().y(y).w(3).wtx(1).wty(1).fill(BOTH).ins(10, 5, 0, 5).build());
		y++;
		setOpaque(isOpaque);
		commentScroll.setOpaque(isOpaque);
		commentScroll.getViewport().setOpaque(isOpaque);
		aggregateCheck.setOpaque(isOpaque);
		sortDescCheck.setOpaque(isOpaque);
	}
	
	private class Version {
    	private String attributeName;
    	private String className;
    	private String attributeType;
    	private int reverseAttribute;
    	private int arrayType;
    	private int sorting;
    	private boolean isDescending;
    	private boolean isAggregate;
    	private String comment;
    	private String tname;

    	public Version(String attributeName, String className, String attributeType, int reverseAttribute, 
    			int arrayType, int sorting, boolean isDescending, boolean isAggregate, String comment, String tname) {
    		this.attributeName = attributeName;
    		this.className = className;
    		this.attributeType = attributeType;
    		this.reverseAttribute = reverseAttribute;
    		this.sorting = sorting;
    		this.arrayType = arrayType;
    		this.sorting = sorting;
    		this.isDescending = isDescending;
    		this.isAggregate = isAggregate;
    		this.comment = comment;
    		this.tname = tname;
    	}
    	
		public String getAttributeName() {
			return attributeName;
		}

		public String getClassName() {
			return className;
		}
		
		public String getTName() {
			return tname;
		}

		public String getAttributeType() {
			return attributeType;
		}

		public int getReverseAttribute() {
			return reverseAttribute;
		}

		public int getArrayType() {
			return arrayType;
		}

		public int getSorting() {
			return sorting;
		}

		public boolean isDescending() {
			return isDescending;
		}

		public boolean isAggregate() {
			return isAggregate;
		}

		public String getComment() {
			return comment;
		}

		public boolean equals(Version version) {
			if (!attributeName.equals(version.getAttributeName())) {
				return false;
			}
    		if (!className.equals(version.getClassName())) {
    			return false;
    		}
    		if (!tname.equals(version.getTName())) {
    			return false;
    		}
    		if (!attributeType.equals(version.getAttributeType())) {
    			return false;
    		}
    		if (reverseAttribute != version.getReverseAttribute()) {
    			return false;
    		}
    		if (arrayType != version.getArrayType()) {
    			return false;
    		} 
    		if (sorting != version.getSorting()) {
    			return false;
    		}
    		if (isDescending != version.isDescending()) {
    			return false;
    		}
    		if (isAggregate != version.isAggregate()) {
    			return false;
    		}
    		if (!comment.equals(version.getComment())) {
    			return false;
    		}
    		return true;
    	}
    }
}