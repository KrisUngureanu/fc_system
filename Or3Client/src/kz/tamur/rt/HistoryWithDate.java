package kz.tamur.rt;

import java.text.SimpleDateFormat;
import java.util.Date;


import com.cifs.or2.kernel.KrnObject;

public class HistoryWithDate {
	private KrnObject obj;
	private String time;
	
	
	public HistoryWithDate(KrnObject obj, String time) {
		this.obj = obj;
		this.time = time;
	}
	public HistoryWithDate(KrnObject obj, Date time) {
		this.obj = obj;
		this.time = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(time);
	}
	public KrnObject getObj() {
		return obj;
	}
	public void setObj(KrnObject obj) {
		this.obj = obj;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
	
}
