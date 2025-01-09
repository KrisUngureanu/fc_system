package com.cifs.or2.server.workflow.definition.impl;

import kz.tamur.guidesigner.service.NodePropertyConstants;
import kz.tamur.lang.OrLang;
import kz.tamur.lang.parser.ASTStart;

import com.cifs.or2.server.Session;
import com.cifs.or2.server.workflow.definition.*;
import org.jdom.Element;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 15.08.2004
 * Time: 15:45:59
 * To change this template use File | Settings | File Templates.
 */
public class TransitionImpl extends DefinitionObjectImpl implements Transition {
    private Node from;
    private String toId;
    private Node to;
    private String id;
    private ASTStart synch;

    public Node getFrom() {
        return from;
    }

    public Node getTo() {
        if (to == null && toId != null) {
            to = getProcessDefinition().getNode(toId);
        }
        return to;
    }

    public void setTo(Node node) {
        to = node;
        setProperty("to", xml, node.getId(), false);
    }

    public TransitionImpl(ProcessDefinition processDefinition,
                          Node from,
                          Element xml, Session s) {
        super(processDefinition, xml, null, s);
        this.from = from;
        toId = getProperty("to", xml);
        id = getProperty("id", xml);
        String expr = getProperty(NodePropertyConstants.SYNCH.getName(), xml);
        synch = (expr != null && expr.trim().length() > 0) ? OrLang.createStaticTemplate(expr) : null;
    }

    public String getId() {
        return id;
    }

    public ASTStart getSynch() {
        return synch;
    }
}
