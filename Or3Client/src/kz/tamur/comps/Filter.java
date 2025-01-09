package kz.tamur.comps;

import com.cifs.or2.kernel.KrnObject;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 24.08.2004
 * Time: 15:18:18
 * To change this template use File | Settings | File Templates.
 */
public class Filter {

    public KrnObject obj;
    private HashMap titleMap=new HashMap();
    private long langId;
    public String className;
    public long flags;

    public Filter (KrnObject obj, long langId, String className, long flags)
    {
      this.obj = obj;
      this.langId = langId;
      this.className = className;
      this.flags = flags;
    }

    public String toString() {
        String title=(String)titleMap.get(new Long(langId));
        if(title==null) title="*";
        return title;
    }

    public void setLangId(long langId) {
        this.langId = langId;
    }
    public void setTitle(String title,long langId) {
        titleMap.put(new Long(langId),title);
    }
}
