package kz.tamur.guidesigner;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.*;

import kz.tamur.rt.TreeUIDMap;
import kz.tamur.util.AbstractDesignerTreeNode;

import java.util.*;

import static com.cifs.or2.client.Kernel.SC_UI;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 13.10.2004
 * Time: 11:31:34
 * To change this template use File | Settings | File Templates.
 */
public class InterfaceNode extends AbstractDesignerTreeNode {

    public InterfaceNode(KrnObject uiObj, String title, long langId) {
    	nodeType = INTERFACE_NODE;
        krnObj = uiObj;
        isLoaded = false;
        this.title = title;
        this.langId = langId;
    }

    public boolean isLeaf() {
        return krnObj.classId == SC_UI.id;
    }

    protected void load() {
        if (!isLoaded) {
            isLoaded = true;
            if (!isLeaf()) {
                final Kernel krn =  Kernel.instance();
                long[] oids = {krnObj.id};
                ObjectValue[] ovs = new com.cifs.or2.kernel.ObjectValue[0];
                try {
                    ovs = krn.getObjectValues(oids,krnObj.classId, "children", 0);
                    long[] ids = new long[ovs.length];
                    for (int i = 0; i < ovs.length; i++) {
                        ids[i] = ovs[i].value.id;
                    }
                    StringValue[] strs = krn.getStringValues(ids, krnObj.classId,
                            "title", langId, false, 0);
                    final Map<Long,String> str_m=new HashMap<Long,String>();
                    for(StringValue str:strs){
                         str_m.put(str.objectId,str.value);
                    }
                    Arrays.sort(ovs, new Comparator<ObjectValue>() {
                        public int compare(ObjectValue ov1, ObjectValue ov2) {
                            if (ov1 == null) return -1;
                            else if (ov2 == null) return 1;
                            else {
                                String p1=str_m.get(ov1.value.id);
                                String p2=str_m.get(ov2.value.id);
                                if (p1 == null)
                                    return -1;
                                else if (p2 == null)
                                    return 1;
                                else
                                    return p1.compareTo(p2);
                            }
                        }
                    });
                    List children = new ArrayList();
                    for (int i = 0; i < ovs.length; i++) {
                        ObjectValue ov = ovs[i];
                        String title = str_m.get(ov.value.id);
                        if(title==null || title.equals(""))
                            title="Безымянный";
                        InterfaceNode childNode = new InterfaceNode(ov.value, title, langId);
                        TreeUIDMap.put(((KrnObject) ov.value).uid, childNode);
                        children.add(childNode);
                    }
                    addAllChildren(children);
                } catch (KrnException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
