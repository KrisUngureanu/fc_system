package kz.tamur.or3.client.util;

import javax.swing.JMenuItem;

import kz.tamur.util.LangItem;

public class LangMenuItem extends JMenuItem {
	
	private LangItem langItem;

	public LangMenuItem(LangItem langItem) {
		super(langItem.name, langItem.icon);
		this.langItem = langItem;
	}

	public LangItem getLangItem() {
		return langItem;
	}
}
