package kz.tamur.rt;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;

import kz.tamur.comps.Constants;
import kz.tamur.guidesigner.ButtonsFactory;

public class ToolButton extends ButtonsFactory.DesignerToolButton implements Descriptionable {

    private String desc;
    private JLabel actionLabel;

    public ToolButton(String iconName, String toolTip, JLabel actionLabel) {
        this(iconName, Constants.SE_UI ? ".png" : ".gif", toolTip, actionLabel);
    }

    public ToolButton(String iconName, String ext, String toolTip, JLabel actionLabel) {
        super(iconName, ext, toolTip);
        this.actionLabel = actionLabel;
        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                ToolButton.this.actionLabel.setText(getToolTipText());
                super.mouseEntered(e);
            }

            public void mouseExited(MouseEvent e) {
                ToolButton.this.actionLabel.setText("");
                super.mouseExited(e);
            }
        });
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
