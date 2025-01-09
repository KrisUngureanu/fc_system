package com.cifs.or2.kernel;
/**
 * Created by Eclipse
 * User: Naik
 * Date: 29.04.2011
 * Time: 18:33:00
 */
//Класс, хранящий единицу изменения в модели
public final class KrnChangeCls {
	private long id;			//идентификатор изменения
	private long type;			//тип сущности
	private int action;		//действие: добавление, изменение или удаление
	private String entityUID;	//uid сущности	
	public KrnChangeCls(long id,long type,int action,String entityUID){
		this.id = id;
		this.type = type;
		this.action = action;
		this.entityUID = entityUID;		
	}
	public long getId(){
		return this.id;
	}
	public long getType(){
		return this.type;
	}
	public int getAction(){
		return this.action;
	}
	public String getEntityUID(){
		return this.entityUID;
	}
}
