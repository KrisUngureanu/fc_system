package kz.tamur.comps.models;

public class CheckColumnPropertyRoot extends ColumnPropertyRoot {
    public CheckColumnPropertyRoot() {
        CheckBoxPropertyRoot tfpr = new CheckBoxPropertyRoot();
        int cnt = tfpr.getChildCount();
        for (int i = 0; i < cnt; i++) {
            PropertyNode node = tfpr.getChildAt(i);
            String name = node.getName();
            if (!"pos".equals(name) && !"title".equals(name) && !"toolTip".equals(name)) {
                addChild(node);
            }
        }
        PropertyNode activity = getChild("pov").getChild("activity");
        activity.removeChild("radioGroup");
        new PropertyNode(activity, "uniqueSelection", Types.BOOLEAN, null, false, Boolean.FALSE);
        PropertyNode constr = getChild("constraints");
        if (constr != null) {
            new PropertyNode(constr.getChildCount() - 1, constr, "unique", Types.INTEGER, null, false, null);
        }
        PropertyNode view = tfpr.getChild("view");
        new PropertyNode(view.getChildCount() - 1, view, "defSummary", Types.BOOLEAN, null, false, Boolean.FALSE);
        PropertyNode refs = getChild("ref");
        PropertyNode data = new PropertyNode(refs, "treeDataRef", Types.REF, null, false, null);
        PropertyReestr.registerProperty(data);
        PropertyReestr.registerDebugProperty(data);
    }
}