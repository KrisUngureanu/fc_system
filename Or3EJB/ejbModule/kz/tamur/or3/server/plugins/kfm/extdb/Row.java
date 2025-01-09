package kz.tamur.or3.server.plugins.kfm.extdb;

import java.util.Date;

public class Row {

	public String id;
	public Date date;
	public double sum;
	public String knp;
	public String desc;
	
	public String senderName;
	public String senderRnn;
	public String senderCode;
	public String senderAct;
	public String senderBankName;
	public String senderBankId;
	
	public String receiverName;
	public String receiverRnn;
	public String receiverCode;
	public String receiverAct;
	public String receiverBankName;
	public String receiverBankId;
    public String toString(){
        return id+"  "+date+"  "+knp+"  "+sum;
    }
}
