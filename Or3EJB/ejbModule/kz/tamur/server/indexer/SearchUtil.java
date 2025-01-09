package kz.tamur.server.indexer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.cifs.or2.kernel.KrnObject;

import kz.tamur.comps.Constants;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.util.Funcs;

public class SearchUtil {
	
	/** Специальные символы, игнорируемые StandardAnalyzer при построении запроса для поиска */
//	public static final String[] SPECIAL_CHARACTERS = new String[] {"+", "-", "&&", "||", "!", "(", ")", "{", "}", "[", "]", "^", "\"", "~", "*", "?", ":", "\\"};
//	public static final String[] SPECIAL_CHARACTERS = new String[] {"\\+", "-", "&&", "\\|", "!", "\\(", "\\)", "\\{", "\\}", "\\[", "\\]", "\\^", "\"", "~", "\\*", "\\?", ":", "\\\\"};
	public static final String[][] SPECIAL_CHARACTERS = new String[][] {
		{"::"," "}, {":"," "}, {"%"," "}, {"\\$"," "}, {"#"," "}, {"\\."," "},
		{"-"," "}, {"\'"," "}, {"\""," "}, {"\\("," "}, {"\\A\\."," "}, {"\\s\\."," "}, {"\\)\\."," "}, {"\\)"," "}, 
		{"\\{"," "}, {"\\}"," "}, {"\\="," "}, {"\\*"," "}, {"\\/"," "}, {"\\?"," "}, {"\\,"," "}, {"\\!"," "}
	};
	/** Кодировки используемые при генерировании текста из массива байтов */
	private static final List<String> CHARSETS = new ArrayList<String>(Arrays.asList("UTF-8", "ASCII"));
	/** Расположение файла с информацией о последнем процессе индексации */
	private static final String FILE = Indexer.getIndexDirectory() + "/LastIndexing.info";

    private static final Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + SearchUtil.class.getName());

	public static String convertText(String inputText) {
		String outputText = null;
		return outputText;
	}

	/** Метод для генерирование текста из массива байтов с учетом кодировки
	 * @param value - массив байтов
	 * @param charset - кодировка текста
	 * @param object - объект класса
	 * @return Возвращает текст преобразованный из массива байтов
	 * */
	private static String textGenerator(byte[] value, String charset, KrnObject object) {
		try {
			if (charset != null && SearchUtil.CHARSETS.contains(charset)) {
				return new String(value, charset);
			} else {
				return new String(value, "UTF-8");
			}
		} catch (UnsupportedEncodingException e) {
			log.warn("Указанная кодировка при генерировании текста из массива байтов не поддерживается." + (object == null ? "" : " UID объекта: " + object.uid + "."));
			return "";			
		}
	}
	
	/** Метод, заменяющий все специальные символы знаком "_"
	 *@param inputText - текст, в котором будет производиться замена
	 *@return Возвращает текст, в котором все специальные символы заменены на знак "_"
	 */
	public static String replaceSpecialSymbols(String value, String charset, KrnObject object) {
		//value = Funcs.normalizeInput(value);
		for (int i = 0; i < SPECIAL_CHARACTERS.length; i++) {
			value = value.replaceAll(SPECIAL_CHARACTERS[i][0],SPECIAL_CHARACTERS[i][1]);
		}
		return value;
	}

	public static String replaceSpecialSymbols(byte[] value, String charset, KrnObject object) {
		String outputText = textGenerator(value, charset, object);
		return replaceSpecialSymbols(outputText, charset, object);
	}
	
	/** Метод для считывания информации о последнем процессе индексации
	 * @return Возвращает текст, содержащий информацию об индексации
	 * */
	public static String getLastIndexingInfo() {
		String lastIndexingInfo = "Информация отсутствует.";
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(FILE);
			lastIndexingInfo = IOUtils.toString(inputStream);
			inputStream.close();
		} catch (FileNotFoundException e) {
			log.error("Файл, содержащий информацию о последней индексации, не найден.");
		} catch (IOException e) {
			log.error("Ошибка при считывании файла.");
		}
		return lastIndexingInfo;
	}
	
	/** Метод для записи информации о последнем процессе индексации
	 * @param lastInfo - информация об индексации
	 * */
	public static void setLastIndexingInfo(String lastIndexingInfo) {
		try {
			FileUtils.writeStringToFile(new File(FILE), lastIndexingInfo);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String extract(String fileName, byte[] data) throws IOException {
		String res = null;
		fileName = Funcs.sanitizeFileName(fileName);
		if (fileName.matches(".+") && data != null && data.length < Constants.MAX_BLOB_SIZE) {				
			int dotPos = fileName.lastIndexOf('.');
			if (dotPos != -1) {
				String ext = fileName.substring(dotPos + 1);
				data = Funcs.normalizeInput(data, "UTF-8");
				if ("doc".equals(ext) || "docx".equals(ext)) {
					String temp = new String(data, "UTF-8");
              	  	if (temp.contains("docProps/core.xml")) {
    					XWPFDocument doc = new XWPFDocument(new ByteArrayInputStream(data));
    					XWPFWordExtractor extractor = new XWPFWordExtractor(doc);
    					res = extractor.getText();
    					extractor.close();
              	  	} else {
    					WordExtractor extractor = new WordExtractor(new ByteArrayInputStream(data));
    					res = extractor.getText();
    					extractor.close();
              	  	}
				} else if ("xls".equals(ext) || "xlsx".equals(ext)) {
					String temp = new String(data, "UTF-8");
              	  	if (temp.contains("_rels/.rels")) {
    					XSSFWorkbook book = new XSSFWorkbook(new ByteArrayInputStream(data));
    					XSSFExcelExtractor extractor = new XSSFExcelExtractor(book);
    					res = extractor.getText();
    					extractor.close();
              	  	} else {
    					POIFSFileSystem fs = new POIFSFileSystem(new ByteArrayInputStream(data));
    					ExcelExtractor extractor = new ExcelExtractor(fs);
    					res = extractor.getText();
    					extractor.close();
              	  	}
				}
			}
		}
		return res;
		//return Funcs.normalizeInput(res);
	}
}
