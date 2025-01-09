package kz.tamur.common;

public final class ErrorCodes {
    public static final int TYPE_WARNING = 1;
    public static final int TYPE_ERROR = 2;

    public static final int CLASS_NOT_FOUND = 101;
    public static final int ATTRIBUTE_NOT_FOUND = 102;
    public static final int OBJECT_NOT_FOUND = 103;

    public static final int ER_LOCK_DEADLOCK = 201;
    public static final int ER_OR3_LOCKED = 202;
    public static final int LOCK_WAIT_TIMEOUT = 203;
    public static final int PROCEDURE_NOT_EXIST = 204;
    public static final int PROCEDURE_NOT_VALID = 205;
    public static final int PROCEDURE_SQL_ERROR = 206;
    // коды ошибок авторизации пользователя
    public static final int USER_HAS_CONNECT = 301;
    public static final int USER_NOT_FOUND = 302;
    public static final int SERVER_NOT_AVAILABLE = 303;
    public static final int USER_IS_BLOCKED = 304;
    public static final int USER_NO_RIGHTS = 305;
    public static final int SERVER_BLOCKED = 306;
    public static final int USER_NO_BASE = 307;
    public static final int USER_NO_IFC_LANG = 308;
    public static final int USER_NO_DATA_LANG = 309;
    public static final int USER_NO_TASK = 310;
    public static final int SERVER_MAX_CLIENTS_REACHED = 311;
    public static final int USER_NEED_ECP = 312;
    public static final int USER_NOT_ACT = 313;
    public static final int USER_NOT_ADM = 314;
    public static final int USER_IS_EXPIRED = 315;
    public static final int USER_IS_ENDED = 316;
    public static final int USER_NOT_LOGIN = 317;
    public static final int USER_HAS_CONNECT_SAME_IP = 318;
    public static final int USER_DUBLICATED = 319;
    public static final int USER_ECP_FAILED = 320;
    public static final int USER_IIN_NOT_MATCH = 321;
    public static final int DRV_ILLEGAL_ARGUMENT_EXCEPTION = 400;
    public static final int DRV_ID_NOT_FOUND = 401;

    public static final int FLR_ATTR_NOT_FILL = 501;

    
    // коды ошибок верификации пароля
    public static final int PASS_NOT_COMPLETE = 600;
    public static final int PASS_PASS_NOT_EQUALS = 601;
    public static final int PASS_PASS_IDENT = 602;
    public static final int PASS_OLD_PASS_INVALID = 603;
    public static final int PASS_MIN_PERIOD_PASS = 604;
    public static final int PASS_VALID_PWD_MIN_LOGIN = 605;
    public static final int PASS_VALID_PWD_MAX_LOGIN = 606;
    public static final int PASS_VALID_PWD_MIN_PASS = 607;
    public static final int PASS_VALID_PWD_MIN_PASS_ADM = 608;
    public static final int PASS_VALID_PWD_MAX_PASS = 609;
    public static final int PASS_VALID_PWD_NO_NUMB = 610;
    public static final int PASS_VALID_PWD_NO_SYMB = 611;
    public static final int PASS_VALID_PWD_NO_REG = 612;
    public static final int PASS_VALID_PWD_NO_SPEC = 613;
    public static final int PASS_VALID_PWD_NOT_NAME = 614;
    public static final int PASS_VALID_PWD_NOT_SURN = 615;
    public static final int PASS_VALID_PWD_NOT_TEL = 616;
    public static final int PASS_VALID_PWD_NOT_WORD = 617;
    public static final int PASS_VALID_PWD_NOT_LOGIN = 618;
    public static final int PASS_VALID_PWD_NOT_REP = 619;
    public static final int PASS_MESS_PASS_DUPL = 620;
    public static final int PASS_VALID_PWD_NO_ALL_NUMB = 621;
    public static final int PASS_VALID_PWD_NOT_REP_ANY_MORE_TWO = 622;
    public static final int PASS_VALID_PWD_NOT_KEYBOARD = 623;
    public static final int PASS_VALID_PWD_NOT_EASY_SYMBOLS = 624;
    
    //Ошибки при откате работающего потока
    public static final int CANCEL_FLOW_BY_USER = 701;
    //Ошибки при вызове вэб сервиса
    public static final int ER_WEB_SERICE_REQUEST = 801;
    //Ошибки при срабатывании триггеров
    public static final int ER_EXEC_TRIGGER_ATTR = 901;
    public static final int ER_EXEC_TRIGGER_CLS = 902;

    public static final int ERROR_FGAC_NOT_ALLOW = 903;
}