package kz.tamur.rt;

import kz.tamur.comps.*;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.web.common.webgui.WebComponent;
import kz.tamur.web.component.OrWebCheckBox;
import kz.tamur.web.component.OrWebComboBox;
import kz.tamur.web.component.OrWebFloatField;
import kz.tamur.web.component.OrWebIntField;
import kz.tamur.web.component.OrWebMemoField;
import kz.tamur.web.component.OrWebRichTextEditor;
import kz.tamur.web.component.OrWebPanel;
import kz.tamur.web.component.OrWebPasswordField;
import kz.tamur.web.component.OrWebSplitPane;
import kz.tamur.web.component.OrWebTabbedPane;
import kz.tamur.web.component.OrWebTextField;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 * User: kazakbala
 * Date: 22.09.2004
 * Time: 17:53:41
 * To change this template use File | Settings | File Templates.
 */
public class RadioGroupManager {
    private String groupId;
    private ArrayList grouped;
    private String primId, secId;
    private OrFrame frame;

    public RadioGroupManager() {
    }

    public void evaluate(OrFrame frame, String radioGroup) {
    	this.frame = frame;
    	OrWebPanel panel = (OrWebPanel) frame.getPanel();
        WebComponent[] comps = panel.getComponents();
        if (grouped != null)
            grouped.clear();
        StringTokenizer IDparts = new StringTokenizer(radioGroup, ".");
        if (IDparts.countTokens() > 0) {
            primId = IDparts.nextToken();
            secId = IDparts.nextToken();
        }
        ArrayList guicomps = getGuiComponents(comps);
        group_evaluate(guicomps); 
    }

    private ArrayList getGuiComponents(WebComponent[] comps) {
        if (grouped == null)
            grouped = new ArrayList();
        for (int i = 0; i < comps.length; i++) {
            if (comps[i] instanceof OrGuiComponent) {
                if (!(comps[i] instanceof OrGuiContainer)) {
                    OrGuiComponent c = (OrGuiComponent) comps[i];
                    String groupId = getGroupId(c);
                    if (groupId != null) {
                        if (groupId.startsWith(primId + "."))
                            grouped.add(c);
                    }
                } else {
                    if (comps[i] instanceof OrWebPanel)
                        getComponents((OrWebPanel) comps[i]);
                    else if (comps[i] instanceof OrWebSplitPane)
                        getComponents((OrWebSplitPane) comps[i]);
                    else if (comps[i] instanceof OrWebTabbedPane)
                        getComponents((OrWebTabbedPane) comps[i]);

                }
            }
        }
        return grouped;
    }

    private void getComponents(OrWebSplitPane cont) {
    	OrWebSplitPane split = (OrWebSplitPane) cont;
        WebComponent[] cs = split.getComponents();
        for (int i = 0; i < cs.length; i++) {
            if (cs[i] instanceof OrGuiComponent) {
                if (cs[i] instanceof OrWebPanel)
                    getComponents((OrWebPanel) cs[i]);
            }
        } /*else if (cont instanceof OrTabbedPane) {
            OrTabbedPane tabpane = (OrTabbedPane) cont;
            Component[] cs = tabpane.getComponents();
            for (int i=0; i < cs.length; i++) {
                if (cs[i] instanceof OrGuiComponent) {
                    if (cs[i] instanceof OrGuiContainer) {}
                        //getComponents((OrGuiComponent)cs[i]);
                }
            }
        }  */
    }

    private void getComponents(OrWebPanel panel) {
        WebComponent[] cs = panel.getComponents();
        getGuiComponents(cs);
    }

    private void getComponents(OrWebTabbedPane tb) {
        WebComponent[] cs = tb.getComponents();
        for (int i=0; i< cs.length; i++) {
            if (cs[i] instanceof OrGuiComponent) {
                if (cs[i] instanceof OrWebPanel)
                    getComponents((OrWebPanel) cs[i]);
            }
        }

    }

    private void group_evaluate(ArrayList comps) {
        OrGuiComponent main = null;

        for (int i = 0; i < comps.size(); i++) {
            OrGuiComponent comp = (OrGuiComponent) comps.get(i);
            if (comp instanceof OrWebTextField) {
                OrWebTextField tf = (OrWebTextField) comp;
                if (tf.getText().length() > 0) {
                    main = comp;
                }
            } else if (comp instanceof OrWebPasswordField) {
                OrWebPasswordField pf = (OrWebPasswordField) comp;
                if (pf.getText().length()>0) {
                    main = comp;
                }
            } else if (comp instanceof OrWebComboBox) {
                OrWebComboBox cmd = (OrWebComboBox) comp;
                if (cmd.getSelectedIndex()>0) {
                    main = comp;
                }
            } else if (comp instanceof OrWebCheckBox) {
                OrWebCheckBox chk = (OrWebCheckBox) comp;
                if (chk.isSelected())
                    main = comp;
            } else if (comp instanceof OrWebIntField) {
                OrWebIntField intf = (OrWebIntField) comp;
                if (intf.getText().length() > 0)
                    main = comp;
            } else if (comp instanceof OrWebFloatField) {
                OrWebFloatField ff = (OrWebFloatField) comp;
                if (ff.getText().length() > 0)
                    main = comp;
            } else if (comp.getClass() == OrWebMemoField.class) {
                OrWebMemoField memo = (OrWebMemoField) comp;
                if (memo.getText().length() > 0)
                    main = comp;
            } else if (comp.getClass() == OrWebRichTextEditor.class) {
            	OrWebRichTextEditor rte = (OrWebRichTextEditor) comp;
                if (rte.getText().length() > 0)
                    main = comp;
            }
        }
        if (main != null) {
            ArrayList liveGroup = new ArrayList();
            ArrayList deadGroup = new ArrayList();
            String groupId = getGroupId(main);
            StringTokenizer IDparts = new StringTokenizer(groupId, ".");
            if (IDparts.countTokens() > 0) {
                primId = IDparts.nextToken();
                secId = IDparts.nextToken();
            }
            for (int i=0;i<comps.size();i++) {
                String id = getGroupId((OrGuiComponent)comps.get(i));
                IDparts = new StringTokenizer(id, ".");
                if (IDparts.countTokens() > 0) {
                    String pId = IDparts.nextToken();
                    String sId = IDparts.nextToken();
                    if (primId.equals(pId) && secId.equals(sId)) {
                     liveGroup.add(comps.get(i));
                 } else {
                     deadGroup.add(comps.get(i));
                 }
                }
            }
            setEnabled(deadGroup, false);
            setEnabled(liveGroup, true);
        } else
            setEnabled(comps, true);


    }

    private String getGroupId(OrGuiComponent c) {
        String groupId = null;
        PropertyNode prop = c.getProperties();
        PropertyNode rprop = prop.getChild("constraints");
        if (rprop != null) {
            PropertyValue pv = c.getPropertyValue(rprop.getChild("radioGroup"));
            if (!pv.isNull()) {
                groupId = pv.stringValue(frame.getKernel());
            }
        }
        return groupId;
    }

    private void setEnabled(ArrayList comps, boolean enabled) {
        for (int i = 0; i < comps.size(); i++) {
            OrGuiComponent c = (OrGuiComponent) comps.get(i);
            if (c instanceof OrWebTextField) {
                OrWebTextField tf = (OrWebTextField) c;
                tf.setEnabled(enabled);
            } else if (c instanceof OrWebPasswordField) {
                OrWebPasswordField pf = (OrWebPasswordField) c;
                pf.setEnabled(enabled);
            } else if (c instanceof OrWebComboBox) {
                OrWebComboBox cmb = (OrWebComboBox) c;
                cmb.setEnabled(enabled);
            } else if (c instanceof OrWebCheckBox) {
                OrWebCheckBox chk = (OrWebCheckBox)c;
                chk.setEnabled(enabled);
            } else if (c instanceof OrWebIntField) {
                OrWebIntField intf = (OrWebIntField) c;
                intf.setEnabled(enabled);
            } else if (c instanceof OrWebFloatField) {
                OrWebFloatField ff = (OrWebFloatField) c;
                ff.setEnabled(enabled);
            } else if (c.getClass() == OrWebMemoField.class) {
                OrWebMemoField memo = (OrWebMemoField) c;
                memo.setEnabled(enabled);
            } else if (c.getClass() == OrWebRichTextEditor.class) {
            	OrWebRichTextEditor rte = (OrWebRichTextEditor) c;
            	rte.setEnabled(enabled);
            }
        }
    }
}
