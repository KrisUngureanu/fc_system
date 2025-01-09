package kz.tamur.guidesigner.noteeditor;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.*;
import kz.tamur.util.AbstractDesignerTreeNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 13.10.2004
 * Time: 11:31:34
 * To change this template use File | Settings | File Templates.
 */
public class NoteNode extends AbstractDesignerTreeNode {

    private static KrnClass noteCls;

    public NoteNode(KrnObject noteObj, String title, long langId, int index) {
        krnObj = noteObj;
        isLoaded = false;
        this.title = title;
        this.langId = langId;
    }


    public boolean isLeaf() {
        if (noteCls == null) {
            try {
                noteCls = Kernel.instance().getClassByName("Note");
            } catch (KrnException e) {
                e.printStackTrace();
            }
        }
        return krnObj.classId == noteCls.id;
    }


    protected void load() {
        if (!isLoaded) {
            isLoaded = true;
            if (!isLeaf()) {
                final Kernel krn = Kernel.instance();
                long[] oids = {krnObj.id};
                ObjectValue[] ovs = new ObjectValue[0];
                try {
                    ovs = krn.getObjectValues(oids, krnObj.classId, "children", 0);
                    long[] ids = new long[ovs.length];
                    for (int i = 0; i < ovs.length; i++) {
                        ids[i] = ovs[i].value.id;
                    }
                    StringValue[] strs = krn.getStringValues(ids, krnObj.classId,
                            "title", langId, false, 0);
                    Arrays.sort(ovs, new Comparator() {
                        public int compare(Object o1, Object o2) {
                            ObjectValue ov1 = (ObjectValue) o1;
                            ObjectValue ov2 = (ObjectValue) o2;
                            if (ov1 == null) {
                                return -1;
                            } else if (ov2 == null) {
                                return 1;
                            } else {
                                return (ov1.index < ov2.index) ? -1 : 1;
                            }
                        }
                    });
                    List children = new ArrayList();
                    for (int i = 0; i < ovs.length; i++) {
                        ObjectValue ov = ovs[i];
                        String title = "Безымянный";
                        for (int j = 0; j < strs.length; j++) {
                            if (strs[j].objectId == ov.value.id) {
                                title = strs[j].value;
                                break;
                            }
                        }
                        children.add(new NoteNode(ov.value, title, langId, ov.index));
                    }
                    addAllChildren(children);
                } catch (KrnException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean equals(Object obj) {
        if (obj instanceof NoteNode) {
            NoteNode node = (NoteNode)obj;
            return node.krnObj.equals(this.krnObj) ;
        }
        return false;
    }


    public String getTitle() {
        return title;
    }
}
