package kz.tamur.rt;

import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.Activity;
import com.cifs.or2.client.Kernel;

import java.util.Map;

import kz.tamur.rt.adapters.OrRef;
import kz.tamur.rt.data.Cache;
import kz.tamur.comps.OrFrame;

public interface InterfaceManager {
    int READONLY_MODE = 0x01;
    int SERVICE_MODE = 0x02;
    int ARCH_RO_MODE = 0x03;
    int SPR_RW_MODE = 0x04;
    int SPR_RO_MODE = 0x05;
    int ARCH_RW_MODE = 0x06;

	public static enum CommitResult {
		CONTINUE_EDIT,
		WITH_FATAL_ERRORS,
		WITH_ERRORS,
		WITHOUT_ERRORS,
		SESSION_REALESED
	}

	CommitResult beforePrevious() throws KrnException;
	CommitResult beforePrevious(boolean check, boolean canIgnore) throws KrnException;
	boolean afterPrevious(boolean isShow, boolean check, boolean isSend, CommitResult cr) throws KrnException;
	
    boolean absolute(KrnObject uiObj, KrnObject[] objs, String refPath, int mode,
                     boolean isHiperTree, long tid, boolean shareCash, long flowId, boolean isBlockErrors, String uiType)
            throws KrnException;

    Map<String, OrRef> getRefs();

    Map<String, OrRef> getContentRefs();

    Cache getCash();

    int getEvaluationMode();

    boolean isCreatingAttr(String path);

    OrFrame getInterface(KrnObject uiObj, KrnObject[] objs, long tid,
                                int mode,long flowId, boolean shareCash, boolean fork) throws KrnException;

    OrFrame getInterfacePanel(KrnObject uiObj, KrnObject[] objs, long tid,
                                int mode, boolean shareCash, boolean fork, boolean isPopup) throws KrnException;

    void releaseInterface(boolean commit);

    KrnObject getInterfaceLang();
    KrnObject getDataLang();

    void clearFrame();

    Kernel getKernel();

    OrFrame getCurrentFrame();
}