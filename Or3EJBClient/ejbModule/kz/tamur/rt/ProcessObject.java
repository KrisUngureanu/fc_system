package kz.tamur.rt;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnObject;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 12.03.2005
 * Time: 12:57:04
 * To change this template use File | Settings | File Templates.
 */
public class ProcessObject {
	public KrnObject parentObj;
    public KrnObject obj;
    protected HashMap<Long, String> titleMap = new HashMap<Long, String>();
    private HashMap<Long, String> descMap = new HashMap<Long, String>();
    protected long langId;
    public long index=0;
    
    protected ProcessObject() {
    }
    
    public ProcessObject(Object[] rec, long lang, Kernel krn) {
        langId = lang;
        obj = (KrnObject)rec[0];
        parentObj = (KrnObject)rec[2];
        index = (rec[3] instanceof Long) ? (Long)rec[3] : 0;
        
        titleMap.put(krn.getLangIdByCode("RU"), (String)rec[4]);
        titleMap.put(krn.getLangIdByCode("KZ"), (String)rec[5]);
        
    	byte[] msg = (byte[]) rec[6];
    	String desc = parseDesc(msg);
    	if (desc != null)
    		descMap.put(krn.getLangIdByCode("RU"), desc);

    	msg = (byte[]) rec[7];
    	desc = parseDesc(msg);
    	if (desc != null)
    		descMap.put(krn.getLangIdByCode("KZ"), desc);
    }
    
    private String parseDesc(byte[] msg) {
    	if (msg == null || msg.length == 0)
    		return null;
        try {
        	String xml = new String(msg, "UTF-8");
        	int beg = xml.indexOf("uid=\"process_desc_0\"");
        	if (beg > -1) {
        		beg = xml.indexOf('>', beg + 20);
        		int end = xml.indexOf('<', beg);
        		if (end > beg)
        			return xml.substring(beg + 1, end);
        	}
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public String toString(){
        String title = (String)titleMap.get(new Long(langId));
        if(title==null) title="*";
        return title;
    }
    
    public int compareTo(Object o){
        if (o == null) {
            return 1;
        } else {
            return (this.index<((ProcessObject)o).index) ? -1 : 1;
        }
    }
    public void setLangId(long langId) {
        this.langId = langId;
    }

    public void setDescription(String desc, long langId) {
        descMap.put(new Long(langId),desc);
    }

    public String getDescription(){
        String desc=(String)descMap.get(new Long(langId));
        return desc;
    }
}
