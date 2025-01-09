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
public class FigStartNode extends FigNamedNode {
    
    private FigCircle portFig;

    public FigStartNode(Object owner) {
        super(owner);
        portFig = new FigCircle(0, 0, 20, 20);
        addFig(portFig);
        addFig(new FigCircle(0, 0, 20, 20, Utils.getDarkShadowSysColor(),
                Utils.getDarkShadowSysColor()));
    }

    public Fig getPortFig() {
        return portFig;
    }

    public void setBounds(int x, int y, int w, int h) {
        Rectangle oldBounds = getBounds();
        super.setBounds(x, y, oldBounds.width, oldBounds.height);
        calcBounds();
        firePropChange("bounds", oldBounds, getBounds());
    }
}
