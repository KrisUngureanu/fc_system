package kz.tamur.rt.adapters;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import kz.tamur.comps.*;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.guidesigner.filters.FilterRecord;
import kz.tamur.or3.client.comps.interfaces.OrRadioBoxComponent;
import kz.tamur.rt.adapters.OrRef.Item;
import kz.tamur.rt.orlang.ClientOrLang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class RadioBoxAdapter extends ComponentAdapter {

    private OrRadioBoxComponent radioBox;
    private OrRef contentRef;
    private OrRadioItem[] radioitems;
    private int oldSelectedindex = -1;

    public RadioBoxAdapter(OrFrame frame, OrRadioBoxComponent radioBx, boolean isEditor)
            throws KrnException {
        super(frame, radioBx, isEditor);
        this.radioBox = radioBx;
        PropertyNode proot = radioBox.getProperties();
        int refreshMode = 0;
        PropertyNode rprop = proot.getChild("ref").getChild("refreshMode");
        PropertyValue pv = radioBox.getPropertyValue(rprop);
        if (!pv.isNull()) {
            refreshMode = pv.intValue();
        }
        
     // установка фильтра по умолчанию.
        pv = radioBox.getPropertyValue(proot.getChild("ref").getChild("defaultFilter"));
        FilterRecord fRecord = null;
        if (!pv.isNull()) {
            try {
                fRecord = pv.filterValue();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        rprop = proot.getChild("ref").getChild("content");
        pv = radioBox.getPropertyValue(rprop);
        if (!pv.isNull()) {
            if (refreshMode == Constants.RM_DIRECTLY) {
                contentRef = OrRef.createRef(pv.stringValue(frame.getKernel()), true, Mode.RUNTIME, frame.getRefs(),
                        frame.getTransactionIsolation(), frame);
            } else {
                contentRef = OrRef.createContentRef(pv.stringValue(frame.getKernel()), refreshMode, Mode.RUNTIME,
                         frame.getTransactionIsolation(), frame);
            }
            contentRef.addOrRefListener(this);
            
            if (fRecord != null) {
                try {
                    KrnObject[] fobjs = {fRecord.getKrnObject()};
                    Kernel krn = frame.getKernel();
                    String[] strs = krn.getStrings(fobjs[0], "className", 0, 0);
                    KrnClass cls = krn.getClassByName(strs[0]);
                    contentRef.getParentOfClass(cls.id).setDefaultFilter(fRecord.getObjId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        PropertyNode prop = radioBox.getProperties();
        pv = radioBox.getPropertyValue(prop.getChild("ref").getChild("defaultFilter"));
        if (!pv.isNull()) {
            contentRef.setDefaultFilter(pv.filterValue().getObjId());
        }
        this.radioBox.setXml(null);
    }

    // RefListener
    public void valueChanged(OrRefEvent e) {
        super.valueChanged(e);
        if (e.getOriginator() != this) {
            try {
                OrRef ref = e.getRef();
                if (ref == contentRef) {
                    radioBox.removeAllButtons();
                    List<Item> items = new ArrayList<Item>(contentRef.getItems(langId));
                    Collections.sort(items);
                    radioitems = new OrRadioItem[items.size()];
                    for (int i = 0; i < items.size(); ++i) {
                        Item item = items.get(i);
                        Object value = item.getCurrent();
                        if (value != null) {
                            radioitems[i] = new OrRadioItem(getParentObject(ref, item), (String) value);
                        }
                    }
                    radioBox.setItems(radioitems);
                    update(false);
                    radioBox.optionsChanged();
                } else if (ref == dataRef || ref == calcRef){
                    update(false);
                }
            } catch (KrnException e1) {
                e1.printStackTrace();
            }
        }
    }

    public void clear() {
    	oldSelectedindex = -1;
    }

    public void select(int selectedindex) {
        try {
        	boolean calcOwner = OrCalcRef.setCalculations();
        	try {
	            KrnObject rdbitem = radioitems[selectedindex].getObject();
        		if (dataRef != null) {
		            OrRef.Item item = dataRef.getItem(langId);
		            if (item == null && radioitems[selectedindex] != null && rdbitem != null) {
		                dataRef.insertItem(0, rdbitem, this, this, false);
		            } else
		                dataRef.changeItem(rdbitem, this, this);
        		}
	            updateParamFilters(rdbitem);
        	} catch (Exception e) {
        		log.error(e, e);
        	} finally {
				if (calcOwner)
					OrCalcRef.makeCalculations();
        	}

            if (oldSelectedindex != selectedindex) {
                if (afterModAction != null) {
                    ClientOrLang orlang = new ClientOrLang(RadioBoxAdapter.this.frame);
                    Map<String, Object> vc = new HashMap<String, Object>();

                    if (dataRef != null && dataRef.isColumn()) {
                        OrRef p = dataRef;
                        while (p != null && p.isColumn()) {
                            p = p.getParent();
                        }
                        if (p != null && p.getItem(0) != null) {
                            Object obj = p.getItem(0).getCurrent();
                            vc.put("SELOBJ", obj);
                        }
                    }
                    calcOwner = OrCalcRef.setCalculations();
                    try {
                        orlang.evaluate(afterModAction, vc, RadioBoxAdapter.this, new Stack<String>());
                    } catch (Exception ex) {
                        Util.showErrorMessage(RadioBoxAdapter.this.radioBox, ex.getMessage(), "Действие после модификации");
                    	log.error("Ошибка при выполнении формулы 'Действие после модификации' компонента '" + (comp != null ? comp.getClass().getName() : "") + "', uuid: " + getUUID());
                        log.error(ex, ex);
                    } finally {
                        if (calcOwner)
                            OrCalcRef.makeCalculations();
                    }
                }
                oldSelectedindex = selectedindex;
            }

        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public class OrRadioItem implements Comparable {
        private KrnObject object_;
        private String title_;

        public OrRadioItem(KrnObject object, String title) {
            object_ = object;
            title_ = title;
        }

        public KrnObject getObject() {
            return object_;
        }

        public String toString() {
            return title_;
        }

        public int compareTo(Object o) {
            if (o != null && o instanceof OrRadioItem) {
                String title = ((OrRadioItem) o).title_;
                if (title != null)
                    return title_.compareTo(title);
                if (title_ == title)
                    return 0;
                if (title_ == null)
                    return -1;
            }
            return 1;
        }

        public boolean equals(Object obj) {
            boolean res = false;
            if (obj == null && object_ == null)
                res = true;
            else if (obj != null) {
                if (object_ == null)
                    res = (((OrRadioItem) obj).object_ == null);
                else
                    res = object_.equals(((OrRadioItem) obj).object_);
            }
            return res;
        }
    }

    private KrnObject getParentObject(OrRef ref, OrRef.Item item)
            throws KrnException {
        KrnClass type = dataRef != null ? dataRef.getType() : null;
        if (type == null)
            type = ref.getRoot().getType();

        OrRef.Item pitem = item;
        OrRef pref = ref;
        KrnClass currType = pref.getType();

        while (type.id != currType.id && pitem.parent != null) {
            pref = pref.getParent();
            pitem = pitem.parent;
            currType = pref.getType();
        }

        return (KrnObject) pitem.getCurrent();
    }

    public OrRef getContentRef() {
    	return contentRef;
    }
    
    private void update(boolean isContent) {
        if (!isContent) {
        	OrRef ref = dataRef != null ? dataRef : calcRef;
        	if (ref != null) {
	            OrRef.Item refitem = ref.getItem(langId);
	            if (refitem != null) {
	                KrnObject obj = (KrnObject) refitem.getCurrent();
	                if (obj != null && radioitems != null) {
	                    radioBox.select(obj, radioitems);
	                } else if (obj == null) radioBox.clearAllSelection();
	            } else
	                radioBox.clearAllSelection();
        	}
        }
    }

    @Override
    public void filterParamChanged(String fuid, String pid, List<?> values) {
    	if (pid.equals(paramName)) {
	        value = values != null && values.size() > 0 ? values.get(0) : null;
	        if (value instanceof KrnObject)
	        	radioBox.select((KrnObject)value, radioitems);
	        else
	        	radioBox.clearAllSelection();
        }
    }

    public void setEnabled(boolean isEnabled) {
        super.setEnabled(isEnabled);
        radioBox.setEnabled(isEnabled);
    }
}
