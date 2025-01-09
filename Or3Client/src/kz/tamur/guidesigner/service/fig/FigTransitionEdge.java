package kz.tamur.guidesigner.service.fig;

import org.tigris.gef.presentation.FigEdgePoly;
import org.tigris.gef.presentation.Fig;
import org.tigris.gef.presentation.FigPoly;

import java.beans.PropertyChangeEvent;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Vector;

import kz.tamur.guidesigner.service.ui.TransitionEdge;
import kz.tamur.guidesigner.service.ui.StateNode;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 13.09.2004
 * Time: 12:06:40
 * To change this template use File | Settings | File Templates.
 */
public class FigTransitionEdge extends FigEdgePoly {
    protected Fig makeEdgeFig() {
        Fig res = super.makeEdgeFig();
        res.addPropertyChangeListener(this);
        return res;
    }

    public void propertyChange(PropertyChangeEvent event) {
        super.propertyChange(event);
    }

    protected void layoutEdge() {
        super.layoutEdge();
        getFig().firePropChange("bounds", null, getFig().getBounds());
    }

    public Vector getPointsVector() {
        FigPoly fig = (FigPoly)getFig();
        return fig.getPointsVector();
    }

    public void setPointsVector(Vector points) {
        FigPoly fig = (FigPoly)getFig();
        Polygon p = new Polygon();
        for (int i = 0; i < points.size(); i++) {
            Point pt = (Point) points.elementAt(i);
            p.addPoint(pt.x, pt.y);
        }
        fig.setPolygon(p);
    }

    public String getTipString(MouseEvent me) {
        if(!((StateNode)((TransitionEdge)getOwner()).getSourcePort().getParent()).isEnabled())
            return null;
        return "Переход";
    }
}
