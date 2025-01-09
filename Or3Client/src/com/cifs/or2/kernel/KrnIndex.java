package com.cifs.or2.kernel;
/**
 * Created by Eclipse
 * User: Naik
 * Date: 05.04.2011
 * Time: 19:17:00
 */
//Многоатрибутный индекс
public final class KrnIndex implements java.io.Serializable{
	private long id;		//id индекса
	private long classId;	//id класса, к которому относится индекс
	private String uid;		//uid индекса	
	public KrnIndex(long id,long classId,String uid){
		this.id = id;
		this.classId = classId;
		this.uid = uid;
	}
	//вернуть id индекса
	public long getId(){
		return this.id;
	}
	//вернуть id класса, к которому принадлежит индекс
	public long getClassId(){
		return this.classId;
	}
	//вернуть uid индекса
	public String getUID(){
		return this.uid;
	}
}
