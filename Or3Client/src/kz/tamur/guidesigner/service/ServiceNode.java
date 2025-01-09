package kz.tamur.guidesigner.service;

import static com.cifs.or2.client.Kernel.SC_PROCESS_DEF;
import static com.cifs.or2.client.Kernel.SC_PROCESS_DEF_FOLDER;

import com.cifs.or2.kernel.*;
import com.cifs.or2.client.*;
import com.cifs.or2.client.util.AttrRequestBuilder;

import java.util.*;

import kz.tamur.rt.TreeUIDMap;
import kz.tamur.util.AbstractDesignerTreeNode;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 13.10.2004
 * Time: 11:31:34
 * To change this template use File | Settings | File Templates.
 */
public class ServiceNode extends AbstractDesignerTreeNode {

    private boolean isTab;
    private String tabName;
    private String tabNameKz;
    private String title_;
    private String titleKz_;
    private long runtimeIndex = 0;
    private static long kzId, ruId;

    /** создавать ли кнопку на панели инструментов для данного процесса */
    private boolean isBtnToolBar = false;

    /** иконка для кнопки на панели интсрументов */
    private byte[] icon = null;

    /** Горячие клавиши для вызова данного процесса */
    private String hotKey = "Не определены";

    private boolean isRuntimeDesign = false;

    private static KrnAttribute hotKeyAttr;
    private static KrnAttribute iconAttr;
    
    static {
    	hotKeyAttr = Kernel.instance().getAttributeByNameTracing(SC_PROCESS_DEF_FOLDER, "hotKey");
    	iconAttr = Kernel.instance().getAttributeByNameTracing(SC_PROCESS_DEF_FOLDER, "icon");
        ruId = Kernel.instance().getLangIdByCode("RU");
        kzId = Kernel.instance().getLangIdByCode("KZ");
    }

    public ServiceNode(KrnObject srvObj, String title, long langId, int index, String titleRU, String titleKZ, long runtimeIndex, 
    		boolean isTab, String tabRU, String tabKZ, String hotKey, boolean isBtnToolBar, byte[] icon) {
    	nodeType = SERVICE_NODE;
        krnObj = srvObj;
        isLoaded = false;
        this.title = title;
        this.langId = langId;
        this.title_ = titleRU;
        this.titleKz_ = titleKZ;
        this.runtimeIndex = runtimeIndex;
        this.isTab = isTab;
        this.tabName = tabRU;
        this.tabNameKz = tabKZ;
        this.hotKey = hotKey;
        this.isBtnToolBar = isBtnToolBar;
        this.icon = icon;
    }

    public boolean isRuntimeDesign() {
        return isRuntimeDesign;
    }

    public void setRuntimeDesign(boolean runtimeDesign) {
        isRuntimeDesign = runtimeDesign;
    }

    public boolean isLeaf() {
        return krnObj.classId == SC_PROCESS_DEF.id;
    }

    protected void load() {
        if (!isLoaded) {
            isLoaded = true;
            if (!isLeaf()) {
                final Kernel krn = Kernel.instance();
                long[] oids = { krnObj.id };
                ObjectValue[] ovs = new ObjectValue[0];
                try {
                    ovs = krn.getObjectValues(oids, krnObj.classId, "children", 0);
                    long[] ids = new long[ovs.length];
                    for (int i = 0; i < ovs.length; i++) {
                        ids[i] = ovs[i].value.id;
                    }
                    
                    final Map<Long, String> str_ru = new HashMap<Long, String>();
                    final Map<Long, String> str_kz = new HashMap<Long, String>();
                    final Map<Long, Long> runtimeIndex_ = new HashMap<Long, Long>();
                    final Map<Long, Boolean> isTab_ = new HashMap<Long, Boolean>();
                    final Map<Long, String> tab_ru = new HashMap<Long, String>();
                    final Map<Long, String> tab_kz = new HashMap<Long, String>();
                    final Map<Long, String> hotKeys_ = new HashMap<Long, String>();
                    final Map<Long, Long> isBtnToolBar_ = new HashMap<Long, Long>();
                    final Map<Long, byte[]> icons_ = new HashMap<Long, byte[]>();

                    try {
                        AttrRequestBuilder arb = new AttrRequestBuilder(SC_PROCESS_DEF_FOLDER, krn)
                        		.add("title", langId).add("title", kzId).add("runtimeIndex")
                        		.add("isTab").add("tabName", ruId).add("tabName", kzId);
                        
                    	if (hotKeyAttr != null)
                    		arb.add("hotKey", langId);
                    	
                    	arb.add("isBtnToolBar");
                    	
                    	if (iconAttr != null)
                    		arb.add("icon", langId);

                        List<Object[]> rows = krn.getObjects(ids, arb.build(), 0);
                        for (Object[] row : rows) {
                            KrnObject obj = (KrnObject)row[0];
                        	
                            if (row[2] != null)
                                str_ru.put(obj.id, (String)row[2]);
                            if (row[3] != null)
                                str_kz.put(obj.id, (String)row[3]);
                        	if (row[4] != null)
                        		runtimeIndex_.put(obj.id, (Long)row[4]);
                            if (row[5] != null)
                                isTab_.put(obj.id, (Boolean)row[5]);
                            if (row[6] != null)
                                tab_ru.put(obj.id, (String)row[6]);
                            if (row[7] != null)
                                tab_kz.put(obj.id, (String)row[7]);
                        	
                        	int k = 7;
                            if (hotKeyAttr != null && row[++k] != null)
                                hotKeys_.put(obj.id, (String)row[k]);
                            if (row[++k] != null)
                                isBtnToolBar_.put(obj.id, (Long)row[k]);
                            if (iconAttr != null && row[++k] != null)
                                icons_.put(obj.id, (byte[])row[k]);
                        	
                        }
                    } catch (KrnException e) {
                        e.printStackTrace();
                    }

                    Arrays.sort(ovs, new Comparator<ObjectValue>() {
                        public int compare(ObjectValue ov1, ObjectValue ov2) {
                            if (ov1 == null) {
                                return -1;
                            } else if (ov2 == null) {
                                return 1;
                            } else {
                                String p1 = str_ru.get(ov1.value.id);
                                String p2 = str_ru.get(ov2.value.id);
                                if (p1 == null)
                                    p1 = "";
                                if (p2 == null)
                                    p2 = "";
                                return p1.compareTo(p2);
                            }
                        }
                    });
                    List<ServiceNode> children = new ArrayList<ServiceNode>();
                    for (int i = 0; i < ovs.length; i++) {
                        ObjectValue ov = ovs[i];
                        String title = str_ru.get(ov.value.id);
                        if (title == null || title.equals(""))
                            title = "Безымянный";
                        
                        String titleKZ = str_kz.get(ov.value.id);
                        if (titleKZ == null)
                        	titleKZ = "";
                        
                        Long l = runtimeIndex_.get(ov.value.id);
                        final long runtimeIndex = l != null ? l : 0;
                        
                        Boolean b = isTab_.get(ov.value.id);
                        final boolean isTab = b != null ? b : false;

                        String tabRU = tab_ru.get(ov.value.id);
                        if (tabRU == null)
                        	tabRU = "";

                        String tabKZ = tab_kz.get(ov.value.id);
                        if (tabKZ == null)
                        	tabKZ = "";

                        String hotKey = hotKeys_.get(ov.value.id);
                        if (hotKey == null)
                        	hotKey = "Не определены";
                        
                        l = isBtnToolBar_.get(ov.value.id);
                        final boolean isBtnToolBar = l != null ? l > 0 : false;

                        byte[] icon = icons_.get(ov.value.id);

                        ServiceNode sNode = new ServiceNode(ov.value, langId == ruId ? title : titleKZ, langId, ov.index,
                        		title, titleKZ, runtimeIndex, isTab, tabRU, tabKZ, hotKey, isBtnToolBar, icon);
                        TreeUIDMap.put(((KrnObject) ov.value).uid, sNode);
                        if (isRuntimeDesign) {
                            sNode.setRuntimeDesign(isRuntimeDesign);
                        }
                        children.add(sNode);
                    }
                    addAllChildren(children);
                } catch (KrnException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void resort() {
        List<ServiceNode> ch = new ArrayList<ServiceNode>();
        Enumeration childElements = children();
        while (childElements.hasMoreElements()) {
            ServiceNode hn = (ServiceNode) childElements.nextElement();
            if (!hn.isLeaf()) {
                hn.resort();
            }
            ch.add(hn);
        }
        Collections.sort(ch, new Comparator<ServiceNode>() {
            public int compare(ServiceNode o1, ServiceNode o2) {
                if (o1 != null && o2 != null) {
                    Long i1 = new Long(o1.getRuntimeIndex());
                    Long i2 = new Long(o2.getRuntimeIndex());
                    return i1.compareTo(i2);
                }
                return 0;
            }
        });
        removeAllChildren();
        for (int i = 0; i < ch.size(); i++) {
            ServiceNode serviceNode = ch.get(i);
            add(serviceNode);
        }
    }

    public boolean isTab() {
        return isTab;
    }

    public void setTab(boolean tab) {
        isTab = tab;
    }

    public String getTabNameKz() {
        return tabNameKz;
    }

    public void setTabNameKz(String tabName) {
        this.tabNameKz = tabName;
    }

    public String getTabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

    public String getTitleKz() {
        return titleKz_;
    }

    public void setTitleKz(String tName) {
        this.titleKz_ = tName;
        if (langId == kzId)
            this.title = tName;
    }

    public String getTitle() {
        return title_;
    }

    public void setTitle(String tName) {
        this.title_ = tName;
        if (langId != kzId) {
            this.title = tName;
        }
    }

    public long getRuntimeIndex() {
        return runtimeIndex;
    }

    public void setRuntimeIndex(int runtimeIndex) {
        this.runtimeIndex = runtimeIndex;
    }

    /**
     * @return the isBtnToolBar
     */
    public boolean isBtnToolBar() {
        return isBtnToolBar;
    }

    /**
     * @param isBtnToolBar
     *            the isBtnToolBar to set
     */
    public void setBtnToolBar(boolean isBtnToolBar) {
        this.isBtnToolBar = isBtnToolBar;
    }

    /**
     * @return the ico
     */
    public byte[] getIcon() {
        return icon;
    }

    /**
     * @param ico
     *            the ico to set
     */
    public void setIcon(byte[] icon) {
        this.icon = icon;
    }

    /**
     * @return the hotKey
     */
    public String getHotKey() {
        return hotKey;
    }

    /**
     * @param hotKey
     *            the hotKey to set
     */
    public void setHotKey(String hotKey) {
        this.hotKey = hotKey;
    }

}
