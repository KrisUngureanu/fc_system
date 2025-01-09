package com.cifs.or2.server;

import java.sql.*;

public class SystemId
{
  private String name_;
  private int value_;
  private boolean isModified_;

  public SystemId(final String name, final int value)
  {
    value_ = value;
    isModified_ = false;
  }

  public synchronized int getNextValue()
  {
    isModified_ = true;
    return ++value_;
  }

  public synchronized int getValue()
  {
    return value_;
  }

  public synchronized void setValue(int value)
  {
    if (value_ != value) {
      value_ = value;
      isModified_ = true;
    }
  }

  public void flush(Connection conn) throws SQLException
  {
    if (isModified_) {
      PreparedStatement pst = conn.prepareStatement(
          "UPDATE t_ids SET c_last_id=? WHERE c_name=?");
      pst.setInt(1, value_);
      pst.setString(2, name_);
      pst.executeUpdate();
      pst.close();

      isModified_ = false;
    }
  }

  public void flush(PreparedStatement pst) throws SQLException
  {
    if (isModified_) {
      pst.setInt(1, value_);
      pst.setString(2, name_);
      pst.executeUpdate();
      pst.close();

      isModified_ = false;
    }
  }
}
