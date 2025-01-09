package kz.tamur.ods;

import com.cifs.or2.kernel.KrnAttribute;

class Column {
	
	public String name;
	public KrnAttribute attr;
	
	public Column(String name, KrnAttribute attr) {
		this.name = name;
		this.attr = attr;
	}
}
