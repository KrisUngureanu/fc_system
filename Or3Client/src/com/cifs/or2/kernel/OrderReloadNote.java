package com.cifs.or2.kernel;

import java.util.Date;
import java.util.List;

public class OrderReloadNote extends Note {
	
	public final String operation;
	public final String type;
	public final List<String> orderIds;

	public OrderReloadNote(Date time, UserSessionValue from, String operation, String type, List<String> orderIds) {
		super(time, from);
		this.operation = operation;
		this.type = type;
		this.orderIds = orderIds;
	}

}
