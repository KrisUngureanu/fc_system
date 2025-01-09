package kz.tamur.util;

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.Utils;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.ClassNode;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnMethod;

public class MethodEditorPanel extends JPanel implements ActionListener {
	
	private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
	private JButton methodEditBtn = ButtonsFactory.createToolButton("MethodEditIcon", ".png", "Редактировать метод");
	private JComboBox methodsCombo = Utils.createCombo();
	private KrnMethod[] methods;
	private JTextArea textArea;
	private Kernel kernel = Kernel.instance();
	
	public MethodEditorPanel(final KrnMethod[] methods) {
		super();
		this.methods = methods;
		setOpaque(isOpaque);
		setLayout(new GridBagLayout());
		
		for (int i = 0; i < this.methods.length; i++) {
			StringBuilder item = new StringBuilder("Метод '" + this.methods[i].name + "'");
			try {
				item.append(", класс '" +  kernel.getClass(this.methods[i].classId).name + "'");
			} catch (KrnException exception) {
				exception.printStackTrace();
			} 
			methodsCombo.addItem(item);
		}
		
		Utils.setAllSize(methodsCombo, new Dimension(475, 25));
		methodsCombo.addActionListener(this);
		methodsCombo.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				textArea.setText(getExpression(methodsCombo.getSelectedIndex()));
				textArea.setCaretPosition(0);
			}
        });
		
		methodEditBtn.addActionListener(this);
		
		add(methodsCombo, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(7, 5, 5, 2), 0, 0));
		add(methodEditBtn, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(7, 0, 5, 7), 0, 0));
		
		textArea = new JTextArea(getExpression(0));
		textArea.setEditable(false);
		JScrollPane scrollpane = new JScrollPane(textArea);
		Utils.setAllSize(scrollpane, new Dimension(505, 250));
		add(scrollpane, new GridBagConstraints(0, 1, 2, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));
	}
	
	private String getExpression(int index) {
		String expression = "Не удается извлечь код метода.";
		try {
         	ClassNode classNode = kernel.getClassNode(methods[index].classId);
			expression = classNode.getMethodExpression(methods[index].name);
		} catch (KrnException exception) {
			exception.printStackTrace();
		}
		return expression;
	}
	
	private void updateMethod(KrnMethod method, int index) throws KrnException {
		methods[index] = kernel.getMethodById(method.uid);
		textArea.setText(kernel.getMethodExpression(methods[index].uid));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == methodEditBtn) {
			KrnMethod method = methods[methodsCombo.getSelectedIndex()];
			ExpressionEditor expressionEditor = new ExpressionEditor(getExpression(methodsCombo.getSelectedIndex()));
			expressionEditor.setSourceMethodToDebugger(method);
			DesignerDialog dialog = null;
			Container container = this.getTopLevelAncestor();
			if (container instanceof Dialog) {
				dialog = new DesignerDialog((Dialog) container, "Редактирование метода '" + method.name + "'", expressionEditor);
			} else {
				dialog = new DesignerDialog((Frame) container, "Редактирование метода '" + method.name + "'", expressionEditor);
			}
			dialog.setSize(new Dimension(kz.tamur.comps.Utils.getMaxWindowSizeActDisplay()));
			dialog.setLocation(kz.tamur.comps.Utils.getCenterLocationPoint(dialog.getSize()));
			dialog.setOkVisible(true);
            dialog.show();
            if (dialog.getResult() == ButtonsFactory.BUTTON_OK) {                            
				try {
					ClassNode classNode = kernel.getClassNode(method.classId);
					classNode.changeMethod(method.uid, method.name, method.isClassMethod, expressionEditor.getExpression());
					updateMethod(method, methodsCombo.getSelectedIndex());
				} catch (KrnException exception) {
					exception.printStackTrace();
	                Container cnt = getTopLevelAncestor();
	                MessagesFactory.showMessageDialog(cnt, MessagesFactory.ERROR_MESSAGE,
	                        "Нельзя редактировать метод!\r\n" + exception.getMessage());
				}
           }
		}
	}
}