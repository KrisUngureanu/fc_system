package kz.tamur.guidesigner.service.cmd;


import kz.tamur.guidesigner.service.MainFrame;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.Or3Frame;
import kz.tamur.comps.Constants;
import java.util.List;
import java.awt.Dimension;
import java.awt.Window;

import org.tigris.gef.base.*;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.KrnVcsChange;

/**
 * Created by IntelliJ IDEA.
 * User: ValeT
 * Date: 03.08.2006
 * Time: 19:47:22
 * To change this template use File | Settings | File Templates.
 */
public class CmdViewHistory extends Cmd {

    private MainFrame frm;

    public CmdViewHistory(String name,MainFrame frm) {
        super(name);
        this.frm = frm;
    }

    public void doIt() {
        KrnObject obj = frm.getSelectedDocument().getKrnObject();
    	if(obj!=null) {
			try {
				List<KrnVcsChange> changes = Kernel.instance().getVcsChangesByUID(Constants.VCS_ALL, -1, -1, -1, obj.uid);
	        	if(changes.size()>0) {
	        		Or3Frame.historysPanel.refreshTable(changes.get(0), true);
	        	}
	        	DesignerDialog dlg = new DesignerDialog((Window) frm.getTopLevelAncestor(), "История изменений", Or3Frame.historysPanel);
	            dlg.setMinimumSize(new Dimension(900, 70));
	            dlg.show();
			} catch (KrnException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    	}
    }

  public void undoIt() {
    System.out.println("Undo does not make sense for CmdCopy");
  }
}