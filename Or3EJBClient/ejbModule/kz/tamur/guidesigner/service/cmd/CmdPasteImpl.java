package kz.tamur.guidesigner.service.cmd;

import org.tigris.gef.base.*;
import org.tigris.gef.presentation.Fig;
import org.tigris.gef.util.VetoableChangeEventSource;
import org.tigris.gef.graph.presentation.NetPort;

import java.util.*;
import java.util.List;
import java.awt.*;
import java.beans.VetoableChangeListener;
import kz.tamur.guidesigner.service.ui.*;
import kz.tamur.guidesigner.service.ServiceModel;
import kz.tamur.guidesigner.service.MainFrame;
import kz.tamur.guidesigner.service.NodePropertyConstants;
import kz.tamur.guidesigner.service.fig.FigTransitionEdge;
import kz.tamur.guidesigner.service.fig.FigLineEdge;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 01.12.2004
 * Time: 19:09:04
 * To change this template use File | Settings | File Templates.
 */
public class CmdPasteImpl extends CmdPaste {
    private Map<String,StateNode> nodeMap=new HashMap();
    private Map<String,String> lineMap=new HashMap();
    private Vector<StateNode> nodes=new Vector();
    private MainFrame frm;

    public CmdPasteImpl(MainFrame frm) {
        super();
        this.frm = frm;
    }

    public void doIt() {
      SelectionManager sm = Globals.curEditor().getSelectionManager();
      Vector figs = new Vector();
      Enumeration cb = Globals.clipBoard.elements();
      Editor ce = Globals.curEditor();
      while (cb.hasMoreElements()) {
        Fig f = (Fig) cb.nextElement();
        int gridSze = ((GuideGrid) ce.getGuide()).gridSize();
        Point p = f.getLocation();
        Object owner = f.getOwner();
        if(owner instanceof StateNode){
            f=pasteNode(owner);
            if(f!=null){
                f.setLocation(p);
                nodes.add((StateNode)owner);
            }
        }else{
            if(f instanceof FigLineEdge){
                f=pastLine(f);
            }else{
                f.translate(gridSze, gridSze);
                f = (Fig) f.clone();
                if (owner instanceof VetoableChangeEventSource && f instanceof VetoableChangeListener)
                  ((VetoableChangeEventSource)owner).addVetoableChangeListener((VetoableChangeListener)f);
            }
        }
        if(f!=null){
            ce.add(f);
            figs.addElement(f);
        }
      }
      connectEdges();
      sm.deselectAll();
      sm.select(figs);
    }
    private Fig pastLine(Fig f){
        Point[] pts=f.getPoints();
        FigLineEdge f_l=new FigLineEdge(pts[0].x,pts[0].y,pts[1].x,pts[1].y,true);
        f_l.setLineColor(f.getLineColor());
        lineMap.put(f.getId(),f_l.getId());
        return f_l;
    }
    private Fig pasteNode(Object owner){
       StateNode node=(StateNode)owner;
        ServiceModel smp=node.getModel();
        Editor ce = Globals.curEditor();
        ServiceModel sm=(ServiceModel)ce.getGraphModel();
        StateNode state_node=null;
        if(node instanceof StartStateNode){
            state_node=new StartStateNode(sm);
        }else if(node instanceof StartSyncNode){
                state_node=new StartSyncNode(sm);
        }else if( node instanceof EndSyncNode){
            state_node=new EndSyncNode(sm);
        }else if( node instanceof EndStateNode){
            state_node=new EndStateNode(sm);
        }else if(node instanceof ActivityStateNode){
            state_node=new ActivityStateNode(sm);
        }else if(node instanceof InBoxStateNode){
            state_node=new InBoxStateNode(sm);
        }else if(node instanceof OutBoxStateNode){
            state_node=new OutBoxStateNode(sm);
        }else if(node instanceof SubProcessStateNode){
            state_node=new SubProcessStateNode(sm);
        }else if(node instanceof DecisionStateNode){
            state_node=new DecisionStateNode(sm);
        }else if(node instanceof ForkNode){
            state_node=new ForkNode(sm);
        }else if(node instanceof JoinNode){
            state_node=new JoinNode(sm);
        }else if(node instanceof NoteStateNode){
            state_node=new NoteStateNode(sm);
        }
        if(state_node!=null && ((ServiceModel)ce.getGraphModel()).canAddNode(state_node)){
            state_node.initialize(null);
            state_node.makePresentation(null);
            sm.addNode(state_node);
            state_node.setName(node.getName());
            sm.setNodeName(state_node,smp.getNodeName(Long.valueOf(node.getId()).longValue(),frm.getRusLang()),frm.getRusLang());
            sm.setNodeName(state_node,smp.getNodeName(Long.valueOf(node.getId()).longValue(),frm.getKazLang()),frm.getKazLang());
            ((ServiceModel)ce.getGraphModel()).setNodeName(state_node,node.getName());
            state_node.setDescription(node.getDescription());
            ((ServiceModel)ce.getGraphModel()).setNodeDescription(state_node,node.getDescription());
            state_node.getPropertyMap().putAll(node.getPropertyMap());
            state_node.getActionMap().putAll(node.getActionMap());
            state_node.getPresentation().setBounds(node.getPresentation().getBounds());
            nodeMap.put(node.getId(),state_node);
            return state_node.getPresentation();
        }else return null;
    }
     private void connectEdges(){
         Vector edges=new Vector();
         Editor ce = Globals.curEditor();
         ServiceModel sm=(ServiceModel)ce.getGraphModel();
         for(Iterator it=nodes.iterator();it.hasNext();){
             StateNode node=(StateNode)it.next();
             List edgs=node.getPresentation().getFigEdges();
             for(Iterator itt=edgs.iterator();itt.hasNext();){
                 FigTransitionEdge edge=(FigTransitionEdge)itt.next();
                 if(edges.contains(edge)) continue;
                 StateNode dst=(StateNode)edge.getDestFigNode().getOwner();
                 StateNode src=(StateNode)edge.getSourceFigNode().getOwner();
                 if(nodes.contains(dst) && nodes.contains(src)){
                     edges.add(edge);
                     TransitionEdge edge_=new TransitionEdge(sm);
                     TransitionEdge edge0=(TransitionEdge)edge.getOwner();
                     ServiceModel smp=edge0.getModel();
                     NetPort src_ = ((StateNode)nodeMap.get(src.getId())).getPort(0);
                     NetPort dst_ = ((StateNode)nodeMap.get(dst.getId())).getPort(0);
                     edge_.connect((ServiceModel)ce.getGraphModel(), src_, dst_);
                     ((ServiceModel)ce.getGraphModel()).addEdge(edge_);
                     edge_.setPoints(edge.getPointsVector());
                     edge_.setName(edge0.getName());
                     sm.setEdgeName(edge_,smp.getEdgeName(Long.valueOf(edge_.getId()).longValue(),frm.getRusLang()),frm.getRusLang());
                     sm.setEdgeName(edge_,smp.getEdgeName(Long.valueOf(edge_.getId()).longValue(),frm.getKazLang()),frm.getKazLang());
                     Object join= edge0.getProperty(NodePropertyConstants.EDGE_JOIN);
                     if(join!=null && !join.toString().equals("")){
                         edge_.setProperty(NodePropertyConstants.EDGE_JOIN,transformJoin(join.toString()));
                     }
                 }
             }

         }

     }

      private String transformJoin(String join){
          String res="";
          StringTokenizer st=new StringTokenizer(join,";",false);
          while(st.hasMoreElements()){
              if(!res.equals("")) res+=";";
              String el=(String)st.nextElement();
              if(el.substring(0,el.indexOf("=")).equals("node")){
                 StateNode node= nodeMap.get(el.substring(el.indexOf("=")+1));
                  if(node!=null) res +=  "node="+node.getId();
              }else if(el.substring(0,el.indexOf("=")).equals("line")){
                  String lineId= lineMap.get(el.substring(el.indexOf("=")+1));
                   if(lineId!=null) res +=  "line="+lineId;
              }
          }
        return res;
      }
  public void undoIt() {
    System.out.println("Undo does not make sense for CmdCopy");
  }
}