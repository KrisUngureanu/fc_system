package kz.tamur.guidesigner.reports;

import com.cifs.or2.kernel.KrnObject;

import java.io.File;
import java.util.Date;

public interface ReportPrinter {
	public static final String FILE_NOT_PRINTED = "FILE_NOT_PRINTED";
	
	long getId();
    Object printToFile(KrnObject lang);
    long getReportFilters(KrnObject lang);
    boolean hasReport(KrnObject lang);

    File printToFile(KrnObject lang, Date fDate, Date lDate, Date cDate);
}
