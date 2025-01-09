package com.cifs.or2.client;


import com.cifs.or2.kernel.KrnObject;

public class Filter
{
  public KrnObject obj;
  public String title;
  public String className;
  public int flags;

  public Filter (KrnObject obj, String title, String className, int flags)
  {
    this.obj = obj;
    this.title = title;
    this.className = className;
    this.flags = flags;
  }

  public String toString() { return title; }
}
