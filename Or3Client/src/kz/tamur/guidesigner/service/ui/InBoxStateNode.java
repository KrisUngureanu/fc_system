package kz.tamur.guidesigner.service.ui;

import kz.tamur.guidesigner.service.NodeProperty;
import kz.tamur.guidesigner.service.NodeEventType;
import kz.tamur.guidesigner.service.ServiceModel;
import kz.tamur.guidesigner.service.fig.FigInBoxNode;
import org.tigris.gef.presentation.FigNode;
import org.tigris.gef.base.Layer;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 04.05.2005
 * Time: 15:10:37
 * To change this template use File | Settings | File Templates.
 */
public class InBoxStateNode extends StateNode{
    public InBoxStateNode() {
        super();
        type="Прием";
    }

    public InBoxStateNode(ServiceModel model) {
        super(model);
        type="Прием";
    }

    public InBoxStateNode(String id,ServiceModel model) {
        super(id,model);
        type="Прием";
    }

    public NodeProperty[] getProperties() {
        return new NodeProperty[] {NODE_ID,EXCH_BOX,NODE_TITLE,OBJECT_TITLE,ASSIGNMENT,DATE_ALARM, DATE_ALERT };
    }

    public NodeEventType[] getEventTypes() {
        return new NodeEventType[] {CHECK_XML, PARS_XML};
    }

    public FigNode makePresentation(Layer layer) {
        FigInBoxNode res = new FigInBoxNode(this);
        res.bindPort(port, res.getPortFig());
        setPresentation(res);
        return res;
    }
}
