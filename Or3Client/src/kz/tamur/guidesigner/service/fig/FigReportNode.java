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
public class FigReportNode extends FigNamedNode {
    
    private FigPoly polyFig;
    private FigPoly cornerFig;
    private FigRect cornerLSFig;
    private FigRect cornerBSFig;
    private FigPoly portFig;

    public FigReportNode(Object owner) {
        super(owner);
        portFig = new FigPoly(Utils.getDarkShadowSysColor(), Utils.getLightSysColor());
        portFig.addPoint(0, 0);
        portFig.addPoint(100, 0);
        portFig.addPoint(120, 20);
        portFig.addPoint(120, 70);
        portFig.addPoint(0, 70);
        portFig.addPoint(0, 0);
        addFig(portFig);

        polyFig = new FigPoly(Utils.getDarkShadowSysColor(), Color.lightGray);
        polyFig.addPoint(0, 0);
        polyFig.addPoint(100, 0);
        polyFig.addPoint(120, 20);
        polyFig.addPoint(120, 70);
        polyFig.addPoint(0, 70);
        polyFig.addPoint(0, 0);
        addFig(polyFig);

        cornerFig = new FigPoly(Utils.getDarkShadowSysColor(), Color.lightGray);
        cornerFig.addPoint(100, 0);
        cornerFig.addPoint(100, 20);
        cornerFig.addPoint(120, 20);
        addFig(cornerFig);

        cornerLSFig = new FigRect(98, 1, 2, 22,  Color.gray, Color.gray);
        addFig(cornerLSFig);

        cornerBSFig = new FigRect(100, 21, 20, 2,  Color.gray, Color.gray);
        addFig(cornerBSFig);

        FigText label = new FigText(5, 5, 97, 60, Utils.getDarkShadowSysColor(),
                "Tahoma", 11);
        label.setMultiLine(false);
        //label.setExpandOnly(true);
        label.setJustification(FigText.JUSTIFY_LEFT);
        label.setLineColor(Color.lightGray);
        label.setText(getName());
        label.setFillColor(Color.lightGray);
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
        p.addPoint(x + (w - 20), y);
        p.addPoint(x + w, y + 20);
        p.addPoint(x + w, y + h);
        p.addPoint(x, y + h);
        p.addPoint(x, y);
        polyFig.setPolygon(p);
        portFig.setPolygon(p);
        p = new Polygon();
        p.addPoint(x + (w - 20), y);
        p.addPoint(x + (w - 20), y + 20);
        p.addPoint(x + w, y + 20);
        cornerFig.setPolygon(p);
        Rectangle r = new Rectangle(x + (w - 22), y + 1, 2, 22);
        cornerLSFig.setBounds(r);
        r = new Rectangle(x + (w - 20), y + 21, 20, 2);
        cornerBSFig.setBounds(r);
        getNameFig().setBounds(x + 5, y + 5, w - 28, h - 20);
        calcBounds();
        firePropChange("bounds", oldBounds, getBounds());
    }
}
