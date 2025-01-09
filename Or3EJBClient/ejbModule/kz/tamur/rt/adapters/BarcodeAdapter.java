package kz.tamur.rt.adapters;

import com.cifs.or2.kernel.KrnException;

import kz.tamur.comps.OrBarcode;
import kz.tamur.comps.OrComboBox;
import kz.tamur.comps.OrFrame;

public class BarcodeAdapter extends ComponentAdapter {

	public BarcodeAdapter(OrFrame frame, OrBarcode barcode, boolean isEditor) throws KrnException {
		super(frame, barcode, isEditor);
		
		
	}
	
	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	public void setLangId(long langId) {
		// TODO Auto-generated method stub
		
	}
	
	// RefListener
    public void valueChanged(OrRefEvent e) {
        super.valueChanged(e);
         
    }
	
}
