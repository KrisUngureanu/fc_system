package kz.tamur.rt.adapters;

import com.cifs.or2.client.ClassNode;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.User;
import com.cifs.or2.kernel.*;
import com.cifs.or2.util.MultiMap;

import kz.tamur.SecurityContextHolder;
import kz.tamur.comps.*;
import kz.tamur.guidesigner.reports.ReportPrinter;
import kz.tamur.guidesigner.reports.ReportRecord;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.rt.CheckContext;
import kz.tamur.rt.adapters.OrRef.Item;
import kz.tamur.rt.data.Cache;
import kz.tamur.rt.data.Record;
import kz.tamur.rt.orlang.ClientOrLang;
import kz.tamur.util.Funcs;
import kz.tamur.util.Pair;
import kz.tamur.util.ReqMsgsList;
import kz.tamur.util.ThreadLocalDateFormat;
import kz.tamur.util.ThreadLocalNumberFormat;
import kz.tamur.or3.client.comps.interfaces.OrPanelComponent;
import kz.tamur.or3.client.comps.interfaces.OrTreeTableComponent;
import kz.tamur.or3.server.lang.SystemOp;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.web.common.LangHelper;
import kz.tamur.web.common.webgui.WebMenuItem;
import kz.tamur.web.component.WebFrame;
import kz.tamur.web.controller.WebController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.*;
import java.text.DateFormat;
import java.util.*;
import java.util.Date;

public class ReportPrinterAdapter extends ComponentAdapter implements ReportPrinter {
	private static final Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + ReportPrinterAdapter.class.getName());
    private ReportRecord report;
    private Map<String, String> sortAttributes = new TreeMap<String, String>();
    private Map<String, Boolean> sortAttributesDesc = new TreeMap<String, Boolean>();
    private Kernel KRN_;
    private static ThreadLocalDateFormat format_ = Funcs.getDateFormat();
    private static ThreadLocalDateFormat timeformat_ = Funcs.getDateFormat(2);
    private static ThreadLocalDateFormat formatFull_ = new ThreadLocalDateFormat("d MMMM yyyy", new Locale("ru", "RU"));
    private static String formatFullKaz_ = "d MMMM yyyy";
    private static ThreadLocalNumberFormat floatFormat_ = new ThreadLocalNumberFormat(null, '.', (char)0, false, 6, -1);
    private String reportFileName="";

    private Map<String, Set<Long>> filteredIds_ = new TreeMap<String, Set<Long>>();
    private KrnObject defaultFilter;
    private OrRef ref;
    protected OrFrame frame;
    private OrPanelComponent panel;
    private Map<String, Long> idByUid;
    private boolean isSingleType;

    private TreeMap<String, MultiMap<Long, Item>> globalMap;
    private TreeMap <String, Map<Element, KrnObject>> globalNodeMap;
    private TreeMap<String, Element> globalRoots;
    private Cache cash;
    private KrnAttribute templateAttr;
    private boolean listen = false;
    private boolean showComment;
    private boolean treePrinted = false;
    private boolean printTree = false;
    
    private OrCalcRef visibilityRef;
    private WebMenuItem reportMenuItem;

    public ReportPrinterAdapter() {
    }

    public ReportPrinterAdapter(OrFrame frame, OrPanelComponent panel,
                                ReportRecord report) {
        this.frame = frame;
        KRN_ = frame.getKernel();
        this.panel = panel;
        this.report = report;
        try {
            ref = OrRef.createRef(report.getPath(), false, Mode.RUNTIME, frame.getRefs(),
                        frame.getTransactionIsolation(), frame);

            templateAttr = KRN_.getAttributeByName(
                    KRN_.getClassByName("ReportPrinter"),
                    "template"
            );
            //ref.evaluate(null);
            
        	String visibilityFunc = report.getVisibilityFunc();
        	if (visibilityFunc != null && visibilityFunc.length() > 0) {
        		try {
        			propertyName = "Свойство: Отчет.Видимость";
                    visibilityRef = new OrCalcRef(visibilityFunc, false, Mode.RUNTIME, frame.getRefs(), frame.getTransactionIsolation(), frame, panel, propertyName, this);
                    visibilityRef.addOrRefListener(this);
    	    	} catch (Exception e) {
    		        showErrorNessage(e.getMessage() + visibilityFunc);
    		        log.error(e, e);
    	    	}
        	}

        } catch (KrnException e) {
            log.error(e, e);
        }
    }

    public long getId() {
        return report.getObjId();
    }


    public boolean hasReport(KrnObject lang) {
    	return report.hasLang(lang.id, templateAttr.id, frame.getKernel());
    }


    public File printToFile(KrnObject lang, Date fDate, Date lDate, Date cDate) {
    	if (frame instanceof WebFrame) {
            log.info("|USER: " + ((WebFrame)frame).getSession().getUserName() + "| Path: " + report.getPath());
            log.info("|USER: " + ((WebFrame)frame).getSession().getUserName() + "| Title: " + report.getTitle(lang.id, frame.getKernel()));
    	} else {
    		log.info("Path: " + report.getPath());
    		log.info("Title: " + report.getTitle(lang.id, frame.getKernel()));
    	}
        long langId = lang.id;
        showComment = System.getProperty("showComment") != null;
        try {
            globalMap = new TreeMap<String, MultiMap<Long, Item>>();
            globalNodeMap = new TreeMap<String, Map<Element, KrnObject>>();
            globalRoots = new TreeMap<String, Element>();
            Element root = new Element("Root");
            clearSortAttributes();

            byte[] buf = null;

            Kernel krn = KRN_;
            KrnClass reportCls = krn.getClassByName("ReportPrinter");
            byte[] data = krn.getBlob(report.getObjId(),
                    krn.getAttributeByName(reportCls, "config"),
                    0, 0, 0);

            InputStream is = new ByteArrayInputStream(data);
            SAXBuilder builder = new SAXBuilder();
            Element xml = builder.build(is).getRootElement();
            Element groupType = xml.getChild("groupType");
            if (groupType != null) {
                isSingleType = "true".equals(groupType.getText());
            } else {
                isSingleType = false;
            }

            buf = krn.getBlob(report.getObjId(),
                    krn.getAttributeByName(reportCls, "data"),
                    0, langId, 0);

            defaultFilter = (report.getFilterId() > 0) ?
                    new KrnObject(report.getFilterId(), "",
                    krn.getClassByName("Filter").id) : null;

        	if (doBeforePrint(report.getFunc())) {
	            if (buf != null && buf.length > 0) {
	                if (!createRefsEx(buf, root, fDate, lDate, cDate))
	                    return null;
	            }
	
	            log.info("removing null rows..." + new Date());
	            removeNullRows(root);
	            log.info("sorting..." + new Date());
	            if (getSortAttributes().size() > 0)
	                sort(root, getSortAttributes(), getSortAttributesDesc());
	
	            log.info("Searching getNum() ...");
	            List<Element> colTags = XPath.selectNodes(root,  ".//Column[@numType='1']");
	            if (colTags != null) {
	                log.info("colTags.size() = " + colTags.size());
	                Map<String, Integer> colIds = new HashMap<String, Integer>();
	                for (Element colTag : colTags) {
	                    Integer curRow = colIds.get(colTag.getAttributeValue("id"));
	                    Object o = org.jdom.xpath.XPath.selectSingleNode(colTag, "count(Value/Value)");
	                    if (curRow == null) curRow = 0;
	                    List<Element> valueTags = colTag.getChildren("Value");
	                    for (Element valueTag : valueTags) {
	                    	if (o instanceof Number && ((Number)o).intValue() > 0) {
	                            List<Element> valueTags2 = valueTag.getChildren("Value");
	                            for (Element valueTag2 : valueTags2) {
	                        		valueTag2.setAttribute("str", String.valueOf(++curRow));
	                            }                    		
	                    	} else
	                    		valueTag.setAttribute("str", String.valueOf(++curRow));
	                    }
	                    colIds.put(colTag.getAttributeValue("id"), curRow);
	                }
	            }
	
	            log.info("Searching getNum2() ...");
	            colTags = XPath.selectNodes(root,  ".//Column[@numType='2']");
	            if (colTags != null) {
	                log.info("colTags.size() = " + colTags.size());
	                for (Element colTag : colTags) {
	                    int i = 0;
	                    Object o = org.jdom.xpath.XPath.selectSingleNode(colTag, "count(Value/Value)");
	                    List<Element> valueTags = colTag.getChildren("Value");
	                    for (Element valueTag : valueTags) {
	                    	if (o instanceof Number && ((Number)o).intValue() > 0) {
	                            List<Element> valueTags2 = valueTag.getChildren("Value");
	                            i = 0;
	                            for (Element valueTag2 : valueTags2) {
	                        		valueTag2.setAttribute("str", String.valueOf(++i));
	                            }                    		
	                    	} else
	                    		valueTag.setAttribute("str", String.valueOf(++i));
	                    }
	                }
	            }
	
	            log.info("making files..." + new Date());
	
	            File dir = WebController.WEB_DOCS_DIRECTORY;
	
	            org.jdom.output.Format ft = org.jdom.output.Format.getRawFormat();
	            ft.setEncoding("UTF-8");
	            org.jdom.output.XMLOutputter f = new org.jdom.output.XMLOutputter(ft);
	            File xmlFile = Funcs.createTempFile("xxx", ".xml", dir);
	            ((WebFrame)frame).getSession().deleteOnExit(xmlFile);
	            OutputStream os = new FileOutputStream(xmlFile);
	            f.output(root, os);
	            os.close();
	            root = null;
	
	            String str = xml.getChildText("editorType");
	            int editorType = (str != null && str.length() > 0) ? Integer.valueOf(str) : 0;
	            str = xml.getChildText("macros");
	            String macros = (str != null && str.length() > 0) ? str : "";
	
	            String templatePD = xml.getChildText("templatePassword");
	
	            String suffix = (editorType == Constants.MSWORD_EDITOR)
	                    ? ".doc" : ".xls";
	            if (!"jacob".equals(System.getProperty("reportType"))) suffix += "x";
	
	            buf = krn.getBlob(report.getObjId(),
	                    krn.getAttributeByName(reportCls, "template"),
	                    0, langId, 0);
	
	            String nameFile = Utils.convertToNameFile(this.toString());
	            
	            File docFile = null;
	            int i = 0;
	            do {
	            	docFile = (nameFile != null) ? new File(dir, nameFile + (i++ > 0 ? i : "") + suffix) : Funcs.createTempFile("xxx", suffix, dir);
	            } while (docFile.exists());
	            
	            ((WebFrame)frame).getSession().deleteOnExit(docFile);
	            os = new FileOutputStream(docFile);
	            os.write(buf);
	            os.close();
	
	            log.info("before forming document..." + new Date());
	
	            ReportLauncher.viewReportI(docFile.getAbsolutePath(),
	                    xmlFile.getAbsolutePath(), editorType,
	                    frame.getResourceBundle().getString("formReport"), macros, templatePD);
	            return docFile;
        	}
        } catch (Exception e) {
            log.error(e, e);
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
    	ReportPrinterAdapter a = new ReportPrinterAdapter();
    	
        InputStream is = new FileInputStream("D:/tmp/xxx6134008494139246185.xml");
        SAXBuilder builder = new SAXBuilder();
        Element root = builder.build(is).getRootElement();

        a.setSortAttributes("0", "14", false);
        a.setSortAttributes("1", "15", false);
        
        a.sort(root, a.getSortAttributes(), a.getSortAttributesDesc());
        
        org.jdom.output.Format ft = org.jdom.output.Format.getRawFormat();
        ft.setEncoding("UTF-8");
        org.jdom.output.XMLOutputter f = new org.jdom.output.XMLOutputter(ft);
        
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        f.output(root, os);
        os.close();
        
        System.out.println(os.toString("UTF-8"));
	}
    
    @Override
    public Object printToFile(KrnObject lang) {
    	if (frame instanceof WebFrame) {
            log.info("|USER: " + ((WebFrame)frame).getSession().getUserName() + "| Path: " + report.getPath());
            log.info("|USER: " + ((WebFrame)frame).getSession().getUserName() + "| Title: " + report.getTitle(lang.id, frame.getKernel()));
    	} else {
    		log.info("Path: " + report.getPath());
    		log.info("Title: " + report.getTitle(lang.id, frame.getKernel()));
    	}
        long langId = lang.id;
        showComment = System.getProperty("showComment") != null;
        try {
            globalMap = new TreeMap<String, MultiMap<Long, Item>>();
            globalNodeMap = new TreeMap<String, Map<Element, KrnObject>>();
            globalRoots = new TreeMap<String, Element>();
            Element root = new Element("Root");
            clearSortAttributes();

            byte[] buf;
            Kernel krn = KRN_;

            KrnClass reportCls = krn.getClassByName("ReportPrinter");
            byte[] data = krn.getBlob(report.getObjId(),
                    krn.getAttributeByName(reportCls, "config"),
                    0, 0, 0);

            InputStream is = new ByteArrayInputStream(data);
            SAXBuilder builder = new SAXBuilder();
            Element xml = builder.build(is).getRootElement();
            Element groupType = xml.getChild("groupType");
            isSingleType = groupType != null && "true".equals(groupType.getText());

            String str = xml.getChildText("editorType");
	        int editorType = (str != null && str.length() > 0) ? Integer.valueOf(str) : 0;

            str = xml.getChildText("macros");
	        String macros = (str != null && str.length() > 0) ? str : "";
            String templatePD = xml.getChildText("templatePassword");

            buf = krn.getBlob(report.getObjId(),
                    krn.getAttributeByName(reportCls, "data"),
                    0, langId, 0);

            defaultFilter = (report.getFilterId() > 0) ?
            		new KrnObject(report.getFilterId(), "",
            				krn.getClassByName("Filter").id) : null;
            
    	    reportFileName="";
    	    
        	if (doBeforePrint(report.getFunc())) {
	
	            if (editorType == Constants.JASPER_EDITOR) {
	            	
		            File dir = WebController.WEB_DOCS_DIRECTORY;
		    		
		            String suffix = ".pdf";
		            
		            if (reportFileName==null || "".equals(reportFileName))
		            	reportFileName = Utils.convertToNameFile(this.toString());
		
		            File docFile = null;
		            int i = 0;
		            do {
		            	docFile = (reportFileName != null) ? new File(dir, reportFileName + (i++ > 0 ? i : "") + suffix) : Funcs.createTempFile("xxx", suffix, dir);
		            } while (docFile.exists());
		
		            ((WebFrame)frame).getSession().deleteOnExit(docFile);
		            
		            ClientOrLang orLang = new ClientOrLang(frame);
		            KrnClassOperations cls_ops = KrnClass.getOperations();
		            KrnAttributeOperations attr_ops = KrnAttribute.getOperations();
		            KrnObjectOperations obj_ops = KrnObject.getOperations();

		            Log oldLog = SecurityContextHolder.getLog();
		        	Log log = getLog();

		        	SecurityContextHolder.setLog(log);
		        	KrnClass.setOperations(orLang);
		        	KrnAttribute.setOperations(orLang);
		        	KrnObject.setOperations(orLang);

		            try {
                        Object obj = (isSingleType) ?
                                (ref.getItem(0) != null ? ref.getItem(0).getCurrent() : null) :
                                makeObjectArray(ref.getItems(0));

			            byte[] pdf = ClientOrLang.getSysOp().generateReport(report.getObjId(), obj, lang, "PDF");
			
			            OutputStream os = new FileOutputStream(docFile);
			            os.write(pdf);
			            os.close();
			            
			            return docFile;
		            } finally {
		            	SecurityContextHolder.setLog(oldLog);
		    	        KrnClass.setOperations(cls_ops);
		    	        KrnAttribute.setOperations(attr_ops);
		    	        KrnObject.setOperations(obj_ops);
		            }

	            } else {
		            if (buf != null && buf.length > 0) {
		                if (!createRefsEx(buf, root))
		                    return null;
		            }
		
		            removeNullRows(root);
		            if (getSortAttributes().size() > 0)
		                sort(root, getSortAttributes(), getSortAttributesDesc());
		
		            log.info("Searching getNum() ...");
		            List<Element> colTags = XPath.selectNodes(root,  ".//Column[@numType='1']");
		            if (colTags != null) {
		                log.info("colTags.size() = " + colTags.size());
		                Map<String, Integer> colIds = new HashMap<String, Integer>();
		                for (Element colTag : colTags) {
		                    Integer curRow = colIds.get(colTag.getAttributeValue("id"));
		                    Object o = org.jdom.xpath.XPath.selectSingleNode(colTag, "count(Value/Value)");
		                    if (curRow == null) curRow = 0;
		                    List<Element> valueTags = colTag.getChildren("Value");
		                    for (Element valueTag : valueTags) {
		                    	if (o instanceof Number && ((Number)o).intValue() > 0) {
		                            List<Element> valueTags2 = valueTag.getChildren("Value");
		                            for (Element valueTag2 : valueTags2) {
		                        		valueTag2.setAttribute("str", String.valueOf(++curRow));
		                            }                    		
		                    	} else
		                    		valueTag.setAttribute("str", String.valueOf(++curRow));
		                    }
		                    colIds.put(colTag.getAttributeValue("id"), curRow);
		                }
		            }
		
		            log.info("Searching getNum2() ...");
		            colTags = XPath.selectNodes(root,  ".//Column[@numType='2']");
		            if (colTags != null) {
		                log.info("colTags.size() = " + colTags.size());
		                for (Element colTag : colTags) {
		                    int i = 0;
		                    Object o = org.jdom.xpath.XPath.selectSingleNode(colTag, "count(Value/Value)");
		                    List<Element> valueTags = colTag.getChildren("Value");
		                    for (Element valueTag : valueTags) {
		                    	if (o instanceof Number && ((Number)o).intValue() > 0) {
		                            List<Element> valueTags2 = valueTag.getChildren("Value");
		                            i = 0;
		                            for (Element valueTag2 : valueTags2) {
		                        		valueTag2.setAttribute("str", String.valueOf(++i));
		                            }                    		
		                    	} else
		                    		valueTag.setAttribute("str", String.valueOf(++i));
		                    }
		                }
		            }
		
		            File dir = WebController.WEB_DOCS_DIRECTORY;
		
		            String suffix = (editorType == Constants.MSWORD_EDITOR) ? ".doc" : ".xls";
		            if (!"jacob".equals(System.getProperty("reportType"))) suffix += "x";
		            
		            if(reportFileName==null || "".equals(reportFileName))
		            	reportFileName = Utils.convertToNameFile(this.toString());
		
		            File docFile = null;
		            int i = 0;
		            do {
		            	docFile = (reportFileName != null) ? new File(dir, reportFileName + (i++ > 0 ? i : "") + suffix) : Funcs.createTempFile("xxx", suffix, dir);
		            } while (docFile.exists());
		
		            ((WebFrame)frame).getSession().deleteOnExit(docFile);
		
		            buf = krn.getBlob(report.getObjId(),
		                    krn.getAttributeByName(reportCls, "template"),
		                    0, langId, 0);
		
		            OutputStream os = new FileOutputStream(docFile);
		            os.write(buf);
		            os.close();
		
		            org.jdom.output.Format ft = org.jdom.output.Format.getRawFormat();
		            ft.setEncoding("UTF-8");
		            org.jdom.output.XMLOutputter f = new org.jdom.output.XMLOutputter(ft);
		            File xmlFile = Funcs.createTempFile("xxx", ".xml", dir);
		            ((WebFrame)frame).getSession().deleteOnExit(xmlFile);
		            os = new FileOutputStream(xmlFile);
		            f.output(root, os);
		            os.close();
		
		            ReportLauncher.viewReportI(docFile.getAbsolutePath(),
		                        xmlFile.getAbsolutePath(), editorType,
		                        frame.getResourceBundle().getString("formReport"), macros, templatePD);
		
		            return docFile;
	            }
        	} else
        		return FILE_NOT_PRINTED;
        } catch (Exception e) {
            log.error(e, e);
        }
        return null;
    }

    public long getReportFilters(KrnObject lang) {
        long langId = lang.id;
        try {
            globalMap = new TreeMap<String, MultiMap<Long, Item>>();
            globalNodeMap = new TreeMap<String, Map<Element, KrnObject>>();
            globalRoots = new TreeMap<String, Element>();
            Element root = new Element("Root");

            Kernel krn = KRN_;

            KrnClass reportCls = krn.getClassByName("ReportPrinter");
            byte[] data = krn.getBlob(report.getObjId(),
                    krn.getAttributeByName(reportCls, "config"),
                    0, 0, 0);

            InputStream is = new ByteArrayInputStream(data);
            SAXBuilder builder = new SAXBuilder();
            Element xml = builder.build(is).getRootElement();

            byte[] buf = krn.getBlob(report.getObjId(),
                    krn.getAttributeByName(reportCls, "data"),
                    0, langId, 0);

            defaultFilter = (report.getFilterId() > 0) ?
                    new KrnObject(report.getFilterId(), "",
                    krn.getClassByName("Filter").id) : null;

            if (buf != null && buf.length > 0) {
                return getFilterFlags(buf, root);
            }
        } catch (Exception e) {
            log.error(e, e);
        }
        return 0;
    }

    public String toString() {
        KrnObject lang = (frame.getInterfaceLang() != null)
                        ? frame.getInterfaceLang()
                        : LangHelper.getRusLang(((WebFrame)frame).getSession().getConfigNumber()).obj;
        return report.getTitle(lang.id, frame.getKernel());
    }

    public Map<String, String> getSortAttributes() {
        return sortAttributes;
    }

    public Map<String, Boolean> getSortAttributesDesc() {
        return sortAttributesDesc;
    }

    public void setSortAttributes(String index, String id, boolean desc) {
        this.sortAttributes.put(index, id);
        this.sortAttributesDesc.put(id, desc);
    }

    public void clearSortAttributes() {
        this.sortAttributes.clear();
        this.sortAttributesDesc.clear();
    }

    private boolean createRefsEx(byte[] xml, Element rootElement) {
        List<ReportNode> rns = new ArrayList<ReportNode>();
        Map<String, OrRef> refs = new TreeMap<String, OrRef>();
        List<OrCalcRef> calcRefs = new ArrayList<OrCalcRef>();

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);

            DocumentBuilder db = dbf.newDocumentBuilder();

            Document doc = db.parse(new ByteArrayInputStream(xml));
            //if (ref != null) refs.put(ref.toString(), ref);

            ArrayList<FilterNode> fns = new ArrayList<FilterNode>();
            ArrayList<Filter> filters = new ArrayList<Filter>();

            try {
                processNode(null, doc, refs, calcRefs, rootElement, rns, fns);
                processTreeNode(null, doc, refs, calcRefs, rootElement, rns, fns);
                if (defaultFilter != null) {
                    long f = readFlags(defaultFilter.id);
                    Filter filter = new Filter(defaultFilter, frame.getInterfaceLang().id, "", f);
                    filters.add(filter);
                }
                OrRef ref = getRef();
                for (int i = 0; i < rns.size(); ++i) {
                    if (rns.get(i) instanceof ColumnReportNode) {
                        ColumnReportNode rn = (ColumnReportNode) rns.get(i);
                        if (rn.filter != null) {
                            filters.add(rn.filter);
                        }
                    }
                    else if (rns.get(i) instanceof TreeReportNode) {
                        TreeReportNode rn = (TreeReportNode) rns.get(i);
                        if (rn.filterTree != null) {
                            filters.add(rn.filterTree);
                        }
                    }
                }

                Map<Long, Long> globalFlags = new TreeMap<Long, Long>();
                Map<Integer, com.cifs.or2.kernel.Date> globalDates = new TreeMap<Integer, com.cifs.or2.kernel.Date>();

                if (defaultFilter != null) {
                    applyFilter(ref, defaultFilter, globalFlags, globalDates);
                }
                listen = true;
                idByUid = new HashMap<String, Long>();
                for (int i = 0; i < rns.size(); ++i) {
                    ReportNode rn = (ReportNode) rns.get(i);
                    if (rn instanceof ConsValueReportNode) {
                        ConsValueReportNode cvrn = (ConsValueReportNode) rn;
                        if (cvrn.filter != null) {
                            Long tmp = globalFlags.get(cvrn.filter.obj.id);
                            long f = (tmp != null) ? tmp : 0;
                            idByUid.put(cvrn.getFuid(), cvrn.filter.obj.id);
                            Set<Long> filteredIds = getFilteredIds(cvrn.getFuid(), f, globalDates, idByUid);
                            cvrn.setFilteredIds(filteredIds);
                        }
                    } else if (rn instanceof ConsColumnExReportNode) {
                        ConsColumnExReportNode cvrn = (ConsColumnExReportNode) rn;
                        if (cvrn.filter != null) {
                            Long tmp = globalFlags.get(cvrn.filter.obj.id);
                            long f = (tmp != null) ? tmp : 0;
                            idByUid.put(cvrn.getFuid(), cvrn.filter.obj.id);
                            Set<Long> filteredIds = getFilteredIds(cvrn.getFuid(), f, globalDates, idByUid);
                            cvrn.setFilteredIds(filteredIds);
                        }
                    } else if (rn instanceof ColumnReportNode) {// &&
                        //!(rn instanceof ConsColumnExReportNode)) {
                        ColumnReportNode crn = (ColumnReportNode) rns.get(i);
                        if (crn.filter != null) { // && !crn.tableRef.isFilterInUse(crn.filter)) {
                            Long tmp = globalFlags.get(crn.filter.obj.id);
                            long f = (tmp != null) ? tmp : 0;
                            idByUid.put(crn.getFuid(), crn.filter.obj.id);
                            Set<Long> filteredIds = getFilteredIds(crn.getFuid(), f, globalDates, idByUid);
                            crn.setFilteredIds(filteredIds);
                            for (int m = 0; m < rns.size(); ++m) {
                                ReportNode colrn = (ReportNode) rns.get(m);
                                if (colrn instanceof ColumnReportNode) {
                                    if (((ColumnReportNode)colrn).tableRef == crn.tableRef &&
                                            ((ColumnReportNode)colrn).tableId == crn.tableId) {
                                        ((ColumnReportNode)colrn).setFilteredIds(filteredIds);
                                    }
                                }
                            }
                        }
                        if (crn.innerFilter != null) { // && !crn.tableRef.isFilterInUse(crn.filter)) {
                            Long tmp = globalFlags.get(crn.innerFilter.obj.id);
                            long f = (tmp != null) ? tmp : 0;
                            idByUid.put(crn.innerFuid, crn.innerFilter.obj.id);
                            Set<Long> filteredIds = getFilteredIds(crn.innerFuid, f, globalDates, idByUid);
                            crn.setInnerFilteredIds(filteredIds);
                        }
                    }
                    if (rn instanceof TreeReportNode) {
                        TreeReportNode trn = (TreeReportNode) rn;
                        if (trn.filterTree != null) {
                            Long tmp = globalFlags.get(trn.filterTree.obj.id);
                            long f = (tmp != null) ? tmp : 0;
                            idByUid.put(trn.getTreeFilterUid(), trn.filterTree.obj.id);
                            Set<Long> filteredIds = getFilteredIds(trn.getTreeFilterUid(), f, globalDates, idByUid);
                            trn.setFilteredTreeIds(filteredIds);
                        } else if (trn.tableRef != null && trn.tableRef.getTableAdapter() instanceof TreeTableAdapter) {
                            TreeTableAdapter a = (TreeTableAdapter) trn.tableRef.getTableAdapter();
                            TreeAdapter ta = ((OrTreeTableComponent)a.getTable()).getTreeAdapter();
                            List<Long> filterIds = ta.getFilteredIds();
                            if (filterIds != null && filterIds.size() > 0) {
                                Set<Long> ids = new TreeSet<Long>();
                                ids.addAll(filterIds);
                                trn.setFilteredTreeIds(ids);
                            }
                        }
                    }
                }


                if (fns.size() > 0) {
                    Map<String, Long> flags = new TreeMap<String, Long>();
                    readFilterNodeFlags(fns, flags);
                    Map<Integer, com.cifs.or2.kernel.Date> dates = new TreeMap<Integer, com.cifs.or2.kernel.Date>();
                    for (Iterator<FilterNode> fnIt = fns.iterator(); fnIt.hasNext();)
                        fnIt.next().execute(rootElement, flags, dates, idByUid);
                }

                Set<String> evaluated = new TreeSet<String>();

                for (int i = 0; i < rns.size(); ++i) {
                    ReportNode rn = (ReportNode) rns.get(i);
                    if (rn.ref != null) {
                        if (rn instanceof TreeReportNode) {
                            OrRef root = (((TreeReportNode) rn).tree != null) ? ((TreeReportNode) rn).tree.rootRef.getRoot()
                                    : ((TreeReportNode) rn).tree2.dataRef.getRoot();
                            if (!evaluated.contains(root.toString())) {
                                KrnObject[] objs = null;
                                if (ref != null && ref.getType() != null
                                        && (KRN_.isSubclassOf(root.getType().id, ref.getType().id)
                                            	|| KRN_.isSubclassOf(ref.getType().id, root.getType().id))) {
                                    if (ref.getItems(0).size() == 0 && ref.getAttr() == null && defaultFilter == null)
                                        ref.evaluate((KrnObject[])null);
                                    objs = (isSingleType) ?
                                            makeObjectArray(ref.getSelectedItems()) :
                                            makeObjectArray(ref.getItems(0));
                                }
                                evaluated.add(root.toString());


                                evaluated = addChildrenRoot(rn, evaluated);
/*                            if (root.getSortAttributes() != null &&
                                    root.getSortAttributes().size() > 0) {
                                root.evaluateAndSort(objs, this);
                                root.clearSortAttributes();
                            } else*/
                                printTree = false;
                                if (root.toString().equals(ref.toString()) && (objs == null || objs.length == 0))
                                    root.fireValueChangedEvent(-1, this, 0);
                                else
                                    root.evaluate(objs, this, true);
                                printTree = true;
                                if (root.toString().equals(ref.toString()) && (objs == null || objs.length == 0))
                                    root.fireValueChangedEvent(-1, this, 0);
                                else
                                    root.evaluate(objs, this, true);
                            }
                        }
                        OrRef root = rn.ref.getRoot();
                        if (!evaluated.contains(root.toString())) {
                            KrnObject[] objs = null;
                            if (ref != null && ref.getType() != null
                                    && (KRN_.isSubclassOf(root.getType().id, ref.getType().id)
                                        	|| KRN_.isSubclassOf(ref.getType().id, root.getType().id))) {
                                if (ref.getItems(0).size() == 0 && ref.getAttr() == null && defaultFilter == null)
                                    ref.evaluate((KrnObject[])null);
                                objs = (isSingleType) ?
                                        makeObjectArray(ref.getSelectedItems()) :
                                        makeObjectArray(ref.getItems(0));
                            }
                            evaluated.add(root.toString());


                            evaluated = addChildrenRoot(rn, evaluated);
/*                            if (root.getSortAttributes() != null &&
                                    root.getSortAttributes().size() > 0) {
                                root.evaluateAndSort(objs, this);
                                root.clearSortAttributes();
                            } else*/
                            printTree = false;
                            if (root.toString().equals(ref.toString()) && (objs == null || objs.length == 0))
                                root.fireValueChangedEvent(-1, this, 0);
                            else
                                root.evaluate(objs, this, true);
                            printTree = true;
                            if (root.toString().equals(ref.toString()) && (objs == null || objs.length == 0))
                                root.fireValueChangedEvent(-1, this, 0);
                            else
                                root.evaluate(objs, this, true);
                        }
                    } else if (rn instanceof ColumnReportNode && ((ColumnReportNode)rn).tableRef != null) {
                            if (rn instanceof TreeReportNode) {
                                OrRef root = (((TreeReportNode) rn).tree != null) ? ((TreeReportNode) rn).tree.rootRef.getRoot()
                                        : ((TreeReportNode) rn).tree2.dataRef.getRoot();
                                if (!evaluated.contains(root.toString())) {
                                    KrnObject[] objs = null;
                                    if (ref != null && ref.getType() != null
                                            && (KRN_.isSubclassOf(root.getType().id, ref.getType().id)
                                                	|| KRN_.isSubclassOf(ref.getType().id, root.getType().id))) {
                                        if (ref.getItems(0).size() == 0 && ref.getAttr() == null && defaultFilter == null)
                                            ref.evaluate((KrnObject[])null);
                                        objs = (isSingleType) ?
                                                makeObjectArray(ref.getSelectedItems()) :
                                                makeObjectArray(ref.getItems(0));
                                    }
                                    evaluated.add(root.toString());

                                    evaluated = addChildrenRoot(rn, evaluated);
/*                            if (root.getSortAttributes() != null &&
                                    root.getSortAttributes().size() > 0) {
                                root.evaluateAndSort(objs, this);
                                root.clearSortAttributes();
                            } else*/
                                    printTree = false;
                                    if (root.toString().equals(ref.toString()) && (objs == null || objs.length == 0))
                                        root.fireValueChangedEvent(-1, this, 0);
                                    else
                                        root.evaluate(objs, this, true);
                                    printTree = true;
                                    if (root.toString().equals(ref.toString()) && (objs == null || objs.length == 0))
                                        root.fireValueChangedEvent(-1, this, 0);
                                    else
                                        root.evaluate(objs, this, true);
                                }
                            }
                            OrRef root = ((ColumnReportNode)rn).tableRef.getRoot();
                            if (!evaluated.contains(root.toString())) {
                                KrnObject[] objs = null;
                                if (ref != null && ref.getType() != null
                                        && (KRN_.isSubclassOf(root.getType().id, ref.getType().id)
                                            	|| KRN_.isSubclassOf(ref.getType().id, root.getType().id))) {
                                    if (ref.getItems(0).size() == 0 && ref.getAttr() == null && defaultFilter == null)
                                        ref.evaluate((KrnObject[])null);
                                    objs = (isSingleType) ?
                                            makeObjectArray(ref.getSelectedItems()) :
                                            makeObjectArray(ref.getItems(0));
                                }
                                evaluated.add(root.toString());

                                evaluated = addChildrenRoot(rn, evaluated);
/*                            if (root.getSortAttributes() != null &&
                                    root.getSortAttributes().size() > 0) {
                                root.evaluateAndSort(objs, this);
                                root.clearSortAttributes();
                            } else*/
                                printTree = false;
                                if (root.toString().equals(ref.toString()) && (objs == null || objs.length == 0))
                                    root.fireValueChangedEvent(-1, this, 0);
                                else
                                    root.evaluate(objs, this, true);
                                printTree = true;
                                if (root.toString().equals(ref.toString()) && (objs == null || objs.length == 0))
                                    root.fireValueChangedEvent(-1, this, 0);
                                else
                                    root.evaluate(objs, this, true);
                            }
                        } else if (rn.exprRef != null && !rn.exprRef.hasParents()
                            && rn.calculated && !(rn instanceof ColumnReportNode)
                            && !(rn instanceof ColumnExReportNode)
                            && !(rn instanceof ConsReportNode))
                        rn.print();
/*
                    for (int i = 0; i < rns.size(); ++i) {
                        if (rns.get(i) instanceof ColumnReportNode &&
                                !(rns.get(i) instanceof ConsColumnExReportNode)) {
                            ColumnReportNode rn = (ColumnReportNode) rns.get(i);
                            if (rn.filter != null) {
                                rn.tableRef.removeFilter(rn.filter.obj.id);
                            }
                        }
                    }
*/
                }
            } catch (Exception ex) {
                log.error(ex, ex);
            } finally {
                new ClientOrLang(frame).getDateOp().clearFilterDatesListeners();
                listen = false;
                for (Object rn1 : rns) {
                    ReportNode rn = (ReportNode) rn1;
                    ref.removeOrRefListener(rn);
                }
                clearRefs(refs, calcRefs);
                if (defaultFilter != null) removeDefaultFilter();
                else {
                    //if (getRef().getAttribute() == null && isTable) {
                    //    getRef().evaluate(null, this);
                    //    if (selectedRow > -1)
                    //        getRef().absolute(selectedRow, this);
                    //}
                }
                //ref.returnTableSort();
            }
            filteredIds_.clear();
        } catch (Exception ex) {
            log.error(ex, ex);
        }

        //Funcs.setJepRefs(null);
        return true;
    }

    private boolean createRefsEx(byte[] xml, Element rootElement,
                                 Date fDate, Date lDate, Date cDate) {
        ArrayList<ReportNode> rns = new ArrayList<ReportNode>();
        Map<String, OrRef> refs = new TreeMap<String, OrRef>();
        List<OrCalcRef> calcRefs = new ArrayList<OrCalcRef>();

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);

            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new ByteArrayInputStream(xml));
            //if (ref != null) refs.put(ref.toString(), ref);

            ArrayList<FilterNode> fns = new ArrayList<FilterNode>();
            ArrayList<Filter> filters = new ArrayList<Filter>();

            try {
                processNode(null, doc, refs, calcRefs, rootElement, rns, fns);
                processTreeNode(null, doc, refs, calcRefs, rootElement, rns, fns);
                if (defaultFilter != null) {
                    long f = readFlags(defaultFilter.id);
                    Filter filter = new Filter(defaultFilter, frame.getInterfaceLang().id, "", f);
                    filters.add(filter);
                }
                OrRef ref = getRef();
                for (int i = 0; i < rns.size(); ++i) {
                    if (rns.get(i) instanceof ColumnReportNode) {
                        ColumnReportNode rn = (ColumnReportNode) rns.get(i);
                        if (rn.filter != null) {
                            filters.add(rn.filter);
                        }
                    }
                    else if (rns.get(i) instanceof TreeReportNode) {
                        TreeReportNode rn = (TreeReportNode) rns.get(i);
                        if (rn.filterTree != null) {
                            filters.add(rn.filterTree);
                        }
                    }
                }

                Map<Long, Long> globalFlags = new TreeMap<Long, Long>();
                Map<Integer, com.cifs.or2.kernel.Date> globalDates = new TreeMap<Integer, com.cifs.or2.kernel.Date>();

                if (filters.size() > 0) {
                    if (fDate!=null) {
                        globalDates.put(new Integer(1), kz.tamur.util.Funcs.convertDate(fDate));
                        Element e = new Element("Field");
                        e.setAttribute("id", "-1");
                        e.setAttribute("str", format_.format(fDate));
                        rootElement.addContent(e);
                    }
                    if (lDate!=null) {
                        globalDates.put(new Integer(2), kz.tamur.util.Funcs.convertDate(lDate));
                        Element e = new Element("Field");
                        e.setAttribute("id", "-2");
                        e.setAttribute("str", format_.format(lDate));
                        rootElement.addContent(e);
                    }
                    if (cDate!=null) {
                        globalDates.put(new Integer(0), kz.tamur.util.Funcs.convertDate(cDate));
                        Element e = new Element("Field");
                        e.setAttribute("id", "0");
                        e.setAttribute("str", format_.format(cDate));
                        rootElement.addContent(e);
                    }
                    ClientOrLang ol = new ClientOrLang(frame);
                    ol.getDateOp().setFilterDates(globalDates);
                }

                if (defaultFilter != null) {
                    applyFilter(ref, defaultFilter, globalFlags, globalDates);
                }
                listen = true;
                idByUid = new HashMap<String, Long>();
                for (int i = 0; i < rns.size(); ++i) {
                    ReportNode rn = (ReportNode) rns.get(i);
                    if (rn instanceof ConsValueReportNode) {
                        ConsValueReportNode cvrn = (ConsValueReportNode) rn;
                        if (cvrn.filter != null) {
                            Long tmp = globalFlags.get(cvrn.filter.obj.id);
                            long f = (tmp != null) ? tmp : 0;
                            idByUid.put(cvrn.getFuid(), cvrn.filter.obj.id);
                            Set<Long> filteredIds = getFilteredIds(cvrn.getFuid(), f, globalDates, idByUid);
                            cvrn.setFilteredIds(filteredIds);
                        }
                    } else if (rn instanceof ConsColumnExReportNode) {
                        ConsColumnExReportNode cvrn = (ConsColumnExReportNode) rn;
                        if (cvrn.filter != null) {
                            Long tmp = globalFlags.get(cvrn.filter.obj.id);
                            long f = (tmp != null) ? tmp : 0;
                            idByUid.put(cvrn.getFuid(), cvrn.filter.obj.id);
                            Set<Long> filteredIds = getFilteredIds(cvrn.getFuid(), f, globalDates, idByUid);
                            cvrn.setFilteredIds(filteredIds);
                        }
                    } else if (rn instanceof ColumnReportNode) {// &&
                        //!(rn instanceof ConsColumnExReportNode)) {
                        ColumnReportNode crn = (ColumnReportNode) rns.get(i);
                        if (crn.filter != null) {
                            Long tmp = globalFlags.get(crn.filter.obj.id);
                            long f = (tmp != null) ? tmp : 0;
                            idByUid.put(crn.getFuid(), crn.filter.obj.id);
                            Set<Long> filteredIds = getFilteredIds(crn.getFuid(), f, globalDates, idByUid);
                            crn.setFilteredIds(filteredIds);

                            for (int m = 0; m < rns.size(); ++m) {
                                ReportNode colrn = (ReportNode) rns.get(m);
                                if (colrn instanceof ColumnReportNode) {
                                    if (((ColumnReportNode)colrn).tableRef == crn.tableRef &&
                                            ((ColumnReportNode)colrn).tableId == crn.tableId) {
                                        ((ColumnReportNode)colrn).setFilteredIds(filteredIds);
                                    }
                                }
                            }
                        }
                        if (crn.innerFilter != null) {
                            Long tmp = globalFlags.get(crn.innerFilter.obj.id);
                            long f = (tmp != null) ? tmp : 0;
                            idByUid.put(crn.innerFuid, crn.innerFilter.obj.id);
                            Set<Long> filteredIds = getFilteredIds(crn.innerFuid, f, globalDates, idByUid);
                            crn.setInnerFilteredIds(filteredIds);
                        }
                    }
                    if (rn instanceof TreeReportNode) {
                        TreeReportNode trn = (TreeReportNode) rn;
                        if (trn.filterTree != null) {
                            Long tmp = globalFlags.get(trn.filterTree.obj.id);
                            long f = (tmp != null) ? tmp : 0;
                            idByUid.put(trn.getTreeFilterUid(), trn.filterTree.obj.id);
                            Set<Long> filteredIds = getFilteredIds(trn.getTreeFilterUid(), f, globalDates, idByUid);
                            trn.setFilteredTreeIds(filteredIds);
                        } else if (trn.tableRef != null && trn.tableRef.getTableAdapter() instanceof TreeTableAdapter) {
                            TreeTableAdapter a = (TreeTableAdapter) trn.tableRef.getTableAdapter();
                            TreeAdapter ta = ((OrTreeTableComponent)a.getTable()).getTreeAdapter();
                            List<Long> filterIds = ta.getFilteredIds();
                            if (filterIds != null && filterIds.size() > 0) {
                                Set<Long> ids = new TreeSet<Long>();
                                ids.addAll(filterIds);
                                trn.setFilteredTreeIds(ids);
                            }
                        }
                    }
                }

                if (fns.size() > 0) {
                    Map<String, Long> flags = new TreeMap<String, Long>();
                    Map<Integer, com.cifs.or2.kernel.Date> dates = new TreeMap<Integer, com.cifs.or2.kernel.Date>();
                    if (fDate!=null) {
                        dates.put(new Integer(1), kz.tamur.util.Funcs.convertDate(fDate));
                        Element e = new Element("FilterDate");
                        e.setAttribute("type", "1");
                        e.setAttribute("str", format_.format(fDate));
                        rootElement.addContent(e);
                        e = new Element("Field");
                        e.setAttribute("id", "-1");
                        e.setAttribute("str", format_.format(fDate));
                        rootElement.addContent(e);
                    }
                    if (lDate!=null) {
                        dates.put(new Integer(2), kz.tamur.util.Funcs.convertDate(lDate));
                        Element e = new Element("FilterDate");
                        e.setAttribute("type", "2");
                        e.setAttribute("str", format_.format(lDate));
                        rootElement.addContent(e);
                        e = new Element("Field");
                        e.setAttribute("id", "-2");
                        e.setAttribute("str", format_.format(lDate));
                        rootElement.addContent(e);
                    }
                    if (cDate!=null) {
                        dates.put(new Integer(1), kz.tamur.util.Funcs.convertDate(cDate));
                        Element e = new Element("FilterDate");
                        e.setAttribute("type", "0");
                        e.setAttribute("str", format_.format(cDate));
                        rootElement.addContent(e);
                        e = new Element("Field");
                        e.setAttribute("id", "0");
                        e.setAttribute("str", format_.format(cDate));
                        rootElement.addContent(e);
                    }
                    ClientOrLang ol = new ClientOrLang(frame);
                    ol.getDateOp().setFilterDates(dates);
                    for (Iterator<FilterNode> fnIt = fns.iterator(); fnIt.hasNext();)
                        fnIt.next().execute(rootElement, flags, dates, idByUid);
                }

                Set<String> evaluated = new TreeSet<String>();

                for (int i = 0; i < rns.size(); ++i) {
                    ReportNode rn = (ReportNode) rns.get(i);
                    if (rn.ref != null) {
                        if (rn instanceof TreeReportNode) {
                            OrRef root = (((TreeReportNode) rn).tree != null) ? ((TreeReportNode) rn).tree.rootRef.getRoot()
                                    : ((TreeReportNode) rn).tree2.dataRef.getRoot();
                            if (!evaluated.contains(root.toString())) {
                                KrnObject[] objs = null;
                                if (ref != null && ref.getType() != null
                                        && (KRN_.isSubclassOf(root.getType().id, ref.getType().id)
                                            	|| KRN_.isSubclassOf(ref.getType().id, root.getType().id))) {
                                    if (ref.getItems(0).size() == 0 && ref.getAttr() == null && defaultFilter == null)
                                        ref.evaluate(null);
                                    objs = (isSingleType) ?
                                            makeObjectArray(ref.getSelectedItems()) :
                                            makeObjectArray(ref.getItems(0));
                                }
                                evaluated.add(root.toString());

                                evaluated = addChildrenRoot(rn, evaluated);
/*                            if (root.getSortAttributes() != null &&
                                    root.getSortAttributes().size() > 0) {
                                root.evaluateAndSort(objs, this);
                                root.clearSortAttributes();
                            } else*/
                                printTree = false;
                                if (root.toString().equals(ref.toString()) && (objs == null || objs.length == 0))
                                    root.fireValueChangedEvent(-1, this, 0);
                                else
                                    root.evaluate(objs, this, true);
                                printTree = true;
                                if (root.toString().equals(ref.toString()) && (objs == null || objs.length == 0))
                                    root.fireValueChangedEvent(-1, this, 0);
                                else
                                    root.evaluate(objs, this, true);
                            }
                        }
                        OrRef root = rn.ref.getRoot();
                        if (!evaluated.contains(root.toString())) {
                            KrnObject[] objs = null;
                            if (ref != null && ref.getType() != null
                                    && (KRN_.isSubclassOf(root.getType().id, ref.getType().id)
                                        	|| KRN_.isSubclassOf(ref.getType().id, root.getType().id))) {
                                if (ref.getItems(0).size() == 0 && ref.getAttr() == null && defaultFilter == null)
                                    ref.evaluate(null);
                                objs = (isSingleType) ?
                                        makeObjectArray(ref.getSelectedItems()) :
                                        makeObjectArray(ref.getItems(0));
                            }
                            evaluated.add(root.toString());

                            evaluated = addChildrenRoot(rn, evaluated);
/*                            if (root.getSortAttributes() != null &&
                                    root.getSortAttributes().size() > 0) {
                                root.evaluateAndSort(objs, this);
                                root.clearSortAttributes();
                            } else*/
                            printTree = false;
                            if (root.toString().equals(ref.toString()) && (objs == null || objs.length == 0))
                                root.fireValueChangedEvent(-1, this, 0);
                            else
                                root.evaluate(objs, this, true);
                            printTree = true;
                            if (root.toString().equals(ref.toString()) && (objs == null || objs.length == 0))
                                root.fireValueChangedEvent(-1, this, 0);
                            else
                                root.evaluate(objs, this, true);
                        }
                    } else if (rn instanceof ColumnReportNode && ((ColumnReportNode)rn).tableRef != null) {
                            if (rn instanceof TreeReportNode) {
                                OrRef root = (((TreeReportNode) rn).tree != null) ? ((TreeReportNode) rn).tree.rootRef.getRoot()
                                        : ((TreeReportNode) rn).tree2.dataRef.getRoot();
                                if (!evaluated.contains(root.toString())) {
                                    KrnObject[] objs = null;
                                    if (ref != null && ref.getType() != null
                                            && (KRN_.isSubclassOf(root.getType().id, ref.getType().id)
                                                	|| KRN_.isSubclassOf(ref.getType().id, root.getType().id))) {
                                        if (ref.getItems(0).size() == 0 && ref.getAttr() == null && defaultFilter == null)
                                            ref.evaluate(null);
                                        objs = (isSingleType) ?
                                                makeObjectArray(ref.getSelectedItems()) :
                                                makeObjectArray(ref.getItems(0));
                                    }
                                    evaluated.add(root.toString());

                                    evaluated = addChildrenRoot(rn, evaluated);
/*                            if (root.getSortAttributes() != null &&
                                    root.getSortAttributes().size() > 0) {
                                root.evaluateAndSort(objs, this);
                                root.clearSortAttributes();
                            } else*/
                                    printTree = false;
                                    if (root.toString().equals(ref.toString()) && (objs == null || objs.length == 0))
                                        root.fireValueChangedEvent(-1, this, 0);
                                    else
                                        root.evaluate(objs, this, true);
                                    printTree = true;
                                    if (root.toString().equals(ref.toString()) && (objs == null || objs.length == 0))
                                        root.fireValueChangedEvent(-1, this, 0);
                                    else
                                        root.evaluate(objs, this, true);
                                }
                            }
                            OrRef root = ((ColumnReportNode)rn).tableRef.getRoot();
                            if (!evaluated.contains(root.toString())) {
                                KrnObject[] objs = null;
                                if (ref != null && ref.getType() != null
                                        && (KRN_.isSubclassOf(root.getType().id, ref.getType().id)
                                            	|| KRN_.isSubclassOf(ref.getType().id, root.getType().id))) {
                                    if (ref.getItems(0).size() == 0 && ref.getAttr() == null && defaultFilter == null)
                                        ref.evaluate(null);
                                    objs = (isSingleType) ?
                                            makeObjectArray(ref.getSelectedItems()) :
                                            makeObjectArray(ref.getItems(0));
                                }
                                evaluated.add(root.toString());

                                evaluated = addChildrenRoot(rn, evaluated);
/*                            if (root.getSortAttributes() != null &&
                                    root.getSortAttributes().size() > 0) {
                                root.evaluateAndSort(objs, this);
                                root.clearSortAttributes();
                            } else*/
                                printTree = false;
                                if (root.toString().equals(ref.toString()) && (objs == null || objs.length == 0))
                                    root.fireValueChangedEvent(-1, this, 0);
                                else
                                    root.evaluate(objs, this, true);
                                printTree = true;
                                if (root.toString().equals(ref.toString()) && (objs == null || objs.length == 0))
                                    root.fireValueChangedEvent(-1, this, 0);
                                else
                                    root.evaluate(objs, this, true);
                            }
                        } else if (rn.exprRef != null && !rn.exprRef.hasParents()
                            && rn.calculated && !(rn instanceof ColumnReportNode)
                            && !(rn instanceof ColumnExReportNode)
                            && !(rn instanceof ConsReportNode))
                        rn.print();
/*
                    for (int i = 0; i < rns.size(); ++i) {
                        if (rns.get(i) instanceof ColumnReportNode &&
                                !(rns.get(i) instanceof ConsColumnExReportNode)) {
                            ColumnReportNode rn = (ColumnReportNode) rns.get(i);
                            if (rn.filter != null) {
                                rn.tableRef.removeFilter(rn.filter.obj.id);
                            }
                        }
                    }
*/
                }
            } catch (Exception ex) {
                log.error(ex, ex);
            } finally {
                log.info("before end of createRefsEx" + new Date());
                new ClientOrLang(frame).getDateOp().clearFilterDatesListeners();
                listen = false;
                ref.returnTableSort();
                for (Object rn1 : rns) {
                    ReportNode rn = (ReportNode) rn1;
                    ref.removeOrRefListener(rn);
                }
                clearRefs(refs, calcRefs);
                if (defaultFilter != null) removeDefaultFilter();

                log.info("end of createRefsEx" + new Date());
                //ref.returnTableSort();
            }
            filteredIds_.clear();
        } catch (Exception ex) {
            log.error(ex, ex);
        }

        //Funcs.setJepRefs(null);
        return true;
    }

    private long getFilterFlags(byte[] xml, Element rootElement) {
        ArrayList<ReportNode> rns = new ArrayList<ReportNode>();
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);

            DocumentBuilder db = null;
            db = dbf.newDocumentBuilder();

            Document doc = db.parse(new ByteArrayInputStream(xml));
            //doc = db.parse(new org.xml.sax.InputSource(new StringReader(xxx)));
            Map<String, OrRef> refs = new TreeMap<String, OrRef>();
            List<OrCalcRef> calcRefs = new ArrayList<OrCalcRef>();
            if (ref != null) refs.put(ref.toString(), ref);
            //Funcs.setJepRefs(refs);
            ArrayList<FilterNode> fns = new ArrayList<FilterNode>();
            ArrayList<Filter> filters = new ArrayList<Filter>();

            try {
                processNode(null, doc, refs, calcRefs, rootElement, rns, fns);
                if (defaultFilter != null) {
                    long f = readFlags(defaultFilter.id);
                    Filter filter = new Filter(defaultFilter, frame.getInterfaceLang().id, "", f);
                    filters.add(filter);
                }
                for (int i = 0; i < rns.size(); ++i) {
                    if (rns.get(i) instanceof ColumnReportNode) {
                        ColumnReportNode rn = (ColumnReportNode) rns.get(i);
                        if (rn.filter != null) {
                            filters.add(rn.filter);
                        }
                    }
                    else if (rns.get(i) instanceof TreeReportNode) {
                        TreeReportNode rn = (TreeReportNode) rns.get(i);
                        if (rn.filterTree != null) {
                            filters.add(rn.filterTree);
                        }
                    }
                }

                Map<Long, Long> globalFlags = new TreeMap<Long, Long>();

                long f1 = 0, f2 = 0;
                if (filters.size() > 0) {
                    f1 = readFlags(filters, globalFlags);
                }
                if (fns.size() > 0) {
                    Map<String, Long> flags = new TreeMap<String, Long>();
                    f2 = readFilterNodeFlags(fns, flags);
                }
                return f1 | f2;
            } catch (Exception ex) {
                log.error(ex, ex);
            } finally {
                for (Object rn1 : rns) {
                    ReportNode rn = (ReportNode) rn1;
                    ref.removeOrRefListener(rn);
                }
                clearRefs(refs, calcRefs);
                if (defaultFilter != null) removeDefaultFilter();
            }
        } catch (Exception ex) {
            log.error(ex, ex);
        }
        return 0;
    }

    private boolean doBeforePrint(String action) throws Exception {
        if (action != null && action.trim().length() > 0) {
            ClientOrLang orlang = new ClientOrLang(frame);
            Map<String, Object> vc = new HashMap<String, Object>();
            OrRef p = getRef();
            if (p != null && p.getItem(0) != null) {
                Object obj = p.getItem(0).getCurrent();
                vc.put("SELOBJ", obj);
            }
            orlang.evaluate(action, vc, null, new Stack<String>());
            reportFileName = (String)vc.get("REPORT_FILE_NAME");

            Object res = vc.get("RETURN");
            return (res == null || Boolean.TRUE.equals(res) || (res instanceof Number && ((Number)res).intValue() == 1));
        }
        return true;
    }

    private void clearRefs(Map<String, OrRef> refs, List<OrCalcRef> calcRefs) {
        for (Iterator<String> it = refs.keySet().iterator(); it.hasNext(); ) {
            String key = it.next();
            OrRef ref = refs.get(key);
            if (ref != getRef() && ref.getParent() != null) {
                ref.getParent().removeChild(ref);
            }
        }
        for (int i=0; i < calcRefs.size(); i++) {
            OrCalcRef ref = calcRefs.get(i);
            if (ref != null) {
                ref.removeFromParents();
            }
        }
    }

    private void applyFilter(OrRef ref, KrnObject filter, Map<Long, Long> flags, Map<Integer, com.cifs.or2.kernel.Date> dates) throws KrnException {
        Long tmp = flags.get(filter.id);
        long f = (tmp != null) ? tmp : 0;
        FilterDate[] fds = getFilterDates(filter.id, f, dates);
        ref.addFilter(filter.id, fds, this);
    }

    private FilterDate[] getFilterDates(long fid, long flag, Map<Integer, com.cifs.or2.kernel.Date> dates) {
        FilterDate[] fds = new FilterDate[countDates(flag)];
        int j = 0;
        if ((flag & 1) > 0) {
            com.cifs.or2.kernel.Date date = (com.cifs.or2.kernel.Date) dates.get(0);
            fds[j++] = new FilterDate(fid, 0, date);
        }
        if ((flag & 2) > 0) {
            com.cifs.or2.kernel.Date date = (com.cifs.or2.kernel.Date) dates.get(1);
            fds[j++] = new FilterDate(fid, 1, date);
        }
        if ((flag & 4) > 0) {
            com.cifs.or2.kernel.Date date = (com.cifs.or2.kernel.Date) dates.get(2);
            fds[j] = new FilterDate(fid, 2, date);
        }
        return fds;
    }

    private void removeDefaultFilter() throws KrnException {
        getRef().removeFilter(defaultFilter.id, this);
    }

    private Set<String> addChildrenRoot(ReportNode rn, Set<String> evaluated) {
        try {
            OrRef ref = getRef();
            for (int k = 0; k < rn.children.size(); k++) {
                ReportNode ch = rn.children.get(k);
                OrRef root;
                if (ch.ref != null) {
                    root = ch.ref.getRoot();
                    if (!evaluated.contains(root.toString())) {
                        KrnObject[] objs = null;
                        if (ref != null && ref.getType().id == root.getType().id) {
                            objs = (isSingleType) ?
                                    makeObjectArray(ref.getSelectedItems()) :
                                    makeObjectArray(ref.getItems(0));
                        }
                        printTree = false;
                        root.evaluate(objs, this, true);
                        printTree = true;
                        root.evaluate(objs, this, true);
                        evaluated.add(root.toString());
                    }
                }
                evaluated = addChildrenRoot(ch, evaluated);
            }
        } catch (KrnException ex) {
            log.error(ex, ex);
        }
        return evaluated;
    }


    private void processNode(ReportNode parent, Node node, Map<String, OrRef> refs, List<OrCalcRef> calcRefs,
                             Element parentNode, Collection<ReportNode> rns, Collection<FilterNode> fns)
            throws KrnException {
        ReportNode newParent = parent;

        String name = node.getNodeName();
        if (name.equals("Field")) {
            try {
                ReportNode rn;
                Node tempNode = node.getAttributes().getNamedItem("sysPath");
                if (tempNode != null)
                    rn = new SystemReportNode(node, parentNode);
                else
                    rn = new ReportNode(parent, node, refs, calcRefs, parentNode, frame);
                rns.add(rn);
            } catch (Exception ex) {
            	String id = node.getAttributes().getNamedItem("id").getNodeValue();
            	log.error("Field id = " + id);
                log.error(ex, ex);
            }
        } else if (name.equals("User")) {
            rns.add(new UserNode(node, parentNode));
        } else if (name.equals("Base")) {
            rns.add(new BaseNode(node, parentNode));
        } else if (name.equals("Department")) {
            rns.add(new DepNode(node, parentNode));
        } else if (name.equals("RowColumn")) {
            try {
                ReportNode rn = new ReportNode(parent, node, refs, calcRefs, parentNode, frame);
                rn.needsNulls = false;
                rns.add(rn);
            } catch (Exception ex) {
            	String id = node.getAttributes().getNamedItem("id").getNodeValue();
            	log.error("RowColumn id = " + id);
                log.error(ex, ex);
            }
        } else if (name.equals("Column")) {
            rns.add(new ColumnReportNode(parent, node, refs, calcRefs, parentNode, frame));
        } else if (name.equals("ColumnEx")) {
            rns.add(newParent = new ColumnExReportNode(parent, node, refs, calcRefs, parentNode, frame));
        } else if (name.equals("ConsColumnEx"))
            rns.add(newParent = new ConsColumnExReportNode(parent, node, refs, calcRefs, parentNode, frame));
        else if (name.equals("TreeColumn")) {
            return;
        } else if (name.equals("ConsColumn"))
            rns.add(newParent = new ConsReportNode(parent, node, parentNode, frame));
        else if (name.equals("ConsValue"))
            rns.add(new ConsValueReportNode(parent, node, refs, calcRefs, parentNode, frame));
        else if (name.equals("Filter")) {
            fns.add(processFilterField(node));
        }

        // Обрабатываем детей
        for (int i = 0; i < node.getChildNodes().getLength(); ++i) {
            Node child = node.getChildNodes().item(i);
            processNode(newParent, child, refs, calcRefs, parentNode, rns, fns);
        }
        for (int i = 0; i < node.getChildNodes().getLength(); ++i) {
            Node child = node.getChildNodes().item(i);
            processTreeNode(newParent, child, refs, calcRefs, parentNode, rns, fns);
        }
    }

    private void processTreeNode(ReportNode parent, Node node, Map<String, OrRef> refs, List<OrCalcRef> calcRefs,
                             Element parentNode, Collection<ReportNode> rns, Collection<FilterNode> fns)
            throws KrnException {
        ReportNode newParent = parent;

        String name = node.getNodeName();
        if (name.equals("TreeColumn")) {
            rns.add(newParent = new TreeReportNode(parent, node, refs, calcRefs, parentNode, frame));
        } else return;

        // Обрабатываем детей
        for (int i = 0; i < node.getChildNodes().getLength(); ++i) {
            Node child = node.getChildNodes().item(i);
            processNode(newParent, child, refs, calcRefs, parentNode, rns, fns);
        }
        for (int i = 0; i < node.getChildNodes().getLength(); ++i) {
            Node child = node.getChildNodes().item(i);
            processTreeNode(newParent, child, refs, calcRefs, parentNode, rns, fns);
        }
    }

    private long readFilterNodeFlags(Collection<FilterNode> fns, Map<String, Long> flags) throws KrnException {
        long res = 0;
        Set<String> filterUids = new TreeSet<String>();
        for (Iterator<FilterNode> fnIt = fns.iterator(); fnIt.hasNext();) {
            FilterNode fn = fnIt.next();
            if (!fn.fuid1.equals("0"))
                filterUids.add(fn.fuid1);
            if (!fn.fuid2.equals("0"))
                filterUids.add(fn.fuid2);
        }
        KrnObject[] objs = KRN_.getObjectsByUid(filterUids.toArray(new String[filterUids.size()]), 0);
        idByUid = new HashMap<String, Long>();
        for (KrnObject obj : objs) {
            idByUid.put(obj.uid, obj.id);
        }
        Map<Long, String> rmap = new HashMap<Long, String>(idByUid.size());
        for (Iterator<String> keyIt = idByUid.keySet().iterator(); keyIt.hasNext();) {
            String key = keyIt.next();
            rmap.put(idByUid.get(key), key);
        }
        long[] fids = Funcs.makeLongArray(idByUid.values());
        KrnClass fcls = KRN_.getClassByName("Filter");
        LongValue lvs[] = KRN_.getLongValues(fids, fcls.id, "dateSelect", 0);
        for (int i = 0; i < lvs.length; i++) {
            long flag = lvs[i].value;
            if (flag > 0) {
                res = res | flag;
                flags.put(rmap.get(lvs[i].objectId), flag);
            }
        }
        return res;
    }


    private long readFlags(ArrayList<Filter> filters, Map<Long, Long> flags) throws KrnException {
        long res = 0;
        long[] fids = new long[filters.size()];
        for (int i = 0; i < filters.size(); i++) {
            fids[i] = filters.get(i).obj.id;
        }


        KrnClass fcls = KRN_.getClassByName("Filter");
        LongValue lvs[] = KRN_.getLongValues(fids, fcls.id, "dateSelect", 0);
        for (int i = 0; i < lvs.length; i++) {
            long flag = lvs[i].value;
            if (flag > 0) {
                res = res | flag;
                flags.put(new Long(lvs[i].objectId), new Long(flag));
            }
        }
        return res;
    }


    private long readFlags(long fid) throws KrnException {
        long res = 0;


        long[] fids = new long[]{fid};
        KrnClass fcls = KRN_.getClassByName("Filter");
        LongValue lvs[] = KRN_.getLongValues(fids, fcls.id, "dateSelect", 0);
        for (int i = 0; i < lvs.length; i++) {
            long flag = lvs[i].value;
            if (flag > 0) {
                res = flag;
            }
        }
        return res;
    }


    private FilterNode processFilterField(Node n) {
        String fuid1 = n.getAttributes().getNamedItem("filter1").getNodeValue();
        String fuid2 = n.getAttributes().getNamedItem("filter2").getNodeValue();
        Node node = n.getAttributes().getNamedItem("attr");


        String attr = (node != null) ? node.getNodeValue() : "";


        node = n.getAttributes().getNamedItem("id");
        if (node != null) {
            int id =
                    Integer.parseInt(node.getNodeValue());
            return new FilterNode(id, fuid1, fuid2, attr);
        } else {
            node = n.getAttributes().getNamedItem("cell");
            String cell = node.getNodeValue();
            node = n.getAttributes().getNamedItem("sheet");
            String sheet = node.getNodeValue();
            return new FilterNode(-1, fuid1, fuid2, attr, cell, sheet);
        }


    }


    class FilterNode {
        int id;
        String fuid1;
        String fuid2;
        String attr;
        String addr;
        String sheet;
        private String list;


        public FilterNode(int id, String fuid1, String fuid2, String attr) {
            this.id = id;
            this.fuid1 = fuid1;
            this.fuid2 = fuid2;
            this.attr = attr;
        }


        public FilterNode(int id, String fuid1, String fuid2, String attr,
                          String addr, String sheet) {
            this.id = id;
            this.fuid1 = fuid1;
            this.fuid2 = fuid2;
            this.attr = attr;
            this.addr = addr;
            this.sheet = sheet;
        }


        public void execute(Element parentElement,
                            Map<String, Long> flags,
                            Map<Integer, com.cifs.or2.kernel.Date> dates,
                            Map<String, Long> m) throws KrnException {
            Set<Long> ids = null, ids1 = new TreeSet<Long>(), ids2 = new TreeSet<Long>();


            if (!fuid1.equals("0")) {
                Long tmp = flags.get(fuid1);
                long f = (tmp != null) ? tmp : 0;
                ids1 = getFilteredIds(fuid1, f, dates, m);
                ids = new TreeSet<Long>(ids1);
            }


            if (!fuid2.equals("0")) {
                Long tmp = flags.get(fuid2);
                long f = (tmp != null) ? tmp.longValue() : 0;
                ids2 = getFilteredIds(fuid2, f, dates, m);
                if (!fuid1.equals("0"))
                    ids.retainAll(ids2);
                else
                    ids = new TreeSet<Long>(ids2);
            }


            if (showComment) {
                final Kernel krn = KRN_;
                long[] oids = Funcs.makeLongArray(ids);
                if (oids != null && oids.length > 0) {
                    KrnObject obj = krn.getObjectsByIds(new long[] {oids[0]},-1)[0];
                    KrnClass cls = krn.getClassNode(obj.classId).getKrnClass();
                    if (cls.name.equals("Персонал")) {
                        KrnAttribute attr1 = krn.getAttributeByName(cls, "текущ  состояние -зап табл персон данных-");
                        KrnClass cls1 = krn.getClassNode(attr1.typeClassId).getKrnClass();
                        KrnAttribute attr2 = krn.getAttributeByName(cls1, "идентиф -фамилия с инициалами-");


                        ObjectValue[] ovs = krn.getObjectValues(oids, attr1, 0);
                        for (int i = 0; i < ovs.length; ++i)
                            oids[i] = (ovs[i].value.id);


                        long ruId = LangHelper.getRusLang(((WebFrame)frame).getSession().getConfigNumber()).obj.id;
                        StringValue[] svs = krn.getStringValues(oids, attr2, ruId, false, 0);
                        if (svs != null && svs.length > 0) {
                            StringBuffer list = new StringBuffer();
                            list.append(svs[0].value);
                            for (int i = 1; i < svs.length; ++i)
                                list.append("\r\n" + svs[i].value);

                            this.list = list.toString();
                        }
                    }
                }
            }

            if (attr.length() == 0) {
                Element e;
                if (id > -1) {
                    e = new Element("Filter");
                    e.setAttribute("id", String.valueOf(id));
                    e.setAttribute("str", "" + ids.size());
                    if (list != null) {
                        e.setAttribute("list", list);
                    }
                } else {
                    e = new Element("Filter2");
                    e.setAttribute("cell", addr);
                    e.setAttribute("sheet", sheet);
                    e.setAttribute("str", "" + ids.size());
                    if (list != null) {
                        e.setAttribute("list", list);
                    }
                }
                parentElement.addContent(e);
            } else {
                long[] objIds = Funcs.makeLongArray(ids);

                final Kernel krn = KRN_;

                StringTokenizer st = new StringTokenizer(attr, ".");
                double res = 0.0;
                Object resObj = "";

                if (st.hasMoreTokens()) {
                    String className = st.nextToken();
                    KrnClass cls = krn.getClassByName(className);

                    while (st.hasMoreTokens()) {
                        String attrName = st.nextToken();
                        KrnAttribute at = krn.getAttributeByName(cls, attrName);
                        cls = krn.getClassNode(at.typeClassId).getKrnClass();
                        if (cls.name.equals("String") || cls.name.equals("string")
                                || cls.name.equals("Memo") || cls.name.equals("memo")) {
                            int lid = (cls.name.equals("String") || cls.name.equals("Memo")) ?
                                    102 : 0;
                            boolean isMemo = (cls.name.equals("memo") || cls.name.equals("Memo")) ?
                                    true : false;

                            StringValue[] svs = krn.getStringValues(objIds, at, lid, isMemo, 0);
                            if (svs != null) {
                                if (svs.length == 0) {
                                    resObj = "-";
                                } else {
                                    String[] strs = new String[svs.length];
                                    for (int k = 0; k < svs.length; k++)
                                        strs[k] = svs[k].value;
                                    if (strs.length == 1)
                                        resObj = trimNumber(strs[0]);
                                    else {
                                        Arrays.sort(strs);
                                        for (int k = 0; k < strs.length; k++)
                                            strs[k] = trimNumber(strs[k]);

                                        resObj = makeOrder(strs);
                                    }
                                }
                            }
                        } else if (cls.name.equals("integer")) {
                            LongValue[] lvs = krn.getLongValues(objIds, at, 0);
                            if (lvs != null) {
                                for (int k = 0; k < lvs.length; k++)
                                    res += lvs[k].value;
                            }
                            resObj = new Integer((int) res);
                        } else if (cls.name.equals("float")) {
                            FloatValue[] fvs = krn.getFloatValues(objIds, at, 0);
                            if (fvs != null) {
                                for (int k = 0; k < fvs.length; k++)
                                    res += fvs[k].value;
                            }
                            resObj = new Double(res);
                        } else {
                            ObjectValue[] ovs = krn.getObjectValues(objIds, at, 0);
                            if (ovs != null) {
                                objIds = new long[ovs.length];
                                List<Long> map = new ArrayList<Long>();
                                for (int k = 0; k < ovs.length; k++) {
                                    objIds[k] = ovs[k].value.id;
                                    if (!map.contains(new Long(objIds[k])))
                                        map.add(objIds[k]);
                                }
                                resObj = map.size();
                            }
                        }
                    }
                }
                Element e;
                if (id > -1) {
                    e = new Element("Filter");
                    e.setAttribute("id", String.valueOf(id));
                    e.setAttribute("str", "" + resObj);
                } else {
                    e = new Element("Filter2");
                    e.setAttribute("cell", addr);
                    e.setAttribute("sheet", sheet);
                    e.setAttribute("str", "" + resObj);
                }
                parentElement.addContent(e);
            }
        }


        private String trimNumber(String number) {
            String str = number.substring(number.lastIndexOf('-') + 1);
            return Integer.toString(Integer.parseInt(str));
        }


        private String makeOrder(String[] numbers) {
            StringBuffer res = new StringBuffer();
            int firstNumber = Integer.parseInt(numbers[0]);
            int delta = 1;
            res.append(firstNumber);
            for (int k = 1; k < numbers.length; k++) {
                int number = Integer.parseInt(numbers[k]);
                if (number == firstNumber + 1) {
                    res.append(" - ");
                }
                if (number == firstNumber + delta) {
                    delta++;
                } else {
                    if (delta > 1) res.append(firstNumber + delta - 1);
                    res.append(", ").append(number);
                    firstNumber = number;
                    delta = 1;
                }
            }
            if (delta > 1) res.append(firstNumber + delta - 1);
            return res.toString();
        }
    }

    private Set<Long> getFilteredIds(String fuid, long flags, Map<Integer, com.cifs.or2.kernel.Date> dates, Map<String,Long> idByUid)
            throws KrnException {
        Set<Long> res = filteredIds_.get(fuid);
        if (res == null) {
            if (Constants.IS_DEBUG)
                log.info("Executing: " + fuid);


            long fid = idByUid.get(fuid);
            //FilterDate[] fds = new FilterDate[countDates(flags)];
            ArrayList<FilterDate> afds = new ArrayList<FilterDate>();
            if ((flags & 1) > 0) {
                com.cifs.or2.kernel.Date date = (com.cifs.or2.kernel.Date) dates.get(0);
                if (date != null) afds.add(new FilterDate(fid, 0, date));
            }
            if ((flags & 2) > 0) {
                com.cifs.or2.kernel.Date date = (com.cifs.or2.kernel.Date) dates.get(1);
                if (date != null) afds.add(new FilterDate(fid, 1, date));
            }
            if ((flags & 4) > 0) {
                com.cifs.or2.kernel.Date date = (com.cifs.or2.kernel.Date) dates.get(2);
                if (date != null) afds.add(new FilterDate(fid, 2, date));
            }
            FilterDate[] fds = afds.toArray(new FilterDate[afds.size()]);
            int[] ih = {0};
            long[] ids = KRN_.getFilteredObjectIds(new long[]{fid}, fds, ih, getCash().getTransactionId());
            res = new TreeSet<Long>();
            for (long id : ids) res.add(id);
            filteredIds_.put(fuid, res);
        }
        return res;
    }


    private int countDates(long flags) {
        switch ((int)flags) {
            case 1:
            case 2:
            case 4:
                return 1;
            case 3:
            case 5:
            case 6:
                return 2;
            case 7:
                return 3;
        }
        return 0;
    }

    class ReportNode implements OrRefListener, CheckContext {
        static final int MODE = Mode.RUNTIME;

        int id;
        long langId;
        OrRef ref;
        Element parentNode;
        boolean hasParent = false;

        List<ReportNode> children = new ArrayList<ReportNode>();
        List<OrRef> exprRefs = new ArrayList<OrRef>();
        boolean needsNulls = true;
        boolean calculated = false;
        String expr;
        OrCalcRef exprRef;
        public Node node;
        int format;
        String dateFormat;
        String num;
        ReportNode parent;
        protected String defText_ = "";
        protected OrFrame frame;

        String align, valign, imgPos;

        protected int exprRefsSize;

        public ReportNode() {
        }

        public ReportNode(ReportNode parent, Node n, Element parentNode, OrFrame frame) {
            this.parentNode = parentNode;
            this.parent = parent;
            this.frame = frame;
            node = n;
            num = "";
            Node tempNode = n.getAttributes().getNamedItem("id");
            id = (tempNode != null && tempNode.getNodeValue().length() > 0)
                    ? Integer.parseInt(tempNode.getNodeValue()) : 0;


            if (parent != null) {
                hasParent = true;
                parent.addChild(this);
            }
        }


        public ReportNode(ReportNode parent, Node n, Map<String, OrRef> refs, List<OrCalcRef> calcRefs, Element parentNode,
                OrFrame frame) throws KrnException {
          this.parentNode = parentNode;
          this.parent = parent;
          this.frame = frame;
            node = n;
            num = "";
            Node tempNode = n.getAttributes().getNamedItem("id");
            id = (tempNode != null && tempNode.getNodeValue().length() > 0)
                    ? Integer.parseInt(tempNode.getNodeValue()) : 0;


            tempNode = n.getAttributes().getNamedItem("defText");
            defText_ = (tempNode != null) ? tempNode.getNodeValue() : "";


            tempNode = n.getAttributes().getNamedItem("ref");
            String path = (tempNode != null) ? tempNode.getNodeValue() : null;


            tempNode = n.getAttributes().getNamedItem("lang");
            String uid = (tempNode != null && tempNode.getNodeValue().length() > 0) ? //daulet-
                    tempNode.getNodeValue() : "";
            //String uidInShablon = System.getProperty("uidInShablon");
            if (uid.length() > 0) {
                //if ("1".equals(uidInShablon)) {
                //    langId = KRN_.getLocalId(uid,false);
                //} else {
                    langId = Integer.parseInt(uid);
                //}
            } else {
                langId = -1;
            }


            if (path != null && path.length() > 0) {
                ref = createRef(path, langId, refs);
                ref.addOrRefListener(this);
            }


            tempNode = n.getAttributes().getNamedItem("format");
            format = (tempNode != null) ?
                    Integer.parseInt(tempNode.getNodeValue()) : 0;


            tempNode = n.getAttributes().getNamedItem("dateFormat");
            dateFormat = (tempNode != null) ? tempNode.getNodeValue() : null;

            tempNode = n.getAttributes().getNamedItem("align");
            align = (tempNode != null) ? tempNode.getNodeValue() : null;
            tempNode = n.getAttributes().getNamedItem("valign");
            valign = (tempNode != null) ? tempNode.getNodeValue() : null;

            tempNode = n.getAttributes().getNamedItem("imgPos");
            imgPos = (tempNode != null) ? tempNode.getNodeValue() : null;

            
            if (ref == null) {
                Node tempChild = n.getFirstChild();
                tempNode = n.getAttributes().getNamedItem("expr");
                expr = (tempNode != null) ? tempNode.getNodeValue() :
                        (tempChild != null && tempChild.getNodeType() == Node.ELEMENT_NODE) ? tempChild.getLastChild().getNodeValue() : "";

                if (expr != null && expr.length() > 0) {
                    //int utilLangId = Utils.getDataLangId();
                    calculated = true;

                    exprRef = new OrCalcRef(expr, this instanceof ColumnReportNode,
                                MODE, refs, frame.getTransactionIsolation(),
                                frame, null, null, this);
                    calcRefs.add(exprRef);
                    exprRef.setReportRef(ReportPrinterAdapter.this.getRef());
                    if (!exprRef.hasParents())
                        exprRef.refresh(this);

                    exprRef.addOrRefListener(this);
                }
            }


            if (parent != null) {
                hasParent = true;
                parent.addChild(this);
            }
            exprRefsSize = exprRefs.size();
        }


        OrRef createRef(String path, long langId, Map<String, OrRef> refs) throws KrnException {
            OrRef res = OrRef.createRef(path, false, MODE, refs,
                    frame.getTransactionIsolation(), frame);
            res.addLanguage(langId);
            return res;
        }

        OrRef createExprRef(String path, int langId, Map<String, OrRef> refs) throws KrnException {
            return OrRef.createRef(path, false, MODE, refs,
                    frame.getTransactionIsolation(), frame);
        }

        void addChild(ReportNode child) {
            children.add(child);
        }

        public List<ReportNode> getChildren() {
            return children;
        }

        public ReportNode getParent() {
            return parent;
        }

        // OrRefListener
        public void valueChanged(OrRefEvent e) {
            try {
                if (listen && e.getOriginator() != this && !hasParent &&
                        !(e.getOriginator() instanceof ColumnExReportNode
                        || e.getOriginator() instanceof ColumnReportNode
                        || e.getOriginator() instanceof OrCalcRef)
                        && (e.getReason() & OrRefEvent.ITERATING) == 0)
                    if ((ref != null || exprRef != null) && exprRefsSize-- == 0/* || expr.startsWith("getNum()")*/) print();
            } catch (Exception ex) {
                log.error(ex, ex);
            }
        }

        public void changesCommitted(OrRefEvent e) {
        }

        public void changesRollbacked(OrRefEvent e) {
        }

        public void pathChanged(OrRefEvent e) {
        }

        public void checkReqGroups(OrRef ref, List<ReqMsgsList.MsgListItem> errMsgs, List<ReqMsgsList.MsgListItem> reqMsgs, Stack<Pair> locs) {
        }

        @Override
		public String getUUID() {
			return null;
		}

		void print() throws KrnException {
            Attribute str = new Attribute("str", "");
            Attribute fmt = null;
            if (!calculated) {
                List<Item> items = ref.getItems(langId);
                OrRef.Item item = null;
                if (items.size() > 0)
                    item = items.get(items.size() - 1);
                if (item != null && item.getCurrent() != null) {
                    long typeId = ref.getType().id;
                    if (typeId == Kernel.IC_BLOB)
                    	str = new Attribute("src", convertBlob(format, item.getCurrent()));
                    else {
                        str.setValue(convertToString(typeId, item.getCurrent()));
                        if (typeId == Kernel.IC_FLOAT)
                        	fmt = new Attribute("type", String.valueOf(typeId));
                    }
                } else if (!needsNulls)
                    return;
            } else {
                OrRef.Item item = exprRef.getItem();
                if (item != null && item.getCurrent() != null) {
                    if (item.getCurrent() instanceof Collection) {
                    	String res = "";
                    	Collection list = (Collection) item.getCurrent();
                    	
                    	if (list.size() > 0) {
                    		for (Object val : list) {
	                    		if (val instanceof byte[]) {
	                                byte[] v = (byte[]) val;
	                                if (v.length > 0) {
	                                    try {
	            							File f = Funcs.createTempFile("image", null, WebController.WEB_IMAGES_DIRECTORY);
	            							((WebFrame)frame).getSession().deleteOnExit(f);
	                                        FileOutputStream os = new FileOutputStream(f);
	                                        os.write(v);
	                                        os.close();

	                                        res += f.getAbsolutePath() + "|";
	                                    } catch (IOException e) {
	                                        e.printStackTrace();
	                                    }
	                                }
	                    		} else if (val instanceof File) {
	        	                    File f = (File) val;
                                    res += f.getAbsolutePath() + "|";
	                    		}
                    		}
                    	}
                    	str = new Attribute("src", res.length() > 0 ? res.substring(0, res.length() - 1) : "");
                    } else if (item.getCurrent() instanceof byte[]) {
    					byte[] v = (byte[]) item.getCurrent();
    					if (v.length > 0) {
    						try {
    							File f = Funcs.createTempFile("image", null, WebController.WEB_IMAGES_DIRECTORY);
    							((WebFrame)frame).getSession().deleteOnExit(f);
    							FileOutputStream os = new FileOutputStream(f);
    							os.write(v);
    							os.close();

    	                        str = new Attribute("src", f.getAbsolutePath());
    						} catch (IOException e) {
    							log.error(e, e);
    						}
    					}
                	} else {
	                    str.setValue(convertToString(item.getCurrent(), format, dateFormat));
	                    if (format == 0)
	                    	fmt = new Attribute("type", String.valueOf(Kernel.IC_FLOAT));
                	}
                }
            }
            if (str.getValue().length() == 0) str.setValue(defText_);
            Element field = new Element("Field");
            field.setAttribute("id", String.valueOf(id));
            field.setAttribute(str);
            if (fmt != null) field.setAttribute(fmt);

            if (align != null)
            	field.setAttribute("align", align);
            if (valign != null)
            	field.setAttribute("valign", valign);
            if (imgPos != null)
            	field.setAttribute("imgPos", imgPos);

            parentNode.addContent(field);
        }

        boolean print(Element parentElement) throws KrnException {
            return false;
        }


        public void clear() {
        }


        public void stateChanged(OrRefEvent e) {
        }

        public long getLangId() {
            return langId;
        }


        public int getEnterDB() {
            return 0;
        }


        public boolean isActive() {
            return true;
        }


        public void setFocus(OrGuiComponent c) {


        }


        public String getCExpr() {
            return null;
        }


        public String getReqMsg() {
            return null;
        }


        public ASTStart getCTemplate() {
            return null;
        }


        public int getReqGroup() {
            return 0;
        }


        public OrRef getRef() {
            return null;
        }


        public boolean isCheckConstr() {
            return false;
        }


        public boolean isCheckConstrValue() {
            return false;
        }


        public void setState(Integer index, Integer type) {


        }


        public void removeState(Integer index) {


        }


        public void clearStates() {


        }


        public Integer getState(Integer index) {
            return null;
        }


        public String getConstrMsg() {
            return null;
        }
    }


    class SystemReportNode extends ReportNode {
        private String path = "";


        public SystemReportNode(Node n, Element parentNode) {
            this.parentNode = parentNode;
            node = n;
            Node tempNode = n.getAttributes().getNamedItem("id");
            id = (tempNode != null && tempNode.getNodeValue().length() > 0)
                    ? Integer.parseInt(tempNode.getNodeValue()) : 0;


            tempNode = n.getAttributes().getNamedItem("lang");
            langId = (tempNode != null && tempNode.getNodeValue().length() > 0) ?
                    Integer.parseInt(tempNode.getNodeValue()) : -1;


            tempNode = n.getAttributes().getNamedItem("sysPath");
            path = (tempNode != null) ? tempNode.getNodeValue() : null;
            calculated = true;
        }


        void print() throws KrnException {
            final Kernel krn = KRN_;

            StringTokenizer st = new StringTokenizer(path, ".");
            String res = "";


            if (st.hasMoreTokens()) {
                String className = st.nextToken();
                KrnClass cls = krn.getClassNodeByName(className).getKrnClass();


                KrnObject user = krn.getUser().getObject();
                KrnObject base = krn.getUser().getBase();
                long[] objIds;
                if (cls.id == user.classId)
                    objIds = new long[]{user.id};
                else
                    objIds = new long[]{base.id};


                while (st.hasMoreTokens()) {
                    String attrName = st.nextToken();
                    KrnAttribute attr = krn.getAttributeByName(cls, attrName);
                    cls = krn.getClassNode(attr.typeClassId).getKrnClass();


                    if (cls.name.equals("String") || cls.name.equals("string")
                            || cls.name.equals("Memo") || cls.name.equals("memo")) {
                        long lid = (cls.name.equals("String") || cls.name.equals("Memo")) ?
                                langId : 0;
                        boolean isMemo = (cls.name.equals("memo") || cls.name.equals("Memo")) ?
                                true : false;


                        StringValue[] svs = krn.getStringValues(objIds, attr, lid, isMemo, 0);
                        if (svs != null && svs.length > 0) {
                            res = svs[svs.length - 1].value;
                        }
                    } else if (cls.name.equals("integer")) {
                        LongValue[] lvs = krn.getLongValues(objIds, attr, 0);
                        if (lvs != null && lvs.length > 0)
                            res += lvs[lvs.length - 1].value;
                    } else if (cls.name.equals("float")) {
                        FloatValue[] fvs = krn.getFloatValues(objIds, attr, 0);
                        if (fvs != null && fvs.length > 0)
                            res += fvs[fvs.length - 1].value;
                    } else {
                        ObjectValue[] ovs = krn.getObjectValues(objIds, attr, 0);
                        if (ovs != null && ovs.length > 0) {
                            objIds = new long[]{ovs[ovs.length - 1].value.id};
                        }
                    }
                }
            }
            Element field = new Element("Field");
            field.setAttribute("id", String.valueOf(id));
            field.setAttribute("str", res);
            parentNode.addContent(field);
        }
    }


    class UserNode extends ReportNode {
        public UserNode(Node n, Element parentNode) {
            this.parentNode = parentNode;
            node = n;
            Node tempNode = n.getAttributes().getNamedItem("id");
            id = (tempNode != null && tempNode.getNodeValue().length() > 0)
                    ? Integer.parseInt(tempNode.getNodeValue()) : 0;
            calculated = true;
        }


        void print() throws KrnException {
            User user = KRN_.getUser();
            String str = user.getUserSign();
            Element field = new Element("User");
            field.setAttribute("id", String.valueOf(id));
            field.setAttribute("str", str);
            parentNode.addContent(field);
        }
    }


    class BaseNode extends UserNode {
        public BaseNode(Node n, Element parentNode) {
            super(n, parentNode);
        }


        void print() throws KrnException {
            User user = KRN_.getUser();
            String str = user.getBaseCode();
            Element field = new Element("Base");
            field.setAttribute("id", String.valueOf(id));
            field.setAttribute("str", str);
            parentNode.addContent(field);
        }
    }


    class DepNode extends UserNode {
        public DepNode(Node n, Element parentNode) {
            super(n, parentNode);
        }


        void print() throws KrnException {
            Kernel krn = KRN_;
            long selBases[] = krn.getSelectedBases();
            String str = "";
            for (int i = 0; i < selBases.length; i++) {
                KrnObject o = krn.getObjectsSingular(selBases[i],
                        krn.getAttributeByName(krn.getClassByName("Структура баз"), "значение").id, true);
                String s = krn.getStringsSingular(o.id, krn.getAttributeByName(krn.getClassByName("База"), "наименование").id,
                        0, false, true);
                str += s;
                if (i < selBases.length - 1) str += ", ";
            }
            Element field = new Element("Department");
            field.setAttribute("id", String.valueOf(id));
            field.setAttribute("str", str);
            parentNode.addContent(field);
        }
    }


    class ColumnReportNode extends ReportNode {
        OrRef tableRef;

        OrRef tableRef2;
        OrRef treeGroupRef;

        Filter filter;
        protected String fuid;
        protected Set<Long> filteredIds;
        Filter innerFilter;
        protected String innerFuid;
        protected Set<Long> innerFilteredIds;
        boolean filterInnerTable = false;

        OrRef uniqueRef;
        Collection<Object> uniqueObjs = new ArrayList<Object>();

        TreeAdapter tree;
        TreeAdapter.Node nodeInProgress;

        TreeAdapter2 tree2;

        int tableId = -1;
        boolean showNulls = true;

        public ColumnReportNode(ReportNode parent, Node n, Map<String, OrRef> refs, List<OrCalcRef> calcRefs,
                                Element parentNode, OrFrame frame) throws KrnException {
            super(parent, n, refs, calcRefs, parentNode, frame);


            Node pathNode = n.getAttributes().getNamedItem("table");
            String path = (pathNode != null) ? pathNode.getNodeValue() : null;
            if (path != null && path.length() > 0) {
                int t = path.indexOf("@");
                if (t < 0) {
                    tableRef = createTableRef(path, refs);
                } else if (t == 0) {
                	tableRef = createTableRef(path.substring(1), refs);
                } else {
                    tableRef = createTableRef(path.substring(0, t), refs);
                    tableRef2 = createTableRef(path.substring(t + 1), refs);
                    if (exprRef != null) tableRef2.addOrRefListener(exprRef);
                }
            }

            pathNode = n.getAttributes().getNamedItem("tableId");
            path = (pathNode != null) ? pathNode.getNodeValue() : null;
            if (path != null && path.length() > 0)
                tableId = Integer.parseInt(path);


            pathNode = n.getAttributes().getNamedItem("showNulls");
            path = (pathNode != null) ? pathNode.getNodeValue() : null;
            if (path != null && path.length() > 0)
                showNulls = false;


            Node fNode = n.getAttributes().getNamedItem("filter");
            fuid = (fNode != null) ? fNode.getNodeValue() : null;


            try {
                if (fuid != null && fuid.length() > 0) {
                    if (fuid.charAt(0) == '@') {
                        innerFuid = fuid.substring(1);
                        fuid = null;
                        filterInnerTable = true;
                    }
                    
                    if (fuid != null) {
	                    KrnObject[] fobjs = KRN_.getObjectsByUid(new String[]{fuid}, 0);
	                    long[] fids = Funcs.makeObjectIdArray(fobjs);
	
	                    if (fids != null && fids.length > 0 && tableRef != null) {
	                        long flags = readFlags(fids[0]);
	                        KrnClass fcls = KRN_.getClassByName("Filter");
	                        KrnObject obj = new KrnObject(fids[0], "", fcls.id);
	                        filter = new Filter(obj, langId, "", flags);
	                    }
                    }
                    if (innerFuid != null) {
	                    KrnObject[] fobjs = KRN_.getObjectsByUid(new String[]{innerFuid}, 0);
	                    long[] fids = Funcs.makeObjectIdArray(fobjs);
	
	                    if (fids != null && fids.length > 0 && tableRef != null) {
	                        long flags = readFlags(fids[0]);
	                        KrnClass fcls = KRN_.getClassByName("Filter");
	                        KrnObject obj = new KrnObject(fids[0], "", fcls.id);
	                        innerFilter = new Filter(obj, langId, "", flags);
	                    }
                    }
                }
            } catch (Exception ex) {
                log.error("-----===== The filter is not assigned or not found =====----- id = " + id);
                log.error(ex, ex);
            }


            //if (exprRef != null) exprRefsSize = exprRef.getParentsSize();


            pathNode = n.getAttributes().getNamedItem("treeGroup");
            path = (pathNode != null) ? pathNode.getNodeValue() : null;
            if (path != null && path.length() > 0)
                treeGroupRef = createRef(path, langId, refs);


            pathNode = n.getAttributes().getNamedItem("unique");
            path = (pathNode != null) ? pathNode.getNodeValue() : null;
            if (path != null && path.length() > 0)
                uniqueRef = createRef(path, langId, refs);


            pathNode = n.getAttributes().getNamedItem("sort");
            path = (pathNode != null) ? pathNode.getNodeValue() : null;
            if (path != null && path.length() > 0 && tableRef != null) {
//                tableRef.getRoot().setSortAttributes(path, ref.toString());
                pathNode = n.getAttributes().getNamedItem("direction");
                boolean desc = (pathNode != null) ? "1".equals(pathNode.getNodeValue()) : false;
                setSortAttributes(path, String.valueOf(id), desc);
            }
            pathNode = n.getAttributes().getNamedItem("root");
            if (pathNode != null) {
                String rootPath = pathNode.getNodeValue();


                pathNode = n.getAttributes().getNamedItem("title");
                String titlePath = (pathNode != null) ? pathNode.getNodeValue() : null;


                tree = new TreeAdapter(rootPath, titlePath, null, refs, langId, frame);


                if (rootPath.equals(ReportPrinterAdapter.this.getRef().toString()))
                    tree.setRoot(ReportPrinterAdapter.this.getRef().getItem(0).getRec());


                tree.getRoot();
            }
        }

        public String getFuid() {
            return fuid;
        }

        public void setFilteredIds(Set<Long> ids) {
            filteredIds = ids;
        }

        public void setInnerFilteredIds(Set<Long> ids) {
            innerFilteredIds = ids;
        }

        OrRef createRef(String path, long langId, Map<String, OrRef> refs) throws KrnException {
            return createRef(path, langId, refs, true);
        }


        OrRef createRef(String path, long langId, Map<String, OrRef> refs, boolean isColumn) throws KrnException {
            KrnAttribute[] attrs = kz.tamur.rt.Utils.getAttributesForPath(path, frame.getKernel());
            if (attrs != null && attrs.length > 0) {
                if (!Funcs.isIntegralType(attrs[attrs.length - 1].typeClassId))
                    langId = -1;
            } else if (attrs != null && attrs.length == 0) {
                langId = 0;
            }
            int t = frame.getTransactionIsolation();


            OrRef res = OrRef.createRef(path, isColumn, MODE, refs,
                    t, frame);
            res.addLanguage(langId);
            return res;
        }


        OrRef createTableRef(String path, Map<String, OrRef> refs) throws KrnException {
            return createTableRef(path, refs, false);
        }


        OrRef createTableRef(String path, Map<String, OrRef> refs, boolean isColumn) throws KrnException {
            OrRef res = OrRef.createRef(path, false, MODE, refs,
                    frame.getTransactionIsolation(), frame);
            res.setColumn(isColumn);

            if (expr != null && expr.indexOf("$Interface.getAttr(\"" + path) == -1)
                res.addOrRefListener(this);

            OrRef r = ref;
            while (r != null && r != res) {
                r.setColumn(true);
                r = r.getParent();
            }
            if (exprRef != null) exprRef.setTableRef(res);
            return res;
        }


        OrRef createExprRef(String path, int langId, Map<String, OrRef> refs) throws KrnException {
            OrRef res = OrRef.createRef(path, true, MODE, refs,
                    frame.getTransactionIsolation(), frame);
            res.addLanguage(langId);
            return res;
        }


        public void valueChanged(OrRefEvent e) {
            try {
                if (listen && e.getOriginator() != this
                        && e.getOriginator() != null
                        && !hasParent && !(e.getOriginator() instanceof ColumnExReportNode
                                        || e.getOriginator() instanceof ColumnReportNode
                                        || e.getOriginator() instanceof OrCalcRef)
                        && (e.getReason() & OrRefEvent.ITERATING) == 0) {
                    if (e.getRef() == tableRef && tableRef2 == null) {
                        if (exprRef != null) exprRef.refresh(this);
                    } else if (e.getRef() == tableRef2) {
                        if (exprRef != null) {
                            //exprRef.refresh(this);
                            print();
                        }
                    }
                    if (ref != null || (exprRef != null && --exprRefsSize < 1 && tableRef2 == null)) print();
                }
                if (listen && e.getOriginator() instanceof TreeReportNode
                           && exprRef != null) {
                    exprRef.refresh(this);
                }
            } catch (Exception ex) {
                log.error(ex, ex);
            }
        }


        void print() throws KrnException {
            if (tree != null) {
                treePrint();
                return;
            }

            List<Item> items = (!calculated && tableRef2 == null) ?
                    ref.getItems(langId) : tableRef.getItems(langId);//((OrRef) exprRefs.get(0)).getItems();

            int items_size = items.size();

            try {
                XPath oPath = XPath.newInstance("./Column[@id='" + id + "']");
                if (oPath.selectSingleNode(parentNode) != null)
                    return;
            }catch (JDOMException e) {
                log.error(e, e);
            }
            Element column = new Element("Column");
            column.setAttribute("id", String.valueOf(id));
            column.setAttribute("tableId", String.valueOf(tableId));
            if (dateFormat != null) {
                column.setAttribute("dateFormat", dateFormat);
            }
            if (calculated && expr.startsWith("getNum()")) {
                column.setAttribute("nosort", "1");
                column.setAttribute("numType", "1");
            }
            if (calculated && expr.startsWith("getNum2()")) {
                column.setAttribute("nosort", "1");
                column.setAttribute("numType", "2");
            }

            if (items_size > 0) {
                for (int j = 0; j < items_size; ++j) {
                    Object tableObj = tableRef.getItem(langId, j).getCurrent();
                    if (tableObj != null && (filteredIds == null
                            || filteredIds.contains(new Long(((KrnObject) tableObj).id)) || ((KrnObject) tableObj).id < 0)) {
                        Attribute str = new Attribute("str", "");
                        Attribute fmt = null;
                        if (tableRef2 == null) {
                            if (calculated) {
                                OrRef.Item item = exprRef.getItem(langId, j);
                                Object value = null;
                                if (item != null)
                                    value = item.getCurrent();
                                if (value != null && value.equals("num")) {
                                    str.setValue(num + (j + 1));
                                } else {
	                            	if (value instanceof byte[]) {
	                					byte[] v = (byte[]) value;
	                					if (v.length > 0) {
	                						try {
	                							File f = Funcs.createTempFile("image", null, WebController.WEB_IMAGES_DIRECTORY);
	                							((WebFrame)frame).getSession().deleteOnExit(f);
	                							FileOutputStream os = new FileOutputStream(f);
	                							os.write(v);
	                							os.close();
	
	                	                        str = new Attribute("src", f.getAbsolutePath());
	                						} catch (IOException e) {
	                							log.error(e, e);
	                						}
	                					}
	                            	} else {
	                                    str.setValue(convertToString(value, format, dateFormat));
	                                    if (format == 0)
	                                    	fmt = new Attribute("type", String.valueOf(Kernel.IC_FLOAT));
	                            	}
                                }
                            } else {
                                OrRef.Item item = items.get(j);
                                if (tableRef == null) continue; //daulet+ 30.11.2004
                                if (tableRef.getItem(langId, j) == null) continue; //daulet+ 30.11.2004
                                if (item != null && item.getCurrent() != null) {
                                    long typeId = ref.getType().id;
                                    if (typeId == Kernel.IC_BLOB)
                                        str = new Attribute("src", convertToString(typeId, item.getCurrent()));
                                    else {
                                        str.setValue(convertToString(typeId, item.getCurrent()));
                                        if (typeId == Kernel.IC_FLOAT)
                                        	fmt = new Attribute("type", String.valueOf(typeId));
                                    }
                                }
                            }
                            if (!showNulls && "0".equals(str.getValue())) str.setValue("");
                            if (str.getValue().length() == 0) str.setValue(defText_);
                            Element valueElement = new Element("Value");
                            valueElement.setAttribute(str);
                            if (fmt != null) valueElement.setAttribute(fmt);
                            column.addContent(valueElement);
                        } else {
                            tableRef.absolute(j, this);
                            List<Item> items2 = tableRef2.getItems(langId);

                            if (exprRef != null)
                            	exprRef.refresh(this);

                            int items_size2 = items2.size();
                            Element valueElement = new Element("Value");
                            column.addContent(valueElement);


                            for (int k = 0; k < items_size2; ++k) {
                                Object tableObj2 = tableRef2.getItem(langId, k).getCurrent();
                                if (tableObj2 != null && (innerFilteredIds == null
                                        || innerFilteredIds.contains(new Long(((KrnObject) tableObj2).id)) || ((KrnObject) tableObj2).id < 0)) {

                                    Attribute str2 = new Attribute("str", "");
                                    Attribute fmt2 = null;
                                    if (calculated) {
                                        //tableRef2.absolute(k, this);
                                        Object value = exprRef.getValue(langId, k);
    	                            	if (value instanceof byte[]) {
    	                					byte[] v = (byte[]) value;
    	                					if (v.length > 0) {
    	                						try {
    	                							File f = Funcs.createTempFile("image", null, WebController.WEB_IMAGES_DIRECTORY);
    	                							((WebFrame)frame).getSession().deleteOnExit(f);
    	                							FileOutputStream os = new FileOutputStream(f);
    	                							os.write(v);
    	                							os.close();
    	
    	                	                        str2 = new Attribute("src", f.getAbsolutePath());
    	                						} catch (IOException e) {
    	                							log.error(e, e);
    	                						}
    	                					}
    	                            	} else {
	                                        str2.setValue(convertToString(value, format, dateFormat));
	                                        if (format == 0)
	                                        	fmt2 = new Attribute("type", String.valueOf(Kernel.IC_FLOAT));
    	                            	}
                                    } else {
                                        OrRef.Item item = (OrRef.Item) ref.getItems(langId).get(k);
                                        if (item != null && item.getCurrent() != null) {
                                            long typeId = ref.getType().id;
                                            if (typeId == Kernel.IC_BLOB)
                                                str2 = new Attribute("src", convertToString(typeId, item.getCurrent()));
                                            else {
                                                str2.setValue(convertToString(typeId, item.getCurrent()));
                                                if (typeId == Kernel.IC_FLOAT)
                                                	fmt2 = new Attribute("type", String.valueOf(typeId));
                                            }
                                        }
                                    }
                                    if (!showNulls && "0".equals(str2.getValue())) str2.setValue("");
                                    if (str2.getValue().length() == 0) str2.setValue(defText_);
                                    Element valueElement2 = new Element("Value");
                                    valueElement2.setAttribute(str2);
                                    if (fmt2 != null) valueElement2.setAttribute(fmt2);
                                    valueElement.addContent(valueElement2);
                                }
                            }
                        }
                    }
                }
            } else if (defText_.length() > 0) {
                column.setAttribute("str", defText_);
            }
            parentNode.addContent(column);
        }

        public Element evaluate(int index) throws KrnException {
            Attribute str = new Attribute("str", "");
            Attribute fmt = null;
            if (calculated) {
                OrRef.Item item = exprRef.getItem(langId, index);
                Object value = null;
                if (item != null)
                    value = item.getCurrent();
                if (value != null && value.equals("num")) {
                    str.setValue(num + (index + 1));
                } else {
                    if (value instanceof byte[]) {
                        byte[] v = (byte[]) value;
                        if (v.length > 0) {
                            try {
    							File f = Funcs.createTempFile("image", null, WebController.WEB_IMAGES_DIRECTORY);
    							((WebFrame)frame).getSession().deleteOnExit(f);
                                FileOutputStream os = new FileOutputStream(f);
                                os.write(v);
                                os.close();

                                str = new Attribute("src", f.getAbsolutePath());
                            } catch (IOException e) {
                                log.error(e, e);
                            }
                        }
                    } else {
                        str.setValue(convertToString(value, format, dateFormat));
                        if (format == 0)
                            fmt = new Attribute("type", String.valueOf(Kernel.IC_FLOAT));
                    }
                }
            } else {
                OrRef.Item item = (OrRef.Item) ref.getItem(langId, index);

                if (tableRef == null || tableRef.getItem(langId, index) == null)
                    return null;

                if (item != null && item.getCurrent() != null) {
                    long typeId = ref.getType().id;
                    if (typeId == Kernel.IC_BLOB)
                        str = new Attribute("src", convertToString(typeId, item.getCurrent()));
                    else {
                        str.setValue(convertToString(typeId, item.getCurrent()));
                        if (typeId == Kernel.IC_FLOAT)
                            fmt = new Attribute("type", String.valueOf(typeId));
                    }
                }
            }
            if (!showNulls && "0".equals(str.getValue()))
                str.setValue("");
            if (str.getValue().length() == 0)
                str.setValue(defText_);
            Element valueElement = new Element("Value");
            valueElement.setAttribute(str);
            if (fmt != null)
                valueElement.setAttribute(fmt);
            return valueElement;
        }

        boolean print(Element parentElement) throws KrnException {
            boolean res = false;

            List<Item> items = !calculated && tableRef2 == null ?
                    ref.getItems(langId) : tableRef.getItems(langId);//((OrRef) exprRefs.get(0)).getItems();


            int items_size = items.size();
            /*for (int k = 0; k < exprRefs.size(); ++k) {
                ((OrRef) exprRefs.get(k)).setColumn(false);
            }*/

            Element column = new Element("Column");
            column.setAttribute("id", String.valueOf(id));
            column.setAttribute("tableId", String.valueOf(tableId));
            if (dateFormat != null) {
                column.setAttribute("dateFormat", dateFormat);
            }
            if (calculated && expr.startsWith("getNum()")) {
                column.setAttribute("nosort", "1");
                column.setAttribute("numType", "1");
            }
            if (calculated && expr.startsWith("getNum2()")) {
                column.setAttribute("nosort", "1");
                column.setAttribute("numType", "2");
            }


            if (items_size > 0) {
                for (int j = 0; j < items_size; ++j) {
                    Object tableObj = tableRef.getItem(langId, j).getCurrent();
                    if (tableObj != null && (filteredIds == null
                            || filteredIds.contains(new Long(((KrnObject) tableObj).id)) || ((KrnObject) tableObj).id < 0)) {
                        Attribute str = new Attribute("str", "");
                        Attribute fmt = null;
                        if (tableRef2 == null) {
                            if (calculated) {
                                OrRef.Item item = exprRef.getItem(langId, j);
                                Object value = null;
                                if (item != null)
                                    value = item.getCurrent();
                                if (value != null && value.equals("num")) {
                                    str.setValue(num + (j + 1));
                                } else {
	                            	if (value instanceof byte[]) {
	                					byte[] v = (byte[]) value;
	                					if (v.length > 0) {
	                						try {
	                							File f = Funcs.createTempFile("image", null, WebController.WEB_IMAGES_DIRECTORY);
	                							((WebFrame)frame).getSession().deleteOnExit(f);
	                							FileOutputStream os = new FileOutputStream(f);
	                							os.write(v);
	                							os.close();
	
	                	                        str = new Attribute("src", f.getAbsolutePath());
	                						} catch (IOException e) {
	                							log.error(e, e);
	                						}
	                					}
	                            	} else {
	                                    str.setValue(convertToString(value, format, dateFormat));
	                                    if (format == 0)
	                                    	fmt = new Attribute("type", String.valueOf(Kernel.IC_FLOAT));
	                            	}
                                }
                            } else {
                                OrRef.Item item = items.get(j);
                                if (tableRef == null) continue; //daulet+ 30.11.2004
                                if (tableRef.getItem(langId, j) == null) continue; //daulet+ 30.11.2004
                                if (item != null && item.getCurrent() != null) {
                                    long typeId = ref.getType().id;
                                    if (typeId == Kernel.IC_BLOB)
                                        str = new Attribute("src", convertToString(typeId, item.getCurrent()));
                                    else {
                                        str.setValue(convertToString(typeId, item.getCurrent()));
                                        if (typeId == Kernel.IC_FLOAT)
                                        	fmt = new Attribute("type", String.valueOf(typeId));
                                    }
                                }
                            }
                            if (!showNulls && "0".equals(str.getValue())) str.setValue("");
                            if (str.getValue().length() == 0) str.setValue(defText_);
                            Element valueElement = new Element("Value");
                            valueElement.setAttribute(str);
                            if (fmt != null) valueElement.setAttribute(fmt);
                            column.addContent(valueElement);
                            res = true;
                        } else {
                            tableRef.absolute(j, this);
                            List<Item> items2 = tableRef2.getItems(langId);
                            int items_size2 = items2.size();
                            Element valueElement = new Element("Value");
                            column.addContent(valueElement);


                            for (int k = 0; k < items_size2; ++k) {
                                Object tableObj2 = tableRef2.getItem(langId, k).getCurrent();
                                if (tableObj2 != null && (innerFilteredIds == null
                                        || innerFilteredIds.contains(new Long(((KrnObject) tableObj2).id)) || ((KrnObject) tableObj2).id < 0)) {
                                    Attribute str2 = new Attribute("str", "");
                                    Attribute fmt2 = null;
                                    if (calculated) {
                                        tableRef2.absolute(k, this);
                                        Object value = exprRef.getValue(langId);
    	                            	if (value instanceof byte[]) {
    	                					byte[] v = (byte[]) value;
    	                					if (v.length > 0) {
    	                						try {
    	                							File f = Funcs.createTempFile("image", null, WebController.WEB_IMAGES_DIRECTORY);
    	                							((WebFrame)frame).getSession().deleteOnExit(f);
    	                							FileOutputStream os = new FileOutputStream(f);
    	                							os.write(v);
    	                							os.close();
    	
    	                	                        str2 = new Attribute("src", f.getAbsolutePath());
    	                						} catch (IOException e) {
    	                							log.error(e, e);
    	                						}
    	                					}
    	                            	} else {
	                                        str2.setValue(convertToString(value, format, dateFormat));
	                                        if (format == 0)
	                                        	fmt2 = new Attribute("type", String.valueOf(Kernel.IC_FLOAT));
    	                            	}
                                    } else {
                                        OrRef.Item item = (OrRef.Item) ref.getItems(langId).get(k);
                                        if (item != null && item.getCurrent() != null) {
                                            long typeId = ref.getType().id;
                                            if (typeId == Kernel.IC_BLOB)
                                                str2 = new Attribute("src", convertToString(typeId, item.getCurrent()));
                                            else {
                                                str2.setValue(convertToString(typeId, item.getCurrent()));
                                                if (typeId == Kernel.IC_FLOAT)
                                                	fmt2 = new Attribute("type", String.valueOf(typeId));
                                            }
                                        }
                                    }
                                    if (!showNulls && "0".equals(str2.getValue())) str2.setValue("");
                                    if (str2.getValue().length() == 0) str2.setValue(defText_);
                                    Element valueElement2 = new Element("Value");
                                    valueElement2.setAttribute(str2);
                                    if (fmt2 != null) valueElement2.setAttribute(fmt2);
                                    valueElement.addContent(valueElement2);
                                    res = true;
                                }
                            }
                        }
                    }
                }
            } else if (defText_.length() > 0) {
                column.setAttribute("str", defText_);
            }
            if (res) {
                int i = parentElement.getChildren("Column").size();
                parentElement.addContent(i, column);
            }
            return res;
        }


        void treePrint() throws KrnException {
            List<Item> items = ref.getItems(langId);
            MultiMap<Long, Item> mmap = new MultiMap<Long, Item>();
            for (int i = 0; i < items.size(); ++i) {
                KrnObject obj = (KrnObject) items.get(i).getCurrent();
                if (obj != null) {
                    Long objId = new Long(obj.id);
                    mmap.put(objId, tableRef.getItems(langId).get(i));
                }
            }


            Element column = new Element("Column");
            column.setAttribute("id", String.valueOf(id));
            column.setAttribute("tableId", String.valueOf(tableId));
            TreeAdapter.Node root = tree.getRoot();
            processNode(root, mmap, column);
            parentNode.addContent(column);
        }


        boolean processNode(TreeAdapter.Node n, MultiMap<Long, Item> mmap, Element parentElement) throws KrnException {
            nodeInProgress = n;

            Element valueElement = new Element("Value");
            valueElement.setAttribute("str", n.toString());
            parentElement.addContent(valueElement);
            List<Item> items = mmap.get(n.getObject().id);
            tableRef.getItems(langId).clear();
            if (items != null && items.size() > 0) {
                tableRef.getItems(langId).addAll(items);
                tableRef.fireValueChangedEvent(-1, this, 0);
            } else {
                tableRef.fireValueChangedEvent(-1, this, 0);
                ref.getItems(langId).clear();
                ref.getItems(langId).add(ref.new Item(n.getObject()));
                ref.fireValueChangedEvent(0, this, 0);
            }

            for (int i = 0; i < n.getChildCount(); ++i) {
                processNode((TreeAdapter.Node) n.getChildAt(i), mmap, valueElement);
            }

            return true;
        }
    }

    class ColumnExReportNode extends ColumnReportNode {
        public ColumnExReportNode(ReportNode parent, Node n, Map<String, OrRef> refs, List<OrCalcRef> calcRefs,
                                  Element parentNode, OrFrame frame) throws KrnException {
            super(parent, n, refs, calcRefs, parentNode, frame);
        }


        OrRef createRef(String path, int langId, Map<String, OrRef> refs) throws KrnException {
            KrnAttribute[] attrs = kz.tamur.rt.Utils.getAttributesForPath(path, frame.getKernel());
            if (attrs != null && attrs.length > 0) {
                if (!Funcs.isIntegralType(attrs[attrs.length - 1].typeClassId))
                    langId = -1;
            }
            OrRef res = OrRef.createRef(path, true, MODE, refs,
                    frame.getTransactionIsolation(), frame);
            res.addLanguage(langId);
            return res;
        }


        OrRef createTableRef(String path, Map<String, OrRef> refs) throws KrnException {
            OrRef res = OrRef.createRef(path, false, MODE, refs,
                    frame.getTransactionIsolation(), frame);
            res.setColumn(false);
            res.addOrRefListener(this);


            OrRef r = ref;
            while (r != null && r != res) {
                r.setColumn(true);
                r = r.getParent();
            }


            return res;
        }


        OrRef createExprRef(String path, int langId, Map<String, OrRef> refs) throws KrnException {
            OrRef res = OrRef.createRef(path, true, MODE, refs,
                    frame.getTransactionIsolation(), frame);
            res.addLanguage(langId);
            return res;
        }


        /*
        public void valueChanged(OrRefEvent e)
        {
          try {
            if (e.getRef() == ref && e.getOriginator() != this)
              print();
          } catch (Exception ex) {
            log.error(ex, ex);
          }
        }     */




        void print() throws KrnException {
            List<Item> items = !calculated ?
                    ((ref != null) ? ref.getItems(langId) : new ArrayList<Item>())
                    : ((exprRef != null) ?
                    exprRef.getItems(langId) : new ArrayList<Item>());


            Element parentElement = parentNode;

            if (ref != null && calculated) items = ref.getItems(langId);

            if (items.size() > 0) {
                if (!hasParent) {
                    Element column = new Element("Column");
                    column.setAttribute("id", String.valueOf(id));
                    column.setAttribute("tableId", String.valueOf(tableId));
                    if (calculated && expr.startsWith("getNum()")) {
                        column.setAttribute("nosort", "1");
                        column.setAttribute("numType", "1");
                    }
                    if (calculated && expr.startsWith("getNum2()")) {
                        column.setAttribute("nosort", "1");
                        column.setAttribute("numType", "2");
                    }
                    parentElement.addContent(column);
                    parentElement = column;
                }
                for (int j = 0; j < items.size(); ++j) {
                    Attribute str = new Attribute("str", "");
                    Attribute fmt = null;
                    tableRef.absolute(j, this);
                    //  tableRef.fireValueChangedEvent(-1, this);
                    if (calculated) {
                        Object v = exprRef.getValue(langId);
                        if (v != null && v.equals("num")) {
                            str.setValue(num + (j + 1));
                        } else if (v != null) {
                        	if (v instanceof byte[]) {
            					byte[] v1 = (byte[]) v;
            					if (v1.length > 0) {
            						try {
            							File f = Funcs.createTempFile("image", null, WebController.WEB_IMAGES_DIRECTORY);
            							((WebFrame)frame).getSession().deleteOnExit(f);
            							FileOutputStream os = new FileOutputStream(f);
            							os.write(v1);
            							os.close();

            	                        str = new Attribute("src", f.getAbsolutePath());
            						} catch (IOException e) {
            							log.error(e, e);
            						}
            					}
                        	} else {
	                            str.setValue(convertToString(v, format, dateFormat));
	                            if (format == 0)
	                            	fmt = new Attribute("type", String.valueOf(Kernel.IC_FLOAT));
                        	}
                        }
                    } else {
                        OrRef.Item item = ref.getItem(langId);
                        if (item != null && item.getCurrent() != null) {
                            long typeId = ref.getType().id;
                            if (typeId == Kernel.IC_BLOB)
                                str = new Attribute("src", convertToString(typeId, item.getCurrent()));
                            else {
                                str.setValue(convertToString(typeId, item.getCurrent()));
                                if (typeId == Kernel.IC_FLOAT)
                                	fmt = new Attribute("type", String.valueOf(typeId));
                            }
                        }
                    }
                    if (!showNulls && "0".equals(str.getValue())) str.setValue("");
                    if (str.getValue().length() == 0) str.setValue(defText_);

                    Element valueElement = new Element("Value");
                    valueElement.setAttribute(str);
                    if (!hasParent) valueElement.setAttribute("decoration", "bold");
                    if (fmt != null) valueElement.setAttribute(fmt);

                    parentElement.addContent(valueElement);

                    for (int i = 0; i < children.size(); i++) {
                        ColumnExReportNode child = (ColumnExReportNode) children.get(i);
                        child.num = num + (j + 1) + ".";
                        child.print();
                    }
                }
            } else if (ref == null && exprRefs.size() == 0) {
                if (!hasParent) {
                    Element column = new Element("Column");
                    column.setAttribute("id", String.valueOf(id));
                    column.setAttribute("tableId", String.valueOf(tableId));
                    parentElement.addContent(column);
                    parentElement = column;
                }
                for (int j = 0; j < tableRef.getItems(0).size(); ++j) {
                    Attribute str = new Attribute("str", "");
                    tableRef.absolute(j, this);
                    if (expr != null && expr.equals("getNum()")) {
                        str.setValue(num + (j + 1));
                    }
                    if (!showNulls && "0".equals(str.getValue())) str.setValue("");
                    Element valueElement = new Element("Value");
                    valueElement.setAttribute(str);
                    if (!hasParent) valueElement.setAttribute("decoration", "bold");
                    parentElement.addContent(valueElement);
                    for (int i = 0; i < children.size(); i++) {
                        ColumnExReportNode child = (ColumnExReportNode) children.get(i);
                        child.num = num + (j + 1) + ".";
                        child.print();
                    }
                }
            }
        }

        boolean print(Element parentElement) throws KrnException {
            boolean res = false;
            Element temp = null;

            List<Item> items = !calculated ?
                    ref.getItems(langId) : ((exprRef!=null) ? exprRef.getItems(langId) : new ArrayList<Item>());
            if (ref != null && calculated) items = ref.getItems(langId);

            if (items.size() > 0) {
                if (!hasParent) {
                    Element column = new Element("Column");
                    column.setAttribute("id", String.valueOf(id));
                    column.setAttribute("tableId", String.valueOf(tableId));
                    temp = column;
                }
                for (int j = 0; j < items.size(); ++j) {
                    Attribute str = new Attribute("str", "");
                    Attribute fmt = null;
                    tableRef.absolute(j, this);
                    //  tableRef.fireValueChangedEvent(-1, this);
                    if (calculated) {
                        Object v = exprRef.getValue(langId);
                        if (v != null && v.equals("num")) {
                            str.setValue(num + (j + 1));
                        } else if (v != null) {
                        	if (v instanceof byte[]) {
            					byte[] v1 = (byte[]) v;
            					if (v1.length > 0) {
            						try {
            							File f = Funcs.createTempFile("image", null, WebController.WEB_IMAGES_DIRECTORY);
            							((WebFrame)frame).getSession().deleteOnExit(f);
            							FileOutputStream os = new FileOutputStream(f);
            							os.write(v1);
            							os.close();

            	                        str = new Attribute("src", f.getAbsolutePath());
            						} catch (IOException e) {
            							log.error(e, e);
            						}
            					}
                        	} else {
	                            str.setValue(convertToString(v, format, dateFormat));
	                            if (format == 0)
	                            	fmt = new Attribute("type", String.valueOf(Kernel.IC_FLOAT));
                        	}
                        }
                    } else {
                        OrRef.Item item = ref.getItem(langId);
                        if (item != null && item.getCurrent() != null) {
                            long typeId = ref.getType().id;
                            if (typeId == Kernel.IC_BLOB)
                                str = new Attribute("src", convertToString(typeId, item.getCurrent()));
                            else {
                                str.setValue(convertToString(typeId, item.getCurrent()));
                                if (typeId == Kernel.IC_FLOAT)
                                	fmt = new Attribute("type", String.valueOf(typeId));
                            }
                        }
                    }
                    if (!showNulls && "0".equals(str.getValue())) str.setValue("");
                    Element valueElement = new Element("Value");
                    valueElement.setAttribute(str);
                    if (!hasParent) valueElement.setAttribute("decoration", "bold");
                    if (fmt != null) valueElement.setAttribute(fmt);
                    if (temp != null)
                        temp.addContent(valueElement);
                    else
                        temp = valueElement;

                    for (int i = 0; i < children.size(); i++) {
                        ColumnExReportNode child = (ColumnExReportNode) children.get(i);
                        child.num = num + (j + 1) + ".";

                        boolean r = child.print(temp);
                        res |= r;
                    }
                }
            } else if (ref == null && exprRefs.size() == 0) {
                if (!hasParent) {
                    Element column = new Element("Column");
                    column.setAttribute("id", String.valueOf(id));
                    column.setAttribute("tableId", String.valueOf(tableId));
                    temp = column;
                }
                for (int j = 0; j < tableRef.getItems(0).size(); ++j) {
                    Attribute str = new Attribute("str", "");
                    tableRef.absolute(j, this);
                    if (expr != null && expr.equals("getNum()")) {
                        str.setValue(num + (j + 1));
                    }
                    if (!showNulls && "0".equals(str.getValue())) str.setValue("");
                    Element valueElement = new Element("Value");
                    valueElement.setAttribute(str);
                    if (!hasParent) valueElement.setAttribute("decoration", "bold");
                    if (temp != null)
                        temp.addContent(valueElement);
                    else
                        temp = valueElement;
                    for (int i = 0; i < children.size(); i++) {
                        ColumnExReportNode child = (ColumnExReportNode) children.get(i);
                        child.num = num + (j + 1) + ".";

                        boolean r = child.print(temp);
                        res |= r;
                    }
                }
            }
            if (res) parentElement.addContent(temp);
            return res;
        }
    }


    class ConsColumnExReportNode extends ColumnReportNode {
        List<OrRef> consRefs = new ArrayList<OrRef>();
        OrCalcRef groupCalcRef;
        String groupExpr;
        //OrRef consRef2;
        OrRef ref2;
        OrRef multiConsRef;
        int filterId = 0;
        private int evaluationCounter_ = 0;
        private int maxCount_ = 1;


        public ConsColumnExReportNode(ReportNode parent, Node n, Map<String, OrRef> refs, List<OrCalcRef> calcRefs,
                                      Element parentNode, OrFrame frame) throws KrnException {
            super(parent, n, refs, calcRefs, parentNode, frame);
            Node pathNode = n.getAttributes().getNamedItem("group");
            String path = (pathNode != null) ? pathNode.getNodeValue() : null;
            if (path != null && path.length() > 0) {
                StringTokenizer st = new StringTokenizer(path, "@");
                while (st.hasMoreTokens()) {
                    String consPath = st.nextToken();
                    OrRef consRef = createRef(consPath, 0, refs);
                    consRef.addOrRefListener(this);
                    consRefs.add(consRef);
                    maxCount_++;
                }
            }
            //Node tempChild = n.getFirstChild();
            pathNode = n.getAttributes().getNamedItem("groupExpression");
            groupExpr = (pathNode != null) ? pathNode.getNodeValue() :
                    /*(tempChild != null) ? tempChild.getLastChild().getNodeValue() :*/ null;


            if (groupExpr != null) {
                groupCalcRef = new OrCalcRef(groupExpr, this instanceof ColumnReportNode,
                            MODE, refs, frame.getTransactionIsolation(),
                            frame, null, null, this);
                calcRefs.add(exprRef);
                if (!groupCalcRef.hasParents())
                    groupCalcRef.refresh(this);
                groupCalcRef.addOrRefListener(this);
                groupCalcRef.setReportRef(ReportPrinterAdapter.this.getRef());
                groupCalcRef.setTableRef(tableRef);
                maxCount_++;
            }
            if (ref == null) maxCount_--;


            if (exprRef != null) maxCount_+=exprRef.getParentsSize();
        }


        void print() throws KrnException {
            List<List<Item>> consItems = new ArrayList<List<Item>>();
            for (int i=0; i<consRefs.size(); i++)
                consItems.add(consRefs.get(i).getItems(0));

            MultiMap<String, Object> dict = new MultiMap<String, Object>();

            List<Item> items = !calculated ?
                    ref.getItems(langId) :
                    (exprRef != null ?
                    exprRef.getItems(langId) :
                    tableRef.getItems(0));

            for (int i = 0; i < items.size(); ++i) {
                String key = "";
                for (int j=0; j < consItems.size(); j++) {
                	List<Item> consItems2 = consItems.get(j);
                    Object objKey = consItems2.get(i).getCurrent();
                    if (objKey instanceof Number &&
                            ((Number)objKey).intValue() == 0)
                        objKey = null;
                    if (objKey != null) {
                        key += convertValueToString(consRefs.get(j).getType().id, objKey);
                    }
                }
                if (groupCalcRef != null) {
                    OrRef.Item item = groupCalcRef.getItem(0, i);
                    Object objKey = null;
                    if (item != null)
                        objKey = item.getCurrent();
                    if (objKey instanceof List) {
                        for (int k = 0; k < ((List)objKey).size(); k++) {
                            Object lobjKey = ((List)objKey).get(k);
                            if (lobjKey instanceof Number &&
                                    ((Number)lobjKey).intValue() == 0)
                                lobjKey = null;
                            if (lobjKey != null) {
                                String str= key + convertValueToString(-1, lobjKey);
                                dict.put(str, new ArrayList<Object>());
                            }
                        }
                    }
                } else {
                    dict.put(key, new ArrayList<Object>());
                }
            }

            for (int i = 0; i < items.size(); ++i) {
                Object tableObj = tableRef.getItem(0, i).getCurrent();
                if (tableObj != null && (filteredIds == null
                        || filteredIds.contains(new Long(((KrnObject) tableObj).id)))) {
                    if (uniqueRef != null) {
                        Object uniqueObj = uniqueRef.getItem(langId, i).getCurrent();
                        if (uniqueObj instanceof KrnObject) {
                            Long id = new Long(((KrnObject) uniqueObj).id);
                            if (uniqueObjs.contains(id))
                                continue;
                            uniqueObjs.add(id);
                        } else if (uniqueObj instanceof String) {
                            if (uniqueObjs.contains(uniqueObj))
                                continue;
                            uniqueObjs.add(uniqueObj);
                        }
                    }

                    String key = "";
                    for (int j=0; j < consItems.size(); j++) {
                        List<Item> consItems2 = consItems.get(j);
                        Object objKey = consItems2.get(i).getCurrent();
                        if (objKey instanceof Number &&
                                ((Number)objKey).intValue() == 0)
                            objKey = null;
                        if (objKey != null) {
                            key += convertValueToString(consRefs.get(j).getType().id, objKey);
                        }
                    }
                    Object value = null;
                    if (calculated) {
                        OrRef.Item item = exprRef.getItem(langId, i);
                        if (item != null)
                            value = item.getCurrent();
                    } else {
                        OrRef.Item item = items.get(i);
                        value = (item != null) ? item.getCurrent() : null;
                    }
                    if (groupCalcRef != null) {
                        OrRef.Item item = groupCalcRef.getItem(0, i);
                        Object objKey = null;
                        if (item != null)
                            objKey = item.getCurrent();
                        if (objKey instanceof List) {
                            if (value instanceof List) {
                                for (int k = 0; k < ((List)objKey).size(); k++) {
                                    Object lobjKey = ((List)objKey).get(k);
                                    if (lobjKey instanceof Number &&
                                            ((Number)lobjKey).intValue() == 0)
                                        lobjKey = null;
                                    if (lobjKey != null) {
                                        String str= key + convertValueToString(-1, lobjKey);
                                        dict.put(str, ((List)value).get(k));
                                    }
                                }
                            } else {
                                for (int k = 0; k < ((List)objKey).size(); k++) {
                                    Object lobjKey = ((List)objKey).get(k);
                                    if (lobjKey instanceof Number &&
                                            ((Number)lobjKey).intValue() == 0)
                                        lobjKey = null;
                                    if (lobjKey != null) {
                                        String str= key + convertValueToString(-1, lobjKey);
                                        dict.put(str, value);
                                    }
                                }
                            }
                        }
                    } else {
                        dict.put(key, value);
                    }
                }
            }

            Element column = new Element("Column");
            column.setAttribute("id", String.valueOf(id));
            column.setAttribute("tableId", String.valueOf(tableId));
            if (dateFormat != null) {
                column.setAttribute("dateFormat", dateFormat);
            }
            parentNode.addContent(column);

            for (Iterator<String> it = dict.keySet().iterator(); it.hasNext();) {
                Attribute str = new Attribute("str", "");
                List<Object> values = dict.get(it.next());
                if (values == null || values.size() == 0)
                    str.setValue("0");
                else {
                    Object value = values.get(0);
                    if (value instanceof String)
                        str.setValue((String) value);
                    else if ((calculated && format == 0) ||
                            (ref != null && ref.getType().id == Kernel.IC_FLOAT)) {
                        double sum = 0.0;
                        for (Object value1 : values) {
                            Double v = (Double) value1;
                            sum += (v != null) ? v : 0;
                        }
                        str.setValue(floatFormat_.format(sum));
                    } else if ((calculated && (format == 1 || format == 5)) ||
                                (ref != null && (ref.getType().id == Kernel.IC_BOOL ||
                                                 ref.getType().id == Kernel.IC_INTEGER))) {
                        long sum = 0;
                        for (int i = 0; i < values.size(); ++i) {
                            Number v = (Number) values.get(i);
                            sum += (v != null) ? v.longValue() : 0;
                        }
                        str.setValue("" + sum);
                    } else if (ref != null &&
                            (ref.getType().id == Kernel.IC_STRING ||
                            ref.getType().id == Kernel.IC_MEMO)) {
                        str.setValue("");
                    } else
                        str.setValue("" + values.size());
                }
                if (!showNulls && "0".equals(str.getValue())) str.setValue("");
                Element valueElement = new Element("Value");
                valueElement.setAttribute(str);
                column.addContent(valueElement);
            }
            //}
//            if (filter != null)
//                tableRef.removeFilter(filter.obj.id);
        }


        boolean print(Element parentElement) throws KrnException {
            boolean res = false;

            List<List<Item>> consItems = new ArrayList<List<Item>>();
            for (OrRef consRef : consRefs) consItems.add(consRef.getItems(0));

            MultiMap<String, Object> dict = new MultiMap<String, Object>();

            List<Item> items = !calculated ?
                    ref.getItems(langId) :
                    tableRef.getItems(0);

            for (int i = 0; i < items.size(); ++i) {
                String key = "";
                for (int j=0; j < consItems.size(); j++) {
                	List<Item> consItems2 = consItems.get(j);
                    Object objKey = consItems2.get(i).getCurrent();
                    if (objKey instanceof Number &&
                            ((Number)objKey).intValue() == 0)
                        objKey = null;
                    if (objKey != null) {
                        key += convertValueToString(consRefs.get(j).getType().id, objKey);
                    }
                }

                dict.put(key, new ArrayList<Object>());
            }

            for (int i = 0; i < items.size(); ++i) {
                Object tableObj = tableRef.getItem(0, i).getCurrent();
                if (tableObj != null && (filteredIds == null
                        || filteredIds.contains(new Long(((KrnObject) tableObj).id)))) {
                    if (uniqueRef != null) {
                        Object uniqueObj = uniqueRef.getItem(langId, i).getCurrent();
                        if (uniqueObj instanceof KrnObject) {
                            long id = ((KrnObject) uniqueObj).id;
                            if (uniqueObjs.contains(id))
                                continue;
                            uniqueObjs.add(id);
                        } else if (uniqueObj instanceof String) {
                            if (uniqueObjs.contains(uniqueObj))
                                continue;
                            uniqueObjs.add(uniqueObj);
                        }
                    }
                    String key = "";
                    for (int j=0; j < consItems.size(); j++) {
                        List<Item> consItems2 = consItems.get(j);
                        Object objKey = consItems2.get(i).getCurrent();
                        if (objKey instanceof Number &&
                                ((Number)objKey).intValue() == 0)
                            objKey = null;
                        if (objKey != null) {
                            key += convertValueToString(consRefs.get(j).getType().id, objKey);
                        }
                    }
                    Object value = null;
                    if (calculated) {
                        OrRef.Item item = exprRef.getItem(langId, i);
                        if (item != null)
                            value = item.getCurrent();
                    } else {
                        OrRef.Item item = items.get(i);
                        value = (item != null) ? item.getCurrent() : null;
                    }
                    dict.put(key, value);
                }
            }

            Element column = new Element("Column");
            column.setAttribute("id", String.valueOf(id));
            column.setAttribute("tableId", String.valueOf(tableId));
            if (dateFormat != null) {
                column.setAttribute("dateFormat", dateFormat);
            }

            for (Iterator<String> it = dict.keySet().iterator(); it.hasNext();) {
                Attribute str = new Attribute("str", "");
                List<Object> values = dict.get(it.next());
                if (values == null || values.size() == 0)
                    str.setValue("0");
                else {
                    Object value = values.get(0);
                    if (value instanceof String)
                        str.setValue((String) value);
                    else if (value instanceof Double) {
                        double sum = 0.0;
                        for (Object value1 : values) {
                            Double v = (Double) value1;
                            sum = (v != null) ? sum + v : sum;
                        }
                        str.setValue(floatFormat_.format(sum));
                    } else if (value instanceof Number) {
                        long sum = 0;
                        for (Object value1 : values) {
                            Number v = (Number) value1;
                            sum = (v != null) ? sum + v.longValue() : sum;
                        }
                        str.setValue("" + sum);
                    } else if (ref != null &&
                            (ref.getType().id == Kernel.IC_STRING ||
                            ref.getType().id == Kernel.IC_MEMO)) {
                        str.setValue("");
                    }
                    else
                        str.setValue("" + values.size());
                }
                if (!showNulls && "0".equals(str.getValue())) str.setValue("");
                Element valueElement = new Element("Value");
                valueElement.setAttribute(str);
                column.addContent(valueElement);
                res = true;
            }
            if (res) parentElement.addContent(column);
            return res;
        }
        // OrRefListener
        public void valueChanged(OrRefEvent e) {
            try {
                if (!printTree && !treePrinted && listen && e.getOriginator() != null && e.getOriginator() != this && !hasParent &&
                        (e.getReason() & OrRefEvent.ITERATING) == 0) {
                    if ((e.getRef() == ref
                            || consRefs.contains(e.getRef())
                            || e.getRef() == tableRef
                            || e.getRef() == ref2
                            || e.getRef() == exprRef
                            || e.getRef() == groupCalcRef
                            )) {
                        if (++evaluationCounter_ == maxCount_) {
                            if (exprRef != null) {
                                exprRef.refresh(this);
                            }
                            print();
                        }
                    }
                }
            } catch (Exception ex) {
                log.error(ex, ex);
            }
        }
    }

    class TreeReportNode extends ColumnReportNode {
        private int level = 10;
        private int level2 = 0;
        private int processingLevel;
        KrnAttribute valueAttr, parentAttr, childrenAttr, titleAttrs[];
        Map<Element, KrnObject> nodeObjects;
        private Element elementInProgress;
        //private boolean removeNullRows = true;
        private boolean readyToPrint = false;
        private boolean printed = false;
        private String rootExpr;
        private String childrenExpr;
        //private OrCalcRef rootExprRef;
        public Filter filterTree;
        private Set<Long> filteredTreeIds_;
        private String tfuid;
        private int firstChildLevel;
        private boolean oneRow = false;

        public TreeReportNode(ReportNode parent, Node n, Map<String, OrRef> refs, List<OrCalcRef> calcRefs, Element parentNode, OrFrame frame)
                throws KrnException {


            super(parent, n, refs, calcRefs, parentNode, frame);


            Node pathNode = n.getAttributes().getNamedItem("level");
            String level = (pathNode != null) ? pathNode.getNodeValue() : null;
            if (level != null && level.length() > 0) this.level = Integer.parseInt(level);


            pathNode = n.getAttributes().getNamedItem("level2");
            level = (pathNode != null) ? pathNode.getNodeValue() : null;
            if (level != null && level.length() > 0) this.level2 = Integer.parseInt(level);

            pathNode = n.getAttributes().getNamedItem("oneRow");
            oneRow = (pathNode != null) ? true : false;

            pathNode = n.getAttributes().getNamedItem("root");
            String rootPath = (pathNode != null) ? pathNode.getNodeValue() : null;

            pathNode = n.getAttributes().getNamedItem("title");
            String titlePath = (pathNode != null) ? pathNode.getNodeValue() : null;

            pathNode = n.getAttributes().getNamedItem("childrenRef");
            String childPath = (pathNode != null) ? pathNode.getNodeValue() : "дети";

            for (int i = 0; i < n.getChildNodes().getLength(); ++i) {
                Node child = n.getChildNodes().item(i);
                if (child != null && "rootExpr".equals(child.getNodeName())) {
                    rootExpr = child.getLastChild().getNodeValue();
                }
                else if (child != null && "childrenExpr".equals(child.getNodeName())) {
                	childrenExpr = child.getLastChild().getNodeValue();
                }
            }

            Node fNode = n.getAttributes().getNamedItem("filterTree");
            tfuid = (fNode != null) ? fNode.getNodeValue() : null;

            long tfid = 0;
            try {
                if (tfuid != null && tfuid.length() > 0) {
                    KrnObject[] fobjs = KRN_.getObjectsByUid(new String[]{tfuid}, 0);
                    long[] fids = Funcs.makeObjectIdArray(fobjs);

                    if (fids != null && fids.length > 0) {
                        tfid = fids[0];
                        long flags = readFlags(fids[0]);
                        KrnClass fcls = KRN_.getClassByName("Filter");
                        KrnObject obj = new KrnObject(tfid, "", fcls.id);
                        filterTree = new Filter(obj, langId, "", flags);
                    }
                }
            } catch (Exception ex) {
                log.error("-----===== The filter is not assigned or not found =====-----");
                log.error(ex, ex);
            }

            if (rootPath == null && rootExpr == null) {
                tree2 = new TreeAdapter2(tableRef.toString(), titlePath, childPath, childrenExpr, refs, langId, tfid, frame);
                tree2.dataRef.addOrRefListener(this);
            } else {
	            tree = new TreeAdapter(rootPath, titlePath, rootExpr, refs, langId, frame);
	            if (tree.rootCalcRef != null) tree.rootCalcRef.addOrRefListener(this);
	            else if (tree.rootRef != null) tree.rootRef.addOrRefListener(this);
	
	            ClassNode cn = KRN_.getClassNode(tree.rootRef.getType().id);
	            valueAttr = cn.getAttribute("значение");
	            parentAttr = cn.getAttribute("родитель");
	            childrenAttr = cn.getAttribute("дети");
	
	            if (titlePath != null && titlePath.length() > 0) {
	                titleAttrs = kz.tamur.rt.Utils.getAttributesForPath(titlePath, frame.getKernel());
	            }
	
	            if (rootPath.equals(ReportPrinterAdapter.this.getRef().toString()))
	                tree.setRoot(ReportPrinterAdapter.this.getRef().getItem(0).getRec());
	
	            tree.getRoot();
            }
        }


        OrRef createRef(String path, int langId, Map<String, OrRef> refs) throws KrnException {
            OrRef res = OrRef.createRef(path, true, MODE, refs,
                    frame.getTransactionIsolation(), frame);
            res.addLanguage(langId);
            return res;
        }


        public void valueChanged(OrRefEvent e) {
            if (!listen) return;
            if (tree != null) {
	            if (e.getOriginator() == null || e.getRef() == tree.rootRef || e.getRef() == tree.rootCalcRef) {
	                tree.valueChanged(e);
	            }
	            if (e.getOriginator() == ReportPrinterAdapter.this && (e.getRef() != tree.rootRef || ref == tree.rootRef) && e.getRef() != tree.rootCalcRef) {
	            	if (printTree) readyToPrint = true;
	            }
	            if (tree.getRoot() != null && readyToPrint && !printed) {
	                if (printTree) {
	                    printed = true;
	                    if (tree.rootCalcRef != null) {
	                        tree.rootCalcRef.removeOrRefListener(this);
	                        tree.rootCalcRef.removeFromParents();
	                    } else if (tree.rootRef != null) {
	                        tree.rootRef.removeOrRefListener(this);
	                    }
	                    try {
	                        if (ref != null || exprRef != null) print();
	                    } catch (Exception ex) {
	                        log.error(ex, ex);
	                    }
	                }
	                //super.valueChanged(e);
	            }
            } else if (tree2 != null) {
                OrRef ref = e.getRef();
                if (tree2.dataRef == ref) {
                    if (printTree) {
                        if (e.getReason() == OrRefEvent.ROOT_ITEM_CHANGED && !printed) {
                            // Обновление корня
                            printed = true;
                            if (tree2.dataRef != null) {
                                tree2.dataRef.removeOrRefListener(this);
                            }
                            try {
                                tree2.populateRootForReport();
                                print2();
                            } catch (Exception ex) {
                                log.error(ex, ex);
                            }
                        }
                    }
                }
            }
        }

        void print2() throws KrnException {
            treePrinted = true;
            OrRef titleRef = tree2.getTitleRef();
            List<Item> items = tree2.dataRef.getItems(0);
            List<TreeAdapter2.Node> nodes = tree2.getListView();
            log.info("Processing Tree id = " + id + " (size = " + items.size() + ")");
            if (items.size() > 0) {
                for (int i = 0; i < children.size(); ++i) {
                    if (children.get(i) instanceof ColumnReportNode) {
                        ColumnReportNode crn = (ColumnReportNode) children.get(i);
                        if (crn.exprRef != null)
                            crn.exprRef.refresh(this);
                    }
                }

                Element column = new Element("TreeColumn");
                column.setAttribute("id", String.valueOf(id));
                column.setAttribute("tableId", String.valueOf(tableId));
                column.setAttribute("remove", "no");
                // if (oneRow)
                column.setAttribute("oneRow", "1");
                if (children.size() == 0)
                    column.setAttribute("remove", "false");

                Element lastParent = column;
                int lastLevel = -1;

                for (int j = 0; j < items.size(); ++j) {
                    Item titleItem = titleRef.getItem(langId, j);
                    int level = ((TreeAdapter2.Node) nodes.get(j)).getLevel();
                    
                    if (level < this.level) continue;

                    String name = (titleItem != null && titleItem.getCurrent() != null) ? (String) titleItem.getCurrent() : "";

                    Element valueElement = new Element("Value");
                    valueElement.setAttribute("str", name);

                    for (int k = 0; k <= lastLevel - level; k++) {
                        lastParent = lastParent.getParentElement();
                    }

                    lastParent.addContent(valueElement);
                    lastParent = valueElement;
                    lastLevel = level;

                    for (int i = 0; i < children.size(); ++i) {
                        if (children.get(i) instanceof ColumnReportNode) {
                            ColumnReportNode crn = (ColumnReportNode) children.get(i);

                            Element column2 = new Element("Column");
                            column2.setAttribute("id", String.valueOf(crn.id));
                            column2.setAttribute("tableId", String.valueOf(crn.tableId));
                            if (crn.dateFormat != null) {
                                column2.setAttribute("dateFormat", crn.dateFormat);
                            }
                            if (crn.calculated && crn.expr.startsWith("getNum()")) {
                                column2.setAttribute("nosort", "1");
                                column2.setAttribute("numType", "1");
                            }
                            if (crn.calculated && crn.expr.startsWith("getNum2()")) {
                                column2.setAttribute("nosort", "1");
                                column2.setAttribute("numType", "2");
                            }

                            Element valueElement2 = crn.evaluate(j);
                            if (valueElement2 != null)
                                column2.addContent(valueElement2);

                            valueElement.addContent(column2);
                        }
                    }
                }
                parentNode.addContent(column);
            }
            treePrinted = false;
        }

        void print() throws KrnException {
            treePrinted = true;
            List<Item> items = ref.getItems(0);
            if (items.size() > 0) {
                List<Item> titems = tableRef.getItems(0);
                MultiMap<Long, Item> mmap = globalMap.get(ref.toString());
                nodeObjects = globalNodeMap.get(ref.toString());
                Element rootElement = globalRoots.get(ref.toString());
                if (mmap == null) {
                    Map<Long, Element> elements = new TreeMap<Long, Element>();
                    List<Long> objIds = new ArrayList<Long>();
                    mmap = new MultiMap<Long, Item>();
                    nodeObjects = new HashMap<Element, KrnObject>();
                    TreeAdapter.Node root = tree.getRoot();
                    for (int i = 0; i < items.size(); ++i) {
                        KrnObject obj = (KrnObject) items.get(i).getCurrent();
                        if (obj != null) {
                            Long objId = new Long(obj.id);
                            mmap.put(objId, tableRef.getItems(0).get(i));

                            Record prec = null;
                            boolean isParent = false;
                            do {
                                if (!objIds.contains(obj.id) && (isParent || filteredTreeIds_ == null
                                        || filteredTreeIds_.contains(obj.id))) {
                                    prec = getParent(obj.id);
                                    objIds.add(obj.id);
                                    if (prec != null) obj = (KrnObject) prec.getValue();
                                    isParent = true;
                                } else break;
                            } while (prec != null && obj.id != root.getObject().id);
/*
                            Element oldValue = null;
                            Record prec = null;
                            do {
                                Element e = (Element) elements.get(new Long(obj.id));
                                if (e == null) {
                                    prec = getParent(obj.id);
                                    Record vrec = getValue(obj.id);
                                    Record trec = null;
                                    if (vrec != null) {
                                        KrnObject value = (KrnObject) vrec.getValue();
                                        trec = getTitleRecord(value);
                                    }
                                    Element value = new Element("Value");
                                    value.setAttribute("str",
                                            (trec != null) ? (String) trec.getValue() : "");
                                    if (oldValue != null) value.addContent(0, oldValue);
                                    elements.put(new Long(obj.id), value);
                                    nodeObjects.put(value, obj);
                                    oldValue = value;
                                    if (prec != null) obj = (KrnObject) prec.getValue();
                                } else {
                                    if (oldValue != null) e.addContent(0, oldValue);
                                    break;
                                }
                            } while (prec != null);
*/
                        }
                    }
                    //TreeAdapter.Node root = tree.getRoot();
                    if (!objIds.contains(root.getObject().id)) objIds.add(root.getObject().id);
                    rootElement = formTree(root.getObject(), elements, objIds, "0");
                    if (level < 0)
                        level = firstChildLevel + level;
                    if (level2 < 0)
                        level2 = firstChildLevel + level2;


                    //rootElement = (Element)elements.get(new Long(root.getObject().id));
                    globalRoots.put(ref.toString(), rootElement);
                    globalMap.put(ref.toString(), mmap);
                    globalNodeMap.put(ref.toString(), nodeObjects);
                }
                Element column = new Element("TreeColumn");
                column.setAttribute("id", String.valueOf(id));
                column.setAttribute("tableId", String.valueOf(tableId));
                if (oneRow)
                	column.setAttribute("oneRow", "1");
                if (children.size() == 0) column.setAttribute("remove", "false");
                if (rootElement != null) {
                    processNode(rootElement, nodeObjects, mmap, column, 0);
                    int i = 0;
                    boolean cont = true;
                    List<Element> res = new ArrayList<Element> ();
                    Element root = (Element) rootElement.clone();
                    removeColumns(rootElement);
                    res.add(root);
                    while (i++ < level2 && cont) {
                        List<Element> temp = new ArrayList<Element> ();
                        for (int j = 0; j<res.size(); j++) {
                            Element e = res.get(j);
                            List<Element> content = e.getChildren("Value");
                            if (content != null) {
                                temp.addAll(content);
                                int size = content.size();
                                for (int k=size-1; k >= 0; k--) {
                                    List<Element> chs = content.get(k).getChildren("Column");
                                    if (chs != null && chs.size() > 0) cont = false;
                                    e.removeContent(content.get(k));
                                }
                            }
                        }
                        res = temp;
                    }
                    column.addContent(res);
                }
                parentNode.addContent(column);
                tableRef.setItems(0, titems, this);
            }
            treePrinted = false;
        }


        private Element formTree(KrnObject obj, Map<Long, Element> elements, List<Long> objIds, String index) throws KrnException {
            Element value = null;
            if (objIds.contains(obj.id)) {
                if (Long.parseLong(index) == 0 && (level < 0 || level2 < 0))
                    firstChildLevel = index.length();
                Record vrec = getValue(obj.id);
                Record trec = null;
                if (vrec != null) {
                    KrnObject val = (KrnObject) vrec.getValue();
                    trec = getTitleRecord(val);
                }
                value = new Element("Value");
                value.setAttribute("str",
                        (trec != null) ? (String) trec.getValue() : "");
                elements.put(obj.id, value);
                nodeObjects.put(value, obj);


                List<Object> children = getChildren(obj.id);
                int nextIndex = 0;
                for (int i = 0; i < children.size(); i++) {
                    KrnObject child = (KrnObject) children.get(i);
                    Element chValue = formTree(child, elements, objIds, index + nextIndex);
                    if (chValue != null) {
                        value.addContent(chValue);
                        nextIndex++;
                    }
                }
            }
            return value;
        }

        private void removeColumns(Element e) {
            if (!e.getName().equals("Value")) {
                e.getParent().removeContent(e);
            } else {
                for (int i = e.getContentSize()-1; i>=0; i--) {
                    removeColumns((Element)e.getChildren().get(i));
                }
            }
        }

        private Record getParent(long id) throws KrnException {
            return getCash().getRecord(id, parentAttr, 0, 0);
        }


        public Record getValue(long id) throws KrnException {
            return getCash().getRecord(id, valueAttr, 0, 0);
        }


        public List<Object> getChildren(long id) throws KrnException {
            List<Object> res = new ArrayList<Object>();
            SortedSet<Record> chObjRecs = getCash().getRecords(new long[] {id}, childrenAttr, 0, null);
            for (Record r : chObjRecs) {
                 res.add(r.getValue());
            }
            return res;
        }


        private Record getTitleRecord(KrnObject value)
                throws KrnException {
            final Cache cash = getCash();
            Record rec = null;
            for (int i = 0; i < titleAttrs.length - 1; i++) {
                long[] valueIds = {value.id};
                SortedSet<Record> recs = cash.getRecords(valueIds, titleAttrs[i], 0, null);
                if (recs.size() > 0) {
                    rec = recs.last();
                    value = (KrnObject) rec.getValue();
                } else {
                    return null;
                }
            }
            long lid = (langId == 0) ? frame.getDataLang().id : langId;
            long[] valueIds = {value.id};
            SortedSet<Record> recs = cash.getRecords(valueIds, titleAttrs[titleAttrs.length - 1], lid,
                    null);
            if (recs.size() > 0) {
                rec = recs.last();
            } else {
                return null;
            }
            return rec;
        }

        boolean processNode(TreeAdapter.Node n, MultiMap<Long, Item> mmap, Element parentElement, int level) throws KrnException {
            nodeInProgress = n;
            processingLevel = level;
            boolean res = false;

            if (n == null) return res; //daulet+ 07.12.2004
            Element valueElement = new Element("Value");
            String nodeName = (n.toString() != null) ?
                    n.toString() : "";
            valueElement.setAttribute("str", nodeName);
            List<Item> items;
            List<Long> objs = new ArrayList<Long>();
            if (level == this.level) {
                items = getChildItems(n, mmap, objs);
            } else {
                items = mmap.get(n.getObject().id);
                objs.add(n.getObject().id);
            }
            if (items != null && items.size() > 0) {
                tableRef.setItems(0, items, this);
            } else {
                tableRef.setItems(0, new ArrayList<Item>(), this);
                List<Item> l = new ArrayList<Item>();
                l.add(ref.new Item(n.getObject()));
                ref.setItems(langId, l, this);
            }

            for (int i = 0; i < children.size(); ++i) {
                if (children.get(i) instanceof ConsReportNode) {
                    res |= ((ConsReportNode) children.get(i)).print(valueElement, objs);
                } else {
                    res |= children.get(i).print(valueElement);
                }
            }

            if (level < this.level) {
                for (int i = 0; i < n.getChildCount(); ++i) {
                    boolean r = processNode((TreeAdapter.Node) n.getChildAt(i), mmap, valueElement, level + 1);
                    res |= r;
                }
            }

            if (children.size() == 0) res = true;
            if (res) parentElement.addContent(valueElement);
            return res;
        }

        boolean processNode(Element e, Map<Element, KrnObject> nodeObjects, MultiMap<Long, Item> mmap, Element parentElement, int level) throws KrnException {
            processingLevel = level;
            elementInProgress = e;
            boolean res = false;

            KrnObject nodeObj = nodeObjects.get(e);
            if (nodeObj == null) return true;
            List<Item> items;
            List<Long> objs = new ArrayList<Long>();
            if (level == this.level) {
                items = getChildItems(e, nodeObjects, mmap, objs);
            } else {
                items = mmap.get(nodeObj.id);
                objs.add(nodeObj.id);
            }
            if (items != null && items.size() > 0) {
                tableRef.setItems(0, items, this);
            } else {
                tableRef.setItems(0, new ArrayList<OrRef.Item>(), this);
                List<OrRef.Item> l = new ArrayList<OrRef.Item>();
                l.add(ref.new Item(nodeObj));
                ref.setItems(langId, l, this);
            }

            for (int i = 0; i < children.size(); ++i) {
                if (children.get(i) instanceof ConsReportNode) {
                    res |= ((ConsReportNode) children.get(i)).print(e, objs);
                } else {
                    res |= children.get(i).print(e);
                }
            }

            if (level < this.level) {
                for (int i = 0; i < e.getContentSize(); ++i) {
                    boolean r = processNode((Element) e.getChildren().get(i), nodeObjects, mmap, e, level + 1);
                    res |= r;
                }
            }

            if (children.size() == 0) res = true;
            return res;
        }

        private List<Item> getChildItems(TreeAdapter.Node n, MultiMap<Long, Item> mmap, List<Long> objs) {
            List<Item> res = new ArrayList<Item>();
            List<Item> items = mmap.get(n.getObject().id);
            objs.add(n.getObject().id);
            if (items != null) res.addAll(items);
            for (int i = 0; i < n.getChildCount(); ++i) {
                items = getChildItems((TreeAdapter.Node) n.getChildAt(i), mmap, objs);
                if (items != null) res.addAll(items);
            }
            return res;
        }

        private List<Item> getChildItems(Element e, Map<Element, KrnObject> nodeObjects, MultiMap<Long, Item> mmap, List<Long> objs) {
            List<Item> res = new ArrayList<Item>();
            KrnObject obj = nodeObjects.get(e);
            List<Item> items = mmap.get(new Long(obj.id));
            objs.add(obj.id);
            if (items != null) res.addAll(items);
            for (int i = 0; i < e.getContentSize(); ++i) {
                items = getChildItems((Element) e.getChildren().get(i), nodeObjects, mmap, objs);
                if (items != null) res.addAll(items);
            }
            return res;
        }

        public int getLevel() {
            return level;
        }

        public int getProcessingLevel() {
            return processingLevel;
        }

        //public void setRemoveNullRows(boolean b) {
        //    removeNullRows = b;
        //}

        public String getTreeFilterUid() {
            return tfuid;
        }

        public void setFilteredTreeIds(Set<Long> filteredIds) {
            filteredTreeIds_ = filteredIds;
        }
    }

    class ConsReportNode extends ReportNode {
        ConsReportNode(ReportNode parent, Node n, Element parentNode, OrFrame frame)
                throws KrnException {
            super(parent, n, parentNode, frame);
        }

        void print() throws KrnException {
            if (children.size() > 0) {
                Element column = new Element("ConsColumn");
                column.setAttribute("id", String.valueOf(id));
                for (int i = 0; i < children.size(); ++i) {
                    ((ReportNode) children.get(i)).print();
                }
                parentNode.addContent(column);
            }
        }

        boolean print(Element parentElement, List<Long> objs) throws KrnException {
            boolean res = false;
            Element column = new Element("ConsColumn");
            column.setAttribute("id", String.valueOf(id));
            if (children.size() > 0) {
                for (int i = 0; i < children.size(); ++i) {
                    res |= ((ConsValueReportNode) children.get(i)).print(column, objs);
                }
            }
            if (res) parentElement.addContent(column);
            return res;
        }
    }

    class ConsValueReportNode extends ColumnReportNode {
        OrRef consRef;
        OrRef titleRef;

        ConsValueReportNode(ReportNode parent, Node n, Map<String, OrRef> refs, List<OrCalcRef> calcRefs, Element parentNode, OrFrame frame)
                throws KrnException {
            super(parent, n, refs, calcRefs, parentNode, frame);
            String path;

            Node pathNode = n.getAttributes().getNamedItem("group");
            path = (pathNode != null) ? pathNode.getNodeValue() : null;
            if (path != null && path.length() > 0)
                consRef = createRef(path, langId, refs);

            pathNode = n.getAttributes().getNamedItem("treeGroup");
            path = (pathNode != null) ? pathNode.getNodeValue() : null;
            if (path != null && path.length() > 0)
                treeGroupRef = createRef(path, langId, refs);
        }

        void print() throws KrnException {
            MultiMap<String, Integer> mm = new MultiMap<String, Integer>();

            List<Item> items = !calculated ?
                    ref.getItems(langId) : exprRef.getItems(langId);

            if (consRef != null) {
                TreeReportNode trn = (TreeReportNode) this.parent.parent;
                KrnObject treeObj = trn.nodeObjects.get(trn.elementInProgress);

                for (int i = 0; i < items.size(); ++i) {
                    OrRef.Item keyItem = consRef.getItem(0, i);
                    Object treeGroupObj = treeGroupRef.getItem(0, i).getCurrent();
                    Object tableObj = tableRef.getItem(0, i).getCurrent();
                    if (treeObj != null && treeGroupObj != null
                            && treeObj.id == ((KrnObject) treeGroupObj).id
                            && tableObj != null && filteredIds != null
                            && filteredIds.contains(new Long(((KrnObject) tableObj).id))) {
                        if (keyItem != null) {
                            Object key = keyItem.getCurrent();
                            if (key != null)
                                mm.put(convertValueToString(consRef.getType().id, key), i);
                        }
                    }
                }
                for (Iterator<String> it = mm.keySet().iterator(); it.hasNext();) {
                    String key = it.next();
                    List<Integer> inds = mm.get(key);

                    Element consValue = new Element("ConsValue");
                    consValue.setAttribute("id", "" + id);
                    consValue.setAttribute("str", "" + calculate(inds));
                    consValue.setAttribute("title", key);
                    parentNode.addContent(consValue);
                }
            } else if (items.size() > 0) {
                TreeReportNode trn = (TreeReportNode) this.parent.parent;
                KrnObject treeObj = trn.nodeObjects.get(trn.elementInProgress);

                List<Integer> inds = new ArrayList<Integer>();
                for (int i = 0; i < items.size(); ++i) {
                    Object treeGroupObj = treeGroupRef.getItem(0, i).getCurrent();
                    Object tableObj = tableRef.getItem(0, i).getCurrent();
                    if (treeObj != null && treeGroupObj != null
                            && treeObj.id == ((KrnObject) treeGroupObj).id
                            && tableObj != null && filteredIds != null
                            && filteredIds.contains(new Long(((KrnObject) tableObj).id)))
                        inds.add(i);
                }
                if (inds.size() > 0) {
                    Element consValue = new Element("ConsValue");
                    consValue.setAttribute("id", "" + id);
                    consValue.setAttribute("str", "" + calculate(inds));
                    consValue.setAttribute("title", "");
                    parentNode.addContent(consValue);
                }
            }


        }

        boolean print(Element parentElement, List<Long> objs) throws KrnException {
            boolean res = false;
            MultiMap<String, Integer> mm = new MultiMap<String, Integer>();

            List<Item> items = !calculated ?
                    ref.getItems(langId) : tableRef.getItems(0);
            if (tableRef.getItems(0) != null &&
                    tableRef.getItems(0).size() > 0) {
                if (consRef != null) {
                    for (int i = 0; i < items.size(); ++i) {
                        OrRef.Item keyItem = consRef.getItem(0, i);
                        Object treeGroupObj = treeGroupRef.getItem(0, i).getCurrent();
                        Object tableObj = tableRef.getItem(0, i).getCurrent();

                        if (treeGroupObj != null
                                && objs.contains(((KrnObject) treeGroupObj).id)
                                && tableObj != null && (filteredIds == null
                                || filteredIds.contains(((KrnObject) tableObj).id))) {
                            if (uniqueRef != null) {
                                Object uniqueObj = uniqueRef.getItem(langId, i).getCurrent();
                                if (uniqueObj instanceof KrnObject) {
                                    Long id = ((KrnObject) uniqueObj).id;
                                    if (uniqueObjs.contains(id))
                                        continue;
                                    uniqueObjs.add(id);
                                } else if (uniqueObj instanceof String) {
                                    if (uniqueObjs.contains(uniqueObj))
                                        continue;
                                    uniqueObjs.add(uniqueObj);
                                }
                            }
                            if (keyItem != null) {
                                Object key = keyItem.getCurrent();
                                if (key != null)
                                    mm.put(convertValueToString(consRef.getType().id, key), i);
                            }
                        }
                    }
                    if (mm.keySet().size() > 0) res = true;
                    for (Iterator<String> it = mm.keySet().iterator(); it.hasNext();) {
                        String key = it.next();
                        List<Integer> inds = mm.get(key);
                        Element consValue = new Element("ConsValue");
                        consValue.setAttribute("id", "" + id);
                        consValue.setAttribute("str", "" + calculate(inds));
                        consValue.setAttribute("title", key);
                        parentElement.addContent(consValue);
                    }
                } else if (items.size() > 0) {
                    List<Integer> inds = new ArrayList<Integer>();
                    for (int i = 0; i < items.size(); ++i) {
                        Object treeGroupObj = treeGroupRef.getItem(0, i).getCurrent();
                        Object tableObj = tableRef.getItem(0, i).getCurrent();
                        if (treeGroupObj != null
                                && objs.contains(((KrnObject) treeGroupObj).id)
                                && tableObj != null && (filteredIds == null
                                || filteredIds.contains(new Long(((KrnObject) tableObj).id)))) {
                            if (uniqueRef != null) {
                                Object uniqueObj = uniqueRef.getItem(langId, i).getCurrent();
                                if (uniqueObj instanceof KrnObject) {
                                    Long id = ((KrnObject) uniqueObj).id;
                                    if (uniqueObjs.contains(id))
                                        continue;
                                    uniqueObjs.add(id);
                                } else if (uniqueObj instanceof String) {
                                    if (uniqueObjs.contains(uniqueObj))
                                        continue;
                                    uniqueObjs.add(uniqueObj);
                                }
                            }
                            inds.add(i);
                        }
                    }
                    if (inds.size() > 0) {
                        Element consValue = new Element("ConsValue");
                        consValue.setAttribute("id", "" + id);
                        consValue.setAttribute("str", "" + calculate(inds));
                        consValue.setAttribute("title", "");
                        parentElement.addContent(consValue);
                        res = true;
                    }
                }
            }
            return res;
        }

        private Object calculate(List<Integer> inds) throws KrnException {
            KrnClass type = !calculated ? ref.getType() :
                    KRN_.getClassNode(Kernel.IC_FLOAT).getKrnClass();
            if (type.id == Kernel.IC_INTEGER) {
                int res = 0;
                List<Item> items = ref.getItems(0);
                for (int i = 0; i < inds.size(); i++) {
                    int j = inds.get(i);
                    Number val = (Number) items.get(j).getCurrent();
                    if (val != null)
                        res += val.longValue();
                }
                return res;
            } else if (type.id == Kernel.IC_FLOAT) {
                double res = 0;
                for (int i = 0; i < inds.size(); i++) {
                    int j = inds.get(i);
                    if (calculated) {
                        List<Item> items = exprRef.getItems(langId);
                        Double val = (Double) items.get(j).getCurrent();
                        if (val != null)
                            res += val;
                    } else {
                        List<Item> items = ref.getItems(langId);
                        Number val = (Number) items.get(j).getCurrent();
                        if (val != null)
                            res += val.doubleValue();
                    }
                }
                return res;
            } else if (type.id == Kernel.IC_STRING) {
                return  (inds.size() > 0 &&
                        ref.getItem(langId).getCurrent() != null)
                        ? ref.getItem(langId).getCurrent() : "";
            } else
                return inds.size();
        }
    }

    public OrRef getRef() {
        return ref;
    }

    public void setReqGroup(int groupId) {
    }

    public int getReqGroup() {
        return 0;
    }

    public void setConstraintMessage(String msg) {
    }

    public void setConstraintExpression(String expr) {
    }

    protected KrnObject[] makeObjectArray(List<Item> items) {
        KrnObject[] res = null;

        if (items.size() > 0) {
            res = new KrnObject[items.size()];

            for (int i = 0; i < items.size(); ++i) {
                Item item = items.get(i);
                res[i] = (KrnObject) item.getCurrent();
            }
        }

        return res;
    }

    private String convertToString(long typeId, Object obj) {
        String str = "";
        if (typeId == Kernel.IC_DATE) {
        	if (!(obj instanceof Date)) return "";
        	str = format_.format((Date)obj);
        } else if (typeId == Kernel.IC_TIME)
            str = timeformat_.format((Date)obj);
        else if (typeId == Kernel.IC_BLOB)
            str = ((File) obj).getAbsolutePath();
        else if (typeId == Kernel.IC_FLOAT)
            str = floatFormat_.format(((Number) obj).doubleValue());
        else if (typeId == Kernel.IC_INTEGER)
            str = "" + ((Number) obj).longValue();
        else if (obj instanceof KrnObject) {
            str = "" + ((KrnObject) obj).id;
        } else
            str = obj.toString();
        return str;
    }

    private String convertValueToString(long typeId, Object obj) {
        String str;
        if (typeId == Kernel.IC_DATE) {
            str = (obj != null) ? format_.format((Date)obj) : null;
        } else if (typeId == Kernel.IC_TIME)
            str = (obj != null) ? timeformat_.format((Date)obj) : null;
        else if (typeId == Kernel.IC_BLOB)
            str = (obj != null) ? ((File) obj).getAbsolutePath() : null;
        else if (typeId == Kernel.IC_FLOAT)
            str = floatFormat_.format(((Number) obj).doubleValue());
        else if (typeId == Kernel.IC_INTEGER)
            str = "" + ((Number) obj).longValue();
        else if (obj instanceof KrnObject) {
            str = "" + ((KrnObject) obj).id;
        } else
            str = obj.toString();
        return str;
    }

    private String convertBlob(long height, Object obj) {
        String str = "";
        if (obj instanceof byte[] && ((byte[])obj).length > 0) {
        	try {
        		byte[] img = null;
        		if (height > 10) {
        			img = new SystemOp(null).getScaledImage((byte[])obj, 0, (int)height, "PNG");
        		} else
        			img = (byte[])obj;
        		
				File f = Funcs.createTempFile("imgr", null, WebController.WEB_IMAGES_DIRECTORY);
				((WebFrame)frame).getSession().deleteOnExit(f);
				FileOutputStream os = new FileOutputStream(f);
				os.write(img);
				os.close();

                str = f.getAbsolutePath();
        	} catch (Exception e) {
        		log.error(e, e);
        	}
        }
        else if (obj instanceof File) {
        	try {
        		byte[] img = new byte[(int)((File)obj).length()];
        		FileInputStream fis = new FileInputStream((File)obj);
        		fis.read(img);
        		fis.close();
        		if (height > 10) {
        			img = new SystemOp(null).getScaledImage(img, 0, (int)height, "PNG");
        		}

        		FileOutputStream fos = new FileOutputStream((File)obj);
        		fos.write(img);
        		fos.close();

        		return ((File) obj).getAbsolutePath();
        	} catch (Exception e) {
        		log.error(e, e);
        	}
        }
        return str;
    }

    private String convertToString(Object value, int format,
                                   String dateFormat) {
        if (value != null && value.toString().length() > 0) {
            switch (format) {
                case 0:
                    return floatFormat_.format(((Number)value).doubleValue());
                case 1:
                    if (value instanceof Number)
                        return "" + ((Number) value).longValue();
                    return "";
                case 2:
                    return format_.format(new Date(((Number)value).longValue() * 1000L));
                case 6:
                    return Utils.convertToText(formatFull_.format(new Date(((Number)value).longValue() * 1000L)));
                case 7:
                    return Utils.convertToTextDate(formatFull_.format(new Date(((Number)value).longValue() * 1000L)));
                case 8:
                    return Utils.convertToDateKaz(formatFullKaz_,
                            new Date(((Number)value).longValue() * 1000L));
                case 9:
                    return Utils.convertToTextDateKaz(formatFullKaz_,
                            new Date(((Number)value).longValue() * 1000L));
                case 4:
                    return value.toString();
                case 5:
                    long iv = ((Number)value).longValue();
                    return (iv == 0) ? "Нет" : "Да";
                case 10:
                    return Utils.convertToDateRus(dateFormat,
                            new Date(((Number)value).longValue() * 1000L));
                case 11:
                    return Utils.convertToDateKaz(dateFormat,
                            new Date(((Number)value).longValue() * 1000L));
                case 12:
                    return Utils.convertToTextDateRus(dateFormat,
                            new Date(((Number)value).longValue() * 1000L));
                case 13:
                    return Utils.convertToTextDateKaz(dateFormat,
                            new Date(((Number)value).longValue() * 1000L));
            }
        }
        return "";
    }

    public boolean checkEnabled() {
        return true;
    }

    public void removeNullRows (Element root) {
        List elements = root.getChildren();
        for (int i = elements.size() - 1; i >=0; i--) {
            Element e = (Element) elements.get(i);
            Element parent = (Element)e.getParent();
            if (e.getName().equals("Column")) {
                String tableId = e.getAttributeValue("tableId");
                List<Element> cols = getElementsByAttribute(parent, "tableId", tableId);
                for (int row = e.getChildren().size() - 1; row >= 0; row--) {
                    boolean isNull = true;
                    for (int col = 0; col<cols.size(); col++) {
                        Element column = cols.get(col);
                        //TODO Посмотреть Ерику
                        Element val = (column.getChildren().size() <= row) ? null : (Element) column.getChildren().get(row);
                        String s = val != null ? val.getAttributeValue("str") : "0";
                        //---------------------
                        if (s == null || (s.length() > 0 && !"0".equals(s))) {
                            isNull = false;
                            break;
                        }
                    }
                    if (isNull) {
                        for (int col = 0; col<cols.size(); col++) {
                            Element column = (Element) cols.get(col);
                            column.removeContent(row);
                        }
                    }
                }
            } else if (e.getName().equals("TreeColumn") && e.getAttribute("remove") != null) {
                return;
            } else {
                removeNullRows(e);
            }
            if ((e.getName().equals("Column") || e.getName().equals("Value")) &&
                    e.getContentSize() == 0) {
                parent.removeContent(e);
            }
        }
    }


    public void sort (Element root, final Map<String, String> paths, final Map<String, Boolean> descs) {
        try {
            String[] keys = paths.keySet().toArray(new String[paths.keySet().size()]);
            Arrays.sort(keys, new Comparator<String>() {
                public int compare(String o1, String o2) {
                    Integer i1 = new Integer(o1);
                    Integer i2 = new Integer(o2);
                    return i1.compareTo(i2);
                }
            });
            for (int i = keys.length - 1; i>=0; i--) {
                String id = paths.get(String.valueOf(keys[i]));
                Boolean desc = descs.get(id);
                sort(root, id, desc != null ? desc : false);
            }
        }
        catch (Exception ex) {
            log.error(ex, ex);
        }
    }


    public void sort (Element root, String id, boolean desc) {
        List<Element> elements = getElementsById(root, id);
        for (Element e : elements) {
            if (e.getName().equals("Column")) {
                sortColumn(e, desc);
            } else if (e.getName().equals("TreeColumn")) {
                sortTreeColumn(e);
            }
        }
    }

    public void sortColumn (Element element, final boolean desc) {
        List children = element.getChildren();
        Element parent = (Element)element.getParent();
        String tableId = element.getAttributeValue("tableId");
        List<Element> elements = getElementsByAttribute(parent, "tableId", tableId);

        String d = element.getAttributeValue("dateFormat");
        final DateFormat dateFormat = (d != null) ? new ThreadLocalDateFormat(d).get() : format_.get();
        
        if (children.size() > 0) {
            Element v = (Element) children.get(0);
            if (v.getAttributeValue("str") == null && v.getAttributeValue("src") == null) {
                for (int i = 0; i < children.size(); i++) {
                    Element value = (Element) children.get(i);

                    List children2 = value.getChildren();

                    List<ArrayElement> toSort = new ArrayList<ArrayElement>();
                    for (int j = 0; j < children2.size(); j++) {
                        Element value2 = (Element) children2.get(j);
                        List<Element> values2 = new ArrayList<Element>();
                        for (Element col : elements) {
                            Element val1 = (Element) col.getChildren().get(i);

                            if (val1.getAttributeValue("str") == null && val1.getAttributeValue("src") == null) {
                                values2.add((Element)val1.getChildren().get(j));
                            }
                        }
                        toSort.add(new ArrayElement(value2, values2));
                    }
                    ArrayElement[] vals2 = new ArrayElement[toSort.size()];
                    for (int j = 0; j < toSort.size(); j++) {
                        vals2[j] = toSort.get(j);
                    }

                    Arrays.sort(vals2, new ColumnComparator(dateFormat, desc));

                    for (Element col : elements) {
                        Element val1 = (Element) col.getChildren().get(i);

                        if (val1.getAttributeValue("str") == null && val1.getAttributeValue("src") == null) {
                            if (col.getAttribute("nosort") == null) {
                                val1.removeChildren("Value");
                            }
                        }
                    }

                    for (ArrayElement val2 : vals2) {
                        List<Element> values2 = val2.getElements();

                        int j = 0;

                        for (Element col : elements) {
                            Element val1 = (Element) col.getChildren().get(i);

                            if (val1.getAttributeValue("str") == null && val1.getAttributeValue("src") == null) {
                                if (col.getAttribute("nosort") == null) {
                                    val1.addContent((Element) values2.get(j));
                                }
                                j++;
                            }
                        }
                    }
                }
            } else {
                ArrayElement[] vals = new ArrayElement[children.size()];
                for (int i=0; i<children.size(); i++) {
                    Element value = (Element) children.get(i);
                    List<Element> values = new ArrayList<Element>();
                    for (Element col : elements) {
                        values.add((Element)col.getChildren().get(i));
                    }
                    vals[i] = new ArrayElement(value, values);
                }

                
                Arrays.sort(vals, new ColumnComparator(dateFormat, desc));

                for (int j=0; j<elements.size(); j++) {
                    Element col = elements.get(j);
                    if (col.getAttribute("nosort") == null) {
                        col.removeChildren("Value");
                    }
                }

                for (ArrayElement val : vals) {
                    List<Element> values = val.getElements();

                    for (int j = 0; j < values.size(); j++) {
                        Element col = elements.get(j);
                        if (col.getAttribute("nosort") == null) {
                            col.addContent(values.get(j));
                        }
                    }
                }
            }
        }
    }
    
    class ColumnComparator implements Comparator<ArrayElement> {
        private DateFormat dateFormat;
        private boolean desc;

        public ColumnComparator(DateFormat dateFormat, boolean desc) {
            super();
            this.dateFormat = dateFormat;
            this.desc = desc;
        }

        public int compare(ArrayElement a1, ArrayElement a2) {
            Element e1 = a1.getElement();
            Element e2 = a2.getElement();

            String s1 = e1.getAttributeValue("str");
            String s2 = e2.getAttributeValue("str");
            try {
                double d1 = Double.parseDouble(s1);
                double d2 = Double.parseDouble(s2);
                if (d1 > d2)
                    return (desc ? -1 : 1);
                else if (d1 < d2)
                    return (desc ? 1 : -1);
                else
                    return 0;
            } catch (Exception ex) {
            }
            try {
                Date d1 = dateFormat.parse(s1);
                Date d2 = dateFormat.parse(s2);
                return d1.compareTo(d2) * (desc ? -1 : 1);
            } catch (Exception ex) {
            }

            return s1.compareTo(s2) * (desc ? -1 : 1);
        }
    }

    public void sortTreeColumn (Element element) {
        List<Element> children = element.getChildren("Value");

        for (int i = 0; i<children.size(); i++) {
            Element child = (Element) children.get(i);
            sortTreeColumn(child);
        }

        Element[] vals = children.toArray(new Element[children.size()]);

        Arrays.sort(vals, new Comparator<Element>() {
        	@Override
            public int compare(Element e1, Element e2) {
                String s1 = e1.getAttributeValue("str");
                String s2 = e2.getAttributeValue("str");
                try {
                    double d1 = Double.parseDouble(s1);
                    double d2 = Double.parseDouble(s2);
                    if (d1 > d2)
                        return 1;
                    else if (d1 < d2)
                        return -1;
                    else
                        return 0;
                } catch (Exception ex) {
                }

                return s1.compareTo(s2);
            }
        });

        element.removeChildren("Value");

        for (int i=0; i<vals.length; i++) {
            Element val = vals[i];
            element.addContent(val);
        }
    }

    public List<Element> getElementsById (Element root, String id) {
        List<Element> res = new ArrayList<Element>();
        List<Element> list = root.getChildren();
        for (int i = 0; i < list.size(); i++) {
            Element e = list.get(i);
            if (id.equals(e.getAttributeValue("id")))
                res.add(e);
            res.addAll(getElementsById(e, id));
        }
        return res;
    }


    public List<Element> getElementsByAttribute (Element root, String attr, String id) {
        List<Element> res = new ArrayList<Element>();
        List list = root.getChildren();
        for (Object temp : list) {
            Element e = (Element) temp;
            if (id.equals(e.getAttributeValue(attr)) || ("Value".equals(e.getName()) && e.getAttribute("str") == null))
                res.add(e);
            else {
                List ch = e.getChildren("Value");
                if (ch != null && ch.size() > 0) {
                    Element v = (Element) ch.get(0);
                    if ("Value".equals(v.getName()) && v.getAttribute("str") == null)
                        res.add(e);
                }
            }
        }
        return res;
    }

    public class ArrayElement {
        Element element;
        List<Element> elements;

        public ArrayElement(Element element, List<Element> elements) {
            this.element = element;
            this.elements = elements;
        }

        public List<Element> getElements() {
            return elements;
        }

        public Element getElement() {
            return element;
        }
    }

    protected Cache getCash() {
        if (cash == null) {
            cash = frame.getCash();
        }
        return cash;
    }
    
 // OrRefListener
    public void valueChanged(OrRefEvent e) {
        if (!selfChange) {
            OrRef ref = e.getRef();
            if (ref == visibilityRef) {
            	boolean isVisible = true;
            	Object res = visibilityRef.getValue(langId);
                if (res instanceof Boolean)
                	isVisible = ((Boolean)res).booleanValue();
                else if (res instanceof Number)
                	isVisible = ((Number) res).intValue() == 1 ? true : false;
                
                if (reportMenuItem != null)
                	reportMenuItem.setVisible(isVisible);
            }
        }
    }

	@Override
	public void clear() {
	}
	
	public void setMenuItem(WebMenuItem m) {
		this.reportMenuItem = m;
	}
}