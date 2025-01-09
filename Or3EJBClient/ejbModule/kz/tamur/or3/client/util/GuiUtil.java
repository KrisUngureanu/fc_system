package kz.tamur.or3.client.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;

import kz.tamur.guidesigner.DesignerDialog;

public class GuiUtil {

	public static DesignerDialog createDesignerDialog(
			Container parent, String title, Component content) {
		if (parent instanceof Frame) {
			return new DesignerDialog((Frame)parent, title, content);
		} else if (parent instanceof Dialog) {
			return new DesignerDialog((Dialog)parent, title, content);
		}
		return null;
	}
}
