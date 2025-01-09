package kz.tamur.comps.models;

import javax.swing.*;

public class RichTextEditorPropertyRoot extends PropertyRoot{
	public RichTextEditorPropertyRoot () {
		
		super();
        PropertyNode title = new PropertyNode(this, "title", Types.RSTRING, null, false, null);
        new PropertyNode(this, "description", Types.STYLEDTEXT, null, false, null);
        new PropertyNode(this, "toolTip", Types.HTML_TEXT, null, false, null);
        PropertyReestr.registerProperty(title);
        //Позиция
        new CompPosition(this);
        //Данные
        PropertyNode refs = new PropertyNode(this, "ref", -1, null, false, null);
            PropertyNode data = new PropertyNode(refs, "data", Types.REF, null, false, null);
            PropertyReestr.registerProperty(data);
            PropertyReestr.registerDebugProperty(data);
        new ParamFilters(refs);
        new PropertyNode(refs, "calcData", Types.EXPR, null, false, null);
        //Вид
        PropertyNode view = new PropertyNode(this, "view", -1, null, false, null);
        
        PropertyNode lang = new PropertyNode(view, "language", Types.KRNOBJECT, null, false, null);
        lang.setKrnClass("Language", "code");
        //Поведение
        PropertyNode pov = new PropertyNode(this, "pov", -1, null, false, null);
        new ActivProperty(pov);
        new CopyProperty(pov);
        new PropertyNode(pov, "beforeModAction", Types.EXPR, null, false, null);
        new PropertyNode(pov, "afterModAction", Types.EXPR, null, false, null);
        new PropertyNode(pov, "lineWrap", Types.BOOLEAN, null, false, Boolean.TRUE);
        new PropertyNode(pov, "wrapStyleWord", Types.BOOLEAN, null, false, Boolean.TRUE);
        new PropertyNode (pov, "tabIndex", Types.INTEGER, null, false, null);
        //Ограничения
        PropertyNode constr = new PropertyNode(this, "constraints", -1, null, false, null);
        new CompConstraints(constr);
        //
        new CompObligation(this);
		
	}
}
