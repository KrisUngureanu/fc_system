package kz.tamur.guidesigner.service.ui;

import org.tigris.gef.presentation.FigNode;
import org.tigris.gef.base.Layer;
import kz.tamur.guidesigner.service.fig.FigStartSyncNode;
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
public class StartSyncNode extends StateNode {

    public StartSyncNode() {
        super();
        type="Начало синхронизации";
    }

    public StartSyncNode(ServiceModel model) {
        super(model);
        type="Начало синхронизации";
    }

    public StartSyncNode(String id,ServiceModel model) {
        super(id,model);
        type="Начало синхронизации";
    }

    public FigNode makePresentation(Layer layer) {
        FigStartSyncNode res = new FigStartSyncNode(this);
        res.bindPort(port, res.getPortFig());
        setPresentation(res);
        return res;
    }

    public NodeProperty[] getProperties() {
        return new NodeProperty[]{NODE_ID,NODE_TITLE};
    }

    public NodeEventType[] getEventTypes() {
        return new NodeEventType[]{SYNC_START};
    }
}
