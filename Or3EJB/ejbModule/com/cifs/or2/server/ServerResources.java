package com.cifs.or2.server;

import java.text.MessageFormat;

public class ServerResources extends java.util.ListResourceBundle
{
  static final Object[][] contents =
  {
    { "created",         "{0} created" },
    { "isReady",         "{0} is ready!" },
    { "numberObjects",   "Number of objects created" },
    { "logTitle",        "{0} Log" }
  };

  public Object[][] getContents()
  {
    return contents;
  }

  public static String format(String pattern, Object p1)
  {
    return MessageFormat.format(pattern, new Object[] {p1});
  }

  public static String format(String pattern, Object p1, Object p2)
  {
    return MessageFormat.format(pattern, new Object[] {p1, p2});
  }

  public static String format(String pattern, Object p1, Object p2, Object p3)
  {
    return MessageFormat.format(pattern, new Object[] {p1, p2, p3});
  }

  public static String format(String pattern, Object[] objects)
  {
    return MessageFormat.format(pattern, objects);
  }
}
