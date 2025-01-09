package com.cifs.or2.server.workflow.organisation;

import com.cifs.or2.kernel.AttrChange;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.ObjectValue;
import com.cifs.or2.server.Session;
import com.cifs.or2.server.UserSrv;
import com.cifs.or2.util.MultiMap;

import static kz.tamur.ods.ComparisonOperations.CO_EQUALS;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kz.tamur.ods.Driver2;
import kz.tamur.or3ee.common.AttrChangeListener;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.or3ee.server.kit.SrvUtils;
import kz.tamur.util.Funcs;

/**
 * Created by IntelliJ IDEA. User: Vale Date: 18.08.2004 Time: 10:43:23 To
 * change this template use File | Settings | File Templates. 
 */
public class OrganisationComponent implements AttrChangeListener {

	private static Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + OrganisationComponent.class);
	
	private HashMap<Long, UserSrv> users = new HashMap<Long, UserSrv>();

	// баланасовые единицы и их дети
	private MultiMap<Long, Long> balansEdsByParent = new MultiMap<Long, Long>();
	// балансовые единицы и их сотрудники
	private MultiMap<Long, Long> usersByBalansEd = new MultiMap<Long, Long>();
	// роли(пользователи) и их родители
	private MultiMap<Long, KrnObject> rolesByChild = new MultiMap<Long, KrnObject>();
	// роли и их дети
	private MultiMap<Long, Long> rolesByParent = new MultiMap<Long, Long>();

	// Класс User
	private KrnClass clsUser;
	// Класс UserFolder (роли)
	private KrnClass clsRole;
	// Класс "уд::осн::Баланс_ед"
	private KrnClass clsBalansEd;
	// Атрибут дети Балансовой единицы
	private KrnAttribute attrBalansEdChildren;
	private KrnAttribute attrBalansEdParent;
	private KrnAttribute attrUserParent;
	private KrnAttribute attrUserBalansEd;

	// пользователь "sys"
	private UserSrv superUser;
	
	private String dsName = null;
	private boolean listenerInitialized = false;

	private static Map<UUID, List<AttrChange>> attrChanges = new HashMap<>();
	private static Map<Long, List<AttrChange>> committedAttrChanges = new HashMap<>();
	private static Map<UUID, List<Long>> committedTrs = new HashMap<>();
	private static Map<UUID, List<Long>> rollbackedTrs = new HashMap<>();

	public OrganisationComponent(Session session) {
		try {
			this.dsName = session.getDsName();
			
			// инициализируем необходимые классы и атрибуты и слушаем их изменения
			initListener(session);

			// загрузка иерархии ролей
			loadRoles(session);

			//Загрузка иерархии балансовых единиц
			reloadBalansEds(session);
			
		} catch (KrnException ex) {
            log.error(ex, ex);
		}
	}
	
	//Перезагрузка иерархии балансовых единиц
	private void reloadBalansEds(Session s) {
		if (clsBalansEd != null) {
			try {
				// считываем из базы все балансовые единицы
				KrnObject[] objs = s.getClassObjects(clsBalansEd, new long[0], 0);
				long[] ids = Funcs.makeObjectIdArray(objs);
				
				// считываем у каждой балансовой единицы ее детей
				MultiMap<Long, Long> tmp = new MultiMap<Long, Long>();
				ObjectValue[] vals = s.getObjectValues(ids, attrBalansEdChildren.id, new long[0], 0);
				for (ObjectValue val : vals) {
					if (val.value != null)
						tmp.put(val.objectId, val.value.id);
				}

				// и помещаем в мапу
				synchronized (balansEdsByParent) {
					balansEdsByParent.clear();
					for (Iterator<Long> it = tmp.keySet().iterator(); it.hasNext(); ) {
						Long key = it.next();
						balansEdsByParent.put(key, tmp.get(key));
					}
				}
			} catch (Exception e) {
	            log.error(e, e);
			}
		}
	}

	// загрузка иерархии ролей
	private void loadRoles(Session s) {
		if (clsRole != null) {
			try {
				// считываем из базы все роли
				KrnObject[] objs = s.getClassObjects(clsRole, new long[0], 0);
				long[] ids = Funcs.makeObjectIdArray(objs);
				
				// считываем у всех ролей родителей
				MultiMap<Long, KrnObject> tmpChild = new MultiMap<Long, KrnObject>();
				MultiMap<Long, Long> tmpParent = new MultiMap<Long, Long>();
				ObjectValue[] vals = s.getObjectValues(ids, attrUserParent.id, new long[0], 0);
				for (ObjectValue val : vals) {
					if (val.value != null) {
						tmpChild.put(val.objectId, val.value);
						tmpParent.put(val.value.id, val.objectId);
					}
				}

				// и помещаем в мапу
				synchronized (rolesByChild) {
					rolesByChild.clear();
					for (Iterator<Long> it = tmpChild.keySet().iterator(); it.hasNext(); ) {
						Long key = it.next();
						rolesByChild.put(key, tmpChild.get(key));
					}
				}
				synchronized (rolesByParent) {
					rolesByParent.clear();
					for (Iterator<Long> it = tmpParent.keySet().iterator(); it.hasNext(); ) {
						Long key = it.next();
						rolesByParent.put(key, tmpParent.get(key));
					}
				}

				// помещаем роли в мапу пользователей
				for (KrnObject obj : objs) {
					UserSrv actor = users.get(obj.id);
					if (actor == null) {
						actor = new UserSrv(s, null, obj, null, null, false, this);
						users.put(obj.id, actor);
					}
				}
			} catch (Exception e) {
	            log.error(e, e);
			}
		}
	}
	
	public void getParentRoles(long[] roles, List<KrnObject> result) {
		List<KrnObject> tmp = new ArrayList<>();
		for (long role : roles) {
			List<KrnObject> parents = rolesByChild.get(role);
			if (parents != null) {
				for (KrnObject parent : parents) {
					if (!result.contains(parent)) {
						tmp.add(parent);
						result.add(parent);
					}
				}
			}
		}
		if (tmp.size() > 0)
			getParentRoles(Funcs.makeObjectIdArray(tmp), result);
	}

	public boolean isUserActor(long userId,long actorId,Session session) {
		if(userId==actorId) return true;
		UserSrv user=findActorById(userId, session);
		if(user!=null){
			KrnObject[] parents=user.getParents();
			if (parents != null) {
				for(KrnObject parent:parents){
					if(parent.getId()==actorId) return true;
				}
			}
		}
		return false;
	}
	
	public UserSrv getSuperUser(Session s) {
		if (superUser == null) {
			try {
	            KrnClass userCls = s.getClassByName("User");
	            KrnAttribute nameAttr = s.getAttributeByName(userCls, "name");
	            KrnObject[] objs = s.getObjectsByAttribute(userCls.id, nameAttr.id, 0, CO_EQUALS, "sys", 0);
	
	            if (objs.length == 1)
	            	superUser = findActorById(objs[0].id, s);
	            
			} catch (Exception e) {
				log.error(e, e);
			}

		}
		return superUser;
	}
	
	public UserSrv findActorById(long actorId, Session session) {
		UserSrv res = users.get(actorId);
		if (res == null) {
			res=loadUser(actorId, session);
		}
		return res;
	}

	private UserSrv loadUser(long actorId, Session session) {
		UserSrv res = null;
		try {
			KrnObject child = session.getObjectById(actorId, 0);
			res = new UserSrv(session, null, child, null, null, false, this);
			users.put(actorId, res);

			if (res.getBalansId() != 0)
				usersByBalansEd.put(res.getBalansId(), res.getUserId());
		} catch (KrnException e) {
			log.error(e, e);
		}
		return res;
	}
	
	private UserSrv removeUser(long actorId) {
		UserSrv user = users.remove(actorId);
		if (user != null && user.getBalansId() > 0) {
			List<Long> usersOfBalansEd = usersByBalansEd.get(user.getBalansId());
			if (usersOfBalansEd != null)
				usersOfBalansEd.remove(actorId);
		}
		return user;
	}

	private void loadFolder(KrnObject obj, Session s) {
		try {
			UserSrv folder = new UserSrv(s, null, obj, null, null, false, this);
			users.put(obj.id, folder);
		} catch (KrnException e) {
			log.error(e, e);
		}
	}
	
	private UserSrv removeFolder(long id) {
		UserSrv folder = users.remove(id);
		List<KrnObject> parents = rolesByChild.remove(id);
		if (parents != null) {
			for (KrnObject parent : parents) {
				List<Long> children = rolesByParent.get(parent.id);
				if (children != null)
					children.remove(id);
			}
		}
		
		List<Long> children = rolesByParent.remove(id);
		if (children != null) {
			for (long child : children) {
				List<KrnObject> parentObjs = rolesByChild.get(child);
				if (parentObjs != null) {
					parentObjs.remove(new KrnObject(id, "", 0));
				}
			}
		}
		return folder;
	}
	
	private void loadBalansEdParent(KrnObject obj, Session s) {
		try {
			// считываем родителя
			KrnObject[] vals = s.getObjects(obj.id, attrBalansEdParent.id, new long[0], 0);

			// и помещаем в мапу
			synchronized (balansEdsByParent) {
				if (vals.length > 0 && vals[0] != null) {
					balansEdsByParent.put(vals[0].id, obj.id);
				}
			}
		} catch (Exception e) {
            log.error(e, e);
		}
	}

	private void removeBalansEdFromParent(long objId) {
		synchronized (balansEdsByParent) {
			for (Iterator<Long> it = balansEdsByParent.keySet().iterator(); it.hasNext(); ) {
				Long key = it.next();
				List<Long> children = balansEdsByParent.get(key);
				if (children != null)
					children.remove(objId);
			}
		}
	}

	private void removeBalansEd(long objId) {
		synchronized (balansEdsByParent) {
			balansEdsByParent.remove(objId);
		}
		removeBalansEdFromParent(objId);
	}

	public Collection<Long> getBalansEdUsers(long balansEd) {
		Collection<Long> col = new ArrayList<Long>();
		synchronized (usersByBalansEd) {
			Collection<Long> col2 = usersByBalansEd.get(balansEd);
			if (col2 != null)
				col.addAll(col2);
		}
		
		Collection<Long> bases = new ArrayList<Long>();
		synchronized (balansEdsByParent) {
			Collection<Long> col2 = balansEdsByParent.get(balansEd);
			if (col2 != null)
				bases.addAll(col2);
		}
		for(Long bs:bases){
			Collection<Long> col2 = getBalansEdUsers(bs);
			if (col2 != null)
				col.addAll(col2);
		}
		return col;
	}

	private Collection<Long> findUsersByGroup(long groupId, Collection<Long> group) {
		UserSrv user = users.get(groupId);
		if (group == null)
			group = new TreeSet<Long>();
			group.add(user.getUserId());
		if (user.getUserObj().classId == clsRole.id) {
			KrnObject[] parents = user.getParents();
			if (parents != null) {
				for (KrnObject parent : parents) {
					Object obj = users.get(parent.id);
					if (obj != null)
						findUsersByGroup(parent.id, group);
				}
			}
			KrnObject[] children = user.getChildren();
			for (KrnObject aChildren : children) {
				if (aChildren.classId != clsRole.id) {
						group.add(aChildren.id);
				}
			}
		}
		return group;
	}

	public boolean isActorInGroup(long groupId, long actorId) {
		UserSrv user = users.get(groupId);
		if (user != null) {
			if (actorId == groupId) return true;
			if (user.getUserObj().classId == clsRole.id) {
				KrnObject[] parents = user.getParents();
				if (parents != null) {
					for (KrnObject parent : parents) {
						if (isActorInGroup(parent.id, actorId)) return true;
					}
				}
				KrnObject[] children = user.getChildren();
				for (KrnObject aChildren : children) {
					if (aChildren.classId != clsRole.id) {
						if (actorId == aChildren.id) return true;
					}
				}
			}
		}
		return false;
	}

	public Collection<Long> findActorIdsByGroup(long groupId, Collection<Long> group) {
		if (group == null)
			group = new TreeSet<Long>();
		UserSrv user = users.get(groupId);
		if (user != null) {
			group.add(groupId);
			if (user.getUserObj()!=null && user.getUserObj().classId == clsRole.id) {
				KrnObject[] parents = user.getParents();
				if (parents != null) {
					for (KrnObject parent : parents) {
						Object obj = users.get(parent.id);
						if (obj != null)
							findUsersByGroup(parent.id, group);
					}
				}
				KrnObject[] children = user.getChildren();
				for (KrnObject aChildren : children) {
					if (aChildren.classId != clsRole.id) {
						group.add(aChildren.id);
					}
				}
			}
		}
		return group;
	}

	public void updateUser(KrnObject[] objs, boolean isUpdateParent, Session s) {
		for (KrnObject obj : objs) {
			removeUser(obj.id);
			loadUser(obj.id, s);
		}
	}

	public void removeUser(KrnObject[] objs) {
		for (KrnObject obj : objs) {
			removeUser(obj.id);
		}
	}
	
	private synchronized void initListener(Session s) throws KrnException {
		if (!listenerInitialized) {
			clsUser = s.getClassByName("User");
			clsRole = s.getClassByName("UserFolder");
			clsBalansEd = s.getClassByName("уд::осн::Баланс_ед");
			
			if (clsBalansEd != null) {
				attrBalansEdChildren = s.getAttributeByName(clsBalansEd, "дети");
				attrBalansEdParent = s.getAttributeByName(clsBalansEd, "родитель");
				Driver2.addAttrChangeListener(clsBalansEd.id, this);
			}
			
			if (clsUser != null) {
				attrUserParent = s.getAttributeByName(clsUser, "parent");
				attrUserBalansEd = s.getAttributeByName(clsUser, "баланс_ед");
				Driver2.addAttrChangeListener(clsUser.id, this);
			}

			if (clsRole != null) 
				Driver2.addAttrChangeListener(clsRole.id, this);

			listenerInitialized = true;
		}
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
	
	public void attrCommitted(AttrChange ch) {
    	List<AttrChange> l = committedAttrChanges.get(ch.trId);
		if (l == null) {
			l = new ArrayList<AttrChange>();
			committedAttrChanges.put(ch.trId, l);
		}
		
		l.add(ch);
	}

	@Override
	public void rollback(UUID uuid) {
		if (uuid != null) {
			List<AttrChange> l = attrChanges.remove(uuid);
			if (l != null && l.size() > 0) {

				if (l.get(0).obj.classId == clsUser.id)
					log.info("ROLBACK USER " + uuid + "; SIZE = " + l.size());
				else if (l.get(0).obj.classId == clsRole.id)
					log.info("ROLBACK FOLDER " + uuid + "; SIZE = " + l.size());
				else if (l.get(0).obj.classId == clsBalansEd.id)
					log.info("ROLBACK BALANSED " + uuid + "; SIZE = " + l.size());

				l.clear();
			}
			
			committedTrs.remove(uuid);
			rollbackedTrs.remove(uuid);
		}
	}

	@Override
	public void commit(UUID uuid) {
		if (uuid != null) {
			List<AttrChange> l = attrChanges.remove(uuid);
			if (l != null && l.size() > 0) {
				log.info("COMMIT ORGCOMP " + uuid + "; SIZE = " + l.size());
				
		        try {
			        Session s = SrvUtils.getSession(this.dsName, "sys", null);
			        try {
						for (AttrChange ch : l) {
							if (ch.obj.classId == clsUser.id)
								log.info("COMMIT USER " + ch.obj.id);
							else if (ch.obj.classId == clsRole.id)
								log.info("COMMIT FOLDER " + ch.obj.id);
							else if (ch.obj.classId == clsBalansEd.id)
								log.info("COMMIT BALANSED " + ch.obj.id);
							
							if (ch.trId > 0)
								attrCommitted(ch);
							else
								processAttrChanged(ch.obj, ch.attrId, s);
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
				l.clear();
			}
			
	    	List<Long> trs = committedTrs.remove(uuid);
			if (trs != null && trs.size() > 0) {
				log.info("COMMIT LONG TRANSACTION ORGCOMP " + uuid + "; trs.size = " + trs.size());
				
		        try {
			        Session s = SrvUtils.getSession(this.dsName, "sys", null);
			        try {
				        for (Long trId : trs) {
				        	l = committedAttrChanges.remove(trId);
							if (l != null && l.size() > 0) {
								log.info("COMMIT LONG TRANSACTION ORGCOMP " + uuid + "; TR = " + trId);
							
								for (AttrChange ch : l) {
									if (ch.obj.classId == clsUser.id)
										log.info("COMMIT LONG TRANSACTION USER " + ch.obj.id);
									else if (ch.obj.classId == clsRole.id)
										log.info("COMMIT LONG TRANSACTION FOLDER " + ch.obj.id);
									else if (ch.obj.classId == clsBalansEd.id)
										log.info("COMMIT LONG TRANSACTION BALANSED " + ch.obj.id);
									
									processAttrChanged(ch.obj, ch.attrId, s);
								}
								l.clear();
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

				trs.clear();
			}
			
	    	trs = rollbackedTrs.remove(uuid);
			if (trs != null && trs.size() > 0) {
				log.info("ROLBACK LONG TRANSACTION ORGCOMP " + uuid + "; trs.size = " + trs.size());
				
		        for (Long trId : trs) {
		        	l = committedAttrChanges.remove(trId);
					if (l != null && l.size() > 0) {
		
						if (l.get(0).obj.classId == clsUser.id)
							log.info("ROLBACK LONG TRANSACTION USER " + uuid + "; SIZE = " + l.size());
						else if (l.get(0).obj.classId == clsRole.id)
							log.info("ROLBACK LONG TRANSACTION FOLDER " + uuid + "; SIZE = " + l.size());
						else if (l.get(0).obj.classId == clsBalansEd.id)
							log.info("ROLBACK LONG TRANSACTION BALANSED " + uuid + "; SIZE = " + l.size());
		
						l.clear();
					}
		        }
			}
		}
	}

	@Override
	public void commitLongTransaction(UUID uuid, long trId) {
		if (uuid != null) {
	    	List<Long> trs = committedTrs.get(uuid);
			if (trs == null) {
				trs = new ArrayList<Long>();
				committedTrs.put(uuid, trs);
			}
			trs.add(trId);
		}
	}

	@Override
	public void rollbackLongTransaction(UUID uuid, long trId) {
		if (uuid != null) {
	    	List<Long> trs = rollbackedTrs.get(uuid);
			if (trs == null) {
				trs = new ArrayList<Long>();
				rollbackedTrs.put(uuid, trs);
			}
			trs.add(trId);
		}
	}

	public void processAttrChanged(KrnObject obj, long attrId, Session s) {
		if (obj.classId == clsUser.id) {
			if (attrId == 0 || attrId == 1) {
				loadUser(obj.id, s);
			} else if (attrId == 2) {
				removeUser(obj.id);
			} else if (attrUserBalansEd != null && attrId == attrUserBalansEd.id) {
				removeUser(obj.id);
				loadUser(obj.id, s);
			} else if (attrUserParent != null && attrId == attrUserParent.id) {
				UserSrv user = users.get(obj.id);
				if (user != null)
					user.reloadParents(s, this);
			}
		} else if (obj.classId == clsRole.id) {
			if (attrId == 0 || attrId == 1) {
				loadFolder(obj, s);
			} else if (attrId == 2) {
				removeFolder(obj.id);
			} else if (attrUserParent != null && attrId == attrUserParent.id) {
				UserSrv folder = removeFolder(obj.id);
				if (folder != null) {
					KrnObject[] parents = folder.loadImmediateParents(s);
					
					synchronized (rolesByChild) {
						for (KrnObject parent : parents) {
							rolesByChild.put(obj.id, parent);
						}
					}
					synchronized (rolesByParent) {
						for (KrnObject parent : parents) {
							rolesByParent.put(parent.id, obj.id);
						}
					}

					loadFolder(obj, s);
				}
			}
		} else if (obj.classId == clsBalansEd.id) {
			if (attrId == 2) {
				removeBalansEd(obj.id);
			} else if (attrBalansEdParent != null && attrId == attrBalansEdParent.id) {
				removeBalansEdFromParent(obj.id);
				loadBalansEdParent(obj, s);
			}
		}
	}
}
