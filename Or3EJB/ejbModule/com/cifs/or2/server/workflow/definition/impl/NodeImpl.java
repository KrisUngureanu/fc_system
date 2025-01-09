package com.cifs.or2.server.workflow.definition.impl;

import com.cifs.or2.server.Session;
import com.cifs.or2.server.workflow.definition.Node;
import com.cifs.or2.server.workflow.definition.ProcessDefinition;
import com.cifs.or2.server.workflow.definition.ProcessBlock;
import com.cifs.or2.server.workflow.definition.Transition;
import org.jdom.Element;

import java.util.*;
import java.util.List;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 15.08.2004
 * Time: 15:38:02
 * To change this template use File | Settings | File Templates.
 */
public class NodeImpl extends DefinitionObjectImpl implements Node {
    private ProcessBlock processBlock;
    private Collection<TransitionImpl> arrivingTransitions;
    private Collection<TransitionImpl> leavingTransitions;
    private Rectangle bounds;
    private String id;

    public ProcessBlock getProcessBlock() {
        return processBlock;
    }

    public Collection<TransitionImpl> getArrivingTransitions() {
        if (arrivingTransitions == null) {
            arrivingTransitions = new ArrayList<TransitionImpl>();
            Collection<NodeImpl> nodes = processBlock.getNodes();
            for (Iterator<NodeImpl> nodeIt = nodes.iterator(); nodeIt.hasNext();) {
                NodeImpl node = nodeIt.next();
                Collection<TransitionImpl> leavingTransitions = node.getLeavingTransitions();
                for (Iterator<TransitionImpl> trIt = leavingTransitions.iterator();
                        trIt.hasNext();) {
                	TransitionImpl transition = trIt.next();
                    if (transition.getTo() == this) {
                        arrivingTransitions.add(transition);
                    }
                }
            }
        }
        return arrivingTransitions;
    }

    public Collection<TransitionImpl> getLeavingTransitions() {
        return leavingTransitions;
    }
    public String getId() {
        return id;
    }

    public String getType() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Transition createTransition() {
        TransitionImpl transition = new TransitionImpl(
                getProcessDefinition(), this,
                new Element("transition"), session);
        setProperty("from", transition.xml, getId(), false);
        leavingTransitions.add(transition);
        xml.addContent(transition.xml);
        return transition;
    }

    public void removeTransition(Transition tr) {
        leavingTransitions.remove(tr);
        xml.removeContent(((TransitionImpl)tr).xml);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void setBounds(Rectangle rect) {
        this.bounds = rect;
        String str = rect.x + "," + rect.y + ","
                + rect.width + "," + rect.height;
        setProperty("bounds", xml, str, false);
    }

    protected NodeImpl(ProcessDefinition processDefinition,
                       ProcessBlock processBlock,
                       Element xml, Session s) {
        super(processDefinition, xml, null, s);
        this.processBlock = processBlock;
        // Id
        ProcessDefinitionImpl pdImpl = (ProcessDefinitionImpl)processDefinition;
        id = getProperty("id", xml);
        if (id == null) {
            id = pdImpl.getNextNodeId();
            setProperty("id", xml, id, true);
        } else {
            pdImpl.checkLastNodeId(id);
        }
        // Границы
        String boundsStr = getProperty("bounds", xml);
        if (boundsStr != null) {
            StringTokenizer t = new StringTokenizer(boundsStr, ",");
            if (t.countTokens() == 4) {
                int x = Integer.parseInt(t.nextToken());
                int y = Integer.parseInt(t.nextToken());
                int w = Integer.parseInt(t.nextToken());
                int h = Integer.parseInt(t.nextToken());
                bounds = new Rectangle(x, y, w, h);
            }
        }
        // Исходящие переходы
        leavingTransitions = new ArrayList<TransitionImpl>();
        List es = xml.getChildren("transition");
        for (int i = 0; i < es.size(); i++) {
            Element e = (Element) es.get(i);
            TransitionImpl transition = new TransitionImpl(
                    processDefinition, this, e, s);
            leavingTransitions.add(transition);
        }
    }
    public void refreash(ProcessDefinition processDefinition,
                       Element xml, Session s){
        init(processDefinition, xml, null, s);
        arrivingTransitions=null;
        // Исходящие переходы
        leavingTransitions = new ArrayList<TransitionImpl>();
        List es = xml.getChildren("transition");
        for (int i = 0; i < es.size(); i++) {
            Element e = (Element) es.get(i);
            TransitionImpl transition = new TransitionImpl(
                    processDefinition, this, e, s);
            leavingTransitions.add(transition);
        }
    }
}
