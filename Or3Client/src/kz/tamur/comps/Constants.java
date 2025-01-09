package kz.tamur.comps;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import kz.tamur.comps.models.GradientColor;
import kz.tamur.util.Funcs;

/**
 * Created by IntelliJ IDEA. 
 * User: Vital 
 * Date: 30.04.2004 
 * Time: 10:24:12
 */
public final class Constants {

	public static final File DOCS_DIRECTORY = Funcs.getCanonicalFile(new File(Funcs.getSystemProperty("user.dir"), "doc"));
	public static final File TMP_DIRECTORY = Funcs.getCanonicalFile(Funcs.getSystemProperty("java.io.tmpdir"));

    public final static boolean removeDefaultColor = "true".equals(System.getProperty("removeDefaultColor"));
    
	public static final boolean IS_UL_PROJECT = Boolean.parseBoolean(System.getProperty("isUlProject", "false"))||Boolean.parseBoolean(System.getProperty("isEGKNProject", "false"))||Boolean.parseBoolean(System.getProperty("isRnDB", "false"));
	public static final boolean IS_FC_PROJECT = Boolean.parseBoolean(System.getProperty("isFcProject", "false"));

	public static final String COMPS_PACKAGE = "kz.tamur.comps.";
    public static final int COMPS_PACKAGE_L = COMPS_PACKAGE.length();
    public static final String WEB_COMPS_PACKAGE = "kz.tamur.web.component";
    public static final String SERVICE_FIG_PACKAGE = "kz.tamur.guidesigner.service.fig.";
    public static final String NAME_RESOURCES = "kz.tamur.rt.RuntimeResources";
    // Component's types values
    public static final int NONE_COMP = 0;
    public static final int STANDART_COMP = 1;
    public static final int CONTAINER_COMP = 2;
    public static final int USER_COMP = 3;
    public static final int TREES_COMP = 4;
    public static final int HYPER_COMP = 5;
    public static final int TABLE_COMP = 6;
    public static final int DECOR_COMP = 7;
    
    // Timer task constants
    public static final int TIMER_NOT_ACTIVE = 0;
    public static final int TIMER_ACTIVE = 1;
    public static final int TIMER_RUN_AT_START = 2;
    public static final int TIMER_RUN_DAY_START = 3;
    
    // Active user search param types
    public static final int BY_SRVNAME = 0;
    public static final int BY_DB = 1;
    public static final int BY_SESSION = 2;
    public static final int BY_CLIENTTYPE = 3;
    public static final int BY_LOGIN = 4;
    public static final int BY_IP = 5;
    public static final int BY_COMP = 6;
    public static final int BY_TIME = 7;

    // Position values
    public static final int CENTER_POSITION = 0;
    public static final int CENTER1_POSITION = 1;
    public static final int CENTER2_POSITION = 2;

    // Alignment values
    public static final int CENTER_ALIGNMENT = 0;
    public static final int LEFT_ALIGNMENT = 1;
    public static final int RIGHT_ALIGNMENT = 2;

    // Input values
    public static final int BINDING = 0; // красные
    public static final int OPTIONAL = 1; // серые
    public static final int CONSTRAINT = 2; // синие

    // Orientation
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    public static final int DIAGONAL = 2;
    public static final int DIAGONAL2 = 3;
    //VcsControl
    public static final int VCS_NOT_FIXD = 0;
    public static final int VCS_FIXD = 1;
    public static final int VCS_IMPORT = 2;
    public static final int VCS_EXPORT = 3;
//    public static final int VCS_NOT_REPLD = 2;
    //public static final int VCS_REPLD = 3;
    public static final int VCS_ALL = 4;
    public static final int VCS_COUNT = 5;

    // Scroll Policy
    public static final int SCROLL_BOTH = 0;
    public static final int SCROLL_HORIZONTAL = 1;
    public static final int SCROLL_VERTICAL = 2;
    public static final int SCROLL_AS_NEEDED = 3;

    // Tab Policy
    public static final int TAB_WRAP_LINE = 0;
    public static final int TAB_SCROLL = 1;

    // Tab Orientation
    public static final int TAB_TOP = 1;
    public static final int TAB_LEFT = 2;
    public static final int TAB_BOTTOM = 3;
    public static final int TAB_RIGHT = 4;

    // FileChooser filter type
    public static final int IMAGE_FILTER = 0;
    public static final int XML_FILTER = 1;
    public static final int STYLEDTEXT_FILTER = 2;
    public static final int MSDOC_FILTER = 3;
    public static final int HTML_FILTER = 4;
    public static final int JASPER_FILTER = 5;
    
    // Combobox appearance type
    public static final int VIEW_SIMPLE_COMBO = 0;
    public static final int VIEW_LIST = 1;
    public static final int VIEW_CHECKBOX_LIST = 2;
    public static final int VIEW_RADIOBOX_LIST = 3;
    public static final int VIEW_SIMPLE_AND_LIST = 4;
    public static final int VIEW_SOLID_LIST = 5;
    
    // ReportPrintor Editor type
    public static final int MSWORD_EDITOR = 0;
    public static final int MSEXCEL_EDITOR = 1;
    public static final int JASPER_EDITOR = 2;
    // Тип отображаемых данных
    public static final int FILES = 0;
    public static final int PEOPLES = 1;

    // Default column width values
    public static final int DEFAULT_PREF_WIDTH = 100;
    public static final int DEFAULT_MAX_WIDTH = 300;
    public static final int DEFAULT_MIN_WIDTH = 20;

    // Summary identifier values
    public static final int SUMMARY_NO = 0;
    public static final int SUMMARY_SUMM = 1;
    public static final int SUMMARY_COUNT = 2;
    public static final int SUMMARY_AVERAGE = 3;
    public static final int SUMMARY_MAX = 4;
    public static final int SUMMARY_MIN = 5;
    public static final int SUMMARY_TRUE_COUNT = 6;

    // Refresh mode values
    // Обновление содержимого только при первой загрузке интерфейса
    public static final int RM_ONCE = 0;
    // Обновление содержимого при каждой загрузке интерфейса
    public static final int RM_ALWAYS = 1;
    // Обновление содержимого при любых действиях пользователя на интерфейсе
    public static final int RM_DIRECTLY = 2;

    // Mode for activitys
    public static final int ACT_PERMIT = 0x0001;
    public static final int ACT_ALARM = 0x0002;
    public static final int ACT_ALERT = 0x0004;
    public static final int ACT_CANCEL = 0x0008;
    public static final int ACT_WINDOW = 0x0010;
    public static final int ACT_DIALOG = 0x0020;
    public static final int ACT_AUTO = 0x0040;
    public static final int ACT_IN_BOX = 0x0080;
    public static final int ACT_ARTICLE = 0x0100;
    public static final int ACT_ERR = 0x0200;
    public static final int ACT_OUT_BOX = 0x0400;
    public static final int ACT_SUB_PROC = 0x0800;
    public static final int IFC_NOT_ABL = 0x1000;
    public static final int ACT_DEBUG = 0x2000;
    public static final int ACT_FORK_JOIN = 0x4000;
    public static final int ACT_FASTREPORT = 0x8000;
    public static final int ACT_AUTO_NEXT = 0x10000;
    public static final int ACT_REPORT_REQUIRE = 0x20000;
    public static final String ACT_NO_UI = "Нет";
    public static final String ACT_WINDOW_STRING = "Полноэкранный";
    public static final String ACT_DIALOG_STRING = "Диалог";
    public static final String ACT_AUTO_STRING = "Выбор";
    public static final String ACT_ARTICLE_STRING = "Отчет";
    public static final String ACT_FASTREPORT_STRING = "Нестандартный отчет";
    public static final String SUBPROCESS_WAIT = "Подпроцесс(транз.суп.проц.)";
    public static final String SUBPROCESS_PASS = "Процесс";
    public static final String SUBPROCESS_PASS_WAIT = "Подпроцесс(собств.транз.)";
    public static final String SUPERPROCESS_CONTINUE = "Продолжать при откате";
    public static final String CHOPPER_NO = "Недоступен";
    public static final String CHOPPER_YES = "Доступен";
    public static final String NOT_RETURN_VAR = "Не возвращать перем.";

    // Mode for filters
    public static final int UNION_OR = 0;
    public static final int UNION_AND = 1;
    public static final int OPER_ZERO = 0;
    public static final int OPER_EQ = 1;
    public static final int OPER_NEQ = 2;
    public static final int OPER_GT = 3;
    public static final int OPER_LT = 4;
    public static final int OPER_GEQ = 5;
    public static final int OPER_LEQ = 6;
    public static final int OPER_EXIST = 7;
    public static final int OPER_NOT_EXIST = 8;
    public static final int OPER_INCLUDE = 9;
    public static final int OPER_EXCLUDE = 10;
    public static final int OPER_CONTAIN = 11;
    public static final int OPER_START_WITH = 12;
    public static final int OPER_ANOTHER = 13;
    public static final int OPER_ASCEND = 14;
    public static final int OPER_DESCEND = 15;
    public static final int OPER_FINISH_ON = 16;
    public static final int OPER_NOT_CONTAIN = 17;
    public static final int COMPARE_VALUE = 0;
    public static final int COMPARE_FUNC = 1;
    public static final int COMPARE_ATTR = 2;
    public static final int SQL_PAR = 3;
    public static final int TRANS_ZERO = 0;
    public static final int TRANS_ALL = 1;
    public static final int TRANS_CUR = 2;
    public static final int ESCAPE_NOT = 0;
    public static final int ESCAPE_MANUAL = 1;
    public static final int ESCAPE_DEFAULT = 2;

    public static final String OP_EQ = "=";
    public static final String OP_NEQ = "<>";
    public static final String OP_GT = ">";
    public static final String OP_LT = "<";
    public static final String OP_GEQ = ">=";
    public static final String OP_LEQ = "<=";
    public static final String OP_EXIST = "существует";
    public static final String OP_NOT_EXIST = "не существует";
    public static final String OP_INCLUDE = "включает";
    public static final String OP_EXCLUDE = "исключает";
    public static final String OP_CONTAIN = "содержит";
    public static final String OP_NOT_CONTAIN = "не содержит";
    public static final String OP_START_WITH = "начинается с";
    public static final String OP_FINISH_ON = "заканчивается на";
    public static final String OP_ANOTHER = "другой";
    public static final String OP_ASCEND = "по возрастанию";
    public static final String OP_DESCEND = "по убыванию";
    
    public static final int GROUP_ZERO = 0;
    public static final int GROUP_COUNT = 1;
    public static final int GROUP_SUM = 2;
    public static final int GROUP_MAX = 3;
    public static final int GROUP_MIN = 4;
    public static final int GROUP_AVG = 5;
    
    public static final String GR_COUNT = "COUNT";
    public static final String GR_SUM = "SUM";
    public static final String GR_MAX = "MAX";
    public static final String GR_MIN = "MIN";
    public static final String GR_AVG = "AVG";
    // Mode for boxes
    public static final String TRANSPORT_LOCAL = "Локальная папка";
    public static final String TRANSPORT_EMAIL = "Электронная почта";
    public static final String TRANSPORT_MQ_CLIENT = "MqClient";
    public static final String TRANSPORT_MQ_JMS = "MqJms";
    public static final String TRANSPORT_JBOSS_JMS = "JbossJms";
    public static final String TRANSPORT_WS = "Web Service";
    public static final String TRANSPORT_SGDS = "СГДС";
    public static final String TRANSPORT_DIIOP = "Lotus Notes DIIOP";
    // public static final String TRANSPORT_OPENMQ = "OpenMQ";
    public static final String MSG_XML = "XML-сообщение";
    public static final String MSG_FILE = "Двоичный файл";
    public static final int MSG_XML_INT = 0;
    public static final int MSG_FILE_INT = 1;
    // Cash sets
    public static final int CASH_SEPARATE = 0;
    public static final int CASH_GENERAL = 1;
    public static final int CASH_SEPARATE_NULL_TR = 2;

    // search parameters
    public static final int CURRENT_INTERFACE = 0;
    public static final int ALL_OPEN_INTERFACE = 1;
    public static final int CURRENT_INTERFACE_ALL_PROP = 2;
    public static final int SELECTED_PROP = 0;
    public static final int ALL_PROPS = 1;
    // search compare
    public static final int START_WHIH = 0;
    public static final int CONTAINS = 1;
    public static final int ALL = 2;

    // Error types
    public static final int NO_ERROR_TYPE = 0;
    public static final int REF_ERROR_TYPE = 1;
    public static final int EXPR_ERROR_TYPE = 2;

    // save/open types
    public static final int DOC_VIEW = 0;
    public static final int DOC_UPDATE = 1;
    public static final int DOC_EDIT = 2;
    public static final int DOC_PRINT = 3;
    public static final int DOC_UPDATE_VIEW = 4;

    // Вид таблицы
    public static final int TABLE_VIEW_COMMON = 0;
    public static final int TABLE_VIEW_ICON = 1;

    // yesMan Direction
    public static final int DIRECTION_RIGHT = 0;
    public static final int DIRECTION_DOWN = 1;
    public static final int DIRECTION_RIGHT_WITHOUT_ADD_ROW = 2;
    public static final int DIRECTION_DOWN_WITHOUT_ADD_ROW = 3;

    // table access property
    public static final int FULL_ACCESS = 0;
    public static final int READ_ONLY_ACCESS = 1;
    public static final int LAST_ROW_ACCESS = 2;
    public static final int BY_TRANSACTION_ACCESS = 3;

    // Таблица свойств компонента - формат задания фильтра, тип отображаемого интерфейса(только для 0 и 3)
    public static final int DIALOG = 0;
    public static final int MENU_MULTI = 1;
    public static final int MENU_SWITCH = 2;
    public static final int MENU_TREE_MULTI = 3;
    public static final int MENU_TREE_SWITCH = 4;
    public static final int FRAME = 3;

    // Errors
    public static final int REQ_ERR_MSG = 1;
    public static final int FLAGS_ENTER_DB = 2;
    public static final int C_REQ_GROUP_TYPE = 4;
    public static final int C_EXPR = 5;

    public static final boolean IS_DEBUG = true;
    public static final String EOL = Funcs.getValidatedSystemProperty("line.separator", "\n");
    public static final String REOL = Funcs.getValidatedSystemProperty("line.separator", "\r\n");
    // Error types
    public static final Integer NO_ERROR = new Integer(0);
    public static final Integer REQ_ERROR = new Integer(1);
    public static final Integer EXPR_ERROR = new Integer(2);

    // Hyperpopup action
    public static final int CHANGE_ACTION = 0;
    public static final int ADD_ACTION = 1;

    // DateField formats
    public static final int DD_MM_YYYY = 0;
    public static final int DD_MM_YYYY_HH_MM = 1;
    public static final int DD_MM_YYYY_HH_MM_SS = 2;
    public static final int DD_MM_YYYY_HH_MM_SS_SSS = 3;
    public static final int HH_MM_SS = 4;
    public static final int HH_MM = 5;
    public static final int DD_MM = 6;

    // Border Properties
    public static final Integer BORDER_STYLE = new Integer(0);
    public static final Integer BORDER_THICK = new Integer(1);
    public static final Integer BORDER_COLOR = new Integer(2);
    public static final Integer BORDER_TEXT_UID = new Integer(3);
    public static final Integer BORDER_POS = new Integer(4);
    public static final Integer BORDER_JUST = new Integer(5);
    public static final Integer BORDER_FONT = new Integer(6);
    public static final Integer BORDER_FONT_COLOR = new Integer(7);

    // Search properties
    public static final int SEARCH_ID = 3;
    public static final int SEARCH_UID = 4;
    // Sorting directions
    public static final int SORT_ASCENDING = 0;
    public static final int SORT_DESCENDING = 1;

    public static final int CAN_SORT = 0;
    public static final int CANNOT_SORT = 1;

    public static final boolean NEED_EXPAND_EXCEL_CELL = "1".equals(System.getProperty("needExpandExcelCell", "1"));

    // виды диаграмм для построения
    public static final int CHART_TEST = -1;
    public static final int CHART_GANTT_0 = 0;
    public static final int AREA_CHARTS = 1;
    public static final int AREA_CHART_1 = 101; // An area chart created with a CategoryDataset. This chart has a long subtitle, which automatically wraps when the chart is resized.
    public static final int STACKED_AREA_CHART_1 = 102; // A simple demo for the StackedAreaRenderer class.
    public static final int STACKED_XY_AREA_CHART_1 = 103; // A stacked area chart using data from a DefaultTableXYDataset.
    public static final int STACKED_XY_AREA_CHART_2 = 104; // A stacked area chart using data from a CategoryTableXYDataset.
    public static final int STACKED_XY_AREA_RENDERER_1 = 105; // A stacked area chart using the StackedXYAreaRenderer class (the usual default renderer is StackedXYAreaRenderer2).
    public static final int XY_AREA_CHART_1 = 106; // An area chart created using an XYDataset. There is a "test" annotation on this chart.
    public static final int XY_AREA_CHART_2 = 107; // An area chart created using an XYDataset (a TimeSeriesCollection in this case). TimeSeriesCollection
    public static final int XY_AREA_RENDERER_2_1 = 108; // An area chart created using an XYAreaRenderer2.
    public static final int XY_STEP_AREA_RENDERER_1 = 109; // A "step" chart where the area under the steps is filled.
    public static final int BAR_CHARTS = 2;
    public static final int CATEGORY_PLOT = 201;
    public static final int BAR_CHART_1 = 20101; // A simple bar chart.
    public static final int BAR_CHART_2 = 20102; // A bar chart with a horizontal orientation.
    public static final int BAR_CHART_3 = 20103; // A bar chart with item labels displayed. The item labels for positive data values are displayed inside the bars, while the item label for the negative data value is displayed underneath the bar (this is configurable).
    public static final int BAR_CHART_4 = 20104; // A demo that shows the use of the maximum bar width setting to cap the width of bars.
    public static final int BAR_CHART_5 = 20105; // Another horizontal bar chart.
    public static final int BAR_CHART_6 = 20106; // A bar chart with most features (axes, labels, etc.) hidden.
    public static final int BAR_CHART_7 = 20107; // A bar chart with the item label position customised. A range marker has been added to the chart to indicate a target range for the data values.
    public static final int BAR_CHART_8 = 20108; // A bar chart with items labels displayed for just one series.
    public static final int BAR_CHART_9 = 20109; // A bar chart customised to use GradientPaint for the bar colors and the chart background. Yes, it's ugly, but some people like that kind of thing.
    public static final int BAR_CHART_10 = 201010; // A simple bar chart with animation.
    public static final int BAR_CHART_11 = 201011; // A simple bar chart with a horizontal orientation.
    public static final int BAR_CHART_3D_1 = 201012; // A bar chart with a renderer that draws bars with a "3D" effect.
    public static final int BAR_CHART_3D_2 = 201013; // A "3D" bar chart with a horizontal orientation.
    public static final int BAR_CHART_3D_3 = 201014; // A "3D" bar chart with a maximum bar width specified.
    public static final int BAR_CHART_3D_4 = 201015; // A "3D" bar chart with customised bar colors (red bars show students that failed to reach the passing grade).
    public static final int CYLINDER_CHART_1 = 201016; // A bar chart with a custom renderer that draws cylinders for the bars.
    public static final int CYLINDER_CHART_2 = 201017; // A bar chart with a custom renderer that draws cylinders for the bars (in this case, with a horizontal orientation).
    public static final int INTERVAL_BAR_CHART_1 = 201018; // A bar chart that displays intervals. This uses the DefaultIntervalCategoryDataset and IntervalBarRenderer classes.
    public static final int LAYERED_BAR_CHART_1 = 201019; // A layered bar chart.
    public static final int LAYERED_BAR_CHART_2 = 201020; // A layered bar chart (horizontal orientation).
    public static final int SLIDING_CATEGORY_DATASET_1 = 201021; // A simple demo for the SlidingCategoryDataset class.
    public static final int SLIDING_CATEGORY_DATASET_2 = 201022; // A simple demo for the SlidingCategoryDataset class.
    public static final int STATISTICAL_BAR_CHART_1 = 201023; // A bar chart that uses a StatisticalCategoryDataset to overlay a standard deviation indicator over each of the bars.
    public static final int SURVEY_RESULTS_1 = 201024; // A bar chart with special formatting to display survey results.
    public static final int SURVEY_RESULTS_2 = 201025; // A bar chart with sublabels for the categories.
    public static final int SURVEY_RESULTS_3 = 201026; // A bar chart with item labels.
    public static final int WATERFALL_CHART_1 = 201027; // A waterfall chart.
    public static final int XY_PLOT = 202;
    public static final int XY_BAR_CHART_1 = 20201; // A bar chart created using data from a TimeSeriesCollection. The chart uses a subtitle to cite the data source.
    public static final int XY_BAR_CHART_2 = 20202; // A bar chart created using the ClusteredXYBarRenderer class.
    public static final int XY_BAR_CHART_3 = 20203; // A demo that shows varying bar widths on an XYPlot. The underlying dataset is an IntervalXYDataset which places no limitation on the interval for either the x-values or the y-values.
    public static final int XY_BAR_CHART_4 = 20204; // In this demo, the domain axis shows only integer values.
    public static final int XY_BAR_CHART_5 = 20205; // Another demo for the XYBarRenderer class.
    public static final int XY_BAR_CHART_6 = 20206; // In this demo, the XYBarRenderer is configured to use the y-interval from the dataset (rather than using zero for the base of the bars).
    public static final int XY_BAR_CHART_7 = 20207; // In this demo, the XYBarRenderer displays some date intervals along the x-axis.
    public static final int CLUSTERED_XY_BAR_RENDERER_1 = 20208; // A simple demo for the ClusteredXYBarRenderer class.
    public static final int STACKED_XY_BAR_CHART_1 = 20209; // A stacked bar chart using data from a DefaultTableXYDataset.
    public static final int STACKED_XY_BAR_CHART_2 = 202010; // A stacked bar chart using data from a TimeTableXYDataset.
    public static final int STACKED_XY_BAR_CHART_3 = 202011; // A stacked bar chart using data from a TimeTableXYDataset, and displaying the values as percentages.
    public static final int XY_RELATIVE_DATE_FORMAT_1 = 202012; // A time series chart where the time axis displays a relative date (that is, the elapsed time in hours, minutes and seconds).
    public static final int XY_RELATIVE_DATE_FORMAT_2 = 202013; // A time series bar chart where the range axis displays the elapsed time in hours, minutes and seconds. This uses the RelativeDateFormat class.\
    public static final int BAR_CHARTS_STACKED = 3;
    public static final int POPULATION_CHART_1 = 301; // A population pyramid chart. This is constructed using a stacked bar chart...but it would be better to create a dedicated plot type. That's on the TODO list (since forever).
    public static final int STACKED_BAR_CHART_1 = 302; // A regular stacked bar chart.
    public static final int STACKED_BAR_CHART_2 = 303; // A stacked bar chart with a horizontal orientation. A completely transparent color is used for the middle series, so that it leaves a gap in the chart.
    public static final int STACKED_BAR_CHART_3 = 304; // A stacked bar chart that uses a custom renderer to show the totals at the top of the stack for each category.
    public static final int STACKED_BAR_CHART_4 = 305; // A grouped stacked bar chart.
    public static final int STACKED_BAR_CHART_5 = 306; // A grouped stacked bar chart with item labels.
    public static final int STACKED_BAR_CHART_6 = 307; // A stacked bar chart that uses a DateAxis for the range axis.
    public static final int STACKED_BAR_CHART_7 = 308; // A stacked bar chart that displays values as percentages.
    public static final int STACKED_BAR_CHART_3D_1 = 309; // A stacked bar chart with 3D effect.
    public static final int STACKED_BAR_CHART_3D_2 = 3010; // A stacked bar chart with 3D effect.
    public static final int STACKED_BAR_CHART_3D_3 = 3011; // A stacked bar chart with 3D effect.
    public static final int STACKED_BAR_CHART_3D_4 = 3012; // A stacked bar chart with 3D effect.
    public static final int STACKED_BAR_CHART_3D_5 = 3013; // A panel containing four stacked bar charts with 3D effect, each with a different orientation.
    public static final int COMBINED_AXIS_CHARTS = 4;
    public static final int COMBINED_CATEGORY_PLOT_1 = 401; // A demo showing two category plots that share a single domain axis.
    public static final int COMBINED_CATEGORY_PLOT_2 = 402; // A demo showing two category plots that share a common range axis.
    public static final int COMBINED_TIME_SERIES_1 = 403; // A demo showing two XYPlot instances that share a common range axis.
    public static final int COMBINED_XY_PLOT_1 = 404; // A demo showing two plots sharing a common domain axis.
    public static final int COMBINED_XY_PLOT_2 = 405; // A demo showing two plots that share a common range axis.
    public static final int COMBINED_XY_PLOT_3 = 406; // A demo showing three plots that share a common range axis.
    public static final int COMBINED_XY_PLOT_4 = 407; // A demo showing two plots that share a common domain axis. Notice also that the top plot has two range axes.
    public static final int FINANCIAL_CHARTS = 5;
    public static final int CANDLESTICK_CHART_1 = 501; // A candlestick chart, often used to display financial data.
    public static final int HIGHLOW_CHART_1 = 502; // A chart that displays the opening price, the trading range (high-low) and closing price for a commodity during each trading session.
    public static final int HIGHLOW_CHART_2 = 503; // An open-high-low-close (OHLC) chart with a moving average series overlaid on top.
    public static final int HIGHLOW_CHART_3 = 504; // A candlestick chart and an open-high-low-close chart overlaid on one plot.
    public static final int MOVING_AVERAGE_1 = 505; // A chart with a moving average line (common in stock charts).
    public static final int PRICE_VOLUME_1 = 506; // A chart showing the closing price and trading volume for a futures contract.
    public static final int PRICE_VOLUME_2 = 507; // A chart showing prices (using a HighLowRenderer) and trading volumes by day.
    public static final int YIELD_CURVE_1 = 508; // This time series chart uses a custom date axis with irregular tick labels to display a yield curve.
    public static final int GANTT_CHARTS = 6;
    /**
     * Простейшая диаграмма Гантта показывающая актуальное состояние выполения задачи в разрезе всего процесса.
     * Диаграмма конструируется по <i>одной</i> серии задач
     */
    public static final int GANTT_0 = 600; 
    /**
     * Простая диаграмма Гантта показывающая актуальное состояние выполения задачи в разрезе всего процесса
     * Диаграмма конструируется по по <i>нескольким</i> сериям задач
     * 
     * A simple Gantt chart showing actual vs. scheduled progress.
     */
    public static final int GANTT_1 = 601; 
    public static final int GANTT_2 = 602; // A simple Gantt chart showing progress-to-date on a set of tasks.
    public static final int SLIDING_GANTT_DATASET_1 = 603; // A simple demo for the SlidingGanttCategoryDataset class. Try moving the scrollbar at the right side of the chart.
    public static final int XY_TASK_DATASET_1 = 604; // A demo for the XYTaskDataset class.
    public static final int XY_TASK_DATASET_2 = 605; // A demo for the XYTaskDataset class. Left/right panning has been enabled for this chart (via CTRL-mouse-drag, or ALT-mouse-drag on the Mac).
    public static final int LINE_CHARTS = 7;
    public static final int ANNOTATION_1 = 701; // A line chart with annotations used to label each series. The axis ranges do not automatically adjust to accommodate the annotations, so the upper margin on the domain axis has been increased manually.
    public static final int LINE_CHART_1 = 702; // A line chart demo. The default Stroke values have been changed for each series.
    public static final int LINE_CHART_2 = 703; // A line chart created using an XYDataset.
    public static final int LINE_CHART_3 = 704; // A line chart with many series. A shapes is displayed at each data point to help to differentiate the series.
    public static final int LINE_CHART_4 = 705; // A plot showing mathematical functions (these are plotted with a custom dataset that samples the function values).
    public static final int LINE_CHART_5 = 706; // A line chart with item labels showing. The orientation of the plot has been changed to horizontal.
    public static final int LINE_CHART_6 = 707; // A line chart where each series is displayed with a different combination of lines and/or shapes. This plot uses the XYLineAndShapeRenderer.
    public static final int LINE_CHART_7 = 708; // A line chart where each series is displayed with a different combination of lines and/or shapes. This plot is a CategoryPlot using a LineAndShapeRenderer.
    public static final int LINE_CHART_8 = 709; // A line chart where the range axis is a SymbolAxis.
    public static final int LINE_CHART_3D_1 = 7010; // A line chart with a 3D effect. This chart is based on a CategoryDataset.
    public static final int STATISTICAL_LINE_CHART_1 = 7011; // A line chart that uses a StatisticalCategoryDataset to overlay a standard deviation indicator for each data value.
    public static final int XY_SPLINE_RENDERER_1 = 7012; // A demo that shows a line chart drawn using XYSplineRenderer.
    public static final int XY_STEP_RENDERER_1 = 7013; // A "step" chart using data from an XYDataset. Points in a series are connected by a line that first extends horizontally from one x-value to the next, then vertically from the old y-value to the new y-value.
    public static final int XY_STEP_RENDERER_2 = 7014; // A "step" chart using data from an XYDataset. This demo is similar to XYStepRendererDemo1 but adds item labels to the chart.
    public static final int DIAL_METER_CHARTS = 8;
    public static final int DIAL_1 = 801; // A dial chart that displays a single value.
    public static final int DIAL_2 = 802; // A dial chart that displays two values, one per scale.
    public static final int DIAL_2A = 803; // Two dials side-by-side.
    public static final int DIAL_3 = 804; // A dial chart with a non-circular frame.
    public static final int DIAL_4 = 805; // A dial chart with a non-circular frame.
    public static final int DIAL_5 = 806; // A dial chart that displays a clock face.
    public static final int METER_CHART_1 = 807; // A simple demo showing a chart created using the MeterPlot class.
    public static final int METER_CHART_2 = 808; // A MeterPlot with interactive updates.
    public static final int METER_CHART_3 = 809; // A demo showing three meter plots each with a different DialShape.
    public static final int THERMOMETER_1 = 8010; // A simple demo of the ThermometerPlot class.
    public static final int MULTIPLE_AXIS_CHARTS = 9;
    public static final int DUAL_AXIS_1 = 901; // A CategoryPlot with multiple datasets, axes and renderers.
    public static final int DUAL_AXIS_2 = 902; // An XYPlot with two range axes. Notice also that two legends are displayed, one on the left and one on the right.
    public static final int DUAL_AXIS_3 = 903; // A dual axis chart with a "horizontal" orientation.
    public static final int DUAL_AXIS_4 = 904; // A CategoryPlot with two range axes.
    public static final int DUAL_AXIS_5 = 905; // A bar chart with two range axes.
    public static final int MULTIPLE_AXIS_1 = 906; // This demo shows the use of multiple axes with time series data.
    public static final int MULTIPLE_AXIS_2 = 907; // This demo shows the use of multiple axes with time series data.
    public static final int MULTIPLE_AXIS_3 = 908; // This demo shows the use of multiple axes with time series data.
    public static final int PARETO_CHART_1 = 909; // A Pareto chart.
    public static final int OVERLAID_CHARTS = 10;
    public static final int OVERLAID_BAR_CHART_1 = 1001; // A bar chart with an overlaid line chart.
    public static final int OVERLAID_BAR_CHART_2 = 1002; // A bar chart with an overlaid series plotted using the LevelRenderer class.
    public static final int OVERLAID_XY_PLOT_1 = 1003; // A time series bar chart with an overlaid line chart.
    public static final int OVERLAID_XY_PLOT_2 = 1004; // A time series bar chart with overlaid lines against two axes.
    public static final int PIE_CHARTS = 11;
    public static final int PIE_CHART_1 = 1101; // This demo shows a simple pie chart created from a DefaultPieDataset.
    public static final int PIE_CHART_2 = 1102; // This demo shows a pie chart with an "exploded" section.
    public static final int PIE_CHART_3 = 1103; // A pie chart with no data - in this case, a "no data" message is displayed.
    public static final int PIE_CHART_4 = 1104; // A pie chart with buttons allowing you to specify the order of the items in the dataset.
    public static final int PIE_CHART_5 = 1105; // The two pie charts on the left are constrained to a circular shape, while the two pie charts on the right are elliptical (you may need to resize the panel to see the effect).
    public static final int PIE_CHART_6 = 1106; // These charts illustrate the flags for handling zero and null values in pie charts.
    public static final int PIE_CHART_7 = 1107; // A rotating pie chart with a lot of section labels. This chart was created to test the label distribution code.
    public static final int PIE_CHART_8 = 1108; // A pie chart using a custom section label generator.
    public static final int PIE_CHART_3D_1 = 1109; // A pie chart with a "3D" effect.
    public static final int PIE_CHART_3D_2 = 11010; // An animated pie chart with a "3D" effect.
    public static final int PIE_CHART_3D_3 = 11011; // A pie chart with a "3D" effect. This example also has a custom label generator that omits the label for one section.
    public static final int MULTIPLE_PIE_CHART_1 = 11012; // A chart containing multiple pie plots, generated using a CategoryDataset.
    public static final int MULTIPLE_PIE_CHART_2 = 11013; // A chart containing multiple pie plots, generated using a CategoryDataset.
    public static final int MULTIPLE_PIE_CHART_3 = 11014; // A chart containing multiple pie plots, generated using a CategoryDataset.
    public static final int MULTIPLE_PIE_CHART_4 = 11015; // A chart containing multiple pie plots, generated using a CategoryDataset.
    public static final int RING_CHART_1 = 11016; // This demo shows a simple ring chart created from a DefaultPieDataset.
    public static final int STATISTICAL_CHARTS = 12;
    public static final int BOX_AND_WHISKER_CHART_1 = 1201; // A box-and-whisker chart.
    public static final int BOX_AND_WHISKER_CHART_2 = 1202; // A box-and-whisker chart (based on an XYPlot).
    public static final int HISTOGRAM_1 = 1203; // A simple histogram illustrating the use of the HistogramDataset class. Mouse-wheel zooming has been enabled for this chart, as well as panning (via CTRL-mouse-drag).
    public static final int HISTOGRAM_2 = 1204; // A simple histogram illustrating the use of the SimpleHistogramDataset class.
    public static final int MIN_MAX_CATEGORYPLOT_1 = 1205; // A category plot that highlights minimum and maximum values.
    public static final int NORMAL_DISTRIBUTION_1 = 1206; // A line chart displaying a standard normal distribution. The function is represented using the NormalDistributionFunction2D class, and sampled using DatasetUtilities.sampleFunction2D().
    public static final int NORMAL_DISTRIBUTION_2 = 1207; // A line chart displaying several normal distribution functions. The functions are represented using the NormalDistributionFunction2D class, and sampled using DatasetUtilities.sampleFunction2D(). Mouse-wheel zooming has been enabled for this chart.
    public static final int REGRESSION_1 = 1208; // This demo contains two charts with fitted regression lines.
    public static final int SCATTER_PLOT_1 = 1209; // A scatter plot displays data from an XYDataset.
    public static final int SCATTER_PLOT_2 = 12010; // A scatter plot displays data from an XYDataset. In this example, the renderer is changed to an XYDotRenderer.
    public static final int SCATTER_PLOT_3 = 12011; // A scatter plot displays data from an XYDataset. In this example, a mouse listener is used to write the axis coordinates to standard output whenever the mouse is clicked.
    public static final int SCATTER_PLOT_4 = 12012; // A scatter plot displays data from an XYDataset. In this example, the renderer is an XYDotRenderer.
    public static final int XY_ERROR_RENDERER_1 = 12013; // A simple demo that uses the XYErrorRenderer class.
    public static final int XY_ERROR_RENDERER_2 = 12014; // A simple demo that uses the XYErrorRenderer class.
    public static final int TIME_SERIES_CHARTS = 13;
    public static final int TIME_RENDERER_1 = 1301; // A time series chart displaying two series.
    public static final int TIME_SERIES_2 = 1302; // A time series chart showing one series with a discontinuity (a null value in the series causes a break in the line).
    public static final int TIME_SERIES_3 = 1303; // A time series chart showing an option to draw unfilled shapes for one series.
    public static final int TIME_SERIES_4 = 1304; // This demo shows a background image for the plot, as well as the use of (random) Unicode characters within the chart title. Any Unicode characters can be used, provided that the selected font supports them.
    public static final int TIME_SERIES_5 = 1305; // This chart displays 10 years worth of daily (random) data, to give an indicator of performance. To boost the performance, we replace the regular renderer with an instance of SamplingXYLineRenderer.
    public static final int TIME_SERIES_6 = 1306; // A chart showing constant data (originally used to track down a bug in the axis code).
    public static final int TIME_SERIES_7 = 1307; // Yet another time series demo.
    public static final int TIME_SERIES_8 = 1308; // A time series chart with a 30-day moving average.
    public static final int TIME_SERIES_9 = 1309; // A time series chart where the points on each series are marked with different shapes.
    public static final int TIME_SERIES_10 = 13010; // In this demo, the time series contains data that is recorded against one minute time periods.
    public static final int TIME_SERIES_11 = 13011; // Four charts showing different views of the same dataset.
    public static final int TIME_SERIES_12 = 13012; // This demo shows a series ("Series 2") with all zero values - it is only visible because the Stroke has been set to a width of 2.0. This highlights a limitation in the JFreeChart rendering process that will hopefully be fixed in a future release.
    public static final int TIME_SERIES_13 = 13013; // A time series containing data recorded against one week time periods.
    public static final int PERIOD_AXIS_1 = 13014; // A time series chart displaying two series. The domain axis is an instance of PeriodAxis - notice how there are two bands of axis labels.
    public static final int PERIOD_AXIS_2 = 13015; // A time series chart where the domain axis is an instance of PeriodAxis - notice how there are three bands of axis labels.
    public static final int PERIOD_AXIS_3 = 13016; // A time series chart where the domain axis is an instance of PeriodAxis.
    public static final int RELATIVE_DATE_FORMAT_1 = 13017; // A time series chart where the time axis displays a relative date (that is, the elapsed time in hours, minutes and seconds).
    public static final int DEVIATION_RENDERER_1 = 13018; // A line chart where the y-values are displayed with a "range of uncertainty". This plot uses the DeviationRenderer first introduced in JFreeChart version 1.0.5.
    public static final int DEVIATION_RENDERER_2 = 13019; // A time series chart where the y-values are displayed with a "range of uncertainty". This plot uses the DeviationRenderer first introduced in JFreeChart version 1.0.5.
    public static final int DIFFERENCE_CHART_1 = 13020; // A difference chart highlights the difference between two series. In this demo, the data is randomly generated.
    public static final int DIFFERENCE_CHART_2 = 13021; // A difference chart highlights the difference between two series. This demo also shows the use of a Marker to highlight a range of values on the domain axis.
    public static final int COMPARE_TO_PREVIOUS_YEAR = 13022; // This time series chart compares figures for 2007 with the corresponding figures for the previous year. The chart used a hidden secondary axis.
    public static final int XY_CHARTS = 14;
    public static final int XY_SCATTER_PLOT_1 = 1401; // A scatter plot displays data from an XYDataset.
    public static final int XY_SCATTER_PLOT_2 = 1402; // A scatter plot displays data from an XYDataset. In this example, the renderer is changed to an XYDotRenderer.
    public static final int XY_SCATTER_PLOT_3 = 1403; // A scatter plot displays data from an XYDataset. In this example, a mouse listener is used to write the axis coordinates to standard output whenever the mouse is clicked.
    public static final int LOG_AXIS_1 = 1404; // A simple demo of the LogAxis class.
    public static final int FUNCTION_2D_1 = 1405; // This demo illustrates how to plot a mathematical function y = f(x) with JFreeChart. The function is sampled to create a dataset which is then plotted. This isn't ideal - it would be preferable to have a renderer that can plot mathematical functions directly.
    public static final int XY_BLOCK_CHART_1 = 1406; // A simple demo of the XYBlockRenderer class.
    public static final int XY_BLOCK_CHART_2 = 1407; // A simple demo of the XYBlockRenderer class.
    public static final int XY_BLOCK_CHART_3 = 1408; // A simple demo of the XYBlockRenderer class.
    public static final int XY_LINE_AND_SHAPE_RENDERER_1 = 1409; // This demo shows how the XYLineAndShapeRenderer can display one series with shapes and another with lines.
    public static final int XY_LINE_AND_SHAPE_RENDERER_2 = 14010; // This demo illustrates the use of the useOutlinePaint and useFillPaint flags with the XYLineAndShapeRenderer.
    public static final int XY_SERIES_1 = 14011; // A simple demo using an XYSeries to represent the data. Notice how a null value causes a break in the line that is drawn to represent the data.
    public static final int XY_SERIES_2 = 14012; // A demo where all the y-values are the same (originally created to track down a bug).
    public static final int XY_SERIES_3 = 14013; // A demo that shows a bar chart created from an XYDataset. Notice also that a range marker has been added to the chart to highlight a target range.
    public static final int XY_SHAPE_RENDERER_1 = 14014; // A simple demo for the XYShapeRenderer class.
    public static final int VECTOR_PLOT_1 = 14015; // A demo for the VectorRenderer class.
    // список возможных разрешений
    public static final int R1920_1200 = 12; // 1920x1200
    public static final int R1920_1080 = 11; // 1920x1080
    public static final int R1680_1050 = 10; // 1680x1050
    public static final int R1600_900 = 9; // 1600x900
    public static final int R1440_900 = 8; // 1440x900
    public static final int R1366_768 = 7; // 1366x768
    public static final int R1360_768 = 6; // 1360x768
    public static final int R1280_1024 = 5; // 1280x1024
    public static final int R1280_800 = 4; // 1280x800
    public static final int R1280_768 = 3; // 1280x768
    public static final int R1024_768 = 2; // 1024x768
    public static final int R800_600 = 1; // 800x600
    public static final int R640_480 = 0; // 640x480
    // поворот текста в метках
    public final static int ROTATE_RIGHT = 1;
    public final static int DONT_ROTATE = 0;
    public final static int ROTATE_LEFT = -1;
    // источник строки (используется для вытаскивания заголовка)
    public final static int SOURSE_SIMPLE = 0;
    public final static int SOURSE_EXPRESSION = 1;
    public final static int SOURSE_HTML = 2;
    // состояния чекбокса с тремя состояниями
    public static final int NOT_SELECTED = 0;
    public static final int SELECTED =  1;
    public static final int DONT_CARE = 2;
    public static final GradientColor GLOBAL_DEF_GRADIENT = new GradientColor("-4209202, -1775379, 3, 1, 0, 27, 1");
    public static final String ERROR_MESSAGE_1 = "Ошибка при сохранении данных.\nВведенные данные не будут сохранены.\nОбратитесь к разработчику!\n";
    public static final Cursor WAIT_CURSOR = new Cursor(Cursor.WAIT_CURSOR);
    public static final Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);
    public static final Cursor HAND_CURSOR = new Cursor(Cursor.HAND_CURSOR);

    public static final String NAME_CLASS_CONTROL_FOLDER = "ControlFolder";
    public static final String NAME_CLASS_CONTROL_FOLDER_ROOT = "ControlFolderRoot";
    public static final String NAME_CLASS_CONFIG_GLOBAL = "ConfigGlobal";
    public static final String NAME_CLASS_CONFIG_GLOBAL_FIX = "ConfigGlobalFix";
    public static final String NAME_CLASS_CONFIG_LOCAL = "ConfigLocal";
    public static final String NAME_CLASS_CONFIG_OBJECT = "ConfigObject";
    public static final String NAME_CLASS_PROPERTY = "Property";
    public static final String ATTR_GRADIENT_MAIN_FRAME = "gradientMainFrame";
    public static final String ATTR_GRADIENT_CONTROL_PANEL = "gradientControlPanel";
    public static final String ATTR_GRADIENT_MENU_PANEL = "gradientMenuPanel";
    public static final String ATTR_TRANSPARENT_MAIN = "transparentMain";
    public static final String ATTR_TRANSPARENT_DIALOG = "transparentDialog";
    public static final String ATTR_COLOR_MAIN = "colorMain";
    public static final String ATTR_TRANSPARENT_CELL_TABLE = "transparentCellTable";
    public static final String ATTR_COLOR_HEADER_TABLE = "colorHeaderTable";
    public static final String ATTR_COLOR_TAB_TITLE = "colorTabTitle";
    public static final String ATTR_COLOR_BACK_TAB_TITLE = "colorBackTabTitle";
    public static final String ATTR_COLOR_FONT_TAB_TITLE = "colorFontTabTitle";
    public static final String ATTR_COLOR_FONT_BACK_TAB_TITLE = "colorFontBackTabTitle";
    public static final String ATTR_TRANSPARENT_BACK_TAB_TITLE = "transparentBackTabTitle";
    public static final String ATTR_TRANSPARENT_SELECTED_TAB_TITLE = "transparentSelectedTabTitle";
    public static final String ATTR_GRADIENT_FIELD_NO_FLC = "gradientFieldNOFLC";

    public static final String ATTR_BLUE_SYS_COLOR = "blueSysColor";
    public static final String ATTR_DARK_SHADOW_SYS_COLOR = "darkShadowSysColor";
    public static final String ATTR_MID_SYS_COLOR = "midSysColor";
    public static final String ATTR_LIGHT_YELLOW_COLOR = "lightYellowColor";
    public static final String ATTR_RED_COLOR = "redColor";
    public static final String ATTR_LIGHT_RED_COLOR = "lightRedColor";
    public static final String ATTR_LIGHT_GREEN_COLOR = "lightGreenColor";
    public static final String ATTR_SHADOW_YELLOW_COLOR = "shadowYellowColor";
    public static final String ATTR_SYS_COLOR = "sysColor";
    public static final String ATTR_LIGHT_SYS_COLOR = "lightSysColor";
    public static final String ATTR_DEFAULT_FONT_COLOR = "defaultFontColor";
    public static final String ATTR_SILVER_COLOR = "silverColor";
    public static final String ATTR_SHADOWS_GREY_COLOR = "shadowsGreyColor";
    public static final String ATTR_KEYWORD_COLOR = "keywordColor";
    public static final String ATTR_VARIABLE_COLOR = "variableColor";
    public static final String ATTR_CLIENT_VARIABLE_COLOR = "clientVariableColor";
    public static final String ATTR_COMMENT_COLOR = "commentColor";
    public static final String ATTR_OBJECT_BROWSER_LIMIT = "objectBrowserLimit";
    public static final String ATTR_OBJECT_BROWSER_LIMIT_FOR_CLASSES = "objectBrowserLimitForClasses";
    public static final String ATTR_IS_OBJECT_BROWSER_LIMIT = "isObjectBrowserLimit";
    public static final String ATTR_IS_OBJECT_BROWSER_LIMIT_FOR_CLASSES = "isObjectBrowserLimitForClasses";
    public static final String ATTR_HISTORY_SRV = "historySrv";
    public static final String ATTR_SRV_HISTORY = "srvHistory";
    public static final String ATTR_HISTORY_IFC = "historyIfc";
    public static final String ATTR_IFC_HISTORY = "ifcHistory";
    public static final String ATTR_HISTORY_FLT = "historyFlt";
    public static final String ATTR_FLT_HISTORY = "fltHistory";
    public static final String ATTR_HISTORY_RPT = "historyRpt";
    public static final String ATTR_RPT_HISTORY = "rptHistory";

    // Политика паролей
    public static final String ATTR_MAX_VALID_PERIOD = "рекомен срок действия пароля";
    public static final String ATTR_MIN_LOGIN_LENGTH = "мин длина логина";
    public static final String ATTR_MIN_PASSWORD_LENGTH = "мин длина пароля";
    public static final String ATTR_MIN_PASSWORD_LENGTH_ADMIN = "мин длина пароля адм";
    public static final String ATTR_NUMBER_PASSWORD_DUBLICATE = "кол не дублир паролей";
    public static final String ATTR_NUMBER_PASSWORD_DUBLICATE_ADMIN = "кол не дублир паролей адм";
    public static final String ATTR_USE_NUMBERS = "использовать цифры";
    public static final String ATTR_USE_NOTALLNUMBERS = "не должно явно преобладать цифры";
    public static final String ATTR_USE_SYMBOLS = "использовать буквы";
    public static final String ATTR_USE_REGISTER_SYMBOLS = "использовать регистр";
    public static final String ATTR_USE_SPECIAL_SYMBOL = "использовать спец символы";
    public static final String ATTR_BAN_NAMES = "запрет имён";
    public static final String ATTR_BAN_FAMILIES = "запрет фамилий";
    public static final String ATTR_BAN_PHONE = "запрет телефонов";
    public static final String ATTR_BAN_WORD = "запрет слов";
    public static final String ATTR_BAN_KEYBOARD = "запрет слов на клавиатуре";
    public static final String ATTR_MAX_PERIOD_PASSWORD = "макс срок действия пароля";
    public static final String ATTR_MIN_PERIOD_PASSWORD = "мин срок действия пароля";
    public static final String ATTR_NUMBER_FAILED_LOGIN = "кол неуд авторизаций";
    public static final String ATTR_TIME_LOCK = "время блокировки";
    public static final String ATTR_BAN_LOGIN_IN_PASSWORD = "блокировать логин в пароле";
    public static final String ATTR_MAX_LENGTH_PASS = "макс длина пароля";
    public static final String ATTR_MAX_LENGTH_LOGIN = "макс длина логина";
    public static final String ATTR_CHANGE_FIRST_PASS = "смена 1го пароля";
    public static final String ATTR_MAX_PERIOD_FIRST_PASS = "макс срок 1го пароля";
    public static final String ATTR_BAN_REPEAT_CHAR = "запрет повтора 1х 3х букв пароля";
    public static final String ATTR_BAN_REPEAT_ANYWHERE_MORE_2_NOREGISTER_CHAR = "запрет повтора в любом месте из более 2-х одинаковых символов пароля";
    public static final String ATTR_ACTIVATE_LIABILITY_SIGN = "активировать подписание обязательства о неразглашении";
    public static final String ATTR_LIABILITY_SIGN_PERIOD = "срок действия подписи обязательства о неразглашении";
    public static final String ATTR_ACTIVATE_ECP_EXPIRY_NOTIF = "активировать оповещение об истечении срока действия ЭЦП";
    public static final String ATTR_ECP_EXPIRY_NOTIF_PERIOD = "период оповещения срока действия ЭЦП";
    public static final String ATTR_ACTIVATE_TEMP_REG_NOTIF = "активировать оповещение об истечении срока временной регистрации";
    public static final String ATTR_TEMP_REG_NOTIF_PERIOD = "период оповещения об истечении срока временной регистрации";
    public static final String ATTR_CHECK_CLIENT_IP = "проверять ip-адрес клиента";
    public static final String ATTR_USE_ECP = "авторизация с ЭЦП";
    public static final String ATTR_BAN_USE_OWN_IDENTIFICATION_DATA = "запрет использования собственных идентификационных данных в пароле";

    /** Новый параметр который будет использоваться в системах с новым интерфейсом. */
    public static final boolean SE_UI = "1".equals(System.getProperty("se_ui")) || "1".equals(System.getProperty("knb_ui"));

    public static final Insets INSETS_0 = new Insets(0, 0, 0, 0);
    public static final Insets INSETS_1 = new Insets(1, 1, 1, 1);
    public static final Insets INSETS_2 = new Insets(2, 2, 2, 2);
    public static final Insets INSETS_3 = new Insets(3, 3, 3, 3);
    public static final Insets INSETS_4 = new Insets(4, 4, 4, 4);
    public static final Insets INSETS_5 = new Insets(5, 5, 5, 5);
    
    /** Количество пользователей, которое система будет помнить для облегчения авторизации */
    public static final int NUMBER_STORED_USER = 10;
    /** Количество объектов класса при его отображении в редакторе*/
    public static final int LIMIT_VIEW_OBJ_CLASSES = 100;
    
    public static final Color MAIN_COLOR = new Color(238, 238, 238);
    public static final Color BLUE_SYS_COLOR = new Color(184, 207, 238); // голубой фон для КНБ
    public static final Color DARK_SHADOW_SYS_COLOR = new Color(70, 81, 106);
    public static final Color MID_SYS_COLOR = new Color(151, 168, 196);
    public static final Color LIGHT_YELLOW_COLOR = new Color(255, 255, 198);
    public static final Color RED_COLOR = new Color(100, 0, 0);
    public static final Color LIGHT_RED_COLOR = new Color(255, 151, 151);
    public static final Color LIGHT_GREEN_COLOR = new Color(151, 255, 151);
    public static final Color SHADOW_YELLOW_COLOR = new Color(214, 201, 161);
    public static final Color SYS_COLOR = new Color(178, 186, 202);
    public static final Color LIGHT_SYS_COLOR = new Color(216, 221, 231);
    public static final Color DEFAULT_FONT_COLOR = new Color(69, 87, 118);
    public static final Color SILVER_COLOR = new Color(192, 192, 192);// цвет фона редактора формул
    public static final Color SHADOWS_GREY_COLOR = new Color(54, 57, 61);
    public static final Color KEYWORD_COLOR = new Color(0, 0, 128);// цвет ключевого слова
    public static final Color VARIABLE_COLOR = new Color(102, 14, 122);// цвет системной переменной
    public static final Color CLIENT_VARIABLE_COLOR = new Color(0, 102, 0);// цвет пользовательской переменной
    public static final Color COMMENT_COLOR = Color.GRAY;// цвет комментариев
    
    public static final int HEIGHT_ROW = 18; // высота строк таблиц
    
    // тип вывода WebTextField
    public static final int DEF_TYPE = 0; // 
    public static final int INT_TYPE = 1; // 
    public static final int FLOAT_TYPE = 2; //
    
    // типы браузеров
    public static final String CHROME = "Chrome";
    public static final String OMNIWEB = "OmniWeb";
    public static final String SAFARI = "Safari";
    public static final String OPERA = "Opera";
    public static final String ICAB = "iCab";
    public static final String KONQUEROR = "Konqueror";
    public static final String FIREFOX = "Firefox";
    public static final String CAMINO = "Camino";
    public static final String NETSCAPE = "Netscape";
    public static final String EXPLORER = "Explorer";
    public static final String MOZILLA = "Mozilla";

    // типы ОС
    public static final String WIN = "Windows";
    public static final String MAC = "Mac";
    public static final String IPHONE = "iPhone/iPod";
    public static final String LINUX = "Linux";
    
    /** количество миллисекунд в одном дне 60 * 60 * 24 * 1000 */
    public final static long ONE_DAY = 86400000;
    
    public final static Dimension BTN_EDITOR_SIZE = new Dimension(16, 16);
    public final static int TIME_OUT_WEB = 1000;
    /** Ожидание ответа от пользователя */
    public final static int TIME_OUT_WEB_WAIT_QUANTS = 180;
    public final static int TIME_OUT_WEB_LONG_POLLING_QUANTS = 180;
    public final static int TIME_OUT_REPORT_WAIT_QUANTS = 600;

    public final static int TIME_OUT_WEB_WAIT_QUANT = 1000;

    public final static String REL_PATH_IMG = "media/img/";
    
    /*
     * заглушки для справочников
     * TODO переработать заглушки. Как хороший вариант
     * работы - связять данные справочники с личными данными сотрудников и
     * делать проверку по ним
     */
    
    // источники запуска процессов
    /** Источник запуска "по умолчанию"*/
    public final static OrConstant DEFAULT_SOURSE = new OrConstant();
    /** Запуск процесса из WEB-интерфейса, раздела "Процессы"*/
    public final static OrConstant WEB_PROCESS_SOURSE = new OrConstant();
    public final static OrConstant WEB_MAIN = new OrConstant();
    public final static OrConstant WEB_PROCESS_MONITOR = new OrConstant();
    
    public static final Map<Character, String> nonletters = new HashMap<Character, String>();
    static {
    	nonletters.put(' ', "_");
    	nonletters.put(':', "_");
    	nonletters.put('-', "_");
    	nonletters.put(',', "_");
    	nonletters.put('/', "_");
    	nonletters.put('\'', "");
    	nonletters.put('<', "");
    	nonletters.put('>', "");
    	nonletters.put('!', "");
    	nonletters.put('@', "");
    	nonletters.put('?', "");
    }
    // Служебные слова SQL, которые нельзя применять для наименований таблиц, колонок
    public static final List<String> reservedSQLWords = new ArrayList<String>();
    static {
    	reservedSQLWords.add("ORDER");
    	reservedSQLWords.add("INDEX");
    	reservedSQLWords.add("BLOB");
    	reservedSQLWords.add("FROM");
    	reservedSQLWords.add("TO");
    	reservedSQLWords.add("IN");
    	reservedSQLWords.add("DATABASE");
    	reservedSQLWords.add("VALUES");
    	reservedSQLWords.add("USER");
    	reservedSQLWords.add("DATE");
    	reservedSQLWords.add("CURRENT");
    	reservedSQLWords.add("START");
    	reservedSQLWords.add("END");
    	reservedSQLWords.add("ACCESS");
    	reservedSQLWords.add("UID");
    }
    /**
     * Транслит по ГОСТ 7.79-2000 и  ГУГК № 231 с рекомендациями ООН за 1987.
     * С доработками спец символов, твёрдого и мягкого знаков под наш функционал.
     * */
    public static final Map<Character, String> letters = new HashMap<Character, String>();
    static {
        letters.put('А', "A");
        letters.put('Б', "B");
        letters.put('В', "V");
        letters.put('Г', "G");
        letters.put('Д', "D");
        letters.put('Е', "E");
        letters.put('Ё', "Jo");
        letters.put('Ж', "Zh");
        letters.put('З', "Z");
        letters.put('И', "I");
        letters.put('Й', "J");
        letters.put('К', "K");
        letters.put('Л', "L");
        letters.put('М', "M");
        letters.put('Н', "N");
        letters.put('О', "O");
        letters.put('П', "P");
        letters.put('Р', "R");
        letters.put('С', "S");
        letters.put('Т', "T");
        letters.put('У', "U");
        letters.put('Ф', "F");
        letters.put('Х', "H");
        letters.put('Ц', "C");
        letters.put('Ч', "Ch");
        letters.put('Ш', "Sh");
        letters.put('Щ', "Shh");
        letters.put('Ъ', "");
        letters.put('Ы', "Y");
        letters.put('Ь', "");
        letters.put('Э', "E");
        letters.put('Ю', "Ju");
        letters.put('Я', "Ja");
        
        letters.put('а', "a");
        letters.put('б', "b");
        letters.put('в', "v");
        letters.put('г', "g");
        letters.put('д', "d");
        letters.put('е', "e");
        letters.put('ё', "jo");
        letters.put('ж', "zh");
        letters.put('з', "z");
        letters.put('и', "i");
        letters.put('й', "j");
        letters.put('к', "k");
        letters.put('л', "l");
        letters.put('м', "m");
        letters.put('н', "n");
        letters.put('о', "o");
        letters.put('п', "p");
        letters.put('р', "r");
        letters.put('с', "s");
        letters.put('т', "t");
        letters.put('у', "u");
        letters.put('ф', "f");
        letters.put('х', "h");
        letters.put('ц', "c");
        letters.put('ч', "ch");
        letters.put('ш', "sh");
        letters.put('щ', "shh");
        letters.put('ъ', "");
        letters.put('ы', "y");
        letters.put('ь', "");
        letters.put('э', "e");
        letters.put('ю', "ju");
        letters.put('я', "ja");
    }
    
    public static final List<String> IMAGE_FORMATS = Arrays.asList ( "png", "apng", "gif", "agif", "jpg", "jpeg", "jpeg2000", "bmp" );
    
    // Debug option
    public static boolean DEBUG = false;
    public static Font DEBUG_FONT = new Font("Dialog", Font.BOLD, 8);
    public static NumberFormat DEBUG_FORMAT = new DecimalFormat("#0.00");
    
    /**
     * Класс для создания уникальных констант
     * @author Sergey Lebedev
     *
     */
    private static class OrConstant {
        private OrConstant() {
        }
    }
    
    public static final String CLIENT_TYPE_APP = "client";
    public static final String CLIENT_TYPE_DESIGNER = "designer";
    public static final String CLIENT_TYPE_REPORT = "report";
    public static final String CLIENT_TYPE_WEB = "webclient";
    public static final String CLIENT_TYPE_LOCALWEB = "localweb";
    public static final String CLIENT_TYPE_MOBILE = "mobile";
    
    public static final String ROOT_CERT_PATH = System.getProperty("root_cert_path");
    public static final String OCSP_SERVICE_URL = System.getProperty("ocsp_service_url", "http://ocsp.pki.kz:60001");
    public static final String PROXY_HOST = System.getProperty("proxy_host");
    public static final String PROXY_PORT = System.getProperty("proxy_port");

    // Запас ширины и высоты для диалогов в вебе - границы, титул диалога, кнопки диалога, бордюры, отступы и т.д.
    public static final int WEB_DIALOG_EXTRA_WIDTH = 14;
    public static final int WEB_DIALOG_EXTRA_HEIGHT = 61;

    public static final int MAX_MESSAGE_SIZE = 	  1000000000; // 1GB
    public static final int MAX_IMAGE_SIZE = 	    50000000; // 50MB
    public static final int MAX_DOC_SIZE = 		  1000000000; // 1GB
    public static final int MAX_BLOB_SIZE = Integer.MAX_VALUE; // 2,147483647 GB
    public static final long MAX_ARCHIVED_SIZE = 10000000000L; // 10GB
    public static final int MAX_USER_NAME = 50;
    public static final int MAX_FILE_NAME = 200;
    public static final int MAX_ELEMENT_NAME = 			1000;
    public static final int MAX_DIALOG_MSG_SIZE = 	  	5000;
    
    public static final int MAX_ELEMENTS_COUNT_1 = 	  	3000;
    public static final int MAX_ELEMENTS_COUNT_2 = 	  	30000;
    public static final int MAX_ELEMENTS_COUNT_3 = 	  	100000;
    public static final int MAX_ELEMENTS_COUNT_4 = 	  	1000000;
    
    public static final int NEED_CHECK_FOLDER = 100;
    
    static public final Locale OK = Locale.ROOT;
    
    public final static boolean CHECK_DB_BLOCK = "true".equals(System.getProperty("checkDbBlock")) || "1".equals(System.getProperty("checkDbBlock"));
    
}
