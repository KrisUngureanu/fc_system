package kz.tamur.comps.models;

import kz.tamur.comps.Constants;
import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: erik-b
 * Date: 27.03.2009
 * Time: 9:53:52
 * To change this template use File | Settings | File Templates.
 */
public class MapPropertyRoot extends PropertyRoot {
    public MapPropertyRoot() {
        super();

        new PropertyNode(this, "title", Types.RSTRING, null, false, null);
        new PropertyNode(this, "description", Types.STYLEDTEXT, null, false, null);
        new PropertyNode(this, "toolTip", Types.HTML_TEXT, null, false, null);
        //Позиция
        new CompPosition(this);
        //Данные
        PropertyNode refs = new PropertyNode(this, "ref", -1, null, false, null);

        new PropertyNode(refs, "data", Types.REF, null, false, null);                   //Привязка к регионам
        new PropertyNode(refs, "indexRef", Types.REF, null, false, null);                   //Привязка к индексу региона
        new PropertyNode(refs, "colorRef", Types.REF, null, false, null);                   //Привязка к цвету региона
        new PropertyNode(refs, "content", Types.REF, null, false, null);                   //Привязка к содержимому 
        new PropertyNode(refs, "titlePath", Types.REF, null, false, null);                   //Привязка к титулам
        new PropertyNode(refs, "valuePath", Types.REF, null, false, null);                   //Привязка к показателям
        new PropertyNode(refs, "contentFilter", Types.FILTER, null, false, null);            //Фильтр на содержимое

        new ParamFilters(refs);
        EnumValue[] env = new EnumValue[] {
            new EnumValue(Constants.RM_ONCE, "При первом откр. интерф."),
            new EnumValue(Constants.RM_ALWAYS, "При каждом откр. интерф."),
            new EnumValue(Constants.RM_DIRECTLY, "В зависим. от действ. польз.")
        };
        new PropertyNode(refs, "refreshMode", Types.ENUM, env, false, null);
        //Вид
        PropertyNode view = new PropertyNode(this, "view", -1, null, false, null);
        PropertyNode font = new FontProperty(view);
        PropertyNode col1 = font.getChild("fontColor");
        col1.setDefaultValue(UIManager.getColor("Button.foreground"));
        new PropertyNode(font, "lightFontColor", Types.COLOR, null, false, Color.blue);
        PropertyNode back = new BackgroundProperty(view);
        PropertyNode col = back.getChild("backgroundColor");
        col.setDefaultValue(UIManager.getColor("Button.background"));

        new CompBorder(view, UIManager.getBorder("Button.border"));
        new PropertyNode(view, "opaque", Types.BOOLEAN, null, false, null);
        PropertyNode lang =
                new PropertyNode(view, "language", Types.KRNOBJECT, null, false, null);
        lang.setKrnClass("Language", "code");

        //Поведение
        PropertyNode pov = new PropertyNode(this, "pov", -1, null, false, null);
        new ActivProperty(pov);
        //Поведение.Действие
        PropertyNode act = new PropertyNode(pov, "act", -1, null, false, null);
        new PropertyNode(act, "beforeOpen", Types.EXPR, null, false, null);
        new PropertyNode(act, "actionJobBefore", Types.EXPR, null, false, null);
        new PropertyNode(act, "actionJobAfter", Types.EXPR, null, false, null);

        PropertyNode callDialog = new PropertyNode(act, "callDialog",
                Types.KRNOBJECT, null, false, null);
        callDialog.setKrnClass("UI","title");
        EnumValue[] cash = new EnumValue[] {
            new EnumValue(Constants.CASH_SEPARATE, "Раздельный"),
            new EnumValue(Constants.CASH_GENERAL, "Общий"),
        };
        new PropertyNode(pov, "cashFlag", Types.ENUM, cash, false, null);
        new PropertyNode(pov, "ifcLock", Types.BOOLEAN, null, false, null);
        new PropertyNode(pov, "tabIndex", Types.INTEGER, null, false, null);
        new PropertyNode(pov, "dynamicIfc", Types.REF, null, false, null);
        new PropertyNode(pov, "dynamicIfcExpr", Types.EXPR, null, false, null);
        //Ограничения
        PropertyNode constr = new PropertyNode(this, "constraints", -1, null, false, null);
        new CompConstraints(constr);
        new PropertyNode(constr, "formatPattern", Types.STRING, null, false, new String("#.#"));
        //Обязательность
        new CompObligation(this);
    }
}
