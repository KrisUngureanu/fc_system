package com.cifs.or2.server.timer;

import com.cifs.or2.kernel.KrnObject;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 26.04.2005
 * Time: 17:06:15
 * To change this template use File | Settings | File Templates.
 */
public class Task{
    public int[] mins;
    public int[] hours;
    public int[] daysw;
    public int[] daysm;
    public int[] months;
    public long[] process;
    public KrnObject user;
    public Date date;
    public String title="";
    public TimerTaskImpl ttask;
    public int ready=0;
    public KrnObject obj;
    public Task(KrnObject obj) {
        this.obj=obj;
    }
}
