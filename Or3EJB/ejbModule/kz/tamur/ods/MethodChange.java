package kz.tamur.ods;

import com.cifs.or2.kernel.KrnClass;

/**
 * Created by IntelliJ IDEA. User: daulet Date: 23.05.2006 Time: 11:44:40 To
 * change this template use File | Settings | File Templates.
 */
public class MethodChange extends ModelChange {
	public final String name;

	public final KrnClass cls;

	public final boolean isCMethod;

	public String expr;

	public String comment;

	public MethodChange(long id, int action, String entityId, String name,
			KrnClass cls, boolean isCMethod, String expr, String comment) {
		super(id, 3, action, entityId);
		this.name = name;
		this.cls = cls;
		this.isCMethod = isCMethod;
		this.expr = expr;
		this.comment = comment;
	}

	public MethodChange(long id, int action, String entityId, String name,
			KrnClass cls, boolean isCMethod) {
		super(id, 3, action, entityId);
		this.name = name;
		this.cls = cls;
		this.isCMethod = isCMethod;
	}
}
