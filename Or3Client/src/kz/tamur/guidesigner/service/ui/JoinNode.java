package kz.tamur.guidesigner.service.ui;

import kz.tamur.guidesigner.service.NodeEventType;
import kz.tamur.guidesigner.service.NodeProperty;
import kz.tamur.guidesigner.service.ServiceModel;
import kz.tamur.guidesigner.service.fig.FigJoinNode;
import org.tigris.gef.base.Layer;
import org.tigris.gef.presentation.FigNode;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 14.09.2004
 * Time: 18:45:38
 * To change this template use File | Settings | File Templates.
 */
public class JoinNode extends StateNode {
    public JoinNode() {
        super();
        type="Слияние";
    }


    public JoinNode(ServiceModel model) {
        super(model);
        type="Слияние";
    }

    public JoinNode(String id,ServiceModel model) {
        super(id,model);
        type="Слияние";
    }
    public FigNode makePresentation(Layer lay) {
        FigJoinNode res = new FigJoinNode(this);
        res.bindPort(port, res.getPortFig());
        setPresentation(res);
        return res;
    }

    public NodeEventType[] getEventTypes() {
        return new NodeEventType[]{JOIN};
    }

    public NodeProperty[] getProperties() {
        return new NodeProperty[] {NODE_ID};
    }
}
