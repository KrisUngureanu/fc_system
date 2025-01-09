package kz.tamur.guidesigner.service.fig;

import org.tigris.gef.presentation.*;
import kz.tamur.rt.Utils;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 04.10.2004
 * Time: 18:43:50
 * To change this template use File | Settings | File Templates.
 */
public class FigActivityNode extends FigNamedNode {
    
    private FigRRect rrectFig;
    private FigRRect portFig;

    public FigActivityNode(Object owner) {
        super(owner);
        portFig = new FigRRect(0, 0, 100, 24, Utils.getDarkShadowSysColor(), Utils.getLightSysColor());
        addFig(portFig);

        rrectFig = new FigRRect(0, 0, 100, 24,Utils.getDarkShadowSysColor(), Utils.getLightSysColor());
        addFig(rrectFig);

        FigText label = new FigText(15, 1, 90, 14, Utils.getDarkShadowSysColor(),
                "Tahoma", 11);
        label.setLineColor(Utils.getLightSysColor());
        label.setText(getName());
        label.setFillColor(Utils.getLightSysColor());
        addFig(label);
        setNameFig(label);
    }

    public Fig getPortFig() {
        return portFig;
    }

    public void setBounds(int x, int y, int w, int h) {
        Rectangle oldBounds = getBounds();
        Rectangle newBounds = new Rectangle(x, y, w, h);
        rrectFig.setBounds(newBounds);
        portFig.setBounds(newBounds);
        getNameFig().setBounds(x + 10, y + (h - 21) / 2, w - 20, 21);
        calcBounds();
        firePropChange("bounds", oldBounds, getBounds());
    }
}
