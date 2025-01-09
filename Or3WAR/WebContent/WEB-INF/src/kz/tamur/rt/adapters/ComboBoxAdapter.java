package kz.tamur.rt.adapters;

import static kz.tamur.comps.Mode.RUNTIME;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.swing.DefaultComboBoxModel;

import kz.tamur.comps.Constants;
import kz.tamur.comps.Mode;
import kz.tamur.comps.OrFrame;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.guidesigner.filters.FilterRecord;
import kz.tamur.lang.OrLang;
import kz.tamur.or3.client.comps.interfaces.OrComboBoxComponent;
import kz.tamur.rt.InterfaceManager;
import kz.tamur.rt.RadioGroupManager;
import kz.tamur.rt.adapters.OrRef.Item;
import kz.tamur.rt.orlang.ClientOrLang;
import kz.tamur.util.Funcs;
import kz.tamur.web.common.webgui.WebComponent;
import kz.tamur.web.component.OrWebComboBox;
import kz.tamur.web.component.WebFrame;

import com.cifs.or2.client.FilterParamListener;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.util.expr.Editor;

public class ComboBoxAdapter extends ComponentAdapter implements FilterParamListener {

    private OrComboBoxComponent comboBox;
    private boolean selfChange = false;
    private OrRef contentRef, attentionRef, hintRef;
    private OrCalcRef contentCalcExpr = null;
    private OrRef[] contentRefs;
    private int parentsSize = 4;
    private RadioGroupManager groupManager = new RadioGroupManager();
    private boolean commitCell = true;
    private OrComboItem lastItem = null;
    private Font font;
    private boolean focused = false;
    private boolean sorted = true;
    private boolean isStructCls = false;
    private OrRef contentRefSort;
    private Map<KrnObject,Object> itemMap=new HashMap<>();
    private List<OrComboItem> citems;
    private int contentRefPass = 0;

    @SuppressWarnings("serial")
    public ComboBoxAdapter(OrFrame frame, OrComboBoxComponent comboBox_, boolean isEditor) throws KrnException {
        super(frame, comboBox_, isEditor);
        this.comboBox = comboBox_;
        Kernel krn = frame.getKernel();
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
            	log.error("Ошибка при инициализации компонента 'Combobox'; uuid = " + getUUID());
        		log.error(e, e);
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
        
        rprop = proot.getChild("ref").getChild("content");
        pv = comboBox.getPropertyValue(rprop);

        if (!pv.isNull()) {
            propertyName = "Свойство: Содержимое";
            try {
                if (refreshMode == Constants.RM_DIRECTLY) {
                    String path = pv.stringValue(frame.getKernel());
                    contentRef = OrRef.createRef(path, false, RUNTIME, frame.getRefs(), frame.getTransactionIsolation(), frame);
                    contentRef.addLanguage(frame.getInterfaceLang().id);
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
                        contentRef = OrRef.createContentRef(pv.stringValue(frame.getKernel()), fRecord.getObjId(), refreshMode,
                                RUNTIME, frame.getTransactionIsolation(), frame);
                    } else {
                        contentRef = OrRef.createContentRef(pv.stringValue(frame.getKernel()), refreshMode, RUNTIME,
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
                            contentRefs[k] = OrRef.createRef(path, false, RUNTIME, frame.getRefs(),
                                    frame.getTransactionIsolation(), frame);
                        } else {
                            contentRefs[k] = OrRef.createContentRef(path, parentRef, refreshMode, RUNTIME,
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
            	log.error("Ошибка при инициализации компонента 'Combobox'; uuid = " + getUUID());
        		log.error(e, e);
            }
        }
        
        rprop = proot.getChild("ref").getChild("hintTitle");
        pv = comboBox.getPropertyValue(rprop);

        if (!pv.isNull()) {
            String expr = pv.stringValue();
            if (expr.trim().length() > 0) {
                try {
                    propertyName = "Свойство: Подсказка";
                    hintRef = OrRef.createRef(expr, false, RUNTIME, frame.getRefs(), frame.getTransactionIsolation(), frame);
                    hintRef.addLanguage(frame.getInterfaceLang().id);
                    OrRef r = hintRef;
                	while (r.getParent() != null && (dataRef == null || dataRef.getType().id != r.getType().id)) {
                		r.setColumn(true);
                		r = r.getParent();
                	}
                    if (frame.getContentRef().get(expr) == null) {
                        OrRef tempRef = hintRef;
                        frame.getContentRef().put(expr, tempRef);
                    }
                    hintRef.addOrRefListener(this);
                } catch (Exception e) {
                    showErrorNessage(e.getMessage() + expr);
                	log.error("Ошибка при инициализации Свойство: Подсказка компонента 'Combobox'; uuid = " + getUUID());
            		log.error(e, e);
                }
            }
        }
        
        rprop = proot.getChild("ref").getChild("contentCalc");
        pv = comboBox.getPropertyValue(rprop);

        if (!pv.isNull()) {
            String expr = pv.stringValue(frame.getKernel());
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
                	log.error("Ошибка при инициализации Свойство: Содержимое формула компонента 'Combobox'; uuid = " + getUUID());
            		log.error(e, e);
                }
            }
        }

        if (fRecord != null) {
            try {
                KrnObject[] fobjs = krn.getObjectsByIds(new long[] { fRecord.getObjId() }, -1);
                String[] strs = krn.getStrings(fobjs[0], "className", 0, 0);
                KrnClass cls = krn.getClassByName(strs[0]);
                contentRef.getParentOfClass(cls.id).setDefaultFilter(fRecord.getObjId());
                String fuid = krn.getUId(fRecord.getObjId());
                krn.addFilterParamListener(fuid, "", this);
            } catch (Exception e) {
            	log.error("Ошибка при инициализации 'Combobox'; uuid = " + getUUID());
        		log.error(e, e);
            }
        }

        PropertyNode pn = proot.getChild("view");
        pv = comboBox.getPropertyValue(pn.getChild("combonotsorted"));
        if (!pv.isNull()) {
            sorted = !pv.booleanValue();
        }
        pv = comboBox.getPropertyValue(proot.getChild("pov").getChild("afterModAction"));
        String afterExpr = null;
        if (!pv.isNull()) {
            afterExpr = pv.stringValue(frame.getKernel());
        }
        if (afterExpr != null && afterExpr.length() > 0) {
            long ifcId = ((WebFrame) frame).getObj().id;
            String key = ((WebComponent) comboBox_).getId() + "_" + OrLang.AFTER_MODIF_TYPE;
            afterModAction = ClientOrLang.getStaticTemplate(ifcId, key, afterExpr, getLog());
            try {
                Editor e = new Editor(afterExpr);
                ArrayList<String> paths = e.getRefPaths();
                for (int j = 0; j < paths.size(); ++j) {
                    String path = paths.get(j);
                    OrRef.createRef(path, false, RUNTIME, frame.getRefs(), OrRef.TR_CLEAR, frame);
                }
            } catch (Exception ex) {
            	log.error("Ошибка при инициализации компонента Свойство: Действие после модификации 'Combobox'; uuid = " + getUUID());
        		log.error(ex, ex);
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
        setАttentionRef(comboBox);
    }
    
    public void setАttentionRef(OrGuiComponent c) {
		PropertyValue pv = ((OrWebComboBox) c).getPropertyValue(((OrWebComboBox) c).getProperties().getChild("pov").getChild("activity").getChild("attention"));
		String attentionExpr = null;
        if (!pv.isNull()) {
        	attentionExpr = pv.stringValue(frame.getKernel());
        }
		if (attentionExpr != null && attentionExpr.length() > 0) {
			try {
				propertyName = "Свойство: Поведение.Активность.Внимание";
				attentionRef = new OrCalcRef(attentionExpr, false, Mode.RUNTIME, frame.getRefs(), frame.getTransactionIsolation(), frame, c, propertyName, this);
				attentionRef.addOrRefListener(this);
			} catch (Exception e) {
				showErrorNessage(e.getMessage() + attentionExpr);
            	log.error("Ошибка при инициализации Свойство: Поведение.Активность.Внимание компонента 'Combobox'; uuid = " + getUUID());
        		log.error(e, e);
			}
		}
	}

	// RefListener
	public void valueChanged(OrRefEvent e) {
		if (!selfChange && e.getOriginator() != this) {
			try {
				selfChange = true;
				try {
					OrRef ref = e.getRef();
					if ((ref == contentRef && !isStructCls) || (contentRefs != null && ref == contentRefs[parentsSize - 1])) {
						comboBox.removeAllItems();
						KrnObject lang = frame.getInterfaceLang();
						List<Item> items = contentRef.getItems(lang != null ? lang.id : 0);
						int size = (comboBox.getAppearance() == Constants.VIEW_SIMPLE_COMBO || comboBox.getAppearance() == Constants.VIEW_SOLID_LIST) ? items.size() + 1
								: items.size();
						citems = new ArrayList<OrComboItem>(size);
						if (comboBox.getAppearance() == Constants.VIEW_SIMPLE_COMBO || comboBox.getAppearance() == Constants.VIEW_SOLID_LIST)
							citems.add(new OrComboItem(null, "", null));
						for (int i = 0; i < items.size(); ++i) {
							OrRef.Item item = (OrRef.Item) items.get(i);
							Object value = item.getCurrent();
							if (value != null) {
								OrComboItem citem = new OrComboItem(getParentObject(contentRef, item), value, null);
								citems.add(citem);

								if (contentRefPass == 2)

									// аттрибуту combolist, присваиваем его атрибут по сортировке(sortAttr)
									citem.setSortAttr(itemMap.get(citem.getObject()));
								else
									itemMap.put(citem.getObject(), citem);

								// ?? сортировка структурного объекта ??
								
								/*if (isStructCls) {
									KrnObject[] pobjs = new KrnObject[parentsSize];
									for (int k = 0; k < parentsSize; k++) {
										List<OrRef.Item> pitems = contentRefs[k].getItems(0);
										OrRef.Item pitem = pitems.get(i);
										pobjs[k] = (KrnObject) pitem.getCurrent();
									}
									citem.setPobjs(pobjs);
								}*/
							}
						}

						if (contentRefSort == null || contentRefPass == 2) {
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
							contentRefPass = 0;
						} else
							contentRefPass = 1;
					} else if (ref == contentRefSort) {
						if (contentRefSort != null) {
							List<Item> itemsSort = contentRefSort.getItems(0);
							for (int i = 0; i < itemsSort.size(); ++i) {
								OrRef.Item item = (OrRef.Item) itemsSort.get(i);
								Object value = item.getCurrent();
								if (value != null) {
									if (contentRefPass == 1) {
										OrComboItem citem = (OrComboItem) itemMap
												.get(getParentObject(contentRefSort, item));
										if (citem != null)
											citem.setSortAttr(value);
									} else {
										itemMap.put(getParentObject(contentRefSort, item), value);
									}
								}
							}
							if (contentRefPass == 1) {
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
								contentRefPass = 0;
							} else
								contentRefPass = 2;
						}

					} else if (contentCalcExpr == ref) {
						calculateContent();
					}
				} finally {
					selfChange = false;
				}
			} catch (Exception ex) {
				log.error(ex, ex);
			}
		}
		if (e.getRef() == attentionRef) {
			((OrWebComboBox) comboBox).sendChangeProperty("comboBoxAttention",
					attentionRef.getValue(langId).toString());
		}
		if (radioGroup != null) {
			groupManager.evaluate(frame, radioGroup);
		}
		super.valueChanged(e);
	}

    public void clear() {
    	if (dataRef != null && dataRef.getAttribute() == null)
    		dataRef.clearSelItem();
        /*
         * if (comboBox.getItemCount() > 0) {
         * comboBox.setSelectedIndex(0);
         * }
         */
    }

    public void stateChanged(OrRefEvent e) {
        if (comboBox.isEnabled()) {
            Integer state = getState(new Integer(0));
            if (comboBox instanceof OrWebComboBox) {
                if (state == null)
                    state = 0;
                ((WebComponent) comboBox).setState(state);
            }
        }
    }

    public void filterParamChanged(String fuid, String pid, List<?> values) {
        super.filterParamChanged(fuid, pid, values);
        try {
            OrRef parent = contentRef.getParent();
            while (parent.getParent() != null && (dataRef == null || dataRef.getType().id == parent.getType().id)) {
                parent = parent.getParent();
            }
            parent.evaluate((KrnObject[]) null, null);
        } catch (Exception e) {
    		log.error(e, e);
        }
    }

    public void clearParam() {
        try {
            OrRef parent = contentRef.getParent();
            while (parent.getParent() != null && (dataRef == null || dataRef.getType().id == parent.getType().id)) {
                parent = parent.getParent();
            }
            parent.evaluate((KrnObject[]) null, null);
        } catch (Exception e) {
    		log.error(e, e);
        }
    }

    public void setFocused(boolean b) {
        focused = b;
    }

    public class OrComboItem implements Comparable {
        private KrnObject object_;
        private Object title_;
        private Object hint_;
        //private KrnObject[] pobjs_;
        private int level_ = 0;
        private Object sortAttr_;

        public OrComboItem(KrnObject object, Object title, Object hint) {
            object_ = object;
            title_ = title;
            hint_ = hint;
        }

        public KrnObject getObject() {
            return object_;
        }
        
        public void setSortAttr(Object sortAttr) {
            this.sortAttr_=sortAttr;
        }

        public String toString() {
            return (title_ != null) ? title_.toString() : "";
        }
        
        public String getHint() {
            return (hint_ != null) ? hint_.toString() : "";
        }
        
        public void setHint(Object hint) {
            this.hint_ = hint;
        }

        /*public void setPobjs(KrnObject[] pobjs) {
            this.pobjs_ = pobjs;
            level_ = 0;
            for (int k = 0; k < pobjs.length; k++) {
                if (pobjs[k] != null) {
                    level_++;
                }
            }
        }*/
        
        public boolean isListTitle() {
        	return (title_ instanceof List);
        }
        
        public List getTitles() {
        	return (List) title_;
        }

        public int compareTo(Object o) {
            if (o != null && o instanceof OrComboItem) {
            	
                OrComboItem c = (OrComboItem) o;
                if(contentRefSort!=null) {
	                Object sortAttr = ((OrComboItem) o).sortAttr_;
	                if ((sortAttr_ instanceof String && sortAttr instanceof String) || (sortAttr_ instanceof Long && sortAttr instanceof Long)
	                        || (sortAttr_ instanceof Double && sortAttr instanceof Double))
	                    return ((Comparable) sortAttr_).compareTo((Comparable) sortAttr);
	                if (sortAttr_ == sortAttr)
	                    return 0;
            	} else {
	                if (title_ == c.title_)
	                    return 0;

            		Object comparableTitle1 = null;
            		
            		if (title_ instanceof List) {
            			comparableTitle1 = "";
            			
            			for (Object v : (List) title_) {
            				comparableTitle1 += (v != null ? v.toString() : "");
            			}
            		} else
            			comparableTitle1 = title_;
            		
            		Object comparableTitle2 = null;
            		
            		if (c.title_ instanceof List) {
            			comparableTitle2 = "";
            			
            			for (Object v : (List) c.title_) {
            				comparableTitle2 += (v != null ? v.toString() : "");
            			}
            		} else
            			comparableTitle2 = c.title_;

	                if ((comparableTitle1 == null || "".equals(comparableTitle1)) && !(comparableTitle2 == null || "".equals(comparableTitle2))) {
	            		return -1;
	            	} else if (!(comparableTitle1 == null || "".equals(comparableTitle1)) && (comparableTitle2 == null || "".equals(comparableTitle2))) {
	            		return 1;
	            	}
	                
	                // ?? сортировка структурного объекта ??
	            	
	                /*if (isStructCls) {
	                    if (level_ != c.level_)
	                        return new Integer(level_).compareTo(c.level_);
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
	                }*/
	
	                if ((comparableTitle1 instanceof String && comparableTitle2 instanceof String) || (comparableTitle1 instanceof Number && comparableTitle2 instanceof Number))
	                    return ((Comparable) comparableTitle1).compareTo((Comparable) comparableTitle2);
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
        if (type == null) {
            type = ref.getRoot().getType();
        }
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

    public void updateValue() {
        try {
            if (!selfChange && commitCell) {
                try {
                    selfChange = true;
                    Object selItem = comboBox.getSelectedItem();
                    OrComboItem item = (selItem instanceof OrComboItem) ? (OrComboItem) selItem : null;
                    if (frame.getEvaluationMode() == InterfaceManager.ARCH_RO_MODE) {
                        lastItem = item;
                    }
                    Object value = (item != null) 
                    		? ((contentRef != null || contentCalcExpr != null) ? item.getObject() : item.toString()) 
                    		: null;
                    Object realValue = changeValue(value);
                    if (!Funcs.equals(realValue, value)) {
                        comboBox.setValue(realValue);
                    }
                } finally {
                    selfChange = false;
                }
            }
        } catch (Exception ex) {
    		log.error(ex, ex);
        }
    }

    public void addValue(Object value) {
        try {
            if (!selfChange && commitCell) {
                try {
                    selfChange = true;
                    boolean calcOwner = OrCalcRef.setCalculations();
                    try {
    	                if (dataRef != null && dataRef.getAttribute() != null) {
    	                    dataRef.insertItem(-1, value, this, this, false);
    	                } else if (dataRef != null) {
    	                	dataRef.addSelItem((KrnObject)value);
    	                }
    	                updateParamFilters(value);
                	} catch (Exception e) {
                		log.error(e, e);
                	} finally {
        	            if (calcOwner)
        	            	OrCalcRef.makeCalculations();
                	}
                } finally {
                    selfChange = false;
                }
            }
        } catch (Exception ex) {
    		log.error(ex, ex);
        }
    }

    public void deleteValue(KrnObject value) {
        try {
            if (!selfChange && commitCell) {
                try {
                    selfChange = true;
                    boolean calcOwner = OrCalcRef.setCalculations();
                    try {
    	                if (dataRef != null && dataRef.getAttribute() != null) {
    		                dataRef.deleteItem(value, this, this);
    	                } else if (dataRef != null) {
    	                	dataRef.removeSelItem((KrnObject)value);
    	                }
    	                updateParamFilters(value);
                	} catch (Exception e) {
                		log.error(e, e);
                	} finally {
        	            if (calcOwner)
        	            	OrCalcRef.makeCalculations();
                	}
                } finally {
                    selfChange = false;
                }
            }
        } catch (Exception ex) {
    		log.error(ex, ex);
        }
    }

    public Font getFont() {
        return font;
    }

    public OrRef getContent() {
        return contentRef;
    }
    
    public OrRef getHintRef() {
        return hintRef;
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

    public boolean isCommitCell() {
        return commitCell;
    }

    public void setCommitCell(boolean commitCell) {
        this.commitCell = commitCell;
    }
    
	public void calculateContent() {
		if (contentCalcExpr != null) {
	        comboBox.removeAllItems();
	        
	        Map<String, Object> vc = new HashMap<String, Object>();
	        boolean calcOwner = OrCalcRef.setCalculations();
	        try {
		        ClientOrLang orlang = new ClientOrLang(frame);
		        orlang.evaluate(contentCalcExpr.template, vc, this, new Stack<String>());
	        } catch (Exception e) {
            	log.error("Ошибка при выполнении формулы 'Содержимое' компонента '" + (comp != null ? comp.getClass().getName() : "") + "', uuid: " + getUUID());
                log.error(e, e);
	        } finally {
				if (calcOwner)
					OrCalcRef.makeCalculations();
	        }
	        List res = (List)vc.get("RETURN");
	
	        if (res != null) {
	            java.util.List<OrComboItem> citems = new ArrayList<OrComboItem>(res.size() + 1);
	            if (res.size() > 0 && res.get(0) instanceof List) {
	            	citems.add(new OrComboItem(null, Collections.nCopies(((List)res.get(0)).size() - 1, ""), null));
	            } else {
	            	citems.add(new OrComboItem(null, "", null));
	            }
	            for (int i = 0; i < res.size(); ++i) {
	                Object val = res.get(i);
	                if (val != null) {
	                	OrComboItem citem;
	                	if (val instanceof List) {
	                		List objs = (List)val;
	                		citem = new OrComboItem((KrnObject)objs.get(0), objs.subList(1, objs.size()), null);
	                	} else {
	                		citem = new OrComboItem(null, val, null);
	                	}
	                    citems.add(citem);
	                }
	            }
	            if (sorted)
	                Collections.sort(citems);
	            
	            comboBox.setModel(new DefaultComboBoxModel(citems.toArray()));
	        }
		}
	}
	
	public void setData(List<String> data) {
        java.util.List<OrComboItem> citems = new ArrayList<OrComboItem>(data.size() + 1);
        for (int i = 0; i < data.size(); ++i) {
            String val = data.get(i);
            if (val != null) {
                OrComboItem citem = new OrComboItem(null, val, null);
                citems.add(citem);
            }
        }
        if (sorted)
            Collections.sort(citems);
        
        comboBox.setModel(new DefaultComboBoxModel(citems.toArray()));
	}

}
