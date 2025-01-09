package com.cifs.or2.server.replicator;

import java.io.*;
import com.cifs.or2.kernel.*;
import com.cifs.or2.server.Session;

import kz.tamur.or3ee.server.kit.SrvUtils;

public class Sender
{
  private static StringBuffer indent_ = new StringBuffer();

  private static void writeClass (KrnClass cls, Session s, PrintWriter pw)
  throws KrnException
  {
    pw.println (indent_ +
      "<Class id=\"" + cls.id +
      "\" name=\"" + cls.name +
      "\" baseId=\"" + cls.parentId + "\">"
    );

    indent_.append ("  ");
    // Выводим дочерние классы
    KrnClass[] classes = s.getClasses (cls.id);
    for (int i = 0; i < classes.length; ++i)
      writeClass (classes [i], s, pw);

    // Выводим атрибуты
    KrnAttribute[] attrs = s.getAttributes (cls);
    for (int i = 0; i < attrs.length; ++i)
      if (attrs [i].classId == cls.id)
        writeAttribute (attrs [i], s, pw);

    indent_.setLength (indent_.length() - 2);

    pw.println (indent_ + "</Class>");
  }

  public static void process (File file) throws Exception
  {
    try {
      Session s = SrvUtils.getSession();
      try {
        PrintWriter pw = new PrintWriter (new FileOutputStream (file));
        pw.println ("<?xml version=\"1.0\" encoding=\"Windows-1251\"?>");
        pw.println ("<Root>");
        indent_.append("  ");
        KrnClass[] classes = s.getClasses (0);
        for (int i = 0; i < classes.length; ++i) {
          writeClass (classes [i], s, pw);
        }
        pw.println ("</Root>");
        pw.close();
      }
      finally {
        s.release();
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
      throw ex;
    }
  }

  public static void writeAttribute (
    KrnAttribute attr,
    Session s,
    PrintWriter pw
  ) throws KrnException
  {
    pw.println (indent_ +
      "<Attribute id=\"" + attr.id +
      "\" name=\"" + attr.name +
      "\" typeClassId=\"" + attr.typeClassId +
            "\" isArray=\"" + (attr.collectionType) +
            "\" isUnique=\"" + (attr.isUnique ? 1 : 0) +
      "\" />"
    );
  }
}