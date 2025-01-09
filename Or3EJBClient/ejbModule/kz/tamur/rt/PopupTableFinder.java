package kz.tamur.rt;

import kz.tamur.comps.*;
import kz.tamur.comps.models.PropertyNode;
import javax.swing.*;
import java.awt.*;
/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 06.04.2004
 * Time: 11:29:23
 * To change this template use File | Settings | File Templates.
 */
public class PopupTableFinder {

    private JTable table;
    private Component frame;
    private Component linkedField;

    public PopupTableFinder(Component fr, JTable table) {
        this.table = table;
        this.frame = fr;
    }

    private Component findLinkedComponent() {
        Container c = frame.getParent();
        if (c == null) {
            return null;
        }
        PropertyNode proot = ((OrHyperPopup)frame).getProperties();
        PropertyNode rprop = proot.getChild("ref").getChild("data");
        PropertyValue pv = ((OrHyperPopup) frame).getPropertyValue(rprop);

        String textToFind = pv.stringValue(); //(OrHyperPopup)frame.getRef().toString();
        Component[] comps = c.getComponents();
        for (int i = 0; i < comps.length; i++) {
            Component comp = comps[i];
            if (comp != null && //comp instanceof OrGuiComponent &&
                    comp instanceof OrTextField) {
                if (((OrTextField)comp).getProperties().getChild("ref").getChild("data") != null) {
                    rprop = ((OrTextField)comp).getProperties().getChild("ref").getChild("data");
                    String str = ((OrTextField)comp).getPropertyValue(rprop).stringValue();
                    if (str != null && str.length() > textToFind.length()) {
                        String compStartRef =
                                str.substring(0, textToFind.length());
                        if (textToFind.equals(compStartRef) && comp instanceof OrTextField) {
                            return comps[i];
                        }
                    }
                }
            }
        }
        return null;
    }

    public void find() {
        linkedField = findLinkedComponent();
        if (linkedField == null) {
            return;
        }
        String textToFind = ((OrTextField)linkedField).getText();
        for (int i = 0; i < table.getRowCount(); i++) {
            for (int j = 0; j < table.getColumnCount(); j++) {
                Object tableVal = table.getValueAt(i, j);
                if (tableVal != null && tableVal.toString().equals(textToFind)) {
                    table.getSelectionModel().setSelectionInterval(i,i);
                    moveViewPort(i);
                    return;
                }
            }
        }
        table.getSelectionModel().setSelectionInterval(0,0);
    }

    private void moveViewPort(int row) {
        if (!(table.getParent() instanceof JViewport)) {
            return;
        }
        JViewport view = (JViewport)table.getParent();
        Point pt = view.getViewPosition();
        pt.y = row * table.getRowHeight();
        int maxYExt = view.getView().getHeight() - view.getHeight();
        pt.y = Math.max(0, pt.y);
	    pt.y = Math.min((maxYExt < 0) ? 0 : maxYExt, pt.y);
        view.setViewPosition(pt);
    }

    //private void getRef(Component) {

    //}
}
