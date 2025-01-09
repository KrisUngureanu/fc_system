package kz.tamur.guidesigner;

import com.cifs.or2.kernel.KrnObject;

import kz.tamur.util.AbstractDesignerTreeNode;
import kz.tamur.util.ServiceControlNode;

/**
 * User: vital
 * Date: 30.11.2004
 * Time: 18:01:05
 */
public class KrnObjectPattern implements FindPattern {
    private KrnObject obj;
    private boolean searchOnObject = false;

    public KrnObjectPattern(KrnObject obj) {
        this.obj = obj;
    }

    public boolean isMatches(Object obj) {
        if (!searchOnObject && obj instanceof ServiceControlNode) {
            ServiceControlNode node = (ServiceControlNode)obj;
            return (node.getValue() != null && this.obj != null && node.getValue().id == this.obj.id);
        }else if (obj instanceof AbstractDesignerTreeNode) {
            AbstractDesignerTreeNode node = (AbstractDesignerTreeNode)obj;
            return (node.getKrnObj() != null && this.obj != null && node.getKrnObj().id == this.obj.id);
        }
        return false;
    }

    /**
     * @return the searchOnObject
     */
    public boolean isSearchOnObject() {
        return searchOnObject;
    }

    /**
     * @param searchOnObject the searchOnObject to set
     */
    public void setSearchOnObject(boolean searchOnObject) {
        this.searchOnObject = searchOnObject;
    }
}
