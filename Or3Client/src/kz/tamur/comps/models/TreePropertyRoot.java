package kz.tamur.comps.models;

import static kz.tamur.comps.models.Types.*;
import kz.tamur.comps.Constants;

import javax.swing.*;

import com.cifs.or2.client.Kernel;

public class TreePropertyRoot extends PropertyRoot {
    public TreePropertyRoot() {
        super();
        new PropertyNode(this, "title", STRING, null, false, null);
        new PropertyNode(this, "description", STYLEDTEXT, null, false, null);
        new PropertyNode(this, "toolTip", HTML_TEXT, null, false, null);
        
        // Позиция
        new CompPosition(this);
       
        // Данные
        PropertyNode refs = new PropertyNode(this, "ref", -1, null, false, null);
        new PropertyNode(refs, "data", REF, null, false, null);
        PropertyNode root = new PropertyNode(refs, "rootRef", REF, null, false, null);

        PropertyNode filt = new PropertyNode(refs, "defaultFilter", FILTER, null, false, null);
        filt.setKrnClass("PublicFilter", "title");

        PropertyReestr.registerDebugProperty(root);
        PropertyReestr.registerProperty(root);
        new PropertyNode(refs, "rootRefUID", KRNOBJECT, null, false, null);
        PropertyNode treeTitle = new PropertyNode(refs, "titlePath", REF, null, false, null);
        PropertyReestr.registerDebugProperty(treeTitle);
        PropertyReestr.registerProperty(treeTitle);
        PropertyNode treeTitle2 = new PropertyNode(refs, "titlePath2", REF, null, false, null);
        PropertyReestr.registerDebugProperty(treeTitle2);
        PropertyReestr.registerProperty(treeTitle2);
        PropertyNode titlePathExpr = new PropertyNode(refs, "titlePathExpr", EXPR, null, false, null);
        PropertyReestr.registerDebugProperty(titlePathExpr);
        PropertyReestr.registerProperty(titlePathExpr);
        PropertyNode treeSort = new PropertyNode(refs, "sortPath", REF, null, false, null,Kernel.IC_INTEGER);
        PropertyReestr.registerDebugProperty(treeSort);
        PropertyReestr.registerProperty(treeSort);
        EnumValue[] env = new EnumValue[] { new EnumValue(Constants.RM_ONCE, "При первом откр. интерф."),
                new EnumValue(Constants.RM_ALWAYS, "При каждом откр. интерф."),
                new EnumValue(Constants.RM_DIRECTLY, "В зависим. от действ. польз.") };
        new PropertyNode(refs, "refreshMode", ENUM, env, false, null);
        new ParamFilters(refs);

        new PropertyNode(refs, "valueRef", REF, null, false, null);
        new PropertyNode(refs, "parentRef", REF, null, false, null);
        new PropertyNode(refs, "childrenRef", REF, null, false, null);
        new PropertyNode(refs, "childrenExpr", EXPR, null, false, null);
        new PropertyNode(refs, "rootExpr", EXPR, null, false, null);

        // Вид
        PropertyNode view = new PropertyNode(this, "view", -1, null, false, null);
        new FontProperty(view);
        PropertyNode background = new BackgroundProperty(view);
        PropertyNode backColor = background.getChild("backgroundColor");
        backColor.setDefaultValue(PropertyUtil.getLightSysColor());

        new CompBorder(view, UIManager.getBorder("TextField.border"));
        new PropertyNode(view, "image", IMAGE, null, false, null);
        new PropertyNode(view, "wrapNodeContent", BOOLEAN, null, false, Boolean.FALSE);
        new PropertyNode(view, "folderSelect", BOOLEAN, null, false, Boolean.FALSE);
        new PropertyNode(view, "folderAsLeaf", BOOLEAN, null, false, Boolean.FALSE);
        new PropertyNode(view, "showSearchLine", BOOLEAN, null, false, Boolean.TRUE);
        new PropertyNode(view, "combonotsorted", Types.BOOLEAN, null, false, Boolean.TRUE);

        PropertyNode lang = new PropertyNode(refs, "language", KRNOBJECT, null, false, null);
        lang.setKrnClass("Language", "code");

        PropertyNode navi = new PropertyNode(view, "popup", -1, null, false, null);
        new PropertyNode(navi, "show", BOOLEAN, null, false, null);
        PropertyNode buttonsNode = new PropertyNode(navi, "items", -1, null, false, null);
        new PropertyNode(buttonsNode, "renameNode", BOOLEAN, null, false, Boolean.TRUE);
        new PropertyNode(buttonsNode, "changeNode", BOOLEAN, null, false, Boolean.TRUE);
        new PropertyNode(buttonsNode, "createNode", BOOLEAN, null, false, Boolean.TRUE);
        new PropertyNode(buttonsNode, "createAndBindNode", BOOLEAN, null, false, Boolean.TRUE);
        new PropertyNode(buttonsNode, "deleteNode", BOOLEAN, null, false, Boolean.TRUE);
        new PropertyNode(buttonsNode, "expandNode", BOOLEAN, null, false, Boolean.TRUE);
        new PropertyNode(buttonsNode, "collapseNode", BOOLEAN, null, false, Boolean.TRUE);

        EnumValue dfEnv1 = new EnumValue(Constants.FILES, "Файлы");
        EnumValue[] env1 = new EnumValue[] { dfEnv1, new EnumValue(Constants.PEOPLES, "Люди")};
        new PropertyNode(view, "viewType", ENUM, env1, false, dfEnv1);
        
        // Поведение
        PropertyNode pov = new PropertyNode(this, "pov", -1, null, false, null);
        new ActivProperty(pov);
        new PropertyNode(pov, "tabIndex", INTEGER, null, false, null);
        new PropertyNode(pov, "beforeModAction", EXPR, null, false, null);
        new PropertyNode(pov, "afterModAction", EXPR, null, false, null);
        new PropertyNode(pov, "wClickAsOK", BOOLEAN, null, false, false);
        new PropertyNode(pov, "beforeDelete", EXPR, null, false, null);
        new PropertyNode(pov, "afterDelete", EXPR, null, false, null);
        new PropertyNode(pov, "multiselection", BOOLEAN, null, false, false);
        new PropertyNode(pov, "rootChecked", BOOLEAN, null, false, true);
        // Ограничения
        PropertyNode constr = new PropertyNode(this, "constraints", -1, null, false, null);
        new CompConstraints(constr);
        
        // Обязательность
        new CompObligation(this);
        PropertyNode extended = new PropertyNode(this, "extended", -1, null, false, null);
        new PropertyNode(extended, "transparent", BOOLEAN, null, false, false);
    }
}
