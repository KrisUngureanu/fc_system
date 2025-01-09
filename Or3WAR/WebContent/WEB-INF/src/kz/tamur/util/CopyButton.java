package kz.tamur.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

import kz.tamur.comps.Constants;

public class CopyButton extends JButton {
    private ActionListener copyAdapter;

    private JComponent comp;

    public CopyButton(JComponent comp, String title) {
        super(kz.tamur.rt.Utils.getImageIcon("copyAttr"));
        this.comp = comp;
        setPreferredSize(new Dimension(20, 20));
        setMargin(Constants.INSETS_0);
        setCursor(Cursor.getDefaultCursor());
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

