package kz.tamur.ekyzmet.test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import kz.tamur.or3ee.common.UserSession;
import kz.tamur.or3ee.server.kit.SrvUtils;
import kz.tamur.util.Base64;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.server.Session;

public class JspUtil {

	public static void savePicture(HttpServletRequest req) throws Exception {
		HttpSession hs = req.getSession();
		UserSession us = (UserSession) hs.getAttribute("userSession");
		Session ors = null;
		try {
			ors = SrvUtils.getSession(us);
			KrnObject stepObj = (KrnObject)hs.getAttribute("step");
			KrnClass stepCls = ors.getClassByName("ек::тест::История заявки");
			KrnAttribute picAttr = ors.getAttributeByName(stepCls, "фото");
			ors.setBlob(stepObj.id, picAttr.id, 0, dataUrlToBytes(req.getParameter("picture")), 0, 0);
			ors.commitTransaction();
		} finally {
			if (ors != null)
				ors.release();
		}
		
	}
	
	public static byte[] dataUrlToBytes(String dataUrl) {
		String encodingPrefix = "base64,";
		int contentStartIndex = dataUrl.indexOf(encodingPrefix) + encodingPrefix.length();
		return Base64.decode(dataUrl.substring(contentStartIndex));
	}
}
