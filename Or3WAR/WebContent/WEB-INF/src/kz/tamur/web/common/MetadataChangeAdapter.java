package kz.tamur.web.common;

import kz.tamur.or3ee.common.MetadataChangeListener;
import kz.tamur.rt.orlang.ClientOrLang;
import kz.tamur.web.component.WebFrame;

public class MetadataChangeAdapter implements MetadataChangeListener {
	
	private int configNumber = 0;

	public MetadataChangeAdapter(int configNumber) {
		super();
		this.configNumber = configNumber;
	}

	@Override
	public void ifcChanged(long ifcId) {
		WebFrame.reloadInterface(ifcId, configNumber);
		ClientOrLang.clearActions(ifcId);
	}
}
