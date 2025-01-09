package kz.tamur.admin;

import javax.swing.Icon;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;

public class IndexKeyListNode{		
	private KrnAttribute attr = null;
	private boolean isDesc = false;
	private String nodeText = null;
	
	public IndexKeyListNode(String nodeText){
		this.nodeText = nodeText;
	}
	
	public IndexKeyListNode(KrnAttribute attr){
		this(attr,false);
	}
	
	public IndexKeyListNode(KrnAttribute attr,boolean isDesc){
		this.attr = attr;
		this.isDesc = isDesc;
	}
		
	public KrnAttribute getAttribute(){
		return this.attr;
	}
	
	public boolean isDesc(){
		return this.isDesc;
	}
	
	public String getText(){
		if(isShowNoData()){
			return this.nodeText;
		}else{
			return attr.name; 
		}
	}
	
	public String getSubText(){
		if(isShowNoData()){
			return "";
		}
		try{
			return Kernel.instance().getClass(attr.typeClassId).name;	
			
		}catch(KrnException e){
			return "тип класса не определен";
		}
	}
	
	public String getDescText(){
		if(this.isDesc){
			return " DESC";
		}
		return "";
	}
	
	public Icon getIcon(){
		if(isShowNoData()){
			return null;
		}
		return AttributeTreeIconLoader.getIcon(attr);
	}
	
	private boolean isShowNoData(){
		return this.nodeText != null || this.attr == null;
	}
	
}