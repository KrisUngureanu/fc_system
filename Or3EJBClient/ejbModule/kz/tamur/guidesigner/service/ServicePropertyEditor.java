package kz.tamur.guidesigner.service;

import kz.tamur.rt.Utils;
import kz.tamur.util.Funcs;
import kz.tamur.guidesigner.service.fig.FigTransitionEdge;
import kz.tamur.guidesigner.service.ui.*;
import kz.tamur.or3.client.props.InspectorOwner;
import kz.tamur.or3.client.props.inspector.PropertyInspector;
import org.tigris.gef.base.Editor;
import org.tigris.gef.event.GraphSelectionEvent;
import org.tigris.gef.event.GraphSelectionListener;
import org.tigris.gef.graph.presentation.JGraph;
import org.tigris.gef.presentation.FigNode;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;


/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 09.09.2004
 * Time: 17:24:14
 * To change this template use File | Settings | File Templates.
 */
public class ServicePropertyEditor extends JPanel
        implements GraphSelectionListener, DocumentListener,
        PropertyChangeListener, KeyListener , InspectorOwner {

    private StateNode currNode;
    private TransitionEdge currEdge;

    private MainFrame frm;
    
    private String oldNameEditorText = "";
    private String oldComEditorText = "";

    private JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    private JTextArea commentEditor = new JTextArea();
    private javax.swing.text.Document commentDoc = commentEditor.getDocument();

    private JTextField nameEditor = Utils.createDesignerTextField();
    private Document nameDoc = nameEditor.getDocument();
    private PropertyInspector inspector= new PropertyInspector(this);

    public ServicePropertyEditor(MainFrame frm) {
        super(new BorderLayout());
        this.frm=frm;
        nameEditor.setPreferredSize(new Dimension(100, 22));
        nameEditor.setMaximumSize(new Dimension(100, 22));
        nameEditor.setMinimumSize(new Dimension(100, 22));
        nameEditor.addKeyListener(this);
        commentDoc.addDocumentListener(this);
        nameDoc.addDocumentListener(this);
        JPanel p = new JPanel(new GridBagLayout());
        p.add(nameEditor, new GridBagConstraints(0, 0, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(3, 2, 2, 2), 0, 0));
        commentEditor.setLineWrap(true);
        commentEditor.setWrapStyleWord(true);
        commentEditor.setFont(Utils.getDefaultFont());
        commentEditor.setForeground(Utils.getDarkShadowSysColor());
        commentEditor.addKeyListener(this);
        p.add(new JScrollPane(commentEditor), new GridBagConstraints(0, 1, 1, 3, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(3, 2, 2, 2), 0, 0));
        mainSplitPane.setLeftComponent(p);
        mainSplitPane.setRightComponent(inspector);
        add(mainSplitPane, BorderLayout.CENTER);
        mainSplitPane.setDividerLocation(0.5);
        inspector.getDialog("Процессы");
    }

    public void selectionChanged(GraphSelectionEvent event) {
        currNode = null;
        currEdge = null;
        commentEditor.setText("");
        nameEditor.setText("");
        Vector v = event.getSelections();
        if (v.size() > 0) {
            Object o = v.get(0);
            if (o instanceof FigNode) {
                FigNode fn = (FigNode)o;
                Object owner = fn.getOwner();
                if (owner instanceof StateNode) {
                    StateNode sn = (StateNode)owner;
                    if (!(sn instanceof NoteStateNode) &&
                            !(sn instanceof ReportStateNode)) {
                        commentEditor.setText(sn.getDescription());
                        nameEditor.setText(sn.getName());
                    } else {
                        commentEditor.setText(sn.getName());
                        nameEditor.setText("Примечание");
                    }
                    inspector.setObject(new ServiceItem(sn,frm));
                    if (currNode != null) {
                        currNode.removePropertyChangeListener(this);
                    }
                    currNode = sn;
                    currNode.addPropertyChangeListener(this);
                }
            }else if (o instanceof FigTransitionEdge) {
                FigTransitionEdge ft = (FigTransitionEdge)o;
                Object owner = ft.getOwner();
                if (owner instanceof TransitionEdge) {
                    TransitionEdge tr = (TransitionEdge)owner;
                    nameEditor.setText(tr.getName());
                    inspector.setObject(new ServiceItem(tr,frm));
                    if (currEdge != null) {
                        currEdge.removePropertyChangeListener(this);
                    }
                    currEdge = tr;
                    currEdge.addPropertyChangeListener(this);
                }
            }
        }else{
            StateNode sn=((ServiceModel)((Editor)event.getSource()).getGraphModel()).getProcess();
            if(sn!=null){
                commentEditor.setText(sn.getDescription());
                nameEditor.setText(sn.getName());
                inspector.setObject(new ServiceItem(sn,frm));
                if (currNode != null) {
                    currNode.removePropertyChangeListener(this);
                }
                currNode = sn;
                currNode.addPropertyChangeListener(this);
            }
        }
        if(currNode!=null){
        frm.getSelectedNode().setText("Узел:"+currNode.getType()+"."+currNode.getName());
        }else if(currEdge!=null){
            frm.getSelectedNode().setText("Переход:"+currEdge.getName());
        }else
            frm.getSelectedNode().setText("");
    }

    public void changedUpdate(DocumentEvent e) {
        if (currNode != null) {
            Document doc = e.getDocument();
            if (doc == commentDoc) {
                if (!(currNode instanceof NoteStateNode) &&
                        !(currNode instanceof ReportStateNode)) {
                    currNode.setDescription(commentEditor.getText());
                    if(frm.getSelectedDocument()!=null)
                       frm.getSelectedDocument().getModel().setNodeDescription(currNode,commentEditor.getText());
                } else {
                    currNode.setName(Funcs.sanitizeElementName(commentEditor.getText()));
                    if(frm.getSelectedDocument()!=null)
                       frm.getSelectedDocument().getModel().setNodeName(currNode,commentEditor.getText());
                }
            } else if (doc == nameDoc) {
                if (!(currNode instanceof NoteStateNode) &&
                        !(currNode instanceof ReportStateNode)) {
                    currNode.setName(Funcs.sanitizeElementName(nameEditor.getText()));
                    if(frm.getSelectedDocument()!=null)
                       frm.getSelectedDocument().getModel().setNodeName(currNode,nameEditor.getText());
                } else {
                    currNode.setDescription("Примечание");
                    if(frm.getSelectedDocument()!=null)
                       frm.getSelectedDocument().getModel().setNodeDescription(currNode,"Примечание");
                }
            }
        } else if (currEdge != null) {
            Document doc = e.getDocument();
            if (doc == nameDoc) {
                currEdge.setName(nameEditor.getText());
                if(frm.getSelectedDocument()!=null)
                   frm.getSelectedDocument().getModel().setEdgeName(currEdge,nameEditor.getText());
            }
        }
    }

    public void insertUpdate(DocumentEvent e) {
        changedUpdate(e);
    }

    public void removeUpdate(DocumentEvent e) {
        changedUpdate(e);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() == currNode
                && "nodeName".equals(evt.getPropertyName())) {
            if(currNode instanceof NoteStateNode || currNode instanceof ReportStateNode)
                commentEditor.setText((String)evt.getNewValue());
            else
                nameEditor.setText((String)evt.getNewValue());
        }else if (evt.getSource() == currEdge
                && "edgeName".equals(evt.getPropertyName())) {
            nameEditor.setText((String)evt.getNewValue());
        }
    }


    public void keyPressed(KeyEvent e) {
    	oldNameEditorText = nameEditor.getText();
    	oldComEditorText = commentEditor.getText();
    	
    }

    public void keyReleased(KeyEvent e) {
    	String newNameText = nameEditor.getText();
    	String newComText = commentEditor.getText();
    	if(!newNameText.equals(oldNameEditorText) || !newComText.equals(oldComEditorText))
        frm.setProcessModified(true);
    }

    public void keyTyped(KeyEvent e) {
//        frm.setProcessModified(true);
    }

    public void setDeviderLocation(double location) {
        mainSplitPane.setDividerLocation(location);
    }

    public void setEmptyState() {
        nameEditor.setText("");
        commentEditor.setText("");
        inspector.setObject(new ServiceItem(null,frm));
    }
    public void setLang(){
        inspector.setObject(new ServiceItem(currNode,frm));
        if (!(currNode instanceof NoteStateNode) &&
                !(currNode instanceof ReportStateNode)) {
            commentEditor.setText(currNode!=null?currNode.getDescription():"");
            nameEditor.setText(currNode!=null?currNode.getName():currEdge!=null?currEdge.getName():"");
        } else {
            commentEditor.setText(currNode!=null?currNode.getName():"");
            nameEditor.setText(currNode!=null?currNode.getDescription():"");
        }

    }
    public JGraph getGraph(){
        if(!(currNode!=null &&  currNode instanceof DecisionStateNode)) return null;
        kz.tamur.guidesigner.service.Document doc=frm.getSelectedDocument();
        ServiceModel model=(ServiceModel)doc.getGraph().getEditor().getGraphModel();
        ServiceModel model_=new ServiceModel(true,doc.getKrnObject(),model.getDefaultLangId());
        JGraph graph_=new JGraph(model_);
        HashMap nodeMap=new HashMap();
        Collection edges=currNode.getPresentation().getFigEdges();
        StateNode state_node=null;
        for(Iterator it=edges.iterator();it.hasNext();){
            TransitionEdge edge_=(TransitionEdge)((FigTransitionEdge)it.next()).getOwner();
            String dst_p_id = edge_.getDestPort().getParentNode().getId();
            state_node= model.getNodesMap().get(dst_p_id);
            nodeMap.put(dst_p_id,state_node);
        }
        for(Iterator it=nodeMap.values().iterator();it.hasNext();){
            StateNode node= (StateNode)it.next();
            String node_id=node.getId();
            String node_class=node.getClass().getName();
            node_class=node_class.substring(node_class.lastIndexOf(".")+1);
            if(node_class.equals("StartStateNode")){
                state_node=new StartStateNode(node_id,model_);
            }else if(node_class.equals("EndStateNode")){
                state_node=new EndStateNode(node_id,model_);
            }else if(node_class.equals("ActivityStateNode")){
                state_node=new ActivityStateNode(node_id,model_);
            }else if(node_class.equals("InBoxStateNode")){
                state_node=new InBoxStateNode(node_id,model_);
            }else if(node_class.equals("OutBoxStateNode")){
                state_node=new OutBoxStateNode(node_id,model_);
            }else if(node_class.equals("SubProcessStateNode")){
                state_node=new SubProcessStateNode(node_id,model_);
            }else if(node_class.equals("DecisionStateNode")){
                state_node=new DecisionStateNode(node_id,model_);
            }else if(node_class.equals("ForkNode")){
                state_node=new ForkNode(node_id,model_);
            }else if(node_class.equals("JoinNode")){
                state_node=new JoinNode(node_id,model_);
            }else if(node_class.equals("NoteStateNode")){
                state_node=new NoteStateNode(node_id,model_);
            }else if(node_class.equals("ReportStateNode")){
                state_node=new ReportStateNode(node_id,model_);
            }
                state_node.initialize(null);
                model_.addNode(state_node);
                state_node.getPresentation().setBounds(node.getPresentation().getBounds());
                state_node.setName(node.getName());
                nodeMap.put(node_id,state_node);
        }
        //edges
        for(Iterator it=edges.iterator();it.hasNext();){
            TransitionEdge edge_=(TransitionEdge)((FigTransitionEdge)it.next()).getOwner();
            String edge_id=edge_.getId();
            String dst_p_id = edge_.getDestPort().getParentNode().getId();
            if(dst_p_id.equals(currNode.getId())) continue;
            String src_p_id = edge_.getSourcePort().getParentNode().getId();
            TransitionEdge edge=new TransitionEdge(edge_id,model_);
            edge.setName(edge_.getName());
            edge.connect(model_,((StateNode)nodeMap.get(src_p_id)).getPort(0), ((StateNode)nodeMap.get(dst_p_id)).getPort(0));
            model_.addEdge(edge);
            edge.setPoints(edge_.getPoints());
        }
     return graph_;
    }

    public void setInspector(boolean isInspector) {
            mainSplitPane.setRightComponent(inspector);
            mainSplitPane.setDividerLocation(0.5);
    }
    
    public ServiceModel getServiceModel(){
        ServiceModel model=(ServiceModel)frm.getSelectedDocument().getGraph().getEditor().getGraphModel();
        return model;
    }
    public void processExit(){
        inspector.processExit();
    }
}

