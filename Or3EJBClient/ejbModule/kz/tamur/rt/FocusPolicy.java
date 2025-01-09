package kz.tamur.rt;



import kz.tamur.comps.*;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.OrGuiContainer;

import java.awt.*;
import java.util.ArrayList;


/**
 * Created by IntelliJ IDEA.
 * User: KazakBala
 * Date: 09.02.2005
 * Time: 10:19:46
 * To change this template use File | Settings | File Templates.
 */
public class FocusPolicy extends ContainerOrderFocusTraversalPolicy{
    private Container focusContainer;

    public FocusPolicy(Container focusContainer) {
        this.focusContainer = focusContainer;
    }

    public int getMaxComponentIndex(OrPanel container) {
        Component[] children = container.getComponents();
        int res = 0;
        for (int i = 0; i < children.length; i++) {
                if (children[i] instanceof OrGuiComponent) {
                OrGuiComponent comp = (OrGuiComponent) children[i];
                if (comp != null) {
                    /*if (comp.getTabIndex() != -1) {
                        int val = comp.getTabIndex();
                        if (val > 0 && val > res) {
                            res = val;
                        }
                    }  */
                }
            }
        }
        return res;
    }

    private int getComponentTabOrder(Component focusedComponent) {
        if (focusedComponent instanceof OrGuiComponent) {
            OrGuiComponent comp = (OrGuiComponent) focusedComponent;
               // int tab = comp.getTabIndex();
              //  return (tab == 0) ? 1 : tab;
        } else {
            while(!(focusedComponent instanceof OrGuiComponent)) {
                Component parent = focusedComponent.getParent();
                if (parent != null) {
                    focusedComponent = parent;
                } else {
                    break;
                }
            }
            OrGuiComponent comp = (OrGuiComponent) focusedComponent;
               // int tab = comp.getTabIndex();
               // return (tab == 0) ? 1 : tab;
            }
        return -1;
    }

    private Component getComponentByIndex(OrGuiContainer owner, int idx) {
        Component res = null;
        if (owner instanceof OrGuiContainer) {
            Component[] childList = getComponents((OrPanel) owner);
            for (int i = 0; i < childList.length; i++) {
                Component fc = (Component)childList[i];
                if (fc instanceof OrGuiComponent) {
                    if (fc instanceof OrTabbedPane) {
                        /*OrTabbedPane tp = (OrTabbedPane)fc;
                        ArrayList tabs = tp.getPanels();
                        Container cnt = (Container)tabs.get(tp.getSelectedIndex());
                        Component c = getFirstOrGuiComponent((OrGuiContainer)cnt);
                        if (c instanceof OrGuiComponent) {
                            if (c instanceof JComponent)
                                ((JComponent)c).grabFocus();
                        }
                        return c;  */
                    }
                    OrGuiComponent comp = (OrGuiComponent) fc;
                    /*if (comp != null && comp.getTabIndex() == idx) {
                        //((Container)owner).setViewPosition((JComponent)comp);
                        res = fc;
                        break;
                    }     */
                }
            }
            if (res != null && (!res.isEnabled()))  {
                res = getComponentAfter((Container) owner, res);
            }
        }
        return res;
    }


    public Component getFirstOrGuiComponent(OrGuiContainer panel) {
        OrGuiComponent res = panel;
        while (res instanceof OrGuiContainer && !(res instanceof OrTable)) {
            Component[] childList = getComponents((OrPanel)panel);
            if (childList.length == 1 && childList[0] instanceof OrTabbedPane) {
                res = (OrPanel) ((OrTabbedPane)childList[0]).getSelectedComponent();
            } else if (childList.length == 1 && childList[0] instanceof OrTable) {
                res = (OrTable)childList[0];
                return (Component)res;
            } else {
                for (int i = 0; i < childList.length; i++) {
                    if (childList[i] instanceof OrGuiComponent) {
                        res = (OrGuiComponent) childList[i];
                        /*if (res != null && res.getTabIndex() == 1) {
                            break;
                        }*/
                    }

                }
            }
        }
        Component comp = (Component) res;
        boolean isEnabled = true;
        if (comp instanceof OrTextField)
            isEnabled = ((OrTextField) comp).isEditable();
        else
            isEnabled = comp.isEnabled();

        if (res != null && (!isEnabled)) {// ||
                //((OrEnum)((OrInspectable)res).getProperty(
                        //"Редактирование")).getStringVal().equals("Запрещено")))  {
            comp = getComponentAfter((Container)panel, comp);
        }
        if (comp != null) {
            //container.setViewPosition((JComponent)comp);
        }
        return comp;
    }

    public Component getDefaultComponent(Container focusCycleRoot) {
        if (focusCycleRoot instanceof OrPanel) {
            Component res = getComponentByIndex((OrGuiContainer)focusCycleRoot, 1);
            if (res instanceof OrButton) { // || res instanceof OrReportPrinter) {
                res = getComponentAfter(focusCycleRoot, res);
            }
            return res;
        }
        return null;
    }


    public Component getFirstComponent(Container focusCycleRoot) {
        if (focusCycleRoot instanceof OrPanel) {
            Component res = getFirstOrGuiComponent((OrGuiContainer)focusCycleRoot);
            if (res != null) {
                return res;
            }
        }
        return null;
    }

    public Component getInitialComponent(Window window) {
        return getFirstComponent(focusContainer);
    }

    public Component getComponentAfter(Container focusCycleRoot, Component aComponent) {
        if (focusCycleRoot instanceof OrGuiContainer) {
            int order = getComponentTabOrder(aComponent) + 1;
            Component c = getComponentByIndex((OrGuiContainer)focusCycleRoot, order);
            if (c != null) {
                return c;
            }
        } else {
            while(!(aComponent instanceof OrTable)) {
                Component parent = aComponent.getParent();
                if (parent != null) {
                    aComponent = parent;
                } else {
                    break;
                }
            }
            if (aComponent != null) {
                return aComponent;
            }
        }
        return null;
    }

    private Component[] getComponents(OrPanel panel){
        ArrayList res = new ArrayList();
        Component[] comps = panel.getComponents();
        for (int i=0; i< comps.length; i++) {

            if (!(comps[i] instanceof OrPanel.Spacer) && !(comps[i] instanceof OrLabel))
                res.add(comps[i]);
        }
        return (Component[])res.toArray(new Component[res.size()]);
    }
}
