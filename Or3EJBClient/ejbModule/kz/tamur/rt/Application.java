package kz.tamur.rt;

import com.cifs.or2.client.ClientCallback;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.PathWordChange;
import com.cifs.or2.client.User;
import com.cifs.or2.kernel.KrnException;
import kz.tamur.Or3Frame;
import kz.tamur.common.ErrorCodes;
import kz.tamur.comps.Constants;
import kz.tamur.comps.Mode;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.Splash;
import kz.tamur.util.AppState;
import kz.tamur.util.Funcs;
import kz.tamur.util.LangItem;
import kz.tamur.util.crypto.KalkanUtil;
import kz.tamur.or3.client.CachedKernel;
import kz.tamur.or3ee.server.session.SessionOpsOperations;
import kz.tamur.rt.login.LoginBox;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.io.*;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

import static kz.tamur.guidesigner.MessagesFactory.ERROR_MESSAGE;
import static kz.tamur.guidesigner.MessagesFactory.showMessageDialog;
import static  kz.tamur.rt.Utils.getImageIcon;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 13.04.2004
 * Time: 15:05:32
 */
public class Application {
    private static final Log clientLog = LogFactory.getLog("ClientLog");
    boolean packFrame = false;
    public int screen = -1;
    /** отображение монитора задач */
    private boolean isMonitorTask = true;
    private static Application app = null;

    ResourceBundle res = ResourceBundle.getBundle(Constants.NAME_RESOURCES, new Locale("ru"));

    public static Application instance() {
        return app;
    }
    
    public Application(String[] args) {
        app = this;
    }
    
    private boolean login() throws Exception {
        // определить дисплей вывода окон
        screen = Utils.getScreenApplication();
        if (screen == -1) {
            screen = 0;
        }
        Splash splash = new Splash(Splash.RUNTIME);
        splash.setVisible(true);

        LoginBox lbox = new LoginBox(null, true);
        Kernel krn = null;
        User user = null;
        res = ResourceBundle.getBundle(Constants.NAME_RESOURCES, new Locale("RU".equals(lbox.getCurrLang()) ? "ru" : "kk"));

        String cacheConfPath = Funcs.normalizeInput(Funcs.getSystemProperty("localCacheConf"));
        if (cacheConfPath != null) {
            Kernel.setInstance(new CachedKernel(cacheConfPath));
        }
        InetAddress address = InetAddress.getLocalHost();
        boolean sLogin = false;
        String pd = null; 
        while (true) {
            if (!sLogin) {
                splash.setVisible(false);
                lbox.result = -1;
                lbox.setVisible(true);
                if (lbox.result == ButtonsFactory.BUTTON_CANCEL) {
                    return false;
                }
                splash.setVisible(true);
                pd = lbox.getPassword();
            }
            
            String name = lbox.getUserName();
            String keyFilePath = lbox.getKeyFilePath();
            String serverType = Funcs.getSystemProperty("serverType");
            String host = Funcs.getSystemProperty("host");
            String port = Funcs.getSystemProperty("port");
            String baseName_ = Funcs.getSystemProperty("dsName");
            String earName = Funcs.getSystemProperty("earName");

            if ("1".equals(Funcs.getSystemProperty("selSrv"))) {
                serverType = lbox.getServerType();
                host = lbox.getHost();
                port = lbox.getPort();
                baseName_ = lbox.getBaseName();
                earName = lbox.getEarName();
            }
            String ip = address.getHostAddress(); 
            String pcName = address.getHostName();
            try {
                SessionOpsOperations ops = Or3Frame.lookup(serverType, Funcs.sanitizeLDAP(host), Integer.parseInt(port), Funcs.sanitizeSQL(earName), true);
                if (keyFilePath != null && !keyFilePath.isEmpty()) {
                    String random = ops.randomString();
                    String sign = KalkanUtil.createPkcs7(keyFilePath, pd, random, false);
                    Kernel.instance().init(sign, null, null, null, host, port, baseName_, Constants.CLIENT_TYPE_APP, ip, pcName, Kernel.LOGIN_KALKAN, ops);
                } else {
                    Kernel.instance().init(name, pd, null, null, host, port, baseName_, Constants.CLIENT_TYPE_APP, ip, pcName, Kernel.LOGIN_USUAL, ops, false, sLogin, false, null);
                }
                break;
            } catch (KrnException e) {
                ResourceBundle res = ResourceBundle.getBundle(Constants.NAME_RESOURCES, new Locale("ru"));
                PathWordChange pdChange;
                switch (e.code) {
                case ErrorCodes.USER_NO_BASE:
                case ErrorCodes.USER_NO_IFC_LANG:
                case ErrorCodes.USER_NO_DATA_LANG:
                    clientLog.info("Ошибка: " + e.getMessage());
                    showMessageDialog(lbox, ERROR_MESSAGE, res.getString(e.getMessage()), lbox.getCurrLang());
                    break;
                case ErrorCodes.USER_HAS_CONNECT:
                    clientLog.info("Ошибка.Пользователь:" + name + " уже подключен к серверу:" + port);
                    showMessageDialog(lbox, ERROR_MESSAGE, res.getString("userHasConnected"), lbox.getCurrLang());
                    break;
                case ErrorCodes.USER_NOT_FOUND:
                    // очистить поле ввода пароля
                    lbox.clearPassword();
                    clientLog.info("Ошибка.Пользователь:" + name + " не имеет доступа к серверу:" + port);
                    showMessageDialog(lbox, ERROR_MESSAGE, res.getString("wrongLoginOrPassword"), lbox.getCurrLang());
                    break;
                case ErrorCodes.SERVER_NOT_AVAILABLE:
                    clientLog.info("Ошибка.Отсутствует связь с сервером:" + port);
                    showMessageDialog(lbox, ERROR_MESSAGE, res.getString("serverDisconnect"), lbox.getCurrLang());
                    break;
                case ErrorCodes.USER_IS_BLOCKED:
                    clientLog.info("Учетная запись '" + name + "' заблокирована администратором");
                    showMessageDialog(lbox, ERROR_MESSAGE, res.getString("userIsBlocked"), lbox.getCurrLang());
                    break;
                case ErrorCodes.USER_IS_EXPIRED:
                    pdChange = new PathWordChange(baseName_,name,Constants.CLIENT_TYPE_APP,ip, pcName,lbox, lbox.getCurrLang(), res, e.object,ErrorCodes.USER_IS_EXPIRED);
                    pdChange.setVisible(true);
                    sLogin = true;
                    if(pdChange.isChangePass()) {
                    	pd = pdChange.getNewPassword();  
                    }
                    break;
                case ErrorCodes.USER_IS_ENDED:
                    pdChange = new PathWordChange(baseName_,name,Constants.CLIENT_TYPE_APP,ip, pcName,lbox, lbox.getCurrLang(), res, e.object,ErrorCodes.USER_IS_ENDED);
                    pdChange.setVisible(true);
                    // если пользователь пароль не изменил
                    if (!pdChange.isChangePass()) {
                        showMessageDialog(lbox, ERROR_MESSAGE, e.getMessage(), lbox.getCurrLang());
                        Kernel.instance().release();
                        return false;
                    }
                    break;
                case ErrorCodes.USER_NOT_LOGIN:
                    pdChange = new PathWordChange(baseName_,name,Constants.CLIENT_TYPE_APP,ip, pcName,lbox, lbox.getCurrLang(), res, e.object,ErrorCodes.USER_NOT_LOGIN);
                    pdChange.setVisible(true);
                    // если пользователь пароль не изменил
                    if (!pdChange.isChangePass()) {
                        showMessageDialog(lbox, ERROR_MESSAGE, e.getMessage(), lbox.getCurrLang());
                        Kernel.instance().release();
                        return false;
                    }
                    lbox.clearPassword();
                    break;
                default:
                    showMessageDialog(lbox, ERROR_MESSAGE, e.getMessage(), lbox.getCurrLang());
                    break;
                }
            }
        }
        krn = Kernel.instance();
        user = krn.getUser();
        lbox.setUserData();
        lbox.dispose();
        // обновить системные переменные
        GlobalConfig.instance(Kernel.instance()).updateSysVar();
        // Обновление системных переменных по настройкам пользователя
        Kernel.instance().getUser().updateConfigUser();
        setUIManagerProps();
        System.setErr(System.out);
        Kernel.instance().setAutoCommit(false);
        UIManager.put("Tree.expandedIcon", getImageIcon("OpenTreeIcon"));
        UIManager.put("Tree.collapsedIcon", getImageIcon("CloseTreeIcon"));
        String currDataLang = LangItem.getById(Kernel.instance().getDataLanguage().id).code;
        String currLang = LangItem.getById(Kernel.instance().getInterfaceLanguage().id).code;
        // определить, необходимо ли отображать монитор задач для текущего пользователя
        isMonitorTask = Kernel.instance().getUser().isMonitor();
        MainFrame frame = new MainFrame(currLang);
        ClientCallback callback = (ClientCallback) Kernel.instance().getCallback();
        callback.setFrame(frame);
        callback.start();
        // frame.setSize(FrameTemplate.frameWidth, FrameTemplate.frameHeight);
        frame.setLocation(kz.tamur.comps.Utils.getCenterLocationPoint(frame.getSize()));
        frame.setVisible(true);
        frame.setDefaultDataLanguage(currDataLang);
        splash.setVisible(false);
        splash.dispose();
        
        return true;
    }

    private static void setUIManagerProps() {
        
        java.util.List<Serializable> buttonGradient = Arrays.asList(.3f, 0f, Utils.getLightSysColor(), Color.white, Utils.getSysColor());
       java.util.List<Object> menuGradient = Arrays.asList(.3f, 0f, UIManager.get("Button.background"),
                UIManager.get("Button.background"), UIManager.get("Button.background"));

        UIManager.put("Button.backgroung", Utils.getLightSysColor());
        UIManager.put("Button.shadow", Utils.getLightSysColor());
        UIManager.put("Button.gradient", buttonGradient);

        UIManager.put("CheckBox.gradient", buttonGradient);
        UIManager.put("ToggleButton.gradient", buttonGradient);

        UIManager.put("Tree.expandedIcon", getImageIcon("OpenTreeIcon"));
        UIManager.put("Tree.collapsedIcon", getImageIcon("CloseTreeIcon"));
        UIManager.put("Tree.background", Utils.getLightSysColor());

        UIManager.put("SplitPane.dividerSize", 3);

        UIManager.put("ScrollBar.thumb", Utils.getSysColor());
        UIManager.put("ScrollBar.thumbHighlight", Utils.getLightSysColor());
        UIManager.put("ScrollBar.thumbShadow", Utils.getDarkShadowSysColor());
        UIManager.put("ScrollBar.gradient", buttonGradient);

        UIManager.put("MenuBar.background", Utils.getDarkShadowSysColor());
        UIManager.put("MenuBar.gradient", menuGradient);

        UIManager.put("Menu.selectionBackground", Utils.getLightSysColor());
        UIManager.put("Menu.background", Utils.getDarkShadowSysColor());
        UIManager.put("Menu.foreground", Utils.getLightSysColor());

        UIManager.put("MenuItem.selectionBackground", Utils.getSysColor());
        UIManager.put("CheckBoxMenuItem.selectionBackground", Utils.getLightSysColor());

        UIManager.put("ToolTip.background", Utils.getLightSysColor());
        UIManager.put("ToolTip.font", Utils.getDefaultFont());

        UIManager.put("List.selectionBackground", Utils.getSysColor());

        UIManager.put("Table.selectionBackground", Utils.getSysColor());

        UIManager.put("Separator.foreground", Utils.getDarkShadowSysColor());

        UIManager.put("ProgressBar.background", Utils.getLightSysColor());
        UIManager.put("ProgressBar.foreground", Utils.getDarkShadowSysColor());
        UIManager.put("ProgressBar.cellLength", 6);
        UIManager.put("ProgressBar.cellSpacing", 2);
        UIManager.put("ProgressBar.border", BorderFactory.createLineBorder(Utils.getDarkShadowSysColor()));

        UIManager.put("RadioButton.font", Utils.getDefaultFont());
        UIManager.put("RadioButton.foreground", Utils.getDarkShadowSysColor());

        UIManager.put("Slider.horizontalThumbIcon", getImageIcon("Slider"));
        UIManager.put("Slider.verticalThumbIcon", getImageIcon("VSlider"));

        UIManager.put("FileView.hardDriveIcon", getImageIcon("Drive"));
        UIManager.put("FileView.floppyDriveIcon", getImageIcon("Save"));
        UIManager.put("FileView.directoryIcon", getImageIcon("NewFolder"));
        UIManager.put("FileView.computerIcon", getImageIcon("Comp"));
        UIManager.put("FileView.fileIcon", getImageIcon("Create"));

        UIManager.put("FileChooser.upFolderIcon", getImageIcon("UpFolder"));
        UIManager.put("FileChooser.homeFolderIcon", getImageIcon("Home"));
        UIManager.put("FileChooser.newFolderIcon", getImageIcon("NewFolder"));
        UIManager.put("FileChooser.listViewIcon", getImageIcon("ListView"));
        UIManager.put("FileChooser.detailsViewIcon", getImageIcon("DetailsView"));
        UIManager.put("FileChooser.directoryOpenButtonText", "Открыть");
        UIManager.put("FileChooser.directoryOpenButtonToolTipText", "Открыть директорию");
        UIManager.put("FileChooser.cancelButtonToolTipText", "Отмена");
        UIManager.put("FileChooser.saveButtonToolTipText", "Сохранить");
        UIManager.put("FileChooser.saveButtonText", "Сохранить");

        UIManager.put("FileChooserUI", "kz.tamur.util.OrFileChooserUI");

        UIManager.put("TextField.inactiveForeground", Color.black);
        UIManager.put("TextArea.inactiveForeground", Color.black);
        Border b = BorderFactory.createLineBorder(Utils.getDarkShadowSysColor(), 2);
        UIManager.put("Table.focusCellHighlightBorder", b);
    }

    public static void main(String[] args) throws Exception {
        AppState.CURRENT_MODE = Mode.RUNTIME;

        String out = Funcs.getSystemProperty("out");
        try {
            String look = Funcs.getSystemProperty("Dlook");
            if (look == null) {
                look = "kz.tamur.comps.ui.OrLookAndFeel";
            }
            UIManager.setLookAndFeel(look);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (Funcs.isValid(out)) {
            File dumpFile = Funcs.getCanonicalFile(out);
            PrintStream newOut = null;
            try {
                newOut = new PrintStream(new BufferedOutputStream(new FileOutputStream(dumpFile, true)), true);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            System.setOut(newOut);
            System.setErr(newOut);
        }
        if (!new Application(args).login())
        	System.exit(0);
    }

    /**
     * @return the isMonitorTask
     */
    public boolean isMonitorTask() {
        return isMonitorTask;
    }

    /**
     * @param isMonitorTask the isMonitorTask to set
     */
    public void setMonitorTask(boolean isMonitorTask) {
        this.isMonitorTask = isMonitorTask;
    }
}
