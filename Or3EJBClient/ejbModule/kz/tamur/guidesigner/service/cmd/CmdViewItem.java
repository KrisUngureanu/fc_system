package kz.tamur.guidesigner.service.cmd;

import kz.tamur.util.ExpressionEditor;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.service.MainFrame;
import kz.tamur.guidesigner.service.Document;
import org.tigris.gef.base.Cmd;
import org.tigris.gef.base.SelectionManager;
import org.tigris.gef.base.Globals;
import org.tigris.gef.base.SelectionResize;

import javax.swing.*;
import java.util.Vector;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 29.01.2005
 * Time: 13:04:25
 * To change this template use File | Settings | File Templates.
 */
public class CmdViewItem extends Cmd {
    private MainFrame frm;

    public CmdViewItem(String name, MainFrame frm) {
        super(name);
        this.frm = frm;
    }

    public void doIt() {
        SelectionManager sm = Globals.curEditor().getSelectionManager();
        Vector figs = sm.selections();
        String res_expr="";
        Document doc=frm.getSelectedDocument();
        if(doc==null) return;
        if(figs.size()==0){
            res_expr=doc.getModel().getScriptOfNodes(null,doc.getKrnObject().id);
        }else{

            SelectionResize s = (SelectionResize)figs.get(0);
            res_expr=doc.getModel().getScriptOfNodes(s.getContent(),doc.getKrnObject().id);
        }
        ExpressionEditor exprEditor = new ExpressionEditor(res_expr, CmdViewItem.this);
        exprEditor.setServiceFrm(frm);
        
        DesignerDialog dlg = new DesignerDialog((JFrame)frm.getTopLevelAncestor(), "Выражение", exprEditor);
        dlg.setSize(new Dimension(kz.tamur.comps.Utils.getMaxWindowSizeActDisplay()));
        dlg.setLocation(kz.tamur.comps.Utils.getCenterLocationPoint(dlg.getSize()));
        dlg.show();
        if (dlg.isOK()) {
        	setExpression(exprEditor.getExpression());
        }
    }
    
    public void setExpression(String expression) {
    	System.out.println(expression);
    }

    public void undoIt() {}
}
