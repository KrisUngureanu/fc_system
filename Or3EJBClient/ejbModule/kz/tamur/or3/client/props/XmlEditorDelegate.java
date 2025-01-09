package kz.tamur.or3.client.props;

import com.cifs.or2.client.util.CnrBuilder;
import kz.tamur.Or3Frame;
import kz.tamur.comps.Utils;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.users.Or3RightsTree;
import kz.tamur.or3.client.props.inspector.EditorDelegate;
import kz.tamur.or3.client.props.inspector.PropertyEditor;
import kz.tamur.or3.client.props.inspector.RendererDelegate;
import kz.tamur.rt.MainFrame;

import org.jdom.Element;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class XmlEditorDelegate extends JPanel implements EditorDelegate, RendererDelegate, ActionListener {

	private Element value;
    private PropertyEditor editor;

	private JLabel label;
	private JButton exprBtn;
	private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
	
    public XmlEditorDelegate(JTable table) {
        setLayout(new GridBagLayout());
        label = new JLabel();
        label.setFont(table.getFont());

        exprBtn = kz.tamur.comps.Utils.createBtnEditor(this);
        add(label, new CnrBuilder().x(1).wtx(1).fill(GridBagConstraints.HORIZONTAL).build());
        add(exprBtn, new CnrBuilder().x(0).build());
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
        Element xml = null;
        if(value!=null && !"".equals(value))
            xml=(Element)value;
        this.value = xml;
		if (xml != null) {
			label.setText(xml.toString());
		} else {
			label.setText("");
		}
	}

	public Component getRendererComponent() {
		return this;
	}

	public void setPropertyEditor(PropertyEditor editor) {
		this.editor=editor;

	}
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == exprBtn) {
            Or3RightsTree tree= Utils.getOr3RightsTree(value);
            JScrollPane scroller = new JScrollPane(tree);
            scroller.setPreferredSize(new Dimension(500, 600));
            scroller.setOpaque(isOpaque);
            scroller.getViewport().setOpaque(isOpaque);
            DesignerDialog dlg = new DesignerDialog(Or3Frame.instance(),
                    "Выберите права OR3", scroller, true);
            dlg.show();
            int res = dlg.getResult();
            if (res == ButtonsFactory.BUTTON_OK) {
                Element rights = tree.getOr3Rights();
                value = rights;
                editor.stopCellEditing();
            } else if (res == ButtonsFactory.BUTTON_CLEAR){
                value = null;
                editor.stopCellEditing();
            }else
                editor.cancelCellEditing();
        }
    }

}
