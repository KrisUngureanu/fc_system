package kz.tamur.util;

import com.cifs.or2.kernel.KrnAttribute;

public class KrnAttributeItem {
	
	public KrnAttribute attr;
	
	public KrnAttributeItem(KrnAttribute attr) {
		this.attr = attr;
	}

	@Override
	public String toString() {
		return attr.name;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof KrnAttributeItem) {
			KrnAttribute attr1 = ((KrnAttributeItem)obj).attr;
			return (attr == attr1) ? true : attr.id == attr1.id;
		}
		return false;
	}
}
