package kz.tamur.rt.adapters;

import com.cifs.or2.client.ClassNode;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.PropDlg;
import com.cifs.or2.client.ReadFilterDatesPanel;
import com.cifs.or2.client.Utils;
import com.cifs.or2.kernel.*;

import kz.tamur.comps.Constants;
import kz.tamur.comps.Filter;
import kz.tamur.comps.Mode;
import kz.tamur.comps.OrFrame;
import kz.tamur.lang.parser.*;
import kz.tamur.ods.AttrRequest;
import kz.tamur.or3.util.PathElement;
import kz.tamur.or3.util.PathElement2;
import kz.tamur.rt.CheckContext;
import kz.tamur.rt.DummyCheckContext;
import kz.tamur.rt.data.*;
import static kz.tamur.rt.data.RecordStatus.DELETED;
import static kz.tamur.rt.data.RecordStatus.TMPDELETED;
import kz.tamur.rt.orlang.ClientOrLang;
import static kz.tamur.util.CollectionTypes.*;
import kz.tamur.util.Funcs;
import kz.tamur.util.Pair;
import kz.tamur.util.ReqMsgsList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import javax.swing.event.EventListenerList;

import java.util.*;

public class OrRef implements OrRefListener, CashChangeListener,
		RecordsReceiver {

	private static Log log = LogFactory.getLog(OrRef.class);
	private static final int FETCH_DEPTH =
			Integer.parseInt(System.getProperty("ref.fetch.depth", "0"));

	public static final int TR_CLEAR = 0;
	public static final int TR_DIRTY = 1;

	private static final Kernel krn_ = Kernel.instance();

	private TableAdapter tableAdapter;

	private KrnClass cls_;
	protected KrnClass type_;
	private KrnAttribute attr_;
	private KrnAttribute childrenAttr_;
	private KrnAttribute hasChildrenAttr_;
	private KrnAttribute deletedAttr_;
	private ASTStart childrenExpr_;

	private Object attrIndex_;

	public Values values = new Values();
	private List<Integer> deletedIndexes = new ArrayList<Integer>();

	private Item rootItem; // для древовидного OrRef

	public int index;

	public boolean isColumn;

	private OrRef parent_;

	private List<OrRef> children_;

	private String title_;

	private EventListenerList listeners_ = new EventListenerList();

	private int refreshMode_;

	private boolean isLoaded_ = false;

	private KrnObject[] objs_;

	private Set<Long> filterIds_ = new TreeSet<Long>();

	private List<FilterDate> filterDates_ = new ArrayList<FilterDate>();

	private long defaultFilterId_;

	private int[] selectedInds_;

	private Boolean isInOrTable_ = null;

	private int dirtyTransactions = 0;

	private int savedIndex;
	private int[] savedIndexes;

	private Values savedValues;

	protected OrFrame frm;

	protected boolean isCleared = false;

	private boolean isChecking = false;

	private List<CheckContext> checkContextList = new ArrayList<CheckContext>();

	private long langId_;

	protected boolean allowsLangs = false;

	private boolean hasFilters = false;

	private int limit = 0;

	private boolean limitExceeded = false;

	private JProgressBar progress;

	private boolean isHyperPopup = false;
	private boolean exitPrevFire = false;
	/** Список выбранных объектов в данном REF*/
        private List<Item> selItems;
        
	public static OrRef createRef(KrnObject[] objects, String path,
			boolean isColumn, int mode, int dirtyTransactions, OrFrame frm,
			Map<String, OrRef> refs) throws KrnException {
		OrRef ref = null;

		Map<String, OrRef> refsMap = (refs != null) ? refs : frm.getRefs();
		if (mode == Mode.RUNTIME && path != null)
			ref = getRef(refsMap, Utils.normalizePath(path));

		if (ref == null) {
			ref = new OrRef(path, isColumn, mode, false, 1, dirtyTransactions,
				frm, refsMap);
		}

		if (mode == Mode.RUNTIME) {
			for (int i = 0; i < objects.length; ++i) {
				ref.values.add(0, ref.new Item(objects[i]));
			}
		}

		if (isColumn && ref.attr_ != null
				&& ref.attr_.collectionType == COLLECTION_NONE)
			ref.setColumn(true);

		return ref;
	}

	protected static void findPaths(Node t, List<String> refPaths,
			List<String> attrPaths) {

		if (t instanceof ASTAccess) {
			Node node = t.jjtGetChild(0);
			if (node instanceof ASTVar) {
				if (((ASTVar) node).getName().equals("Interface")) {
					node = t.jjtGetChild(1);
					if (node instanceof ASTMethod) {
						ASTMethod m = (ASTMethod) node;
						if (m.getName().equals("getAttr")) {
							node = m.jjtGetChild(0);
							if (node instanceof ASTString) {
								refPaths.add(((ASTString) node).getText());
							}
						}
					}
				} else if (attrPaths != null) {
					node = t.jjtGetChild(1);
					if (node instanceof ASTMethod) {
						ASTMethod m = (ASTMethod) node;
						if (m.getName().equals("getAttr")) {
							node = m.jjtGetChild(0);
							if (node instanceof ASTString) {
								attrPaths.add(((ASTString) node).getText());
							}
						}
					}
				}
			}
		}
		for (int i = 0; i < t.jjtGetNumChildren(); i++) {
			findPaths(t.jjtGetChild(i), refPaths, attrPaths);
		}
	}

	public static OrRef createContentRef(String path, int refreshMode,
			int mode, int dirtyTransactions, OrFrame frm) throws KrnException {
		OrRef ref = null;

		if (mode == Mode.RUNTIME)
			ref = getRef(frm.getContentRef(), path);

		if (ref == null) {
			ref = new OrRef(path, true, mode, true, refreshMode,
				dirtyTransactions, frm, frm.getContentRef());
		}

		ref.refreshMode_ = refreshMode;

		return ref;
	}

	public static OrRef createContentRef(String path, OrRef parentRef, int refreshMode,
			int mode, int dirtyTransactions, OrFrame frm) throws KrnException {
		OrRef ref = null;

		if (mode == Mode.RUNTIME)
			ref = getRef(frm.getContentRef(), path);

		if (ref == null) {
			ref = new OrRef(path, parentRef, true, mode, true, refreshMode,
				dirtyTransactions, frm, frm.getContentRef());
		}

		ref.refreshMode_ = refreshMode;

		return ref;
	}

	public static OrRef createContentRef(String path, long filterId, int refreshMode,
            int mode, int dirtyTransactions, OrFrame frm) throws KrnException {
        OrRef ref = null;

        if (mode == Mode.RUNTIME)
            ref = getRef(frm.getContentRef(), path, filterId);

        if (ref == null) {
            ref = new OrRef(path, filterId, true, mode, true, refreshMode,
                dirtyTransactions, frm, frm.getContentRef());
        }

        ref.refreshMode_ = refreshMode;

        return ref;
    }

	public static OrRef createContentRef(String path, long filterId,
			int refreshMode, int mode, int dirtyTransactions,
			boolean isHyperPopup, OrFrame frm) throws KrnException {
		OrRef ref = null;

		if (mode == Mode.RUNTIME)
			ref = getRef(frm.getContentRef(), (filterId > 0) ? path + filterId
					: path);

		if (ref == null) {
			ref = new OrRef(path, filterId, true, mode, true, refreshMode,
				dirtyTransactions, isHyperPopup, frm, frm.getContentRef());
		}

		ref.refreshMode_ = refreshMode;

		return ref;
	}

	public int getRefreshMode() {
		return refreshMode_;
	}

	public boolean isColumn() {
		return isColumn;
	}

	public boolean isHyperPopup() {
		return isHyperPopup;
	}

	public List<Item> getSelectedItems() {
	    if (selItems != null) {
            return selItems;
        }
		List<Item> res = new ArrayList<Item>();

		if ((selectedInds_ == null || selectedInds_.length <= 1) && index > -1)
			selectedInds_ = new int[] { index };
		
		boolean contains = false;
		for (int i = 0; !contains && selectedInds_ != null
				&& i < selectedInds_.length; i++) {
			int ind = selectedInds_[i];
			if (ind == index) {
				contains = true;
			}
		}
		int cnt = values.size(0);
		if (!contains && selectedInds_ != null && selectedInds_.length > 0) {
			if (index > -1 && index < cnt) {
				res.add(values.get(0, index));
			}
		}

		for (int i = 0; selectedInds_ != null && i < selectedInds_.length; i++) {
			int ind = selectedInds_[i];
			if (ind < cnt) {
				res.add(values.get(0, ind));
			}
		}
		return res;
	}

	public void setSelectedItems(int[] inds) {
		selectedInds_ = inds;
	}

	public void setSelectedItems(KrnObject[] objs) {
		Set<Long> objIds = new HashSet<Long>();
		for (int i = 0; i < objs.length; ++i)
			objIds.add(objs[i].id);
		List<Integer> selInds = new LinkedList<Integer>();
		for (int i = 0; i < values.size(0); ++i) {
			KrnObject curr = (KrnObject) values.get(0, i).getCurrent();
			if (curr != null && objIds.contains(curr.id))
				selInds.add(i);
		}
		selectedInds_ = new int[selInds.size()];
		for (int i = 0; i < selectedInds_.length; ++i)
			selectedInds_[i] = selInds.get(i);
	}

	public boolean isArray() {
		return (attr_ == null
				|| attr_.collectionType == COLLECTION_ARRAY
				|| attr_.collectionType == COLLECTION_SET
				|| childrenAttr_ != null);
	}

	public static OrRef createRef(String path, boolean isColumn, int mode,
			Map<String, OrRef> refs, int dirtyTransactions, OrFrame frm)
			throws KrnException {
		return createRef(new KrnObject[0], path, isColumn, mode,
			dirtyTransactions, frm, refs);
	}

	private static OrRef getRef(Map<String, OrRef> mlRefs, String path) {
		return mlRefs.get(path);
	}

    private static OrRef getRef(Map<String, OrRef> mlRefs, String path, long filterId) {
        return mlRefs.get(path + filterId);
    }

	protected OrRef() {
	}

	private OrRef(String path, long filterId, boolean isColumn, int mode,
			boolean isContent, int refreshMode, int dirtyTransactions,
			boolean isHyperPopup, OrFrame frm, Map<String, OrRef> refs)
			throws KrnException {
		this.isHyperPopup = isHyperPopup;
		this.frm = frm;
		index = 0;
		setColumn(isColumn);
		title_ = path;
		refreshMode_ = refreshMode;
		this.dirtyTransactions = dirtyTransactions;

		if (mode == Mode.RUNTIME && path != null) {
			StringTokenizer st = new StringTokenizer(path, ".");
			int count = st.countTokens();
			if (count > 0) {
				String parentPath = st.nextToken();
				if (count > 1 && mode == Mode.RUNTIME) {
					OrRef parent = getRef(refs, parentPath);
					if (parent == null) {
						parent = new OrRef(parentPath, false, mode, isContent,
							refreshMode, dirtyTransactions, frm, refs);
					} else if (dirtyTransactions == TR_CLEAR)
						parent.setDirtyTransactions(dirtyTransactions);
					setParent(parent);
				} else {
					// Корневой узел не может быть isColumn
					setColumn(false);
				}
				ClassNode cnode = krn_.getClassNodeByName(parentPath);
				cls_ = cnode.getKrnClass();
				for (int i = 0; i < count - 1; ++i) {
					String str = st.nextToken();
					PathElement pe = Funcs.parseAttrName(str);
					attr_ = cnode.getAttribute(pe.name);
					attrIndex_ = pe.index;
					if (attr_ == null) {
						/*
						 * System.out.println("ERROR: Attribute '" + aname + "'
						 * not found"); System.out.println(" in path '" + path +
						 * "'");
						 */
						throw new RuntimeException("ERROR: Атрибут '" + pe.name
								+ "' не найден!\n" + "Путь '" + path + "'.\n");
					}
					if (pe.castClassName != null) {
						type_ = krn_.getClassByName(pe.castClassName);
					} else {
						type_ = krn_.getClassNode(attr_.typeClassId).getKrnClass();
					}
					cnode = krn_.getClassNode(type_.id);
					cls_ = cnode.getKrnClass();
					parentPath += "." + str;
					if (i < count - 2 && mode == Mode.RUNTIME) {
						OrRef parent = getRef(refs, parentPath);
						if (parent == null) {
							boolean b = isContent
									|| (isColumn && attr_.collectionType == COLLECTION_NONE);
							parent = new OrRef(parentPath, b, mode, isContent,
								refreshMode, dirtyTransactions, frm, refs);
						} else if (dirtyTransactions == TR_CLEAR)
							parent.setDirtyTransactions(dirtyTransactions);
						setParent(parent);
					}
				}
				if (type_ == null) {
					type_ = cls_;
				}
				refs.put((filterId > 0) ? path + filterId : path, this);
				if (attr_ != null) {
					getCash().addCashChangeListener(attr_.id, this);
					if (attr_.isMultilingual) {
						allowsLangs = true;
					}
				}
			}
			if (!allowsLangs) {
				values.addLanguage(0);
			}
		} else
			pathChanged(new OrRefEvent(this, -1, -1, this));
	}

	private OrRef(String path, boolean isColumn, int mode, boolean isContent,
			int refreshMode, int dirtyTransactions, OrFrame frm,
			Map<String, OrRef> refs) throws KrnException {
		this.frm = frm;
		index = 0;
		setColumn(isColumn);
		title_ = path;
		refreshMode_ = refreshMode;
		this.dirtyTransactions = dirtyTransactions;

		if (mode == Mode.RUNTIME && path != null) {
			PathElement2[] ps = Utils.parsePath2(path);
			int count = ps.length;
			if (count > 0) {
				String parentPath = ps[0].toString();
				if (count > 1 && mode == Mode.RUNTIME) {
					OrRef parent = getRef(refs, Utils.normalizePath(parentPath));
					if (parent == null) {
						parent = new OrRef(parentPath, false, mode, isContent,
							refreshMode, dirtyTransactions, frm, refs);
					} else if (dirtyTransactions == TR_CLEAR)
						parent.setDirtyTransactions(dirtyTransactions);
					setParent(parent);
				}
				//ClassNode cnode = krn_.getClassNodeByName(parentPath);
				cls_ = ps[0].type;
				for (int i = 1; i < count; ++i) {
					PathElement2 pe = ps[i];
					attr_ = pe.attr;
					attrIndex_ = pe.index;
					type_ = pe.type;
					//cnode = krn_.getClassNode(type_.id);
					parentPath += "." + pe.toString();
					if (i < count - 1) {
						OrRef parent = getRef(refs, Utils.normalizePath(parentPath));
						if (parent == null) {
							boolean b = isContent
									|| (isColumn && attr_.collectionType == COLLECTION_NONE);
							parent = new OrRef(parentPath, b, mode, isContent,
								refreshMode, dirtyTransactions, frm, refs);
						} else if (dirtyTransactions == TR_CLEAR)
							parent.setDirtyTransactions(dirtyTransactions);
						setParent(parent);
					}
				}
				if (type_ == null) {
					type_ = cls_;
				}
				refs.put(Utils.normalizePath(path), this);
				if (attr_ != null) {
					getCash().addCashChangeListener(attr_.id, this);
					if (attr_.typeClassId < 99 && attr_.isMultilingual) {
						allowsLangs = true;
					}
				}
			}
			if (!allowsLangs) {
				values.addLanguage(0);
			}
		} else
			pathChanged(new OrRefEvent(this, -1, -1, this));
	}

	private OrRef(String path, OrRef parentRef, boolean isColumn, int mode, boolean isContent,
			int refreshMode, int dirtyTransactions, OrFrame frm,
			Map<String, OrRef> refs) throws KrnException {
		this.frm = frm;
		index = 0;
		setColumn(isColumn);
		title_ = path;
		refreshMode_ = refreshMode;
		this.dirtyTransactions = dirtyTransactions;

		if (mode == Mode.RUNTIME && path != null) {
			PathElement2[] ps = Utils.parsePath2(path);
			int count = ps.length;
			if (count > 0) {
				String parentPath = ps[0].toString();
				if (count > 1 && mode == Mode.RUNTIME) {
					OrRef parent = (count > 2 || parentRef == null)
									? getRef(refs, Utils.normalizePath(parentPath))
									: parentRef;
					if (parent == null) {
						parent = new OrRef(parentPath, false, mode, isContent,
							refreshMode, dirtyTransactions, frm, refs);
					} else if (dirtyTransactions == TR_CLEAR)
						parent.setDirtyTransactions(dirtyTransactions);
					setParent(parent);
				}
				//ClassNode cnode = krn_.getClassNodeByName(parentPath);
				cls_ = ps[0].type;
				for (int i = 1; i < count; ++i) {
					PathElement2 pe = ps[i];
					attr_ = pe.attr;
					attrIndex_ = pe.index;
					type_ = pe.type;
					//cnode = krn_.getClassNode(type_.id);
					parentPath += "." + pe.toString();
					if (i < count - 1) {
						OrRef parent = (i < count - 2 || parentRef == null)
										? getRef(refs, Utils.normalizePath(parentPath))
										: parentRef;
						if (parent == null) {
							boolean b = isContent
									|| (isColumn && attr_.collectionType == COLLECTION_NONE);
							parent = new OrRef(parentPath, b, mode, isContent,
								refreshMode, dirtyTransactions, frm, refs);
						} else if (dirtyTransactions == TR_CLEAR)
							parent.setDirtyTransactions(dirtyTransactions);
						setParent(parent);
					}
				}
				if (type_ == null) {
					type_ = cls_;
				}
				refs.put(Utils.normalizePath(path), this);
				if (attr_ != null) {
					getCash().addCashChangeListener(attr_.id, this);
					if (attr_.typeClassId < 99 && attr_.isMultilingual) {
						allowsLangs = true;
					}
				}
			}
			if (!allowsLangs) {
				values.addLanguage(0);
			}
		} else
			pathChanged(new OrRefEvent(this, -1, -1, this));
	}

	private OrRef(String path, long filterId, boolean isColumn, int mode, boolean isContent,
            int refreshMode, int dirtyTransactions, OrFrame frm,
            Map<String, OrRef> refs) throws KrnException {
        this.frm = frm;
        index = 0;
        setColumn(isColumn);
        title_ = path;
        refreshMode_ = refreshMode;
        this.dirtyTransactions = dirtyTransactions;

        if (mode == Mode.RUNTIME && path != null) {
            StringTokenizer st = new StringTokenizer(path, ".");
            int count = st.countTokens();
            if (count > 0) {
                String parentPath = st.nextToken();
                if (count > 1 && mode == Mode.RUNTIME) {
                    OrRef parent = getRef(refs, parentPath, filterId);
                    if (parent == null) {
                        parent = new OrRef(parentPath, filterId, false, mode, isContent,
                            refreshMode, dirtyTransactions, frm, refs);
                    } else if (dirtyTransactions == TR_CLEAR)
                        parent.setDirtyTransactions(dirtyTransactions);
                    setParent(parent);
                }
                ClassNode cnode = krn_.getClassNodeByName(parentPath);
                cls_ = cnode.getKrnClass();
                for (int i = 0; i < count - 1; ++i) {
                    String str = st.nextToken();
                    PathElement pe = Funcs.parseAttrName(str);
                    attr_ = cnode.getAttribute(pe.name);
                    attrIndex_ = pe.index;
                    if (attr_ == null) {
                        /*
                         * System.out.println("ERROR: Attribute '" + aname + "'
                         * not found"); System.out.println(" in path '" + path +
                         * "'");
                         */
                        throw new RuntimeException("ERROR: Атрибут '" + pe.name
                                + "' не найден!\n" + "Путь '" + path + "'.\n");
                    }
                    if (pe.castClassName != null) {
                        type_ = krn_.getClassByName(pe.castClassName);
                    } else {
                        type_ = krn_.getClassNode(attr_.typeClassId).getKrnClass();
                    }
                    cnode = krn_.getClassNode(type_.id);
                    cls_ = cnode.getKrnClass();
                    parentPath += "." + str;
                    if (i < count - 2 && mode == Mode.RUNTIME) {
                        OrRef parent = getRef(refs, parentPath, filterId);
                        if (parent == null) {
                            boolean b = isContent
                                    || (isColumn && attr_.collectionType == COLLECTION_NONE);
                            parent = new OrRef(parentPath, filterId, b, mode, isContent,
                                refreshMode, dirtyTransactions, frm, refs);
                        } else if (dirtyTransactions == TR_CLEAR)
                            parent.setDirtyTransactions(dirtyTransactions);
                        setParent(parent);
                    }
                }
                if (type_ == null) {
                    type_ = cls_;
                }
                refs.put(path + filterId, this);
                if (attr_ != null) {
                    getCash().addCashChangeListener(attr_.id, this);
                    if (attr_.typeClassId < 99 && attr_.isMultilingual) {
                        allowsLangs = true;
                    }
                }
            }
            if (!allowsLangs) {
                values.addLanguage(0);
            }
        } else
            pathChanged(new OrRefEvent(this, -1, -1, this));
    }

	public KrnClass getType() throws KrnException {
		return type_;
	}

	public OrRef getParent() {
		return parent_;
	}

	public KrnAttribute getAttribute() {
		return attr_;
	}

	public int preEvaluate(KrnObject[] objs, Object originator)
			throws KrnException {
		if (title_ == null || title_.length() == 0)
			return values.size(0);

		if (isLoaded_ && refreshMode_ == Constants.RM_ONCE)
			return values.size(0);
		fireClear();
		isCleared = false;
		values.clear();

		objs_ = objs;

		Cache cash = getCash();

		SortedSet<ObjectRecord> recs = null;
		if (objs == null && cls_ != null) {
			int[] ih = {limit};
			recs = cash.getObjects(cls_.id, defaultFilterId_, ih, this);
			limitExceeded = ih[0] > limit;
		} else if (objs != null) {
			recs = cash.findRecords(objs);
		}
		for (Record rec : recs)
			values.add(langId_, new Item(null, rec, true));

		if (index < 0 || index >= values.size(0))
			index = 0;

		applyFilters();

		return values.size(0);
	}

    public void evaluate(KrnObject[] objs, Object originator)
            throws KrnException {
        evaluate(objs, originator, true);
    }

    public void evaluate(KrnObject[] objs, Object originator, boolean sort)
			throws KrnException {
		if (title_ == null || title_.length() == 0)
			return;

		if (isLoaded_ && refreshMode_ == Constants.RM_ONCE)
			return;
		
		boolean calcOwner = OrCalcRef.setCalculations();
        
		fireClear();
		isCleared = false;
		clearValues();

		objs_ = objs;

		Cache cash = getCash();

		// флаг необходимости фильтрования
		boolean filter = filterIds_.size() > 0;
		
		if (objs == null && cls_ != null) {
			String info = limit == 0 ? "UI:" + ((UIFrame)frm).getUid() + " " + "REF:" + toString() : null;
			int[] ih = {limit};
            Set<ObjectRecord> recs = null;
            AttrRequest req = getRequest(null);
            if (req.getChildren().size() > 0)
            	recs = cash.getObjects(cls_.id, req, defaultFilterId_, ih, this, info);
            else
            	recs = cash.getObjects(cls_.id, defaultFilterId_, ih, this);
            if (recs.size() > 100) {
            	System.out.println("Предупреждение: Отбор " + recs.size() + " записей '" + this + "'.");
            }
			limitExceeded = ih[0] > limit;
            for (Record rec : recs) {
            	if (deletedAttr_ != null && rec.getValue() instanceof KrnObject) {
        			Record dr = cash.getRecord(((KrnObject)rec.getValue()).id, deletedAttr_, 0, 0);
        			if (Long.valueOf(1).equals(dr.getValue()))
    					addValue(langId_, new Item(null, rec, true, true));
        			else
        				addValue(langId_, new Item(null, rec, true, false));
        				
            	} else
            		values.add(langId_, new Item(null, rec, true));
            }
		} else if (objs != null) {
			filter = filter || defaultFilterId_ > 0;
			AttrRequest req = getRequest(null);
			if (req.getChildren().size() > 0) {
				List<Long> objIds = new ArrayList<Long>(objs.length);
				for (KrnObject obj : objs)
					if (obj.id > 0)
						objIds.add(obj.id);
	            long[] ids = Funcs.makeLongArray(objIds);
	            cash.getObjects(ids, req);
			}
            if (sort) {
                Set<ObjectRecord> recs = null;
                recs = cash.findRecords(objs);
                for (Record rec : recs) {
                	Item newItem = new Item(null, rec, true);
					if (childrenAttr_ != null || childrenExpr_ != null)
						rootItem = newItem;
					else
						values.add(langId_, newItem);
//                    values.add(langId_, new Item(null, rec));
                }
            } else {
                List<ObjectRecord> recs = null;
                recs = cash.findRecords(objs, true);
                for (Record rec : recs)
                    values.add(langId_, new Item(null, rec, true));
            }
        }

		if (index < 0 || index >= values.size(0))
			index = 0;

		if (filter) {
			applyFilters();
		}

		if (progress != null) {
			int v = progress.getValue();
			v += values.size(0);
			if (v > progress.getMaximum())
				v = progress.getMaximum();

			progress.setValue(v);
			progress = null;
		}
		// cachePreload();

        if (isInOrTable()) {
            tableAdapter.setSort(false);
        }
		if (childrenAttr_ != null || childrenExpr_ != null)
			fireRootItemsChanged(index, 0, originator);
		else {
	        //int cnt = values.get(langId_).size();
	        //if (cnt > 0)
	        //	fireItemsInserted(0, cnt, originator);
	        fireItemsChanged(originator);
		}
		isLoaded_ = true;
		
		if (calcOwner)
			OrCalcRef.makeCalculations();
	}

	public Cache getCash() {
		return frm.getCash();
	}

	public void evaluate(OrRef ref, Object originator, int reason) throws KrnException {
		if (title_ == null || title_.length() == 0)
			return;

		boolean calcOwner = OrCalcRef.setCalculations();

		fireClear();
		isCleared = false;
		values.clear();

		List<Item> pitems = parent_.getItems(0);
		evaluateItems(0, pitems.size(), true);

		applyFilters();

		if (progress != null) {
			int v = progress.getValue();
			v += values.size(getLangId());
			if (v > progress.getMaximum())
				v = progress.getMaximum();

			progress.setValue(v);
			progress = null;
		}

        if (isInOrTable()) {
            tableAdapter.setSort(false);
        }
        fireValueChangedEvent(-1, originator, reason);
        
		if (calcOwner)
			OrCalcRef.makeCalculations();
	}

	public List<Item> getItems(long langId) {
		long lid = (allowsLangs && langId > 0) ? langId : langId_;
		return values.get(lid);
	}

	public List<Integer> getDeletedIndexes() {
		return deletedIndexes;
	}

	public Item getItem(long langId) {
		long lid = (allowsLangs && langId > 0) ? langId : langId_;
		return (index < values.size(lid) && index >= 0) ? values.get(
			lid, index) : null;
	}

	public Object getValue(long langId) {
		long lid = (allowsLangs && langId > 0) ? langId : langId_;
		Item item = getItem(lid);
		return (item != null) ? item.getCurrent() : null;
	}

    public Object getValue(long langId, int index) {
        long lid = (allowsLangs && langId > 0) ? langId : langId_;
        Item item = getItem(lid, index);
        return (item != null) ? item.getCurrent() : null;
    }

	public void changeItem(Object obj, CheckContext context, Object originator)
			throws KrnException {

		changeItem(index, obj, context, originator);
	}

	public void createObject(CheckContext context, Object originator)
			throws KrnException {

		Cache cache = getCash();
		Object obj = cache.createObject(type_.id).getValue();

		changeItem(index, obj, context, originator);
	}

	public Item getItem(long langId, int ind) {
		long lid = (allowsLangs && langId > 0) ? langId : langId_;
		return values.get(lid, ind);
	}

	// TODO Оптимизация поиска Item по parent
	public Item getItem(long langId, Item parent) {
		long lid = (allowsLangs && langId > 0) ? langId : langId_;
		List<Item> items = values.get(lid);
		for (Item item : items) {
			if (item.parent == parent) {
				return item;
			}
		}
		return null;
	}

	public void changeItem(OrRef.Item item, Object value, CheckContext context,
			Object originator) throws KrnException {

		changeItemHack(item, value, context, originator);
		fireValueChangedEvent(index, originator, 0);
		// state_evaluate();
		fireStateChanged(this);
	}

	public void changeItem(int itemIndex, Object obj, CheckContext context,
			Object originator) throws KrnException {

		changeItemHack(itemIndex, obj, context, originator);
		fireItemsUpdated(itemIndex, itemIndex + 1, originator);
		// state_evaluate();
		fireStateChanged(this);
	}

	public Item insertItem(int i, Object obj, Object originator,
			CheckContext context, boolean autoCreate) throws KrnException {
		if (attr_ == null && obj instanceof KrnObject) {
			absolute((KrnObject) obj, originator);
			return null;
		}
		index = (i == -1) ? values.size(context.getLangId()) : i;
		int index_ = index;
		Item item = null;

		Cache cash = getCash();
		long lid = (context.getLangId() == 0) ? langId_ : context.getLangId();

		if (attr_ == null) {
			Record rec = cash.createObject(cls_.id);
			item = new Item(null, rec, true);
		} else if (parent_.getItem(0) != null) {
			if (obj == null && autoCreate
					&& !Funcs.isIntegralType(getType().id))
				obj = cash.createObject(type_.id).getValue();
			KrnObject o = (KrnObject) parent_.getItem(0).getCurrent();
			if (o != null) {
				Record rec = cash.insertObjectAttribute(o, attr_, i, lid, obj,
					this);
				item = new Item(null, rec, true);
				item.parent = parent_.getItem(0);
			}
		} else {
			return null;
		}

		if (!isColumn) {
			values.add(lid, index_, item);
			fireItemsInserted(index_, index_ + 1, originator);
		} else {
			values.set(lid, index, item);
			fireItemsUpdated(index, index, originator);
		}

		// state_evaluate();
		fireStateChanged(this);

		return item;
	}

	public Item insertItem(
			Item parentItem,
			int index,
			Object obj,
			Object originator,
			CheckContext context,
			boolean autoCreate
			) throws KrnException {

		Cache cash = getCash();
		long lid = (context.getLangId() == 0) ? langId_ : context.getLangId();

		if (obj == null
				&& autoCreate
				&& !Funcs.isIntegralType(getType().id)) {
			obj = cash.createObject(type_.id).getValue();
		}
		KrnObject o = (KrnObject)parentItem.getCurrent();
		if (o != null) {
			Record rec = cash.insertObjectAttribute(o, attr_, index, lid, obj,
				this);
			Item item = new Item(parentItem, rec, true);
			if (!isColumn) {
				int pos = rec.getIndex();
				values.add(lid, pos, item);
				fireItemsInserted(pos, pos + 1, originator);
			} else {
				int pos = parentItem.index;
				values.set(lid, pos, item);
				fireItemsUpdated(pos, pos + 1, originator);
			}
			
			return item;
		}
		return null;
	}

	// HACK надо будет создать модель с возможностью пакетной вставки
	public void insertItemHack(int pos, int i, Object obj, Object originator,
			CheckContext context, boolean autoCreate) throws KrnException {
		if (attr_ == null && obj instanceof KrnObject) {
			absolute((KrnObject) obj, originator);
			return;
		}
		long langId = context.getLangId();
		index = (pos == -1) ? values.size(langId) : pos;
		Item item = null;

		Cache cash = getCash();

		if (attr_ == null)
			item = new Item(null, cash.createObject(cls_.id), true);
		else {
			if (obj == null && autoCreate
					&& !Funcs.isIntegralType((int) getType().id))
				obj = cash.createObject(attr_.typeClassId).getValue();
			KrnObject o = (KrnObject) parent_.getItem(langId).getCurrent();
			Record rec = cash.insertObjectAttribute(o, attr_, i, langId, obj,
				this);
			item = new Item(null, rec, true);
			item.parent = parent_.getItem(langId);
		}

		values.add(langId, index, item);
	}

	public void changeItemHack(int itemIndex, Object obj, CheckContext context,
			Object originator) throws KrnException {
		if (attr_ == null && obj instanceof KrnObject) {
			absolute((KrnObject) obj, originator);
			return;
		}
		long lid = context.getLangId();
		lid = (allowsLangs && lid > 0) ? lid : langId_;
		if (itemIndex == -1) {
			itemIndex = values.size(lid) - 1;
		}

		Item item = values.get(lid, itemIndex);
		
		changeItemHack(item, obj, context, originator);
	}

	public void changeItemHack(Item item, Object obj, CheckContext context,
			Object originator) throws KrnException {

		long lid = context.getLangId();
		lid = (allowsLangs && lid > 0) ? lid : langId_;

		Cache cash = getCash();

		KrnObject o = (KrnObject) item.parent.getCurrent();
		if (isColumn && item.rec == null) {
			// && item.getCurrent() == null) {
			// && !Funcs.isIntegralType(getType().id)) {
			Record rec = cash
				.insertObjectAttribute(o, attr_, 0, lid, obj, this);
			item.rec = rec;
		} else
			cash.changeObjectAttribute(item.rec, obj, this);
		// rec = cash.changeObjectAttribute(o, attr_, itemIndex, langId_, obj,
		// null);
		item.isDeleted = false;
	}

	// END HACK

	public void deleteItem(CheckContext context, Object originator) {
		deleteItem(index, context, originator);
	}

	// val
	public void deleteItem(CheckContext context, int aIndexItem,
			Object originator) throws KrnException {
		deleteItem(aIndexItem, context, originator);
	}

	// val

	public void deleteItem(
			int itemIndex,
			CheckContext context,
			Object originator) {
		
		if (itemIndex < 0) return;
		
		long lid = (context.getLangId() == 0) ? langId_ : context.getLangId();
		List<Item> items = getItems(lid);
		if (items != null && items.size() > itemIndex) {
			Item item = getItems(lid).get(itemIndex);
			if (item != null) {
				Cache cash = getCash();
				try {
					if (attr_ == null) {
						cash.deleteObject(item.rec, this);
					} else {
						cash.deleteObjectAttribute(item.rec, this, false);
					}
				} catch (KrnException e) {
					log.error(e, e);
				}
				if (isColumn) {
					item.rec = null;
					fireItemsUpdated(itemIndex, itemIndex + 1, originator);
				} else {
					values.remove(lid, itemIndex);
					if (index >= values.size(lid))
						index = values.size(lid) - 1;
					if (index == -1 && !isInOrTable()) {
						index = 0;
					}
					fireItemsDeleted(itemIndex, itemIndex + 1, originator);
				}
			}
		}
	}

	public void moveUp(CheckContext context, int i, Object originator)
			throws KrnException {
		if (attr_ == null)
			return;
		long lid = (context.getLangId() == 0) ? langId_ : context.getLangId();
		Item item = getItem(lid);
		if (item != null) {
			Cache cash = getCash();
			if (parent_.getItem(0) != null) {
				KrnObject o = (KrnObject) parent_.getItem(0).getCurrent();
				if (o != null) {
					cash.moveUpObjectAttribute(o, attr_, i, lid, originator);
				}
			}
		}
	}

	public void moveDown(CheckContext context, int i, Object originator)
			throws KrnException {
		if (attr_ == null)
			return;
		long lid = (context.getLangId() == 0) ? langId_ : context.getLangId();
		Item item = getItem(lid);
		if (item != null) {
			Cache cash = getCash();
			if (parent_.getItem(0) != null) {
				KrnObject o = (KrnObject) parent_.getItem(0).getCurrent();
				if (o != null) {
					cash.moveDownObjectAttribute(o, attr_, i, lid, originator);
				}
			}
		}
	}

    public void moveRowsBefore(CheckContext context, int i, int[] rows, Object originator)
            throws KrnException {
        if (attr_ == null)
            return;
        long lid = (context.getLangId() == 0) ? langId_ : context.getLangId();
        Item item = getItem(lid);
        if (item != null) {
            Cache cash = getCash();
            if (parent_.getItem(0) != null) {
                KrnObject o = (KrnObject) parent_.getItem(0).getCurrent();
                if (o != null) {
                    cash.moveRowsBeforeObjectAttribute(o, attr_, i, rows, lid, originator);
                }
            }
        }
    }

    public void moveRowsBefore(CheckContext context, int i, int[] rows, int[] mrows, Object originator)
            throws KrnException {
        if (attr_ == null)
            return;
        long lid = (context.getLangId() == 0) ? langId_ : context.getLangId();
        Item item = getItem(lid, i);
        if (item != null) {
            Cache cash = getCash();
            if (parent_.getItem(0) != null) {
                KrnObject o = (KrnObject) parent_.getItem(0).getCurrent();
                if (o != null) {
                    i = item.getRec().getIndex();
                    for (int k = 0; k<rows.length; k++) {
                        item = getItem(lid, rows[k]);
                        if (item != null && item.getRec() != null) {
                            rows[k] = item.getRec().getIndex();
                        }
                    }
                    for (int k = 0; k<mrows.length; k++) {
                        item = getItem(lid, mrows[k]);
                        if (item != null && item.getRec() != null) {
                            mrows[k] = item.getRec().getIndex();
                        }
                    }
                    cash.moveRowsBeforeObjectAttribute(o, attr_, i, rows, mrows, lid, originator);
                }
            }
        }
    }

    public void changePlaces(CheckContext context, int index1, int index2, Object originator)
            throws KrnException {
        if (attr_ == null)
            return;
        long lid = (context.getLangId() == 0) ? langId_ : context.getLangId();

        Item item1 = values.get(lid, index1);
        Item item2 = values.get(lid, index2);
        if (item1 != null && item2 != null && item1.getRec() != null && item2.getRec() != null) {
            Cache cash = getCash();
            if (parent_.getItem(0) != null) {
                KrnObject o = (KrnObject) parent_.getItem(0).getCurrent();
                if (o != null) {
                    cash.changePlacesObjectAttribute(o, attr_, item1.getRec(), item2.getRec(), originator);
                }
            }
        }
    }

	public int getIndex() {
		return index;
	}

	public boolean absolute(int index, Object originator) {
		if (this.index != index && index < values.size(langId_) && -1 <= index) {
	        boolean calcOwner = OrCalcRef.setCalculations();
			this.index = index;
			fireValueIteratedEvent(originator);
			if (calcOwner)
				OrCalcRef.makeCalculations();
			return true;
		}
		return false;
	}

	public void commitChanges(Object originator) throws KrnException {
		fireChangesCommitted(originator);
	}

	public void rollback(Object originator, long flowId) throws KrnException {
		getCash().rollback(flowId);
		evaluate(originator);
		fireChangesRollbacked(originator);
	}

	public void evaluate(Object originator) throws KrnException {
		evaluate(objs_, originator);
	}

	public String toString() {
		return title_;
	}

	public void setLangId(long langId) throws KrnException {
		if (attr_ != null && attr_.isMultilingual) {
			langId_ = langId;
		}
		if (children_ != null) {
			for (int i = 0; i < children_.size(); i++) {
				OrRef child = (OrRef) children_.get(i);
				child.setLangId(langId);
			}
		}
		values.addLanguage(langId_);
	}

	public long getLangId() {
		return langId_;
	}

	public void addOrRefListener(OrRefListener l) {
		listeners_.add(OrRefListener.class, l);
	}

	public void removeOrRefListener(OrRefListener l) {
		listeners_.remove(OrRefListener.class, l);
	}

	public void setDefaultFilter(long filterId) throws KrnException {
		defaultFilterId_ = filterId;
		if (parent_ != null)
			filterIds_.add(new Long(defaultFilterId_));
	}

	public boolean hasFilters() {
		if (hasFilters || defaultFilterId_ > 0 || filterIds_.size() > 0)
			return true;
		else
			return false;
	}

	public void setHasFilters(boolean hasFilters) {
		this.hasFilters = hasFilters;
	}

	public FilterDate[] addFilter(Filter filter) throws KrnException {
		Long fid = new Long(filter.obj.id);
		FilterDate[] fds = null;
		if (filterIds_.contains(fid)) {
			// JOptionPane.showMessageDialog(
			// null, "Данный фильтр уже используется", "Внимание",
			// JOptionPane.INFORMATION_MESSAGE);
			filterIds_.remove(fid);
		} else {
			if (filter.flags > 0) {
				ResourceBundle res = frm.getResourceBundle();
				ReadFilterDatesPanel rfdp = new ReadFilterDatesPanel(
					filter.obj.id, filter.flags, res);
				String title = (res != null) ? res
					.getString("filterDatesTitle")
						: "Введите временные параметры";

				PropDlg dlg = new PropDlg((JFrame) null, false);
				dlg.setTitle(title);
				dlg.setContent(rfdp);
				dlg.show();
				if (dlg.result == PropDlg.MR_OK) {
					fds = rfdp.getFilterDates();
					if (fds.length > 0)
						filterDates_.clear();
					for (int i = 0; i < fds.length; i++)
						filterDates_.add(fds[i]);
				} else
					return null;
			}
			filterIds_.add(fid);
		}
		refresh();
		return fds;
	}

	public void addFilters(List<Filter> filters) throws KrnException {
		filterDates_.clear();
		filterIds_.clear();
		if (defaultFilterId_ > 0 && parent_ != null)
			filterIds_.add(defaultFilterId_);
		for (Filter filter : filters) {
			Long fid = new Long(filter.obj.id);
			FilterDate[] fds = null;
			if (!filterIds_.contains(fid)) {
				if (filter.flags > 0) {
					ResourceBundle res = frm.getResourceBundle();
					ReadFilterDatesPanel rfdp = new ReadFilterDatesPanel(
						filter.obj.id, filter.flags, res);
					String title = (res != null) ? res
						.getString("filterDatesTitle")
							: "Введите временные параметры";

					PropDlg dlg = new PropDlg((JFrame) null, false);
					dlg.setTitle(title);
					dlg.setContent(rfdp);
					dlg.show();
					if (dlg.result == PropDlg.MR_OK) {
						fds = rfdp.getFilterDates();
						if (fds.length > 0)
							filterDates_.clear();
						for (int i = 0; i < fds.length; i++)
							filterDates_.add(fds[i]);
					}
				}
				filterIds_.add(fid);
			}
		}
		refresh();
	}

	public void addFilter(long filterId, FilterDate[] fds, Object originator)
			throws KrnException {
		filterIds_.add(new Long(filterId));
		if (fds != null && fds.length > 0) {
			filterDates_.clear();
			for (int i = 0; i < fds.length; i++) {
				if (fds[i].date != null)
					filterDates_.add(fds[i]);
			}
		}
		evaluate((KrnObject[]) null, originator);
	}

	public void removeFilter(long filterId, Object originator)
			throws KrnException {
		if (filterIds_.contains(new Long(filterId))) {
			filterIds_.remove(new Long(filterId));
			evaluate((KrnObject[]) null, originator);
		}
	}

	public void removeAllFilters(boolean isRecursive) throws KrnException {
		filterDates_.clear();
		filterIds_.clear();
		if (defaultFilterId_ > 0 && parent_ != null)
			filterIds_.add(defaultFilterId_);
		if (isRecursive && children_ != null) {
			for (Iterator<OrRef> it = children_.iterator(); it.hasNext();)
				it.next().removeAllFilters(isRecursive);
		}
		if (!isRecursive)
			refresh();
	}

	private boolean applyFilters() throws KrnException {
		boolean res = false;
		if (values.size(0) > 0) {
			Set<Long> finalObjIds = filter();
			if (finalObjIds != null) {
				for (int i = values.size(0) - 1; i >= 0; --i) {
					Item item = values.get(0, i);
					KrnObject val = (KrnObject) item.getCurrent();
					if (val == null || (val.id > 0 && !finalObjIds.contains(new Long(val.id)))) {
						values.remove(0, i);
						res = true;
					}
				}
				index = 0;
				selectedInds_ = null;
			}
		}
		return res;
	}

	private Set<Long> filter() throws KrnException {
		Set<Long> finalObjIds = null;
		if (filterIds_.size() > 0 || defaultFilterId_ > 0) {
			List<Long> filtIds = new ArrayList<Long>(filterIds_);

			if (defaultFilterId_ > 0 && parent_ == null)
				filtIds.add(defaultFilterId_);

			FilterDate[] fds = new FilterDate[filterDates_.size()];
			for (int i = 0; i < filterDates_.size(); ++i)
				fds[i] = (FilterDate) filterDates_.get(i);

			for (int i = 0; i < filtIds.size(); i++) {
				int[] ih = { limit };
				long[] oids = krn_.getFilteredObjectIds(new long[] { filtIds
					.get(i) }, fds, ih, getCash().getTransactionId());
				limitExceeded &= ih[0] > limit;
				Set<Long> objIds = new HashSet<Long>();
				for (int j = 0; j < oids.length; ++j)
					objIds.add(oids[j]);

				if (finalObjIds == null)
					finalObjIds = objIds;
				else
					finalObjIds.retainAll(objIds);
			}
		}
		return finalObjIds;
	}

	private void refresh() throws KrnException {
		if (parent_ != null)
			evaluate(parent_, null, 0);
		else
			evaluate(objs_, null);
	}

	public void setColumn(boolean isColumn) {
		this.isColumn = isColumn;
	}

	public OrRef getRoot() {
		OrRef ref = this;
		while (ref.getParent() != null)
			ref = ref.getParent();
		return ref;
	}

	public OrRef getParentOfClass(long classId) {
		OrRef ref = this;
		try {
			while (ref.getParent() != null
					&& !(krn_.isSubclassOf(classId,
						ref.getType().id) || krn_.isSubclassOf(
						ref.getType().id, classId)))
				ref = ref.getParent();
		} catch (KrnException e) {
			log.error(e, e);
		}
		return ref;
	}

	// OrRefListener
	public void checkReqGroups(OrRef ref, List<ReqMsgsList.MsgListItem> msgs,
			List<ReqMsgsList.MsgListItem> reqMsgs, Stack<Pair> locs) {
		setChecking(true);
		try {
			cachePreload();
			checkReqGroups(ref, msgs, reqMsgs, locs, "");
		} finally {
			setChecking(false);
		}
	}

	public void cachePreload() {
		if (isInOrTable()) {
			List<KrnObject> pobjs = new ArrayList<KrnObject>();
			for (int i = 0; i < values.size(langId_); i++) {
				KrnObject obj = (KrnObject)values.get(langId_, i).getCurrent();
				if (obj != null)
					pobjs.add(obj);
			}
			long[] poids = Funcs.makeObjectIdArray(pobjs);

			fireCachePreload(poids);
		} else {
			Item item = getItem(langId_);
			if (item != null) {
				KrnObject obj = (KrnObject)item.getCurrent();
				if (obj != null)
					fireCachePreload(new long[] {obj.id});
			}
		}
	}

	private void fireCachePreload(long[] pids) {
		Object[] listeners = listeners_.getListenerList();
		for (int i = 0; i < listeners.length; i += 2) {
			if (listeners[i] == OrRefListener.class) {
				OrRefListener l = (OrRefListener) listeners[i + 1];
				if (l instanceof OrRef && !(l instanceof OrCalcRef))
					((OrRef)l).preloadCache(pids);
			}
		}
	}

	private void preloadCache(long[] pids) {
		
		if (filterIds_.size() > 0 || defaultFilterId_ > 0)
			return;
		
		Cache cache = getCash();

		Set<Long> langIds = values.getLangIds();
		for (long langId : langIds) {
			try {
				SortedSet<Record> recs = cache.getRecords(pids, attr_, langId, null);
				if (attr_.typeClassId > 99) {
					List<KrnObject> pobjs = new ArrayList<KrnObject>();
					if (recs.size() > 0) {
						Set<Long> filteredIds = filter();
						for (Record r : recs) {
							KrnObject obj = (KrnObject)r.getValue();
							if (obj != null && (filteredIds == null || filteredIds.contains(obj.id)))
								pobjs.add(obj);
						}
					}
					
					long[] poids = Funcs.makeObjectIdArray(pobjs);
	
					fireCachePreload(poids);
				}
			} catch (KrnException e) {
				e.printStackTrace();
			}
		}
	}

	public void checkReqGroups(OrRef ref, List<ReqMsgsList.MsgListItem> msgs,
			List<ReqMsgsList.MsgListItem> reqMsgs,
			Stack<Pair> locs, String path) {
		// Проверить себя
		if (checkContextList.isEmpty()) {
			checkContextList.add(new DummyCheckContext(this));
		}
		
		boolean listenersChecked = false;
		
		for (int i = 0; i < checkContextList.size(); i++) {
			CheckContext context = checkContextList.get(i);
			// context.clearStates();
			Item pitem = ref.getItem(0);
			Item item = getItem(context.getLangId());
			if (!context.isActive()) {
				if (!listenersChecked) {
					listenersChecked = true;
					checkListeners(msgs, reqMsgs, locs, path);
				}
				continue;
			}
			if (isInOrTable() && context.isCheckConstr()) {
				String tableName = tableAdapter.getTable().getTitle();// getPropertyValue(
				// tableAdapter.getTable().getProperties().getChild("title")).stringValue();
				if (!listenersChecked) {
					listenersChecked = true;
					path = path + " Таблица: \"" + tableName + "\"";
					int pos = index;
					
					if (filterIds_.size() > 0 || defaultFilterId_ > 0)
						cachePreload();
					
					for (int j = 0; j < values.size(langId_); ++j) {
						absolute(j, this);
						locs.push(new Pair<OrRef, Integer>(this, j));
						int row = j;
						String pth;
						if (tableAdapter instanceof TreeTableAdapter)
							pth = path + "[" + ((TreeTableAdapter)tableAdapter).getSelectedPathString(row) + "]";
						else 
							pth = path + "(" + ++row + ")";
						
						checkListeners(msgs, reqMsgs, locs, pth);
						locs.pop();
					}
					absolute(pos, this);
				}
				if (context.getReqGroup() > 0 && values.size(langId_) == 0
						&& tableAdapter.getTable().isEnabled()) {
					ReqMsgsList.MsgListItem mes = new ReqMsgsList.MsgListItem(
						this, index, context.getReqMsg(), context.getEnterDB(),
						locs.toArray(new Pair[locs.size()]), context.getLangId(), context.getUUID());
					reqMsgs.add(mes);
				}
				checkConstraints(msgs, path, context);
			} else {
				if (context.isCheckConstr() && context.getReqGroup() > 1) {
					List<CheckContext> friendContexts = frm.getRefGroups(context
						.getReqGroup());
					boolean full = false;
					for (int j = 0; j < friendContexts.size(); j++) {
						CheckContext fcontext = friendContexts
							.get(j);
						Item fitem = fcontext.getRef().getItem(
							fcontext.getLangId());
						if (fitem != null && fitem.getCurrent() != null
								&& fitem.toString().trim().length() > 0) {
							full = true;
							break;
						}
					}
					if (full) {
						if (item == null || item.getCurrent() == null
								|| item.toString().trim().length() == 0) {
							String msg = context.getReqMsg();
							msg += path;
							ReqMsgsList.MsgListItem mes = new ReqMsgsList.MsgListItem(
								this, index, msg, context.getEnterDB(), locs
									.toArray(new Pair[locs.size()]), context.getLangId(), context.getUUID());
							reqMsgs.add(mes);
							if (context.getEnterDB() == Constants.BINDING)
								context.setState(new Integer(index),
									Constants.REQ_ERROR);
						}
					} else {
						context.removeState(new Integer(index));
						context
							.setState(new Integer(index), Constants.NO_ERROR);
					}
				} else if (context.isCheckConstr()
						&& context.getReqGroup() == 1 && pitem != null) {
					if (!context.isCheckConstrValue() || item == null || item.getCurrent() == null
							|| item.toString().trim().length() == 0 
							|| (context instanceof CheckBoxColumnAdapter && item.toString().equals("0"))) {//буленовское поле 0-если не заполнено
						String msg = context.getReqMsg();
						msg += path;
						ReqMsgsList.MsgListItem mes = new ReqMsgsList.MsgListItem(
							this, index, msg, context.getEnterDB(), locs
								.toArray(new Pair[locs.size()]), context.getLangId(), context.getUUID());
						reqMsgs.add(mes);
						if (context.getEnterDB() == Constants.BINDING)
							context.setState(new Integer(index),
								Constants.REQ_ERROR);
					} else {
						context.removeState(new Integer(index));
						context
							.setState(new Integer(index), Constants.NO_ERROR);
					}
				} else if (!context.isCheckConstr()) {
					context.removeState(new Integer(index));
					context.setState(new Integer(index), Constants.NO_ERROR);
				}
				checkConstraints(msgs, path, context);
				if (!listenersChecked) {
					listenersChecked = true;
					checkListeners(msgs, reqMsgs, locs, path);
				}
			}
		}
	}

	private void checkConstraints(List<ReqMsgsList.MsgListItem> msgs,
			String path, CheckContext context) {
//		for (int i = 0; i < checkContextList.size(); i++) {
//			CheckContext context = (CheckContext) checkContextList.get(i);
			Item item = getItem(context.getLangId());
			Map<String, Object> vc = new HashMap<String, Object>();
			ClientOrLang parser = new ClientOrLang(frm);
			ASTStart ctemplate = context.getCTemplate();
			Object o = (item != null) ? item.getCurrent() : null;
			if (o != null && !"".equals(o) && ctemplate != null) {
				try {
					parser
						.evaluate(ctemplate, vc, context, new Stack<String>());
				} catch (Exception e) {
					log.error(e, e);
					Util.showErrorMessage(null, e.getMessage(),
						"Проверка ограничения. REF='" + toString() + "'");
				}
				Object val = vc.get("RETURN");
				Object error = vc.get("ERRMSG");
				Object errorType = vc.get("ERRTYPE");
				boolean isOk = false;
				if (val != null) {
					isOk = (val instanceof Number && ((Number) val).intValue() == 1);
					if (!isOk) {
						if (error == null) {
							String constrMsg = context.getConstrMsg();
							if (constrMsg != null) {
								error = constrMsg;
							} else {
								System.out
									.println("!ERROR: Не задано сообщения для "
											+ "ограничения '"
											+ context.getCExpr() + "'");
							}
						}
						String msg = (error != null) ? error.toString() : "";
						msg += path;
						int erType=errorType instanceof Integer?((Integer)errorType).intValue()
								:(errorType instanceof Long?((Long)errorType).intValue():-1);
						erType =(erType==Constants.BINDING?Constants.BINDING:(erType==Constants.OPTIONAL?Constants.OPTIONAL:Constants.CONSTRAINT)); 
						ReqMsgsList.MsgListItem mes = new ReqMsgsList.MsgListItem(
							this, index, msg, erType, null, context.getLangId(), context.getUUID());
						msgs.add(mes);
						context.setState(new Integer(index),
							Constants.EXPR_ERROR);
					}
				}
			}
//		}
	}

	private void checkListeners(List<ReqMsgsList.MsgListItem> msgs,
			List<ReqMsgsList.MsgListItem> reqMsgs,
			Stack<Pair> locs, String path) {
		Object[] listeners = listeners_.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == OrRefListener.class) {
				OrRefListener l = (OrRefListener) listeners[i + 1];
				if (l instanceof OrRef)
					((OrRef) l).checkReqGroups(this, msgs, reqMsgs,
						locs, path);
			}
		}
	}

	public ReqMsgsList canCommit() {
		saveState();
		List<ReqMsgsList.MsgListItem> res = new ArrayList<ReqMsgsList.MsgListItem>();
		List<ReqMsgsList.MsgListItem> reqMsgs = new ArrayList<ReqMsgsList.MsgListItem>();
		state_evaluate();
		Stack<Pair> locs = new Stack<Pair>();
		checkReqGroups(this, res, reqMsgs, locs);
		restoreState();
		fireStateChanged(this);
		return getReqMessage(res, reqMsgs);
	}

	private ReqMsgsList getReqMessage(List<ReqMsgsList.MsgListItem> constr,
			List<ReqMsgsList.MsgListItem> reqMsgs) {
		ReqMsgsList msgList = new ReqMsgsList();
		for (int i = 0; i < constr.size(); i++) {
			reqMsgs.add(constr.get(i));
		}
		ReqMsgsList.MsgListItem[] listObjs = new ReqMsgsList.MsgListItem[reqMsgs
			.size()];
		for (int i = 0; i < reqMsgs.size(); i++) {
			listObjs[i] = (ReqMsgsList.MsgListItem) reqMsgs.get(i);
		}
		Arrays
			.sort(listObjs, new ReqMsgsList.SortOnTitles(ReqMsgsList.SORT_UP));
		msgList.addToList(listObjs);
		return msgList;
	}

	public void valueChanged(OrRefEvent e) {
		if (e.getRef() != this) {
			try {
				int reason = e.getReason();

				if (reason == OrRefEvent.INSERTED) {
					if (isColumn) {
						index = parent_.index;
						evaluateItems(e.getStartIndex(), e.getEndIndex(), true);
						fireItemsInserted(e.getStartIndex(), e.getEndIndex(), e.getOriginator());
					
					} else if (parent_.index >= e.getStartIndex()
							&& parent_.index <= e.getEndIndex()) {
						index = 0;
						values.clear();
						int cnt = evaluateItems(index, index, false);
						if (childrenAttr_ != null || childrenExpr_ != null)
							fireRootItemsChanged(index, cnt, e.getOriginator());
						else
							fireItemsChanged(e.getOriginator());
					}
			
				} else if (reason == OrRefEvent.DELETED) {
					if (isColumn) {
						for (int i = e.getEndIndex() - 1; i >= e.getStartIndex(); i--)
							values.remove(i);
						index = parent_.getIndex();
						fireItemsDeleted(e.getStartIndex(), e.getEndIndex(), this);
					
					} else {
						index = 0;
						values.clear();
						evaluateItems(index, index, false);
						fireItemsChanged(e.getOriginator());
					}
				
				} else if (reason == OrRefEvent.UPDATED) {
					if (isColumn) {
						evaluateItems(e.getStartIndex(), e.getEndIndex(), false);
						fireItemsUpdated(e.getStartIndex(), e.getEndIndex(), e.getOriginator());
					} else if (parent_.index >= e.getStartIndex()
							&& parent_.index <= e.getEndIndex()) {
						index = 0;
						values.clear();
						int cnt = evaluateItems(e.getStartIndex(), e.getEndIndex(), false);
						if (childrenAttr_ != null || childrenExpr_ != null)
							fireRootItemsChanged(index, cnt, e.getOriginator());
						else
							fireItemsChanged(e.getOriginator());
					}
					
				} else if (reason == OrRefEvent.CHANGED) {
					if (isColumn) {
						index = parent_.getIndex();
						values.clear();
						int cnt = parent_.getItems(0).size();
						evaluateItems(0, cnt, true);
						fireItemsChanged(e.getOriginator());
					} else {
						index = 0;
						values.clear();
						int cnt = evaluateItems(e.getStartIndex(), e.getEndIndex(), false);
						if (childrenAttr_ != null || childrenExpr_ != null)
							fireRootItemsChanged(index, cnt, e.getOriginator());
						else
							fireItemsChanged(e.getOriginator());
					}
					
				} else if (reason == OrRefEvent.ITERATING) {
					if (isColumn) {
						index = parent_.getIndex();
						fireValueIteratedEvent(e.getOriginator());
					} else {
						values.clear();
						int cnt = evaluateItems(index, index + 1, false);
						if (childrenAttr_ != null || childrenExpr_ != null)
							fireRootItemsChanged(index, cnt, e.getOriginator());
						else
							fireItemsChanged(e.getOriginator());
					}
				}
			} catch (KrnException ex) {
				ex.printStackTrace();
                Util.showErrorMessage(((UIFrame)frm).getPanel(), "Системная ошибка. Обратитесь к разработчику!\n" + ex.getMessage(), "Обновление содержимого");
			}
		}
	}

	public void changesCommitted(OrRefEvent e) {
		fireChangesCommitted(e.getOriginator());
	}

	public void changesRollbacked(OrRefEvent e) {
		fireChangesRollbacked(e.getOriginator());
	}

	public void pathChanged(OrRefEvent e) {
		cls_ = null;
		attr_ = null;
		if (title_ != null && title_.length() > 0) {
			try {
				int p = title_.indexOf('.');
				if (p == -1)
					p = title_.length();
				cls_ = krn_.getClassByName(title_.substring(0, p));
				// @TODO Сделать проверку
				KrnAttribute[] attrs = Utils.getAttributesForPath(title_);
				if (attrs != null && attrs.length > 0)
					attr_ = attrs[attrs.length - 1];
			} catch (KrnException ex) {
				JOptionPane.showMessageDialog(null, ex.getMessage(),
					"Неверный путь", JOptionPane.ERROR_MESSAGE);
			}
		}
		firePathChangedEvent(e.getOriginator());
	}

	public void fireValueChangedEvent(int index, Object originator, int reason) {
		for (int i = 0; i < checkContextList.size(); i++) {
			CheckContext context = (CheckContext) checkContextList.get(i);
			Integer currentState = context.getState(new Integer(index));
			if (currentState != null
					&& !currentState.equals(Constants.NO_ERROR)) {
				context.removeState(new Integer(index));
				context.setState(new Integer(index), Constants.NO_ERROR);
			}
		}
		fireRefEvent(new OrRefEvent(this, OrRefEvent.CHANGED, index,
			originator));
	}

	protected void fireValueIteratedEvent(Object originator) {
		fireRefEvent(new OrRefEvent(this, OrRefEvent.ITERATING, index, originator));
	}

	protected void fireValueChanged(int index, Object originator) {
		fireRefEvent(new OrRefEvent(this, OrRefEvent.UPDATED, index, originator));
	}

	protected void fireItemInserted(int index, Object originator) {
		fireRefEvent(new OrRefEvent(this, OrRefEvent.INSERTED, index,
			originator));
	}

	protected void fireItemsInserted(int startIndex, int endIndex, Object originator) {
		fireRefEvent(new OrRefEvent(this, OrRefEvent.INSERTED, startIndex, endIndex,
			originator));
	}

	protected void fireItemsDeleted(int startIndex, int endIndex, Object originator) {
		fireRefEvent(new OrRefEvent(this, OrRefEvent.DELETED, startIndex, endIndex,
			originator));
	}

	protected void fireItemsUpdated(int startIndex, int endIndex, Object originator) {
		fireRefEvent(new OrRefEvent(this, OrRefEvent.UPDATED, startIndex, endIndex,
			originator));
	}

	protected void fireItemsChanged(Object originator) {
		fireRefEvent(new OrRefEvent(this, OrRefEvent.CHANGED, -1, -1,
			originator));
	}

	protected void fireRootItemsChanged(int startIndex, int endIndex, Object originator) {
		fireRefEvent(new OrRefEvent(this, OrRefEvent.ROOT_ITEM_CHANGED, startIndex, endIndex,
			originator));
	}

	protected void fireItemDeleted(Object originator) {
		fireRefEvent(new OrRefEvent(this, OrRefEvent.DELETED, index, originator));
	}

	private void fireRefEvent(OrRefEvent e) {
		exitPrevFire = false;
		Object[] listeners = listeners_.getListenerList();
		for (int i = 0; i < listeners.length; i += 2) {
			//if (exitPrevFire) break;
			if (listeners[i] == OrRefListener.class) {
				OrRefListener l = (OrRefListener) listeners[i + 1];
				if ((e.getReason() != OrRefEvent.CHECKING || l instanceof OrRef) && (!exitPrevFire || l instanceof ReportPrinterAdapter.TreeReportNode))
					l.valueChanged(e);
			}
		}
	}

	protected void firePathChangedEvent(Object originator) {
		Object[] listeners = listeners_.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == OrRefListener.class) {
				OrRefListener l = (OrRefListener) listeners[i + 1];
				l.pathChanged(new OrRefEvent(this, -1, -1, originator));
			}
		}
	}

	protected void fireChangesCommitted(Object originator) {
		Object[] listeners = listeners_.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == OrRefListener.class) {
				OrRefListener l = (OrRefListener) listeners[i + 1];
				l.changesCommitted(new OrRefEvent(this, -1, -1, originator));
			}
		}
	}

	protected void fireChangesRollbacked(Object originator) {
		Object[] listeners = listeners_.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == OrRefListener.class) {
				OrRefListener l = (OrRefListener) listeners[i + 1];
				l.changesRollbacked(new OrRefEvent(this, -1, -1, originator));
			}
		}
	}

	public int getListenersCount(JProgressBar progress) {
		this.progress = progress;
		int res = 1;
		Object[] listeners = listeners_.getListenerList();
		for (int i = 0; i < listeners.length; i += 2) {
			if (listeners[i + 1] instanceof OrRef) {
				OrRef l = (OrRef) listeners[i + 1];
				if (l.isColumn)
					res += l.getListenersCount(progress);
			}
		}
		return res;
	}

	public int getSimpleListenersCount(JProgressBar progress) {
		this.progress = progress;
		int res = 1;
		Object[] listeners = listeners_.getListenerList();
		for (int i = 0; i < listeners.length; i += 2) {
			if (listeners[i + 1] instanceof OrRef) {
				OrRef l = (OrRef) listeners[i + 1];
				if (!l.isColumn)
					res += l.getSimpleListenersCount(progress);
			}
		}
		return res;
	}

	protected void fireStateChanged(Object originator) {
		Object[] listeners = listeners_.getListenerList();
		for (int i = 0; i < listeners.length; i += 2) {
			if (listeners[i] == OrRefListener.class) {
				OrRefListener l = (OrRefListener) listeners[i + 1];
				l.stateChanged(new OrRefEvent(this, -1, -1, originator));
			}
		}
	}

	protected void fireSetFocus(int index, long langId, Object originator) {
		Object[] listeners = listeners_.getListenerList();
		for (int i = 0; i < listeners.length; i += 2) {
			if (listeners[i] == OrRefListener.class) {
				OrRefListener l = (OrRefListener) listeners[i + 1];
				if (l instanceof ComponentAdapter
						&& !((ComponentAdapter) l).isEditor()
						&& ((ComponentAdapter) l).dataRef == this
						&& ((ComponentAdapter) l).getLangId() == langId) {
					l.setFocus(index, new OrRefEvent(this, -1, -1, originator));
					break;
				}
			}
		}
	}

	public void fireSetFocusEvent(int index, long langId, Object originator) {
		fireSetFocus(index, langId, this);
	}

	private int evaluateItems(int startIndex, int endIndex, boolean insert) throws KrnException {
		isCleared = false;
		Cache cache = getCash();
		if (isColumn) {
			List<Item> pitems = parent_.getItems(0);
			List<KrnObject> pobjs = new ArrayList<KrnObject>();
			for (int i = startIndex; i < endIndex; i++) {
				KrnObject obj = (KrnObject)pitems.get(i).getCurrent();
				if (obj != null)
					pobjs.add(obj);
			}
			long[] poids = Funcs.makeObjectIdArray(pobjs);
			Set<Long> langIds = values.getLangIds();
			for (long langId : langIds) {
				SortedSet<Record> recs = cache.getRecords(poids, attr_, langId, null);
				for (int i = startIndex; i < endIndex; ++i) {
					Item pitem = pitems.get(i);
					KrnObject pobj = (KrnObject)pitem.getCurrent();
					Item newItem = null;
					if (pobj != null) {
						Record rec = getLast(pobj.id, langId,	recs, attrIndex_);
						newItem = new Item(pitem, rec, true);
					} else {
						newItem = new Item(pitem, null, true);
					}
					if (insert)
						values.add(langId, i, newItem);
					else
						values.set(langId, i, newItem);
				}
			}
			return endIndex;
		} else if (!isArray()) {
			Item pitem = parent_.getItem(0);
			if (pitem != null && pitem.getCurrent() instanceof KrnObject) {
				long poid = ((KrnObject)pitem.getCurrent()).id;
				int i = 0;
				if (attrIndex_ instanceof Number)
					i = ((Number)attrIndex_).intValue();
				Set<Long> langIds = values.getLangIds();
				Set<Long> lostIds = new HashSet<Long>();
				AttrRequest req = (attr_.typeClassId >= 99) ? getRequest(null) : null;
				for (long langId : langIds) {
					Record rec = cache.getRecord(poid, attr_, langId, i, lostIds, req);
					if (rec != null) {
						Item newItem = new Item(pitem, rec, true);
		            	if (childrenAttr_ != null || childrenExpr_ != null)
							rootItem = newItem;
						else
							values.add(langId, newItem);
					}
				}
				return 1;
			} else
				return 0;
		} else {
			int cnt = 0;
			Set<Long> lostIds = new HashSet<Long>();
			Item pitem = parent_.getItem(0);
			if (pitem != null && pitem.getCurrent() instanceof KrnObject) {
				long poid = ((KrnObject)pitem.getCurrent()).id;
				long[] poids = { poid };
				Set<Long> langIds = values.getLangIds();
				AttrRequest req = (attr_.typeClassId >= 99) ? getRequest(null) : null;
				for (long langId : langIds) {
					SortedSet<Record> recs = cache.getRecords(poids, attr_, langId, new long[0], lostIds, null, true, req);
					if (attrIndex_ == null || attrIndex_ instanceof String) {
						for (Record rec : recs) {
							Item newItem = new Item(pitem, rec, true);
							if (childrenAttr_ != null || childrenExpr_ != null)
								rootItem = newItem;
							else
								values.add(langId, newItem);
						}
						
						int size = values.size(langId);
						
						if (!isInOrTable())
							index = size - 1;
						cnt = Math.max(cnt, recs.size());
						
						if (size == 0)
							index = 0;
						else if (index >= size)
							absolute(0, this);
						
						if (index > -1 && index < values.size(langId))
							setSelectedItems(new int[] { index });
						else
							setSelectedItems(new int[0]);
						
					} else {
						Record rec = getLast(poid, langId, recs, attrIndex_);
						if (rec != null) {
							Item newItem = new Item(pitem, rec, true);
							if (childrenAttr_ != null || childrenExpr_ != null)
								rootItem = newItem;
							else
								values.add(langId, newItem);
							cnt = 1;
						}
					}
				}
			}
			boolean f = applyFilters();
			return f ? -1 : cnt;
		}
	}

	public boolean isInOrTable() {
		if (isInOrTable_ == null) {
			Object[] listeners = listeners_.getListenerList();
			for (int i = listeners.length - 2; i >= 0; i -= 2) {
				if (listeners[i] == OrRefListener.class) {
					if (listeners[i + 1] instanceof TableAdapter) {
						tableAdapter = (TableAdapter) listeners[i + 1];
						isInOrTable_ = Boolean.TRUE;
						break;
					}
				}
			}
			if (isInOrTable_ == null)
				isInOrTable_ = Boolean.FALSE;
		}
		return isInOrTable_.booleanValue();
	}

	private Record getLast(Long objId, long langId, SortedSet<Record> recs,
			Object attrIndex) {
		if (recs.size() > 0) {
			long attrId = attr_.id;
			if (attrIndex == null || attrIndex instanceof String) {
				return Funcs.findLast(recs, objId, attrId, langId);
			} else {
				int i = ((Integer) attrIndex).intValue();
				if (i < 0) {
					Record last = Funcs.findLast(recs, objId, attrId, 0);
					if (last == null) {
						return null;
					} else if (i == -1) {
						return last;
					}
					i = last.getIndex() + i + 1;
				}
				return Funcs.find(recs, objId, attrId, 0, i);
			}
		}
		return null;
	}

	public void returnTableSort() {
		Object[] listeners = listeners_.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == OrRefListener.class) {
				if (listeners[i + 1] instanceof TableAdapter) {
					((TableAdapter) listeners[i + 1]).sort();
				} else if (listeners[i + 1] instanceof OrRef) {
					((OrRef) listeners[i + 1]).returnTableSort();
				}
			}
		}
	}

	public int getIndexForObjId(long objId) {
		List<Item> items = getItems(0);
		int i = -1;
		for (Item item : items) {
			i++;
			if (item != null) {
				Object obj = item.getCurrent();
				if (obj instanceof KrnObject && objId == ((KrnObject) obj).id) {
					return i;
				}
			}
		}
		return -1;
	}

	public class Item implements Comparable<Item> {
		public Item parent;

		private List<Item> children;

		public boolean isDeleted;
		public boolean isCheckDeleted;

		private Object value;

		private Record rec;
		
		public int index;
		
		public String title;
		
		public boolean hasChildren;
		private boolean selfChange = false;

		public Item(Item parent, Record value, boolean hasChildren, boolean isCheckDeleted) {
			this.parent = parent;
			this.isDeleted = false;
			this.isCheckDeleted = isCheckDeleted;
			this.rec = value;
			this.hasChildren = hasChildren;
		}

		public Item(Item parent, Record value, boolean hasChildren) {
			this.parent = parent;
			this.isDeleted = false;
			this.rec = value;
			this.hasChildren = hasChildren;
		}

		public Item(Object value) {
			this.value = value;
			this.hasChildren = true;
		}

		public Item(Item parent, Record value, boolean hasChildren, String title) {
			this(parent, value, hasChildren);
			this.title = title;
		}
		
		public Object getCurrent() {
			return (rec != null && (rec.getStatus() & (DELETED | TMPDELETED)) == 0) ? rec
				.getValue()
					: value;
		}

		public Record getRec() {
			return rec;
		}

		public String toString() {
			Object current = getCurrent();
			if (current != null) {
				if (current instanceof KrnObject) {
					return "KrnObject (" + ((KrnObject) current).id + ")";
				} else
					return current.toString();
			}
			return "null";
		}

		// {Berik} Для использования в хэширующих контейнерах
		public boolean equals(Object extObj) {
			if (extObj == this)
				return true;
			if (extObj instanceof Item) {
				Item extItem = (Item) extObj;
				Object extCurrent = extItem.getCurrent();
				Object current = getCurrent();
				if (extCurrent == current) {
					return true;
				} else if (current instanceof KrnObject) {
					if (extCurrent instanceof KrnObject) {
						return ((KrnObject) current).id == ((KrnObject) extCurrent).id;
					}
				} else if (current != null)
					return current.equals(extCurrent);
			}
			return false;
		}

		public int hashCode() {
			int res = 17;
			Object current = getCurrent();
			if (current instanceof KrnObject)
				res = 37 * res + (int) ((KrnObject) current).id;
			else if (current != null)
				res = 37 * res + current.hashCode();
			return res;
		}

		// {Berik}

		// {Berik} Для использования в бинарных контейнерах и двоичного поиска
		// Comarable
		public int compareTo(Item item) {
			Object current = getCurrent();
			if (current instanceof Comparable) {
				return ((Comparable) current).compareTo((Comparable) item
					.getCurrent());
			} else if (current instanceof KrnObject) {
				int myId = (int) ((KrnObject) current).id;
				int id = (int) ((KrnObject) item.getCurrent()).id;
				if (myId < id)
					return -1;
				if (myId > id)
					return 1;
			}
			return 0;
		}

		// {Berik}

		public List<Item> getChildren(Object originator) throws KrnException {
			// Если это не рекурсивный Ref, то возвращаем пустой список
			if (childrenAttr_ == null && childrenExpr_ == null) {
				return Collections.emptyList();
			}
			// Иначе проводим "ленивую" загрузку "детей"
			if (children == null) {
				children = new ArrayList<Item>();
				Object o = getCurrent();
				if (o instanceof KrnObject) {
					Cache cache = getCash();
					if (childrenExpr_ != null) {
						// Загрузка по формуле
				        Map<String, Object> vc = new HashMap<String, Object>();
				        OrFrame oldFrame = ClientOrLang.getFrame();
				        ClientOrLang lng = new ClientOrLang(frm);
				        vc.put("OBJ", o);
				        try {
				            lng.evaluate(childrenExpr_, vc, null, new Stack<String>());
				        } catch (Exception e) {
				        	e.printStackTrace();
				        } finally {
				        	if (oldFrame != null)
				        		ClientOrLang.setFrame(oldFrame);
				        }
				        Object res = vc.get("RETURN");
				        if (res instanceof List) {
							if (hasChildrenAttr_ != null) {
								AttrRequest req = new AttrRequest(null);
								AttrRequest hcReq = new AttrRequest(req);
								hcReq.attrId = hasChildrenAttr_.id;
								long[] objIds = Funcs.makeObjectIdArray((List)res);
								cache.getObjects(objIds, req);
							}
				        	for (KrnObject obj : (List<KrnObject>)res) {
								boolean hasChildren = true;
								if (hasChildrenAttr_ != null) {
									Record hcRec = cache.getRecord(obj.id, hasChildrenAttr_, 0, 0);
									if (hcRec != null) {
										Object hcValue = hcRec.getValue();
										if (hcValue instanceof Number)
											hasChildren = ((Number)hcValue).intValue() == 1;
									}
								}
			        			children.add(new Item(this, cache.findRecord((KrnObject)obj), hasChildren));
				        	}
				        }
					} else {
						// Загрузка по пути
						long[] objIds = { ((KrnObject) o).id };
						long[] filterIds = (defaultFilterId_ > 0) ?
								new long[]{defaultFilterId_} : new long[0];
						AttrRequest req = getRequest(null);
						if (hasChildrenAttr_ != null) {
							AttrRequest hcReq = new AttrRequest(req);
							hcReq.attrId = hasChildrenAttr_.id;
						}
						Set<Long> lostIds = new HashSet<Long>();
						SortedSet<Record> recs = cache.getRecords(
								objIds, childrenAttr_, 0, filterIds, lostIds, null, true, req);
						int parentIndex = index;
						for (Record rec : recs) {
							boolean hasChildren = true;
							if (hasChildrenAttr_ != null) {
								KrnObject obj = (KrnObject)rec.getValue();
								Record hcRec = cache.getRecord(obj.id, hasChildrenAttr_, 0, 0);
								if (hcRec != null) {
									Object hcValue = hcRec.getValue();
									if (hcValue instanceof Number)
										hasChildren = ((Number)hcValue).intValue() == 1;
								}
							}
							Item item = new Item(this, rec, hasChildren);
							item.index = ++parentIndex;
							children.add(item);
						}
					}
				}
			}
			return Collections.unmodifiableList(children);
		}

		public Item createChild(int index, Object originator)
		throws KrnException {
			Cache cache = getCash();
			Record vrec = cache.createObject(type_.id);
			KrnObject obj = (KrnObject)getCurrent();
			selfChange = true;
			try {
				Record rec = cache.insertObjectAttribute(
					obj, childrenAttr_, index, 0, vrec.getValue(), originator);
				Item newItem = new Item(this, rec, false);
				children.add(index, newItem);
				return newItem;
			} finally {
				selfChange = false;
			}
		}

		public void removeChild(Item child, Object originator) throws KrnException {
			Cache cache = getCash();
			selfChange = true;
			try {
				cache.deleteObjectAttribute(child.rec, originator, false);
				children.remove(child);
			} finally {
				selfChange = false;
			}
		}
		
		public int getIndex() {
			return rec.getIndex();
		}
		
		public void reset() {
			if (!selfChange)
				children = null;
		}
	}

	public void absolute(KrnObject obj, Object originator) {
		List<Item> items = getItems(0);
		boolean isFound = false;
		for (int i = 0; i < items.size(); ++i) {
			KrnObject curr = (KrnObject) ((Item) items.get(i)).getCurrent();
			if (curr != null && obj.id == curr.id) {
				absolute(i, originator);
				isFound = true;
				break;
			}
		}
		if (!isFound) {
			fireClear();
			index = values.size(0);
			SortedSet<ObjectRecord> recs = getCash().findRecords(
				new KrnObject[] { obj });
			Item item = new Item(null, recs.first(), true);
			values.add(0, item);
			if (isInOrTable()) {
                            tableAdapter.setSort(false);
                        }
			fireValueChangedEvent(index, originator, 0);
		}
		if (selectedInds_ == null || selectedInds_.length <= 1) {
			setSelectedItems(new int[] { index });
		}
	}

	public void absolute(Item item, Object originator) {
		List<Item> items = getItems(0);
		for (int i = 0; i < items.size(); ++i) {
			if (items.get(i) == item) {
				this.index = i;
			}
		}
		if (selectedInds_ == null || selectedInds_.length <= 1) {
			setSelectedItems(new int[] { index });
		}
	}

	private void setParent(OrRef parent) {
		// Удалаяем себя из списка детей старого родителя
		if (parent_ != null)
			parent_.removeChild(this);
		// Устанавливаем нового родителя
		parent_ = parent;
		// Добавляем себя в список нового родителя
		if (parent_ != null)
			parent_.addChild(this);
	}

	private void addChild(OrRef child) {
		if (children_ == null)
			children_ = new ArrayList<OrRef>();
		children_.add(child);
		addOrRefListener(child);
	}

	public void removeChild(OrRef child) {
		if (children_ != null && children_.size() > 0) {
			children_.remove(child);
			removeOrRefListener(child);
		}
	}

	public void setDirtyTransactions(int dirtyTransactions) {
		this.dirtyTransactions = dirtyTransactions;
	}

	public int getDirtyTransactions() {
		return this.dirtyTransactions;
	}

	private void saveState() {
		savedIndex = index;
		savedIndexes = selectedInds_;
		savedValues = (Values) values.clone();
		if (children_ != null) {
			for (int i = 0; i < children_.size(); ++i)
				((OrRef) children_.get(i)).saveState();
		}
	}

	private void restoreState() {
		values = savedValues;
		absolute(savedIndex, null);
		setSelectedItems(savedIndexes);
		if (children_ != null) {
			for (int i = 0; i < children_.size(); ++i)
				((OrRef) children_.get(i)).restoreState();
		}
	}

	public void setLoaded(boolean isLoaded) {
		isLoaded_ = isLoaded;
	}

	public void addLanguage(long langId) {
		values.addLanguage(langId);
	}

	public boolean hasLanguage(long langId) {
		return values.getLangIds().contains(new Long(langId));
	}

	public class Values implements Cloneable {

		private Map<Long, List<Item>> vals = new HashMap<Long, List<Item>>();

		public void addLanguage(long langId) {
			Long key = new Long(langId);
			if (!vals.containsKey(key)) {
				vals.put(key, new ArrayList<Item>());
			}
		}

		public Set<Long> getLangIds() {
			return vals.keySet();
		}

		public void clear() {
			for (Iterator<Long> it = vals.keySet().iterator(); it.hasNext();) {
				Long langId = it.next();
				List<Item> l = vals.get(langId);
				l.clear();
			}
		}

		public void add(long langId, Item item) {
			List<Item> items = getItems(langId);
			items.add(item);
			item.index = items.size() - 1;
		}

		public void addAll(long langId, int pos, List<Item> items) {
			List<Item> l = getItems(langId);
			l.addAll(pos, items);
			// Обновляем индексы для всех элеметов начиная с pos
			for (int j = pos; j < l.size(); j++) {
				l.get(j).index = j;
			}
		}

		public void add(long langId, int i, Item item) {
			List<Item> items = getItems(langId);
			items.add(i, item);
			// Обновляем индексы для всех элеметов начиная с i
			for (int j = i; j < items.size(); j++) {
				items.get(j).index = j;
			}
		}

		public Item get(long langId, int i) {
			List<Item> items = getItems(langId);
			return (i > -1 && items.size() > i) ? items.get(i) : null;
		}

		public List<Item> get(long langId) {
			return new ArrayList<OrRef.Item>(getItems(langId));
		}
		
		private List<Item> getItems(long langId) {
			List<Item> res = vals.get(langId);
			if (res == null) {
				res = new ArrayList<Item>();
				vals.put(new Long(langId), res);
			}
			return res;
		}

		public int size(long langId) {
			List<Item> l = vals.get(new Long(langId));
			return l != null ? l.size() : 0;
		}

		public void set(long langId, List<Item> l) {
			vals.put(new Long(langId), l);
		}

		public void set(long langId, int i, Item value) {
			List<Item> l = getItems(langId);
			if (i < l.size()) {
				l.set(i, value);
			}
		}

		public void remove(long langId, int i) {
			List<Item> items = getItems(langId);
			if (i < items.size())
				items.remove(i);
		}

		public void remove(long langId, int i, int count) {
			List<Item> items = getItems(langId);
			while (count-- > 0 && i < items.size())
				items.remove(i);
			// Обновляем индексы для всех элеметов начиная с i
			for (int j = i; j < items.size(); j++) {
				items.get(j).index = j;
			}
		}

		public void remove(int i) {
			for (Long langId : vals.keySet()) {
				List<Item> items = getItems(langId);
				if (i < items.size())
					items.remove(i);
			}
		}

		public void remove(long langId, Item item) {
			List<Item> items = getItems(langId);
			if (items.size() > 0)
				items.remove(item);
		}

		public Object clone() {
			try {
				return super.clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
				return null;
			}
		}

		public boolean isEmpty() {
			return vals.isEmpty();
		}
	}

	protected void fireClear() {
		Object[] listeners = listeners_.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == OrRefListener.class) {
				OrRefListener l = (OrRefListener) listeners[i + 1];
				l.clear();
			}
		}
	}

	public void clear() {
		isCleared = true;
		values.clear();
		fireClear();
	}

	public void stateChanged(OrRefEvent e) {
		/*
		 * Integer currentState = (Integer)states.get(new Integer(index)); if
		 * (currentState != null && !currentState.equals(Constants.NO_ERROR)) {
		 * states.remove(new Integer(index)); states.put(new Integer(index),
		 * Constants.NO_ERROR); }
		 */
		fireStateChanged(this);
	}

	public void setFocus(int index, OrRefEvent e) {
		fireSetFocus(index, 0, this);
	}

    private void state_evaluate() {
        Item item = getItem(0);
        if (!isColumn()) {
            if (item != null && item.getCurrent() != null && !item.toString().trim().isEmpty()) {
                for (CheckContext context:checkContextList) {
                    context.setState(new Integer(0), Constants.NO_ERROR);
                }
            }
        } 
    }

	public void objectChanged(Object src, long objId, long attrId) {
		if (src != this) {
			
			if ((childrenAttr_ != null && childrenAttr_.id == attrId)
					|| (hasChildrenAttr_ != null && hasChildrenAttr_.id == attrId)) {
				int i = indexOfObjectItem(objId);
				if (i != -1) {
					getItem(0, i).reset();
					fireItemsUpdated(i, i + 1, this);
				}
			} else if (deletedAttr_ != null && deletedAttr_.id == attrId) {
				try {
        			deletedIndexes.clear();
        			int i = 0;
        			for (Item item : values.getItems(0)) {
        				KrnObject curr = (KrnObject)item.getCurrent();
        				if (curr != null && objId == curr.id) {
        					Record dr = getCash().getRecord(objId, deletedAttr_, 0, 0);
                			if (Long.valueOf(1).equals(dr.getValue()))
                				item.isCheckDeleted = true;
                			else
                				item.isCheckDeleted = false;
        					
        				}
    					if (item.isCheckDeleted)
    						deletedIndexes.add(i);
        				
    					i++;
        			}
					fireItemsChanged(this);
				} catch (KrnException e) {
					log.error(e, e);
				}
			} else {
				try {
					values.clear();
					evaluateItems(0, parent_.getItems(0).size(), true);
					fireItemsChanged(this);
				} catch (KrnException e) {
					log.error(e, e);
				}
			}
		}
	}

	public void objectCreated(Cache cache, long classId, long objId) {
	}

	public void objectDeleted(Cache cache, long classId, long objId) {
	}

    public void statesClear() {
        if (!isChecking) {
            for (CheckContext context : checkContextList) {
                context.clearStates();
            }
        }
    }

	private void setChecking(boolean isChecking) {
		this.isChecking = isChecking;
		Object[] listeners = listeners_.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == OrRefListener.class) {
				Object l = listeners[i + 1];
				if (l instanceof OrRef) {
					((OrRef) l).setChecking(isChecking);
				}
			}
		}
	}

	public void addCheckContext(CheckContext context) {
		//if (allowsLangs || checkContextList.isEmpty()) {
		if (context instanceof TableAdapter)
			checkContextList.add(0, context);
		else
			checkContextList.add(context);
			ASTStart temp = context.getCTemplate();
			if (temp != null) {
				List<String> paths = new ArrayList<String>();
				findPaths(temp, paths, null);
				for (int i = 0; i < paths.size(); ++i) {
					try {
						OrRef.createRef((String) paths.get(i), false,
							Mode.RUNTIME, frm.getRefs(), OrRef.TR_CLEAR, frm);
					} catch (KrnException e) {
						e.printStackTrace();
					}
				}
			}
			// parseConstraintExpression();
			if (context.getReqGroup() > 0) {
				frm.addRefGroup(context.getReqGroup(), context);
			}
		//}
	}

	public void setRecords(SortedSet<Record> recs) {
	}

	public void cacheChanged(Cache cache, Cache newCache) {
		if (attr_ != null) {
			if (cache != null) {
				cache.removeCashChangeListener(this);
			}
			if (newCache != null) {
				newCache.addCashChangeListener(attr_.id, this);
			}
		}
		if (children_ != null) {
			for (OrRef ch : children_) {
				ch.cacheChanged(cache, newCache);
			}
		}
	}

	public void copy(Item parent, Object originator) throws KrnException {
		Set<Long> langs = values.getLangIds();
		KrnObject obj = (KrnObject) parent.getCurrent();
		Cache c = getCash();
		for (Long lang : langs) {
			Item item = getItem(lang);
			if (item != null) {
				c.insertObjectAttribute(obj, attr_, 0, lang, item.getCurrent(),
					originator);
			}
		}
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getLimit() {
		return limit;
	}

	public boolean isLimitExceeded() {
		return limitExceeded;
	}
	
	public KrnAttribute getChildrenAttr() {
		return childrenAttr_;
	}
	
	public void setChildrenAttr(KrnAttribute childrenAttr) {
		this.childrenAttr_ = childrenAttr;
		if (childrenAttr != null)
			getCash().addCashChangeListener(childrenAttr.id, this);
	}

	public void setChildrenExpr(String expr) {
		childrenExpr_ = ClientOrLang.createStaticTemplate(expr);
	}

	public void setHasChildrenAttr(KrnAttribute hasChildrenAttr) {
		this.hasChildrenAttr_ = hasChildrenAttr;
		if (hasChildrenAttr != null)
			getCash().addCashChangeListener(hasChildrenAttr.id, this);
	}
	
	public void setDeletedAttr(KrnAttribute attr) {
		this.deletedAttr_ = attr;
		if (attr != null)
			getCash().addCashChangeListener(attr.id, this);
	}

	public TableAdapter getTableAdapter() {
        return tableAdapter;
    }
    
    public Item getRootItem() {
    	return rootItem;
    }
    
    public void insertItems(int pos, List<Item> items, Object originator) {
    	values.addAll(0, pos, items);
    	fireItemsInserted(pos, pos + items.size(), originator);
    }

    public void deleteItems(int startPos, int endPos, Object originator) {
    	values.remove(0, startPos, endPos - startPos);
    	fireItemsDeleted(startPos, endPos, originator);
    }
    
    public void setItems(long langId, List<Item> items, Object originator) {
    	values.set(langId, items);
    	if (deletedAttr_ != null) {
    		deletedIndexes.clear();
    		for (int i=0; i<items.size(); i++) {
    			if (items.get(i).isCheckDeleted)
    				deletedIndexes.add(i);
    		}
    	}

    	boolean calcOwner = OrCalcRef.setCalculations();
    	fireItemsChanged(originator);
		if (calcOwner)
			OrCalcRef.makeCalculations();
    	exitPrevFire = true;
    }

    private int indexOfObjectItem(long objId) {
		List<Item> items = getItems(0);
		for (int i = 0; i < items.size(); i++) {
			KrnObject curr = (KrnObject)items.get(i).getCurrent();
			if (curr != null && objId == curr.id) {
				return i;
			}
		}
		return -1;
    }
    
    private AttrRequest getRequest(AttrRequest preq) {
		AttrRequest req = null;
    	if (preq == null
    			|| (attr_ != null && attr_.collectionType == COLLECTION_NONE
    			&& attr_.rAttrId == 0)) {
    		if (preq == null) {
    			req = new AttrRequest(null);
	    		if (children_ != null && children_.size() > 0) {
	    			for (OrRef child : children_) {
	    				child.getRequest(req); 
	    			}
	    		}
	    		if (deletedAttr_ != null) {
	    			boolean hasDeletedAttr = false;
	    			List<AttrRequest> chs = req.getChildren();
	    			for (AttrRequest ch : chs) {
	    				if (ch.attrId == deletedAttr_.id) {
	    					hasDeletedAttr = true;
	    					break;
	    				}
	    			}
	    			if (!hasDeletedAttr) {
		    			AttrRequest dreq = new AttrRequest(req);
			    		dreq.attrId = deletedAttr_.id;
	    			}
	    		}
    		} else {
    			if (FETCH_DEPTH == 0) {
					Set<Long> langIds = values.getLangIds();
					for (Long langId : langIds) {
			    		req = new AttrRequest(preq);
			    		req.attrId = attr_.id;
						req.langId = langId;
	
						if (children_ != null && children_.size() > 0) {
			    			for (OrRef child : children_) {
			    				child.getRequest(req); 
			    			}
			    		}
					}
    			}
    		}
    	}
    	return req;
    }
    
	public List<Record> getItemsFor(OrRef rootRef, long[] objIds) throws KrnException {
		List<OrRef> refs = new ArrayList<OrRef>();
		OrRef r = this;
		while (r != rootRef) {
			refs.add(r);
			r = r.getParent();
		}
		for (int i = refs.size() - 1; i>0; i--) {
			r = refs.get(i);
			List<Record> recs = r.getItemsFor(objIds);
			if (recs != null) {
				int k = 0;
				for (Iterator<Record> it = recs.iterator(); it.hasNext(); ) {
					objIds[k++] = ((KrnObject)it.next().getValue()).id;
				}
			}
			else
				return Collections.emptyList();
		}
		return refs.get(0).getItemsFor(objIds);
	}

	private List<Record> getItemsFor(long[] objIds) throws KrnException {
		List<Record> recList = new ArrayList<Record>(objIds.length);
		Cache cache = getCash();
		SortedSet<Record> recs = cache.getRecords(objIds, attr_, langId_, null);
		for (Record rec : recs) {
			recList.add(rec);
		}
		return Collections.unmodifiableList(recList);
	}

	public Record getItemFor(OrRef rootRef, long objId) throws KrnException {
		List<OrRef> refs = new ArrayList<OrRef>();
		OrRef r = this;
		while (r != rootRef) {
			refs.add(r);
			r = r.getParent();
		}
		for (int i = refs.size() - 1; i>0; i--) {
			r = refs.get(i);
			Record rec = r.getItemFor(objId);
			if (rec != null) {
				objId = ((KrnObject)rec.getValue()).id;
			}
			else
				return null;
		}
		return refs.get(0).getItemFor(objId);
	}

	private Record getItemFor(long objId) throws KrnException {
		Cache cache = getCash();
		SortedSet<Record> recs = cache.getRecords(new long[] {objId}, attr_, langId_, null);
		if (recs.size() > 0) {
			return recs.first();
		}
		return null;
	}
	
	/**
	     * Добавление объекта в список выбранных объектов
	     *
	     * @param obj объект типа KrnObject 
	     */
	    public void addSelItem(KrnObject obj) {
	        if (selItems == null) {
	            selItems = new ArrayList<Item>();
	        }
	        if (obj != null) {
	            selItems.add(new Item(obj));
	        }
	    }

	    /**
	     * Удаление объекта из списка выбранных объектов
	     *
	     * @param obj объект типа KrnObject
	     */
	    public void removeSelItem(KrnObject obj) {
	        if (selItems != null) {
	            Iterator<Item> iter = selItems.iterator();
	            while(iter.hasNext()){
	                Item next = iter.next();
	                if(obj.equals(next.getCurrent()))
	                    iter.remove();
	                }
	        }
	    }

	    /**
	     * Проверка, есть ли объект в списке выбранных.
	     *
	     * @param obj объект, который ищется.
	     * @return true, если есть в списке.
	     */
	    public boolean isInSelItem(KrnObject obj) {
	        if (selItems != null) {
	            for (Item item : selItems) {
	                if (obj.equals(item.getCurrent())) {
	                    return true;
	                }
	            }
	        }
	        return false;
	    }

	    /**
	     * Очистка списка выбранных объектов.
	     */
	    public void clearSelItem() {
	        if (selItems != null) {
	            selItems.clear();
	        }
	    }

	    /**
	     * Возврат список выбранных объектов.
	     * 
	     * @return the selItems
	     */
	    public List<Item> getSelItems() {
	        return selItems;
	    }

	    /**
	     * Задание список выбранных объектов
	     *
	     * @param selItems the selItems to set
	     */
	    public void setSelItems(List<Item> selItems) {
	        this.selItems = selItems;
	    }
	    
	    public void addValue(long langId, Item item) {
	    	values.add(langId, item);
	    	if (deletedAttr_ != null && item.isCheckDeleted)
	    		deletedIndexes.add(values.size(langId) - 1);
	    }
	    
	    protected void clearValues() {
	    	values.clear();
	    	deletedIndexes.clear();
	    }
}
