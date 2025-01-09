package kz.tamur.util;

import kz.tamur.comps.Constants;
import kz.tamur.rt.Utils;
import kz.tamur.rt.Descriptionable;

import javax.swing.*;
import javax.swing.border.Border;
import java.util.List;
import java.awt.event.*;
import java.awt.*;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnObject;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 05.05.2004
 * Time: 17:48:07
 * To change this template use File | Settings | File Templates.
 */
public class LanguageCombo extends JButton implements ActionListener, Descriptionable {

    private JPopupMenu pm = new JPopupMenu();
    private ButtonGroup bg = new ButtonGroup();

    private LangItem selectedLangItem;
    private JMenu otherItem = new JMenu("Дополнительно...");

    private KrnObject rusLang;
    private KrnObject kazLang;
    private Border mouseMoveBorder = BorderFactory.createLineBorder(new Color(163, 184, 204));;
    private boolean selfChange = false;
    private String desc;

    public LanguageCombo() {
        super();
        setOpaque(false);
        setContentAreaFilled(false);
        setBorder(null);
        setPreferredSize(new Dimension(20,20));
        setMargin(Constants.INSETS_0);
        setMaximumSize(new Dimension(20, 20));
        setMinimumSize(new Dimension(20, 20));
        initCombo();
    }

    private void initCombo() {
        pm.setFont(Utils.getDefaultFont());
        pm.setBackground(Utils.getLightSysColor());
        List langItems = LangItem.getAll();
        otherItem.setFont(Utils.getDefaultFont());
        otherItem.setForeground(Utils.getLightSysColor());
        for (int i = 0; i < langItems.size(); i++) {
            LangMenuItem mi = new LangMenuItem((LangItem)langItems.get(i), false);
            mi.addActionListener(this);
            bg.add(mi);
            if ("RU".equals(mi.getLangItem().code) ||
                    "KZ".equals(mi.getLangItem().code) ||
                    "EN".equals(mi.getLangItem().code)) {
                if ("RU".equals(mi.getLangItem().code)) {
                    rusLang = mi.getLangItem().obj;
                } else if ("KZ".equals(mi.getLangItem().code)) {
                    kazLang = mi.getLangItem().obj;
                }

                pm.add(mi);
            } else {
                otherItem.add(mi);
            }
            if (otherItem.getItemCount() > 0) {
                pm.add(otherItem);
            }
        }
        if (langItems.size() > 0) {
            selectedLangItem = LangItem.getById(
                    Kernel.instance().getInterfaceLanguage().id);
            setIcon(selectedLangItem.icon);
            if (selectedLangItem != null) {
                for (int i = 0; i < pm.getComponentCount(); i++) {
                    Component comp = pm.getComponent(i);
                    if (comp instanceof LangMenuItem) {
                        LangMenuItem lmi = (LangMenuItem)comp;
                        if (lmi.getLangItem().obj.id == selectedLangItem.obj.id) {
                            lmi.setSelected(true);
                            break;
                        }
                    }
                }
            }
        }
/*
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 1) {
                    pm.show(LanguageCombo.this, e.getX(), e.getY());
                }
            }
        });
*/
        addActionListener(this);
        addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                setBorder(mouseMoveBorder);
                super.focusGained(e);
            }

            public void focusLost(FocusEvent e) {
                setBorder(null);
                super.focusLost(e);    //To change body of overridden methods use File | Settings | File Templates.
            }
        });
    }

    public void reloadItems() {
        pm.removeAll();
        otherItem.removeAll();
        initCombo();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof LangMenuItem) {
            LangMenuItem lmi = (LangMenuItem)e.getSource();
            if (lmi.isSelected()) {
                selectedLangItem = lmi.getLangItem();
                setIcon(lmi.getLangItem().icon);
                selfChange = true;
                fireActionPerformed(e);
                selfChange = false;
            }
        } else {
            if (!selfChange) {
                LanguageCombo combo = (LanguageCombo)e.getSource();
                pm.show(combo, combo.getWidth()/2, combo.getHeight()/2);
            }
        }
    }

    public boolean isSelfChange(){
        return selfChange;
    }
    public Object getSelectedItem() {
        return selectedLangItem;
    }

    public class LangMenuItem extends JCheckBoxMenuItem {

        private LangItem langItem;

        public LangMenuItem(LangItem item, boolean selected) {
            super(item.name, item.icon, selected);
            langItem = item;
            setFont(Utils.getDefaultFont());
        }

        public LangItem getLangItem() {
            return langItem;
        }
    }

    
    public void setSelectedLanguage(KrnObject langObj) {
        List langItems = LangItem.getAll();
        if (langItems.size() > 0) {
            selectedLangItem = LangItem.getById(langObj.id);
            setIcon(selectedLangItem.icon);
            if (selectedLangItem != null) {
                for (int i = 0; i < pm.getComponentCount(); i++) {
                    Component comp = pm.getComponent(i);
                    if (comp instanceof LangMenuItem) {
                        LangMenuItem lmi = (LangMenuItem)comp;
                        if (lmi.getLangItem().obj.id == selectedLangItem.obj.id) {
                            lmi.setSelected(true);
                            break;
                        }
                    }
                }
            }
        }
    }

    public KrnObject getRusLang() {
        return rusLang;
    }

    public KrnObject getKazLang() {
        return kazLang;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
