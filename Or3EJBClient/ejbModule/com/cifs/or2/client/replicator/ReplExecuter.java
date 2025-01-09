/*
 * Created by IntelliJ IDEA.
 * User: daulet
 * Date: Jan 22, 2003
 * Time: 2:13:20 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.cifs.or2.client.replicator;

import com.cifs.or2.client.Kernel;

import java.text.SimpleDateFormat;

import kz.tamur.Or3Frame;

public class ReplExecuter {
  public static void main(String[] argv)
  {
    Kernel krn = null;
    try {
      try {
        SimpleDateFormat formatter_time = new SimpleDateFormat ("dd.MM.yyyy HH:mm:ss");
        String time = "["+formatter_time.format(new java.util.Date())+"] ";
        System.out.println(time+"replication started...");
        String baseName = Or3Frame.getBaseName();
        //Kernel.instance().init(baseName,"", "", "sys", "123",null);
        krn = Kernel.instance();
        krn.runReplication();
        time = "["+formatter_time.format(new java.util.Date())+"] ";
        System.out.println(time+"replication ended.");
      } catch (Exception e) {
        System.out.println("error of replication.");
        e.printStackTrace();
      }
    } finally {
      if (krn != null)
        krn.release();
    }
  }
}
