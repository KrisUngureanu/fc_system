package kz.tamur.comps.models;

import static kz.tamur.comps.models.Types.BOOLEAN;
import static kz.tamur.comps.models.Types.COLOR;
import static kz.tamur.comps.models.Types.COMPONENT;
import static kz.tamur.comps.models.Types.ENUM;
import static kz.tamur.comps.models.Types.EXPR;
import static kz.tamur.comps.models.Types.FILTER;
import static kz.tamur.comps.models.Types.GRADIENT_COLOR;
import static kz.tamur.comps.models.Types.HTML_TEXT;
import static kz.tamur.comps.models.Types.IMAGE;
import static kz.tamur.comps.models.Types.INTEGER;
import static kz.tamur.comps.models.Types.PMENUITEM;
import static kz.tamur.comps.models.Types.PROCESSES;
import static kz.tamur.comps.models.Types.REF;
import static kz.tamur.comps.models.Types.RSTRING;
import static kz.tamur.comps.models.Types.STRING;
import static kz.tamur.comps.models.Types.STYLEDTEXT;

import java.awt.Color;

import javax.swing.UIManager;

import kz.tamur.comps.Constants;
import kz.tamur.util.DefaultsUtil;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 26.04.2004
 * Time: 18:48:17
 * To change this template use File | Settings | File Templates.
 */
public class TablePropertyRoot extends PropertyRoot {
    protected PropertyNode ref;
    protected PropertyNode pov;
    protected PropertyNode view;
    protected CompObligation obligation;

    public TablePropertyRoot() {
        super();
        new PropertyNode(this, "title", RSTRING, null, false, null);
        PropertyNode title1 = new PropertyNode(this, "title1", -1, null, false, null);
        new PropertyNode(title1, "font", Types.FONT, null, false, PropertyUtil.getDefaultTableTitleFont());
        new PropertyNode(title1, "fontColorCol", Types.COLOR, null, false, UIManager.getColor("TableHeader.foreground"));
        new PropertyNode(this, "addPanC", COMPONENT, null, false, null);
        new PropertyNode(this, "description", STYLEDTEXT, null, false, null);
        new PropertyNode(this, "toolTip", HTML_TEXT, null, false, null);
        new PropertyNode(this, "columns", COMPONENT, null, false, null);
        PropertyNode rowNums = new PropertyNode(this, "rowNums", -1, null, false, Boolean.FALSE);
        new PropertyNode(rowNums, "isVisibleNumRows", BOOLEAN, null, false, Boolean.FALSE);
        new PropertyNode(rowNums, "width", INTEGER, null, false, null);
        // Позиция
        new CompPosition(this);
        // Данные
        ref = new PropertyNode(this, "ref", -1, null, false, null);
        new PropertyNode(ref, "data", REF, null, false, null);
        new PropertyNode(ref, "deletedRef", REF, null, false, null);
        PropertyNode filt = new PropertyNode(ref, "filters", FILTER, null, true, null);
        filt.setKrnClass("PublicFilter", "title");
        PropertyNode defFilt = new PropertyNode(ref, "defaultFilter", FILTER, null, false, null);
        defFilt.setKrnClass("PublicFilter", "title");
        new ParamFilters(ref);
        // Вид
        view = new PropertyNode(this, "view", -1, null, false, null);
        // Гарнитура шрифта по умолчанию
        new FontProperty(view);
        PropertyNode back = new BackgroundProperty(view);
        PropertyNode zebra = new PropertyNode(back, "zebra", -1, null, false, null);
        new PropertyNode(zebra, "color1", COLOR, null, false, Color.white);
        new PropertyNode(zebra, "color2", COLOR, null, false, Color.white);
        new PropertyNode(back, "rowBackColorExpr", EXPR, null, false, null);
        new PropertyNode(back, "rowFontColorExpr", EXPR, null, false, null);
        new PropertyNode(back, "rowFontExpr", EXPR, null, false, null);

        EnumValue[] tvt = { new EnumValue(Constants.TABLE_VIEW_COMMON, "Обычный"), new EnumValue(Constants.TABLE_VIEW_ICON, "Иконочный")};
        new PropertyNode(view, "tableViewType", ENUM, tvt, false, Constants.TABLE_VIEW_COMMON);

        new PropertyNode(view, "showTitle", BOOLEAN, null, false, DefaultsUtil.getBooleanDefaultValue("table.property.showTitle", Boolean.TRUE)); 
        new PropertyNode(view, "showColHeader", BOOLEAN, null, false, Boolean.TRUE);
        new PropertyNode(view, "showPaging", BOOLEAN, null, false, DefaultsUtil.getBooleanDefaultValue("table.property.showPaging", Boolean.TRUE));
        new PropertyNode(view, "pageSize", INTEGER, null, false, 10); //TODO: pageSize
        new PropertyNode(view, "pageList", STRING, null, false, "10,20,30,40,50"); //TODO: pageList
        new PropertyNode(view, "fitColumns", BOOLEAN, null, false, Boolean.FALSE);
        new PropertyNode(view, "deleteRowColumn", BOOLEAN, null, false, Boolean.FALSE);
        new PropertyNode(view, "rowNowrap", BOOLEAN, null, false, Boolean.TRUE);

        PropertyNode navi = new PropertyNode(view, "navi", -1, null, false, null);
        new PropertyNode(navi, "show", BOOLEAN, null, false, null);
        new PropertyNode(navi, "addPan", BOOLEAN, null, false, null);
        EnumValue[] es = { new EnumValue(Constants.DIRECTION_RIGHT, "Вправо"), new EnumValue(Constants.DIRECTION_DOWN, "Вниз"), new EnumValue(Constants.DIRECTION_RIGHT_WITHOUT_ADD_ROW, "Вправо без добавления строки"), new EnumValue(Constants.DIRECTION_DOWN_WITHOUT_ADD_ROW, "Вниз без добавления строки"), };
        new PropertyNode(navi, "yesManDirection", ENUM, es, false, null);

        PropertyNode buttonsNode = new PropertyNode(navi, "buttons", -1, null, false, null);
        
        new PropertyNode(buttonsNode, "fastRepBtn", BOOLEAN, null, false, Boolean.FALSE);
		PropertyNode fastRepBtnProp = new PropertyNode(buttonsNode, "fastRepBtnProp", -1, null, false, null);
		new PropertyNode(fastRepBtnProp, "naviBtnTooltip", RSTRING, null, false, null);
		new PropertyNode(fastRepBtnProp, "naviBtnIcon", IMAGE, null, false, null);

        new PropertyNode(buttonsNode, "consalBtn", BOOLEAN, null, false, Boolean.FALSE);
        PropertyNode consalBtnProp = new PropertyNode(buttonsNode, "consalBtnProp", -1, null, false, null);
		new PropertyNode(consalBtnProp, "naviBtnTooltip", RSTRING, null, false, null);
		new PropertyNode(consalBtnProp, "naviBtnIcon", IMAGE, null, false, null);

        new PropertyNode(buttonsNode, "addBtn", BOOLEAN, null, false, Boolean.TRUE);
        PropertyNode addBtnProp = new PropertyNode(buttonsNode, "addBtnProp", -1, null, false, null);
		new PropertyNode(addBtnProp, "naviBtnTooltip", RSTRING, null, false, null);
		new PropertyNode(addBtnProp, "naviBtnIcon", IMAGE, null, false, null);
		
        new PropertyNode(buttonsNode, "delBtn", BOOLEAN, null, false, Boolean.TRUE);
		PropertyNode delBtnProp = new PropertyNode(buttonsNode, "delBtnProp", -1, null, false, null);
		new PropertyNode(delBtnProp, "naviBtnTooltip", RSTRING, null, false, null);
		new PropertyNode(delBtnProp, "naviBtnIcon", IMAGE, null, false, null);

        new PropertyNode(buttonsNode, "copyRowsBtn", BOOLEAN, null, false, Boolean.FALSE);
		PropertyNode copyRowsBtnProp = new PropertyNode(buttonsNode, "copyRowsBtnProp", -1, null, false, null);
		new PropertyNode(copyRowsBtnProp, "naviBtnTooltip", RSTRING, null, false, null);
		new PropertyNode(copyRowsBtnProp, "naviBtnIcon", IMAGE, null, false, null);

        new PropertyNode(buttonsNode, "yesManBtn", BOOLEAN, null, false, Boolean.FALSE);
		PropertyNode yesManBtnProp = new PropertyNode(buttonsNode, "yesManBtnProp", -1, null, false, null);
		new PropertyNode(yesManBtnProp, "naviBtnTooltip", RSTRING, null, false, null);
		new PropertyNode(yesManBtnProp, "naviBtnIcon", IMAGE, null, false, null);

        new PropertyNode(buttonsNode, "findBtn", BOOLEAN, null, false, Boolean.FALSE);
		PropertyNode findBtnProp = new PropertyNode(buttonsNode, "findBtnProp", -1, null, false, null);
		new PropertyNode(findBtnProp, "naviBtnTooltip", RSTRING, null, false, null);
		new PropertyNode(findBtnProp, "naviBtnIcon", IMAGE, null, false, null);

        new PropertyNode(buttonsNode, "filterBtn", BOOLEAN, null, false, Boolean.FALSE);
		PropertyNode filterBtnProp = new PropertyNode(buttonsNode, "filterBtnProp", -1, null, false, null);
		new PropertyNode(filterBtnProp, "naviBtnTooltip", RSTRING, null, false, null);
		new PropertyNode(filterBtnProp, "naviBtnIcon", IMAGE, null, false, null);

        new PropertyNode(buttonsNode, "downBtn", BOOLEAN, null, false, Boolean.FALSE);
		PropertyNode downBtnProp = new PropertyNode(buttonsNode, "downBtnProp", -1, null, false, null);
		new PropertyNode(downBtnProp, "naviBtnTooltip", RSTRING, null, false, null);
		new PropertyNode(downBtnProp, "naviBtnIcon", IMAGE, null, false, null);

        new PropertyNode(buttonsNode, "upBtn", BOOLEAN, null, false, Boolean.FALSE);
		PropertyNode upBtnProp = new PropertyNode(buttonsNode, "upBtnProp", -1, null, false, null);
		new PropertyNode(upBtnProp, "naviBtnTooltip", RSTRING, null, false, null);
		new PropertyNode(upBtnProp, "naviBtnIcon", IMAGE, null, false, null);

        new PropertyNode(buttonsNode, "showDelBtn", BOOLEAN, null, false, Boolean.FALSE);
		PropertyNode showDelBtnProp = new PropertyNode(buttonsNode, "showDelBtnProp", -1, null, false, null);
		new PropertyNode(showDelBtnProp, "naviBtnTooltip", RSTRING, null, false, null);
		new PropertyNode(showDelBtnProp, "naviBtnIcon", IMAGE, null, false, null);

        new PropertyNode(buttonsNode, "naviPane", BOOLEAN, null, false, false);
        new PropertyNode(buttonsNode, "naviSeparator", STRING, null, false, null);
        
        new BackgroundProperty(navi);
        new PropertyNode(view, "heightRow", INTEGER, null, false, 18);
        // Поведение
        pov = new PropertyNode(this, "pov", -1, null, false, null);
        new PropertyNode(pov, "autoRefresh", INTEGER, null, false, null);
        ActivProperty ap = new ActivProperty(pov);
        new PropertyNode(ap, "activDelExpr", EXPR, null, false, null);
        new PropertyNode(ap, "processes", PROCESSES, null, true, null);
        // Действие
        PropertyNode act = new PropertyNode(pov, "act", -1, null, false, null);
        new PropertyNode(act, "beforAdd", EXPR, null, false, null);
        new PropertyNode(act, "afterAdd", EXPR, null, false, null);
        new PropertyNode(act, "beforeDelete", EXPR, null, false, null);
        new PropertyNode(act, "afterDelete", EXPR, null, false, null);
        new PropertyNode(act, "afterCopy", EXPR, null, false, null);
        new PropertyNode(act, "afterMove", EXPR, null, false, null);
        new PropertyNode(pov, "multiselection", BOOLEAN, null, false, null);
        new PropertyNode(pov, "footer", BOOLEAN, null, false, null);
        EnumValue[] evs = { new EnumValue(Constants.FULL_ACCESS, "Полный"), new EnumValue(Constants.READ_ONLY_ACCESS, "Чтение"), new EnumValue(Constants.LAST_ROW_ACCESS, "Редакт. последней строки"), };
        new PropertyNode(pov, "access", ENUM, evs, false, null);
        new PropertyNode(pov, "maxObjectCount", INTEGER, null, false, null);
        new PropertyNode(pov, "maxObjectCountMessage", RSTRING, null, false, null);
        new PropertyNode(pov, "canSort", BOOLEAN, null, false, true);
        // Определяет формат диалога, который вызывается по кнопке фильтра
        EnumValue dlgType = new EnumValue(Constants.DIALOG, "Диалог");
        EnumValue[] filterF = { dlgType, new EnumValue(Constants.MENU_MULTI, "Меню-мультивыбор"), new EnumValue(Constants.MENU_SWITCH, "Меню-переключатель"), new EnumValue(Constants.MENU_TREE_MULTI, "Древовидное меню-мультивыбор"), new EnumValue(Constants.MENU_TREE_SWITCH, "Древовидное меню-переключатель") };
        new PropertyNode(pov, "filterBtnAttr", ENUM, filterF, false, dlgType);
        // Ограничения
        PropertyNode constr = new PropertyNode(this, "constraints", -1, null, false, null);
        new CompConstraints(constr);
        // Обязательность
        obligation = new CompObligation(this);
        new PropertyNode(this, "pmenu", PMENUITEM, null, true, null);
        PropertyNode extended = new PropertyNode(this, "extended", -1, null, false, null);
        new PropertyNode(extended, "gradient", GRADIENT_COLOR, null, false, null);
        new PropertyNode(extended, "transparent", BOOLEAN, null, false, false);
    }
}