package kz.tamur.util;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.AbstractCellEditor;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import kz.tamur.Or3Frame;
import kz.tamur.comps.Constants;
import kz.tamur.comps.Utils;

public class IconCellEditor extends AbstractCellEditor
implements TableCellEditor, TableCellRenderer, ActionListener, Icon {
	
	private static final long serialVersionUID = 1L;

	private BufferedImage image;
	
	private JButton button;
	
	
    public IconCellEditor() {
        super();
        button = new JButton();
        kz.tamur.rt.Utils.setAllSize(button, Constants.BTN_EDITOR_SIZE);
        button.setIcon(this);
        button.addActionListener(this);
    }

	public Object getCellEditorValue() {
		return image;
	}

	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		image = (BufferedImage)value;
		return button;
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		image = (BufferedImage)value;
		return button;
	}

	public void actionPerformed(ActionEvent e) {
		if (button == e.getSource()) {
            JFileChooser fChooser = Utils.createOpenChooser(Constants.IMAGE_FILTER);
            if (fChooser.showOpenDialog(Or3Frame.instance())
                    == JFileChooser.APPROVE_OPTION) {
                File sf = fChooser.getSelectedFile();
                kz.tamur.rt.Utils.setLastSelectDir(sf.getParentFile().toString());
                if (sf != null) {
                    try {
                    	image = ImageIO.read(sf);
                        stopCellEditing();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            cancelCellEditing();
		}
	}
	
	public void paintIcon(Component c, Graphics g, int x, int y) {
		if (image != null)
			g.drawImage(image, x, y, null);
	}
	
	public int getIconWidth() {
		return image != null ? image.getWidth() : 0;
	}
	
	public int getIconHeight() {
		return image != null ? image.getHeight() : 0;
	}
}
