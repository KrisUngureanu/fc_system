package kz.tamur.rt;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.util.AttrRequestBuilder;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;

import kz.tamur.comps.Constants;
import kz.tamur.comps.models.ColorAct;
import kz.tamur.comps.models.GradientColor;
import kz.tamur.ods.Value;

import static kz.tamur.comps.Constants.*;
import static kz.tamur.rt.Utils.isColorActive;

/**
 * Класс, реализующий работу с конфигурацией системы
 * 
 * @author Sergey Lebedev
 */
public class GlobalConfig {

    /** KRN объект данного класса */
    protected KrnObject krnObj;

    /** Объект, хранения загруженной из БД конфигурации */
    protected Config config = new Config();

    /** Кернель */
    protected Kernel krn;

    /** Массив идентификаторов объектов, для временных целей */
    protected long[] oids = null;

    /** KRN класс данного класса */
    protected KrnClass baseCls;

    /** Сборщик запросов */
    protected AttrRequestBuilder arb;

    /** Строка с данными, используется со сборщиком запросов */
    protected Object[] row;

    private static Map<UUID, GlobalConfig> instances_ = new HashMap<UUID, GlobalConfig>();

    /** KRN класс класса "string" */
    KrnClass clsString;

    /** KRN класс класса "long" */
    KrnClass clsLong;
    
    KrnClass clsProp;

    /** Флаг доступности класса ControlFolder */
    public boolean isExistClassControlFolder = false;

    /** Флаг доступности класса ControlFolderRoot */
    public boolean isExistClassControlFolderRoot = false;

    /** Флаг доступности класса ConfigGlobal */
    public boolean isExistClassConfigGlobal = false;

    /** Флаг доступности класса ConfigLocal */
    public boolean isExistClassConfigLocal = false;

    /** Флаг доступности класса ConfigObject */
    public boolean isExistClassConfigObject = false;

    /** Флаг доступности класса Property */
    public boolean isExistClassProperty = false;

    /** Флаг корректности класса ControlFolder */
    public boolean isCorrectClassControlFolder = true;

    /** Флаг корректности класса ConfigGlobal */
    public boolean isCorrectClassConfigGlobal = true;

    /** Флаг корректности класса ConfigLocal */
    public boolean isCorrectClassConfigLocal = true;

    /** Флаг корректности класса ConfigObject */
    public boolean isCorrectClassConfigObject = true;

    /** Флаг корректности класса Property */
    public boolean isCorrectClassProperty = true;

    /** Флаг корректности класса User */
    public boolean isCorrectClassUser = true;

    /** Флаг корректности класса ProcessDef */
    public boolean isCorrectClassProcessDef = true;

    /** атрибут класса, используется как временный контейнер */
    private KrnAttribute attr;
    private KrnAttribute attr2;

    private Map<Long, KrnObject> clLimObj = new HashMap<Long, KrnObject>();

    private GlobalConfig() {
        this(Kernel.instance());
    }

    /**
     * Создание нового экземпляра класса.
     */
    private GlobalConfig(Kernel krn) {
        this.krn = krn;
    }
    
    public void init() {
        checkDB();
        initialize();
    }

    private void checkDB() {
        try {
            baseCls = krn.getClassByName(NAME_CLASS_CONFIG_GLOBAL_FIX);
            if (baseCls != null) {
                KrnObject[] objs = krn.getClassObjects(baseCls, 0l);
                krnObj = (objs != null && objs.length > 0) ? objs[0] : krn.createObject(baseCls, 0);
            }
            clsProp = krn.getClassByName(NAME_CLASS_PROPERTY);
        } catch (Exception e) {
            config.setGradientMainFrame(GLOBAL_DEF_GRADIENT);
            config.setGradientMenuPanel(GLOBAL_DEF_GRADIENT);
            return;
        }
    }
    
    /**
     * шаблон Singltone
     * 
     * @return global единственный экземпляр данного класса
     */
    public static GlobalConfig instance(Kernel krn) {
    	GlobalConfig co = null;
    	boolean init = false;
    	
    	synchronized (instances_) {
    		co = instances_.get(krn.getUUID());
            if (co == null) {
                co = new GlobalConfig(krn);
                instances_.put(krn.getUUID(), co);
                init = true;
            }
    	}
    	
    	if (init)
    		co.init();
    	
        return co;
    }

    /**
     * Для веба обязательно ощищать созданные экземпляры конфигураций, так как 
     * при каждом новом входе в Систему создается новый Kernel, а старые не
     * удаляются, что приводит к большим потерям памяти
     */
    public static void removeInstance(Kernel krn) {
    	synchronized (instances_) {
    		instances_.remove(krn.getUUID());
    	}
    }

    /**
     * Проверка корректности структуры БД
     * 
     * @throws KrnException
     *             the krn exception
     */
    public void checkConfigDataBase() throws KrnException {
        isExistClassConfigGlobal = krn.checkExistenceClassByName(NAME_CLASS_CONFIG_GLOBAL_FIX);
        isExistClassControlFolder = krn.checkExistenceClassByName(NAME_CLASS_CONTROL_FOLDER);
        isExistClassControlFolderRoot = krn.checkExistenceClassByName(NAME_CLASS_CONTROL_FOLDER_ROOT);
        isExistClassConfigLocal = krn.checkExistenceClassByName(NAME_CLASS_CONFIG_LOCAL);
        isExistClassConfigObject = krn.checkExistenceClassByName(NAME_CLASS_CONFIG_OBJECT);
        isExistClassProperty = krn.checkExistenceClassByName(NAME_CLASS_PROPERTY);
        KrnClass clsCnf = null;
        // проверка структуры класса
        if (isExistClassConfigGlobal) {
            clsCnf = krn.getClassByName(NAME_CLASS_CONFIG_GLOBAL_FIX);
            isCorrectClassConfigGlobal = !(krn.getAttributeByName(clsCnf, ATTR_COLOR_BACK_TAB_TITLE) == null
                    || krn.getAttributeByName(clsCnf, ATTR_COLOR_FONT_BACK_TAB_TITLE) == null
                    || krn.getAttributeByName(clsCnf, ATTR_COLOR_FONT_TAB_TITLE) == null
                    || krn.getAttributeByName(clsCnf, ATTR_COLOR_HEADER_TABLE) == null
                    || krn.getAttributeByName(clsCnf, ATTR_COLOR_MAIN) == null
                    || krn.getAttributeByName(clsCnf, ATTR_COLOR_TAB_TITLE) == null
                    || krn.getAttributeByName(clsCnf, ATTR_GRADIENT_CONTROL_PANEL) == null
                    || krn.getAttributeByName(clsCnf, ATTR_GRADIENT_FIELD_NO_FLC) == null
                    || krn.getAttributeByName(clsCnf, ATTR_GRADIENT_MAIN_FRAME) == null
                    || krn.getAttributeByName(clsCnf, ATTR_GRADIENT_MENU_PANEL) == null
                    || krn.getAttributeByName(clsCnf, ATTR_TRANSPARENT_BACK_TAB_TITLE) == null
                    || krn.getAttributeByName(clsCnf, ATTR_TRANSPARENT_CELL_TABLE) == null
                    || krn.getAttributeByName(clsCnf, ATTR_TRANSPARENT_DIALOG) == null
                    || krn.getAttributeByName(clsCnf, ATTR_TRANSPARENT_MAIN) == null
                    || krn.getAttributeByName(clsCnf, ATTR_TRANSPARENT_SELECTED_TAB_TITLE) == null
                    || krn.getAttributeByName(clsCnf, ATTR_BLUE_SYS_COLOR) == null
                    || krn.getAttributeByName(clsCnf, ATTR_DARK_SHADOW_SYS_COLOR) == null
                    || krn.getAttributeByName(clsCnf, ATTR_MID_SYS_COLOR) == null
                    || krn.getAttributeByName(clsCnf, ATTR_LIGHT_YELLOW_COLOR) == null
                    || krn.getAttributeByName(clsCnf, ATTR_RED_COLOR) == null
                    || krn.getAttributeByName(clsCnf, ATTR_LIGHT_RED_COLOR) == null
                    || krn.getAttributeByName(clsCnf, ATTR_LIGHT_GREEN_COLOR) == null
                    || krn.getAttributeByName(clsCnf, ATTR_SHADOW_YELLOW_COLOR) == null
                    || krn.getAttributeByName(clsCnf, ATTR_SYS_COLOR) == null
                    || krn.getAttributeByName(clsCnf, ATTR_LIGHT_SYS_COLOR) == null
                    || krn.getAttributeByName(clsCnf, ATTR_DEFAULT_FONT_COLOR) == null
                    || krn.getAttributeByName(clsCnf, ATTR_SILVER_COLOR) == null
                    || krn.getAttributeByName(clsCnf, ATTR_SHADOWS_GREY_COLOR) == null
                    || krn.getAttributeByName(clsCnf, ATTR_KEYWORD_COLOR) == null
                    || krn.getAttributeByName(clsCnf, ATTR_VARIABLE_COLOR) == null
                    || krn.getAttributeByName(clsCnf, ATTR_CLIENT_VARIABLE_COLOR) == null
                    || krn.getAttributeByName(clsCnf, ATTR_COMMENT_COLOR) == null
                    || krn.getAttributeByName(clsCnf, ATTR_OBJECT_BROWSER_LIMIT) == null
                    || krn.getAttributeByName(clsCnf, ATTR_OBJECT_BROWSER_LIMIT_FOR_CLASSES) == null
                    || krn.getAttributeByName(clsCnf, ATTR_IS_OBJECT_BROWSER_LIMIT) == null 
                    || krn.getAttributeByName(clsCnf, ATTR_IS_OBJECT_BROWSER_LIMIT_FOR_CLASSES) == null
                    );
        }
        
        if (isExistClassConfigLocal) {
            clsCnf = krn.getClassByName(NAME_CLASS_CONFIG_LOCAL);
            if (krn.getAttributeByName(clsCnf, "maxObjectCount") == null
                    || krn.getAttributeByName(clsCnf, "isToolBar") == null
                    || krn.getAttributeByName(clsCnf, "isMonitor") == null
                    || krn.getAttributeByName(clsCnf, "configByUUIDs") == null 
                    || krn.getAttributeByName(clsCnf, ATTR_HISTORY_SRV) == null
                    || krn.getAttributeByName(clsCnf, ATTR_HISTORY_IFC) == null
                    || krn.getAttributeByName(clsCnf, ATTR_HISTORY_FLT) == null
                    || krn.getAttributeByName(clsCnf, ATTR_HISTORY_RPT) == null
                    ) {
                isCorrectClassConfigLocal = false;
            }
        }
        
        if (isExistClassConfigObject) {
            clsCnf = krn.getClassByName(NAME_CLASS_CONFIG_OBJECT);
            if (krn.getAttributeByName(clsCnf, "uuid") == null || krn.getAttributeByName(clsCnf, "properties") == null) {
                isCorrectClassConfigObject = false;
            }
        }
        if (isExistClassProperty) {
            clsCnf = krn.getClassByName(NAME_CLASS_PROPERTY);
            if (krn.getAttributeByName(clsCnf, "name") == null || krn.getAttributeByName(clsCnf, "value") == null) {
                isCorrectClassProperty = false;
            }
        }
        if (krn.checkExistenceClassByName("User")) {
            clsCnf = krn.getClassByName("User");
            if (krn.getAttributeByName(clsCnf, "config") == null || krn.getAttributeByName(clsCnf, "lastLoginTime") == null
                    || krn.getAttributeByName(clsCnf, "isLogged") == null
                    || krn.getAttributeByName(clsCnf, "previous passwords") == null
                    || krn.getAttributeByName(clsCnf, "время блокировки") == null
                    || krn.getAttributeByName(clsCnf, "дата изменения пароля") == null
                    || krn.getAttributeByName(clsCnf, "дата истечения срока действия пароля") == null
                    || krn.getAttributeByName(clsCnf, "кол неуд авторизаций") == null) {
                isCorrectClassUser = false;
            }
        }
        if (isExistClassControlFolder) {
            clsCnf = krn.getClassByName(NAME_CLASS_CONTROL_FOLDER);
            if (krn.getAttributeByName(clsCnf, "title") == null || krn.getAttributeByName(clsCnf, "parent") == null
                    || krn.getAttributeByName(clsCnf, "children") == null || krn.getAttributeByName(clsCnf, "value") == null
                    || krn.getAttributeByName(clsCnf, "type") == null) {
                isCorrectClassControlFolder = false;
            }
        }
        clsCnf = krn.getClassByName("ProcessDef");
        if (krn.getAttributeByName(clsCnf, "isBtnToolBar") == null) {
            isCorrectClassProcessDef = false;
        }
    }

    /**
     * Перезагрузка переменных класса
     */
    public void reloadParam() {
        krn = Kernel.instance();
        initialize();
    }

    /**
     * Инициализация настроек.
     * выполняет чтение настроек из базы, преобразовывает и сохраняет их в своих атрибутах
     */
    protected void initialize() {
        try {
            arb = new AttrRequestBuilder(baseCls, krn).add(ATTR_GRADIENT_MAIN_FRAME).add(ATTR_GRADIENT_CONTROL_PANEL)
                    .add(ATTR_GRADIENT_MENU_PANEL).add(ATTR_TRANSPARENT_MAIN).add(ATTR_TRANSPARENT_DIALOG).add(ATTR_COLOR_MAIN)
                    .add(ATTR_TRANSPARENT_CELL_TABLE).add(ATTR_COLOR_HEADER_TABLE).add(ATTR_COLOR_TAB_TITLE)
                    .add(ATTR_COLOR_BACK_TAB_TITLE).add(ATTR_COLOR_FONT_TAB_TITLE).add(ATTR_COLOR_FONT_BACK_TAB_TITLE)
                    .add(ATTR_TRANSPARENT_BACK_TAB_TITLE).add(ATTR_TRANSPARENT_SELECTED_TAB_TITLE)
                    .add(ATTR_GRADIENT_FIELD_NO_FLC).add(ATTR_BLUE_SYS_COLOR).add(ATTR_DARK_SHADOW_SYS_COLOR)
                    .add(ATTR_MID_SYS_COLOR).add(ATTR_LIGHT_YELLOW_COLOR).add(ATTR_RED_COLOR).add(ATTR_LIGHT_RED_COLOR)
                    .add(ATTR_LIGHT_GREEN_COLOR).add(ATTR_SHADOW_YELLOW_COLOR).add(ATTR_SYS_COLOR).add(ATTR_LIGHT_SYS_COLOR)
                    .add(ATTR_DEFAULT_FONT_COLOR).add(ATTR_SILVER_COLOR).add(ATTR_SHADOWS_GREY_COLOR).add(ATTR_KEYWORD_COLOR)
                    .add(ATTR_VARIABLE_COLOR).add(ATTR_CLIENT_VARIABLE_COLOR).add(ATTR_COMMENT_COLOR)
                    .add(ATTR_OBJECT_BROWSER_LIMIT).add(ATTR_OBJECT_BROWSER_LIMIT_FOR_CLASSES).add(ATTR_IS_OBJECT_BROWSER_LIMIT)
                    .add(ATTR_IS_OBJECT_BROWSER_LIMIT_FOR_CLASSES);
            long[] objIds = { krnObj.id };
            row = krn.getObjects(objIds, arb.build(), 0).get(0);
            config.setGradientMainFrame(new GradientColor(arb.getStringValue(ATTR_GRADIENT_MAIN_FRAME, row)));
            config.setGradientControlPanel(new GradientColor(arb.getStringValue(ATTR_GRADIENT_CONTROL_PANEL, row)));
            config.setGradientMenuPanel(new GradientColor(arb.getStringValue(ATTR_GRADIENT_MENU_PANEL, row)));
            config.setTransparentMain(arb.getLongValue(ATTR_TRANSPARENT_MAIN, row, 0) == 1);
            config.setTransparentDialog(arb.getLongValue(ATTR_TRANSPARENT_DIALOG, row, 1) == 1);
            config.setColorMain(decodeColorAndActiv(arb.getStringValue(ATTR_COLOR_MAIN, row)));
            config.setTransparentCellTable((int) arb.getLongValue(ATTR_TRANSPARENT_CELL_TABLE, row, 0));
            config.setColorHeaderTable(decodeColor(arb.getStringValue(ATTR_COLOR_HEADER_TABLE, row)));
            config.setColorTabTitle(decodeColor(arb.getStringValue(ATTR_COLOR_TAB_TITLE, row)));
            config.setColorBackTabTitle(decodeColor(arb.getStringValue(ATTR_COLOR_BACK_TAB_TITLE, row)));
            config.setColorFontTabTitle(decodeColor(arb.getStringValue(ATTR_COLOR_FONT_TAB_TITLE, row)));
            config.setColorFontBackTabTitle(decodeColor(arb.getStringValue(ATTR_COLOR_FONT_BACK_TAB_TITLE, row)));
            config.setTransparentBackTabTitle((int) arb.getLongValue(ATTR_TRANSPARENT_BACK_TAB_TITLE, row, 0));
            config.setTransparentSelectedTabTitle((int) arb.getLongValue(ATTR_TRANSPARENT_SELECTED_TAB_TITLE, row, 0));
            config.setGradientFieldNOFLC(new GradientColor(arb.getStringValue(ATTR_GRADIENT_FIELD_NO_FLC, row)));

            config.setBlueSysColor(decodeColorAndActiv(arb.getStringValue(ATTR_BLUE_SYS_COLOR, row)));
            config.setDarkShadowSysColor(decodeColorAndActiv(arb.getStringValue(ATTR_DARK_SHADOW_SYS_COLOR, row)));
            config.setMidSysColor(decodeColorAndActiv(arb.getStringValue(ATTR_MID_SYS_COLOR, row)));
            config.setLightYellowColor(decodeColorAndActiv(arb.getStringValue(ATTR_LIGHT_YELLOW_COLOR, row)));
            config.setRedColor(decodeColorAndActiv(arb.getStringValue(ATTR_RED_COLOR, row)));
            config.setLightRedColor(decodeColorAndActiv(arb.getStringValue(ATTR_LIGHT_RED_COLOR, row)));
            config.setLightGreenColor(decodeColorAndActiv(arb.getStringValue(ATTR_LIGHT_GREEN_COLOR, row)));
            config.setShadowYellowColor(decodeColorAndActiv(arb.getStringValue(ATTR_SHADOW_YELLOW_COLOR, row)));
            config.setSysColor(decodeColorAndActiv(arb.getStringValue(ATTR_SYS_COLOR, row)));
            config.setLightSysColor(decodeColorAndActiv(arb.getStringValue(ATTR_LIGHT_SYS_COLOR, row)));
            config.setDefaultFontColor(decodeColorAndActiv(arb.getStringValue(ATTR_DEFAULT_FONT_COLOR, row)));
            config.setSilverColor(decodeColorAndActiv(arb.getStringValue(ATTR_SILVER_COLOR, row)));
            config.setShadowsGreyColor(decodeColorAndActiv(arb.getStringValue(ATTR_SHADOWS_GREY_COLOR, row)));
            config.setKeywordColor(decodeColorAndActiv(arb.getStringValue(ATTR_KEYWORD_COLOR, row)));
            config.setVariableColor(decodeColorAndActiv(arb.getStringValue(ATTR_VARIABLE_COLOR, row)));
            config.setClientVariableColor(decodeColorAndActiv(arb.getStringValue(ATTR_CLIENT_VARIABLE_COLOR, row)));
            config.setCommentColor(decodeColorAndActiv(arb.getStringValue(ATTR_COMMENT_COLOR, row)));
            config.setObjectBrowserLimit((int) arb.getLongValue(ATTR_OBJECT_BROWSER_LIMIT, row, 100));
            config.setObjectBrowserLimit(arb.getBooleanValue(ATTR_IS_OBJECT_BROWSER_LIMIT, row));
            config.setObjectBrowserLimitForClasses(arb.getBooleanValue(ATTR_IS_OBJECT_BROWSER_LIMIT_FOR_CLASSES, row));
            // Список свойств классов с данными об их ограничении на вывод
            List<Value> props = (List<Value>) arb.getValue(ATTR_OBJECT_BROWSER_LIMIT_FOR_CLASSES, row);
            long[] propsIds = {};
            if (props != null) {
                propsIds = new long[props.size()];
                for (int i = 0; i < props.size(); i++) {
                    propsIds[i] = ((KrnObject) props.get(i).value).id;
                }
                AttrRequestBuilder arbProp = new AttrRequestBuilder(clsProp, krn).add("name").add("value");
                // получить объекты
                List<Object[]> propsRows = krn.getObjects(propsIds, arbProp.build(), 0);
                // парсинг полученных объектов
                if (propsRows.size() > 0) {
                    for (Object[] propRow : propsRows) {
                        if (arbProp.getStringValue("name", propRow) != null) {
                            Long classId = Long.parseLong(arbProp.getStringValue("name", propRow));
                            String value = arbProp.getStringValue("value", propRow);
                            config.getObjectBrowserLimitForClasses().put(classId, Integer.parseInt(value));
                            clLimObj.put(classId, (KrnObject) propRow[0]);
                        }
                    }
                }
            }
        } catch (Exception e) {
            config.setGradientMainFrame(GLOBAL_DEF_GRADIENT);
            config.setGradientMenuPanel(GLOBAL_DEF_GRADIENT);
        }
        updateSysVar();
    }

    /**
     * Расшифровка цвета
     * 
     * @param color
     *            строковое значение цвета
     * @return color расшифрованный цвет
     */
    public static Color decodeColor(String color) {
        return color == null ? null : Color.decode(color);
    }

    public static ColorAct decodeColorAndActiv(String color) {
        return color == null ? null : new ColorAct(Color.decode(color.replaceFirst(" .+", "")), color.replaceFirst(".+ ", "")
                .equals("1"));
    }

    /**
     * Установить gradient main frame.
     * 
     * @param gradientMainFrame
     *            the new gradient main frame
     */
    public void setGradientMainFrame(GradientColor gradient) {
        config.setGradientMainFrame(gradient);
        try {
            krn.setString(krnObj.id, krnObj.classId, ATTR_GRADIENT_MAIN_FRAME, 0, 0,
                    gradient == null ? null : gradient.toString(), 0);
        } catch (KrnException e) {
            e.printStackTrace();
        }

    }

    /**
     * Установить gradient control panel.
     * 
     * @param gradientControlPanel
     *            the new gradient control panel
     */
    public void setGradientControlPanel(GradientColor gradient) {
        config.setGradientControlPanel(gradient);
        try {
            krn.setString(krnObj.id, krnObj.classId, ATTR_GRADIENT_CONTROL_PANEL, 0, 0,
                    gradient == null ? null : gradient.toString(), 0);
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }

    /**
     * Установить gradient menu panel.
     * 
     * @param gradientMenuPanel
     *            the new gradient menu panel
     */
    public void setGradientMenuPanel(GradientColor gradient) {
        config.setGradientMenuPanel(gradient);
        try {
            krn.setString(krnObj.id, krnObj.classId, ATTR_GRADIENT_MENU_PANEL, 0, 0,
                    gradient == null ? null : gradient.toString(), 0);
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }

    /**
     * Установить transparent main.
     * 
     * @param transparentMain
     *            the new transparent main
     */
    public void setTransparentMain(boolean transparent) {
        config.setTransparentMain(transparent);
        try {
            krn.setLong(krnObj.id, krnObj.classId, ATTR_TRANSPARENT_MAIN, 0, Utils.toLong(transparent), 0);
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }

    /**
     * Установить transparent dialog.
     * 
     * @param transparentDialog
     *            the new transparent dialog
     */
    public void setTransparentDialog(boolean transparent) {
        config.setTransparentDialog(transparent);
        try {
            krn.setLong(krnObj.id, krnObj.classId, ATTR_TRANSPARENT_DIALOG, 0, Utils.toLong(transparent), 0);
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }

    /**
     * Установить color main.
     * 
     * @param colorMain
     *            the new color main
     */
    public void setColorMain(ColorAct color) {
        config.setColorMain(color);
        try {
            krn.setString(krnObj.id, krnObj.classId, ATTR_COLOR_MAIN, 0, 0, color == null ? null : color.getRGBAct(), 0);
        } catch (KrnException e) {
            e.printStackTrace();
        }
        Utils.setMainColor(isColorActive(color) ? color : Constants.MAIN_COLOR);
    }

    /**
     * Установить transparent cell table.
     * 
     * @param transparentCellTable
     *            the new transparent cell table
     */
    public void setTransparentCellTable(int transparent) {
        config.setTransparentCellTable(transparent);
        try {
            krn.setLong(krnObj.id, krnObj.classId, ATTR_TRANSPARENT_CELL_TABLE, 0, transparent, 0);
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }

    /**
     * Установить color header table.
     * 
     * @param colorHeaderTable
     *            the new color header table
     */
    public void setColorHeaderTable(Color color) {
        config.setColorHeaderTable(color);
        try {
            krn.setString(krnObj.id, krnObj.classId, ATTR_COLOR_HEADER_TABLE, 0, 0, color == null ? null : color.getRGB() + "", 0);
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }

    /**
     * Установить color tab title.
     * 
     * @param colorTabTitle
     *            the new color tab title
     */
    public void setColorTabTitle(Color color) {
        config.setColorTabTitle(color);
        try {
            krn.setString(krnObj.id, krnObj.classId, ATTR_COLOR_TAB_TITLE, 0, 0, color == null ? null : color.getRGB() + "", 0);
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }

    /**
     * Установить color back tab title.
     * 
     * @param colorBackTabTitle
     *            the new color back tab title
     */
    public void setColorBackTabTitle(Color color) {
        config.setColorBackTabTitle(color);
        try {
            krn.setString(krnObj.id, krnObj.classId, ATTR_COLOR_BACK_TAB_TITLE, 0, 0, color == null ? null : color.getRGB() + "",
                    0);
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }

    /**
     * Установить color font tab title.
     * 
     * @param colorFontTabTitle
     *            the new color font tab title
     */
    public void setColorFontTabTitle(Color color) {
        config.setColorFontTabTitle(color);
        try {
            krn.setString(krnObj.id, krnObj.classId, ATTR_COLOR_FONT_TAB_TITLE, 0, 0, color == null ? null : color.getRGB() + "",
                    0);
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }

    /**
     * Установить color font back tab title.
     * 
     * @param colorFontBackTabTitle
     *            the new color font back tab title
     */
    public void setColorFontBackTabTitle(Color color) {
        config.setColorFontBackTabTitle(color);
        try {
            krn.setString(krnObj.id, krnObj.classId, ATTR_COLOR_FONT_BACK_TAB_TITLE, 0, 0, color == null ? null : color.getRGB()
                    + "", 0);
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }

    /**
     * Установить transparent back tab title.
     * 
     * @param transparentBackTabTitle
     *            the new transparent back tab title
     */
    public void setTransparentBackTabTitle(int transparent) {
        config.setTransparentBackTabTitle(transparent);
        try {
            krn.setLong(krnObj.id, krnObj.classId, ATTR_TRANSPARENT_BACK_TAB_TITLE, 0, transparent, 0);
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }

    /**
     * Установить transparent selected tab title.
     * 
     * @param transparentSelectedTabTitle
     *            the new transparent selected tab title
     */
    public void setTransparentSelectedTabTitle(int transparent) {
        config.setTransparentSelectedTabTitle(transparent);
        try {
            krn.setLong(krnObj.id, krnObj.classId, ATTR_TRANSPARENT_SELECTED_TAB_TITLE, 0, transparent, 0);
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }

    /**
     * Установить gradient field noflc.
     * 
     * @param gradientFieldNOFLC
     *            the new gradient field noflc
     */
    public void setGradientFieldNOFLC(GradientColor gradient) {
        config.setGradientFieldNOFLC(gradient);
        try {
            krn.setString(krnObj.id, krnObj.classId, ATTR_GRADIENT_FIELD_NO_FLC, 0, 0,
                    gradient == null ? null : gradient.toString(), 0);
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param blueSysColor
     *            the blueSysColor to set
     */
    public void setBlueSysColor(ColorAct color) {
        config.setBlueSysColor(color);
        try {
            krn.setString(krnObj.id, krnObj.classId, ATTR_BLUE_SYS_COLOR, 0, 0, color == null ? null : color.getRGBAct(), 0);
        } catch (KrnException e) {
            e.printStackTrace();
        }
        Utils.setBlueSysColor(isColorActive(color) ? color : Constants.BLUE_SYS_COLOR);
    }

    /**
     * @param darkShadowSysColor
     *            the darkShadowSysColor to set
     */
    public void setDarkShadowSysColor(ColorAct color) {
        config.setDarkShadowSysColor(color);
        try {
            krn.setString(krnObj.id, krnObj.classId, ATTR_DARK_SHADOW_SYS_COLOR, 0, 0, color == null ? null : color.getRGBAct(),
                    0);
        } catch (KrnException e) {
            e.printStackTrace();
        }
        Utils.setDarkShadowSysColor(isColorActive(color) ? color : Constants.DARK_SHADOW_SYS_COLOR);
    }

    /**
     * @param midSysColor
     *            the midSysColor to set
     */
    public void setMidSysColor(ColorAct color) {
        config.setMidSysColor(color);
        try {
            krn.setString(krnObj.id, krnObj.classId, ATTR_MID_SYS_COLOR, 0, 0, color == null ? null : color.getRGBAct(), 0);
        } catch (KrnException e) {
            e.printStackTrace();
        }
        Utils.setMidSysColor(isColorActive(color) ? color : Constants.MID_SYS_COLOR);
    }

    /**
     * @param lightYellowColor
     *            the lightYellowColor to set
     */
    public void setLightYellowColor(ColorAct color) {
        config.setLightYellowColor(color);
        try {
            krn.setString(krnObj.id, krnObj.classId, ATTR_LIGHT_YELLOW_COLOR, 0, 0, color == null ? null : color.getRGBAct(), 0);
        } catch (KrnException e) {
            e.printStackTrace();
        }
        Utils.setLightYellowColor(isColorActive(color) ? color : Constants.LIGHT_YELLOW_COLOR);
    }

    /**
     * @param redColor
     *            the redColor to set
     */
    public void setRedColor(ColorAct color) {
        config.setRedColor(color);
        try {
            krn.setString(krnObj.id, krnObj.classId, ATTR_RED_COLOR, 0, 0, color == null ? null : color.getRGBAct(), 0);
        } catch (KrnException e) {
            e.printStackTrace();
        }
        Utils.setRedColor(isColorActive(color) ? color : Constants.RED_COLOR);
    }

    /**
     * @param lightRedColor
     *            the lightRedColor to set
     */
    public void setLightRedColor(ColorAct color) {
        config.setLightRedColor(color);
        try {
            krn.setString(krnObj.id, krnObj.classId, ATTR_LIGHT_RED_COLOR, 0, 0, color == null ? null : color.getRGBAct(), 0);
        } catch (KrnException e) {
            e.printStackTrace();
        }
        Utils.setLightRedColor(isColorActive(color) ? color : Constants.LIGHT_RED_COLOR);
    }

    /**
     * @param lightGreenColor
     *            the lightGreenColor to set
     */
    public void setLightGreenColor(ColorAct color) {
        config.setLightGreenColor(color);
        try {
            krn.setString(krnObj.id, krnObj.classId, ATTR_LIGHT_GREEN_COLOR, 0, 0, color == null ? null : color.getRGBAct(), 0);
        } catch (KrnException e) {
            e.printStackTrace();
        }
        Utils.setLightGreenColor(isColorActive(color) ? color : Constants.LIGHT_GREEN_COLOR);
    }

    /**
     * @param shadowYellowColor
     *            the shadowYellowColor to set
     */
    public void setShadowYellowColor(ColorAct color) {
        config.setShadowYellowColor(color);
        try {
            krn.setString(krnObj.id, krnObj.classId, ATTR_SHADOW_YELLOW_COLOR, 0, 0, color == null ? null : color.getRGBAct(), 0);
        } catch (KrnException e) {
            e.printStackTrace();
        }
        Utils.setShadowYellowColor(isColorActive(color) ? color : Constants.SHADOW_YELLOW_COLOR);
    }

    /**
     * @param sysColor
     *            the sysColor to set
     */
    public void setSysColor(ColorAct color) {
        config.setSysColor(color);
        try {
            krn.setString(krnObj.id, krnObj.classId, ATTR_SYS_COLOR, 0, 0, color == null ? null : color.getRGBAct(), 0);
        } catch (KrnException e) {
            e.printStackTrace();
        }
        Utils.setSysColor(isColorActive(color) ? color : Constants.SYS_COLOR);
    }

    /**
     * @param lightSysColor
     *            the lightSysColor to set
     */
    public void setLightSysColor(ColorAct color) {
        config.setLightSysColor(color);
        try {
            krn.setString(krnObj.id, krnObj.classId, ATTR_LIGHT_SYS_COLOR, 0, 0, color == null ? null : color.getRGBAct(), 0);
        } catch (KrnException e) {
            e.printStackTrace();
        }
        Utils.setLightSysColor(isColorActive(color) ? color : Constants.LIGHT_SYS_COLOR);
    }

    /**
     * @param defaultFontColor
     *            the defaultFontColor to set
     */
    public void setDefaultFontColor(ColorAct color) {
        config.setDefaultFontColor(color);
        try {
            krn.setString(krnObj.id, krnObj.classId, ATTR_DEFAULT_FONT_COLOR, 0, 0, color == null ? null : color.getRGBAct(), 0);
        } catch (KrnException e) {
            e.printStackTrace();
        }
        Utils.setDefaultFontColor(isColorActive(color) ? color : Constants.DEFAULT_FONT_COLOR);
    }

    /**
     * @param silverColor
     *            the silverColor to set
     */
    public void setSilverColor(ColorAct color) {
        config.setSilverColor(color);
        try {
            krn.setString(krnObj.id, krnObj.classId, ATTR_SILVER_COLOR, 0, 0, color == null ? null : color.getRGBAct(), 0);
        } catch (KrnException e) {
            e.printStackTrace();
        }
        Utils.setSilverColor(isColorActive(color) ? color : Constants.SILVER_COLOR);
    }

    /**
     * @param shadowsGreyColor
     *            the shadowsGreyColor to set
     */
    public void setShadowsGreyColor(ColorAct color) {
        config.setShadowsGreyColor(color);
        try {
            krn.setString(krnObj.id, krnObj.classId, ATTR_SHADOWS_GREY_COLOR, 0, 0, color == null ? null : color.getRGBAct(), 0);
        } catch (KrnException e) {
            e.printStackTrace();
        }
        Utils.setShadowsGreyColor(isColorActive(color) ? color : Constants.SHADOWS_GREY_COLOR);
    }

    /**
     * @param keywordColor
     *            the keywordColor to set
     */
    public void setKeywordColor(ColorAct color) {
        config.setKeywordColor(color);
        try {
            krn.setString(krnObj.id, krnObj.classId, ATTR_KEYWORD_COLOR, 0, 0, color == null ? null : color.getRGBAct(), 0);
        } catch (KrnException e) {
            e.printStackTrace();
        }
        Utils.setKeywordColor(isColorActive(color) ? color : Constants.KEYWORD_COLOR);
    }

    /**
     * @param variableColor
     *            the variableColor to set
     */
    public void setVariableColor(ColorAct color) {
        config.setVariableColor(color);
        try {
            krn.setString(krnObj.id, krnObj.classId, ATTR_VARIABLE_COLOR, 0, 0, color == null ? null : color.getRGBAct(), 0);
        } catch (KrnException e) {
            e.printStackTrace();
        }
        Utils.setVariableColor(isColorActive(color) ? color : Constants.VARIABLE_COLOR);
    }

    /**
     * @param clientVariableColor
     *            the clientVariableColor to set
     */
    public void setClientVariableColor(ColorAct color) {
        config.setClientVariableColor(color);
        try {
            krn.setString(krnObj.id, krnObj.classId, ATTR_CLIENT_VARIABLE_COLOR, 0, 0, color == null ? null : color.getRGBAct(),
                    0);
        } catch (KrnException e) {
            e.printStackTrace();
        }
        Utils.setClientVariableColor(isColorActive(color) ? color : Constants.CLIENT_VARIABLE_COLOR);
    }

    /**
     * @param commentColor
     *            the commentColor to set
     */
    public void setCommentColor(ColorAct color) {
        config.setCommentColor(color);
        try {
            krn.setString(krnObj.id, krnObj.classId, ATTR_COMMENT_COLOR, 0, 0, color == null ? null : color.getRGBAct(), 0);
        } catch (KrnException e) {
            e.printStackTrace();
        }
        Utils.setCommentColor(isColorActive(color) ? color : Constants.COMMENT_COLOR);
    }

    public void setObjectBrowserLimit(int limit) {
        config.setObjectBrowserLimit(limit);
        try {
            krn.setLong(krnObj.id, krnObj.classId, ATTR_OBJECT_BROWSER_LIMIT, 0, limit, 0);
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }

    public void setObjectBrowserLimitForClasses(Long classId, int limit) {
        try {
            if (config.getObjectBrowserLimitForClasses().get(classId) != null) {
                KrnObject o = clLimObj.get(classId);
                krn.setString(o.id, o.classId, "name", 0, 0, classId + "", 0);
                krn.setString(o.id, o.classId, "value", 0, 0, limit + "", 0);
            } else {
                KrnObject obj = krn.createObject(clsProp, 0);
                kz.tamur.rt.Utils.addObject(krnObj, ATTR_OBJECT_BROWSER_LIMIT_FOR_CLASSES, obj);
                krn.setString(obj.id, obj.classId, "name", 0, 0, classId + "", 0);
                krn.setString(obj.id, obj.classId, "value", 0, 0, limit + "", 0);
                clLimObj.put(classId, obj);
            }
            config.getObjectBrowserLimitForClasses().put(classId, limit);
        } catch (KrnException e) {
            e.printStackTrace();
        }

    }

    public void cleanObjectBrowserLimitForClasses() {
        try {
            for (Entry<Long, KrnObject> header : clLimObj.entrySet()) {
                krn.deleteObject(header.getValue(), 0);
            }
        } catch (KrnException e) {
            e.printStackTrace();
        }
        clLimObj = new HashMap<Long, KrnObject>();
    }

    public void setObjectBrowserLimit(boolean limit) {
        config.setObjectBrowserLimit(limit);
        try {
            krn.setLong(krnObj.id, krnObj.classId, ATTR_IS_OBJECT_BROWSER_LIMIT, 0, Utils.toLong(limit), 0);
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }

    public void setObjectBrowserLimitForClasses(boolean limit) {
        config.setObjectBrowserLimitForClasses(limit);
        try {
            krn.setLong(krnObj.id, krnObj.classId, ATTR_IS_OBJECT_BROWSER_LIMIT_FOR_CLASSES, 0, Utils.toLong(limit), 0);
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }

    /**
     * Получить всю конфигурацию.
     * 
     * @return конфигурация
     */
    public Config getConfig() {
        return config;
    }

    public void updateSysVar() {
        if (isColorActive(config.getColorMain())) {
            Utils.setMainColor(config.getColorMain());
        }
        if (isColorActive(config.getBlueSysColor())) {
            Utils.setBlueSysColor(config.getBlueSysColor());
        }
        if (isColorActive(config.getDarkShadowSysColor())) {
            Utils.setDarkShadowSysColor(config.getDarkShadowSysColor());
        }
        if (isColorActive(config.getMidSysColor())) {
            Utils.setMidSysColor(config.getMidSysColor());
        }
        if (isColorActive(config.getLightYellowColor())) {
            Utils.setLightYellowColor(config.getLightYellowColor());
        }
        if (isColorActive(config.getRedColor())) {
            Utils.setRedColor(config.getRedColor());
        }
        if (isColorActive(config.getLightRedColor())) {
            Utils.setLightRedColor(config.getLightRedColor());
        }
        if (isColorActive(config.getLightGreenColor())) {
            Utils.setLightGreenColor(config.getLightGreenColor());
        }
        if (isColorActive(config.getShadowYellowColor())) {
            Utils.setShadowYellowColor(config.getShadowYellowColor());
        }
        if (isColorActive(config.getSysColor())) {
            Utils.setSysColor(config.getSysColor());
        }
        if (isColorActive(config.getLightSysColor())) {
            Utils.setLightSysColor(config.getLightSysColor());
        }
        if (isColorActive(config.getDefaultFontColor())) {
            Utils.setDefaultFontColor(config.getDefaultFontColor());
        }
        if (isColorActive(config.getSilverColor())) {
            Utils.setSilverColor(config.getSilverColor());
        }
        if (isColorActive(config.getShadowsGreyColor())) {
            Utils.setShadowsGreyColor(config.getShadowsGreyColor());
        }
        if (isColorActive(config.getKeywordColor())) {
            Utils.setKeywordColor(config.getKeywordColor());
        }
        if (isColorActive(config.getVariableColor())) {
            Utils.setVariableColor(config.getVariableColor());
        }
        if (isColorActive(config.getClientVariableColor())) {
            Utils.setClientVariableColor(config.getClientVariableColor());
        }
        if (isColorActive(config.getCommentColor())) {
            Utils.setCommentColor(config.getCommentColor());
        }
    }
}
