package kz.tamur.admin;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jdom.Element;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

import kz.tamur.comps.Constants;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.util.Funcs;
import kz.tamur.util.ThreadLocalDateFormat;

public class ReportLogHelper {
    private static String fileSepar = File.separator;
    private static String dirlog = "logs";
	private static File rootLogDir = null;;
    
    private static final ThreadLocalDateFormat df = new ThreadLocalDateFormat("yyyy-MM-dd");
    private static final ThreadLocalDateFormat logDF = new ThreadLocalDateFormat("HH:mm:ss.SSS");
    private static final ThreadLocalDateFormat repDF = new ThreadLocalDateFormat("dd.MM.yyyy HH:mm:ss");
    private static final ThreadLocalDateFormat dfFull = new ThreadLocalDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    
    private static final Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + ReportLogHelper.class.getName());

    public static String readLog(String dirPath, String logName, int fromRow, int toRow, Date dateFrom, Date dateTo,
    		String eventType, String event, List<String> users, String ip) {
    	List<String[]> arr = readLogAsList(dirPath, logName, fromRow, toRow, dateFrom, dateTo, eventType, event, users, ip);
        StringBuilder sb = new StringBuilder();
		for (String[] row : arr) {
			for (String entry : row)
				sb.append(entry + " | ");
			sb.append("\r\n");
		}
		return sb.toString();
    }
    
    public static List<String[]> readLogAsList(String dirPath, String logName, int fromRow, int toRow, Date dateFrom, Date dateTo,
    		String eventType, String event, List<String> users, String ip) {
    	return readLogAsList(dirPath, logName, fromRow, toRow, dateFrom, dateTo, eventType, event, users, ip, 100000);
    }
    
    public static List<String[]> readLogAsList(String dirPath, String logName, int fromRow, int toRow, Date dateFrom, Date dateTo,
    		String eventType, String event, List<String> users, String ip, long maxRows) {
    	
    	List<String[]> arr = new ArrayList<String[]>();

    	try {
    		if (dateFrom == null)
    			dateFrom = df.parse("1970-01-01");
    	} catch (Exception e) {
    	}
    	if (dateTo == null)
    		dateTo = new Date();
    	
    	String startFileName = logName + ".log." +  df.format(dateFrom);
    	String endFileName = logName + ".log." +  df.format(dateTo);
    	String curFileName = logName + ".log." +  df.format(new Date());
    	
    	Calendar beginDate = new GregorianCalendar();
    	beginDate.setTime(dateFrom);
    	Calendar endDate = new GregorianCalendar();
    	endDate.setTime(dateTo);
    	
        ArrayList<File> amd=new ArrayList<File>();

        File file = Funcs.getCanonicalFile(dirPath + "/" + logName + ".log");
        if (curFileName.compareTo(startFileName) >= 0 
        		&& curFileName.compareTo(endFileName) <= 0) {

            amd.add(file);
        }

        File dir = file.getParentFile();
        File[] files = dir.listFiles();
        if (files != null) {
	        for(int i = 0; i < files.length; ++i) {
	            if (files[i].getName().compareTo(startFileName) >= 0 
	            		&& files[i].getName().compareTo(endFileName) <= 0) {
	
	                amd.add(files[i]);
	            }
	        }

        }
        long count = 0;
        for(int i=0; i < amd.size(); ++i){
//            if(gt) break;
            try {
            	File f = (File)amd.get(i);
            	String fileDateStr = (f.getName().length() > logName.length() + 4) 
            			? f.getName().substring(logName.length() + 5) : df.format(new Date());
            	
            	String timeStart = (df.format(dateFrom).compareTo(fileDateStr) == 0) 
            			? logDF.format(dateFrom) : "00:00:00.000";
            	
            	String timeEnd = (df.format(dateTo).compareTo(fileDateStr) == 0) 
            			? logDF.format(dateTo) : "23:59:59.999";
            	
            	List<String[]> arr2 = new ArrayList<String[]>();
    			readLogFile(f, fileDateStr, eventType, event, users, ip, timeStart, timeEnd, arr2, count, maxRows);
    			arr.addAll(arr2);
    			if (count >= maxRows) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return arr;
    }
    
    public static void readLogFile(File file, String date, String eventType, String event, List<String> users, String ip, String timeStart, String timeEnd, List<String[]> arr, long curRows, long maxRows) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String delim = "|";
        
        String line = null;
        
        while ((line = Funcs.normalizeInput(br.readLine())) != null) {
        	StringTokenizer st = new StringTokenizer(line, delim);
            int count = st.countTokens();
            if (count > 6) {
                String time = st.nextToken().trim();
                if (time.compareTo(timeStart) < 0) continue;
                
                if (time.compareTo(timeEnd) > 0) {
                	br.close();
                	return;
                }
                
                String logLevel = time.substring(time.indexOf(" ") + 1);
                time = time.substring(0, time.indexOf(" "));
                
                String logType = st.nextToken().trim(); // Тип события
                if (eventType != null && !eventType.equals(logType)) continue;

                String logEvent = st.nextToken().trim(); // Событие
                if (event != null && !event.equals(logEvent)) continue;

                String logUser = st.nextToken().trim(); // Пользователь
                if (users != null && users.size() > 0 && !users.contains(logUser)) continue;

                String logIp = st.nextToken().trim(); // IP
                if (ip != null && ip.length() > 0 && !logIp.contains(ip)) continue;

                String logComp = st.nextToken().trim(); // IP

                String logAdmin = st.nextToken().trim(); // IP

                String logMsg = st.nextToken().trim(); // IP

                String[] row = new String[10];
                row[0] = date;
                row[1] = time;
                row[2] = logLevel;
                row[3] = logType;
                row[4] = logEvent;
                row[5] = logUser;
                row[6] = logIp;
                row[7] = logComp;
                row[8] = logAdmin;
                row[9] = logMsg;
                
                arr.add(0, row);
                if (++curRows >= maxRows) break;
            }
        }
    	br.close();
    }

    public static File printToFile(Date dateFrom, Date dateTo,
    		String eventType, String event, List<String> users, String ip, List<String[]> arr) {
    	
    	Workbook wb = new XSSFWorkbook();
    	
    	Sheet sh = wb.createSheet("Мониторинг");
    	
    	Row row = sh.createRow(0);
    	sh.addMergedRegion(new CellRangeAddress(0, 0, 0, 9));
    	Cell cell = row.createCell(0);
    	cell.setCellValue("Отчет по действиям пользователей");
    	
    	CellStyle st0 = wb.createCellStyle();
    	st0.setAlignment(CellStyle.ALIGN_CENTER);
    	Font f = wb.createFont();
        f.setBoldweight(Font.BOLDWEIGHT_BOLD);
        f.setFontHeightInPoints((short)14);
    	st0.setFont(f);

    	cell.setCellStyle(st0);

    	String period = "";
    	if (dateFrom != null || dateTo != null) {
    		period = "за период";
    		
    		if (dateFrom != null) {
    			period += " с " + repDF.format(dateFrom);
    		}
    		if (dateTo != null) {
    			period += " по " + repDF.format(dateTo);
    		}
    	} else {
    		period = "за весь период";
    	}
    	
    	row = sh.createRow(1);
        sh.addMergedRegion(new CellRangeAddress(1, 1, 0, 9));
    	cell = row.createCell(0);
    	cell.setCellValue(period);
    	cell.setCellStyle(st0);

    	sh.setColumnWidth(0, 3500);
    	sh.setColumnWidth(1, 3700);
    	sh.setColumnWidth(2, 2500);
    	sh.setColumnWidth(3, 4000);
    	sh.setColumnWidth(4, 4000);
    	sh.setColumnWidth(5, 6000);
    	sh.setColumnWidth(6, 3800);
    	sh.setColumnWidth(7, 3800);
    	sh.setColumnWidth(8, 2000);
    	sh.setColumnWidth(9, 10000);

    	row = sh.createRow(3);
    	cell = row.createCell(0);
    	cell.setCellValue("Тип события: ");
    	
    	if (eventType != null) {
	    	cell = row.createCell(1);
	    	cell.setCellValue(eventType);
    	}

    	row = sh.createRow(4);
    	cell = row.createCell(0);
    	cell.setCellValue("Событие: ");
    	
    	if (event != null) {
	    	cell = row.createCell(1);
	    	cell.setCellValue(event);
    	}

    	row = sh.createRow(5);
    	cell = row.createCell(0);
    	cell.setCellValue("Пользователь: ");
    	
    	if (users != null && users.size() > 0) {
	    	cell = row.createCell(1);
	    	String val = users.get(0);
	    	for (int i=1; i<users.size(); i++)
	    		val += ", " + users.get(i);
	    	cell.setCellValue(val);
    	}

    	row = sh.createRow(6);
    	cell = row.createCell(0);
    	cell.setCellValue("IP: ");
    	
    	if (ip != null) {
	    	cell = row.createCell(1);
	    	cell.setCellValue(ip);
    	}

    	int r = 8;

    	row = sh.createRow(r++);

    	String[] line1 = {"Дата", "Время", "Уровень", "Тип события", "Событие", "Пользователь", "IP", "Компьютер", "Админ?", "Примечание"};
    	
    	int c = 0;
    	
    	CellStyle st1 = wb.createCellStyle();
    	st1.setBorderBottom(CellStyle.BORDER_THIN);
   		st1.setBorderTop(CellStyle.BORDER_THIN);
		st1.setBorderLeft(CellStyle.BORDER_THIN);
		st1.setBorderRight(CellStyle.BORDER_THIN);
    	st1.setAlignment(CellStyle.ALIGN_CENTER);
    	f = wb.createFont();
        f.setBoldweight(Font.BOLDWEIGHT_BOLD);
        f.setFontHeightInPoints((short)11);
    	st1.setFont(f);

    	CellStyle st2 = wb.createCellStyle();
    	st2.setBorderBottom(CellStyle.BORDER_THIN);
   		st2.setBorderTop(CellStyle.BORDER_THIN);
		st2.setBorderLeft(CellStyle.BORDER_THIN);
		st2.setBorderRight(CellStyle.BORDER_THIN);
		st2.setWrapText(true);
		st2.setVerticalAlignment(CellStyle.VERTICAL_TOP);

    	for (String entry : line1) {
    		cell = row.createCell(c++);
    		cell.setCellValue(entry);
    		cell.setCellStyle(st1);
    	}

    	if (arr != null) {
	    	for (String[] line : arr) {
	        	row = sh.createRow(r++);
	
	        	c = 0;
	        	for (String entry : line) {
	        		cell = row.createCell(c++);
	        		cell.setCellValue(entry);
	        		cell.setCellStyle(st2);
	        	}
	    	}
    	}

    	try {
    		File ret = Funcs.createTempFile("temp", ".xlsx");
	    	FileOutputStream os = new FileOutputStream(ret);
	        wb.write(os);
	        os.close();
	        
	        return ret;
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return null;
    }
    
    public static File printToFileAudit(Date dateFrom, Date dateTo,
    		String queryType, String rule, String tblName, List<String> users, List<Object[]> arr) {
    	
    	Workbook wb = new XSSFWorkbook();
    	
    	Sheet sh = wb.createSheet("Мониторинг");
    	
    	Row row = sh.createRow(0);
    	sh.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));
    	Cell cell = row.createCell(0);
    	cell.setCellValue("Отчет по запросам к БД");
    	
    	CellStyle st0 = wb.createCellStyle();
    	st0.setAlignment(CellStyle.ALIGN_CENTER);
    	Font f = wb.createFont();
        f.setBoldweight(Font.BOLDWEIGHT_BOLD);
        f.setFontHeightInPoints((short)14);
    	st0.setFont(f);

    	cell.setCellStyle(st0);

    	String period = "";
    	if (dateFrom != null || dateTo != null) {
    		period = "за период";
    		
    		if (dateFrom != null) {
    			period += " с " + repDF.format(dateFrom);
    		}
    		if (dateTo != null) {
    			period += " по " + repDF.format(dateTo);
    		}
    	} else {
    		period = "за весь период";
    	}
    	
    	row = sh.createRow(1);
        sh.addMergedRegion(new CellRangeAddress(1, 1, 0, 5));
    	cell = row.createCell(0);
    	cell.setCellValue(period);
    	cell.setCellStyle(st0);

    	sh.setColumnWidth(0, 7000);
    	sh.setColumnWidth(1, 5000);
    	sh.setColumnWidth(2, 5000);
    	sh.setColumnWidth(3, 4000);
    	sh.setColumnWidth(4, 7000);
    	sh.setColumnWidth(5, 12000);
    	sh.setColumnWidth(6, 9000);

    	row = sh.createRow(3);
    	cell = row.createCell(0);
    	cell.setCellValue("Тип запроса: ");
    	
    	if (queryType != null) {
	    	cell = row.createCell(1);
	    	cell.setCellValue(queryType);
    	}

    	row = sh.createRow(4);
    	cell = row.createCell(0);
    	cell.setCellValue("Правило: ");
    	
    	if (rule != null) {
	    	cell = row.createCell(1);
	    	cell.setCellValue(rule);
    	}

    	row = sh.createRow(5);
    	cell = row.createCell(0);
    	cell.setCellValue("Таблица БД: ");
    	
    	if (tblName != null) {
	    	cell = row.createCell(1);
	    	cell.setCellValue(tblName);
    	}

    	row = sh.createRow(6);
    	cell = row.createCell(0);
    	cell.setCellValue("Пользователь: ");
    	
    	if (users != null && users.size() > 0) {
	    	cell = row.createCell(1);
	    	String val = users.get(0);
	    	for (int i=1; i<users.size(); i++)
	    		val += ", " + users.get(i);
	    	cell.setCellValue(val);
    	}

    	int r = 8;

    	row = sh.createRow(r++);

    	String[] line1 = {"Время", "Таблица БД", "Правило", "Тип запроса", "Пользователь", "SQL запрос", "SQL_BIND"};
    	
    	int c = 0;
    	
    	CellStyle st1 = wb.createCellStyle();
    	st1.setBorderBottom(CellStyle.BORDER_THIN);
   		st1.setBorderTop(CellStyle.BORDER_THIN);
		st1.setBorderLeft(CellStyle.BORDER_THIN);
		st1.setBorderRight(CellStyle.BORDER_THIN);
    	st1.setAlignment(CellStyle.ALIGN_CENTER);
    	f = wb.createFont();
        f.setBoldweight(Font.BOLDWEIGHT_BOLD);
        f.setFontHeightInPoints((short)11);
    	st1.setFont(f);

    	CellStyle st2 = wb.createCellStyle();
    	st2.setBorderBottom(CellStyle.BORDER_THIN);
   		st2.setBorderTop(CellStyle.BORDER_THIN);
		st2.setBorderLeft(CellStyle.BORDER_THIN);
		st2.setBorderRight(CellStyle.BORDER_THIN);
		st2.setWrapText(true);
		st2.setVerticalAlignment(CellStyle.VERTICAL_TOP);

    	for (String entry : line1) {
    		cell = row.createCell(c++);
    		cell.setCellValue(entry);
    		cell.setCellStyle(st1);
    	}

    	if (arr != null) {
	    	for (Object[] line : arr) {
	        	row = sh.createRow(r++);
	
	        	c = 0;
	        	for (Object entry : line) {
	        		cell = row.createCell(c++);
	        		cell.setCellValue(entry != null ? entry.toString() : "");
	        		cell.setCellStyle(st2);
	        	}
	    	}
    	}

    	try {
    		File ret = Funcs.createTempFile("temp", ".xlsx");
	    	FileOutputStream os = new FileOutputStream(ret);
	        wb.write(os);
	        os.close();
	        
	        return ret;
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return null;
    }

    public static int lpp(Reader r, String[] args, String start, String end, Dispatch sheet, int row) throws IOException {
        BufferedReader br = new BufferedReader(r);
        String line = Funcs.normalizeInput(br.readLine());
        String date,delim;
        delim=args[2];
        Pattern[] cRe_a = null;
        String[] cRe_a_i = null;
        if(args.length>5){
            cRe_a=new Pattern[(args.length-5)/2];
            cRe_a_i=new String[(args.length-5)/2];
            for(int i=5;i<args.length;i++){
                cRe_a_i[(i-5)/2]=args[i];
                if(args.length > i+1)
                    cRe_a[(i-5)/2] = Pattern.compile(args[i+1], Pattern.CASE_INSENSITIVE);
                i++;
            }
        }
        boolean lt = false;
        boolean gt = false;
        int countDelim = 0;
        while(line != null) {
            StringTokenizer st=new StringTokenizer(line,delim);
            countDelim=0;
            int count = st.countTokens();
            boolean par=true;
            if (count > 0) {
                date = st.nextToken();
                lt=(date.compareTo(start)<0);
                gt=(date.compareTo(end)>0);
                if(lt || gt){
                    line = Funcs.sanitizeXml(br.readLine());
                    continue;
                }
                for (int i = 0; i < count - 1; ++i) {
                    countDelim++;
                    String str = st.nextToken();
                    if(cRe_a_i!=null && cRe_a_i.length>0)
                    for(int j=0;j<cRe_a_i.length;++j){
                        if(cRe_a_i[j].equals(""+countDelim) && cRe_a[j]!=null){
                            if(!cRe_a[j].matcher(str).find())
                                par=false;
                        }
                    }
                    if(!par)
                        break;
                }
                if(par){
                    int j = 1;
                    StringTokenizer stz = new StringTokenizer(line, "|");

                    fillCell(sheet, row, j++, stz.nextToken().trim(), true);
                    while (stz.hasMoreTokens()) {
                    	fillCell(sheet, row, j++, stz.nextToken().trim());
                    }  
                    row++;
                }
            }
            line = Funcs.normalizeInput(br.readLine());
        }
        return row;
    }

    public static void lpp(Reader r, String type,
			String action, String user, String ip, String comp, String start, String end, SortedMap<Date,Element> smap) throws IOException {
        BufferedReader br = new BufferedReader(r);
        String line = Funcs.normalizeInput(br.readLine());
        String date,delim;
        delim = "|";
        Pattern[] cRe_a = null;
        String[] cRe_a_i = null;
        int size = 0;
        if (type != null) size++;
        if (action != null) size++;
        if (user != null) size++;
        if (ip != null) size++;
        if (comp != null) size++;

        cRe_a=new Pattern[size];
        cRe_a_i=new String[size];
        size = 0;
        if (type != null) {
            cRe_a_i[size] = "1";
            cRe_a[size] = Pattern.compile(type);
            size++;
        }
        if (action != null) {
            cRe_a_i[size] = "2";
            cRe_a[size] = Pattern.compile(action);
            size++;
        }
        if (user != null) {
            cRe_a_i[size] = "3";
            cRe_a[size] = Pattern.compile(user);
            size++;
        }
        if (ip != null) {
            cRe_a_i[size] = "4";
            cRe_a[size] = Pattern.compile(ip, Pattern.CASE_INSENSITIVE);
            size++;
        }
        if (comp != null) {
            cRe_a_i[size] = "5";
            cRe_a[size] = Pattern.compile(comp, Pattern.CASE_INSENSITIVE);
            size++;
        }

        boolean lt = false;
        boolean gt = false;
        int countDelim = 0;
        while(line != null) {
            StringTokenizer st=new StringTokenizer(line,delim);
            countDelim=0;
            int count = st.countTokens();
            boolean par=true;
            if (count > 0) {
                date = st.nextToken();
                lt=(date.compareTo(start)<0);
                gt=(date.compareTo(end)>0);
                if(lt || gt){
                    line = br.readLine();
                    continue;
                }
                for (int i = 0; i < count - 1; ++i) {
                    countDelim++;
                    String str = st.nextToken();
                    if(cRe_a_i!=null && cRe_a_i.length>0)
                    	for(int j=0;j<cRe_a_i.length;++j){
	                        if(cRe_a_i[j].equals(""+countDelim) && cRe_a[j]!=null){
	                            if(!cRe_a[j].matcher(str).find())
	                                par=false;
	                        }
	                    }
                    if(!par)
                        break;
                }
                if(par){
                    //System.out.println(line);
                    StringTokenizer stz = new StringTokenizer(line, "|");

                    Element row = new Element("Row");
                    
                    Element cell = new Element("Cell");
                    String s = stz.nextToken().trim();
                    //s = s.substring(0, s.length() - 4);
                    Date key = new Date();
					try {
						key = dfFull.parse(s);
					} catch (ParseException e) {
						e.printStackTrace();
					}
                    cell.setText(s);
                    row.addContent(cell);
                    while (stz.hasMoreTokens()) {
                        cell = new Element("Cell");
                        cell.setText(stz.nextToken().trim());
                        row.addContent(cell);
                    }  
                    smap.put(key, row);
                }
            }
            line = Funcs.normalizeInput(br.readLine());
        }
    }

    public static void createReport(Map messageMap, String filePrefix, String title1, String title2) {
    	ComThread.InitSTA();
        ActiveXComponent excel = ActiveXComponent.createNewInstance("Excel.Application");
        Dispatch wbooks = excel.getProperty("Workbooks").toDispatch();
        Dispatch wbook = Dispatch.call(wbooks, "Add").toDispatch();
        try {
            Dispatch sheet = Dispatch.call(wbook, "Worksheets", new Variant(1)).toDispatch();

            int row = 1;
            int tWidth = 6;
            if (title1 != null) {
                Dispatch cell = Dispatch.call(sheet, "Cells", new Variant(row), new Variant(1)).toDispatch();
                Dispatch.put(cell, "Value", new Variant(title1));
                Dispatch font = Dispatch.call(cell, "Font").toDispatch();
                Dispatch.put(font, "Bold", new Variant(true));

                Dispatch cell3 = Dispatch.call(sheet, "Cells", new Variant(row), new Variant(tWidth)).toDispatch();
                Dispatch range2 = Dispatch.call(sheet, "Range", cell, cell3).toDispatch();
                Dispatch.put(range2, "MergeCells", new Variant(true));
                Dispatch.put(range2, "HorizontalAlignment", new Variant(-4108));
                row++;
            }
            if (title2 != null) {
                Dispatch cell = Dispatch.call(sheet, "Cells", new Variant(row), new Variant(1)).toDispatch();
                Dispatch.put(cell, "Value", new Variant(title2));
                Dispatch font = Dispatch.call(cell, "Font").toDispatch();
                Dispatch.put(font, "Bold", new Variant(true));

                Dispatch cell3 = Dispatch.call(sheet, "Cells", new Variant(row), new Variant(tWidth)).toDispatch();
                Dispatch range2 = Dispatch.call(sheet, "Range", cell, cell3).toDispatch();
                Dispatch.put(range2, "MergeCells", new Variant(true));
                Dispatch.put(range2, "HorizontalAlignment", new Variant(-4108));
                row++;
            }

            row++;

            Dispatch cell1 = Dispatch.call(sheet, "Cells", new Variant(row), new Variant(1)).toDispatch();
            
            String[] colNames = {"Тип события", "Событие", "Пользователь", "IP адрес", "Имя компьютера", "Количество"};
            for (int i=0; i<tWidth; i++) {
                Dispatch cell = Dispatch.call(sheet, "Cells", new Variant(row), new Variant(i+1)).toDispatch();
                Dispatch.put(cell, "Value", new Variant(colNames[i]));
                Dispatch font = Dispatch.call(cell, "Font").toDispatch();
                Dispatch.put(font, "Bold", new Variant(true));
            }
            row++;
            for(Iterator it=messageMap.keySet().iterator();it.hasNext();){
                String mtek=(String)it.next();
                StringTokenizer st = new StringTokenizer(mtek, "|");
                String comp = st.nextToken();
                String user = st.nextToken();
                String action = st.nextToken();
                String ip = st.nextToken();
                String type = st.nextToken();
                String count = ((Integer)messageMap.get(mtek)).toString();
                
                int j = 1;
                fillCell(sheet, row, j++, type);
                fillCell(sheet, row, j++, action);
                fillCell(sheet, row, j++, user);
                fillCell(sheet, row, j++, ip);
                fillCell(sheet, row, j++, comp);
                fillCell(sheet, row++, j++, count);
           }

            for (int i=0; i<tWidth; i++) {
                Dispatch column = Dispatch.call(sheet, "Columns", new Variant(i+1)).toDispatch();
                Dispatch.call(column, "AutoFit");
            }
            
            Dispatch cell2 = Dispatch.call(sheet, "Cells", new Variant(--row), new Variant(tWidth)).toDispatch();

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
            border = Dispatch.call(range, "Borders", new Variant(11)).toDispatch();
            Dispatch.put(border, "LineStyle", new Variant(1));
            Dispatch.put(border, "Weight", new Variant(2));
            Dispatch.put(border, "ColorIndex", new Variant(-4105));
            border = Dispatch.call(range, "Borders", new Variant(12)).toDispatch();
            Dispatch.put(border, "LineStyle", new Variant(1));
            Dispatch.put(border, "Weight", new Variant(2));
            Dispatch.put(border, "ColorIndex", new Variant(-4105));

        	File dir = Funcs.getCanonicalFile(dirlog+fileSepar+"excel");
        	dir.mkdirs();
        	int num = 0;
        	File[] files = dir.listFiles();
        	if (files.length > 0) {
        		Arrays.sort(files);
        		int k = files.length - 1;
        		while (k >= 0) {
	        		String s = files[k].getName();
	        		if (s.length() == filePrefix.length() + 10 && s.startsWith(filePrefix) && s.endsWith((".xls"))) {
	        			num = Integer.parseInt(s.substring(filePrefix.length(), filePrefix.length() + 6)) + 1;
	        			break;
	        		}
	        		k--;
	        	}
        	}
        	
        	File file = Funcs.getCanonicalFile(new File(dir, String.format("%s%06d.xls", filePrefix, num)));

        	Dispatch.call(wbook, "SaveAs", new Variant(file.getAbsolutePath()));
        } finally {
        	Dispatch.call(excel, "Quit");
        	ComThread.Release();
        	excel = null;
        }
    }
    
    public static Element createReport(Map messageMap, String title1, String title2) {
    	Element root = new Element("Report");
        try {
        	Element par = new Element("Title1");
        	par.setText(title1);
        	root.addContent(par);

        	par = new Element("Title2");
        	par.setText(title2);
        	root.addContent(par);

        	String[] colNames = {"Тип события", "Событие", "Пользователь", "IP адрес", "Имя компьютера", "Количество"};
        	int tWidth = colNames.length;;

        	Element header = new Element("Header");
            for (int i=0; i<tWidth; i++) {
            	par = new Element("Cell");
            	par.setText(colNames[i]);
            	header.addContent(par);
            }
            root.addContent(header);
            
            for(Iterator it=messageMap.keySet().iterator();it.hasNext();){
                String mtek=(String)it.next();
                StringTokenizer st = new StringTokenizer(mtek, "|");
                String comp = st.nextToken();
                String user = st.nextToken();
                String action = st.nextToken();
                String ip = st.nextToken();
                String type = st.nextToken();
                String count = ((Integer)messageMap.get(mtek)).toString();
                
            	Element row = new Element("Row");
                root.addContent(row);
                
                par = new Element("Cell");
            	par.setText(type);
            	row.addContent(par);
                par = new Element("Cell");
            	par.setText(action);
            	row.addContent(par);
                par = new Element("Cell");
            	par.setText(user);
            	row.addContent(par);
                par = new Element("Cell");
            	par.setText(ip);
            	row.addContent(par);
                par = new Element("Cell");
            	par.setText(comp);
            	row.addContent(par);
                par = new Element("Cell");
            	par.setText(count);
            	row.addContent(par);
           }
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return root;
    }

    public static void fillCell(Dispatch sheet, int row, int column, String value, boolean isDate) {
        Dispatch cell = Dispatch.call(sheet, "Cells", new Variant(row), new Variant(column)).toDispatch();
        value = value.substring(0, value.length() - 4);
        Dispatch.put(cell, "NumberFormat", new Variant("@"));
        Dispatch.put(cell, "Value", new Variant(value));
    }

    public static void fillCell(Dispatch sheet, int row, int column, String value) {
        Dispatch cell = Dispatch.call(sheet, "Cells", new Variant(row), new Variant(column)).toDispatch();
        Dispatch.put(cell, "Value", new Variant(value));
    }

/*    private static void createReport()throws IOException{
        PrintWriter out = new PrintWriter(new FileWriter(fileRpt),true);
        out.println(" Сводный отчет по обмену сообщениями за период с "+start+" по "+end);
        out.println("------------------------------------------------------------------------------------------");
        out.println(" ТИП СООБЩЕНИЯ "+" КОД СОБЫТИЯ "+"                     СОБЫТИЕ                       "+" КОЛИЧЕСТВО ");
        out.println("------------------------------------------------------------------------------------------");
        for(Iterator it=messageMap.keySet().iterator();it.hasNext();){
             String mtek=(String)it.next();
             String mt=mtek.substring(0,mtek.indexOf("|"));
             String ek=mtek.substring(mtek.indexOf("|")+1);
             String ev=(String)eventMap.get(ek);
             out.println(mt+getSpace(15-mt.length())+"  "+ek+getSpace(13-ek.length())+"  "+ev+getSpace(50-ev.length())+"  "+((Integer)messageMap.get(mtek)).intValue());
        }
    }
*/
    private static void linkMonthLog(File dirLog, String logFileName){
        String str_curr_date=getCurrDate();
        String str_curr_year=getYear(System.currentTimeMillis());
        String str_last_year=""+(Integer.valueOf(str_curr_year).intValue()-1);
        
        Path dir = Paths.get(dirLog.getAbsolutePath());
        List<Path> files = Funcs.fileList(dir, logFileName + ".20??-??-??");

        String name_log=logFileName;
        String month_str=str_curr_date .substring(0,str_curr_date.lastIndexOf("-"));
        String name=name_log+"."+month_str;
        String name0=name_log+"."+str_curr_year+"-00";
        String name1=name_log+"."+str_last_year+"-00";
        
        try {
            for(int i=0; i<files.size(); ++i){
                if(!Files.exists(files.get(i))) continue;
                
                String file_name = files.get(i).toString().substring(0, files.get(i).toString().length()-3);
                Path month = Files.createFile(Paths.get(Funcs.getCanonicalFile(file_name).getAbsolutePath()));
                
                OutputStream os = Files.newOutputStream(month, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
                InputStream is = Files.newInputStream(files.get(i));
                if (is != null) {
                	Funcs.writeStream(is, os, Constants.MAX_ARCHIVED_SIZE);
                }
                is.close();
                os.close();
                
                Files.delete(files.get(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        List<Path> files_ = Funcs.fileList(dir, null);
        ArrayList<Path> am = new ArrayList<Path>();
        for(int i=0;i<files_.size();++i){
            String file_name = files_.get(i).getFileName().toString();
            if(file_name.length()==name.length() && file_name.compareTo(name)<0 && file_name.compareTo(name0)>0 ){
                am.add(files_.get(i));
            }
        }
        if(am.size()>0){
            addToYearArh(am);
        }
        am = new ArrayList<Path>();
        for(int i=0;i<files_.size();++i){
            String file_name = files_.get(i).getFileName().toString();
            if(file_name.length()==name.length() && file_name.compareTo(name0)<0 && file_name.compareTo(name1)>0 ){
                am.add(files_.get(i));
            }
        }
        if(am.size()>0){
            addToYearArh(am);
        }
    }
    private static void addToYearArh(List<Path> files){
        String name=files.get(0).getFileName().toString();
        String name_log=name.substring(0,name.lastIndexOf("."));
        String data_str=name.substring(name.lastIndexOf("."));
        String name_year=name_log+data_str.substring(0,data_str.lastIndexOf("-"))+".zip";
        Path year=null,year_=null,tmp=null;
        Path dir = files.get(0).getParent();

        List<Path> files_ = Funcs.fileList(dir, null);
        for(int i=0;i<files_.size();++i){
            if(files_.get(i).getFileName().toString().equals(name_year)){
                year = files_.get(i);
                break;
            }
        }

        if (year != null) {
        	year_ = dir.resolve("_" + name_year);
        	year.toFile().renameTo(year_.toFile());
        }
        year = dir.resolve(name_year);
        
        try{
            OutputStream os = Files.newOutputStream(year);
            ZipOutputStream zos = new ZipOutputStream(os);
            Collection<String> fc=new Vector<String>();
            for(int i =0;i<files.size();++i){
                fc.add(files.get(i).getFileName().toString());
            }
            if(year_!=null){
                ZipFile zf = new ZipFile(year_.toFile());
                Enumeration e = zf.entries();
                while (e.hasMoreElements()) {
                    ZipEntry ze = (ZipEntry)e.nextElement();
                    ZipEntry zenew=new ZipEntry(ze.getName());
                    InputStream is ;
                    if(fc.contains(ze.getName())){
                        tmp = Files.createTempFile(dir, "tmp", ".tmp");
                        
                        OutputStream osf = Files.newOutputStream(tmp, StandardOpenOption.CREATE);
                        is = zf.getInputStream(ze);
                        Funcs.writeStream(is, osf, Constants.MAX_ARCHIVED_SIZE);
                        osf.close();
                        is.close();
                        osf = Files.newOutputStream(tmp, StandardOpenOption.APPEND);
                        is = Files.newInputStream(dir.resolve(ze.getName()));
                        Funcs.writeStream(is, osf, Constants.MAX_ARCHIVED_SIZE);
                        osf.close();
                        is.close();
                        fc.remove(ze.getName());
                        is = Files.newInputStream(tmp);
                    }else{
                        is = zf.getInputStream(ze);
                    }
                    zos.putNextEntry(zenew);
                    
                    Funcs.writeStream(is, zos, Constants.MAX_ARCHIVED_SIZE);
                    is.close();
                    
                    if (tmp != null) Files.delete(tmp);
                    tmp = null;
                }
                zf.close();
                Files.delete(year_);
            }
            
            for(int i =0;i<files.size();++i){
                if(fc.contains(files.get(i).getFileName().toString())){
                    InputStream is = Files.newInputStream(files.get(i));
                    ZipEntry ze = new ZipEntry(files.get(i).getFileName().toString());
                    zos.putNextEntry(ze);
                    Funcs.writeStream(is, zos, Constants.MAX_ARCHIVED_SIZE);
                    is.close();
                }
                Files.delete(files.get(i));
            }
            zos.closeEntry();
            zos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void collectDataForRpt(File file,boolean isBalanse, String start, String end){
        String file_name=file.getName();
        String start_year=start.substring(0,start.indexOf("-"));
        String start_month=start.substring(0,start.lastIndexOf("-"));
        String end_year=end.substring(0,end.indexOf("-"));
        String end_month=end.substring(0,end.lastIndexOf("-"));
        String str_curr_date=getCurrDate();
        ArrayList<File> ay=new ArrayList<File>();
        ArrayList<File> amd=new ArrayList<File>();
        if(end.compareTo(str_curr_date)>=0){
            //Текущий день
            amd.add(file);
        }
        File dir=file.getParentFile();
        File[] files=dir.listFiles();
        
        Map map = new TreeMap();
        boolean gt = false;
        for(int i=0;i<files.length;++i){
            if(files[i].getName().indexOf(file_name+".")<0) continue;
            String suffix=files[i].getName().substring(file_name.length()+1);
            if(suffix.indexOf("zip")>0){
                String year_=suffix.substring(0,suffix.indexOf("."));
                if(year_.compareTo(start_year)<0 || year_.compareTo(end_year)>0) continue;
                ay.add(files[i]);
            }else if(suffix.indexOf("-")>0){
                int ind=suffix.indexOf("-");
                if(suffix.indexOf("-",ind+1)>0){
                    //Дни месяца
                    if(suffix.compareTo(start) < 0 || suffix.compareTo(end) > 0) continue;
                    amd.add(files[i]);
                }else{
                    //Месяцы
                    if(suffix.compareTo(start_month) < 0 || suffix.compareTo(end_month) > 0) continue;
                    amd.add(files[i]);
                }
            }
        }
        for(int i=0;i<amd.size();++i){
            if(gt) break;
            try {
                gt = prepareUserActons(new FileReader((File)amd.get(i)),null,null, null, null, null,
                	isBalanse, map, start, end);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for(int i=0;i<ay.size();++i){
            if(gt) break;
            File year_=(File)ay.get(i);
            try {
                ZipFile zf = new ZipFile(year_);
                Enumeration e = zf.entries();
                while (e.hasMoreElements()) {
                    ZipEntry ze = (ZipEntry)e.nextElement();
                    String file_name_=ze.getName();
                    file_name_= file_name_.substring(file_name_.lastIndexOf(".")+1);
                    if(file_name_.compareTo(start_month) < 0 || file_name_.compareTo(end_month) > 0) continue;
                    InputStream is = zf.getInputStream(ze);
                    gt = prepareUserActons(new InputStreamReader(is),null, null, null, null, null,
                    	isBalanse, map, start, end);
                    is.close();
                }
                zf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(isBalanse){
        	String title1 = "Сводный отчет по действиям пользователя";
        	String title2 = "за период с "+start+" по "+end;
            createReport(map, "rep", title1, title2);
        }
    }

    private static Element collectDataForRpt(File file,boolean isBalanse, String type,
			String action, String user, String ip, String comp, String start, String end){
    	if (start == null) start = getCurrDate();
    	if (end == null) end = getCurrDate();
    	
    	String end2 = end;
    	end = getNextDate(end);
    	
    	String file_name=file.getName();
        String start_year=start.substring(0,start.indexOf("-"));
        String start_month=start.substring(0,start.lastIndexOf("-"));
        String end_year=end.substring(0,end.indexOf("-"));
        String end_month=end.substring(0,end.lastIndexOf("-"));
        String str_curr_date=getCurrDate();
        ArrayList<File> ay=new ArrayList<File>();
        ArrayList<File> amd=new ArrayList<File>();
        if(end.compareTo(str_curr_date)>=0){
            //Текущий день
            amd.add(file);
        }
        File dir=file.getParentFile();
        File[] files=dir.listFiles();
        
        Map map = new TreeMap();
        boolean gt = false;
        for(int i=0;i<files.length;++i){
            if(files[i].getName().indexOf(file_name+".")<0) continue;
            String suffix=files[i].getName().substring(file_name.length()+1);
            if(suffix.indexOf("zip")>0){
                String year_=suffix.substring(0,suffix.indexOf("."));
                if(year_.compareTo(start_year)<0 || year_.compareTo(end_year)>0) continue;
                ay.add(files[i]);
            }else if(suffix.indexOf("-")>0){
                int ind=suffix.indexOf("-");
                if(suffix.indexOf("-",ind+1)>0){
                    //Дни месяца
                    if(suffix.compareTo(start) < 0 || suffix.compareTo(end) > 0) continue;
                    amd.add(files[i]);
                }else{
                    //Месяцы
                    if(suffix.compareTo(start_month) < 0 || suffix.compareTo(end_month) > 0) continue;
                    amd.add(files[i]);
                }
            }
        }
        for(int i=0;i<amd.size();++i){
            if(gt) break;
            try {
                gt = prepareUserActons(new FileReader((File)amd.get(i)), type, action, user, ip, comp,
                	isBalanse, map, start, end);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for(int i=0;i<ay.size();++i){
            if(gt) break;
            File year_=(File)ay.get(i);
            try {
                ZipFile zf = new ZipFile(year_);
                Enumeration e = zf.entries();
                while (e.hasMoreElements()) {
                    ZipEntry ze = (ZipEntry)e.nextElement();
                    String file_name_=ze.getName();
                    file_name_= file_name_.substring(file_name_.lastIndexOf(".")+1);
                    if(file_name_.compareTo(start_month) < 0 || file_name_.compareTo(end_month) > 0) continue;
                    InputStream is = zf.getInputStream(ze);
                    gt = prepareUserActons(new InputStreamReader(is), null, null, null, null, null,
                    	isBalanse, map, start, end);
                    is.close();
                }
                zf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(isBalanse){
        	String title1 = "Сводный отчет по действиям пользователя";
        	String title2 = "за период с "+start+" по "+end2;
            return createReport(map, title1, title2);
        }
        return null;
    }

    private static void collectData(File file,String[] args, String start, String end){
        String file_name=file.getName();
        String start_year=start.substring(0,start.indexOf("-"));
        String start_month=start.substring(0,start.lastIndexOf("-"));
        String end_year=end.substring(0,end.indexOf("-"));
        String end_month=end.substring(0,end.lastIndexOf("-"));
        ArrayList<File> ay=new ArrayList<File>();
        ArrayList<File> amd=new ArrayList<File>();
//        if(end.compareTo(str_curr_date)>=0){
            //Текущий день
            amd.add(file);
//        }
        File dir=file.getParentFile();
        File[] files=dir.listFiles();
        for(int i=0;i<files.length;++i){
            if(files[i].getName().indexOf(file_name+".")<0) continue;
            String suffix=files[i].getName().substring(file_name.length()+1);
            if(suffix.indexOf("zip")>0){
                String year_=suffix.substring(0,suffix.indexOf("."));
                if(year_.compareTo(start_year)<0 || year_.compareTo(end_year)>0) continue;
                ay.add(files[i]);
            }else if(suffix.indexOf("-")>0){
                int ind=suffix.indexOf("-");
                if(suffix.indexOf("-",ind+1)>0){
                    //Дни месяца
                    if(suffix.compareTo(start) < 0 || suffix.compareTo(end) > 0) continue;
                    amd.add(files[i]);
                }else{
                    //Месяцы
                    if(suffix.compareTo(start_month) < 0 || suffix.compareTo(end_month) > 0) continue;
                    amd.add(files[i]);
                }
            }
        }
        
    	ComThread.InitSTA();
        ActiveXComponent excel = ActiveXComponent.createNewInstance("Excel.Application");
        Dispatch wbooks = excel.getProperty("Workbooks").toDispatch();
        Dispatch wbook = Dispatch.call(wbooks, "Add").toDispatch();
        try {
            Dispatch sheet = Dispatch.call(wbook, "Worksheets", new Variant(1)).toDispatch();

            int row = 1;
        	String title1 = "Журнал действий пользователей";
        	String title2 = "за период с "+start+" по "+end;
        	int tWidth = 8;

            if (title1 != null) {
                Dispatch cell = Dispatch.call(sheet, "Cells", new Variant(row), new Variant(1)).toDispatch();
                Dispatch.put(cell, "Value", new Variant(title1));
                Dispatch font = Dispatch.call(cell, "Font").toDispatch();
                Dispatch.put(font, "Bold", new Variant(true));

                Dispatch cell2 = Dispatch.call(sheet, "Cells", new Variant(row), new Variant(tWidth)).toDispatch();
                Dispatch range = Dispatch.call(sheet, "Range", cell, cell2).toDispatch();
                Dispatch.put(range, "MergeCells", new Variant(true));
                Dispatch.put(range, "HorizontalAlignment", new Variant(-4108));
                row++;
            }
            if (title2 != null) {
                Dispatch cell = Dispatch.call(sheet, "Cells", new Variant(row), new Variant(1)).toDispatch();
                Dispatch.put(cell, "Value", new Variant(title2));
                Dispatch font = Dispatch.call(cell, "Font").toDispatch();
                Dispatch.put(font, "Bold", new Variant(true));

                Dispatch cell2 = Dispatch.call(sheet, "Cells", new Variant(row), new Variant(tWidth)).toDispatch();
                Dispatch range = Dispatch.call(sheet, "Range", cell, cell2).toDispatch();
                Dispatch.put(range, "MergeCells", new Variant(true));
                Dispatch.put(range, "HorizontalAlignment", new Variant(-4108));
                row++;
            }
            
            row++;
            
            Dispatch cell1 = Dispatch.call(sheet, "Cells", new Variant(row), new Variant(1)).toDispatch();
            String[] colNames = {"Дата и время", "Тип события", "Событие", "Пользователь", "IP адрес", "Имя компьютера", "Администратор", "Примечание"};
            for (int i=0; i<tWidth; i++) {
                Dispatch cell = Dispatch.call(sheet, "Cells", new Variant(row), new Variant(i+1)).toDispatch();
                Dispatch.put(cell, "Value", new Variant(colNames[i]));
                Dispatch font = Dispatch.call(cell, "Font").toDispatch();
                Dispatch.put(font, "Bold", new Variant(true));
            }
            row++;
            
	        for(int i=0;i<amd.size();++i){
	//            if(gt) break;
	            try {
	                row = lpp(new FileReader((File)amd.get(i)),args, start, end, sheet, row);
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	        for(int i=0;i<ay.size();++i){
	//            if(gt) break;
	            File year_=(File)ay.get(i);
	            try {
	                ZipFile zf = new ZipFile(year_);
	                Enumeration e = zf.entries();
	                while (e.hasMoreElements()) {
	                    ZipEntry ze = (ZipEntry)e.nextElement();
	                    String file_name_=ze.getName();
	                    file_name_= file_name_.substring(file_name_.lastIndexOf(".")+1);
	                    if(file_name_.compareTo(start_month) < 0 || file_name_.compareTo(end_month) > 0) continue;
	                    InputStream is = zf.getInputStream(ze);
	                    row = lpp(new InputStreamReader(is),args, start, end, sheet, row);
	                    is.close();
	                }
	                zf.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
            for (int i=0; i<tWidth; i++) {
                Dispatch column = Dispatch.call(sheet, "Columns", new Variant(i+1)).toDispatch();
                Dispatch.call(column, "AutoFit");
            }

            Dispatch cell2 = Dispatch.call(sheet, "Cells", new Variant(--row), new Variant(tWidth)).toDispatch();

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
            border = Dispatch.call(range, "Borders", new Variant(11)).toDispatch();
            Dispatch.put(border, "LineStyle", new Variant(1));
            Dispatch.put(border, "Weight", new Variant(2));
            Dispatch.put(border, "ColorIndex", new Variant(-4105));
            border = Dispatch.call(range, "Borders", new Variant(12)).toDispatch();
            //Dispatch.put(border, "LineStyle", new Variant(1));
            Dispatch.put(border, "Weight", new Variant(2));
            //Dispatch.put(border, "ColorIndex", new Variant(-4105));

        	File ldir = Funcs.getCanonicalFile(dirlog+fileSepar+"excel");
        	ldir.mkdirs();
        	int num = 0;
        	File[] lfiles = ldir.listFiles();
        	String filePrefix = "list";
        	if (lfiles.length > 0) {
        		Arrays.sort(lfiles);
        		int k = lfiles.length - 1;
        		while (k >= 0) {
	        		String s = lfiles[k].getName();
	        		if (s.length() == filePrefix.length() + 10 && s.startsWith(filePrefix) && s.endsWith((".xls"))) {
	        			num = Integer.parseInt(s.substring(filePrefix.length(), filePrefix.length() + 6)) + 1;
	        			break;
	        		}
	        		k--;
	        	}
        	}
        	
        	File lfile = Funcs.getCanonicalFile(new File(ldir, String.format("%s%06d.xls", filePrefix, num)));

        	Dispatch.call(wbook, "SaveAs", new Variant(lfile.getAbsolutePath()));
        } finally {
        	Dispatch.call(excel, "Quit");
        	ComThread.Release();
        	excel = null;
        }
    }

    private static Element collectData(File file, String type,
			String action, String user, String ip, String comp, String start, String end){
    	if (start == null) start = getCurrDate();
    	if (end == null) end = getCurrDate();  
    	
    	String end2 = end;
    	end = getNextDate(end);
    	
    	String file_name=file.getName();
        String start_year=start.substring(0,start.indexOf("-"));
        String start_month=start.substring(0,start.lastIndexOf("-"));
        String end_year=end.substring(0,end.indexOf("-"));
        String end_month=end.substring(0,end.lastIndexOf("-"));
        ArrayList<File> ay=new ArrayList<File>();
        ArrayList<File> amd=new ArrayList<File>();
//        if(end.compareTo(str_curr_date)>=0){
            //Текущий день
            amd.add(file);
//        }
        File dir=file.getParentFile();
        File[] files=dir.listFiles();
        for(int i=0;i<files.length;++i){
            if(files[i].getName().indexOf(file_name+".")<0) continue;
            String suffix=files[i].getName().substring(file_name.length()+1);
            if(suffix.indexOf("zip")>0){
                String year_=suffix.substring(0,suffix.indexOf("."));
                if(year_.compareTo(start_year)<0 || year_.compareTo(end_year)>0) continue;
                ay.add(files[i]);
            }else if(suffix.indexOf("-")>0){
                int ind=suffix.indexOf("-");
                if(suffix.indexOf("-",ind+1)>0){
                    //Дни месяца
                    if(suffix.compareTo(start) < 0 || suffix.compareTo(end) > 0) continue;
                    amd.add(files[i]);
                }else{
                    //Месяцы
                    if(suffix.compareTo(start_month) < 0 || suffix.compareTo(end_month) > 0) continue;
                    amd.add(files[i]);
                }
            }
        }
        
    	Element root = new Element("Report");
        try {
        	Element par = new Element("Title1");
        	par.setText("Журнал действий пользователей");
        	root.addContent(par);

        	par = new Element("Title2");
        	par.setText("за период с "+start+" по "+end2);
        	root.addContent(par);

            String[] colNames = {"Дата и время", "Тип события", "Событие", "Пользователь", "IP адрес", "Имя компьютера", "Администратор", "Примечание"};
        	int tWidth = colNames.length;
            Element header = new Element("Header");
            for (int i=0; i<tWidth; i++) {
            	par = new Element("Cell");
            	par.setText(colNames[i]);
            	header.addContent(par);
            }
            root.addContent(header);
            
            SortedMap<Date, Element> smap = new TreeMap<Date, Element>(
            		new Comparator<Date>() {

						public int compare(Date o1, Date o2) {
							return -o1.compareTo(o2);
						}
            			
            		}
            );
            
            
	        for(int i=0;i<amd.size();++i){
	//            if(gt) break;
	            try {
	                lpp(new FileReader((File)amd.get(i)), type,
	                	action, user, ip, comp, start, end, smap);
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	        for(int i=0;i<ay.size();++i){
	//            if(gt) break;
	            File year_=(File)ay.get(i);
	            try {
	                ZipFile zf = new ZipFile(year_);
	                Enumeration e = zf.entries();
	                while (e.hasMoreElements()) {
	                    ZipEntry ze = (ZipEntry)e.nextElement();
	                    String file_name_=ze.getName();
	                    file_name_= file_name_.substring(file_name_.lastIndexOf(".")+1);
	                    if(file_name_.compareTo(start_month) < 0 || file_name_.compareTo(end_month) > 0) continue;
	                    InputStream is = zf.getInputStream(ze);
	                    lpp(new InputStreamReader(is), type, action, user, ip, comp, start, end, smap);
	                    is.close();
	                }
	                zf.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	        
	        for (Iterator<Date> it = smap.keySet().iterator(); it.hasNext(); ) {
	        	Date key = it.next();
	        	root.addContent(smap.get(key));
	        }

        } catch (Exception e) {
        	e.printStackTrace();
        }
    	return root;
    }

    private static String getCurrDate(){
        Date curDate=new Date(System.currentTimeMillis());
        Calendar cal=Calendar.getInstance();
        cal.setTime(curDate);
        int mt=cal.get(Calendar.MONTH)+1;
        int dm=cal.get(Calendar.DAY_OF_MONTH);
        int g=cal.get(Calendar.YEAR);
        String year=""+g;
        String month=(mt>9?""+mt:"0"+mt);
        String day=(dm>9?""+dm:"0"+dm);
        return year+"-"+month+"-"+day;
    }

    private static String getNextDate(String date){
    	try {
	        Date curDate = df.parse(date);
	        Calendar cal = Calendar.getInstance();
	        cal.setTime(curDate);
	        cal.add(Calendar.DATE, 1);
	        int mt=cal.get(Calendar.MONTH)+1;
	        int dm=cal.get(Calendar.DAY_OF_MONTH);
	        int g=cal.get(Calendar.YEAR);
	        String year=""+g;
	        String month=(mt>9?""+mt:"0"+mt);
	        String day=(dm>9?""+dm:"0"+dm);
	        return year+"-"+month+"-"+day;
    	} catch(Exception e) {
    		return date;
    	}
    }

    private static String getYear(long time){
        Date curDate=new Date(time);
        Calendar cal=Calendar.getInstance();
        cal.setTime(curDate);
        int g=cal.get(Calendar.YEAR);
        return ""+g;
    }
   
/*    public static void main(String[] args) throws IOException {
        Element e = ReportLogHelper.getUserActionsList("2009-01-31", "2009-01-31", TYPE_NORMAL, null, null, null, null);
        ReportLauncher.generateReport(e, 0, "0");

        e = ReportLogHelper.getUserActionsReport("2009-01-31", "2009-01-31", TYPE_NORMAL, null, null, null, null);
        ReportLauncher.generateReport(e, 0, "0");
*//*


        if (args.length ==0 || (!args[0].equals("l")
                && !args[0].equals("r")
                && !args[0].equals("ra"))) {
            System.out.println("Invalid paramters");
            System.out.println("Usage: compress log <l>");
            System.out.println("       log for period <r> <yyyy-MM-dd> <yyyy-MM-dd> [reg_expr]");
            System.out.println("       agregate log report for period <ra> <yyyy-MM-dd> <yyyy-MM-dd>");
            System.exit(1);
        }
        String cmd=args[0];
        if(cmd.equals("l")){
            File[] files;
            
            String logFileName;
            if(args.length>1){
            	logFileName = args[1];
                pathlog=dirlog+fileSepar+logFileName+fileSepar+logFileName+".log";
            }
            else  {
            	logFileName = filelog;
            }
            List<File> flist=new Vector<File>();
            File dir=new File(dirlog + fileSepar + logFileName);
            File[] files_=dir.listFiles();
            Pattern cRe = Pattern.compile(logFileName+".log"+"\\.20..-..-..");
            for(int i=0;i<files_.length;++i){
                if(cRe.matcher(files_[i].getName()).find())
                flist.add(files_[i]);
            }
            files=new File[flist.size()];
            for(int i=0;i<files.length;++i){
                files[i]=(File)flist.get(i);
            }
            linkMonthLog(files, logFileName);
        }else if(cmd.equals("r")){
            String start = (args.length<4  || args[3]==null) ? getCurrDate() : args[3];
            String end = (args.length<5  || args[4]==null) ? getCurrDate() : args[4];

            String logFileName = args[1];
            String fullLogFileName=dirlog+fileSepar+logFileName+fileSepar+logFileName+".log";
            
            collectData(new File(fullLogFileName),args, start, end);
        }else if(cmd.equals("ra")){
            String start = (args.length<2  || args[1]==null) ? getCurrDate() : args[1];
            String end = (args.length<3  || args[2]==null) ? getCurrDate() : args[2];
            
            collectDataForRpt(new File(pathlog),true, start, end);
        }
*/
/*    }
*/    
    public static Element getUserActionsList(String start, String end, String type,
    										String action, String user, String ip, String comp, String logFileName) {
		String logDirName = "report";

		File rootDir = getRootLogDir();
		String path = rootDir.getAbsolutePath() + fileSepar + dirlog;
		String fullLogFileName=path+fileSepar+logDirName+fileSepar+logFileName;
		File fullLogFile = Funcs.getCanonicalFile(fullLogFileName);
        
        return collectData(fullLogFile, type, action, user, ip, comp, start, end);
    }
   
    public static Element getUserActionsReport(String start, String end, String type,
    										String action, String user, String ip, String comp, String logFileName) {
		String logDirName = "report";

		File rootDir = getRootLogDir();
		String path = rootDir.getAbsolutePath() + fileSepar + dirlog;
		String fullLogFileName=path+fileSepar+logDirName+fileSepar+logFileName;
		File fullLogFile = Funcs.getCanonicalFile(fullLogFileName);

		return collectDataForRpt(fullLogFile, true, type, action, user, ip, comp, start, end);
}

/*    public static void writeLogRecord(String type, String event, String user,
		   							String ip, String computer, boolean isAdmin, String remark) {
	   if (!"sys".equals(user)) {
	       String res = " | " + type + " | " + event + " | " + user + " | " + ip + " | "
						+ computer + " | " + (isAdmin ? "1" : "0") + " | " + remark;
	       reportLog.info(res);
	   }
   }
*/   
   public static boolean prepareUserActons(Reader r, String findType, String findAction, String findUser, String findIP,
		   String findComp, boolean isBalanse, Map map, String start, String end) throws IOException {
       String date,type="",action="",user="",computer="",ip="";
       StreamTokenizer tok=new StreamTokenizer(r);
       tok.resetSyntax();
       tok.eolIsSignificant(true);
       tok.wordChars(33,256);
       boolean newLn = true;
       boolean lt = false;
       boolean gt = false;
       int countDelim = 0;
       while(tok.nextToken()!=StreamTokenizer.TT_EOF){

           if(isBalanse){
               switch(tok.ttype){
                  case StreamTokenizer.TT_EOL:
                       newLn=true;
                	   type=action=user=computer=ip="";
                	   countDelim=0;
                       lt=false;
                       break;
                  case StreamTokenizer.TT_WORD:
                       if(lt) break;
                       if(newLn){
                           newLn=false;
                           date=tok.sval;
                           lt=(date.compareTo(start)<0);
                           gt=(date.compareTo(end)>0);
                       }else if(tok.sval.equals("|")){
                           countDelim++;
/*                           if(countDelim==3){
                               if(!eventMap.containsKey(eventKod))
                                   eventMap.put(eventKod,event);
                           }*/
                       }else{
                           if(countDelim==1){
                               type+=" "+tok.sval;
                               type = type.trim();
                           }else if(countDelim==2){
                               action+=" "+tok.sval;
                               action = action.trim();
                           }else if(countDelim==3){
                               user += tok.sval;
                           }else if(countDelim==4){
                               ip += tok.sval;
                           }else if(countDelim==5){
                               computer+=tok.sval;
                               if ((findType == null || findType.equals(type)) &&
                            		   (findUser == null || findUser.equals(user)) &&
                            		   (findIP == null || ip.contains(findIP)) &&
                            		   (findComp == null || computer.toUpperCase(Constants.OK).contains(findComp.toUpperCase(Constants.OK))) &&
                            		   (findAction == null || findAction.equals(action))) {
	                               
                            	   String key = computer+"|"+user+"|"+action + "|" + ip + "|" + type;
	                               Integer li=(Integer)map.get(key);
	                               if(li==null){
	                                   map.put(key,1);
	                               }else{
	                                   map.put(key,++li);
	                               }
                               }
                           }
                       }
                       break;
               }
               if(gt) break;
           }
       }
       return gt;
   }

   public static void linkLogs(String logDirName, String logFileName) {
	   System.out.println("Linking log files: " + logDirName + fileSepar + logFileName);
	   
	   File rootDir = getRootLogDir();
	   
       String path = rootDir.getAbsolutePath() + fileSepar + dirlog;
		
       String dirLogPath = (logDirName != null && logDirName.length() > 0) 
			? path + fileSepar + logDirName
			: path;

       File dirLog = Funcs.getCanonicalFile(dirLogPath);
       log.info("dirLogPath = " + dirLog.getAbsolutePath());
       
       linkMonthLog(dirLog, logFileName);
	   System.out.println("Linking log files completed!");
   }
   
   private static synchronized File getRootLogDir() {
	   if (rootLogDir == null) {
		   String path = Funcs.normalizeInput(System.getProperty("jboss.home.dir"));
		   if (path == null)
			   path = ".";
		   rootLogDir  = Funcs.getCanonicalFile(path);
	   }
	   return rootLogDir;
   }
}

