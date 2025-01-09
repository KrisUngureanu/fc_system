package kz.tamur.admin;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;



import kz.tamur.Or3Frame;
import kz.tamur.comps.Constants;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.expr.EditorWindow;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.Utils;
import kz.tamur.util.ExpressionEditor;
import kz.tamur.util.Funcs;

import com.cifs.or2.client.ClassNode;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnMethod;

public class MethodPanel extends JPanel implements ActionListener, DocumentListener, ItemListener {

    protected static String title = null;
    private KrnMethod method;
    private JLabel nameLabel = Utils.createLabel("Имя метода");
    private JLabel ownerLabel = Utils.createLabel("");
    private JTextField nameText = Utils.createDesignerTextField();
    private JCheckBox classMethodCheck = Utils.createCheckBox("метод класса", false);
    private JTextArea commentArea = new JTextArea();
    private JTextArea exprArea = new JTextArea();
    private JButton editorBtn = ButtonsFactory.createToolButton(null, "Редактировать", "", true);
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    private Version version;
    private boolean init = true;

    public MethodPanel(KrnMethod method, String comment,boolean readOnly) {
        super(new GridBagLayout());
        this.method = method;
        setPreferredSize(new Dimension(400, 400));
        jbInit(readOnly);
        commentArea.setText(comment);
        commentArea.setEnabled(!readOnly);
        init = false;
    }
    
    public void setExpression(String expression) {
    	exprArea.setText(expression);
   }

    private void jbInit(final boolean readOnly) {
        if (method != null) {
        	nameText.setText(method.name);
            classMethodCheck.setSelected(method.isClassMethod);
            Kernel krn = Kernel.instance();
            try {
                ClassNode clsNode = krn.getClassNode(method.classId);
                title = clsNode.getName() + "." + method.name;
                String expr = clsNode.getMethodExpression(method.name);
                exprArea.setText(expr);
                exprArea.setEnabled(!readOnly);
            } catch (KrnException e) {
                e.printStackTrace();
            }
            
            nameText.getDocument().addDocumentListener(this);
            nameText.setEnabled(!readOnly);
            classMethodCheck.addItemListener(this);
            classMethodCheck.setEnabled(!readOnly);
            commentArea.getDocument().addDocumentListener(this);
            exprArea.getDocument().addDocumentListener(this);
            version = new Version(nameText.getText(), classMethodCheck.isSelected(), getComment(), getExpression());
        }

        Utils.setAllSize(editorBtn, new Dimension(100, 30));
        editorBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final ExpressionEditor ex = new ExpressionEditor(getExpression(), MethodPanel.this, method, readOnly);
                ex.getStatusBar().addLabel(ownerLabel);
                ex.setSourceMethodToDebugger(method);
                ActionListener btnaction = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        setExpression(ex.getExpression());
                    }
                };
                EditorWindow.addTab(method.uid, title, ex, btnaction, null); //TODO EditorWindiw
            }
        });
        
        ownerLabel.setText("Автор: " + (kz.tamur.comps.Utils.getMethodOwner(method)!= null? kz.tamur.comps.Utils.getMethodOwner(method): ""));

        add(nameLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
        add(nameText, new GridBagConstraints(1, 0, 2, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 0, 5, 5), 0, 0));
        add(classMethodCheck, new GridBagConstraints(0, 1, 1, 1, 0, 0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 5, 0, 0), 0, 0));
        add(new JLabel(""), new GridBagConstraints(1, 1, 2, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                Constants.INSETS_0, 0, 0));
        add(new JLabel("Комментарий"), new GridBagConstraints(0, 2, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 0, 0));
        add(new JScrollPane(commentArea), new GridBagConstraints(0, 3, 3, 1, 1, 0.3,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 0, 0));
        add(new JLabel("Тело метода"), new GridBagConstraints(0, 4, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 0, 0));
        add(new JScrollPane(exprArea), new GridBagConstraints(0, 5, 3, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 0, 0));
        
        add(ownerLabel, new GridBagConstraints(0, 6, 1, 1, 0, 0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
        add(editorBtn, new GridBagConstraints(2, 6, 1, 1, 0, 0,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
        nameText.requestFocusInWindow();
        
        classMethodCheck.setOpaque(isOpaque);
        setOpaque(isOpaque);
    }
    
    public void changedUpdate(DocumentEvent e) {
    }

    public void removeUpdate(DocumentEvent e) {
        checkForModification();
    }

    public void insertUpdate(DocumentEvent e) {
        checkForModification();
    }

    private void checkForModification() {
        if (!init) {
            DesignerDialog dialog = (DesignerDialog) getTopLevelAncestor();
            if (version.equals(new Version(nameText.getText(), classMethodCheck.isSelected(), getComment(), getExpression()))) {
                dialog.setOkEnabled(false);
            } else {
                dialog.setOkEnabled(true);
            }
        }
    }

	public void itemStateChanged(ItemEvent e) {
		Object source = e.getSource();
		if (source == classMethodCheck) {
			checkForModification();
		}
	}
    
    public void actionPerformed(ActionEvent e) {
        ExpressionEditor ex = new ExpressionEditor(getExpression(), MethodPanel.this);
        ex.setSourceMethodToDebugger(method);
        Dimension dim = Utils.getScreenSize(Or3Frame.instance());
        DesignerDialog dlg = new DesignerDialog((Dialog)getTopLevelAncestor(), "Выражение", ex);
        dlg.setSize(new Dimension(kz.tamur.comps.Utils.getMaxWindowSizeActDisplay()));
        dlg.setLocation(kz.tamur.comps.Utils.getCenterLocationPoint(dlg.getSize()));
        dlg.show();
        if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
        	setExpression(ex.getExpression());
        }
    }

    public String getMethodName() {
        String text = Funcs.normalizeInput(nameText.getText());
        return (text != null && !"".equals(text)) ? text : null;
    }

    public boolean isClassMethod() {
        return classMethodCheck.isSelected();
    }

    public String getExpression() {
        return Funcs.normalizeInput(exprArea.getText());
    }
    
    public String getComment() {
    	return Funcs.normalizeInput(commentArea.getText());
    }
    
    private class Version {
    	private String methodName;
    	private boolean isClassMethod;
    	private String comment;
    	private String expression;
    	
    	public Version(String methodName, boolean isClassMethod, String comment, String expression) {
    		this.methodName = methodName;
    		this.isClassMethod = isClassMethod;
    		this.comment = comment;
    		this.expression = expression;
    	}
    	
    	public String getMethodName() {
			return methodName;
		}

		public boolean isClassMethod() {
			return isClassMethod;
		}

		public String getComment() {
			return comment;
		}

		public String getExpression() {
			return expression;
		}

		public boolean equals(Version version) {
    		if (!methodName.equals(version.getMethodName())) {
    			return false;
    		} 
    		if (isClassMethod != version.isClassMethod()) {
    			return false;
    		}
    		if (!comment.equals(version.getComment())) {
    			return false;
    		}
    		if (!expression.equals(version.getExpression())) {
    			return false;
    		} 
    		return true;
    	}
    }
}
