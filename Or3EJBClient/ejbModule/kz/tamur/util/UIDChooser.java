package kz.tamur.util;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import kz.tamur.comps.Constants;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.MessagesFactory;

import com.cifs.or2.client.ClassNode;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.Utils;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.StringValue;
import com.cifs.or2.util.Funcs;

/**
 * Created by IntelliJ IDEA.
 * User: kazakbala
 * Date: 15.03.2004
 * Time: 16:58:20
 * To change this template use Options | File Templates.
 */
public class UIDChooser extends JPanel implements ListSelectionListener {
    private String path;
    private KrnAttribute attribute;
    private JList valuesList = new JList();
    private JScrollPane scrollPane;
    private JPanel searchPanel = new JPanel();
    private Dimension buttonDimension = new Dimension(22, 22);
    private JLabel searchLabel = kz.tamur.rt.Utils.createLabel("Введите UID для поиска: ");
    private JTextField searchText = kz.tamur.rt.Utils.createDesignerTextField();
    private JButton searchBtn = ButtonsFactory.createToolButton("Find", "Найти", true);
    private TemplatesPanel templateSelectionPanel;

    public UIDChooser(String path, boolean isShowTemplatesPanel) {
        this.path = path;
        init(isShowTemplatesPanel);
    }
    
    public UIDChooser(String path) {
        this.path = path;
        init(false);
    }
    
    private void init(boolean isShowTemplatesPanel) {
        searchBtn.setPreferredSize(buttonDimension);
        searchBtn.setMaximumSize(buttonDimension);
        searchBtn.setMinimumSize(buttonDimension);
        searchBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchByUID(kz.tamur.util.Funcs.normalizeInput(searchText.getText()));
            }
        });
        searchPanel.setLayout(new GridBagLayout());
        searchPanel.add(searchLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 0), 0, 0));
        searchPanel.add(searchText, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));
        searchPanel.add(searchBtn, new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 0), 0, 0));
        searchPanel.add(new JLabel(" "), new GridBagConstraints(3, 0, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, Constants.INSETS_0, 0, 0));
        setSize(700, 500);
        setLayout(new BorderLayout());
        add(searchPanel, BorderLayout.NORTH);
        scrollPane = new JScrollPane(valuesList);
        scrollPane.setMinimumSize(new Dimension(600,400));
        scrollPane.setPreferredSize(new Dimension(600,400));
        scrollPane.setMaximumSize(new Dimension(600,400));
        try {
            Kernel krn = Kernel.instance();
            StringTokenizer st = new StringTokenizer(path, ".");
            int count = st.countTokens();
            if (count > 0) {
                String head = st.nextToken();
                ClassNode cnode = krn.getClassNodeByName(head);
                for (int i = 0; i < count - 2; ++i) {
                    String str = st.nextToken();
                    attribute = cnode.getAttribute(str);
                    cnode = krn.getClassNode(attribute.typeClassId);

                }
                attribute = cnode.getAttribute(st.nextToken());
                KrnObject[] objs = krn.getClassObjects(cnode.getKrnClass(), 0);
                long[] objId = Funcs.makeObjectIdArray(objs);
                Map uidmap = krn.getObjectUids(objId);
                String[] uids = new String[objId.length];
                for (int i = 0; i < objId.length; i++) {
                    uids[i] = (String) uidmap.get(new Long(objId[i]));
                }
                long langId = 0;
                if (attribute.isMultilingual)
                    langId = Utils.getInterfaceLangId();
                StringValue[] svs = krn.getStringValues(objId, attribute, langId, false, 0);
                ArrayList<UIDs> uid_list = new ArrayList<UIDs>();
                for (int i = 0; i < objId.length; i++) {
                    for (int j = 0; j < svs.length; j++) {
                        if (svs[j].objectId == objId[i])
                            uid_list.add(new UIDs(svs[j].value, uids[i]));
                    }
                }
                UIDs[] uid_array = new UIDs[uid_list.size()];
                for (int i = 0; i < uid_list.size(); i++)
                    uid_array[i] = (UIDs) uid_list.get(i);    

                DefaultListModel lm = new DefaultListModel();
                valuesList.setModel(lm);
                valuesList.setFont(Utils.getDefaultFont());
                Arrays.sort(uid_array);
                for (int i = 0; i < uid_array.length; i++) {
                    lm.addElement(uid_array[i]);
                }
            }
            valuesList.addListSelectionListener(this);
        } catch (KrnException e) {
            System.out.println("ОШИБКА В UIDCHOOSER");
            e.printStackTrace();  
        }

        add(scrollPane, BorderLayout.CENTER);
        if (isShowTemplatesPanel) {
        	templateSelectionPanel = new TemplatesPanel(valuesList);
            add(templateSelectionPanel, BorderLayout.SOUTH);
        }
    }

    private void searchByUID(String uid) {
        String message = "";
        StringTokenizer st = new StringTokenizer(uid, ".");
        if (st.countTokens() < 2) {
            message = "Неверный формат UID-а!";
            MessagesFactory.showMessageDialog((Dialog)this.getTopLevelAncestor(), MessagesFactory.ERROR_MESSAGE, message);
            return;
        }
        DefaultListModel listModel = (DefaultListModel)valuesList.getModel();
        for (int i = 0; i <  listModel.size(); i++) {
            UIDs uidObject = (UIDs)listModel.getElementAt(i);
            if (uidObject.getUID().equals(uid)) {
            	valuesList.setSelectedIndex(i);
                Point point = valuesList.indexToLocation(i);
                Point pointToScrol = new Point(point.x,  point.y + 300);
                valuesList.scrollRectToVisible(new Rectangle(pointToScrol));
                return;
            }
        }
        MessagesFactory.showMessageNotFound(getTopLevelAncestor());
    }
    
    public void valueChanged(ListSelectionEvent e) {
        UIDs uid = (UIDs)valuesList.getSelectedValue();
        DesignerDialog dialog = (DesignerDialog) getTopLevelAncestor();
        if (uid != null) {
            searchText.setText(uid.getUID());
            if (templateSelectionPanel != null) {
            	templateSelectionPanel.insertIntoTemplate(uid);
            }
        	dialog.setOkEnabled(true);
        } else {
        	dialog.setOkEnabled(false);
        }
    }

    public String[] getSelectedUID() {
        if (templateSelectionPanel != null) {
    		return templateSelectionPanel.getCodeTemplate();
        } else {
        	UIDs item = (UIDs) valuesList.getSelectedValue();
        	return new String[] { item.getUID() + "," + item.toString() };
        }
    }
    
    public String getStringUID() {
    	UIDs item = (UIDs) valuesList.getSelectedValue();
    	return item.getUID();
    }
    
    public JList getValuesList() {
    	return valuesList;
    }
   
    public class UIDs implements Comparable {
        private String stringValue;
        private String UID;

        public UIDs(String stringValue, String UID) {
        	this.stringValue = stringValue;
            this.UID = UID;

        }

        public int compareTo(Object o) {
            if (o != null && o instanceof UIDs) {
                String title = ((UIDs) o).stringValue;
                if (stringValue != null && title != null)
                    return stringValue.compareTo(title);
                if (stringValue == title)
                    return 0;
                if (stringValue == null)
                    return -1;
            }
            return 0;
        }

        public String toString() {
            return stringValue;
        }

        public String getUID() {
            return UID;
        }
    }
}
