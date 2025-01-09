package kz.tamur.web.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.esapi.ESAPI;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;

import kz.tamur.comps.Constants;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.rt.Utils;
import kz.tamur.util.Funcs;

public class TemplateHelper {
	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + TemplateHelper.class);
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	public static String load(String guid, String ifcUid, long langId, Kernel krn, String dir, boolean cache) throws IOException {

		try {
			if (krn != null) {
				KrnObject ifcObj = krn.getObjectByUid(ifcUid, 0);
				langId = langId == 0 ? krn.getUser().getIfcLang().id : langId;
				
				Path flt = null;
				
				if (cache) {
					Path directory = Paths.get(dir);
					flt = Funcs.getChild(directory, ifcUid + "-" + langId + ".html");
				}
	
				byte[] webConfigData = getHtmlTemplate(krn, ifcObj, langId, flt);
				
				String ifc = new String(webConfigData, "UTF-8");
				ifc = Funcs.validate(ifc);
				ifc = ifc.replace("/main?", "/main?guid=" + guid + "&");
//				ifc = ESAPI.encoder().encodeForHTML(ifc);

				return ifc;
			}
		} catch (KrnException e) {
			e.printStackTrace();
		}
		return "error";
	}

	/**
	 * get html template for UI
	 * @param flt 
	 * @param langId 
	 * @param krnUIObj 
	 * @param krn 
	 * @return String
	 * @throws KrnException
	 * @throws IOException
	 */
	private static byte[] getHtmlTemplate(Kernel krn, KrnObject krnUIObj, long langId, Path flt) throws KrnException, IOException{
		if(flt == null){
			return getHtmlTemplateFromDB(krn, krnUIObj, langId, flt);
		} else {
			return getHtmlTemplateFromFile(flt);
		}
	}

	/**
	 * Возвращает html шаблон интерфейса из атрибута webConfig класса UI с БД
	 * @return String
	 * @throws KrnException
	 * @throws IOException
	 */
	private static byte[] getHtmlTemplateFromDB(Kernel krn, KrnObject krnUIObj, long langId, Path flt) throws KrnException, IOException{
		byte[] webConfigData = krn.getBlob(krnUIObj, "webConfig", 0, langId, 0);
		if(webConfigData != null){
			if(webConfigData.length > 0){
				if (flt != null) {
					try (OutputStream os = Files.newOutputStream(flt)) {
						os.write(webConfigData);
					}
				}
				return webConfigData;
			} else
				return "Интерфейс не сгенерирован".getBytes("UTF-8");
		} else
			return "Интерфейс не сгенерирован".getBytes("UTF-8");
	}

	/**
	 * Возвращает html шаблон интерфейса из файла
	 * @return String
	 * @throws IOException
	 * @throws KrnException
	 */
	private static byte[] getHtmlTemplateFromFile(Path flt) throws IOException {
		InputStream is = null; 
		try {
			is = Files.newInputStream(flt);
            return Funcs.readStream(is, Constants.MAX_DOC_SIZE);
		} catch (Exception e) {
			Files.delete(flt);
			log.error(e, e);
			return "Файл интерфейса поврежден".getBytes("UTF-8");
		} finally {
			Utils.closeQuietly(is);
		}
	}
}
