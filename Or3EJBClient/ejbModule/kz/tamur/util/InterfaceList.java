package kz.tamur.util;

import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.client.Kernel;

import javax.swing.*;
import java.util.ArrayList;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.*;

import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.comps.Constants;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.MessagesFactory;
import static kz.tamur.rt.Utils.createMenuItem;
/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 12.05.2004
 * Time: 10:26:45
 * To change this template use File | Settings | File Templates.
 */
public class InterfaceList extends ObjectList implements ActionListener {

    public static final int TYPE_ALL = 0;
    public static final int TYPE_FORMS = 1;
    public static final int TYPE_SPRAVS = 2;
    public static final int TYPE_ARCHIVS = 3;

    private JMenuItem renameItem = createMenuItem("Переименовать");
    private JMenuItem deleteItem = createMenuItem("Удалить");

    private JPopupMenu pm = new JPopupMenu();

    private ObjectList.Item[] allInterfaces;

    public InterfaceList(KrnClass cls, String attrName) throws KrnException {
        super(cls, attrName);
        init();
    }

    public InterfaceList(KrnClass cls, KrnAttribute attr, int langId) throws KrnException {
        super(cls, attr, langId);
        init();
    }

    void init() {
        renameItem.addActionListener(this);
        deleteItem.addActionListener(this);
        pm.add(renameItem);
        pm.add(deleteItem);
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    pm.show(InterfaceList.this, e.getX(), e.getY());
                }
            }
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    pm.show(InterfaceList.this, e.getX(), e.getY());
                }
            }

            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Container c = getTopLevelAncestor();
                    if (c instanceof DesignerDialog) {
                        ((DesignerDialog)c).processOkClicked();
                    }
                }
                super.mouseClicked(e);
            }
        });
    }

    public void actionPerformed(ActionEvent e) {
        JMenuItem src = (JMenuItem)e.getSource();
        if (src == renameItem) {
            renameInterface();
        }  else if (src == deleteItem) {
            deleteInterface();
        }
    }

    private void renameInterface() {
        if ("".equals(getSelectedTitle())) {
            MessagesFactory.showMessageDialog((Dialog)getTopLevelAncestor(),
                    MessagesFactory.ERROR_MESSAGE, "Не выбран интерфейс!");
            return;
        }
        CreateElementPanel cp = new CreateElementPanel(
                CreateElementPanel.RENAME_TYPE, getSelectedTitle());
        DesignerDialog dlg = new DesignerDialog((Dialog)getTopLevelAncestor(),
                "Переименование интерфейса", cp);
        dlg.show();
        int res = dlg.getResult();
        if (res == ButtonsFactory.BUTTON_OK) {
            Kernel krn = Kernel.instance();
            try {
                KrnObject lang = krn.getInterfaceLanguage();
                krn.setString(getSelectedObject().id, attr_.id, 0, lang.id,
                        cp.getElementName(), 0);
                ((Item)getSelectedValue()).title = cp.getElementName();
            } catch (KrnException e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteInterface() {
        if ("".equals(getSelectedTitle())) {
            MessagesFactory.showMessageDialog((Dialog)getTopLevelAncestor(),
                    MessagesFactory.ERROR_MESSAGE, "Не выбран интерфейс!");
            return;
        }
        String mess = "Удалить интерфейс '" + getSelectedTitle() + "'?";
        int res = MessagesFactory.showMessageDialog((Dialog)getTopLevelAncestor(),
                MessagesFactory.QUESTION_MESSAGE, mess);
        if (res == ButtonsFactory.BUTTON_YES) {
            Kernel krn = Kernel.instance();
            try {
                int idx = getSelectedIndex();
                krn.deleteObject(getSelectedObject(), 0);
                ((DefaultListModel)getModel()).removeElement(getSelectedValue());
                setSelectedIndex(idx + 1);
            } catch (KrnException e) {
                e.printStackTrace();
            }
        }
    }

    public void filtered(int type) {
        if (allInterfaces == null) {
            DefaultListModel model = (DefaultListModel)this.getModel();
            allInterfaces = new ObjectList.Item[model.getSize()];
            for (int i = 0; i < model.getSize(); i++) {
                allInterfaces[i] = (ObjectList.Item)model.getElementAt(i);
            }
        }
        ArrayList filteredArray = new ArrayList();
        String prefix = "";
        switch(type) {
            case TYPE_FORMS:
                prefix = "Форма: ";
                break;
            case TYPE_SPRAVS:
                prefix = "Справочник: ";
                break;
            case TYPE_ARCHIVS:
                prefix = "Архив: ";
                break;
            default:
                prefix = "";
        }
        setSearchPrefix(prefix);
        if (type != TYPE_ALL) {
            for (int i = 0; i < allInterfaces.length; i++) {
                ObjectList.Item item = allInterfaces[i];
                if (item.toString().startsWith(prefix)) {
                    filteredArray.add(item);
                }
            }
            ObjectList.Item[] filteredItems = new ObjectList.Item[filteredArray.size()];
            for (int i = 0; i < filteredArray.size(); i++) {
                filteredItems[i] = (ObjectList.Item)filteredArray.get(i);
            }
            setListData(filteredItems);
        } else {
            setListData(allInterfaces);
        }
    }

    public void startView(String prevOpened) {
        String str = "";
        if (prevOpened.startsWith("* ")) {
            str = prevOpened.substring("* ".length());
        } else {
            str = prevOpened;
        }
        ListModel lm = getModel();
        for (int i = 0; i < lm.getSize(); i++) {
            if (lm.getElementAt(i).toString().toLowerCase(Constants.OK).startsWith(
                    str.toLowerCase(Constants.OK))) {
                setSelectedIndex(i);
                setViewPos();
                break;
            }
        }
    }

    private void setViewPos() {
        JViewport v = (JViewport)getParent();
        Point pt = v.getViewPosition();
        FontMetrics fm = getFontMetrics(getFont());
        pt.y = fm.getHeight() * getSelectedIndex();
        int maxYExt = v.getView().getHeight() - v.getHeight();
        pt.y = Math.max(0, pt.y);
        pt.y = Math.min(maxYExt, pt.y);
        v.setViewPosition(pt);
    }

}
