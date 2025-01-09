package com.cifs.or2.server.workflow.definition.impl;

import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.server.Session;
import com.cifs.or2.server.workflow.definition.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.XMLOutputter;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import kz.tamur.lang.OrLang;
import kz.tamur.lang.StringResources;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.server.wf.WfUtils;
import kz.tamur.server.wf.WorkflowException;
import kz.tamur.util.MapMap;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 15.08.2004
 * Time: 14:28:25
 * To change this template use File | Settings | File Templates.
 */
public class ProcessDefinitionImpl extends ProcessBlockImpl
        implements ProcessDefinition, Comparable, StringResources{
	
	private static final Log LOG = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + ProcessDefinitionImpl.class.getName());
	private static org.jdom.xpath.XPath xpName;

	private StartState start;
    private EndState end;
    private ASTStart responsibleExpr = null;
    private List<Long> responsibleIds=new ArrayList<Long>();
    private ASTStart chopperId;
    private String responsibleIdKrn;
    private String chopperIdKrn;
    private ASTStart conflict;
    private ASTStart uiExpressionInf;
    private String uiExpressionInfKrn;
    private String actorExpressionInf;
    private ASTStart objExpressionInf;
    private String uiTypeInf;
    private ASTStart inspectors;
    private ASTStart titleExpr;
    private ASTStart beforeCommitExpr;
    private ASTStart afterCommitExpr;
    private MapMap<Long, String, String> strings;
    private boolean isInbox=false;


    private long processId;
    private int lastNodeId;
    
    static {
    	try {
    		xpName = org.jdom.xpath.XPath.newInstance("//msg[@uid='process_0']");
    	} catch (JDOMException e) {
    		LOG.error(e, e);
    	}
    }

    public long getId() {
        return processId;
    }


    public boolean isInbox() {
        return isInbox;
    }

    public String getNextNodeId() {
        return "" + (++lastNodeId);
    }

    public void checkLastNodeId(String id) {
        int iid = Integer.parseInt(id);
        if (iid > lastNodeId) {
            lastNodeId = iid;
        }
    }

    public StartState getStartState() {
        return start;
    }

    public EndState getEndState() {
        return end;
    }

    public ASTStart getResponsibleExpr() {
        return responsibleExpr;
    }

    public ASTStart getChopperId() {
        return chopperId;
    }
    public String getResponsibleKrn() {
        return responsibleIdKrn;
    }

    public String getChopperIdKrn() {
        return chopperIdKrn;
    }
    public long getProcessId(){return processId;}

    public ProcessDefinitionImpl(long processId, Element xml, Element[] xml_strs, Session s) throws WorkflowException {
    	this.session = s;
        strings = new MapMap<Long, String, String>();
    	List<KrnObject> langs=s.getSystemLangs();
    	int i=0;
        for (Element xml_str : xml_strs) {
        	if (xml_str == null) continue;
            Map<String,String> str = new HashMap<String,String>();
            List strs = xml_str.getChildren("msg");
            for (Object str1 : strs) {
                Element e = (Element) str1;
                String uid = e.getAttribute("uid").getValue();
                String value = e.getText();
                str.put(uid, value);
            }
            strings.put(langs.get(i++).id, str);
        }
    	String name = null;
		name = strings.get(122L, "process_0");
		try {
			init(this, null, xml, name, s);
		} catch (Throwable e) {
    		LOG.warn("Процесс '" + name + "(" + processId +	")' имеет ошибку в определении процесса!");
    		LOG.error(e, e);
    		//throw(new WorkflowException(e));
		}
        this.processId=processId;
        
        String expr;
        
        responsibleIdKrn = getProperty("KRNresponsible", xml);
        chopperIdKrn = getProperty("KRNchopper", xml);
        uiExpressionInfKrn = getProperty("KRNprocessInfUi", xml);

        if (responsibleIdKrn == null || responsibleIdKrn.length() == 0) {
	        expr = getProperty("responsible", xml);
	        responsibleExpr = (expr != null && expr.trim().length() > 0) ? OrLang.createStaticTemplate(expr) : null;
        }
        if (chopperIdKrn == null || chopperIdKrn.length() == 0) {
	        expr = getProperty("chopper", xml);
	        chopperId = (expr != null && expr.trim().length() > 0) ? OrLang.createStaticTemplate(expr) : null;
        }
        if (uiExpressionInfKrn == null || uiExpressionInfKrn.length() == 0) {
	        expr = DefinitionObjectImpl.getProperty("processInfUi", xml);
	        uiExpressionInf = (expr != null && expr.trim().length() > 0) ? OrLang.createStaticTemplate(expr) : null;
        }
        expr = DefinitionObjectImpl.getProperty("conflict", xml);
        conflict = (expr != null && expr.trim().length() > 0) ? OrLang.createStaticTemplate(expr) : null;
        actorExpressionInf = getProperty("actorInf", xml);
        
        expr = DefinitionObjectImpl.getProperty("processObjInf", xml);
        objExpressionInf = (expr != null && expr.trim().length() > 0) ? OrLang.createStaticTemplate(expr) : null;
        expr = DefinitionObjectImpl.getProperty("inspectors", xml);
        inspectors = (expr != null && expr.trim().length() > 0) ? OrLang.createStaticTemplate(expr) : null;
        expr = DefinitionObjectImpl.getProperty("title", xml);
        titleExpr = (expr != null && expr.trim().length() > 0) ? OrLang.createStaticTemplate(expr) : null;

        expr = DefinitionObjectImpl.getProperty("eventBeforeCommit", xml);
        beforeCommitExpr = (expr != null && expr.trim().length() > 0) ? OrLang.createStaticTemplate(expr) : null;
        expr = DefinitionObjectImpl.getProperty("eventAfterCommit", xml);
        afterCommitExpr = (expr != null && expr.trim().length() > 0) ? OrLang.createStaticTemplate(expr) : null;
        
        uiTypeInf = getProperty("processUiTypeInf", xml);
        Element e = xml.getChild("inbox");
        if(e!=null){
            isInbox=e.getText().equals("true");
        }
        e = xml.getChild("start-state");
        if (e == null) {
            System.out.println("!ERROR: Process definition - '"+name+"' without Start state");
        } else {
            start = new StartStateImpl(this, this, e, s);
            addNode(start, true);
        }
        e = xml.getChild("end-state");
        if (e == null) {
            System.out.println("!ERROR: Process definition - '"+name+"' without End state");
        } else {
            end = new EndStateImpl(this, this, e, s);
            addNode(end, true);
        }
        // Обновляем ID пользователя
        try {
        	updateResponsibleId(s);
        } catch (Exception ex) {
        	LOG.error(ex, ex);
        }
    }

    public void refreashProcess(Element xml,Element[] xml_strs, Session s) throws WorkflowException {
    	String name = null;
    /*	if (xml_msg != null) {
    		try {
	    		org.jdom.xpath.XPath xp =  org.jdom.xpath.XPath.newInstance("//msg[@uid='process_0']");
	        	name = xp.valueOf(xml_msg);
    		} catch (Exception e) {}
    	}*/
        super.refreash(this,xml,name, s);
        strings = new MapMap<Long, String, String>();
    	List<KrnObject> langs=s.getSystemLangs();
    	int i=0;
        for (Element xml_str : xml_strs) {
        	if (xml_str == null) continue;
            Map<String,String> str = new HashMap<String,String>();
            List strs = xml_str.getChildren("msg");
            for (Object str1 : strs) {
                Element e = (Element) str1;
                String uid = e.getAttribute("uid").getValue();
                String value = e.getText();
                str.put(uid, value);
            }
            strings.put(langs.get(i++).id, str);
        }
        String expr;
        
        responsibleIdKrn = getProperty("KRNresponsible", xml);
        chopperIdKrn = getProperty("KRNchopper", xml);
        uiExpressionInfKrn = getProperty("KRNprocessInfUi", xml);

        if (responsibleIdKrn == null || responsibleIdKrn.length() == 0) {
	        expr = getProperty("responsible", xml);
	        responsibleExpr = (expr != null && expr.trim().length() > 0) ? OrLang.createStaticTemplate(expr) : null;
        }
        if (chopperIdKrn == null || chopperIdKrn.length() == 0) {
	        expr = getProperty("chopper", xml);
	        chopperId = (expr != null && expr.trim().length() > 0) ? OrLang.createStaticTemplate(expr) : null;
        }
        if (uiExpressionInfKrn == null || uiExpressionInfKrn.length() == 0) {
	        expr = DefinitionObjectImpl.getProperty("processInfUi", xml);
	        uiExpressionInf = (expr != null && expr.trim().length() > 0) ? OrLang.createStaticTemplate(expr) : null;
        }
        expr = DefinitionObjectImpl.getProperty("conflict", xml);
        conflict = (expr != null && expr.trim().length() > 0) ? OrLang.createStaticTemplate(expr) : null;
        actorExpressionInf = getProperty("actorInf", xml);

        expr = DefinitionObjectImpl.getProperty("processObjInf", xml);
        objExpressionInf = (expr != null && expr.trim().length() > 0) ? OrLang.createStaticTemplate(expr) : null;
        expr = DefinitionObjectImpl.getProperty("inspectors", xml);
        inspectors = (expr != null && expr.trim().length() > 0) ? OrLang.createStaticTemplate(expr) : null;
        expr = DefinitionObjectImpl.getProperty("title", xml);
        titleExpr = (expr != null && expr.trim().length() > 0) ? OrLang.createStaticTemplate(expr) : null;

        expr = DefinitionObjectImpl.getProperty("eventBeforeCommit", xml);
        beforeCommitExpr = (expr != null && expr.trim().length() > 0) ? OrLang.createStaticTemplate(expr) : null;
        expr = DefinitionObjectImpl.getProperty("eventAfterCommit", xml);
        afterCommitExpr = (expr != null && expr.trim().length() > 0) ? OrLang.createStaticTemplate(expr) : null;
        
        uiTypeInf = getProperty("processUiTypeInf", xml);
        Element e = xml.getChild("inbox");
        if(e!=null){
            isInbox=e.getText().equals("true");
        }
        e = xml.getChild("start-state");
        if (e == null) {
            System.out.println("!ERROR: Process definition - '"+name+"' without Start state");
        } else {
            if(start==null){
                start = new StartStateImpl(this, this, e, s);
                addNode(start, true);
            }else
                ((StartStateImpl)start).refreash(this, e, s);
        }
        e = xml.getChild("end-state");
        if (e == null) {
            System.out.println("!ERROR: Process definition - '"+name+"' without End state");
        } else {
            if(end==null){
                end = new EndStateImpl(this, this, e, s);
                addNode(end, true);
            }else
                ((EndStateImpl)end).refreash(this, e, s);
        }
        // Обновляем ID пользователя
        updateResponsibleId(s);
    }

    public StartState createStartState() {
        start = new StartStateImpl(this, this, new Element("start-state"), session);
        addNode(start);
        return start;
    }

    public byte[] serialize() {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            FileOutputStream fos = new FileOutputStream("out.xml");
            XMLOutputter out = new XMLOutputter();
            out.getFormat().setEncoding("UTF-8");
            out.output(xml, os);
            os.close();
            out.output(xml, fos);
            fos.close();
            return os.toByteArray();
        } catch (IOException e) {
            LOG.error(e, e);
        }
        return new byte[0];
    }

    public int compareTo(Object o) {
        return this.processId>((ProcessDefinitionImpl)o).getProcessId()?1:-1;
    }

    public Map<String, String> getStrings(long langId) throws Exception {
        return strings.get(langId);
    }

    public String getString(long langId, String uid) throws Exception {
        return strings.get(langId, uid);
    }
    
    public long getDefaultLangId() {
        return 0;
    }

    public void save() throws Exception {
    }

    public ASTStart getConflict() {
        return conflict;
    }

    public ASTStart getUiExpressionInf() {
        return uiExpressionInf;
    }

    public String getUiExpressionInfKrn() {
        return uiExpressionInfKrn;
    }
    public String getActorExpressionInf() {
        return actorExpressionInf;
    }

    public ASTStart getObjExpressionInf() {
        return objExpressionInf;
    }

    public String getUiTypeInf() {
        return uiTypeInf;
    }

    public ASTStart getInspectors() {
        return inspectors;
    }
    
    public ASTStart getTitleExpr() {
        return titleExpr;
    }

    public ASTStart getBeforeCommitExpr() {
        return beforeCommitExpr;
    }

    public ASTStart getAfterCommitExpr() {
        return afterCommitExpr;
    }

    public List<Long> getResponsibleId() {
    	return responsibleIds;
    }
    
    private void updateResponsibleId(Session s) throws WorkflowException {
    	responsibleIds.clear();
        if (responsibleIdKrn != null) {
            try {
            	KrnObject obj = s.getObjectByUid(responsibleIdKrn, 0);
            	if (obj == null) {
            		LOG.warn("Процесс '" + getName() + "(" + getId() +
            				")' ссылается на несуществующего пользователя '" +
            				responsibleIdKrn + "'");
            	} else {
            		responsibleIds.add(obj.id);
            	}
             } catch (KrnException ex) {
                 LOG.error(ex, ex);
             }
        } else if (responsibleExpr != null) {
        	
            Object user_ = WfUtils.getResolvExpression(this, responsibleExpr, null,
                    null, null,EventType.RESPONSIBLE, s);
            if (user_ instanceof String) {
            	if (!"SERVER".equals(user_))
            		responsibleIds.add(Long.parseLong((String)user_));
            } else if (user_ instanceof Number)
            	responsibleIds.add(((Number) user_).longValue());
            else if (user_ instanceof KrnObject)
            	responsibleIds.add(((KrnObject) user_).id);
            else if (user_ instanceof List) {
                for (int i = 0; i < ((List) user_).size(); ++i) {
                    Object o = ((List) user_).get(i);
                    if (o != null)
                        responsibleIds.add(((KrnObject) o).id);
                }
            }
        }
    }
}
