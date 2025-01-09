package kz.tamur.guidesigner;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.EAST;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.NORTHWEST;
import static java.awt.GridBagConstraints.WEST;
import static kz.tamur.comps.Constants.BLUE_SYS_COLOR;
import static kz.tamur.comps.Constants.CLIENT_VARIABLE_COLOR;
import static kz.tamur.comps.Constants.COMMENT_COLOR;
import static kz.tamur.comps.Constants.DARK_SHADOW_SYS_COLOR;
import static kz.tamur.comps.Constants.DEFAULT_FONT_COLOR;
import static kz.tamur.comps.Constants.INSETS_1;
import static kz.tamur.comps.Constants.INSETS_2;
import static kz.tamur.comps.Constants.KEYWORD_COLOR;
import static kz.tamur.comps.Constants.LIGHT_GREEN_COLOR;
import static kz.tamur.comps.Constants.LIGHT_RED_COLOR;
import static kz.tamur.comps.Constants.LIGHT_SYS_COLOR;
import static kz.tamur.comps.Constants.LIGHT_YELLOW_COLOR;
import static kz.tamur.comps.Constants.MAIN_COLOR;
import static kz.tamur.comps.Constants.MID_SYS_COLOR;
import static kz.tamur.comps.Constants.RED_COLOR;
import static kz.tamur.comps.Constants.SHADOWS_GREY_COLOR;
import static kz.tamur.comps.Constants.SHADOW_YELLOW_COLOR;
import static kz.tamur.comps.Constants.SILVER_COLOR;
import static kz.tamur.comps.Constants.SYS_COLOR;
import static kz.tamur.comps.Constants.VARIABLE_COLOR;
import static kz.tamur.guidesigner.ButtonsFactory.BUTTON_DEFAULT;
import static kz.tamur.guidesigner.ButtonsFactory.BUTTON_YES;
import static kz.tamur.guidesigner.MessagesFactory.CONFIRM_MESSAGE;
import static kz.tamur.guidesigner.MessagesFactory.INFORMATION_MESSAGE;
import static kz.tamur.guidesigner.MessagesFactory.QUESTION_MESSAGE;
import static kz.tamur.rt.Utils.createMenuItem;
import static kz.tamur.rt.Utils.getBlueSysColor;
import static kz.tamur.rt.Utils.getClientVariableColor;
import static kz.tamur.rt.Utils.getCommentColor;
import static kz.tamur.rt.Utils.getDarkShadowSysColor;
import static kz.tamur.rt.Utils.getDefaultFont;
import static kz.tamur.rt.Utils.getDefaultFontColor;
import static kz.tamur.rt.Utils.getKeywordColor;
import static kz.tamur.rt.Utils.getLightGreenColor;
import static kz.tamur.rt.Utils.getLightRedColor;
import static kz.tamur.rt.Utils.getLightSysColor;
import static kz.tamur.rt.Utils.getLightYellowColor;
import static kz.tamur.rt.Utils.getMainColor;
import static kz.tamur.rt.Utils.getMidSysColor;
import static kz.tamur.rt.Utils.getRedColor;
import static kz.tamur.rt.Utils.getShadowYellowColor;
import static kz.tamur.rt.Utils.getShadowsGreyColor;
import static kz.tamur.rt.Utils.getSilverColor;
import static kz.tamur.rt.Utils.getSysColor;
import static kz.tamur.rt.Utils.getVariableColor;
import static kz.tamur.rt.Utils.isColorActive;
import static kz.tamur.rt.Utils.setAllSize;
import static kz.tamur.rt.Utils.setBlueSysColor;
import static kz.tamur.rt.Utils.setClientVariableColor;
import static kz.tamur.rt.Utils.setCommentColor;
import static kz.tamur.rt.Utils.setDarkShadowSysColor;
import static kz.tamur.rt.Utils.setDefaultFontColor;
import static kz.tamur.rt.Utils.setKeywordColor;
import static kz.tamur.rt.Utils.setLightGreenColor;
import static kz.tamur.rt.Utils.setLightRedColor;
import static kz.tamur.rt.Utils.setLightSysColor;
import static kz.tamur.rt.Utils.setLightYellowColor;
import static kz.tamur.rt.Utils.setMainColor;
import static kz.tamur.rt.Utils.setMidSysColor;
import static kz.tamur.rt.Utils.setRedColor;
import static kz.tamur.rt.Utils.setShadowYellowColor;
import static kz.tamur.rt.Utils.setShadowsGreyColor;
import static kz.tamur.rt.Utils.setSilverColor;
import static kz.tamur.rt.Utils.setSysColor;
import static kz.tamur.rt.Utils.setVariableColor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.owasp.esapi.ESAPI;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.UserSessionValue;

import kz.tamur.Or3Frame;
import kz.tamur.comps.Constants;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.OrGuiContainer;
import kz.tamur.comps.OrPanel;
import kz.tamur.comps.PropertyHelper;
import kz.tamur.comps.models.ColorAct;
import kz.tamur.comps.models.GradientColor;
import kz.tamur.comps.ui.GradientPanel;
import kz.tamur.comps.ui.OrGradientMenuBar;
import kz.tamur.guidesigner.service.ServicesTree;
import kz.tamur.rt.Config;
import kz.tamur.rt.GlobalConfig;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.Utils;
import kz.tamur.util.AbstractDesignerTreeNode;
import kz.tamur.util.OpenElementPanel;
import kz.tamur.util.colorchooser.OrColorChooser;
import kz.tamur.util.colorchooser.OrGradientColorChooser;

/**
 * Конфигуратор глобальных настроек системы.
 * 
 * @author Sergey Lebedev
 */
public class ConfigEditor extends GradientPanel implements ActionListener {
	
	private File logotypePic;

    /** Отображение конфигуратором глобальных настроек. */
    private boolean isGlobal;

    /** The status panel. */
    private DesignerStatusBar statusPanel = new DesignerStatusBar();

    /** The menu bar. */
    private OrGradientMenuBar menuBar = new OrGradientMenuBar();

    /** The main menu. */
    private JMenu mainMenu = new JMenu("Конфигурация");

    /** The ok item. */
    private JMenuItem okItem = createMenuItem("Сохранить");

    /** The reset item. */
    private JMenuItem resetItem = createMenuItem("Сбросить","setToDefault.png");

    /** Левая панель глобальных настроек. */
    protected JPanel leftPanel = new JPanel();

    /** Центральная панель глобальных настроек. */
    private JPanel centerPanel = new JPanel();

    /** Правая панель глобальных настроек. */
    private JPanel rightPanel = new JPanel();

    /** для {@link #leftPanel} */
    private JScrollPane leftScroller;

    /** для {@link #centerPanel} */
    private JScrollPane centerScroller;

    /** для {@link #rightPanel} */
    private JScrollPane rightScroller;

    /** Параметр градиентной заливки для главного фрейма системы. */
    protected GradientColor gradientMainFrame = null;

    /** Параметр градиентной заливки для панели управления системы. */
    protected GradientColor gradientControlPanel = null;

    /** Параметр градиентной заливки для панели меню системы. */
    protected GradientColor gradientMenuPanel = null;

    /** Настройка прозрачности панелей системы, позволяет включить/выключить возможность работы с ней (прозрачностью). */
    protected boolean transparentMain;

    /** Настройка прозрачности диалоговых окон системы, позволяет включить/выключить возможность работы с ней (прозрачностью). */
    protected boolean transparentDialog;

    /** Прозрачность ячеек таблиц и деревьев. */
    protected int transparentCellTable;

    /** Цвет заголовков таблиц и деревьев. */
    protected Color colorHeaderTable;

    /** Цвет фона заголовка выбранной вкладки. */
    protected Color colorTabTitle;

    /** Цвет фона заголовка фоновых вкладок. */
    protected Color colorBackTabTitle;

    /** Цвет шрифта заголовка выбранной вкладки. */
    protected Color colorFontTabTitle;

    /** Цвет шрифта заголовка фоновых вкладок. */
    protected Color colorFontBackTabTitle;

    /** Прозрачность заголовка фоновой вкладки. */
    protected int transparentBackTabTitle;

    /** Прозрачность фона заголовка выбранной вкладки. */
    protected int transparentSelectedTabTitle;

    /** Параметр градиентной заливки для полей не прошедших ФЛК. */
    protected GradientColor gradientFieldNOFLC = new GradientColor();

    /** The object browser limit. */
    protected int objectBrowserLimit;

    /** The is object browser limit. */
    protected boolean isObjectBrowserLimit;

    /** The is object browser limit for classes. */
    protected boolean isObjectBrowserLimitForClasses;

    // компоненты интерфейса

    // активаторы градиентных заливок
    /** Флажок активации градиентной заливки главного интерфейса. */
    private AdditChecBox gradientMainFrameEnabledCheck = new AdditChecBox();

    /** Флажок активации градиентной заливки панели управления. */
    private AdditChecBox gradientControlPanelEnabledCheck = new AdditChecBox();

    /** Флажок активации градиентной заливки панели меню. */
    private AdditChecBox gradientMenuPanelEnabledCheck = new AdditChecBox();

    /** Флажок активации градиентной заливки полей, не прошедших ФЛК. */
    private AdditChecBox gradientFieldNOFLCEnabledCheck = new AdditChecBox();

    /** The is object browser limit check. */
    private AdditChecBox isObjectBrowserLimitCheck = new AdditChecBox("Активировать лимитированное отображение классов");

    /** The is object browser limit for classes check. */
    private AdditChecBox isObjectBrowserLimitForClassesCheck = new AdditChecBox("Разрешить индивидуальные лимиты");

    /** Кнопка запуска обновления идентификаторов компонентов. */
    private JButton refreshUUID = ButtonsFactory.createToolButton("refreshUUID", ".png", "Обновить все UUID компонентов");
    private JButton refreshUUIDF = ButtonsFactory.createToolButton("refreshUUIDF", ".png", "Переписать все UUID компонентов");

    /** генерация html шаблонов */
    private AdditChecBox isDataLog = new AdditChecBox("Логировать изменения атрибутов? НЕ УБИРАТЬ ЭТУ ГАЛОЧКУ!!!!!!!!!!");
    private AdditButton generateHtmlTemplates = new AdditButton("Сгенерировать html шаблоны для UI");
    
    private AdditButton resaveIfcForEGKNH = new AdditButton("Пересохранить указанный интерфейс для ЕГКН");
    private AdditButton selectUI = new AdditButton("Выбрать интерфейс");
    private AdditButton resaveAllIfcsForEGKNH = new AdditButton("Пересохранить все интерфейсы для ЕГКН");
    
    private AdditButton resaveProcForEGKNH = new AdditButton("Пересохранить указанный процесс для ЕГКН");
    private AdditButton selectProc = new AdditButton("Выбрать процесс");
    private AdditButton resaveAllProcsForEGKNH = new AdditButton("Пересохранить все процессы для ЕГКН");
    
    private AdditButton resaveLogotypePic = new AdditButton("Сохранить");
    private AdditButton selectPic = new AdditButton("Выбрать рисунок");
    private JTextField selPicT = new JTextField("");
    private AdditButton removePic = new AdditButton("Очистить рисунок логотипа");
    private AdditLabel picWidthL = new AdditLabel("Ширина рисунка: ");
    private AdditLabel picHeightL = new AdditLabel("Высота рисунка: ");
    private JTextField picWidthT = new JTextField("");
    private JTextField picHeightT = new JTextField("");
    private JLabel picLabel = new JLabel();
    private byte[] iconPic;
    
    private AdditButton resaveSearchParam = new AdditButton("Сохранить");
    private AdditChecBox showSearchField = new AdditChecBox("Показать поле поиска в главном окне?");
    private AdditLabel srch_txtL = new AdditLabel("Текст поиска: ");
    private AdditLabel srch_txtKzL = new AdditLabel("Текст поиска Каз: ");
    private AdditLabel ifc_uidL = new AdditLabel("UID интерфейса: ");
    private JTextField srch_txtT = new JTextField("");
    private JTextField srch_txtKzT = new JTextField("");
    private JTextField ifc_uidT = new JTextField("");
    
    private AdditButton resaveChatSearchParam = new AdditButton("Сохранить");
    private AdditLabel chat_srch_txtL = new AdditLabel("Текст поиска: ");
    private AdditLabel chat_srch_txtKzL = new AdditLabel("Текст поиска Каз: ");
    private JTextField chat_srch_txtT = new JTextField("");
    private JTextField chat_srch_txtKzT = new JTextField("");   
    		
    private AdditButton resaveNoteSound = new AdditButton("Сохранить");
    private AdditChecBox useNotificationSound = new AdditChecBox("Активировать звук уведомления");
    private AdditButton selectSound = new AdditButton("Выбрать звук");
    private JTextField selSoundT = new JTextField("");
    private AdditButton removeNoteSound = new AdditButton("Очистить звук");
    private AdditButton playNoteSound = new AdditButton("Играть звук");
    private byte[] noteSound;
    
    
    

    // кнопки задания градиентных заливок
    /** Кнопка задания градиентной заливки главного интрефейса системы. */ 
    private AdditButton gradientMainFrameBtn = new AdditButton();

    /** Кнопка задания градиентной заливки панели управления. */
    private AdditButton gradientControlPanelBtn = new AdditButton();

    /** Кнопка задания градиентной заливки панели меню. */
    private AdditButton gradientMenuPanelBtn = new AdditButton();

    /** Кнопка задания главного цвета системы. */
    private AdditButton colorMainBtn = new AdditButton();

    /** Кнопка задания градиентной заливки полей, не прошедших ФЛК. */
    private AdditButton gradientFieldNOFLCBtn = new AdditButton();

    /** The color header table btn. */
    private AdditButton colorHeaderTableBtn = new AdditButton();

    /** The color tab title btn. */
    private AdditButton colorTabTitleBtn = new AdditButton();

    /** The color back tab title btn. */
    private AdditButton colorBackTabTitleBtn = new AdditButton();

    /** The color font tab title btn. */
    private AdditButton colorFontTabTitleBtn = new AdditButton();

    /** The color font back tab title btn. */
    private AdditButton colorFontBackTabTitleBtn = new AdditButton();

    /** The blue sys color btn. */
    private AdditButton blueSysColorBtn = new AdditButton();

    /** The dark shadow sys color btn. */
    private AdditButton darkShadowSysColorBtn = new AdditButton();

    /** The mid sys color btn. */
    private AdditButton midSysColorBtn = new AdditButton();

    /** The light yellow color btn. */
    private AdditButton lightYellowColorBtn = new AdditButton();

    /** The red color btn. */
    private AdditButton redColorBtn = new AdditButton();

    /** The light red color btn. */
    private AdditButton lightRedColorBtn = new AdditButton();

    /** The light green color btn. */
    private AdditButton lightGreenColorBtn = new AdditButton();

    /** The shadow yellow color btn. */
    private AdditButton shadowYellowColorBtn = new AdditButton();

    /** The sys color btn. */
    private AdditButton sysColorBtn = new AdditButton();

    /** The light sys color btn. */
    private AdditButton lightSysColorBtn = new AdditButton();

    /** The default font color btn. */
    private AdditButton defaultFontColorBtn = new AdditButton();

    /** The silver color btn. */
    private AdditButton silverColorBtn = new AdditButton();

    /** The shadows grey color btn. */
    private AdditButton shadowsGreyColorBtn = new AdditButton();

    /** The keyword color btn. */
    private AdditButton keywordColorBtn = new AdditButton();

    /** The variable color btn. */
    private AdditButton variableColorBtn = new AdditButton();

    /** The client variable color btn. */
    private AdditButton clientVariableColorBtn = new AdditButton();

    /** The comment color btn. */
    private AdditButton commentColorBtn = new AdditButton();

    /** The clean object browser limit for classes btn. */
    private AdditButton cleanObjectBrowserLimitForClassesBtn = new AdditButton("Сбросить настройки индивидуальных лимитов");

    /** Флажок включения прозрачности для панелей системы. */
    protected AdditChecBox transparentMainCheck = new AdditChecBox("Разрешить прозрачность");

    /** Флажок для включения прозрачности системных диалогов. */
    protected AdditChecBox transparentDialogCheck = new AdditChecBox("Разрешить прозрачность диалогов");

    /** Панель отображения градиентной заливки основных панелей интерфейса. */
    private GradientPanel gradientMainFramePane = new GradientPanel();

    /** Панель отображения градиентной заливки панели управления. */
    private GradientPanel gradientControlPanelPane = new GradientPanel();

    /** Панель отображения градинтной заливки панели меню. */
    protected GradientPanel gradientMenuPanelPane = new GradientPanel();

    /** Панель отображения градиентной заливки полей, не прошедших ФЛК. */
    private GradientPanel gradientFieldNOFLCPane = new GradientPanel();

    /** The color header table pane. */
    protected JPanel colorHeaderTablePane = new JPanel();

    /** The color tab title pane. */
    protected JPanel colorTabTitlePane = new JPanel();

    /** The color back tab title pane. */
    protected JPanel colorBackTabTitlePane = new JPanel();

    /** The color font tab title pane. */
    protected JPanel colorFontTabTitlePane = new JPanel();

    /** The color font back tab title pane. */
    protected JPanel colorFontBackTabTitlePane = new JPanel();

    /** The class control pane. */
    protected JPanel classControlPane = new JPanel();
    
    /** The class control pane. */
    protected JPanel uiControlPane = new JPanel();
    
    private JPanel uiOpsForEGKNPane = new JPanel();
    
    private JPanel procOpsForEGKNPane = new JPanel();
    
    private JPanel logotypePicsPane = new JPanel();
    
    private JPanel searchParamPane = new JPanel();
    
    private JPanel chatSearchParamPane = new JPanel();
    
    private JPanel notificationSoundPane = new JPanel();
    
    /** Панель отображения цвета главного интерфейса системы. */
    protected JPanel colorMainPane = new JPanel();
    
    /** The sys var pane. */
    protected JPanel sysVarPane = new JPanel();

    /** The blue sys color pane. */
    private JPanel blueSysColorPane = new JPanel();

    /** The dark shadow sys color pane. */
    private JPanel darkShadowSysColorPane = new JPanel();

    /** The mid sys color pane. */
    private JPanel midSysColorPane = new JPanel();

    /** The light yellow color pane. */
    private JPanel lightYellowColorPane = new JPanel();

    /** The red color pane. */
    private JPanel redColorPane = new JPanel();

    /** The light red color pane. */
    private JPanel lightRedColorPane = new JPanel();

    /** The light green color pane. */
    private JPanel lightGreenColorPane = new JPanel();

    /** The shadow yellow color pane. */
    private JPanel shadowYellowColorPane = new JPanel();

    /** The sys color pane. */
    private JPanel sysColorPane = new JPanel();

    /** The light sys color pane. */
    private JPanel lightSysColorPane = new JPanel();

    /** The default font color pane. */
    private JPanel defaultFontColorPane = new JPanel();

    /** The silver color pane. */
    private JPanel silverColorPane = new JPanel();

    /** The shadows grey color pane. */
    private JPanel shadowsGreyColorPane = new JPanel();

    /** The keyword color pane. */
    private JPanel keywordColorPane = new JPanel();

    /** The variable color pane. */
    private JPanel variableColorPane = new JPanel();

    /** The client variable color pane. */
    private JPanel clientVariableColorPane = new JPanel();

      /** The comment color pane. */
    private JPanel commentColorPane = new JPanel();

    protected JPanel colorMainPane2 = new JPanel();
    
    /** The blue sys color pane2. */
    private JPanel blueSysColorPane2 = new JPanel();

    /** The dark shadow sys color pane2. */
    private JPanel darkShadowSysColorPane2 = new JPanel();

    /** The mid sys color pane2. */
    private JPanel midSysColorPane2 = new JPanel();

    /** The light yellow color pane2. */
    private JPanel lightYellowColorPane2 = new JPanel();

    /** The red color pane2. */
    private JPanel redColorPane2 = new JPanel();

    /** The light red color pane2. */
    private JPanel lightRedColorPane2 = new JPanel();

    /** The light green color pane2. */
    private JPanel lightGreenColorPane2 = new JPanel();

    /** The shadow yellow color pane2. */
    private JPanel shadowYellowColorPane2 = new JPanel();

    /** The sys color pane2. */
    private JPanel sysColorPane2 = new JPanel();

    /** The light sys color pane2. */
    private JPanel lightSysColorPane2 = new JPanel();

    /** The default font color pane2. */
    private JPanel defaultFontColorPane2 = new JPanel();

    /** The silver color pane2. */
    private JPanel silverColorPane2 = new JPanel();

    /** The shadows grey color pane2. */
    private JPanel shadowsGreyColorPane2 = new JPanel();

    /** The keyword color pane2. */
    private JPanel keywordColorPane2 = new JPanel();

    /** The variable color pane2. */
    private JPanel variableColorPane2 = new JPanel();

    /** The client variable color pane2. */
    private JPanel clientVariableColorPane2 = new JPanel();

    /** The comment color pane2. */
    private JPanel commentColorPane2 = new JPanel();

    /** The Object browser view pane. */
    private JPanel ObjectBrowserViewPane = new JPanel();

    /** The transparent cell table pane. */
    protected GradientPanel transparentCellTablePane = new GradientPanel();

    /** The transparent back tab title pane. */
    protected GradientPanel transparentBackTabTitlePane = new GradientPanel();

    /** The transparent selected tab title pane. */
    protected GradientPanel transparentSelectedTabTitlePane = new GradientPanel();

    // текстовые поля задания настроек вручную
    /** Поле редактирования градиентной заливки главного окна. */
    private JTextField gradientMainFrameField = new JTextField();

    /** Поле редактирования градиентной заливки панели управления. */
    private JTextField gradientControlPanelField = new JTextField();

    /** Поле редактирования градиентной заливки панели меню. */
    private JTextField gradientMenuPanelField = new JTextField();

    /** Поле редактирования главного цвета системы. */
    private JTextField colorMainField = new JTextField();

    /** The transparent cell table field. */
    private JTextField transparentCellTableField = new JTextField();

    /** The color header table field. */
    private JTextField colorHeaderTableField = new JTextField();

    /** The color tab title field. */
    private JTextField colorTabTitleField = new JTextField();

    /** The color back tab title field. */
    private JTextField colorBackTabTitleField = new JTextField();

    /** The color font tab title field. */
    private JTextField colorFontTabTitleField = new JTextField();

    /** The color font back tab title field. */
    private JTextField colorFontBackTabTitleField = new JTextField();

    /** The transparent back tab title field. */
    private JTextField transparentBackTabTitleField = new JTextField();

    /** The transparent selected tab title field. */
    private JTextField transparentSelectedTabTitleField = new JTextField();

    /** The blue sys color field. */
    private JTextField blueSysColorField = new JTextField();

    /** The dark shadow sys color field. */
    private JTextField darkShadowSysColorField = new JTextField();

    /** The mid sys color field. */
    private JTextField midSysColorField = new JTextField();

    /** The light yellow color field. */
    private JTextField lightYellowColorField = new JTextField();

    /** The red color field. */
    private JTextField redColorField = new JTextField();

    /** The light red color field. */
    private JTextField lightRedColorField = new JTextField();

    /** The light green color field. */
    private JTextField lightGreenColorField = new JTextField();

    /** The shadow yellow color field. */
    private JTextField shadowYellowColorField = new JTextField();

    /** The sys color field. */
    private JTextField sysColorField = new JTextField();

    /** The light sys color field. */
    private JTextField lightSysColorField = new JTextField();

    /** The default font color field. */
    private JTextField defaultFontColorField = new JTextField();

    /** The silver color field. */
    private JTextField silverColorField = new JTextField();

    /** The shadows grey color field. */
    private JTextField shadowsGreyColorField = new JTextField();

    /** The keyword color field. */
    private JTextField keywordColorField = new JTextField();

    /** The variable color field. */
    private JTextField variableColorField = new JTextField();

    /** The client variable color field. */
    private JTextField clientVariableColorField = new JTextField();

    /** The comment color field. */
    private JTextField commentColorField = new JTextField();

    /** Поле редактирования градиентной заливки полей не прошедших ФЛК. */
    private JTextField gradientFieldNOFLCField = new JTextField();

    /** The object browser limit field. */
    private JTextField objectBrowserLimitField = new JTextField("Сбросить индивидуальные настройки лимитов");

    /** The transparent cell table slider. */
    private JSlider transparentCellTableSlider;

    /** The transparent back tab title slider. */
    private JSlider transparentBackTabTitleSlider;

    /** The transparent selected tab title slider. */
    private JSlider transparentSelectedTabTitleSlider;

    /** The blue sys color check. */
    protected AdditChecBox colorMainCheck = new AdditChecBox();
    
    /** The blue sys color check. */
    protected AdditChecBox blueSysColorCheck = new AdditChecBox();

    /** The dark shadow sys color check. */
    protected AdditChecBox darkShadowSysColorCheck = new AdditChecBox();

    /** The mid sys color check. */
    protected AdditChecBox midSysColorCheck = new AdditChecBox();

    /** The light yellow color check. */
    protected AdditChecBox lightYellowColorCheck = new AdditChecBox();

    /** The red color check. */
    protected AdditChecBox redColorCheck = new AdditChecBox();

    /** The light red color check. */
    protected AdditChecBox lightRedColorCheck = new AdditChecBox();

    /** The light green color check. */
    protected AdditChecBox lightGreenColorCheck = new AdditChecBox();

    /** The shadow yellow color check. */
    protected AdditChecBox shadowYellowColorCheck = new AdditChecBox();

    /** The sys color check. */
    protected AdditChecBox sysColorCheck = new AdditChecBox();

    /** The light sys color check. */
    protected AdditChecBox lightSysColorCheck = new AdditChecBox();

    /** The default font color check. */
    protected AdditChecBox defaultFontColorCheck = new AdditChecBox();

    /** The silver color check. */
    protected AdditChecBox silverColorCheck = new AdditChecBox();

    /** The shadows grey color check. */
    protected AdditChecBox shadowsGreyColorCheck = new AdditChecBox();

    /** The keyword color check. */
    protected AdditChecBox keywordColorCheck = new AdditChecBox();

    /** The variable color check. */
    protected AdditChecBox variableColorCheck = new AdditChecBox();

    /** The client variable color check. */
    protected AdditChecBox clientVariableColorCheck = new AdditChecBox();

    /** The comment color check. */
    protected AdditChecBox commentColorCheck = new AdditChecBox();

    // класс настроек
    /** The config. */
    protected GlobalConfig config = GlobalConfig.instance(Kernel.instance());

    /** The conf. */
    protected Config conf = config.getConfig();

    // Блок объявления переменных цветов, обязательно должен идти после инициализации класса GlobalConfig
    
    /** Цвет, определяющий основную цветовую гамму интерфейсов. */
    protected Color mainColor = getMainColor();
    
    /** The blue sys color. */
    protected Color blueSysColor = getBlueSysColor();

    /** The dark shadow sys color. */
    protected Color darkShadowSysColor = getDarkShadowSysColor();

    /** The mid sys color. */
    protected Color midSysColor = getMidSysColor();

    /** The light yellow color. */
    protected Color lightYellowColor = getLightYellowColor();

    /** The red color. */
    protected Color redColor = getRedColor();

    /** The light red color. */
    protected Color lightRedColor = getLightRedColor();

    /** The light green color. */
    protected Color lightGreenColor = getLightGreenColor();

    /** The shadow yellow color. */
    protected Color shadowYellowColor = getShadowYellowColor();

    /** The sys color. */
    protected Color sysColor = getSysColor();

    /** The light sys color. */
    protected Color lightSysColor = getLightSysColor();

    /** The default font color. */
    protected Color defaultFontColor = getDefaultFontColor();

    /** The silver color. */
    protected Color silverColor = getSilverColor();

    /** The shadows grey color. */
    protected Color shadowsGreyColor = getShadowsGreyColor();

    /** The keyword color. */
    protected Color keywordColor = getKeywordColor();

    /** The variable color. */
    protected Color variableColor = getVariableColor();

    /** The client variable color. */
    protected Color clientVariableColor = getClientVariableColor();

    /** The comment color. */
    protected Color commentColor = getCommentColor();

    /** The is change gradient main frame. */
    protected boolean isChangeGradientMainFrame = false;

    /** The is change gradient control panel. */
    protected boolean isChangeGradientControlPanel = false;

    /** The is change gradient menu panel. */
    protected boolean isChangeGradientMenuPanel = false;

    /** The is change transparent main. */
    protected boolean isChangeTransparentMain = false;

    /** The is change transparent dialog. */
    protected boolean isChangeTransparentDialog = false;

    /** The is change color main. */
    protected boolean isChangeColorMain = false;

    /** The is transparent cell table. */
    protected boolean isTransparentCellTable = false;

    /** The is color header table. */
    protected boolean isColorHeaderTable = false;

    /** The is color tab title. */
    protected boolean isColorTabTitle = false;

    /** The is color back tab title. */
    protected boolean isColorBackTabTitle = false;

    /** The is color font tab title. */
    protected boolean isColorFontTabTitle = false;

    /** The is color font back tab title. */
    protected boolean isColorFontBackTabTitle = false;

    /** The is transparent back tab title. */
    protected boolean isTransparentBackTabTitle = false;

    /** The is transparent selected tab title. */
    protected boolean isTransparentSelectedTabTitle = false;

    /** The is gradient field noflc. */
    protected boolean isGradientFieldNOFLC = false;

    /** The is blue sys color. */
    protected boolean isBlueSysColor = false;

    /** The is dark shadow sys color. */
    protected boolean isDarkShadowSysColor = false;

    /** The is mid sys color. */
    protected boolean isMidSysColor = false;

    /** The is light yellow color. */
    protected boolean isLightYellowColor = false;

    /** The is red color. */
    protected boolean isRedColor = false;

    /** The is light red color. */
    protected boolean isLightRedColor = false;

    /** The is light green color. */
    protected boolean isLightGreenColor = false;

    /** The is shadow yellow color. */
    protected boolean isShadowYellowColor = false;

    /** The is sys color. */
    protected boolean isSysColor = false;

    /** The is light sys color. */
    protected boolean isLightSysColor = false;

    /** The is default font color. */
    protected boolean isDefaultFontColor = false;

    /** The is silver color. */
    protected boolean isSilverColor = false;

    /** The is shadows grey color. */
    protected boolean isShadowsGreyColor = false;

    /** The is keyword color. */
    protected boolean isKeywordColor = false;

    /** The is variable color. */
    protected boolean isVariableColor = false;

    /** The is client variable color. */
    protected boolean isClientVariableColor = false;

    /** The is comment color. */
    protected boolean isCommentColor = false;

    /** The is change object browser limit. */
    protected boolean isChangeObjectBrowserLimit = false;

    /** The is change is object browser limit. */
    protected boolean isChangeIsObjectBrowserLimit = false;

    /** The is change is object browser limit for classes. */
    protected boolean isChangeIsObjectBrowserLimitForClasses = false;

    /** The gr pane size. */
    private final Dimension grPaneSize = new Dimension(450, 60);

    /** The gr pane size2. */
    private final Dimension grPaneSize2 = new Dimension(450, 1100);

    /** The gr pane size3. */
    private final Dimension grPaneSize3 = new Dimension(450, 120);

    /** The color pane size. */
    private final Dimension colorPaneSize = new Dimension(75, 25);

    /** The btn size. */
    private final Dimension btnSize = new Dimension(100, 25);

    /** The btn size2. */
    private final Dimension btnSize2 = new Dimension(200, 25);

    /** The field size3. */
    private final Dimension fieldSize3 = new Dimension(50, 25);

    /** The is opaque. */
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;

    boolean isValid = true;
    /** Процент выполнения процесса генерации. */
    double prc;
    /** Признак генерации шаблонов. */
    boolean isGen = false;
    
    boolean isUIResaveInProcess = false;
    boolean isProcResaveInProcess = false;

    private OpenElementPanel panel;
    
    /**
     * Создание нового config editor.
     */
    public ConfigEditor() {
        this(null, true);
    }

    /**
     * Создание нового config editor.
     * 
     * @param config
     *            the config
     * @param _isGlobal
     *            флаг запуска редактора
     */
    public ConfigEditor(Config config, boolean _isGlobal) {
        isGlobal = _isGlobal;
        if (config != null) {
            conf = config;
        }
        GridBagLayout gbl = new GridBagLayout();
        // установка свойств панелей
        setLayout(gbl);
        leftPanel.setLayout(gbl);
        centerPanel.setLayout(gbl);
        rightPanel.setLayout(gbl);
        setOpaque(isOpaque);

        leftPanel.setOpaque(isOpaque);
        centerPanel.setOpaque(isOpaque);
        rightPanel.setOpaque(isOpaque);

        leftScroller = new JScrollPane(leftPanel);
        leftScroller.setOpaque(isOpaque);
        leftScroller.getViewport().setOpaque(isOpaque);

        centerScroller = new JScrollPane(centerPanel);
        centerScroller.setOpaque(isOpaque);
        centerScroller.getViewport().setOpaque(isOpaque);

        rightScroller = new JScrollPane(rightPanel);
        rightScroller.setOpaque(isOpaque);
        rightScroller.getViewport().setOpaque(isOpaque);

        // установка свойств компонентов
        ObjectBrowserViewPane.setLayout(new GridBagLayout());
        ObjectBrowserViewPane.setBorder(Utils.createTitledBorder(getBorder(), "Настройка объектного браузера"));

        gradientMainFramePane.setLayout(new GridBagLayout());
        gradientMainFramePane.setBorder(Utils.createTitledBorder(getBorder(), "Градиент главного окна"));

        gradientControlPanelPane.setLayout(new GridBagLayout());
        gradientControlPanelPane.setBorder(Utils.createTitledBorder(getBorder(), "Градиент панели управления"));

        gradientMenuPanelPane.setLayout(new GridBagLayout());
        gradientMenuPanelPane.setBorder(Utils.createTitledBorder(getBorder(), "Градиент меню"));

        gradientFieldNOFLCPane.setLayout(new GridBagLayout());
        gradientFieldNOFLCPane.setBorder(Utils.createTitledBorder(getBorder(), "Градиент полей не прошедших ФЛК"));

        // цветовые панели
        
        colorHeaderTablePane.setLayout(new GridBagLayout());
        colorHeaderTablePane.setBorder(Utils.createTitledBorder(getBorder(), "Цвет заголовков таблиц и деревьев"));

        colorTabTitlePane.setLayout(new GridBagLayout());
        colorTabTitlePane.setBorder(Utils.createTitledBorder(getBorder(), "Цвет фона заголовка выбранной вкладки"));

        colorBackTabTitlePane.setLayout(new GridBagLayout());
        colorBackTabTitlePane.setBorder(Utils.createTitledBorder(getBorder(), "Цвет фона заголовка фоновых вкладок"));

        colorFontTabTitlePane.setLayout(new GridBagLayout());
        colorFontTabTitlePane.setBorder(Utils.createTitledBorder(getBorder(), "Цвет шрифта заголовка выбранной вкладки"));

        colorFontBackTabTitlePane.setLayout(new GridBagLayout());
        colorFontBackTabTitlePane.setBorder(Utils.createTitledBorder(getBorder(), "Цвет шрифта заголовка фоновых вкладок"));

        classControlPane.setLayout(new GridBagLayout());
        classControlPane.setBorder(Utils.createTitledBorder(getBorder(), "Верификация новых классов базы"));

        uiControlPane.setLayout(new GridBagLayout());
        uiControlPane.setBorder(Utils.createTitledBorder(getBorder(), "Генерация html шаблонов"));        
        
        uiOpsForEGKNPane.setLayout(new GridBagLayout());
        uiOpsForEGKNPane.setBorder(Utils.createTitledBorder(getBorder(), "Операции с интерфейсами для ЕГКН"));
        
        procOpsForEGKNPane.setLayout(new GridBagLayout());
        procOpsForEGKNPane.setBorder(Utils.createTitledBorder(getBorder(), "Операции с процессами для ЕГКН"));
        
        logotypePicsPane.setLayout(new GridBagLayout());
        logotypePicsPane.setBorder(Utils.createTitledBorder(getBorder(), "Рисунок логотипа"));
        
        searchParamPane.setLayout(new GridBagLayout());
        searchParamPane.setBorder(Utils.createTitledBorder(getBorder(), "Быстрый доступ к объекту учета (Placeholder)"));
        
        chatSearchParamPane.setLayout(new GridBagLayout());
        chatSearchParamPane.setBorder(Utils.createTitledBorder(getBorder(), "Чат (Placeholder)"));
        
        notificationSoundPane.setLayout(new GridBagLayout());
        notificationSoundPane.setBorder(Utils.createTitledBorder(getBorder(), "Звук уведомления"));
        
        transparentCellTablePane.setLayout(new GridBagLayout());
        transparentCellTablePane.setBorder(Utils.createTitledBorder(getBorder(), "Прозрачность ячеек таблиц и деревьев"));

        transparentBackTabTitlePane.setLayout(new GridBagLayout());
        transparentBackTabTitlePane.setBorder(Utils.createTitledBorder(getBorder(), "Прозрачность заголовка фоновой вкладки"));

        transparentSelectedTabTitlePane.setLayout(new GridBagLayout());
        transparentSelectedTabTitlePane.setBorder(Utils.createTitledBorder(getBorder(), "Прозрачность фона заголовка выбранной вкладки"));

        sysVarPane.setLayout(new GridBagLayout());
        sysVarPane.setBorder(Utils.createTitledBorder(getBorder(), "Переопределение системных переменных"));

        colorMainPane2.add(new AdditLabel("Tекущий цвет"));
        blueSysColorPane2.add(new AdditLabel("Tекущий цвет"));
        darkShadowSysColorPane2.add(new AdditLabel("Tекущий цвет"));
        midSysColorPane2.add(new AdditLabel("Tекущий цвет"));
        lightYellowColorPane2.add(new AdditLabel("Tекущий цвет"));
        redColorPane2.add(new AdditLabel("Tекущий цвет"));
        lightRedColorPane2.add(new AdditLabel("Tекущий цвет"));
        lightGreenColorPane2.add(new AdditLabel("Tекущий цвет"));
        shadowYellowColorPane2.add(new AdditLabel("Tекущий цвет"));
        sysColorPane2.add(new AdditLabel("Tекущий цвет"));
        lightSysColorPane2.add(new AdditLabel("Tекущий цвет"));
        defaultFontColorPane2.add(new AdditLabel("Tекущий цвет"));
        silverColorPane2.add(new AdditLabel("Tекущий цвет"));
        shadowsGreyColorPane2.add(new AdditLabel("Tекущий цвет"));
        keywordColorPane2.add(new AdditLabel("Tекущий цвет"));
        variableColorPane2.add(new AdditLabel("Tекущий цвет"));
        clientVariableColorPane2.add(new AdditLabel("Tекущий цвет"));
        commentColorPane2.add(new AdditLabel("Tекущий цвет"));

        colorMainPane.setLayout(new GridBagLayout());
        blueSysColorPane.setLayout(new GridBagLayout());
        darkShadowSysColorPane.setLayout(new GridBagLayout());
        midSysColorPane.setLayout(new GridBagLayout());
        lightYellowColorPane.setLayout(new GridBagLayout());
        redColorPane.setLayout(new GridBagLayout());
        lightRedColorPane.setLayout(new GridBagLayout());
        lightGreenColorPane.setLayout(new GridBagLayout());
        shadowYellowColorPane.setLayout(new GridBagLayout());
        sysColorPane.setLayout(new GridBagLayout());
        lightSysColorPane.setLayout(new GridBagLayout());
        defaultFontColorPane.setLayout(new GridBagLayout());
        silverColorPane.setLayout(new GridBagLayout());
        shadowsGreyColorPane.setLayout(new GridBagLayout());
        keywordColorPane.setLayout(new GridBagLayout());
        variableColorPane.setLayout(new GridBagLayout());
        clientVariableColorPane.setLayout(new GridBagLayout());
        commentColorPane.setLayout(new GridBagLayout());

        colorMainPane.setBorder(Utils.createTitledBorder(getBorder(), "Главный цвет интерфейсов системы"));
        blueSysColorPane.setBorder(Utils.createTitledBorder(getBorder(), "blueSysColor"));
        darkShadowSysColorPane.setBorder(Utils.createTitledBorder(getBorder(), "darkShadowSysColor"));
        midSysColorPane.setBorder(Utils.createTitledBorder(getBorder(), "midSysColor"));
        lightYellowColorPane.setBorder(Utils.createTitledBorder(getBorder(), "lightYellowColor"));
        redColorPane.setBorder(Utils.createTitledBorder(getBorder(), "redColor"));
        lightRedColorPane.setBorder(Utils.createTitledBorder(getBorder(), "lightRedColor"));
        lightGreenColorPane.setBorder(Utils.createTitledBorder(getBorder(), "lightGreenColor"));
        shadowYellowColorPane.setBorder(Utils.createTitledBorder(getBorder(), "shadowYellowColor"));
        sysColorPane.setBorder(Utils.createTitledBorder(getBorder(), "sysColor"));
        lightSysColorPane.setBorder(Utils.createTitledBorder(getBorder(), "lightSysColor"));
        defaultFontColorPane.setBorder(Utils.createTitledBorder(getBorder(), "defaultFontColor"));
        silverColorPane.setBorder(Utils.createTitledBorder(getBorder(), "silverColor"));
        shadowsGreyColorPane.setBorder(Utils.createTitledBorder(getBorder(), "shadowsGreyColor"));
        keywordColorPane.setBorder(Utils.createTitledBorder(getBorder(), "Редактор формул, ключевые слова (keywordColor)"));
        variableColorPane.setBorder(Utils.createTitledBorder(getBorder(), "Редактор формул, переменные (variableColor)"));
        clientVariableColorPane.setBorder(Utils.createTitledBorder(getBorder(), "Редактор формул, пользовательские переменные (clientVariableColor)"));
        commentColorPane.setBorder(Utils.createTitledBorder(getBorder(), "Редактор формул, комментарии (commentColor)"));

        setAllSize(ObjectBrowserViewPane, grPaneSize3);
        setAllSize(gradientMainFramePane, grPaneSize);
        setAllSize(gradientControlPanelPane, grPaneSize);
        setAllSize(gradientMenuPanelPane, grPaneSize);
        setAllSize(gradientFieldNOFLCPane, grPaneSize);
        setAllSize(colorHeaderTablePane, grPaneSize);
        setAllSize(colorTabTitlePane, grPaneSize);
        setAllSize(colorBackTabTitlePane, grPaneSize);
        setAllSize(colorFontTabTitlePane, grPaneSize);
        setAllSize(colorFontBackTabTitlePane, grPaneSize);
        setAllSize(classControlPane, grPaneSize);
        setAllSize(uiControlPane, grPaneSize);
        setAllSize(uiOpsForEGKNPane, new Dimension(450, 80));
        setAllSize(procOpsForEGKNPane, new Dimension(450, 80));
        setAllSize(logotypePicsPane, new Dimension(450, 140));
        setAllSize(selPicT, new Dimension(250, 25));
        setAllSize(picWidthT, fieldSize3);
        setAllSize(picHeightT, fieldSize3);
        setAllSize(searchParamPane, new Dimension(450, 160));
        setAllSize(srch_txtT, new Dimension(200, 25));
        setAllSize(srch_txtKzT, new Dimension(200, 25));
        setAllSize(ifc_uidT, new Dimension(200, 25));
        setAllSize(chatSearchParamPane, new Dimension(450, 110));
        setAllSize(chat_srch_txtT, new Dimension(200, 25));
        setAllSize(chat_srch_txtKzT, new Dimension(200, 25));
        setAllSize(notificationSoundPane, new Dimension(450, 100));
        setAllSize(selSoundT, new Dimension(250, 25));
        setAllSize(transparentCellTablePane, grPaneSize);
        setAllSize(transparentBackTabTitlePane, grPaneSize);
        setAllSize(transparentSelectedTabTitlePane, grPaneSize);
        setAllSize(sysVarPane, grPaneSize2);

        // Панели цветов системных переменных
        setAllSize(colorMainPane, grPaneSize);
        setAllSize(blueSysColorPane, grPaneSize);
        setAllSize(darkShadowSysColorPane, grPaneSize);
        setAllSize(midSysColorPane, grPaneSize);
        setAllSize(lightYellowColorPane, grPaneSize);
        setAllSize(redColorPane, grPaneSize);
        setAllSize(lightRedColorPane, grPaneSize);
        setAllSize(lightGreenColorPane, grPaneSize);
        setAllSize(shadowYellowColorPane, grPaneSize);
        setAllSize(sysColorPane, grPaneSize);
        setAllSize(lightSysColorPane, grPaneSize);
        setAllSize(defaultFontColorPane, grPaneSize);
        setAllSize(silverColorPane, grPaneSize);
        setAllSize(shadowsGreyColorPane, grPaneSize);
        setAllSize(keywordColorPane, grPaneSize);
        setAllSize(variableColorPane, grPaneSize);
        setAllSize(clientVariableColorPane, grPaneSize);
        setAllSize(commentColorPane, grPaneSize);

        setAllSize(colorMainPane2, colorPaneSize);
        setAllSize(blueSysColorPane2, colorPaneSize);
        setAllSize(darkShadowSysColorPane2, colorPaneSize);
        setAllSize(midSysColorPane2, colorPaneSize);
        setAllSize(lightYellowColorPane2, colorPaneSize);
        setAllSize(redColorPane2, colorPaneSize);
        setAllSize(lightRedColorPane2, colorPaneSize);
        setAllSize(lightGreenColorPane2, colorPaneSize);
        setAllSize(shadowYellowColorPane2, colorPaneSize);
        setAllSize(sysColorPane2, colorPaneSize);
        setAllSize(lightSysColorPane2, colorPaneSize);
        setAllSize(defaultFontColorPane2, colorPaneSize);
        setAllSize(silverColorPane2, colorPaneSize);
        setAllSize(shadowsGreyColorPane2, colorPaneSize);
        setAllSize(keywordColorPane2, colorPaneSize);
        setAllSize(variableColorPane2, colorPaneSize);
        setAllSize(clientVariableColorPane2, colorPaneSize);
        setAllSize(commentColorPane2, colorPaneSize);
        // кнопки
        setAllSize(gradientMainFrameBtn, btnSize);
        setAllSize(gradientControlPanelBtn, btnSize);
        setAllSize(gradientMenuPanelBtn, btnSize);
        setAllSize(gradientFieldNOFLCBtn, btnSize);
        setAllSize(colorHeaderTableBtn, btnSize);
        setAllSize(colorTabTitleBtn, btnSize);
        setAllSize(colorBackTabTitleBtn, btnSize);
        setAllSize(colorFontTabTitleBtn, btnSize);
        setAllSize(colorFontBackTabTitleBtn, btnSize);
        
        setAllSize(colorMainBtn, btnSize);
        setAllSize(blueSysColorBtn, btnSize);
        setAllSize(darkShadowSysColorBtn, btnSize);
        setAllSize(midSysColorBtn, btnSize);
        setAllSize(lightYellowColorBtn, btnSize);
        setAllSize(redColorBtn, btnSize);
        setAllSize(lightRedColorBtn, btnSize);
        setAllSize(lightGreenColorBtn, btnSize);
        setAllSize(shadowYellowColorBtn, btnSize);
        setAllSize(sysColorBtn, btnSize);
        setAllSize(lightSysColorBtn, btnSize);
        setAllSize(defaultFontColorBtn, btnSize);
        setAllSize(silverColorBtn, btnSize);
        setAllSize(shadowsGreyColorBtn, btnSize);
        setAllSize(keywordColorBtn, btnSize);
        setAllSize(variableColorBtn, btnSize);
        setAllSize(clientVariableColorBtn, btnSize);
        setAllSize(commentColorBtn, btnSize);

        setAllSize(cleanObjectBrowserLimitForClassesBtn, new Dimension(300, 25));
        setAllSize(objectBrowserLimitField, fieldSize3);

        setAllSize(gradientMainFrameField, btnSize2);
        setAllSize(gradientControlPanelField, btnSize2);
        setAllSize(gradientMenuPanelField, btnSize2);
        setAllSize(transparentCellTableField, fieldSize3);
        setAllSize(colorHeaderTableField, btnSize);
        setAllSize(colorTabTitleField, btnSize);
        setAllSize(colorBackTabTitleField, btnSize);
        setAllSize(colorFontTabTitleField, btnSize);
        setAllSize(colorFontBackTabTitleField, btnSize);
        setAllSize(transparentBackTabTitleField, fieldSize3);
        setAllSize(transparentSelectedTabTitleField, fieldSize3);
        setAllSize(gradientFieldNOFLCField, btnSize);
        
        setAllSize(colorMainField, btnSize);
        setAllSize(blueSysColorField, btnSize);
        setAllSize(darkShadowSysColorField, btnSize);
        setAllSize(midSysColorField, btnSize);
        setAllSize(lightYellowColorField, btnSize);
        setAllSize(redColorField, btnSize);
        setAllSize(lightRedColorField, btnSize);
        setAllSize(lightGreenColorField, btnSize);
        setAllSize(shadowYellowColorField, btnSize);
        setAllSize(sysColorField, btnSize);
        setAllSize(lightSysColorField, btnSize);
        setAllSize(defaultFontColorField, btnSize);
        setAllSize(silverColorField, btnSize);
        setAllSize(shadowsGreyColorField, btnSize);
        setAllSize(keywordColorField, btnSize);
        setAllSize(variableColorField, btnSize);
        setAllSize(clientVariableColorField, btnSize);
        setAllSize(commentColorField, btnSize);

        refreshUUID.addActionListener(this);
        refreshUUIDF.addActionListener(this);
        
        objectBrowserLimitField.addActionListener(this);
        
        transparentCellTableField.addActionListener(this);
        colorHeaderTableField.addActionListener(this);
        colorTabTitleField.addActionListener(this);
        colorBackTabTitleField.addActionListener(this);
        colorFontTabTitleField.addActionListener(this);
        colorFontBackTabTitleField.addActionListener(this);
        transparentBackTabTitleField.addActionListener(this);
        transparentSelectedTabTitleField.addActionListener(this);

        gradientMainFrameEnabledCheck.addActionListener(this);
        gradientControlPanelEnabledCheck.addActionListener(this);
        gradientMenuPanelEnabledCheck.addActionListener(this);
        gradientFieldNOFLCEnabledCheck.addActionListener(this);
        
        colorMainField.addActionListener(this);
        blueSysColorField.addActionListener(this);
        darkShadowSysColorField.addActionListener(this);
        midSysColorField.addActionListener(this);
        lightYellowColorField.addActionListener(this);
        redColorField.addActionListener(this);
        lightRedColorField.addActionListener(this);
        lightGreenColorField.addActionListener(this);
        shadowYellowColorField.addActionListener(this);
        sysColorField.addActionListener(this);
        lightSysColorField.addActionListener(this);
        defaultFontColorField.addActionListener(this);
        silverColorField.addActionListener(this);
        shadowsGreyColorField.addActionListener(this);
        keywordColorField.addActionListener(this);
        variableColorField.addActionListener(this);
        clientVariableColorField.addActionListener(this);
        commentColorField.addActionListener(this);

        transparentCellTableSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, conf.getTransparentCellTable());
        transparentCellTableSlider.setMajorTickSpacing(50);
        transparentCellTableSlider.setMinorTickSpacing(10);
        transparentCellTableSlider.setPaintTicks(true);
        transparentCellTableSlider.setPaintLabels(true);
        transparentCellTableSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent arg0) {
                transparentCellTableField.setText(String.valueOf(transparentCellTableSlider.getValue()));
                transparentCellTable = transparentCellTableSlider.getValue();
                transparentCellTablePane.setTransparent(transparentCellTable);
                isTransparentCellTable = true;
            }
        });

        transparentBackTabTitleSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, conf.getTransparentBackTabTitle());
        transparentBackTabTitleSlider.setMajorTickSpacing(50);
        transparentBackTabTitleSlider.setMinorTickSpacing(10);
        transparentBackTabTitleSlider.setPaintTicks(true);
        transparentBackTabTitleSlider.setPaintLabels(true);
        transparentBackTabTitleSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent arg0) {
                transparentBackTabTitleField.setText(String.valueOf(transparentBackTabTitleSlider.getValue()));
                transparentBackTabTitle = transparentBackTabTitleSlider.getValue();
                transparentBackTabTitlePane.setTransparent(transparentBackTabTitle);
                isTransparentBackTabTitle = true;
            }
        });

        transparentSelectedTabTitleSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, conf.getTransparentSelectedTabTitle());
        transparentSelectedTabTitleSlider.setMajorTickSpacing(50);
        transparentSelectedTabTitleSlider.setMinorTickSpacing(10);
        transparentSelectedTabTitleSlider.setPaintTicks(true);
        transparentSelectedTabTitleSlider.setPaintLabels(true);
        transparentSelectedTabTitleSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent arg0) {
                transparentSelectedTabTitleField.setText(String.valueOf(transparentSelectedTabTitleSlider.getValue()));
                transparentSelectedTabTitle = transparentSelectedTabTitleSlider.getValue();
                transparentSelectedTabTitlePane.setTransparent(transparentSelectedTabTitle);
                isTransparentSelectedTabTitle = true;
            }
        });

        int y = 0;
        GridBagConstraints cnst = new GridBagConstraints(0, y, 1, 1, 1, 1, CENTER, BOTH, INSETS_1, 0, 0);
        if (isGlobal) {
            add(leftScroller, cnst);
            cnst.gridx = ++y;
            add(centerScroller, cnst);
            cnst.gridx = ++y;
            add(rightScroller, cnst);
        } else {
            add(centerScroller, cnst);
            cnst.gridy = ++y;
            add(rightScroller, cnst);
        }

        y = 0;
        cnst = new GridBagConstraints(0, y, 1, 1, 0, 0, NORTHWEST, NONE, INSETS_1, 0, 0);
        leftPanel.add(ObjectBrowserViewPane, cnst);
        cnst.gridy = ++y;
        leftPanel.add(classControlPane, cnst);
        cnst.gridy = ++y;
        leftPanel.add(uiControlPane, cnst);
        cnst.gridy = ++y;
        leftPanel.add(uiOpsForEGKNPane, cnst);
        cnst.gridy = ++y;
        leftPanel.add(procOpsForEGKNPane, cnst);
        cnst.gridy = ++y;
        leftPanel.add(logotypePicsPane, cnst);
        cnst.gridy = ++y;
        leftPanel.add(searchParamPane, cnst);
        cnst.gridy = ++y;
        leftPanel.add(chatSearchParamPane, cnst);
        cnst.gridy = ++y;
        leftPanel.add(notificationSoundPane, cnst);
        leftPanel.add(new JLabel(), new GridBagConstraints(0, ++y, 1, 1, 1, 1, CENTER, BOTH, Constants.INSETS_0, 0, 0));
        cnst.gridy = y = 0;
        if (isGlobal) {
            centerPanel.add(gradientMainFramePane, cnst);
            cnst.gridy = ++y;
        }
        centerPanel.add(gradientControlPanelPane, cnst);
        cnst.gridy = ++y;
        centerPanel.add(gradientMenuPanelPane, cnst);
        cnst.gridy = ++y;
        if (isGlobal) {
            centerPanel.add(colorMainPane, cnst);
            cnst.gridy = ++y;
            centerPanel.add(transparentMainCheck, cnst);
            cnst.gridy = ++y;
            centerPanel.add(transparentDialogCheck, cnst);
            cnst.gridy = ++y;
        }
        centerPanel.add(gradientFieldNOFLCPane, cnst);
        cnst.gridy = ++y;
        if (isGlobal) {
            centerPanel.add(colorHeaderTablePane, cnst);
            cnst.gridy = ++y;
            centerPanel.add(colorTabTitlePane, cnst);
            cnst.gridy = ++y;
            centerPanel.add(colorBackTabTitlePane, cnst);
            cnst.gridy = ++y;
            centerPanel.add(colorFontTabTitlePane, cnst);
            cnst.gridy = ++y;
            centerPanel.add(colorFontBackTabTitlePane, cnst);
            cnst.gridy = ++y;
            centerPanel.add(transparentCellTablePane, cnst);
            cnst.gridy = ++y;
            centerPanel.add(transparentBackTabTitlePane, cnst);
            cnst.gridy = ++y;
            centerPanel.add(transparentSelectedTabTitlePane, cnst);
        }
        cnst.gridy = y = 0;
        rightPanel.add(sysVarPane, cnst);

        classControlPane.add(refreshUUID, new GridBagConstraints(0, 0, 1, 1, 1, 0, WEST, NONE, INSETS_2, 0, 0));
        classControlPane.add(refreshUUIDF, new GridBagConstraints(1, 0, 1, 1, 1, 0, EAST, NONE, INSETS_2, 0, 0));
        uiControlPane.add(isDataLog, new GridBagConstraints(1, 0, 1, 1, 1, 0, WEST, NONE, INSETS_2, 0, 0));
        uiControlPane.add(generateHtmlTemplates, new GridBagConstraints(1, 1, 1, 1, 1, 0, WEST, NONE, INSETS_2, 0, 0));
        
        uiOpsForEGKNPane.add(resaveAllIfcsForEGKNH, new GridBagConstraints(0, 0, 1, 1, 1, 0, WEST, NONE, INSETS_2, 0, 0));
        uiOpsForEGKNPane.add(resaveIfcForEGKNH, new GridBagConstraints(0, 1, 1, 1, 1, 0, WEST, NONE, INSETS_2, 0, 0));
        uiOpsForEGKNPane.add(selectUI, new GridBagConstraints(1, 1, 1, 1, 1, 0, WEST, NONE, INSETS_2, 0, 0));

        procOpsForEGKNPane.add(resaveAllProcsForEGKNH, new GridBagConstraints(0, 0, 1, 1, 1, 0, WEST, NONE, INSETS_2, 0, 0));
        procOpsForEGKNPane.add(resaveProcForEGKNH, new GridBagConstraints(0, 1, 1, 1, 1, 0, WEST, NONE, INSETS_2, 0, 0));
        procOpsForEGKNPane.add(selectProc, new GridBagConstraints(1, 1, 1, 1, 1, 0, WEST, NONE, INSETS_2, 0, 0));
        
        logotypePicsPane.add(selectPic, new GridBagConstraints(0, 0, 1, 1, 1, 0, WEST, NONE, INSETS_2, 0, 0));
        logotypePicsPane.add(selPicT, new GridBagConstraints(1, 0, 2, 1, 1, 0, WEST, NONE, INSETS_2, 0, 0));
        logotypePicsPane.add(picWidthL, new GridBagConstraints(0, 1, 1, 1, 1, 0, WEST, NONE, INSETS_2, 0, 0));
        logotypePicsPane.add(picWidthT, new GridBagConstraints(1, 1, 1, 1, 1, 0, WEST, NONE, INSETS_2, 0, 0));
        logotypePicsPane.add(picHeightL, new GridBagConstraints(0, 2, 1, 1, 1, 0, WEST, NONE, INSETS_2, 0, 0));
        logotypePicsPane.add(picHeightT, new GridBagConstraints(1, 2, 1, 1, 1, 0, WEST, NONE, INSETS_2, 0, 0));
        logotypePicsPane.add(picLabel, new GridBagConstraints(2, 1, 1, 2, 1, 0, WEST, NONE, INSETS_2, 0, 0));
        logotypePicsPane.add(resaveLogotypePic, new GridBagConstraints(0, 3, 2, 1, 1, 0, WEST, NONE, INSETS_2, 0, 0));
        logotypePicsPane.add(removePic, new GridBagConstraints(1, 3, 2, 1, 1, 0, WEST, NONE, INSETS_2, 0, 0));
        selPicT.setEnabled(false);
        
        int yPos = 0;
        searchParamPane.add(showSearchField, new GridBagConstraints(0, yPos, 2, 1, 1, 0, WEST, NONE, INSETS_2, 0, 0));
        yPos++;
        searchParamPane.add(srch_txtL, new GridBagConstraints(0, yPos, 1, 1, 1, 0, WEST, NONE, INSETS_2, 0, 0));
        searchParamPane.add(srch_txtT, new GridBagConstraints(1, yPos, 1, 1, 1, 0, WEST, NONE, INSETS_2, 0, 0));
        yPos++;
        searchParamPane.add(srch_txtKzL, new GridBagConstraints(0, yPos, 1, 1, 1, 0, WEST, NONE, INSETS_2, 0, 0));
        searchParamPane.add(srch_txtKzT, new GridBagConstraints(1, yPos, 1, 1, 1, 0, WEST, NONE, INSETS_2, 0, 0));
        yPos++;
        searchParamPane.add(ifc_uidL, new GridBagConstraints(0, yPos, 1, 1, 1, 0, WEST, NONE, INSETS_2, 0, 0));
        searchParamPane.add(ifc_uidT, new GridBagConstraints(1, yPos, 1, 1, 1, 0, WEST, NONE, INSETS_2, 0, 0));
        yPos++;
        searchParamPane.add(resaveSearchParam, new GridBagConstraints(0, yPos, 1, 1, 1, 0, WEST, NONE, INSETS_2, 0, 0));
        
        yPos = 0;        
        chatSearchParamPane.add(chat_srch_txtL, new GridBagConstraints(0, yPos, 1, 1, 1, 0, WEST, NONE, INSETS_2, 0, 0));
        chatSearchParamPane.add(chat_srch_txtT, new GridBagConstraints(1, yPos, 1, 1, 1, 0, WEST, NONE, INSETS_2, 0, 0));
        yPos++;
        chatSearchParamPane.add(chat_srch_txtKzL, new GridBagConstraints(0, yPos, 1, 1, 1, 0, WEST, NONE, INSETS_2, 0, 0));
        chatSearchParamPane.add(chat_srch_txtKzT, new GridBagConstraints(1, yPos, 1, 1, 1, 0, WEST, NONE, INSETS_2, 0, 0));
        yPos++;
        chatSearchParamPane.add(resaveChatSearchParam, new GridBagConstraints(0, yPos, 1, 1, 1, 0, WEST, NONE, INSETS_2, 0, 0));
        
        notificationSoundPane.add(useNotificationSound, new GridBagConstraints(0, 0, 3, 1, 1, 0, WEST, NONE, INSETS_2, 0, 0));
        notificationSoundPane.add(selectSound, new GridBagConstraints(0, 1, 1, 1, 1, 0, WEST, NONE, INSETS_2, 0, 0));
        notificationSoundPane.add(selSoundT, new GridBagConstraints(1, 1, 2, 1, 1, 0, WEST, NONE, INSETS_2, 0, 0));
        notificationSoundPane.add(resaveNoteSound, new GridBagConstraints(0, 2, 1, 1, 1, 0, WEST, NONE, INSETS_2, 0, 0));
        notificationSoundPane.add(playNoteSound, new GridBagConstraints(1, 2, 1, 1, 1, 0, WEST, NONE, INSETS_2, 0, 0));
        notificationSoundPane.add(removeNoteSound, new GridBagConstraints(2, 2, 1, 1, 1, 0, WEST, NONE, INSETS_2, 0, 0));
        selSoundT.setEditable(false);
        
        isDataLog.setSelected(Kernel.instance().isDataLog());
        
        // активаторы
        cnst = new GridBagConstraints(0, 0, 1, 1, 1, 0, WEST, NONE, INSETS_2, 0, 0);
        gradientMainFramePane.add(gradientMainFrameEnabledCheck, cnst);
        gradientControlPanelPane.add(gradientControlPanelEnabledCheck, cnst);
        gradientMenuPanelPane.add(gradientMenuPanelEnabledCheck, cnst);
        gradientFieldNOFLCPane.add(gradientFieldNOFLCEnabledCheck, cnst);

        // Управляющие элементы,
        cnst.gridx = 1;
        gradientMainFramePane.add(gradientMainFrameBtn, cnst);
        gradientControlPanelPane.add(gradientControlPanelBtn, cnst);
        gradientMenuPanelPane.add(gradientMenuPanelBtn, cnst);
        gradientFieldNOFLCPane.add(gradientFieldNOFLCBtn, cnst);
        colorHeaderTablePane.add(colorHeaderTableBtn, cnst);
        colorTabTitlePane.add(colorTabTitleBtn, cnst);
        colorBackTabTitlePane.add(colorBackTabTitleBtn, cnst);
        colorFontTabTitlePane.add(colorFontTabTitleBtn, cnst);
        colorFontBackTabTitlePane.add(colorFontBackTabTitleBtn, cnst);
        cnst.fill = HORIZONTAL;
        transparentCellTablePane.add(transparentCellTableSlider, cnst);
        transparentBackTabTitlePane.add(transparentBackTabTitleSlider, cnst);
        transparentSelectedTabTitlePane.add(transparentSelectedTabTitleSlider, cnst);

        // поля для ввода вручную
        cnst.fill = NONE;
        cnst.gridx = 2;
        transparentCellTablePane.add(transparentCellTableField, cnst);
        transparentBackTabTitlePane.add(transparentBackTabTitleField, cnst);
        transparentSelectedTabTitlePane.add(transparentSelectedTabTitleField, cnst);

        gradientMainFramePane.add(gradientMainFrameField, cnst);
        gradientControlPanelPane.add(gradientControlPanelField, cnst);
        gradientMenuPanelPane.add(gradientMenuPanelField, cnst);
        gradientFieldNOFLCPane.add(gradientFieldNOFLCField, cnst);
        colorHeaderTablePane.add(colorHeaderTableField, cnst);
        colorTabTitlePane.add(colorTabTitleField, cnst);
        colorBackTabTitlePane.add(colorBackTabTitleField, cnst);
        colorFontTabTitlePane.add(colorFontTabTitleField, cnst);
        colorFontBackTabTitlePane.add(colorFontBackTabTitleField, cnst);

        // Конфигуратор редактора классов
        cnst = new GridBagConstraints(0, 0, 2, 1, 1, 1, WEST, NONE, INSETS_1, 0, 0);
        ObjectBrowserViewPane.add(isObjectBrowserLimitCheck, cnst);
        cnst.gridy = ++y;
        ObjectBrowserViewPane.add(isObjectBrowserLimitForClassesCheck, cnst);
        cnst.gridy = ++y;
        ObjectBrowserViewPane.add(cleanObjectBrowserLimitForClassesBtn, cnst);
        cnst.gridy = ++y;
        cnst.gridwidth = 1;
        cnst.weightx = 0;
        ObjectBrowserViewPane.add(new AdditLabel("Лимит отображения объектов "), cnst);
        cnst.gridx = 1;
        cnst.weightx = 1;
        ObjectBrowserViewPane.add(objectBrowserLimitField, cnst);

        // Компоновка панели системных переменных
        cnst = new GridBagConstraints(0, 0, 2, 1, 0, 0, CENTER, BOTH, INSETS_1, 0, 0);

        sysVarPane.add(colorMainPane, cnst);
        cnst.gridy = ++y;
        sysVarPane.add(blueSysColorPane, cnst);
        cnst.gridy = ++y;
        sysVarPane.add(darkShadowSysColorPane, cnst);
        cnst.gridy = ++y;
        sysVarPane.add(midSysColorPane, cnst);
        cnst.gridy = ++y;
        sysVarPane.add(lightYellowColorPane, cnst);
        cnst.gridy = ++y;
        sysVarPane.add(redColorPane, cnst);
        cnst.gridy = ++y;
        sysVarPane.add(lightRedColorPane, cnst);
        cnst.gridy = ++y;
        sysVarPane.add(lightGreenColorPane, cnst);
        cnst.gridy = ++y;
        sysVarPane.add(shadowYellowColorPane, cnst);
        cnst.gridy = ++y;
        sysVarPane.add(sysColorPane, cnst);
        cnst.gridy = ++y;
        sysVarPane.add(lightSysColorPane, cnst);
        cnst.gridy = ++y;
        sysVarPane.add(defaultFontColorPane, cnst);
        cnst.gridy = ++y;
        sysVarPane.add(silverColorPane, cnst);
        cnst.gridy = ++y;
        sysVarPane.add(shadowsGreyColorPane, cnst);
        cnst.gridy = ++y;
        sysVarPane.add(keywordColorPane, cnst);
        cnst.gridy = ++y;
        sysVarPane.add(variableColorPane, cnst);
        cnst.gridy = ++y;
        sysVarPane.add(clientVariableColorPane, cnst);
        cnst.gridy = ++y;
        sysVarPane.add(commentColorPane, cnst);

        GridBagConstraints cnst1 = new GridBagConstraints(0, 0, 1, 1, 1, 0, WEST, NONE, INSETS_2, 0, 0);
        GridBagConstraints cnst2 = new GridBagConstraints(1, 0, 1, 1, 0, 0, WEST, NONE, INSETS_2, 0, 0);
        GridBagConstraints cnst3 = new GridBagConstraints(2, 0, 1, 1, 1, 0, WEST, NONE, INSETS_2, 1, 0);
        GridBagConstraints cnst4 = new GridBagConstraints(3, 0, 1, 1, 1, 0, WEST, NONE, INSETS_2, 1, 0);

        colorMainPane.add(colorMainCheck, cnst1);
        colorMainPane.add(colorMainBtn, cnst2);
        colorMainPane.add(colorMainField, cnst3);
        colorMainPane.add(colorMainPane2, cnst4);
        
        blueSysColorPane.add(blueSysColorCheck, cnst1);
        blueSysColorPane.add(blueSysColorBtn, cnst2);
        blueSysColorPane.add(blueSysColorField, cnst3);
        blueSysColorPane.add(blueSysColorPane2, cnst4);

        darkShadowSysColorPane.add(darkShadowSysColorCheck, cnst1);
        darkShadowSysColorPane.add(darkShadowSysColorBtn, cnst2);
        darkShadowSysColorPane.add(darkShadowSysColorField, cnst3);
        darkShadowSysColorPane.add(darkShadowSysColorPane2, cnst4);

        midSysColorPane.add(midSysColorCheck, cnst1);
        midSysColorPane.add(midSysColorBtn, cnst2);
        midSysColorPane.add(midSysColorField, cnst3);
        midSysColorPane.add(midSysColorPane2, cnst4);

        lightYellowColorPane.add(lightYellowColorCheck, cnst1);
        lightYellowColorPane.add(lightYellowColorBtn, cnst2);
        lightYellowColorPane.add(lightYellowColorField, cnst3);
        lightYellowColorPane.add(lightYellowColorPane2, cnst4);

        redColorPane.add(redColorCheck, cnst1);
        redColorPane.add(redColorBtn, cnst2);
        redColorPane.add(redColorField, cnst3);
        redColorPane.add(redColorPane2, cnst4);

        lightRedColorPane.add(lightRedColorCheck, cnst1);
        lightRedColorPane.add(lightRedColorBtn, cnst2);
        lightRedColorPane.add(lightRedColorField, cnst3);
        lightRedColorPane.add(lightRedColorPane2, cnst4);

        lightGreenColorPane.add(lightGreenColorCheck, cnst1);
        lightGreenColorPane.add(lightGreenColorBtn, cnst2);
        lightGreenColorPane.add(lightGreenColorField, cnst3);
        lightGreenColorPane.add(lightGreenColorPane2, cnst4);

        shadowYellowColorPane.add(shadowYellowColorCheck, cnst1);
        shadowYellowColorPane.add(shadowYellowColorBtn, cnst2);
        shadowYellowColorPane.add(shadowYellowColorField, cnst3);
        shadowYellowColorPane.add(shadowYellowColorPane2, cnst4);

        sysColorPane.add(sysColorCheck, cnst1);
        sysColorPane.add(sysColorBtn, cnst2);
        sysColorPane.add(sysColorField, cnst3);
        sysColorPane.add(sysColorPane2, cnst4);

        lightSysColorPane.add(lightSysColorCheck, cnst1);
        lightSysColorPane.add(lightSysColorBtn, cnst2);
        lightSysColorPane.add(lightSysColorField, cnst3);
        lightSysColorPane.add(lightSysColorPane2, cnst4);

        defaultFontColorPane.add(defaultFontColorCheck, cnst1);
        defaultFontColorPane.add(defaultFontColorBtn, cnst2);
        defaultFontColorPane.add(defaultFontColorField, cnst3);
        defaultFontColorPane.add(defaultFontColorPane2, cnst4);

        silverColorPane.add(silverColorCheck, cnst1);
        silverColorPane.add(silverColorBtn, cnst2);
        silverColorPane.add(silverColorField, cnst3);
        silverColorPane.add(silverColorPane2, cnst4);

        shadowsGreyColorPane.add(shadowsGreyColorCheck, cnst1);
        shadowsGreyColorPane.add(shadowsGreyColorBtn, cnst2);
        shadowsGreyColorPane.add(shadowsGreyColorField, cnst3);
        shadowsGreyColorPane.add(shadowsGreyColorPane2, cnst4);

        keywordColorPane.add(keywordColorCheck, cnst1);
        keywordColorPane.add(keywordColorBtn, cnst2);
        keywordColorPane.add(keywordColorField, cnst3);
        keywordColorPane.add(keywordColorPane2, cnst4);

        variableColorPane.add(variableColorCheck, cnst1);
        variableColorPane.add(variableColorBtn, cnst2);
        variableColorPane.add(variableColorField, cnst3);
        variableColorPane.add(variableColorPane2, cnst4);

        clientVariableColorPane.add(clientVariableColorCheck, cnst1);
        clientVariableColorPane.add(clientVariableColorBtn, cnst2);
        clientVariableColorPane.add(clientVariableColorField, cnst3);
        clientVariableColorPane.add(clientVariableColorPane2, cnst4);

        commentColorPane.add(commentColorCheck, cnst1);
        commentColorPane.add(commentColorBtn, cnst2);
        commentColorPane.add(commentColorField, cnst3);
        commentColorPane.add(commentColorPane2, cnst4);

        reloadConfig();
    }
    
    private void setIconPic(byte[] img) {
    	if(img != null && img.length > 0) {
			int pic_wd;
			int pic_ht;
			try { 
			pic_wd = Integer.parseInt(picWidthT.getText());
			} catch(NumberFormatException e) {
				pic_wd = 150;
			}
			try {
			pic_ht = Integer.parseInt(picHeightT.getText());
			} catch (NumberFormatException e) {
				pic_ht = 60;
			}
			BufferedImage bufImg = Utils.resize(img, pic_wd, pic_ht);
			picLabel.setIcon(new ImageIcon(bufImg));
		}
    }
    
    private void putCurrVal() {
    	Kernel krn = Kernel.instance();
    	try {
    		KrnClass cls = krn.getClassByName("ConfigGlobal");
    		KrnObject[] krnObjs = krn.getClassOwnObjects(cls, 0);
    		if(krnObjs != null && krnObjs.length > 0) {
    			String attrName;
    			long langId;
    			attrName = "logoPicWidth";
    			long[] pic_widths = krn.getLongs(krnObjs[0], attrName, 0);
    			if(pic_widths != null && pic_widths.length > 0) {
    				long pic_width = pic_widths[0];
    				picWidthT.setText(pic_width + "");
    			}
    			
    			attrName = "logoPicHeight";
    			long[] pic_heights = krn.getLongs(krnObjs[0], attrName, 0);
    			if(pic_heights != null && pic_heights.length > 0) {
    				long pic_height = pic_heights[0];
    				picHeightT.setText(pic_height + "");
    			}
    			
    			attrName = "logotypePic";
    			iconPic = krn.getBlob(krnObjs[0], attrName, 0, 0, 0);
    			if(iconPic != null && iconPic.length > 0) {
    				setIconPic(iconPic);
    			}

    			attrName = "srch_txt";
    			langId = krn.getLangIdByCode("RU");
    			String[] srch_txts = krn.getStrings(krnObjs[0], attrName, langId, 0);
    			if(srch_txts != null && srch_txts.length > 0) {
    				String srch_txt = srch_txts[0];
    				srch_txtT.setText(srch_txt);
    			}
    			langId = krn.getLangIdByCode("KZ");
    			srch_txts = krn.getStrings(krnObjs[0], attrName, langId, 0);
    			if(srch_txts != null && srch_txts.length > 0) {
    				String srch_txt = srch_txts[0];
    				srch_txtKzT.setText(srch_txt);
    			}


    			attrName = "ifc_uid";
    			String[] ifc_uids = krn.getStrings(krnObjs[0], attrName, 0, 0);
    			if(ifc_uids != null && ifc_uids.length > 0) {
    				String ifc_uid = ifc_uids[0];
    				ifc_uidT.setText(ifc_uid);
    			}

    			attrName = "showSearchField";
    			long showSrchFldL[] = krn.getLongs(krnObjs[0], attrName, 0);
    			if(showSrchFldL != null && showSrchFldL.length > 0) {
    				boolean showSrchFld = showSrchFldL[0] == 1 ? true : false;
    				showSearchField.setSelected(showSrchFld);
    			}
    			
    			attrName = "chat_srch_txt";
    			langId = krn.getLangIdByCode("RU");
    			srch_txts = krn.getStrings(krnObjs[0], attrName, langId, 0);
    			if(srch_txts != null && srch_txts.length > 0) {
    				String srch_txt = srch_txts[0];
    				chat_srch_txtT.setText(srch_txt);
    			}
    			langId = krn.getLangIdByCode("KZ");
    			srch_txts = krn.getStrings(krnObjs[0], attrName, langId, 0);
    			if(srch_txts != null && srch_txts.length > 0) {
    				String srch_txt = srch_txts[0];
    				chat_srch_txtKzT.setText(srch_txt);
    			}
    			
    			attrName = "useNotificationSound";
    			long useNoteSoundL[] = krn.getLongs(krnObjs[0], attrName, 0);
    			if(useNoteSoundL != null && useNoteSoundL.length > 0) {
    				boolean useNoteSound = useNoteSoundL[0] == 1 ? true : false;
    				useNotificationSound.setSelected(useNoteSound);
    			}
    			
    			attrName = "notificationSound";
    			noteSound = krn.getBlob(krnObjs[0], attrName, 0, 0, 0);
    			boolean hasNoteSound = noteSound != null && noteSound.length > 0;    		
    			playNoteSound.setEnabled(hasNoteSound);
    			removeNoteSound.setEnabled(hasNoteSound);
    			useNotificationSound.setEnabled(hasNoteSound);
    		}

    	} catch (KrnException e1) {
    		// TODO Auto-generated catch block
    		e1.printStackTrace();
    	}
    }

    
    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        OrGradientColorChooser gcch = null;
        OrColorChooser cch = null;
        DesignerDialog dlg = null;
        final String grTitle = "Выбор градиентной заливки";
        final String clTitle = "Выбор цвета заливки";
        if (obj.equals(gradientMainFrameBtn)) {
            gcch = gradientMainFrame == null ? new OrGradientColorChooser() : new OrGradientColorChooser(gradientMainFrame);
            dlg = new DesignerDialog(Or3Frame.instance(), grTitle, gcch, false, true);
            dlg.show();
            if (dlg.isOK()) {
                gradientMainFrame = gcch.getGradient();
            } else if (dlg.getResult() == BUTTON_DEFAULT) {
                gradientMainFrame.setGradient(null);
            }
            if (dlg.isOK() || dlg.getResult() == BUTTON_DEFAULT) {
                gradientMainFramePane.setGradient(gradientMainFrame);
                gradientMainFrameField.setText(gradientMainFrame.toString());
                gradientMainFrameEnabledCheck.setSelected(gradientMainFrame.isEnabled());
                isChangeGradientMainFrame = true;
            }

        } else if (obj.equals(gradientControlPanelBtn)) {
            gcch = gradientControlPanel == null ? new OrGradientColorChooser() : new OrGradientColorChooser(gradientControlPanel);
            dlg = new DesignerDialog(Or3Frame.instance(), grTitle, gcch, false, true);
            dlg.show();
            if (dlg.isOK()) {
                gradientControlPanel = gcch.getGradient();
            } else if (dlg.getResult() == BUTTON_DEFAULT) {
                gradientControlPanel.setGradient(null);
            }
            if (dlg.isOK() || dlg.getResult() == BUTTON_DEFAULT) {
                gradientControlPanelPane.setGradient(gradientControlPanel);
                gradientControlPanelField.setText(gradientControlPanel.toString());
                gradientControlPanelEnabledCheck.setSelected(gradientControlPanel.isEnabled());
                isChangeGradientControlPanel = true;
            }
        } else if (obj.equals(gradientMenuPanelBtn)) {
            gcch = gradientMenuPanel == null ? new OrGradientColorChooser() : new OrGradientColorChooser(gradientMenuPanel);
            dlg = new DesignerDialog(Or3Frame.instance(), grTitle, gcch, false, true);
            dlg.show();
            if (dlg.isOK()) {
                gradientMenuPanel = gcch.getGradient();
            } else if (dlg.getResult() == BUTTON_DEFAULT) {
                gradientMenuPanel.setGradient(null);
            }
            if (dlg.isOK() || dlg.getResult() == BUTTON_DEFAULT) {
                gradientMenuPanelPane.setGradient(gradientMenuPanel);
                gradientMenuPanelField.setText(gradientMenuPanel.toString());
                gradientMenuPanelEnabledCheck.setSelected(gradientMenuPanel.isEnabled());
                isChangeGradientMenuPanel = true;
            }
        } else if (obj.equals(colorMainBtn)) {
            cch = new OrColorChooser(mainColor);
            dlg = new DesignerDialog(Or3Frame.instance(), clTitle, cch, false, true);
            dlg.show();
            if (dlg.isOK()) {
                mainColor = cch.getColor();
            } else if (dlg.getResult() == BUTTON_DEFAULT) {
                mainColor = MAIN_COLOR;
            }
            if (dlg.isOK() || dlg.getResult() == BUTTON_DEFAULT) {
                colorMainField.setText(mainColor.getRGB() + (colorMainCheck.isSelected() ? " 1" : " 0"));
                colorMainPane2.setOpaque(isColorActive(new ColorAct(mainColor, colorMainCheck.isSelected())));
                colorMainPane2.setBackground(mainColor);
                isChangeColorMain = true;
            }
        } else if (obj.equals(transparentMainCheck)) {
            transparentMain = transparentMainCheck.isSelected();
            setTransparent(!transparentMain);
            isChangeTransparentMain = true;
        } else if (obj.equals(transparentDialogCheck)) {
            transparentDialog = transparentDialogCheck.isSelected();
            setTransparentDialog(transparentDialog);
            isChangeTransparentDialog = true;
        } else if (obj.equals(gradientMainFrameField)) {// /
            gradientMainFrame.setGradient(gradientMainFrameField.getText());
            gradientMainFramePane.setGradient(gradientMainFrame);
            gradientMainFrameEnabledCheck.setSelected(gradientMainFrame.isEnabled());
            isChangeGradientMainFrame = true;
        } else if (obj.equals(gradientControlPanelField)) {
            gradientControlPanel.setGradient(gradientControlPanelField.getText());
            gradientControlPanelPane.setGradient(gradientControlPanel);
            gradientControlPanelEnabledCheck.setSelected(gradientControlPanel.isEnabled());
            isChangeGradientControlPanel = true;
        } else if (obj.equals(gradientMenuPanelField)) {
            gradientMenuPanel.setGradient(gradientMenuPanelField.getText());
            gradientMenuPanelPane.setGradient(gradientMenuPanel);
            gradientMenuPanelEnabledCheck.setSelected(gradientMenuPanel.isEnabled());
            isChangeGradientMenuPanel = true;
        } else if (obj.equals(colorMainField)) {
            mainColor = Color.decode(colorMainField.getText());
            colorMainPane.setBackground(mainColor);
            isChangeColorMain = true;
        } else if (obj.equals(gradientMainFrameEnabledCheck)) {
            gradientMainFrame.setEnabled(gradientMainFrameEnabledCheck.isSelected());
            gradientMainFrameField.setText(gradientMainFrame.toString());
            gradientMainFramePane.setGradient(gradientMainFrame);
            isChangeGradientMainFrame = true;
        } else if (obj.equals(gradientControlPanelEnabledCheck)) {
            gradientControlPanel.setEnabled(gradientControlPanelEnabledCheck.isSelected());
            gradientControlPanelField.setText(gradientControlPanel.toString());
            gradientControlPanelPane.setGradient(gradientControlPanel);
            isChangeGradientControlPanel = true;
        } else if (obj.equals(gradientMenuPanelEnabledCheck)) {
            gradientMenuPanel.setEnabled(gradientMenuPanelEnabledCheck.isSelected());
            gradientMenuPanelField.setText(gradientMenuPanel.toString());
            gradientMenuPanel.setGradient(gradientMenuPanel);
            isChangeGradientMenuPanel = true;
        } else if (obj.equals(colorHeaderTableBtn)) {
            cch = new OrColorChooser(colorHeaderTable);
            dlg = new DesignerDialog(Or3Frame.instance(), clTitle, cch, false, true);
            dlg.show();
            if (dlg.isOK()) {
                colorHeaderTable = cch.getColor();
            } else if (dlg.getResult() == BUTTON_DEFAULT) {
                // не задано
            }
            if (dlg.isOK() || dlg.getResult() == BUTTON_DEFAULT) {
                colorHeaderTablePane.setBackground(colorHeaderTable);
                colorHeaderTableField.setText(colorHeaderTable.getRGB() + "");
                isColorHeaderTable = true;
            }
        } else if (obj.equals(colorTabTitleBtn)) {
            cch = new OrColorChooser(colorTabTitle);
            dlg = new DesignerDialog(Or3Frame.instance(), clTitle, cch, false, true);
            dlg.show();
            if (dlg.isOK()) {
                colorTabTitle = cch.getColor();
            } else if (dlg.getResult() == BUTTON_DEFAULT) {
                // не задано
            }
            if (dlg.isOK() || dlg.getResult() == BUTTON_DEFAULT) {
                colorTabTitlePane.setBackground(colorTabTitle);
                colorTabTitleField.setText(colorTabTitle.getRGB() + "");
                isColorTabTitle = true;
            }
        } else if (obj.equals(colorBackTabTitleBtn)) {
            cch = new OrColorChooser(colorBackTabTitle);
            dlg = new DesignerDialog(Or3Frame.instance(), clTitle, cch, false, true);
            dlg.show();
            if (dlg.isOK()) {
                colorBackTabTitle = cch.getColor();
            } else if (dlg.getResult() == BUTTON_DEFAULT) {
                // не задано
            }
            if (dlg.isOK() || dlg.getResult() == BUTTON_DEFAULT) {
                colorBackTabTitlePane.setBackground(colorBackTabTitle);
                colorBackTabTitleField.setText(colorBackTabTitle.getRGB() + "");
                isColorBackTabTitle = true;
            }
        } else if (obj.equals(colorFontTabTitleBtn)) {
            cch = new OrColorChooser(colorFontTabTitle);
            dlg = new DesignerDialog(Or3Frame.instance(), clTitle, cch, false, true);
            dlg.show();
            if (dlg.isOK()) {
                colorFontTabTitle = cch.getColor();
            } else if (dlg.getResult() == BUTTON_DEFAULT) {
                // не задано
            }
            if (dlg.isOK() || dlg.getResult() == BUTTON_DEFAULT) {
                colorFontTabTitlePane.setBackground(colorFontTabTitle);
                colorFontTabTitleField.setText(colorFontTabTitle.getRGB() + "");
                isColorFontTabTitle = true;
            }
        } else if (obj.equals(colorFontBackTabTitleBtn)) {
            cch = new OrColorChooser(colorFontBackTabTitle);
            dlg = new DesignerDialog(Or3Frame.instance(), clTitle, cch, false, true);
            dlg.show();
            if (dlg.isOK()) {
                colorFontBackTabTitle = cch.getColor();
            } else if (dlg.getResult() == BUTTON_DEFAULT) {
                // не задано
            }
            if (dlg.isOK() || dlg.getResult() == BUTTON_DEFAULT) {
                colorFontBackTabTitlePane.setBackground(colorFontBackTabTitle);
                colorFontBackTabTitleField.setText(colorFontBackTabTitle.getRGB() + "");
                isColorFontBackTabTitle = true;
            }
        } else if (obj.equals(gradientFieldNOFLCBtn)) {
            gcch = gradientFieldNOFLC == null ? new OrGradientColorChooser() : new OrGradientColorChooser(gradientFieldNOFLC);
            dlg = new DesignerDialog(Or3Frame.instance(), grTitle, gcch, false, true);
            dlg.show();
            if (dlg.isOK()) {
                gradientFieldNOFLC = gcch.getGradient();
            } else if (dlg.getResult() == BUTTON_DEFAULT) {
                gradientFieldNOFLC.setGradient(null);
            }
            if (dlg.isOK() || dlg.getResult() == BUTTON_DEFAULT) {
                gradientFieldNOFLCPane.setGradient(gradientFieldNOFLC);
                gradientFieldNOFLCField.setText(gradientFieldNOFLC.toString());
                gradientFieldNOFLCEnabledCheck.setSelected(gradientFieldNOFLC.isEnabled());
                isGradientFieldNOFLC = true;
            }
        } else if (obj.equals(colorHeaderTableField)) {//
            colorHeaderTable = Color.decode(colorHeaderTableField.getText());
            colorHeaderTablePane.setBackground(colorHeaderTable);
            isColorHeaderTable = true;
        } else if (obj.equals(colorTabTitleField)) {
            colorTabTitle = Color.decode(colorTabTitleField.getText());
            colorTabTitlePane.setBackground(colorTabTitle);
            isColorTabTitle = true;
        } else if (obj.equals(colorBackTabTitleField)) {
            colorBackTabTitle = Color.decode(colorBackTabTitleField.getText());
            colorBackTabTitlePane.setBackground(colorBackTabTitle);
            isColorBackTabTitle = true;
        } else if (obj.equals(colorFontTabTitleField)) {
            colorFontTabTitle = Color.decode(colorFontTabTitleField.getText());
            colorFontTabTitlePane.setBackground(colorFontTabTitle);
            isColorFontTabTitle = true;
        } else if (obj.equals(colorFontBackTabTitleField)) {
            colorFontBackTabTitle = Color.decode(colorFontBackTabTitleField.getText());
            colorFontBackTabTitlePane.setBackground(colorFontBackTabTitle);
            isColorFontBackTabTitle = true;
        } else if (obj.equals(gradientFieldNOFLCField)) {
            gradientFieldNOFLC.setGradient(gradientFieldNOFLCField.getText());
            gradientFieldNOFLCPane.setGradient(gradientFieldNOFLC);
            gradientFieldNOFLCEnabledCheck.setSelected(gradientFieldNOFLC.isEnabled());
            isGradientFieldNOFLC = true;
        } else if (obj.equals(transparentCellTableField)) {
            // если в поле число
            String text = transparentCellTableField.getText();
            if (text.replaceAll("\\d", "").equals("")) {
                int value = Integer.parseInt(text);
                if (value >= 0 && value <= 100) {
                    transparentCellTableSlider.setValue(value);
                    transparentCellTable = value;
                    transparentCellTablePane.setTransparent(transparentCellTable);
                    isTransparentCellTable = true;
                }
            }
        } else if (obj.equals(transparentBackTabTitleField)) {
            // если в поле число
            String text = transparentBackTabTitleField.getText();
            if (text.replaceAll("\\d", "").equals("")) {
                int value = Integer.parseInt(text);
                if (value >= 0 && value <= 100) {
                    transparentBackTabTitleSlider.setValue(value);
                    transparentBackTabTitle = value;
                    transparentBackTabTitlePane.setTransparent(transparentBackTabTitle);
                    isTransparentBackTabTitle = true;
                }
            }
        } else if (obj.equals(transparentSelectedTabTitleField)) {
            // если в поле число
            String text = transparentSelectedTabTitleField.getText();
            if (text.replaceAll("\\d", "").equals("")) {
                int value = Integer.parseInt(text);
                if (value >= 0 && value <= 100) {
                    transparentSelectedTabTitleSlider.setValue(value);
                    transparentSelectedTabTitle = value;
                    transparentSelectedTabTitlePane.setTransparent(transparentSelectedTabTitle);
                    isTransparentSelectedTabTitle = true;
                }
            }
        } else if (obj.equals(gradientFieldNOFLCEnabledCheck)) {
            gradientFieldNOFLC.setEnabled(gradientFieldNOFLCEnabledCheck.isSelected());
            gradientFieldNOFLCField.setText(gradientFieldNOFLC.toString());
            gradientFieldNOFLC.setGradient(gradientFieldNOFLC);
            isGradientFieldNOFLC = true;
        }  else if (obj.equals(colorMainCheck)) {
            updateFieldByCheck(colorMainCheck, colorMainField);
            colorMainPane2.setOpaque(isColorActive(new ColorAct(mainColor, colorMainCheck.isSelected())));
            colorMainPane2.repaint();
            isChangeColorMain = true;
        } else if (obj.equals(blueSysColorCheck)) {
            updateFieldByCheck(blueSysColorCheck, blueSysColorField);
            blueSysColorPane2.setOpaque(isColorActive(new ColorAct(blueSysColor, blueSysColorCheck.isSelected())));
            blueSysColorPane2.repaint();
            isBlueSysColor = true;
        } else if (obj.equals(darkShadowSysColorCheck)) {
            updateFieldByCheck(darkShadowSysColorCheck, darkShadowSysColorField);
            darkShadowSysColorPane2
                    .setOpaque(isColorActive(new ColorAct(darkShadowSysColor, darkShadowSysColorCheck.isSelected())));
            darkShadowSysColorPane2.repaint();
            isDarkShadowSysColor = true;
        } else if (obj.equals(midSysColorCheck)) {
            updateFieldByCheck(midSysColorCheck, midSysColorField);
            midSysColorPane2.setOpaque(isColorActive(new ColorAct(midSysColor, midSysColorCheck.isSelected())));
            midSysColorPane2.repaint();
            isMidSysColor = true;
        } else if (obj.equals(lightYellowColorCheck)) {
            updateFieldByCheck(lightYellowColorCheck, lightYellowColorField);
            lightYellowColorPane2.setOpaque(isColorActive(new ColorAct(lightYellowColor, lightYellowColorCheck.isSelected())));
            lightYellowColorPane2.repaint();
            isLightYellowColor = true;
        } else if (obj.equals(redColorCheck)) {
            updateFieldByCheck(redColorCheck, redColorField);
            redColorPane2.setOpaque(isColorActive(new ColorAct(redColor, redColorCheck.isSelected())));
            redColorPane2.repaint();
            isRedColor = true;
        } else if (obj.equals(lightRedColorCheck)) {
            updateFieldByCheck(lightRedColorCheck, lightRedColorField);
            lightRedColorPane2.setOpaque(isColorActive(new ColorAct(lightRedColor, lightRedColorCheck.isSelected())));
            lightRedColorPane2.repaint();
            isLightRedColor = true;
        } else if (obj.equals(lightGreenColorCheck)) {
            updateFieldByCheck(lightGreenColorCheck, lightGreenColorField);
            lightGreenColorPane2.setOpaque(isColorActive(new ColorAct(lightGreenColor, lightGreenColorCheck.isSelected())));
            lightGreenColorPane2.repaint();
            isLightGreenColor = true;
        } else if (obj.equals(shadowYellowColorCheck)) {
            updateFieldByCheck(shadowYellowColorCheck, shadowYellowColorField);
            shadowYellowColorPane2.setOpaque(isColorActive(new ColorAct(shadowYellowColor, shadowYellowColorCheck.isSelected())));
            shadowYellowColorPane2.repaint();
            isShadowYellowColor = true;
        } else if (obj.equals(sysColorCheck)) {
            updateFieldByCheck(sysColorCheck, sysColorField);
            sysColorPane2.setOpaque(isColorActive(new ColorAct(sysColor, sysColorCheck.isSelected())));
            sysColorPane2.repaint();
            isSysColor = true;
        } else if (obj.equals(lightSysColorCheck)) {
            updateFieldByCheck(lightSysColorCheck, lightSysColorField);
            lightSysColorPane2.setOpaque(isColorActive(new ColorAct(lightSysColor, lightSysColorCheck.isSelected())));
            lightSysColorPane2.repaint();
            isLightSysColor = true;
        } else if (obj.equals(defaultFontColorCheck)) {
            updateFieldByCheck(defaultFontColorCheck, defaultFontColorField);
            defaultFontColorPane2.setOpaque(isColorActive(new ColorAct(defaultFontColor, defaultFontColorCheck.isSelected())));
            defaultFontColorPane2.repaint();
            isDefaultFontColor = true;
        } else if (obj.equals(silverColorCheck)) {
            updateFieldByCheck(silverColorCheck, silverColorField);
            silverColorPane2.setOpaque(isColorActive(new ColorAct(silverColor, silverColorCheck.isSelected())));
            silverColorPane2.repaint();
            isSilverColor = true;
        } else if (obj.equals(shadowsGreyColorCheck)) {
            updateFieldByCheck(shadowsGreyColorCheck, shadowsGreyColorField);
            shadowsGreyColorPane2.setOpaque(isColorActive(new ColorAct(shadowsGreyColor, shadowsGreyColorCheck.isSelected())));
            shadowsGreyColorPane2.repaint();
            isShadowsGreyColor = true;
        } else if (obj.equals(keywordColorCheck)) {
            updateFieldByCheck(keywordColorCheck, keywordColorField);
            keywordColorPane2.setOpaque(isColorActive(new ColorAct(keywordColor, keywordColorCheck.isSelected())));
            keywordColorPane2.repaint();
            isKeywordColor = true;
        } else if (obj.equals(variableColorCheck)) {
            updateFieldByCheck(variableColorCheck, variableColorField);
            variableColorPane2.setOpaque(isColorActive(new ColorAct(variableColor, variableColorCheck.isSelected())));
            variableColorPane2.repaint();
            isVariableColor = true;
        } else if (obj.equals(clientVariableColorCheck)) {
            updateFieldByCheck(clientVariableColorCheck, clientVariableColorField);
            clientVariableColorPane2.setOpaque(isColorActive(new ColorAct(clientVariableColor, clientVariableColorCheck
                    .isSelected())));
            clientVariableColorPane2.repaint();
            isClientVariableColor = true;
        } else if (obj.equals(commentColorCheck)) {
            updateFieldByCheck(commentColorCheck, commentColorField);
            commentColorPane2.setOpaque(isColorActive(new ColorAct(commentColor, commentColorCheck.isSelected())));
            commentColorPane2.repaint();
            isCommentColor = true;
        } else if (obj.equals(blueSysColorBtn)) {
            cch = new OrColorChooser(blueSysColor);
            dlg = new DesignerDialog(Or3Frame.instance(), clTitle, cch, false, true);
            dlg.show();
            if (dlg.isOK()) {
                blueSysColor = cch.getColor();
            } else if (dlg.getResult() == BUTTON_DEFAULT) {
                blueSysColor = BLUE_SYS_COLOR;
            }
            if (dlg.isOK() || dlg.getResult() == BUTTON_DEFAULT) {
                blueSysColorField.setText(blueSysColor.getRGB() + (blueSysColorCheck.isSelected() ? " 1" : " 0"));
                blueSysColorPane2.setOpaque(isColorActive(new ColorAct(blueSysColor, blueSysColorCheck.isSelected())));
                blueSysColorPane2.setBackground(blueSysColor);
                isBlueSysColor = true;
            }
        } else if (obj.equals(darkShadowSysColorBtn)) {
            cch = new OrColorChooser(darkShadowSysColor);
            dlg = new DesignerDialog(Or3Frame.instance(), clTitle, cch, false, true);
            dlg.show();
            if (dlg.isOK()) {
                darkShadowSysColor = cch.getColor();
            } else if (dlg.getResult() == BUTTON_DEFAULT) {
                darkShadowSysColor = DARK_SHADOW_SYS_COLOR;
            }
            if (dlg.isOK() || dlg.getResult() == BUTTON_DEFAULT) {
                darkShadowSysColorField.setText(darkShadowSysColor.getRGB()
                        + (darkShadowSysColorCheck.isSelected() ? " 1" : " 0"));
                darkShadowSysColorPane2.setOpaque(isColorActive(new ColorAct(darkShadowSysColor, darkShadowSysColorCheck
                        .isSelected())));
                darkShadowSysColorPane2.setBackground(darkShadowSysColor);
                isDarkShadowSysColor = true;
            }
        } else if (obj.equals(midSysColorBtn)) {
            cch = new OrColorChooser(midSysColor);
            dlg = new DesignerDialog(Or3Frame.instance(), clTitle, cch, false, true);
            dlg.show();
            if (dlg.isOK()) {
                midSysColor = cch.getColor();
            } else if (dlg.getResult() == BUTTON_DEFAULT) {
                midSysColor = MID_SYS_COLOR;
            }
            if (dlg.isOK() || dlg.getResult() == BUTTON_DEFAULT) {
                midSysColorField.setText(midSysColor.getRGB() + (midSysColorCheck.isSelected() ? " 1" : " 0"));
                midSysColorPane2.setOpaque(isColorActive(new ColorAct(midSysColor, midSysColorCheck.isSelected())));
                midSysColorPane2.setBackground(midSysColor);
                isMidSysColor = true;
            }
        } else if (obj.equals(lightYellowColorBtn)) {
            cch = new OrColorChooser(lightYellowColor);
            dlg = new DesignerDialog(Or3Frame.instance(), clTitle, cch, false, true);
            dlg.show();
            if (dlg.isOK()) {
                lightYellowColor = cch.getColor();
            } else if (dlg.getResult() == BUTTON_DEFAULT) {
                lightYellowColor = LIGHT_YELLOW_COLOR;
            }
            if (dlg.isOK() || dlg.getResult() == BUTTON_DEFAULT) {
                lightYellowColorField.setText(lightYellowColor.getRGB() + (lightYellowColorCheck.isSelected() ? " 1" : " 0"));
                lightYellowColorPane2
                        .setOpaque(isColorActive(new ColorAct(lightYellowColor, lightYellowColorCheck.isSelected())));
                lightYellowColorPane2.setBackground(lightYellowColor);
                isLightYellowColor = true;
            }
        } else if (obj.equals(redColorBtn)) {
            cch = new OrColorChooser(redColor);
            dlg = new DesignerDialog(Or3Frame.instance(), clTitle, cch, false, true);
            dlg.show();
            if (dlg.isOK()) {
                redColor = cch.getColor();
            } else if (dlg.getResult() == BUTTON_DEFAULT) {
                redColor = RED_COLOR;
            }
            if (dlg.isOK() || dlg.getResult() == BUTTON_DEFAULT) {
                redColorField.setText(redColor.getRGB() + (redColorCheck.isSelected() ? " 1" : " 0"));
                redColorPane2.setOpaque(isColorActive(new ColorAct(redColor, redColorCheck.isSelected())));
                redColorPane2.setBackground(redColor);
                isRedColor = true;
            }
        } else if (obj.equals(lightRedColorBtn)) {
            cch = new OrColorChooser(lightRedColor);
            dlg = new DesignerDialog(Or3Frame.instance(), clTitle, cch, false, true);
            dlg.show();
            if (dlg.isOK()) {
                lightRedColor = cch.getColor();
            } else if (dlg.getResult() == BUTTON_DEFAULT) {
                lightRedColor = LIGHT_RED_COLOR;
            }
            if (dlg.isOK() || dlg.getResult() == BUTTON_DEFAULT) {
                lightRedColorField.setText(lightRedColor.getRGB() + (lightRedColorCheck.isSelected() ? " 1" : " 0"));
                lightRedColorPane2.setOpaque(isColorActive(new ColorAct(lightRedColor, lightRedColorCheck.isSelected())));
                lightRedColorPane2.setBackground(lightRedColor);
                isLightRedColor = true;
            }
        } else if (obj.equals(lightGreenColorBtn)) {
            cch = new OrColorChooser(lightGreenColor);
            dlg = new DesignerDialog(Or3Frame.instance(), clTitle, cch, false, true);
            dlg.show();
            if (dlg.isOK()) {
                lightGreenColor = cch.getColor();
            } else if (dlg.getResult() == BUTTON_DEFAULT) {
                lightGreenColor = LIGHT_GREEN_COLOR;
            }
            if (dlg.isOK() || dlg.getResult() == BUTTON_DEFAULT) {
                lightGreenColorField.setText(lightGreenColor.getRGB() + (lightGreenColorCheck.isSelected() ? " 1" : " 0"));
                lightGreenColorPane2.setOpaque(isColorActive(new ColorAct(lightGreenColor, lightGreenColorCheck.isSelected())));
                lightGreenColorPane2.setBackground(lightGreenColor);
                isLightGreenColor = true;
            }
        } else if (obj.equals(shadowYellowColorBtn)) {
            cch = new OrColorChooser(shadowYellowColor);
            dlg = new DesignerDialog(Or3Frame.instance(), clTitle, cch, false, true);
            dlg.show();
            if (dlg.isOK()) {
                shadowYellowColor = cch.getColor();
            } else if (dlg.getResult() == BUTTON_DEFAULT) {
                shadowYellowColor = SHADOW_YELLOW_COLOR;
            }
            if (dlg.isOK() || dlg.getResult() == BUTTON_DEFAULT) {
                shadowYellowColorField.setText(shadowYellowColor.getRGB() + (shadowYellowColorCheck.isSelected() ? " 1" : " 0"));
                shadowYellowColorPane2.setOpaque(isColorActive(new ColorAct(shadowYellowColor, shadowYellowColorCheck
                        .isSelected())));
                shadowYellowColorPane2.setBackground(shadowYellowColor);
                isShadowYellowColor = true;
            }
        } else if (obj.equals(sysColorBtn)) {
            cch = new OrColorChooser(sysColor);
            dlg = new DesignerDialog(Or3Frame.instance(), clTitle, cch, false, true);
            dlg.show();
            if (dlg.isOK()) {
                sysColor = cch.getColor();
            } else if (dlg.getResult() == BUTTON_DEFAULT) {
                sysColor = SYS_COLOR;
            }
            if (dlg.isOK() || dlg.getResult() == BUTTON_DEFAULT) {
                sysColorField.setText(sysColor.getRGB() + (sysColorCheck.isSelected() ? " 1" : " 0"));
                sysColorPane2.setOpaque(isColorActive(new ColorAct(sysColor, sysColorCheck.isSelected())));
                sysColorPane2.setBackground(sysColor);
                isSysColor = true;
            }
        } else if (obj.equals(lightSysColorBtn)) {
            cch = new OrColorChooser(lightSysColor);
            dlg = new DesignerDialog(Or3Frame.instance(), clTitle, cch, false, true);
            dlg.show();
            if (dlg.isOK()) {
                lightSysColor = cch.getColor();
            } else if (dlg.getResult() == BUTTON_DEFAULT) {
                lightSysColor = LIGHT_SYS_COLOR;
            }
            if (dlg.isOK() || dlg.getResult() == BUTTON_DEFAULT) {
                lightSysColorField.setText(lightSysColor.getRGB() + (lightSysColorCheck.isSelected() ? " 1" : " 0"));
                lightSysColorPane2.setOpaque(isColorActive(new ColorAct(lightSysColor, lightSysColorCheck.isSelected())));
                lightSysColorPane2.setBackground(lightSysColor);
                isLightSysColor = true;
            }
        } else if (obj.equals(defaultFontColorBtn)) {
            cch = new OrColorChooser(defaultFontColor);
            dlg = new DesignerDialog(Or3Frame.instance(), clTitle, cch, false, true);
            dlg.show();
            if (dlg.isOK()) {
                defaultFontColor = cch.getColor();
            } else if (dlg.getResult() == BUTTON_DEFAULT) {
                defaultFontColor = DEFAULT_FONT_COLOR;
            }
            if (dlg.isOK() || dlg.getResult() == BUTTON_DEFAULT) {
                defaultFontColorField.setText(defaultFontColor.getRGB() + (defaultFontColorCheck.isSelected() ? " 1" : " 0"));
                defaultFontColorPane2
                        .setOpaque(isColorActive(new ColorAct(defaultFontColor, defaultFontColorCheck.isSelected())));
                defaultFontColorPane2.setBackground(defaultFontColor);
                isDefaultFontColor = true;
            }
        } else if (obj.equals(silverColorBtn)) {
            cch = new OrColorChooser(silverColor);
            dlg = new DesignerDialog(Or3Frame.instance(), clTitle, cch, false, true);
            dlg.show();
            if (dlg.isOK()) {
                silverColor = cch.getColor();
            } else if (dlg.getResult() == BUTTON_DEFAULT) {
                silverColor = SILVER_COLOR;
            }
            if (dlg.isOK() || dlg.getResult() == BUTTON_DEFAULT) {
                silverColorField.setText(silverColor.getRGB() + (silverColorCheck.isSelected() ? " 1" : " 0"));
                silverColorPane2.setOpaque(isColorActive(new ColorAct(silverColor, silverColorCheck.isSelected())));
                silverColorPane2.setBackground(silverColor);
                isSilverColor = true;
            }
        } else if (obj.equals(shadowsGreyColorBtn)) {
            cch = new OrColorChooser(shadowsGreyColor);
            dlg = new DesignerDialog(Or3Frame.instance(), clTitle, cch, false, true);
            dlg.show();
            if (dlg.isOK()) {
                shadowsGreyColor = cch.getColor();
            } else if (dlg.getResult() == BUTTON_DEFAULT) {
                shadowsGreyColor = SHADOWS_GREY_COLOR;
            }
            if (dlg.isOK() || dlg.getResult() == BUTTON_DEFAULT) {
                shadowsGreyColorField.setText(shadowsGreyColor.getRGB() + (shadowsGreyColorCheck.isSelected() ? " 1" : " 0"));
                shadowsGreyColorPane2
                        .setOpaque(isColorActive(new ColorAct(shadowsGreyColor, shadowsGreyColorCheck.isSelected())));
                shadowsGreyColorPane2.setBackground(shadowsGreyColor);
                isShadowsGreyColor = true;
            }
        } else if (obj.equals(keywordColorBtn)) {
            cch = new OrColorChooser(keywordColor);
            dlg = new DesignerDialog(Or3Frame.instance(), clTitle, cch, false, true);
            dlg.show();
            if (dlg.isOK()) {
                keywordColor = cch.getColor();
            } else if (dlg.getResult() == BUTTON_DEFAULT) {
                keywordColor = KEYWORD_COLOR;
            }
            if (dlg.isOK() || dlg.getResult() == BUTTON_DEFAULT) {
                keywordColorField.setText(keywordColor.getRGB() + (keywordColorCheck.isSelected() ? " 1" : " 0"));
                keywordColorPane2.setOpaque(isColorActive(new ColorAct(keywordColor, keywordColorCheck.isSelected())));
                keywordColorPane2.setBackground(keywordColor);
                isKeywordColor = true;
            }
        } else if (obj.equals(variableColorBtn)) {
            cch = new OrColorChooser(variableColor);
            dlg = new DesignerDialog(Or3Frame.instance(), clTitle, cch, false, true);
            dlg.show();
            if (dlg.isOK()) {
                variableColor = cch.getColor();
            } else if (dlg.getResult() == BUTTON_DEFAULT) {
                variableColor = VARIABLE_COLOR;
            }
            if (dlg.isOK() || dlg.getResult() == BUTTON_DEFAULT) {
                variableColorField.setText(variableColor.getRGB() + (variableColorCheck.isSelected() ? " 1" : " 0"));
                variableColorPane2.setOpaque(isColorActive(new ColorAct(variableColor, variableColorCheck.isSelected())));
                variableColorPane2.setBackground(variableColor);
                isVariableColor = true;
            }
        } else if (obj.equals(clientVariableColorBtn)) {
            cch = new OrColorChooser(clientVariableColor);
            dlg = new DesignerDialog(Or3Frame.instance(), clTitle, cch, false, true);
            dlg.show();
            if (dlg.isOK()) {
                clientVariableColor = cch.getColor();
            } else if (dlg.getResult() == BUTTON_DEFAULT) {
                clientVariableColor = CLIENT_VARIABLE_COLOR;
            }
            if (dlg.isOK() || dlg.getResult() == BUTTON_DEFAULT) {
                clientVariableColorField.setText(clientVariableColor.getRGB()
                        + (clientVariableColorCheck.isSelected() ? " 1" : " 0"));
                clientVariableColorPane2.setOpaque(isColorActive(new ColorAct(clientVariableColor, clientVariableColorCheck
                        .isSelected())));
                clientVariableColorPane2.setBackground(clientVariableColor);
                isClientVariableColor = true;
            }
        } else if (obj.equals(commentColorBtn)) {
            cch = new OrColorChooser(commentColor);
            dlg = new DesignerDialog(Or3Frame.instance(), clTitle, cch, false, true);
            dlg.show();
            if (dlg.isOK()) {
                commentColor = cch.getColor();
            } else if (dlg.getResult() == BUTTON_DEFAULT) {
                commentColor = COMMENT_COLOR;
            }
            if (dlg.isOK() || dlg.getResult() == BUTTON_DEFAULT) {
                commentColorField.setText(commentColor.getRGB() + (commentColorCheck.isSelected() ? " 1" : " 0"));
                commentColorPane2.setOpaque(isColorActive(new ColorAct(commentColor, commentColorCheck.isSelected())));
                commentColorPane2.setBackground(commentColor);
                isCommentColor = true;
            }
        } else if (obj.equals(blueSysColorField)) {
            updateCompByField(blueSysColorField, blueSysColorCheck, blueSysColorPane, blueSysColor);
            isBlueSysColor = true;
        } else if (obj.equals(darkShadowSysColorField)) {
            updateCompByField(darkShadowSysColorField, darkShadowSysColorCheck, darkShadowSysColorPane, darkShadowSysColor);
            isDarkShadowSysColor = true;
        } else if (obj.equals(midSysColorField)) {
            updateCompByField(midSysColorField, midSysColorCheck, midSysColorPane, midSysColor);
            isMidSysColor = true;
        } else if (obj.equals(lightYellowColorField)) {
            updateCompByField(lightYellowColorField, lightYellowColorCheck, lightYellowColorPane, lightYellowColor);
            isLightYellowColor = true;
        } else if (obj.equals(redColorField)) {
            updateCompByField(redColorField, redColorCheck, redColorPane, redColor);
            isRedColor = true;
        } else if (obj.equals(lightRedColorField)) {
            updateCompByField(lightRedColorField, lightRedColorCheck, lightRedColorPane, lightRedColor);
            isLightRedColor = true;
        } else if (obj.equals(lightGreenColorField)) {
            updateCompByField(lightGreenColorField, lightGreenColorCheck, lightGreenColorPane, lightGreenColor);
            isLightGreenColor = true;
        } else if (obj.equals(shadowYellowColorField)) {
            updateCompByField(shadowYellowColorField, shadowYellowColorCheck, shadowYellowColorPane, shadowYellowColor);
            isShadowYellowColor = true;
        } else if (obj.equals(sysColorField)) {
            updateCompByField(sysColorField, sysColorCheck, sysColorPane, sysColor);
            isSysColor = true;
        } else if (obj.equals(lightSysColorField)) {
            updateCompByField(lightSysColorField, lightSysColorCheck, lightSysColorPane, lightSysColor);
            isLightSysColor = true;
        } else if (obj.equals(defaultFontColorField)) {
            updateCompByField(defaultFontColorField, defaultFontColorCheck, defaultFontColorPane, defaultFontColor);
            isDefaultFontColor = true;
        } else if (obj.equals(silverColorField)) {
            updateCompByField(silverColorField, silverColorCheck, silverColorPane, silverColor);
            isSilverColor = true;
        } else if (obj.equals(shadowsGreyColorField)) {
            updateCompByField(shadowsGreyColorField, shadowsGreyColorCheck, shadowsGreyColorPane, shadowsGreyColor);
            isShadowsGreyColor = true;
        } else if (obj.equals(keywordColorField)) {
            updateCompByField(keywordColorField, keywordColorCheck, keywordColorPane, keywordColor);
            isKeywordColor = true;
        } else if (obj.equals(variableColorField)) {
            updateCompByField(variableColorField, variableColorCheck, variableColorPane, variableColor);
            isVariableColor = true;
        } else if (obj.equals(clientVariableColorField)) {
            updateCompByField(clientVariableColorField, clientVariableColorCheck, clientVariableColorPane, clientVariableColor);
            isClientVariableColor = true;
        } else if (obj.equals(commentColorField)) {
            updateCompByField(commentColorField, commentColorCheck, commentColorPane, commentColor);
            isCommentColor = true;
        } else if (obj == refreshUUID) { // Обновить UUID интерфейсов
            if (showMessageQuestion("Сгенерировать UUID для всех компонентов?") != BUTTON_YES) {
                return;
            }
            generateUUID();
        } else if (obj == refreshUUIDF) { // Переписать UUID интерфейсов
            if (showMessageQuestion("Сгенерировать НОВЫЕ UUID для всех компонентов?") != BUTTON_YES) {
                return;
            }
            PropertyHelper.forseGenUUID = true;
            try {
                generateUUID();
            } catch (Exception e2) {
                System.out.println("Ошибка генерации UUID");
            } finally {
                PropertyHelper.forseGenUUID = false;
            }

        } else if (obj == isObjectBrowserLimitCheck) {
            isObjectBrowserLimit = isObjectBrowserLimitCheck.isSelected();
            isChangeIsObjectBrowserLimit = true;
        } else if (obj == isObjectBrowserLimitForClassesCheck) {
            isObjectBrowserLimitForClasses = isObjectBrowserLimitForClassesCheck.isSelected();
            isChangeIsObjectBrowserLimitForClasses = true;
        } else if (obj == cleanObjectBrowserLimitForClassesBtn) {
            if (showMessageQuestion("Вы действительно хотите сбросить индивидуальные настройки отображения классов?") == BUTTON_YES) {
                conf.cleanObjectBrowserLimitForClasses();
            }
        } else if (obj == objectBrowserLimitField) {
            String text = objectBrowserLimitField.getText();
            if (text.replaceAll("\\d", "").equals("")) {
                int value = Integer.parseInt(text);
                if (value >= 0) {
                    objectBrowserLimit = value;
                    isChangeObjectBrowserLimit = true;
                }
            }
        } else if (obj == okItem) {
            setAllConfig();
        } else if (obj == resetItem) {
        	resetConfig();
        } else if (obj.equals(isDataLog)) {
        	Kernel.instance().setDataLog(isDataLog.isSelected());
        } else if (obj == generateHtmlTemplates) { // сгенерировать html шаблоны для интерфейсов
        	generateHtmlTemplatesForAllUI();
        	Runtime.getRuntime().gc();
        } else if (obj == resaveAllIfcsForEGKNH) {
        	resaveAllIfcsForEGKNH();
        	Runtime.getRuntime().gc();
        } else if (obj == resaveIfcForEGKNH) {
        	AbstractDesignerTreeNode node = panel.getTree().getSelectedNode();
            if (node == null || !node.isLeaf()) {
                showMessageInfo("Выберите интерфейс!");
            } else {
            	resaveIfcForEGKNH(node.getKrnObj());
            }
        } else if (obj == selectUI) {
        	 InterfaceTree ifc_tree = kz.tamur.comps.Utils.getInterfaceTree();
             panel = new OpenElementPanel(ifc_tree);
             panel.setSearchUIDPanel(true);
             DesignerDialog dialog;
             if (getTopLevelAncestor() instanceof JFrame) {
             	dialog = new DesignerDialog((Frame) getTopLevelAncestor(), "Выбор интерфейса", panel);
             } else {
             	dialog = new DesignerDialog((Dialog) getTopLevelAncestor(), "Выбор интерфейса", panel);
             }
             if (panel.getTree().getSelectedNode() == null) {
             	dialog.setOkEnabled(false);
             } else {
             	dialog.setOkEnabled(true);
             }
             dialog.show();
        } else if (obj == resaveAllProcsForEGKNH) {
        	resaveAllProcsForEGKNH();
        	Runtime.getRuntime().gc();
        } else if (obj == resaveProcForEGKNH) {
        	AbstractDesignerTreeNode node = panel.getTree().getSelectedNode();
            if (node == null || !node.isLeaf()) {
                showMessageInfo("Выберите процесс!");
            } else {
            	resaveProcForEGKNH(node.getKrnObj());
            }
        } else if (obj == selectProc) {
        	ServicesTree proc_tree = kz.tamur.comps.Utils.getServicesTree();
	        panel = new OpenElementPanel(proc_tree);
	        panel.setSearchUIDPanel(true);
	        DesignerDialog dialog;
	        if (getTopLevelAncestor() instanceof JFrame) {
	        	dialog = new DesignerDialog((Frame) getTopLevelAncestor(), "Выбор процесса", panel);
	        } else {
	        	dialog = new DesignerDialog((Dialog) getTopLevelAncestor(), "Выбор процесса", panel);
	        }
	        if (panel.getTree().getSelectedNode() == null) {
	        	dialog.setOkEnabled(false);
	        } else {
	        	dialog.setOkEnabled(true);
	        }
	        dialog.show();             
        } else if (obj == resaveLogotypePic) {
        	Kernel krn = Kernel.instance();
        	try {
        		KrnClass cls = krn.getClassByName("ConfigGlobal");
        		KrnObject[] krnObjs = krn.getClassOwnObjects(cls, 0);
        		String attrName;
        		if(krnObjs.length > 0) {
        			if(logotypePic != null) {
        				byte[] val = null;
        				try {
        					val = Files.readAllBytes(logotypePic.toPath());
        				} catch (IOException e1) {
        					e1.printStackTrace();
        				}
        				attrName = "logotypePic";
        				krn.setBlob(krnObjs[0].id, cls.id, attrName, 0, val, 0, 0);
        				iconPic = val;
        				logotypePic = null;
        				selPicT.setText("");
        			}

        			String picWidth = picWidthT.getText();
        			attrName = "logoPicWidth";
        			if(picWidth != null && picWidth.length() > 0){
        				int widthInt = Integer.parseInt(picWidth);
        				if(widthInt >= 0) {
        					krn.setLong(krnObjs[0].id, cls.id, attrName, 0, widthInt, 0);
        				}
        			} else {
        				int[] index = new int[1];
        				krn.deleteValue(krnObjs[0].id, cls.id, attrName, index, 0);
        			}

        			String picHeight = picHeightT.getText();
        			attrName = "logoPicHeight";
        			if(picHeight != null && picHeight.length() > 0){
        				int heightInt = Integer.parseInt(picHeight);
        				if(heightInt >= 0) {
        					krn.setLong(krnObjs[0].id, cls.id, attrName, 0, heightInt, 0);
        				}
        			} else {
        				int[] index = new int[1];
        				krn.deleteValue(krnObjs[0].id, cls.id, attrName, index, 0);
        			}
        			setIconPic(iconPic);
        		}

        	} catch (KrnException e1) {
        		// TODO Auto-generated catch block
        		e1.printStackTrace();
        	} 
        	showMessageInfo("Параметры рисунка логотипа успешно сохранено!");
        } 
        else if (obj == removePic) {
        	if (showMessageQuestion("Вы действительно хотите удалить рисунок логотипа с базы?") != BUTTON_YES) {
                return;
            }
        	logotypePic = null;
        	iconPic = null;
        	selPicT.setText("");
        	Kernel krn = Kernel.instance();
    		try {
				KrnClass cls = krn.getClassByName("ConfigGlobal");
				KrnObject[] krnObjs = krn.getClassOwnObjects(cls, 0);
				String attrName = "logotypePic";
				int[] index = new int[1];
				index[0] = 0;
				krn.deleteValue(krnObjs[0].id, cls.id, attrName, index, 0);
				picLabel.setIcon(null);
    		} catch (KrnException e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    		}
        } else if (obj == selectPic) {
        	File file = selectFile();
        	if(file != null) {
        		String extension = file.toString().substring(file.toString().length()-4);
        		if("jpeg".equals(extension) || ".jpg".equals(extension) || ".png".equals(extension) || ".gif".equals(extension)) {
        			logotypePic = file;
            		selPicT.setText(file.toString());
        		} else
        			showMessageInfo("Выберите файл типа рисунка");
        	}
        } else if (obj == resaveSearchParam) {
        	Kernel krn = Kernel.instance();
        	try {
        		KrnClass cls = krn.getClassByName("ConfigGlobal");
        		KrnObject[] krnObjs = krn.getClassOwnObjects(cls, 0);
        		if(krnObjs.length > 0) {
        			String attrName;
                	long langId = 0;

                	String srch_txt = srch_txtT.getText();
                	if(srch_txt != null) {
                		langId = krn.getLangIdByCode("RU");
            			attrName = "srch_txt";
                		if(srch_txt.length() > 0){                			
                			krn.setString(krnObjs[0].id, cls.id, attrName, 0, langId, srch_txt, 0);
                		} else if(srch_txt.length() == 0) {
                			krn.setString(krnObjs[0].id, cls.id, attrName, 0, langId, null, 0);
                		}
                	}
        			
        			String srch_txtKz = srch_txtKzT.getText();
        			if(srch_txtKz != null) {
        				langId = krn.getLangIdByCode("KZ");
    					attrName = "srch_txt";
        				if(srch_txtKz.length() > 0){        					
        					krn.setString(krnObjs[0].id, cls.id, attrName, 0, langId, srch_txtKz, 0);
        				} else if(srch_txtKz.length() == 0) {
        					krn.setString(krnObjs[0].id, cls.id, attrName, 0, langId, null, 0);
        				}
        			}

        			String ifc_uid = ifc_uidT.getText();
        			if(ifc_uid != null){        				
        					attrName = "ifc_uid";
        					if(ifc_uid.length() > 0) {
        						krn.setString(krnObjs[0].id, cls.id, attrName, 0, 0, ifc_uid, 0);
        					} else if(ifc_uid.length() == 0) {
        						krn.setString(krnObjs[0].id, cls.id, attrName, 0, 0, null, 0);
        					}
        			}
        			
        			attrName = "showSearchField";
        			krn.setLong(krnObjs[0].id, cls.id, attrName, 0, showSearchField.isSelected()? 1:0, 0);
        		}

        	} catch (KrnException e1) {
        		// TODO Auto-generated catch block
        		e1.printStackTrace();
        	} 
        	showMessageInfo("Параметры быстрого доступа к объекту учета успешно сохранено!");
        } else if (obj == resaveChatSearchParam) {
        	Kernel krn = Kernel.instance();
        	try {
        		KrnClass cls = krn.getClassByName("ConfigGlobal");
        		KrnObject[] krnObjs = krn.getClassOwnObjects(cls, 0);
        		if(krnObjs.length > 0) {
        			String attrName;
                	long langId = 0;

                	String srch_txt = chat_srch_txtT.getText();
                	if(srch_txt != null) {
                		langId = krn.getLangIdByCode("RU");
            			attrName = "chat_srch_txt";
                		if(srch_txt.length() > 0){                			
                			krn.setString(krnObjs[0].id, cls.id, attrName, 0, langId, srch_txt, 0);
                		} else if(srch_txt.length() == 0) {
                			krn.setString(krnObjs[0].id, cls.id, attrName, 0, langId, null, 0);
                		}
                	}
        			
        			String srch_txtKz = chat_srch_txtKzT.getText();
        			if(srch_txtKz != null) {
        				langId = krn.getLangIdByCode("KZ");
    					attrName = "chat_srch_txt";
        				if(srch_txtKz.length() > 0){        					
        					krn.setString(krnObjs[0].id, cls.id, attrName, 0, langId, srch_txtKz, 0);
        				} else if(srch_txtKz.length() == 0) {
        					krn.setString(krnObjs[0].id, cls.id, attrName, 0, langId, null, 0);
        				}
        			}
        		}

        	} catch (KrnException e1) {
        		// TODO Auto-generated catch block
        		e1.printStackTrace();
        	} 
        	showMessageInfo("Параметры быстрого доступа к объекту учета успешно сохранено!");
        } else if (obj == selectSound) {
        	File file = selectFile();
        	if(file != null) {
        		String extension = file.toString().substring(file.toString().length()-4);
        		if(".mp3".equals(extension) || ".wav".equals(extension)) {
        			try {
						byte[] fileBytes = Files.readAllBytes(file.toPath());
						if(fileBytes != null) {
							noteSound = fileBytes;
							selSoundT.setText(file.toString());
	        				playNoteSound.setEnabled(true);
	            			removeNoteSound.setEnabled(true);
	            			useNotificationSound.setEnabled(true);
						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}            		
        		} else
        			showMessageInfo("Выберите файл типа аудио 'mp3' или 'wav'");
        	}
        } else if(obj == resaveNoteSound) {
        	Kernel krn = Kernel.instance();
        	try {
        		KrnClass cls = krn.getClassByName("ConfigGlobal");
        		KrnObject[] krnObjs = krn.getClassOwnObjects(cls, 0);
        		if(krnObjs.length > 0) {
        			String attrName;
                	attrName = "useNotificationSound";
        			krn.setLong(krnObjs[0].id, cls.id, attrName, 0, useNotificationSound.isSelected()? 1:0, 0);        			
        			if(noteSound != null) {        				
        				attrName = "notificationSound";
        				krn.setBlob(krnObjs[0].id, cls.id, attrName, 0, noteSound, 0, 0);
        				selSoundT.setText("");
        			}
        		}
        	} catch (KrnException e1) {
        		// TODO Auto-generated catch block
        		e1.printStackTrace();
        	} 
        	showMessageInfo("Параметры звука уведомления успешно сохранено!");
        } else if(obj == playNoteSound) {
        	playSound(noteSound);
        } else if(obj == removeNoteSound) {
        	if (showMessageQuestion("Вы действительно хотите удалить звук уведомления?") != BUTTON_YES) {
                return;
            }
        	Kernel krn = Kernel.instance();
        	try {
        		KrnClass cls = krn.getClassByName("ConfigGlobal");
        		KrnObject[] krnObjs = krn.getClassOwnObjects(cls, 0);
        		if(krnObjs.length > 0) {
        			String attrName;
                	attrName = "useNotificationSound";
                	useNotificationSound.setSelected(false);
        			useNotificationSound.setEnabled(false);
                	selSoundT.setText("");
        			krn.setLong(krnObjs[0].id, cls.id, attrName, 0, 0, 0);        			
        			attrName = "notificationSound";
        			int[] index = new int[1];
    				index[0] = 0;
        			krn.deleteValue(krnObjs[0].id, cls.id, attrName, index, 0);
        			noteSound = null;
        			playNoteSound.setEnabled(false);
        			removeNoteSound.setEnabled(false);
        		}
        	} catch (KrnException e1) {
        		// TODO Auto-generated catch block
        		e1.printStackTrace();
        	} 
        } else if(obj == useNotificationSound) {
        	if(useNotificationSound.isSelected()) {
        		if(noteSound == null) {
        			useNotificationSound.setSelected(false);
        		}
        	}
        }
    } 
    
    public void playSound(byte[] bytes) {
    	
    	try {
			AudioInputStream ais = AudioSystem.getAudioInputStream(new ByteArrayInputStream(bytes));
			Clip clip = AudioSystem.getClip();
			clip.open(ais);
			clip.start();
			
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    }
    
    public File selectFile() {
    	File file = null;
    	JFileChooser fc = new JFileChooser();
    	fc.setDialogTitle("выберите рисунок");
		int result = fc.showOpenDialog(null);
		if(result == JFileChooser.APPROVE_OPTION) {
			file = fc.getSelectedFile();
		}
    	
    	return file;
    }

    
    /**
     * Генерирует html шаблоны для всех объектоа класса UI
     */
    private void generateHtmlTemplatesForAllUI() {
        if (isGen) {
            showMessageInfo("Процесс генерации уже запущен!");
        } else {
            if (showMessageQuestion("Будет запущен процесс генерации html шаблонов для всех UI. \n Продолжить?") != BUTTON_YES) {
                return;
            }
            GenHTML gen = new GenHTML();
            gen.start();
        }
    }
    
    private void resaveAllIfcsForEGKNH() {
		if (isUIResaveInProcess) {
			showMessageInfo("Процесс пересохранения интерфейсов для ЕГКН уже запущен!");
		} else {
			if (showMessageQuestion("Будет запущен процесс пересохранения всех интерфейсов для ЕГКН. \n Продолжить?") != BUTTON_YES) {
				return;
			}
			ResaveUIForEGKN resaveUI = new ResaveUIForEGKN();
			resaveUI.start();
		}
	}

    private void resaveIfcForEGKNH(KrnObject obj) {
//		try {
//	    	SAXBuilder builder = new SAXBuilder();
//	   	  	File xmlFile = new File("C:\\file1.xml");
//	   		Document document = (Document) builder.build(xmlFile);
//	   		Element rootElement = document.getRootElement();
//	   		
//	   		editIfcForEGKN(rootElement);
//	   		
//	        ByteArrayOutputStream os = new ByteArrayOutputStream();
//	   		XMLOutputter out = new XMLOutputter();
//	        out.getFormat().setEncoding("UTF-8");
//	        out.output(document, os);
//	        os.close();
//	        OutputStream outputStream = new FileOutputStream("C:\\file2.xml");
//	        os.writeTo(outputStream);
//	        outputStream.close();
//		} catch (JDOMException | IOException e) {
//			e.printStackTrace();
//		}
    	try {
    		Kernel krn = Kernel.instance();
			byte[] data = krn.getBlob(obj, "config", 0, 0, 0);
			if (data.length > 0) {
				SAXBuilder builder = new SAXBuilder();
				ByteArrayInputStream is = new ByteArrayInputStream(data);
				Document document = (Document) builder.build(is);
				is.close();
				Element rootElement = document.getRootElement();

				editIfcForEGKN(rootElement);

				ByteArrayOutputStream os = new ByteArrayOutputStream();
				XMLOutputter out = new XMLOutputter();
				out.getFormat().setEncoding("UTF-8");
				out.output(document, os);
				os.close();

				krn.setBlob(obj.id, obj.classId, "config", 0, os.toByteArray(), 0, 0);
			}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    private void resaveAllProcsForEGKNH() {
		if (isProcResaveInProcess) {
			showMessageInfo("Пересохранение процессов для ЕГКН уже запущен!");
		} else {
			if (showMessageQuestion("Будет запущено пересохранение всех процессов для ЕГКН. \n Продолжить?") != BUTTON_YES) {
				return;
			}
			ResaveProcForEGKN resaveProc = new ResaveProcForEGKN();
			resaveProc.start();
		}
	}

    private void resaveProcForEGKNH(KrnObject obj) {
    	try {
    		 // Пересохранение атрибута config
    		Kernel krn = Kernel.instance();
            byte[] data = krn.getBlob(obj, "config", 0, 0, 0);
            if (data.length > 0) {
                SAXBuilder builder = new SAXBuilder();
                ByteArrayInputStream is = new ByteArrayInputStream(data);
    	   		Document document = (Document) builder.build(is);
    	   		is.close();
    	   		Element rootElement = document.getRootElement();
    	   		
    	   		Element responsibleElement = rootElement.getChild("responsible");
    	    	if (responsibleElement != null) {
    	    		Element krnResponsibleElement = rootElement.getChild("KRNresponsible");
	    			
    	    		responsibleElement.setText("/*" + responsibleElement.getText() + (krnResponsibleElement != null ? "\n" + krnResponsibleElement.getText() : "") + "*/");
	    			
	    			rootElement.removeContent(krnResponsibleElement);

    	    		ByteArrayOutputStream os = new ByteArrayOutputStream();
        	   		XMLOutputter out = new XMLOutputter();
        	        out.getFormat().setEncoding("UTF-8");
        	        out.output(document, os);
        	        os.close();
                    
        	        krn.setBlob(obj.id, obj.classId, "config", 0, os.toByteArray(), 0, 0);
    	    	}
            }
            
            // Пересохранение атрибута diagram
            data = krn.getBlob(obj, "diagram", 0, 0, 0);
            if (data.length > 0) {
                SAXBuilder builder = new SAXBuilder();
                ByteArrayInputStream is = new ByteArrayInputStream(data);
    	   		Document document = (Document) builder.build(is);
    	   		is.close();
    	   		Element rootElement = document.getRootElement();
    	   		
    	   		Element processElement = rootElement.getChild("process");
    	    	if (processElement != null) {
    	    		Element responsibleElement = null;
    	    		Element krnResponsibleElement = null;
    	    		List<Element> propertyElements = processElement.getChildren("property");
    	    		for (Element propertyElement: propertyElements) {
    	    			if ("responsible".equals(propertyElement.getAttributeValue("name"))) {
    	    				responsibleElement = propertyElement;
    	    			} else if ("KRNresponsible".equals(propertyElement.getAttributeValue("name"))) {
    	    				krnResponsibleElement = propertyElement;
    	    			}
    	    		}
    	    		
    	    		if (responsibleElement != null) {
    	    			responsibleElement.setText("/*" + responsibleElement.getText() + (krnResponsibleElement != null ? "\n" + krnResponsibleElement.getText() : "") + "*/");
    					
    	    			processElement.removeContent(krnResponsibleElement);
    	    			
    	    			ByteArrayOutputStream os = new ByteArrayOutputStream();
            	   		XMLOutputter out = new XMLOutputter();
            	        out.getFormat().setEncoding("UTF-8");
            	        out.output(document, os);
            	        os.close();
                        
            	        krn.setBlob(obj.id, obj.classId, "diagram", 0, os.toByteArray(), 0, 0);
    	    		}
    	    	}
            }
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    /**
     * метод для очистки компонентов интерфейса после завершения генерации html
     * @param component
     */
    private void cleanInterfaceComponents(Component component) {
        if (component instanceof JComponent) {
            Component[] comps = ((JComponent) component).getComponents();
            if (comps != null) {
                for (Component comp : comps) {
                    if (comp != null) {
                        if (comp instanceof OrGuiComponent) {
                            cleanInterfaceComponents(comp);
                            if (component instanceof OrPanel) {
                                ((OrPanel) component).removeComponentForMassHtmlGen((OrGuiComponent) comp);
                            } else if (component instanceof OrGuiContainer) {
                                ((OrGuiContainer) component).removeComponent((OrGuiComponent) comp);
                            }
                            ((OrGuiComponent) comp).setXml(null);
                        }
                        comp = null;
                    }
                }
                comps = null;
            }
        }
        
    }
    
    
    /**
     * Устанавливает прозрачность для заданных компонентов.
     * 
     * @param isOpaque
     *            <code>true</code> если непрозрачность включена
     */
    private void setTransparent(boolean isOpaque) {
        transparentMainCheck.setOpaque(isOpaque);
        transparentDialogCheck.setOpaque(isOpaque);
        gradientMainFrameEnabledCheck.setOpaque(isOpaque);
        gradientControlPanelEnabledCheck.setOpaque(isOpaque);
        gradientMenuPanelEnabledCheck.setOpaque(isOpaque);
        gradientFieldNOFLCEnabledCheck.setOpaque(isOpaque);
        transparentCellTablePane.setOpaque(isOpaque);
        transparentBackTabTitlePane.setOpaque(isOpaque);
        transparentSelectedTabTitlePane.setOpaque(isOpaque);
        classControlPane.setOpaque(isOpaque);
        uiControlPane.setOpaque(isOpaque);
        uiOpsForEGKNPane.setOpaque(isOpaque);
        procOpsForEGKNPane.setOpaque(isOpaque);
        logotypePicsPane.setOpaque(isOpaque);
        searchParamPane.setOpaque(isOpaque);
        chatSearchParamPane.setOpaque(isOpaque);
        notificationSoundPane.setOpaque(isOpaque);
        transparentCellTableSlider.setOpaque(isOpaque);
        transparentBackTabTitleSlider.setOpaque(isOpaque);
        transparentSelectedTabTitleSlider.setOpaque(isOpaque);
        isObjectBrowserLimitCheck.setOpaque(isOpaque);
        isObjectBrowserLimitForClassesCheck.setOpaque(isOpaque);

        sysVarPane.setOpaque(isOpaque);
        blueSysColorCheck.setOpaque(isOpaque);
        darkShadowSysColorCheck.setOpaque(isOpaque);
        midSysColorCheck.setOpaque(isOpaque);
        lightYellowColorCheck.setOpaque(isOpaque);
        redColorCheck.setOpaque(isOpaque);
        lightRedColorCheck.setOpaque(isOpaque);
        lightGreenColorCheck.setOpaque(isOpaque);
        shadowYellowColorCheck.setOpaque(isOpaque);
        sysColorCheck.setOpaque(isOpaque);
        lightSysColorCheck.setOpaque(isOpaque);
        defaultFontColorCheck.setOpaque(isOpaque);
        silverColorCheck.setOpaque(isOpaque);
        shadowsGreyColorCheck.setOpaque(isOpaque);
        keywordColorCheck.setOpaque(isOpaque);
        variableColorCheck.setOpaque(isOpaque);
        clientVariableColorCheck.setOpaque(isOpaque);
        commentColorCheck.setOpaque(isOpaque);
        ObjectBrowserViewPane.setOpaque(isOpaque);
        repaint();
    }

    /**
     * Установить transparent dialog.
     * 
     * @param isTransparent
     *            the new transparent dialog
     */
    private void setTransparentDialog(boolean isTransparent) {
        MainFrame.TRANSPARENT_DIALOG = isTransparent;
        ((JComponent) this.getParent()).setOpaque(!isTransparent);
        ((JComponent) this.getParent()).repaint();
    }

    /**
     * Sets the all config.
     */
    public void setAllConfig() {
        if (isChangeGradientMainFrame) {
            config.setGradientMainFrame(gradientMainFrame);
            isChangeGradientMainFrame = false;
        }
        if (isChangeGradientControlPanel) {
            config.setGradientControlPanel(gradientControlPanel);
            isChangeGradientControlPanel = false;
        }
        if (isChangeGradientMenuPanel) {
            config.setGradientMenuPanel(gradientMenuPanel);
            isChangeGradientMenuPanel = false;
        }
        if (isChangeTransparentMain) {
            config.setTransparentMain(transparentMain);
            isChangeTransparentMain = false;
        }
        if (isChangeTransparentDialog) {
            config.setTransparentDialog(transparentDialog);
            isChangeTransparentDialog = false;
        }
        if (isTransparentCellTable) {
            config.setTransparentCellTable(transparentCellTable);
            isTransparentCellTable = false;
        }
        if (isColorHeaderTable) {
            config.setColorHeaderTable(colorHeaderTable);
            isColorHeaderTable = false;
        }
        if (isColorTabTitle) {
            config.setColorTabTitle(colorTabTitle);
            isColorTabTitle = false;
        }
        if (isColorBackTabTitle) {
            config.setColorBackTabTitle(colorBackTabTitle);
            isColorBackTabTitle = false;
        }
        if (isColorFontTabTitle) {
            config.setColorFontTabTitle(colorFontTabTitle);
            isColorFontTabTitle = false;
        }
        if (isColorFontBackTabTitle) {
            config.setColorFontBackTabTitle(colorFontBackTabTitle);
            isColorFontBackTabTitle = false;
        }
        if (isTransparentBackTabTitle) {
            config.setTransparentBackTabTitle(transparentBackTabTitle);
            isTransparentBackTabTitle = false;
        }
        if (isTransparentSelectedTabTitle) {
            config.setTransparentSelectedTabTitle(transparentSelectedTabTitle);
            isTransparentSelectedTabTitle = false;
        }
        if (isGradientFieldNOFLC) {
            config.setGradientFieldNOFLC(gradientFieldNOFLC);
            isGradientFieldNOFLC = false;
        }
        if (isChangeColorMain) {
            config.setColorMain(new ColorAct(mainColor, colorMainCheck.isSelected()));
            isChangeColorMain = false;
            setMainColor(mainColor);
        }
        if (isBlueSysColor) {
            config.setBlueSysColor(new ColorAct(blueSysColor, blueSysColorCheck.isSelected()));
            isBlueSysColor = false;
            setBlueSysColor(blueSysColor);
        }
        if (isDarkShadowSysColor) {
            config.setDarkShadowSysColor(new ColorAct(darkShadowSysColor, darkShadowSysColorCheck.isSelected()));
            isDarkShadowSysColor = false;
            setDarkShadowSysColor(darkShadowSysColor);
        }
        if (isMidSysColor) {
            config.setMidSysColor(new ColorAct(midSysColor, midSysColorCheck.isSelected()));
            isMidSysColor = false;
            setMidSysColor(midSysColor);
        }
        if (isLightYellowColor) {
            config.setLightYellowColor(new ColorAct(lightYellowColor, lightYellowColorCheck.isSelected()));
            isLightYellowColor = false;
            setLightYellowColor(lightYellowColor);
        }
        if (isRedColor) {
            config.setRedColor(new ColorAct(redColor, redColorCheck.isSelected()));
            isRedColor = false;
            setRedColor(redColor);
        }
        if (isLightRedColor) {
            config.setLightRedColor(new ColorAct(lightRedColor, lightRedColorCheck.isSelected()));
            isLightRedColor = false;
            setLightRedColor(lightRedColor);
        }
        if (isLightGreenColor) {
            config.setLightGreenColor(new ColorAct(lightGreenColor, lightGreenColorCheck.isSelected()));
            isLightGreenColor = false;
            setLightGreenColor(lightGreenColor);
        }
        if (isShadowYellowColor) {
            config.setShadowYellowColor(new ColorAct(shadowYellowColor, shadowYellowColorCheck.isSelected()));
            isShadowYellowColor = false;
            setShadowYellowColor(shadowYellowColor);
        }
        if (isSysColor) {
            config.setSysColor(new ColorAct(sysColor, sysColorCheck.isSelected()));
            isSysColor = false;
            setSysColor(sysColor);
        }
        if (isLightSysColor) {
            config.setLightSysColor(new ColorAct(lightSysColor, lightSysColorCheck.isSelected()));
            isLightSysColor = false;
            setLightSysColor(lightSysColor);
        }
        if (isDefaultFontColor) {
            config.setDefaultFontColor(new ColorAct(defaultFontColor, defaultFontColorCheck.isSelected()));
            isDefaultFontColor = false;
            setDefaultFontColor(defaultFontColor);
        }
        if (isSilverColor) {
            config.setSilverColor(new ColorAct(silverColor, silverColorCheck.isSelected()));
            isSilverColor = false;
            setSilverColor(silverColor);
        }
        if (isShadowsGreyColor) {
            config.setShadowsGreyColor(new ColorAct(shadowsGreyColor, shadowsGreyColorCheck.isSelected()));
            isShadowsGreyColor = false;
            setShadowsGreyColor(shadowsGreyColor);
        }
        if (isKeywordColor) {
            config.setKeywordColor(new ColorAct(keywordColor, keywordColorCheck.isSelected()));
            isKeywordColor = false;
            setKeywordColor(keywordColor);
        }
        if (isVariableColor) {
            config.setVariableColor(new ColorAct(variableColor, variableColorCheck.isSelected()));
            isVariableColor = false;
            setVariableColor(variableColor);
        }
        if (isClientVariableColor) {
            config.setClientVariableColor(new ColorAct(clientVariableColor, clientVariableColorCheck.isSelected()));
            isClientVariableColor = false;
            setClientVariableColor(clientVariableColor);
        }
        if (isCommentColor) {
            config.setCommentColor(new ColorAct(commentColor, commentColorCheck.isSelected()));
            isCommentColor = false;
            setCommentColor(commentColor);
        }
        if (isChangeObjectBrowserLimit) {
            config.setObjectBrowserLimit(objectBrowserLimit);
            isChangeObjectBrowserLimit = false;
        }
        if (isChangeIsObjectBrowserLimit) {
            config.setObjectBrowserLimit(isObjectBrowserLimit);
            isChangeIsObjectBrowserLimit = false;
        }
        if (isChangeIsObjectBrowserLimitForClasses) {
            config.setObjectBrowserLimitForClasses(isObjectBrowserLimitForClasses);
            isChangeIsObjectBrowserLimitForClasses = false;
        }
    }

    /**
     * Reset config.
     */
    public void resetConfig() {
        if (showMessageQuestion("Вы действительно ходите сбросить все настройки на их значениям \"По умолчанию\"") == BUTTON_YES) {
            // сброс установок
            gradientMainFrame = Constants.GLOBAL_DEF_GRADIENT;
            gradientControlPanel = new GradientColor();
            gradientMenuPanel = new GradientColor();
            transparentMain = false;
            transparentDialog = true;
            transparentCellTable = 0;
            colorHeaderTable = null;
            colorTabTitle = null;
            colorBackTabTitle = null;
            colorFontTabTitle = null;
            colorFontBackTabTitle = null;
            transparentBackTabTitle = 0;
            transparentSelectedTabTitle = 0;
            gradientFieldNOFLC = new GradientColor();
            
            // системные переменные
            mainColor = MAIN_COLOR;
            blueSysColor = BLUE_SYS_COLOR;
            darkShadowSysColor = DARK_SHADOW_SYS_COLOR;
            midSysColor = MID_SYS_COLOR;
            lightYellowColor = LIGHT_YELLOW_COLOR;
            redColor = RED_COLOR;
            lightRedColor = LIGHT_RED_COLOR;
            lightGreenColor = LIGHT_GREEN_COLOR;
            shadowYellowColor = SHADOW_YELLOW_COLOR;
            sysColor = SYS_COLOR;
            lightSysColor = LIGHT_SYS_COLOR;
            defaultFontColor = DEFAULT_FONT_COLOR;
            silverColor = SILVER_COLOR;
            shadowsGreyColor = SHADOWS_GREY_COLOR;
            keywordColor = KEYWORD_COLOR;
            variableColor = VARIABLE_COLOR;
            clientVariableColor = CLIENT_VARIABLE_COLOR;
            commentColor = COMMENT_COLOR;
            objectBrowserLimit = Constants.LIMIT_VIEW_OBJ_CLASSES;
            isObjectBrowserLimit = true;
            isObjectBrowserLimitForClasses = false;
            blueSysColorCheck.setSelected(false);
            darkShadowSysColorCheck.setSelected(false);
            midSysColorCheck.setSelected(false);
            lightYellowColorCheck.setSelected(false);
            redColorCheck.setSelected(false);
            lightRedColorCheck.setSelected(false);
            lightGreenColorCheck.setSelected(false);
            shadowYellowColorCheck.setSelected(false);
            sysColorCheck.setSelected(false);
            lightSysColorCheck.setSelected(false);
            defaultFontColorCheck.setSelected(false);
            silverColorCheck.setSelected(false);
            shadowsGreyColorCheck.setSelected(false);
            keywordColorCheck.setSelected(false);
            variableColorCheck.setSelected(false);
            clientVariableColorCheck.setSelected(false);
            commentColorCheck.setSelected(false);

            isObjectBrowserLimitCheck.setSelected(true);
            isObjectBrowserLimitForClassesCheck.setSelected(false);
            objectBrowserLimitField.setText(objectBrowserLimit + "");

            // отметить все настройки как изменённые
            isChangeGradientMainFrame = true;
            isChangeGradientControlPanel = true;
            isChangeGradientMenuPanel = true;
            isChangeTransparentMain = true;
            isChangeTransparentDialog = true;
            isChangeColorMain = true;
            isTransparentCellTable = true;
            isColorHeaderTable = true;
            isColorTabTitle = true;
            isColorBackTabTitle = true;
            isColorFontTabTitle = true;
            isColorFontBackTabTitle = true;
            isTransparentBackTabTitle = true;
            isTransparentSelectedTabTitle = true;
            isGradientFieldNOFLC = true;

            isBlueSysColor = true;
            isDarkShadowSysColor = true;
            isMidSysColor = true;
            isLightYellowColor = true;
            isRedColor = true;
            isLightRedColor = true;
            isLightGreenColor = true;
            isShadowYellowColor = true;
            isSysColor = true;
            isLightSysColor = true;
            isDefaultFontColor = true;
            isSilverColor = true;
            isShadowsGreyColor = true;
            isKeywordColor = true;
            isVariableColor = true;
            isClientVariableColor = true;
            isCommentColor = true;
            isChangeObjectBrowserLimit = true;
            isChangeIsObjectBrowserLimit = true;
            isChangeIsObjectBrowserLimitForClasses = true;

            setAllConfig();
        }
    }

    /**
     * Show message info.
     * 
     * @param info
     *            the info
     * @return int
     */
    public int showMessageInfo(String info) {
        return MessagesFactory
                .showMessageDialog((Or3Frame) this.getTopLevelAncestor(), INFORMATION_MESSAGE, info);
    }

    /**
     * Show message question.
     * 
     * @param question
     *            the question
     * @return int
     */
    public int showMessageQuestion(String question) {
        return MessagesFactory.showMessageDialog((Or3Frame) this.getTopLevelAncestor(), QUESTION_MESSAGE,
                question);
    }

    /**
     * Update field by check.
     * 
     * @param check
     *            the check
     * @param field
     *            the field
     */
    private void updateFieldByCheck(AdditChecBox check, JTextField field) {
    	String text = field.getText().replaceFirst(" .+", check.isSelected() ? " 1" : " 0");
    	field.setText("");
        try {
        	field.getDocument().insertString(0, text, null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
    }

    /**
     * Обновление отображение состояния компонентов в зависимости от установленного флажка и значения поля.
     * 
     * @param field
     *            поле с кодом цвета и признаком его активности
     * @param check
     *            флажок состояния активности
     * @param pane
     *            панель отображения цвета
     * @param color
     *            устанавливаемый цвет
     */
    private void updateCompByField(JTextField field, AdditChecBox check, JPanel pane, Color color) {
    	String textColor = field.getText();
        check.setSelected(textColor.replaceFirst(".+ ", "").equals("1"));
        try {
            Color col = Color.decode(textColor.replaceFirst(" .+", ""));
            pane.setBackground(col);
            color = col;
        } catch (Exception e) {
        	if (textColor.length() > 0 && textColor.matches(".*"))
        		System.out.println("Ошибка преобразования цвета, для строки " + ESAPI.encoder().encodeForHTML(field.getText()));
        }

    }

    /**
     * Получить status bar.
     * 
     * @return the status bar
     */
    public DesignerStatusBar getStatusBar() {
        return statusPanel;
    }

    /**
     * Получить menu.
     * 
     * @return the menu
     */
    public OrGradientMenuBar getMenu() {
        return menuBar;
    }

    /**
     * Inits the menu.
     */
    public void initMenu() {
        menuBar.add(mainMenu);
        mainMenu.add(okItem);
        mainMenu.addSeparator();
        mainMenu.add(resetItem);
        okItem.addActionListener(this);
        resetItem.addActionListener(this);
        putCurrVal();
    }

    /**
     * Перезагрузка конфигурации.
     */
    public void reloadConfig() {
        // инициализация переменных конфигурации
        gradientMainFrame = conf.getGradientMainFrame();
        gradientControlPanel = conf.getGradientControlPanel();
        gradientMenuPanel = conf.getGradientMenuPanel();
        transparentMain = conf.isTransparentMain();
        transparentDialog = conf.isTransparentDialog();
        transparentCellTable = conf.getTransparentCellTable();
        colorHeaderTable = conf.getColorHeaderTable();
        colorTabTitle = conf.getColorTabTitle();
        colorBackTabTitle = conf.getColorBackTabTitle();
        colorFontTabTitle = conf.getColorFontTabTitle();
        colorFontBackTabTitle = conf.getColorFontBackTabTitle();
        transparentBackTabTitle = conf.getTransparentBackTabTitle();
        transparentSelectedTabTitle = conf.getTransparentSelectedTabTitle();
        gradientFieldNOFLC = conf.getGradientFieldNOFLC();

        objectBrowserLimit = conf.getObjectBrowserLimit();
        isObjectBrowserLimit = conf.isObjectBrowserLimit();
        isObjectBrowserLimitForClasses = conf.isObjectBrowserLimitForClasses();

        // инициализация значений компонентов

        isObjectBrowserLimitCheck.setSelected(isObjectBrowserLimit);
        isObjectBrowserLimitForClassesCheck.setSelected(isObjectBrowserLimitForClasses);
        objectBrowserLimitField.setText(objectBrowserLimit + "");

        gradientMainFrameField.setText(gradientMainFrame.toString());
        gradientControlPanelField.setText(gradientControlPanel.toString());
        gradientMenuPanelField.setText(gradientMenuPanel.toString());

        transparentCellTableField.setText(transparentCellTable + "");
        colorHeaderTableField.setText((colorHeaderTable == null) ? "" : colorHeaderTable.getRGB() + "");
        colorTabTitleField.setText((colorTabTitle == null) ? "" : colorTabTitle.getRGB() + "");
        colorBackTabTitleField.setText((colorBackTabTitle == null) ? "" : colorBackTabTitle.getRGB() + "");
        colorFontTabTitleField.setText((colorFontTabTitle == null) ? "" : colorFontTabTitle.getRGB() + "");
        colorFontBackTabTitleField.setText((colorFontBackTabTitle == null) ? "" : colorFontBackTabTitle.getRGB() + "");
        transparentBackTabTitleField.setText(transparentBackTabTitle + "");
        transparentSelectedTabTitleField.setText(transparentSelectedTabTitle + "");

        gradientFieldNOFLCField.setText(gradientFieldNOFLC.toString());
        gradientMainFramePane.setGradient(gradientMainFrame);
        gradientControlPanelPane.setGradient(gradientControlPanel);
        gradientMenuPanelPane.setGradient(gradientMenuPanel);
        gradientFieldNOFLCPane.setGradient(gradientFieldNOFLC);
        
        colorHeaderTablePane.setBackground(colorHeaderTable);
        colorTabTitlePane.setBackground(colorTabTitle);
        colorBackTabTitlePane.setBackground(colorBackTabTitle);
        colorFontTabTitlePane.setBackground(colorFontTabTitle);
        colorFontBackTabTitlePane.setBackground(colorFontBackTabTitle);
        transparentCellTablePane.setTransparent(transparentCellTable);
        transparentBackTabTitlePane.setTransparent(transparentBackTabTitle);
        transparentSelectedTabTitlePane.setTransparent(transparentSelectedTabTitle);
        transparentMainCheck.setSelected(transparentMain);
        setTransparent(!transparentMain);

        transparentDialogCheck.setFont(getDefaultFont());
        transparentDialogCheck.setSelected(transparentDialog);
        setTransparent(!transparentDialog);

        gradientMainFrameEnabledCheck.setSelected(gradientMainFrame.isEnabled());
        gradientControlPanelEnabledCheck.setSelected(gradientControlPanel.isEnabled());
        gradientMenuPanelEnabledCheck.setSelected(gradientMenuPanel.isEnabled());
        gradientFieldNOFLCEnabledCheck.setSelected(gradientFieldNOFLC.isEnabled());
        
        colorMainPane.setBackground(MAIN_COLOR);
        blueSysColorPane.setBackground(BLUE_SYS_COLOR);
        darkShadowSysColorPane.setBackground(DARK_SHADOW_SYS_COLOR);
        midSysColorPane.setBackground(MID_SYS_COLOR);
        lightYellowColorPane.setBackground(LIGHT_YELLOW_COLOR);
        redColorPane.setBackground(RED_COLOR);
        lightRedColorPane.setBackground(LIGHT_RED_COLOR);
        lightGreenColorPane.setBackground(LIGHT_GREEN_COLOR);
        shadowYellowColorPane.setBackground(SHADOW_YELLOW_COLOR);
        sysColorPane.setBackground(SYS_COLOR);
        lightSysColorPane.setBackground(LIGHT_SYS_COLOR);
        defaultFontColorPane.setBackground(DEFAULT_FONT_COLOR);
        silverColorPane.setBackground(SILVER_COLOR);
        shadowsGreyColorPane.setBackground(SHADOWS_GREY_COLOR);
        keywordColorPane.setBackground(KEYWORD_COLOR);
        variableColorPane.setBackground(VARIABLE_COLOR);
        clientVariableColorPane.setBackground(CLIENT_VARIABLE_COLOR);
        commentColorPane.setBackground(COMMENT_COLOR);

        colorMainPane2.setBackground(conf.getColorMain());
        blueSysColorPane2.setBackground(conf.getBlueSysColor());
        darkShadowSysColorPane2.setBackground(conf.getDarkShadowSysColor());
        midSysColorPane2.setBackground(conf.getMidSysColor());
        lightYellowColorPane2.setBackground(conf.getLightYellowColor());
        redColorPane2.setBackground(conf.getRedColor());
        lightRedColorPane2.setBackground(conf.getLightRedColor());
        lightGreenColorPane2.setBackground(conf.getLightGreenColor());
        shadowYellowColorPane2.setBackground(conf.getShadowYellowColor());
        sysColorPane2.setBackground(conf.getSysColor());
        lightSysColorPane2.setBackground(conf.getLightSysColor());
        defaultFontColorPane2.setBackground(conf.getDefaultFontColor());
        silverColorPane2.setBackground(conf.getSilverColor());
        shadowsGreyColorPane2.setBackground(conf.getShadowsGreyColor());
        keywordColorPane2.setBackground(conf.getKeywordColor());
        variableColorPane2.setBackground(conf.getVariableColor());
        clientVariableColorPane2.setBackground(conf.getClientVariableColor());
        commentColorPane2.setBackground(conf.getCommentColor());

        colorMainCheck.setSelected(isColorActive(conf.getColorMain()));
        blueSysColorCheck.setSelected(isColorActive(conf.getBlueSysColor()));
        darkShadowSysColorCheck.setSelected(isColorActive(conf.getDarkShadowSysColor()));
        midSysColorCheck.setSelected(isColorActive(conf.getMidSysColor()));
        lightYellowColorCheck.setSelected(isColorActive(conf.getLightYellowColor()));
        redColorCheck.setSelected(isColorActive(conf.getRedColor()));
        lightRedColorCheck.setSelected(isColorActive(conf.getLightRedColor()));
        lightGreenColorCheck.setSelected(isColorActive(conf.getLightGreenColor()));
        shadowYellowColorCheck.setSelected(isColorActive(conf.getShadowYellowColor()));
        sysColorCheck.setSelected(isColorActive(conf.getSysColor()));
        lightSysColorCheck.setSelected(isColorActive(conf.getLightSysColor()));
        defaultFontColorCheck.setSelected(isColorActive(conf.getDefaultFontColor()));
        silverColorCheck.setSelected(isColorActive(conf.getSilverColor()));
        shadowsGreyColorCheck.setSelected(isColorActive(conf.getShadowsGreyColor()));
        keywordColorCheck.setSelected(isColorActive(conf.getKeywordColor()));
        variableColorCheck.setSelected(isColorActive(conf.getVariableColor()));
        clientVariableColorCheck.setSelected(isColorActive(conf.getClientVariableColor()));
        commentColorCheck.setSelected(isColorActive(conf.getCommentColor()));

        colorMainField.setText(conf.getColorMain() == null ? "" : conf.getColorMain().getRGBAct());
        blueSysColorField.setText(conf.getBlueSysColor() == null ? "" : conf.getBlueSysColor().getRGBAct());
        darkShadowSysColorField.setText(conf.getDarkShadowSysColor() == null ? "" : conf.getDarkShadowSysColor().getRGBAct());
        midSysColorField.setText(conf.getMidSysColor() == null ? "" : conf.getMidSysColor().getRGBAct());
        lightYellowColorField.setText(conf.getLightYellowColor() == null ? "" : conf.getLightYellowColor().getRGBAct());
        redColorField.setText(conf.getRedColor() == null ? "" : conf.getRedColor().getRGBAct());
        lightRedColorField.setText(conf.getLightRedColor() == null ? "" : conf.getLightRedColor().getRGBAct());
        lightGreenColorField.setText(conf.getLightGreenColor() == null ? "" : conf.getLightGreenColor().getRGBAct());
        shadowYellowColorField.setText(conf.getShadowYellowColor() == null ? "" : conf.getShadowYellowColor().getRGBAct());
        sysColorField.setText(conf.getSysColor() == null ? "" : conf.getSysColor().getRGBAct());
        lightSysColorField.setText(conf.getLightSysColor() == null ? "" : conf.getLightSysColor().getRGBAct());
        defaultFontColorField.setText(conf.getDefaultFontColor() == null ? "" : conf.getDefaultFontColor().getRGBAct());
        silverColorField.setText(conf.getSilverColor() == null ? "" : conf.getSilverColor().getRGBAct());
        shadowsGreyColorField.setText(conf.getShadowsGreyColor() == null ? "" : conf.getShadowsGreyColor().getRGBAct());
        keywordColorField.setText(conf.getKeywordColor() == null ? "" : conf.getKeywordColor().getRGBAct());
        variableColorField.setText(conf.getVariableColor() == null ? "" : conf.getVariableColor().getRGBAct());
        clientVariableColorField.setText(conf.getClientVariableColor() == null ? "" : conf.getClientVariableColor().getRGBAct());
        commentColorField.setText(conf.getCommentColor() == null ? "" : conf.getCommentColor().getRGBAct());

        colorMainPane2.setOpaque(colorMainCheck.isSelected());
        blueSysColorPane2.setOpaque(blueSysColorCheck.isSelected());
        darkShadowSysColorPane2.setOpaque(darkShadowSysColorCheck.isSelected());
        midSysColorPane2.setOpaque(midSysColorCheck.isSelected());
        lightYellowColorPane2.setOpaque(lightYellowColorCheck.isSelected());
        redColorPane2.setOpaque(redColorCheck.isSelected());
        lightRedColorPane2.setOpaque(lightRedColorCheck.isSelected());
        lightGreenColorPane2.setOpaque(lightGreenColorCheck.isSelected());
        shadowYellowColorPane2.setOpaque(shadowYellowColorCheck.isSelected());
        sysColorPane2.setOpaque(sysColorCheck.isSelected());
        lightSysColorPane2.setOpaque(lightSysColorCheck.isSelected());
        defaultFontColorPane2.setOpaque(defaultFontColorCheck.isSelected());
        silverColorPane2.setOpaque(silverColorCheck.isSelected());
        shadowsGreyColorPane2.setOpaque(shadowsGreyColorCheck.isSelected());
        keywordColorPane2.setOpaque(keywordColorCheck.isSelected());
        variableColorPane2.setOpaque(variableColorCheck.isSelected());
        clientVariableColorPane2.setOpaque(clientVariableColorCheck.isSelected());
        commentColorPane2.setOpaque(commentColorCheck.isSelected());

        // проверка классов, требуемых для работы
        try {
            config.checkConfigDataBase();
        } catch (KrnException e2) {
            e2.printStackTrace();
        }

        isValid = config.isExistClassConfigGlobal && config.isExistClassConfigLocal && config.isExistClassConfigObject
                && config.isExistClassProperty && config.isCorrectClassUser && config.isCorrectClassConfigGlobal
                && config.isCorrectClassConfigLocal && config.isCorrectClassConfigObject && config.isCorrectClassProperty;
        
            mainMenu.setEnabled(isValid);

    }

    public int processExit() {
        boolean isChanged = isChangeGradientMainFrame || isChangeGradientControlPanel || isChangeGradientMenuPanel
                || isChangeTransparentMain || isChangeTransparentDialog || isChangeColorMain || isTransparentCellTable
                || isColorHeaderTable || isColorTabTitle || isColorBackTabTitle || isColorFontTabTitle || isColorFontBackTabTitle
                || isTransparentBackTabTitle || isTransparentSelectedTabTitle || isGradientFieldNOFLC || isBlueSysColor
                || isDarkShadowSysColor || isMidSysColor || isLightYellowColor || isRedColor || isLightRedColor
                || isLightGreenColor || isShadowYellowColor || isSysColor || isLightSysColor || isDefaultFontColor
                || isSilverColor || isShadowsGreyColor || isKeywordColor || isVariableColor || isClientVariableColor
                || isCommentColor || isChangeObjectBrowserLimit || isChangeIsObjectBrowserLimit
                || isChangeIsObjectBrowserLimitForClasses;

        int res = ButtonsFactory.BUTTON_NOACTION;
        if (isChanged && isValid) {
            res = MessagesFactory.showMessageDialog((JFrame) this.getTopLevelAncestor(), CONFIRM_MESSAGE,
                    "Конфигурация была изменена!\r\nСохранить изменения?");

            if (res == ButtonsFactory.BUTTON_YES) {
                setAllConfig();
            }
        }
        return res;
    }

    /**
     * Вытаскивает все интерфейсы из БД.
     * Последовательно перебирает их.
     * При обнаружении на интерфейсе элемента без UUID данному элементу генерируется UUID.
     * Если был сгененированн хоть один UUID интерфес сохраняется.
     */
    private void generateUUID() {
        // идентификатор узла интерфейсов
        long idFolder = 0;
        long idRoot = 0;
        Kernel krn = null;
        // все интерфейсы системы
        KrnObject[] objs = null;
        try {
            krn = Kernel.instance();
            idFolder = krn.getClassByName("UIFolder").id;
            idRoot = krn.getClassByName("UIRoot").id;
            // получить все интерфейсы
            objs = krn.getClassObjects(krn.getClassByName("UI"), 0);
        } catch (KrnException e1) {
            e1.printStackTrace();
        }

        int k = 0;
        JProgressBar pBar = null;
        InterfaceFrame frame = null;
        KrnObject objKrn;
        Component[] comps;
        // перебор всех интерфейсов сисемы
        for (int i = 0; i < objs.length; i++) {
            objKrn = objs[i];
            // если объект является директорией - пропустить шаг цикла
            if (objKrn.classId == idFolder || objKrn.classId == idRoot) {
                continue;
            }

            try {
                k++; // счетчик интерфейсов
                // проход всех объектов
                pBar = new JProgressBar();
                frame = new InterfaceFrame(objKrn);
                frame.setInterfaceLang(krn.getInterfaceLanguage());
                // инициализация признака генерации UUID
                PropertyHelper.genUUID = false;
                frame.loadMass(pBar);
                // если были сгенерированны UUIDs то сохраняю интерфейс
                if (PropertyHelper.genUUID) {
                    PropertyHelper.genUUID = false;
                    try {
                        // сохранение
                        frame.save(pBar, frame.getUiObject(), false);
                        frame.saveWebConfig(true);
                        System.out.println(k + " Интерфейс обновлён. UID=" + objKrn.uid);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                } else {
                    System.out.println(k + " Интерфейс актуален и не требует обновления. UID=" + objKrn.uid);
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            } finally {
                pBar = null;
                if (frame != null && frame.getRootPanel() != null) {
                    comps = frame.getRootPanel().getComponents();
                    if (comps != null) {
                        for (Component comp : comps) {
                            if (comp instanceof OrGuiComponent) {
                                cleanInterfaceComponents(comp);
                                ((JComponent) comp).removeAll();
                                ((OrPanel) frame.getRootPanel()).removeComponentForMassHtmlGen((OrGuiComponent) comp);
                                comp = null;
                            }
                        }
                        comps = null;
                    }
                    ((OrGuiComponent) frame.getRootPanel()).setXml(null);
                    ((JComponent) frame.getRootPanel()).removeAll();
                }
                frame = null;
            }
        }
        System.out.println("*******************************************");
        System.out.println("Процедура обновления интерфейсов завершена!");
        Runtime.getRuntime().gc();
    }
    
    /**
     * Локальный класс метки, для использования внутри редактора.
     */
    class AdditLabel extends JLabel {

        /**
         * Конструктор новой метки.
         * 
         * @param title
         *            текст метки
         */
        AdditLabel(String title) {
            super(title);
            setForeground(Color.BLACK);
            setFont(getDefaultFont());
        }
    }

    /**
     * Локальный класс флажка, для использования внутри редактора.
     */
    protected class AdditChecBox extends JCheckBox {

        /**
         * Конструктор нового флажка.
         * 
         * @param title
         *            подпись флажка
         */
        AdditChecBox(String title) {
            super(title);
            setForeground(Color.BLACK);
            setFont(getDefaultFont());
            addActionListener(ConfigEditor.this);
        }

        /**
         * Конструктор нового флажка.
         */
        AdditChecBox() {
            this("Активировать");
        }

    }

    /**
     * Локальный класс кнопки, для использования внутри редактора.
     */
    class AdditButton extends JButton {

        /**
         * Конструктор новой кнопки.
         * 
         * @param title
         *            Заголовок кнопки
         */
        AdditButton(String title) {
            super(title);
            setFont(getDefaultFont());
            addActionListener(ConfigEditor.this);
        }

        /**
         * Конструктор новой кнопки.
         */
        AdditButton() {
            this("Задать");
        }
    }

    public class GenHTML extends Thread {
        public void run() {
            isGen = true;
            long idFolder = 0;
            long idRoot = 0;
            Kernel krn = null;
            KrnObject[] objs = null; // все интерфейсы системы
            InterfaceFrame frame = null;
            JProgressBar pBar = null;

            try {
                krn = Kernel.instance();
                idFolder = krn.getClassByName("UIFolder").id;
                idRoot = krn.getClassByName("UIRoot").id;
                objs = krn.getClassObjects(krn.getClassByName("UI"), 0);
            } catch (KrnException e1) {
                e1.printStackTrace();
            }

            long[] langIds = { krn.getLangIdByCode("RU"), krn.getLangIdByCode("KZ") };

            int k = 0;
            int l = objs.length;
            final Or3Frame or3frame = Or3Frame.instance();
            prc = 0;
            Utils.start();
            String warning = "";
            int count = 0;
            for (KrnObject objKrn : objs) {
                k++;
                prc = ((double)(k *100)) / l;
                if (objKrn.classId == idFolder || objKrn.classId == idRoot) {
                    continue;
                }
                try {
                	UserSessionValue us = krn.vcsLockObject(objKrn.id);
					if(us != null ) {
						count++;
						warning += count + ": Интерфейс c uid " + objKrn.uid + " редактируется пользователем " + us.name + "\n";
					} else {
						System.out.println("Starting HTML for UID=" + objKrn.uid);
						pBar = new JProgressBar();
						frame = new InterfaceFrame(objKrn);
						frame.setInterfaceLang(krn.getObjectById(langIds[0], 0));
						frame.loadMass(pBar);
						frame.saveWebConfig(true);
						System.out.println("Finish HTML for UID=" + objKrn.uid);
						EventQueue.invokeLater(new Runnable() {
							public void run() {
								or3frame.setState("Выполненно: " + String.format("%.2f", ConfigEditor.this.prc) + "%");
							}
						});
					}

                } catch (Exception e1) {
                    e1.printStackTrace();
                } finally {
                    pBar = null;
                    if (frame != null && frame.getRootPanel() != null) {
                        Component[] comps = frame.getRootPanel().getComponents();
                        if (comps != null) {
                            for (Component comp : comps) {
                                    if (comp instanceof OrGuiComponent) {
                                        cleanInterfaceComponents(comp);
                                        ((JComponent) comp).removeAll();
                                        ((OrPanel) frame.getRootPanel()).removeComponentForMassHtmlGen((OrGuiComponent) comp);
                                        comp = null;
                                    }
                            }
                            comps = null;
                        }
                        ((OrGuiComponent) frame.getRootPanel()).setXml(null);
                        ((JComponent) frame.getRootPanel()).removeAll();
                    }
                    frame = null;
                }
            }
            objs = null;
            System.out.print("Генерация HTML шаблонов завершена. ");
            Utils.finish();
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    or3frame.setState("Генерация HTML шаблонов завершена.");
                }
            });
            isGen = false;
            
            if(count > 0) {
            	Container cont = ConfigEditor.this.getTopLevelAncestor();
            	String errMessage = "";
            	if(count > 1) {
            		errMessage = "Следующие итнерфейсы редактируются:\n";
            	} else errMessage = "Следующий итнерфейс редактируется:\n";
            	
            	errMessage += warning;
            	JTextArea textArea = new JTextArea(errMessage);
        		JScrollPane scrollPane = new JScrollPane(textArea);
        		scrollPane.setPreferredSize(new Dimension(500, 200));

            	JOptionPane.showMessageDialog(cont,
            			scrollPane,
            			"Ошибка",
            			JOptionPane.ERROR_MESSAGE);
            }

        }
    }
    
    public class ResaveUIForEGKN extends Thread {
        public void run() {
            isUIResaveInProcess = true;
            long idFolder = 0;
            long idRoot = 0;
            Kernel krn = null;
            KrnObject[] objs = null;

            try {
                krn = Kernel.instance();
                idFolder = krn.getClassByName("UIFolder").id;
                idRoot = krn.getClassByName("UIRoot").id;
                objs = krn.getClassObjects(krn.getClassByName("UI"), 0);
            } catch (KrnException e) {
                e.printStackTrace();
            }

            int k = 0;
            int l = objs.length;
            final Or3Frame or3frame = Or3Frame.instance();
            prc = 0;
            Utils.start();
            for (KrnObject obj : objs) {
                k++;
                prc = ((double)(k *100)) / l;
                if (obj.classId == idFolder || obj.classId == idRoot) {
                    continue;
                }
                try {
                    System.out.println("Starting resave UI for UID=" + obj.uid);
                    
                    byte[] data = krn.getBlob(obj, "config", 0, 0, 0);
                    if (data.length > 0) {
                        SAXBuilder builder = new SAXBuilder();
                        ByteArrayInputStream is = new ByteArrayInputStream(data);
            	   		Document document = (Document) builder.build(is);
            	   		is.close();
            	   		Element rootElement = document.getRootElement();
            	   		
            	   		editIfcForEGKN(rootElement);

            	   		ByteArrayOutputStream os = new ByteArrayOutputStream();
            	   		XMLOutputter out = new XMLOutputter();
            	        out.getFormat().setEncoding("UTF-8");
            	        out.output(document, os);
            	        os.close();
                        
            	        krn.setBlob(obj.id, obj.classId, "config", 0, os.toByteArray(), 0, 0);
                    }
                    
                    System.out.println("Finish resave UI for UID=" + obj.uid);
                    EventQueue.invokeLater(new Runnable() {
                        public void run() {
                            or3frame.setState("Выполненно: " + String.format("%.2f", ConfigEditor.this.prc) + "%");
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.print("Пересохранение интерфейсов для ЕГКН завершено.");
            Utils.finish();
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    or3frame.setState("Пересохранение интерфейсов для ЕГКН завершено.");
                }
            });
            isUIResaveInProcess = false;
        }
    }
    
    public class ResaveProcForEGKN extends Thread {
        public void run() {
            isProcResaveInProcess = true;
            long idFolder = 0;
            long idRoot = 0;
            Kernel krn = null;
            KrnObject[] objs = null;

            try {
                krn = Kernel.instance();
                idFolder = krn.getClassByName("ProcessDefFolder").id;
                idRoot = krn.getClassByName("ProcessDefRoot").id;
                objs = krn.getClassObjects(krn.getClassByName("ProcessDef"), 0);
            } catch (KrnException e) {
                e.printStackTrace();
            }

            int k = 0;
            int l = objs.length;
            final Or3Frame or3frame = Or3Frame.instance();
            prc = 0;
            Utils.start();
            for (KrnObject obj : objs) {
                k++;
                prc = ((double)(k *100)) / l;
                if (obj.classId == idFolder || obj.classId == idRoot) {
                    continue;
                }
                try {
                    System.out.println("Starting resave process for UID=" + obj.uid);
                    
                    // Пересохранение атрибута config
                    byte[] data = krn.getBlob(obj, "config", 0, 0, 0);
                    if (data.length > 0) {
                        SAXBuilder builder = new SAXBuilder();
                        ByteArrayInputStream is = new ByteArrayInputStream(data);
            	   		Document document = (Document) builder.build(is);
            	   		is.close();
            	   		Element rootElement = document.getRootElement();
            	   		
            	      	Element responsibleElement = rootElement.getChild("responsible");
            	    	if (responsibleElement != null) {
            	    		Element krnResponsibleElement = rootElement.getChild("KRNresponsible");
        	    			
            	    		responsibleElement.setText("/*" + responsibleElement.getText() + (krnResponsibleElement != null ? "\n" + krnResponsibleElement.getText() : "") + "*/");
        	    			
        	    			rootElement.removeContent(krnResponsibleElement);

            	    		ByteArrayOutputStream os = new ByteArrayOutputStream();
                	   		XMLOutputter out = new XMLOutputter();
                	        out.getFormat().setEncoding("UTF-8");
                	        out.output(document, os);
                	        os.close();
                            
                	        krn.setBlob(obj.id, obj.classId, "config", 0, os.toByteArray(), 0, 0);
            	    	}
                    }
                    
                    // Пересохранение атрибута diagram
                    data = krn.getBlob(obj, "diagram", 0, 0, 0);
                    if (data.length > 0) {
                        SAXBuilder builder = new SAXBuilder();
                        ByteArrayInputStream is = new ByteArrayInputStream(data);
            	   		Document document = (Document) builder.build(is);
            	   		is.close();
            	   		Element rootElement = document.getRootElement();
            	   		
            	   		Element processElement = rootElement.getChild("process");
            	    	if (processElement != null) {
            	    		Element responsibleElement = null;
            	    		Element krnResponsibleElement = null;
            	    		List<Element> propertyElements = processElement.getChildren("property");
            	    		for (Element propertyElement: propertyElements) {
            	    			if ("responsible".equals(propertyElement.getAttributeValue("name"))) {
            	    				responsibleElement = propertyElement;
            	    			} else if ("KRNresponsible".equals(propertyElement.getAttributeValue("name"))) {
            	    				krnResponsibleElement = propertyElement;
            	    			}
            	    		}
            	    		
            	    		if (responsibleElement != null) {
            	    			responsibleElement.setText("/*" + responsibleElement.getText() + (krnResponsibleElement != null ? "\n" + krnResponsibleElement.getText() : "") + "*/");
            					
            	    			processElement.removeContent(krnResponsibleElement);
            	    			
            	    			ByteArrayOutputStream os = new ByteArrayOutputStream();
                    	   		XMLOutputter out = new XMLOutputter();
                    	        out.getFormat().setEncoding("UTF-8");
                    	        out.output(document, os);
                    	        os.close();
                                
                    	        krn.setBlob(obj.id, obj.classId, "diagram", 0, os.toByteArray(), 0, 0);
            	    		}
            	    	}
                    }
                    
                    System.out.println("Finish resave process for UID=" + obj.uid);
                    EventQueue.invokeLater(new Runnable() {
                        public void run() {
                            or3frame.setState("Выполненно: " + String.format("%.2f", ConfigEditor.this.prc) + "%");
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.print("Пересохранение процессов для ЕГКН завершено.");
            Utils.finish();
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    or3frame.setState("Пересохранение процессов для ЕГКН завершено.");
                }
            });
            isProcResaveInProcess = false;
        }
    }
    
    private void editIfcForEGKN(Element element) {
		String classAttr = element.getAttributeValue("class");
		/*if ("ScrollPane".equals(classAttr)) {
			// Пункт 2
			Element viewCompElement = element.getChild("viewComp");
			if (viewCompElement != null) {
				Element componentElement = viewCompElement.getChild("Component");
				if (componentElement != null) {
					Element parentElement = element.getParentElement();
					int index = parentElement.indexOf(element);
					componentElement.detach();
					Element posElement = element.getChild("pos");
					if (posElement != null) {
						posElement.detach();
						componentElement.removeChild("pos");
						componentElement.addContent(posElement);
					}
					parentElement.addContent(index, componentElement);
					parentElement.removeContent(element);
				}
			}
		} else */if ("HyperPopup".equals(classAttr)) {
			/*// Пункт 3
			Element panelElement = new Element("Component");
			panelElement.setAttribute("class", "Panel");
			
			Element hyperPopupPosElement = element.getChild("pos");
			Element posElement = (Element) hyperPopupPosElement.clone();
			
			Element xElement = hyperPopupPosElement.getChild("x");
			if (xElement == null) {
				xElement = new Element("x");
				hyperPopupPosElement.addContent(xElement);
			}
			xElement.setText("0");

			Element yElement = hyperPopupPosElement.getChild("y");
			if (yElement == null) {
				yElement = new Element("y");
				hyperPopupPosElement.addContent(yElement);
			}
			yElement.setText("0");
			
			posElement.removeChild("pref");
			posElement.removeChild("max");
			posElement.removeChild("min");
			posElement.removeChild("insets");						
			panelElement.addContent(posElement);
			
			Element childrenElement = new Element("children");
			Element parentElement = element.getParentElement();
			int index = parentElement.indexOf(element);
			element.detach();
			childrenElement.addContent(element);
			panelElement.addContent(childrenElement);
			parentElement.addContent(index, panelElement);
			// Пункт 4
			setFontParams(element);
			// Пункт 5
			setHeightParams(element);
			// Пункт 7
//			setIcon(element, "hyperPopupIcon.png");
*/
			Element viewElement = element.getChild("view");
			if (viewElement == null) {
				viewElement = new Element("view");
				Element showIconElement = new Element("showIcon");
				showIconElement.setText("true");
				viewElement.addContent(showIconElement);
				element.addContent(viewElement);
			} else {
				Element showIconElement = viewElement.getChild("showIcon");
				if (showIconElement == null) {
					showIconElement = new Element("showIcon");
					showIconElement.setText("true");
					viewElement.addContent(showIconElement);
				} else {
					showIconElement.setText("true");
				}
			}
			
			Element posElement = element.getChild("pos");
			if (posElement == null) {
				posElement = new Element("pos");
				Element anchorImageElement = new Element("anchorImage");
				anchorImageElement.setText("13");
				posElement.addContent(anchorImageElement);
				element.addContent(posElement);
			} else {
				Element anchorImageElement = posElement.getChild("anchorImage");
				if (anchorImageElement == null) {
					anchorImageElement = new Element("anchorImage");
					anchorImageElement.setText("13");
					posElement.addContent(anchorImageElement);
				} else {
					anchorImageElement.setText("13");
				}
			}
			
			setIcon(element, "VSlider1.gif");
			/*
		} else if ("Button".equals(classAttr)) {
			// Пункт 4
			setFontParams(element);
			// Пункт 5
			setHeightParams(element);
			// Пункт 6
//			setIcon(element, "buttonIcon.png");
		} else if ("DateField".equals(classAttr)) {
			// Пункт 8
	    	Element posElement = element.getChild("pos");
	    	if (posElement != null) {
		    	Element insetsElement = element.getChild("insets");
		    	if (insetsElement != null) {
		    		insetsElement.removeChild("rightInsets");
		    	}
	    	}
			// Пункт 9
			setShowDateChooser(element);
			
			// Изменение размеров в зависимости от формата
			Element viewElement = element.getChild("view");
			if (viewElement != null) {
				Element formatElement = viewElement.getChild("format");
				if (formatElement != null) {
					String format = formatElement.getText();
					if ("0".equals(format)) {
						setSizeForDateField(element, "20", "75", "20", "75", "20", "75");
					} else if ("1".equals(format)) {
						setSizeForDateField(element, "20", "110", "20", "110", "20", "110");
					} else if ("3".equals(format)) {
						setSizeForDateField(element, "20", "145", "20", "145", "20", "145");
					}
 				}
			}
		/*} else if ("DateColumn".equals(classAttr)) {
			// Пункт 9
			setShowDateChooser(element);
		} else if ("Table".equals(classAttr)) {
			// Пункт 11
			Element viewElement = element.getChild("view");
			if (viewElement != null) {
				Element naviElement = viewElement.getChild("navi");
				if (naviElement != null) {
					Element showElement = naviElement.getChild("show");
					if (showElement != null) {
						if ("true".equals(showElement.getText())) {
							Element addPanElement = naviElement.getChild("addPan");
							if (addPanElement == null) {
								addPanElement = new Element("addPan");
								addPanElement.setText("true");
								naviElement.addContent(addPanElement);
							} else {
								addPanElement.setText("true");
							}
						}
					}
				}
			}*/
		} else if ("SplitPane".equals(classAttr)) {
			Element leftElement = element.getChild("left");
			if (leftElement != null) {
				List<Element> children = leftElement.getChildren();
		    	for (int i = 0; i < children.size(); i++) {
		    		editIfcForEGKN(children.get(i));
		    	}
			}
			Element rightElement = element.getChild("right");
			if (leftElement != null) {
				List<Element> children = rightElement.getChildren();
		    	for (int i = 0; i < children.size(); i++) {
		    		editIfcForEGKN(children.get(i));
		    	}
			}
		/*} else if ("Label".equals(classAttr)) {
			// Пункт 13
			Element viewElement = element.getChild("view");
			if (viewElement != null) {
				Element fontElement = viewElement.getChild("font");
				if (fontElement != null) {
					Element fontGElement = fontElement.getChild("fontG");
					if (fontGElement != null) {
						if (fontGElement.getText().contains("BOLD")) {
							Element fontColorElement = fontElement.getChild("fontColor");
							if (fontColorElement != null) {
								if ("-16777216".equals(fontColorElement.getText())) {
									fontColorElement.setText("-16751002");
								}
							}
						}
					}
				}
			}
		} else if ("HyperLabel".equals(classAttr)) {
			// Пункт 16
			Element viewElement = element.getChild("view");
			if (viewElement == null) {
				viewElement = new Element("view");
				Element visibleArrowElement = new Element("visibleArrow");
				visibleArrowElement.setText("0");
				viewElement.addContent(visibleArrowElement);
				element.addContent(viewElement);
			} else {
				Element visibleArrowElement = viewElement.getChild("visibleArrow");
				if (visibleArrowElement == null) {
					visibleArrowElement = new Element("visibleArrow");
					visibleArrowElement.setText("0");
					viewElement.addContent(visibleArrowElement);
				} else {
					visibleArrowElement.setText("0");
				}
			}
			Element fontElement = viewElement.getChild("font");
			if (fontElement == null) {
				fontElement = new Element("font");
				Element fontGElement = new Element("fontG");
				fontGElement.setText("Dialog-BOLD-14");
				fontElement.addContent(fontGElement);
				Element fontColorElement = new Element("fontColor");
				fontColorElement.setText("-16751002");
				fontElement.addContent(fontColorElement);
				viewElement.addContent(fontElement);
			} else {
				Element fontGElement = fontElement.getChild("fontG");
				if (fontGElement == null) {
					fontGElement = new Element("fontG");
					fontElement.addContent(fontGElement);
				}
				fontGElement.setText("Dialog-BOLD-14");
				
				Element fontColorElement = fontElement.getChild("fontColor");
				if (fontColorElement == null) {
					fontColorElement = new Element("fontColor");
					fontElement.addContent(fontColorElement);
				}
				fontColorElement.setText("-16751002");
			}
		} else if ("Note".equals(classAttr)) {
			// Пункт 14
			Element viewElement = element.getChild("view");
			if (viewElement == null) {
				viewElement = new Element("view");
				Element opaqueElement = new Element("opaque");
				opaqueElement.setText("0");
				viewElement.addContent(opaqueElement);
				element.addContent(viewElement);
			} else {
				Element opaqueElement = viewElement.getChild("opaque");
				if (opaqueElement == null) {
					opaqueElement = new Element("opaque");
					opaqueElement.setText("0");
					viewElement.addContent(opaqueElement);
				} else {
					opaqueElement.setText("0");
				}
			}*/
		} else {
			/*if ("Panel".equals(classAttr)) {
				// Пункт 12
				Element viewElement = element.getChild("view");
				if (viewElement != null) {
					Element borderElement = viewElement.getChild("border");
					if (borderElement != null) {
						Element borderTypeElement = borderElement.getChild("borderType");
						if (borderTypeElement != null) {
							String borderType = borderTypeElement.getText();
							StringTokenizer st = new StringTokenizer(borderType, "|");
							int idx = 0;
							String oldColor = null;
							while (st.hasMoreTokens()) {
								String token = st.nextToken();
						        if (idx == 2) {
						        	oldColor = token;
						        	break;
								}					        
						        idx++;
						    }
							if (oldColor != null) {
								borderType = borderType.replace(oldColor, "-16751002");
								borderTypeElement.setText(borderType);
							}
						}
					}
				}
			}*/
					
			Element childrenElement = element.getChild("children");
			if (childrenElement != null) {
				List<Element> children = childrenElement.getChildren();
		    	for (int i = 0; i < children.size(); i++) {
	    			editIfcForEGKN(children.get(i));
		    	}
			}
		}
    }
    
    private void setSizeForDateField(Element element, String prefHeight, String prefWidth, String minHeight, String minWidth, String maxHeight, String maxWidth) {
    	Element posElement = element.getChild("pos");
		if (posElement == null) {
			posElement = new Element("pos");
			Element prefElement = new Element("pref");
			Element prefHeightElement = new Element("height");
			prefHeightElement.setText(prefHeight);
			prefElement.addContent(prefHeightElement);
			Element prefWidthElement = new Element("width");
			prefWidthElement.setText(prefWidth);
			prefElement.addContent(prefWidthElement);
			posElement.addContent(prefElement);
			
			Element maxElement = new Element("max");
			Element maxHeightElement = new Element("height");
			maxHeightElement.setText(maxHeight);
			maxElement.addContent(maxHeightElement);
			Element maxWidthElement = new Element("width");
			maxWidthElement.setText(maxWidth);
			maxElement.addContent(maxWidthElement);
			posElement.addContent(maxElement);
			
			Element minElement = new Element("min");
			Element minHeightElement = new Element("height");
			minHeightElement.setText(minHeight);
			minElement.addContent(minHeightElement);
			Element minWidthElement = new Element("width");
			minWidthElement.setText(minWidth);
			minElement.addContent(minWidthElement);
			posElement.addContent(minElement);
			element.addContent(posElement);
		} else {
			Element prefElement = posElement.getChild("pref");
			if (prefElement == null) {
				prefElement = new Element("pref");
				Element prefHeightElement = new Element("height");
				prefHeightElement.setText(prefHeight);
				prefElement.addContent(prefHeightElement);
				Element prefWidthElement = new Element("width");
				prefWidthElement.setText(prefWidth);
				prefElement.addContent(prefWidthElement);
				posElement.addContent(prefElement);
			} else {
				Element prefHeightElement = prefElement.getChild("height");
				if (prefHeightElement == null) {
					prefHeightElement = new Element("height");
					prefHeightElement.setText(prefHeight);
					prefElement.addContent(prefHeightElement);
				} else {
					prefHeightElement.setText(prefHeight);
				}
				
				Element prefWidthElement = prefElement.getChild("width");
				if (prefWidthElement == null) {
					prefWidthElement = new Element("width");
					prefWidthElement.setText(prefWidth);
					prefElement.addContent(prefWidthElement);
				} else {
					prefWidthElement.setText(prefWidth);
				}
			}
			
			Element maxElement = posElement.getChild("max");
			if (maxElement == null) {
				maxElement = new Element("max");
				Element maxHeightElement = new Element("height");
				maxHeightElement.setText(maxHeight);
				maxElement.addContent(maxHeightElement);
				Element maxWidthElement = new Element("width");
				maxWidthElement.setText(maxWidth);
				maxElement.addContent(maxWidthElement);
				posElement.addContent(maxElement);
			} else {
				Element maxHeightElement = maxElement.getChild("height");
				if (maxHeightElement == null) {
					maxHeightElement = new Element("height");
					maxHeightElement.setText(maxHeight);
					maxElement.addContent(maxHeightElement);
				} else {
					maxHeightElement.setText(maxHeight);
				}
				
				Element maxWidthElement = maxElement.getChild("width");
				if (maxWidthElement == null) {
					maxWidthElement = new Element("width");
					maxWidthElement.setText(maxWidth);
					maxElement.addContent(maxWidthElement);
				} else {
					maxWidthElement.setText(maxWidth);
				}
			}
			
			Element minElement = posElement.getChild("min");
			if (minElement == null) {
				minElement = new Element("min");
				Element minHeightElement = new Element("height");
				minHeightElement.setText(minHeight);
				minElement.addContent(minHeightElement);
				Element minWidthElement = new Element("width");
				minWidthElement.setText(minWidth);
				minElement.addContent(minWidthElement);
				posElement.addContent(minElement);
			} else {
				Element minHeightElement = minElement.getChild("height");
				if (minHeightElement == null) {
					minHeightElement = new Element("height");
					minHeightElement.setText(minHeight);
					minElement.addContent(minHeightElement);
				} else {
					minHeightElement.setText(minHeight);
				}
				
				Element minWidthElement = minElement.getChild("width");
				if (minWidthElement == null) {
					minWidthElement = new Element("width");
					minWidthElement.setText(minWidth);
					minElement.addContent(minWidthElement);
				} else {
					minWidthElement.setText(minWidth);
				}
			}
		}
    }
    
    private void setShowDateChooser(Element element) {
    	Element viewElement = element.getChild("view");
    	if (viewElement == null) {
			viewElement = new Element("view");
			Element showDateChooserElement = new Element("showDateChooser");
			showDateChooserElement.setText("1");
			viewElement.addContent(showDateChooserElement);
			element.addContent(viewElement);
    	} else {
	    	Element showDateChooserElement = element.getChild("showDateChooser");
	    	if (showDateChooserElement == null) {
	    		showDateChooserElement = new Element("showDateChooser");
				showDateChooserElement.setText("1");
				viewElement.addContent(showDateChooserElement);
	    	} else {
				showDateChooserElement.setText("1");
	    	}
    	}
    }
    
    private void setFontParams(Element element) {
    	Element viewElement = element.getChild("view");
		if (viewElement == null) {
			viewElement = new Element("view");
			Element fontElement = new Element("font");
			Element fontGElement = new Element("fontG");
			fontGElement.setText("Tahoma-PLAIN-12");
			fontElement.addContent(fontGElement);
			Element fontColorElement = new Element("fontColor");
			fontColorElement.setText("-16751002");
			fontElement.addContent(fontColorElement);
			viewElement.addContent(fontElement);
			element.addContent(viewElement);
		} else {
			Element fontElement = viewElement.getChild("font");
			if (fontElement == null) {
				fontElement = new Element("font");
				Element fontGElement = new Element("fontG");
				fontGElement.setText("Tahoma-PLAIN-12");
				fontElement.addContent(fontGElement);
				Element fontColorElement = new Element("fontColor");
				fontColorElement.setText("-16751002");
				fontElement.addContent(fontColorElement);
				viewElement.addContent(fontElement);
			} else {
				Element fontGElement = fontElement.getChild("fontG");
				if (fontGElement == null) {
					fontGElement = new Element("fontG");
					fontElement.addContent(fontGElement);
				}
				fontGElement.setText("Tahoma-PLAIN-12");
				
				Element fontColorElement = fontElement.getChild("fontColor");
				if (fontColorElement == null) {
					fontColorElement = new Element("fontColor");
					fontElement.addContent(fontColorElement);
				}
				fontColorElement.setText("-16751002");
			}
		}
    }
    
    private void setIcon(Element element, String path) {
    	try {
    		URL url = Utils.class.getResource("/kz/tamur/comps/images/" + path);
    		if (url != null) {
    			BufferedImage image = ImageIO.read(url);
    			ByteArrayOutputStream baos = new ByteArrayOutputStream();
    			ImageIO.write(image, "gif", baos);
    			byte[] bytes = baos.toByteArray();
	        
		    	Element viewElement = element.getChild("view");
				if (viewElement == null) {
					viewElement = new Element("view");
					Element imageElement = new Element("image");
					imageElement.setText(Utils.getImageToString(bytes));
					viewElement.addContent(imageElement);
					element.addContent(viewElement);
				} else {
			    	Element imageElement = element.getChild("image");
					if (imageElement == null) {
						imageElement = new Element("image");
						imageElement.setText(Utils.getImageToString(bytes));
						viewElement.addContent(imageElement);
					} else {
						imageElement.setText(Utils.getImageToString(bytes));
					}
				}
    		} else {
    			System.out.println("Иконка '" + path + "' не найдена!");
    		}
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    private void setHeightParams(Element element) {
    	Element posElement = element.getChild("pos");
		if (posElement == null) {
			posElement = new Element("pos");
			Element prefElement = new Element("pref");
			Element prefHeightElement = new Element("height");
			prefHeightElement.setText("5");
			prefElement.addContent(prefHeightElement);
			posElement.addContent(prefElement);
			Element maxElement = new Element("max");
			Element maxHeightElement = new Element("height");
			maxHeightElement.setText("5");
			maxElement.addContent(maxHeightElement);
			posElement.addContent(maxElement);
			Element minElement = new Element("min");
			Element minHeightElement = new Element("height");
			minHeightElement.setText("5");
			minElement.addContent(minHeightElement);
			posElement.addContent(minElement);
			element.addContent(posElement);
		} else {
			Element prefElement = posElement.getChild("pref");
			if (prefElement == null) {
				prefElement = new Element("pref");
				Element prefHeightElement = new Element("height");
				prefHeightElement.setText("5");
				prefElement.addContent(prefHeightElement);
				posElement.addContent(prefElement);
			} else {
				Element prefHeightElement = prefElement.getChild("height");
				if (prefHeightElement == null) {
					prefHeightElement = new Element("height");
					prefHeightElement.setText("5");
					prefElement.addContent(prefHeightElement);
				} else {
					int prefHeight = Integer.parseInt(prefHeightElement.getText());
					prefHeightElement.setText(Integer.toString(prefHeight + 5));
				}
			}
			
			Element maxElement = posElement.getChild("max");
			if (maxElement == null) {
				maxElement = new Element("max");
				Element maxHeightElement = new Element("height");
				maxHeightElement.setText("5");
				maxElement.addContent(maxHeightElement);
				posElement.addContent(maxElement);
			} else {
				Element maxHeightElement = maxElement.getChild("height");
				if (maxHeightElement == null) {
					maxHeightElement = new Element("height");
					maxHeightElement.setText("5");
					maxElement.addContent(maxHeightElement);
				} else {
					int maxHeight = Integer.parseInt(maxHeightElement.getText());
					maxHeightElement.setText(Integer.toString(maxHeight + 5));
				}
			}
			
			Element minElement = posElement.getChild("min");
			if (minElement == null) {
				minElement = new Element("min");
				Element minHeightElement = new Element("height");
				minHeightElement.setText("5");
				minElement.addContent(minHeightElement);
				posElement.addContent(minElement);
			} else {
				Element minHeightElement = minElement.getChild("height");
				if (minHeightElement == null) {
					minHeightElement = new Element("height");
					minHeightElement.setText("5");
					minElement.addContent(minHeightElement);
				} else {
					int minHeight = Integer.parseInt(minHeightElement.getText());
					minHeightElement.setText(Integer.toString(minHeight + 5));
				}
			}
		}
    }
}