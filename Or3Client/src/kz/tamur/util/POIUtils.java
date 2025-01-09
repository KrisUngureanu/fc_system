package kz.tamur.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFChart;
import org.apache.poi.hssf.usermodel.HSSFChart.HSSFSeries;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFPicture;
import org.apache.poi.hssf.usermodel.HSSFPictureData;
import org.apache.poi.hssf.usermodel.HSSFShape;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFSimpleShape;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Header;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressBase;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFName;
import org.apache.poi.xssf.usermodel.XSSFPicture;
import org.apache.poi.xssf.usermodel.XSSFPictureData;
import org.apache.poi.xssf.usermodel.XSSFShape;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTMarker;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTTwoCellAnchor;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Utilities;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPRow;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

import kz.tamur.comps.Constants;

public class POIUtils {
	
	private static ThreadLocalDateFormat dfShort = ThreadLocalDateFormat.get("dd.MM.yy");
	private static ThreadLocalDateFormat dfNormal = ThreadLocalDateFormat.get("dd.MM.yyyy");
	private static final Map<String, BaseFont> _fontMap = new HashMap<>();
	
    public static File mergeXls(List<Object> files) {
        try {
        	if (files.size() > 1) {
        		String fn = null;
                InputStream is = null;
        		if (files.get(0) instanceof File) {
            		fn = ((File)files.get(0)).getName();
                    is = new FileInputStream((File)files.get(0));
        		} else {
        			fn = (String)files.get(0);
                    is = new FileInputStream(fn);
        		}
        		String ext = (fn.indexOf(".") > -1) ? fn.substring(fn.lastIndexOf(".")) : ".tmp";
            	File res = Funcs.createTempFile("merge", ext, Constants.DOCS_DIRECTORY);
                Workbook wb = WorkbookFactory.create(is);
                is.close();
                
                List<String> names = new ArrayList<String>();
                int wshsCount = wb.getNumberOfSheets();
                for (int i=0; i<wshsCount; i++)
                	names.add(wb.getSheetName(i));

                for (int fNum = 1; fNum<files.size(); fNum++) {
                    InputStream is2 = (files.get(fNum) instanceof File) ? new FileInputStream((File)files.get(fNum)) : new FileInputStream((String)files.get(fNum));
                    Workbook wb2 = WorkbookFactory.create(is2);
                    is2.close();

                    int wshsCount2 = wb2.getNumberOfSheets();
                    
                    for (int i=0; i<wshsCount2; i++) {
                    	Sheet sh = wb2.getSheetAt(i);
                    	String name = sh.getSheetName();
                    	int k = 1;
                    	while (names.contains(name)) {
                    		name = name + (k++);
                    	}
                    	names.add(name);
                    	Sheet sh2 = wb.cloneSheet(wshsCount - 1);
                    	wb.setSheetName(wb.getNumberOfSheets() - 1, name);
                    	
                        for (int m = sh2.getNumMergedRegions() - 1; m >= 0; m--) {  
                        	sh2.removeMergedRegion(m);
                        }
                        int maxColumnNum = 0;  
                        for (int m = sh2.getLastRowNum(); m >= 0; m--) {
                        	Row row = sh2.getRow(m);
                        	if (row != null) {
                                if (row.getLastCellNum() > maxColumnNum)  
                                    maxColumnNum = row.getLastCellNum();  
                            	sh2.removeRow(row);
                        	}
                        }
                        for (int m = sh2.getLastRowNum(); m >= 0; m--) {
                        	Row row = sh2.getRow(m);
                        	if (row != null)
                            	sh2.removeRow(row);
                        }
                        int[] breaks = sh2.getRowBreaks();
                        for (int m = 0; m < breaks.length; m++) {  
                        	sh2.removeRowBreak(breaks[m]);
                        }
                    	
                    	copySheets(sh2, sh, true, maxColumnNum);
                    }
                }
                FileOutputStream fileOut = new FileOutputStream(res);
                wb.write(fileOut);
                fileOut.close();
                return res;
        	}
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private static void copySheets(Sheet newSheet, Sheet sheet, boolean copyStyle, int oldMaxColumnNum) {  
        int maxColumnNum = 0;  
        List<CellStyle> styleMap = (copyStyle)  
                ? new ArrayList<CellStyle>() : null;  
  
        for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {  
            Row srcRow = sheet.getRow(i);  
            if (srcRow != null) {  
                if (srcRow.getLastCellNum() > maxColumnNum) {  
                    maxColumnNum = srcRow.getLastCellNum();  
                }  
            }  
        }  
        for (int i = 0; i < maxColumnNum; i++) {  
            newSheet.setColumnWidth(i, sheet.getColumnWidth(i));  
            newSheet.setColumnHidden(i, sheet.isColumnHidden(i));
        }  
        for (int i = maxColumnNum; i < oldMaxColumnNum; i++) {  
            newSheet.setColumnHidden(i, true);
        }  
        
        Set<CellRangeAddress> mergedRegions = new HashSet<CellRangeAddress>();  
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {  
            Row srcRow = sheet.getRow(i);  
            Row destRow = newSheet.createRow(i);
            if (srcRow == null)
            	srcRow = sheet.createRow(i);
            
            copyRow(sheet, newSheet, srcRow, destRow, maxColumnNum, styleMap);

            for (int j = 0; j <= maxColumnNum; j++) {
                CellRangeAddress mergedRegion = getMergedRegion(sheet, i, j);  
                if (mergedRegion != null) {  
                	CellRangeAddress newMergedRegion = new CellRangeAddress(mergedRegion.getFirstRow(), mergedRegion.getLastRow(), 
                			mergedRegion.getFirstColumn(), mergedRegion.getLastColumn());  
                    if (isNewMergedRegion(newMergedRegion, mergedRegions)) {  
                        mergedRegions.add(newMergedRegion);  
                        newSheet.addMergedRegion(newMergedRegion);  
                    }  
                }  
	        }  
        }
        
        copySheetSettings(newSheet, sheet);
        copyPictures(newSheet, sheet);
    } 
    
    private static void copyRow(Sheet srcSheet, Sheet destSheet, Row srcRow, Row destRow, int max, List<CellStyle> styleMap) {  
        destRow.setHeight(srcRow.getHeight());
        destRow.setZeroHeight(srcRow.getZeroHeight());
        for (int j = 0; j < max; j++) {  
            Cell oldCell = srcRow.getCell(j);  
            Cell newCell = destRow.getCell(j);  
            if (oldCell == null)
            	oldCell = srcRow.createCell(j);
            if (newCell == null) {  
                newCell = destRow.createCell(j);  
            }  
            copyCell(oldCell, newCell, styleMap);  
        }  
          
    }
    
    private static void copySheetSettings(Sheet newSheet, Sheet sheetToCopy) {

		newSheet.setAutobreaks(sheetToCopy.getAutobreaks());
		newSheet.setDefaultColumnWidth(sheetToCopy.getDefaultColumnWidth());
		newSheet.setDefaultRowHeight(sheetToCopy.getDefaultRowHeight());
		newSheet.setDefaultRowHeightInPoints(sheetToCopy
				.getDefaultRowHeightInPoints());
		newSheet.setDisplayGuts(sheetToCopy.getDisplayGuts());
		newSheet.setFitToPage(sheetToCopy.getFitToPage());

		newSheet.setForceFormulaRecalculation(sheetToCopy
				.getForceFormulaRecalculation());

		PrintSetup sheetToCopyPrintSetup = sheetToCopy.getPrintSetup();
		PrintSetup newSheetPrintSetup = newSheet.getPrintSetup();

		newSheetPrintSetup.setPaperSize(sheetToCopyPrintSetup.getPaperSize());
		newSheetPrintSetup.setScale(sheetToCopyPrintSetup.getScale());
		newSheetPrintSetup.setPageStart(sheetToCopyPrintSetup.getPageStart());
		newSheetPrintSetup.setFitWidth(sheetToCopyPrintSetup.getFitWidth());
		newSheetPrintSetup.setFitHeight(sheetToCopyPrintSetup.getFitHeight());
		newSheetPrintSetup.setLeftToRight(sheetToCopyPrintSetup
				.getLeftToRight());
		newSheetPrintSetup.setLandscape(sheetToCopyPrintSetup.getLandscape());
		newSheetPrintSetup.setValidSettings(sheetToCopyPrintSetup
				.getValidSettings());
		newSheetPrintSetup.setNoColor(sheetToCopyPrintSetup.getNoColor());
		newSheetPrintSetup.setDraft(sheetToCopyPrintSetup.getDraft());
		newSheetPrintSetup.setNotes(sheetToCopyPrintSetup.getNotes());
		newSheetPrintSetup.setNoOrientation(sheetToCopyPrintSetup
				.getNoOrientation());
		newSheetPrintSetup.setUsePage(sheetToCopyPrintSetup.getUsePage());
		newSheetPrintSetup.setHResolution(sheetToCopyPrintSetup
				.getHResolution());
		newSheetPrintSetup.setVResolution(sheetToCopyPrintSetup
				.getVResolution());
		newSheetPrintSetup.setHeaderMargin(sheetToCopyPrintSetup
				.getHeaderMargin());
		newSheetPrintSetup.setFooterMargin(sheetToCopyPrintSetup
				.getFooterMargin());
		newSheetPrintSetup.setCopies(sheetToCopyPrintSetup.getCopies());

		Header sheetToCopyHeader = sheetToCopy.getHeader();
		Header newSheetHeader = newSheet.getHeader();
		newSheetHeader.setCenter(sheetToCopyHeader.getCenter());
		newSheetHeader.setLeft(sheetToCopyHeader.getLeft());
		newSheetHeader.setRight(sheetToCopyHeader.getRight());

		Footer sheetToCopyFooter = sheetToCopy.getFooter();
		Footer newSheetFooter = newSheet.getFooter();
		newSheetFooter.setCenter(sheetToCopyFooter.getCenter());
		newSheetFooter.setLeft(sheetToCopyFooter.getLeft());
		newSheetFooter.setRight(sheetToCopyFooter.getRight());

		newSheet.setHorizontallyCenter(sheetToCopy.getHorizontallyCenter());
		newSheet.setMargin(Sheet.LeftMargin,
				sheetToCopy.getMargin(Sheet.LeftMargin));
		newSheet.setMargin(Sheet.RightMargin,
				sheetToCopy.getMargin(Sheet.RightMargin));
		newSheet.setMargin(Sheet.TopMargin,
				sheetToCopy.getMargin(Sheet.TopMargin));
		newSheet.setMargin(Sheet.BottomMargin,
				sheetToCopy.getMargin(Sheet.BottomMargin));

		newSheet.setPrintGridlines(sheetToCopy.isPrintGridlines());
		newSheet.setRowSumsBelow(sheetToCopy.getRowSumsBelow());
		newSheet.setRowSumsRight(sheetToCopy.getRowSumsRight());
		newSheet.setVerticallyCenter(sheetToCopy.getVerticallyCenter());
		newSheet.setDisplayFormulas(sheetToCopy.isDisplayFormulas());
		newSheet.setDisplayGridlines(sheetToCopy.isDisplayGridlines());
		newSheet.setDisplayRowColHeadings(sheetToCopy.isDisplayRowColHeadings());
		newSheet.setDisplayZeros(sheetToCopy.isDisplayZeros());
		newSheet.setPrintGridlines(sheetToCopy.isPrintGridlines());
		newSheet.setRightToLeft(sheetToCopy.isRightToLeft());
		newSheet.setZoom(1, 1);
		copyPrintTitle(newSheet, sheetToCopy);
	}
    
    private static void copyPrintTitle(Sheet newSheet, Sheet sheetToCopy) {
		int nbNames = sheetToCopy.getWorkbook().getNumberOfNames();
		Name name = null;
		String formula = null;

		String part1S = null;
		String part2S = null;
		String formS = null;
		String formF = null;
		String part1F = null;
		String part2F = null;
		int rowB = -1;
		int rowE = -1;
		int colB = -1;
		int colE = -1;

		for (int i = 0; i < nbNames; i++) {
			name = sheetToCopy.getWorkbook().getNameAt(i);
			if (name.getSheetIndex() == sheetToCopy.getWorkbook()
					.getSheetIndex(sheetToCopy)) {
				if (name.getNameName().equals("Print_Titles")
						|| name.getNameName().equals(
								XSSFName.BUILTIN_PRINT_TITLE)) {
					formula = name.getRefersToFormula();
					int indexComma = formula.indexOf(",");
					if (indexComma == -1) {
						indexComma = formula.indexOf(";");
					}
					String firstPart = null;
					;
					String secondPart = null;
					if (indexComma == -1) {
						firstPart = formula;
					} else {
						firstPart = formula.substring(0, indexComma);
						secondPart = formula.substring(indexComma + 1);
					}

					formF = firstPart.substring(firstPart.indexOf("!") + 1);
					part1F = formF.substring(0, formF.indexOf(":"));
					part2F = formF.substring(formF.indexOf(":") + 1);

					if (secondPart != null) {
						formS = secondPart
								.substring(secondPart.indexOf("!") + 1);
						part1S = formS.substring(0, formS.indexOf(":"));
						part2S = formS.substring(formS.indexOf(":") + 1);
					}

					rowB = -1;
					rowE = -1;
					colB = -1;
					colE = -1;
					String rowBs, rowEs, colBs, colEs;
					if (part1F.lastIndexOf("$") != part1F.indexOf("$")) {
						rowBs = part1F.substring(part1F.lastIndexOf("$") + 1,
								part1F.length());
						rowEs = part2F.substring(part2F.lastIndexOf("$") + 1,
								part2F.length());
						rowB = Integer.parseInt(rowBs);
						rowE = Integer.parseInt(rowEs);
						if (secondPart != null) {
							colBs = part1S.substring(
									part1S.lastIndexOf("$") + 1,
									part1S.length());
							colEs = part2S.substring(
									part2S.lastIndexOf("$") + 1,
									part2S.length());
							colB = Integer.parseInt(colBs);
							colE = Integer.parseInt(colEs);
						}
					} else {
						colBs = part1F.substring(part1F.lastIndexOf("$") + 1,
								part1F.length());
						colEs = part2F.substring(part2F.lastIndexOf("$") + 1,
								part2F.length());
						colB = Integer.parseInt(colBs);
						colE = Integer.parseInt(colEs);
						if (secondPart != null) {
							rowBs = part1S.substring(
									part1S.lastIndexOf("$") + 1,
									part1S.length());
							rowEs = part2S.substring(
									part2S.lastIndexOf("$") + 1,
									part2S.length());
							rowB = Integer.parseInt(rowBs);
							rowE = Integer.parseInt(rowEs);
						}
					}

					CellRangeAddress rcra = new CellRangeAddress(rowB - 1, rowE - 1, colB, colE);
					newSheet.setRepeatingRows(rcra);
					newSheet.setRepeatingColumns(rcra);
					/*newSheet.getWorkbook().setRepeatingRowsAndColumns(
							newSheet.getWorkbook().getSheetIndex(newSheet),
							colB, colE, rowB - 1, rowE - 1);*/
				}
			}
		}
	}
	
    private static void copyCell(Cell oldCell, Cell newCell,
			List<CellStyle> styleList) {
		if (styleList != null) {
			if (oldCell.getSheet().getWorkbook() == newCell.getSheet()
					.getWorkbook()) {
				newCell.setCellStyle(oldCell.getCellStyle());
			} else {
				DataFormat newDataFormat = newCell.getSheet().getWorkbook().createDataFormat();

				CellStyle newCellStyle = getSameCellStyle(oldCell, newCell,	styleList);
				if (newCellStyle == null) {
					// Create a new cell style
					Font oldFont = oldCell.getSheet().getWorkbook()
							.getFontAt(oldCell.getCellStyle().getFontIndex());
					// Find a existing font corresponding to avoid to create a
					// new one
					Font newFont = newCell
							.getSheet()
							.getWorkbook()
							.findFont(oldFont.getBoldweight(),
									oldFont.getColor(),
									oldFont.getFontHeight(),
									oldFont.getFontName(), oldFont.getItalic(),
									oldFont.getStrikeout(),
									oldFont.getTypeOffset(),
									oldFont.getUnderline());
					if (newFont == null) {
						newFont = newCell.getSheet().getWorkbook().createFont();
						newFont.setBoldweight(oldFont.getBoldweight());
						newFont.setColor(oldFont.getColor());
						newFont.setFontHeight(oldFont.getFontHeight());
						newFont.setFontName(oldFont.getFontName());
						newFont.setItalic(oldFont.getItalic());
						newFont.setStrikeout(oldFont.getStrikeout());
						newFont.setTypeOffset(oldFont.getTypeOffset());
						newFont.setUnderline(oldFont.getUnderline());
						newFont.setCharSet(oldFont.getCharSet());
					}

					short newFormat = newDataFormat.getFormat(oldCell
							.getCellStyle().getDataFormatString());
					newCellStyle = newCell.getSheet().getWorkbook()
							.createCellStyle();
					newCellStyle.setFont(newFont);
					newCellStyle.setDataFormat(newFormat);

					newCellStyle.setAlignment(oldCell.getCellStyle()
							.getAlignment());
					newCellStyle.setHidden(oldCell.getCellStyle().getHidden());
					newCellStyle.setLocked(oldCell.getCellStyle().getLocked());
					newCellStyle.setWrapText(oldCell.getCellStyle()
							.getWrapText());
					newCellStyle.setBorderBottom(oldCell.getCellStyle()
							.getBorderBottom());
					newCellStyle.setBorderLeft(oldCell.getCellStyle()
							.getBorderLeft());
					newCellStyle.setBorderRight(oldCell.getCellStyle()
							.getBorderRight());
					newCellStyle.setBorderTop(oldCell.getCellStyle()
							.getBorderTop());
					newCellStyle.setBottomBorderColor(oldCell.getCellStyle()
							.getBottomBorderColor());
					newCellStyle.setFillBackgroundColor(oldCell.getCellStyle()
							.getFillBackgroundColor());
					newCellStyle.setFillForegroundColor(oldCell.getCellStyle()
							.getFillForegroundColor());
					newCellStyle.setFillPattern(oldCell.getCellStyle()
							.getFillPattern());
					newCellStyle.setIndention(oldCell.getCellStyle()
							.getIndention());
					newCellStyle.setLeftBorderColor(oldCell.getCellStyle()
							.getLeftBorderColor());
					newCellStyle.setRightBorderColor(oldCell.getCellStyle()
							.getRightBorderColor());
					newCellStyle.setRotation(oldCell.getCellStyle()
							.getRotation());
					newCellStyle.setTopBorderColor(oldCell.getCellStyle()
							.getTopBorderColor());
					newCellStyle.setVerticalAlignment(oldCell.getCellStyle()
							.getVerticalAlignment());

					styleList.add(newCellStyle);
				}
				newCell.setCellStyle(newCellStyle);
			}
		}
		switch (oldCell.getCellType()) {
		case Cell.CELL_TYPE_STRING:
			newCell.setCellValue(oldCell.getStringCellValue());
			break;
		case Cell.CELL_TYPE_NUMERIC:
			newCell.setCellValue(oldCell.getNumericCellValue());
			break;
		case Cell.CELL_TYPE_BLANK:
			newCell.setCellType(Cell.CELL_TYPE_BLANK);
			break;
		case Cell.CELL_TYPE_BOOLEAN:
			newCell.setCellValue(oldCell.getBooleanCellValue());
			break;
		case Cell.CELL_TYPE_ERROR:
			newCell.setCellErrorValue(oldCell.getErrorCellValue());
			break;
		case Cell.CELL_TYPE_FORMULA:
			newCell.setCellFormula(oldCell.getCellFormula());
			break;
		default:
			break;
		}
	}
    
    private static CellStyle getSameCellStyle(Cell oldCell, Cell newCell,
			List<CellStyle> styleList) {
		CellStyle styleToFind = oldCell.getCellStyle();
		CellStyle currentCellStyle = null;
		CellStyle returnCellStyle = null;
		Iterator<CellStyle> iterator = styleList.iterator();
		Font oldFont = null;
		Font newFont = null;
		while (iterator.hasNext() && returnCellStyle == null) {
			currentCellStyle = iterator.next();

			if (currentCellStyle.getAlignment() != styleToFind.getAlignment()) {
				continue;
			}
			if (currentCellStyle.getHidden() != styleToFind.getHidden()) {
				continue;
			}
			if (currentCellStyle.getLocked() != styleToFind.getLocked()) {
				continue;
			}
			if (currentCellStyle.getWrapText() != styleToFind.getWrapText()) {
				continue;
			}
			if (currentCellStyle.getBorderBottom() != styleToFind
					.getBorderBottom()) {
				continue;
			}
			if (currentCellStyle.getBorderLeft() != styleToFind.getBorderLeft()) {
				continue;
			}
			if (currentCellStyle.getBorderRight() != styleToFind
					.getBorderRight()) {
				continue;
			}
			if (currentCellStyle.getBorderTop() != styleToFind.getBorderTop()) {
				continue;
			}
			if (currentCellStyle.getBottomBorderColor() != styleToFind
					.getBottomBorderColor()) {
				continue;
			}
			if (currentCellStyle.getFillBackgroundColor() != styleToFind.getFillBackgroundColor()) {
				continue;
			}
			if (currentCellStyle.getFillForegroundColor() != styleToFind
					.getFillForegroundColor()) {
				continue;
			}
			if (currentCellStyle.getFillPattern() != styleToFind
					.getFillPattern()) {
				continue;
			}
			if (currentCellStyle.getIndention() != styleToFind.getIndention()) {
				continue;
			}
			if (currentCellStyle.getLeftBorderColor() != styleToFind
					.getLeftBorderColor()) {
				continue;
			}
			if (currentCellStyle.getRightBorderColor() != styleToFind
					.getRightBorderColor()) {
				continue;
			}
			if (currentCellStyle.getRotation() != styleToFind.getRotation()) {
				continue;
			}
			if (currentCellStyle.getTopBorderColor() != styleToFind
					.getTopBorderColor()) {
				continue;
			}
			if (currentCellStyle.getVerticalAlignment() != styleToFind
					.getVerticalAlignment()) {
				continue;
			}

			oldFont = oldCell.getSheet().getWorkbook()
					.getFontAt(oldCell.getCellStyle().getFontIndex());
			newFont = newCell.getSheet().getWorkbook()
					.getFontAt(currentCellStyle.getFontIndex());

			if (newFont.getBoldweight() != oldFont.getBoldweight()) {
				continue;
			}
			if (newFont.getColor() != oldFont.getColor()) {
				continue;
			}
			if (newFont.getFontHeight() != oldFont.getFontHeight()) {
				continue;
			}
			if (!newFont.getFontName().equals(oldFont.getFontName())) {
				continue;
			}
			if (newFont.getItalic() != oldFont.getItalic()) {
				continue;
			}
			if (newFont.getStrikeout() != oldFont.getStrikeout()) {
				continue;
			}
			if (newFont.getTypeOffset() != oldFont.getTypeOffset()) {
				continue;
			}
			if (newFont.getUnderline() != oldFont.getUnderline()) {
				continue;
			}
			if (newFont.getCharSet() != oldFont.getCharSet()) {
				continue;
			}
			if (!oldCell.getCellStyle().getDataFormatString()
					.equals(currentCellStyle.getDataFormatString())) {
				continue;
			}

			returnCellStyle = currentCellStyle;
		}
		return returnCellStyle;
	}
	
	private static void copyPictures(Sheet newSheet, Sheet sheet) {
		Drawing drawingOld = sheet.createDrawingPatriarch();
		Drawing drawingNew = newSheet.createDrawingPatriarch();
		CreationHelper helper = newSheet.getWorkbook().getCreationHelper();

		if (drawingNew instanceof HSSFPatriarch) {
			List<HSSFShape> shapes = ((HSSFPatriarch) drawingNew).getChildren();
			for (int i = shapes.size() - 1; i>=0; i--) {
				((HSSFPatriarch) drawingNew).removeShape(shapes.get(i));
			}
			
			shapes = ((HSSFPatriarch) drawingOld).getChildren();
			for (int i = 0; i < shapes.size(); i++) {
				if (shapes.get(i) instanceof HSSFPicture) {
					HSSFPicture pic = (HSSFPicture) shapes.get(i);
					HSSFPictureData picdata = pic.getPictureData();
					int pictureIndex = newSheet.getWorkbook().addPicture(
							picdata.getData(), picdata.getFormat());
					ClientAnchor anchor = null;
					if (pic.getAnchor() != null) {
						anchor = helper.createClientAnchor();
						anchor.setDx1(((HSSFClientAnchor) pic.getAnchor())
								.getDx1());
						anchor.setDx2(((HSSFClientAnchor) pic.getAnchor())
								.getDx2());
						anchor.setDy1(((HSSFClientAnchor) pic.getAnchor())
								.getDy1());
						anchor.setDy2(((HSSFClientAnchor) pic.getAnchor())
								.getDy2());
						anchor.setCol1(((HSSFClientAnchor) pic.getAnchor())
								.getCol1());
						anchor.setCol2(((HSSFClientAnchor) pic.getAnchor())
								.getCol2());
						anchor.setRow1(((HSSFClientAnchor) pic.getAnchor())
								.getRow1());
						anchor.setRow2(((HSSFClientAnchor) pic.getAnchor())
								.getRow2());
						anchor.setAnchorType(((HSSFClientAnchor) pic
								.getAnchor()).getAnchorType());
					}
					drawingNew.createPicture(anchor, pictureIndex);
				}
			}
		} else {
			if (drawingNew instanceof XSSFDrawing) {
				List<XSSFShape> shapes = ((XSSFDrawing) drawingNew).getShapes();
				shapes.clear();

				shapes = ((XSSFDrawing) drawingOld).getShapes();
				for (int i = 0; i < shapes.size(); i++) {
					if (shapes.get(i) instanceof XSSFPicture) {
						XSSFPicture pic = (XSSFPicture) shapes.get(i);
						XSSFPictureData picdata = pic.getPictureData();
						int pictureIndex = newSheet.getWorkbook().addPicture(
								picdata.getData(), picdata.getPictureType());
						XSSFClientAnchor anchor = null;
						CTTwoCellAnchor oldAnchor = ((XSSFDrawing) drawingOld)
								.getCTDrawing().getTwoCellAnchorArray(i);
						if (oldAnchor != null) {
							anchor = (XSSFClientAnchor) helper
									.createClientAnchor();
							CTMarker markerFrom = oldAnchor.getFrom();
							CTMarker markerTo = oldAnchor.getTo();
							anchor.setDx1((int) markerFrom.getColOff());
							anchor.setDx2((int) markerTo.getColOff());
							anchor.setDy1((int) markerFrom.getRowOff());
							anchor.setDy2((int) markerTo.getRowOff());
							anchor.setCol1(markerFrom.getCol());
							anchor.setCol2(markerTo.getCol());
							anchor.setRow1(markerFrom.getRow());
							anchor.setRow2(markerTo.getRow());
						}
						drawingNew.createPicture(anchor, pictureIndex);
					}
				}
			}
		}
	}
    
	private static CellRangeAddress getMergedRegion(Sheet sheet, int rowNum, int cellNum) {  
        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {  
            CellRangeAddress merged = sheet.getMergedRegion(i);  
            if (merged.isInRange(rowNum, cellNum)) {  
                return merged;  
            }  
        }  
        return null;  
    }  
  
	private static PdfCellCoords getCell(com.itextpdf.text.pdf.PdfPTable table, int rowNum, int cellNum) {  
		PdfPCell cell = table.getRow(rowNum).getCells()[cellNum];
		if (cell == null) {
	        for (int i = 0; i < table.getRows().size(); i++) {
	        	PdfPRow row = table.getRow(i);
	        	for (int j = 0; j<row.getCells().length; j++) {
	        		PdfPCell c = row.getCells()[j];
	        		if (c != null) {
	        			int rowspan = c.getRowspan();
	        			int colspan = c.getColspan();
	        			
	        			if (rowNum >= i && rowNum <= i+rowspan-1 && cellNum >= j && cellNum <= j+colspan-1) {
	        				return new PdfCellCoords(c, i, j);
	        			}
	        		}
	        	}
	        }
		}
        return new PdfCellCoords(cell, rowNum, cellNum);  
    }  

	private static PdfCellPosition getCellPosition(com.itextpdf.text.pdf.PdfPTable table, int rowNum, int cellNum, 
			Sheet sh, ClientAnchor anchor, float[] widths, float tableW, float printKoeff) {
		float left = 0.0f;
		// Сначала добавляем ширину всех ячеек до col1
		for (int k = 0; k < cellNum; k++) {
			left += widths[k];
		}
		// Затем добавляем смещение внутри ячейки col1
		int colWidthExcel = columnWidthToEMU(sh.getColumnWidth(cellNum));
		left += anchor.getDx1() * widths[cellNum] / colWidthExcel;
		left *= tableW;

		float top = 0.0f;
		// Сначала добавляем высоту всех ячеек до row1
		for (int k = 0; k < rowNum; k++) {
			top += table.getRowHeight(k);
		}
		// Затем добавляем смещение внутри ячейки row1
		float colHeightExcel = sh.getRow(rowNum).getHeightInPoints() * XSSFShape.EMU_PER_POINT;
		top += anchor.getDy1() * table.getRowHeight(rowNum) / colHeightExcel;
		//top *= printKoeff;
		
        return new PdfCellPosition(left, top);  
    }  

	private static CellRangeAddress getPicRegion(Sheet sheet, int rowNum, int cellNum) {  
		Drawing drawing = sheet.createDrawingPatriarch();
		if (drawing instanceof HSSFPatriarch) {
			List<HSSFShape> shapes = ((HSSFPatriarch) drawing).getChildren();
			for (int i = 0; i < shapes.size(); i++) {
				if (shapes.get(i) instanceof HSSFPicture) {
					HSSFPicture pic = (HSSFPicture) shapes.get(i);
					if (pic.getAnchor() != null) {
				        int row1 = ((ClientAnchor) pic.getAnchor()).getRow1();
						int col1 = ((ClientAnchor) pic.getAnchor()).getCol1();

						int row2 = ((ClientAnchor) pic.getAnchor()).getRow2();
						int col2 = ((ClientAnchor) pic.getAnchor()).getCol2();

						if (row1 <= rowNum && row2 >= rowNum && col1 <= cellNum && col2 >= cellNum && (row2 > row1 || col2 > col1))
							return new CellRangeAddress(row1, row2, col1, col2);
					}
				}
			}
		}
        return null;  
    }  

	private static boolean isNewMergedRegion(CellRangeAddress newMergedRegion,  
            Collection<CellRangeAddress> mergedRegions) {  
         
        boolean isNew = true;  
  
        // we want to check if newMergedRegion is contained inside our collection  
        for (CellRangeAddress add : mergedRegions) {  
  
            boolean r1 = (add.getFirstRow() == newMergedRegion.getFirstRow());  
            boolean r2 = (add.getLastRow() == newMergedRegion.getLastRow());  
            boolean c1 = (add.getFirstColumn() == newMergedRegion.getFirstColumn());  
            boolean c2 = (add.getLastColumn() == newMergedRegion.getLastColumn());  
            if (r1 && r2 && c1 && c2) {  
            	isNew = false;  
            }  
        }  
  
        return isNew;  
    }
	
	static float tW, tH;
	
	static float tW1 = PageSize.A4.getWidth();
	static float tH1 = PageSize.A4.getHeight();

	static float tW2 = PageSize.A4.rotate().getWidth();
	static float tH2 = PageSize.A4.rotate().getHeight();
	
	static float tW1_A3 = PageSize.A3.getWidth();
	static float tH1_A3 = PageSize.A3.getHeight();

	static float tW2_A3 = PageSize.A3.rotate().getWidth();
	static float tH2_A3 = PageSize.A3.rotate().getHeight();

	static float DEFAULT_VERTICAL_MARGIN = 39;
	static float indentKoeff = 8;
	
	static float SHEET_SIZE_A3 = org.apache.poi.ss.usermodel.PrintSetup.A3_PAPERSIZE;

	public static void main(String[] args) throws Exception {
		List<Object> files = new ArrayList<Object>();
		files.add("D:/tmp/RN/convert/График отпусков(каз).xlsx");
		files.add("D:/tmp/RN/convert/КЖ1 (1).xls");
		files.add("D:/tmp/RN/convert/Вся техника.xlsx");
		
		File agg = convertToPDF(files);

		byte[] agg1 = addPageNumbersToPDF(agg);
		
		Funcs.copy(agg, new File("D:/tmp/RN/convert/agg1.pdf"));
		Funcs.write(agg1, new File("D:/tmp/RN/convert/agg1-1.pdf"));

		files.add("D:/tmp/RN/convert/xxx1.xlsx");
		files.add("D:/tmp/RN/convert/Отпуска Сабина_01.06.2021.xls");
		files.add("D:/tmp/RN/convert/Вся техника.xlsx");
		
		agg = convertToPDF(files);
		agg1 = addPageNumbersToPDF(agg);

		Funcs.copy(agg, new File("D:/tmp/RN/convert/agg2.pdf"));
		Funcs.write(agg1, new File("D:/tmp/RN/convert/agg2-1.pdf"));
		
		BufferedImage img = ImageUtil.loadImageFromPDF("C:\\Users\\User\\Downloads\\подложка.pdf");

		if (img == null) {
			byte[] b = combinePdf("C:\\Users\\User\\Downloads\\титулка.pdf", "C:\\Users\\User\\Downloads\\подложка.pdf");
		    	
	    	FileOutputStream fos = new FileOutputStream("C:\\Users\\User\\Downloads\\final1.pdf");
	    	fos.write(b);
	    	fos.close();
	    	
	    	List<String> fs = new ArrayList<>();
	    	fs.add("C:\\Users\\User\\Downloads\\титулка.pdf");
	    	fs.add("C:\\Users\\User\\Downloads\\титулка.pdf");
	    	fs.add("C:\\Users\\User\\Downloads\\титулка.pdf");
	    	fs.add("C:\\Users\\User\\Downloads\\титулка.pdf");
	    	fs.add("C:\\Users\\User\\Downloads\\подложка.pdf");
	    	
			b = mergePdf(fs);
	    	
	    	fos = new FileOutputStream("C:\\Users\\User\\Downloads\\final2.pdf");
	    	fos.write(b);
	    	fos.close();

			b = combinePdf(b, "C:\\Users\\User\\Downloads\\подложка.pdf", 0, 2);
	    	fos = new FileOutputStream("C:\\Users\\User\\Downloads\\final3.pdf");
	    	fos.write(b);
	    	fos.close();

		} else {
			FileOutputStream fos = new FileOutputStream("C:\\Users\\User\\Downloads\\подложка.png");
			ImageUtil.writeImage(img, fos);
			fos.close();
			
			PdfReader reader = new PdfReader(new FileInputStream("C:\\Users\\User\\Downloads\\титулка.pdf"));
			Rectangle rect = reader.getPageSize(1);
			
			OutputStream os = new ByteArrayOutputStream();
			
			com.itextpdf.text.Document pdfDoc = new com.itextpdf.text.Document(rect, 0, 0, 0, 0); 
			
			PdfWriter writer = com.itextpdf.text.pdf.PdfWriter.getInstance(pdfDoc, os);
	    	pdfDoc.open();
			
			int n = reader.getNumberOfPages();
			PdfContentByte canvas = writer.getDirectContentUnder();
			
	        for (int i = 0; i < n;) {
	        	pdfDoc.newPage();
	        	
	        	PdfImportedPage page = writer.getImportedPage(reader, ++i);
	            canvas.addTemplate(page, 1f, 0, 0, 1, 0, 0);

	            Image image = Image.getInstance(ImageUtil.getImageData(img));
				//float iHeight = image.getHeight() * printKoeff * 0.75f;
				//float iWidth = image.getWidth() * printKoeff * 0.75f;
				
				image.scaleAbsolute(rect.getWidth(), rect.getHeight());
				PdfContentByte canvasUnder = writer.getDirectContentUnder();
		        image.setAbsolutePosition(0, 0);
		        canvasUnder.addImage(image);
	        }
			
	        pdfDoc.close();
	    	os.close();
	    	
	    	byte[] b = ((ByteArrayOutputStream)os).toByteArray();
	    	fos = new FileOutputStream("C:\\Users\\User\\Downloads\\final1.pdf");
	    	fos.write(b);
	    	fos.close();
			
			
		}
    }
    
    public static File convertToPDF(List<Object> files) throws Exception {
    	File res = Funcs.createTempFile("report", ".pdf", Constants.DOCS_DIRECTORY);
    	com.itextpdf.text.Document pdfDoc = new com.itextpdf.text.Document(); 
		PdfWriter writer = PdfWriter.getInstance(pdfDoc, new FileOutputStream(res));
		pdfDoc.open();

		Map<String, BaseFont> fontMap = initFonts();

    	for (Object file : files) {
    		FileInputStream is = (file instanceof File) ? new FileInputStream((File)file) : new FileInputStream((String)file);
    		Workbook wb = WorkbookFactory.create(is);
    		is.close(); // close xls

    		for (int shNum = 0; shNum<wb.getNumberOfSheets(); shNum++) {
    			if (!wb.isSheetHidden(shNum)) {
    				Sheet sh = wb.getSheetAt(shNum);

			    	float mt = Utilities.inchesToPoints((float)(sh.getMargin(Sheet.TopMargin) + sh.getPrintSetup().getHeaderMargin()))
			    			- DEFAULT_VERTICAL_MARGIN;
			    	float mb = Utilities.inchesToPoints((float)(sh.getMargin(Sheet.BottomMargin) + sh.getPrintSetup().getFooterMargin()))
			    			- DEFAULT_VERTICAL_MARGIN;
			    	float ml = Utilities.inchesToPoints((float)sh.getMargin(Sheet.LeftMargin));
			    	float mr = Utilities.inchesToPoints((float)sh.getMargin(Sheet.RightMargin));
			    	
					float printKoeff = 1.0f * sh.getPrintSetup().getScale() / 100;
					
					if (sh.getPrintSetup().getLandscape()) {
						if (SHEET_SIZE_A3 == sh.getPrintSetup().getPaperSize()) {
							pdfDoc.setPageSize(PageSize.A3.rotate());
							tW = tW2_A3 - (ml + mr);
							tH = tH2_A3;
						} else {
							pdfDoc.setPageSize(PageSize.A4.rotate());
							tW = tW2 - (ml + mr);
							tH = tH2;
						}
						pdfDoc.setMargins(ml, mr, mt, mb);
					} else {
						if (SHEET_SIZE_A3 == sh.getPrintSetup().getPaperSize()) {
							pdfDoc.setPageSize(PageSize.A3);
							tW = tW1_A3 - (ml + mr);
							tH = tH1_A3;
						} else {
							pdfDoc.setPageSize(PageSize.A4);
							tW = tW1 - (ml + mr);
							tH = tH1;
						}
						pdfDoc.setMargins(ml, mr, mt, mb);
					}
					
					pdfDoc.newPage();
					formSheet(sh, pdfDoc, writer, fontMap, printKoeff, ml, mt);
    			}
    		}
    	}
		pdfDoc.close();
		
		return res;
    }
    
    private static Map<String, BaseFont> initFonts() throws IOException, DocumentException {
    	BaseFont arial = loadFont("/kz/tamur/util/fonts/arial.ttf");
    	BaseFont arialbd = loadFont("/kz/tamur/util/fonts/arialbd.ttf");
    	BaseFont arialbi = loadFont("/kz/tamur/util/fonts/arialbi.ttf");
    	BaseFont ariali = loadFont("/kz/tamur/util/fonts/ariali.ttf");
    	BaseFont times = loadFont("/kz/tamur/util/fonts/times.ttf");
    	BaseFont timesbd = loadFont("/kz/tamur/util/fonts/timesbd.ttf");
    	BaseFont timesbi = loadFont("/kz/tamur/util/fonts/timesbi.ttf");
    	BaseFont timesi = loadFont("/kz/tamur/util/fonts/timesi.ttf");
        
		Map<String, BaseFont> fontMap = new HashMap<String, BaseFont>();
		fontMap.put("Arial", arial);
		fontMap.put("Arial(BD)", arialbd);
		fontMap.put("Arial(BD)(IT)", arialbi);
		fontMap.put("Arial(IT)", ariali);
		fontMap.put("Times New Roman", times);
		fontMap.put("Times New Roman(BD)", timesbd);
		fontMap.put("Times New Roman(BD)(IT)", timesbi);
		fontMap.put("Times New Roman(IT)", timesi);
		
		return fontMap;
    }
    
    private static BaseFont loadFont(String resourcePath) throws IOException, DocumentException {
    	BaseFont font = _fontMap.get(resourcePath);
		if (font == null) {
			InputStream fis = POIUtils.class.getResourceAsStream(resourcePath);
			File fontFile = Funcs.createTempFile("font", ".ttf", Constants.DOCS_DIRECTORY);
	        FileOutputStream out = new FileOutputStream(fontFile);
	
	        Funcs.writeStream(fis, out, Constants.MAX_IMAGE_SIZE);
	        fis.close();
	        out.flush();
	        out.close();
	        
	        font = BaseFont.createFont(fontFile.getAbsolutePath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
	        _fontMap.put(resourcePath, font);
	        
			fontFile.delete();
		}        
        return font;
    }
    
    public static File convertToPDF(File file) throws Exception {
		FileInputStream is = new FileInputStream(file);
		Workbook wb = WorkbookFactory.create(is);
		is.close(); // close xls

    	File res = Funcs.createTempFile("report", ".pdf", Constants.DOCS_DIRECTORY);
    	
		com.itextpdf.text.Document pdfDoc = new com.itextpdf.text.Document();
		PdfWriter writer = PdfWriter.getInstance(pdfDoc, new FileOutputStream(res));
		pdfDoc.open();
		
		Map<String, BaseFont> fontMap = initFonts();

		for (int shNum = 0; shNum<wb.getNumberOfSheets(); shNum++) {
				if (!wb.isSheetHidden(shNum)) {
    				Sheet sh = wb.getSheetAt(shNum);

			    	float mt = Utilities.inchesToPoints((float)(sh.getMargin(Sheet.TopMargin) + sh.getPrintSetup().getHeaderMargin()))
			    			- DEFAULT_VERTICAL_MARGIN;
			    	float mb = Utilities.inchesToPoints((float)(sh.getMargin(Sheet.BottomMargin) + sh.getPrintSetup().getFooterMargin()))
			    			- DEFAULT_VERTICAL_MARGIN;
			    	float ml = Utilities.inchesToPoints((float)sh.getMargin(Sheet.LeftMargin));
			    	float mr = Utilities.inchesToPoints((float)sh.getMargin(Sheet.RightMargin));
			    	
					float printKoeff = 1.0f * sh.getPrintSetup().getScale() / 100;
					
					if (sh.getPrintSetup().getLandscape()) {
						if (SHEET_SIZE_A3 == sh.getPrintSetup().getPaperSize()) {
							pdfDoc.setPageSize(PageSize.A3.rotate());
							tW = tW2_A3 - (ml + mr);
							tH = tH2_A3;
						} else {
							pdfDoc.setPageSize(PageSize.A4.rotate());
							tW = tW2 - ml - mr;
							tH = tH2;
						}
						pdfDoc.setMargins(printKoeff * ml, printKoeff * mr, printKoeff * mt, printKoeff * mb);
					} else {
						if (SHEET_SIZE_A3 == sh.getPrintSetup().getPaperSize()) {
							pdfDoc.setPageSize(PageSize.A3);
							tW = tW1_A3 - (ml + mr);
							tH = tH1_A3;
						} else {
							pdfDoc.setPageSize(PageSize.A4);
							tW = tW1 - ml - mr;
							tH = tH1;
						}
						pdfDoc.setMargins(printKoeff * ml, printKoeff * mr, printKoeff * mt, printKoeff * mb);
					}
					
					pdfDoc.newPage();
					formSheet(sh, pdfDoc, writer, fontMap, printKoeff, ml, mt);
				}
		}
		
		pdfDoc.close();
		
		return res;
    }
    
    private static void formSheet(Sheet sh, Document pdfDoc, PdfWriter writer, Map<String, BaseFont> fontMap, float printKoeff, float ml, float mt) throws Exception {
    	float tableW = tW;
        float printKoeffW = printKoeff * 1.15f;
        if (printKoeffW > 1) printKoeffW = 1f;

		int maxColumnNum = 0;
        for (int i = sh.getFirstRowNum(); i <= sh.getLastRowNum(); i++) {  
            Row srcRow = sh.getRow(i);  
            if (srcRow != null) {  
                if (srcRow.getLastCellNum() > maxColumnNum) {  
                    maxColumnNum = srcRow.getLastCellNum();  
                }  
            }  
        }  
        
        float[] widths = new float[maxColumnNum + 1];
        float colWidths = 0f;
        int totalWidth = 0;
        for (int i = 0; i < maxColumnNum; i++) {  
        	totalWidth += (!sh.isColumnHidden(i)) ? sh.getColumnWidth(i) : 0;  
        }  
        for (int i = 0; i < maxColumnNum; i++) {  
        	widths[i] = (!sh.isColumnHidden(i)) ? (printKoeffW * sh.getColumnWidth(i))/totalWidth : 0;
        	colWidths += widths[i];
        }
        widths[maxColumnNum] = (colWidths < 1) ? (1f - colWidths) : 0f;
        
        if (maxColumnNum == 0) return;
        
        com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(maxColumnNum + 1);
        
        table.setTotalWidth(tableW);
        table.setLockedWidth(true);
        table.setWidths(widths);
        
        for (int i = 0; i <= sh.getLastRowNum(); i++) {
            Row srcRow = sh.getRow(i);  
            if (srcRow == null)
            	srcRow = sh.createRow(i);
            
            if (!srcRow.getZeroHeight()) {
            	srcRow.setHeightInPoints(srcRow.getHeightInPoints());

		        for (int j = 0; j < maxColumnNum; j++) {  
		            com.itextpdf.text.pdf.PdfPCell pdfCell = null;
	                Cell cell = srcRow.getCell(j);  
	                if (cell == null)
	                	cell = srcRow.createCell(j);
	                
	                CellRangeAddress mreg = getMergedRegion(sh, i, j);  
	                if (mreg != null) {
	                	if (mreg.getFirstRow() == i && mreg.getFirstColumn() == j) {
	                		pdfCell = new com.itextpdf.text.pdf.PdfPCell();
	                		pdfCell.setRowspan(mreg.getLastRow() - mreg.getFirstRow() + 1);
	                		pdfCell.setColspan(mreg.getLastColumn() - mreg.getFirstColumn() + 1);
	                	}
	                } else {
	            		pdfCell = new com.itextpdf.text.pdf.PdfPCell();
	                	pdfCell.setFixedHeight(srcRow.getHeightInPoints() * printKoeff);
	                }
	                if (pdfCell != null) {
	                	if (sh.isColumnHidden(j)) {
	                		pdfCell.setBorderWidth(0);
	                		pdfCell.setPadding(0);
	                	} else {
	                		short indention = cell.getCellStyle().getIndention();
	                		pdfCell.setLeading(0.4f * printKoeff, 1.15f);
	                		
	                		pdfCell.setPaddingTop(0);
		                	pdfCell.setMinimumHeight(cell.getRow().getHeightInPoints() * printKoeff);
		                	
		                	if (cell.getCellStyle().getRotation() > 0) {
		                		pdfCell.setRotation(cell.getCellStyle().getRotation());
		                		
			                	switch (cell.getCellStyle().getAlignment()) {
			                		case CellStyle.ALIGN_CENTER:
			                			pdfCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			                			if (indention > 0)
			                				pdfCell.setIndent(indention * indentKoeff * printKoeff);
			                			break;
			                		case CellStyle.ALIGN_RIGHT:
			                			pdfCell.setVerticalAlignment(Element.ALIGN_TOP);
			                			if (indention > 0)
			                				pdfCell.setRightIndent(indention * indentKoeff * printKoeff);
			                			break;
			                		case CellStyle.ALIGN_LEFT:
			                			pdfCell.setVerticalAlignment(Element.ALIGN_BOTTOM);
			                			if (indention > 0)
			                				pdfCell.setIndent(indention * indentKoeff * printKoeff);
			                			break;
			                	}
			                	
			                	switch (cell.getCellStyle().getVerticalAlignment()) {
				            		case CellStyle.VERTICAL_TOP:
				            			pdfCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				            			break;
				            		case CellStyle.VERTICAL_CENTER:
				            			pdfCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				            			break;
				            		case CellStyle.VERTICAL_BOTTOM:
				            			pdfCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				            			break;
				            	}
		                	} else {
			                	switch (cell.getCellStyle().getAlignment()) {
			                		case CellStyle.ALIGN_CENTER:
			                			pdfCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			                			if (indention > 0)
			                				pdfCell.setIndent(indention * indentKoeff * printKoeff);
			                			break;
			                		case CellStyle.ALIGN_RIGHT:
			                			pdfCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			                			if (indention > 0)
			                				pdfCell.setRightIndent(indention * indentKoeff * printKoeff);
			                			break;
			                	}
			                	
			                	switch (cell.getCellStyle().getVerticalAlignment()) {
				            		case CellStyle.VERTICAL_TOP:
				            			pdfCell.setVerticalAlignment(Element.ALIGN_TOP);
				            			break;
				            		case CellStyle.VERTICAL_CENTER:
				            			pdfCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				            			break;
				            		case CellStyle.VERTICAL_BOTTOM:
				            			pdfCell.setVerticalAlignment(Element.ALIGN_BOTTOM);
				            			break;
				            	}
		                	}		                	
		                	
		                	Font f = sh.getWorkbook().getFontAt(cell.getCellStyle().getFontIndex());
		                	com.itextpdf.text.Font pf = resolveFont(f, fontMap, printKoeff);
		                	
		                	setBorder(pdfCell, cell);

		                	pdfCell.setNoWrap(!cell.getCellStyle().getWrapText());
		                	
							switch (cell.getCellType()) { // Identify CELL type
								case Cell.CELL_TYPE_STRING:
									pdfCell.setPhrase(new com.itextpdf.text.Phrase(cell.getStringCellValue(), pf));
									break;
								case Cell.CELL_TYPE_NUMERIC:
									if (DateUtil.isCellDateFormatted(cell)) {
										String format = cell.getCellStyle().getDataFormatString();
										if (format != null && format.startsWith("dd/mm/yy;"))
											pdfCell.setPhrase(new com.itextpdf.text.Phrase(dfShort.format(cell.getDateCellValue()), pf));
										else
											pdfCell.setPhrase(new com.itextpdf.text.Phrase(dfNormal.format(cell.getDateCellValue()), pf));
									} else {
										String format = cell.getCellStyle().getDataFormatString();
								        DecimalFormat dblFmt = new DecimalFormat("#");
										if (format.contains("0"))
											dblFmt = new DecimalFormat(format.replace('#', '0'));
										
										double val = cell.getNumericCellValue();
										if (val != 0 || sh.isDisplayZeros())
											pdfCell.setPhrase(new com.itextpdf.text.Phrase(dblFmt.format(val), pf));
									}
									break;
								case Cell.CELL_TYPE_BOOLEAN:
									pdfCell.setPhrase(new com.itextpdf.text.Phrase("" + cell.getBooleanCellValue(), pf));
									break;
								case Cell.CELL_TYPE_ERROR:
									pdfCell.setPhrase(new com.itextpdf.text.Phrase("" + cell.getErrorCellValue(), pf));
									break;
								case Cell.CELL_TYPE_FORMULA:
									FormulaEvaluator evaluator = sh.getWorkbook().getCreationHelper().createFormulaEvaluator();
									try {
										evaluator.evaluateFormulaCell(cell);
									} catch (Throwable e) {
										System.out.println("Ошбика при вычислении формулы в Excel: " + cell.getCellFormula());
									}
							    	
							    	String val = "";
									
									String format = cell.getCellStyle().getDataFormatString();
									
							        DecimalFormat dblFmt = new DecimalFormat("#");
									if (format.contains("0")) {
										dblFmt = new DecimalFormat(format.replace('#', '0'));
										try {
											val = cell.getStringCellValue();
										} catch (Exception e) {
											double dval = cell.getNumericCellValue();
											if (dval != 0 || sh.isDisplayZeros())
												val = dblFmt.format(dval);
										}
									} else {
										try {
											val = cell.getStringCellValue();
										} catch (Exception e) {
											double dval = cell.getNumericCellValue();
											if (dval != 0 || sh.isDisplayZeros())
												val = dblFmt.format(dval);
										}
									}
									pdfCell.setPhrase(new com.itextpdf.text.Phrase(val, pf));
									break;
								default:
									pdfCell.setPhrase(new com.itextpdf.text.Phrase());
									break;
							}
	                	}
						table.addCell(pdfCell);
	                }
		        }
		        PdfPCell pdfCell = new com.itextpdf.text.pdf.PdfPCell();
		    	pdfCell.setBorderWidthBottom(0);
		    	pdfCell.setBorderWidthLeft(0);
		    	pdfCell.setBorderWidthRight(0);
		    	pdfCell.setBorderWidthTop(0);
        		pdfCell.setPadding(0);
		        table.addCell(pdfCell);
	        }
        }
        for (int i = 0; i < table.getRows().size() - 1; i++) {
        	PdfPRow row = table.getRow(i);
        	
            for (int j = 0; j < row.getCells().length - 1; j++) {
            	PdfPCell cell = getCellAt(table, i, j);
            	
            	if (cell != null) {
	            	if (j < row.getCells().length - 2) {
		            	PdfPCell rightCell = getCellAt(table, i, j + 1);
		            	if (!cell.equals(rightCell) && rightCell.getBorderWidthLeft() > 0)
		            		cell.setBorderWidthRight(0);
	            	}
	            	
	            	PdfPCell bottomCell = getCellAt(table, i + 1, j);
	            	if (!cell.equals(bottomCell) && bottomCell.getBorderWidthTop() > 0)
	            		cell.setBorderWidthBottom(0);
            	}
            }
        }
		
		List<JFreeChart> jcharts = new ArrayList<>(); 
		if (sh instanceof HSSFSheet) {
			HSSFChart[] charts = HSSFChart.getSheetCharts((HSSFSheet)sh);
			
			for (HSSFChart chart : charts) {
				String title = chart.getChartTitle();
				HSSFSeries[] series = chart.getSeries();

				DefaultCategoryDataset dataset = new DefaultCategoryDataset();
				
				int shIndex = sh.getWorkbook().getSheetIndex(sh);
				if (sh.getWorkbook().getNumberOfSheets() > shIndex + 1) {
					Sheet dataSh = sh.getWorkbook().getSheetAt(shIndex + 1);
					
					for (int i=0; i<series.length; i++) {
						CellRangeAddressBase catCR = series[i].getCategoryLabelsCellRange();
						CellRangeAddressBase valCR = series[i].getValuesCellRange();

						List<String> cats = new ArrayList<>();
						for (int row = catCR.getFirstRow(); row<= catCR.getLastRow(); row++) {
							for (int col = catCR.getFirstColumn(); col<= catCR.getLastColumn(); col++) {
								cats.add(dataSh.getRow(row).getCell(col).getStringCellValue());
							}
						}

						List<Number> vals = new ArrayList<>();
						for (int row = valCR.getFirstRow(); row<= valCR.getLastRow(); row++) {
							for (int col = valCR.getFirstColumn(); col<= valCR.getLastColumn(); col++) {
								vals.add(dataSh.getRow(row).getCell(col).getNumericCellValue());
							}
						}
						
						for (int j = 0; j<cats.size(); j++) {
							dataset.addValue(vals.get(j), series[i].getSeriesTitle(), cats.get(j));
						}
					}
				}
				
				Or3SpiderWebPlot plot = new Or3SpiderWebPlot(dataset);
				plot.setInteriorGap(0.20);
				plot.setLabelGenerator(new StandardCategoryItemLabelGenerator("ww {2}", NumberFormat.getInstance()));
				plot.setAxisLabelGap(0.05);
				
				plot.setSeriesPaint(0, new Color(192, 80, 77));
				plot.setSeriesOutlinePaint(0, new Color(192, 80, 77));
				plot.setSeriesOutlineStroke(0, new BasicStroke(2.5f));

				plot.setSeriesPaint(1, new Color(79, 129, 189));
				plot.setSeriesOutlinePaint(1, new Color(79, 129, 189));
				plot.setSeriesOutlineStroke(1, new BasicStroke(2.5f));
				
				plot.setTickFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 9));
				plot.setTickPaint(Color.gray);
				
				plot.setLegendItemShape(new Ellipse2D.Double(-3, -3, 6, 6));
				
				JFreeChart jchart = new JFreeChart(title, TextTitle.DEFAULT_FONT, plot, true);
				jcharts.add(jchart);
			}
		}
		
		int chartIndex = 0;

		Drawing drawing = sh.createDrawingPatriarch();
		if (drawing instanceof HSSFPatriarch) {
			List<HSSFShape> shapes = ((HSSFPatriarch) drawing).getChildren();
			
			for (int i = 0; i < shapes.size(); i++) {
				if (shapes.get(i) instanceof HSSFPicture) {
					HSSFPicture pic = (HSSFPicture) shapes.get(i);
					HSSFPictureData picdata = pic.getPictureData();
					
					if (pic.getAnchor() != null) {
						int row1 = ((ClientAnchor) pic.getAnchor()).getRow1();
						int col1 = ((ClientAnchor) pic.getAnchor()).getCol1();

						PdfCellPosition pos = getCellPosition(table, row1, col1, sh, (ClientAnchor)pic.getAnchor(), widths, tableW, printKoeff);
						
						Image image = Image.getInstance(picdata.getData());
						float iHeight = image.getHeight() * printKoeff * 0.75f;
						float iWidth = image.getWidth() * printKoeff * 0.75f;
						
						image.scaleAbsolute(iWidth, iHeight);
						PdfContentByte canvas = writer.getDirectContentUnder();
				        image.setAbsolutePosition(pos.left + ml, tH - mt - pos.top - iHeight);
				        canvas.addImage(image);
					}
				} else if (shapes.get(i) instanceof HSSFSimpleShape && jcharts.size() > 0) {
					HSSFSimpleShape shape = (HSSFSimpleShape)shapes.get(i);
					if (shape.getAnchor() != null) {
						int row1 = ((ClientAnchor) shape.getAnchor()).getRow1();
						int row2 = ((ClientAnchor) shape.getAnchor()).getRow2();
						int col1 = ((ClientAnchor) shape.getAnchor()).getCol1();
						int col2 = ((ClientAnchor) shape.getAnchor()).getCol2();

						int type = shape.getShapeType();
						
						PdfCellCoords cell = getCell(table, row1, col1);
						//table.getRow(row1).getCells()[col1];
						if (cell != null) {
							float totalW = 0.0f;
							//float totalH = 0.0f;
							for (int cs = cell.col; cs < cell.col + cell.pdfCell.getColspan(); cs++) {
								totalW += widths[cs];
							}
							//for (int cs = cell.col; cs < cell.col + cell.pdfCell.getColspan(); cs++) {
							//	totalH += widths[col1];
							//}
							
							float left = totalW*tableW * ((ClientAnchor) shape.getAnchor()).getDx1() * printKoeff / 1024;
							float top = table.getRowspanHeight(cell.row, cell.col) * printKoeff * ((ClientAnchor) shape.getAnchor()).getDy1() * printKoeff/ 256;
							
							float right = widths[col2]*tableW * (1024 - ((ClientAnchor) shape.getAnchor()).getDx2()) / 1024;
							float bottom = cell.pdfCell.getMinimumHeight() * printKoeff * (256 - ((ClientAnchor) shape.getAnchor()).getDy2()) / 256;

							float imgW = 0.0f;
							float imgH = 0.0f;
							for (int cs = col1; cs <= col2; cs++) {
								imgW += widths[cs] * tableW;
							}
							imgW = imgW - left - right;
							for (int cs = row1; cs <= row2; cs++) {
								imgH += table.getRow(cs).getMaxHeights();
							}
							imgH = imgH - top - bottom;
							
							cell.pdfCell.setPaddingLeft(left);
							cell.pdfCell.setPaddingTop(top);
							cell.pdfCell.setPaddingRight(widths[col1]*tableW - left - imgW);
							cell.pdfCell.setPaddingBottom(table.getRow(row1).getMaxHeights() - top - imgH);
							JFreeChart jchart = jcharts.get(chartIndex++);
							ByteArrayOutputStream bos = new ByteArrayOutputStream();
							ChartUtilities.writeChartAsJPEG(bos, jchart, (int)imgW, (int)imgH);
							Image image = Image.getInstance(bos.toByteArray());

							bos.close();
/*							ImageIcon img = new ImageIcon(picdata.getData());
							java.awt.Image scaled = img.getImage().getScaledInstance((int) (img.getIconWidth() * printKoeff), (int)(img.getIconHeight()*printKoeff), java.awt.Image.SCALE_AREA_AVERAGING);
							Image image = Image.getInstance(scaled, null);
*/
							cell.pdfCell.setImage(image);
						}
					}
				}
			}
		} else if (drawing instanceof XSSFDrawing) {
			List<XSSFShape> shapes = ((XSSFDrawing) drawing).getShapes();
			for (int i = 0; i < shapes.size(); i++) {
				if (shapes.get(i) instanceof XSSFPicture) {
					XSSFPicture pic = (XSSFPicture) shapes.get(i);
					XSSFPictureData picdata = pic.getPictureData();
					
					if (pic.getAnchor() != null) {
						int row1 = ((ClientAnchor) pic.getAnchor()).getRow1();
						int col1 = ((ClientAnchor) pic.getAnchor()).getCol1();

						PdfCellPosition pos = getCellPosition(table, row1, col1, sh, (ClientAnchor)pic.getAnchor(), widths, tableW, printKoeff);
						
						Image image = Image.getInstance(picdata.getData());
						float iHeight = image.getHeight() * printKoeff * 0.75f;
						float iWidth = image.getWidth() * printKoeff * 0.75f;
						
						image.scaleAbsolute(iWidth, iHeight);
						PdfContentByte canvas = writer.getDirectContentUnder();
				        image.setAbsolutePosition(pos.left + ml, tH - mt - pos.top - iHeight);
				        canvas.addImage(image);
					}
				}
			}
		}
		
		// Finally add the table to PDF document
		pdfDoc.add(table);
	}
    
    private static void setBorder(PdfPCell pdfCell, Cell cell) {
    	short borderBottom = cell.getCellStyle().getBorderBottom();
    	short borderLeft = cell.getCellStyle().getBorderLeft();
    	short borderRight = cell.getCellStyle().getBorderRight();
    	short borderTop = cell.getCellStyle().getBorderTop();

    	if (pdfCell.getRowspan() > 1 || pdfCell.getColspan() > 1) {
    		Sheet sh = cell.getSheet();
    		CellRangeAddress mreg = getMergedRegion(sh, cell.getRowIndex(), cell.getColumnIndex());
    		
    		for (int i = mreg.getFirstRow(); i <= mreg.getLastRow(); i++) {
    			Row row = sh.getRow(i);
    			
        		for (int j = mreg.getFirstColumn(); j <= mreg.getLastColumn(); j++) {
        			Cell cell2 = row.getCell(j);
        			
        			if (cell2 != null) {
        				borderBottom = (short) Math.max(borderBottom, cell2.getCellStyle().getBorderBottom());
        				borderLeft = (short) Math.max(borderLeft, cell2.getCellStyle().getBorderLeft());
        				borderRight = (short) Math.max(borderRight, cell2.getCellStyle().getBorderRight());
        				borderTop = (short) Math.max(borderTop, cell2.getCellStyle().getBorderTop());
        			}
        		}
    		}
    	}
    	pdfCell.setBorderWidthBottom(borderBottom * 0.5f);
    	pdfCell.setBorderWidthLeft(borderLeft * 0.5f);
    	pdfCell.setBorderWidthRight(borderRight * 0.5f);
    	pdfCell.setBorderWidthTop(borderTop * 0.5f);
	}

	private static PdfPCell getCellAt(PdfPTable table, int r, int c) {
    	PdfPRow row = table.getRow(r);
    	PdfPCell cell = row.getCells()[c];
    	
		int i = r;
		int j = c - 1;
    	while (cell == null) {
    		if (j > -1) {
    			PdfPCell prevCell = row.getCells()[j];
    			if (prevCell != null) {
    				if (j + prevCell.getColspan() > c && i + prevCell.getRowspan() > r)
    					cell = prevCell;
    				else {
    					row = table.getRow(--i);
    					j = c;
    				}
    			} else {
    				j--;
    			}
    		} else {
				row = table.getRow(--i);
				j = c;
    		}
    	}
    	return cell;
	}

	private static com.itextpdf.text.Font resolveFont(Font f, Map<String, BaseFont> fontMap, float printKoeff) {
    	int fh = Math.round(f.getFontHeightInPoints() * printKoeff);
    	String ff = f.getFontName();
    	
    	int fs = 0;

    	if (!fontMap.containsKey(ff))
    		ff = "Arial";
    	
    	if (f.getBoldweight() > 400) {
    		if (fontMap.containsKey(ff + "(BD)"))
    			ff += "(BD)";
    		else
	    		fs |= com.itextpdf.text.Font.BOLD;
    	}
    	
    	if (f.getItalic()) {
    		if (fontMap.containsKey(ff + "(IT)"))
    			ff += "(IT)";
    		else
        		fs |= com.itextpdf.text.Font.ITALIC;
    	}

		BaseFont bf = fontMap.get(ff);
    	
    	com.itextpdf.text.Font pf = new com.itextpdf.text.Font(bf, fh, fs);
    	return pf;
	}

	static class PdfCellCoords {
    	public PdfPCell pdfCell;
    	public int row;
    	public int col;
		public PdfCellCoords(PdfPCell pdfCell, int row, int col) {
			super();
			this.pdfCell = pdfCell;
			this.row = row;
			this.col = col;
		}
    }
	
	static class PdfCellPosition {
    	public float left;
    	public float top;
    	
		public PdfCellPosition(float left, float top) {
			super();
			this.left = left;
			this.top = top;
		}
    }

	public static final float DEFAULT_CHARACTER_WIDTH = 7.0017f;
    public static final int EMU_PER_CHARACTER = (int) (XSSFShape.EMU_PER_PIXEL * DEFAULT_CHARACTER_WIDTH);
    
    public static int columnWidthToEMU(int columnWidth) {
        return charactersToEMU(columnWidth / 256d);
    }
    
    public static int charactersToEMU(double characters) {
        return (int) characters * EMU_PER_CHARACTER;
    }
    
    public static byte[] mergePdf(List<?> files) {
    	try {
    		
    		ByteArrayOutputStream os = new ByteArrayOutputStream();
			
			com.itextpdf.text.Document pdfDoc = new com.itextpdf.text.Document(PageSize.A4, 0, 0, 0, 0); 
			
			PdfWriter writer = com.itextpdf.text.pdf.PdfWriter.getInstance(pdfDoc, os);
	    	pdfDoc.open();
	    	PdfContentByte canvas = writer.getDirectContent();

	    	for (int k = 0; k<files.size(); k++) {
	    		Object file = files.get(k);
	    		PdfReader reader = (file instanceof File) ? new PdfReader(new FileInputStream((File)file))
	    				: (file instanceof String) ? new PdfReader(new FileInputStream((String)file))
	    				: (file instanceof byte[]) ? new PdfReader((byte[])file)
	    				: null;
	    				
	    		if (reader != null) {
	    			int n = reader.getNumberOfPages();
	    			
	    	        for (int i = 1; i <= n; i++) {
	    	        	Rectangle rect = reader.getPageSize(i);
	    	        	pdfDoc.setPageSize(rect);
	    	        	pdfDoc.newPage();
	    	        	
	    	        	PdfImportedPage page = writer.getImportedPage(reader, i);
	    	            canvas.addTemplate(page, 1f, 0, 0, 1, 0, 0);
	    	        }
	    		}
	    	}
			
	        pdfDoc.close();
	    	os.close();
	    	
	    	return os.toByteArray();
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
    	return null;
    }

    public static File bytesToFile(byte[] b) {
    	return bytesToFile(b, ".pdf");
    }
    
    public static File bytesToFile(byte[] b, String ext) {
    	File res = Funcs.createTempFile("file", ext, Constants.DOCS_DIRECTORY);
    	try {
	    	FileOutputStream fos = new FileOutputStream(res);
	    	fos.write(b);
	    	fos.close();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    	return res;
    }
    
    public static byte[] combinePdf(Object file1, Object file2) {
    	return combinePdf(file1, file2, 0, -1);
    }
    
    public static byte[] combinePdf(Object file1, Object file2, Number pageStart, Number pageEnd) {
    	try {
    		
    		ByteArrayOutputStream os = new ByteArrayOutputStream();
			
			com.itextpdf.text.Document pdfDoc = new com.itextpdf.text.Document();
			
			PdfWriter writer = com.itextpdf.text.pdf.PdfWriter.getInstance(pdfDoc, os);
	    	pdfDoc.open();
	    	PdfContentByte canvas = writer.getDirectContent();

    		PdfReader reader1 = (file1 instanceof File) ? new PdfReader(new FileInputStream((File)file1))
    				: (file1 instanceof String) ? new PdfReader(new FileInputStream((String)file1))
    				: (file1 instanceof byte[]) ? new PdfReader((byte[])file1)
    				: null;
    				
    		PdfReader reader2 = (file2 instanceof File) ? new PdfReader(new FileInputStream((File)file2))
    				: (file2 instanceof String) ? new PdfReader(new FileInputStream((String)file2))
    				: (file2 instanceof byte[]) ? new PdfReader((byte[])file2)
    				: null;

			if (reader1 != null && reader2 != null) {
    			int n = reader1.getNumberOfPages();
    			
    			if (pageStart.intValue() < 0) pageStart = n + pageStart.intValue();
    			if (pageEnd.intValue() < 0) pageEnd = n + pageEnd.intValue();
    			
    	        for (int i = 1; i <= n; i++) {
    	        	Rectangle rect = reader1.getPageSizeWithRotation(i);
    	        	pdfDoc.setPageSize(rect);
    	        	
    	        	int rotation = reader1.getPageRotation(i);
					float pageWidth = rect.getWidth();
					float pageHeight = rect.getHeight();
    	        	
    	        	pdfDoc.newPage();
    	        	
    	        	PdfImportedPage page = writer.getImportedPage(reader1, i);
    	        	
    	        	if (rotation == 0) {
						canvas.addTemplate(page, 1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F);
					} else if (rotation == 90) {
						canvas.addTemplate(page, 0.0F, -1.0F, 1.0F, 0.0F, 0.0F, pageHeight);
					} else if (rotation == 180) {
						canvas.addTemplate(page, 1.0F, 0.0F, 0.0F, -1.0F, pageWidth, pageHeight);
					} else if (rotation == 270) {
						canvas.addTemplate(page, 0.0F, -1.0F, 1.0F, 0.0F, 0.0F, pageHeight);
					}
    	        	
    	            if (i > pageStart.intValue() && i <= pageEnd.intValue() + 1) {
						rotation = reader2.getPageRotation(1);
						
        	        	PdfImportedPage page2 = writer.getImportedPage(reader2, 1);
        	        	
        	        	if (rotation == 0) {
							canvas.addTemplate(page2, 1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F);
						} else if (rotation == 90) {
							canvas.addTemplate(page2, 0.0F, -1.0F, 1.0F, 0.0F, 0.0F, pageHeight);
						} else if (rotation == 180) {
							canvas.addTemplate(page2, 1.0F, 0.0F, 0.0F, -1.0F, pageWidth, pageHeight);
						} else if (rotation == 270) {
							canvas.addTemplate(page2, 0.0F, -1.0F, 1.0F, 0.0F, 0.0F, pageHeight);
						}
    	            }
    	        }
    		}
			
	        pdfDoc.close();
	    	os.close();
	    	
	    	return os.toByteArray();
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
    	return null;
    }
	
	public static byte[] addPageNumbersToPDF(Object file) {
		return addPageNumbersToPDF(file, 1, null);
	}
	
	public static byte[] addPageNumbersToPDF(Object file, int firstPage) {
		return addPageNumbersToPDF(file, firstPage, null);
	}
	
	public static byte[] addPageNumbersToPDF(Object file, String addText) {
		return addPageNumbersToPDF(file, 1, addText);
	}
	
	public static byte[] addPageNumbersToPDF(Object file, int firstPage, String addText) {
		try {
		    com.itextpdf.kernel.pdf.PdfReader reader;
			if (file instanceof File) {
				reader = new com.itextpdf.kernel.pdf.PdfReader(new FileInputStream((File)file));
			} else if (file instanceof String) {
				reader = new com.itextpdf.kernel.pdf.PdfReader(new FileInputStream((String)file));
			} else if (file instanceof byte[]) {
				reader = new com.itextpdf.kernel.pdf.PdfReader(new ByteArrayInputStream((byte[])file));
			} else {
				reader = null;
			}
			
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			com.itextpdf.kernel.pdf.PdfWriter writer = new com.itextpdf.kernel.pdf.PdfWriter(os);
			
			com.itextpdf.kernel.pdf.PdfDocument pdfDoc = new com.itextpdf.kernel.pdf.PdfDocument(reader, writer);
			com.itextpdf.layout.Document doc = new com.itextpdf.layout.Document(pdfDoc);
			
			PdfPage page;
			Paragraph paragraph;
			
			int numberOfPages = pdfDoc.getNumberOfPages();
			
			PdfFontFactory.register("/arial.ttf", "arial");
			PdfFont arialFont = PdfFontFactory.createRegisteredFont("arial", com.itextpdf.io.font.PdfEncodings.IDENTITY_H, true);

			for (int i = firstPage; i <= numberOfPages; i++) {
				page = pdfDoc.getPage(i);
				page.setIgnorePageRotationForContent(true);
				
				paragraph = new Paragraph(String.format("Стр. %s из %s", i, numberOfPages));
				paragraph.setFont(arialFont).setFontSize(7.0F);
				doc.showTextAligned(paragraph, page.getPageSizeWithRotation().getWidth() - 30, 30, i, TextAlignment.RIGHT, VerticalAlignment.TOP, 0);
				
				if (addText != null && !addText.isEmpty()) {
					paragraph = new Paragraph(addText);
					paragraph.setFont(arialFont).setFontSize(7.0F);
					doc.showTextAligned(paragraph, 30, 30, i, TextAlignment.LEFT, VerticalAlignment.TOP, 0);
				}
			}
			
			doc.close();
			os.close();
			return os.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
