package kz.tamur.guidesigner.users;

import static com.cifs.or2.util.CursorToolkit.startWaitCursor;
import static com.cifs.or2.util.CursorToolkit.stopWaitCursor;
import static kz.tamur.comps.Constants.DONT_CARE;
import static kz.tamur.comps.Constants.NOT_SELECTED;
import static kz.tamur.comps.Constants.SELECTED;

import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JOptionPane;

import kz.tamur.or3.client.props.CheckProperty;
import kz.tamur.or3.client.props.FolderProperty;
import kz.tamur.or3.client.props.Inspectable;
import kz.tamur.or3.client.props.KrnObjectProperty;
import kz.tamur.or3.client.props.PasswordProperty;
import kz.tamur.or3.client.props.Property;
import kz.tamur.or3.client.props.StringProperty;
import kz.tamur.or3.client.props.TreeProperty;
import kz.tamur.or3.client.props.TristateCheckProperty;
import kz.tamur.or3.client.props.UiOrJumpProperty;
import kz.tamur.or3.client.props.XmlProperty;

import org.jdom.Element;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.util.KrnObjectItem;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;

public class UserNodeItem implements Inspectable {
	private Kernel krn = Kernel.instance();
    private static Property proot;
    private Object item;
    private UserPanel owner;
    // статус пользователя до изменения
    private boolean oldStatusAdmin;

    // текстовые константы
    private final String MAX_VALID_PERIOD = "Рекомендуемый срок действия пароля (дней)";
    private final String MIN_LOGIN_LENGTH = "Мин. длина имени пользователя";
    private final String MIN_PASSWORD_LENGTH = "Мин. длина пароля";
    private final String MIN_PASSWORD_LENGTH_ADMIN = "Мин. длина пароля для админ.";
    private final String NUMBER_PASSWORD_DUBLICATE = "Кол-во не дублируемых предыдущих паролей";
    private final String NUMBER_PASSWORD_DUBLICATE_ADMIN = "Кол-во не дублируемых предыдущих паролей для админ.";
    private final String USE_NUMBERS = "Сложность пароля: Использовать цифры";
    private final String USE_NOTALLNUMBERS = "Сложность пароля: Не должны явно преобладать цифры";
    private final String USE_SYMBOLS = "Сложность пароля: Использовать буквы";
    private final String USE_REGISTER_SYMBOLS = "Сложность пароля: Использовать различный регистр";
    private final String USE_SPECIAL_SYMBOL = "Сложность пароля: Использовать спец. символы";
    private final String BAN_NAMES = "Сложность пароля: Запрет использования имён";
    private final String BAN_FAMILIES = "Сложность пароля: Запрет использования фамилий";
    private final String BAN_PHONE = "Сложность пароля: Запрет использования телефонных номеров";
    private final String BAN_WORD = "Сложность пароля: Запрет использования словарных слов";
    private final String BAN_KEYBOARD = "Сложность пароля: Запрет использования клавиатурных выражений";
    private final String MAX_PERIOD_PASSWORD = "Макс. срок действия пароля (дней)";
    private final String MIN_PERIOD_PASSWORD = "Мин. срок действия пароля (дней)";
    private final String NUMBER_FAILED_LOGIN = "Кол-во неуспешных авторизаций для блокировки уч. записи";
    private final String TIME_LOCK = "Время блокировки уч. записи (мин)";
    private final String BAN_LOGIN_IN_PASSWORD = "Сложность пароля: Запрет использования логина в пароле";

    private final String MAX_LENGTH_PASS = "Макс. длина пароля";
    private final String MAX_LENGTH_LOGIN = "Макс. длина имени пользователя";
    private final String CHANGE_FIRST_PASS = "Обязательная смена пароля при первой авторизации";
    private final String MAX_PERIOD_FIRST_PASS = "Макс. срок действия ПЕРВОГО пароля (дней)";
    private final String BAN_REPEAT_CHAR = "Сложность пароля: Запрет повтора первых трёх символов";
    private final String BAN_REPEAT_ANYWHERE_MORE_TWO_CHAR = "Сложность пароля: Запрет повтора в любом месте из более двух символов";
    
    public final String ACTIVATE_LIABILITY_SIGN = "Активировать подписание обязательства о неразглашении";
    public final String LIABILITY_SIGN_PERIOD = "Срок действия подписи обязательства о неразглашении (дней)";
    public final String ACTIVATE_ECP_EXPIRY_NOTIF = "Активировать оповещение об истечении срока действия ЭЦП";
    public final String ECP_EXPIRY_NOTIF_PERIOD = "Период оповещения об истечении срока действия ЭЦП";
    public final String ACTIVATE_TEMP_REG_NOTIF = "Активировать оповещениe об истечении срока временной регистрации";
    public final String TEMP_REG_NOTIF_PERIOD = "Период оповещения об истечении срока временной регистрации";
    public final String CHECK_CLIENT_IP = "Проверять ip-адрес клиента";
    public final String USE_LOGIN_ECP = "Авторизация с ЭЦП";
    public final String BAN_USE_OWN_IDENTIFICATION_DATA = "Запрет использования собственных идентификационных данных в пароле";
    
    public UserNodeItem(Object item, UserPanel owner) {
        this.item = item;
        this.owner = owner;
    }

    public Property getProperties() {
        proot = new FolderProperty(null, null, "Элементы");
        if (item != null) {
            if (item instanceof UserNode) {
                if (item instanceof PolicyNode) {
                    new StringProperty(proot, MIN_LOGIN_LENGTH, MIN_LOGIN_LENGTH);
                    new StringProperty(proot, MAX_LENGTH_LOGIN, MAX_LENGTH_LOGIN);
                    new StringProperty(proot, MIN_PASSWORD_LENGTH, MIN_PASSWORD_LENGTH);
                    new StringProperty(proot, MIN_PASSWORD_LENGTH_ADMIN, MIN_PASSWORD_LENGTH_ADMIN);
                    new StringProperty(proot, MAX_LENGTH_PASS, MAX_LENGTH_PASS);
                    new StringProperty(proot, NUMBER_PASSWORD_DUBLICATE, NUMBER_PASSWORD_DUBLICATE);
                    new StringProperty(proot, NUMBER_PASSWORD_DUBLICATE_ADMIN, NUMBER_PASSWORD_DUBLICATE_ADMIN);
                    new StringProperty(proot, MIN_PERIOD_PASSWORD, MIN_PERIOD_PASSWORD);
                    new StringProperty(proot, MAX_VALID_PERIOD, MAX_VALID_PERIOD);
                    new StringProperty(proot, MAX_PERIOD_PASSWORD, MAX_PERIOD_PASSWORD);
                    new StringProperty(proot, MAX_PERIOD_FIRST_PASS, MAX_PERIOD_FIRST_PASS);
                    new CheckProperty(proot, CHANGE_FIRST_PASS, CHANGE_FIRST_PASS);
                    new StringProperty(proot, NUMBER_FAILED_LOGIN, NUMBER_FAILED_LOGIN);
                    new StringProperty(proot, TIME_LOCK, TIME_LOCK);
                    new CheckProperty(proot, USE_NUMBERS, USE_NUMBERS);
                    new CheckProperty(proot, USE_NOTALLNUMBERS, USE_NOTALLNUMBERS);
                    new CheckProperty(proot, USE_SYMBOLS, USE_SYMBOLS);
                    new CheckProperty(proot, USE_REGISTER_SYMBOLS, USE_REGISTER_SYMBOLS);
                    new CheckProperty(proot, USE_SPECIAL_SYMBOL, USE_SPECIAL_SYMBOL);
                    new CheckProperty(proot, BAN_NAMES, BAN_NAMES);
                    new CheckProperty(proot, BAN_FAMILIES, BAN_FAMILIES);
                    new CheckProperty(proot, BAN_PHONE, BAN_PHONE);
                    new CheckProperty(proot, BAN_WORD, BAN_WORD);
                    new CheckProperty(proot, BAN_KEYBOARD, BAN_KEYBOARD);
                    new CheckProperty(proot, BAN_LOGIN_IN_PASSWORD, BAN_LOGIN_IN_PASSWORD);
                    new CheckProperty(proot, BAN_REPEAT_CHAR, BAN_REPEAT_CHAR);
                    new CheckProperty(proot, BAN_REPEAT_ANYWHERE_MORE_TWO_CHAR, BAN_REPEAT_ANYWHERE_MORE_TWO_CHAR);
                    new CheckProperty(proot, ACTIVATE_LIABILITY_SIGN, ACTIVATE_LIABILITY_SIGN);
                    new StringProperty(proot, LIABILITY_SIGN_PERIOD, LIABILITY_SIGN_PERIOD);
                    new CheckProperty(proot, ACTIVATE_ECP_EXPIRY_NOTIF, ACTIVATE_ECP_EXPIRY_NOTIF);
                    new StringProperty(proot, ECP_EXPIRY_NOTIF_PERIOD, ECP_EXPIRY_NOTIF_PERIOD);
                    new CheckProperty(proot, ACTIVATE_TEMP_REG_NOTIF, ACTIVATE_TEMP_REG_NOTIF);
                    new StringProperty(proot, TEMP_REG_NOTIF_PERIOD, TEMP_REG_NOTIF_PERIOD);
                    new CheckProperty(proot, CHECK_CLIENT_IP, CHECK_CLIENT_IP);
                    if (krn.isRNDB() || krn.hasUseECP()) {
                        new CheckProperty(proot, USE_LOGIN_ECP, USE_LOGIN_ECP);
                    }
                    if (! krn.isULDB() && !krn.isRNDB()) {
                    	new CheckProperty(proot, BAN_USE_OWN_IDENTIFICATION_DATA, BAN_USE_OWN_IDENTIFICATION_DATA);
                    }
                } else if (((UserNode) item).isLeaf()) {
                    new StringProperty(proot, "name", "Логин");
                    new PasswordProperty(proot, "password", "Пароль");
                    new StringProperty(proot, "sign", "Подпись");
                    new StringProperty(proot, "signKz", "ПодписьКаз");
                    new StringProperty(proot, "doljnost", "Должность");
                    new TreeProperty(proot, "base", "База данных", "Структура баз");
                    new KrnObjectProperty(proot, "languageData", "Язык данных", "Language", "name");
                    new KrnObjectProperty(proot, "languageIfs", "Язык интерфейса", "Language", "name");
                    new UiOrJumpProperty(proot, "interface", "Интерфейс");
                    new CheckProperty(proot, "admin", "Администратор");
                    new CheckProperty(proot, "blocked", "Заблокирован");
                    new CheckProperty(proot, "multi", "Множественный вход");
                    new StringProperty(proot, "email", "E-mail");
                    new StringProperty(proot, "ip_address", "IP-address PC");
                    new StringProperty(proot, "iin", "ИИН");
                    new CheckProperty(proot, "onlyECP", "Вход только по ЭЦП");
                    new CheckProperty(proot, "isMonitor", "Монитор задач(толстый клиент)");
                    new CheckProperty(proot, "isToolBar", "Отображать панель инструментов в WEB");
                } else {
                    new StringProperty(proot, "name", "Имя группы");
                    new KrnObjectProperty(proot, "hyperMenu", "Пункты гиперменю", "HyperTree", "title");
                    new CheckProperty(proot, "editor", "Редактор НСИ");
                    new KrnObjectProperty(proot, "helps", "Доступная помощь", "Note", "title");
                    new TreeProperty(proot, "process", "Процесс", "ProcessDef");
                    new XmlProperty(proot, "or3rights", "Права OR3");
                    new TristateCheckProperty(proot, "isMonitorGroup", "Монитор задач для группы(толстый клиент)");
                    new TristateCheckProperty(proot, "isToolBarGroup", "Отображать панель инструментов в WEB для группы");
                }
            }
        }
        return proot;
    }

    public Object getValue(Property prop) {
        Object res = "";
        if (item != null && !(prop instanceof FolderProperty)) {
            String id = prop.getId();
            if (item instanceof UserNode) {
                if (item instanceof PolicyNode) {
                    if (MAX_VALID_PERIOD.equals(id))
                        res = "" + ((PolicyNode) item).getPolicyWrapper().getMaxValidPeriod();
                    else if (MIN_LOGIN_LENGTH.equals(id))
                        res = "" + ((PolicyNode) item).getPolicyWrapper().getMinLoginLength();
                    else if (MIN_PASSWORD_LENGTH.equals(id))
                        res = "" + ((PolicyNode) item).getPolicyWrapper().getMinPasswordLength();
                    else if (MIN_PASSWORD_LENGTH_ADMIN.equals(id))
                        res = "" + ((PolicyNode) item).getPolicyWrapper().getMinPasswordLengthAdmin();
                    else if (NUMBER_PASSWORD_DUBLICATE.equals(id))
                        res = "" + ((PolicyNode) item).getPolicyWrapper().getNumPassDubl();
                    else if (NUMBER_PASSWORD_DUBLICATE_ADMIN.equals(id))
                        res = "" + ((PolicyNode) item).getPolicyWrapper().getNumPassDublAdmin();
                    else if (USE_NUMBERS.equals(id))
                        res = ((PolicyNode) item).getPolicyWrapper().getUseNumbers();
                    else if (USE_NOTALLNUMBERS.equals(id))
                    	res = ((PolicyNode) item).getPolicyWrapper().getUseNotAllNumbers();
                    else if (USE_SYMBOLS.equals(id))
                        res = ((PolicyNode) item).getPolicyWrapper().getUseSymbols();
                    else if (USE_REGISTER_SYMBOLS.equals(id))
                        res = ((PolicyNode) item).getPolicyWrapper().getUseRegisterSymbols();
                    else if (USE_SPECIAL_SYMBOL.equals(id))
                        res = ((PolicyNode) item).getPolicyWrapper().getUseSpecialSymbol();
                    else if (BAN_NAMES.equals(id))
                        res = ((PolicyNode) item).getPolicyWrapper().getBanNames();
                    else if (BAN_FAMILIES.equals(id))
                        res = ((PolicyNode) item).getPolicyWrapper().getBanFamilies();
                    else if (BAN_PHONE.equals(id))
                        res = ((PolicyNode) item).getPolicyWrapper().getBanPhone();
                    else if (BAN_WORD.equals(id))
                        res = ((PolicyNode) item).getPolicyWrapper().getBanWord();
                    else if (BAN_KEYBOARD.equals(id))
                        res = ((PolicyNode) item).getPolicyWrapper().isBanKeyboard();
                    else if (MAX_PERIOD_PASSWORD.equals(id))
                        res = "" + ((PolicyNode) item).getPolicyWrapper().getMaxPeriodPassword();
                    else if (MIN_PERIOD_PASSWORD.equals(id))
                        res = "" + ((PolicyNode) item).getPolicyWrapper().getMinPeriodPassword();
                    else if (NUMBER_FAILED_LOGIN.equals(id))
                        res = "" + ((PolicyNode) item).getPolicyWrapper().getNumberFailedLogin();
                    else if (TIME_LOCK.equals(id))
                        res = "" + ((PolicyNode) item).getPolicyWrapper().getTimeLock();
                    else if (BAN_LOGIN_IN_PASSWORD.equals(id))
                        res = ((PolicyNode) item).getPolicyWrapper().getBanLoginInPassword();
                    else if (MAX_LENGTH_PASS.equals(id))
                        res =  "" + ((PolicyNode) item).getPolicyWrapper().getMaxLengthPass();
                    else if (MAX_LENGTH_LOGIN.equals(id))
                        res =  "" + ((PolicyNode) item).getPolicyWrapper().getMaxLengthLogin();
                    else if (CHANGE_FIRST_PASS.equals(id))
                        res = ((PolicyNode) item).getPolicyWrapper().isChangeFirstPass();
                    else if (MAX_PERIOD_FIRST_PASS.equals(id))
                            res =  "" + ((PolicyNode) item).getPolicyWrapper().getMaxPeriodFirstPass();
                    else if (BAN_REPEAT_CHAR.equals(id))
                        res = ((PolicyNode) item).getPolicyWrapper().isBanRepeatChar();
                    else if (BAN_REPEAT_ANYWHERE_MORE_TWO_CHAR.equals(id))
                    	res = ((PolicyNode) item).getPolicyWrapper().isBanRepAnyWhereMoreTwoChar();
                    else if (ACTIVATE_LIABILITY_SIGN.equals(prop.getId()))
                        res = ((PolicyNode) item).getPolicyWrapper().isActivateLiabilitySign();
                    else if (LIABILITY_SIGN_PERIOD.equals(prop.getId()))
                        res = "" + ((PolicyNode) item).getPolicyWrapper().getLiabilitySignPeriod();
                    else if (ACTIVATE_ECP_EXPIRY_NOTIF.equals(prop.getId()))
                        res = ((PolicyNode) item).getPolicyWrapper().isActivateECPExpiryNotif();
                    else if (ECP_EXPIRY_NOTIF_PERIOD.equals(prop.getId()))
                        res = "" + ((PolicyNode) item).getPolicyWrapper().getECPExpiryNotifPeriod();
                    else if (ACTIVATE_TEMP_REG_NOTIF.equals(prop.getId()))
                        res = ((PolicyNode) item).getPolicyWrapper().isActivateTempRegNotif();
                    else if (TEMP_REG_NOTIF_PERIOD.equals(prop.getId()))
                        res = "" + ((PolicyNode) item).getPolicyWrapper().getTempRegNotifPeriod();
                    else if (CHECK_CLIENT_IP.equals(prop.getId()))
                        res = ((PolicyNode) item).getPolicyWrapper().isCheckClientIp();
                    else if (USE_LOGIN_ECP.equals(prop.getId()))
                        res = ((PolicyNode) item).getPolicyWrapper().isUseECP();
                    else if (BAN_USE_OWN_IDENTIFICATION_DATA.equals(prop.getId()))
                        res = ((PolicyNode) item).getPolicyWrapper().isBanUseOwnIdentificationData();
                } else if (((UserNode) item).isLeaf()) {
                    if ("name".equals(id))
                        res = ((UserNode) item).getName();
                    else if ("password".equals(id))
                        res = "*******";
                    else if ("sign".equals(id))
                        res = ((UserNode) item).getSign();
                    else if ("signKz".equals(id))
                        res = ((UserNode) item).getSignKz();
                    else if ("doljnost".equals(id))
                        res = ((UserNode) item).getDoljnost();
                    else if ("email".equals(id))
                        res = ((UserNode) item).getEmail();
                    else if ("ip_address".equals(id))
                        res = ((UserNode) item).getIpAddress();
                    else if ("base".equals(id)) {
                        res = ((UserNode) item).getBaseStructureObj();
                    } else if ("languageData".equals(id)) {
                        res = ((UserNode) item).getDataLangObj();
                    } else if ("languageIfs".equals(id)) {
                        res = ((UserNode) item).getIfcLangObj();
                    } else if ("interface".equals(id)) {
                        res = ((UserNode) item).getIfcObject();
                        if (res != null) {
                            res = new KrnObjectItem((KrnObject) res, ((UserNode) item).getItemTitle((KrnObject) res));
                        }
                    } else if ("admin".equals(id)) {
                        res = ((UserNode) item).isAdmin();
                        oldStatusAdmin = ((UserNode) item).isAdmin();
                    } else if ("blocked".equals(id))
                        res = ((UserNode) item).isBlocked();
                    else if ("multi".equals(id))
                        res = ((UserNode) item).isMulti();
                    else if ("iin".equals(id))
                        res = ((UserNode) item).getIIN();
                    else if ("onlyECP".equals(id)) {
                        res = ((UserNode) item).isOnlyECP();
                    } else if ("isMonitor".equals(id)) {
                        res = ((UserNode) item).isMonitor();
                    } else if ("isToolBar".equals(id)) {
                        res = ((UserNode) item).isToolBar();
                    }
                } else {
                    if ("name".equals(id))
                        res = ((UserNode) item).getName();
                    else if ("hyperMenu".equals(id)) {
                        res = ((UserNode) item).getHypers();
                    } else if ("editor".equals(id))
                        res = ((UserNode) item).isEditor();
                    else if ("helps".equals(id)) {
                        res = ((UserNode) item).getHelp();
                    } else if ("process".equals(id)) {
                        res = ((UserNode) item).getProcess();
                    } else if ("or3rights".equals(id)) {
                        res = ((UserNode) item).getOr3Rights();
                    } else if ("isMonitorGroup".equals(id)) {
                        res = ((UserNode) item).getMonitor();
                    } else if ("isToolBarGroup".equals(id)) {
                        res = ((UserNode) item).getToolBar();
                    }
                }

                if (res instanceof KrnObject) {
                    Vector<KrnObjectItem> objs = new Vector<KrnObjectItem>();
                    objs.add(new KrnObjectItem((KrnObject) res, ((UserNode) item).getItemTitle((KrnObject) res)));
                    res = objs;
                } else if (res instanceof KrnObject[]) {
                    Vector<KrnObjectItem> objs = new Vector<KrnObjectItem>();
                    for (KrnObject obj : (KrnObject[]) res) {
                        objs.add(new KrnObjectItem(obj, ((UserNode) item).getItemTitle(obj)));
                    }
                    res = objs;
                }
            }
        }
        return res;
    }

	public void setValue(Property prop, Object value) {
		setValue(prop, value, null);
	}

	public void setValue(Property prop, Object value, Object oldValue) {
        if (item != null && item instanceof UserNode && !(prop instanceof FolderProperty)) {
            String id = prop.getId();
            if (item instanceof UserNode) {
                if (item instanceof PolicyNode) {
                	((PolicyNode) item).propertyChanged(id, oldValue, value);
                    if (MAX_VALID_PERIOD.equals(id)) {
                        ((PolicyNode) item).getPolicyWrapper().setMaxValidPeriod(Long.valueOf((String) value));
                    } else if (MIN_LOGIN_LENGTH.equals(id)) {
                        ((PolicyNode) item).getPolicyWrapper().setMinLoginLength(Long.valueOf((String) value));
                    } else if (MIN_PASSWORD_LENGTH.equals(id)) {
                        ((PolicyNode) item).getPolicyWrapper().setMinPasswordLength(Long.valueOf((String) value));
                    } else if (MIN_PASSWORD_LENGTH_ADMIN.equals(id)) {
                        ((PolicyNode) item).getPolicyWrapper().setMinPasswordLengthAdmin(Integer.valueOf((String) value));
                    } else if (NUMBER_PASSWORD_DUBLICATE.equals(id)) {
                        ((PolicyNode) item).getPolicyWrapper().setNumPassDubl(Integer.valueOf((String) value));
                    } else if (NUMBER_PASSWORD_DUBLICATE_ADMIN.equals(id)) {
                        ((PolicyNode) item).getPolicyWrapper().setNumPassDublAdmin(Integer.valueOf((String) value));
                    } else if (USE_NUMBERS.equals(id)) {
                        ((PolicyNode) item).getPolicyWrapper().setUseNumbers(PolicyNode.toBoolean(value));
                    } else if (USE_NOTALLNUMBERS.equals(id)) {
                    	((PolicyNode) item).getPolicyWrapper().setUseNotAllNumbers(PolicyNode.toBoolean(value));
                    } else if (USE_SYMBOLS.equals(id)) {
                        ((PolicyNode) item).getPolicyWrapper().setUseSymbols(PolicyNode.toBoolean(value));
                    } else if (USE_REGISTER_SYMBOLS.equals(id)) {
                        ((PolicyNode) item).getPolicyWrapper().setUseRegisterSymbols(PolicyNode.toBoolean(value));
                    } else if (USE_SPECIAL_SYMBOL.equals(id)) {
                        ((PolicyNode) item).getPolicyWrapper().setUseSpecialSymbol(PolicyNode.toBoolean(value));
                    } else if (BAN_NAMES.equals(id)) {
                        ((PolicyNode) item).getPolicyWrapper().setBanNames(PolicyNode.toBoolean(value));
                    } else if (BAN_FAMILIES.equals(id)) {
                        ((PolicyNode) item).getPolicyWrapper().setBanFamilies(PolicyNode.toBoolean(value));
                    } else if (BAN_PHONE.equals(id)) {
                        ((PolicyNode) item).getPolicyWrapper().setBanPhone(PolicyNode.toBoolean(value));
                    } else if (BAN_WORD.equals(id)) {
                        ((PolicyNode) item).getPolicyWrapper().setBanWord(PolicyNode.toBoolean(value));
                    } else if (BAN_KEYBOARD.equals(id)) {
                        ((PolicyNode) item).getPolicyWrapper().setBanKeyboard(PolicyNode.toBoolean(value));
                    } else if (MAX_PERIOD_PASSWORD.equals(id)) {
                        ((PolicyNode) item).getPolicyWrapper().setMaxPeriodPassword(Long.valueOf((String) value));
                    } else if (MIN_PERIOD_PASSWORD.equals(id)) {
                        ((PolicyNode) item).getPolicyWrapper().setMinPeriodPassword(Long.valueOf((String) value));
                    } else if (NUMBER_FAILED_LOGIN.equals(id)) {
                        ((PolicyNode) item).getPolicyWrapper().setNumberFailedLogin(Long.valueOf((String) value));
                    } else if (TIME_LOCK.equals(id)) {
                        ((PolicyNode) item).getPolicyWrapper().setTimeLock(Long.valueOf((String) value));
                    } else if (BAN_LOGIN_IN_PASSWORD.equals(id)) {
                        ((PolicyNode) item).getPolicyWrapper().setBanLoginInPassword(PolicyNode.toBoolean(value));
                    } else if (MAX_LENGTH_PASS.equals(id)) {
                        ((PolicyNode) item).getPolicyWrapper().setMaxLengthPass(Long.valueOf((String) value));
                    } else if (MAX_LENGTH_LOGIN.equals(id)) {
                        ((PolicyNode) item).getPolicyWrapper().setMaxLengthLogin(Long.valueOf((String) value));
                    } else if (CHANGE_FIRST_PASS.equals(id)) {
                        ((PolicyNode) item).getPolicyWrapper().setChangeFirstPass(PolicyNode.toBoolean(value));
                    } else if (MAX_PERIOD_FIRST_PASS.equals(id)) {
                        ((PolicyNode) item).getPolicyWrapper().setMaxPeriodFirstPass(Long.valueOf((String) value));
                    } else if (BAN_REPEAT_CHAR.equals(id)) {
                        ((PolicyNode) item).getPolicyWrapper().setBanRepeatChar(PolicyNode.toBoolean(value));
                    } else if (BAN_REPEAT_ANYWHERE_MORE_TWO_CHAR.equals(id)) {
                        ((PolicyNode) item).getPolicyWrapper().setBanRepAnyWhereMoreTwoChar(PolicyNode.toBoolean(value));
                    } else if (ACTIVATE_LIABILITY_SIGN.equals(id)) {
                        ((PolicyNode) item).getPolicyWrapper().setActivateLiabilitySign(PolicyNode.toBoolean(value));
                    } else if (LIABILITY_SIGN_PERIOD.equals(id)) {
                        ((PolicyNode) item).getPolicyWrapper().setLiabilitySignPeriod(Long.valueOf((String) value));
                    } else if (ACTIVATE_ECP_EXPIRY_NOTIF.equals(id)) {
                        ((PolicyNode) item).getPolicyWrapper().setActivateECPExpiryNotif(PolicyNode.toBoolean(value));
                    } else if (ECP_EXPIRY_NOTIF_PERIOD.equals(id)) {
                        ((PolicyNode) item).getPolicyWrapper().setECPExpiryNotifPeriod(Long.valueOf((String) value));
                    } else if (ACTIVATE_TEMP_REG_NOTIF.equals(id)) {
                        ((PolicyNode) item).getPolicyWrapper().setActivateTempRegNotif(PolicyNode.toBoolean(value));
                    } else if (TEMP_REG_NOTIF_PERIOD.equals(id)) {
                        ((PolicyNode) item).getPolicyWrapper().setTempRegNotifPeriod(Long.valueOf((String) value));
                    } else if (CHECK_CLIENT_IP.equals(id)) {
                        ((PolicyNode) item).getPolicyWrapper().setCheckClientIp(PolicyNode.toBoolean(value));
                    } else if (USE_LOGIN_ECP.equals(prop.getId())) {
                        ((PolicyNode) item).getPolicyWrapper().setUseECP(PolicyNode.toBoolean(value));
                    } else if (BAN_USE_OWN_IDENTIFICATION_DATA.equals(prop.getId())) {
                        ((PolicyNode) item).getPolicyWrapper().setBanUseOwnIdentificationData(PolicyNode.toBoolean(value));
                    }    
                } else if (((UserNode) item).isLeaf()) {
                    String rCheck = null;
                    if ("name".equals(id))
                        ((UserNode) item).setName((String) value);
                    else if ("password".equals(id)) {
                        // применения пароля
                        rCheck = ((UserNode) item).setPassword(String.valueOf((char[]) value));
                        if (rCheck != null) {
                            if (!rCheck.equals("NOT_CHANGED")) {
                                JOptionPane.showMessageDialog(owner, rCheck, "Сообщение", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            return;
                        }
                    } else if ("sign".equals(id))
                        ((UserNode) item).setSign((String) value);
                    else if ("signKz".equals(id))
                        ((UserNode) item).setSignKz((String) value);
                    else if ("doljnost".equals(id))
                        ((UserNode) item).setDoljnost((String) value);
                    else if ("email".equals(id))
                        ((UserNode) item).setEmail((String) value);
                    else if ("ip_address".equals(id))
                        ((UserNode) item).setIpAddress((String) value);
                    else if ("base".equals(id)) {
                        KrnObject obj = null;
                        if (value instanceof KrnObjectItem) {
                            obj = ((KrnObjectItem) value).obj;
                            ((UserNode) item).setItemTitle(obj, ((KrnObjectItem) value).title);
                        }
                        ((UserNode) item).setBaseStructureObj(obj);
                    } else if ("languageData".equals(id)) {
                        KrnObject obj = null;
                        if (value instanceof Vector && ((Vector) value).size() > 0) {
                            KrnObjectItem obji = (KrnObjectItem) ((Vector) value).get(0);
                            obj = obji.obj;
                            ((UserNode) item).setItemTitle(obj, obji.title);
                        }
                        ((UserNode) item).setDataLangObj(obj);
                    } else if ("languageIfs".equals(id)) {
                        KrnObject obj = null;
                        if (value instanceof Vector && ((Vector) value).size() > 0) {
                            KrnObjectItem obji = (KrnObjectItem) ((Vector) value).get(0);
                            obj = obji.obj;
                            ((UserNode) item).setItemTitle(obj, obji.title);
                        }
                        ((UserNode) item).setIfcLangObj(obj);
                    } else if ("interface".equals(id)) {
                        KrnObject obj = null;
                        if (value instanceof KrnObjectItem) {
                            obj = ((KrnObjectItem) value).obj;
                            ((UserNode) item).setItemTitle(obj, ((KrnObjectItem) value).title);
                        }
                        ((UserNode) item).setIfcObject(obj);
                    } else if ("admin".equals(id)) {
                        boolean isAdmin = value instanceof Boolean ? (Boolean) value : Boolean.FALSE;
                        ((UserNode) item).setAdmin(isAdmin);
                    } else if ("blocked".equals(id))
                        ((UserNode) item).setBlocked(value instanceof Boolean ? (Boolean) value : Boolean.FALSE);
                    else if ("multi".equals(id))
                        ((UserNode) item).setMulti(value instanceof Boolean ? (Boolean) value : Boolean.FALSE);
                    else if ("iin".equals(id))
                        ((UserNode) item).setIIN((String) value);
                    else if ("onlyECP".equals(id))
                        ((UserNode) item).setOnlyECP(value instanceof Boolean ? (Boolean) value : Boolean.FALSE);
                    else if ("isMonitor".equals(id)) {
                        /*
                         * для группы, к которой относится пользователь, необходимо установить аналогичное свойство, в зависимости от состояния всех пользователей и групп вложенных в группу
                         * рекурсивно !
                         */
                        startWaitCursor(owner);
                        int value_ = value instanceof Boolean ? ((Boolean) value ? SELECTED : NOT_SELECTED)
                                : (value instanceof Integer ? (Integer) value : SELECTED);
                        ((UserNode) item).setMonitor(value_);
                        setMonitorParent((UserNode) item, value_);
                        stopWaitCursor(owner);
                    } else if ("isToolBar".equals(id)) {
                        /*
                         * для группы, к которой относится пользователь, необходимо установить аналогичное свойство, в зависимости от состояния всех пользователей и групп вложенных в группу
                         * рекурсивно !
                         */
                        startWaitCursor(owner);
                        int value_ = value instanceof Boolean ? ((Boolean) value ? SELECTED : NOT_SELECTED)
                                : (value instanceof Integer ? (Integer) value : SELECTED);
                        ((UserNode) item).setToolBar(value_);
                        setToolBarParent((UserNode) item, value_);
                        stopWaitCursor(owner);
                    }
                } else {
                    if ("name".equals(id))
                        ((UserNode) item).setName((String) value);
                    else if ("hyperMenu".equals(id)) {
                        KrnObject[] objs = null;
                        if (value instanceof Vector) {
                            objs = new KrnObject[((Vector) value).size()];
                            int i = 0;
                            for (KrnObjectItem obj : (Vector<KrnObjectItem>) value) {
                                objs[i++] = obj.obj;
                                ((UserNode) item).setItemTitle(obj.obj, obj.title);
                            }
                        }
                        ((UserNode) item).setHypers(objs);
                    } else if ("editor".equals(id))
                        ((UserNode) item).setEditor(value instanceof Boolean ? (Boolean) value : Boolean.FALSE);
                    else if ("helps".equals(id)) {
                        KrnObject[] objs = null;
                        if (value instanceof Vector) {
                            objs = new KrnObject[((Vector) value).size()];
                            int i = 0;
                            for (KrnObjectItem obj : (Vector<KrnObjectItem>) value) {
                                objs[i++] = obj.obj;
                                ((UserNode) item).setItemTitle(obj.obj, obj.title);
                            }
                        }
                        ((UserNode) item).setHelp(objs);
                    } else if ("process".equals(id)) {
                        KrnObject obj = null;
                        if (value instanceof KrnObjectItem) {
                            obj = ((KrnObjectItem) value).obj;
                            ((UserNode) item).setItemTitle(obj, ((KrnObjectItem) value).title);
                        }
                        ((UserNode) item).setProcess(obj);
                    } else if ("or3rights".equals(id)) {
                        ((UserNode) item).setOr3Rights(value instanceof Element ? (Element) value : null);
                    } else if ("isMonitorGroup".equals(id)) {
                        int value_ = value instanceof Integer ? (Integer) value : SELECTED;
                        startWaitCursor(owner);
                        // рекурсивно обработать всех потомков ветки(как отдельных пользователей, так и группы)
                        setMonitorChildren((UserNode) item, value_);
                        /*
                         * для группы, к которой относится пользователь(или папка), необходимо установить аналогичное свойство, в зависимости от состояния всех пользователей и групп вложенных в группу
                         * рекурсивно !
                         */
                        setMonitorParent((UserNode) item, value_);
                        stopWaitCursor(owner);
                    } else if ("isToolBarGroup".equals(id)) {
                        int value_ = value instanceof Integer ? (Integer) value : SELECTED;
                        startWaitCursor(owner);
                        // рекурсивно обработать всех потомков ветки(как отдельных пользователей, так и группы)
                        setToolBarChildren((UserNode) item, value_);
                        /*
                         * для группы, к которой относится пользователь(или папка), необходимо установить аналогичное свойство, в зависимости от состояния всех пользователей и групп вложенных в группу рекурсивно !
                         */
                        setToolBarParent((UserNode) item, value_);
                        stopWaitCursor(owner);
                    }
                }
            }
            owner.setModified((UserNode) item);
        }
    }

    /**
     * Установка свойства "отображать монитор" для всех потомков узла
     * 
     * @param node
     *            корневой узел
     * @param value
     *            значение
     */
    public void setMonitorChildren(UserNode node, int value) {
        if (node != null) {
            // если значение изменилось
            if (node.getMonitor() != value) {
                // задать флаг монитора для узла
                node.setMonitor(value);
                // отметить узел как модифицированный
                owner.setModified(node);
            }
            // если узел - папка
            if (!node.isLeaf()) {
                // получить потомков
                Enumeration childNodes = node.children();
                while (childNodes.hasMoreElements()) {
                    // получить первого потомка
                    UserNode child = (UserNode) childNodes.nextElement();
                    // рекурсия
                    setMonitorChildren(child, value);
                }
            }
        }
    }

    /**
     * !
     * 
     * Метод не корректно отрабатывает когда пользователь сидит в нескольких папках(несколько потомков).
     * В результате этого у родителей пользователя(папок), некорректно проставляется атрибут "isMonitor"
     * На работу это не влияет. страдает только визуальная составляющая 
     * 
     * Решения пока нет.
     * 
     * Установка свойства "отображать монитор" для всех предков данного узла
     * 
     * @param node
     *            корневой узел, скоторого идёт поиск
     * @param value
     *            значение
     */
    public void setMonitorParent(UserNode node, int value) {
        if (node != null) {
            // получить родителя узла
            UserNode parent = (UserNode) node.getParent();
            if (parent != null) {
                // получить всех потомков
                Enumeration childNodes = parent.children();
                int state = -1;
                int stateNew = -1;
                while (childNodes.hasMoreElements()) {
                    UserNode child = (UserNode) childNodes.nextElement();
                    if (state == -1) {
                        state = child.getMonitor();
                    } else {
                        stateNew = node.getMonitor();
                        if (state != stateNew || stateNew == DONT_CARE) {
                            state = DONT_CARE;
                            break;
                        }
                    }
                }
                // если состояние изменилось
                if (parent.getMonitor() != state) {
                    parent.setMonitor(state);
                    owner.setModified(parent);
                    setMonitorParent(parent, value);
                }
            }
        }
    }

    /**
     * Установка свойства "отображать панель инструментов" для всех потомков узла
     * 
     * @param node
     *            корневой узел
     * @param value
     *            значение
     */
    public void setToolBarChildren(UserNode node, int value) {
        if (node != null) {
            // если значение изменилось
            if (node.getToolBar() != value) {
                // задать флаг монитора для узла
                node.setToolBar(value);
                // отметить узел как модифицированный
                owner.setModified(node);
            }
            // если узел - папка
            if (!node.isLeaf()) {
                // получить потомков
                Enumeration childNodes = node.children();
                while (childNodes.hasMoreElements()) {
                    // получить первого потомка
                    UserNode child = (UserNode) childNodes.nextElement();
                    // рекурсия
                    setToolBarChildren(child, value);
                }
            }
        }
    }

    /**
     * Установка свойства "отображать панель инструментов" для всех предков данного узла
     * 
     * @param node
     *            корневой узел, скоторого идёт поиск
     * @param value
     *            значение
     */
    public void setToolBarParent(UserNode node, int value) {
        if (node != null) {
            // получить родителя узла
            UserNode parent = (UserNode) node.getParent();
            if (parent != null) {
                // получить всех потомков
                Enumeration childNodes = parent.children();
                int state = -1;
                int stateNew = -1;
                while (childNodes.hasMoreElements()) {
                    UserNode child = (UserNode) childNodes.nextElement();
                    if (state == -1) {
                        state = child.getToolBar();
                    } else {
                        stateNew = node.getToolBar();
                        if (state != stateNew || stateNew == DONT_CARE) {
                            state = DONT_CARE;
                            break;
                        }
                    }
                }
                // если состояние изменилось
                if (parent.getToolBar() != state) {
                    parent.setToolBar(state);
                    owner.setModified(parent);
                    setToolBarParent(parent, value);
                }
            }
        }
    }
    
    public String getTitle() {
        return "";
    }

    public Property getNewProperties() {
        return null;
    }
}