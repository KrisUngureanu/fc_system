package com.cifs.or2.kernel;

import java.io.Serializable;

public class KrnVcsChange implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public KrnObject cvsChangeObj = null;
	public KrnMethod cvsChangeMethod = null;
	public KrnClass cvsChangeClass = null;
	public KrnAttribute cvsChangeAttr = null;
	public KrnObject user = null;
	public KrnDate dateChange = null;
	public KrnDate dateConfirm = null;
	public long id=-1;
	public String title="";
	public String comment="";
	public long attrId=-1;
	public long langId=-1;
	public long importId=-1;
    public long exportId=-1;
    public long oldUserId = -1;
	public boolean isChecked=false;
	public boolean isTrigger=false;
	public int typeId = -1;
	public long count = 1;

	public KrnVcsChange() {
	}

	public KrnVcsChange(KrnObject _cvsChangeObj, KrnObject _user, KrnDate _dateChange,String _comment) {
		cvsChangeObj = _cvsChangeObj;
		user = _user;
		dateChange = _dateChange;
		comment=_comment;
	}
	public KrnVcsChange(KrnMethod _cvsChangeMethod,int _typyId, KrnObject _user, KrnDate _dateChange,String _comment) {
		cvsChangeMethod = _cvsChangeMethod;
		typeId = _typyId;
		user = _user;
		dateChange = _dateChange;
		comment=_comment;
	}
	public KrnVcsChange(KrnClass _cvsChangeClass, int _typyId, KrnObject _user, KrnDate _dateChange,String _comment) {
		cvsChangeClass = _cvsChangeClass;
		typeId = _typyId;
		user = _user;
		dateChange = _dateChange;
		comment=_comment;
	}
	public KrnVcsChange(long _id,KrnClass _cvsChangeClass, int _typyId, KrnObject _user, KrnDate _dateChange,String _comment) {
		id=_id;
		cvsChangeClass = _cvsChangeClass;
		typeId = _typyId;
		user = _user;
		dateChange = _dateChange;
		comment=_comment;
	}
	public KrnVcsChange(KrnAttribute _cvsChangAttr, int _typyId, KrnObject _user, KrnDate _dateChange,String _comment) {
		cvsChangeAttr = _cvsChangAttr;
		typeId = _typyId;
		user = _user;
		dateChange = _dateChange;
		comment=_comment;
	}
	public KrnVcsChange(long _id,KrnAttribute _cvsChangAttr, int _typyId, KrnObject _user, KrnDate _dateChange,String _comment) {
		id=_id;
		cvsChangeAttr = _cvsChangAttr;
		typeId = _typyId;
		user = _user;
		dateChange = _dateChange;
		comment=_comment;
	}
	public KrnVcsChange(long _id,KrnObject _cvsChangeObj, KrnObject _user, KrnDate _dateChange,String _comment) {
		id=_id;
		cvsChangeObj = _cvsChangeObj;
		user = _user;
		dateChange = _dateChange;
		comment=_comment;
	}
	public KrnVcsChange(long _id,KrnMethod _cvsChangeMethod, KrnObject _user, KrnDate _dateChange,String _comment) {
		id=_id;
		cvsChangeMethod = _cvsChangeMethod;
		user = _user;
		dateChange = _dateChange;
		comment=_comment;
	}
	public KrnVcsChange(long _id,KrnObject _cvsChangeObj, KrnObject _user, KrnDate _dateChange,String _comment,long _attrId,long _langId) {
		id=_id;
		cvsChangeObj = _cvsChangeObj;
		user = _user;
		dateChange = _dateChange;
		comment=_comment;
		attrId=_attrId;
		langId=_langId;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof KrnVcsChange) {
	    	if(this.cvsChangeObj!=null)
	    		return (this.cvsChangeObj.equals(((KrnVcsChange)obj).cvsChangeObj));
	    	else if(this.cvsChangeMethod!=null)
	    		return (this.cvsChangeMethod.equals(((KrnVcsChange)obj).cvsChangeMethod));
	    	else if(this.cvsChangeClass!=null)
	    		return (this.cvsChangeClass.equals(((KrnVcsChange)obj).cvsChangeClass));
	    	else if(this.cvsChangeAttr!=null)
	    		return (this.cvsChangeAttr.equals(((KrnVcsChange)obj).cvsChangeAttr));
		}
    	return false;
    }
	
    public String toString() {
    	StringBuilder sb = new StringBuilder(title == null ? "null" : title);
		if (cvsChangeObj != null) {
			sb.append(".| " + cvsChangeObj.uid);
		} else if (cvsChangeMethod != null) {
			sb.append(".| " + cvsChangeMethod.classId);
			sb.append(":| " + cvsChangeMethod.className);
		} else if (cvsChangeClass != null) {
			sb.append(".| " + cvsChangeClass.id);
		} else if (cvsChangeAttr != null) {
			sb.append(".| " + cvsChangeAttr.id);
		}
    	return sb.toString();
    }
}