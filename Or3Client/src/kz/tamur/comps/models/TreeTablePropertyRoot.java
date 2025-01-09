package kz.tamur.comps.models;

import javax.swing.UIManager;

import com.cifs.or2.client.Kernel;

import kz.tamur.comps.Constants;
import static kz.tamur.comps.models.Types.*;

/**
 * Created by IntelliJ IDEA.
 * User: Администратор
 * Date: 29.11.2004
 * Time: 15:33:31
 */
public class TreeTablePropertyRoot extends TablePropertyRoot {
    public TreeTablePropertyRoot() {
        super();
        PropertyNode pos = getChild("pos");
        new PropertyNode(0, pos, "treeWidth", INTEGER, null, false, null);
        new PropertyNode(ref, "treeRef", REF, null, false, null);
        new PropertyNode(ref, "rootRef", REF, null, false, null);
        new PropertyNode(ref, "titlePath", REF, null, false, null);
        new PropertyNode(ref, "titlePath2", REF, null, false, null);
        new PropertyNode(ref, "titlePathExpr", EXPR, null, false, null);
        new PropertyNode(ref, "treeValueRef", REF, null, false, null);
        new PropertyNode(pov, "wClickAsOK", BOOLEAN, null, false, false);
        new PropertyNode(1, this, "treeTitle", RSTRING, null, false, null);
        PropertyNode title = new PropertyNode(2,this, "treeTitle1", -1, null, false, null);
        new PropertyNode(title, "font", Types.FONT, null, false, PropertyUtil.getDefaultComponentFont());
        new PropertyNode(title, "fontColorCol", Types.COLOR, null, false, UIManager.getColor("TableHeader.foreground"));
        PropertyNode filt = new PropertyNode(ref, "treeFilter", FILTER, null, false, null);
        filt.setKrnClass("PublicFilter", "title");
        //Атрибут сортировки
        PropertyNode treeSort = new PropertyNode(ref, "sortPath", REF, null, false, null,Kernel.IC_INTEGER);
        PropertyReestr.registerDebugProperty(treeSort);
        PropertyReestr.registerProperty(treeSort);
        //
        EnumValue[] env = new EnumValue[] {
                new EnumValue(Constants.RM_ONCE, "При первом откр. интерф."),
                new EnumValue(Constants.RM_ALWAYS, "При каждом откр. интерф."),
                new EnumValue(Constants.RM_DIRECTLY, "В зависим. от действ. польз.")
        };
        new PropertyNode(ref, "refreshMode", ENUM, env, false, null);

        new PropertyNode(ref, "valueRef", REF, null, false, null);
        new PropertyNode(ref, "parentRef", REF, null, false, null);
        new PropertyNode(ref, "childrenRef", REF, null, false, null);
        new PropertyNode(ref, "childrenExpr", EXPR, null, false, null);
        new PropertyNode(ref, "hasChildrenRef", REF, null, false, null);
        new PropertyNode(ref, "rootExpr", EXPR, null, false, null);

        PropertyNode lang = new PropertyNode(this, "language", KRNOBJECT, null, false, null);
        lang.setKrnClass("Language", "code");
        new PropertyNode(this, "image", IMAGE, null, false, null);
        new PropertyNode(view, "expandAll", BOOLEAN, null, false, false);
        new PropertyNode(view, "showEmpty", BOOLEAN, null, false, null);
        new PropertyNode(view, "hideRoot", BOOLEAN, null, false, false);
        new PropertyNode(view, "useCheck", BOOLEAN, null, false, false);
        new PropertyNode(view, "childrenSize", INTEGER, null, false, 0);

        EnumValue dfEnv1 = new EnumValue(Constants.FILES, "Файлы");
        EnumValue[] env1 = new EnumValue[] { dfEnv1, new EnumValue(Constants.PEOPLES, "Люди")};
        new PropertyNode(view, "viewType", ENUM, env1, false, dfEnv1);
        new PropertyNode(view, "folderAsLeaf", BOOLEAN, null, false, Boolean.FALSE);
        new PropertyNode(view, "showSearchLine", BOOLEAN, null, false, Boolean.FALSE);

        PropertyNode navi = new PropertyNode(view, "popup", -1, null, false, null);
        new PropertyNode(navi, "show", BOOLEAN, null, false, null);
        PropertyNode buttonsNode = new PropertyNode(navi, "items", -1, null, false, null);
        new PropertyNode(buttonsNode, "renameNode", BOOLEAN, null, false, true);
        new PropertyNode(buttonsNode, "changeNode", BOOLEAN, null, false, true);
        new PropertyNode(buttonsNode, "createNode", BOOLEAN, null, false, true);
        new PropertyNode(buttonsNode, "createAndBindNode", BOOLEAN, null, false, true);
        new PropertyNode(buttonsNode, "deleteNode", BOOLEAN, null, false, true);
        new PropertyNode(buttonsNode, "expandNode", BOOLEAN, null, false, true);
        new PropertyNode(buttonsNode, "collapseNode", BOOLEAN, null, false, true);

        new PropertyNode(obligation, "onlyLeaf", BOOLEAN, null, false, true);
    }
}
