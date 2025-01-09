package kz.tamur.common;

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
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_NOT_EASY_SYMBOLS;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_NOT_KEYBOARD;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_NOT_LOGIN;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_NOT_NAME;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_NOT_REP;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_NOT_REP_ANY_MORE_TWO;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_NOT_SURN;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_NOT_TEL;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_NOT_WORD;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_NO_ALL_NUMB;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_NO_NUMB;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_NO_REG;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_NO_SPEC;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_NO_SYMB;
import static kz.tamur.common.ErrorCodes.TYPE_WARNING;
import static kz.tamur.comps.FileUtils.readTextFromJar;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kz.tamur.comps.Constants;
import kz.tamur.util.PasswordService;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.Date;
import com.cifs.or2.kernel.DateValue;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.Time;

public class PasswordPolicy {
    // атрибуты политики
    // рекомендуемый срок действия пароля (при его достижении, пользователю
    // предлагается изменить пароль)
    private long maxValidPeriod;
    // минимальная длина пароля
    private long minPDLength;
    // минимальная длина имени учётной записи
    private long minLoginLength;
    // Минимальная длина пароля для администраторов
    private long minPDLengthAdmin;
    // Количество предыдущих паролей для проверки дублирования для пользователей
    // (по умолчанию: 3)
    private long numPassDubl;
    // Количество предыдущих паролей для проверки дублирования для
    // администраторов (по умолчанию 20)
    private long numPassDublAdmin;
    // Сложность пароля: использовать цифры
    private boolean useNumbers;
    // Сложность пароля: не должно явно преобладать цифры
    private boolean useNotAllNumbers;
    // Сложность пароля: использовать буквенные символы
    private boolean useSymbols;
    // Сложность пароля: использовать буквенные символы в различном регистре
    private boolean useRegisterSymbols;
    // Сложность пароля: использовать спец. символы
    private boolean useSpecialSymbol;
    // Проверка часто употребляемых выражений: запрет использования имён
    private boolean banNames;
    // Проверка часто употребляемых выражений: запрет использования фамилий
    private boolean banFamilies;
    // Проверка часто употребляемых выражений: запрет использования номеров
    // телефонов
    private boolean banPhone;
    // Проверка часто употребляемых выражений: запрет использования словарных
    // слов
    private boolean banWord;
    // Обязательность смены пароля через указанный промежуток времени (по
    // умолчанию: 90 дней)
    private long maxPeriodPD;
    // Минимальный промежуток времени между сменой пароля (по умолчанию: 48
    // часов)
    private long minPeriodPD;
    // Обработка неуспешных авторизаций: Количество неуспешных авторизаций после
    // которых запись блокируется (по умолчанию 10)
    private long numberFailedLogin;
    // Обработка неуспешных авторизаций: Время блокировки записи при достижении
    // лимита неуспешных авторизаций (по умолчанию 30 минут)
    private long timeLock;
    // Запрет использования логина пользователя в качестве пароля.
    private boolean banLoginInPD;
    
    private long maxLengthPass;
    private long maxLengthLogin;
    private boolean changeFirstPass;
    private long maxPeriodFirstPass;
    private boolean banRepeatChar;
    // Сложность пароля: запрет повтора в любом месте из более 2-х одинаковых символов пароля
    private boolean banRepAnyWhereMoreTwoChar;
    // Проверка часто употребляемых выражений: запрет ипользование выражений
    // которые легко вычисляется на клавиатуре
    private boolean banKeyboard;
    // Активация подписания обязательства
    private boolean activateLiabilitySign;
    // Периодичность подписания соглашения о неразглашении
    private long liabilitySignPeriod;
    // Активация оповещения об истечении срока действия ЭЦП 
    private boolean activateECPExpiryNotif;
    // Период оповещения об истечении срока действия ЭЦП
    private long ecpExpiryNotifPeriod;
    // Активация оповещения об истечении срока временной регистрации
    private boolean activateTempRegNotif;
    // Период оповещения об истечении срока временной регистрации
    private long tempRegNotifPeriod;
    // Проверка ip-адреса рабочей станции пользователя
    private boolean checkClientIp;
    // Разрешить использовать ЭЦП для входа?
    private boolean useECP = false;
    // Запрет использования своего имени, фамилии, даты рождения, телефона в качестве пароля.
    private boolean banUseOwnIdentificationData = false;
    
    private static List<String> dictNames = null;
    private static List<String> dictSurnames = null;
    private static List<String> dictWords = null;
    private static List<String> dictKeyboards = null;

    public PasswordPolicy(long maxValidPeriod, long minPDLength, long minLoginLength, long minPDLengthAdmin,
			long numPassDubl, long numPassDublAdmin, boolean useNumbers, boolean useNotAllNumbers, boolean useSymbols,
			boolean useRegisterSymbols, boolean useSpecialSymbol, boolean banNames, boolean banFamilies,
			boolean banPhone, boolean banWord, long maxPeriodPD, long minPeriodPD, long numberFailedLogin,
			long timeLock, boolean banLoginInPD, long maxLengthPass, long maxLengthLogin, boolean changeFirstPass,
			long maxPeriodFirstPass, boolean banRepeatChar, boolean banRepAnyWhereMoreTwoChar, boolean banKeyboard,
			boolean activateLiabilitySign, long liabilitySignPeriod,
			boolean activateECPExpiryNotif, long ecpExpiryNotifPeriod,
			boolean activateTempRegNotif, long tempRegNotifPeriod, boolean checkClientIp, boolean useECP, boolean banUseOwnIdentificationData) {
		super();
		this.maxValidPeriod = maxValidPeriod;
		this.minPDLength = minPDLength;
		this.minLoginLength = minLoginLength;
		this.minPDLengthAdmin = minPDLengthAdmin;
		this.numPassDubl = numPassDubl;
		this.numPassDublAdmin = numPassDublAdmin;
		this.useNumbers = useNumbers;
		this.useNotAllNumbers = useNotAllNumbers;
		this.useSymbols = useSymbols;
		this.useRegisterSymbols = useRegisterSymbols;
		this.useSpecialSymbol = useSpecialSymbol;
		this.banNames = banNames;
		this.banFamilies = banFamilies;
		this.banPhone = banPhone;
		this.banWord = banWord;
		this.maxPeriodPD = maxPeriodPD;
		this.minPeriodPD = minPeriodPD;
		this.numberFailedLogin = numberFailedLogin;
		this.timeLock = timeLock;
		this.banLoginInPD = banLoginInPD;
		this.maxLengthPass = maxLengthPass;
		this.maxLengthLogin = maxLengthLogin;
		this.changeFirstPass = changeFirstPass;
		this.maxPeriodFirstPass = maxPeriodFirstPass;
		this.banRepeatChar = banRepeatChar;
		this.banRepAnyWhereMoreTwoChar = banRepAnyWhereMoreTwoChar;
		this.banKeyboard = banKeyboard;
		this.activateLiabilitySign = activateLiabilitySign;
		this.liabilitySignPeriod = liabilitySignPeriod;
		this.activateECPExpiryNotif = activateECPExpiryNotif;
		this.ecpExpiryNotifPeriod = ecpExpiryNotifPeriod;
		this.activateTempRegNotif = activateTempRegNotif;
		this.tempRegNotifPeriod = tempRegNotifPeriod;
		this.checkClientIp = checkClientIp;
		this.useECP = useECP;
		this.banUseOwnIdentificationData = banUseOwnIdentificationData;
	}

    /**
	 * 
	 */
    public String verificationPassAndLogin(char[] pd, String userName,boolean isAdmin, boolean chekLogin) {
        return verificationPassAndLogin(null, pd, userName, isAdmin,  chekLogin, new Locale("ru"));
    }
    
    /**
     * верификация пароля на соответствие требования м политики паролей
     * 
     * @param pd
     *            пароль пользователя
     * @param userName
     *            логин пользователя
     * @param isAdmin
     *            пользователь администратор?
     * @param chekLogin
     *            необходима ли проверка логина?
     * @param loc
     *            язык на котором выдавать сообщение?
     * @return если NULL то пароль валиден
     */
    public String verificationPassAndLogin(KrnObject o, char[] pd, String userName, boolean isAdmin, boolean chekLogin, Locale loc) {
        // чтение ресурсов
        final ResourceBundle res = ResourceBundle.getBundle("kz.tamur.rt.RuntimeResources", loc);
        try {
        	verifyPassword(o, pd, chekLogin ? userName : null, isAdmin, false, null, null);
        	if (isBanUseOwnIdentificationData()) {
            	checkForUseOwnIdentificationData(o, pd);
            }
        } catch (KrnException e) {
            String mess = "";
            switch (e.code) {
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
	                mess = mess.replaceFirst("X", e.getMessage());
	                break;
	            case PASS_VALID_PWD_MIN_LOGIN:
	                mess = res.getString("validPwdMinLogin");
	                mess = mess.replaceFirst("X", e.getMessage());
	                break;
	            case PASS_VALID_PWD_MAX_LOGIN:
	                mess = res.getString("validPwdMaxLogin");
	                mess = mess.replaceFirst("X", e.getMessage());
	                break;
	            case PASS_VALID_PWD_MIN_PASS:
	                mess = res.getString("validPwdmMinPass");
	                mess = mess.replaceFirst("X", e.getMessage());
	                break;
	            case PASS_VALID_PWD_MIN_PASS_ADM:
	                mess = res.getString("validPwdMinPassAdm");
	                mess = mess.replaceFirst("X", e.getMessage());
	                break;
	            case PASS_VALID_PWD_MAX_PASS:
	                mess = res.getString("validPwdmMaxPass");
	                mess = mess.replaceFirst("X", e.getMessage());
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
	                mess = mess.replaceFirst("X", e.getMessage());
	                break;
	            case PASS_VALID_PWD_NOT_EASY_SYMBOLS:
	                mess = res.getString("validPwdNotIdentificationData");
	                break;
	            default:
	            	mess = e.getMessage();
            }
            return mess;
        }
        return null;
    }
    
    private void checkForUseOwnIdentificationData(KrnObject object, char[] newpd) throws KrnException {
    	// Проверка включения имени, фамилии и даты рождения для кызмета
    	Kernel krn = Kernel.instance();
    	if (object != null) {
	        KrnClass userCls = krn.getClassById(object.classId);
	        KrnAttribute personalAttr = krn.getAttributeByName(userCls, "персона");
	        if (personalAttr != null) {
		        KrnObject personalObj = krn.getObjectsSingular(object.id, personalAttr.id, false);
		        if (personalObj != null) {
		            KrnClass personalCls = krn.getClassByName("Персонал");
			        if (personalCls != null) {
			            KrnAttribute zapTablPersDannyhAttr = krn.getAttributeByName(personalCls, "текущ  состояние -зап табл персон данных-");
				        if (zapTablPersDannyhAttr != null) {
				            KrnObject zapTablPersDannyhObj = krn.getObjectsSingular(personalObj.id, zapTablPersDannyhAttr.id, false);
				            if (zapTablPersDannyhObj != null) {
				                KrnClass zapTablPersDannyhCls = krn.getClassByName("Зап табл персон данных");
					            if (zapTablPersDannyhCls != null) {
					                String pdStr = new String(newpd);
					            	String pdUp = pdStr.toUpperCase(Constants.OK);
	
					                KrnAttribute nameAttr = krn.getAttributeByName(zapTablPersDannyhCls, "идентиф -имя-");
					            	String personName = krn.getStringsSingular(zapTablPersDannyhObj.id, nameAttr.id, 0, false, false);
					            	if (personName != null && personName.length() > 0) { 
						            	String personNameUp = personName.toUpperCase(Constants.OK);
						            	if (pdUp.contains(personNameUp)) {
						                    throw new KrnException(PASS_VALID_PWD_NOT_EASY_SYMBOLS, object, "", TYPE_WARNING); // Пароль не должен включать в себя легко вычисляемые сочетания символов!
						            	}
					            	}
					            	
					                KrnAttribute surnameAttr = krn.getAttributeByName(zapTablPersDannyhCls, "идентиф -фамилия-");
					            	String personSurname = krn.getStringsSingular(zapTablPersDannyhObj.id, surnameAttr.id, 0, false, false);
					            	if (personSurname != null && personSurname.length() > 0) {
						            	String personSurnameUp = personSurname.toUpperCase(Constants.OK);
						            	if (pdUp.contains(personSurnameUp)) {
						                    throw new KrnException(PASS_VALID_PWD_NOT_EASY_SYMBOLS, object, "", TYPE_WARNING);
						            	}
					            	}
					            	
					                KrnAttribute middlenameAttr = krn.getAttributeByName(zapTablPersDannyhCls, "идентиф -отчество-");
					            	String personMiddlename = krn.getStringsSingular(zapTablPersDannyhObj.id, middlenameAttr.id, 0, false, false);
					            	if (personMiddlename != null && personMiddlename.length() > 0) {
						            	String personMiddlenameUp = personMiddlename.toUpperCase(Constants.OK);
						            	if (pdUp.contains(personMiddlenameUp)) {
						                    throw new KrnException(PASS_VALID_PWD_NOT_EASY_SYMBOLS, object, "", TYPE_WARNING);
						            	}
					            	}
					            	
					            	KrnAttribute telAttr = krn.getAttributeByName(zapTablPersDannyhCls, "семья -домашний телефон");
					            	String personTel = krn.getStringsSingular(zapTablPersDannyhObj.id, telAttr.id, 0, false, false);
					            	if (personTel != null && personTel.length() > 0) {
						            	String personTelUp = personTel.toUpperCase(Constants.OK);
						            	if (pdUp.contains(personTelUp)) {
						                    throw new KrnException(PASS_VALID_PWD_NOT_EASY_SYMBOLS, object, "", TYPE_WARNING);
						            	}
					            	}
					            	
					                KrnAttribute dateAttr = krn.getAttributeByName(zapTablPersDannyhCls, "идентиф -дата рождения-");
					            	DateValue[] dateValues = krn.getDateValues(new long[] {zapTablPersDannyhObj.id}, dateAttr, 0);
					            	Date orDate = (dateValues.length == 0) ? null : dateValues[0].value;
					            	if (orDate != null) {
						            	Calendar c = Calendar.getInstance();
						            	c.set(Calendar.YEAR, orDate.year);
						            	c.set(Calendar.MONTH, orDate.month);
						            	c.set(Calendar.DAY_OF_MONTH, orDate.day);
						            	String dateString = String.format("%1$td%1$tm%1$tY", c.getTime());
						            	if (pdUp.contains(dateString)) {
						                    throw new KrnException(PASS_VALID_PWD_NOT_EASY_SYMBOLS, object, "", TYPE_WARNING);
						            	}
					            	}
					            }
				            }
				        }
			        }
		        }
	        }
    	}
    }

    public void verifyPassword(KrnObject object, char[] newpd, String name, boolean admin, boolean isLogged, String psw, Time lastChangeTime) throws KrnException {
        String pdStr = new String(newpd);
        String newPd = PasswordService.getInstance().encrypt(pdStr);

        // проверка на допустимый минимальный период смены пароля
        Calendar curDate = new GregorianCalendar(TimeZone.getTimeZone("Asia/Dhaka"));

        // получить дату с которой разрешено менять пароль
        Calendar nextChangeDate = null; 
        if (minPeriodPD > 0 && lastChangeTime != null && kz.tamur.rt.Utils.isEmpty(lastChangeTime)) {
        	nextChangeDate = new GregorianCalendar(TimeZone.getTimeZone("Asia/Dhaka"));
        	nextChangeDate.set(Calendar.MILLISECOND, lastChangeTime.msec);
        	nextChangeDate.set(Calendar.SECOND, lastChangeTime.sec);
        	nextChangeDate.set(Calendar.MINUTE, lastChangeTime.min);
        	nextChangeDate.set(Calendar.HOUR_OF_DAY, lastChangeTime.hour);
            nextChangeDate.set(Calendar.DAY_OF_MONTH, lastChangeTime.day);
            nextChangeDate.set(Calendar.MONTH, lastChangeTime.month);
            nextChangeDate.set(Calendar.YEAR, lastChangeTime.year);

            nextChangeDate.add(Calendar.DAY_OF_MONTH, (int) (Constants.ONE_DAY * minPeriodPD));
        }
        
        if (nextChangeDate != null && isLogged && curDate.before(nextChangeDate)) {
            throw new KrnException(PASS_MIN_PERIOD_PASS, object, "" + minPeriodPD, TYPE_WARNING);// Пароль разрешено менять не чаще чем раз в X суток!
        }

        if (minLoginLength > 0 && name != null && minLoginLength > name.length() && !"sys".equals(name)) {
            throw new KrnException(PASS_VALID_PWD_MIN_LOGIN, object, "" + minLoginLength, TYPE_WARNING); // Имя пользователя не может быть меньше X символов!
        }

        if (maxLengthLogin > 0 && name != null && maxLengthLogin < name.length() && !"sys".equals(name)) {
            throw new KrnException(PASS_VALID_PWD_MAX_LOGIN, object, "" + maxLengthLogin, TYPE_WARNING); // Имя пользователя не может быть больше X символов!
        }

        // минимальная длина пароля для пользователей
        if (!admin && minPDLength != 0 && minPDLength > newpd.length) {
            throw new KrnException(PASS_VALID_PWD_MIN_PASS, object, "" + minPDLength, TYPE_WARNING); // Пароль не может быть меньше X символов!
        }
        // Минимальная длина пароля для администраторов
        if (admin && minPDLengthAdmin != 0 && minPDLengthAdmin > newpd.length) {
            throw new KrnException(PASS_VALID_PWD_MIN_PASS_ADM, object, "" + minPDLengthAdmin, TYPE_WARNING); // Для администратора пароль не может быть меньше X символов!
        }

        // максимальная длина пароля
        if (maxLengthPass != 0 && maxLengthPass < newpd.length) {
            throw new KrnException(PASS_VALID_PWD_MAX_PASS, object, "" + maxLengthPass, TYPE_WARNING); // Пароль не может быть больше X символов!
        }

        boolean temp;
        // Сложность пароля: использовать цифры
        if (useNumbers) {
            temp = true;
            for (char ch : newpd) {
                if (Character.isDigit(ch)) {
                    temp = false;
                    break;
                }
            }
            if (temp) {
                throw new KrnException(PASS_VALID_PWD_NO_NUMB, object); // В пароле должны присутствовать цифры!
            }
        }
        // Сложность пароля: не должно явно преобладать цифры
        if (useNotAllNumbers) {
        	int i = 0;
        	for (char ch : newpd) {
                if (Character.isDigit(ch)) {
                    i++;
                }
            }
        	if (i > newpd.length - i) throw new KrnException(PASS_VALID_PWD_NO_ALL_NUMB, object); // В пароле не должно явно преобладать цифры!
        }
        // Сложность пароля: использовать буквы
        if (useSymbols) {
            temp = true;
            for (char ch : newpd) {
                if (Character.isLetter(ch)) {
                    temp = false;
                    break;
                }
            }
            if (temp) {
                throw new KrnException(PASS_VALID_PWD_NO_SYMB, object); // В пароле должны присутствовать буквы!
            }
        }
        // Сложность пароля: использовать буквенные символы в различном
        // регистре
        int temp2;
        if (useRegisterSymbols) {
            temp2 = 0;
            for (char ch : newpd) {
                if ((temp2 & 1) != 1 && Character.isLowerCase(ch)) {
                    temp2 |= 1;
                }
                if ((temp2 & 2) != 2 && Character.isUpperCase(ch)) {
                    temp2 |= 2;
                }
                if (temp2 == 3) {
                    break;
                }
            }
            if (temp2 != 3) {
                throw new KrnException(PASS_VALID_PWD_NO_REG, object); // В пароле должны присутствовать буквы в различном регистре!
            }
        }

        // спецсимволы
        final Matcher M01 = Pattern.compile("[№\\:\"\\;'|`~=!@#\\$%\\^&\\.\\*,\\{\\}\\(\\)_\\+\\-{}\\[\\]<>\\\\\\?/]").matcher(pdStr);
        // Сложность пароля: использовать спец. символы
        if (useSpecialSymbol && !M01.find()) {
            throw new KrnException(PASS_VALID_PWD_NO_SPEC, object); // В пароле должны присутствовать специальные символы!
        }

        String pdUp = pdStr.toUpperCase(Constants.OK);
        // Проверка часто употребляемых выражений: запрет использования
        // имён
        if (banNames) {
        	List<String> list = readNames();
        	for (String s : list) {
               if (pdUp.contains(s)) {
            	   throw new KrnException(PASS_VALID_PWD_NOT_NAME, object); // В пароле запрещено использовать имена!
               }
            }
        }
        // Проверка часто употребляемых выражений: запрет использования
        // фамилий
        if (banFamilies) {
        	List<String> list = readSurnames();
        	for (String s : list) {
               if (pdUp.contains(s)) {
            	   throw new KrnException(PASS_VALID_PWD_NOT_SURN, object); // В пароле запрещено использовать фамилии!
               }
            }
        }

        // Проверка часто употребляемых выражений: запрет использования
        // номеров телефонов
        /**
         * шаблоны телефонных носмеров, которые отследит регулярка
         * 
         * +79261234567 89261234567 79261234567 +7 926 123 45 67 8(926)123-45-67
         * 123-45-67 23-45-67 9261234567 79261234567 (495)1234567 (495) 123 45
         * 67 89261234567 8-926-123-45-67 8 927 1234 234 8 927 12 12 888 8 927
         * 12 555 12 8 927 123 8 123
         */
        final Matcher M02 = Pattern.compile("^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{6,10}$").matcher(pdStr);

        if (banPhone && M02.find()) {
            throw new KrnException(PASS_VALID_PWD_NOT_TEL, object); // В пароле запрещено использовать номера телефонов!
        }
        // Проверка часто употребляемых выражений: запрет использования
        // словарных слов
        if (banWord) {
        	List<String> list = readWords();
        	for (String s : list) {
               if (pdUp.contains(s)) {
            	   throw new KrnException(PASS_VALID_PWD_NOT_WORD, object); // В пароле запрещено использовать частоупотребляемые слова!
               }
            }
        }
        // Проверка часто употребляемых выражений: запрет использования
        // клавиатурных слов
        if (banKeyboard) {
        	if (pdUp.length() > 3) {
        		int y = 0;
	        	char a = 0;
	        	List<String> list = readKeyboards();
	            int lists = list.size();
	            if (lists > 0) {
	            	for(int i = 0; i < pdUp.length() - 1; i++) {
		            	for (int j = 0; j < lists; j++) {
		            		// Сравниваем каждый символ пароля с первым символом в каждой строке в списке запрета клавиатурных выражений
			            	if(pdUp.charAt(i) == list.get(j).charAt(0)) {
			            		for(int u = 1; u < list.get(j).length(); u++) {
			            			// Ищем непосредственно второй символ в выражений
			            			if(pdUp.charAt(i+1) == list.get(j).charAt(u)) {
			            				boolean tempa = false;
			            				for(int r = 1; r < list.get(j).length(); r++) {
			            					// Если это первое вхождение сюда то а = 0, если нет то мы ищем связь с предудущим удачным входа значений a
				            				if (a == list.get(j).charAt(r) || a == 0){
				            					y++;
				            					tempa = true;
				            					break;
				            				}
			            				}
			            				if(!tempa) y = 1;
			            				
			            				a = pdUp.charAt(i); // запоминаем последний удачный символ
			            				
			            				if(y > 2) 
			            					throw new KrnException(PASS_VALID_PWD_NOT_KEYBOARD, object); // В пароле запрещено использовать выражений которые легко вычисляеться на клавиатуре!
					            		break;
			            			}
			            		}
			            	}
		            	}
	            	}
	            }
        	}
        }
        // Запрет использования логина пользователя в качестве пароля.
        if (banLoginInPD && name != null && pdUp.contains(name.toUpperCase(Locale.getDefault()))) {
            throw new KrnException(PASS_VALID_PWD_NOT_LOGIN, object); // В пароле не должен употребляться логин пользователя!
        }

        // Проверка на повторение первых трёх символов
        if (banRepeatChar) {
            if (newpd.length > 2 && newpd[0] == newpd[1] && newpd[1] == newpd[2]) {
                throw new KrnException(PASS_VALID_PWD_NOT_REP, object); // В пароле запрещено повторение первых трёх символов!
            }
        }

        // Проверка на повторение в любом месте из более двух символов
        if (banRepAnyWhereMoreTwoChar) {
        	if (pdUp.length() > 2){
	        	for(int i = 0;i < pdUp.length()-2;i++) {
		            if (pdUp.charAt(i) == pdUp.charAt(i+1) && pdUp.charAt(i+1) == pdUp.charAt(i+2)) {
		            	throw new KrnException(PASS_VALID_PWD_NOT_REP_ANY_MORE_TWO, object); // В пароле запрещено повторение последовательности из более 2-х одинаковых символов подряд
		            }
	        	}
        	}
        }
        // Сравнение пароля с предыдущими (полное соответствие)
        long k = admin ? numPassDublAdmin : numPassDubl;
        if (k > 0 && psw != null && !psw.isEmpty()) {
        	String pds[] = psw.split(";");
            if (k > 0) {
                // Перебор k предыдущих паролей (хэшей) с конца списка
            	int repeats = 0;
            	for (int i = pds.length - 1; i >= 0 && k > repeats; i--) {
            		repeats++;
                    if (pds[i].equals(newPd)) {
                        throw new KrnException(PASS_MESS_PASS_DUPL, object, "" + k, TYPE_WARNING); // Пароль не должен повторять k предыдущих паролей!
                    }
                }
            }
        }

        // Смена пароля корректна
    }
    
    public void checkPassAndLogin(char[] pd, String name, boolean admin, boolean chekLogin) throws KrnException {
        if (chekLogin) {
            // Минимальная длина логина
        	if (minLoginLength > 0 && name != null && minLoginLength > name.length() && !"sys".equals(name)) {
                throw new KrnException(PASS_VALID_PWD_MIN_LOGIN, "" + minLoginLength); // Имя пользователя не может быть меньше X символов!
            }
        	// Максимальная длина логина
            if (maxLengthLogin > 0 && name != null && maxLengthLogin < name.length() && !"sys".equals(name)) {
                throw new KrnException(PASS_VALID_PWD_MAX_LOGIN, "" + maxLengthLogin); // Имя пользователя не может быть больше X символов!
            }
        } else {
	    	String pdStr = new String(pd);
	        // Минимальная длина пароля для пользователей
	        if (!admin && minPDLength != 0 && minPDLength > pd.length) {
	            throw new KrnException(PASS_VALID_PWD_MIN_PASS, "" + minPDLength); // Пароль не может быть меньше X символов!
	        }
	        // Минимальная длина пароля для администраторов
	        if (admin && minPDLengthAdmin != 0 && minPDLengthAdmin > pd.length) {
	            throw new KrnException(PASS_VALID_PWD_MIN_PASS_ADM, "" + minPDLengthAdmin); // Для администратора пароль не может быть меньше X символов!
	        }
	        // Максимальная длина пароля
	        if (maxLengthPass != 0 && maxLengthPass < pd.length) {
	            throw new KrnException(PASS_VALID_PWD_MAX_PASS, "" + maxLengthPass); // Пароль не может быть больше X символов!
	        }
	        boolean temp;
	        // Сложность пароля: использовать цифры
	        if (useNumbers) {
	            temp = true;
	            for (char ch : pd) {
	                if (Character.isDigit(ch)) {
	                    temp = false;
	                    break;
	                }
	            }
	            if (temp) {
	                throw new KrnException(PASS_VALID_PWD_NO_NUMB); // В пароле должны присутствовать цифры!
	            }
	        }
	        // Сложность пароля: не должно явно преобладать цифры
	        if (useNotAllNumbers) {
	        	int i = 0;
	        	for (char ch : pd) {
	                if (Character.isDigit(ch)) {
	                    i++;
	                }
	            }
	        	if (i > pd.length - i) throw new KrnException(PASS_VALID_PWD_NO_ALL_NUMB); // В пароле не должно явно преобладать цифры!
	        }
	        // Сложность пароля: использовать буквы
	        if (useSymbols) {
	            temp = true;
	            for (char ch : pd) {
	                if (Character.isLetter(ch)) {
	                    temp = false;
	                    break;
	                }
	            }
	            if (temp) {
	                throw new KrnException(PASS_VALID_PWD_NO_SYMB); // В пароле должны присутствовать буквы!
	            }
	        }
	        // Сложность пароля: использовать буквенные символы в различном регистре
	        int temp2;
	        if (useRegisterSymbols) {
	            temp2 = 0;
	            for (char ch : pd) {
	                if ((temp2 & 1) != 1 && Character.isLowerCase(ch)) {
	                    temp2 |= 1;
	                }
	                if ((temp2 & 2) != 2 && Character.isUpperCase(ch)) {
	                    temp2 |= 2;
	                }
	                if (temp2 == 3) {
	                    break;
	                }
	            }
	            if (temp2 != 3) {
	                throw new KrnException(PASS_VALID_PWD_NO_REG); // В пароле должны присутствовать буквы в различном регистре!
	            }
	        }
	        // Спецсимволы
	        final Matcher M01 = Pattern.compile("[№\\:\"\\;'|`~=!@#\\$%\\^&\\.\\*,\\{\\}\\(\\)_\\+\\-{}\\[\\]<>\\\\\\?/]").matcher(pdStr);
	        // Сложность пароля: использовать спец. символы
	        if (useSpecialSymbol && !M01.find()) {
	            throw new KrnException(PASS_VALID_PWD_NO_SPEC); // В пароле должны присутствовать специальные символы!
	        }
	        String pdUp = pdStr.toUpperCase(Constants.OK);
	        // Проверка часто употребляемых выражений: запрет использования имён
	        if (banNames) {
	        	List<String> list = readNames();
	        	for (String s : list) {
	               if (pdUp.contains(s)) {
	            	   throw new KrnException(PASS_VALID_PWD_NOT_NAME); // В пароле запрещено использовать имена!
	               }
	            }
	        }
	        // Проверка часто употребляемых выражений: запрет использования фамилий
	        if (banFamilies) {
	        	List<String> list = readSurnames();
	        	for (String s : list) {
	               if (pdUp.contains(s)) {
	            	   throw new KrnException(PASS_VALID_PWD_NOT_SURN); // В пароле запрещено использовать фамилии!
	               }
	            }
	        }
	        // Проверка часто употребляемых выражений: запрет использования номеров телефонов
	        /**
	         * Шаблоны телефонных номеров, которые отследит регулярка
	         * 
	         * +79261234567 89261234567 79261234567 +7 926 123 45 67 8(926)123-45-67
	         * 123-45-67 23-45-67 9261234567 79261234567 (495)1234567 (495) 123 45
	         * 67 89261234567 8-926-123-45-67 8 927 1234 234 8 927 12 12 888 8 927
	         * 12 555 12 8 927 123 8 123
	         */
	        final Matcher M02 = Pattern.compile("^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{6,10}$").matcher(pdStr);
	        if (banPhone && M02.find()) {
	            throw new KrnException(PASS_VALID_PWD_NOT_TEL); // В пароле запрещено использовать номера телефонов!
	        }
	        // Проверка часто употребляемых выражений: запрет использования словарных слов
	        if (banWord) {
	        	List<String> list = readWords();
	        	for (String s : list) {
	               if (pdUp.contains(s)) {
	            	   throw new KrnException(PASS_VALID_PWD_NOT_WORD); // В пароле запрещено использовать частоупотребляемые слова!
	               }
	            }
	        }
	        // Проверка часто употребляемых выражений: запрет использования клавиатурных слов
	        if (banKeyboard) {
	        	if (pdUp.length() > 3) {
	        		int y = 0;
		        	char a = 0;
		        	List<String> list = readKeyboards();
		            int lists = list.size();
		            if (lists > 0) {
		            	for(int i = 0; i < pdUp.length() - 1; i++) {
			            	for (int j = 0; j < lists; j++) {
			            		// Сравниваем каждый символ пароля с первым символом в каждой строке в списке запрета клавиатурных выражений
				            	if(pdUp.charAt(i) == list.get(j).charAt(0)) {
				            		for(int u = 1; u < list.get(j).length(); u++) {
				            			// Ищем непосредственно второй символ в выражений
				            			if(pdUp.charAt(i+1) == list.get(j).charAt(u)) {
				            				boolean tempa = false;
				            				for(int r = 1; r < list.get(j).length(); r++) {
				            					// Если это первое вхождение сюда то а = 0, если нет то мы ищем связь с предудущим удачным входа значений a
					            				if (a == list.get(j).charAt(r) || a == 0){
					            					y++;
					            					tempa = true;
					            					break;
					            				}
				            				}
				            				if(!tempa) y = 1;
				            				
				            				a = pdUp.charAt(i); // Запоминаем последний удачный символ
				            				
				            				if(y > 2) 
				            					throw new KrnException(PASS_VALID_PWD_NOT_KEYBOARD); // В пароле запрещено использовать выражений которые легко вычисляеться на клавиатуре!
						            		break;
				            			}
				            		}
				            	}
			            	}
		            	}
		            }
	        	}
	        }
	        // Запрет использования логина пользователя в качестве пароля.
	        if (banLoginInPD && name != null && pdUp.contains(name.toUpperCase(Locale.getDefault()))) {
	            throw new KrnException(PASS_VALID_PWD_NOT_LOGIN); // В пароле не должен употребляться логин пользователя!
	        }
	        // Проверка на повторение первых трёх символов
	        if (banRepeatChar) {
	            if (pd.length > 2 && pd[0] == pd[1] && pd[1] == pd[2]) {
	                throw new KrnException(PASS_VALID_PWD_NOT_REP); // В пароле запрещено повторение первых трёх символов!
	            }
	        }
	        // Проверка на повторение в любом месте из более двух символов
	        if (banRepAnyWhereMoreTwoChar) {
	        	if (pdUp.length() > 2){
		        	for(int i = 0;i < pdUp.length()-2;i++) {
			            if (pdUp.charAt(i) == pdUp.charAt(i+1) && pdUp.charAt(i+1) == pdUp.charAt(i+2)) {
			            	throw new KrnException(PASS_VALID_PWD_NOT_REP_ANY_MORE_TWO); // В пароле запрещено повторение последовательности из более 2-х одинаковых символов подряд
			            }
		        	}
	        	}
	        }
        }
    }

    /**
     * Получить рекомендуемый период действия пароля
     * 
     * @return период в миллисекундах
     */
    public long getMaxValidPeriod() {
        return maxValidPeriod;
    }

    /**
     * Установить рекомендуемый период действия пароля
     * 
     * @param maxValidPeriod
     *            период в миллисекундах
     */
    public void setMaxValidPeriod(long maxValidPeriod) {
        this.maxValidPeriod = maxValidPeriod;
    }

    /**
     * получить минимально разрешённую длину логина пользователя
     * 
     * @return длина логина пользователя
     */
    public long getMinLoginLength() {
        return minLoginLength;
    }

    /**
     * установить минимально разрешённую длину логина пользователя
     * 
     * @param minLoginLength
     *            минимально разрешённая длина
     */
    public void setMinLoginLength(long minLoginLength) {
        this.minLoginLength = minLoginLength;
    }

    /**
     * Получить минимально разрешённую длину пароля для пользователей
     * 
     * @return длина пароля
     */
    public long getMinPasswordLength() {
        return minPDLength;
    }

    /**
     * установить минимально разрешённую длину пароля для пользователей
     * 
     * @param minPDLength
     *            длина пароля
     */
    public void setMinPasswordLength(long minPDLength) {
        this.minPDLength = minPDLength;
    }

    /**
     * Получить минимальную длину пароля для учётной записи
     * 
     * @return длина пароля
     */
    public long getMinPasswordLengthAdmin() {
        return minPDLengthAdmin;
    }

    /**
     * Установить минимальную длину пароля для учётной записи
     * 
     * @param minPDLengthAdmin
     */
    public void setMinPasswordLengthAdmin(long minPDLengthAdmin) {
        this.minPDLengthAdmin = minPDLengthAdmin;
    }

    /**
     * Получить число не дублируемых предыдущих паролей
     * 
     * @return число паролей
     */
    public long getNumPassDubl() {
        return numPassDubl;
    }

    /**
     * Установить число не дублируемых предыдущих паролей
     * 
     * @param numPassDubl
     */
    public void setNumPassDubl(long numberPDDublicate) {
        this.numPassDubl = numberPDDublicate;
    }

    /**
     * Получить число не дублируемых предыдущих паролей для учетной записи
     * администратора
     * 
     * @return
     */
    public long getNumPassDublAdmin() {
        return numPassDublAdmin;
    }

    /**
     * Установить число не дублируемых предыдущих паролей для учетной записи
     * администратора
     * 
     * @param numPassDublAdmin
     */
    public void setNumPassDublAdmin(long numberPDDublicateAdmin) {
        this.numPassDublAdmin = numberPDDublicateAdmin;
    }

    /**
     * Получить значение атрибута "использовать цифры в пароле"
     * 
     * @return TRUE если необходимо использовать
     */
    public boolean getUseNumbers() {
        return useNumbers;
    }

    /**
     * Установить значение атрибута "использовать цифры в пароле"
     * 
     * @param useNumbers
     *            TRUE если необходимо использовать
     */
    public void setUseNumbers(boolean useNumbers) {
        this.useNumbers = useNumbers;
    }

    /**
     * Получить значение атрибута "в пароле не должно явно преобладать цифры"
     * 
     * @return TRUE если необходимо использовать
     */
    public boolean getUseNotAllNumbers() {
        return useNotAllNumbers;
    }

    /**
     * Установить значение атрибута "в пароле не должно явно преобладать цифры"
     * 
     * @param useNumbers
     *            TRUE если необходимо использовать
     */
    public void setUseNotAllNumbers(boolean useNotAllNumbers) {
        this.useNotAllNumbers = useNotAllNumbers;
    }
    
    /**
     * Получить значение атрибута "использовать буквы в пароле"
     * 
     * @return TRUE если необходимо использовать
     */
    public boolean getUseSymbols() {
        return useSymbols;
    }

    /**
     * Установить значение атрибута "использовать буквы в пароле"
     * 
     * @param useSymbols
     *            TRUE если необходимо использовать
     */
    public void setUseSymbols(boolean useSymbols) {
        this.useSymbols = useSymbols;
    }

    /**
     * Установить значение атрибута
     * "использовать буквы в различном регистре в пароле"
     * 
     * @return TRUE если необходимо использовать
     */
    public boolean getUseRegisterSymbols() {
        return useRegisterSymbols;
    }

    /**
     * Получить значение атрибута
     * "использовать буквы в различном регистре в пароле"
     * 
     * @param useRegisterSymbols
     *            TRUE если необходимо использовать
     */
    public void setUseRegisterSymbols(boolean useRegisterSymbols) {
        this.useRegisterSymbols = useRegisterSymbols;
    }

    /**
     * Получить значение атрибута
     * "использовать спец. символы в различном регистре в пароле"
     * 
     * @return TRUE если необходимо использовать
     */
    public boolean getUseSpecialSymbol() {
        return useSpecialSymbol;
    }

    /**
     * Установить значение атрибута
     * "использовать спец. символы в различном регистре в пароле"
     * 
     * @param useSpecialSymbol
     *            TRUE если необходимо использовать
     */
    public void setUseSpecialSymbol(boolean useSpecialSymbol) {
        this.useSpecialSymbol = useSpecialSymbol;
    }

    /**
     * Получить значение атрибута
     * "Блокировать Имена собственные(имена людей) в пароле"
     * 
     * @return TRUE если необходимо блокировать
     */
    public boolean getBanNames() {
        return banNames;
    }

    /**
     * Установить значение атрибута
     * "Блокировать Имена собственные(имена людей) в пароле"
     * 
     * @param banNames
     *            TRUE если необходимо блокировать
     */
    public void setBanNames(boolean banNames) {
        this.banNames = banNames;
    }

    /**
     * Получить значение атрибута
     * "Блокировать Имена собственные(фамилии людей) в пароле"
     * 
     * @return TRUE если необходимо блокировать
     */
    public boolean getBanFamilies() {
        return banFamilies;
    }

    /**
     * Установить значение атрибута
     * "Блокировать Имена собственные(фамилии людей) в пароле"
     * 
     * @param banFamilies
     *            TRUE если необходимо блокировать
     */
    public void setBanFamilies(boolean banFamilies) {
        this.banFamilies = banFamilies;
    }

    /**
     * Получить значение атрибута "Блокировать телефоны в пароле"
     * 
     * @return TRUE если необходимо блокировать
     */
    public boolean getBanPhone() {
        return banPhone;
    }

    /**
     * Установить значение атрибута "Блокировать телефоны в пароле"
     * 
     * @param banPhone
     *            TRUE если необходимо блокировать
     */
    public void setBanPhone(boolean banPhone) {
        this.banPhone = banPhone;
    }

    /**
     * Получить значение атрибута "Блокировать словарные слова в пароле"
     * 
     * @return TRUE если необходимо блокировать
     */
    public boolean getBanWord() {
        return banWord;
    }

    /**
     * Установить значение атрибута "Блокировать словарные слова в пароле"
     * 
     * @param banWord
     *            TRUE если необходимо блокировать
     */
    public void setBanWord(boolean banWord) {
        this.banWord = banWord;
    }
    
    /**
     * Получить значение атрибута "Блокировать слова на клавиатуре в пароле"
     * 
     * @return TRUE если необходимо блокировать
     */
    public boolean isBanKeyboard() {
		return banKeyboard;
	}

    /**
     * Установить значение атрибута "Блокировать слова на клавиатуре в пароле"
     * 
     * @param banWord
     *            TRUE если необходимо блокировать
     */
	public void setBanKeyboard(boolean banKeyboard) {
		this.banKeyboard = banKeyboard;
	}

    /**
     * Получить максимальный период в течении которого действует пароль
     * 
     * @return период (в миллисекундах)
     */
    public long getMaxPeriodPassword() {
        return maxPeriodPD;
    }

    /**
     * Установить максимальный период в течении которого действует пароль
     * 
     * @param maxPeriodPD
     *            период - в миллисекундах
     */
    public void setMaxPeriodPassword(long maxPeriodPD) {
        this.maxPeriodPD = maxPeriodPD;
    }

    /**
     * Получить минимальный период в течении которого действует пароль
     * 
     * @return период - в миллисекундах
     */
    public long getMinPeriodPassword() {
        return minPeriodPD;
    }

    /**
     * Установить минимальный период в течении которого действует пароль
     * 
     * @param minPeriodPD
     *            период - в миллисекундах
     */
    public void setMinPeriodPassword(long minPeriodPD) {
        this.minPeriodPD = minPeriodPD;
    }

    /**
     * Установить количество неуспешных авторизаций пользователя при достижении
     * которого учётная запись блокируется
     * 
     * @return количество авторизаций
     */
    public long getNumberFailedLogin() {
        return numberFailedLogin;
    }

    /**
     * Установить количество неуспешных авторизаций пользователя при достижении
     * которого учётная запись блокируется
     * 
     * @param numberFailedLogin
     *            количество авторизаций
     */
    public void setNumberFailedLogin(long numberFailedLogin) {
        this.numberFailedLogin = numberFailedLogin;
    }

    /**
     * Получить время блокировки учётной записи пользователя
     * 
     * @return время в милисекундах
     */
    public long getTimeLock() {
        return timeLock;
    }

    /**
     * Установить время блокировки учётной записи пользователя
     * 
     * @param timeLock
     *            время в милисекундах
     */
    public void setTimeLock(long timeLock) {
        this.timeLock = timeLock;
    }

    /**
     * Получить значение атрибута
     * "Блокировать использование логина пользоватля в пароле"
     * 
     * @return
     */
    public boolean getBanLoginInPassword() {
        return banLoginInPD;
    }

    /**
     * Установить значение атрибута
     * "Блокировать использование логина пользоватля в пароле"
     * 
     * @param banLoginInPD
     */
    public void setBanLoginInPassword(boolean banLoginInPD) {
        this.banLoginInPD = banLoginInPD;
    }

    /**
     * @return the maxLengthPass
     */
    public long getMaxLengthPass() {
        return maxLengthPass;
    }

    /**
     * @param maxLengthPass the maxLengthPass to set
     */
    public void setMaxLengthPass(long maxLengthPass) {
        this.maxLengthPass = maxLengthPass;
    }

    /**
     * @return the maxLengthLogin
     */
    public long getMaxLengthLogin() {
        return maxLengthLogin;
    }

    /**
     * @param maxLengthLogin the maxLengthLogin to set
     */
    public void setMaxLengthLogin(long maxLengthLogin) {
        this.maxLengthLogin = maxLengthLogin;
    }

    /**
     * @return the maxPeriodFirstPass
     */
    public long getMaxPeriodFirstPass() {
        return maxPeriodFirstPass;
    }

    /**
     * @param maxPeriodFirstPass the maxPeriodFirstPass to set
     */
    public void setMaxPeriodFirstPass(long maxPeriodFirstPass) {
        this.maxPeriodFirstPass = maxPeriodFirstPass;
    }
    
    /**
     * @return the changeFirstPass
     */
    public boolean isChangeFirstPass() {
        return changeFirstPass;
    }

    /**
     * @param changeFirstPass the changeFirstPass to set
     */
    public void setChangeFirstPass(boolean changeFirstPass) {
        this.changeFirstPass = changeFirstPass;
    }

    /**
     * @return the banRepeatChar
     */
    public boolean isBanRepeatChar() {
        return banRepeatChar;
    }

    /**
     * @param banRepeatChar the banRepeatChar to set
     */
    public void setBanRepeatChar(boolean banRepeatChar) {
        this.banRepeatChar = banRepeatChar;
    }
    
    /**
     * @return the banRepAnyWhereMoreTwoChar
     */
    public boolean isBanRepAnyWhereMoreTwoChar() {
		return banRepAnyWhereMoreTwoChar;
	}

    /**
     * @param banRepAnyWhereMoreTwoChar the banRepAnyWhereMoreTwoChar to set
     */
	public void setBanRepAnyWhereMoreTwoChar(boolean banRepAnyWhereMoreTwoChar) {
		this.banRepAnyWhereMoreTwoChar = banRepAnyWhereMoreTwoChar;
	}
	
    public boolean isActivateLiabilitySign() {
        return activateLiabilitySign;
    }
    
    public void setActivateLiabilitySign(boolean activateLiabilitySign) {
        this.activateLiabilitySign = activateLiabilitySign;
    }

    public long getLiabilitySignPeriod() {
        return liabilitySignPeriod;
    }
    
    public void setLiabilitySignPeriod(long liabilitySignPeriod) {
        this.liabilitySignPeriod = liabilitySignPeriod;
    }

    public boolean isActivateECPExpiryNotif() {
		return activateECPExpiryNotif;
	}

	public void setActivateECPExpiryNotif(boolean activateECPExpiryNotif) {
		this.activateECPExpiryNotif = activateECPExpiryNotif;
	}

	public long getECPExpiryNotifPeriod() {
		return ecpExpiryNotifPeriod;
	}

	public void setECPExpiryNotifPeriod(long ecpExpiryNotifPeriod) {
		this.ecpExpiryNotifPeriod = ecpExpiryNotifPeriod;
	}

	public boolean isActivateTempRegNotif() {
		return activateTempRegNotif;
	}

	public void setActivateTempRegNotif(boolean activateTempRegNotif) {
		this.activateTempRegNotif = activateTempRegNotif;
	}

	public long getTempRegNotifPeriod() {
		return tempRegNotifPeriod;
	}

	public void setTempRegNotifPeriod(long tempRegNotifPeriod) {
		this.tempRegNotifPeriod = tempRegNotifPeriod;
	}
	
	public boolean isCheckClientIp() {
		return checkClientIp;
	}

	public void setCheckClientIp(boolean checkClientIp) {
		this.checkClientIp = checkClientIp;
	}
	
	public boolean isUseECP() {
		return useECP;
	}

	public void setUseECP(boolean useECP) {
		this.useECP = useECP;
	}
	
	public boolean isBanUseOwnIdentificationData() {
		return banUseOwnIdentificationData;
	}

	public void setBanUseOwnIdentificationData(boolean banUseOwnIdentificationData) {
		this.banUseOwnIdentificationData = banUseOwnIdentificationData;
	}

	private static synchronized List<String> readNames() {
		if (dictNames == null)
			dictNames = readTextFromJar("dictNames.txt");
		return dictNames;
	}

	private static synchronized List<String> readSurnames() {
		if (dictSurnames == null)
			dictSurnames = readTextFromJar("dictSurnames.txt");
		return dictSurnames;
	}
	
	private static synchronized List<String> readWords() {
		if (dictWords == null)
			dictWords = readTextFromJar("dictWords.txt");
		return dictWords;
	}
	
	private static synchronized List<String> readKeyboards() {
		if (dictKeyboards == null)
			dictKeyboards = readTextFromJar("dictKeyboards.txt");
		return dictKeyboards;
	}
}
