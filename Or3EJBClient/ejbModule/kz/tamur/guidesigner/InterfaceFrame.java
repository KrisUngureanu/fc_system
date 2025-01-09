package kz.tamur.guidesigner;

import kz.tamur.comps.*;
import kz.tamur.guidesigner.reports.ReportPrinter;
import kz.tamur.guidesigner.reports.ReportRecord;
import kz.tamur.or3.util.SystemEvent;
import kz.tamur.rt.CheckContext;
import kz.tamur.rt.InterfaceManager;
import kz.tamur.rt.adapters.OrRef;
import kz.tamur.rt.data.Cache;
import kz.tamur.util.MapMap;
import kz.tamur.util.crypto.XmlUtil;
import kz.tamur.util.Funcs;
import kz.tamur.util.LangItem;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.client.Kernel;

import javax.swing.*;

import org.jdom.Element;
import org.jdom.CDATA;
import org.jdom.Text;
import org.jdom.output.XMLOutputter;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

import java.awt.Frame;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.lang.ref.*;
import java.util.*;

/*
 * User: vital
 * Date: 23.10.2005
 * Time: 15:42:43
 */

public class InterfaceFrame implements OrFrame {

    private KrnObject lang;
    private String title;
    private MapMap<Long, String, Object> strings = new MapMap<Long, String, Object>();
    private KrnObject uiObject;
    private JComponent rootPanel;
    private int componentCounter = -1;
    private int stringCounter = -1;
    private Set<Long> modified = new HashSet<Long>();
    private boolean isModified= false;
    private boolean isReadOnly= false;

    public InterfaceFrame(KrnObject uiObject) {
        this.uiObject = uiObject;
    }

    public void setInterfaceLang(KrnObject lang) {
        this.lang = lang;
        if (rootPanel != null) {
            ((OrGuiComponent)rootPanel).setLangId(lang.id);
        }
    }

    public void setInterfaceLang(KrnObject lang, ResourceBundle res) {
        setInterfaceLang(lang);
    }

    public ResourceBundle getResourceBundle() {
        return null;
    }

    public KrnObject getInterfaceLang() {
        return lang;
    }

    public String getString(String uid) {
        return getString(uid, "");
    }

    public String getString(String uid, String defStr) {
        if (uid == null || uid.length() == 0) return defStr;
        Object res_obj=null;
        try {
        	res_obj=loadMessages().get(uid);
            String res = (String)res_obj;
            return (res != null) ? res : defStr;
        } catch (Exception e) {
        	System.out.println("Не является строкой:uid="+uid+"res_cls="+res_obj!=null?res_obj.getClass():null);
            e.printStackTrace();
        }
        return "";
    }

    public byte[] getBytes(String uid) {
        if (uid == null || uid.length() == 0) return null;
        try {
            Object str = loadMessages().get(uid);
            return (str instanceof byte[]) ? (byte[])str : ((str instanceof String) ? ((String)str).getBytes("UTF-8") : null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setString(String uid, String str) {
        if (uid.length() == 0) return;

        try {
        	loadAllStrings();
        } catch (Exception e) {
        	e.printStackTrace();
        }
        Set<Long> keySet = strings.keySet();
        for (Iterator<Long> it = keySet.iterator(); it.hasNext();) {
            Long lang = it.next();
            Map<String, Object> msgs = strings.get(lang);
            if (lang.equals(this.lang.id)) {
                msgs.put(uid, str);
                modified.add(lang);
            }
        }
    }

    public void setBytes(String uid, byte[] bytes) {
        if (uid.length() == 0) return;

        try {
        	loadAllStrings();
        } catch (Exception e) {
        	e.printStackTrace();
        }
        Set<Long> keySet = strings.keySet();
        for (Iterator<Long> it = keySet.iterator(); it.hasNext();) {
            Long lang = it.next();
            Map<String, Object> msgs = strings.get(lang);
            if (lang.equals(this.lang.id)) {
                msgs.put(uid, bytes);
                modified.add(lang);
            }
        }
    }

    public String getNextUid() {
        Map<String, Object> msgs = strings.get(lang.id);
    	while (msgs.get(String.valueOf(++stringCounter)) != null);
        
    	return String.valueOf(stringCounter);
    }

    public String getTitle() {
        return title;
    }

    public JComponent getRootPanel() {
        return rootPanel;
    }

    public void load(final JProgressBar progress) throws Exception {
        FactoryListener flnr = new FactoryListener() {
            public void componentCreated(OrGuiComponent c) {
                progress.setValue(progress.getValue() + 1);
            }

            public void componentCreating(String className) {}
        };
        SAXBuilder b = null;
        Element xml = null;
        try {
            loadMessages();
            final Kernel krn = Kernel.instance();
            String[] strs = krn.getStrings(uiObject, "title", lang.id, 0);
            title = (strs.length > 0) ? strs[0] : "Untitled";
            Factories.instance().addFactoryListener(flnr);
            byte[] data = krn.getBlob(uiObject, "config", 0, 0, 0);
            if (data.length > 0) {
                ByteArrayInputStream is = new ByteArrayInputStream(data);
                b = new SAXBuilder();
                xml = b.build(is).getRootElement();
                progress.setMinimum(0);
                progress.setValue(0);
                progress.setMaximum(kz.tamur.rt.Utils.getChildrenCount(xml));
                is.close();
                String s = xml.getAttributeValue("elementCount");
                componentCounter = (s != null && !s.isEmpty()) ? Integer.parseInt(s) : -1;
                s = xml.getAttributeValue("stringCounter");
                stringCounter = (s != null && !s.isEmpty()) ? Integer.parseInt(s) : -1;
                //Factories.instance().setCounter(componentCounter);
                rootPanel = (JComponent)Factories.instance().create(xml, Mode.DESIGN, this);
            } else {
                //Factories.instance().setCounter(componentCounter);
                rootPanel = (JComponent)Factories.instance().create("Panel", this);
                componentCounter++;
            }
            ((OrGuiComponent)rootPanel).setLangId(lang.id);
        } finally{
            Factories.instance().removeFactoryListener(flnr);
            progress.setValue(0);
            flnr = null;
            b = null;
            xml = null;
        }
    }
    
    /**
     * Загрузка интерфейса для массовой обработки
     * @param progress
     * @throws Exception
     */
    public void loadMass(final JProgressBar progress) throws Exception {
        FactoryListener flnr = new FactoryListener() {
            public void componentCreated(OrGuiComponent c) {
                if (progress != null)
                	progress.setValue(progress.getValue() + 1);
            }

            public void componentCreating(String className) {}
        };
        WeakReference<SAXBuilder> sbuild = new WeakReference<SAXBuilder>(new SAXBuilder());
        Element xml = null;
        ByteArrayInputStream is = null;
        try {
            loadMessages();
            final Kernel krn = Kernel.instance();
            Factories.instance().addFactoryListener(flnr);
            byte[] data = krn.getBlob(uiObject, "config", 0, 0, 0);
            if (data.length > 0) {
                is = new ByteArrayInputStream(data);
                
                if(sbuild == null || sbuild.get() == null){
                    sbuild = new WeakReference<SAXBuilder>(new SAXBuilder());
                }
                sbuild.get().setReuseParser(true);
                
                xml = sbuild.get().build(is).getRootElement();
                is.close();
                rootPanel = (JComponent)Factories.instance().create(xml, Mode.PREVIEW, this);
            } else {
                rootPanel = (JComponent)Factories.instance().create("Panel", this);
                componentCounter++;
            }
            ((OrGuiComponent)rootPanel).setLangId(lang.id);
        } finally{
            Factories.instance().removeFactoryListener(flnr);
            if (progress != null) progress.setValue(0);
            flnr = null;
            xml = null;
            is = null;
        }
    }
    

    public void save(JProgressBar progress) throws Exception {
        try {
	        save(progress, uiObject, false);
	        saveWebConfig(false);
        } catch (KrnException e) {
        	MessagesFactory.showMessageDialog((Frame)rootPanel.getTopLevelAncestor(), MessagesFactory.ERROR_MESSAGE, e.getMessage(), LangItem.getById(lang.id));
        }
    }

    void save(JProgressBar progress, KrnObject obj, boolean isCopyCreating) throws Exception {
        final Kernel krn = Kernel.instance();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        XMLOutputter out = new XMLOutputter();
        out.getFormat().setEncoding("UTF-8");
        OrGuiComponent c = (OrGuiComponent)rootPanel;
        c.getXml().setAttribute("elementCount", "" + componentCounter);
        c.getXml().setAttribute("stringCounter", "" + stringCounter);
        out.output(c.getXml(), os);
        os.close();
    	krn.setBlob(obj.id, obj.classId, "config", 0, os.toByteArray(), 0, 0);
        saveMessages(obj, isCopyCreating);
        krn.writeLogRecord(SystemEvent.EVENT_CHANGE_INTERFACE, title);
        krn.interfaceChanged(obj.id);
    }

    
    /**
     * Сохранить интерфейс в виде html в аттрибуте webConfig класса UI
     * @throws Exception
     * @param boolean параметр для типа генерации. если истина, то массовая, если нет, то одиночная
     */
    public void saveWebConfig(boolean massGenerate) throws Exception{
    	saveWebConfig(uiObject, massGenerate);
    }

    private void saveWebConfig(KrnObject obj, boolean massGen) throws Exception {
        final Kernel krn = Kernel.instance();
        boolean webConfChanged = false;
        if (!massGen) {
            KrnClass krncls = krn.getClass(obj.classId);
            KrnAttribute attr = krn.getAttributeByName(krncls, "webConfigChanged");
            webConfChanged = krn.getLongs(obj, attr, 0)[0] != 0;
        }

        long[] langIds = { krn.getLangIdByCode("RU"), krn.getLangIdByCode("KZ") };
        
        if (!webConfChanged || massGen) {
            StringBuilder sb = new StringBuilder();
            OrHtmlGenerator htmlGen = null;

            htmlGen = new OrHtmlGenerator((OrPanel) ((OrGuiComponent) rootPanel), obj);
            String msgsXml;
            InputSource is;
            for (int i = 0; i < langIds.length; i++) {
                msgsXml = new String(krn.getBlob(obj, "strings", 0, langIds[i], 0), "UTF-8");
                if (!msgsXml.isEmpty() && msgsXml.contains("uid")) {
                    is = new InputSource(new StringReader(msgsXml));
                    sb = htmlGen.generateHtml(XmlUtil.getDocument(is));
                    krn.setBlob(obj.id, obj.classId, "webConfig", 0, sb.toString().getBytes("UTF-8"), langIds[i], 0);
                }
            }
            msgsXml = null;
            htmlGen = null;
            sb = null;

            if (massGen) {
                uiObject = null;
                strings = null;
            }
        } else {
            String[] options = { "YES", "NO" };
            int response = JOptionPane.showOptionDialog(null, "Аттрибут webConfig интерфейса \" " + this.title
                    + " \" ранеее был изменен вручную.\n\nПерезаписать ?", "Внимание!", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
            if (response == JOptionPane.YES_OPTION) {
                StringBuilder sb = new StringBuilder();
                OrHtmlGenerator htmlGen = null;
                htmlGen = new OrHtmlGenerator((OrPanel) ((OrGuiComponent) rootPanel), obj);
                String msgsXml;
                InputSource is;
                for (int i = 0; i < langIds.length; i++) {
                    msgsXml = new String(krn.getBlob(obj, "strings", 0, langIds[i], 0), "UTF-8");
                    if (!msgsXml.isEmpty()) {
                        is = new InputSource(new StringReader(msgsXml));
                        sb = htmlGen.generateHtml(XmlUtil.getDocument(is));
                        krn.setBlob(obj.id, obj.classId, "webConfig", 0, sb.toString().getBytes("UTF-8"), langIds[i], 0);
                        krn.setLong(obj.id, obj.classId, "webConfigChanged", 0, 0, 0);
                    }

                }
                msgsXml = null;
                htmlGen = null;
                sb = null;
            }
        }
    }
    
    
    private Map<String, Object> loadMessages() throws Exception {
        long lid = lang.id;
        Map<String, Object> msgs = strings.get(lid);
        if (msgs == null) {
            msgs = new HashMap<String, Object>();
            strings.put(lid, msgs);
            final Kernel krn = Kernel.instance();
            byte[] strings = krn.getBlob(uiObject, "strings", 0, lid, 0);
            if (strings.length > 0) {
                ByteArrayInputStream is = new ByteArrayInputStream(strings);
                org.jdom2.input.SAXBuilder b = XmlUtil.createSaxBuilder2();
                org.jdom2.Element e = b.build(is).getRootElement();
                if ("Messages".equals(e.getName())) {
	                List<org.jdom2.Element> chs = e.getChildren("Msg");
	                
	            	int count = chs.size();
	            	count = Funcs.checkInt(count, 30000);
	
	                for (int i = 0; i < count; i++) {
	                    org.jdom2.Element ch = chs.get(i);
	                    String uid = ch.getAttributeValue("uid");
	                    if (ch.getContentSize() > 0) {
	                        for (int j=0; j<ch.getContentSize(); j++) {
	                            if (ch.getContent(j) instanceof org.jdom2.CDATA) {
	                                String s = ((org.jdom2.CDATA)ch.getContent(j)).getText();
	                                byte[] value = s.getBytes();
	                                msgs.put(uid, value);
	                            } else if (ch.getContent(j) instanceof org.jdom2.Text) {
	                                String value = ch.getText();
	                                if (!"Безымянный".equals(value))
	                                    msgs.put(uid, value);
	                            }
	                        }
	                    }
	                }
                }
	            b = null;
            }
        }
        return msgs;
    }

    private void saveMessages(KrnObject obj, boolean isCopyCreating) throws Exception {
        final Kernel krn = Kernel.instance();
        XMLOutputter out = new XMLOutputter();
        out.getFormat().setEncoding("UTF-8");
        Iterator<Long> it = null;
        if (isCopyCreating) {
            loadAllStrings();
            it = strings.keySet().iterator();
        } else {
            it = modified.iterator();
        }
        while (it.hasNext()) {
            Long lid = (Long) it.next();
            Element e = new Element("Messages");
            Map<String, Object> msgs = strings.get(lid);
            for (Iterator<String> uidIt = msgs.keySet().iterator(); uidIt.hasNext();) {
                String uid = uidIt.next();
                Element ch = new Element("Msg");
                ch.setAttribute("uid", uid);
                Object msg = msgs.get(uid);
                if (msg instanceof String) {
                    ch.setText((String)msg);
                } else if (msg instanceof byte[]) {
                    CDATA cdata = new CDATA(new String((byte[])msg));
                    ch.addContent(cdata);
                }
                e.addContent(ch);
            }
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            out.output(e, os);
            os.close();
            krn.setBlob(obj.id, obj.classId, "strings", 0,
                    os.toByteArray(), lid.longValue(), 0);
        }
        modified.clear();
    }

    public InterfaceFrame makeCopy(KrnObject newObj, JProgressBar progress)
            throws Exception {
        save(progress, newObj, true);
        InterfaceFrame res = new InterfaceFrame(newObj);
        res.lang = lang;
        return res;
    }

    public int getInterfaceCounter() {
        return componentCounter;
    }

    public KrnObject getUiObject() {
        return uiObject;
    }

    public boolean isModified() {
        return isModified;
    }

    public boolean isReadOnly() {
        return isReadOnly;
    }

    public void setReadOnly(boolean readOnly) {
        isReadOnly = readOnly;
    }

    public void setModified(boolean modified) {
        isModified = modified;
    }

    public void setComponentCounter(int componentCounter) {
        this.componentCounter = componentCounter;
    }

    private void loadAllStrings() throws Exception {
        List<LangItem> langs = LangItem.getAll();
        for (int i = 0; i < langs.size(); i++) {
            LangItem item = langs.get(i);
            Map<String, Object> m = strings.get(new Long(item.obj.id));
            if (m == null) {
                m = new HashMap<String, Object>();
                strings.put(new Long(item.obj.id), m);
                final Kernel krn = Kernel.instance();
                byte[] strings = krn.getBlob(uiObject, "strings", 0, item.obj.id, 0);
                if (strings.length > 0) {
                    ByteArrayInputStream is = new ByteArrayInputStream(strings);
                    org.jdom2.input.SAXBuilder b = XmlUtil.createSaxBuilder2();
                    org.jdom2.Element e = b.build(is).getRootElement();
                    if ("Messages".equals(e.getName())) {
    	                List<org.jdom2.Element> chs = e.getChildren("Msg");
	                    for (int j = 0; j < chs.size(); j++) {
	                        org.jdom2.Element ch = chs.get(j);
	                        String uid = ch.getAttributeValue("uid");
	                        if (ch.getContentSize() > 0) {
	                            for (int k=0; k<ch.getContentSize(); k++) {
	                                if (ch.getContent(k) instanceof org.jdom2.CDATA) {
	                                    String s = ((org.jdom2.CDATA)ch.getContent(k)).getText();
	                                    byte[] value = s.getBytes();
	                                    m.put(uid, value);
	                                } else if (ch.getContent(k) instanceof org.jdom2.Text) {
	                                    String value = ch.getText();
	                                    m.put(uid, value);
	                                }
	                            }
	                        }
	                    }
                    }
                }
            }
        }
    }

    public Object getString(Long lang, String uid) {
        Object res = null;
        try {
            loadAllStrings();
            res = strings.get(lang, uid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public void setCopyString(Long lang, String uid, Object text) throws Exception {
        loadAllStrings();
        strings.put(lang, uid, text);
        modified.add(lang);
    }

    public void removeStrings(String uid) {
    	if (uid != null) {
	        Long[] langs = frameLangs();
	        for (int i = 0; i < langs.length; i++) {
	            Long lang = langs[i];
	            strings.remove(lang, uid);
	            modified.add(lang);
	        }
    	}
    }

    public Long[] frameLangs() {
        Long[] res = null;
        try {
            loadAllStrings();
            Set<Long> s = strings.keySet();
            res = new Long[s.size()];
            Iterator<Long> it = s.iterator();
            List<Long> l = new ArrayList<Long>();
            while(it.hasNext()) {
                l.add(it.next());
            }
            for (int i = 0; i < l.size(); i++) {
                Long lang =  l.get(i);
                res[i] = lang;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

	public void addRefGroup(int group, CheckContext context) {
	}

	public Cache getCash() {
		return null;
	}

	@Override
	public long getFlowId() {
		return 0;
	}
	
    public long getTransactionId() {
        return 0;
    }
	public Map<String, OrRef> getContentRef() {
		return null;
	}

	public List<CheckContext> getRefGroups(int group) {
		return null;
	}

	public Map<String, OrRef> getRefs() {
		return null;
	}

	public OrGuiComponent getPanel() {
		return null;
	}

	public int getTransactionIsolation() {
		return 0;
	}

	public ReportPrinter getReportPrinter(long id) {
		return null;
	}

	public void addReport(ReportPrinter report) {
	}

	public KrnObject getDataLang() {
		return null;
	}

	public int getEvaluationMode() {
		return 0;
	}

    public void setRootReport(ReportRecord reportRecord) {
    }

	public InterfaceManager getInterfaceManager() {
		return null;
	}	
	
	@Override
	public void setAllwaysFocused(OrGuiComponent comp) {
	}
}
