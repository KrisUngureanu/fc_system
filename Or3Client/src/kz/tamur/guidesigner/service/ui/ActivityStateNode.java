package kz.tamur.guidesigner.service.ui;

import java.util.Hashtable;

import kz.tamur.guidesigner.service.NodeEventType;
import kz.tamur.guidesigner.service.NodeProperty;
import kz.tamur.guidesigner.service.ServiceModel;
import kz.tamur.guidesigner.service.fig.FigActivityNode;
import org.tigris.gef.base.Layer;
import org.tigris.gef.presentation.FigNode;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 09.09.2004
 * Time: 18:13:47
 * To change this template use File | Settings | File Templates.
 */
public class ActivityStateNode extends StateNode {
    public ActivityStateNode() {
        super();
        type="Действие";
    }
    public ActivityStateNode(ServiceModel model) {
        super(model);
        type="Действие";
    }

    public ActivityStateNode(String id,ServiceModel model) {
        super(id,model);
        type="Действие";
    }

    public NodeProperty[] getProperties() {
        return new NodeProperty[] {NODE_ID,NODE_TITLE, ASSIGNMENT,ENABLE_CHOPPER,PROCESS_OBJECT,OBJECT_TITLE,OBJECT_PARAM,UI_PROCESS,
                                   UI_TYPE,DATE_ALARM, DATE_ALERT,TASK_COLOR,ACT_REPORT_REQUIRE,ACT_AUTO_NEXT,ERROR_PROCESS };
    }

    public NodeEventType[] getEventTypes() {
        return new NodeEventType[] {BEFORE_ACTIVITYSTATE_ASSIGNMENT, AFTER_ACTIVITYSTATE_ASSIGNMENT,
                                    BEFORE_PERFORM_OF_ACTIVITY, BEFORE_OPEN_INTERFACE,PERFORM_OF_ACTIVITY,AFTER_PERFORM_OF_ACTIVITY };
    }

    public FigNode makePresentation(Layer layer) {
        FigActivityNode res = new FigActivityNode(this);
        res.bindPort(port, res.getPortFig());
        setPresentation(res);
        return res;
    }

    @Override
	public void initialize(Hashtable args) {
		super.initialize(args);
		setProperty(ASSIGNMENT, "#return $user_");
	}
}
