package kz.tamur.guidesigner.service.ui;

import kz.tamur.guidesigner.service.*;
import kz.tamur.guidesigner.service.fig.FigNamedNode;
import org.tigris.gef.graph.presentation.NetNode;
import org.tigris.gef.presentation.FigNode;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.TreeMap;
import java.util.Hashtable;

public abstract class StateNode extends NetNode
        implements PropertyChangeListener, NodePropertyConstants,
                   NodeEventTypeConstants, ServiceNodeIfc {

    private static int lastId = 0;
    protected NodePort port;
    protected Map properties_ = new TreeMap();
    protected Map actions_ = new TreeMap();
    private FigNamedNode presentation;
    private boolean isEnable=true;
    protected String type;

    private String id;
    private boolean isEditName=false;
    private boolean isEditDesc=false;
    private ServiceModel model;
    protected StateNode() {
        id = "" + (++lastId);
        setProperty(NODE_ID, id);
  }

    protected StateNode(ServiceModel model) {
        this.model=model;
        id = "" + (++lastId);
        setProperty(NODE_ID, id);
    }

    public String getType() {
        return type;
    }

    protected StateNode(String id,ServiceModel model) {
        this.id = id;
        this.model=model;
        int i = Integer.parseInt(id);
        if (lastId < i) {
            lastId = i;
        }
        setProperty(NODE_ID, id);
    }

    public String getId() {
        return id;
    }

    public FigNode getPresentation() {
        return presentation;
    }

    public void initialize(Hashtable args) {
        if(args!=null){
           Object o=args.get("enable");
            if(o!=null && o instanceof Boolean) isEnable=((Boolean)o).booleanValue();
        }
        setName("Безымянный");
        addPort(port = new NodePort(this));
    }

    public Map getPropertyMap() {
        return properties_;
    }

    public Map getActionMap() {
        return actions_;
    }
    public abstract NodeProperty[] getProperties();

    public abstract NodeEventType[] getEventTypes();

    public Object getProperty(NodeProperty prop) {
        return properties_.get(prop);
    }

    public Object getAction(NodeEventType eventType) {
        return actions_.get(eventType);
    }

    public void setProperty(NodeProperty prop, Object expression){
        properties_.put(prop, expression);
    }
    
    public void setProperty(NodeProperty prop, String expression){
        properties_.put(prop, expression);
    }

    public void setAction(NodeEventType type, String expression){
        actions_.put(type, expression);
    }


    public String getName() {
        return (String)getProperty(NODE_NAME);
    }

    public void setName(String name) {
        setProperty(NODE_NAME, name);
        if (presentation != null) {
            presentation.setName(name);
            presentation.redraw();
        }
        isEditName=true;
    }

/*    public String getTitle() {
        return (String)getProperty(NODE_TITLE);
    }

    public void setTitle(String title) {
        setProperty(NODE_TITLE, title);
    }

*/    public String getDescription() {
        return (String)getProperty(NODE_DESCRIPTION);
    }

    public void setDescription(String str) {
        setProperty(NODE_DESCRIPTION, str);
        isEditDesc=true;
    }


    public void setPresentation(FigNamedNode presentation) {
        this.presentation = presentation;
        presentation.addPropertyChangeListener(this);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() == presentation) {
            String pname = evt.getPropertyName();
            if ("nodeName".equals(pname)) {
                setName((String)evt.getNewValue());
                firePropertyChange("nodeName", evt.getOldValue(), evt.getNewValue());
            }
        }
    }
    public boolean isEnabled(){
        return isEnable;
    }

    public boolean isEditDesc() {
        return isEditDesc;
    }

    public boolean isEditName() {
        return isEditName;
    }

    public ServiceModel getModel() {
        return model;
    }
}
