package kz.tamur.rt;

import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.client.Kernel;

import kz.tamur.rt.adapters.UIFrame;
import kz.tamur.rt.data.Cache;

public interface InterfaceManager {
    int READONLY_MODE = 0x01;
    int SERVICE_MODE = 0x02;
    int ARCH_RO_MODE = 0x03;
    int SPR_RW_MODE = 0x04;
    int SPR_RO_MODE = 0x05;
    int ARCH_RW_MODE = 0x06;

    public static enum CommitResult {
        CONTINUE_EDIT, WITH_FATAL_ERRORS, WITH_ERRORS, WITHOUT_ERRORS
    }

    boolean absolute(KrnObject uiObj, KrnObject[] objs, String refPath, int mode, boolean isHiperTree, long tid, long flowId,
            boolean isBlockErrors, String uiType) throws KrnException;

    Cache getCash();

    int getEvaluationMode();

    UIFrame getInterface(KrnObject uiObj, KrnObject[] objs, long tid, int mode, long flowId, boolean shareCash, boolean fork) throws KrnException;

    UIFrame getInterfacePanel(KrnObject uiObj, KrnObject[] objs, long tid, int mode, boolean shareCash, boolean fork) throws KrnException;

    void releaseInterface(boolean commit);

    KrnObject getInterfaceLang();

    KrnObject getDataLang();

    Kernel getKernel();

    UIFrame getCurrentInterface();

    void afterPrevious(boolean isShow, boolean check, CommitResult cr) throws KrnException;

    CommitResult beforePrevious() throws KrnException;

    CommitResult beforePrevious(boolean check, boolean canIgnore) throws KrnException;

    CommitResult beforePrevious(boolean check, boolean canIgnore, String titleContinueEdit, String titleiIgnoreError) throws KrnException;
}