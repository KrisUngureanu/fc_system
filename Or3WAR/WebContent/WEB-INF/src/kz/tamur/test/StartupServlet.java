package kz.tamur.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import javax.ejb.EJB;
import javax.naming.NamingException;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kz.tamur.DriverException;
import kz.tamur.admin.ErrorsNotification;
import kz.tamur.admin.ReportLogHelper;
import kz.tamur.admin.SignatureCheckingParams;
import kz.tamur.common.LoggingHelper;
import kz.tamur.comps.Constants;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.or3ee.server.admin.ServerCleanerLocal;
import kz.tamur.or3ee.server.kit.SrvUtils;
import kz.tamur.server.indexer.Indexer;
import kz.tamur.server.login.LtpaLoginModule;
import kz.tamur.server.plugins.NotificationListener;
import kz.tamur.server.wf.ExecutionComponent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.ProjectConfiguration;
import com.cifs.or2.server.Context;
import com.cifs.or2.server.Session;
import com.cifs.or2.server.db.ConnectionManager;
import com.cifs.or2.server.db.ConnectionManagerLocal;
import com.cifs.or2.server.exchange.Box;
import com.cifs.or2.server.exchange.transport.MessageCash;
import com.cifs.or2.server.orlang.SrvOrLang;
import com.cifs.or2.server.plugins.SystemProperties;
import com.cifs.or2.server.timer.ServerTasks;

/**
 * Servlet implementation class StartupServlet
 */
public class StartupServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final Log LOG = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + StartupServlet.class.getName());

    // Менеджер для создания подключения к различным базам данных
    @EJB(beanName="ConnectionManager", beanInterface = ConnectionManagerLocal.class)
    private ConnectionManagerLocal connectionManager;

    @EJB(beanName="ServerCleaner", beanInterface = kz.tamur.or3ee.server.admin.ServerCleanerLocal.class)
    private ServerCleanerLocal cleaner;

    public static String REMOTE_LOGIN_SK;
    public static String REMOTE_LOGIN_KGA;
    public static String REMOTE_LOGIN_MDA;
    public static String REMOTE_LOGIN_SALT;
    public static int REMOTE_LOGIN_MDL;

    private boolean activateScheduler = System.getProperty("activateScheduler", "0").equals("1");
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public StartupServlet() {
        super();
    }

    /**
     * @see Servlet#init(ServletConfig)
     */
    public void init(final ServletConfig config) throws ServletException {
        try {
        	LOG.info("user.dir = " + System.getProperty("user.dir"));
        	LOG.info("curr.dir = " + new File(".").getAbsolutePath());
        	LOG.info("docs.dir = " + new File("docs").getAbsolutePath());
        	
			// Проверяем наличие папки doc и очищаем ее
			Constants.DOCS_DIRECTORY.mkdirs();
			File[] files = Constants.DOCS_DIRECTORY.listFiles();
	        for (File file : files) {
	        	try {
	        		LOG.info("deleting file: " + file.getAbsolutePath());
	        		file.delete();
	        	} catch (Exception e) {
	        		LOG.error(e, e);
	        	}
	        }

			Indexer.startIndexing();

			synchronized (this) {
		        // Секретный ключ для авторизации без пароля из другой внешней системы
		        REMOTE_LOGIN_SK = config.getInitParameter("remote_login_sk");
		        REMOTE_LOGIN_KGA = config.getInitParameter("remote_login_kga");
		        REMOTE_LOGIN_MDA = config.getInitParameter("remote_login_mda");
		        REMOTE_LOGIN_SALT = config.getInitParameter("remote_login_salt"); //"-bc81-bb96d8f94605"
		        REMOTE_LOGIN_MDL = config.getInitParameter("remote_login_mdl") != null ? Integer.parseInt(config.getInitParameter("remote_login_mdl")) : 16;
		        
		        LtpaLoginModule.WEBSPHERE_SK = config.getInitParameter("websphere_sk");
	            LtpaLoginModule.WEBSPHERE_CA = config.getInitParameter("websphere_ca");
	            LtpaLoginModule.WEBSPHERE_SKF = config.getInitParameter("websphere_skf");
	            LtpaLoginModule.WEBSPHERE_DA = config.getInitParameter("websphere_da");
	            LtpaLoginModule.WEBSPHERE_DL = config.getInitParameter("WEBSPHERE_DL") != null ? Integer.parseInt(config.getInitParameter("WEBSPHERE_DL")) : 24;
	
	            String noReloadFlt = config.getInitParameter("noReloadFlt");
	            if ("0".equals(noReloadFlt))
	            	Kernel.reloadFlt = true;
			}
			
            final String configFilePath = config.getInitParameter("config-file");

            if (configFilePath != null) {
            	ConnectionManager.CONFIGS_FILE_PATH = configFilePath;
            	
                new Timer().schedule(new TimerTask() {
                    public void run() {
                        try {
                        	// Адрес и порт административной консоли
                            String adminHost = config.getInitParameter("managementHost");
                            int adminPort = Integer.parseInt(config.getInitParameter("managementPort"));

                        	// Читаем конфигурацию из xml-файла
                            SAXBuilder builder = new SAXBuilder();
                            Document doc = builder.build(new File(configFilePath));
                            Element projectsNode = doc.getRootElement();
                            List<Element> projectNodes = projectsNode.getChildren("project");
                            
                            ProjectConfiguration root = ConnectionManager.instance().getRoot();
                            
                            for (Element projectNode : projectNodes) {
                            	loadProjectConfig(root, projectNode, adminHost, adminPort);
                            }
                            
                            List<Element> userNodes = projectsNode.getChildren("user");
                            
                            for (Element userNode : userNodes) {
                            	String userName = userNode.getAttributeValue("name");
                            	String userPass = userNode.getAttributeValue("password");
                            	
                            	connectionManager.addUser(userName, userPass);
                            }

                        } catch (Exception e) {
                            LOG.error(e, e);
                        }
                    }
                }, 10000);

            	
            } else {
//            	Or3SystemOut.bind();
            	
            	if (SystemProperties.log4jPath != null)
            		LoggingHelper.instance().configure(SystemProperties.log4jPath);
	            String errorsNotify = (String) connectionManager.getInitParamByName("errorsNotify");

	            //String[] dsNames = config.getInitParameter("dataSourceNames").split(";");
	            String[] dsNames = (String[]) connectionManager.getInitParamByName("dataSourceNames");
	            
	            String[] tpropsFileNames = null;
	            for (int i=0;i<dsNames.length;i++) {
	            	Session s = SrvUtils.getSession(dsNames[i], "sys", null);
	            	
					if (s != null) {
						if (Constants.IS_UL_PROJECT || Constants.IS_FC_PROJECT) {
							SignatureCheckingParams.init(s);
						}
						if (errorsNotify != null && errorsNotify.equals("1")) {
							ErrorsNotification.init(s);
						}
						Collection<kz.tamur.ods.Lock> locks = s.getAllLocks();
						for (kz.tamur.ods.Lock lock : locks) {
							if (lock.sessionId != null) {
								UserSession us = Session.findUserSession(UUID.fromString(lock.sessionId));
								if (us == null) {
									s.unlockObject(lock.objId, lock.lockerId);
									s.commitTransaction();
								}
							}
						}
						s.unlockUnexistingFlowObjects();
						s.commitTransaction();

						ExecutionComponent exeComp = new ExecutionComponent(s);
						Session.addExeComp(dsNames[i], exeComp);
						if (tpropsFileNames == null)
							tpropsFileNames = (String[]) ConnectionManager.instance().getInitParamByName("tpropsFiles");
						if (tpropsFileNames != null && tpropsFileNames.length > i) {
							MessageCash messageCache = new MessageCash(s, tpropsFileNames[i], exeComp);
							Session.addMessageCache(dsNames[i], messageCache);
							Collection<Box> boxes = messageCache.getBoxes();
							for (Box box : boxes) {
								box.addBoxListener(exeComp);
							}
						}
						ServerTasks serverTasks = new ServerTasks(s, dsNames[i], activateScheduler);
						Session.addServerTasks(dsNames[i], serverTasks);
						
						NotificationListener.instance(dsNames[i]);

			    		final String fullStartMethodName = System.getProperty("run.after.start");
			    		
			    		if (fullStartMethodName != null) {
			    			String[] tokens = fullStartMethodName.split("\\.");
			    			
			    			final String clsName = tokens[0];
			    			final String methodName = tokens[1];
			    			final String dsName = dsNames[i];
			    			
			    			new Thread(new Runnable() {
			    	            public void run() {
				    				Session s = null;
					    			try {
					    				s = SrvUtils.getSession(dsName, "sys", null);
						    			KrnClass cls = s.getClassByName(clsName);
						    	        Context ctx = new Context(new long[0], 0, 0);
						    	        ctx.langId = 0;
						    	        ctx.trId = 0;
						    	        s.setContext(ctx);
						    	        
								        List<Object> args = new ArrayList<Object>();
								        SrvOrLang orlang = s.getSrvOrLang();
								        Object res = orlang.exec(cls, cls, methodName, args, new Stack<String>());
								        
								        LOG.info(fullStartMethodName + " returned res: " + res);
								        s.commitTransaction();
					    			} catch (Throwable e) {
					    				LOG.error(e, e);
					    			} finally {
					    				if (s!= null)
					    					s.release();
					    			}
			    	            }
			    			}).start();
			    		}

						s.release();

						Calendar c = Calendar.getInstance();
						int hour = c.get(Calendar.HOUR_OF_DAY);
						if (hour >= 1) {
							c.set(Calendar.DATE, c.get(Calendar.DATE) + 1);
						}
						c.set(Calendar.HOUR_OF_DAY, 1);

						long day_period = 24L * 3600000L;
						final String reportLogName = s.getReportLogName();

						new Timer().schedule(new TimerTask() {
							public void run() {
								try {
									ReportLogHelper.linkLogs("report", reportLogName);
								} catch (Exception e) {
									LOG.error(e, e);
								}
							}
						}, c.getTime(), day_period);
					}
	            }
	            
	            cleaner.start();
	            
            }            
        } catch (Exception e) {
        	LOG.error(e, e);
            throw new ServletException(e);
        }

    }

    private void loadProjectConfig(ProjectConfiguration parent, Element projectNode, String adminHost, int adminPort) throws KrnException, DriverException, NamingException {
    	String name = projectNode.getAttributeValue("name");
    	String dsName = projectNode.getAttributeValue("distinctName");
    	String schemeName = projectNode.getAttributeValue("schemeName");
    	boolean notifyErrors = "true".equals(projectNode.getAttributeValue("notifyErrors"));
    	boolean watchTransactions = "true".equals(projectNode.getAttributeValue("watchTransactions"));
    	
    	String webContextName = projectNode.getChildText("webContext");
    	String replicationDir = projectNode.getChildText("replication.directory");
    	String srvPlugsFileName = projectNode.getChildText("server.plugins");
    	String triggerExceptionsFileName = projectNode.getChildText("trigger.exceptions");
    	String transportPropertiesFileName = projectNode.getChildText("transport.properties");
    	int maxUserCount = projectNode.getChildText("maxUserCount") != null ? Integer.parseInt(projectNode.getChildText("maxUserCount")) : 10;
    	
    	Element dataSourceNode = projectNode.getChild("datasource");
    	String dsJndiName = dataSourceNode.getAttributeValue("jndi-name");
    	String poolName = dataSourceNode.getAttributeValue("pool-name");
    	String driverName = dataSourceNode.getChildText("driver");

    	String connectionUrl = dataSourceNode.getChildText("connection-url");
    	String transactionIsolation = dataSourceNode.getChildText("transaction-isolation");
    	boolean prefill = "true".equals(dataSourceNode.getChild("pool").getChildText("prefill"));
    	boolean useStrictMin = "true".equals(dataSourceNode.getChild("pool").getChildText("use-strict-min"));
    	int minPoolSize = Integer.parseInt(dataSourceNode.getChild("pool").getChildText("min-pool-size"));
    	int maxPoolSize = Integer.parseInt(dataSourceNode.getChild("pool").getChildText("max-pool-size"));
    	String flushStrategy = dataSourceNode.getChild("pool").getChildText("flush-strategy");

    	boolean sharePreparedStatements = "true".equals(dataSourceNode.getChild("statement").getChildText("share-prepared-statements"));
    	long cacheSize = Long.parseLong(dataSourceNode.getChild("statement").getChildText("prepared-statement-cache-size"));

    	String userName = dataSourceNode.getChild("security").getChildText("user-name");
    	String userPass = dataSourceNode.getChild("security").getChildText("password");

    	ProjectConfiguration pc = new ProjectConfiguration(name, dsName, schemeName, notifyErrors, watchTransactions, 
    			dsJndiName, poolName, connectionUrl, driverName, transactionIsolation, minPoolSize, maxPoolSize, prefill, useStrictMin, 
        		flushStrategy, userName, userPass, sharePreparedStatements, cacheSize, srvPlugsFileName, transportPropertiesFileName, 
        		triggerExceptionsFileName, replicationDir, webContextName, maxUserCount);
    	
    	parent.addChild(pc);
    	
/*    	boolean dbExists = false;
    	try {
    		Session s = new Session();
    		dbExists = s.checkIfDatasourceExists(poolName, adminHost, adminPort);
    		if (!dbExists) {
    			s.createDatasource(dsJndiName, poolName, driverName, connectionUrl, transactionIsolation, 
    					prefill, useStrictMin, minPoolSize, maxPoolSize, flushStrategy,
    					sharePreparedStatements, cacheSize, userName, userPass, adminHost, adminPort);
    			
    			
    		}
    	} catch (IOException e) {
    		LOG.error("Can't connect to JBoss manaement port!");
    		LOG.error(e, e);
    	}*/
    	
    	loadProjectConfig(dsName, schemeName, dsJndiName, driverName, webContextName, replicationDir, srvPlugsFileName,
    			triggerExceptionsFileName, transportPropertiesFileName, notifyErrors, watchTransactions);
    	
    	ConnectionManager.instance().addConfiguration(dsName, pc);
    	
        List<Element> projectNodes = projectNode.getChildren("project");
        for (Element child : projectNodes) {
        	loadProjectConfig(pc, child, adminHost, adminPort);
        }

    }
    
    private void loadProjectConfig(String dsName, String schemeName, String dsJndiName, String dsType, String webContextName, 
    		String replicationDir, String srvPlugsFileName, String triggerExceptionsFileName, String transportPropertiesFileName, 
    		boolean notifyErrors, boolean watchTransactions) throws KrnException, DriverException, NamingException {
        LOG.info("LOADING CONFIGURATION! - " + dsName);
        Session s = SrvUtils.getSession(dsName, "sys", null);
        if(s!=null){
            if (notifyErrors)
            	ErrorsNotification.init(s);

            Collection<kz.tamur.ods.Lock> locks = s.getAllLocks();
            for (kz.tamur.ods.Lock lock : locks) {
                if (lock.sessionId != null) {
                    UserSession us = Session.findUserSession(UUID.fromString(lock.sessionId));
                    if (us == null) {
                        s.unlockObject(lock.objId, lock.lockerId);
                        s.commitTransaction();
                    }
                }
            }
            s.unlockUnexistingFlowObjects();
            s.commitTransaction();
            
            ExecutionComponent exeComp = new ExecutionComponent(s);
            Session.addExeComp(dsName, exeComp);

            MessageCash messageCache = new MessageCash(s, transportPropertiesFileName, exeComp);
            Session.addMessageCache(dsName, messageCache);
            Collection<Box> boxes = messageCache.getBoxes();
            for (Box box : boxes) {
                box.addBoxListener(exeComp);
            }
            ServerTasks serverTasks = new ServerTasks(s, dsName, activateScheduler);
            Session.addServerTasks(dsName, serverTasks);
            s.release();
            Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            if (hour >= 1) {
                c.set(Calendar.DATE, c.get(Calendar.DATE) + 1);
            }
            c.set(Calendar.HOUR_OF_DAY, 1);

            long day_period = 24L * 3600000L;
            final String reportLogName = s.getReportLogName();

            new Timer().schedule(new TimerTask() {
                public void run() {
                    try {
                        ReportLogHelper.linkLogs("report", reportLogName);
                    } catch (Exception e) {
                        LOG.error(e, e);
                    }
                }
            }, c.getTime(), day_period);
        }
        
        LOG.info("CONFIGURATION LOADED! - " + dsName);
	}
    
	/**
     * @see Servlet#destroy()
     */
    public void destroy() {
        LOG.info("destroy");
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}
