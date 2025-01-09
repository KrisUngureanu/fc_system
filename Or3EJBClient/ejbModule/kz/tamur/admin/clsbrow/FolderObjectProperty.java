package kz.tamur.admin.clsbrow;

import com.cifs.or2.kernel.KrnAttribute;

import javax.swing.*;

public class FolderObjectProperty extends ObjectProperty {

	public FolderObjectProperty(ObjectProperty parent, KrnAttribute attr) {
		super(parent, attr);
	}

	@Override
	public ObjectEditorDelegate createEditorDelegate(JTable table) {
		return null;
	}
    public ObjectRendererDelegate createRendererDelegate(JTable table) {
        return null;
    }

}
