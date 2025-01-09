package kz.tamur.web.common;

import com.cifs.or2.kernel.*;
import com.cifs.or2.server.Session;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.util.AttrRequestBuilder;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import java.util.*;

import kz.tamur.comps.Constants;
import kz.tamur.ods.ComparisonOperations;
import kz.tamur.ods.Driver2;
import kz.tamur.or3ee.common.AttrChangeListener;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.or3ee.server.kit.SrvUtils;
import kz.tamur.util.AbstractDesignerTreeNode;
import kz.tamur.web.common.LangHelper.WebLangItem;
import kz.tamur.web.common.ProcessHelper.ProcessNode;

import javax.swing.tree.TreePath;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by IntelliJ IDEA.
 * User: Администратор
 * Date: 12.01.2007
 * Time: 15:55:11
 * To change this template use File | Settings | File Templates.
 */
public class ArchiveHelper {
	private static HyperNode root;
	private static HyperNode dictRoot;
	private static HyperNode adminRoot;
    private Set<Long> roItems;
    private Set<Long> rwItems;
    private Hashtable<String, Boolean> expStates;

    private static KrnClass uiCls, uiFolderCls;
	private WebSession session;
	private Log log;
	private static Log logReload;
	private static ArchiveListener listener;
	private static KrnAttribute attrParent;
	private static KrnAttribute attrHyper;
	private static KrnAttribute attrTitle;
	private static KrnAttribute attrDialog;
	private static KrnAttribute attrChangeable;
	private static KrnAttribute attrIndex;

	private static JsonArray listArchsSave;
	
//	private List<HyperNode> dictListKaz = new ArrayList<HyperNode>();
//	private List<HyperNode> dictListRus = new ArrayList<HyperNode>();
	
	private static Map<String, HyperNode> nodesByUid = new HashMap<String, HyperNode>();
	private static Map<Long, List<HyperNode>> nodesByParentId = new HashMap<Long, List<HyperNode>>();
	private static Map<UUID, List<AttrChange>> attrChanges = new HashMap<UUID, List<AttrChange>>();
	private static Boolean listenerInitialized = false;

    public ArchiveHelper(WebSession tempSess) throws KrnException {
    	this.session = tempSess;
    	initListener(session);
        this.log = WebSessionManager.getLog(session.getKernel().getUserSession().dsName, session.getKernel().getUserSession().logName);

        loadRoots(tempSess);
        load(tempSess);
    }
    
    
    private void load(WebSession s) throws KrnException {
        Hashtable<String, Boolean> expandedState = new Hashtable<String, Boolean>();

        Set<Long> read = s.getKernel().getUser().getReadOnlyItems();
        Set<Long> write = s.getKernel().getUser().getReadWriteItems();

        roItems = read;
        rwItems = write;
        expStates = expandedState;
    }

    public void release() {
        roItems.clear();
        rwItems.clear();
        expStates.clear();
        roItems = null;
        rwItems = null;
        expStates = null;
    }

    public HyperNode getRoot() {
        return root;
    }

    public static synchronized void loadRoots(WebSession s) throws KrnException {
    	if (root == null) {
    		KrnObject[] objs = null;
            KrnClass cls = s.getKernel().getClassByName("HiperTree");
            KrnAttribute isSystem = s.getKernel().getAttributeByName(cls, "isSystem");
            if (isSystem != null) 
            	objs = s.getKernel().getObjectsByAttribute(cls.id, isSystem.id, 0, ComparisonOperations.CO_EQUALS, 1, 0);
            
            if (objs != null && objs.length > 2) {
                Arrays.sort(objs, new Comparator<KrnObject>() {
    				@Override
    				public int compare(KrnObject o1, KrnObject o2) {
    					// TODO Auto-generated method stub
    					return new Long(o1.id).compareTo(o2.id);
    				}
    			});

	            long[] ids = {objs[0].id, objs[1].id, objs[2].id};
	            load(s, ids);
	            
	            // root устанавливаем после считывания всех нод, иначе возможна недогрузка узлов!!!(Redmine #10460)
                root = new HyperNode(objs[0], null);
	            dictRoot = new HyperNode(objs[1], null);
	            adminRoot = new HyperNode(objs[2], null);
            } else {
            	KrnClass clsMain = s.getKernel().getClassByName("MainTree");
                KrnObject obj = s.getKernel().getClassObjects(clsMain, 0)[0];

                KrnObject[] objsMains = s.getKernel().getObjects(obj, "hipers", 0);

                long[] ids = {objsMains[0].id, objsMains[1].id};
	            load(s, ids);
	            
                root = new HyperNode(objsMains[0], null);
                dictRoot = new HyperNode(objsMains[1], null);
            }
    	}
    }

    public HyperNode getNode(String uid) {
        return nodesByUid.get(uid);
    }

    public HyperNode getDictRoot() {
        return dictRoot;
    }
    
    public HyperNode getAdminRoot() {
		return adminRoot;
	}

    public static class HyperNode extends AbstractDesignerTreeNode {

        private KrnObject ifcObj;
        private long parentId = 0;
        private boolean isModified = false;
        private boolean isAdded = false;
        private long runtimeIndex = 0;
        private boolean isDialog;
        private boolean isChangeable;
        private HashMap<Long, String> titleMap = new HashMap<Long, String>();

        public HyperNode(KrnObject uiObj, KrnObject ifcObj) {
            this.krnObj = uiObj;
            this.ifcObj = ifcObj;

            isLoaded = false;
        }

        public boolean isLeaf() {
            return krnObj.classId == uiCls.id;
        }

        public KrnObject getIfcObject() {
            return ifcObj;
        }

        protected void load() {}

        public long getRuntimeIndex() {
            return runtimeIndex;
        }

        public void setRuntimeIndex(int runtimeIndex) {
            this.runtimeIndex = runtimeIndex;
        }

        public boolean isModified() {
            return isModified;
        }

        public void setModified(boolean modified) {
            isModified = modified;
        }

        public void setIfcObject(KrnObject obj) {
            ifcObj = obj;
        }

        public boolean isAdded() {
            return isAdded;
        }

        public void setAdded(boolean added) {
            isAdded = added;
/*
        if (!isLeaf()) {
            for (int i = 0; i < getChildCount(); i++) {
                HyperNode child = (HyperNode)getChildAt(i);
                child.setAdded(isAdded);
            }
        }
*/
        }

        public String toString(Long langId, WebSession s) {
            String title = (String) titleMap.get(langId);
            if (title == null) {
                long rid = LangHelper.getRusLang(s.getConfigNumber()).obj.id;
                title = (String) titleMap.get(rid);
            }
            if (title == null) title = "*";
            return title;
        }

        public String toString(WebSession s) {
            long rid = LangHelper.getRusLang(s.getConfigNumber()).obj.id;
            title = (String) titleMap.get(rid);
            if (title == null) title = "*";
            return title;
        }

        public void setTitle(String title, long langId) {
            titleMap.put(langId, title);
        }

        public String getTitle(long langId, WebSession s) {
            String title = (String) titleMap.get(langId);
            if (title == null) {
                long rid = LangHelper.getRusLang(s.getConfigNumber()).obj.id;
                title = (String) titleMap.get(rid);
            }
            if (title == null) title = "*";
            return title;
        }
        
        public String getTitle(WebSession s) {
            long rid = LangHelper.getRusLang(s.getConfigNumber()).obj.id;
            title = (String) titleMap.get(rid);
            if (title == null) title = "*";
            return title;
        }
        
        public List<HyperNode> getChildren(final long langId, final WebSession s) {
        	List<HyperNode> sortedNodes = nodesByParentId.get(krnObj.id);
        	if (sortedNodes != null) {
	        	Collections.sort(sortedNodes, new Comparator<HyperNode>() {
	        	        public int compare(HyperNode o1, HyperNode o2) {
	        	        	if (o1 != null && o2 != null) {
	        	        		Long runtimeIndex1 = o1.getRuntimeIndex();
	        	        		Long runtimeIndex2 = o2.getRuntimeIndex();
	        	        		int res = runtimeIndex1.compareTo(runtimeIndex2);
	        	        		if (res == 0) {
	        	                    String title1 = o1.getTitle(langId, s);
	        	                    String title2 = o2.getTitle(langId, s);
	        	                    res = title1.compareTo(title2);
	        	        		}
	        	        		return res;
							}
	        	        	else {
								return 0;
							}
	        	        }
	        	});
        	}
           	return sortedNodes;
        }

        public int getChildCount() {
        	List<HyperNode> children = nodesByParentId.get(krnObj.id);
            if (children == null) return 0;
            return children.size();
        }

        public boolean isDialog() {
            return isDialog;
        }

        public void setDialog(boolean dialog) {
            isDialog = dialog;
        }

        public boolean isChangeable() {
            return isChangeable;
        }

        public void setChangeable(boolean value) {
            isChangeable = value;
        }

		public void setParentId(long pid) {
			this.parentId = pid;
		}
		
    }

    public void changeState(TreePath path) {
        setExpandedState(path, !isExpanded(path));
    }

    private boolean isExpanded(TreePath path) {
        if (path != null) {
            Boolean b = expStates.get(path.toString());
            if (b != null)
                return b;
        }
        return false;
    }

    private void setExpandedState(TreePath path, boolean b) {
        if (path != null) expStates.put(path.toString(), b);
    }

    public boolean isReadOnly(Long id, Integer sid) {
        boolean isEditable = true;
        if (rwItems != null)
            isEditable = rwItems.contains(id);

        return !isEditable;
    }

    public JsonArray getArchiveNodeJSON(WebSession s, HyperNode p, long lid) {
    	JsonArray arr = new JsonArray();
    	List<HyperNode> children = p.getChildren(lid, s);
        if (children != null) {
            Set<Long> readOnlyItems = roItems;
            Set<Long> readWriteItems = rwItems;
            for (HyperNode child : children) {
                boolean hasChildren = child.getChildren(lid, s) != null && child.getChildren(lid, s).size() > 0;
                long id = child.getKrnObj().id;
                if ((readOnlyItems == null && readWriteItems == null) || readWriteItems.contains(id) || readOnlyItems.contains(id)) {
                	JsonObject res = new JsonObject();
                    res.add("uid", child.getKrnObj().uid);
                    res.add("title", child.toString(lid, s));
                    if (hasChildren) {
                    	JsonArray childArray = getArchiveNodeJSON(s, child, lid);
                    	if (childArray.size() > 0) {
                    		res.add("children", childArray);
                    	}
                    }
                    arr.add(res);
                }
            }
        }
        return arr;
    }
    
    public JsonObject searchArchiveByName(WebSession s, HyperNode p, long lid, String text, String index) {
    	JsonObject obj = new JsonObject();
    	if("0".equals(index)) {
    		JsonArray listDicts = searchArchives(s, p, lid, text, "");
    		listArchsSave = new JsonArray();
    		listArchsSave = listDicts;
    		if(!listDicts.isEmpty()) {
    			obj = (JsonObject)listDicts.get(0);
    		}
    	} else {
    		if(!listArchsSave.isEmpty()) {
    			int idx = Integer.parseInt(index) % listArchsSave.size();
    			obj = (JsonObject)listArchsSave.get(idx);

    		}
    	}
    	return obj;
    }
    
    public JsonArray searchArchives(WebSession s, HyperNode p, long lid, String text, String parentUids) {
    	JsonArray arr = new JsonArray();
		parentUids += ',' + p.getKrnObj().uid;
    	List<HyperNode> children = p.getChildren(lid, s);
        if (children != null) {
        	Set<Long> readOnlyItems = roItems;
        	Set<Long> readWriteItems = rwItems;
        	for (HyperNode child : children) {
        		long id = child.getKrnObj().id;
        		if ((readOnlyItems == null && readWriteItems == null) || readWriteItems.contains(id) || readOnlyItems.contains(id)) {
        			if(child.isLeaf()) {
        				String ss = child.toString(lid, s); 
        				if (child.toString(lid, s).toUpperCase(Constants.OK).contains(text.toUpperCase(Constants.OK))) {
        					JsonObject obj = new JsonObject();
        					obj.add("childUid", child.getKrnObj().uid);	
        					obj.add("parentNodeUids", parentUids);
        					arr.add(obj);
        				}
        			} else {
        				boolean hasChildren = child.getChildren(lid, s) != null && child.getChildren(lid, s).size() > 0;
        				if (hasChildren) {
        					JsonArray subArr = searchArchives(s, child, lid, text, parentUids);
        					for (int i = 0; i < subArr.size(); i++) {
        						arr.add(subArr.get(i));
        					}
        				}
        			}
        		}
        	}
        }
        return arr;
    }
    
    public String getArchiveFolderJSON(WebSession s, String parentId, boolean loadLeafs, HyperNode root, long lid) {
    	JsonArray arr = new JsonArray();

    	if (parentId != null && parentId.length() > 0) {
    		HyperNode p = nodesByUid.get(parentId);
        	List<HyperNode> children = p.getChildren(lid, s);
            if (children != null) {
                Set<Long> readOnlyItems = roItems;
                Set<Long> readWriteItems = rwItems;

                for (HyperNode child : children) {
                    long id = child.getKrnObj().id;

                    if ((readOnlyItems == null && readWriteItems == null)
                            || readWriteItems.contains(id)
                            || readOnlyItems.contains(id)) {
                    	
        	            if (!loadLeafs && !child.isLeaf()) {
	    		            JsonObject row = new JsonObject();
	    		            row.add("id", child.getKrnObj().uid);
	    		        	row.add("text", child.toString(lid, s));
	    		        	boolean hasFolders = hasNonEmptyFolder(child);
	    		        	row.add("state", hasFolders ? "closed" : "open");
	    		        	if (!hasFolders) {
		    		        	row.add("iconCls", "tree-folder");
	    		        	}
	    		        	row.add("parent", parentId);
	    		       		arr.add(row);
        	            } else if (loadLeafs && child.isLeaf()) {
        		            JsonObject row = new JsonObject();
	    		            row.add("id", child.getKrnObj().uid);
	    		        	row.add("title", child.toString(lid, s));
        		        	row.add("state", "open");
        		        	row.add("parent", parentId);
        		       		arr.add(row);
        	            }
                    }
                }
            }
    	} else {
        	List<HyperNode> children = root.getChildren(lid, s);
            if (children != null) {
                Set<Long> readOnlyItems = roItems;
                Set<Long> readWriteItems = rwItems;

                for (HyperNode child : children) {
                    long id = child.getKrnObj().id;

                    if ((readOnlyItems == null && readWriteItems == null)
                            || readWriteItems.contains(id)
                            || readOnlyItems.contains(id)) {
                    	
        	            if (!loadLeafs && !child.isLeaf()) {
	    		            JsonObject row = new JsonObject();
	    		            row.add("id", child.getKrnObj().uid);
	    		        	row.add("text", child.toString(lid, s));
	    		        	boolean hasFolders = hasNonEmptyFolder(child);
	    		        	row.add("state", hasFolders ? "closed" : "open");
	    		        	if (!hasFolders) {
		    		        	row.add("iconCls", "tree-folder");
	    		        	}
	    		        	row.add("parent", parentId);
	    		       		arr.add(row);
        	            } else if (loadLeafs && child.isLeaf()) {
        		            JsonObject row = new JsonObject();
	    		            row.add("id", child.getKrnObj().uid);
	    		        	row.add("title", child.toString(lid, s));
        		        	row.add("state", "open");
        		        	row.add("parent", parentId);
        		       		arr.add(row);
        	            }
                    }
                }
            }
    	}
        
        return arr.toString();
    }
    
    private boolean hasNonEmptyFolder(HyperNode p) {
    	List<HyperNode> children = nodesByParentId.get(p.getKrnObj().id);
        if (children != null) {
            for (HyperNode child : children) {
                if (!child.isLeaf() && containsLeaf(child)) {
                	return true;
                }
            }
        }
        return false;
    }
    
    private boolean containsLeaf(HyperNode node) {
    	long id = node.getKrnObj().id;
    	
    	if (node.isLeaf() && ((roItems == null && rwItems == null)
                				|| roItems.contains(id)
                				|| rwItems.contains(id)))
			return true;
    	
    	List<HyperNode> children = nodesByParentId.get(id);
    	if (children != null) {
    		for (HyperNode child : children) {
    			if (containsLeaf(child)) return true;
    		}
    	}
		return false;
    }
    
    public static void addNode(String uid, HyperNode n) {
    	nodesByUid.put(uid, n);
    }
    
    protected static void addNode(long parentId, HyperNode n) {
    	nodesByUid.put(n.getKrnObj().uid, n);
    	
    	if (parentId > 0) {
	    	List<HyperNode> chs = nodesByParentId.get(parentId);
	    	if (chs == null) {
	    		chs = new ArrayList<HyperNode>();
	    		nodesByParentId.put(parentId, chs);
	    	}
	    	chs.remove(n);
	    	chs.add(n);
    	}
    }
    
    protected static void load(WebSession s, long[] ids) {
        final Kernel krn = s.getKernel();

        try {
            // дети
            ObjectValue[] ovs = krn.getObjectValues(ids, uiFolderCls.id, "hipers", 0);
            
            if (ovs.length > 0) {
            	Map<Long, Long> parentByChild = new HashMap<Long, Long>();
	            long[] childIds = new long[ovs.length];
	            for (int i = 0; i < ovs.length; i++) {
	            	childIds[i] = ovs[i].value.id;
	            	parentByChild.put(childIds[i], ovs[i].objectId);
	            }
	
	            List<WebLangItem> langs = LangHelper.getAll(s.getConfigNumber());
	            
	            AttrRequestBuilder arb = new AttrRequestBuilder(uiCls, krn).add("hiperObj");
	            
	            for (WebLangItem li : langs) {
	            	arb.add("title", li.obj.id);
	            }
	            
	            arb.add("runtimeIndex").add("isDialog").add("isChangeable");
	
	            List<Object[]> recs = krn.getObjects(childIds, arb.build(), 0);
                    
                for (Object[] rec : recs) {
                	KrnObject obj = (KrnObject)rec[0];

                	KrnObject ifcObj = (KrnObject)rec[2];
                    HyperNode n = new HyperNode(obj, ifcObj);
                    n.setParentId(parentByChild.get(obj.id));

                    int i = 3;
    	            for (WebLangItem li : langs) {
                    	String title = (rec[i] instanceof String) ? (String) rec[i] : "Безымянный";
                        n.setTitle(title, li.obj.id);
                        i++;
    	            }
                	
                    n.setRuntimeIndex(arb.getIntValue("runtimeIndex", rec));
                    n.setDialog(arb.getBooleanValue("isDialog", rec));
                    n.setChangeable(arb.getBooleanValue("isChangeable", rec));
                    
                    addNode(parentByChild.get(obj.id), n);

                }
                for (int i = 0; i < ovs.length; i++) {
                	List<HyperNode> l = nodesByParentId.get(ovs[i].objectId);
                	if (l != null) {
		                Collections.sort(l, new Comparator<HyperNode>() {
		                    public int compare(HyperNode o1, HyperNode o2) {
		                        if (o1 != null && o2 != null) {
		                            Long i1 = o1.getRuntimeIndex();
		                            Long i2 = o2.getRuntimeIndex();
		                            return i1.compareTo(i2);
		                        }
		                        return 0;
		                    }
		                });
                	}
                }
                load(s, childIds);
            }
        } catch (KrnException e) {
        	logReload = WebSessionManager.getLog(s.getKernel().getUserSession().dsName, s.getKernel().getUserSession().logName);
        	logReload.error(e, e);
        }
    }

    protected static void reload(Session s, long id, HyperNode n) {
        try {
            List<KrnObject> langs = s.getSystemLangs();
            
            kz.tamur.or3ee.server.kit.AttrRequestBuilder arb = new kz.tamur.or3ee.server.kit.AttrRequestBuilder(uiCls, s).add("hiperObj").add("parent");
            
            for (KrnObject li : langs) {
            	arb.add("title", li.id);
            }
            
            arb.add("runtimeIndex").add("isDialog").add("isChangeable");

            QueryResult qr = s.getObjects(new long[] {id}, arb.build(), 0);
        	
            for (Object[] rec : qr.rows) {
            	KrnObject obj = arb.getObject(rec);
            	KrnObject ifcObj=(KrnObject)rec[2];
            	KrnObject pObj = (KrnObject)rec[3];
            	// Если создается новое гиперменю, то его нет в мапах, нужно его добавить
            	if(n == null){
            		n = new HyperNode(obj, ifcObj);
            		addNode(pObj != null ? pObj.id : 0, n);
            	}
            	n.ifcObj = ifcObj;
                n.setParentId(pObj != null ? pObj.id : 0);
                
            	int i = 4;
	            for (KrnObject li : langs) {
                	String title = (rec[i] instanceof String) ? (String) rec[i] : "Безымянный";
                    n.setTitle(title, li.id);
                    i++;
	            }
            	
                n.setRuntimeIndex(arb.getIntValue("runtimeIndex", rec));
                n.setDialog(arb.getBooleanValue("isDialog", rec));
                n.setChangeable(arb.getBooleanValue("isChangeable", rec));
            }
        } catch (KrnException e) {
        	logReload = WebSessionManager.getLog(s.getUserSession().getDsName(), s.getUserSession().getLogUserName());
        	logReload.error(e, e);
        }
    }
    
    private static class ArchiveListener implements AttrChangeListener {
    	private static final Log log = LogFactory.getLog("ArchiveListener" + (UserSession.SERVER_ID != null ? ("." + UserSession.SERVER_ID) : ""));
    	private String dsName;
    	
    	public ArchiveListener(String dsName) {
    		this.dsName = dsName;
    	}
    	
	    @Override
		public void attrChanged(KrnObject obj, long attrId, long langId, long trId, UUID uuid) {
			List<AttrChange> l = attrChanges.get(uuid);
			if (l == null) {
				l = new ArrayList<AttrChange>();
				attrChanges.put(uuid, l);
			}
			
			l.add(new AttrChange(obj, attrId, langId, trId));
		}
	
		@Override
		public void commit(UUID uuid) {
			if (uuid != null) {
				List<AttrChange> l = attrChanges.remove(uuid);
				if (l != null && l.size() > 0) {
					log.info("COMMIT IFC " + uuid + "; SIZE = " + l.size());
			        try {
				        Session s = SrvUtils.getSession(dsName, "sys", null);
				        try {
							for (AttrChange ch : l) {
								processAttrChanged(ch.obj, ch.attrId, ch.langId, ch.trId, s);
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
			}
		}
	
		@Override
		public void rollback(UUID uuid) {
			if (uuid != null) {
				List<AttrChange> l = attrChanges.remove(uuid);
				if (l != null && l.size() > 0) {
			        log.info("ROLBACK USER SESSION " + uuid + "; SIZE = " + l.size());
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

		public void processAttrChanged(KrnObject obj, long attrId, long langId, long trId, Session s) {
			if (attrId == 0 || attrId == 1) {
				HyperNode n = nodesByUid.get(obj.uid);
				if (n == null)
					reload(s, obj.id, null);
			} if (attrId == 2) {
				nodesByUid.remove(obj.uid);
				nodesByParentId.remove(obj.id);
				for (List<HyperNode> l : nodesByParentId.values()) {
					l.remove(new HyperNode(obj, null));
				}
			} else if (attrId == attrParent.id) {
				HyperNode n = nodesByUid.get(obj.uid);
				List<HyperNode> l = nodesByParentId.get(n.parentId);
				if (l != null) l.remove(n);
				reload(s, obj.id, n);
				addNode(n.parentId, n);
			} else if (attrId == attrHyper.id || attrId == attrTitle.id || attrId == attrIndex.id || attrId == attrDialog.id || attrId == attrChangeable.id) {
				HyperNode n = nodesByUid.get(obj.uid);
				reload(s, obj.id, n);
			}
		}
    }
    
	private static synchronized void initListener(WebSession s) throws KrnException {
		if (!listenerInitialized) {
			listener = new ArchiveListener(s.getKernel().getBaseName());
	        uiCls = s.getKernel().getClassByName("HiperTree");
	        uiFolderCls = s.getKernel().getClassByName("HiperFolder");

			attrParent = s.getKernel().getAttributeByName(uiCls, "parent");
			if (attrParent == null)
				attrParent = s.getKernel().getAttributeByName(uiFolderCls, "parent");
				
			attrHyper = s.getKernel().getAttributeByName(uiCls, "hiperObj");
			attrTitle = s.getKernel().getAttributeByName(uiCls, "title");
			attrIndex = s.getKernel().getAttributeByName(uiCls, "runtimeIndex");
			attrDialog = s.getKernel().getAttributeByName(uiCls, "isDialog");
			attrChangeable = s.getKernel().getAttributeByName(uiCls, "isChangeable");

			Driver2.addAttrChangeListener(uiCls.id, listener);
			Driver2.addAttrChangeListener(uiFolderCls.id, listener);

			listenerInitialized = true;
		}
	}
}
