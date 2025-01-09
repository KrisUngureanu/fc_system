package kz.tamur.guidesigner;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.NONE;
import static javax.swing.BorderFactory.createTitledBorder;
import static javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION;
import static javax.swing.border.TitledBorder.DEFAULT_POSITION;
import static kz.tamur.comps.Constants.INSETS_0;
import static kz.tamur.comps.Constants.INSETS_1;
import static kz.tamur.rt.Utils.createMenuItem;
import static kz.tamur.rt.Utils.getDefaultFont;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import kz.tamur.comps.RecycleObject;
import kz.tamur.comps.ui.OrGradientMenuBar;
import kz.tamur.comps.ui.GradientPanel;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.Utils;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.util.AttrRequestBuilder;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.Time;
 
/**
 * Реализация корзины для удалённых объектов.
 * 
 * @author Lebedev Sergey
 */
public class Recycle extends GradientPanel implements ActionListener {

    /** gbl. */
    private GridBagLayout gbl = new GridBagLayout();
    
    /** status panel. */
    private DesignerStatusBar statusPanel = new DesignerStatusBar();
    
    /** menu bar. */
    private OrGradientMenuBar menuBar = new OrGradientMenuBar();
    
    /** main menu. */
    private JMenu mainMenu = new JMenu("Корзина");
    
    /** restore item. */
    private JMenuItem restoreItem = createMenuItem("Восстановить");
    
    /** delete item. */
    private JMenuItem deleteItem = createMenuItem("Удалить");
    
    /** clear item. */
    private JMenuItem clearItem = createMenuItem("Очистить");
    
    /** list model. */
    private DefaultListModel listModel = new DefaultListModel();
    
    /** list. */
    private JList list = new JList(listModel);
    
    /** scroll. */
    private JScrollPane scroll = new JScrollPane(list);
    
    /** split. */
    private JSplitPane split = new JSplitPane();
    
    /** tools. */
    private GradientPanel tools = new GradientPanel();
    
    /** right pan. */
    private GradientPanel rightPan = new GradientPanel();
    
    /** view pan. */
    private GradientPanel viewPan = new GradientPanel();
    
    /** view. */
    private JEditorPane view = new JEditorPane();
    
    /** scroll pr. */
    private JScrollPane scrollPR = new JScrollPane(view);
    
    /** restore rtn. */
    private JButton restoreRtn = new JButton("Восстановить");
    
    /** delete btn. */
    private JButton deleteBtn = new JButton("Удалить");
    
    /** clear btn. */
    private JButton clearBtn = new JButton("Очистить");

    /** is opaque. */
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    
    /** cls. */
    private KrnClass cls;
    
    /** objects. */
    private List<RecycleObject> objects = new ArrayList<RecycleObject>();

    /** krn. */
    private final Kernel krn = Kernel.instance();
    
    /** time format. */
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    /** recycle. */
    private static Recycle recycle = null;

    /**
     * Конструктор класса recycle.
     */
    public Recycle() {
        init();
    }

    /**
     * Instance.
     *
     * @return recycle
     */
    public static Recycle instance() {
        if (recycle == null) {
            recycle = new Recycle();
        }
        return recycle;
    }

    /**
     * Inits the.
     */
    private void init() {
        initMenu();
        view.setFont(kz.tamur.rt.Utils.getDefaultFont());
        list.setFont(kz.tamur.rt.Utils.getDefaultFont());
        view.setEditable(false);
        view.setContentType("text/html");
        view.setBorder(BorderFactory.createEmptyBorder());
        setOpaque(isOpaque);
        view.setOpaque(isOpaque);
        scroll.setOpaque(isOpaque);
        scroll.getViewport().setOpaque(isOpaque);
        scrollPR.setOpaque(isOpaque);
        scrollPR.getViewport().setOpaque(isOpaque);
        scrollPR.setBorder(BorderFactory.createEmptyBorder());
        scrollPR.setViewportBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        split.setOpaque(isOpaque);
        list.setOpaque(isOpaque);
        rightPan.setOpaque(isOpaque);
        tools.setOpaque(isOpaque);
        viewPan.setOpaque(isOpaque);

        setLayout(gbl);
        rightPan.setLayout(gbl);
        tools.setLayout(gbl);

        restoreRtn.addActionListener(this);
        deleteBtn.addActionListener(this);
        clearBtn.addActionListener(this);
        restoreItem.addActionListener(this);
        deleteItem.addActionListener(this);
        clearItem.addActionListener(this);

        viewPan.setBorder(Utils.createTitledBorder(getBorder(), "Свойства объекта"));
        viewPan.setPreferredSize(new Dimension(400, 200));
        scrollPR.setPreferredSize(new Dimension(400, 200));

        viewPan.add(scrollPR);

        tools.add(restoreRtn, new GridBagConstraints(0, 0, 1, 1, 1, 1, CENTER, NONE, INSETS_1, 0, 0));
        tools.add(deleteBtn, new GridBagConstraints(1, 0, 1, 1, 1, 1, CENTER, NONE, INSETS_1, 0, 0));
        tools.add(clearBtn, new GridBagConstraints(2, 0, 1, 1, 1, 1, CENTER, NONE, INSETS_1, 0, 0));
        rightPan.add(viewPan, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, NONE, INSETS_1, 0, 0));
        rightPan.add(tools, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, NONE, INSETS_1, 0, 0));

        split.setLeftComponent(scroll);
        split.setRightComponent(rightPan);

        add(split, new GridBagConstraints(0, 0, 1, 1, 1, 1, CENTER, BOTH, INSETS_0, 0, 0));

        try {
            cls = krn.getClassByName("Recycle");
        } catch (KrnException e) {
            System.out.println("Ошибка получения класса Recycle!");
        }

        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                RecycleObject obj = (RecycleObject) value;
                label.setIcon(kz.tamur.rt.Utils.getImageIconFull(getIconObject(obj.value)));
                label.setText(obj.title == null && obj.value != null ? obj.value.uid : obj.title);
                label.setOpaque(false);
                return label;
            }
        });

        list.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                try {
                    getPropertyObject((RecycleObject) list.getSelectedValue());
                } catch (KrnException e1) {
                    view.setText("Ошибка получения данных!");
                    e1.printStackTrace();
                }

            }
        });
        try {
            refresh();
        } catch (KrnException e) {
            e.printStackTrace();
        }
        fillList();

    }

    /**
     * Получить status bar.
     * 
     * @return the status bar
     */
    public DesignerStatusBar getStatusBar() {
        return statusPanel;
    }

    /**
     * Получить menu.
     * 
     * @return the menu
     */
    public OrGradientMenuBar getMenu() {
        return menuBar;
    }

    /**
     * Inits the menu.
     */
    public void initMenu() {
        menuBar.add(mainMenu);
        mainMenu.add(restoreItem);
        mainMenu.addSeparator();
        mainMenu.add(deleteItem);
    }

    
    @Override
    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        if (obj.equals(restoreRtn) || obj.equals(restoreItem)) {

        } else if (obj.equals(deleteBtn) || obj.equals(deleteItem)) {

        } else if (obj.equals(clearBtn) || obj.equals(clearItem)) {

        }
    }

    /**
     * Refresh.
     *
     * @throws KrnException the krn exception
     */
    public void refresh() throws KrnException {

        // получить все объекты корзины
        KrnObject[] all = krn.getClassObjects(cls, 0);
        AttrRequestBuilder arb = new AttrRequestBuilder(cls, krn).add("object").add("parent").add("dateDel").add("user");
        long[] objIds = new long[all.length];
        for (int i = 0; i < all.length; ++i) {
            objIds[i] = all[i].id;
        }
        List<Object[]> rows = krn.getObjects(objIds, arb.build(), 0);

        for (Object[] row : rows) {
            objects.add(new RecycleObject(arb.getObject(row), arb.getObjectValue("object", row), arb
                    .getObjectValue("parent", row), kz.tamur.util.Funcs.convertTime((Time) arb.getValue("dateDel", row)), arb.getObjectValue("user", row)));
        }

        Iterator<RecycleObject> itr = objects.iterator();
        while (itr.hasNext()) {
            RecycleObject obj = itr.next();
            if (obj.value != null && haveTitle(obj.value.classId)) {
                obj.title = krn.getStrings(obj.value, "title", 0, 0)[0];
            }
        }
    }

    /**
     * Fill list.
     */
    public void fillList() {
        listModel.removeAllElements();
        Iterator<RecycleObject> itr = objects.iterator();
        while (itr.hasNext()) {
            RecycleObject obj = itr.next();
            if (obj.value != null) {
                listModel.addElement(obj);
            }
        }
    }

    /**
     * Получить property object.
     *
     * @param obj the obj
     * @return property object
     * @throws KrnException the krn exception
     */
    public void getPropertyObject(RecycleObject obj) throws KrnException {
        if (obj == null) {
            view.setText("Не найден связанный объект!");
        } else {
            StringBuilder out = new StringBuilder(200);
            if (obj.value != null) {
                out.append("<html><b>Класс: </b>");
                out.append(getTypeObject(obj.value.classId));
            }
            out.append("<br><b>UID: </b>");
            out.append(obj.value.uid);

            if (krn.getAttributeByName(krn.getClass(obj.value.classId), "title") != null) {
                out.append("<br><b>Заголовок: </b>");
                out.append(krn.getStrings(obj.value, "title", 0, 0)[0]);
            }

            if (obj.time != null) {
                out.append("<br><b>Дата удаления: </b>");
                timeFormat.format(obj.time);
            }

            if (obj.user != null) {
                out.append("<br><b>Пользователь: </b>");
                out.append(krn.getStrings(obj.user, "name", 0, 0)[0]);
            }

            view.setText(out.toString());
        }
    }

    /**
     * Получить type object.
     *
     * @param id the id
     * @return type object
     */
    public String getTypeObject(long id) {
        if (id == Kernel.SC_UI.id) {
            return id + " - Интерфейс";
        } else if (id == Kernel.SC_FILTER.id) {
            return id + " - Фильтр";
        } else if (id == Kernel.SC_USER.id) {
            return id + " - Пользователь";
        } else if (id == Kernel.SC_PROCESS_DEF.id) {
            return id + " - Процесс";
        } else if (id == Kernel.SC_CONTROL_FOLDER.id) {
            return id + " - Узел проектного дерева";
        } else if (id == Kernel.SC_CONTROL_FOLDER_ROOT.id) {
            return id + " - Корень проектного дерева";
        } else if (id == Kernel.SC_REPORT_PRINTER.id) {
            return id + " - Отчёт";
        } else {
            return id + "";
        }
    }

    /**
     * Получить icon object.
     *
     * @param obj the obj
     * @return icon object
     */
    public String getIconObject(KrnObject obj) {
        if (obj == null) {
            return "FnRecycle2.png";
        } else {
            if (obj.classId == Kernel.SC_UI.id) {
                return "FnIfc2.png";
            } else if (obj.classId == Kernel.SC_FILTER.id) {
                return "FnFilters2.png";
            } else if (obj.classId == Kernel.SC_USER.id) {
                return "FnUsers2.png";
            } else if (obj.classId == Kernel.SC_PROCESS_DEF.id) {
                return "FnServices2.png";
            } else if (obj.classId == Kernel.SC_CONTROL_FOLDER.id || obj.classId == Kernel.SC_CONTROL_FOLDER_ROOT.id) {
                return "FnServicesControl2.png";
            } else if (obj.classId == Kernel.SC_REPORT_PRINTER.id) {
                return "FnReports2.png";
            } else {
                return "FnRecycle2.png";
            }
        }
    }

    /**
     * По id класса возвращает <code>true</code> если у класса есть атрибут "title".
     *
     * @param id the id
     * @return true, в случае успеха
     */
    public boolean haveTitle(long id) {
        return id == Kernel.SC_UI.id || id == Kernel.SC_FILTER.id || id == Kernel.SC_PROCESS_DEF.id
                || id == Kernel.SC_CONTROL_FOLDER.id || id == Kernel.SC_CONTROL_FOLDER_ROOT.id
                || id == Kernel.SC_REPORT_PRINTER.id;
    }

    /**
     * Поместить объект в корзину.
     *
     * @param object the object
     * @param parent the parent
     * @throws KrnException the krn exception
     */
    public void put(KrnObject object,KrnObject parent) throws KrnException {
        
        KrnObject objRecycle = krn.createObject(cls, 0);
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("Asia/Dhaka"));
        RecycleObject obj = new RecycleObject(objRecycle, object, parent, cal.getTime(), krn.getUser().getObject());
        
        krn.setObject(objRecycle.id, objRecycle.classId, "object", 0, obj.value.id, 0, false);
        krn.setObject(objRecycle.id, objRecycle.classId, "parent", 0, obj.parentValue.id, 0, false);
        krn.setTime(objRecycle.id, objRecycle.classId, "dateDel", 0, obj.time, 0);
        krn.setObject(objRecycle.id, objRecycle.classId, "user", 0, obj.user.id, 0, false);
        
        objects.add(obj);
    }
}
