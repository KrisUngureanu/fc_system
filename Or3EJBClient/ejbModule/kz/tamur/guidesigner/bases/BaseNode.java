package kz.tamur.guidesigner.bases;

import com.cifs.or2.kernel.*;
import com.cifs.or2.client.Kernel;
import java.util.*;
import kz.tamur.util.AbstractDesignerTreeNode;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 13.10.2004
 * Time: 11:31:34
 * To change this template use File | Settings | File Templates.
 */
public class BaseNode extends AbstractDesignerTreeNode {

    //Class fields
    private long flags;
    private long level;
    private KrnObject baseObj;
    //
    private boolean isModified = false;
    private boolean isPhysical = false;

    public BaseNode(KrnObject obj, String name, long flags, long level,
                    KrnObject baseObject, int index, boolean isPhysical) {
        krnObj = obj;
        isLoaded = false;
        title = name;
        this.flags = flags;
        this.level = level;
        this.baseObj = baseObject;
        if (this.baseObj == null) {
            createBaseObject();
        }
        this.isPhysical = isPhysical;
//        load();
    }



    private void createBaseObject() {
        Kernel krn = Kernel.instance();
        try {
            KrnClass cls = krn.getClassByName("База");
            KrnObject baseObj = krn.createObject(cls, 0);
            krn.setObject(krnObj.id, krnObj.classId, "значение", 0, baseObj.id, 0, false);
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }

    public void rename(String newName) {
        title = newName;
        final Kernel krn = Kernel.instance();
        try {
            krn.setString(krnObj.id, krnObj.classId,
                    "наименование", 0, 0, title, 0);
        } catch (KrnException e) {
            e.printStackTrace();
        }

    }

    protected void load() {
        if (!isLoaded) {
            isLoaded = true;
            //if (!isLeaf()) {
                final Kernel krn =  Kernel.instance();
                long[] oids = {krnObj.id};
                ObjectValue[] ovs = new ObjectValue[0];
                try {
                    //KrnClass baseCls = krn.getClassByName("База");
                    ovs = krn.getObjectValues(oids,krnObj.classId, "дети", 0);
                    long[] ids = new long[ovs.length];
                    for (int i = 0; i < ovs.length; i++) {
                        ids[i] = ovs[i].value.id;
                    }
                    StringValue[] names = krn.getStringValues(ids, krnObj.classId,
                            "наименование", 0, false, 0);
                    LongValue[] flagsArr = krn.getLongValues(ids, krnObj.classId,
                            "flags", 0);
                    LongValue[] levelArr = krn.getLongValues(ids, krnObj.classId,
                            "уровень", 0);
                    ObjectValue[] bases = krn.getObjectValues(ids, krnObj.classId,
                            "значение", 0);
                    LongValue[] isPhysicalArr = krn.getLongValues(ids, krnObj.classId,
                            "физически раздельная?", 0);
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
                        for (int j = 0; j < names.length; j++) {
                            if (names[j].objectId == ov.value.id) {
                                title = names[j].value;
                                break;
                            }
                        }
                        //Temporary
                        String baseName = title;
                        if ("Безымянный".equals(title) && bases.length > 0) {
                            for (int j = 0; j < bases.length; j++) {
                                ObjectValue baseObject = bases[j];
                                if (baseObject.objectId == ov.value.id) {
                                    baseName = getStructureNameByBase(baseObject.value);
                                    break;
                                }
                            }
                            if (!"".equals(baseName)) {
                                title = baseName;
                            }
                        }
                        //
                        long flagsV = 0;
                        for (int j = 0; j < flagsArr.length; j++) {
                            if (flagsArr[j].objectId == ov.value.id) {
                                flagsV = flagsArr[j].value;
                                break;
                            }
                        }
                        long levelV = 0;
                        for (int j = 0; j < levelArr.length; j++) {
                            if (levelArr[j].objectId == ov.value.id) {
                                levelV = levelArr[j].value;
                                break;
                            }
                        }
                        KrnObject baseObject = null;
                        for (int j = 0; j < bases.length; j++) {
                            if (bases[j].objectId == ov.value.id) {
                                baseObject = bases[j].value;
                            }
                        }
                        Boolean isPhysical = null;
                        for (int j = 0; j < isPhysicalArr.length; j++) {
                            if (isPhysicalArr[j].objectId == ov.value.id) {
                            	isPhysical = isPhysicalArr[j].value == 1 ? true : false;
                            }
                        }
                        add(new BaseNode(ov.value, title, flagsV, levelV,
                                baseObject, ov.index, isPhysical));
                    }
                    addAllChildren(children);
                } catch (KrnException e) {
                    e.printStackTrace();
                }
            //}
        }
    }


    public boolean isModified() {
        return isModified;
    }

    public void setModified(boolean modified) {
        isModified = modified;
    }

    public String getName() {
        return title;
    }

    public void setName(String name) {
        title = name;
    }

    public long getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public int getLevel() {
        return (int)level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isPhysical() {
        return this.isPhysical;
    }

    public void setAsPhysical(boolean value) {
    	this.isPhysical = value;
    }

    public KrnObject getBaseObj() {
        return baseObj;
    }

    private String getStructureNameByBase(KrnObject baseObject) {
        Kernel krn = Kernel.instance();
        try {
            StringValue[] names = krn.getStringValues(new long[] {baseObject.id},
                    baseObject.classId, "наименование", 0, false, 0);
            for (int i = 0; i < names.length; i++) {
                if (names[i].objectId == baseObject.id) {
                    return names[i].value;
                }
            }
        } catch (KrnException e) {
            e.printStackTrace();
        }
        return "";
    }

}
