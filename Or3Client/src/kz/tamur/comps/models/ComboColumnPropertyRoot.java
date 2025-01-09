package kz.tamur.comps.models;

import kz.tamur.comps.Constants;

public class ComboColumnPropertyRoot extends ColumnPropertyRoot {
    public ComboColumnPropertyRoot() {
        ComboBoxPropertyRoot ffpr = new ComboBoxPropertyRoot();
        int cnt = ffpr.getChildCount();
        for (int i = 0; i < cnt; i++) {
            PropertyNode node = ffpr.getChildAt(i);
            String name = node.getName();
            if (!"pos".equals(name) && !"title".equals(name) && !"border".equals(name) && !"toolTip".equals(name)) {
                addChild(node);
            }
        }
        PropertyNode view = ffpr.getChild("view");
        EnumValue[] env = new EnumValue[] {
            new EnumValue(Constants.SUMMARY_NO, "Отсутствует"),
            new EnumValue(Constants.SUMMARY_COUNT, "Количество"),
        };
        new PropertyNode(view.getChildCount(), view, "summary", Types.ENUM, env, false, null);
        PropertyNode constr = getChild("constraints");
        if (constr != null) {
            new PropertyNode(constr.getChildCount() - 1, constr, "unique", Types.INTEGER, null, false, null);
        }
        PropertyNode refs = getChild("ref");
        PropertyNode data = new PropertyNode(refs, "treeDataRef", Types.REF, null, false, null);
        PropertyReestr.registerProperty(data);
        PropertyReestr.registerDebugProperty(data);
    }
}