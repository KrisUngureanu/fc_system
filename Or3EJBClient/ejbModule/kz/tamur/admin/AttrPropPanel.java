package kz.tamur.admin;

import static kz.tamur.rt.Utils.createCheckBox;
import static kz.tamur.rt.Utils.createCombo;
import static kz.tamur.rt.Utils.createDesignerTextField;
import static kz.tamur.rt.Utils.createLabel;
import static kz.tamur.rt.Utils.getMidSysColor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import kz.tamur.comps.Constants;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.rt.MainFrame;
import kz.tamur.util.ExpressionEditor;
import kz.tamur.util.Funcs;

import com.cifs.or2.client.ClassNode;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;

public class AttrPropPanel extends JPanel implements ItemListener, ActionListener, DocumentListener {
    
	private static final Kernel kernel = Kernel.instance();

    private JTextField attrNameField = createDesignerTextField();
    private JTextField classNameField = createDesignerTextField();
    private JTextField attrSize = createDesignerTextField();
    private JTextField attrIdField = createDesignerTextField();
    private JTextField typeNameField = createDesignerTextField();
    
    private JTextField attrTNameField = createDesignerTextField();

    private JCheckBox isMultiLangCheck = createCheckBox("Мультиязычный", false);
    private JCheckBox isIndexCheck = createCheckBox("Индексируемый", false);
    private JCheckBox isUniqueCheck = createCheckBox("Уникальный", false);
    private JCheckBox isReplCheck = createCheckBox("Реплицировать", false);
    private JCheckBox isMandatoryCheck = createCheckBox("Обязательный", false);
    private JCheckBox isAggregateCheck = createCheckBox("Агрегация", false);
    private JCheckBox isFullTextCheck = createCheckBox("Полнотекстовый", false);
    private JCheckBox isGroupCheck = createCheckBox("Групповой", false);
    private JCheckBox isEncryptCheck = createCheckBox("Зашифрованный", false);
    private JComboBox arrayTypeCombo = createCombo();
    private JComboBox accessModifiersCombo = createCombo();
    
    private JTextArea commentArea = new JTextArea();
    private JTextArea exprField = new JTextArea();

    private JList revAttrs = new JList();
    private JButton addRevAttrBtn = ButtonsFactory.createToolButton("plus", "Добавить обратный атрибут", true);
    private JButton delRevAttrBtn = ButtonsFactory.createToolButton("minus", "Удалить обратный атрибут", true);
    private JButton fnBtn = ButtonsFactory.createToolButton("func", "Мастер выражений", true);
    private JButton selectTypeBtn = ButtonsFactory.createToolButton("editor", "", true);
    private JButton selectClassBtn = ButtonsFactory.createToolButton("editor", "", true);
    
    private KrnAttribute attr_;
    private KrnClass type_;
    private KrnClass cls_;

    private Container cont = null;
    
    private ClassNode lastSelectedClass;
    
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    
    private Version version;
    private boolean isExistingAttribute = true;
    
    private Map<JTextField, Filtr> Filtrs = new HashMap<JTextField, Filtr>();
    private Font FieldFont = new Font(attrTNameField.getFont().getName(), attrTNameField.getFont().getStyle(), attrTNameField.getFont().getSize());
    private Font FieldFontErr = new Font(attrTNameField.getFont().getName(), Font.ITALIC, attrTNameField.getFont().getSize());
    boolean isTnameOk = true;
    
    private class Filtr{
    	public final String filtr;
    	public ArrayList<String> NotText = new ArrayList<String>();
    	public boolean check = true;
    	
    	public Filtr(final String _filtr) {
    		this.filtr = _filtr;
		}
    }
    
    public AttrPropPanel(KrnClass cls, KrnAttribute attr, KrnClass type, String comment) {
    	cls_ = cls;
        type_ = type;
        if (attr != null)
        	attr_ = new KrnAttribute(attr);
        else {
        	attr_ = new KrnAttribute();
        	attr_.name = "Новый атрибут";
        	if (cls != null)
        		attr_.classId = cls.id;
        	if (type != null)
        		attr_.typeClassId = type.id;
        	isExistingAttribute = false;
        }
        try {
            jbInit();
            Filtrs.put(attrTNameField, new Filtr("^[a-zA-Z][a-zA-Z0-9\\u005F\\-]{1,28}[a-zA-Z0-9]$"));
            if (attr != null) {
                populateRevAttributes();
            }
            arrayTypeCombo.addItem(new CollectionType(0, "Нет"));
            arrayTypeCombo.addItem(new CollectionType(1, "Массив"));
            arrayTypeCombo.addItem(new CollectionType(2, "Набор"));
            arrayTypeCombo.addActionListener(this);
            
            accessModifiersCombo.addItem(new AccessModifierType(0, "Public"));
            accessModifiersCombo.addItem(new AccessModifierType(1, "Protected"));
            accessModifiersCombo.addItem(new AccessModifierType(2, "Private"));
            accessModifiersCombo.addActionListener(this);
            
            classNameField.setText(cls.name);
            attrNameField.setDocument(new LowDocument());
            attrNameField.setText(attr_.name);
//            if (cls != null){
//            	if (kz.tamur.or3.util.Tname.isParentSytemClass(cls)) {
//            		attrNameField.enable(false);
//            	}
//            }
            String tmp_tname = (attr != null && attr.tname != null) ? attr.tname.trim() : ""; 
            if (attr_.sAttrId != 0){
            	attrTNameField.setEnabled(false);
            }
            attrTNameField.setDocument(new LowDocument());
            attrTNameField.setText(tmp_tname);
            attrTNameField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent evt) {
                	PatternFiltr();
                }
            });
            attrSize.setText((attr != null) ? String.valueOf(attr_.size) : "0");
            typeNameField.setText((type_ == null) ? null : type_.name);
            if (type_ != null) {
                lastSelectedClass = Kernel.instance().getClassNodeByName(type_.name);
            }
            if(attr_.id != 0){
            	attrIdField.setText(" ID=" + attr_.id + "  UID=" + attr_.uid);
            }            
            attrIdField.setEditable(false);
            arrayTypeCombo.setSelectedIndex(attr_.collectionType);
            accessModifiersCombo.setSelectedIndex(attr_.accessModifierType);
            isMandatoryCheck.setSelected(attr_.isMandatory());
            isUniqueCheck.setSelected(attr_.isUnique);
            isMultiLangCheck.setSelected(attr_.isMultilingual);
            isIndexCheck.setSelected(attr_.isIndexed);
            isReplCheck.setSelected(attr_.isRepl);
            isAggregateCheck.setSelected(attr_.isAggregate());
            isFullTextCheck.setSelected(attr_.isIndexed?attr_.isFullText():false);
            isFullTextCheck.setEnabled(attr_.isIndexed);
            isGroupCheck.setSelected(attr_.isGroup());
            commentArea.setText(comment);
            isEncryptCheck.setSelected(attr_.isEncrypt);
            
            isMandatoryCheck.addItemListener(this);
        	isUniqueCheck.addItemListener(this);
        	isIndexCheck.addItemListener(this);
        	isMultiLangCheck.addItemListener(this);
        	isReplCheck.addItemListener(this);
        	isAggregateCheck.addItemListener(this);
        	isFullTextCheck.addItemListener(this);
            isGroupCheck.addItemListener(this);
            isEncryptCheck.addItemListener(this);
            if (isExistingAttribute) {
            	attrNameField.getDocument().addDocumentListener(this);
            	attrTNameField.getDocument().addDocumentListener(this);
            	classNameField.getDocument().addDocumentListener(this);
            	typeNameField.getDocument().addDocumentListener(this);
            	attrSize.getDocument().addDocumentListener(this);
            	arrayTypeCombo.addItemListener(this);
            	accessModifiersCombo.addItemListener(this);
            	commentArea.getDocument().addDocumentListener(this);
            	revAttrs.getModel().addListDataListener(new ListDataListener() {
					public void intervalRemoved(ListDataEvent e) {}
					public void intervalAdded(ListDataEvent e) {}
					
					public void contentsChanged(ListDataEvent e) {
						checkForModification();
					}
				});
            	List<Object> reverseAttributes = new ArrayList<Object>();
            	
            	int count = revAttrs.getModel().getSize();
            	count = Funcs.checkInt(count, 500);
            	
            	for (int i = 0; i < count; i ++) {
            		reverseAttributes.add(revAttrs.getModel().getElementAt(i));
            	}
            	version = new Version(attrNameField.getText(), classNameField.getText(), typeNameField.getText(), attrSize.getText(), arrayTypeCombo.getSelectedIndex(),
                		isMandatoryCheck.isSelected(), isUniqueCheck.isSelected(), isIndexCheck.isSelected(), isMultiLangCheck.isSelected(), isReplCheck.isSelected(),
                		isAggregateCheck.isSelected(), isFullTextCheck.isSelected(), isGroupCheck.isSelected(), commentArea.getText(), reverseAttributes, attrTNameField.getText(), accessModifiersCombo.getSelectedIndex(), isEncryptCheck.isSelected());
            }
        } catch (Exception e) {
            e.printStackTrace();
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
    	List<Object> reverseAttributes = new ArrayList<Object>();
    	for (int i = 0; i < revAttrs.getModel().getSize(); i ++) {
    		reverseAttributes.add(revAttrs.getModel().getElementAt(i));
    	}
    	if (version.equals(new Version(Funcs.normalizeInput(attrNameField.getText()), Funcs.normalizeInput(classNameField.getText()), Funcs.normalizeInput(typeNameField.getText()),
    			Funcs.normalizeInput(attrSize.getText()), arrayTypeCombo.getSelectedIndex(), isMandatoryCheck.isSelected(), isUniqueCheck.isSelected(),
    			isIndexCheck.isSelected(), isMultiLangCheck.isSelected(), isReplCheck.isSelected(), isAggregateCheck.isSelected(), isFullTextCheck.isSelected(),
    			isGroupCheck.isSelected(), Funcs.normalizeInput(commentArea.getText()), reverseAttributes, Funcs.normalizeInput(attrTNameField.getText()), accessModifiersCombo.getSelectedIndex(), isEncryptCheck.isSelected()))) {
            dialog.setOkEnabled(false);
    	} else {
            dialog.setOkEnabled(true);
    	}
    }
    
    public KrnClass getAttrClass() {
        return cls_;
    }

    public String getAttrName() {
        return Funcs.normalizeInput(attrNameField.getText());
    }
    
    public String getAttrTname() {
    	String newTname = Funcs.normalizeInput(attrTNameField.getText()).trim().toUpperCase(Constants.OK);
    	if (attr_.tname == null){
    		return ((newTname.length() == 0) ? null : newTname);
    	} else if (!newTname.equals(attr_.tname)){
    		return ((newTname.length() == 0) ? null : newTname);
    	}
    	return attr_.tname;
    }

    public boolean isIndexed() {
        return attr_.isIndexed;
    }


    public boolean isMultiLang() {
        return attr_.isMultilingual;
    }

    public KrnClass getType() {
        return type_;
    }

    public int collType() {
        return attr_.collectionType;
    }

    public int getAccessModifierType() {
        return attr_.accessModifierType;
    }
    
    public boolean isUnique() {
        return attr_.isUnique;
    }

    public boolean isRepl() {
        return attr_.isRepl;
    }

    public boolean isMandatory() {
        return attr_.isMandatory();
    }
    
    public boolean isAggregate() {
        return attr_.isAggregate();
    }
    
    public boolean isFullText() {
        return attr_.isFullText();
    }
    
    public boolean isEncrypt() {
    	return attr_.isEncrypt;
    }
    
    public KrnAttribute[] getRevAttributes() throws KrnException {
        KrnAttribute[] res = new KrnAttribute[0];
        if (type_ != null) {
            ClassNode cnode = kernel.getClassNode(type_.id);
            ListModel model = revAttrs.getModel();
            res = new KrnAttribute[model.getSize()];
            for (int i = 0; i < model.getSize(); i++) {
                String name = (String) model.getElementAt(i);
                res[i] = cnode.getAttribute(name);
            }
        }
        return res;
    }

    public String getExpression() {
        return exprField.getText();
    }
    
    public void setExpression(String expression) {
    	exprField.setText(expression);
    }

    public long getFlags() {
        return attr_.flags;
    }

    public int getAttrSize() {
        return Integer.parseInt(attrSize.getText());
    }

    public void itemStateChanged(ItemEvent e) {
        Object source = e.getSource();
        if (source == isMultiLangCheck) {
            attr_.isMultilingual = (e.getStateChange() == ItemEvent.SELECTED);
			if (isExistingAttribute) {
				checkForModification();
			}
        } else if (source == isUniqueCheck) {
            attr_.isUnique = (e.getStateChange() == ItemEvent.SELECTED);
            if (isExistingAttribute) {
				checkForModification();
			}
        } else if (source == isIndexCheck) {
            attr_.isIndexed = (e.getStateChange() == ItemEvent.SELECTED);
            isFullTextCheck.setEnabled(attr_.isIndexed);
            if(!attr_.isIndexed) isFullTextCheck.setSelected(false);
            if (isExistingAttribute) {
				checkForModification();
			}
        } else if (source == isReplCheck) {
            attr_.isRepl = (e.getStateChange() == ItemEvent.SELECTED);
            if (isExistingAttribute) {
				checkForModification();
			}
        } else if (source == isMandatoryCheck) {
            attr_.setMandatory(e.getStateChange() == ItemEvent.SELECTED);
            if (isExistingAttribute) {
				checkForModification();
			}
        } else if (source == isAggregateCheck) {
            attr_.setAggregate(e.getStateChange() == ItemEvent.SELECTED);
            if (isExistingAttribute) {
				checkForModification();
			}
        } else if (source == isFullTextCheck) {
            attr_.setFullText(e.getStateChange() == ItemEvent.SELECTED);
            if (isExistingAttribute) {
				checkForModification();
			}
        } else if (source == isGroupCheck) {
            attr_.setGroup(e.getStateChange() == ItemEvent.SELECTED);
            if (isExistingAttribute) {
				checkForModification();
			}
        } else if (source == isEncryptCheck) {
        	attr_.isEncrypt = (e.getStateChange() == ItemEvent.SELECTED);
        	if (isExistingAttribute) {
        		checkForModification();
        	}
        } else if (source == arrayTypeCombo) {
			checkForModification();
        } else if (source == accessModifiersCombo) {
			checkForModification();
        }
    }

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
            } else if (src == fnBtn) {
                ExpressionEditor exprEditor = new ExpressionEditor(exprField.getText(), AttrPropPanel.this);
                DesignerDialog dlg = new DesignerDialog((Dialog) getTopLevelAncestor(), "Выражение", exprEditor);
                dlg.setSize(new Dimension(kz.tamur.comps.Utils.getMaxWindowSizeActDisplay()));
                dlg.setLocation(kz.tamur.comps.Utils.getCenterLocationPoint(dlg.getSize()));
                dlg.show();
                if (dlg.isOK()) {
                	setExpression(exprEditor.getExpression());
                }
            } else if (src == addRevAttrBtn) {
                selectRevAttributes();
            } else if (src == delRevAttrBtn) {
                deleteRevAttributes();
            } else if (src == arrayTypeCombo) {
                attr_.collectionType = ((CollectionType) arrayTypeCombo.getSelectedItem()).col_type;
            } else if (src == accessModifiersCombo) {
                attr_.accessModifierType = ((AccessModifierType) accessModifiersCombo.getSelectedItem()).type;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void populateRevAttributes()
            throws KrnException {
        if (attr_ != null) {
            KrnAttribute[] revAttrs = kernel.getRevAttributes(attr_.id);
            AttrItem[] items = new AttrItem[revAttrs.length];
            for (int i = 0; i < revAttrs.length; i++) {
                items[i] = new AttrItem(revAttrs[i]);
            }
            this.revAttrs.setModel(new AttrListData(items));
        } else {
            this.revAttrs.setModel(new AttrListData(null));
        }
    }

    private KrnClass selectClass(String title) {
        JPanel mainPanel = new JPanel(new BorderLayout());
        Dimension MPDimension = new Dimension(600, 500);
        mainPanel.setMinimumSize(MPDimension);
        mainPanel.setMaximumSize(MPDimension);
        mainPanel.setPreferredSize(MPDimension);
        
    	ClassTree classTree = new ClassTree();
        JScrollPane scrollPane = new JScrollPane(classTree);
        scrollPane.setOpaque(isOpaque);
        scrollPane.getViewport().setOpaque(isOpaque);

        try {
    	   TreeNode root = Kernel.instance().getClassNodeByName("Объект");
           classTree.setModel(new DefaultTreeModel(root));
            if (lastSelectedClass != null) {
                classTree.setSelectedPath(lastSelectedClass);
            }
        } catch (KrnException e) {
            e.printStackTrace();
        }
        
        ClassSearchingPanel classSearchingPanel = new ClassSearchingPanel(classTree);
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(classSearchingPanel, BorderLayout.SOUTH);
        
        DesignerDialog dialog = null;
        if (cont instanceof Dialog) {
        	dialog = new DesignerDialog((Dialog)cont, title, mainPanel);
        } else {
        	dialog = new DesignerDialog((Frame)cont, title, mainPanel);
        }
        dialog.setResizable(false);
        dialog.show();
        if (dialog.isOK()) {
            KrnClass cls = classTree.getSelectedClass();
            return cls;
        }
        return null;
    }

    private void jbInit() throws Exception {
        cont = getTopLevelAncestor();
        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(500, 450));
        int y = 0;

        JLabel lb = kz.tamur.rt.Utils.createLabel("Наименование");
        lb.setHorizontalAlignment(SwingConstants.RIGHT);
        add(lb, new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 5, 0), 0, 0));
        add(attrNameField, new GridBagConstraints(1, y, 3, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 0, 0));
        y++;
        lb = createLabel("Класс");
        lb.setHorizontalAlignment(SwingConstants.RIGHT);
        add(lb, new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 0), 0, 0));
        add(classNameField, new GridBagConstraints(1, y, 2, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 5, 0), 0, 0));
        add(selectClassBtn, new GridBagConstraints(3, y, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 5, 5), 0, 0));
        y++;
        lb = createLabel("Тип");
        lb.setHorizontalAlignment(SwingConstants.RIGHT);
        add(lb, new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 0), 0, 0));
        add(typeNameField, new GridBagConstraints(1, y, 2, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 5, 0), 0, 0));
        add(selectTypeBtn, new GridBagConstraints(3, y, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 5, 5), 0, 0));
        
        y++;
        lb = kz.tamur.rt.Utils.createLabel("Имя таблици/столбца");
        lb.setHorizontalAlignment(SwingConstants.RIGHT);
        add(lb, new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 5, 0), 0, 0));
        add(attrTNameField, new GridBagConstraints(1, y, 3, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 0, 0));

        y++;
        lb = createLabel();
        lb.setIcon(AttributeTreeIconLoader.getIcon(false, false, false, false, false, false));
        lb.setHorizontalAlignment(SwingConstants.RIGHT);        
        add(lb, new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 0), 0, 0));
        add(attrIdField, new GridBagConstraints(1, y, 3, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 5), 0, 0));
        
        y++;
        JPanel pan = new JPanel(new GridBagLayout());
        Border bord = BorderFactory.createLineBorder(getMidSysColor());
        Border titleBorder = kz.tamur.rt.Utils.createTitledBorder(bord, "Свойства");
        pan.setBorder(titleBorder);
        lb = createLabel("Размер");
        lb.setHorizontalAlignment(SwingConstants.RIGHT);
        pan.add(lb, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        setAllSize(attrSize, new Dimension(50, 22));
        pan.add(attrSize, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        lb = createLabel("Тип множества");
        lb.setHorizontalAlignment(SwingConstants.RIGHT);
        pan.add(lb, new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        pan.add(arrayTypeCombo, new GridBagConstraints(3, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        lb = createLabel("Модификатор доступа");
        lb.setHorizontalAlignment(SwingConstants.RIGHT);
        pan.add(lb, new GridBagConstraints(4, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        pan.add(accessModifiersCombo, new GridBagConstraints(5, 0, 1, 1, 1, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

        JPanel p2 = new JPanel(new GridBagLayout());
        p2.add(isMandatoryCheck, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        p2.add(isUniqueCheck, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        p2.add(isIndexCheck, new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        p2.add(isFullTextCheck, new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        p2.add(isMultiLangCheck, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        p2.add(isReplCheck, new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        p2.add(isAggregateCheck, new GridBagConstraints(2, 1, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        p2.add(isGroupCheck, new GridBagConstraints(3, 1, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        p2.add(isEncryptCheck, new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        pan.add(p2, new GridBagConstraints(0, 1, 6, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, Constants.INSETS_0, 0, 0));
        JScrollPane commentScroll = new JScrollPane(commentArea); 
        pan.add(commentScroll, new GridBagConstraints(0, 2, 7, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 5, 5, 5), 0, 0));
        add(pan, new GridBagConstraints(0, y, 4, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 5, 5, 5), 0, 0));
        y++;
        add(createRevAttrPanel(), new GridBagConstraints(0, y, 4, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0));

        classNameField.setEditable(false);
        typeNameField.setEditable(false);

        selectTypeBtn.addActionListener(this);
        selectClassBtn.addActionListener(this);
        fnBtn.addActionListener(this);
        
        setOpaque(isOpaque);
        pan.setOpaque(isOpaque);
        p2.setOpaque(isOpaque);
        isMultiLangCheck.setOpaque(isOpaque); 
        isIndexCheck.setOpaque(isOpaque);
        isUniqueCheck.setOpaque(isOpaque);
        isReplCheck.setOpaque(isOpaque);
        isMandatoryCheck.setOpaque(isOpaque); 
        isAggregateCheck.setOpaque(isOpaque); 
        isFullTextCheck.setOpaque(isOpaque); 
        isGroupCheck.setOpaque(isOpaque); 
        arrayTypeCombo.setOpaque(isOpaque);
    }

    private JPanel createRevAttrPanel() {
        JPanel res = new JPanel(new GridBagLayout());
        res.setOpaque(isOpaque);
        Border title = BorderFactory.createLineBorder(getMidSysColor());
        Border titleBorder = kz.tamur.rt.Utils.createTitledBorder(title, "Обратные атрибуты");
        res.setBorder(titleBorder);
        JScrollPane sp = new JScrollPane(revAttrs);
        setAllSize(sp, new Dimension(300, 40));
        res.add(sp, new GridBagConstraints(0, 0, 4, 3, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
        res.add(new JLabel(" "), new GridBagConstraints(0, 3, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        res.add(addRevAttrBtn, new GridBagConstraints(2, 3, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        res.add(delRevAttrBtn, new GridBagConstraints(3, 3, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        addRevAttrBtn.setText("Добавить");
        setAllSize(addRevAttrBtn, new Dimension(100, 25));
        delRevAttrBtn.setText("Удалить");
        setAllSize(delRevAttrBtn, new Dimension(100, 25));
        addRevAttrBtn.addActionListener(this);
        delRevAttrBtn.addActionListener(this);
        return res;
    }

    private void selectRevAttributes() throws KrnException {
        if (type_ != null) {
            ClassNode cnode = kernel.getClassNode(cls_.id);
            List<ClassNode> subCNodes = new ArrayList<ClassNode>();
            subCNodes.add(cnode);
            cnode.getSubClasses(subCNodes);
            ClassNode cn = kernel.getClassNode(type_.id);
            List attrs = cn.getAttributes();
            List<AttrItem> items = new ArrayList<AttrItem>();
            for (int i = 0; i < attrs.size(); i++) {
                KrnAttribute attr = (KrnAttribute) attrs.get(i);
                for (ClassNode subCNode : subCNodes) {
                    if (attr.typeClassId == subCNode.getKrnClass().id) {
                        items.add(new AttrItem(attr));
                        break;
                    }
                }
            }
            JList attrList = new JList(items.toArray(new AttrItem[items.size()]));
            DesignerDialog dlg = null;
            if (cont instanceof Dialog) {
                dlg = new DesignerDialog((Dialog)cont, "Выбор обратного атрибута", new JScrollPane(attrList));
            } else {
                dlg = new DesignerDialog((Frame)cont, "Выбор обратного атрибута", new JScrollPane(attrList));
            }
            dlg.show();
            if (dlg.isOK()) {
                Object[] selItems = attrList.getSelectedValues();
                AttrListData model = (AttrListData) this.revAttrs.getModel();
                if (model.addItems(selItems)) {
                    saveRevAttributes();
                }
            }
        }
    }

    private void deleteRevAttributes() throws KrnException {
        Object[] items = revAttrs.getSelectedValues();
        AttrListData model = (AttrListData) revAttrs.getModel();
        if (model.removeItems(items)) {
            saveRevAttributes();
        }
    }

    private void saveRevAttributes() throws KrnException {
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

    private static class AttrListData extends AbstractListModel {
        private List data = new ArrayList();

        public AttrListData(Object[] items) {
            if (items != null && items.length > 0) {
                for (int i = 0; i < items.length; i++) {
                    data.add(items[i]);
                }
            }
        }

        public int getSize() {
            return data.size();
        }

        public Object getElementAt(int index) {
            return data.get(index);
        }

        public boolean addItems(Object[] items) {
            boolean changed = false;
            for (int i = 0; i < items.length; i++) {
                Object item = items[i];
                if (!data.contains(item)) {
                    data.add(item);
                    changed = true;
                }
            }
            if (changed) {
                fireContentsChanged(this, 0, data.size() - 1);
            }
            return changed;
        }

        private boolean removeItems(Object[] items) {
            boolean changed = false;
            for (int i = 0; i < items.length; i++) {
                Object item = items[i];
                if (data.contains(item)) {
                    data.remove(item);
                    changed = true;
                }
            }
            if (changed) {
                fireContentsChanged(this, 0, data.size() - 1);
            }
            return changed;
        }
    }

    private class LowDocument extends PlainDocument {
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            if (offs==0 && str.length()==1)
            str = str.toLowerCase(Constants.OK);
            super.insertString(offs, str, a);
        }
    }

    private void setAllSize(JComponent comp, Dimension size) {
        comp.setPreferredSize(size);
        comp.setMaximumSize(size);
        comp.setMinimumSize(size);
    }
    
    public String getComment() {
    	return Funcs.normalizeInput(commentArea.getText());
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
        if (attrNameField.getText().length() == 0 || "".equals(attrNameField.getText()) || attrNameField.getText() == null) {
            sb.append("Не заполнено имя атрибута!\n");
        }
        if (classNameField.getText().length() == 0 || "".equals(classNameField.getText()) || classNameField.getText() == null) {
            sb.append("Не заполнено имя класса атрибута!\n");
        }
        if (typeNameField.getText().length() == 0 || "".equals(typeNameField.getText()) || typeNameField.getText() == null) {
            sb.append("Не заполнен тип атрибута!\n");
        }else{        
	        if(attr_.isMultilingual && (type_.id != Kernel.IC_STRING && type_.id != Kernel.IC_MEMO && type_.id != Kernel.IC_BLOB)){
	        	sb.append("Атрибут с типом '" + typeNameField.getText() + "' не может быть мультиязычным!\n");
	        }
        }
        if (!isTnameOk){
        	sb.append("Не корректно заполнено имя таблицы/столбца атрибута!\n");
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
    
    class AccessModifierType {
        int type;
        String name;

        public AccessModifierType(int type, String name) {
            this.type = type;
            this.name = name;
        }

        public String toString() {
            return name;
        }
    }

    private class Version {
    	private String attributeName;
    	private String className;
    	private String attributeType;
    	private String attributeSize;
    	private int arrayType;
    	private boolean isMandatory;
    	private boolean isUnique;
    	private boolean isIndex;
    	private boolean isMultiLang;
    	private boolean isReplicate;
    	private boolean isAggregate;
    	private boolean isFullText;
    	private boolean isGroup;
    	private boolean isEncrypt;
    	private String comment;
    	private List<Object> reverseAttributes;
    	private String Tname;
    	private int accessModifier;

    	public Version(String attributeName, String className, String attributeType, String attributeSize, int arrayType, boolean isMandatory, boolean isUnique,
    			boolean isIndex, boolean isMultiLang, boolean isReplicate, boolean isAggregate, boolean isFullText,boolean isGroup, String comment, List<Object> reverseAttributes, String tname, int accessModifier, boolean isEncrypt) {
    		this.attributeName = attributeName;
    		this.className = className;
    		this.attributeType = attributeType;
    		this.attributeSize = attributeSize;
    		this.arrayType = arrayType;
    		this.isMandatory = isMandatory;
    		this.isUnique = isUnique;
    		this.isIndex = isIndex;
    		this.isMultiLang = isMultiLang;
    		this.isReplicate = isReplicate;
    		this.isAggregate = isAggregate;
    		this.isFullText = isFullText;
    		this.isGroup = isGroup;
    		this.comment = comment;
    		this.reverseAttributes = reverseAttributes;
    		this.Tname = tname;
    		this.accessModifier = accessModifier;
    		this.isEncrypt = isEncrypt;
    	}
    	
    	public String getAttributeName() {
			return attributeName;
		}

		public String getClassName() {
			return className;
		}

		public String getAttributeType() {
			return attributeType;
		}

		public String getAttributeSize() {
			return attributeSize;
		}

		public int getArrayType() {
			return arrayType;
		}

		public boolean isMandatory() {
			return isMandatory;
		}

		public boolean isUnique() {
			return isUnique;
		}

		public boolean isIndex() {
			return isIndex;
		}

		public boolean isMultiLang() {
			return isMultiLang;
		}

		public boolean isReplicate() {
			return isReplicate;
		}

		public boolean isAggregate() {
			return isAggregate;
		}

		public boolean isFullText() {
			return isFullText;
		}
		public boolean isGroup() {
			return isGroup;
		}
		public boolean isEncrypt() {
			return isEncrypt;
		}
		public String getComment() {
			return comment;
		}

		public List<Object> getReverseAttributes() {
			return reverseAttributes;
		}
		
		public String getTname() {
			return Tname;
		}
		
		public int getAccessModifier() {
			return accessModifier;
		}

		public boolean equals(Version version) {
			if (!attributeName.equals(version.getAttributeName())) {
				return false;
			}
    		if (!className.equals(version.getClassName())) {
    			return false;
    		}
    		if (!attributeType.equals(version.getAttributeType())) {
    			return false;
    		} 
    		if (!attributeSize.equals(version.getAttributeSize())) {
    			return false;
    		} 
    		if (arrayType != version.getArrayType()) {
    			return false;
    		} 
    		if (isMandatory != version.isMandatory()) {
    			return false;
    		}
    		if (isUnique != version.isUnique()) {
    			return false;
    		}
    		if (isIndex != version.isIndex()) {
    			return false;
    		}
    		if (isMultiLang != version.isMultiLang()) {
    			return false;
    		}
    		if (isReplicate != version.isReplicate()) {
    			return false;
    		}
    		if (isAggregate != version.isAggregate()) {
    			return false;
    		}
    		if (isFullText != version.isFullText()) {
    			return false;
    		}
    		if (isGroup != version.isGroup()) {
    			return false;
    		}
    		if (!comment.equals(version.getComment())) {
    			return false;
    		}
    		if (!Tname.equals(version.getTname())) {
    			return false;
    		}
    		if (accessModifier != version.getAccessModifier()) {
    			return false;
    		} 
    		if (isEncrypt != version.isEncrypt()) {
    			return false;
    		}
    		for (int i = 0; i < reverseAttributes.size(); i ++) {
    			if (!reverseAttributes.equals(version.getReverseAttributes().get(i))) {
    				return false;
    			}
    		}
    		return true;
    	}
    }
    
    public void PatternFiltr() {
    	isTnameOk = true;
    	for (Map.Entry<JTextField, Filtr> entry : Filtrs.entrySet()) {
	    	String source = entry.getKey().getText().trim().toUpperCase(Constants.OK);
	    	boolean isok = false;
	    	if (source.length() == 0) {
	    		isok = true;
	    	} else {
		        final Pattern pattern_UserName = Pattern.compile(entry.getValue().filtr);
		    	final Matcher matcher = pattern_UserName.matcher(source);
		        if (matcher.matches()) {
		        	isok = true;
		        } else {
		        	isok = false;
		        }
	    	}
	    	
	    	for (String str : entry.getValue().NotText){
	    		if (source.equals(str.toUpperCase(Constants.OK))){
	    			isok = false;
	    			break;
	    		}
	    	}
	    	
	    	if (isok) {
	    		entry.getKey().setForeground(Color.black);
	    		entry.getKey().setFont(FieldFont);
	    		entry.getValue().check = true;
	    	} else {
	    		entry.getKey().setForeground(Color.red);
	    		entry.getKey().setFont(FieldFontErr);
	    		entry.getValue().check = false;
	    		isTnameOk = false;
	    	}
    	}
    }
}