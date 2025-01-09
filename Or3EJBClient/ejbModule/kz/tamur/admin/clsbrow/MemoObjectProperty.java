package kz.tamur.admin.clsbrow;

import com.cifs.or2.kernel.KrnAttribute;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Valeri
 * Date: 04.06.2009
 * Time: 15:43:14
 * To change this template use File | Settings | File Templates.
 */
public class MemoObjectProperty extends ObjectProperty{
    private KrnAttribute attr;

    public MemoObjectProperty(ObjectProperty parent, KrnAttribute attr) {
        super(parent, attr);
        this.attr=attr;
    }

    @Override
    public ObjectEditorDelegate createEditorDelegate(JTable table) {
        return new MemoAttrEditorDelegate(table,attr);
    }
    public ObjectRendererDelegate createRendererDelegate(JTable table) {
        return new MemoAttrEditorDelegate(table,attr);
    }

}
