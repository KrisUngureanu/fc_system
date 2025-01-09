package kz.tamur.guidesigner;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;

public class DesignerWebPreviewFrame {

	private Kernel kernel = Kernel.instance();
	
	public DesignerWebPreviewFrame(KrnObject krnUIObj, String title, Long langId, int project) throws KrnException, IOException {
		File folder = new File("html");
		if(!folder.exists())
			folder.mkdir();
		File indexFile = new File(folder + "\\index"+krnUIObj.id+".html");
		if(!indexFile.exists())
			indexFile.createNewFile();
		File js = new File(folder + "\\js");
		if(!js.exists())
			js.mkdir();
		File css = new File(folder + "\\css");
		if(!css.exists())
			css.mkdir();
		File easyuicss = new File(css + "\\easyui.css");
		File bootstrapcss = new File(css + "\\bootstrap.min.css");
		File uiv1css = new File(css + "\\ui" + (project == 0 ? "v1" : "v4") + ".css");
		File appjs = new File(js + "\\app.js");
		File easyuijs = new File(js + "\\jquery.easyui.min.js");
		File jqueryjs = new File(js + "\\jquery.min.js");
		if(!appjs.exists())
			appjs.createNewFile();
		if(!easyuijs.exists())
			easyuijs.createNewFile();
		if(!jqueryjs.exists())
			jqueryjs.createNewFile();
		if(!easyuicss.exists())
			easyuicss.createNewFile();
		if(!bootstrapcss.exists())
			bootstrapcss.createNewFile();
		if(!uiv1css.exists())
			uiv1css.createNewFile();
		if(appjs.length() == 0) {
			copyFile(appjs, "app.js");
		}
		if(easyuijs.length() == 0) {
			copyFile(easyuijs, "jquery.easyui.min.js");
		}
		if(jqueryjs.length() == 0) {
			copyFile(jqueryjs, "jquery.min.js");
		}
		if(easyuicss.length() == 0) {
			copyFile(easyuicss, "easyui.css");
		}
		if(bootstrapcss.length() == 0) {
			copyFile(bootstrapcss, "bootstrap.min.css");
		}
		if(uiv1css.length() == 0) {
			copyFile(uiv1css, "ui" + (project == 0 ? "v1" : "v4") + ".css");
		}
		
		Desktop desktop = Desktop.getDesktop();
		
		byte[] webConfigData = kernel.getBlob(krnUIObj, "webConfig", 0, langId, 0);
		if(webConfigData != null && webConfigData.length > 0){
			BufferedWriter br = new BufferedWriter(new FileWriter(indexFile));
			br.write("<!DOCTYPE html><html><head><meta charset='utf-8'><meta http-equiv='X-UA-Compatible' content='IE=edge,chrome=1,firefox=1'>"+
						"<meta name='viewport' content='width=device-width'>"+
						"<meta http-equiv='Content-Type' content='text/html; charset=utf-8' />"+
						"<title>"+title+"</title>"+
						"<link rel='stylesheet' type='text/css' href='css/bootstrap.min.css'>"+
						"<link rel='stylesheet' type='text/css' href='css/easyui.css'>"+
						"<link rel='stylesheet' type='text/css' href='css/ui" + (project == 0 ? "v1" : "v4") + ".css'>"+
						"<script type='text/javascript' src='js/jquery.min.js'></script>"+
						"<script type='text/javascript' src='js/app" + (project == 0 ? "" : ".gis") + ".js'></script>"+
						"<script type='text/javascript' src='js/jquery.easyui.min.js'></script></head><body>");
			br.write(new String(webConfigData));
			br.write("</body></html>");
			br.close();
			desktop.browse(indexFile.toURI());
		}	    
	}
	
	private void copyFile(File file, String name) throws IOException {
		InputStream is = DesignerWebPreviewFrame.class.getResourceAsStream("filesPreviewWeb/"+name);
		OutputStream os = new FileOutputStream(file);
	    byte[] buffer = new byte[1024];
	    int length;
	    while ((length = is.read(buffer)) > 0) {
	    	os.write(buffer, 0, length);
	    }
	    os.close();
	}
}
