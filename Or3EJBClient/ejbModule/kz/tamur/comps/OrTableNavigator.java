package kz.tamur.comps;

import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.LINE_END;
import static java.awt.GridBagConstraints.LINE_START;
import static java.awt.GridBagConstraints.NONE;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import kz.tamur.comps.models.GradientColor;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.ui.OrSpinner;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.rt.ConfigObject;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.RuntimeResources_kk;
import kz.tamur.rt.RuntimeResources_ru;
import kz.tamur.rt.Utils;
import kz.tamur.rt.adapters.TableAdapter;
import kz.tamur.util.FilterObject;
import kz.tamur.util.LangItem;
import kz.tamur.util.OrNodeTree;

import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.ObjectValue;
import com.cifs.or2.kernel.StringValue;

/**
 * Created by IntelliJ IDEA. User: Vital Date: 04.08.2004 Time: 9:56:23 To
 * change this template use File | Settings | File Templates.
 */
public class OrTableNavigator extends JPanel implements ActionListener, PropertyChangeListener, MouseTarget {

    ResourceBundle res = ResourceBundle.getBundle(Constants.NAME_RESOURCES, new Locale("ru"));
    private JButton fastRepBtn = ButtonsFactory.createToolButtonTransp("fastRepNavi", res.getString("fastRep"));
    private JButton consalBtn = ButtonsFactory.createToolButtonTransp("consalNavi", res.getString("consal"));
    private JButton addBtn = ButtonsFactory.createToolButtonTransp("addNavi", res.getString("add"));
    private JButton delBtn = ButtonsFactory.createToolButtonTransp("delNavi", res.getString("delete"));
    private JButton findBtn = ButtonsFactory.createToolButtonTransp("findNavi", res.getString("find"));
    private JButton filterBtn = ButtonsFactory.createToolButtonTransp("filterNavi", res.getString("filter"));
    private JButton copyRowsBtn = ButtonsFactory.createToolButtonTransp("copyRowsNavi", res.getString("copyRows"));
    private JButton yesManBtn = ButtonsFactory.createToolButtonTransp("goRight", res.getString("goDown"));
    private JButton upBtn = ButtonsFactory.createToolButtonTransp("moveUp", res.getString("moveUp"));
    private JButton downBtn = ButtonsFactory.createToolButtonTransp("moveDown", res.getString("moveDown"));
    private JButton firstPage = ButtonsFactory.createToolButtonTransp("firstPage", res.getString("firstPage"));
    private JButton lastPage = ButtonsFactory.createToolButtonTransp("lastPage", res.getString("lastPage"));
    private JButton nextPage = ButtonsFactory.createToolButtonTransp("nextPage", res.getString("nextPage"));
    private JButton backPage = ButtonsFactory.createToolButtonTransp("backPage", res.getString("backPage"));
    private JButton showDelBtn = ButtonsFactory.createToolButtonTransp("showDel", ".png", res.getString("showDeleted"), res.getString("showDeleted"));
    private List <JButton> actBtn = new ArrayList<JButton>();
    private JPanel naviPane = new JPanel();
    private OrSpinner setterCountRowPage;
    private JLabel infoPage = new JLabel("__ страница из __");

    private JLabel counterLabel = Utils.createLabel("");
    // метка для промежуточного хранения компонента
    private JLabel separator;
    // индекс процессной кнопки
    private int iProc = 0;
    // индекс обычной кнопки
    boolean[] indxBtn;
    // индекс разделителя. Разделители начинают нумероваться с единицы!
    private int sepIndx = 0;
    // иконка разделителя, которая будет вешаться на каждую метку-разделитель
    ImageIcon separatorImg = kz.tamur.rt.Utils.getImageIconFull("separator.png");

    /** Панель инструментов */
    private JToolBar toolBar = new JToolBar();

    /** Индекс выбранной строки */
    private int selRowIdx;

    /** Количество строк */
    private int rowCount;

    /** идентификатор языка интерфейса. */
    private long ifcLangId;

    /** The table adapter. */
    private TableAdapter tableAdapter;
    // диалоговое окно выбора и установки фильтров таблицы
    /** The dlg. */
    private DesignerDialog dlg = null;
    // меню выбора и установки фильтров для таблицы
    /** Всплывающее мню */
    private JPopupMenu menu = null;

    /** Панель фильтров */
    private JPanel filtersPanel = new JPanel();

    /** Таблица, которой принадлежит данная панель навигации */
    private OrTable table;

    /** Карта фильтров */
    private Map filters;

    /** The message label. */
    private JLabel messageLabel = Utils.createLabel("");

    /** Начальный цвет градиента. */
    private Color startColor;

    /** конечный цвет градиента. */
    private Color endColor;

    /** Ориентация градиента. */
    private int orientation = 0;

    /** Цикличность градиента. */
    private boolean isCycle = true;

    /** позиция отсчёта градиента для начального цвета. */
    private int positionStartColor = 0;

    /** The is enable gradient. */
    private boolean isEnableGradient = true;

    /** позиция отсчёта градиента для конечного цвета. */
    private int positionEndColor = 50;

    /** The number menu levels. */
    private int numberMenuLevels;

    /** Константа krn. */
    private final static Kernel krn = Kernel.instance();

    /** The flr cls. */
    private KrnClass flrCls;

    /** The filters obj. */
    List<FilterObject> filtersObj;

    /** Группа переключателей, объединяющая пункты меню выбора фильтра */
    ButtonGroup itemGroup = new ButtonGroup();

    /** Список Созданных пунктнов меню для фильтров, необходим для удобной их дезактивации */
    List<Object> itemFilter = new ArrayList<Object>();

    /** Корень дерева при построении иерархического меню */
    OrNodeTree root = new OrNodeTree();

    /** Временное хранение пункта меню */
    OrCheckBoxMenuItemFilter item;

    /** Временное хранение пункта меню */
    OrRadioButtonMenuItemFilter itemRadio;

    /** Элемент меню (для временного хранения) */
    final JRadioButtonMenuItem hideItem = new JRadioButtonMenuItem();

    /** Пункт меню для отмены действия фильтров */
    final OrFilterMenuItem itemCancel = new OrFilterMenuItem(res.getString("resetData"));

    protected boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    
    final Dimension sizeBtn = new Dimension(30, 30);

    /**
     * конструктор панели инструментов.
     * 
     * @param table
     *            таблица для которой конструируется панель
     */
    public OrTableNavigator(OrTable table) {
        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(3, 3, 3, 3));
        this.table = table;
        setterCountRowPage = new OrSpinner(this, 100, 10, 10, 50);
        MouseDelegator delegator = new MouseDelegator(this);
        toolBar.addMouseListener(delegator);
        toolBar.addMouseMotionListener(delegator);
        init();
    }

    /**
     * Получить table.
     * 
     * @return the table
     */
    public OrTable getTable() {
        return table;
    }

    /**
     * Установить table adapter.
     * 
     * @param tableAdapter
     *            the new table adapter
     */
    public void setTableAdapter(TableAdapter tableAdapter) {
        this.tableAdapter = tableAdapter;
    }

    /**
     * инициализация панели инструментов.
     */
    void init() {
        try {
            flrCls = krn.getClassByName("Filter");
        } catch (KrnException e) {
            e.printStackTrace();
        }
        
        PropertyValue pv = table.getPropertyValue(table.getProperties().getChild("view").getChild("navi").getChild("buttons").getChild("naviPane"));
        if (!pv.booleanValue()||ConfigObject.instance(krn).getProperty(table.UUID, "countRowPage")==null) {
            pv = table.getPropertyValue(table.getProperties().getChild("pov").getChild("maxObjectCount"));
            setCountRowPage(pv.isNull() ? -1 : pv.intValue());
        }else {
            ConfigObject.instance(krn).getProperty(table.UUID, "countRowPage");
            setCountRowPage(Integer.parseInt(ConfigObject.instance(krn).getProperty(table.UUID, "countRowPage")));
        }
        
        counterLabel.setFocusable(false);
        messageLabel.setForeground(Color.red);

        toolBar.setBorder(null);
        toolBar.setRollover(true);
        toolBar.setFloatable(false);

        naviPane.setLayout(new GridBagLayout());
        naviPane.setOpaque(false);
        naviPane.add(firstPage, new GridBagConstraints(0, 0, 1, 1, 0, 0, LINE_START, NONE, Constants.INSETS_1, 0, 0));
        naviPane.add(backPage, new GridBagConstraints(1, 0, 1, 1, 1, 0, LINE_START, NONE, Constants.INSETS_1, 0, 0));
        naviPane.add(infoPage, new GridBagConstraints(2, 0, 1, 1, 0, 0, CENTER, HORIZONTAL, new Insets(1, 3, 1, 3), 0, 0));
        naviPane.add(nextPage, new GridBagConstraints(3, 0, 1, 1, 1, 0, LINE_END, NONE, Constants.INSETS_1, 0, 0));
        naviPane.add(lastPage, new GridBagConstraints(4, 0, 1, 1, 0, 0, LINE_END, NONE, Constants.INSETS_1, 0, 0));
        naviPane.add(setterCountRowPage, new GridBagConstraints(5, 0, 1, 1, 0, 0, LINE_END, NONE, Constants.INSETS_1, 0, 0));

        firstPage.addActionListener(this);
        lastPage.addActionListener(this);
        nextPage.addActionListener(this);
        backPage.addActionListener(this);
        addBtn.addActionListener(this);
        delBtn.addActionListener(this);
        copyRowsBtn.addActionListener(this);
        findBtn.addActionListener(this);
        filterBtn.addActionListener(this);
        yesManBtn.addActionListener(this);
        upBtn.addActionListener(this);
        downBtn.addActionListener(this);
        showDelBtn.addActionListener(this);

        // добавить кнопку "добавить строку"
        toolBar.add(addBtn);
        addNaviSeparator();
        // добавить кнопку "удалить строку"
        toolBar.add(delBtn);
        addNaviSeparator();
        // добавить кнопку "копировать строки"
        toolBar.add(copyRowsBtn);
        addNaviSeparator();
        // добавить кнопку "поиск"
        toolBar.add(findBtn);
        addNaviSeparator();
        // добавить кнопку "фильтры"
        toolBar.add(filterBtn);
        addNaviSeparator();
        // добавить кнопку "быстрый отчет"
        toolBar.add(fastRepBtn);
        addNaviSeparator();
        // добавить кнопку "объединить записи"
        toolBar.add(consalBtn);
        addNaviSeparator();
        // добавить кнопку "направление перехода"
        toolBar.add(yesManBtn);
        addNaviSeparator();
        // добавить кнопку "строку вверх"
        toolBar.add(upBtn);
        addNaviSeparator();
        // добавить кнопку "строку вниз"
        toolBar.add(downBtn);
        addNaviSeparator();

        // задать размеры кнопок
        setButtonsSize();

        toolBar.add(showDelBtn);
        Utils.setAllSize(showDelBtn, new Dimension(250, 30));
        addNaviSeparator();

        toolBar.setOpaque(false);
        
        add(toolBar, new GridBagConstraints(0, 0, 1, 1, 1, 0, LINE_START, HORIZONTAL, Constants.INSETS_1, 0, 0));
        add(naviPane, new GridBagConstraints(1, 0, 1, 1, 0, 0, LINE_START, NONE, Constants.INSETS_1, 0, 0));
        add(messageLabel, new GridBagConstraints(2, 0, 1, 1, 0, 0, CENTER, NONE, Constants.INSETS_1, 0, 0));
        add(counterLabel, new GridBagConstraints(4, 0, 1, 1, 0, 0, LINE_END, NONE, Constants.INSETS_1, 0, 0));
        
    }

    /**
     * задаётся отображение необходимых кнопок таблицы также в методе
     * заполняется массив <b>indxBtn</b> в этом массиве проиндексированнна
     * активность кнопок панели инструментов.
     * 
     * @param propNode
     *            свойство, в котором задано имя элемента
     * @param isVisible
     *            флаг отображения компонента
     */
    public void setButtonsVisible(PropertyNode propNode, boolean isVisible) {
        String propName = propNode.getName();
        if ("fastRepBtn".equals(propName)) {
            fastRepBtn.setVisible(isVisible);
            indxBtn[5] = isVisible;
        } else if ("consalBtn".equals(propName)) {
            consalBtn.setVisible(isVisible);
            indxBtn[6] = isVisible;
        } else if ("addBtn".equals(propName)) {
            addBtn.setVisible(isVisible);
            indxBtn[0] = isVisible;
        } else if ("delBtn".equals(propName)) {
            delBtn.setVisible(isVisible);
            indxBtn[1] = isVisible;
        } else if ("findBtn".equals(propName)) {
            findBtn.setVisible(isVisible);
            indxBtn[3] = isVisible;
        } else if ("copyRowsBtn".equals(propName)) {
            copyRowsBtn.setVisible(isVisible);
            indxBtn[2] = isVisible;
        } else if ("yesManBtn".equals(propName)) {
            yesManBtn.setVisible(isVisible);
            indxBtn[7] = isVisible;
        } else if ("filterBtn".equals(propName)) {
            filterBtn.setVisible(isVisible);
            indxBtn[4] = isVisible;
        } else if ("downBtn".equals(propName)) {
            downBtn.setVisible(isVisible);
            indxBtn[9] = isVisible;
        } else if ("upBtn".equals(propName)) {
            upBtn.setVisible(isVisible);
            indxBtn[8] = isVisible;
        } else if ("showDelBtn".equals(propName)) {
        	showDelBtn.setVisible(isVisible);
            indxBtn[10] = isVisible;
        } else if ("naviPane".equals(propName)) {
            naviPane.setVisible(isVisible);
            indxBtn[11] = isVisible;
            counterLabel.setVisible(!isVisible);
        }
    }
    
    public void setButtonsToolTip(PropertyNode propNode, String toolTip) {
        String propName = propNode.getName();
        if ("fastRepBtnProp".equals(propName)) {
            fastRepBtn.setToolTipText(toolTip);
        } else if ("consalBtnProp".equals(propName)) {
            consalBtn.setToolTipText(toolTip);
        } else if ("addBtnProp".equals(propName)) {
            addBtn.setToolTipText(toolTip);
        } else if ("delBtnProp".equals(propName)) {
            delBtn.setToolTipText(toolTip);
        } else if ("findBtnProp".equals(propName)) {
            findBtn.setToolTipText(toolTip);
        } else if ("copyRowsBtnProp".equals(propName)) {
            copyRowsBtn.setToolTipText(toolTip);
        } else if ("yesManBtnProp".equals(propName)) {
            yesManBtn.setToolTipText(toolTip);
        } else if ("filterBtnProp".equals(propName)) {
            filterBtn.setToolTipText(toolTip);
        } else if ("downBtnProp".equals(propName)) {
            downBtn.setToolTipText(toolTip);
        } else if ("upBtnProp".equals(propName)) {
            upBtn.setToolTipText(toolTip);
        } else if ("showDelBtnProp".equals(propName)) {
        	showDelBtn.setToolTipText(toolTip);
        }
    }
    
    public void setButtonsIcon(PropertyNode propNode, byte[] iconBytes) {
        String propName = propNode.getName();
        ImageIcon icon = new ImageIcon(iconBytes);
        if ("fastRepBtnProp".equals(propName)) {
            fastRepBtn.setIcon(icon);
        } else if ("consalBtnProp".equals(propName)) {
            consalBtn.setIcon(icon);
        } else if ("addBtnProp".equals(propName)) {
            addBtn.setIcon(icon);
        } else if ("delBtnProp".equals(propName)) {
            delBtn.setIcon(icon);
        } else if ("findBtnProp".equals(propName)) {
            findBtn.setIcon(icon);
        } else if ("copyRowsBtnProp".equals(propName)) {
            copyRowsBtn.setIcon(icon);
        } else if ("yesManBtnProp".equals(propName)) {
            yesManBtn.setIcon(icon);
        } else if ("filterBtnProp".equals(propName)) {
            filterBtn.setIcon(icon);
        } else if ("downBtnProp".equals(propName)) {
            downBtn.setIcon(icon);
        } else if ("upBtnProp".equals(propName)) {
            upBtn.setIcon(icon);
        } else if ("showDelBtnProp".equals(propName)) {
        	showDelBtn.setIcon(icon);
        }
    }

    /**
     * Добавление на панель инструментов визуального разделителя для обращения к
     * разделителю ему задаётся свойство NAME
     * 
     * имя задаётся по шаблону: <i>separator</i>+<i>$</i> <i>$</i> - глобальный
     * счётчик разделителей для задания уникальности имён.
     */
    private void addNaviSeparator() {
        toolBar.add(separator = new JLabel());
        separator.setVisible(false);
        separator.setIcon(separatorImg);
        separator.setName("separator" + (++sepIndx));
    }

    /**
     * Активация разделителей.
     * 
     * @param index
     *            массив позиций разделителей на панели инструментов
     *            (вытаскивается из свойства определённого в конструкторе
     *            интерфейсов)
     * @param indxBtn
     *            - индексированный массив активности кнопок на панели
     *            инструментов
     */
    public void setSeparator(int[] index, boolean[] indxBtn) {
        // защита от ненужного срабатывания
        if (indxBtn == null || indxBtn.length == 0) {
            return;
        }

        // очистка
        if (index == null || index.length == 0) {
            Component[] cArr = toolBar.getComponents();
            for (int i = 0; i < cArr.length; ++i) {
                if (cArr[i] instanceof JLabel) {
                    if (((JLabel) cArr[i]).getName().contains("separator")) {
                        ((JLabel) cArr[i]).setVisible(false);
                    }
                }
            }
            return;
        }

        /**
         * конечный массив индексов разделителей для активации именно в этом
         * массиве окажутся реальные индексы разделителей по которым можно
         * отследить существующий объект
         */
        int[] indxSeparator = new int[index.length];

        // номер разделителя в массиве
        int iS = 0;
        // просчёт реальный индексов разделителей
        for (int i = 0; i < index.length; ++i) {
            /* так как разделителей может быть указано больше чем поместиться на панели, необходима эта проверка */
            if (iS == indxSeparator.length) {
                break;
            }
            /* index[i] - позиция кнопки после которой нужно активировать разделитель просчитать индекс разделителя для нужной кнопки */
            int j;
            int k = 0;
            for (j = 0; j < indxBtn.length; ++j) {
                // если кнопка выводиться на панель
                if (indxBtn[j]) {
                    // увеличить индекс выводимой кнопки и сравнить его с
                    // позицией разделителя
                    if (++k == index[i]) {
                        k = -1;
                        break;
                    }
                }
            }
            // если был сделан принудительный выход из цикла, записать индекс
            // выхода из цикла как индекс сепаратора +1
            indxSeparator[iS] = k == -1 ? j + 1 : -1;
            ++iS;
        }

        // активация разделителей
        if (indxSeparator.length != 0) {
            Component[] cArr = toolBar.getComponents();
            // очистка
            for (int i = 0; i < cArr.length; ++i) {
                if (cArr[i] instanceof JLabel) {
                    if (((JLabel) cArr[i]).getName().contains("separator")) {
                        ((JLabel) cArr[i]).setVisible(false);
                    }
                }
            }
            // прорисовать разделители
            for (int i = 0; i < cArr.length; ++i) {
                // если сомпонент - метка
                if (cArr[i] instanceof JLabel) {
                    // сравнивнить свойство NAME с предполагаемым именем
                    // выводимого раделителя
                    for (int j = 0; j < indxSeparator.length; ++j) {
                        // если имена совпадают - вывести на панель
                        if (indxSeparator[j] != -1 && ((JLabel) cArr[i]).getName().equals("separator" + indxSeparator[j])) {
                            ((JLabel) cArr[i]).setVisible(true);
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Получить кнопку по её имени.
     * 
     * @param name
     *            имя кнопки
     * @return кнопка
     */
    public JButton getButtonByName(String name) {
        JButton res = null;
        if ("fastRepBtn".equals(name)) {
            res = fastRepBtn;
        } else if ("consalBtn".equals(name)) {
            res = consalBtn;
        } else if ("addBtn".equals(name)) {
            res = addBtn;
        } else if ("delBtn".equals(name)) {
            res = delBtn;
        } else if ("findBtn".equals(name)) {
            res = findBtn;
        } else if ("copyRowsBtn".equals(name)) {
            res = copyRowsBtn;
        } else if ("yesManBtn".equals(name)) {
            res = yesManBtn;
        } else if ("filterBtn".equals(name)) {
            res = filterBtn;
        } else if ("downBtn".equals(name)) {
            res = downBtn;
        } else if ("upBtn".equals(name)) {
            res = upBtn;
        } else if ("showDelBtn".equals(name)) {
            res = showDelBtn;
        }
        return res;
    }

    
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!isEnableGradient) {
            return;
        }
        if (startColor == null && endColor == null) {
            Color defColor = g.getColor();
            g.setColor(Utils.getDarkShadowSysColor());
            g.drawLine(0, 0, getWidth() - 1, 0);
            g.drawLine(0, 0, 0, getHeight());
            g.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());
            g.setColor(defColor);
            return;
        }

        final int height = getHeight();
        final int wigth = getWidth();
        final int startH = (int) (wigth / 100f * positionStartColor);
        final int endH = (int) (wigth / 100f * positionEndColor);
        final int startV = (int) (height / 100f * positionStartColor);
        final int endV = (int) (height / 100f * positionEndColor);
        GradientPaint gp;
        switch (orientation) {
        case Constants.HORIZONTAL:
            gp = new GradientPaint(startH, 0, startColor, endH, 0, endColor, isCycle);
            break;
        case Constants.VERTICAL:
            gp = new GradientPaint(0, startV, startColor, 0, endV, endColor, isCycle);
            break;
        case Constants.DIAGONAL:
            gp = new GradientPaint(startH, height - startV, startColor, endH, height - endV, endColor, isCycle);
            break;
        case Constants.DIAGONAL2:
            gp = new GradientPaint(startH, startV, startColor, endH, endV, endColor, isCycle);
            break;
        default:
            gp = new GradientPaint(startH, 0, startColor, endH, 0, endColor, isCycle);
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), getHeight());
    }

    
    public void actionPerformed(ActionEvent e) {
        JButton src = (JButton) e.getSource();
        if (tableAdapter != null) {
            if (src == addBtn) {
                tableAdapter.addNewRow();
            } else if (src == delBtn) {
                tableAdapter.deleteRow();
            } else if (src == showDelBtn) {
                boolean b = tableAdapter.showDeleted();
                if (b)
                	showDelBtn.setIcon(Utils.getImageIconFull("showDel.png"));
                else
                	showDelBtn.setIcon(Utils.getImageIconFull("showDelUn.png"));
            } else if (src == findBtn) {
                String command = e.getActionCommand();
                if ("nextRow".equals(command)) {
                    tableAdapter.findNextRow();
                } else {
                    tableAdapter.findRow();
                }
            } else if (src == copyRowsBtn) {
                tableAdapter.copyRows();
            } else if (src == downBtn) {
                tableAdapter.moveDown();
            } else if (src == upBtn) {
                tableAdapter.moveUp();
            } else if (src == yesManBtn) {
                tableAdapter.yesMan();
            } else if (src == filterBtn) {
                if (filters.size() == 0) {
                    return;
                }

                // получить координаты курсора
                Point location = MouseInfo.getPointerInfo().getLocation();
                // преобразование координат
                SwingUtilities.convertPointFromScreen(location, filterBtn);
                int x = (int) location.getX();
                int y = (int) location.getY();
                // добавить обработку нажатия на кнопку сброса фильтров
                itemCancel.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        // отключение фильтров
                        tableAdapter.cancelFilterAction();
                        // чистка массива
                        for (FilterObject filter : filtersObj) {
                            filter.setEnabled(false);
                        }
                        // выключение чекбоксов в меню
                        for (Object item : itemFilter) {
                            if (item instanceof OrCheckBoxMenuItemFilter) {
                                ((OrCheckBoxMenuItemFilter) item).setSelected(false);
                            } else if (item instanceof OrRadioButtonMenuItemFilter) {
                                // включить скрытый элемент
                                hideItem.setSelected(true);
                                break;
                            }

                        }
                    }
                });

                switch (table.getFilterBtnView()) {
                default:
                case Constants.DIALOG: // Диалог
                    initDialog();
                    dlg.show();
                    // если нажата кнопка "ОК"
                    if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                        List<Filter> filters = new ArrayList<Filter>();
                        for (int i = 0; i < filtersPanel.getComponentCount(); i++) {
                            Component c = filtersPanel.getComponent(i);
                            if (c instanceof FilterMenuItem) {
                                FilterMenuItem fmi = (FilterMenuItem) c;
                                if ((fmi).isSelected()) {
                                    filters.add(fmi.filter);
                                }
                            }
                        }
                        try {
                            tableAdapter.applyFilters(filters);
                        } catch (KrnException e1) {
                            e1.printStackTrace();
                        }
                    } else if (dlg.getResult() == ButtonsFactory.BUTTON_CANCEL_FILTER) {
                        tableAdapter.cancelFilterAction();
                        for (int i = 0; i < filtersPanel.getComponentCount(); i++) {
                            Component c = filtersPanel.getComponent(i);
                            if (c instanceof FilterMenuItem) {
                                ((FilterMenuItem) c).setSelected(false);
                            }
                        }
                    }
                    break;
                case Constants.MENU_MULTI: // Меню-мультивыбор
                    initMenu(true);
                    menu.show(filterBtn, x, y);
                    break;
                case Constants.MENU_SWITCH: // Меню-переключатель
                    initMenu(false);
                    menu.show(filterBtn, x, y);
                    break;
                case Constants.MENU_TREE_MULTI: // Древовидное меню-мультивыбор
                    initTreeMenu(true);
                    menu.show(filterBtn, x, y);
                    break;
                case Constants.MENU_TREE_SWITCH: // Древовидное меню-переключатель
                    initTreeMenu(false);
                    menu.show(filterBtn, x, y);
                    break;
                }
            } else if (src == firstPage) {
                tableAdapter.firstPage();
            } else if (src == backPage) {
                tableAdapter.backPage();
            } else if (src == nextPage) {
                tableAdapter.nextPage();
            } else if (src == lastPage) {
                tableAdapter.lastPage();
            }
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        String name = evt.getPropertyName();
        if ("rowSelected".equals(name)
                && ((Integer) evt.getOldValue()).intValue() != ((Integer) evt.getNewValue()).intValue()) {
            selRowIdx = (Integer) evt.getNewValue() + 1;
            setCounterText();
        }else if ("rowCont".equals(name)
                && ((Integer) evt.getOldValue()).intValue() != ((Integer) evt.getNewValue()).intValue()) {
            rowCount = (Integer) evt.getNewValue() + 1;
            setCounterText();
        } else if ("countRowPage".equals(name)) {
            final int countRowPage = (Integer) evt.getNewValue();
            table.setPropertyValue(new PropertyValue(countRowPage, table.getProperties().getChild("pov").getChild("maxObjectCount")));
           // setCountRowPage(countRowPage);
            saveCountRowPage(countRowPage);
            tableAdapter.setCountRowPage(countRowPage);
        }
    }

    /**
     * Sets the counter text.
     */
    private void setCounterText() {
        counterLabel.setText(selRowIdx + " / " + rowCount + " ");
    }

    /**
     * Inits the filter popup menu.
     * 
     * @param items
     *            the items
     */
    public void initFilterPopupMenu(FilterMenuItem[] items) {
        filters = new HashMap();
        if (items != null) {
            if (items.length > 0) {
                GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
                        GridBagConstraints.BOTH, Constants.INSETS_0, 0, 0);
                filtersPanel.setLayout(new GridBagLayout());
                ArrayList<FilterMenuItem> arr = new ArrayList<FilterMenuItem>();
                for (int i = 0; i < items.length; i++)
                    arr.add(items[i]);
                java.util.Collections.sort(arr);
                for (int i = 0; i < arr.size(); i++)
                    items[i] = arr.get(i);
                int i = 0;
                int col = 0;
                while (i < items.length) {
                    gbc.gridx = col;
                    for (int row = 0; row < 12 && i < items.length; row++) {
                        JCheckBox item = items[i];
                        filters.put(((FilterMenuItem) item).filter.obj.id, ((FilterMenuItem) item).filter);
                        gbc.gridy = row;
                        item.setOpaque(false);
                        filtersPanel.add(item, gbc);
                        i++;
                    }
                    col++;
                }
            }
        }
    }

    
    public void delegateMouseEvent(MouseEvent e) {
        Component c = e.getComponent();
        e.setSource(this);
        e.translatePoint(c.getX(), c.getY());
        getToolkit().getSystemEventQueue().postEvent(e);
    }

    
    public void delegateMouseMotionEvent(MouseEvent e) {
        Component c = e.getComponent();
        e.setSource(this);
        e.translatePoint(c.getX(), c.getY());
        getToolkit().getSystemEventQueue().postEvent(e);
    }

    /**
     * Задать размеры кнопок
     */
    private void setButtonsSize() {
        int count = toolBar.getComponentCount();
        for (int i = 0; i < count; i++) {
            Component c = toolBar.getComponent(i);
            if (c instanceof JButton) {
                Utils.setAllSize((JButton) c, sizeBtn);
            }
            if (c instanceof JPanel) {
                int countSub = ((JPanel) c).getComponentCount();
                for (int j = 0; j < countSub; j++) {
                    Component c2 = ((JPanel) c).getComponent(j);
                    if (c2 instanceof JButton) {
                        Utils.setAllSize((JButton) c2, sizeBtn);
                    }
                }
            }
        }
    }

    
    public void setEnabled(boolean enable) {
        toolBar.setEnabled(enable);
        Component[] buttons = toolBar.getComponents();
        for (Component button : buttons) {
            if (button != findBtn && button != filterBtn && actBtn.indexOf(button)==-1) {
                button.setEnabled(enable); 
            }
        }
    }

    /**
     * Установить del enabled.
     * 
     * @param enable
     *            the new del enabled
     */
    public void setDelEnabled(boolean enable) {
        if (delBtn != null) {
            delBtn.setEnabled(enable);
        }
    }

    /**
     * Change titles.
     * 
     * @param res
     *            the res
     */
    private void changeTitles(ResourceBundle res) {
    	if (isChange(fastRepBtn, "fastRep")) {
    		fastRepBtn.setToolTipText(res.getString("fastRep"));
    	}
    	if (isChange(consalBtn, "consal")) {
    		consalBtn.setToolTipText(res.getString("consal"));
    	}
    	if (isChange(addBtn, "add")) {
    		addBtn.setToolTipText(res.getString("add"));
    	}
    	if (isChange(delBtn, "delete")) {
    		delBtn.setToolTipText(res.getString("delete"));
    	}
    	if (isChange(findBtn, "find")) {
    		findBtn.setToolTipText(res.getString("find"));
    	}
    	if (isChange(filterBtn, "filter")) {
    		filterBtn.setToolTipText(res.getString("filter"));
    	}
    	if (isChange(copyRowsBtn, "copyRows")) {
    		copyRowsBtn.setToolTipText(res.getString("copyRows"));
    	}
    	if (isChange(yesManBtn, "goDown")) {
    		yesManBtn.setToolTipText(res.getString("goDown"));
    	}
    	if (isChange(downBtn, "moveDown")) {
    		downBtn.setToolTipText(res.getString("moveDown"));
    	}
    	if (isChange(upBtn, "moveUp")) {
    		upBtn.setToolTipText(res.getString("moveUp"));
    	}
    	if (isChange(showDelBtn, "showDeleted")) {
    		showDelBtn.setToolTipText(res.getString("showDeleted"));
    	}
    }
    
    private boolean isChange(JButton button, String key) {
    	String toolTipText = button.getToolTipText();
    	String ru = ((ResourceBundle) new RuntimeResources_ru()).getString(key);
    	String kz = ((ResourceBundle) new RuntimeResources_kk()).getString(key);
    	if (toolTipText.equals(ru) || toolTipText.equals(kz)) {
    		return true;
    	}
    	return false;
    }

    /**
     * Установить interfase lang id.
     * 
     * @param langId
     *            the new interfase lang id
     */
    public void setInterfaseLangId(long langId) {
        OrTableNavigator.this.ifcLangId = langId;
        LangItem li = LangItem.getById(langId);
        if (li != null) {
            if ("KZ".equals(li.code)) {
                res = ResourceBundle.getBundle(Constants.NAME_RESOURCES, new Locale("kk"));
            } else {
                res = ResourceBundle.getBundle(Constants.NAME_RESOURCES, new Locale("ru"));
            }
            changeTitles(res);
        }
        if (filtersPanel.getComponentCount() > 0) {
            long[] ids = new long[filters.values().size()];
            int i = 0;
            for (Iterator it = filters.values().iterator(); it.hasNext();) {
                ids[i++] = ((Filter) it.next()).obj.id;
            }
            try {
                KrnClass cls = Kernel.instance().getClassByName("Filter");
                StringValue[] sv = Kernel.instance().getStringValues(ids, cls.id, "title", langId, false, 0);
                for (i = 0; i < sv.length; ++i) {
                    if (sv[i].index == 0) {
                        Filter f = (Filter) filters.get(sv[i].objectId);
                        f.setTitle(sv[i].value, langId);
                    }
                }
            } catch (KrnException e) {
                e.printStackTrace();
            }
            for (i = 0; i < filtersPanel.getComponentCount(); ++i) {
                Component c = filtersPanel.getComponent(i);
                if (c instanceof FilterMenuItem) {
                    ((FilterMenuItem) c).filter.setLangId(langId);
                    ((FilterMenuItem) c).setText(((FilterMenuItem) c).filter.toString());
                }
            }
            // daulet
            // ((JMenuItem)filtersPanel.getComponent(filtersPanel.getComponentCount()-1)).setText(res.getString("cancelApplyFilter"));
        }
    }

    /**
     * Установить message.
     * 
     * @param msg
     *            the new message
     */
    public void setMessage(String msg) {
        messageLabel.setText(msg);
    }

    
    @Override
    public void setBackground(Color bg) {
        super.setBackground(bg);
        if (toolBar != null)
            toolBar.setBackground(bg);
    }

    /**
     * Adds the action.
     * 
     * @param action
     *            the action
     */
    public void addAction(Action action) {
        JButton button = ButtonsFactory.createToolButton(action);
        Utils.setAllSize(button, sizeBtn);
        button.setOpaque(false);
        button.setHideActionText(true);
        button.setToolTipText((String) action.getValue(Action.NAME));
        toolBar.add(button);
        actBtn.add(button);
        addNaviSeparator();
        indxBtn[table.countBtn + iProc++] = true;
    }

    /**
     * Создаёт диалоговое окошко выбора и установки фильтров Если объект
     * существует, то повторной инициализации не происходит.
     */
    private void initDialog() {
        if (dlg == null) { // если объекта не существует
            JScrollPane content = new JScrollPane(filtersPanel, JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            filtersPanel.setOpaque(isOpaque);
            content.setOpaque(isOpaque);
            content.getViewport().setOpaque(isOpaque);
            content.setMinimumSize(new Dimension(10, 10));
            content.setMaximumSize(new Dimension(800, 800));
            content.setPreferredSize(new Dimension(400, 300));
            dlg = kz.tamur.comps.Utils.getFilterMenu(getTopLevelAncestor(), res.getString("selectFilters"), content, ifcLangId);
            dlg.pack();
        }
    }

    /**
     * Создаёт PopUp меню выбора и установки фильтров Если объект существует, то
     * повторной инициализации не происходит.
     * 
     * @param multi
     *            мультивыбор?
     */
    private void initMenu(boolean multi) {
        if (menu == null) { // если объекта не существует
            // запомнить количество фильтров
            final int count = filtersPanel.getComponentCount();
            // меню
            menu = new JPopupMenu();
            menu.setFont(Utils.getDefaultFont());
            // задание элементов меню (по количеству фильтров)
            // собрать список Фильтров-объектов
            filtersObj = new ArrayList<FilterObject>();

            for (int i = 0; i < count; i++) {
                Component c = filtersPanel.getComponent(i);
                if (c instanceof FilterMenuItem) {
                    filtersObj.add(new FilterObject(((FilterMenuItem) c).getText(), ((FilterMenuItem) c).filter));
                }
            }

            for (FilterObject filterObj : filtersObj) {
                if (multi) {
                    menu.add(item = new OrCheckBoxMenuItemFilter(filterObj.getTitle(), filterObj));
                    itemFilter.add(item);
                } else {
                    menu.add(itemRadio = new OrRadioButtonMenuItemFilter(filterObj.getTitle(), filterObj));
                    // добавить пункт в группу
                    itemGroup.add(itemRadio);
                    // добавить пункт в список для удобного доступа
                    itemFilter.add(itemRadio);
                }
            }

            if (!multi) {
                // добавить скрытый компоенент, который используется при необходимости убрать выделение во всех радиобаттонов
                menu.add(hideItem);
                hideItem.setVisible(false);
                itemGroup.add(hideItem);
            }
            // добавить в меню разделитель
            menu.addSeparator();
            // добавить в меню элемент
            menu.add(itemCancel);
        }
    }

    /**
     * Инициализация древовидного меню
     * 
     * @param multi
     *            мультивыбор?
     */
    private void initTreeMenu(boolean multi) {
        if (menu == null) {
            // Определить список всех фильтров
            filtersObj = new ArrayList<FilterObject>();
            for (Component c : filtersPanel.getComponents()) {
                if (c instanceof FilterMenuItem) {
                    filtersObj.add(new FilterObject(((FilterMenuItem) c).getText(), ((FilterMenuItem) c).filter));
                }
            }
            /*
             * найти предка с установленным атрибутом отображения узла в меню
             * перебор списка фильтров для нахождения всех эленентов первого уровня
             * перебор идёт снижу вверх, от листьев к корню
             */
            for (FilterObject filter : filtersObj) {
                KrnObject parent = null;
                long[] oids;
                // проход вверх по дереву для получения элемента первого уровня
                long id = filter.getFilter().obj.id;
                long classId = filter.getFilter().obj.classId;
                String title;
                while (true) {
                    title = null;
                    oids = new long[] { id };
                    ObjectValue[] ovs = null;
                    /** Признак узла первого уровня. */
                    boolean isNodeMenu = false;
                    try {
                        ovs = krn.getObjectValues(oids, classId, "parent", 0);
                        if (ovs == null || ovs.length == 0) {
                            System.out.println("Предок фильтра не найден!");
                            parent = null;
                            break;
                        }
                        parent = ovs[0].value;
                        oids = new long[] { parent.id };
                        byte[] data = krn.getBlob(parent, "config", 0, 0, 0);
                        if (data.length > 0) {
                            InputStream is = new ByteArrayInputStream(data);
                            Element xml = new SAXBuilder().build(is).getRootElement();
                            Element e = xml.getChild("isNodeMenu");
                            isNodeMenu = e != null ? "true".equals(e.getText()) : false;
                            e = xml.getChild("title");
                            title = e.getText();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (isNodeMenu) {
                        // если атрибут установлен, то данный узел является первоуровневым
                        break;
                    } else {
                        // иначе проанализировать нового предка уже найденного предка узла
                        id = parent.id;
                        classId = parent.classId;
                    }
                }
                // добавить элемент в дерево
                // проверить, возможно он уже там присутствует, если нет, добавить туда
                if (!root.isInTheNexts(parent)) {
                    FilterObject filterObj = getFilterObj(parent, filtersObj);
                    if (filterObj == null) {
                        filterObj = new FilterObject(title, new Filter(parent, ifcLangId, "FilterFolder", 0));
                    }
                    // Уточнение! Узел отмечается как первоуровневый, последним параметром в конструкторе
                    OrNodeTree node = new OrNodeTree(parent.id, root, new ArrayList<OrNodeTree>(), filterObj, true);
                    root.addChild(node);
                }

            }
            // собрать дерево объектов
            root = buildTree(root);

            // построение визуального меню
            menu = StartBuildMenu(root, multi);

            if (!multi) {
                // добавить скрытый компонент, который используется при необходимости убрать выделение во всех радиобаттонов
                menu.add(hideItem);
                hideItem.setVisible(false);
                itemGroup.add(hideItem);
            }

            // добавить в меню разделитель
            menu.addSeparator();
            // добавить в меню элемент
            menu.add(itemCancel);
        }
    }

    /**
     * Сборка дерева объектов для построения меню (рекурсия).
     * 
     * @param root
     *            дерева обектов
     * @return дерево объектов
     */
    private OrNodeTree buildTree(OrNodeTree root) {
        if (root != null && root.getNext() != null) {
            // если корневой узел существует
            // получить его потомков
            List<OrNodeTree> nextNodes = root.getNext();
            // Если потомки существуют, то для них запускается рекурсия
            // (данная ветка необходима в случае когда определены узлы первого уровня и отталкиваясь от них нужно строить дерево)
            if (nextNodes.size() > 0) {
                for (OrNodeTree next : nextNodes) {
                    buildTree(next);
                }
            } else if (nextNodes.size() == 0) {
                // если опредеолённых узлов у дерева не найдено
                long[] oids = { ((FilterObject) root.getValue()).getFilter().obj.id };
                ObjectValue[] ovs = null;
                try {
                    // получить значение (Value) потомков данного узла
                    ovs = krn.getObjectValues(oids, ((FilterObject) root.getValue()).getFilter().obj.classId, "children", 0);
                    for (ObjectValue obj : ovs) {
                        OrNodeTree next;
                        if (obj.value.classId == flrCls.id) {
                            // если это лист , добавить его в дерево, но только если он есть в списке фильтров
                            if (containsObjInList(obj.value, filtersObj)) {
                                root.addChild(next = new OrNodeTree(obj.value.id, root, null, getFilterObj(obj.value, filtersObj)));
                                // отметить ветку с добавленным листом флагом добавления листа
                                root.setAddedLeaf(true);
                                // запустить рекурсию для листа
                                buildTree(next);
                            }
                        } else {
                            // если это узел (папка фильтров)
                            // определить для данного объекта его Фильтр-объект
                            FilterObject filterObj = getFilterObj(obj.value, filtersObj);
                            if (filterObj == null) {
                                // Если Фильтр-объект не найден - то создать его
                                // неоходимо для создания корректного пути к фильтру по нескольким папкам
                                // получить название папки
                                String title = null;
                                byte[] data = krn.getBlob(obj.value, "config", 0, 0, 0);
                                if (data.length > 0) {
                                    InputStream is = new ByteArrayInputStream(data);
                                    SAXBuilder builder = new SAXBuilder();
                                    Element xml = builder.build(is).getRootElement();
                                    Element e = xml.getChild("title");
                                    title = e.getText();
                                }
                                // Создать Фильтр-объект
                                filterObj = new FilterObject(title, new Filter(obj.value, ifcLangId, "FilterFolder", 0));
                            }
                            // Создать с помощью рекурсии ветку дерева для данного узла
                            OrNodeTree node = buildTree(new OrNodeTree(obj.value.id, root, new ArrayList<OrNodeTree>(), filterObj));
                            if (node.isAddedLeaf() && !isFirstLevelNode(node)) {
                                // Добавить ветку к корневому узлу только если у ветки есть листья и ветка не является узлом первого уровня
                                // отметить корневой узел как содержащий листья
                                root.setAddedLeaf(true);
                                // Добавить ветку.
                                root.addChild(node);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return root;
    }

    /**
     * Проверяет, является ли узел, узлом первого уровня.
     * 
     * @param node
     *            узел для проверки
     * @return true, если узел первоуровневый
     */
    private boolean isFirstLevelNode(OrNodeTree node) {
        // получить список всех первоуровневых узлов
        List<OrNodeTree> nexts = root.getNext();
        if (nexts != null) {
            for (OrNodeTree next : nexts) {
                // сравнение значений узлов
                if (((FilterObject) node.getValue()).getFilter().obj.equals(((FilterObject) next.getValue()).getFilter().obj)
                        && next.isNodeMenu()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Запуск сборки меню.
     * 
     * @param root
     *            дерево объектов, по которому строится меню
     * @param multi
     *            мультивибор пунктов?
     * @return всплывающее меню
     */
    private JPopupMenu StartBuildMenu(OrNodeTree root, boolean multi) {
        menu = new JPopupMenu();
        menu.setFont(Utils.getDefaultFont());
        menu.setForeground(Color.black);
        List<OrNodeTree> nextNodes = root.getNext();
        if (nextNodes != null) {
            for (OrNodeTree next : nextNodes) {
                // добавлять только папки с фильтрами, обработка первоуровневых фильтров(листьев) не предусмотрена
                if (next.getNext() != null) {
                    menu.add(buildMenu(new OrPopupMenu(((FilterObject) next.getValue()).getTitle()), next, multi));
                }
            }
        }
        return menu;
    }

    /**
     * Сборка визуального меню (рекурсия!).
     * 
     * @param menu
     *            компонент меню, на который происходит нанизываение пунктов
     * @param root
     *            дерево объектов, по котороу строится меню
     * @param multi
     *            мультивыбор пунктов?
     * @return собранное меню
     */
    private JMenu buildMenu(JMenu menu, OrNodeTree root, boolean multi) {
        List<OrNodeTree> nextNodes = root.getNext();
        if (nextNodes != null) {
            for (OrNodeTree next : nextNodes) {
                if (next.getNext() == null) {
                    FilterObject nextFilterObj = (FilterObject) next.getValue();
                    if (multi) {
                        menu.add(item = new OrCheckBoxMenuItemFilter(nextFilterObj.getTitle(), nextFilterObj));
                        // добавить пункт в список для удобного доступа
                        itemFilter.add(item);
                    } else {
                        menu.add(itemRadio = new OrRadioButtonMenuItemFilter(nextFilterObj.getTitle(), nextFilterObj));
                        // добавить пункт в группу
                        itemGroup.add(itemRadio);
                        // добавить пункт в список для удобного доступа
                        itemFilter.add(itemRadio);
                    }
                } else {
                    menu.add(buildMenu(new OrPopupMenu(((FilterObject) next.getValue()).getTitle()), next, multi));
                }
            }
        }
        return menu;
    }

    /**
     * Установить градиентную заливку объекта.
     * 
     * @param gradient
     *            новое значение градиента
     */
    public void setGradient(GradientColor gradient) {
        startColor = gradient.getStartColor();
        endColor = gradient.getEndColor();
        orientation = gradient.getOrientation();
        isCycle = gradient.isCycle();
        positionStartColor = gradient.getPositionStartColor();
        positionEndColor = gradient.getPositionEndColor();
        isEnableGradient = gradient.isEnabled();
        repaint();
    }

    /**
     * Получить значение количества строк на страницу вывода информации.
     * 
     * @return значение количества строк на страницу вывода информации.
     */
    public int getCountRowPage() {
        return setterCountRowPage.getValue();
    }

    /**
     * Установить значение количества строк на страницу вывода информации.
     * 
     * @param value
     *            новое количество строк
     */
    public void setCountRowPage(int value) {
        setterCountRowPage.setValue(value);
    }

    public void saveCountRowPage(int value) {
        ConfigObject.instance(krn).saveProperty(table.UUID, "countRowPage", value + "");
    }

    /**
     * Проверить существование объекта KrnObject в качестве значения элементов списка Фильтров-объектов.
     * 
     * @param value
     *            KrnObject в соответствии с которым ищется Фильтр-объект
     * @param filtersObj
     *            Список фильтров-объектов, в котором производится поиск
     * @return <code>true</code>, в случае успеха поиска
     */
    private boolean containsObjInList(KrnObject value, List<FilterObject> filtersObj) {
        for (FilterObject filter : filtersObj) {
            if (filter.getFilter().obj.equals(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Получить Фильтр-объект по KrnObject.
     * 
     * @param value
     *            KrnObject в соответствии с которым ищется Фильтр-объект
     * @param filtersObj
     *            Список фильтров-объектов, в котором производится поиск
     * @return Фильтр-объект или <code>null</code>, если объект не найден в списке
     */
    private FilterObject getFilterObj(KrnObject value, List<FilterObject> filtersObj) {
        for (FilterObject filter : filtersObj) {
            if (filter.getFilter().obj.equals(value)) {
                return filter;
            }
        }
        return null;
    }

    /**
     * Пункт меню - флажок с хранением фильтра ему принадлежащим
     * 
     * @author Sergey Lebedev
     * 
     */
    class OrCheckBoxMenuItemFilter extends OrCheckBoxMenuItem {

        private FilterObject filter;

        public OrCheckBoxMenuItemFilter(String title, FilterObject filter) {
            super(title);
            this.filter = filter;
            setFont(Utils.getDefaultFont());
            setOpaque(true);
            setGradient(Constants.SE_UI ? MainFrame.GRADIENT_MENU_PANEL : MainFrame.GRADIENT_MAIN_FRAME);
            addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    AbstractButton aButton = (AbstractButton) event.getSource();
                    ((OrCheckBoxMenuItemFilter) event.getSource()).getFilter().setEnabled(aButton.getModel().isSelected());
                    // сформировать список активных фильтров
                    List<Filter> filters = new ArrayList<Filter>();
                    tableAdapter.cancelFilterAction();
                    for (FilterObject filter : filtersObj) {
                        if (filter.isEnabled()) {
                            filters.add(filter.getFilter());
                        }
                    }
                    try {
                        // активировать фильтры
                        tableAdapter.applyFilters(filters);
                    } catch (KrnException e1) {
                        e1.printStackTrace();
                    }
                }
            });
        }

        /**
         * Получить фильтр-объект
         * 
         * @return
         */
        public FilterObject getFilter() {
            return filter;
        }

        /**
         * Задать фильтр-объект
         * 
         * @param filter
         */
        public void setFilter(FilterObject filter) {
            this.filter = filter;
        }
    }

    /**
     * Пункт меню - переключатель с хранением фильтра ему принадлежащим
     * 
     * @author Sergey Lebedev
     * 
     */
    class OrRadioButtonMenuItemFilter extends OrRadioButtonMenuItem {

        private FilterObject filter;

        public OrRadioButtonMenuItemFilter(String title, FilterObject filter) {
            super(title);
            this.filter = filter;
            setFont(Utils.getDefaultFont());
            setOpaque(true);
            setGradient(Constants.SE_UI ? MainFrame.GRADIENT_MENU_PANEL : MainFrame.GRADIENT_MAIN_FRAME);
            addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    AbstractButton aButton = (AbstractButton) event.getSource();
                    if (aButton.getModel().isSelected()) {
                        // сформировать список активных фильтров
                        List<Filter> filters = new ArrayList<Filter>();
                        filters.add(((OrRadioButtonMenuItemFilter) event.getSource()).getFilter().getFilter());
                        try {
                            // активировать фильтр
                            tableAdapter.applyFilters(filters);
                        } catch (KrnException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            });
        }

        /**
         * Получить фильтр-объект
         * 
         * @return
         */
        public FilterObject getFilter() {
            return filter;
        }

        /**
         * Задать фильтр-объект
         * 
         * @param filter
         */
        public void setFilter(FilterObject filter) {
            this.filter = filter;
        }
    }

    /**
     * Класс описывает меню с градиентной заливкой
     * 
     * @author Sergey Lebedev
     */
    class OrPopupMenu extends OrMenu {

        /**
         * Конструктор класса.
         * 
         * @param заголовок
         *            меню
         */
        public OrPopupMenu(String s) {
            super(s);
            subMenu = true;
            init();
        }

        /**
         * Инициализация меню
         */
        private void init() {
            setOpaque(true);
            setFont(Utils.getDefaultFont());
            setForeground(Utils.getDarkShadowSysColor());
            setGradient(Constants.SE_UI ? MainFrame.GRADIENT_MENU_PANEL : MainFrame.GRADIENT_MAIN_FRAME);
        }
    }

    /**
     * Класс описывает пункт меню с градиентной заливкой
     * 
     * @author Sergey Lebedev
     */
    class OrFilterMenuItem extends OrMenuItem {

        /**
         * Создание нового пункта меню.
         * 
         * @param s
         *            заголовок пункта меню
         */
        public OrFilterMenuItem(String s) {
            super(s);
            setFont(Utils.getDefaultFont());
            setOpaque(true);
            setGradient(Constants.SE_UI ? MainFrame.GRADIENT_MENU_PANEL : MainFrame.GRADIENT_MAIN_FRAME);
        }

        public void setText(String text) {
            final int beg = text.indexOf('&');
            if (beg > -1) {
                final char m = text.charAt(beg + 1);
                super.setText(text.substring(0, beg) + text.substring(beg + 2));
                setMnemonic(m);
                setDisplayedMnemonicIndex(beg);
            } else {
                super.setText(text);
            }
        }
    }
}
