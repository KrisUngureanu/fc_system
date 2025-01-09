package kz.tamur.guidesigner.service.fig;

import kz.tamur.rt.Utils;
import kz.tamur.guidesigner.service.ServiceModel;
import kz.tamur.guidesigner.service.ui.StateNode;
import kz.tamur.guidesigner.service.ui.SubProcessStateNode;
import org.tigris.gef.presentation.Fig;
import org.tigris.gef.presentation.FigRect;
import org.tigris.gef.presentation.FigText;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 04.10.2004
 * Time: 18:43:50
 * To change this template use File | Settings | File Templates.
 */
public class FigSubProcessNode extends FigNamedNode {
    
    private FigRect rectFig;
    private FigRect portFig;

    public FigSubProcessNode(Object owner) {
        super(owner);
        portFig = new FigRect(0, 0, 100, 24, Utils.getDarkShadowSysColor(), Utils.getLightSysColor());
        addFig(portFig);

        rectFig = new FigRect(0, 0, 100, 24,Utils.getDarkShadowSysColor(), Utils.getLightSysColor());
        addFig(rectFig);

        FigText label = new FigText(15, 1, 90, 14, Utils.getDarkShadowSysColor(),
                "Tahoma", 11);
        label.setMultiLine(true);
        label.setLineColor(Utils.getLightSysColor());
        label.setText(getName());
        label.setFillColor(Utils.getLightSysColor());
        addFig(label);
        setNameFig(label);
    }

    public Fig getPortFig() {
        return portFig;
    }
    
    public void mouseClicked(MouseEvent event) {
        if (event.getClickCount() > 1) {
            StateNode owner = (StateNode)getOwner();
            boolean res=false;
            ServiceModel model = owner.getModel();
            if (model!=null) {
                res=owner.getModel().openProcess((SubProcessStateNode)owner);
            }
            if (!res)
                super.mouseClicked(event);
        } else {
            super.mouseClicked(event);
        }
    }

    public void setBounds(int x, int y, int w, int h) {
        Rectangle oldBounds = getBounds();
        Rectangle newBounds = new Rectangle(x, y, w, h);
        rectFig.setBounds(newBounds);
        portFig.setBounds(newBounds);
        getNameFig().setBounds(x + 10, y + (h - 21) / 2, w - 20, 21);
        calcBounds();
        firePropChange("bounds", oldBounds, getBounds());
    }
}
