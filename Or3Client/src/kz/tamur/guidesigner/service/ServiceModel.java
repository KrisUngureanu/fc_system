package kz.tamur.guidesigner.service;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import kz.tamur.guidesigner.ProcessFrameTemplate;
import kz.tamur.guidesigner.service.fig.FigLineEdge;
import kz.tamur.guidesigner.service.fig.FigNamedNode;
import kz.tamur.guidesigner.service.fig.FigTransitionEdge;
import kz.tamur.guidesigner.service.ui.ActivityStateNode;
import kz.tamur.guidesigner.service.ui.DecisionStateNode;
import kz.tamur.guidesigner.service.ui.EndStateNode;
import kz.tamur.guidesigner.service.ui.EndSyncNode;
import kz.tamur.guidesigner.service.ui.ForkNode;
import kz.tamur.guidesigner.service.ui.InBoxStateNode;
import kz.tamur.guidesigner.service.ui.JoinNode;
import kz.tamur.guidesigner.service.ui.NoteStateNode;
import kz.tamur.guidesigner.service.ui.OutBoxStateNode;
import kz.tamur.guidesigner.service.ui.ProcessStateNode;
import kz.tamur.guidesigner.service.ui.ReportStateNode;
import kz.tamur.guidesigner.service.ui.StartStateNode;
import kz.tamur.guidesigner.service.ui.StartSyncNode;
import kz.tamur.guidesigner.service.ui.StateNode;
import kz.tamur.guidesigner.service.ui.SubProcessStateNode;
import kz.tamur.guidesigner.service.ui.TransitionEdge;
import kz.tamur.lang.ErrRecord;
import kz.tamur.lang.StringResources;
import kz.tamur.rt.Utils;
import kz.tamur.util.DebuggerInterface;
import kz.tamur.util.LangItem;
import kz.tamur.util.MapMap;
import kz.tamur.util.Pair;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.tigris.gef.base.LayerManager;
import org.tigris.gef.graph.presentation.DefaultGraphModel;
import org.tigris.gef.graph.presentation.JGraph;
import org.tigris.gef.graph.presentation.NetPort;
import org.tigris.gef.presentation.Fig;
import org.tigris.gef.presentation.FigCircle;
import org.tigris.gef.presentation.FigEdge;
import org.tigris.gef.presentation.FigInk;
import org.tigris.gef.presentation.FigLine;
import org.tigris.gef.presentation.FigPoly;
import org.tigris.gef.presentation.FigRRect;
import org.tigris.gef.presentation.FigRect;
import org.tigris.gef.presentation.FigSpline;
import org.tigris.gef.presentation.FigText;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.util.KrnObjectItem;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.util.MultiMap;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 09.09.2004
 * Time: 16:37:03
 * To change this template use File | Settings | File Templates.
 */
public class ServiceModel extends DefaultGraphModel
        implements PropertyChangeListener,StringResources {

    private StartStateNode startNode;
    private EndStateNode endNode;
    public HashMap<String,StateNode> nodeMap=new HashMap<String,StateNode>();
    private HashMap<FigTransitionEdge,FigTransitionEdge> edges;
    private HashMap<String,Object> lines;
    private HashMap<StateNode,StateNode> forkJoin,nodeFork;
    private MultiMap processNodes;
    private Vector<StateNode> selectNode=new Vector<StateNode>();
    private Vector<FigTransitionEdge> selectEdge=new Vector<FigTransitionEdge>();
    private Vector<FigLineEdge> selectLine=new Vector<FigLineEdge>();
    private MultiMap transitions;
    private Vector<StateNode> scanNodes;
    private Vector<String> errors_;
    private Vector<String> eventErrors;
    public Vector<String> getEventErrors() {
		return eventErrors;
	}

	public void setEventErrors(Vector<String> eventErrors) {
		this.eventErrors = eventErrors;
	}
	private ProcessStateNode processNode;
    private boolean isEnable=true,isInbox=false;
    private MapMap<Long, String, String> langMap = new MapMap<Long, String, String>();
    private MapMap<Long, String, String> stringsMap = new MapMap<Long, String, String>();
    private long lang;
    private long langRu;
    private KrnObject obj;
//    private ClientOrLang clientOrlang;
    private DebuggerInterface exprDebuger;

    private ProcessFrameTemplate mf;

    private boolean isLoadProcess = false;

    public ServiceModel(boolean isEnable,KrnObject obj,long langId){
        this.obj = obj;
        processNode=new ProcessStateNode(this);
        this.isEnable=isEnable;
        lang=langId;
    }

    public void setMf(ProcessFrameTemplate mf) {
        this.mf = mf;
        this.langRu = mf.getRusLang();
    }

    public MapMap<Long, String, String> getLangMap() {
        return langMap;
    }
    
    public Long geKrnObject() {
        return obj.id;
    }

    public MapMap<Long, String, String> getStringsMap() {
        return stringsMap;
    }

    public void save(OutputStream os,Document doc) throws IOException {
        validate();
//        if(errors_!=null && errors_.size()>0)return;
        int i=1;
        Element root = new Element("diagram");
        Collection figs_=doc.getGraph().getEditor().getLayerManager().getContents(new ArrayList());
        Element xml = new Element("process");
        transitions=new MultiMap();
        Map props=processNode.getPropertyMap();
        for (Object prop : props.keySet()) {
            Object item= props.get(prop);
            if (item == null) continue;
            Element e = new Element("property");
            String teg= ((NodeProperty) prop).getName();
            e.setAttribute("name", teg);
            //Сохраненеие русского наименования
            if (teg.equals("name")
                    && lang!=langRu && langMap.get(langRu,"process_0")!=null
                    && langMap.get(langRu,"process_0").length() > 0) {
                e.setText(langMap.get(langRu,"process_0"));
            }else{
                e.setText(props.get(prop).toString());
            }
            xml.addContent(e);
            if(item instanceof KrnObjectItem){
                e = new Element("property");
                e.setAttribute("name", "KRN"+teg);
                e.setText(((KrnObjectItem)item).obj.uid);
                xml.addContent(e);
            }
        }
        Map acts=processNode.getActionMap();
        for (Object act : acts.keySet()) {
            if (acts.get(act) == null) continue;
            Element e = new Element("action");
            e.setAttribute("name", ((NodeEventType) act).getTitle());
            e.setText(acts.get(act).toString());
            xml.addContent(e);
        }
        root.addContent(xml);
        for (Object aFigs_ : figs_) {
            Fig f = (Fig) aFigs_;
            xml = null;
            if (f instanceof FigNamedNode) {
                StateNode node = (StateNode) f.getOwner();
                xml = new Element("node");
                xml.setAttribute("id", node.getId());
                String class_name = node.getClass().getName();
                xml.setAttribute("class", class_name.substring(class_name.lastIndexOf(".") + 1));
                Element bounds = new Element("bounds");
                bounds.setText(rectToString(node.getPresentation().getBounds()));
                xml.addContent(bounds);
                props = node.getPropertyMap();
                for (Object prop : props.keySet()) {
                    Object item=props.get(prop);
                    if (item == null) continue;
                    Element e = new Element("property");
                    String teg= ((NodeProperty) prop).getName();
                    e.setAttribute("name", teg);
                    //Сохраненеие русского наименования
                    if (teg.equals("name")
                        && lang!=langRu && langMap.get(langRu,props.get(prop).toString()+"_"+node.getId())!=null
                        && langMap.get(langRu,props.get(prop).toString()+"_"+node.getId()).length() > 0) {
                        e.setText(langMap.get(langRu,props.get(prop).toString()+"_"+node.getId()));
                    }else{
                        e.setText(item.toString());
                        if(item instanceof KrnObjectItem){
                            Element ee = new Element("property");
                            ee.setAttribute("name", "KRN"+teg);
                            ee.setText(((KrnObjectItem)item).obj.uid);
                            xml.addContent(ee);
                        }
                    }
                    xml.addContent(e);
                    //Добавка в Messages
                    if (((NodeProperty) prop).getName().equals("name") && ((node instanceof NoteStateNode && node.isEditDesc()) ||
                            (!(node instanceof NoteStateNode) && node.isEditName()))) {

                    }
                    //
                }
                acts = node.getActionMap();
                for (Object act : acts.keySet()) {
                    if (acts.get(act) == null) continue;
                    Element e = new Element("action");
                    e.setAttribute("name", ((NodeEventType) act).getTitle());
                    e.setText(acts.get(act).toString());
                    xml.addContent(e);
                }
            } else if (f instanceof FigTransitionEdge) {
                TransitionEdge edge = (TransitionEdge) f.getOwner();
                StateNode src = (StateNode) edge.getSourcePort().getParent();
                StateNode dst = (StateNode) edge.getDestPort().getParent();
                transitions.put(src.getId(), new Pair<StateNode, TransitionEdge>(dst, edge));
                xml = new Element("edge");
                xml.setAttribute("id", edge.getId());
                if (edge.getName() != null){
                    String name_="";
                    if(lang!=langRu){
                        name_ = langMap.get(langRu,"edge_"+edge.getId());
                    }else
                        name_=edge.getName();
                    xml.setAttribute("name", name_);
                }
                xml.setAttribute("from", src.getId());
                xml.setAttribute("to", dst.getId());
                if (edge.getPoints().size() > 0) {
                    Element pts = new Element("point");
                    pts.setText(pointsToString(edge.getPoints()));
                    xml.addContent(pts);
                }
                props = edge.getPropertyMap();
                for (Object prop : props.keySet()) {
                    if (props.get(prop) == null) continue;
                    Element e = new Element("property");
                    e.setAttribute("name", ((NodeProperty) prop).getName());
                    e.setText(props.get(prop).toString());
                    xml.addContent(e);
                }
                if (src instanceof StartStateNode && dst instanceof InBoxStateNode) {
                    isInbox = true;
                }
            } else if (f instanceof FigCircle) {
                xml = new Element("circle");
                xml.setAttribute("bounds", rectToString(f.getBounds()));
                xml.setAttribute("lineColor", colorToString(f.getLineColor()));
                xml.setAttribute("fillColor", colorToString(f.getFillColor()));
            } else if (f instanceof FigRRect) {
                xml = new Element("rrect");
                xml.setAttribute("bounds", rectToString(f.getBounds()));
                xml.setAttribute("cornerRadius", String.valueOf(((FigRRect) f).getCornerRadius()));
                xml.setAttribute("lineColor", colorToString(f.getLineColor()));
                xml.setAttribute("fillColor", colorToString(f.getFillColor()));
            } else if (f instanceof FigRect) {
                xml = new Element("rect");
                xml.setAttribute("bounds", rectToString(f.getBounds()));
                xml.setAttribute("lineColor", colorToString(f.getLineColor()));
                xml.setAttribute("fillColor", colorToString(f.getFillColor()));
            } else if (f instanceof FigText) {
                xml = new Element("text");
                xml.setAttribute("bounds", rectToString(f.getBounds()));
                xml.setAttribute("lineColor", colorToString(f.getLineColor()));
                xml.setAttribute("fillColor", colorToString(f.getFillColor()));
                xml.setText(((FigText) f).getText());
                xml.setAttribute("textColor", colorToString(((FigText) f).getTextColor()));
                langMap.put(lang, "text_" + i, ((FigText) f).getText());
                i++;
            } else if (f instanceof FigSpline) {
                xml = new Element("spline");
                if (((FigSpline) f).getPointsVector() != null && ((FigSpline) f).getPointsVector().size() > 0)
                    xml.setAttribute("points", pointsToString(((FigSpline) f).getPointsVector()));
                xml.setAttribute("lineColor", colorToString(f.getLineColor()));
                xml.setAttribute("fillColor", colorToString(f.getFillColor()));
            } else if (f instanceof FigInk) {
                xml = new Element("ink");
                if (((FigInk) f).getPointsVector() != null && ((FigInk) f).getPointsVector().size() > 0)
                    xml.setAttribute("points", pointsToString(((FigInk) f).getPointsVector()));
                xml.setAttribute("lineColor", colorToString(f.getLineColor()));
                xml.setAttribute("fillColor", colorToString(f.getFillColor()));
            } else if (f instanceof FigPoly) {
                xml = new Element("poly");
                if (((FigPoly) f).getPointsVector() != null && ((FigPoly) f).getPointsVector().size() > 0)
                    xml.setAttribute("points", pointsToString(((FigPoly) f).getPointsVector()));
                xml.setAttribute("lineColor", colorToString(f.getLineColor()));
                xml.setAttribute("fillColor", colorToString(f.getFillColor()));
            } else if (f instanceof FigLineEdge) {
                xml = new Element("lineEdge");
                xml.setAttribute("id", f.getId());
                if (f.getPoints() != null && (f.getPoints().length > 0))
                    xml.setAttribute("points", pointsToString(f.getPoints()));
                xml.setAttribute("lineColor", colorToString(f.getLineColor()));
            } else if (f instanceof FigLine) {
                xml = new Element("line");
                if (f.getPoints() != null && (f.getPoints().length > 0))
                    xml.setAttribute("points", pointsToString(f.getPoints()));
                xml.setAttribute("lineColor", colorToString(f.getLineColor()));
            }
            if (xml != null)
                root.addContent(xml);
        }
//        FileOutputStream fos = new FileOutputStream("out1.xml");
       XMLOutputter out = new XMLOutputter();
        out.getFormat().setEncoding("UTF-8");
        out.output(root, os);
//        out.output(root, fos);
//        fos.close();
    }

    public void saveProcess(OutputStream os,Document doc) throws IOException {
        if(processNodes==null) processNodes=new MultiMap();
        Collection nodes_=processNodes.get(0);
        Element root=new Element("process-definition");
        Element e = new Element("inbox");
        e.setText(isInbox?"true":"false");
        root.addContent(e);
        Map props=processNode.getPropertyMap();
        for (Object prop : props.keySet()) {
            String teg= ((NodeProperty) prop).getName();
            e = new Element(teg);
            Object item= props.get(prop);
            e.setText(item.toString());
            root.addContent(e);
            if(item instanceof KrnObjectItem){
                e = new Element("KRN"+teg);
                e.setText(((KrnObjectItem)item).obj.uid);
                root.addContent(e);
            }
        }
        Map acts=processNode.getActionMap();
        for (Object act : acts.keySet()) {
            if (acts.get(act) == null) continue;
            root.addContent("\n");
            e = new Element("action");
            e.setAttribute("event", ((NodeEventType) act).getEventType().toString());
            Element exp = new Element("expression");
            exp.setText(acts.get(act).toString());
            e.addContent(exp);
            root.addContent(e);
        }
        root.addContent("\n");
        if(nodes_!=null)
            for (Object aNodes_ : nodes_) {
                Element xml = createNodeXml((StateNode) aNodes_);
                root.addContent(xml);
                root.addContent("\n");
            }
//        FileOutputStream fos = new FileOutputStream("out.xml");
        XMLOutputter out = new XMLOutputter();
        out.getFormat().setEncoding("UTF-8");
        out.output(root, os);
//        out.output(root, fos);
//        fos.close();
    }
    private Element createNodeXml(StateNode node_){
            Element xml=null;
            if(node_ instanceof StartStateNode){
                xml=new Element("start-state");
            }else if(node_ instanceof ActivityStateNode){
                xml=new Element("activity-state");
            }else if(node_ instanceof InBoxStateNode){
                xml=new Element("inbox-state");
            }else if(node_ instanceof OutBoxStateNode){
                xml=new Element("outbox-state");
            }else if(node_ instanceof DecisionStateNode){
                xml=new Element("decision");
            }else if(node_ instanceof ForkNode){
                xml=new Element("fork");
            }else if(node_ instanceof JoinNode){
                xml=new Element("join");
            }else if(node_ instanceof SubProcessStateNode){
                xml=new Element("process-state");
            }else if(node_ instanceof ReportStateNode){
                xml=new Element("report-state");
            }else if(node_ instanceof NoteStateNode){
                xml=new Element("note-state");
            }else if(node_ instanceof StartSyncNode){
                xml=new Element("start-sync-state");
            }else if(node_ instanceof EndSyncNode){
                xml=new Element("end-sync-state");
            }else if(node_ instanceof EndStateNode){
                xml=new Element("end-state");
            }
        assert xml != null;
        xml.setAttribute("id", node_.getId());
            Map props=node_.getPropertyMap();
        for (Object prop : props.keySet()) {
            if (props.get(prop) == null) continue;
            String teg= ((NodeProperty) prop).getName();
            Element e = new Element(teg);
            Object item=props.get(prop);
            e.setText(item.toString());
            xml.addContent(e);
            if(item instanceof KrnObjectItem){
                e = new Element("KRN"+teg);
                e.setText(((KrnObjectItem)item).obj.uid);
                xml.addContent(e);
            }
        }
            Map acts=node_.getActionMap();
            if(acts.size()>0)xml.addContent("\n");
        for (Object act : acts.keySet()) {
            if (acts.get(act) == null) continue;
            Element e = new Element("action");
            e.setAttribute("event", ((NodeEventType) act).getEventType().toString());
            Element exp = new Element("expression");
            exp.setText(acts.get(act).toString());
            e.addContent(exp);
            xml.addContent(e);
            xml.addContent("\n");
        }
        Collection<Pair<StateNode, TransitionEdge>> src = transitions.get(node_.getId());
        if (acts.size() == 0)
            xml.addContent("\n");
        if (src != null) {
            for (Pair<StateNode, TransitionEdge> to : src) {
                Element e = new Element("transition");
                e.setAttribute("id", to.second.getId());
                if (to.second.getName() != null) {
                    e.setAttribute("name", to.second.getName());
                }
                e.setAttribute("to", to.first.getId());
                Object prSynch = to.second.getProperty(NodePropertyConstants.SYNCH);
                e.setAttribute("synch", prSynch == null ? "" : prSynch.toString());
                xml.addContent(e);
                xml.addContent("\n");
            }
        }
        if(node_ instanceof ForkNode){
             Element xml_fork=new Element("concurrent-block");
             xml_fork.setAttribute("id",node_.getId());
             xml_fork.addContent("\n");
             xml_fork.addContent(xml);
             Collection fork_=processNodes.get(new Integer(node_.getId()));
             for (Object aFork_ : fork_) {
                 StateNode fork_node = (StateNode) aFork_;
                 if (!fork_node.equals(node_)) {
                     xml_fork.addContent("\n");
                     Element xml_join = createNodeXml(fork_node);
                     xml_fork.addContent(xml_join);
                 }
             }
             xml_fork.addContent("\n");
             return xml_fork;
         }
        return xml;
    }
    public void loadLangs(InputStream msg,long langId){
        if(msg!=null){
            try{
            SAXBuilder builder = new SAXBuilder();
            builder.setValidation(false);
            Element xml_msg = builder.build(msg).getRootElement();
            //messages
            List msgs=xml_msg.getChildren("msg");
                for (Object msg1 : msgs) {
                    Element e = (Element) msg1;
                    String uid = e.getAttribute("uid").getValue();
                    String value = e.getText();
                    langMap.put(langId, uid, value);
                }
            //
            }
                    catch(Exception e){
                        e.printStackTrace();

            }
        }

    }
    public void loads(InputStream is,InputStream msg,JGraph graph) throws IOException {
        isLoadProcess = true;
        Hashtable<String,Boolean> table=new Hashtable<String,Boolean>();
        table.put("enable", isEnable);
        LayerManager lm_=graph.getEditor().getLayerManager();
        processNode.initialize(null);
        if(is.available()==0) return;
        lines=new HashMap<String,Object>();
        try {
            processNode.setAction(NodeEventTypeConstants.PROCESS_INSTANCE_START,null);
            SAXBuilder builder = new SAXBuilder();
            builder.setValidation(false);
            Element xml = builder.build(is).getRootElement();
            if(msg!=null){
	            Element xml_msg = builder.build(msg).getRootElement();
	            //messages
	            List msgs = xml_msg.getChildren("msg");
	            for (Object msg1 : msgs) {
	                Element e = (Element) msg1;
	                String uid = e.getAttribute("uid").getValue();
	                String value = e.getText();
	                langMap.put(lang, uid, value);
	            }
            }
            //nodes
            List nodes=xml.getChildren("node");
            List process=xml.getChildren("process");
            if(process.size()>0){
            Element e =(Element)process.get(0);
                List ee =e.getChildren("property");
                for (Object anEe : ee) {
                    String property_name = ((Element) anEe).getAttribute("name").getValue();
                    String property_expr;
                    String krn_name="";
                    NodeProperty node_prop=NodeProperty.forName(property_name);
                    if(property_name.indexOf("KRN")>=0){
                        krn_name=property_name.substring(3);
                    }
                    if (langMap.get(lang) != null && langMap.get(lang).size() > 0 && property_name.equals("name"))
                    {
                        property_expr = langMap.get(lang, "process_0");
                    } else if (langMap.get(lang) != null && langMap.get(lang).size() > 0 && property_name.equals("description"))
                    {
                        property_expr = langMap.get(lang, "process_desc_0");
                    } else
                        property_expr = ((Element) anEe).getText();
                    if (property_expr == null || property_expr.equals(""))
                        property_expr = ((Element) anEe).getText();
                    if (node_prop != null){
                        Object prop_value= processNode.getProperty(node_prop);
                        if(prop_value instanceof KrnObjectItem){
                            ((KrnObjectItem)prop_value).title= property_expr;
                            processNode.setProperty(node_prop, prop_value);
                        }else
                            processNode.setProperty(node_prop, property_expr);
                    }
                }
                ee =e.getChildren("action");
                for (Object anEe1 : ee) {
                    String action_name = ((Element) anEe1).getAttribute("name").getValue();
                    String action_expr = ((Element) anEe1).getText();
                    if (NodeEventType.forName(action_name) != null)
                        processNode.setAction(NodeEventType.forName(action_name), action_expr);

                }
            }
            for (Object node : nodes) {
                Element e = (Element) node;
                String node_id = e.getAttribute("id").getValue();
                String node_class = e.getAttribute("class").getValue();
                StateNode state_node = null;
                if (node_class.equals("StartStateNode")) {
                    state_node = new StartStateNode(node_id, this);
                } else if (node_class.equals("StartSyncNode")) {
                    state_node = new StartSyncNode(node_id, this);
                } else if (node_class.equals("EndSyncNode")) {
                    state_node = new EndSyncNode(node_id, this);
                } else if (node_class.equals("EndStateNode")) {
                    state_node = new EndStateNode(node_id, this);
                } else if (node_class.equals("ActivityStateNode")) {
                    state_node = new ActivityStateNode(node_id, this);
                } else if (node_class.equals("InBoxStateNode")) {
                    state_node = new InBoxStateNode(node_id, this);
                } else if (node_class.equals("OutBoxStateNode")) {
                    state_node = new OutBoxStateNode(node_id, this);
                } else if (node_class.equals("SubProcessStateNode")) {
                    state_node = new SubProcessStateNode(node_id, this);
                } else if (node_class.equals("DecisionStateNode")) {
                    state_node = new DecisionStateNode(node_id, this);
                } else if (node_class.equals("ForkNode")) {
                    state_node = new ForkNode(node_id, this);
                } else if (node_class.equals("JoinNode")) {
                    state_node = new JoinNode(node_id, this);
                } else if (node_class.equals("NoteStateNode")) {
                    state_node = new NoteStateNode(node_id, this);
                } else if (node_class.equals("ReportStateNode")) {
                    state_node = new ReportStateNode(node_id, this);
                } else if (node_class.equals("StartSyncNode")) {
                    state_node = new StartSyncNode(node_id, this);
                } else if (node_class.equals("EndSyncNode")) {
                    state_node = new EndSyncNode(node_id, this);
                }
                if (state_node != null) {
                    state_node.initialize(table);
                    addNode(state_node);
                    Element bounds_ = e.getChild("bounds");
                    if (bounds_ != null)
                        state_node.getPresentation().setBounds(stringToRect(bounds_.getText()));
                    List ee = e.getChildren("property");
                    for (Object anEe : ee) {
                        String property_name = ((Element) anEe).getAttribute("name").getValue();
                        String property_expr=null;
                        String krn_name="";
                        NodeProperty node_prop=NodeProperty.forName(property_name);
                        if(property_name.indexOf("KRN")>=0){
                            krn_name=property_name.substring(3);
                        }
                        if (langMap.get(lang) != null && langMap.get(lang).size() > 0 && property_name.equals("name"))
                        {
                            property_expr = langMap.get(lang, "name_" + node_id);
                        } else if (langMap.get(lang) != null && langMap.get(lang).size() > 0 && property_name.equals("description"))
                        {
                            property_expr = langMap.get(lang, "desc_" + node_id);
                        }
                        if(property_expr==null)
                            property_expr = ((Element) anEe).getText();
                        if (node_prop != null){
                            Object prop_value= state_node.getProperty(node_prop);
                            if(prop_value instanceof KrnObjectItem){
                                ((KrnObjectItem)prop_value).title= property_expr;
                                state_node.setProperty(node_prop, prop_value);
                            }else
                                state_node.setProperty(node_prop, property_expr);
                        }
                    }
                    ee = e.getChildren("action");
                    for (Object anEe1 : ee) {
                        String action_name = ((Element) anEe1).getAttribute("name").getValue();
                        String action_expr = ((Element) anEe1).getText();
                        if (NodeEventType.forName(action_name) != null)
                            state_node.setAction(NodeEventType.forName(action_name), action_expr);

                    }
                    state_node.setName(state_node.getName());
                    nodeMap.put(node_id, state_node);
                }
            }
            //edges
            List edges=xml.getChildren("edge");
            for (Object edge1 : edges) {
                Element e = (Element) edge1;
                Attribute edge_id = e.getAttribute("id");
                Attribute edge_name = e.getAttribute("name");
                String edge_from = e.getAttribute("from").getValue();
                String edge_to = e.getAttribute("to").getValue();
                TransitionEdge edge;
                if (edge_id != null)
                    edge = new TransitionEdge(edge_id.getValue(), this);
                else
                    edge = new TransitionEdge(this);
                String edge_id_=edge.getId();
                NetPort src = nodeMap.get(edge_from).getPort(0);
                NetPort dst = nodeMap.get(edge_to).getPort(0);
                if (edge_name != null) {
                    String name_= langMap.get(lang,"edge_"+edge_id_);
                    if(name_!=null){
                        edge.setName(name_);
                    }else{
                        langMap.put(langRu,"edge_"+edge_id_,edge_name.getValue());
                        edge.setName(edge_name.getValue());
                    }
                }
                edge.connect(this, src, dst);
                addEdge(edge);
                if (e.getChild("point") != null) {
                    String edge_pts = e.getChild("point").getText();
                    edge.setPoints(stringToPointsVector(edge_pts));
                }
                List ee = e.getChildren("property");
                for (Object anEe : ee) {
                    String property_name = ((Element) anEe).getAttribute("name").getValue();
                    String property_expr = ((Element) anEe).getText();
                    if (NodeProperty.forName(property_name) != null)
                        edge.setProperty(NodeProperty.forName(property_name), property_expr);
                }
            }
            List circles_=xml.getChildren("circle");
            for (Object aCircles_ : circles_) {
                Element el = (Element) aCircles_;
                String bounds_str = el.getAttribute("bounds").getValue();
                Rectangle rec = stringToRect(bounds_str);
                FigCircle f_c = new FigCircle(rec.x, rec.y, rec.width, rec.height);
                Attribute color_line_ = el.getAttribute("colorLine");
                if (color_line_ != null) f_c.setLineColor(stringToColor(color_line_.getValue()));
                Attribute color_fill_ = el.getAttribute("colorFill");
                if (color_fill_ != null) f_c.setLineColor(stringToColor(color_fill_.getValue()));
                lm_.add(f_c);
            }
            List rect_=xml.getChildren("rect");
            for (Object aRect_ : rect_) {
                Element el = (Element) aRect_;
                String bounds_str = el.getAttribute("bounds").getValue();
                Rectangle rec = stringToRect(bounds_str);
                FigRect f_c = new FigRect(rec.x, rec.y, rec.width, rec.height);
                Attribute color_line_ = el.getAttribute("colorLine");
                if (color_line_ != null) f_c.setLineColor(stringToColor(color_line_.getValue()));
                Attribute color_fill_ = el.getAttribute("colorFill");
                if (color_fill_ != null) f_c.setLineColor(stringToColor(color_fill_.getValue()));
                lm_.add(f_c);
            }
            List rrect_=xml.getChildren("rrect");
            for (Object aRrect_ : rrect_) {
                Element el = (Element) aRrect_;
                String bounds_str = el.getAttribute("bounds").getValue();
                Rectangle rec = stringToRect(bounds_str);
                FigRRect f_c = new FigRRect(rec.x, rec.y, rec.width, rec.height);
                Attribute c_radius = el.getAttribute("cornerRadius");
                if (c_radius != null) f_c.setCornerRadius(Integer.valueOf(c_radius.getValue()));
                Attribute color_line_ = el.getAttribute("colorLine");
                if (color_line_ != null) f_c.setLineColor(stringToColor(color_line_.getValue()));
                Attribute color_fill_ = el.getAttribute("colorFill");
                if (color_fill_ != null) f_c.setLineColor(stringToColor(color_fill_.getValue()));
                lm_.add(f_c);
            }
            List texts_=xml.getChildren("text");
            int ti=1;
            for(Iterator e=texts_.iterator();e.hasNext();++ti){
                Element el= (Element)e.next();
                String bounds_str=el.getAttribute("bounds").getValue();
                Rectangle rec=stringToRect(bounds_str);
                FigText f_t=new FigText(rec.x,rec.y,rec.width,rec.height);
                Attribute color_line_=el.getAttribute("lineColor");
                if(color_line_!=null) f_t.setLineColor(stringToColor(color_line_.getValue()));
                Attribute color_fill_=el.getAttribute("fillColor");
                if(color_fill_!=null) f_t.setLineColor(stringToColor(color_fill_.getValue()));
                String ft_str=langMap.get(lang)!=null && langMap.get(lang).size()>0?langMap.get(lang,"text_"+ti):el.getText();
                f_t.setText(ft_str==null?"Безымянный":ft_str);
                Attribute color_text_=el.getAttribute("textColor");
                if(color_text_!=null) f_t.setTextColor(stringToColor(color_text_.getValue()));
                lm_.add(f_t);
            }
            List splines_=xml.getChildren("spline");
            for (Object aSplines_ : splines_) {
                Element el = (Element) aSplines_;
                String points_str = el.getAttribute("points").getValue();
                FigSpline f_s = new FigSpline();
                Vector<Point> pts = stringToPointsVector(points_str);
                for (Object pt : pts) {
                    f_s.addPoint((Point) pt);
                }
                Attribute color_line_ = el.getAttribute("colorLine");
                if (color_line_ != null) f_s.setLineColor(stringToColor(color_line_.getValue()));
                Attribute color_fill_ = el.getAttribute("colorFill");
                if (color_fill_ != null) f_s.setLineColor(stringToColor(color_fill_.getValue()));
                lm_.add(f_s);
            }
            List inks_=xml.getChildren("ink");
            for (Object anInks_ : inks_) {
                Element el = (Element) anInks_;
                String points_str = el.getAttribute("points").getValue();
                FigInk f_i = new FigInk();
                Vector<Point> pts = stringToPointsVector(points_str);
                for (Object pt : pts) {
                    f_i.addPoint((Point) pt);
                }
                Attribute color_line_ = el.getAttribute("colorLine");
                if (color_line_ != null) f_i.setLineColor(stringToColor(color_line_.getValue()));
                Attribute color_fill_ = el.getAttribute("colorFill");
                if (color_fill_ != null) f_i.setLineColor(stringToColor(color_fill_.getValue()));
                lm_.add(f_i);
            }
            List polies_=xml.getChildren("poly");
            for (Object aPolies_ : polies_) {
                Element el = (Element) aPolies_;
                String points_str = el.getAttribute("points").getValue();
                FigPoly f_p = new FigPoly();
                Vector<Point> pts = stringToPointsVector(points_str);
                for (Object pt : pts) {
                    f_p.addPoint((Point) pt);
                }
                Attribute color_line_ = el.getAttribute("colorLine");
                if (color_line_ != null) f_p.setLineColor(stringToColor(color_line_.getValue()));
                Attribute color_fill_ = el.getAttribute("colorFill");
                if (color_fill_ != null) f_p.setLineColor(stringToColor(color_fill_.getValue()));
                lm_.add(f_p);
            }
            List lines_edge=xml.getChildren("lineEdge");
            for (Object aLines_edge : lines_edge) {
                Element el = (Element) aLines_edge;
                String id_str = el.getAttribute("id").getValue();
                String points_str = el.getAttribute("points").getValue();
                Point[] pts = stringToPoints(points_str);
                FigLineEdge f_l = new FigLineEdge(pts[0].x, pts[0].y, pts[1].x, pts[1].y, isEnable);
                f_l.setId(id_str);
                Attribute color_line_ = el.getAttribute("colorLine");
                if (color_line_ != null) f_l.setLineColor(stringToColor(color_line_.getValue()));
                lm_.add(f_l);
                lines.put(f_l.getId(), f_l);
            }
            List lines_=xml.getChildren("line");
            for (Object aLines_ : lines_) {
                Element el = (Element) aLines_;
                String points_str = el.getAttribute("points").getValue();
                Point[] pts = stringToPoints(points_str);
                FigLine f_l = new FigLine(pts[0].x, pts[0].y, pts[1].x, pts[1].y);
                Attribute color_line_ = el.getAttribute("colorLine");
                if (color_line_ != null) f_l.setLineColor(stringToColor(color_line_.getValue()));
                lm_.add(f_l);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        isLoadProcess = false;
    }

    public void load(InputStream is, JGraph graph) throws IOException {
        if (is.available() > 0) {
            try {
                SAXBuilder builder = new SAXBuilder();
                builder.setValidation(false);
                Element xml = builder.build(is).getRootElement();
                is.close();
                load(xml, graph);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public void load(Element confXml, JGraph graph) throws IOException {
    	Kernel krn = Kernel.instance();
        isLoadProcess = true;
        Hashtable<String,Boolean> table=new Hashtable<String,Boolean>();
        table.put("enable", isEnable);
        LayerManager lm_=graph.getEditor().getLayerManager();
        processNode.initialize(null);
        lines=new HashMap<String,Object>();
        try {
            processNode.setAction(NodeEventTypeConstants.PROCESS_INSTANCE_START,null);
            //nodes
            List nodes=confXml.getChildren("node");
            List process=confXml.getChildren("process");
            if(process.size()>0){
            Element e =(Element)process.get(0);
                List ee =e.getChildren("property");
                for (Object anEe : ee) {
                    String property_name = ((Element) anEe).getAttribute("name").getValue();
                    String property_expr;
                    String krn_name="";
                    NodeProperty node_prop=NodeProperty.forName(property_name);
                    if(property_name.indexOf("KRN")>=0){
                        krn_name=property_name.substring(3);
                    }
                    if (langMap.get(lang) != null && langMap.get(lang).size() > 0 && property_name.equals("name"))
                    {
                        property_expr = langMap.get(lang, "process_0");
                    } else if (langMap.get(lang) != null && langMap.get(lang).size() > 0 && property_name.equals("description"))
                    {
                        property_expr = langMap.get(lang, "process_desc_0");
                    } else
                        property_expr = ((Element) anEe).getText();
                    if (property_expr == null || property_expr.equals(""))
                        property_expr = ((Element) anEe).getText();
                    if (node_prop != null){
                        Object prop_value= processNode.getProperty(node_prop);
                        if(prop_value instanceof KrnObjectItem){
                            ((KrnObjectItem)prop_value).title= property_expr;
                            processNode.setProperty(node_prop, prop_value);
                        }else
                            processNode.setProperty(node_prop, property_expr);
                    }else if(!krn_name.equals("")){
                        try{
                            NodeProperty node_prop_=NodeProperty.forName(krn_name);
                            KrnObject obj = krn.getCachedObjectByUid(property_expr);
                            if(node_prop_ != null && obj != null){
                                Object prop_value= processNode.getProperty(node_prop_);
                                processNode.setProperty(node_prop_,
                                        new KrnObjectItem(obj,prop_value instanceof String?(String)prop_value:"title"));
                        }
                        }catch(Exception ex){
                            ex.printStackTrace();
                        }
                    }
                }
                ee =e.getChildren("action");
                for (Object anEe1 : ee) {
                    String action_name = ((Element) anEe1).getAttribute("name").getValue();
                    String action_expr = ((Element) anEe1).getText();
                    if (NodeEventType.forName(action_name) != null)
                        processNode.setAction(NodeEventType.forName(action_name), action_expr);

                }
            }
            for (Object node : nodes) {
                Element e = (Element) node;
                String node_id = e.getAttribute("id").getValue();
                String node_class = e.getAttribute("class").getValue();
                StateNode state_node = null;
                if (node_class.equals("StartStateNode")) {
                    state_node = new StartStateNode(node_id, this);
                } else if (node_class.equals("StartSyncNode")) {
                    state_node = new StartSyncNode(node_id, this);
                } else if (node_class.equals("EndSyncNode")) {
                    state_node = new EndSyncNode(node_id, this);
                } else if (node_class.equals("EndStateNode")) {
                    state_node = new EndStateNode(node_id, this);
                } else if (node_class.equals("ActivityStateNode")) {
                    state_node = new ActivityStateNode(node_id, this);
                } else if (node_class.equals("InBoxStateNode")) {
                    state_node = new InBoxStateNode(node_id, this);
                } else if (node_class.equals("OutBoxStateNode")) {
                    state_node = new OutBoxStateNode(node_id, this);
                } else if (node_class.equals("SubProcessStateNode")) {
                    state_node = new SubProcessStateNode(node_id, this);
                } else if (node_class.equals("DecisionStateNode")) {
                    state_node = new DecisionStateNode(node_id, this);
                } else if (node_class.equals("ForkNode")) {
                    state_node = new ForkNode(node_id, this);
                } else if (node_class.equals("JoinNode")) {
                    state_node = new JoinNode(node_id, this);
                } else if (node_class.equals("NoteStateNode")) {
                    state_node = new NoteStateNode(node_id, this);
                } else if (node_class.equals("ReportStateNode")) {
                    state_node = new ReportStateNode(node_id, this);
                } else if (node_class.equals("StartSyncNode")) {
                    state_node = new StartSyncNode(node_id, this);
                } else if (node_class.equals("EndSyncNode")) {
                    state_node = new EndSyncNode(node_id, this);
                }
                if (state_node != null) {
                    state_node.initialize(table);
                    addNode(state_node);
                    Element bounds_ = e.getChild("bounds");
                    if (bounds_ != null)
                        state_node.getPresentation().setBounds(stringToRect(bounds_.getText()));
                    List ee = e.getChildren("property");
                    for (Object anEe : ee) {
                        String property_name = ((Element) anEe).getAttribute("name").getValue();
                        String property_expr=null;
                        String krn_name="";
                        NodeProperty node_prop=NodeProperty.forName(property_name);
                        if(property_name.indexOf("KRN")>=0){
                            krn_name=property_name.substring(3);
                        }
                        if (langMap.get(lang) != null && langMap.get(lang).size() > 0 && property_name.equals("name"))
                        {
                            property_expr = langMap.get(lang, "name_" + node_id);
                        } else if (langMap.get(lang) != null && langMap.get(lang).size() > 0 && property_name.equals("description"))
                        {
                            property_expr = langMap.get(lang, "desc_" + node_id);
                        }
                        if(property_expr==null)
                            property_expr = ((Element) anEe).getText();
                        if (node_prop != null){
                            Object prop_value= state_node.getProperty(node_prop);
                            if(prop_value instanceof KrnObjectItem){
                                ((KrnObjectItem)prop_value).title= property_expr;
                                state_node.setProperty(node_prop, prop_value);
                            }else
                                state_node.setProperty(node_prop, property_expr);
                        }else if(!krn_name.equals("")){
                            try{
                                NodeProperty node_prop_=NodeProperty.forName(krn_name);
                                KrnObject obj = krn.getCachedObjectByUid(property_expr);
                                if(node_prop_!=null && obj != null){
                                    Object prop_value= state_node.getProperty(node_prop_);
                                    state_node.setProperty(node_prop_,
                                            new KrnObjectItem(obj,prop_value instanceof String?(String)prop_value:"title"));
                            }
                            }catch(Exception ex){
                                ex.printStackTrace();
                            }
                        }
                    }
                    ee = e.getChildren("action");
                    for (Object anEe1 : ee) {
                        String action_name = ((Element) anEe1).getAttribute("name").getValue();
                        String action_expr = ((Element) anEe1).getText();
                        if (NodeEventType.forName(action_name) != null)
                            state_node.setAction(NodeEventType.forName(action_name), action_expr);

                    }
                    state_node.setName(state_node.getName());
                    nodeMap.put(node_id, state_node);
                }
            }
            //edges
            List edges=confXml.getChildren("edge");
            for (Object edge1 : edges) {
                Element e = (Element) edge1;
                Attribute edge_id = e.getAttribute("id");
                Attribute edge_name = e.getAttribute("name");
                String edge_from = e.getAttribute("from").getValue();
                String edge_to = e.getAttribute("to").getValue();
                TransitionEdge edge;
                if (edge_id != null)
                    edge = new TransitionEdge(edge_id.getValue(), this);
                else
                    edge = new TransitionEdge(this);
                String edge_id_=edge.getId();
                NetPort src = nodeMap.get(edge_from).getPort(0);
                NetPort dst = nodeMap.get(edge_to).getPort(0);
                if (edge_name != null) {
                    String name_= langMap.get(lang,"edge_"+edge_id_);
                    if(name_!=null){
                        edge.setName(name_);
                    }else{
                        langMap.put(langRu,"edge_"+edge_id_,edge_name.getValue());
                        edge.setName(edge_name.getValue());
                    }
                }
                edge.connect(this, src, dst);
                addEdge(edge);
                if (e.getChild("point") != null) {
                    String edge_pts = e.getChild("point").getText();
                    edge.setPoints(stringToPointsVector(edge_pts));
                }
                List ee = e.getChildren("property");
                for (Object anEe : ee) {
                    String property_name = ((Element) anEe).getAttribute("name").getValue();
                    String property_expr = ((Element) anEe).getText();
                    if (NodeProperty.forName(property_name) != null)
                        edge.setProperty(NodeProperty.forName(property_name), property_expr);
                }
            }
            List circles_=confXml.getChildren("circle");
            for (Object aCircles_ : circles_) {
                Element el = (Element) aCircles_;
                String bounds_str = el.getAttribute("bounds").getValue();
                Rectangle rec = stringToRect(bounds_str);
                FigCircle f_c = new FigCircle(rec.x, rec.y, rec.width, rec.height);
                Attribute color_line_ = el.getAttribute("colorLine");
                if (color_line_ != null) f_c.setLineColor(stringToColor(color_line_.getValue()));
                Attribute color_fill_ = el.getAttribute("colorFill");
                if (color_fill_ != null) f_c.setLineColor(stringToColor(color_fill_.getValue()));
                lm_.add(f_c);
            }
            List rect_=confXml.getChildren("rect");
            for (Object aRect_ : rect_) {
                Element el = (Element) aRect_;
                String bounds_str = el.getAttribute("bounds").getValue();
                Rectangle rec = stringToRect(bounds_str);
                FigRect f_c = new FigRect(rec.x, rec.y, rec.width, rec.height);
                Attribute color_line_ = el.getAttribute("colorLine");
                if (color_line_ != null) f_c.setLineColor(stringToColor(color_line_.getValue()));
                Attribute color_fill_ = el.getAttribute("colorFill");
                if (color_fill_ != null) f_c.setLineColor(stringToColor(color_fill_.getValue()));
                lm_.add(f_c);
            }
            List rrect_=confXml.getChildren("rrect");
            for (Object aRrect_ : rrect_) {
                Element el = (Element) aRrect_;
                String bounds_str = el.getAttribute("bounds").getValue();
                Rectangle rec = stringToRect(bounds_str);
                FigRRect f_c = new FigRRect(rec.x, rec.y, rec.width, rec.height);
                Attribute c_radius = el.getAttribute("cornerRadius");
                if (c_radius != null) f_c.setCornerRadius(Integer.valueOf(c_radius.getValue()));
                Attribute color_line_ = el.getAttribute("colorLine");
                if (color_line_ != null) f_c.setLineColor(stringToColor(color_line_.getValue()));
                Attribute color_fill_ = el.getAttribute("colorFill");
                if (color_fill_ != null) f_c.setLineColor(stringToColor(color_fill_.getValue()));
                lm_.add(f_c);
            }
            List texts_=confXml.getChildren("text");
            int ti=1;
            for(Iterator e=texts_.iterator();e.hasNext();++ti){
                Element el= (Element)e.next();
                String bounds_str=el.getAttribute("bounds").getValue();
                Rectangle rec=stringToRect(bounds_str);
                FigText f_t=new FigText(rec.x,rec.y,rec.width,rec.height);
                Attribute color_line_=el.getAttribute("lineColor");
                if(color_line_!=null) f_t.setLineColor(stringToColor(color_line_.getValue()));
                Attribute color_fill_=el.getAttribute("fillColor");
                if(color_fill_!=null) f_t.setLineColor(stringToColor(color_fill_.getValue()));
                String ft_str=langMap.get(lang)!=null && langMap.get(lang).size()>0?langMap.get(lang,"text_"+ti):el.getText();
                f_t.setText(ft_str==null?"Безымянный":ft_str);
                Attribute color_text_=el.getAttribute("textColor");
                if(color_text_!=null) f_t.setTextColor(stringToColor(color_text_.getValue()));
                lm_.add(f_t);
            }
            List splines_=confXml.getChildren("spline");
            for (Object aSplines_ : splines_) {
                Element el = (Element) aSplines_;
                String points_str = el.getAttribute("points").getValue();
                FigSpline f_s = new FigSpline();
                Vector<Point> pts = stringToPointsVector(points_str);
                for (Object pt : pts) {
                    f_s.addPoint((Point) pt);
                }
                Attribute color_line_ = el.getAttribute("colorLine");
                if (color_line_ != null) f_s.setLineColor(stringToColor(color_line_.getValue()));
                Attribute color_fill_ = el.getAttribute("colorFill");
                if (color_fill_ != null) f_s.setLineColor(stringToColor(color_fill_.getValue()));
                lm_.add(f_s);
            }
            List inks_=confXml.getChildren("ink");
            for (Object anInks_ : inks_) {
                Element el = (Element) anInks_;
                String points_str = el.getAttribute("points").getValue();
                FigInk f_i = new FigInk();
                Vector<Point> pts = stringToPointsVector(points_str);
                for (Object pt : pts) {
                    f_i.addPoint((Point) pt);
                }
                Attribute color_line_ = el.getAttribute("colorLine");
                if (color_line_ != null) f_i.setLineColor(stringToColor(color_line_.getValue()));
                Attribute color_fill_ = el.getAttribute("colorFill");
                if (color_fill_ != null) f_i.setLineColor(stringToColor(color_fill_.getValue()));
                lm_.add(f_i);
            }
            List polies_=confXml.getChildren("poly");
            for (Object aPolies_ : polies_) {
                Element el = (Element) aPolies_;
                String points_str = el.getAttribute("points").getValue();
                FigPoly f_p = new FigPoly();
                Vector<Point> pts = stringToPointsVector(points_str);
                for (Object pt : pts) {
                    f_p.addPoint((Point) pt);
                }
                Attribute color_line_ = el.getAttribute("colorLine");
                if (color_line_ != null) f_p.setLineColor(stringToColor(color_line_.getValue()));
                Attribute color_fill_ = el.getAttribute("colorFill");
                if (color_fill_ != null) f_p.setLineColor(stringToColor(color_fill_.getValue()));
                lm_.add(f_p);
            }
            List lines_edge=confXml.getChildren("lineEdge");
            for (Object aLines_edge : lines_edge) {
                Element el = (Element) aLines_edge;
                String id_str = el.getAttribute("id").getValue();
                String points_str = el.getAttribute("points").getValue();
                Point[] pts = stringToPoints(points_str);
                FigLineEdge f_l = new FigLineEdge(pts[0].x, pts[0].y, pts[1].x, pts[1].y, isEnable);
                f_l.setId(id_str);
                Attribute color_line_ = el.getAttribute("colorLine");
                if (color_line_ != null) f_l.setLineColor(stringToColor(color_line_.getValue()));
                lm_.add(f_l);
                lines.put(f_l.getId(), f_l);
            }
            List lines_=confXml.getChildren("line");
            for (Object aLines_ : lines_) {
                Element el = (Element) aLines_;
                String points_str = el.getAttribute("points").getValue();
                Point[] pts = stringToPoints(points_str);
                FigLine f_l = new FigLine(pts[0].x, pts[0].y, pts[1].x, pts[1].y);
                Attribute color_line_ = el.getAttribute("colorLine");
                if (color_line_ != null) f_l.setLineColor(stringToColor(color_line_.getValue()));
                lm_.add(f_l);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        isLoadProcess = false;
    }

    private String pointsToString(List pts) {
        StringBuffer res = new StringBuffer(((Point)pts.get(0)).x + "," + ((Point)pts.get(0)).y);
        for (int i = 1; i < pts.size(); i++) {
            Point pt = (Point)pts.get(i);
            res.append(",");
            res.append(pt.x);
            res.append(",");
            res.append(pt.y);
        }
        return res.toString();
    }

    private String pointsToString(Point[] pts) {
        StringBuffer res = new StringBuffer(pts[0].x + "," + pts[0].y);
        for (int i = 1; i < pts.length; i++) {
            res.append(",");
            res.append(pts[i].x);
            res.append(",");
            res.append(pts[i].y);
        }
        return res.toString();
    }
    private Vector<Point> stringToPointsVector(String str) {
        StringTokenizer st = new StringTokenizer(str, ",");
        Vector<Point> res = new Vector<Point>();
        while (st.hasMoreTokens()) {
            Point p_= new Point();
            p_.x = Integer.parseInt(st.nextToken());
            p_.y = Integer.parseInt(st.nextToken());
            res.add(p_);
        }
        return res;
    }

    private Point[] stringToPoints(String str) {
        StringTokenizer st = new StringTokenizer(str, ",");
        Point[] res = new Point[st.countTokens()/2];
        int i=0;
        while (st.hasMoreTokens()) {
            Point p_= new Point();
            p_.x = Integer.parseInt(st.nextToken());
            p_.y = Integer.parseInt(st.nextToken());
            res[i++]=p_;
        }
        return res;
    }
    private String rectToString(Rectangle rect) {
        return rect.x + "," + rect.y + "," + rect.width + "," + rect.height;
    }

    private Rectangle stringToRect(String str) {
        StringTokenizer st = new StringTokenizer(str, ",");
        int x = Integer.parseInt(st.nextToken());
        int y = Integer.parseInt(st.nextToken());
        int w = Integer.parseInt(st.nextToken());
        int h = Integer.parseInt(st.nextToken());
        return new Rectangle(x, y, w, h);
    }

    private String colorToString(Color color) {
        return color.getRed()+ "," + color.getGreen() + "," + color.getBlue();
    }

    private Color stringToColor(String str) {
        StringTokenizer st = new StringTokenizer(str, ",");
        int r = Integer.parseInt(st.nextToken());
        int g = Integer.parseInt(st.nextToken());
        int b = Integer.parseInt(st.nextToken());
        return new Color(r,g,b);
    }

    public void addEdge(Object edge) {
        super.addEdge(edge);
        if(mf!=null) {
            if (!isLoadProcess) {
            	mf.addEdge((TransitionEdge) edge, obj);
                mf.setProcessModified(true);
            }
        }
    }
    
    public void removeEdge(Object edge){
        super.removeEdge(edge);
        if(mf!=null) {
        	mf.removeEdge((TransitionEdge) edge, obj);
        	mf.setProcessModified(true);
        }
    }

    public void addNode(Object node) {
        super.addNode(node);
        ((StateNode)node).getPresentation().addPropertyChangeListener(this);
        if (node instanceof StartStateNode) {
            startNode = (StartStateNode)node;
        }else if (node instanceof EndStateNode) {
            endNode = (EndStateNode)node;
        }
        if (mf != null) {
            if (!isLoadProcess) {     
            	mf.addNode((StateNode)node, obj);
            	mf.setProcessModified(true);
            }
            mf.setSelectionMode();
        }
        nodeMap.put(((StateNode)node).getId(),(StateNode)node);
    }

    public void removeNode(Object node) {
        super.removeNode(node);
        if (node instanceof StartStateNode) {
            startNode = null;
        }else if (node instanceof EndStateNode) {
            endNode = null;
        }
        mf.removeNode((StateNode) node, obj);
    	mf.setProcessModified(true);
        nodeMap.remove(((StateNode)node).getId());
    }

    public boolean canAddNode(Object node) {
        if ((node instanceof StartStateNode && startNode != null)
        ||(node instanceof EndStateNode && endNode != null)) {
            return false;
        } else {
            return super.canAddNode(node);
        }
    }

    public StateNode getStartNode(){
        return startNode;
    }

    public StateNode getEndNode(){
        return endNode;
    }

    private void validate() {
      errors_= new Vector<String>();
      if(startNode == null) {
          errors_.add("Ошибка! Отсутствует начало процесса!");
          return;
      } else if (endNode == null) {
            errors_.add("Ошибка! Отсутствует окончание процесса!");
            return;
      }
      edges = new HashMap<FigTransitionEdge, FigTransitionEdge>();
      forkJoin = new HashMap<StateNode, StateNode>();
      nodeFork = new HashMap<StateNode, StateNode>();
      processNodes = new MultiMap();
      scanNodes = new Vector<StateNode>();
      scanNode(startNode, null);
      processNodes.put(0,endNode);
      Vector nodes_= (Vector) getNodes(new Vector());
      boolean par=true;
        for (Object aNodes_ : nodes_) {
            StateNode node = (StateNode) aNodes_;
            if ((!scanNodes.contains(node) && !(node instanceof NoteStateNode)
                    && !(node instanceof ReportStateNode)) && !node.equals(endNode)) {
                if (par) {
                    errors_.add("Ошибка! Недостижимый элемент:");
                    par = false;
                }
                errors_.add("'" + ((StateNode) aNodes_).getName() + "'");
            }
        }
    }
    
    public Map<String,StateNode> getNodesMap(){
        return nodeMap;
    }
    
//    public Map getLinesMap(){
//        return lines;
//    }
    
    private StateNode scanNode(StateNode node,StateNode fork){
        if(node==null || node.equals(endNode)) return null;
        StateNode fork_=null,join_=null,node_=null;
        Vector<StateNode> d_node=null;
        if(node instanceof ForkNode) fork_=node;
        if(!scanNodes.contains(node)) {
            scanNodes.add(node);
            if(fork_==null && fork==null) processNodes.put(0,node);
            else  {
                processNodes.put(new Integer((fork_==null?fork:fork_).getId()),node);
                if(fork_!=null)
                processNodes.put(new Integer(fork==null?"0":fork.getId()),node);
                if(fork!=null){
                    if(!nodeFork.containsKey(node)) nodeFork.put(node,fork);
                }
            }
        }
        Collection c= node.getPresentation().getFigEdges(new ArrayList());
        Iterator it=c.iterator();
        int live_edges=0;
        while(it.hasNext()){
            FigTransitionEdge edge_=(FigTransitionEdge)it.next();
            StateNode node_c=(StateNode)edge_.getDestFigNode().getOwner();
            if(node==node_c) continue;
            live_edges++;
            if(edges.keySet().contains(edge_)) {
                continue;//пропускать повторный проход петли
            }else{
                    edges.put(edge_,edge_);
            }
            if(node instanceof DecisionStateNode ){
                TransitionEdge te= (TransitionEdge)edge_.getOwner();
                if(te.getName()==null || te.getName().equals(""))
                    errors_.add("Ошибка:У перехода для узла '"+node.getName()+"' отсутствует наименование");
            }
            node_=node_c;
            if(node_ instanceof JoinNode ){
                if(fork==null)
                    errors_.add("Ошибка:Элемент '"+node.getName()+"' без разветвления");
                if(join_==null){
                    if(fork_!=null ||fork!=null){
                       if(forkJoin.get((node_))==null) forkJoin.put(node_,fork);
                       join_=node_;
                    }
                }
                continue;
            }
            node_= scanNode(node_,fork_==null?fork:fork_);
            if(node_!=null && node instanceof DecisionStateNode){
             if(d_node==null) d_node=new Vector<StateNode>();
                d_node.add(node_);
            }
            if(node_c!=endNode &&
               !(node_c instanceof ForkNode)&&
               ((fork_!=null && !(node_ instanceof JoinNode))||(fork!=null && node_==null)))
              if((fork!=null && nodeFork.get(node_c)!= fork && nodeFork.get(node_c)!= fork_)|| (node_c instanceof ForkNode && nodeFork.get(node_)!= node_c))
                  errors_.add("Ошибка! Элемент '"+node_c.getName()+"' имеет другое разветвление!");
            else if(nodeFork.get(node_c)!=fork && fork!=null)
                  errors_.add("Ошибка! Элемент '"+node_c.getName()+"' без объединения!");
            if(fork==null && nodeFork.get(node_c)!= null && nodeFork.get(node_c)!= fork_)
                errors_.add("Ошибка! Путь к элементу '"+node_c.getName()+"'из вне разветвления'"+nodeFork.get(node_c).getName()+"'!");
            if(node_c==endNode && fork!=null && fork!=forkJoin.get(node))
                errors_.add("Ошибка! Элемент '"+node.getName()+"' без объединения!");
            if(node_ instanceof JoinNode ){
                if(fork_==null && fork==null)
                    errors_.add("Ошибка! Объединение '"+node_.getName()+"' без разветвления!");
                if(join_==null) {
                    if(fork_!=null ||fork!=null){
                        StateNode fork_j =forkJoin.get((node_));
                        if(fork_j==null){
                            join_=node_;
                            forkJoin.put(join_,fork_==null?fork:fork_);
                        }else if(fork_j!=(fork_==null?fork:fork_))
                             errors_.add("Ошибка! Элемент '"+node_c.getName()+"' имеет другое объединение!");
                        else join_=node_;
                    }
                }
                else if(join_!=node_){
                    errors_.add("Ошибка! Элемент '"+node_c.getName()+"' имеет другое объединение!"+join_.getName());
                    if(forkJoin.get(node_)==(fork_==null?fork:fork_)) forkJoin.remove(node_);
                }
            }
        }
        if (live_edges==0){
            errors_.add("Ошибка! Не завершается процесс по пути через элемент '"+node.getName()+"'");
        }
       if(fork_!=null) {
            if(join_==null){
                if(fork!=null && nodeFork.get(fork_)==null)
                    errors_.add("Ошибка! Элемент '"+fork_.getName()+"' не должен иметь разветвления!");
                else if(fork!=null && nodeFork.get(fork_)!=fork)
                    errors_.add("Ошибка! Элемент '"+fork_.getName()+"' имеет другое разветвление!");
            }
            node_=scanNode(join_,fork);
        } else if(join_!=null) return join_;
        else if(node_!=null) node_=scanNode(node_,fork);
        return node_;
    }
    public List<String> checkProcess(DebuggerInterface debugger) {
      errors_=new Vector<String>();
//      if(clientOrlang==null) clientOrlang=new ClientOrLang();
//      if(exprDebuger==null)
      exprDebuger = debugger;
      Set<String> vars= new HashSet<String>();
      Set<StateNode> nodes= new HashSet<StateNode>();
      scanNodeDebug(startNode,vars,nodes);
//      return errors_;
        List<ErrRecord> errs= exprDebuger.getErrors();
        for (Object err : errs) {
            ErrRecord rec = (ErrRecord) err;
            if (!errors_.contains(rec.toString())) errors_.add(rec.toString());
        }
        errors_.addAll(eventErrors);
        return errors_;
    }
    private StateNode scanNodeDebug(StateNode node,Set<String> vars,Set<StateNode> nodes){
        nodes.add(node);
        checkFuncsNode(node,vars);
        StateNode node_=node;
        Collection c= node.getPresentation().getFigEdges(new ArrayList());
        Iterator it=c.iterator();
        Set<String> vars_f=new HashSet<String>();
        vars_f.addAll(vars);
        while(it.hasNext()){
            FigTransitionEdge edge_=(FigTransitionEdge)it.next();
            node_=(StateNode)edge_.getDestFigNode().getOwner();
            if(nodes.contains(node_)) continue;
            Set<String> vars_=new HashSet<String>();
            vars_.addAll(vars);
            Set<StateNode> nodes_=new HashSet<StateNode>();
            nodes_.addAll(nodes);
            node_= scanNodeDebug(node_,vars_,nodes_);
            if(node instanceof ForkNode && node_ instanceof JoinNode){
                vars_f.addAll(vars_);
            }
        }
        if(node instanceof ForkNode){
            vars.addAll(vars_f);
        }
        return node_;
    }
    private void checkFuncsNode(StateNode node,Set<String> vars){
        if(node instanceof StartStateNode){
            Object res=processNode.getProperty(NodePropertyConstants.NODE_TITLE);
            if(res!=null) checkExpr(processNode.getName()+"|"+NodePropertyConstants.NODE_TITLE.getTitle(),vars,res.toString());
            res=processNode.getProperty(NodePropertyConstants.RESPONSIBLE);
            if(res!=null && !(res instanceof KrnObjectItem)) checkExpr(processNode.getName()+"|"+NodePropertyConstants.RESPONSIBLE.getTitle(),vars,res.toString());
            res=processNode.getProperty(NodePropertyConstants.CHOPPER);
            if(res!=null && !(res instanceof KrnObjectItem)) checkExpr(processNode.getName()+"|"+NodePropertyConstants.CHOPPER.getTitle(),vars,res.toString());
            res=processNode.getProperty(NodePropertyConstants.CONFLICT_PROCESS);
            if(res!=null && !(res instanceof KrnObjectItem)) checkExpr(processNode.getName()+"|"+NodePropertyConstants.CONFLICT_PROCESS.getTitle(),vars,res.toString());
            res=processNode.getProperty(NodePropertyConstants.INSPECTORS);
            if(res!=null) checkExpr(processNode.getName()+"|"+NodePropertyConstants.INSPECTORS.getTitle(),vars,res.toString());
            res=processNode.getAction(NodeEventTypeConstants.PROCESS_INSTANCE_START);
            if(res!=null) {
            	checkExpr(processNode.getName()+"|"+NodeEventTypeConstants.PROCESS_INSTANCE_START.getTitle(),vars,res.toString());
            	checkEventExpr(processNode.getName()+"|"+NodeEventTypeConstants.PROCESS_INSTANCE_START.getTitle(), res.toString());
            }
            res=node.getAction(NodeEventTypeConstants.BEFORE_PERFORM_OF_ACTIVITY);
            if(res!=null) checkExpr("Начало|"+NodeEventTypeConstants.BEFORE_PERFORM_OF_ACTIVITY.getTitle(),vars,res.toString());
            res=node.getAction(NodeEventTypeConstants.AFTER_PERFORM_OF_ACTIVITY);
            if(res!=null) checkExpr("Начало|"+NodeEventTypeConstants.AFTER_PERFORM_OF_ACTIVITY.getTitle(),vars,res.toString());
        }else if(node instanceof ActivityStateNode){
            Object res=node.getProperty(NodePropertyConstants.NODE_TITLE);
            if(res!=null) checkExpr(node.getName()+"|"+NodePropertyConstants.NODE_TITLE.getTitle(),vars,res.toString());
            res=node.getAction(NodeEventTypeConstants.BEFORE_ACTIVITYSTATE_ASSIGNMENT);
            if(res!=null) checkExpr(node.getName()+"|"+NodeEventTypeConstants.BEFORE_ACTIVITYSTATE_ASSIGNMENT.getTitle(),vars,res.toString());
            res=node.getProperty(NodePropertyConstants.ASSIGNMENT);
            if(res!=null && !(res instanceof KrnObjectItem)) checkExpr(node.getName()+"|"+NodePropertyConstants.ASSIGNMENT.getTitle(),vars,res.toString());
            res=node.getAction(NodeEventTypeConstants.AFTER_ACTIVITYSTATE_ASSIGNMENT);
            if(res!=null) checkExpr(node.getName()+"|"+NodeEventTypeConstants.AFTER_ACTIVITYSTATE_ASSIGNMENT.getTitle(),vars,res.toString());
            res=node.getAction(NodeEventTypeConstants.BEFORE_PERFORM_OF_ACTIVITY);
            if(res!=null) checkExpr(node.getName()+"|"+NodeEventTypeConstants.BEFORE_PERFORM_OF_ACTIVITY.getTitle(),vars,res.toString());
            res=node.getProperty(NodePropertyConstants.PROCESS_OBJECT);
            if(res!=null && !(res instanceof KrnObjectItem)) checkExpr(node.getName()+"|"+NodePropertyConstants.PROCESS_OBJECT.getTitle(),vars,res.toString());
            res=node.getProperty(NodePropertyConstants.OBJECT_TITLE);
            if(res!=null) checkExpr(node.getName()+"|"+NodePropertyConstants.OBJECT_TITLE.getTitle(),vars,res.toString());
            res=node.getProperty(NodePropertyConstants.OBJECT_PARAM);
            if(res!=null) checkExpr(node.getName()+"|"+NodePropertyConstants.OBJECT_PARAM.getTitle(),vars,res.toString());
            res=node.getProperty(NodePropertyConstants.UI_PROCESS);
            if(res!=null && !(res instanceof KrnObjectItem)) checkExpr(node.getName()+"|"+NodePropertyConstants.UI_PROCESS.getTitle(),vars,res.toString());
            res=node.getProperty(NodePropertyConstants.DATE_ALARM);
            if(res!=null) checkExpr(node.getName()+"|"+NodePropertyConstants.DATE_ALARM.getTitle(),vars,res.toString());
            res=node.getProperty(NodePropertyConstants.DATE_ALERT);
            if(res!=null) checkExpr(node.getName()+"|"+NodePropertyConstants.DATE_ALERT.getTitle(),vars,res.toString());
            res=node.getProperty(NodePropertyConstants.TASK_COLOR);
            if(res!=null) checkExpr(node.getName()+"|"+NodePropertyConstants.TASK_COLOR.getTitle(),vars,res.toString());
            res=node.getAction(NodeEventTypeConstants.PERFORM_OF_ACTIVITY);
            if(res!=null) {
            	checkExpr(node.getName()+"|"+NodeEventTypeConstants.PERFORM_OF_ACTIVITY.getTitle(),vars,res.toString());
            	checkEventExpr(node.getName()+"|"+NodeEventTypeConstants.PERFORM_OF_ACTIVITY.getTitle(), res.toString());
            }
            res=node.getAction(NodeEventTypeConstants.AFTER_PERFORM_OF_ACTIVITY);
            if(res!=null) checkExpr(node.getName()+"|"+NodeEventTypeConstants.AFTER_PERFORM_OF_ACTIVITY.getTitle(),vars,res.toString());
        }else if(node instanceof SubProcessStateNode){
            Object res=node.getProperty(NodePropertyConstants.PROCESS);
            if(res!=null && !(res instanceof KrnObjectItem)) checkExpr(node.getName()+"|"+NodePropertyConstants.PROCESS.getTitle(),vars,res.toString());
            res=node.getAction(NodeEventTypeConstants.SUB_PROCESS_INSTANCE_START);
            if(res!=null) checkExpr(node.getName()+"|"+NodeEventTypeConstants.SUB_PROCESS_INSTANCE_START.getTitle(),vars,res.toString());
            res=node.getAction(NodeEventTypeConstants.SUB_PROCESS_INSTANCE_COMPLETION);
            if(res!=null) checkExpr(node.getName()+"|"+NodeEventTypeConstants.SUB_PROCESS_INSTANCE_COMPLETION.getTitle(),vars,res.toString());
        }else if(node instanceof DecisionStateNode){
            Object res=node.getAction(NodeEventTypeConstants.BEFORE_DECISION);
            if(res!=null) {
            	checkExpr(node.getName()+"|"+NodeEventTypeConstants.BEFORE_DECISION.getTitle(),vars,res.toString());
            	checkEventExpr(node.getName()+"|"+NodeEventTypeConstants.BEFORE_DECISION.getTitle(), res.toString());
            }
            res=node.getAction(NodeEventTypeConstants.AFTER_DECISION);
            if(res!=null) {
            	checkExpr(node.getName()+"|"+NodeEventTypeConstants.AFTER_DECISION.getTitle(),vars,res.toString());
            	checkEventExpr(node.getName()+"|"+NodeEventTypeConstants.AFTER_DECISION.getTitle(), res.toString());
            }
        }else if(node instanceof ForkNode){
            Object res=node.getAction(NodeEventTypeConstants.FORK);
            if(res!=null) checkExpr(node.getName()+"|"+NodeEventTypeConstants.FORK.getTitle(),vars,res.toString());
        }else if(node instanceof JoinNode){
            Object res=node.getAction(NodeEventTypeConstants.JOIN);
            if(res!=null) checkExpr(node.getName()+"|"+NodeEventTypeConstants.JOIN.getTitle(),vars,res.toString());
        }else if(node instanceof InBoxStateNode){
            Object res=node.getProperty(NodePropertyConstants.EXCH_BOX);
            if(res!=null && !(res instanceof KrnObjectItem)) checkExpr(node.getName()+"|"+NodePropertyConstants.EXCH_BOX.getTitle(),vars,res.toString());
            res=node.getAction(NodeEventTypeConstants.CHECK_XML);
            if(res!=null) checkExpr(node.getName()+"|"+NodeEventTypeConstants.CHECK_XML.getTitle(),vars,res.toString());
            res=node.getProperty(NodePropertyConstants.NODE_TITLE);
            if(res!=null) checkExpr(node.getName()+"|"+NodePropertyConstants.NODE_TITLE.getTitle(),vars,res.toString());
            res=node.getProperty(NodePropertyConstants.ASSIGNMENT);
            if(res!=null && !(res instanceof KrnObjectItem)) checkExpr(node.getName()+"|"+NodePropertyConstants.ASSIGNMENT.getTitle(),vars,res.toString());
            res=node.getProperty(NodePropertyConstants.DATE_ALARM);
            if(res!=null) checkExpr(node.getName()+"|"+NodePropertyConstants.DATE_ALARM.getTitle(),vars,res.toString());
            res=node.getProperty(NodePropertyConstants.DATE_ALERT);
            if(res!=null) checkExpr(node.getName()+"|"+NodePropertyConstants.DATE_ALERT.getTitle(),vars,res.toString());
            res=node.getAction(NodeEventTypeConstants.PARS_XML);
            if(res!=null) checkExpr(node.getName()+"|"+NodeEventTypeConstants.PARS_XML.getTitle(),vars,res.toString());
        }else if(node instanceof OutBoxStateNode){
            Object res=node.getProperty(NodePropertyConstants.EXCH_BOX);
            if(res!=null && !(res instanceof KrnObjectItem)) checkExpr(node.getName()+"|"+NodePropertyConstants.EXCH_BOX.getTitle(),vars,res.toString());
            res=node.getProperty(NodePropertyConstants.NODE_TITLE);
            if(res!=null) checkExpr(node.getName()+"|"+NodePropertyConstants.NODE_TITLE.getTitle(),vars,res.toString());
            res=node.getProperty(NodePropertyConstants.ASSIGNMENT);
            if(res!=null && !(res instanceof KrnObjectItem)) checkExpr(node.getName()+"|"+NodePropertyConstants.ASSIGNMENT.getTitle(),vars,res.toString());
            res=node.getAction(NodeEventTypeConstants.PERFORM_XML);
            if(res!=null) checkExpr(node.getName()+"|"+NodeEventTypeConstants.PERFORM_XML.getTitle(),vars,res.toString());
        }else if(node instanceof EndStateNode){
            Object res=processNode.getAction(NodeEventTypeConstants.PROCESS_INSTANCE_END);
            if(res!=null) checkExpr(processNode.getName()+"|"+NodeEventTypeConstants.PROCESS_INSTANCE_END.getTitle(),vars,res.toString());
            res=processNode.getAction(NodeEventTypeConstants.PROCESS_INSTANCE_CANCEL);
            if(res!=null) checkExpr(processNode.getName()+"|"+NodeEventTypeConstants.PROCESS_INSTANCE_CANCEL.getTitle(),vars,res.toString());
        }
    }
    private void checkExpr(String nodeName,Set<String> vars,String expr){
//        clientOrlang.check("process:"+processNode.getName()+" node:"+nodeName,expr,vars,errors_);
        exprDebuger.debugExpression(nodeName,expr,vars);
    }
    
    private void checkEventExpr(String nodeName, String expr) {
    	String[] methods = {".setAttr(", ".deleteAttr(", ".createObject(", ".delete(", ".lock(", ".unlock("};
    	String[] lines = expr.split("\n");
    	for(String method:methods) {
    		for(int i=0;i<lines.length;i++) {
    			if(!lines[i].startsWith("//"))
    			if(lines[i].contains(method)) {
    				int j = i+1;
    				String m = method.substring(1);
    				String err = nodeName + "(" + j + "): метод " + m + ") не применим в данном событии!";
    				if(!eventErrors.contains(err))
    				eventErrors.add(err);
    			}
    		}
    	}
    }
    public Vector<String> getError(){
        return errors_;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if ("bounds".equals(evt.getPropertyName())) {
            Rectangle boundsOld = (Rectangle)evt.getOldValue();
            Rectangle boundsNew = (Rectangle)evt.getNewValue();
            if ((boundsOld.x != 0 && boundsOld.y != 0) &&
                    (!boundsOld.equals(boundsNew))) {
                if (mf != null) {
                	if (!mf.getUndoRedoCall(obj)) {
	                    mf.changeNodeLocation(obj, (StateNode)((FigNamedNode) evt.getSource()).getOwner(), boundsOld, boundsNew);
                	}
                    mf.setProcessModified(true);
                }
            }
        }
    }

    public void paintTrace(long[][] nodes){
            if (selectNode.size()>0){
                for (Object aSelectNode : selectNode) {
                    StateNode node = (StateNode) aSelectNode;
                    if (!(node instanceof ForkNode || node instanceof StartSyncNode || node instanceof EndSyncNode || node instanceof JoinNode || node instanceof StartStateNode || node instanceof EndStateNode))
                    {
                        node.getPresentation().setFillColor(node instanceof NoteStateNode ? Utils.getLightYellowColor() : Utils.getLightSysColor());
                        ((FigNamedNode) node.getPresentation()).getNameFig().setLineColor(node instanceof NoteStateNode ? Utils.getLightYellowColor() : Utils.getLightSysColor());
                    } else
                        node.getPresentation().setFillColor(Utils.getDarkShadowSysColor());
                }
                for (Object aSelectEdge : selectEdge) {
                    FigTransitionEdge edge = (FigTransitionEdge) aSelectEdge;
                    edge.setLineColor(Color.black);
                    edge.getDestArrowHead().setFillColor(Color.black);
                }
                for (Object aSelectLine : selectLine) {
                    FigLineEdge edge = (FigLineEdge) aSelectLine;
                    edge.setLineColor(Color.black);
                }
            }
            selectNode.clear();
            selectEdge.clear();
            selectLine.clear();
            for(int i=0,k=0;i<nodes.length;++i){
                for (int j = 0; j < nodes[i].length; ++j) {
                    StateNode node = nodeMap.get(""+nodes[i][j]);
                    if(node!=null){
	                    if(i==0 && j==nodes[i].length-1){
	                        node.getPresentation().setFillColor(Utils.getLightRedColor());
	                        if(!(node instanceof ForkNode || node instanceof JoinNode || node instanceof StartSyncNode || node instanceof EndSyncNode || node instanceof StartStateNode|| node instanceof EndStateNode))
	                        ((FigNamedNode)node.getPresentation()).getNameFig().setLineColor(Utils.getLightRedColor());
	                    }else{
	                        node.getPresentation().setFillColor(Utils.getLightGreenColor());
	                        if(!(node instanceof ForkNode || node instanceof StartSyncNode || node instanceof EndSyncNode || node instanceof JoinNode || node instanceof StartStateNode|| node instanceof EndStateNode))
	                        ((FigNamedNode)node.getPresentation()).getNameFig().setLineColor(Utils.getLightGreenColor());
	                    }
	                    selectNode.add(node);
                    }
                    k++;
                 }
                if(selectNode.size()>0){
                 for (int j = k-nodes[i].length; j < k-1; ++j) {
                     if (selectNode.size() <= j+1) continue; 
                    List edges_= selectNode.get(j).getPresentation().getFigEdges();
                     for (Object anEdges_ : edges_) {
                         FigTransitionEdge edge_ = (FigTransitionEdge) anEdges_;
                         if (((StateNode) edge_.getSourceFigNode().getOwner()).getId().equals(selectNode.get(j).getId()) 
                        		 && ((StateNode) edge_.getDestFigNode().getOwner()).getId().equals(selectNode.get(j + 1).getId()))
                         {
                             edge_.setLineColor(Utils.getLightGreenColor().darker());
                             edge_.getDestArrowHead().setFillColor(Utils.getLightGreenColor().darker());
                             String expr_sel = (String) ((TransitionEdge) edge_.getOwner()).getProperty(NodePropertyConstants.EDGE_JOIN);
                             if (expr_sel != null && !expr_sel.equals("")) {
                                 StringTokenizer st = new StringTokenizer(expr_sel, ";", false);
                                 while (st.hasMoreElements()) {
                                     String el = (String) st.nextElement();
                                     if (el.substring(0, el.indexOf("=")).equals("node")) {
                                         StateNode node = nodeMap.get(el.substring(el.indexOf("=") + 1));
                                         if (node != null) {
                                             node.getPresentation().setFillColor(Utils.getLightGreenColor().brighter());
                                             ((FigNamedNode) node.getPresentation()).getNameFig().setLineColor(Utils.getLightGreenColor().brighter());
                                             selectNode.add(node);
                                         }
                                     } else if (el.substring(0, el.indexOf("=")).equals("line")) {
                                         FigLineEdge line = (FigLineEdge) lines.get(el.substring(el.indexOf("=") + 1));
                                         if (line != null) {
                                             line.setLineColor(Utils.getLightGreenColor().darker());
                                             selectLine.add(line);
                                         }
                                     }
                                 }
                             }
                             selectEdge.add(edge_);
                         }
                     }
                 }
                }
            }

    }
    public String getScriptOfNodes(Fig fig,long processId){
        String res_expr="";
        Vector<StateNode> figs = null;
        if(fig==null){
            StateNode node=getProcess();
            figs=new Vector<StateNode>(nodeMap.size()+1);
            figs.add(node);
            node=getStartNode();
            if(node!=null) figs.add(node);
            for (StateNode stateNode : nodeMap.values()) {
                node = stateNode;
                if (!(node instanceof StartStateNode || node instanceof EndStateNode)) figs.add(node);
            }
            node=getEndNode();
            if(node!=null) figs.add(node);
            res_expr+="\n"+"processId="+processId+"\n";
        }else if(fig.getOwner() instanceof StateNode){
            figs=new Vector<StateNode>();
            figs.add((StateNode)fig.getOwner());
        }
        assert figs != null;
        for (StateNode node : figs) {
            if (!(node instanceof NoteStateNode)) {
                res_expr += (!res_expr.equals("") ? "\n" : "") + "$" + node.getType() + ". " + node.getName() + "(" + node.getId() + ")" + ":{\n";
                Map map = node.getPropertyMap();
                NodeProperty [] props = node.getProperties();
                res_expr += "  $Свойства:";
                for (NodeProperty prop : props) {
                    String expr = null;
                    Object expr_obj = map.get(prop);
                    if(expr_obj instanceof String){
                        expr = (String) map.get(prop);
                    }else if(expr_obj instanceof KrnObjectItem){
                          expr=((KrnObjectItem)expr_obj).title+":"+((KrnObjectItem)expr_obj).obj.uid;
                    }
                    if (expr != null) {
                        StringBuffer sb = new StringBuffer(expr);
                        int index = 0;
                        sb.insert(index++, "        ");
                        while (index > 0) {
                            index = sb.indexOf("\n", index);
                            if (index > 0)
                                sb.insert(++index, "        ");
                        }
                        expr = sb.toString();
                        res_expr += "\n    " + prop.getTitle() + "{\n" + expr + "\n    }";
                    }
                }
                map = node.getActionMap();
                NodeEventType [] events = node.getEventTypes();
                res_expr += "\n" + "  $События:";
                for (NodeEventType event : events) {
                    String expr = (String) map.get(event);
                    if (expr != null) {
                        StringBuffer sb = new StringBuffer(expr);
                        int index = 0;
                        sb.insert(index++, "        ");
                        while (index > 0) {
                            index = sb.indexOf("\n", index);
                            if (index > 0)
                                sb.insert(++index, "        ");
                        }
                        expr = sb.toString();
                        res_expr += "\n    " + event.getTitle() + "{\n" + expr + "\n    }";
                    }
                }
                res_expr += "\n}";
            }
        }
        return res_expr;

    }
    public void resaveStrings(){
        try {
            Kernel krn=Kernel.instance();
            final KrnClass cls = krn.getClassByName("ProcessDef");
            List ls = LangItem.getAll();
            long[] langs = new long[ls.size()];
            for (int i=0; i< ls.size(); i++) {
                langs[i] = ((LangItem)ls.get(i)).obj.id;
            }
            //long[] langs=new long[]{122,123};
            KrnObject[] objs=krn.getClassObjects(cls,0);
            for (KrnObject obj1 : objs) {
                for (long lang1 : langs) {
                    Map<String,String> str = new HashMap<String,String>();
                    byte[] msg = Kernel.instance().getBlob(obj1, "message", 0, lang1, 0);
                    if (msg.length > 0) {
                        SAXBuilder builder = new SAXBuilder();
                        builder.setValidation(false);
                        Element xml = null;
                        try {
                            xml = builder.build(new ByteArrayInputStream(msg)).getRootElement();
                        } catch (JDOMException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        } catch (IOException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }
                        //messages
                        List msgs = xml.getChildren("msg");
                        for (Object msg1 : msgs) {
                            Element e = (Element) msg1;
                            String uid = e.getAttribute("uid").getValue();
                            String value = e.getText();
                            str.put(uid, value);
                        }
                        //
                        if (str != null && str.size() > 0) {
                            Element root = new Element("message");
                            for (Object o : str.keySet()) {
                                String uid_ = (String) o;
                                String value = str.get(uid_);
                                xml = new Element("msg");
                                xml.setAttribute("uid", uid_);
                                xml.setText(value);
                                root.addContent(xml);
                            }
                            ByteArrayOutputStream os_msg = new ByteArrayOutputStream();
                            XMLOutputter out = new XMLOutputter();
                            out.getFormat().setEncoding("UTF-8");
                            try {
                                out.output(root, os_msg);
                            } catch (IOException e) {
                                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            }
                            krn.setBlob(obj1.id, obj1.classId, "message", 0,
                                    os_msg.toByteArray(), lang1, 0);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public boolean setLang(long langId,JGraph graph){
        if(lang==langId) return false;
        if(langMap.get(langId)==null){
            try{
                byte[] msg=Kernel.instance().getBlob(obj,"message",0,langId,0);
                if(msg.length>0){
                    SAXBuilder builder = new SAXBuilder();
                    builder.setValidation(false);
                    Element xml = builder.build(new ByteArrayInputStream(msg)).getRootElement();
                    //messages
                      List msgs=xml.getChildren("msg");
                    for (Object msg1 : msgs) {
                        Element e = (Element) msg1;
                        String uid = e.getAttribute("uid").getValue();
                        String value = e.getText();
                        langMap.put(langId, uid, value);
                    }
                    //
                }

            }catch(KrnException e){
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JDOMException e) {
                e.printStackTrace();
            }

        }
/*        for(Iterator it=nodeMap.values().iterator();it.hasNext();){
            StateNode node=(StateNode)it.next();
            String property_expr=(String)langMap.get(new Integer(lang),"name_"+node.getId());
            node.setProperty(NodeProperty.forName("name"),property_expr);
            node.setName(property_expr);
        }
*/        Collection figs_=graph.getEditor().getLayerManager().getContents(new ArrayList());
        int i=1;
        for (Object aFigs_ : figs_) {
            Fig f = (Fig) aFigs_;
            if (f instanceof FigNamedNode) {
                StateNode node = (StateNode) f.getOwner();
                String property_expr = langMap.get(langId, "name_" + node.getId());
                node.setProperty(NodeProperty.forName("name"), property_expr);
                node.setName(property_expr == null ? "*" : property_expr);
                property_expr = langMap.get(langId, "desc_" + node.getId());
                node.setProperty(NodeProperty.forName("description"), property_expr);
                node.setDescription(property_expr == null ? "" : property_expr);
            } else if (f instanceof FigEdge) {
                TransitionEdge edge = (TransitionEdge) f.getOwner();
                String name = langMap.get(langId, "edge_" + edge.getId());
                edge.setName(name);
            } else if (f instanceof FigText) {
                String property_expr = langMap.get(langId, "text_" + i);
                ((FigText) f).setText(property_expr == null ? "*" : property_expr);
            }
        }
        String property_expr = langMap.get(langId, "process_0");
        processNode.setProperty(NodeProperty.forName("name"), property_expr == null ? "*" : property_expr);
        property_expr = langMap.get(langId, "process_desc_0");
        processNode.setProperty(NodeProperty.forName("description"), property_expr == null ? "" : property_expr);
        lang=langId;
        return true;
    }
    
    public Map<String,String> getStrings(long langId) {
        Map<String,String> str = stringsMap.get(langId);
        if(str==null){
            str=new HashMap<String,String>();
            try{
                byte[] msg=Kernel.instance().getBlob(obj,"strings",0,langId,0);
                if(msg.length>0){
                    SAXBuilder builder = new SAXBuilder();
                    builder.setValidation(false);
                    Element xml = builder.build(new ByteArrayInputStream(msg)).getRootElement();
                    //messages
                      List msgs=xml.getChildren("strings");
                    for (Object msg1 : msgs) {
                        Element e = (Element) msg1;
                        String uid = e.getAttribute("uid").getValue();
                        String value = e.getAttribute("value").getValue();
                        //noinspection unchecked
                        str.put(uid, value);
                    }
                    //
                }

            }catch(KrnException e){
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JDOMException e) {
                e.printStackTrace();
            }
            stringsMap.put(lang, str);
        }
        return str;
    }

    public long getDefaultLangId() {
        return lang;
    }

    public void save() throws Exception {
    }
    public ProcessStateNode getProcess(){
        return processNode;
    }
    public void setEdgeName(TransitionEdge edge,String name,long lang){
        langMap.put(lang,"edge_"+edge.getId(),name);
    }
    public void setEdgeName(TransitionEdge edge,String name){
        langMap.put(lang,"edge_"+edge.getId(),name);
    }
    public String getEdgeName(long edgeId,long lang){
        return langMap.get(lang, "edge_"+edgeId);
    }
    public String getEdgeName(long edgeId){
        return langMap.get(lang,"edge_"+edgeId);
    }
    public void setNodeName(StateNode node,String name){
        if(node instanceof ProcessStateNode)
            langMap.put(lang,"process_0",name);
        else
            langMap.put(lang,"name_"+node.getId(),name);
    }
    public void setNodeName(StateNode node,String name,long lang){
        if(node instanceof ProcessStateNode)
            langMap.put(lang,"process_0",name);
        else
            langMap.put(lang,"name_"+node.getId(),name);
    }
    public String getNodeName(long nodeId){
        return langMap.get(lang,"name_"+nodeId);
    }
    public String getNodeName(long nodeId,long lang){
        return langMap.get(lang,"name_"+nodeId);
    }

    public void setNodeDescription(StateNode node,String name){
        setNodeDescription(node, name, lang);
    }
    public void setNodeDescription(StateNode node,String name,long lang){
        if(node instanceof ProcessStateNode)
            langMap.put(lang,"process_desc_0",name);
        else
            langMap.put(lang,"desc_"+node.getId(),name);
    }
    public String getNodeDescription(long nodeId){
        return getNodeDescription(nodeId, lang);
    }
    public String getNodeDescription(long nodeId,long lang){
        return langMap.get(lang,"desc_"+nodeId);
    }
    public boolean openProcess(SubProcessStateNode node) {
        boolean result = false;
        try{
            if (mf != null) {
                Object prop = node.getProperty(NodePropertyConstants.PROCESS);
                Object res = null;
                String title = "";
                if (prop instanceof KrnObjectItem) {
                    res = ((KrnObjectItem) prop).obj;
                    ServiceNode fnode = mf.findServiceNode(((KrnObjectItem) prop).obj);
                	title = fnode == null ? ((KrnObjectItem) prop).title : ((ServiceNode) fnode).getTitle();
                }
                if (res instanceof KrnObject) {
                    mf.open((KrnObject) res, title, null);
                    result = true;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }
    public boolean isInBox() {
    	return isInbox;
    }
}
