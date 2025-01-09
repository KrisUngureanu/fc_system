package kz.tamur.ekyzmet.test;

import com.cifs.or2.kernel.KrnObject;

public class ResultRecord {

	public final KrnObject obj;
	public final KrnObject appHist;
	public final int orderNum;
	public final Section section;
	public final SubSection subSection;
	public final int qsnCount;
	public final int level;
	public final int correctAwrCount;
	public final KrnObject result;
	
	public ResultRecord(KrnObject obj, KrnObject appHist, int orderNum, Section section, SubSection subSection,
			int qsnCount, int level, int correctAwrCount, KrnObject result) {
		super();
		this.obj = obj;
		this.appHist = appHist;
		this.orderNum = orderNum;
		this.section = section;
		this.subSection = subSection;
		this.qsnCount = qsnCount;
		this.level = level;
		this.correctAwrCount = correctAwrCount;
		this.result = result;
	}
}
