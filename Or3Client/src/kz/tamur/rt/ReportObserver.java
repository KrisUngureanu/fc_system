package kz.tamur.rt;

public interface ReportObserver {
	void setReportComplete(long flowId);
	
	void setProgressCaption(String text);
	void setProgressMinimum(int val);
	void setProgressMaximum(int val);
	void setProgressValue(int val);
}
