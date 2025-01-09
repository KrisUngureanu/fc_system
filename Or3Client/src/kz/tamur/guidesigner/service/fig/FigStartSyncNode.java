package kz.tamur.guidesigner.service.fig;

import org.tigris.gef.presentation.Fig;
import org.tigris.gef.presentation.FigPoly;
import org.tigris.gef.presentation.FigText;

import java.awt.*;

import kz.tamur.rt.Utils;

/**
 * Created by IntelliJ IDEA.
 * User: ValeT
 * Date: 18.04.2006
 * Time: 18:30:37
 * To change this template use File | Settings | File Templates.
 */
public class FigStartSyncNode  extends FigNamedNode {
    private FigPoly diamondFig;
    private FigPoly portFig;

    public FigStartSyncNode(Object owner) {
        super(owner);
        portFig = new FigPoly(Utils.getDarkShadowSysColor(), Utils.getLightSysColor());
        portFig.addPoint(0,0);
        portFig.addPoint(20,30);
        portFig.addPoint(40,0);
        portFig.addPoint(20,0);
        addFig(portFig);

        diamondFig = new FigPoly(Utils.getDarkShadowSysColor(), Utils.getLightSysColor());
        diamondFig.addPoint(0,0);
        diamondFig.addPoint(20,30);
        diamondFig.addPoint(40,0);
        diamondFig.addPoint(0,0);
        addFig(diamondFig);

        FigText label = new FigText(6,3, 1,1, Utils.getDarkShadowSysColor(),
                "Tahoma", 11);
        label.setLineColor(null);
        label.setText("S");
        label.setFillColor(null);
        addFig(label);
        setNameFig(label);
    }

    public Fig getPortFig() {
        return portFig;
    }

    public void setBounds(int x, int y, int w, int h) {
        Rectangle oldBounds = getBounds();
        Polygon p = new Polygon();
        p.addPoint(x, y);
        p.addPoint(x + w, y );
        p.addPoint(x + (w / 2), y + h);
        p.addPoint(x, y );
        diamondFig.setPolygon(p);
        p.addPoint(x+w/2, y );
        portFig.setPolygon(p);
        getNameFig().setBounds(x + (w / 2), y + (h / 3)-10,0,0);
        calcBounds();
        firePropChange("bounds", oldBounds, getBounds());
    }

}
