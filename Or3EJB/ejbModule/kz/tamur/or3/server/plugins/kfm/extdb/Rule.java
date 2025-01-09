package kz.tamur.or3.server.plugins.kfm.extdb;

import java.util.ArrayList;
import java.util.List;

public class Rule {
	
	public static final int OP_EQ = 0;
	public static final int OP_LT = 1;
	public static final int OP_LE = 2;
	public static final int OP_GT = 3;
	public static final int OP_GE = 4;

	public double sum;
	public int sumOp;
	
	private List<OperationType> incOpTypes;
	
	public List<String> incRnns;
	
	public double getSum() {
		return sum;
	}
	
	public int getSumOperation() {
		return sumOp;
	}
	
	public Rule setSum(double sum, int sumOp) {
		this.sum = sum;
		this.sumOp = sumOp;
		return this;
	}
	
	public OperationType[] getOperationTypes() {
		if (incOpTypes != null) {
			return incOpTypes.toArray(new OperationType[incOpTypes.size()]);
		}
		return null;
	}
	
	public Rule addIncludeOperationType(OperationType opType) {
		if (incOpTypes == null) {
			incOpTypes = new ArrayList<OperationType>();
		}
		incOpTypes.add(opType);
		return this;
	}

	public String[] getIncludeRnns() {
		if (incRnns != null) {
			return incRnns.toArray(new String[incRnns.size()]);
		}
		return null;
	}
	
	public Rule addIncludeRnn(String rnn) {
		if (incRnns == null) {
			incRnns = new ArrayList<String>();
		}
		incRnns.add(rnn);
		return this;
	}
}
