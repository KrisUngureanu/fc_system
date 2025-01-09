package kz.tamur.guidesigner.service.ui;

import org.tigris.gef.presentation.FigNode;
import org.tigris.gef.base.Layer;
import kz.tamur.guidesigner.service.fig.FigEndSyncNode;
import kz.tamur.guidesigner.service.NodeProperty;
import kz.tamur.guidesigner.service.NodeEventType;
import kz.tamur.guidesigner.service.ServiceModel;

/**
 * Created by IntelliJ IDEA.
 * User: ValeT
 * Date: 18.04.2006
 * Time: 18:45:51
 * To change this template use File | Settings | File Templates.
 */
public class EndSyncNode extends StateNode {
    public EndSyncNode() {
        super();
        type="Завершение синхронизации";
    }
    public EndSyncNode(ServiceModel model) {
        super(model);
        type="Завершение синхронизации";
    }

    public EndSyncNode(String id,ServiceModel model) {
        super(id,model);
        type="Завершение синхронизации";
    }

    public FigNode makePresentation(Layer layer) {
        FigEndSyncNode res = new FigEndSyncNode(this);
        res.bindPort(port, res.getPortFig());
        setPresentation(res);
        return res;
    }

    public NodeProperty[] getProperties() {
        return new NodeProperty[] {NODE_ID};
    }

    public NodeEventType[] getEventTypes() {
        return new NodeEventType[]{SYNC_STOP};
    }
}
