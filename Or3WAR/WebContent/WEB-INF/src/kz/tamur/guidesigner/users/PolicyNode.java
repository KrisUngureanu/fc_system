package kz.tamur.guidesigner.users;

import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kz.tamur.common.PasswordPolicy;
import kz.tamur.comps.Constants;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.util.LangItem;

import com.cifs.or2.kernel.*;
import com.cifs.or2.client.Kernel;

public class PolicyNode extends UserNode {
	private static final Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + PolicyNode.class.getName());

    private KrnClass policyCls;
    private final static String policyName = "Политика учетных записей";
    private LangItem langItem=null;
    private Kernel krn;

    private PasswordPolicy policyWrapper;

	private boolean isModified = false;

    public PolicyNode(Kernel krn) {
        super(krn);
        krnObj = null;
        isLoaded = false;
        title = policyName;
        this.krn=krn;
        getPolicyClass();
        if (krnObj != null)
            load();
    }

    public KrnClass getPolicyClass() {
        if (policyCls == null) {
            try {
                policyCls = krn.getClassByName(policyName);
                if (policyCls != null) {
                    KrnObject[] objs = krn.getClassObjects(
                            policyCls, 0);
                    krnObj = (objs != null && objs.length > 0) ? objs[0]
                            : krn.createObject(policyCls, 0);
                }
            } catch (Exception e) {
                log.warn("Не найден класс \"" + policyName + "\"");
            }
        }
        return policyCls;
    }

    /**
	 * 
	 */
    public void reload(Kernel krn) {
    	this.krn = krn;
        isLoaded = false;
        load();
    }

    protected void load() {
        if (!isLoaded) {
            isLoaded = true;
            long maxValidPeriod;
            long minPDLength = 6;
            long minLoginLength = 3;
            long minPDLengthAdmin;
            long numPassDubl;
            long numPassDublAdmin;
            boolean useNumbers;
            boolean useNotAllNumbers;
            boolean useSymbols;
            boolean useRegisterSymbols;
            boolean useSpecialSymbol;
            boolean banNames;
            boolean banFamilies;
            boolean banPhone;
            boolean banWord;
            long maxPeriodPD = 90;
            long minPeriodPD;
            long numberFailedLogin;
            long timeLock;
            boolean banLoginInPD;
            long maxLengthPass;
            long maxLengthLogin;
            boolean changeFirstPass;
            long maxPeriodFirstPass;
            boolean banRepeatChar;
            boolean banRepAnyWhereMoreTwoChar;
            boolean banKeyboard;
            boolean activateLiabilitySign;
            long liabilitySignPeriod;
            boolean activateECPExpiryNotif;
            long ecpExpiryNotifPeriod;
            boolean activateTempRegNotif;
            long tempRegNotifPeriod;
            boolean checkClientIp;
            boolean useECP = false;
            boolean banUseOwnIdentificationData = false;

            try {
                long[] oids = { krnObj.id };
                LongValue[] lvs = new com.cifs.or2.kernel.LongValue[0];

                // атрибуты присутствующие и в старой политике, должны быть
                // первыми
                lvs = krn.getLongValues(oids, krnObj.classId, Constants.ATTR_MAX_PERIOD_PASSWORD, 0);
                // 90 суток
                maxPeriodPD = (lvs != null && lvs.length > 0) ? lvs[0].value : 90;

                lvs = krn.getLongValues(oids, krnObj.classId, Constants.ATTR_MIN_LOGIN_LENGTH, 0);
                minLoginLength = (lvs != null && lvs.length > 0) ? lvs[0].value : 3;

                lvs = krn.getLongValues(oids, krnObj.classId, Constants.ATTR_MIN_PASSWORD_LENGTH, 0);
                minPDLength = (lvs != null && lvs.length > 0) ? lvs[0].value : 8;

                // новые атрибуты
                lvs = krn.getLongValues(oids, krnObj.classId, Constants.ATTR_MAX_VALID_PERIOD, 0);
                maxValidPeriod = (lvs != null && lvs.length > 0) ? lvs[0].value : 30;

                lvs = krn.getLongValues(oids, krnObj.classId, Constants.ATTR_MIN_PASSWORD_LENGTH_ADMIN, 0);
                minPDLengthAdmin = (lvs != null && lvs.length > 0) ? ((int) lvs[0].value) : 12;

                lvs = krn.getLongValues(oids, krnObj.classId, Constants.ATTR_NUMBER_PASSWORD_DUBLICATE, 0);
                numPassDubl = (lvs != null && lvs.length > 0) ? ((int) lvs[0].value) : 3;

                lvs = krn.getLongValues(oids, krnObj.classId, Constants.ATTR_NUMBER_PASSWORD_DUBLICATE_ADMIN, 0);
                numPassDublAdmin = (lvs != null && lvs.length > 0) ? ((int) lvs[0].value) : 20;

                lvs = krn.getLongValues(oids, krnObj.classId, Constants.ATTR_USE_NUMBERS, 0);
                useNumbers = (lvs != null && lvs.length > 0) ? toBoolean(lvs[0].value) : true;

                lvs = krn.getLongValues(oids, krnObj.classId, Constants.ATTR_USE_SYMBOLS, 0);
                useSymbols = (lvs != null && lvs.length > 0) ? toBoolean(lvs[0].value) : true;

                lvs = krn.getLongValues(oids, krnObj.classId, Constants.ATTR_USE_REGISTER_SYMBOLS, 0);
                useRegisterSymbols = (lvs != null && lvs.length > 0) ? toBoolean(lvs[0].value) : false;

                lvs = krn.getLongValues(oids, krnObj.classId, Constants.ATTR_USE_SPECIAL_SYMBOL, 0);
                useSpecialSymbol = (lvs != null && lvs.length > 0) ? toBoolean(lvs[0].value) : false;

                lvs = krn.getLongValues(oids, krnObj.classId, Constants.ATTR_BAN_NAMES, 0);
                banNames = (lvs != null && lvs.length > 0) ? toBoolean(lvs[0].value) : true;

                lvs = krn.getLongValues(oids, krnObj.classId, Constants.ATTR_BAN_FAMILIES, 0);
                banFamilies = (lvs != null && lvs.length > 0) ? toBoolean(lvs[0].value) : true;

                lvs = krn.getLongValues(oids, krnObj.classId, Constants.ATTR_BAN_PHONE, 0);
                banPhone = (lvs != null && lvs.length > 0) ? toBoolean(lvs[0].value) : true;

                lvs = krn.getLongValues(oids, krnObj.classId, Constants.ATTR_BAN_WORD, 0);
                banWord = (lvs != null && lvs.length > 0) ? toBoolean(lvs[0].value) : true;

                lvs = krn.getLongValues(oids, krnObj.classId, Constants.ATTR_MIN_PERIOD_PASSWORD, 0);
                // 2 дня
                minPeriodPD = (lvs != null && lvs.length > 0) ? lvs[0].value : 2;

                lvs = krn.getLongValues(oids, krnObj.classId, Constants.ATTR_NUMBER_FAILED_LOGIN, 0);
                // 10 попыток
                numberFailedLogin = (lvs != null && lvs.length > 0) ? ((int) lvs[0].value) : 10;

                lvs = krn.getLongValues(oids, krnObj.classId, Constants.ATTR_TIME_LOCK, 0);
                // 30 минут
                timeLock = (lvs != null && lvs.length > 0) ? lvs[0].value : 30;

                lvs = krn.getLongValues(oids, krnObj.classId, Constants.ATTR_BAN_LOGIN_IN_PASSWORD, 0);
                banLoginInPD = (lvs != null && lvs.length > 0) ? toBoolean(lvs[0].value) : false;
                
                lvs = krn.getLongValues(oids, krnObj.classId, Constants.ATTR_ACTIVATE_LIABILITY_SIGN, 0);
                activateLiabilitySign = (lvs != null && lvs.length > 0) ? toBoolean(lvs[0].value) : false;
                
                lvs = krn.getLongValues(oids, krnObj.classId, Constants.ATTR_LIABILITY_SIGN_PERIOD, 0);
                liabilitySignPeriod = (lvs != null && lvs.length > 0) ? lvs[0].value : 365;

                lvs = krn.getLongValues(oids, krnObj.classId, Constants.ATTR_ACTIVATE_ECP_EXPIRY_NOTIF, 0);
                activateECPExpiryNotif = (lvs != null && lvs.length > 0) ? toBoolean(lvs[0].value) : false;
                
                lvs = krn.getLongValues(oids, krnObj.classId, Constants.ATTR_ECP_EXPIRY_NOTIF_PERIOD, 0);
                ecpExpiryNotifPeriod = (lvs != null && lvs.length > 0) ? lvs[0].value : 30;

                lvs = krn.getLongValues(oids, krnObj.classId, Constants.ATTR_ACTIVATE_TEMP_REG_NOTIF, 0);
                activateTempRegNotif = (lvs != null && lvs.length > 0) ? toBoolean(lvs[0].value) : false;
                
                lvs = krn.getLongValues(oids, krnObj.classId, Constants.ATTR_TEMP_REG_NOTIF_PERIOD, 0);
                tempRegNotifPeriod = (lvs != null && lvs.length > 0) ? lvs[0].value : 0;

                lvs = krn.getLongValues(oids, krnObj.classId, Constants.ATTR_CHECK_CLIENT_IP, 0);
                checkClientIp = (lvs != null && lvs.length > 0) ? toBoolean(lvs[0].value) : false;
                
                if (krn.isRNDB() || krn.hasUseECP()) {
                	lvs = krn.getLongValues(oids, krnObj.classId, Constants.ATTR_USE_ECP, 0);
                	useECP = (lvs != null && lvs.length > 0) ? toBoolean(lvs[0].value) : false;
                }
                
                if (!krn.isULDB() && !krn.isRNDB()) {
                	lvs = krn.getLongValues(oids, krnObj.classId, Constants.ATTR_BAN_USE_OWN_IDENTIFICATION_DATA, 0);
                	banUseOwnIdentificationData = (lvs != null && lvs.length > 0) ? toBoolean(lvs[0].value) : false;
                }

                try {
	                lvs = krn.getLongValues(oids, krnObj.classId, Constants.ATTR_MAX_LENGTH_PASS, 0);
	                maxLengthPass = (lvs != null && lvs.length > 0) ? lvs[0].value : 30;
	                
	                lvs = krn.getLongValues(oids, krnObj.classId, Constants.ATTR_MAX_LENGTH_LOGIN, 0);
	                maxLengthLogin = (lvs != null && lvs.length > 0) ? lvs[0].value : 50;
	                
	                lvs = krn.getLongValues(oids, krnObj.classId, Constants.ATTR_CHANGE_FIRST_PASS, 0);
	                changeFirstPass = (lvs != null && lvs.length > 0) ? toBoolean(lvs[0].value) : true;
	                
	                lvs = krn.getLongValues(oids, krnObj.classId, Constants.ATTR_MAX_PERIOD_FIRST_PASS, 0);
	                maxPeriodFirstPass = (lvs != null && lvs.length > 0) ? lvs[0].value : 5;
	             
	                lvs = krn.getLongValues(oids, krnObj.classId, Constants.ATTR_BAN_REPEAT_CHAR, 0);
	                banRepeatChar = (lvs != null && lvs.length > 0) ? toBoolean(lvs[0].value) : true;
	                
	                lvs = krn.getLongValues(oids, krnObj.classId, Constants.ATTR_USE_NOTALLNUMBERS, 0);
	                useNotAllNumbers = (lvs != null && lvs.length > 0) ? toBoolean(lvs[0].value) : false;
	                
	                lvs = krn.getLongValues(oids, krnObj.classId, Constants.ATTR_BAN_REPEAT_ANYWHERE_MORE_2_NOREGISTER_CHAR, 0);
	                banRepAnyWhereMoreTwoChar = (lvs != null && lvs.length > 0) ? toBoolean(lvs[0].value) : false;
	                
	                lvs = krn.getLongValues(oids, krnObj.classId, Constants.ATTR_BAN_KEYBOARD, 0);
	                banKeyboard = (lvs != null && lvs.length > 0) ? toBoolean(lvs[0].value) : false;
                } catch (Exception e) {
                    maxLengthPass = 30;
                    maxLengthLogin = 50;
                    changeFirstPass = true;
                    maxPeriodFirstPass = 5;
                    banRepeatChar = true;
                    useNotAllNumbers = false;
                    banRepAnyWhereMoreTwoChar = false;
                    banKeyboard = false;
                }
            } catch (Exception e) {
                maxValidPeriod = 30;
                minPDLengthAdmin = 12;
                numPassDubl = 3;
                numPassDublAdmin = 20;
                useNumbers = true;
                useNotAllNumbers = false;
                useSymbols = true;
                useRegisterSymbols = false;
                useSpecialSymbol = false;
                banNames = true;
                banFamilies = true;
                banPhone = true;
                banWord = true;
                minPeriodPD = 2;
                numberFailedLogin = 10;
                timeLock = 30;
                banLoginInPD = true;
                maxLengthPass = 30;
                maxLengthLogin = 50;
                changeFirstPass = true;
                maxPeriodFirstPass = 5;
                banRepeatChar = true;
                banRepAnyWhereMoreTwoChar = false;
                banKeyboard = false;
                activateLiabilitySign = false;
                liabilitySignPeriod = 365;
                activateECPExpiryNotif = false;
                ecpExpiryNotifPeriod = 30;
                activateTempRegNotif = false;
                tempRegNotifPeriod = 0;
                checkClientIp = false;
            }
            
            policyWrapper = new PasswordPolicy(maxValidPeriod, minPDLength, minLoginLength, minPDLengthAdmin, 
            		numPassDubl, numPassDublAdmin, useNumbers, useNotAllNumbers, useSymbols, useRegisterSymbols, useSpecialSymbol, 
            		banNames, banFamilies, banPhone, banWord, maxPeriodPD, minPeriodPD, numberFailedLogin, timeLock,
            		banLoginInPD, maxLengthPass, maxLengthLogin, changeFirstPass, maxPeriodFirstPass, 
            		banRepeatChar, banRepAnyWhereMoreTwoChar, banKeyboard, activateLiabilitySign, liabilitySignPeriod,
            		activateECPExpiryNotif, ecpExpiryNotifPeriod, activateTempRegNotif, tempRegNotifPeriod, checkClientIp, useECP, banUseOwnIdentificationData);
        }
    }

    public boolean isLeaf() {
        return true;
    }

    // зачем оно тут?
    public void rename(String newName) {
    }

    /**
     * временный метод переводит объект в эквивалент булева типа
     * 
     * @param объект
     *            который значение которого будет переведено в тип boolean
     * @return
     */
    public static Boolean toBoolean(Object o) {
        return (o instanceof Boolean) ? (Boolean) o : o.toString().equals("1")
                || o.toString().toUpperCase(Locale.ROOT).equals("TRUE");
    }

    public boolean isModified() {
        return isModified;
    }

    public void setModified(boolean modified) {
        isModified = modified;
    }

	public PasswordPolicy getPolicyWrapper() {
		return policyWrapper;
	}
}