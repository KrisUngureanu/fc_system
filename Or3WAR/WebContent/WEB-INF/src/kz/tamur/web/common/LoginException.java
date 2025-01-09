package kz.tamur.web.common;

import static kz.tamur.common.ErrorCodes.PASS_MESS_PASS_DUPL;
import static kz.tamur.common.ErrorCodes.PASS_MIN_PERIOD_PASS;
import static kz.tamur.common.ErrorCodes.PASS_NOT_COMPLETE;
import static kz.tamur.common.ErrorCodes.PASS_OLD_PASS_INVALID;
import static kz.tamur.common.ErrorCodes.PASS_PASS_IDENT;
import static kz.tamur.common.ErrorCodes.PASS_PASS_NOT_EQUALS;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_MAX_LOGIN;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_MAX_PASS;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_MIN_LOGIN;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_MIN_PASS;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_MIN_PASS_ADM;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_NOT_KEYBOARD;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_NOT_LOGIN;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_NOT_NAME;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_NOT_REP;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_NOT_REP_ANY_MORE_TWO;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_NOT_SURN;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_NOT_TEL;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_NOT_WORD;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_NO_NUMB;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_NO_REG;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_NO_SPEC;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_NO_SYMB;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_NO_ALL_NUMB;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.eclipsesource.json.JsonObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import kz.tamur.common.ErrorCodes;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.web.controller.WebController;

/**
 * Created by IntelliJ IDEA.
 * User: erik
 * Date: Nov 12, 2003
 * Time: 5:35:32 PM
 * To change this template use Options | File Templates.
 */

public class LoginException extends Exception {
    private static final Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + LoginException.class);

    private int code = 0;
    private String msg;
    private int configNumber;

    public LoginException(String s) {
        this(0, s, -1);
    }
    
    public LoginException(int code, String s) {
        super(s);
        this.code = code;
        ResourceBundle res = CommonHelper.RESOURCE_RU;
        switch (code) {
            case PASS_NOT_COMPLETE:
            	msg = res.getString("notCompleteMessage");
                break;
            case PASS_PASS_NOT_EQUALS:
                msg = res.getString("passNotEqualsMessage");
                break;
            case PASS_PASS_IDENT:
                msg = res.getString("messPassIdent");
                break;
            case PASS_OLD_PASS_INVALID:
                msg = res.getString("oldPassInvalidMessage");
                break;
            case PASS_MIN_PERIOD_PASS:
                msg = res.getString("messMinPeriodPass");
                msg = msg.replaceFirst("X", s);
                break;
            case PASS_VALID_PWD_MIN_LOGIN:
                msg = res.getString("validPwdMinLogin");
                msg = msg.replaceFirst("X", s);
                break;
            case PASS_VALID_PWD_MAX_LOGIN:
                msg = res.getString("validPwdMaxLogin");
                msg = msg.replaceFirst("X", s);
                break;
            case PASS_VALID_PWD_MIN_PASS:
                msg = res.getString("validPwdmMinPass");
                msg = msg.replaceFirst("X", s);
                break;
            case PASS_VALID_PWD_MIN_PASS_ADM:
                msg = res.getString("validPwdMinPassAdm");
                msg = msg.replaceFirst("X", s);
                break;
            case PASS_VALID_PWD_MAX_PASS:
                msg = res.getString("validPwdmMaxPass");
                msg = msg.replaceFirst("X", s);
                break;
            case PASS_VALID_PWD_NO_NUMB:
                msg = res.getString("validPwdNoNumb");
                break;
            case PASS_VALID_PWD_NO_ALL_NUMB:
                msg = res.getString("validPwdNoAllNumb");
                break;
            case PASS_VALID_PWD_NO_SYMB:
                msg = res.getString("validPwdNoSymb");
                break;
            case PASS_VALID_PWD_NO_REG:
                msg = res.getString("validPwdNoReg");
                break;
            case PASS_VALID_PWD_NO_SPEC:
                msg = res.getString("validPwdNoSpec");
                break;
            case PASS_VALID_PWD_NOT_NAME:
                msg = res.getString("validPwdNotName");
                break;
            case PASS_VALID_PWD_NOT_SURN:
                msg = res.getString("validPwdNotSurn");
                break;
            case PASS_VALID_PWD_NOT_TEL:
                msg = res.getString("validPwdNotTel");
                break;
            case PASS_VALID_PWD_NOT_WORD:
                msg = res.getString("validPwdNotWord");
                break;
            case PASS_VALID_PWD_NOT_KEYBOARD:
                msg = res.getString("validPwdNotKeyboard");
                break;
            case PASS_VALID_PWD_NOT_LOGIN:
                msg = res.getString("validPwdNotLogin");
                break;
            case PASS_VALID_PWD_NOT_REP:
                msg = res.getString("validPwdNotRep");
                break;
            case PASS_VALID_PWD_NOT_REP_ANY_MORE_TWO:
                msg = res.getString("validPwdNotRepAnyMoreTwo");
                break;
            case PASS_MESS_PASS_DUPL:
                msg = res.getString("messPassDupl");
                msg = msg.replaceFirst("X", s);
                break;
            case ErrorCodes.USER_HAS_CONNECT_SAME_IP:
                msg = res.getString("userHasConnectedSameIP");
                break;
            case ErrorCodes.PASS_VALID_PWD_NOT_EASY_SYMBOLS:
                msg = res.getString("validPwdNotIdentificationData");
                break;
            default:
            	try {
            		String str = res.getString(s);
            		msg = str != null ? str : s;
            	} catch (MissingResourceException e) {
            		msg = s;
            	}
            	
        }
        this.configNumber = 0;
    }

    public LoginException(int code, String s, int configNumber) {
        super(s);
        this.code = code;
        msg = s;
        this.configNumber = configNumber;
    }

    public void makeResponse(Map<String, String> params, HttpServletResponse response) {
        try {
            if (params.get("xml") != null) {
                response.setContentType("text/xml; charset=UTF-8");
                response.setHeader("Cache-Control", "no-cache");
                PrintWriter w = response.getWriter();
                w.println(ViewHelper.getAlertXml(msg));
            } else { // if (params.get("json") != null) {
                response.setContentType("application/json; charset=UTF-8");
                response.setHeader("Cache-Control", "no-cache");
                PrintWriter w = response.getWriter();
                JsonObject obj = ViewHelper.getAlertJSON(msg);
                if (code == ErrorCodes.USER_IS_EXPIRED) {
                    obj.add("passChange", "2");
                } else if (code == ErrorCodes.USER_IS_ENDED || code == ErrorCodes.USER_NOT_LOGIN) {
                    obj.add("passChange", "1");
                } else if (code == ErrorCodes.USER_HAS_CONNECT_SAME_IP) {
                    obj.add("reconnect", "1");
                }
                w.println(obj.toString());
            }
        } catch (IOException ex) {
			log.error(ex.getMessage(), ex);
        }
    }
}
