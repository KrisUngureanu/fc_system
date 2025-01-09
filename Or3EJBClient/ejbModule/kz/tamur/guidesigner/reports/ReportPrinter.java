package kz.tamur.guidesigner.reports;

import com.cifs.or2.kernel.KrnObject;

import java.io.File;

import kz.tamur.comps.OrFrame;

public interface ReportPrinter {
    long getId();
    void print();
    void print(KrnObject lang);
    File printToFile(KrnObject lang);
    boolean hasReport(KrnObject lang);
    String getVisibilityFunc();
    OrFrame getFrame();
}
