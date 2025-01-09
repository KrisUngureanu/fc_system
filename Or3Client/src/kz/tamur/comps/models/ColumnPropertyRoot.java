package kz.tamur.comps.models;

import kz.tamur.comps.Constants;

import javax.swing.*;

public class ColumnPropertyRoot extends PropertyRoot {
    public ColumnPropertyRoot() {
        super();
        PropertyNode title = new PropertyNode(this, "header", -1, null, false, null);
        new PropertyNode(this, "toolTip", Types.HTML_TEXT, null, false, null);
        PropertyNode pn = new PropertyNode(title, "text", Types.RSTRING, null, false, null);
        PropertyReestr.registerProperty(pn);
        new PropertyNode(title, "editor", Types.HTML_TEXT, null, false, null);
        new PropertyNode(title, "font", Types.FONT, null, false, PropertyUtil.getDefaultComponentFont());
        new PropertyNode(title, "fontColorCol", Types.COLOR, null, false, UIManager.getColor("TableHeader.foreground"));
        new PropertyNode(title, "backgroundColorCol", Types.COLOR, null, false, UIManager.getColor("TableHeader.background"));
        new PropertyNode(title, "sorted", Types.BOOLEAN, null, false, null);
        EnumValue[] evs = { new EnumValue(Constants.SORT_ASCENDING, "По возрастанию"), new EnumValue(Constants.SORT_DESCENDING, "По убыванию"), };
        new PropertyNode(title, "sortingDirection", Types.ENUM, evs, false, null);
        new PropertyNode(title, "sortingIndex", Types.INTEGER, evs, false, 0);
        new PropertyNode(title, "canSort", Types.BOOLEAN, null, false, true);
        // Поворот заголовка столбца 
        EnumValue defaultEvr = new EnumValue(Constants.DONT_ROTATE, "Без поворота");
        EnumValue[] evr = { new EnumValue(Constants.ROTATE_RIGHT, "Вправо"), defaultEvr, new EnumValue(Constants.ROTATE_LEFT, "Влево"), };
        new PropertyNode(title, "rotation", Types.ENUM, evr, false, defaultEvr);
        new ColumnPosition(this);
    }
}