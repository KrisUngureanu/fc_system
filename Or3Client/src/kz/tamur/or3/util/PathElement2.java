package kz.tamur.or3.util;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;

public class PathElement2 {
	
	public final KrnClass type;
	public final KrnAttribute attr;
	public final Object index;
	public final String filterName;

	public PathElement2(final KrnClass type, final KrnAttribute attr, final Object index, final String filterName) {
		super();
		this.type = type;
		this.attr = attr;
		this.index = index;
		this.filterName = filterName;
	}
	
	public PathElement2(final KrnClass type, final KrnAttribute attr, final Object index) {
		this(type, attr, index, null);
	}

	public String noCastString() {
		if (attr == null) {
			if (filterName == null)
				return type.name;
			else
				return type.name + "(" + filterName + ")";
		}
		String res = attr.name;
		if (filterName != null)
			res += "(" + filterName + ")";
		
		if (index instanceof Number)
			res += "[" + index + "]";
		else if (index == null)
			res += "[]";
		return res;
	}
	
	public String toString() {
		String res = noCastString();
		if (attr != null && type.id != attr.typeClassId)
			res += "<" + type.name + ">";
		return res;
	}
}
