package kz.tamur.comps.models;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 15.03.2004
 * Time: 16:40:15
 */
public final class Types {
    public static final int INTEGER = 1;
    public static final int DOUBLE = 2;
    public static final int STRING = 3;
    /** Не редактируемые строковые данные */
    public static final int VIEW_STRING = 4;
    public static final int MSTRING = 5;
    public static final int BOOLEAN = 6;
    public static final int KRNOBJECT = 7;
    public static final int REF = 8;
    public static final int EXPR = 9;
    public static final int COLOR = 10;
    public static final int FONT = 11;
    public static final int BORDER = 12;
    public static final int IMAGE = 13;
    public static final int STYLEDTEXT = 14;
    public static final int REPORT = 15;
    public static final int SEQUENCE = 16;
    public static final int FILTER = 17;
    public static final int PMENUITEM = 18;
    /**
     * Раскрывающийся список
     */
    public static final int ENUM = 19;
    /**
     * Раскрывающийся список с подсказкамив виде картинок
     */
    public static final int ENUM_TOOL_TIP = 20;
    public static final int COMPONENT = 21;
    public static final int RSTRING = 22;
    public static final int PROCESSES = 23;

    /**
     * Выбор одного или нескольких KrnObject, с хранением только их идентификаторов
     */
    public static final int KRNOBJECT_ID = 24;
    /**
     * Форматируемый текст в формате html
     */
    public static final int HTML_TEXT = 25;
    /**
     * Градиентная заливка компонента
     */
    public static final int GRADIENT_COLOR = 26;
    /**
     * Ссылка на объект
     */
    public static final int KRNOBJECT_ITEM = 27;
}
