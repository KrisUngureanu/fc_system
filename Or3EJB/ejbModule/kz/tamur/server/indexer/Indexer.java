package kz.tamur.server.indexer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import kz.tamur.comps.Constants;
import kz.tamur.ods.Value;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.util.Funcs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.sandbox.queries.regex.JavaUtilRegexCapabilities;
import org.apache.lucene.sandbox.queries.regex.RegexQuery;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.apache.poi.POITextExtractor;
import org.apache.poi.extractor.ExtractorFactory;
import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.xmlbeans.XmlException;

import com.cifs.or2.client.OrlangTriggerInfo;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnMethod;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.KrnVcsChange;
import com.cifs.or2.server.Session;

public class Indexer {
	private static final Version VERSION = Version.LUCENE_45;
	private static final File INDEX_DIRECTORY = Funcs.getCanonicalFile(System.getProperty("indexDir", "indexes"));
	private static final File TO_INDEX_DIRECTORY = Funcs.getCanonicalFile(INDEX_DIRECTORY.getAbsolutePath() + "/toIndex");

	public static final boolean IS_INDEX_SERVER = Boolean.parseBoolean(System.getProperty("isIndexServer", "false"));

	private static final long maxIndexDirWaitTimeout = 30000;

	private static final Analyzer analyzerStandardCS = new CaseSensitiveStandardAnalyzer(VERSION);
	private static final Analyzer analyzerStandardCI = new CaseInsensitiveStandardAnalyzer(VERSION);
	private static final Analyzer analyzerRussianCS = new RussianAnalyzer(VERSION, true);
	private static final Analyzer analyzerRussianCI = new RussianAnalyzer(VERSION, false);

    private static String FIELD_CONTENT = "content";
    private static String FIELD_UID = "uid";
	
    private static int SLOP = 0;
    private static int SLOP_MAX = Integer.MAX_VALUE;

    private static final Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + Indexer.class.getName());
    
	private static final long TIME_TO_WAIT_STOP_INDEXING = 7000;
	
    private static IndexerPool indexerPool = null;
	private static final int INDEXER_THREADS_COUNT = 40;
	private static final int INDEXER_FILE_GROUP_COUNT = 80;
    private static final Set<String> indexTasks = Collections.synchronizedSortedSet(new TreeSet<String>());
    private static final Set<String> processingFileNames = Collections.synchronizedSortedSet(new TreeSet<String>());
    private static final Map<String, List<Path>> indexCategories = new HashMap<>();
    private static final List<Path> filesToDelete = new ArrayList<>();
    private static final Map<String,Long> indexDirCount = new HashMap<>();
	static enum Language {
		ENGLISH(0),
		KAZAKH(123),
		RUSSIAN(122);
		long id;
		
		Language(long id) {
			this.id = id;
		}
		
		public long getId() {
			return id;
		}		
	}
	
	private static Analyzer getAnalyzerForLanguage(long lang, boolean caseSensitive) {
		switch ((int) lang) {
			case 122:
				return caseSensitive ? analyzerStandardCS : analyzerStandardCI;
//				return caseSensitive ? analyzerRussianCS : analyzerRussianCI;
			case 123:
				return caseSensitive ? analyzerStandardCS : analyzerStandardCI;
			default:
				return caseSensitive ? analyzerStandardCS : analyzerStandardCI;
		}
	}
	
	// Обновление индексов аттрибутов и объектов
	public static void updateIndex(KrnAttribute attr, Session session, String charset) throws IOException, KrnException {
		KrnClass cls = session.getClassById(attr.classId);
		KrnObject[] objs = session.getClassObjects(cls,new long[0], 0);
		for (KrnObject obj : objs) {
			byte[] value = session.getBlob(obj.id, attr.id, 0, 0, 0);
			updateIndex(obj, attr, value, charset, Language.ENGLISH.getId(), 0, session);			
		}
	}
	
	// Обновление индексов аттрибутов и объектов
	public static void updateIndex(KrnObject object, KrnAttribute attr, byte[] value, long langId, long trId, Session session) throws Exception {
	    updateIndex(object, attr, value, null, langId, trId, session);
	}	

	/** Метод, возвращающий полный путь к каталогу записей индексов
	 * @return Возвращает путь к каталогу записей индексов
	 * */
	public static String getIndexDirectory() {
		return INDEX_DIRECTORY.getAbsolutePath();
	}	

	public static void indexAllMSDoc(String charset, long langId, long trId, Session s) throws KrnException {
			String content = null;
			
			KrnClass docCls = s.getClassByName("MSDoc");
			KrnAttribute fileAttr = s.getAttributeByName(docCls, "file");
			KrnAttribute fileNameAttr = s.getAttributeByName(docCls, "filename");

			KrnObject[] objs = s.getClassObjects(docCls, new long[0], trId);
			int i=0;
			for (KrnObject obj : objs) {
				log.debug(i++ + " из " + objs.length);
				// Достаем содержимое
				long[] objIds = {obj.id};
				try {
					SortedSet<Value> vs = s.getValues(objIds, fileNameAttr.id, langId, trId);
					if (vs.size() > 0) {
						String fileName = (String)vs.first().value;
						
						byte[] b = s.getBlob(obj.id, fileAttr.id, 0, langId, trId);
						if (b != null) {
							b = Funcs.normalizeInput(b, charset);
							
							content = SearchUtil.extract(fileName, b);
							if (content == null) {
								content = SearchUtil.replaceSpecialSymbols(b, charset, obj);
							}else{
								content = SearchUtil.replaceSpecialSymbols(content, charset, obj);
							}
							
							addFileToIndex("attr_" + fileAttr.id + "_" + langId + "_" + obj.id + "_" + obj.uid, content);
						}
					}
				} catch (Throwable e) {
					log.error("Ошибка обработки MSDoc; uid: "+(obj!=null?obj.uid:""));
					log.error(e, e);
				}	
			}
	}

	/** Метод для удаления каталога записей индексов
	 * @param file - каталог записей индексов
	 * @return Возвращает true, если каталог был удален, в противном случае false 
	 * */
	public static boolean dropAllIndexes() {
		return dropIndexDirectory(INDEX_DIRECTORY, true);
	}
	
	public static boolean dropIndexDirectory(File file, boolean root) {
	    if (!file.exists())
	        return false;
	    if (file.isDirectory()) {
	    	File[] files = file.listFiles();
	        for (int i = 0 ; i < files.length; i++) {
	        	dropIndexDirectory(files[i], false);
	        }
	        if (!root)
	        	file.delete();
	    } else {
	        file.delete();
	    }
		return true;
	}

	// Обнуление папки с индексами атрибута или методов
	public static void dropIndex(KrnAttribute attr, long langId) {
		File dir = (attr != null)
				? Funcs.getCanonicalFile(INDEX_DIRECTORY, attr.id + "_" + langId)
				: Funcs.getCanonicalFile(INDEX_DIRECTORY, "methods");
				
		if (dir.exists()) {
			dropIndexDirectory(dir, false);
		}
		
		dir = (attr != null)
				? Funcs.getCanonicalFile(INDEX_DIRECTORY, attr.id + "_" + langId + "_cs")
				: Funcs.getCanonicalFile(INDEX_DIRECTORY, "methods_cs");
				
		if (dir.exists()) {
			dropIndexDirectory(dir, false);
		}
	}
	
	// Обнуление папки с индексами триггеров
	public static void dropTriggersIndexFolder() {
		File dir = Funcs.getCanonicalFile(INDEX_DIRECTORY, "triggers");
				
		if (dir.exists()) {
			File[] files = dir.listFiles();
			for (File f : files)
				f.delete();
		}
		
		dir = Funcs.getCanonicalFile(INDEX_DIRECTORY, "triggers_cs");
				
		if (dir.exists()) {
			File[] files = dir.listFiles();
			for (File f : files)
				f.delete();
		}
	}
	
	// Обнуление папки с индексами vcs changes
	public static void dropVcsChangesIndexFolder() {
		File dir = Funcs.getCanonicalFile(INDEX_DIRECTORY, "changes");
				
		if (dir.exists()) {
			File[] files = dir.listFiles();
			for (File f : files)
				f.delete();
		}
		
		dir = Funcs.getCanonicalFile(INDEX_DIRECTORY, "changes_cs");
				
		if (dir.exists()) {
			File[] files = dir.listFiles();
			for (File f : files)
				f.delete();
		}
	}
	// Обновление индексов аттрибутов и объектов
	public static void updateIndex(KrnObject object, KrnAttribute attr, byte[] value, String charset, long langId, long trId, Session s) {
		String fileName ="";
		try {
			String content = null;
			//value = Funcs.normalizeInput(value, charset);

			KrnClass docCls = s.getClassByName("MSDoc");
			if (docCls != null) {
				KrnAttribute fileAttr = s.getAttributeByName(docCls, "file");
				KrnAttribute fileNameAttr = s.getAttributeByName(docCls, "filename");
				KrnClass reportCls = s.getClassByName("ReportPrinter");
				boolean isReport = object.classId == reportCls.id;
				if (isReport || (fileAttr != null && fileNameAttr != null && attr.id == fileAttr.id)) {
					// Достаем содержимое
					if(isReport && attr.name.contains("template")){
						KrnAttribute configAttr = s.getAttributeByName(reportCls, "config");
						String configValue = Funcs.normalizeInput(new String(s.getBlob(object.id, configAttr.id, 0, 0, trId)));
						if(configValue.contains("<editorType>1")){
							fileName = "xxx.xlsx";
						}else if(configValue.contains("<editorType>0")){
							fileName = "xxx.docx";
						}else {
							fileName = "xxx.docx";
						}
						content = SearchUtil.extract(fileName, value);
					} else {
						long[] objIds = {object.id};
						SortedSet<Value> vs = s.getValues(objIds, fileNameAttr.id, langId, trId);
						if (vs.size() > 0) {
							fileName = (String)vs.first().value;
						}
						content = SearchUtil.extract(fileName, value);
					}
				}
			}
			if (content == null) {
				content = SearchUtil.replaceSpecialSymbols(value, charset, object);
			}else{
				content = SearchUtil.replaceSpecialSymbols(content, charset, object);
			}
			
			addFileToIndex("attr_" + attr.id + "_" + langId + "_" + object.id + "_" + object.uid, content);
		} catch (Throwable e) {
			log.error("Ошибка обработки файла:"+fileName+";uid:"+(object!=null?object.uid:""));
		}	
	}
	
	// Обновление индексов триггера
	public static void updateTriggersIndex(OrlangTriggerInfo trigger, Session session) {
		try {
			String content = getContent(trigger.getExpression(), null);
			Object triggerOwner = trigger.getOwnerType() == 0 ? session.getClassById(trigger.getOwnerId()) : session.getAttributeById(trigger.getOwnerId());
			updateTriggersIndex(content, triggerOwner, trigger.getTriggerType());
		} catch (IOException e) {
			log.error(e, e);
		}
	}
	
	// Обновление индексов триггера
	public static void updateTriggersIndex(String content, Object triggerOwner, int triggerType) {
		try {
			content = SearchUtil.replaceSpecialSymbols(content, null, null);
			
        	KrnClass cls = null;
        	KrnAttribute attr = null;
        	int owner = 0;
        	if (triggerOwner instanceof KrnClass) {
        		owner = 0;
        		cls = (KrnClass) triggerOwner;
        	} else {
        		owner = 1;
        		attr = (KrnAttribute) triggerOwner;
        	}
			addFileToIndex("trigger_" + (owner == 0 ? cls.uid : attr.uid) + "_" + owner +  "_" + triggerType, content);
		} catch (IOException e) {
			log.error(e, e);
		}
	}
	
	// Удаление индексов триггера
	public static void removeTriggersIndex(final Object triggerOwner, final int triggerType) {
    	KrnClass cls = null;
    	KrnAttribute attr = null;
    	int owner = 0;
    	if (triggerOwner instanceof KrnClass) {
    		owner = 0;
    		cls = (KrnClass) triggerOwner;
    	} else {
    		owner = 1;
    		attr = (KrnAttribute) triggerOwner;
    	}
    	try {
    		addFileToIndex("xdel-trigger_" + (owner == 0 ? cls.uid : attr.uid) + "_" + owner +  "_" + triggerType, null);
		} catch (IOException e) {
			log.error(e, e);
		}
	}
	
	// Update index on Methods blobs
	public static void updateMethodsIndex(KrnMethod method, byte[] value, String charset) {
		try {
			String content = getContent(value, charset);
			content = SearchUtil.replaceSpecialSymbols(content, charset, null);
			addFileToIndex("method_" + method.uid, content);
		} catch (IOException e) {
			log.error(e, e);
		}
	}
	
	// Update index on VcsChanges blobs
	public static void updateVcsChangesIndex(KrnVcsChange change, byte[] value, String charset) {
		try {
			String content = getContent(value, charset);
			content = SearchUtil.replaceSpecialSymbols(content, charset, null);
			if(change.cvsChangeMethod!=null){
				addFileToIndex("changemethod_" + change.id, content);
			}else if(change.cvsChangeObj!=null){
				addFileToIndex("changeobj_" + change.id, content);
			}
		} catch (IOException e) {
			log.error(e, e);
		}
	}
	// Remove index on Attribute and Object
	public static void removeIndex(KrnObject obj, KrnAttribute attr) {
		for (Language lang : Language.values()) {
			try {
				addFileToIndex("xdel-attr_" + attr.id + "_" + lang.getId() + "_" + obj.id + "_" + obj.uid, null);
			} catch (IOException e) {
				log.error(e, e);
			}
		}	
	}

	// Remove index on Methods blobs
	public static void removeMethodsIndex(final KrnMethod method) {
		try {
			addFileToIndex("xdel-method_" + method.uid, null);
		} catch (IOException e) {
			log.error(e, e);
		}
	}
	
	// Remove index on VcsChanges blobs
	public static void removeVcsChangesIndex(final KrnVcsChange change) {
		try {
			if(change.cvsChangeMethod!=null){
				addFileToIndex("xdel-changemethod_" + change.cvsChangeMethod.uid, null);
			}else if(change.cvsChangeObj!=null){
				addFileToIndex("xdel-changeobj_" + change.cvsChangeObj.uid, null);
			}
		} catch (IOException e) {
			log.error(e, e);
		}
	}
	public static List<KrnObject> find(long attrId, long langId, String pattern, HashMap<String, KrnObject> map)
			throws IOException, ParseException {
		Map<KrnObject, Float> resultMap = new HashMap<KrnObject, Float>();

		File attrDir = Funcs.getCanonicalFile(INDEX_DIRECTORY, attrId + "_" + langId);
		float maxScore = -1;
		float minScore = maxScore;

		List<File> indexDirs = new ArrayList<>();
		File[] files2 = attrDir.listFiles();
		if (files2 != null && files2.length > 0) {
			if (files2[0].isFile())
				indexDirs.add(attrDir);
			else {
				for (File file2 : files2) {
					indexDirs.add(file2);
				}
			}
		}
		
		if (indexDirs != null && indexDirs.size() > 0) {
			for (File indexDir : indexDirs) {
				Directory dir = FSDirectory.open(indexDir);
				DirectoryReader reader = DirectoryReader.open(dir);
				IndexSearcher is = new IndexSearcher(reader);
				QueryParser parser = new QueryParser(VERSION, FIELD_CONTENT, getAnalyzerForLanguage(langId, false));
				pattern = Funcs.normalizeInput(pattern).replaceAll("\\.", " ");	// почему то с точками не ищет
				Query query = parser.parse(pattern);
				TopDocs hits = is.search(query, 1);
				if (hits.totalHits > 1) {
					hits = is.search(query, hits.totalHits);
				}
				for (ScoreDoc scoreDoc : hits.scoreDocs) {
					Document doc = is.doc(scoreDoc.doc);
					KrnObject obj = map.get(doc.get(FIELD_UID));
					if (obj != null) {
						if (maxScore == -1) {
							maxScore = minScore = scoreDoc.score;
						} else {
							if (scoreDoc.score > maxScore)
								maxScore = scoreDoc.score;
							if (scoreDoc.score < minScore)
								minScore = scoreDoc.score;
						}
						resultMap.put(obj, scoreDoc.score);
					}
				}
				reader.close();
				dir.close();
			}
		}
		while (resultMap.size() > 100) {
			minScore += (maxScore - minScore) / ((float) 10.0);
			Iterator<KrnObject> it = resultMap.keySet().iterator();
			while (it.hasNext()) {
				KrnObject obj = it.next();
				float score = resultMap.get(obj);
				if (score < minScore)
					it.remove();
			}
		}
		return new ArrayList<KrnObject>(resultMap.keySet());
	}
	
	// Find on Attribute. To be Improved
	public static List<String> find(long attrId, long langId, String pattern) 
		throws IOException, ParseException {
		List<String> result = new ArrayList<String>();

		File attrDir = Funcs.getCanonicalFile(INDEX_DIRECTORY, attrId + "_" + langId);
		List<File> indexDirs = new ArrayList<>();
		File[] files2 = attrDir.listFiles();
		if (files2 != null && files2.length > 0) {
			if (files2[0].isFile())
				indexDirs.add(attrDir);
			else {
				for (File file2 : files2) {
					indexDirs.add(file2);
				}
			}
		}

		if (indexDirs != null && indexDirs.size() > 0) {
			for (File indexDir : indexDirs) {
				Directory dir = FSDirectory.open(indexDir);
				DirectoryReader reader = DirectoryReader.open(dir);
				IndexSearcher is = new IndexSearcher(reader);
				QueryParser parser = new QueryParser(VERSION, FIELD_CONTENT, getAnalyzerForLanguage(langId, false));
				Query query = parser.parse(Funcs.normalizeInput(pattern));
				TopDocs hits = is.search(query, 10);
				for (ScoreDoc scoreDoc : hits.scoreDocs) {
					Document doc = is.doc(scoreDoc.doc);
					result.add(doc.get(FIELD_UID));
				}
				reader.close();
				dir.close();
			}
		}
		return result;
	}
	
	// Extract text from Offices Word, Excel or plain Text Document. Extract text for XML and HTML to be done.
	private static String getContent(byte[] data, String charSet) throws IOException {
		if (charSet == null) charSet = "UTF-8";
		data = Funcs.normalizeInput(data, charSet);
		InputStream is = new ByteArrayInputStream(data);
		try {
			POITextExtractor extr = ExtractorFactory.createExtractor(is);
			if (extr instanceof ExcelExtractor) {				
				((ExcelExtractor) extr).setFormulasNotResults(true);
				((ExcelExtractor) extr).setIncludeSheetNames(false);
			}
			else if (extr instanceof XSSFExcelExtractor) {				
				((XSSFExcelExtractor) extr).setFormulasNotResults(true);
				((XSSFExcelExtractor) extr).setIncludeSheetNames(false);
			}
			return extr.getText();
			//return Funcs.normalizeInput(extr.getText());
		} catch (XmlException e) {
		} catch (InvalidFormatException e) {
		} catch (OpenXML4JException e) {
		} catch (IllegalArgumentException e) {
		} finally {
			is.close();			
		}
		try {
			return new String(data, charSet);
			//return Funcs.normalizeInput(new String(data, charSet));
		} catch (Exception e) {
			return "";
		}
	}
	
	public static List<Object> search(String pattern, int results, int[] searchProperties, boolean[] searchArea, Session session) throws IOException, ParseException {
		Query query = null;
		String searchingText = pattern; 
		if (searchProperties[1] < 3) { 
			for (int i = 0; i < SearchUtil.SPECIAL_CHARACTERS.length; i++) {
				pattern = pattern.replaceAll(SearchUtil.SPECIAL_CHARACTERS[i][0],SearchUtil.SPECIAL_CHARACTERS[i][1]);
			}
			pattern = pattern.replaceAll("[ ]+", " ").trim();
		}
		String[] words = pattern.split(" ");
		List<Query> queries = new ArrayList<Query>();; 
		File[] files = INDEX_DIRECTORY.listFiles();
		Directory directory = null;
		List<String> queryExpressions = new ArrayList<String>();
		Map<String, String[]> valuesMap = new HashMap<String, String[]>();
		if (searchProperties[1] == 0) {
			// Содержит хотя бы одно слово из выражения
			QueryParser parser = new QueryParser(VERSION, FIELD_CONTENT, getAnalyzerForLanguage(0, searchProperties[1] == 1));
			if (searchProperties[0] == 0) {
				query = parser.parse(QueryParser.escape(pattern.toLowerCase(Constants.OK)));
			} else {
				query = parser.parse(QueryParser.escape(pattern));
			}
			queries.add(query);
			words = searchingText.split(" ");
			for (String word : words) {
				queryExpressions.add(word);
			}
		} else if (searchProperties[1] == 1) {
			// Все выражение с учетом порядка слов
			query = new PhraseQuery();
			for (String word : words) {
				if (searchProperties[0] == 0) {
					((PhraseQuery) query).add(new Term(FIELD_CONTENT, word.toLowerCase(Constants.OK)));
				} else {
					((PhraseQuery) query).add(new Term(FIELD_CONTENT, word));
				}
	        }
			((PhraseQuery) query).setSlop(SLOP);
			queries.add(query);
			queryExpressions.add(searchingText);
		} else if (searchProperties[1] == 2) {
			// Все выражение без учета порядка слов
		    List<Object[]> combinations = new ArrayList<Object[]>();
	        getCombinations(combinations, words, new Stack<String>(), words.length);
	        for (int i = 0; i < combinations.size(); i++) {
	        	query = new PhraseQuery();
				for (Object word : combinations.get(i)) {
					if (searchProperties[0] == 0) {
						((PhraseQuery) query).add(new Term(FIELD_CONTENT, String.valueOf(word).toLowerCase(Constants.OK)));
					} else {
						((PhraseQuery) query).add(new Term(FIELD_CONTENT, String.valueOf(word)));
					}
		        }
				((PhraseQuery) query).setSlop(SLOP_MAX);
				queries.add(query);
	        }
	    	words = searchingText.split(" ");
			for (String word : words) {
				queryExpressions.add(word);
			}
		} else {
			query = new RegexQuery(new Term(FIELD_CONTENT, pattern));
			((RegexQuery) query).setRegexImplementation(new JavaUtilRegexCapabilities()); 
			queries.add(query);
			queryExpressions.add(searchingText);
		}
		
		List<File> indexDirs = new ArrayList<>();
		for (File file : files) {
			if (!file.isDirectory()) {
				continue;
			} else if ("toIndex".equals(file.getName()))
				continue;
			
			if ((searchProperties[0] == 1 && !file.getName().endsWith("_cs")) || (searchProperties[0] == 0 && file.getName().endsWith("_cs"))) {
				continue;
			}
			File[] files2 = file.listFiles();
			if (files2 != null && files2.length > 0) {
				if (files2[0].isFile())
					indexDirs.add(file);
				else {
					for (File file2 : files2) {
						indexDirs.add(file2);
					}
				}
			}
		}
		
		for (File indexDir : indexDirs) {
			String type = indexDir.getName().startsWith("methods") ? "method" 
					: indexDir.getName().startsWith("triggers") ? "trigger" 
							: indexDir.getName().startsWith("changemethods") ? "changemethod" 
									: "class";
			if (!searchArea[0]) {
				if (type.equals("method")) {
					if (!searchArea[1]) {
						continue;
					}
				} else if (type.equals("trigger")) {
					if (!searchArea[2]) {
						continue;
					}
				} else if (type.equals("changemethod")) {
					if (!searchArea[7]) {
						continue;
					}
				} else {
					try {
	 					String indexName = indexDir.getName().contains("_") ? indexDir.getName() : indexDir.getParentFile().getName();
						long attrId = Long.parseLong(indexName.split("_")[0]);
						KrnAttribute attr = session.getAttributeById(attrId);
						long clsId = attr.classId;
						if (clsId == session.getDatabase().CLS_PROCESS_DEF.id) {	// Процессы
							if (!searchArea[3]) {
								continue;
							}
						} else if (clsId == session.getDatabase().CLS_UI.id) {	// Интерфейсы
							if (!searchArea[4]) {
								continue;
							}
						} else if (clsId == session.getDatabase().CLS_FILTER.id) {	// Фильтры 
							if (!searchArea[5]) {
								continue;
							}
						} else if (clsId == session.getDatabase().CLS_REPORT.id) {	// Отчеты
							if (!searchArea[6]) {
								continue;
							}
						} else {
							continue;
						}
					} catch(Exception e) {
						log.error(e, e);
						continue;
					}
				}
			}
			directory = FSDirectory.open(indexDir);
			IndexReader ir = DirectoryReader.open(directory);
			IndexSearcher is = new IndexSearcher(ir);
			for (int i = 0; i < queries.size(); i++) {
				query = queries.get(i);
				if (query == null) {
					continue;
				}
				TopDocs hits = is.search(query, results);
				Explanation explanation;
				for (ScoreDoc scoreDoc : hits.scoreDocs) {
					explanation = is.explain(query, scoreDoc.doc);
	 				Document doc = is.doc(scoreDoc.doc);
	 				
	 				String attrId;
	 				String langId;
	 				if (type.equals("method")) {
	 					attrId = "Method";
	 					langId = "0";
	 				} else if (type.equals("trigger")) {
	 					attrId = "Trigger";
	 					langId = "0";
	 				} else if (type.equals("changemethod")) {
	 					attrId = "Change";
	 					langId = "0";
	 				} else {
	 					String indexName = indexDir.getName().contains("_") ? indexDir.getName() : indexDir.getParentFile().getName();
	 					attrId = indexName.split("_")[0];
	 					langId = indexName.split("_")[1];
	 				}
					String[] value = { doc.get(FIELD_UID), attrId, langId, String.format("%.8f", explanation.getValue()), type };
					String key = doc.get(FIELD_UID) + "_" + attrId + "_" + langId;
					String[] oldValue = valuesMap.get(key);
					if (oldValue == null || oldValue[3].compareTo(value[3]) < 0) {
						valuesMap.put(key, value);
					}
				}
			}
			ir.close();
			directory.close();
		}
		List<Object> array = new ArrayList<Object>();
		array.add(new ArrayList<String[]>(valuesMap.values()));
		array.add(queryExpressions);
		return array;
	}
	
	// Генерирует комбинации слов
	public static void getCombinations(List<Object[]> combinations, String[] words, Stack<String> stack, int index) {
        if (index == 0) {
        	combinations.add(stack.toArray());
            return;
        }
        for (int i = 0; i < words.length; i++) if (words[i] != "") {
        	stack.push(words[i]);
            words[i] = "";
            getCombinations(combinations, words, stack, index - 1);
            words[i] = stack.pop();
        }
    }
	
	public static String getHighlightedFragments(String pattern, byte[] content, long langId) throws ParseException, IOException {
		Analyzer analyzer = getAnalyzerForLanguage(langId, false);
		QueryParser parser = new QueryParser(VERSION, FIELD_CONTENT, analyzer);
		try {
			Query query = parser.parse(pattern);
			String text = getContent(content, null); 
			return HighlighterUtil.getFragmentsWithHighlightedTerms(analyzer, query, FIELD_CONTENT, text, 50);
		} catch (InvalidTokenOffsetsException e) {
			log.error(e, e);
		}
		return null;
	}
	
	// Using lucene search in plain string
	public static boolean stringSearch(String content, String pattern, long langId) throws IOException, ParseException {
		boolean result = false;
		Directory dir = new RAMDirectory();
		Analyzer analyzer = getAnalyzerForLanguage(langId, true);
		IndexWriterConfig cfg = new IndexWriterConfig(VERSION, analyzer);
		IndexWriter writer = new IndexWriter(dir, cfg);
		try {
			Document doc = new Document();
			doc.add(new TextField(FIELD_CONTENT, content, Field.Store.NO));
			writer.addDocument(doc);
		} finally {
			writer.close();
		}
		
		IndexReader ir = DirectoryReader.open(dir);
		IndexSearcher is = new IndexSearcher(ir);			
		QueryParser parser = new QueryParser(VERSION, FIELD_CONTENT, getAnalyzerForLanguage(langId, true));
		Query query = parser.parse(pattern);	
		TopDocs hits = is.search(query, 10);
		if (hits.scoreDocs.length > 0) result = true;
		ir.close();
		dir.close();
		return result;
	}
	
	private static Timer checkFilesToIndexTask = null;
	private static boolean indexingStopped = false;
	private static boolean directoryIsRunning = false;
	
	public static void stopIndexing() {
		log.info("Stoping Indexing....");
		indexingStopped = true;
		if (checkFilesToIndexTask != null) 
			checkFilesToIndexTask.cancel();
		if (indexerPool != null)
			indexerPool.shutdown();
		try {
			Thread.sleep(TIME_TO_WAIT_STOP_INDEXING);
		} catch (Exception e) {}
		processingFileNames.clear();
	}
	
	public static void startIndexing() {
		log.info("Starting Indexing....");
		indexDirCount.clear();
		if (IS_INDEX_SERVER) {
			indexerPool = new IndexerPool(INDEXER_THREADS_COUNT);
			TO_INDEX_DIRECTORY.mkdirs();
			indexingStopped = false;
			directoryIsRunning = false;
			processingFileNames.clear();
			
			checkFilesToIndexTask = new Timer();
			checkFilesToIndexTask.schedule(new TimerTask() {
				@Override
				public void run() {
					if (!directoryIsRunning) {
						directoryIsRunning = true;
						try {
							indexCategories.clear();
							
							synchronized (filesToDelete) {
								for (Path file : filesToDelete) {
									if (file.toFile().delete()) {
										String name = file.toFile().getName();
										int dotPos = name.indexOf(".v");
										if (dotPos > -1)
											name = name.substring(0, dotPos);
										processingFileNames.remove(name);
									}
								}
								filesToDelete.clear();
							}
							
							try (DirectoryStream<Path> serversStream = Files.newDirectoryStream(Paths.get(TO_INDEX_DIRECTORY.getAbsolutePath()))) {
					            for (Path serverToIndexPath : serversStream) {
					            	if (indexingStopped) return;
									try (DirectoryStream<Path> toIndexDirStream = Files.newDirectoryStream(serverToIndexPath)) {
							            for (Path dirToIndexPath : toIndexDirStream) {
											try (DirectoryStream<Path> toIndexFilesStream = Files.newDirectoryStream(dirToIndexPath)) {
									            for (Path fileToIndexPath : toIndexFilesStream) {
									            	if (indexingStopped) return;
									            	categorizeFile(fileToIndexPath);
									            }
											}
							            }
							        }
					            }
					        } catch (IOException ioe) {
					        	log.error(ioe, ioe);
					        }
							
							for (List<Path> files : indexCategories.values()) {
								indexFiles(files);
							}
						} catch (Throwable e) {
							log.error(e, e);
				        } finally {
							directoryIsRunning = false;
						}
					}
				}
			}, 1000, TIME_TO_WAIT_STOP_INDEXING);
		} else
			log.warn("Server is not for Indexing!");
	}
	
	private static void addFileToIndex(String fileName, String content) throws IOException {
		if (Funcs.isValid(fileName)) {
			String dirName="/CommonDir";
			//Разбивка по папкам всех типов документов, чтобы не сваливать все в одну папку
			int index=fileName.indexOf("_");
			if(index>0){
				dirName="/"+fileName.substring(0,index);
				if("/attr".equals(dirName)){
					index=fileName.indexOf("_",fileName.indexOf("_",index+1)+1);
					dirName="/"+fileName.substring(0,index);
				}
			}
			String child_str= UserSession.SERVER_ID != null ? UserSession.SERVER_ID+dirName : "MyDir"+dirName;
			String[] children=child_str.split("/");
			File myToIndexDir = Funcs.getCanonicalFile(TO_INDEX_DIRECTORY);
			for(String child:children){
				myToIndexDir = Funcs.getCanonicalFile(myToIndexDir,child);
			}
			myToIndexDir.mkdirs();
			File f = Funcs.getCanonicalFile(myToIndexDir, fileName);
			int i = 0;
			while (f.exists()) {
				f = Funcs.getCanonicalFile(myToIndexDir, fileName + ".v" + ++i);
			}
			if (content != null)
				Funcs.write(content.getBytes("UTF-8"), f);
			else
				f.createNewFile();
		}
	}
	
	private static void categorizeFile(Path path) {
		String name = path.toFile().getName();
		try {
			int dotPos = name.indexOf(".v");
			if (dotPos > -1)
				name = name.substring(0, dotPos);

			if (!processingFileNames.contains(name)) {
				processingFileNames.add(name);
				String[] names = name.split("_");
					
				if (names.length > 1) {
					String indexDirName = null;
					
					switch (names[0]) {
					case "attr":
						indexDirName = names[1] + "_" + names[2]; // attrId + langId
						break;
					case "method":
						indexDirName = "methods";
						break;
					case "trigger":
						indexDirName = "triggers";
						break;
					case "change":
						indexDirName = "changes";
						break;
					case "xdel-attr":
						indexDirName = names[1] + "_" + names[2]; // attrId + langId
						break;
					case "xdel-method":
						indexDirName = "methods";
						break;
					case "xdel-trigger":
						indexDirName = "triggers";
						break;
					case "xdel-change":
						indexDirName = "changes";
						break;
					}
					

					List<Path> list = indexCategories.get(indexDirName);
					if (list == null) {
						list = new ArrayList<>();
						indexCategories.put(indexDirName, list);
					}
					if (list.size() < INDEXER_FILE_GROUP_COUNT)
						list.add(path);
					else
						processingFileNames.remove(name);
					
				} else {
					processingFileNames.remove(name);
					throw new KrnException(0, "Недопустимый файл в папке для индексирования - " + name);
				}
			}
		} catch (KrnException e) {
			processingFileNames.remove(name);
			log.error("File indexing error: " + path.toFile().getAbsolutePath());
			log.error(e.getMessage());
		} catch (Throwable e) {
			processingFileNames.remove(name);
			log.error("File indexing error: " + path.toFile().getAbsolutePath());
			log.error(e, e);
		}
	}

	private static void indexFiles(List<Path> paths) {
		String indexDirName = null;
		String indexDirNameCS = null;
		List<Path> toIndexPaths = new ArrayList<>();
		List<String> fieldUIDs = new ArrayList<>();
		List<Boolean> dels = new ArrayList<>();
		long langId = 0;
		List<byte[]> bufs = new ArrayList<>();
		
		for (Path path : paths) {
			String name = path.toFile().getName();
			try {
				int dotPos = name.indexOf(".v");
				if (dotPos > -1)
					name = name.substring(0, dotPos);
	
				byte[] buf = readFile(path);
				if (buf != null) {
					String[] names = name.split("_");
					
					switch (names[0]) {
					case "attr":
						if (indexDirName == null) {
							indexDirName = names[1] + "_" + names[2]; // attrId + langId
							indexDirNameCS = names[1] + "_" + names[2] + "_cs"; // attrId + langId
							langId = Long.parseLong(names[2]);
						}
						fieldUIDs.add(names[4]);
						dels.add(false);
						break;
					case "method":
						if (indexDirName == null) {
							indexDirName = "methods";
							indexDirNameCS = "methods_cs";
						}
						fieldUIDs.add(names[1]);
						dels.add(false);
						break;
					case "trigger":
						if (indexDirName == null) {
							indexDirName = "triggers";
							indexDirNameCS = "triggers_cs";
						}
						fieldUIDs.add(names[1] + "_" + names[2] + "_" + names[3]); //attr or cls id, owner (0,1), triggerType
						dels.add(false);
						break;
					case "changemethod":
						if (indexDirName == null) {
							indexDirName = "changemethods";
							indexDirNameCS = "changemethods_cs";
						}
						fieldUIDs.add(names[1]);
						dels.add(false);
						break;
					case "xdel-attr":
						if (indexDirName == null) {
							indexDirName = names[1] + "_" + names[2]; // attrId + langId
							indexDirNameCS = names[1] + "_" + names[2] + "_cs"; // attrId + langId
							langId = Long.parseLong(names[2]);
						}
						fieldUIDs.add(names[4]);
						dels.add(true);
						break;
					case "xdel-method":
						if (indexDirName == null) {
							indexDirName = "methods";
							indexDirNameCS = "methods_cs";
						}
						fieldUIDs.add(names[1]);
						dels.add(true);
						break;
					case "xdel-trigger":
						if (indexDirName == null) {
							indexDirName = "triggers";
							indexDirNameCS = "triggers_cs";
						}
						fieldUIDs.add(names[1] + "_" + names[2] + "_" + names[3]); //attr or cls id, owner (0,1), triggerType
						dels.add(true);
						break;
					case "xdel-change":
						if (indexDirName == null) {
							indexDirName = "changes";
							indexDirNameCS = "changes_cs";
						}
						fieldUIDs.add(names[1] + "_" + names[2] + "_" + names[3]); //attr or cls id, owner (0,1), triggerType
						dels.add(true);
						break;
					}
					
					bufs.add(buf);
					toIndexPaths.add(path);
				} else {
					processingFileNames.remove(name);
					paths.remove(path);
				}
			} catch (Throwable e) {
				processingFileNames.remove(name);
				paths.remove(path);
				log.error("File index preparing error: " + path.toFile().getAbsolutePath());
				log.error(e, e);
			}
		}
		
		if (paths.size() > 0) {
			if (!indexTasks.contains(indexDirName)) {
				indexTasks.add(indexDirName);
				log.info("Files added to pool for indexing '"+indexDirName+"': " + paths.size());
				indexerPool.execute(new IndexTask(toIndexPaths, indexDirName, indexDirNameCS, fieldUIDs, langId, dels, bufs));
			} else {
				for (Path path : paths) {
					String name = path.toFile().getName();
					int dotPos = name.indexOf(".v");
					if (dotPos > -1)
						name = name.substring(0, dotPos);
					processingFileNames.remove(name);
				}
			}
		}
	}
	
	private static boolean indexFilesExclusive(List<Path> paths, String indexDirName, String indexDirNameCS, 
			List<String> fieldUIDs, long langId, List<Boolean> dels, List<byte[]> bufs) {
    	IndexWriter indexWriter = null;
    	Directory directory = null;
    	boolean ok = false;
    	
		try {
			IndexWriterConfig cfg = new IndexWriterConfig(VERSION, getAnalyzerForLanguage(langId, false));
			cfg.setWriteLockTimeout(maxIndexDirWaitTimeout);
			File dir = Funcs.getCanonicalFile(INDEX_DIRECTORY, indexDirName);
			dir.mkdirs();
			
			directory = FSDirectory.open(dir);
			indexWriter = new IndexWriter(directory, cfg);
			if(!indexDirCount.containsKey(indexDirName))
				indexDirCount.put(indexDirName, (long)0);
			long indexDir=indexDirCount.get(indexDirName);
			log.info("Files indexing starting for '"+indexDirName+"': " + indexDir);

			for (int i=0; i<paths.size(); i++) {
	    		Path path = paths.get(i);
//				log.info(i + ": " + path.toFile().getAbsolutePath());
		    	String name = path.toFile().getName();
				int dotPos = name.indexOf(".v");
				if (dotPos > -1)
					name = name.substring(0, dotPos);
	
				if (dels.get(i)) {
	    			indexWriter.deleteDocuments(new Term(FIELD_UID, fieldUIDs.get(i)));
				} else {
					Document document = new Document();
					document.add(new StringField(FIELD_UID, fieldUIDs.get(i), Field.Store.YES));
					document.add(new TextField(FIELD_CONTENT, new String(bufs.get(i), "UTF-8"), Field.Store.YES));
					indexWriter.updateDocument(new Term(FIELD_UID, fieldUIDs.get(i)), document);
				}
				indexDir++;
			}
			indexWriter.close();
			directory.close();

			cfg = new IndexWriterConfig(VERSION, getAnalyzerForLanguage(langId, true));
			cfg.setWriteLockTimeout(maxIndexDirWaitTimeout);
			
			dir = Funcs.getCanonicalFile(INDEX_DIRECTORY, indexDirNameCS);
			dir.mkdirs();
			
			directory = FSDirectory.open(dir);
			indexWriter = new IndexWriter(directory, cfg);

			for (int i=0; i<paths.size(); i++) {
	    		Path path = paths.get(i);
		    	String name = path.toFile().getName();
				int dotPos = name.indexOf(".v");
				if (dotPos > -1)
					name = name.substring(0, dotPos);
	
				if (dels.get(i)) {
	    			indexWriter.deleteDocuments(new Term(FIELD_UID, fieldUIDs.get(i)));
				} else {
					Document document = new Document();
					document.add(new StringField(FIELD_UID, fieldUIDs.get(i), Field.Store.YES));
					document.add(new TextField(FIELD_CONTENT, new String(bufs.get(i), "UTF-8"), Field.Store.YES));
					indexWriter.updateDocument(new Term(FIELD_UID, fieldUIDs.get(i)), document);
				}			
			}
			indexWriter.close();
			directory.close();
			log.info("Files indexing finished for '"+indexDirName+"': " + indexDir);
			indexDirCount.put(indexDirName, indexDir);
			
			synchronized (filesToDelete) {
				for (Path path : paths) {
					filesToDelete.add(path);
				}
			}
			ok = true;
			
		} catch (CorruptIndexException e) {
			log.error("Files indexing error, one of them: " + paths.get(0).toFile().getAbsolutePath());
			log.error(e, e);
		} catch (LockObtainFailedException e) {
			log.error("Files indexing error, one of them: " + paths.get(0).toFile().getAbsolutePath());
			log.error(e, e);
		} catch (IOException e) {
			log.error("Files indexing error, one of them: " + paths.get(0).toFile().getAbsolutePath());
			log.error(e, e);
		} catch (Throwable e) {
			log.error("Files indexing error, one of them: " + paths.get(0).toFile().getAbsolutePath());
			log.error(e, e);
			return true;
		} finally {
			try {
				if (!ok) {
					for (Path path : paths) {
				    	String name = path.toFile().getName();
						int dotPos = name.indexOf(".v");
						if (dotPos > -1)
							name = name.substring(0, dotPos);
	
						processingFileNames.remove(name);
					}
				}
				indexTasks.remove(indexDirName);
				Exception e = null; 
				try {
					if (indexWriter != null) indexWriter.close();
				} catch (Exception ex) {
					e = ex;
				}
				if (directory != null) directory.close();
				if (e != null)
					throw e;
			} catch (Throwable ex) {
				log.error(ex, ex);
				return false;
			}
		}
		return false;
	}
	
	private static byte[] readFile(Path path) {
		try {
			byte[] res = Funcs.read(path.toFile());
			return res;
		} catch (IOException e) {
		}
		return null;
	}

	private static class IndexTask implements Runnable {
		List<Path> paths;
		String indexDirName;
		String indexDirNameCS;
		List<String> fieldUIDs;
		long langId;
		List<Boolean> dels;
		List<byte[]> bufs;
		
		public IndexTask(List<Path> paths, String indexDirName, String indexDirNameCS, List<String> fieldUIDs, long langId,
				List<Boolean> dels, List<byte[]> bufs) {
			super();
			this.paths = paths;
			this.indexDirName = indexDirName;
			this.indexDirNameCS = indexDirNameCS;
			this.fieldUIDs = fieldUIDs;
			this.langId = langId;
			this.dels = dels;
			this.bufs = bufs;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Indexer.indexFilesExclusive(paths, indexDirName, indexDirNameCS, fieldUIDs, langId, dels, bufs);
		}
		
	}
}