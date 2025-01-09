package kz.tamur.admin;

import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
//import kz.tamur.comps.Utils;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.DesignerFrame;
import kz.tamur.guidesigner.DesignerStatusBar;
import kz.tamur.rt.MainFrame;
import kz.tamur.admin.clsbrow.ObjectBrowser;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import static kz.tamur.rt.Utils.createMenuItem;
import static kz.tamur.comps.Utils.createCheckMenuItem;
/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 09.07.2004
 * Time: 11:02:55
 * To change this template use File | Settings | File Templates.
 */
public class AdminFrame extends JFrame implements ActionListener {

    private ClassBrowser classBrowser = null;
    private ObjectBrowser objectBrowser = null;
    private ServiceBrowser serviceBrowser = null;

    private Container content;

    private JToolBar bigTools = kz.tamur.comps.Utils.createDesignerToolBar();

    private JButton accessBtn = ButtonsFactory.createToolButton("Access48",
            "Доступ");
    private JButton usersBtn = ButtonsFactory.createToolButton("Users48",
            "Пользователи");
    private JButton classesBtn = ButtonsFactory.createToolButton("Classes48",
            "Классы системы");
    private JButton objectsBtn = ButtonsFactory.createToolButton("Objects48",
            "Объекты класса");
    private JButton servicesBtn = ButtonsFactory.createToolButton("Services48",
            "Службы системы");
    private JButton filtersBtn = ButtonsFactory.createToolButton("Filters48",
            "Фильтры системы");
    private JButton findBugBtn = ButtonsFactory.createToolButton("FindBug48",
            "Найти ошибки");
    private JButton corBugBtn = ButtonsFactory.createToolButton("CorrectBug48",
            "Исправить ошибки");

    private JMenu fileMenu = new DesignerFrame.DesinerMenu("Файл");
    private JCheckBoxMenuItem openModeItem = createCheckMenuItem("Открывать в отдельном окне");
    private JMenuItem accessItem = createMenuItem("Доступ");
    private JMenuItem usersItem = createMenuItem("Пользователи");
    private JMenuItem classesItem = createMenuItem("Классы");
    private JMenuItem objectsItem = createMenuItem("Объекты");
    private JMenuItem servicesItem = createMenuItem("Службы");
    private JMenuItem filtersItem = createMenuItem("Фильтры");
    private JMenuItem closeItem = createMenuItem("Закрыть");

    private JMenu replMenu = new DesignerFrame.DesinerMenu("Репликация");
    private JMenuItem replItem = createMenuItem("Запуск репликации");
    private JMenuItem replJournItem = createMenuItem("Журнал репликации");
    private JMenuItem replSelectItem = createMenuItem("Выборочная репликация");

    private JMenu helpMenu = new DesignerFrame.DesinerMenu("?");
    private JMenuItem helpItem = createMenuItem("Помощь");
    private JMenuItem aboutItem = createMenuItem("О программе");

    private DesignerStatusBar statusPanel = new DesignerStatusBar();
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    private Dimension size = new Dimension(72, 72);
    
    public AdminFrame() {
        super("Консоль администратора");
        init();
        pack();
    }

    void init() {

        setIconImage(kz.tamur.rt.Utils.getImageIcon("iconAdmin").getImage());

        usersItem.addActionListener(this);
        usersBtn.addActionListener(this);
        accessItem.addActionListener(this);
        accessBtn.addActionListener(this);
        classesItem.addActionListener(this);
        classesBtn.addActionListener(this);
        objectsItem.addActionListener(this);
        objectsBtn.addActionListener(this);
        servicesItem.addActionListener(this);
        servicesBtn.addActionListener(this);

        JMenuBar menuBar = new JMenuBar();
        fileMenu.add(openModeItem);
        fileMenu.addSeparator();
        fileMenu.add(accessItem);
        fileMenu.add(usersItem);
        fileMenu.addSeparator();
        fileMenu.add(classesItem);
        fileMenu.add(objectsItem);
        fileMenu.add(servicesItem);
        fileMenu.add(filtersItem);
        fileMenu.addSeparator();
        fileMenu.add(closeItem);

        replMenu.add(replItem);
        replMenu.add(replJournItem);
        replMenu.add(replSelectItem);

        helpMenu.add(helpItem);
        helpMenu.addSeparator();
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(replMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);

        content = getContentPane();
        content.setLayout(new BorderLayout());

        bigTools.setOrientation(JToolBar.VERTICAL);
        bigTools.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        //bigTools.setRollover(false);
        kz.tamur.rt.Utils.setAllSize(usersBtn, size);
        usersBtn.setMargin(null);
        usersBtn.setHorizontalTextPosition(AbstractButton.CENTER);
        usersBtn.setVerticalTextPosition(AbstractButton.BOTTOM);
        bigTools.add(usersBtn);

        accessBtn.setText("Доступ");
        kz.tamur.rt.Utils.setAllSize(accessBtn, size);
        accessBtn.setMargin(null);
        accessBtn.setHorizontalTextPosition(AbstractButton.CENTER);
        accessBtn.setVerticalTextPosition(AbstractButton.BOTTOM);
        bigTools.add(accessBtn);

        classesBtn.setText("Классы");
        kz.tamur.rt.Utils.setAllSize(classesBtn, size);
        classesBtn.setMargin(null);
        classesBtn.setHorizontalTextPosition(AbstractButton.CENTER);
        classesBtn.setVerticalTextPosition(AbstractButton.BOTTOM);
        bigTools.add(classesBtn);

        objectsBtn.setText("Объекты");
        kz.tamur.rt.Utils.setAllSize(objectsBtn, size);
        objectsBtn.setMargin(null);
        objectsBtn.setHorizontalTextPosition(AbstractButton.CENTER);
        objectsBtn.setVerticalTextPosition(AbstractButton.BOTTOM);
        bigTools.add(objectsBtn);

        servicesBtn.setText("Службы");
        kz.tamur.rt.Utils.setAllSize(servicesBtn, size);
        servicesBtn.setMargin(null);
        servicesBtn.setHorizontalTextPosition(AbstractButton.CENTER);
        servicesBtn.setVerticalTextPosition(AbstractButton.BOTTOM);
        bigTools.add(servicesBtn);

        filtersBtn.setText("Фильтры");
        kz.tamur.rt.Utils.setAllSize(filtersBtn, size);
        filtersBtn.setMargin(null);
        filtersBtn.setHorizontalTextPosition(AbstractButton.CENTER);
        filtersBtn.setVerticalTextPosition(AbstractButton.BOTTOM);
        bigTools.add(filtersBtn);

        //bigTools.addSeparator();

        findBugBtn.setText("Найти");
        kz.tamur.rt.Utils.setAllSize(findBugBtn, size);
        findBugBtn.setMargin(null);
        findBugBtn.setHorizontalTextPosition(AbstractButton.CENTER);
        findBugBtn.setVerticalTextPosition(AbstractButton.BOTTOM);
        bigTools.add(findBugBtn);

        corBugBtn.setText("Исправить");
        kz.tamur.rt.Utils.setAllSize(corBugBtn, size);
        corBugBtn.setMargin(null);
        corBugBtn.setHorizontalTextPosition(AbstractButton.CENTER);
        corBugBtn.setVerticalTextPosition(AbstractButton.BOTTOM);
        bigTools.add(corBugBtn);

        content.add(bigTools, BorderLayout.WEST);
        content.add(statusPanel, BorderLayout.SOUTH);
    }

    void openObjectBrowser() {
        //setFlrBtnEnabled(false);
        try {
            ClassTree ct = new ClassTree();
            JScrollPane sp = new JScrollPane(ct);
            sp.setPreferredSize(new Dimension(500, 600));
            sp.setOpaque(isOpaque);
            sp.getViewport().setOpaque(isOpaque);
            DesignerDialog dlg = new DesignerDialog(this, "Выберите класс", sp);
            dlg.show();
            if (dlg.isOK()) {
                KrnClass cls = ct.getSelectedClass();
                if (cls != null) {
                    objectBrowser = new ObjectBrowser(cls, false);
                    addSelectedBrowser(objectBrowser);
                }
            }
        } catch (KrnException ex) {
            ex.printStackTrace();
        }
    }

    void openServiceBrowser() {
        if (serviceBrowser == null) {
            serviceBrowser = new ServiceBrowser();
        }
        addSelectedBrowser(serviceBrowser);
        //serviceBrowser.setInspector(this);
    }


    private void addSelectedBrowser(JPanel browser) {
        for (int i = 0; i < content.getComponentCount(); i++) {
            Component comp = content.getComponent(i);
            if (comp instanceof ObjectBrowser || comp instanceof ClassBrowser
                    || comp instanceof ServiceBrowser) {
                content.remove(comp);
            }
        }
        content.add(browser, BorderLayout.CENTER);
        content.repaint();
        content.validate();
    }

    public void setDividerLocation() {
        classBrowser.setSplitLocation();
    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == usersBtn || src == usersItem) {
        } else if (src == accessBtn || src == accessItem) {
        } else if (src == classesBtn || src == classesItem) {
            if (classBrowser == null) {
                classBrowser = new ClassBrowser(null, true);
            }
            addSelectedBrowser(classBrowser);
        } else if (src == objectsItem || src == objectsBtn) {
            openObjectBrowser();
        } else if (src == servicesItem || src == servicesBtn) {
            openServiceBrowser();
        }
    }
}
