package com.cifs.or2.server.workflow.definition.impl;

import com.cifs.or2.server.Session;
import com.cifs.or2.server.workflow.definition.Decision;
import com.cifs.or2.server.workflow.definition.ProcessDefinition;
import com.cifs.or2.server.workflow.definition.ProcessBlock;
import org.jdom.Element;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 15.08.2004
 * Time: 17:04:03
 * To change this template use File | Settings | File Templates.
 */
public class DecisionImpl extends NodeImpl implements Decision {
    public DecisionImpl(ProcessDefinition processDefinition,
                        ProcessBlock processBlock,
                        Element xml, Session s) {
        super(processDefinition, processBlock, xml, s);
    }
    public String getType() {
        return "decision";
    }
}
