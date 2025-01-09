package kz.tamur.guidesigner.terminal;

import java.util.Vector;

public class VariableObject {
	public Vector<VariableObject> vrs = new Vector<VariableObject>();
	public String name;
	public String type;
	public Object var;
	
	public VariableObject(Object obj){
		this.var = obj;
		if(obj!=null) {
			this.type = obj.getClass().getSimpleName();
			this.name = obj.getClass().getCanonicalName();
		}
	}
	
	public String toString() {
		return name;
	}

}
