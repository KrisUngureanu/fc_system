package kz.tamur.web.component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.cifs.or2.kernel.KrnException;

import kz.tamur.comps.OrFrame;
import kz.tamur.rt.adapters.ComponentAdapter;
import kz.tamur.rt.adapters.OrRefEvent;
import kz.tamur.web.OrWebBarcode;

public class BarcodeAdapter extends ComponentAdapter {

	public BarcodeAdapter(OrFrame frame, OrWebBarcode orWebBarcode) throws KrnException {
		// TODO Auto-generated constructor stub
		super(frame,orWebBarcode,false);
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}
	
	public void copy(File src, File dst) throws IOException {
        FileInputStream is = new FileInputStream(src);
        byte[] buf = new byte[(int) src.length()];
        is.read(buf);
        is.close();
        FileOutputStream os = new FileOutputStream(dst);
        os.write(buf);
        os.close();
    }

    public void copy(byte[] src, File dst) throws IOException {
        FileOutputStream os = new FileOutputStream(dst);
        os.write(src);
        os.close();
    }
    
    public void valueChanged(OrRefEvent e) {
    	super.valueChanged(e);
    	
    }
}
