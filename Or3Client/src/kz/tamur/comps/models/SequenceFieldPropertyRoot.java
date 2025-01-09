package kz.tamur.comps.models;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 23.04.2004
 * Time: 12:25:24
 * To change this template use File | Settings | File Templates.
 */
public class SequenceFieldPropertyRoot extends PropertyRoot {
    public SequenceFieldPropertyRoot() {
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
        new CompBorder(view, UIManager.getBorder("Panel.border"));
        PropertyNode lang =
                new PropertyNode(view, "language", Types.KRNOBJECT, null, false, null);
        lang.setKrnClass("Language", "code");
        //Поведение
        PropertyNode pov = new PropertyNode(this, "pov", -1, null, false, null);
        new ActivProperty(pov);
        PropertyNode seq = new PropertyNode(pov, "sequences", -1,
                    null, false, null);
            PropertyNode sequence = new PropertyNode(seq, "sequence", Types.SEQUENCE,
                    null, false, null);
            sequence.setKrnClass("Sequence", "name");
            new PropertyNode(seq, "seqPrefix", Types.EXPR, null, false, null);
            new PropertyNode(seq, "strikes", Types.BOOLEAN, null, false, null);
            new PropertyNode(seq, "showPrefix", Types.BOOLEAN, null, false, null);
        new PropertyNode(pov, "afterModAction", Types.EXPR, null, false, null);
        new PropertyNode (pov, "tabIndex", Types.INTEGER, null, false, null);
        //Ограничения
        PropertyNode constr = new PropertyNode(this, "constraints", -1, null, false, null);
        new CompConstraints(constr);
        //Обязательность
        new CompObligation(this);
    }
}
