package kz.tamur.admin.clsbrow;

import com.cifs.or2.kernel.KrnAttribute;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Valeri
 * Date: 02.06.2009
 * Time: 16:04:10
 * To change this template use File | Settings | File Templates.
 */
public class StringObjectProperty extends ObjectProperty{
    private KrnAttribute attr;

    public StringObjectProperty(ObjectProperty parent, KrnAttribute attr) {
        super(parent, attr);
        this.attr=attr;
    }

    @Override
    public ObjectEditorDelegate createEditorDelegate(JTable table) {
        return new StringAttrEditorDelegate(table,attr);
    }

    public ObjectRendererDelegate createRendererDelegate(JTable table) {
        return new StringAttrEditorDelegate(table,attr);
    }
}
