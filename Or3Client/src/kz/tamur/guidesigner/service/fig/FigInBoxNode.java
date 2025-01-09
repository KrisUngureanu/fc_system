package kz.tamur.guidesigner.service.fig;

import org.tigris.gef.presentation.*;
import kz.tamur.rt.Utils;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 03.05.2005
 * Time: 19:59:18
 * To change this template use File | Settings | File Templates.
 */
public class FigInBoxNode  extends FigNamedNode {

    private FigPoly rectFig;
    private FigRect portFig;

    public FigInBoxNode(Object owner) {
        super(owner);
        Polygon poly=new Polygon();
        poly.addPoint(0,0);
        poly.addPoint(10,24);
        poly.addPoint(110,24);
        poly.addPoint(100,0);
        poly.addPoint(0,0);
        portFig = new FigRect(10, 0, 90, 24, Utils.getLightSysColor(), Utils.getLightSysColor());
        addFig(portFig);

        rectFig = new FigPoly(Utils.getDarkShadowSysColor(), Utils.getLightSysColor());
        rectFig.setPolygon(poly);
        addFig(rectFig);

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
        Polygon p = new Polygon();
        p.addPoint(x, y );
        p.addPoint(x + 10, y+h);
        p.addPoint(x + w, y + h);
        p.addPoint(x + w - 10, y );
        p.addPoint(x, y );
        rectFig.setPolygon(p);
        portFig.setBounds(x+10, y, w-20, h);
        getNameFig().setBounds(x + 10, y + (h - 21) / 2, w - 30, 21);
        calcBounds();
        firePropChange("bounds", oldBounds, getBounds());
    }
}
