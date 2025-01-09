package com.cifs.or2.client.replicator;

import com.cifs.or2.client.Kernel;

import java.text.SimpleDateFormat;

import kz.tamur.Or3Frame;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 27.11.2006
 * Time: 17:00:08
 * To change this template use File | Settings | File Templates.
 */
public class DbExportExecuter {
    public static void main(String[] argv)
    {
      Kernel krn = null;
      try {
        try {
          SimpleDateFormat formatter_time = new SimpleDateFormat ("dd.MM.yyyy HH:mm:ss");
          String time = "["+formatter_time.format(new java.util.Date())+"] ";
          System.out.println(time+"dbexport started...");
          String baseName=Or3Frame.getBaseName();
          //Kernel.instance().init(baseName, "", "", "sys", "123",null);
          krn = Kernel.instance();
          krn.dbExport("", "");
          time = "["+formatter_time.format(new java.util.Date())+"] ";
          System.out.println(time+"replication ended.");
        } catch (Exception e) {
          System.out.println("error of dbexport.");
          e.printStackTrace();
        }
      } finally {
        if (krn != null)
          krn.release();
      }
    }

}

