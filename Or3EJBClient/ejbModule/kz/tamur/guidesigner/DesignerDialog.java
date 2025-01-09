package kz.tamur.guidesigner;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.JViewport;

import kz.tamur.Or3Frame;
import kz.tamur.admin.ClassBrowser;
import kz.tamur.comps.Constants;
import kz.tamur.comps.OrButton;
import kz.tamur.comps.OrFrame;
import kz.tamur.comps.OrHyperPopup;
import kz.tamur.comps.OrPanel;
import kz.tamur.comps.OrTable;
import kz.tamur.comps.ui.GradientPanel;
import kz.tamur.guidesigner.reports.ReportPrinter;
import kz.tamur.guidesigner.reports.ReportPanel;
import kz.tamur.guidesigner.reports.ReportRecord;
import kz.tamur.guidesigner.users.CreateUserPanel;
import kz.tamur.guidesigner.users.UserTree;
import kz.tamur.or3.client.props.Expression;
import kz.tamur.or3.client.props.inspector.BoxOrExprEditorDelegate;
import kz.tamur.or3.client.props.inspector.EditorDelegate;
import kz.tamur.or3.client.props.inspector.EditorDelegateSet;
import kz.tamur.or3.client.props.inspector.ExprEditorDelegate;
import kz.tamur.or3.client.props.inspector.HTMLEditorDelegate;
import kz.tamur.or3.client.props.inspector.TreeOrExprEditorDelegate;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.PopupTableFinder;
import kz.tamur.rt.adapters.ReportPrinterAdapter;
import kz.tamur.rt.adapters.UIFrame;
import kz.tamur.util.ExpressionCellEditor;
import kz.tamur.util.ExpressionEditor;
import kz.tamur.util.LangItem;
import kz.tamur.rt.Utils;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;

import static java.awt.GridBagConstraints.*;
import static kz.tamur.comps.Constants.INSETS_4;

/**
 * Created by IntelliJ IDEA. User: Vital Date: 07.05.2004 Time: 9:49:37 To
 * change this template use File | Settings | File Templates.
 */
public class DesignerDialog extends JDialog implements ActionListener, AWTEventListener {

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
    protected JLabel emptyLab = kz.tamur.rt.Utils.createLabel("");
    public JLabel getEmptyLab() {
		return emptyLab;
	}

	protected JCheckBox showCheckBox = Utils.createCheckBox("Не показывать при следующем запуске", false);

    private JPanel buttonsPanel = new JPanel(new GridBagLayout());

    private int dialogResult = ButtonsFactory.BUTTON_CANCEL;

    private Component content;
    private int type = -1;
    private boolean hasOkBtn = true;
    private boolean hasClearBtn = false;
    private boolean hasDefaultBtn = false;
    private boolean hasEditBtn = false;
    private boolean hasCancelBtn = true;
    private boolean isCheckShow = false;
    private boolean hasCancelFilter = false;
    private boolean hasToBckBtn = false;
    private boolean disposeOnClear = false;
    private Component initiator;

    private JPopupMenu printerMenu;

    private DialogEventHandler dialogEventHandler;
    private UserTree tree;
    private GradientPanel contentPane;
    static boolean isOpaque = true;
    static {
        if (Kernel.instance().getUser() != null) {
            isOpaque = !MainFrame.TRANSPARENT_DIALOG;
        }
    }

    public DesignerDialog(boolean undecorated, Window owner, String title, Component content) {
        super(owner, title, DEFAULT_MODALITY_TYPE);
        this.content = content;
        setUndecorated(undecorated);
        this.setBackground(Color.WHITE);
        init();
    }

    public DesignerDialog(Window owner, String title, Component content) {
        super(owner, title, DEFAULT_MODALITY_TYPE);
        this.content = content;
        if (content instanceof ReportPanel) {
        	this.hasOkBtn = false;
        }
        init();
    }

    public DesignerDialog(Frame owner, String title, Component content) {
        super(owner, title, true);
        this.content = content;
        init();
    }

    public DesignerDialog(Frame owner, String title, Component content, int type) {
        super(owner, title, true);
        this.content = content;
        this.type = type;
        init();
    }

    public DesignerDialog(Frame owner, String title, Component content, UserTree tree) {
        super(owner, title, true);
        this.content = content;
        this.tree = tree;
        init();
    }

    public DesignerDialog(Dialog owner, String title, Component content) {
        super(owner, title, true);
        this.content = content;
        init();
    }

    public DesignerDialog(Frame owner, String title, boolean hasCancelBtn, Component content) {
        super(owner, title, true);
        this.content = content;
        this.hasCancelBtn = hasCancelBtn;
        init();
    }

    public DesignerDialog(Frame owner, String title, Component content, boolean hasClearBtn) {
        super(owner, title, true);
        this.content = content;
        this.hasClearBtn = hasClearBtn;
        init();
    }

    public DesignerDialog(Dialog owner, String title, Component content, boolean hasClearBtn) {
        super(owner, title, true);
        this.content = content;
        this.hasClearBtn = hasClearBtn;
        init();
    }

    public DesignerDialog(Frame owner, String title, Component content, boolean hasClearBtn, boolean hasDefaultBtn) {
        super(owner, title, true);
        this.content = content;
        this.hasClearBtn = hasClearBtn;
        this.hasDefaultBtn = hasDefaultBtn;
        init();
    }

    public DesignerDialog(Dialog owner, String title, Component content, boolean hasClearBtn, boolean hasDefaultBtn) {
        super(owner, title, true);
        this.content = content;
        this.hasClearBtn = hasClearBtn;
        this.hasDefaultBtn = hasDefaultBtn;
        init();
    }

    public DesignerDialog(Frame owner, String title, Component content, boolean hasClearBtn, boolean hasDefaultBtn,
            boolean hasEditBtn) {
        super(owner, title, true);
        this.content = content;
        this.hasClearBtn = hasClearBtn;
        this.hasDefaultBtn = hasDefaultBtn;
        this.hasEditBtn = hasEditBtn;
        init();
    }

    public DesignerDialog(Dialog owner, String title, Component content, boolean hasClearBtn, boolean hasDefaultBtn,
            boolean hasEditBtn) {
        super(owner, title, true);
        this.content = content;
        this.hasClearBtn = hasClearBtn;
        this.hasDefaultBtn = hasDefaultBtn;
        this.hasEditBtn = hasEditBtn;
        init();
    }

    public DesignerDialog(Dialog owner, String title, Component content, boolean hasClearBtn, boolean hasDefaultBtn,
            boolean hasEditBtn, boolean isCheckShow) {
        super(owner, title, true);
        this.content = content;
        this.hasClearBtn = hasClearBtn;
        this.hasDefaultBtn = hasDefaultBtn;
        this.hasEditBtn = hasEditBtn;
        this.isCheckShow = isCheckShow;
        init();
    }

    public DesignerDialog(Frame owner, String title, Component content, boolean hasClearBtn, boolean hasDefaultBtn,
            boolean hasEditBtn, boolean isCheckShow) {
        super(owner, title, true);
        this.content = content;
        this.hasClearBtn = hasClearBtn;
        this.hasDefaultBtn = hasDefaultBtn;
        this.hasEditBtn = hasEditBtn;
        this.isCheckShow = isCheckShow;
        init();
    }

    public DesignerDialog(Dialog owner, String title, Component content, boolean hasClearBtn, boolean hasDefaultBtn,
            boolean hasEditBtn, boolean isCheckShow, boolean hasCancelFilter) {
        super(owner, title, true);
        this.content = content;
        this.hasClearBtn = hasClearBtn;
        this.hasDefaultBtn = hasDefaultBtn;
        this.hasEditBtn = hasEditBtn;
        this.isCheckShow = isCheckShow;
        this.hasCancelFilter = hasCancelFilter;
        init();
    }

    public DesignerDialog(Frame owner, String title, Component content, boolean hasClearBtn, boolean hasDefaultBtn,
            boolean hasEditBtn, boolean isCheckShow, boolean hasCancelFilter) {
        super(owner, title, true);
        this.content = content;
        this.hasClearBtn = hasClearBtn;
        this.hasDefaultBtn = hasDefaultBtn;
        this.hasEditBtn = hasEditBtn;
        this.isCheckShow = isCheckShow;
        this.hasCancelFilter = hasCancelFilter;
        init();
    }

    public DesignerDialog(Frame owner, String title, Component content, boolean hasClearBtn, boolean hasDefaultBtn,
            boolean hasEditBtn, boolean isCheckShow, boolean hasCancelFilter, boolean hasToBckBtn) {
        super(owner, title, true);
        this.content = content;
        this.hasClearBtn = hasClearBtn;
        this.hasDefaultBtn = hasDefaultBtn;
        this.hasEditBtn = hasEditBtn;
        this.isCheckShow = isCheckShow;
        this.hasCancelFilter = hasCancelFilter;
        this.hasToBckBtn = hasToBckBtn;
        init();
    }

    void init() {
        setIconImage(kz.tamur.rt.Utils.getImageIcon("aboutS").getImage());
        contentPane = new GradientPanel();
        if (Kernel.instance().getUser()!=null && !MainFrame.GRADIENT_MAIN_FRAME.isEmpty()) {
            contentPane.setGradient(MainFrame.GRADIENT_MAIN_FRAME);
        } else {
            contentPane.setGradient(Constants.GLOBAL_DEF_GRADIENT);
        }
        // поместить основную панель
        super.getContentPane().add(contentPane, BorderLayout.CENTER);
        buttonsPanel.setOpaque(isOpaque);
        if (content instanceof JComponent) {
            ((JComponent) content).setOpaque(isOpaque);
        }
        showCheckBox.setOpaque(isOpaque);
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
			// Добавить кнопку OK
			buttonsPanel.add(okBtn, new GridBagConstraints(2, 0, 1, 1, 0, 0, CENTER, NONE, INSETS_4, 0, 0));
			// Добавить кнопку отмена
			buttonsPanel.add(cancelBtn, new GridBagConstraints(3, 0, 1, 1, 0, 0, CENTER, NONE, INSETS_4, 0, 0));
		} else {
			// если не должна кнопка чек
			if (!isCheckShow) {
				// Необходима кнопка по умолчанию
				if (hasDefaultBtn) {
					buttonsPanel.add(defBtn, new GridBagConstraints(0, 0, 1, 1, 0, 0, CENTER, NONE, INSETS_4, 0, 0));
					buttonsPanel.add(emptyLab, new GridBagConstraints(1, 0, 1, 1, 6, 0, CENTER, BOTH, INSETS_4, 0, 0));
					// Добавить кнопку OK
					buttonsPanel.add(okBtn, new GridBagConstraints(2, 0, 1, 1, 0, 0, CENTER, NONE, INSETS_4, 0, 0));
					// Добавить кнопку отмена
					buttonsPanel.add(cancelBtn, new GridBagConstraints(3, 0, 1, 1, 0, 0, CENTER, NONE, INSETS_4, 0, 0));
				} else {
					// если не должна быть кнопка очистить
					if (!hasClearBtn) {
						// необходима кнопка ок
						if (hasOkBtn) {
							buttonsPanel.add(emptyLab,
									new GridBagConstraints(0, 0, 1, 1, 7, 0, CENTER, BOTH, INSETS_4, 0, 0));
							buttonsPanel.add(okBtn,
									new GridBagConstraints(1, 0, 1, 1, 0, 0, CENTER, NONE, INSETS_4, 0, 0));
						}
						// необходима кнопка отмена
						if (hasCancelBtn) {
							buttonsPanel.add(cancelBtn,
									new GridBagConstraints(2, 0, 1, 1, 0, 0, CENTER, NONE, INSETS_4, 0, 0));
						}
						// необходима кнопка редактировать
						if (hasEditBtn) {
							buttonsPanel.add(editBtn,
									new GridBagConstraints(3, 0, 1, 1, 0, 0, CENTER, NONE, INSETS_4, 0, 0));
						}
						// необходима кнопка "в фоновом режиме"
						if (hasToBckBtn) {
							toBckBtn.setPreferredSize(new Dimension(150, 30));
							buttonsPanel.add(toBckBtn,
									new GridBagConstraints(4, 0, 1, 1, 0, 0, CENTER, NONE, INSETS_4, 0, 0));
						}
						// если должна быть кнопка очистить
					} else {
						// Добавить кнопку очистить
						buttonsPanel.add(clearBtn,
								new GridBagConstraints(1, 0, 1, 1, 0, 0, CENTER, NONE, INSETS_4, 0, 0));
						buttonsPanel.add(emptyLab,
								new GridBagConstraints(2, 0, 1, 1, 2, 0, CENTER, HORIZONTAL, INSETS_4, 0, 0));
						// Добавить кнопку ок
						buttonsPanel.add(okBtn, new GridBagConstraints(3, 0, 1, 1, 0, 0, CENTER, NONE, INSETS_4, 0, 0));
						// Добавить кнопку cancel
						buttonsPanel.add(cancelBtn,
								new GridBagConstraints(4, 0, 1, 1, 0, 0, CENTER, NONE, INSETS_4, 0, 0));
						if (hasEditBtn) {
							// Добавить кнопку редактировать
							buttonsPanel.add(editBtn,
									new GridBagConstraints(5, 0, 1, 1, 0, 0, CENTER, NONE, INSETS_4, 0, 0));
						}
					}
				}
			} else {
				// Добавить чекбокс(флажок)
				buttonsPanel.add(showCheckBox, new GridBagConstraints(0, 0, 1, 1, 0, 0, CENTER, NONE, INSETS_4, 0, 0));
				buttonsPanel.add(emptyLab, new GridBagConstraints(1, 0, 1, 1, 6, 0, CENTER, BOTH, INSETS_4, 0, 0));
				// Добавить кнопку ок
				buttonsPanel.add(okBtn, new GridBagConstraints(2, 0, 1, 1, 0, 0, CENTER, NONE, INSETS_4, 0, 0));
				showCheckBox.setSelected(!Or3Frame.isQuickStartShow);
			}
		}
        Container cont = getContentPane();
        cont.setLayout(new BorderLayout());
        cont.add(content, BorderLayout.CENTER);
        cont.add(buttonsPanel, BorderLayout.SOUTH);
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
    
    public void setMethodAuthor(String author) {
    	emptyLab.setText(author);
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
    }

    public void setNoButton() {
        buttonsPanel.setVisible(false);
        pack();
        validate();
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
            if (content instanceof OrPanel) {
            	((UIFrame) ((OrPanel) content).frame).setUserDecision(dialogResult);
            }
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
            
            if(content instanceof ExpressionEditor) {
            	Object so = ((ExpressionEditor) content).getSourceObject();
            	if(so instanceof EditorDelegate) {
            		EditorDelegate ed = (EditorDelegate)((ExpressionEditor) content).getSourceObject();
            		if (ed instanceof ExprEditorDelegate || ed instanceof BoxOrExprEditorDelegate || ed instanceof HTMLEditorDelegate
            				|| ed instanceof ExpressionCellEditor || ed instanceof TreeOrExprEditorDelegate) {
            			int res = ((ExpressionEditor) content).getErrs();
            			if(res == 1) return;
            		}
            	}
            	if(so instanceof EditorDelegateSet) {
            		EditorDelegateSet eds = (EditorDelegateSet) so;
            		eds.setStringValue(null);
            	}
            }

            if (dialogEventHandler != null) {
                if (!dialogEventHandler.checkConstraints(this)) {
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
            if(content instanceof JScrollPane)
            {
                JViewport viewport =(( JScrollPane)content).getViewport();
                if (viewport !=null) {
                    Object list = viewport.getView();
                    if(list instanceof JList)
                    	((JList)list).clearSelection();
                    else if(list instanceof JTree)
                    	((JTree)list).clearSelection();
                }
                if (disposeOnClear)
                	dispose();
            }
            else{dispose();}
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
            if (content instanceof OrPanel) {
            	((UIFrame) ((OrPanel) content).frame).setUserDecision(dialogResult);
            }
            if(content instanceof ExpressionEditor) {
            	Object so = ((ExpressionEditor) content).getSourceObject();
            	if(so instanceof EditorDelegateSet) {
            		EditorDelegateSet eds = (EditorDelegateSet) so;
                		String val = eds.getStringValue();
                		if(val != null && val.length() > 0) {
                			eds.setExpression(val);
                			eds.setStringValue(null);
                		}
            	}
            }
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

    public void setEditEnabled(boolean isEnabled) {
        editBtn.setEnabled(isEnabled);
    }
    
    public void setEditVisible(boolean visible) {
    	editBtn.setVisible(visible);
    }
    
    public void setOkEnabled(boolean isEnabled) {
        okBtn.setEnabled(isEnabled);
    }

    public void setOkVisible(boolean isVisible) {
        okBtn.setVisible(isVisible);
    }
    
    public void setCancelVisible(boolean isVisible) {
        cancelBtn.setVisible(isVisible);
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

    /*
     * private void updateDefaultButton() { JButton btn =
     * getRootPane().getDefaultButton();
     * btn.setText(Funcs.underline(btn.getText())); }
     */

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
        }
        super.processWindowEvent(e);
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
            // setForeground(Utils.getDarkShadowSysColor());
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
                    pi.addActionListener(DesignerDialog.this);
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
            addActionListener(DesignerDialog.this);
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

    public void setDialogEventHandler(DialogEventHandler dialogEventHandler) {
        this.dialogEventHandler = dialogEventHandler;
    }
    
    public boolean isOK() {
        return  dialogResult != ButtonsFactory.BUTTON_NOACTION && dialogResult == ButtonsFactory.BUTTON_OK;
    }

    public JButton getToBckBtn() {
        return toBckBtn;
    }
    
    public JButton getOkBtn() {
        return okBtn;
    }
    
    public JButton getEditBtn() {
        return editBtn;
    }
    
    public JButton getCancelBtn() {
        return cancelBtn;
    }

	public boolean isDisposeOnClear() {
		return disposeOnClear;
	}

	public void setDisposeOnClear(boolean disposeOnClear) {
		this.disposeOnClear = disposeOnClear;
	}
}

