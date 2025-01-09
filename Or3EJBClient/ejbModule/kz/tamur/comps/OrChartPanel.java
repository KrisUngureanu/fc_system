package kz.tamur.comps;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventListener;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.event.EventListenerList;

import org.jdom.Element;
import org.jfree.chart.ChartPanel;

import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.util.expr.Editor;

import kz.tamur.comps.models.ChartPropertyRoot;
import kz.tamur.comps.models.EnumValue;
import kz.tamur.comps.models.GradientColor;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.models.Types;
import kz.tamur.comps.ui.GradientPanel;
import kz.tamur.guidesigner.PropertyListener;
import kz.tamur.guidesigner.filters.FilterRecord;
import kz.tamur.lang.OrLang;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.or3.client.props.Expression;
import kz.tamur.rt.adapters.ChartAdapter;
import kz.tamur.rt.adapters.OrRef;
import kz.tamur.util.LangItem;
import kz.tamur.util.Pair;
import static kz.tamur.comps.Constants.*;

/**
 * The Class OrChartPanel.
 * 
 * @author Sergey Lebedev
 * 
 *         Класс Реализует компонент вывода диаграмм и графиков
 */
public class OrChartPanel extends JPanel implements OrGuiComponent, MouseTarget {
    
    private java.util.List<OrGuiComponent> listListeners = new ArrayList<OrGuiComponent>();
    protected String UUID;
    private PropertyNode PROPS = new ChartPropertyRoot();

    /** The pov. */
    PropertyNode pov;

    /** The pn chart. */
    private PropertyNode pnChart;
    // изначальные, полные свойства компонента
    /** The original props. */
    private final static PropertyNode originalProps = new ChartPropertyRoot();
    // режим работы компонента
    /** The mode. */
    private int mode;

    /** The xml. */
    private Element xml;

    /** The frame. */
    private OrFrame frame;

    /** The title. */
    private String title;

    /** The title uid. */
    private String titleUID;

    /** The listeners. */
    private EventListenerList listeners = new EventListenerList();

    /** The constraints. */
    private GridBagConstraints constraints;

    /** The pref size. */
    private Dimension prefSize;

    /** The enabled. */
    private boolean enabled;

    /** The max size. */
    private Dimension maxSize;

    /** The min size. */
    private Dimension minSize;

    /** The adapter. */
    private ChartAdapter adapter;

    /** The gbl. */
    private GridBagLayout gbl = new GridBagLayout();

    /** The grid const. */
    private GridBagConstraints gridConst = new GridBagConstraints();

    /** The border type. */
    private Border borderType;

    /** The standart border. */
    private Border standartBorder;

    /** The copy border. */
    private Border copyBorder = BorderFactory.createLineBorder(kz.tamur.rt.Utils.getMidSysColor());

    /** The create xml template. */
    private ASTStart beforeOpenTemplate, afterOpenTemplate, beforeCloseTemplate, afterCloseTemplate, createXmlTemplate;

    /** The description uid. */
    private String descriptionUID;

    /** The border title uid. */
    private String borderTitleUID;

    /** The var name. */
    private String varName;

    /** The description. */
    private byte[] description;

    /** The is selected. */
    private boolean isSelected;

    /** The is copy. */
    private boolean isCopy;

    /** The gui parent. */
    private OrGuiContainer guiParent;

    /** идентификатор строки с подсказкой. */
    private String toolTipUid;
    /** Формула для вывода вспл. подсказки */
    private String toolTipExpr = null;
    /** Текст вспл. подсказки, сформированной по формуле */
    private String toolTipExprText = null;
    // тип диаграммы для компонента
    /** The type. */
    private int type = -1;
    // тип интерфейса

    /** The chart pan global. */
    private GradientPanel chartPanGlobal = new GradientPanel(new GridBagLayout());

    /** The control panel. */
    GradientPanel controlPanel = new GradientPanel(new GridBagLayout());;

    /** The chart. */
    private OrCharts chart;

    /** The is btn save jpeg. */
    private boolean isBtnSaveJPEG;

    /** The resolution jpeg. */
    private int resolutionJPEG;

    /** The is init. */
    private boolean isInit = true;

    /** Кнопка сохранения графика в виде картинки */
    private JButton btnSave;

    private ResourceBundle resource = ResourceBundle.getBundle(Constants.NAME_RESOURCES, new Locale("ru"));

    /** Делегатор собитий мыши. используется для перенаправления событий мыши компонентов на данный THIS компонент */
    private MouseDelegator delegator = new MouseDelegator(this);

    private boolean isSlider = false;

    /**
     * Конструктор класса.
     * 
     * @param xml
     *            the xml
     * @param mode
     *            the mode
     * @param fm
     *            the fm
     * @param frame
     *            the frame
     * @throws KrnException
     *             the krn exception
     */
    OrChartPanel(Element xml, int mode, Factory fm, OrFrame frame) throws KrnException {
        this.xml = xml;
        this.mode = mode;
        this.frame = frame;
        UUID = PropertyHelper.getUUID(this);
        init(fm, mode);
    }

    /**
     * Инициализация компонента.
     * 
     * @param fm
     *            the fm
     * @param mode
     *            the mode
     * @throws KrnException
     *             the krn exception
     */
    private void init(Factory fm, int mode) throws KrnException {
        String expr;
        setLayout(gbl);
        pov = PROPS.getChild("pov");
        pnChart = PROPS.getChild("chart");
        // Если компонент в режиме дизайна
        if (mode == Mode.DESIGN) {
            enableEvents(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
            setFocusable(true);
            doLayout();
            // перенаправляю события мыши компонентов на this
            controlPanel.addMouseListener(delegator);
            controlPanel.addMouseMotionListener(delegator);
            chartPanGlobal.addMouseListener(delegator);
            chartPanGlobal.addMouseMotionListener(delegator);

        }
        // Если компонент в режиме не в режиме предпросмота
        if (mode != Mode.PREVIEW) {
            adapter = new ChartAdapter(frame, this, false);
            kz.tamur.rt.Utils.setComponentFocusCircle(this);
        }

        // работа со свойствами компонента

        // активность компонента
        PropertyNode pn = pov.getChild("activity");
        PropertyValue pv = getPropertyValue(pn.getChild("enabled"));
        if (pv.isNull()) {
            enabled = true;
            setPropertyValue(new PropertyValue(enabled, pn.getChild("enabled")));
        } else {
            enabled = pv.booleanValue();
        }

        // Раздел "Диаграмма"

        // показать ли кнопку "сохранить диаграмму как картинку в формате JPEG"
        pv = getPropertyValue(pnChart.getChild("isBtnSaveJPEG"));
        if (pv.isNull()) {
            isBtnSaveJPEG = true;
            setPropertyValue(new PropertyValue(isBtnSaveJPEG, pnChart.getChild("isBtnSaveJPEG")));
        } else {
            isBtnSaveJPEG = pv.booleanValue();
        }
        // выбор разрешения для картинки диаграммы
        pv = getPropertyValue(pnChart.getChild("resolutionJPEG"));
        if (pv.isNull()) {
            resolutionJPEG = R1024_768;
            setPropertyValue(new PropertyValue(resolutionJPEG, pnChart.getChild("resolutionJPEG")));
        } else {
            resolutionJPEG = pv.intValue();
        }
        // прозрачность полосок диаграммы
        pv = getPropertyValue(pnChart.getChild("transparencyChartBar"));
        if (pv.isNull()) {
            setPropertyValue(new PropertyValue(5, pnChart.getChild("transparencyChartBar")));
        }

        // тип диаграммы
        pn = pnChart.getChild("typeChart");
        pv = getPropertyValue(pn);
        // Тип диаграммы для данного компонента
        type = ((pv.getProperty().getType() == Types.ENUM_TOOL_TIP) && (pv.isNull() || pv.intValue() == -1)) ? ((EnumValue) pn
                .getDefaultValue()).code : pv.intValue();

        pv = getPropertyValue(pn);
        if (pv.isNull()) {
            setPropertyValue(new PropertyValue(pn.getDefaultValue(), pn));
        }

        // Если диаграмма НЕ в режиме дизайна
        if (mode != Mode.DESIGN) {
            constraints = PropertyHelper.getConstraints(PROPS, xml);
            prefSize = PropertyHelper.getPreferredSize(this);
            maxSize = PropertyHelper.getMaximumSize(this);
            minSize = PropertyHelper.getMinimumSize(this);
            PropertyValue titleVal = getPropertyValue(PROPS.getChild("title"));
            titleUID = (titleVal.isNull()) ? "" : (String) titleVal.resourceStringValue().first;
            title = (titleVal.isNull()) ? "" : frame.getString(titleUID);
        }

        // Если режим выполнения
        if (mode == Mode.RUNTIME) {
            // всплывающая подсказка
            pv = getPropertyValue(PROPS.getChild("toolTip"));
            if (!pv.isNull()) {
                if (pv.objectValue() instanceof Expression) {
                    try {
                        toolTipExpr = ((Expression) pv.objectValue()).text;
                        toolTipExprText = Utils.getExpReturn(toolTipExpr, frame, getAdapter());
                        if (toolTipExprText != null && !toolTipExprText.isEmpty()) {
                            setToolTipText(toolTipExprText);
                        }
                    } catch (Exception e) {
                        System.out.println("Ошибка в формуле\r\n" + toolTipExpr + "\r\n" + e);
                    }
                } else {
                    toolTipUid = (String) pv.resourceStringValue().first;
                    byte[] toolTip = frame.getBytes(toolTipUid);
                    if (toolTip != null) {
                        setToolTipText(new String(toolTip));
                    }
                }
            }
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    updateToolTip();
                }
            });
            pn = pov.getChild("beforeOpen");
            if (pn != null) {
                pv = getPropertyValue(pn);
                expr = (pv.isNull()) ? null : pv.stringValue();
                if (expr != null && expr.length() > 0) {
                    beforeOpenTemplate = OrLang.createStaticTemplate(expr);
                    try {
                        ArrayList<String> paths = new Editor(expr).getRefPaths();
                        for (int j = 0; j < paths.size(); ++j) {
                            OrRef.createRef(paths.get(j), false, Mode.RUNTIME, frame.getRefs(), OrRef.TR_CLEAR, frame);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }

            pn = pov.getChild("afterOpen");
            if (pn != null) {
                pv = getPropertyValue(pn);
                expr = (pv.isNull()) ? null : pv.stringValue();
                if (expr != null && expr.length() > 0) {
                    afterOpenTemplate = OrLang.createStaticTemplate(expr);
                    try {
                        ArrayList<String> paths = new Editor(expr).getRefPaths();
                        for (int j = 0; j < paths.size(); ++j) {
                            OrRef.createRef(paths.get(j), false, Mode.RUNTIME, frame.getRefs(), OrRef.TR_CLEAR, frame);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }

            pn = pov.getChild("beforeClose");
            if (pn != null) {
                pv = getPropertyValue(pn);
                expr = (pv.isNull()) ? null : pv.stringValue();
                if (expr != null && expr.length() > 0) {
                    beforeCloseTemplate = OrLang.createStaticTemplate(expr);
                    try {
                        ArrayList<String> paths = new Editor(expr).getRefPaths();
                        for (int j = 0; j < paths.size(); ++j) {
                            OrRef.createRef(paths.get(j), false, Mode.RUNTIME, frame.getRefs(), OrRef.TR_CLEAR, frame);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            pn = pov.getChild("afterClose");
            if (pn != null) {
                pv = getPropertyValue(pn);
                expr = (pv.isNull()) ? null : pv.stringValue();
                if (expr != null && expr.length() > 0) {
                    afterCloseTemplate = OrLang.createStaticTemplate(expr);
                    try {
                        ArrayList<String> paths = new Editor(expr).getRefPaths();
                        for (int j = 0; j < paths.size(); ++j) {
                            OrRef.createRef(paths.get(j), false, Mode.RUNTIME, frame.getRefs(), OrRef.TR_CLEAR, frame);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            pn = pov.getChild("createXml");
            if (pn != null) {
                pv = getPropertyValue(pn);
                expr = (pv.isNull()) ? null : pv.stringValue();
                if (expr != null && expr.length() > 0) {
                    createXmlTemplate = OrLang.createStaticTemplate(expr);
                    try {
                        ArrayList<String> paths = new Editor(expr).getRefPaths();
                        for (int j = 0; j < paths.size(); ++j) {
                            OrRef.createRef(paths.get(j), false, Mode.RUNTIME, frame.getRefs(), OrRef.TR_CLEAR, frame);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }

        // настройка панели
        // компоновка на панели дополнительных компонентов
        // создание компонент
        btnSave = new JButton();

        btnSave.setIcon(kz.tamur.rt.Utils.getImageIconFull("chartSaveAsPict.png"));
        btnSave.setToolTipText("Сохранить график в виде картинки");
        btnSave.setPreferredSize(new Dimension(36, 36));
        btnSave.setHorizontalAlignment(JLabel.CENTER);
        btnSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // выбор местоназначения файла
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setSelectedFile(new File("fileToSave.jpeg"));
                jFileChooser.showSaveDialog(OrChartPanel.this);
                File file = jFileChooser.getSelectedFile();
                Dimension resolution = new Dimension();
                switch (resolutionJPEG) {
                case R1920_1200:
                    resolution.setSize(1920, 1200);
                    break;
                case R1920_1080:
                    resolution.setSize(1920, 1080);
                    break;
                case R1680_1050:
                    resolution.setSize(1680, 1050);
                    break;
                case R1600_900:
                    resolution.setSize(1600, 900);
                    break;
                case R1440_900:
                    resolution.setSize(1440, 900);
                    break;
                case R1366_768:
                    resolution.setSize(1366, 768);
                    break;
                case R1360_768:
                    resolution.setSize(1360, 768);
                    break;
                case R1280_1024:
                    resolution.setSize(1280, 1024);
                    break;
                case R1280_800:
                    resolution.setSize(1280, 800);
                    break;
                case R1280_768:
                    resolution.setSize(1280, 768);
                    break;
                case R1024_768:
                    resolution.setSize(1024, 768);
                    break;
                case R800_600:
                    resolution.setSize(800, 600);
                    break;
                case R640_480:
                    resolution.setSize(640, 480);
                    break;
                }
                if (file != null) {
                    chart.saveChart(file.getPath(), resolution);
                }
            }
        });

        gridConst.anchor = GridBagConstraints.LINE_START;
        gridConst.fill = GridBagConstraints.BOTH;
        gridConst.insets = Constants.INSETS_1;
        gridConst.gridx = 1;
        gridConst.gridy = 1;
        gridConst.weightx = 1;
        gridConst.weighty = 1;
        add(chartPanGlobal, gridConst);
        gridConst.gridy = 0;
        gridConst.weightx = 0;
        gridConst.weighty = 0;
        gridConst.fill = GridBagConstraints.HORIZONTAL;
        add(controlPanel, gridConst);
        gridConst.anchor = GridBagConstraints.LINE_START;
        gridConst.fill = GridBagConstraints.NONE;
        gridConst.weightx = 0.1;
        gridConst.weighty = 0.1;
        gridConst.insets = new Insets(3, 3, 3, 3);
        controlPanel.add(btnSave, gridConst);
        btnSave.setVisible(isBtnSaveJPEG);
        initDynamicProperty(false);
        constructChart(isInit);
        chartPanGlobal.addComponentListener(new ComponentListener() {
            public void componentShown(ComponentEvent e) {
            }

            public void componentResized(ComponentEvent e) {
                ChartPanel chartPane = chart.getChartPanel();
                if (chartPane != null) {
                    chartPane.setPreferredSize(chartPanGlobal.getPreferredSize());
                }
            }

            public void componentMoved(ComponentEvent e) {
            }

            public void componentHidden(ComponentEvent e) {
            }
        });
    }

    /**
     * Обновление свойств диаграммы.
     * 
     * @param type
     *            тип диаграммы
     * @param isAdd
     *            обновление с добавлением новых свойств
     */
    public void initDynProp(int type, boolean isAdd) {
        this.type = type;
        initDynamicProperty(isAdd);
    }

    /**
     * Обновление свойств диаграммы Раздел "Серии".
     * 
     * @param count
     *            the count
     * @param isAdd
     *            обновление с добавлением новых свойств
     */
    public void initDynPropSeries(int count, boolean isAdd) {

        if (mode != Mode.DESIGN) {
            return;
        }

        PropertyNode pn = pnChart.getChild("attr").getChild("series");

        final PropertyNode originalNode = originalProps.getChild("chart").getChild("attr").getChild("series");
        // массив свойств которые будут использоваться
        String[] prop = new String[count];

        for (int i = 0; i < count; ++i) {
            prop[i] = "series" + (i + 1) + "-601";
        }

        // если есть свойства для добавления
        if (prop.length != 0) {
            // Если сначала необходимо добавить заданные свойства
            if (isAdd) {
                for (String name : prop) {
                    // если в текущих свойствах нет необходимого свойства
                    if (pn.getChild(name) == null) {
                        // добавить свойство из оригинальной и полной ветки свойств
                        pn.addChild(originalNode.getChild(name));
                    }
                }
            }
            // удалить всех потомков, кроме заданных
            pn.removeAllChildExcept(prop);
        }
    }

    /**
     * Метод производит формирование динамических свойств под каждый график и диаграмму.
     * 
     * @param isAdd
     *            указывает необходимо ли добавлять свойства перед чисткой
     */
    public void initDynamicProperty(boolean isAdd) {
        if (mode != Mode.DESIGN) {
            return;
        }
        PropertyNode pn = pnChart.getChild("attr");

        final PropertyNode originalNode = originalProps.getChild("chart").getChild("attr");
        // массив свойств которые будут использоваться
        String[] prop = null;
        switch (type) {
        case CHART_TEST:
            prop = new String[] {};
            break;
        case CHART_GANTT_0:
            prop = new String[] {};
            break;
        case AREA_CHARTS:
            prop = new String[] {};
            break;
        case AREA_CHART_1:
            prop = new String[] {};
            break;
        case STACKED_AREA_CHART_1:
            prop = new String[] {};
            break;
        case STACKED_XY_AREA_CHART_1:
            prop = new String[] {};
            break;
        case STACKED_XY_AREA_CHART_2:
            prop = new String[] {};
            break;
        case STACKED_XY_AREA_RENDERER_1:
            prop = new String[] {};
            break;
        case XY_AREA_CHART_1:
            prop = new String[] {};
            break;
        case XY_AREA_CHART_2:
            prop = new String[] {};
            break;
        case XY_AREA_RENDERER_2_1:
            prop = new String[] {};
            break;
        case XY_STEP_AREA_RENDERER_1:
            prop = new String[] {};
            break;
        case BAR_CHARTS:
            prop = new String[] {};
            break;
        case CATEGORY_PLOT:
            prop = new String[] {};
            break;
        case BAR_CHART_1:
            prop = new String[] {};
            break;
        case BAR_CHART_2:
            prop = new String[] {};
            break;
        case BAR_CHART_3:
            prop = new String[] {};
            break;
        case BAR_CHART_4:
            prop = new String[] {};
            break;
        case BAR_CHART_5:
            prop = new String[] {};
            break;
        case BAR_CHART_6:
            prop = new String[] {};
            break;
        case BAR_CHART_7:
            prop = new String[] {};
            break;
        case BAR_CHART_8:
            prop = new String[] {};
            break;
        case BAR_CHART_9:
            prop = new String[] {};
            break;
        case BAR_CHART_10:
            prop = new String[] {};
            break;
        case BAR_CHART_11:
            prop = new String[] {};
            break;
        case BAR_CHART_3D_1:
            prop = new String[] {};
            break;
        case BAR_CHART_3D_2:
            prop = new String[] {};
            break;
        case BAR_CHART_3D_3:
            prop = new String[] {};
            break;
        case BAR_CHART_3D_4:
            prop = new String[] {};
            break;
        case CYLINDER_CHART_1:
            prop = new String[] {};
            break;
        case CYLINDER_CHART_2:
            prop = new String[] {};
            break;
        case INTERVAL_BAR_CHART_1:
            prop = new String[] {};
            break;
        case LAYERED_BAR_CHART_1:
            prop = new String[] {};
            break;
        case LAYERED_BAR_CHART_2:
            prop = new String[] {};
            break;
        case SLIDING_CATEGORY_DATASET_1:
            prop = new String[] {};
            break;
        case SLIDING_CATEGORY_DATASET_2:
            prop = new String[] {};
            break;
        case STATISTICAL_BAR_CHART_1:
            prop = new String[] {};
            break;
        case SURVEY_RESULTS_1:
            prop = new String[] {};
            break;
        case SURVEY_RESULTS_2:
            prop = new String[] {};
            break;
        case SURVEY_RESULTS_3:
            prop = new String[] {};
            break;
        case WATERFALL_CHART_1:
            prop = new String[] {};
            break;
        case XY_PLOT:
            prop = new String[] {};
            break;
        case XY_BAR_CHART_1:
            prop = new String[] {};
            break;
        case XY_BAR_CHART_2:
            prop = new String[] {};
            break;
        case XY_BAR_CHART_3:
            prop = new String[] {};
            break;
        case XY_BAR_CHART_4:
            prop = new String[] {};
            break;
        case XY_BAR_CHART_5:
            prop = new String[] {};
            break;
        case XY_BAR_CHART_6:
            prop = new String[] {};
            break;
        case XY_BAR_CHART_7:
            prop = new String[] {};
            break;
        case CLUSTERED_XY_BAR_RENDERER_1:
            prop = new String[] {};
            break;
        case STACKED_XY_BAR_CHART_1:
            prop = new String[] {};
            break;
        case STACKED_XY_BAR_CHART_2:
            prop = new String[] {};
            break;
        case STACKED_XY_BAR_CHART_3:
            prop = new String[] {};
            break;
        case XY_RELATIVE_DATE_FORMAT_1:
            prop = new String[] {};
            break;
        case XY_RELATIVE_DATE_FORMAT_2:
            prop = new String[] {};
            break;
        case BAR_CHARTS_STACKED:
            prop = new String[] {};
            break;
        case POPULATION_CHART_1:
            prop = new String[] {};
            break;
        case STACKED_BAR_CHART_1:
            prop = new String[] {};
            break;
        case STACKED_BAR_CHART_2:
            prop = new String[] {};
            break;
        case STACKED_BAR_CHART_3:
            prop = new String[] {};
            break;
        case STACKED_BAR_CHART_4:
            prop = new String[] {};
            break;
        case STACKED_BAR_CHART_5:
            prop = new String[] {};
            break;
        case STACKED_BAR_CHART_6:
            prop = new String[] {};
            break;
        case STACKED_BAR_CHART_7:
            prop = new String[] {};
            break;
        case STACKED_BAR_CHART_3D_1:
            prop = new String[] {};
            break;
        case STACKED_BAR_CHART_3D_2:
            prop = new String[] {};
            break;
        case STACKED_BAR_CHART_3D_3:
            prop = new String[] {};
            break;
        case STACKED_BAR_CHART_3D_4:
            prop = new String[] {};
            break;
        case STACKED_BAR_CHART_3D_5:
            prop = new String[] {};
            break;
        case COMBINED_AXIS_CHARTS:
            prop = new String[] {};
            break;
        case COMBINED_CATEGORY_PLOT_1:
            prop = new String[] {};
            break;
        case COMBINED_CATEGORY_PLOT_2:
            prop = new String[] {};
            break;
        case COMBINED_TIME_SERIES_1:
            prop = new String[] {};
            break;
        case COMBINED_XY_PLOT_1:
            prop = new String[] {};
            break;
        case COMBINED_XY_PLOT_2:
            prop = new String[] {};
            break;
        case COMBINED_XY_PLOT_3:
            prop = new String[] {};
            break;
        case COMBINED_XY_PLOT_4:
            prop = new String[] {};
            break;
        case FINANCIAL_CHARTS:
            prop = new String[] {};
            break;
        case CANDLESTICK_CHART_1:
            prop = new String[] {};
            break;
        case HIGHLOW_CHART_1:
            prop = new String[] {};
            break;
        case HIGHLOW_CHART_2:
            prop = new String[] {};
            break;
        case HIGHLOW_CHART_3:
            prop = new String[] {};
            break;
        case MOVING_AVERAGE_1:
            prop = new String[] {};
            break;
        case PRICE_VOLUME_1:
            prop = new String[] {};
            break;
        case PRICE_VOLUME_2:
            prop = new String[] {};
            break;
        case YIELD_CURVE_1:
            prop = new String[] {};
            break;
        case GANTT_CHARTS:
            prop = new String[] {};
            break;
        case GANTT_0: // простейшая диаграмма Гантта
            prop = new String[] { "nameTaskSeries", "leftVerticlLabel", "upHorizontalLabel", "taskPath601", "startPath601",
                    "endPath601" };
            break;
        case GANTT_1: // диаграмма Гантта с изменяемым количеством серий
            prop = new String[] { "nameTaskSeries", "leftVerticlLabel", "upHorizontalLabel", "taskPath601", "startPath601",
                    "endPath601", "nameAttrSeries601", "countSeries601", "series", "taskSeries" };
            break;
        case GANTT_2:
            prop = new String[] { "bla-bla-bla1", "bla-bla-bla2", "bla-bla-bla3" };
            break;
        case SLIDING_GANTT_DATASET_1:
            prop = new String[] { "nameTaskSeries", "leftVerticlLabel", "upHorizontalLabel", "taskPath601", "startPath601",
                    "endPath601", "nameAttrSeries601", "countSeries601", "series", "taskSeries", "countVisibleTask" };
            break;
        case XY_TASK_DATASET_1:
            prop = new String[] {};
            break;
        case XY_TASK_DATASET_2:
            prop = new String[] {};
            break;
        case LINE_CHARTS:
            prop = new String[] {};
            break;
        case ANNOTATION_1:
            prop = new String[] {};
            break;
        case LINE_CHART_1:
            prop = new String[] {};
            break;
        case LINE_CHART_2:
            prop = new String[] {};
            break;
        case LINE_CHART_3:
            prop = new String[] {};
            break;
        case LINE_CHART_4:
            prop = new String[] {};
            break;
        case LINE_CHART_5:
            prop = new String[] {};
            break;
        case LINE_CHART_6:
            prop = new String[] {};
            break;
        case LINE_CHART_7:
            prop = new String[] {};
            break;
        case LINE_CHART_8:
            prop = new String[] {};
            break;
        case LINE_CHART_3D_1:
            prop = new String[] {};
            break;
        case STATISTICAL_LINE_CHART_1:
            prop = new String[] {};
            break;
        case XY_SPLINE_RENDERER_1:
            prop = new String[] {};
            break;
        case XY_STEP_RENDERER_1:
            prop = new String[] {};
            break;
        case XY_STEP_RENDERER_2:
            prop = new String[] {};
            break;
        case DIAL_METER_CHARTS:
            prop = new String[] {};
            break;
        case DIAL_1:
            prop = new String[] {};
            break;
        case DIAL_2:
            prop = new String[] {};
            break;
        case DIAL_2A:
            prop = new String[] {};
            break;
        case DIAL_3:
            prop = new String[] {};
            break;
        case DIAL_4:
            prop = new String[] {};
            break;
        case DIAL_5:
            prop = new String[] {};
            break;
        case METER_CHART_1:
            prop = new String[] {};
            break;
        case METER_CHART_2:
            prop = new String[] {};
            break;
        case METER_CHART_3:
            prop = new String[] {};
            break;
        case THERMOMETER_1:
            prop = new String[] {};
            break;
        case MULTIPLE_AXIS_CHARTS:
            prop = new String[] {};
            break;
        case DUAL_AXIS_1:
            prop = new String[] {};
            break;
        case DUAL_AXIS_2:
            prop = new String[] {};
            break;
        case DUAL_AXIS_3:
            prop = new String[] {};
            break;
        case DUAL_AXIS_4:
            prop = new String[] {};
            break;
        case DUAL_AXIS_5:
            prop = new String[] {};
            break;
        case MULTIPLE_AXIS_1:
            prop = new String[] {};
            break;
        case MULTIPLE_AXIS_2:
            prop = new String[] {};
            break;
        case MULTIPLE_AXIS_3:
            prop = new String[] {};
            break;
        case PARETO_CHART_1:
            prop = new String[] {};
            break;
        case OVERLAID_CHARTS:
            prop = new String[] {};
            break;
        case OVERLAID_BAR_CHART_1:
            prop = new String[] {};
            break;
        case OVERLAID_BAR_CHART_2:
            prop = new String[] {};
            break;
        case OVERLAID_XY_PLOT_1:
            prop = new String[] {};
            break;
        case OVERLAID_XY_PLOT_2:
            prop = new String[] {};
            break;
        case PIE_CHARTS:
            prop = new String[] {};
            break;
        case PIE_CHART_1:
            prop = new String[] {};
            break;
        case PIE_CHART_2:
            prop = new String[] {};
            break;
        case PIE_CHART_3:
            prop = new String[] {};
            break;
        case PIE_CHART_4:
            prop = new String[] {};
            break;
        case PIE_CHART_5:
            prop = new String[] {};
            break;
        case PIE_CHART_6:
            prop = new String[] {};
            break;
        case PIE_CHART_7:
            prop = new String[] {};
            break;
        case PIE_CHART_8:
            prop = new String[] {};
            break;
        case PIE_CHART_3D_1:
            prop = new String[] {};
            break;
        case PIE_CHART_3D_2:
            prop = new String[] {};
            break;
        case PIE_CHART_3D_3:
            prop = new String[] {};
            break;
        case MULTIPLE_PIE_CHART_1:
            prop = new String[] {};
            break;
        case MULTIPLE_PIE_CHART_2:
            prop = new String[] {};
            break;
        case MULTIPLE_PIE_CHART_3:
            prop = new String[] {};
            break;
        case MULTIPLE_PIE_CHART_4:
            prop = new String[] {};
            break;
        case RING_CHART_1:
            prop = new String[] {};
            break;
        case STATISTICAL_CHARTS:
            prop = new String[] {};
            break;
        case BOX_AND_WHISKER_CHART_1:
            prop = new String[] {};
            break;
        case BOX_AND_WHISKER_CHART_2:
            prop = new String[] {};
            break;
        case HISTOGRAM_1:
            prop = new String[] {};
            break;
        case HISTOGRAM_2:
            prop = new String[] {};
            break;
        case MIN_MAX_CATEGORYPLOT_1:
            prop = new String[] {};
            break;
        case NORMAL_DISTRIBUTION_1:
            prop = new String[] {};
            break;
        case NORMAL_DISTRIBUTION_2:
            prop = new String[] {};
            break;
        case REGRESSION_1:
            prop = new String[] {};
            break;
        case SCATTER_PLOT_1:
            prop = new String[] {};
            break;
        case SCATTER_PLOT_2:
            prop = new String[] {};
            break;
        case SCATTER_PLOT_3:
            prop = new String[] {};
            break;
        case SCATTER_PLOT_4:
            prop = new String[] {};
            break;
        case XY_ERROR_RENDERER_1:
            prop = new String[] {};
            break;
        case XY_ERROR_RENDERER_2:
            prop = new String[] {};
            break;
        case TIME_SERIES_CHARTS:
            prop = new String[] {};
            break;
        case TIME_RENDERER_1:
            prop = new String[] {};
            break;
        case TIME_SERIES_2:
            prop = new String[] {};
            break;
        case TIME_SERIES_3:
            prop = new String[] {};
            break;
        case TIME_SERIES_4:
            prop = new String[] {};
            break;
        case TIME_SERIES_5:
            prop = new String[] {};
            break;
        case TIME_SERIES_6:
            prop = new String[] {};
            break;
        case TIME_SERIES_7:
            prop = new String[] {};
            break;
        case TIME_SERIES_8:
            prop = new String[] {};
            break;
        case TIME_SERIES_9:
            prop = new String[] {};
            break;
        case TIME_SERIES_10:
            prop = new String[] {};
            break;
        case TIME_SERIES_11:
            prop = new String[] {};
            break;
        case TIME_SERIES_12:
            prop = new String[] {};
            break;
        case TIME_SERIES_13:
            prop = new String[] {};
            break;
        case PERIOD_AXIS_1:
            prop = new String[] {};
            break;
        case PERIOD_AXIS_2:
            prop = new String[] {};
            break;
        case PERIOD_AXIS_3:
            prop = new String[] {};
            break;
        case RELATIVE_DATE_FORMAT_1:
            prop = new String[] {};
            break;
        case DEVIATION_RENDERER_1:
            prop = new String[] {};
            break;
        case DEVIATION_RENDERER_2:
            prop = new String[] {};
            break;
        case DIFFERENCE_CHART_1:
            prop = new String[] {};
            break;
        case DIFFERENCE_CHART_2:
            prop = new String[] {};
            break;
        case COMPARE_TO_PREVIOUS_YEAR:
            prop = new String[] {};
            break;
        case XY_CHARTS:
            prop = new String[] {};
            break;
        case XY_SCATTER_PLOT_1:
            prop = new String[] {};
            break;
        case XY_SCATTER_PLOT_2:
            prop = new String[] {};
            break;
        case XY_SCATTER_PLOT_3:
            prop = new String[] {};
            break;
        case LOG_AXIS_1:
            prop = new String[] {};
            break;
        case FUNCTION_2D_1:
            prop = new String[] {};
            break;
        case XY_BLOCK_CHART_1:
            prop = new String[] {};
            break;
        case XY_BLOCK_CHART_2:
            prop = new String[] {};
            break;
        case XY_BLOCK_CHART_3:
            prop = new String[] {};
            break;
        case XY_LINE_AND_SHAPE_RENDERER_1:
            prop = new String[] {};
            break;
        case XY_LINE_AND_SHAPE_RENDERER_2:
            prop = new String[] {};
            break;
        case XY_SERIES_1:
            prop = new String[] {};
            break;
        case XY_SERIES_2:
            prop = new String[] {};
            break;
        case XY_SERIES_3:
            prop = new String[] {};
            break;
        case XY_SHAPE_RENDERER_1:
            prop = new String[] {};
            break;
        case VECTOR_PLOT_1:
            prop = new String[] {};
            break;
        }
        // если есть свойства для добавления
        if (prop != null && prop.length != 0) {
            // Если сначала необходимо добавить заданные свойства
            if (isAdd) {
                for (String name : prop) {
                    // если в текущих свойствах нет необходимого свойства
                    if (pn.getChild(name) == null) {
                        // добавить свойство из оригинальной и полной ветки свойств
                        pn.addChild(originalNode.getChild(name));
                    }
                }
            }
            // удалить всех потомков, кроме заданных
            pn.removeAllChildExcept(prop);
            // инициализация вложенных динамических свойст
            PropertyValue pv;
            switch (type) {
            case GANTT_1:
                // получить количество серий
                pv = getPropertyValue(pnChart.getChild("attr").getChild("countSeries601"));
                int count = 3;
                if (!pv.isNull()) {
                    count = pv.intValue();
                }
                initDynPropSeries(count, isAdd);
                break;
            }

        }
    }

    /**
     * Компоновка и вывод диаграмм.
     * 
     * @param isInit
     *            the is init
     */
    private void constructChart(boolean isInit) {
        PropertyValue pv;
        chart = new OrCharts(this, title, mode);
        chart.setLeftVerticlLabel(getAttrString("leftVerticlLabel"));
        chart.setUpHorizontalLabel(getAttrString("upHorizontalLabel"));

        switch (type) {
        case CHART_TEST:
            break;
        case CHART_GANTT_0:
            break;
        case AREA_CHARTS:
            break;
        case AREA_CHART_1:
            break;
        case STACKED_AREA_CHART_1:
            break;
        case STACKED_XY_AREA_CHART_1:
            break;
        case STACKED_XY_AREA_CHART_2:
            break;
        case STACKED_XY_AREA_RENDERER_1:
            break;
        case XY_AREA_CHART_1:
            break;
        case XY_AREA_CHART_2:
            break;
        case XY_AREA_RENDERER_2_1:
            break;
        case XY_STEP_AREA_RENDERER_1:
            break;
        case BAR_CHARTS:
            break;
        case CATEGORY_PLOT:
            break;
        case BAR_CHART_1:
            break;
        case BAR_CHART_2:
            break;
        case BAR_CHART_3:
            break;
        case BAR_CHART_4:
            break;
        case BAR_CHART_5:
            break;
        case BAR_CHART_6:
            break;
        case BAR_CHART_7:
            break;
        case BAR_CHART_8:
            break;
        case BAR_CHART_9:
            break;
        case BAR_CHART_10:
            break;
        case BAR_CHART_11:
            break;
        case BAR_CHART_3D_1:
            break;
        case BAR_CHART_3D_2:
            break;
        case BAR_CHART_3D_3:
            break;
        case BAR_CHART_3D_4:
            break;
        case CYLINDER_CHART_1:
            break;
        case CYLINDER_CHART_2:
            break;
        case INTERVAL_BAR_CHART_1:
            break;
        case LAYERED_BAR_CHART_1:
            break;
        case LAYERED_BAR_CHART_2:
            break;
        case SLIDING_CATEGORY_DATASET_1:
            break;
        case SLIDING_CATEGORY_DATASET_2:
            break;
        case STATISTICAL_BAR_CHART_1:
            break;
        case SURVEY_RESULTS_1:
            break;
        case SURVEY_RESULTS_2:
            break;
        case SURVEY_RESULTS_3:
            break;
        case WATERFALL_CHART_1:
            break;
        case XY_PLOT:
            break;
        case XY_BAR_CHART_1:
            break;
        case XY_BAR_CHART_2:
            break;
        case XY_BAR_CHART_3:
            break;
        case XY_BAR_CHART_4:
            break;
        case XY_BAR_CHART_5:
            break;
        case XY_BAR_CHART_6:
            break;
        case XY_BAR_CHART_7:
            break;
        case CLUSTERED_XY_BAR_RENDERER_1:
            break;
        case STACKED_XY_BAR_CHART_1:
            break;
        case STACKED_XY_BAR_CHART_2:
            break;
        case STACKED_XY_BAR_CHART_3:
            break;
        case XY_RELATIVE_DATE_FORMAT_1:
            break;
        case XY_RELATIVE_DATE_FORMAT_2:
            break;
        case BAR_CHARTS_STACKED:
            break;
        case POPULATION_CHART_1:
            break;
        case STACKED_BAR_CHART_1:
            break;
        case STACKED_BAR_CHART_2:
            break;
        case STACKED_BAR_CHART_3:
            break;
        case STACKED_BAR_CHART_4:
            break;
        case STACKED_BAR_CHART_5:
            break;
        case STACKED_BAR_CHART_6:
            break;
        case STACKED_BAR_CHART_7:
            break;
        case STACKED_BAR_CHART_3D_1:
            break;
        case STACKED_BAR_CHART_3D_2:
            break;
        case STACKED_BAR_CHART_3D_3:
            break;
        case STACKED_BAR_CHART_3D_4:
            break;
        case STACKED_BAR_CHART_3D_5:
            break;
        case COMBINED_AXIS_CHARTS:
            break;
        case COMBINED_CATEGORY_PLOT_1:
            break;
        case COMBINED_CATEGORY_PLOT_2:
            break;
        case COMBINED_TIME_SERIES_1:
            break;
        case COMBINED_XY_PLOT_1:
            break;
        case COMBINED_XY_PLOT_2:
            break;
        case COMBINED_XY_PLOT_3:
            break;
        case COMBINED_XY_PLOT_4:
            break;
        case FINANCIAL_CHARTS:
            break;
        case CANDLESTICK_CHART_1:
            break;
        case HIGHLOW_CHART_1:
            break;
        case HIGHLOW_CHART_2:
            break;
        case HIGHLOW_CHART_3:
            break;
        case MOVING_AVERAGE_1:
            break;
        case PRICE_VOLUME_1:
            break;
        case PRICE_VOLUME_2:
            break;
        case YIELD_CURVE_1:
            break;
        case GANTT_CHARTS:
            break;
        case GANTT_0:// Простая диаграмма Гантта
            chart.createGanttDataset(getAttrString("nameTaskSeries"), getAttrExpr("dataChart", isInit, mode),
                    getAttrString("taskPath601"), getAttrString("startPath601"), getAttrString("endPath601"));
            // инициализировать диаграмму
            chart.initGantt(type);
            break;
        case GANTT_1: // Диаграмма Гантта с несколькими сериями
            try {
                PropertyNode pn = pnChart.getChild("attr").getChild("countSeries601");
                pv = getPropertyValue(pn);
                if (pv.isNull()) {
                    setPropertyValue(new PropertyValue(pn.getDefaultValue(), pn));
                }

                pn = pnChart.getChild("attr").getChild("nameAttrSeries601");
                pv = getPropertyValue(pn);
                if (pv.isNull()) {
                    setPropertyValue(new PropertyValue(pn.getDefaultValue(), pn));
                }

                chart.createGantt1Dataset(getAttrExpr("dataChart", isInit, mode), getAttrString("taskPath601"),
                        getAttrString("startPath601"), getAttrString("endPath601"), getAttrString("nameAttrSeries601"),
                        getAttrInt("countSeries601"), getAttrString("taskSeries"));
            } catch (KrnException e) {
                e.printStackTrace();
            }
            // инициализировать диаграмму
            chart.initGantt(type);
            break;
        case GANTT_2:
            break;
        case SLIDING_GANTT_DATASET_1:

            PropertyNode pn = pnChart.getChild("attr").getChild("countSeries601");
            pv = getPropertyValue(pn);
            if (pv.isNull()) {
                setPropertyValue(new PropertyValue(pn.getDefaultValue(), pn));
            }

            pn = pnChart.getChild("attr").getChild("nameAttrSeries601");
            pv = getPropertyValue(pn);
            if (pv.isNull()) {
                setPropertyValue(new PropertyValue(pn.getDefaultValue(), pn));
            }

            try {
                chart.createSlidingGanttDataset(getAttrExpr("dataChart", isInit, mode), getAttrString("taskPath601"),
                        getAttrString("startPath601"), getAttrString("endPath601"), getAttrString("nameAttrSeries601"),
                        getAttrInt("countSeries601"), getAttrString("taskSeries"), getAttrInt("countVisibleTask"));
            } catch (KrnException e) {
                e.printStackTrace();
            }
            isSlider = true;
            chart.initGantt(type);
            break;
        case XY_TASK_DATASET_1:
            break;
        case XY_TASK_DATASET_2:
            break;
        case LINE_CHARTS:
            break;
        case ANNOTATION_1:
            break;
        case LINE_CHART_1:
            break;
        case LINE_CHART_2:
            break;
        case LINE_CHART_3:
            break;
        case LINE_CHART_4:
            break;
        case LINE_CHART_5:
            break;
        case LINE_CHART_6:
            break;
        case LINE_CHART_7:
            break;
        case LINE_CHART_8:
            break;
        case LINE_CHART_3D_1:
            break;
        case STATISTICAL_LINE_CHART_1:
            break;
        case XY_SPLINE_RENDERER_1:
            break;
        case XY_STEP_RENDERER_1:
            break;
        case XY_STEP_RENDERER_2:
            break;
        case DIAL_METER_CHARTS:
            break;
        case DIAL_1:
            break;
        case DIAL_2:
            break;
        case DIAL_2A:
            break;
        case DIAL_3:
            break;
        case DIAL_4:
            break;
        case DIAL_5:
            break;
        case METER_CHART_1:
            break;
        case METER_CHART_2:
            break;
        case METER_CHART_3:
            break;
        case THERMOMETER_1:
            break;
        case MULTIPLE_AXIS_CHARTS:
            break;
        case DUAL_AXIS_1:
            break;
        case DUAL_AXIS_2:
            break;
        case DUAL_AXIS_3:
            break;
        case DUAL_AXIS_4:
            break;
        case DUAL_AXIS_5:
            break;
        case MULTIPLE_AXIS_1:
            break;
        case MULTIPLE_AXIS_2:
            break;
        case MULTIPLE_AXIS_3:
            break;
        case PARETO_CHART_1:
            break;
        case OVERLAID_CHARTS:
            break;
        case OVERLAID_BAR_CHART_1:
            break;
        case OVERLAID_BAR_CHART_2:
            break;
        case OVERLAID_XY_PLOT_1:
            break;
        case OVERLAID_XY_PLOT_2:
            break;
        case PIE_CHARTS:
            break;
        case PIE_CHART_1:
            break;
        case PIE_CHART_2:
            break;
        case PIE_CHART_3:
            break;
        case PIE_CHART_4:
            break;
        case PIE_CHART_5:
            break;
        case PIE_CHART_6:
            break;
        case PIE_CHART_7:
            break;
        case PIE_CHART_8:
            break;
        case PIE_CHART_3D_1:
            break;
        case PIE_CHART_3D_2:
            break;
        case PIE_CHART_3D_3:
            break;
        case MULTIPLE_PIE_CHART_1:
            break;
        case MULTIPLE_PIE_CHART_2:
            break;
        case MULTIPLE_PIE_CHART_3:
            break;
        case MULTIPLE_PIE_CHART_4:
            break;
        case RING_CHART_1:
            break;
        case STATISTICAL_CHARTS:
            break;
        case BOX_AND_WHISKER_CHART_1:
            break;
        case BOX_AND_WHISKER_CHART_2:
            break;
        case HISTOGRAM_1:
            break;
        case HISTOGRAM_2:
            break;
        case MIN_MAX_CATEGORYPLOT_1:
            break;
        case NORMAL_DISTRIBUTION_1:
            break;
        case NORMAL_DISTRIBUTION_2:
            break;
        case REGRESSION_1:
            break;
        case SCATTER_PLOT_1:
            break;
        case SCATTER_PLOT_2:
            break;
        case SCATTER_PLOT_3:
            break;
        case SCATTER_PLOT_4:
            break;
        case XY_ERROR_RENDERER_1:
            break;
        case XY_ERROR_RENDERER_2:
            break;
        case TIME_SERIES_CHARTS:
            break;
        case TIME_RENDERER_1:
            break;
        case TIME_SERIES_2:
            break;
        case TIME_SERIES_3:
            break;
        case TIME_SERIES_4:
            break;
        case TIME_SERIES_5:
            break;
        case TIME_SERIES_6:
            break;
        case TIME_SERIES_7:
            break;
        case TIME_SERIES_8:
            break;
        case TIME_SERIES_9:
            break;
        case TIME_SERIES_10:
            break;
        case TIME_SERIES_11:
            break;
        case TIME_SERIES_12:
            break;
        case TIME_SERIES_13:
            break;
        case PERIOD_AXIS_1:
            break;
        case PERIOD_AXIS_2:
            break;
        case PERIOD_AXIS_3:
            break;
        case RELATIVE_DATE_FORMAT_1:
            break;
        case DEVIATION_RENDERER_1:
            break;
        case DEVIATION_RENDERER_2:
            break;
        case DIFFERENCE_CHART_1:
            break;
        case DIFFERENCE_CHART_2:
            break;
        case COMPARE_TO_PREVIOUS_YEAR:
            break;
        case XY_CHARTS:
            break;
        case XY_SCATTER_PLOT_1:
            break;
        case XY_SCATTER_PLOT_2:
            break;
        case XY_SCATTER_PLOT_3:
            break;
        case LOG_AXIS_1:
            break;
        case FUNCTION_2D_1:
            break;
        case XY_BLOCK_CHART_1:
            break;
        case XY_BLOCK_CHART_2:
            break;
        case XY_BLOCK_CHART_3:
            break;
        case XY_LINE_AND_SHAPE_RENDERER_1:
            break;
        case XY_LINE_AND_SHAPE_RENDERER_2:
            break;
        case XY_SERIES_1:
            break;
        case XY_SERIES_2:
            break;
        case XY_SERIES_3:
            break;
        case XY_SHAPE_RENDERER_1:
            break;
        case VECTOR_PLOT_1:
            break;

        default:
            break;
        }

        if (chart != null && chart.getChartPanel() != null) {
            // добавить на интерфейс диаграмму
            ChartPanel chartPane = chart.getChartPanel();
            if (mode == Mode.DESIGN) {
                // перенаправляю события мыши компонентов на this
                chartPane.addMouseListener(delegator);
                chartPane.addMouseMotionListener(delegator);
            }
            chartPanGlobal.removeAll();
            gridConst.anchor = GridBagConstraints.LINE_START;
            gridConst.fill = GridBagConstraints.BOTH;
            gridConst.insets = Constants.INSETS_0;
            gridConst.gridx = 0;
            gridConst.gridy = 0;
            gridConst.weightx = 0;
            gridConst.weighty = 0;
            chartPanGlobal.add(chartPane, gridConst);
            if (isSlider) {
                gridConst.anchor = GridBagConstraints.LINE_END;
                gridConst.fill = GridBagConstraints.VERTICAL;
                gridConst.insets = Constants.INSETS_0;
                gridConst.gridx = 1;
                gridConst.gridy = 0;
                gridConst.weightx = 0;
                gridConst.weighty = 0;
                chartPanGlobal.add(chart.getScroller(), gridConst);
            }
            chartPane.setMinimumSize(chartPanGlobal.getPreferredSize());
            chartPane.setPreferredSize(chartPanGlobal.getPreferredSize());
            chart.setAdditionalSize(chartPanGlobal.getPreferredSize());
            chartPane.setLayout(chartPanGlobal.getLayout());
            chartPanGlobal.updateUI();
        }
        // обновление свойств
        updateProperties();
    }

    /**
     * Обновление свойств компонента.
     */
    private void updateProperties() {
        // цвет панели
        PropertyNode pn = PROPS.getChild("view").getChild("background");
        PropertyValue pv = getPropertyValue(pn.getChild("backgroundColor"));
        setBackground((pv.isNull()) ? (Color) pn.getChild("backgroundColor").getDefaultValue() : pv.colorValue());
        // цвета графика
        pn = pnChart.getChild("colors");

        pv = getPropertyValue(pn.getChild("colorControlPane"));
        controlPanel.setGradient((pv.isNull()) ? (GradientColor) pn.getChild("colorControlPane").getDefaultValue()
                : (GradientColor) pv.objectValue());

        pv = getPropertyValue(pn.getChild("colorChartPane"));
        chartPanGlobal.setGradient((pv.isNull()) ? (GradientColor) pn.getChild("colorControlPane").getDefaultValue()
                : (GradientColor) pv.objectValue());

        if (chart != null) {
            pv = getPropertyValue(pn.getChild("colorChart"));
            chart.setColorChart((pv.isNull()) ? (Color) pn.getChild("colorChart").getDefaultValue() : pv.colorValue());

            pv = getPropertyValue(pn.getChild("colorCanvasChart"));
            chart.setGradientCanvasChart((pv.isNull()) ? (GradientColor) pn.getChild("colorCanvasChart").getDefaultValue()
                    : (GradientColor) pv.objectValue());

            // прозрачность
            pv = getPropertyValue(pnChart.getChild("transparencyChartBar"));
            chart.setTransparencyChartBar((Integer) ((pv.isNull()) ? (Color) pnChart.getChild("transparencyChartBar")
                    .getDefaultValue() : pv.intValue()));
        }
        pn = PROPS.getChild("view").getChild("border");
        if (pn != null) {
            pv = getPropertyValue(pn.getChild("borderType"));
            borderType = ((pv.isNull()) ? (Border) pn.getChild("borderType").getDefaultValue() : pv.borderValue());
            pv = getPropertyValue(pn.getChild("borderTitle"));
            if (!pv.isNull()) {
                borderTitleUID = (String) pv.resourceStringValue().first;
            }
        }

        pv = getPropertyValue(PROPS.getChild("description"));
        if (!pv.isNull()) {
            Pair p = pv.resourceStringValue();
            descriptionUID = (String) p.first;
            description = (byte[]) p.second;
        }

        pv = getPropertyValue(PROPS.getChild("varName"));
        if (!pv.isNull()) {
            varName = pv.stringValue();
        }
        Utils.processBorderProperties(this, frame);
    }

    /**
     * Update constraints.
     * 
     * @param c
     *            the c
     */
    public void updateConstraints(OrGuiComponent c) {
        invalidate();
        setConstraints(c);
        setPreferredSize(c);
        setMinimumSize(c);
        setMaximumSize(c);
        validate();
        repaint();
    }

    /**
     * Adds the property listener.
     * 
     * @param l
     *            the l
     */
    public void addPropertyListener(PropertyListener l) {
        listeners.add(PropertyListener.class, l);
    }

    /**
     * Removes the property listener.
     * 
     * @param l
     *            the l
     */
    public void removePropertyListener(PropertyListener l) {
        listeners.remove(PropertyListener.class, l);
    }

    /**
     * Fire property modified.
     */
    public void firePropertyModified() {
        EventListener[] list = listeners.getListeners(PropertyListener.class);
        for (EventListener lst : list) {
            ((PropertyListener) lst).propertyModified(this);
        }
    }

    /**
     * Метод Визуально выделяет компонент если кликнуть на нём, работает в режиме дизайнера.
     * 
     * @param g
     *            the g
     */
    public void paint(Graphics g) {
        super.paint(g);
        if (mode == Mode.DESIGN && isSelected) {
            kz.tamur.rt.Utils.drawRects(this, g);
        }
        if (isInit) {
            isInit = false;
            constructChart(false);
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

    @Override
    public void setSelected(boolean isSelected) {
        if (mode == Mode.DESIGN && isSelected) {
            for (OrGuiComponent listener : listListeners) {
                if (listener instanceof OrCollapsiblePanel) {
                    ((OrCollapsiblePanel) listener).expand();
                } else if (listener instanceof OrAccordion) {
                    ((OrAccordion) listener).expand();
                } else if (listener instanceof OrPopUpPanel) {
                    ((OrPopUpPanel) listener).showEditor(true);
                }
            }
        }
        this.isSelected = isSelected;
        repaint();
    }

    
    public void setPropertyValue(PropertyValue value) {
        PropertyHelper.setPropertyValue(value, xml, frame);
        Utils.processStdCompProperties(this, value);
        updateProperties();
        final String name = value.getProperty().getName();
        final String nameParent = value.getProperty().getParent().getName();

        if ("sizeChart".equals(nameParent)) {
            ChartPanel chartPane = chart.getChartPanel();
            GridBagConstraints contrains = PropertyHelper.getConstraints(PROPS.getChild("sizeChart"), xml);
            chartPane.invalidate();
            chart.setConstraints(contrains);
            setPreferredSize(chartPane);
            setMinimumSize(chartPane);
            setMaximumSize(chartPane);
            chartPane.validate();
            chartPane.repaint();
        }
        PropertyNode pn = pnChart.getChild("colors");
        PropertyValue pv;
        if ("title".equals(name)) {
            firePropertyModified();
        } else if (btnSave != null && "isBtnSaveJPEG".equals(name)) {
            pv = getPropertyValue(pnChart.getChild("isBtnSaveJPEG"));
            isBtnSaveJPEG = pv.booleanValue();
            btnSave.setVisible(isBtnSaveJPEG);
        }
    }

    
    public void setLangId(long langId) {
        Utils.processBorderProperties(this, frame);
        title = frame.getString(titleUID);
        if (mode == Mode.RUNTIME) {
            if (descriptionUID != null) {
                description = frame.getBytes(descriptionUID);
            }
            if (toolTipUid != null) {
                byte[] toolTip = frame.getBytes(toolTipUid);
                setToolTipText(toolTip == null ? null : new String(toolTip));
            } else {
                updateToolTip();
            }
            LangItem langItem = LangItem.getById(langId);

            String code = langItem.code;
            if ("RU".equals(code)) {
                resource = ResourceBundle.getBundle(Constants.NAME_RESOURCES, new Locale("ru"));
            } else if ("KZ".equals(code)) {
                resource = ResourceBundle.getBundle(Constants.NAME_RESOURCES, new Locale("kk"));
            }
        } else {
            PropertyValue pv = getPropertyValue(PROPS.getChild("description"));
            if (!pv.isNull()) {
                Pair p = pv.resourceStringValue();
                description = (byte[]) p.second;
            }
        }
        Component[] cs = getComponents();
        for (Component c : cs) {
            if (c instanceof OrGuiComponent) {
                ((OrGuiComponent) c).setLangId(langId);
            }
        }
    }

    
    public void setCopy(boolean copy) {
        isCopy = copy;
        setBorder(isCopy ? copyBorder : standartBorder);
        if (isCopy) {
            standartBorder = getBorder();
        }
    }

    
    public void setGuiParent(OrGuiContainer parent) {
        guiParent = parent;
    }

    
    public void setXml(Element xml) {
        this.xml = xml;
    }

    /**
     * Установить constraints.
     * 
     * @param comp
     *            the new constraints
     */
    public void setConstraints(OrGuiComponent comp) {
        gbl.setConstraints((Component) comp, comp.getConstraints());
        doLayout();
    }

    /**
     * Установить preferred size.
     * 
     * @param comp
     *            the new preferred size
     */
    public void setPreferredSize(OrGuiComponent comp) {
        Dimension sz = comp.getPrefSize();
        if (sz != null) {
            ((JComponent) comp).setPreferredSize(sz);
        }
    }

    /**
     * Установить minimum size.
     * 
     * @param comp
     *            the new minimum size
     */
    public void setMinimumSize(OrGuiComponent comp) {
        Dimension sz = comp.getMinSize();
        if (sz != null) {
            ((JComponent) comp).setMinimumSize(sz);
        }
    }

    /**
     * Установить maximum size.
     * 
     * @param comp
     *            the new maximum size
     */
    public void setMaximumSize(OrGuiComponent comp) {
        Dimension sz = comp.getMaxSize();
        if (sz != null) {
            ((JComponent) comp).setMaximumSize(sz);
        }
    }

    /**
     * Установить preferred size.
     * 
     * @param chart
     *            the new preferred size
     */
    public void setPreferredSize(ChartPanel chart) {
        Dimension sz = getPrefSize(chart);
        if (sz != null) {
            chart.setPreferredSize(sz);
        }
    }

    /**
     * Установить minimum size.
     * 
     * @param chart
     *            the new minimum size
     */
    public void setMinimumSize(ChartPanel chart) {
        Dimension sz = getMinSize(chart);
        if (sz != null) {
            chart.setMinimumSize(sz);
        }
    }

    /**
     * Установить maximum size.
     * 
     * @param chart
     *            the new maximum size
     */
    public void setMaximumSize(ChartPanel chart) {
        Dimension sz = getMaxSize(chart);
        if (sz != null) {
            chart.setMaximumSize(sz);
        }
    }

    
    public GridBagConstraints getConstraints() {
        return (mode == Mode.RUNTIME) ? constraints : PropertyHelper.getConstraints(PROPS, xml);
    }

    
    public Element getXml() {
        return xml;
    }

    /**
     * Получить node chart.
     * 
     * @return the node chart
     */
    public PropertyNode getNodeChart() {
        return pnChart;
    }

    
    public int getComponentStatus() {
        return CONTAINER_COMP;
    }

    
    public int getMode() {
        return mode;
    }

    
    public OrGuiContainer getGuiParent() {
        return guiParent;
    }

    
    public Dimension getPrefSize() {
        return (mode == Mode.RUNTIME) ? prefSize : PropertyHelper.getPreferredSize(this);
    }

    
    public Dimension getMaxSize() {
        return (mode == Mode.RUNTIME) ? maxSize : PropertyHelper.getMaximumSize(this);
    }

    
    public Dimension getMinSize() {
        return (mode == Mode.RUNTIME) ? minSize : PropertyHelper.getMinimumSize(this);
    }

    /**
     * Получить pref size.
     * 
     * @param chart
     *            the chart
     * @return the pref size
     */
    public Dimension getPrefSize(ChartPanel chart) {
        return (mode == Mode.RUNTIME) ? prefSize : getPreferredSize(chart);
    }

    /**
     * Получить max size.
     * 
     * @param chart
     *            the chart
     * @return the max size
     */
    public Dimension getMaxSize(ChartPanel chart) {
        return (mode == Mode.RUNTIME) ? maxSize : getMaximumSize(chart);
    }

    /**
     * Получить min size.
     * 
     * @param chart
     *            the chart
     * @return the min size
     */
    public Dimension getMinSize(ChartPanel chart) {
        return (mode == Mode.RUNTIME) ? minSize : getMinimumSize(chart);
    }

    /**
     * Получить preferred size.
     * 
     * @param c
     *            the c
     * @return the preferred size
     */
    public Dimension getPreferredSize(ChartPanel c) {
        return getSize(c, PROPS.getChild("sizeChart").getChild("pref"));
    }

    /**
     * Получить minimum size.
     * 
     * @param c
     *            the c
     * @return the minimum size
     */
    public Dimension getMinimumSize(ChartPanel c) {
        return getSize(c, PROPS.getChild("sizeChart").getChild("min"));
    }

    /**
     * Получить maximum size.
     * 
     * @param c
     *            the c
     * @return the maximum size
     */
    public Dimension getMaximumSize(ChartPanel c) {
        return getSize(c, PROPS.getChild("sizeChart").getChild("max"));
    }

    /**
     * Получить size.
     * 
     * @param c
     *            the c
     * @param ps
     *            the ps
     * @return the size
     */
    public Dimension getSize(ChartPanel c, PropertyNode ps) {
        final int width = PropertyHelper.getPropertyValue(ps.getChild("width"), xml, null).intValue();
        final int height = PropertyHelper.getPropertyValue(ps.getChild("height"), xml, null).intValue();
        return (width == 0 && height == 0) ? null : new Dimension(width, height);
    }

    
    public byte[] getDescription() {
        return description != null ? Arrays.copyOf(description, description.length) : null;
    }

    
    public ChartAdapter getAdapter() {
        return adapter;
    }

    
    public String getVarName() {
        return varName;
    }

    /**
     * Получить title.
     * 
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Получить component.
     * 
     * @param title
     *            the title
     * @return the component
     */
    public OrGuiComponent getComponent(String title) {
        if (title.equals(getVarName())) {
            return this;
        }
        final int count = getComponentCount();
        for (int i = 0; i < count; i++) {
            Component c = getComponent(i);
            if (c instanceof OrGuiContainer) {
                OrGuiComponent cc = ((OrGuiContainer) c).getComponent(title);
                if (cc != null) {
                    return cc;
                }
            } else if (c instanceof OrGuiComponent) {
                OrGuiComponent gc = (OrGuiComponent) c;
                if (title.equals(gc.getVarName())) {
                    return gc;
                }
            }
        }
        return null;
    }

    /**
     * Получение текущей ветки свойств объекта.
     * 
     * @return the properties
     */
    public PropertyNode getProperties() {
        return PROPS;
    }

    /**
     * Получение оригинальной и неизменённой ветки свойств объекта.
     * 
     * @return the original properties
     */
    public PropertyNode getOriginalProperties() {
        return originalProps;
    }

    
    public PropertyValue getPropertyValue(PropertyNode prop) {
        return PropertyHelper.getPropertyValue(prop, xml, frame);
    }

    /**
     * Получить attr expr.
     * 
     * @param nameAttr
     *            the name attr
     * @param first
     *            the first
     * @param mode
     *            the mode
     * @return the attr expr
     */
    public KrnObject[] getAttrExpr(String nameAttr, boolean first, int mode) {
        return (mode == Mode.RUNTIME) ? adapter.getData(nameAttr, first) : null;
    }

    /**
     * Получение записей фильтра по указанному атрибуту.
     * 
     * @param nameAttr
     *            the name attr
     * @return записи фильтра (массив) или null
     */
    public FilterRecord[] getAttrFilter(String nameAttr) {
        PropertyValue pv;
        try {
            pv = getPropertyValue(pnChart.getChild("attr").getChild(nameAttr));
        } catch (NullPointerException e) {
            return null;
        }

        if (!pv.isNull() && pv.objectValue() instanceof FilterRecord[]) {
            return (FilterRecord[]) pv.objectValue();
        } else if (!pv.isNull() && pv.objectValue() instanceof FilterRecord) {
            return new FilterRecord[] { (FilterRecord) pv.objectValue() };
        } else {
            return null;
        }
    }

    /**
     * Получение строкового значения указанного атрибута.
     * 
     * @param nameAttr
     *            имя атрибута
     * @return значение атрибута или ""
     */
    public String getAttrString(String nameAttr) {
        PropertyValue pv;
        try {
            pv = getPropertyValue(pnChart.getChild("attr").getChild(nameAttr));
        } catch (NullPointerException e) {
            return "";
        }
        return (!pv.isNull()) ? pv.stringValue() : "";
    }

    /**
     * Получение целочисленного значения по заданному атрибуту диаграммы.
     * 
     * @param nameAttr
     *            имя атрибута
     * @return значение атрибута или -1
     */
    public int getAttrInt(String nameAttr) {
        PropertyValue pv;
        try {
            pv = getPropertyValue(pnChart.getChild("attr").getChild(nameAttr));
        } catch (NullPointerException e) {
            return -1;
        }
        return (!pv.isNull()) ? pv.intValue() : -1;
    }

    
    public boolean isCopy() {
        return isCopy;
    }

    
    public boolean isEnabled() {
        return enabled;
    }

    void updateToolTip() {
        if (toolTipExpr != null && !toolTipExpr.isEmpty()) {
            String toolTipExprText_ = Utils.getExpReturn(toolTipExpr, frame, getAdapter());
            if (toolTipExprText_ != null && !toolTipExprText_.equals(toolTipExprText)) {
                if (toolTipExprText_.isEmpty()) {
                    toolTipExprText_ = null;
                }
                setToolTipText(toolTipExprText_);
                toolTipExprText = toolTipExprText_;
            }
        }
    }
    
    @Override
    public String getUUID() {
        return UUID;
    }

    @Override
    public void setComponentChange(OrGuiComponent comp) {
        listListeners.add(comp);
    }
    
    @Override
    public void setListListeners(java.util.List<OrGuiComponent> listListeners,  java.util.List<OrGuiComponent> listForDel) {
        for (OrGuiComponent orGuiComponent : listForDel) {
            this.listListeners.remove(orGuiComponent);
        }
        for (int i = 0; i < listListeners.size(); i++) {
            this.listListeners.add(i, listListeners.get(i));
        }
    }
    
    
    @Override
    public List<OrGuiComponent> getListListeners() {
        return listListeners;
    }
    
    @Override
    public String getToolTip() {
        return null;
    }

    @Override
    public void updateDynProp() {
    }

    @Override
    public int getPositionOnTopPan() {
        return -1;
    }

    @Override
    public boolean isShowOnTopPan() {
        return false;
    }

    @Override
    public void setAttention(boolean attention) {
    }
}
