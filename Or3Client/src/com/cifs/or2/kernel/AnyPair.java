package com.cifs.or2.kernel;

import java.io.Serializable;


/**
* com/cifs/or2/kernel/AnyPair.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from ./src/com/cifs/or2/idl/kernel.idl
* 27 Август 2009 г. 4:40:10 GMT
*/

public final class AnyPair implements Serializable
{
  public String name = null;
  public Object value = null;

  public AnyPair ()
  {
  } // ctor

  public AnyPair (String _name, Object _value)
  {
    name = _name;
    value = _value;
  } // ctor

} // class AnyPair