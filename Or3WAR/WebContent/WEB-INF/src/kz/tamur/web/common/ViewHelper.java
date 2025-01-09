package kz.tamur.web.common;

import static kz.tamur.web.common.ServletUtilities.EOL;
import static kz.tamur.web.controller.WebController.APP_PATH;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.swing.tree.TreePath;

import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.rt.InterfaceManager;
import kz.tamur.util.Funcs;
import kz.tamur.util.ThreadLocalDateFormat;
import kz.tamur.web.common.ProcessHelper.ProcessNode;
import kz.tamur.web.common.table.WebTable;
import kz.tamur.web.common.webgui.WebComponent;
import kz.tamur.web.common.webgui.WebDateField;
import kz.tamur.web.common.webgui.WebMenu;
import kz.tamur.web.common.webgui.WebMenuItem;
import kz.tamur.web.common.webgui.WebProcessMenuItem;
import kz.tamur.web.component.OrWebNoteBrowser;
import kz.tamur.web.component.OrWebPanel;
import kz.tamur.web.component.WebFrame.PrinterLangItem;
import kz.tamur.web.component.WebFrame.PrinterMenu;
import kz.tamur.web.component.WebFrame.PrinterMenuItem;
import kz.tamur.web.component.WebFrame.ReportMenu;
import kz.tamur.web.component.WebFrameManager;
import kz.tamur.web.controller.WebController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.PathWordChange;
import com.cifs.or2.client.User;
import com.cifs.or2.client.util.AttrRequestBuilder;
import com.cifs.or2.kernel.Activity;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.StringValue;
import com.cifs.or2.kernel.UserSessionValue;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

public class ViewHelper {
    private static final Log log = LogFactory.getLog("WebLog" + (UserSession.SERVER_ID != null ? ("." + UserSession.SERVER_ID) : ""));
    public static final String[] COL_NAMES_RU = {"","Задача","Объект обработки",
                                       "", "", "",
                                       "Дата", "Время", "Дата контр.",
                                       "Процесс","От кого", "Иниц.процесса"};
    
    private static Map<Integer, List<String>> listProcessSave = new HashMap<Integer, List<String>>();
    public static String getTopHTMLOld(String title, String langCode, Map<String, String> args, WebSession s) {
        StringBuilder b = new StringBuilder(2048);
        int configNumber = s.getConfigNumber();
        try {
            b.append(ServletUtilities.DOCTYPE).append(EOL);
            b.append("<html>").append(EOL);
            b.append("<head>").append(EOL);
            b.append("<title>").append(title).append("</title>").append(EOL);
            b.append("<link rel=\"stylesheet\" href=\"Styles/toc.css?hash="+UpdateContent.tocHash[configNumber]+"\" type=\"text/css\" media=\"screen\" />").append(EOL);
            b.append("<SCRIPT  SRC=\"script/close.js?hash="+UpdateContent.closeHash[configNumber]+"\"" +
                    " TYPE=\"text/javascript\"></SCRIPT>").append(EOL);
            b.append("</head>").append(EOL);
            b.append("<body height=\"100%\" onunload=\"cancelProgram();\">");
            b.append("<table style=\"margin: 0; padding:0\" width=\"100%\" height=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"1\">");

            if (WebController.TITLE_PAGE_HEIGTH != null) {
                b.append("<tr height=\"").append(WebController.TITLE_PAGE_HEIGTH).append("\">");
                b.append("<td width=\"100%\" height=\"").append(WebController.TITLE_PAGE_HEIGTH).append("\">");
                b.append("<iframe class=\"mm\" style=\"width:100%; height: ").append(WebController.TITLE_PAGE_HEIGTH).append("px;\" name=\"title\" scrolling=\"no\" src=\"").append(
                        APP_PATH).append("/title_").append(langCode).append(".html\"></iframe>");
                b.append("</td>");
                b.append("</tr>");
            }

            String cmd = args.get("cmd");
            if (args.get("flow") == null) {
	            b.append("<tr height=\"29\">");
	            b.append("<td width=\"100%\" height=\"29\">");
	            b.append("<iframe class=\"mm\" style=\"width:100%; height: 29px;\" name=\"menu\" scrolling=\"no\" src=\"").append(
	                    APP_PATH).append("/main?trg=menu\" onFocus=\"document.menu.focusCurrent();\" onBlur=\"document.menu.blurCurrent();\"></iframe>");
	            b.append("</td>");
	            b.append("</tr>");

	            b.append("<tr height=\"100%\" valign=\"top\">");
	            b.append("<td width=\"100%\" height=\"100%\">");
	
	            String newTrg = args.get("ntrg");
	
	            if ("lng".equals(cmd) || "dlng".equals(cmd)) {
	                if (s.isNeedMain() && !s.getFrameManager().hasPrev()) {
	                    b.append("<iframe class=\"mm\" style=\"width:100%; height:100%\" name=\"srv\" src=\"").append(
	                            APP_PATH).append("/main?trg=fwb");
	                    for(Iterator<String> it = args.keySet().iterator(); it.hasNext(); ) {
	                        String key = it.next();
	                        if (!"trg".equals(key)) {
	                            String value = args.get(key);
	                            b.append("&").append(key).append("=").append(value);
	                        }
	                    }
	                } else {
	                    b.append("<iframe class=\"mm\" style=\"width:100%; height:100%\" name=\"srv\" src=\"").append(
	                            APP_PATH).append("/main?trg=frm&cmd=lng");
	                }
	            } else if (newTrg == null || newTrg.length() == 0 ) {
	                KrnObject iObj = s.getKernel().getInterface();
	                if (!s.getKernel().isADVANCED_UI()) {
		                s.getFrameManager().clearFrame2();
	                	if (iObj != null)
		                    s.getInterfaceManager().absolute(null, iObj, null, "", InterfaceManager.SERVICE_MODE, true, 0, false, 0, false,"");
		                else
		                    s.getFrameManager().absolute(new KrnObject(0, "", 0), null);
	                }
	
	                b.append("<iframe class=\"mm\" style=\"width:100%; height:100%\" name=\"srv\" src=\"").append(
	                        APP_PATH).append("/main?trg=fwb");
	                for(Iterator<String> it = args.keySet().iterator(); it.hasNext(); ) {
	                    String key = it.next();
	                    if (!"trg".equals(key)) {
	                        String value = args.get(key);
	                        b.append("&").append(key).append("=").append(value);
	                    }
	                }
	            } else {
	                s.getFrameManager().clearFrame2();
	                b.append("<iframe class=\"mm\" style=\"width:100%; height:100%\" name=\"srv\" src=\"").append(
	                        APP_PATH).append("/main?trg=");
	                b.append(newTrg);
	                for(Iterator<String> it = args.keySet().iterator(); it.hasNext(); ) {
	                    String key = it.next();
	                    if (!"trg".equals(key) && !"ntrg".equals(key)) {
	                        String value = args.get(key);
	                        b.append("&").append(key).append("=").append(value);
	                    }
	                }
	            }
	            b.append("\" onFocus=\"if (document.srv.focusCurrent) document.srv.focusCurrent();\" onBlur=\"if (document.srv.blurCurrent) document.srv.blurCurrent();\"\"></iframe>").append(EOL);
	            b.append("</td>");
	            b.append("</tr>");
            } else {
	            b.append("<tr height=\"100%\" valign=\"top\">");
	            b.append("<td>");
	            if ("lng".equals(cmd) || "dlng".equals(cmd)) {
                    b.append("<iframe class=\"mm\" style=\"width:100%; height:100%;\" name=\"srv\" src=\"").append(
                            APP_PATH).append("/main?trg=frm&cmd=lng");
	            } else {
	            	b.append("<iframe class=\"mm\" style=\"width:100%; height:100%;\" name=\"srv\" src=\"").append(
	            			APP_PATH).append("/main?trg=frm&cmd=opn&id=").append(args.get("flow"));
	            }

	            b.append("\"></iframe>").append(EOL);
                b.append("</td>");
	            b.append("</tr>");
            }
            b.append("<tr><td width=\"100%\" height=\"18\">");
            b.append("<iframe class=\"mm\" style=\"width:100%; height: 18px;\" name=\"sts\" scrolling=\"no\" src=\"").append(
                    APP_PATH).append("/main?trg=sts\" onFocus=\"document.sts.focusCurrent();\" onBlur=\"document.sts.blurCurrent();\"></iframe>");
            b.append("</td></tr></table>");
            b.append("<!--[if lte IE 6.5]><iframe id=\"iallfone\" style=\"display: none;\"></iframe><![endif]-->");
            b.append("<div class=\"allfone\" style=\"width: 100%; height: 100%;" +
                    " left: 0; top: 0; display: none;\" id=\"allfone\">");
            b.append("</div>");
            b.append("<img style=\"left: 0; top: 0; display: none;\" class=\"loading\" id=\"loading\" src=\"").append(APP_PATH).append("/images/loading.gif\"></img>");
            b.append("<textarea readonly=\"1\" style=\"width: 280px; height: 100px; text-align: center; border: none; overflow: hidden; background-color: transparent; font-weight: bold; left: 0; top: 0; display: none;\" class=\"loading\" id=\"loadtext\"></textarea>");
            b.append("<input type=\"button\" value=\"Повторить сейчас\" style=\"width: 130px; height: 20px; text-align: center; left: 0; top: 0; display: none;\" id=\"loadbtn\" onclick=\"retryNow();\" />");
        	
            b.append("<div id=\"tip\" class=\"tip\"></div>");

/*            OrWebNoteBrowser[] helps = s.getHelpObjs();
            if (helps != null && helps.length > 0) {
            	getHelpMenuHTML(helps, b);
                b.append("<div class=\"fone\" style=\"width: 100%; height: 100%;" +
                " left: 0; top: 0; display: none;\" id=\"fone\" onclick=\"hideAll(this);\"></div>");
            }
*/            
            b.append("</body></html>");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b.toString();
    }
    
    public static String getLogoHTML(int configNumber) {
    	StringBuilder out = new StringBuilder(1024);
        out.append(ServletUtilities.DOCTYPE).append(EOL);
        out.append(
                "<html><head>").append(EOL).append(
                "<link rel=\"stylesheet\" href=\"Styles/toc.css?hash="+UpdateContent.tocHash[configNumber]+"\" type=\"text/css\" media=\"screen\"/>").append(EOL).append(
                "</head>").append(EOL).append(
                "<body class=\"logo\">").append(EOL);
        out.append("<img src=\"").append(
                                APP_PATH).append("/images/top.jpg\" />");
        out.append("</body></html>");
        return out.toString();
    }

    public static String getStatusHTML(WebSession s) {
    	ResourceBundle resource = s.getResource();
    	long langId = s.getInterfaceLangId();
    	long dLangId = s.getDataLangId(); 
    	
    	StringBuilder out = new StringBuilder(2048);
        out.append("<table class=\"sts\" width=\"100%\" height=\"18\" cellpadding=\"0\" cellspacing=\"0\">").append(EOL);
        out.append("<tr valign=\"center\">").append(EOL);
        out.append("<td align=\"center\" width=\"20%\" style=\"white-space: nowrap;\">").append(EOL);
        out.append("Версия: " + s.getVersion());
        out.append("</td>");

        out.append("<td align=\"center\" width=\"20%\" style=\"white-space: nowrap;\">").append(EOL);
        out.append("Время ответа: <span id=\"ping\"></span> мс");
        out.append("</td>");
        KrnObject baseObj = s.getKernel().getUser().getBase();
        String title = "";
        try {
            String[] strs = s.getKernel().getStrings(baseObj, "наименование", 0, 0);
            if (strs.length > 0) {
                title = strs[0];
            } else {
                title = "Значение не присвоено";
            }
        } catch (Exception e) {}
        out.append("<td align=\"center\" width=\"10%\" style=\"white-space: nowrap;\">").append(EOL);
        out.append(title);
        out.append("</td>");
        out.append("<td align=\"center\" width=\"45%\" style=\"white-space: nowrap;\">").append(EOL);
        out.append(s.getKernel().getUser().getUserSign()).append(": ").append(s.getKernel().getUser().getName());
        out.append("</td>");
        out.append("<td align=\"right\" style=\"white-space: nowrap;\">").append(EOL);
        out.append(resource.getString("interfaceLangLabel"));
        out.append("</td>");

        long rusId = LangHelper.getRusLang(s.getConfigNumber()).obj.id;
        long kazId = LangHelper.getKazLang(s.getConfigNumber()).obj.id;
        if (langId == rusId) {
            out.append("<td align=\"center\" width=\"18\">").append(EOL);
            out.append("<img src=\"").append(
                                APP_PATH).append("/images/RULang.gif\" class=\"sellang\" />");
        } else {
            out.append("<td align=\"center\" width=\"18\">").append(EOL);
            out.append("<a onclick=\"setInterfaceLang(").append(
                    rusId).append(
                    ");\">");
            out.append("<img src=\"").append(
                                APP_PATH).append("/images/RULang.gif\" />");
            out.append("</a>");
        }
        out.append("</td>");
        if (langId == kazId) {
            out.append("<td align=\"center\" width=\"18\">").append(EOL);
            out.append("<img src=\"").append(
                                APP_PATH).append("/images/KZLang.gif\" class=\"sellang\" />");
        } else {
            out.append("<td align=\"center\" width=\"18\">").append(EOL);
            out.append("<a onclick=\"setInterfaceLang(").append(
                    kazId).append(
                    ");\">");
            out.append("<img src=\"").append(
                                APP_PATH).append("/images/KZLang.gif\" />");
            out.append("</a>");
        }
        out.append("</td>");
        out.append("<td align=\"right\" style=\"white-space: nowrap;\">").append(EOL);
        out.append(resource.getString("datalang"));
        out.append("</td>");
        if (dLangId == rusId) {
            out.append("<td align=\"center\" width=\"18\">").append(EOL);
            out.append("<img src=\"").append(
                                APP_PATH).append("/images/RULang.gif\" class=\"sellang\" />");
        } else {
            out.append("<td align=\"center\" width=\"18\">").append(EOL);
            out.append("<a onclick=\"setDataLang(").append(
                    rusId).append(
                    ");\">");
            out.append("<img src=\"").append(
                                APP_PATH).append("/images/RULang.gif\" />");
            out.append("</a>");
        }
        out.append("</td>");
        if (dLangId == kazId) {
            out.append("<td align=\"center\" width=\"18\">").append(EOL);
            out.append("<img src=\"").append(
                                APP_PATH).append("/images/KZLang.gif\" class=\"sellang\" />");
        } else {
            out.append("<td align=\"center\" width=\"18\">").append(EOL);
            out.append("<a onclick=\"setDataLang(").append(kazId).append(");\">");
            out.append("<img src=\"").append(
                                APP_PATH).append("/images/KZLang.gif\" />");
            out.append("</a>");
        }
        out.append("</td>");

        out.append("</tr></table>");

        return out.toString();
    }

    public static String getStatusHTMLOld(long langId, long dLangId, ResourceBundle resource, WebSession s) {
    	StringBuilder out = new StringBuilder(2048);
    	int configNumber = s.getConfigNumber();
        out.append(ServletUtilities.DOCTYPE).append(EOL);
        out.append(
                "<html><head>").append(EOL).append(
                "<link rel=\"stylesheet\" href=\"Styles/toc.css?hash="+UpdateContent.tocHash[configNumber]+"\" type=\"text/css\" media=\"screen\" />").append(EOL);
        out.append("<SCRIPT  SRC=\"script/langs.js?hash="+UpdateContent.langsHash[configNumber]+"\" TYPE=\"text/javascript\"></SCRIPT>").append(EOL);
        out.append("</head>").append(EOL);

        out.append("<body class=\"sts\" onload=\"attachKeydownHandler(); setInterval('refreshPingInfo()', ").append(WebController.PING_PERIOD).append(");\">").append(EOL);
        out.append("<table class=\"sts\" width=\"100%\" height=\"18\" cellpadding=\"0\" cellspacing=\"0\">").append(EOL);
        out.append("<tr valign=\"center\">").append(EOL);

        out.append("<td align=\"center\" width=\"20%\" style=\"white-space: nowrap;\">").append(EOL);
        out.append("Версия: " + s.getVersion());
        out.append("</td>");

        out.append("<td align=\"center\" width=\"20%\" style=\"white-space: nowrap;\">").append(EOL);
        out.append("Время ответа: <span id=\"ping\"></span> мс");
        out.append("</td>");
        KrnObject baseObj = s.getKernel().getUser().getBase();
        String title = "";
        try {
            String[] strs = s.getKernel().getStrings(baseObj, "наименование", 0, 0);
            if (strs.length > 0) {
                title = strs[0];
            } else {
                title = "Значение не присвоено";
            }
        } catch (Exception e) {}
        out.append("<td align=\"center\" width=\"10%\" style=\"white-space: nowrap;\">").append(EOL);
        out.append(title);
        out.append("</td>");
        out.append("<td align=\"center\" width=\"45%\" style=\"white-space: nowrap;\">").append(EOL);
        out.append(s.getKernel().getUser().getUserSign()).append(": ").append(s.getKernel().getUser().getName());
        out.append("</td>");
        out.append("<td align=\"right\" style=\"white-space: nowrap;\">").append(EOL);
        out.append(resource.getString("interfaceLangLabel"));
        out.append("</td>");

        long rusId = LangHelper.getRusLang(s.getConfigNumber()).obj.id;
        long kazId = LangHelper.getKazLang(s.getConfigNumber()).obj.id;
        if (langId == rusId) {
            out.append("<td align=\"center\" width=\"18\">").append(EOL);
            out.append("<img src=\"").append(
                                APP_PATH).append("/images/RULang.gif\" class=\"sellang\" />");
        } else {
            out.append("<td align=\"center\" width=\"18\">").append(EOL);
            out.append("<a onclick=\"setInterfaceLang(").append(
                    rusId).append(
                    ");\">");
            out.append("<img src=\"").append(
                                APP_PATH).append("/images/RULang.gif\" />");
            out.append("</a>");
        }
        out.append("</td>");
        if (langId == kazId) {
            out.append("<td align=\"center\" width=\"18\">").append(EOL);
            out.append("<img src=\"").append(
                                APP_PATH).append("/images/KZLang.gif\" class=\"sellang\" />");
        } else {
            out.append("<td align=\"center\" width=\"18\">").append(EOL);
            out.append("<a onclick=\"setInterfaceLang(").append(
                    kazId).append(
                    ");\">");
            out.append("<img src=\"").append(
                                APP_PATH).append("/images/KZLang.gif\" />");
            out.append("</a>");
        }
        out.append("</td>");
        out.append("<td align=\"right\" style=\"white-space: nowrap;\">").append(EOL);
        out.append(resource.getString("datalang"));
        out.append("</td>");
        if (dLangId == rusId) {
            out.append("<td align=\"center\" width=\"18\">").append(EOL);
            out.append("<img src=\"").append(
                                APP_PATH).append("/images/RULang.gif\" class=\"sellang\" />");
        } else {
            out.append("<td align=\"center\" width=\"18\">").append(EOL);
            out.append("<a onclick=\"setDataLang(").append(
                    rusId).append(
                    ");\">");
            out.append("<img src=\"").append(
                                APP_PATH).append("/images/RULang.gif\" />");
            out.append("</a>");
        }
        out.append("</td>");
        if (dLangId == kazId) {
            out.append("<td align=\"center\" width=\"18\">").append(EOL);
            out.append("<img src=\"").append(
                                APP_PATH).append("/images/KZLang.gif\" class=\"sellang\" />");
        } else {
            out.append("<td align=\"center\" width=\"18\">").append(EOL);
            out.append("<a onclick=\"setDataLang(").append(kazId).append(");\">");
            out.append("<img src=\"").append(
                                APP_PATH).append("/images/KZLang.gif\" />");
            out.append("</a>");
        }
        out.append("</td>");

        out.append("</tr></table>");
        out.append("</body></html>");

        return out.toString();
    }

    public static String getMenuHTML(WebSession s, boolean mainFrame, boolean forPublicUser, boolean isAdmin, boolean isSigner, WebMenu printMenu,
            HttpServletRequest request) {

        ResourceBundle resource = s.getResource();
        WebMenuItem processMenu = s.getProcessMenu();
        boolean themeD = "daulet".equals(WebController.THEME);
        StringBuilder out = new StringBuilder(8 * 1024);
        out.append(ServletUtilities.DOCTYPE).append(EOL);
        out.append("<div style='width:100%; height:45px;'></div><div class='navbar navbar-static-top' style='")
                .append(s.isToolBar ? "" : " display:none; ")
                .append("position: fixed; top: 0; z-index: 1;'><div class='navbar-inner'><div class='container pull-left' style='width: 80%;'>")
                .append(EOL);

        out.append("<ul class=\"nav\">");

        if (!forPublicUser && !isSigner) {
        	out.append("<li>");
	        out.append("<a id=\"rlb\" href=\"#\" onclick=\"rollback();\" rel='tooltip' data-placement='bottom' title='")
	                .append(resource.getString("cancelChangesShort")).append("'>").append(EOL);
	        out.append("<img width=\"24\" height=\"24\" src=\"").append(APP_PATH).append(themeD?"/images/Cancel32-d.png\" />":"/images/Cancel32.png\" />").append("</a>")
	                .append(EOL);
	        out.append("</li>");

	        out.append("<li class='active'>");
	        out.append("<a id='com' href=\"#\" onclick=\"commit();\" rel='tooltip' data-placement='bottom' title='")
	                .append(resource.getString("applyChangesShort")).append("'>").append(EOL);
	        out.append("<img width=\"24\" height=\"24\" src=\"").append(APP_PATH).append(themeD?"/images/Apply32-d.png\" />":"/images/Apply32.png\" />").append("</a>")
	                .append(EOL);
	        out.append("</li>");
        }
        
		if (!isSigner) {
	        out.append("<li>");
	        out.append("<a id=\"prev\" href=\"#\" onclick=\"previous();\" rel='tooltip' data-placement='bottom' title='")
	                .append(resource.getString("backPageShort")).append("'>").append(EOL);
	        out.append("<img width=\"24\" height=\"24\" src=\"").append(APP_PATH).append(themeD?"/images/BackPage32-d.png\" />":"/images/BackPage32.png\" />")
	                .append("</a>").append(EOL);
	        out.append("</li>");
		}
		
		if (!forPublicUser && !isSigner) {
	        out.append("<li>");
	        out.append("<a id=\"run\" href=\"#\" onclick=\"next();\" rel='tooltip' data-placement='bottom' title='")
	                .append(resource.getString("buttonRun")).append("'>").append(EOL);
	        out.append("<img width=\"24\" height=\"24\" src=\"").append(APP_PATH).append(themeD?"/images/buttonRun-d.png\" />":"/images/buttonRun.png\" />").append("</a>")
	                .append(EOL);
	        out.append("</li>");

	        StringBuilder repMenu = new StringBuilder();

	        if (printMenu instanceof WebMenu) {
	        	repMenu.append("<li class=\"divider-vertical\"></li>");
	        	getReportMenuHtml(resource, repMenu, printMenu, s, "caret");
	        }
	        out.append(repMenu);

/*	        out.append("<li>");
	        out.append("<a id=\"print\" href=\"#\" onclick=\"showPrintMenu();\" rel='tooltip' data-placement='bottom' title='")
	                .append(resource.getString("print")).append("'>").append(EOL);
	        out.append("<img width=\"24\" height=\"24\" src=\"").append(APP_PATH).append("/images/Printer32.png\" />").append("</a>")
	                .append(EOL);
	        out.append("</li>");
*/	
	        out.append("<li>");
	        out.append("<a id=\"chng\" href=\"#\" onclick=\"changePassword();\" rel='tooltip' data-placement='bottom' title='")
	                .append(resource.getString("pwdChange")).append("'>").append(EOL);
	        out.append("<img width=\"24\" height=\"24\" src=\"").append(APP_PATH).append("/images/access.png\" />").append("</a>")
	                .append(EOL);
	        out.append("</li>");
		}
		
        StringBuilder procMenu = new StringBuilder();
        
        if (processMenu instanceof WebMenu) {
            procMenu.append("<li class=\"divider-vertical\"></li>");
        	getProcessMenuHtml(s, procMenu, out, (WebMenu)processMenu, "caret");
        }

        out.append(procMenu);
        out.append("</ul></div>");

        out.append("<ul class=\"nav\" style=\"float:right;\">");

        out.append("<li>");
        out.append(
                "<a id=\"exit\" href=\"#\" onclick=\"exit('" + (forPublicUser ? s.backPage : WebController.BACK_PAGE)
                        + "');\" rel='tooltip' data-placement='bottom' title='").append(resource.getString("exit")).append("'>")
                .append(EOL);
        out.append("<img width=\"24\" height=\"24\" src=\"").append(APP_PATH).append("/images/exit.png\" />").append("</a>")
                .append(EOL);
        out.append("</li>");
        out.append("</ul>");

        out.append("</div></div>").append(EOL);
        return out.toString();
    }

	private static void getProcessMenuHtml(WebSession s, StringBuilder procMenu, StringBuilder out, WebMenu m, String caretClass) {
		ResourceBundle r = s.getResource();
		
    	String text = "";
    	if (LangHelper.getRusLang(s.getConfigNumber()).obj.id == s.getInterfaceLangId()) {
    		text = m.getText();
    	} else {
    		text = m.getNameKz();
    	}

    	procMenu.append("<li class=\"dropdown\">");
        procMenu.append("<a class=\"dropdown-toggle\" data-toggle=\"dropdown\" href=\"#\">").append(text)
                .append("<b class=\"").append(caretClass).append("\"></b></a>");
        procMenu.append("<ul class=\"dropdown-menu\">");

        WebComponent[] children = m.getPopupMenu().getChildren();
        for (WebComponent c : children) {
            if (c instanceof WebMenu) {
                getProcessMenuHtml(s, procMenu, out, (WebMenu) c, "caret-right");
            } else if (c instanceof WebMenuItem) {
                WebProcessMenuItem mi = (WebProcessMenuItem) c;
            	if (LangHelper.getRusLang(s.getConfigNumber()).obj.id == s.getInterfaceLangId()) {
            		text = mi.getText();
            	} else {
            		text = mi.getNameKz();
            	}
            			
                long procId = mi.getProcessObject().id;
                procMenu.append("<li>");
                procMenu.append("<a href='#' processId='").append(procId).append("' msg='")
                        .append(r.getString("startProcMessage")).append("'>").append(text).append("</a>");
                procMenu.append("</li>");

                if (mi.isOnToolbal()) {
                    out.append("<li>");
                    out.append("<a href='#' processId='").append(procId).append("' msg='")
                            .append(r.getString("startProcMessage"));

                    out.append("' rel='tooltip' data-placement='bottom' title='").append(text).append("'>")
                            .append(EOL);
                    out.append("<img width=\"24\" height=\"24\" src=\"").append(mi.getIconFullPath()).append("\" />")
                            .append("</a>").append(EOL);
                    out.append("</li>");
                }
            }
        }

        procMenu.append("</ul>");
        procMenu.append("</li>");
	}

	private static void getReportMenuHtml(ResourceBundle r, StringBuilder menuOut, WebMenu m, WebSession s, String caretClass) {
		String img = "caret".equals(caretClass) 
				? "<img width=\"24\" height=\"24\" src=\"" + WebController.APP_PATH + "/images/Printer32.png\" />" 
				: "";
		
		menuOut.append("<li class=\"dropdown\">");
		
		menuOut.append("<a class=\"dropdown-toggle\" data-toggle=\"dropdown\" href=\"#\">").append(img).append(m.getText())
                .append("<b class=\"").append(caretClass).append("\"></b></a>");
		menuOut.append("<ul class=\"dropdown-menu\">");

        WebComponent[] children = m.getPopupMenu().getChildren();
        for (WebComponent c : children) {
            if (c instanceof ReportMenu || c instanceof PrinterMenu) {
            	getReportMenuHtml(r, menuOut, (WebMenu) c, s, "caret-right");
            } else if (c instanceof PrinterMenuItem) {
            	PrinterMenuItem mi = (PrinterMenuItem) c;
    			if (LangHelper.getRusLang(s.getConfigNumber()).obj.id == mi.getLanguage().id) {
    				img = "<img src=\"" + WebController.APP_PATH
    						+ "/images/RULang.gif\" />";
    			} else if (LangHelper.getKazLang(s.getConfigNumber()).obj.id == mi.getLanguage().id) {
    				img = "<img src=\"" + WebController.APP_PATH
    						+ "/images/KZLang.gif\" />";
    			}

    			String reportId = mi.getId();
                menuOut.append("<li>");
                menuOut.append("<a href='#' reportId='").append(reportId).append("'>").append(img).append(mi.getText()).append("</a>");
                menuOut.append("</li>");
            } else if (c instanceof PrinterLangItem) {
            	PrinterLangItem mi = (PrinterLangItem) c;
                String reportId = mi.getId();
    			if (LangHelper.getRusLang(s.getConfigNumber()).obj.id == mi.getLanguage().id) {
    				img = "<img src=\"" + WebController.APP_PATH
    						+ "/images/RULang.gif\" />";
    			} else if (LangHelper.getKazLang(s.getConfigNumber()).obj.id == mi.getLanguage().id) {
    				img = "<img src=\"" + WebController.APP_PATH
    						+ "/images/KZLang.gif\" />";
    			}

    			menuOut.append("<li>");
                menuOut.append("<a href='#' reportId='").append(reportId).append("'>").append(img).append(mi.getText()).append("</a>");
                menuOut.append("</li>");
            }
        }

        menuOut.append("</ul>");
        menuOut.append("</li>");
	}

	public static String getMenuHTMLOld(WebSession s, ResourceBundle resource,
			boolean mainFrame, boolean forPublicUser, boolean isAdmin,
			boolean isSigner, HttpServletRequest request) {
		StringBuilder out = new StringBuilder(8 * 1024);
		int configNumber = s.getConfigNumber();
		 boolean themeD = "daulet".equals(WebController.THEME);
		out.append(ServletUtilities.DOCTYPE).append(EOL);
		out.append("<html><head>").append(EOL);
		out.append(
				"<link rel=\"stylesheet\" href=\"Styles/toc.css?hash="+UpdateContent.tocHash[configNumber]+"\" type=\"text/css\" media=\"screen\" />")
				.append(EOL);

		out.append(
				"<SCRIPT  SRC=\"script/commit.js?hash="+UpdateContent.commitHash[configNumber]+"\""
						+ " TYPE=\"text/javascript\"></SCRIPT>").append(
				EOL);
		out.append("</head>").append(EOL);

		out.append("<script language=\"JavaScript\">").append(
				EOL);
		out.append("window.onload = function() {attachKeydownHandler(); ")
				.append(EOL);
		out.append("}").append(EOL);
		out.append("</script>").append(EOL);

		out.append("<body style=\"vertical-align: middle;\" class=\"nospace\">");

		out.append(
				"<table style=\"height: 28px; width: 100%;\" class=\"nospace\" cellspacing=\"0\" cellpadding=\"0\">")
				.append(EOL);
		out.append("<tr valign=\"center\">").append(EOL);
		if (!forPublicUser && !isSigner) {
			out.append("<td align=\"center\">").append(EOL);
			out.append(
					"<a class=\"menuBtn\" onmouseover=\"menuBtnOver(this);\" onmouseout=\"menuBtnOut(this);\" onclick=\"rollback();\" id=\"rlb\">"
							+ "<table cellspacing=\"0\" cellpadding=\"0\"><tr><td>"
							+ "<img width=\"24\" height=\"24\" src=\"")
					.append(APP_PATH)
					.append(themeD?"/images/Cancel32-d.png\" alt=\"":"/images/Cancel32.png\" alt=\"")
					.append(resource.getString("cancelChangesShort"))
					.append("\"/></td><td>")
					.append(resource.getString("cancelChangesShort"))
					.append("</td></tr></table></a>")
					.append(EOL);
			out.append("</td>").append(EOL);
			out.append("<td align=\"center\">").append(EOL);
			out.append(
					"<a class=\"menuBtn\" onmouseover=\"menuBtnOver(this);\" onmouseout=\"menuBtnOut(this);\" onclick=\"canCommit();\" id=\"com\">")
					.append("<table cellspacing=\"0\" cellpadding=\"0\"><tr><td>"
							+ "<img width=\"24\" height=\"24\" src=\"")
					.append(APP_PATH)
					.append(themeD?"/images/Apply32-d.png\" alt=\"":"/images/Apply32.png\" alt=\"")
					.append(resource.getString("applyChangesShort"))
					.append("\"/></td><td>")
					.append(resource.getString("applyChangesShort"))
					.append("</td></tr></table></a>")
					.append(EOL);
			out.append("</td>").append(EOL);
		}
		if (!isSigner) {
			out.append("<td align=\"center\">").append(EOL);
			out.append(
					"<a class=\"menuBtn\" onmouseover=\"menuBtnOver(this);\" onmouseout=\"menuBtnOut(this);\" onclick=\"previousWithCommit();\" id=\"prev\">")
					.append("<table cellspacing=\"0\" cellpadding=\"0\"><tr><td>"
							+ "<img width=\"24\" height=\"24\" src=\"")
					.append(APP_PATH)
					.append(themeD?"/images/BackPage32-d.gif\" alt=\"":"/images/BackPage32.gif\" alt=\"")
					.append(resource.getString("backPageShort"))
					.append("\"/></td><td style=\"white-space: nowrap;\">")
					.append(resource.getString("backPageShort"))
					.append("</td></tr></table></a>")
					.append(EOL);
			out.append("</td>").append(EOL);
		}
		if (!forPublicUser && !isSigner) {
			out.append("<td align=\"center\">").append(EOL);
			out.append("<a class=\"menuBtn\" onmouseover=\"menuBtnOver(this);\" onmouseout=\"menuBtnOut(this);\" onclick=\"showPrintMenu();\" id=\"print\">");
			out.append(
					"<table cellspacing=\"0\" cellpadding=\"0\"><tr><td>"
							+ "<img width=\"24\" height=\"24\" src=\"")
					.append(APP_PATH)
					.append("/images/Printer32.png\" alt=\"")
					.append(resource.getString("print"))
					.append("\"/></td><td style=\"white-space: nowrap;\">")
					.append(resource.getString("print"))
					.append(EOL);
			out.append("</td></tr></table></a>").append(EOL);
			out.append("</td>").append(EOL);
			/*
			 * out.append("<td align=\"center\">").append(EOL);
			 * out.append("<img src=\"" +
			 * APP_PATH).append("/images/SrvMap.gif\" alt=\"" +
			 * resource
			 * .getString("processMap")).append("\"/>").append(ServletUtilities
			 * .EOL); out.append("</td>").append(EOL);
			 * out.append("<td align=\"center\">").append(EOL);
			 * out.append("<img src=\"" +
			 * APP_PATH).append("/images/SuperProc.gif\" alt=\"" +
			 * resource
			 * .getString("superProcess")).append("\"/>").append(ServletUtilities
			 * .EOL); out.append("</td>").append(EOL);
			 * out.append("<td align=\"center\">").append(EOL);
			 * out.append("<img src=\"" +
			 * APP_PATH).append("/images/SubProc.gif\" alt=\"" +
			 * resource
			 * .getString("subProcess")).append("\"/>").append(ServletUtilities
			 * .EOL); out.append("</td>").append(EOL);
			 * out.append("<td align=\"center\">").append(EOL);
			 * out.append("<img src=\"" +
			 * APP_PATH).append("/images/DebugProc.gif\" alt=\"" +
			 * resource
			 * .getString("debugProcess")).append("\"/>").append(ServletUtilities
			 * .EOL); out.append("</td>").append(EOL);
			 */
			out.append("<td align=\"center\">").append(EOL);
			out.append("<a class=\"menuBtn\" onmouseover=\"menuBtnOver(this);\" onmouseout=\"menuBtnOut(this);\" onclick=\"changePassword();\">");
			out.append(
					"<table cellspacing=\"0\" cellpadding=\"0\"><tr><td>"
							+ "<img width=\"24\" height=\"24\" src=\"")
					.append(APP_PATH)
					.append("/images/access.png\" alt=\"")
					.append(resource.getString("pwdChange"))
					.append("\"/></td><td style=\"white-space: nowrap;\">")
					.append(resource.getString("pwdChange"))
					.append(EOL);
			out.append("</td></tr></table></a>").append(EOL);
			out.append("</td>").append(EOL);
		}
		if (isAdmin && !isSigner) {
			out.append("<td align=\"center\">").append(EOL);
			out.append("<a class=\"menuBtn\" onmouseover=\"menuBtnOver(this);\" onmouseout=\"menuBtnOut(this);\" onclick=\"showUsers();\">");
			out.append(
					"<table cellspacing=\"0\" cellpadding=\"0\"><tr><td>"
							+ "<img width=\"24\" height=\"24\" src=\"")
					.append(APP_PATH)
					.append("/images/users.gif\" alt=\"")
					.append(resource.getString("usersShort"))
					.append("\"/></td><td>")
					.append(resource.getString("usersShort"))
					.append(EOL);
			out.append("</td></tr></table></a>").append(EOL);
			out.append("</td>").append(EOL);
		}
		/*
		 * if (WebController.HELP_LINK != null) { String helpUrl =
		 * request.getRequestURL().toString(); int end =
		 * helpUrl.indexOf(APP_PATH); if (end > -1) { helpUrl =
		 * new StringBuffer("mhtml:").append(helpUrl.substring(0,
		 * end)).append(WebController
		 * .APP_PATH).append("/").append(WebController.HELP_LINK).toString(); }
		 * 
		 * out.append("<td align=\"center\">").append(EOL);
		 * out.append(
		 * "<a class=\"menuBtn\" onmouseover=\"menuBtnOver(this);\" onmouseout=\"menuBtnOut(this);\""
		 * ); out.append(" target=\"helpWnd\"");
		 * out.append(" href=\"").append(helpUrl).append("\">");
		 * out.append("<table cellspacing=\"0\" cellpadding=\"0\"><tr><td>" +
		 * "<img width=\"24\" height=\"24\" src=\"").append(
		 * APP_PATH
		 * ).append("/images/helpAkt.gif\" alt=\"").append(
		 * resource.getString("helpShort"
		 * )).append("\"/></td><td>").append(resource.getString("helpShort"));
		 * out.append("</td></tr></table></a>").append(EOL);
		 * out.append("</td>").append(EOL); }
		 */
		if (isSigner) {
			out.append("<td align=\"center\">").append(EOL);
			out.append(
					"&nbsp;&nbsp;&nbsp;<a class=\"menuBtn\" onmouseover=\"menuBtnOver(this);\" onmouseout=\"menuBtnOut(this);\" onclick=\"signAndSend();\" id=\"sign\">")
					.append("<table cellspacing=\"0\" cellpadding=\"0\"><tr><td>"
							+ "<img width=\"24\" height=\"24\" src=\"")
					.append(APP_PATH)
					.append(themeD?"/images/Apply32-d.png\" alt=\"":"/images/Apply32.png\" alt=\"")
					.append(resource.getString("signShort"))
					.append("\"/></td><td>")
					.append(resource.getString("signShort"))
					.append("</td></tr></table></a>")
					.append(EOL);
			out.append("</td>").append(EOL);
		}
		if (!isSigner) {
/*			OrWebNoteBrowser[] helps = s.getHelpObjs();
			if (helps != null && helps.length > 0) {
				out.append("<td align=\"center\">")
						.append(EOL);
				out.append("<a class=\"menuBtn\" onmouseover=\"menuBtnOver(this);\" onmouseout=\"menuBtnOut(this);\" onclick=\"showHelpMenu();\">");
				out.append(
						"<table cellspacing=\"0\" cellpadding=\"0\"><tr><td>"
								+ "<img width=\"24\" height=\"24\" src=\"")
						.append(APP_PATH)
						.append("/images/helpAkt.gif\" alt=\"")
						.append(resource.getString("helpShort"))
						.append("\"/></td><td>")
						.append(resource.getString("helpShort"));
				out.append("</td></tr></table></a>").append(
						EOL);
				out.append("</td>").append(EOL);
			}
*/			out.append("<td align=\"center\">").append(EOL);
			out.append("<a class=\"menuBtn\" onmouseover=\"menuBtnOver(this);\" onmouseout=\"menuBtnOut(this);\" onclick=\"changeCursor(event);\" id=\"hctx\">");
			out.append(
					"<table cellspacing=\"0\" cellpadding=\"0\"><tr><td>"
							+ "<img width=\"24\" height=\"24\" src=\"")
					.append(APP_PATH)
					.append("/images/HelpCursor1.gif\" alt=\"")
					.append(resource.getString("helpTipLong"))
					.append("\"/></td><td>")
					.append(resource.getString("helpTipShort"))
					.append(EOL);
			out.append("</td></tr></table></a>").append(EOL);
			out.append(
					"<div id=\"tipCtx\" class=\"tip\" style=\"padding: 0px; margin: 0px;\">")
					.append(resource.getString("helpTipDesc")).append("</div>");
			out.append("</td>").append(EOL);
		}

		out.append("<td align=\"right\" width=\"60%\">").append(
				EOL);
		out.append("<a class=\"menuBtn\" onmouseover=\"menuBtnOver(this);\" onmouseout=\"menuBtnOut(this);\" onclick=\"cancelProgram();\">");
		out.append(
				"<table cellspacing=\"0\" cellpadding=\"0\"><tr><td>"
						+ "<img width=\"24\" height=\"24\" src=\"")
				.append(APP_PATH)
				.append("/images/exit.png\" alt=\"")
				.append(resource.getString("exit")).append("\"/></td><td>")
				.append(resource.getString("exit"))
				.append(EOL);
		out.append("</td></tr></table></a>").append(EOL);
		out.append("</td>").append(EOL);

		out.append("</tr>").append(EOL);
		out.append("</table>");
		out.append("</body></html>");
		return out.toString();
	}

    public static String getButtonsHTML(int selectedButton, long langId, String backpage, String onclick, WebSession s) {
        StringBuffer out = new StringBuffer(2048);
        int configNumber = s.getConfigNumber();
        out.append(ServletUtilities.DOCTYPE).append(EOL);
        out.append("<html><head>").append(EOL);
        out.append("<link rel=\"stylesheet\" href=\"Styles/toc.css?hash="+UpdateContent.tocHash[configNumber]+"\" type=\"text/css\" media=\"screen\">").append(EOL);

        out.append("<SCRIPT  SRC=\"script/buttons.js?hash="+UpdateContent.buttonsHash[configNumber]+"\"" +
                " TYPE=\"text/javascript\"></SCRIPT>").append(EOL);

        out.append("</head>").append(EOL);

        out.append("<script language=\"JavaScript\">").append(EOL);
        out.append("window.onload = function() {").append(EOL);
        out.append("disableButton(\"com\");").append(EOL);
        out.append("disableButton(\"rlb\");").append(EOL);
        out.append("disableButton(\"prev\", \"").append(backpage).append("\", \"").append(onclick).append("\");").append(EOL);
        out.append("disableButton(\"print\");").append(EOL);
        out.append("disableButton(\"hctx\");").append(EOL);
        out.append("}").append(EOL);
        out.append("</script>").append(EOL);

        out.append("<body class=\"nospace\">");

        out.append("<table class=\"nospace\">").append(EOL);
        out.append("<tr valign=\"center\">").append(EOL);
        out.append("<td id=\"ss\" align=\"center\" onclick=\"showService();\"");
        if (selectedButton == CommonHelper.TASKS_BUTTON)
            out.append(" bgcolor=\"white\">").append(EOL);
        else
            out.append(">").append(EOL);

        long rusId = LangHelper.getRusLang(s.getConfigNumber()).obj.id;
        //long kazId = LangHelper.getKazLang().obj.id;
        if (langId == rusId) {
            out.append("<img src=\"").append(
                                APP_PATH).append("/images/tasks.gif\" />").append(EOL);
        } else {
            out.append("<img src=\"").append(
                                APP_PATH).append("/images/tasksKaz.gif\" />").append(EOL);
        }
        out.append("</td></tr>").append(EOL);

        out.append("<tr valign=\"center\">").append(EOL);
        out.append("<td id=\"sa\" align=\"center\" onclick=\"showArchive();\"");
        if (selectedButton == CommonHelper.ARCHIVE_BUTTON)
            out.append(" bgcolor=\"white\">").append(EOL);
        else
            out.append(">").append(EOL);
        if (langId == rusId) {
            out.append("<img src=\"").append(
                                APP_PATH).append("/images/archiv.gif\" />").append(EOL);
        } else {
            out.append("<img src=\"").append(
                                APP_PATH).append("/images/archivKaz.gif\" />").append(EOL);

        }
        out.append("</td></tr>").append(EOL);

        out.append("<tr valign=\"center\">").append(EOL);
        out.append("<td id=\"sd\" align=\"center\" onclick=\"showDictionary();\"");
        if (selectedButton == CommonHelper.DICT_BUTTON)
            out.append(" bgcolor=\"white\">").append(EOL);
        else
            out.append(">").append(EOL);
        if (langId == rusId) {
            out.append("<img src=\"").append(
                                APP_PATH).append("/images/catalog.gif\" />").append(EOL);
        } else {
            out.append("<img src=\"").append(
                                APP_PATH).append("/images/catalogKaz.gif\" />").append(EOL);
        }
        out.append("</td></tr></table>");
        out.append("</body></html>");
        return out.toString();
    }

	public static String getTaskAndProcessesHTML(Map<String, String> args, WebSession s) {
        log.info("|USER: " + s.getUserName() + "| in getTaskAndProcessesHTML");
        StringBuilder out = new StringBuilder(2048);
        int configNumber = s.getConfigNumber();
        out.append(ServletUtilities.DOCTYPE).append(EOL);
        out.append("<html><head>").append(EOL);
        out.append("<link rel=\"stylesheet\" href=\"Styles/toc.css?hash="+UpdateContent.tocHash[configNumber]+"\" type=\"text/css\" media=\"screen\" />").append(EOL);
        out.append("</head>").append(EOL);
        out.append("<body>");
        out.append("<table width=\"100%\" height=\"100%\" border=\"1\">");
        out.append("<tr height=\"50%\">");
        out.append("<td width=\"100%\">");
        out.append("<iframe class=\"mm\" style=\"width:100%; height: 100%; overflow: auto;\" name=\"tsk\" src=\"").append(
                APP_PATH).append("/main?trg=srv&cmd=tsk\"></iframe>");
        out.append("</td></tr>");

        out.append("<tr height=\"50%\">");
        out.append("<td width=\"100%\">");
        out.append("<iframe class=\"mm\" style=\"width:100%; height: 100%; overflow: auto;\" name=\"prs\" src=\"").append(
                APP_PATH).append("/main?trg=srv&cmd=prs");
        try {
            for(Iterator<String> it = args.keySet().iterator(); it.hasNext(); ) {
                String key = it.next();
                if (!"trg".equals(key)) {
                    out.append("&").append(key).append("=");

                    String value = args.get(key);
                    char[] arr = value.toCharArray();
                    for (int i = 0; i < arr.length; ++i) {
                        char ch = arr[i];
                        if (ch > 255) {
                            String code = Integer.toHexString(ch);
                            while (code.length() < 4) {
                                code = "0" + code;
                            }
                            code = "\\u" + code;
                            out.append(code);
                        } else {
                            out.append(ch);
                        }
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        out.append("\"></iframe>").append(EOL);
        out.append("</td></tr>");

        out.append("</table></body></html>");
        return out.toString();
    }

	public static String getTaskAndProcessesHTML2(Map<String, String> args, WebSession s) {
        StringBuilder out = new StringBuilder(2048);
        out.append("<table width=\"100%\" height=\"100%\" border=\"1\">");
        out.append("<tr height=\"50%\">");
        out.append("<td width=\"100%\">");

        if (s.getTaskHelper() != null) {
            out.append("<div style='width:100%;height:100%;position:relative;overflow:auto;'>");
        	out.append(getTaskTableInnerHTML/*jq*/(s.getTaskHelper().getTable()));
            out.append("</div>");
        }

        out.append("</td></tr>");

        if (!WebController.PROCESS_MENU) {
	        out.append("<tr height=\"50%\" valign=\"top\">");
	        out.append("<td width=\"100%\" style=\"vertical-align: top;\">");
	        
	    	if (s.getProcessHelper() != null) {
	            SortedSet<ProcessNode> tabs = s.getProcessHelper().getTabs();
	            Long selectedTab = s.getProcessHelper().getSelectedTab();
	            out.append("<div style='width:100%;height:100%;position:relative;overflow:auto;'>");
	            out.append(getProcessesInnerHTML(tabs, selectedTab, s));
	            out.append("</div>");
	    	}
	        out.append("</td></tr>");
        }

        out.append("</table></body></html>");
        return out.toString();
    }

	public static String getTaskTableHTML() {
        StringBuffer out = new StringBuffer();
        out.append(ServletUtilities.DOCTYPE).append(EOL);
        out.append("<html>").append(EOL);
        out.append("<body style=\"overflow: auto;\">").append(EOL);
        out.append("<p>TaskTable</p>").append(EOL);
        out.append("<table id=\"tasks\">").append(EOL);
        out.append("<thead>").append(EOL);
        out.append("<tr>").append(EOL);
        out.append("<th>p</th>").append(EOL);
        out.append("<th>num</th>").append(EOL);
        out.append("</tr>").append(EOL);
        out.append("</thead>").append(EOL);
        out.append("<tbody>").append(EOL);
        out.append("</tbody>").append(EOL);
        out.append("</table>").append(EOL);
        out.append("</body>").append(EOL);
        out.append("</html>");
        return out.toString();
    }

    public static String getTaskTableInnerHTML(WebTable table) {
        StringBuilder out = new StringBuilder(2048);

        table.getHTML(out);
        // Поле для поиска
        out.append("<input type=\"text\" style=\"position: absolute; display: none; font-weight: bold; border-width: 2px;\" id=\"sfd\" value=\"\" />");

        out.append("<SCRIPT  TYPE=\"text/javascript\">");
        out.append("attachKeydownHandlerTT(); setInterval('refreshTaskTable()', ").append(WebController.REFRESH_PERIOD).append(");").append(EOL);
        out.append("</SCRIPT>");

        return out.toString();
    }

    public static String getProcessesJSON(SortedSet<ProcessHelper.ProcessNode> tabs, ResourceBundle resource, ProcessHelper ph, WebSession s) {
        log.info("|USER: " + s.getUserName() + "| in getProcessesJSON");

    	JsonObject res = new JsonObject();
    	JsonArray arr = new JsonArray();
    	res.add("processes", arr);

        for (Iterator<ProcessHelper.ProcessNode> it = tabs.iterator(); it.hasNext(); ) {
            ProcessHelper.ProcessNode p = it.next();
            
        	JsonObject tab = new JsonObject();
        	tab.add("uid", p.getObject().uid);
        	tab.add("title", p.toString(s.getInterfaceLangId(), s));

        	JsonArray childArray = getTabJSON(ph, p);
       		tab.add("children", childArray);
       		
       		arr.add(tab);
        }

        return res.toString();
    }
    
    public static String getFavoriteProcessesJSON(ProcessHelper ph, WebSession s, long langId, List<KrnObject> favouriteProcesses ) {
    	JsonArray arr = new JsonArray();
    	Map<Long, String> helper = s.getCommonHelper().getUserNotOpenProcessDef();
    	for (int i = 0; i < favouriteProcesses.size(); i++) {
    		ProcessNode pn = ph.getProcessByUID(favouriteProcesses.get(i).uid); 
    		JsonObject row = new JsonObject();
            row.add("id", pn.getObject().uid);
            row.add("fontWeight", helper.containsKey(pn.getObject().id));
            row.add("time", helper.get(pn.getObject().id));
        	row.add("title", pn.toString(langId, s));
        	row.add("state", "open");
       		arr.add(row);
    	}
        return arr.toString();
    }
    
    public static String getUsersArray(String text, WebSession s) {
    	JsonObject res = new JsonObject();
    	Kernel krn = s.getKernel();
    	String listOfNames = new String();
    	String listOfUids = new String();
    	try {
    		User user = krn.getUser();
    		String[] scopeUids = user.getScopeUids();
    		if(!user.isLdMapCalculated()) {
    			user.setLdMap(krn.getStringUidMap(scopeUids));
    			user.setLdMapCalculated(true);
    		}
    		Map<String, String> nameUid = user.getLdMap();
    		for(String str: nameUid.keySet()) {
    			if(str != null && str.toUpperCase().contains(text.toUpperCase())) {
    				listOfNames += ";" + str;
    				listOfUids += ";" + nameUid.get(str);
    			}
    		}
    		res.add("iinNames", listOfNames);
    		res.add("uids", listOfUids);

    	} catch(KrnException e) {
    		e.printStackTrace();
    	}
    	return res.toString();
    }
    
    public static String getSearchProcessJSON(String text, ProcessHelper ph, long langId, String index) {
    	JsonObject row = new JsonObject();
    	String listString = "";
    	if("0".equals(index)) {
    		Map<Integer, List<String>> listProcess = ph.searchProcesses(text, langId);
    		if(!listProcess.isEmpty()) {
	    		List<String> list = listProcess.get(0);
	    		for (String str : list)
	    		{
	    		    listString += str + ",";
	    		}
	    		listProcessSave.clear();
	    		listProcessSave.putAll(listProcess);
	    		row.add("parent", listString);
	            row.add("size", listProcess.size());
    		}
    	} else {
    		if(!listProcessSave.isEmpty()) {
    			int idx = Integer.parseInt(index) % listProcessSave.size();
    			List<String> list = listProcessSave.get(idx);
    			for (String str : list)
	    		{
	    		    listString += str + ",";
	    		}
    			row.add("parent", listString);
    		}
    	}
    	return row.toString();
    }

    public static String getProcessFolderJSON(String parentId, boolean loadLeafs, ProcessHelper ph, WebSession s, long langId) {
    	JsonArray arr = new JsonArray();
		
    	Map<Long, String> helper = s.getCommonHelper().getUserNotOpenProcessDef();
    	List<KrnObject> favouriteProcesses = s.getCommonHelper().getUserFavouriteProcesses();
    	if (parentId != null && parentId.length() > 0) {
        	ProcessNode n = ph.getProcessByUID(parentId);
        	Set<ProcessNode> childs;
        	if (loadLeafs) {
        		childs = new TreeSet<ProcessNode>(User.USE_OLD_USER_RIGHTS ? new ProcessNodeComparator(2, langId, s) : null);
        	} else {
        		childs = new TreeSet<ProcessNode>(new ProcessNodeComparator(2, langId, s));
        	}
        	childs.addAll(ph.getChildren(n));
        	for (ProcessNode child : childs) {
	            if (!loadLeafs && child.isFolder()) {
		            JsonObject row = new JsonObject();
		            row.add("id", child.getObject().uid);
		            row.add("fontWeight", helper.containsKey(child.getObject().id));
		            row.add("time", helper.get(child.getObject().id));
		        	row.add("text", child.toString(langId, s));
		        	
		        	boolean hasFolders = ph.hasNonEmptyFolder(child);
		        	row.add("state", hasFolders ? "closed" : "open");
		        	if (!hasFolders) {
    		        	row.add("iconCls", "tree-folder");
		        	}
		        	row.add("parent", parentId);
		       		arr.add(row);
	            } else if (loadLeafs && !child.isFolder()) {
		            JsonObject row = new JsonObject();
		            row.add("id", child.getObject().uid);
		            row.add("fontWeight", helper.containsKey(child.getObject().id));
		            row.add("time", helper.get(child.getObject().id));
		        	row.add("title", child.toString(langId, s));
		        	row.add("state", "open");
		        	row.add("parent", parentId);
		        	String desc = child.getProcDesc();
		        	if (desc != null && desc.length() > 0) {
		        		row.add("procDesc", desc);
		        	}
		        	if (favouriteProcesses.contains(child.getObject()))
		        		row.add("favourite", true);
		        	
		        	arr.add(row);
	            }
        	}
        } else {
        	Set<ProcessNode> childs = new TreeSet<ProcessNode>(new ProcessNodeComparator(User.USE_OLD_USER_RIGHTS ? 2 : 1, langId, s));
        	childs.addAll(ph.getTabs());
        	for (ProcessNode child : childs) {
	            if (!loadLeafs && child.isFolder()) {
		            JsonObject row = new JsonObject();
		            row.add("id", child.getObject().uid);
		        	row.add("text", child.toString(langId, s));
		        	boolean hasFolders = ph.hasNonEmptyFolder(child);
		        	row.add("state", hasFolders ? "closed" : "open");
		        	if (!hasFolders) {
    		        	row.add("iconCls", "tree-folder");
		        	}
		        	row.add("parent", parentId);
		       		arr.add(row);
	            } else if (loadLeafs && !child.isFolder()) {
		            JsonObject row = new JsonObject();
		            row.add("id", child.getObject().uid);
		        	row.add("title", child.toString(langId, s));
		        	row.add("state", "open");
		        	row.add("parent", parentId);
		       		arr.add(row);
	            }
            }        	
        }

        return arr.toString();
    }

    private static JsonArray getTabJSON(ProcessHelper ph, ProcessHelper.ProcessNode p) {
    	JsonArray arr = null;
    	List<ProcessNode> children = ph.getChildren(p);
        if (children != null && children.size() > 0) {
        	arr = new JsonArray();
            for (ProcessNode child : children) {
            	
            	JsonObject res = new JsonObject();
                res.add("uid", child.getObject().uid);
                res.add("title", child.toString());

                boolean hasChildren = ph.getChildren(child) != null;
                
                if (hasChildren) {
                	JsonArray childArray = getTabJSON(ph, child);
               		res.add("children", childArray);
                }
                
                arr.add(res);
            }
        }
        return arr;
    }

    public static String getProcessesHTML(SortedSet<ProcessHelper.ProcessNode> tabs, Long selectedId, ResourceBundle resource, ProcessHelper ph, WebSession s) {
        log.info("|USER: " + s.getUserName() + "| in getProcessesHTML");
        StringBuilder out = new StringBuilder(5 * 1024);
        int configNumber = s.getConfigNumber();
        out.append(ServletUtilities.DOCTYPE).append(EOL);
        out.append("<html><head>").append(EOL);
        out.append("<link rel=\"stylesheet\" href=\"Styles/toc.css?hash="+UpdateContent.tocHash[configNumber]+"\" type=\"text/css\" media=\"screen\">").append(EOL);
        out.append("<SCRIPT  TYPE=\"text/javascript\">");
        out.append("var askConfirm=");
        out.append(WebController.ASK_CONFIRM);
        out.append(";</SCRIPT>");
        out.append(EOL);
        out.append("<SCRIPT  SRC=\"script/tabs.js?hash="+UpdateContent.tabsHash[configNumber]+"\" TYPE=\"text/javascript\"></SCRIPT>");
        out.append(EOL);
        out.append("<SCRIPT  SRC=\"script/processes.js?hash="+UpdateContent.processesHash[configNumber]+"\" TYPE=\"text/javascript\"></SCRIPT>");
        out.append(EOL);
        out.append("</head>");
        out.append(EOL);
        out.append("<body style=\"overflow: auto;\">");
        out.append(EOL);

        out.append("<table style=\"width: 100%;\" cellpadding=\"0\" cellspacing=\"0\" class=\"tabpane\">");
        out.append(EOL);
        out.append("<tr>");
        out.append(EOL);
        out.append("<td>");
        out.append(EOL);
        out.append("<div class=\"tabs\">");
        out.append(EOL);
        out.append("<ul>");
        out.append(EOL);

        long id = 0;

        for (Iterator<ProcessHelper.ProcessNode> it = tabs.iterator(); it.hasNext(); ) {
            ProcessHelper.ProcessNode p = it.next();
            out.append("<li id=\"p:").append(id).append("\" class=\"");
            if (id == selectedId) {
                out.append("selectedTab");
            } else {
                out.append("notselectedTab");
            }

            out.append("\">");
            out.append(EOL);

            out.append("<a href=\"#\"");

            out.append(" onclick=\"selectTab('p', '").append(id).append("');\">");
            out.append("<span>").append(p.toString()).append("</span>");
            out.append("</a>");
            out.append(EOL);

            out.append("</li>");
            out.append(EOL);
            id++;
        }

        out.append("</ul>");
        out.append(EOL);
        out.append("</div>");
        out.append(EOL);

        out.append("</td></tr>");
        out.append(EOL);
        out.append("<tr><td>");
        out.append(EOL);
        out.append("<div class=\"tab\">");
        out.append(EOL);
        id = 0;
        for (Iterator<ProcessHelper.ProcessNode> it = tabs.iterator(); it.hasNext(); ) {
            ProcessHelper.ProcessNode p = it.next();

            out.append("<ul id=\"ul").append(id).append("\" class=\"");
            if (id == selectedId) {
                out.append("Shown\">");
                out.append(EOL);
                getTabHTML(p, ph, resource, out);
                out.append("</ul>");
                out.append(EOL);
            } else {
                out.append("Hidden\">");
                out.append(EOL);
                out.append("</ul>");
                out.append(EOL);
            }
            id++;
        }

        out.append("</div>");
        out.append(EOL);
        out.append("</td></tr></table>");
        out.append(EOL);
        out.append("</body></html>");
        return out.toString();
    }

    public static String getProcessesInnerHTML(SortedSet<ProcessHelper.ProcessNode> tabs, Long selectedId, WebSession s) {
        log.info("|USER: " + s.getUserName() + "| in getProcessesHTML");
        ProcessHelper ph = s.getProcessHelper();
        ResourceBundle resource = s.getResource();
        
        StringBuilder out = new StringBuilder(5 * 1024);

        out.append("<table style=\"width: 100%; position:absolute;\" cellpadding=\"0\" cellspacing=\"0\" class=\"tabpane\">");
        out.append(EOL);
        out.append("<tr>");
        out.append(EOL);
        out.append("<td>");
        out.append(EOL);
        out.append("<div class=\"tabs\">");
        out.append(EOL);
        out.append("<ul>");
        out.append(EOL);

        long id = 0;

        for (Iterator<ProcessHelper.ProcessNode> it = tabs.iterator(); it.hasNext(); ) {
            ProcessHelper.ProcessNode p = it.next();
            out.append("<li id=\"p:").append(id).append("\" class=\"");
            if (id == selectedId) {
                out.append("selectedTab");
            } else {
                out.append("notselectedTab");
            }

            out.append("\">");
            out.append(EOL);

            out.append("<a href=\"#\"");

            out.append(" onclick=\"selectTab('p', '").append(id).append("');\">");
            out.append("<span>").append(p.toString()).append("</span>");
            out.append("</a>");
            out.append(EOL);

            out.append("</li>");
            out.append(EOL);
            id++;
        }

        out.append("</ul>");
        out.append(EOL);
        out.append("</div>");
        out.append(EOL);

        out.append("</td></tr>");
        out.append(EOL);
        out.append("<tr><td>");
        out.append(EOL);
        out.append("<div class=\"tab\">");
        out.append(EOL);
        id = 0;
        for (Iterator<ProcessHelper.ProcessNode> it = tabs.iterator(); it.hasNext(); ) {
            ProcessHelper.ProcessNode p = it.next();

            out.append("<ul id=\"ul").append(id).append("\" class=\"");
            if (id == selectedId) {
                out.append("Shown\">");
                out.append(EOL);
                getTabHTML(p, ph, resource, out);
                out.append("</ul>");
                out.append(EOL);
            } else {
                out.append("Hidden\">");
                out.append(EOL);
                out.append("</ul>");
                out.append(EOL);
            }
            id++;
        }

        out.append("</div>");
        out.append(EOL);
        out.append("</td></tr></table>");
        out.append(EOL);
        return out.toString();
    }

    private static void getTabHTML(ProcessHelper.ProcessNode p, ProcessHelper ph, ResourceBundle resource, StringBuilder out) {
        List<ProcessNode> children = ph.getChildren(p);
        if (children != null) {
            for (ProcessNode child : children) {
                boolean hasChildren = ph.getChildren(child) != null;
                boolean isExpanded = ph.isExpanded(new TreePath(child.getPath()));
                out.append("<li id=\"li" ).append(child.getObject().id).append("\">");
                if (hasChildren) {
                    if (!isExpanded) {
                        out.append("<a onclick=\"expand('").append(child.getObject().id).append("');\">");
                        out.append("<img id=\"img").append(child.getObject().id).append("\" src=\"").append(
                                APP_PATH).append("/images/CloseFolder.gif\" />");
                        out.append(child.toString());
                        out.append("</a>");
                    } else {
                        out.append("<a onclick=\"expand('").append(child.getObject().id).append("');\">");
                        out.append("<img id=\"img").append(child.getObject().id).append("\" src=\"").append(
                                APP_PATH).append("/images/Open.gif\" />");
                        out.append(child.toString());
                        out.append("</a>");
                    }
                } else {
                    out.append("<a onclick=\"askStartProcess('").append(child.getObject().id).append("', '").append(
                            resource.getString("startProcMessage")).append("');\">");
                    out.append("<img src=\"").append(
                                APP_PATH).append("/images/ServiceTab.gif\" />");
                    out.append(child.toString());
                    out.append("</a>");
                }
                out.append("</li>").append(EOL);
                if (hasChildren) {
                    if (!isExpanded) {
                        out.append("<ul id=\"ul").append(child.getObject().id);
                        out.append("\" class=\"Hidden\">").append(EOL);
                        out.append("</ul>").append(EOL);
                    } else {
                        out.append("<ul id=\"ul").append(child.getObject().id);
                        out.append("\" class=\"Shown\">").append(EOL);
                        getTabHTML(child, ph, resource, out);
                        out.append("</ul>").append(EOL);
                    }
                }
            }
        }
    }

    public static String getProcessXML(Map<String, String> args, ProcessHelper ph, ResourceBundle resource) {
        String pid = args.get("id");
        long id = Long.parseLong(pid);
        String wait = args.get("wait");
        ph.expand(id);

        StringBuilder out = new StringBuilder(2048);

        if (wait == null) {
            out.append("<r>");

            out.append("<id>");
            out.append(pid);
            out.append("</id>");
            out.append("<data>");

            ProcessHelper.ProcessNode p = ph.getProcessById(id);

            getTabHTML(p, ph, resource, out);
            out.append("</data>");

            out.append("</r>");
        }

        return out.toString();
    }

    public static String getTabXML(long id, SortedSet<ProcessNode> tabs, ResourceBundle resource, ProcessHelper ph) {
    	StringBuilder out = new StringBuilder(2048);

        out.append("<r>");

        out.append("<id>");
        out.append(id);
        out.append("</id>");
        out.append("<data>");
        Iterator<ProcessNode> it = tabs.iterator();
        ProcessHelper.ProcessNode p = null;
        for (int i=0; i<=id; i++) p = it.next();

        getTabHTML(p, ph, resource, out);
        out.append("</data>");

        out.append("</r>");

        return out.toString();
    }

    public static String getStartProcessXML(Map<String, String> args) {
        String pid = args.get("pid");
        StringBuilder out = new StringBuilder(50);
        out.append("<num>");
        out.append(pid);
        out.append("</num>");
        return out.toString();
    }

    public static String getArchiveHTML(ArchiveHelper ah, boolean dict, Integer sid, Long lid, int configNumber) {
    	StringBuilder out = new StringBuilder(12 * 1024);
/*        out.append(ServletUtilities.DOCTYPE).append(EOL);
        out.append("<html><head>").append(EOL);
        out.append("<link rel=\"stylesheet\" href=\"Styles/toc.css?hash="+UpdateContent.tocHash[configNumber]+"\" type=\"text/css\" media=\"screen\">").append(EOL);
        out.append("<SCRIPT  SRC=\"script/archive.js?hash="+UpdateContent.archiveHash[configNumber]+"\"" +
                " TYPE=\"text/javascript\"></SCRIPT>").append(EOL);
        out.append("</head>").append(EOL);
        out.append("<body style=\"overflow: auto;\">").append(EOL);

        out.append("<ul>");
        if (dict)
            ah.getArchiveNodeHTML(ah.getDictRoot(), dict, sid, lid, out);
        else
            ah.getArchiveNodeHTML(ah.getRoot(), dict, sid, lid, out);

        out.append("</ul>");
        out.append("</body></html>");
*/        return out.toString();
    }

    public static String getArchiveNodeXML(Map<String, String> args, ArchiveHelper ah, boolean dict, Integer sid, Long lid) {
        String pid = args.get("id");
        String wait = args.get("wait");
        return null;
    }

    public static String getActiveTasksJSON(WebTable table, WebSession s) {
    	return table.getJSON(s);
    }
    
    public static String getUserSessionsJSON(UserSessionValue[] uss, WebSession s) {
    	JsonObject res = new JsonObject();
    	JsonArray arr = new JsonArray();
    	res.add("processes", arr);

    	if (uss != null) {
    		ThreadLocalDateFormat sdf = ThreadLocalDateFormat.get("dd.MM.yyyy HH:mm:ss");
    		for (int i = uss.length - 1; i>=0; i--) {
    			UserSessionValue us = uss[i];
            	
    			JsonObject r = new JsonObject();
            	JsonObject process = new JsonObject();
            	process.add("p", us.name);
            	process.add("t", us.typeClient);
            	process.add("o", us.ip);
            	process.add("d", sdf.format(us.startTime));

            	r.add(us.id.toString(), process);
            	
            	arr.add(r);
    		}
    	}
        
        return res.toString();
    }

    public static String getActiveTasksCountJSON(WebTable table, WebSession s) {
    	return table.getCountJSON(s);
    }

    public static String getTaskTableHTML(WebTable table, int configNumber) {
        StringBuilder out = new StringBuilder(2048);
        out.append(ServletUtilities.DOCTYPE).append(EOL);
        out.append("<html>").append(EOL);
        out.append("<head>").append(EOL);
        out.append("<link rel=\"stylesheet\" href=\"Styles/toc.css?hash="+UpdateContent.tocHash[configNumber]+"\" type=\"text/css\" media=\"screen\">").append(EOL);
        out.append("<SCRIPT  TYPE=\"text/javascript\">");
        out.append("var askConfirm=");
        out.append(WebController.ASK_CONFIRM);
        out.append(";</SCRIPT>");
        out.append(EOL);
        out.append("<SCRIPT  SRC=\"script/tasks.js?hash="+UpdateContent.tasksHash[configNumber]+"\"" +
                " TYPE=\"text/javascript\"></SCRIPT>").append(EOL);
        out.append("</head>").append(EOL);
/* nagruzka */
        out.append("<body onload=\"attachKeydownHandlerTT(); setInterval('refreshTaskTable()', ").append(WebController.REFRESH_PERIOD).append(");\" style=\"overflow: auto;\">").append(EOL);
//        out.append("<body onload=\"attachKeydownHandlerTT();\" style=\"overflow: auto;\">").append(EOL);
/* nagruzka */
        table.getHTML(out);
        // Поле для поиска
        out.append("<input type=\"text\" style=\"position: absolute; display: none; font-weight: bold; border-width: 2px;\" id=\"sfd\" value=\"\" />");
        
        out.append("<!--[if lte IE 6.5]><iframe id=\"ifone\" style=\"display: none;\"></iframe><![endif]--><div class=\"fone\" style=\"width: 100%; height: 100%;" +
                " left: 0; top: 0; display: none;\" id=\"fone\"></div>");

        out.append("</body></html>");
        return out.toString();
    }

    public static String getAfterStartProcessXML(Object res, WebTable table, boolean isMonitorTask) {
        StringBuilder out = new StringBuilder(1024);
        out.append("<r>");
        if (res instanceof String) {
            out.append("<alert>");
            out.append(res);
            out.append("</alert>");
        } else if (res instanceof Activity && isMonitorTask) {
            int row = table.getRowForObject(res);
            table.getRowHTML(row, out);
        }
        out.append("</r>");
        return out.toString();
    }

    public static String getStartProcessJSON(Object obj, WebTable table, boolean isMonitorTask) {
        JsonObject res = new JsonObject();
        if (obj instanceof String) {
            res.add("result", "error");
            res.add("message", Funcs.xmlQuote((String)obj));
        } else if (obj instanceof Activity && isMonitorTask) {
            res.add("result", "success");
        }
        return res.toString();
    }

    public static String getRefreshTaskTableXML(WebTable table, WebSession webSession) {
    	StringBuilder out = new StringBuilder(2048);
        out.append("<r>");
        List<Activity> autoStartedRows = new ArrayList<Activity>(table.getAutoStartedRows());
        table.getAutoStartedRows().clear();
        if (autoStartedRows.size() > 0) {
            out.append("<auto>");
            Dimension size;
            for (int i = 0; i < autoStartedRows.size(); i++) {
                Activity a = autoStartedRows.get(i);
                size = null;
                if (a!=null) {
                    size = ((OrWebPanel)webSession.getFrameManager().absolute2(a.ui, null).getPanel()).getPrefSize();
                }
                if (table.getRowForObject(a) > -1) {
                    table.getRowHTML(table.getRowForObject(a), out, size);
                }
            }
            out.append("</auto>");
        }
        List<Activity> openUIRows = new ArrayList<Activity>(table.getOpenUIRows());
        table.getOpenUIRows().clear();
        if (openUIRows.size() > 0) {
            for (int i = 0; i < openUIRows.size(); i++) {
                out.append("<openUI>");
                Activity a = openUIRows.get(i);
                out.append(a.flowId);
                out.append("</openUI>");
            }
        }
        out.append("</r>");
        return out.toString();
    }

    public static String changePasswordDialog(Map<String, String> args, WebSession s, ResourceBundle bundle) {
        StringBuilder res = new StringBuilder(2048);
        res.append("<form>");
        res.append("<table width='100%' height='100%'>");
        res.append("<tr>");
        res.append("<td align='right'>");
        res.append("<table  width='100%'>");
        res.append("<tr valign='top'>");
        res.append("<td style='vertical-align: middle;' align='right'>");
        res.append("<div style='text-decoration: none;' class='tt'>").append(bundle.getString("oldPass")).append("</div>");
        res.append("</td>");
        res.append("<td align='left'>");
        res.append("<input style='margin:2px 2px 0px 5px;' name='oldpass' id='oldpass' type='password' width='20'>");
        res.append("</td>");
        res.append("</tr>");
        res.append("<tr valign='top'>");
        res.append("<td style='vertical-align: middle;' align='right'>");
        res.append("<div style='text-decoration: none;' class='tt'>").append(bundle.getString("newPass")).append("</div>");
        res.append("</td>");
        res.append("<td align='left'>");
        res.append("<input style='margin:2px 2px 0px 5px;' name='newpass' id='newpass' type='password' width='20'>");
        res.append("</td>");
        res.append("</tr>");
        res.append("<tr valign=\"top\">");
        res.append("<td style='vertical-align: middle;' align='right'>");
        res.append("<div style='text-decoration: none;' class='tt'>").append(bundle.getString("confPass")).append("</div>");
        res.append("</td>");
        res.append("<td align=\"left\">");
        res.append("<input style='margin:2px 2px 2px 5px;' name='confirm' id='confirm' type='password' width='20'>");
        res.append("</td>");
        res.append("</tr>");
        res.append("</table>");
        res.append("</td>");
        res.append("</tr>");
        res.append("</table></form>");

        return res.toString();
    }

    public static String changePassword(Map<String, String> args, WebSession s, ResourceBundle bundle) {
        String oldPass = args.get("old");
        String newPass = args.get("new");
        String confirmPass = args.get("confirm");

        StringBuilder res = new StringBuilder("<r>");
        
        PathWordChange.Message mess = null;
        if (newPass != null) {
            try {
                mess = PathWordChange.changePassword(newPass.toCharArray(),
                       confirmPass.toCharArray(), oldPass.toCharArray(),
                       s.getKernel(), bundle);
            } catch (KrnException e) {
                mess = new PathWordChange.Message("error", MessagesFactory.ERROR_MESSAGE);
            }
            res.append("<alert>");
            res.append(mess.getMessage());
            res.append("</alert>");
            res.append("<code>");
            res.append(mess.getCode());
            res.append("</code>");
        }
        res.append("</r>");
        return res.toString();
    }

    public static JsonObject changePasswordJSON(Map<String, String> args, WebSession s, ResourceBundle bundle) {
        String oldPass = args.get("oldPass");
        String newPass = args.get("newPass");
        String confirmPass = args.get("confirmPass");

        JsonObject res = new JsonObject();
        
        PathWordChange.Message mess = null;
        if (newPass != null) {
            try {
                mess = PathWordChange.changePassword(newPass.toCharArray(),
                       confirmPass.toCharArray(), oldPass.toCharArray(),
                       s.getKernel(), bundle);
                
                if (mess.getType() == MessagesFactory.INFORMATION_MESSAGE)
                	res.add("result", "success");
                else
                    res.add("result", "error");
            } catch (KrnException e) {
                res.add("result", "error");
                mess = new PathWordChange.Message("error", MessagesFactory.ERROR_MESSAGE);
            }
            res.add("message", mess.getMessage().replace("<br />", "\n").replaceFirst("^\\!", ""));
        }
        return res;
    }

    public static String getShowUsers(WebSession s, ResourceBundle bundle) {
        boolean themeD = "daulet".equals(WebController.THEME);
    	StringBuilder res = new StringBuilder(5 * 1024);
    	int configNumber = s.getConfigNumber();
        res.append("<html><head>");
        res.append("<title>").append(bundle.getString("usersShort")).append("</title>");
        res.append("<link rel=\"stylesheet\" href=\"Styles/toc.css?hash="+UpdateContent.tocHash[configNumber]+"\" type=\"text/css\" media=\"screen\" />");
        res.append("<SCRIPT  SRC=\"script/users.js?hash="+UpdateContent.usersHash[configNumber]+"\" TYPE=\"text/javascript\"></SCRIPT>");
        res.append("</head>");
        res.append("<body class=\"frm\">");

        res.append("<table class=\"out\" width=\"100%\" height=\"100%\">");
        res.append("<tr>");
        res.append("<td class=\"hout\" height=\"20\">");
        res.append("<div class=\"ht\">").append(bundle.getString("usersShort")).append("</div>");
        res.append("</td>");
        res.append("</tr>");
        res.append("<tr valign=\"top\">");
        res.append("<td align=\"center\">");

        res.append("<table class=\"in\" id=\"users\" cellpadding=\"4\" width=\"100%\">");
        try {
            Map<Integer,  WebSession> sessions = WebSessionManager.getSessions();
            if (sessions != null) {
                int row = 0;
                for (WebSession session : sessions.values()) {
                    res.append("<tr id=\"").append(session.getId()).append("\">");
                    res.append("<td>");
                    res.append(session.getUserName());
                    res.append("</td>");
                    res.append("<td width=\"20\" align=\"center\">");
                    res.append("<a class=\"menuBtn\" onmouseover=\"menuBtnOver(this);\" onmouseout=\"menuBtnOut(this);\" onclick=\"deleteUser(").append(session.getId()).append(");\" id=\"rlb\"><img src=\"").append(
                                            APP_PATH).append(themeD?"/images/Cancel32-d.png\" alt=\"":"/images/Cancel32.png\" alt=\"").append(
                            bundle.getString("deleteUser")).append("\"/> ").append("</a>").append(EOL);
                    res.append("</td>");
                    res.append("</tr>");
                    row++;
                }
            }
        } catch (Exception e) {e.printStackTrace();}
        res.append("</table>");

        res.append("</td>");
        res.append("</tr>");
        res.append("<tr>");
        res.append("<td class=\"hout\" align=\"right\" height=\"20\">");
        res.append("<a class=\"tool\" onclick=\"" +
                   "window.close();\">Ok</a>").append(EOL);
        res.append("</td>");
        res.append("</tr>");
        res.append("</table>");

        res.append("</body>");
        res.append("</html>");

        return res.toString();
    }

    public static String getNeedTitleXML(String id) {
        StringBuffer out = new StringBuffer();
        out.append("<r>");
        out.append("<id>");
        out.append(id);
        out.append("</id>");
        out.append("</r>");
        return out.toString();
    }

    public static String getNeedTitleHTML(String id, ResourceBundle bundle, int configNumber) {
        StringBuffer res = new StringBuffer();
        res.append("<html>").append(EOL).append(
                   "<head>").append(EOL).append(
                   "<link rel=\"stylesheet\" href=\"Styles/toc.css?hash="+UpdateContent.tocHash[configNumber]+"\" type=\"text/css\" media=\"screen\">").append(EOL).append(
                   "<SCRIPT  SRC=\"script/password.js?hash="+UpdateContent.pdHash[configNumber]+"\"").append(
                   " TYPE=\"text/javascript\"></SCRIPT>").append(EOL).append(
                   "</head>").append(EOL).append(
                   "<body class=\"frm\">").append(EOL);
        res.append("<form>");
        res.append("<table class=\"out\" width=\"100%\" height=\"100%\">");
        res.append("<tr>");
        res.append("<td class=\"hout\" height=\"20\">");
        res.append("<div class=\"ht\">").append(bundle.getString("createNodeTitle")).append("</div>");
        res.append("</td>");
        res.append("</tr>");

        res.append("<tr valign=\"top\">");
        res.append("<td align=\"right\">");
        res.append("<table class=\"out\" width=\"100%\">");
        res.append("<tr valign=\"top\">");
        res.append("<td align=\"right\">");
        res.append("<div class=\"tt\">").append(bundle.getString("enterElementName")).append("</div>");
        res.append("</td>");
        res.append("<td align=\"left\">");
        res.append("<input name=\"newtl\" type=\"text\" width=\"20\">");
        res.append("</td>");
        res.append("</tr>");
        res.append("</table>");
        res.append("</td>");
        res.append("</tr>");

        res.append("<tr>");
        res.append("<td colspan=\"2\" class=\"hout\" align=\"right\" height=\"20\">");
        res.append("<a class=\"tool\" onclick=\"" +
                   "opener.addNodeTitle('").append(id).append("', document.forms[0].newtl.value); " +
                   "window.close();"+
                   "\">").append(bundle.getString("ok")).append("</a>").append(EOL);
        res.append("<a class=\"tool\" onclick=\"" +
                   "window.close();"+
                   "\">").append(bundle.getString("cancel")).append("</a>").append(EOL);
        res.append("</td>");
        res.append("</tr>");

        res.append("</table>");
        res.append("</form>");
        res.append("</body>");
        res.append("</html>");

        return res.toString();
    }

    public static String getDatesPanelXml(String id, long flags) {
        StringBuffer res = new StringBuffer();
        res.append("<r>");
        res.append("<openWindow>");
        res.append("<address>");
        res.append(Funcs.xmlQuote(APP_PATH + "/main?trg=frm&cmd=fdp&id=" +
                                  id + "&fs=" + flags));
        res.append("</address>");
        res.append("<width>300</width>");
        res.append("<height>160</height>");
        res.append("</openWindow>");
        res.append("</r>");
        return res.toString();
    }

    public static String getLangChangedXml() {
        StringBuffer res = new StringBuffer();
        res.append("<r>");
        res.append("</r>");
        return res.toString();
    }

    public static String getDatesPanelHTML(String id, long flags, ResourceBundle resource, int configNumber) {
        StringBuffer res = new StringBuffer();
        res.append("<html>").append(EOL)
                .append("<head>")
                .append(EOL)
                .append("<link rel=\"stylesheet\" href=\"Styles/toc.css?hash="+UpdateContent.tocHash[configNumber]+"\" type=\"text/css\" media=\"screen\">")
                .append(EOL)
                .append("<SCRIPT  SRC=\"script/dates.js?hash="+UpdateContent.datesHash[configNumber]+"\" TYPE=\"text/javascript\"></SCRIPT>").append(EOL)
                .append("</head>").append(EOL)
                .append("<body class=\"frm\">").append(EOL);
        res.append("<form>");
        res.append("<table class=\"out\" width=\"100%\" height=\"100%\">");
        res.append("<tr>");
        res.append("<td class=\"hout\" height=\"20\">");
        res.append("<div class=\"ht\">").append(resource.getString("filterDatesTitle")).append("</div>");
        res.append("</td>");
        res.append("</tr>");

        res.append("<tr valign=\"top\">");
        res.append("<td align=\"right\">");
        res.append("<table class=\"out\" width=\"100%\">");

        // Инициализация компонента для ввода начала периода
        String title = (resource != null)
                    ? resource.getString("filterDatesBegin")
                    : "Начало периода";
        if ((flags & 0x02) > 0) {
            res.append("<tr valign=\"top\">");
            res.append("<td align=\"right\">");
            res.append("<div class=\"tt\">").append(title).append("</div>");
            res.append("</td>");
            res.append("<td align=\"left\">");
            res.append("<input name=\"fd\" type=\"text\" width=\"20\" value=\"").append(WebDateField.MASK_TABLE).append("\"");
            res.append(" onFocus=\"moveStart();\" onKeyDown=\"return formatDate(this, event);\" />");
            res.append("</td>");
            res.append("</tr>");
        }

        // Инициализация компонента для ввода конца периода
        title = (res != null)
                    ? resource.getString("filterDatesEnd")
                    : "Конец периода";
        if ((flags & 0x04) > 0) {
            res.append("<tr valign=\"top\">");
            res.append("<td align=\"right\">");
            res.append("<div class=\"tt\">").append(title).append("</div>");
            res.append("</td>");
            res.append("<td align=\"left\">");
            res.append("<input name=\"ld\" type=\"text\" width=\"20\" value=\"").append(WebDateField.MASK_TABLE).append("\"");
            res.append(" onFocus=\"moveStart();\" onKeyDown=\"return formatDate(this, event);\" />");
            res.append("</td>");
            res.append("</tr>");
        }

        // Инициализация компонента для ввода текущей даты
        title = (res != null)
                    ? resource.getString("filterDatesCurrent")
                    : "Текущая дата";
        if ((flags & 0x01) > 0) {
            res.append("<tr valign=\"top\">");
            res.append("<td align=\"right\">");
            res.append("<div class=\"tt\">").append(title).append("</div>");
            res.append("</td>");
            res.append("<td align=\"left\">");
            res.append("<input name=\"cd\" type=\"text\" width=\"20\" value=\"").append(WebDateField.MASK_TABLE).append("\"");
            res.append(" onFocus=\"moveStart();\" onKeyDown=\"return formatDate(this, event);\" />");
            res.append("</td>");
            res.append("</tr>");
        }

        res.append("</table>");
        res.append("</td>");
        res.append("</tr>");

        res.append("<tr>");
        res.append("<td colspan=\"2\" class=\"hout\" align=\"right\" height=\"20\">");
        res.append("<a class=\"tool\" onclick=\"").append(
                   "openReportWithDates('").append(id).append("'); ").append(
                   "window.close();").append(
                   "\">").append(resource.getString("ok")).append("</a>").append(EOL);
        res.append("<a class=\"tool\" onclick=\"").append(
                   "window.close();").append(
                   "\">").append(resource.getString("cancel")).append("</a>").append(EOL);
        res.append("</td>");
        res.append("</tr>");

        res.append("</table>");
        res.append("</form>");
        res.append("</body>");
        res.append("</html>");

        return res.toString();
    }

    public static String getAlertXml(String msg) {
        StringBuffer b = new StringBuffer();
        b.append("<r>");
        if (msg != null) {
            b.append("<fatal>");
            b.append(Funcs.xmlQuote(msg));
            b.append("</fatal>");
        }
        b.append("</r>");
        return b.toString();
    }

    public static JsonObject getAlertJSON(String msg) {
        JsonObject obj = new JsonObject();
        if (msg != null) {
            obj.add("result", "error");
            obj.add("message", msg.replaceFirst("^\\!", ""));
            obj.add("session", "off");
        }
        return obj;
    }

/*    public static String getAlertHTML(String msg, Map<String, String> params) {
        StringBuffer res = new StringBuffer();
        res.append("<html>").append(EOL).append(
                   "<head>").append(EOL).append(
                   "<link rel=\"stylesheet\" href=\"Styles/main.css\" type=\"text/css\" media=\"screen\"/>").append(EOL).append(
                   "</head>").append(EOL).append(
                   "<body bgcolor=\"#c0c0c0\">").append(EOL);

        res.append("<form target=\"_top\" name=\"logonForm\" action=\"").append(APP_PATH).append("/main\" method=\"post\">");
        res.append("<table width=\"100%\" height=\"100%\">");
        res.append("<tr>");
        res.append("<td align=\"center\" valign=\"center\">");
        res.append("<TABLE border=\"0\" width=\"361\" height=\"220\" align=\"center\" background=\"images/LoginBox.jpg\">");
        res.append("<TR height=\"50%\">");
        res.append("<td colspan=\"3\" heigth=\"50%\"></td></TR>");
        res.append("</TR>");
        res.append("<TR>");
        res.append("<TH align=\"right\" width=\"50%\">Пользователь:</TH>");
        res.append("<TD align=\"left\" width=\"50%\" colspan=\"2\"><input type=\"text\" name=\"name\" value=\"").append(params.get("name")).append("\" class=\"login\" style=\"width: 170px\"></TD>");
        res.append("</TR>");
        res.append("<TR>");
        res.append("<TH align=\"right\">Пароль:</TH>");
        res.append("<TD align=\"left\" colspan=\"2\"><input type=\"password\" name=\"passwd\" value=\"\" class=\"login\" style=\"width: 170px\"></TD>");
        res.append("</TR>");
        res.append("<TR>");
        res.append("<td></td>");
        res.append("<TD align=\"right\"><input type=\"submit\" name=\"submit\" value=\"OK\"></TD>");
        res.append("<TD align=\"left\"><input type=\"reset\" name=\"reset\" value=\"Отмена\" class=\"test\"></TD>");
        res.append("</TR>");
        res.append("<TR height=\"50%\" valign=\"bottom\">");
        res.append("<td colspan=\"3\" heigth=\"50%\" align=\"center\" class=\"error\">").append(msg).append("</td>");
        res.append("</TR>");
        res.append("</TABLE>");
        res.append("</td>");
        res.append("</tr>");
        res.append("</table>");
        res.append("<input type=\"hidden\" name=\"trg\" value=\"top\">");
        res.append("<input type=\"hidden\" name=\"bp\" value=\"").append(WebController.BACK_PAGE).append("\">");
        res.append("</form>");

        res.append("</body>");
        res.append("</html>");

        return res.toString();
    }
*/
    public static String getAlertHTML(String msg, Map<String, String> params, int configNumber) {
        StringBuffer res = new StringBuffer();
        res.append("<html><head>");
        res.append("<meta http-equiv='Content-Type' content='text/html; charset=utf-8'>");
        res.append("<title>Авторизация</title>");
        res.append("<link href='login.css");
        if (configNumber != -1) {
            res.append("?hash=").append(UpdateContent.loginHash[configNumber]);
        }
        res.append("' rel='StyleSheet' type='text/css'>");
        res.append("</head>");
        res.append("<body bgcolor='#F0F8FF' style='margin:0'>");
        res.append("<form name='logonForm' action=\"").append(APP_PATH)
                .append("/main\" method='post' onSubmit='login(); return false;' onReset='resetForm();'>");
        res.append("<table style='width:100%;height:100%;' cellpadding='0' cellspacing='0' border='0'>");
        res.append("<tr>");
        res.append("<td style='height:100%;' align='center' valign='middle'>");
        res.append("<table border='0' cellpadding='0' cellspacing='5' class='designLoginPageForm'");
        res.append(" style='width:300px;height: 212px; background-image:url(loginBox.png);background-repeat:no-repeat;'>");
        res.append("<tr>");
        res.append("<td valign='middle' style='height:140px'>");
        res.append("<table width='100%' border='0' cellpadding='2' cellspacing='1'>");
        res.append("<tr height='40'>");
        res.append("<td width='30%' rowspan='6' align='center' valign='middle' nowrap>");
        res.append("<img src='key2.png' width='60' height='82' border='0' align='bottom' />");
        res.append("</td>");
        res.append("<td align='center' valign='middle' nowrap>&nbsp;</td>");
        res.append("</tr>");
        res.append("<tr>");
        res.append("<td align='left' valign='bottom' nowrap><label class='label'>Имя пользователя:</label></td>");
        res.append("</tr>");
        res.append("<tr>");
        res.append("<td align='center' valign='top'><input type='text' name='name' value='' class='input'/></td>");
        res.append("</tr>");
        res.append("<tr>");
        res.append("<td align='left' valign='middle'><label class='label'>Пароль:</label></td>");
        res.append("</tr>");
        res.append("<tr>");
        res.append("<td align='center' valign='middle'><input type='password' name='passwd' class='input'/></td>");
        res.append("</tr>");
        res.append("<tr>");
        res.append("<td align='center' valign='middle'><input name='submit' type='submit' class='button' value='Вход'/><input name='reset' type='reset' class='button' value='Очистить'/></td>");
        res.append("</tr>");
        res.append("<TR valign='bottom' height='40'>");
        res.append("<td id='error' colspan='2' align='center' class='error'>").append(msg).append("</td>");
        res.append("</TR>");
        res.append("</table>");
        res.append("</td>");
        res.append("</tr>");
        res.append("</table>");
        res.append("</td>");
        res.append("</tr>");
        res.append("<tr>");
        res.append("</tr>");
        res.append("</table>");
        res.append("<input type='hidden' name='trg' value='top'>");
        res.append("<input type='hidden' name='bp' value=\"").append(WebController.BACK_PAGE).append("\">");
        res.append("<input type='hidden' name='windowName' value=''>");
        res.append("</form>");
        res.append("<script language='JavaScript' type='text/javascript'>");
        res.append(EOL);
        res.append("<!--");
        res.append(EOL);
        res.append("document.forms[\"logonForm\"].elements[\"name\"].focus();");
        res.append(EOL);
        res.append("function resetForm() {");
        res.append("document.forms[\"logonForm\"].elements[\"submit\"].disabled = false;");
        res.append("return true;");
        res.append("} ");
        res.append(EOL);
        res.append("function login() {");
        res.append("document.forms[\"logonForm\"].elements[\"windowName\"].value = 'Or3Frame' + (new Date).getTime();");
        res.append("document.forms[\"logonForm\"].elements[\"submit\"].disabled = true;");
        res.append("document.getElementById(\"error\").innerHTML = \"Идет авторизация. Пожалуйста, подождите...\";");
        res.append("var un = document.forms[\"logonForm\"].elements[\"name\"].value;");
        res.append("var pwd = document.forms[\"logonForm\"].elements[\"passwd\"].value;");
        res.append("var url = \"").append(APP_PATH).append("/main\";");
        res.append("var post = \"xml=1&name=\" + un + \"&passwd=\" + pwd;");
        res.append("post += \"&noCache=\" + (new Date).getTime();");
        res.append("if (window.XMLHttpRequest) {");
        res.append("req = new XMLHttpRequest();");
        res.append("} else if (window.ActiveXObject) {");
        res.append("req = new ActiveXObject(\"Microsoft.XMLHTTP\");");
        res.append("}");

        res.append("req.open(\"POST\", url, true);");
        res.append("req.setRequestHeader(\"Content-Type\", \"application/x-www-form-urlencoded\");");
        res.append("req.setRequestHeader(\"Content-Length\", post.length);");
        res.append("req.onreadystatechange = function() { processLogin(req); };");
        res.append("req.send(post);");
        res.append("} ");
        res.append(EOL);

        res.append("function processLogin(req)");
        res.append("{");
        res.append("if (req.readyState == 4) {");
        res.append("if (req.status == 200) {");
        res.append("var d = document;");
        res.append("var responseTag = req.responseXML.getElementsByTagName(\"r\")[0];");
        res.append("var tag = responseTag.getElementsByTagName(\"status\")[0];");
        res.append("if (tag != null) {");
        res.append("var id = tag.childNodes[0].nodeValue;");
        res.append("if (id == \"1\") {");
        res.append("var url = \"").append(APP_PATH).append("/main?trg=top&bp=")
                .append(WebController.BACK_PAGE.replaceAll("/", "%2F")).append("\";");
        res.append("url += \"&noCache=\" + (new Date).getTime();");
        res.append("location.assign(url);");
        res.append("}");
        res.append("} else {");
        res.append("var tag = responseTag.getElementsByTagName(\"fatal\")[0];");
        res.append("if (tag != null) {");
        res.append("var id = (tag.childNodes.length > 0) ? tag.childNodes[0].nodeValue : \"Неизвестная ошибка! Пожалуйста, обратитесь к разработчику\";");
        res.append("d.getElementById(\"error\").innerHTML = id;  	    	");
        res.append("d.forms[\"logonForm\"].elements[\"submit\"].disabled = false;");
        res.append("}}}}}");
        res.append(EOL);
        res.append(" // -->");
        res.append("</script>");
        res.append("</BODY></HTML>");
        return res.toString();
    }

    public static String getFotoPanelHTML(String id, String row, String col, WebSession session) {
        StringBuilder res = new StringBuilder(1024);
        res.append(ServletUtilities.DOCTYPE).append(EOL);

        res.append("<div id='modalBody' style='text-align: center; ");
        if (session.GRADIENT_MAIN_FRAME != null) {
            res.append("background: ").append(session.getIDCoreBrowser()).append(" linear-gradient(").append(WebUtils.gradientToString(session.GRADIENT_MAIN_FRAME)).append("); ");
        }
        res.append("width:350 px; heigtn:300px;'>").append(EOL);

        res.append("<form enctype='multipart/form-data' target='frm").append(id).append("' method='post' action=\"").append(APP_PATH).append("/main?id=").append(id);
        if (row != null && col != null) {
        	res.append("&row=").append(row).append("&col=").append(col);
        }
        res.append("\">");
        res.append("<p>Выберите файл</p>");
        res.append("<input type='file' name='f'  />");//style=\"width: 150px; height: 25px;\"
        
        res.append("<p><input type=\"submit\" value='Загрузить' title = \"Загрузить\"></p>");
        res.append("</form>");
        
        res.append("<IFRAME name=\"frm").append(id).append("\"></IFRAME>");
        res.append("</div>");
        
        return res.toString();
    }

    public static String getFotoPanelHTMLOld(String id, String row, String col, ResourceBundle resource, int configNumber) {
        StringBuffer res = new StringBuffer();
        res.append("<html>").append(EOL)
                .append("<head>")
                .append(EOL).append("<link rel=\"stylesheet\" href=\"Styles/toc.css?hash="+UpdateContent.tocHash[configNumber]+"\" type=\"text/css\" media=\"screen\" />")
                .append(EOL)
                .append("</head>")
                .append(EOL)
                .append("<body class=\"frm\">").append(EOL);
        res.append("<form enctype=\"multipart/form-data\" " +
                "target=\"frm").append(id).append("\" method=\"post\" action=\"").append(APP_PATH)
                .append("/main?id=").append(id);
        if (row != null && col != null)
        	res.append("&row=").append(row).append("&col=").append(col);
        
        res.append("\">");
        res.append("<table class=\"out\" width=\"100%\" height=\"100%\">");
        res.append("<tr>");
        res.append("<td class=\"hout\" height=\"20\">");
        res.append("<div class=\"ht\">").append("ФАЙЛ").append("</div>");
        res.append("</td>");
        res.append("</tr>");

        res.append("<tr valign=\"top\">");
        res.append("<td align=\"right\">");
        res.append("<table class=\"out\" width=\"100%\">");
        res.append("<tr valign=\"top\">");
        res.append("<td align=\"center\">");
        res.append("<input type=\"file\" name=\"f\" />");
        res.append("</td>");
        res.append("</tr>");
        res.append("<tr valign=\"top\">");
        res.append("<td align=\"center\">");
        res.append("<input style=\"height: 20px;\" type=\"submit\" value=\"Загрузить на сервер\" name=\"b\" />");
        res.append("</td>");
        res.append("</tr>");

        res.append("</table>");
        res.append("</td>");
        res.append("</tr>");
        res.append("</table>");        
        res.append("</form>");
        res.append("<IFRAME name=\"frm").append(id).append("\"></IFRAME>");
        res.append("</body>");
        res.append("</html>");

        return res.toString();
    }

    public static String getDocPanelHTML(String id, ResourceBundle resource, int configNumber) {
        StringBuffer res = new StringBuffer();
        res.append("<html>").append(EOL).append(
                   "<head>").append(EOL).append(
                   "<link rel=\"stylesheet\" href=\"Styles/toc.css?hash="+UpdateContent.tocHash[configNumber]+"\" type=\"text/css\" media=\"screen\" />").append(EOL).append(
                   "</head>").append(EOL).append(
                   "<body class=\"frm\">").append(EOL);
        res.append("<form enctype=\"multipart/form-data\" target=\"frm").append(id)
                .append("\" method=\"post\" action=\"").append(APP_PATH).append("/main?id=").append(id).append("\">");
        res.append("<table class=\"out\" width=\"100%\" height=\"100%\">");
        res.append("<tr>");
        res.append("<td class=\"hout\" height=\"20\">");
        res.append("<div class=\"ht\">").append("ДОКУМЕНТ").append("</div>");
        res.append("</td>");
        res.append("</tr>");

        res.append("<tr valign=\"top\">");
        res.append("<td align=\"right\">");
        res.append("<table class=\"out\" width=\"100%\">");
        res.append("<tr valign=\"top\">");
        res.append("<td align=\"center\">");
        res.append("<input type=\"file\" name=\"f\" />");
        res.append("</td>");
        res.append("</tr>");
        res.append("<tr valign=\"top\">");
        res.append("<td align=\"center\">");
        res.append("<input type=\"submit\" value=\"Загрузить на сервер\" name=\"b\" />");
        res.append("</td>");
        res.append("</tr>");

        res.append("</table>");
        res.append("</td>");
        res.append("</tr>");
        res.append("</table>");
        res.append("</form>");
        res.append("<IFRAME name=\"frm").append(id).append("\"></IFRAME>");
        res.append("</body>");
        res.append("</html>");

        return res.toString();
    }

    public static void getOptionDialogXml(Activity act, StringBuilder b) {
        b.append("<optionPane>");
        b.append("<flow>");
        b.append(act.flowId);
        b.append("</flow>");
        b.append("<width>500</width>");
        b.append("<height>");
        b.append(100 + act.transitions.length*20);
        b.append("</height>");
        b.append("</optionPane>");
    }

    public static String getOptionDialogHTML(Activity act, ResourceBundle resource, int configNumber) {
        String[] trs = new String[act.transitions.length];

        StringBuilder res = new StringBuilder(2048);
        res.append("<html><head>").append(EOL);
        res.append("<link rel=\"stylesheet\" href=\"Styles/toc.css?hash=").append(UpdateContent.tocHash[configNumber]).append("\" type=\"text/css\" media=\"screen\" />").append(EOL);
        res.append("<SCRIPT TYPE='text/javascript' SRC='script/dates.js?hash=").append(UpdateContent.datesHash[configNumber]).append("'></SCRIPT>").append(EOL);
        //res.append("<SCRIPT TYPE='text/javascript' SRC='script/jquery-1.8.3.min.js?hash=").append(UpdateContent.jqueryMinHash[configNumber]).append("'></SCRIPT>").append(EOL);
        res.append("<SCRIPT TYPE='text/javascript' SRC='script/jquery.textchange.min.js?hash=").append(UpdateContent.jqueryTextChangeHash[configNumber]).append("'></SCRIPT>").append(EOL);
        res.append("<SCRIPT TYPE='text/javascript' SRC='script/jquery.caret.min.js?hash=").append(UpdateContent.jqueryCaretHash[configNumber]).append("'></SCRIPT>").append(EOL);
        res.append("<SCRIPT TYPE='text/javascript' SRC='script/jquery.blockUI.js?hash=").append(UpdateContent.jqueryBlockUIHash[configNumber]).append("'></SCRIPT>").append(EOL);
        res.append("<SCRIPT TYPE='text/javascript' SRC='").append(UpdateContent.NIC_EDIT_PATH_FILE).append("?hash=").append(UpdateContent.nicEditHash[configNumber]).append("'></SCRIPT>").append(EOL);
        res.append("</head>").append(EOL);
        res.append("<body class='frm'>").append(EOL);
        res.append("<form>");
        res.append("<table class='out' width='100%' height='100%'>");
        res.append("<tr>");
        res.append("<td class='hout' height='20'>");
        res.append("<div class='ht'>Выбор</div>");
        res.append("</td>");
        res.append("</tr>");
        res.append("<tr valign='top'>");
        res.append("<td align='right'>");
        res.append("<table class='out' width='100%'>");

        for (int i = 0; i < trs.length; ++i) {
            res.append("<tr valign='top'>");
            res.append("<td align='left'>");
            res.append("<input class='radio' type='radio' name='opt'");
            res.append(" value=\"").append(i).append("\">");
            res.append("<span class='radio'>");
            trs[i] = act.transitions[i].substring(0, act.transitions[i].indexOf(";"));
            res.append(trs[i]);
            res.append("</span>");
            res.append("</input>");
            res.append("</td>");
            res.append("</tr>");
        }

        res.append("</table>");
        res.append("</td>");
        res.append("</tr>");

        res.append("<tr>");
        res.append("<td colspan=\"2\" class=\"hout\" align=\"right\" height=\"20\">");
        res.append("<a class=\"tool\" onclick=\"").append(
                   "opener.selectOption('").append(act.flowId).append("', $(\"#myform input[type='radio']:checked\").val()); ").append(
                   "window.close();").append(
                   "\">").append(resource.getString("ok")).append("</a>").append(EOL);
        res.append("<a class=\"tool\" onclick=\"").append(
                   "window.close();").append(
                   "\">").append(resource.getString("cancel")).append("</a>").append(EOL);
        res.append("</td>");
        res.append("</tr>");

        res.append("</table></form>");
        res.append("</body></html>");

        return res.toString();
    }

    private static void getHelpMenuHTML(OrWebNoteBrowser[] helps, StringBuilder b) {
    	int width = getHelpMaxWidth(helps);
    	
    	int x = 200;
    	int y = 29;
    	
        b.append("<div class=\"report\" style=\"width: ")
                .append(width).append("px;").append(" left: ")
                .append(x).append(";").append(" top: ")
                .append(y).append(";").append(" display: none;\" id=\"printMenu\">");
        b.append("<table id=\"menu\" onselectstart=\"return false;\">");
        for (OrWebNoteBrowser help : helps) {
            b.append("<tr><td onmouseover=\"HoverMe(this);\" onmouseout=\"UnhoverMe(this);\">");

            b.append("<a class=\"hlb\" target=\"_blank\" href=\"").append("help.getHref()").append("\"");
            b.append("><img src=\"images/Note.gif\" />");
            b.append(help.getTitle());
            b.append("</a>");

            b.append("</td></tr>");
        }
        b.append("</table></div>");
    }
    
    private static int getHelpMaxWidth(OrWebNoteBrowser[] helps) {
        int res = 0;
        for (OrWebNoteBrowser help : helps) {
            String s = help.getTitle();
            Font f = new Font("Tahoma", 0, 13);
            Rectangle2D bs = f.getStringBounds(s, new FontRenderContext(null, false, false));
            int width = (int)bs.getWidth();
            if (width > res)
                res = width;
        }
        return res + 15;
    }
}

class ProcessNodeComparator implements Comparator<ProcessNode> {
	
	private int mode = 0;	// 0 - Сортировка по индексу; 1 - Сортировка по названию; 2 - Сортировка по индексу и по названию
	private long langId;
	private WebSession session;
	
	public ProcessNodeComparator(int mode, long langId, WebSession s) {
		this.mode = mode;
		this.langId = langId;
		this.session = s;
	}
	
	@Override
	public int compare(ProcessNode node1, ProcessNode node2) {
		switch(mode) {
			case 0:
				if (node1.getIndex() < node2.getIndex()) {
					return -1;
				} else if (node1.getIndex() > node2.getIndex()) {
					return 1;
				} else {
					return 0;
				}
			case 1:
				if (node1.toString(langId, session) == node2.toString(langId, session)) {
					return (node1.getObject().id == node2.getObject().id) ? 0
									: (node1.getObject().id < node2.getObject().id) ? -1 : 0;
				} else if (node1.toString(langId, session) == null || node1.toString(langId, session).trim().length() == 0) {
					return -1;
				} else if (node2.toString(langId, session) == null || node2.toString(langId, session).trim().length() == 0) {
					return 1;
				} else if (node1.toString(langId, session).equals(node2.toString(langId, session))) {
					return (node1.getObject().id == node2.getObject().id) ? 0
							: (node1.getObject().id < node2.getObject().id) ? -1 : 0;
				} else {
					return (node1.toString(langId, session).compareTo(node2.toString(langId, session)));
				}
			case 2:
				if (node1.getIndex() < node2.getIndex()) {
					return -1;
				} else if (node1.getIndex() > node2.getIndex()) {
					return 1;
				} else {
					if (node1.toString(langId, session) == node2.toString(langId, session)) {
						return 0;
					} else if (node1.toString(langId, session) == null || node1.toString(langId, session).trim().length() == 0) {
						return -1;
					} else if (node2.toString(langId, session) == null || node2.toString(langId, session).trim().length() == 0) {
						return 1;
					} else {
						return (node1.toString(langId, session).compareTo(node2.toString(langId, session)));
					}
				}
			default:
				return 0;
		}
	}
}