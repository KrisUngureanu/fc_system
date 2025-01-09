package kz.tamur.guidesigner.service.cmd;

import javax.swing.JFrame;

import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.guidesigner.service.MainFrame;

import org.tigris.gef.base.Cmd;

import com.cifs.or2.kernel.KrnObject;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 13.09.2004
 * Time: 16:08:56
 * To change this template use File | Settings | File Templates.
 */
public class CmdDeleteProcess extends Cmd {
    private MainFrame frm;

    public CmdDeleteProcess(String name, MainFrame frm) {
        super(name);
        this.frm = frm;
    }

    public void doIt() {
        int res = MessagesFactory.showMessageDialog((JFrame) frm.getTopLevelAncestor(), MessagesFactory.QUESTION_MESSAGE, "Удалить процесс '" + frm.getSelectedDocument().getTitle() + "'?");
        if (res != ButtonsFactory.BUTTON_NOACTION && res == ButtonsFactory.BUTTON_YES) {
            KrnObject obj = frm.getSelectedDocument().getKrnObject();
            frm.removeTab(obj);
        }
    }

    public void undoIt() {}
}