package kz.tamur.guidesigner.reports;

import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.StringValue;
import com.cifs.or2.client.Kernel;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import kz.tamur.comps.OrFrame;

/**
 * User: vital
 * Date: 20.01.2005
 * Time: 10:00:28
 */
public class ReportRecord {

    private long objId;
    private String path;
    private long filterId;
    private String func;
    private String visibilityFunc;
    private String uid;
    private List<ReportRecord> children;
    private String name;

    private Map<Long, String> titleMap = new TreeMap<Long, String>();
    private List<Long> langs;
    boolean formOnServer = false;

    public ReportRecord(long objId, String path, long filterId, String func, String visFunc, boolean formOnServer) {
        this.objId = objId;
        this.path = path;
        this.filterId = filterId;
        this.func = func;
        this.visibilityFunc = visFunc;
        this.formOnServer = formOnServer;
    }

    public ReportRecord(String uid) {
        this.uid = uid;
        children = new ArrayList<ReportRecord>();
    }

    public ReportRecord() {
        this.uid = null;
        this.name = "Отчеты";
        children = new ArrayList<ReportRecord>();
    }

    public long getObjId() {
        return objId;
    }

    public void setObjId(int objId) {
        this.objId = objId;
    }

    public String getTitle(long langId, Kernel krn) {
    	String title = titleMap.get(langId);
    	if (title == null) {
            try {
                StringValue[] vals = krn.getStringValues(new long[] {objId}, krn.getClassByName("ReportPrinter").id,
                        "title", langId, false, 0);
                if (vals.length > 0) {
                    title = vals[0].value;
                }
            } catch (KrnException e) {
                e.printStackTrace();
            }
            if (title == null || title.length() == 0) title = "*";
            titleMap.put(langId, title);
    	}
        return title;
    }

    public boolean hasLang(long langId, long attrId, Kernel krn) {
    	if (langs == null) {
    		langs = new ArrayList<Long>();
            try {
                long langIds[] = krn.getLangs(objId, attrId, 0);
                for (int i = 0; i < langIds.length; i++) {
                	langs.add(langIds[i]);
                }
            } catch (KrnException ex) {
            	langs = null;
            	return false;
            }
    	}
        return langs.contains(langId);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFunc() {
        return func!=null?func:"";
    }

    public void setFunc(String func) {
        this.func = func;
    }
    
    public String getVisibilityFunc() {
        return visibilityFunc != null ? visibilityFunc : "";
	}

	public void setVisibilityFunc(String visibilityFunc) {
		this.visibilityFunc = visibilityFunc;
	}

    public String formOnServerStr() {
        return formOnServer ? "1" : "";
    }

    public boolean isFormOnServer() {
		return formOnServer;
	}

	public void setFormOnServer(boolean formOnServer) {
		this.formOnServer = formOnServer;
	}

	public boolean equals(Object obj) {
        if (obj instanceof ReportRecord) {
            ReportRecord r = (ReportRecord)obj;
            if (r.isFolder() && isFolder()) {
                return r == this;
            } else {
                return objId == ((ReportRecord)obj).getObjId();
            }
        }
        return false;
    }

    public long getFilterId() {
        return filterId;
    }

    public void setFilterId(long filterId) {
        this.filterId = filterId;
    }

    public void addChild(ReportRecord child) {
        children.add(child);
    }

    public void addChild(ReportRecord child, int index) {
        children.add(index, child);
    }

    public List<ReportRecord> getChildren() {
        return children;
    }

    public String getName(OrFrame frm) {
        if (name != null) return name;
        String res = frm.getString(uid);
        return res;
    }

    public boolean isFolder() {
        return children != null;
    }

    public String getUid() {
        return uid;
    }
}
