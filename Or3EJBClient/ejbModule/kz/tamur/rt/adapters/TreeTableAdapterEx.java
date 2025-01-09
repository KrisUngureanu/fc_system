package kz.tamur.rt.adapters;

import kz.tamur.comps.OrFrame;
import kz.tamur.comps.OrTreeTable;

import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;

public class TreeTableAdapterEx extends TableAdapterEx {

	public TreeTableAdapterEx(OrFrame frame, OrTreeTable table, boolean isEditor)
			throws KrnException {
		
		super(frame, table, isEditor);
	}

	public int getRow(KrnObject value) {
		return columnAdapters.get(0).indexOf(value);
	}
}
