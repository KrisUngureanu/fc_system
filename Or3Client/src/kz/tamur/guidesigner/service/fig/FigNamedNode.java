package kz.tamur.guidesigner.service.fig;

import org.tigris.gef.presentation.FigNode;
import org.tigris.gef.presentation.FigText;
import org.tigris.gef.util.Localizer;
import org.tigris.gef.base.CmdReorder;
import org.tigris.gef.base.Cmd;

import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.util.Vector;

import kz.tamur.comps.Constants;
import kz.tamur.rt.Utils;
import kz.tamur.guidesigner.service.ui.StateNode;

import javax.swing.*;
import static kz.tamur.rt.Utils.createMenuItem;
/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 09.09.2004
 * Time: 20:04:35
 * To change this template use File | Settings | File Templates.
 */
public class FigNamedNode extends FigNode {

    JMenuItem reorderBackItem = createMenuItem("На задний план", "ReorderBack");
    JMenuItem reorderFrontItem = createMenuItem("На передний план", "ReorderFront");
    JMenuItem forwardItem = createMenuItem("Вперёд", "Forward");
    JMenuItem backwardItem = createMenuItem("Назад", "Backward");

    private FigText nameFig;
    private String name;
    private Object owner;

    public FigNamedNode(Object owner) {
        super(owner);
        this.owner = owner;
    }

    public FigText getNameFig() {
        return nameFig;
    }

    public Object getOwner() {
        return owner;
    }
    
    public void setNameFig(FigText nameFig) {
        this.nameFig = nameFig;
        this.name = nameFig.getText();
        nameFig.addPropertyChangeListener(this);
    }

    public String getName() {
        return (name != null) ? name : "Безымянный";
    }

    public void setName(String name) {
        this.name = name;
        if (nameFig != null) {
//            nameFig.setText(name==null || name.equals("Безымянный")?" ":name);
        	nameFig.setText(name);
        }
    }

    public void mouseClicked(MouseEvent event) {
        super.mouseClicked(event);
        boolean par=true;
        if(getOwner() instanceof StateNode)
            par=((StateNode)getOwner()).isEnabled();
        if (par && event.getClickCount() > 1) {
            if (nameFig != null) {
                nameFig.startTextEditor(event);
            }
        }
    }

    public void propertyChange(PropertyChangeEvent event) {
        if ("editing".equals(event.getPropertyName())
                && Boolean.FALSE.equals(event.getNewValue())) {
            firePropChange("nodeName", name, name = nameFig.getText());
        } else {
            super.propertyChange(event);
        }
    }

    public void dispose() {
        super.dispose();
    }

    public String getTipString(MouseEvent me) {
        if(getOwner() instanceof StateNode && !((StateNode)getOwner()).isEnabled())
            return null;
        String className = getClass().getName().substring(
                Constants.SERVICE_FIG_PACKAGE.length());
        String name = (this instanceof FigNoteNode) ? "Примечание" : getName();
        return name + " [" + className + "]";
    }

    class PopupActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object src = e.getSource();
            Cmd command = null;
            if (src == reorderBackItem) {
                command = CmdReorder.SendToBack;
            } else if (src == reorderFrontItem) {
                command = CmdReorder.BringToFront;
            } else if (src == forwardItem) {
                command = CmdReorder.BringForward;
            } else if (src == backwardItem) {
                command = CmdReorder.SendBackward;
            }
            if (command != null) {
                command.doIt();
            }
        }
    }

    public Vector getPopUpActions(MouseEvent me) {
        Vector popUpActions = new Vector();
        JMenu orderMenu = new JMenu(Localizer.localize("PresentationGef", "Положение"));
        orderMenu.setMnemonic('П');
        orderMenu.setFont(Utils.getDefaultFont());
        orderMenu.setForeground(Utils.getDarkShadowSysColor());
        PopupActionListener al = new PopupActionListener();
        reorderBackItem.addActionListener(al);
        reorderFrontItem.addActionListener(al);
        forwardItem.addActionListener(al);
        backwardItem.addActionListener(al);
        orderMenu.add(reorderBackItem);
        orderMenu.add(reorderFrontItem);
        orderMenu.add(forwardItem);
        orderMenu.add(backwardItem);
        popUpActions.addElement(orderMenu);
        return popUpActions;
    }


}
