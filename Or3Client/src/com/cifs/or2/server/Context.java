package com.cifs.or2.server;

import java.io.Serializable;

import kz.tamur.lang.parser.ASTStart;

public class Context implements Serializable
{
  public long objectId;
  public long trId;
  public long langId;
  public long flowId;
  public long pdId;
  public long uiId;
  public long[] objIds;
  public boolean isOpenTranpaction = false;
  public boolean isEvaluated = false;
  public boolean isNotWriteSysLog = false;
  public boolean isLogCommitLongTr = false;
  public ASTStart beforeCommitExpr;
  public ASTStart afterCommitExpr;

  public Context(long[] objIds, long trId, long langId)
  {
    this.objIds = objIds;
    this.trId = trId;
    this.langId = langId;
  }
  @Override
  public String toString(){
	  String res="objectId:"+objectId+"; uiId:"+uiId+"; trId:"+trId+"; langId:"+langId+"; flowId:"+flowId+"; pdId:"+pdId;
	  if(objIds.length>0){
		  res+="; objIds:"+objIds[0];
		  for(int i=1;i<objIds.length;i++){
			  res+=","+objIds[i];
		  }
	  }
	  return res;
	  
  }
}

