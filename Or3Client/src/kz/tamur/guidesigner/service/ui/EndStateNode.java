package kz.tamur.guidesigner.service.ui;

import org.tigris.gef.presentation.FigNode;
import org.tigris.gef.base.Layer;
import kz.tamur.guidesigner.service.fig.FigEndNode;
import kz.tamur.guidesigner.service.NodeProperty;
import kz.tamur.guidesigner.service.NodeEventType;
import kz.tamur.guidesigner.service.ServiceModel;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 16.09.2004
 * Time: 10:17:26
 * To change this template use File | Settings | File Templates.
 */
public class EndStateNode extends StateNode {
    public EndStateNode() {
        super();
        type="Завершение";
    }
    public EndStateNode(ServiceModel model) {
        super(model);
        type="Завершение";
    }

    public EndStateNode(String id,ServiceModel model) {
        super(id,model);
        type="Завершение";
    }
    public FigNode makePresentation(Layer layer) {
        FigEndNode res = new FigEndNode(this);
        res.bindPort(port, res.getPortFig());
        setPresentation(res);
        return res;
    }

    public NodeProperty[] getProperties() {
        return new NodeProperty[] {NODE_ID};
    }

    public NodeEventType[] getEventTypes() {
        return new NodeEventType[0];
    }
}
