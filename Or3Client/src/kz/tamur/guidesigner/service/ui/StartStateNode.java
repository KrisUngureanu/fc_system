package kz.tamur.guidesigner.service.ui;

import kz.tamur.guidesigner.service.NodeEventType;
import kz.tamur.guidesigner.service.NodeProperty;
import kz.tamur.guidesigner.service.ServiceModel;
import kz.tamur.guidesigner.service.fig.FigStartNode;
import org.tigris.gef.base.Layer;
import org.tigris.gef.presentation.FigNode;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 09.09.2004
 * Time: 15:56:58
 * To change this template use File | Settings | File Templates.
 */
public class StartStateNode extends StateNode {
    public StartStateNode() {
        super();
        type="Начало";
    }
    public StartStateNode(ServiceModel model) {
        super(model);
        type="Начало";
    }

    public StartStateNode(String id,ServiceModel model) {
        super(id,model);
        type="Начало";
    }

    public FigNode makePresentation(Layer layer) {
        FigStartNode res = new FigStartNode(this);
        res.bindPort(port, res.getPortFig());
        setPresentation(res);
        return res;
    }

    public NodeProperty[] getProperties() {
        return new NodeProperty[] {NODE_ID};
    }

    public NodeEventType[] getEventTypes() {
        return new NodeEventType[]{BEFORE_PERFORM_OF_ACTIVITY,AFTER_PERFORM_OF_ACTIVITY};
    }
}
