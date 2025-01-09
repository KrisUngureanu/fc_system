package kz.tamur.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import kz.tamur.Or3Frame;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.or3.client.props.Expression;
import kz.tamur.or3.client.props.inspector.EditorDelegateSet;

public class ExpressionCellEditor extends AbstractCellEditor implements TableCellEditor, TableCellRenderer, ActionListener, EditorDelegateSet {
	private static final long serialVersionUID = 1L;
	private Expression expr;
	private JButton exprBtn;
	
	private String stringValue = null;
	
    public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	public ExpressionCellEditor() {
        super();
        exprBtn = kz.tamur.comps.Utils.createBtnEditor(this);
    }

	public Object getCellEditorValue() {
		return expr;
	}

	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		expr = (Expression)value;
		return exprBtn;
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		expr = (Expression)value;
		return exprBtn;
	}

	public void actionPerformed(ActionEvent e) {
		if (exprBtn == e.getSource()) {
			String text = expr != null ? expr.text : "";
			stringValue = text;
	        ExpressionEditor exprEditor = new ExpressionEditor(text, ExpressionCellEditor.this);
	        DesignerDialog dlg = new DesignerDialog(Or3Frame.instance(), "Выражение", exprEditor);
	        dlg.setSize(new Dimension(kz.tamur.comps.Utils.getMaxWindowSizeActDisplay()));
	            dlg.setLocation(kz.tamur.comps.Utils.getCenterLocationPoint(dlg.getSize()));
	            dlg.show();
	            if (dlg.isOK()) {
	            	setExpression(exprEditor.getExpression());
	            } else if (dlg.getResult() == ButtonsFactory.BUTTON_CANCEL) {
	                cancelCellEditing();
	        }
		}
	}
	
	public void setExpression(String expression) {
        expr = expression.trim().length() > 0 ? new Expression(expression) : null;
        stopCellEditing();
	}
}
