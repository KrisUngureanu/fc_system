package kz.tamur.rt.orlang;

import kz.tamur.comps.OrFrame;

public abstract class AbstractClientPlugin implements ClientPlugin {
	
	public OrFrame getFrame() {
		return ClientOrLang.getFrame();
	}
}
