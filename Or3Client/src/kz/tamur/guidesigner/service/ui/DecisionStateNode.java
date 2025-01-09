package kz.tamur.guidesigner.service.ui;

import kz.tamur.guidesigner.service.NodeProperty;
import kz.tamur.guidesigner.service.NodeEventType;
import kz.tamur.guidesigner.service.ServiceModel;
import kz.tamur.guidesigner.service.fig.FigDecisionNode;
import org.tigris.gef.presentation.*;
import org.tigris.gef.base.Layer;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 16.09.2004
 * Time: 10:58:04
 * To change this template use File | Settings | File Templates.
 */
public class DecisionStateNode extends StateNode {
    public DecisionStateNode() {
        super();
        type="Решение";
    }
    public DecisionStateNode(ServiceModel model) {
        super(model);
        type="Решение";
    }

    public DecisionStateNode(String id,ServiceModel model) {
        super(id,model);
        type="Решение";
    }

    public NodeProperty[] getProperties() {
        return new NodeProperty[] {NODE_ID,} ;
    }

    public NodeEventType[] getEventTypes() {
        return new NodeEventType[]{BEFORE_DECISION,AFTER_DECISION};
    }

    public FigNode makePresentation(Layer layer) {
        FigDecisionNode res = new FigDecisionNode(this);
        // Порт
        res.bindPort(port, res.getPortFig());
        // Основной ромб
        setPresentation(res);
        return res;
    }
    public void setName(String name) {
        if(name==null || name.equals("") || name.equals("Безымянный"))
            name="?";
        super.setName(name);
    }

}
