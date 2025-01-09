package kz.tamur.rt;

import java.awt.Color;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnObject;

import kz.tamur.comps.models.ColorAct;
import kz.tamur.comps.models.GradientColor;

/**
 * The Class Config.
 */
public class Config {

    private final static int maxLengthHistory = 10;

    /** Хранит параметры градиентной заливки для главного фрейма системы. */
    private GradientColor gradientMainFrame = new GradientColor();

    /** Хранит параметры градиентной заливки для панели управления системы. */
    private GradientColor gradientControlPanel = new GradientColor();

    /** Хранит параметры градиентной заливки для панели меню системы. */
    private GradientColor gradientMenuPanel = new GradientColor();

    /** Настройка прозрачности панелей системы, позволяет включить/выключить возможность работы с ней. */
    private boolean transparentMain;

    /** Прозрачность диалоговых окон */
    private boolean transparentDialog;

    /** Цвет, определяющий основную цветовую гамму интерфейсов. */
    private ColorAct colorMain;

    /** Хранит прозрачность ячеек таблиц и деревьев. */
    private int transparentCellTable;

    /** Хранит Цвет заголовков таблиц и деревьев. */
    private Color colorHeaderTable;

    /** Хранит цвет фона заголовка выбранной вкладки. */
    private Color colorTabTitle;

    /** Хранит цвет фона заголовка фоновых вкладок. */
    private Color colorBackTabTitle;

    /** Хранит цвет шрифта заголовка выбранной вкладки. */
    private Color colorFontTabTitle;

    /** Хранит цвет шрифта заголовка фоновых вкладок. */
    private Color colorFontBackTabTitle;

    /** Хранит прозрачность заголовка фоновой вкладки (в будущем, возможна полупрозрачность, поэтому тип int). */
    private int transparentBackTabTitle;

    /** Хранит прозрачность фона заголовка выбранной вкладки (в будущем, возможна полупрозрачность, поэтому тип int). */
    private int transparentSelectedTabTitle;

    /** Хранит параметры градиентной заливки для полей не прошедших ФЛК. */
    private GradientColor gradientFieldNOFLC = new GradientColor();

    /** Реализует работу с настройкой системной переменной blueSysColor. */
    private ColorAct blueSysColor;

    /** Реализует работу с настройкой системной переменной darkShadowSysColor. */
    private ColorAct darkShadowSysColor;

    /** Реализует работу с настройкой системной переменной midSysColor. */
    private ColorAct midSysColor;

    /** Реализует работу с настройкой системной переменной lightYellowColor. */
    private ColorAct lightYellowColor;

    /** Реализует работу с настройкой системной переменной redColor. */
    private ColorAct redColor;

    /** Реализует работу с настройкой системной переменной lightRedColor. */
    private ColorAct lightRedColor;

    /** Реализует работу с настройкой системной переменной lightGreenColor. */
    private ColorAct lightGreenColor;

    /** Реализует работу с настройкой системной переменной shadowYellowColor. */
    private ColorAct shadowYellowColor;

    /** Реализует работу с настройкой системной переменной sysColor. */
    private ColorAct sysColor;

    /** Реализует работу с настройкой системной переменной lightSysColor. */
    private ColorAct lightSysColor;

    /** Реализует работу с настройкой системной переменной defaultFontColor. */
    private ColorAct defaultFontColor;

    /** Реализует работу с настройкой системной переменной silverColor. */
    private ColorAct silverColor;

    /** Реализует работу с настройкой системной переменной shadowsGreyColor. */
    private ColorAct shadowsGreyColor;

    /** Реализует работу с настройкой системной переменной keywordColor. */
    private ColorAct keywordColor;

    /** Реализует работу с настройкой системной переменной variableColor. */
    private ColorAct variableColor;

    /** Реализует работу с настройкой системной переменной clientVariableColor. */
    private ColorAct clientVariableColor;

    /** Реализует работу с настройкой системной переменной commentColor. */
    private ColorAct commentColor;

    /** Количество объектов, отображаемое редактором классов. */
    private int objectBrowserLimit;

    /** Индивидуальные настройки классов по количеству объектов, отображаемых редактором классов. */
    private Map<Long, Integer> objectBrowserLimitForClasses = new HashMap<Long, Integer>();

    /** * Индивидуальная история просмотров процессов для пользователя. */
    private LinkedList<KrnObject> historySrv = new LinkedList<KrnObject>();
    /** * Индивидуальная история просмотров интерфейсов для пользователя. */
    private LinkedList<KrnObject> historyIfc = new LinkedList<KrnObject>();
    /** * Индивидуальная история просмотров фильтров для пользователя. */
    private LinkedList<KrnObject> historyFlt = new LinkedList<KrnObject>();
    /** * Индивидуальная история просмотров отчётов для пользователя. */
    private LinkedList<KrnObject> historyRpt = new LinkedList<KrnObject>();
    
    private LinkedList<HistoryWithDate> srvHistory = new LinkedList<HistoryWithDate>();
    
    public LinkedList<HistoryWithDate> getSrvHistory(){
		return srvHistory;
	}
    
    public LinkedList<KrnObject> getSrvHistoryObjs(){
    	return getHistoryKrnObjs(srvHistory);
    }

    private LinkedList<HistoryWithDate> ifcHistory = new LinkedList<HistoryWithDate>();

    public LinkedList<HistoryWithDate> getIfcHistory(){
    	return ifcHistory;
    }

    public LinkedList<KrnObject> getIfcHistoryObjs(){
    	return getHistoryKrnObjs(ifcHistory);
    }
    
    private LinkedList<HistoryWithDate> fltHistory = new LinkedList<HistoryWithDate>();

    public LinkedList<HistoryWithDate> getFltHistory(){
    	return fltHistory;
    }

    public LinkedList<KrnObject> getFltHistoryObjs(){
    	return getHistoryKrnObjs(fltHistory);
    }
    
    private LinkedList<HistoryWithDate> rptHistory = new LinkedList<HistoryWithDate>();

    public LinkedList<HistoryWithDate> getRptHistory(){
    	return rptHistory;
    }

    public LinkedList<KrnObject> getRptHistoryObjs(){
    	return getHistoryKrnObjs(rptHistory);
    }

    
    private Map<KrnObject,String> namesSrv = new HashMap<KrnObject,String>();
    private Map<KrnObject,String> namesIfc = new HashMap<KrnObject,String>();
    private Map<KrnObject,String> namesFlt = new HashMap<KrnObject,String>();
    private Map<KrnObject,String> namesRpt = new HashMap<KrnObject,String>();
    
    /** Разрешено лимитированное отображение объектов классов. */
    private boolean isObjectBrowserLimit;

    /** Разрешены индивидуальные настройки лимитированного отображения объектов классов. */
    private boolean isObjectBrowserLimitForClasses;

    /** The is monitor. */
    private int isMonitor;

    private int isToolBar;

    /**
     * Конструктор класса.
     */
    public Config() {
    }

    /**
     * Получить gradient main frame.
     * 
     * @return the gradientMainFrame
     */
    public GradientColor getGradientMainFrame() {
        return gradientMainFrame;
    }

    /**
     * Установить gradient main frame.
     * 
     * @param gradientMainFrame
     *            the gradientMainFrame to set
     */
    public void setGradientMainFrame(GradientColor gradientMainFrame) {
        this.gradientMainFrame = gradientMainFrame;
    }

    /**
     * Получить gradient control panel.
     * 
     * @return the gradientControlPanel
     */
    public GradientColor getGradientControlPanel() {
        return gradientControlPanel;
    }

    /**
     * Установить gradient control panel.
     * 
     * @param gradientControlPanel
     *            the gradientControlPanel to set
     */
    public void setGradientControlPanel(GradientColor gradientControlPanel) {
        this.gradientControlPanel = gradientControlPanel;
    }

    /**
     * Получить gradient menu panel.
     * 
     * @return the gradientMenuPanel
     */
    public GradientColor getGradientMenuPanel() {
        return gradientMenuPanel;
    }

    /**
     * Установить gradient menu panel.
     * 
     * @param gradientMenuPanel
     *            the gradientMenuPanel to set
     */
    public void setGradientMenuPanel(GradientColor gradientMenuPanel) {
        this.gradientMenuPanel = gradientMenuPanel;
    }

    /**
     * Проверяет, является ли transparent main.
     * 
     * @return the transparentMain
     */
    public boolean isTransparentMain() {
        return transparentMain;
    }

    /**
     * Установить transparent main.
     * 
     * @param transparentMain
     *            the transparentMain to set
     */
    public void setTransparentMain(boolean transparentMain) {
        this.transparentMain = transparentMain;
    }

    /**
     * Проверяет, является ли transparent dialog.
     * 
     * @return the transparentDialog
     */
    public boolean isTransparentDialog() {
        return transparentDialog;
    }

    /**
     * Установить transparent dialog.
     * 
     * @param transparentDialog
     *            the transparentDialog to set
     */
    public void setTransparentDialog(boolean transparentDialog) {
        this.transparentDialog = transparentDialog;
    }

    /**
     * Получить color main.
     * 
     * @return the colorMain
     */
    public ColorAct getColorMain() {
        return colorMain;
    }

    /**
     * Установить color main.
     * 
     * @param colorMain
     *            the colorMain to set
     */
    public void setColorMain(ColorAct colorMain) {
        this.colorMain = colorMain;
    }

    /**
     * Получить transparent cell table.
     * 
     * @return the transparentCellTable
     */
    public int getTransparentCellTable() {
        return transparentCellTable;
    }

    /**
     * Установить transparent cell table.
     * 
     * @param transparentCellTable
     *            the transparentCellTable to set
     */
    public void setTransparentCellTable(int transparentCellTable) {
        this.transparentCellTable = transparentCellTable;
    }

    /**
     * Получить color header table.
     * 
     * @return the colorHeaderTable
     */
    public Color getColorHeaderTable() {
        return colorHeaderTable;
    }

    /**
     * Установить color header table.
     * 
     * @param colorHeaderTable
     *            the colorHeaderTable to set
     */
    public void setColorHeaderTable(Color colorHeaderTable) {
        this.colorHeaderTable = colorHeaderTable;
    }

    /**
     * Получить color tab title.
     * 
     * @return the colorTabTitle
     */
    public Color getColorTabTitle() {
        return colorTabTitle;
    }

    /**
     * Установить color tab title.
     * 
     * @param colorTabTitle
     *            the colorTabTitle to set
     */
    public void setColorTabTitle(Color colorTabTitle) {
        this.colorTabTitle = colorTabTitle;
    }
    
    private LinkedList<KrnObject> getHistoryKrnObjs(LinkedList<HistoryWithDate> hists){
		LinkedList<KrnObject> objs = new LinkedList<KrnObject>();
    	if(hists != null && hists.size()>0) {
    		for(HistoryWithDate hist: hists) {
    			objs.add(hist.getObj());
    		}
    	}
    	return objs;
    }
    

    /**
     * Получить color back tab title.
     * 
     * @return the colorBackTabTitle
     */
    public Color getColorBackTabTitle() {
        return colorBackTabTitle;
    }

    /**
     * Установить color back tab title.
     * 
     * @param colorBackTabTitle
     *            the colorBackTabTitle to set
     */
    public void setColorBackTabTitle(Color colorBackTabTitle) {
        this.colorBackTabTitle = colorBackTabTitle;
    }

    /**
     * Получить color font tab title.
     * 
     * @return the colorFontTabTitle
     */
    public Color getColorFontTabTitle() {
        return colorFontTabTitle;
    }

    /**
     * Установить color font tab title.
     * 
     * @param colorFontTabTitle
     *            the colorFontTabTitle to set
     */
    public void setColorFontTabTitle(Color colorFontTabTitle) {
        this.colorFontTabTitle = colorFontTabTitle;
    }

    /**
     * Получить color font back tab title.
     * 
     * @return the colorFontBackTabTitle
     */
    public Color getColorFontBackTabTitle() {
        return colorFontBackTabTitle;
    }

    /**
     * Установить color font back tab title.
     * 
     * @param colorFontBackTabTitle
     *            the colorFontBackTabTitle to set
     */
    public void setColorFontBackTabTitle(Color colorFontBackTabTitle) {
        this.colorFontBackTabTitle = colorFontBackTabTitle;
    }

    /**
     * Получить transparent back tab title.
     * 
     * @return the transparentBackTabTitle
     */
    public int getTransparentBackTabTitle() {
        return transparentBackTabTitle;
    }

    /**
     * Установить transparent back tab title.
     * 
     * @param transparentBackTabTitle
     *            the transparentBackTabTitle to set
     */
    public void setTransparentBackTabTitle(int transparentBackTabTitle) {
        this.transparentBackTabTitle = transparentBackTabTitle;
    }

    /**
     * Получить transparent selected tab title.
     * 
     * @return the transparentSelectedTabTitle
     */
    public int getTransparentSelectedTabTitle() {
        return transparentSelectedTabTitle;
    }

    /**
     * Установить transparent selected tab title.
     * 
     * @param transparentSelectedTabTitle
     *            the transparentSelectedTabTitle to set
     */
    public void setTransparentSelectedTabTitle(int transparentSelectedTabTitle) {
        this.transparentSelectedTabTitle = transparentSelectedTabTitle;
    }

    /**
     * Получить gradient field noflc.
     * 
     * @return the gradientFieldNOFLC
     */
    public GradientColor getGradientFieldNOFLC() {
        return gradientFieldNOFLC;
    }

    /**
     * Установить gradient field noflc.
     * 
     * @param gradientFieldNOFLC
     *            the gradientFieldNOFLC to set
     */
    public void setGradientFieldNOFLC(GradientColor gradientFieldNOFLC) {
        this.gradientFieldNOFLC = gradientFieldNOFLC;
    }

    /**
     * Получить checks if is monitor.
     * 
     * @return the isMonitor
     */
    public int getIsMonitor() {
        return isMonitor;
    }

    /**
     * Установить checks if is monitor.
     * 
     * @param isMonitor
     *            the isMonitor to set
     */
    public void setIsMonitor(int isMonitor) {
        this.isMonitor = isMonitor;
    }

    /**
     * Порлучить флаг отображения панели задач в WEB.
     * 
     * @return Флаг отображения панели задач в WEB.
     */
    public int getIsToolBar() {
        return isToolBar;
    }

    /**
     * Установить флаг отображения панели задач в WEB.
     * 
     * @param isToolBar
     *            the new checks if is tool bar
     */
    public void setIsToolBar(int isToolBar) {
        this.isToolBar = isToolBar;
    }

    /**
     * Получить blue sys color.
     * 
     * @return the blueSysColor
     */
    public ColorAct getBlueSysColor() {
        return blueSysColor;
    }

    /**
     * Установить blue sys color.
     * 
     * @param blueSysColor
     *            the blueSysColor to set
     */
    public void setBlueSysColor(ColorAct blueSysColor) {
        this.blueSysColor = blueSysColor;
    }

    /**
     * Получить dark shadow sys color.
     * 
     * @return the darkShadowSysColor
     */
    public ColorAct getDarkShadowSysColor() {
        return darkShadowSysColor;
    }

    /**
     * Установить dark shadow sys color.
     * 
     * @param darkShadowSysColor
     *            the darkShadowSysColor to set
     */
    public void setDarkShadowSysColor(ColorAct darkShadowSysColor) {
        this.darkShadowSysColor = darkShadowSysColor;
    }

    /**
     * Получить mid sys color.
     * 
     * @return the midSysColor
     */
    public ColorAct getMidSysColor() {
        return midSysColor;
    }

    /**
     * Установить mid sys color.
     * 
     * @param midSysColor
     *            the midSysColor to set
     */
    public void setMidSysColor(ColorAct midSysColor) {
        this.midSysColor = midSysColor;
    }

    /**
     * Получить light yellow color.
     * 
     * @return the lightYellowColor
     */
    public ColorAct getLightYellowColor() {
        return lightYellowColor;
    }

    /**
     * Установить light yellow color.
     * 
     * @param lightYellowColor
     *            the lightYellowColor to set
     */
    public void setLightYellowColor(ColorAct lightYellowColor) {
        this.lightYellowColor = lightYellowColor;
    }

    /**
     * Получить red color.
     * 
     * @return the redColor
     */
    public ColorAct getRedColor() {
        return redColor;
    }

    /**
     * Установить red color.
     * 
     * @param redColor
     *            the redColor to set
     */
    public void setRedColor(ColorAct redColor) {
        this.redColor = redColor;
    }

    /**
     * Получить light red color.
     * 
     * @return the lightRedColor
     */
    public ColorAct getLightRedColor() {
        return lightRedColor;
    }

    /**
     * Установить light red color.
     * 
     * @param lightRedColor
     *            the lightRedColor to set
     */
    public void setLightRedColor(ColorAct lightRedColor) {
        this.lightRedColor = lightRedColor;
    }

    /**
     * Получить light green color.
     * 
     * @return the lightGreenColor
     */
    public ColorAct getLightGreenColor() {
        return lightGreenColor;
    }

    /**
     * Установить light green color.
     * 
     * @param lightGreenColor
     *            the lightGreenColor to set
     */
    public void setLightGreenColor(ColorAct lightGreenColor) {
        this.lightGreenColor = lightGreenColor;
    }

    /**
     * Получить shadow yellow color.
     * 
     * @return the shadowYellowColor
     */
    public ColorAct getShadowYellowColor() {
        return shadowYellowColor;
    }

    /**
     * Установить shadow yellow color.
     * 
     * @param shadowYellowColor
     *            the shadowYellowColor to set
     */
    public void setShadowYellowColor(ColorAct shadowYellowColor) {
        this.shadowYellowColor = shadowYellowColor;
    }

    /**
     * Получить sys color.
     * 
     * @return the sysColor
     */
    public ColorAct getSysColor() {
        return sysColor;
    }

    /**
     * Установить sys color.
     * 
     * @param sysColor
     *            the sysColor to set
     */
    public void setSysColor(ColorAct sysColor) {
        this.sysColor = sysColor;
    }

    /**
     * Получить light sys color.
     * 
     * @return the lightSysColor
     */
    public ColorAct getLightSysColor() {
        return lightSysColor;
    }

    /**
     * Установить light sys color.
     * 
     * @param lightSysColor
     *            the lightSysColor to set
     */
    public void setLightSysColor(ColorAct lightSysColor) {
        this.lightSysColor = lightSysColor;
    }

    /**
     * Получить default font color.
     * 
     * @return the defaultFontColor
     */
    public ColorAct getDefaultFontColor() {
        return defaultFontColor;
    }

    /**
     * Установить default font color.
     * 
     * @param defaultFontColor
     *            the defaultFontColor to set
     */
    public void setDefaultFontColor(ColorAct defaultFontColor) {
        this.defaultFontColor = defaultFontColor;
    }

    /**
     * Получить silver color.
     * 
     * @return the silverColor
     */
    public ColorAct getSilverColor() {
        return silverColor;
    }

    /**
     * Установить silver color.
     * 
     * @param silverColor
     *            the silverColor to set
     */
    public void setSilverColor(ColorAct silverColor) {
        this.silverColor = silverColor;
    }

    /**
     * Получить shadows grey color.
     * 
     * @return the shadowsGreyColor
     */
    public ColorAct getShadowsGreyColor() {
        return shadowsGreyColor;
    }

    /**
     * Установить shadows grey color.
     * 
     * @param shadowsGreyColor
     *            the shadowsGreyColor to set
     */
    public void setShadowsGreyColor(ColorAct shadowsGreyColor) {
        this.shadowsGreyColor = shadowsGreyColor;
    }

    /**
     * Получить keyword color.
     * 
     * @return the keywordColor
     */
    public ColorAct getKeywordColor() {
        return keywordColor;
    }

    /**
     * Установить keyword color.
     * 
     * @param keywordColor
     *            the keywordColor to set
     */
    public void setKeywordColor(ColorAct keywordColor) {
        this.keywordColor = keywordColor;
    }

    /**
     * Получить variable color.
     * 
     * @return the variableColor
     */
    public ColorAct getVariableColor() {
        return variableColor;
    }

    /**
     * Установить variable color.
     * 
     * @param variableColor
     *            the variableColor to set
     */
    public void setVariableColor(ColorAct variableColor) {
        this.variableColor = variableColor;
    }

    /**
     * Получить client variable color.
     * 
     * @return the clientVariableColor
     */
    public ColorAct getClientVariableColor() {
        return clientVariableColor;
    }

    /**
     * Установить client variable color.
     * 
     * @param clientVariableColor
     *            the clientVariableColor to set
     */
    public void setClientVariableColor(ColorAct clientVariableColor) {
        this.clientVariableColor = clientVariableColor;
    }

    /**
     * Получить comment color.
     * 
     * @return the commentColor
     */
    public ColorAct getCommentColor() {
        return commentColor;
    }

    /**
     * Установить comment color.
     * 
     * @param commentColor
     *            the commentColor to set
     */
    public void setCommentColor(ColorAct commentColor) {
        this.commentColor = commentColor;
    }

    /**
     * Получить object browser limit.
     * 
     * @return the objectBrowserLimit
     */
    public int getObjectBrowserLimit() {
        return objectBrowserLimit;
    }

    /**
     * Установить object browser limit.
     * 
     * @param limit
     *            the new object browser limit
     */
    public void setObjectBrowserLimit(int limit) {
        this.objectBrowserLimit = limit;
    }

    /**
     * Получить object browser limit for classes.
     * 
     * @return the objectBrowserLimitForClasses
     */
    public Map<Long, Integer> getObjectBrowserLimitForClasses() {
        return objectBrowserLimitForClasses;
    }

    /**
     * Clean object browser limit for classes.
     * 
     */
    public void cleanObjectBrowserLimitForClasses() {
        objectBrowserLimitForClasses = new HashMap<Long, Integer>();
    }

    /**
     * Проверяет, является ли object browser limit.
     * 
     * @return the isObjectBrowserLimit
     */
    public boolean isObjectBrowserLimit() {
        return isObjectBrowserLimit;
    }

    /**
     * Установить object browser limit.
     * 
     * @param limit
     *            the new object browser limit
     */
    public void setObjectBrowserLimit(boolean limit) {
        isObjectBrowserLimit = limit;
    }

    /**
     * Проверяет, является ли object browser limit for classes.
     * 
     * @return the isObjectBrowserLimitForClasses
     */
    public boolean isObjectBrowserLimitForClasses() {
        return isObjectBrowserLimitForClasses;
    }

    /**
     * Установить object browser limit for classes.
     * 
     * @param limit
     *            the new object browser limit for classes
     */
    public void setObjectBrowserLimitForClasses(boolean limit) {
        isObjectBrowserLimitForClasses = limit;
    }

    /**
     * @return the historySrv
     */
    public LinkedList<KrnObject> getHistorySrv() {
        return historySrv;
    }
    
   
    /**
     * @return the historyIfc
     */
    public LinkedList<KrnObject> getHistoryIfc() {
        return historyIfc;
    }

    /**
     * @return the historyFlt
     */
    public LinkedList<KrnObject> getHistoryFlt() {
        return historyFlt;
    }

    /**
     * @return the historyRpt
     */
    public LinkedList<KrnObject> getHistoryRpt() {
        return historyRpt;
    }
    
    public void removeSrvInHistory(KrnObject obj) {
    	if(getHistoryKrnObjs(srvHistory).contains(obj)) {
        	int idx = getHistoryKrnObjs(srvHistory).indexOf(obj);
        	srvHistory.remove(idx);
        } 
    }

    public void addSrvInHistory(HistoryWithDate hwd, String name) {
    	KrnObject obj = hwd.getObj();
        addInHistory(hwd, srvHistory);
        namesSrv.put(obj, name);
    }
    
    public void removeIfcInHistory(KrnObject obj) {
    	if(getHistoryKrnObjs(ifcHistory).contains(obj)) {
        	int idx = getHistoryKrnObjs(ifcHistory).indexOf(obj);
        	ifcHistory.remove(idx);
        } 
    }
    

    public void addIfcInHistory(HistoryWithDate hwd, String name) {
    	KrnObject obj = hwd.getObj();
        addInHistory(hwd, ifcHistory);
        namesIfc.put(obj, name);
    }
    
    public void removeFltInHistory(KrnObject obj) {
    	if(getHistoryKrnObjs(fltHistory).contains(obj)) {
        	int idx = getHistoryKrnObjs(fltHistory).indexOf(obj);
        	fltHistory.remove(idx);
        } 
    }

    public void addFltInHistory(HistoryWithDate hwd, String name) {
    	KrnObject obj = hwd.getObj();
        addInHistory(hwd, fltHistory);
        namesFlt.put(obj, name);
    }

    public void removeRptInHistory(KrnObject obj) {
    	if(getHistoryKrnObjs(rptHistory).contains(obj)) {
        	int idx = getHistoryKrnObjs(rptHistory).indexOf(obj);
        	rptHistory.remove(idx);
        } 
    }
    
    public void addRptInHistory(HistoryWithDate hwd, String name) {
    	KrnObject obj = hwd.getObj();
        addInHistory(hwd, rptHistory);
        namesRpt.put(obj, name);
    }
    
    public String getSrvName(KrnObject obj) {
        return namesSrv.get(obj);
    }

    public String getIfcName(KrnObject obj) {
        return namesIfc.get(obj);
    }

    public String getFltName(KrnObject obj) {
        return namesFlt.get(obj);
    }

    public String getRptName(KrnObject obj) {
        return namesRpt.get(obj);
    }
    
    public void addInHistory(HistoryWithDate hwd, LinkedList<HistoryWithDate> history) {
    	KrnObject obj = hwd.getObj();
    	if(getHistoryKrnObjs(history).contains(obj)) {
        	int idx = getHistoryKrnObjs(history).indexOf(obj);
        	history.remove(idx);
        	HistoryWithDate newHwd = new HistoryWithDate(obj, new Date());
        	history.addFirst(newHwd);
        } else {
        	if(history.size() == maxLengthHistory) {        	
        		history.removeLast();
        	}        	
        	history.addFirst(hwd);
        }
    }

    public void addInHistory(KrnObject obj, LinkedList<KrnObject> history) {
    	if(history.contains(obj)) {
    		int idx = history.indexOf(obj);
    		history.remove(idx);
    		history.addFirst(obj);
    	} else {
    		if (history.size() == maxLengthHistory) {
    			history.remove(maxLengthHistory-1);
    		}
    		history.addFirst(obj);
    	}
    }
}
