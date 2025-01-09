package kz.tamur.comps.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javax.swing.JPopupMenu;

import kz.tamur.comps.MenuItemRecord;
import kz.tamur.comps.Mode;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.lang.OrLang;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.rt.adapters.ComponentAdapter;
import kz.tamur.rt.adapters.OrRef;
import kz.tamur.rt.orlang.ClientOrLang;

import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.util.expr.Editor;

public class PopupMenuSupport implements ActionListener {
	
	private JPopupMenu menu = new JPopupMenu();
	private ComponentAdapter adapter;
	
	public static PopupMenuSupport create(ComponentAdapter adapter) {
		OrGuiComponent comp = adapter.getComponent();
        PropertyNode pn = comp.getProperties().getChild("pmenu");
        if (pn != null) {
            PropertyValue pv = comp.getPropertyValue(pn);
            if (!pv.isNull()) {
            	MenuItemRecord[] items = pv.menuItemsValues();
            	return new PopupMenuSupport(adapter, items);
            }
        }
        return null;
	}
	
	private PopupMenuSupport(ComponentAdapter adapter, MenuItemRecord[] items) {
		this.adapter = adapter;
		OrGuiComponent comp = adapter.getComponent();
    	if (comp instanceof Component) {
            for (MenuItemRecord item : items) {
                ASTStart template = null;
                String expr = item.getExpr();
                if (expr != null) {
                    try {
                        template = OrLang.createStaticTemplate(expr);
                        Editor e = new Editor(expr);
                        ArrayList<String> paths = e.getRefPaths();
                        for (int j = 0; j < paths.size(); ++j) {
                            String path = paths.get(j);
                            OrRef.createRef(path, false, Mode.RUNTIME, adapter.getFrame().getRefs(), OrRef.TR_CLEAR, adapter.getFrame());
                        }
                    } catch (KrnException e1) {
                        e1.printStackTrace();
                    }
                    kz.tamur.rt.Utils.FuncPopupItem mi = kz.tamur.rt.Utils.createFuncPopupItem(item.getTitle(), template);
                    menu.add(mi);
                    mi.addActionListener(this);
                }

            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        kz.tamur.rt.Utils.FuncPopupItem item = (kz.tamur.rt.Utils.FuncPopupItem) e.getSource();
        ASTStart template = item.getTemplate();
        if (template != null) {
            ClientOrLang jep = new ClientOrLang(adapter.getFrame());
            Map vc = new HashMap();
            try {
                jep.evaluate(template, vc, adapter, new Stack<String>());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public JPopupMenu getMenu() {
    	return menu;
    }
}
