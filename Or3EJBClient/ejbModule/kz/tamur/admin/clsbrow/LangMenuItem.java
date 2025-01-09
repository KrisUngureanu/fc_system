package kz.tamur.admin.clsbrow;

import kz.tamur.util.LangItem;

import javax.swing.*;

public class LangMenuItem extends JMenuItem {

        private LangItem langItem;

        public LangMenuItem(LangItem item) {
            super(item.name, item.icon);
            langItem = item;
            setFont(kz.tamur.rt.Utils.getDefaultFont());
        }

        public LangItem getLangItem() {
            return langItem;
        }
    }
