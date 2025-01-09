package com.cifs.or2.kernel;
/**
 * Created by Eclipse
 * User: Naik
 * Date: 05.04.2011
 * Time: 19:24:00
 */
//Ключ индекса (один столбец в многостолбцовом индексе)
public final class KrnIndexKey implements java.io.Serializable{	
	long indexId = 0;		//id индекса
	long attrId = 0;		//id атрибута
	long keyNo = 0;			//порядковый номер столбца в индексе
	boolean isDesc = false; //asc(false) или desc(true)
	public KrnIndexKey(long indexId,long attrId,long keyNo,boolean isDesc){		
		this.indexId = indexId;
		this.attrId = attrId;
		this.keyNo = keyNo;
		this.isDesc = isDesc;
	}
	//вернуть id индекса, в составе которого данных ключ
	public long getIndexId(){
		return this.indexId;
	}
	//вернуть id атрибута
	public long getAttributeId(){
		return this.attrId;
	}
	//вернуть порядковый номер ключа в индексе
	public long getKeyOrderNumber(){
		return this.keyNo;
	}
	//вернуть порядок: по возрастанию или по убыванию
	public boolean isDesc(){
		return this.isDesc;
	}
}
