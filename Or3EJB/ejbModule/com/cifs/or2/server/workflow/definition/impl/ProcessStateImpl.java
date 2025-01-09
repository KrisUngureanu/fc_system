package com.cifs.or2.server.workflow.definition.impl;

import kz.tamur.lang.OrLang;
import kz.tamur.lang.parser.ASTStart;

import com.cifs.or2.server.Session;
import com.cifs.or2.server.workflow.definition.ProcessState;
import com.cifs.or2.server.workflow.definition.ProcessDefinition;
import com.cifs.or2.server.workflow.definition.ProcessBlock;
import org.jdom.Element;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 15.08.2004
 * Time: 16:50:49
 * To change this template use File | Settings | File Templates.
 */
public class ProcessStateImpl extends StateImpl implements ProcessState {
    private ASTStart subProcess;
    private String actorExpression;
    private String subProcessKrn;
    private String subProcessType="";
    private String returnVarType="";
    private String subRollbackType="";

    public ASTStart getSubProcess() {
        return subProcess;
    }

    public String getActorExpression() {
        return actorExpression;
    }

    public String getReturnVarType() {
        return returnVarType;
    }
    public String getSubProcessType() {
        return subProcessType;
    }

    public String getSubRollbackType() {
        return subRollbackType;
    }

    public void refreash(ProcessDefinition processDefinition, Element xml, Session s){
        super.refreash(processDefinition, xml, s);
        subProcessKrn = getProperty("KRNprocess", xml);
        if (subProcessKrn == null || subProcessKrn.length() == 0) {
	        String expr = getProperty("process", xml);
	        subProcess = (expr != null && expr.trim().length() > 0) ? OrLang.createStaticTemplate(expr) : null;
        }
        subProcessType = getProperty("subprocessType", xml);
        subRollbackType = getProperty("subRollbackType", xml);
        returnVarType = getProperty("returnVarType", xml);
        actorExpression = getProperty("actor-expression", xml);
    }
    public ProcessStateImpl(ProcessDefinition processDefinition,
                            ProcessBlock processBlock,
                            Element xml, Session s) {
        super(processDefinition, processBlock, xml, s);
        subProcessKrn = getProperty("KRNprocess", xml);
        if (subProcessKrn == null || subProcessKrn.length() == 0) {
	        String expr = getProperty("process", xml);
	        subProcess = (expr != null && expr.trim().length() > 0) ? OrLang.createStaticTemplate(expr) : null;
        }
        subProcessType = getProperty("subprocessType", xml);
        subRollbackType = getProperty("subRollbackType", xml);
        returnVarType = getProperty("returnVarType", xml);
        actorExpression = getProperty("actor-expression", xml);

    }
    public String getType() {
        return "process-state";
    }

    public String getSubProcessKrn() {
        return subProcessKrn;
    }
}
