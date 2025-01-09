package kz.tamur.util;

import com.cifs.or2.kernel.KrnObject;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.util.Comparator;
import java.util.List;
import java.util.Collections;
import java.util.Enumeration;

import kz.tamur.rt.TreeUIDMap;
import kz.tamur.rt.Utils;

/**
 * User: vital
 * Date: 31.01.2005
 * Time: 18:39:24
 */
public abstract class AbstractDesignerTreeNode extends
        DefaultMutableTreeNode implements DesignerTreeNode {
	public static final int SERVICE_NODE = 1;
	public static final int INTERFACE_NODE = 2;
	public static final int FILTER_NODE = 3;
	public static final int REPORT_NODE = 4;
	public static final int USER_NODE = 5;
	public static final int BOX_NODE = 6;

    protected boolean isLoaded = false;
    protected KrnObject krnObj;
    protected String title;
    protected String oldName;
    protected long langId;
    protected boolean isCopyProcessStarted = false;
    protected boolean isCutProcess = false;
    protected int nodeType = 0;

    protected AbstractDesignerTreeNode() {}

    protected abstract void load();
    
    public int getNodeType() {
    	return nodeType;
    }

    // Добавляем в узел детей, предварительно отсортировав их согласно правилу компаратора
    public void addAllChildren(List<? extends AbstractDesignerTreeNode> children, Comparator<AbstractDesignerTreeNode> comparator) {
        Collections.sort(children, comparator);
        for (int i = 0; i < children.size(); i++) {
            AbstractDesignerTreeNode node = (AbstractDesignerTreeNode)children.get(i);
            add(node);
        }
    }
    
    // по умолчанию компаратором является Utils.getFolderAndLeafsComparator()
    public void addAllChildren(List<? extends AbstractDesignerTreeNode> children) {
    	// сортировка всех потомков исключая папки
    	addAllChildren(children, Utils.getFolderAndLeafsComparator());
    }

    public Enumeration children() {
        load();
        return super.children();
    }
    
    public List<DesignerTreeNode> children(boolean asList) {
        load();
        return super.children;
    }

    public int getChildCount() {
        load();
        return super.getChildCount();
    }

    public int getLoadedChildCount() {
        return super.getChildCount();
    }

    public String toString() {
        return title;
    }
    
    public String getTitle() {
    	return title;
    }

    public KrnObject getKrnObj() {
        return krnObj;
    }

    public boolean equals(Object obj) {
        if (obj instanceof AbstractDesignerTreeNode) {
            AbstractDesignerTreeNode node = (AbstractDesignerTreeNode)obj;
            if (krnObj != null)
            	return krnObj.id == node.getKrnObj().id;
            else 
            	return toString().equals(node.toString());
        }
        return false;
    }

    public TreePath find(KrnObject obj) {
        if (obj == null) {
            return null;
        }
        if (krnObj.id == obj.id) {
            return new TreePath(getPath());
        }
        load();
        TreePath result = null;
        for (Enumeration c = children(); c.hasMoreElements();) {
            AbstractDesignerTreeNode child =
                    (AbstractDesignerTreeNode) c.nextElement();
            result = child.find(obj);
            if (result != null)
                break;
        }
        return result;
    }

    public void rename(String newName) {
        title = newName;
    }
    
    public String getOldName() {
        return oldName;
    }

    public void rename() {
        oldName = title;
    }

    public long getLangId() {
        return langId;
    }

    public void setLangId(int langId) {
        this.langId = langId;
    }

    public boolean isCopyProcessStarted() {
        return isCopyProcessStarted;
    }

    public void setCopyProcessStarted(boolean copyProcessStarted) {
        isCopyProcessStarted = copyProcessStarted;
    }

    public boolean isCutProcess() {
        return isCutProcess;
    }

    public void setCutProcess(boolean cutProcess) {
        isCutProcess = cutProcess;
    }


}
