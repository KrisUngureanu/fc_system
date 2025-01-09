package kz.tamur.rt;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;

import kz.tamur.comps.Constants;
import kz.tamur.util.ImageUtil;
import kz.tamur.util.ThreadLocalDateFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.record.RowRecord;
import org.apache.poi.hssf.record.aggregates.RowRecordsAggregate;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFShape;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFHeaderFooter;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.impl.values.XmlValueDisconnectedException;
import org.apache.xmlbeans.impl.xb.xmlschema.SpaceAttribute.Space;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTColor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFldChar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFonts;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHdrFtr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHpsMeasure;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTInd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPicture;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSimpleField;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSpacing;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTUnderline;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STFldCharType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STLineSpacingRule;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STUnderline;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.impl.CTBodyImpl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.impl.CTHdrFtrImpl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.impl.CTTcImpl;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.SwingWorker;
import com.cifs.or2.util.MultiMap;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

/**
 * Created by IntelliJ IDEA.
 * User: erik-b
 * Date: 03.09.2009
 * Time: 11:12:01
 * To change this template use File | Settings | File Templates.
 */
public class ReportWrapperPOI extends SwingWorker {
    private static Log log = LogFactory.getLog(ReportWrapperPOI.class);

    public static final XmlOptions DEFAULT_XML_OPTIONS;
    static {
        DEFAULT_XML_OPTIONS = new XmlOptions();
        DEFAULT_XML_OPTIONS.setSaveOuter();
        DEFAULT_XML_OPTIONS.setUseDefaultNamespace();
        DEFAULT_XML_OPTIONS.setSaveAggressiveNamespaces();
    }
    
    private Map<Integer, XmlObjectRange> idToRange = new TreeMap<Integer, XmlObjectRange>();
    private Map<Integer, Object> idToCell = new TreeMap<Integer, Object>();
    private Map<Integer, ArrayList<String[]>> vals = new TreeMap<Integer, ArrayList<String[]>>();
    private Map<Integer, int[]> lens = new TreeMap<Integer, int[]>();
    private Map<Integer, Boolean> lenChanged = new TreeMap<Integer, Boolean>();
    private Map<Integer, Integer> types = new TreeMap<Integer, Integer>();

    private Map<Integer, Cell> filterDatesMap = new TreeMap<Integer, Cell>();
    private Map<Integer, String> typeMap = new TreeMap<Integer, String>();
    private Map<Integer, String> treeMap = new TreeMap<Integer, String>();
    private Map<Integer, String> specsumMap = new TreeMap<Integer, String>();
    private Map<Integer, String> initialMap = new TreeMap<Integer, String>();
    private Map<Integer, String> consValMap = new TreeMap<Integer, String>();
    private Map<Integer, String> opMap = new TreeMap<Integer, String>();
    private Map<Integer, Integer> tableMap = new TreeMap<Integer, Integer>();
    private Map<Integer, Integer> columnMap = new TreeMap<Integer, Integer>();
    private Map<Integer, Integer> treeColumnMap = new TreeMap<Integer, Integer>();
    private Map<Integer, Integer> processedColumns = new TreeMap<Integer, Integer>();
    private Map<String, String> totalMap = new TreeMap<String, String>();
    private Map<String, String> freeMap = new TreeMap<String, String>();
    private Map<String, String> countMap = new TreeMap<String, String>();
    private Map<String, String> statMap = new TreeMap<String, String>();
    private Map<Integer, Boolean> noemptyMap = new TreeMap<Integer, Boolean>();

    private Map<String, String> tblIds = new TreeMap<String, String>();
    private MultiMap<Integer, XmlObject> toDelete = new MultiMap<Integer, XmlObject>();
    
    private Map<Integer, MultiMap<Integer, CellRangeAddress>> mergedRegionMap = new TreeMap<>();

    private int currentValue = 0;
    private String fileName;
    private String dataFileName;
    private String title;
    private String macros;
    private String pd;
    private int id = 0;
    private int type = 0;

    boolean firstColumn = false;
    int firstConsValue = 0;
    boolean firstRowColumn = true;
    int cCount = 0, fCount = 0;

    private boolean fastReport = false;
    private boolean showAfterComplete;

    private long flowId;
    private ReportObserver taskTable;

    private String jndiInitial;
    private String jndiPkgs;
    private String jndiUrl;
    private String baseName;
    private static char defaultSeparator = new DecimalFormat().getDecimalFormatSymbols().getDecimalSeparator();

    private ThreadLocalDateFormat df = new ThreadLocalDateFormat("dd.MM.yyyy");

	private static final int ALIGN_LEFT = 1;
	private static final int ALIGN_RIGHT = 2;
	private static final int ALIGN_CENTER = 3;
	private static final int ALIGN_JUSTIFY = 4;
	
	private static final int FRAGMENT_TEXT = 0;
	private static final int FRAGMENT_FONT_SIZE = 1;
	private static final int FRAGMENT_FONT_COLOR = 2;
	private static final int FRAGMENT_TEXT_ALIGN = 3;
	private static final int FRAGMENT_NEW_LINE = 4;
	private static final int FRAGMENT_TEXT_BOLD = 5;
	private static final int FRAGMENT_TEXT_ITALIC = 6;
	private static final int FRAGMENT_TEXT_UNDERLINE = 7;

	
    public ReportWrapperPOI(String fileName, String dataFileName, String title, String macros, String pd, int type, boolean showAfterComplete) {
        this.fileName = fileName;
        this.dataFileName = dataFileName;
        this.title = title;
        this.macros = macros;
        this.pd = pd;
        this.type = type;
        this.showAfterComplete = showAfterComplete;
    }

    public ReportWrapperPOI(String fileName, String dataFileName, String title, String macros, String pd, int type, boolean showAfterComplete,
    		ReportObserver taskTable, long flowId) {
        this.fileName = fileName;
        this.dataFileName = dataFileName;
        this.title = title;
        this.macros = macros;
        this.pd = pd;
        this.type = type;
        this.showAfterComplete = showAfterComplete;
        this.taskTable = taskTable;
        this.flowId = flowId;
    }

    public ReportWrapperPOI(String fileName, String title, int id, int type,
    		String jndiInitial, String jndiPkgs, String jndiUrl, String baseName) {
        this.fileName = fileName;
        this.title = title;
        this.id = id;
        this.type = type;
        
        this.jndiInitial = jndiInitial;
        this.jndiPkgs = jndiPkgs;
        this.jndiUrl = jndiUrl;
        this.baseName = baseName;
    }

    public Object construct() {
        print();
        return null;
    }

    public void print() {
        long time = System.currentTimeMillis();
        if (type == Constants.MSWORD_EDITOR) {
            if (id > 0)
                createWordReport(fileName, title, id, jndiInitial,
      				  jndiPkgs, jndiUrl, baseName);
            else
                viewWordReport(fileName, dataFileName, title, macros);
        } else {
            if (id > 0)
                createExcelReport(fileName, title, id, jndiInitial,
      				  jndiPkgs, jndiUrl, baseName);
            else {
                try {
                    System.out.println("loading xml..." + new Date());
                    InputStream is = new FileInputStream(dataFileName);
                    SAXBuilder builder = new SAXBuilder();
                    Element xml = builder.build(is).getRootElement();

                    System.out.println("counting values..." + new Date());
                    int totalCount = CountAllValues(xml);
                    System.out.println("Grand Total: " + totalCount + " " + new Date());

                    if (taskTable != null) {
                    	taskTable.setProgressCaption("Формирование отчета:");
                    	taskTable.setProgressMinimum(0);
                    	taskTable.setProgressMaximum(totalCount);
                    }

                    viewExcelReport(fileName, xml, title, macros);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("Forming report time: " + (System.currentTimeMillis() - time));
        if (taskTable != null && flowId > 0) {
            taskTable.setReportComplete(flowId);
        }
    }

    public void createWordReport(String fileName, String title, int id,
    		String jndiInitial, String jndiPkgs, String jndiUrl, String baseName) {
        ComThread.InitSTA();
        ActiveXComponent word = ActiveXComponent.createNewInstance("Word.Application");
        try {
            System.out.println(" version=" + word.getProperty("Version"));

            File dir = new File("doc");

            String dotPath = dir.getAbsolutePath().substring(0, dir.getAbsolutePath().length() - 3) + "ORAdminReport.dot";

            String savePath = dir.getAbsolutePath() + "\\" + title + ".doc";

            Dispatch oDocs = word.getProperty("Documents").toDispatch();
            Dispatch oDoc;

            if ("no-file".equals(fileName)) {
                oDoc = Dispatch.call(oDocs, "Add", dotPath, new Variant(false)).toDispatch();

                Dispatch oVars = Dispatch.get(oDoc, "Variables").toDispatch();
                Dispatch.call(oVars, "Add", "id", new Variant(id)).toDispatch();

                Dispatch.call(oDoc, "SaveAs", savePath, new Variant(0), new Variant(false), "", new Variant(true), "", new Variant(false), new Variant(false));
            } else {
                oDoc = Dispatch.call(oDocs, "Open", fileName).toDispatch();
                Dispatch.put(oDoc, "AttachedTemplate", dotPath);
            }
            Dispatch.call(word, "Run", "SetJNDI", jndiInitial,
  				  jndiPkgs, jndiUrl, baseName);

            word.setProperty("Visible", new Variant(true));
        } catch (Exception e) {
            word.invoke("Quit", new Variant(false));
            e.printStackTrace();
        } finally {
            ComThread.Release();
        }
    }

    public static void resaveWordReport(String fileName, String fileName2, int format) {
        System.out.println("Initializing ComThread...");
        ComThread.InitSTA();
        System.out.println("Opening word...");
        ActiveXComponent word = ActiveXComponent.createNewInstance("Word.Application");
        try {
            System.out.println(" version=" + word.getProperty("Version"));

            //File dir = new File("doc");

            System.out.println("Opening document...");
            Dispatch oDocs = word.getProperty("Documents").toDispatch();
            Dispatch oDoc = Dispatch.call(oDocs, "Open", fileName).toDispatch();

            Dispatch attachedTemplate = Dispatch.get(oDoc, "AttachedTemplate").toDispatch();
            String templateName = (attachedTemplate != null) ? Dispatch.get(attachedTemplate, "Name").getString() : "";
            System.out.println("Attached template name = " + templateName);

            if (templateName.contains("ORAdminReport")) {
                System.out.println("Removing attached template...");
                Dispatch.put(oDoc, "AttachedTemplate", "");
            }

            // 12 - docx формат
            System.out.println("Saving document...");
            Dispatch.call(oDoc, "SaveAs", fileName2, new Variant(format));
            
            System.out.println("Closing word...");
            word.invoke("Quit", new Variant(false));
        } catch (Exception e) {
            System.out.println("Closing word after exception...");
            word.invoke("Quit", new Variant(false));
            e.printStackTrace();
        } finally {
            ComThread.Release();
        }
    }

    public void createExcelReport(String fileName, String title, int id,
    		String jndiInitial, String jndiPkgs, String jndiUrl, String baseName) {
        ComThread.InitSTA();
        ActiveXComponent excel = ActiveXComponent.createNewInstance("Excel.Application");
        try {
            System.out.println(" version=" + excel.getProperty("Version"));

            File dir = new File("doc");

            String addinPath = dir.getAbsolutePath().substring(0, dir.getAbsolutePath().length() - 3) + "ORAdminExcel.xla";

            String savePath = dir.getAbsolutePath() + "\\" + title + ".xls";

            Dispatch wbs = excel.getProperty("Workbooks").toDispatch();
            Dispatch wb;

            if ("no-file".equals(fileName)) {
                wb = Dispatch.call(wbs, "Add").toDispatch();

                Dispatch.call(wb, "SaveAs", savePath, new Variant(-4143), "", "", new Variant(false), new Variant(false), new Variant(0), new Variant(2));

            } else {
                Dispatch.call(wbs, "Open", fileName).toDispatch();
            }

            String macro = "'" + addinPath + "'!Test";
            Dispatch.call(excel, "Run", macro);
            macro = "'" + addinPath + "'!SetJNDI";
            Dispatch.call(excel, "Run", macro, jndiInitial,
  				  jndiPkgs, jndiUrl, baseName);
            excel.setProperty("Visible", new Variant(true));

        } catch (Exception e) {
            excel.invoke("Quit");
            e.printStackTrace();
        } finally {
            ComThread.Release();
        }
    }

    public static void resaveExcelReport(String fileName, String fileName2, int format) {
        System.out.println("Initializing ComThread...");
        ComThread.InitSTA();
        System.out.println("Opening excel...");
        ActiveXComponent excel = ActiveXComponent.createNewInstance("Excel.Application");
        try {
            System.out.println(" version=" + excel.getProperty("Version"));

            //File dir = new File("doc");

            System.out.println("Opening spreadsheet...");
            Dispatch wbs = excel.getProperty("Workbooks").toDispatch();
            Dispatch wb = Dispatch.call(wbs, "Open", fileName).toDispatch();

            // 51 - xlsx формат
            System.out.println("Saving spreadsheet...");
            Dispatch.call(wb, "SaveAs", fileName2, new Variant(format), "", "", new Variant(false), new Variant(false), new Variant(0), new Variant(2));
            
            System.out.println("Closing excel...");
            excel.invoke("Quit");
        } catch (Exception e) {
            System.out.println("Closing excel after exception...");
            excel.invoke("Quit");
            e.printStackTrace();
        } finally {
            ComThread.Release();
        }
    }

    public void viewWordReport(String fileName, String dataFileName, String title, String macros) {
        try {
            InputStream fis = new FileInputStream(fileName);
            XWPFDocument oDoc = new XWPFDocument(fis);
            fis.close();

            InputStream is = new FileInputStream(dataFileName);
            SAXBuilder builder = new SAXBuilder();
            Element xml = builder.build(is).getRootElement();

            int totalCount = CountAllValues(xml);

            if (taskTable != null) {
            	taskTable.setProgressCaption("Формирование отчета:");
            	taskTable.setProgressMinimum(0);
            	taskTable.setProgressMaximum(totalCount);
            }
            System.out.println("Total: " + totalCount);

            XWPFHeaderFooterPolicy policy = oDoc.getHeaderFooterPolicy();
            if (policy != null) {
            	List<XWPFFooter> footers = oDoc.getFooterList();
            	for (XWPFFooter f : footers) {
            		processHeader(oDoc, f, xml, f.getPackagePart());
            	}
            	
            	List<XWPFHeader> headers = oDoc.getHeaderList();
            	for (XWPFHeader f : headers) {
            		processHeader(oDoc, f, xml, f.getPackagePart());
            	}
            }

        	findWordComments(oDoc.getDocument());
        	processSingleComments(oDoc, xml);

            CTTbl[] oTabs = oDoc.getDocument().getBody().getTblArray();

            int tCount = (oTabs != null) ? oTabs.length : 0;

            int lastId = -1;
            for (int m = 0; m < tCount; m++) {
                CTTbl oTab = oTabs[m];
                lastId++;
                lastId = SetId(oTab, lastId);
            }
            for (int m = 0; m < tCount; m++) {
                CTTbl oTab = oTabs[m];
                lastId = ProcessTable(oTab, new int[] {0, 0}, 0, lastId);
            }

            for (int nl = 5; nl > 0; nl--) {
            	List<XmlObject> list = toDelete.get(nl);
            	if (list != null) {
            		for (XmlObject delRng : list) {
                        try {
                        	//if (nl == getNestingLevel(delRng)) {
                        	//	CTRow row = getRow(delRng, 1);
                        		
                        		XmlCursor c = delRng.newCursor();
                        		c.removeXml();
                        	//}
                        } catch (Exception ex) {
                            System.out.println("Exception key");
                        }
            		}
            	}
            }

            for (Integer key : idToRange.keySet()) {
                XmlObjectRange oFld = idToRange.get(key);

                try {
                    setValue(oFld, "");
                } catch (Exception ex) {
                    System.out.println("Exception key: " + key);
                }
            }
            idToRange.clear();
            idToRange = null;

            //@todo Word MACROS call
            //if (macros.length() > 0) {
            //    Dispatch.call(word, "Run", macros);
            //}

            FileOutputStream os = new FileOutputStream(fileName);
            oDoc.write(os);
            os.close();

            if (showAfterComplete) {
                Runtime ru = Runtime.getRuntime();
                ru.exec("cmd /c \"" + fileName + "\"");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (taskTable != null) {
            	taskTable.setProgressCaption("");
            	taskTable.setProgressValue(0);
            }
        }
    }
    
    private String formatText(String value, int fmt) {
    	if (fmt == Kernel.IC_FLOAT) {
    		char separator = defaultSeparator;
    		char antisepar = (separator == '.') ? ',' : '.';
    		value = value.replace(antisepar, separator);
    	}
        return value;
    }
    
    private static List<XmlObjectRange> getWordComments(XmlObject parent) {
	    String nsText = "declare namespace w = 'http://schemas.openxmlformats.org/wordprocessingml/2006/main'; ";
	    String pathText = ".//w:fldChar | .//w:instrText";
	    String queryText = nsText + pathText;
	    String pathText2 = ".//w:fldSimple";
	    String queryText2 = nsText + pathText2;
	
	    XmlObject[] xos = parent.selectPath(queryText);
	
	    String com = "";
	
	    List<XmlObjectRange> list = new ArrayList<XmlObjectRange>();
	
	    boolean separateRiched = false;
	    boolean endRiched = false;
	    XmlObject oFld = null;
	
	    for (XmlObject xo : xos) {
	        if (xo instanceof CTFldChar) {
	            CTFldChar o = (CTFldChar)xo;
	            if (STFldCharType.BEGIN.equals(o.getFldCharType())) {
	                com = "";
	                separateRiched = false;
	                endRiched = false;
	                oFld = xo;
	
	            } else if (STFldCharType.SEPARATE.equals(o.getFldCharType())) {
	                separateRiched = true;
	            } else if (STFldCharType.END.equals(o.getFldCharType())) {
	                endRiched = true;
	            }
	        } else if (xo instanceof CTText && !separateRiched) {
	            CTText t = (CTText)xo;
	            com += t.getStringValue();
	        }
	
	        if (endRiched) {
	            int beg = com.indexOf("|");
	            int end = com.lastIndexOf("\\*");
	            if (beg > 0 && end > beg) {
	                com = com.substring(beg + 1, end).trim();
	                
	                beg = com.indexOf("|");
	                end = com.indexOf("|", beg + 1);
	                
	                if (beg > 0 && end > beg) {
                        int id = Integer.parseInt(com.substring(beg + 1, end));
                        list.add(new XmlObjectRange(id, oFld, parent, com));
		            } else {
		            	log.error("Неверный комментарий: " + com);
	                }
	            } else {
	            	log.error("Неверный комментарий: " + com);
	            }
	        }
	    }
	
	    xos = parent.selectPath(queryText2);
	    for (XmlObject xo : xos) {
	        if (xo instanceof CTSimpleField) {
	            CTSimpleField o = (CTSimpleField)xo;
	            com = o.getInstr();
	
	            int beg = com.indexOf("|");
	            int end = com.lastIndexOf("\\*");
	            if (beg > 0 && end > beg) {
	                com = com.substring(beg + 1, end).trim();

	                beg = com.indexOf("|");
	                end = com.indexOf("|", beg + 1);
	                
	                if (beg > 0 && end > beg) {
                        int id = Integer.parseInt(com.substring(beg + 1, end));
                        list.add(new XmlObjectRange(id, xo, parent, com));
		            } else {
		            	log.error("Неверный комментарий: " + com);
	                }
	            } else {
	            	log.error("Неверный комментарий: " + com);
	            }
	        }
	    }
	    return list;
    }
    
    private XmlObjectRange findWordComment(XmlObject parent, int id) {
	    String wNsc = "http://schemas.openxmlformats.org/wordprocessingml/2006/main";
        String nsText = "declare namespace w = '" + wNsc + "'; ";
        String pathText = ".//w:fldChar | .//w:instrText";
        String queryText = nsText + pathText;
        String pathText2 = ".//w:fldSimple";
        String queryText2 = nsText + pathText2;

        XmlObject[] xos = parent.selectPath(queryText);
        
        String str = "";

        boolean separateRiched = false;
        boolean endRiched = false;
        XmlObject x = null;

        for (XmlObject xo : xos) {
            if (xo instanceof CTFldChar) {
                CTFldChar o = (CTFldChar)xo;
                if (STFldCharType.BEGIN.equals(o.getFldCharType())) {
                    str = "";
                    separateRiched = false;
                    endRiched = false;

                    x = xo;

                } else if (STFldCharType.SEPARATE.equals(o.getFldCharType())) {
                    separateRiched = true;
                } else if (STFldCharType.END.equals(o.getFldCharType())) {
                    endRiched = true;
                }
            } else if (xo instanceof CTText && !separateRiched) {
                CTText t = (CTText)xo;
                str += t.getStringValue();
            }

            if (endRiched) {
                int beg = str.indexOf("|");
                int end = str.lastIndexOf("\\*");
                if (beg > 0 && end > beg) {
                    str = str.substring(beg + 1, end).trim();

                    int mid = str.indexOf('|');
                    if (mid > -1) {
                        String type = str.substring(0, mid);
                        if ("User".equals(type) || "Department".equals(type) || "Base".equals(type)) {
                            String result = str.substring(mid + 1);
                            int idc = Integer.parseInt(result);
                            
                            if (idc == id)
                            	return new XmlObjectRange(id, x, parent, str);
                        } else {
                            end = str.indexOf('|', mid + 1);
                            if (end > -1) {
                                String result = str.substring(mid + 1, end);
                                int idc = Integer.parseInt(result);

                                if (idc == id)
                                	return new XmlObjectRange(id, x, parent, str);
                            }
                        }
                    }
                }
            }
        }

        xos = parent.selectPath(queryText2);
        for (XmlObject xo : xos) {
            if (xo instanceof CTSimpleField) {
                CTSimpleField o = (CTSimpleField)xo;
                str = o.getInstr();
                x = xo;

                int beg = str.indexOf("|");
                int end = str.lastIndexOf("\\*");
                if (beg > 0 && end > beg) {
                    str = str.substring(beg + 1, end).trim();

                    int mid = str.indexOf('|');
                    if (mid > -1) {
                        String type = str.substring(0, mid);
                        if ("User".equals(type) || "Department".equals(type) || "Base".equals(type)) {
                            String result = str.substring(mid + 1);
                            int idc = Integer.parseInt(result);
                            if (idc == id)
                            	return new XmlObjectRange(id, x, parent, str);
                        } else {
                            end = str.indexOf('|', mid + 1);
                            if (end > -1) {
                                String result = str.substring(mid + 1, end);
                                int idc = Integer.parseInt(result);
                                if (idc == id)
                                	return new XmlObjectRange(id, x, parent, str);
                            }
                        }
                    }
                }
            }
        }
        
        return null;
    }

    private void findWordComments(XmlObject parent) {
        String wNsc = "http://schemas.openxmlformats.org/wordprocessingml/2006/main";
        String nsText = "declare namespace w = '" + wNsc + "'; ";
        String pathText = ".//w:fldChar | .//w:instrText";
        String queryText = nsText + pathText;
        String pathText2 = ".//w:fldSimple";
        String queryText2 = nsText + pathText2;

        XmlObject[] xos = parent.selectPath(queryText);
        
        String str = "";

        boolean separateRiched = false;
        boolean endRiched = false;
        XmlObject x = null;

        for (XmlObject xo : xos) {
            if (xo instanceof CTFldChar) {
                CTFldChar o = (CTFldChar)xo;
                if (STFldCharType.BEGIN.equals(o.getFldCharType())) {
                    str = "";
                    separateRiched = false;
                    endRiched = false;

                    x = xo;

                } else if (STFldCharType.SEPARATE.equals(o.getFldCharType())) {
                    separateRiched = true;
                } else if (STFldCharType.END.equals(o.getFldCharType())) {
                    endRiched = true;
                }
            } else if (xo instanceof CTText && !separateRiched) {
                CTText t = (CTText)xo;
                str += t.getStringValue();
            }

            if (endRiched) {
                int beg = str.indexOf("|");
                int end = str.lastIndexOf("\\*");
                if (beg > 0 && end > beg) {
                    str = str.substring(beg + 1, end).trim();

                    int mid = str.indexOf('|');
                    if (mid > -1) {
                        String type = str.substring(0, mid);
                        if ("User".equals(type) || "Department".equals(type) || "Base".equals(type)) {
                            String result = str.substring(mid + 1);
                            int id = Integer.parseInt(result);
                        	idToRange.put(id, new XmlObjectRange(id, x, parent, str));
                            continue;
                        } else {
                            end = str.indexOf('|', mid + 1);
                            if (end > -1) {
                                String result = str.substring(mid + 1, end);
                                int id = Integer.parseInt(result);
                                if (id != 0) {
                                	idToRange.put(id, new XmlObjectRange(id, x, parent, str));
                                }
                            }
                        }
                    }
                }
            }
        }

        xos = parent.selectPath(queryText2);
        for (XmlObject xo : xos) {
            if (xo instanceof CTSimpleField) {
                CTSimpleField o = (CTSimpleField)xo;
                str = o.getInstr();
                x = xo;

                int beg = str.indexOf("|");
                int end = str.lastIndexOf("\\*");
                if (beg > 0 && end > beg) {
                    str = str.substring(beg + 1, end).trim();

                    int mid = str.indexOf('|');
                    if (mid > -1) {
                        String type = str.substring(0, mid);
                        if ("User".equals(type) || "Department".equals(type) || "Base".equals(type)) {
                            String result = str.substring(mid + 1);
                            int id = Integer.parseInt(result);
                        	idToRange.put(id, new XmlObjectRange(id, x, parent, str));
                            continue;
                        } else {
                            end = str.indexOf('|', mid + 1);
                            if (end > -1) {
                                String result = str.substring(mid + 1, end);
                                int id = Integer.parseInt(result);
                                if (id != 0) {
                                	idToRange.put(id, new XmlObjectRange(id, x, parent, str));
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void processSingleComments(XWPFDocument oDoc, Element xml) throws XmlException {
        for (int i = xml.getChildren().size() - 1; i>=0; i--) {
            Element pNode = (Element) xml.getChildren().get(i);
            List pChildNodes = pNode.getChildren();

            int childCount = pChildNodes.size();

            Element pChildNode = pNode;

            List pChildAttr = pNode.getAttributes();
            Attribute pChildItem = (Attribute) pChildAttr.get(0);
            String value = pChildItem.getValue();
            String tfmt = pNode.getAttributeValue("type");
            int fmt = (tfmt != null && tfmt.length() > 0) ? Integer.parseInt(tfmt) : 0;

            String name = pNode.getName();

            int id = Integer.parseInt(value);
            if ("User".equals(name) || "Department".equals(name) || "Base".equals(name)) {
                XmlObjectRange oFld = idToRange.get(id);
                if (oFld == null) continue;

                value = pNode.getAttributeValue("str");
                value = formatText(value, fmt);
                
                setValue(oFld, value);

                if (taskTable != null) {
                	taskTable.setProgressValue(++currentValue);
                }

                idToRange.remove(id);
                continue;
            }
            else if ("TreeColumn".equals(name)) {
                XmlObjectRange oFld = idToRange.get(id);
                if (oFld == null) continue;

                ProcessTreeColumn(0, childCount, pChildNodes, oFld);

                setValue(oFld, "");
                idToRange.remove(id);
                continue;
            }

            XmlObjectRange oFld = idToRange.get(id);
            if (oFld == null) continue;

            if (childCount == 0) {
                int length = pChildAttr.size();

                if (length > 1) {
                    pChildItem = (Attribute) pChildAttr.get(1);

                    String bName = pChildItem.getName();
                    value = pChildItem.getValue();

                    idToRange.remove(id);

                    if ("str".equals(bName)) {
                        if (value.length() == 0) {
                            //@todo Empty value - remove one space before
                            //int tt = Dispatch.call(oSel, "MoveLeft", new Variant(1), new Variant(1), new Variant(1)).getInt();
                            setValue(oFld, "");
                        } else {
                            value = formatText(value, fmt);
                            setValue(oFld, value);
                        }
                        if (taskTable != null) {
                        	taskTable.setProgressValue(++currentValue);
                        }

                    } else if ("src".equals(bName)) {
                        if (value.length() > 0) {
                        	try {
                            	String imgPos = pNode.getAttributeValue("imgPos");
                        		setImage(oDoc, oFld, value, imgPos);
                        	} catch (Exception e) {
                        		e.printStackTrace();
                        	}
                        } else {
                            setValue(oFld, "");
                        }

                        if (taskTable != null) {
                        	taskTable.setProgressValue(++currentValue);
                        }
                    }
                } else {
                    setValue(oFld, "");
                    idToRange.remove(id);
                }
            } else {
                boolean info = isInTable(oFld.getObject());

                if (info) { // Если в таблице
                    ArrayList<String[]> allVals = new ArrayList<String[]>();
                    int[] childCounts = new int[1];
                    int attrsCount;
                    int valueChildCount;

                    List pValueChildNodes;
                    Element pValueChildNode;
                    Attribute pValueChildItem;
                    List pValueChildAtr;
                    if (childCount > 0) {
                        pChildNode = (Element) pChildNodes.get(0);
                        pChildAttr = pChildNode.getAttributes();

                        attrsCount = pChildAttr.size();

                        if (attrsCount > 0) {
                            String[] valls = new String[childCount];
                            allVals.add(valls);
                            childCounts = new int[1];
                            childCounts[0] = childCount;
                        } else {
                            String[] valls = new String[childCount];
                            allVals.add(valls);
                            childCounts = new int[childCount + 1];
                            childCounts[0] = childCount;
                        }
                    }

                    for (int j = 0; j < childCount; j++) {
                        pChildNode = (Element) pChildNodes.get(j);
                        pChildAttr = pChildNode.getAttributes();
                        attrsCount = pChildAttr.size();

                        if (attrsCount > 0) {
                            pChildItem = (Attribute) pChildAttr.get(0);
                            value = pChildItem.getValue();
                            String bName = pChildItem.getName();
                            String tcfmt = pChildNode.getAttributeValue("type");
                            int cfmt = (tcfmt != null && tcfmt.length() > 0) ? Integer.parseInt(tcfmt) : 0;
                            allVals.get(0)[j] = value;
                            if ("str".equals(bName)) {
                                types.put(id, cfmt);
                            }
                            if ("src".equals(bName)) {
                                types.put(id, 1);
                            }
                        } else {
                            pValueChildNodes = pChildNode.getChildren();
                            valueChildCount = pValueChildNodes.size();

                            String[] valls;
                            if (valueChildCount == 0) {
                                valls = new String[1];
                                valls[0] = "@deleteRow()";
                            } else
                                valls = new String[valueChildCount];

                            for (int vj = 0; vj < valueChildCount; vj++) {
                                pValueChildNode = (Element) pValueChildNodes.get(vj);
                                pValueChildAtr = pValueChildNode.getAttributes();
                                pValueChildItem = (Attribute) pValueChildAtr.get(0);
                                value = pValueChildItem.getValue();

                                String bName = pValueChildItem.getName();
                                String tcfmt = pValueChildNode.getAttributeValue("type");
                                int cfmt = (tcfmt != null && tcfmt.length() > 0) ? Integer.parseInt(tcfmt) : 0;

                                valls[vj] = value;
                                if ("str".equals(bName)) {
                                    types.put(id, cfmt);
                                }
                                if ("src".equals(bName)) {
                                    types.put(id, 1);
                                }
                            }
                            allVals.add(valls);
                            childCounts[j + 1] = valueChildCount;
                        }
                    }
                    vals.put(id, allVals);
                    lens.put(id, childCounts);
                } else {
                    String str2 = "";
                    for (int j = 0; j < childCount; j++) {
                        pChildNode = (Element) pChildNodes.get(j);

                        pChildAttr = pChildNode.getAttributes();
                        pChildItem = (Attribute) pChildAttr.get(0);
                        value = pChildItem.getValue();

                        str2 += value;
                    }

                    if (childCount == 0) {
                        str2 = pChildNode.getAttributeValue("value");
                    } else str2 = str2.substring(2);

                    setValue(oFld, str2);
                    idToRange.remove(id);
                }
            }
        }
    }

    private void processHeader(XWPFDocument oDoc, XWPFHeaderFooter h, Element xml, PackagePart pp) throws IOException, XmlException {
    	CTHdrFtr header = h._getHdrFtr();
    	if (header == null || pp == null) return;
    	
    	findWordComments(header);
    	processSingleComments(oDoc, xml);
        
        OutputStream outputStream = pp.getOutputStream();
        XmlOptions xmlOptions = commit(h);

        header.save(outputStream, xmlOptions);
        outputStream.close();

    }
    
    private XmlOptions commit(XWPFHeaderFooter wrapper) {
    	
        XmlOptions xmlOptions = new XmlOptions(DEFAULT_XML_OPTIONS);
	    Map<String, String> map = new HashMap<String, String>();
	    map.put("http://schemas.openxmlformats.org/officeDocument/2006/math", "m");
	    map.put("urn:schemas-microsoft-com:office:office", "o");
	    map.put("http://schemas.openxmlformats.org/officeDocument/2006/relationships", "r");
	    map.put("urn:schemas-microsoft-com:vml", "v");
	    map.put("http://schemas.openxmlformats.org/markup-compatibility/2006", "ve");
	    map.put("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "w");
	    map.put("urn:schemas-microsoft-com:office:word", "w10");
	    map.put("http://schemas.microsoft.com/office/word/2006/wordml", "wne");
	    map.put("http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing", "wp");
	    xmlOptions.setSaveSuggestedPrefixes(map);
        return xmlOptions;
  }

    private int ProcessTable(CTTbl oTab, int[] index, int level, int lastId) {
        int tableLevel = getNestingLevel(oTab);
        Map<Integer, Boolean> tMap = new TreeMap<Integer, Boolean>();
        
        List<XmlObjectRange> list = getWordComments(oTab);
        
        boolean isTableInTable = false;

        for (XmlObjectRange xo : list) {
            String str = xo.getComment();

            if (str.indexOf('@') > -1)
                isTableInTable = true;
            else
                isTableInTable = false;

            int start = str.indexOf('|');
            if (start == -1) continue;
            int end = str.indexOf('|', start + 1);
            if (end == -1) continue;
            String result = str.substring(start + 1, end);
            int id = Integer.parseInt(result);
            if (id == 0) continue;
            start = str.indexOf("|tableId=");
            int intTableId = 0;
            if (start > -1) {
                end = str.indexOf('|', start + 1);
                String tableId = str.substring(start + 9, end);
                intTableId = Integer.parseInt(tableId);
            } else {
                start = str.indexOf("| tableId=");
                if (start > -1) {
                    end = str.indexOf('|', start + 1);
                    String tableId = str.substring(start + 10, end);
                    intTableId = Integer.parseInt(tableId);
                }
            }
            if (intTableId == 0) continue;
            
            int nestingLevel = getNestingLevel(xo.getObject());
            if (nestingLevel == -1) continue;
            else if (!isTableInTable && nestingLevel - tableLevel > 1) {
            	CTRow[] trs = oTab.getTrArray();
                int trsCount = trs.length;
                for (int j=0; j<trsCount; j++) {
                    CTTc[] rowCells = trs[j].getTcArray();

                    int rowCellsCount = rowCells.length;

                    for (int cm = 0; cm < rowCellsCount; cm++) {
                        CTTc rowCell = rowCells[cm];

                        CTTbl[] oTabs = rowCell.getTblArray();
                        int oTabsCount = oTabs.length;

                        for (int m = 0; m < oTabsCount; m++) {
                            CTTbl oChTab = oTabs[m];
                            
                            lastId = ProcessTable(oChTab, new int[] {j, 0}, level + 1, lastId);
                        }
                    }
                }
                continue;
            }

            int[] childCounts = lens.get(id);
            int childCount = 0;

            if (childCounts != null && index[0] < childCounts.length)
            	childCount = childCounts[index[0]];

            if (childCounts != null && !isTableInTable && lenChanged.get(id) == null) {
            	childCounts[0] = 0;
            	lenChanged.put(id, true);
            }
            
            if (childCounts == null || index[0] >= childCounts.length) continue;
            //childCount = childCounts[index[0]];

            CTTbl oT = oTab;
            int base = getRow(oTab, str);
            if (base == -1) continue;
            
            CTRow[] trows = oTab.getTrArray();
            CTRow selRow = trows[base];

            String oS = GetId(oT);
            
            if (oS == null || oS.length() == 0) {
                lastId++;
                oS = String.valueOf(lastId);
                lastId = SetId(oT, lastId);
            }

            int oId = Integer.parseInt(oS);

            int compTid = intTableId * 100 + oId;

            if (!tMap.containsKey(compTid) && level < 2) {
                tMap.put(compTid, true);

                if (childCount > 1) {
                    XmlCursor c = selRow.newCursor();
                    for (int k=0; k<childCount-1; k++)
                        c.copyXml(c);
                    
                    c.dispose();
                }

                for (int j = 0; j < childCount; j++) {
                    CTRow curRow = oTab.getTrArray(base + j);

                    CTTc[] rowCells = curRow.getTcArray();

                    int rowCellsCount = rowCells.length;

                    for (int cm = 0; cm < rowCellsCount; cm++) {
                        CTTc rowCell = rowCells[cm];

                        CTTbl[] oTabs = rowCell.getTblArray();
                        int oTabsCount = oTabs.length;

                        for (int m = 0; m < oTabsCount; m++) {
                            CTTbl oChTab = oTabs[m];
                            lastId = ProcessTable(oChTab, new int[] {level == 0 ? (j + 1) : index[0], level == 1 ? j : index[1]}, level + 1, lastId);
                        }
                    }
                }
            } 
            childCounts[index[0]] = 0;
            lens.put(id, childCounts);
            if (idToRange.containsKey(id)) idToRange.remove(id);
        }
        
        list = getWordComments(oTab);

        for (XmlObjectRange xo : list) {
            int nestingLevel = getNestingLevel(xo.getObject());
            String str = xo.getComment();
            
            boolean isTableInTable2 = (str.indexOf('@') > -1);
            
            if (!isTableInTable && nestingLevel - tableLevel > 1) continue;
            
            int start = str.indexOf('|');
            if (start == -1) continue;
            if (str.substring(0, start).equals("Field")) continue;
            
            int end = str.indexOf('|', start + 1);
            if (end == -1) continue;
            String result = str.substring(start + 1, end);
            int id = Integer.parseInt(result);
            if (id == 0) continue;

            ArrayList<String[]> allVals;
            int[] numbers;

            String[] valls;
            int number = index[1];
            int type = -1;
            String v = "";
            
            allVals = vals.get(id);
            numbers = lens.get(id);

            if (allVals != null && (index[0] < allVals.size() || !isTableInTable2) && numbers != null) {
            	valls = allVals.get(isTableInTable2 ? index[0] : 0);
            	if (level == 0) {
            		number = numbers[index[0]];
            		numbers[index[0]] = number + 1;
            		lens.put(id, numbers);
            	} else if (!isTableInTable2) {
            		number = numbers[0];
            		numbers[0] = number + 1;
            		lens.put(id, numbers);
            	}
	            v = (number < valls.length) ? valls[number] : "";
            } 
            
            if (types.containsKey(id)) {
            	type = types.get(id);
            }
            idToRange.remove(id);

            if (type == -1 || "@deleteRow()".equals(v)) {
                nestingLevel = getNestingLevel(xo.getObject());
                CTRow row = getRow(xo.getObject(), 1);
                List<XmlObject> rows = toDelete.get(nestingLevel);
                if (rows == null || !rows.contains(row))
                	toDelete.put(nestingLevel, row);
            } else if (type == 0) {
            	try {
            		setValue(xo, v);
            	} catch (XmlException e) {
            		log.error(e, e);
            	}
            } else if (type > 1) {
            	v = formatText(v, type);
            	try {
            		setValue(xo, v);
            	} catch (XmlException e) {
            		log.error(e, e);
            	}
            } else {
//                Dispatch oShps = Dispatch.get(oSel, "InlineShapes").toDispatch();
//                Dispatch.call(oShps, "AddPicture", v, new Variant(false), new Variant(true)).toDispatch();
            }
            if (taskTable != null) {
            	taskTable.setProgressValue(++currentValue);
            }
        }
        return lastId;
    }

    private String GetId(CTTbl oTab) {
        CTRow[] rows = oTab.getTrArray();
        if (rows.length > 0) {
        	return tblIds.get(new String(rows[0].getRsidR()));
        } else {
        	return tblIds.get("0");
        }
    }    
    
    private int SetId(CTTbl oTab, int lastId) {
        String str = String.valueOf(lastId);
        
        CTRow[] rows = oTab.getTrArray();
        if (rows.length > 0) {
        	tblIds.put(new String(rows[0].getRsidR()), str);
        } else {
        	tblIds.put("0", str);
        }
        
        String nsText = "declare namespace w = 'http://schemas.openxmlformats.org/wordprocessingml/2006/main'; ";
        String pathText = ".//w:tbl";
        String queryText = nsText + pathText;

        XmlObject[] xos = oTab.selectPath(queryText);

        int count = xos.length;

        for (int i = 0; i < count; i++) {
        	CTTbl chTab = (CTTbl) xos[i];
            lastId++;
            lastId = SetId(chTab, lastId);
        }
        return lastId;
    }

    private int getNestingLevel(XmlObject xo) {
    	try {
	        XmlCursor c = xo.newCursor();
	        int count = 0;
	        try {
	            while (c.toParent() && c.getName() != null) {
	                if (c.getObject() instanceof CTTbl)
	                	++count;
	            }
	        } finally {
	            c.dispose();
	        }
	        return count;
    	} catch (Throwable e) {}
        return -1;
    }

    private static int getRow(CTTbl oTab, String str) {
        CTRow[] trows = oTab.getTrArray();
        
        for (int i = 0; i < trows.length; i++) {
        	List<XmlObjectRange> list = getWordComments(trows[i]);

            for (XmlObjectRange xo : list) {
                String str2 = xo.getComment();
                if (str2.equals(str)) return i;
            }
        }
        return -1;
    }

    private static CTRow getRow(XmlObject xo, int level) {
        XmlCursor c = xo.newCursor();
        int count = 0;
        try {
            while (c.toParent() && c.getName() != null) {
                if (c.getObject() instanceof CTRow && ++count == level)
                    return (CTRow)c.getObject();
            }
        } finally {
            c.dispose();
        }
        return null;
    }

    private static boolean isInTable(XmlObject xo) {
        XmlCursor c = xo.newCursor();
        try {
            while (c.toParent() && c.getName() != null) {
                if ("tbl".equalsIgnoreCase(c.getName().getLocalPart()))
                    return true;
            }
        } finally {
            c.dispose();
        }
        return false;
    }

    private static Fragment parseRichTextValue(String rich) {
        rich = rich.replaceAll("\r", "");
        rich = rich.replaceAll("\n", "<br/>");
    	
		Pattern pattern7 = Pattern.compile("(<span[^>]*>|<div[^>]*>|<br>|<br\\/>|<\\/div>|<\\/span>|<b>|<\\/b>|<i>|<\\/i>|<u>|<\\/u>"
				+ "|\\[b\\]|\\[\\/b\\]|\\[i\\]|\\[\\/i\\]|\\[u\\]|\\[\\/u\\]"
				+ "|<blockquote[^>]*>|</blockquote>|<ol[^>]*>|<li[^>]*>|<img[^>]*>|<a[^>]*>|</ol>|</li>|</img>|</a>)");
		Pattern pattern8 = Pattern.compile("font-size:\\s*(\\d*?)pt;");
		Pattern pattern9 = Pattern.compile("align=\"(\\w*?)\"");
		Pattern pattern10 = Pattern.compile("[^\\-]color:\\s*rgb\\(([\\d\\,\\s]*?)\\);");

		Matcher matcher7 = pattern7.matcher(rich);
        
        Fragment lastFragment = new Fragment();
        int lastPos = 0;

        while (matcher7.find()) {
        	if (matcher7.start() > lastPos) {
            	Fragment f = new Fragment();
        		f.setText(rich.substring(lastPos, matcher7.start()));
        		lastFragment.addChild(f);
        	}
        	
            String var = matcher7.group(1);

            if (var.endsWith("\"/>")) {
            	
            } else if ("<br>".equals(var) || "<br/>".equals(var)) {
            	Fragment f = new Fragment();
            	f.setNewLine(true);
        		lastFragment.addChild(f);
            } else if (var.startsWith("<b>") || var.startsWith("[b]")) {
            	Fragment f = new Fragment();
            	f.setBold(true);
        		lastFragment.addChild(f);
        		lastFragment = f;
            } else if (var.startsWith("<i>") || var.startsWith("[i]")) {
            	Fragment f = new Fragment();
            	f.setItalic(true);
        		lastFragment.addChild(f);
        		lastFragment = f;
            } else if (var.startsWith("<u>") || var.startsWith("[u]")) {
            	Fragment f = new Fragment();
            	f.setUnderline(true);
        		lastFragment.addChild(f);
        		lastFragment = f;
            } else if (var.startsWith("<span ")) {
            	Fragment f = new Fragment();
            	
            	Matcher matcher8 = pattern8.matcher(var);
            	Matcher matcher10 = pattern10.matcher(var);
            	
            	if (matcher8.find())
            		f.setFontSize(Integer.parseInt(matcher8.group(1)));
            	else if (matcher10.find())
            		f.setColor(matcher10.group(1));
            	
        		lastFragment.addChild(f);
        		lastFragment = f;
            } else if (var.startsWith("<div ")) {
            	Fragment f = new Fragment();
            	
            	Matcher matcher9 = pattern9.matcher(var);
            	if (matcher9.find())
            		f.setAlign(matcher9.group(1));
            	
        		lastFragment.addChild(f);
        		lastFragment = f;
            } else if (var.startsWith("<blockquote") || var.startsWith("<ol") || var.startsWith("<li")
            		 || var.startsWith("<img") || var.startsWith("<a ")) {
            	Fragment f = new Fragment();
        		lastFragment.addChild(f);
        		lastFragment = f;
            } else if ("</span>".equals(var) || "</div>".equals(var) || "</b>".equals(var)
            		 || "</i>".equals(var) || "</u>".equals(var) || "[/b]".equals(var) || "[/i]".equals(var) || "[/u]".equals(var)
            		 || "</blockquote>".equals(var) || "</ol>".equals(var) || "</li>".equals(var)
            		 || "</img>".equals(var) || "</a>".equals(var)) {
            	lastFragment = lastFragment.getParent();
            }

            lastPos = matcher7.end();
        }
    	if (rich.length() > lastPos) {
        	Fragment f = new Fragment();
    		f.setText(rich.substring(lastPos, rich.length()));
    		lastFragment.addChild(f);
    	}
    	
    	return lastFragment;
    }
    
    private int getIndexOfChildInP(CTP p, Node r) {
    	int index = 0;
		NodeList children = p.getDomNode().getChildNodes();
		for (int i=0; i<children.getLength(); i++) {
			if (r.equals(children.item(i))) {
				return index;
			}
			index++;
		}
		return index;
    }

    private void removeFldinP(CTP p, CTSimpleField r) {
    	CTSimpleField[] rs = p.getFldSimpleArray();
		for (int i=0; i<rs.length; i++) {
			if (r.equals(rs[i])) {
				p.removeFldSimple(i);
				return;
			}
		}
    }

    private CTR addNewR(CTP p, int indexR) {
		NodeList children = p.getDomNode().getChildNodes();
    	Node currR = (children.getLength() > indexR) ? children.item(indexR) : null;
    	CTR newr = p.addNewR();
    	
    	Node inserted = p.getDomNode().insertBefore(newr.getDomNode(), currR);

		CTR[] rs = p.getRArray();
		for (CTR r : rs) {
			if (r.getDomNode().equals(inserted))
				return r;
		}
    	
    	return null;
    }

    private CTP addNewP(XmlObject m, CTP lastP, int indexR) {
    	int index = getIndexOfP(m, lastP);
    	CTP p = null;
		Node parentNode = null;
    	
    	if (m instanceof CTBodyImpl) {
    		p = ((CTBodyImpl) m).insertNewP(index);
    		parentNode = ((CTBodyImpl) m).getDomNode();
    	}
    	else if (m instanceof CTHdrFtrImpl) {
    		p = ((CTHdrFtrImpl) m).insertNewP(index);
    		parentNode = ((CTHdrFtrImpl) m).getDomNode();
    	}
    	else if (m instanceof CTTcImpl) {
    		p = ((CTTcImpl) m).insertNewP(index);
    		parentNode = ((CTTcImpl) m).getDomNode();
    	}
    	
    	Node afterLastP = lastP.getDomNode().getNextSibling();
    	parentNode.insertBefore(p.getDomNode(), afterLastP);
    	
    	CTP[] ps = getPArray(m);
    	lastP = ps[index];
    	p = ps[index + 1];

		NodeList children = lastP.getDomNode().getChildNodes();
		List<Node> toMove = new ArrayList<>();
		
		for (int i=indexR; i<children.getLength(); i++)
			toMove.add(children.item(i));
		for (Node child : toMove)
			p.getDomNode().appendChild(child);

    	return p;
    }

    private CTP[] getPArray(XmlObject m) {
    	if (m instanceof CTBodyImpl)
    		return ((CTBodyImpl) m).getPArray();
    	else if (m instanceof CTHdrFtrImpl)
    		return ((CTHdrFtrImpl) m).getPArray();
    	else if (m instanceof CTTcImpl)
    		return ((CTTcImpl) m).getPArray();
    	return new CTP[0];
    }
    
    private int getIndexOfP(XmlObject m, CTP p) {
    	CTP[] ps = getPArray(m);
		for (int i=0; i<ps.length; i++) {
			if (p.equals(ps[i])) {
				return i;
			}
		}
		return -1;
    }

    private void removeP(XmlObject m, int index) {
    	if (m instanceof CTBodyImpl)
    		((CTBodyImpl) m).removeP(index);
    	else if (m instanceof CTHdrFtrImpl)
    		((CTHdrFtrImpl) m).removeP(index);
    	else if (m instanceof CTTcImpl)
    		((CTTcImpl) m).removeP(index);
    }

    private boolean removeFldChar(CTP p, int indexR) {
    	boolean foundEnd = false;
    	int endIndex = indexR;
    	
		NodeList children = p.getDomNode().getChildNodes();
		for (int i=indexR; i<children.getLength() && !foundEnd; i++) {
			endIndex = i;
			Node child = children.item(i);
			if ("r".equals(child.getLocalName())) {
					for (int j = 0; j < child.getChildNodes().getLength(); j++) {
						Node fldChar = child.getChildNodes().item(j);
						if ("fldChar".equals(fldChar.getLocalName())) {
							Node attr = fldChar.getAttributes().getNamedItemNS(fldChar.getNamespaceURI(), "fldCharType");
							if ("end".equals(attr.getNodeValue()))
								foundEnd = true;
						}
					}
			}
		}
		if (children.getLength() > 0) {
			for (int i=endIndex; i>=indexR; i--)
				p.getDomNode().removeChild(p.getDomNode().getChildNodes().item(i));
		}

        return foundEnd;
    }
    
    private boolean removeFldChar(XmlObject m, CTP p, int indexR) {
    	// Ищем окончание комментария в текущем параграфе (p) и удаляем все элементы комментария
    	boolean foundEnd = removeFldChar(p, indexR);
    	int endIndex = -1;
        
    	// если комментарий переходит на следующий параграф ищем дальше по параграфам
        CTP[] ps = getPArray(m);
        int indexP = getIndexOfP(m, p);

        for (int mi = indexP + 1; mi < ps.length && !foundEnd; mi++) {
        	endIndex = mi;
        	CTP nextP = ps[mi];
        	foundEnd = removeFldChar(nextP, 0);
        }

        // в последнем параграфе остатки переносим в предыдущий
        if (endIndex > -1) {
    		NodeList children = ps[endIndex].getDomNode().getChildNodes();
    		List<Node> toMove = new ArrayList<>();
    		
    		for (int i=0; i<children.getLength(); i++)
    			if (!"pPr".equals(children.item(i).getLocalName()))
    				toMove.add(children.item(i));
    		
    		for (Node child : toMove)
    			p.getDomNode().appendChild(child);

        }
        	
        // удаляем пустые параграфы
        for (int i=endIndex; i>indexP; i--)
        	removeP(m, i);
        
        return foundEnd;
	}

    private void setValue(XmlObjectRange xor, String value) throws XmlException {
    	if (value == null) value = "";

    	XmlObject xo = xor.getObject();
    	
    	XmlCursor c = null;
    	try {
    		c = xo.newCursor();
    	} catch (XmlValueDisconnectedException e) {
    		xor = findWordComment(xor.getParent(), xor.getId());
    		xo = xor.getObject();
    		c = xo.newCursor();
    	}

        Fragment rootFrag = parseRichTextValue(value);
        rootFrag.normalize();
        
        if (xo instanceof CTFldChar) {
        	c.toParent();
        	CTR r = (CTR)c.getObject();
        	c.toParent();
        	CTP p = (CTP)c.getObject();
        	c.toParent();
        	XmlObject m = c.getObject();
        	
            PAttrs pAttrs = new PAttrs();
            pAttrs.readAttrs(p);

            RAttrs rAttrs = new RAttrs();
            rAttrs.readAttrs(r);
        	
            // порядковый номер строки в параграфе
            int indexR = getIndexOfChildInP(p, r.getDomNode());
        	// удаляем комментарий из документа
        	removeFldChar(m, p, indexR);
        	// вставляем на его место текстовое значение
        	processFragments(rootFrag, m, indexR, pAttrs, rAttrs);
        } else if (xo instanceof CTSimpleField) {
        	CTSimpleField o = (CTSimpleField)xo;
        	c.toParent();
        	CTP p = (CTP)c.getObject();
        	c.toParent();
        	XmlObject m = c.getObject();

            PAttrs pAttrs = new PAttrs();
            pAttrs.readAttrs(p);

            CTR[] rs = o.getRArray();
            RAttrs rAttrs = new RAttrs();

            if (rs != null && rs.length > 0) {
                rAttrs.readAttrs(rs[0]);
            } else {
            	CTR r = o.addNewR();
                rAttrs.readAttrs(r);
            }
            
            // порядковый номер строки в параграфе
            int indexR = getIndexOfChildInP(p, o.getDomNode());
        	// удаляем комментарий из документа
            removeFldinP(p, o);
        	// вставляем на его место текстовое значение
        	processFragments(rootFrag, m, indexR, pAttrs, rAttrs);
        }
        c.dispose();
    }

	private int processFragments(Fragment parent, XmlObject m, int indexR,
    		PAttrs pAttrs, RAttrs rAttrs) {
    	
    	List<Fragment> frags = parent.getChildren();
        for (int i=0; i<frags.size(); i++) {
        	Fragment frag = frags.get(i);
        	
        	if (frag.getType() == FRAGMENT_TEXT_ALIGN) {
        		int tmp = pAttrs.align;

        		int newAlign = frag.getAlign();
                CTPPr ppr = pAttrs.p.getPPr();
                if (ppr == null) {
                	ppr = pAttrs.p.addNewPPr();
                }
                if (newAlign == 0 && ppr.isSetJc()) {
                	ppr.unsetJc();
                } else if (newAlign == ALIGN_LEFT) {
                	CTJc jc = ppr.getJc();
                	if (jc == null)
                		jc = ppr.addNewJc();
                	jc.setVal(STJc.LEFT);
                } else if (newAlign == ALIGN_RIGHT) {
                	CTJc jc = ppr.getJc();
                	if (jc == null)
                		jc = ppr.addNewJc();
                	jc.setVal(STJc.RIGHT);
                } else if (newAlign == ALIGN_CENTER) {
                	CTJc jc = ppr.getJc();
                	if (jc == null)
                		jc = ppr.addNewJc();
                	jc.setVal(STJc.CENTER);
                } else if (newAlign == ALIGN_JUSTIFY) {
                	CTJc jc = ppr.getJc();
                	if (jc == null)
                		jc = ppr.addNewJc();
                	jc.setVal(STJc.BOTH);
                }

                pAttrs.align = newAlign;
                indexR = processFragments(frag, m, indexR, pAttrs, rAttrs);
                pAttrs.align = tmp;
        	} else if (frag.getType() == FRAGMENT_NEW_LINE) {
        		// оставляем элементы в текущем параграфе до позиции indexR, остальные переносим в новый параграф
        		pAttrs.p = addNewP(m, pAttrs.p, indexR);
        		CTPPr ppr = pAttrs.p.addNewPPr();

        		indexR = 1;
        		
                if (pAttrs.align == 0 && ppr.isSetJc()) {
                	ppr.unsetJc();
                } else if (pAttrs.align == ALIGN_LEFT) {
                	CTJc jc = ppr.getJc();
                	if (jc == null)
                		jc = ppr.addNewJc();
                	jc.setVal(STJc.LEFT);
                } else if (pAttrs.align == ALIGN_RIGHT) {
                	CTJc jc = ppr.getJc();
                	if (jc == null)
                		jc = ppr.addNewJc();
                	jc.setVal(STJc.RIGHT);
                } else if (pAttrs.align == ALIGN_CENTER) {
                	CTJc jc = ppr.getJc();
                	if (jc == null)
                		jc = ppr.addNewJc();
                	jc.setVal(STJc.CENTER);
                } else if (pAttrs.align == ALIGN_JUSTIFY) {
                	CTJc jc = ppr.getJc();
                	if (jc == null)
                		jc = ppr.addNewJc();
                	jc.setVal(STJc.BOTH);
                }
                
                if (pAttrs.firstLine != null || pAttrs.left != null) {
        			CTInd ind = ppr.getInd();
        			if (ind == null)
        				ind = ppr.addNewInd();
        			
        			if (pAttrs.firstLine != null)
        				ind.setFirstLine(pAttrs.firstLine);
        			if (pAttrs.left != null)
        				ind.setLeft(pAttrs.left);
                }

                if (pAttrs.spacingAfter != null ||
                		pAttrs.spacingLine != null || pAttrs.lineRule != null) {
        			CTSpacing sp = ppr.getSpacing();
        			if (sp == null)
        				sp = ppr.addNewSpacing();
        		
        			if (pAttrs.spacingAfter != null)
        				sp.setAfter(pAttrs.spacingAfter);
        			if (pAttrs.spacingLine != null)
        				sp.setLine(pAttrs.spacingLine);
	    			if (pAttrs.lineRule != null)
	    				sp.setLineRule(pAttrs.lineRule);
                }
        	} else if (frag.getType() == FRAGMENT_FONT_SIZE) {
        		BigInteger tmp = rAttrs.sz;
        		rAttrs.sz = BigInteger.valueOf(frag.getFontSize() * 2);
    			indexR = processFragments(frag, m, indexR, pAttrs, rAttrs);
    			rAttrs.sz = tmp;
        	} else if (frag.getType() == FRAGMENT_FONT_COLOR) {
        	} else if (frag.getType() == FRAGMENT_TEXT_BOLD) {
        		STOnOff.Enum tmp = rAttrs.b;
        		rAttrs.b = STOnOff.X_1;
    			indexR = processFragments(frag, m, indexR, pAttrs, rAttrs);
        		rAttrs.b = tmp;
        	} else if (frag.getType() == FRAGMENT_TEXT_ITALIC) {
        		STOnOff.Enum tmp = rAttrs.i;
        		rAttrs.i = STOnOff.X_1;
    			indexR = processFragments(frag, m, indexR, pAttrs, rAttrs);
        		rAttrs.i = tmp;
        	} else if (frag.getType() == FRAGMENT_TEXT_UNDERLINE) {
        		STUnderline.Enum tmp = rAttrs.u;
        		rAttrs.u = STUnderline.SINGLE;
    			indexR = processFragments(frag, m, indexR, pAttrs, rAttrs);
        		rAttrs.u = tmp;
        	} else if (frag.getType() == FRAGMENT_TEXT) {
    			CTR r = addNewR(pAttrs.p, indexR);
        		indexR++;

                CTRPr rpr = r.getRPr();
                if (rpr == null) {
                	rpr = r.addNewRPr();
                }
                if (rAttrs.b != null) {
                	CTOnOff b = rpr.getB();
                	if (b == null) {
                		b = rpr.addNewB();
                	}
                	b.setVal(rAttrs.b);
                }
                if (rAttrs.i != null) {
                	CTOnOff ci = rpr.getI();
                	if (ci == null) {
                		ci = rpr.addNewI();
                	}
                	ci.setVal(rAttrs.i);
                }
                if (rAttrs.u != null) {
                	CTUnderline u = rpr.getU();
                	if (u == null) {
                		u = rpr.addNewU();
                	}
                	u.setVal(rAttrs.u);
                }
                
                if (rAttrs.sz != null) {
                	CTHpsMeasure ms = rpr.getSz();
                	if (ms == null) {
                		ms = rpr.addNewSz();
                	}
                	ms.setVal(rAttrs.sz);
                }
                if (rAttrs.szCs != null) {
                	CTHpsMeasure ms = rpr.getSzCs();
                	if (ms == null) {
                		ms = rpr.addNewSzCs();
                	}
                	ms.setVal(rAttrs.szCs);
                }
                if (rAttrs.ftAscii != null) {
                	CTFonts fnt = rpr.getRFonts();
                	if (fnt == null) {
                		fnt = rpr.addNewRFonts();
                	}
                	fnt.setAscii(rAttrs.ftAscii);
                }                	
                if (rAttrs.ftAnsi != null) {
                	CTFonts fnt = rpr.getRFonts();
                	if (fnt == null) {
                		fnt = rpr.addNewRFonts();
                	}
                	fnt.setHAnsi(rAttrs.ftAnsi);
                }                	
                if (rAttrs.color != null) {
                	CTColor color = rpr.getColor();
                	if (color == null) {
                		color = rpr.addNewColor();
                	}
                	color.setVal(rAttrs.color);
                }                	

            	String val = frag.getText();
                CTText[] ts = r.getTArray();
                if (ts != null) {
                    for (int k = ts.length - 2; k >= 0; k--) {
                    	r.removeT(k);
                    }
                    ts = r.getTArray();
                    CTText t;
                    if (ts.length == 0) {
                        t = r.addNewT();
                    } else {
                        t = ts[0];
                    }
                    if (val.endsWith(" ") || val.startsWith(" ")) t.setSpace(Space.PRESERVE);
                    t.setStringValue(val);
                }
        	}
        }
        return indexR;
	}

	private void setImage(XWPFDocument oDoc, XmlObjectRange xor, String value, String imgPos) throws Exception {
    	if (value == null) return;
        XmlObject xo = xor.getObject();
        XmlCursor c = xo.newCursor();

        List<ReportImage> images = new ArrayList<ReportWrapperPOI.ReportImage>();
        
        if (value != null && value.length() > 0) {
        	StringTokenizer st = new StringTokenizer(value, "|");
        	int i = 0;
        	while (st.hasMoreTokens()) {
        		String filePath = st.nextToken();
        		
	        	BufferedImage bi = ImageIO.read(new FileInputStream(filePath));
	            int w = bi.getWidth();
	            int h = bi.getHeight();
	            int format = 8;
	            String relationId = oDoc.addPictureData(new FileInputStream(filePath), format);
            
	            images.add(new ReportImage(w, h, relationId, "image" + i++));
        	}
        }

        if (xo instanceof CTFldChar) {
        	XmlCursor c2 = xo.newCursor();
        	c2.toParent(); // r
        	
        	CTR r = null;
        	CTP p = null;
        	if (c2.getObject() instanceof CTR) {
        		r = (CTR) c2.getObject();
        	}
        	
        	c2.toParent(); // p
        	int index = -1;
        	if (c2.getObject() instanceof CTP && r != null) {
        		p = (CTP) c2.getObject();
        		CTR[] rs = p.getRArray();
        		for (int i=0; i<rs.length; i++) {
        			if (r.equals(rs[i])) {
        				index = i;
        				break;
        			}
        		}
        	}
        
        	c2.toEndToken();

            c.toNextToken();
            c.removeXml();
            c.toNextToken();

            boolean end = false;
            while (!end) {
                Object ro = c.getObject();
                if (ro == null) {
                	c.toNextToken();
                	//c.toChild(new javax.xml.namespace.QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "r"));
                	ro = c.getObject();
                }
                if (ro instanceof CTR) {
                	CTFldChar[] ch = ((CTR)ro).getFldCharArray();
	                if (ch.length > 0 && STFldCharType.END.equals(ch[0].getFldCharType())) {
	                    end = true;
	                }
                } else if (ro instanceof CTP) {
                	CTR[] rs = ((CTP)ro).getRArray();
	                if (rs != null && rs.length > 0) {
	                	for (CTR r2 : rs) {
	    	                if (end) {
	    	                	r2.newCursor().moveXml(c2);
	    	                } else {
		                    	CTFldChar[] ch = r2.getFldCharArray();
		    	                if (ch.length > 0 && STFldCharType.END.equals(ch[0].getFldCharType())) {
		    	                    end = true;
		    	                }
	    	                }
	                	}
	                }
                } else {
                	c.toNextToken();
                	ro = c.getObject();
                }
                c.removeXml();
            }
            if (value != null && value.length() > 0) {
                if (r == null) {
                	r = p.addNewR();
                }

                if (images.size() > 0) {
                	for (ReportImage ri : images) {
                		addImageCTR(r, ri, imgPos);
                	}
                }
            }
            c2.dispose();
        } else if (xo instanceof CTSimpleField) {
        	CTSimpleField o = (CTSimpleField)xo;
            CTR[] rs = o.getRArray();
            CTR r = null;

            if (rs != null && rs.length > 0) {
            	r = rs[0];
                CTText[] ts = rs[0].getTArray();
                CTRPr rpr = rs[0].getRPr();
                if (rpr == null) {
                	rpr = rs[0].addNewRPr();
                }
                
                if (ts != null) {
                    for (int i = ts.length - 1; i >= 0; i--) {
                        rs[0].removeT(i);
                    }
                }

                if (images.size() > 0) {
                	for (ReportImage ri : images) {
                		addImageCTR(r, ri, imgPos);
                	}
                }
                rs[0].newCursor().moveXml(c);
            } else {
            	r = o.addNewR();
                if (images.size() > 0) {
                	for (ReportImage ri : images) {
                		addImageCTR(r, ri, imgPos);
                	}
                }
                r.newCursor().moveXml(c);
            }
            c.removeXml();
        }
        c.dispose();
    }
	
	private void addImageCTR(CTR r, ReportImage ri, String imgPos) throws XmlException {
		String pic = "<v:shape xmlns:o=\"urn:schemas-microsoft-com:office:office\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\" xmlns:v=\"urn:schemas-microsoft-com:vml\""
				+ " style=\"width:" + ri.getWidth() + "pt;height:" + ri.getHeight() + "pt"
				+ ("behind".equals(imgPos) ? ";position:absolute;z-index:-100" : "")
				+ "\"><v:imagedata r:id=\"" + ri.getRelationId() + "\" o:title=\"" + ri.getTitle() + "\"/></v:shape>";
		CTPicture pict = r.addNewPict();
		pict.set(XmlToken.Factory.parse(pic));
	}

    private int CountAllValues(Element xml) {
        try {
            List<Element> children = xml.getChildren();
            
            int count = 0;
            for (Element child : children) {
            	if (child.getAttribute("str") != null || child.getAttribute("src") != null)
            		count++;
            	
            	count += CountAllValues(child);
            }
            return count;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void ProcessTreeColumn(int level, int count, List tempNodes, XmlObjectRange oFld) {
        List ptChildNodes;
        Element ptNode;
        Attribute ptChildItem;
        List ptChildAttr;
        int childCount;
        String value;

        for (int n = 0; n < count; n++) {
            ptNode = (Element) tempNodes.get(n);
            ptChildNodes = ptNode.getChildren();
            ptChildAttr = ptNode.getAttributes();
            childCount = ptChildNodes.size();

            ptChildItem = (Attribute) ptChildAttr.get(0);
            value = ptChildItem.getValue();

            InsertTreeNodeValue(value, level, oFld);

            ProcessTreeColumn(level + 1, childCount, ptChildNodes, oFld);
        }
    }

    public void InsertTreeNodeValue(String value, int level, XmlObjectRange oFld) {
/*        Dispatch.call(oFld, "Select");
        Dispatch.call(oSel, "InsertRowsAbove", new Variant(1));
        Dispatch.call(oSel, "Collapse");

        Dispatch oPhs = Dispatch.get(oSel, "Paragraphs").toDispatch();
        Dispatch.put(oPhs, "LeftIndent", new Variant(10 * level));

        Dispatch.put(oSel, "Text", value);
*/    }

    public void viewExcelReport(String fileName, Element xml, String title, String macros) {
        try {
            InputStream is = new FileInputStream(fileName);
            Workbook wb = WorkbookFactory.create(is);
            is.close();

            int count;
            String str;

            int wshsCount = wb.getNumberOfSheets();

            for (int w = 0; w < wshsCount; w++) // Пробегаемся по всем листам в книге Excel
            {
                Sheet wsh = wb.getSheetAt(w);

                int colCount = 0;

                for (Iterator<Row> ri = wsh.rowIterator(); ri.hasNext(); ) {
                    Row row = ri.next();

                    colCount = (row.getLastCellNum() > colCount) ? row.getLastCellNum() : colCount;

                    for (int ci = 0; ci < colCount; ci++) {
                        Object range = getCell(row, ci);
                        Cell rng = (Cell)range;

                        Comment com = null; 
                        	
                        try {
                        	com = rng.getCellComment();
                        } catch (Exception e) {
                        	System.out.println("" + (char)('A' + (char)rng.getColumnIndex()) + " " + (rng.getRowIndex() + 1));
                        }

                        if (com == null || com.getString() == null) continue;

                        str = com.getString().getString();

                        int start = str.indexOf('|');
                        if (start == -1) continue;
                        String type = str.substring(0, start);

                        if ("FilterDate".equals(type)) {
                            int id = Integer.parseInt(str.substring(start + 1));
                            filterDatesMap.put(id, rng);
                            continue;
                        }
                        if ("User".equals(type) || "Department".equals(type) || "Base".equals(type)) {
                            int id = Integer.parseInt(str.substring(start + 1));
                            idToCell.put(id, rng);
                            typeMap.put(id, type);
                            initialMap.put(id, "");
                            continue;
                        }
                        int end = str.indexOf('|', start + 1);
                        if ("Filter".equals(type)) {
                            int id;
                            if (end == -1) id = Integer.parseInt(str.substring(start + 1));
                            else id = Integer.parseInt(str.substring(start + 1, end));
                            idToCell.put(id, rng);
                            typeMap.put(id, type);
                            initialMap.put(id, "");
                            continue;
                        }
                        if (end == -1) continue;
                        String result = str.substring(start + 1, end);
                        if ("Sum".equals(type)) {
                            String cols = str.substring(end + 1);
                            totalMap.put(result, cols);
                            continue;
                        }
                        if ("Count".equals(type)) {
                            String cols = str.substring(end + 1);
                            countMap.put(result, cols);
                            continue;
                        }
                        if ("Free".equals(type)) {
                            String cols = str.substring(end + 1);
                            freeMap.put(result, cols);
                            continue;
                        }
                        int id = 0;
                        try {
                        	id = Integer.parseInt(result);
                        } catch (NumberFormatException nfe) {}
                        if (id == 0) continue;
                        if ("SpecSum".equals(type)) {
                            String cols = str.substring(end + 1);
                            specsumMap.put(id, cols);
                        }
                        if ("ConsValue".equals(type)) {
                            end = str.lastIndexOf('|');
                            String cols = str.substring(end + 1);
                            consValMap.put(id, cols);
                        }
                        if ("StatTable".equals(type)) {
                            str = str.substring(end + 1);
                            end = str.indexOf('|', 1);
                            String tpidLev = str.substring(0, end);
                            String ost = result + "|" + str.substring(end + 1);
                            statMap.put(tpidLev, ost);
                        }
                        if ("TreeColumn".equals(type)) {
                            String buf = "";

                            int mrngIndex = getMergedRegionIndex(wsh, rng);
                            if (mrngIndex > -1)
                                wsh.removeMergedRegion(mrngIndex);

                            boolean oneRow = str.indexOf("oneRow=") > -1;
                            noemptyMap.put(id, oneRow);

                            //@todo WRAPPER ?????
                            //rng = Dispatch.call(rng, "Range", "A1", "AZ1").toDispatch();

                            for (Iterator<Row> ri2 = wsh.iterator(); ri2.hasNext(); ) {
                                Row row2 = ri2.next();

                                int colCount2 = (row2.getLastCellNum() > colCount) ? row2.getLastCellNum() : colCount;
                                
                                for (int ci2 = 0; ci2 < colCount2; ci2++) {
                                    Cell rng2 = (Cell) getCell(row2, ci2);

                                    Comment com2 = rng2.getCellComment();

                                    if (com2 == null || com2.getString() == null) continue;

                                    str = com2.getString().getString();

                                    if (str.length() > 0 && str.endsWith("|")) {
                                        start = str.indexOf('|');
                                        end = str.indexOf('|', start + 1);
                                        String result1 = str.substring(start + 1, end);

                                        end = str.lastIndexOf('|', str.length() - 1);
                                        //str.MakeReverse();
                                        //end = str.indexOf('|', 1);
                                        String result2 = str.substring(end, str.length() - 1);
                                        if (result.equals(result2) && !result.equals(result1)) {
                                            buf += result1;
                                            
                                            try {
                                            	Integer cid = Integer.parseInt(result1);
                                                treeColumnMap.put(cid, id);
                                            } catch (NumberFormatException nfe) {}

                                            buf += ",";
                                        }
                                    } else if (str.indexOf("tree=") > -1) {
                                        start = str.indexOf('|');
                                        end = str.indexOf('|', start + 1);
                                        String result1 = str.substring(start + 1, end);

                                        start = str.indexOf("tree=");
                                        end = str.indexOf('|', start + 1);
                                        String result2;
                                        if (end > -1) {
                                            result2 = str.substring(start + 5, end);
                                        } else {
                                            result2 = str.substring(start + 5);
                                        }
                                        if (result.equals(result2) && !result.equals(result1)) {
                                            buf += result1;
                                            buf += ",";
                                            try {
                                            	Integer cid = Integer.parseInt(result1);
                                                treeColumnMap.put(cid, id);
                                            } catch (NumberFormatException nfe) {}

                                        }
                                    }
                                }
                            }
                            treeMap.put(id, buf);

                            range = new SheetRange(
                                    new CellRangeAddress(rng.getRowIndex(), rng.getRowIndex(), rng.getColumnIndex(), rng.getColumnIndex() + 26),
                                    rng.getSheet(), oneRow);

                        }
                        if ("Table".equals(type)) {
                            boolean oneRow = str.indexOf("oneRow=") > -1;
                            noemptyMap.put(id, oneRow);

                            start = str.indexOf("columns=");

                            int cols = 0;
                            if (start > -1) {
                                end = str.indexOf("|", start + 1);
                                if (end == -1) end = str.length();

                                String tmp = str.substring(start + 8, end);
                                cols = Integer.parseInt(tmp);
                            }

                            range = new SheetRange(
                                        new CellRangeAddress(rng.getRowIndex(), rng.getRowIndex(), rng.getColumnIndex(), rng.getColumnIndex() + cols),
                                        rng.getSheet(), false);

                            processedColumns.put(id, 1);
                            for (Iterator<Row> ri2 = wsh.iterator(); ri2.hasNext(); ) {
                                Row row2 = ri2.next();
                                int colCount2 = (row2.getLastCellNum() > colCount) ? row2.getLastCellNum() : colCount;
                                
                                for (int ci2 = 0; ci2 < colCount2; ci2++) {
                                    Cell rng2 = (Cell) getCell(row2, ci2);
                                    Comment com2 = rng2.getCellComment();

                                    if (com2 == null || com2.getString() == null) continue;

                                    str = com2.getString().getString();

                                    if (str.indexOf("tableId=") > -1) {
                                        start = str.indexOf('|');
                                        end = str.indexOf('|', start + 1);
                                        String result1 = str.substring(start + 1, end);

                                        start = str.indexOf("tableId=");
                                        end = str.indexOf('|', start + 1);
                                        String result2;
                                        if (end > -1) {
                                            result2 = str.substring(start + 8, end);
                                        } else {
                                            result2 = str.substring(start + 8);
                                        }
                                        if (result.equals(result2) && !result.equals(result1)) {
                                            tableMap.put(Integer.parseInt(result1), id);
                                        }
                                    }
                                }
                            }
                        }
                        idToCell.put(id, range);
                        //@todo WRAPPER ?????
                        //String addr = new CellRangeAddress().formatAsString();
                                //Dispatch.call(rng, "Address", new Variant(true), new Variant(true), new Variant(1)).getString();
                        //initialMap.put(id, addr);
                        typeMap.put(id, type);
                    }
                }

                for (Iterator<Row> ri = wsh.rowIterator(); ri.hasNext(); ) {
                    Row row = ri.next();

                    for (int ci = 0; ci < colCount; ci++) {
                        Object range = getCell(row, ci);
                        Cell rng = (Cell)range;

                        Comment com = null; 
                    	
                        try {
                        	com = rng.getCellComment();
                        } catch (Exception e) {
                        }

                        if (com == null || com.getString() == null) continue;

                        rng.removeCellComment();
                    }
                }

                // Запоминаем все объединения ячеек в мапе
                int mrsCount = wsh.getNumMergedRegions();
                
                MultiMap<Integer, CellRangeAddress> sheetMergedRegionMap = new MultiMap<>();
                mergedRegionMap.put(w, sheetMergedRegionMap);

                for (int i = 0; i<mrsCount; i++) {
                    CellRangeAddress cr = wsh.getMergedRegion(i);
                    sheetMergedRegionMap.put(cr.getFirstRow(), cr);
                }
            }

            normalizeXml(xml);
            List pNodes = xml.getChildren();
            count = pNodes.size();

            System.out.println("start process nodes: " + new Date());

            ProcessNodes(0, -1, 0, count, pNodes, wb);
            System.out.println("end process nodes: " + new Date());

            Object range;
            Cell rng, rng2, crng1, crng2, crng12, crng22;
            String type, specStr;

            //@todo WRAPPER ?????

/*
            for (Integer key3 : specsumMap.keySet()) {
                str = specsumMap.get(key3);
                rng = myMap.get(key3);

                long sum = 0;
                int end = str.indexOf('|');
                int pid = Integer.parseInt(str.substring(0, end));
                str = str.substring(end + 1);

                String startAddress;
                startAddress = initialMap.get(pid); // Column with profession
                crng1 = myMap.get(pid);

                end = startAddress.indexOf('$', 1);
                end = startAddress.indexOf('$', end);
                int start = Integer.parseInt(startAddress.substring(end + 1));

                startAddress = Dispatch.call(crng1, "Address", new Variant(true), new Variant(true), new Variant(1)).getString();
                end = startAddress.indexOf('$', 1);
                end = startAddress.indexOf('$', end);
                int stop = Integer.parseInt(startAddress.substring(end + 1));

                end = str.indexOf('|');
                specStr = str.substring(0, end);
                //specStr.Insert(specStr.length(), ',');
                String prof = str.substring(end + 1);
                prof += ',';
                end = prof.indexOf(',');
                int m = 0;
                while (end > -1) {
                    tMap.put(m, prof.substring(0, end));
                    prof = prof.substring(end + 1);
                    end = prof.indexOf(',');
                    m++;
                }
                int pid2 = Integer.parseInt(specStr);
                crng12 = myMap.get(pid2);
                for (int j = stop - start; j >= 0; j--) {
                    crng2 = Dispatch.call(crng1, "Offset", new Variant(-j), new Variant(0)).toDispatch();
                    String curValue = Dispatch.get(crng2, "Value").getString();
                    crng22 = Dispatch.call(crng12, "Offset", new Variant(-j), new Variant(0)).toDispatch();
                    //end = prof.indexOf(',');
                    for (int cc = 0; cc < m; cc++) {
                        String curProf = tMap.get(cc);
                        if (curValue.equals(curProf) && Dispatch.get(crng22, "Value").getString() != null) {
                            String formula = Dispatch.get(crng22, "Formula").getString();
                            if (!(formula.length() > 0 && "=".equals(formula.substring(0, 1)))) {
                                sum += Dispatch.get(crng22, "Value").getDouble();
                            }
                        }
                    }
                }
                Dispatch.put(rng, "Value", new Variant(sum));
            }
*/

            Map<String, SortedSet<Integer>> map = new TreeMap<String, SortedSet<Integer>>();
            
            for (Integer key : idToCell.keySet()) {
                range = idToCell.get(key);

                if (range instanceof Cell) {
                    rng = (Cell)range;
                } else {
                    Row r = getRow(((SheetRange)range).getSheet(), ((SheetRange)range).getCellRangeAddress().getFirstRow());
                    rng = getCell(r, ((SheetRange)range).getCellRangeAddress().getFirstColumn());
                }
                type = typeMap.get(key);
                if ("TreeColumn".equals(type))// && rng.GetValue().vt == VT_EMPTY)
                {
                    try {
                        rng.getSheet().removeRow(rng.getRow());
                    }
                    catch (Exception e) {
                    }
                } else if ("Column".equals(type) || "ConsColumnEx".equals(type)
                        || "Table".equals(type))// && rng.GetValue().vt == VT_EMPTY)
                {
                    try {
                        if (!tableMap.containsKey(key)) {
                            if (processedColumns.containsKey(key) && !treeColumnMap.containsKey(key)) {
                            	SortedSet<Integer> rowsToDelete = map.get(rng.getSheet().getSheetName());
                            	if (rowsToDelete == null) {
                                    rowsToDelete = new TreeSet<Integer>(new Comparator<Integer>() {
                                    	public int compare(Integer o1, Integer o2) {
                                    		return (o1 > o2) ? -1 : (o1 < o2) ? 1 : 0;
                                    	}
                        			});
                                    map.put(rng.getSheet().getSheetName(), rowsToDelete);
                            	}
                            	rowsToDelete.add(rng.getRowIndex());
                            }
                        }
                    }
                    catch (Exception e) {
                    	e.printStackTrace();
                    }
                } else if ("StatTable".equals(type)) {
                    rng2 = rng.getSheet().getRow(rng.getRowIndex() - 1).getCell(rng.getColumnIndex());
                    rng2.getSheet().removeRow(rng2.getRow());
                    rng.getSheet().removeRow(rng.getRow());
                }
            }
            idToCell.clear();
            typeMap.clear();
            initialMap.clear();

            for (String name : map.keySet()) {
            	SortedSet<Integer> rowsToDelete = map.get(name);
	            for (int m : rowsToDelete) {
	            	deleteRows(m, 1, wb.getSheet(name));
	            }
            }
            
            for (int w = 0; w < wshsCount; w++) {
	            // добавляем все новые объединения ячеек при копировании строк таблицы
	            addMergedRegions(wb.getSheetAt(w));
            }
            calculateFormulas(wb);
            
            if (macros.length() > 0) {
                //@todo WRAPPER ?????
//                Dispatch.call(excel, "Run", macros);
            }

            FileOutputStream os = new FileOutputStream(fileName);
            wb.write(os);
            os.close();

            if (showAfterComplete) {
                Runtime r = Runtime.getRuntime();
                r.exec("cmd /c \"" + fileName + "\"");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (taskTable != null) {
            	taskTable.setProgressCaption("");
            	taskTable.setProgressValue(0);
            }
        }
    }

    private void normalizeXml (Element element) {
        List children = element.getChildren();
        List<String> processed = new ArrayList<String>();
        
        for (Object o1 : children) {
        	Element e = (Element)o1;
	        String tableId = e.getAttributeValue("tableId");
	        if (tableId != null && !processed.contains(tableId)) {
	            processed.add(tableId);
	            boolean noempty =  noemptyMap.containsKey(Integer.parseInt(tableId)) && noemptyMap.get(Integer.parseInt(tableId));
	        	List<Element> elements = getElementsByAttribute(element, "tableId", tableId);
	            for (Element col : elements) {
	            	if (col.getChildren().size() > 0 && ((Element)col.getChildren().get(0)).getAttributes().size() == 0) {
	            		normalizeCols(elements, noempty);
	            		break;
	            	}
	            }
	        }
        }
    }
    
    private void normalizeCols (List<Element> cols, boolean deleteFirstRow) {
    	
    	List<Integer> sizes = new ArrayList<Integer>();
    	
    	for (Element col : cols) {
    		List<Element> values1 = col.getChildren("Value");
    		int i = 0;
    		for (Element value1 : values1) {
    			int count = value1.getChildren("Value").size();
    			if (sizes.size() <= i) {
    				sizes.add(count);
    			} else {
    				sizes.set(i, Math.max(count, sizes.get(i)));
    			}
    			i++;
    		}
    	}
    	
    	for (Element col : cols) {
    		List<Element> values1 = col.getChildren("Value");
    		int size1 = values1.size();
    		
    		for (int i = size1 - 1; i>=0; i--) {
    			Element value1 = values1.get(i);
    			List<Element> values2 = value1.getChildren("Value");
    			int count = values2.size();
    			int maxCount = sizes.get(i);
    			
            	int to = (deleteFirstRow ? 1 : 0);

            	int at = i;
            	
            	for (int j = values2.size() - 1; j >= to; j--) {
            		Element value2 = (Element) values2.get(j);
            		value2.detach();
	            	col.addContent(i + 1, value2);
	            	at++;
            	}
            	
            	if (deleteFirstRow && count > 0) {
            		Element value2 = (Element) values2.get(0);
            		value2.detach();
            		if (value2.getAttributeValue("str") != null)
            			value1.setAttribute("str", value2.getAttributeValue("str"));
            		else if (value2.getAttributeValue("src") != null)
            			value1.setAttribute("src", value2.getAttributeValue("src"));
            	}

            	to = maxCount + (deleteFirstRow && count == 0 ? -1 : 0);
            	
            	for (int j = count; j < to; j++) {
            		Element value = new Element("Value");
            		col.addContent(at + 1, value);
            	}
    		}
    	}
    }

    public List<Element> getElementsByAttribute (Element root, String attr, String id) {
        List<Element> res = new ArrayList<Element>();
        List list = root.getChildren();
        for (Object temp : list) {
            Element e = (Element) temp;
            if (id.equals(e.getAttributeValue(attr)))
                res.add(e);
        }
        return res;
    }

    private int getMergedRegionIndex(Sheet wsh, Cell rng) {
        int count = wsh.getNumMergedRegions();
        for (int i=0; i<count; i++) {
            CellRangeAddress r = wsh.getMergedRegion(i);
            if (r.isInRange(rng.getRowIndex(), rng.getColumnIndex())) return i;
        }
        return -1;
    }

    private void ProcessNodes(int pid, int level, int tpid, int count, List tempNodes, Workbook wb) {
        List ptChildNodes;
        Element ptNode;
        Attribute ptChildItem;
        List ptChildAtr;
        int childCount;
        String value;
        String type;
        //int trow = 0;
        //boolean prev = false;
        String startAddress = "";
        int startRow = 0;

        CreationHelper factory = wb.getCreationHelper();
        ClientAnchor anchor = factory.createClientAnchor();

        for (int n = 0; n < count; n++) {
            ptNode = (Element) tempNodes.get(n);
            ptChildNodes = ptNode.getChildren();
            ptChildAtr = ptNode.getAttributes();
            childCount = ptChildNodes.size();

            Cell trng, trng2;
            Cell rng, rng2;

            if (childCount == 0) {
                String name = ptNode.getName();

                if ("FilterDate".equals(name)) {
                    if (taskTable != null) {
                    	taskTable.setProgressValue(++currentValue);
                    }

                    value = ptNode.getAttributeValue("type");
                    int id = Integer.parseInt(value);
                    rng = filterDatesMap.get(id);
                    if (rng == null) continue;
                    value = ptNode.getAttributeValue("str");
                    SetValue(rng, value);
                    filterDatesMap.remove(id);
                    continue;
                }
                // Если поле или фильтр
                if (("Field".equals(name) || "Filter".equals(name) || "Department".equals(name)
                        || "User".equals(name) || "Base".equals(name)) && tpid == 0) {
                    value = ptNode.getAttributeValue("id");
                    int id = Integer.parseInt(value);
                    rng = (Cell)idToCell.get(id);
                    if (rng == null) continue;

                    value = ptNode.getAttributeValue("str");
                    SetValue(rng, value);
                    
                    value = ptNode.getAttributeValue("src");
                    if (value != null && value.length() > 0) {
                    	String align = ptNode.getAttributeValue("align");
                    	String valign = ptNode.getAttributeValue("valign");
                    	setImageValue(rng, value, align, valign);
                    }
                    
                    value = ptNode.getAttributeValue("list");
                    if (value != null && value.length() > 0) {
                        Drawing drawing = rng.getSheet().createDrawingPatriarch();
                        Comment comment = drawing.createCellComment(anchor);
                        RichTextString str = factory.createRichTextString(value);
                        comment.setString(str);
                        rng.setCellComment(comment);
                    }

                    if (taskTable != null) {
                    	taskTable.setProgressValue(++currentValue);
                    }

                    idToCell.remove(id);
                    typeMap.remove(id);
                    initialMap.remove(id);
                    continue;
                }
                if ("Filter2".equals(name)) {
                    String addr = ptNode.getAttributeValue("cell");

                    value = ptNode.getAttributeValue("sheet");
                    int sheet = Integer.parseInt(value);

                    value = ptNode.getAttributeValue("str");

                    Sheet wsh = wb.getSheetAt(sheet - 1);
                    addr = addr.replaceAll("\\$", "");
                    addr = addr + ":" + addr;
                    CellRangeAddress r = CellRangeAddress.valueOf(addr);
                    rng = wsh.getRow(r.getFirstRow()).getCell(r.getFirstColumn());

                    try {
                        Double val = Double.valueOf(value);
                        rng.setCellValue(val);
                    } catch (Exception ex) {
                        rng.setCellValue(value);
                    }

                    value = ptNode.getAttributeValue("list");
                    if (value != null && value.length() > 0) {
                        rng.removeCellComment();
                        Drawing drawing = rng.getSheet().createDrawingPatriarch();
                        Comment comment = drawing.createCellComment(anchor);
                        RichTextString str = factory.createRichTextString(value);
                        comment.setString(str);
                        rng.setCellComment(comment);
                    }
                    if (taskTable != null) {
                    	taskTable.setProgressValue(++currentValue);
                    }

                    continue;
                }
                String buf = treeMap.get(tpid);
                if (buf == null) buf = "1";
                if (buf.length() == 0 && "Value".equals(name)) {
                    if (taskTable != null) {
                    	taskTable.setProgressValue(++currentValue);
                    }

                    value = ptNode.getAttributeValue("str");
                    InsertTreeNodeValue(value, tpid, level, false);
                    continue;
                }
                if ("Column".equals(name) || "TreeColumn".equals(name) || pid == tpid) {
                    continue;
                }

                if ("ConsValue".equals(name)) {
                    if (taskTable != null) {
                    	taskTable.setProgressValue(++currentValue);
                    }

                    ptChildItem = (Attribute) ptChildAtr.get(0);
                    value = ptChildItem.getValue();
                    int id = Integer.parseInt(value);

                    rng = (Cell)idToCell.get(pid);
                    if (rng == null) continue;
                    ptChildItem = (Attribute) ptChildAtr.get(1);
                    value = ptChildItem.getValue();
                    String finalVal = value;
                    if ("Infinity".equals(finalVal) || "NaN".equals(finalVal)) finalVal = "";
                    if (firstColumn) {
                        if (id != firstConsValue) {
                            if (startRow > 0) {
                                fCount += AddSummary(5, tpid, startRow);
                            }
                            firstConsValue = id;
                            String str2 = consValMap.get(id);
                            if (str2.length() > 0) {
                                Object range = idToCell.get(tpid);

                                if (range instanceof Cell) {
                                    trng = (Cell)range;
                                } else {
                                    Row r = getRow(((SheetRange)range).getSheet(), ((SheetRange)range).getCellRangeAddress().getFirstRow());
                                    trng = getCell(r, ((SheetRange)range).getCellRangeAddress().getFirstColumn());
                                }

                                trng2 = trng.getRow().getCell(trng.getColumnIndex());
                                startAddress = new CellRangeAddress(trng2.getRowIndex(), trng2.getRowIndex(), trng2.getColumnIndex(), trng2.getColumnIndex()).formatAsString();
                                startRow = trng2.getRowIndex();
                                InsertTreeNodeValue(str2, tpid, 5, false);
                                fCount++;
                            }
                        }

                        ptChildItem = (Attribute) ptChildAtr.get(2);
                        String tt = ptChildItem.getValue();
                        if (tt.length() > 0) {
                            InsertTreeNodeValue(value, tpid, 6, false);
                            fCount++;
                        }
                        rng2 = rng.getSheet().getRow(rng.getRowIndex() - 1).getCell(rng.getColumnIndex());
                        SetValue(rng2, finalVal);

                        if (n == count - 1 && startAddress.length() > 0) {
                            fCount += AddSummary(5, tpid, startRow);
                        }

                    } else {
                        if (id != firstConsValue) {
                            if (startAddress.length() > 0) {
                                cCount -= HowAddSummary(5, tpid);
                            }
                            ptChildItem = (Attribute) ptChildAtr.get(2);
                            value = ptChildItem.getValue();
                            firstConsValue = id;
                            String str2 = consValMap.get(id);
                            if (str2.length() > 0) {
                                startAddress = "notNull";
                                if (value.length() > 0) cCount--;
                            }
                        }

                        if (cCount == 0) cCount = 1;
                        rng2 = rng.getSheet().getRow(rng.getRowIndex() - cCount--).getCell(rng.getColumnIndex());
                        SetValue(rng2, finalVal);
                    }
                }

                if ("Value".equals(name)) {// если значение из колонки
                    if (taskTable != null) {
                    	taskTable.setProgressValue(++currentValue);
                    }

                    rng = (Cell)idToCell.get(pid);
                    if (rng == null) continue;

                    value = ptNode.getAttributeValue("str");
                    value = (value != null) ? value : "";
                    
                    String srcValue = ptNode.getAttributeValue("src");

                    if (tpid == 0) {
                        //if (str=="Column")
                        //{
                        if (tableMap.containsKey(pid)) {
                            int tableId = tableMap.get(pid);
                            int firstColumnId;
                            processedColumns.put(pid, 1);
                            if (columnMap.containsKey(tableId)) {
                                firstColumnId = columnMap.get(tableId);
                            } else {
                                columnMap.put(tableId, pid);
                                firstColumnId = pid;
                            }
                            if (firstColumnId == pid && n == 0) {
                                Object range = idToCell.get(tableId);
                                if (range == null) continue;

                                if (range instanceof Cell) {
                                	Cell tableRng = (Cell)range;
                                    shiftCells(tableRng.getRowIndex(), tableRng.getColumnIndex(), tableRng.getColumnIndex(), count, tableRng.getSheet());
                                    tableRng = tableRng.getSheet().getRow(tableRng.getRowIndex() + 1).getCell(tableRng.getColumnIndex());
                                    idToCell.put(tableId, tableRng);
                                } else {
                                    SheetRange sr = (SheetRange)range;

                                    shiftCells(sr.getCellRangeAddress().getFirstRow(), sr.getCellRangeAddress().getFirstColumn(), sr.getCellRangeAddress().getLastColumn(), count, sr.getSheet());
                                }
                            }

                            rng2 = getCell(getRow(rng.getSheet(), rng.getRowIndex() + n), rng.getColumnIndex());

                            if (ptNode.getAttribute("decoration") != null) {
                                Font f = wb.getFontAt( rng2.getCellStyle().getFontIndex() );
                                f.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
                                rng2.getCellStyle().setFont(f);
                            }

                            if (srcValue != null && srcValue.length() > 0)
                            	setImageValue(rng2, srcValue);
                            else if (value.length() > 0)
                                SetValue(rng2, value);
                        } else {
                            if (count == 1) {
                                if (ptNode.getAttribute("decoration") != null) {
                                    Font f = wb.getFontAt( rng.getCellStyle().getFontIndex() );
                                    f.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
                                    rng.getCellStyle().setFont(f);
                                }

                                if (srcValue != null && srcValue.length() > 0)
                                	setImageValue(rng, srcValue);
                                else if (value.length() > 0)
                                    SetValue(rng, value);

                                idToCell.remove(pid);
                                typeMap.remove(pid);
                                initialMap.remove(pid);
                            } else {
                                processedColumns.put(pid, 1);

                                shiftCells(rng.getRowIndex(), rng.getColumnIndex(), rng.getColumnIndex(), 1, rng.getSheet());
                                rng = rng.getSheet().getRow(rng.getRowIndex() + 1).getCell(rng.getColumnIndex());

                                String cVal = "";
                                try { cVal = rng.getCellFormula();} catch (Exception e) {}
                                rng2 = rng.getSheet().getRow(rng.getRowIndex() - 1).getCell(rng.getColumnIndex());
                                
                                if (rng2.getCellComment() != null) rng2.getCellComment().setRow(rng.getRowIndex());

                                if (ptNode.getAttribute("decoration") != null) {
                                    Font f = wb.getFontAt( rng2.getCellStyle().getFontIndex() );
                                    f.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
                                    rng.getCellStyle().setFont(f);
                                }

                                if (srcValue != null && srcValue.length() > 0)
                                	setImageValue(rng2, srcValue);
                                else if (value.length() > 0)
                                    SetValue(rng2, value);
                                else if (cVal.length() > 0) {
                                    if (n == count - 1)
                                        rng = rng.getSheet().getRow(rng.getRowIndex() + count - 1).getCell(rng.getColumnIndex());
                                    else
                                        rng = rng.getSheet().getRow(rng.getRowIndex() - 1).getCell(rng.getColumnIndex());
                                }

                                idToCell.put(pid, rng);
                            }
                        }
                    }
                    if (tpid != 0) {
                        processedColumns.put(pid, 1);
                    	SheetRange sr = (SheetRange)idToCell.get(tpid);
                        if (firstColumn) {
                            trng = getCell(getRow(sr.getSheet(),sr.getCellRangeAddress().getFirstRow()), sr.getCellRangeAddress().getFirstColumn());

                            if (!sr.isOneRow())
                            	shiftCells(sr.getCellRangeAddress().getFirstRow(), sr.getCellRangeAddress().getFirstColumn(), sr.getCellRangeAddress().getLastColumn(), 1, sr.getSheet());

                            fCount++;
                            
                            trng2 = getCell(getRow(sr.getSheet(),sr.getCellRangeAddress().getFirstRow() - 1), sr.getCellRangeAddress().getFirstColumn());

                            rng2 = rng.getSheet().getRow(sr.getCellRangeAddress().getFirstRow() - 1).getCell(rng.getColumnIndex());

                            if (srcValue != null && srcValue.length() > 0)
                            	setImageValue(rng2, srcValue);
                            else
                            	SetValue(rng2, value);
                        } else {
                            rng2 = rng.getSheet().getRow(sr.getCellRangeAddress().getFirstRow() - cCount--).getCell(rng.getColumnIndex());
                            if (srcValue != null && srcValue.length() > 0)
                            	setImageValue(rng2, srcValue);
                            else 
                            	SetValue(rng2, value);
                        }
                    }
                }
                continue;
            }
            type = ptNode.getName();
            if ("Value".equals(type)) {
                if (taskTable != null) {
                	taskTable.setProgressValue(++currentValue);
                }

                firstColumn = true;
                fCount = 0;
                ptChildItem = (Attribute) ptChildAtr.get(0);
                value = ptChildItem.getValue();

            	SheetRange sr = (SheetRange)idToCell.get(tpid);
                trng = getCell(getRow(sr.getSheet(),sr.getCellRangeAddress().getFirstRow()), sr.getCellRangeAddress().getFirstColumn());

                trng2 = trng;
                startAddress = new CellRangeAddress(trng2.getRowIndex(), trng2.getRowIndex(), trng2.getColumnIndex(), trng2.getColumnIndex()).formatAsString();
                startRow = trng2.getRowIndex();
                String buf = treeMap.get(tpid);
                if (buf.length() == 0 || HasData(childCount, ptChildNodes)) {
                    InsertTreeNodeValue(value, tpid, level, false);
                    firstRowColumn = true;
                    ProcessRowColumns(tpid, childCount, ptChildNodes);
                    ProcessNodes(pid, level + 1, tpid, childCount, ptChildNodes, wb);
                    AddSummary(level, tpid, startRow);
                    AddCount(level, tpid, startAddress);
                    AddTotal(level, tpid, startAddress);
                    AddFree(level, tpid, startRow);
                    AddStatTable(level, tpid, value);
                }
            }
            if ("TreeColumn".equals(type)) {
                ptChildItem = (Attribute) ptChildAtr.get(0);
                value = ptChildItem.getValue();
                int inpid = Integer.parseInt(value);
                ProcessNodes(inpid, level + 1, inpid, childCount, ptChildNodes, wb);
            }
            if ("Column".equals(type) || "ConsColumn".equals(type)) {
                ptChildItem = (Attribute) ptChildAtr.get(0);
                value = ptChildItem.getValue();
                int inpid = Integer.parseInt(value);

                firstConsValue = 0;
                ProcessNodes(inpid, level + 1, tpid, childCount, ptChildNodes, wb);

                firstColumn = false;
                cCount = fCount;
            }
        }
    }

    private static void copy(Cell rng1, Cell rng2) {
        CellStyle cs1 = rng1.getSheet().getWorkbook().getCellStyleAt(rng2.getCellStyle().getIndex());
        rng1.setCellStyle(cs1);
    }

    private void setImageValue(Cell cell, String value) {
    	setImageValue(cell, value, null, null);
    }
    
    private void setImageValue(Cell cell, String value, String align, String valign) {
        Sheet wsh = cell.getSheet();

        if (value != null && value.length() > 0) {
            Drawing patriarch = wsh.createDrawingPatriarch();

            List<BufferedImage> images = new ArrayList<>();
            StringTokenizer st = new StringTokenizer(value, "|");

        	while (st.hasMoreTokens()) {
        		String filePath = st.nextToken();
        		
        		BufferedImage img = ImageUtil.loadImageFromPDF(filePath);
        		if (img != null) {
        			images.add(img);
        			File f = ImageUtil.getImageFile(img);
        			value = f.getAbsolutePath();
        		}
        		else
        			images.add(ImageUtil.loadImage(filePath));
        	}
        	
        	BufferedImage img = (images.size() > 1) ? ImageUtil.combineImages(0, images) : images.get(0);
        		
    		int width = img.getWidth();
        	int height = img.getHeight();
        	
        	int left = 0, top = 0;
        	if ("center".equals(align))
        		left -= width/2;
        	else if ("right".equals(align))
        		left -= width;
        	if ("center".equals(valign))
        		top -= height/2;
        	else if ("bottom".equals(valign))
        		top -= height;

        	int index = getPicIndex(wsh.getWorkbook(), img);
            if (Constants.NEED_EXPAND_EXCEL_CELL) {
                XSSFClientAnchor anchor = new XSSFClientAnchor(0,0,width*XSSFShape.EMU_PER_PIXEL,height*XSSFShape.EMU_PER_PIXEL,(short)cell.getColumnIndex(), cell.getRowIndex(),(short)cell.getColumnIndex(),cell.getRowIndex());
                anchor.setAnchorType(1);
                patriarch.createPicture(anchor, index);
        		cell.getRow().setHeight((short)(height * 15));
        		wsh.setColumnWidth(cell.getColumnIndex(), width * 36);
        	} else {
                ClientAnchor anchor = null;
                if (cell instanceof HSSFCell) {
                	anchor = wsh.getWorkbook().getCreationHelper().createClientAnchor();
                	anchor.setRow1(cell.getRowIndex());
                	anchor.setDx1(left);
                	anchor.setCol1(cell.getColumnIndex());
                	anchor.setDy1(top);
                	
                	normalizeAnchor(wsh, anchor);
                } else {
                	anchor = new XSSFClientAnchor(left*XSSFShape.EMU_PER_PIXEL, top*XSSFShape.EMU_PER_PIXEL, width*XSSFShape.EMU_PER_PIXEL, height*XSSFShape.EMU_PER_PIXEL,
                			cell.getColumnIndex(), cell.getRowIndex(), cell.getColumnIndex(), cell.getRowIndex());
                	normalizeAnchor(wsh, anchor);
            	}
                anchor.setAnchorType(1);
                Picture p = patriarch.createPicture(anchor, index);
                p.resize();
        	}
        }
	}
    
	private void normalizeAnchor(Sheet wsh, ClientAnchor anchor) {
		int c = anchor.getCol1();
		int dx = anchor.getDx1();
		
		while (dx < 0 && c > 0) {
			c--;
			if (!wsh.isColumnHidden(c)) {
				int w = columnWidthToEMU(wsh.getColumnWidth(c));
				if (anchor instanceof HSSFClientAnchor) w /= XSSFShape.EMU_PER_PIXEL;
				dx += w;
			}
		}
		if (c == 0) {
			while (wsh.isColumnHidden(c)) {
				c++;
			}
		}
		anchor.setCol1(c);
		anchor.setDx1(dx);
			
		int r = anchor.getRow1();
		int dy = anchor.getDy1();
		while (dy < 0 && r > 0) {
			r--;
			Row row = wsh.getRow(r);
			if (!row.getZeroHeight()) {
				float h = row.getHeightInPoints() * XSSFShape.EMU_PER_POINT;
				if (anchor instanceof HSSFClientAnchor) h /= XSSFShape.EMU_PER_PIXEL;
				dy += h;
			}
		}
		if (r == 0) {
			while (wsh.getRow(r).getZeroHeight()) {
				r++;
			}
		}
		anchor.setRow1(r);
		anchor.setDy1(dy);
	}
	
    /**
     * @param columnWidth specified in 256ths of a standard character
     * @return equivalent EMUs
     */
    public static int columnWidthToEMU(int columnWidth) {
        return charactersToEMU(columnWidth / 256d);
    }

    public static final float DEFAULT_CHARACTER_WIDTH = 7.0017f;
    public static final int EMU_PER_CHARACTER = (int) (XSSFShape.EMU_PER_PIXEL * DEFAULT_CHARACTER_WIDTH);
    
    public static int charactersToEMU(double characters) {
        return (int) characters * EMU_PER_CHARACTER;
    }

    public static int getPicIndex(Workbook wb, final BufferedImage img) {
		int index = -1;
		try {
			byte[] picData = ImageUtil.getImageData(img);
			index = wb.addPicture(picData, HSSFWorkbook.PICTURE_TYPE_PNG);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return index;
	}

	public static int getPicIndex(Workbook wb, final String path){
        int index = -1;
        try {
	        byte[] picData = null;
	        File pic = new File(path);
	        long length = pic.length(  );
	        picData = new byte[ ( int ) length ];
	        FileInputStream picIn = new FileInputStream( pic );
	        picIn.read( picData );
	        picIn.close();
	        index = wb.addPicture( picData, HSSFWorkbook.PICTURE_TYPE_JPEG );
        } catch (IOException e) {
            e.printStackTrace();
        }  catch (Exception e) {
            e.printStackTrace();
        } 
        return index;
    }
    
    private Dimension getImageDim(final String path) {
        Dimension result = null;
        try {
	        ImageInputStream ios = new FileImageInputStream(new File(path));
	        Iterator<ImageReader> iter = ImageIO.getImageReaders(ios);
	        if (iter.hasNext()) {
	            ImageReader reader = iter.next();
                ImageInputStream stream = new FileImageInputStream(new File(path));
                reader.setInput(stream);
                int width = reader.getWidth(reader.getMinIndex());
                int height = reader.getHeight(reader.getMinIndex());
                reader.dispose();
                result = new Dimension(width, height);
	        } else {
	            System.err.println("No reader found for given image");
	        }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void SetValue(Cell cell, String value) {
        if (value == null || value.length() == 0) return;
        	
        CellStyle newCellStyle = null;
        
        Workbook wb = cell.getSheet().getWorkbook();
        Font f = wb.getFontAt(cell.getCellStyle().getFontIndex());

        value = value.replaceAll("\\\r", "");
        
        if (value.startsWith("[left]")) {
        	value = value.substring(6);
    		newCellStyle = wb.createCellStyle();
    		newCellStyle.cloneStyleFrom(cell.getCellStyle());
    		newCellStyle.setAlignment(CellStyle.ALIGN_LEFT);
        }

    	List<Integer> boldPos = new ArrayList<Integer>();
    	StringBuilder sb = new StringBuilder();

    	String temp = value;
        try {
            int style = ((f.getBoldweight() >= Font.BOLDWEIGHT_BOLD) ? java.awt.Font.BOLD : 0) 
            		| ((f.getItalic()) ? java.awt.Font.ITALIC : 0);
            
            java.awt.Font currFont = new java.awt.Font(f.getFontName(), style, f.getFontHeight());

            java.awt.font.FontRenderContext frc = new java.awt.font.FontRenderContext(null, true, true);

            int nextPos = 0;
            int lineCnt = 0;
            
            int sheetIndex = cell.getSheet().getWorkbook().getSheetIndex(cell.getSheet());

            int mergedCellWidth = 0;
            float h2 = cell.getRow().getHeightInPoints();
            float h3 = 0f;

            MultiMap<Integer, CellRangeAddress> sheetMergedRegionMap = mergedRegionMap.get(sheetIndex);

            List<CellRangeAddress> crs = sheetMergedRegionMap.get(cell.getRowIndex());

            if (crs != null) {
	            for (CellRangeAddress cr : crs) {
		            if (cr.getFirstColumn() == cell.getColumnIndex()) {
		                for (int j = cr.getFirstColumn(); j <= cr.getLastColumn(); j++) {
		                	mergedCellWidth += cell.getSheet().getColumnWidth(j);
		                }
		                
		                for (int j = cr.getFirstRow() + 1; j <= cr.getLastRow(); j++) {
		                	h3 += cell.getSheet().getRow(j).getHeightInPoints();
		                }
		            }
	            }
            }
            if (mergedCellWidth != 0) { 
                mergedCellWidth = (int) (mergedCellWidth / 2.56) + 30;
                
                StringTokenizer st = new StringTokenizer(value, "\n");
                while (st.hasMoreTokens()) {
                	String val = st.nextToken();
                	
                    java.text.AttributedString attrStr = new java.text.AttributedString(val);
                    attrStr.addAttribute(java.awt.font.TextAttribute.FONT, currFont);

                    java.awt.font.LineBreakMeasurer measurer = new java.awt.font.LineBreakMeasurer(attrStr.getIterator(),
                    		frc);

            		while (measurer.getPosition() < val.length()) {
                        nextPos = measurer.nextOffset(mergedCellWidth); // mergedCellWidth is
                        lineCnt++;
                        measurer.setPosition(nextPos);
                    }
                }
                
                float h1 = 1.32f * lineCnt * f.getFontHeightInPoints();//cell.getSheet().getDefaultRowHeight());
                
                if (h1 > h2 + h3)
                	cell.getRow().setHeightInPoints(h1 - h3);
            }
            
            Pattern p = Pattern.compile("\\[b\\](.*?\\s*?)*?\\[/b\\]");
            Matcher m = p.matcher(value);

            int lastPos = 0;
	        for (int i = 0; m.find(i); i = m.end()) {
	            sb.append(value.substring(lastPos, m.start()));
	
	            boldPos.add(sb.length());

	            sb.append(value.substring(m.start() + 3, m.end() - 4));

	            boldPos.add(sb.length());

	            lastPos = m.end();
	        }
	        sb.append(value.substring(lastPos, value.length()));

            short format = wb.getCellStyleAt(cell.getCellStyle().getIndex()).getDataFormat();
            if (DateUtil.isCellDateFormatted(cell)) {
                try {
                	Calendar c = Calendar.getInstance();
                	if (boldPos.size() == 0) {
                		c.setTime(df.parse(temp));
                		cell.setCellValue(c);
                	} else {
                		c.setTime(df.parse(sb.toString()));
                		cell.setCellValue(c);
                		
                		if (newCellStyle == null) {
                			newCellStyle = wb.createCellStyle();
                			newCellStyle.cloneStyleFrom(cell.getCellStyle());
                		}
                		Font ft = cloneFont(f, wb);
                        ft.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
                        newCellStyle.setFont(ft);
                	}
                } catch (Exception e2) {
                    cell.setCellValue(temp);
                }
            } else if (format == 0 || format == 49) {
            	if (boldPos.size() == 0) {
	    	        cell.setCellValue(temp);
            	} else {
	        		Font ft = cloneFont(f, wb);
	                ft.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
	                
	                XSSFRichTextString rstr = new XSSFRichTextString(sb.toString());
                	rstr.applyFont(0, sb.length(), f);
                	
	    	        for (int i = 0; i < boldPos.size(); i+=2) {
	                	rstr.applyFont(boldPos.get(i), boldPos.get(i + 1), ft);
	    	        }
	    	        cell.setCellValue(rstr);
            	}
            } else {
            	if (boldPos.size() == 0) {
                	cell.setCellValue(Double.parseDouble(temp));
            	} else {
                	cell.setCellValue(Double.parseDouble(sb.toString()));
            		if (newCellStyle == null) {
	            		newCellStyle = wb.createCellStyle();
	            		newCellStyle.cloneStyleFrom(cell.getCellStyle());
            		}
            		Font ft = cloneFont(f, wb);
                    ft.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
                    newCellStyle.setFont(ft);
            	}
            }
            
            if (newCellStyle != null) {
                cell.setCellStyle(newCellStyle);
            }
        } catch (Exception e1) {
            temp = temp.replaceAll("\\.", ",");
            try {
            	if (boldPos.size() == 0) {
                	cell.setCellValue(Double.parseDouble(temp));
            	} else {
                	cell.setCellValue(Double.parseDouble(sb.toString().replaceAll("\\.", ",")));
            		if (newCellStyle == null) {
	            		newCellStyle = wb.createCellStyle();
	            		newCellStyle.cloneStyleFrom(cell.getCellStyle());
            		}
            		Font ft = cloneFont(f, wb);
                    ft.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
                    newCellStyle.setFont(ft);
            	}
            } catch (Exception e2) {
                if (value.startsWith("-")) value = value.substring(1);
            	if (boldPos.size() == 0) {
                	cell.setCellValue(value);
            	} else {
                	cell.setCellValue(sb.substring(1).toString());

	        		Font ft = cloneFont(f, wb);
	                ft.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

	                XSSFRichTextString rstr = new XSSFRichTextString(sb.toString());
	    	        for (int i = 0; i < boldPos.size(); i+=2) {
	                	rstr.applyFont(boldPos.get(i) > 0 ? boldPos.get(i)-1 : 0, boldPos.get(i + 1)-1, ft);
	    	        }
	    	        cell.setCellValue(rstr);
            	}
            }
            if (newCellStyle != null) {
                cell.setCellStyle(newCellStyle);
            }
        }
    }

    private Font cloneFont(Font f, Workbook wb) {
		Font ft = wb.createFont();
		ft.setBoldweight(f.getBoldweight());
		ft.setColor(f.getColor());
		ft.setFontHeight(f.getFontHeight());
		ft.setFontName(f.getFontName());
		ft.setItalic(f.getItalic());
		ft.setStrikeout(f.getStrikeout());
		ft.setTypeOffset(f.getTypeOffset());
		ft.setUnderline(f.getUnderline());
		
		return ft;
    }
    
    private int HowAddSummary(int level, int tpid) {
        String tpidLev = tpid + "," + level;
        if (!totalMap.containsKey(tpidLev)) return 0;
        return 1;
    }

    private int AddSummary(int level, int tpid, int startRow) {
        Cell rng, rng2, rng3;

        String tpidLev = tpid + "," + level;
        String pids = totalMap.get(tpidLev);

        if (pids == null) return 0;

        int l = pids.indexOf('|');

        String sum = pids.substring(l + 1);
        pids = pids.substring(0, l);

        InsertTreeNodeValue(sum, tpid, level, true);
        SheetRange sr = (SheetRange)idToCell.get(tpid);
        int endRow = sr.getCellRangeAddress().getFirstRow();

        while (pids.length() > 0) {
            int end = pids.indexOf(',');
            int curPid;
            if (end > -1) {
                curPid = Integer.parseInt(pids.substring(0, end));
                pids = pids.substring(end + 1);
            } else {
                curPid = Integer.parseInt(pids);
                pids = "";
            }

            rng = (Cell)idToCell.get(curPid);
            rng2 = rng.getSheet().getRow(endRow - 1).getCell(rng.getColumnIndex());
            rng3 = rng.getSheet().getRow(endRow - 2).getCell(rng.getColumnIndex());

            char colChar = (char) ('A' + rng3.getColumnIndex());
            
            String f1 = "SUBTOTAL(9," + colChar + (startRow + 1) + ":" + colChar + (endRow - 1)  + ")";
            rng2.setCellFormula(f1);
        }
        return 1;
    }

    private void InsertTreeNodeValue(String value, int tpid, int level, boolean copy) {
        Cell trng2, trng3;

        Object range = idToCell.get(tpid);

        if (range == null) return;

        SheetRange sr = (SheetRange)range;

        shiftCells(sr.getCellRangeAddress().getFirstRow(), sr.getCellRangeAddress().getFirstColumn(), sr.getCellRangeAddress().getLastColumn(), 1, sr.getSheet());

        trng2 = getCell(getRow(sr.getSheet(),sr.getCellRangeAddress().getFirstRow() - 1), sr.getCellRangeAddress().getFirstColumn());
        
        trng3 = getCell(getRow(sr.getSheet(),sr.getCellRangeAddress().getFirstRow() - 1), sr.getCellRangeAddress().getFirstColumn() + level);

        if (fastReport) {
            Font f = trng3.getSheet().getWorkbook().getFontAt( trng3.getCellStyle().getFontIndex() );
            f.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            trng3.getCellStyle().setFont(f);
        }
        trng3.setCellValue(value);

        if (copy) {
            Font fnt2 = trng2.getSheet().getWorkbook().getFontAt(trng2.getCellStyle().getFontIndex());
            Font fnt3 = trng3.getSheet().getWorkbook().getFontAt(trng3.getCellStyle().getFontIndex());

            fnt2.setBoldweight(fnt3.getBoldweight());
            fnt2.setItalic(fnt3.getItalic());
            fnt2.setUnderline(fnt3.getUnderline());
            fnt2.setFontName(fnt3.getFontName());
            fnt2.setFontHeight(fnt3.getFontHeight());
            fnt2.setColor(fnt3.getColor());
            fnt2.setBoldweight(fnt3.getBoldweight());
            trng2.getCellStyle().setFillBackgroundColor(trng3.getCellStyle().getFillBackgroundColor());
        }
    }

    private boolean HasData(int count, List tempNodes) {
        List ptChildNodes;
        Element ptNode;
        String name;
        int childCount;

        for (int n = 0; n < count; n++) {
            ptNode = (Element) tempNodes.get(n);
            name = ptNode.getName();
            if ("Column".equals(name) || "ConsValue".equals(name)) return true;

            ptChildNodes = ptNode.getChildren();
            childCount = ptChildNodes.size();

            if (HasData(childCount, ptChildNodes)) return true;
        }

        return false;
    }

    private void AddStatTable(int level, int tpid, String startAddr) {
        Cell rng, rng2, rng3;
        Cell crng, crng2;
        Cell trng, trng2;
        String tpidLev = tpid + "," + level;
        String ost = statMap.get(tpidLev);

        if (ost == null) return;
        int start = ost.indexOf('|');
        int id = Integer.parseInt(ost.substring(0, start));
        rng = (Cell)idToCell.get(id);
        trng = (Cell)idToCell.get(tpid);
        String texts = ost.substring(start + 1) + '|';

        int k = 0;
        while (texts.length() > 0) {
            k++;
            int first = texts.indexOf('|');
            int second = texts.indexOf('|', first + 1);
            String textToFind = texts.substring(0, first);
            int colNum = Integer.parseInt(texts.substring(first + 1, second));
            texts = texts.substring(second + 1);
            crng = (Cell)idToCell.get(colNum);
            String value = "1";
            int i = 0;
            while (value.length() > 0) {
                i--;
                crng2 = crng.getSheet().getRow(crng.getRowIndex() + i).getCell(crng.getColumnIndex());
                trng2 = trng.getSheet().getRow(trng.getRowIndex() + i).getCell(trng.getColumnIndex() + level);
                trng2 = trng;
                value = trng2.getStringCellValue();

                if (k == 1) {
                    rng2 = rng.getSheet().getRow(rng.getRowIndex() - 1).getCell(rng.getColumnIndex());
                    rng2.setCellValue(startAddr);
                }
                if (value.equals(textToFind)) {
                    rng2 = rng.getSheet().getRow(rng.getRowIndex() - 1).getCell(rng.getColumnIndex() + k);
                    rng2.setCellValue(crng2.getStringCellValue());
                }
            }
            if ("Formula".equals(textToFind)) {
                rng2 = rng.getSheet().getRow(rng.getRowIndex() - 1).getCell(rng.getColumnIndex() + k);
                rng3 = rng.getSheet().getRow(rng.getRowIndex()).getCell(rng.getColumnIndex() + k);
                rng3.getCellStyle().cloneStyleFrom(rng2.getCellStyle());
            }
        }
        RowRecordsAggregate rra = new RowRecordsAggregate();
        rra.insertRow(new RowRecord(rng.getRowIndex()));
    }

    private void AddCount(int level, int tpid, String startAddr) {
        Cell rng, rng2, rng3;

        String tpidLev = tpid + "," + level;
        String pids = countMap.get(tpidLev);

        if (pids == null) return;

        int l = pids.indexOf('|');
        String sum = pids.substring(l + 1);
        pids = pids.substring(0, l);
        InsertTreeNodeValue(sum, tpid, level, true);

        while (pids.length() > 0) {
            int end = pids.indexOf(',');
            int curPid;
            if (end > -1) {
                curPid = Integer.parseInt(pids.substring(0, end));
                pids = pids.substring(end + 1);
            } else {
                curPid = Integer.parseInt(pids);
                pids = "";
            }

            rng = (Cell)idToCell.get(curPid);
            rng2 = rng.getSheet().getRow(rng.getRowIndex() - 1).getCell(rng.getColumnIndex());
            rng3 = rng.getSheet().getRow(rng.getRowIndex() - 2).getCell(rng.getColumnIndex());
            String addr = new CellRangeAddress(rng3.getRowIndex(), rng3.getRowIndex(), rng3.getColumnIndex(), rng3.getColumnIndex()).formatAsString();

            int ifhas = startAddr.indexOf(':', 1);
            String startAddress = (ifhas > -1) ? startAddr.substring(0, ifhas) : startAddr;

            end = startAddress.indexOf('$', 1);
            int end2 = addr.indexOf('$', 1);
            startAddress = startAddress.substring(end);
            startAddress = addr.substring(0, end2) + startAddress;
            String formula = "=ПРОМЕЖУТОЧНЫЕ.ИТОГИ(3; " + startAddress + ":" + addr + ")";
            rng2.setCellFormula(formula);
        }
    }

    private void AddTotal(int level, int tpid, String startAddr) {
        Cell rng, rng2, rng3;
        String pids = opMap.get(tpid);
        if (pids == null) return;

        InsertTreeNodeValue("Всего:", tpid, level, true);
        String seps = " ,\t\n";
        StringTokenizer st = new StringTokenizer(pids, seps);
        while (st.hasMoreTokens()) {
            int curPid = Integer.parseInt(st.nextToken());
            String operation = st.nextToken();
            rng = (Cell)idToCell.get(curPid);

            if (tpid == 0) {
                RowRecordsAggregate rra = new RowRecordsAggregate();
                rra.insertRow(new RowRecord(rng.getRowIndex()));
            }

            rng2 = rng.getSheet().getRow(rng.getRowIndex() - 1).getCell(rng.getColumnIndex());
            rng3 = rng.getSheet().getRow(rng.getRowIndex() - 2).getCell(rng.getColumnIndex());
            String addr = new CellRangeAddress(rng3.getRowIndex(), rng3.getRowIndex(), rng3.getColumnIndex(), rng3.getColumnIndex()).formatAsString();

            int ifhas = startAddr.indexOf(':', 1);
            String startAddress = (ifhas > -1) ? startAddr.substring(0, ifhas) : startAddr;

            int end = startAddress.indexOf('$', 1);
            int end2 = addr.indexOf('$', 1);
            startAddress = startAddress.substring(end);
            startAddress = addr.substring(0, end2) + startAddress;
            String formula = "=ПРОМЕЖУТОЧНЫЕ.ИТОГИ(" + operation + "; " + startAddress + ":" + addr + ")";

            if (fastReport) {
                Font f = rng2.getSheet().getWorkbook().getFontAt( rng2.getCellStyle().getFontIndex() );
                f.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
                rng2.getCellStyle().setFont(f);
            }
            rng2.setCellType(0);
            rng2.setCellFormula(formula);
        }
    }

    private void AddFree(int level, int tpid, int startRow) {
        Cell rng, rng2, rng3;

        String tpidLev = tpid + "," + level;
        String pids = freeMap.get(tpidLev);

        if (pids == null) return;

        int l = pids.indexOf('|');
        String sums = pids.substring(l + 1);
        pids = pids.substring(0, l);

        l = sums.indexOf('|');
        String sum1 = sums.substring(0, l);
        sums = sums.substring(l + 1);

        l = sums.indexOf('|');
        String sum2 = sums.substring(0, l);
        String sum3 = sums.substring(l + 1);

        InsertTreeNodeValue(sum1, tpid, level, true);

        int end = pids.indexOf(',');
        int pid1 = Integer.parseInt(pids.substring(0, end));
        int pid2 = Integer.parseInt(pids.substring(end + 1));

        SheetRange sr = (SheetRange)idToCell.get(tpid);
        int endRow = sr.getCellRangeAddress().getFirstRow();

        rng = (Cell)idToCell.get(pid1);
        rng2 = rng.getSheet().getRow(endRow - 1).getCell(rng.getColumnIndex());
        rng3 = rng.getSheet().getRow(endRow - 2).getCell(rng.getColumnIndex());

        char colChar = (char) ('A' + rng3.getColumnIndex());
        
        String f1 = "SUBTOTAL(3," + colChar + (startRow + 1) + ":" + colChar + (endRow - 1)  + ")";
        rng2.setCellFormula(f1);

        InsertTreeNodeValue(sum2, tpid, level, true);
        rng = (Cell)idToCell.get(pid2);
        rng2 = (Cell)idToCell.get(pid1);

        rng2 = rng2.getSheet().getRow(endRow).getCell(rng2.getColumnIndex());
        rng3 = rng.getSheet().getRow(endRow - 1).getCell(rng.getColumnIndex());

        colChar = (char) ('A' + rng3.getColumnIndex());
        
        String f2 = "SUBTOTAL(3," + colChar + (startRow + 1) + ":" + colChar + (endRow - 1) + ")";
        rng2.setCellFormula(f2);

        InsertTreeNodeValue(sum3, tpid, level, true);
        rng2 = (Cell)idToCell.get(pid1);
        rng2 = rng2.getSheet().getRow(endRow + 1).getCell(rng2.getColumnIndex());

        rng2.setCellFormula(f1 + " - " + f2);
    }

    private void ProcessRowColumns(int tpid, int count, List tempNodes) {
        Element ptNode;
        Attribute ptChildItem;
        List ptChildAtr;
        String value;
        String name;
        Cell rng, rng2, trng, trng2;

        for (int n = 0; n < count; n++) {
            ptNode = (Element) tempNodes.get(n);
            ptChildAtr = ptNode.getAttributes();
            name = ptNode.getName();

            if ("RowColumn".equals(name) && tpid > 0) {
                ptChildItem = (Attribute) ptChildAtr.get(0);
                value = ptChildItem.getValue();

                int id = Integer.parseInt(value);

                rng = (Cell)idToCell.get(id);
                if (rng == null) continue;

                ptChildItem = (Attribute) ptChildAtr.get(1);
                value = ptChildItem.getValue();

                if (firstRowColumn) {
                    trng = (Cell)idToCell.get(tpid);

                    RowRecordsAggregate rra = new RowRecordsAggregate();
                    rra.insertRow(new RowRecord(trng.getRowIndex()));

                    trng2 = trng.getSheet().getRow(trng.getRowIndex() - 1).getCell(trng.getColumnIndex());
                    trng.getCellStyle().cloneStyleFrom(trng2.getCellStyle());

                    idToCell.put(tpid, trng);

                    rng2 = rng.getSheet().getRow(rng.getRowIndex() - 1).getCell(rng.getColumnIndex());
                    rng2.setCellValue(value);

                } else {
                    rng2 = rng.getSheet().getRow(rng.getRowIndex() - 1).getCell(rng.getColumnIndex());
                    rng2.setCellValue(value);
                }
                firstRowColumn = false;
            }
        }
    }

    public static void main(String[] args) {
        try {
            new ReportWrapperPOI("D:\\tmp\\egkn\\reps\\xxx.docx", "D:\\tmp\\egkn\\reps\\sss.xml", "", "", "", Constants.MSWORD_EDITOR, true).print();
        	//new ReportWrapperPOI("D:\\tmp\\kyzmet\\xxx.xlsx", "D:\\tmp\\kyzmet\\xxx.xml", "", "", "", Constants.MSEXCEL_EDITOR, true).print();
            //new ReportWrapperPOI("C:\\Users\\User\\Downloads\\1\\xxx.docx", "C:\\Users\\User\\Downloads\\1\\xxx.xml", "", "", "", Constants.MSWORD_EDITOR, true).print();
            //new ReportWrapper("D:\\WORK\\or3final\\doc\\xxx2.xls", "D:\\WORK\\or3final\\doc\\xxx.xml", "", "", "", 1, true).print();
            //new ReportWrapper("D:\\WORK\\or3final\\doc\\xxx3.xls", "D:\\WORK\\or3final\\doc\\xxx.xml", "", "", "", 1, true).print();
            ///new ReportWrapper("D:\\WORK\\or3final\\doc\\xxx4.xls", "D:\\WORK\\or3final\\doc\\xxx.xml", "", "", "", 1, true).print();
            //new ReportWrapper("D:\\WORK\\or3final\\doc\\xxx5.xls", "D:\\WORK\\or3final\\doc\\xxx.xml", "", "", "", 1, true).print();
        } catch (Exception e) {
            e.printStackTrace();
        }

/*        try {
            InputStream fis = new FileInputStream("workbook.xlsx");
            XSSFWorkbook oDoc = new XSSFWorkbook(fis);
            fis.close();

            FileOutputStream fileOut = new FileOutputStream("workbook.xls");
            oDoc.write(fileOut);
            fileOut.close();
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
*/    }

    static void calculateFormulas(Workbook wb) {
        //System.out.println("start calculate formulas: " + new Date());

        FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();

    	try {
    		evaluator.evaluateAll();
    		evaluator.clearAllCachedResultValues();
    	} catch (Exception e) {
    		System.out.println(e.getMessage());
    	}
        //System.out.println("end calculate formulas: " + new Date());
    }
    
    private static void increaseFormulas(Sheet sh, int row, int n) {
        //System.out.println("start increase formulas: " + new Date());

        int colCount = 0;
        String rs = String.valueOf(row + 1);
        String rs2 = String.valueOf(row + n);
        Pattern p = Pattern.compile(":[A-Z]+" + rs);
        Pattern p2 = Pattern.compile("[A-Z]+" + rs2);

        for (Iterator<Row> ri = sh.rowIterator(); ri.hasNext(); ) {
            Row r = ri.next();
            
            int rowNum = r.getRowNum();

            colCount = (r.getLastCellNum() > colCount) ? r.getLastCellNum() : colCount;

            if (rowNum < row || rowNum > row + n) {
	            for (int ci = 0; ci < colCount; ci++) {
	                Object range = getCell(r, ci);
	                Cell cell = (Cell)range;
	
	                if (cell != null && cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
	                    String formula = cell.getCellFormula();
	                    boolean changed = false;
	                    String res = null;
	                    
	                    Matcher m = p.matcher(formula);
	
	                    int lastPos = 0;
	                    res = "";
	
	                    for (int k = 0; m.find(k); k = m.end()) {
	                        if (m.end() == formula.length() || !Character.isDigit(formula.charAt(m.end()))) {
	                            res += formula.substring(lastPos, m.end() - rs.length()) + (row + 1 + n);
	                        } else {
	                            res += formula.substring(lastPos, m.end());
	                        }
	
	                        lastPos = m.end();
	                        changed = true;
	                    }
	                    res += formula.substring(lastPos);
	                    formula = res;
	                    
	                    m = p2.matcher(formula);
	                    lastPos = 0;
	                    res = "";
	
	                    for (int k = 0; m.find(k); k = m.end()) {
	                        if (m.end() == formula.length() || !Character.isDigit(formula.charAt(m.end()))) {
	                            res += formula.substring(lastPos, m.end() - rs2.length()) + (row);
	                        } else {
	                            res += formula.substring(lastPos, m.end());
	                        }
	
	                        lastPos = m.end();
	                        changed = true;
	                    }
	                    res += formula.substring(lastPos);
	
	                    if (changed)
	                    	cell.setCellFormula(res);
	                }
	            }
            }
        }
        //System.out.println("end increase formulas: " + new Date());
    }

    private static void decreaseFormulas(Sheet sh, int row) {
        int colCount = 0;

        for (Iterator<Row> ri = sh.rowIterator(); ri.hasNext(); ) {
            Row r = ri.next();

            colCount = (r.getLastCellNum() > colCount) ? r.getLastCellNum() : colCount;

            for (int ci = 0; ci < colCount; ci++) {
                Object range = getCell(r, ci);
                Cell cell = (Cell)range;

                if (cell != null && cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                    String formula = cell.getCellFormula();

                    String rs = String.valueOf(row+1);
                    Pattern p = Pattern.compile(":[A-Z]+" + rs);
                    Matcher m = p.matcher(formula);

                    int lastPos = 0;
                    String res = "";

                    for (int k = 0; m.find(k); k = m.end()) {

                        if (m.end() == formula.length() || !Character.isDigit(formula.charAt(m.end()))) {
                            res += formula.substring(lastPos, m.end() - rs.length()) + row;
                        } else {
                            res += formula.substring(lastPos, m.end());
                        }

                        lastPos = m.end();
                    }
                    
                    if (lastPos > 0) {
                    	res += formula.substring(lastPos);
                    	cell.setCellFormula(res);
                    }
                }
            }
        }
    }

    private void deleteRows(int row, int n, Sheet sh) {
    	int k = sh.getLastRowNum();
    	if (row + n > k) k = row + n;

    	for (int rowNum = row; rowNum < row + n; rowNum++) {
    		Row r = getRow(sh, rowNum);
    		int colCount = r.getLastCellNum();

	        for (int ci = 0; ci <= colCount; ci++) {
	            Object range = getCell(r, ci);
	            Cell cell = (Cell)range;
	
	            if (cell != null && cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
	            	cell.setCellFormula(null);
	            }
	        }
	
	        sh.removeRow(r);

	        MultiMap<Integer, CellRangeAddress> sheetMergedRegionMap = mergedRegionMap.get(sh.getWorkbook().getSheetIndex(sh));
            sheetMergedRegionMap.remove(rowNum);
    	}
        shiftMergedRegions(row, -n, sh);

    	int lastRow = (sh.getLastRowNum() < row + n) ? row + n : sh.getLastRowNum();
		sh.shiftRows(row + n, lastRow, -n, true, false);
    	
    	decreaseFormulas(sh, row);
    }
    
    // сдвигаем объединения ячеек в памяти при добавлении/удалении строк
    private void shiftMergedRegions(int row, int n, Sheet sh) {
        MultiMap<Integer, CellRangeAddress> sheetMergedRegionMap = mergedRegionMap.get(sh.getWorkbook().getSheetIndex(sh));
    	MultiMap<Integer, CellRangeAddress> shiftedMergedRegionMap = new MultiMap<>();
        for (int crRow : sheetMergedRegionMap.keySet()) {
        	if (crRow > row) {
            	List<CellRangeAddress> crs = sheetMergedRegionMap.get(crRow);
        		shiftedMergedRegionMap.put(crRow, crs);
                
                for (CellRangeAddress cr : crs) {
                	cr.setFirstRow(crRow + n);
                	cr.setLastRow(cr.getLastRow() + n);
                }
        	}
        }
        
        for (int crRow : shiftedMergedRegionMap.keySet()) {
        	sheetMergedRegionMap.remove(crRow);
        }
        for (int crRow : shiftedMergedRegionMap.keySet()) {
        	sheetMergedRegionMap.put(crRow + n, shiftedMergedRegionMap.get(crRow));
        }
    }
    
    // добавляем все новые объединения ячеек в Excel
    private void addMergedRegions(Sheet sh) {
        MultiMap<Integer, CellRangeAddress> sheetMergedRegionMap = mergedRegionMap.get(sh.getWorkbook().getSheetIndex(sh));
        
        int mrsCount = sh.getNumMergedRegions();
        
        for (int i = 0; i<mrsCount; i++) {
            CellRangeAddress cr = sh.getMergedRegion(i);
            List<CellRangeAddress> crs = sheetMergedRegionMap.get(cr.getFirstRow());
            
            if (crs != null) {
	            for (int k = crs.size() - 1; k >= 0; k--) {
	            	CellRangeAddress cr2 = crs.get(k);
	            	if (cr2.getFirstRow() == cr.getFirstRow() && cr2.getFirstColumn() == cr.getFirstColumn())
	            		crs.remove(k);
	            }
            }
        }

        for (int crRow : sheetMergedRegionMap.keySet()) {
        	List<CellRangeAddress> crs = sheetMergedRegionMap.get(crRow);
            
            for (CellRangeAddress cr : crs) {
                sh.addMergedRegion(cr);
            }
        }
    }

    private void shiftCells(int row, int col1, int col2, int n, Sheet sh) {
        //System.out.println("start shift cells: " + new Date());

        if (sh.getLastRowNum() < row + 1)
        	sh.createRow(row + 1);
        	
    	sh.shiftRows(row + 1, sh.getLastRowNum(), n, true, false);

        String rs = String.valueOf(row + 1);
        Pattern p = Pattern.compile("[A-Z]+" + rs);

        for (int i = col1; i <= col2; i++) {
            Row r1 = getRow(sh, row);
            Cell c1 = getCell(r1, i);

            for (int j = 1; j <= n; j++) {
            	Row r2 = getRow(sh, row + j);
            	Cell c2 = getCell(r2, i);

	            int type = c1.getCellType();
	            c2.setCellType(type);
	
	            copy(c2, c1);
	
	            switch (type) {
	                case Cell.CELL_TYPE_BOOLEAN:
	                    c2.setCellValue(c1.getBooleanCellValue());
	                    break;
	                case Cell.CELL_TYPE_FORMULA:
	                    String formula = c1.getCellFormula();
	
	                    Matcher m = p.matcher(formula);
	
	                    int lastPos = 0;
	                    String res = "";

	                    for (int k = 0; m.find(k); k = m.end()) {
	                        if (m.end() == formula.length() || !Character.isDigit(formula.charAt(m.end()))) {
	                            res += formula.substring(lastPos, m.end() - rs.length()) + (row+1+j);
	                        } else {
	                            res += formula.substring(lastPos, m.end());
	                        }

	                        lastPos = m.end();
	                    }
	                    
                    	res += formula.substring(lastPos);
                    	c2.setCellFormula(res);
	                    break;
	                case Cell.CELL_TYPE_NUMERIC:
	                    c2.setCellValue(c1.getNumericCellValue());
	                    break;
	                case Cell.CELL_TYPE_STRING:
	                    c2.setCellValue(c1.getStringCellValue());
	                    break;
	            }
	            //c1.setCellType(Cell.CELL_TYPE_BLANK);
            }
        }

        shiftRanges(row, n, sh);
        increaseFormulas(sh, row, n);

        MultiMap<Integer, CellRangeAddress> sheetMergedRegionMap = mergedRegionMap.get(sh.getWorkbook().getSheetIndex(sh));
        List<CellRangeAddress> crs = sheetMergedRegionMap.get(row);
        
        if (crs != null) {
	        for (CellRangeAddress cr : crs) {
	            if (cr.getFirstColumn() >= col1 && cr.getLastColumn() <= col2) {
	                for (int j = 1; j <= n; j++) {
		                CellRangeAddress cr2 = cr.copy();
		                cr2.setLastRow(row + j);
		                cr2.setFirstRow(row + j);
		                //sh.addMergedRegion(cr2);
		                
		                sheetMergedRegionMap.put(row + j, cr2);
	                }
	            }
	        }
        }
        //System.out.println("end shift cells: " + new Date());
    }
    
    private void shiftRanges(int row, int n, Sheet sh) {
    	for (Integer key : idToCell.keySet()) {
    		Object obj = idToCell.get(key);
    		if (obj instanceof SheetRange) {
	            SheetRange sr = (SheetRange)obj;
	            
	            if (sh.equals(sr.getSheet()) && sr.getCellRangeAddress().getFirstRow() >= row) {
		            sr.getCellRangeAddress().setFirstRow(sr.getCellRangeAddress().getFirstRow() + n);
		            sr.getCellRangeAddress().setLastRow(sr.getCellRangeAddress().getFirstRow() + n);
		            idToCell.put(key, sr);
	            }
    		}
    	}

    	shiftMergedRegions(row, n, sh);
    }
    
    private static Row getRow(Sheet sh, int i) {
        Row r = sh.getRow(i);
        if (r == null) r = sh.createRow(i);
        return r;
    }

    private static Cell getCell(Row r, int i) {
        Cell c = r.getCell(i);
        if (c == null) c = r.createCell(i);
        return c;
    }

    private void protectWorkbook(Workbook wb) {
    	if (pd != null && pd.length() > 0) {    	
            int wshsCount = wb.getNumberOfSheets();

            for (int w = 0; w < wshsCount; w++) { // Пробегаемся по всем листам в книге Excel
                Sheet wsh = wb.getSheetAt(w);
	        	protectWorksheet(wsh);
	        }
    	}
    }
    
    private void protectWorksheet(Sheet wsh) {
    	// 3 - только чтение
    	//if (pd != null && pd.length() > 0)
    }

    private void protectDocument(XWPFDocument oDoc) {
    	// 3 - только чтение
//    	if (pd != null && pd.length() > 0)
//    		Dispatch.call(oDoc, "Protect", new Variant(3), new Variant(false), pd);
    }

    private class SheetRange {
        private CellRangeAddress cellRangeAddress;
        private Sheet sheet;
        private boolean oneRow;

        private SheetRange(CellRangeAddress cellRangeAddress, Sheet sheet,
        		 			boolean oneRow) {
            this.cellRangeAddress = cellRangeAddress;
            this.sheet = sheet;
            this.oneRow = oneRow;
        }

        public CellRangeAddress getCellRangeAddress() {
            return cellRangeAddress;
        }

        public Sheet getSheet() {
            return sheet;
        }

		public boolean isOneRow() {
			return oneRow;
		}
    }
    
    private static class XmlObjectRange {
        private int id;
        private XmlObject object;
        private XmlObject parent;
        private String comment;

        private XmlObjectRange(int id, XmlObject object, XmlObject parent, String comment) {
        	this.id = id;
            this.object = object;
            this.parent = parent;
            this.comment = comment;
        }

        public int getId() {
            return id;
        }

        public XmlObject getObject() {
            return object;
        }

        public XmlObject getParent() {
            return parent;
        }

        public String getComment() {
            return comment;
        }
    }

    private static class RAttrs {
        STOnOff.Enum b = null;
        STUnderline.Enum u = null;
        STOnOff.Enum i = null;
        BigInteger sz = null, szCs = null;
        String ftAscii = null, ftAnsi = null;
        Object color;

        public RAttrs() {}
        
        public void readAttrs(CTR r) {
        	CTRPr rpr = r.getRPr();
            if (rpr == null) {
            	rpr = r.addNewRPr();
            }
            
            this.b = (rpr.getB() != null && rpr.getB().isSetVal()) ? rpr.getB().getVal() 
            		: (rpr.getB() != null) ? STOnOff.X_1 : null;
            this.u = (rpr.getU() != null && rpr.getU().isSetVal()) ? rpr.getU().getVal()
            		: (rpr.getU() != null) ? STUnderline.SINGLE : null;
            this.i = (rpr.getI() != null && rpr.getI().isSetVal()) ? rpr.getI().getVal() 
            		: (rpr.getI() != null) ? STOnOff.X_1 : null;
            
            this.sz = (rpr.getSz() != null) ? rpr.getSz().getVal() : null;
            this.szCs = (rpr.getSzCs() != null) ? rpr.getSzCs().getVal() : null;
            
            this.ftAscii = (rpr.getRFonts() != null) ? rpr.getRFonts().getAscii() : null;
            this.ftAnsi = (rpr.getRFonts() != null) ? rpr.getRFonts().getHAnsi() : null;
            
            this.color = (rpr.getColor() != null) ? rpr.getColor().getVal() : null;
        }
    }
    
    private static class PAttrs {
    	CTP p = null;
        int align = 0;
        BigInteger firstLine = null, left = null, spacingAfter = null, spacingLine = null;
        STLineSpacingRule.Enum lineRule = null;

        public PAttrs() {}
        
        public void readAttrs(CTP p) {
        	this.p = p;
        	CTPPr ppr = p.getPPr();
    		if (ppr != null) {
    			CTJc jc = ppr.getJc();
    			if (jc != null) {
    				if (jc.getVal().equals(STJc.LEFT))
    					this.align = ALIGN_LEFT;
    				else if (jc.getVal().equals(STJc.RIGHT))
    					this.align = ALIGN_RIGHT;
    				else if (jc.getVal().equals(STJc.BOTH))
    					this.align = ALIGN_JUSTIFY;
    				else if (jc.getVal().equals(STJc.CENTER))
    					this.align = ALIGN_CENTER;
    			}
    			
    			CTInd ind = ppr.getInd();
    			if (ind != null) {
    				BigInteger bi = ind.getFirstLine();
    				if (bi != null)
    					this.firstLine = bi;

    				bi = ind.getLeft();
    				if (bi != null)
    					this.left = bi;
    			}
    			
    			CTSpacing sp = ppr.getSpacing();
    			if (sp != null) {
    				BigInteger bi = sp.getAfter();
    				if (bi != null)
    					this.spacingAfter = bi;

    				bi = sp.getLine();
    				if (bi != null)
    					this.spacingLine = bi;

    				lineRule = sp.getLineRule();
    			}
            }
        }
    }

    private static class Fragment {
        private String text;
        
        private int align = -1;
        private int size = -1;
        private String color;
        
        private int type = -1;
        
        private List<Fragment> children = new ArrayList<>();
        private Fragment parent = null;
        
        public Fragment() {}
        
		public String getText() {
			return text;
		}
		public int getFontSize() {
			return this.size;
		}
		public int getAlign() {
			return this.align;
		}
		public int getType() {
			return this.type;
		}
		public void setFontSize(int size) {
			this.type = FRAGMENT_FONT_SIZE;
			this.size = size;
		}
		public void setNewLine(boolean b) {
			this.type = FRAGMENT_NEW_LINE;
		}
		public void setBold(boolean b) {
			this.type = FRAGMENT_TEXT_BOLD;
		}
		public void setItalic(boolean b) {
			this.type = FRAGMENT_TEXT_ITALIC;
		}
		public void setUnderline(boolean b) {
			this.type = FRAGMENT_TEXT_UNDERLINE;
		}
		public void setColor(String color) {
			this.type = FRAGMENT_FONT_COLOR;
			this.color = color;
		}

		public void addChild(Fragment f) {
			this.children.add(f);
			f.parent = this;
		}
		
		public List<Fragment> getChildren() {
			return children;
		}
		
		public Fragment getParent() {
			return this.parent;
		}

		public void setAlign(String align) {
			this.type = FRAGMENT_TEXT_ALIGN;
			if ("left".equals(align))
				this.align = ALIGN_LEFT;
			else if ("right".equals(align))
				this.align = ALIGN_RIGHT;
			else if ("center".equals(align))
				this.align = ALIGN_CENTER;
			else if ("justify".equals(align))
				this.align = ALIGN_JUSTIFY;
		}

		public void setText(String text) {
			this.text = text;
			this.type = FRAGMENT_TEXT;
		}
		
		public String toString() {
			return toString(0);
		}
		
		public String toString(int level) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i<level; i++)
				sb.append("----");
			
			sb.append("Fragment: ");
			if (type == 0)
				sb.append("text: " + text);
			else if (type == FRAGMENT_NEW_LINE)
				sb.append("NEW LINE!");
			else if (type == FRAGMENT_TEXT_BOLD)
				sb.append("BOLD");
			else if (type == FRAGMENT_TEXT_ITALIC)
				sb.append("ITALIC");
			else if (type == FRAGMENT_TEXT_UNDERLINE)
				sb.append("UNDERLINE");
			else if (type == FRAGMENT_FONT_SIZE)
				sb.append("font-size: " + size);
			else if (type == FRAGMENT_FONT_COLOR)
				sb.append("font-color: " + color);
			else if (type == FRAGMENT_TEXT_ALIGN)
				sb.append("align: " + (align == ALIGN_LEFT ? "" : align == ALIGN_CENTER ? "ALIGN_CENTER" : 
					align == ALIGN_RIGHT ? "ALIGN_RIGHT" : "ALIGN_JUSTIFY"));
				
			for (Fragment child : children)
				sb.append("\r\n").append(child.toString(level + 1));
			
			return sb.toString();
		}

		public void normalize() {
			for (Fragment child : children) {
				child.normalize();
			}
			for (int i = children.size() - 1; i>=0; i--) {
				Fragment child = children.get(i);
				if (child.getType() == -1) {
					children.remove(i);
					for (int j = child.children.size() - 1; j>=0; j--) {
						Fragment grandChild = child.children.get(j);
						children.add(i, grandChild);
					}
				}
			}
			
			List<Fragment> newChildren = new ArrayList<>();
			for (Fragment child : children) {
				if (newChildren.size() > 0 && child.getType() == FRAGMENT_TEXT) {
					Fragment prevChild = newChildren.get(newChildren.size() - 1);
					if (prevChild.getType() == FRAGMENT_TEXT)
						prevChild.setText(prevChild.getText() + child.getText());
					else
						newChildren.add(child);
				} else
					newChildren.add(child);
			}
			this.children = newChildren;
		}
    }
    
    private static class ReportImage {
    	private int width;
    	private int height;
    	private String relationId;
    	private String title;
		
    	public ReportImage(int width, int height, String relationId,
				String title) {
			super();
			this.width = width;
			this.height = height;
			this.relationId = relationId;
			this.title = title;
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}

		public String getRelationId() {
			return relationId;
		}

		public String getTitle() {
			return title;
		}
    }
}