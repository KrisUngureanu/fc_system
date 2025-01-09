package kz.tamur.guidesigner.service.ui;

import kz.tamur.guidesigner.service.NodeProperty;
import kz.tamur.guidesigner.service.NodeEventType;
import kz.tamur.guidesigner.service.ServiceModel;
import kz.tamur.guidesigner.service.fig.FigNoteNode;
import org.tigris.gef.presentation.*;
import org.tigris.gef.base.Layer;


/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 16.09.2004
 * Time: 14:50:11
 * To change this template use File | Settings | File Templates.
 */
public class NoteStateNode extends StateNode {
    public NoteStateNode() {
        super();
        type="Примечание";
    }
    public NoteStateNode(ServiceModel model) {
        super(model);
        type="Примечание";
    }

    public NoteStateNode(String id,ServiceModel model) {
        super(id,model);
        type="Примечание";
    }

    public NodeProperty[] getProperties() {
        return new NodeProperty[] {NODE_ID};
    }

    public NodeEventType[] getEventTypes() {
        return new NodeEventType[0];
    }

    public FigNode makePresentation(Layer layer) {
        FigNoteNode res = new FigNoteNode(this);
        res.bindPort(port, res.getPortFig());
        setPresentation(res);
        return res;
    }
}
