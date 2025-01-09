package kz.tamur.or3.server.plugins.kfm.extdb;

import java.util.ArrayList;
import java.util.List;

public class OperationType {

	private int days;
	private List<String> knps;
	
	public OperationType(int days) {
		this.days = days;
		this.knps = new ArrayList<String>();
	}
	
	public int getDays() {
		return days;
	}
	
	public OperationType addKnp(String knp) {
		knps.add(knp);
		return this;
	}
	
	public String[] getKnps() {
		return knps.toArray(new String[knps.size()]);
	}
}
