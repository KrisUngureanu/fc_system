package kz.tamur.admin;

import javax.swing.table.TableCellRenderer;
import javax.swing.*;
import java.awt.*;

public class PasswdTableCellRenderer extends JLabel
																		 implements TableCellRenderer
{
	private boolean colorsAdjusted_ = false;

	public PasswdTableCellRenderer() {
		setText("******");
		setOpaque(true);
	}

	public Component getTableCellRendererComponent(JTable table,
																								 Object value,
																								 boolean isSelected,
																								 boolean hasFocus,
																								 int row, int column)
	{
		if (!colorsAdjusted_)
			adjustColors(table);
		if (isSelected) {
			setForeground(table.getSelectionForeground());
			setBackground(table.getSelectionBackground());
		}
		else {
			setForeground(table.getForeground());
			setBackground(table.getBackground());
		}
		return this;
	}

	private void adjustColors(JTable table) {
		colorsAdjusted_ = true;
		setFont(table.getFont());
	}
}
