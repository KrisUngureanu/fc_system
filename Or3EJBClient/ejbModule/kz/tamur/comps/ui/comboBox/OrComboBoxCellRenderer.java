package kz.tamur.comps.ui.comboBox;

import java.awt.Component;
import java.awt.Dimension;
import java.util.List;

import javax.swing.*;

import kz.tamur.rt.adapters.ComboBoxAdapter.OrComboItem;


public class OrComboBoxCellRenderer extends DefaultListCellRenderer {
    private JComponent comboBox;
    private OrComboBoxElement renderer;

    public OrComboBoxCellRenderer(JComponent comboBox) {
        super();
        this.comboBox = comboBox;
        renderer = new OrComboBoxElement();
    }

    public OrComboBoxElement getRenderer() {
        return renderer;
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        renderer.setIndex(index);
        renderer.setTotalElements(list.getModel().getSize());
        renderer.setSelected(isSelected);
        renderer.updatePainter();
        renderer.setEnabled(comboBox.isEnabled());
        renderer.setFont(comboBox.getFont());
        renderer.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
        renderer.setComponentOrientation(list.getComponentOrientation());
        
        
        if (value instanceof Icon) {
            renderer.setIcon((Icon) value);
            renderer.setText("");
        } else if (value instanceof OrComboItem && ((OrComboItem) value).isListTitle()) {
        	renderer.removeAll();
        	List titles = ((OrComboItem) value).getTitles();
        	StringBuilder sb = new StringBuilder("<html><table style=\"width: ").append(comboBox.getWidth()).append("px\"><tr>");
        	for (Object title : titles) {
	        	sb.append("<td style=\"width: ").append(comboBox.getWidth() / titles.size()).append("px\">").append(title != null ? title.toString() : "").append("</td>");
        	}
        	sb.append("</tr></table></html>");
        	renderer.setText(sb.toString());
        	renderer.setPreferredSize(new Dimension(comboBox.getWidth(), renderer.getMinimumSize().height));
        } else {
            renderer.setIcon(null);
            renderer.setText(value == null ? "" : value.toString());
            if (value == null ||value.toString().isEmpty()) {
                // необходимо для пустых элементов
                renderer.setPreferredSize(new Dimension(20,20));
            }
        }
        return renderer;
    }
    
    public String buildTitles(Object value) {
    	List titles = ((OrComboItem) value).getTitles();
    	StringBuilder sb = new StringBuilder();
    	for (int i = 0; i < titles.size() - 1; i++) {
    		Object title = titles.get(i);
    		sb.append(title != title ? title : "").append(" ");
    	}
    	Object title = titles.get(titles.size() - 1);
    	sb.append(title != null ? title : "");
    	return sb.toString();
    }
}

