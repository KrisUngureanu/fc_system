package com.cifs.or2.kernel;


/**
* com/cifs/or2/kernel/DateValue.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from ./src/com/cifs/or2/idl/kernel.idl
* 27 Август 2009 г. 4:40:10 GMT
*/

public final class DateValue implements java.io.Serializable
{
  public long objectId = (long)0;
  public int index = (int)0;
  public com.cifs.or2.kernel.Date value = null;

  public DateValue ()
  {
  } // ctor

  public DateValue (long _objectId, int _index, com.cifs.or2.kernel.Date _value)
  {
    objectId = _objectId;
    index = _index;
    value = _value;
  } // ctor

} // class DateValue
