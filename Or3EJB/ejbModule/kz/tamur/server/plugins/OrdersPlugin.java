package kz.tamur.server.plugins;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kz.tamur.ods.Driver2;
import kz.tamur.ods.Value;
import kz.tamur.or3ee.common.AttrChangeListener;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.or3ee.server.kit.AttrRequestBuilder;
import kz.tamur.or3ee.server.kit.SrvUtils;

import com.cifs.or2.kernel.AttrChange;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnDate;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.QueryResult;
import com.cifs.or2.server.Session;
import com.cifs.or2.server.orlang.SrvPlugin;

public class OrdersPlugin implements SrvPlugin, AttrChangeListener {
	
	private static final Log log = LogFactory.getLog("OrdersPlugin" + (UserSession.SERVER_ID != null ? ("." + UserSession.SERVER_ID) : ""));
	
	private static final String STATUS_WAITING = "21051986.23878388";
	private static final String STATUS_PROCESSING = "21051986.23878372";
	
	private static final String TYPE_NOTIFICATION = "3.1188";
	
	//private static final String ORDER_TYPE_INCOMING = "in";
	//private static final String ORDER_TYPE_OUTGOING = "out";
	private static final String ORDER_TYPE_PROJECTS = "my";
	private static final String ORDER_TYPE_NOTIFICATIONS = "notif";
	
	private Session session;
	private String dsName = null;

	private static Map<String, Map<Long, List<Order>>> ordersByUser = new HashMap<String, Map<Long, List<Order>>>();
	private static Map<String, Map<Long, List<Long>>> usersByDep = new HashMap<String, Map<Long,List<Long>>>();
	private static Map<Long, List<Long>> usersByRole = new HashMap<Long,List<Long>>();

	private static Map<UUID, List<AttrChange>> attrChanges = new HashMap<UUID, List<AttrChange>>();

	private static Map<Long, Long> usersByPerson = new HashMap<Long, Long>();
	private static Map<Long, Long> personsByUser = new HashMap<Long, Long>();
	private static Map<Long, Long> balansEdByPerson = new HashMap<Long, Long>();
	
	private static Boolean listenerInitialized = false;

	public static KrnClass clsOrder;
	public static KrnAttribute attrStatus;
	public static KrnAttribute attrRespPerson;
	public static KrnAttribute attrRespUser;
	public static KrnAttribute attrRespDep;
	public static KrnAttribute attrRespRole;
	public static KrnAttribute attrShow;
	public static KrnAttribute attrCanKill;
	public static KrnAttribute attrReassigned;
	public static KrnAttribute attrBalansEd;
	public static KrnAttribute attrAuthor;
	public static KrnAttribute attrParent;
	public static KrnAttribute attrOrderType;

	public static KrnAttribute attrChildren;

	// Атрибуты, влияющие на наименование поручения
	public static KrnAttribute attrOrderTitle;

	// Атрибуты при изменении которых нужно перечитать поручение
	public static KrnAttribute attrOrderFrom;
	public static KrnAttribute attrOrderTo;
	public static KrnAttribute attrOrderUrgency;
	public static KrnAttribute attrOrderCtrlDate;
	public static KrnAttribute attrOrderSrok;
	public static KrnAttribute attrOrderSrok1;
	public static KrnAttribute attrOrderSrok2;
	public static KrnAttribute attrOrderClass;
	public static KrnAttribute attrOrderDoc;
	// Атрибуты документа при изменении которых нужно перечитать поручение
	public static KrnClass clsDoc;
	public static KrnAttribute attrDocZaprosDoc;
	public static KrnAttribute attrDocAderkk;
	// Атрибуты АД ЭРКК при изменении которых нужно перечитать поручение
	public static KrnClass clsAd;
	public static KrnAttribute attrAdNumber;
	public static KrnAttribute attrAdTitle;
	public static KrnAttribute attrAdDecision;

	public static KrnClass clsUser;
	public static KrnAttribute attrUserPerson;

	private static AttrRequestBuilder requestForParentDeps;
	private static AttrRequestBuilder requestForOrder;

	private static final boolean reloadOrdersAtLogin = "1".equals(System.getProperty("reloadOrdersAtLogin"));
	
	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
		this.dsName = session.getDsName();
	}
	
	private void addUserToRole(long userId, long roleId) {
        List<Long> usersOfRole = null;
        synchronized (usersByRole) {
            usersOfRole = usersByRole.get(roleId);
    		if (usersOfRole == null) {
    			usersOfRole = new ArrayList<Long>();
    			usersByRole.put(roleId, usersOfRole);
    		}
		}
        synchronized (usersOfRole) {
			if (!usersOfRole.contains(userId))
				usersOfRole.add(userId);
        }
	}

	private void addUserToDep(String type, long userId, long depId) {
        Map<Long, List<Long>> usersByDepOfType = null;
        synchronized (usersByDep) {
        	usersByDepOfType = usersByDep.get(type);
	        if (usersByDepOfType == null) {
	        	usersByDepOfType = new HashMap<Long, List<Long>>();
	        	usersByDep.put(type, usersByDepOfType);
	        }
		}
        List<Long> usersOfDep = null;
        synchronized (usersByDepOfType) {
            usersOfDep = usersByDepOfType.get(depId);
    		if (usersOfDep == null) {
    			usersOfDep = new ArrayList<Long>();
    			usersByDepOfType.put(depId, usersOfDep);
    		}
		}
        synchronized (usersOfDep) {
			if (!usersOfDep.contains(userId))
				usersOfDep.add(userId);
        }
	}
	
	public void reloadUserRoles(KrnObject user, List<KrnObject> roles) {
		try {
			// Ощищаем мапы пользователей и ролей
	        synchronized (usersByRole) {
				for (List<Long> usersOfRole : usersByRole.values()) {
					usersOfRole.remove(user.id);
				}
			}
	        
	        if (roles != null && roles.size() > 0) {
	            for (KrnObject role : roles) {
	            	if (role != null) {
		            	addUserToRole(user.id, role.id);
	            	}
	        	}
	        }            
		} catch (Exception e) {
			log.error(e, e);
		}
	}

	public void reloadUserDeps(String type, KrnObject user, KrnObject person, List<KrnObject> deps) {
		try {
			synchronized (usersByPerson) {
				usersByPerson.put(person.id, user.id);
			}
			synchronized (personsByUser) {
				personsByUser.put(user.id, person.id);
			}
			// Ощищаем мапы пользователей и подразделений
	        Map<Long, List<Long>> usersByDepOfType = null;
	        synchronized (usersByDep) {
	        	usersByDepOfType = usersByDep.get(type);
		        if (usersByDepOfType == null) {
		        	usersByDepOfType = new HashMap<Long, List<Long>>();
		        	usersByDep.put(type, usersByDepOfType);
		        }
			}
	        synchronized (usersByDepOfType) {
				for (List<Long> usersOfDep : usersByDepOfType.values()) {
					usersOfDep.remove(person.id);
				}
			}
	        
        	if (reloadOrdersAtLogin) {
    	        Map<Long, List<Order>> userOrders = null;
    	        synchronized (ordersByUser) {
    		        userOrders = ordersByUser.get(type);
    		        if (userOrders == null) {
    		        	userOrders = new HashMap<Long, List<Order>>();
    		        	ordersByUser.put(type, userOrders);
    		        }
    			}
    	        synchronized (userOrders) {
    	        	userOrders.remove(person.id);
	        	}
			}
			
	        if (deps != null && deps.size() > 0) {
	        	long[] depIds = new long[deps.size()];
	        	int i = 0;
	            for (KrnObject dep : deps) {
	            	depIds[i++] = dep.id;
	            }
	
	            AttrRequestBuilder arb = getRequestForParentDeps(session);
	
	            QueryResult qr = session.getObjects(depIds, arb.build(), 0);
	
	            for (Object[] prow : qr.rows) {
	            	KrnObject dep = arb.getObject(prow);
	            	addUserToDep(type, person.id, dep.id);
	
	            	KrnObject parent = arb.getObjectValue("родитель", prow);
	            	if (parent != null) {
		            	addUserToDep(type, person.id, parent.id);
	                	parent = arb.getObjectValue("родитель.родитель", prow);
	                	if (parent != null) {
			            	addUserToDep(type, person.id, parent.id);
	                    	parent = arb.getObjectValue("родитель.родитель.родитель", prow);
	                    	if (parent != null) {
	    		            	addUserToDep(type, person.id, parent.id);
	                        	parent = arb.getObjectValue("родитель.родитель.родитель.родитель", prow);
	                        	if (parent != null) {
	        		            	addUserToDep(type, person.id, parent.id);
	                        	}
	                    	}
	                	}
	            	}
	        	}
	        }            
		} catch (Exception e) {
			log.error(e, e);
		}
	}

	public List<Order> loadOrders(String type, KrnObject person, List<String> uids) {
        Map<Long, List<Order>> userOrders = null;
        synchronized (ordersByUser) {
	        userOrders = ordersByUser.get(type);
	        if (userOrders == null) {
	        	userOrders = new HashMap<Long, List<Order>>();
	        	ordersByUser.put(type, userOrders);
	        }
		}
        List<Order> orders = null;
        synchronized (userOrders) {
        	orders = userOrders.get(person.id);
        	if (orders == null) {
                orders = new ArrayList<Order>();
    	        userOrders.put(person.id, orders);
        	}
		}
        
        List<Order> res = new ArrayList<Order>();
        synchronized (orders) {
        	for (String uid : uids) {
        		for (Order order : orders) {
        			if (order.getOrderObj() != null && uid.equals(order.getOrderObj().uid)) {
                		res.add(order);
                		break;
        			}
        		}
        	}
		}
        return res;
	}

	public List<Order> loadOrders(String type, String filterUid, KrnObject person, List<KrnObject> deps) {
		return loadOrders(type, filterUid, person, deps, null, null);
	}
	
	public List<Order> loadOrders(String type, String filterUid, KrnObject person, List<KrnObject> deps,
			KrnObject balansEd, List<KrnObject> roles) {
		return loadOrders(type, filterUid, null, person, deps, balansEd, roles, null);
	}
	
	public List<Order> loadOrders(String type, String filterUid, KrnObject person, List<KrnObject> deps,
			KrnObject balansEd, List<KrnObject> roles, KrnDate dateControl) {
		return loadOrders(type, filterUid, null, person, deps, balansEd, roles, dateControl);
	}
	
	public List<Order> loadOrders(String type, String filterUid, KrnObject user, KrnObject person, List<KrnObject> deps,
			KrnObject balansEd, List<KrnObject> roles) {
		return loadOrders(type, filterUid, user, person, deps, balansEd, roles, null);
	}
	
	public List<Order> loadOrders(String type, String filterUid, KrnObject user, KrnObject person, List<KrnObject> deps,
			KrnObject balansEd, List<KrnObject> roles, KrnDate dateControl) {
		return loadOrders(type, filterUid, user, person, deps, balansEd, roles, dateControl, ORDER_TYPE_PROJECTS.equals(type) ? -1 : 0);
	}
	
	public List<Order> loadOrders(String type, String filterUid, KrnObject user, KrnObject person, List<KrnObject> deps,
									KrnObject balansEd, List<KrnObject> roles, KrnDate dateControl, long trId) {
		try {
			initListener(session);
				
	        Map<Long, List<Order>> userOrders = null;
	        synchronized (ordersByUser) {
		        userOrders = ordersByUser.get(type);
		        if (userOrders == null) {
		        	userOrders = new HashMap<Long, List<Order>>();
		        	ordersByUser.put(type, userOrders);
		        }
			}
	        List<Order> orders = null;
	        List<KrnObject> orderObjs = null;
	        boolean loaded = true;
	        synchronized (userOrders) {
	        	orders = userOrders.get(user != null ? user.id : person.id);
	        	if (orders == null) {
	        		loaded = false;
	                orders = new ArrayList<Order>();
	    	        userOrders.put(user != null ? user.id : person.id, orders);
	        	}
			}

	        if (balansEd != null && user != null)
    	        balansEdByPerson.put(user.id, balansEd.id);
	        else if(balansEd != null && person != null)
	        	balansEdByPerson.put(person.id, balansEd.id);
	        
	        if (!loaded) {
    	        Map<String, Object> params = new HashMap<String, Object>();
    	        if(user != null) 
    	        	params.put("%Юзер", user);
    	        else if(person != null)
    	        	params.put("%Персона", person);
    	        params.put("%Подразделение", deps);
    	        if (balansEd != null)
        	        params.put("%БалансЕд", balansEd);
    	        if (roles != null)
        	        params.put("%Роль", roles);
    	        if (dateControl != null)
        	        params.put("%ДатаСфор", dateControl);

    	        session.clearFilterParams(filterUid);
    	        
    	        if (params != null) {
    	            for (Iterator<String> it = params.keySet().iterator(); it.hasNext();) {
    	                String name = it.next();
    	                Object value = params.get(name);
    	                session.setFilterParam(filterUid, name, value);
    	            }
    	        }
    	        orderObjs = session.filter(session.getObjectByUid(filterUid, 0), 0, -1, -1, trId);
    	        log.info("FILTER = " + filterUid + "; " + type + " ORDERS COUNT = " + orderObjs.size());
	        }

            if (orderObjs != null && orderObjs.size() > 0) {
	        	long[] orderIds = new long[orderObjs.size()];
	        	
	        	for (int i=0; i<orderIds.length; i++) {
	        		orderIds[i] = orderObjs.get(i).id;
	        	}
	
	            AttrRequestBuilder arb = getRequestForOrder(session);

	            QueryResult qr = session.getObjects(orderIds, arb.build(), -1);
	
	            for (Object[] prow : qr.rows) {
	            	KrnObject orderObj = arb.getObject(prow);
	            	KrnObject statusObj = arb.getObjectValue(attrStatus.name, prow);
	            	KrnObject personObj = arb.getObjectValue(attrRespPerson.name, prow);
	            	KrnObject userObj = arb.getObjectValue(attrRespUser.name, prow);
	            	KrnObject depObj = arb.getObjectValue(attrRespDep.name, prow);
	            	List<Value> roleVals = (List<Value>)arb.getValue(attrRespRole.name, prow);
	            	List<KrnObject> roleObjs = null; 
	            	if (roleVals != null && roleVals.size() > 0) {
	            		roleObjs = new ArrayList<KrnObject>();
	            		for (Value val : roleVals)
	            			if (val != null && val.value instanceof KrnObject)
	            				roleObjs.add((KrnObject)val.value);
	            	}
	            	
	            	KrnObject balObj = arb.getObjectValue(attrBalansEd.name, prow);
	            	KrnObject authorObj = arb.getObjectValue(attrAuthor.name, prow);
	            	KrnObject parentObj = arb.getObjectValue(attrParent.name, prow);
	            	KrnObject parentPersonObj = arb.getObjectValue(attrParent.name + "." + attrRespPerson.name, prow);
	            	KrnObject typeObj = arb.getObjectValue(attrOrderType.name, prow);
	            	KrnObject killerObj = arb.getObjectValue(attrCanKill.name + "." + attrUserPerson.name, prow);
	            	boolean show = arb.getBooleanValue(attrShow.name, prow, false);
	            	boolean reassigned = arb.getBooleanValue(attrReassigned.name, prow, false);
	            	KrnObject docObj = arb.getObjectValue(attrOrderDoc.name, prow);
	            	KrnObject zaprosDocObj = arb.getObjectValue(attrOrderDoc.name + "." + attrDocZaprosDoc.name, prow);
	            	KrnObject erkkObj = arb.getObjectValue(attrOrderDoc.name + "." + attrDocAderkk.name, prow);
	            	KrnObject erkkObj2 = arb.getObjectValue(attrOrderDoc.name + "." + attrDocZaprosDoc.name + "." + attrDocAderkk.name, prow);
	            	
	            	List<Value> children_ = (List<Value>) arb.getValue(attrChildren.name, prow);
	            	List<KrnObject> childObjs = null; 
	            	if (children_ != null && children_.size() > 0) {
	            		childObjs = new ArrayList<KrnObject>();
	            		for (Value val : children_)
	            			if (val != null && val.value instanceof KrnObject)
	            				childObjs.add((KrnObject)val.value);
	            		
	            		long childIds[] = new long[childObjs.size()];
	            		
	            		for (int i=0; i<childObjs.size(); i++) {
	            			childIds[i] = childObjs.get(i).id;
	    	        	}
	    	
	    	            AttrRequestBuilder arb2 = new AttrRequestBuilder(clsOrder, session).add(attrAuthor.name);

	    	            QueryResult qr2 = session.getObjects(childIds, arb2.build(), -1);
	            		
	    	            for (Object[] prow2 : qr2.rows) {
	    	            	KrnObject authorObj2 = arb2.getObjectValue(attrAuthor.name, prow2);
	    	            	if (!childObjs.contains(authorObj2))
	    	            		childObjs.add(authorObj2);
	    	            }
	            	}
	            	Order order = new Order(orderObj, parentObj, personObj, userObj, depObj, roleObjs, statusObj, typeObj, balObj,
	            			authorObj, killerObj, show, reassigned, docObj, zaprosDocObj, erkkObj, erkkObj2,
	            			parentPersonObj, childObjs);
	            	synchronized (orders) {
	    	        	orders.add(order);
					}
    	        }
	        }

            List<Order> res = new ArrayList<Order>();
            synchronized (orders) {
            	res.addAll(orders);
			}
	        return res;
	    } catch (Exception e) {
			log.error(e, e);
		}
		return Collections.emptyList();
	}

	public synchronized void initListener(Session s) throws KrnException {
		if (!listenerInitialized) {
			clsOrder = s.getClassByName("уд::осн::Поручение");

			attrStatus = s.getAttributeByName(clsOrder, "исп_ние_статус_исполнения");
			attrRespPerson = s.getAttributeByName(clsOrder, "исполнитель_персона");
			attrRespUser = s.getAttributeByName(clsOrder, "исполнитель_user");
			attrRespDep = s.getAttributeByName(clsOrder, "исполнитель_подр_производственная_структура");
			attrRespRole = s.getAttributeByName(clsOrder, "исполнитель_UserFolder");
			attrShow = s.getAttributeByName(clsOrder, "тхн_показать_в_архиве");
			attrCanKill = s.getAttributeByName(clsOrder, "может_убить_поручение_user");
			attrBalansEd = s.getAttributeByName(clsOrder, "баланс_ед");
			attrAuthor = s.getAttributeByName(clsOrder, "автор_персона");
			attrParent = s.getAttributeByName(clsOrder, "родитель");
			attrChildren = s.getAttributeByName(clsOrder, "дети");
			attrReassigned = s.getAttributeByName(clsOrder, "перепоручено");
			
			attrOrderTitle = s.getAttributeByName(clsOrder, "наименование");
			attrOrderType = s.getAttributeByName(clsOrder, "тип_поручения");
			attrOrderFrom = s.getAttributeByName(clsOrder, "автор_сформировал_текст");
			attrOrderTo = s.getAttributeByName(clsOrder, "кому_текст");
			attrOrderUrgency = s.getAttributeByName(clsOrder, "контр_срочность");
			attrOrderCtrlDate = s.getAttributeByName(clsOrder, "контр_срок_заверш_дата");
			attrOrderSrok = s.getAttributeByName(clsOrder, "исп_ние_срок_получ");
			attrOrderSrok1 = s.getAttributeByName(clsOrder, "исп_ние_срок_взял");
			attrOrderSrok2 = s.getAttributeByName(clsOrder, "исп_ние_срок_заверш");
			attrOrderClass = s.getAttributeByName(clsOrder, "классификатор_бп");
			attrOrderDoc = s.getAttributeByName(clsOrder, "документ");

			clsDoc = s.getClassByName("уд::осн::Документ");
			attrDocAderkk = s.getAttributeByName(clsDoc, "ад_эркк");
			attrDocZaprosDoc = s.getAttributeByName(clsDoc, "запрос_документ");

			clsAd = s.getClassByName("уд::осн::ЭРКК");
			attrAdTitle = s.getAttributeByName(clsAd, "рег_наименование");
			attrAdNumber = s.getAttributeByName(clsAd, "рег_номер");
			attrAdDecision = s.getAttributeByName(clsAd, "решение_по_док_Да_Нет");

			clsUser = s.getClassByName("User");
			attrUserPerson = s.getAttributeByName(clsUser, "персона");

			Driver2.addAttrChangeListener(clsOrder.id, this);
			Driver2.addAttrChangeListener(clsDoc.id, this);
			Driver2.addAttrChangeListener(clsAd.id, this);

			listenerInitialized = true;
		}
	}
	
	private synchronized AttrRequestBuilder getRequestForParentDeps(Session s) {
		if (requestForParentDeps == null) {
			requestForParentDeps = new AttrRequestBuilder("Производственная структура", s)
	            	.add("родитель", new AttrRequestBuilder("Производственная структура", s)
	            	.add("родитель", new AttrRequestBuilder("Производственная структура", s)
	            	.add("родитель", new AttrRequestBuilder("Производственная структура", s)
	            	.add("родитель"))));
		}
		return requestForParentDeps;
	}

	public synchronized AttrRequestBuilder getRequestForOrder(Session s) {
		if (requestForOrder == null) {
			requestForOrder = new AttrRequestBuilder(clsOrder, s)
					.add(attrStatus.name).add(attrRespPerson.name).add(attrRespUser.name).add(attrRespDep.name).add(attrRespRole.name).add(attrReassigned.name)
					.add(attrShow.name).add(attrBalansEd.name).add(attrAuthor.name)
					.add(attrParent.name, new AttrRequestBuilder(clsOrder, s)
							.add(attrRespPerson.name))
					.add(attrOrderType.name)
					.add(attrCanKill.name, new AttrRequestBuilder(clsUser, s)
							.add(attrUserPerson.name))
					.add(attrOrderDoc.name, new AttrRequestBuilder(clsDoc, s)
							.add(attrDocAderkk.name)
							.add(attrDocZaprosDoc.name, new AttrRequestBuilder(clsDoc, s)
									.add(attrDocAderkk.name)))
					.add(attrChildren.name);

		}
		return requestForOrder;
	}

	@Override
	public void attrChanged(KrnObject orderObj, long attrId, long langId, long trId, UUID uuid) {
    	List<AttrChange> l = attrChanges.get(uuid);
		if (l == null) {
			l = new ArrayList<AttrChange>();
			attrChanges.put(uuid, l);
		}
		
		l.add(new AttrChange(orderObj, attrId, langId, trId));
	}
	
	@Override
	public void commit(UUID uuid) {
		if (uuid != null) {
			Map<KrnObject, Map<Long, List<Long>>> groupedMapByObj = new HashMap<>();

			List<AttrChange> l = attrChanges.remove(uuid);
			if (l != null && l.size() > 0) {
				log.info("COMMIT ORDER " + uuid + "; SIZE = " + l.size());
				
				for (AttrChange ch : l) {
					Map<Long, List<Long>> groupedMapByTr = groupedMapByObj.get(ch.obj);
					if (groupedMapByTr == null) {
						groupedMapByTr = new HashMap<>();
						groupedMapByObj.put(ch.obj, groupedMapByTr);
					}
					
					List<Long> groupedList = groupedMapByTr.get(ch.trId);
					if (groupedList == null) {
						groupedList = new ArrayList<>();
						groupedMapByTr.put(ch.trId, groupedList);
					}
					groupedList.add(ch.attrId);	
				}

				l.clear();
			}
			if (groupedMapByObj.size() > 0)
				processObjectsChanged(groupedMapByObj);
		}
	}
	
	@Override
	public void rollback(UUID uuid) {
		if (uuid != null) {
			List<AttrChange> l = attrChanges.remove(uuid);
			if (l != null && l.size() > 0) {
		        log.info("ROLBACK ORDER " + uuid + "; SIZE = " + l.size());
				l.clear();
			}
		}
	}

	@Override
	public void commitLongTransaction(UUID uuid, long trId) {
	}

	@Override
	public void rollbackLongTransaction(UUID uuid, long trId) {
	}
	
	private void processObjectsChanged(Map<KrnObject, Map<Long, List<Long>>> groupedMapByObj) {
        try {
	        Session s = SrvUtils.getSession(dsName, "sys", null);
	        try {
				for (KrnObject obj : groupedMapByObj.keySet()) {
					Map<Long, List<Long>> attrsByTr = groupedMapByObj.get(obj);
					
					for (long trId : attrsByTr.keySet()) {
						List<Long> attrs = attrsByTr.get(trId);
						log.info("COMMIT ORDER OBJ: " + obj + "; TR = " + trId + "; ATTRS SIZE = " + toString(attrs));

			        	Map<Long, Map<String, List<Order>>> updOrders = new HashMap<Long, Map<String, List<Order>>>();
			        	Map<Long, Map<String, List<Order>>> delOrders = new HashMap<Long, Map<String, List<Order>>>();
			        	Map<Long, Map<String, List<Order>>> forceUpdateOrders = new HashMap<Long, Map<String, List<Order>>>();

			        	processAttrChanged(obj, attrs, s, updOrders, delOrders, forceUpdateOrders, trId);
						
						for (Long personId : delOrders.keySet()) {
							Map<String, List<Order>> ordersByType = delOrders.get(personId);
							for (String type : ordersByType.keySet()) {
								List<Order> orders = ordersByType.get(type);
								if (orders != null && orders.size() > 0) {
									List<String> orderIds = new ArrayList<String>();
									long forUserId = -1;
									
									for (Order order : orders) {
										orderIds.add(order.getOrderObj().uid);
										if (order.getUserObj() != null)
											forUserId = order.getUserObj().id;
									}
									
									if (orderIds.size() > 0) {
										Long userId = usersByPerson.get(personId);
										if (trId == 0 || ORDER_TYPE_PROJECTS.equals(type)) {
											if (userId != null) {
												s.orderChanged(userId, "deleteOrders", type, orderIds);
											} else if (forUserId > -1) {
												s.orderChanged(forUserId, "deleteOrders", type, orderIds);
											}
										}
									}
								}
							}
						}
						
						for (Long personId : updOrders.keySet()) {
							Map<String, List<Order>> ordersByType = updOrders.get(personId);
							for (String type : ordersByType.keySet()) {
								List<Order> orders = ordersByType.get(type);
								if (orders != null && orders.size() > 0) {
									List<String> orderIds = new ArrayList<String>();
									long forUserId = -1;
									
									for (Order order : orders) {
										orderIds.add(order.getOrderObj().uid);
										if (order.getUserObj() != null)
											forUserId = order.getUserObj().id;
									}
									if (orderIds.size() > 0) {
										Long userId = usersByPerson.get(personId);
										
										if (trId == 0 || ORDER_TYPE_PROJECTS.equals(type)) {
											if (userId != null) {
												s.orderChanged(userId, "updateOrders", type, orderIds);
											} else if (forUserId > -1) {
												s.orderChanged(forUserId, "updateOrders", type, orderIds);
											}
										}
									}
								}
							}
						}

						for (Long personId : forceUpdateOrders.keySet()) {
							Map<String, List<Order>> ordersByType = updOrders.get(personId);
							for (String type : ordersByType.keySet()) {
								List<Order> orders = ordersByType.get(type);
								if (orders != null && orders.size() > 0) {
									List<String> orderIds = new ArrayList<String>();
									long forUserId = -1;
									
									for (Order order : orders) {
										orderIds.add(order.getOrderObj().uid);
										if (order.getUserObj() != null)
											forUserId = order.getUserObj().id;
									}
									if (orderIds.size() > 0) {
										Long userId = usersByPerson.get(personId);
										
										if (userId != null) {
											s.orderChanged(userId, "updateOrders", type, orderIds);
										} else if (forUserId > -1) {
											s.orderChanged(forUserId, "updateOrders", type, orderIds);
										}
									}
								}
							}
						}
					}
				}
	        } catch (Exception e) {
	        	log.error(e, e);
            } finally {
                if (s != null) {
                    s.release();
                }
            }
        } catch (KrnException e) {
        	log.error(e, e);
        }
	}

	private void processAttrChanged(KrnObject orderObj, List<Long> attrs, Session s,
					Map<Long, Map<String, List<Order>>> updOrders, Map<Long, Map<String, List<Order>>> delOrders, Map<Long, Map<String, List<Order>>> forceUpdateOrders, long trId) {
		if (attrs.contains(2L)) {
			orderDeleted(orderObj, delOrders, trId);
		} else if (
				attrs.contains(0L) || attrs.contains(1L) 
				|| attrs.contains(attrRespPerson.id) || attrs.contains(attrRespUser.id) || attrs.contains(attrRespDep.id) || attrs.contains(attrRespRole.id)
				|| attrs.contains(attrAuthor.id) || attrs.contains(attrCanKill.id) || attrs.contains(attrBalansEd.id)
				|| attrs.contains(attrStatus.id)
				|| attrs.contains(attrOrderType.id)
				|| attrs.contains(attrShow.id) || attrs.contains(attrReassigned.id)
				|| attrs.contains(attrOrderSrok.id)
				|| attrs.contains(attrOrderTitle.id) || attrs.contains(attrOrderFrom.id)
				|| attrs.contains(attrOrderTo.id) || attrs.contains(attrOrderUrgency.id) || attrs.contains(attrOrderCtrlDate.id)
				|| attrs.contains(attrOrderSrok1.id) || attrs.contains(attrOrderSrok2.id) || attrs.contains(attrOrderClass.id) || attrs.contains(attrOrderDoc.id)
				) {
			Order orderInDB = null;
			Order orderInDB_0 = null;
			List<Order> orders = null;
			
			if (attrs.contains(0L) || attrs.contains(1L) || attrs.contains(attrRespPerson.id) || attrs.contains(attrRespUser.id) || attrs.contains(attrRespDep.id) || attrs.contains(attrRespRole.id)
					|| attrs.contains(attrAuthor.id) || attrs.contains(attrCanKill.id) || attrs.contains(attrBalansEd.id)
					|| attrs.contains(attrStatus.id)
					|| attrs.contains(attrOrderType.id)
					|| attrs.contains(attrShow.id) || attrs.contains(attrReassigned.id)) {
				orderInDB = reloadOrder(orderObj, s, -1);
				orderInDB_0 = reloadOrder(orderObj, s, 0);
			}
			if (attrs.contains(attrOrderSrok.id)
					|| attrs.contains(attrOrderTitle.id) || attrs.contains(attrOrderFrom.id)
					|| attrs.contains(attrOrderTo.id) || attrs.contains(attrOrderUrgency.id) || attrs.contains(attrOrderCtrlDate.id)
					|| attrs.contains(attrOrderSrok1.id) || attrs.contains(attrOrderSrok2.id) || attrs.contains(attrOrderClass.id) || attrs.contains(attrOrderDoc.id)) {
				orders = findOrdersById(orderObj.id, updOrders, forceUpdateOrders, attrs);
			} else if (attrs.contains(attrStatus.id)
					|| attrs.contains(attrOrderType.id)
					|| attrs.contains(attrShow.id) || attrs.contains(attrReassigned.id)) {
				orders = findOrdersById(orderObj.id, null, null, null);
			}
			
			
			if (attrs.contains(0L) || attrs.contains(1L) || attrs.contains(attrRespPerson.id) || attrs.contains(attrRespUser.id) ||attrs.contains(attrRespDep.id) || attrs.contains(attrRespRole.id)
						|| attrs.contains(attrAuthor.id) || attrs.contains(attrCanKill.id) || attrs.contains(attrBalansEd.id)) {
		    	// Перечитываем атрибуты поручения
	    		removeOrder(orderInDB, delOrders, trId);
		    	if (orderInDB.getParentObj() != null) {
		    		findOrdersById(orderInDB.getParentObj().id, updOrders, null, null);
		    	}
		    	// Вставляем поручение
	            insertOrder(orderInDB, orderInDB_0, updOrders, delOrders);
			} else if (attrs.contains(attrStatus.id)) {
				if(orderInDB.getTypeObj() != null && TYPE_NOTIFICATION.equals(orderInDB.getTypeObj().uid)) {
					updateOrder(orderInDB, updOrders);
				} else {
			    	for (Order order : orders) {
				    	order = copyOrder(order, orderInDB);
			    	}
			    	
			    	if (orderInDB.getParentObj() != null) {
			    		findOrdersById(orderInDB.getParentObj().id, updOrders, null, null);
			    	}
		
	                removeOrder(orderInDB, delOrders, trId);
	            	insertOrder(orderInDB, orderInDB_0, updOrders, delOrders);
				}
			}
			if (attrs.contains(attrShow.id)) {
		    	for (Order order : orders) {
			    	order = copyOrder(order, orderInDB);
		    	}
        		removeOrder(orderInDB, delOrders, trId);
            	insertOrder(orderInDB, orderInDB_0, updOrders, delOrders);
			}
			if (attrs.contains(attrOrderType.id)) {
	    		removeOrder(orderInDB, delOrders, trId);
	        	insertOrder(orderInDB, orderInDB_0, updOrders, delOrders);
			} else if (attrs.contains(attrReassigned.id)) {
		    	for (Order order : orders) {
			    	order = copyOrder(order, orderInDB);
		    	}
        		removeOrder(orderInDB, delOrders, trId);
            	insertOrder(orderInDB, orderInDB_0, updOrders, delOrders);
			}
			if (attrs.contains(attrOrderSrok.id)) {
				if (orders != null && orders.size() > 0) {
					Order order = orders.get(0);
			    	if (order.getParentObj() != null) {
			    		findOrdersById(order.getParentObj().id, updOrders, null, null);
			    	}
				}
			}
		} else if (attrs.contains(attrDocAderkk.id)) {
    		List<Order> orders = findOrdersByZaprosDocId(orderObj.id, updOrders);
    		if (orders.size() > 0) {
    			Order orderInDB = reloadOrder(orders.get(0).getOrderObj(), s, -1);
    	    	for (Order order : orders) {
    		    	order = copyOrder(order, orderInDB);
    	    	}
    		}
		} else if (attrs.contains(attrDocZaprosDoc.id)) {
    		List<Order> orders = findOrdersByDocId(orderObj.id, updOrders);
    		if (orders.size() > 0) {
    			Order orderInDB = reloadOrder(orders.get(0).getOrderObj(), s, -1);
    	    	for (Order order : orders) {
    		    	order = copyOrder(order, orderInDB);
    	    	}
    		}
		} else if (attrs.contains(attrAdNumber.id) || attrs.contains(attrAdDecision.id) || attrs.contains(attrAdTitle.id)) {
    		List<Order> orders = findOrdersByErkkId(orderObj.id, updOrders);
    		if (orders.size() > 0) {
    			Order orderInDB = reloadOrder(orders.get(0).getOrderObj(), s, -1);
    	    	for (Order order : orders) {
    		    	order = copyOrder(order, orderInDB);
    	    	}
    		}
    	}
	}

	private List<Order> findOrdersById(long id, Map<Long, Map<String, List<Order>>> map, Map<Long, Map<String, List<Order>>> forceMap, List<Long> attrs) {
		List<Order> res = new ArrayList<Order>();
        for (String type : ordersByUser.keySet()) {
        	Map<Long, List<Order>> userOrders = ordersByUser.get(type);
            synchronized (userOrders) {
                for (Long userId : userOrders.keySet()) {
        			List<Order> orders = userOrders.get(userId);
        			synchronized (orders) {
        	        	for (Order order : orders) {
        	        		if (order.getOrderObj().id == id) {
        	        			if (map != null)
        	        				addOrderToMap(map, userId, type, order);
        	        			if (forceMap != null && forceUpdate(type, attrs))
        	        				addOrderToMap(forceMap, userId, type, order);
        	        				
        	        			res.add(order);
        	        		}
        	        	}
					}
                }
    		}
        }
        return res;
	}
	
	private boolean forceUpdate(String type, List<Long> attrs) {
		if (ORDER_TYPE_NOTIFICATIONS.equals(type) && attrs.contains(attrOrderSrok1.id))
			return true;
		return false;
	}

	private List<Order> findOrdersByDocId(long id, Map<Long, Map<String, List<Order>>> map) {
		List<Order> res = new ArrayList<Order>();
        for (String type : ordersByUser.keySet()) {
        	Map<Long, List<Order>> userOrders = ordersByUser.get(type);
            synchronized (userOrders) {
                for (Long userId : userOrders.keySet()) {
        			List<Order> orders = userOrders.get(userId);
        			synchronized (orders) {
	    	        	for (Order order : orders) {
	    	        		if (order.getDocObj() != null && order.getDocObj().id == id) {
	    	        			if (map != null)
	    	        				addOrderToMap(map, userId, type, order);
	    	        			res.add(order);
	    	        		}
	    	        	}
        			}
                }
    		}
        }
        return res;
	}

	private List<Order> findOrdersByZaprosDocId(long id, Map<Long, Map<String, List<Order>>> map) {
		List<Order> res = new ArrayList<Order>();
        for (String type : ordersByUser.keySet()) {
        	Map<Long, List<Order>> userOrders = ordersByUser.get(type);
            synchronized (userOrders) {
                for (Long userId : userOrders.keySet()) {
        			List<Order> orders = userOrders.get(userId);
        			synchronized (orders) {
	    	        	for (Order order : orders) {
	    	        		if ((order.getZaprosDocObj() != null && order.getZaprosDocObj().id == id) ||
	    	        				(order.getDocObj() != null && order.getDocObj().id == id)) {
	    	        			if (map != null)
	    	        				addOrderToMap(map, userId, type, order);
	    	        			res.add(order);
	    	        		}
	    	        	}
        			}
                }
    		}
        }
        return res;
	}

	private List<Order> findOrdersByErkkId(long id, Map<Long, Map<String, List<Order>>> map) {
		List<Order> res = new ArrayList<Order>();
        for (String type : ordersByUser.keySet()) {
        	Map<Long, List<Order>> userOrders = ordersByUser.get(type);
            synchronized (userOrders) {
                for (Long userId : userOrders.keySet()) {
        			List<Order> orders = userOrders.get(userId);
        			synchronized (orders) {
	    	        	for (Order order : orders) {
	    	        		if ((order.getErkkObj() != null && order.getErkkObj().id == id) ||
	    	        				(order.getZaprosErkkObj() != null && order.getZaprosErkkObj().id == id)) {
	    	        			if (map != null)
	    	        				addOrderToMap(map, userId, type, order);
	    	        			res.add(order);
	    	        		}
	    	        	}
        			}
                }
    		}
        }
        return res;
	}

	public void insertOrder(Order order, Order order_0, Map<Long, Map<String, List<Order>>> updOrders, Map<Long, Map<String, List<Order>>> delOrders) {
    	if (order != null) {
            // Раскидываем поручения по мапам (входящие, исходящие, мои) и по пользователям
        	if (order.isShow() && order.getStatusObj() != null 
        			&& ((STATUS_PROCESSING.equals(order.getStatusObj().uid) && !order.isReassigned()) || STATUS_WAITING.equals(order.getStatusObj().uid))
        			) {
        		// Мои поручения
        		if (order.getKillerObj() != null && order.getAuthorObj() != null && order.getKillerObj().equals(order.getPersonObj()) && 
        				((order.getPersonObj() == null && order.getDepObj() != null) || (order.getPersonObj() != null && order.getPersonObj().equals(order.getAuthorObj())))) {
        			orderCreated("my", order, order.getAuthorObj(), null, null, null, updOrders, delOrders);
        		}
        	}
    	}
    	if (order_0 != null) {
        	if (order_0.isShow() && order_0.getStatusObj() != null && (order_0.getTypeObj() == null || !TYPE_NOTIFICATION.equals(order_0.getTypeObj().uid))
        			&& ((STATUS_PROCESSING.equals(order_0.getStatusObj().uid) && !order_0.isReassigned()) || STATUS_WAITING.equals(order_0.getStatusObj().uid))
        			) {
        		if (order_0.getKillerObj() == null) {
        			if (order_0.getPersonObj() != null) {
        				orderCreated("in", order_0, order_0.getPersonObj(), null, null, null, updOrders, delOrders);
        			} else if (order_0.getDepObj() != null) {
        				orderCreated("in", order_0, null, null, order_0.getDepObj(), null, updOrders, delOrders);
        			} else if (order_0.getRoleObjs() != null) {
        				for (KrnObject roleObj : order_0.getRoleObjs()) 
        					orderCreated("in", order_0, null, null, null, roleObj, updOrders, delOrders);
        			}
        		}
        	}
        	// 30.09.2016 	Для исходящих поручений 
        	//			  	Добавляем для условие на статус "Исполняется", чтобы было не перепоручено
        	//				Убираем условие по исполнителю-производственной структуре
        	//
        	if (order_0.isShow() && order_0.getStatusObj() != null 
        			&& (STATUS_PROCESSING.equals(order_0.getStatusObj().uid) || STATUS_WAITING.equals(order_0.getStatusObj().uid))
        			) {
        		if (order_0.getParentPersonObj() != null && 
        				(
        						(order_0.getPersonObj() != null && !order_0.getPersonObj().equals(order_0.getParentPersonObj())) ||
        						(order_0.getRoleObjs() != null && order_0.getRoleObjs().size() > 0)
        				)
        			) {
        			orderCreated("out", order_0, order_0.getParentPersonObj(), null, null, null, updOrders, delOrders);
        		}
        		/* #11344 Убираем в фильтре исходящих зада отбор по детям */
        		/*if (order_0.getChildAuthorObjs() != null && order_0.getChildAuthorObjs().size() > 0 && !order_0.isReassigned()) {
        			for (KrnObject childAuthorObj : order_0.getChildAuthorObjs()) {
        				if (
        						(order_0.getPersonObj() != null && !order_0.getPersonObj().equals(childAuthorObj)) ||
        						(order_0.getRoleObjs() != null && order_0.getRoleObjs().size() > 0)
        				) {
        					orderCreated("out", order_0, childAuthorObj, null, null, null, updOrders, delOrders);
        				}
        			}
        		}*/
        	}
        	if(order_0.isShow() && order_0.getTypeObj() != null && TYPE_NOTIFICATION.equals(order_0.getTypeObj().uid)) {
        		if (order_0.getKillerObj() == null) {
        			if (order_0.getPersonObj() != null) {
        				orderCreated("notif", order_0, order_0.getPersonObj(), null, null, null, updOrders, delOrders);
        			} else if (order_0.getDepObj() != null) {
        				orderCreated("notif", order_0, null, null, order_0.getDepObj(), null, updOrders, delOrders);
        			} else if (order_0.getRoleObjs() != null) {
        				for (KrnObject roleObj : order_0.getRoleObjs()) 
        					orderCreated("notif", order_0, null, null, null, roleObj, updOrders, delOrders);
        			} else if (order_0.getUserObj() != null) {
        				orderCreated("notif", order_0, null, order_0.getUserObj(), null, null, updOrders, delOrders);
        			}
        		}
        	}
		}
	}

	public void removeOrder(Order order, Map<Long, Map<String, List<Order>>> delOrders, long trId) {
    	if (order != null) {
    		synchronized (ordersByUser) {
    	        for (String type : ordersByUser.keySet()) {
	            	Map<Long, List<Order>> userOrders = ordersByUser.get(type);
	        	    synchronized (userOrders) {
	        	       	for (Long userId : userOrders.keySet()) {
	        	       		List<Order> orders = userOrders.get(userId);
	        	    	    boolean b = (ORDER_TYPE_PROJECTS.equals(type) || trId == 0) 
	        	    	    		? orders.remove(order)
	        	    	    		: orders.contains(order);
	        	    	    if (b)
	        	    	    	addOrderToMap(delOrders, userId, type, order);
	        	        }
	        	    }
    	        }
			}
		}
	}
	
	public void updateOrder(Order order, Map<Long, Map<String, List<Order>>> updOrders) {
    	if (order != null) {
    		synchronized (ordersByUser) {
    	        for (String type : ordersByUser.keySet()) {
            		Map<Long, List<Order>> userOrders = ordersByUser.get(type);
        	        synchronized (userOrders) {
        	        	for (Long userId : userOrders.keySet()) {
        	               	List<Order> orders = userOrders.get(userId);
        	    	       	boolean b = orders.contains(order);
        	    	       	if (b)
        	    	       		addOrderToMap(updOrders, userId, type, order);
        	            }
        	    	}
    	        }
			}
		}
	}

	private void addOrderToMap(Map<Long, Map<String, List<Order>>> map,
			Long userId, String type, Order order) {
		
		Map<String, List<Order>> userOrders = map.get(userId);
        if (userOrders == null) {
        	userOrders = new HashMap<String, List<Order>>();
        	map.put(userId, userOrders);
        }
    	List<Order> orders = userOrders.get(type);
    	if (orders == null) {
    		orders = new ArrayList<Order>();
    		userOrders.put(type, orders);
    	}
    	if (!orders.contains(order)) {
        	orders.add(0, order);
        }
	}

	private void removeOrderFromMap(Map<Long, Map<String, List<Order>>> map,
			Long userId, String type, Order order) {
		
		Map<String, List<Order>> userOrders = map.get(userId);
        if (userOrders != null) {
	    	List<Order> orders = userOrders.get(type);
	    	if (orders != null) {
	        	orders.remove(order);
	        }
        }
	}

	public void orderDeleted(KrnObject orderObj, Map<Long, Map<String, List<Order>>> delOrders, long trId) {
    	removeOrder(new Order(orderObj, null, null, null, null, null, null, null, null, null, null, false, false, null, null, null, null, null, null), delOrders, trId);
	}

	// Добавление нового (изменнего поручения) в мапы
	private void orderCreated(String type, Order order, KrnObject personObj, KrnObject userObj, KrnObject depObj, KrnObject roleObj,
			Map<Long, Map<String, List<Order>>> updOrders, Map<Long, Map<String, List<Order>>> delOrders) {
		Map<Long, List<Order>> userOrders = null;
        synchronized (ordersByUser) {
	        userOrders = ordersByUser.get(type);
	        if (userOrders == null) {
	        	userOrders = new HashMap<Long, List<Order>>();
	        	ordersByUser.put(type, userOrders);
	        }
		}
        synchronized (userOrders) {
    		if (personObj != null) {
            	List<Order> orders = userOrders.get(personObj.id);
            	if (orders != null) {
            		synchronized (orders) {
		            	if (!orders.contains(order)) {
		    	        	orders.add(0, copyOrder(null, order));
			        		removeOrderFromMap(delOrders, personObj.id, type, order);
			        		addOrderToMap(updOrders, personObj.id, type, order);
		                }
            		}
    			}
    		} 
    		if (userObj != null) {
            	List<Order> orders = userOrders.get(userObj.id);
            	if (orders != null) {
            		synchronized (orders) {
		            	if (!orders.contains(order)) {
		    	        	orders.add(0, copyOrder(null, order));
			        		removeOrderFromMap(delOrders, userObj.id, type, order);
			        		addOrderToMap(updOrders, userObj.id, type, order);
		                }
            		}
    			}
    		}
    		// или для всех пользователей подразделения
    		if (depObj != null) {
    			Map<Long, List<Long>> usersByDepOfType = usersByDep.get(type);
    			if (usersByDepOfType != null) {
		        	List<Long> usersOfDep = usersByDepOfType.get(depObj.id);
		        	if (usersOfDep != null) {
			        	for (long personId : usersOfDep) {
			            	List<Order> orders = userOrders.get(personId);
			            	if (orders != null) {
			            		synchronized (orders) {
					            	if (!orders.contains(order)) {
					    	        	orders.add(0, copyOrder(null, order));
						        		removeOrderFromMap(delOrders, personId, type, order);
						        		addOrderToMap(updOrders, personId, type, order);
					                }
			            		}
							}
			        	}
		        	}
    			}
    		}
    		// или для всех пользователей роли
    		if (roleObj != null) {
	        	List<Long> usersOfRole = usersByRole.get(roleObj.id);
	        	if (usersOfRole != null) {
		        	for (long personId : usersOfRole) {
		        		long balansId = balansEdByPerson.get(personId);
		        		if (order.getBalObj() != null && order.getBalObj().id == balansId) {
			            	List<Order> orders = userOrders.get(personId);
			            	if (orders != null) {
			            		synchronized (orders) {
			            			if (!orders.contains(order)) {
					    	        	orders.add(0, copyOrder(null, order));
						        		removeOrderFromMap(delOrders, personId, type, order);
						        		addOrderToMap(updOrders, personId, type, order);
					                }
			            		}
							}
		        		}
		        	}
	        	}
    		}
		}
	}

	private Order reloadOrder(KrnObject orderObj, Session s, long trId) {
    	// Перечитываем атрибуты поручения
    	long[] orderIds = new long[] {orderObj.id};
    	
		try {
            AttrRequestBuilder arb = getRequestForOrder(s);

            QueryResult qr = s.getObjects(orderIds, arb.build(), trId);

            for (Object[] prow : qr.rows) {
            	KrnObject statusObj = arb.getObjectValue(attrStatus.name, prow);
            	KrnObject personObj = arb.getObjectValue(attrRespPerson.name, prow);
            	KrnObject userObj = arb.getObjectValue(attrRespUser.name, prow);
            	KrnObject depObj = arb.getObjectValue(attrRespDep.name, prow);
            	List<Value> roleVals = (List<Value>)arb.getValue(attrRespRole.name, prow);
            	List<KrnObject> roleObjs = null; 
            	if (roleVals != null && roleVals.size() > 0) {
            		roleObjs = new ArrayList<KrnObject>();
            		for (Value val : roleVals)
            			if (val != null && val.value instanceof KrnObject)
            				roleObjs.add((KrnObject)val.value);
            	}
            	KrnObject balObj = arb.getObjectValue(attrBalansEd.name, prow);
            	KrnObject authorObj = arb.getObjectValue(attrAuthor.name, prow);
            	KrnObject parentObj = arb.getObjectValue(attrParent.name, prow);
            	KrnObject parentPersonObj = arb.getObjectValue(attrParent.name + "." + attrRespPerson.name, prow);
            	KrnObject typeObj = arb.getObjectValue(attrOrderType.name, prow);
            	KrnObject killerObj = arb.getObjectValue(attrCanKill.name + "." + attrUserPerson.name, prow);
            	boolean show = arb.getBooleanValue(attrShow.name, prow, false);
            	boolean reassigned = arb.getBooleanValue(attrReassigned.name, prow, false);
            	KrnObject docObj = arb.getObjectValue(attrOrderDoc.name, prow);
            	KrnObject zaprosDocObj = arb.getObjectValue(attrOrderDoc.name + "." + attrDocZaprosDoc.name, prow);
            	KrnObject erkkObj = arb.getObjectValue(attrOrderDoc.name + "." + attrDocAderkk.name, prow);
            	KrnObject erkkObj2 = arb.getObjectValue(attrOrderDoc.name + "." + attrDocZaprosDoc.name + "." + attrDocAderkk.name, prow);

            	List<Value> children_ = (List<Value>) arb.getValue(attrChildren.name, prow);
            	List<KrnObject> childObjs = null; 
            	if (children_ != null && children_.size() > 0) {
            		childObjs = new ArrayList<KrnObject>();
            		for (Value val : children_)
            			if (val != null && val.value instanceof KrnObject)
            				childObjs.add((KrnObject)val.value);
            		
            		long childIds[] = new long[childObjs.size()];
            		
            		for (int i=0; i<childObjs.size(); i++) {
            			childIds[i] = childObjs.get(i).id;
    	        	}
            		childObjs.clear();
    	
    	            AttrRequestBuilder arb2 = new AttrRequestBuilder(clsOrder, s).add(attrAuthor.name);

    	            QueryResult qr2 = s.getObjects(childIds, arb2.build(), trId);
            		
    	            for (Object[] prow2 : qr2.rows) {
    	            	KrnObject authorObj2 = arb2.getObjectValue(attrAuthor.name, prow2);
    	            	if (!childObjs.contains(authorObj2))
    	            		childObjs.add(authorObj2);
    	            }
            	}
            	
            	return new Order(orderObj, parentObj, personObj, userObj, depObj, roleObjs, statusObj, typeObj, balObj,
            			authorObj, killerObj, show, reassigned, docObj, zaprosDocObj, erkkObj, erkkObj2,
            			parentPersonObj, childObjs);
	        }
		} catch (KrnException e) {
			log.error(e, e);
		}
		return null;
	}
	
	private Order copyOrder(Order order, Order orderInDB) {
		if (order == null)
			return new Order(orderInDB.getOrderObj(), orderInDB.getParentObj(), orderInDB.getPersonObj(), orderInDB.getUserObj(),
					orderInDB.getDepObj(), orderInDB.getRoleObjs(), orderInDB.getStatusObj(), orderInDB.getTypeObj(), orderInDB.getBalObj(),
					orderInDB.getAuthorObj(), orderInDB.getKillerObj(), orderInDB.isShow(), orderInDB.isReassigned(),
					orderInDB.getDocObj(), orderInDB.getZaprosDocObj(), orderInDB.getErkkObj(), orderInDB.getZaprosErkkObj(),
					orderInDB.getParentPersonObj(), orderInDB.getChildAuthorObjs());
		else {
			order.setBalObj(orderInDB.getBalObj());
			order.setParentObj(orderInDB.getParentObj());
			order.setPersonObj(orderInDB.getPersonObj());
			order.setUserObj(orderInDB.getUserObj());
			order.setDepObj(orderInDB.getDepObj());
			order.setRoleObjs(orderInDB.getRoleObjs());
			order.setStatusObj(orderInDB.getStatusObj());
			order.setTypeObj(orderInDB.getTypeObj());
			order.setAuthorObj(orderInDB.getAuthorObj());
			order.setKillerObj(orderInDB.getKillerObj());
			order.setShow(orderInDB.isShow());
			order.setReassigned(orderInDB.isReassigned());
			order.setDocObj(orderInDB.getDocObj());
			order.setZaprosDocObj(orderInDB.getZaprosDocObj());
			order.setErkkObj(orderInDB.getErkkObj());
			order.setZaprosErkkObj(orderInDB.getZaprosErkkObj());
			order.setParentPersonObj(orderInDB.getParentPersonObj());
			order.setChildAuthorObjs(orderInDB.getChildAuthorObjs());
			return order;
		}
	}

	public class Order {
		private KrnObject orderObj;
		private KrnObject parentObj;
		private KrnObject personObj;
		private KrnObject userObj;
		private KrnObject depObj;
		private List<KrnObject> roleObjs;
		private KrnObject statusObj;
		private KrnObject typeObj;
		private KrnObject balObj;
		private KrnObject authorObj;
		private KrnObject killerObj;
    	boolean show;
    	boolean reassigned;
		private KrnObject docObj;
		private KrnObject zaprosDocObj;
		private KrnObject erkkObj;
		private KrnObject zaprosErkkObj;
		
		private KrnObject parentPersonObj;
		private List<KrnObject> childAuthorObjs;
		
		private boolean forceUpdate = false;
		
		public Order(KrnObject orderObj, KrnObject parentObj,
				KrnObject personObj, KrnObject userObj, KrnObject depObj, List<KrnObject> roleObjs, KrnObject statusObj, KrnObject typeObj,
				KrnObject balObj, KrnObject authorObj, KrnObject killerObj, boolean show, boolean reassigned,
				KrnObject docObj, KrnObject zaprosDocObj, KrnObject erkkObj, KrnObject zaprosErkkObj,
				KrnObject parentPersonObj, List<KrnObject> childAuthorObjs) {
			this.orderObj = orderObj;
			this.parentObj = parentObj;
			this.personObj = personObj;
			this.userObj = userObj;
			this.depObj = depObj;
			this.roleObjs = roleObjs;
			this.statusObj = statusObj;
			this.typeObj = typeObj;
			this.balObj = balObj;
			this.authorObj = authorObj;
			this.killerObj = killerObj;
			this.show = show;
			this.reassigned = reassigned;
			this.docObj = docObj;
			this.zaprosDocObj = zaprosDocObj;
			this.erkkObj = erkkObj;
			this.zaprosErkkObj = zaprosErkkObj;
			
			this.parentPersonObj = parentPersonObj;
			this.childAuthorObjs = childAuthorObjs;
		}

		public KrnObject getParentObj() {
			return parentObj;
		}

		public void setParentObj(KrnObject parentObj) {
			this.parentObj = parentObj;
		}

		public KrnObject getOrderObj() {
			return orderObj;
		}
		public void setOrderObj(KrnObject orderObj) {
			this.orderObj = orderObj;
		}
		public KrnObject getPersonObj() {
			return personObj;
		}
		public void setPersonObj(KrnObject personObj) {
			this.personObj = personObj;
		}
		public KrnObject getUserObj() {
			return userObj;
		}
		public void setUserObj(KrnObject userObj) {
			this.userObj = userObj;
		}
		public KrnObject getDepObj() {
			return depObj;
		}
		public void setDepObj(KrnObject depObj) {
			this.depObj = depObj;
		}
		public List<KrnObject> getRoleObjs() {
			return roleObjs;
		}
		public void setRoleObjs(List<KrnObject> roleObjs) {
			this.roleObjs = roleObjs;
		}
		public KrnObject getStatusObj() {
			return statusObj;
		}
		public void setStatusObj(KrnObject statusObj) {
			this.statusObj = statusObj;
		}
		public KrnObject getTypeObj() {
			return typeObj;
		}
		public void setTypeObj(KrnObject typeObj) {
			this.typeObj = typeObj;
		}
		public KrnObject getBalObj() {
			return balObj;
		}

		public void setBalObj(KrnObject balObj) {
			this.balObj = balObj;
		}

		public KrnObject getAuthorObj() {
			return authorObj;
		}

		public void setAuthorObj(KrnObject authorObj) {
			this.authorObj = authorObj;
		}

		public KrnObject getKillerObj() {
			return killerObj;
		}

		public void setKillerObj(KrnObject killerObj) {
			this.killerObj = killerObj;
		}

		public boolean isShow() {
			return show;
		}

		public void setShow(boolean show) {
			this.show = show;
		}

		public boolean isReassigned() {
			return reassigned;
		}

		public void setReassigned(boolean reassigned) {
			this.reassigned = reassigned;
		}

		public KrnObject getDocObj() {
			return docObj;
		}

		public void setDocObj(KrnObject docObj) {
			this.docObj = docObj;
		}

		public KrnObject getZaprosDocObj() {
			return zaprosDocObj;
		}

		public void setZaprosDocObj(KrnObject zaprosDocObj) {
			this.zaprosDocObj = zaprosDocObj;
		}

		public KrnObject getErkkObj() {
			return erkkObj;
		}

		public void setErkkObj(KrnObject erkkObj) {
			this.erkkObj = erkkObj;
		}

		public KrnObject getZaprosErkkObj() {
			return zaprosErkkObj;
		}

		public void setZaprosErkkObj(KrnObject zaprosErkkObj) {
			this.zaprosErkkObj = zaprosErkkObj;
		}

		public boolean isForceUpdate() {
			return forceUpdate;
		}

		public void setForceUpdate(boolean forceUpdate) {
			this.forceUpdate = forceUpdate;
		}

		public KrnObject getParentPersonObj() {
			return parentPersonObj;
		}

		public void setParentPersonObj(KrnObject parentPersonObj) {
			this.parentPersonObj = parentPersonObj;
		}

		public List<KrnObject> getChildAuthorObjs() {
			return childAuthorObjs;
		}

		public void setChildAuthorObjs(List<KrnObject> childAuthorObjs) {
			this.childAuthorObjs = childAuthorObjs;
		}
		
		public boolean containsChildAuthor(KrnObject authorObj) {
			if (childAuthorObjs == null || childAuthorObjs.size() == 0)
				return false;
			return childAuthorObjs.contains(authorObj);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj instanceof Order)
				return orderObj.id == ((Order)obj).getOrderObj().id;
			return false;
		}
	}
	
	public static void removeUserOrders(UserSession us) {
		if (reloadOrdersAtLogin) {
			Long pid = personsByUser.get(us.getUserId());
			
			if (pid != null) {
				synchronized (ordersByUser) {
			        for (String type : ordersByUser.keySet()) {
			        	Map<Long, List<Order>> userOrders = ordersByUser.get(type);
		    	        synchronized (userOrders) {
			            	userOrders.remove(pid);
			        	}
			        }
				}
	        }
		}
		
		UUID uuid = us.getId();

		attrChanges.remove(uuid);
	}
	
	private String toString(List<?> list) {
		StringBuilder res = new StringBuilder("[");
		for (Object o : list)
			res.append(o).append(", ");
		res.append("]");
		return res.toString();
	}
}
