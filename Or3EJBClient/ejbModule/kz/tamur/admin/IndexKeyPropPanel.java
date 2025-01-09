package kz.tamur.admin;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;

import kz.tamur.comps.Constants;
import kz.tamur.rt.Utils;
import kz.tamur.rt.MainFrame;

public class IndexKeyPropPanel extends JPanel {
    private DefaultListModel listModel = new DefaultListModel();
    private JList keyList = Utils.createListBox(listModel);
    private JCheckBox descCheckBox = Utils.createCheckBox("По убыванию (DESC)", false);// флаг - по убыванию
    private KrnClass currentClass;
    private Set<Long> usedAttrs;// Уже использованные атрибуты
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;

    public IndexKeyPropPanel(KrnClass krnClass, Set<Long> usedAttrs) {
        this.currentClass = krnClass;
        this.usedAttrs = usedAttrs;
        initPanel();
    }

    private void initPanel() {
        setOpaque(isOpaque);
        setLayout(new GridBagLayout());
        keyList.setCellRenderer(new CellRenderList());
        descCheckBox.setOpaque(isOpaque);
        add(new JScrollPane(keyList), new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, Constants.INSETS_0, 0, 0));
        add(descCheckBox, new GridBagConstraints(0, 1, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, Constants.INSETS_0, 0, 0));
        refreshContent();
    }

    private void refreshContent() {
        KrnAttribute[] krnAttrs;
        try {
            krnAttrs = Kernel.instance().getAttributesForIndexing(currentClass);
            listModel.clear();
            for (KrnAttribute krnAttr : krnAttrs) {
                if (!usedAttrs.contains(krnAttr.id)) {// отсеиваем уже использованные атрибуты
                    IndexKeyAttrListNode node = new IndexKeyAttrListNode(krnAttr);
                    listModel.addElement(node);
                }
            }
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }

    public DefaultListModel getListModel() {
        return this.listModel;
    }

    public JList getKeyList() {
        return this.keyList;
    }

    public boolean isDescending() {
        return this.descCheckBox.isSelected();
    }

    private static class CellRenderList extends JLabel implements ListCellRenderer {
        public Component getListCellRendererComponent(JList list, Object obj, int index, boolean selected, boolean b) {
            IndexKeyAttrListNode node = (IndexKeyAttrListNode) obj;
            String text = "<html>" + node.getText() + "<font color=gray> " + node.getSubText() + "</font>" + "</html>";
            JLabel lbl = Utils.createLabel(text);
            lbl.setIcon(node.getIcon());
            lbl.setOpaque(true);
            lbl.setFont(Utils.getDefaultComponentFont());
            if (selected) {// выделенный элемент
                lbl.setForeground(Color.WHITE);
                lbl.setBackground(Utils.getDarkShadowSysColor());
            } else {// не выделенный элемент
                lbl.setForeground(Color.BLACK);
                lbl.setBackground(Utils.getSilverColor());
            }
            return lbl;
        }
    }
}
