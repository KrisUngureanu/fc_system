package kz.tamur.comps.models;

import static kz.tamur.comps.models.Types.*;

public class CheckBoxPropertyRoot extends PropertyRoot {
    public CheckBoxPropertyRoot() {
        super();
        new PropertyNode(this, "title", RSTRING, null, false, null);
        new PropertyNode(this, "description", STYLEDTEXT, null, false, null);
        new PropertyNode(this, "toolTip", HTML_TEXT, null, false, null);
        // Позиция
        new CompPosition(this);
        // Данные
        PropertyNode refs = new PropertyNode(this, "ref", -1, null, false, null);
        new PropertyNode(refs, "data", REF, null, false, null);
        new ParamFilters(refs);
        new PropertyNode(refs, "calcData", EXPR, null, false, null);
        // Вид
        PropertyNode view = new PropertyNode(this, "view", -1, null, false, null);
        new FontProperty(view);
        new BackgroundProperty(view);
        new PropertyNode(view, "opaque", BOOLEAN, null, false, Boolean.TRUE);
        PropertyNode lang = new PropertyNode(view, "language", KRNOBJECT, null, false, null);
        lang.setKrnClass("Language", "code");
        // Поведение
        PropertyNode pov = new PropertyNode(this, "pov", -1, null, false, null);
        new ActivProperty(pov);
        new PropertyNode(pov, "afterModAction", EXPR, null, false, null);
        new PropertyNode(pov, "tabIndex", INTEGER, null, false, null);
        //Обязательность
        new CompObligation(this);
   }
}
