package kz.tamur.guidesigner.scheduler;

import java.util.Vector;

import org.jdom.Document;

import com.cifs.or2.kernel.KrnDate;
import com.cifs.or2.kernel.KrnObject;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 09.06.2005
 * Time: 10:38:32
 * To change this template use File | Settings | File Templates.
 */
public class TimerObject extends SchedObject{
    public SchedObject user;
    public Vector<SchedObject> srvs;
    public Document doc;
    public long redy;
    public String monthCol;
    public String monthsDaysCol;
    public String weekDaysCol;
    public String hourCol;
    public String minuteCol;
    public boolean isModified = false;
    public String timeStart;
    public String timeFinish;
    public boolean err;
    
    public TimerObject(KrnObject obj) {
        super(obj);
    }
}