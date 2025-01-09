package kz.tamur.util;

import javax.swing.plaf.basic.BasicToolBarUI;
import javax.swing.border.Border;
import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 19.05.2004
 * Time: 21:28:29
 * To change this template use File | Settings | File Templates.
 */
public class OrToolBarUI extends BasicToolBarUI {

    private Border nonRolloverBorder;
    //private Border rolloverBorder;

    protected Border createNonRolloverBorder() {
        return BorderFactory.createLineBorder(kz.tamur.rt.Utils.getDarkShadowSysColor());
    }



    protected void setBorderToNonRollover(Component c) {
        if (c instanceof AbstractButton) {
            AbstractButton b = (AbstractButton)c;
            if (b.getBorder() != null) {
                if (b instanceof JToggleButton && !(b instanceof JCheckBox)) {
                    if (nonRolloverBorder == null) {
                        nonRolloverBorder = createNonRolloverBorder();
                    }
                    b.setBorder(nonRolloverBorder);
                }
            }
        }
    }

}
