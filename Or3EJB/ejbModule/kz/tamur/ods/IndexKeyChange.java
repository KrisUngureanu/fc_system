package kz.tamur.ods;

import com.cifs.or2.kernel.KrnAttribute;

public class IndexKeyChange{
	private KrnAttribute krnAttr;	//атрибут
	private long keyno;				//порядковый номер в индексе
	private boolean isDesc;			//acs или desc(по убыванию)
	public IndexKeyChange(KrnAttribute krnAttr,long keyno,boolean isDesc){		
		this.krnAttr = krnAttr;
		this.keyno = keyno;
		this.isDesc = isDesc;
	}
	public KrnAttribute getKrnAttr(){
		return this.krnAttr;
	}
	public long getKeyNo(){
		return this.keyno;
	}
	public boolean isDesc(){
		return this.isDesc;
	}
}
