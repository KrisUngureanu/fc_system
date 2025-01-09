package kz.tamur.guidesigner.service;

import static kz.tamur.guidesigner.service.ServiceActionsConteiner.getServiceActions;

import java.awt.Color;

import com.cifs.or2.client.util.KrnObjectItem;

import kz.tamur.comps.Constants;
import kz.tamur.guidesigner.service.ui.StateNode;
import kz.tamur.guidesigner.service.ui.TransitionEdge;
import kz.tamur.or3.client.props.*;

/**
 * Created by IntelliJ IDEA.
 * User: Valeri
 * Date: 17.03.2009
 * Time: 17:14:16
 * To change this template use File | Settings | File Templates.
 */
public class ServiceItem implements Inspectable {
    private static Property proot;
    private Object item;
    private MainFrame mf;
    public ServiceItem(Object item,MainFrame owner){
       this.item = item;
        this.mf=owner;
    }

    public Property getProperties() {
            proot = new FolderProperty(null, null,"Элементы");
            if(item!=null){
                Property properties = new FolderProperty(proot, "Свойства", "Свойства");
                if(item instanceof StateNode){
                    NodeProperty[] nodeProperties=((StateNode)item).getProperties();
                    NodeEventType[] nodeEvents=((StateNode)item).getEventTypes();
                    Property events = new FolderProperty(proot, "События", "События");
                    for(NodeProperty prop:nodeProperties){
                        if(NodePropertyConstants.UI_TYPE.equals(prop)){
                            ComboProperty typeProp = new ComboProperty(properties,prop.getName(), prop.getTitle());
                            typeProp.addItem("", "")
                                    .addItem(Constants.ACT_WINDOW_STRING, Constants.ACT_WINDOW_STRING)
                                    .addItem(Constants.ACT_DIALOG_STRING, Constants.ACT_DIALOG_STRING)
                                    .addItem(Constants.ACT_AUTO_STRING, Constants.ACT_AUTO_STRING)
                                    .addItem(Constants.ACT_ARTICLE_STRING, Constants.ACT_ARTICLE_STRING)
                                    .addItem(Constants.ACT_FASTREPORT_STRING, Constants.ACT_FASTREPORT_STRING);
                         }else if(NodePropertyConstants.UI_TYPE_INF.equals(prop)){
                            ComboProperty typeProp = new ComboProperty(properties,prop.getName(), prop.getTitle());
                            typeProp.addItem("", "")
                                    .addItem(Constants.ACT_WINDOW_STRING, Constants.ACT_WINDOW_STRING)
                                    .addItem(Constants.ACT_DIALOG_STRING, Constants.ACT_DIALOG_STRING);
                        }else if(NodePropertyConstants.SUBPROCESS_TYPE.equals(prop)){
                            ComboProperty typeProp = new ComboProperty(properties,prop.getName(), prop.getTitle());
                            typeProp.addItem("", "")
                                    .addItem(Constants.SUBPROCESS_WAIT, Constants.SUBPROCESS_WAIT)
                                    .addItem(Constants.SUBPROCESS_PASS_WAIT, Constants.SUBPROCESS_PASS_WAIT)
                                    .addItem(Constants.SUBPROCESS_PASS, Constants.SUBPROCESS_PASS);
                        }else if(NodePropertyConstants.SUB_ROLLBACK_TYPE.equals(prop)){
                            ComboProperty typeProp = new ComboProperty(properties,prop.getName(), prop.getTitle());
                            typeProp.addItem("", "")
                                    .addItem(Constants.SUPERPROCESS_CONTINUE, Constants.SUPERPROCESS_CONTINUE);
                        }else if(NodePropertyConstants.RETURN_VAR_TYPE.equals(prop)){
                            ComboProperty typeProp = new ComboProperty(properties,prop.getName(), prop.getTitle());
                            typeProp.addItem("", "")
                                    .addItem(Constants.NOT_RETURN_VAR, Constants.NOT_RETURN_VAR);
                        }else if(NodePropertyConstants.ENABLE_CHOPPER.equals(prop)){
                            ComboProperty typeProp = new ComboProperty(properties,prop.getName(), prop.getTitle());
                            typeProp.addItem("", "")
                                    .addItem(Constants.CHOPPER_NO, Constants.CHOPPER_NO)
                                    .addItem(Constants.CHOPPER_YES, Constants.CHOPPER_YES);
                        }else if(NodePropertyConstants.ASSIGNMENT.equals(prop)
                                || NodePropertyConstants.RESPONSIBLE.equals(prop)
                                || NodePropertyConstants.CHOPPER.equals(prop)){
                                new TreeOrExprProperty(properties,prop.getName(), prop.getTitle(),"User");
                        }else if(NodePropertyConstants.PROCESS.equals(prop)){
                                new TreeOrExprProperty(properties,prop.getName(), prop.getTitle(),"ProcessDef");
                        }else if(NodePropertyConstants.UI_PROCESS.equals(prop)
                                || NodePropertyConstants.UI_INF_PROCESS.equals(prop)){
                                new TreeOrExprProperty(properties,prop.getName(), prop.getTitle(),"UI");
                        }else if(NodePropertyConstants.EXCH_BOX.equals(prop)){
                                new TreeOrExprProperty(properties,prop.getName(), prop.getTitle(),"BoxExchange");
                        }else if(NodePropertyConstants.TASK_COLOR.equals(prop)){
                            new ColorProperty(properties,prop.getName(), prop.getTitle());
                        }else if(NodePropertyConstants.ACT_REPORT_REQUIRE.equals(prop)){
                            new CheckProperty(properties,prop.getName(), prop.getTitle());
                        }else if(NodePropertyConstants.ACT_AUTO_NEXT.equals(prop)){
                            new CheckProperty(properties,prop.getName(), prop.getTitle());
                        }else if(NodePropertyConstants.NODE_ID.equals(prop)){
                            new StringProperty(properties,prop.getName(), prop.getTitle(),false);
                       }else
                            new ExprProperty(properties,prop.getName(), prop.getTitle());
                    }
                    for(NodeEventType event:nodeEvents){
                        new ExprProperty(events,event.getTitle(), event.getTitle());
                    }
                }else if(item instanceof TransitionEdge){
                    NodeProperty[] nodeProperties=((TransitionEdge)item).getProperties();
                    for(NodeProperty prop:nodeProperties){
                        if(NodePropertyConstants.SYNCH.equals(prop)){
                            new ExprProperty(properties,prop.getName(), prop.getTitle());
                        }else if(NodePropertyConstants.NODE_ID.equals(prop)){
                            new StringProperty(properties,prop.getName(), prop.getTitle(),false);
                       }else {
                        new ExprProperty(properties, prop.getName(),prop.getTitle());
                        }
                    }
                }
            }
        return proot;
    }

    public Object getValue(Property prop) {
        Object res="";
        if(item!=null && !(prop instanceof FolderProperty)){
            NodeProperty node_prop = NodeProperty.forName(prop.getId());
            NodeEventType node_event= NodeEventType.forName(prop.getId());
            if(item instanceof StateNode){
                if(node_prop!=null){
                        res=((StateNode)item).getProperty(node_prop);
                }else if(node_event!=null){
                    res=((StateNode)item).getAction(node_event);
                }
            }else if(item instanceof TransitionEdge){
                if(node_prop!=null){
                    if(!node_prop.getName().equals("name")){
                        res=((TransitionEdge)item).getProperty(node_prop);
                    }
                }
            }
            if(res==null) res="";
            if(prop instanceof ComboProperty){
                res=((ComboProperty)prop).getItem(res!=null?res.toString():"");
            }else if(prop instanceof ColorProperty ){
                    res=!"".equals(res)? new Color(Integer.valueOf((String)res).intValue()):"";
            }else if(prop instanceof ExprProperty
                    ||(prop instanceof TreeOrExprProperty && res instanceof String)){
                res= new Expression((String)res);
            }else if(prop instanceof CheckProperty && res instanceof String){
                res="true".equals(res)? true:false;
            }
        }
        return res;
    }

    public void setValue(Property prop, Object value) {
        kz.tamur.guidesigner.service.Document doc = mf.getSelectedDocument();
        Long modelID = ((ServiceModel) doc.getGraph().getEditor().getGraphModel()).geKrnObject();
        if (item != null && !(prop instanceof FolderProperty)) {
            NodeProperty nodeProp = NodeProperty.forName(prop.getId());
            NodeEventType node_event = NodeEventType.forName(prop.getId());
            if (prop instanceof ComboProperty) {
                value = new ComboPropertyItem(prop.getId(), String.valueOf(value)).title;
            }
            if (item instanceof StateNode) {
                Long id = null;
                if (((StateNode) item).getModel() != null) {
                    id = ((StateNode) item).getModel().geKrnObject();
                }
                id = id == null ? modelID : id;
                if (nodeProp != null) {
                    Object pr = ((StateNode) item).getProperty(nodeProp);
                    String title = nodeProp.getTitle();
                    if (value instanceof KrnObjectItem) {
                        getServiceActions(id).propertyChanged((StateNode) item, title, pr, value, prop);
                        ((StateNode) item).setProperty(nodeProp, value);
                    } else if (value instanceof Expression) {
                        getServiceActions(id).propertyChanged((StateNode) item, title, pr, (Expression) value, prop);
                        ((StateNode) item).setProperty(nodeProp, ((Expression) value).text);
                    } else if (value instanceof Boolean) {
                        getServiceActions(id)
                                .propertyChanged((StateNode) item, title, pr, ((Boolean) value).booleanValue(), prop);
                        ((StateNode) item).setProperty(nodeProp, ((Boolean) value).booleanValue());
                    } else if (value instanceof Color) {
                        getServiceActions(id).propertyChanged((StateNode) item, title, pr, ((Color) value).getRGB(), prop);
                        ((StateNode) item).setProperty(nodeProp, "" + ((Color) value).getRGB());
                    } else {
                        getServiceActions(id).propertyChanged((StateNode) item, title, pr, value != null ? (String) value : "",
                                prop);
                        ((StateNode) item).setProperty(nodeProp, value != null ? (String) value : "");
                    }
                } else if (node_event != null) {
                    if (value instanceof Expression) {
                        String value_ = ((Expression) value).text;
                        getServiceActions(id).propertyChanged((StateNode) item, node_event.getTitle(),
                                ((StateNode) item).getAction(node_event), value_ != null ? value_ : "", prop);
                        ((StateNode) item).setAction(node_event, value_ != null ? value_ : "");
                    } else {
                        getServiceActions(id).propertyChanged((StateNode) item, node_event.getTitle(),
                                ((StateNode) item).getAction(node_event), value != null ? (String) value : "", prop);
                        ((StateNode) item).setAction(node_event, value != null ? (String) value : "");
                    }
                }
            } else if (item instanceof TransitionEdge) {
                ((TransitionEdge) item).setProperty(nodeProp,
                        value != null ? value instanceof Expression ? ((Expression) value).text : (String) value : "");
            }
            mf.setProcessModified(true);
        }
    }
    
    public void setValue(Property prop, Object value, Document doc) {
        Long modelID = ((ServiceModel) doc.getGraph().getEditor().getGraphModel()).geKrnObject();
        if (item != null && !(prop instanceof FolderProperty)) {
            NodeProperty nodeProp = NodeProperty.forName(prop.getId());
            NodeEventType node_event = NodeEventType.forName(prop.getId());
            if (prop instanceof ComboProperty) {
                value = new ComboPropertyItem(prop.getId(), String.valueOf(value)).title;
            }
            if (item instanceof StateNode) {
                Long id = null;
                if (((StateNode) item).getModel() != null) {
                    id = ((StateNode) item).getModel().geKrnObject();
                }
                id = id == null ? modelID : id;
                if (nodeProp != null) {
                    Object pr = ((StateNode) item).getProperty(nodeProp);
                    String title = nodeProp.getTitle();
                    if (value instanceof KrnObjectItem) {
                        getServiceActions(id).propertyChanged((StateNode) item, title, pr, value, prop);
                        ((StateNode) item).setProperty(nodeProp, value);
                    } else if (value instanceof Expression) {
                        getServiceActions(id).propertyChanged((StateNode) item, title, pr, (Expression) value, prop);
                        ((StateNode) item).setProperty(nodeProp, ((Expression) value).text);
                    } else if (value instanceof Boolean) {
                        getServiceActions(id)
                                .propertyChanged((StateNode) item, title, pr, ((Boolean) value).booleanValue(), prop);
                        ((StateNode) item).setProperty(nodeProp, ((Boolean) value).booleanValue());
                    } else if (value instanceof Color) {
                        getServiceActions(id).propertyChanged((StateNode) item, title, pr, ((Color) value).getRGB(), prop);
                        ((StateNode) item).setProperty(nodeProp, "" + ((Color) value).getRGB());
                    } else {
                        getServiceActions(id).propertyChanged((StateNode) item, title, pr, value != null ? (String) value : "",
                                prop);
                        ((StateNode) item).setProperty(nodeProp, value != null ? (String) value : "");
                    }
                } else if (node_event != null) {
                    if (value instanceof Expression) {
                        String value_ = ((Expression) value).text;
                        getServiceActions(id).propertyChanged((StateNode) item, node_event.getTitle(),
                                ((StateNode) item).getAction(node_event), value_ != null ? value_ : "", prop);
                        ((StateNode) item).setAction(node_event, value_ != null ? value_ : "");
                    } else {
                        getServiceActions(id).propertyChanged((StateNode) item, node_event.getTitle(),
                                ((StateNode) item).getAction(node_event), value != null ? (String) value : "", prop);
                        ((StateNode) item).setAction(node_event, value != null ? (String) value : "");
                    }
                }
            } else if (item instanceof TransitionEdge) {
                ((TransitionEdge) item).setProperty(nodeProp,
                        value != null ? value instanceof Expression ? ((Expression) value).text : (String) value : "");
            }
            mf.setProcessModified(doc, true);
        }
    }
    
	public void setValue(Property prop, Object value, Object oldValue) {
		setValue(prop, value);
	}
    
    public MainFrame getMainFrame(){
        return mf;
    }
    
    public Object getItem(){
        return item;
    }
    
    public String getTitle(){
        String title="";
        if(item instanceof  StateNode){
           title=" - "+ ((StateNode)item).getType()+":"+((StateNode)item).getName();
        }else if(item instanceof TransitionEdge){
            title=" - Переход:"+((TransitionEdge)item).getName();
        }
        return "Процессы"+title;
    }

    public Property getNewProperties() {
        return null;
    }
}
