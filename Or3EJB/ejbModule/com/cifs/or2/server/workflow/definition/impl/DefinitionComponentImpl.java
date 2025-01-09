package com.cifs.or2.server.workflow.definition.impl;

import com.cifs.or2.server.Session;
import com.cifs.or2.server.workflow.definition.DefinitionComponent;
import com.cifs.or2.server.workflow.definition.ProcessDefinition;

import kz.tamur.or3ee.common.UserSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import java.io.InputStream;
import java.util.*;

public class DefinitionComponentImpl implements DefinitionComponent {
	
	private String dsName;
	private Log log;

    private Map<Long,ProcessDefinitionImpl> inBox = new HashMap<Long,ProcessDefinitionImpl>();
    private Map<Long,ProcessDefinitionImpl> byId = new HashMap<Long,ProcessDefinitionImpl>();
    private Map<String,ProcessDefinitionImpl> byName = new HashMap<String,ProcessDefinitionImpl>();
    
    public DefinitionComponentImpl(String dsName) {
    	this.dsName = dsName;
    	this.log = LogFactory.getLog(dsName + "." + (UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + getClass().getName());
    }

    public Collection getInBox() {
        return inBox.values();
    }

    public ProcessDefinition deployProcess(Long processId, InputStream processStream, InputStream[] messageStreams, Session s) {
        try {
            SAXBuilder builder = new SAXBuilder();
            builder.setValidation(false);
            Element xml = builder.build(processStream).getRootElement();
            Element[] xml_strs=new Element[messageStreams.length];
            for(int i=0;i<xml_strs.length;++i){
                Document doc=null;
                if(messageStreams[i]!=null)
                    doc=builder.build(messageStreams[i]);
                if(doc !=null)
                    xml_strs[i] = doc.getRootElement();
                else
                    xml_strs[i]=null;
            }
            ProcessDefinitionImpl pd = byId.get(processId);
            if(pd == null){
                pd = new ProcessDefinitionImpl(processId, xml, xml_strs, s);
                byId.put(processId, pd);
            } else {
                byName.remove(pd.getName());
                pd.refreashProcess(xml, xml_strs, s);
            }
            byName.put(pd.getName(), pd);
            if(pd.isInbox()) {
                inBox.put(processId,pd);
            }else if(inBox.containsKey(processId)){
                inBox.remove(processId);
            }
            return pd;
        } catch (Exception e) {
            log.error(e, e);
        }
        return null;
    }

    public ProcessDefinition getProcessDefinition(Long processId) {
        return byId.get(processId);
    }

    public Collection<ProcessDefinitionImpl> getProcessDefinition() {
    	return byId.values();
/*        TreeSet<ProcessDefinitionImpl> res=new TreeSet<ProcessDefinitionImpl>();
        for (ProcessDefinitionImpl processDefinition : byId.values()) res.add(processDefinition);
        return res;
*/    }

    public ProcessDefinition getProcessDefinition(String name) {
        return byName.get(name);
    }

}
