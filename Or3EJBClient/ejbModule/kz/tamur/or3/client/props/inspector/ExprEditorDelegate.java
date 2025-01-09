package kz.tamur.or3.client.props.inspector;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.tigris.gef.base.SelectionReshape;
import org.tigris.gef.base.SelectionResize;
import org.tigris.gef.graph.presentation.JGraph;

import com.cifs.or2.client.util.CnrBuilder;

import kz.tamur.Or3Frame;
import kz.tamur.comps.GuiComponentItem;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.Utils;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.InterfaceFrame;
import kz.tamur.guidesigner.expr.EditorWindow;
import kz.tamur.guidesigner.service.Document;
import kz.tamur.guidesigner.service.NodeEventType;
import kz.tamur.guidesigner.service.NodeProperty;
import kz.tamur.guidesigner.service.NodePropertyConstants;
import kz.tamur.guidesigner.service.ServiceItem;
import kz.tamur.guidesigner.service.ServiceModel;
import kz.tamur.guidesigner.service.fig.FigLineEdge;
import kz.tamur.guidesigner.service.ui.ActivityStateNode;
import kz.tamur.guidesigner.service.ui.DecisionStateNode;
import kz.tamur.guidesigner.service.ui.EndStateNode;
import kz.tamur.guidesigner.service.ui.EndSyncNode;
import kz.tamur.guidesigner.service.ui.ForkNode;
import kz.tamur.guidesigner.service.ui.InBoxStateNode;
import kz.tamur.guidesigner.service.ui.JoinNode;
import kz.tamur.guidesigner.service.ui.NoteStateNode;
import kz.tamur.guidesigner.service.ui.OutBoxStateNode;
import kz.tamur.guidesigner.service.ui.ReportStateNode;
import kz.tamur.guidesigner.service.ui.ServiceNodeIfc;
import kz.tamur.guidesigner.service.ui.StartStateNode;
import kz.tamur.guidesigner.service.ui.StartSyncNode;
import kz.tamur.guidesigner.service.ui.StateNode;
import kz.tamur.guidesigner.service.ui.SubProcessStateNode;
import kz.tamur.guidesigner.service.ui.TransitionEdge;
import kz.tamur.or3.client.props.Expression;
import kz.tamur.or3.client.props.Inspectable;
import kz.tamur.or3.client.props.Property;
import kz.tamur.util.ExpressionEditor;

public class ExprEditorDelegate extends JPanel implements EditorDelegate, RendererDelegate, ActionListener, EditorDelegateSet {

    private Expression value;
    // StateNode and (PropertyNode or EventNode) name of the PropertyTable Where the ExprEditorDelegate located;  
    private String state_prop;
    
    private String stringValue = null;

	private PropertyEditor editor;

    private JTextField label;
    private JButton exprBtn;
    private String[] CheckExprs = {"Действие: Перемещение", "Условие: Перед выбором", "Условие: После выбора", "Процесс: Начало процесса"};
    long langId = com.cifs.or2.client.Utils.getInterfaceLangId();
    private String id = null;
    
    private Property prop = null;

    public PropertyEditor getEditor() {
    	return editor;
    }
    
    public ExprEditorDelegate(JTable table, String id) {
        this.id = id;
        setLayout(new GridBagLayout());
        label = Utils.createEditor(this, table.getFont());
        exprBtn = Utils.createBtnEditor(this);
        add(label, new CnrBuilder().x(1).wtx(1).fill(GridBagConstraints.HORIZONTAL).build());
        add(exprBtn, new CnrBuilder().x(0).build());
    }
    
    public ExprEditorDelegate(JTable table, String id, Property prop) {
        this.id = id;
        setLayout(new GridBagLayout());
        label = Utils.createEditor(this, table.getFont());
        exprBtn = Utils.createBtnEditor(this);
        add(label, new CnrBuilder().x(1).wtx(1).fill(GridBagConstraints.HORIZONTAL).build());
        add(exprBtn, new CnrBuilder().x(0).build());
        this.prop = prop;
    }

    public int getClickCountToStart() {
        return 1;
    }

    public Component getEditorComponent() {
        return this;
    }
    
    public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

    public Object getValue() {
        return value;
    }

    public void setValue(Object val) {
    	Object value;
    	if(val instanceof ExprEditorObject) {
    		value = ((ExprEditorObject)val).getObject();
    		state_prop = ((ExprEditorObject)val).getString();
    	} else {
    		value = val;
    		state_prop = null;
    	}
        if (value != null && !"".equals(value)) {
        		Expression expr = (Expression) value;
        		this.value = expr;
        		label.setText(expr.text);
        } else {
        	this.value = null;
        	label.setText("");
        }
    }

    public Component getRendererComponent() {
        return this;
    }

    public void setPropertyEditor(PropertyEditor editor) {
        this.editor = editor;

    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == exprBtn) {
            Inspectable ins = editor.getObject();
            if (ins instanceof ServiceItem && ins.getProperties().getChild("Свойства") != null
                    && ins.getProperties().getChild("Свойства").getChild("join") != null
                    && NodePropertyConstants.EDGE_JOIN.getName().endsWith(id)) {

                Document doc = ((ServiceItem) ins).getMainFrame().getSelectedDocument();
                if (doc == null) {
                    return;
                }
                Vector<String> nods = new Vector<String>();
                Vector<String> lns = new Vector<String>();
                Vector<Object> sel = new Vector<Object>();
                String expr_sel = value.text;
                stringValue = expr_sel;
                if (expr_sel != null && !expr_sel.equals("")) {
                    StringTokenizer st = new StringTokenizer(expr_sel, ";", false);
                    while (st.hasMoreElements()) {
                        String el = (String) st.nextElement();
                        if (el.substring(0, el.indexOf("=")).equals("node")) {
                            nods.add(el.substring(el.indexOf("=") + 1));
                        } else if (el.substring(0, el.indexOf("=")).equals("line")) {
                            lns.add(el.substring(el.indexOf("=") + 1));
                        }
                    }
                }
                Enumeration enum1 = doc.getGraph().getEditor().getLayerManager().elements();
                ServiceModel model = (ServiceModel) doc.getGraph().getEditor().getGraphModel();
                ServiceModel model_ = new ServiceModel(true, doc.getKrnObject(), langId);
                JGraph graph_ = new JGraph(model_);
                java.util.List edges = model.getEdges();
                java.util.List nodes = model.getNodes();
                HashMap<String, StateNode> nodeMap = new HashMap<String, StateNode>();
                for (Object node : nodes) {
                    String idNode = ((StateNode) node).getId();
                    String classNode = node.getClass().getName();
                    classNode = classNode.substring(classNode.lastIndexOf(".") + 1);
                    StateNode stateNode = null;
                    if (classNode.equals("StartStateNode")) {
                        stateNode = new StartStateNode(model);
                    } else if (classNode.equals("StartSyncNode")) {
                        stateNode = new StartSyncNode(model);
                    } else if (classNode.equals("EndSyncNode")) {
                        stateNode = new EndSyncNode(model);
                    } else if (classNode.equals("EndStateNode")) {
                        stateNode = new EndStateNode(model);
                    } else if (classNode.equals("ActivityStateNode")) {
                        stateNode = new ActivityStateNode(model);
                    } else if (classNode.equals("InBoxStateNode")) {
                        stateNode = new InBoxStateNode(model);
                    } else if (classNode.equals("OutBoxStateNode")) {
                        stateNode = new OutBoxStateNode(model);
                    } else if (classNode.equals("SubProcessStateNode")) {
                        stateNode = new SubProcessStateNode(model);
                    } else if (classNode.equals("DecisionStateNode")) {
                        stateNode = new DecisionStateNode(model);
                    } else if (classNode.equals("ForkNode")) {
                        stateNode = new ForkNode(model);
                    } else if (classNode.equals("JoinNode")) {
                        stateNode = new JoinNode(model);
                    } else if (classNode.equals("NoteStateNode")) {
                        stateNode = new NoteStateNode(model);
                    } else if (classNode.equals("ReportStateNode")) {
                        stateNode = new ReportStateNode(model);
                    }
                    if (stateNode != null) {
                        stateNode.initialize(null);
                        model_.addNode(stateNode);
                        stateNode.getPresentation().setBounds(((StateNode) node).getPresentation().getBounds());
                        stateNode.setName(((StateNode) node).getName());
                        nodeMap.put(idNode, stateNode);
                        if (nods.contains(idNode))
                            sel.add(stateNode.getPresentation());
                    }
                }
                // edges
                for (Object edge1 : edges) {
                    TransitionEdge edge_ = (TransitionEdge) edge1;
                    TransitionEdge edge = new TransitionEdge(model);
                    String src_p_id = ((TransitionEdge) edge1).getSourcePort().getParentNode().getId();
                    String dst_p_id = ((TransitionEdge) edge1).getDestPort().getParentNode().getId();
                    edge.setName(((TransitionEdge) edge1).getName());
                    edge.connect(model_, nodeMap.get(src_p_id).getPort(0), nodeMap.get(dst_p_id).getPort(0));
                    model_.addEdge(edge);
                    edge.setPoints(((TransitionEdge) edge1).getPoints());
                    if (edge_ == ((ServiceItem) ins).getItem()) {
                        edge.getPresentation().setLineColor(Color.red);
                        edge.getPresentation().getDestArrowHead().setFillColor(Color.red);
                    }
                }
                while (enum1.hasMoreElements()) {
                    Object o = enum1.nextElement();
                    if (o instanceof FigLineEdge) {
                        FigLineEdge f_l = (FigLineEdge) o;
                        String id_str = f_l.getId();
                        Point[] pts = f_l.getPoints();
                        FigLineEdge f = new FigLineEdge(pts[0].x, pts[0].y, pts[1].x, pts[1].y, true);
                        f.setId(id_str);
                        graph_.getEditor().getLayerManager().add(f);
                        if (lns.contains(id_str))
                            sel.add(f_l);
                    }
                }
                if (sel.size() > 0)
                    graph_.getEditor().getSelectionManager().select(sel);
                graph_.setPreferredSize(new Dimension(700, 600));
                DesignerDialog dlg = new DesignerDialog((Frame) exprBtn.getTopLevelAncestor(),
                        "Выберите узлы и линии связанные с перемещением", graph_);
                dlg.show();
                if (dlg.isOK()) {
                    Vector sel_ = graph_.getEditor().getSelectionManager().selections();
                    boolean par = false;
                    String val = "";
                    if (sel_.size() > 0) {
                        for (Object aSel : sel_) {
                            if (aSel instanceof SelectionResize) {
                                Object sl = ((SelectionResize) aSel).getContent().getOwner();
                                if (sl instanceof NoteStateNode) {
                                    val += (par ? ";" : "") + "node=" + ((NoteStateNode) sl).getId();
                                    if (!par)
                                        par = true;
                                }
                            } else if (aSel instanceof SelectionReshape) {
                                Object sl = ((SelectionReshape) aSel).getContent();
                                if (sl instanceof FigLineEdge) {
                                    val += (par ? ";" : "") + "line=" + ((FigLineEdge) sl).getId();
                                    if (!par)
                                        par = true;
                                }
                            }
                        }
                    }
                    value = new Expression(val);
                }
                editor.stopCellEditing();
            } else {
            	
            	StringBuilder tabId = new StringBuilder();
            	StringBuilder tabTitle = new StringBuilder();
            	Property prop = null;
            	OrGuiComponent comp = null;            	
            	PropertyNode node = null;  
            	Object stNode = null;
            	NodeProperty nodeProp = null;
            	NodeEventType nodeEvent = null;
            	Document doc = null;
            	InterfaceFrame ifc = null;
            	if(ins instanceof GuiComponentItem ) {
            		ifc = ((GuiComponentItem) ins).getDesignerFrame().tabbedContent.getSelectedFrame();
            		String uid = ifc.getUiObject().uid;
            		String title = ((GuiComponentItem) ins).getDesignerFrame().tabbedContent.getSelectedFrame().getTitle();
            		comp = ((OrGuiComponent)((GuiComponentItem) ins).getItem());
            		String uuid = comp.getUUID();
            		String compTitle = comp.getPropertyValue(comp.getProperties().getChild("title")).toString();
            		prop = this.prop != null? this.prop: ((PropertyTable) editor.getTable()).getPropertyTableModel().getSelectedNode(editor.getTable().getSelectedRow()-1);
            		String prid = prop.getId();
            		node = prop.getNode();
            		String propString =prop.toString();
            		tabTitle.append("Interface/").append(title + "/").append((compTitle == null || compTitle.equals(""))?"":compTitle+"/").append(propString);
            		tabId.append(uid).append(uuid).append(prid);
            	}
            	
            	if(ins instanceof ServiceItem) {
            		doc = ((ServiceItem) ins).getMainFrame().getTabbedContent().getSelectedDocument();
            		String uid = doc.getKrnObject().uid;
            		String title = ((ServiceItem) ins).getMainFrame().getTabbedContent().getSelectedDocument().getTitle();
            		stNode = ((ServiceItem) ins).getItem();
            		String id = (stNode instanceof ServiceNodeIfc)? ((ServiceNodeIfc) stNode).getId(): null;
            		String compTitle = (stNode instanceof ServiceNodeIfc)? (((ServiceNodeIfc) stNode).getName() != null)? ((ServiceNodeIfc) stNode).getName() : id : null;
            		prop = this.prop != null? this.prop: ((PropertyTable) editor.getTable()).getPropertyTableModel().getSelectedNode(editor.getTable().getSelectedRow()-1);
            		String propId = prop.getId();
            		nodeProp = NodeProperty.forName(propId);
            		nodeEvent = NodeEventType.forName(propId);
            		String propString =prop.toString();
            		tabTitle.append("Process/").append(title + "/").append(compTitle.equals("")?"":compTitle+"/").append(propString);
            		tabId.append(uid).append(id).append(propString);
            		
            	}
            	final ExpressionEditor exprEditor = (comp!=null)?new ExpressionEditor(value != null ? value.text : "", ExprEditorDelegate.this,ins, prop, comp, node, ifc):
            		(stNode!=null)? new ExpressionEditor(value != null ? value.text : "", ExprEditorDelegate.this, ins, prop, stNode, nodeProp, nodeEvent, doc):null;
            	exprEditor.setCheck(false);
            	if(state_prop != null)
            		for(String str: CheckExprs) {
            			if(state_prop.equals(str)) {
            				exprEditor.setCheck(true);
            			}
            		}
            	exprEditor.setServiceFrm(Or3Frame.instance().getServiceFrame());

            	EditorWindow.addTab(tabId.toString(), tabTitle.toString(), exprEditor);

//                ExpressionEditor exprEditor = new ExpressionEditor(value != null ? value.text : "", ExprEditorDelegate.this);
//                exprEditor.setCheck(false);
//                if(state_prop != null)
//                for(String str: CheckExprs) {
//                	if(state_prop.equals(str)) {
//                		exprEditor.setCheck(true);
//                	}
//                }
//                exprEditor.setServiceFrm(Or3Frame.instance().getServiceFrame());
//                DesignerDialog dlg = new DesignerDialog(Or3Frame.instance(), "Выражение", exprEditor);
//                dlg.setSize(new Dimension(Utils.getMaxWindowSizeActDisplay()));
//                dlg.setLocation(Utils.getCenterLocationPoint(dlg.getSize()));
//                dlg.show();
//                if (dlg.isOK()) {
//                    setExpression(exprEditor.getExpression());
//                } else if (dlg.getResult() == ButtonsFactory.BUTTON_CANCEL) {
//                    editor.cancelCellEditing();
//                }
            }
        }
    }

    public void setExpression(String expression) {
        value = new Expression(expression);
        editor.stopCellEditing();
    }
    
    /**
     * Класс делигат при вызове редактора выражений из таблицы с ошибками
     * 
     * */
   
}
