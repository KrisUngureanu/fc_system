package com.cifs.or2.server;

import javax.swing.*;
import java.awt.*;

public class ServerMonitorPage extends javax.swing.JPanel
{
  java.util.ResourceBundle res = java.util.ResourceBundle.getBundle("com.cifs.or2.server.ServerResources");
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  GridLayout gridLayout1 = new GridLayout(1, 2, 3, 0);
  javax.swing.JPanel panelOuter1 = new javax.swing.JPanel();

  javax.swing.JPanel panelObjects1 = new javax.swing.JPanel();
  JLabel labelObjects1 = new JLabel();
  JTextField textObjects1 = new JTextField();
  int objectsCounter = 0;

  Object monitoredObject;


  public ServerMonitorPage(Object obj)
  {
    monitoredObject = obj;
    try
    {
      jbInit();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }

  private void jbInit() throws Exception
  {
    panelOuter1.setLayout(gridBagLayout1);
    panelObjects1.setLayout(gridLayout1);
    textObjects1.setEnabled(false);
    labelObjects1.setText(res.getString("numberObjects"));
    labelObjects1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    panelObjects1.setVisible(false);
    panelObjects1.add(labelObjects1);
    panelObjects1.add(textObjects1);
    panelOuter1.add(panelObjects1,
      new java.awt.GridBagConstraints(1, 2, 2, 1, 1.0, 1.0,
        java.awt.GridBagConstraints.NORTH, java.awt.GridBagConstraints.HORIZONTAL,
        new Insets(3, 0, 3, 3), 0, 0));

    add(panelOuter1);
  }

  public void showObjectCounter(boolean bVisible)
  {
    panelObjects1.setVisible(bVisible);
    refresh();
  }

  public synchronized void updateObjectCounter(int n)
  {
    objectsCounter += n;
    textObjects1.setText(String.valueOf(objectsCounter));
  }

  public void refresh()
  {
    textObjects1.setText(String.valueOf(objectsCounter));
  }
}
