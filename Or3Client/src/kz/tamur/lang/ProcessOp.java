package kz.tamur.lang;

import java.util.Map;

import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;

public abstract class ProcessOp {
	public abstract long startProcess(KrnObject def, Map<String, Object> vars) throws KrnException;
	public abstract long stopProcess(long flowId) throws KrnException;
	public abstract long findProcess(KrnObject def, KrnObject obj) throws KrnException;
}
