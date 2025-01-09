package kz.tamur.rt.adapters;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JLabel;
import javax.swing.JProgressBar;

import kz.tamur.comps.Factories;
import kz.tamur.comps.Factory;
import kz.tamur.comps.FactoryListener;
import kz.tamur.comps.Mode;
import kz.tamur.comps.OrButton;
import kz.tamur.comps.OrCheckBox;
import kz.tamur.comps.OrCheckColumn;
import kz.tamur.comps.OrComboBox;
import kz.tamur.comps.OrComboColumn;
import kz.tamur.comps.OrCoolDateField;
import kz.tamur.comps.OrDateColumn;
import kz.tamur.comps.OrDateField;
import kz.tamur.comps.OrDocField;
import kz.tamur.comps.OrDocFieldColumn;
import kz.tamur.comps.OrFloatColumn;
import kz.tamur.comps.OrFloatField;
import kz.tamur.comps.OrFrame;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.OrHyperColumn;
import kz.tamur.comps.OrHyperLabel;
import kz.tamur.comps.OrHyperPopup;
import kz.tamur.comps.OrImage;
import kz.tamur.comps.OrImageColumn;
import kz.tamur.comps.OrImagePanel;
import kz.tamur.comps.OrIntColumn;
import kz.tamur.comps.OrIntField;
import kz.tamur.comps.OrMap;
import kz.tamur.comps.OrMemoColumn;
import kz.tamur.comps.OrMemoField;
import kz.tamur.comps.OrRichTextEditor;
import kz.tamur.comps.OrPanel;
import kz.tamur.comps.OrPasswordField;
import kz.tamur.comps.OrPopupColumn;
import kz.tamur.comps.OrRadioBox;
import kz.tamur.comps.OrSequenceField;
import kz.tamur.comps.OrTable;
import kz.tamur.comps.OrTextColumn;
import kz.tamur.comps.OrTextField;
import kz.tamur.comps.OrTreeColumn;
import kz.tamur.comps.OrTreeControl2;
import kz.tamur.comps.OrTreeCtrl;
import kz.tamur.comps.OrTreeField;
import kz.tamur.comps.OrTreeTable;
import kz.tamur.guidesigner.reports.ReportPrinter;
import kz.tamur.guidesigner.reports.ReportRecord;
import kz.tamur.rt.CheckContext;
import kz.tamur.rt.InterfaceManager;
import kz.tamur.rt.data.Cache;
import kz.tamur.util.MapMap;

import org.jdom.CDATA;
import org.jdom.Element;
import org.jdom.Text;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 13.04.2004
 * Time: 15:21:04
 * To change this template use File | Settings | File Templates.
 */
public class UIFrame implements OrFrame {

    private static Pattern uidPtn = Pattern.compile("\\$Objects\\s*\\.\\s*getObject\\s*\\(\\s*\"([^\"]+)\"");

    private Map<String, OrRef> refs = new HashMap<String, OrRef>();
    private KrnObject dataLang;
    private KrnObject lang;
    private PanelAdapter adapter;
    private MapMap strings = new MapMap();

    private Map<String, OrRef> contentRefs = new HashMap<String, OrRef>();
    private Cache ownCache = new Cache(Kernel.instance());
    private Cache cache;

    private Map<Integer, List<CheckContext>> refGroups = new HashMap<Integer, List<CheckContext>>();

    private int evaluationMode = 0;
    private long flowId = 0;

    private KrnObject obj;
    private String title = "";
    private List<ReportPrinter> reports_ = new ArrayList<ReportPrinter>();

    private Stack<List<ComponentAdapter>> adaptersStack =
    	new Stack<List<ComponentAdapter>>();
    private ResourceBundle res;
    private ReportRecord rootReport;
    private boolean loaded = false;

	private UIFrame oldFrm;
	private InterfaceManager mgr;

	private OrGuiComponent allwaysFocusedComponent;
  	private KrnObject objs[] = null;
    private int userDecision = -1;

    public UIFrame(InterfaceManager mgr, KrnObject obj, UIFrame oldFrm, JProgressBar progress, JLabel label,KrnObject lang) {
    	this.mgr = mgr;
        this.obj = obj;
        this.oldFrm = oldFrm;
        this.lang=lang;
        try {
            if (obj.id != 0) {
                label.setText("Открытие интерфейса:");
                load(progress);
                label.setText("");
            } else
            	loaded = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public String getUid() {
    	return obj.uid;
    }

    public void setTransactionId(long id) {
    	if (cache != null) {
    		cache.setTransactionId(id);
    	} else {
    		ownCache.setTransactionId(id);
    	}
    }

    public long getTransactionId() {
    	if (cache != null) {
    		return cache.getTransactionId();
    	} else {
    		return ownCache.getTransactionId();
    	}
    }
    public void setEvaluationMode(int mode) {
        this.evaluationMode = mode;
    }

    public void setFlowId(long flowId) {
        this.flowId = flowId;
    }

    public long getFlowId() {
        return flowId;
    }

    public Map<String, OrRef> getRefs() {
        return refs;
    }

    public Map<String, OrRef> getContentRef() {
        return contentRefs;
    }

    public Cache getCash() {
        return oldFrm != null ? oldFrm.getCash() : cache != null ? cache : ownCache;
    }
    
    public boolean isSharedCache() {
    	return cache != null;
    }
    
    public void setCache(Cache cache) {
    	if (this.cache != cache) {
            OrRef ref = getRef();
            if(ref != null){
                ref.cacheChanged(this.cache, cache);
            }
        	this.cache = cache;
        	this.oldFrm = null;
    	}
    }

    public OrRef getRef() {
        return (adapter != null) ? adapter.getDataRef() : null;
    }

    public OrPanel getPanel() {
        return (adapter != null) ? adapter.getPanel() : null;
    }

    public int getEvaluationMode() {
        return evaluationMode;
    }

    private void load(JProgressBar progress) throws Exception {
        final Factory fs = Factories.instance();
        Builder b = new Builder(progress);
        try {
            loadMessages();
            final Kernel krn = Kernel.instance();
            reports_.clear();
            rootReport = null;
            fs.addFactoryListener(b);
            byte[] data = krn.getBlob(obj, "config", 0, 0, 0);
            InputStream is = new ByteArrayInputStream(data);
            SAXBuilder builder = new SAXBuilder();
            Element xml = builder.build(is).getRootElement();
            progress.setMinimum(0);
            progress.setValue(0);
            progress.setMaximum(kz.tamur.rt.Utils.getChildrenCount(xml));
            
            // Находим все ссылки на объекты и загружаем их одним запросом к СП
            Set<String> uids = new HashSet<String>();
            fillCache("//KrnObject", xml, uids);
            fillCache("//Report", xml, uids);
            fillCache("//Filter", xml, uids);
            
            // Находим все ссылки на объекты в формулах
            String confStr = new String(data, "UTF-8");
            Matcher m = uidPtn.matcher(confStr);
            while (m.find()) {
            	uids.add(m.group(1));
            }
            krn.addToCache(uids);
            
            fs.create(xml, Mode.RUNTIME, this);
            adapter = b.getCurrentPanel();
            
            ReportRecord root = getRootReport();
            if (root != null && root.getChildren().size() > 0) {
                loadReports(root, this);
            }
            if(adapter!=null && adapter.getPanel()!=null && lang!=null)
            	adapter.getPanel().setLangId(lang.id);
            loaded = true;
        } finally {
            fs.removeFactoryListener(b);
            progress.setValue(0);
        }
    }
    
    private void fillCache(String xpath, Element xml, Set<String> uids) throws Exception {
        XPath xp = XPath.newInstance(xpath);
        List<Element> objElems = xp.selectNodes(xml);
        for (Element objElem : objElems) {
        	String id = objElem.getAttributeValue("id");
        	if (id != null && id.indexOf('.') != -1)
        		uids.add(id);
        }
    }

    private void loadReports(ReportRecord parent, UIFrame frame) {
        List<ReportRecord> records = parent.getChildren();
        for (ReportRecord record : records) {
            if (record.isFolder()) {
                loadReports(record, frame);
            } else {
                ReportPrinter rp = new ReportPrinterAdapter(frame,
                        frame.getPanel(), record);
            }
        }
    }

    private Map<String, Object> loadMessages() throws Exception {
    	if (lang != null) {
	        Long lid = new Long(lang.id);
	        Map<String, Object> msgs = strings.get(lid);
	        if (msgs == null) {
	            msgs = new HashMap<String, Object>();
	            strings.put(lid, msgs);
	            final Kernel krn = Kernel.instance();
	            byte[] strings = krn.getBlob(obj, "strings", 0, lang.id, 0);
	            if (strings.length > 0) {
	                ByteArrayInputStream is = new ByteArrayInputStream(strings);
	                SAXBuilder b = new SAXBuilder();
	                Element e = b.build(is).getRootElement();
	                List chs = e.getChildren();
	                for (int i = 0; i < chs.size(); i++) {
	                    Element ch = (Element) chs.get(i);
	                    String uid = ch.getAttributeValue("uid");
                            if (ch.getContentSize() > 0) {
                                for (int j=0; j<ch.getContentSize(); j++) {
                                    if (ch.getContent(j) instanceof CDATA) {
                                        String s = ((CDATA)ch.getContent(j)).getText();
                                        byte[] value = s.getBytes();
                                        msgs.put(uid, value);
                                    } else if (ch.getContent(j) instanceof Text) {
                                        String value = ch.getText();
                                        if (!"Безымянный".equals(value))
                                            msgs.put(uid, value);
                                    }
                                }
                            }
	                }
	            }
	        }
	        return msgs;
    	}
    	return Collections.EMPTY_MAP;
    }


    public String getTitle() {
        return title;
    }

    public int getTransactionIsolation() {
        return 0;
    }

    public void setInterfaceLang(KrnObject lang, boolean withReloading) {
        if (this.lang == null || this.lang.id != lang.id) {
            this.lang = lang;
            if (adapter != null) {
                adapter.getPanel().setLangId(lang.id);
            }
        }
    }

    public KrnObject getIfcLang() {
        return lang;
    }

    public KrnObject getDataLang() {
        return dataLang;
    }

    public void setDataLang(KrnObject lang, boolean refresh) {
        if (dataLang == null || dataLang.id != lang.id) {
            dataLang = lang;
            if (adapter != null) {
                try {
                    OrRef ref_=getRef();
                    if(ref_!=null){
                        ref_.setLangId(lang.id);
                        if (refresh) {
                            ref_.evaluate(this);
                        }
                    }
                    Map<String, OrRef> crefs = getContentRef();
                    for (Iterator<OrRef> it = crefs.values().iterator(); it.hasNext();) {
                        OrRef cref = it.next();
                        if (cref.getParent() == null) {
                                cref.setLangId(lang.id);
                                if (refresh && !cref.isHyperPopup()) {
                                    cref.setLoaded(false);
                                    cref.evaluate(this);
                                }
                        } else if (crefs.get(cref.getParent().toString()) == null) {
                                cref.setLangId(lang.id);
                                if (refresh && !cref.isHyperPopup()) {
                                    cref.setLoaded(false);
                                    cref.evaluate(cref, this, 0);
                                }
                        }
                    }
                    if (getRef() != null) {
                        OrRef ref = getRef();
                        Map<String, OrRef> refs = getRefs();
                        for (Iterator<OrRef> langIt = refs.values().iterator(); langIt.hasNext();) {
                            OrRef chRef = langIt.next();
                            if (chRef.getParent() == null && !chRef.toString().equals(ref.toString())) {
                                chRef.setLangId(lang.id);
                                if (refresh) {
                                    chRef.evaluate(this);
                                }
                            }
                        }
                    }
                } catch (KrnException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public ReportPrinter getReportPrinter(long id) {
    	for (ReportPrinter report : reports_) {
    		if (report.getId() == id) {
    			return report;
    		}
    	}
    	if (rootReport != null) {
	    	ReportRecord rec = findReportRecord(rootReport, id);
	    	if (rec != null) {
	            return new ReportPrinterAdapter(this, getPanel(), rec);
	    	}
    	}
		return null;
	}
    
    private ReportRecord findReportRecord(ReportRecord rec, long id) {
    	if (rec.getObjId() == id) {
    		return rec;
    	}
    	List<ReportRecord> children = rec.getChildren();
    	if (children != null) {
	    	for (ReportRecord child : children) {
	    		ReportRecord res = findReportRecord(child, id);
	    		if (res != null) {
	    			return res;
	    		}
	    	}
    	}
    	return null;
    }

	public void addReport(ReportPrinter report) {
        reports_.add(report);
    }

    public List<ReportPrinter> getReports() {
        return reports_;
    }

    public ReportRecord getRootReport() {
        return rootReport;
    }

    public void setRootReport(ReportRecord reportRecord) {
        if (rootReport == null)
            this.rootReport = reportRecord;
        else {
            for (int i = 0; i < reportRecord.getChildren().size(); i++) {
                this.rootReport.addChild(reportRecord.getChildren().get(i));
            }
        }
    }

    private class Builder implements FactoryListener {

        private PanelAdapter currentPanel;
        private List<ColumnAdapter> columnAdapters =
        	new ArrayList<ColumnAdapter>();
        private JProgressBar progress;

        public Builder(JProgressBar progress) {
            this.progress = progress;
        }

        public PanelAdapter getCurrentPanel() {
            return currentPanel;
        }

        public void componentCreated(OrGuiComponent c) {
            try {
                if (c instanceof OrPanel) {
                    OrPanel panel = (OrPanel)c;
                    PanelAdapter pa = (PanelAdapter)panel.getAdapter();
                    List<ComponentAdapter> l = adaptersStack.pop();
                    if (l != null && l.size() > 0) {
                        pa.setChildrenAdapters(l);
                    }
                    if (!adaptersStack.isEmpty()) {
                        adaptersStack.peek().add(pa);
                    }
                    currentPanel = pa;
                } else if (c instanceof kz.tamur.comps.OrTabbedPane) {
                    TabbedPaneAdapter tpa = new TabbedPaneAdapter(
                            UIFrame.this, (kz.tamur.comps.OrTabbedPane)c, false);
                    List<ComponentAdapter> l = adaptersStack.pop();
                    if (l != null && l.size() > 0) {
                        tpa.setChildrenAdapters(l);
                    }
                    if (!adaptersStack.isEmpty()) {
                        adaptersStack.peek().add(tpa);
                    }
                } else if (c instanceof kz.tamur.comps.OrSplitPane) {
                    SplitPaneAdapter spa = new SplitPaneAdapter(
                            UIFrame.this, (kz.tamur.comps.OrSplitPane)c, false);
                    List<ComponentAdapter> l = adaptersStack.pop();
                    if (l != null && l.size() > 0) {
                        spa.setChildrenAdapters(l);
                    }
                    if (!adaptersStack.isEmpty()) {
                        adaptersStack.peek().add(spa);
                    }
                } else if (c instanceof kz.tamur.comps.OrScrollPane) {
                    ScrollPaneAdapter spa = new ScrollPaneAdapter(
                            UIFrame.this, (kz.tamur.comps.OrScrollPane)c, false);
                    List<ComponentAdapter> l = adaptersStack.pop();
                    if (l != null && l.size() > 0) {
                        spa.setChildrenAdapters(l);
                    }
                    if (!adaptersStack.isEmpty()) {
                        adaptersStack.peek().add(spa);
                    }
                } else if (c instanceof kz.tamur.comps.OrLayoutPane) {
                    LayoutPaneAdapter spa = new LayoutPaneAdapter(
                            UIFrame.this, (kz.tamur.comps.OrLayoutPane)c, false);
                    List<ComponentAdapter> l = adaptersStack.pop();
                    if (l != null && l.size() > 0) {
                        spa.setChildrenAdapters(l);
                    }
                    if (!adaptersStack.isEmpty()) {
                        adaptersStack.peek().add(spa);
                    }
                } else if (c instanceof OrTextField) {
                    ComponentAdapter tfa = c.getAdapter();
                    adaptersStack.peek().add(tfa);
                } else if (c instanceof OrPasswordField) {
                    ComponentAdapter tfa = c.getAdapter();
                    adaptersStack.peek().add(tfa);
                } else if (c instanceof OrTextColumn) {
                    TextColumnAdapter a = new TextColumnAdapter(
                            UIFrame.this, (OrTextColumn)c);
                    columnAdapters.add(a);
                } else if (c instanceof OrTreeTable) {
                    TreeTableAdapter a = (TreeTableAdapter)((OrTreeTable)c).getAdapter();
                    adaptersStack.peek().add(a);
                    for (int i = 0; i < columnAdapters.size(); i++) {
                        ColumnAdapter ca = (ColumnAdapter)columnAdapters.get(i);
                        a.addColumnAdapter(ca);
                    }
                    ((OrTreeTable)c).setFooter();
                    columnAdapters.clear();
                } else if (c instanceof OrTable) {
                	TableAdapter a = (TableAdapter)((OrTable)c).getAdapter();
                    adaptersStack.peek().add(a);
                    for (int i = 0; i < columnAdapters.size(); i++) {
                        ColumnAdapter ca = (ColumnAdapter)columnAdapters.get(i);
                        a.addColumnAdapter(ca);
                    }
                    ((OrTable)c).setFooter();
                    columnAdapters.clear();
                } else if (c instanceof OrIntField) {
                    IntFieldAdapter ifa = new IntFieldAdapter(UIFrame.this, (OrIntField)c, false);
                    adaptersStack.peek().add(ifa);
                } else if (c instanceof OrIntColumn) {
                    IntColumnAdapter a = new IntColumnAdapter(
                            UIFrame.this, (OrIntColumn)c);
                    columnAdapters.add(a);
                } else if (c instanceof OrCheckBox) {
                    CheckBoxAdapter chba = new CheckBoxAdapter(UIFrame.this, (OrCheckBox)c, false);
                    adaptersStack.peek().add(chba);
                } else if (c instanceof OrCheckColumn) {
                    CheckBoxColumnAdapter a = new CheckBoxColumnAdapter(
                            UIFrame.this, (OrCheckColumn) c);
                    columnAdapters.add(a);
                } else if (c instanceof OrFloatField) {
                    FloatFieldAdapter ffa = new FloatFieldAdapter(UIFrame.this, (OrFloatField)c, false);
                    adaptersStack.peek().add(ffa);
                } else if (c instanceof OrFloatColumn) {
                    FloatColumnAdapter a = new FloatColumnAdapter(
                            UIFrame.this, (OrFloatColumn)c);
                    columnAdapters.add(a);
                } else if (c.getClass() == OrMemoField.class) {
                    ComponentAdapter mfa = ((OrMemoField)c).getAdapter();
                    adaptersStack.peek().add(mfa);
                } else if (c.getClass() == OrRichTextEditor.class) {
                    ComponentAdapter mfa = ((OrRichTextEditor)c).getAdapter();
                    adaptersStack.peek().add(mfa);
                } else if (c instanceof OrMemoColumn) {
                    MemoColumnAdapter a = new MemoColumnAdapter(
                            UIFrame.this, (OrMemoColumn) c);
                    columnAdapters.add(a);
                } else if (c instanceof OrDateField) {
                    ComponentAdapter dfa = ((OrDateField)c).getAdapter();
                    adaptersStack.peek().add(dfa);
                } else if (c instanceof OrCoolDateField) {
                    CoolDateFieldAdapter cdfa = new CoolDateFieldAdapter(UIFrame.this,
                            (OrCoolDateField)c, false);
                    adaptersStack.peek().add(cdfa);
                } else if (c instanceof OrDateColumn) {
                    DateColumnAdapter a = new DateColumnAdapter(
                            UIFrame.this, (OrDateColumn) c);
                    columnAdapters.add(a);
                } else if (c instanceof OrHyperPopup) {
                    HyperPopupAdapter hpa = new HyperPopupAdapter(UIFrame.this, (OrHyperPopup)c, false);
                    adaptersStack.peek().add(hpa);
                } else if (c instanceof OrPopupColumn) {
                    PopupColumnAdapter a = new PopupColumnAdapter(
                            UIFrame.this, (OrPopupColumn)c);
                    columnAdapters.add(a);
                } else if (c instanceof OrHyperLabel) {
                    HyperLabelAdapter hla = new HyperLabelAdapter(UIFrame.this, (OrHyperLabel)c, false);
                    adaptersStack.peek().add(hla);
                } else if (c instanceof OrHyperColumn) {
                    HyperColumnAdapter a = new HyperColumnAdapter(
                            UIFrame.this, (OrHyperColumn)c);
                    columnAdapters.add(a);
                }  else if (c instanceof OrImageColumn) {
                    ImageColumnAdapter a = new ImageColumnAdapter(
                            UIFrame.this, (OrImageColumn)c);
                    columnAdapters.add(a);
                } else if (c instanceof OrTreeCtrl) {
                    TreeCtrlAdapter tca = new TreeCtrlAdapter(UIFrame.this, (OrTreeCtrl)c, false);
                    adaptersStack.peek().add(tca);
                } else if (c instanceof OrTreeControl2) {
                	ComponentAdapter a = c.getAdapter();
                    adaptersStack.peek().add(a);
                } else if (c instanceof OrTreeField) {
                    TreeFieldAdapter tfa = new TreeFieldAdapter(UIFrame.this, (OrTreeField)c, false);
                    adaptersStack.peek().add(tfa);
                } else if (c instanceof OrTreeColumn) {
                    TreeColumnAdapter a = new TreeColumnAdapter(
                            UIFrame.this, (OrTreeColumn) c);
                    columnAdapters.add(a);
                } else if (c instanceof OrComboBox) {
                    ComponentAdapter cba = c.getAdapter();
                    adaptersStack.peek().add(cba);
                } else if (c instanceof OrComboColumn) {
                    ComboColumnAdapter a = new ComboColumnAdapter(
                            UIFrame.this, (OrComboColumn)c);
                    columnAdapters.add(a);
                } else if (c instanceof OrRadioBox) {
                    RadioBoxAdapter rba = new RadioBoxAdapter(UIFrame.this, (OrRadioBox) c, false);
                    adaptersStack.peek().add(rba);
                } else if (c instanceof OrButton) {
                    ButtonAdapter ba = new ButtonAdapter(UIFrame.this, (OrButton) c, false);
                    adaptersStack.peek().add(ba);
                } else if (c instanceof OrImage) {
                    ImageAdapter ia = new ImageAdapter(UIFrame.this, (OrImage) c, false);
                    adaptersStack.peek().add(ia);
                } else if (c instanceof OrImagePanel) {
                    ImagePanelAdapter ia = new ImagePanelAdapter(UIFrame.this, (OrImagePanel) c, false);
                    adaptersStack.peek().add(ia);
                } else if (c instanceof OrDocField) {
                    DocFieldAdapter dfa = new DocFieldAdapter(UIFrame.this, (OrDocField) c, false);
                    adaptersStack.peek().add(dfa);
                } else if (c instanceof OrDocFieldColumn) {
                    DocFieldColumnAdapter a = new DocFieldColumnAdapter(
                            UIFrame.this, (OrDocFieldColumn)c);
                    columnAdapters.add(a);
                } else if (c instanceof OrSequenceField) {
                    SequenceFieldAdapter sfa = new SequenceFieldAdapter(UIFrame.this, (OrSequenceField)c, false);
                    adaptersStack.peek().add(sfa);
                } else if (c instanceof OrMap) {
                    MapAdapter ma = new MapAdapter(UIFrame.this, (OrMap)c, false);
                    adaptersStack.peek().add(ma);
                }
                progress.setValue(progress.getValue() + 1);
            } catch (Exception e) {
                progress.setValue(0);
                e.printStackTrace();
            }
        }

        public void componentCreating(String className) {
            if ("Panel".equals(className) || "TabbedPane".equals(className) ||
                    "SplitPane".equals(className) || "ScrollPane".equals(className) || "LayoutPane".equals(className)) {
                adaptersStack.push(new ArrayList<ComponentAdapter>());
            }
        }

    }

    public List<CheckContext> getRefGroups(int group) {
        List<CheckContext> refList = refGroups.get(new Integer(group));
        return refList;
    }

    public void addRefGroup(int group, CheckContext context) {
        Integer key = new Integer(group);
        List<CheckContext> contextList = refGroups.get(key);
        if (contextList == null) {
            contextList = new ArrayList<CheckContext>();
            refGroups.put(key, contextList);
        }
        contextList.add(context);
    }

    public PanelAdapter getPanelAdapter() {
        return adapter;
    }

    public KrnObject getInterfaceLang() {
        return lang;
    }
    
    public void setInterfaceLang(KrnObject lang) {
    	setInterfaceLang(lang, false);
    }

    public void setInterfaceLang(KrnObject lang, ResourceBundle res) {
    	setInterfaceLang(lang, res, false);
    }

    public void setInterfaceLang(KrnObject lang, ResourceBundle res, boolean withReloading) {
        this.res = res;
    	setInterfaceLang(lang, withReloading);
    }

    public ResourceBundle getResourceBundle() {
        return res;
    }

    public String getNextUid() {
        return null;
    }

    public String getString(String uid) {
        return getString(uid, "");
    }

    public String getString(String uid, String defStr) {
        if (uid == null || uid.length() == 0) return defStr;
        try {
            Object res = loadMessages().get(uid);
            String res_m = (res != null)? 
            		(res instanceof String)? 
            				(String)res :(res instanceof byte[])? 
            						(new String((byte[])res)): defStr:"";
            return res_m;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public byte[] getBytes(String uid) {
        if (uid == null || uid.length() == 0) return null;
        try {
            Object str = loadMessages().get(uid);
            return (str instanceof byte[]) ? (byte[])str : null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setString(String uid, String str) {

    }

    public void setBytes(String s, byte[] bytes) {
    }

    public void clear() {
        OrRef ref = getRef();
        if(ref != null){
            ref.clear();
        }
        ownCache.clear();
    }

	public InterfaceManager getInterfaceManager() {
		return mgr;
	}

	public boolean isLoaded() {
		return loaded;
	}

	public OrGuiComponent getAllwaysFocusedComponent() {
		return allwaysFocusedComponent;
	}

	@Override
	public void setAllwaysFocused(OrGuiComponent comp) {
		this.allwaysFocusedComponent = comp;
	}

    /**
     * @return the obj
     */
    public KrnObject getObj() {
        return obj;
    }
    
    public KrnObject[] getInitialObjs() {
		return objs;
	}

	public void setInitialObjs(KrnObject[] objs) {
		this.objs = objs;
	}
	
	public void setUserDecision(int userDecision) {
		this.userDecision = userDecision;
	}
	
	public int getUserDecision() {
		return userDecision;
	}
}