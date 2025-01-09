package kz.tamur.comps.models;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 23.04.2004
 * Time: 12:25:24
 * To change this template use File | Settings | File Templates.
 */
public class SliderPropertyRoot extends PropertyRoot {
    public SliderPropertyRoot() {
        super();
        new PropertyNode(this, "title", Types.STRING, null, false, null);
        new PropertyNode(this, "description", Types.STYLEDTEXT, null, false, null);
        new PropertyNode(this, "toolTip", Types.HTML_TEXT, null, false, null);
        //Позиция
        new CompPosition(this);
        //Данные
        PropertyNode refs = new PropertyNode(this, "ref", -1, null, false, null);
            new PropertyNode(refs, "data", Types.REF, null, false, null);
        //Вид
        PropertyNode view = new PropertyNode(this, "view", -1, null, false, null);
        new FontProperty(view);
        new BackgroundProperty(view);
        new CompBorder(view, UIManager.getBorder("TextField.border"));
        //Поведение
        PropertyNode pov = new PropertyNode(this, "pov", -1, null, false, null);
        new ActivProperty(pov);
        PropertyNode slider = new PropertyNode(pov, "slider", -1, null, false, null);
        EnumValue[] evs = new EnumValue[] {
            new EnumValue(JSlider.HORIZONTAL, "Горизонтальный"),
            new EnumValue(JSlider.VERTICAL, "Вертикальный")};
            new PropertyNode(slider, "sliderOrientation", Types.ENUM, evs, false,
                new Integer(JSlider.HORIZONTAL));
            new PropertyNode(slider, "sliderLabels", Types.BOOLEAN, null, false, null);
            new PropertyNode(slider, "sliderTicks", Types.BOOLEAN, null, false, null);
            new PropertyNode(slider, "sliderSnapTicks", Types.BOOLEAN, null, false, null);
            new PropertyNode(slider, "sliderTrack", Types.BOOLEAN, null, false, Boolean.TRUE);
            PropertyNode ticks = new PropertyNode(slider, "ticks", -1, null, false, null);
                new PropertyNode(ticks, "sliderMin", Types.INTEGER, null, false, null);
                new PropertyNode(ticks, "sliderMax", Types.INTEGER, null, false, null);
                new PropertyNode(ticks, "sliderStepMin", Types.INTEGER, null, false, null);
                new PropertyNode(ticks, "sliderStepMax", Types.INTEGER, null, false, null);
        new PropertyNode (pov, "tabIndex", Types.INTEGER, null, false, null);
        //Ограничения
        PropertyNode constr = new PropertyNode(this, "constraints", -1, null, false, null);
        new PropertyNode(constr, "charsNumber", Types.INTEGER, null, false, null);
        new CompConstraints(constr);
        //Обязательность
        new CompObligation(this);
        
    }
}
