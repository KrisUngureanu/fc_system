package com.cifs.or2.client.replicator;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

import com.cifs.or2.kernel.*;
import com.cifs.or2.client.Kernel;


public class ConfirmFileClient extends JDialog {
  JPanel panel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JList jList1 = new JList();
  JButton jButton1 = new JButton();
  Kernel krn;

  public ConfirmFileClient(Frame frame) {
    super(frame, "Выберите базу", true);
    try {
			krn = Kernel.instance();
      jbInit();
      pack();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  void jbInit() throws Exception {
    this.setSize(400, 180);
    this.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - getSize().width) / 2, (Toolkit.getDefaultToolkit().getScreenSize().height - getSize().height) / 2);
    panel1.setLayout(borderLayout1);
    jList1.setBorder(BorderFactory.createLineBorder(Color.black));
    jButton1.setText("Создать");
    jButton1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try
        {
          jButton1_actionPerformed(e);
        }
        catch(Exception ex) {
          ex.printStackTrace();
        }
      }
    });
    getContentPane().add(panel1);
    panel1.add(jList1, BorderLayout.WEST);
    panel1.add(jButton1, BorderLayout.SOUTH);

//		Database curDb = new Database(krn.getCurrentDbId());
//		System.out.println("curDb.toString() = " + curDb.toString());
//		DefaultListModel lm = new DefaultListModel();
//
//		KrnObject parent = krn.getParentDbId();
//		if (parent.id != 0) {
//			Database parentDb = new Database(parent);
//			lm.addElement (parentDb);
//		}
//		KrnObject[] childDbs = krn.getChildDbs(false,true);
//    for (int i = 0; i < childDbs.length; ++i)
//      lm.addElement (new Database(childDbs [i]));
//    jList1.setModel(lm);
  }

  void jButton1_actionPerformed(ActionEvent e) throws java.io.IOException, KrnException
  {
    Database db = (Database)jList1.getSelectedValue();
    System.out.println("select = "+db);
    krn.createConfirmationFile(db.obj.id);
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