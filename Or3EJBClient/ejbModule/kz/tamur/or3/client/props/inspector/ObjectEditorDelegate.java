package kz.tamur.or3.client.props.inspector;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnObject;

public class ObjectEditorDelegate extends JButton implements EditorDelegate,
		ActionListener {

	private KrnObject value;
	
	private KrnClass cls;
	private KrnAttribute titleAttr;

	
	public ObjectEditorDelegate(KrnClass cls, KrnAttribute titleAttr) {
		this.cls = cls;
		this.titleAttr = titleAttr;
	}

	public int getClickCountToStart() {
		return 2;
	}

	public Component getEditorComponent() {
		return this;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = (KrnObject) value;
	}

	public void actionPerformed(ActionEvent e) {
	}

	public void setPropertyEditor(PropertyEditor editor) {
	}
	
}
