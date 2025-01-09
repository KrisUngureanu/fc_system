package kz.tamur.guidesigner.filters;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.*;
import java.util.*;

import kz.tamur.util.AbstractDesignerTreeNode;
import kz.tamur.comps.OrFrame;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.rt.data.Cache;
import kz.tamur.rt.adapters.OrRef;
import kz.tamur.rt.CheckContext;
import kz.tamur.rt.InterfaceManager;
import kz.tamur.rt.TreeUIDMap;
import kz.tamur.guidesigner.reports.ReportPrinter;
import kz.tamur.guidesigner.reports.ReportRecord;

import static com.cifs.or2.client.Kernel.SC_FILTER;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 13.10.2004
 * Time: 11:31:34
 * To change this template use File | Settings | File Templates.
 */
public class FilterNode extends AbstractDesignerTreeNode implements OrFrame {

    int index;
    public FilterNode(KrnObject filterObj,String title, long langId, int index) {
    	nodeType = FILTER_NODE;
        krnObj = filterObj;
        isLoaded = false;
        this.title = title;
        this.langId = langId;
        this.index = index;
    }
    
    public int getIndex() {
    	return index;
    }
    
    public boolean isLeaf() {
        return krnObj.classId == SC_FILTER.id;
    }

    protected void load() {
        if (!isLoaded) {
            isLoaded = true;
            if (!isLeaf()) {
                final Kernel krn =  Kernel.instance();
                long[] oids = {krnObj.id};
                try {
                    ObjectValue[] ovs = krn.getObjectValues(oids,krnObj.classId, "children", 0);
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
                            if (ov1 == null) {
                                return -1;
                            } else if (ov2 == null) {
                                return 1;
                            } else {
                                String p1 = str_m.get(ov1.value.id);
                                String p2 = str_m.get(ov2.value.id);
                                if (p1 == null)
                                	p1 = "";
                                if (p2 == null)
                                	p2 = "";
                                return p1.compareTo(p2);
                            }
                        }
                    });
                    List<FilterNode> children = new ArrayList<FilterNode>();
                    for (int i = 0; i < ovs.length; i++) {
                        ObjectValue ov = ovs[i];
                        String title = str_m.get(ov.value.id);
                        if(title==null || title.equals("")) 
                            title="Безымянный";
                        FilterNode childNode = new FilterNode(ov.value, title, langId, ov.index);
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

    public KrnObject getInterfaceLang() {
         return com.cifs.or2.client.Utils.getInterfaceLang();
     }

     public void setInterfaceLang(KrnObject lang) {
     }

     public void setInterfaceLang(KrnObject lang, ResourceBundle res) {
         setInterfaceLang(lang);
     }

     public ResourceBundle getResourceBundle() {
         return null;
     }

     public String getNextUid() {
         return null;
     }

     public String getString(String uid) {
         return null;
     }

     public String getString(String uid, String defStr) {
         return null;
     }

     public byte[] getBytes(String s) {
         return null;
     }

     public void setString(String uid, String str) {
     }

     public void setBytes(String s, byte[] bytes) {
     }

     public void addRefGroup(int group, CheckContext context) {
     }

     public Cache getCash() {
         return null;
     }

     @Override
	public long getFlowId() {
		return 0;
	}

     public long getTransactionId() {
         return 0;
     }

	public Map<String, OrRef> getContentRef() {
         return null;
     }

     public List<CheckContext> getRefGroups(int group) {
         return null;
     }

     public Map<String, OrRef> getRefs() {
         return null;
     }

     public OrGuiComponent getPanel() {
         return null;
     }

     public int getTransactionIsolation() {
         return 0;
     }

     public ReportPrinter getReportPrinter(long id) {
         return null;
     }

     public void addReport(ReportPrinter report) {
     }

     public KrnObject getDataLang() {
         return null;
     }

     public int getEvaluationMode() {
         return 0;
     }

     public void setRootReport(ReportRecord reportRecord) {
     }


	public InterfaceManager getInterfaceManager() {
		return null;
	}

	@Override
	public void setAllwaysFocused(OrGuiComponent comp) {
	}
}
