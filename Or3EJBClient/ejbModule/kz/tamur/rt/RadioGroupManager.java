package kz.tamur.rt;

import kz.tamur.comps.*;
import kz.tamur.comps.models.PropertyNode;

import java.awt.*;
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



    public RadioGroupManager() {
    }

    public void evaluate(OrFrame frame, String radioGroup) {
        OrPanel panel = (OrPanel) frame.getPanel();
        Component[] comps = panel.getComponents();
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

    private ArrayList getGuiComponents(Component[] comps) {
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
                    if (comps[i] instanceof OrPanel)
                        getComponents((OrPanel) comps[i]);
                    else if (comps[i] instanceof OrSplitPane)
                        getComponents((OrSplitPane) comps[i]);
                    else if (comps[i] instanceof OrTabbedPane)
                        getComponents((OrTabbedPane) comps[i]);

                }
            }
        }
        return grouped;
    }

    private void getComponents(OrSplitPane cont) {
        OrSplitPane split = (OrSplitPane) cont;
        Component[] cs = split.getComponents();
        for (int i = 0; i < cs.length; i++) {
            if (cs[i] instanceof OrGuiComponent) {
                if (cs[i] instanceof OrPanel)
                    getComponents((OrPanel) cs[i]);
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

    private void getComponents(OrPanel panel) {
        Component[] cs = panel.getComponents();
        getGuiComponents(cs);
    }

    private void getComponents(OrTabbedPane tb) {
        Component[] cs = tb.getComponents();
        for (int i=0; i< cs.length; i++) {
            if (cs[i] instanceof OrGuiComponent) {
                if (cs[i] instanceof OrPanel)
                    getComponents((OrPanel) cs[i]);
            }
        }

    }

    private void group_evaluate(ArrayList comps) {
        OrGuiComponent main = null;

        for (int i = 0; i < comps.size(); i++) {
            OrGuiComponent comp = (OrGuiComponent) comps.get(i);
            if (comp instanceof OrTextField) {
                OrTextField tf = (OrTextField) comp;
                if (tf.getText().length() > 0) {
                    main = comp;
                }
            } else if (comp instanceof OrPasswordField) {
                OrPasswordField pf = (OrPasswordField) comp;
                if (pf.getPassword().length>0) {
                    main = comp;
                }
            } else if (comp instanceof OrComboBox) {
                OrComboBox cmd = (OrComboBox) comp;
                if (cmd.getSelectedIndex()>0) {
                    main = comp;
                }
            } else if (comp instanceof OrCheckBox) {
                OrCheckBox chk = (OrCheckBox) comp;
                if (chk.isSelected())
                    main = comp;
            } else if (comp instanceof OrIntField) {
                OrIntField intf = (OrIntField) comp;
                if (intf.getText().length() > 0)
                    main = comp;
            } else if (comp instanceof OrFloatField) {
                OrFloatField ff = (OrFloatField) comp;
                if (ff.getText().length() > 0)
                    main = comp;
            } else if (comp.getClass() == OrMemoField.class) {
                OrMemoField memo = (OrMemoField) comp;
                if (memo.getText().length() > 0)
                    main = comp;
            } else if (comp.getClass() == OrRichTextEditor.class) {
            	OrRichTextEditor rte = (OrRichTextEditor) comp;
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
                groupId = pv.stringValue();
            }
        }
        return groupId;
    }

    private void setEnabled(ArrayList comps, boolean enabled) {
        for (int i = 0; i < comps.size(); i++) {
            OrGuiComponent c = (OrGuiComponent) comps.get(i);
            if (c instanceof OrTextField) {
                OrTextField tf = (OrTextField) c;
                tf.setEditable(enabled);
            } else if (c instanceof OrPasswordField) {
                OrPasswordField pf = (OrPasswordField) c;
                pf.setEnabled(enabled);
            } else if (c instanceof OrComboBox) {
                OrComboBox cmb = (OrComboBox) c;
                cmb.setEnabled(enabled);
            } else if (c instanceof OrCheckBox) {
                OrCheckBox chk = (OrCheckBox)c;
                chk.setEnabled(enabled);
            } else if (c instanceof OrIntField) {
                OrIntField intf = (OrIntField) c;
                intf.setEditable(enabled);
            } else if (c instanceof OrFloatField) {
                OrFloatField ff = (OrFloatField) c;
                ff.setEditable(enabled);
            } else if (c.getClass() == OrMemoField.class) {
                OrMemoField memo = (OrMemoField) c;
                memo.setEditable(enabled);
            } else if (c.getClass() == OrRichTextEditor.class) {
            	OrRichTextEditor rte = (OrRichTextEditor) c;
            	rte.setEditable(enabled);
            }
        }
    }
}
