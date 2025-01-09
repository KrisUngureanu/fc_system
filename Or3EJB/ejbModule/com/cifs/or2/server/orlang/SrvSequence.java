package com.cifs.or2.server.orlang;

import kz.tamur.lang.Sequence;
import kz.tamur.or3ee.common.UserSession;

import com.cifs.or2.server.Session;
import com.cifs.or2.server.Context;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.KrnException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 11.03.2005
 * Time: 10:24:19
 * To change this template use File | Settings | File Templates.
 */
public class SrvSequence implements Sequence {
    private Session s;
    private KrnObject obj;

    private static Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + SrvSequence.class.getName());

    public SrvSequence(KrnObject obj, Session s) {
        this.obj = obj;
        this.s = s;
    }

    public int getNextValue() {
        try {
            return s.getNextValue(obj.id, 0);
        } catch (KrnException e) {
            log.error(e, e);
        }
        return 0;
    }

    public void useValue(int value, String strValue) {
        try {
            Context ctx = s.getContext();
            s.useValue(obj.id, value, strValue, ctx.trId);
        } catch (KrnException e) {
            log.error(e, e);
        }
    }
}
