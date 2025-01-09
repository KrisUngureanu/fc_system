package kz.tamur.guidesigner.service.cmd;


import kz.tamur.guidesigner.service.ServiceModel;
import kz.tamur.guidesigner.service.MainFrame;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.rt.orlang.ClientOrLang;
import kz.tamur.util.ExpressionDebuger;
import kz.tamur.comps.Utils;
import java.util.List;
import java.util.Vector;
import java.util.Iterator;

import org.tigris.gef.base.*;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: ValeT
 * Date: 03.08.2006
 * Time: 19:47:22
 * To change this template use File | Settings | File Templates.
 */
public class CmdCheck extends CmdPaste {

    private MainFrame frm;

    public CmdCheck(MainFrame frm) {
        super();
        this.frm = frm;
    }

    public void doIt() {
        ServiceModel model = frm.getSelectedDocument().getModel();
        model.setEventErrors(new Vector<String>());
        List errors=model.checkProcess(new ExpressionDebuger(new ClientOrLang(null)));
        String err_str="";
        if(errors.size()>0){
            for(Iterator it=errors.iterator();it.hasNext();err_str+="\n"+it.next());
        }else
            err_str+="\n"+"Ошибок нет!"+"\n";
        JScrollPane debugPane=new JScrollPane(new JTextArea(err_str));
        DesignerDialog dlg = new DesignerDialog((JFrame)frm.getTopLevelAncestor(),
                "Результат проверки процесса: "+model.getProcess().getName(),
                false,
                debugPane);
        dlg.setSize(700,550);
        dlg.setLocation(Utils.getCenterLocationPoint(700, 550));
        dlg.show();

    }

  public void undoIt() {
    System.out.println("Undo does not make sense for CmdCopy");
  }
}