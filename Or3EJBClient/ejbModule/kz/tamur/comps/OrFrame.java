package kz.tamur.comps;

import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import kz.tamur.guidesigner.reports.ReportPrinter;
import kz.tamur.guidesigner.reports.ReportRecord;
import kz.tamur.rt.CheckContext;
import kz.tamur.rt.InterfaceManager;
import kz.tamur.rt.adapters.OrRef;
import kz.tamur.rt.data.Cache;

import com.cifs.or2.kernel.KrnObject;

/*
 * User: vital
 * Date: 23.10.2005
 * Time: 15:37:11
 */

public interface OrFrame {
    KrnObject getInterfaceLang();
    KrnObject getDataLang();
    void setInterfaceLang(KrnObject lang);
    void setInterfaceLang(KrnObject lang, ResourceBundle res);

    String getNextUid();
    String getString(String uid);
    String getString(String uid, String defStr);
    void setString(String uid, String str);
    
    Cache getCash();
    long getFlowId();
    long getTransactionId();
    Map<String, OrRef> getRefs();
    Map<String, OrRef> getContentRef();
    List<CheckContext> getRefGroups(int group);
    void addRefGroup(int group, CheckContext context);
    int getTransactionIsolation();
    OrGuiComponent getPanel();
    
    ReportPrinter getReportPrinter(long id);
    void addReport(ReportPrinter report);
    
    int getEvaluationMode();

    ResourceBundle getResourceBundle();

    void setRootReport(ReportRecord reportRecord);

    void setBytes(String s, byte[] bytes);

    byte[] getBytes(String s);
    
    InterfaceManager getInterfaceManager();
    
	void setAllwaysFocused(OrGuiComponent comp);
}
