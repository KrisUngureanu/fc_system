package kz.tamur.ods;

import static com.cifs.or2.kernel.ModelChange.ENTITY_TYPE_INDEX;

import java.util.ArrayList;
import java.util.List;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;

import kz.tamur.util.Funcs;

public class IndexChange extends ModelChange{
	private KrnClass cls;
	private List<IndexKeyChange> keyChanges;	
	public IndexChange(long id,int action,String entityId){
		this(id,action,entityId,null);
	}
	public IndexChange(long id,int action,String entityId,KrnClass cls){
		super(id,ENTITY_TYPE_INDEX,action,entityId);				
		this.cls = cls;
		keyChanges = new ArrayList<IndexKeyChange>();
	}	 
	public void addKeyChange(IndexKeyChange keyChange){
		if(keyChanges == null){
			keyChanges = new ArrayList<IndexKeyChange>();
		}			
		keyChanges.add(keyChange);
	}
	
	public void setKrnClass(KrnClass cls){
		this.cls = cls;
	}
	
	public KrnClass getKrnClass(){
		return this.cls;
	}
	
	public KrnAttribute[] getKrnAttributes(){		
		if(keyChanges == null || keyChanges.size() == 0)
			return new KrnAttribute[0];
		int size = keyChanges.size();
		KrnAttribute[] ret = new KrnAttribute[size];
		for(int i=0;i<size;i++){			
			ret[i] = keyChanges.get(i).getKrnAttr();
		}		
		return ret;
	}
	
	public boolean[] getIsDecs(){
		if(keyChanges == null || keyChanges.size() == 0)
			return new boolean[0];
		int size = keyChanges.size();
		boolean[] ret = new boolean[size];
		for(int i=0;i<size;i++){
			ret[i] = keyChanges.get(i).isDesc();
		}
		return ret;
	}
		
	public String getContentXML(){
		return getKeysXML(); 
	}
	private String getKeysXML(){
		String keysXML = "";
		if(keyChanges != null && keyChanges.size() > 0){
			keysXML += "<Keys>";
			for(IndexKeyChange keyChange : keyChanges){
				keysXML += "<Key attr=\"" + Funcs.sanitizeHtml(keyChange.getKrnAttr().uid) + "\"" +
						" keyno=\"" + keyChange.getKeyNo() + "\"" +
						" isDesc=\"" + (keyChange.isDesc() ? 1 : 0) + "\"" +
						"></Key>";				
			}
			keysXML += "</Keys>";
		}
		return keysXML;
	}
	
}
