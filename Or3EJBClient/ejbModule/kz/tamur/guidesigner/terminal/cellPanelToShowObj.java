package kz.tamur.guidesigner.terminal;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

public class cellPanelToShowObj implements TableCellRenderer{
	private TableCellRenderer defaultRenderer = new DefaultTableCellRenderer();

	private Map<Class, TableCellRenderer> registeredRenderers = new HashMap<Class, TableCellRenderer>();

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		TableCellRenderer delegate = null;
		if ( value != null && value.toString().equals("KrnObject")) {
			//delegate = getDelegate(value.getClass());
			return onKrnObj(table, value, isSelected, hasFocus, row, column);
		}

		if (delegate == null) {
			delegate = defaultRenderer;
		}

		return delegate.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}
	
	private Component onKrnObj(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
		KrnObjCellComponent comp = new KrnObjCellComponent(table.getValueAt(row, 2));
		comp.load(table, value, isSelected, row, column);
		return comp;
	}
}