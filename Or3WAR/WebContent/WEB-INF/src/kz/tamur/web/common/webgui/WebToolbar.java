package kz.tamur.web.common.webgui;

import java.awt.GridBagConstraints;

import org.jdom.Element;

import kz.tamur.comps.OrFrame;

public class WebToolbar extends WebPanel {

	private int alignment;

	public WebToolbar(Element xml, int mode, OrFrame frame, String id) {
		this(GridBagConstraints.WEST, xml, mode, frame, id);
	}
	
	public WebToolbar(int align, Element xml, int mode, OrFrame frame, String id) {
		super(xml, mode, frame, id);
		this.alignment  = align;
	}

	public void getHTML(StringBuilder res) {
		res.append("<div class=\"nav\">");
		for (WebComponent comp : children) {
			if (comp instanceof WebButton) {
				WebButton b = (WebButton)comp;
				if (b.isVisible()) {
					res.append("<a id=\"rlb\" class=\"brand\" data-placement=\"bottom\" rel=\"tooltip\" title=\"").append(b.getToolTip()).append("\" href=\"#\">");
					res.append("<img src=\"").append(b.getIconPath()).append("\" />");
					res.append("</a>");
				}
			}
		}
		res.append("</div>");
	}

	public int getAlignment() {
		return alignment;
	}
}
