package kz.tamur.rt;

import java.util.Map;

import kz.tamur.rt.adapters.OrRef;
import kz.tamur.rt.adapters.UIFrame;
import kz.tamur.rt.data.Cache;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;

public class TerminalInterfaceManager implements InterfaceManager {
	
	private Kernel krn;
	private Cache cache;

	public TerminalInterfaceManager(Kernel krn) {
		this.krn = krn;
		cache = new Cache(krn);
	}

	public boolean absolute(KrnObject uiObj, KrnObject[] objs, String refPath,
			int mode, boolean isHiperTree, long tid, long flowId,
			boolean isBlockErrors, String uiType) throws KrnException {
		return false;
	}

	public Map<String, OrRef> getRefs() {
		return null;
	}

	public Map<String, OrRef> getContentRefs() {
		return null;
	}

	public Cache getCash() {
		return cache;
	}

	public int getEvaluationMode() {
		return 0;
	}

	public boolean isCreatingAttr(String path) {
		return false;
	}

	public UIFrame getInterface(KrnObject uiObj, KrnObject[] objs, long tid,
			int mode, long flowId, boolean shareCash, boolean fork) throws KrnException {
		return null;
	}

	public UIFrame getInterfacePanel(KrnObject uiObj, KrnObject[] objs,
			long tid, int mode, boolean shareCash, boolean fork) throws KrnException {
		return null;
	}

	public void releaseInterface(boolean commit) {
	}

	public KrnObject getInterfaceLang() {
		return null;
	}

	public KrnObject getDataLang() {
		return null;
	}

	public void clearFrame() {
	}

	public Kernel getKernel() {
		return krn;
	}

	public UIFrame getCurrentInterface() {
		return null;
	}

    @Override
    public CommitResult beforePrevious(boolean check, boolean canIgnore, String titleContinueEdit, String titleiIgnoreError) throws KrnException {
        return CommitResult.CONTINUE_EDIT;
    }

    @Override
    public CommitResult beforePrevious(boolean check, boolean canIgnore) throws KrnException {
        return beforePrevious(check, canIgnore, null, null);
    }

    @Override
    public CommitResult beforePrevious() throws KrnException {
        return beforePrevious(true, true);
    }

    @Override
    public void afterPrevious(boolean isShow, boolean check, CommitResult cr) throws KrnException {}

}
