package com.cifs.or2.server.db;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

import com.cifs.or2.kernel.ProjectConfiguration;
import com.cifs.or2.server.Session;
import com.cifs.or2.server.orlang.SrvOrLang;

import kz.tamur.DriverException;
import kz.tamur.ods.Driver2;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.or3ee.server.admin.TransactionWatchDogLocal;
import kz.tamur.or3ee.server.kit.SrvUtils;
import kz.tamur.server.wf.ExecutionComponent;
import kz.tamur.util.Funcs;
import kz.tamur.util.crypto.XmlUtil;

@Stateless(name="ConnectionManager", mappedName="ConnectionManager")

@Local(ConnectionManagerLocal.class)
public class ConnectionManager implements ConnectionManagerLocal {

    transient private Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + ConnectionManager.class.getName());
    
    private static ConnectionManager inst_;

    private static Map<String, Database> dataBases = new HashMap<String, Database>();
    private static Map<String, String> users = new HashMap<String, String>();

    private ProjectConfiguration root = new ProjectConfiguration("Конфигурации", "", null);
    private static Map<String, ProjectConfiguration> configs = new HashMap<String, ProjectConfiguration>();

    private HashMap<String,Object> initparams=new HashMap<String,Object>();
    public static String CONFIGS_FILE_PATH;

    @EJB(beanName="TransactionWatchDog", beanInterface = TransactionWatchDogLocal.class)
    private TransactionWatchDogLocal transactionWatchDog;
    
    private boolean withTransactionWatchDog=false;//Один для всех баз

    private boolean ready = false; //Один для всех баз

    public ConnectionManager() {
    	super();
    	log.info("*************************************************");
    	log.info("**************CONSTRUCTOR************************");
    	log.info("*************************************************");
    	configs.put("", root);
    }
    
    @PostConstruct
    void init() {
    	log.info("*************************************************");
    	log.info("**************@PostConstruct*********************");
    	log.info("*************************************************");
    	if (inst_ == null)
    		inst_ = this;
    	this.ready = true;
    }
    
    @PreDestroy
    void closeMutexConnections() {
    	log.info("*************************************************");
    	log.info("*************** @PreDestroy *********************");
    	log.info("*************************************************");
    	
    	this.ready = false;

    	for (Database db : dataBases.values()) {
    		db.destroyMutexConnections();
    	}
    }
    
    public static ConnectionManager instance() {
        return inst_;
    }
    
    public boolean addDatabase(Database db) throws NamingException, DriverException {
    	if (db != null && !dataBases.containsKey(db.getName())) {
			dataBases.put(db.getName(), db);
			return true;
    	}
    	return false;
    }

    public Database createDatabase(String name, String schemeName, String jndiName, String type, String replDir, boolean withTransactionWatchDog,
    		String dbSeparateClassIds,String fileStoreType) throws NamingException, DriverException {
    	if (!dataBases.containsKey(name)) {
			Context ic = new InitialContext();
			Object obj = ic.lookup(Funcs.sanitizeSQL(jndiName));
	    	ic.close();
			return new Database(name, schemeName, type, (DataSource)obj, replDir, withTransactionWatchDog, dbSeparateClassIds,fileStoreType);
    	}
    	return null;
    }

    public Database getDatabase(String name) {
    	if(!dataBases.containsKey(name)) initDatabases(name);
        return dataBases.get(name);
    }
    
    public Set<String> getDatabaseNames() {
    	return new HashSet<String>(dataBases.keySet());
    }

    public Object getInitParamByName(String paramName) {
    	synchronized (initparams) {
        	if (initparams.size() == 0)
        		loadParams();
		}
    	return initparams.get(paramName);
    }
    private synchronized void initDatabases(String name) {
        //Загрузка параметров инициализации
    	if (initparams.size() == 0)
    		loadParams();
    	
    	if(initparams.size()>0){
    		String[] dsJndiNames = (String[])initparams.get("dataSources");
    		String[] dsNames = (String[])initparams.get("dataSourceNames");
    		String[] dsTypes = (String[])initparams.get("dataSourceTypes");
    		String[] pluginsFileNames = (String[])initparams.get("pluginsFiles");
    		String[] replDirs = (String[])initparams.get("replicationDirs");
    		String[] trgExcFileNames = (String[])initparams.get("triggerExceptFiles");
    		String[] schemeNames = (String[])initparams.get("dataSourceSchemes");
            String dbSeparateClassIds = (String)initparams.get("db.separateClassIds");
            String fileStoreType = (String)initparams.get("fileStoreType");
            for (int i = 0; i < dsJndiNames.length; i++) {
            	if(!name.equals(dsNames[i])) continue;
            	
            	log.info("Инициализация конфигурации сервера приложений OR3 для dataSourceNames = " + name);
            	
            	boolean withTransactionWatchDog_=false;
	        	long checkInterval = 0;
	        	long transactionTimeout = 0;
        		if(!withTransactionWatchDog){
    	            // Проверяем параметры transactionWatchDog до создания Database чтобы исключить создание
    	            // динамичеких заглушек для Connection при их отсутствии. Необходимо для исключения потери
    	            // производительности. Сам transactionWatchDog будет запущен позже после завершения инициализации.
    	            String checkIntervalParam = (String)initparams.get("transactionWatchDog.checkInterval");
    	            String transactionTimeoutParam = (String)initparams.get("transactionWatchDog.transactionTimeout");
    	            if (checkIntervalParam != null && transactionTimeoutParam != null) {
    	            	try {
    		            	checkInterval = Long.parseLong(checkIntervalParam);
    		            	transactionTimeout = Long.parseLong(transactionTimeoutParam);
    		            	if (checkInterval > 0 && transactionTimeout > 0)
    		            		withTransactionWatchDog_ = true;
    	            	} catch (NumberFormatException e) {
    	            		log.warn("Неверное значение параметров transactionWatchDog для dataSourceNames = " + name, e);
    	            	}
    	            }
        		}
                Database db;
				try {
					db = createDatabase(dsNames[i], 
							schemeNames != null && schemeNames.length > i && schemeNames[i].length() > 0 ? schemeNames[i] : dsNames[i],
							dsJndiNames[i],
							dsTypes[i], replDirs[i], withTransactionWatchDog, dbSeparateClassIds,fileStoreType);
	                if (db != null) {
	                    SrvOrLang.addPluginsFile(dsNames[i], pluginsFileNames[i]);
	                    if (trgExcFileNames != null && trgExcFileNames.length > i && trgExcFileNames[i].length() > 0) {
	                        Driver2.addTriggerExceptFile(dsNames[i], trgExcFileNames[i]);
	                    }
	                    db.setReadyForConnection(true);
	                	addDatabase(db);
	                	
	                	Session s = null;
	                	try {
	                		s = SrvUtils.getSession(dsNames[i], "sys", null);
	                		ExecutionComponent exeComp = new ExecutionComponent(s);
	                		Session.addExeComp(dsNames[i], exeComp);
	                	} catch (Exception e) {
	                		log.error(e, e);
	                	} finally {
	                		if (s!= null)
	                			s.release();
	                	}
	                	
	                }
				} catch (DriverException e) {
            		log.error("Ошибка при подключении к DataSource с jndiName = '" + dsJndiNames[i] + "' для dataSourceNames = " + name, e);
            		log.error(e, e);
				} catch (NamingException e) {
            		log.error("Не найден DataSource с jndiName = '" + dsJndiNames[i] + "' для dataSourceNames = " + name, e);
            		log.error(e, e);
				}
	            if (withTransactionWatchDog_) {
	            	try {
		            	transactionWatchDog.start(checkInterval, transactionTimeout);
		            	log.info("TransactionWatchDog started. Transaction timeout: " + transactionTimeout + " ms."
		            			+ " Checking interval: " + checkInterval + " ms.");
		            	withTransactionWatchDog=true;
					} catch (Exception e) {
	            		log.warn("Неверная инициализация transactionWatchDog для dataSourceNames = " + name, e);
					}
	            }
            }
    	}
    		
    }

	public void addUser(String userName, String userPass) {
		// TODO Auto-generated method stub
		users.put(userName, userPass);
	}
    
	public boolean authorizeUser(String userName, String userPass) {
		return userName != null && userPass != null && userPass.equals(users.get(userName));
	}
	
	public void addConfiguration(String dsName, ProjectConfiguration config) {
		configs.put(dsName, config);
	}

	public void removeConfiguration(String dsName) {
		configs.remove(dsName);
	}

	public ProjectConfiguration getRoot() {
		return root;
	}
	
	public ProjectConfiguration getConfiguration(String dsName) {
		return configs.get(dsName);
	}
	
	public List<ProjectConfiguration> getChildConfigurations(String dsName) {
		if (dsName == null) {
			List<ProjectConfiguration> res = new ArrayList<ProjectConfiguration>();
			res.add(root);
			return res;
		}
		if (configs.get(dsName) == null)
			return new ArrayList<ProjectConfiguration>();
		else
			return configs.get(dsName).getChildren();
	}

	public void saveAllConfigurations() {
		Element e = new Element("projects");
		if (root.getChildren().size() > 0) {
			for (int i = 0; i < root.getChildren().size(); i++) {
				e.addContent(root.getChildren().get(i).toXml());
			}
		}
		for (String name : users.keySet()) {
			e.addContent(new Element("user").setAttribute("name", name).setAttribute("password", users.get(name)));
		}
		
		try {
	        FileOutputStream os = new FileOutputStream(CONFIGS_FILE_PATH);
	        XMLOutputter out = new XMLOutputter();
	        out.getFormat().setEncoding("UTF-8");
	        out.output(e, os);
	        os.close();
		} catch (Exception ex) {
			log.error(ex, ex);
		}
	}
    private void loadParams() {
    	String filePath = Funcs.getSystemProperty("initParamsFile");
    	HashMap<String, String> obligatoryParam = new HashMap<String, String>();
    	if (Funcs.isValid(filePath)){
    		File xml = new File(filePath);
        	log.error(xml.getAbsolutePath());
	        if (xml != null && xml.exists()) {
		    	List<Element> parList=null;
	            try {
		        	byte[] bytes = Funcs.read(xml);
	            	String dataStr = kz.tamur.util.Funcs.normalizeInput(new String(bytes, "UTF-8"));
	            	dataStr = kz.tamur.util.Funcs.validate(dataStr);
	            	
	                ByteArrayInputStream is = new ByteArrayInputStream(dataStr.getBytes("UTF-8"));

		            SAXBuilder builder = XmlUtil.createSaxBuilder2();
	            	parList = new ArrayList<Element>(builder.build(is).getRootElement().getChildren());
	            	is.close();
	            } catch (JDOMException e) {
			    	log.error("Ошибка при разборе файла с параметрами инициализации '" + filePath + "', указанный с помощью -DinitParamsFile");
	            	log.error(e, e);
	            } catch (IOException e) {
			    	log.error("Ошибка при разборе файла с параметрами инициализации '" + filePath + "', указанный с помощью -DinitParamsFile");
	    			log.error(e, e);
	            }
	            if (parList != null) {
		        	for (int i = 0; i < parList.size(); i++) {
	                    Element item = parList.get(i);
	                    String name = item.getAttribute("name").getValue();
	                    String value = item.getAttribute("value").getValue();
	                    obligatoryParam.put(name,value);
	                    initparams.put(name, parsParamValue(name,value));
	                }
		        	log.info("\r\n" + "ПАРAМЕТРЫ КОМПОНЕНТОВ ФАЙЛА WEB.XML НЕОБХОДИМЫХ ДЛЯ ЗАГРУЗКИ СЕРВЕРА" + "\r\n");
		        	for (Map.Entry<String, String> entry : obligatoryParam.entrySet()){
		        		String nameParam = entry.getKey();
		        		String valueParam = entry.getValue();
		        		if(valueParam.isEmpty()){
		        			log.error("!!!!!!!!!!!!!ДОБАВЬТЕ ЗНАЧЕНИЕ VALUE В ДОКУМЕНТЕ WEB.XML КОМПОНЕНТУ " + nameParam);
		        		} else  {
		        		log.info(entry.getKey() + " = " + entry.getValue());
		        		}
		        		if (nameParam.contains("dataSources")){
		        			try {
				        		InitialContext ctx = new InitialContext();
				        		DataSource ds = (DataSource) ctx.lookup(valueParam);
				    	    	ctx.close();
				        	} catch (NamingException e) { 
				        		log.error("Ошибка при подключении к DataSource с jndi-name, проверьте наличие объекта и правильность написания jndi-name");
				        		}
		        		}
		        	}
	            }
	        } else {
		    	log.error("Не найден файл с параметрами инициализации '" + filePath + "' (" + xml.getAbsolutePath() + "), указанный с помощью -DinitParamsFile");
	        }
	    } else {
	    	log.error("Не задан файл с параметрами инициализации! Задайте его в параметрах запуска сервера с помощью атрибута -DinitParamsFile");
	    }
    }
    
    private Object parsParamValue(String name,String value){
		Object res=null;
		if ("dataSources".equals(name)) {
			res = (value != null && value.length() > 0) ? value.split(";") : new String[0];
		} else if ("dataSourceNames".equals(name)
				|| "dataSourceTypes".equals(name)
				|| "dataSourceSchemes".equals(name)
				|| "pluginsFiles".equals(name)
				|| "tpropsFiles".equals(name)
				|| "replicationDirs".equals(name) 
				|| "triggerExceptFiles".equals(name)) {
			res = (value != null) ? value.split(";") : null;
		} else if ("transactionWatchDog.checkInterval".equals(name)
				|| "transactionWatchDog.transactionTimeout".equals(name)
				|| "errorsNotify".equals(name)
				|| "db.separateClassIds".equals(name)
				|| "activateChat".equals(name)){
			res = value;
		}else
			res = value;
			
        return res;
    }

	public boolean isReady() {
		return ready;
	}
}
