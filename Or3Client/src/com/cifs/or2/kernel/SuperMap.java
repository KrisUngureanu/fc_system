package com.cifs.or2.kernel;


/**
* com/cifs/or2/kernel/SuperMap.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from ./src/com/cifs/or2/idl/kernel.idl
* 27 Август 2009 г. 4:40:10 GMT
*/

public final class SuperMap implements java.io.Serializable
{
  public long flowId = (long)0;
  public long processDefId = (long)0;
  public long subflowId = (long)0;
  public long nodes[][] = null;

  public SuperMap ()
  {
  } // ctor

  public SuperMap (long _flowId, long _processDefId, long _subflowId, long[][] _nodes)
  {
    flowId = _flowId;
    processDefId = _processDefId;
    subflowId = _subflowId;
    nodes = _nodes;
  } // ctor

} // class SuperMap
