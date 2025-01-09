package kz.tamur.comps.models;

import static kz.tamur.comps.models.Types.*;
import kz.tamur.comps.Constants;

import javax.swing.*;

import com.cifs.or2.client.Kernel;

public class TreeFieldPropertyRoot extends PropertyRoot {
    public TreeFieldPropertyRoot() {
        super();
        new PropertyNode(this, "title", STRING, null, false, null);
        new PropertyNode(this, "description", STYLEDTEXT, null, false, null);
        new PropertyNode(this, "toolTip", HTML_TEXT, null, false, null);
        // Позиция
        new CompPosition(this);
        // Данные
        PropertyNode refs = new PropertyNode(this, "ref", -1, null, false, null);
        PropertyNode data = new PropertyNode(refs, "data", REF, null, false, null);
        PropertyReestr.registerProperty(data);
        PropertyReestr.registerDebugProperty(data);
        PropertyNode root = new PropertyNode(refs, "rootRef", REF, null, false, null);
        PropertyReestr.registerDebugProperty(root);
        PropertyReestr.registerProperty(root);

        PropertyNode filt = new PropertyNode(refs, "defaultFilter", FILTER, null, false, null);
        filt.setKrnClass("PublicFilter", "title");

        PropertyNode treeTitle = new PropertyNode(refs, "titlePath", REF, null, false, null);
        PropertyReestr.registerDebugProperty(treeTitle);
        PropertyReestr.registerProperty(treeTitle);
        PropertyNode treeTitle2 = new PropertyNode(refs, "titlePath2", REF, null, false, null);
        PropertyReestr.registerDebugProperty(treeTitle2);
        PropertyReestr.registerProperty(treeTitle2);
        EnumValue[] env = new EnumValue[] {
                new EnumValue(Constants.RM_ONCE, "При первом откр. интерф."),
                new EnumValue(Constants.RM_ALWAYS, "При каждом откр. интерф."),
                new EnumValue(Constants.RM_DIRECTLY, "В зависим. от действ. польз.") };
        new PropertyNode(refs, "refreshMode", ENUM, env, false, null);
        new ParamFilters(refs);
        new PropertyNode(refs, "calcData", EXPR, null, false, null);
        new PropertyNode(refs, "valueRef", REF, null, false, null);
        new PropertyNode(refs, "parentRef", REF, null, false, null);
        new PropertyNode(refs, "childrenRef", REF, null, false, null);
        new PropertyNode(refs, "rootExpr", EXPR, null, false, null);
        EnumValue[] evc = { new EnumValue(Constants.SORT_ASCENDING, "По возрастанию"), new EnumValue(Constants.SORT_DESCENDING, "По убыванию"), };
        new PropertyNode(refs, "sortTreeData", Types.ENUM, evc, false, null);
        //Атрибут сортировки
        PropertyNode treeSort = new PropertyNode(refs, "sortPath", REF, null, false, null,Kernel.IC_INTEGER);
        PropertyReestr.registerDebugProperty(treeSort);
        PropertyReestr.registerProperty(treeSort);
        // Вид
        PropertyNode view = new PropertyNode(this, "view", -1, null, false, null);
        PropertyNode font = new FontProperty(view);
        new PropertyNode(font, "lightFontColor", COLOR, null, false, null);
        PropertyNode back = new BackgroundProperty(view);
        PropertyNode col = back.getChild("backgroundColor");
        col.setDefaultValue(UIManager.getColor("Button.background"));
        new CompBorder(view, UIManager.getBorder("Button.border"));
        new PropertyNode(view, "image", IMAGE, null, false, null);
        new PropertyNode(view, "opaque", BOOLEAN, null, false, null);
        new PropertyNode(view, "fullPath", BOOLEAN, null, false, null);
        new PropertyNode(view, "folderSelect", BOOLEAN, null, false, Boolean.FALSE);
        new PropertyNode(view, "folderAsLeaf", BOOLEAN, null, false, Boolean.FALSE);
        new PropertyNode(view, "showSearchLine", BOOLEAN, null, false, Boolean.TRUE);
        new PropertyNode(view, "combonotsorted", BOOLEAN, null, false, Boolean.TRUE);
        new PropertyNode(view, "clearBtnShow", BOOLEAN, null, false, false);
        EnumValue[] evs = {
                new EnumValue(SwingConstants.CENTER, "По центру"),
                new EnumValue(SwingConstants.LEFT, "Слева"),
                new EnumValue(SwingConstants.RIGHT, "Справа"), };
        new PropertyNode(view, "alignmentText", ENUM, evs, false, new Integer(SwingConstants.CENTER));
        PropertyNode lang = new PropertyNode(view, "language", KRNOBJECT, null, false, null);
        lang.setKrnClass("Language", "code");
        // Поведение
        PropertyNode pov = new PropertyNode(this, "pov", -1, null, false, null);
        new ActivProperty(pov);
        PropertyNode size = new PropertyNode(pov, "dialogSize", -1, null, false, null);
        new PropertyNode(size, "dialogWidth", INTEGER, null, false, new Integer(500));
        new PropertyNode(size, "dialogHeight", INTEGER, null, false, new Integer(500));
        new PropertyNode(pov, "beforeModAction", EXPR, null, false, null);
        new PropertyNode(pov, "afterModAction", EXPR, null, false, null);
        new PropertyNode(pov, "wClickAsOK", BOOLEAN, null, false, false);
        // Ограничения
        PropertyNode constr = new PropertyNode(this, "constraints", -1, null, false, null);
        new CompConstraints(constr);
        // Обязательность
        new CompObligation(this);
        new WebProperty(this);
    }
}
