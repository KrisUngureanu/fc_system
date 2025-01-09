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
public class FigJoinNode extends FigNamedNode {
    
    private FigRect rectFig;
    private FigRect portFig;

    public FigJoinNode(Object owner) {
        super(owner);
        portFig = new FigRect(0, 0, 100, 12);
        addFig(portFig);

        rectFig = new FigRect(0, 0, 100, 12, Utils.getDarkShadowSysColor(),
                Utils.getDarkShadowSysColor());
        addFig(rectFig);
    }

    public Fig getPortFig() {
        return portFig;
    }

    public void setBounds(int x, int y, int w, int h) {
        Rectangle oldBounds = getBounds();
        super.setBounds(x, y, w, oldBounds.height);
        portFig.setBounds(getBounds());
        calcBounds();
        firePropChange("bounds", oldBounds, getBounds());
    }
}
