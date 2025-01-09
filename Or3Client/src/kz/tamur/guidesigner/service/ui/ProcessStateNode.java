package kz.tamur.guidesigner.service.ui;

import kz.tamur.guidesigner.service.NodeProperty;
import kz.tamur.guidesigner.service.NodeEventType;
import kz.tamur.guidesigner.service.ServiceModel;
import org.tigris.gef.presentation.FigNode;
import org.tigris.gef.base.Layer;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 04.10.2004
 * Time: 16:06:21
 * To change this template use File | Settings | File Templates.
 */
public class ProcessStateNode extends StateNode {
    public ProcessStateNode() {
        super();
        type="Процесс";
    }

    public ProcessStateNode(ServiceModel model) {
        super(model);
        type="Процесс";
    }

    public ProcessStateNode(String id,ServiceModel model) {
        super(id,model);
        type="Процесс";
    }

    public NodeProperty[] getProperties() {
        return new NodeProperty[] {NODE_ID,NODE_TITLE, RESPONSIBLE,CHOPPER,CONFLICT_PROCESS,INSPECTORS,UI_INF_PROCESS
        							,PROCESS_OBJECT_INF,UI_TYPE_INF,EVENT_BEFORE_COMMT,EVENT_AFTER_COMMT};
    }

    public NodeEventType[] getEventTypes() {
        return new NodeEventType[] {PROCESS_INSTANCE_START, PROCESS_INSTANCE_END, BEFORE_PROCESS_INSTANCE_CANCEL, PROCESS_INSTANCE_CANCEL, PROCESS_INSTANCE_AFTER_END};
    }

    public FigNode makePresentation(Layer layer) {
        return null;
    }
}
