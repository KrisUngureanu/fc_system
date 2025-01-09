package kz.tamur.guidesigner.hypers;

import static com.cifs.or2.client.Kernel.SC_HIPERTREE;
import static com.cifs.or2.client.Kernel.SC_UI;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.util.AttrRequestBuilder;
import com.cifs.or2.client.util.KrnObjectItem;
import com.cifs.or2.kernel.*;

import kz.tamur.util.AbstractDesignerTreeNode;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 13.10.2004
 * Time: 11:31:34
 * To change this template use File | Settings | File Templates.
 */
public class HyperNode extends AbstractDesignerTreeNode {

    private KrnObjectItem ifcObj;
    private boolean isModified = false;
    private boolean isAdded = false;
    private long runtimeIndex = 0;
    private Set readOnlyItems; 
    private Set readWriteItems;
    private boolean isReadOnly;
    private boolean isDialog;
    private boolean isChangeable;
    private long langId;
    private String titleKz;
    private HashMap<Long, String> titleMap=new HashMap<Long, String>();
    private List<HyperNode> children;
    private HyperTree tree;
    private String titleIfc;
    private byte[] icon;

    private static long kzId, ruId;

    static {
        ruId = Kernel.instance().getLangIdByCode("RU");
        kzId = Kernel.instance().getLangIdByCode("KZ");
    }

    public HyperNode(KrnObject uiObj, String titleRu, String titleKz, KrnObject ifcObj, String titleIfc, int index,
                     Set readItemsSet, Set writeItemsSet, long langId,
                     boolean isReadOnly, byte[] icon) {
        krnObj = uiObj;
        isLoaded = false;
        this.title = langId == ruId ? titleRu : titleKz;
        this.langId = langId;
        titleMap.put(ruId, titleRu);
        titleMap.put(kzId, titleKz);
        readOnlyItems = readItemsSet;
        readWriteItems = writeItemsSet;
        this.isReadOnly = isReadOnly;
        this.titleIfc = titleIfc;
        this.ifcObj = new KrnObjectItem(ifcObj,titleIfc);
        this.icon = icon;
    }

    public void setTree(HyperTree tree) {
        this.tree = tree;
    }

    public boolean isLeaf() {
        return krnObj.classId == SC_HIPERTREE.id;
    }

    public KrnObject getIfcObject() {
        return (ifcObj==null?null:ifcObj.obj);
    }

    public KrnObjectItem getIfcObjectItem() {
        return ifcObj;
    }

    public void rename(String newName) {
        titleMap.put(new Long(langId), title=newName);
        final Kernel krn = Kernel.instance();
        try {
            krn.setString(krnObj.id, krnObj.classId,
                    "title", 0, langId, title, 0);
        } catch (KrnException e) {
            e.printStackTrace();
        }
        setModified(true);
    }

    public void renameKz(String newName) {
        final Kernel krn = Kernel.instance();
        try {
            long kzId=krn.getLangIdByCode("KZ");
            titleMap.put(kzId, titleKz=newName);
            krn.setString(krnObj.id, krnObj.classId,
                    "title", 0, kzId, newName, 0);
        } catch (KrnException e) {
            e.printStackTrace();
        }
        setModified(true);
    }
    public void resort() {
        List ch = new ArrayList();
        Enumeration childElements = children();
        while(childElements.hasMoreElements()) {
            HyperNode hn = (HyperNode)childElements.nextElement();
            if (!hn.isLeaf()) {
                hn.resort();
            }
            ch.add(hn);
        }
        Collections.sort(ch, new Comparator() {
            public int compare(Object o1, Object o2) {
                if (o1 != null && o2 != null) {
                    Long i1 = new Long(((HyperNode)o1).getRuntimeIndex());
                    Long i2 = new Long(((HyperNode)o2).getRuntimeIndex());
                    return i1.compareTo(i2);
                }
                return 0;
            }
        });
        removeAllChildren();
        for (int i = 0; i < ch.size(); i++) {
            HyperNode hyperNode = (HyperNode) ch.get(i);
            add(hyperNode);
        }
    }

    protected void load() {
        if (!isLoaded) {
            isLoaded = true;
            final Kernel krn =  Kernel.instance();
            long[] oids = {krnObj.id};
            if (!isLeaf()) {
                try {
                	ObjectValue[] ovs = krn.getObjectValues(oids,krnObj.classId, "hipers", 0);
                    long[] ids = new long[ovs.length];
                    for (int i = 0; i < ovs.length; i++) {
                        ids[i] = ovs[i].value.id;
                    }
                    
                    AttrRequestBuilder arb = new AttrRequestBuilder(Kernel.SC_HIPERTREE, krn)
                    .add("hiperObj")
                    .add("title", ruId)
                    .add("title", kzId)
                    .add("runtimeIndex")
                    .add("isDialog")
                    .add("isChangeable")
                    .add("uiIcon");
                   
                    
                    List<Object[]> rows = krn.getObjects(ids, arb.build(), 0);

                    final Map<Long, KrnObject> objs_ = new HashMap<Long, KrnObject>();
                    final Map<Long, String> titleRU_ = new HashMap<Long, String>();
                    final Map<Long, String> titleKZ_ = new HashMap<Long, String>();
                    final Map<Long, KrnObject> ifcs_ = new HashMap<Long, KrnObject>();
                    final Map<Long, Integer> runtimeIndex_ = new HashMap<Long, Integer>();
                    final Map<Long, Boolean> isDialog_ = new HashMap<Long, Boolean>();
                    final Map<Long, Boolean> isChangeable_ = new HashMap<Long, Boolean>();
                    final Map<Long, String> ifcTitle_ = new HashMap<Long, String>();
                    final Map<Long, byte[]> icon_ = new HashMap<Long, byte[]>();
                    
                    List<Long> iids = new ArrayList<Long>();

                    for (Object[] row : rows) {
                    	KrnObject chObj = arb.getObject(row);
                    	objs_.put(chObj.id, chObj);
                    	
                    	if (row[2] != null) {
                    		KrnObject ifcObject = (KrnObject)row[2];
                    		ifcs_.put(chObj.id, ifcObject);

                    		if (!iids.contains(ifcObject.id))
                    			iids.add(ifcObject.id);
                    	}
                        
                    	if (row[3] != null)
                    		titleRU_.put(chObj.id, (String)row[3]);
                    	if (row[4] != null)
                    		titleKZ_.put(chObj.id, (String)row[4]);
                    	if (row[5] != null)
                    		runtimeIndex_.put(chObj.id, ((Number)row[5]).intValue());
                    	if (row[6] != null)
                    		isDialog_.put(chObj.id, (Boolean)row[6]);
                    	if (row[7] != null)
                    		isChangeable_.put(chObj.id, (Boolean)row[7]);
                    	if (row.length > 8 && row[8] != null) 
                    		icon_.put(chObj.id, (byte[])row[8]);
                        
                    }
                    
                    if (iids.size() > 0) {
                    	long[] ifcIds = kz.tamur.rt.Utils.toIdsArray(iids);
                    	StringValue[] svs = krn.getStringValues(ifcIds, SC_UI.id, "title", langId, false, 0);
                    	
                    	for (StringValue sv : svs) {
                    		ifcTitle_.put(sv.objectId, sv.value);
                    	}
                    }
                    
                    int i = 0;
                    children = new ArrayList<HyperNode>();
                    for (int k = 0; k < ids.length; k++) {
                    	long id = ids[k];
                    	KrnObject chObj = objs_.get(id);
                        
                        String titleRu = titleRU_.get(id);
                        String titleKz = titleKZ_.get(id);
                        KrnObject ifcObject = ifcs_.get(id);
                        String titleIfc = ifcObject != null ? ifcTitle_.get(ifcObject.id) : null;
                        
                        Integer ti = runtimeIndex_.get(id);
                        int runtimeIndex = (ti != null) ? ti : 0;
                        Boolean b = isDialog_.get(id);
                        boolean isDialog = (b != null) ? b : false;
                        b = isChangeable_.get(id);
                        boolean isChangeable = (b != null) ? b : false;
                        byte[] icon = icon_.get(id); 

                        boolean isEditable = true;
                        if ((readOnlyItems == null && readWriteItems == null)
                                || (isEditable = readWriteItems.contains(chObj.id))
                                || readOnlyItems.contains(chObj.id)) {
                        	
                            HyperNode n = new HyperNode(chObj, titleRu, titleKz, ifcObject, titleIfc,
                                    i++, readOnlyItems, readWriteItems, langId, !isEditable, icon);
                            
                            n.setRuntimeIndex(runtimeIndex);
                            n.setDialog(isDialog);
                            n.setChangeable(isChangeable);

                            children.add(n);
                        }
                    }

                    addAllChildren(children);
                } catch (KrnException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void addAllChildren(List children) {
        Collections.sort(children, new Comparator() {
            public int compare(Object o1, Object o2) {
                if (o1 != null && o2 != null) {
                    Long i1 = new Long(((HyperNode)o1).getRuntimeIndex());
                    Long i2 = new Long(((HyperNode)o2).getRuntimeIndex());
                    return i1.compareTo(i2);
                }
                return 0;
            }
        });
        for (int i = 0; i < children.size(); i++) {
            AbstractDesignerTreeNode node = (AbstractDesignerTreeNode)children.get(i);
            add(node);
        }

    }

    public long getRuntimeIndex() {
        return runtimeIndex;
    }

    public void setRuntimeIndex(int runtimeIndex) {
        this.runtimeIndex = runtimeIndex;
    }


    public boolean isModified() {
        return isModified;
    }

    public void setModified(boolean modified) {
        isModified = modified;
    }

    public void setIfcObject(KrnObject obj) {
        ifcObj.obj = obj;
    }

    public void setIfcObjectItem(KrnObjectItem obj) {
        ifcObj = obj;
    }
    public boolean isAdded() {
        return isAdded;
    }

    public void setAdded(boolean added) {
        isAdded = added;
/*
        if (!isLeaf()) {
            for (int i = 0; i < getChildCount(); i++) {
                HyperNode child = (HyperNode)getChildAt(i);
                child.setAdded(isAdded);
            }
        }
*/
    }
    public String toString(){
        String title=(String)titleMap.get(new Long(langId));
        if(title==null) title="*";
        return title;
    }
    public void setLang(long langId){
        this.langId=langId;
    }
    public void setTitle(String title, long langId){
        titleMap.put(new Long(langId),title);
        this.langId=langId;
    }
    public void setTitle(String title){
        titleMap.put(new Long(langId),title);
    }

    public String getTitleKz(){
        return titleKz;
    }
    public void setTitleKz(String titleKz){
        this.titleKz=titleKz;
    }
    public List getChildren(){
        return children;
    }

    public boolean isReadOnly() {
        return isReadOnly;
    }

    public boolean isDialog() {
        return isDialog;
    }

    public void setDialog(boolean dialog) {
        isDialog = dialog;
    }

    public boolean isChangeable() {
        return isChangeable;
    }

    public void setChangeable(boolean value) {
        isChangeable = value;
    }
    
    public void setIcon(byte[] icon) {
    	this.icon = icon;
    }
    
    public byte[] getIcon() {
    	return icon;
    }

	public String getTitleIfc() {
		return titleIfc;
	}
}
