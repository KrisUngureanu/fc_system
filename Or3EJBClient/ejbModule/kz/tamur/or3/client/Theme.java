package kz.tamur.or3.client;

import java.awt.Font;
import java.io.InputStream;

import org.apache.commons.collections.map.MultiKeyMap;

public class Theme {

	private static Theme currentTheme;
	
	private static MultiKeyMap fonts = new MultiKeyMap();
	
	public static Theme getCurrentTheme() {
		if (currentTheme == null) {
			currentTheme = new Theme();
		}
		return currentTheme;
	}
	
	public static void init(InputStream is) {
		currentTheme = new Theme();
	}

	public Font getFont(String name, int style, int size) {
		Font font;
		synchronized (fonts) {
			font = (Font)fonts.get(name, style, size);
			if (font == null) {
				font = new Font(name, style, size);
				fonts.put(name, style, size, font);
			}
		}
		return font;
	}
}
