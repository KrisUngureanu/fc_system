package kz.tamur.guidesigner.service.ui;

import kz.tamur.guidesigner.service.NodeProperty;
import kz.tamur.guidesigner.service.NodeEventType;
import kz.tamur.guidesigner.service.ServiceModel;
import kz.tamur.guidesigner.service.fig.FigSubProcessNode;
import org.tigris.gef.presentation.*;
import org.tigris.gef.base.Layer;


/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 16.09.2004
 * Time: 14:50:11
 * To change this template use File | Settings | File Templates.
 */
public class SubProcessStateNode extends StateNode {
    public SubProcessStateNode() {
        super();
        type="Подпроцесс";
    }

    public SubProcessStateNode(ServiceModel model) {
        super(model);
        type="Подпроцесс";
    }

    public SubProcessStateNode(String id,ServiceModel model) {
        super(id,model);
        type="Подпроцесс";
    }

    public NodeProperty[] getProperties() {
        return new NodeProperty[] {NODE_ID,PROCESS, SUBPROCESS_TYPE, RETURN_VAR_TYPE,SUB_ROLLBACK_TYPE};
    }

    public NodeEventType[] getEventTypes() {
        return new NodeEventType[]{SUB_PROCESS_INSTANCE_START,SUB_PROCESS_INSTANCE_COMPLETION };
    }

    public FigNode makePresentation(Layer layer) {
        FigSubProcessNode res = new FigSubProcessNode(this);
        res.bindPort(port, res.getPortFig());
        setPresentation(res);
        return res;
    }
}
