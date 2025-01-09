package kz.tamur.web.common;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.imageio.ImageIO;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.util.AttrRequestBuilder;
import com.cifs.or2.kernel.Date;
import com.cifs.or2.kernel.DateValue;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnDate;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;

import kz.tamur.SecurityContextHolder;
import kz.tamur.guidesigner.users.Or3RightsNode;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.rt.Utils;
import kz.tamur.rt.orlang.ClientOrLang;
import kz.tamur.util.Funcs;
import kz.tamur.util.ThreadLocalDateFormat;
import kz.tamur.web.common.ArchiveHelper.HyperNode;
import kz.tamur.web.common.LangHelper.WebLangItem;
import kz.tamur.web.controller.WebController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by IntelliJ IDEA. начаол-подпроцесса
 * User: Erik
 * Date: 01.11.2005
 * Time: 10:26:42
 * To change this template use File | Settings | File Templates.
 */
public class WebUser {
    private String name = null;
    private String path = null;
    private String newPath = null;
    private String confPath = null;
    private String ip = null;
    private String host = null;
    private Integer sid = null;
    private String dn = null;
	private int loginType;
	private int configNumber;
	private String dsName;
	private String iin;
    private Log log;
    private long downtime;
    private boolean isUseECP;
    private String signedData;
    
    public boolean waitingToUnbound = false;
	private String guid;
	private Map<String, String> loginParams = null;

    public WebUser(String dn, String ip, String host, int loginType, int configNumber, long downtime) {
        this.dn = dn;
        this.ip = ip;
        this.host = host;
        this.loginType = loginType;
        this.configNumber = configNumber;
        this.downtime = downtime;
    	this.log = LogFactory.getLog(WebController.BASE_NAME[configNumber] + "." + (UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + getClass().getName());
    }
    
    public WebUser(String name, String path, String newPath, String confPath, String ip, String host, int configNumber, String dsName, long downtime, boolean isUseECP, String signedData) {
    	this.name = name;
        this.dsName = dsName != null ? dsName : WebController.BASE_NAME[configNumber];
        this.path = path;
        this.newPath = newPath;
        this.confPath = confPath;
        this.ip = ip;
        this.host = host;
        loginType = Kernel.LOGIN_USUAL;
        this.configNumber = configNumber;
        this.downtime = downtime;
        this.isUseECP = isUseECP;
        this.signedData = signedData;
        StringBuilder sb = new StringBuilder(this.dsName).append(".").append(name.replaceAll("\\s|\\.", "_")).append(".").append(UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "").append(getClass().getName());
        log = LogFactory.getLog(sb.toString());
    }

    public WebUser(String name, String path, String newPath, String confPath, String ip, String host, int configNumber, String dsName, long downtime) {
        this.name = name;
        this.dsName = dsName != null ? dsName : WebController.BASE_NAME[configNumber];
        this.path = path;
        this.newPath = newPath;
        this.confPath = confPath;
        this.ip = ip;
        this.host = host;
        loginType = Kernel.LOGIN_USUAL;
        this.configNumber = configNumber;
        this.downtime = downtime;
        StringBuilder sb = new StringBuilder(this.dsName).append(".").append(name.replaceAll("\\s|\\.", "_")).append(".").append(UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "").append(getClass().getName());
        log = LogFactory.getLog(sb.toString());
    }

    public WebUser(String name, String path, String ip, String host, String signedStr, int configNumber, long downtime) {
        this.name = name;
        this.path = path;
        this.ip = ip;
        this.host = host;
        loginType = Kernel.LOGIN_CERT;
        this.configNumber = configNumber;
        this.downtime = downtime;
        StringBuilder sb = new StringBuilder(WebController.BASE_NAME[configNumber]).append(".").append(name.replaceAll("\\s|\\.", "_")).append(".").append(UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "").append(getClass().getName());
        log = LogFactory.getLog(sb.toString());
    }

    public void login(boolean force, boolean sLogin, String signedStr) throws KrnException {
    	switch (loginType) {
			case Kernel.LOGIN_USUAL:
	    		sid = WebSessionManager.createSession(name, path, newPath, confPath, ip, host, Kernel.LOGIN_USUAL, configNumber, dsName, force, sLogin, downtime, isUseECP, signedData);
	    		break;
			case Kernel.LOGIN_CERT:
	    		sid = WebSessionManager.createSession(name, signedStr, newPath, confPath, ip, host, Kernel.LOGIN_CERT, configNumber, WebController.BASE_NAME[configNumber], force, false, downtime, isUseECP, signedData);
	    		break;
			case Kernel.LOGIN_DN:
			case Kernel.LOGIN_LDAP:
	    		sid = WebSessionManager.createSession(dn, null, newPath, confPath, ip, host, this.loginType, configNumber, WebController.BASE_NAME[configNumber], force, false, downtime, isUseECP, signedData);
	    		break;
			default:
				break;
		}
    	
    	WebSession s = getSession();
    	if (s != null) {
    		name = s.getUserName();
    		s.setWebUser(this);
    	}
    	
        StringBuilder sb = new StringBuilder(WebController.BASE_NAME[configNumber]).append(".").append(name.replaceAll("\\s|\\.", "_")).append(".").append(UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "").append(getClass().getName());
        log = LogFactory.getLog(sb.toString());
    }

    public WebSession getSession() {
    	WebSession s = WebSessionManager.getSession(sid);
    	if (s != null) {
    		SecurityContextHolder.setKernel(s.getKernel());
    	}
    	return s;
    }

    public void valueBound(Map<String, Object> hs) {
    	this.guid = (String) hs.get("GUID");
    	
        Kernel krn = getSession().getKernel();
        hs.put("ws", getSession());
        hs.put("userLogin", krn.getUser().getName());
        hs.put("userSign", krn.getUser().getUserSign());
        hs.put("userId", krn.getUser().getObject().id);
        hs.put("userUID", krn.getUser().getObject().uid);
        hs.put("isMonitor", krn.getUser().isMonitor());
        hs.put("interface", krn.getUser().getIfc());
        
        hs.put("showTooltip", krn.getUser().isShowTooltip());
        hs.put("useNoteSound", krn.getUser().useNoteSound());
        hs.put("instantECP", krn.getUser().isInstantECP());

        hs.put("lastSuccessfullTime", krn.getUser().getLastSuccessfullTime() != null
        					? ThreadLocalDateFormat.get("dd.MM.yyyy HH:mm:ss").format(krn.getUser().getLastSuccessfullTime()) : "");
        hs.put("lastUnsuccessfullTime", krn.getUser().getLastUnsuccessfullTime() != null
				? ThreadLocalDateFormat.get("dd.MM.yyyy HH:mm:ss").format(krn.getUser().getLastUnsuccessfullTime()) : "");

        long kz = LangHelper.getKazLang(configNumber).obj.id;
        long ru = LangHelper.getRusLang(configNumber).obj.id;

        long langId = getSession().getInterfaceLangId();
        WebLangItem li = LangHelper.getLangById(langId, configNumber);
        if (li == null) {
            li = LangHelper.getKazLang(configNumber);
            langId = li.obj.id;
        }

        hs.put("langCode", li.code);
        hs.put("langId", langId);

        String serverId = getSession().getKernel().getUserSession().getServerId();
        if (serverId == null) {
			try {
				serverId = InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException e2) {
			}
        }
        hs.put("serverId", serverId);
        
        // Смена пароля происходит перед логином, а не внутри как сначало было сделано
        hs.put("changePass", false); 
        //boolean isLogged = krn.getUser().isLogged();
        //boolean isChangeFirstPass = krn.getUser().isChangeFirstPass();
        //krn.getUser().isExpired() || krn.getUser().isEnded() || (!isLogged && isChangeFirstPass));

        List<String> roles = krn.getUser().getUserRoleUIDs();

        // Логотип из web.xml
    	byte[] img = null;
    	long picWidth = 0;
    	long picHeight =  0;
    	try {
    		KrnClass cls = krn.getClassByName("ConfigGlobal");
			KrnObject[] objs = krn.getClassOwnObjects(cls, 0);
			KrnObject obj = null;
			if(objs != null && objs.length > 0)
				obj = objs[0];
    		if(obj != null) {
    			String attrName = "logotypePic";
    			img = krn.getBlob(obj, attrName, 0, 0, 0);
        		attrName = "logoPicWidth";
        		if(krn.getLongs(obj, attrName, 0).length > 0)
        			picWidth = krn.getLongs(obj, attrName, 0)[0];
        		attrName = "logoPicHeight";
        		if(krn.getLongs(obj, attrName, 0).length > 0)
        			picHeight = krn.getLongs(obj, attrName, 0)[0];
    		}
    			
    	} catch (KrnException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}

    	if(img != null && img.length > 0) {
    		BufferedImage bufImg = Utils.resize(img, (int)picWidth, (int)picHeight);
    		ByteArrayOutputStream baos = new ByteArrayOutputStream();
    		try {
				ImageIO.write(bufImg, "png", baos);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		img = baos.toByteArray();
    		StringBuilder src = new StringBuilder();
    		String imgBase64 = Base64.encodeBytes(img);
    	    String imgSource = "data:image/png;base64, " + imgBase64;
    	    src.append(imgSource);
    		hs.put("head.logo", src.toString());
    	} else 
    		hs.put("head.logo", WebController.PATH_TO_LOGO);
        hs.put("head.title", WebController.WINDOW_TITLE);
        hs.put("breadcrumpsOn", WebController.BREADCRUMPS_ON);
        hs.put("hideCloseIfcBtn", WebController.HIDE_CLOSE_INTERFACE_BUTTON);

        // Раздача прав в конструкторе
        Map<String, Object> m = new HashMap<String, Object>();

        m.put("menu.archive", krn.getUser().hasRight(Or3RightsNode.WEB_PAGE_ARCHS_RIGHT));
        m.put("menu.dict", krn.getUser().hasRight(Or3RightsNode.WEB_PAGE_DICTS_RIGHT));
        m.put("menu.help", krn.getUser().hasRight(Or3RightsNode.WEB_PAGE_HELPS_RIGHT));
        m.put("menu.main", krn.getUser().hasRight(Or3RightsNode.WEB_PAGE_MAIN_RIGHT));
        m.put("menu.mainifc", krn.getUser().hasRight(Or3RightsNode.WEB_PAGE_MAINIFC_RIGHT));
        m.put("menu.monitor", krn.getUser().isMonitor() || krn.getUser().hasRight(Or3RightsNode.WEB_PAGE_MONITOR_RIGHT));
        m.put("menu.process", krn.getUser().hasRight(Or3RightsNode.WEB_PAGE_PROCESS_RIGHT));
        m.put("menu.shtat", krn.getUser().hasRight(Or3RightsNode.WEB_PAGE_SHTAT_RIGHT));
        m.put("menu.statistics", krn.getUser().hasRight(Or3RightsNode.WEB_PAGE_STATS_RIGHT));
        m.put("menu.useractions", krn.getUser().hasRight(Or3RightsNode.WEB_PAGE_ACTIONS_RIGHT));
        m.put("menu.userrights", krn.getUser().hasRight(Or3RightsNode.WEB_PAGE_RIGHTS_RIGHT));
        m.put("menu.usersessions", krn.getUser().hasRight(Or3RightsNode.WEB_PAGE_SESSIONS_RIGHT));
        m.put("menu.profile", krn.getUser().hasRight(Or3RightsNode.WEB_PAGE_PROFILE_RIGHT));
        m.put("menu.notification", krn.getUser().hasRight(Or3RightsNode.WEB_PAGE_NOTIFICATION));
        m.put("menu.admins", krn.getUser().hasRight(Or3RightsNode.WEB_PAGE_ADMINS_RIGHT));

        m.put("menu.kadmap", krn.getUser().hasRight(Or3RightsNode.WEB_PAGE_KAD_MAP_RIGHT) || krn.getUser().hasRight(Or3RightsNode.WEB_PAGE_STATS_RIGHT));
        m.put("menu.kadwork", krn.getUser().hasRight(Or3RightsNode.WEB_PAGE_KAD_WORK_RIGHT));

        hs.put("options", m);

        if (roles.contains("1014162.3415624"))
            hs.put("isKadry", "1");
        if (roles.contains("1014162.3152251"))
            hs.put("isChief", "1");
        if (roles.contains("17821.241206"))
            hs.put("isAdminRole", "1");
        if (krn.getUser().isAdmin())
            hs.put("isAdmin", "1");

        Map<Long, List<String>> userRoles;
        userRoles = krn.getUser().getUserRoles(ru, kz);
        hs.put("userRoles" + ru, userRoles.get(ru));
        hs.put("userRoles" + kz, userRoles.get(kz));

        KrnObject obj = krn.getUser().getObject();

        AttrRequestBuilder arb = new AttrRequestBuilder(Kernel.SC_USER, krn);

        try {
            arb.add("iin");
        } catch (Throwable e) {
            log.warn("Не найден атрибут 'iin' в классе 'User'");
        }

        try {
            arb.add("theme");
        } catch (Throwable e) {
            log.warn("Не найден атрибут 'theme' в классе 'User'");
        }

        try {
            arb.add("sign", kz).add("sign", ru);
        } catch (Throwable e) {
            log.warn("Не найден атрибут 'sign' в классе 'User'");
        }

        // 0 - kyzmet
        // 1 - ul
        // 2 - egkn
        
        int project = 0;
        try {
            arb.add("персона",
                    new AttrRequestBuilder(krn.getClassByName("Персонал"), krn).add(
                            "текущ  состояние -приказ о перемещении-",
                            new AttrRequestBuilder(krn.getClassByName("Приказ о перемещении"), krn).add(
                                    "зап табл штатн распис -цель-",
                                    new AttrRequestBuilder(krn.getClassByName("Зап табл штатн распис"), krn)
                                            // .add("раб место -телефон -внешний-")
                                            .add("раб место -телефон -внутренний-")
                                            .add("наименование им", kz)
                                            .add("наименование им", ru)
                                            .add("родитель",
                                                    new AttrRequestBuilder(krn.getClassByName("Зап табл штатн распис"), krn).add(
                                                            "наименование им", kz).add("наименование им", ru)))).add(
                            "текущ  состояние -зап табл персон данных-",
                            new AttrRequestBuilder(krn.getClassByName("Зап табл персон данных"), krn).add(
                                    "идентиф -дата рождения-").add("адрес электронной почты")));
        } catch (Throwable e) {
        	try {
                arb.add("исполнитель",
                        new AttrRequestBuilder(krn.getClassByName("Исполнитель"), krn).add(
                                "рег орган -структ регистрирущ органа-",
                                new AttrRequestBuilder(krn.getClassByName("Структ регистрирущ органа"), krn).add(
                                        "значение",
                                        new AttrRequestBuilder(krn.getClassByName("Регистрирующий орган"), krn)
                                                .add("наименование", kz)
                                                .add("наименование", ru)))
                        		.add("фио"));
        		project = 1;
            } catch (Throwable e1) {
            	try {
                    arb.add("исполнитель",
                            new AttrRequestBuilder(krn.getClassByName("Исполнитель"), krn).add(
                                    "департамент",
                                    new AttrRequestBuilder(krn.getClassByName("Департамент"), krn)
                                    			.add("название ведомства", kz)
                                    			.add("название ведомства", ru)
                                    			.add("название", kz)
                                    			.add("название", ru))
                    				.add("должность",
                                            new AttrRequestBuilder(krn.getClassByName("Должность"), krn)
                                            			.add("мультинаименование", kz)
                                            			.add("мультинаименование", ru))
                            		.add("фио"));
            		project = 2;
                } catch (Throwable e2) {
                    log.warn("либо, Не найден класс 'Исполнитель'");
                    log.warn("либо, Не найден класс 'Департамент'");
                }
            }
        }

        if (project == 0) {
	        try {
	            arb.add("баланс_ед", new AttrRequestBuilder(krn.getClassByName("уд::осн::Баланс_ед"), krn).add("наименование", kz)
	                    .add("наименование", ru));
	        } catch (Throwable e) {
	            log.warn("Не найден атрибут 'баланс_ед' в классе 'User'");
	            log.warn("либо, Не найден класс 'уд::осн::Баланс_ед'");
	            log.warn("либо, Не найден атрибут 'наименование' в классе 'уд::осн::Баланс_ед'");
	        }
        } else {
        	try {
        		arb.add("email");
        		arb.add("ip_address");
        		arb.add("doljnost");
            } catch (Throwable e) {
                log.warn("Не найден атрибут 'email' в классе 'User'");
                log.warn("Не найден атрибут 'ip_address' в классе 'User'");
                log.warn("Не найден атрибут 'doljnost' в классе 'User'");
            }
        }

        try {
            long[] objIds = { obj.id };
            Object[] row = krn.getObjects(objIds, arb.build(), 0).get(0);
            
            this.iin = arb.getStringValue("iin", row);
            
            hs.put("userIIN", this.iin);
            hs.put("userIP", this.ip);

            Long ltmp = arb.getLongValue("theme", row);
            if (ltmp != null && ltmp > 0)
                hs.put("theme", ltmp);

            String tmp = (String) arb.getValue("исполнитель.фио", row);
            hs.put("userSign" + ru, tmp);
            hs.put("userSign" + kz, tmp);

            if (hs.get("userSign" + ru) == null) {
            	tmp = arb.getStringValue("sign", kz, row);
            	hs.put("userSign" + kz, tmp);
            	tmp = arb.getStringValue("sign", ru, row);
            	hs.put("userSign" + ru, tmp);
            }

            tmp = arb.getStringValue("баланс_ед.наименование", kz, row);
            hs.put("userGO" + kz, tmp);

            tmp = arb.getStringValue("баланс_ед.наименование", ru, row);
            hs.put("userGO" + ru, tmp);

            if (hs.get("userGO" + kz) == null) {
            	tmp = arb.getStringValue("исполнитель.департамент.название ведомства", kz, row);
            	hs.put("userGO" + kz, tmp);
            }

            if (hs.get("userGO" + ru) == null) {
            	tmp = arb.getStringValue("исполнитель.департамент.название ведомства", ru, row);
            	hs.put("userGO" + ru, tmp);
            }

            if (hs.get("userGO" + kz) == null) {
            	tmp = arb.getStringValue("исполнитель.рег орган -структ регистрирущ органа-.значение.наименование", kz, row);
            	hs.put("userGO" + kz, tmp);
            }

            if (hs.get("userGO" + ru) == null) {
            	tmp = arb.getStringValue("исполнитель.рег орган -структ регистрирущ органа-.значение.наименование", ru, row);
            	hs.put("userGO" + ru, tmp);
            }

            //Проверка наличия давно запущенных процессов
            Date dateContr = null;
    		long daysAlarmBeforeBlocking=-1;
    		try {
    	    		KrnClass cls = null;
    	    		KrnAttribute dattr=null;
    	    		try {
    	                dattr=krn.getAttributeByName(Kernel.SC_USER,"notificationAlarmDate");
    	    			KrnClass gcls = krn.getClassByName("Глобальные параметры");
    	                KrnAttribute daysAttr = krn.getAttributeByName(gcls,"alarmPeriodBeforeBlocking");
    	    			KrnObject[] gobjs=krn.getClassObjects(gcls, 0);
    	    			if(gobjs!=null && gobjs.length>0){
    	    				long[] days=krn.getLongs(gobjs[0], daysAttr, 0);
    	    				if(days!=null && days.length>0)
    	    					daysAlarmBeforeBlocking=days[0];
    	    			}
    	    			cls = krn.getClassByName("XmlUtil");
    	    		} catch (Exception e) {
    	    		}
    	    		
    		    	if (cls != null && dattr!=null && daysAlarmBeforeBlocking > 0) {
    		            ClientOrLang orlang = new ClientOrLang(krn);
    		        	List<Object> args = new ArrayList<Object>();
    		        	args.add("1");
    		            orlang.sexec(cls, cls, "getOldOrders", args, new Stack<String>());
    		            DateValue[] vdates = krn.getDateValues(objIds, dattr, 0);
    		            if(vdates!=null && vdates.length>0){
    		            	dateContr=vdates[0].value;
    		            }
    		            
//    		    	} else {
//    		    		if(cls==null) 
//    		    			log.warn("Не найден класс XmlUtil");
//    		            if(dattr==null) 
//    		            	log.warn("Не найден атрибут 'дата_получ_уведомл_резерв' в классе 'User'");
    		    	}
    		} catch (Throwable e) {
    			log.error(e, e);
    		}
            if(dateContr!=null && dateContr.year > 0){
            	long days = daysAlarmBeforeBlocking + Funcs.convertDate(dateContr).getDaysAfter(new KrnDate(System.currentTimeMillis()));
                hs.put("daysOldFlows", days>0?""+days:"0");
            }else{
                hs.put("daysOldFlows", "-1");
            }
            //Завершение проверки наличия давно запущенных процессов.
            
            KrnObject pers = (KrnObject) arb.getValue("персона", row);
            hs.put("hasPerson", pers != null);
            m.put("menu.myinfo", krn.getUser().hasRight(Or3RightsNode.WEB_PAGE_MY_INFO_RIGHT) || pers != null);
            
            List<KrnObject> dynamicRightObjs = Utils.getDynamicNodeObjs(krn);
            List<KrnObject> dynamicRightObjUis = Utils.getDynamicNodeUis(krn, dynamicRightObjs);
            for(int i = 0; i < dynamicRightObjs.size(); i++) {
            	m.put("ui_dynamicTitle_" + dynamicRightObjs.get(i).id, krn.getUser().hasRight("ui_dynamicTitle_" + dynamicRightObjs.get(i).id));
            	HyperNode node = new HyperNode(dynamicRightObjs.get(i), dynamicRightObjUis.get(i));
            	ArchiveHelper.addNode(dynamicRightObjs.get(i).uid, node);
            }

            tmp = arb.getStringValue( "персона.текущ  состояние -приказ о перемещении-.зап табл штатн распис -цель-.наименование им", kz, row);
            hs.put("userPosition" + kz, tmp);

            tmp = arb.getStringValue( "персона.текущ  состояние -приказ о перемещении-.зап табл штатн распис -цель-.наименование им", ru, row);
            hs.put("userPosition" + ru, tmp);

            tmp = arb.getStringValue("исполнитель.должность.мультинаименование", ru, row);
            if (hs.get("userPosition" + ru) == null) {
            	hs.put("userPosition" + ru, tmp);
            }
            tmp = arb.getStringValue("исполнитель.должность.мультинаименование", kz, row);
            if (hs.get("userPosition" + kz) == null) {
            	hs.put("userPosition" + kz, tmp);
            }

            
            tmp = arb .getStringValue( "персона.текущ  состояние -приказ о перемещении-.зап табл штатн распис -цель-.раб место -телефон -внутренний-", row);
            hs.put("userPhoneIn", tmp);

            tmp = arb.getStringValue( "персона.текущ  состояние -приказ о перемещении-.зап табл штатн распис -цель-.родитель.наименование им", kz, row);
            hs.put("userDepartment" + kz, tmp);

            if (hs.get("userDepartment" + kz) == null) {
            	tmp = arb.getStringValue("исполнитель.департамент.название", kz, row);
            	hs.put("userDepartment" + kz, tmp);
            }

            tmp = arb.getStringValue( "персона.текущ  состояние -приказ о перемещении-.зап табл штатн распис -цель-.родитель.наименование им", ru, row);
            hs.put("userDepartment" + ru, tmp);

            if (hs.get("userDepartment" + ru) == null) {
            	tmp = arb.getStringValue("исполнитель.департамент.название", ru, row);
            	hs.put("userDepartment" + ru, tmp);
            }

            Date dr = (Date) arb.getValue("персона.текущ  состояние -зап табл персон данных-.идентиф -дата рождения-", row);
            hs.put("userBirthday", dr == null ? null : ThreadLocalDateFormat.get("dd.MM.yyyy").format(Funcs.convertDate(dr)));

            tmp = arb.getStringValue("персона.текущ  состояние -зап табл персон данных-.адрес электронной почты-", row);
            hs.put("userEmail", tmp);

            if (hs.get("userEmail") == null) {
	            tmp = arb.getStringValue("email", row);
	            hs.put("userEmail", tmp);
            }
            
            tmp = arb.getStringValue("doljnost", row);
            if (hs.get("userPosition" + ru) == null) {
            	hs.put("userPosition" + ru, tmp);
            }
            if (hs.get("userPosition" + kz) == null) {
            	hs.put("userPosition" + kz, tmp);
            }
            
            //tmp = arb.getStringValue("ip_address", row);
            //session.setAttribute("userIP", tmp);
        } catch (Exception e) {
            log.error(e, e);
        }

        log.info("|USER: " + name + "| Session bounded " + new java.util.Date());
        CommonHelper.takeMemorySnapshot(getSession());
    }

    public void valueUnbound(boolean releaseHttpSession) {
    	WebSessionManager.releaseSession(sid, false);
        log.info("|USER: " + name + "| Session unbounded " + new java.util.Date());
        CommonHelper.takeMemorySnapshot(null);
        if (!waitingToUnbound) {
        	Thread.dumpStack();
        }
        if (releaseHttpSession)
        	WebController.releaseHttpSession(null, guid);
    }
    
    public void setWaitingToUnbound(boolean waitingToUnbound) {
    	this.waitingToUnbound = waitingToUnbound;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void ltpaLogin() throws KrnException {
        sid = WebSessionManager.createSession(name, null, null, null, ip, host, Kernel.LOGIN_USUAL, configNumber, WebController.BASE_NAME[configNumber], false, false, downtime, isUseECP, signedData);
    }

    public String getPassword() {
        return path;
    }

    public Integer getSid() {
        return sid;
    }

    public void setSid(Integer sid) {
        this.sid = sid;
    }

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public String getIin() {
		return iin;
	}

	public String getGUID() {
		return guid;
	}

	public void setLoginParams(Map<String, String> loginParams) {
		this.loginParams  = loginParams;	
	}

	public void doAfterLogin() {
		if (loginParams != null && loginParams.get("afterLoginFunc") != null) {
			String clsName = loginParams.get("cls");
        	String afterLoginFunc = loginParams.get("afterLoginFunc");
        	
    		try {
            	Kernel krn = getSession().getKernel();

    			KrnClass cls = null;
	    		try {
	    			cls = krn.getClassByName(clsName);
	    		} catch (Exception e) {}
	    		
	    		// Запускаем функцию afterLoginFunc
	    		// параметром функции является мапа с параметрами запроса из другой системы
	    		
	    		// #set($params = $ARGS[0])
	    		// #set($userId = $params.get("userId"))
	    		// и т.д.
	    		
		    	if (cls != null) {
		            ClientOrLang orlang = new ClientOrLang(getSession().getFrameManager().getCurrentFrame());
		            List<Object> args = new ArrayList<>();
		            args.add(loginParams);
	                orlang.exec(cls, cls, afterLoginFunc, args, new Stack<String>());
	                // только один раз, при обновлении окна больше не запускаем скрипт
	                this.loginParams = null;
		    	} else {
		    		log.error("Не найден класс " + clsName);
		    	}
    		} catch (Throwable e) {
    			log.error(e, e);
    		}
		}
	}
}
