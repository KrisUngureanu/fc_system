package kz.tamur.guidesigner.service;

import org.tigris.gef.base.*;
import javax.swing.*;
import java.util.Hashtable;
import java.awt.*;

import kz.tamur.guidesigner.service.ui.*;
import kz.tamur.guidesigner.service.cmd.ModeCreateFigLineEdge;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.comps.Utils;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 09.09.2004
 * Time: 16:21:46
 * To change this template use File | Settings | File Templates.
 */
public class NodePalette extends Utils.DesignerToolBar {

    public NodePalette() {
        super();
        defineButtons();
    }

    public void defineButtons() {
        add(new JLabel(kz.tamur.rt.Utils.getImageIcon("decor")));
        JToggleButton tb = ButtonsFactory.createCompButton(
                new CmdCreateNode(StartStateNode.class, ""), null,
                kz.tamur.rt.Utils.getImageIcon("Start"));
        tb.setToolTipText("Начало");
        add(tb);
        tb = ButtonsFactory.createCompButton(
                new CmdCreateNode(EndStateNode.class, ""), null,
                kz.tamur.rt.Utils.getImageIcon("Finish"));
        tb.setToolTipText("Завершение");
        add(tb);
        tb = ButtonsFactory.createCompButton(
                new CmdCreateNode(StartSyncNode.class, ""), null,
                kz.tamur.rt.Utils.getImageIcon("startSync"));
        tb.setToolTipText("Начало синхронизации");
        add(tb);
        tb = ButtonsFactory.createCompButton(
                new CmdCreateNode(EndSyncNode.class, ""), null,
                kz.tamur.rt.Utils.getImageIcon("finishSync"));
        tb.setToolTipText("Завершение синхронизации");
        add(tb);
        addSeparator();
        tb = ButtonsFactory.createCompButton(
                new CmdCreateNode(ActivityStateNode.class, ""), null,
                kz.tamur.rt.Utils.getImageIcon("Activity"));
        tb.setToolTipText("Действие");
        add(tb);
        tb = ButtonsFactory.createCompButton(
                new CmdCreateNode(SubProcessStateNode.class, ""), null,
                kz.tamur.rt.Utils.getImageIcon("Subprocess"));
        tb.setToolTipText("Подпроцесс");
        add(tb);
        tb = ButtonsFactory.createCompButton(
                new CmdCreateNode(DecisionStateNode.class, ""), null,
                kz.tamur.rt.Utils.getImageIcon("Decision"));
        tb.setToolTipText("Решение");
        add(tb);
        addSeparator();
        tb = ButtonsFactory.createCompButton(
                new CmdCreateNode(ForkNode.class, ""), null,
                kz.tamur.rt.Utils.getImageIcon("Fork"));
        tb.setToolTipText("Разветвление");
        add(tb);
        tb = ButtonsFactory.createCompButton(
                new CmdCreateNode(JoinNode.class, ""), null,
                kz.tamur.rt.Utils.getImageIcon("Join"));
        tb.setToolTipText("Слияние");
        add(tb);
        addSeparator();
        tb = ButtonsFactory.createCompButton(
                new CmdCreateNode(InBoxStateNode.class, ""), null,
                kz.tamur.rt.Utils.getImageIcon("MailIn"));
        tb.setToolTipText("Прием");
        add(tb);
        addSeparator();
        tb = ButtonsFactory.createCompButton(
                new CmdCreateNode(OutBoxStateNode.class, ""), null,
                kz.tamur.rt.Utils.getImageIcon("MailTo"));
        tb.setToolTipText("Отправка");
        add(tb);
        addSeparator();
        Hashtable args = new Hashtable();
        //args.put("edgeClass", TransitionEdge.class);
        args.put("edgeClass", TransitionEdge.class);
        CmdSetMode cmd = new CmdSetMode(ModeCreateEdge.class, args);
        cmd.setName("");
        tb = ButtonsFactory.createCompButton(
                cmd, null,
                kz.tamur.rt.Utils.getImageIcon("Edge"));
        tb.setToolTipText("Переход");
        add(tb);
        addSeparator();
        tb = ButtonsFactory.createCompButton(
                new CmdCreateNode(NoteStateNode.class, ""), null,
                kz.tamur.rt.Utils.getImageIcon("NoteServ"));
        tb.setToolTipText("Примечание");
        add(tb);
        addSeparator();
        tb = ButtonsFactory.createCompButton(
                new CmdCreateNode(ReportStateNode.class, ""), null,
                kz.tamur.rt.Utils.getImageIcon("ReportServ"));
        tb.setToolTipText("Отчёты");
        add(tb);
        addSeparator();
        cmd = new CmdSetMode(ModeCreateFigLineEdge.class, "");
        cmd.setName("");
        tb = ButtonsFactory.createCompButton(
                cmd, null,
                kz.tamur.rt.Utils.getImageIcon("Line1"));
        tb.setToolTipText("Связь");
        add(tb);
        JLabel l = new JLabel("");
        l.setPreferredSize(new Dimension(600, 10));
        add(l);
    }
}
