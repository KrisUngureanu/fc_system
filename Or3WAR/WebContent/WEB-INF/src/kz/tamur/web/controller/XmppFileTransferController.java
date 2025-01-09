package kz.tamur.web.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kz.tamur.comps.Constants;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.util.Funcs;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class XmppFileTransferController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private static File fileDir;
    private static final Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + XmppFileTransferController.class);

    public XmppFileTransferController() {
        super();
    }

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
        String path = Funcs.normalizeInput(request.getPathInfo());
		if ("/get".equals(path)) {
			String user = Funcs.sanitizeUsername(Funcs.getValidatedParameter(request, "user"));
			String fileName = Funcs.sanitizeFileName(Funcs.getValidatedParameter(request, "file"));
			if (Funcs.isValid(user) && Funcs.isValid(fileName)) {
				File file = getFile(user, fileName, false);
				if (file != null && file.exists()) {
					response.addHeader("Content-Disposition", "attachment; filename*=UTF-8''" + URLEncoder.encode(file.getName(), "UTF-8").replace("+", "%20"));
					response.setContentLength((int) file.length());
					long expiry = new Date(). getTime() + Constants.ONE_DAY * 3;
					response.setDateHeader("Expires", expiry);
					response.setHeader("Cache-Control", "max-age=" + Constants.ONE_DAY * 3);
		    		InputStream is = new FileInputStream(file);
		    		OutputStream os = response.getOutputStream();
		    		copyStream(is, os);
		    		is.close();
		    		os.flush();
		    		os.close();
				}
			}
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = Funcs.normalizeInput(request.getPathInfo());
    	if ("/put".equals(path)) {
    		String user = Funcs.sanitizeUsername(Funcs.getParameter(request, "user"));
    		if (Funcs.isValid(user)) {
	            ServletFileUpload servletFileUpload = new ServletFileUpload(new DiskFileItemFactory());
				try {
					List<FileItem> fileItemsList = servletFileUpload.parseRequest(request);
		            Iterator<FileItem> it = fileItemsList.iterator();
		            while (it.hasNext()) {
		                FileItem fileItem = it.next();
		                if (!fileItem.isFormField()) {
		    				String fileName = Funcs.sanitizeFileName(fileItem.getName());
		    	    		if (Funcs.isValid(fileName)) {
			    	    		File file = getFile(user, fileName, true);
			    	    		if (file != null) {
			    		    		InputStream is = fileItem.getInputStream();
			    		    		OutputStream os = new FileOutputStream(file);
			    		    		copyStream(is, os);
			    		    		os.close();
			    	    		}
		    	    		}
		                }
		            }
				} catch (FileUploadException e) {
					e.printStackTrace();
				}
    		}
    	}
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		String param = config.getInitParameter("xmpp.file.dir");
		param = param != null ? param : "chat_files";
		
		fileDir = Funcs.getCanonicalFile(param);
	}
	
	private File getFile(String user, String fileName, boolean createDirs) {
		File dir = Funcs.getCanonicalFile(fileDir, user);
		if (!createDirs || dir.exists() || dir.mkdirs())
			return Funcs.getCanonicalFile(dir, fileName);
		return null;
	}

	private void copyStream(InputStream is, OutputStream os) throws IOException {
        Funcs.writeStream(is, os, Constants.MAX_DOC_SIZE);
	}
}
