/*
 * Created by IntelliJ IDEA.
 * User: daulet
 * Date: Jan 16, 2003
 * Time: 9:40:24 AM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.cifs.or2.client.replicator;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

public class ReplicationSeparately extends JDialog {
  private boolean replicationEnded = true;
  class RunReplImport extends Thread {
    private long DbId;
    public RunReplImport (long ADbId) {
      this.DbId = ADbId;
    }
    public void run() {
      try {
        try {
          krn.setChanges(null);
        } catch (Exception e) {
          e.printStackTrace();
        }
      } finally {
        replicationEnded = true;
        btnImport.setEnabled(true);
        btnNextExport.setEnabled(true);
        btnSecondExport.setEnabled(true);
        jList1.setEnabled(true);
        progress.setValue(progress.getMinimum());
      }
    }
  }
  class RunReplExport extends Thread {
    private long DbId;
    private int ReplAction;
    public RunReplExport (long ADbId, int AReplAction) {
      this.DbId = ADbId;
      this.ReplAction = AReplAction;
    }
    public void run() {
      try {
        try {
          krn.getChanges(ReplAction, "", "", "");
        } catch (Exception e) {
          e.printStackTrace();
        }
      } finally {
        replicationEnded = true;
        btnImport.setEnabled(true);
        btnNextExport.setEnabled(true);
        btnSecondExport.setEnabled(true);
        jList1.setEnabled(true);
        progress.setValue(progress.getMinimum());
      }
    }
  }
  class ProccessProgress extends Thread {
    private int DELAY = 10;
    public ProccessProgress() {
      progress.setMinimum(0);
      progress.setMaximum(100);
      progress.setValue(0);
    }
    public void run() {
      while (!replicationEnded) {
        try {
          sleep(DELAY);
          progress.setValue(progress.getValue()+1);
          if (progress.getValue() == 100)
            progress.setValue(0);
        } catch(InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }
  class MyThread extends Thread {
    private int DELAY = 10000;
    public void run() {
      try {
        sleep(DELAY);
      } catch(InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
  JPanel panel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JList jList1 = new JList();
  JButton btnImport = new JButton();
  JButton btnNextExport = new JButton();
  JButton btnSecondExport = new JButton();
  JProgressBar progress = new JProgressBar();
  Kernel krn;
  public ReplicationSeparately (Frame frame) {
    super(frame, "Репликация отдельно по базам", true);
    try {
			krn = Kernel.instance();
      jbInit();
      pack();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  protected void processWindowEvent(WindowEvent e) {
    if (e.getID() == WindowEvent.WINDOW_CLOSING)
      if (replicationEnded)
        super.processWindowEvent(e);
  }

  void jbInit() throws Exception {
    this.setSize(600, 400);
    this.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - getSize().width) / 2,
      (Toolkit.getDefaultToolkit().getScreenSize().height - getSize().height) / 2);
    panel1.setLayout(borderLayout1);
    jList1.setBorder(BorderFactory.createLineBorder(Color.black));
    btnImport.setText("Импортировать");
    btnImport.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try
        {
          btnImport_actionPerformed(e);
        }
        catch(Exception ex) {
          ex.printStackTrace();
        }
      }
    });
    btnNextExport.setText("Экспортировать новые изменения");
    btnNextExport.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try
        {
          btnNextExport_actionPerformed(e);
        }
        catch(Exception ex) {
          ex.printStackTrace();
        }
      }
    });
    btnSecondExport.setText("Экспортировать изменения предыдущего экспорта");
    btnSecondExport.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try
        {
          btnSecondExport_actionPerformed(e);
        }
        catch(Exception ex) {
          ex.printStackTrace();
        }
      }
    });
    JPanel bottobPane = new JPanel();
    panel1.add(bottobPane, BorderLayout.SOUTH);
    panel1.add(jList1, BorderLayout.CENTER);
    getContentPane().add(panel1);

    bottobPane.setLayout(new GridLayout(4,1));
    bottobPane.add(btnImport);
    bottobPane.add(btnNextExport);
    bottobPane.add(btnSecondExport);
    bottobPane.add(progress);

    Database curDb = new Database(krn.getCurrentDb());
    System.out.println("curDb.toString() = " + curDb.toString());
    DefaultListModel lm = new DefaultListModel();

    KrnObject parent = krn.getObjectsSingular(krn.getCurrentDb().id,
      krn.getAttributeByName(krn.getClassByName("Структура баз"),
      "родитель").id, false);
    if (parent != null) {
      Database parentDb = new Database(parent);
      lm.addElement (parentDb);
    }
    KrnObject[] childDbs = krn.getChildDbs(false,true);
    for (int i = 0; i < childDbs.length; ++i)
      lm.addElement (new Database(childDbs [i]));
    jList1.setModel(lm);
  }

  void btnImport_actionPerformed(ActionEvent e) throws  java.io.IOException, KrnException
  {
    Database db = (Database)jList1.getSelectedValue();
    if (db == null) return;
    System.out.println("select = "+db);
    replicationEnded = false;
    btnImport.setEnabled(false);
    btnNextExport.setEnabled(false);
    btnSecondExport.setEnabled(false);
    jList1.setEnabled(false);
    ProccessProgress p = new ProccessProgress();
    p.start();
    RunReplImport r = new RunReplImport(db.obj.id);
    r.start();
  }
  void btnNextExport_actionPerformed(ActionEvent e) throws  java.io.IOException, KrnException
  {
    Database db = (Database)jList1.getSelectedValue();
    if (db == null) return;
    System.out.println("select = "+db);
    replicationEnded = false;
    btnImport.setEnabled(false);
    btnNextExport.setEnabled(false);
    btnSecondExport.setEnabled(false);
    jList1.setEnabled(false);
    ProccessProgress p = new ProccessProgress();
    p.start();
    RunReplExport r = new RunReplExport(db.obj.id, Kernel.et_NEXT_EXPORT);
    r.start();
  }
  void btnSecondExport_actionPerformed(ActionEvent e)
    throws java.io.IOException, KrnException
  {
    Database db = (Database)jList1.getSelectedValue();
    if (db == null) return;
    System.out.println("select = "+db);
    replicationEnded = false;
    btnImport.setEnabled(false);
    btnNextExport.setEnabled(false);
    btnSecondExport.setEnabled(false);
    jList1.setEnabled(false);
    ProccessProgress p = new ProccessProgress();
    p.start();
    RunReplExport r = new RunReplExport(db.obj.id, Kernel.et_SECOND_EXPORT);
    r.start();
  }
  private class Database
  {
    KrnObject obj;
    private String title_;

    public Database (KrnObject o)
    {
      obj = o;
    }
    public String toString()
    {
      String res = "";
      try
      {
				KrnAttribute attr = krn.getAttributeByName(
					krn.getClassByName("Структура баз"),"значение");
				KrnObject o = krn.getObjectsSingular(obj.id, attr.id, false);
				attr = krn.getAttributeByName(krn.getClassByName("База"),"наименование");
				String s = krn.getStringsSingular(o.id, attr.id, 0, false, false);
        res = "["+String.valueOf(obj.id)+"] "+s;
      }
      catch(Exception ex) {
        ex.printStackTrace();
      }
      return res;
    }
  }
}
