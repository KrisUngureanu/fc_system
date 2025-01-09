package kz.tamur.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Formatter;
import java.util.logging.Handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import kz.tamur.lang.parser.LangUtils;
import kz.tamur.or3.JdbcAppender;

public class LoggingHelper {
	private static Log log = LogFactory.getLog(LoggingHelper.class); 
	
	private static LoggingHelper instance;
	private static String log4jFileName;
	
	private List<List<String>> currentAppenders = new ArrayList<List<String>>();
	private List<List<String>> currentLoggers = new ArrayList<List<String>>();
	private List<List<String>> lastAppenders = new ArrayList<List<String>>();
	private List<List<String>> lastLoggers = new ArrayList<List<String>>();

	private List<Object> configuredLoggers = new ArrayList<Object>();

	private boolean configured = false;

	public static synchronized LoggingHelper instance() {
		if (instance == null) {
			instance = new LoggingHelper();
		}
		return instance;
	}
	
	public List<List<String>> getLoggers() {
		return currentLoggers;
	}

	public List<List<String>> getAppenders() {
		return currentAppenders;
	}

	public synchronized void configure(String fileName) {
		try {
			if (configured) return;
			configured = true;
			
			// Запоминаем имя файла конфигурации
			if (fileName.startsWith("file://")) fileName = fileName.substring(6);
			else if (fileName.startsWith("file:/")) fileName = fileName.substring(5);
			log4jFileName = fileName;
			
			log.info("Log parameters path: " + new File(log4jFileName).getAbsolutePath());
			
			
			// Читаем конфигурацию
			Properties ps = new Properties();
			ps.load(new FileInputStream(fileName));

			// Считываем массив сконфигурированных аппендеров
			for (Iterator<String> it = ps.stringPropertyNames().iterator(); it.hasNext(); ) {
				String pn = it.next();
				if (pn.startsWith("log4j.appender.") && pn.lastIndexOf('.') == 14) {
					String name = pn.substring(15);
					List<String> appender = getAppender(ps, name);
					lastAppenders.add(appender);
				}
			}

			// Определяем тип логгера
			java.util.logging.Logger root = java.util.logging.LogManager.getLogManager().getLogger("");
			log.info("Logger class: " + root.getClass().getName());

			Map<String, List<Object>> newAppendersMap = new HashMap<String, List<Object>>();
			Map<String, Object> appendersByName = new HashMap<String, Object>();
			
			// JBoss logger
			if ("org.jboss.logmanager.Logger".equals(root.getClass().getName())) {
				// Ссылка на консоль аппендер
				Handler ch = null;
				for (Handler h : root.getHandlers()) {
					if ("org.jboss.logmanager.handlers.ConsoleHandler".equals(h.getClass().getName()))
						ch = h;
				}
				
				// Создаем аппендеры для корневого логгера
				String str = ps.getProperty("log4j.rootLogger");
				if (str != null) {
					String[] appenders = str.split(",");
					String level = appenders[0].trim();
					
					List<Object> newAppenders = new ArrayList<Object>();
					newAppendersMap.put("rootLogger", newAppenders);
					
					for (int i=1; i<appenders.length; i++) {
						String appenderName = appenders[i].trim();
						
						// Если логгер пишет в несколько аппендеров, добавляем его несколько раз в массив логгеров
						List<String> logger = Arrays.asList(new String[4]);
						logger.set(0, "rootLogger");
						logger.set(1, level);
						logger.set(2, appenderName);
						logger.set(3, "Нет");
						
						Handler h = getHandler(ps, appenderName, level, appendersByName);
						root.setLevel(java.util.logging.Level.parse(level));
						if ("org.jboss.logmanager.handlers.ConsoleHandler".equals(h.getClass().getName())) {
							if (ch == null) ch = h;
							newAppenders.add(ch);
						} else {
							newAppenders.add(h);
						}
						
						lastLoggers.add(logger);
					}
				}
				
				// Создаем аппендеры для остальных логгеров
				for (Iterator<String> it = ps.stringPropertyNames().iterator(); it.hasNext(); ) {
					String pn = it.next();
					if (pn.startsWith("log4j.logger.")) {
						String name = pn.substring(13);
						str = ps.getProperty(pn);
						if (str != null) {
							String[] appenders = str.split(",");
							String level = appenders[0].trim();
							
							java.util.logging.Logger l = java.util.logging.Logger.getLogger(name);

							List<Object> newAppenders = new ArrayList<Object>();
							newAppendersMap.put(name, newAppenders);
							
							if (l != null) {
								log.info(name + " loading handler class: " + l.getClass().getName());
								// Нужно ли логгировать в родительские логи?
								boolean add = !"false".equals(ps.getProperty("log4j.additivity." + name));
								l.setUseParentHandlers(add);
		
								for (int i=1; i<appenders.length; i++) {
									String appenderName = appenders[i].trim();
									List<String> logger = Arrays.asList(new String[4]);
									logger.set(0, name);
									logger.set(1, level);
									logger.set(2, appenderName);
									logger.set(3, add ? "Да" : "Нет");
									
									Handler h = getHandler(ps, appenderName, level, appendersByName);
									l.setLevel(java.util.logging.Level.parse(level));
									if ("org.jboss.logmanager.handlers.ConsoleHandler".equals(h.getClass().getName())) {
										if (ch == null) ch = h;
										newAppenders.add(ch);
									} else {
										newAppenders.add(h);
									}
									lastLoggers.add(logger);
								}
							}
						}
					}
				}
				for (String name : newAppendersMap.keySet()) {
					java.util.logging.Logger l = ("rootLogger".equals(name)) ? root
											: java.util.logging.Logger.getLogger(name);
					// Удаляем все аппендеры из логгера
					List<Handler> toDel = new ArrayList<Handler>();
					for (Handler h : l.getHandlers()) {
						log.info(name + " removing handler class: " + h.getClass().getName());
						toDel.add(h);
					}
					
					for (Handler h : toDel) l.removeHandler(h);

					List<Object> newAppenders = newAppendersMap.get(name);
					for (Object newAppender : newAppenders) {
						log.info(name + " adding handler class: " + newAppender.getClass().getName());
						l.addHandler((Handler)newAppender);
					}
					if (!configuredLoggers.contains(l)) configuredLoggers.add(l);
				}
			} 
			// log4j logger
			else {
				Logger root4j = Logger.getRootLogger();

				// Ссылка на консоль аппендер
				Appender ca = null;
				for (Enumeration en = root4j.getAllAppenders(); en.hasMoreElements();) {
					Appender a = (Appender) en.nextElement();
					if (a instanceof ConsoleAppender)
						ca = a;
				}
				
				String str = ps.getProperty("log4j.rootLogger");
				if (str != null) {
					String[] appenders = str.split(",");
					String level = appenders[0].trim();
					
					List<Object> newAppenders = new ArrayList<Object>();
					newAppendersMap.put("rootLogger", newAppenders);

					for (int i=1; i<appenders.length; i++) {
						String appenderName = appenders[i].trim();
						List<String> logger = Arrays.asList(new String[4]);
						logger.set(0, "rootLogger");
						logger.set(1, level);
						logger.set(2, appenderName);
						logger.set(3, "Нет");

						Appender a = getAppender(ps, appenderName, level, appendersByName);
						root4j.setLevel(Level.toLevel(level));
						if (a instanceof ConsoleAppender) {
							if (ca == null) ca = a;
							newAppenders.add(ca);
						} else {
							newAppenders.add(a);
						}
						
						lastLoggers.add(logger);
					}
				}
				
				for (Iterator<String> it = ps.stringPropertyNames().iterator(); it.hasNext(); ) {
					String pn = it.next();
					if (pn.startsWith("log4j.logger.")) {
						String name = pn.substring(13);
						str = ps.getProperty(pn);
						if (str != null) {
							String[] appenders = str.split(",");
							String level = appenders[0].trim();
							
							Logger l4j = Logger.getLogger(name);

							if (l4j != null) {
								log.info("Logger4 class: " + l4j.getClass().getName());

								List<Object> newAppenders = new ArrayList<Object>();
								newAppendersMap.put(name, newAppenders);

								// Нужно ли логгировать в родительские логи?
								boolean add = !"false".equals(ps.getProperty("log4j.additivity." + name));
								l4j.setAdditivity(add);
		
								for (int i=1; i<appenders.length; i++) {
									String appenderName = appenders[i].trim();
									List<String> logger = Arrays.asList(new String[4]);
									logger.set(0, name);
									logger.set(1, level);
									logger.set(2, appenderName);
									logger.set(3, add ? "Да" : "Нет");

									Appender a = getAppender(ps, appenderName, level, appendersByName);
									l4j.setLevel(Level.toLevel(level));
									if (a instanceof ConsoleAppender) {
										if (ca == null) ca = a;
										newAppenders.add(ca);
									} else {
										newAppenders.add(a);
									}
									lastLoggers.add(logger);
								}
							}
						}
					}
				}
				
				for (String name : newAppendersMap.keySet()) {
					Logger l4j = ("rootLogger".equals(name)) ? root4j : Logger.getLogger(name);
					// Удаляем все аппендеры из логгера
					List<Appender> toDel = new ArrayList<Appender>();
					for (Enumeration en = l4j.getAllAppenders(); en.hasMoreElements();) {
						Appender a = (Appender) en.nextElement();
						log.info(name + " removing appender class: " + a.getClass().getName());
						toDel.add(a);
					}
					
					for (Appender a : toDel) l4j.removeAppender(a);

					List<Object> newAppenders = newAppendersMap.get(name);
					for (Object newAppender : newAppenders) {
						if (newAppender != null) {
							l4j.addAppender((Appender)newAppender);
							log.info(name + " adding appender class: " + newAppender.getClass().getName());
						}
					}
					if (!configuredLoggers.contains(l4j)) configuredLoggers.add(l4j);				}
			}
			copyToCurrentConfigurarion();
		} catch (Exception e) {
			log.error(e, e);
		}
	}
	
	private void copyToCurrentConfigurarion() {
		currentLoggers.clear();
		for (List<String> l : lastLoggers) {
			List<String> l2 = new ArrayList<String>();
			l2.addAll(l);
			currentLoggers.add(l2);
		}
		currentAppenders.clear();
		for (List<String> l : lastAppenders) {
			List<String> l2 = new ArrayList<String>();
			l2.addAll(l);
			currentAppenders.add(l2);
		}
	}

	public void rollback() {
		copyToCurrentConfigurarion();
	}
	
	public boolean apply() {
		try {
			Properties ps = new Properties() {
			    @Override
			    public synchronized Enumeration<Object> keys() {
			    	SortedSet<Object> set = new TreeSet<Object>(new Comparator<Object>() {

						@Override
						public int compare(Object o1, Object o2) {
							String s1 = (String)o1;
							String s2 = (String)o2;
							if ("log4j.rootLogger".equals(s1)) return -1;
							if ("log4j.rootLogger".equals(s1)) return -1;
							
							return s1.compareTo(s2);
						}
					});
			    	set.addAll(super.keySet());
			        return Collections.enumeration(set);
			    }
			};
			
			lastAppenders.clear();
			for (List<String> l : currentAppenders) {
				String name = l.get(0);
				String direction = l.get(1);
				if ("Файл".equals(direction)) {
					ps.setProperty("log4j.appender." + name, "org.apache.log4j.DailyRollingFileAppender");
					ps.setProperty("log4j.appender." + name + ".File", l.get(2));
					ps.setProperty("log4j.appender." + name + ".DatePattern", ".yyyy-MM-dd");
				} else if ("Консоль".equals(direction)) {
					ps.setProperty("log4j.appender." + name, "org.apache.log4j.ConsoleAppender");
				} else if ("БД".equals(direction)) {
					ps.setProperty("log4j.appender." + name, "kz.tamur.or3.JdbcAppender");
					ps.setProperty("log4j.appender." + name + ".dataSource", l.get(2));
					if (l.size() > 3 && l.get(3).length() > 0)
						ps.setProperty("log4j.appender." + name + ".scheme", l.get(3));
				}
				if (!"БД".equals(direction)) {
					ps.setProperty("log4j.appender." + name + ".layout", "org.apache.log4j.PatternLayout");
					ps.setProperty("log4j.appender." + name + ".layout.ConversionPattern", l.size() > 3 ? l.get(3) : "");
				}
				
				List<String> l2 = new ArrayList<String>();
				l2.addAll(l);
				lastAppenders.add(l2);
			}

			// Ссылка на консоль аппендер
			Handler ch = null;
			Appender ca = null;
			// Определяем тип логгера
			java.util.logging.Logger root = java.util.logging.LogManager.getLogManager().getLogger("");
			log.info("Logger class: " + root.getClass().getName());

			List<String> oldLoggers = new ArrayList<String>();
			for (List<String> l : lastLoggers) {
				String name = l.get(0);
				oldLoggers.add(name);
				
				// JBoss logger
				if ("org.jboss.logmanager.Logger".equals(root.getClass().getName())) {
					if ("rootLogger".equals(name)) {
						for (Handler h : root.getHandlers()) {
							if ("org.jboss.logmanager.handlers.ConsoleHandler".equals(h.getClass().getName()))
								ch = h;
						}
					} else {
						java.util.logging.Logger logger = java.util.logging.Logger.getLogger(name);
						if (logger != null) {
							for (Handler h : logger.getHandlers()) {
								if ("org.jboss.logmanager.handlers.ConsoleHandler".equals(h.getClass().getName()))
									ch = h;
								
								log.info(name + " handler class: " + h.getClass().getName());
							}
						}
					}
				} else {
					Logger root4j = Logger.getRootLogger();
					if ("rootLogger".equals(name)) {
						for (Enumeration en = root4j.getAllAppenders(); en.hasMoreElements();) {
							Appender a = (Appender) en.nextElement();
							if (a instanceof ConsoleAppender)
								ca = a;
						}
					} else {
						Logger l4j = Logger.getLogger(name);
						if (l4j != null) {
							log.info("Logger4 class: " + l4j.getClass().getName());
							// Удаляем все аппендеры из логгера
							for (Enumeration en = l4j.getAllAppenders(); en.hasMoreElements();) {
								Appender a = (Appender) en.nextElement();
								if (a instanceof ConsoleAppender)
									ca = a;
								
								log.info(name + " appender class: " + a.getClass().getName());
							}
						}
					}
				}
			}
			Map<String, List<Object>> newAppendersMap = new HashMap<String, List<Object>>();
			Map<String, Object> appendersByName = new HashMap<String, Object>();
			lastLoggers.clear();
			for (List<String> l : currentLoggers) {
				String name = l.get(0);
				String level = l.get(1);
				String appenderName = l.get(2);
				boolean additivity = "Да".equals(l.get(3));

				List<Object> newAppenders = newAppendersMap.get(name);
				if (newAppenders == null) {
					newAppenders = new ArrayList<Object>();
					newAppendersMap.put(name, newAppenders);
				}

				if ("rootLogger".equals(name)) {
					String p = ps.getProperty("log4j.rootLogger");
					if (p == null)
						p = level + "," + appenderName;
					else
						p += "," + appenderName;
					
					ps.setProperty("log4j.rootLogger", p);
					
					// JBoss logger
					if ("org.jboss.logmanager.Logger".equals(root.getClass().getName())) {
						log.info(name + " loading handler class: " + l.getClass().getName());

						Handler h = getHandler(ps, appenderName, level, appendersByName);
						root.setLevel(java.util.logging.Level.parse(level));
						if ("org.jboss.logmanager.handlers.ConsoleHandler".equals(h.getClass().getName())) {
							if (ch == null) ch = h;
							newAppenders.add(ch);
						} else
							newAppenders.add(h);
					} else {
						Appender a = getAppender(ps, appenderName, level, appendersByName);
						Logger root4j = Logger.getRootLogger();
						root4j.setLevel(Level.toLevel(level));
						if (a instanceof ConsoleAppender) {
							if (ca == null) ca = a;
							newAppenders.add(ca);
						} else
							newAppenders.add(a);
					}
				} else {
					String p = ps.getProperty("log4j.logger." + name);
					if (p == null)
						p = level + "," + appenderName;
					else
						p += "," + appenderName;
					
					ps.setProperty("log4j.logger." + name, p);
					ps.setProperty("log4j.additivity." + name, String.valueOf(additivity));

					// JBoss logger
					if ("org.jboss.logmanager.Logger".equals(root.getClass().getName())) {
						java.util.logging.Logger logger = java.util.logging.Logger.getLogger(name);
						Handler h = getHandler(ps, appenderName, level, appendersByName);
						logger.setLevel(java.util.logging.Level.parse(level));
						if ("org.jboss.logmanager.handlers.ConsoleHandler".equals(h.getClass().getName())) {
							if (ch == null) ch = h;
							newAppenders.add(ch);
						} else
							newAppenders.add(h);
					} else {
						Appender a = getAppender(ps, appenderName, level, appendersByName);
						Logger l4j = Logger.getLogger(name);
						l4j.setLevel(Level.toLevel(level));
						if (a instanceof ConsoleAppender) {
							if (ca == null) ca = a;
							newAppenders.add(ca);
						} else
							newAppenders.add(a);
					}
				}
				
				List<String> l2 = new ArrayList<String>();
				l2.addAll(l);
				lastLoggers.add(l2);
			}
			ps.store(new FileOutputStream(log4jFileName), null);
			
			for (String name : oldLoggers) {
				// JBoss logger
				if ("org.jboss.logmanager.Logger".equals(root.getClass().getName())) {
					java.util.logging.Logger l = ("rootLogger".equals(name)) ? root
											: java.util.logging.Logger.getLogger(name);
					// Удаляем все аппендеры из логгера
					List<Handler> toDel = new ArrayList<Handler>();
					for (Handler h : l.getHandlers()) {
						toDel.add(h);
					}

					for (Handler h : toDel) {
						log.info(name + " removing handler class: " + h.getClass().getName());
						l.removeHandler(h);
						if (h.getClass().getName().equals("org.jboss.logmanager.handlers.PeriodicRotatingFileHandler"))
							h.close();
					}
				} else {
					Logger l4j = ("rootLogger".equals(name)) ? Logger.getRootLogger() : Logger.getLogger(name);
					// Удаляем все аппендеры из логгера
					List<Appender> toDel = new ArrayList<Appender>();
					for (Enumeration en = l4j.getAllAppenders(); en.hasMoreElements();) {
						Appender a = (Appender) en.nextElement();
						toDel.add(a);
					}
					for (Appender a : toDel) {
						log.info(name + " removing appender class: " + a.getClass().getName());
						l4j.removeAppender(a);
						if (a instanceof DailyRollingFileAppender)
							a.close();
					}
				}
			}

			configuredLoggers.clear();
			for (String name : newAppendersMap.keySet()) {
				boolean add = !"false".equals(ps.getProperty("log4j.additivity." + name));
				// JBoss logger
				if ("org.jboss.logmanager.Logger".equals(root.getClass().getName())) {
					java.util.logging.Logger l = ("rootLogger".equals(name)) ? root
											: java.util.logging.Logger.getLogger(name);
					List<Object> newAppenders = newAppendersMap.get(name);
					for (Object newAppender : newAppenders) {
						log.info(name + " adding handler class: " + newAppender.getClass().getName());
						l.addHandler((Handler)newAppender);
						l.setUseParentHandlers(add);
					}
					if (!configuredLoggers.contains(l)) configuredLoggers.add(l);
				} else {
					Logger l4j = ("rootLogger".equals(name)) ? Logger.getRootLogger() : Logger.getLogger(name);
					List<Object> newAppenders = newAppendersMap.get(name);
					for (Object newAppender : newAppenders) {
						if (newAppender != null) {
							log.info(name + " adding appender class: " + newAppender.getClass().getName());
							l4j.addAppender((Appender)newAppender);
							l4j.setAdditivity(add);
						}
					}
					if (!configuredLoggers.contains(l4j)) configuredLoggers.add(l4j);				}
			}
			
			return true;
		} catch (Exception e) {
			log.error(e, e);
		}
		return false;
	}
	
	private Appender getAppender(Properties ps, String name, String level, Map<String, Object> appendersByName) {
		AppenderSkeleton a = (AppenderSkeleton) appendersByName.get(name);
		if (a == null) {
			String str = ps.getProperty("log4j.appender." + name);
			if ("org.apache.log4j.ConsoleAppender".equals(str)) {
				a = new ConsoleAppender();
			} else if ("org.apache.log4j.DailyRollingFileAppender".equals(str)) {
				a = new DailyRollingFileAppender();
				
				str = ps.getProperty("log4j.appender." + name + ".File");
				if (str != null) {
					((FileAppender)a).setFile(str);
				}
				str = ps.getProperty("log4j.appender." + name + ".DatePattern");
				if (str != null) {
					((DailyRollingFileAppender)a).setDatePattern(str);
				}
			} else if ("kz.tamur.or3.JdbcAppender".equals(str)) {
				JdbcAppender jdbcAppender = new JdbcAppender();
				try {
					jdbcAppender.setDataSource(ps.getProperty("log4j.appender." + name + ".dataSource"));
					jdbcAppender.setScheme(ps.getProperty("log4j.appender." + name + ".scheme"));
					
					a = jdbcAppender;
				} catch (Exception ex) {
					log.error("Failed to configure JdbcAppender " + name, ex);
				}
			}
			
			if (a != null) {
				a.setName(name);
				//a.setThreshold(Level.toLevel(level, Level.DEBUG));
				
				str = ps.getProperty("log4j.appender." + name + ".layout");
				if ("org.apache.log4j.PatternLayout".equals(str)) {
					PatternLayout pl = new PatternLayout();
					str = ps.getProperty("log4j.appender." + name + ".layout.ConversionPattern");
					if (str != null)
						pl.setConversionPattern(str);
					
					a.setLayout(pl);
				}
				
				a.activateOptions();
				appendersByName.put(name, a);
			}
		}
		return a;
	}

	private Handler getHandler(Properties ps, String name, String level, Map<String, Object> appendersByName) throws Exception {
		Handler a = (Handler) appendersByName.get(name);
		if (a == null) {
			String str = ps.getProperty("log4j.appender." + name);
			if ("org.apache.log4j.ConsoleAppender".equals(str)) {
				a = (Handler) LangUtils.getType("org.jboss.logmanager.handlers.ConsoleHandler", null).newInstance();
			} else if ("org.apache.log4j.DailyRollingFileAppender".equals(str)) {
				str = ps.getProperty("log4j.appender." + name + ".File");
				
				Class<?> appenderCls = LangUtils.getType("org.jboss.logmanager.handlers.PeriodicRotatingFileHandler", null);
				a = (Handler) appenderCls.getConstructor(String.class, boolean.class).newInstance(str, true);
				Method m = appenderCls.getMethod("setAutoFlush", boolean.class);
				m.invoke(a, true);
				
				str = ps.getProperty("log4j.appender." + name + ".DatePattern");
				if (str != null) {
					m = appenderCls.getMethod("setSuffix", String.class);
					m.invoke(a, str);
				}
			}
			
			if (a != null) {
				//a.setLevel(java.util.logging.Level.parse(level));
				
				str = ps.getProperty("log4j.appender." + name + ".layout");
				if ("org.apache.log4j.PatternLayout".equals(str)) {
					str = ps.getProperty("log4j.appender." + name + ".layout.ConversionPattern");
					if (str != null) {
						Class<?> patternCls = LangUtils.getType("org.jboss.logmanager.formatters.PatternFormatter", null);
						Formatter formatter = (Formatter) patternCls.newInstance();
						Method m = patternCls.getMethod("setPattern", String.class);
						m.invoke(formatter, str);
						a.setFormatter(formatter);
					}
				}
				
				appendersByName.put(name, a);
			}
		}
		return a;
	}

	private List<String> getAppender(Properties ps, String name) {
		List<String> res = Arrays.asList(new String[4]);
		res.set(0, name);
		String str = ps.getProperty("log4j.appender." + name);
		if ("org.apache.log4j.ConsoleAppender".equals(str)) {
			res.set(1, "Консоль");
		} else if ("org.apache.log4j.DailyRollingFileAppender".equals(str)) {
			res.set(1, "Файл");
			str = ps.getProperty("log4j.appender." + name + ".File");
			res.set(2, str);
		} else if ("kz.tamur.or3.JdbcAppender".equals(str)) {
			res.set(1, "БД");
			str = ps.getProperty("log4j.appender." + name + ".dataSource");
			res.set(2, str);
			str = ps.getProperty("log4j.appender." + name + ".scheme");
			res.set(3, str);
		}
		
		if (!"kz.tamur.or3.JdbcAppender".equals(str)) {
			str = ps.getProperty("log4j.appender." + name + ".layout");
			if ("org.apache.log4j.PatternLayout".equals(str)) {
				str = ps.getProperty("log4j.appender." + name + ".layout.ConversionPattern");
				if (str != null)
					res.set(3, str);
			}
		}
		return res;
	}
}
