package com.cifs.or2.kernel;


/**
* com/cifs/or2/kernel/CursorOperations.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from ./src/com/cifs/or2/idl/kernel.idl
* 27 Август 2009 г. 4:40:10 GMT
*/


// Interfaces
public interface CursorOperations 
{
  int getSize ();
  com.cifs.or2.kernel.KrnObject[] get (int offs, int count) throws com.cifs.or2.kernel.KrnException;
  void release ();
} // interface CursorOperations
