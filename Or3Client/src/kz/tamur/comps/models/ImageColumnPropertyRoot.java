package kz.tamur.comps.models;

import kz.tamur.comps.Constants;

/**
 * Класс свойств для столбца с иконкам.
 * 
 * @author Sergey Lebedev
 */
public class ImageColumnPropertyRoot extends ColumnPropertyRoot {
    public ImageColumnPropertyRoot() {
        LabelPropertyRoot lpr = new LabelPropertyRoot(this);
        new PropertyNode(1, this, "toolTip", Types.HTML_TEXT, null, false, null);
        PropertyNode refs = new PropertyNode(this, "ref", -1, null, false, null);
        PropertyNode data = new PropertyNode(refs, "data", Types.REF, null, false, null);
        PropertyReestr.registerProperty(data);
        PropertyReestr.registerDebugProperty(data);
        new PropertyNode(refs, "calcData", Types.EXPR, null, false, null);
        EnumValue[] env = new EnumValue[] {new EnumValue(Constants.SUMMARY_NO, "Отсутствует"), new EnumValue(Constants.SUMMARY_COUNT, "Количество")};
        new PropertyNode(lpr.view.getChildCount(), lpr.view, "summary", Types.ENUM, env, false, null);
        new BackgroundProperty(lpr.view);
    }
}