package kz.tamur.guidesigner.filters;

import com.cifs.or2.kernel.KrnObject;

/**
 * User: vital
 * Date: 20.01.2005
 * Time: 10:00:28
 */
public class FilterRecord {

    private KrnObject obj;
    private String title;

    public FilterRecord(KrnObject obj, String title) {
        this.obj = obj;
        this.title = title;
    }

    public long getObjId() {
        return obj.id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean equals(Object obj) {
        if (obj instanceof FilterRecord) {
            return this.obj.uid.equals(((FilterRecord)obj).obj.uid);
        }
        return false;
    }

    public String toString() {
        return title;
    }
    
    public KrnObject getKrnObject() {
    	return obj;
    }
}
