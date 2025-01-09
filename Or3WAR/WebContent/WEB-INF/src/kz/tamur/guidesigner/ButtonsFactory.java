package kz.tamur.guidesigner;

import kz.tamur.comps.Constants;
import java.util.ResourceBundle;
import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 07.05.2004
 * Time: 10:33:03
 * To change this template use File | Settings | File Templates.
 */
public class ButtonsFactory {

    static ResourceBundle resource = ResourceBundle.getBundle(
            Constants.NAME_RESOURCES, new Locale("ru"));

    //Dialog's buttons
    public static final int BUTTON_OK = 0;
    public static final int BUTTON_CANCEL = 1;
    public static final int BUTTON_REFRESH = 2;
    public static final int BUTTON_YES = 3;
    public static final int BUTTON_NO = 4;
    public static final int BUTTON_CLEAR = 5;
    public static final int BUTTON_DEFAULT = 6;
    public static final int BUTTON_EDIT = 7;
    public static final int BUTTON_NOACTION = 99;
    public static final int BUTTON_FIND = 8;
    public static final int BUTTON_CLOSE = 9;
    public static final int BUTTON_REPLACE = 10;
    public static final int BUTTON_REPLACEALL = 11;
    public static final int BUTTON_FIND_NEXT = 12;
    public static final int BUTTON_CANCEL_FILTER = 13;
    public static final int BUTTON_CREATE = 14;


    //Function's buttons
    public static final int FN_TREE = 0;
    public static final int FN_INSPECTOR = 1;
    public static final int FN_DEBUG = 2;
    public static final int FN_CLASSES = 3;
    public static final int FN_AREA = 4;
    public static final int FN_SERVICES = 5;
    public static final int FN_INTERFACES = 6;
    public static final int FN_FILTERS = 7;
    public static final int FN_USERS = 8;
    public static final int FN_REPORTS = 9;
    public static final int FN_HYPERS = 10;
    public static final int FN_BASE = 11;
    public static final int FN_REPLICATIONS = 12;
    public static final int FN_SCHEDULER = 13;
    public static final int FN_BOXES = 14;
    public static final int FN_FUNC = 15;
    public static final int FN_REPL = 16;
    public static final int FN_ACTIVE_USERS = 17;

    //Spacer's buttons
    public static final int INS_COL_BEFORE = 0;
    public static final int INS_COL_AFTER = 1;
    public static final int INS_ROW_BEFORE = 2;
    public static final int INS_ROW_AFTER = 3;
    public static final int DELETE_RC = 4;
    public static final int ARROW_LEFT = 5;
    public static final int ARROW_DOWN = 6;

    //Editors's buttons
    public static final int DEFAULT_EDITOR = 0;
    public static final int COLOR_EDITOR = 1;
    public static final int IFC_EDITOR = 2;
    public static final int CREATE_EDITOR = 3;

    //Frame's buttons
    public static final int MINIMIZE = 0;
    public static final int CLOSE = 1;
    public static final int MAXIMIZE = 2;
    public static final int RESTORE = 3;
}
