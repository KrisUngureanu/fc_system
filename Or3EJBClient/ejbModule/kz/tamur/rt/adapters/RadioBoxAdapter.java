package kz.tamur.rt.adapters;

import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.util.expr.Editor;
import com.cifs.or2.client.Kernel;
import kz.tamur.comps.*;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.guidesigner.filters.FilterRecord;
import kz.tamur.lang.OrLang;
import kz.tamur.rt.adapters.OrRef.Item;
import kz.tamur.rt.orlang.ClientOrLang;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class RadioBoxAdapter extends ComponentAdapter implements ActionListener {

    private OrRadioBox radioBox;
    private OrRef contentRef;
    private OrRadioItem[] radioitems;
    private JRadioButton clear;
    private int oldSelectedindex=-1;
    public RadioBoxAdapter(UIFrame frame, OrRadioBox radioBx, boolean isEditor)
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
                contentRef = OrRef.createRef(pv.stringValue(), true, Mode.RUNTIME, frame.getRefs(),
                        frame.getTransactionIsolation(), frame);
            } else {
                contentRef = OrRef.createContentRef(pv.stringValue(), refreshMode, Mode.RUNTIME,
                         frame.getTransactionIsolation(), frame);
            }
            contentRef.addOrRefListener(this);
            if (fRecord != null) {
                try {
                    KrnObject[] fobjs = {fRecord.getKrnObject()};
                    Kernel krn = Kernel.instance();
                    String[] strs = krn.getStrings(fobjs[0], "className", 0, 0);
                    KrnClass cls = krn.getClassByName(strs[0]);
                    contentRef.getParentOfClass(cls.id).setDefaultFilter(fRecord.getObjId());
                    String fuid = fRecord.getKrnObject().uid;
                    krn.addFilterParamListener(fuid, "", this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        clear = new JRadioButton("clear");
        radioBox.btnGroup.add(clear);
        //this.radioBox.add(clear);
        clear.setVisible(false);
        kz.tamur.rt.Utils.setComponentFocusCircle(this.radioBox);
        PropertyNode prop = radioBox.getProperties();
        pv = radioBox.getPropertyValue(prop.getChild("ref").getChild("defaultFilter"));
        if (!pv.isNull()) {
            contentRef.setDefaultFilter(pv.filterValue().getObjId());
        }
        
        pv = radioBox.getPropertyValue(proot.getChild("pov").getChild("afterModAction"));
        String afterExpr = null;
        if (!pv.isNull()) {
            afterExpr = pv.stringValue();
        }
        if (afterExpr != null && afterExpr.length() > 0) {
            afterModAction = OrLang.createStaticTemplate(afterExpr);
            try {
                Editor e = new Editor(afterExpr);
                ArrayList<String> paths = e.getRefPaths();
                for (int j = 0; j < paths.size(); ++j) {
                    String path = paths.get(j);
                    OrRef.createRef(path, false, Mode.RUNTIME, frame.getRefs(),
                            OrRef.TR_CLEAR, frame);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
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
                        OrRef.Item item = (OrRef.Item) items.get(i);
                        Object value = item.getCurrent();
                        if (value != null) {
                            radioitems[i] = new OrRadioItem(getParentObject(ref, item), (String) value);
                            JRadioButton btn = new JRadioButton(value.toString());
                            btn.setFont(radioBox.getDesFont());
                            btn.setBackground(radioBox.getDesBackground());
                            btn.setForeground(radioBox.getDesForeground());
                            radioBox.btnGroup.add(btn);
                            btn.setOpaque(false);
                            radioBox.add(btn);
                            btn.addActionListener(this);
                        }
                    }
                    clear = new JRadioButton("clear");
                    radioBox.btnGroup.add(clear);
                    //radioBox.add(clear);
                    clear.setVisible(false);
                    radioBox.setBoxLayout(items.size());
                    update(false);
                } else if (ref == dataRef){
                    update(false);
                }
                
            } catch (KrnException e1) {
                e1.printStackTrace();
            }
        }
    }

    public void clear() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void actionPerformed(ActionEvent e) {
        try {
            boolean calcOwner = OrCalcRef.setCalculations();
            OrRef.Item item = dataRef.getItem(langId);
            int selectedindex = getSelectedIndex(radioBox.btnGroup);
            KrnObject rdbitem = radioitems[selectedindex].getObject();
            if (item == null && radioitems[selectedindex] != null && rdbitem != null) {
                dataRef.insertItem(0, rdbitem, this, this, false);
            } else {
                dataRef.changeItem(rdbitem, this, this);
            }
            updateParamFilters(rdbitem);
            if (calcOwner) {
                OrCalcRef.makeCalculations();
            }
            if (oldSelectedindex != selectedindex) {
                if (afterModAction != null) {
                    ClientOrLang orlang = new ClientOrLang(RadioBoxAdapter.this.frame);
                    Map vc = new HashMap();

                    if (dataRef.isColumn()) {
                        OrRef p = dataRef;
                        while (p != null && p.isColumn()) {
                            p = p.getParent();
                        }
                        if (p != null && p.getItem(0) != null) {
                            Object obj = p.getItem(0).getCurrent();
                            vc.put("SELOBJ", obj);
                        }
                    }
                    try {
                        calcOwner = OrCalcRef.setCalculations();
                        orlang.evaluate(afterModAction, vc, RadioBoxAdapter.this, new Stack<String>());
                        if (calcOwner)
                            OrCalcRef.makeCalculations();
                    } catch (Exception ex) {
                        Util.showErrorMessage(RadioBoxAdapter.this.radioBox, ex.getMessage(), "Действие после модификации");
                    }
                }
                oldSelectedindex = selectedindex;
            }
        } catch (KrnException e1) {
            e1.printStackTrace();
        }
    }

    protected void updateParamFilters(Object value) {
        try {
            if (value != null && value.toString().length() == 0) {
                value = null;
            }
            if (paramFiltersUIDs != null && paramFiltersUIDs.length > 0) {
                for (int i = 0; i < paramFiltersUIDs.length; i++) {
                    String paramFiltersUID = paramFiltersUIDs[i];
                    if (value instanceof List) {
                        Kernel.instance().setFilterParam(paramFiltersUID, paramName, (List)value);
                    } else {
                        Kernel.instance().setFilterParam(paramFiltersUID, paramName, value!=null ? Collections.singletonList(value):null);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                if (title != null && title != null)
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
        KrnClass type = dataRef.getType();
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


    private void update(boolean isContent) {
        if (!isContent) {
            OrRef.Item refitem = dataRef.getItem(langId);
            if (refitem != null) {
                KrnObject obj = (KrnObject) refitem.getCurrent();
                if (obj != null && radioitems != null) {
                    int i;
                    i = 0;
                    for (Enumeration e = radioBox.btnGroup.getElements(); e.hasMoreElements() && i < radioitems.length;) {
                        JRadioButton item_ = (JRadioButton) e.nextElement();
                        if (!item_.getText().equals("clear")) {
                            KrnObject radiobtn = radioitems[i].getObject();
                            ++i;
                            if (radiobtn.id == obj.id) {
                                item_.setSelected(true);
                                // updateTip();
                            }
                        }
                    }
                } else if (obj == null)
                    clearAllSelection(radioBox.btnGroup);
            } else {
                clearAllSelection(radioBox.btnGroup);
            }
        }
    }

    public void clearAllSelection(ButtonGroup group) {
        clear.setSelected(true);
    }

    private int getSelectedIndex(ButtonGroup group) {
        int i;
        i = 0;
        for (Enumeration e = group.getElements(); e.hasMoreElements();) {
            JRadioButton b = (JRadioButton) e.nextElement();
            if (!b.getText().equals("clear")) {
                if (b.getModel() == group.getSelection()) {
                    return i;
                }
                ++i;
            }
        }
        return -1;
    }

    public void setEnabled(boolean isEnabled) {
        super.setEnabled(isEnabled);
        radioBox.setEnabled(isEnabled);
    }
}
