package kz.tamur.rt.orlang;

import static kz.tamur.rt.InterfaceManager.CommitResult.CONTINUE_EDIT;
import static kz.tamur.rt.InterfaceManager.CommitResult.WITHOUT_ERRORS;
import static kz.tamur.rt.InterfaceManager.CommitResult.WITH_ERRORS;

import static kz.tamur.comps.Constants.ACT_AUTO_STRING;	
import static kz.tamur.comps.Constants.ACT_DIALOG_STRING;
import static kz.tamur.comps.Constants.ACT_ERR;
import kz.tamur.comps.Constants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import kz.tamur.SecurityContextHolder;
import kz.tamur.comps.Mode;
import kz.tamur.comps.OrFrame;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.OrGuiContainer;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.or3.client.comps.interfaces.OrPanelComponent;
import kz.tamur.or3.util.PathElement2;
import kz.tamur.rt.CheckContext;
import kz.tamur.rt.InterfaceManager;
import kz.tamur.rt.InterfaceManager.CommitResult;
import kz.tamur.rt.adapters.ComponentAdapter;
import kz.tamur.rt.adapters.OrCalcRef;
import kz.tamur.rt.adapters.OrRef;
import kz.tamur.rt.adapters.OrRef.Item;
import kz.tamur.rt.adapters.Util;
import kz.tamur.rt.data.Cache;
import kz.tamur.util.CacheChangeRecord;
import kz.tamur.util.Funcs;
import kz.tamur.util.ReqMsgsList;
import kz.tamur.web.common.Base64;
import kz.tamur.web.common.TemplateHelper;
import kz.tamur.web.common.WebSession;
import kz.tamur.web.common.webgui.WebComponent;
import kz.tamur.web.common.webgui.WebPanel;
import kz.tamur.web.component.OrWebPanel;
import kz.tamur.web.component.OrWebRadioBox;
import kz.tamur.web.component.OrWebTabbedPane;
import kz.tamur.web.component.OrWebTable;
import kz.tamur.web.component.OrWebTreeTable2;
import kz.tamur.web.component.WebFrame;
import kz.tamur.web.component.WebFrameManager;
import kz.tamur.web.controller.WebController;
import kz.tamur.web.controller.WebUITemplateController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.cifs.or2.client.Utils;
import com.cifs.or2.kernel.Activity;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnDate;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.ProcessException;
import com.eclipsesource.json.JsonObject;

/**
 * Created by IntelliJ IDEA.
 * 
 * Date: 10.01.2005
 * Time: 15:44:25
 * 
 * @author berik
 */
public class InterfaceWrp {

    /** ctx. */
    private CheckContext ctx;

    /** frame. */
    private OrFrame frame;

    /** before open. */
    private boolean beforeOpen;

    /** obj map. */
    private Map<String, KrnObject[]> objMap;
    
    /** log */
    private Log log;

    /**
     * Конструктор класса InterfaceWrp.
     * 
     * @param frame
     *            the frame
     * @param beforeOpen
     *            the before open
     */
    public InterfaceWrp(OrFrame frame, boolean beforeOpen) {
        super();
        this.frame = frame;
        if (frame instanceof WebFrame)
        	this.log = ((WebFrame)frame).getLog(InterfaceWrp.class);
        else
        	this.log = LogFactory.getLog(InterfaceWrp.class);

        this.beforeOpen = beforeOpen;
    }

    /**
     * Получить context.
     * 
     * @return context
     */
    public CheckContext getContext() {
        return ctx;
    }
    
    public int getUserDecision() {
    	return ((WebFrame) frame).getUserDecision();
    }

    /**
     * Установить context.
     * 
     * @param ctx
     *            новое значение context
     */
    public void setContext(CheckContext ctx) {
        this.ctx = ctx;
    }

    /**
     * Возвращает значение атрибута, указанного в пути &lt;Path&gt;.
     * В случае, если путь указывает на множественный атрибут, метод
     * возвращает последнее значение множества атрибутов.
     * Метод может вернуть все значения множества атрибутов в виде массива,
     * если к пути добавить символы квадратных скобок []:
     * 
     * <pre>
     * getAttr(&quot;&lt;Path&gt;[]&quot;)
     * </pre>
     * 
     * Пример:
     * 
     * <pre>
     * $Interface.getAttr(&quot;&lt;Path&gt;&quot;)
     * </pre>
     * 
     * Пример получения массива:
     * 
     * <pre>
     * #set($my_array = $Interface.getAttr("&lt;Path&gt;[]")
     * </pre>
     * 
     * @param path
     *            путь к атрибуту.
     * @return атрибут.
     */
    public Object getAttr(String path) {
        return getAttr(path, ctx.getLangId());
    }

    /**
     * Возвращает значение атрибута, указанного в пути &lt;Path&gt; для указанного языка.
     * Язык передаётся объектом в качестве второго параметра &lt;lang&gt;.
     * В случае, если путь указывает на множественный атрибут, метод
     * возвращает последнее значение множества атрибутов.
     * Метод может вернуть все значения множества атрибутов в виде массива,
     * если к пути добавить символы квадратных скобок []:
     * 
     * <pre>
     * getAttr(&quot;&lt;Path&gt;[]&quot;, $obj)
     * </pre>
     * 
     * Пример:
     * 
     * <pre>
     * $Interface.getAttr(&quot;&lt;Path&gt;&quot;, $obj)
     * </pre>
     * 
     * Пример получения массива:
     * 
     * <pre>
     * #set( $my_array = $Interface.getAttr("&lt;Path&gt;[]", $obj)
     * </pre>
     * 
     * @param path
     *            путь к атрибуту.
     * @param lang
     *            язык, передающийся в виде объекта БД.
     * @return атрибут.
     */
    public Object getAttr(String path, KrnObject lang) {
        return getAttr(path, lang.getId());
    }

    /**
     * Возвращает значение атрибута, указанного в пути &lt;Path&gt; для указанного языка.
     * Язык передаётся в виде id объекта в качестве второго параметра &lt;langId&gt;.
     * В случае, если путь указывает на множественный атрибут, метод
     * возвращает последнее значение множества атрибутов.
     * Метод может вернуть все значения множества атрибутов в виде массива,
     * если к пути добавить символы квадратных скобок []:
     * 
     * <pre>
     * getAttr(&quot;&lt;Path&gt;[]&quot;, $langId)
     * </pre>
     * 
     * Пример:
     * 
     * <pre>
     * $Interface.getAttr(&quot;&lt;Path&gt;&quot;, $langId)
     * </pre>
     * 
     * Пример получения массива:
     * 
     * <pre>
     * #set( $my_array = $Interface.getAttr("&lt;Path&gt;[]", $langId)
     * </pre>
     * 
     * @param path
     *            путь к атрибуту.
     * @param langId
     *            the lang id
     * @return атрибут.
     */
    private Object getAttr(String path, long langId) {
        Map<String, OrRef> refs = frame.getRefs();
        OrRef ref = refs.get(Utils.normalizePath2(path));
        if (ref == null)
        	ref = refs.get(Utils.normalizePath(path));
        boolean isArray = false;
        try {
            PathElement2[] ps = Utils.parsePath2(path, frame.getKernel());
            if (ps.length > 0) {
                PathElement2 p = ps[ps.length - 1];
                KrnAttribute attr = p.attr;
                isArray = (attr == null && path.endsWith("[]")) || (attr != null && attr.collectionType > 0 && p.index == null);
            }
            return getValue(ref, isArray, langId);
        } catch (Exception e) {
            SecurityContextHolder.getLog().error("$Interface.getAttr(\"" + path + "\")");
            SecurityContextHolder.getLog().error(e, e);
        }
        return null;
    }

    /**
     * Задаёт текущий активный индекс атрибута.
     * Применяется только для множественных типов атрибутов.
     * 
     * @param path
     *            путь атрибута.
     * @param index
     *            необходимый индекс атрибута.
     * @throws KrnException
     *             the krn exception
     */
    public void setIndex(String path, int index) throws KrnException {
        Map<String, OrRef> refs = frame.getRefs();
        OrRef ref = refs.get(Utils.normalizePath2(path));
        if (ref != null && ref.isArray()) {
            ref.absolute(index, this);
        }
    }

    /**
     * Устанавливает значение заданного атрибута.
     * 
     * @param path
     *            путь атрибута
     * @param value
     *            новое значение атрибута.
     */
    public void setAttr(String path, Object value) {
        Map<String, OrRef> refs = frame.getRefs();
        OrRef ref = refs.get(Utils.normalizePath2(path));
        setValue(ref, value);
    }

    /**
     * Удаляет атрибут объекта.
     * 
     * @param path
     *            путь удаляемого атрибута.
     */
    public void deleteAttr(String path) {
        Map<String, OrRef> refs = frame.getRefs();
        OrRef ref = refs.get(Utils.normalizePath2(path));
        ref.deleteItem(ctx, this);
    }

    /**
     * Удаляет атрибут объекта по указанному в &lt;index&gt; номеру.
     * Применяется только для множественных типов атрибутов.
     * 
     * @param path
     *            путь удаляемого атрибута.
     * @param index
     *            индекс удаляемого атрибута.
     */
    public void deleteAttr(String path, int index) {
        Map<String, OrRef> refs = frame.getRefs();
        OrRef ref = refs.get(Utils.normalizePath2(path));
        try {
            ref.deleteItem(ctx, index, this);
        } catch (KrnException e) {
        	SecurityContextHolder.getLog().error(e, e);
        }
    }

    /**
     * Получить текущий активный индекс атрибута.
     * Применяется только для множественных типов атрибутов.
     * 
     * @param path
     *            путь атрибута.
     * @return index активный индекс атрибута.
     */
    public int getIndex(String path) {
        Map<String, OrRef> refs = frame.getRefs();
        OrRef ref = refs.get(Utils.normalizePath2(path));
        return ref.getIndex();
    }

    /**
     * Получить value.
     * 
     * @param ref
     *            the ref
     * @param isArray
     *            the is array
     * @param langId
     *            the lang id
     * @return value
     */
    private Object getValue(OrRef ref, boolean isArray, long langId) {
        Object res = null;
        if (!ref.hasLanguage(langId)) {
            langId = 0;
        }

        if (isArray) {
            List<Item> items = ref.getItems(langId);
            List<Object> v = new ArrayList<>(items.size());
            for (int i = 0; i < items.size(); i++) {
                OrRef.Item item = (OrRef.Item) items.get(i);
                v.add(item.getCurrent());
            }
            return v;
        }

        OrRef.Item item = ref.getItem(langId);

        if (item != null && item.getCurrent() != null) {
            res = item.getCurrent();
            if (res instanceof KrnDate) {
                res = ((KrnDate) res).clone();
            }
        }

        return res;
    }

    /**
     * Sets the value.
     * 
     * @param ref
     *            the ref
     * @param value
     *            the value
     */
    private void setValue(OrRef ref, Object value) {
        try {
            if (ref.getAttribute() == null) {
                Collection<KrnObject> col = (Collection<KrnObject>) value;
                KrnObject[] objs = col != null ? col.toArray(new KrnObject[col.size()]) : null;
                if (beforeOpen) {
                    if (objMap == null)
                        objMap = new HashMap<String, KrnObject[]>();
                    objMap.put(ref.toString(), objs);
                } else {
                    ref.evaluate(objs, this);
                }
            } else if (ref.getAttribute().collectionType > 0 || ref.getItem(ctx.getLangId()) == null) {
                ref.insertItem(-1, value, null, ctx, false);
            } else {
                ref.changeItem(value, ctx, null);
            }
        } catch (KrnException e) {
        	SecurityContextHolder.getLog().error(e, e);
        }
    }

    /**
     * Показать сообщение.
     * 
     * @param str
     *            текст сообщения.
     * @param type
     *            <code>1</code> - информационное окно, все остальные значения выводят окно типа "Ошибка"
     */
    public void showMsg(String str, int type) {
        if (frame != null) {
            WebPanel panel = (WebPanel) frame.getPanel();
            int msg_type = 0;
            if (type == 1) {
                msg_type = 4;
            }
            panel.setAlertMessage(str, false);
        }
    }
    
    public int reloadTable(String name) {
    	int status = 0;
    	OrGuiComponent component = getComponent(name);
    	if (component != null && component instanceof OrWebTable) {
    		ComponentAdapter adapter = ((OrWebTable) component).getAdapter();
    		((kz.tamur.rt.adapters.TableAdapter) adapter).sort();
            ((OrWebTable) component).putJSON(true);
            status = 1;
    	}
    	return status;
    }
    
    public int setActiveTab(String name, int index) {
    	int status = 0;
    	OrGuiComponent component = getComponent(name);
    	if (component != null && component instanceof OrWebTabbedPane) {
//            ((OrWebTabbedPane) component).selectedIndex(index);
//            ((OrWebTabbedPane) component).putJSON(true);
            ((WebFrame) frame).getSession().sendCommand("setSelectedTab", component.getUUID() + "," + index);
            status = 1;
    	}
    	return status;
    }
    
    public void setMultiSelection(OrWebTreeTable2 table, boolean multi) {
    	if (table != null) {
    		((WebFrame) frame).getSession().sendCommand("setMultiSelection", table.getUUID() + ',' + multi);
    	}
    }
    
    public int setActiveRadio(String name, int index) {
    	int status = 0;
    	OrGuiComponent component = getComponent(name);
    	if (component != null && component instanceof OrWebRadioBox) {
            ((OrWebRadioBox) component).selectIndex(index);
            ((OrWebRadioBox) component).putJSON(true);
            status = 1;
    	}
    	return status;
    }
    
    public void showWaiting(String message) {
        ((WebFrame) frame).getSession().sendCommand("showWaiting", (message != null && message.length() > 0) ? message : "Пожалуйста, подождите...");
    }
    
    public void closeWaiting() {
        ((WebFrame) frame).getSession().sendCommand("closeWaiting", "1");
    }

    /**
     * Определяет текущий язык интерфейса.
     * Пример:
     * 
     * <pre>
     * #set($lang = $Interface.getInterfaceLang())
     * </pre>
     * 
     * @return Язык интерфейса, <code>KrnObject</code>
     */
    public KrnObject getInterfaceLang() {
        kz.tamur.rt.InterfaceManager mgr = frame.getInterfaceManager();
        return mgr.getInterfaceLang();
    }

    /**
     * Определяет текущий язык данных.
     * Пример:
     * 
     * <pre>
     * #set($lang = $Interface.getDataLang())
     * </pre>
     * 
     * @return Язык данных, <code>KrnObject</code>
     */
    public KrnObject getDataLang() {
        kz.tamur.rt.InterfaceManager mgr = frame.getInterfaceManager();
        return mgr.getDataLang();
    }

    /**
     * Возвращает значение переменной с именем <code>name</code>.
     * 
     * @param name
     *            имя переменной.
     * @return var значение переменной.
     */
    public Object getVar(String name) {
        return frame.getCash().getVar(name);
    }

    /**
     * Создаёт переменную с указанным именем и значением.
     * Если переменная уже созданно, значение переписывается.
     * Пример:
     * 
     * <pre>
     * $Interface.setVar(&quot;ru_glob&quot;, $Objects.getObject(&quot;0.1&quot;))
     * </pre>
     * 
     * @param name
     *            имя переменной.
     * @param var
     *            значение переменной.
     */
    public void setVar(String name, Object var) {
        frame.getCash().setVar(name, var);
    }

    /**
     * Получает список выбранных объектов атрибута.
     * 
     * @param path
     *            путь к атрибуту
     * @return selection выбранные значения
     */
    public List<Object> getSelection(String path) {
        OrRef ref = frame.getRefs().get(Utils.normalizePath2(path));
        List<OrRef.Item> items = ref.getSelectedItems();
        List<Object> res = new ArrayList<Object>(items.size());
        for (OrRef.Item item : items) {
            res.add(item.getCurrent());
        }
        return res;
    }

    /**
     * ТОЛЬКО WEB
     * Проверка текущего интерфейса на наличие ошибок.
     * 
     * @deprecated использовать <code>verification()</code>
     * 
     * @return <code>0</code> если ошибок нет и <code>1</code> если есть.
     */
    public int checkErrors() {
        try {
            if (frame.getRef() != null && frame.getRef().getType() != null
                    && (frame.getEvaluationMode() & InterfaceManager.READONLY_MODE) == 0) {
                ReqMsgsList msg = frame.getRef().canCommit();
                ((WebFrame) frame).setMessageList(msg);
                return msg.getListSize() > 0 ? 0 : 1;
            }
        } catch (KrnException ex) {
        	SecurityContextHolder.getLog().error(ex, ex);
        }
        return 1;
    }

    /**
     * ТОЛЬКО WEB
     * Получить результат формулы из свойства "В XML вид",
     *  главной панели интерфейса.
     * 
     * @return результат выполнения формулы.
     */
    public String getStringToSign() {
        String ret = "";
        OrPanelComponent p = ((WebFrame) frame).getPanel();
        ASTStart template = p.getCreateXmlTemplate();
        if (template != null) {
            ClientOrLang orlang = new ClientOrLang(frame);
            Map<String, Object> vc = new HashMap<>();
            boolean calcOwner = OrCalcRef.setCalculations();
            try {
                orlang.evaluate(template, vc, p.getAdapter(), new Stack<String>());
            } catch (Exception ex) {
                Util.showErrorMessage(p, ex.getMessage(), "Формирование XML");
            	log.error("Ошибка при выполнении формулы 'Формирование XML' компонента '" + (p != null ? p.getClass().getName() : "") + "', uuid: " + p.getUUID());
                log.error(ex, ex);
            } finally {
    			if (calcOwner)
    				OrCalcRef.makeCalculations();
            }
            Object res = vc.get("RETURN");
            if (res instanceof Element) {
                try {
                    Format ft = Format.getRawFormat();
                    ft.setEncoding("UTF-8");
                    XMLOutputter f = new XMLOutputter(ft);
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    f.output(new Document((Element) res), os);
                    os.close();
                    ret = new String(os.toByteArray(), "UTF-8");
                } catch (Exception e) {
                	SecurityContextHolder.getLog().error(e, e);
                }
            } else if (res instanceof String) {
                ret = (String) res;
            }
        }
        return ret;
    }

    /**
     * 
     * ТОЛЬКО WEB
     * Дабавить дополнительную xml для ответа.
     * 
     * @deprecated в новом WEB (json) не используется
     * 
     * @param xml
     *            the xml
     */
    public void addAdditionalResponseXml(String xml) {
        ((WebFrame) frame).addAdditionalResponseXml(xml);
    }

    /**
     * ТОЛЬКО WEB
     * Получить профиль сессии пользователя.
     * 
     * @return профиль сессии.
     */
    public String getProfile() {
        return ((WebFrame) frame).getSession().getProfile();
    }

    /**
     * Получить сертификат пользователя.
     * Применимо для тех пользователей, что залогинились через ЭЦП
     * 
     * @return cert сертификат
     */
    public String getCert() {
        return ((WebFrame) frame).getSession().getCert();
    }

    /**
     * Получить компонент с интерфейса по его свойству<code>Имя переменной</code>.
     * 
     * @param name
     *            имя переменной компонента.
     * @return component найденный компонент, или <code>null</code> если не найден.
     */
    public OrGuiComponent getComponent(String name) {
        return ((OrGuiContainer) frame.getPanel()).getComponent(name);
    }

    /**
     * Получить значение атрибута типа <code>BLOB</code>.
     * 
     * @param path
     *            путь атрибута.
     * @return значение атрибута.
     * @throws Exception
     *             the exception
     */
    public String getBlobAttr(String path) throws Exception {
        return getBlobAttr(path, null);
    }

    /**
     * Получить значение атрибута типа <code>BLOB</code>.
     * 
     * @param path
     *            путь атрибута.
     * @param lang
     *            язык, на котором требуется значение атрибута.
     * @return значение атрибута.
     * @throws Exception
     *             the exception
     */
    public String getBlobAttr(String path, KrnObject lang) throws Exception {
        try {
            Object o = getAttr(path, lang);
            if (o instanceof File) {
                FileInputStream fis = new FileInputStream((File) o);
                byte[] bs = new byte[(int) ((File) o).length()];
                fis.read(bs);
                fis.close();
                if (bs.length > 0) {
                    return new String(bs, "UTF-8");
                }
            }
        } catch (Exception e) {
        	SecurityContextHolder.getLog().info("<OBJECT>.getAttr(\"" + path + "\")");
        	SecurityContextHolder.getLog().error(e, e);
        }
        return null;
    }

    /**
     * Задать значение атрибута типа <code>BLOB</code>.
     * 
     * @param path
     *            путь атрибута.
     * @param value
     *            новое значение атрибута.
     */
    public void setBlobAttr(String path, Object value) {
        Object v = null;
        try {
            if (value instanceof String) {
                v = ((String) value).getBytes("UTF-8");
            } else {
                v = value;
            }
        } catch (Exception e) {
        	SecurityContextHolder.getLog().error(e, e);
        }
        setAttr(path, v);
    }

    /**
     * Показать диалог выбора объектов.
     * 
     * @param ui
     *            интерфейс
     * @param shareCache
     *            общий кэш?
     * @param objs
     *            содержимое интерфейса
     * @param selectionPath
     *            атрибут для выбранных значений
     * @return list выбранные значения
     * @throws Exception
     *             the exception
     */
    public List<KrnObject> showDialog(KrnObject ui, boolean shareCache, List<KrnObject> objs, String selectionPath)
            throws Exception {
        // Формируем массив объектов - содержимое интерфейса
        KrnObject[] objArray = null;
        if (objs != null) {
            objArray = new KrnObject[objs.size()];
            for (int i = 0; i < objs.size(); i++) {
                KrnObject objWrp = objs.get(i);
                if (objWrp != null)
                    objArray[i] = objWrp.getKrnObject();
            }
        }

        kz.tamur.rt.InterfaceManager mgr = frame.getInterfaceManager();
        long tr_id = mgr.getCash().getTransactionId();

        WebFrame frm = null;
        boolean commit = false;

        WebFrame curFrame = ClientOrLang.getFrame();
        try {
            List<OrCalcRef> calcs = OrCalcRef.removeCalculations();
            int dlgRes = -1;
            OrRef selectionRef = null;
            
            try {
	            frm = (WebFrame) mgr.getInterfacePanel(ui.getKrnObject(), objArray, tr_id, frame.getEvaluationMode(), shareCache, false, true);
	
	            if (selectionPath != null && selectionPath.trim().length() > 0) {
	                selectionRef = OrRef.createRef(selectionPath, false, Mode.RUNTIME, frm.getRefs(), OrRef.TR_CLEAR, frm);
	            }
	
	            OrWebPanel panel = (OrWebPanel) frm.getPanel();
	            // panel.setOpaque(isOpaque);
	            String title = panel.getTitle();
	            dlgRes = frm.showDialog(curFrame, title, panel.getWidth(), panel.getHeight(), false);
	            // dlg.setLanguage(frm.getInterfaceLang().id);
	            // dlg.setFirstRow(frm);
	            // Dimension pSize = panel.getPrefSize();
	            // if (pSize == null) {
	            // pSize = kz.tamur.comps.Utils.getMaxWindowSize();
	            // }
	            // dlg.setSize(pSize);
	            // dlg.setLocation(kz.tamur.comps.Utils.getCenterLocationPoint(dlg.getSize()));
	            // dlg.show();
            } finally {
	            if (calcs != null) {
	                OrCalcRef.setCalculations(calcs);
	            }
            }
            if (dlgRes == 0) { // OK
                // Получаем спсок выбранных объектов
                List<OrRef.Item> selectedItems = selectionRef != null ? selectionRef.getSelectedItems() : frm.getRef()
                        .getSelectedItems();
                if (shareCache)
                    frm.getRef().fireValueChangedEvent(-1, this, 0);
                commit = true;
                List<KrnObject> res = new ArrayList<KrnObject>();
                for (OrRef.Item item : selectedItems) {
                    KrnObject obj = (KrnObject) item.getCurrent();
                    if (obj != null)
                        res.add(obj);
                }
                return res;
            }
        } finally {
            if (frm != null) {
                mgr.releaseInterface(commit);
                ClientOrLang.setFrame(curFrame);
            }
        }

        return null;
    }

    /**
     * Откат внесённых изменений.
     */
    public void rollback() {
        ((WebFrame) frame).rollback();
    }

    /**
     * Сохранение внесённых изменений.
     * 
     * @throws Exception
     *             the exception
     */
    public void save() throws Exception {
        OrFrame frame = ClientOrLang.getFrame();
        Cache cache = frame.getCash();
        cache.commit(frame.getFlowId());
        ((WebFrame) frame).getSession().sendCommand("disableCancelBtn", "1");
    }

    /**
     * Получить все объекты заданные через <code>setAttr</code>.
     * 
     * @return карта объектов
     */
    public Map<String, KrnObject[]> getObjMap() {
        return objMap;
    }
    
    public void keyPressed(String command) {
    	if(command.equals("Cancel")) 
    		command = "Escape";
    	((WebFrame) frame).getSession().sendCommand("keyPressed", command);
    }
    
    public void showProcessUI(String data) {
    	if(data != null && data.length() > 0) {
    		JsonObject obj = JsonObject.readFrom(data);
    		obj.add("waitTime", 1000);
    		((WebFrame) frame).getSession().sendCommand("showProcessUI", obj.toString());           
    	}
    }

    public void showProcessUI(String data, long waitTime) {
    	if(data != null && data.length() > 0) {
    		JsonObject obj = JsonObject.readFrom(data);
    		obj.add("waitTime", waitTime);
    		((WebFrame) frame).getSession().sendCommand("showProcessUI", obj.toString());
    	}
    }

    /**
     * Запуск процесса.
     * 
     * <pre>
     * $vars.put("OBJS", $iter)        
     * $Interface.startProcess($proc, $vars)
     * </pre>
     * 
     * @param def
     *            объект процесса, который необходимо запустить.
     * @param vars
     *            переменные для процесса.
     * @return activity экземпляр запущенного процесса.
     * @throws KrnException
     *             the krn exception
     * @throws ProcessException
     *             the process exception
     */
    public Activity startProcess(KrnObject def, Map<String, Object> vars) throws KrnException, ProcessException {
        String[] res = ((WebFrame) frame).getSession().getKernel().startProcess(def.id, vars);
        if (res.length > 0 && !"".equals(res[0])) {
            ((WebPanel) frame.getPanel()).setAlertMessage(res[0], false);
            throw new ProcessException(res[0]);
        } else {
            List<String> param = new ArrayList<String>();
            param.add("autoIfc");
            if (res.length > 3) {
                param.add(res[3]);
            }
            return ((WebFrame) frame).getSession().getTaskHelper().startProcess(res[1], param);
        }
    }  

    public String startProcess(KrnObject def, Map<String, Object> vars, KrnObject kObj) throws KrnException, ProcessException, InterruptedException {
    	JsonObject res = new JsonObject();
    	WebSession session = ((WebFrame) frame).getSession();
    	session.getTaskHelper().clearAutoIfcFlowId(0);
    	Object pr = null;
    	
    	List<Activity> acts = session.getTaskHelper().findProcess(def, kObj, false, false);  
    	if (acts.size() == 1) {
    		Activity act = acts.get(0);
    		if ((act.param & ACT_ERR) != ACT_ERR) {
    			if (act.ui != null && act.ui.id > 0) {
    				res.add("result", "success");
    				res.add("uid", act.flowId);
    				boolean dlg = ACT_DIALOG_STRING.equals(act.uiType) || ACT_AUTO_STRING.equals(act.uiType);
    				res.add("mode", dlg ? "dialog" : "window");
    			} else {
            		if (act.timeActive > 0) {
                    	res.add("message", session.getResource().getString("processActive"));
            		} else if (act.actorId > 0) {
    					String flowUserName = session.getUserNameById(act.actorId, session.getInterfaceLangId());
    					res.add("message", session.getResource().getString("processEngagedByUser").replace("{1}", flowUserName));
    				} else {
    					res.add("message", session.getResource().getString("processEngaged"));
    				}
    				res.add("result", "error");
    			}
    		} else {
    			res.add("result", "error");
    			res.add("message", session.getResource().getString("processStateError"));
    		}
    	} else if (acts.size() > 1) {
    		res.add("result", "error").add("acts", acts.size()).add("message", session.getResource().getString("processManyFound").replace("{1}", String.valueOf(acts.size())));
    	} else {
    		WebFrame frm = session.getFrameManager().getCurrentFrame();
    		if (vars == null) {
    			pr = session.getProcessHelper().createProcess(def.uid, null,frm);
    		} else {
    			pr = session.getProcessHelper().createProcess(def.uid, vars,frm);
    		}
    		if(pr instanceof Activity) {
    			res.add("result", "success");
    			long id = def.id;
    			session.getCommonHelper().setUserNotOpenProcessDef(id);
    			long flowId = ((Activity)pr).flowId;
    			int waitCount = 20;
    			boolean waitReseted = false;
    			
    			Activity act = session.getReadyToOpenActivity(flowId);
    			if (act != null && act.infMsg != null && !"".equals(act.infMsg)) {
    				res.add("infMsg", act.infMsg);
    			}
    			
    			while (act == null || ((act.uiType == null || act.uiType.length() == 0) && (act.param & Constants.ACT_ERR) != Constants.ACT_ERR)) {
    				Thread.sleep(1000);
    				act = session.getReadyToOpenActivity(flowId);
    				if (waitCount == 0) {
    					res.set("result", "success");
    					return res.toString();
    				}

    				if (act == null) {
    					waitCount--;
    				} else if (!waitReseted) {
    					waitReseted = true;
    					waitCount = 20;
    				}

    				if ((act!=null && (act.param & Constants.ACT_AUTO_NEXT) == Constants.ACT_AUTO_NEXT)) {
    					res.set("result", "success");
    					return res.toString();
    				}
    			}

    			if ((act.param & Constants.ACT_ERR) == Constants.ACT_ERR) {
    				res.set("result", "error");
    				res.add("message", "Ошибка при запуске процесса!");
    				return res.toString();
    			}


    			if (vars != null) {
    				waitCount = 20;
    				while (act.objs == null || act.objs.length == 0) {
    					Thread.sleep(1000);
    					act = session.getTaskHelper().getActivityById(act.flowId);
    					if (act == null || waitCount-- == 0) {
    						res.set("result", "error");
    						res.add("message", "Ошибка при запуске процесса!");
    						return res.toString();
    					}
    				}
    			}

    			res.add("uid", act.flowId);
    			String mode = ACT_DIALOG_STRING.equals(act.uiType) || ACT_AUTO_STRING.equals(act.uiType)
    					? "dialog" : Constants.ACT_NO_UI.equals(act.uiType) ? "no" : "window";
    			res.add("mode", mode);
    		}
    	}
    	return res.toString();
    }

    /**
     * Поиск процесса.
     * 
     * <pre>
     * #set($iter = $Interface.getAttr(“уд::осн::Поручение”))
     * #set($proc = $Objects.getObject("30223634.30302249"))  
     * #set($acts = $Interface.findProcess($proc, $iter))
     * </pre>
     * 
     * @param def
     *            объект процесса который необходимо найти.
     * @param obj
     *            объект для которого должен быть запущен процесс.
     * 
     * @return list список запущенных процессов, соответствующих параметрам поиска.
     * @throws KrnException
     *             the krn exception
     */
    public List<Activity> findProcess(KrnObject def, KrnObject obj) throws KrnException {
        return ((WebFrame) frame).getSession().getTaskHelper().findProcess(def, obj, false, false);
    }

    /**
     * Поиск процесса у которого данный пользователь не является инициатором.
     * 
     */
    public List<Long> findForeignProcess(KrnObject def, KrnObject obj) throws KrnException {
        return ((WebFrame) frame).getSession().getTaskHelper().findForeignProcess(def, obj);
    }

    /**
     * Остановка процесса.
     * 
     * @param activity
     *            останавливаеваемый процесс.
     * @throws KrnException
     *             the krn exception
     */
    public boolean stopProcess(Activity activity) throws KrnException {
        return ((WebFrame) frame).getSession().getTaskHelper().stopProcess(activity, false);
    }

    public boolean stopProcess(Activity activity, boolean forceCancel) throws KrnException {
        return ((WebFrame) frame).getSession().getTaskHelper().stopProcess(activity, forceCancel);
    }

    /**
     * Перевод процесса на следующий шаг.
     * 
     * Пример:
     * 
     * <pre>
     * // Находим запущенный процесс
     * #set($acts = $Interface.findProcess($proc, $poruch))
     * #if($acts.size() == 1)
     *  // переход на след шаг
     *  $Interface.executeProcess($acts.get(o))
     * #end
     * </pre>
     * 
     * @param act
     *            обрабатываемый процесс.
     * 
     * @throws KrnException
     *             the krn exception
     */
    public void executeProcess(Activity act) throws KrnException {
        WebFrame mframe = (WebFrame) frame;
        InterfaceManager ifc = mframe.getInterfaceManager();
        CommitResult cr = ifc.beforePrevious(true, false);
        if (cr == WITH_ERRORS || cr == WITHOUT_ERRORS) {
            int res = mframe.getSession().getTaskHelper().next(act, mframe);
            if (res == ButtonsFactory.BUTTON_YES) {
                ifc.afterPrevious(true, true, frame.getKernel().isSE_UI(), cr);
            }
            if (!frame.getKernel().isSE_UI()) {
                mframe.getSession().sendCommand("main_ui", "");
            }
        }
    }

    /**
     * Получение идентификатора потока.
     * 
     */
    public long getFlowId(){
        return frame.getFlowId();
    }

    /**
     * Получение транзакции потока.
     * 
     */
    public long getTransactionId(){
        return frame.getTransactionId();
    }

    /**
     * Проверка текущего фрейма на наличие ошибок.
     * 
     * @return boolean <code>true</code> если верификация пройдена
     * @throws KrnException
     *             the krn exception
     */
    public boolean verification() throws KrnException {
        return verification("hide", "Ok");
    }

    /**
     * Проверка текущего фрейма на наличие ошибок.
     * 
     * @param titleiIgnoreError
     *            заголовок кнопки игнорирующей ошибки.
     * @param titleContinueEdit
     *            заголовок кнопки останавливающей выполнение.
     * @return boolean <code>true</code> если верификация пройдена
     * @throws KrnException
     *             the krn exception
     */
    public boolean verification(String titleiIgnoreError, String titleContinueEdit) throws KrnException {
        WebFrame frame_ = (WebFrame) ((WebFrame) frame).getInterfaceManager().getCurrentFrame();
        CommitResult cr = CONTINUE_EDIT;
        List<OrCalcRef> calcs = OrCalcRef.removeCalculations();
        
        try {
	        if (titleiIgnoreError == null || titleContinueEdit == null) {
	            cr = frame_.commitCurrent();
	        } else {
	            cr = frame_.commitCurrent(new String[] { titleContinueEdit, titleiIgnoreError }, null, true, false);
	        }
        } finally {
	        if (calcs != null) {
	            OrCalcRef.setCalculations(calcs);
	        }
        }
        return cr != CONTINUE_EDIT;
    }

    /**
     * Принудительный перевод процесса на следующий шаг.
     * 
     * Пример:
     * 
     * <pre>
     * // Находим запущенный процесс
     * #set($acts = $Interface.findProcess($proc, $poruch))
     * #if($acts.size() == 1)
     *  // переход на след шаг
     *  $Interface.forceExecuteProcess($acts.get(o))
     * #end
     * </pre>
     * 
     * @param act
     *            обрабатываемый процесс.
     * @throws KrnException
     *             the krn exception
     */
    public int forceExecuteProcess(Activity act) throws KrnException {
        return forceExecuteProcess(act, true);
    }

    public int forceExecuteProcess(Activity act, KrnObject openArh) throws KrnException {
        return forceExecuteProcess(act, true, null, null, openArh);
    }

    /**
     * Принудительный перевод процесса на следующий шаг.
     * 
     * Пример:
     * 
     * <pre>
     * // Находим запущенный процесс
     * #set($acts = $Interface.findProcess($proc, $poruch))
     * #if($acts.size() == 1)
     *  // переход на след шаг
     *  $Interface.forceExecuteProcess($acts.get(o), "Игнорировать", "Продолжить редактирвоание")
     * #end
     * </pre>
     * 
     * @param act
     *            обрабатываемый процесс.
     * @param titleiIgnoreError
     *            заголовок кнопки игнорирующей ошибки.
     * @param titleContinueEdit
     *            заголовок кнопки останавливающей выполнение.
     * @throws KrnException
     *             the krn exception
     */
    public int forceExecuteProcess(Activity act, String titleiIgnoreError, String titleContinueEdit) throws KrnException {
        return forceExecuteProcess(act, true, titleiIgnoreError, titleContinueEdit);
    }

    /**
     * Принудительный перевод процесса на следующий шаг.
     * 
     * Пример:
     * 
     * <pre>
     * // Находим запущенный процесс
     * #set($acts = $Interface.findProcess($proc, $poruch))
     * #if($acts.size() == 1)
     *  // переход на след шаг
     *  $Interface.forceExecuteProcess($acts.get(o), true)
     * #end
     * </pre>
     * 
     * @param act
     *            обрабатываемый процесс.
     * @param check
     *            нужна проверка ФЛК?
     * @throws KrnException
     *             the krn exception
     */
    public int forceExecuteProcess(Activity act, boolean check) throws KrnException {
        return forceExecuteProcess(act, check, null, null);
    }

    public int forceExecuteProcess(Activity act, boolean check, KrnObject openArh) throws KrnException {
        return forceExecuteProcess(act, check, null, null, openArh);
    }

    /**
     * Принудительный перевод процесса на следующий шаг.
     * 
     * Пример:
     * 
     * <pre>
     * // Находим запущенный процесс
     * #set($acts = $Interface.findProcess($proc, $poruch))
     * #if($acts.size() == 1)
     *  // переход на след шаг
     *  $Interface.forceExecuteProcess($acts.get(o), true)
     * #end
     * </pre>
     * 
     * @param act
     *            обрабатываемый процесс.
     * @param check
     *            нужна проверка ФЛК?
     * @throws KrnException
     *             the krn exception
     */
    public int forceExecuteProcess(Activity act, boolean check, String titleiIgnoreError, String titleContinueEdit) throws KrnException {
        return forceExecuteProcess(act, check, titleiIgnoreError, titleContinueEdit,null);
    }

    /**
     * Принудительный перевод процесса на следующий шаг.
     * 
     * Пример:
     * 
     * <pre>
     * // Находим запущенный процесс
     * #set($acts = $Interface.findProcess($proc, $poruch))
     * #if($acts.size() == 1)
     *  // переход на след шаг
     *  $Interface.forceExecuteProcess($acts.get(o), true, "Игнорировать", "Продолжить редактирвоание")
     * #end
     * </pre>
     * 
     * @param act
     *            обрабатываемый процесс.
     * @param check
     *            нужна проверка ФЛК?
     * @param titleiIgnoreError
     *            заголовок кнопки игнорирующей ошибки.
     * @param titleContinueEdit
     *            заголовок кнопки останавливающей выполнение.
     * @throws KrnException
     *             the krn exception
     */
    public int forceExecuteProcess(Activity act, boolean check, String titleiIgnoreError, String titleContinueEdit, KrnObject openArh)
            throws KrnException {
        WebFrame mframe = (WebFrame) frame;
        WebFrameManager ifc = (WebFrameManager) mframe.getInterfaceManager();
        boolean exit = false;
        boolean frameChanged = false;
        CommitResult cr = null;
        WebSession s = mframe.getSession();
        int result=-1;
        List<OrCalcRef> calcs = OrCalcRef.removeCalculations();
        try {
	        do {
	            exit = ifc.getCurrentFrame().getObj().equals(act.firstUI)
	                    && ifc.getIndex() == ifc.getIndex(ifc.getCurrentFrame());
	            cr = ifc.beforePrevious(check, false, titleContinueEdit, titleiIgnoreError);
	            if (cr != WITH_ERRORS && cr != WITHOUT_ERRORS) {
	                break;
	            } else {
	                if (!exit)
	                	frameChanged = ifc.afterPrevious(exit, check, frame.getKernel().isSE_UI(), cr);
	            }
	        } while (!exit);
        } finally {
	        if (calcs != null) {
	        	if (frameChanged) calcs.clear();
	            OrCalcRef.setCalculations(calcs);
	        }
        }
        
        if (cr == CommitResult.WITH_FATAL_ERRORS) {
            s.sendMultipleCommand("alert", "Процесс нельзя отправить на следующий шаг,\nпока есть ошибки заполнения данных!");
        } else if (cr == WITH_ERRORS || cr == WITHOUT_ERRORS) {
        	act.openArh = openArh;
            int res = s.getTaskHelper().next(act, mframe, true);
            if (res == ButtonsFactory.BUTTON_NOACTION) return result;
            calcs = OrCalcRef.removeCalculations();
            try {
            	frameChanged = ifc.afterPrevious(exit, check, frame.getKernel().isSE_UI(), cr);
            } finally {
	            if (calcs != null) {
		        	if (frameChanged) calcs.clear();
	                OrCalcRef.setCalculations(calcs);
	            }
            }
            if (res == ButtonsFactory.BUTTON_YES) {
            	if (!frame.getKernel().isSE_UI()) {
                    s.sendCommand("main_ui", "");
                }
            	result=1;
            } else {
                s.sendCommand("next_ui", String.valueOf(act.flowId));
            }
        }
        return result;
    }

    /**
     * Открывает интерфейс процесса.
     * 
     * <pre>
     * $Interface.openProcess($acts.get(o))
     * </pre>
     * 
     * @param act
     *            обрабатываемый процесс.
     */
    public void openProcess(Activity act) {
        ((WebFrame) frame).getSession().getTaskHelper().openUI(act, false);
    }

    /**
     * Закрыть текущий процесс, производится выход из открытого интерфейса.
     * 
     * @throws KrnException
     *             the krn exception
     */
    public void closeProcess() throws KrnException {
        WebFrame mframe = (WebFrame) frame;
        WebFrameManager ifc = (WebFrameManager) mframe.getInterfaceManager();

        boolean frameChanged = false;
        List<OrCalcRef> calcs = OrCalcRef.removeCalculations();
        CommitResult cr = null;
        try {
	        cr = ifc.beforePrevious(true, true);
	        frameChanged = ifc.afterPrevious(true, true, frame.getKernel().isSE_UI(), cr);
        } finally {
	        if (calcs != null) {
	        	if (frameChanged) calcs.clear();
	            OrCalcRef.setCalculations(calcs);
	        }
        }
        if (cr != CommitResult.CONTINUE_EDIT && !frame.getKernel().isSE_UI()) {
            mframe.getSession().sendCommand("main_ui", "");
        }
    }
    
    public void executeTree(String operation) {
    	((WebFrame) frame).getSession().sendCommand("makeTreeTable", operation);
    }
    
    public void closeInterface() {
    	((WebFrame) frame).getSession().sendCommand("closeInterface", "");
    }
    
    public void closePopupInterface() {
    	closePopupInterface("");
    }
    
    public void closePopupInterface(String val) {
    	((WebFrame) frame).getSession().sendCommand("closePopupInterface", val);
    }
    
    /**
     * Выйти из открытого интерфейса.
     * 
     * @throws KrnException
     *             the krn exception
     */
    public void exitAllInterfaces() throws KrnException {
        WebFrame mframe = (WebFrame) frame;
        WebFrameManager ifc = (WebFrameManager) mframe.getInterfaceManager();

        while (ifc.hasPrev()) {
        	ifc.prev();
        }
        mframe.getSession().sendCommand("main_ui", "");
    }

    /**
     * Закрыть процесс.
     * 
     * @param act
     *            закрываемый процесс.
     * @throws KrnException
     *             the krn exception
     */
    public void closeProcess(Activity act) throws KrnException {
        WebFrame mframe = (WebFrame) frame;
        WebFrameManager ifc = (WebFrameManager) mframe.getInterfaceManager();
        CommitResult cr;

        boolean frameChanged = false;
        boolean exit = false;
        List<OrCalcRef> calcs = OrCalcRef.removeCalculations();
        try {
	        do {
	            exit = ifc.getCurrentFrame().getObj().equals(act.firstUI)
	                    && ifc.getIndex() == ifc.getIndex(ifc.getCurrentFrame());
	            cr = ifc.beforePrevious(true, true);
	            if (/* cr != WITH_ERRORS && */cr != WITHOUT_ERRORS) {
	                break;
	            }
	
	        } while (!exit);
	        frameChanged = ifc.afterPrevious(exit, true, frame.getKernel().isSE_UI(), cr);
        } finally {
	        if (calcs != null) {
	        	if (frameChanged) calcs.clear();
	            OrCalcRef.setCalculations(calcs);
	        }
        }
        if (cr != CONTINUE_EDIT && !frame.getKernel().isSE_UI()) {
            mframe.getSession().sendCommand("main_ui", "");
        }
    }

    /**
     * Очистить все кэшированные данные объекта.
     * При повторном запросе <code>getAttr</code> будет брать не с кеша, а заново с сервера.
     * 
     * @param obj
     *            очищаемый объект.
     */
    public void dropCache(KrnObject obj) {
    	dropCache(obj, false);
    }
    
    public void dropCache(KrnObject obj, boolean recursive) {
        Cache cache = frame.getCash();
        cache.drop(obj, recursive);
    }

    public void dropCache(List<KrnObject> objs) {
    	dropCache(objs, false);
    }

    public void dropCache(List<KrnObject> objs, boolean recursive) {
        Cache cache = frame.getCash();
        cache.drop(objs, recursive);
    }

    /**
     * Открыть файл.
     * 
     * @param f
     *            открываемый файл.
     */
    public void openFile(File f) {
    	openFile(f, "Report", f.getName().substring(f.getName().lastIndexOf(".")));
    }

    public void openFile(File f, String fname) {
    	String prefix = "Report";
    	String ext = ".tmp";
    	if (fname != null) {
    		int index = fname.lastIndexOf(".");
    		prefix = fname.substring(0, index);
    		ext = fname.substring(index);
    	}
    	openFile(f, prefix, ext);
    }

    public void openFile(File f, String prefix, String ext) {
        try {
            WebFrame mframe = (WebFrame) frame;
            File tmpFile = Funcs.createTempFile(prefix + "_", ext, WebController.WEB_DOCS_DIRECTORY);
            mframe.getSession().deleteOnExit(tmpFile);
            Funcs.copy(f, tmpFile);
            mframe.getSession().sendCommand("open_report", Base64.encodeBytes(tmpFile.getName().getBytes()));
        } catch (Exception e) {
        	SecurityContextHolder.getLog().error(e, e);
        }
    }

    public void openFile(byte[] bs) {
    	openFile(bs, "Report", ".tmp");
    }

    public void openFile(byte[] bs, String fname) {
    	String prefix = "Report";
    	String ext = ".txt";
    	if (fname != null) {
    		int index = fname.lastIndexOf(".");
    		prefix = fname.substring(0, index);
    		ext = fname.substring(index);
    	}
    	openFile(bs, prefix, ext);
    }

    public void openFile(byte[] bs, String prefix, String ext) {
        try {
            WebFrame mframe = (WebFrame) frame;
            File tmpFile = Funcs.createTempFile(prefix + "_", ext, WebController.WEB_DOCS_DIRECTORY);
            mframe.getSession().deleteOnExit(tmpFile);
            Funcs.write(bs, tmpFile);
            mframe.getSession().sendCommand("open_report", Base64.encodeBytes(tmpFile.getName().getBytes()));
        } catch (Exception e) {
        	SecurityContextHolder.getLog().error(e, e);
        }
    }
    
    /**
     * Получить путь к директории логов.
     * 
     * @return путь к директории логов.
     */
    public String getLogFolder() {
        return WebController.LOG_FOLDER;
    }

    /**
     * Помечает необходимое значение атрибута как "выбранное".
     * Если значение не найдено, оно добавляется.
     * 
     * @param path
     *            путь к атрибуту
     * @param obj
     *            необходимое значение атрибута.
     */
    public void setSelection(String path, KrnObject obj) {
        OrRef ref = frame.getRefs().get(Utils.normalizePath2(path));
        ref.absolute(obj, this);
    }

    /**
     * Блокирует ввод данных на интерфейсе и выводит ожидающий курсор.
     */
    public void waitCursor() {
        ((WebFrame) frame).getSession().sendCommand("cursor", "1");
    }

    /**
     * Разблокировка интерфейса и возврат курсора "по умолчанию".
     */
    public void defaultCursor() {
        ((WebFrame) frame).getSession().sendCommand("cursor", "0");
    }

    /**
     * ТОЛЬКО WEB
     * Получить выбранный на интерфейсе компонент.
     * 
     * @return выбранный компонент, или <code>null</code>.
     */
    
    public OrGuiComponent getSelectedComponent() {
        return ((WebFrame) frame).getSelectedComponent();
    }
    
    public void addProcessListener(KrnObject procDef) {
    	((WebFrame) frame).getSession().getTaskHelper().addProcessListener(procDef.id, (WebFrame)frame);
    }

    public void addAttrChangeListener(KrnClass cls) {
    	try {
    		((WebFrame) frame).getKernel().addAttrChangeListener(cls.id);
    	} catch (Exception e) {
    		SecurityContextHolder.getLog().error(e, e);
    	}
    }

    public void loadInterface(OrWebPanel p, KrnObject obj) {
    	WebFrame parentFrm = (WebFrame)frame;
    	WebFrame frm = parentFrm.getSession().getFrameManager().createChildFrame(obj, (WebFrame)frame);
    	OrGuiContainer comp = (OrGuiContainer)p.getParent();
    	Object cs = comp.removeComponent(p);
    	comp.addComponent(frm.getPanel(), cs);
    	
    	WebComponent newPanel = (WebComponent)frm.getPanel();
    	String uuid = newPanel.uuid;

    	parentFrm.registerComponent(p.uuid, newPanel);
    	parentFrm.getSession().getFrameManager().putFrameByPanelUid(p.uuid, frm);
    	
    	newPanel.uuid = p.uuid;
    	newPanel.setVarName(p.getVarName());
    	
    	try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
    		String str = TemplateHelper.load(parentFrm.getSession().getWebUser().getGUID(), frm.getInterfaceUid(), frm.getIfcLang().id, parentFrm.getKernel(),
	    			WebUITemplateController.ORUITEMPLATES_HOME, false);
	    	
	    	int uuidPos = str.indexOf(" id=") + 5;
	    	int htmlBeg = str.substring(0, uuidPos).lastIndexOf('<');
	    	int parentCount = 0;
	    	int ltPos = htmlBeg;
	    	while (ltPos > -1) {
	    		ltPos = str.substring(0, ltPos).lastIndexOf('<');
	    		parentCount++;
	    	}
	    	str = str.substring(htmlBeg, uuidPos) + newPanel.uuid + str.substring(uuidPos + uuid.length());
	    	for (int i=0; i<parentCount; i++) {
	    		int htmlEnd = str.lastIndexOf('<');
	    		str = str.substring(0, htmlEnd);
	    	}
	    	newPanel.setHtml(str);
    	} catch (IOException e) {
    		SecurityContextHolder.getLog().error(e, e);
    	}
    	p.removeChangeProperties();
    	parentFrm.getSession().sendCommand("reload", newPanel.uuid);
    }
    /**
     * Получить идентификатор текущего интерфейса.
     * 
     * @return Uid идентификатор текущего интерфейса.
     */
    public String getUid() {
        return ((WebFrame) frame).getInterfaceUid();
    }
    
    public List<CacheChangeRecord> getChanges() {
    	return frame.getCash().getChanges();
    }
    
    public boolean connectScanWebsocket() {
        return ((WebFrame) frame).connectScanWebsocket();
    }
    
    public boolean disconnectScanWebsocket() {
        return ((WebFrame) frame).disconnectScanWebsocket();
    }

    public JsonObject loadClientFile(String path) {
        if (path != null && path.length() > 0) {
            return ((WebFrame) frame).wsLoadClientFile(path);
        }
        return null;
    }

    public JsonObject saveFileOnClient(String path, byte[] content) {
        if (path != null && path.length() > 0 && content != null) {
            return ((WebFrame) frame).wsSaveFileOnClient(path, content);
        }
        return null;
    }

    public JsonObject saveFileOnClient(String path, File file) {
        if (path != null && path.length() > 0 && file != null) {
        	try {
        		byte[] content = Funcs.read(file);
        		return ((WebFrame) frame).wsSaveFileOnClient(path, content);
        	} catch (IOException e) {
        		log.error(e, e);
        	}
        }
        return null;
    }

    public JsonObject startScan() {
        return ((WebFrame) frame).wsStartScan();
    }

    public JsonObject startScan(String id) {
        return ((WebFrame) frame).wsStartScan(id);
    }
    
    public JsonObject openClientFiles(String dialogTitle, String buttonTitle, String lastPath, String extensions, String desc) {
        return ((WebFrame) frame).wsOpenClientFiles(dialogTitle, buttonTitle, lastPath, extensions, desc);
    }
}
