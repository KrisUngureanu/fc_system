package kz.tamur.comps.models;

import static kz.tamur.comps.models.Types.BOOLEAN;
import static kz.tamur.comps.models.Types.EXPR;
import static kz.tamur.comps.models.Types.STRING;

import kz.tamur.comps.Constants;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 31.03.2006
 * Time: 11:01:18
 */
public class ActivProperty extends PropertyNode {
    public ActivProperty(PropertyNode parent) {
        super(parent, "activity", -1, null, false, null);
        new PropertyNode(this, "editable", BOOLEAN, null, false, false);
        new PropertyNode(this, "nocopy", BOOLEAN, null, false, false);
        new PropertyNode(this, "radioGroup", STRING, null, false, null);
        new PropertyNode(this, "activExpr", EXPR, null, false, null);
        new PropertyNode(this, "checkDisabled", BOOLEAN, null, false, false);
        new PropertyNode(this, "attention", EXPR, null, false, null);
        new PropertyNode(this, "inherit", Types.BOOLEAN, null, false, !Constants.IS_UL_PROJECT);
        new PropertyNode(parent, "isVisible", EXPR, null, false, null);
    }
}
