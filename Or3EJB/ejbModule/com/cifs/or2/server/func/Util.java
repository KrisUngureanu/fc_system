package com.cifs.or2.server.func;

import com.cifs.or2.kernel.*;
import com.cifs.or2.server.Session;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

import kz.tamur.util.Funcs;
import static kz.tamur.or3ee.common.SessionIds.*;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 24.11.2004
 * Time: 18:51:40
 * To change this template use File | Settings | File Templates.
 */
public class Util {

    public static void setObjectAttr(KrnObject obj, KrnAttribute attr, int i,
            long langId, long trId, Object v, Session s)
    		throws KrnException {
	setObjectAttr(obj, attr, i, langId, trId, v, null, s);
    }
    
    public static void setObjectAttr(KrnObject obj, KrnAttribute attr, int i,
                                     long langId, long trId, Object v,
                                     String charSet, Session s)
            throws KrnException {
        switch ((int)attr.typeClassId) {
            case CID_STRING :
            case CID_MEMO :
                {
                    boolean isMemo = attr.typeClassId == CID_MEMO;
                    long lid = attr.isMultilingual ? langId : 0;
                    s.setValue(obj, attr.id, i, lid, trId, v, false);
                    break;
                }
            case CID_INTEGER :
            case CID_BOOL :
                {
                    long value = 0;
                    if (v instanceof Number) {
                    	value = ((Number)v).longValue();
                    } else if (v instanceof Boolean) {
                    	value = ((Boolean)v) ? 1 : 0;
                    }
                    s.setValue(obj, attr.id, i, 0, trId, v, false);
                    break;
                }
            case CID_DATE :
                    s.setValue(obj, attr.id, i, 0, trId, Funcs.convertToSqlDate((Date)v), false);
                    break;
            case CID_TIME :
            		s.setValue(obj, attr.id, i, 0, trId, Funcs.convertToSqlTime((Date)v), false);
                    break;
            case CID_FLOAT :
                {
                    double value = (v instanceof Number)
                            ? ((Number)v).doubleValue() : 0;
            		s.setValue(obj, attr.id, i, 0, trId, value, false);
                    break;
                }
            case CID_BLOB :
                {
                	byte[] value = new byte[0];
                	if (v instanceof String) {
                		try {
                			value = Funcs.read((String)v);
                		} catch (IOException e) {
                			e.printStackTrace();
                			throw new KrnException(0, e.getMessage());
                		}
					} else if (v instanceof byte[]) {
						value = (byte[])v;
					}
                    s.setBlob(obj.id, attr.id, i, value, charSet, langId, trId);
                    break;
                }
            default :
                {
            		s.setValue(obj, attr.id, i, 0, trId, v, false);
                }
        }
    }
}
