package kz.tamur.admin;


import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnObject;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 11.08.2003
 * Time: 10:53:55
 * To change this template use Options | File Templates.
 */
public class FilterObject extends IerObject {
  public KrnAttribute[] Attr_;
  public String className_="";
  public String left_="";
  public String operator_="";
  public String rightStr_="";
  public KrnObject[]  rightObj_;
  public int dateSelect_=0;
  public int dateSql_=0;
  public int dateEnd_=0;
  public String kolObj_="0";
  public String kolOp_="";
  public int union_=0;
  public boolean maxind_=false;
  public boolean maxval_=false;
  public boolean trans_=false;
  public boolean fill_=false;
  public int[] t_ind_=new int[]{0};
  public int[] d_ind_=new int[]{0,0};
  public FilterObject(KrnObject o){super(o);}
  public String toString(){return super.title_;}
}
