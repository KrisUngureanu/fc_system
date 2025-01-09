package kz.tamur.guidesigner.service.fig;

import org.tigris.gef.presentation.Fig;
import org.tigris.gef.presentation.FigPoly;
import org.tigris.gef.presentation.FigText;
import kz.tamur.rt.Utils;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 04.10.2004
 * Time: 18:43:50
 * To change this template use File | Settings | File Templates.
 */
public class FigDecisionNode extends FigNamedNode {

    private FigPoly diamondFig;
    private FigPoly portFig;

    public FigDecisionNode(Object owner) {
        super(owner);
        portFig = new FigPoly(Utils.getDarkShadowSysColor(), Utils.getLightSysColor());
        portFig.addPoint(0,15);
        portFig.addPoint(40,30);
        portFig.addPoint(80,15);
        portFig.addPoint(40,0);
        addFig(portFig);

        diamondFig = new FigPoly(Utils.getDarkShadowSysColor(), Utils.getLightSysColor());
        diamondFig.addPoint(0,15);
        diamondFig.addPoint(40,30);
        diamondFig.addPoint(80,15);
        diamondFig.addPoint(40,0);
        diamondFig.addPoint(0,15);
        addFig(diamondFig);

        FigText label = new FigText(26,6, 10, 6, Utils.getDarkShadowSysColor(),
                "Tahoma", 11);
        label.setLineColor(Utils.getLightSysColor());
        label.setText("?");
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
        p.addPoint(x, y + (h / 2));
        p.addPoint(x + (w / 2), y);
        p.addPoint(x + w, y + (h / 2));
        p.addPoint(x + (w / 2), y + h);
        p.addPoint(x, y + (h / 2));
        diamondFig.setPolygon(p);
        portFig.setPolygon(p);
        getNameFig().setBounds(x + 25, y + (h -18) / 2, w -50, 14);
        calcBounds();
        firePropChange("bounds", oldBounds, getBounds());
    }
}
