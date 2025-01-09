package com.cifs.or2.server.workflow.definition.impl;

import com.cifs.or2.server.Session;
import com.cifs.or2.server.workflow.definition.*;

import java.util.*;

import org.jdom.Element;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 15.08.2004
 * Time: 15:27:31
 * To change this template use File | Settings | File Templates.
 */
public class ProcessBlockImpl extends DefinitionObjectImpl implements ProcessBlock {
    protected ProcessBlock parentBlock;
    private List<Node> nodes = new ArrayList<Node>();
    private Map<String,Node> nodesById = new HashMap<String,Node>();
    protected Map<String,ProcessBlockImpl> childBlocks = new HashMap<String,ProcessBlockImpl>();

    public Collection<Node> getNodes() {
        return new ArrayList<Node>(nodes);
    }

    public Collection getAttributes() {
        return Collections.EMPTY_LIST;
    }

    public Node getNode(String id) {
        Node res = nodesById.get(id);
        if (res == null) {
            for (Object childBlock : childBlocks.values()) {
                ProcessBlock block = (ProcessBlock) childBlock;
                res = block.getNode(id);
                if (res != null) {
                    break;
                }
            }
        }
        return res;
    }

    public void addNode(Node n) {
        addNode(n, false);
    }

    public void addNode(Node n, boolean isLoading) {
        nodes.add(n);
        nodesById.put(n.getId(), n);
        if (!isLoading) {
            xml.addContent(((NodeImpl)n).xml);
        }
    }

    public void removeNode(Node n) {
        nodes.remove(n);
        nodesById.remove(n.getId());
        xml.removeContent(((NodeImpl)n).xml);
        ProcessDefinition pd = getProcessDefinition();
        if (pd != this) {
            pd.removeNode(n);
        }
    }

    public ProcessBlock getParentBlock() {
        return parentBlock;
    }

    public Collection getChildBlocks() {
        return new ArrayList<ProcessBlock>(childBlocks.values());
    }

    public ConcurrentBlock createConcurrentBlock() {
        ConcurrentBlockImpl block = new ConcurrentBlockImpl(
                getProcessDefinition(), this, new Element("concurrent-block"), session);
        xml.addContent(block.xml);
        String id = block.xml.getAttribute("id").getValue();
        childBlocks.put(id,block);
        return block;
    }

    public void removeChildBlock(ProcessBlock block) {
        Element e= ((ProcessBlockImpl)block).xml;
        xml.removeContent(e);
        childBlocks.remove(e.getAttribute("id").getValue());
    }

    public void addChildBlock(ProcessBlock block) {
        Element e= ((ProcessBlockImpl)block).xml;
        childBlocks.put(e.getAttribute("id").getValue(),(ProcessBlockImpl)block);
        xml.addContent(((ProcessBlockImpl)block).xml);
    }

    protected void init(ProcessDefinition processDefinition,
                        ProcessBlock parentBlock,
                        Element xml, String name, Session s) {
        super.init(processDefinition, xml, name, s);

        this.parentBlock = parentBlock;

        List children = xml.getChildren();
        for (Object aChildren : children) {
            Element e = (Element) aChildren;
            createNewNode(processDefinition, e, s);
        }
    }
    public void refreash(ProcessDefinition processDefinition, Element xml, String name, Session s){
    	super.init(processDefinition, xml, name, s);
        List children = xml.getChildren();
        for (Object aChildren : children) {
            Element e = (Element) aChildren;
            refreashNode(processDefinition, e, s);
        }
    }

    protected Node createNewNode(ProcessDefinition processDefinition, Element e, Session s){
        NodeImpl node = null;
        String name = e.getName();
        if ("activity-state".equals(name)) {
            node = new ActivityStateImpl(processDefinition, this, e, s);
        } else if ("process-state".equals(name)) {
            node = new ProcessStateImpl(processDefinition, this, e, s);
        } else if ("start-sync-state".equals(name)) {
            node = new StartSyncState(processDefinition, this, e, s);
        } else if ("end-sync-state".equals(name)) {
            node = new EndSyncState(processDefinition, this, e, s);
        } else if ("inbox-state".equals(name)) {
            node = new InBoxStateImpl(processDefinition, this, e, s);
        } else if ("outbox-state".equals(name)) {
            node = new OutBoxStateImpl(processDefinition, this, e, s);
        } else if ("decision".equals(name)) {
            node = new DecisionImpl(processDefinition, this, e, s);
        } else if ("fork".equals(name)) {
            node = new ForkImpl(processDefinition, this, e, s);
        } else if ("join".equals(name)) {
            node = new JoinImpl(processDefinition, this, e, s);
        } else if ("concurrent-block".equals(name)) {
            ConcurrentBlockImpl block = new ConcurrentBlockImpl(
                    processDefinition, this, e, s);
            String id=e.getAttributeValue("id");
            if(id==null || id.equals("")) id="0";
            childBlocks.put(id,block);
        }
        if (node != null) {
            addNode(node, true);
        }
        return node;

    }

    protected void refreashNode(ProcessDefinition processDefinition,Element e, Session s){
        NodeImpl n;
        String name = e.getName();
        if ("activity-state".equals(name)
                || "process-state".equals(name)
                || "start-sync-state".equals(name)
                || "end-sync-state".equals(name)
                || "inbox-state".equals(name)
                || "outbox-state".equals(name)
                || "decision".equals(name)
                || "fork".equals(name)
                || "join".equals(name)) {
            n=(NodeImpl)nodesById.get(getProperty("id",e));
            if (n != null) {
                n.refreash(processDefinition, e, s);
            }else{
                createNewNode(processDefinition, e, s);
            }
        }else if("concurrent-block".equals(name)){
            ProcessBlockImpl block= childBlocks.get(e.getAttributeValue("id"));
            if(block!=null){
                List es=e.getChildren();
                for(Object ee : es){
                    Element e_=(Element)ee;
                    block.refreashNode(processDefinition, e_, s);
                }
            }else{
                createNewNode(processDefinition, e, s);
            }

        }

    }
    public ActivityState createActivityState() {
        ProcessDefinition pd = getProcessDefinition();
        ActivityState activity = new ActivityStateImpl(
                pd, this, new Element("activity-state"), session);
        addNode(activity);
        return activity;
    }

    public Join createJoin() {
        Join join = new JoinImpl(getProcessDefinition(), this, new Element("join"), session);
        addNode(join, true);
        return join;
    }


}
