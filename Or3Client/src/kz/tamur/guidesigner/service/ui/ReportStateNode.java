package kz.tamur.guidesigner.service.ui;

import kz.tamur.guidesigner.service.NodeProperty;
import kz.tamur.guidesigner.service.NodeEventType;
import kz.tamur.guidesigner.service.ServiceModel;
import kz.tamur.guidesigner.service.fig.FigReportNode;
import org.tigris.gef.presentation.*;
import org.tigris.gef.base.Layer;


/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 16.09.2004
 * Time: 14:50:11
 * To change this template use File | Settings | File Templates.
 */
public class ReportStateNode extends StateNode {
    public ReportStateNode() {
        super();
        type="Примечание";
    }

    public ReportStateNode(ServiceModel model) {
        super(model);
        type="Примечание";
    }

    public ReportStateNode(String id,ServiceModel model) {
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
        FigReportNode res = new FigReportNode(this);
        res.bindPort(port, res.getPortFig());
        setPresentation(res);
        return res;
    }
}
