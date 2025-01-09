package kz.tamur.admin;

import javax.swing.Icon;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;

public class IndexKeyAttrListNode {
	private KrnAttribute attr = null;
	public IndexKeyAttrListNode(KrnAttribute attr){
		this.attr = attr;
	}
	
	public KrnAttribute getAttribute(){
		return this.attr;
	}
	
	public String getText(){
		return attr.name;
	}
	
	public String getSubText(){		
		try{
			return Kernel.instance().getClass(attr.typeClassId).name;
		}catch(KrnException e){
			return "тип класса не определен";
		}
	}
	
	public Icon getIcon(){
		return AttributeTreeIconLoader.getIcon(attr);
	}
}


