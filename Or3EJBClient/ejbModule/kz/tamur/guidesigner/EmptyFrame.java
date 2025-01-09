package kz.tamur.guidesigner;

import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnObject;

import kz.tamur.comps.OrFrame;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.guidesigner.reports.ReportPrinter;
import kz.tamur.guidesigner.reports.ReportRecord;
import kz.tamur.rt.CheckContext;
import kz.tamur.rt.InterfaceManager;
import kz.tamur.rt.adapters.OrRef;
import kz.tamur.rt.data.Cache;

/*
 * User: vital
 * Date: 31.10.2005
 * Time: 10:37:14
 */

public class EmptyFrame implements OrFrame {

    private KrnObject lang = new KrnObject(0, "", 0);
	private Kernel krn;
	private Cache cache;

	public EmptyFrame() {
	}
	
    public EmptyFrame(Kernel krn) {
		this.krn = krn;
		cache = new Cache(krn);
    }
    
    public KrnObject getInterfaceLang() {
        return lang;
    }

    public void setInterfaceLang(KrnObject lang) {
        this.lang = lang;
    }

    public String getNextUid() {
        return null;
    }

    public String getString(String uid) {
        return null;
    }

    public String getString(String uid, String defStr) {
        return null;
    }

    public byte[] getBytes(String s) {
        return null;
    }

    public void setString(String uid, String str) {

    }

    public void setBytes(String s, byte[] bytes) {
    }

	public void addRefGroup(int group, CheckContext context) {
	}

	public Cache getCash() {
		return cache;
	}
	
	@Override
	public long getFlowId() {
		return 0;
	}

    public long getTransactionId() {
        return 0;
    }
	public Map<String, OrRef> getContentRef() {
		return null;
	}

	public List<CheckContext> getRefGroups(int group) {
		return null;
	}

	public Map<String, OrRef> getRefs() {
		return null;
	}

	public OrGuiComponent getPanel() {
		return null;
	}

	public int getTransactionIsolation() {
		return 0;
	}

	public ReportPrinter getReportPrinter(long id) {
		return null;
	}

	public void addReport(ReportPrinter report) {
	}

	public KrnObject getDataLang() {
		return null;
	}

	public int getEvaluationMode() {
		return 0;
	}

    public void setRootReport(ReportRecord reportRecord) {
    }

    public void setInterfaceLang(KrnObject lang, ResourceBundle res) {
        setInterfaceLang(lang);
    }

    public ResourceBundle getResourceBundle() {
        return null;
    }

	public InterfaceManager getInterfaceManager() {
		return null;
	}

	@Override
	public void setAllwaysFocused(OrGuiComponent comp) {
	}
}
