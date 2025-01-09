package kz.tamur.comps;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.jdom.Element;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import kz.tamur.rt.HtmlReportWrapper;
import kz.tamur.rt.ReportWrapperPOI;
import kz.tamur.util.Funcs;
import kz.tamur.rt.ReportWrapper;
import kz.tamur.rt.ReportObserver;

/**
 * Created by IntelliJ IDEA.
 * User: Администратор
 * Date: 20.01.2005
 * Time: 10:51:03
 * To change this template use File | Settings | File Templates.
 */
public class ReportLauncher {
    public static void createReport(long id, long langId, String fileName, int editorType,
            String title, String webUrl, String uuid) {
		new ReportWrapper(fileName, title, id, langId, editorType, webUrl, uuid).start();
	}

    public static void viewReport(String fileName, String dataFileName, int editorType, String title, String macros, String pass) {
    	String type = System.getProperty("reportType");
    	if ("jacob".equals(type))
    		new ReportWrapper(fileName, dataFileName, title, macros, pass, editorType, true).start();
    	else
    		new ReportWrapperPOI(fileName, dataFileName, title, macros, pass, editorType, true).start();
    }

    public static void viewReport(String fileName, String dataFileName, int editorType, String title, String macros, String pass, ReportObserver taskTable, long flowId) {
    	String type = System.getProperty("reportType");
    	if ("jacob".equals(type))
    		new ReportWrapper(fileName, dataFileName, title, macros, pass, editorType, true, taskTable, flowId, -1).start();
    	else
    		new ReportWrapperPOI(fileName, dataFileName, title, macros, pass, editorType, true, taskTable, flowId).start();
    }

    public static void viewHtmlReport(byte[] xml, byte[] html, String title, ReportObserver taskTable, long flowId, File dir) {
		new HtmlReportWrapper(xml, html, title, true, taskTable, flowId, dir).start();
    }

    public static File viewHtmlReportI(byte[] xml, byte[] html, String title, ReportObserver taskTable, long flowId, File dir) {
		return new HtmlReportWrapper(xml, html, title, false, taskTable, flowId, dir).print();
    }

    public static File viewPdfReport(byte[] pdf, String title, ReportObserver taskTable, long flowId, File dir, boolean showAfterComplete) {
        long time = System.currentTimeMillis();
        
        File res = null;
        try {
            res = Funcs.createTempFile("xxx", ".pdf", dir);
            res.deleteOnExit();
            FileOutputStream os = new FileOutputStream(res);
            os.write(pdf);
            os.close();
            if (showAfterComplete) {
                Runtime r = Runtime.getRuntime();
                r.exec("cmd /c \"" + res.getAbsolutePath() + "\"");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Forming report time: " + (System.currentTimeMillis() - time));
        
        if (taskTable != null && flowId > 0) {
            taskTable.setReportComplete(flowId);
        }

        return res;
    }

    public static void viewReportI(String fileName, String dataFileName, int editorType, String title, String macros, String pass) {
    	String type = System.getProperty("reportType");
    	if ("jacob".equals(type))
    		new ReportWrapper(fileName, dataFileName, title, macros, pass, editorType, false).print();
    	else
    		new ReportWrapperPOI(fileName, dataFileName, title, macros, pass, editorType, false).print();
    }

    public static void viewReportI(String fileName, String dataFileName, int editorType, String title, String macros, String pass, ReportObserver taskTable, long flowId) {
    	viewReportI(fileName, dataFileName, editorType, title, macros, pass, taskTable, flowId, -1);
    }
    
    public static void viewReportI(String fileName, String dataFileName, int editorType, String title, String macros, String pass, ReportObserver taskTable, long flowId, int format) {
    	String type = System.getProperty("reportType");
    	if ("jacob".equals(type))
    		new ReportWrapper(fileName, dataFileName, title, macros, pass, editorType, false, taskTable, flowId, format).print();
    	else
    		new ReportWrapperPOI(fileName, dataFileName, title, macros, pass, editorType, false, taskTable, flowId).print();
    }
    
    public static void viewReportI(String fileName, String dataFileName, int editorType, String title, String macros, String pass, ReportObserver taskTable, long flowId, int format, String type) {
    	if (type == null) type = System.getProperty("reportType");
    	
    	if ("jacob".equals(type))
    		new ReportWrapper(fileName, dataFileName, title, macros, pass, editorType, false, taskTable, flowId, format).print();
    	else
    		new ReportWrapperPOI(fileName, dataFileName, title, macros, pass, editorType, false, taskTable, flowId).print();
    }

    public static void viewReportWaitI(String fileName, String dataFileName, int editorType, String title, String macros, String pass) {
    	String type = System.getProperty("reportType");
    	if ("jacob".equals(type))
    		new ReportWrapper(fileName, dataFileName, title, macros, pass, editorType, false).print();
    	else
    		new ReportWrapperPOI(fileName, dataFileName, title, macros, pass, editorType, false).print();
    }

    public static void viewFastReport(String dataFileName) {
        try {
            Runtime r = Runtime.getRuntime();
            //switch (editorType) {
            //  case 0:
            r.exec("createReportE -\"" + dataFileName + "\"-");
            //    break;
            //  case 1:
            //    r.exec("createReportE -\"" + fileName + "\"- -\"" + dataFileName + "\"-");
            //    break;
            //}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void generateReport(Element report, long editorType, String title) {
    	generateReport(report, editorType, title, null);
    }
    
    public static void generateReport(Element report, long editorType, String title, String fileName) {
        if (editorType == 0) {
            ComThread.InitSTA();
            ActiveXComponent excel = ActiveXComponent.createNewInstance("Excel.Application");

            Dispatch wbooks = null;
            Dispatch wbook = null;
            try {
                wbooks = excel.getProperty("Workbooks").toDispatch();
                wbook = Dispatch.call(wbooks, "Add").toDispatch();
                Dispatch sheet = Dispatch.call(wbook, "Worksheets", new Variant(1)).toDispatch();

                String name = report.getChildText("Title");
                if (name != null) {
                    int row = 1;
                    if (name != null) {
                        row++;
                    }

                    row++;

                    List cols = report.getChild("Criteria").getChildren("Crit");
                    row += cols.size() + 1;

                    cols = report.getChildren("Column");
                    int tWidth = cols.size();
                    Dispatch cell1 = Dispatch.call(sheet, "Cells", new Variant(row), new Variant(1)).toDispatch();
                    int j = 0;
                    for (int i = 0; i < cols.size(); i++) {
                        Element col = (Element) cols.get(i);
                        Dispatch cell = Dispatch.call(sheet, "Cells", new Variant(row), new Variant(i + 1)).toDispatch();
                        Dispatch.put(cell, "Value", new Variant(col.getAttributeValue("name")));
                        Dispatch font = Dispatch.call(cell, "Font").toDispatch();
                        Dispatch.put(font, "Bold", new Variant(true));

                        List values = col.getChildren("Value");

                        String type = col.getAttributeValue("type");
                        String align = col.getAttributeValue("align");

                        for (j = 0; j < values.size(); j++) {
                            Element value = (Element) values.get(j);
                            cell = Dispatch.call(sheet, "Cells", new Variant(row + 1 + j), new Variant(i + 1)).toDispatch();

                            if ("string".equals(type)) {
                            	Dispatch.put(cell, "NumberFormat", "@");
                            }

                            if ("1".equals(align)) { // right
                                Dispatch.put(cell, "HorizontalAlignment", new Variant(-4152));
                            } else if ("2".equals(align)) { // center
                                Dispatch.put(cell, "HorizontalAlignment", new Variant(-4108));
                            }
                            
                            Dispatch.put(cell, "VerticalAlignment", new Variant(-4160));
                            Dispatch.put(cell, "WrapText", new Variant(true));
                            Dispatch.put(cell, "Value", new Variant(value.getText()));
                        }
                        String width = col.getAttributeValue("width");
                        Dispatch column = Dispatch.call(sheet, "Columns", new Variant(i + 1)).toDispatch();
                        if (width != null && width.length() > 0) {
                            Dispatch.put(column, "ColumnWidth", new Variant(Integer.valueOf(width)));
                        } else
                            Dispatch.call(column, "AutoFit");
                    }

                    Dispatch cell2 = Dispatch.call(sheet, "Cells", new Variant(row + j), new Variant(tWidth)).toDispatch();

                    Dispatch range = Dispatch.call(sheet, "Range", cell1, cell2).toDispatch();
                    Dispatch border = Dispatch.call(range, "Borders", new Variant(7)).toDispatch();
                    Dispatch.put(border, "LineStyle", new Variant(1));
                    Dispatch.put(border, "Weight", new Variant(2));
                    Dispatch.put(border, "ColorIndex", new Variant(-4105));
                    border = Dispatch.call(range, "Borders", new Variant(8)).toDispatch();
                    Dispatch.put(border, "LineStyle", new Variant(1));
                    Dispatch.put(border, "Weight", new Variant(2));
                    Dispatch.put(border, "ColorIndex", new Variant(-4105));
                    border = Dispatch.call(range, "Borders", new Variant(9)).toDispatch();
                    Dispatch.put(border, "LineStyle", new Variant(1));
                    Dispatch.put(border, "Weight", new Variant(2));
                    Dispatch.put(border, "ColorIndex", new Variant(-4105));
                    border = Dispatch.call(range, "Borders", new Variant(10)).toDispatch();
                    Dispatch.put(border, "LineStyle", new Variant(1));
                    Dispatch.put(border, "Weight", new Variant(2));
                    Dispatch.put(border, "ColorIndex", new Variant(-4105));
                    try {
                        border = Dispatch.call(range, "Borders", new Variant(11)).toDispatch();
                        Dispatch.put(border, "LineStyle", new Variant(1));
                        Dispatch.put(border, "Weight", new Variant(2));
                        Dispatch.put(border, "ColorIndex", new Variant(-4105));
                        border = Dispatch.call(range, "Borders", new Variant(12)).toDispatch();
                        Dispatch.put(border, "LineStyle", new Variant(1));
                        Dispatch.put(border, "Weight", new Variant(2));
                        Dispatch.put(border, "ColorIndex", new Variant(-4105));
                    } catch (Exception e) {
                    }

                    row = 1;
                    if (name != null) {
                        Dispatch cell = Dispatch.call(sheet, "Cells", new Variant(row), new Variant(1)).toDispatch();
                        Dispatch.put(cell, "Value", new Variant(name));
                        Dispatch font = Dispatch.call(cell, "Font").toDispatch();
                        Dispatch.put(font, "Bold", new Variant(true));

                        cell2 = Dispatch.call(sheet, "Cells", new Variant(row), new Variant(tWidth)).toDispatch();
                        range = Dispatch.call(sheet, "Range", cell, cell2).toDispatch();
                        Dispatch.put(range, "MergeCells", new Variant(true));
                        Dispatch.put(range, "HorizontalAlignment", new Variant(-4108));
                        row++;
                    }

                    row++;

                    cols = report.getChild("Criteria").getChildren("Crit");
                    for (int i = 0; i < cols.size(); i++) {
                        Element col = (Element) cols.get(i);
                        Dispatch cell = Dispatch.call(sheet, "Cells", new Variant(row++), new Variant(1)).toDispatch();
                        Dispatch.put(cell, "Value", new Variant(col.getText()));
                    }
                } else {
                    int row = 1;
                    Element header = report.getChild("Header");
                    List children = header.getChildren("Cell");

                    String title1 = report.getChildText("Title1");
                    if (title1 != null) {
                        Dispatch cell = Dispatch.call(sheet, "Cells", new Variant(row), new Variant(1)).toDispatch();
                        Dispatch.put(cell, "Value", new Variant(title1));
                        Dispatch font = Dispatch.call(cell, "Font").toDispatch();
                        Dispatch.put(font, "Bold", new Variant(true));

                        Dispatch cell3 = Dispatch.call(sheet, "Cells", new Variant(row), new Variant(children.size())).toDispatch();
                        Dispatch range2 = Dispatch.call(sheet, "Range", cell, cell3).toDispatch();
                        Dispatch.put(range2, "MergeCells", new Variant(true));
                        Dispatch.put(range2, "HorizontalAlignment", new Variant(-4108));
                        row++;
                    }
                    String title2 = report.getChildText("Title2");
                    if (title2 != null) {
                        Dispatch cell = Dispatch.call(sheet, "Cells", new Variant(row), new Variant(1)).toDispatch();
                        Dispatch.put(cell, "Value", new Variant(title2));
                        Dispatch font = Dispatch.call(cell, "Font").toDispatch();
                        Dispatch.put(font, "Bold", new Variant(true));

                        Dispatch cell3 = Dispatch.call(sheet, "Cells", new Variant(row), new Variant(children.size())).toDispatch();
                        Dispatch range2 = Dispatch.call(sheet, "Range", cell, cell3).toDispatch();
                        Dispatch.put(range2, "MergeCells", new Variant(true));
                        Dispatch.put(range2, "HorizontalAlignment", new Variant(-4108));
                        row++;
                    }

                    row++;

                    Dispatch cell1 = Dispatch.call(sheet, "Cells", new Variant(row), new Variant(1)).toDispatch();

                    for (int i = 0; i < children.size(); i++) {
                        Element child = (Element) children.get(i);
                        Dispatch cell = Dispatch.call(sheet, "Cells", new Variant(row), new Variant(i + 1)).toDispatch();
                        Dispatch.put(cell, "Value", child.getText());
                        Dispatch font = Dispatch.call(cell, "Font").toDispatch();
                        Dispatch.put(font, "Bold", new Variant(true));
                    }
                    row++;
                    List rowTags = report.getChildren("Row");
                    for (int i = 0; i < rowTags.size(); i++) {
                        Element rowTag = (Element) rowTags.get(i);
                        children = rowTag.getChildren("Cell");
                        for (int j = 0; j < children.size(); j++) {
                            Element child = (Element) children.get(j);
                            Dispatch cell = Dispatch.call(sheet, "Cells", new Variant(row), new Variant(j + 1)).toDispatch();
                            //Dispatch.put(cell, "NumberFormat", 49);
                            String type = child.getAttributeValue("type");
                            if ("string".equals(type)) {
                            	Dispatch.put(cell, "NumberFormat", "@");
                            }

                            String align = child.getAttributeValue("type");
                            if ("1".equals(align)) { // right
                                Dispatch.put(cell, "HorizontalAlignment", new Variant(-4152));
                            } else if ("2".equals(align)) { // center
                                Dispatch.put(cell, "HorizontalAlignment", new Variant(-4108));
                            }
                            Dispatch.put(cell, "VerticalAlignment", new Variant(-4160));
                            Dispatch.put(cell, "WrapText", new Variant(true));
                            Dispatch.put(cell, "Value", " " + child.getText());
                        }
                        row++;
                    }

                    for (int i = 0; i < children.size(); i++) {
                        Dispatch column = Dispatch.call(sheet, "Columns", new Variant(i + 1)).toDispatch();
                        Dispatch.call(column, "AutoFit");
                    }

                    Dispatch cell2 = Dispatch.call(sheet, "Cells", new Variant(--row), new Variant(children.size())).toDispatch();

                    Dispatch range = Dispatch.call(sheet, "Range", cell1, cell2).toDispatch();
                    Dispatch border = Dispatch.call(range, "Borders", new Variant(7)).toDispatch();
                    Dispatch.put(border, "LineStyle", new Variant(1));
                    Dispatch.put(border, "Weight", new Variant(2));
                    Dispatch.put(border, "ColorIndex", new Variant(-4105));
                    border = Dispatch.call(range, "Borders", new Variant(8)).toDispatch();
                    Dispatch.put(border, "LineStyle", new Variant(1));
                    Dispatch.put(border, "Weight", new Variant(2));
                    Dispatch.put(border, "ColorIndex", new Variant(-4105));
                    border = Dispatch.call(range, "Borders", new Variant(9)).toDispatch();
                    Dispatch.put(border, "LineStyle", new Variant(1));
                    Dispatch.put(border, "Weight", new Variant(2));
                    Dispatch.put(border, "ColorIndex", new Variant(-4105));
                    border = Dispatch.call(range, "Borders", new Variant(10)).toDispatch();
                    Dispatch.put(border, "LineStyle", new Variant(1));
                    Dispatch.put(border, "Weight", new Variant(2));
                    Dispatch.put(border, "ColorIndex", new Variant(-4105));
                    try {
                        border = Dispatch.call(range, "Borders", new Variant(11)).toDispatch();
                        Dispatch.put(border, "LineStyle", new Variant(1));
                        Dispatch.put(border, "Weight", new Variant(2));
                        Dispatch.put(border, "ColorIndex", new Variant(-4105));
                        border = Dispatch.call(range, "Borders", new Variant(12)).toDispatch();
                        Dispatch.put(border, "LineStyle", new Variant(1));
                        Dispatch.put(border, "Weight", new Variant(2));
                        Dispatch.put(border, "ColorIndex", new Variant(-4105));
                    } catch (Exception e) {
                    }
                }
            } finally {
            	if (fileName != null) {
            		if (wbook != null) Dispatch.call(wbook, "SaveAs", fileName, new Variant(-4143), "", "", new Variant(false), new Variant(false), new Variant(0), new Variant(2));
            	} else
            		excel.setProperty("Visible", new Variant(true));
            	
                ComThread.Release();
            }
        }
    }

    public static void generateReportPoi(Element report, long editorType, String title, String fileName) {
        if (editorType == 0) {
            HSSFWorkbook wb=null;
            try {
                String name = report.getChildText("Title");
                wb = new HSSFWorkbook();
                HSSFSheet sheet = (name != null && name.length() > 0) ? wb.createSheet(name) : wb.createSheet();
                
                // Стиль заголовков таблицы
                HSSFCellStyle csh = wb.createCellStyle();
            	csh.setWrapText(true);
            	csh.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            	csh.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            	csh.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            	csh.setBottomBorderColor(IndexedColors.BLACK.getIndex());
            	csh.setBorderLeft(CellStyle.BORDER_THIN);
            	csh.setLeftBorderColor(IndexedColors.BLACK.getIndex());
            	csh.setBorderRight(CellStyle.BORDER_THIN);
            	csh.setRightBorderColor(IndexedColors.BLACK.getIndex());
            	csh.setBorderTop(CellStyle.BORDER_THIN);
            	csh.setTopBorderColor(IndexedColors.BLACK.getIndex());
            	
            	HSSFFont font=wb.createFont();
            	font.setBoldweight(Font.BOLDWEIGHT_BOLD);
            	csh.setFont(font);
                
            	// Стиль заголовка отчета
            	HSSFCellStyle csh0 = wb.createCellStyle();
            	csh0.setWrapText(true);
            	csh0.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            	csh0.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            	csh0.setFont(font);

            	// Стиль ячейки таблицы
            	HSSFCellStyle csc = wb.createCellStyle();
            	csc.setWrapText(true);
            	csc.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            	csc.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
            	csc.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            	csc.setBottomBorderColor(IndexedColors.BLACK.getIndex());
            	csc.setBorderLeft(CellStyle.BORDER_THIN);
            	csc.setLeftBorderColor(IndexedColors.BLACK.getIndex());
            	csc.setBorderRight(CellStyle.BORDER_THIN);
            	csc.setRightBorderColor(IndexedColors.BLACK.getIndex());
            	csc.setBorderTop(CellStyle.BORDER_THIN);
            	csc.setTopBorderColor(IndexedColors.BLACK.getIndex());
            	
            	HSSFCellStyle csl = wb.createCellStyle();
            	csl.cloneStyleFrom(csc);
            	csl.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            	
            	HSSFCellStyle csr = wb.createCellStyle();
            	csr.cloneStyleFrom(csc);
            	csr.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
            	
            	int rown = 1;
            	if(name!=null){
            		HSSFRow row0 = sheet.createRow(rown);
            		HSSFCell cell0 = row0.createCell(0);
            		cell0.setCellStyle(csh0);
            		cell0.setCellValue(name);
            	}
                rown++;

                rown++;

                List colsc = report.getChild("Criteria").getChildren("Crit");
                for (int c = 0; c < colsc.size(); c++) {
                	HSSFRow rowc = sheet.createRow(rown+ 1 + c);
                    Element col = (Element) colsc.get(c);
                    HSSFCell cellc = rowc.createCell(0);
            		cellc.setCellValue(col.getText());
                }
                rown += colsc.size() + 1;

                List cols = report.getChildren("Column");
                int tWidth = cols.size();
        		sheet.addMergedRegion(new CellRangeAddress(1,1,0,tWidth-1));
                int rHeader=rown + 1;
                HSSFRow row1 = sheet.createRow(rown);
                HSSFCell cell;
                int j = 0;
                ArrayList<HSSFRow> rows=new ArrayList<HSSFRow>();
                HSSFRow row;
                for (int i = 0; i < cols.size(); i++) {
                    Element col = (Element) cols.get(i);
                    cell = row1.createCell(i);
            		cell.setCellStyle(csh);
            		cell.setCellValue(col.getAttributeValue("name"));
                    
            		String type = col.getAttributeValue("type");
                    String align = col.getAttributeValue("align");
                    String width = col.getAttributeValue("width");
                    if (width != null && width.length() > 0)
                       	sheet.setColumnWidth(i,Integer.valueOf(width)*300);
                    
                    List values = col.getChildren("Value");
                    for (j = 0; j < values.size(); j++) {
                    	if(rows.size()>j){
                    		row=rows.get(j);
                    	}else{
                            row = sheet.createRow(rown+ 1 + j);
                            rows.add(row);
                    	}
                        Element value = (Element) values.get(j);
                        cell = row.createCell(i);

                        if ("string".equals(type))
                        	cell.setCellType(HSSFCell.CELL_TYPE_STRING);

                        if ("1".equals(align)) { // right
                            cell.setCellStyle(csr);
                        } else if ("2".equals(align)) { // center
                            cell.setCellStyle(csl);
                        } else {
                            cell.setCellStyle(csc);
                        }

                		cell.setCellValue(value.getText());
                    }
                }
                if(rows.size()>50) sheet.createFreezePane( 0, rown + 1 );
            } finally {
            	if (fileName != null && wb!=null){
                    FileOutputStream fileOut = null;
                try {
                    fileOut = new FileOutputStream(fileName);
                    wb.write(fileOut);
                    fileOut.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            	}
            }
        }
    }
    public static void main(String arg[]) {
        System.out.println("Inside Outlook Events");
        ComThread.InitSTA();
        ActiveXComponent word = ActiveXComponent.createNewInstance("Word.Application");
        try {
            word.invoke("MsgBox", new Variant("dddd"));
            //Object exp = word.getObject();
            //WordEvents wes = new WordEvents();
            //DispatchEvents oDispatchEvents = new DispatchEvents((Dispatch) exp,
            //        wes);
            System.out.println("version=" + word.getProperty("Version"));
            word.setProperty("Visible", new Variant(true));
            ComThread.Release();

            //while (true) {
            //    try {
            //       Thread.sleep(2000);
            //    } catch (Exception e) {
            //    }
            //}
        } finally {
            word.invoke("Quit", new Variant(false));
            ComThread.Release();
        }
    }
}