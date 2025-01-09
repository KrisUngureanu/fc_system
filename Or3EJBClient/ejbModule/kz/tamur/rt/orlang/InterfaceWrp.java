package kz.tamur.rt.orlang;

import static kz.tamur.rt.InterfaceManager.CommitResult.CONTINUE_EDIT;
import static kz.tamur.rt.InterfaceManager.CommitResult.WITHOUT_ERRORS;
import static kz.tamur.rt.InterfaceManager.CommitResult.WITH_ERRORS;

import java.awt.Dimension;
import java.awt.Frame;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.Context;

import kz.gamma.asn1.ASN1Sequence;
import kz.gamma.asn1.x509.TBSCertificateStructure;
import kz.gamma.util.encoders.Base64;
import kz.tamur.comps.Mode;
import kz.tamur.comps.OrFrame;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.OrGuiContainer;
import kz.tamur.comps.OrPanel;
import kz.tamur.comps.OrTabbedPane;
import kz.tamur.comps.OrTreeTable2;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.or3.util.PathElement2;
import kz.tamur.rt.CheckContext;
import kz.tamur.rt.InterfaceManager.CommitResult;
import kz.tamur.rt.InterfaceManagerFactory;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.TaskTable;
import kz.tamur.rt.adapters.OrCalcRef;
import kz.tamur.rt.adapters.OrRef;
import kz.tamur.rt.adapters.OrRef.Item;
import kz.tamur.rt.adapters.UIFrame;
import kz.tamur.rt.data.Cache;
import kz.tamur.util.CacheChangeRecord;
import kz.tamur.util.Funcs;
import kz.tumar.Signer32;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.Utils;
import com.cifs.or2.kernel.Activity;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnDate;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.ProcessException;
import com.cifs.or2.util.CursorToolkit;

/**
 * Created by IntelliJ IDEA.
 * 
 * Date: 10.01.2005
 * 
 * @author berik
 */
public class InterfaceWrp {

    /** log. */
    private static Log log = LogFactory.getLog(InterfaceWrp.class);

    /** ctx. */
    private CheckContext ctx;

    /** frame. */
    private OrFrame frame;

    /** before open. */
    private boolean beforeOpen;

    /** obj map. */
    private Map<String, KrnObject[]> objMap;

    /** Прозрачность диалогов. */
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;

    /**
     * Конструктор класса InterfaceWrp.
     * 
     * @param frame
     *            фрейм, для которого выполняются методы класса.
     * @param beforeOpen
     *            флаг - перед открытием фрейма.
     */
    public InterfaceWrp(OrFrame frame, boolean beforeOpen) {
        super();
        this.frame = frame;
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
    	return ((UIFrame) frame).getUserDecision();
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
        OrRef ref = refs.get(Utils.normalizePath(path));
        boolean isArray = false;
        try {
            PathElement2[] ps = Utils.parsePath2(path);
            if (ps.length > 0) {
                PathElement2 p = ps[ps.length - 1];
                KrnAttribute attr = p.attr;
                isArray = (attr != null && attr.collectionType > 0 && p.index == null);
            }
            return getValue(ref, isArray, langId);
        } catch (Exception e) {
            log.error("$Interface.getAttr(\"" + path + "\")");
            log.error(e, e);
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
        OrRef ref = refs.get(Utils.normalizePath(path));
        if (ref != null && ref.isArray()) {
        	if (index < 0)
        		index = ref.getItems(0).size() + index;
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
        OrRef ref = refs.get(Utils.normalizePath(path));
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
        OrRef ref = refs.get(Utils.normalizePath(path));
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
        OrRef ref = refs.get(Utils.normalizePath(path));
        try {
            ref.deleteItem(ctx, index, this);
        } catch (KrnException e) {
            e.printStackTrace();
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
        OrRef ref = refs.get(Utils.normalizePath(path));
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
            List<Object> v = new ArrayList<Object>(items.size());
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
                KrnObject[] objs = col.toArray(new KrnObject[col.size()]);
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
            e.printStackTrace();
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
        kz.tamur.rt.InterfaceManager mgr = InterfaceManagerFactory.instance().getManager();
        UIFrame frame = mgr.getCurrentInterface();
        if (frame != null) {
            MessagesFactory.showMessageDialog((Frame) frame.getPanel().getTopLevelAncestor(), type == 1 ? 4 : 0, str);
        }
    }
    
    public int setActiveTab(String name, int index) {
    	int status = 0;
    	OrGuiComponent component = getComponent(name);
    	if (component != null && component instanceof OrTabbedPane) {
            ((OrTabbedPane) component).setSelectedIndex(index);
            status = 1;
    	}
    	return status;
    }
    
    public void setMultiSelection(OrTreeTable2 table, boolean multi) {
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
        kz.tamur.rt.InterfaceManager mgr = InterfaceManagerFactory.instance().getManager();
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
        kz.tamur.rt.InterfaceManager mgr = InterfaceManagerFactory.instance().getManager();
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
        OrRef ref = frame.getRefs().get(Utils.normalizePath(path));
        List<Item> items = ref.getSelectedItems();
        List<Object> res = new ArrayList<Object>(items.size());
        for (Item item : items) {
            res.add(item.getCurrent());
        }
        return res;
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
        OrRef ref = frame.getRefs().get(Utils.normalizePath(path));
        ref.absolute(obj, this);
    }

    /**
     * Проверка данных.
     * 
     * @param data
     *            данные для проверки
     * @param sign
     *            подписанные данные
     * @param cert
     *            сертификат подписавшего
     * @return <code>true</code>, в случае успеха
     */
    public boolean validate(String data, String sign, Object cert) {
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream is = null;
            if (cert instanceof String) {
                byte[] certB = Base64.decode(((String) cert).getBytes());
                is = new ByteArrayInputStream(certB);
            } else if (cert instanceof File) {
                is = new FileInputStream((File) cert);
            } else {
                System.out.println("function VALIDATE(data, sign, cert) - Incorrect param CERT");
                return false;
            }
            X509Certificate c = (X509Certificate) cf.generateCertificate(is);
            is.close();
            byte[] cb = (new TBSCertificateStructure((ASN1Sequence) ASN1Sequence.fromByteArray(c.getTBSCertificate())))
                    .getSubjectPublicKeyInfo().getPublicKeyData().getBytes();
            byte[] s = Base64.decode(sign.getBytes());
            Signer32 s32 = new Signer32();
            int res = s32.verifyString(data.getBytes("UTF-8"), cb, s);
            return res == 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Получить ФИО того кому принадлежит сертификат.
     * 
     * @param cert
     *            сертификат
     * @return cn ФИО человека
     */
    public String getCN(String cert) {
        try {
            byte[] certB = Base64.decode(cert.getBytes());
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            ByteArrayInputStream bis = new ByteArrayInputStream(certB);
            X509Certificate c = (X509Certificate) cf.generateCertificate(bis);
            bis.close();

            return c.getSubjectDN().toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Получить сертификат человека из хранилища расположенного по адресу <code>url</code> с заданным ФИО в <code>cn</code>.
     * 
     * @param url
     *            адрес хранилища сертификатов.
     * @param cn
     *            ФИО человека, сертификат которого необходим
     * @return certificate найденный сертификат, или <code>null</code> если не найден.
     */
    public String getCertificate(String url, String cn) {
        Map<String, String> env = new HashMap<String, String>();

        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        // env.put(Context.SECURITY_AUTHENTICATION, "DIGEST-MD5");
        env.put(Context.PROVIDER_URL, url);
        env.put(Context.SECURITY_PRINCIPAL, "");
        env.put(Context.SECURITY_CREDENTIALS, "");
        env.put("java.naming.ldap.attributes.binary", "userCertificate");

        List<File> fs = Funcs.getCertificatesFromLDAP(env, "", cn);

        try {
            if (fs != null && fs.size() > 0) {
                byte[] b = new byte[(int) fs.get(0).length()];
                FileInputStream fis = new FileInputStream(fs.get(0));
                fis.read(b);
                fis.close();
                return new String(Base64.encode(b));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
     * Получить сертификат пользователя.
     * Применимо для тех пользователей, что залогинились через ЭЦП
     * 
     * @return cert сертификат
     */
    public String getCert() {
        return Kernel.instance().getCert();
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
            log.info("<OBJECT>.getAttr(\"" + path + "\")");
            e.printStackTrace();
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
            e.printStackTrace();
        }
        setAttr(path, v);
    }

    /**
     * Показать интерфейс.
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

        UIFrame frm = null;
        boolean commit = false;

        OrFrame curFrame = ClientOrLang.getFrame();
        try {
            List<OrCalcRef> calcs = OrCalcRef.removeCalculations();
            frm = mgr.getInterfacePanel(ui.getKrnObject(), objArray, tr_id, frame.getEvaluationMode(), shareCache, false);

            OrRef selectionRef = null;
            if (selectionPath != null && selectionPath.trim().length() > 0) {
                selectionRef = OrRef.createRef(selectionPath, false, Mode.RUNTIME, frm.getRefs(), OrRef.TR_CLEAR, frm);
            }

            OrPanel panel = frm.getPanel();
            panel.setOpaque(isOpaque);
            String title = panel.getTitle();
            DesignerDialog dlg = kz.tamur.comps.Utils.getDesignerDialog(ClientOrLang.getComponent().getTopLevelAncestor(), title,
                    panel, false);
            dlg.setLanguage(frm.getInterfaceLang().id);
            dlg.setFirstRow(frm);
            Dimension pSize = panel.getPrefSize();
            if (pSize == null) {
                pSize = kz.tamur.comps.Utils.getMaxWindowSize();
            }
            dlg.setSize(pSize);
            dlg.setLocation(kz.tamur.comps.Utils.getCenterLocationPoint(dlg.getSize()));
            dlg.show();
            if (calcs != null)
                OrCalcRef.setCalculations(calcs);
            if (dlg.isOK()) {
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
        MainFrame mframe = (MainFrame) frame.getInterfaceManager();
        mframe.rollbackCurrent();
    }

    /**
     * обновление данных с сервера.
     */
    public void rollbackAfterCommit() {
        try {
			frame.getInterfaceManager().getCurrentInterface().getRef().rollback(this,frame.getFlowId());
		} catch (KrnException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
    }

    /**
     * Получить все объекты заданные через <code>setAttr</code>.
     * 
     * @return карта объектов
     */
    public Map<String, KrnObject[]> getObjMap() {
        return objMap;
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
        CursorToolkit.startWaitCursor(TaskTable.instance(false));
        String[] res = Kernel.instance().startProcess(def.id, vars);
        CursorToolkit.stopWaitCursor(TaskTable.instance(false));
        if (res.length > 0 && !"".equals(res[0])) {
            showMsg(res[0], MessagesFactory.ERROR_MESSAGE);
            return null;
        } else {
            List<String> param = new ArrayList<String>();
            param.add("autoIfc");
            if (res.length > 3) {
                param.add(res[3]);
            }
            return TaskTable.instance(false).startProcess(res[1], param);
        }
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
        return TaskTable.instance(false).findProcess(def, obj);
    }

    /**
     * Поиск процесса у которого не является инициатором.
     * 
     */
    public List<Long> findForeignProcess(KrnObject def, KrnObject obj) throws KrnException {
        return TaskTable.instance(false).findForeignProcess(def, obj);
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
        return TaskTable.instance(false).stopProcess(activity, false);
    }

    public boolean stopProcess(Activity activity, boolean forceCancel) throws KrnException {
        return TaskTable.instance(false).stopProcess(activity, forceCancel);
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
        MainFrame mframe = (MainFrame) frame.getInterfaceManager();
        CommitResult cr = mframe.beforePrevious();
        if (cr == WITH_ERRORS || cr == WITHOUT_ERRORS) {
            int res = TaskTable.instance(false).next(act, mframe);
            if (res == ButtonsFactory.BUTTON_YES) {
                mframe.afterPrevious(true, true, cr);
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
        MainFrame mframe = (MainFrame) frame.getInterfaceManager();
        CommitResult cr = CONTINUE_EDIT;
        List<OrCalcRef> calcs = OrCalcRef.removeCalculations();
        if (titleiIgnoreError == null || titleContinueEdit == null) {
            cr = mframe.commitCurrent();
        } else {
            cr = mframe.commitCurrent(new String[] { titleContinueEdit, titleiIgnoreError }, null, true, false);
        }
        if (calcs != null) {
            OrCalcRef.setCalculations(calcs);
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
    public void forceExecuteProcess(Activity act) throws KrnException {
        forceExecuteProcess(act, true);
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
    public void forceExecuteProcess(Activity act, String titleiIgnoreError, String titleContinueEdit) throws KrnException {
        forceExecuteProcess(act, true, titleiIgnoreError, titleContinueEdit);
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
    public void forceExecuteProcess(Activity act, boolean check) throws KrnException {
        forceExecuteProcess(act, check, null, null);
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
    public void forceExecuteProcess(Activity act, boolean check, String titleiIgnoreError, String titleContinueEdit)
            throws KrnException {
        MainFrame mframe = (MainFrame) frame.getInterfaceManager();
        boolean exit = false;
        CommitResult cr = null;
        List<OrCalcRef> calcs = OrCalcRef.removeCalculations();
        do {
            exit = mframe.getCurrentInterface().getObj().equals(act.firstUI)
                    && mframe.getFrameManager().getIndex() == mframe.getFrameManager().getIndex(mframe.getCurrentInterface());
            cr = mframe.beforePrevious(check, false, titleContinueEdit, titleiIgnoreError);
            if (cr != WITH_ERRORS && cr != WITHOUT_ERRORS) {
                break;
            } else {
                if (!exit)
                    mframe.afterPrevious(exit, check, cr);
            }
        } while (!exit);
        if (calcs != null) {
            OrCalcRef.setCalculations(calcs);
        }
        if (cr == WITH_ERRORS || cr == WITHOUT_ERRORS) {
            TaskTable.instance(false).next(act, mframe, true);
            calcs = OrCalcRef.removeCalculations();
            mframe.afterPrevious(exit, check, cr);
            if (calcs != null) {
                OrCalcRef.setCalculations(calcs);
            }
        } else if (cr == CommitResult.WITH_FATAL_ERRORS) {
            MessagesFactory.showMessageDialog(mframe, MessagesFactory.EXCLAMATION_MESSAGE,
                    "Процесс нельзя отправить на следующий шаг,\nпока есть ошибки заполнения данных!");
        }
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
        TaskTable.instance(false).openUI(act);
    }

    /**
     * Блокирует ввод данных на интерфейсе и выводит ожидающий курсор.
     */
    public void waitCursor() {
        CursorToolkit.startWaitCursor((MainFrame) frame.getInterfaceManager());
    }

    /**
     * Разблокировка интерфейса и возврат курсора "по умолчанию".
     */
    public void defaultCursor() {
        CursorToolkit.stopWaitCursor((MainFrame) frame.getInterfaceManager());
    }

    /**
     * Закрыть текущий процесс, производится выход из открытого интерфейса.
     * 
     * @throws KrnException
     *             the krn exception
     */
    public void closeProcess() throws KrnException {
        MainFrame mframe = (MainFrame) frame.getInterfaceManager();
        CommitResult cr = mframe.beforePrevious();
        mframe.afterPrevious(true, true, cr);
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
        MainFrame mframe = (MainFrame) frame.getInterfaceManager();
        boolean exit = false;
        CommitResult cr;
        do {
            exit = mframe.getCurrentInterface().getObj().equals(act.firstUI)
                    && mframe.getFrameManager().getIndex() == mframe.getFrameManager().getIndex(mframe.getCurrentInterface());
            cr = mframe.beforePrevious();
        } while (!exit);
        mframe.afterPrevious(exit, true, cr);
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
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec(f.getAbsolutePath());
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
    /**
     * ТОЛЬКО WEB
     * 
     * Реализация здесь для исключения падений формул.
     * @return <code>null</code>
     */
    public OrGuiComponent getSelectedComponent() {
        return null;
    }
    /**
     * Получить идентификатор текущего интерфейса.
     * 
     * @return Uid идентификатор текущего интерфейса.
     */
    public String getUid() {
        return frame.getInterfaceManager().getCurrentInterface().getUid();
    }

    public List<CacheChangeRecord> getChanges() {
    	return frame.getCash().getChanges();
    }
}
