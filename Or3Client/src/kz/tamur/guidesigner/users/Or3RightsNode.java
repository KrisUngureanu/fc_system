package kz.tamur.guidesigner.users;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;

import org.jdom.Element;
import org.jdom.xpath.XPath;

import com.cifs.or2.kernel.KrnObject;

import kz.tamur.rt.Utils;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 13.10.2004
 * Time: 11:31:34
 * To change this template use File | Settings | File Templates.
 */
public class Or3RightsNode extends DefaultMutableTreeNode {

    public static final String OR3_RIGHTS = "or3rights";
    public static final String DRAG_TREE_RIGHT = "drag";
    public static final String PROCESS_RIGHTS = "process";
    public static final String PROCESS_VIEW_RIGHT = "processView";
    public static final String PROCESS_CREATE_RIGHT = "processCreate";
    public static final String PROCESS_EDIT_RIGHT = "processEdit";
    public static final String PROCESS_DELETE_RIGHT = "processDelete";

    public static final String INTERFACE_RIGHTS = "interface";
    public static final String INTERFACE_VIEW_RIGHT = "interfaceView";
    public static final String INTERFACE_CREATE_RIGHT = "interfaceCreate";
    public static final String INTERFACE_EDIT_RIGHT = "interfaceEdit";
    public static final String INTERFACE_DELETE_RIGHT = "interfaceDelete";

    public static final String CLASSES_RIGHTS = "classes";
    public static final String CLASSES_VIEW_RIGHT = "classesView";
    public static final String CLASSES_CREATE_RIGHT = "classesCreate";
    public static final String CLASSES_EDIT_RIGHT = "classesEdit";
    public static final String CLASSES_DELETE_RIGHT = "classesDelete";

    public static final String ATTRIBUTES_VIEW_RIGHT = "attributesView";
    public static final String ATTRIBUTES_CREATE_RIGHT = "attributesCreate";
    public static final String ATTRIBUTES_EDIT_RIGHT = "attributesEdit";
    public static final String ATTRIBUTES_DELETE_RIGHT = "attributesDelete";

    public static final String METHODS_VIEW_RIGHT = "methodsView";
    public static final String METHODS_CREATE_RIGHT = "methodsCreate";
    public static final String METHODS_EDIT_RIGHT = "methodsEdit";
    public static final String METHODS_DELETE_RIGHT = "methodsDelete";

    public static final String FILTERS_RIGHTS = "filters";
    public static final String FILTERS_VIEW_RIGHT = "filtersView";
    public static final String FILTERS_CREATE_RIGHT = "filtersCreate";
    public static final String FILTERS_EDIT_RIGHT = "filtersEdit";
    public static final String FILTERS_DELETE_RIGHT = "filtersDelete";

    public static final String BASES_RIGHTS = "bases";
    public static final String BASES_VIEW_RIGHT = "basesView";
    public static final String BASES_CREATE_RIGHT = "basesCreate";
    public static final String BASES_EDIT_RIGHT = "basesEdit";
    public static final String BASES_DELETE_RIGHT = "basesDelete";

    public static final String BOXES_RIGHTS = "boxes";
    public static final String BOXES_VIEW_RIGHT = "boxesView";
    public static final String BOXES_CREATE_RIGHT = "boxesCreate";
    public static final String BOXES_EDIT_RIGHT = "boxesEdit";
    public static final String BOXES_DELETE_RIGHT = "boxesDelete";

    public static final String FUNCS_RIGHTS = "funcs";
    public static final String FUNCS_VIEW_RIGHT = "funcsView";
    public static final String FUNCS_CREATE_RIGHT = "funcsCreate";
    public static final String FUNCS_EDIT_RIGHT = "funcsEdit";
    public static final String FUNCS_DELETE_RIGHT = "funcsDelete";

    public static final String PROCS_RIGHTS = "procs";
    public static final String PROCS_VIEW_RIGHT = "procsView";
    public static final String PROCS_CREATE_RIGHT = "procsCreate";
    public static final String PROCS_EDIT_RIGHT = "procsEdit";
    public static final String PROCS_DELETE_RIGHT = "procsDelete";

    public static final String VCS_CHANGE_RIGHTS = "vcsChanges";
    public static final String VCS_CHANGE_VIEW_RIGHT = "vcsChangeView";
    public static final String VCS_CHANGE_COMMIT_RIGHT = "vcsChangeCommit";

    public static final String USERS_RIGHTS = "users";
    public static final String USERS_VIEW_RIGHT = "usersView";
    public static final String USERS_CREATE_RIGHT = "usersCreate";
    public static final String USERS_EDIT_RIGHT = "usersEdit";
    public static final String USERS_DELETE_RIGHT = "usersDelete";

    public static final String REPORTS_RIGHTS = "reports";
    public static final String REPORTS_VIEW_RIGHT = "reportsView";
    public static final String REPORTS_CREATE_RIGHT = "reportsCreate";
    public static final String REPORTS_EDIT_RIGHT = "reportsEdit";
    public static final String REPORTS_DELETE_RIGHT = "reportsDelete";

    public static final String MENU_RIGHTS = "menu";
    public static final String MENU_VIEW_RIGHT = "menuView";
    public static final String MENU_CREATE_RIGHT = "menuCreate";
    public static final String MENU_EDIT_RIGHT = "menuEdit";
    public static final String MENU_DELETE_RIGHT = "menuDelete";

    public static final String TASKS_RIGHTS = "tasks";
    public static final String TASKS_VIEW_RIGHT = "tasksView";
    public static final String TASKS_CREATE_RIGHT = "tasksCreate";
    public static final String TASKS_EDIT_RIGHT = "tasksEdit";
    public static final String TASKS_DELETE_RIGHT = "tasksDelete";

    public static final String USER_RIGHT_RIGHTS = "rights";
    public static final String USER_RIGHT_VIEW_RIGHT = "rightsView";
    public static final String USER_RIGHT_CREATE_RIGHT = "rightsCreate";
    public static final String USER_RIGHT_EDIT_RIGHT = "rightsEdit";
    public static final String USER_RIGHT_DELETE_RIGHT = "rightsDelete";

    public static final String REPLICATION_RIGHTS = "replication";
    public static final String REPLICATION_VIEW_RIGHT = "replicationView";
    public static final String REPLICATION_IMPORT_RIGHT = "replicationImport";
    public static final String REPLICATION_EXPORT_RIGHT = "replicationExport";
    public static final String REPLICATION_EXPORTTEXT_RIGHT = "replicationExportToText";
    public static final String REPLICATION_SENDMSG_RIGHT = "replicationSendMessage";
    public static final String REPLICATION_BANUSER_RIGHT = "replicationBanUser";
    public static final String REPLICATION_BLOCKSERVER_RIGHT = "replicationBlockServer";

    public static final String SEARCH_RIGHTS = "search";
    public static final String SEARCH_MAKE_RIGHT = "searchMake";
    
    public static final String TERMINAL_RIGHTS = "terminal";
    public static final String TERMINAL_VIEW_RIGHT = "terminalView";
    
    public static final String CONFIG_RIGHTS = "config";
    public static final String CONFIG_VIEW_RIGHT = "configView";

    public static final String SERVICE_CONTROL_RIGHTS = "serviceControl";
    public static final String SERVICE_CONTROL_VIEW_RIGHT = "serviceControlView";

    public static final String WEB_PAGE_RIGHTS = "web";
    public static final String WEB_PAGE_MAIN_RIGHT = "webMain";
    public static final String WEB_PAGE_MAINIFC_RIGHT = "webMainIfc";
    public static final String WEB_PAGE_MONITOR_RIGHT = "webMonitor";
    public static final String WEB_PAGE_PROCESS_RIGHT = "webProcess";
    public static final String WEB_PAGE_ARCHS_RIGHT = "webArchs";
    public static final String WEB_PAGE_DICTS_RIGHT = "webDicts";
    public static final String WEB_PAGE_STATS_RIGHT = "webStatistic";
    public static final String WEB_PAGE_HELPS_RIGHT = "webHelps";
    public static final String WEB_PAGE_SHTAT_RIGHT = "webShtat";
    public static final String WEB_PAGE_RIGHTS_RIGHT = "webUserRights";
    public static final String WEB_PAGE_ACTIONS_RIGHT = "webUserActions";
    public static final String WEB_PAGE_SESSIONS_RIGHT = "webUserSessions";
    public static final String WEB_PAGE_PROFILE_RIGHT = "webProfile";
    public static final String WEB_PAGE_NOTIFICATION = "webNotification";
    public static final String WEB_PAGE_ADMINS_RIGHT = "webAdmins";

    public static final String WEB_PAGE_MY_INFO_RIGHT = "webMyInfo";
    //ГБД РН
    public static final String WEB_PAGE_KAD_MAP_RIGHT = "webKadMap";
    public static final String WEB_PAGE_KAD_WORK_RIGHT = "webKadWork";

    private boolean checked;
    private String title;
    private String name;
    private String asString;

    public Or3RightsNode(Element xml, String name) {
        this.name = name;
        this.title = translate(name);
        this.checked = (xml != null && xml.getChildren().size() == 0 && xml.getText().equals("1"));
        String[] children = structure.get(name);
        if (children != null) {
            for (String child : children) {
                Or3RightsNode node = new Or3RightsNode((xml != null) ? xml.getChild(child) : null, child);
                add(node);
            }
        }
        
    }
    
    private static void reLoadWebPageRights() {
    	structure.put(WEB_PAGE_RIGHTS, new String[] {WEB_PAGE_MAIN_RIGHT, WEB_PAGE_MAINIFC_RIGHT, WEB_PAGE_MONITOR_RIGHT, WEB_PAGE_PROCESS_RIGHT,
				WEB_PAGE_ARCHS_RIGHT, WEB_PAGE_DICTS_RIGHT, WEB_PAGE_STATS_RIGHT, WEB_PAGE_HELPS_RIGHT, 
				WEB_PAGE_SHTAT_RIGHT, WEB_PAGE_RIGHTS_RIGHT, WEB_PAGE_ACTIONS_RIGHT, WEB_PAGE_SESSIONS_RIGHT, WEB_PAGE_PROFILE_RIGHT, WEB_PAGE_NOTIFICATION, WEB_PAGE_ADMINS_RIGHT,
				WEB_PAGE_MY_INFO_RIGHT, WEB_PAGE_KAD_MAP_RIGHT, WEB_PAGE_KAD_WORK_RIGHT});
    }
    
    public static void addDynamicNodes() {
    	List<KrnObject> objs = Utils.getDynamicNodeObjs();
    	String[] dynamicObjTitles = Utils.getDynamicNodeTitles(null, objs,  0);
    	if(dynamicObjTitles != null){
    		String[] dynamicEngTitles = new String[dynamicObjTitles.length];
    		for(int i = 0; i < dynamicObjTitles.length; i++) {
    			dynamicEngTitles[i] = "ui_dynamicTitle_" + objs.get(i).id;
    			dict.put(dynamicEngTitles[i], dynamicObjTitles[i]);
    		}
    		reLoadWebPageRights();
    		String[] webPageRights = concatenateArrays(structure.get(WEB_PAGE_RIGHTS), dynamicEngTitles);

    		structure.put(WEB_PAGE_RIGHTS, webPageRights);
    	}
    }

    public String getName() {
        return title;
    }

    public void setName(String name) {
        title = name;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    protected void load() {
    }

    private static final Set<String> IMPORTANT_RIGHTS = new HashSet<String>();
    
    static {
    	IMPORTANT_RIGHTS.addAll(Arrays.asList(new String[] {
    			PROCESS_CREATE_RIGHT, PROCESS_EDIT_RIGHT, PROCESS_DELETE_RIGHT,
    			INTERFACE_CREATE_RIGHT, INTERFACE_EDIT_RIGHT, INTERFACE_DELETE_RIGHT,
    			CLASSES_CREATE_RIGHT, CLASSES_EDIT_RIGHT, CLASSES_DELETE_RIGHT,
    			ATTRIBUTES_CREATE_RIGHT, ATTRIBUTES_EDIT_RIGHT, ATTRIBUTES_DELETE_RIGHT,
    			METHODS_CREATE_RIGHT, METHODS_EDIT_RIGHT, METHODS_DELETE_RIGHT,
    			USERS_DELETE_RIGHT,
    			REPORTS_CREATE_RIGHT, REPORTS_EDIT_RIGHT, REPORTS_DELETE_RIGHT,
    			FILTERS_CREATE_RIGHT, FILTERS_EDIT_RIGHT, FILTERS_DELETE_RIGHT,
    			BASES_CREATE_RIGHT, BASES_EDIT_RIGHT, BASES_DELETE_RIGHT,
    			FUNCS_CREATE_RIGHT, FUNCS_EDIT_RIGHT, FUNCS_DELETE_RIGHT,
    			MENU_CREATE_RIGHT, MENU_EDIT_RIGHT, MENU_DELETE_RIGHT,
    			VCS_CHANGE_COMMIT_RIGHT
    	}));
    }
    
    public static boolean isImportantRight(String right) {
    	return IMPORTANT_RIGHTS.contains(right);
    }
    
    private static final Map<String, String[]> structure = new HashMap<String, String[]>() {{
        put(OR3_RIGHTS, new String[] {DRAG_TREE_RIGHT, PROCESS_RIGHTS, INTERFACE_RIGHTS, CLASSES_RIGHTS, USERS_RIGHTS, REPORTS_RIGHTS,
                                    BASES_RIGHTS, BOXES_RIGHTS, FUNCS_RIGHTS, FILTERS_RIGHTS, MENU_RIGHTS, TASKS_RIGHTS, USER_RIGHT_RIGHTS,
                                    VCS_CHANGE_RIGHTS,REPLICATION_RIGHTS, SEARCH_RIGHTS, TERMINAL_RIGHTS, CONFIG_RIGHTS, WEB_PAGE_RIGHTS});
        put(PROCESS_RIGHTS, new String[] {PROCESS_VIEW_RIGHT, PROCESS_CREATE_RIGHT, PROCESS_EDIT_RIGHT, PROCESS_DELETE_RIGHT});
        put(INTERFACE_RIGHTS, new String[] {INTERFACE_VIEW_RIGHT, INTERFACE_CREATE_RIGHT, INTERFACE_EDIT_RIGHT, INTERFACE_DELETE_RIGHT});
        put(CLASSES_RIGHTS, new String[] {CLASSES_VIEW_RIGHT, CLASSES_CREATE_RIGHT, CLASSES_EDIT_RIGHT, CLASSES_DELETE_RIGHT,
                                        ATTRIBUTES_VIEW_RIGHT, ATTRIBUTES_CREATE_RIGHT, ATTRIBUTES_EDIT_RIGHT, ATTRIBUTES_DELETE_RIGHT,
                                        METHODS_VIEW_RIGHT, METHODS_CREATE_RIGHT, METHODS_EDIT_RIGHT, METHODS_DELETE_RIGHT});
        put(USERS_RIGHTS, new String[] {USERS_VIEW_RIGHT, USERS_CREATE_RIGHT, USERS_EDIT_RIGHT, USERS_DELETE_RIGHT});
        put(REPORTS_RIGHTS, new String[] {REPORTS_VIEW_RIGHT, REPORTS_CREATE_RIGHT, REPORTS_EDIT_RIGHT, REPORTS_DELETE_RIGHT});

        put(FILTERS_RIGHTS, new String[] {FILTERS_VIEW_RIGHT, FILTERS_CREATE_RIGHT, FILTERS_EDIT_RIGHT, FILTERS_DELETE_RIGHT});
        put(BASES_RIGHTS, new String[] {BASES_VIEW_RIGHT, BASES_CREATE_RIGHT, BASES_EDIT_RIGHT, BASES_DELETE_RIGHT});
        put(BOXES_RIGHTS, new String[] {BOXES_VIEW_RIGHT, BOXES_CREATE_RIGHT, BOXES_EDIT_RIGHT, BOXES_DELETE_RIGHT});
        put(FUNCS_RIGHTS, new String[] {FUNCS_VIEW_RIGHT, FUNCS_CREATE_RIGHT, FUNCS_EDIT_RIGHT, FUNCS_DELETE_RIGHT});
        put(MENU_RIGHTS, new String[] {MENU_VIEW_RIGHT, MENU_CREATE_RIGHT, MENU_EDIT_RIGHT, MENU_DELETE_RIGHT});
        put(TASKS_RIGHTS, new String[] {TASKS_VIEW_RIGHT, TASKS_CREATE_RIGHT, TASKS_EDIT_RIGHT, TASKS_DELETE_RIGHT});
        put(USER_RIGHT_RIGHTS, new String[] {USER_RIGHT_VIEW_RIGHT, USER_RIGHT_CREATE_RIGHT, USER_RIGHT_EDIT_RIGHT, USER_RIGHT_DELETE_RIGHT});
        put(VCS_CHANGE_RIGHTS, new String[] {VCS_CHANGE_VIEW_RIGHT, VCS_CHANGE_COMMIT_RIGHT});
        put(REPLICATION_RIGHTS, new String[] {REPLICATION_VIEW_RIGHT, REPLICATION_IMPORT_RIGHT, REPLICATION_EXPORT_RIGHT,
                                    REPLICATION_EXPORTTEXT_RIGHT, REPLICATION_SENDMSG_RIGHT, REPLICATION_BANUSER_RIGHT,
                                    REPLICATION_BLOCKSERVER_RIGHT});
        put(SEARCH_RIGHTS, new String[] {SEARCH_MAKE_RIGHT});
        put(TERMINAL_RIGHTS, new String[] {TERMINAL_VIEW_RIGHT});
        put(CONFIG_RIGHTS, new String[] {CONFIG_VIEW_RIGHT});
        put(SERVICE_CONTROL_RIGHTS, new String[] {SERVICE_CONTROL_VIEW_RIGHT});

        put(WEB_PAGE_RIGHTS, new String[] {WEB_PAGE_MAIN_RIGHT, WEB_PAGE_MAINIFC_RIGHT, WEB_PAGE_MONITOR_RIGHT, WEB_PAGE_PROCESS_RIGHT,
        							WEB_PAGE_ARCHS_RIGHT, WEB_PAGE_DICTS_RIGHT, WEB_PAGE_STATS_RIGHT, WEB_PAGE_HELPS_RIGHT, 
        							WEB_PAGE_SHTAT_RIGHT, WEB_PAGE_RIGHTS_RIGHT, WEB_PAGE_ACTIONS_RIGHT, WEB_PAGE_SESSIONS_RIGHT, WEB_PAGE_PROFILE_RIGHT, WEB_PAGE_NOTIFICATION, WEB_PAGE_ADMINS_RIGHT,
        							WEB_PAGE_MY_INFO_RIGHT, WEB_PAGE_KAD_MAP_RIGHT, WEB_PAGE_KAD_WORK_RIGHT});

    }};

    private static final Map<String, String> dict = new HashMap<String, String>() {{
        put(OR3_RIGHTS, "Права OR3");
        put(DRAG_TREE_RIGHT, "Перемещение узлов дерева");
        put(PROCESS_RIGHTS, "Процессы");
        put(PROCESS_VIEW_RIGHT, "Просмотр процессов");
        put(PROCESS_CREATE_RIGHT, "Создание процессов");
        put(PROCESS_EDIT_RIGHT, "Редактирование процессов");
        put(PROCESS_DELETE_RIGHT, "Удаление процессов");
        put(INTERFACE_RIGHTS, "Интерфейсы");
        put(INTERFACE_VIEW_RIGHT, "Просмотр интерфейсов");
        put(INTERFACE_CREATE_RIGHT, "Создание интерфейсов");
        put(INTERFACE_EDIT_RIGHT, "Редактирование интерфейсов");
        put(INTERFACE_DELETE_RIGHT, "Удаление интерфейсов");
        put(CLASSES_RIGHTS, "Классы");
        put(CLASSES_VIEW_RIGHT, "Просмотр классов");
        put(CLASSES_CREATE_RIGHT, "Создание классов");
        put(CLASSES_EDIT_RIGHT, "Редактирование классов");
        put(CLASSES_DELETE_RIGHT, "Удаление классов");
        put(ATTRIBUTES_VIEW_RIGHT, "Просмотр атрибутов");
        put(ATTRIBUTES_CREATE_RIGHT, "Создание атрибутов");
        put(ATTRIBUTES_EDIT_RIGHT, "Редактирование атрибутов");
        put(ATTRIBUTES_DELETE_RIGHT, "Удаление атрибутов");
        put(METHODS_VIEW_RIGHT, "Просмотр методов");
        put(METHODS_CREATE_RIGHT, "Создание методов");
        put(METHODS_EDIT_RIGHT, "Редактирование методов");
        put(METHODS_DELETE_RIGHT, "Удаление методов");
        put(FILTERS_RIGHTS, "Фильтры");
        put(FILTERS_VIEW_RIGHT, "Просмотр фильтров");
        put(FILTERS_CREATE_RIGHT, "Создание фильтров");
        put(FILTERS_EDIT_RIGHT, "Редактирование фильтров");
        put(FILTERS_DELETE_RIGHT, "Удаление фильтров");
        put(USERS_RIGHTS, "Пользователи");
        put(USERS_VIEW_RIGHT, "Просмотр пользователей");
        put(USERS_CREATE_RIGHT, "Создание пользователей");
        put(USERS_EDIT_RIGHT, "Редактирование пользователей");
        put(USERS_DELETE_RIGHT, "Удаление пользователей");
        put(REPORTS_RIGHTS, "Отчеты");
        put(REPORTS_VIEW_RIGHT, "Просмотр отчетов");
        put(REPORTS_CREATE_RIGHT, "Создание отчетов");
        put(REPORTS_EDIT_RIGHT, "Редактирование отчетов");
        put(REPORTS_DELETE_RIGHT, "Удаление отчетов");

        put(BASES_RIGHTS, "Базы");
        put(BASES_VIEW_RIGHT, "Просмотр баз");
        put(BASES_CREATE_RIGHT, "Создание баз");
        put(BASES_EDIT_RIGHT, "Редактирование баз");
        put(BASES_DELETE_RIGHT, "Удаление баз");

        put(BOXES_RIGHTS, "Почтовые ящики");
        put(BOXES_VIEW_RIGHT, "Просмотр ящиков");
        put(BOXES_CREATE_RIGHT, "Создание ящиков");
        put(BOXES_EDIT_RIGHT, "Редактирование ящиков");
        put(BOXES_DELETE_RIGHT, "Удаление ящиков");

        put(FUNCS_RIGHTS, "Функции");
        put(FUNCS_VIEW_RIGHT, "Просмотр функций");
        put(FUNCS_CREATE_RIGHT, "Создание функций");
        put(FUNCS_EDIT_RIGHT, "Редактирование функций");
        put(FUNCS_DELETE_RIGHT, "Удаление функций");

        put(MENU_RIGHTS, "Меню");
        put(MENU_VIEW_RIGHT, "Просмотр меню");
        put(MENU_CREATE_RIGHT, "Создание меню");
        put(MENU_EDIT_RIGHT, "Редактирование меню");
        put(MENU_DELETE_RIGHT, "Удаление меню");

        put(TASKS_RIGHTS, "Планировщик задач");
        put(TASKS_VIEW_RIGHT, "Просмотр задач");
        put(TASKS_CREATE_RIGHT, "Создание задач");
        put(TASKS_EDIT_RIGHT, "Редактирование задач");
        put(TASKS_DELETE_RIGHT, "Удаление задач");

        put(USER_RIGHT_RIGHTS, "Права доступа");
        put(USER_RIGHT_VIEW_RIGHT, "Просмотр прав доступа");
        put(USER_RIGHT_CREATE_RIGHT, "Создание прав доступа");
        put(USER_RIGHT_EDIT_RIGHT, "Редактирование прав доступа");
        put(USER_RIGHT_DELETE_RIGHT, "Удаление прав доступа");

        put(VCS_CHANGE_RIGHTS, "Контроль версий");
        put(VCS_CHANGE_VIEW_RIGHT, "Просмотр изменений");
        put(VCS_CHANGE_COMMIT_RIGHT, "Подтверждение изменений");

        put(REPLICATION_RIGHTS, "Репликация");
        put(REPLICATION_VIEW_RIGHT, "Просмотр активных пользователей");
        put(REPLICATION_IMPORT_RIGHT, "Импорт базы");
        put(REPLICATION_EXPORT_RIGHT, "Экспорт базы");
        put(REPLICATION_EXPORTTEXT_RIGHT, "Экспорт базы в файл");
        put(REPLICATION_SENDMSG_RIGHT, "Отправка сообщений");
        put(REPLICATION_BANUSER_RIGHT, "Отключение пользователей");
        put(REPLICATION_BLOCKSERVER_RIGHT, "Блокировка сервера");

        put(SEARCH_RIGHTS, "Поиск");
        put(SEARCH_MAKE_RIGHT, "Выполнение поиска");
        
        put(TERMINAL_RIGHTS, "Консоль");
        put(TERMINAL_VIEW_RIGHT, "Просмотр Консоли");
        
        put(CONFIG_RIGHTS, "Конфигуратор");
        put(CONFIG_VIEW_RIGHT, "Просмотр Конфигуратора");
        
        put(SERVICE_CONTROL_RIGHTS, "Управление процессами");
        put(SERVICE_CONTROL_VIEW_RIGHT, "Просмотр управления процессами");
        
        put(WEB_PAGE_RIGHTS, "Пункты меню WEB");
        put(WEB_PAGE_MAIN_RIGHT, "Главная страница");
        put(WEB_PAGE_MAINIFC_RIGHT, "Главное окно (КНБ)");
        put(WEB_PAGE_MONITOR_RIGHT, "Монитор задач");
        put(WEB_PAGE_PROCESS_RIGHT, "Процессы");
        put(WEB_PAGE_ARCHS_RIGHT, "Архивы");
        put(WEB_PAGE_DICTS_RIGHT, "Справочники");
        put(WEB_PAGE_STATS_RIGHT, "Статистика");
        put(WEB_PAGE_HELPS_RIGHT, "Помощь");
        put(WEB_PAGE_RIGHTS_RIGHT, "Права доступа");
        put(WEB_PAGE_SHTAT_RIGHT, "Штатная расстановка");
        put(WEB_PAGE_ACTIONS_RIGHT, "Мониторинг действий пользователей");
        put(WEB_PAGE_SESSIONS_RIGHT, "Управление сессиями пользователей");
        put(WEB_PAGE_PROFILE_RIGHT, "Профиль пользователя");
        put(WEB_PAGE_NOTIFICATION, "Уведомления");
        put(WEB_PAGE_ADMINS_RIGHT, "Технологический блок");
    
        put(WEB_PAGE_MY_INFO_RIGHT, "Мое личное дело");
        put(WEB_PAGE_KAD_MAP_RIGHT, "Кадастровая карта");
        put(WEB_PAGE_KAD_WORK_RIGHT, "Кадастровое дело");

    }};
    
    static {
    	addDynamicNodes();
    }
    
    private static String[] concatenateArrays(String[] arr1, String[] arr2) {
    	int len = (arr1 != null ? arr1.length : 0) + (arr2 != null? arr2.length : 0);
    	String[] res = new String[len];
    	
    	if(arr1 != null) {
    		for(int i = 0; i< arr1.length; i++) {
    			res[i] = arr1[i];
    		}
    	}
    	
    	if(arr2 != null) {
    		int pref = 0;
    		if(arr1 != null) 
    			pref = arr1.length;
    		for(int i = 0; i < arr2.length; i++) {
    			res[pref + i] = arr2[i];
    		}
    	}    	
    	return res;
    }

    private static String translate(String enStr) {
        String res = (String)dict.get(enStr);
        return (res != null) ? res : enStr;
    }

    public Element getXml() {
        Element xml = null;
        if (isLeaf() && checked) {
            xml = new Element(name);
            xml.setText("1");
            return xml;
        }

        for (Enumeration en = children(); en.hasMoreElements(); ) {
            Or3RightsNode node = (Or3RightsNode)en.nextElement();
            Element childXml = node.getXml();
            if (childXml != null) {
                if (xml == null)
                    xml = new Element(name);
                xml.addContent(childXml);
            }
        }
        return xml;
    }

    public String toString() {
        return asString;
    }

    public void calculate() {
        if (isLeaf() && checked) {
            asString = title;
        }

        for (Enumeration en = children(); en.hasMoreElements(); ) {
            Or3RightsNode node = (Or3RightsNode)en.nextElement();
            node.calculate();
            String child = node.toString();
            if (child != null && child.length() > 0) {
                if (asString == null || asString.length() == 0)
                    asString = child;
                else
                    asString += "," + child;
            }
        }
    }

    public static String calculate(Element xml) {
        String res = "";
        String title = translate(xml.getName());
        List children = xml.getChildren();
        boolean checked = (xml != null && children.size() == 0 && xml.getText().equals("1"));

        if (checked) {
            res = title;
        }

        for (int i= 0; i < children.size(); i++) {
            Element child = (Element) children.get(i);

            String chres = calculate(child);
            if (chres != null && chres.length() > 0) {
                if (res.length() == 0)
                    res = chres;
                else
                    res += "," + chres;
            }
        }
        return res;
    }

    public static void merge(Element e1, Element e2) {
        List children = e2.getChildren();

        for (int i = children.size() - 1; i >= 0; i--) {
            Element child2 = (Element) children.get(i);

            String name = child2.getName();
            Element child1 = e1.getChild(name);
            if (child1 == null) {
                child2.detach();
                e1.addContent(child2);
            } else {
                merge(child1, child2);
            }
        }
    }

    public static boolean hasRight(Element or3Rights, String right) {
//        return true;
        try {
            XPath xp = XPath.newInstance(".//" + right);
            String res = xp.valueOf(or3Rights);
            return "1".equals(res);
        } catch (Exception e) {
            return false;
        }
    }
}
