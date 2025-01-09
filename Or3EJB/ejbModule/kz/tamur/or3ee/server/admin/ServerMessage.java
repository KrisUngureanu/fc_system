package kz.tamur.or3ee.server.admin;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import kz.tamur.ods.Driver2;
import kz.tamur.or3.util.SystemEvent;
import kz.tamur.or3ee.common.AttrChangeListener;
import kz.tamur.or3ee.common.MetadataChangeListener;
import kz.tamur.or3ee.common.ModelChangeListener;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.or3ee.server.kit.Cache;
import kz.tamur.or3ee.server.kit.CacheListener;
import kz.tamur.or3ee.server.kit.CacheUtils;
import kz.tamur.or3ee.server.kit.SrvUtils;
import kz.tamur.util.Funcs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cifs.or2.client.ResponseWaiter;
import com.cifs.or2.kernel.AttrChange;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnMethod;
import com.cifs.or2.kernel.MessageNote;
import com.cifs.or2.kernel.Note;
import com.cifs.or2.server.ServerUserSession;
import com.cifs.or2.server.Session;
import com.cifs.or2.server.SessionManager;

public class ServerMessage implements Serializable {
	private static final Log log = LogFactory.getLog("ServerMessage" + (UserSession.SERVER_ID != null ? ("." + UserSession.SERVER_ID) : ""));
	private static Cache<String, ServerMessage> msgCache = CacheUtils.getCache("ServerMessageCache");
	 
	public static final int ACTION_KILL_SESSION = 1;
	public static final int ACTION_SEND_MESSAGE = 2;
	public static final int ACTION_ADD_ATTR = 3;
	public static final int ACTION_DEL_ATTR = 4;
	public static final int ACTION_CHG_ATTR = 5;
	public static final int ACTION_ADD_CLASS = 6;
	public static final int ACTION_DEL_CLASS = 7;
	public static final int ACTION_CHG_CLASS = 8;
	public static final int ACTION_ADD_METH = 9;
	public static final int ACTION_DEL_METH = 10;
	public static final int ACTION_CHG_METH = 11;
	
	public static final int ACTION_CHG_IFC = 12;
	public static final int ACTION_CHG_PRD = 13;
	public static final int ACTION_BLOCK_SERVER = 14;
	public static final int ACTION_UNBLOCK_SERVER = 15;
	public static final int ACTION_CANCEL_PROCESS = 16;

	public static final int ACTION_RELOAD_FILTER = 17;

	public static final int ACTION_OBJECT_CHANGED = 20;
//	public static final int ACTION_NOTIFICATION = 21;

	public static final int ACTION_LONG_TR_COMMITTED = 22;
	public static final int ACTION_LONG_TR_ROLLBACKED = 23;
	public static final int ACTION_ROLLBACK = 24;

	private String dsName;
	private int action;
	private String msgId;
	private String serverId;
	private UUID uuid;
	private Note note;
	private Object obj;
	private Object oldObj;
	private List<AttrChange> changes;
	private String ip;
	
	private static Map<UUID, List<AttrChange>> attrChanges = new HashMap<UUID, List<AttrChange>>();
	private static Map<UUID, List<Long>> committedTrs = new HashMap<>();
	private static Map<UUID, List<Long>> rollbackedTrs = new HashMap<>();
	
	private static Timer timer = new Timer();

	private static final long REMOVE_SERVER_MESSAGE_TIMEOUT = 30_000;

	static {
		msgCache.addEntryListener(new ServerMessageCacheListener());
	}
	
	private void readObject(ObjectInputStream is) throws ClassNotFoundException, IOException {
		is.defaultReadObject();
	}
	
	private static synchronized void sendMessageToCluster(ServerMessage msg) {
		log.info("ADD MSG action (" + msg.action + "), size = " + msgCache.getSize());
		msgCache.put(msg.msgId, msg);
		try {
			timer.schedule(new RemoveMessageFromCacheTask(msg.msgId, msg.action), REMOVE_SERVER_MESSAGE_TIMEOUT);
		} catch (Throwable e) {
			try {
				synchronized (timer) {
					timer = new Timer();
				}
				timer.schedule(new RemoveMessageFromCacheTask(msg.msgId, msg.action), REMOVE_SERVER_MESSAGE_TIMEOUT);
				e.printStackTrace();
			} catch (Throwable e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public static void sendKillMessage(String dsName, UUID uuid) {
		ServerMessage msg = new ServerMessage();
		msg.msgId = UUID.randomUUID().toString();
		msg.action = ACTION_KILL_SESSION;
		msg.dsName = dsName;
		msg.uuid = uuid;
		
		sendMessageToCluster(msg);
	}

	public static void sendReloadFilter(String dsName, long fid) {
		ServerMessage msg = new ServerMessage();
		msg.msgId = UUID.randomUUID().toString();
		msg.action = ACTION_RELOAD_FILTER;
		msg.dsName = dsName;
		msg.obj = fid;
		
		sendMessageToCluster(msg);
	}

	public static void sendBlockServer(String dsName, String ip, int action) {
		ServerMessage msg = new ServerMessage();
		msg.msgId = UUID.randomUUID().toString();
		msg.action = action;
		msg.dsName = dsName;
		msg.ip = ip;
		
		sendMessageToCluster(msg);
	}
	
	public static void sendMessage(String dsName, UUID to, Note note) {
		ServerMessage msg = new ServerMessage();
		msg.msgId = UUID.randomUUID().toString();
		msg.action = ACTION_SEND_MESSAGE;
		msg.dsName = dsName;
		msg.uuid = to;
		msg.note = note;
		
		sendMessageToCluster(msg);
	}

	public static void sendMessage(String serverId, String dsName, int action, Object obj, Object oldObj) {
		ServerMessage msg = new ServerMessage();
		msg.msgId = UUID.randomUUID().toString();
		msg.action = action;
		msg.serverId = serverId;
		msg.dsName = dsName;
		msg.obj = obj;
		msg.oldObj = oldObj;
		
		sendMessageToCluster(msg);
	}

	public static void sendMessage(String serverId, String dsName, UUID from, AttrChange ch) {
		if (ch.attrId > -1) {
	    	List<AttrChange> l = attrChanges.get(from);
			if (l == null) {
				l = new ArrayList<AttrChange>();
				attrChanges.put(from, l);
			}
			l.add(new AttrChange(ch.obj, ch.attrId, ch.langId, ch.trId));
		} else if (ch.attrId == -3) {
			ServerMessage msg = new ServerMessage();
			msg.msgId = UUID.randomUUID().toString();
			msg.serverId = serverId;
			msg.dsName = dsName;
			msg.uuid = from;
			msg.obj = ch.trId;
			msg.action = ACTION_LONG_TR_COMMITTED;
			
			sendMessageToCluster(msg);
			
	    	List<Long> trs = committedTrs.get(from);
			if (trs == null) {
				trs = new ArrayList<Long>();
				committedTrs.put(from, trs);
			}
			trs.add(ch.trId);

		} else if (ch.attrId == -4) {
			ServerMessage msg = new ServerMessage();
			msg.msgId = UUID.randomUUID().toString();
			msg.serverId = serverId;
			msg.dsName = dsName;
			msg.uuid = from;
			msg.obj = ch.trId;
			msg.action = ACTION_LONG_TR_ROLLBACKED;
			
			sendMessageToCluster(msg);
			
	    	List<Long> trs = rollbackedTrs.get(from);
			if (trs == null) {
				trs = new ArrayList<Long>();
				rollbackedTrs.put(from, trs);
			}
			trs.add(ch.trId);

		} else {
			List<AttrChange> l = attrChanges.remove(from);
			if (l == null) l = new ArrayList<AttrChange>();
			
			List<Long> trs1 = committedTrs.remove(from);
			List<Long> trs2 = rollbackedTrs.remove(from);
			
			if (ch.attrId == -1 && (!Funcs.isEmpty(l) || !Funcs.isEmpty(trs1) || !Funcs.isEmpty(trs2))) {
				ServerMessage msg = new ServerMessage();
				msg.msgId = UUID.randomUUID().toString();
				msg.serverId = serverId;
				msg.dsName = dsName;
				msg.uuid = from;
				msg.changes = l;
				msg.action = ACTION_OBJECT_CHANGED;
				sendMessageToCluster(msg);
			} else if (ch.attrId == -2 && (!Funcs.isEmpty(trs1) || !Funcs.isEmpty(trs2))) {
				ServerMessage msg = new ServerMessage();
				msg.msgId = UUID.randomUUID().toString();
				msg.serverId = serverId;
				msg.dsName = dsName;
				msg.uuid = from;
				msg.action = ACTION_ROLLBACK;
				sendMessageToCluster(msg);
			}
		}
	}
	
	public static class ServerMessageCacheListener implements CacheListener<String, ServerMessage> {

		@Override
		public void entryAdded(String cacheName, String key, ServerMessage msg, ServerMessage oldValue) {
			log.info("RECEIVED MSG action (" + msg.action + "), size = " + msgCache.getSize());

			if (msg.changes != null && !msg.serverId.equals(UserSession.SERVER_ID)) {
				Map<Long, List<AttrChangeListener>> map = Driver2.getAllListeners();
				if (map != null && map.size() > 0) {
					for (Long classId : map.keySet()) {
						List<AttrChangeListener> list = map.get(classId);
						List<AttrChange> toDel = new ArrayList<AttrChange>();
						for (AttrChangeListener l : list) {
							for (AttrChange ch : msg.changes) {
								if (classId == ch.obj.classId) {
									l.attrChanged(ch.obj, ch.attrId, ch.langId, ch.trId, msg.uuid);
									toDel.add(ch);
								}
							}
						}
						msg.changes.removeAll(toDel);
						for (AttrChangeListener l : list) {
							l.commit(msg.uuid);
						}
					}
				}
			} else {
				Session s = null;
				try {
					if (msg.action == ACTION_LONG_TR_COMMITTED) {
						Map<Long, List<AttrChangeListener>> map = Driver2.getAllListeners();
						if (map != null && map.size() > 0) {
							for (Long classId : map.keySet()) {
								List<AttrChangeListener> list = map.get(classId);
								for (AttrChangeListener l : list) {
									l.commitLongTransaction(msg.uuid, (Long)msg.obj);
								}
							}
						}
					} else if (msg.action == ACTION_LONG_TR_ROLLBACKED) {
						Map<Long, List<AttrChangeListener>> map = Driver2.getAllListeners();
						if (map != null && map.size() > 0) {
							for (Long classId : map.keySet()) {
								List<AttrChangeListener> list = map.get(classId);
								for (AttrChangeListener l : list) {
									l.rollbackLongTransaction(msg.uuid, (Long)msg.obj);
								}
							}
						}
					} else if (msg.action == ACTION_ROLLBACK) {
						Map<Long, List<AttrChangeListener>> map = Driver2.getAllListeners();
						if (map != null && map.size() > 0) {
							for (Long classId : map.keySet()) {
								List<AttrChangeListener> list = map.get(classId);
								for (AttrChangeListener l : list) {
									l.rollback(msg.uuid);
								}
							}
						}
					} else if (msg.action == ACTION_KILL_SESSION) {
						try {
							s = SrvUtils.getSession(msg.dsName, "sys", null);
							s.handlekillUserSession(msg.uuid);
						} catch (Exception e) {
							log.warn("Невозможно подключиться к серверу. Возможно сервер еще не запущен.");
						}
					} else if (msg.action == ACTION_RELOAD_FILTER) {
						try {
							s = SrvUtils.getSessionWithoutDb(msg.dsName, "sys");
							s.removeFilter((Long)msg.obj);
						} catch (Exception e) {
							log.warn("Невозможно подключиться к серверу. Возможно сервер еще не запущен.");
						}
					} else if (msg.action == ACTION_BLOCK_SERVER) {
						try {
							s = SrvUtils.getSession(msg.dsName, "sys", null);
				            SessionManager.blockServer(msg.ip);
				            s.writeLogRecord(SystemEvent.EVENT_SERVER_BLOCK, msg.ip,-1,-1);
						} catch (Exception e) {
							log.warn("Невозможно подключиться к серверу. Возможно сервер еще не запущен.");
						}
					} else if (msg.action == ACTION_UNBLOCK_SERVER) {
						try {
							s = SrvUtils.getSession(msg.dsName, "sys", null);
				            SessionManager.unblockServer(msg.ip);
				            s.writeLogRecord(SystemEvent.EVENT_SERVER_UNBLOCK, msg.ip,-1,-1);
						} catch (Exception e) {
							log.warn("Невозможно подключиться к серверу. Возможно сервер еще не запущен.");
						}
					} else if (msg.action == ACTION_SEND_MESSAGE) {
						ServerUserSession us = Session.findUserSession(msg.uuid);
				    	if (us != null && us.isMySession())
			    			us.addNote(msg.note);
				    	else if (msg.note instanceof MessageNote) {
				    		String message = ((MessageNote)msg.note).message;
				    		int ind = message.indexOf('|');
				    		if (ind > 0) {
				    			ResponseWaiter rw = Session.getResponseWaiter(msg.uuid, message.substring(0, ind));
				    			if (rw != null)
				    				rw.responseRecieved(message.substring(ind + 1));
				    		}
				    	}
					} else if (msg.action == ACTION_ADD_ATTR) {
						List<ModelChangeListener> list = Driver2.getModelListeners();
						for (ModelChangeListener l : list) {
							l.attrCreated((KrnAttribute)msg.obj);
						}
					} else if (msg.action == ACTION_DEL_ATTR) {
						List<ModelChangeListener> list = Driver2.getModelListeners();
						for (ModelChangeListener l : list) {
							l.attrDeleted((KrnAttribute)msg.obj);
						}
					} else if (msg.action == ACTION_CHG_ATTR) {
						List<ModelChangeListener> list = Driver2.getModelListeners();
						for (ModelChangeListener l : list) {
							l.attrChanged((KrnAttribute)msg.oldObj, (KrnAttribute)msg.obj);
						}
					} else if (msg.action == ACTION_ADD_CLASS) {
						List<ModelChangeListener> list = Driver2.getModelListeners();
						for (ModelChangeListener l : list) {
							l.classCreated((KrnClass)msg.obj);
						}
					} else if (msg.action == ACTION_DEL_CLASS) {
						List<ModelChangeListener> list = Driver2.getModelListeners();
						for (ModelChangeListener l : list) {
							l.classDeleted((KrnClass)msg.obj);
						}
					} else if (msg.action == ACTION_CHG_CLASS) {
						List<ModelChangeListener> list = Driver2.getModelListeners();
						for (ModelChangeListener l : list) {
							l.classChanged((KrnClass)msg.oldObj, (KrnClass)msg.obj);
						}
					} else if (msg.action == ACTION_ADD_METH) {
						List<ModelChangeListener> list = Driver2.getModelListeners();
						for (ModelChangeListener l : list) {
							l.methodCreated((KrnMethod)msg.obj);
						}
					} else if (msg.action == ACTION_DEL_METH) {
						List<ModelChangeListener> list = Driver2.getModelListeners();
						for (ModelChangeListener l : list) {
							l.methodDeleted((KrnMethod)msg.obj);
						}
					} else if (msg.action == ACTION_CHG_METH) {
						List<ModelChangeListener> list = Driver2.getModelListeners();
						for (ModelChangeListener l : list) {
							l.methodChanged((KrnMethod)msg.oldObj, (KrnMethod)msg.obj);
						}
					} else if (msg.action == ACTION_CHG_IFC) {
						List<MetadataChangeListener> list = Session.getMetadataChangeListeners();
						for (MetadataChangeListener l : list) {
							l.ifcChanged((Long)msg.obj);
						}
					} else if (msg.action == ACTION_CHG_PRD) {
						try {
							s = SrvUtils.getSessionWithoutDb(msg.dsName, "sys");
							s.reloadProcessDefinition((Long)msg.obj);
						} catch (Exception e) {
							log.warn("Невозможно подключиться к серверу. Возможно сервер еще не запущен.");
						}
					} else if (msg.action == ACTION_CANCEL_PROCESS && (msg.serverId==null || msg.serverId.length() == 0 || msg.serverId.equals(UserSession.SERVER_ID))) {
						//выполнять удаление только на сервере на котором запущен процесс если он задан
						try {
							s = SrvUtils.getSessionWithoutDb(msg.dsName, "sys");
							Object[] params = (Object[]) msg.obj;
							s.cancelProcess((Long)params[0], (String)params[1], (Boolean)params[2], (Boolean)params[3], true);
						} catch (Exception e) {
							log.warn("Невозможно подключиться к серверу. Возможно сервер еще не запущен.");
						}
					}
				} catch (Exception e) {
					log.error(e, e);
				} finally {
					if (s != null)
						s.release();
				}
			}
		}

		@Override
		public void entryUpdated(String cacheName, String key, ServerMessage value, ServerMessage oldValue) {
		}

		@Override
		public void entryRemoved(String cacheName, String key, ServerMessage oldValue) {
		}

		@Override
		public void entryEvicted(String cacheName, String key, ServerMessage oldValue) {
		}
	}
	
	private static class RemoveMessageFromCacheTask extends TimerTask {
		private String msgId = null;
		private int action = -1;
		
		public RemoveMessageFromCacheTask(String msgId, int action) {
			this.msgId = msgId;
			this.action = action;
		}
		
		@Override
		public void run() {
			msgCache.remove(msgId);
			log.info("REMOVED MSG action (" + action + "), size = " + msgCache.getSize());
		}
	}
}
