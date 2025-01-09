package kz.tamur.guidesigner.scheduler;

import com.cifs.or2.kernel.KrnObject;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 28.04.2005
 * Time: 9:58:40
 * To change this template use File | Settings | File Templates.
 */
public class SchedObject{
    public KrnObject obj;
    public String title="";
    public String oldName="";

    public SchedObject(KrnObject obj) {
        this.obj = obj;
    }

    public String toString(){
        return title;
    }
}
