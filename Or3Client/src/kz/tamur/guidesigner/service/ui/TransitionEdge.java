package kz.tamur.guidesigner.service.ui;

import kz.tamur.guidesigner.service.fig.FigTransitionEdge;
import kz.tamur.guidesigner.service.NodeEventType;
import kz.tamur.guidesigner.service.NodeProperty;
import kz.tamur.guidesigner.service.NodePropertyConstants;
import kz.tamur.guidesigner.service.ServiceModel;
import org.tigris.gef.base.Layer;
import org.tigris.gef.graph.presentation.NetEdge;
import org.tigris.gef.graph.presentation.NetPort;
import org.tigris.gef.graph.GraphModel;
import org.tigris.gef.presentation.ArrowHeadTriangle;
import org.tigris.gef.presentation.FigEdge;
import java.util.Vector;
import java.util.Map;
import java.util.TreeMap;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 09.09.2004
 * Time: 18:29:35
 * To change this template use File | Settings | File Templates.
 */
public class TransitionEdge extends NetEdge implements PropertyChangeListener, NodePropertyConstants, ServiceNodeIfc {
    private static int nextId;
    private String id;
    protected Map properties_ = new TreeMap();
    private ServiceModel model;

    public String getName() {
        return name;
    }
    
    public void setAction(NodeEventType type, String expression){
    	
    	throw new UnsupportedOperationException("In TransitionEdge methed setAction is not supported");
    	
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;
    private FigTransitionEdge presentation;
    public TransitionEdge() {
        id = "" + (++nextId);
        setProperty(NODE_ID, id);
   }

    public TransitionEdge(ServiceModel model) {
        this.model = model;
        id = "" + (++nextId);
        setProperty(NODE_ID, id);
  }

    public TransitionEdge(String id,ServiceModel model) {
        this.id = id;
        this.model = model;
        int i = Integer.parseInt(id);
        if (nextId < i) {
            nextId = i;
        }
        setProperty(NODE_ID, id);
  }

    public Map getPropertyMap() {
        return properties_;
    }

    public Object getProperty(NodeProperty prop) {
        return properties_.get(prop);
    }
    public void setProperty(NodeProperty prop, String expression) {
        this.properties_.put(prop,expression);
    }

    public NodeProperty[] getProperties() {
        return new NodeProperty[]{NODE_ID,EDGE_JOIN, SYNCH};
    }
    public FigEdge getPresentation() {
        return presentation;
    }
    public FigEdge makePresentation(Layer layer) {
        presentation = new FigTransitionEdge();
        presentation.setBetweenNearestPoints(true);
        presentation.setDestArrowHead(new ArrowHeadTriangle());
        return presentation;
    }

    public boolean connect(GraphModel gm, Object srcPort, Object destPort) {
      StateNode src = (StateNode)((NetPort)srcPort).getParent();
      StateNode dst = (StateNode)((NetPort)destPort).getParent();
      if(dst instanceof StartStateNode
         || src instanceof EndStateNode
         || srcPort.equals(destPort)
         ||(dst instanceof JoinNode && src instanceof ForkNode)
         ||(dst instanceof EndStateNode && src instanceof ForkNode)) return false;
      Vector edges=((NetPort)srcPort).getEdges();
      for(int i=0;i<edges.size();++i){
         TransitionEdge edge=(TransitionEdge)edges.get(i);
         if(edge.getDestPort().equals(destPort) && edge.getSourcePort().equals(srcPort)) return false;
//         else if(edge.getDestPort().equals(srcPort) && edge.getSourcePort().equals(destPort)) return false;
         else if(!edge.getDestPort().equals(srcPort)
                 && !(src instanceof ActivityStateNode) 
                 && !(src instanceof DecisionStateNode)
                 && !(src instanceof ForkNode)) return false;
      }
      super.connect(gm,srcPort,destPort);
      return true;
    }
    public String getId() {
        return id;
    }

    public Vector getPoints() {
        return presentation.getPointsVector();
    }

    public void setPoints(Vector pts) {
        presentation.setPointsVector(pts);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() == presentation) {
            String pname = evt.getPropertyName();
            if ("edgeName".equals(pname)) {
                setName((String)evt.getNewValue());
                firePropertyChange("edgeName", evt.getOldValue(), evt.getNewValue());
            }
        }

    }

    public ServiceModel getModel() {
        return model;
    }
}
