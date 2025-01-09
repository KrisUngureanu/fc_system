package kz.tamur.admin.clsbrow;

import com.cifs.or2.kernel.KrnAttribute;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Valeri
 * Date: 01.06.2009
 * Time: 16:31:17
 * To change this template use File | Settings | File Templates.
 */
public class KrnObjectProperty extends ObjectProperty{
    private KrnAttribute attr;

    public KrnObjectProperty(ObjectProperty parent, KrnAttribute attr) {
        super(parent, attr);
        this.attr=attr;
    }

    @Override
    public ObjectEditorDelegate createEditorDelegate(JTable table) {
        return new KrnAttrEditorDelegate(table,attr);
    }
    public ObjectRendererDelegate createRendererDelegate(JTable table) {
        return new KrnAttrEditorDelegate(table,attr);
    }

}
