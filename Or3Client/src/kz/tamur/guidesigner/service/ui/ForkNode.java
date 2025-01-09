package kz.tamur.guidesigner.service.ui;

import kz.tamur.guidesigner.service.NodeEventType;
import kz.tamur.guidesigner.service.NodeProperty;
import kz.tamur.guidesigner.service.ServiceModel;
import kz.tamur.guidesigner.service.fig.FigForkNode;
import org.tigris.gef.base.Layer;
import org.tigris.gef.presentation.FigNode;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 14.09.2004
 * Time: 16:47:23
 * To change this template use File | Settings | File Templates.
 */
public class ForkNode extends StateNode {

    public ForkNode() {
        super();
        type="Разветвление";
    }

    public ForkNode(ServiceModel model) {
        super(model);
        type="Разветвление";
    }

    public ForkNode(String id,ServiceModel model) {
        super(id,model);
        type="Разветвление";
    }

    public FigNode makePresentation(Layer lay) {
        FigForkNode res = new FigForkNode(this);
        res.bindPort(port, res.getPortFig());
        setPresentation(res);
        return res;
    }



    public NodeEventType[] getEventTypes() {
        return new NodeEventType[]{FORK };
    }

    public NodeProperty[] getProperties() {
        return new NodeProperty[] {NODE_ID};
    }
}
