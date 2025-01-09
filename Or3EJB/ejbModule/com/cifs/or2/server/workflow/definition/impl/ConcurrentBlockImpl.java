package com.cifs.or2.server.workflow.definition.impl;

import com.cifs.or2.server.Session;
import com.cifs.or2.server.workflow.definition.*;
import org.jdom.Element;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 15.08.2004
 * Time: 17:07:23
 * To change this template use File | Settings | File Templates.
 */
public class ConcurrentBlockImpl extends ProcessBlockImpl
        implements ConcurrentBlock {
    private Fork fork;
    private Join join;

    public Fork getFork() {
        return fork;
    }

    public Join getJoin() {
        return join;
    }

    public Fork createFork() {
        if (fork == null) {
            fork = new ForkImpl(getProcessDefinition(), this, new Element("fork"), session);
            addNode(fork, false);
        }
        return fork;
    }

    public ConcurrentBlockImpl(ProcessDefinition processDefinition,
                               ProcessBlock parentBlock,
                               Element xml, Session s) {
        init(processDefinition, parentBlock, xml, null, s);
        Collection nodes = getNodes();
        for (Iterator it = nodes.iterator(); it.hasNext();) {
            Node node = (Node) it.next();
            if (node instanceof Fork) {
                fork = (Fork)node;
            } else if (node instanceof Join) {
                join = (Join)node;
            }
        }
    }
}
