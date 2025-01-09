package kz.tamur.ods;

import com.cifs.or2.kernel.KrnClass;

/**
 * Created by IntelliJ IDEA. User: daulet Date: 23.05.2006 Time: 11:15:24 To
 * change this template use File | Settings | File Templates.
 */
public class ClassChange extends ModelChange {
	public final String name;

	public final KrnClass parentCls;

	public final boolean isRepl;

	public String comment;
	
	public final String tname;

	public final int mod;

	public ClassChange(
			long id,
			int action,
			String entityId,
			String name,
			KrnClass parentCls,
			boolean isRepl,
			String comment,
			String tname,
			int mod
			) {
		super(id, 0, action, entityId);
		this.name = name;
		this.parentCls = parentCls;
		this.isRepl = isRepl;
		this.comment = comment;
		this.tname = tname;
		this.mod = mod;
	}

	public ClassChange(
			long id,
			int action,
			String entityId,
			String name,
			KrnClass parentCls,
			boolean isRepl,
			String tname,
			int mod
			) {
		super(id, 0, action, entityId);
		this.name = name;
		this.parentCls = parentCls;
		this.isRepl = isRepl;
		this.tname = tname;
		this.mod = mod;
	}
}