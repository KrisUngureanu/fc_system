package com.cifs.or2.server.workflow.definition.impl;

import com.cifs.or2.server.Session;
import com.cifs.or2.server.workflow.definition.ProcessDefinition;
import com.cifs.or2.server.workflow.definition.ProcessBlock;
import org.jdom.Element;

/**
 * Created by IntelliJ IDEA.
 * User: ValeT
 * Date: 11.05.2006
 * Time: 11:28:11
 * To change this template use File | Settings | File Templates.
 */
public class StartSyncState extends StateImpl{
        private String stateExpression="";

        public StartSyncState(ProcessDefinition processDefinition,
                               ProcessBlock processBlock,
                               Element xml, Session s) {
            super(processDefinition, processBlock, xml, s);
            stateExpression = getProperty("syncState", xml);
        }
        public String getStateExpression() {
            return stateExpression;
        }

        public void setStateExpression(String boxExpression) {
            this.stateExpression = boxExpression;
        }
    public String getType() {
        return "start-sync-state";
    }
}
