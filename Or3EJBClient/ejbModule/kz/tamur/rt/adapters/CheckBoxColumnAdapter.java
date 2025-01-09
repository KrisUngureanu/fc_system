package kz.tamur.rt.adapters;

import kz.tamur.comps.*;
import com.cifs.or2.kernel.KrnException;


/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 29.04.2004
 * Time: 9:24:31
 * To change this template use File | Settings | File Templates.
 */
public class CheckBoxColumnAdapter extends ColumnAdapter {
    private CheckBoxAdapter editor;
    
    public CheckBoxColumnAdapter(UIFrame frame, OrCheckColumn column) throws KrnException {
        super(frame, column, false);
        editor = new CheckBoxAdapter(frame, (OrCheckBox) column.getEditor(), true);
        celleditor = editor.getCellEditor();
    }

	
	@Override
	public Class getColumnClass() {
		return Boolean.class;
	}
    
}
