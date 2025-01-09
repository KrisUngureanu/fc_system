package com.cifs.or2.server.orlang;

import kz.tamur.lang.MethodWrp;
import kz.tamur.or3ee.common.UserSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.cifs.or2.server.Session;
import com.cifs.or2.kernel.KrnMethod;
import com.cifs.or2.kernel.KrnException;

/**
 * Created by IntelliJ IDEA.
 * User: Valeri
 * Date: 17.12.2006
 * Time: 15:12:46
 * To change this template use File | Settings | File Templates.
 */
public class SrvMethodWrp extends MethodWrp {
        private static Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + SrvMethodWrp.class.getName());
        private Session session;

        public SrvMethodWrp(KrnMethod mtd, Session session) {
            super(mtd);
            this.session = session;
        }
        public byte[] getMethodExpression(){
            try {
                return session.getMethodExpression(mtd.uid);
            } catch (KrnException e) {
            	log.error(e, e);
            }
            return null;
        }
}
