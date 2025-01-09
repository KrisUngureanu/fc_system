package kz.tamur.fc.servlet;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


import javassist.tools.rmi.RemoteException;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.server.Context;
import com.cifs.or2.server.Session;
import com.cifs.or2.server.orlang.SrvOrLang;

import kz.tamur.or3ee.server.kit.SrvUtils;


public class FileServlet extends HttpServlet {
 
	public void doGet(HttpServletRequest request,
	                    HttpServletResponse response)
	      throws ServletException, IOException {
		
	      String fileId = request.getParameter("fileId");
              String codeBank = request.getParameter("codeBank");
              String iin = request.getParameter("iin");
              OutputStream os = null;
              InputStream is = null;
              if(fileId!=null && !"".equals(fileId) && codeBank!=null && !"".equals(codeBank)) {
                  String fileDir=getFileDir();
                  File dir=new File(fileDir+File.separator+codeBank);
                  dir.mkdirs();
                  File file=new File(dir,fileId);
                  if(file.exists())
                  try {
                      os = response.getOutputStream();
                      is = new BufferedInputStream(new FileInputStream(file));
                      if (is != null) {
                          byte[] buf = new byte[8 * 1024];
                          int n;
                          while ((n = is.read(buf)) != -1) {
                              os.write(buf, 0, n);
                          }
                      }
                      } catch (IOException ioe) {
                          throw new ServletException(ioe.getMessage());
                      } finally {
                          if (is != null)
                              is.close();
                          if (os != null)
                              os.close();
                      }
                  }else if(iin!=null) {
                      System.out.println("request iin="+iin);
                      String res=checkIIN(iin);
                      System.out.println("response ="+res);
                      response.setContentType("text/plain; charset=UTF-8");
                      response.getWriter().println(res);
                  }
              
              }
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	        String codeBank=request.getParameter("codeBank");
                ServletOutputStream ros=response.getOutputStream();
	        if(codeBank!=null && !"".equals(codeBank)) {
    	        String fileDir=getFileDir();
                    String fileId=getFileId();
                    File dir=new File(fileDir+File.separator+codeBank);
                    dir.mkdirs();
                    try {
                        List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
                        for (FileItem item : items) {
                            if (!item.isFormField()) {
                                FileOutputStream fos = new FileOutputStream(fileDir+File.separator+codeBank+File.separator+fileId);
                                InputStream is = item.getInputStream();
                                if (is != null) {
                                    byte[] buf = new byte[8 * 1024];
                                    int n;
                                    while ((n = is.read(buf)) != -1) {
                                        fos.write(buf, 0, n);
                                    }
                                }
                                ros.print("fileId="+fileId);
                                is.close();
                                fos.close();
                            }
                        }
                    } catch (FileUploadException e) {
                        throw new ServletException("Cannot parse multipart request.", e);
                    }
	        }else {
	                ros.print("not found codeBank!!!");
	        }
                ros.close();
    }
	private String getFileId() {
	    Session s = null;
	    try {
	        s = getSession();
	        KrnClass wsCls = s.getClassByName("уд::view::WsUtil");
	        Context ctx = new Context(new long[0], 0, 0);
	        ctx.langId = 0;
	        ctx.trId = 0;
	        s.setContext(ctx);
                SrvOrLang orlang = s.getSrvOrLang();
                String res = (String)orlang.exec(wsCls,wsCls, "getFileId", new ArrayList<Object>(), new Stack<String>());
	        s.commitTransaction();
	        return res;
	        } catch (Throwable e) {
	                 e.printStackTrace();
	                 throw new RemoteException(e.getMessage());
	        } finally {
	                 if (s != null)
	                    s.release();
	                }
	        }
        private String getFileDir() {
            Session s = null;
            try {
                s = getSession();
                KrnClass wsCls = s.getClassByName("уд::view::WsUtil");
                Context ctx = new Context(new long[0], 0, 0);
                ctx.langId = 0;
                ctx.trId = 0;
                s.setContext(ctx);
                SrvOrLang orlang = s.getSrvOrLang();
                String res = (String)orlang.exec(wsCls,wsCls, "getFileDir", new ArrayList<Object>(), new Stack<String>());
                s.commitTransaction();
                return res;
                } catch (Throwable e) {
                         e.printStackTrace();
                         throw new RemoteException(e.getMessage());
                } finally {
                         if (s != null)
                            s.release();
                        }
                }
        private String checkIIN(String iin) {
            Session s = null;
            String res="Сервис недоступен!";
            try {
                s = getSession();
                KrnClass wsCls = s.getClassByName("уд::view::WsUtil");
                Context ctx = new Context(new long[0], 0, 0);
                ctx.langId = 0;
                ctx.trId = 0;
                s.setContext(ctx);
                List<Object> args = new ArrayList<Object>();
                args.add(iin);
                SrvOrLang orlang = s.getSrvOrLang();
                res = (String)orlang.exec(wsCls,wsCls, "checkIIN", args, new Stack<String>());
                s.commitTransaction();
                } catch (Throwable e) {
                         e.printStackTrace();
                         throw new RemoteException(e.getMessage());
                } finally {
                         if (s != null)
                            s.release();
                        }
                return res;
            }
   private Session getSession() throws Exception {    	
    	ServletContext servletContext = getServletContext();
    	String dsName = servletContext.getInitParameter("dataSourceName");
    	String user = servletContext.getInitParameter("user");
    	String password = servletContext.getInitParameter("password");
    	return SrvUtils.getSession(dsName, user, password);
    }
}
