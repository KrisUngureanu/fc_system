package kz.tamur.rt.adapters;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Stack;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import kz.tamur.comps.Constants;
import kz.tamur.comps.Mode;
import kz.tamur.comps.OrCellEditor;
import kz.tamur.comps.OrComboBox;
import kz.tamur.comps.OrFrame;
import kz.tamur.comps.OrTable;
import kz.tamur.comps.OrTableModel;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.ui.comboBox.OrComboBoxCellRenderer;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.guidesigner.filters.FilterRecord;
import kz.tamur.rt.InterfaceManager;
import kz.tamur.rt.RadioGroupManager;
import kz.tamur.rt.orlang.ClientOrLang;
import kz.tamur.util.Funcs;

import com.cifs.or2.client.FilterParamListener;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.util.expr.Editor;

public class ComboBoxAdapter extends ComponentAdapter implements ActionListener, ListSelectionListener, FilterParamListener {

    private OrComboBox comboBox;
    private boolean selfChange = false;
    private OrRef contentRef;
    private OrRef contentRefSort;
    private OrCalcRef contentCalcExpr = null;
    private OrRef[] contentRefs;
    private int parentsSize = 4;
    private RadioGroupManager groupManager = new RadioGroupManager();
    private OrCellEditor editor_;
    private boolean commitCell = true;
    private OrComboItem lastItem = null;
    private Font font;
    private boolean focused = false;
    private boolean sorted = true;
    private boolean isStructCls = false;
    private KrnObject nullKrnObject=new KrnObject(-1,"",-1); 
    private Map<KrnObject,Object> itemMap=new HashMap<>();
    private List<OrComboItem> citems;
    private int contentRefPass = 0;
    
    public ComboBoxAdapter(OrFrame frame, OrComboBox comboBox_, boolean isEditor) throws KrnException {
        super(frame, comboBox_, isEditor);
        this.comboBox = comboBox_;

        Kernel krn = Kernel.instance();

        PropertyNode proot = comboBox.getProperties();
        int refreshMode = 0;
        PropertyNode rprop = proot.getChild("ref").getChild("refreshMode");
        PropertyValue pv = comboBox.getPropertyValue(rprop);
        if (!pv.isNull()) {
            refreshMode = pv.intValue();
        }

        // установка фильтра по умолчанию.
        pv = comboBox.getPropertyValue(proot.getChild("ref").getChild("defaultFilter"));
        FilterRecord fRecord = null;
        if (!pv.isNull()) {
            try {
                fRecord = pv.filterValue();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        //  свойство (данные.содержимое)
        rprop = proot.getChild("ref").getChild("contentSort");
        // значение (Содержание.наименование)
        pv = comboBox.getPropertyValue(rprop);
        
              
        if (!pv.isNull()) {
            propertyName = "Свойство: contentSort";
            try {
                if (refreshMode == Constants.RM_DIRECTLY) {
                    String path = pv.stringValue();
                    // Создание рефа (связи с объектами) для значений сортировки
                    contentRefSort = OrRef.createRef(path, false, Mode.RUNTIME, frame.getRefs(), frame.getTransactionIsolation(),
                            frame);

                    OrRef r = contentRefSort;
                    while (r.getParent() != null && (dataRef == null || dataRef.getType().id != r.getType().id)) {
                        r.setColumn(true);
                        r = r.getParent();
                    }
                    if (frame.getContentRef().get(path) == null) {
                        OrRef tempRef = contentRefSort;
                        frame.getContentRef().put(path, tempRef);
                    }
                } else {
                    if (fRecord != null && fRecord.getObjId() > 0) {
                    	contentRefSort = OrRef.createContentRef(pv.stringValue(), fRecord.getObjId(), refreshMode, Mode.RUNTIME,
                                frame.getTransactionIsolation(), frame);
                    } else {
                    	contentRefSort = OrRef.createContentRef(pv.stringValue(), refreshMode, Mode.RUNTIME,
                                frame.getTransactionIsolation(), frame);
                    }
                }
                contentRefSort.addOrRefListener(this);

            } catch (Exception e) {
                if (e instanceof RuntimeException) {
                    showErrorNessage(e.getMessage());
                }
                e.printStackTrace();
            }
        }

        //  свойство (данные.содержимое)
        rprop = proot.getChild("ref").getChild("content");
        // значение (Содержание.наименование)
        pv = comboBox.getPropertyValue(rprop);
        
              
        if (!pv.isNull()) {
            propertyName = "Свойство: Содержимое";
            try {
                if (refreshMode == Constants.RM_DIRECTLY) {
                    String path = pv.stringValue();
                    // Создание рефа (связи с объектами) для содержимого комбобокса
                    contentRef = OrRef.createRef(path, false, Mode.RUNTIME, frame.getRefs(), frame.getTransactionIsolation(),
                            frame);

                    OrRef r = contentRef;
                    while (r.getParent() != null && (dataRef == null || dataRef.getType().id != r.getType().id)) {
                        r.setColumn(true);
                        r = r.getParent();
                    }
                    if (frame.getContentRef().get(path) == null) {
                        OrRef tempRef = contentRef;
                        frame.getContentRef().put(path, tempRef);
                    }
                } else {
                    if (fRecord != null && fRecord.getObjId() > 0) {
                        contentRef = OrRef.createContentRef(pv.stringValue(), fRecord.getObjId(), refreshMode, Mode.RUNTIME,
                                frame.getTransactionIsolation(), frame);
                    } else {
                        contentRef = OrRef.createContentRef(pv.stringValue(), refreshMode, Mode.RUNTIME,
                                frame.getTransactionIsolation(), frame);
                    }
                }
                contentRef.addOrRefListener(this);
                OrRef r = contentRef;
                while (r.getParent() != null && (dataRef == null || dataRef.getType().id != r.getType().id))
                    r = r.getParent();
                KrnClass rCls = r.getType();
                KrnAttribute parentAttr = krn.getAttributeByName(rCls, "родитель");
                if (parentAttr != null) {
                    isStructCls = true;
                    String path = r.toString();
                    contentRefs = new OrRef[parentsSize];
                    OrRef parentRef = r;
                    for (int k = 0; k < parentsSize; k++) {
                        path = path + ".родитель";

                        if (refreshMode == Constants.RM_DIRECTLY) {
                            contentRefs[k] = OrRef.createRef(path, false, Mode.RUNTIME, frame.getRefs(),
                                    frame.getTransactionIsolation(), frame);
                        } else {
                            contentRefs[k] = OrRef.createContentRef(path, parentRef, refreshMode, Mode.RUNTIME,
                                    frame.getTransactionIsolation(), frame);
                        }
                        parentRef = contentRefs[k];
                        parentRef.setColumn(true);
                    }
                    parentRef.addOrRefListener(this);
                }

            } catch (Exception e) {
                if (e instanceof RuntimeException) {
                    showErrorNessage(e.getMessage());
                }
                e.printStackTrace();
            }
        }


        rprop = proot.getChild("ref").getChild("contentCalc");
        pv = comboBox.getPropertyValue(rprop);

        if (!pv.isNull()) {
            String expr = pv.stringValue();
            if (expr.trim().length() > 0) {
                try {
                    propertyName = "Свойство: Содержимое формула";
                    contentCalcExpr = new OrCalcRef(expr, isEditor, Mode.RUNTIME, frame.getRefs(),
                            frame.getTransactionIsolation(), frame, comboBox, propertyName, this);
                    contentCalcExpr.addOrRefListener(this);
                    Editor e = new Editor(expr);
                    ArrayList<String> paths = e.getRefPaths();
                    for (int j = 0; j < paths.size(); ++j) {
                        String path = paths.get(j);
                        OrRef.createRef(path, false, Mode.RUNTIME, frame.getRefs(),
                                OrRef.TR_CLEAR, frame);
                    }
                } catch (Exception e) {
                    showErrorNessage(e.getMessage() + expr);
                    e.printStackTrace();
                }
            }
        }

        if (fRecord != null) {
            try {
                KrnObject[] fobjs = { fRecord.getKrnObject() };
                String[] strs = krn.getStrings(fobjs[0], "className", 0, 0);
                KrnClass cls = krn.getClassByName(strs[0]);
                contentRef.getParentOfClass(cls.id).setDefaultFilter(fRecord.getObjId());
                String fuid = fRecord.getKrnObject().uid;
                krn.addFilterParamListener(fuid, "", this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        PropertyNode pn = proot.getChild("view");
        pv = comboBox.getPropertyValue(pn.getChild("combonotsorted"));
        if (!pv.isNull()) {
            sorted = !pv.booleanValue();
        }
        if (!isEditor) {
        	if (comboBox.getComboBox() != null)
        		this.comboBox.getComboBox().addActionListener(this);
        	else if (comboBox.getList() != null)
        		this.comboBox.getList().addListSelectionListener(this);
        }
        final JTextField tf;
        if (comboBox.getEditor() != null) {
            tf = (JTextField) comboBox.getEditor().getEditorComponent();
            if (comboBox.isEnabled())
                tf.setBackground(Color.WHITE);
            // Utils.setComponentFocusCircle(tf);
            if (tf != null) {
                comboBox.setEditable(true);
                // tborder = tf.getBorder();
                // bgColor = tf.getBackground();
                tf.setDocument(new CBDocument());
                tf.addMouseListener(new MouseAdapter() {
                    public void mousePressed(MouseEvent e) {
                        if (e.getComponent().isEnabled()) {
                            if (comboBox.getComboBox().isShowing() && comboBox.getComboBox().isEnabled())
                                comboBox.getComboBox().showPopup();
                            else
                                comboBox.getComboBox().hidePopup();
                        }
                    }
                });
                Font f = tf.getFont();
                if (f == null || !f.equals(getFont())) {
                    tf.setFont(getFont());
                }
                comboBox.getComboBox().addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        JTextField tf = (JTextField) comboBox.getEditor().getEditorComponent();
                        String text = tf.getText();
                        ComboBoxModel aModel = comboBox.getComboBox().getModel();
                        int size = aModel.getSize();
                        String current;
                        int i = 0;
                        boolean found = false;
                        int beginIndex = (comboBox.getComboBox().getSelectedIndex() > -1) ? comboBox.getComboBox().getSelectedIndex() : 0;
                        for (i = beginIndex; i < size; i++) {
                        	current = aModel.getElementAt(i).toString();
                            if (current.toLowerCase(Constants.OK).startsWith(text.toLowerCase(Constants.OK))) {
                                tf.setText(current);
                                tf.setSelectionStart(text.length());
                                tf.setSelectionEnd(current.length());
                                if (i != comboBox.getComboBox().getSelectedIndex())
                                    comboBox.setSelectedIndex(i);

                                found = true;
                                break;
                            }
                        }
                        if (i == size) {
                            for (i = 0; i < beginIndex; i++) {
                            	current = aModel.getElementAt(i).toString();
                                if (current.toLowerCase(Constants.OK).startsWith(text.toLowerCase(Constants.OK))) {
                                    tf.setText(current);
                                    tf.setSelectionStart(text.length());
                                    tf.setSelectionEnd(current.length());
                                    if (i != comboBox.getComboBox().getSelectedIndex())
                                        comboBox.setSelectedIndex(i);

                                    found = true;
                                    break;
                                }
                            }
                        }
                        if (!found) {
                            if (text.length() > 1) {
                            	Object selItem = comboBox.getSelectedItem();
                                current = (selItem != null) ? selItem.toString() : "";
                                tf.setText(current);
                                int selStart = Math.min(text.length() - 1, current.length());
                                tf.setSelectionStart(selStart);
                                tf.setSelectionEnd(current.length());
                            } else {
                                tf.setText("");
                                comboBox.setSelectedIndex(-1);
                            }
                        }
                    }

                });
                setFont();
            }
        }
        comboBox.setRenderer(new OrComboBoxCellRenderer(comboBox) {
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                getRenderer().setToolTipText(value != null 
                		? (value instanceof OrComboItem && ((OrComboItem) value).isListTitle() 
                				? buildTitles(value) 
                				: kz.tamur.rt.Utils.cutTextMessage(value.toString())) 
                		: null);
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });

        if (comboBox.getComboBox() != null) {
	        ActionMap am = comboBox.getComboBox().getActionMap();
	        am.put("popup", new AbstractAction() {
	            public void actionPerformed(ActionEvent e) {
	                comboBox.setEditable(true);
	                comboBox.getComboBox().showPopup();
	                JTextField tf = (JTextField) comboBox.getComboBox().getEditor().getEditorComponent();
	                tf.select(0, tf.getText().length());
	                tf.requestFocus();
	            }
	        });
	
	        if (dataRef != null && isEditor) {
	            InputMap im = comboBox.getComboBox().getInputMap();
	            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "popup");
	            comboBox.getComboBox().putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
	        }
        }
        PropertyNode prop = comboBox.getProperties();
        pn = prop.getChild("view");
        pv = comboBox.getPropertyValue(pn.getChild("font").getChild("fontG"));
        if (!pv.isNull()) {
            font = pv.fontValue();
        } else {
            font = (Font) pn.getChild("font").getChild("fontG").getDefaultValue();
        }
    }
    
    // RefListener
    public void valueChanged(OrRefEvent e) {
        if (!selfChange && e.getOriginator() != this) {
            try {
                selfChange = true;
                try {
                	// что если сперва придет не в этот реф, а во второй реф где заполняются коды
                    OrRef ref = e.getRef();
                    // если ты сюда заходишь второй раз, ты не заполняешь их в itemMap, а считываешь значения из itemMap из записываешь их в sortAttr_ OrComboItem citem = new OrComboItem(getParentObject(contentRef, item), value);
                    if ((ref == contentRef && !isStructCls) || (contentRefs != null && ref == contentRefs[parentsSize - 1])) {
                        comboBox.removeAllItems();
                        KrnObject lang = frame.getDataLang();
                        // здесь берется массив значений списка ComboBox из переменной contentRef
                        List items = contentRef.getItems(lang != null ? lang.id : 0);
                        citems = new ArrayList<OrComboItem>(items.size() + 1);
                        citems.add(new OrComboItem(isStructCls?nullKrnObject:null, ""));
                        for (int i = 0; i < items.size(); ++i) {
                            OrRef.Item item = (OrRef.Item) items.get(i);
                            Object value = item.getCurrent();
                            if (value != null) {
                                OrComboItem citem = new OrComboItem(getParentObject(contentRef, item), value);
                                citems.add(citem);
                                if(contentRefPass==2)
                                	//*
                                	//аттрибуту combolist, присваиваем его аттрибут по сортировке(sortAttr)
                                	citem.setSortAttr(itemMap.get(citem.getObject()));
                                else
                                	itemMap.put(citem.getObject(),citem);
                                if (isStructCls) {
                                    KrnObject[] pobjs = new KrnObject[parentsSize];
                                    for (int k = 0; k < parentsSize; k++) {
                                        List<OrRef.Item> pitems = contentRefs[k].getItems(0);
                                        OrRef.Item pitem = pitems.get(i);
                                        pobjs[k] = (KrnObject) pitem.getCurrent();
                                    }
                                    citem.setPobjs(pobjs);
                                }
                            }
                        }
                        if(contentRefSort==null || contentRefPass==2) {
	                        if (sorted)
	                            Collections.sort(citems);
	                        
	                        comboBox.setModel(new DefaultComboBoxModel(citems.toArray()));
	                        if (lastItem != null) {
	                            comboBox.setSelectedItem(lastItem);
	                            KrnObject value = (lastItem != null) ? lastItem.getObject() : null;
	                            updateParamFilters(value);
	                        }
	                        comboBox.setValue(value);
	                        itemMap.clear();
	                        contentRefPass=0;
                        }else
                        	contentRefPass=1;
                    }else if (ref == contentRefSort) {
                        if(contentRefSort!=null) {
                    	 List itemsSort = contentRefSort.getItems(0);
	                        for (int i = 0; i < itemsSort.size(); ++i) {
	                            OrRef.Item item = (OrRef.Item) itemsSort.get(i);
	                            Object value = item.getCurrent();
	                            if (value != null) {
	                            	if(contentRefPass==1){
	                            	OrComboItem citem=(OrComboItem)itemMap.get(getParentObject(contentRefSort, item));
	                            	if(citem!=null)
	                            		citem.setSortAttr(value);
	                            	}else {
	                            		itemMap.put(getParentObject(contentRefSort, item), value);
	                            	}
	                            }
	                        }
	                        if(contentRefPass==1) {
		                        if (sorted)
		                            Collections.sort(citems);
		                        
		                        comboBox.setModel(new DefaultComboBoxModel(citems.toArray()));
		                        if (lastItem != null) {
		                            comboBox.setSelectedItem(lastItem);
		                            KrnObject value = (lastItem != null) ? lastItem.getObject() : null;
		                            updateParamFilters(value);
		                        }
		                        comboBox.setValue(value);
		                        itemMap.clear();
		                        contentRefPass=0;
	                        }else
	                        	contentRefPass=2;
                     }
                   
                 } else if (contentCalcExpr != null) {
                	 calculateContent();
                 }
                        		
                }finally {
                    selfChange = false;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (radioGroup != null) {
            groupManager.evaluate(frame, radioGroup);
        }
        super.valueChanged(e);
    }

    public void clear() {
    }

    public void stateChanged(OrRefEvent e) {
        if (comboBox.isEnabled()) {
            Integer state = getState(new Integer(0));
            JComponent tf = comboBox.getEditorComponent();
            tf.setOpaque(false);
            if (state == Constants.REQ_ERROR) {
                tf.setOpaque(true);
                tf.setBackground(REQ_ERROR_COLOR);
            } else if (state == Constants.EXPR_ERROR) {
                tf.setOpaque(true);
                tf.setBackground(EXPR_ERROR_COLOR);
            } else if (focused) {
                tf.setBackground(kz.tamur.rt.Utils.getLightYellowColor());
            } else {
                tf.setBackground(getBgColor(0));
            }
        }
    }

    public void filterParamChanged(String fuid, String pid, List<?> values) {
        super.filterParamChanged(fuid, pid, values);
        try {
            OrRef parent = contentRef.getParent();
            while (parent.getParent() != null && (dataRef == null || dataRef.getType().id == parent.getType().id))
                parent = parent.getParent();
            parent.evaluate((KrnObject[]) null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearParam() {
        try {
            OrRef parent = contentRef.getParent();
            while (parent.getParent() != null && (dataRef == null || dataRef.getType().id == parent.getType().id))
                parent = parent.getParent();
            parent.evaluate((KrnObject[]) null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setFocused(boolean b) {
        focused = b;
    }

    public class OrComboItem implements Comparable {
        private KrnObject object_;
        private Object title_;
        private Object sortAttr_;
        private KrnObject[] pobjs_;
        private int level_ = 0;

        public OrComboItem(KrnObject object, Object title) {
            object_ = object;
            title_ = title;
        }

        public void setSortAttr(Object sortAttr) {
            this.sortAttr_=sortAttr;
        }
        public KrnObject getObject() {
            return object_;
        }

        public String toString() {
            return (title_ != null) ? (title_ instanceof List ? ((List)title_).get(0).toString() : title_.toString()) : "";
        }

        public void setPobjs(KrnObject[] pobjs) {
            this.pobjs_ = pobjs;
            level_ = 0;
            for (int k = 0; k < pobjs.length; k++)
                if (pobjs[k] != null)
                    level_++;
        }
        
        public boolean isListTitle() {
        	return (title_ instanceof List);
        }
        
        public List getTitles() {
        	return (List) title_;
        }
        
// если св-во код задано, то сортируется по коду, если нет, то сортируется по алфавиту
        public int compareTo(Object o) {
            if (o != null && o instanceof OrComboItem) {
            	if(contentRefSort!=null) {
	                Object sortAttr = ((OrComboItem) o).sortAttr_;
	                if ((sortAttr_ instanceof String && sortAttr instanceof String) || (sortAttr_ instanceof Long && sortAttr instanceof Long)
	                        || (sortAttr_ instanceof Double && sortAttr instanceof Double))
	                    return ((Comparable) sortAttr_).compareTo((Comparable) sortAttr);
	                if (sortAttr_ == sortAttr)
	                    return 0;
            	}else {
	                if (title_ == null || "".equals(title_))
	                    return -1;
	
	                if (isStructCls) {
	                    OrComboItem c = (OrComboItem) o;
	                    for (int level = 0; level <= level_ && level <= c.level_; level++) {
	                        KrnObject o1 = (level >= level_) ? object_ : pobjs_[level_ - 1 - level];
	                        KrnObject o2 = (level >= c.level_) ? c.object_ : c.pobjs_[c.level_ - 1 - level];
	                        if (o1 == null && o2 == null)
	                            break;
	                        else if (o1 == null)
	                            return 1;
	                        else if (o2 == null)
	                            return -1;
	                        else if (o1.id != o2.id)
	                            return new Long(o1.id).compareTo(o2.id);
	                    }
	                    if (level_ != c.level_)
	                        return new Integer(level_).compareTo(c.level_);
	                }
	                Object title = ((OrComboItem) o).title_;
	                if ((title_ instanceof String && title instanceof String) || (title_ instanceof Long && title instanceof Long)
	                        || (title_ instanceof Double && title instanceof Double))
	                    return ((Comparable) title_).compareTo((Comparable) title);
	                if (title_ == title)
	                    return 0;
	            }
            }
	        return 1;
	    }
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof OrComboItem) {
                OrComboItem item = (OrComboItem) obj;
                if (object_ == item.object_ && (title_ == item.title_ || title_.equals(item.title_))) {
                    return true;
                }
                return object_ != null && item.object_ != null && object_.id == item.object_.id;
            }
            return false;
        }
    }

    private KrnObject getParentObject(OrRef ref, OrRef.Item item) throws KrnException {
        KrnClass type = (dataRef != null) ? dataRef.getType() : null;
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
        if (pitem.getCurrent() instanceof KrnObject) {
            return (KrnObject) pitem.getCurrent();
        } else {
            return null;
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (comboBox.isHelpClick()) {
            comboBox.setHelpClick(false);
        } else {
            updateValue();
        }
    }

    @Override
	public void valueChanged(ListSelectionEvent e) {
        if (comboBox.isHelpClick()) {
            comboBox.setHelpClick(false);
        } else {
            updateValue();
        }
    }

    protected void updateValue() {
        try {
            if (!selfChange && commitCell) {
                try {
                    selfChange = true;
                    Object selItem = comboBox.getSelectedItem();
                    OrComboItem item = (selItem instanceof OrComboItem) ? (OrComboItem) selItem : null;
                    if (frame.getEvaluationMode() == InterfaceManager.ARCH_RO_MODE) {
                        lastItem = item;
                    }
                    Object value = (item != null && item.getObject()!=nullKrnObject) 
                    		? item.getObject() : null;
                    Object realValue = changeValue(value);
                    if (!Funcs.equals(realValue, value)) {
                        comboBox.setValue(realValue);
                    }
                } finally {
                    selfChange = false;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public class CBDocument extends PlainDocument {
        public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
            if (str == null)
                return;
            try {
                super.insertString(offset, str, a);
                if (str.length() == 1) {
                    comboBox.fireEvent();
                }
            } catch (NullPointerException e) {}
        }
    }

    public class OrComboCellEditor extends OrCellEditor {

        private JTable editingTable;

        public Object getCellEditorValue() {
            if (comboBox.getSelectedItem() != null)
                return ((OrComboItem) comboBox.getSelectedItem()).toString();
            else
                return null;
        }

        public JTable getEditingTable() {
            return editingTable;
        }

        public OrComboBox getComboBox() {
            return comboBox;
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            editingTable = table;

            valueChanged(new OrRefEvent(dataRef, -1, -1, null));
            OrTableModel model = (OrTableModel) table.getModel();
            if (model instanceof TreeTableAdapter.RtTreeTableModel) {
                row = ((TreeTableAdapter.RtTreeTableModel) model).getActualRow(row);
            }
            setState(new Integer(row), new Integer(0));
            comboBox.setBorder(BorderFactory.createEmptyBorder());
            comboBox.setFont(getFont());
            
            if (contentCalcExpr != null) {
            	calculateContent();
            }
            
            return comboBox;
        }

        public Object getValueFor(Object obj) {
            OrRef.Item refItem = (OrRef.Item) obj;
            KrnObject object = (refItem == null) ? null : (KrnObject) refItem.getCurrent();
            if (object != null) {
                int count = comboBox.getItemCount();
                for (int i = 0; i < count; ++i) {
                    OrComboItem item = (OrComboItem) comboBox.getItemAt(i);
                    KrnObject iobj = (item != null) ? item.getObject() : null;
                    if (iobj != null && iobj.equals(object))
                        return item.toString();
                }
            }
            return null;
        }

        public boolean stopCellEdit() {
            return checkUnique();
        }

        public void check() {
            if (checkUnique()) {
                commitCell = true;
            } else {
                Container parent = editingTable.getParent();
                while (parent != null && !(parent instanceof OrTable)) {
                    parent = parent.getParent();
                }
                ResourceBundle res;
                if (parent instanceof OrTable) {
                    OrFrame frame = ((OrTable) parent).getFrame();
                    res = frame.getResourceBundle();
                } else {
                    res = ResourceBundle.getBundle(Constants.NAME_RESOURCES, new Locale("ru"));
                }
                MessagesFactory.showMessageDialog(editingTable.getTopLevelAncestor(), 4, res.getString("duplicateData"));
                cancelCellEditing();
                commitCell = false;
            }
        }

        public boolean stopCellEditing() {
            check();
            updateValue();
            boolean res = super.stopCellEditing();
            if (editingTable != null)
                editingTable.requestFocusInWindow();
            return res;
        }

        public void cancelCellEditing() {
            super.cancelCellEditing();
            if (editingTable != null)
                editingTable.requestFocusInWindow();
        }

        public boolean shouldSelectCell(EventObject anEvent) {
            if (anEvent instanceof MouseEvent) {
                MouseEvent e = (MouseEvent) anEvent;
                return e.getID() != MouseEvent.MOUSE_DRAGGED;
            }
            return true;
        }

    }

    public OrCellEditor getCellEditor() {
        if (editor_ == null) {
            editor_ = new OrComboCellEditor();
            try {
            ActionMap am = comboBox.getComboBox().getActionMap();
            am.put("cancel", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    editor_.cancelCellEditing();
                }
            });
            } catch(Exception e) {
            	e.printStackTrace();
            }
            InputMap im = comboBox.getComboBox().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel");

            comboBox.getComboBox().getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
                public void keyReleased(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        editor_.stopCellEditing();
                    }
                }
            });

            Object popup = comboBox.getComboBox().getUI().getAccessibleChild(null, 0);
            if (popup instanceof ComboPopup) {
                ((ComboPopup) popup).getList().addMouseListener(new MouseAdapter() {
                    public void mouseReleased(MouseEvent e) {
                        super.mouseReleased(e);
                        editor_.stopCellEditing();
                    }
                });
            }
        }
        return editor_;
    }

    private void setFont() {
        comboBox.setFont(font);
    }

    private Font getFont() {
        return font;
    }

    public OrRef getContent() {
        return contentRef;
    }

    public void setEnabled(boolean isEnabled) {
        super.setEnabled(isEnabled);
        comboBox.setEnabled(isEnabled);
    }

    public void clearFilterParam() {
        super.clearFilterParam();
        if (dataRef == null) {
            comboBox.setSelectedIndex(-1);
            lastItem = null;
        }
    }

	public void calculateContent() {
		if (contentCalcExpr != null) {
	        comboBox.removeAllItems();
	        
	        try {
		        ClientOrLang orlang = new ClientOrLang(frame);
		        Map<String, Object> vc = new HashMap<String, Object>();
		        boolean calcOwner = OrCalcRef.setCalculations();
		        orlang.evaluate(contentCalcExpr.template, vc, this, new Stack<String>());
				if (calcOwner)
					OrCalcRef.makeCalculations();
				
		        List res = (List<String>)vc.get("RETURN");
		
		        if (res != null) {
		            java.util.List<OrComboItem> citems = new ArrayList<OrComboItem>(res.size() + 1);
		            if (res.size() > 0 && res.get(0) instanceof List) {
		            	citems.add(new OrComboItem(null, Collections.nCopies(((List)res.get(0)).size() - 1, "")));
		            } else {
		            	citems.add(new OrComboItem(null, ""));
		            }
		            for (int i = 0; i < res.size(); ++i) {
		            	 Object val = res.get(i);
			                if (val != null) {
			                	OrComboItem citem;
			                	if (val instanceof List) {
			                		List objs = (List)val;
			                		citem = new OrComboItem((KrnObject)objs.get(0), objs.subList(1, objs.size()));
			                	} else {
			                		citem = new OrComboItem(null, val);
			                	}
			                    citems.add(citem);
			                }
		            }
		            if (sorted)
		            
		                Collections.sort(citems);
		            
		            comboBox.setModel(new DefaultComboBoxModel(citems.toArray()));
		        }
	        } catch (Exception e) {
	        	e.printStackTrace();
	        }
		}
	}

}
