package kz.tamur.guidesigner.service.cmd;

import org.tigris.gef.base.*;
import org.tigris.gef.presentation.Fig;
import org.tigris.gef.graph.presentation.NetEdge;
import org.tigris.gef.graph.presentation.NetPort;
import kz.tamur.guidesigner.service.ui.StateNode;
import kz.tamur.guidesigner.service.ui.NodePort;
import kz.tamur.guidesigner.service.ServiceModel;
import kz.tamur.guidesigner.service.MainFrame;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.guidesigner.ButtonsFactory;

import javax.swing.*;
import java.util.Vector;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 20.09.2004
 * Time: 10:12:02
 * To change this template use File | Settings | File Templates.
 */


public class CmdDeleteFromSrvModel extends Cmd {

    private MainFrame frm;

    public CmdDeleteFromSrvModel(MainFrame mf) {
        super("Delete From Model");
        frm = mf;
    }

    public void doIt() {
        Editor ce = Globals.curEditor();
        SelectionManager sm = ce.getSelectionManager();
        ServiceModel model = (ServiceModel) ce.getGraphModel();
        Vector col = sm.selections();
        Vector edges = new Vector();
        int res = MessagesFactory.showMessageDialog((JFrame)frm.getTopLevelAncestor(),
                MessagesFactory.QUESTION_MESSAGE,
                "Подтвердите удаление " + col.size() + " элемента(ов)!");
        if (res == ButtonsFactory.BUTTON_YES) {
            for (int i = 0; i < col.size(); ++i) {
                Selection sr = (Selection) col.get(i);
                Fig f = sr.getContent();
                Object o = f.getOwner();
                if ((o instanceof NetEdge) && !edges.contains(o)) {
	            	  NetEdge edge = (NetEdge) o;
	                  NetPort srcPort = edge.getSourcePort();
	                  NetPort dstPort = edge.getDestPort();
	                  model.removeEdge(edge);
	                  srcPort.removeEdge(edge);
	                  dstPort.removeEdge(edge);
                } else if (o instanceof StateNode) {
                    List ports_ = ((StateNode) o).getPorts();
                    for (int j = 0; j < ports_.size(); ++j) {
                        Vector edges_ = ((NodePort) ports_.get(j)).getEdges();
                        edges.addAll(edges_);
                    }
                } 
            }          
            for (int i = 0; i < edges.size(); ++i) {
                NetEdge edge = (NetEdge) edges.get(i);
                NetPort srcPort = edge.getSourcePort();
                NetPort dstPort = edge.getDestPort();
                model.removeEdge(edge);
                srcPort.removeEdge(edge);
                dstPort.removeEdge(edge);
            }
            for (int i = 0; i < col.size(); ++i) {
                Selection sr = (Selection) col.get(i);
                Fig f = sr.getContent();
                Object o = f.getOwner();
                if (o instanceof StateNode) {
                    model.removeNode(o);
                }
            }
            sm.removeFromGraph();
            sm.deselectAll();
        }
    }

    public void undoIt() {
    }
}
    