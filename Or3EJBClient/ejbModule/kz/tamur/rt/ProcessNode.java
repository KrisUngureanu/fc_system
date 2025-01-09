package kz.tamur.rt;

import com.cifs.or2.kernel.*;
import com.cifs.or2.client.*;
import com.cifs.or2.util.MMap;
import java.util.*;
import javax.swing.tree.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 18.02.2005
 * Time: 11:04:37
 * To change this template use File | Settings | File Templates.
 */
public class ProcessNode extends DefaultMutableTreeNode {

    private Comparator foldersLeafsComparator = new FoldersAndLeafsComparator();
    protected boolean isLoaded = false;
    protected ProcessObject srvObj;
    protected MMap<Long, ProcessObject, ? extends List<ProcessObject>> map;

    public ProcessNode(MMap<Long, ProcessObject, ? extends List<ProcessObject>> map_,ProcessObject srvObj_) {
        map = map_;
        srvObj = srvObj_;
        isLoaded = false;
    }

    public String getDesc() {
        return srvObj.getDescription();
    }

    public boolean isLeaf() {
        return srvObj.obj.classId == Kernel.SC_PROCESS_DEF.id;
    }

    public int getChildCount() {
        load();
        return super.getChildCount();
    }

    public String toString() {
        return srvObj.toString();
    }

    public KrnObject getKrnObject(){
        return srvObj.obj;
    }
    protected void load() {
        if (!isLoaded) {
            isLoaded = true;
            if (!isLeaf()) {
                List<ProcessObject> objs = map.get(new Long(srvObj.obj.id));
                if (objs != null) {
	                Collections.sort(objs, new Comparator<ProcessObject>() {
	                    public int compare(ProcessObject o1, ProcessObject o2) {
	                        if (o1 == null) {
	                            return -1;
	                        } else if (o2 == null) {
	                            return 1;
	                        } else {
	                            return (o1.index < o2.index) ? -1 : 1;
	                        }
	                    }
	                });
                    List children = new ArrayList();
                    for (ProcessObject obj : objs) {
                    	if(!(obj instanceof ProcessFolderObject) || !((ProcessFolderObject)obj).isTab)
                    		children.add(new ProcessNode(map, obj));
                    }
                    addAllChildren(children);
                }
            }
        }
    }

    public void addAllChildren(List children) {
        Collections.sort(children, foldersLeafsComparator);
        for (int i = 0; i < children.size(); i++) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)children.get(i);
            add(node);
        }
    }

    class FoldersAndLeafsComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            DefaultMutableTreeNode n1 = (DefaultMutableTreeNode)o1;
            DefaultMutableTreeNode n2 = (DefaultMutableTreeNode)o2;
            if (n1 == null) {
                return -1;
            } else if (n2 == null) {
                return 1;
            } else {
                if (n1.isLeaf() && !n2.isLeaf()) {
                    return 1;
                } else if (!n1.isLeaf() && n2.isLeaf()) {
                    return -1;
                }
                return 0;
            }
        }
    }
}
