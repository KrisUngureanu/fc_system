package kz.tamur.comps.gui;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.*;

import javax.swing.*;
import javax.swing.border.Border;

import kz.tamur.comps.OrComboBox;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.OrTreeControl2;
import kz.tamur.comps.OrTreeCtrl;
import kz.tamur.rt.adapters.ComponentAdapter;
import kz.tamur.rt.adapters.ComboBoxAdapter;

public class DefaultFocusAdapter implements FocusListener {
    private Border mouseMoveBorder = BorderFactory.createLineBorder(new Color(163, 184, 204));
    private Border mouseMoveBorder4 = BorderFactory.createLineBorder(new Color(163, 184, 204), 2);

    private final ComponentAdapter adapter;
    private OrGuiComponent comp;
    private boolean oldPainted;
    private Border oldBorder;

    public DefaultFocusAdapter(ComponentAdapter adapter) {
       this.adapter = adapter;
    }

    public DefaultFocusAdapter(OrGuiComponent comp) {
       this.comp = comp;
       this.adapter = null;
    }

    public void focusGained(FocusEvent e) {
        if (adapter != null) {
            OrGuiComponent comp = adapter.getComponent();
            if (comp instanceof OrComboBox) {
                JTextField tf =
                        (JTextField) ((OrComboBox) comp).getEditor().getEditorComponent();
                if (tf.isEnabled()) {
                    tf.setBackground(kz.tamur.rt.Utils.getLightYellowColor());
                    ((ComboBoxAdapter)adapter).setFocused(true);
                }
                ((ComboBoxAdapter)adapter).calculateContent();
            } else if (comp.isEnabled()) {
                if (comp instanceof JComponent && !((JComponent)comp).isEnabled())
                    return;
                if (!(comp instanceof OrComboBox)) {
                    ((JComponent) comp).setBackground(kz.tamur.rt.Utils.getLightYellowColor());
                }
            }
        } else {
            oldBorder = ((JComponent)comp).getBorder();
            if (comp instanceof JButton) {
                oldPainted = ((JButton)comp).isBorderPainted();
                ((JButton)comp).setBorderPainted(true);
                ((JButton)comp).setBorder(mouseMoveBorder);
            } else if (comp instanceof JComponent) {
                if (comp instanceof OrTreeCtrl || comp instanceof OrTreeControl2) {
                    ((JComponent)comp).setBorder(mouseMoveBorder4);
                } else {
                    ((JComponent)comp).setBorder(mouseMoveBorder);
                }
            }
        }
    }

    public void focusLost(FocusEvent e) {
        if (adapter != null) {
            OrGuiComponent comp = adapter.getComponent();
            if (comp instanceof OrComboBox) {
                ((ComboBoxAdapter)adapter).setFocused(false);
                if (((OrComboBox) comp).getEditor() != null) {
	                JTextField tf = (JTextField) ((OrComboBox) comp).getEditor().getEditorComponent();
	                tf.setBackground(adapter.getBgColor(0));
	                if (((OrComboBox) comp).getComboBox().isPopupVisible()) {
	                    ((OrComboBox) comp).getComboBox().hidePopup();
	                }
                }
                //((OrComboBox) comp).setBackground(Color.lightGray);
            } else if (comp.isEnabled()) {
                if (comp instanceof JComponent && !((JComponent) comp).isEnabled())
                    return;
                if (!(comp instanceof OrComboBox)) {
                    ((JComponent) comp).setBackground(adapter.getBgColor(0));
                }
            }
        } else {
            if (comp instanceof JButton) {
                ((JButton)comp).setBorderPainted(oldPainted);
                ((JButton)comp).setBorder(oldBorder);
            } else if (comp instanceof JComponent) {
                ((JComponent)comp).setBorder(oldBorder);
            }
        }
    }

}
