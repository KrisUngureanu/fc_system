/*
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 03.10.2002
 * Time: 17:54:51
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package kz.tamur.admin;

import com.cifs.or2.kernel.KrnObject;

import javax.swing.*;

//Иерархический обьект
public class IerObject implements Comparable{
  public int numIer_=0;
  public boolean visible_=false;
  public int head_=0;
  public int type_obj=0;
  public int sprUi_=-1;
  public int parent_=-1;
  public KrnObject obj_;
  public String title_="*";
  public ImageIcon icon_;
  public ImageIcon open_icon_;
  public IerObject(KrnObject o){obj_=o;}
  public String toString(){return title_;}
  public int compareTo(Object o)
  { int res= this.title_.compareTo(((IerObject)o).title_);
    if(res==0 && !this.obj_.equals(o)) res=this.obj_.id>((IerObject)o).obj_.id?1:-1;
    return res;
  }
}

