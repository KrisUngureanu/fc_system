package kz.tamur.guidesigner.service.ui;

import kz.tamur.guidesigner.service.NodeProperty;
import kz.tamur.guidesigner.service.NodeEventType;
import kz.tamur.guidesigner.service.ServiceModel;
import kz.tamur.guidesigner.service.fig.FigOutBoxNode;
import org.tigris.gef.presentation.FigNode;
import org.tigris.gef.base.Layer;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 05.05.2005
 * Time: 9:46:01
 * To change this template use File | Settings | File Templates.
 */
public class OutBoxStateNode extends StateNode{
    public OutBoxStateNode() {
        super();
        type="Отправка";
    }

    public OutBoxStateNode(ServiceModel model) {
        super(model);
        type="Отправка";
    }

    public OutBoxStateNode(String id,ServiceModel model) {
        super(id,model);
        type="Отправка";
    }

    public NodeProperty[] getProperties() {
        return new NodeProperty[] {NODE_ID,EXCH_BOX,NODE_TITLE,ASSIGNMENT};
    }

    public NodeEventType[] getEventTypes() {
        return new NodeEventType[] {PERFORM_XML};
    }

    public FigNode makePresentation(Layer layer) {
        FigOutBoxNode res = new FigOutBoxNode(this);
        res.bindPort(port, res.getPortFig());
        setPresentation(res);
        return res;
    }
}
