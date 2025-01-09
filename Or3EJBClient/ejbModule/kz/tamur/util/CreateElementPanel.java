package kz.tamur.util;

import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.EAST;
import static java.awt.GridBagConstraints.HORIZONTAL;
import kz.tamur.comps.Constants;
import kz.tamur.rt.Utils;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.rt.MainFrame;

import javax.swing.*;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.event.TreeSelectionEvent;

import com.cifs.or2.client.Kernel;

import java.awt.*;
import java.util.ResourceBundle;
import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 12.05.2004
 * Time: 12:13:35
 * To change this template use File | Settings | File Templates.
 */
public class CreateElementPanel extends JPanel implements TreeSelectionListener {

    public static final int CREATE_ELEMENT_TYPE = 0;
    public static final int RENAME_TYPE = 1;
    public static final int COPY_TYPE = 2;
    public static final int CREATE_FOLDER_TYPE = 3;
    public static final int CREATE_NEW_ELEMENT_TYPE = 4;
    public static final int CREATE_SERVICE_FOLDER_TYPE = 5;
    public static final int CREATE_FOLDER_TYPE_2 = 6;
    private int type;

    private JLabel imageLab = new JLabel(kz.tamur.rt.Utils.getImageIcon("CreateBig"));

    private JLabel titleLab = new JLabel();
    private JTextField textFld = new JTextField();
    private JPanel textPanel = new JPanel();
    private JCheckBox isRole = new JCheckBox("Роль");
    private String defText = "";
    private DesignerTree tree;
    private JLabel folderName = new JLabel();
    ResourceBundle res = ResourceBundle.getBundle(Constants.NAME_RESOURCES, new Locale("ru"));

    private ServiceFolderPropertyPanel servProp = new ServiceFolderPropertyPanel(true);
    static boolean isOpaque = true;
    static {
        if (Kernel.instance().getUser()!=null) {
            isOpaque = !MainFrame.TRANSPARENT_DIALOG;
        }
    }
    
    public CreateElementPanel(int type, String defText, long langId) {
        super(new GridBagLayout());
        LangItem li = LangItem.getById(langId);
        if (li != null) {
            if ("KZ".equals(li.code)) {
                res = ResourceBundle.getBundle(
                        Constants.NAME_RESOURCES, new Locale("kk"));
            }
        }

        this.type = type;
        if (type == CREATE_SERVICE_FOLDER_TYPE) {
            setPreferredSize(new Dimension(600, 150));
        } else {
            setPreferredSize(new Dimension(600, 100));
        }
        this.defText = defText;
        init();
    }

    public CreateElementPanel(int type, String defText, String lang) {
        super(new GridBagLayout());
        if ("KZ".equals(lang)) {
                res = ResourceBundle.getBundle(
                        Constants.NAME_RESOURCES, new Locale("kk"));
        }
        this.type = type;
        if (type == CREATE_SERVICE_FOLDER_TYPE) {
            setPreferredSize(new Dimension(600, 150));
        } else {
            setPreferredSize(new Dimension(600, 100));
        }
        this.defText = defText;
        init();
    }
    public CreateElementPanel(int type) {
        super(new GridBagLayout());
        this.type = type;
        if (type == CREATE_SERVICE_FOLDER_TYPE) {
            setPreferredSize(new Dimension(600, 150));
        } else {
            setPreferredSize(new Dimension(600, 100));
        }
        init();
    }
    public CreateElementPanel(int type, String defText) {
        this(type, defText, -1);
    }

    public CreateElementPanel(int type, String defText, DesignerTree tree) {
        super(new GridBagLayout());
        setPreferredSize(new Dimension(600, 500));
        this.type = type;
        this.defText = defText;
        this.tree = tree;
        init();
    }

    void init() {
        
        titleLab.setOpaque(false);
        titleLab.setFont(Utils.getDefaultFont());
        titleLab.setForeground(Utils.getDarkShadowSysColor());
        isRole.setOpaque(false);
        BoxLayout bl1 = new BoxLayout(textPanel, BoxLayout.Y_AXIS);
        textPanel.setLayout(bl1);
        textPanel.add(titleLab);
        textPanel.add(textFld);
        folderName.setFont(Utils.getDefaultFont());
        folderName.setForeground(Utils.getDarkShadowSysColor());
        add(imageLab, new GridBagConstraints(0, 0, 1, 1, 0, 0, CENTER, HORIZONTAL, Constants.INSETS_2, 0, 0));
        add(textPanel, new GridBagConstraints(1, 0, 1, 1, 1, 0, CENTER, HORIZONTAL, Constants.INSETS_2, 0, 0));
        switch(type) {
            case CREATE_FOLDER_TYPE:
                imageLab.setIcon(kz.tamur.rt.Utils.getImageIcon("CreateFolderBig"));
                titleLab.setText(res.getString("enterFolderName"));
                break;
            case CREATE_FOLDER_TYPE_2:
                imageLab.setIcon(kz.tamur.rt.Utils.getImageIcon("CreateFolderBig"));
                titleLab.setText(res.getString("enterFolderName"));
                add(isRole, new GridBagConstraints(1, 1, 1, 1, 1, 0, EAST, HORIZONTAL, Constants.INSETS_2, 0, 0));
                break;
            case CREATE_ELEMENT_TYPE:
                imageLab.setIcon(kz.tamur.rt.Utils.getImageIcon("CreateElementBig"));
                titleLab.setText(res.getString("enterElementName"));
                break;
            case CREATE_NEW_ELEMENT_TYPE:
                imageLab.setIcon(kz.tamur.rt.Utils.getImageIcon("CreateElementBig"));
                titleLab.setText(res.getString("enterElementName"));
                JScrollPane sp = new JScrollPane(tree);
                sp.setOpaque(isOpaque);
                sp.getViewport().setOpaque(isOpaque);
                tree.addTreeSelectionListener(this);
                sp.setPreferredSize(new Dimension(400, 400));
                add(folderName, new GridBagConstraints(1, 1, 1, 1, 1, 1,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        Constants.INSETS_2, 0, 0));
                add(sp, new GridBagConstraints(1, 2, 1, 1, 1, 1,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        Constants.INSETS_2, 0, 0));
                break;
            case COPY_TYPE:
                textFld.setText(defText);
                imageLab.setIcon(kz.tamur.rt.Utils.getImageIcon("CopyIfcBig"));
                titleLab.setText(res.getString("enterIntCopyName"));
                break;
            case RENAME_TYPE:
                textFld.setText(defText);
                break;
            case CREATE_SERVICE_FOLDER_TYPE:
                imageLab.setIcon(kz.tamur.rt.Utils.getImageIcon("CreateFolderBig"));
                titleLab.setText(res.getString("enterFolderName"));
                add(servProp, new GridBagConstraints(1, 1, 1, 1, 1, 1,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        Constants.INSETS_2, 0, 0));
                break;
        }
        if (type == CREATE_ELEMENT_TYPE) {
            textFld.requestFocusInWindow();
        }
        setOpaque(isOpaque);
        textPanel.setOpaque(isOpaque);
    }

    public String getElementName() {
    	Document doc = textFld.getDocument();
        try {
			return ("".equals(textFld.getText())) ? "Безымянный" : Funcs.sanitizeElementName(doc.getText(0, doc.getLength()));
		} catch (BadLocationException e) {
		}
        return "";
    }

    public void valueChanged(TreeSelectionEvent e) {
        AbstractDesignerTreeNode node =
                (AbstractDesignerTreeNode)e.getPath().getLastPathComponent();
        DesignerDialog dlg = (DesignerDialog)getTopLevelAncestor();
        if (node.equals(tree.getModel().getRoot())) {
            folderName.setText("Папка: Корень дерева интерфейсов");
            dlg.setOkEnabled(true);
        }
        if (node.isLeaf()) {
            dlg.setOkEnabled(false);
            folderName.setText("Папка:");
        } else {
            folderName.setText("Папка: " + node.toString());
            dlg.setOkEnabled(true);
        }
    }

    public String getServiceTabName() {
        return servProp.getInputName();
    }

    public boolean isServiceTab() {
        return servProp.isTab();
    }
    
    public boolean isRole() {
        return isRole.isSelected();
    }

}
