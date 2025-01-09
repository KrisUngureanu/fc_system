package kz.tamur.guidesigner;

import java.awt.AWTEvent;
import java.awt.ActiveEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.MenuComponent;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import kz.tamur.Or3Frame;
import kz.tamur.admin.ClassBrowser;
import kz.tamur.comps.Constants;
import kz.tamur.comps.OrButton;
import kz.tamur.comps.OrFrame;
import kz.tamur.comps.OrHyperPopup;
import kz.tamur.comps.OrPanel;
import kz.tamur.comps.OrTable;
import kz.tamur.comps.ui.GradientPanel;
import kz.tamur.rt.Utils;
import kz.tamur.guidesigner.reports.ReportPrinter;
import kz.tamur.guidesigner.reports.ReportRecord;
import kz.tamur.guidesigner.users.CreateUserPanel;
import kz.tamur.guidesigner.users.UserTree;
import kz.tamur.lang.parser.LangUtils;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.PopupTableFinder;
import kz.tamur.rt.adapters.ReportPrinterAdapter;
import kz.tamur.rt.adapters.UIFrame;
import kz.tamur.util.ExpressionEditor;
import kz.tamur.util.LangItem;

import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import static java.awt.GridBagConstraints.*;
import static kz.tamur.comps.Constants.INSETS_4;

/**
 * Данный класс создан на основе {@link kz.tamur.guidesigner.DesignerDialog} класс является копией и его методы должны быть зеркальны вышеуказанному классу, за исключением того, что данный класс является наследником <code>JFrame</code>
 * 
 * Фрейм модальный!
 * 
 * @author Sergey Lebedev
 * 
 */
public class DesignerModalFrame extends JFrame implements ActionListener, AWTEventListener, InvocationHandler {

    ResourceBundle resource = ResourceBundle.getBundle(Constants.NAME_RESOURCES, new Locale("ru"));
    private JToolBar printToolBar = kz.tamur.comps.Utils.createDesignerToolBar();

    protected JButton okBtn = ButtonsFactory.createDialogButton(ButtonsFactory.BUTTON_OK, true);
    protected JButton cancelBtn = ButtonsFactory.createDialogButton(ButtonsFactory.BUTTON_CANCEL);
    protected JButton clearBtn = ButtonsFactory.createDialogButton(ButtonsFactory.BUTTON_CLEAR);
    protected JButton editBtn = ButtonsFactory.createDialogButton(ButtonsFactory.BUTTON_EDIT);
    protected JButton defBtn = ButtonsFactory.createDialogButton(ButtonsFactory.BUTTON_DEFAULT);
    protected JButton cancelFilterBtn = ButtonsFactory.createDialogButton(ButtonsFactory.BUTTON_CANCEL_FILTER);
    protected JButton toBckBtn = ButtonsFactory.createDialogButton(ButtonsFactory.BUTTON_TO_BACKGROUND);
    protected JButton printBtn = ButtonsFactory.createToolButton("ReportPrinter", "Отчёты");
    protected JLabel emptyLab = new JLabel();
    protected JCheckBox showCheckBox = Utils.createCheckBox("Не показывать при следующем запуске", false);

    private JPanel buttonsPanel = new JPanel(new GridBagLayout());

    protected int dialogResult = ButtonsFactory.BUTTON_CANCEL;

    private Component content;
    private int type = -1;
    private boolean hasClearBtn = false;
    private boolean hasDefaultBtn = false;
    private boolean hasEditBtn = false;
    private boolean hasCancelBtn = true;
    private boolean isCheckShow = false;
    private boolean hasCancelFilter = false;
    private boolean hasToBckBtn = false;
    private Component initiator;

    private JPopupMenu printerMenu;

    private FrameEventHandler frameEventHandler;
    private UserTree tree;
    private GradientPanel contentPane;
    protected boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    protected boolean isRevertModal = false;
    protected Object owner = null;
    protected boolean isButtonsPanel = true;
    
    public DesignerModalFrame(boolean undecorated, Window owner, String title, Component content) {
        super(title);
        setFrameModal(owner);
        this.content = content;
        this.owner = owner;
        setUndecorated(undecorated);
        this.setBackground(new Color(255, 255, 255));
        init();
    }

    public DesignerModalFrame(Window owner, String title, Component content) {
        super(title);
        setFrameModal(owner);
        this.content = content;
        this.owner = owner;
        init();
    }

    public DesignerModalFrame(Frame owner, String title, Component content) {
        super(title);
        setFrameModal(owner);
        this.content = content;
        this.owner = owner;
        init();
    }

    public DesignerModalFrame(Frame owner, String title, Component content, int type) {
        super(title);
        setFrameModal(owner);
        this.content = content;
        this.type = type;
        this.owner = owner;
        init();
    }

    public DesignerModalFrame(Frame owner, String title, Component content, UserTree tree) {
        super(title);
        setFrameModal(owner);
        this.content = content;
        this.tree = tree;
        this.owner = owner;
        init();
    }

    public DesignerModalFrame(Dialog owner, String title, Component content) {
        super(title);
        if (owner.isModal()) {
        	Window parent = owner.getOwner();
            while (parent instanceof JDialog) {
            	((JDialog)parent).setModal(false);
            	parent = ((JDialog)parent).getOwner();
            }
            owner.setModal(false);
            isRevertModal = true;
        }
        setFrameModal(owner);
        this.content = content;
        this.owner = owner;
        init();
    }

    public DesignerModalFrame(Frame owner, String title, boolean hasCancelBtn, Component content) {
        super(title);
        setFrameModal(owner);
        this.content = content;
        this.owner = owner;
        this.hasCancelBtn = hasCancelBtn;
        init();
    }

    public DesignerModalFrame(Frame owner, String title, Component content, boolean hasClearBtn) {
        super(title);
        setFrameModal(owner);
        this.content = content;
        this.hasClearBtn = hasClearBtn;
        this.owner = owner;
        init();
    }

    public DesignerModalFrame(Dialog owner, String title, Component content, boolean hasClearBtn) {
        super(title);
        if (owner.isModal()) {
        	Window parent = owner.getOwner();
            while (parent instanceof JDialog) {
            	((JDialog)parent).setModal(false);
            	parent = ((JDialog)parent).getOwner();
            }
            owner.setModal(false);
            isRevertModal = true;
        }
        setFrameModal(owner);
        this.content = content;
        this.hasClearBtn = hasClearBtn;
        this.owner = owner;
        init();
        
    }

    public DesignerModalFrame(Frame owner, String title, Component content, boolean hasClearBtn, boolean hasDefaultBtn) {
        super(title);
        setFrameModal(owner);
        this.content = content;
        this.hasClearBtn = hasClearBtn;
        this.hasDefaultBtn = hasDefaultBtn;
        this.owner = owner;
        init();
    }

    public DesignerModalFrame(Dialog owner, String title, Component content, boolean hasClearBtn, boolean hasDefaultBtn) {
        super(title);
        setFrameModal(owner);
        this.content = content;
        this.hasClearBtn = hasClearBtn;
        this.hasDefaultBtn = hasDefaultBtn;
        this.owner = owner;
        init();
    }

    public DesignerModalFrame(Frame owner, String title, Component content, boolean hasClearBtn, boolean hasDefaultBtn,
            boolean hasEditBtn) {
        super(title);
        setFrameModal(owner);
        this.content = content;
        this.hasClearBtn = hasClearBtn;
        this.hasDefaultBtn = hasDefaultBtn;
        this.hasEditBtn = hasEditBtn;
        this.owner = owner;
        init();
    }

    public DesignerModalFrame(Dialog owner, String title, Component content, boolean hasClearBtn, boolean hasDefaultBtn,
            boolean hasEditBtn) {
        super(title);
        if (owner.isModal()) {
        	Window parent = owner.getOwner();
            while (parent instanceof JDialog) {
            	((JDialog)parent).setModal(false);
            	parent = ((JDialog)parent).getOwner();
            }
            owner.setModal(false);
            isRevertModal = true;
        }
        setFrameModal(owner);
        this.content = content;
        this.hasClearBtn = hasClearBtn;
        this.hasDefaultBtn = hasDefaultBtn;
        this.hasEditBtn = hasEditBtn;
        this.owner = owner;
        init();
    }

    public DesignerModalFrame(Dialog owner, String title, Component content, boolean hasClearBtn, boolean hasDefaultBtn,
            boolean hasEditBtn, boolean isCheckShow) {
        super(title);
        if (owner.isModal()) {
        	Window parent = owner.getOwner();
            while (parent instanceof JDialog) {
            	((JDialog)parent).setModal(false);
            	parent = ((JDialog)parent).getOwner();
            }
            owner.setModal(false);
            isRevertModal = true;
        }
        setFrameModal(owner);
        this.content = content;
        this.hasClearBtn = hasClearBtn;
        this.hasDefaultBtn = hasDefaultBtn;
        this.hasEditBtn = hasEditBtn;
        this.isCheckShow = isCheckShow;
        this.owner = owner;
        init();
    }

    public DesignerModalFrame(Frame owner, String title, Component content, boolean hasClearBtn, boolean hasDefaultBtn,
            boolean hasEditBtn, boolean isCheckShow) {
        super(title);
        setFrameModal(owner);
        this.content = content;
        this.hasClearBtn = hasClearBtn;
        this.hasDefaultBtn = hasDefaultBtn;
        this.hasEditBtn = hasEditBtn;
        this.isCheckShow = isCheckShow;
        this.owner = owner;
        init();
    }

    public DesignerModalFrame(Dialog owner, String title, Component content, boolean hasClearBtn, boolean hasDefaultBtn,
            boolean hasEditBtn, boolean isCheckShow, boolean hasCancelFilter) {
        super(title);
        if (owner.isModal()) {
        	Window parent = owner.getOwner();
            while (parent instanceof JDialog) {
            	((JDialog)parent).setModal(false);
            	parent = ((JDialog)parent).getOwner();
            }
            owner.setModal(false);
            isRevertModal = true;
        }
        setFrameModal(owner);
        this.content = content;
        this.hasClearBtn = hasClearBtn;
        this.hasDefaultBtn = hasDefaultBtn;
        this.hasEditBtn = hasEditBtn;
        this.isCheckShow = isCheckShow;
        this.hasCancelFilter = hasCancelFilter;
        this.owner = owner;
        init();
    }

    public DesignerModalFrame(Frame owner, String title, Component content, boolean hasClearBtn, boolean hasDefaultBtn,
            boolean hasEditBtn, boolean isCheckShow, boolean hasCancelFilter) {
        super(title);
        setFrameModal(owner);
        this.content = content;
        this.hasClearBtn = hasClearBtn;
        this.hasDefaultBtn = hasDefaultBtn;
        this.hasEditBtn = hasEditBtn;
        this.isCheckShow = isCheckShow;
        this.hasCancelFilter = hasCancelFilter;
        this.owner = owner;
        init();
    }

    public DesignerModalFrame(Frame owner, String title, Component content, boolean hasClearBtn, boolean hasDefaultBtn,
            boolean hasEditBtn, boolean isCheckShow, boolean hasCancelFilter, boolean hasToBckBtn) {
        super(title);
        setFrameModal(owner);
        this.content = content;
        this.hasClearBtn = hasClearBtn;
        this.hasDefaultBtn = hasDefaultBtn;
        this.hasEditBtn = hasEditBtn;
        this.isCheckShow = isCheckShow;
        this.hasCancelFilter = hasCancelFilter;
        this.hasToBckBtn = hasToBckBtn;
        this.owner = owner;
        init();
    }

    void init() {
        if (owner instanceof DesignerModalFrame) {
            ((DesignerModalFrame) owner).stopModal();
            isRevertModal = true;
        }
        setIconImage(kz.tamur.rt.Utils.getImageIcon("aboutS").getImage());
        contentPane = new GradientPanel();
        if (!MainFrame.GRADIENT_MAIN_FRAME.isEmpty()) {
            contentPane.setGradient(MainFrame.GRADIENT_MAIN_FRAME);
        } else {
            contentPane.setGradient(Constants.GLOBAL_DEF_GRADIENT);
        }
        // поместить основную панель
        super.getContentPane().add(contentPane, BorderLayout.CENTER);
        buttonsPanel.setOpaque(!MainFrame.TRANSPARENT_DIALOG);
        if (content instanceof JComponent) {
            ((JComponent) content).setOpaque(!MainFrame.TRANSPARENT_DIALOG);
        }
        // Глобальная прослушка событий
        Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.KEY_EVENT_MASK);
        // setUndecorated(true);
        // getRootPane().setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        getRootPane().setDefaultButton(okBtn);
        if (content instanceof OrPanel) {
            OrButton button = ((OrPanel) content).getDefaultButton();
            if (button instanceof JButton)
                getRootPane().setDefaultButton((JButton) button);
        }
        okBtn.addActionListener(this);
        cancelBtn.addActionListener(this);
        clearBtn.addActionListener(this);
        defBtn.addActionListener(this);
        editBtn.addActionListener(this);
        cancelFilterBtn.addActionListener(this);
        toBckBtn.addActionListener(this);
        if (hasCancelFilter) {
            buttonsPanel.add(cancelFilterBtn, new GridBagConstraints(0, 0, 2, 1, 0, 0, WEST, NONE, INSETS_4, 0, 0));
            cancelFilterBtn.setPreferredSize(new Dimension(300, 30));
            buttonsPanel.add(emptyLab, new GridBagConstraints(1, 0, 1, 1, 6, 0, CENTER, BOTH, INSETS_4, 0, 0));
            buttonsPanel.add(okBtn, new GridBagConstraints(2, 0, 1, 1, 0, 0, CENTER, NONE, INSETS_4, 0, 0));
            buttonsPanel.add(cancelBtn, new GridBagConstraints(3, 0, 1, 1, 0, 0, CENTER, NONE, INSETS_4, 0, 0));
        } else {
            if (!isCheckShow) {
                if (hasDefaultBtn) {
                    buttonsPanel.add(defBtn, new GridBagConstraints(0, 0, 1, 1, 0, 0, CENTER, NONE, INSETS_4, 0, 0));
                    buttonsPanel.add(emptyLab, new GridBagConstraints(1, 0, 1, 1, 6, 0, CENTER, BOTH, INSETS_4, 0, 0));
                    buttonsPanel.add(okBtn, new GridBagConstraints(2, 0, 1, 1, 0, 0, CENTER, NONE, INSETS_4, 0, 0));
                    buttonsPanel.add(cancelBtn, new GridBagConstraints(3, 0, 1, 1, 0, 0, CENTER, NONE, INSETS_4, 0, 0));
                } else {
                    if (!hasClearBtn) {
                        buttonsPanel.add(emptyLab, new GridBagConstraints(0, 0, 1, 1, 7, 0, CENTER, BOTH, INSETS_4, 0, 0));
                        buttonsPanel.add(okBtn, new GridBagConstraints(1, 0, 1, 1, 0, 0, CENTER, NONE, INSETS_4, 0, 0));
                        if (hasCancelBtn) {
                            buttonsPanel.add(cancelBtn, new GridBagConstraints(2, 0, 1, 1, 0, 0, CENTER, NONE, INSETS_4, 0, 0));
                        }
                        if (hasEditBtn) {
                            buttonsPanel.add(editBtn, new GridBagConstraints(3, 0, 1, 1, 0, 0, CENTER, NONE, INSETS_4, 0, 0));
                        }
                        if (hasToBckBtn) {
                            toBckBtn.setPreferredSize(new Dimension(150, 30));
                            buttonsPanel.add(toBckBtn, new GridBagConstraints(4, 0, 1, 1, 0, 0, CENTER, NONE, INSETS_4, 0, 0));
                        }
                    } else {
                        buttonsPanel.add(clearBtn, new GridBagConstraints(1, 0, 1, 1, 0, 0, CENTER, NONE, INSETS_4, 0, 0));
                        buttonsPanel.add(emptyLab, new GridBagConstraints(2, 0, 1, 1, 2, 0, CENTER, HORIZONTAL, INSETS_4, 0, 0));
                        buttonsPanel.add(okBtn, new GridBagConstraints(3, 0, 1, 1, 0, 0, CENTER, NONE, INSETS_4, 0, 0));
                        buttonsPanel.add(cancelBtn, new GridBagConstraints(4, 0, 1, 1, 0, 0, CENTER, NONE, INSETS_4, 0, 0));
                        if (hasEditBtn) {
                            buttonsPanel.add(editBtn, new GridBagConstraints(5, 0, 1, 1, 0, 0, CENTER, NONE, INSETS_4, 0, 0));
                        }
                    }
                }
            } else {
                buttonsPanel.add(showCheckBox, new GridBagConstraints(0, 0, 1, 1, 0, 0, CENTER, NONE, INSETS_4, 0, 0));
                buttonsPanel.add(emptyLab, new GridBagConstraints(1, 0, 1, 1, 6, 0, CENTER, BOTH, INSETS_4, 0, 0));
                buttonsPanel.add(okBtn, new GridBagConstraints(2, 0, 1, 1, 0, 0, CENTER, NONE, INSETS_4, 0, 0));
                showCheckBox.setSelected(!Or3Frame.isQuickStartShow);
            }
        }
        Container cont = getContentPane();
        cont.setLayout(new BorderLayout());
        // titleBar = new Or3DialogTitleBar(this, getTitle());
        // cont.add(titleBar, BorderLayout.NORTH);
        cont.add(content, BorderLayout.CENTER);
        if (isButtonsPanel) {
            cont.add(buttonsPanel, BorderLayout.SOUTH);
        }
        if (!(content instanceof OrPanel)) {
            pack();
        } else {
            setOrSize();
        }
        setLocation(kz.tamur.comps.Utils.getCenterLocationPoint(getSize()));
        if (content instanceof OrPanel && printerMenu == null) {
            OrPanel panel = (OrPanel) content;
            OrFrame frame = panel.getOrFrame();
            if (frame instanceof UIFrame) {
                ReportRecord root = ((UIFrame) frame).getRootReport();
                if (root != null && root.getChildren().size() > 0) {
                    printerMenu = new JPopupMenu();
                    loadReports(root, printerMenu, (UIFrame) frame);
                    printBtn.addActionListener(this);
                    printToolBar.add(new JLabel(kz.tamur.rt.Utils.getImageIcon("decor")));
                    printToolBar.add(printBtn);
                    cont.add(printToolBar, BorderLayout.NORTH);
                }
            }
        }
    }

    
    @Override
    public Container getContentPane() {
        return contentPane;
    }

    private void loadReports(ReportRecord parent, Object menu, UIFrame frame) {
        List<ReportRecord> records = parent.getChildren();
        for (ReportRecord record : records) {
            if (record.isFolder()) {
                String name = record.getName(frame);
                ReportMenu subMenu = new ReportMenu(name, record, true);
                loadReports(record, subMenu, frame);
                if (menu instanceof JMenu)
                    ((JMenu) menu).add(subMenu);
                else
                    ((JPopupMenu) menu).add(subMenu);
            } else {
                ReportPrinter rp = new ReportPrinterAdapter(frame, frame.getPanel(), record);

                JMenuItem ri = null;
                List langItems = LangItem.getAll();
                int reportsCount = 0;
                LangItem existLang = null;
                for (int i = 0; i < langItems.size(); i++) {
                    LangItem li = (LangItem) langItems.get(i);
                    if (rp.hasReport(li.obj)) {
                        existLang = li;
                        reportsCount++;
                    }
                }

                ri = reportsCount == 1 ? new PrinterMenuItem(rp, true, existLang) : new PrinterMenu(rp, true);
                if (menu instanceof JMenu)
                    ((JMenu) menu).getPopupMenu().add(ri);
                else
                    ((JPopupMenu) menu).add(ri);
            }
        }

    }

    public void show() {
        if (content instanceof ClassBrowser) {
            ((ClassBrowser) content).setSplitLocation();
        } else if (content instanceof ExpressionEditor) {
            ((ExpressionEditor) content).focusEditor();
        }
        super.show();
        startModal();
    }

    private void setOrSize() {
        OrPanel panel = (OrPanel) content;
        Dimension sz = panel.getPrefSize();
        if (sz != null) {
            int width = sz.width;
            int height = sz.height;
            if (width > 0 && height > 0) {
                pack();
                Dimension bSize = buttonsPanel.getSize();
                setSize(width, height + bSize.height);
                validate();
            }
        }
    }

    public void setOnlyOkButton() {
        cancelBtn.setVisible(false);
        cancelBtn.setText("OK");
    }

    public void hideOkButton() {
        okBtn.setVisible(false);
    }

    public void processOkClicked() {
        okBtn.doClick();
    }

    public void processCancelClicked() {
        cancelBtn.doClick();
    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == okBtn) {
            boolean disp = true;
            dialogResult = ButtonsFactory.BUTTON_OK;
            if (isCheckShow) {
                if (content == Or3Frame.quickStart) {
                    try {
                        Properties props = new Properties();
                        File dir = new File(Utils.getUserWorkingDir());
                        dir.mkdirs();

                        File f = new File(dir, "propsJboss");
                        FileInputStream fis = new FileInputStream(f);
                        props.load(fis);
                        fis.close();
                        FileOutputStream fos = new FileOutputStream(f);
                        Or3Frame.isQuickStartShow = !showCheckBox.isSelected();
                        props.setProperty("quickstart", (showCheckBox.isSelected()) ? "0" : "1");
                        props.store(fos, "Properties");
                        fos.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }

            if (frameEventHandler != null) {
                if (!frameEventHandler.checkConstraints(this)) {
                    return;
                }
            }

            if (content instanceof CreateUserPanel) {
                CreateUserPanel rp = (CreateUserPanel) content;
                String userName = rp.getText();

                UserTree.UserTreeModel model = (UserTree.UserTreeModel) tree.getModel();
                if (rp.isFolder()) {
                    try {
                        disp = (model.createFolderNode(userName) != null);
                    } catch (KrnException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    try {
                        disp = (model.createChildNode(userName, rp.getPD(), rp.isAdmin()) != null);
                    } catch (KrnException e1) {
                        e1.printStackTrace();
                    }
                }
                validate();
                repaint();
            }

            if (disp) {
                dispose();
            }
        } else if (src == clearBtn) {
            dialogResult = ButtonsFactory.BUTTON_CLEAR;
            dispose();
        } else if (src == editBtn) {
            dialogResult = ButtonsFactory.BUTTON_EDIT;
            dispose();
        } else if (src == defBtn) {
            dialogResult = ButtonsFactory.BUTTON_DEFAULT;
            dispose();
        } else if (src == printBtn) {
            printerMenu.show(printBtn, 0, printBtn.getHeight());
        } else if (src instanceof PrinterLangItem) {
            PrinterLangItem pi = (PrinterLangItem) src;
            PrinterMenu pm = pi.getPrinterMenu();
            pi.setBackground(Utils.getLightGraySysColor());
            long time = System.currentTimeMillis();
            pm.getPrinter().print(pi.getLanguage());
            System.out.println("Report time: " + (System.currentTimeMillis() - time));
        } else if (src instanceof PrinterMenuItem) {
            PrinterMenuItem pmi = (PrinterMenuItem) src;
            if (pmi.getLanguage() != null) {
                pmi.setBackground(Utils.getLightGraySysColor());
                long time = System.currentTimeMillis();
                pmi.getPrinter().print(pmi.getLanguage());
                System.out.println("Report time: " + (System.currentTimeMillis() - time));
            }
        } else if (src instanceof PrinterItem) {
            long time = System.currentTimeMillis();
            ((PrinterItem) e.getSource()).getPrinter().print();
            System.out.println("Report time: " + (System.currentTimeMillis() - time));
        } else if (src == cancelFilterBtn) {
            dialogResult = ButtonsFactory.BUTTON_CANCEL_FILTER;
            dispose();
        } else if (src == toBckBtn) {
            dialogResult = ButtonsFactory.BUTTON_TO_BACKGROUND;
            dispose();
        } else {
            dialogResult = ButtonsFactory.BUTTON_CANCEL;
            dispose();
        }
    }

    public void eventDispatched(AWTEvent event) {
        KeyEvent ke = (KeyEvent) event;
        if (ke.getID() == KeyEvent.KEY_PRESSED) {// обработка нажатия кнопки
            if (ke.getKeyCode() == KeyEvent.VK_ESCAPE) {// обработка нажатия
                                                        // клавиши ESC
                if (this.isActive()) {// закрываем только активные диалоги
                    Component comp = this.content;
                    if (comp != null) {
                        // Запретить закрытие редактора формул по ESCAPE
                        if (comp instanceof ExpressionEditor) {
                            return;
                        }
                    }
                    processCancelClicked();
                }
            }
        }
    }

    public int getResult() {
        return dialogResult;
    }

    public void setOkEnabled(boolean isEnabled) {
        okBtn.setEnabled(isEnabled);
    }

    public void setOkVisible(boolean isEnabled) {
        okBtn.setVisible(isEnabled);
    }

    public void setOkText(String text) {
        okBtn.setText(text);
    }

    public void setDialogResult(int dialogResult) {
        this.dialogResult = dialogResult;
    }

    public void setFirstRow(UIFrame farme) {
        OrPanel pane = (OrPanel) farme.getPanel();
        Component[] children = pane.getComponents();
        for (int i = 0; i < children.length; i++) {
            Object o = children[i];
            if (o instanceof OrTable) {
                JTable table = ((OrTable) o).getJTable();
                if (table.getSelectedRow() == -1) {
                    if (initiator == null || !(initiator instanceof OrHyperPopup)) {
                        table.getSelectionModel().setSelectionInterval(0, 0);
                        ((OrTable) o).requestFocusInWindow();
                        break;
                    } else if (initiator instanceof OrHyperPopup) {
                        PopupTableFinder finder = new PopupTableFinder(initiator, table);
                        finder.find();
                        ((OrTable) o).requestFocusInWindow();
                        break;
                    }
                }
            }
        }
    }

    public void setInitiator(Component comp) {
        this.initiator = comp;
    }

    public void setOkButtonActionListener(ActionListener l) {
        okBtn.removeActionListener(this);
        okBtn.addActionListener(l);
    }

    public void setLanguage(long lid) {
        List list = LangItem.getAll();
        LangItem li = null;
        for (int i = 0; i < list.size(); i++) {
            LangItem l = (LangItem) list.get(i);
            if (l.obj.id == lid) {
                li = l;
                break;
            }
        }
        if (li != null) {
            resource = ResourceBundle.getBundle(Constants.NAME_RESOURCES, new Locale("KZ".equals(li.code) ? "kk" : "ru"));
            refreshButtonTitles(resource);
        }
    }

    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING && hasCancelBtn) {
            cancelBtn.doClick();
        } else {
            super.processWindowEvent(e);
        }
    }

    private void refreshButtonTitles(ResourceBundle res) {
        okBtn.setText(res.getString("ok"));
        cancelBtn.setText(res.getString("cancel"));
        clearBtn.setText(res.getString("clear"));
        editBtn.setText(res.getString("edit"));
        cancelFilterBtn.setText(res.getString("cancelApplyFilter"));
    }

    private class PrinterItem extends JMenuItem {
        private ReportPrinter p_;

        public PrinterItem(ReportPrinter p) {
            super(p.toString());
            p_ = p;
            setFont(Utils.getDefaultFont());
        }

        public ReportPrinter getPrinter() {
            return p_;
        }
    }

    public void setClearBtnText(String text) {
        clearBtn.setText(text);
    }

    public void setClearBtnActionListener(ActionListener l) {
        clearBtn.addActionListener(l);
    }

    public void setCancelBtnText(String text) {
        cancelBtn.setText(text);
    }

    public void setCancelBtnActionListener(ActionListener l) {
    	cancelBtn.addActionListener(l);
    }
    
    class ReportMenu extends JMenu {
        private ReportRecord record;

        public ReportMenu(String s, ReportRecord r) {
            super(s);
            setFont(Utils.getDefaultFont());
            record = r;
        }

        public ReportMenu(String s, ReportRecord r, boolean submenu) {
            super(s);
            setFont(Utils.getDefaultFont());
            record = r;
            if (submenu) {
                setForeground(Utils.getDarkShadowSysColor());
            }
        }

        public void changeTitle() {
            if (content instanceof OrPanel) {
                OrPanel panel = (OrPanel) content;
                UIFrame frame = (UIFrame) panel.getOrFrame();
                setText(record.getName(frame));
            }
        }
    }

    class PrinterMenu extends JMenu {
        private ReportPrinter p_;

        public PrinterMenu(ReportPrinter p) {
            super(p.toString());
            setFont(Utils.getDefaultFont());
            p_ = p;
        }

        public PrinterMenu(ReportPrinter p, boolean submenu) {
            super(p.toString());
            setFont(Utils.getDefaultFont());
            p_ = p;
            if (submenu) {
                setForeground(Utils.getDarkShadowSysColor());
            }

            List langItems = LangItem.getAll();
            for (int i = 0; i < langItems.size(); i++) {
                LangItem li = (LangItem) langItems.get(i);
                if (p.hasReport(li.obj)) {
                    PrinterLangItem pi = new PrinterLangItem(this, li);
                    pi.addActionListener(DesignerModalFrame.this);
                    add(pi);
                }
            }
        }

        public void changeTitle() {
            setText(p_.toString());
        }

        public void changeSelection() {
            // To change body of created methods use File | Settings | File
            // Templates.
        }

        public ReportPrinter getPrinter() {
            return p_;
        }
    }

    class PrinterMenuItem extends JMenuItem {
        private ReportPrinter p_;
        private LangItem langItem;

        public PrinterMenuItem(ReportPrinter p, boolean submenu, LangItem li) {
            super(p.toString());
            setFont(Utils.getDefaultFont());
            p_ = p;
            langItem = li;
            if (submenu) {
                setForeground(Utils.getDarkShadowSysColor());
            }

            addActionListener(DesignerModalFrame.this);
        }

        public void changeTitle() {
            setText(p_.toString());
        }

        public void changeSelection() {
        }

        public ReportPrinter getPrinter() {
            return p_;
        }

        public KrnObject getLanguage() {
            return langItem.obj;
        }
    }

    private class PrinterLangItem extends JMenuItem {
        private LangItem langItem;
        PrinterMenu pm;

        public PrinterLangItem(PrinterMenu pm, LangItem item) {
            super(item.name, item.icon);
            this.pm = pm;
            langItem = item;
            setFont(Utils.getDefaultFont());
        }

        public LangItem getLangItem() {
            return langItem;
        }

        public KrnObject getLanguage() {
            return langItem.obj;
        }

        public PrinterMenu getPrinterMenu() {
            return pm;
        }
    }

    public void setDialogEventHandler(FrameEventHandler frameEventHandler) {
        this.frameEventHandler = frameEventHandler;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return this.isShowing() ? Boolean.TRUE : Boolean.FALSE;
    }

    
    @Override
    public void setVisible(final boolean isVisible) {
            try {
                javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
                    public void run() {
                        if (isVisible) {
                            if (content instanceof ClassBrowser) {
                                ((ClassBrowser) content).setSplitLocation();
                            } else if (content instanceof ExpressionEditor) {
                                ((ExpressionEditor) content).focusEditor();
                            }
                        }
                        DesignerModalFrame.super.setVisible(isVisible);
                        if (isVisible) {
                            try {
                                start();
                            } catch (Throwable throwable) {
                                throw new RuntimeException(throwable);
                            }
                        }
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

    }

    /**
     * when the reflection calls in this method has to be replaced once Sun provides a public API to pump events.
     * 
     * @throws Exception
     */
    public void start() throws Exception {
        Class<?> clazz = LangUtils.getType("java.awt.Conditional", null);
        Object conditional = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] { clazz }, this);
        Method pumpMethod = LangUtils.getType("java.awt.EventDispatchThread", null).getDeclaredMethod("pumpEvents", new Class[] { clazz });
        pumpMethod.setAccessible(true);
        pumpMethod.invoke(Thread.currentThread(), new Object[] { conditional });
    }

    /**
     * Установка предка фрейма для отображения фрема модальным к предку
     * 
     * @param owner
     */
    private void setFrameModal(final Frame owner) {
        addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e) {
                owner.setEnabled(false);
            }

            public void windowClosed(WindowEvent e) {
                owner.setEnabled(true);
                removeWindowListener(this);
            }
        });

        owner.addWindowListener(new WindowAdapter() {
            public void windowActivated(WindowEvent e) {
                if (isShowing()) {
                    setExtendedState(JFrame.NORMAL);
                    toFront();
                } else
                    owner.removeWindowListener(this);
            }
        });
    }

    /**
     * Установка предка фрейма для отображения фрема модальным к предку
     * 
     * @param owner
     */
    private void setFrameModal(final Window owner) {
        addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e) {
                owner.setEnabled(false);
            }

            public void windowClosed(WindowEvent e) {
                owner.setEnabled(true);
                removeWindowListener(this);
            }
        });

        owner.addWindowListener(new WindowAdapter() {
            public void windowActivated(WindowEvent e) {
                if (isShowing()) {
                    setExtendedState(JFrame.NORMAL);
                    toFront();
                } else
                    owner.removeWindowListener(this);
            }
        });
    }

    /**
     * Установка предка фрейма для отображения фрема модальным к предку
     * 
     * @param owner
     */
    private void setFrameModal(final Dialog owner) {
        addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e) {
                owner.setEnabled(false);
            }

            public void windowClosed(WindowEvent e) {
                owner.setEnabled(true);
                removeWindowListener(this);
            }
        });

        owner.addWindowListener(new WindowAdapter() {
            public void windowActivated(WindowEvent e) {
                if (isShowing()) {
                    setExtendedState(JFrame.NORMAL);
                    toFront();
                } else
                    owner.removeWindowListener(this);
            }
        });
    }

    public /*synchronized*/ void startModal() {
        try {
            if (SwingUtilities.isEventDispatchThread()) {
                EventQueue theQueue = getToolkit().getSystemEventQueue();
                while (isVisible()) {
                    AWTEvent event = theQueue.getNextEvent();
                    Object src = event.getSource();
                    if (event instanceof ActiveEvent) {
                        ((ActiveEvent) event).dispatch();
                    } else if (src instanceof Component) {
                        ((Component) src).dispatchEvent(event);
                    } else if (src instanceof MenuComponent) {
                        ((MenuComponent) src).dispatchEvent(event);
                    } else {
                        System.err.println("unable to dispatch event: " + event);
                    }
                }
            } else {
                while (isVisible()) {
                    wait();
                }
            }
        } catch (InterruptedException e) {
        }
    }

    public synchronized void stopModal() {
        notifyAll();
    }

    public synchronized void stopModal(Component focusedComponent) {
        notifyAll();
        focusedComponent.requestFocus();
    }

    public void dispose() {
        stopModal();
        super.dispose();
        /*
         * Если данный фрейм был открыт с модального окна, которому была убрана модальность для
         * отображения текущего фрейма - то необходимо возвратить модальность предку
         */
        if (isRevertModal && owner instanceof JDialog) {
            ((JDialog) owner).setModal(true);
        	Window parent = ((JDialog) owner).getOwner();
            while (parent instanceof JDialog) {
            	((JDialog)parent).setModal(true);
            	parent = ((JDialog)parent).getOwner();
            }
            ((JDialog) owner).setVisible(true);
            isRevertModal = false;
        } else if (owner instanceof JFrame) {
            ((JFrame) owner).setVisible(true);
        } else if (owner instanceof DesignerModalFrame) {
            ((DesignerModalFrame) owner).startModal();
            isRevertModal = false;
        }
    }
}
