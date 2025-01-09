package kz.tamur.or3.client.props.inspector;

import kz.tamur.or3.client.props.ComboProperty;
import kz.tamur.or3.client.props.ComboPropertyItem;
import javax.swing.*;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.event.*;

public class ComboEditorDelegate extends JComboBox implements EditorDelegate {

	private ComboPropertyItem value;
	private PropertyEditor propertyEditor;

    public ComboEditorDelegate(ComboProperty prop, final JTable table) {
		super(prop.getItems());
        this.setFont(table.getFont());
        this.setBorder(BorderFactory.createLineBorder(kz.tamur.rt.Utils.getDarkShadowSysColor()));
        this.setBackground(kz.tamur.rt.Utils.getLightSysColor());
        this.setEditable(false);
        addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				value = (ComboPropertyItem)getSelectedItem();
			}
		});

        addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    propertyEditor.stopCellEditing();
                    table.requestFocusInWindow();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    propertyEditor.cancelCellEditing();
                    table.requestFocusInWindow();
                }
            }
        });

        Object popup = getUI().getAccessibleChild(null, 0);
        if (popup instanceof ComboPopup) {
            ((ComboPopup)popup).getList().addMouseListener(new MouseAdapter() {
                public void mouseReleased(MouseEvent e) {
                    super.mouseReleased(e);
                    propertyEditor.stopCellEditing();
                    table.requestFocusInWindow();
                }
            });
        }

	}

	public int getClickCountToStart() {
		return 1;
	}

	public Component getEditorComponent() {
		return this;
	}

	public Object getValue() {
        return value;
	}

	public void setValue(Object value) {
    	if (value instanceof ExprEditorObject) {
    		this.value = (ComboPropertyItem)((ExprEditorObject)value).getObject();
    	} else {
    		this.value = (ComboPropertyItem)value;
    	}

        this.setSelectedItem(this.value);
	}

	public void setPropertyEditor(PropertyEditor editor) {
		propertyEditor = editor;
	}
}
