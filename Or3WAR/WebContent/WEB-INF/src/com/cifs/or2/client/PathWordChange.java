package com.cifs.or2.client;

import static kz.tamur.common.ErrorCodes.*;

import java.util.ResourceBundle;

import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.util.PasswordService;

import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.UserSessionValue;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 27.12.2004
 * Time: 11:25:03
 * To change this template use File | Settings | File Templates.
 */
public class PathWordChange {

    public static Message changePassword(char[] newPd, char[] cnfPd, char[] oldPd, Kernel krn, ResourceBundle res)
            throws KrnException {
        String mess = "";
        try {
            String oldPde = PasswordService.getInstance().encrypt(new String(oldPd));
            UserSessionValue us = krn.getUserSession();
            krn.changePassword(us.dsName, us.name, us.typeClient, us.ip, us.pcName, krn.getUser().getObject(), oldPde.toCharArray(), newPd, cnfPd);
            // подтверждение смены пароля
            mess = res.getString("completeMessage");
            return new Message(mess, MessagesFactory.INFORMATION_MESSAGE, "completeMessage");
        } catch (KrnException e1) {
            switch (e1.code) {
            case PASS_NOT_COMPLETE:
                mess = res.getString("notCompleteMessage");
                break;
            case PASS_PASS_NOT_EQUALS:
                mess = res.getString("passNotEqualsMessage");
                break;
            case PASS_PASS_IDENT:
                mess = res.getString("messPassIdent");
                break;
            case PASS_OLD_PASS_INVALID:
                mess = res.getString("oldPassInvalidMessage");
                break;
            case PASS_MIN_PERIOD_PASS:
                mess = res.getString("messMinPeriodPass");
                mess = mess.replaceFirst("X", e1.getMessage());
                break;
            case PASS_VALID_PWD_MIN_LOGIN:
                mess = res.getString("validPwdMinLogin");
                mess = mess.replaceFirst("X", e1.getMessage());
                break;
            case PASS_VALID_PWD_MAX_LOGIN:
                mess = res.getString("validPwdMaxLogin");
                mess = mess.replaceFirst("X", e1.getMessage());
                break;
            case PASS_VALID_PWD_MIN_PASS:
                mess = res.getString("validPwdmMinPass");
                mess = mess.replaceFirst("X", e1.getMessage());
                break;
            case PASS_VALID_PWD_MIN_PASS_ADM:
                mess = res.getString("validPwdMinPassAdm");
                mess = mess.replaceFirst("X", e1.getMessage());
                break;
            case PASS_VALID_PWD_MAX_PASS:
                mess = res.getString("validPwdmMaxPass");
                mess = mess.replaceFirst("X", e1.getMessage());
                break;
            case PASS_VALID_PWD_NO_NUMB:
                mess = res.getString("validPwdNoNumb");
                break;
            case PASS_VALID_PWD_NO_ALL_NUMB:
                mess = res.getString("validPwdNoAllNumb");
                break;
            case PASS_VALID_PWD_NO_SYMB:
                mess = res.getString("validPwdNoSymb");
                break;
            case PASS_VALID_PWD_NO_REG:
                mess = res.getString("validPwdNoReg");
                break;
            case PASS_VALID_PWD_NO_SPEC:
                mess = res.getString("validPwdNoSpec");
                break;
            case PASS_VALID_PWD_NOT_NAME:
                mess = res.getString("validPwdNotName");
                break;
            case PASS_VALID_PWD_NOT_SURN:
                mess = res.getString("validPwdNotSurn");
                break;
            case PASS_VALID_PWD_NOT_TEL:
                mess = res.getString("validPwdNotTel");
                break;
            case PASS_VALID_PWD_NOT_WORD:
                mess = res.getString("validPwdNotWord");
                break;
            case PASS_VALID_PWD_NOT_KEYBOARD:
                mess = res.getString("validPwdNotKeyboard");
                break;
            case PASS_VALID_PWD_NOT_LOGIN:
                mess = res.getString("validPwdNotLogin");
                break;
            case PASS_VALID_PWD_NOT_REP:
                mess = res.getString("validPwdNotRep");
                break;
            case PASS_VALID_PWD_NOT_REP_ANY_MORE_TWO:
                mess = res.getString("validPwdNotRepAnyMoreTwo");
                break;
            case PASS_MESS_PASS_DUPL:
                mess = res.getString("messPassDupl");
                mess = mess.replaceFirst("X", e1.getMessage());
                break;
            case PASS_VALID_PWD_NOT_EASY_SYMBOLS:
            	mess = res.getString("validPwdNotIdentificationData");
                break;
            default:
                mess = e1.getMessage();
            }
            return new Message(mess, MessagesFactory.ERROR_MESSAGE);
        }
    }

    public static class Message {
        private String message;
        private int type;
        private String code;

        public Message(String message, int type) {
            this.message = message;
            this.type = type;
        }

        public Message(String message, int type, String code) {
            this.message = message;
            this.type = type;
            this.code = code;
        }

        public int getType() {
            return type;
        }

        public String getMessage() {
            return message;
        }

        public String getCode() {
            return code;
        }
    }
}