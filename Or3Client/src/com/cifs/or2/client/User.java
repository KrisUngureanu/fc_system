package com.cifs.or2.client;

import static kz.tamur.comps.Constants.ATTR_BLUE_SYS_COLOR;
import static kz.tamur.comps.Constants.ATTR_CLIENT_VARIABLE_COLOR;
import static kz.tamur.comps.Constants.ATTR_COLOR_BACK_TAB_TITLE;
import static kz.tamur.comps.Constants.ATTR_COLOR_FONT_BACK_TAB_TITLE;
import static kz.tamur.comps.Constants.ATTR_COLOR_FONT_TAB_TITLE;
import static kz.tamur.comps.Constants.ATTR_COLOR_HEADER_TABLE;
import static kz.tamur.comps.Constants.ATTR_COLOR_MAIN;
import static kz.tamur.comps.Constants.ATTR_COLOR_TAB_TITLE;
import static kz.tamur.comps.Constants.ATTR_COMMENT_COLOR;
import static kz.tamur.comps.Constants.ATTR_DARK_SHADOW_SYS_COLOR;
import static kz.tamur.comps.Constants.ATTR_DEFAULT_FONT_COLOR;
import static kz.tamur.comps.Constants.ATTR_GRADIENT_CONTROL_PANEL;
import static kz.tamur.comps.Constants.ATTR_GRADIENT_FIELD_NO_FLC;
import static kz.tamur.comps.Constants.ATTR_GRADIENT_MAIN_FRAME;
import static kz.tamur.comps.Constants.ATTR_GRADIENT_MENU_PANEL;
import static kz.tamur.comps.Constants.ATTR_HISTORY_FLT;
import static kz.tamur.comps.Constants.ATTR_FLT_HISTORY;
import static kz.tamur.comps.Constants.ATTR_HISTORY_IFC;
import static kz.tamur.comps.Constants.ATTR_IFC_HISTORY;
import static kz.tamur.comps.Constants.ATTR_HISTORY_RPT;
import static kz.tamur.comps.Constants.ATTR_RPT_HISTORY;
import static kz.tamur.comps.Constants.ATTR_HISTORY_SRV;
import static kz.tamur.comps.Constants.ATTR_SRV_HISTORY;
import static kz.tamur.comps.Constants.ATTR_KEYWORD_COLOR;
import static kz.tamur.comps.Constants.ATTR_LIGHT_GREEN_COLOR;
import static kz.tamur.comps.Constants.ATTR_LIGHT_RED_COLOR;
import static kz.tamur.comps.Constants.ATTR_LIGHT_SYS_COLOR;
import static kz.tamur.comps.Constants.ATTR_LIGHT_YELLOW_COLOR;
import static kz.tamur.comps.Constants.ATTR_MID_SYS_COLOR;
import static kz.tamur.comps.Constants.ATTR_RED_COLOR;
import static kz.tamur.comps.Constants.ATTR_SHADOWS_GREY_COLOR;
import static kz.tamur.comps.Constants.ATTR_SHADOW_YELLOW_COLOR;
import static kz.tamur.comps.Constants.ATTR_SILVER_COLOR;
import static kz.tamur.comps.Constants.ATTR_SYS_COLOR;
import static kz.tamur.comps.Constants.ATTR_TRANSPARENT_BACK_TAB_TITLE;
import static kz.tamur.comps.Constants.ATTR_TRANSPARENT_CELL_TABLE;
import static kz.tamur.comps.Constants.ATTR_TRANSPARENT_DIALOG;
import static kz.tamur.comps.Constants.ATTR_TRANSPARENT_MAIN;
import static kz.tamur.comps.Constants.ATTR_TRANSPARENT_SELECTED_TAB_TITLE;
import static kz.tamur.comps.Constants.ATTR_VARIABLE_COLOR;
import static kz.tamur.comps.Constants.NAME_CLASS_CONFIG_LOCAL;
import static kz.tamur.comps.Constants.NAME_CLASS_CONFIG_OBJECT;
import static kz.tamur.comps.Constants.SELECTED;
import static kz.tamur.rt.GlobalConfig.decodeColor;
import static kz.tamur.rt.GlobalConfig.decodeColorAndActiv;
import static kz.tamur.rt.Utils.isColorActive;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kz.tamur.comps.Constants;
import kz.tamur.comps.models.GradientColor;
import kz.tamur.guidesigner.users.Or3RightsNode;
import kz.tamur.lang.SharedMemoryOp;
import kz.tamur.ods.Value;
import kz.tamur.or3.util.SystemAction;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.rt.Config;
import kz.tamur.rt.ConfigObject;
import kz.tamur.rt.HistoryWithDate;
import kz.tamur.rt.Utils;
import kz.tamur.util.Funcs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import com.cifs.or2.client.util.AttrRequestBuilder;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.ObjectValue;

public class User {
    
    private Kernel krn;
    private Filter currentFilter_;
    public KrnObject object;
    KrnObject ifcLang;
    KrnObject dataLang;
    KrnObject ifc;
    KrnObject base;
    KrnObject configObj;
    String sign = "";
    String name = "";
    boolean admin = false;
    boolean developer = false;
    boolean onlyECP = false;
    String baseCode = "";
    boolean isEditor = false;
    boolean showTooltip = false;
    boolean useNoteSound = false;
    boolean instantECP = false;
    private String[] scopeUids;
    private Map<String, String> ldMap;
    private boolean isLdMapCalculated;
    
    private Date lastSuccessfullTime;
    private Date lastUnsuccessfullTime;
    
    public Map<String, String> getLdMap() {
		return ldMap;
	}

	public void setLdMap(Map<String, String> ldMap) {
		this.ldMap = ldMap;
	}

	public boolean isLdMapCalculated() {
		return isLdMapCalculated;
	}

	public void setLdMapCalculated(boolean isLdMapCalculated) {
		this.isLdMapCalculated = isLdMapCalculated;
	}

	public String[] getScopeUids() {
		return scopeUids;
	}

	private Set<Long> readOnlyItems = new HashSet<Long>();
    private Set<Long> readWriteItems = new HashSet<Long>();
    List<KrnObject> help;
    List<HelpFile> helpFiles;
    boolean helpLoaded = false;
    private Element or3Rights;
    private boolean hasOr3Rights = true;

    protected static KrnObject krnObj;
    
    /** флаг отображения монитора задач у пользователя */
    private boolean isMonitor;
    /** Флаг отображения панели задач у пользоватля в WEB интерфейсе. */
    private boolean isToolBar;
    
    public Config config = new Config();
    private ConfigObject configByUUIDs;
    private static KrnClass configCls;
    private static KrnClass configUUIDCls;
    public boolean isDesignerRun;
    
    private List<Object> forDelHistorySrv = new ArrayList<Object>();
    private List<Object> forDelHistoryIfc = new ArrayList<Object>();
    private List<Object> forDelHistoryFlt = new ArrayList<Object>();
    private List<Object> forDelHistoryRpt = new ArrayList<Object>();
    
    private long[] parentIds = {};
    
    private List<String> userRoleUIDs = new ArrayList<String>();
    private Map<Long, List<String>> userRoles = new HashMap<Long, List<String>>();
    
    private static final Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + User.class.getName());

    private boolean isDbReadOnly = true;

    public static final boolean USE_OLD_USER_RIGHTS = "true".equals(System.getProperty("useOldUserRights"));

    public User(String name) {
    	this.name = name;
    }
    
    public User(KrnObject obj, Kernel krn, String typeClient) throws KrnException {
        this.krn = krn;
        isDesignerRun = Constants.CLIENT_TYPE_DESIGNER.equals(typeClient);
        isDbReadOnly = krn.isDbReadOnly();
        
        object = obj;
        help = new ArrayList<KrnObject>();
        helpFiles = new ArrayList<HelpFile>();
        
        or3Rights = new Element(Or3RightsNode.OR3_RIGHTS);
        List<Long> helpIds = new ArrayList<Long>();
        try {
            configCls = krn.getClassByName(NAME_CLASS_CONFIG_LOCAL);
        } catch (Exception e) {
            log.warn("Не найден класс " + NAME_CLASS_CONFIG_LOCAL);
        }

        try {
            configUUIDCls = krn.getClassByName(NAME_CLASS_CONFIG_OBJECT);
        } catch (Exception e) {
        	log.warn("Не найден класс " + NAME_CLASS_CONFIG_OBJECT);
        }

        try {
            KrnClass baseCls = krn.getClassByName("Структура баз");
            AttrRequestBuilder arb = new AttrRequestBuilder(Kernel.SC_USER, krn).add("interface").add("interface language")
                    .add("data language").add("name").add("sign").add("admin").add("editor")
                    .add("base", new AttrRequestBuilder(baseCls, krn).add("код")).add("developer").add("onlyECP")
                    .add("parent").add("showTooltip").add("useNoteSound").add("instantECP").add("scope").add("дата неудачного входа").add("дата предыдущего входа");
                try {
                // конфигурация пользователя
                arb.add("config",
                        new AttrRequestBuilder(configCls, krn).add(ATTR_GRADIENT_MAIN_FRAME)
                                .add(ATTR_GRADIENT_CONTROL_PANEL).add(ATTR_GRADIENT_MENU_PANEL)
                                .add(ATTR_TRANSPARENT_MAIN).add(ATTR_TRANSPARENT_DIALOG)
                                .add(ATTR_COLOR_MAIN).add(ATTR_TRANSPARENT_CELL_TABLE)
                                .add(ATTR_COLOR_HEADER_TABLE).add(ATTR_COLOR_TAB_TITLE)
                                .add(ATTR_COLOR_BACK_TAB_TITLE).add(ATTR_COLOR_FONT_TAB_TITLE)
                                .add(ATTR_COLOR_FONT_BACK_TAB_TITLE).add(ATTR_TRANSPARENT_BACK_TAB_TITLE)
                                .add(ATTR_TRANSPARENT_SELECTED_TAB_TITLE).add(ATTR_GRADIENT_FIELD_NO_FLC).add(ATTR_BLUE_SYS_COLOR).add(ATTR_DARK_SHADOW_SYS_COLOR)
                                .add(ATTR_MID_SYS_COLOR).add(ATTR_LIGHT_YELLOW_COLOR).add(ATTR_RED_COLOR).add(ATTR_LIGHT_RED_COLOR)
                                .add(ATTR_LIGHT_GREEN_COLOR).add(ATTR_SHADOW_YELLOW_COLOR).add(ATTR_SYS_COLOR).add(ATTR_LIGHT_SYS_COLOR)
                                .add(ATTR_DEFAULT_FONT_COLOR).add(ATTR_SILVER_COLOR).add(ATTR_SHADOWS_GREY_COLOR).add(ATTR_KEYWORD_COLOR)
                                .add(ATTR_VARIABLE_COLOR).add(ATTR_CLIENT_VARIABLE_COLOR).add(ATTR_COMMENT_COLOR).add("isMonitor").add("isToolBar")
                                .add("configByUUIDs").add("historySrv").add("historyIfc").add("historyFlt").add("historyRpt").add("srvHistory")
                                .add("ifcHistory").add("fltHistory").add("rptHistory"));
                }catch(NullPointerException e) {
                }
            long[] objIds = { object.id };
            Object[] row = krn.getObjects(objIds, arb.build(), 0).get(0);

            ifc = (KrnObject) arb.getValue("interface", row);

            ifcLang = (KrnObject) arb.getValue("interface language", row);

            if (ifcLang == null) {
                error("Данному пользователю не назначен язык интерфейса");
            }

            dataLang = (KrnObject) arb.getValue("data language", row);

            if (dataLang == null) {
                error("Данному пользователю не назначен язык данных");
            }

            name = (String) arb.getValue("name", row);
            sign = (String) arb.getValue("sign", row);
            admin = arb.getBooleanValue("admin", row);
            developer = arb.getBooleanValue("developer", row);
            showTooltip = arb.getBooleanValue("showTooltip", row);
            useNoteSound = arb.getBooleanValue("useNoteSound", row);
            instantECP = arb.getBooleanValue("instantECP", row);
            
            List<Value> userScope = (List<Value>) arb.getValue("scope", row);
            scopeUids = new String[userScope.size()];
            for(int i=0; i<userScope.size(); i++) {
            	scopeUids[i] = ((KrnObject) userScope.get(i).value).uid;
            }
            
            if ("sys_admin".equals(name)) {
                developer = true;
            }

            isEditor = arb.getBooleanValue("editor", row);
            onlyECP = arb.getBooleanValue("onlyECP", row);
            base = (KrnObject) arb.getValue("base", row);

            lastSuccessfullTime = Funcs.convertTime(arb.getTimeValue("дата предыдущего входа", row));
            lastUnsuccessfullTime = Funcs.convertTime(arb.getTimeValue("дата неудачного входа", row));
            
            KrnAttribute timeAttr = krn.getAttributeByName(Kernel.SC_USER, "дата предыдущего входа");
            if (timeAttr != null) {
                krn.setTime(obj.id,	timeAttr.id, 0, new Date(), 0);
            }
            
            if (base != null) {
                krn.selectBases(new long[] { (int) base.id });
            } else {
                error("Данному пользователю не назначен уровень доступа");
            }

            baseCode = (String) arb.getValue("base.код", row);

            isMonitor = true;
            isToolBar = true;
            // заполучить конфигурацию пользователя
            configObj = arb.getObjectValue("config", row);

            if (configObj == null && configCls != null) {
                configObj = krn.createObject(configCls, 0);
                krn.setObject(object.id, object.classId, "config", 0, configObj.id, 0, false);
            }
            final String con = "config.";
            config.setGradientMainFrame(new GradientColor(arb.getStringValue(con+ATTR_GRADIENT_MAIN_FRAME, row)));
            config.setGradientControlPanel(new GradientColor(arb.getStringValue(con+ATTR_GRADIENT_CONTROL_PANEL, row)));
            config.setGradientMenuPanel(new GradientColor(arb.getStringValue(con+ATTR_GRADIENT_MENU_PANEL, row)));
            config.setTransparentMain(arb.getLongValue(con+ATTR_TRANSPARENT_MAIN, row, 0) == 1);
            config.setTransparentDialog(arb.getLongValue(con+ATTR_TRANSPARENT_DIALOG, row, 1) == 1);
            config.setColorMain(decodeColorAndActiv(arb.getStringValue(con+ATTR_COLOR_MAIN, row)));
            config.setTransparentCellTable((int) arb.getLongValue(con+ATTR_TRANSPARENT_CELL_TABLE, row, 0));
            config.setColorHeaderTable(decodeColor(arb.getStringValue(con+ATTR_COLOR_HEADER_TABLE, row)));
            config.setColorTabTitle(decodeColor(arb.getStringValue(con+ATTR_COLOR_TAB_TITLE, row)));
            config.setColorBackTabTitle(decodeColor(arb.getStringValue(con+ATTR_COLOR_BACK_TAB_TITLE, row)));
            config.setColorFontTabTitle(decodeColor(arb.getStringValue(con+ATTR_COLOR_FONT_TAB_TITLE, row)));
            config.setColorFontBackTabTitle(decodeColor(arb.getStringValue(con+ATTR_COLOR_FONT_BACK_TAB_TITLE, row)));
            config.setTransparentBackTabTitle((int) arb.getLongValue(con+ATTR_TRANSPARENT_BACK_TAB_TITLE, row, 0));
            config.setTransparentSelectedTabTitle((int) arb.getLongValue(con+ATTR_TRANSPARENT_SELECTED_TAB_TITLE, row, 0));
            config.setGradientFieldNOFLC(new GradientColor(arb.getStringValue(con+ATTR_GRADIENT_FIELD_NO_FLC, row)));
            config.setBlueSysColor(decodeColorAndActiv(arb.getStringValue(con+ATTR_BLUE_SYS_COLOR, row)));
            config.setDarkShadowSysColor(decodeColorAndActiv(arb.getStringValue(con+ATTR_DARK_SHADOW_SYS_COLOR, row)));
            config.setMidSysColor(decodeColorAndActiv(arb.getStringValue(con+ATTR_MID_SYS_COLOR, row)));
            config.setLightYellowColor(decodeColorAndActiv(arb.getStringValue(con+ATTR_LIGHT_YELLOW_COLOR, row)));
            config.setRedColor(decodeColorAndActiv(arb.getStringValue(con+ATTR_RED_COLOR, row)));
            config.setLightRedColor(decodeColorAndActiv(arb.getStringValue(con+ATTR_LIGHT_RED_COLOR, row)));
            config.setLightGreenColor(decodeColorAndActiv(arb.getStringValue(con+ATTR_LIGHT_GREEN_COLOR, row)));
            config.setShadowYellowColor(decodeColorAndActiv(arb.getStringValue(con+ATTR_SHADOW_YELLOW_COLOR, row)));
            config.setSysColor(decodeColorAndActiv(arb.getStringValue(con+ATTR_SYS_COLOR, row)));
            config.setLightSysColor(decodeColorAndActiv(arb.getStringValue(con+ATTR_LIGHT_SYS_COLOR, row)));
            config.setDefaultFontColor(decodeColorAndActiv(arb.getStringValue(con+ATTR_DEFAULT_FONT_COLOR, row)));
            config.setSilverColor(decodeColorAndActiv(arb.getStringValue(con+ATTR_SILVER_COLOR, row)));
            config.setShadowsGreyColor(decodeColorAndActiv(arb.getStringValue(con+ATTR_SHADOWS_GREY_COLOR, row)));
            config.setKeywordColor(decodeColorAndActiv(arb.getStringValue(con+ATTR_KEYWORD_COLOR, row)));
            config.setVariableColor(decodeColorAndActiv(arb.getStringValue(con+ATTR_VARIABLE_COLOR, row)));
            config.setClientVariableColor(decodeColorAndActiv(arb.getStringValue(con+ATTR_CLIENT_VARIABLE_COLOR, row)));
            config.setCommentColor(decodeColorAndActiv(arb.getStringValue(con+ATTR_COMMENT_COLOR, row)));
            config.setIsMonitor((int) arb.getLongValue("config.isMonitor", row, 1));
            config.setIsToolBar((int) arb.getLongValue("config.isToolBar", row, 1));

            isMonitor = config.getIsMonitor() == SELECTED;
            isToolBar = config.getIsToolBar() == SELECTED;
            
            
            // Список UUID-дов со свойствами им принадлежащими
            long[] uuidsIds = {};
            List<Value> list = (List<Value>) arb.getValue("config.configByUUIDs", row); // FIXME не работает, доработать обработку вложенных множественных атрибутов
            if (list != null) {
            	uuidsIds = new long[list.size()];
                for (int i = 0; i < list.size(); i++) {
                	uuidsIds[i] = ((KrnObject) list.get(i).value).id;
                }
            }
            
            readFromDb(ATTR_SRV_HISTORY);
            readFromDb(ATTR_IFC_HISTORY);
            readFromDb(ATTR_FLT_HISTORY);
            readFromDb(ATTR_RPT_HISTORY);
            
            // Создание нового объекта конфигурации пользователя
            configByUUIDs = ConfigObject.instance(krn, configObj);

            if (uuidsIds.length > 0) {
                // Определить сборку запроса для извлечения концигурации UUID-дов из вышеполученных id объектов
                AttrRequestBuilder arbConfig = new AttrRequestBuilder(configUUIDCls, krn).add("uuid").add("properties");
                // получить объекты
                List<Object[]> uuidRows = krn.getObjects(uuidsIds, arbConfig.build(), 0);
                // парсинг полученных объектов
                for (Object[] uuidRow : uuidRows) {
                    // в нулевом элементе каждого элемента списка содержится сам KrnObject
                    KrnObject uuidObj = (KrnObject) uuidRow[0];
                    // UUID объекта
                    String uuid = arbConfig.getStringValue("uuid", uuidRow);
                    // его свойства
                    List<Value> props = (List<Value>) arbConfig.getValue("properties", uuidRow);

                    long[] propsIds = {};
                    if (props != null) {
                        propsIds = new long[props.size()];
                        for (int i = 0; i < props.size(); i++) {
                            propsIds[i] = ((KrnObject) props.get(i).value).id;
                        }
                        // Определить сборку запроса для извлечения отдельных свойств по найденным объектам для конкретных UUID-дов
                        AttrRequestBuilder arbProp = new AttrRequestBuilder(krn.getClassByName("Property"), krn).add("name")
                                .add("value");
                        // получить объекты
                        List<Object[]> propsRows = krn.getObjects(propsIds, arbProp.build(), 0);
                        // парсинг полученных объектов
                        for (Object[] propRow : propsRows) {

                            String name = arbProp.getStringValue("name", propRow);
                            String value = arbProp.getStringValue("value", propRow);
                            // Запомнить полученное свойство
                            // в нулевом элементе каждого элемента списка содержится сам KrnObject
                            configByUUIDs.setProperty(uuid, name, value, (KrnObject) propRow[0], uuidObj);
                        }
                    }

                }
            }
            List<Value> parents = (List<Value>) arb.getValue("parent", row);
            
            List<Long> allParentIds = new ArrayList<>();
            long[] parentIds = {};
            if (parents != null) {
                List<Long> newParentIds = new ArrayList<>();
                for (int i = 0; i < parents.size(); i++) {
                    long id = ((KrnObject) parents.get(i).value).id;
                    if (!allParentIds.contains(id)) {
                    	allParentIds.add(id);
                    	newParentIds.add(id);
                    }
                }
                
                int maxDepth = 10;
                while (newParentIds.size() > 0 && maxDepth-- > 0) {
                    parentIds = new long[newParentIds.size()];
                    for (int i = 0; i < newParentIds.size(); i++)
                        parentIds[i] = newParentIds.get(i);

                    newParentIds = new ArrayList<>();
                    ObjectValue[] ps = krn.getObjectValues(parentIds, Kernel.SC_USER_FOLDER.id, "parent", 0);
                    for (int i = 0; i < ps.length; i++) {
                    	if (ps[i].value != null && !allParentIds.contains(ps[i].value.id)) {
                    		newParentIds.add(ps[i].value.id);
                    		allParentIds.add(ps[i].value.id);
                    	}
                    }
                }
            }            
            
            if (allParentIds.size() > 0) {
                parentIds = new long[allParentIds.size()];
                this.parentIds = new long[allParentIds.size()];
                for (int i = 0; i < allParentIds.size(); i++) {
                    parentIds[i] = this.parentIds[i] = allParentIds.get(i);
                }
                
                AttrRequestBuilder arb2 = new AttrRequestBuilder(Kernel.SC_USER_FOLDER, krn).add("name").add("helps").add("or3rights");
                
                if (USE_OLD_USER_RIGHTS)
                	arb2.add("editor").add("hyperMenu");
                
                // получить объекты
                List<Object[]> rows2 = krn.getObjects(parentIds, arb2.build(), 0);
                // парсинг полученных объектов
                for (Object[] row2 : rows2) {
                    KrnObject pobj = arb2.getObject(row2);
                    userRoleUIDs.add(pobj.uid);

                    List<Value> helps = (List<Value>) arb2.getValue("helps", row2);
                    if (helps != null) {
	                    for (int i = 0; i < helps.size(); i++) {
	                    	KrnObject helpObj = (KrnObject) helps.get(i).value;
	                        if (!helpIds.contains(helpObj.id)) {
	                            helpIds.add(helpObj.id);
	                        }
	                    }
                    }
                    
                    byte[] b = (byte[]) arb2.getValue("or3rights", row2);
                    try {
                        if (b != null && b.length > 0) {
                            SAXBuilder builder = new SAXBuilder();
                            Document doc = builder.build(new ByteArrayInputStream(b), "UTF-8");
                            Or3RightsNode.merge(or3Rights, doc.getRootElement());
                        }
                    } catch (Exception ee) {
                    }
                    
                    if (USE_OLD_USER_RIGHTS) {
	                    boolean isEdit = arb2.getBooleanValue("editor", row2);
	
	                    List<Value> menus = (List<Value>) arb2.getValue("hyperMenu", row2);
	                    if (menus != null) {
		                    for (int i = 0; i < menus.size(); i++) {
		                    	KrnObject menuObj = (KrnObject) menus.get(i).value;
		                        if (isEdit) {
			                        if (!readWriteItems.contains(menuObj.id))
			                        	readWriteItems.add(menuObj.id);
		                        } else {
			                        if (!readOnlyItems.contains(menuObj.id))
			                        	readOnlyItems.add(menuObj.id);
		                        }
		                    }
	                    }
                    }
                }
            }

            if (!USE_OLD_USER_RIGHTS) {
            	List<Long> dicts = krn.getUserSubjects(SystemAction.ACTION_EDIT_DICTIONARY, object.id);
	            List<Long> archs = krn.getUserSubjects(SystemAction.ACTION_VIEW_ARCHIVE, object.id);
	            
	            long[] hiperIds = {};
	            if (archs != null && dicts != null && archs.size() + dicts.size() > 0) {
	            	hiperIds = new long[archs.size() + dicts.size()];
	            	int i = 0;
		            for (long arch : archs) {
		            	hiperIds[i++] = arch;
		            }
		            for (long dict : dicts) {
		            	hiperIds[i++] = dict;
		            }
		            
	                arb = new AttrRequestBuilder(krn.getClassByName("HiperTree"), krn)
		                	.add("parent", new AttrRequestBuilder(krn.getClassByName("HiperTree"), krn)
		                	.add("parent", new AttrRequestBuilder(krn.getClassByName("HiperTree"), krn)
		                	.add("parent", new AttrRequestBuilder(krn.getClassByName("HiperTree"), krn)
		                	.add("parent", new AttrRequestBuilder(krn.getClassByName("HiperTree"), krn)
		                	.add("parent", new AttrRequestBuilder(krn.getClassByName("HiperTree"), krn)
		                	.add("parent", new AttrRequestBuilder(krn.getClassByName("HiperTree"), krn)
		                	.add("parent", new AttrRequestBuilder(krn.getClassByName("HiperTree"), krn)
		                	.add("parent", new AttrRequestBuilder(krn.getClassByName("HiperTree"), krn)
		                	.add("parent", new AttrRequestBuilder(krn.getClassByName("HiperTree"), krn)
		                	.add("parent"))))))))));
	
	                List<Object[]> prows = krn.getObjects(hiperIds, arb.build(), 0);
	
	                for (Object[] prow : prows) {
	                	KrnObject arch = (KrnObject)prow[0];
	                	Set<Long> hipers = (dicts.contains(arch.id)) ? readWriteItems : readOnlyItems;
	                	
	            		if (!hipers.contains(arch.id)) hipers.add(arch.id);
	
	                	KrnObject parent = arb.getObjectValue("parent", prow);
	                	if (parent != null) {
	                		if (!hipers.contains(parent.id)) hipers.add(parent.id);
	                    	parent = arb.getObjectValue("parent.parent", prow);
	                    	if (parent != null) {
		                		if (!hipers.contains(parent.id)) hipers.add(parent.id);
	                        	parent = arb.getObjectValue("parent.parent.parent", prow);
	                        	if (parent != null) {
	    	                		if (!hipers.contains(parent.id)) hipers.add(parent.id);
	                            	parent = arb.getObjectValue("parent.parent.parent.parent", prow);
	                            	if (parent != null) {
	        	                		if (!hipers.contains(parent.id)) hipers.add(parent.id);
		                            	parent = arb.getObjectValue("parent.parent.parent.parent.parent", prow);
		                            	if (parent != null) {
		        	                		if (!hipers.contains(parent.id)) hipers.add(parent.id);
			                            	parent = arb.getObjectValue("parent.parent.parent.parent.parent.parent", prow);
			                            	if (parent != null) {
			        	                		if (!hipers.contains(parent.id)) hipers.add(parent.id);
				                            	parent = arb.getObjectValue("parent.parent.parent.parent.parent.parent.parent", prow);
				                            	if (parent != null) {
				        	                		if (!hipers.contains(parent.id)) hipers.add(parent.id);
					                            	parent = arb.getObjectValue("parent.parent.parent.parent.parent.parent.parent.parent", prow);
					                            	if (parent != null) {
					        	                		if (!hipers.contains(parent.id)) hipers.add(parent.id);
						                            	parent = arb.getObjectValue("parent.parent.parent.parent.parent.parent.parent.parent.parent", prow);
						                            	if (parent != null) {
						        	                		if (!hipers.contains(parent.id)) hipers.add(parent.id);
							                            	parent = arb.getObjectValue("parent.parent.parent.parent.parent.parent.parent.parent.parent.parent", prow);
							                            	if (parent != null) {
							        	                		if (!hipers.contains(parent.id)) hipers.add(parent.id);
							                            	}
						                            	}
					                            	}
				                            	}
			                            	}
		                            	}
	                            	}
	                        	}
	                    	}
	                	}
	            	}
	            } else if (archs == null && dicts == null) {
	            	// Все архивы и справочники для sys_admin и sys
		            KrnObject[] objs = krn.getClassObjects(krn.getClassByName("HiperTree"), 0);
		            for (KrnObject arch : objs) {
	                    readOnlyItems.add(arch.id);
	                    readWriteItems.add(arch.id);
		            }
	            }
            }
            
            if (helpIds.size() > 0) {
            	long[] hids = new long[helpIds.size()];
                for (int i = 0; i < helpIds.size(); i++)
                	hids[i] = helpIds.get(i);
                
                KrnClass noteCls = krn.getClassByName("Note");
                arb = new AttrRequestBuilder(noteCls, krn);
                
                for (KrnObject lang : Kernel.LANGUAGES)
                	arb.add("title", lang.id);

                if (krn.getAttributeByName(noteCls, "msDoc") != null)
                	arb.add("msDoc", new AttrRequestBuilder(krn.getClassByName("MSDoc"), krn).add("filename").add("file"));
        
                List<Object[]> prows = krn.getObjects(hids, arb.build(), 0);

		        for (Object[] prow : prows) {
		            KrnObject helpObj = arb.getObject(prow);
		            
		            Map<Long, String> titles = new HashMap<Long, String>();
	                for (KrnObject lang : Kernel.LANGUAGES) {
	                	String title = arb.getStringValue("title", lang.id, prow);
	                	titles.put(lang.id, title);
	                }

		            String fileName = arb.getStringValue("msDoc.filename", prow);

                	if (fileName != null) {
    		            byte[] file = (byte[]) arb.getValue("msDoc.file", prow);
                		helpFiles.add(new HelpFile(file, fileName, titles));
                	} else {
                		help.add(helpObj);
                	}
		        }
            }
            
            SharedMemoryOp.removeUserMaps(obj.id);

        } catch (Exception e) {
        	log.error(e, e);
        }
    }
    
    public void setShowTooltip(boolean showTooltip) {
		this.showTooltip = showTooltip;
	}
    
    public boolean isShowTooltip() {
    	return showTooltip;
    }
    
    public void setUseNoteSound(boolean useNoteSound) {
    	this.useNoteSound = useNoteSound;
    }
    
    public boolean useNoteSound() {
    	return useNoteSound;
    }

    public void setInstantECP(boolean instantECP) {
		this.instantECP = instantECP;
	}
    
    public boolean isInstantECP() {
    	return instantECP;
    }
    
    public Set<Long> getReadOnlyItems() {
        return readOnlyItems;
    }

    public Set<Long> getReadWriteItems() {
        return readWriteItems;
    }

    public Filter getCurrentFilter() {
        return currentFilter_;
    }

    public void setCurrentFilter(Filter filter) {
        currentFilter_ = filter;
    }

    public KrnObject[] getAccess() {
        return new KrnObject[0];
    }

    public boolean isAdmin() {
        return admin;
    }

    public boolean isDeveloper() {
        return developer;
    }

    public boolean isOnlyECP() {
        return onlyECP;
    }

    public KrnObject getBase() {
        return base;
    }

    public KrnObject getDataLanguage() {
        return dataLang;
    }

    public void setDataLanguage(KrnObject dataLang) {
        this.dataLang = dataLang;
    }

    public KrnObject getIfcLang() {
        return ifcLang;
    }

    public void setIfcLang(KrnObject ifcLang) {
        this.ifcLang = ifcLang;
    }

    public KrnObject getIfc() {
        return ifc;
    }

    public String getUserSign() {
        return sign;
    }

    public String getBaseCode() {
        return baseCode;
    }

    public boolean hasRight(String right) {
    	if (isDbReadOnly && Or3RightsNode.isImportantRight(right)) return false;
    	
        if (hasOr3Rights)
            return Or3RightsNode.hasRight(or3Rights, right);
        else
            return true;
    }

    public void setHasOr3Rights(boolean hasOr3Rights) {
        this.hasOr3Rights = hasOr3Rights;
    }

    private void error(String msg) throws KrnException {
        throw new KrnException(0, msg);
    }

    public KrnObject getObject() {
        return object;
    }

    public String getName() {
        return name;
    }

    public boolean isEditor() {
        return isEditor;
    }

    public List<KrnObject> getHelp() {
        return help;
    }

    public List<HelpFile> getHelpFiles() {
        return helpFiles;
    }

    /**
     * Отображать ли монитор задач для пользователя.
     */
    public boolean isMonitor() {
        return isMonitor;
    }
    
    /**
     * Отображать ли панель инструментов в WEB интерфейсе для пользователя
     */
    public boolean isToolBar() {
        return isToolBar;
    }
    
    public void updateConfigUser() {
        // системные переменные
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

    /**
     * @return the configObj
     */
    public KrnObject getConfigObj() {
        return configObj;
    }
    
    private void readFromDb(String attr_name) {
    	try {
        	byte[] msg = krn.getBlob(configObj, attr_name, 0, 0, 0);
        	if (msg.length > 0) {
        		SAXBuilder builder = new SAXBuilder();
        		builder.setValidation(false);
        		Element xml = null;
        		try {
        			xml = builder.build(new ByteArrayInputStream(msg)).getRootElement();
        		} catch (JDOMException e) {
        			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        		} catch (IOException e) {
        			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        		}
        		//messages
        		List msgs = xml.getChildren("msg");
        		for (Object msg1 : msgs) {
        			Element e = (Element) msg1;
        			String uid = e.getAttribute("uid").getValue();
        			String time = e.getAttribute("time").getValue();
        			KrnObject obj_ = krn.getObjectByUid(uid, -1);
        			if (obj_ != null) {
	        			String[] strs = krn.getStrings(obj_, "title", ifcLang.id, 0);
	        			HistoryWithDate hwd = new HistoryWithDate(obj_, time);
	        			String name = strs != null && strs.length > 0 ? strs[0] : "Безымянный";
	        			if(attr_name == ATTR_SRV_HISTORY)
	        				addSrvInHistory(hwd, name);
	        			else if (attr_name == ATTR_IFC_HISTORY)
	        				addIfcInHistory(hwd, name);
	        			else if (attr_name == ATTR_FLT_HISTORY)
	        				addFltInHistory(hwd, name);
	        			else if (attr_name == ATTR_RPT_HISTORY)
	        				addRptInHistory(hwd, name);
        			}
        		}
        	} 
        }catch(Exception e) {e.printStackTrace();}
    }
    
    private void writeToDb(LinkedList<HistoryWithDate> hwds, String attr_name) {
        Element root = new Element("message");
        if(hwds != null && hwds.size()>0) {
        	int i = hwds.size()-1;
        	while (i>=0) {
        		Element xml = new Element("msg");
        		KrnObject obj = hwds.get(i).getObj();
        		xml.setAttribute("uid", obj.uid);
        		String time = hwds.get(i).getTime();
        		xml.setAttribute("time", time);
        		root.addContent(xml);
        		i--;
        	}
        }
        try {
        	ByteArrayOutputStream os_msg = new ByteArrayOutputStream();
        	XMLOutputter out = new XMLOutputter();
        	out.getFormat().setEncoding("UTF-8");
        	out.output(root, os_msg);
        	krn.setBlob(configObj.id, configObj.classId, attr_name, 0, os_msg.toByteArray(),0, 0);
        } catch(Exception e) {e.printStackTrace();}
    }
    /**
     * Сохранение истории просмотров в БД 
     * @throws KrnException 
     */
    public void saveHistories() throws KrnException {
        if (krn.checkExistenceClassByName(NAME_CLASS_CONFIG_LOCAL)) {
            KrnClass clsCnf = krn.getClassByName(NAME_CLASS_CONFIG_LOCAL);
            if (krn.getAttributeByName(clsCnf, ATTR_HISTORY_SRV) == null
                    || krn.getAttributeByName(clsCnf, ATTR_HISTORY_IFC) == null
                    || krn.getAttributeByName(clsCnf, ATTR_HISTORY_FLT) == null
                    || krn.getAttributeByName(clsCnf, ATTR_HISTORY_RPT) == null) {
                return;
            }

            int[] indx;
            Iterator it;
            try {
            	final KrnClass cls = krn.getClassByName("Config");
                if (forDelHistorySrv.size() > 0) {
                    // удаление предыдущих значений атрибута
                    krn.deleteValue(configObj.id, configObj.classId, ATTR_HISTORY_SRV, forDelHistorySrv, 0);
                    krn.deleteValue(configObj.id, configObj.classId, ATTR_SRV_HISTORY, forDelHistorySrv, 0);
                }
                // запись истории в базу
                LinkedList<HistoryWithDate> hwds = config.getSrvHistory();
                writeToDb(hwds, ATTR_SRV_HISTORY);

                if (forDelHistoryIfc.size() > 0) {
                	// удаление предыдущих значений атрибута
                    krn.deleteValue(configObj.id, configObj.classId, ATTR_HISTORY_IFC, forDelHistoryIfc, 0);
                    krn.deleteValue(configObj.id, configObj.classId, ATTR_IFC_HISTORY, forDelHistoryIfc, 0);
                }
                // запись истории в базу
                hwds = config.getIfcHistory();
                writeToDb(hwds, ATTR_IFC_HISTORY);
//                it = config.getHistoryIfc().iterator();
//                while (it.hasNext()) {
//                	addObject(configObj, ATTR_HISTORY_IFC, (KrnObject) it.next());
//                }

                if (forDelHistoryFlt.size() > 0) {
                    // удаление предыдущих значений атрибута
                    krn.deleteValue(configObj.id, configObj.classId, ATTR_HISTORY_FLT, forDelHistoryFlt, 0);
                    krn.deleteValue(configObj.id, configObj.classId, ATTR_FLT_HISTORY, forDelHistoryFlt, 0);
                }
                // запись истории в базу
                hwds = config.getFltHistory();
                writeToDb(hwds, ATTR_FLT_HISTORY);
                
//                it = config.getHistoryFlt().iterator();
//                while (it.hasNext()) {
//                    addObject(configObj, ATTR_HISTORY_FLT, (KrnObject) it.next());
//                }
                
                if (forDelHistoryRpt.size() > 0) {
                    // удаление предыдущих значений атрибута
                    krn.deleteValue(configObj.id, configObj.classId, ATTR_HISTORY_RPT, forDelHistoryRpt, 0);
                    krn.deleteValue(configObj.id, configObj.classId, ATTR_RPT_HISTORY, forDelHistoryRpt, 0);
                }
                // запись истории в базу
                hwds = config.getRptHistory();
                writeToDb(hwds, ATTR_RPT_HISTORY);
//                it = config.getHistoryRpt().iterator();
//                while (it.hasNext()) {
//                    addObject(configObj, ATTR_HISTORY_RPT, (KrnObject) it.next());
//                }
            } catch (KrnException e) {
                e.printStackTrace();
            }
        }}
    
    public void addSrvInHistory(HistoryWithDate hwd, String name) {
    		config.addSrvInHistory(hwd, name);
    }

    public void addIfcInHistory(HistoryWithDate hwd, String name) {
        config.addIfcInHistory(hwd, name);
    }

    public void addFltInHistory(HistoryWithDate hwd, String name) {
        config.addFltInHistory(hwd, name);
    }

    public void addRptInHistory(HistoryWithDate hwd, String name) {
        config.addRptInHistory(hwd, name);
    }
    
	public List<String> getUserRoleUIDs() {
		return userRoleUIDs;
	}
	
	public Map<Long, List<String>> getUserRoles(long langIdRu, long langIdKz) {
		if(userRoles.size()>0)
			return userRoles;
		AttrRequestBuilder arb;
		try {
			arb = new AttrRequestBuilder(Kernel.SC_USER_FOLDER, krn).add("name", langIdRu).add("name", langIdKz);
			// получить объекты
			List<Object[]> rows = krn.getObjects(parentIds, arb.build(), 0);
			List<String> userRolesListRu = new ArrayList<String>();
			List<String> userRolesListKz = new ArrayList<String>();
			// парсинг полученных объектов
			for (Object[] row: rows) {
				userRolesListRu.add(arb.getStringValue("name", langIdRu, row));
				if(arb.getStringValue("name", langIdKz, row) != null)
					userRolesListKz.add(arb.getStringValue("name", langIdKz, row));
				else
					userRolesListKz.add(arb.getStringValue("name", langIdRu, row));
			}
			userRoles.put(langIdRu, userRolesListRu);
			userRoles.put(langIdKz, userRolesListKz);
		} catch (KrnException e) {
			e.printStackTrace();
		}
		return userRoles;
	}

	public Date getLastSuccessfullTime() {
		return lastSuccessfullTime;
	}

	public Date getLastUnsuccessfullTime() {
		return lastUnsuccessfullTime;
	}
}
