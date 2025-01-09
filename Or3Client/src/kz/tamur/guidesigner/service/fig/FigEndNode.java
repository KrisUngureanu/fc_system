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
public class FigEndNode extends FigNamedNode {
    
    private FigCircle portFig;

    public FigEndNode(Object owner) {
        super(owner);
        portFig = new FigCircle(0, 0, 30, 30);
        addFig(portFig);
        addFig(new FigCircle(7, 7, 16, 16, Utils.getDarkShadowSysColor(),
                Utils.getDarkShadowSysColor()));
    }

    public Fig getPortFig() {
        return portFig;
    }

    public void setBounds(int x, int y, int w, int h) {
        Rectangle oldBounds = getBounds();
        super.setBounds(x, y, oldBounds.width, oldBounds.height);
        //portFig.setBounds(getBounds());
        calcBounds();
        firePropChange("bounds", oldBounds, getBounds());
    }
}
