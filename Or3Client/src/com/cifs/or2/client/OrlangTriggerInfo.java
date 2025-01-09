package com.cifs.or2.client;

import java.io.Serializable;
import java.util.Arrays;

public class OrlangTriggerInfo implements Serializable {
	
	private int triggerType;
	private int ownerType;	// 0 - триггер класса, 1 - триггер атрибута
	private long ownerId; 
	private String ownerName; 
	private String name;
	private byte[] expression;
	private int transaction;
	
	public OrlangTriggerInfo(int triggerType, int ownerType, long ownerId, String ownerName, String name, byte[] expression, int transaction) {
		this.triggerType = triggerType;
		this.ownerType = ownerType;
		this.ownerId = ownerId;
		this.ownerName = ownerName; 
		this.name = name; 
		this.expression = expression;
		this.transaction = transaction;
	}

	public int getTriggerType() {
		return triggerType;
	}
	
	public int getOwnerType() {
		return ownerType;
	}

	public long getOwnerId() {
		return ownerId;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public String getName() {
		return name;
	}

	public byte[] getExpression() {
		return expression != null ? Arrays.copyOf(expression, expression.length) : null;
	}
	
	public int getTransaction() {
		return transaction;
	}
}