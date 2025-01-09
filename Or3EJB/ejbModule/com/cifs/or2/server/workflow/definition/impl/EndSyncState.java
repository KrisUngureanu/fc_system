package com.cifs.or2.server.workflow.definition.impl;

import com.cifs.or2.server.Session;
import com.cifs.or2.server.workflow.definition.ProcessDefinition;
import com.cifs.or2.server.workflow.definition.ProcessBlock;
import org.jdom.Element;

/**
 * Created by IntelliJ IDEA.
 * User: ValeT
 * Date: 11.05.2006
 * Time: 11:48:46
 * To change this template use File | Settings | File Templates.
 */
public class EndSyncState extends StateImpl{
        private String stateExpression="";

        public EndSyncState(ProcessDefinition processDefinition,
                              ProcessBlock processBlock,
                              Element xml, Session s) {
            super(processDefinition, processBlock, xml, s);
            stateExpression = getProperty("syncState", xml);
        }
        public String getStateExpression() {
            return stateExpression;
        }

        public void setStateExpression(String stateExpression) {
            this.stateExpression = stateExpression;
        }
    public String getType() {
        return "end-sync-state";
    }
}
