package kz.tamur.util;

import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import kz.tamur.comps.Constants;
import kz.tamur.comps.ui.button.OrTransparentButton;
import kz.tamur.rt.MainFrame;

public class CalendarButton extends OrTransparentButton {
    private ActionListener copyAdapter;

    public ActionListener getCopyAdapter() {
        return copyAdapter;
    }

    private JComponent comp;

    public CalendarButton(JComponent comp, String title) {
        super();
		String iconName = MainFrame.iconsSettings.get("iconCalendar");
		ImageIcon icon = kz.tamur.rt.Utils.getImageIconFull(iconName);
		if (icon != null) {
			setIcon(icon);
		} else {
			setIcon(kz.tamur.rt.Utils.getImageIcon("JCalendar"));
		}
        this.comp = comp;
        setPreferredSize(new Dimension(20, 20));
        setMargin(Constants.INSETS_0);
        setCursor(Constants.DEFAULT_CURSOR);
        setToolTipText(title);
    }

    public void setCopyAdapter(ActionListener a) {
        this.copyAdapter = a;
        addActionListener(copyAdapter);
    }

    public JComponent getComponent() {
        return comp;
    }

    public void setCopyTitle(String title) {
        setToolTipText(title);
    }
}

