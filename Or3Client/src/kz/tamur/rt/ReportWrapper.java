package kz.tamur.rt;

import com.jacob.com.Dispatch;
import com.jacob.com.ComThread;
import com.jacob.com.Variant;
import com.jacob.activeX.ActiveXComponent;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.SwingWorker;
import com.cifs.or2.util.MultiMap;

import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;

import org.jdom.input.SAXBuilder;
import org.jdom.Element;
import org.jdom.Attribute;
import kz.tamur.comps.Constants;
import kz.tamur.util.Funcs;

/**
 * Created by IntelliJ IDEA.
 * User: erik-b
 * Date: 03.09.2009
 * Time: 11:12:01
 * To change this template use File | Settings | File Templates.
 */
public class ReportWrapper extends SwingWorker {
    private Map<Integer, Dispatch> myMap = new TreeMap<Integer, Dispatch>();
    private Map<Integer, ArrayList<String[]>> vals = new TreeMap<Integer, ArrayList<String[]>>();
    private Map<Integer, int[]> lens = new TreeMap<Integer, int[]>();
    private Map<Integer, Integer> types = new TreeMap<Integer, Integer>();

    private Map<Integer, Dispatch> filterDatesMap = new TreeMap<Integer, Dispatch>();
    private Map<Integer, String> typeMap = new TreeMap<Integer, String>();
    private Map<Integer, String> treeMap = new TreeMap<Integer, String>();
    private Map<Integer, String> specsumMap = new TreeMap<Integer, String>();
    private Map<Integer, String> initialMap = new TreeMap<Integer, String>();
    private Map<Integer, String> consValMap = new TreeMap<Integer, String>();
    private Map<Integer, String> tMap = new TreeMap<Integer, String>();
    private Map<Integer, String> opMap = new TreeMap<Integer, String>();
    private Map<Integer, Integer> tableMap = new TreeMap<Integer, Integer>();
    private Map<Integer, Integer> columnMap = new TreeMap<Integer, Integer>();
    private Map<Integer, Integer> processedColumns = new TreeMap<Integer, Integer>();
    private Map<String, String> totalMap = new TreeMap<String, String>();
    private Map<String, String> freeMap = new TreeMap<String, String>();
    private Map<String, String> countMap = new TreeMap<String, String>();
    private Map<String, String> statMap = new TreeMap<String, String>();
    private Map<Integer, Boolean> noemptyMap = new TreeMap<Integer, Boolean>();
    private MultiMap toDelete = new MultiMap();
    
    private int currentValue = 0;
    private String fileName;
    private String dataFileName;
    private String title;
    private String macros;
    private String pd;
    private long id = 0;
    private long langId = 0;
    private int type = 0;

    boolean firstColumn = false;
    int firstConsValue = 0;
    boolean firstRowColumn = true;
    int cCount = 0, fCount = 0;

    private boolean fastReport = false;
    private boolean hasTree_ = false;
    private boolean showAfterComplete;

    private int from = 0;
    private int portion = 1000;
    private int maxCount = 0;
    private long flowId;
    private int format = -1;
    private ReportObserver taskTable;

    private String webUrl;
    private String uuid;
    private static char defaultSeparator = new DecimalFormat().getDecimalFormatSymbols().getDecimalSeparator();
    
    public static final int XL_2003 = 56;
    public static final int XL_2007 = 51;

    public static String username = "";
    public static String userpd = "";

    public ReportWrapper(String fileName, String dataFileName, String title, String macros, String pd, int type, boolean showAfterComplete) {
        this.fileName = fileName;
        this.dataFileName = dataFileName;
        this.title = title;
        this.macros = macros;
        this.pd = pd;
        this.type = type;
        this.showAfterComplete = showAfterComplete;
    }
    
    public ReportWrapper(String fileName, String dataFileName, String title, String macros, String pd, int type, boolean showAfterComplete,
    		ReportObserver taskTable, long flowId, int format) {
		this.fileName = fileName;
		this.dataFileName = dataFileName;
		this.title = title;
		this.macros = macros;
        this.pd = pd;
		this.type = type;
		this.showAfterComplete = showAfterComplete;
		this.taskTable = taskTable;
		this.flowId = flowId;
		this.format = format;
    }

    public ReportWrapper(String fileName, String title, long id, long langId, int type, String webUrl, String uuid) {
        this.fileName = fileName;
        this.title = title;
        this.id = id;
        this.langId = langId;
        this.type = type;
        
        this.webUrl = webUrl;
        this.uuid = uuid;
    }

    public Object construct() {
        print();
        return null;
    }

    public void print() {
        long time = System.currentTimeMillis();
        if (type == Constants.MSWORD_EDITOR) {
            if (id > 0)
                createWordReport(fileName, title, id, langId, webUrl, uuid);
            else
                viewWordReport(fileName, dataFileName, title, macros);
        } else {
            if (id > 0)
                createExcelReport(fileName, title, id, langId, webUrl, uuid);
            else {
                try {
                    System.out.println("loading xml..." + new Date());
                    InputStream is = new FileInputStream(Funcs.getCanonicalFile(dataFileName));
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
                    maxCount = CountMaxCount(xml);
                    System.out.println("Max Count: " + maxCount + " " + new Date());
                    
                    int iteration = 1;
                    System.out.println("iteration " + iteration++ + " " + new Date());
                    boolean b = viewExcelReport(fileName, xml, title, macros);
                    while (!b) {
                        System.out.println("iteration " + iteration++ + " " + new Date());
                        from += portion;
                        b = viewExcelReport(fileName, xml, title, macros);
                    }
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

    public void createWordReport(String fileName, String title, long id, long langId, String webUrl, String uuid) {
        ComThread.InitSTA();
        ActiveXComponent word = ActiveXComponent.createNewInstance("Word.Application");
        try {
            System.out.println(" version=" + word.getProperty("Version"));

            File dir = Funcs.getCanonicalFile("doc");

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
            word.setProperty("Visible", new Variant(true));

            Dispatch.call(word, "Run", "SetIntitialParams", webUrl, uuid, String.valueOf(id), String.valueOf(langId));
        } catch (Exception e) {
            word.invoke("Quit", new Variant(false));
            e.printStackTrace();
        } finally {
            ComThread.Release();
        }
    }

    public void createExcelReport(String fileName, String title, long id, long langId, String webUrl, String uuid) {
        ComThread.InitSTA();
        ActiveXComponent excel = ActiveXComponent.createNewInstance("Excel.Application");
        try {
            System.out.println(" version=" + excel.getProperty("Version"));

            File dir = Funcs.getCanonicalFile("doc");

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
            excel.setProperty("Visible", new Variant(true));

            String macro = "'" + addinPath + "'!SetIntitialParams";
            Dispatch.call(excel, "Run", macro, webUrl, uuid, String.valueOf(id), String.valueOf(langId));
        } catch (Exception e) {
            excel.invoke("Quit");
            e.printStackTrace();
        } finally {
            ComThread.Release();
        }
    }

    public void viewWordReport(String fileName, String dataFileName, String title, String macros) {
        ComThread.InitSTA();
        ActiveXComponent word = ActiveXComponent.createNewInstance("Word.Application");

        try {
            System.out.println(" version=" + word.getProperty("Version"));
            //word.setProperty("Visible", new Variant(true));
            InputStream is = new FileInputStream(Funcs.getCanonicalFile(dataFileName));
            SAXBuilder builder = new SAXBuilder();
            Element xml = builder.build(is).getRootElement();

            int totalCount = CountValues(xml, 0, false);

            if (taskTable != null) {
            	taskTable.setProgressCaption("Формирование отчета:");
            	taskTable.setProgressMinimum(0);
            	taskTable.setProgressMaximum(totalCount);
            }
            System.out.println("Total: " + totalCount);

            Dispatch opts = Dispatch.get(word, "Options").toDispatch();
            Dispatch.put(opts, "CheckGrammarAsYouType", new Variant(false));
            Dispatch.put(opts, "CheckGrammarAsYouType", new Variant(false));

            Dispatch oDocs = word.getProperty("Documents").toDispatch();
            Dispatch oDoc = Dispatch.call(oDocs, "Open", fileName).toDispatch();

            int protectionType = Dispatch.get(oDoc, "ProtectionType").getInt();
            if (protectionType > -1)
                Dispatch.call(oDoc, "Unprotect", "123");

            try {
	            Dispatch attachedTemplate = Dispatch.get(oDoc, "AttachedTemplate").toDispatch();
	            String templateName = (attachedTemplate != null) ? Dispatch.get(attachedTemplate, "Name").getString() : "";
	            System.out.println("Attached template name = " + templateName);
	
	            if (templateName.contains("ORAdminReport")) {
	                System.out.println("Removing attached template...");
	                Dispatch.put(oDoc, "AttachedTemplate", "");
	            }
            } catch (Throwable t) {
            	t.printStackTrace();
            }

            Dispatch oWnd = word.getProperty("ActiveWindow").toDispatch();
            Dispatch oPane = Dispatch.get(oWnd, "ActivePane").toDispatch();
            Dispatch oView = Dispatch.get(oPane, "View").toDispatch();

            Dispatch oFlds;

        	try {
                Dispatch.put(oView, "SeekView", new Variant(1));
                processFootnoteComments(word);
                Dispatch.put(oView, "SeekView", new Variant(4));
                processFootnoteComments(word);
                Dispatch.call(oView, "NextHeaderFooter");
                processFootnoteComments(word);
                Dispatch.put(oView, "SeekView", new Variant(3));
                processFootnoteComments(word);
        	} catch (Exception e) {
        	} finally {
                Dispatch.put(oView, "SeekView", new Variant(0));
        	}

            oFlds = Dispatch.get(oDoc, "Fields").toDispatch();
            int tempCount = Dispatch.get(oFlds, "Count").getInt();

            for (int k = 0; k < tempCount; k++) {
                Dispatch oFld = Dispatch.call(oFlds, "Item", new Variant(k + 1)).toDispatch();
                Dispatch oRng = Dispatch.get(oFld, "Code").toDispatch();

                String str = Dispatch.get(oRng, "Text").getString();

                int start = str.indexOf('|');
                if (start == -1) {
                    continue;
                }
                int mid = str.indexOf('|', start + 1);
                if (mid == -1) {
                    continue;
                }
                String type = str.substring(start + 1, mid);
                if ("User".equals(type) || "Department".equals(type) || "Base".equals(type)) {
                    String result = str.substring(mid + 1);
                    int id = Integer.parseInt(result);
                    myMap.put(id, oFld);
                    continue;
                }

                int end = str.indexOf('|', mid + 1);
                if (end == -1) {
                    continue;
                }
                String result = str.substring(mid + 1, end);
                int id = Integer.parseInt(result);
                if (id == 0) {
                    continue;
                }

                myMap.put(id, oFld);
            }

            for (int i = 0; i < xml.getChildren().size(); i++) {
                Element pNode = (Element) xml.getChildren().get(i);
                List pChildNodes = pNode.getChildren();

                int childCount = pChildNodes.size();

                Element pChildNode = pNode;

                List pChildAttr = pNode.getAttributes();

                String tfmt = pNode.getAttributeValue("type");
                int fmt = (tfmt != null && tfmt.length() > 0) ? Integer.parseInt(tfmt) : 0;
                
                Attribute pChildItem = (Attribute) pChildAttr.get(0);
                String value = pChildItem.getValue();

                String name = pNode.getName();

                int id = Integer.parseInt(value);
                if ("User".equals(name) || "Department".equals(name) || "Base".equals(name)) {
                    Dispatch oFld = myMap.get(id);
                    if (oFld == null) continue;

                    value = pNode.getAttributeValue("str");

                    Dispatch.call(oFld, "Select");
                    Dispatch oSel = word.getProperty("Selection").toDispatch();

                    Dispatch.call(oFld, "Delete");

                    setText(oSel, value, fmt);

                    if (taskTable != null) {
                    	taskTable.setProgressValue(++currentValue);
                    }

                    myMap.remove(id);
                    continue;
                }
                if ("TreeColumn".equals(name)) {
                    Dispatch oFld = myMap.get(id);
                    if (oFld == null) continue;
                    //Range oRng = oFld_.GetCode();
                    Dispatch.call(oFld, "Select");
                    Dispatch oSel = word.getProperty("Selection").toDispatch();

                    //oSel.InsertRowsAbove(COleVariant((long)count_));
                    //Tables oTbs = oSel.GetTables();
                    //Table oTab = oTbs.Item((long)1);
                    //Rows oRows = oTab.GetRows();
                    //oRow_ = oRows.Item((long)1);
                    ProcessTreeColumn(0, childCount, pChildNodes, oSel, oFld);

                    Dispatch.call(oFld, "Select");
                    Dispatch oRows = Dispatch.get(oSel, "Rows").toDispatch();
                    Dispatch.call(oRows, "Delete");
                    myMap.remove(id);
                    continue;
                }

                if (childCount == 0) {
                    Dispatch oFld = myMap.get(id);
                    if (oFld == null) continue;

                    int length = pChildAttr.size();

                    if (length > 1) {
                        pChildItem = (Attribute) pChildAttr.get(1);

                        String bName = pChildItem.getName();
                        value = pChildItem.getValue();

                        Dispatch.call(oFld, "Select");
                        Dispatch oSel = word.getProperty("Selection").toDispatch();

                        Dispatch.call(oFld, "Delete");
                        myMap.remove(id);

                        if ("str".equals(bName)) {
                            if (value.length() == 0) {
                                int tt = Dispatch.call(oSel, "MoveLeft", new Variant(1), new Variant(1), new Variant(1)).getInt();

                                String tmpText = Dispatch.get(oSel, "Text").getString();

                                if (tt > 0 && " ".equals(tmpText))
                                    Dispatch.put(oSel, "Text", "");
                            } else {
                            	setText(oSel, value, fmt);
                            }
                            if (taskTable != null) {
                            	taskTable.setProgressValue(++currentValue);
                            }

                        } else if ("src".equals(bName)) {
                            if (value.length() > 0) {
                                Dispatch oShps = Dispatch.get(oSel, "InlineShapes").toDispatch();
                                Dispatch.call(oShps, "AddPicture", value, new Variant(false), new Variant(true)).toDispatch();
                            }

                            if (taskTable != null) {
                            	taskTable.setProgressValue(++currentValue);
                            }
                        }
                    } else {
                        Dispatch.call(oFld, "Select");
                        Dispatch.call(oFld, "Delete");
                        myMap.remove(id);
                    }
                    continue;
                }

                Dispatch oFld = myMap.get(id);
                if (oFld == null) continue;

                Dispatch oRng = Dispatch.get(oFld, "Code").toDispatch();
                //oFld.Select();
                //Selection oSel = app.GetSelection();
                //oFld.Delete();
                //myMap.RemoveKey(id);

                boolean info = Dispatch.call(oRng, "Information", new Variant(12)).getBoolean(); //12 = wdWithInTable

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

                            //AfxMessageBox(nodeValue);
                            //oCells = oCol.GetCells();
                            //curCell = oTab.Cell(base+j, colIndex);
                            //curCell = oCells.Item(base+j);
                            //curCell.SetHeightRule(0);
                            //curCell.Select();
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
                                
                                String tcfmt = pValueChildNode.getAttributeValue("type");
                                int cfmt = (tcfmt != null && tcfmt.length() > 0) ? Integer.parseInt(tcfmt) : 0;

                                String bName = pValueChildItem.getName();

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
                    String str = "";
                    for (int j = 0; j < childCount; j++) {
                        pChildNode = (Element) pChildNodes.get(j);

                        pChildAttr = pChildNode.getAttributes();
                        pChildItem = (Attribute) pChildAttr.get(0);
                        value = pChildItem.getValue();

                        str += value;
                    }

                    if (childCount == 0) {
                        str = pChildNode.getAttributeValue("value");
                    } else str = str.substring(2);

                    Dispatch.call(oFld, "Select");
                    Dispatch oSel = word.getProperty("Selection").toDispatch();

                    Dispatch.call(oFld, "Delete");

                    setParagraphFormatValue(oSel, str);
                    myMap.remove(id);
                }
            }

            oWnd = word.getProperty("ActiveWindow").toDispatch();
            Dispatch oView1 = Dispatch.get(oWnd, "View").toDispatch();

            int splitSpecial = Dispatch.get(oView1, "SplitSpecial").getInt();

            if (splitSpecial == 0) {
                oPane = Dispatch.get(oWnd, "ActivePane").toDispatch();
                oView = Dispatch.get(oPane, "View").toDispatch();
                Dispatch.put(oView, "Type", new Variant(3));
            } else {
                Dispatch.put(oView1, "Type", new Variant(3));
            }


            Dispatch oTabs = Dispatch.get(oDoc, "Tables").toDispatch();

            int tCount = Dispatch.get(oTabs, "Count").getInt();
            Dispatch oSel = word.getProperty("Selection").toDispatch();

            int lastId = -1;
            for (int m = 0; m < tCount; m++) {
                Dispatch oTab = Dispatch.call(oTabs, "Item", new Variant(m + 1)).toDispatch();
                lastId++;
                lastId = SetId(oTab, lastId);
            }
            for (int m = 0; m < tCount; m++) {
                Dispatch oTab = Dispatch.call(oTabs, "Item", new Variant(m + 1)).toDispatch();
                lastId = ProcessTable(oTab, 0, 0, oSel, lastId);
            }

//            Dispatch oSel = word.getProperty("Selection").toDispatch();
            Dispatch.call(oSel, "HomeKey", new Variant(6));

            oWnd = word.getProperty("ActiveWindow").toDispatch();
            oView = Dispatch.get(oWnd, "View").toDispatch();
            Dispatch.put(oView, "ShowFieldCodes", new Variant(false));

            for (int nl = 5; nl > 0; nl--) {
            	List<Dispatch> list = (List<Dispatch>)toDelete.get(nl);
            	if (list != null) {
            		for (Dispatch delRng : list) {
                        try {
                        	if (nl == getNestingLevel(delRng)) {
	                        	Dispatch oRows = Dispatch.get(delRng, "Rows").toDispatch();
	                        	Dispatch oRow = Dispatch.call(oRows, "Item", new Variant(1)).toDispatch();
	                        	Dispatch.call(oRow, "Delete");
                        	}
                        } catch (Exception ex) {
                            System.out.println("Exception key");
                        }
            		}
            	}
            }

            for (Integer key : myMap.keySet()) {
                Dispatch oFld = myMap.get(key);

                try {
                    Dispatch.call(oFld, "Delete");
                } catch (Exception ex) {
                    System.out.println("Exception key: " + key);
                }
                //myMap.remove(key);
            }
            myMap.clear();
            myMap = null;

            oFlds = Dispatch.get(oDoc, "Fields").toDispatch();
            tempCount = Dispatch.get(oFlds, "Count").getInt();

            for (int k = 0; k < tempCount; k++) {
                Dispatch oFld = Dispatch.call(oFlds, "Item", new Variant(k + 1)).toDispatch();
                Dispatch.call(oFld, "Update");
            }
            
            if (macros.length() > 0) {
                Dispatch.call(word, "Run", macros);
            }

//            if (protectionType > -1)
//                Dispatch.call(oDoc, "Protect", new Variant(protectionType), new Variant(false), "123");
            protectDocument(oDoc);

            Dispatch.call(oDocs, "Save");

            Dispatch.put(opts, "CheckGrammarAsYouType", new Variant(true));
            Dispatch.put(opts, "CheckGrammarAsYouType", new Variant(true));

            if (showAfterComplete) word.setProperty("Visible", new Variant(true));
            else word.invoke("Quit", new Variant(false));
        } catch (Exception e) {
            e.printStackTrace();
            try {
                word.invoke("Quit", new Variant(false));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } finally {
            if (taskTable != null) {
            	taskTable.setProgressCaption("");
            	taskTable.setProgressValue(0);
            }
            ComThread.Release();
        }
    }

    private void processFootnoteComments(ActiveXComponent word) {
        Dispatch oSel = word.getProperty("Selection").toDispatch();
    	
        Dispatch.call(oSel, "WholeStory");

        Dispatch oFlds = Dispatch.get(oSel, "Fields").toDispatch();
        int tempCount = Dispatch.get(oFlds, "Count").getInt();

        for (int k = 0; k < tempCount; k++) {
            Dispatch oFld = Dispatch.call(oFlds, "Item", new Variant(k + 1)).toDispatch();
            Dispatch oRng = Dispatch.get(oFld, "Code").toDispatch();

            String str = Dispatch.get(oRng, "Text").getString();
            //AfxMessageBox(str);
            int start = str.indexOf('|');
            if (start == -1) {
                continue;
            }

            int mid = str.indexOf('|', start + 1);
            if (mid == -1) {
                continue;
            }
            String type = str.substring(start + 1, mid);
            if ("User".equals(type) || "Department".equals(type) || "Base".equals(type)) {
                String result = str.substring(mid + 1);
                int id = Integer.parseInt(result);
                myMap.put(id, oFld);
                continue;
            }

            int end = str.indexOf('|', mid + 1);
            if (end == -1) {
                continue;
            }
            String result = str.substring(mid + 1, end);
            int id = Integer.parseInt(result);
            if (id == 0) {
                continue;
            }

            myMap.put(id, oFld);
        }
    }

    private void setText(Dispatch oSel, String value, int fmt) {
    	if (fmt == Kernel.IC_FLOAT && value != null) {
    		String customSeparator = Funcs.normalizeInput(System.getProperty("decimalSeparator"));
    		char separator = (customSeparator == null) ? defaultSeparator : customSeparator.charAt(0);
    		char antisepar = (separator == '.') ? ',' : '.';
    		value = value.replace(antisepar, separator);
    	}
        setParagraphFormatValue(oSel, value);
    }

    private int CountValues(Element xml, int level, boolean isExcel) {
        List ptChildNodes = xml.getChildren();
        int count = 0;

        int childCount = ptChildNodes.size();
        if (level > 0 && childCount > maxCount) maxCount = childCount;

        int from = (isExcel && level > 0 && childCount > portion) ? this.from : 0;
        int to = (isExcel && level > 0 && childCount > from + portion) ? from + portion : childCount;

        for (int n = from; n < to; n++) {
            Element ptNode = (Element) ptChildNodes.get(n);
            String attr = ptNode.getAttributeValue("str");
            if (attr == null) attr = ptNode.getAttributeValue("src");
            if (attr != null) count++;

            count += CountValues(ptNode, level + 1, isExcel);
        }

        for (int n = childCount - 1; n >= to; n--) {
            ptChildNodes.remove(n);
        }

        for (int n = from - 1; n >= 0; n--) {
            ptChildNodes.remove(n);
        }
        return count;
    }

    private int CountAllValues(Element xml) {
        try {
            List nodes = org.jdom.xpath.XPath.selectNodes(xml, "//*[@src or @str]");
            return nodes.size();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int CountMaxCount(Element xml) {
        try {
            int maxCount = xml.getChildren().size();
            if (maxCount > portion) portion = maxCount;
            List<Element> nodes = org.jdom.xpath.XPath.selectNodes(xml, "//*[@id]");
            for (Element node : nodes) {
                int size = node.getChildren().size();
                Object o = org.jdom.xpath.XPath.selectSingleNode(node, "count(Value/Value)");
                if (o instanceof Number) {
                	int size2 = ((Number)o).intValue() + size;
                	if (size2 > size) size = size2;
                }
                if (size > maxCount) maxCount = size;
            }
            return maxCount;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void ProcessTreeColumn(int level, int count, List tempNodes, Dispatch oSel, Dispatch oFld) {
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

            InsertTreeNodeValue(value, level, oSel, oFld);

            ProcessTreeColumn(level + 1, childCount, ptChildNodes, oSel, oFld);
        }
    }

    public void InsertTreeNodeValue(String value, int level, Dispatch oSel, Dispatch oFld) {
        Dispatch.call(oFld, "Select");
        Dispatch.call(oSel, "InsertRowsAbove", new Variant(1));
        Dispatch.call(oSel, "Collapse");

        Dispatch oPhs = Dispatch.get(oSel, "Paragraphs").toDispatch();
        Dispatch.put(oPhs, "LeftIndent", new Variant(10 * level));

        setParagraphFormatValue(oSel, value);
    }

    private int SetId(Dispatch oTab, int lastId) {
        String str = String.valueOf(lastId);

        Dispatch.put(oTab, "Id", str);

        Dispatch oTabs = Dispatch.get(oTab, "Tables").toDispatch();
        int count = Dispatch.get(oTabs, "Count").getInt();

        for (int i = 1; i <= count; i++) {
            Dispatch chTab = Dispatch.call(oTabs, "Item", new Variant(i)).toDispatch();
            lastId++;
            lastId = SetId(chTab, lastId);
        }
        return lastId;
    }

    private int ProcessTable(Dispatch oTab, int index, int level, Dispatch oSel, int lastId) {
        boolean isTableInTable = false;
        Dispatch oRngM = Dispatch.get(oTab, "Range").toDispatch();

        Dispatch oFlds = Dispatch.get(oRngM, "Fields").toDispatch();
        Dispatch oRows = Dispatch.get(oTab, "Rows").toDispatch();
        Dispatch oCols = Dispatch.get(oTab, "Columns").toDispatch();

        //Map<Integer, Integer> rows;

        Map<Integer, Boolean> tMap = new TreeMap<Integer, Boolean>();

        int count = Dispatch.get(oFlds, "Count").getInt();
        Dispatch[] flds = new Dispatch[count];
        for (int k = 0; k < count; k++) {
            Dispatch oFld;
            try {
                oFld = Dispatch.call(oFlds, "Item", new Variant(k + 1)).toDispatch();
                flds[k] = oFld;
            } catch (Exception ex) {
            }
        }
        String str;

        for (int k = 0; k < count; k++) {
            Dispatch oFld;
            Dispatch oRng;
            try {
                oFld = flds[k];
                oRng = Dispatch.get(oFld, "Code").toDispatch();
            } catch (Exception ex) {
                continue;
            }

            str = Dispatch.get(oRng, "Text").getString();
            if (str.indexOf('@') > -1)
                isTableInTable = true;
            else
                isTableInTable = false;

            int start = str.indexOf('|');
            if (start == -1) continue;
            start = str.indexOf('|', start + 1);
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

            int[] childCounts = lens.get(id);
            int childCount;


            if (childCounts == null || index >= childCounts.length) continue;
            childCount = childCounts[index];

            int parentRow = -1; 
            if (isTableInTable) {
            	parentRow = getParentRow(oTab, str); 
                Dispatch oRow = Dispatch.call(oRows, "Item", new Variant(parentRow)).toDispatch();
                Dispatch.call(oRow, "Select");
            } else {
	            Dispatch.call(oFld, "Select");
	            Dispatch.call(oSel, "SelectRow");
            }
            Dispatch oCells = Dispatch.get(oSel, "Cells").toDispatch();
            Dispatch curCell = Dispatch.call(oCells, "Item", new Variant(1)).toDispatch();

            Dispatch tTbs = Dispatch.get(oSel, "Tables").toDispatch();
            Dispatch oT = Dispatch.call(tTbs, "Item", new Variant(1)).toDispatch();

            String oS = Dispatch.get(oT, "Id").getString();
            //int oRowIndex = Dispatch.get(curCell, "RowIndex").getInt();

            if (oS == null || oS.length() == 0) {
                lastId++;
                oS = String.valueOf(lastId);
                lastId = SetId(oT, lastId);
            }

            int oId = Integer.parseInt(oS);

/*
            if (isTableInTable) {
                Dispatch fT = FindTable(oTab, oT, oS);
                Dispatch.call(fT, "Select");
                //oRow = GetRow(fT, oT);
                //oRowIndex = GetRowIndex(fT, oT);
                oT = fT;
                oS = Dispatch.get(oT, "Id").getString();
                oId = Integer.parseInt(oS);
                //Rows rs = oT.GetRows();
                //oRow = rs.Item(index);
//                oRowIndex = index;
            }
*/
            int compTid = intTableId * 100 + oId;

            if (!tMap.containsKey(compTid)) {
                tMap.put(compTid, true);
                //oFld.Select();
                //Cells oCells = oSel.GetCells();
                //Cell curCell = oCells.Item(1);
                //Row oRow = curCell.GetRow();
                //Table oT = oRow.GetParent();

                //oT.Select();
                //AfxMessageBox("1");
                //oRow.Select();
                Dispatch.call(curCell, "Select");
                //oSel.Copy();
                int base = Dispatch.get(curCell, "RowIndex").getInt();
                //int base = oRow.GetIndex();
                //int colIndex = curCell.GetColumnIndex();

                //Cell tempCell = oTab.Cell(base, colIndex);

                //Range tempRange = tempCell.GetRange();
                //Fields tempFlds = tempRange.GetFields();

                //long gg = tempFlds.GetCount();

                //if (gg == 0) continue;

                //Field tempFld = tempFlds.Item(1);
                //Range tempRng = tempFld.GetCode();
                //CString tempStr = tempRng.GetText();

                //if (tempStr != str) continue;

                if (childCount > 1) {
                    Dispatch.call(oSel, "InsertRowsBelow", new Variant(childCount - 1));

                    //Row tempRow = oRow;
                    Dispatch.call(curCell, "Select");
                    Dispatch.call(oSel, "SelectRow");

                    Dispatch ocells = Dispatch.get(oSel, "Cells").toDispatch();
                    int ocellsCount = Dispatch.get(ocells, "Count").getInt();

                    for (int r = 1; r <= ocellsCount; r++) {
                        curCell = Dispatch.call(oT, "Cell", new Variant(base), new Variant(r)).toDispatch();
                        Dispatch cellRange = Dispatch.get(curCell, "Range").toDispatch();

                        Dispatch tempRange = Dispatch.get(cellRange, "FormattedText").toDispatch();
                        Dispatch.call(tempRange, "SetRange", Dispatch.get(tempRange, "Start").getInt(), Dispatch.get(tempRange, "End").getInt() - 1);
                        for (int j = 1; j < childCount; j++) {
                            curCell = Dispatch.call(oT, "Cell", new Variant(base + j), new Variant(r)).toDispatch();
                            Dispatch curCellRange = Dispatch.get(curCell, "Range").toDispatch();
                            Dispatch.put(curCellRange, "FormattedText", tempRange);
                        }
                    }

                    /*Row tempRow = curCell.GetRow();

                    Cells ocells = tempRow.GetCells();
                    int ocellsCount = ocells.GetCount();

                    for (int r = 1; r <= ocellsCount; r++) {
                        curCell = oT.Cell(base, r);
                        curCell.Select();
                        oSel.Copy();
                        for (int j = 1; j<childCount; j++)
                        {
                            curCell = oT.Cell(base+j, r);
                            curCell.Select();
                            oSel.Paste();
                        }
                    }*/
                }
                //for (int j = 1; j<childCount; j++)
                //{
                //	tempCell = oTab.Cell(base + j - 1, colIndex);
                //		tempCell.Select();
                //Row tempRow = tempCell.GetRow();

                //Cells ocells = tempRow.GetCells();
                //int ocellsCount = ocells.GetCount();

                //if (ocellsCount > 1)
                //			oSel.SelectRow();
                //AfxMessageBox("9");
                //				oSel.Paste();
                //AfxMessageBox("10");
                //}
                
                for (int j = 0; j < childCount; j++) {
                    curCell = Dispatch.call(oT, "Cell", new Variant(base + j), new Variant(1)).toDispatch();
                    Dispatch.call(curCell, "Select");
                    Dispatch.call(oSel, "SelectRow");

                    Dispatch rowCells = Dispatch.get(oSel, "Cells").toDispatch();

                    //Row curRow = curCell.GetRow();

                    //Cells rowCells = curRow.GetCells();
                    int rowCellsCount = Dispatch.get(rowCells, "Count").getInt();

                    for (int cm = 1; cm <= rowCellsCount; cm++) {
                        Dispatch rowCell = Dispatch.call(oT, "Cell", new Variant(base + j), new Variant(cm)).toDispatch();

                        Dispatch oTabs = Dispatch.get(rowCell, "Tables").toDispatch();
                        int oTabsCount = Dispatch.get(oTabs, "Count").getInt();

                        for (int m = 0; m < oTabsCount; m++) {
                            Dispatch oChTab = Dispatch.call(oTabs, "Item", new Variant(m + 1)).toDispatch();
                            lastId = ProcessTable(oChTab, j + 1, 1, oSel, lastId);
                        }
                    }
                }
            }
            childCounts[index] = 0;
            lens.put(id, childCounts);
            if (myMap.containsKey(id)) myMap.remove(id);
        }

        /*
            for (int k = 0; k<count; k++)
            {
                Field oFld = oFlds.Item((long)k+1);
                Range oRng = oFld.GetCode();
                str = oRng.GetText();
                int start = str.Find('|');
                if (start == -1) continue;
                start = str.Find('|', start+1);
                if (start == -1) continue;
                int end = str.Find('|', start+1);
                if (end == -1) continue;
                CString result = str.Mid(start+1, end-start-1);
                int id = Integer.parseInt(result);
                if (id == 0) continue;
                oFld.Select();
                Cells oCells = oSel.GetCells();
                Cell curCell = oCells.Item(1);
                curCell.Select();

                Cell nCell;
                oSel.Copy();
                int base = curCell.GetRowIndex();
                int colIndex = curCell.GetColumnIndex();

                long childCount;
                if (!lens.Lookup(id, childCount)) continue;

                if (childCount>1)
                {
                    int idProcessed = 0;
                    Field tempFld;
                    if (!rows.Lookup(base, idProcessed) && myMap.Lookup(id, tempFld))
                    {
                        oSel.InsertRowsBelow(COleVariant((long)(childCount - 1)));
                        rows.SetAt(base, id);
                    }

                    for (int j = 1; j<childCount; j++)
                    {
                        curCell = oTab.Cell(base+j, colIndex);
                        curCell.Select();
                        oSel.Paste();
                    }
                }
                lens.SetAt(id, 0);
                myMap.RemoveKey(id);
                count = oFlds.GetCount();
            }
        */
        /*	Tables oTabs = oTab.GetTables();

        for (int m=0; m<oTabs.GetCount(); m++)
        {
            Table oChTab = oTabs.Item(m+1);
            ProcessTable(oChTab, 0);
        }*/
        oRngM = Dispatch.get(oTab, "Range").toDispatch();

        oFlds = Dispatch.get(oRngM, "Fields").toDispatch();

        count = Dispatch.get(oFlds, "Count").getInt();

        for (int k = 0; k < count; k++) {
            Dispatch oFld = Dispatch.call(oFlds, "Item", new Variant(1)).toDispatch();
            Dispatch oRng2 = Dispatch.get(oFld, "Code").toDispatch();
            str = Dispatch.get(oRng2, "Text").getString();

            int start = str.indexOf('|');
            if (start == -1) continue;
            start = str.indexOf('|', start + 1);
            if (start == -1) continue;
            int end = str.indexOf('|', start + 1);
            if (end == -1) continue;
            String result = str.substring(start + 1, end);
            int id = Integer.parseInt(result);
            if (id == 0) continue;

            ArrayList<String[]> allVals;
            int[] numbers;

            String[] valls;
            int number;
            int type = -1;
            String v = null;
            
            allVals = vals.get(id);
            if (allVals == null || index >= allVals.size()) continue;
            numbers = lens.get(id);
            if (numbers == null) continue;

            valls = allVals.get(index);
            number = numbers[index];

            if (types.containsKey(id)) {
            	type = types.get(id);
	            v = valls[number];
            }
            Dispatch.call(oFld, "Select");
            Dispatch.call(oFld, "Delete");
            myMap.remove(id);

            if (type == -1 || "@deleteRow()".equals(v)) {
            	Dispatch delRng = Dispatch.get(oSel, "Range").toDispatch();
                int nestingLevel = getNestingLevel(delRng);
            	toDelete.put(nestingLevel, delRng);
            } else if (type == 0) {
                setParagraphFormatValue(oSel, v);
            } else if (type > 1) {
            	setText(oSel, v, type);
            } else {
                Dispatch oShps = Dispatch.get(oSel, "InlineShapes").toDispatch();
                Dispatch.call(oShps, "AddPicture", v, new Variant(false), new Variant(true)).toDispatch();
            }
            if (taskTable != null) {
            	taskTable.setProgressValue(++currentValue);
            }

            numbers[index] = number + 1;
            lens.put(id, numbers);
        }
        return lastId;
    }

    private int getNestingLevel(Dispatch rng) {
    	Dispatch ts = Dispatch.get(rng, "Tables").toDispatch();
        int tc = Dispatch.get(ts, "Count").getInt();
        int nestingLevel = 0;
        if (tc > 0) {
            Dispatch t = Dispatch.call(ts, "Item", new Variant(1)).toDispatch();
            nestingLevel = Dispatch.get(t, "NestingLevel").getInt();
        }
        return nestingLevel;
    }

    private int getParentRow(Dispatch oTab, String str) {
        Dispatch oRows = Dispatch.get(oTab, "Rows").toDispatch();
        Dispatch oCols = Dispatch.get(oTab, "Columns").toDispatch();
        int rcount = Dispatch.get(oRows, "Count").getInt();
        int ccount = Dispatch.get(oCols, "Count").getInt();
        for (int g = 0; g<rcount; g++) {
            for (int h = 0; h<ccount; h++) {
	            Dispatch oCell = null;
	            try {
	                oCell = Dispatch.call(oTab, "Cell", new Variant(g + 1), new Variant(h + 1)).toDispatch();
	            } catch (Exception ex) {
	            }
	            if (oCell != null) {
	                Dispatch oRngC = Dispatch.get(oCell, "Range").toDispatch();
	                Dispatch oFlds = Dispatch.get(oRngC, "Fields").toDispatch();
	                int count = Dispatch.get(oFlds, "Count").getInt();
	                
	                if (count > 0) {
	                    Dispatch[] flds = new Dispatch[count];
	                    for (int k = 0; k < count; k++) {
	                        Dispatch oFld;
	                        try {
	                            oFld = Dispatch.call(oFlds, "Item", new Variant(k + 1)).toDispatch();
	                            flds[k] = oFld;
	                        } catch (Exception ex) {
	                        }
	                    }
	                    String str2;
	                    for (int k = 0; k < count; k++) {
	                        Dispatch oFld;
	                        Dispatch oRng;
	                        try {
	                            oFld = flds[k];
	                            oRng = Dispatch.get(oFld, "Code").toDispatch();
	                        } catch (Exception ex) {
	                            continue;
	                        }

	                        str2 = Dispatch.get(oRng, "Text").getString();
	                        
	                        if (str.equals(str2)) return g + 1;
	                    }
	                }
	            }
            }
        }
        return -1;
    }

    private Dispatch FindTable(Dispatch oTab, Dispatch oT, String oS) {
        Dispatch res = null;

        int oTLevel = Dispatch.get(oT, "NestingLevel").getInt();
        int oTabLevel = Dispatch.get(oTab, "NestingLevel").getInt();

        if (oTLevel > oTabLevel + 1) {
            Dispatch oTabs = Dispatch.get(oTab, "Tables").toDispatch();
            int oTabsCount = Dispatch.get(oTabs, "Count").getInt();

            for (int i = 1; i <= oTabsCount; i++) {
                Dispatch chTab = Dispatch.call(oTabs, "Item", new Variant(i)).toDispatch();
                if ((res = FindTable(chTab, oT, oS)) != null)
                    return res;
            }
        } else {
            Dispatch oTabs = Dispatch.get(oTab, "Tables").toDispatch();
            int oTabsCount = Dispatch.get(oTabs, "Count").getInt();

            for (int i = 1; i <= oTabsCount; i++) {
                Dispatch chTab = Dispatch.call(oTabs, "Item", new Variant(i)).toDispatch();
                String chS = Dispatch.get(chTab, "Id").getString();
                if (chS.equals(oS))
                    return oTab;
            }
        }
        return res;

    }

/*
    private int GetRowIndex(Dispatch oTab, Dispatch oT) {
        Dispatch oRng = Dispatch.get(oTab, "Range").toDispatch();
        Dispatch oCells = Dispatch.get(oRng, "Cells").toDispatch();
        String oS = Dispatch.get(oT, "Id").getString();
        int oCellsCount = Dispatch.get(oCells, "Count").getInt();

        for (int m = 1; m <= oCellsCount; m++) {
            Dispatch oCell = Dispatch.call(oCells, "Item", new Variant(m)).toDispatch();

            Dispatch oTabs = Dispatch.get(oCell, "Tables").toDispatch();
            int oTabsCount = Dispatch.get(oTabs, "Count").getInt();

            for (int i = 1; i <= oTabsCount; i++) {
                //chTab.Select();
                Dispatch chTab = Dispatch.call(oTabs, "Item", new Variant(i)).toDispatch();
                String chS = Dispatch.get(chTab, "Id").getString();
                if (chS.equals(oS))
                    return Dispatch.get(oCell, "RowIndex").getInt();
            }
        }
        return 1;

    }
*/

    public boolean viewExcelReport(String fileName, Element xml, String title, String macros) {
        ComThread.InitSTA();
        ActiveXComponent excel = ActiveXComponent.createNewInstance("Excel.Application");

        try {
            //excel.setProperty("Visible", new Variant(true));
            System.out.println(" version=" + excel.getProperty("Version"));

            int count;
            String str;

            Dispatch wbs = excel.getProperty("Workbooks").toDispatch();
            Dispatch wb = Dispatch.call(wbs, "Open", fileName).toDispatch();
            wbs.safeRelease();
            wbs = null;

            Dispatch wshs = Dispatch.get(wb, "Worksheets").toDispatch();
            int wshsCount = Dispatch.get(wshs, "Count").getInt();

            for (int w = 0; w < wshsCount; w++) // Пробегаемся по всем листам в книге Excel
            {
                Dispatch wsh = Dispatch.call(wshs, "Item", new Variant(w + 1)).toDispatch();
                Dispatch coms = Dispatch.get(wsh, "Comments").toDispatch();
                count = Dispatch.get(coms, "Count").getInt();

                for (int k = 0; k < count; k++) // Пробегаемся по всем коментариям на листе
                {
                    Dispatch com = Dispatch.call(coms, "Item", new Variant(k + 1)).toDispatch();
                    Dispatch rng = Dispatch.get(com, "Parent").toDispatch();

                    str = Dispatch.call(com, "Text").getString();
                    com.safeRelease();
                    com = null;

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
                        myMap.put(id, rng);
                        typeMap.put(id, type);
                        initialMap.put(id, "");
                        continue;
                    }
                    int end = str.indexOf('|', start + 1);
                    if ("Filter".equals(type)) {
                        int id;
                        if (end == -1) id = Integer.parseInt(str.substring(start + 1));
                        else id = Integer.parseInt(str.substring(start + 1, end));
                        myMap.put(id, rng);
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

                        boolean noempty = false;
                        start = str.indexOf("oneRow=");
                        if (start > -1) {
                            end = str.indexOf("|", start + 1);
                            if (end == -1) end = str.length();

                            String tmp = str.substring(start + 7, end);
                            noempty = "1".equals(tmp);
                        }
                        noemptyMap.put(id, noempty);

                        String buf = "";
                        Dispatch mrng = Dispatch.get(rng, "MergeArea").toDispatch();
                        Dispatch.call(mrng, "UnMerge");
                        mrng.safeRelease();
                        mrng = null;

                        rng = Dispatch.call(rng, "Range", "A1", "AZ1").toDispatch();

                        for (int j = 0; j < count; j++) {
                            Dispatch com2 = Dispatch.call(coms, "Item", new Variant(j + 1)).toDispatch();
                            str = Dispatch.call(com2, "Text").getString();
                            com2.safeRelease();
                            com2 = null;

                            if (str.length() > 0 && "|".equals(str.substring(str.length() - 1))) {
                                start = str.indexOf('|');
                                end = str.indexOf('|', start + 1);
                                String result1 = str.substring(start + 1, end);

                                end = str.lastIndexOf('|', str.length() - 1);
                                //str.MakeReverse();
                                //end = str.indexOf('|', 1);
                                String result2 = str.substring(end, str.length() - 1);
                                if (result.equals(result2) && !result.equals(result1)) {
                                    buf += result1;
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
                                }
                            }
                        }
                        treeMap.put(id, buf);
                    }
                    if ("Table".equals(type)) {
                        start = str.indexOf("columns=");

                        String endAddress;

                        int cols = 0;
                        if (start > -1) {
                            end = str.indexOf("|", start + 1);
                            if (end == -1) end = str.length();

                            String tmp = str.substring(start + 8, end);
                            cols = Integer.parseInt(tmp);
                        }

                        if (cols < 26) {
                            endAddress = String.format("%c1", 'A' + cols);
                        } else {
                            endAddress = String.format("A%c1", 'A' + cols - 26);
                        }

                        rng = Dispatch.call(rng, "Range", "A1", endAddress).toDispatch();
                        processedColumns.put(id, 1);

                        boolean noempty = false;
                        start = str.indexOf("oneRow=");
                        if (start > -1) {
                            end = str.indexOf("|", start + 1);
                            if (end == -1) end = str.length();

                            String tmp = str.substring(start + 7, end);
                            noempty = "1".equals(tmp);
                        }
                        noemptyMap.put(id, noempty);

                        for (int j = 0; j < count; j++) {
                            Dispatch com2 = Dispatch.call(coms, "Item", new Variant(j + 1)).toDispatch();
                            str = Dispatch.call(com2, "Text").getString();
                            com2.safeRelease();
                            com2 = null;
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
                    myMap.put(id, rng);
                    String addr = Dispatch.call(rng, "Address", new Variant(true), new Variant(true), new Variant(1)).getString();
                    initialMap.put(id, addr);
                    typeMap.put(id, type);
                }

                for (int k = count - 1; k >= 0; k--) {
                    Dispatch com = Dispatch.call(coms, "Item", new Variant(k + 1)).toDispatch();
                    if (maxCount <= from + portion) {
                        Dispatch r = Dispatch.get(com, "Parent").toDispatch();
                        Dispatch.call(r, "ClearComments");
                        r.safeRelease();
                        r = null;
                    }
                    com.safeRelease();
                    com = null;
                }

                coms.safeRelease();
                coms = null;
            }

            normalizeXml(xml);
            normalizeTreeXml(xml);
            
            List pNodes = xml.getChildren();
            count = pNodes.size();

            ProcessNodes(0, -1, 0, count, false, pNodes, wshs);

            Dispatch rng, rng2, crng1, crng2, crng12, crng22;
            String type, specStr;

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
                setExcelRichTextValue(rng, new Variant(sum));
            }

            for (Integer key : myMap.keySet()) {
                rng = myMap.get(key);
                type = typeMap.get(key);
                if ("TreeColumn".equals(type))// && rng.GetValue().vt == VT_EMPTY)
                {
                    try {
                        Dispatch.call(rng, "Delete", new Variant(3));
                    }
                    catch (Exception e) {
                    }
                } else if ("Column".equals(type) || "ConsColumnEx".equals(type)
                        || "Table".equals(type))// && rng.GetValue().vt == VT_EMPTY)
                {
                    try {
                        if (!tableMap.containsKey(key)) {
                            if (processedColumns.containsKey(key)) {
                                if (maxCount <= from + portion) Dispatch.call(rng, "Delete", new Variant(3));
                            }
                        }
                    }
                    catch (Exception e) {
                    }
                } else if ("StatTable".equals(type)) {
                    rng2 = Dispatch.call(rng, "Offset", new Variant(-1), new Variant(0)).toDispatch();
                    Dispatch.call(rng2, "Delete", new Variant(3));
                    Dispatch.call(rng, "Delete", new Variant(3));
                }
            }
            myMap.clear();
            typeMap.clear();
            initialMap.clear();

            if (macros.length() > 0 && maxCount <= from + portion) {
                Dispatch.call(excel, "Run", macros);
            }

            protectWorkbook(wb);
            
            Dispatch.put(wb, "Saved", new Variant(true));
            if (format > -1) {
            	Dispatch.call(wb, "SaveAs", fileName + "x", new Variant(format));
            	this.format = -1;
            } else {
            	Dispatch.call(wb, "Save");
            }

            if (showAfterComplete && maxCount <= from + portion) excel.setProperty("Visible", new Variant(true));
            else excel.invoke("Quit");
        } catch (Exception e) {
            e.printStackTrace();
            excel.invoke("Quit");
        } finally {
            if (taskTable != null && maxCount <= from + portion) {
            	taskTable.setProgressCaption("");
            	taskTable.setProgressValue(0);
            }
            ComThread.Release();
        }
        return maxCount <= from + portion;
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
    
    private void normalizeTreeXml (Element element) {
        if ("TreeColumn".equals(element.getName()) || "Value".equals(element.getName()) || "Root".equals(element.getName())) {
        	List<Element> elements = element.getChildren("Column");
    		
            for (Element col : elements) {
            	if (col.getChildren().size() > 0 && ((Element)col.getChildren().get(0)).getAttributes().size() == 0) {
            		normalizeCols(elements, false);
            		break;
            	}
            }
            
            List<Element> children = element.getChildren("TreeColumn");
            for (Element el : children) {
            	normalizeTreeXml(el);
            }            

            children = element.getChildren("Value");
            for (Element el : children) {
            	normalizeTreeXml(el);
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

    private void ProcessNodes(int pid, int level, int tpid, int count, boolean oneRow, List tempNodes, Dispatch wshs) {
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

        int from = (level == -1) ? 0 : this.from;
        count = (level > -1 && count > from + portion) ? portion : count - from;

        for (int n = 0; n < count; n++) {
            ptNode = (Element) tempNodes.get(n + from);
            ptChildNodes = ptNode.getChildren();
            ptChildAtr = ptNode.getAttributes();
            childCount = ptChildNodes.size();

            Dispatch trng, trng2;
            Dispatch rng, rng2;

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
                    setExcelRichTextValue(rng, SetValue(rng, value));
                    filterDatesMap.remove(id);
                    continue;
                }
                // Если поле или фильтр
                if (("Field".equals(name) || "Filter".equals(name) || "Department".equals(name)
                        || "User".equals(name) || "Base".equals(name)) && tpid == 0) {
                    value = ptNode.getAttributeValue("id");
                    int id = Integer.parseInt(value);
                    rng = myMap.get(id);
                    if (rng == null) continue;

                    value = ptNode.getAttributeValue("str");
                    if (value != null && value.length() > 0) {
                    	setExcelRichTextValue(rng, SetValue(rng, value));
                    }
                    value = ptNode.getAttributeValue("list");
                    if (value != null && value.length() > 0) {
                        Dispatch.call(rng, "AddComment", value);
                    }
                    value = ptNode.getAttributeValue("src");
                    if (value != null && value.length() > 0) {
                    	setImageValue(rng, value);
                    }

                    if (taskTable != null) {
                    	taskTable.setProgressValue(++currentValue);
                    }

                    myMap.remove(id);
                    typeMap.remove(id);
                    initialMap.remove(id);
                    continue;
                }
                if ("Filter2".equals(name)) {
                    String addr = ptNode.getAttributeValue("cell");

                    value = ptNode.getAttributeValue("sheet");
                    int sheet = Integer.parseInt(value);

                    value = ptNode.getAttributeValue("str");

                    Dispatch wsh = Dispatch.call(wshs, "Item", new Variant(sheet)).toDispatch();

                    rng = Dispatch.call(wsh, "Range", addr, addr).toDispatch();
                    //rng = _wsh.Get(COleVariant(addr), COleVariant(addr));

                    try {
                        Double val = Double.valueOf(value);
                        setExcelRichTextValue(rng, val);
                    } catch (Exception ex) {
                        setExcelRichTextValue(rng, value);
                    }

                    value = ptNode.getAttributeValue("list");
                    if (value != null && value.length() > 0) {
                        Dispatch.call(rng, "ClearComments");
                        Dispatch.call(rng, "AddComment", value);
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

                    rng = myMap.get(pid);
                    if (rng == null) continue;
                    ptChildItem = (Attribute) ptChildAtr.get(1);
                    value = ptChildItem.getValue();
                    String finalVal = value;
                    if ("Infinity".equals(finalVal) || "NaN".equals(finalVal)) finalVal = "";
                    if (firstColumn) {
                        if (id != firstConsValue) {
                            if (startAddress.length() > 0) {
                                fCount += AddSummary(5, tpid, startAddress);
                            }
                            firstConsValue = id;
                            String str2 = consValMap.get(id);
                            if (str2.length() > 0) {
                                trng = myMap.get(tpid);
                                trng2 = Dispatch.call(trng, "Range", "A1", "A1").toDispatch();
                                startAddress = Dispatch.call(trng2, "Address", new Variant(true), new Variant(true), new Variant(1)).getString();
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
                        //ptChildAtr->get_item(1, &ptChildItem);
                        //ptChildItem->get_nodeValue(&value);
                        rng2 = Dispatch.call(rng, "Offset", new Variant(-1), new Variant(0)).toDispatch();
                        setExcelRichTextValue(rng2, SetValue(rng2, finalVal));

                        if (n == count - 1 && startAddress.length() > 0) {
                            fCount += AddSummary(5, tpid, startAddress);
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
                        //ptChildItem = (Attribute) ptChildAtr.get(1);
                        //value = ptChildItem.getValue();

                        if (cCount == 0) cCount = 1;
                        rng2 = Dispatch.call(rng, "Offset", new Variant(-cCount--), new Variant(0)).toDispatch();
                        setExcelRichTextValue(rng2, SetValue(rng2, finalVal));
                    }
                }

                if ("Value".equals(name)) // если значение из колонки
                {
                    if (taskTable != null) {
                    	taskTable.setProgressValue(++currentValue);
                    }

                    rng = myMap.get(pid);
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
                                if (firstColumnId == pid) {
                                    Dispatch tableRng = myMap.get(tableId);
                                    if (tableRng == null) continue;
                                    Dispatch.call(tableRng, "Insert", new Variant(3));

                                    rng2 = Dispatch.call(tableRng, "Offset", new Variant(-1), new Variant(0)).toDispatch();

                                    Dispatch.call(tableRng, "Copy", rng2);
                                    Dispatch.call(rng2, "ClearComments");
                                }
                            } else {
                                columnMap.put(tableId, pid);
                                firstColumnId = pid;
                                Dispatch tableRng = myMap.get(tableId);
                                if (tableRng == null) continue;
                                Dispatch.call(tableRng, "Insert", new Variant(3));

                                rng2 = Dispatch.call(tableRng, "Offset", new Variant(-1), new Variant(0)).toDispatch();

                                Dispatch.call(tableRng, "Copy", rng2);

                                Dispatch.call(rng2, "ClearComments");
                            }

                            Dispatch mrng = Dispatch.get(rng, "MergeArea").toDispatch();
                            Dispatch.call(mrng, "UnMerge");
                            
                            Dispatch mrng2 = null;
                            
                            if (firstColumnId == pid) {
                                rng2 = Dispatch.call(rng, "Offset", new Variant(-1), new Variant(0)).toDispatch();
                                mrng2 = Dispatch.call(mrng, "Offset", new Variant(-1), new Variant(0)).toDispatch();
                            } else {
                                rng2 = Dispatch.call(rng, "Offset", new Variant(n - count), new Variant(0)).toDispatch();
                                mrng2 = Dispatch.call(mrng, "Offset", new Variant(n - count), new Variant(0)).toDispatch();
                            }
                            Dispatch.call(mrng, "Merge");
                            Dispatch.call(mrng2, "Merge");

                            if (ptNode.getAttribute("decoration") != null) {
                                Dispatch fnt = Dispatch.get(rng2, "Font").toDispatch();
                                Dispatch.put(fnt, "Bold", new Variant(true));
                            }

                            if (srcValue != null && srcValue.length() > 0)
                            	setImageValue(rng2, srcValue);
                            else if (value.length() > 0)
                                setExcelRichTextValue(rng2, SetValue(rng2, value));
                        } else {
                            if (count == 1) {
                                if (ptNode.getAttribute("decoration") != null) {
                                    Dispatch fnt = Dispatch.get(rng, "Font").toDispatch();
                                    Dispatch.put(fnt, "Bold", new Variant(true));
                                }

                                if (srcValue != null && srcValue.length() > 0)
                                	setImageValue(rng, srcValue);
                                else if (value.length() > 0)
                                    setExcelRichTextValue(rng, SetValue(rng, value));

                                myMap.remove(pid);
                                typeMap.remove(pid);
                                initialMap.remove(pid);
                            } else {
                                processedColumns.put(pid, 1);
                                Dispatch mrng = Dispatch.get(rng, "MergeArea").toDispatch();
                                //VARIANT cVal = mrng.GetFormula();

                                //if (cVal.bstrVal. != "") AfxMessageBox(cVal.bstrVal);
                                Dispatch.call(mrng, "UnMerge");
                                Dispatch.call(mrng, "Insert", new Variant(2));

                                String cVal = Dispatch.get(mrng, "Formula").getString();
                                rng2 = Dispatch.call(mrng, "Offset", new Variant(-1), new Variant(0)).toDispatch();
                                //}
                                //if (str=="Row")
                                //{
                                //	rng.Insert(COleVariant((long)(1)));
                                //	rng2 = rng.GetOffset(COleVariant((long)0), COleVariant((long)-1));
                                //}
                                Dispatch.call(mrng, "Copy", rng2);

                                Dispatch.call(rng2, "Merge");
                                Dispatch.call(mrng, "Merge");
                                //if (!(cVal.bstrVal == "")) AfxMessageBox(cVal.bstrVal);
                                if (ptNode.getAttribute("decoration") != null) {
                                    Dispatch fnt = Dispatch.get(rng2, "Font").toDispatch();
                                    Dispatch.put(fnt, "Bold", new Variant(true));
                                }

                                if (srcValue != null && srcValue.length() > 0)
                                	setImageValue(rng2, srcValue);
                                else if (value.length() > 0)
                                    setExcelRichTextValue(rng2, SetValue(rng2, value));
                                else if (cVal.length() > 0) {
                                    if (n == count - 1)
                                        rng = Dispatch.call(rng, "Offset", new Variant(count - 1), new Variant(0)).toDispatch();
                                    else
                                        rng = Dispatch.call(rng, "Offset", new Variant(-1), new Variant(0)).toDispatch();
                                }

                                myMap.put(pid, rng);
                            }
                        }
                    }
                    if (tpid != 0) {
                        processedColumns.put(pid, 1);
                        if (firstColumn) {
                            trng = myMap.get(tpid);

                            if (!oneRow) {
                            	Dispatch.call(trng, "Insert", new Variant(3));
	                            trng2 = Dispatch.call(trng, "Offset", new Variant(-1), new Variant(0)).toDispatch();
	                            Dispatch.call(trng, "Copy", trng2);
                            }
                            fCount++;
                            myMap.put(tpid, trng);

                            rng2 = Dispatch.call(rng, "Offset", new Variant(-1), new Variant(0)).toDispatch();
                            if (srcValue != null && srcValue.length() > 0)
                            	setImageValue(rng2, srcValue);
                            else 
                            	setExcelRichTextValue(rng2, SetValue(rng2, value));
                            //myMap.SetAt(pid, rng);
                        } else {
                            //rng.Insert(COleVariant((long)(2)));
                            //if (count == 1) {
                            //	cCount++;
                            //}
                            rng2 = Dispatch.call(rng, "Offset", new Variant(-cCount--), new Variant(0)).toDispatch();
                            if (srcValue != null && srcValue.length() > 0)
                            	setImageValue(rng2, srcValue);
                            else
                            	setExcelRichTextValue(rng2, SetValue(rng2, value));
                            //myMap.SetAt(pid, rng);
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
                if (ptChildAtr.size() == 0) {
                	value = "";
                } else {
	                ptChildItem = (Attribute) ptChildAtr.get(0);
	                value = ptChildItem.getValue();
                }
                
                trng = myMap.get(tpid);
                trng2 = Dispatch.call(trng, "Range", "A1", "A1").toDispatch();
                startAddress = Dispatch.call(trng2, "Address", new Variant(true), new Variant(true), new Variant(1)).getString();
                String buf = treeMap.get(tpid);
                if (buf.length() == 0 || HasData(childCount, ptChildNodes)) {
                    InsertTreeNodeValue(value, tpid, level, false);
                    firstRowColumn = true;
                    ProcessRowColumns(tpid, childCount, ptChildNodes);
                    ProcessNodes(pid, level + 1, tpid, childCount, oneRow, ptChildNodes, wshs);
                    AddSummary(level, tpid, startAddress);
                    AddCount(level, tpid, startAddress);
                    AddTotal(level, tpid, startAddress);
                    AddFree(level, tpid, startAddress);
                    AddStatTable(level, tpid, value);
                }
            }
            if ("TreeColumn".equals(type)) {
//			AfxMessageBox("begin function ProcessNodes()");
                ptChildItem = (Attribute) ptChildAtr.get(0);
                value = ptChildItem.getValue();
                int inpid = Integer.parseInt(value);
                //String to = ptNode.getAttributeValue("oneRow");

                boolean noempty =  noemptyMap.containsKey(inpid) && noemptyMap.get(inpid);

                ProcessTreeColumnNodes(inpid, level + 1, inpid, noempty, ptNode, wshs);
                hasTree_ = true;
            }
            if ("Column".equals(type) || "ConsColumn".equals(type)) {
                //if (!prev && tpid > 0) {
                //    trng = myMap.get(tpid);
                    //trow = Dispatch.get(trng, "Row").getInt();
                //}
                //prev = true;
                ptChildItem = (Attribute) ptChildAtr.get(0);
                value = ptChildItem.getValue();
                int inpid = Integer.parseInt(value);

                firstConsValue = 0;
                ProcessNodes(inpid, level + 1, tpid, childCount, oneRow, ptChildNodes, wshs);

                firstColumn = false;
                cCount = fCount;
            }// else prev = false;
        }
    }

    private void ProcessTreeColumnNodes(int pid, int level, int tpid, boolean oneRow, Element ptNode, Dispatch wshs) {
        
        String value;
        String startAddress = "";

        // ptNode - Элемент TreeColumn или Value
        List<Element> ptChildValues = ptNode.getChildren("Value"); 
        List<Element> ptChildColumns = ptNode.getChildren("Column");
        if (ptChildColumns.size() == 0) ptChildColumns = ptNode.getChildren("ConsColumn");
        
        int col = 0;
        
        for (Element ptColumn : ptChildColumns) {
        	int colId = Integer.parseInt(ptColumn.getAttributeValue("id"));
        	List<Element> ptVals = ptColumn.getChildren("Value");
            if (ptVals.size() == 0) ptVals = ptColumn.getChildren("ConsValue");
        
        	int row = 0;
        	int rowsCount = ptVals.size();
        	
        	for (Element ptVal : ptVals) {
        		Dispatch trng = myMap.get(tpid);

                int shift = -1;

                if (col == 0 && (!oneRow || row > 0)) {
                	Dispatch.call(trng, "Insert", new Variant(3));
                    Dispatch trng2 = Dispatch.call(trng, "Offset", new Variant(-1), new Variant(0)).toDispatch();
                    Dispatch.call(trng, "Copy", trng2);
                    //shift = -1;
                } else if (col > 0) {
                	shift = -rowsCount + row;
                }
                row++;
                
                myMap.put(tpid, trng);
                
                Dispatch rng = myMap.get(colId);
                if (rng == null) continue;

                value = ptVal.getAttributeValue("str");
                value = (value != null) ? value : "";
                String srcValue = ptVal.getAttributeValue("src");

                Dispatch rng2 = Dispatch.call(rng, "Offset", new Variant(shift), new Variant(0)).toDispatch();
                if (srcValue != null && srcValue.length() > 0)
                	setImageValue(rng2, srcValue);
                else 
                	setExcelRichTextValue(rng2, SetValue(rng2, value));

        	}
        	col++;
        }

        for (Element ptValue : ptChildValues) {
            if (taskTable != null) {
            	taskTable.setProgressValue(++currentValue);
            }

            value = ptValue.getAttributeValue("str");
            
            Dispatch trng = myMap.get(tpid);
            Dispatch trng2 = Dispatch.call(trng, "Range", "A1", "A1").toDispatch();
            startAddress = Dispatch.call(trng2, "Address", new Variant(true), new Variant(true), new Variant(1)).getString();
            String buf = treeMap.get(tpid);
            
            List<Element> ptChildNodes = ptNode.getChildren();
            int childCount = ptChildNodes.size();
            		
            if (buf.length() == 0 || HasData(childCount, ptChildNodes)) {
                InsertTreeNodeValue(value, tpid, level, false);
                firstRowColumn = true;
                ProcessRowColumns(tpid, childCount, ptChildNodes);
                //ProcessNodes(pid, level + 1, tpid, childCount, oneRow, ptChildNodes, wshs);
                ProcessTreeColumnNodes(pid, level + 1, tpid, oneRow, ptValue, wshs);
                AddSummary(level, tpid, startAddress);
                AddCount(level, tpid, startAddress);
                AddTotal(level, tpid, startAddress);
                AddFree(level, tpid, startAddress);
                AddStatTable(level, tpid, value);
            }
            
        }
        
    }

    private void setImageValue(Dispatch rng, String value) {
    	Dispatch wsh = Dispatch.get(rng, "Worksheet").toDispatch();
    	
    	double top = Dispatch.get(rng, "Top").getDouble();
    	double left = Dispatch.get(rng, "Left").getDouble();

    	Dimension d = getImageDim(value);
    	int width = (int)(d.getWidth() * 0.75);
    	int height = (int)(d.getHeight() * 0.75);
    	
    	Dispatch shapes = Dispatch.get(wsh, "Shapes").toDispatch();
    	Dispatch p = Dispatch.call(shapes, "AddPicture", new Variant(value), new Variant(-1), new Variant(-1), new Variant(left), new Variant(top), new Variant(width), new Variant(height)).toDispatch();

    	if (Constants.NEED_EXPAND_EXCEL_CELL) {
	    	int rowIndex = Dispatch.get(rng, "Row").getInt();
	    	int colIndex = Dispatch.get(rng, "Column").getInt();
	    	
	    	Dispatch rows = Dispatch.get(wsh, "Rows").toDispatch();
	    	Dispatch cols = Dispatch.get(wsh, "Columns").toDispatch();
	    	
	    	Dispatch row = Dispatch.call(rows, "Item", new Variant(rowIndex)).toDispatch();
	    	Dispatch col = Dispatch.call(cols, "Item", new Variant(colIndex)).toDispatch();
	    	
	    	Dispatch.put(col, "ColumnWidth", new Variant((double)width/5.5));
	    	Dispatch.put(row, "RowHeight", new Variant(height));
    	}
	}
    
    private Dimension getImageDim(final String path) {
        Dimension result = null;
        try {
        	File f = Funcs.getCanonicalFile(path);
	        ImageInputStream ios = new FileImageInputStream(f);
	        Iterator<ImageReader> iter = ImageIO.getImageReaders(ios);
	        if (iter.hasNext()) {
	            ImageReader reader = iter.next();
                ImageInputStream stream = new FileImageInputStream(f);
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

	private Object SetValue(Dispatch rng, String value) {
    	if (value != null) value = value.replaceAll("\\\r", "");
        
    	String format = Dispatch.get(rng, "NumberFormat").getString();
    	if ("@".equals(format))
    		return value;
    	else {
	    	String temp = value;
	
	    	try {
	            return new Variant(Double.parseDouble(temp));
	        } catch (Exception e1) {
	            temp = temp.replaceAll("\\.", ",");
	            try {
	                return new Variant(Double.parseDouble(temp));
	            } catch (Exception e2) {
	                return value;
	            }
	        }
    	}
    }

    private int HowAddSummary(int level, int tpid) {
        String tpidLev = tpid + "," + level;
        if (!totalMap.containsKey(tpidLev)) return 0;
        return 1;
    }

    private int AddSummary(int level, int tpid, String startAddr) {
        Dispatch rng, rng2, rng3;

        String tpidLev = tpid + "," + level;
        String pids = totalMap.get(tpidLev);

        if (pids == null) return 0;

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

            rng = myMap.get(curPid);
            rng2 = Dispatch.call(rng, "Offset", new Variant(-1), new Variant(0)).toDispatch();
            rng3 = Dispatch.call(rng, "Offset", new Variant(-2), new Variant(0)).toDispatch();

            String addr = Dispatch.call(rng3, "Address", new Variant(true), new Variant(true), new Variant(1)).getString();
            int ifhas = startAddr.indexOf(':', 1);
            String startAddress = (ifhas > -1) ? startAddr.substring(0, ifhas) : startAddr;

            end = startAddress.indexOf('$', 1);
            int end2 = addr.indexOf('$', 1);
            startAddress = startAddress.substring(end);
            startAddress = addr.substring(0, end2) + startAddress;
            if (startAddress.equals(addr)) {
                Dispatch trng, trng2;
                trng = myMap.get(tpid);
                trng2 = Dispatch.call(trng, "Offset", new Variant(-1), new Variant(0)).toDispatch();
                Dispatch.call(trng2, "Select");
                Dispatch.call(trng2, "Delete", new Variant(3));
                return 0;
            }
            String formula = "=ПРОМЕЖУТОЧНЫЕ.ИТОГИ(9; " + startAddress + ":" + addr + ")";
            Dispatch.put(rng2, "FormulaLocal", formula);
        }
        return 1;
    }

    private void InsertTreeNodeValue(String value, int tpid, int level, boolean copy) {
        Dispatch trng, trng2, trng3;

        trng = myMap.get(tpid);

        if (trng == null) return;

        Dispatch.call(trng, "Insert", new Variant(3));
        trng2 = Dispatch.call(trng, "Offset", new Variant(-1), new Variant(0)).toDispatch();

        Dispatch.call(trng, "Copy", trng2);

        trng3 = Dispatch.call(trng, "Offset", new Variant(-1), new Variant(level)).toDispatch();

        if (fastReport) {
            Dispatch fnt = Dispatch.get(trng3, "Font").toDispatch();
            Dispatch.put(fnt, "Bold", new Variant(true));
        }
        trng3 = Dispatch.call(trng3, "Range", "A1", "A1").toDispatch();
        setExcelRichTextValue(trng3, SetValue(trng3, value));

        if (copy) {
            Dispatch fnt2 = Dispatch.get(trng2, "Font").toDispatch();
            Dispatch fnt3 = Dispatch.get(trng3, "Font").toDispatch();

            Dispatch.put(fnt2, "Bold", Dispatch.get(fnt3, "Bold"));
            Dispatch.put(fnt2, "Italic", Dispatch.get(fnt3, "Italic"));
            Dispatch.put(fnt2, "Background", Dispatch.get(fnt3, "Background"));
            Dispatch.put(fnt2, "Color", Dispatch.get(fnt3, "Color"));
            Dispatch.put(fnt2, "FontStyle", Dispatch.get(fnt3, "FontStyle"));
            Dispatch.put(fnt2, "Size", Dispatch.get(fnt3, "Size"));
            Dispatch.put(fnt2, "Underline", Dispatch.get(fnt3, "Underline"));
        }

        myMap.put(tpid, trng);
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
        Dispatch rng, rng2, rng3;
        Dispatch crng, crng2;
        Dispatch trng, trng2;
        String tpidLev = tpid + "," + level;
        String ost = statMap.get(tpidLev);

        if (ost == null) return;
        int start = ost.indexOf('|');
        int id = Integer.parseInt(ost.substring(0, start));
        rng = myMap.get(id);
        trng = myMap.get(tpid);
        String texts = ost.substring(start + 1) + '|';

        int k = 0;
        while (texts.length() > 0) {
            k++;
            int first = texts.indexOf('|');
            int second = texts.indexOf('|', first + 1);
            String textToFind = texts.substring(0, first);
            int colNum = Integer.parseInt(texts.substring(first + 1, second));
            texts = texts.substring(second + 1);
            crng = myMap.get(colNum);
            String value = "1";
            int i = 0;
            while (value.length() > 0) {
                i--;
                crng2 = Dispatch.call(crng, "Offset", new Variant(i), new Variant(0)).toDispatch();
                trng2 = Dispatch.call(trng, "Offset", new Variant(i), new Variant(level)).toDispatch();
                trng2 = Dispatch.call(trng2, "Range", "A1", "A1").toDispatch();
                value = Dispatch.get(trng2, "Value").getString();

                if (k == 1) {
                    rng2 = Dispatch.call(rng, "Offset", new Variant(-1), new Variant(0)).toDispatch();
                    setExcelRichTextValue(rng2, startAddr);
                }
                if (value.equals(textToFind)) {
                    rng2 = Dispatch.call(rng, "Offset", new Variant(-1), new Variant(k)).toDispatch();
                    setExcelRichTextValue(rng2, Dispatch.get(crng2, "Value").getString());
                }
            }
            if ("Formula".equals(textToFind)) {
                rng2 = Dispatch.call(rng, "Offset", new Variant(-1), new Variant(k)).toDispatch();
                rng3 = Dispatch.call(rng, "Offset", new Variant(0), new Variant(k)).toDispatch();
                Dispatch.call(rng3, "Copy", rng2);
            }
        }
        Dispatch.call(rng, "Insert", new Variant(3));
    }

    private void AddCount(int level, int tpid, String startAddr) {
        Dispatch rng, rng2, rng3;

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

            rng = myMap.get(curPid);
            rng2 = Dispatch.call(rng, "Offset", new Variant(-1), new Variant(0)).toDispatch();
            rng3 = Dispatch.call(rng, "Offset", new Variant(-2), new Variant(0)).toDispatch();

            String addr = Dispatch.call(rng3, "Address", new Variant(true), new Variant(true), new Variant(1)).getString();

            int ifhas = startAddr.indexOf(':', 1);
            String startAddress = (ifhas > -1) ? startAddr.substring(0, ifhas) : startAddr;

            end = startAddress.indexOf('$', 1);
            int end2 = addr.indexOf('$', 1);
            startAddress = startAddress.substring(end);
            startAddress = addr.substring(0, end2) + startAddress;
            String formula = "=ПРОМЕЖУТОЧНЫЕ.ИТОГИ(3; " + startAddress + ":" + addr + ")";
            Dispatch.put(rng2, "FormulaLocal", formula);

        }
    }

    private void AddTotal(int level, int tpid, String startAddr) {
        Dispatch rng, rng2, rng3;
        String pids = opMap.get(tpid);
        if (pids == null) return;

        InsertTreeNodeValue("Всего:", tpid, level, true);
        String seps = " ,\t\n";
        StringTokenizer st = new StringTokenizer(pids, seps);
        while (st.hasMoreTokens()) {
            int curPid = Integer.parseInt(st.nextToken());
            String operation = st.nextToken();
            rng = myMap.get(curPid);
            if (tpid == 0) Dispatch.call(rng, "Insert", new Variant(2));

            rng2 = Dispatch.call(rng, "Offset", new Variant(-1), new Variant(0)).toDispatch();
            rng3 = Dispatch.call(rng, "Offset", new Variant(-2), new Variant(0)).toDispatch();

            String addr = Dispatch.call(rng3, "Address", new Variant(true), new Variant(true), new Variant(1)).getString();

            int ifhas = startAddr.indexOf(':', 1);
            String startAddress = (ifhas > -1) ? startAddr.substring(0, ifhas) : startAddr;

            int end = startAddress.indexOf('$', 1);
            int end2 = addr.indexOf('$', 1);
            startAddress = startAddress.substring(end);
            startAddress = addr.substring(0, end2) + startAddress;
            String formula = "=ПРОМЕЖУТОЧНЫЕ.ИТОГИ(" + operation + "; " + startAddress + ":" + addr + ")";

            if (fastReport) {
                Dispatch fnt = Dispatch.get(rng2, "Font").toDispatch();
                Dispatch.put(fnt, "Bold", new Variant(true));
            }
            Dispatch.put(rng2, "NumberFormat", "");
            Dispatch.put(rng2, "FormulaLocal", formula);
        }
    }

    private void AddFree(int level, int tpid, String startAddr) {
        Dispatch rng, rng2, rng3;

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

        rng = myMap.get(pid1);
        rng2 = Dispatch.call(rng, "Offset", new Variant(-1), new Variant(0)).toDispatch();
        rng3 = Dispatch.call(rng, "Offset", new Variant(-2), new Variant(0)).toDispatch();

        String addr = Dispatch.call(rng3, "Address", new Variant(true), new Variant(true), new Variant(1)).getString();

        int ifhas = startAddr.indexOf(':', 1);
        String startAddress = (ifhas > -1) ? startAddr.substring(0, ifhas) : startAddr;

        end = startAddress.indexOf('$', 1);
        int end2 = addr.indexOf('$', 1);
        startAddress = startAddress.substring(end);
        startAddress = addr.substring(0, end2) + startAddress;
        String formula = "=ПРОМЕЖУТОЧНЫЕ.ИТОГИ(3; " + startAddress + ":" + addr + ")";
        Dispatch.put(rng2, "FormulaLocal", formula);

        InsertTreeNodeValue(sum2, tpid, level, true);
        rng = myMap.get(pid2);
        rng2 = myMap.get(pid1);

        rng2 = Dispatch.call(rng2, "Offset", new Variant(-1), new Variant(0)).toDispatch();
        rng3 = Dispatch.call(rng, "Offset", new Variant(-2), new Variant(0)).toDispatch();

        String addr2 = Dispatch.call(rng3, "Address", new Variant(true), new Variant(true), new Variant(1)).getString();

        ifhas = startAddr.indexOf(':', 1);
        String startAddress2 = (ifhas > -1) ? startAddr.substring(0, ifhas) : startAddr;

        end = startAddress2.indexOf('$', 1);
        end2 = addr2.indexOf('$', 1);

        startAddress2 = startAddress2.substring(end);
        startAddress2 = addr2.substring(0, end2) + startAddress2;
        formula = "=ПРОМЕЖУТОЧНЫЕ.ИТОГИ(3; " + startAddress2 + ":" + addr2 + ")";
        Dispatch.put(rng2, "FormulaLocal", formula);

        InsertTreeNodeValue(sum3, tpid, level, true);
        rng2 = myMap.get(pid1);
        rng2 = Dispatch.call(rng2, "Offset", new Variant(-1), new Variant(0)).toDispatch();

        formula = "=ПРОМЕЖУТОЧНЫЕ.ИТОГИ(3; " + startAddress + ":" + addr + ")-ПРОМЕЖУТОЧНЫЕ.ИТОГИ(3; " + startAddress2 + ":" + addr2 + ")";
        Dispatch.put(rng2, "FormulaLocal", formula);
    }

    private void ProcessRowColumns(int tpid, int count, List tempNodes) {
        Element ptNode;
        Attribute ptChildItem;
        List ptChildAtr;
        String value;
        String name;
        Dispatch rng, rng2, trng, trng2;

        for (int n = 0; n < count; n++) {
            ptNode = (Element) tempNodes.get(n);
            ptChildAtr = ptNode.getAttributes();
            name = ptNode.getName();

            if ("RowColumn".equals(name) && tpid > 0) {
                ptChildItem = (Attribute) ptChildAtr.get(0);
                value = ptChildItem.getValue();

                int id = Integer.parseInt(value);

                rng = myMap.get(id);
                if (rng == null) continue;

                ptChildItem = (Attribute) ptChildAtr.get(1);
                value = ptChildItem.getValue();

                if (firstRowColumn) {
                    trng = myMap.get(tpid);

                    Dispatch.call(trng, "Insert", new Variant(3));
                    trng2 = Dispatch.call(trng, "Offset", new Variant(-1), new Variant(0)).toDispatch();

                    Dispatch.call(trng, "Copy", trng2);

                    myMap.put(tpid, trng);

                    rng2 = Dispatch.call(rng, "Offset", new Variant(-1), new Variant(0)).toDispatch();
                    setExcelRichTextValue(rng2, SetValue(rng2, value));
                } else {
                    rng2 = Dispatch.call(rng, "Offset", new Variant(-1), new Variant(0)).toDispatch();
                    setExcelRichTextValue(rng2, SetValue(rng2, value));
                }
                firstRowColumn = false;
            }
        }
    }

    private void protectWorkbook(Dispatch wb) {
    	if (pd != null && pd.length() > 0) {    	
	        Dispatch wshs = Dispatch.get(wb, "Worksheets").toDispatch();
	        int wshsCount = Dispatch.get(wshs, "Count").getInt();
	
	        for (int w = 0; w < wshsCount; w++) { // Пробегаемся по всем листам в книге Excel
	        	Dispatch wsh = Dispatch.call(wshs, "Item", new Variant(w + 1)).toDispatch();
	        	protectWorksheet(wsh);
	        }
    	}
    }
    
    private void protectWorksheet(Dispatch wsh) {
    	// 3 - только чтение
    	if (pd != null && pd.length() > 0)
    		Dispatch.call(wsh, "Protect", pd);
    }

    private void protectDocument(Dispatch oDoc) {
    	// 3 - только чтение
    	if (pd != null && pd.length() > 0)
    		Dispatch.call(oDoc, "Protect", new Variant(3), new Variant(false), pd);
    }

    private void setParagraphFormatValue(Dispatch oSel, String rich) {
        if (rich == null || rich.length() == 0)
            Dispatch.put(oSel, "Text", rich);
        else {
            Pattern p = Pattern.compile("\\[c\\](.*?\\s*?)*?\\[/c\\]");
            Matcher m = p.matcher(rich);

            int lastPos = 0;
            Dispatch pft = Dispatch.get(oSel, "ParagraphFormat").toDispatch();
            int alt = Dispatch.get(pft, "Alignment").getInt();

            for (int i = 0; m.find(i); i = m.end()) {
                String s = rich.substring(lastPos, m.start());
                pft = Dispatch.get(oSel, "ParagraphFormat").toDispatch();
                Dispatch.put(pft, "Alignment", new Variant(alt));
                setRichTextValue(oSel, s);
                Dispatch.call(oSel, "Collapse", new Variant(0));
                Dispatch.call(oSel, "TypeParagraph");

                pft = Dispatch.get(oSel, "ParagraphFormat").toDispatch();
                Dispatch.put(pft, "Alignment", new Variant(1));
                s = rich.substring(m.start() + 3, m.end() - 4);
                setRichTextValue(oSel, s);
                Dispatch.call(oSel, "Collapse", new Variant(0));
                Dispatch.call(oSel, "TypeParagraph");

                lastPos = m.end();
            }

            setRichTextValue(oSel, rich.substring(lastPos, rich.length()));
            pft = Dispatch.get(oSel, "ParagraphFormat").toDispatch();
            Dispatch.put(pft, "Alignment", new Variant(alt));
        }
    }

    private void setRichTextValue(Dispatch oSel, String rich) {
        Pattern p = Pattern.compile("\\[b\\](.*?\\s*?)*?\\[/b\\]");
        Matcher m = p.matcher(rich);

        int lastPos = 0;
        Dispatch fnt = Dispatch.get(oSel, "Font").toDispatch();
        boolean bold = Dispatch.get(fnt, "Bold").getInt() != 0;

        for (int i = 0; m.find(i); i = m.end()) {
            String s = rich.substring(lastPos, m.start());
            setUnderlineValue(oSel, s, bold);

            s = rich.substring(m.start() + 3, m.end() - 4);

            setUnderlineValue(oSel, s, true);

            lastPos = m.end();
        }

        setUnderlineValue(oSel, rich.substring(lastPos, rich.length()), bold);
    }

    private void setUnderlineValue(Dispatch oSel, String rich, boolean bold) {
        Pattern p = Pattern.compile("\\[u\\](.*\\s*)*\\[/u\\]");
        Matcher m = p.matcher(rich);

        int lastPos = 0;
        Dispatch fnt = Dispatch.get(oSel, "Font").toDispatch();
        int under = Dispatch.get(fnt, "Underline").getInt();

        for (int i = 0; m.find(i); i = m.end()) {
            String s = rich.substring(lastPos, m.start());
            setItalicValue(oSel, s, bold, under);

            s = rich.substring(m.start() + 3, m.end() - 4);

            setItalicValue(oSel, s, bold, 1);

            lastPos = m.end();
        }

        setItalicValue(oSel, rich.substring(lastPos, rich.length()), bold, under);
    }

    private void setItalicValue(Dispatch oSel, String rich, boolean bold, int under) {
        Pattern p = Pattern.compile("\\[i\\](.*\\s*)*\\[/i\\]");
        Matcher m = p.matcher(rich);

        int lastPos = 0;
        Dispatch fnt = Dispatch.get(oSel, "Font").toDispatch();
        Dispatch.put(fnt, "Bold", new Variant(bold));
        Dispatch.put(fnt, "Underline", new Variant(under));
        boolean ital = Dispatch.get(fnt, "Italic").getInt() != 0;

        for (int i = 0; m.find(i); i = m.end()) {
            String s = rich.substring(lastPos, m.start());

            Dispatch.put(oSel, "Text", s);
            fnt = Dispatch.get(oSel, "Font").toDispatch();
            Dispatch.put(fnt, "Italic", new Variant(ital));
            Dispatch.put(fnt, "Bold", new Variant(bold));
            Dispatch.put(fnt, "Underline", new Variant(under));
            Dispatch.call(oSel, "Collapse", new Variant(0));

            s = rich.substring(m.start() + 3, m.end() - 4);

            Dispatch.put(oSel, "Text", s);
            fnt = Dispatch.get(oSel, "Font").toDispatch();
            Dispatch.put(fnt, "Italic", new Variant(true));
            Dispatch.put(fnt, "Bold", new Variant(bold));
            Dispatch.put(fnt, "Underline", new Variant(under));
            Dispatch.call(oSel, "Collapse", new Variant(0));

            lastPos = m.end();
        }

        Dispatch.put(oSel, "Text", rich.substring(lastPos, rich.length()));
        fnt = Dispatch.get(oSel, "Font").toDispatch();
        Dispatch.put(fnt, "Italic", new Variant(ital));
        Dispatch.put(fnt, "Bold", new Variant(bold));
        Dispatch.put(fnt, "Underline", new Variant(under));
        Dispatch.call(oSel, "Collapse", new Variant(0));
    }

    private void setExcelRichTextValue(Dispatch rng, Object rich) {
    	if (rich instanceof String) {
    		String txt = (String) rich;
	        Pattern p = Pattern.compile("\\[b\\](.*?\\s*?)*?\\[/b\\]");
	        Matcher m = p.matcher(txt);
	
	        int lastPos = 0;
	        Dispatch fnt = Dispatch.get(rng, "Font").toDispatch();
	        
	        boolean bold = false;
	        try {
	        	Variant v = Dispatch.get(fnt, "Bold");
	        	if (v.getvt() == Variant.VariantBoolean)
	        		bold = Dispatch.get(fnt, "Bold").getBoolean();
	        } catch (Exception e) {
	        }
	
	        if (!bold) {
	        	StringBuilder sb = new StringBuilder();
	        	List<Integer> boldPos = new ArrayList<Integer>();
		        for (int i = 0; m.find(i); i = m.end()) {
		            sb.append(txt.substring(lastPos, m.start()));
		
		            boldPos.add(sb.length() + 1);

		            sb.append(txt.substring(m.start() + 3, m.end() - 4));

		            boldPos.add(sb.length() + 1);

		            lastPos = m.end();
		        }
		        sb.append(txt.substring(lastPos, txt.length()));
	            Dispatch.put(rng, "Value", sb.toString());
		        
		        for (int i = 0; i < boldPos.size(); i+=2) {
		        	Dispatch chs = Dispatch.call(rng, "Characters", new Variant(boldPos.get(i)), new Variant(boldPos.get(i + 1) - boldPos.get(i))).getDispatch();
			        Dispatch font = Dispatch.get(chs, "Font").toDispatch();
		            Dispatch.put(font, "Bold", new Variant(true));
		        }
	        } else {
	            Dispatch.put(rng, "Value", rich);
	        }
    	} else {
            Dispatch.put(rng, "Value", rich);
    	}
    }

    public static void main(String[] args) {
        try {
            new ReportWrapper("D:\\tmp\\xxx.xls", "D:\\tmp\\xxx.xml", "", "", "", Constants.MSEXCEL_EDITOR, true).print();
            //new ReportWrapper("D:\\tmp\\liferay\\template.doc", "D:\\tmp\\liferay\\xxx.xml", "", "", "", Constants.MSWORD_EDITOR, true).print();
            //new ReportWrapper("D:\\tmp\\xxx.xls", "D:\\tmp\\xxx.xml", "", "", "", Constants.MSEXCEL_EDITOR, true).print();
            //new ReportWrapper("D:\\WORK\\or3final\\doc\\xxx2.xls", "D:\\WORK\\or3final\\doc\\xxx.xml", "", "", "", 1, true).print();
            //new ReportWrapper("D:\\WORK\\or3final\\doc\\xxx3.xls", "D:\\WORK\\or3final\\doc\\xxx.xml", "", "", "", 1, true).print();
            ///new ReportWrapper("D:\\WORK\\or3final\\doc\\xxx4.xls", "D:\\WORK\\or3final\\doc\\xxx.xml", "", "", "", 1, true).print();
            //new ReportWrapper("D:\\WORK\\or3final\\doc\\xxx5.xls", "D:\\WORK\\or3final\\doc\\xxx.xml", "", "", "", 1, true).print();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
