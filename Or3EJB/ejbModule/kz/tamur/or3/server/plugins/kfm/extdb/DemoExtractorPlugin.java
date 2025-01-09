package kz.tamur.or3.server.plugins.kfm.extdb;

import java.sql.*;
import java.util.*;
import java.util.Date;

import com.cifs.or2.kernel.KrnDate;


public class DemoExtractorPlugin extends ExtractorPlugin {
    private Connection conn;
    private static final String sql_oper="SELECT DISTINCT o.id,o.op_date, o.sum, o.knp,o.descr,"
                                       +"s.name as name_sender,s.rnn as rnn_sender,s.code as code_sender,"
                                       +"s.account as account_sender,s.bank_name as bank_name_sender,s.bank_id as bank_id_sender,"
                                       +"r.name as name_receiver,r.rnn as rnn_receiver,r.code as code_receiver,"
                                       +"r.account as account_receiver,r.bank_name as bank_name_receiver,r.bank_id as bank_id_receiver"
                                       +" FROM OPER o LEFT JOIN AGENT s ON o.sender=s.id LEFT JOIN AGENT r ON o.receiver=r.id";

    public Collection<Row> extract(KrnDate date, Collection<Rule> rules) {
        Collection<Row> res=new ArrayList<Row>();
        Vector<Date> dates=new Vector<Date>();
        boolean flag=false;
        String sql_rules="",sql_where="";
        if(rules.size()>0)
            sql_where +=" WHERE ";
        if(rules.size()>1)
            sql_where +="(";
        int t_join=0;
        for(Rule rule:rules){
            String sql_rule_join="",sql_rule_where="";
            if(rule.incRnns!=null && rule.incRnns.size()>0){
                //отбор по конкретным рнн
                if(rule.incRnns.size()==1){
                    sql_rule_where += "s.rnn ="+rule.incRnns.get(0);
                }else{
                    sql_rule_where += "s.rnn IN ("+rule.incRnns.get(0);
                    for(int i=1;i<rule.incRnns.size();i++){
                        sql_rule_where += ","+ rule.incRnns.get(i);
                    }
                    sql_rule_where +=")";
                }
            }else{
                OperationType[] types=rule.getOperationTypes();
                if(types!=null){
                    //отбор по типам операций (кнп)
                    for(OperationType type:types){
                        String sql_join="",sql_type_where="",sql_knp="",sql_date="",sql_rnn="";
                        String[] knps=type.getKnps();
                        if(knps.length>0){
                            if(knps.length==1){
                                sql_knp += "o.knp ="+knps[0];
                            }else{
                                sql_knp += "o.knp IN ("+knps[0];
                                for(int i=1;i<knps.length;i++){
                                    sql_knp += ","+ knps[i];
                                }
                                sql_knp +=")";
                            }
                            sql_type_where += sql_knp;
                        }
                        int day_before=type.getDays();
                        if(day_before>0){
                            //суммирование за определенное количество дней
                            sql_join=" LEFT JOIN (SELECT rnn, SUM(o.sum) AS sum FROM oper o,agent s WHERE o.sender=s.id";
                            sql_join += !sql_knp.equals("")?" AND "+sql_knp:"";
                            Date date_before=getDateBefore(date,day_before);
                            dates.add(date_before);
                            dates.add(date);
                            sql_date += "o.op_date>? AND o.op_date<=?";
                            sql_join += " AND "+sql_date;
                            sql_join +=" GROUP BY rnn) rule_"+t_join+" ON rule_"+t_join+".rnn=s.rnn AND rule_"+t_join
                                       +".sum "+getStringOp(rule.sumOp)+" "+rule.sum;
                            sql_rnn += " AND s.rnn IN rule_"+t_join+".rnn";
                            t_join++;
                            sql_type_where += (!sql_type_where.equals("")?" AND ":"")+sql_date;
                            dates.add(date_before);
                            dates.add(date);
                        }else{
                            //операции на конкретную дату
                            dates.add(date);
                            sql_type_where += (!sql_type_where.equals("")?" AND ":"")+"o.op_date=? "+(rule.sum>0?"AND o.sum "+getStringOp(rule.sumOp)+" "+rule.sum:"");
                        }
                        //для каждого набора операций отбор по или

                        if(!sql_type_where.equals("")){
                            sql_type_where = "("+sql_type_where+sql_rnn+")";
                            sql_rule_where += (sql_rule_where.equals("")?"":" OR ")+sql_type_where;
                        }
                        sql_rule_join +=sql_join;
                    }
                }else{
                    //все операции на конкретную дату на сумму больше заданной
                    dates.add(date);
                    sql_rule_where = "o.op_date=? "+(rule.sum>0?"AND o.sum "+getStringOp(rule.sumOp)+" "+rule.sum:"");
                }
            }
            if(!sql_rule_where.equals("")){
                //для каждого правила отбор по или
                if(flag){
                    sql_rule_where =" OR ("+sql_rule_where+")";

                }else{
                    flag=true;
                }
            }
            sql_where +=sql_rule_where;
            sql_rules +=sql_rule_join;
        }
        if(rules.size()>1)
            sql_where +=")";
        String sql=sql_oper+sql_rules+sql_where;
        System.out.println("sql:"+sql);
        System.out.println("dates:"+dates.size());
        try{
            conn= DriverManager.getConnection("jdbc:apache:commons:dbcp:/ext_ora");
            PreparedStatement pst=conn.prepareStatement(sql);
            //подстановка всех дат привлеченныъ в запросе
            for(int i=0;i<dates.size();i++){
                pst.setDate(i+1, new java.sql.Date(dates.get(i).getTime()));
            }
            ResultSet set = pst.executeQuery();
            while (set.next()) {
                Row row=new Row();
                row.id=set.getString("id");
                row.date=set.getDate("op_date");
                row.sum=set.getDouble("sum");
                row.knp=set.getString("knp");
                row.desc=set.getString("descr");
                row.senderName=set.getString("name_sender");
                row.senderRnn=set.getString("rnn_sender");
                row.senderCode=set.getString("code_sender");
                row.senderBankName=set.getString("bank_name_sender");
                row.senderBankId=set.getString("bank_id_sender");
                row.senderAct=set.getString("account_sender");
                row.receiverName=set.getString("name_receiver");
                row.receiverRnn=set.getString("rnn_receiver");
                row.receiverCode=set.getString("code_receiver");
                row.receiverBankName=set.getString("bank_name_receiver");
                row.receiverBankId=set.getString("bank_id_receiver");
                row.receiverAct=set.getString("account_receiver");
                res.add(row);
            }
            conn.close();
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        return res;
	}
    private String getStringOp(int op){
        String res="";
        if(op==Rule.OP_EQ)
            res= "=";
        else if(op==Rule.OP_GE)
            res=">=";
        else if(op==Rule.OP_GT)
            res=">";
        else if(op==Rule.OP_LE)
            res="<=";
        else if(op==Rule.OP_LT)
            res=">";
        return res;
    }
    public java.sql.Date convertDate(com.cifs.or2.kernel.Date date) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, date.day);
        cal.set(Calendar.MONTH, date.month);
        cal.set(Calendar.YEAR, date.year);
        return new java.sql.Date(cal.getTimeInMillis());
    }
    public static Date getDateBefore(Date date,int day_before){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, -day_before);
        Date date_before= c.getTime();
        return date_before;
    }

    public static void main(String[] args) throws Exception {

		List<Rule> rules = new ArrayList<Rule>();
		
		Rule rule1 = new Rule();
		rule1.sum = 350;
		rule1.sumOp = Rule.OP_GE;
		
		OperationType opType1 = new OperationType(0);
		opType1.addKnp("911").addKnp("912");
		
		OperationType opType2 = new OperationType(7);
		opType2.addKnp("711").addKnp("712");
		
		rule1.addIncludeOperationType(opType1).addIncludeOperationType(opType2);
		
		Rule rule2 = new Rule();
		rule2.addIncludeRnn("000000000000").addIncludeRnn("111111111111");
		rules.add(rule1);
		rules.add(rule2);
		ExtractorPlugin extractor = new DemoExtractorPlugin();
        Date date=getDateBefore(new Date(),1);
        Collection<Row> rows = extractor.extract(new KrnDate(date.getTime()), rules);
		for(Row row : rows) {
			//System.out.println(row);
		}
	}
}
