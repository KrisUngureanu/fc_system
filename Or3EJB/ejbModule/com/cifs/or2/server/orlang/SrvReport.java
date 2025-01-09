package com.cifs.or2.server.orlang;

import com.cifs.or2.kernel.*;
import com.cifs.or2.server.Context;
import com.cifs.or2.server.Session;
import com.cifs.or2.util.MultiMap;
import kz.tamur.admin.ReportLogHelper;
import kz.tamur.comps.Constants;
import kz.tamur.comps.ReportLauncher;
import kz.tamur.rt.ReportWrapper;
import kz.tamur.rt.Utils;
import kz.tamur.util.Funcs;
import kz.tamur.util.Pair;
import kz.tamur.util.ThreadLocalDateFormat;
import kz.tamur.util.ThreadLocalNumberFormat;
import kz.tamur.util.Base64;
import kz.tamur.lang.EvalException;
import kz.tamur.lang.OrLang;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.or3.server.lang.SystemOp;
import kz.tamur.or3.util.PathElement;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.or3ee.server.kit.SrvUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Attribute;
import org.jdom.Content;
import org.jdom.Element;
import org.jdom.xpath.XPath;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.*;
import java.util.Date;
import static kz.tamur.or3ee.common.SessionIds.*;

/**
 * Created by IntelliJ IDEA.
 * User: Администратор
 * Date: 16.07.2005
 * Time: 13:11:06
 * To change this template use File | Settings | File Templates.
 */
public class SrvReport {

    private Session s;
    private Log log;
    private static final ThreadLocalDateFormat simpleFormat = new ThreadLocalDateFormat("dd.MM.yyyy");
    private static final ThreadLocalDateFormat timeFormat = new ThreadLocalDateFormat("dd.MM.yyyy HH:mm:ss");
    private static final ThreadLocalDateFormat formatFull_ = new ThreadLocalDateFormat("d MMMM yyyy", new Locale("ru", "RU"));
    private static final String formatFullKaz_ = "d MMMM yyyy";

    private static ThreadLocalNumberFormat floatFormat = new ThreadLocalNumberFormat(null, '.', (char)0, false, 6, -1);
    private boolean isSingleType;
    private Map<Integer, List<Object>> tableObjects;
    private Map<Integer, List<KrnObject>> parentObjects;
    private Map<String, String> sortAttributes = new TreeMap<String, String>();
    private Map<String, Boolean> sortAttributesDesc = new TreeMap<String, Boolean>();
    private Map<String, String> sortTreeAttributes = new TreeMap<String, String>();
    private Map<String, Set<Long>> filteredIds_ = new TreeMap<String, Set<Long>>();
    private Set<String> filterUids;
    private Map<String, Long> idByUid;
    private TreeMap<String, MultiMap> globalMap;
    private TreeMap<String, Map<Element, KrnObject>> globalNodeMap;
    private TreeMap<String, Element> globalRoots;
    private boolean showComment;
    private FilterDate[] filterDates_;

    public SrvReport(Session s) {
        this.s = s;
        this.log = LogFactory.getLog(s.getDsName() + "." + s.getUserSession().getLogUserName() + "." + (UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + getClass().getName());
        this.showComment = Funcs.getSystemProperty("showComment") != null;
    }

    public File formReport(String uid, Element root, KrnObject lang) {
    	return formReport(uid, root, lang, -1, null);
    }
    
    public File formReport(String uid, Element root, KrnObject lang, int format) {
    	return formReport(uid, root, lang, format, null);
    }
    
    public File formReport(String uid, Element root, KrnObject lang, int format, String reportType) {
		File docFile = null;
		File xmlFile = null;
    	try {
	    	KrnClass reportClass = s.getClassByName("ReportPrinter");
	    	
	    	Context ctx = s.getContext();
	        KrnObject reportObject = s.getObjectByUid(uid, ctx.trId);
	        long lid = lang.getId();
	        
	        byte[] data = s.getBlob(reportObject.id,
	                s.getAttributeByName(reportClass, "config").id,
	                0, 0, 0);
	
	        InputStream is = new ByteArrayInputStream(data);
	        SAXBuilder builder = new SAXBuilder();
	        Element config = builder.build(is).getRootElement();
	        Element type = config.getChild("editorType");
	        int editorType = Constants.MSWORD_EDITOR;
	        if (type != null) {
	            editorType = Integer.parseInt(type.getText());
	        }
	
	        Element macrosElement = config.getChild("macros");
	        String macros = "";
	        if (macrosElement != null) {
	            macros = macrosElement.getText();
	        }
	
            String templatePD = config.getChildText("templatePassword");

	        File dir = Constants.DOCS_DIRECTORY;

	        byte[] htmlBuf = null;
	        KrnAttribute htmlAttr = s.getAttributeByName(reportClass, "htmlTemplate");
	        if (htmlAttr != null) {
	        	htmlBuf = s.getBlob(reportObject.id, htmlAttr.id, 0, lid, 0);
	        }
	        if (htmlBuf != null && htmlBuf.length > 0) {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                XMLOutputter opr = new XMLOutputter();
                opr.getFormat().setEncoding("UTF-8");
                opr.output(root, os);
                os.close();
                byte[] bytes = os.toByteArray();
	            docFile = ReportLauncher.viewHtmlReportI(bytes,
	            		htmlBuf, "", null, 0, dir);
	        } else {
	            List res = XPath.selectNodes(root, ".//@src");
	
	            if (res != null && res.size() > 0) {
                	log.info("Количество картинок в отчете: " + res.size());
	                for (int i=0; i<res.size(); i++) {
	                    Attribute attr = (Attribute)res.get(i);
	                    String img = attr.getValue();
	                    if (img.length() > 0) {
	                    	StringTokenizer st = new StringTokenizer(img, "|");
	                    	String imgPth = ""; 
	                    	while (st.hasMoreTokens()) {
	                    		String imgBs = st.nextToken();
			                    File f = Funcs.createTempFile("img", ".tmp", dir);
			                    s.deleteFileOnExit(f);
			                    FileOutputStream fos = new FileOutputStream(f);
			                    fos.write(Base64.decode(imgBs));
			                    fos.close();
			                	log.info("Сохраняем картинку в файл: " + f.getAbsolutePath());
			                    imgPth += f.getAbsolutePath() + "|";
	                    	}
		                    attr.setValue(imgPth.substring(0, imgPth.length() - 1));
	                    }
	                }
	            } else
	            	log.info("В отчете нет картинок !");

	            OutputStream os = new ByteArrayOutputStream();
                XMLOutputter opr = new XMLOutputter();
                opr.getFormat().setEncoding("UTF-8");
                opr.output(root, os);
                os.close();
                byte[] bytes = ((ByteArrayOutputStream)os).toByteArray();

                xmlFile = Funcs.createTempFile("xxx", ".xml", dir);
            	log.info("Сохраняем xml в файл: " + xmlFile.getAbsolutePath());
	            os = new FileOutputStream(xmlFile);
	            os.write(bytes);
	            os.close();
	
	            String suffix = (editorType == Constants.MSWORD_EDITOR) ? ".doc" : ".xls";
	            if (!"jacob".equals(System.getProperty("reportType"))) suffix += "x";

	            docFile = Funcs.createTempFile("xxx", suffix, dir);

            	log.info("Сохраняем doc в файл: " + docFile.getAbsolutePath());
	            byte[] buf = s.getBlob(reportObject.id, s.getAttributeByName(reportClass, "template").id, 0,
	            		lid, 0);
	            os = new FileOutputStream(docFile);
	            os.write(buf);
	            os.close();
	
	            ReportLauncher.viewReportI(docFile.getAbsolutePath(),
	                    xmlFile.getAbsolutePath(), editorType, "", macros, templatePD,
	                    null, 0, format, reportType);
	            
	            if (format == ReportWrapper.XL_2007) {
	            	String name = docFile.getAbsolutePath();
	            	docFile.delete();
	            	docFile = new File(name + "x");
		            s.deleteFileOnExit(docFile);
	            } else
		            s.deleteFileOnExit(docFile);
            }
        } catch (Exception e) {
            log.error(e, e);
        } finally {
        	if (xmlFile != null)
        		xmlFile.delete();
        }
        return docFile;
    }

    public Element processReport(Element report, KrnObject lang) {
        clearSortAttributes();
        Element root = new Element("Root");
        tableObjects = new TreeMap<Integer, List<Object>>();
        parentObjects = new TreeMap<Integer, List<KrnObject>>();
        globalMap = new TreeMap<String, MultiMap>();
        globalNodeMap = new TreeMap<String, Map<Element, KrnObject>>();
        globalRoots = new TreeMap<String, Element>();
        filterDates_ = new FilterDate[0];
        long lid = lang.getKrnObject().id;

        try {
            Context ctx = s.getContext();
            String uid = report.getChild("UID").getText();

            String fuid = null;
            Element oFilter = report.getChild("ObjectFilter");
            if (oFilter != null) {
                fuid = oFilter.getChild("UID").getText();
                List<Element> params = oFilter.getChildren("Param");
                for (Element param : params) {
                    String paramName = param.getChild("Name").getText();
                    String paramValue = param.getChild("Value").getText();
                    String paramPath = param.getChild("Path").getText();
                    KrnAttribute[] attrs = s.getAttributesForPath(paramPath);
                    Object val = null;
                    if (attrs != null && attrs.length > 0 &&
                        paramValue != null && paramValue.length() > 0) {
                        int typeClassId = (int) attrs[attrs.length - 1].typeClassId;
                        switch (typeClassId) {
                            case CID_STRING:
                            case CID_MEMO: {
                                val = paramValue;
                                break;
                            }
                            case CID_INTEGER:
                            case CID_BOOL: {
                                val = new Integer(paramValue);
                                break;
                            }
                            case CID_FLOAT: {
                                val = new Double(paramValue);
                                break;
                            }
                            case CID_DATE: {
                                Date d = ThreadLocalDateFormat.dd_MM_yyyy.parse(paramValue);
                                val = Funcs.convertDate(d);
                                break;
                            }
                            case CID_TIME: {
                                Date d = ThreadLocalDateFormat.dd_MM_yyyy_HH_mm_ss.parse(paramValue);
                                val = Funcs.convertTime(d);
                                break;
                            }
                            case CID_BLOB: {
                                break;
                            }
                            default: {
                                val = s.getObjectByUid(paramValue, ctx.trId);
                            }
                        }
                    }

                    s.setFilterParam(fuid, paramName, val);
                }
            }

            List children = report.getChildren("FilterDate");
            Map<Integer, com.cifs.or2.kernel.Date> filterDates = new TreeMap<Integer, com.cifs.or2.kernel.Date>();
            if (children != null) {
                filterDates_ = new FilterDate[children.size()];
                for (int i = 0; i < children.size(); i++) {
                    Element filterDate = (Element)children.get(i);
                    String type = filterDate.getChild("Type").getText();
                    String date = filterDate.getChild("Date").getText();
                    filterDates.put(new Integer(type),
                                    Funcs.convertDate(simpleFormat.parse(date)));
                    filterDates_[i] = new FilterDate(0, new Integer(type), Funcs.convertDate(simpleFormat.parse(date)));
                }
            }
            getOrLang().getDateOp().setFilterDates(filterDates);

            List<Object> objs = Collections.emptyList();
            if (fuid != null) {
                try {
                	int[] ih = {0};
                    long[] objIds = s.filter(new String[] {fuid}, filterDates_, ih,null,null, (int)ctx.trId);
                    if (objIds != null && objIds.length > 0) {
                        KrnObject krnObjs[] = s.getObjectsById(objIds,-1);
                        objs = new ArrayList<Object>(krnObjs.length);
                        objs.addAll(Arrays.asList(krnObjs));
                    }
                } catch (Exception e) {
                    log.error(e, e);
                }
            }

            children = report.getChildren("Filter");
            if (children != null) {
                for (int i = 0; i < children.size(); i++) {
                    Element filter = (Element)children.get(i);
                    fuid = filter.getChild("UID").getText();
                    List params = filter.getChildren("Param");
                    for (int j=0; j<params.size(); j++) {
                        Element param = (Element)params.get(j);
                        String paramName = param.getChild("Name").getText();
                        String paramValue = param.getChild("Value").getText();
                        String paramPath = param.getChild("Path").getText();
                        KrnAttribute[] attrs = s.getAttributesForPath(paramPath);
                        Object val = null;
                        if (attrs != null && attrs.length > 0 &&
                                paramValue != null && paramValue.length() > 0) {
                            int typeClassId = (int)attrs[attrs.length - 1].typeClassId;
                            switch (typeClassId) {
                                case CID_STRING :
                                case CID_MEMO :
                                    {
                                        val = paramValue;
                                        break;
                                    }
                                case CID_INTEGER :
                                case CID_BOOL :
                                    {
                                        val = new Integer(paramValue);
                                        break;
                                    }
                                case CID_FLOAT :
                                    {
                                        val = new Double(paramValue);
                                        break;
                                    }
                                case CID_DATE :
                                    {
                                        Date d = ThreadLocalDateFormat.dd_MM_yyyy.parse(paramValue);
                                        val = Funcs.convertDate(d);
                                        break;
                                    }
                                case CID_TIME :
                                    {
                                        Date d = ThreadLocalDateFormat.dd_MM_yyyy_HH_mm_ss.parse(paramValue);
                                        val = Funcs.convertTime(d);
                                        break;
                                    }
                                case CID_BLOB :
                                    {
                                        break;
                                    }
                                default :
                                    {
                                        val = s.getObjectByUid(paramValue, ctx.trId);
                                    }
                            }
                        }
                        StringTokenizer st = new StringTokenizer(fuid, ",");
                        while (st.hasMoreTokens()) {
                            s.setFilterParam(st.nextToken(), paramName, val);
                        }
                    }
                }
            }
            KrnObject reportObject = s.getObjectByUid(uid, ctx.trId);
            byte[] buf = s.getBlob(reportObject.id,
                    s.getAttributeByName(s.getClassByName("ReportPrinter"), "data").id,
                    0, lid, 0);

            byte[] data = s.getBlob(reportObject.id,
                    s.getAttributeByName(s.getClassByName("ReportPrinter"), "config").id,
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

            if (buf != null && buf.length > 0) {
                if (!createRefsEx(buf, root, objs))
                    return null;
            }

            removeNullRows(root);
            if (getSortAttributes().size() > 0)
                sort(root, getSortAttributes(), getSortAttributesDesc());
            if (getSortTreeAttributes().size() > 0)
                sortTree(root, getSortTreeAttributes());

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

        } catch (Exception ex) {
            log.error(ex, ex);
        }
        filteredIds_.clear();
        return root;
    }

    public Element processReport(Element report, KrnObject obj, KrnObject lang) {
        clearSortAttributes();
        Element root = new Element("Root");
        tableObjects = new TreeMap<Integer, List<Object>>();
        parentObjects = new TreeMap<Integer, List<KrnObject>>();
        globalMap = new TreeMap<String, MultiMap>();
        globalNodeMap = new TreeMap<String, Map<Element, KrnObject>>();
        globalRoots = new TreeMap<String, Element>();
        List<Object> objs = new ArrayList<Object>();
        objs.add(obj.getKrnObject());
        long lid = lang.getKrnObject().id;
        filterDates_ = new FilterDate[0];

        try {
            List children = report.getChildren("FilterDate");
            Map<Integer, com.cifs.or2.kernel.Date> filterDates = new TreeMap<Integer, com.cifs.or2.kernel.Date>();
            if (children != null) {
                filterDates_ = new FilterDate[children.size()];
                for (int i = 0; i < children.size(); i++) {
                    Element filterDate = (Element)children.get(i);
                    String type = filterDate.getChild("Type").getText();
                    String date = filterDate.getChild("Date").getText();
                    filterDates.put(new Integer(type),
                                    Funcs.convertDate(simpleFormat.parse(date)));
                    filterDates_[i] = new FilterDate(0, new Integer(type), Funcs.convertDate(simpleFormat.parse(date)));
                }
            }
            getOrLang().getDateOp().setFilterDates(filterDates);

            String uid = report.getChild("UID").getText();

            KrnObject reportObject = s.getObjectByUid(uid, s.getContext().trId);
            byte[] buf = s.getBlob(reportObject.id,
                    s.getAttributeByName(s.getClassByName("ReportPrinter"), "data").id,
                    0, lid, 0);

            byte[] data = s.getBlob(reportObject.id,
                    s.getAttributeByName(s.getClassByName("ReportPrinter"), "config").id,
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

            if (buf != null && buf.length > 0) {
                if (!createRefsEx(buf, root, objs))
                    return null;
            }

            //removeNullRows(root);
            if (getSortAttributes().size() > 0)
                sort(root, getSortAttributes(), getSortAttributesDesc());
            if (getSortTreeAttributes().size() > 0)
                sortTree(root, getSortTreeAttributes());

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

        } catch (Exception ex) {
            log.error(ex, ex);
        }
        filteredIds_.clear();
        return root;
    }

    public Element processReport(Element report, List<KrnObject> srvObjs, KrnObject lang) {
        clearSortAttributes();
        Element root = new Element("Root");
        tableObjects = new TreeMap<Integer, List<Object>>();
        parentObjects = new TreeMap<Integer, List<KrnObject>>();
        globalMap = new TreeMap<String, MultiMap>();
        globalNodeMap = new TreeMap<String, Map<Element, KrnObject>>();
        globalRoots = new TreeMap<String, Element>();
        filterDates_ = new FilterDate[0];

        List<Object> objs = new ArrayList<Object>();
        for (int i = 0; i < srvObjs.size(); i++) {
            objs.add(srvObjs.get(i).getKrnObject());
        }
        long lid = lang.getKrnObject().id;

        try {
            List children = report.getChildren("FilterDate");
            Map<Integer, com.cifs.or2.kernel.Date> filterDates = new TreeMap<Integer, com.cifs.or2.kernel.Date>();
            if (children != null) {
                filterDates_ = new FilterDate[children.size()];
                for (int i = 0; i < children.size(); i++) {
                    Element filterDate = (Element)children.get(i);
                    String type = filterDate.getChild("Type").getText();
                    String date = filterDate.getChild("Date").getText();
                    filterDates.put(new Integer(type),
                                    Funcs.convertDate(simpleFormat.parse(date)));
                    filterDates_[i] = new FilterDate(0, new Integer(type), Funcs.convertDate(simpleFormat.parse(date)));
                }
            }
            getOrLang().getDateOp().setFilterDates(filterDates);

            String uid = report.getChild("UID").getText();

            KrnObject reportObject = s.getObjectByUid(uid, s.getContext().trId);
            byte[] buf = s.getBlob(reportObject.id,
                    s.getAttributeByName(s.getClassByName("ReportPrinter"), "data").id,
                    0, lid, 0);

            byte[] data = s.getBlob(reportObject.id,
                    s.getAttributeByName(s.getClassByName("ReportPrinter"), "config").id,
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

            if (buf != null && buf.length > 0) {
                if (!createRefsEx(buf, root, objs))
                    return null;
            }

            removeNullRows(root);
            if (getSortAttributes().size() > 0)
                sort(root, getSortAttributes(), getSortAttributesDesc());
            if (getSortTreeAttributes().size() > 0)
                sortTree(root, getSortTreeAttributes());

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

        } catch (Exception ex) {
            log.error(ex, ex);
        }
        filteredIds_.clear();
        return root;
    }
    
    public Element prepareReport(long reportId, KrnObject lang, KrnObject[] srvObjs, FilterDate[] fds) {
        clearSortAttributes();
        Element root = new Element("Root");
        tableObjects = new TreeMap<Integer, List<Object>>();
        parentObjects = new TreeMap<Integer, List<KrnObject>>();
        globalMap = new TreeMap<String, MultiMap>();
        globalNodeMap = new TreeMap<String, Map<Element, KrnObject>>();
        globalRoots = new TreeMap<String, Element>();
        filterDates_ = new FilterDate[0];

        List<Object> objs = new ArrayList<Object>();
        if (srvObjs != null) {
	        for (KrnObject srvObj : srvObjs) {
	            objs.add(srvObj.getKrnObject());
	        }
        }
        long lid = lang.getKrnObject().id;

        try {
            Map<Integer, com.cifs.or2.kernel.Date> filterDates = new TreeMap<Integer, com.cifs.or2.kernel.Date>();
            if (fds != null) {
                filterDates_ = fds;
                for (FilterDate fd : fds) {
                    filterDates.put(fd.type, fd.date);
                }
            }
            getOrLang().getDateOp().setFilterDates(filterDates);

            KrnObject reportObject = s.getObjectById(reportId, 0);
            byte[] buf = s.getBlob(reportObject.id,
                    s.getAttributeByName(s.getClassByName("ReportPrinter"), "data").id,
                    0, lid, 0);

            byte[] data = s.getBlob(reportObject.id,
                    s.getAttributeByName(s.getClassByName("ReportPrinter"), "config").id,
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

            if (buf != null && buf.length > 0) {
                if (!createRefsEx(buf, root, objs))
                    return null;
            }

            removeNullRows(root);
            if (getSortAttributes().size() > 0)
                sort(root, getSortAttributes(), getSortAttributesDesc());
            if (getSortTreeAttributes().size() > 0)
                sortTree(root, getSortTreeAttributes());

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

        } catch (Exception ex) {
            log.error(ex, ex);
        }
        filteredIds_.clear();
        return root;
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
        this.sortTreeAttributes.clear();
    }

    public Map<String, String> getSortTreeAttributes() {
        return sortTreeAttributes;
    }

    public void setSortTreeAttributes(String index, String id) {
        this.sortTreeAttributes.put(index, id);
    }

    private boolean createRefsEx(byte[] xml, Element rootElement, List<Object> objs) {
        try {
            InputStream is = new ByteArrayInputStream(xml);
            SAXBuilder builder = new SAXBuilder();
            Element doc = builder.build(is).getRootElement();

            // log.info(new String(xml, 0, xml.length));

            ArrayList<ReportNode> rns = new ArrayList<ReportNode>();
            ArrayList<FilterNode> fns = new ArrayList<FilterNode>();
            filterUids = new TreeSet<String>();
            idByUid = new HashMap<String, Long>();

            try {
                processNode(null, doc, rootElement, rns, fns);

                KrnObject[] fobjs = s.getObjectsByUid((String[]) filterUids.toArray(new String[filterUids.size()]), s.getContext().trId);
                for (int i = 0; i < fobjs.length; ++i)
                    idByUid.put(fobjs[i].uid, new Long(fobjs[i].id));

                if (fns.size() > 0) {
                    for (Iterator<FilterNode> fnIt = fns.iterator(); fnIt.hasNext();)
                        fnIt.next().execute(rootElement, null);
                }

                for (int i = 0; i < rns.size(); ++i) {
                    ReportNode rn = rns.get(i);
                    if (rn instanceof ConsValueReportNode) {
                        ConsValueReportNode cvrn = (ConsValueReportNode) rn;
                        if (cvrn.fuid != null) {
                            Set<Long> res = getFilteredIds(cvrn.fuid);
                            cvrn.setFilteredIds(res);
                        }
                    } else if (rn instanceof ConsColumnExReportNode) {
                        ConsColumnExReportNode cvrn = (ConsColumnExReportNode) rn;
                        if (cvrn.fuid != null) {
                            Set<Long> res = getFilteredIds(cvrn.fuid);

                            cvrn.setFilteredIds(res);
                        }
                    } else if (rn instanceof ColumnReportNode) {
                        ColumnReportNode crn = (ColumnReportNode) rn;
                        if (crn.fuid != null) {
                            try {
                                Set<Long> res = getFilteredIds(crn.fuid);

                                if (crn.filterInnerTable) {
                                    crn.setFilteredIds2(res);
                                } else {
                                    for (int m = 0; m < rns.size(); ++m) {
                                        ReportNode colrn = (ReportNode) rns.get(m);
                                        if (colrn instanceof ColumnReportNode) {
                                            if (((ColumnReportNode)colrn).tablePath.equals(crn.tablePath) &&
                                                    ((ColumnReportNode)colrn).tableId == crn.tableId) {
                                                ((ColumnReportNode)colrn).setFilteredIds(res);
//                                                ((ColumnReportNode)colrn).filterInnerTable = rn.filterInnerTable;
                                            }
                                        }
                                    }
                                }

                            } catch (Exception e) {
                                log.error(e, e);
                            }
                        }
                    }
                    if (rn instanceof TreeReportNode) {
                        TreeReportNode trn = (TreeReportNode) rn;
                        if (trn.tfuid != null && trn.tfuid.length() > 0) {
                            try {
                                Set<Long> res = getFilteredIds(trn.tfuid);

                                trn.setFilteredTreeIds(res);

                            } catch (Exception e) {
                                log.error(e, e);
                            }
                        }
                    }
                }

                int size = objs.size();
                for (int i = 0; i<rns.size(); i++) {
                    ReportNode rn = (ReportNode) rns.get(i);
                    if (rn.parent == null) {
                        if (objs != null && size > 0) {
                            if (rn instanceof TreeReportNode) {

                            }
                            else if (rn instanceof ColumnReportNode) {
                            	List<Object> tObjs = tableObjects.get(
                                        new Integer(((ColumnReportNode)rn).tableId));
                                if (tObjs != null) {
                                    rn.print(tObjs);
                                    continue;
                                }
                            }
                            if (size == 1 || isSingleType) {
                                rn.print((KrnObject)objs.get(size - 1));
                            } else {
                                rn.print(objs);
                            }
                        } else {
                            try {
                                if (rn.calculated)
                                    rn.print((KrnObject) null);
                            } catch (Exception ex) {
                                log.error(ex, ex);
                            }
                        }
                    }
                }
            } catch (KrnException ex) {
                log.error(ex, ex);
            }
        } catch (Exception ex) {
            log.error(ex, ex);
        }
        return true;
    }


    private void processNode(ReportNode parent, Element node,
                             Element parentNode, Collection<ReportNode> rns, Collection<FilterNode> fns)
            throws KrnException {
        ReportNode newParent = parent;

        String name = node.getName();//getNodeName();
        if (name.equals("Field")) {
            try {
                ReportNode rn;
                Attribute tempNode = node.getAttribute("sysPath");
                if (tempNode != null)
                    rn = new SystemReportNode(node, parentNode);
                else
                    rn = new ReportNode(parent, node, parentNode);
                rns.add(rn);
            } catch (Exception ex) {
                log.error(ex, ex);
            }
        } else if (name.equals("Column"))
            rns.add(new ColumnReportNode(parent, node, parentNode));
        else if (name.equals("ConsColumnEx"))
            rns.add(new ConsColumnExReportNode(parent, node, parentNode));
        else if (name.equals("TreeColumn"))
            rns.add(newParent = new TreeReportNode(parent, node, parentNode));
        else if (name.equals("ConsColumn"))
            rns.add(newParent = new ConsReportNode(parent, node, parentNode));
        else if (name.equals("ConsValue"))
            rns.add(new ConsValueReportNode(parent, node, parentNode));
        else if (name.equals("Filter"))
            fns.add(processFilterField(node));

        // пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅ
        for (int i = 0; i < node.getChildren().size(); ++i) {
            Element child = (Element)node.getChildren().get(i);
            processNode(newParent, child, parentNode, rns, fns);
        }
    }

    private FilterNode processFilterField(Element n) {
        String fuid1 = n.getAttribute("filter1").getValue();
        String fuid2 = n.getAttribute("filter2").getValue();
        Attribute node = n.getAttribute("attr");

        if (!fuid1.equals("0"))
            filterUids.add(fuid1);
        if (!fuid2.equals("0"))
            filterUids.add(fuid2);

        String attr = (node != null) ? node.getValue() : "";
        Element e = n.getChild("expr");
        String expr = (e != null) ? e.getText() : null;

        node = n.getAttribute("id");
        if (node != null) {
            int id =
                    Integer.parseInt(node.getValue());
            return new FilterNode(id, fuid1, fuid2, attr, expr);
        } else {
            node = n.getAttribute("cell");
            String cell = node.getValue();
            node = n.getAttribute("sheet");
            String sheet = node.getValue();
            return new FilterNode(-1, fuid1, fuid2, attr, expr, cell, sheet);
        }
    }

    private Set<Long> getFilteredIds(String fuid)
            throws KrnException {
        Set<Long> res = filteredIds_.get(fuid);
        if (res == null) {
            if (Constants.IS_DEBUG)
                log.info("Executing: " + fuid);
            long fid = idByUid.get(fuid).longValue();
            int[] ih = {0};
            long[] ids = s.getFilteredObjectIds(new long[]{fid}, filterDates_, ih,null,null, s.getContext().trId);
            res = new TreeSet<Long>();
            for (int i = 0; i < ids.length; i++)
                res.add(new Long(ids[i]));
            filteredIds_.put(fuid, res);
        }
        return res;
    }

    class FilterNode {
        int id;
        String fuid1;
        String fuid2;
        String attr;
        String expr;
        ASTStart astExpr;
        String addr;
        String sheet;
        private String list;

        public FilterNode(int id, String fuid1, String fuid2, String attr, String expr) {
            this.id = id;
            this.fuid1 = fuid1;
            this.fuid2 = fuid2;
            this.attr = attr;
            if (expr != null) {
                expr = expr.replaceAll("&#47;", "/");
                expr = expr.replaceAll("&#92;", "\\");
                expr = expr.replaceAll("&#34;", "\"");
                this.expr = expr;
                this.astExpr = OrLang.createStaticTemplate(expr, log);
            }
        }

        public FilterNode(int id, String fuid1, String fuid2, String attr, String expr,
                          String addr, String sheet) {
            this(id, fuid1, fuid2, attr, expr);
            this.addr = addr;
            this.sheet = sheet;
        }

        public void execute(Element parentElement,
                            PrintWriter dout) throws KrnException {
            Set<Long> ids = null, ids1 = new TreeSet<Long>(), ids2 = new TreeSet<Long>();

            if (expr != null) {
                Map<String, Object> vars = new HashMap<String, Object>();
                try {
                    if (expr.indexOf(fuid1) > 0)
                        filteredIds_.remove(fuid1);
                    if (expr.indexOf(fuid2) > 0)
                        filteredIds_.remove(fuid2);
//                    getOrLang().evaluate(astExpr, vars, null, false, new Stack<String>());
                } catch (Exception e) {
                    log.error(e, e);  //To change body of catch statement use File | Settings | File Templates.
                }
            }
            if (!fuid1.equals("0")) {
                ids1 = getFilteredIds(fuid1);
                ids = new TreeSet<Long>(ids1);
            }

            if (!fuid2.equals("0")) {
                ids2 = getFilteredIds(fuid2);
                if (!fuid1.equals("0"))
                    ids.retainAll(ids2);
                else
                    ids = new TreeSet<Long>(ids2);
            }

            if (dout != null) {
                long[] oids = Funcs.makeLongArray(ids1);
                KrnAttribute attr1 = s.getAttributeById(3696);
                ObjectValue[] ovs = s.getObjectValues(oids, attr1.id, new long[0], 0);
                for (int i = 0; i < ovs.length; ++i)
                    oids[i] = (ovs[i].value.id);

                attr1 = s.getAttributeById(3662);
                StringValue[] svs = s.getStringValues(oids, attr1.id, 122, true, 0);
                dout.println("Filter: " + fuid1);
                for (int i = 0; i < svs.length; ++i)
                    dout.println(svs[i].value);
                dout.println();

                oids = Funcs.makeLongArray(ids2);
                attr1 = s.getAttributeById(3696);
                ovs = s.getObjectValues(oids, attr1.id, new long[0], 0);
                for (int i = 0; i < ovs.length; ++i)
                    oids[i] = (ovs[i].value.id);

                attr1 = s.getAttributeById(3662);
                svs = s.getStringValues(oids, attr1.id, 122, true, 0);
                dout.println("Filter: " + fuid2);
                for (int i = 0; i < svs.length; ++i)
                    dout.println(svs[i].value);
                dout.println();

                oids = Funcs.makeLongArray(ids);
                attr1 = s.getAttributeById(3696);
                ovs = s.getObjectValues(oids, attr1.id, new long[0], 0);
                for (int i = 0; i < ovs.length; ++i)
                    oids[i] = (ovs[i].value.id);

                attr1 = s.getAttributeById(3662);
                svs = s.getStringValues(oids, attr1.id, 122, true, 0);
                dout.println("Interception: ");
                for (int i = 0; i < svs.length; ++i)
                    dout.println(svs[i].value);
                dout.println();
            }

            if (showComment) {
                long[] oids = Funcs.makeLongArray(ids);
                if (oids != null && oids.length > 0) {
                    KrnObject obj = s.getObjectsById(new long[] {oids[0]},-1)[0];
                    KrnClass cls = s.getClassById(obj.classId);
                    if (cls.name.equals("Персонал")) {
                        KrnAttribute attr1 = s.getAttributeByName(cls, "текущ  состояние -зап табл персон данных-");
                        KrnClass cls1 = s.getClassById(attr1.typeClassId);
                        KrnAttribute attr2 = s.getAttributeByName(cls1, "идентиф -фамилия с инициалами-");

                        ObjectValue[] ovs = s.getObjectValues(oids, attr1.id, new long[0], 0);
                        for (int i = 0; i < ovs.length; ++i)
                            oids[i] = (ovs[i].value.id);

                        StringValue[] svs = s.getStringValues(oids, attr2.id, 122, false, 0);

                        if (svs != null && svs.length > 0) {
                            StringBuffer list = new StringBuffer();
                            list.append(svs[0].value);
                            for (int i = 1; i < svs.length; ++i)
                                list.append("\r\n" + svs[i].value);

                            this.list = list.toString();
                        }
                    } else if (cls.name.equals("фц::осн::Персона")) {
                        KrnAttribute attr1 = s.getAttributeByName(cls, "ад_зап_перс_данных");
                        KrnClass cls1 = s.getClassById(attr1.typeClassId);
                        KrnAttribute attr2 = s.getAttributeByName(cls1, "фамилия");
                        KrnAttribute attr3 = s.getAttributeByName(cls1, "имя");
                        KrnAttribute attr4 = s.getAttributeByName(cls1, "отчество");

                        ObjectValue[] ovs = s.getObjectValues(oids, attr1.id, new long[0], 0);
                        for (int i = 0; i < ovs.length; ++i)
                            oids[i] = (ovs[i].value.id);

                        StringValue[] svs1 = s.getStringValues(oids, attr2.id, 0, false, 0);
                        StringValue[] svs2 = s.getStringValues(oids, attr3.id, 0, false, 0);
                        StringValue[] svs3 = s.getStringValues(oids, attr4.id, 0, false, 0);

                        int k = 0;
                        if (svs1 != null && svs1.length > 0) {
                            int maxRows = 250;
                            int curRow = 0;
                            int cols = svs1.length / maxRows + 1;

                            StringBuffer list = new StringBuffer();
                            list.append(svs1[0].value);
                            list.append(" " + svs2[0].value);
                            if (svs3.length > k && svs3[k].objectId == svs1[0].objectId) {
                                list.append(" " + svs3[k++].value);
                            }
                            for (int i = 1; i < svs1.length; ++i) {
                            	if (++curRow >= cols) {
                            		list.append("\r\n");
                            		curRow = 0;
                            	} 
                            	else
                            		list.append(", ");

                            	list.append(svs1[i].value);
                                list.append(" " + svs2[i].value);
                                if (svs3.length > k && svs3[k].objectId == svs1[i].objectId) {
                                    list.append(" " + svs3[k++].value);
                                }
                            }

                            this.list = list.toString();
                        }
                    } else if (cls.name.equals("фц::осн::Иск")) {
                        KrnAttribute attr0 = s.getAttributeByName(cls, "персона");
                        KrnClass cls0 = s.getClassById(attr0.typeClassId);
                        KrnAttribute attr1 = s.getAttributeByName(cls0, "ад_зап_перс_данных");
                        KrnClass cls1 = s.getClassById(attr1.typeClassId);
                        KrnAttribute attr2 = s.getAttributeByName(cls1, "фамилия");
                        KrnAttribute attr3 = s.getAttributeByName(cls1, "имя");
                        KrnAttribute attr4 = s.getAttributeByName(cls1, "отчество");

                        ObjectValue[] ovs = s.getObjectValues(oids, attr0.id, new long[0], 0);
                        for (int i = 0; i < ovs.length; ++i)
                            oids[i] = (ovs[i].value.id);

                        ovs = s.getObjectValues(oids, attr1.id, new long[0], 0);
                        for (int i = 0; i < ovs.length; ++i)
                            oids[i] = (ovs[i].value.id);

                        StringValue[] svs1 = s.getStringValues(oids, attr2.id, 0, false, 0);
                        StringValue[] svs2 = s.getStringValues(oids, attr3.id, 0, false, 0);
                        StringValue[] svs3 = s.getStringValues(oids, attr4.id, 0, false, 0);

                        int k = 0;
                        if (svs1 != null && svs1.length > 0) {
                            int maxRows = 250;
                            int curRow = 0;
                            int cols = svs1.length / maxRows + 1;

                            StringBuffer list = new StringBuffer();
                            list.append(svs1[0].value);
                            list.append(" " + svs2[0].value);
                            if (svs3.length > k && svs3[k].objectId == svs1[0].objectId) {
                                list.append(" " + svs3[k++].value);
                            }
                            for (int i = 1; i < svs1.length; ++i) {
                            	if (++curRow >= cols) {
                            		list.append("\r\n");
                            		curRow = 0;
                            	} 
                            	else
                            		list.append(", ");

                            	list.append(svs1[i].value);
                                list.append(" " + svs2[i].value);
                                if (svs3.length > k && svs3[k].objectId == svs1[i].objectId) {
                                    list.append(" " + svs3[k++].value);
                                }
                            }

                            this.list = list.toString();
                        }
                    } else if (cls.name.equals("фц::осн::Претензия")) {
                        KrnAttribute attr0 = s.getAttributeByName(cls, "персона");
                        KrnClass cls0 = s.getClassById(attr0.typeClassId);
                        KrnAttribute attr1 = s.getAttributeByName(cls0, "ад_зап_перс_данных");
                        KrnClass cls1 = s.getClassById(attr1.typeClassId);
                        KrnAttribute attr2 = s.getAttributeByName(cls1, "фамилия");
                        KrnAttribute attr3 = s.getAttributeByName(cls1, "имя");
                        KrnAttribute attr4 = s.getAttributeByName(cls1, "отчество");

                        ObjectValue[] ovs = s.getObjectValues(oids, attr0.id, new long[0], 0);
                        for (int i = 0; i < ovs.length; ++i)
                            oids[i] = (ovs[i].value.id);

                        ovs = s.getObjectValues(oids, attr1.id, new long[0], 0);
                        for (int i = 0; i < ovs.length; ++i)
                            oids[i] = (ovs[i].value.id);

                        StringValue[] svs1 = s.getStringValues(oids, attr2.id, 0, false, 0);
                        StringValue[] svs2 = s.getStringValues(oids, attr3.id, 0, false, 0);
                        StringValue[] svs3 = s.getStringValues(oids, attr4.id, 0, false, 0);

                        int k = 0;
                        if (svs1 != null && svs1.length > 0) {
                            int maxRows = 250;
                            int curRow = 0;
                            int cols = svs1.length / maxRows + 1;

                            StringBuffer list = new StringBuffer();
                            list.append(svs1[0].value);
                            list.append(" " + svs2[0].value);
                            if (svs3.length > k && svs3[k].objectId == svs1[0].objectId) {
                                list.append(" " + svs3[k++].value);
                            }
                            for (int i = 1; i < svs1.length; ++i) {
                            	if (++curRow >= cols) {
                            		list.append("\r\n");
                            		curRow = 0;
                            	} 
                            	else
                            		list.append(", ");

                            	list.append(svs1[i].value);
                                list.append(" " + svs2[i].value);
                                if (svs3.length > k && svs3[k].objectId == svs1[i].objectId) {
                                    list.append(" " + svs3[k++].value);
                                }
                            }

                            this.list = list.toString();
                        }
                    } else if (cls.name.equals("фц::осн::Гарантия")) {
                        KrnAttribute attr2 = s.getAttributeByName(cls, "гарантия_номер");

                        StringValue[] svs = s.getStringValues(oids, attr2.id, 0, false, 0);

                        if (svs != null && svs.length > 0) {
                            int maxRows = 250;
                            int curRow = 0;
                            int cols = svs.length / maxRows + 1;

                            StringBuffer list = new StringBuffer();
                            list.append(svs[0].value);
                            for (int i = 1; i < svs.length; ++i) {
                            	if (++curRow >= cols) {
                            		list.append("\r\n");
                            		curRow = 0;
                            	} 
                            	else
                            		list.append(", ");

                            	list.append(svs[i].value);
                            }

                            this.list = list.toString();
                        }
                    }
                }

            }

            if (astExpr != null) {
                Map<String, Object> vars = new HashMap<String, Object>();
                long[] objIds = Funcs.makeLongArray(ids);
            	KrnObject[] objs = s.getObjectsById(objIds, 0);
            	
            	double resObj = 0;
            	for (KrnObject obj : objs) {
            		vars.put("OBJ", obj);
                    try {
                    	getOrLang().evaluate(astExpr, vars, null, false, new Stack<String>(), null);
	                } catch (EvalException e) {
	                	if (id > -1)
	                		log.error("Ошибка в формуле отчете! Filter id=" + id);
	                	else
	                		log.error("Ошибка в формуле отчете! Filter cell=" + sheet + "!" + addr);
	                    log.error(e, e);
                    } catch (Exception e) {
                        log.error(e, e);
                    }
                    Object res = vars.get("RETURN");
                    if (res instanceof Number) 
                    	resObj += ((Number)res).doubleValue();
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
            } else if (attr.length() == 0) {
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

                StringTokenizer st = new StringTokenizer(attr, ".");
                double res = 0.0;
                Object resObj = "";

                if (st.hasMoreTokens()) {
                    String className = st.nextToken();
                    KrnClass cls = s.getClassByName(className);

                    while (st.hasMoreTokens()) {
                        String attrName = st.nextToken();
                        KrnAttribute at = s.getAttributeByName(cls, attrName);
                        cls = s.getClassById(at.typeClassId);
                        if (cls.name.equals("String") || cls.name.equals("string")
                                || cls.name.equals("Memo") || cls.name.equals("memo")) {
                            int lid = (cls.name.equals("String") || cls.name.equals("Memo")) ?
                                    102 : 0;
                            boolean isMemo = (cls.name.equals("memo") || cls.name.equals("Memo")) ?
                                    true : false;

                            StringValue[] svs = s.getStringValues(objIds, at.id, lid, isMemo, 0);
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
                            LongValue[] lvs = s.getLongValues(objIds, at.id, 0);
                            if (lvs != null) {
                                for (int k = 0; k < lvs.length; k++)
                                    res += lvs[k].value;
                            }
                            resObj = new Integer((int) res);
                        } else if (cls.name.equals("float")) {
                            FloatValue[] fvs = s.getFloatValues(objIds, at.id, 0);
                            if (fvs != null) {
                                for (int k = 0; k < fvs.length; k++)
                                    res += fvs[k].value;
                            }
                            resObj = new Double(res);
                        } else {
                            ObjectValue[] ovs = s.getObjectValues(objIds, at.id, new long[0], 0);
                            if (ovs != null) {
                                objIds = new long[ovs.length];
                                List<Long> map = new ArrayList<Long>();
                                for (int k = 0; k < ovs.length; k++) {
                                    objIds[k] = ovs[k].value.id;
                                    if (!map.contains(new Long(objIds[k])))
                                        map.add(new Long(objIds[k]));
                                }
                                resObj = new Integer(map.size());
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
//            if (dout != null) {
//                final Kernel krn = Kernel.instance();
//                int[] oids = Funcs.makeIntArray(ids);
//                KrnAttribute attr1 = krn.getAttributeById(3560);
//                StringValue[] svs = krn.getStringValues(oids, attr1, 102, false, 0);
//                dout.println("Cell: " + id);
//                for (int i = 0; i < svs.length; ++i)
//                    dout.println(svs[i].value);
//                dout.println();
//            } //daulet- 12.07.2004
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
                    res.append(", " + number);
                    firstNumber = number;
                    delta = 1;
                }
            }
            if (delta > 1) res.append(firstNumber + delta - 1);
            return res.toString();
        }
    }

    class SystemReportNode extends ReportNode {
        private String path = "";

        public SystemReportNode(Element n, Element parentNode) {
            this.parentNode = parentNode;
            node = n;
            Attribute tempNode = n.getAttribute("id");
            id = (tempNode != null && tempNode.getValue().length() > 0)
                    ? Integer.parseInt(tempNode.getValue()) : 0;

            tempNode = n.getAttribute("lang");
            langId = (tempNode != null && tempNode.getValue().length() > 0) ?
                    Integer.parseInt(tempNode.getValue()) : -1;

            tempNode = n.getAttribute("sysPath");
            path = (tempNode != null) ? tempNode.getValue() : null;
            calculated = true;
        }

        void print() throws KrnException {
            StringTokenizer st = new StringTokenizer(path, ".");
            String res = "";

            if (st.hasMoreTokens()) {
                String className = st.nextToken();
                KrnClass cls = s.getClassByName(className);

                KrnObject user = s.getUser();
                KrnObject base = s.getCurrentDb();
                long[] objIds;
                if (cls.id == user.classId)
                    objIds = new long[]{user.id};
                else
                    objIds = new long[]{base.id};

                while (st.hasMoreTokens()) {
                    String attrName = st.nextToken();
                    KrnAttribute attr = s.getAttributeByName(cls, attrName);
                    cls = s.getClassById(attr.typeClassId);

                    if (attr.typeClassId == CID_STRING
                            || attr.typeClassId == CID_MEMO) {
                        long lid = (attr.isMultilingual) ? langId : 0;
                        boolean isMemo = attr.typeClassId == CID_MEMO;

                        StringValue[] svs = s.getStringValues(objIds, attr.id, lid, isMemo, 0);
                        if (svs != null && svs.length > 0) {
                            res = svs[svs.length - 1].value;
                        }
                    } else if (cls.name.equals("integer")) {
                        LongValue[] lvs = s.getLongValues(objIds, attr.id, 0);
                        if (lvs != null && lvs.length > 0)
                            res += lvs[lvs.length - 1].value;
                    } else if (cls.name.equals("float")) {
                        FloatValue[] fvs = s.getFloatValues(objIds, attr.id, 0);
                        if (fvs != null && fvs.length > 0)
                            res += fvs[fvs.length - 1].value;
                    } else {
                        ObjectValue[] ovs = s.getObjectValues(objIds, attr.id, new long[0], 0);
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

    class ReportNode {
        int id;
        long langId;
        Element parentNode;
        boolean hasParent = false;

        ArrayList<ReportNode> children = new ArrayList<ReportNode>();

        boolean needsNulls = true;
        boolean calculated = false;
        
        String expr;
        ASTStart astExpr;
        String path;
        public Element node;
        int format;
        String dateFormat;
        String num;
        ReportNode parent;
        protected String defText_ = "";
        KrnAttribute attr;

        String align, valign, imgPos;

        public ReportNode() {
        }

        public ReportNode(ReportNode parent, Element n, Element parentNode)
                throws KrnException {
            this.parentNode = parentNode;
            this.parent = parent;
            node = n;
            num = "";
            Attribute tempNode = n.getAttribute("id");
            id = (tempNode != null && tempNode.getValue().length() > 0)
                    ? Integer.parseInt(tempNode.getValue()) : 0;

            tempNode = n.getAttribute("defText");
            defText_ = (tempNode != null) ? tempNode.getValue() : "";

            tempNode = n.getAttribute("ref");
            path = (tempNode != null && tempNode.getValue().length() > 0) ? tempNode.getValue() : null;

            tempNode = n.getAttribute("lang");
            String uid = (tempNode != null && tempNode.getValue().length() > 0) ? //daulet-
                    tempNode.getValue() : "";

            String uidInShablon = System.getProperty("uidInShablon");
            if (uid.length() > 0) {
                if ("1".equals(uidInShablon)) {
                    langId = s.getObjectByUid(uid, s.getContext().trId).id;
                } else {
                    langId = Integer.parseInt(uid);
                }
            } else {
                langId = -1;
            }

            tempNode = n.getAttribute("format");
            format = (tempNode != null) ?
                    Integer.parseInt(tempNode.getValue()) : 0;

            tempNode = n.getAttribute("dateFormat");
            dateFormat = (tempNode != null) ? tempNode.getValue() : null;

            tempNode = n.getAttribute("align");
            align = (tempNode != null) ? tempNode.getValue() : null;
            tempNode = n.getAttribute("valign");
            valign = (tempNode != null) ? tempNode.getValue() : null;

            tempNode = n.getAttribute("imgPos");
            imgPos = (tempNode != null) ? tempNode.getValue() : null;

            if (path != null && path.length() > 0) {
                Pair<KrnAttribute, Integer>[] ps = SrvUtils.parsePath(s, path);
                if (ps != null && ps.length > 0)
                    attr = ps[ps.length-1].first;
            }
            else {
                Element e = n.getChild("expr");
                tempNode = n.getAttribute("expr");
                String expr = (e != null) ? e.getText() :
                        (tempNode != null) ? tempNode.getValue() : "";

                if (expr != null) {
                    calculated = true;
                    expr = expr.replaceAll("&#47;", "/");
                    expr = expr.replaceAll("&#92;", "\\");
                    expr = expr.replaceAll("&#34;", "\"");
                    this.expr = expr;
                    if (expr.length() > 0)
                    	this.astExpr = OrLang.createStaticTemplate(expr, log);
                }
            }

            if (parent != null) {
                hasParent = true;
                parent.addChild(this);
            }
        }

        void addChild(ReportNode child) {
            children.add(child);
        }

        public ArrayList<ReportNode> getChildren() {
            return children;
        }

        public ReportNode getParent() {
            return parent;
        }

        void print(KrnObject obj) throws KrnException {
            Attribute str = new Attribute("str", "");
            Attribute fmt = null;
            if (obj != null || calculated) {
                Object value = null;
                if (!calculated) {
                    Pair<KrnAttribute, Object> p = getValueForPath(path, obj, langId);
                    value = p.second;
                    
                    long typeId = (p.first != null) ? p.first.typeClassId : -1;
                    if (typeId == CID_BLOB)
                    	str = new Attribute("src", convertBlob(format, value));
                    else {
                    	str.setValue(convertValueToString(typeId, value));
                    	if (typeId == CID_FLOAT || value instanceof Double)
                    		fmt = new Attribute("type", String.valueOf(CID_FLOAT));
                    }
                } else {
                	value = "";
                	if (astExpr != null) {
		                Map<String, Object> vars = new HashMap<String, Object>();
		                if (obj != null) vars.put("OBJ", obj);
		                boolean res = false;
		                try {
		                    s.getContext().langId = langId;
		                    res = getOrLang().evaluate(astExpr, vars, null, false, new Stack<String>(), null);
		                } catch (EvalException e) {
		                    log.error("Ошибка в формуле отчете! FIELD id=" + id);
		                    log.error(e, e);
		                } catch (Exception e) {
		                    log.error(e, e);
		                }
		                if (res)
		                    value = vars.get("RETURN");
                	}
                    if (value instanceof Collection) {
                    	if (((Collection) value).size() > 0) {
                    		Object val = ((Collection) value).iterator().next();
                    		if (val instanceof byte[]) {
		                    	Collection<byte[]> vs = (Collection<byte[]>) value;
	                        	String res = "";
		                        for (byte[] v : vs) {
		                            res += Base64.encodeBytes(v) + "|";
		                        }
	                        	str = new Attribute("src", res.length() > 0 ? res.substring(0, res.length() - 1) : "");
                    		}
                    	}
                    } else if (value instanceof byte[]) {
                        byte[] v = (byte[]) value;
                        if (v.length > 0) {
                            //str = new Attribute("src", Base64.encodeBytes(v));
                        	str = new Attribute("src", convertBlob(format, value));
                        }
                    } else 
                    	str.setValue(convertToString(value, format, dateFormat));
                	if (format == 0)
                		fmt = new Attribute("type", String.valueOf(CID_FLOAT));
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

        boolean print(Element parentElement, List<KrnObject> objs) throws KrnException {
            return false;
        }

        void print(List<Object> objs) throws KrnException {
            print((KrnObject)objs.get(objs.size() - 1));
        }

        boolean filterAndPrint(Element e, List<KrnObject> objs) throws KrnException {
            return print(e, objs);
        }

        void filterAndPrint(List<Object> objs) throws KrnException {
            print(objs);
        }
    }

    class ColumnReportNode extends ReportNode {
        String tablePath;
        String tablePath2;
        String treeGroupPath;

        long filterId = 0;
        protected String fuid;
        protected Set<Long> filteredIds;
        protected Set<Long> filteredIds2;

        int tableId = -1;
        boolean showNulls = true;
        private boolean filterInnerTable = false;

        public ColumnReportNode(ReportNode parent, Element n,
                                Element parentNode) throws KrnException {
            super(parent, n, parentNode);

            Attribute pathNode = n.getAttribute("table");
            String path = (pathNode != null) ? pathNode.getValue() : null;
            if (path != null && path.length() > 0) {
                tablePath = path;
            }
            if (path != null && path.length() > 0) {
                int t = path.indexOf("@");
                if (t < 0) {
                    tablePath = path;
                } else if (t == 0) {
                	tablePath = path.substring(1);
                } else {
                    tablePath = path.substring(0, t);
                    tablePath2 = path.substring(t + 1);
                }
            }

            pathNode = n.getAttribute("tableId");
            path = (pathNode != null) ? pathNode.getValue() : null;
            if (path != null && path.length() > 0)
                tableId = Integer.parseInt(path);

            pathNode = n.getAttribute("showNulls");
            path = (pathNode != null) ? pathNode.getValue() : null;
            if (path != null && path.length() > 0)
                showNulls = false;

            pathNode = n.getAttribute("sort");
            path = (pathNode != null) ? pathNode.getValue() : null;
            if (path != null && path.length() > 0 && tablePath != null) {
                pathNode = n.getAttribute("direction");
                boolean desc = (pathNode != null) ? "1".equals(pathNode.getValue()) : false;

                setSortAttributes(path, String.valueOf(id), desc);
            }                 

            pathNode = n.getAttribute("sortTree");
            path = (pathNode != null) ? pathNode.getValue() : null;
            if (path != null && path.length() > 0 && tablePath != null) {
                setSortTreeAttributes(path, String.valueOf(id));
            }                 

            Attribute fNode = n.getAttribute("filter");
            fuid = (fNode != null) ? fNode.getValue() : null;

            try {
                if (fuid != null && fuid.length() > 0) {
                    if (fuid.charAt(0) == '@') {
                        fuid = fuid.substring(1);
                        filterInnerTable = true;
                    }

                    filterId = s.getObjectByUid(fuid, s.getContext().trId).id;
                    idByUid.put(fuid, new Long(filterId));
                }
            } catch (Exception ex) {
                log.info("-----===== The filter is not assigned or not found =====-----");
                log.error(ex, ex);
            }


            pathNode = n.getAttribute("sort");
            path = (pathNode != null) ? pathNode.getValue() : null;
        }

        public String getFuid() {
            return fuid;
        }

        public void setFilteredIds(Set<Long> ids) {
            filteredIds = ids;
        }

        public void setFilteredIds2(Set<Long> ids) {
            filteredIds2 = ids;
        }

        void print(KrnObject obj) throws KrnException {
            Pair<KrnAttribute, Integer>[] ps = SrvUtils.parsePath(s, tablePath);
            List<Object> objs = new ArrayList<Object>();
            if (obj != null) {
                if (ps.length == 0) {
                    objs.add(obj);
                    parentObjects.put(new Integer(tableId), new ArrayList<KrnObject>());
                }
                else {
                    List<KrnObject> parentObjs = new ArrayList<KrnObject>();
                    Pair<KrnObject, SortedMap<Integer, Object>> res = 
                    		SrvUtils.getObjectAttr(obj, parentObjs, tablePath, getTransactionId(), 0, s, false);
                    if (res != null) {
                    	SortedMap<Integer, Object> map = res.second;
                    	for (Object val : map.values())
                    		objs.add(val);
                    }
                    parentObjects.put(new Integer(tableId), parentObjs);
                }
            }
            tableObjects.put(new Integer(tableId), objs);
            filterAndPrint(objs);
        }

        void filterAndPrint(List<Object> objs) throws KrnException {
            if (filterId > 0) {
                filteredIds = getFilteredIds(fuid);
                print(objs);
            } else {
                filteredIds = null;
                print(objs);
            }
        }

        boolean filterAndPrint(Element e, List<KrnObject> objs) throws KrnException {
            if (filteredIds == null) {
                if (filterId > 0) {
                    filteredIds = getFilteredIds(fuid);
                    return print(e, objs);
                } else {
                    filteredIds = null;
                    return print(e, objs);
                }
            } else
                return print(e, objs);
        }

        void print(List<Object> objs) throws KrnException {
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

            if (objs != null && objs.size() > 0) {
            	if (objs.get(0) instanceof KrnObject) {
	                KrnObject obj = (KrnObject) objs.get(0);
	                KrnClass cls = s.getClassById(obj.classId);
	                        
	                String refPath = null;
	                if (path != null && path.length() > tablePath.length()) {
	                	if (path.charAt(tablePath.length()) == '.') {
	                		refPath = cls.name + path.substring(tablePath.length());
	                	} else {
	                		int ind = path.indexOf(">", tablePath.length());
	                		if (ind > 0) {
	                			refPath = path.substring(tablePath.length() + 1, ind) + path.substring(ind + 1);
	                		}
	                	}
	                }

	                List<KrnObject> fobjs = new ArrayList<KrnObject>();
	                for (int k = 0; k<objs.size(); k++) {
	                    obj = (KrnObject) objs.get(k);
	                    if (obj != null && (filteredIds == null
	                            || filteredIds.contains(new Long(obj.id)))) {
	                    	fobjs.add(obj);
	                    }
	                }
	                
	                if (fobjs.size() > 0) {
	                	if (tablePath2 == null && !calculated) {
                            Pair<KrnAttribute, Object> p = getValueForPath(refPath, fobjs, langId);
                            Map<Long, Object> valueMap = (Map<Long, Object>) p.second;
        	                for (int k = 0; k < fobjs.size(); k++) {
        	                    obj = fobjs.get(k);

    	                        Attribute str = new Attribute("str", "");
                                Attribute fmt = null;
                                Object value = valueMap.get(obj.id);

                                if(value == null && p.first==null){
                                	str.setValue("");
                            	}else{
                                    long typeId = (p.first != null) ? p.first.typeClassId : -1;
	                                if (typeId == CID_BLOB)
	                                	str = new Attribute("src", convertValueToString(typeId, value));
	                                else {
	                                	str.setValue(convertValueToString(typeId, value));
                                    	if (typeId == CID_FLOAT || value instanceof Double)
                                    		fmt = new Attribute("type", String.valueOf(CID_FLOAT));
	                                }
                                }
                                
	                            Element valueElement = new Element("Value");
	                            valueElement.setAttribute(str);
                                if (fmt != null) valueElement.setAttribute(fmt);
	                            column.addContent(valueElement);
        	                }                                
	                	} else {
	    	                for (int k = 0; k<fobjs.size(); k++) {
	    	                    obj = fobjs.get(k);
    	                        Attribute str = new Attribute("str", "");
                                Attribute fmt = null;
    	                        Object value = null;
    	                        List<KrnObject> parentObjs = new ArrayList<KrnObject>();
    	                        if (parentObjects.get(tableId) != null)
    	                        	parentObjs.addAll(parentObjects.get(tableId));
    	                        
    	                        if (tablePath2 != null) {
    	                            List objs2 = new ArrayList();
    	                            String tabPath2 = null;
	            	                if (tablePath2.length() > tablePath.length()) {
	            	                	if (tablePath2.charAt(tablePath.length()) == '.') {
	            	                		tabPath2 = cls.name + tablePath2.substring(tablePath.length());
	            	                	} else {
	            	                		int ind = tablePath2.indexOf(">", tablePath.length());
	            	                		if (ind > 0) {
	            	                			tabPath2 = tablePath2.substring(tablePath.length() + 1, ind) + tablePath2.substring(ind + 1);
	            	                		}
	            	                	}
	            	                }

    	                            Pair<KrnObject, SortedMap<Integer, Object>> res = SrvUtils.getObjectAttr(obj, parentObjs, tabPath2, getTransactionId(),
    	                                               0, s, false);
    	                            if (res != null) {
    	                            	SortedMap<Integer, Object> map = res.second;
    	                                objs2.addAll(map.values());
    	                            }
    	                            Element valueElement = new Element("Value");
    	                            column.addContent(valueElement);
    	                            if (objs2.size() > 0) {
    	                                KrnObject obj2 = (KrnObject) objs2.get(0);
    	                                KrnClass cls2 = s.getClassById(obj2.classId);
    	                                String refPath2 = null;
    	            	                if (path != null && path.length() > tablePath2.length()) {
	                	                	if (path.charAt(tablePath2.length()) == '.') {
	                	                		refPath2 = cls2.name + path.substring(tablePath2.length());
	                	                	} else {
	                	                		int ind = path.indexOf(">", tablePath2.length());
	                	                		if (ind > 0) {
	                	                			refPath2 = path.substring(tablePath2.length() + 1, ind) + path.substring(ind + 1);
	                	                		}
	                	                	}
    	            	                }
    	                	            for (int t = 0; t < objs2.size(); t++) {
    	                                    obj2 = (KrnObject) objs2.get(t);
    	
    	                                    if (obj2 != null && (!filterInnerTable
    	                                            || filteredIds2 == null
    	                                            || filteredIds2.contains(new Long(obj2.id)))) {
    	
    	                                        Attribute str2 = new Attribute("str", "");
    	                                        Attribute fmt2 = null;
    	                                        Object value2 = null;
    	                                        if (!calculated) {
    	                                            Pair<KrnAttribute, Object> p = getValueForPath(refPath2, obj2, langId);
    	                                            value2 = p.second;
    	
    	                                            long typeId = (p.first != null) ? p.first.typeClassId : -1;
    	                                            if (typeId == CID_BLOB)
    	                                            	str2 = new Attribute("src", convertValueToString(typeId, value2));
    	                                            else { 
    	                                            	str2.setValue(convertValueToString(typeId, value2));
    	                                            	if (typeId == CID_FLOAT || value2 instanceof Double)
    	                                            		fmt2 = new Attribute("type", String.valueOf(CID_FLOAT));

    	                                            }
    	                                        } else {
    	                                    	    value2 = "";
    	                                        	if (astExpr != null) {
    		                                            Map<String, Object> vars = new HashMap<String, Object>();
    		                                            vars.put("OBJ", obj2);
    		    		                                vars.put("OBJS", objs2);
    		    		                                vars.put("PARENTS", parentObjs);
    		                                            boolean res2 = false;
    		                                            try {
    		                                                s.getContext().langId = langId;
    		                                                res2 = getOrLang().evaluate(astExpr, vars, null, false, new Stack<String>(), null);
    		                    		                } catch (EvalException e) {
    		                    		                    log.error("Ошибка в формуле отчете! COLUMN id=" + id);
    		                    		                    log.error(e, e);
    		    		                                } catch (Exception e) {
    		    		                                    log.error(e, e);
    		                                            }
    		                                            if (res2)
    		                                                value2 = vars.get("RETURN");
    	                                        	}
    	
    	                                            str2.setValue(convertToString(value2, format, dateFormat));
    	                                        	if (format == 0)
    	                                        		fmt2 = new Attribute("type", String.valueOf(CID_FLOAT));
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
    	                        } else {
    	                            if (!calculated) {
    	                                Pair<KrnAttribute, Object> p = getValueForPath(refPath, obj, langId);
    	                                value = p.second;
    	                                if(value==null && p.first==null){
    	                                	str.setValue("");
    	                            	}else{
    	                                    long typeId = (p.first != null) ? p.first.typeClassId : -1;
    		                                if (typeId == CID_BLOB)
    		                                	str = new Attribute("src", convertValueToString(typeId, value));
    		                                else {
    		                                	str.setValue(convertValueToString(typeId, value));
    	                                    	if (typeId == CID_FLOAT || value instanceof Double)
    	                                    		fmt = new Attribute("type", String.valueOf(CID_FLOAT));
    		                                }
    	                                }
    	                            } else {
                                	    value = "";
                                    	if (astExpr != null) {
    		                                Map<String, Object> vars = new HashMap<String, Object>();
    		                                vars.put("OBJ", obj);
    		                                vars.put("OBJS", objs);
    		                                vars.put("PARENTS", parentObjs);
    		                                boolean res = false;
    		                                try {
    		                                    s.getContext().langId = langId;
    		                                    res = getOrLang().evaluate(astExpr, vars, null, false, new Stack<String>(), null);
                    		                } catch (EvalException e) {
                    		                    log.error("Ошибка в формуле отчете! COLUMN id=" + id);
                    		                    log.error(e, e);
    		                                } catch (Exception e) {
    		                                    log.error(e, e);
    		                                }
    		                                if (res)
    		                                    value = vars.get("RETURN");
                                    	}	
    	                                str.setValue(convertToString(value, format, dateFormat));
                                    	if (format == 0)
                                    		fmt = new Attribute("type", String.valueOf(CID_FLOAT));
    	                            }
    	                            if (!showNulls && "0".equals(str.getValue())) str.setValue("");
    	                            if (str.getValue().length() == 0) str.setValue(defText_);
    	
    	                            Element valueElement = new Element("Value");
    	                            valueElement.setAttribute(str);
                                    if (fmt != null) valueElement.setAttribute(fmt);
    	                            column.addContent(valueElement);
    	                        }
    	                    }
	                	}
	                }
            	} else {
	                for (int k = 0; k<objs.size(); k++) {
	                    Attribute str = new Attribute("str", "");
                        Attribute fmt = null;
	                    Object value = objs.get(k);
                        long typeId = -1;
                        if (typeId == CID_BLOB)
                        	str = new Attribute("src", convertValueToString(typeId, value));
                        else {
                        	str.setValue(convertValueToString(typeId, value));
                        	if (typeId == CID_FLOAT || value instanceof Double)
                        		fmt = new Attribute("type", String.valueOf(CID_FLOAT));
                        }
                        
                        if (!showNulls && "0".equals(str.getValue())) str.setValue("");
                        if (str.getValue().length() == 0) str.setValue(defText_);

                        Element valueElement = new Element("Value");
                        valueElement.setAttribute(str);
                        if (fmt != null) valueElement.setAttribute(fmt);
                        column.addContent(valueElement);
	                }
            	}
            }

            parentNode.addContent(column);
        }

        boolean print(Element parentElement, List<KrnObject> objs) throws KrnException {
            boolean result = false;
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

            if (objs != null && objs.size() > 0) {
                KrnObject obj = objs.get(0);
                KrnClass cls = s.getClassById(obj.classId);
    	                
                String refPath = null;
                if (path != null && path.length() > tablePath.length()) {
                	if (path.charAt(tablePath.length()) == '.') {
                		refPath = cls.name + path.substring(tablePath.length());
                	} else {
                		int ind = path.indexOf(">", tablePath.length());
                		if (ind > 0) {
                			refPath = path.substring(tablePath.length() + 1, ind) + "." + path.substring(ind + 1);
                			log.info(refPath);
                		}
                	}
                }
        
                for (int k = 0; k<objs.size(); k++) {
                    obj = (KrnObject) objs.get(k);

                    if (obj != null && (filterInnerTable || filteredIds == null
                            || filteredIds.contains(new Long(obj.id)))) {
                        Attribute str = new Attribute("str", "");
                        Object value = null;
                        Attribute fmt = null;
                        List<KrnObject> parentObjs = new ArrayList<KrnObject>();
                        if (parentObjects.get(tableId) != null)
                        	parentObjs.addAll(parentObjects.get(tableId));

                        if (tablePath2 != null) {
                            List objs2 = new ArrayList();
                            String tabPath2 = null;
        	                if (tablePath2.length() > tablePath.length()) {
        	                	if (tablePath2.charAt(tablePath.length()) == '.') {
        	                		tabPath2 = cls.name + tablePath2.substring(tablePath.length());
        	                	} else {
        	                		int ind = tablePath2.indexOf(">", tablePath.length());
        	                		if (ind > 0) {
        	                			tabPath2 = tablePath2.substring(tablePath.length() + 1, ind) + "." + tablePath2.substring(ind + 1);
        	                		}
        	                	}
        	                }

                            Pair res = SrvUtils.getObjectAttr(obj, parentObjs, tabPath2, getTransactionId(),
                                               0, s, false);
                            if (res != null) {
                                SortedMap map = (SortedMap)res.second;
                                objs2.addAll(map.values());
                            }
                            Element valueElement = new Element("Value");
                            column.addContent(valueElement);
                            if (objs2.size() > 0) {
                                KrnObject obj2 = (KrnObject) objs2.get(0);
                                KrnClass cls2 = s.getClassById(obj2.classId);
                                String refPath2 = null;
            	                if (path != null && path.length() > tablePath2.length()) {
            	                	if (path.charAt(tablePath2.length()) == '.') {
            	                		refPath2 = cls2.name + path.substring(tablePath2.length());
            	                	} else {
            	                		int ind = path.indexOf(">", tablePath2.length());
            	                		if (ind > 0) {
            	                			refPath2 = path.substring(tablePath2.length() + 1, ind) + "." + path.substring(ind + 1);
            	                		}
            	                	}
            	                }
                                for (int t = 0; t < objs2.size(); t++) {
                                    obj2 = (KrnObject) objs2.get(t);

                                    if (obj2 != null && (!filterInnerTable
                                            || filteredIds2 == null
                                            || filteredIds2.contains(new Long(obj2.id)))) {

                                        Attribute str2 = new Attribute("str", "");
                                        Attribute fmt2 = null;
                                        Object value2 = null;
                                        if (!calculated) {
                                            Pair<KrnAttribute, Object> p = getValueForPath(refPath2, obj2, langId);
                                            value2 = p.second;

                                            long typeId = (p.first != null) ? p.first.typeClassId : -1;
                                            if (typeId == CID_BLOB)
                                            	str2 = new Attribute("src", convertValueToString(typeId, value2));
                                            else {
                                            	str2.setValue(convertValueToString(typeId, value2));
                                            	if (typeId == CID_FLOAT || value2 instanceof Double)
                                            		fmt2 = new Attribute("type", String.valueOf(CID_FLOAT));
                                            }
                                        } else {
                                    	    value2 = "";
                                        	if (astExpr != null) {
		                                        Map<String, Object> vars = new HashMap<String, Object>();
		                                        vars.put("OBJ", obj2);
	    		                                vars.put("OBJS", objs2);
	    		                                vars.put("PARENTS", parentObjs);
		                                        boolean res2 = false;
		                                        try {
		                                            s.getContext().langId = langId;
		                                            res2 = getOrLang().evaluate(astExpr, vars, null, false, new Stack<String>(), null);
	                    		                } catch (EvalException e) {
	                    		                    log.error("Ошибка в формуле отчете! COLUMN id=" + id);
	                    		                    log.error(e, e);
	    		                                } catch (Exception e) {
	    		                                    log.error(e, e);
		                                        }
		                                        if (res2)
		                                            value2 = vars.get("RETURN");
                                        	}
                                            str2.setValue(convertToString(value2, format, dateFormat));
                                        	if (format == 0)
                                        		fmt2 = new Attribute("type", String.valueOf(CID_FLOAT));
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
                        } else {
                            if (!calculated) {
                                Pair<KrnAttribute, Object> p = getValueForPath(refPath, obj, langId);
                                value = p.second;
                                if (value != null) {
                                    long typeId = (p.first != null) ? p.first.typeClassId : -1;
	                                if (typeId == CID_BLOB)
	                                	str = new Attribute("src", convertValueToString(typeId, value));
	                                else {
	                                	str.setValue(convertValueToString(typeId, value));
                                    	if (typeId == CID_FLOAT || value instanceof Double)
                                    		fmt = new Attribute("type", String.valueOf(CID_FLOAT));
	                                }
                                } else {
                                	str.setValue("");
                                }
                            } else {
                        	    value = "";
                            	if (astExpr != null) {
	                                Map<String, Object> vars = new HashMap<String, Object>();
	                                vars.put("OBJ", obj);
	                                vars.put("OBJS", objs);
	                                vars.put("PARENTS", parentObjs);
	                                boolean res = false;
	                                try {
	                                    s.getContext().langId = langId;
	                                    res = getOrLang().evaluate(astExpr, vars, null, false, new Stack<String>(), null);
            		                } catch (EvalException e) {
            		                    log.error("Ошибка в формуле отчете! COLUMN id=" + id);
            		                    log.error(e, e);
	                                } catch (Exception e) {
	                                    log.error(e, e);
	                                }
	                                if (res)
	                                    value = vars.get("RETURN");
                            	}
                            	str.setValue(convertToString(value, format, dateFormat));
                            	if (format == 0)
                            		fmt = new Attribute("type", String.valueOf(CID_FLOAT));
                            }
                            if (!showNulls && "0".equals(str.getValue())) str.setValue("");
                            if (str.getValue().length() == 0) str.setValue(defText_);

                            Element valueElement = new Element("Value");
                            valueElement.setAttribute(str);
                            if (fmt != null) valueElement.setAttribute(fmt);
                            column.addContent(valueElement);
                            result = true;
                        }
                    }
                }
            }

            if (result) {
                //List children = parentElement.getChildren("Column");
                //int size = (children != null) ? children.size() : 0;
                parentElement.addContent(0, column);
            }
            return result;
        }
    }

    class ConsColumnExReportNode extends ColumnReportNode {
//        protected String groupPath;
        protected List<String> groupPaths;
        protected ASTStart astGroupExpr;

        public ConsColumnExReportNode(ReportNode parent, Element n,
                                Element parentNode) throws KrnException {
            super(parent, n, parentNode);
            groupPaths = new ArrayList<String>();
            Attribute pathNode = n.getAttribute("group");
            String path = (pathNode != null) ? pathNode.getValue() : null;
            if (path != null && path.length() > 0) {
                StringTokenizer st = new StringTokenizer(path, "@");
                while (st.hasMoreTokens()) {
                    groupPaths.add(st.nextToken());
                }
            }
            Element e = n.getChild("groupExpression");
            pathNode = n.getAttribute("groupExpression");
            String groupExpr = (e != null) ? e.getText() :
                    (pathNode != null) ? pathNode.getValue() : null;

            if (groupExpr != null) { // && expr.length() > 0) {
                groupExpr = groupExpr.replaceAll("&#47;", "/");
                groupExpr = groupExpr.replaceAll("&#34;", "\"");
                astGroupExpr = OrLang.createStaticTemplate(groupExpr, log);
            }
            if (parent instanceof TreeReportNode) {
            	((TreeReportNode)parent).groupPaths = groupPaths;
            	((TreeReportNode)parent).astGroupExpr = astGroupExpr;
            }
        }

        void print(KrnObject obj) throws KrnException {
            Pair<KrnAttribute, Integer>[] ps = SrvUtils.parsePath(s, tablePath);
            List<Object> objs = new ArrayList<Object>();
            if (obj != null) {
                if (ps.length == 0) {
                    objs.add(obj);
                }
                else {
                    Pair<KrnObject, SortedMap<Integer, Object>> res
                    	= SrvUtils.getObjectAttr(obj, tablePath, getTransactionId(), 0, s, false);
                    
                    if (res != null) {
                    	SortedMap<Integer, Object> map = res.second;
                    	for (Object val : map.values())
                    		objs.add(val);
                    }
                }
            }
            tableObjects.put(new Integer(tableId), objs);
            print(objs);
        }

        void print(List<Object> objs) throws KrnException {
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

            MultiMap dict = new MultiMap();

            if (objs != null && objs.size() > 0) {
            	if (objs.get(0) instanceof KrnObject) {
            		KrnObject obj = (KrnObject)objs.get(0);
	                KrnClass cls = s.getClassById(obj.classId);
	                Object value = null;
	
	                for (int k = 0; k<objs.size(); k++) {
	                    obj = (KrnObject) objs.get(k);
	                    Object gvalue = null;
	                    String key = "";
	                    for (int m=0; m < groupPaths.size(); m++) {
	                        String groupPath =
	                                cls.name + "." +
	                                groupPaths.get(m).substring(tablePath.length()+1);
	                        Pair<KrnAttribute, Object> p = getValueForPath(groupPath, obj, 0);
	                        gvalue = p.second;
	                        if (gvalue == null) continue;
	                        long typeId = (p.first != null) ? p.first.typeClassId : -1;
	                        key += convertValueToString(typeId, gvalue);
	                    }
	                    if (astGroupExpr != null) {
	                        Map<String, Object> vars = new HashMap<String, Object>();
	                        vars.put("OBJ", obj);
	                        boolean res = false;
	                        try {
	                            s.getContext().langId = langId;
	                            res = getOrLang().evaluate(astGroupExpr, vars, null, false, new Stack<String>(), null);
    		                } catch (EvalException e) {
    		                    log.error("Ошибка в формуле отчете (группировка)! ConsCOLUMN id=" + id);
    		                    log.error(e, e);
                            } catch (Exception e) {
                                log.error(e, e);
	                        }
	
	                        if (res)
	                            gvalue = vars.get("RETURN");
	                        else
	                            gvalue = "";
	                        if (gvalue instanceof List) {
	                            List glist = (List)gvalue;
	                            if (value instanceof List) {
	                                for (int m = 0; m < glist.size(); m++) {
	                                    Object lvalue = glist.get(m);
	                                    String str = key + convertValueToString(-1, lvalue);
	                                    dict.put(str, new ArrayList());
	                                }
	                            } else {
	                                for (int m = 0; m < glist.size(); m++) {
	                                    Object lvalue = glist.get(m);
	                                    String str = key + convertValueToString(-1, lvalue);
	                                    dict.put(str, new ArrayList());
	                                }
	                            }
	                        } else {
	                            key += convertValueToString(-1, gvalue);
	                            dict.put(key, new ArrayList());
	                        }
	                    }
	                    else
	                        dict.put(key, new ArrayList());
	                }
            	
	                for (int k = 0; k<objs.size(); k++) {
	                    obj = (KrnObject) objs.get(k);
	                    if (filteredIds == null
	                        || filteredIds.contains(new Long(obj.id))) {
	                        if (!calculated) {
	                            if (path.length() == tablePath.length()) {
	                                value = obj;
	                            } else {
	                                String refPath = cls.name + "." + path.substring(tablePath.length()+1);
	                                Pair<KrnAttribute, Object> p = getValueForPath(refPath, obj, langId);
	                                value = p.second;
	                            }
	                        } else {
	                            Map<String, Object> vars = new HashMap<String, Object>();
	                            vars.put("OBJ", obj);
                                vars.put("OBJS", objs);
	                            boolean res = false;
	                            try {
	                                s.getContext().langId = langId;
	                                res = getOrLang().evaluate(astExpr, vars, null, false, new Stack<String>(), null);
        		                } catch (EvalException e) {
        		                    log.error("Ошибка в формуле отчете! ConsCOLUMN id=" + id);
        		                    log.error(e, e);
                                } catch (Exception e) {
                                    log.error(e, e);
	                            }
	                            if (res)
	                                value = vars.get("RETURN");
	                            else
	                                value = "";
	                        }
	                        Object gvalue = null;
	                        String key = "";
	                        for (int m=0; m < groupPaths.size(); m++) {
	                            String groupPath =
	                                    cls.name + "." +
	                                    ((String) groupPaths.get(m)).substring(tablePath.length()+1);
	                            Pair<KrnAttribute, Object> p = getValueForPath(groupPath, obj, 0);
	                            gvalue = p.second;
	                            if (gvalue == null) continue;
	                            long typeId = (p.first != null) ? p.first.typeClassId : -1;
	                            key += convertValueToString(typeId, gvalue);
	                        }
	                        if (astGroupExpr != null) {
	                            Map<String, Object> vars = new HashMap<String, Object>();
	                            vars.put("OBJ", obj);
	                            boolean res = false;
	                            try {
	                                s.getContext().langId = langId;
	                                res = getOrLang().evaluate(astGroupExpr, vars, null, false, new Stack<String>(), null);
        		                } catch (EvalException e) {
        		                    log.error("Ошибка в формуле отчете (группировка)! ConsCOLUMNEx id=" + id);
        		                    log.error(e, e);
                                } catch (Exception e) {
                                    log.error(e, e);
	                            }
	
	                            if (res)
	                                gvalue = vars.get("RETURN");
	                            else
	                                gvalue = "";
	                            if (gvalue instanceof List) {
	                                List glist = (List)gvalue;
	                                if (value instanceof List) {
	                                    for (int m = 0; m < glist.size(); m++) {
	                                        Object lvalue = glist.get(m);
	                                        String str = key + convertValueToString(-1, lvalue);
	                                        dict.put(str, ((List)value).get(m));
	                                    }
	                                } else {
	                                    for (int m = 0; m < glist.size(); m++) {
	                                        Object lvalue = glist.get(m);
	                                        String str = key + convertValueToString(-1, lvalue);
	                                        dict.put(str, value);
	                                    }
	                                }
	                            } else {
	                                key += convertValueToString(-1, gvalue);
	                                dict.put(key, value);
	                            }
	                        }
	                        else
	                            dict.put(key, value);
	                    }
	                }
            	}
            }
            for (Iterator it = dict.keySet().iterator(); it.hasNext();) {
                Attribute str = new Attribute("str", "");
                ArrayList values = (ArrayList) dict.get(it.next());
                if (values == null || values.size() == 0) {
                    if (showNulls) str.setValue("0");
                } else {
                    Object value = null;
                    int m = 0;
                    while (value == null && m < values.size()) {
                    	value = values.get(m++);
                    }
                    if (value instanceof String)
                        str.setValue((String) value);
                    else if (value instanceof Double) {
                        double sum = 0.0;
                        for (int i = 0; i < values.size(); ++i) {
                            Double v = (Double) values.get(i);
                            sum = (v != null) ? sum + v.doubleValue() : sum;
                        }
                        if (showNulls || sum != 0.0) str.setValue(floatFormat.format(sum));
                    } else if (value instanceof Number) {
                        long sum = 0;
                        for (int i = 0; i < values.size(); ++i) {
                            Number v = (Number) values.get(i);
                            sum = (v != null) ? sum + v.longValue() : sum;
                        }
                        if (showNulls || sum != 0) str.setValue("" + sum);
                    } else if (attr != null &&
                            (attr.typeClassId == CID_STRING ||
                            attr.typeClassId == CID_MEMO)) {
                        str.setValue("");
                    }
                    else
                        str.setValue("" + values.size());
                }
                if (!showNulls && "0".equals(str.getValue())) str.setValue("");
                Element valueElement = new Element("Value");
                valueElement.setAttribute(str);
                column.addContent(valueElement);
            }

            parentNode.addContent(column);
        }

        boolean print(Element parentElement, List<KrnObject> objs) throws KrnException {
            boolean result = false;
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

            MultiMap dict = new MultiMap();

            if (objs != null && objs.size() > 0) {

                KrnObject obj = objs.get(0);
                KrnClass cls = s.getClassById(obj.classId);
                Object value = null;

                for (int k = 0; k<objs.size(); k++) {
                    obj = objs.get(k);
                    Object gvalue = null;
                    String key = "";
                    for (int m=0; m < groupPaths.size(); m++) {
                        String groupPath =
                                cls.name + "." +
                                ((String) groupPaths.get(m)).substring(tablePath.length()+1);
                        Pair<KrnAttribute, Object> p = getValueForPath(groupPath, obj, 0);
                        gvalue = p.second;
                        if (gvalue == null) continue;
                        long typeId = (p.first != null) ? p.first.typeClassId : -1;
                        key += convertValueToString(typeId, gvalue);
                    }
                    if (astGroupExpr != null) {
                        Map<String, Object> vars = new HashMap<String, Object>();
                        vars.put("OBJ", obj);
                        boolean res = false;
                        try {
                            s.getContext().langId = langId;
                            res = getOrLang().evaluate(astGroupExpr, vars, null, false, new Stack<String>(), null);
		                } catch (EvalException e) {
		                    log.error("Ошибка в формуле отчете (группировка)! ConsCOLUMNEx id=" + id);
		                    log.error(e, e);
                        } catch (Exception e) {
                            log.error(e, e);
                        }

                        if (res)
                            gvalue = vars.get("RETURN");
                        else
                            gvalue = "";
                        if (gvalue instanceof List) {
                            List glist = (List)gvalue;
                            if (value instanceof List) {
                                for (int m = 0; m < glist.size(); m++) {
                                    Object lvalue = glist.get(m);
                                    String str = key + convertValueToString(-1, lvalue);
                                    dict.put(str, new ArrayList());
                                }
                            } else {
                                for (int m = 0; m < glist.size(); m++) {
                                    Object lvalue = glist.get(m);
                                    String str = key + convertValueToString(-1, lvalue);
                                    dict.put(str, new ArrayList());
                                }
                            }
                        } else {
                            key += convertValueToString(-1, gvalue);
                            dict.put(key, new ArrayList());
                        }
                    }
                    else
                        dict.put(key, new ArrayList());
                }

                for (int k = 0; k<objs.size(); k++) {
                    obj = (KrnObject) objs.get(k);
                    if (filteredIds == null
                        || filteredIds.contains(new Long(obj.id))) {
                        if (!calculated) {
                            if (path.length() == tablePath.length()) {
                                value = obj;
                            } else {
                                String refPath = cls.name + "." + path.substring(tablePath.length()+1);
                                Pair<KrnAttribute, Object> p = getValueForPath(refPath, obj, langId);
                                value = p.second;
                            }
                        } else {
                    	    value = "";
                        	if (astExpr != null) {
	                            Map<String, Object> vars = new HashMap<String, Object>();
	                            vars.put("OBJ", obj);
                                vars.put("OBJS", objs);
	                            boolean res = false;
	                            try {
	                                s.getContext().langId = langId;
	                                res = getOrLang().evaluate(astExpr, vars, null, false, new Stack<String>(), null);
        		                } catch (EvalException e) {
        		                    log.error("Ошибка в формуле отчете! ConsCOLUMNEx id=" + id);
        		                    log.error(e, e);
                                } catch (Exception e) {
                                    log.error(e, e);
	                            }
	                            if (res)
	                                value = vars.get("RETURN");
                        	}
                        }
                        Object gvalue = null;
                        String key = "";
                        for (int m=0; m < groupPaths.size(); m++) {
                            String groupPath =
                                    cls.name + "." +
                                    ((String) groupPaths.get(m)).substring(tablePath.length()+1);
                            Pair<KrnAttribute, Object> p = getValueForPath(groupPath, obj, 0);
                            gvalue = p.second;
                            if (gvalue == null) continue;
                            long typeId = (p.first != null) ? p.first.typeClassId : -1;
                            key += convertValueToString(typeId, gvalue);
                        }
                        if (astGroupExpr != null) {
                            Map<String, Object> vars = new HashMap<String, Object>();
                            vars.put("OBJ", obj);
                            boolean res = false;
                            try {
                                s.getContext().langId = langId;
                                res = getOrLang().evaluate(astGroupExpr, vars, null, false, new Stack<String>(), null);
    		                } catch (EvalException e) {
    		                    log.error("Ошибка в формуле отчете (группировка)! ConsCOLUMNEx id=" + id);
    		                    log.error(e, e);
                            } catch (Exception e) {
                                log.error(e, e);
                            }

                            if (res)
                                gvalue = vars.get("RETURN");
                            else
                                gvalue = "";
                            if (gvalue instanceof List) {
                                List glist = (List)gvalue;
                                if (value instanceof List) {
                                    for (int m = 0; m < glist.size(); m++) {
                                        Object lvalue = glist.get(m);
                                        String str = key + convertValueToString(-1, lvalue);
                                        dict.put(str, ((List)value).get(m));
                                    }
                                } else {
                                    for (int m = 0; m < glist.size(); m++) {
                                        Object lvalue = glist.get(m);
                                        String str = key + convertValueToString(-1, lvalue);
                                        dict.put(str, value);
                                    }
                                }
                            } else {
                                key += convertValueToString(-1, gvalue);
                                dict.put(key, value);
                            }
                        }
                        else
                            dict.put(key, value);
                    }
                }
            }
            for (Iterator it = dict.keySet().iterator(); it.hasNext();) {
                Attribute str = new Attribute("str", "");
                ArrayList values = (ArrayList) dict.get(it.next());
                if (values == null || values.size() == 0)
                    str.setValue("0");
                else {
                    Object value = null;
                    int m = 0;
                    while (value == null && m < values.size()) {
                    	value = values.get(m++);
                    }
                    if (value instanceof String)
                        str.setValue((String) value);
                    else if (value instanceof Double) {
                        double sum = 0.0;
                        for (int i = 0; i < values.size(); ++i) {
                            Double v = (Double) values.get(i);
                            sum = (v != null) ? sum + v.doubleValue() : sum;
                        }
                        str.setValue(floatFormat.format(sum));
                    } else if (value instanceof Number) {
                        long sum = 0;
                        for (int i = 0; i < values.size(); ++i) {
                            Number v = (Number) values.get(i);
                            sum = (v != null) ? sum + v.longValue() : sum;
                        }
                        str.setValue("" + sum);
                    } else if (attr != null &&
                            (attr.typeClassId == CID_STRING ||
                            attr.typeClassId == CID_MEMO)) {
                        str.setValue("");
                    } else
                        str.setValue("" + values.size());
                }
                if (!showNulls && "0".equals(str.getValue())) str.setValue("");
                Element valueElement = new Element("Value");
                valueElement.setAttribute(str);
                column.addContent(valueElement);
                result = true;
            }

            if (result) {
                parentElement.addContent(0, column);
            }
            return result;
        }
    }

    class TreeReportNode extends ColumnReportNode {
        private int level = 10;
        private int level2 = 0;
        private int processingLevel;
        String valuePath, parentPath, titlePath, rootPath, childrenPath;
        Map<Element, KrnObject> nodeObjects;
        private KrnObject root;
        private ASTStart astRootExpr, astChildrenExpr;
        private int maxChildLevel;
        private Set<Long> filteredTreeIds_;
        private String tfuid;
        private long tfilterId;
        private Element elementInProgress;
        protected List<String> groupPaths;
        protected ASTStart astGroupExpr;

        public TreeReportNode(ReportNode parent, Element n,
                                Element parentNode)
                throws KrnException {

            super(parent, n, parentNode);

            Attribute pathNode = n.getAttribute("level");
            String level = (pathNode != null) ? pathNode.getValue() : null;
            if (level != null && level.length() > 0) this.level = Integer.parseInt(level);

            pathNode = n.getAttribute("level2");
            level = (pathNode != null) ? pathNode.getValue() : null;
            if (level != null && level.length() > 0) this.level2 = Integer.parseInt(level);

            pathNode = n.getAttribute("oneRow");
            //oneRow = (pathNode != null) ? true : false;

            pathNode = n.getAttribute("root");
            rootPath = (pathNode != null) ? pathNode.getValue() : null;

            pathNode = n.getAttribute("title");
            titlePath = (pathNode != null) ? pathNode.getValue() : null;

            String refPath = null;
            if (titlePath != null && tablePath != null && titlePath.length() > tablePath.length()) {
            	if (titlePath.charAt(tablePath.length()) == '.') {
                    Pair<KrnAttribute, Integer>[] ps = SrvUtils.parsePath(s, tablePath);
                    if (ps != null && ps.length > 0) {
                    	long clsId = ps[ps.length - 1].first.typeClassId;
                    	KrnClass cls = s.getClassById(clsId);
                    	titlePath = cls.name + titlePath.substring(tablePath.length());
                    }
            	} else {
            		int ind = titlePath.indexOf(">", tablePath.length());
            		if (ind > 0) {
            			refPath = titlePath.substring(tablePath.length() + 1, ind) + "." + titlePath.substring(ind + 1);
            		}
            	}
            }

            String rootExpr = null;
            String childrenExpr = null;
            
            for (int i = 0; i < n.getChildren().size(); ++i) {
            	if (n.getChildren().get(i) instanceof Element) {
	                Element child = (Element)n.getChildren().get(i);
	                if ("rootExpr".equals(child.getName())) {
	                    rootExpr = child.getText();
	                }
	                else if ("childrenExpr".equals(child.getName())) {
	                	childrenExpr = child.getText();
	                }
            	}
            }
            
            if (rootExpr == null) {
            	pathNode = n.getAttribute("rootExpr");
            	rootExpr = (pathNode != null) ? pathNode.getValue() : "";
            }

            if (rootExpr != null && rootExpr.length() > 0) {
                rootExpr = rootExpr.replaceAll("&#47;", "/");
                rootExpr = rootExpr.replaceAll("&#92;", "\\");
                rootExpr = rootExpr.replaceAll("&#34;", "\"");
                this.astRootExpr = OrLang.createStaticTemplate(rootExpr, log);

                Map<String, Object> vars = new HashMap<String, Object>();
                boolean res = false;
                try {
                    s.getContext().langId = langId;
                    res = getOrLang().evaluate(astRootExpr, vars, null, false, new Stack<String>(), null);
                } catch (EvalException e) {
                    log.error("Ошибка в формуле отчете (корень)! TreeCOLUMN id=" + id);
                    log.error(e, e);
                } catch (Exception e) {
                    log.error(e, e);
                }
                if (res) {
                	Object o = vars.get("RETURN");
                	root = (o instanceof KrnObject) ? (KrnObject)o : null;
                } else
                    root = null;
            }
            
            if (childrenExpr != null && childrenExpr.length() > 0) {
            	childrenExpr = childrenExpr.replaceAll("&#47;", "/");
                childrenExpr = childrenExpr.replaceAll("&#92;", "\\");
                childrenExpr = childrenExpr.replaceAll("&#34;", "\"");
                this.astChildrenExpr = OrLang.createStaticTemplate(childrenExpr);
            }

            if (root == null) {
                if (rootPath != null && rootPath.indexOf(".") == -1) {
                    KrnClass rootClass = s.getClassByName(rootPath);
                    root = s.getClassObjects(rootClass, new long[0], 0)[0];
                }
            }

            KrnClass objClass = null;
            if (path != null) {
	            if (path.indexOf(".") == -1) {
	                objClass = s.getClassByName(path);
	            } else {
	                KrnAttribute[] pathAttrs = getAttributesForPath(path, s);
	                long classId = pathAttrs[pathAttrs.length - 1].typeClassId;
	                objClass = s.getClassById(classId);
	            }
            } else if (tablePath != null) {
	            if (tablePath.indexOf(".") == -1) {
	                objClass = s.getClassByName(tablePath);
	            } else {
	                KrnAttribute[] pathAttrs = getAttributesForPath(tablePath, s);
	                long classId = pathAttrs[pathAttrs.length - 1].typeClassId;
	                objClass = s.getClassById(classId);
	            }
            }

            tfuid = n.getAttributeValue("filterTree");

            try {
                if (tfuid != null && tfuid.length() > 0) {
                    tfilterId = s.getObjectByUid(tfuid, s.getContext().trId).id;
                    idByUid.put(tfuid, new Long(tfilterId));
                }
            } catch (Exception ex) {
                log.info("-----===== The filter is not assigned or not found =====-----");
                log.error(ex, ex);
            }

            valuePath = objClass.name + ".значение";
            parentPath = objClass.name + ".родитель";
            pathNode = n.getAttribute("childrenRef");
            childrenPath = (pathNode != null) ? pathNode.getValue() : objClass.name + ".дети";
        }

        void print2(List<Object> objs) throws KrnException {
            Element column = new Element("TreeColumn");
            column.setAttribute("id", String.valueOf(id));
            column.setAttribute("tableId", String.valueOf(tableId));
            if (children.size() == 0) column.setAttribute("remove", "false");
            
            if (objs != null && objs.size() > 0) {
            	KrnObject rootObj = (KrnObject)objs.get(0);
            	
                processNode(rootObj, column, 0, null);
                //column.addContent(res);
            }
            parentNode.addContent(column);
        }
        
        boolean processNode(KrnObject obj, Element parentElement, int level, List<KrnObject> items) throws KrnException {
            processingLevel = level;
            boolean res = false;

            Pair<KrnAttribute, Object> p = getValueForPath(titlePath, obj, langId);
            String title = p.second != null ? (String) p.second : "";

            log.info("Title: = " + title);
            
            if (level >= this.level) {
                Element valueElement = new Element("Value");
                valueElement.setAttribute("str", title);
                parentElement.addContent(valueElement);
                parentElement = valueElement;

                if (items == null) {
                	items = new ArrayList<KrnObject>();
                    items.add(obj);
                }
                for (int i = 0; i < children.size(); ++i) {
                    if (children.get(i) instanceof ConsReportNode) {
                        res |= ((ConsReportNode) children.get(i)).print(parentElement, items);
                    } else {
                        res |= ((ReportNode) children.get(i)).filterAndPrint(parentElement, items);
                    }
                }
            }

        	if (this.level2 == 0 || level < this.level2) {
        		if (astChildrenExpr != null) {
	                Map<String, Object> vars = new HashMap<String, Object>();
	                vars.put("OBJ", obj);
	                try {
	                    s.getContext().langId = langId;
	                    res = getOrLang().evaluate(astChildrenExpr, vars, null, false, new Stack<String>(), null);
	                } catch (EvalException e) {
	                    log.error("Ошибка в формуле отчете (дети)! TreeCOLUMN id=" + id);
	                    log.error(e, e);
                    } catch (Exception e) {
                        log.error(e, e);
	                }
	                List children = (List)vars.get("RETURN");
	                if (children != null && children.size() > 0) {
	                	if (groupPaths != null) {
		                    MultiMap dict = new MultiMap();
			                for (int k = 0; k < children.size(); k++) {
			            		KrnObject child = (KrnObject)children.get(k);
				                KrnClass cls = s.getClassById(child.classId);
			                    Object gvalue = null;
			                    String key = "";
			                    for (int m=0; m < groupPaths.size(); m++) {
			                        String groupPath =
		                                cls.name + "." +
			        	                                groupPaths.get(m).substring(tablePath.length()+1);
			                        Pair<KrnAttribute, Object> p1 = getValueForPath(groupPath, child, 0);
			                        gvalue = p1.second;
			                        if (gvalue == null) continue;
			                        long typeId = (p1.first != null) ? p1.first.typeClassId : -1;
			                        key += convertValueToString(typeId, gvalue);
			                    }
		                    	dict.put(key, child);
			                }
		                    for (Iterator it = dict.keySet().iterator(); it.hasNext();) {
		                    	String key = (String)it.next();
		                    	if (key.length() > 0) {
			                        List<KrnObject> values = (List<KrnObject>) dict.get(key);
			                		boolean r = processNode(values.get(0), parentElement, level + 1, values);
			                        res |= r;
		                    	} else {
			                        List<KrnObject> values = (List<KrnObject>) dict.get(key);
				                    for (int k=0; k<values.size(); k++) {
				                		KrnObject child = (KrnObject)values.get(k);
				                		boolean r = processNode(child, parentElement, level + 1, null);
				                        res |= r;
				                	}
		                    	}
		                    }
	                	} else {
		                	for (int k=0; k < children.size(); k++) {
		                		KrnObject child = (KrnObject)children.get(k);
		                		boolean r = processNode(child, parentElement, level + 1, null);
		                        res |= r;
		                	}
	                	}
	                }
        		} else {
        			KrnClass cls = s.getClassById(obj.classId);
                    Pair<KrnObject, SortedMap<Integer, Object>> res2 = SrvUtils.getObjectAttr(obj, cls.name + ".дети", getTransactionId(), 0, s, false);
                    if (res2 != null) {
                    	SortedMap<Integer, Object> m = res2.second;
                    	
                    	for (Object val : m.values()) {
                    		KrnObject child = (KrnObject)val;
	                		boolean r = processNode(child, parentElement, level + 1, null);
	                        res |= r;
                    	}
                    }
        		}
        	}

            if (children.size() == 0) res = true;
            return res;
        }

        void print(List<Object> objs) throws KrnException {
        	if (root == null) {
        		print2(objs);
        		return;
        	}
        	
            MultiMap mmap = globalMap.get(path);
            nodeObjects = globalNodeMap.get(path);
            Element rootElement = globalRoots.get(path);

            if (mmap == null) {
                Map<Long, Element> elements = new TreeMap<Long, Element>();
                List<Long> objIds = new ArrayList<Long>();
                mmap = new MultiMap();
                nodeObjects = new HashMap<Element, KrnObject>();
                if (objs != null && objs.size() > 0) {
                    KrnObject tableObj = (KrnObject)objs.get(0);
                    if (root == null) {
                        Pair<KrnAttribute, Object> p = getValueForPath(rootPath, tableObj, langId);
                        root = (KrnObject) p.second;
                    }

                    KrnClass cls = s.getClassById(tableObj.classId);

	                String refPath = null;
	                if (path != null && path.length() > tablePath.length()) {
	                	if (path.charAt(tablePath.length()) == '.') {
	                		refPath = cls.name + path.substring(tablePath.length());
	                	} else {
	                		int ind = path.indexOf(">", tablePath.length());
	                		if (ind > 0) {
	                			refPath = path.substring(tablePath.length() + 1, ind) + "." + path.substring(ind + 1);
	                			log.info(refPath);
	                		}
	                	}
	                }

                    for (int i = 0; i<objs.size(); i++) {
                        tableObj = (KrnObject)objs.get(i);
                        KrnObject obj = null;
                        if (refPath == null) {
                            obj = tableObj;
                        } else {
                            Pair<KrnAttribute, Object> p = getValueForPath(refPath, tableObj, langId);
                            obj = (KrnObject) p.second;
                        }

                        if (obj != null) {
                            Long objId = new Long(obj.id);
                            mmap.put(objId, tableObj);

                            KrnObject parent = null;
                            do {
                                if (!objIds.contains(obj.id) && (filteredTreeIds_ == null
                                        || filteredTreeIds_.contains(obj.id))) {
                                    parent = getParent(obj);
                                    objIds.add(obj.id);
                                    if (parent != null) obj = parent;
                                } else break;
                            } while (parent != null && obj.id != root.id);
                        }
                    }
                }
                //TreeAdapter.Node root = tree.getRoot();
                if (!objIds.contains(root.id)) objIds.add(root.id);
                rootElement = formTree(root, elements, objIds, 1);
                if (level < 0)
                    level = maxChildLevel + level;
                if (level2 < 0)
                    level2 = maxChildLevel + level2;

                globalRoots.put(path, rootElement);
                globalMap.put(path, mmap);
                globalNodeMap.put(path, nodeObjects);
            }
            Element column = new Element("TreeColumn");
            column.setAttribute("id", String.valueOf(id));
            column.setAttribute("tableId", String.valueOf(tableId));
            if (children.size() == 0) column.setAttribute("remove", "false");
            if (rootElement != null) {
                processNode(rootElement, nodeObjects, mmap, column, 0);
                int i = 0;
                boolean cont = true;
                List<Element> res = new ArrayList<Element>();
                Element root = (Element) rootElement.clone();
                removeColumns(rootElement);
                res.add(root);
                while (i++ < this.level2 && cont) {
                    List temp = new ArrayList();
                    for (int j = 0; j<res.size(); j++) {
                        Element e = (Element)res.get(j);
                        List content = e.getContent();
                        if (content != null) {
                            temp.addAll(content);
                            int size = content.size();
                            for (int k=size-1; k >= 0; k--) {
                                List chs = ((Element)content.get(k)).getChildren("Column");
                                if (chs != null && chs.size() > 0) cont = false;
                                e.removeContent((Element)content.get(k));
                            }
                        }
                    }
                    res = temp;
                }
                column.addContent(res);
            }
            parentNode.addContent(column);
        }

/*
        void print(List objs) throws KrnException {
            MultiMap mmap = (MultiMap) globalMap.get(path);
            nodeObjects = (Map) globalNodeMap.get(path);
            Element rootElement = (Element) globalRoots.get(path);
            if (mmap == null) {
                Map elements = new TreeMap();
                mmap = new MultiMap();
                nodeObjects = new HashMap();
                if (objs != null && objs.size() > 0) {
                    KrnObject tableObj = (KrnObject) objs.get(0);
                    if (root == null) {
                        Pair p = getValueForPath(rootPath, tableObj, langId);
                        root = (KrnObject) p.second;
                    }

                    KrnClass cls = s.getClassById(tableObj.classId);
                    String refPath = (path != null &&
                            path.length() > tablePath.length()) ?
                            cls.name + "." + path.substring(tablePath.length()+1) : null;
                    for (int i = 0; i<objs.size(); i++) {
                        tableObj = (KrnObject) objs.get(i);
                        KrnObject obj = null;
                        if (refPath == null) {
                            obj = tableObj;
                        } else {
                            Pair p = getValueForPath(refPath, tableObj, langId);
                            obj = (KrnObject) p.second;
                        }
                        if (obj != null) {
                            Long objId = new Long(obj.id);
                            mmap.put(objId, tableObj);
                            Element oldValue = null;
                            KrnObject parent = null;
                            do {
                                Element e = (Element) elements.get(objId);
                                if (e == null) {
                                    parent = getParent(obj);
                                    KrnObject v = getValue(obj);
                                    String title = "";
                                    if (v != null) {
                                        title = getTitle(v);
                                    }
                                    Element value = new Element("Value");
                                    value.setAttribute("str", title);
                                    if (oldValue != null) value.addContent(oldValue);
                                    elements.put(objId, value);
                                    nodeObjects.put(value, obj);
                                    oldValue = value;
                                    if (parent != null) {
                                        obj = parent;
                                        objId = new Long(obj.id);
                                    }
                                } else {
                                    if (oldValue != null) e.addContent(oldValue);
                                    break;
                                }
                            } while (parent != null);
                        }
                    }
                }
                rootElement = (Element)elements.get(new Long(root.id));
                globalRoots.put(path, rootElement);
                globalMap.put(path, mmap);
                globalNodeMap.put(path, nodeObjects);
            }
            Element column = new Element("TreeColumn");
            column.setAttribute("id", String.valueOf(id));
            column.setAttribute("tableId", String.valueOf(tableId));
            if (children.size() == 0) column.setAttribute("remove", "false");
            if (rootElement != null) {
                processNode(rootElement, nodeObjects, mmap, column, 0);
                int i = 0;
                List res = new ArrayList();
                Element root = (Element) rootElement.clone();
                removeColumns(rootElement);
                res.add(root);
                while (i++ < level2-1) {
                    List temp = new ArrayList();
                    for (int j = 0; j<res.size(); j++) {
                        Element e = (Element)res.get(j);
                        List content = e.getContent();
                        if (content != null) {
                            temp.addAll(content);
                            int size = content.size();
                            for (int k=size-1; k >= 0; k--) {
                                e.removeContent((Element)content.get(k));
                            }
                        }
                    }
                    res = temp;
                }
                column.addContent(res);
            }
            parentNode.addContent(column);
        }
*/

        private Element formTree(KrnObject obj, Map<Long, Element> elements, List<Long> objIds, int level) throws KrnException {
            Element value = null;
            if (objIds.contains(obj.id)) {
                if (level > maxChildLevel)
                	maxChildLevel = level;
                KrnObject v = getValue(obj);
                String title = null;
                if (v != null) {
                    title = getTitle(v);
                }
                value = new Element("Value");
                value.setAttribute("str", title != null ? title : "");

                elements.put(new Long(obj.id), value);
                nodeObjects.put(value, obj);

                List children = getChildren(obj);
                int nextIndex = 0;
                for (int i = 0; i < children.size(); i++) {
                    KrnObject child = (KrnObject) children.get(i);
                    Element chValue = formTree(child, elements, objIds, level + 1);
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

        private KrnObject getParent(KrnObject obj) throws KrnException {
            Pair<KrnAttribute, Object> p = getValueForPath(parentPath, obj, 0);
            return (KrnObject) p.second;
        }

        public KrnObject getValue(KrnObject obj) throws KrnException {
            Pair<KrnAttribute, Object> p = getValueForPath(valuePath, obj, 0);
            return (KrnObject) p.second;
        }

        private String getTitle(KrnObject value)
                throws KrnException {
            Pair<KrnAttribute, Object> p = getValueForPath(titlePath, value, langId);
            return (String) p.second;
        }

        public List getChildren(KrnObject obj) throws KrnException {
    		List res = new ArrayList();
    		if (astChildrenExpr != null) {
                Map<String, Object> vars = new HashMap<String, Object>();
                vars.put("OBJ", obj);
                try {
                    s.getContext().langId = langId;
                    getOrLang().evaluate(astChildrenExpr, vars, null, false, new Stack<String>(), null);
                } catch (EvalException e) {
                    log.error("Ошибка в формуле отчете (дети)! TreeCOLUMN id=" + id);
                    log.error(e, e);
                } catch (Exception e) {
                    log.error(e, e);
                }
                List children = (List)vars.get("RETURN");
                if (children != null) {
                	for (int k=0; k<children.size(); k++) {
                		KrnObject child = (KrnObject)children.get(k);
                		res.add(child);
                	}
                }
    		} else {
                Pair<KrnObject, SortedMap<Integer, Object>> p = getValuesForPath(childrenPath, obj, langId);
                SortedMap<Integer, Object> map = p.second;
                if (map != null) {
                    for (Integer key : map.keySet()) {
                        res.add(map.get(key));
                    }
                }
    		}

            return res;
        }

        boolean processNode(Element e, Map<Element, KrnObject> nodeObjects, MultiMap mmap, Element parentElement, int level) throws KrnException {
            processingLevel = level;
            elementInProgress = e;
            boolean res = false;

            KrnObject nodeObj = nodeObjects.get(e);
            if (nodeObj == null) return true;
            ArrayList items;
            ArrayList<Long> objs = new ArrayList<Long>();
            if (level == this.level) {
                items = getChildItems(e, nodeObjects, mmap, objs);
            } else {
                items = (ArrayList) mmap.get(new Long(nodeObj.id));
                objs.add(new Long(nodeObj.id));
            }
/*
            if (items != null && items.size() > 0) {
                tableRef.getItems(0).addAll(items);
                tableRef.fireValueChangedEvent(-1, this);
            } else {
                tableRef.fireValueChangedEvent(-1, this);
                ref.getItems(langId).clear();
                ref.getItems(langId).add(ref.new Item(nodeObj));
                ref.fireValueChangedEvent(0, this);
            }
*/
            for (int i = 0; i < children.size(); ++i) {
                if (children.get(i) instanceof ConsReportNode) {
                    res |= ((ConsReportNode) children.get(i)).print(e, items, objs);
                } else {
                    res |= ((ReportNode) children.get(i)).filterAndPrint(e, items);
                }
            }

            if ((this.level2 == 0 || level < this.level2) && level < this.level) {
                for (int i = 0; i < e.getContentSize(); ++i) {
                    boolean r = processNode((Element) e.getChildren().get(i), nodeObjects, mmap, e, level + 1);
                    res |= r;
                }
            }

            if (children.size() == 0) res = true;
            return res;
        }

        private ArrayList getChildItems(Element e, Map<Element, KrnObject> nodeObjects, MultiMap mmap, ArrayList<Long> objs) {
            ArrayList res = new ArrayList();
            KrnObject obj = nodeObjects.get(e);
            ArrayList items = (ArrayList) mmap.get(new Long(obj.id));
            objs.add(new Long(obj.id));
            if (items != null) res.addAll(items);
            for (int i = 0; i < e.getContentSize(); ++i) {
                items = getChildItems((Element) e.getChildren().get(i), nodeObjects, mmap, objs);
                if (items != null) res.addAll(items);
            }
            return res;
        }

        public int getProcessingLevel() {
            return processingLevel;
        }

        public void setFilteredTreeIds(Set<Long> filteredIds) {
            filteredTreeIds_ = filteredIds;
        }
    }

    class ConsReportNode extends ReportNode {
        ConsReportNode(ReportNode parent, Element n, Element parentNode)
                throws KrnException {
            super(parent, n, parentNode);
        }

        boolean print(Element parentElement, List<KrnObject> objs) throws KrnException {
            boolean res = false;
            if (children.size() > 0) {
                Element column = new Element("ConsColumn");
                column.setAttribute("id", String.valueOf(id));
                for (int i = 0; i < children.size(); ++i) {
                	res |= ((ConsValueReportNode) children.get(i)).print(parentElement, objs);
                }
                if (res) parentNode.addContent(column);
            }
            return res;
        }

        boolean print(Element parentElement, ArrayList objs, List nodeObjs) throws KrnException {
            boolean res = false;
            Element column = new Element("ConsColumn");
            column.setAttribute("id", String.valueOf(id));
            if (children.size() > 0) {
                for (int i = 0; i < children.size(); ++i) {
                    res |= ((ConsValueReportNode) children.get(i)).print(column, objs, nodeObjs);
                }
            }
            if (res) parentElement.addContent(column);
            return res;
        }
    }

    class ConsValueReportNode extends ColumnReportNode {
        String consPath;

        ConsValueReportNode(ReportNode parent, Element n, Element parentNode)
                throws KrnException {
            super(parent, n, parentNode);
            String path = n.getAttributeValue("group");
            if (path != null && path.length() > 0)
                consPath = path;

            path = n.getAttributeValue("treeGroup");
            if (path != null && path.length() > 0)
                treeGroupPath = path;
        }

        boolean print(Element parentElement, List<KrnObject> objs) throws KrnException {
            boolean res = false;
            MultiMap mmap = new MultiMap();

            if (consPath != null) {
                TreeReportNode trn = (TreeReportNode) this.parent.parent;
//                KrnObject treeObj = trn.nodeInProgress.getObject();
                KrnObject treeObj = trn.nodeObjects.get(trn.elementInProgress);

                if (objs != null && objs.size() > 0) {
                    KrnObject obj = (KrnObject)objs.get(0);
                    KrnClass cls = s.getClassById(obj.classId);
                    String consRefPath = (consPath != null && consPath.length() > tablePath.length()) ?
                            cls.name + "." + consPath.substring(tablePath.length()+1) : null;
                    String treeGroupRefPath = (treeGroupPath != null && treeGroupPath.length() > tablePath.length()) ?
                            cls.name + "." + treeGroupPath.substring(tablePath.length()+1) : null;
                    for (int k = 0; k<objs.size(); k++) {
                        obj = (KrnObject)objs.get(k);
                        Pair p = getValueForPath(consRefPath, obj, langId);
                        long typeId = ((KrnAttribute)p.first).typeClassId;
                        Object key = p.second;
                        p = getValueForPath(treeGroupRefPath, obj, langId);
                        Object treeGroupObj = p.second;

                        if (treeObj != null && treeGroupObj != null
                                && treeObj.id == ((KrnObject) treeGroupObj).id
                                && obj != null && filteredIds != null
                                && filteredIds.contains(new Long(obj.id)) && key != null) {

                                    mmap.put(convertValueToString(typeId, key), new Integer(k));
                        }
                    }
                    if (mmap.keySet().size() > 0) res = true;
                    for (Iterator it = mmap.keySet().iterator(); it.hasNext();) {
                        String key = (String) it.next();
                        ArrayList<Integer> inds = (ArrayList<Integer>) mmap.get(key);

                        Element consValue = new Element("ConsValue");
                        consValue.setAttribute("id", "" + id);
                        consValue.setAttribute("str", "" + calculate(inds, objs));
                        consValue.setAttribute("title", key);
                        parentNode.addContent(consValue);
                    }
                }
            } else if (objs != null && objs.size() > 0) {
                TreeReportNode trn = (TreeReportNode) this.parent.parent;
                //KrnObject treeObj = trn.nodeInProgress.getObject();
                KrnObject treeObj = trn.nodeObjects.get(trn.elementInProgress);

                ArrayList<Integer> inds = new ArrayList<Integer>();

                KrnObject obj = (KrnObject)objs.get(0);
                KrnClass cls = s.getClassById(obj.classId);
                String treeGroupRefPath = (treeGroupPath != null && treeGroupPath.length() > tablePath.length()) ?
                        cls.name + "." + treeGroupPath.substring(tablePath.length()+1) : null;
                for (int k = 0; k<objs.size(); k++) {
                    obj = (KrnObject)objs.get(k);
                    Pair<KrnAttribute, Object> p = getValueForPath(treeGroupRefPath, obj, langId);
                    Object treeGroupObj = p.second;

                    if (treeObj != null && treeGroupObj != null
                            && treeObj.id == ((KrnObject) treeGroupObj).id
                            && obj != null && filteredIds != null
                            && filteredIds.contains(new Long(obj.id))) {

                        inds.add(new Integer(k));
                    }
                }

                if (inds.size() > 0) {
                    Element consValue = new Element("ConsValue");
                    consValue.setAttribute("id", "" + id);
                    consValue.setAttribute("str", "" + calculate(inds, objs));
                    consValue.setAttribute("title", "");
                    parentNode.addContent(consValue);
                    res = true;
                }
            }
            return res;
        }

        boolean print(Element parentElement, ArrayList objs, List nodeObjs) throws KrnException {
            boolean res = false;
            MultiMap mmap = new MultiMap();

            if (consPath != null) {
                if (objs != null && objs.size() > 0) {
                    KrnObject obj = (KrnObject) objs.get(0);
                    KrnClass cls = s.getClassById(obj.classId);
                    String consRefPath = (consPath != null && consPath.length() > tablePath.length()) ?
                            cls.name + "." + consPath.substring(tablePath.length()+1) : null;
                    String treeGroupRefPath = (treeGroupPath != null && treeGroupPath.length() > tablePath.length()) ?
                            cls.name + "." + treeGroupPath.substring(tablePath.length()+1) : null;
                    for (int k = 0; k<objs.size(); k++) {
                        obj = (KrnObject) objs.get(k);
                        Pair p = getValueForPath(consRefPath, obj, langId);
                        long typeId = ((KrnAttribute)p.first).typeClassId;
                        Object key = p.second;
                        p = getValueForPath(treeGroupRefPath, obj, langId);
                        Object treeGroupObj = p.second;

                        if (treeGroupObj != null
                                && nodeObjs.contains(new Long(((KrnObject) treeGroupObj).id))
                                && obj != null && filteredIds != null
                                && filteredIds.contains(new Long(obj.id)) && key != null) {

                                    mmap.put(convertValueToString(typeId, key), new Integer(k));
                        }
                    }
                    if (mmap.keySet().size() > 0) res = true;
                    for (Iterator it = mmap.keySet().iterator(); it.hasNext();) {
                        String key = (String) it.next();
                        ArrayList<Integer> inds = (ArrayList<Integer>) mmap.get(key);

                        Element consValue = new Element("ConsValue");
                        consValue.setAttribute("id", "" + id);
                        consValue.setAttribute("str", "" + calculate(inds, objs));
                        consValue.setAttribute("title", key);
                        parentElement.addContent(consValue);
                    }
                }
            } else if (objs != null && objs.size() > 0) {
                ArrayList<Integer> inds = new ArrayList<Integer>();

                KrnObject obj = (KrnObject) objs.get(0);
                KrnClass cls = s.getClassById(obj.classId);
                String treeGroupRefPath = (treeGroupPath != null && treeGroupPath.length() > tablePath.length()) ?
                        cls.name + "." + treeGroupPath.substring(tablePath.length()+1) : null;
                for (int k = 0; k<objs.size(); k++) {
                    obj = (KrnObject) objs.get(k);
                    Pair<KrnAttribute, Object> p = getValueForPath(treeGroupRefPath, obj, langId);
                    Object treeGroupObj = p.second;

                    if (treeGroupObj != null
                            && nodeObjs.contains(new Long(((KrnObject) treeGroupObj).id))
                            && obj != null && filteredIds != null
                            && filteredIds.contains(new Long(obj.id))) {

                        inds.add(new Integer(k));
                    }
                }

                if (inds.size() > 0) {
                    Element consValue = new Element("ConsValue");
                    consValue.setAttribute("id", "" + id);
                    consValue.setAttribute("str", "" + calculate(inds, objs));
                    consValue.setAttribute("title", "");
                    parentElement.addContent(consValue);
                    res = true;
                }
            }
            return res;
        }

        private Object calculate(ArrayList<Integer> inds, List objs) throws KrnException {
            if (inds == null || inds.size() == 0) return new Integer(0);
            long typeId;
            if (!calculated) {
                Pair<KrnAttribute, Integer>[] ps = SrvUtils.parsePath(s, path);
                if (ps != null && ps.length > 0)
                    typeId = ps[ps.length - 1].first.typeClassId;
                else
                    typeId = s.getClassByName(path).id;
            } else {
                typeId = CID_FLOAT;
            }
            KrnObject obj = (KrnObject) objs.get(0);
            KrnClass cls = s.getClassById(obj.classId);
            String refPath = null;
            if (path != null && path.length() > tablePath.length()) {
            	if (path.charAt(tablePath.length()) == '.') {
            		refPath = cls.name + path.substring(tablePath.length());
            	} else {
            		int ind = path.indexOf(">", tablePath.length());
            		if (ind > 0) {
            			refPath = path.substring(tablePath.length() + 1, ind) + "." + path.substring(ind + 1);
            			log.info(refPath);
            		}
            	}
            }

            if (typeId == CID_INTEGER) {
                int res = 0;
                for (int i = 0; i < inds.size(); i++) {
                    int j = inds.get(i).intValue();
                    Number val = (Number) getValueForPath(refPath, (KrnObject)objs.get(j), langId).second;
                    if (val != null)
                        res += val.longValue();
                }
                return new Integer(res);
            } else if (typeId == CID_FLOAT) {
                double res = 0;
                for (int i = 0; i < inds.size(); i++) {
                    int j = inds.get(i).intValue();
                    if (calculated) {
                        Map<String, Object> vars = new HashMap<String, Object>();
                        vars.put("OBJ", (KrnObject)objs.get(j));
                        Double val = 0.0;
                        boolean r = false;
                        try {
                            s.getContext().langId = langId;
                            r = getOrLang().evaluate(astExpr, vars, null, false, new Stack<String>(), null);
		                } catch (EvalException e) {
		                    log.error("Ошибка в формуле отчете! TreeCOLUMN id=" + id);
		                    log.error(e, e);
                        } catch (Exception e) {
                            log.error(e, e);
                        }
                        if (r)
                            val = (Double)vars.get("RETURN");

                        if (val != null)
                            res += val.doubleValue();
                    } else {
                        Number val = (Number) getValueForPath(refPath, (KrnObject)objs.get(j), langId).second;
                        if (val != null)
                            res += val.doubleValue();
                    }
                }
                return new Double(res);
            } else if (typeId == CID_STRING) {
                String val = (String) getValueForPath(refPath, (KrnObject)objs.get(0), langId).second;
                if (val == null) return "";
                return  val;
            } else
                return new Integer(inds.size());
        }
    }

    private Pair<KrnAttribute, Object> getValueForPath(String refPath, KrnObject obj, long langId)
                            throws KrnException {
        Object value = null;
        KrnAttribute attr = null;
        Pair<KrnAttribute, Integer>[] ps = SrvUtils.parsePath(s, refPath);
        if (ps == null) return new Pair<KrnAttribute, Object>(null, null);
        Pair<KrnObject, SortedMap<Integer, Object>> res = SrvUtils.getObjectAttr(obj, refPath, getTransactionId(),
                                langId, s, false);
        if (res != null) {
        	SortedMap<Integer, Object> map = res.second;
            Pair<KrnAttribute, Integer> p = ps[ps.length - 1];
            attr = p.first;
            int i = 0;
            if (attr.collectionType == 1) {
                int last = ((Number)map.lastKey()).intValue();
                if (p.second instanceof Number) {
                    i = ((Number)p.second).intValue();
                    if (i < 0) {
                        i = last + 1 + i;
                    }
                } else {
                    i = last + 1;
                }
            }
            if (i >= 0) {
                value = map.get(new Integer(i));
            }
        }
        return new Pair<KrnAttribute, Object>(attr, value);
    }

	private Pair<KrnAttribute, Object> getValueForPath(String refPath, List<KrnObject> objs, long langId) throws KrnException {
		Map<Long, Object> valueMap = new HashMap<Long, Object>();
		Pair<KrnAttribute, Integer>[] ps = SrvUtils.parsePath(s, refPath);
		Pair<KrnAttribute, Integer> p = ps[ps.length - 1];
		KrnAttribute attr = p.first;
		Map<Long, SortedMap<Integer, Object>> res = SrvUtils
				.getObjectAttr(objs, refPath, getTransactionId(), langId, s);
		if (res != null) {
			for (KrnObject obj : objs) {
				SortedMap<Integer, Object> map = res.get(obj.id);
				 
				if (map != null) {
					int i = 0;
					if (attr.collectionType == 1) {
						int last = ((Number) map.lastKey()).intValue();
						if (p.second instanceof Number) {
							i = ((Number) p.second).intValue();
							if (i < 0) {
								i = last + 1 + i;
							}
						} else {
							i = last + 1;
						}
					}
					if (i >= 0) {
						valueMap.put(obj.id, map.get(new Integer(i)));
					}
				}
			}
		}
		return new Pair<KrnAttribute, Object>(attr, valueMap);
	}

    private Pair<KrnObject, SortedMap<Integer, Object>> getValuesForPath(String refPath, KrnObject obj, long langId)
                            throws KrnException {
        Pair<KrnObject, SortedMap<Integer, Object>> res = SrvUtils.getObjectAttr(obj, refPath, getTransactionId(),
                                langId, s, false);
        return res;
    }

    private KrnAttribute[] getAttributesForPath(String path, Session s)
            throws KrnException {
        if (path == null)
            return null;
        StringTokenizer st = new StringTokenizer(path, ".");
        int count = st.countTokens();
        KrnAttribute[] res = new KrnAttribute[(count == 0) ? 0 : count - 1];
        if (count > 0) {
            KrnClass c = s.getClassByName(st.nextToken());
            for (int i = 0; i < count - 1; ++i) {
                PathElement pe = Funcs.parseAttrName(st.nextToken());
                KrnAttribute attr = s.getAttributeByName(c, pe.name);
                res[i] = attr;
                if (attr == null) return null;
                if (pe.castClassName != null) {
                    c = s.getClassByName(pe.castClassName);
                } else {
                    c = s.getClassById(attr.typeClassId);
                }
            }
        }
        return res;
    }

    private long getTransactionId() {
        return s.getContext().trId;
    }

    private String convertValueToString(long typeId, Object obj) {
        String str = "";
        if(obj==null) return str;
        if (typeId == CID_DATE)
            str = (obj instanceof Date) ? simpleFormat.format((Date)obj) : "";
        else if (typeId == CID_TIME)
            str = (obj instanceof Date) ? timeFormat.format((Date)obj) : "";
        else if (obj instanceof Date)
            str = simpleFormat.format((Date)obj);
        else if (typeId == CID_FLOAT || obj instanceof Double)
            str = floatFormat.format(((Number) obj).doubleValue());
        else if (typeId == CID_INTEGER || obj instanceof Integer)
            str = String.valueOf(((Number) obj).longValue());
        else if (typeId == CID_BLOB || obj instanceof byte[]) {
            if (((byte[])obj).length > 0)
                str = Base64.encodeBytes((byte[])obj);
        }
        else if (obj instanceof KrnObject)
            str = "" + ((KrnObject) obj).id;
        else if (obj != null) {
            str = obj.toString();
            if (str.indexOf(0x0) > -1) {
            	str = str.replaceAll("\u0000", " ");
            }
        }
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
        		str = Base64.encodeBytes(img);
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
                    return floatFormat.format(((Number)value).doubleValue());
                case 1:
                    if (value instanceof Number)
                        return "" + ((Number) value).longValue();
                    else if (value instanceof String)
                    	return (String)value;
                    return "";
                case 2:
                    return simpleFormat.format(new Date(((Number)value).longValue() * 1000L));
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

    public void removeNullRows (Element root) {
        int sz = root.getContentSize();
        for (int i = sz - 1; i >=0; i--) {
            Content content = root.getContent(i);
            if (content instanceof Element) {
            	Element e = (Element)content;
	            Element parent = (Element)e.getParent();
	            if (e.getName().equals("Column")) {
	                String tableId = e.getAttributeValue("tableId");
	                List<Element> cols = getElementsByAttribute(parent, "tableId", tableId);
	                
	                int chSz = e.getContentSize();
	                for (int row = chSz - 1; row >= 0; row--) {
	                    boolean isNull = true;
	                    for (int col = 0; col<cols.size(); col++) {
	                        Element column = cols.get(col);
	                        //TODO Посмотреть Ерику
	                        Content val = (column.getContentSize() <= row) ? null : column.getContent(row);
	                        String s = (val instanceof Element) ? ((Element)val).getAttributeValue("str") : "0";
	                        //---------------------
	                        if (s == null || (s.length() > 0 && !"0".equals(s))) {
	//                        if (s != null && s.length() > 0 && !"0".equals(s)) {
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
	                parent.removeContent(i);
	            }
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

    public void sortTree(Element root, final Map<String, String> paths) {
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
                sortTree(root, id);
            }
        }
        catch (Exception ex) {
            log.error(ex, ex);
        }
    }

    public void sort (Element root, String id, boolean desc) {
        List<Element> elements = new ArrayList<Element>();
        getElementsById(root, id, elements);
        for (int i = 0; i < elements.size(); i++) {
            Element e = elements.get(i);
            if (e.getName().equals("Column")) {
                sortColumn(e, desc);
            }
            else if (e.getName().equals("TreeColumn")) {
                sortTreeColumn(e);
            }
        }
    }

    public void sortTree(Element root, String id) {
        List<Element> elements = root.getChildren();
        for (int i = 0; i < elements.size(); i++) {
            Element e = elements.get(i);
            if (e.getName().equals("TreeColumn")) {
                sortTreeColumn(e, id);
            }
        }
    }

    public void sortColumn (Element element, boolean desc) {
        int chSz = element.getContentSize();
        
        Element parent = (Element)element.getParent();
        String tableId = element.getAttributeValue("tableId");
        List<Element> elements = getElementsByAttribute(parent, "tableId", tableId);

        String d = (String) element.getAttributeValue("dateFormat");
        final ThreadLocalDateFormat dateFormat = (d != null) ? ThreadLocalDateFormat.get(d) : simpleFormat;

        if (chSz > 0) {
        	Content content = element.getContent(0);
        	if (content instanceof Element) {
	        	Element val = (Element)content;
	        	if (val.getAttributeValue("str") == null && val.getAttributeValue("src") == null && element.getAttributeValue("name") == null) {
	                for (int i=0; i<chSz; i++) {
	                	content = element.getContent(i);
	                    if (content instanceof Element) {
		                	Element value = (Element)content;
		                    
		                    //List children2 = value.getChildren();
		                    int chSz2 = value.getContentSize();
		                    
		                    List<ArrayElement> toSort = new ArrayList<ArrayElement>();
		                    for (int j=0; j<chSz2; j++) {
		                        Content content2 = value.getContent(j);
		                        if (content2 instanceof Element) {
			                        Element value2 = (Element) content2;
			                        ArrayList values2 = new ArrayList();
			                        for (Element col : elements) {
			                            Element val1 = (Element)col.getContent(i);
			                            
			                            if (val1.getAttributeValue("str") == null && val1.getAttributeValue("src") == null) {
			                            	values2.add(val1.getContent(j));
			                            }
			                        }
			                        toSort.add(new ArrayElement(value2, values2));
		                        }
		                    }
		                    ArrayElement[] vals2 = new ArrayElement[toSort.size()];
		                    for (int j=0; j<toSort.size(); j++) {
		                    	vals2[j] = toSort.get(j);
		                    }
		
		                    Arrays.sort(vals2, new ColumnComparator(dateFormat, desc));
		
		                    for (Element col : elements) {
		                        Element val1 = (Element)col.getContent(i);
		                        
		                    	if (val1.getAttributeValue("str") == null && val1.getAttributeValue("src") == null) {
		                    		if (col.getAttribute("nosort") == null) {
		                                val1.removeContent();
		                            }
		                        }
		                    }
		
		                    for (ArrayElement val2 : vals2) {
		                        ArrayList<Element> values2 = val2.getElements();
		
		                        int j = 0;
		                        
		                        for (Element col : elements) {
		                            Element val1 = (Element)col.getContent(i);
		                            
		                        	if (val1.getAttributeValue("str") == null && val1.getAttributeValue("src") == null) {
		                        		if (col.getAttribute("nosort") == null) {
		                                    val1.addContent(values2.get(j));
		                                }
		                        		j++;
		                            }
		                        }
		                    }
	                    }
	                }        		
	        	} else {
	        	
			        ArrayElement[] vals = new ArrayElement[chSz];
			        for (int i=0; i<chSz; i++) {
	                	content = element.getContent(i);
	                    if (content instanceof Element) {
		                	Element value = (Element)content;
				            ArrayList<Element> values = new ArrayList<Element>();
				            for (int j=0; j<elements.size(); j++) {
				                Element col = elements.get(j);
				                values.add((Element)col.getContent(i));
				            }
				            vals[i] = new ArrayElement(value, values);
	                    }
			        }
			
			        Arrays.sort(vals, new ColumnComparator(dateFormat, desc));
			
			        for (int j=0; j<elements.size(); j++) {
			            Element col = elements.get(j);
			            Element v = col.getChild("Value");
			            if (v != null) {
			            	boolean sort = v.getAttribute("str") == null && v.getAttribute("src") == null;
				            if (sort || col.getAttribute("nosort") == null) {
				                col.removeContent();
				                col.setAttribute("sort", "1");
				            }
			            }
			        }
			
			        for (int i=0; i<vals.length; i++) {
			            ArrayList<Element> values = vals[i].getElements();
			
			            for (int j=0; j<values.size(); j++) {
			                Element col = elements.get(j);
			            	boolean sort = "1".equals(col.getAttributeValue("sort"));
				            if (sort || col.getAttribute("nosort") == null) {
			                    col.addContent((Element) values.get(j));
				            }
			            }
			        }
	
			        for (int j=0; j<elements.size(); j++) {
			            Element col = elements.get(j);
				        col.removeAttribute("sort");
			        }
	        	}
        	}
        }
    }

    class ColumnComparator implements Comparator {
    	private ThreadLocalDateFormat dateFormat;
    	private boolean desc = false;

        public ColumnComparator(ThreadLocalDateFormat dateFormat, boolean desc) {
			super();
			this.dateFormat = dateFormat;
			this.desc = desc;
		}

		public int compare(Object o1, Object o2) {
            ArrayElement a1 = (ArrayElement) o1;
            ArrayElement a2 = (ArrayElement) o2;
            Element e1 = a1.getElement();
            Element e2 = a2.getElement();

            String s1 = e1.getAttributeValue("str");
            String s2 = e2.getAttributeValue("str");
            if (s1 == null) s1 = e1.getText();
            if (s2 == null) s2 = e2.getText();
            if (s1 == null) return (desc ? 1 : -1);
            if (s2 == null) return (desc ? -1 : 1);
            try {
                double d1 = Double.parseDouble(s1);
                double d2 = Double.parseDouble(s2);
                if (d1 > d2)
                    return desc ? -1 : 1;
                else if (d1 < d2)
                    return desc ? 1 : -1;
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
                } catch (Exception ignored) {
                }

                return s1.compareTo(s2);
            }
        });

        element.removeChildren("Value");

        for (int i=0; i<vals.length; i++) {
            Element val = (Element) vals[i];
            element.addContent(val);
        }
    }

    public void sortTreeColumn(Element element, final String id) {
        List<Element> children = element.getChildren("Value");

        for (int i = 0; i<children.size(); i++) {
            Element child = (Element) children.get(i);
            sortTreeColumn(child, id);
        }

        Element[] vals = children.toArray(new Element[children.size()]);

        Arrays.sort(vals, new Comparator<Element>() {
            public int compare(Element e1, Element e2) {
            	try {
	                Element c1 = (Element) XPath.selectSingleNode(e1,  ".//Column[@id='" + id + "']/Value");
	                Element c2 = (Element) XPath.selectSingleNode(e2,  ".//Column[@id='" + id + "']/Value");
	
	                if (c1 != null && c2 != null) {
		                String s1 = c1.getAttributeValue("str");
		                String s2 = c2.getAttributeValue("str");
		                try {
		                    double d1 = Double.parseDouble(s1);
		                    double d2 = Double.parseDouble(s2);
		                    if (d1 > d2)
		                        return 1;
		                    else if (d1 < d2)
		                        return -1;
		                    else
		                        return 0;
		                } catch (Exception ignored) {
		                }
		
		                return s1.compareTo(s2);
	                }
            	} catch (Exception e) {
            		log.error(e, e);
            	}
            	return 0;
            }
        });

        element.removeChildren("Value");

        for (int i=0; i<vals.length; i++) {
            Element val = (Element) vals[i];
            element.addContent(val);
        }
    }

    public void getElementsById (Element root, String id, List<Element> res) {
        int size = root.getContentSize();
        for (int i = 0; i < size; i++) {
        	Content c = root.getContent(i);
        	if (c instanceof Element) {
	            Element e = (Element) c;
	            if (id.equals(e.getAttributeValue("id")))
	                res.add(e);
	            getElementsById(e, id, res);
        	}
        }
    }

    public List<Element> getElementsByAttribute (Element root, String attr, String id) {
        List<Element> res = new ArrayList<Element>();
        int size = root.getContentSize();
        for (int i = 0; i < size; i++) {
        	Content c = root.getContent(i);
        	if (c instanceof Element) {
		        Element e = (Element) c;
		        if (id.equals(e.getAttributeValue(attr)))
		            res.add(e);
		        //res.addAll(getElementsByAttribute(e, attr, id));
        	}
        }
        return res;
    }

    public class ArrayElement {
        Element element;
        ArrayList<Element> elements;

        public ArrayElement(Element element, ArrayList<Element> elements) {
            this.element = element;
            this.elements = elements;
        }

        public ArrayList<Element> getElements() {
            return elements;
        }

        public Element getElement() {
            return element;
        }
    }

     public Element generateReport(Element report, long langId) throws KrnException {
         clearSortAttributes();
         Element res = new Element("Report");
         String path = report.getChildText("ClassName");
         KrnClass cls = s.getClassByName(path);
         String title = report.getChildText("Title");
         Element titleElem = new Element("Title");
         titleElem.setText(title);
         res.addContent(titleElem);
         Element crits = new Element("Criteria");
         res.addContent(crits);

         List<Element> filters = report.getChildren("Filter");
         KrnObject[] objs = null;
         KrnAttribute multiName = s.getAttributeByName(s.getClassByName("СПРАВОЧНИК"), "мультинаименование");
         KrnAttribute langName = s.getAttributeByName(s.getClassByName("Language"), "name");
         KrnAttribute baseName = s.getAttributeByName(s.getClassByName("Структура баз"), "наименование");
         long[] fids = (filters != null) ? new long[filters.size()] : new long[0];
         int filterNumber = 0;
         for (Element filter : filters) {
             String fuid = filter.getChild("UID").getText();
             List params = filter.getChild("Params").getChildren();
             s.clearFilterParams(fuid);
             for (int j=0; j<((params != null) ? params.size() : 0); j++) {
                 Element param = (Element)params.get(j);
                 String paramName = param.getChild("Name").getText();
                 String paramValue = param.getChild("Value").getText();
                 String paramPath = param.getChild("Type").getText();
                 String critText = param.getChild("Text").getText();
                 int typeClassId = Integer.parseInt(paramPath);
                 Object val = null;
                 String tval = "";
                     switch (typeClassId) {
                         case CID_STRING :
                         case CID_MEMO :
                             {
                                 val = tval = paramValue;
                                 break;
                             }
                         case CID_INTEGER :
                         case CID_BOOL :
                             {
                                 val = new Integer(paramValue);
                                 tval = paramValue;
                                 break;
                             }
                         case CID_FLOAT :
                             {
                                 val = new Double(paramValue);
                                 tval = paramValue;
                                 break;
                             }
                         case CID_DATE :
                             {
                                 try {
                                     Date d = ThreadLocalDateFormat.dd_MM_yyyy.parse(paramValue);
                                     val = Funcs.convertDate(d);
                                     tval = paramValue;
                                 } catch (Exception e) {}
                                 break;
                             }
                         case CID_TIME :
                             {
                                 try {
                                     Date d = ThreadLocalDateFormat.dd_MM_yyyy_HH_mm_ss.parse(paramValue);
                                     val = Funcs.convertTime(d);
                                     tval = paramValue;
                                 } catch (Exception ignored) {}
                                 break;
                             }
                         case CID_BLOB :
                             {
                                 break;
                             }
                         default :
                             {
                                 val = s.getObjectByUid(paramValue, s.getContext().trId);

                                 try {
                                     String[] ss = s.getStrings(((KrnObject)val).id, multiName.id, langId, false, 0);
                                     if (ss != null && ss.length > 0) tval = ss[0];
                                     else {
                                         ss = s.getStrings(((KrnObject)val).id, langName.id, 0, false, 0);
                                         if (ss != null && ss.length > 0) tval = ss[0];
                                         else {
                                             ss = s.getStrings(((KrnObject)val).id, baseName.id, 0, false, 0);
                                             if (ss != null && ss.length > 0) tval = ss[0];
                                         }
                                     }
                                 } catch (Exception e1) {
                                     try {
                                         String[] ss = s.getStrings(((KrnObject)val).id, langName.id, 0, false, 0);
                                         if (ss != null && ss.length > 0) tval = ss[0];
                                     } catch (Exception e2) {
                                         try {
                                             String[] ss = s.getStrings(((KrnObject)val).id, baseName.id, 0, false, 0);
                                             if (ss != null && ss.length > 0) tval = ss[0];
                                         } catch (Exception e3) {
                                         }
                                     }
                                 }
                             }
                     }

                 Element crit = new Element("Crit");
                 crit.setText(critText + ": " + tval);
                 crits.addContent(crit);
                 List vals = s.getFilterParams(fuid, paramName);
                 if (vals != null && vals.size() > 0) {
                     vals.add(val);
                     s.setFilterParam(fuid, paramName, vals);
                 } else {
                    s.setFilterParam(fuid, paramName, val);
                 }
             }

             fids[filterNumber++] = s.getObjectByUid(fuid, s.getContext().trId).id;
         }
         objs = s.getClassObjects(cls, fids, 0);

         Element attrsTag = report.getChild("Attrs");
         List attrTags = attrsTag.getChildren("Attr");

         for (int i = 0; i<attrTags.size(); i++) {
             Element attrTag = (Element)attrTags.get(i);
             String name = attrTag.getChildText("Name");
             path = attrTag.getChildText("Path");
             String sort = attrTag.getChildText("Sort");
             if (sort != null && sort.length() > 0) {
                 setSortAttributes(sort, Integer.toString(i), false);
             }

             Element column = new Element("Column");
             String width = attrTag.getChildText("Width");
             if (width != null && width.length() > 0) {
                 column.setAttribute("width", width);
             }
             column.setAttribute("name", name);
             column.setAttribute("tableId", "1");
             column.setAttribute("id", Integer.toString(i));
             for (int j = 0; j<objs.length; j++) {
                KrnObject obj = objs[j];
                Object value  = null;
                if (path != null && path.length() > 0) {
                    Pair p = getValueForPath(path, obj, langId);
                    value = p.second;
                }
                String svalue = formatObject(value);
                Element valueTag = new Element("Value");
                valueTag.setText(svalue);
                column.addContent(valueTag);
             }
             res.addContent(column);
         }

         if (getSortAttributes().size() > 0)
             sort(res, getSortAttributes(), getSortAttributesDesc());

         return res;
    }
     
    public Element generateReport2(Element report, long langId) throws KrnException {
        clearSortAttributes();
        Element res = new Element("Report");
        String path = report.getChildText("ClassName");
        KrnClass cls = s.getClassByName(path);
        String title = report.getChildText("Title");
        Element titleElem = new Element("Title");
        titleElem.setText(title);
        res.addContent(titleElem);
        Element crits = new Element("Criteria");
        res.addContent(crits);

        List<Element> filters = report.getChildren("Filter");

        KrnAttribute multiName = s.getAttributeByName(s.getClassByName("СПРАВОЧНИК"), "мультинаименование");
        KrnAttribute langName = s.getAttributeByName(s.getClassByName("Language"), "name");
        KrnAttribute baseName = s.getAttributeByName(s.getClassByName("Структура баз"), "наименование");
        long[] fids = (filters != null) ? new long[filters.size()] : new long[0];
        int filterNumber = 0;
        for (Element filter : filters) {
            String fuid = filter.getChild("UID").getText();
            List params = filter.getChild("Params").getChildren();
            s.clearFilterParams(fuid);
            for (int j=0; j<((params != null) ? params.size() : 0); j++) {
                Element param = (Element)params.get(j);
                String paramName = param.getChild("Name").getText();
                String paramValue = param.getChild("Value").getText();
                String paramPath = param.getChild("Type").getText();
                String critText = param.getChild("Text").getText();
                int typeClassId = Integer.parseInt(paramPath);
                Object val = null;
                String tval = "";
                    switch (typeClassId) {
                        case CID_STRING :
                        case CID_MEMO :
                            {
                                val = tval = paramValue;
                                break;
                            }
                        case CID_INTEGER :
                        case CID_BOOL :
                            {
                                val = new Integer(paramValue);
                                tval = paramValue;
                                break;
                            }
                        case CID_FLOAT :
                            {
                                val = new Double(paramValue);
                                tval = paramValue;
                                break;
                            }
                        case CID_DATE :
                            {
                                try {
                                    Date d = ThreadLocalDateFormat.dd_MM_yyyy.parse(paramValue);
                                    val = Funcs.convertDate(d);
                                    tval = paramValue;
                                } catch (Exception e) {}
                                break;
                            }
                        case CID_TIME :
                            {
                                try {
                                    Date d = ThreadLocalDateFormat.dd_MM_yyyy_HH_mm_ss.parse(paramValue);
                                    val = Funcs.convertTime(d);
                                    tval = paramValue;
                                } catch (Exception e) {}
                                break;
                            }
                        case CID_BLOB :
                            {
                                break;
                            }
                        default :
                            {
                                val = s.getObjectByUid(paramValue, s.getContext().trId);

                                try {
                                    String[] ss = s.getStrings(((KrnObject)val).id, multiName.id, langId, false, 0);
                                    if (ss != null && ss.length > 0) tval = ss[0];
                                    else {
                                        ss = s.getStrings(((KrnObject)val).id, langName.id, 0, false, 0);
                                        if (ss != null && ss.length > 0) tval = ss[0];
                                        else {
                                            ss = s.getStrings(((KrnObject)val).id, baseName.id, 0, false, 0);
                                            if (ss != null && ss.length > 0) tval = ss[0];
                                        }
                                    }
                                } catch (Exception e1) {
                                    try {
                                        String[] ss = s.getStrings(((KrnObject)val).id, langName.id, 0, false, 0);
                                        if (ss != null && ss.length > 0) tval = ss[0];
                                    } catch (Exception e2) {
                                        try {
                                            String[] ss = s.getStrings(((KrnObject)val).id, baseName.id, 0, false, 0);
                                            if (ss != null && ss.length > 0) tval = ss[0];
                                        } catch (Exception e3) {
                                        }
                                    }
                                }
                            }
                    }

                Element crit = new Element("Crit");
                crit.setText(critText + ": " + tval);
                crits.addContent(crit);
                List vals = s.getFilterParams(fuid, paramName);
                if (vals != null && vals.size() > 0) {
                    vals.add(val);
                    s.setFilterParam(fuid, paramName, vals);
                } else {
                   s.setFilterParam(fuid, paramName, val);
                }
            }
            fids[filterNumber++] = s.getObjectByUid(fuid, s.getContext().trId).id;
        }
        Set<KrnObject> finalObjs = null;
        for (int i = 0; i<fids.length; i++) {
            KrnObject[] objs = s.getClassObjects(cls, new long[] {fids[i]}, 0);
            Set<KrnObject> obs = new TreeSet<KrnObject>(new KrnObjectComparator());
            for (int j = 0; j < objs.length; ++j)
                obs.add(objs[j]);

            if (finalObjs == null)
                finalObjs = obs;
            else
                finalObjs.retainAll(obs);
        }

        Element attrsTag = report.getChild("Attrs");
        List attrTags = attrsTag.getChildren("Attr");

        for (int i = 0; i<attrTags.size(); i++) {
            Element attrTag = (Element)attrTags.get(i);
            String name = attrTag.getChildText("Name");
            String uid = attrTag.getChildText("UID");
            KrnObject attrObj = s.getObjectByUid(uid, s.getContext().trId);
            Pair p = getValueForPath("Атрибут класса отчета.путь к атрибуту в конструкторе", attrObj, langId);
            path = (p.second instanceof String) ? (String) p.second : null;

            p = getValueForPath("Атрибут класса отчета.формула", attrObj, langId);
            ASTStart expr = (p.second instanceof String) ? OrLang.createStaticTemplate((String) p.second, log) : null;

            String sort = attrTag.getChildText("Sort");
            if (sort != null && sort.length() > 0) {
                setSortAttributes(sort, new Integer(i).toString(), false);
            }

            Element column = new Element("Column");
            String width = attrTag.getChildText("Width");
            if (width != null && width.length() > 0) {
                column.setAttribute("width", width);
            }
            String type = attrTag.getChildText("Type");
            if (type != null && type.length() > 0) {
                column.setAttribute("type", type);
            }
            String align = attrTag.getChildText("Align");
            if (align != null && align.length() > 0) {
                column.setAttribute("align", align);
            }
            column.setAttribute("name", name);
            column.setAttribute("tableId", "1");
            column.setAttribute("id", new Integer(i).toString());
            for (Iterator<KrnObject> it = finalObjs.iterator(); it.hasNext(); ) {
                KrnObject obj = it.next();
                Object value;
                if (path != null && path.length() > 0) {
                    p = getValueForPath(path, obj, langId);
                    value = p.second;
                } else {
                    Map<String, Object> vars = new HashMap<String, Object>();
                    vars.put("OBJ", obj);
                    boolean r = false;
                    try {
                        s.getContext().langId = langId;
                        r = getOrLang().evaluate(expr, vars, null, false, new Stack<String>(), null);
	                } catch (EvalException e) {
	                    log.error("Ошибка в формуле отчете!");
	                    log.error(e, e);
                    } catch (Exception e) {
                        log.error(e, e);
                    }
                    if (r)
                        value = vars.get("RETURN");
                    else
                        value = "";
                }
                String svalue = formatObject(value);
                Element valueTag = new Element("Value");
                valueTag.setText(svalue);
                column.addContent(valueTag);
            }
            res.addContent(column);
        }

        if (getSortAttributes().size() > 0)
            sort(res, getSortAttributes(), getSortAttributesDesc());

        return res;
   }

     public Element getUserActionsList(Element report) throws KrnException {
    	 String start = report.getChildText("BeginDate");
    	 String end = report.getChildText("EndDate");
    	 String type = report.getChildText("Type");
    	 String user = report.getChildText("User");
    	 String ip = report.getChildText("IP");
    	 String action = report.getChildText("Action");
    	 String computer = report.getChildText("Computer");
    	 
    	 String reportName = s.getReportLogName();
    	 return ReportLogHelper.getUserActionsList(start, end, type, action, user, ip, computer, reportName);
     }

     public Element getUserActionsReport(Element report) throws KrnException {
    	 String start = report.getChildText("BeginDate");
    	 String end = report.getChildText("EndDate");
    	 String type = report.getChildText("Type");
    	 String user = report.getChildText("User");
    	 String ip = report.getChildText("IP");
    	 String action = report.getChildText("Action");
    	 String computer = report.getChildText("Computer");
    	 
    	 String reportName = s.getReportLogName();

    	 return ReportLogHelper.getUserActionsReport(start, end, type, action, user, ip, computer, reportName);
     }

     private String formatObject(Object value) {
        String res = "";
        if (value instanceof KrnObject) {
            res = "obj " + ((KrnObject)value).id;
        } else if (value instanceof Date) {
            res = simpleFormat.format((Date)value);
        } else if (value != null) {
            res = value.toString();
        }
        return res;
    }

    private class Row implements Comparable {
        private Object[] values;

        public Row (int size) {
            values = new Object[size];
        }

        public Object[] getValues() {
            return values;
        }

        public int compareTo(Object o) {
            return 0;
        }
    }

    class KrnObjectComparator implements Comparator<KrnObject> {
		public int compare(KrnObject o1, KrnObject o2) {
			return (o1.id < o2.id
					? -1
					: o1.id == o2.id ? 0
					: 1);
		}

	}
/*
    private static Element load(File file) {
        try {
            InputStream is = new BufferedInputStream(new FileInputStream(file));
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(is);
            is.close();
            return doc.getRootElement();
        } catch (Exception e) {
            return null;
        }
    }

    private static void save(Element xml, File f) {
        try {
            OutputStream os = new BufferedOutputStream(new FileOutputStream(f));

            XMLOutputter opr = new XMLOutputter();
            opr.getFormat().setEncoding("UTF-8");
            xml.detach();
            opr.output(new Document(xml), os);

            os.close();
        } catch (IOException e) {
            log.error(e, e);
        }
    }

    public static void main(String args[]) {
        File f = new File("D:\\WORK\\or3final\\doc\\xxx27022.xml");
        Element root = load(f);

        List<Element> colTags = null;
        try {
            colTags = XPath.selectNodes(root,  "//*[@numType='1']");
        } catch (JDOMException e) {
            log.error(e, e);  //To change body of catch statement use File | Settings | File Templates.
        }
        if (colTags != null) {
            Map<String, Integer> colIds = new HashMap();
            for (Element colTag : colTags) {
                Integer curRow = colIds.get(colTag.getAttributeValue("id"));
                if (curRow == null) curRow = 0;
                List<Element> valueTags = colTag.getChildren("Value");
                for (Element valueTag : valueTags) {
                    valueTag.setAttribute("str", String.valueOf(++curRow));
                }
                colIds.put(colTag.getAttributeValue("id"), curRow);
            }
        }

        f = new File("D:\\WORK\\or3final\\doc\\xxx27022.xml");
        save(root, f);
    }*/
    
	private SrvOrLang getOrLang() {
    	return SrvOrLang.class.cast(s.getOrLang());
    }
}
