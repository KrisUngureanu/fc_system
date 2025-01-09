package com.cifs.or2.server.workflow.definition.impl;

import com.cifs.or2.server.Session;
import com.cifs.or2.server.workflow.definition.StartState;
import com.cifs.or2.server.workflow.definition.ProcessDefinition;
import com.cifs.or2.server.workflow.definition.ProcessBlock;
import org.jdom.Element;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 15.08.2004
 * Time: 15:37:26
 * To change this template use File | Settings | File Templates.
 */
public class StartStateImpl extends ActivityStateImpl implements StartState {
    public StartStateImpl(ProcessDefinition processDefinition,
                         ProcessBlock processBlock,
                         Element xml, Session s) {
        super(processDefinition, processBlock, xml, s);
    }
    public String getType() {
        return "start-state";
    }
}
