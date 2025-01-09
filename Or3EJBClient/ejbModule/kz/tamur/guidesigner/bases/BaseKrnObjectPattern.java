package kz.tamur.guidesigner.bases;

import com.cifs.or2.kernel.KrnObject;
import kz.tamur.guidesigner.FindPattern;

/**
 * User: vital
 * Date: 30.11.2004
 * Time: 18:01:05
 */
public class BaseKrnObjectPattern implements FindPattern {
    private KrnObject obj;

    public BaseKrnObjectPattern(KrnObject obj) {
        this.obj = obj;
    }

    public boolean isMatches(Object obj) {
        if (obj instanceof BaseNode) {
            BaseNode node = (BaseNode)obj;
            return (node.getKrnObj().id == this.obj.id);
        }
        return false;
    }
}
