package kz.tamur.web.component;

import static kz.tamur.comps.Constants.ACT_ERR;
import static kz.tamur.comps.Constants.ACT_PERMIT;
import static kz.tamur.guidesigner.ButtonsFactory.BUTTON_OK;
import static kz.tamur.web.common.ServletUtilities.EOL;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Stack;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.jdom.CDATA;
import org.jdom.Content;
import org.jdom.Element;
import org.jdom.Text;
import org.jdom.input.SAXBuilder;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.gui.DataCashListener;
import com.cifs.or2.kernel.Activity;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnDate;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.SystemNote;
import com.cifs.or2.kernel.UserSessionValue;
import com.cifs.or2.server.Session;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import kz.gov.pki.kalkan.util.encoders.Base64;
import kz.gov.pki.kalkan.util.encoders.Hex;
import kz.tamur.comps.Constants;
import kz.tamur.comps.FactoryListener;
import kz.tamur.comps.Mode;
import kz.tamur.comps.OrFrame;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.OrGuiContainer;
import kz.tamur.comps.PropertyHelper;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.guidesigner.reports.ReportPrinter;
import kz.tamur.guidesigner.reports.ReportRecord;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.or3.client.comps.interfaces.OrPanelComponent;
import kz.tamur.or3.client.comps.interfaces.OrTableComponent;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.rt.CheckContext;
import kz.tamur.rt.InterfaceManager;
import kz.tamur.rt.InterfaceManager.CommitResult;
import kz.tamur.rt.adapters.ComponentAdapter;
import kz.tamur.rt.adapters.ContainerAdapter;
import kz.tamur.rt.adapters.OrCalcRef;
import kz.tamur.rt.adapters.OrRef;
import kz.tamur.rt.adapters.PanelAdapter;
import kz.tamur.rt.adapters.ReportPrinterAdapter;
import kz.tamur.rt.adapters.Util;
import kz.tamur.rt.data.Cache;
import kz.tamur.rt.data.CashChangeListener;
import kz.tamur.rt.orlang.ClientOrLang;
import kz.tamur.util.Funcs;
import kz.tamur.util.Pair;
import kz.tamur.util.ReqMsgsList;
import kz.tamur.util.ThreadLocalDateFormat;
import kz.tamur.util.crypto.CheckSignResult;
import kz.tamur.web.common.LangHelper;
import kz.tamur.web.common.MetadataChangeAdapter;
import kz.tamur.web.common.ProcessListener;
import kz.tamur.web.common.ViewHelper;
import kz.tamur.web.common.WebSession;
import kz.tamur.web.common.WebSessionManager;
import kz.tamur.web.common.webgui.WebComponent;
import kz.tamur.web.common.webgui.WebMenu;
import kz.tamur.web.common.webgui.WebMenuItem;
import kz.tamur.web.common.webgui.WebPanel;
import kz.tamur.web.common.webgui.WebPopupMenu;
import kz.tamur.web.controller.WebController;

public class WebFrame implements OrFrame, ProcessListener {
	
	private Log log;
	private KrnObject obj;
	private WebSession session;

	private Map<String, OrRef> refs = new HashMap<String, OrRef>();
	private KrnObject dataLang;
	private KrnObject lang;
	private PanelAdapter adapter;
	private static Map<Integer,Map<String, Map<String, Object>>> strings = new HashMap<Integer,Map<String, Map<String, Object>>>();
	private static Map<Integer,Map<Long, Element>> configs = new HashMap<Integer,Map<Long, Element>>();

	private Map<String, OrRef> contentRefs = new HashMap<String, OrRef>();
	private Cache ownCache;
	private Cache cache;

	private Map<Integer, List<CheckContext>> refGroups = new HashMap<Integer, List<CheckContext>>();

	private int evaluationMode = 0;
	private long flowId = 0;

	private String title = "";
	private List<ReportPrinter> reports_ = new ArrayList<ReportPrinter>();

	private Stack<List<ComponentAdapter>> adaptersStack = new Stack<List<ComponentAdapter>>();
	
	private int commitRes = -1;
	private int optionRes = -2;
	private int commitNeedAction = -1;
	private ReqMsgsList msgList;
	
	private ReportRecord rootReport;
	private WebMenu docMenu;
	private int promptAction = -1;
	private String promptRes = null, confirmMessage = null;
	private int confirm = -1;

	private String additionalResponseXml = "";
	private WebFrame oldFrm;

	private String signValue = null;
	private String signMessage = null;
	private String signType = null;
	private boolean needDialog = false;
	private WebFrame parentFrame;
	private String dialogTitle;
	private int dialogWidth;
	private int dialogHeight;
	private boolean dialogHasClearButton;
	private int dialogResult = -1;
	
	private int option = -2;
	private Activity optionActivity;
	private String operation;
	private List<String> operationParams = new ArrayList<String>();
	private String operationResult;
	int configNumber;
	private boolean showAlarm = false;
	private String titleAlarm = null;
        
  	private Map<String, WebComponent> componentMap = new HashMap<String, WebComponent>();
  	private OrGuiComponent selectedComponent;
  	
  	private KrnObject objs[] = null;
  	private int userDecision = -1;
	private List<DataCashListener> cacheListeners = new ArrayList<DataCashListener>();
	private Map<Long, List<CashChangeListener>> cacheChangeListeners = new HashMap<Long, List<CashChangeListener>>();
        
	private Map<String, Object> idCardData = null;

	private String ucgoRequest = null;
	private String ucgoResponse = null;
	
	private String wsRequest = null;
	private String wsResponse = null;

	private String signedData = null;
	private X509Certificate cert = null;
	private CheckSignResult checkSignResult = null;
	private boolean isOkSignedData = false;
	
	private File file = null;
	private String[] data = null;
	
	private ExecutorService exec;
	
	public WebFrame(KrnObject obj, WebFrame oldFrm, WebSession session) {
        this.exec = Executors.newFixedThreadPool(1);

		this.obj = obj;
		this.session = session;
		configNumber = session.getConfigNumber();
		this.oldFrm = oldFrm;
        this.log = WebSessionManager.getLog(session.getKernel().getUserSession().dsName, session.getKernel().getUserSession().logName);

		ownCache = new Cache(session.getKernel(), session);
		lang = session.getKernel().getInterfaceLanguage();

	}

    public long getInterfaceId() {
        return obj.id;
    }
	
    public String getInterfaceUid() {
        return obj.uid;
    }

    public boolean load() {
    	return load(this);
    }
    
    public boolean load(WebFrame parentFrm) {
		final WebFactory fs = new WebFactory(obj);
		Builder b = new Builder();
		boolean res = false;
		try {
			final Kernel krn = session.getKernel();

			loadMessages(lang, obj, krn,session.getConfigNumber());
			reports_.clear();
			rootReport = null;
			fs.addFactoryListener(b);

			/*
			 * byte[] data = krn.getBlob(obj, "config", 0, 0, 0); InputStream is
			 * = new ByteArrayInputStream(data); SAXBuilder builder = new
			 * SAXBuilder(); Element xml = builder.build(is).getRootElement();
			 * is.close();
			 */
			Element xml = loadXml(obj, krn, session.getConfigNumber());

			fs.create(xml, Mode.RUNTIME, parentFrm);
			adapter = b.getCurrentPanel();

			preloadReportRefs();

			res = true;
		} catch (Exception e) {
			log.error("|USER: " + session.getUserName()
					+ "| load interface id=" + obj.id + " failed!");
			log.error(e, e);
		} finally {
	        setSelectedComponent(null);
			fs.removeFactoryListener(b);
		}
		return res;
	}

    public void preloadReportRefs() {
    	try{
			if (rootReport != null) {
				preloadReportRefs(rootReport);
			}
    	} catch (Exception e) {
    		log.error("|USER: " + session.getUserName()
    		+ "| load interface id=" + obj.id + " failed!");
    		log.error(e, e);
    	}
    }

    public void loadReports() {
    	try{
			if (rootReport != null) {
				docMenu = new WebMenu("", "", null, Mode.RUNTIME, this, null);
				//docMenu.setX(WebController.MENU_X);
				//docMenu.setY(0);
				//docMenu.setHeight(WebController.MENU_HEIGHT);
				docMenu.setId("0");
				loadReports(rootReport, docMenu);
			}
    	} catch (Exception e) {
    		log.error("|USER: " + session.getUserName()
    		+ "| load interface id=" + obj.id + " failed!");
    		log.error(e, e);
    	}
    }
    
	public static void reloadInterface(long id, int configNumber) {
		synchronized (configs) {
		    Map<Long, Element> conf = configs.get(configNumber);
			if (conf.containsKey(id)) {
			    conf.remove(id);
			}
		}
		synchronized (strings) {
		    Map<String, Map<String, Object>> strings_ = strings.get(configNumber);
			String key = id + "," + LangHelper.getRusLang(configNumber).obj.id;
			if (strings_ != null && strings_.keySet().contains(key))
			    strings_.remove(key);
			key = id + "," + LangHelper.getKazLang(configNumber).obj.id;
			if (strings_ != null && strings_.keySet().contains(key))
			    strings_.remove(key);
		}
		PropertyHelper.clearPropertyValues(configNumber);
	}

    private static Element loadXml(KrnObject obj, Kernel krn,Integer configNumber) throws Exception {
        Map<Long, Element> conf = null;
        synchronized (configs) {
            conf = configs.get(configNumber);
            if (conf == null) {
                conf = new HashMap<Long, Element>();
                configs.put(configNumber, conf);
    			Session.addMetadataChangeListener(new MetadataChangeAdapter(configNumber));
            }
        }
        
        synchronized (conf) {
            Element xml = conf.get(obj.id);
            if (xml == null) {
                byte[] data = krn.getBlob(obj, "config", 0, 0, 0);
                InputStream is = new ByteArrayInputStream(data);
                SAXBuilder builder = new SAXBuilder();
                xml = builder.build(is).getRootElement();
                is.close();
                conf.put(obj.id, xml);
            }
            return xml;
        }
    }

	private static Map<String, Object> loadMessages(KrnObject lang,
			KrnObject obj, Kernel krn,Integer configNumber) throws Exception {
		if (lang != null) {
			Long lid = lang.id;

			Map<String, Map<String, Object>> strings_ = null;
			synchronized (strings) {
			    strings_ = strings.get(configNumber);
			    if (strings_ == null) {
			        strings_ = new HashMap<String,Map<String, Object>>();
			        strings.put(configNumber, strings_);    
			    }
			}
			
			synchronized (strings_) {
			    String key = new StringBuilder().append(obj.id).append(",").append(lid).toString();
				Map<String, Object> msgs = strings_.get(key);
				if (msgs == null) {
					msgs = new HashMap<String, Object>();
					strings_.put(key, msgs);
					byte[] strings = krn.getBlob(obj, "strings", 0, lang.id, 0);
					if (strings.length > 0) {
						ByteArrayInputStream is = new ByteArrayInputStream(
								strings);
						SAXBuilder b = new SAXBuilder();
						Element e = b.build(is).getRootElement();
						is.close();
						int size = e.getContentSize();
						for (int i = 0; i < size; i++) {
							Content o = e.getContent(i);
							if (o instanceof Element) {
								Element ch = (Element) o;
								int jsize = ch.getContentSize();
								if (jsize > 0) {
									String uid = ch.getAttributeValue("uid");
									for (int j = 0; j < jsize; j++) {
										Content v = ch.getContent(j);
										if (v instanceof CDATA) {
											String s = ((CDATA) v).getText();
											byte[] value = s.getBytes();
											msgs.put(uid, value);
										} else if (v instanceof Text) {
											String value = ((Text) v).getText();
											if (!"Безымянный".equals(value))
												msgs.put(uid, value);
										}
									}
								}
							}
						}
					}
				}
				return msgs;
			}
		}
		return Collections.emptyMap();
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

	public void clear() {
		OrRef ref = getRef();
		if (ref != null) {
			ref.clear();
			ref.setLoaded(false);
		}
        Map<String, OrRef> contents = getContentRef();
        for (Iterator<OrRef> refIt = contents.values().iterator(); refIt.hasNext();) {
            ref = refIt.next();
            if (ref.getParent() == null) {
                ref.clear();
    			ref.setLoaded(false);
            }
        }

		ownCache.clear();
	}

	public void setCache(Cache cache) {
		if (this.cache != cache) {
			if (oldFrm == null || oldFrm.getCash() != cache) {
				if (this.cache != null) {
					for (DataCashListener l : cacheListeners)
						this.cache.removeCashListener(l);
					for (Long attrId : cacheChangeListeners.keySet()) {
						List<CashChangeListener> list = cacheChangeListeners.get(attrId);
						for (CashChangeListener l : list)
							this.cache.removeCashChangeListener(l);
					}
				}
				if (cache != null) {
					for (DataCashListener l : cacheListeners)
						cache.addCashListener(l, null);
					for (Long attrId : cacheChangeListeners.keySet()) {
						List<CashChangeListener> list = cacheChangeListeners.get(attrId);
						for (CashChangeListener l : list)
							cache.addCashChangeListener(attrId, l, null);
					}
				}
/*				OrRef ref = getRef();
				if (ref != null) {
					ref.cacheChanged(this.cache, cache);
				}
*/			}
			this.cache = cache;
			this.oldFrm = null;
		}
	}

	public void setEvaluationMode(int mode) {
		this.evaluationMode = mode;
	}

	public boolean isSharedCache() {
		return cache != null;
	}

    public KrnObject[] getInitialObjs() {
		return objs;
	}

	public void setInitialObjs(KrnObject[] objs) {
		this.objs = objs;
	}

	private class Builder implements FactoryListener {

        private PanelAdapter currentPanel;

        private WebComponent firstComponent = null;

        public PanelAdapter getCurrentPanel() {
            return currentPanel;
        }

        public void componentCreated(OrGuiComponent c) {
            try {
                if (c instanceof WebComponent && firstComponent == null && ((WebComponent) c).isFocusable()) {
                    firstComponent = (WebComponent) c;
                }
                if (!(c instanceof OrTableComponent) && c instanceof OrGuiContainer) {
                    if (c instanceof OrWebPanel) {
                        currentPanel = (PanelAdapter) c.getAdapter();
                    }
                    if (!adaptersStack.isEmpty()) {
                        ContainerAdapter spa = (ContainerAdapter) c.getAdapter();
                        List<ComponentAdapter> l = adaptersStack.pop();
                        if (l != null && l.size() > 0) {
                            spa.setChildrenAdapters(l);
                        }
                    }
                }
                if (!adaptersStack.isEmpty() && c.getAdapter() != null) {
                    adaptersStack.peek().add(c.getAdapter());
                }
            } catch (Exception e) {
                log.error(e, e);
            }
        }

        public void componentCreating(String className) {
            if ("Panel".equals(className) || "TabbedPane".equals(className) || "SplitPane".equals(className)
                    || "ScrollPane".equals(className)) {
                adaptersStack.push(new ArrayList<ComponentAdapter>());
            }
        }

    }

	// implementing OrFrame
	public KrnObject getInterfaceLang() {
		return lang;
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
			try {
				OrRef ref = getRef();
				if (ref != null) {
					ref.setLangId(lang.id);
					if (refresh) {
						ref.evaluate(this);
					}
				}
				Map<String, OrRef> refs = getRefs();
				for (Iterator<OrRef> it = refs.values().iterator(); it.hasNext();) {
					OrRef chRef = it.next();
					if (chRef.getParent() == null
							&& (ref == null || !chRef.toString().equals(ref.toString()))) {
						chRef.setLangId(lang.id);
						if (refresh) {
							chRef.evaluate(this);
						}
					}
				}
			} catch (KrnException e) {
				log.error(e, e);
			}
		}
	}

	public void setInterfaceLang(KrnObject lang) {
		setInterfaceLang(lang, false);
	}

	public void setInterfaceLang(KrnObject lang, ResourceBundle res) {
		setInterfaceLang(lang);
	}

	public ResourceBundle getResourceBundle() {
		return session.getResource();
	}

	public void setInterfaceLang(KrnObject lang, boolean withReloading) {
		if (this.lang == null || this.lang.id != lang.id) {
			this.lang = lang;
			if (adapter != null) {
				adapter.getPanel().setLangId(lang.id);
				if (docMenu != null)
					changePrinterTitles(docMenu);
				try {
					Map<String, OrRef> crefs = getContentRef();
					for (Iterator<OrRef> it = crefs.values().iterator(); it
							.hasNext();) {
						OrRef cref = it.next();
						if (cref.getParent() == null) {
							cref.setLangId(lang.id);
							if (withReloading && !cref.isHyperPopup()) {
								cref.setLoaded(false);
								cref.evaluate(this);
							}
						} else if (crefs.get(cref.getParent().toString()) == null) {
							cref.setLangId(lang.id);
							if (withReloading && !cref.isHyperPopup()) {
								cref.setLoaded(false);
								cref.evaluate(cref, this, 0);
							}
						}
					}
				} catch (KrnException e) {
					log.error(e, e);
				}
			}
		}
	}

	public String getNextUid() {
		return null;
	}

	public String getString(String uid) {
		return getString(uid, "");
	}

	public String getString(String uid, String defStr) {
		if (uid == null || uid.length() == 0)
			return defStr;
		try {
			Object res = loadMessages(lang, obj, session.getKernel(),session.getConfigNumber()).get(uid);
			if(res instanceof String)
				return (String)res;
			else if(res instanceof byte[])
				return new String((byte[])res);
			else
				return defStr;
		} catch (Exception e) {
			log.error(e, e);
		}
		return "";
	}

	public byte[] getBytes(String uid) {
		if (uid == null || uid.length() == 0)
			return null;
		try {
			Object str = loadMessages(lang, obj, session.getKernel(),session.getConfigNumber()).get(uid);
			return (str instanceof byte[]) ? (byte[]) str : null;
		} catch (Exception e) {
			log.error(e, e);
		}
		return null;
	}

	public void setString(String uid, String str) {
	}

	public void setBytes(String s, byte[] bytes) {
	}

    public Map<String, OrRef> getRefs() {
		return refs;
	}

	public void setRefs(Map<String, OrRef> refs) {
		this.refs = refs;
	}

	public Map<String, OrRef> getContentRef() {
		return contentRefs;
	}

	public void setContentRef(Map<String, OrRef> contentRefs) {
		this.contentRefs = contentRefs;
	}

	public Cache getCash() {
		return oldFrm != null ? oldFrm.getCash() : cache != null ? cache
				: ownCache;
	}

	public List<CheckContext> getRefGroups(int group) {
		return refGroups.get(group);
	}

	public Map<Integer, List<CheckContext>> getRefGroups() {
		return refGroups;
	}

	public void setRefGroups(Map<Integer, List<CheckContext>> refGroups) {
		this.refGroups = refGroups;
	}

	public void addRefGroup(int group, CheckContext context) {
		List<CheckContext> contextList = refGroups.get(group);
		if (contextList == null) {
			contextList = new ArrayList<CheckContext>();
			refGroups.put(group, contextList);
		}
		contextList.add(context);
	}

	public int getTransactionIsolation() {
		return 0;
	}

	public OrRef getRef() {
		return (adapter != null) ? adapter.getDataRef() : null;
	}

	public OrPanelComponent getPanel() {
		return (adapter != null) ? adapter.getPanel() : null;
	}

	public void addReport(ReportPrinter report) {
		reports_.add(report);
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

	public int getEvaluationMode() {
		return evaluationMode;
	}

	public void setFlowId(long flowId) {
		this.flowId = flowId;
	}

	public long getFlowId() {
		return flowId;
	}

	// end of implementing OrFrame
	public String setSize(String id, long fid) {
		return ((OrWebPanel) adapter.getPanel()).setSizeAndLoad(id, fid);
	}

	public String setSize(String id, long fid, int row, int col) {
		return ((OrWebPanel) adapter.getPanel())
				.setSizeAndLoad(id, fid, row, col);
	}

	private StringBuilder mapToString(Map<String, String> args) {
		StringBuilder res = new StringBuilder();
		for (String key : args.keySet()) {
			res.append(key).append("=").append(args.get(key)).append(";");
		}
		return res;
	}

	public void rollback() {
		try {
			if (getRef() != null) {
				Map<String, OrRef> contents = getContentRef();
				for (Iterator<OrRef> langIt = contents.values().iterator(); langIt
						.hasNext();) {
					OrRef chRef = langIt.next();
					if (chRef.getParent() == null)
						try {
							chRef.rollback(this, getFlowId());
						} catch (KrnException e) {
							log.error(e, e);
						}
				}

				OrRef ref = getRef();
				Map<String, OrRef> refs = getRefs();
				for (Iterator<OrRef> langIt = refs.values().iterator(); langIt
						.hasNext();) {
					OrRef chRef = langIt.next();
					if (chRef.getParent() == null
							&& !chRef.toString().equals(ref.toString()))
						try {
							chRef.rollback(this, getFlowId());
						} catch (KrnException e) {
							log.error(e, e);
						}
				}

				getRef().rollback(this, getFlowId());
			}
			getPanelAdapter().clearFilterParam();

			doAfterOpen();
		} catch (KrnException ex) {
			log.error(ex, ex);
		}
	}

	public JsonObject canCommit() {
		try {
			if (getRef() != null
					&& getRef().getType() != null
					&& (getEvaluationMode() & InterfaceManager.READONLY_MODE) == 0) {
				ReqMsgsList msg = getRef().canCommit();
				setMessageList(msg);
				if (msg.getListSize() > 0) {
					JsonObject res = new JsonObject().add("result", "error").add("fatal", msg.hasFatalErrors() ? 1 : 0).add("errors", toJSON(msg));
		        	Pair<String, String> docFile = toFileMsg(msg);
		        	if (docFile != null)
		        		res.add("path", docFile.first).add("name", docFile.second);
		        	return res;
				} else {
					return commit(true);
				}
			} else if (getRef() == null && (getEvaluationMode() & InterfaceManager.READONLY_MODE) == 0) {
				boolean b = doAfterCommit(this);
			}
		} catch (KrnException ex) {
			log.error(ex, ex);
            return new JsonObject().add("result", "fatal");
		}
        return new JsonObject().add("result", "success");
	}

	public JsonArray toJSON(ReqMsgsList msg) {
		JsonArray errs = new JsonArray();
		
		if (msg != null && msg.getListSize() > 0) {
			for (int i = 0; i < msg.getListSize(); i++) {
				JsonObject obj = new JsonObject();
				
				ReqMsgsList.MsgListItem err = (ReqMsgsList.MsgListItem) msg
						.getElementAt(i);
				
				obj.add("type", err.getType());
				obj.add("msg", err.toString());
				obj.add("uuid", err.getUUID());
				
				errs.add(obj);
			}
		}
		return errs;
	}
	
	public Pair<String, String> toFileMsg(ReqMsgsList msg) {
		if (msg != null && msg.getListSize() > 0) {
			File docFile = null;
			try {
			   XWPFDocument document = new XWPFDocument();
	
		       docFile = Funcs.createTempFile("OZD", ".docx", WebController.WEB_DOCS_DIRECTORY);
			   session.deleteOnExit(docFile);

		       FileOutputStream out = new FileOutputStream(docFile);
	
			   XWPFParagraph paragraph = document.createParagraph();
	
			   XWPFRun paragraphRunOne = paragraph.createRun();
			   paragraphRunOne.setColor("FF0000");
			   for (int i = 0; i < msg.getListSize(); i++) {
					
				ReqMsgsList.MsgListItem err = (ReqMsgsList.MsgListItem) msg
							.getElementAt(i);
					if (err.getType() == 0) {
						paragraphRunOne.setText(err.toString());
						paragraphRunOne.addBreak();
					}
			   }
			   XWPFRun paragraphRunTwo = paragraph.createRun();
			   paragraphRunTwo.setColor("0000FF");
			   for (int i = 0; i < msg.getListSize(); i++) {
					
					ReqMsgsList.MsgListItem err = (ReqMsgsList.MsgListItem) msg
								.getElementAt(i);
					if (err.getType() == 1) {
						paragraphRunTwo.setText(err.toString());
						paragraphRunTwo.addBreak();
					}
			   }
			   XWPFRun paragraphRunThree = paragraph.createRun();
			   paragraphRunThree.setColor("B00000");
			   for (int i = 0; i < msg.getListSize(); i++) {
					
					ReqMsgsList.MsgListItem err = (ReqMsgsList.MsgListItem) msg
								.getElementAt(i);
					if (err.getType() == 2) {
						paragraphRunThree.setText(err.toString());
						paragraphRunThree.addBreak();
					}
			   }
			   document.write(out);
			   out.close();
			} catch (Exception e){
				log.error(e, e);
			}
			if (docFile != null && docFile.length() > 0) {
				String fileName = kz.tamur.web.common.Base64.encodeBytes(docFile.getName().getBytes());
				String refName = kz.tamur.web.common.Base64.encodeBytes("Ошибки заполнения данных.docx".getBytes());
				
				return new Pair<String, String>(fileName, refName);
			}
		}
		return null;
	}

	public String toHTML(ReqMsgsList msg) {
		StringBuilder sb = new StringBuilder("<table>");
		
		if (msg != null && msg.getListSize() > 0) {
			for (int i = 0; i < msg.getListSize(); i++) {
				ReqMsgsList.MsgListItem err = (ReqMsgsList.MsgListItem) msg
						.getElementAt(i);

				sb.append("<tr><td class='err").append(err.getType()).append("'>");
				sb.append(err.toString());
				sb.append("</td></tr>");
			}
		}
		sb.append("</table>");
		return sb.toString();
	}

	public String xmlBeforePrevCanCommitChanges() {
		StringBuilder res = new StringBuilder(30);
		res.append("<r>");
		try {
			if (getRef() != null
					&& getRef().getType() != null
					&& (getEvaluationMode() & InterfaceManager.READONLY_MODE) == 0) {
				ReqMsgsList msg = getRef().canCommit();
				if (msg.getListSize() > 0) {
					setMessageList(msg);
					res.append("<hasErrors>true</hasErrors>");
				} else {
					res.append("<hasErrors>false</hasErrors>");
				}
			}
		} catch (KrnException ex) {
			log.error(ex, ex);
		}
		res.append("</r>");
		return res.toString();
	}

    public boolean beforePrevCanCommitChanges() {
        try {
            if (getRef() != null && getRef().getType() != null && (getEvaluationMode() & InterfaceManager.READONLY_MODE) == 0) {
                ReqMsgsList msg = getRef().canCommit();
                setMessageList(msg);
                return msg.getListSize() <= 0;
            }
        } catch (KrnException ex) {
            log.error(ex, ex);
        }
        return true;
    }

    public void setMessageList(ReqMsgsList msg) {
        msgList = msg.getListSize() > 0 ? msg : null;
    }

    public ReqMsgsList getMessageList() {
        return msgList;
    }

    public CommitResult commitCurrent() throws KrnException {
        return commitCurrent(new String[] { session.getResource().getString("continue"), session.getResource().getString("save") }, null, true, false);
    }

    public CommitResult commitCurrent(String[] options) throws KrnException {
        return commitCurrent(options, null, true, true);
    }

    public CommitResult commitCurrent(String[] options, boolean[] refresh) throws KrnException {
        return commitCurrent(options, refresh, true, true);
    }

    public CommitResult commitCurrent(String[] options, boolean[] refresh, boolean check, boolean canIgnore) throws KrnException {
        CommitResult result = CommitResult.WITHOUT_ERRORS;
        if (getRef() != null && getRef().getType() != null &&
                (getEvaluationMode() & InterfaceManager.READONLY_MODE) == 0) {
            try {
            	int r = 1;
                ReqMsgsList msg = getRef().canCommit();
                if (check && msg.getListSize() > 0) {
                    r = getCommitAction(msg, canIgnore, options);
                }
                
                if (r == -1)
                	result = CommitResult.SESSION_REALESED;
                else if (check && r == BUTTON_OK) {
                	result = CommitResult.CONTINUE_EDIT;
                } else {
                	getCash().commit(getFlowId());
                    getRef().commitChanges(this);
                    boolean b = doAfterCommit(this);
                    if (refresh != null)
                    	refresh[0] = b;
                    if (getFlowId() > 0) {
                        if (check && msg.hasFatalErrors()) {
                            session.getKernel().setPermitPerform(getFlowId(), false);
                            session.getTaskHelper().setPermitPerform(getFlowId(), false);
                            result = CommitResult.WITH_FATAL_ERRORS;
                        } else {
                        	session.getKernel().setPermitPerform(getFlowId(), true);
                        	session.getTaskHelper().setPermitPerform(getFlowId(), true);
                            if (msg.getListSize() > 0)
                            	result = CommitResult.WITH_ERRORS;
                        }
                        List<OrRef.Item> a_sel = getRef().getSelectedItems();
                        KrnObject[] selObjs = new KrnObject[a_sel.size()];
                        for (int i = 0; i < a_sel.size(); i++) {
                            OrRef.Item item = a_sel.get(i);
                            selObjs[i] = (KrnObject) item.getCurrent();
                        }
                        Activity act_= session.getTaskHelper().getActivityById(getFlowId());
                        if(act_!=null){
                        	session.getKernel().setSelectedObjects(act_.flowId,act_.nodesId[0][act_.nodesId[0].length-1], selObjs);
                            session.getTaskHelper().taskReload(act_.flowId,act_.infUi.id>0 && act_.ui.id>0?2:act_.infUi.id>0?1:act_.ui.id>0?0:-1);
                        }
                    }
                }
            } catch (KrnException ex) {
                log.error(ex, ex);
                ((OrWebPanel)getPanel()).setErrorMessage("Ошибка при сохранении интерфейса!\r\n" + ex.getMessage(), false);
                session.getKernel().setPermitPerform(getFlowId(), false);
                session.getTaskHelper().setPermitPerform(getFlowId(), false);
                
                throw new KrnException(ex.code, "Ошибка при сохранении интерфейса!\r\n" + ex.getMessage());
            }
        }
        return result;
    }

    public boolean doAfterCommit(WebFrame frm) {
        OrWebPanel p = (OrWebPanel)frm.getPanel();
        ASTStart template = p.getAfterSaveTemplate();
        if (template != null) {
            ClientOrLang orlang = new ClientOrLang(frm);
            Map<String, Object> vc = new HashMap<String, Object>();
        	boolean calcOwner = OrCalcRef.setCalculations();
            try {
                orlang.evaluate(template, vc, frm.getPanelAdapter(), new Stack<String>());
                Object res = vc.get("RETURN");
                if (res instanceof Boolean)
                	return ((Boolean)res).booleanValue();
            } catch (Exception ex) {
                Util.showErrorMessage(p, ex.getMessage(), "Действие после сохранения");
            	log.error("Ошибка при выполнении формулы 'Действие после сохранения'" + p.getClass().getName() + "', uuid: " + p.getUUID());
                log.error(ex, ex);
            } finally {
                if (calcOwner)
                	OrCalcRef.makeCalculations();
            }
        }
        return false;
    }

    
    
    public void doAfterTaskListUpdate() {
        synchronized (session.getFrameManager().getFrames()) {
            for (WebFrame frm : session.getFrameManager().getFrames()) {
                if (frm.getObj().id != 0 && frm.getPanel() != null) {
                    OrWebPanel p = (OrWebPanel) frm.getPanel();
                    ASTStart template = p.getAfterTaskListUpdateTemplate();
                    if (template != null) {
                        ClientOrLang orlang = new ClientOrLang(frm);
                        Map<String, Object> vc = new HashMap<String, Object>();
                        boolean calcOwner = OrCalcRef.setCalculations();
                        try {
                            orlang.evaluate(template, vc, frm.getPanelAdapter(), new Stack<String>());
                        } catch (Exception ex) {
                            Util.showErrorMessage(p, ex.getMessage(), "Действие после обновления списка задач");
                        	log.error("Ошибка при выполнении формулы 'Действие после обновления списка задач'" + p.getClass().getName() + "', uuid: " + p.getUUID());
                            log.error(ex, ex);
	                    } finally {
	                        if (calcOwner)
	                        	OrCalcRef.makeCalculations();
                        }
                    }
                }
            }
        }
    }

    public void doOnNotification(SystemNote note) {
        synchronized (session.getFrameManager().getFrames()) {
            for (WebFrame frm : session.getFrameManager().getFrames()) {
                if (frm.getObj().id != 0) {
                    OrWebPanel p = (OrWebPanel) frm.getPanel();
                    ASTStart template = p.getOnNotificationTemplate();
                    if (template != null) {
                        ClientOrLang orlang = new ClientOrLang(frm);
                        Map<String, Object> vc = new HashMap<String, Object>();
                        vc.put("NOTIFICATION_DATA", note.data);
                        vc.put("NOTIFICATION_TYPE", note.type);
                        vc.put("NOTIFICATION_TIME", new KrnDate(note.time.getTime()));
                        if (note.from != null)
                            vc.put("NOTIFICATION_FROM", note.from.userObj);
                        boolean calcOwner = OrCalcRef.setCalculations();
                        try {
                            orlang.evaluate(template, vc, frm.getPanelAdapter(), new Stack<String>());
                        } catch (Exception ex) {
                            Util.showErrorMessage(p, ex.getMessage(), "Действие при получении уведомления");
                        	log.error("Ошибка при выполнении формулы 'Действие при получении уведомления'" + p.getClass().getName() + "', uuid: " + p.getUUID());
                            log.error(ex, ex);
	                    } finally {
	                        if (calcOwner)
	                        	OrCalcRef.makeCalculations();
                        }
                    }
                    getTemplateNotification(frm, p.getComponents(), note);
                }
            }
        }
        // Уведомить пользователя
        showAlarm(note.title);
    }

    public void getTemplateNotification(OrFrame frm, WebComponent[] comps, SystemNote note) {
        if (comps != null && comps.length > 0) {
            for (WebComponent comp : comps) {
                if (comp instanceof OrWebPanel) {
                    OrWebPanel p = (OrWebPanel) comp;
                    ASTStart template = p.getOnNotificationTemplate();
                    if (template != null) {
                        ClientOrLang orlang = new ClientOrLang(frm);
                        Map<String, Object> vc = new HashMap<String, Object>();
                        vc.put("NOTIFICATION_DATA", note.data);
                        vc.put("NOTIFICATION_TYPE", note.type);
                        vc.put("NOTIFICATION_TIME", new KrnDate(note.time.getTime()));
                        if (note.from != null)
                            vc.put("NOTIFICATION_FROM", note.from.userObj);
                        boolean calcOwner = OrCalcRef.setCalculations();
                        try {
                            orlang.evaluate(template, vc, p.getAdapter(), new Stack<String>());
                        } catch (Exception ex) {
                            Util.showErrorMessage(p, ex.getMessage(), "Действие при получении уведомления");
                        	log.error("Ошибка при выполнении формулы 'Действие при получении уведомления'" + p.getClass().getName() + "', uuid: " + p.getUUID());
                            log.error(ex, ex);
	                    } finally {
	                        if (calcOwner)
	                        	OrCalcRef.makeCalculations();
                        }
                    }
                    getTemplateNotification(frm, p.getComponents(), note);
                }else if (comp instanceof OrWebTabbedPane) {
                    getTemplateNotification(frm, ((OrWebTabbedPane)comp).getComponents(), note);
                }else if (comp instanceof OrWebSplitPane) {
                    getTemplateNotification(frm, ((OrWebSplitPane)comp).getComponents(), note);
                }
            }
        }
    } 
    
    
    public JsonObject commit(boolean selfCommit) {
        try {
            if (getRef() != null && getRef().getType() != null && (getEvaluationMode() & InterfaceManager.READONLY_MODE) == 0) {
            	
                int commitAction = 0;
                OrPanelComponent p = getPanel();
                ASTStart template = p.getBeforeCloseTemplate();
                if (template != null) {
                    ClientOrLang orlang = new ClientOrLang(this);
                    Map<String, Object> vc = new HashMap<String, Object>();
                	boolean calcOwner = OrCalcRef.setCalculations();
                    try {
                        orlang.evaluate(template, vc, getPanelAdapter(), new Stack<String>());
                    } catch (Exception ex) {
                        Util.showErrorMessage(p, ex.getMessage(), "Действие перед закрытием");
                    	log.error("Ошибка при выполнении формулы 'Действие перед закрытием'" + p.getClass().getName() + "', uuid: " + p.getUUID());
                        log.error(ex, ex);
                    } finally {
                        if (calcOwner)
                        	OrCalcRef.makeCalculations();
                        if (vc.get("RETURN") instanceof Number)
                        	commitAction = ((Number)vc.get("RETURN")).intValue();
                    }
                }
            	
                if (selfCommit || commitAction == 0) {
	                getCash().commit(getFlowId());
	                getRef().commitChanges(this);
	                boolean b = doAfterCommit(this);
	
	                if (getFlowId() > 0) {
	                    ReqMsgsList msg = msgList;
	                    if (msg != null && msg.hasFatalErrors()) {
	                        session.getKernel().setPermitPerform(getFlowId(), false);
	                        session.getTaskHelper().setPermitPerform(getFlowId(), false);
	                    } else {
	                        session.getKernel().setPermitPerform(getFlowId(), true);
	                        session.getTaskHelper().setPermitPerform(getFlowId(), true);
	                    }
	                    List<OrRef.Item> a_sel = getRef().getSelectedItems();
	                    KrnObject[] selObjs = new KrnObject[a_sel.size()];
	                    for (int i = 0; i < a_sel.size(); i++) {
	                        OrRef.Item item = a_sel.get(i);
	                        selObjs[i] = (KrnObject) item.getCurrent();
	                    }
	                    Activity act_ = session.getTaskHelper().getActivityById(getFlowId());
	                    if (act_ != null) {
	                        session.getKernel().setSelectedObjects(act_.flowId, act_.nodesId[0][act_.nodesId[0].length - 1], selObjs);
	                        session.getTaskHelper().taskReload(act_.flowId,
	                                act_.infUi.id > 0 && act_.ui.id > 0 ? 2 : act_.infUi.id > 0 ? 1 : act_.ui.id > 0 ? 0 : -1);
	                    }
	                }
                }
            }
            return new JsonObject().add("result", "success");
        } catch (KrnException ex) {
            log.error(ex, ex);
            ((WebPanel) getPanel()).setErrorMessage(Constants.ERROR_MESSAGE_1 + ex.getMessage(), false);
            return new JsonObject().add("result", "fatal");
        }
    }

    public void commit(int permit) {
        try {
            if (getRef() != null && getRef().getType() != null && (getEvaluationMode() & InterfaceManager.READONLY_MODE) == 0) {
            	
                int commitAction = 0;
            	if (permit == 1 || getFlowId() == 0) {
	                OrPanelComponent p = getPanel();
	                ASTStart template = p.getBeforeCloseTemplate();
	                if (template != null) {
	                    ClientOrLang orlang = new ClientOrLang(this);
	                    Map<String, Object> vc = new HashMap<String, Object>();
                    	boolean calcOwner = OrCalcRef.setCalculations();
	                    try {
	                        orlang.evaluate(template, vc, getPanelAdapter(), new Stack<String>());
	                    } catch (Exception ex) {
	                        Util.showErrorMessage(p, ex.getMessage(), "Действие перед закрытием");
	                    	log.error("Ошибка при выполнении формулы 'Действие перед закрытием'" + p.getClass().getName() + "', uuid: " + p.getUUID());
	                        log.error(ex, ex);
	                    } finally {
	                        if (calcOwner)
	                        	OrCalcRef.makeCalculations();
	                        if (vc.get("RETURN") instanceof Number)
	                        	commitAction = ((Number)vc.get("RETURN")).intValue();
	                    }
	                }
            	}
            	
                if (commitAction == 0) {
	                getCash().commit(getFlowId());
	                getRef().commitChanges(this);
	
	                if (getFlowId() > 0) {
	                    ReqMsgsList msg = msgList;
	                    if (permit == 1) {
	                        if (msg != null && msg.hasFatalErrors()) {
	                            session.getKernel().setPermitPerform(getFlowId(), false);
	                            session.getTaskHelper().setPermitPerform(getFlowId(), false);
	                        } else {
	                            session.getKernel().setPermitPerform(getFlowId(), true);
	                            session.getTaskHelper().setPermitPerform(getFlowId(), true);
	                        }
	                    }
	                    List<OrRef.Item> a_sel = getRef().getSelectedItems();
	                    KrnObject[] selObjs = new KrnObject[a_sel.size()];
	                    for (int i = 0; i < a_sel.size(); i++) {
	                        OrRef.Item item = a_sel.get(i);
	                        selObjs[i] = (KrnObject) item.getCurrent();
	                    }
	                    Activity act_ = session.getTaskHelper().getActivityById(getFlowId());
	                    if (act_ != null) {
	                        session.getKernel().setSelectedObjects(act_.flowId, act_.nodesId[0][act_.nodesId[0].length - 1], selObjs);
	                        session.getTaskHelper().taskReload(act_.flowId,
	                                act_.infUi.id > 0 && act_.ui.id > 0 ? 2 : act_.infUi.id > 0 ? 1 : act_.ui.id > 0 ? 0 : -1);
	                    }
	                }
                }
            }
        } catch (KrnException ex) {
            log.error(ex, ex);
            ((WebPanel) getPanel()).setErrorMessage(Constants.ERROR_MESSAGE_1 + ex.getMessage(), false);
        }
    }

    public String nextStep(String transitionId) {
        try {
            JsonObject obj = new JsonObject();
            Activity act = session.getTaskHelper().getActivityById(getFlowId());
            if (act != null) {
            	long oldId = act.ui.id;
            	long oldInfId = act.ui.id;
            	int oldPermit = (int) act.param & ACT_PERMIT;
                try {
                	session.getTaskHelper().disableActivity(act);

                    String[] res = session.getKernel().performActivitys(new Activity[] { act }, transitionId == null ? "" : transitionId);
                    if (res.length == 1 && res[0].equals("synch")) {
                        session.getTaskHelper().setAutoIfcFlowId_(act.flowId);
                    }
                    if (res.length > 0 && !res[0].equals("synch")) {
                        act.param |= ACT_ERR;
                        String msg = res[0];
                        for (int i = 1; i < res.length; ++i) {
                            msg += "<br>" + res[i];
                        }
                        session.getTaskHelper().reenableActivity(act, oldId, oldInfId, oldPermit);
                        obj.add("message", msg.replaceFirst("^\\!", ""));
                        obj.add("result", "success");
                    } else {
                        session.getTaskHelper().reenableActivity(act.flowId);
                        session.getTaskHelper().addAutoActivity(act);
                        obj.add("result", "success");
                        if (res.length == 1 && res[0].equals("synch")) {
                            session.getTaskHelper().reloadTask(act.flowId, 0, true, true);
                        }
                    }
                } catch (KrnException e) {
                    log.error(e, e);
                    session.getTaskHelper().reenableActivity(act, oldId, oldInfId, oldPermit);
                }
            } else {
                obj.add("message", "Не найден процесс с flowId = " + flowId);
                obj.add("result", "error");
            }
            return obj.toString();
        } catch (Exception ex) {
            log.error(ex, ex);
            ((WebPanel) getPanel()).setErrorMessage(Constants.ERROR_MESSAGE_1 + ex.getMessage(), false);
        }
        return "{\"result\":\"error\"}";
    }

    public String commitAuto() {
		try {
			if (getRef() != null
					&& getRef().getType() != null
					&& (getEvaluationMode() & InterfaceManager.READONLY_MODE) == 0) {
				getCash().commit(getFlowId());
				getRef().commitChanges(null);
				// 8.10.05 Vital
				if (getRef().getItems(getRef().getLangId()) != null
						&& getRef().getItems(getRef().getLangId()).size() != 0
						&& getRef().getSelectedItems().size() == 0) {
					getRef().setSelectedItems(new int[] { 0 });
				}
				//
				List<OrRef.Item> a_sel = getRef().getSelectedItems();
				if (a_sel.size() == 0) {
					return getResourceBundle().getString("checkObjectMessage");
				}
				KrnObject[] selObjs = new KrnObject[a_sel.size()];
				for (int i = 0; i < a_sel.size(); i++) {
					OrRef.Item item = a_sel.get(i);
					selObjs[i] = (KrnObject) item.getCurrent();
				}
				Activity auto_act = session.getTaskHelper().getAutoAct();

                if (auto_act != null) {
                    if (session.getKernel().setSelectedObjects(auto_act.flowId,
                            auto_act.nodesId[0][auto_act.nodesId[0].length - 1], selObjs)) {
                        session.getKernel().setPermitPerform(auto_act.flowId, true);
                        //Activity act = session.getTaskHelper().getSelectedActivity();

	                	long oldId = auto_act.ui.id;
	                	long oldInfId = auto_act.ui.id;
	                	int oldPermit = (int) auto_act.param & ACT_PERMIT;
	                    try {
	                    	session.getTaskHelper().disableActivity(auto_act);

	                    	String[] res_ = session.getKernel().performActivitys(new Activity[] { auto_act }, "");
	                        if (res_.length == 1 && res_[0].equals("synch")) {
	                            session.getTaskHelper().setAutoIfcFlowId_(auto_act.flowId);
	                        } else {
		                    	session.getTaskHelper().reenableActivity(auto_act, oldId, oldInfId, oldPermit);
	                            if (res_.length > 0) {
	                                // обработка ошибок
	                                String msg_ = res_[0];
	                                for (int i = 1; i < res_.length; ++i)
	                                    msg_ += "\n" + res_[i];
	                                return msg_;
	                            }
	                        }
	                        // auto_act.param |= Constants.ACT_PERMIT;
	                        session.getTaskHelper().reenableActivity(auto_act.flowId);
	                        session.getTaskHelper().addAutoActivity(auto_act);
	
	                        if (res_.length == 1 && res_[0].equals("synch")) {
	                            session.getTaskHelper().reloadTask(auto_act.flowId, 0, true, true);
	                        }
	                    } catch (KrnException e) {
	                    	session.getTaskHelper().reenableActivity(auto_act, oldId, oldInfId, oldPermit);
	                        log.error(e, e);
	                    }
                    }
                    session.getTaskHelper().closeAutoAct();
                }
			}
		} catch (KrnException ex) {
			log.error(ex, ex);
		}
		return null;
	}

	public void cancelAutoActivity() {
		try {
			Activity auto_act = session.getTaskHelper().getAutoAct();
			if (auto_act != null) {
				session.getKernel().cancelProcess(auto_act.flowId,
						auto_act.msg, false, true);
			}
			session.getTaskHelper().closeAutoAct();
		} catch (KrnException ex) {
			log.error(ex, ex);
		}
	}

	public WebComponent getComponent(String id) {
		WebComponent res = (WebPanel) adapter.getPanel();
		if (id.equals(res.getId()))
			return res;
		else
			return res.getWebComponent(id);
	}
	
	public WebComponent getComponentUID(String id) {
		WebComponent res = (WebPanel) adapter.getPanel();
		if (id.equals(res.getId()))
			return res;
		else
			return res.getWebComponentUID(id);
	}
	
	public WebComponent getComponentByUID(String uid) {
		return componentMap.get(uid);
	}
	
	public void registerComponent(String uid, WebComponent comp) {
		componentMap.put(uid, comp);
	}

	public PanelAdapter getPanelAdapter() {
		return adapter;
	}

	public WebSession getSession() {
		return session;
	}

	public Kernel getKernel() {
		return session.getKernel();
	}

	public InterfaceManager getInterfaceManager() {
		return session.getFrameManager();
	}

	public KrnObject getObj() {
		return obj;
	}

	private void loadReports(ReportRecord parent, Object menu) {
		boolean isVisible;
		List<ReportRecord> records = parent.getChildren();
		String pid = ((WebComponent) menu).getId();
		for (ReportRecord record : records) {
			int count = 0;
			if (menu instanceof WebMenu) {
				count = ((WebMenu) menu).getPopupMenu().getComponentCount();
			} else {
				count = ((WebPopupMenu) menu).getComponentCount();
			}
			String id = pid + ((count > 9) ? "" + count : "0" + count);
			if (record.isFolder()) {
				String name = record.getName(this);
				ReportMenu subMenu = new ReportMenu(WebFrame.this, name, record, true);
				subMenu.setId(id);
				if (menu instanceof WebMenu) {
					((WebMenu) menu).add(subMenu);
				} else {
					((WebPopupMenu) menu).add(subMenu);
				}
				loadReports(record, subMenu);
			} else {
				boolean reportVisible = false;
				try {
					KrnObject obj = getKernel().getObjectsByIds(
							new long[] { record.getObjId() }, -1)[0];

					KrnAttribute basesAttr = getKernel()
							.getAttributeByName(
									getKernel().getClassNode(obj.classId)
											.getKrnClass(), "bases");
					KrnObject[] bases = null;
					if (basesAttr != null)
						bases = getKernel().getObjects(obj, "bases", 0);
					else
						log.error("|USER: " + session.getUserName()
								+ "| Attribute \"bases\" not found!");

					KrnObject curDb = getKernel().getCurrentDb();
					if (bases != null && bases.length > 0) {
						for (int k = 0; k < bases.length; k++) {
							KrnObject base = bases[k];
							if (base.id == curDb.id) {
								reportVisible = true;
								break;
							}
						}
					} else {
						reportVisible = true;
					}
				} catch (Exception e) {
					reportVisible = true;
				}
				
				if (reportVisible) {
					ReportPrinter rp = new ReportPrinterAdapter(this,
							this.getPanel(), record);
					String funcVisible = record.getVisibilityFunc();
					if(funcVisible != null && !"".equals(funcVisible)){
						isVisible = isReportVisible(WebFrame.this, funcVisible);
					}
					else
						isVisible = true;
					
					WebMenuItem ri;
					List<LangHelper.WebLangItem> langItems = LangHelper.getAll(session.getConfigNumber());
					int reportsCount = 0;
					LangHelper.WebLangItem existLang = null;
					for (int i = 0; i < langItems.size(); i++) {
						LangHelper.WebLangItem li = langItems.get(i);
						if ("RU".equals(li.code)) {
							if (rp.hasReport(li.obj)) {
								existLang = li;
								reportsCount++;
							}
						} else if ("KZ".equals(li.code)) {
							if (rp.hasReport(li.obj)) {
								existLang = li;
								reportsCount++;
							}
						}
					}
					if (reportsCount == 1) {
						ri = new PrinterMenuItem(session, rp, true, existLang);
						ri.setId(id);
					} else
						ri = new PrinterMenu(session, rp, true, id);
					
					ri.setVisible(isVisible, false);
					((ReportPrinterAdapter)rp).setMenuItem(ri);

					if (menu instanceof WebMenu) {
						//if(isVisible)
							((WebMenu) menu).add(ri);
					} else {
						//if(isVisible)
							((WebPopupMenu) menu).add(ri);
					}
				}
			}
		}
	}

	private void preloadReportRefs(ReportRecord parent) {
		List<ReportRecord> records = parent.getChildren();
		for (ReportRecord record : records) {
			if (record.isFolder()) {
				preloadReportRefs(record);
			} else {
				boolean reportVisible = false;
				try {
					KrnObject obj = getKernel().getObjectsByIds(
							new long[] { record.getObjId() }, -1)[0];

					KrnAttribute basesAttr = getKernel().getAttributeByName(
									getKernel().getClassNode(obj.classId)
											.getKrnClass(), "bases");
					KrnObject[] bases = null;
					if (basesAttr != null)
						bases = getKernel().getObjects(obj, "bases", 0);
					else
						log.error("|USER: " + session.getUserName()
								+ "| Attribute \"bases\" not found!");

					KrnObject curDb = getKernel().getCurrentDb();
					if (bases != null && bases.length > 0) {
						for (int k = 0; k < bases.length; k++) {
							KrnObject base = bases[k];
							if (base.id == curDb.id) {
								reportVisible = true;
								break;
							}
						}
					} else {
						reportVisible = true;
					}
				} catch (Exception e) {
					reportVisible = true;
				}
				
				if (reportVisible) {
		            try {
						OrRef.createRef(record.getPath(), false, Mode.RUNTIME, getRefs(), getTransactionIsolation(), this);
					} catch (KrnException e) {
						log.error(e, e);
					}
				}
			}
		}
	}

	private void changePrinterTitles(Object menu) {
	        WebComponent[] children = null;
		if (menu instanceof WebPopupMenu) {
			children = ((WebPopupMenu) menu).getComponents();
		} else if (menu instanceof WebMenu) {
			children = ((WebMenu) menu).getPopupMenu().getComponents();
		}

		for (int i = 0; i < children.length; i++) {
			WebComponent child = children[i];
			if (child instanceof PrinterMenu) {
				PrinterMenu pi = (PrinterMenu) child;
				pi.changeTitle();
			} else if (child instanceof PrinterMenuItem) {
				PrinterMenuItem pmi = (PrinterMenuItem) child;
				pmi.changeTitle();
			} else if (child instanceof ReportMenu) {
				ReportMenu rm = (ReportMenu) child;
				rm.changeTitle();
				changePrinterTitles(rm);
			}
		}
	}

	public boolean hasReports() {
		return (docMenu != null && docMenu.getPopupMenu().getComponentCount() > 0);
	}

	public class ReportMenu extends WebMenu {
		private ReportRecord record;
		private WebFrame frame;

		public ReportMenu(String s, ReportRecord r) {
			super(s, s, null, Mode.RUNTIME, WebFrame.this, null);
			record = r;
		}

		public ReportMenu(WebFrame frame, String s, ReportRecord r, boolean submenu) {
			super(s, s, null, Mode.RUNTIME, frame, null);
			record = r;
			this.frame = frame;
		}

		public void changeTitle() {
			setText(record.getName(frame));
		}

		public void toHTML(StringBuilder b) {
			b.append("<img src=\"").append(WebController.APP_PATH)
					.append("/images/1x16.gif\" />").append(getText());
		}
	}

	public class PrinterMenu extends WebMenu {
		private ReportPrinter p_;
		private WebSession session;

		public PrinterMenu(WebSession session, ReportPrinter p) {
			super(p.toString(), p.toString(), null, Mode.RUNTIME, WebFrame.this, null);
			p_ = p;
		}

		public PrinterMenu(WebSession session, ReportPrinter p, boolean submenu, String id) {
			super(p.toString(), p.toString(), null, Mode.RUNTIME, WebFrame.this, null);
			p_ = p;
			this.session = session;
			setId(id);
			List<LangHelper.WebLangItem> langItems = LangHelper.getAll(session.getConfigNumber());
			for (int i = 0; i < langItems.size(); i++) {
				LangHelper.WebLangItem li = langItems.get(i);
				if ("RU".equals(li.code)) {
					if (p.hasReport(li.obj)) {
						PrinterLangItem pi = new PrinterLangItem(session, this, li);
						pi.setId(getId() + getPopupMenu().getComponentCount());
						add(pi);
					}
				} else if ("KZ".equals(li.code)) {
					if (p.hasReport(li.obj)) {
						PrinterLangItem pi = new PrinterLangItem(session, this, li);
						pi.setId(getId() + getPopupMenu().getComponentCount());
						add(pi);
					}
				}
			}
		}

		public void changeTitle() {
			setText(p_.toString());
		}

		public void changeSelection() {
		}

		public ReportPrinter getPrinter() {
			return p_;
		}

		public void toHTML(StringBuilder b) {
			b.append("<img src=\"").append(WebController.APP_PATH)
					.append("/images/1x16.gif\" />").append(getText());
		}
	}

	public class PrinterMenuItem extends WebMenuItem {
		private ReportPrinter p_;
		private LangHelper.WebLangItem langItem;
		private WebSession session;
		
		public PrinterMenuItem(WebSession session, ReportPrinter p, boolean submenu,
				LangHelper.WebLangItem li) {
			super(p.toString(), p.toString(), null, Mode.RUNTIME, WebFrame.this, null, li.code.toLowerCase(Locale.ROOT));
			p_ = p;
			langItem = li;
			this.session = session;
		}

		public void changeTitle() {
			setText(p_.toString());
		}

		public void changeSelection() {
		}

		public ReportPrinter getPrinter() {
			return p_;
		}

		public KrnObject getLanguage() {
			return langItem.obj;
		}

		public void toHTML(StringBuilder b) {
			String img = "";
			if (LangHelper.getRusLang(session.getConfigNumber()).obj.id == getLanguage().id) {
				img = "<img src=\"" + WebController.APP_PATH
						+ "/images/RULang.gif\" />";
			} else if (LangHelper.getKazLang(session.getConfigNumber()).obj.id == getLanguage().id) {
				img = "<img src=\"" + WebController.APP_PATH
						+ "/images/KZLang.gif\" />";
			}
			b.append("<a class=\"rep\" onclick=\"showReport('").append(id)
					.append("'); return true;\">").append(img)
					.append(getText()).append("</a>");
		}
	}

	public class PrinterLangItem extends WebMenuItem {
		private LangHelper.WebLangItem langItem;
		PrinterMenu pm;
		private WebSession session;

		public PrinterLangItem(WebSession session, PrinterMenu pm, LangHelper.WebLangItem item) {
			super(item.name, item.name, null, Mode.RUNTIME, WebFrame.this, null, item.code.toLowerCase(Locale.ROOT));
			this.pm = pm;
			langItem = item;
			this.session = session;
		}

		public KrnObject getLanguage() {
			return langItem.obj;
		}

		public PrinterMenu getPrinterMenu() {
			return pm;
		}

		public void toHTML(StringBuilder b) {
			String img = "";
			if (LangHelper.getRusLang(session.getConfigNumber()).obj.id == getLanguage().id) {
				img = "<img src=\"" + WebController.APP_PATH
						+ "/images/RULang.gif\" />";
			} else if (LangHelper.getKazLang(session.getConfigNumber()).obj.id == getLanguage().id) {
				img = "<img src=\"" + WebController.APP_PATH
						+ "/images/KZLang.gif\" />";
			}
			b.append("<a class=\"rep\" onclick=\"showReport('").append(id)
					.append("'); return true;\">").append(img)
					.append(getText()).append("</a>");
		}
		
	    public JsonObject getReportJSON(JsonArray arr) {
	        JsonObject obj = new JsonObject();

	        obj.add("name", pm.getText() + "(" + getText() + ")");
	        obj.add("id", id);
	        obj.add("v", isVisible);

	        arr.add(obj);
	        return obj;
	    }
	}

	public String generateReport(String id, Date fDate, Date lDate, Date cDate) {
		File file;
		WebComponent comp = docMenu.getWebComponent(id);
		ReportPrinter p = null;
		KrnObject lang = null;
		if (comp instanceof PrinterMenuItem) {
			PrinterMenuItem pmi = (PrinterMenuItem) comp;
			p = pmi.getPrinter();
			lang = pmi.getLanguage();
		} else if (comp instanceof PrinterLangItem) {
			PrinterLangItem pli = (PrinterLangItem) comp;
			p = pli.getPrinterMenu().getPrinter();
			lang = pli.getLanguage();
		}
		file = p.printToFile(lang, fDate, lDate, cDate);

		if (file == null || file.length() == 0) {
		    return "<r><alert>Ошибка при генерации отчета!</alert><stopWait/></r>";
		} else {
			String afn = file.getAbsolutePath();
			afn = afn.replaceAll("\\\\", "/");
			String fileName = afn.substring(afn.lastIndexOf("/") + 1);
			StringBuilder res = new StringBuilder(100);
		        res.append("<r><openFile>").append(fileName).append("</openFile></r>");
			return res.toString();
		}
	}

    public String generateReport(String id) {
    	Object file = null;
        WebComponent comp = docMenu.getWebComponent(id);
        ReportPrinter p = null;
        KrnObject lang = null;
        if (comp instanceof PrinterMenuItem) {
            PrinterMenuItem pmi = (PrinterMenuItem) comp;
            p = pmi.getPrinter();
            lang = pmi.getLanguage();
        } else if (comp instanceof PrinterLangItem) {
            PrinterLangItem pli = (PrinterLangItem) comp;
            p = pli.getPrinterMenu().getPrinter();
            lang = pli.getLanguage();
        }
        long flags = p.getReportFilters(lang);
        if (flags > 0) {
            return ViewHelper.getDatesPanelXml(id, flags);
        } else {
            file = p.printToFile(lang);
        }

        if (ReportPrinter.FILE_NOT_PRINTED.equals(obj))
            return "<r><alert>Файл не может быть сформирован!</alert><stopWait/></r>";
        else if (file == null || ((File)file).length() == 0) {
            return "<r><alert>Ошибка при генерации отчета!</alert><stopWait/></r>";
        } else {
            String afn = ((File)file).getAbsolutePath();
            afn = afn.replaceAll("\\\\", "/");
            String fileName = afn.substring(afn.lastIndexOf("/") + 1);
            StringBuilder res = new StringBuilder(100);
            res.append("<r><openFile>").append(fileName).append("</openFile></r>");
            return res.toString();
        }
    }

    public JsonObject generateReportJSON(String id) {
        WebComponent comp = docMenu.getWebComponent(id);
        ReportPrinter p = null;
        KrnObject lang = null;
        if (comp instanceof PrinterMenuItem) {
            PrinterMenuItem pmi = (PrinterMenuItem) comp;
            p = pmi.getPrinter();
            lang = pmi.getLanguage();
        } else if (comp instanceof PrinterLangItem) {
            PrinterLangItem pli = (PrinterLangItem) comp;
            p = pli.getPrinterMenu().getPrinter();
            lang = pli.getLanguage();
        }
        
        Object obj = p.printToFile(lang);

        if (ReportPrinter.FILE_NOT_PRINTED.equals(obj))
            return new JsonObject().add("result", "success");
        else if (obj == null || ((File) obj).length() == 0) {
            return new JsonObject().add("result", "error").add("message", "Ошибка при генерации отчета!");
        } else {
            String afn = ((File) obj).getAbsolutePath();
            afn = afn.replaceAll("\\\\", "/");
            String fileName = afn.substring(afn.lastIndexOf("/") + 1);
            if (!fileName.startsWith("xxx")){
            	fileName = kz.tamur.web.common.Base64.encodeBytes(((File) obj).getName().getBytes());
            }
            return new JsonObject().add("result", "success").add("file", fileName);
        }
    }

    private void doAfterOpen() {
        OrPanelComponent p = getPanel();
        ASTStart template = p != null ? p.getAfterOpenTemplate() : null;
        if (template != null) {
            ClientOrLang orlang = new ClientOrLang(this);
            Map<String, Object> vc = new HashMap<String, Object>();
            boolean calcOwner = OrCalcRef.setCalculations();
            try {
                orlang.evaluate(template, vc, this.getPanelAdapter(), new Stack<String>());
            } catch (Exception ex) {
                Util.showErrorMessage(p, ex.getMessage(), "Действие после открытия");
            	log.error("Ошибка при выполнении формулы 'Действие после открытия'" + p.getClass().getName() + "', uuid: " + p.getUUID());
                log.error(ex, ex);
            } finally {
                if (calcOwner)
                	OrCalcRef.makeCalculations();
            }
        }
    }

    public int confirm(String msg) {
        confirm = -1;
        confirmMessage = msg;
    	session.sendCommand("confirm", msg);

        waitFrameAction();
        if (confirm == -1)
            WebSessionManager.releaseSession(session.getId());
        return confirm;
    }

    public void setConfirm(int res) {
        confirm = res;
        confirmMessage = null;
        wakeupFrameAction();
    }

    public String askPassword(String msg) {
    	promptAction = -1;
    	session.sendCommand("askPassword", msg);

        waitFrameAction();
        if (promptAction == -1)
            WebSessionManager.releaseSession(session.getId());
        // OK - 1, Cancel = 0, Timeout = -1
        return promptAction == 1 ? promptRes : null;
    }

    public void setPassword(String res, int action) {
    	promptRes = res;
    	promptAction = action;
        wakeupFrameAction();
    }

    public int getOption(Activity act) {
        option = -2;
        optionActivity = act;
        waitFrameAction();
        if (option == -2)
            WebSessionManager.releaseSession(session.getId());
        return option;
    }

    public void setOption(int res) {
        option = res;
        optionActivity = null;
        wakeupFrameAction();
    }

    public int getCommitAction(ReqMsgsList list, boolean canIgnore, String[] options) {
        commitRes = -1;
        msgList = list;
        commitNeedAction = 1;
        JsonObject data = new JsonObject();
        if (options != null) {
            if (options.length > 0 && options[0] != null) data.add("btnEdt", options[0]);
            if (options.length > 1 && options[1] != null) data.add("btnIgn", options[1]);
        }
        if (list != null) {
            int isFatal = (list.hasFatalErrors() && !canIgnore) || (options != null && options.length > 0 && options[1].equals("hide")) ? 1 : 0;
            data.add("fatal", isFatal);
            data.add("errors", toJSON(list));
        	
            Pair<String, String> docFile = toFileMsg(list);
        	if (docFile != null)
        		data.add("path", docFile.first).add("name", docFile.second);

        	if(isFatal==1) {
                data.set("btnEdt", "OK");
            }
            session.sendCommand("showErrors", data);
        }
        waitFrameAction();
        if (commitRes == -1)
            WebSessionManager.releaseSession(session.getId());
        return commitRes;
    }
    
    public synchronized void waitFrameAction() {
    	session.setWaitingFrame(this);
        try {
        	for (int i = 0; i<Constants.TIME_OUT_WEB_WAIT_QUANTS; i++) {
            	this.wait(Constants.TIME_OUT_WEB_WAIT_QUANT);
            	if (WebController.isDestroying() || session.getFrameManager().getWaitingFrame() == null)
            		return;
        	}
        } catch (InterruptedException e) {
            log.error(e, e);
            WebSessionManager.releaseSession(session.getId());
        }
    }

    public synchronized void wakeupFrameAction() {
    	session.setWaitingFrame(null);
        this.notify();
    }

    public void setCommitAction(int res) {
        commitRes = res;
        msgList = null;
        wakeupFrameAction();
    }

    public int getOption(String[] options) {
        optionRes = -2;

		JsonArray opts = new JsonArray();
		
		for (int i = 0; i < options.length; i++) {
			JsonObject obj = new JsonObject();
			
			obj.add("o", options[i]);
			opts.add(obj);
		}

        session.sendCommand("showOptions", 
        		new JsonObject().add("options", opts));
            
        waitFrameAction();
        if (optionRes == -2)
            WebSessionManager.releaseSession(session.getId());
        return optionRes;
    }

    public void setOptionRes(int res) {
    	optionRes = res;
        wakeupFrameAction();
    }

    public String getAdditionalResponseXml() {
        return additionalResponseXml;
    }

    public void addAdditionalResponseXml(String additionalResponseXml) {
        this.additionalResponseXml += additionalResponseXml;
    }

    public void release() {
    	exec.shutdown();
        this.exec = null;

    	cacheListeners.clear();
    	cacheChangeListeners.clear();
    	
    	if (cache != null)
    		cache.clear();
        cache = null;
    	if (ownCache != null)
    		ownCache.clear();
        ownCache = null;
        if (refs != null) {
            for (OrRef r : refs.values()) {
                r.release();
            }
            refs.clear();
        }
        refs = null;
        if (contentRefs != null) {
            for (OrRef r : contentRefs.values()) {
                r.release();
            }
            contentRefs.clear();
        }
        contentRefs = null;
        adapter = null;
        if (adaptersStack != null) {
            adaptersStack.clear();
        }
        adaptersStack = null;
        if (refGroups != null) {
        	refGroups.clear();
        }
        refGroups = null;
        if (componentMap != null) {
        	componentMap.clear();
        }
        componentMap = null;
        if (reports_ != null) {
            reports_.clear();
        }
        reports_ = null;
        rootReport = null;
        parentFrame = null;
        oldFrm = null;
        
        wakeupFrameAction();
    }

    public String signIola(String msg, boolean newKey, boolean auth) {
        if (newKey && !session.getKernel().getUser().isInstantECP()) {
            getSession().setProfilePassword(null);
        }
        signValue = null;
        signType = "iola";
        signMessage = msg;
        
        try {
        	JsonObject res = new JsonObject();
        	res.add("str", msg);
        	res.add("type", "iola");
        
        	res.add("path", session.getProfile());
        	res.add("pass", session.getProfilePassword());
        	res.add("cont", session.getProfileConteiner());
        	res.add("auth", auth);

        	session.sendCommand("signString", res);
        } catch (Exception e) {
        	log.error(e, e);
        }
        
        waitFrameAction();
        if (signValue == null)
            WebSessionManager.releaseSession(session.getId());

        return signValue;
    }
    
    public List<Object> signTextWithNCA(String text, boolean newKey) {
        if (newKey && !session.getKernel().getUser().isInstantECP()) {
            getSession().setNCAProfilePassword(null);
        }

        signedData = null;
        cert = null;
        checkSignResult = null;
        try {
        	JsonObject res = new JsonObject();
        	res.add("text", text);

        	res.add("path", session.getNCAProfile());
        	res.add("pass", session.getNCAProfilePassword());
        	res.add("cont", session.getNCAProfileConteiner());

        	session.sendCommand("signTextWithNCA", res);
        } catch (Exception e) {
        	log.error(e, e);
        }
        waitFrameAction();
        if (signedData == null) {
            WebSessionManager.releaseSession(session.getId());
        }
        return Arrays.asList(isOkSignedData, signedData, cert, checkSignResult);
    }
    
    public File getFile() {
    	file = null;
        try {
        	JsonObject res = new JsonObject();
        	session.sendCommand("getFile", res);
        } catch (Exception e) {
        	log.error(e, e);
        }
        waitFrameAction();
        return file;
    }

    public void setSignValue(String value) {
        signValue = value;
        signMessage = null;
        wakeupFrameAction();
    }
    
    public void setSignDataResult(boolean isOkSignedData, String result, X509Certificate cert, CheckSignResult checkSignResult) {
    	this.isOkSignedData = isOkSignedData;
    	this.signedData = result;
    	this.cert = cert;
    	this.checkSignResult = checkSignResult;
        wakeupFrameAction();
    }
    
    public void setFile(File file) {
    	this.file = file;
        wakeupFrameAction();
    }

    public boolean connectUcgoWebsocket() {
        ucgoResponse = null;
        ucgoRequest = "connect";
        
        try {
        	session.sendCommand("connectUcgoWebsocket", "1");
        } catch (Exception e) {
        	log.error(e, e);
        }
        
        waitFrameAction();
        if (ucgoResponse == null)
            WebSessionManager.releaseSession(session.getId());

        return Boolean.parseBoolean(ucgoResponse);
    }

    public String generateUcgoPKCS10(String dn, boolean auth) {
        ucgoResponse = null;
        ucgoRequest = dn;
        
        try {
        	JsonObject res = new JsonObject();
        	res.add("str", dn);
        	res.add("auth", auth);

        	session.sendCommand("generateUcgoPKCS10", res);
        } catch (Exception e) {
        	log.error(e, e);
        }
        
        waitFrameAction();
        if (ucgoResponse == null)
            WebSessionManager.releaseSession(session.getId());

        return ucgoResponse;
    }

    public String generateUcgoPKCS7(String content, boolean auth) {
        ucgoResponse = null;
        ucgoRequest = content;
        
        try {
        	JsonObject res = new JsonObject();
        	res.add("str", content);
        	res.add("auth", auth);

        	session.sendCommand("generateUcgoPKCS7", res);
        } catch (Exception e) {
        	log.error(e, e);
        }
        
        waitFrameAction();
        if (ucgoResponse == null)
            WebSessionManager.releaseSession(session.getId());

        return ucgoResponse;
    }

    public void setUcgoResponse(String value) {
    	ucgoResponse = value;
    	ucgoRequest = null;
        wakeupFrameAction();
    }

    public String saveUcgoCertificate(String cert, String reader, String uid, String tokPD) {
        ucgoResponse = null;
        ucgoRequest = cert;
        
        try {
        	JsonObject res = new JsonObject();
        	res.add("cert", cert);
        	res.add("reader", reader);
        	res.add("uid", uid);
        	if (tokPD != null)
            	res.add("tokpd", tokPD);

        	session.sendCommand("saveUcgoCertificate", res);
        } catch (Exception e) {
        	log.error(e, e);
        }
        
        waitFrameAction();
        if (ucgoResponse == null)
            WebSessionManager.releaseSession(session.getId());

        return ucgoResponse;
    }

    public String haveUcgoCertificate(String reader, String uid, String tokPD) {
        ucgoResponse = null;
        ucgoRequest = "have";
        
        try {
        	JsonObject res = new JsonObject();
        	res.add("reader", reader);
        	res.add("uid", uid);
        	if (tokPD != null)
            	res.add("tokpd", tokPD);

        	session.sendCommand("haveUcgoCertificate", res);
        } catch (Exception e) {
        	log.error(e, e);
        }
        
        waitFrameAction();
        if (ucgoResponse == null)
            WebSessionManager.releaseSession(session.getId());

        return ucgoResponse;
    }

    public String selectUcgoCertificate(String iin, String bin) {
        ucgoResponse = null;
        ucgoRequest = "select";
        
        try {
        	JsonObject res = new JsonObject();
        	if (iin != null)
        		res.add("iin", iin);
        	if (bin != null)
        		res.add("bin", bin);

        	session.sendCommand("selectUcgoCertificate", res);
        } catch (Exception e) {
        	log.error(e, e);
        }
        
        waitFrameAction();
        if (ucgoResponse == null)
            WebSessionManager.releaseSession(session.getId());

        return ucgoResponse;
    }

    public String deleteUcgoCertificate(String keyName) {
        ucgoResponse = null;
        ucgoRequest = "delete";
        
        try {
        	session.sendCommand("deleteUcgoCertificate", keyName);
        } catch (Exception e) {
        	log.error(e, e);
        }
        
        waitFrameAction();
        if (ucgoResponse == null)
            WebSessionManager.releaseSession(session.getId());

        return ucgoResponse;
    }

    public boolean connectScanWebsocket() {
        wsResponse = null;
        wsRequest = "connect";
        
        try {
        	session.sendCommand("connectScanWebsocket", "1");
        } catch (Exception e) {
        	log.error(e, e);
        }
        
        waitFrameAction();
        if (wsResponse == null)
            WebSessionManager.releaseSession(session.getId());

        return Boolean.parseBoolean(wsResponse);
    }

    public boolean disconnectScanWebsocket() {
        try {
        	session.sendCommand("disconnectScanWebsocket", "1");
        } catch (Exception e) {
        	log.error(e, e);
        }
        return true;
    }

    public void setWsResponse(String value) {
    	wsResponse = value;
    	wsRequest = null;
        wakeupFrameAction();
    }

    public JsonObject wsLoadClientFile(String path) {
    	wsResponse = null;
    	wsRequest = "loadClientFile";
        
        try {
        	JsonObject res = new JsonObject();
        	res.add("path", path);
        	res.add("id", UUID.randomUUID().toString());

        	session.sendCommand("loadClientFile", res);
        } catch (Exception e) {
        	log.error(e, e);
        }
        
        waitFrameAction();
        if (wsResponse == null)
            WebSessionManager.releaseSession(session.getId());

        return JsonObject.readFrom(wsResponse);
    }

    public JsonObject wsSaveFileOnClient(String path, byte[] content) {
    	wsResponse = null;
    	wsRequest = "saveFileOnClient";
        
        try {
        	JsonObject res = new JsonObject();
        	res.add("path", path);
        	res.add("data", new String(Base64.encode(content)));
        	res.add("id", UUID.randomUUID().toString());

        	session.sendCommand("saveFileOnClient", res);
        } catch (Exception e) {
        	log.error(e, e);
        }
        
        waitFrameAction();
        if (wsResponse == null)
            WebSessionManager.releaseSession(session.getId());

        return JsonObject.readFrom(wsResponse);
    }

    public JsonObject wsStartScan() {
    	return wsStartScan(UUID.randomUUID().toString());
    }
    
    public JsonObject wsStartScan(String id) {
    	wsResponse = null;
    	wsRequest = "startScan " + id;
        
        try {
        	JsonObject res = new JsonObject();
        	res.add("id", id);

        	session.sendCommand("startScan", res);
        } catch (Exception e) {
        	log.error(e, e);
        }
        
        waitFrameAction();
        if (wsResponse == null)
            WebSessionManager.releaseSession(session.getId());

        return JsonObject.readFrom(wsResponse);
    }
    
	public JsonObject wsOpenClientFiles(String windowTitle, String buttonTitle, String dir, String extensions,
			String description) {
    	
		String id = UUID.randomUUID().toString();
		wsResponse = null;
    	wsRequest = "openClientFiles " + id;

        try {
        	JsonObject res = new JsonObject();
        	res.add("id", id);
        	res.add("windowTitle", windowTitle);
        	res.add("buttonTitle", buttonTitle);
        	res.add("dir", dir);
        	res.add("extensions", extensions);
        	res.add("description", description);

        	session.sendCommand("openClientFiles", res);
        } catch (Exception e) {
        	log.error(e, e);
        }
        
        waitFrameAction();
        if (wsResponse == null)
            WebSessionManager.releaseSession(session.getId());

        return JsonObject.readFrom(wsResponse);
	}
    
    public String getWsRequest() {
    	return wsRequest;
    }

    public String getSignXML() {
        StringBuilder b = new StringBuilder(30);
        b.append("<r>").append(EOL);
        if (signMessage != null) {
            b.append("<sign>");
            b.append("<type>");
            b.append(signType);
            b.append("</type>");
            b.append("<text>");
            b.append(signMessage);
            signMessage = null;
            b.append("</text>");
            if (getSession().getProfile() != null) {
                b.append("<path>").append(getSession().getProfile()).append("</path>");
            }
            if (getSession().getProfilePassword() != null) {
                b.append("<code>").append(getSession().getProfilePassword()).append("</code>");
            }
            b.append("</sign>");
        }

        b.append("</r>");
        return b.toString();
    }

    public int showDialog(WebFrame parent, String title, int width, int height, boolean hasClearButton) {
        needDialog = true;
        parentFrame = parent;
        dialogTitle = title;
        dialogWidth = width;
        dialogHeight = height;
        dialogHasClearButton = hasClearButton;
        dialogResult = -1;
        
        JsonObject res = new JsonObject();
    	res.add("title", dialogTitle);
    	res.add("w", dialogWidth);
    	res.add("h", dialogHeight);
    	res.add("clear", dialogHasClearButton);

    	session.sendCommand("showDialog", res);
        waitFrameAction();
        if (dialogResult == -1)
            WebSessionManager.releaseSession(session.getId());

        return dialogResult;
    }

    public void setDialogResult(int res) {
        dialogResult = res;
        parentFrame = null;
        dialogTitle = null;
        wakeupFrameAction();
    }

    /**
     * @return the dialogResult
     */
    public int getDialogResult() {
        return dialogResult;
    }

    public String getECPParams() {
        operation = "getECPParams";
        operationParams = new ArrayList<String>();
        operationResult = null;

        waitFrameAction();
        if (operationResult == null)
            WebSessionManager.releaseSession(session.getId());

        return operationResult;
    }

    public String clearECPParams() {
        operation = "clearECPParams";
        operationParams = new ArrayList<String>();
        operationResult = null;

        waitFrameAction();
        if (operationResult == null)
            WebSessionManager.releaseSession(session.getId());

        return operationResult;
    }

    public String getLastError() {
        operation = "getLastError";
        operationParams = new ArrayList<String>();
        operationResult = null;

        waitFrameAction();
        if (operationResult == null)
            WebSessionManager.releaseSession(session.getId());

        return operationResult;
    }

	public void setOperationResult(String value) {
        operationResult = value;
        operation = null;
        wakeupFrameAction();
    }

    public byte[] signData(int storeType, String storePath, String password2, byte[] text) {
        operation = "signData";
        operationParams = new ArrayList<String>();
        operationParams.add(Hex.encodeStr(text));
        operationResult = null;

        waitFrameAction();
        if (operationResult == null)
            WebSessionManager.releaseSession(session.getId());

        return operationResult.length() < 2 ? null : Base64.decode(operationResult.replaceAll(" ", "+"));
    }

    public byte[] getCertificate(int storeType, String storePath, String password2) {
        operation = "getCertificate";
        operationParams = new ArrayList<String>();
        operationResult = null;

        waitFrameAction();
        if (operationResult == null)
            WebSessionManager.releaseSession(session.getId());

        return operationResult.length() < 2 ? null : Base64.decode(operationResult.replaceAll(" ", "+"));
    }

    public static String beforeDecodeBase64(String str) {
        StringBuilder out = new StringBuilder(str.replaceAll("-", "+").replaceAll("_", "/"));
        while (out.length() % 4 > 0) {
            out.append("=");
        }
        return out.toString();
    }

    public static String afterEncodeBase64(String str) {
        str = str.replaceAll("\\+", "-").replaceAll("\\/", "_");
        int beg = str.indexOf("=");
        if (beg > -1) {
            return str.substring(0, beg);
        }
        return str;
    }

    public void showAlarm(String title) {
        showAlarm = true;
        titleAlarm = title;
    }
    
    public JsonObject getReportsJSON() {
        if (docMenu != null && docMenu.getPopupMenu() != null) {
        	return docMenu.getPopupMenu().getReportsJSON();
        }
        return new JsonObject().add("result", "success");
    }
    
    public String getStringRes(String name) {
        return session.getResource().getString(name);
    }

    /**
     * @return the selectedComponent
     */
    public OrGuiComponent getSelectedComponent() {
        return selectedComponent;
    }

    /**
     * @param selectedComponent the selectedComponent to set
     */
    public void setSelectedComponent(OrGuiComponent selectedComponent) {
        this.selectedComponent = selectedComponent;
    }

	@Override
	public void processStarted(long defId, long flowId) {
		callAfterTaskListUpdate(defId, flowId, "start");
	}

	@Override
	public void processCanceled(long defId, long flowId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processEnded(long defId, long flowId) {
		callAfterTaskListUpdate(defId, flowId, "end");
	}
	
	public void callAfterTaskListUpdate(long defId, long flowId, String type) {
        OrWebPanel p = (OrWebPanel) getPanel();
        ASTStart template = p.getAfterTaskListUpdateTemplate();
        if (template != null) {
            ClientOrLang orlang = new ClientOrLang(this);
            Map<String, Object> vc = new HashMap<String, Object>();
            vc.put("FLOW_ID", flowId);
            vc.put("PROC_ID", defId);
            vc.put("PROC_EVENT", type);
            boolean calcOwner = OrCalcRef.setCalculations();
            try {
                orlang.evaluate(template, vc, getPanelAdapter(), new Stack<String>());
            } catch (Exception ex) {
                Util.showErrorMessage(p, ex.getMessage(), "Действие после обновления списка задач");
            	log.error("Ошибка при выполнении формулы 'Действие после обновления списка задач'" + p.getClass().getName() + "', uuid: " + p.getUUID());
                log.error(ex, ex);
            } finally {
                if (calcOwner)
                	OrCalcRef.makeCalculations();
            }
            session.sendCommand("refresh", "");
        }
	}
	
	public void doMessageRecieved(final String type, final Object msg) {
        final OrWebPanel p = (OrWebPanel) getPanel();
        final ASTStart template = p.getMessageRecievedTemplate();
        if (template != null) {
            exec.submit(new Runnable() {
                @Override
                public void run() {
		            ClientOrLang orlang = new ClientOrLang(WebFrame.this);
		            Map<String, Object> vc = new HashMap<String, Object>();
		            vc.put("TYPE", type);
		            vc.put("DATA", msg);
		            boolean calcOwner = OrCalcRef.setCalculations();
		            try {
		                orlang.evaluate(template, vc, getPanelAdapter(), new Stack<String>());
		            } catch (Exception ex) {
		                Util.showErrorMessage(p, ex.getMessage(), "Действие при получении сообщения");
		            	log.error("Ошибка при выполнении формулы 'Действие при получении сообщения'" + p.getClass().getName() + "', uuid: " + p.getUUID());
		                log.error(ex, ex);
		            } finally {
		                if (calcOwner)
		                	OrCalcRef.makeCalculations();
		            }
		            session.sendCommand("refresh", "");
                }
            });
        }
	}
	
	public Log getLog(Class cls) {
		try {
			UserSessionValue us = getKernel().getUserSession();
			return WebSessionManager.getLog(us.dsName, us.logName);
		} catch (Throwable e) {
			return WebSessionManager.getLog(null, null);
		}
	}
	
	public void setUserDecision(int userDecision) {
		this.userDecision = userDecision;
	}
	
	public int getUserDecision() {
		return userDecision;
	}

	public void addCashListener(DataCashListener l) {
		cacheListeners.add(l);
	}

	public void addCashChangeListener(long attrId, CashChangeListener l) {
        List<CashChangeListener> list = cacheChangeListeners.get(attrId);
        if (list == null) {
            list = new ArrayList<CashChangeListener>();
            cacheChangeListeners.put(attrId, list);
        }
        if (!list.contains(l)) {
            list.add(l);
        }
	}
	
	public boolean isReportVisible (WebFrame frm, String action) {
        OrWebPanel p = (OrWebPanel)frm.getPanel();
        ClientOrLang orlang = new ClientOrLang(frm);
        Map<String, Object> vc = new HashMap<String, Object>();
        boolean isVisible = OrCalcRef.setCalculations();
        try {
        	orlang.evaluate(action, vc, frm.getPanelAdapter(), new Stack<String>());
        	Object res = vc.get("RETURN");
            if (res instanceof Boolean)
            	return ((Boolean)res).booleanValue();
            else if (res instanceof Number)
            	return ((Number) res).intValue() == 1 ? true : false;
            } catch (Exception ex) {
                Util.showErrorMessage(p, ex.getMessage(), "Видимость отчета");
            } finally {
                if (isVisible)
                	OrCalcRef.makeCalculations();
            }
        return false;
    }

	public Map<String, Object> readIdCard() {
        idCardData = null;
        
        try {
        	JsonObject res = new JsonObject();
        	session.sendCommand("readIdCard", res);
        } catch (Exception e) {
        	log.error(e, e);
        }
        
        waitFrameAction();
        if (idCardData == null)
            WebSessionManager.releaseSession(session.getId());

        return idCardData;
    }
    
    public void setIdCardData(Map<String, String> params) {
    	idCardData = new HashMap<String, Object>();
    	try {
	    	for (String key : params.keySet()) {
	    		
	    		String param = params.get(key);
	    		Object value = param;
	    		
	    		if ("photo".equals(key)) {
	        		value = kz.tamur.util.Base64.decode(param);
	    		
	    		} else if ("first".equals(key)) {
	    			value = param.replaceAll("<", "");
	    			
	    		} else if ("dob".equals(key)
	    				|| "docExp".equals(key)) {
	    			try {
	    				value = ThreadLocalDateFormat.get("yyMMdd").parse(param);
	    			} catch (ParseException ex) {
	    				log.error("Failed to parse date from ID card: " + key + "=" + param);
	    			}
	    		}
	    		idCardData.put(key, value);
	    	}
    	} catch (Exception e) {
			log.error(e, e);
    	}
        wakeupFrameAction();
    }

	public boolean setData(int count, int num, String data) {
		if (this.data == null)
			this.data = new String[count];
		
		this.data[num] = data;
		
		for (int i = 0; i<this.data.length; i++)
			if (this.data[i] == null) return false;
		
		return true;
	}
	
	public String getData() {
		StringBuilder res = new StringBuilder();
		
		for (int i = 0; i<this.data.length; i++)
			res.append(this.data[i]);
		
		this.data = null;
		
		return res.toString();
	}
}