package kz.tamur.guidesigner.xmldesigner;

import com.cifs.or2.kernel.*;
import com.cifs.or2.client.Kernel;
import java.util.*;
import java.io.UnsupportedEncodingException;

import kz.tamur.util.AbstractDesignerTreeNode;


/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 13.10.2004
 * Time: 11:31:34
 * To change this template use File | Settings | File Templates.
 */
public class XmlNode extends AbstractDesignerTreeNode {

    private static KrnClass funcCls;

    private boolean isModify = false;
    private String expressionText = "";

    public XmlNode(KrnObject uiObj, String title, int index) {
        krnObj = uiObj;
        isLoaded = false;
        this.title = title;
        Kernel krn = Kernel.instance();
        try {
            byte[] data = krn.getBlob(krnObj, "text", 0, 0, 0);
                expressionText = new String(data, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isLeaf() {
        if (funcCls == null) {
            try {
                funcCls = Kernel.instance().getClassByName("Func");
            } catch (KrnException e) {
                e.printStackTrace();
            }
        }
        return krnObj.classId == funcCls.id;
    }



    protected void load() {
        if (!isLoaded) {
            isLoaded = true;
            if (!isLeaf()) {
                final Kernel krn =  Kernel.instance();
                long[] oids = {krnObj.id};
                ObjectValue[] ovs = new ObjectValue[0];
                try {
                    ovs = krn.getObjectValues(oids,krnObj.classId, "children", 0);
                    long[] ids = new long[ovs.length];
                    for (int i = 0; i < ovs.length; i++) {
                        ids[i] = ovs[i].value.id;
                    }
                    StringValue[] strs = krn.getStringValues(ids, krnObj.classId,
                            "name", 0, false, 0);
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
                        children.add(new XmlNode(ov.value, title, ov.index));
                    }
                    addAllChildren(children);
                } catch (KrnException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean isModify() {
        return isModify;
    }

    public void setModify(boolean modify) {
        isModify = modify;
    }

    public String getExpressionText() {
        return expressionText;
    }

    public void setExpressionText(String expressionText) {
        this.expressionText = expressionText;
    }

    public void save() {
        Kernel krn = Kernel.instance();
        try {
            krn.setBlob(krnObj.id, funcCls.id, "text", 0,
                    expressionText.getBytes("UTF-8"), 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reloadExpression() {
        Kernel krn = Kernel.instance();
        try {
            byte[] data = krn.getBlob(krnObj, "text", 0, 0, 0);
            try {
                expressionText = new String(data, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }


}
