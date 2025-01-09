package kz.tamur.rt.adapters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import kz.tamur.comps.OrFrame;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.lang.FilterDatesListener;
import kz.tamur.lang.OrLang;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.or3.util.PathElement2;
import kz.tamur.rt.CheckContext;
import kz.tamur.rt.data.Cache;
import kz.tamur.rt.orlang.ClientOrLang;
import kz.tamur.util.Funcs;

import com.cifs.or2.client.FilterParamListener;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.Utils;
import com.cifs.or2.kernel.Date;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.util.MultiMap;
import com.cifs.or2.util.expr.Editor;

public class OrCalcRef extends OrRef implements FilterParamListener, FilterDatesListener {
    ASTStart template;
    Map<String, OrRef> parents = new TreeMap<String, OrRef>();
    private boolean isSelfChangeing_ = false;
    private OrRef tableRef_;
    private OrGuiComponent comp;
    private String propertyName;
    private OrRef reportRef_;
    private CheckContext ctx;
    private static boolean debug = "1".equals(Funcs.normalizeInput(System.getProperty("showMessage")));

    public OrCalcRef(
    		String expr,
    		boolean isColumn,
    		int mode,
    		Map<String, OrRef> refs,
    		int dirtyTransactions,
    		OrFrame frame,
    		OrGuiComponent comp,
    		String propertyName,
    		CheckContext ctx
    		) throws KrnException {
    	this.frm = frame;
        this.isColumn = isColumn;
        this.template = OrLang.createStaticTemplate(expr);
        this.comp = comp;
        this.propertyName = propertyName;
        this.ctx = ctx;
        this.allowsLangs = true;
        if (ctx.getLangId() > 0) {
            values.addLanguage(ctx.getLangId());
        }
        List<String> refPaths = new ArrayList<String>();
        List<String> attrPaths = new ArrayList<String>();
        findPaths(template, refPaths, attrPaths);
        for (int i = 0; i < refPaths.size(); ++i) {
            String path = (String) refPaths.get(i);
            if (!parents.containsKey(path)) {
                OrRef pref = OrRef.createRef(path, isColumn, mode, refs,
                        dirtyTransactions, frame);
                pref.addOrRefListener(this);
                parents.put(path, pref);
            }
        }
        Cache cash = frame.getCash();
        for (int i = 0; i < attrPaths.size(); ++i) {
            String path = (String) attrPaths.get(i);
			PathElement2[] ps = Utils.parsePath2(path);
            cash.addCashChangeListener(
                    (int)ps[ps.length - 1].attr.id, this);
        }
        Editor e = new Editor(expr);
        MultiMap filterParams = e.getFilterParams();
        for (Iterator it = filterParams.keySet().iterator(); it.hasNext(); ) {
            String fuid = (String) it.next();
            Collection params = filterParams.get(fuid);
            for (Iterator pit = params.iterator(); pit.hasNext();) {
                String param = (String) pit.next();
                Kernel.instance().addFilterParamListener(fuid, param, this);
            }
        }

        boolean hasDateOps = e.hasDateOps();
        if (hasDateOps) {
            ((ClientOrLang) new ClientOrLang(frame)).getDateOp().addFilterDatesListener(this);
        }
    }

    public OrCalcRef(
    		String path,
    		String expr,
    		boolean isColumn,
    		int mode,
    		Map<String, OrRef> refs,
            int dirtyTransactions,
            OrFrame frame,
            OrGuiComponent comp,
            String propertyName,
            CheckContext ctx
            ) throws KrnException {
    	this(expr, isColumn, mode, refs, dirtyTransactions, frame, comp, propertyName, ctx);
    	PathElement2[] pes2 = Utils.parsePath2(path);
    	this.type_ = pes2[0].type;
    	frame.getContentRef().put(path, this);
    }
    
    void proto_evaluate(int pos, boolean isInserting) throws KrnException {
        values.clear();
        if (isColumn) {
            if (tableRef_ != null) {
                List<Item> l = tableRef_.getItems(0);
                if (l != null) {
                    for (int p = 0; p < l.size(); ++p) {
                        calculateItem(p);
                    }
                }
            }
        } else {
            calculateItem(pos);
        }
    }

    private boolean calculateItem(int pos) throws KrnException {
        boolean res = false;
        Map<String, Object> vc = new HashMap<String, Object>();
        OrFrame oldFrame = ClientOrLang.getFrame();
        ClientOrLang lng = new ClientOrLang(frm);
        if (tableRef_ != null && tableRef_.getItem(0, pos) != null) {
            vc.put("OBJ", tableRef_.getItem(0, pos).getCurrent());
            
            OrRef parent = tableRef_.getParent();
            List<Object> parentObjs = new ArrayList<Object>();
            List<Object> objs = new ArrayList<Object>();
            while (parent != null) {
            	parentObjs.add(parent.getItem(0).getCurrent());
            	parent = parent.getParent();
            }
            vc.put("PARENTS", parentObjs);
            for (Item item : tableRef_.getItems(0)) {
            	objs.add(item.getCurrent());
            }
            vc.put("OBJS", objs);
        } else if (reportRef_ != null && reportRef_.getItem(0) != null) {
            vc.put("OBJ", reportRef_.getItem(0).getCurrent());
        }
        try {
            lng.evaluate(template, vc, ctx, new Stack<String>());
        } catch (Exception e) {
        	e.printStackTrace();
            if (debug)
                Util.showErrorMessage(comp, e.getMessage(), propertyName);               
        } finally {
        	if (oldFrame != null)
        		ClientOrLang.setFrame(oldFrame);
        }
        Object val = vc.get("RETURN");
        values.add(ctx.getLangId(),new Item(val));
        //if (tableRef_ != null)
        //    tableRef_.absolute(oldIndex, this);
        return res;
    }

    public void clear() {
    }

    public void valueChanged(OrRefEvent e) {
        if (isColumn && (e.getReason() & OrRefEvent.ITERATING) > 0 &&
                (e.getRef() == null || parents.containsKey(e.getRef().toString()))) {
        	index = e.getIndex();
            fireValueIteratedEvent(e.getOriginator());
            return;
        }
        if (!isSelfChangeing_ && !(e.getOriginator() instanceof OrCalcRef) && e.getReason() != OrRefEvent.ROOT_ITEM_CHANGED) {
        	
        	OrCalcRef.addCalculation(this);
        	/*
            isSelfChangeing_ = true;
            try {
                if (isColumn)
                    index = e.getIndex();
                proto_evaluate(index, false);
                fireValueChangedEvent(e.getIndex(), e.getOriginator(), 0);
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                isSelfChangeing_ = false;
            }
            */
        }
    }

    public void refresh(Object originator) {
        //valueChanged(new OrRefEvent(this, OrRefEvent.CHANGED, index, originator));
    	calculate();
    }

    public void setTableRef(OrRef ref) {
        tableRef_ = ref;
    }

    public void setReportRef(OrRef ref) {
        reportRef_ = ref;
    }

    public boolean hasParents() {
        return (parents.size() > 0);
    }

    public OrRef.Item getItem() {
        if (!hasParents() && values.isEmpty()) {
            try {
                proto_evaluate(index, false);
            } catch (KrnException e) {
                e.printStackTrace();
            }
        }
        return super.getItem(ctx.getLangId());
    }

    public void filterParamChanged(String fuid, String pid, List<?> values) {
        try {
            proto_evaluate(0, false);
            fireValueChangedEvent(0, this, 0);
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }

    public void clearParam() {
        try {
            proto_evaluate(0, false);
            fireValueChangedEvent(0, this, 0);
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }

    public void objectChanged(Object src, long objId, long attrId) {
        if (!isSelfChangeing_ && !(src instanceof OrCalcRef)) {
            isSelfChangeing_ = true;
            try {
                proto_evaluate(index, false);
                fireValueChangedEvent(index, this, 0);
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                isSelfChangeing_ = false;
            }
        }
    }

    public int getParentsSize() {
        return parents.size();
    }

    public void removeFromParents() {
        for (Iterator it = parents.keySet().iterator(); it.hasNext(); ) {
            String key = (String) it.next();
            OrRef ref = (OrRef) parents.get(key);
            if (ref != null) {
                ref.removeOrRefListener(this);
            }
        }
    }

	public void changed(Map<Integer, Date> filterDates) {
		refresh(null);
	}

	private static ThreadLocal<List<OrCalcRef>> calculations = new ThreadLocal<List<OrCalcRef>>();

	public static void addCalculation(OrCalcRef ref) {
		List<OrCalcRef> list = calculations.get();
		if (!list.contains(ref))
			list.add(ref);
	}
	
	public static void setCalculations(List<OrCalcRef> calcs) {
		calculations.set(calcs);
	}

	public static boolean setCalculations() {
		if (calculations.get() != null)
			return false;
		calculations.set(new ArrayList<OrCalcRef>());
		return true;
	}
	
	public static List<OrCalcRef> removeCalculations() {
		List<OrCalcRef> res = calculations.get();
		calculations.remove();
		return res;
	}

	public void calculate() {
        isSelfChangeing_ = true;
        try {
            proto_evaluate(0, false);
            fireValueChangedEvent(0, this, 0);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            isSelfChangeing_ = false;
        }
    }

	public static void makeCalculations() {
		List<OrCalcRef> calcs = calculations.get();
        while (calcs.size() > 0) {
            List<OrCalcRef> calcs2 = new ArrayList<OrCalcRef>();
            calculations.set(calcs2);
            for (OrCalcRef calc : calcs) {
            	calc.calculate();
            }
            calcs.removeAll(calcs2);
            if (calcs.size() > 0)
            	calcs = calcs2;
        }
        calculations.remove();
	}
}
