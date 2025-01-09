package kz.tamur.admin;

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
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import kz.tamur.comps.Constants;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.Utils;
import kz.tamur.util.Funcs;

import com.cifs.or2.client.ClassNode;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;

public class ClassPropPanel extends JPanel implements DocumentListener, ItemListener {
	private KrnClass currentClass;
    private KrnClass baseClass;
    private JLabel clsNameLbl = Utils.createLabel("Имя");
    private JLabel baseClsNameLbl = Utils.createLabel("Базовый класс");
    private JTextField classNameField = new JTextField();
    private JTextField baseNameField = new JTextField();
    private JButton baseClassBtn = ButtonsFactory.createToolButton("editor","", true);    
    private JCheckBox replCheck = Utils.createCheckBox("Реплицировать", false);
    private JButton indexBtn = ButtonsFactory.createToolButton(null, "Индексы...", "Многоатрибутные индексы", true);
    private JTextArea commentField = new JTextArea();
    private JLabel clsIdLbl = Utils.createLabel();
    private JTextField classIdField = new JTextField();
    private ClassNode lastSelectedClass;
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    private Version version;
    private DesignerDialog dialog = null;
    private Map<JTextField, Filtr> Filtrs = new HashMap<JTextField, Filtr>();
    private JTextArea EroorLabel = new JTextArea(); //TODO tname
    private JLabel tableNameLabel = Utils.createLabel("Таблица в БД"); //TODO tname
    private JTextField tableNameField = new JTextField(); //TODO tname
    private Font FieldFont = new Font(tableNameField.getFont().getName(), tableNameField.getFont().getStyle(), tableNameField.getFont().getSize());
    private Font FieldFontErr = new Font(tableNameField.getFont().getName(), Font.ITALIC, tableNameField.getFont().getSize());
    private JCheckBox isVirtualClassCheck = Utils.createCheckBox("Виртуальный класс", false);
    
    private class Filtr{
    	public final String filtr;
    	public ArrayList<String> NotText = new ArrayList<String>();
    	public boolean check = true;
    	
    	public Filtr(final String _filtr) {
    		this.filtr = _filtr;
		}
    }

    public ClassPropPanel(KrnClass cls, KrnClass base, String comment) {
    	this(cls, base, comment, false);
    	isVirtualClassCheck.setEnabled(true);
    }
    
    public ClassPropPanel(KrnClass cls, KrnClass base, String comment, boolean isVirtualClass) {
        try {
            jbInit();
            Filtrs.put(classNameField, new Filtr("^\\p{IsL}[^\\\"\\'\\`]{2,253}$"));
            Filtrs.put(tableNameField, new Filtr("^[a-zA-Z][a-zA-Z0-9\\u005F\\-]{1,28}[a-zA-Z0-9]$"));
            //EroorLabel.setHorizontalAlignment(SwingConstants.CENTER);
            EroorLabel.setBorder(null);
            EroorLabel.setLineWrap(true);
            EroorLabel.setWrapStyleWord(true);
            EroorLabel.setBackground(this.getBackground());
            EroorLabel.setEditable(false);
            EroorLabel.setForeground(Color.red);
            EroorLabel.setVisible(false);
            currentClass = cls;
            baseClass = base;
            if (cls == null) {
                replCheck.setSelected(baseClass.isRepl);
            } else {
                replCheck.setSelected(cls.isRepl);
                commentField.setText(comment);
                if (cls.tname != null) {
                	tableNameField.setText(cls.tname); //TODO tname
                }
            }
            isVirtualClassCheck.setSelected(isVirtualClass);
            isVirtualClassCheck.setEnabled(false);
            classNameField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent evt) {
                	EroorLabel.setText("");
                	EroorLabel.setVisible(false);
                	PatternFiltr();
                }
            });
            tableNameField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent evt) {
                	EroorLabel.setText("");
                	EroorLabel.setVisible(false);
                	PatternFiltr();
                }
            });
            PatternFiltr();
            tableNameField.setToolTipText("Допустимые символы \"a-z, 0-9, _, -\" (без запятых, пробелов и кавычек) не может начинаться на \"_, -, 0-9\" или заканчиваться на \"_, -\".");
            
            classNameField.setDocument(new mydoc());
            classNameField.setText((cls == null) ? "NewClass" : cls.name);
            if (cls != null){
            	//if (kz.tamur.or3.util.Tname.isParentSytemClass(cls)) {
            	//	classNameField.enable(false);
            	//}
            }
            baseNameField.setText(base.name);
            classIdField.setText((cls == null) ? "" : " ID=" + cls.id + "  UID=" + cls.uid);
            indexBtn.setEnabled(cls!=null);
            if (cls != null) {
	            classNameField.getDocument().addDocumentListener(this);
	            baseNameField.getDocument().addDocumentListener(this);
	            tableNameField.getDocument().addDocumentListener(this);
	            replCheck.addItemListener(this);
	            commentField.getDocument().addDocumentListener(this);
	            version = new Version(classNameField.getText(), baseNameField.getText(), tableNameField.getText(), replCheck.isSelected(), commentField.getText());
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
    	if (version.equals(new Version(Funcs.normalizeInput(classNameField.getText()), Funcs.normalizeInput(baseNameField.getText()),
    			Funcs.normalizeInput(tableNameField.getText()), replCheck.isSelected(), Funcs.normalizeInput(commentField.getText())))) {
            dialog.setOkEnabled(false);
    	} else {
            dialog.setOkEnabled(true);
    	}
    }
    
    public String getClassName() {
        return Funcs.normalizeInput(classNameField.getText());
    }

    public KrnClass getBaseClass() {
        return baseClass;
    }
    
    public KrnClass getCurrentClass(){
    	return currentClass;
    }

    private void jbInit() throws Exception {
        replCheck.setOpaque(isOpaque);
        setOpaque(isOpaque);
        setLayout(new GridBagLayout());
        baseNameField.setEditable(false);
        setPreferredSize(new Dimension(600, 450));
        clsNameLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        tableNameLabel.setHorizontalAlignment(SwingConstants.RIGHT); //TODO tname
        int gridy = 0;
        add(clsNameLbl, new GridBagConstraints(0, gridy, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(20, 10, 0, 0), 0, 0));
        add(classNameField, new GridBagConstraints(1, gridy, 3, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(20, 10, 0, 10), 0, 0));

        add(baseClsNameLbl, new GridBagConstraints(0, ++gridy, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 10, 0, 0), 0, 0));
        add(baseNameField, new GridBagConstraints(1, gridy, 2, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 10, 0, 0), 0, 0));
        add(baseClassBtn, new GridBagConstraints(3, gridy, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 10, 0, 10), 0, 0));
        
        add(tableNameLabel, new GridBagConstraints(0, ++gridy, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 10, 0, 0), 0, 0));//TODO tname
        add(tableNameField, new GridBagConstraints(1, gridy, 3, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 10, 0, 10), 0, 0));//TODO tname
        
        
        add(indexBtn,new GridBagConstraints(0, ++gridy, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 10, 0, 0), 0, 0));
        add(replCheck, new GridBagConstraints(1, gridy, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 10, 0, 10), 0, 0));
        add(isVirtualClassCheck, new GridBagConstraints(2, gridy, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 10, 0, 10), 0, 0));
        JScrollPane commentScroll = new JScrollPane(commentField);
        commentScroll.setPreferredSize(new Dimension(400, 400));
        add(commentScroll, new GridBagConstraints(0, ++gridy, 4, 1, 1, 1,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(10, 10, 10, 10), 0, 0));
        
        clsIdLbl.setIcon(ClassTreeIconLoader.getIcon(false, false));
        clsIdLbl.setHorizontalAlignment(SwingConstants.RIGHT);        
        add(clsIdLbl, new GridBagConstraints(0, ++gridy, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 10, 15, 0), 0, 0));
        classIdField.setEditable(false);
        add(classIdField, new GridBagConstraints(1, gridy, 3, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 10, 15, 10), 0, 0));
        
        add(EroorLabel, new GridBagConstraints(0, ++gridy, 4, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(3, 10, 0, 0), 0, 0));
        EroorLabel.setVisible(true);
        
        baseClassBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                baseClassBtn_actionPerformed(e);
            }
        });
        indexBtn.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e){
        		showIndexes();
        	}
        });
    }

    void baseClassBtn_actionPerformed(ActionEvent e) {
        KrnClass cls = selectClass();
        if (cls != null) {
            baseClass = cls;
            baseNameField.setText(baseClass.name);
        }
    }     

    private KrnClass selectClass() {
        if (baseClass != null) {
            try {
                lastSelectedClass = Kernel.instance().getClassNodeByName(
                        baseClass.name);
            } catch (KrnException e) {
                e.printStackTrace();
            }
        }

        KrnClass res = null;
        DesignerDialog dlg = null;
        Container cont = getTopLevelAncestor();
        ClassTree ct = new ClassTree();
        JScrollPane sp = new JScrollPane(ct);
        sp.setPreferredSize(new Dimension(300, 400));
        sp.setOpaque(isOpaque);
        sp.getViewport().setOpaque(isOpaque);
        if (cont instanceof Dialog) {
            dlg = new DesignerDialog((Dialog)cont, "Классы", sp);
        } else {
            dlg = new DesignerDialog((Frame)cont, "Классы", sp);
        }
        if (lastSelectedClass != null) {
            try {
                ct.setSelectedPath(lastSelectedClass);
            } catch (KrnException e) {
                e.printStackTrace();
            }
        }

        dlg.show();
        if (dlg.getResult() == ButtonsFactory.BUTTON_OK)
            res = ct.getSelectedClass();
        return res;
    }
    
    private void showIndexes(){
    	DesignerDialog dlg = null;
    	Container cont = getTopLevelAncestor();
    	IndexBrowser ib = new IndexBrowser(getCurrentClass());
    	JScrollPane sp = new JScrollPane(ib);
    	sp.setPreferredSize(new Dimension(550, 400));
    	sp.setOpaque(isOpaque);
        sp.getViewport().setOpaque(isOpaque);
    	String title = "Многоатрибутные индексы";
    	if (cont instanceof Dialog) {
            dlg = new DesignerDialog((Dialog)cont, title, sp);
        } else {
            dlg = new DesignerDialog((Frame)cont, title, sp);
        }
    	dlg.show();
    	if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {}
    }

    private class mydoc extends PlainDocument {
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            if (offs==0 && str.length()==1)
            str = str.toUpperCase(Constants.OK);
            super.insertString(offs, str, a);
        }
    }

    public ClassNode getLastSelectedClass() {
        return lastSelectedClass;
    }

    public void setLastSelectedClass(ClassNode lastSelectedClass) {
        this.lastSelectedClass = lastSelectedClass;
    }

    public boolean isRepl() {
        return replCheck.isSelected();
    }
    
    public boolean isVirtual() {
        return isVirtualClassCheck.isSelected();
    }

    public String getComment() {
    	return Funcs.normalizeInput(commentField.getText());
    }
    
    private class Version {
    	private String className;
    	private String baseClassName;
    	private String tableNameField;
    	private boolean isReplicate;
    	private String comment;

    	public Version(String className, String baseClassName, String tableNameField, boolean isReplicate, String comment) {
    		this.className = className;
    		this.baseClassName = baseClassName;
    		this.tableNameField = tableNameField;
    		this.isReplicate = isReplicate;
    		this.comment = comment;
    	}
    	
    	public String getClassName() {
			return className;
		}

		public String getBaseClassName() {
			return baseClassName;
		}

		public String getTableNameField() {
			return tableNameField;
		}
		
		public boolean isReplicate() {
			return isReplicate;
		}

		public String getComment() {
			return comment;
		}
    	
    	public boolean equals(Version version) {
    		if (!className.equals(version.getClassName())) {
    			return false;
    		} 
    		if (!baseClassName.equals(version.getBaseClassName())) {
    			return false;
    		}
    		if (!tableNameField.equals(version.getTableNameField())) {
    			return false;
    		}
    		if (isReplicate != version.isReplicate()) {
    			return false;
    		}
    		if (!comment.equals(version.getComment())) {
    			return false;
    		} 
    		return true;
    	}
    }

	public void itemStateChanged(ItemEvent e) {
		Object source = e.getSource();
		if (source == replCheck) {
			checkForModification();
		}
	}
	
    public String getTableName() { //TODO tname
    	if (Filtrs.containsKey(tableNameField) && Filtrs.get(tableNameField).check){
	    	String TName = tableNameField.getText().trim().toUpperCase(Constants.OK);
	        return (TName.length() == 0) ? null : TName;
    	} else {
    		return null;
    	}
    }
    
    public void setNotClassName(String name){
    	if (Filtrs.containsKey(classNameField)){
    		Filtrs.get(classNameField).NotText.add(name);
    	}
    	EroorLabel.setText("Невозможно создать класс с таким наименованием.");
    	EroorLabel.setVisible(true);
    	PatternFiltr();
    }
    
    public void setNotTableName(String name){
    	if (Filtrs.containsKey(tableNameField)){
    		Filtrs.get(tableNameField).NotText.add(name);
    	}
    	EroorLabel.setText("Невозможно создать таблицу с таким наименованием.");
    	EroorLabel.setVisible(true);
    	PatternFiltr();
    }
    
    public void setErrorMessange(String name){
    	EroorLabel.setText("Ошибка: " + name);
    	EroorLabel.setVisible(true);
    	PatternFiltr();
    }
    
    public void PatternFiltr() {
    	boolean isokd = true;
    	for (Map.Entry<JTextField, Filtr> entry : Filtrs.entrySet()) {
	    	String source = entry.getKey().getText().trim().toUpperCase(Constants.OK);
	    	boolean isok = false;
	    	if (source.length() == 0 && entry.getKey().equals(tableNameField)) {
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
	    		isokd = false;
	    	}
    	}
    	
    	if (dialog != null) {
	    	if (isokd) {
	        	dialog.getOkBtn().setEnabled(true);
	    	} else {
	        	dialog.getOkBtn().setEnabled(false);
	    	}
    	}
    }
    
    public void setparentDesignerDialog(DesignerDialog dlg) {
    	this.dialog = dlg;
    }
}