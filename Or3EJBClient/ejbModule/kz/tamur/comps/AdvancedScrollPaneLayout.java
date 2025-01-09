package kz.tamur.comps;

import javax.swing.*;
import java.awt.*;

import kz.tamur.comps.ui.AdvancedScrollPane;


/**
 * Created by IntelliJ IDEA.
 * User: Berik
 * Date: 29.01.2004
 * Time: 10:16:05
 * To change this template use Options | File Templates.
 */

public class AdvancedScrollPaneLayout extends ScrollPaneLayout {
        protected JViewport colFoot;

        public AdvancedScrollPaneLayout() {
        }

        public void syncWithScrollPane(JScrollPane sp) {
            super.syncWithScrollPane(sp);
            colFoot = sp.getColumnHeader();
        }

        public void addLayoutComponent(String s, Component c) {
            if (s.equals(AdvancedScrollPane.COLUMN_FOOTER)) {
                colFoot = (JViewport)addSingletonComponent(colFoot, c);
            } else {
                super.addLayoutComponent(s, c);
            }
        }

        public void removeLayoutComponent(Component c) {
            if (c == colFoot) {
                colFoot = null;
            } else {
                super.removeLayoutComponent(c);
            }
        }

        public JViewport getColumnFooter() {
            return colFoot;
        }

        public void layoutContainer(Container parent) {
            if (viewport != null && colFoot != null) {
                JScrollPane scrollPane = (JScrollPane)parent;
                Rectangle bounds = scrollPane.getBounds();
                Dimension sz  = colFoot.getPreferredSize();
                int currDelta = colFoot.getFont().getSize();
                int moHeight =  sz.height - currDelta;
                scrollPane.setBounds(bounds.x, bounds.y, bounds.width, bounds.height - sz.height+ (moHeight > 0?moHeight-4 :0));
                super.layoutContainer(parent);
                Rectangle r = viewport.getBounds();
                colFoot.setBounds(r.x,r.y+ r.height+(moHeight > 0?1 :0) , r.width, sz.height - (moHeight > 0?moHeight-3 :0));
                if (hsb != null && hsb.isVisible()) {
                    Rectangle w = hsb.getBounds();
                    w.y += sz.height-(moHeight > 0?moHeight-4 :0);
                    hsb.setBounds(w);
                }
                scrollPane.setBounds(bounds);
            } else {
                super.layoutContainer(parent);
            }
        }
    }




