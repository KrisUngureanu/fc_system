package kz.tamur.or3ee.common;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnMethod;

public interface ModelChangeListener {
	public void classCreated(KrnClass cls);
	public void attrCreated(KrnAttribute attr);
	public void classDeleted(KrnClass cls);
	public void attrDeleted(KrnAttribute attr);
	public void classChanged(KrnClass clsOld, KrnClass clsNew);
	public void attrChanged(KrnAttribute attrOld, KrnAttribute attrNew);
	public void methodCreated(KrnMethod m);
	public void methodDeleted(KrnMethod m);
	public void methodChanged(KrnMethod oldm, KrnMethod newm);
}