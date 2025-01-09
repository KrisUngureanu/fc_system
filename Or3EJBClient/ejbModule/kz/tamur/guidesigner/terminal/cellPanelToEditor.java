package kz.tamur.guidesigner.terminal;

import java.awt.Component;
import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

public class cellPanelToEditor extends AbstractCellEditor implements TableCellEditor{
	 
	  public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			TableCellEditor delegate = null;
			if (value.toString().equals("KrnObject")) {
				return onKrnObj(table, value, isSelected, row, column);
			}

			if (delegate == null) {
				delegate = table.getCellEditor(row, column);
			}

			return delegate.getTableCellEditorComponent(table, value, isSelected, row, column);
	  }
	 
	  private Component onKrnObj(JTable table, Object value, boolean isSelected, int row, int column){
		  KrnObjCellComponent comp = new KrnObjCellComponent(table.getValueAt(row, 2));
		  comp.load(table, value, isSelected, row, column);
		  return comp;
	  }
	  
	  public Object getCellEditorValue() {
	    return null;
	  }
}
