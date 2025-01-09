package kz.tamur.guidesigner.users;

import static kz.tamur.comps.Constants.ATTR_ACTIVATE_ECP_EXPIRY_NOTIF;
import static kz.tamur.comps.Constants.ATTR_ACTIVATE_LIABILITY_SIGN;
import static kz.tamur.comps.Constants.ATTR_ACTIVATE_TEMP_REG_NOTIF;
import static kz.tamur.comps.Constants.ATTR_BAN_FAMILIES;
import static kz.tamur.comps.Constants.ATTR_BAN_KEYBOARD;
import static kz.tamur.comps.Constants.ATTR_BAN_LOGIN_IN_PASSWORD;
import static kz.tamur.comps.Constants.ATTR_BAN_NAMES;
import static kz.tamur.comps.Constants.ATTR_BAN_PHONE;
import static kz.tamur.comps.Constants.ATTR_BAN_REPEAT_ANYWHERE_MORE_2_NOREGISTER_CHAR;
import static kz.tamur.comps.Constants.ATTR_BAN_REPEAT_CHAR;
import static kz.tamur.comps.Constants.ATTR_BAN_WORD;
import static kz.tamur.comps.Constants.ATTR_CHANGE_FIRST_PASS;
import static kz.tamur.comps.Constants.ATTR_CHECK_CLIENT_IP;
import static kz.tamur.comps.Constants.ATTR_ECP_EXPIRY_NOTIF_PERIOD;
import static kz.tamur.comps.Constants.ATTR_LIABILITY_SIGN_PERIOD;
import static kz.tamur.comps.Constants.ATTR_MAX_LENGTH_LOGIN;
import static kz.tamur.comps.Constants.ATTR_MAX_LENGTH_PASS;
import static kz.tamur.comps.Constants.ATTR_MAX_PERIOD_FIRST_PASS;
import static kz.tamur.comps.Constants.ATTR_MAX_PERIOD_PASSWORD;
import static kz.tamur.comps.Constants.ATTR_MAX_VALID_PERIOD;
import static kz.tamur.comps.Constants.ATTR_MIN_LOGIN_LENGTH;
import static kz.tamur.comps.Constants.ATTR_MIN_PASSWORD_LENGTH;
import static kz.tamur.comps.Constants.ATTR_MIN_PASSWORD_LENGTH_ADMIN;
import static kz.tamur.comps.Constants.ATTR_MIN_PERIOD_PASSWORD;
import static kz.tamur.comps.Constants.ATTR_NUMBER_FAILED_LOGIN;
import static kz.tamur.comps.Constants.ATTR_NUMBER_PASSWORD_DUBLICATE;
import static kz.tamur.comps.Constants.ATTR_NUMBER_PASSWORD_DUBLICATE_ADMIN;
import static kz.tamur.comps.Constants.ATTR_TEMP_REG_NOTIF_PERIOD;
import static kz.tamur.comps.Constants.ATTR_TIME_LOCK;
import static kz.tamur.comps.Constants.ATTR_USE_NOTALLNUMBERS;
import static kz.tamur.comps.Constants.ATTR_USE_NUMBERS;
import static kz.tamur.comps.Constants.ATTR_USE_REGISTER_SYMBOLS;
import static kz.tamur.comps.Constants.ATTR_USE_SPECIAL_SYMBOL;
import static kz.tamur.comps.Constants.ATTR_USE_SYMBOLS;
import static kz.tamur.comps.Constants.ATTR_USE_ECP;
import static kz.tamur.comps.Constants.ATTR_BAN_USE_OWN_IDENTIFICATION_DATA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import kz.tamur.common.PasswordPolicy;

import com.cifs.or2.client.ClassNode;
import com.cifs.or2.client.util.AttrRequestBuilder;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnObject;

public class PolicyNode extends UserNode {

    private KrnClass policyCls;
    private final static String policyName = "Политика учетных записей";
    private PasswordPolicy policyWrapper;

	private boolean isModified = false;
	
	private Map<String, Object[]> changes = new HashMap<String, Object[]>();

	public PolicyNode() {
        super();
        krnObj = null;
        isLoaded = false;
        title = policyName;
        getPolicyClass();
        if (krnObj != null)
            load();
    }

    public KrnClass getPolicyClass() {
        if (policyCls == null) {
            try {
                policyCls = krn.getClassByName(policyName);
                if (policyCls != null) {
                    KrnObject[] objs = krn.getClassObjects(policyCls, 0);
                    krnObj = (objs != null && objs.length > 0) ? objs[0] : krn.createObject(policyCls, 0);
                }
            } catch (Exception e) {
                System.out.println("Не найден класс \"" + policyName + "\"");
            }
        }
        return policyCls;
    }

    public boolean isLeaf() {
        return true;
    }

    
    public void rename(String newName) {
    }

    protected void load() {
        if (!isLoaded) {
            isLoaded = true;
            long maxValidPeriod;
            long minPasswordLength = 6;
            long minLoginLength = 3;
            long minPasswordLengthAdmin;
            long numPassDubl;
            long numPassDublAdmin;
            boolean useNumbers;
            boolean useNotAllNumbers = false;
            boolean useSymbols;
            boolean useRegisterSymbols;
            boolean useSpecialSymbol;
            boolean banNames;
            boolean banFamilies;
            boolean banPhone;
            boolean banWord;
            long maxPeriodPd = 90;
            long minPeriodPd;
            long numberFailedLogin;
            long timeLock;
            boolean banLoginInPassword;
            long maxLengthPass = 30;
            long maxLengthLogin = 50;
            boolean changeFirstPass = true;
            long maxPeriodFirstPass = 5;
            boolean banRepeatChar = true;
            boolean banRepAnyWhereMoreTwoChar = false;
            boolean banKeyboard = false;
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

                ClassNode policyCls = krn.getClassNodeByName(policyName);
                AttrRequestBuilder arb = new AttrRequestBuilder(policyCls.getKrnClass(), krn).add(ATTR_MAX_PERIOD_PASSWORD)
                        .add(ATTR_MIN_LOGIN_LENGTH).add(ATTR_MIN_PASSWORD_LENGTH).add(ATTR_MAX_VALID_PERIOD)
                        .add(ATTR_MIN_PASSWORD_LENGTH_ADMIN).add(ATTR_NUMBER_PASSWORD_DUBLICATE)
                        .add(ATTR_NUMBER_PASSWORD_DUBLICATE_ADMIN).add(ATTR_USE_NUMBERS).add(ATTR_USE_NOTALLNUMBERS).add(ATTR_USE_SYMBOLS)
                        .add(ATTR_USE_REGISTER_SYMBOLS).add(ATTR_USE_SPECIAL_SYMBOL).add(ATTR_BAN_NAMES).add(ATTR_BAN_FAMILIES)
                        .add(ATTR_BAN_PHONE).add(ATTR_BAN_WORD).add(ATTR_BAN_KEYBOARD).add(ATTR_MIN_PERIOD_PASSWORD).add(ATTR_NUMBER_FAILED_LOGIN)
                        .add(ATTR_TIME_LOCK).add(ATTR_BAN_LOGIN_IN_PASSWORD).add(ATTR_MAX_LENGTH_PASS).add(ATTR_MAX_LENGTH_LOGIN)
                        .add(ATTR_CHANGE_FIRST_PASS).add(ATTR_MAX_PERIOD_FIRST_PASS).add(ATTR_BAN_REPEAT_CHAR).add(ATTR_BAN_REPEAT_ANYWHERE_MORE_2_NOREGISTER_CHAR)
                        .add(ATTR_ACTIVATE_LIABILITY_SIGN).add(ATTR_LIABILITY_SIGN_PERIOD).add(ATTR_ACTIVATE_ECP_EXPIRY_NOTIF).add(ATTR_ECP_EXPIRY_NOTIF_PERIOD)
                        .add(ATTR_ACTIVATE_TEMP_REG_NOTIF).add(ATTR_TEMP_REG_NOTIF_PERIOD).add(ATTR_CHECK_CLIENT_IP).add(ATTR_BAN_USE_OWN_IDENTIFICATION_DATA);
                if (krn.isRNDB() || krn.hasUseECP()) {
                	arb.add(ATTR_USE_ECP);
                }

                long[] objIds = { krnObj.id };
                Object[] row = krn.getObjects(objIds, arb.build(), 0).get(0);

                maxPeriodPd = arb.getLongValue(ATTR_MAX_PERIOD_PASSWORD, row, 90);
                minLoginLength = arb.getLongValue(ATTR_MIN_LOGIN_LENGTH, row, 3);
                minPasswordLength = arb.getLongValue(ATTR_MIN_PASSWORD_LENGTH, row, 6);
                maxValidPeriod = arb.getLongValue(ATTR_MAX_VALID_PERIOD, row, 30);
                minPasswordLengthAdmin = arb.getLongValue(ATTR_MIN_PASSWORD_LENGTH_ADMIN, row, 12);
                numPassDubl = arb.getLongValue(ATTR_NUMBER_PASSWORD_DUBLICATE, row, 3);
                numPassDublAdmin = arb.getLongValue(ATTR_NUMBER_PASSWORD_DUBLICATE_ADMIN, row, 20);
                useNumbers = arb.getBooleanValue(ATTR_USE_NUMBERS, row, true);
                useSymbols = arb.getBooleanValue(ATTR_USE_SYMBOLS, row, true);
                useRegisterSymbols = arb.getBooleanValue(ATTR_USE_REGISTER_SYMBOLS, row, false);
                useSpecialSymbol = arb.getBooleanValue(ATTR_USE_SPECIAL_SYMBOL, row, false);
                banNames = arb.getBooleanValue(ATTR_BAN_NAMES, row, true);
                banFamilies = arb.getBooleanValue(ATTR_BAN_FAMILIES, row, true);
                banPhone = arb.getBooleanValue(ATTR_BAN_PHONE, row, true);
                banWord = arb.getBooleanValue(ATTR_BAN_WORD, row, true);
                minPeriodPd = arb.getLongValue(ATTR_MIN_PERIOD_PASSWORD, row, 2);
                numberFailedLogin = arb.getLongValue(ATTR_NUMBER_FAILED_LOGIN, row, 10);
                timeLock = arb.getLongValue(ATTR_TIME_LOCK, row, 30);
                banLoginInPassword = arb.getBooleanValue(ATTR_BAN_LOGIN_IN_PASSWORD, row, true);
                maxLengthPass = arb.getLongValue(ATTR_MAX_LENGTH_PASS, row, 30);
                maxLengthLogin = arb.getLongValue(ATTR_MAX_LENGTH_LOGIN, row, 50);
                changeFirstPass = arb.getBooleanValue(ATTR_CHANGE_FIRST_PASS, row, true);
                maxPeriodFirstPass = arb.getLongValue(ATTR_MAX_PERIOD_FIRST_PASS, row, 5);
                banRepeatChar = arb.getBooleanValue(ATTR_BAN_REPEAT_CHAR, row, true);
            	useNotAllNumbers = arb.getBooleanValue(ATTR_USE_NOTALLNUMBERS, row, false);
            	banRepAnyWhereMoreTwoChar = arb.getBooleanValue(ATTR_BAN_REPEAT_ANYWHERE_MORE_2_NOREGISTER_CHAR, row, false);
            	banKeyboard = arb.getBooleanValue(ATTR_BAN_KEYBOARD, row, false);
                activateLiabilitySign = arb.getBooleanValue(ATTR_ACTIVATE_LIABILITY_SIGN, row, false);
                liabilitySignPeriod = arb.getLongValue(ATTR_LIABILITY_SIGN_PERIOD, row, 365);
                activateECPExpiryNotif = arb.getBooleanValue(ATTR_ACTIVATE_ECP_EXPIRY_NOTIF, row, false);
                ecpExpiryNotifPeriod = arb.getLongValue(ATTR_ECP_EXPIRY_NOTIF_PERIOD, row, 30);
                activateTempRegNotif = arb.getBooleanValue(ATTR_ACTIVATE_TEMP_REG_NOTIF, row, false);
                tempRegNotifPeriod = arb.getLongValue(ATTR_TEMP_REG_NOTIF_PERIOD, row, 0);
                checkClientIp = arb.getBooleanValue(ATTR_CHECK_CLIENT_IP, row, false);
                if (krn.isRNDB() || krn.hasUseECP()) {
                	useECP = arb.getBooleanValue(ATTR_USE_ECP, row, false);
                }
                if (!krn.isULDB() && !krn.isRNDB()) {
                	banUseOwnIdentificationData = arb.getBooleanValue(ATTR_BAN_USE_OWN_IDENTIFICATION_DATA, row, false);
                }
            } catch (Exception e) {
                kz.tamur.rt.Utils.outErrorCreateAttrPolicy();
                maxValidPeriod = 30;
                minPasswordLengthAdmin = 12;
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
                banWord = false;
                banKeyboard = true;
                minPeriodPd = 2;
                numberFailedLogin = 10;
                timeLock = 30;
                banLoginInPassword = true;
                maxLengthPass = 30;
                maxLengthLogin = 50;
                changeFirstPass = true;
                maxPeriodFirstPass = 5;
                banRepeatChar = true;
                banRepAnyWhereMoreTwoChar = false;
                activateLiabilitySign = false;
                liabilitySignPeriod = 365;
                activateECPExpiryNotif = false;
                ecpExpiryNotifPeriod = 30;
                activateTempRegNotif = false;
                tempRegNotifPeriod = 0;
                checkClientIp = false;
            }
            
            policyWrapper = new PasswordPolicy(maxValidPeriod, minPasswordLength, minLoginLength, minPasswordLengthAdmin, 
            		numPassDubl, numPassDublAdmin, useNumbers, useNotAllNumbers, useSymbols, useRegisterSymbols, useSpecialSymbol, 
            		banNames, banFamilies, banPhone, banWord, maxPeriodPd, minPeriodPd, numberFailedLogin, timeLock,
            		banLoginInPassword, maxLengthPass, maxLengthLogin, changeFirstPass, maxPeriodFirstPass, 
            		banRepeatChar, banRepAnyWhereMoreTwoChar, banKeyboard, activateLiabilitySign, liabilitySignPeriod, 
            		activateECPExpiryNotif, ecpExpiryNotifPeriod, activateTempRegNotif, tempRegNotifPeriod, checkClientIp, useECP, banUseOwnIdentificationData);
        }
    }

	/**
     * временный метод переводит объект в эквивалент булева типа
     * 
     * @param объект
     *            который значение которого будет переведено в тип boolean
     * @return
     */
    public static Boolean toBoolean(Object o) {
        return (o instanceof Boolean) ? (Boolean) o : o.toString().equals("1") || o.toString().toUpperCase(Locale.ROOT).equals("TRUE");
    }

    public boolean isModified() {
        return isModified;
    }

    public void setModified(boolean isModified) {
        this.isModified = isModified;
        if (!isModified)
        	changes.clear();
    }

    public void reload() {
        isLoaded = false;
        load();
    }

    public PasswordPolicy getPolicyWrapper() {
		return policyWrapper;
	}
    
    public void propertyChanged(String property, Object oldValue, Object newValue) {
    	Object[] vals = changes.get(property);
    	if (vals == null) {
    		vals = new Object[] {oldValue, newValue};
    		changes.put(property, vals);
    	} else
    		vals[1] = newValue;
    	
    	if (vals[0].equals(vals[1])) changes.remove(property);
    }
    
    public List<String> logChanges() {
    	List<String> res = new ArrayList<String>(changes.size());
    
    	for (String prop : changes.keySet()) {
    		Object[] vals = changes.get(prop);
    		res.add(prop + " с " + vals[0] + " на " + vals[1]);
    	}
    	return res;
    }
}