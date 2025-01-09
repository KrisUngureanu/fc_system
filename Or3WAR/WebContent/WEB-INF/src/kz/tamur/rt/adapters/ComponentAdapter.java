package kz.tamur.rt.adapters;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import kz.tamur.comps.Constants;
import kz.tamur.comps.Mode;
import kz.tamur.comps.OrFrame;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.guidesigner.filters.FilterRecord;
import kz.tamur.lang.OrLang;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.or3.client.comps.interfaces.OrColumnComponent;
import kz.tamur.or3.client.comps.interfaces.OrComboBoxComponent;
import kz.tamur.or3.client.comps.interfaces.OrDateComponent;
import kz.tamur.or3.client.comps.interfaces.OrHyperLabelComponent;
import kz.tamur.or3.client.comps.interfaces.OrHyperPopupComponent;
import kz.tamur.or3.client.comps.interfaces.OrMemoComponent;
import kz.tamur.or3.client.comps.interfaces.OrPanelComponent;
import kz.tamur.or3.client.comps.interfaces.OrScrollPaneComponent;
import kz.tamur.or3.client.comps.interfaces.OrSplitPaneComponent;
import kz.tamur.or3.client.comps.interfaces.OrTabbedPaneComponent;
import kz.tamur.or3.client.comps.interfaces.OrTextComponent;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.rt.CheckContext;
import kz.tamur.rt.InterfaceManager;
import kz.tamur.rt.orlang.ClientOrLang;
import kz.tamur.util.Funcs;
import kz.tamur.util.Pair;
import kz.tamur.util.ReqMsgsList;
import kz.tamur.web.common.WebSessionManager;
import kz.tamur.web.common.webgui.WebComponent;
import kz.tamur.web.component.BarcodeComponent;
import kz.tamur.web.component.OrWebAccordion;
import kz.tamur.web.component.OrWebComboBox;
import kz.tamur.web.component.OrWebDateField;
import kz.tamur.web.component.OrWebLabel;
import kz.tamur.web.component.OrWebLayoutPane;
import kz.tamur.web.component.OrWebTabbedPane;
import kz.tamur.web.component.OrWebTableColumn;
import kz.tamur.web.component.OrWebTextField;
import kz.tamur.web.component.WebFrame;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cifs.or2.client.FilterParamListener;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.util.expr.Editor;

public abstract class ComponentAdapter implements OrRefListener, CheckContext, FilterParamListener {
	protected Log log;
    protected OrFrame frame;
    protected OrRef dataRef;
    protected OrCalcRef activityRef;
    protected OrCalcRef visibleRef;
    protected OrCalcRef calcRef;
    protected OrCalcRef constraintsRef;
    protected OrCalcRef constraintsValueRef;
    protected OrCalcRef backColorRef;
    protected OrCalcRef fontColorRef;
    protected String radioGroup;
    protected int reqGroup = 0;
    protected boolean isEnabled = true;
    protected boolean isVisible = true;
    protected OrGuiComponent comp;
    protected long langId;
    private int enterDB;
    private String reqMsg = "";
    private String cexpr;
    private ASTStart ctemplate;
    private boolean checkConstr = true;
    private boolean checkConstrValue = true;
    private String constrMsg;
    protected boolean editable = true;

    Color REQ_ERROR_COLOR = new Color(255, 204, 204);
    Color EXPR_ERROR_COLOR = new Color(202, 247, 187);

    protected Color defaultBgColor = null;
    protected Color defaultFontColor = null;
    protected Color bgColor = null;
    protected Color fontColor = null;

    protected String[] paramFiltersUIDs;
    protected String paramName;

    protected String propertyName = "Свойство: ";

    protected ContainerAdapter parentAdapter = null;

    private Map<Integer, Integer> states = new HashMap<Integer, Integer>();
    private boolean isEditor = false;
    
    /**
     * Текущее значение.
     */
    protected Object value;
    
    protected boolean selfChange = false;

    /**
     * Триггер "После модификации"
     */
    protected ASTStart afterModAction;

    /**
     * Триггер "До модификации"
     */
    protected ASTStart beforeModAction;

    /**
     * Триггер "До удаления"
     */
    protected ASTStart beforeDelAction;

    /**
     * Триггер "После удаления"
     */
    protected ASTStart afterDelAction;
    private boolean checkDisabled = false;
    private boolean inherit = true;
    
    protected ComponentAdapter() {
    }

    protected ComponentAdapter(OrFrame frame, OrGuiComponent c,
                               boolean isEditor) throws KrnException {
        this.frame = frame;
        this.comp = c;
        this.isEditor = isEditor;
        this.log = getLog();
        
        PropertyNode pnode = c.getProperties().getChild("view");
        if (pnode != null) {
        	PropertyNode pnode_ = pnode.getChild("langExpr");
            PropertyValue pv = c.getPropertyValue(pnode_);
            if (!pv.isNull() && !pv.objectValue().equals(null)) {
            	try {
					langId=getLangFromExpr((String) pv.objectValue());
				} catch (Exception e) {
                    showErrorNessage(e.getMessage());
					log.error(e, e);
				}
            }
            if (langId<=0) {
	            pnode_ = pnode.getChild("language");
	            if (pnode_ != null) {
	                pv = c.getPropertyValue(pnode_);
	                if (langId<=0 && !pv.isNull() && !pv.getKrnObjectId().equals("") ) {
	                    langId = Long.parseLong(pv.getKrnObjectId());
	                }
	            }
            }
        }
        pnode = c.getProperties();
        if (pnode != null && pnode.getChild("view") != null && pnode.getChild("view").getChild("background") != null) {
            PropertyNode pnColor = pnode.getChild("view").getChild("background").getChild("backgroundColor");
            PropertyValue pv = c.getPropertyValue(pnColor);
            if (!pv.isNull()) {
                defaultBgColor = pv.colorValue();
            } else {
                defaultBgColor = (Color)pnColor.getDefaultValue();
            }
            if (defaultBgColor == null) {
                defaultBgColor = Color.WHITE;
            }
        }

        if (pnode != null && pnode.getChild("view") != null &&
                pnode.getChild("view").getChild("font") != null &&
                pnode.getChild("view").getChild("font").getChild("fontColor") != null) {
            PropertyValue pv = c.getPropertyValue(pnode.getChild(
                    "view").getChild("font").getChild("fontColor"));
            if (!pv.isNull()) {
                defaultFontColor = pv.colorValue();
            } else
                defaultFontColor = (Color)pnode.getChild("view").getChild("font").getChild("fontColor").getDefaultValue();
        }

        PropertyNode pn = c.getProperties().getChild("ref");
        if (pn != null) {
            PropertyNode pn1 = pn.getChild("paramFilters");
            if (pn1 != null) {
                PropertyValue pv = c.getPropertyValue(pn1.getChild("filters"));
                Kernel krn = frame.getKernel();
                if (!pv.isNull()) {
                    
                    FilterRecord[] filters = pv.filterValues();
                    paramFiltersUIDs = new String[filters.length];
                    for (int i = 0; i < filters.length; i++) {
                        FilterRecord filter = filters[i];
                        paramFiltersUIDs[i] = krn.getUId(filter.getObjId());
                    }
                }
                pv = c.getPropertyValue(pn1.getChild("paramName"));
                paramName = (!pv.isNull()) ? pv.stringValue(frame.getKernel()) : "";
                if (paramFiltersUIDs != null && paramName != null) {
                    for (String paramFilterUid : paramFiltersUIDs) {
                    	krn.addFilterParamListener(paramFilterUid, paramName, this);
                    }
            }
            }
            reqGroup = getReqGroup(c);
            setObligations(c);
            setExpression(c);
        }

        // Настройка повденеия компонента
        PropertyNode behavNode = c.getProperties().getChild("pov");
        if (behavNode != null) {
        	// Активность
            PropertyNode pn1 = behavNode.getChild("activity");
            if (pn1 != null) {
            	// Запрет редактирования?
                PropertyNode pn2 = pn1.getChild("editable");
                if (pn2 != null) {
                    PropertyValue pv = c.getPropertyValue(pn2);
                    editable = pv.isNull() ? true : !pv.booleanValue();
                }
            	// Доступность?
                pn2 = pn1.getChild("enabled");
                if (pn2 != null) {
                    PropertyValue pv = c.getPropertyValue(pn2);
                    editable = pv.isNull() ? true : pv.booleanValue();
                }
                // Наследовать активность от родителя?
                pn2 = pn1.getChild("inherit");
                if (pn2 != null) {
                	PropertyValue pv = c.getPropertyValue(pn2);
                    inherit = pv.isNull() ? (Boolean)pn2.getDefaultValue() : pv.booleanValue();
                }
                // Проверять задизэйбленный?
                pn2 = pn1.getChild("checkDisabled");
                if (pn2 != null) {
                    PropertyValue pv = c.getPropertyValue(pn2);
                    if (!pv.isNull()) {
                        checkDisabled = pv.booleanValue();
                    } else {
                        checkDisabled = false;
                    }
                }
            }

            // Триггеры
            // До модификации
            PropertyNode beforeModNode = behavNode.getChild("beforeModAction");
            if (beforeModNode != null) {
                PropertyValue pv = c.getPropertyValue(beforeModNode);
                String expr = pv.isNull() ? "" : pv.stringValue(frame.getKernel());
                if (expr.trim().length() == 0) expr = "";
                if (expr.length() > 0) {
                	if (c instanceof WebComponent && frame instanceof WebFrame) {
                    	long ifcId = ((WebFrame)frame).getObj().id;
                    	String key = ((WebComponent)c).getId() + "_" + OrLang.BEFORE_MODIF_TYPE;
                    	beforeModAction = ClientOrLang.getStaticTemplate(ifcId, key, expr, getLog());
                	} else {
                		beforeModAction = OrLang.createStaticTemplate(expr, log);
                	}
                    Editor e = new Editor(expr);
                    ArrayList<String> paths = e.getRefPaths();
                    for (int j = 0; j < paths.size(); ++j) {
                        String path = paths.get(j);
                        OrRef.createRef(path, false, Mode.RUNTIME, frame.getRefs(),
                                OrRef.TR_CLEAR, frame);
                    }
                }
            }
            // После модификации
            PropertyNode afterModNode = behavNode.getChild("afterModAction");
            if (afterModNode != null) {
                PropertyValue pv = c.getPropertyValue(afterModNode);
                String expr = pv.isNull() ? "" : pv.stringValue(frame.getKernel());
                if (expr.trim().length() == 0) expr = "";
                if (expr.length() > 0) {
                	if (c instanceof WebComponent && frame instanceof WebFrame) {
                    	long ifcId = ((WebFrame)frame).getObj().id;
                    	String key = ((WebComponent)c).getId() + "_" + OrLang.AFTER_MODIF_TYPE;
                    	afterModAction = ClientOrLang.getStaticTemplate(ifcId, key, expr, getLog());
                	} else {
                		afterModAction = OrLang.createStaticTemplate(expr, log);
                	}
                    Editor e = new Editor(expr);
                    ArrayList<String> paths = e.getRefPaths();
                    for (int j = 0; j < paths.size(); ++j) {
                        String path = paths.get(j);
                        OrRef.createRef(path, false, Mode.RUNTIME, frame.getRefs(),
                                OrRef.TR_CLEAR, frame);
                    }
                }
            }
            // До удаления
            PropertyNode beforeDelNode = behavNode.getChild("beforeDelete");
            if (beforeDelNode != null) {
                PropertyValue pv = c.getPropertyValue(beforeDelNode);
                String expr = pv.isNull() ? "" : pv.stringValue(frame.getKernel());
                if (expr.trim().length() == 0) expr = "";
                if (expr.length() > 0) {
                	if (c instanceof WebComponent && frame instanceof WebFrame) {
                    	long ifcId = ((WebFrame)frame).getObj().id;
                    	String key = ((WebComponent)c).getId() + "_" + OrLang.BEFORE_DELETE_TYPE;
                    	beforeDelAction = ClientOrLang.getStaticTemplate(ifcId, key, expr, getLog());
                	} else {
                		beforeDelAction = OrLang.createStaticTemplate(expr, log);
                	}
                    Editor e = new Editor(expr);
                    ArrayList<String> paths = e.getRefPaths();
                    for (int j = 0; j < paths.size(); ++j) {
                        String path = paths.get(j);
                        OrRef.createRef(path, false, Mode.RUNTIME, frame.getRefs(),
                                OrRef.TR_CLEAR, frame);
                    }
                }
            }
            // После удаления
            PropertyNode afterDelNode = behavNode.getChild("afterDelete");
            if (afterDelNode != null) {
                PropertyValue pv = c.getPropertyValue(afterDelNode);
                String expr = pv.isNull() ? "" : pv.stringValue(frame.getKernel());
                if (expr.trim().length() == 0) expr = "";
                if (expr.length() > 0) {
                	if (c instanceof WebComponent && frame instanceof WebFrame) {
                    	long ifcId = ((WebFrame)frame).getObj().id;
                    	String key = ((WebComponent)c).getId() + "_" + OrLang.AFTER_DELETE_TYPE;
                    	afterDelAction = ClientOrLang.getStaticTemplate(ifcId, key, expr, getLog());
                	} else {
                		afterDelAction = OrLang.createStaticTemplate(expr, log);
                	}
                    Editor e = new Editor(expr);
                    ArrayList<String> paths = e.getRefPaths();
                    for (int j = 0; j < paths.size(); ++j) {
                        String path = paths.get(j);
                        OrRef.createRef(path, false, Mode.RUNTIME, frame.getRefs(),
                                OrRef.TR_CLEAR, frame);
                    }
                }
            }
        }

        if ((c instanceof OrTabbedPaneComponent) || (c instanceof OrSplitPaneComponent) ||
                (c instanceof OrScrollPaneComponent) || (c instanceof OrWebLayoutPane)) {
            setActivityRef(c);
            setVisibleRef(c);
        } else {
            createDataRef(c);
            setActivityRef(c);
            setVisibleRef(c);
            if (!isEditor) {
                createConstraintsRef(c);
                createConstraintsValueRef(c);
                createEvalRef(c);
                createCompBackColorRef(c);
                createCompFontColorRef(c);
            }
            setRadioGroup(c);
        }
    }

    protected void createDataRef(OrGuiComponent c) throws KrnException {
        if (c instanceof OrWebLabel) {
            return;
        }
        PropertyNode prop = c.getProperties();
        PropertyNode rprop = prop.getChild("ref").getChild("data");
        PropertyValue pv = c.getPropertyValue(rprop);
        if (!pv.isNull() && pv.stringValue(frame.getKernel()).length() > 0) {
            try {
                propertyName = "Свойство: Данные";
                //@todo Зачем это нужно? Ерик.
                if (this instanceof TableAdapter) {
                    boolean hasParentRef = false;
                    Map<String, OrRef> refs = frame.getRefs();
                    Iterator<String> it = refs.keySet().iterator();
                    while (it.hasNext()) {
                        String key = it.next();
                        if (key != null && pv.stringValue(frame.getKernel()).startsWith(key)) {
                            hasParentRef = true;
                            break;
                        }
                    }
                    if (!hasParentRef)
                        dataRef = OrRef.createContentRef(pv.stringValue(frame.getKernel()),
                                Constants.RM_ALWAYS, Mode.RUNTIME,
                                frame.getTransactionIsolation(), frame);
                }

                if (dataRef == null)
                    dataRef = OrRef.createRef(pv.stringValue(frame.getKernel()), isEditor, Mode.RUNTIME,
                                    frame.getRefs(), frame.getTransactionIsolation(), frame);
                if (!isEditor) {
                    dataRef.addCheckContext(this);
                    dataRef.addOrRefListener(this);
                }
            } catch (Exception e) {
                showErrorNessage(e.getMessage() + pv.stringValue(frame.getKernel()));
                log.error(e, e);
            }
            if (langId > 0) {
                dataRef.addLanguage(langId);
            }
        }
/*
        rprop = prop.getChild("editable");
        if (rprop != null) {
            pv = c.getPropertyValue(rprop);
            if (!pv.isNull()) {
                boolean res = pv.booleanValue();
                if (res)
*/
                    //isEnabled = !editable;
/*
                else
                    isEnabled = true;
                if (dataRef != null) {
                    dataRef.setActive(isEnabled);
                }
*/
/*
            } else {
                Object defVal = rprop.getDefaultValue();
                if (defVal != null) {
                    isEnabled = !((Boolean)defVal).booleanValue();
                }
            }
*/
        //}
    }

    protected void setRadioGroup(OrGuiComponent c) {
        PropertyNode prop = c.getProperties();
        PropertyNode rprop = prop.getChild("pov");
        if (rprop != null) {
            PropertyNode c_rprop = rprop.getChild("radioGroup");
            if (c_rprop != null) {
                PropertyValue pv = c.getPropertyValue(c_rprop);
                if (pv != null && !pv.isNull()) {
                    radioGroup = pv.stringValue(frame.getKernel());
                }
            }
        }
    }

    protected void setActivityRef(OrGuiComponent c) {
        PropertyNode prop = c.getProperties();
        PropertyNode rprop = prop.getChild("pov");
        if (rprop != null) {
            PropertyNode pn = rprop.getChild("activity");
            if (pn != null) {
                PropertyNode  pn1 = pn.getChild("activExpr");
                if (pn1 != null) {
                    PropertyValue pv = c.getPropertyValue(pn1);
                    String fx = "";
                    if (!pv.isNull() && !"".equals(pv.stringValue(frame.getKernel()))) {
                        try {
                            propertyName = "Свойство: Активность";
                            fx = pv.stringValue(frame.getKernel());
                            if (fx.trim().length() > 0) {
                                activityRef = new OrCalcRef(fx, isEditor, Mode.RUNTIME, frame.getRefs(),
                                        frame.getTransactionIsolation(), frame, c, propertyName, this);
                                activityRef.addOrRefListener(this);
/*
                                if (!activityRef.hasParents()) {
                                    //activityRef.refresh(null);
                                    if (parentAdapter != null && parentAdapter.checkEnabled()) {
                                        isEnabled = checkEnabled();
                                        setEnabled(isEnabled);
                                    } else {
                                        isEnabled = false;
                                        setEnabled(false);
                                    }
                                }
*/
                            }
                        } catch (Exception e) {
                            showErrorNessage(e.getMessage() + fx);
                            log.error(e, e);
                        }
                    }
                }
            }
        }
    }

	protected void setVisibleRef(OrGuiComponent c) {
        PropertyNode prop = c.getProperties();
        PropertyNode rprop = prop.getChild("pov");
        if (rprop != null) {
            PropertyNode  pn1 = rprop.getChild("isVisible");
            if (pn1 != null) {
                PropertyValue pv = c.getPropertyValue(pn1);
                String fx = "";
                if (!pv.isNull() && !"".equals(pv.stringValue(frame.getKernel()))) {
                    try {
                        propertyName = "Свойство: Видимость";
                        fx = pv.stringValue(frame.getKernel());
                        if (fx.trim().length() > 0) {
                            visibleRef = new OrCalcRef(fx, isEditor, Mode.RUNTIME, frame.getRefs(),
                                    frame.getTransactionIsolation(), frame, c, propertyName, this);
                            visibleRef.addOrRefListener(this);
                        }
                    } catch (Exception e) {
                        showErrorNessage(e.getMessage() + fx);
                        log.error(e, e);
                    }
                }
            }
        }
    }


    protected void createConstraintsRef(OrGuiComponent c) {
        PropertyNode prop = c.getProperties();
        PropertyNode rprop = prop.getChild("obligation");
        if (rprop != null) {
            PropertyValue pv = c.getPropertyValue(rprop.getChild("calc"));
            String fx = "";
            if (!pv.isNull() && !"".equals(pv.stringValue(frame.getKernel())))  {
                try {
                    propertyName = "Свойство: Обязательность-Формула";
                    fx = pv.stringValue(frame.getKernel());
                    if (fx.trim().length() > 0) {
                        constraintsRef = new OrCalcRef(fx, false, Mode.RUNTIME, frame.getRefs(),
                                frame.getTransactionIsolation(), frame, c, propertyName, this);
                        constraintsRef.addOrRefListener(this);
                        //if (!dataRef.isColumn()) {
                            if (!constraintsRef.hasParents()) {
                                constraintsRef.refresh(null);
                                if (constraintsRef.getValue(langId) != null) {
                                    checkConstr = (((Number) constraintsRef.getValue(langId)).intValue() == 1);
                                }
                            }
                        //}
                    }
                } catch (Throwable e) {
                    showErrorNessage(e.getMessage() + fx);
                    log.error(e, e);
                }
            }
        }
    }

    protected void createConstraintsValueRef(OrGuiComponent c) {
        PropertyNode prop = c.getProperties();
        PropertyNode rprop = prop.getChild("obligation");
        if (rprop != null) {
            PropertyValue pv = c.getPropertyValue(rprop.getChild("calcValue"));
            String fx = "";
            if (!pv.isNull() && !"".equals(pv.stringValue(frame.getKernel())))  {
                try {
                    propertyName = "Свойство: Обязательность-Формула";
                    fx = pv.stringValue(frame.getKernel());
                    if (fx.trim().length() > 0) {
                        constraintsValueRef = new OrCalcRef(fx, false, Mode.RUNTIME, frame.getRefs(),
                                frame.getTransactionIsolation(), frame, c, propertyName, this);
                        constraintsValueRef.addOrRefListener(this);
                        //if (!dataRef.isColumn()) {
                            if (!constraintsValueRef.hasParents()) {
                                constraintsValueRef.refresh(null);
                                if (constraintsValueRef.getValue(langId) != null) {
                                    checkConstrValue = (((Number) constraintsValueRef.getValue(langId)).intValue() == 1);
                                }
                            }
                        //}
                    }
                } catch (Throwable e) {
                    showErrorNessage(e.getMessage() + fx);
                    log.error(e, e);
                }
            }
        }
    }

    protected void createCompBackColorRef(OrGuiComponent c) {
        PropertyNode prop = c.getProperties();
        PropertyNode pn = prop.getChild("view");
        if (pn != null) {
            pn = pn.getChild("background");
            if (pn != null) {
                PropertyNode pn1 = pn.getChild("backgroundColorExpr");
                if (pn1 != null) {
                    PropertyValue pv = c.getPropertyValue(pn1);
                    if (!pv.isNull()) {
                        try {
                            propertyName = "Свойство: Цвет фона";
                            String fx = pv.stringValue(frame.getKernel());
                            if (fx.trim().length() > 0) {
                                boolean isColumn = this.isEditor();
                                backColorRef = new OrCalcRef(fx, isColumn, Mode.RUNTIME, frame.getRefs(),
                                        frame.getTransactionIsolation(), frame, c, propertyName, this);
                                backColorRef.addOrRefListener(this);
                                int backColor = -1;
                                if (!backColorRef.hasParents()) {
                                    backColorRef.refresh(null);
                                    if (backColorRef.getValue(0) != null) {
                                        Object val = backColorRef.getValue(0);
                                        if (val instanceof Number) {
                                            backColor = ((Number) val).intValue();
                                            if (c instanceof Component)
                                                ((Component) c).setBackground(new Color(backColor));
                                            else if (c instanceof WebComponent)
                                                ((WebComponent) c).setBackground(new Color(backColor));

                                        } else if (val instanceof String) {
                                            if (c instanceof Component)
                                                ((Component) c).setBackground(kz.tamur.rt.Utils.getColorByName(val.toString()));
                                            else if (c instanceof WebComponent)
                                                ((WebComponent) c).setBackground(kz.tamur.rt.Utils.getColorByName(val.toString()));
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            showErrorNessage(e.getMessage());
                            log.error(e, e);
                        }
                    }
                }
            }
        }
    }

    protected void createCompFontColorRef(OrGuiComponent c) {
        PropertyNode prop = c.getProperties();
        PropertyNode pn = prop.getChild("view");
        if (pn != null) {
            PropertyNode pn1 = pn.getChild("font");
            if (pn1 != null) {
                PropertyNode pn2 = pn1.getChild("fontExpr");
                if (pn2 != null) {
                    PropertyValue pv = c.getPropertyValue(pn2);
                    if (!pv.isNull()) {
                        try {
                            propertyName = "Свойство: Цвет шрифта";
                            String fx = pv.stringValue(frame.getKernel());
                            if (fx.trim().length() > 0) {
                                boolean isColumn = c instanceof OrColumnComponent;
                                fontColorRef = new OrCalcRef(fx, isColumn, Mode.RUNTIME, frame.getRefs(),
                                        frame.getTransactionIsolation(), frame, c, propertyName, this);
                                fontColorRef.addOrRefListener(this);
                                int fontColor = -1;
                                if (!fontColorRef.hasParents()) {
                                    fontColorRef.refresh(null);
                                    if (fontColorRef.getValue(0) != null) {
                                        Object val = fontColorRef.getValue(0);
                                        if (val instanceof Number) {
                                            fontColor = ((Number) val).intValue();
                                            if (c instanceof Component)
                                                ((Component) c).setForeground(new Color(fontColor));
                                            else if (c instanceof WebComponent)
                                                ((WebComponent) c).setForeground(new Color(fontColor));
                                        } else if (val instanceof String) {
                                            if (c instanceof Component)
                                                ((Component) c).setForeground(kz.tamur.rt.Utils.getColorByName(val.toString()));
                                            else if (c instanceof WebComponent)
                                                ((WebComponent) c)
                                                        .setForeground(kz.tamur.rt.Utils.getColorByName(val.toString()));
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            showErrorNessage(e.getMessage());
                            log.error(e, e);
                        }
                    }
                }
            }
        }
    }


    protected void createEvalRef(OrGuiComponent c) {
        PropertyNode prop = c.getProperties();
        prop = prop.getChild("ref");
        if (prop != null) {
            prop = prop.getChild("calcData");
            if (prop != null) {
                PropertyValue pv = c.getPropertyValue(prop);
                String expr = pv.stringValue(frame.getKernel());
                if (expr.trim().length() > 0) {
                    try {
                        propertyName = "Свойство: Данные.Формула";
                        calcRef = new OrCalcRef(expr, false, Mode.RUNTIME,
                                frame.getRefs(), frame.getTransactionIsolation(),
                                frame, c, propertyName, this);
                        if (!calcRef.hasParents()) {
                            calcRef.refresh(null);
                        }
                        calcRef.addOrRefListener(this);
                    } catch (Exception e) {
                        showErrorNessage(e.getMessage() + expr);
                        log.error(e, e);
                    }
                }
            }
        }
    }

    public OrRef getDataRef() {
        return dataRef;
    }

    public OrCalcRef getDataCalcRef() {
        return calcRef;
    }

    public String getRadioGroup() {
        return radioGroup;
    }

    // OrRefListener
    public void valueChanged(OrRefEvent e) {
        OrRef ref = e.getRef();
        if (ref == null)
            return;
        if (ref == activityRef) {
            if (parentAdapter != null) {
                if (!inherit) {

                    isEnabled = checkEnabled();
                    setEnabled(isEnabled);
                } else {
                    if (parentAdapter.checkEnabled()) {
                        isEnabled = checkEnabled();
                        setEnabled(isEnabled);
                    } else {
                        isEnabled = false;
                        setEnabled(false);
                    }
                }
            } else {
                isEnabled = checkEnabled();
                setEnabled(isEnabled);
            }
        } else if (ref == constraintsRef) {
            if (constraintsRef.getValue(langId) != null) {
                checkConstr = (((Number) constraintsRef.getValue(langId)).intValue() == 1);
            }
        } else if (ref == constraintsValueRef) {
            if (constraintsValueRef.getValue(langId) != null) {
                checkConstrValue = (((Number) constraintsValueRef.getValue(langId)).intValue() == 1);
            }
        } else if (ref == backColorRef && !(comp instanceof OrColumnComponent)) {
            Object backColor = null;
            if (backColorRef.getValue(langId) != null) {
                backColor = backColorRef.getValue(langId);
                if (backColor instanceof Number) {
                    bgColor = new Color(((Number) backColor).intValue());
                } else if (backColor instanceof String) {
                    bgColor = kz.tamur.rt.Utils.getColorByName(backColor.toString());
                }
                if (comp instanceof Component)
                    ((Component) comp).setBackground(bgColor);
                else if (comp instanceof WebComponent)
                    ((WebComponent) comp).setBackground(bgColor);
            }
        } else if (ref == fontColorRef && !(comp instanceof OrColumnComponent)) {
            Object fColor = null;
            if (fontColorRef.getValue(langId) != null) {
                fColor = fontColorRef.getValue(langId);
                if (fColor instanceof Number) {
                    fontColor = new Color(((Number) fColor).intValue());
                } else if (fColor instanceof String) {
                    fontColor = kz.tamur.rt.Utils.getColorByName(fColor.toString());
                }
                if (comp instanceof Component)
                    ((Component) comp).setForeground(fontColor);
                else if (comp instanceof WebComponent)
                    ((WebComponent) comp).setForeground(fontColor);
            }
        } else if (ref == dataRef || ref == calcRef) {
            if (!selfChange) {
                try {
                    selfChange = true;
                    value = ref.getValue(langId);
                    if (value != null && value.toString().length() == 0) {
                        value = null;
                    }
                    if (comp instanceof OrTextComponent 
                    		|| comp instanceof OrComboBoxComponent 
                    		|| comp instanceof OrDateComponent
                            || comp instanceof OrMemoComponent 
                            || comp instanceof OrHyperPopupComponent
                            || comp instanceof OrGuiComponent) {
                        // Пока реализовано только для след компонентов
                        update(value);
                    }
                } catch (Exception ex) {
                    // @todo Непонятная ошибка при первом открытии комбоколонки
                    log.warn(ex.toString());
                } finally {
                    selfChange = false;
                }
            } else if (comp instanceof OrWebTableColumn) {
                try {
                    selfChange = true;
                    ((OrWebTableColumn) comp).getEditor().getAdapter().setValue(ref.getValue(langId));
                } finally {
                    selfChange = false;
                }
            }
        } else if (ref == visibleRef) {
            if (visibleRef.getValue(langId) != null) {
                setVisible(((Number) visibleRef.getValue(langId)).intValue() == 1);
            } else {
            	setVisible(false);
            }
        }
    }

    public void update(Object value) {
        updateParamFilters(value);
        if (comp instanceof OrTextComponent) {
        	((OrTextComponent)comp).setValue(value);
        } else if (comp instanceof OrComboBoxComponent) {
        	((OrComboBoxComponent)comp).setValue(value);
        } else if (comp instanceof OrDateComponent) {
        	((OrDateComponent)comp).setValue(value);
        } else if (comp instanceof OrMemoComponent) {
        	((OrMemoComponent)comp).setValue(value);
        } else if (comp instanceof OrHyperPopupComponent) {
        	// NOP
        } else if (comp instanceof BarcodeComponent) {
        	((BarcodeComponent)comp).setValue(value);
        }
    }

    public boolean checkEnabled() {
        boolean isEnabled = true;
        if (dataRef != null && parentAdapter != null) 
        	isEnabled = parentAdapter.isActivityEnabled();
    	//boolean isEnabled = (parentAdapter != null) ? parentAdapter.isSelfEnabled() : true;
        if (isEnabled && dataRef != null) {
            final InterfaceManager im =
                    frame.getInterfaceManager();
            if (im != null) {
                int mode = im.getEvaluationMode();
                isEnabled = (mode != InterfaceManager.READONLY_MODE);
                if (isEnabled) {
                    isEnabled = (mode != InterfaceManager.SPR_RO_MODE);
                }
            }
        }
        if (this instanceof HyperPopupAdapter || this instanceof PopupColumnAdapter || this instanceof DocFieldAdapter) {
        	if (inherit) 
        		 isEnabled = editable && isEnabled;
    		 else
    			 isEnabled = editable;
        } else if (this instanceof ButtonAdapter) {
        	isEnabled = editable && (!((ButtonAdapter)this).hasSetAttr() || isEnabled);
        } else if (isEnabled) {
            isEnabled = editable;
        }
        if (comp instanceof OrPanelComponent) {
            isEnabled = ((OrPanelComponent)comp).isPanelEnabled();
        }
        if (comp instanceof OrHyperLabelComponent) {
            isEnabled = ((OrHyperLabelComponent)comp).isArchiv();
        }
        if (activityRef != null) {
            if (isEnabled || (!Constants.IS_UL_PROJECT && !inherit)) {
                if (activityRef.getValue(getLangId()) != null) {
                    isEnabled = (((Number) activityRef.getValue(getLangId())).intValue() == 1);
                } else {
                    isEnabled = false;
                }
            }
        }
        return isEnabled;
    }


    public void changesCommitted(OrRefEvent e) {
    }

    public void changesRollbacked(OrRefEvent e) {
    }

    public void checkReqGroups(OrRef ref, List<ReqMsgsList.MsgListItem> errMsgs, List<ReqMsgsList.MsgListItem> reqMsgs, Stack<Pair> locs) {
    }

    public void stateChanged(OrRefEvent e) {
        if (comp instanceof WebComponent) {
            Integer state = getState(new Integer(0));
            if (state == null) state = 0; 
        	((WebComponent)comp).setState(state);
        } else if (comp instanceof OrWebTableColumn) {
            ((OrWebTableColumn)comp).setStates(states);
        }
    }

    public void pathChanged(OrRefEvent e) {
    }

    private int getReqGroup(OrGuiComponent c) {
        int reqGroup = 0;
        PropertyNode prop = c.getProperties();
        PropertyNode rprop = prop.getChild("obligation");
        if (rprop != null) {
            PropertyNode prn = rprop.getChild("group");
            PropertyValue pv = c.getPropertyValue(prn);
            if (!pv.isNull()) {
                reqGroup = pv.intValue();
            }
        }
        return reqGroup;
    }

    private void setObligations(OrGuiComponent c) {
        PropertyNode prop = c.getProperties();
        PropertyNode rprop = prop.getChild("obligation");
        if (rprop != null) {
            PropertyNode prn = rprop.getChild("input");
            PropertyValue pv = c.getPropertyValue(prn);
            if (!pv.isNull()) {
                enterDB = pv.intValue();
            } else
                enterDB = 0;
            prn = rprop.getChild("message");
            pv = c.getPropertyValue(prn);
            if (!pv.isNull()) {
                reqMsg = (String)pv.resourceStringValue().first;
            }
        }
    }

    private void setExpression(OrGuiComponent c) {
        PropertyNode prop = c.getProperties();
        PropertyNode rprop = prop.getChild("constraints");
        if (rprop != null) {
            PropertyNode rprop2 = rprop.getChild("formula");
            if (rprop2 != null) {
                PropertyNode prn = rprop2.getChild("expr");
                PropertyValue pv = c.getPropertyValue(prn);
                if (!pv.isNull()) {
                    cexpr = pv.stringValue(frame.getKernel());
                	if (c instanceof WebComponent && frame instanceof WebFrame) {
                    	long ifcId = ((WebFrame)frame).getObj().id;
                    	String key = ((WebComponent)c).getId() + "_" + OrLang.CONSTRAINTS_TYPE;
                    	ctemplate = ClientOrLang.getStaticTemplate(ifcId, key, cexpr, getLog());
                	} else {
                		ctemplate = OrLang.createStaticTemplate(cexpr, log);
                	}
                } else {
                    cexpr = null;
                    ctemplate = null;
                }
                pv = c.getPropertyValue(rprop2.getChild("message"));
                if (!pv.isNull()) {
                    constrMsg = (String)pv.resourceStringValue().first;
                }
            }
        }
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }
    
	public void setVisible(boolean visible) {
		this.isVisible = visible;
		if (comp instanceof WebComponent) {
			WebComponent cnt = ((WebComponent) comp).getParent();
			if (cnt instanceof OrWebTabbedPane) {
				((OrWebTabbedPane) cnt).setTabVisible((WebComponent) comp, isVisible);
			} else if (cnt == null && parentAdapter != null) {
				OrGuiComponent parentComp = parentAdapter.getComponent();
				if (parentComp instanceof OrWebTabbedPane) {
					((OrWebTabbedPane) parentComp).setTabVisible((WebComponent) comp, isVisible);
				} else if (parentComp instanceof OrWebAccordion) {
					((OrWebAccordion) parentComp).setCardionPanelVisible((WebComponent) comp, isVisible);
				}
			} else {
				((WebComponent) comp).setVisible(isVisible);
			}
		}
	}
    
    protected void updateParamFilters(Object value) {
        try {
            if (value != null && value.toString().length() == 0) {
                value = null;
            }
            if (paramFiltersUIDs != null && paramFiltersUIDs.length > 0 && paramName != null && paramName.length() > 0) {
                for (int i = 0; i < paramFiltersUIDs.length; i++) {
                    String paramFiltersUID = paramFiltersUIDs[i];
                    Kernel krn = frame.getKernel();
                    if (value instanceof List) {
                        krn.setFilterParam(paramFiltersUID, paramName, (List)value);
                    } else {
                        krn.setFilterParam(paramFiltersUID, paramName, value!=null ? Collections.singletonList(value):null);
                    }
                }
            }
        } catch (Exception e) {
            log.error(e, e);
        }
    }

    public void setParentAdapter(ContainerAdapter parentAdapter) {
        this.parentAdapter = parentAdapter;
/*        if(!isEditor && isVisible() && dataRef != null) {
            dataRef.addCheckContext(this);
        }
*/    }

    public void showErrorNessage(String message) {
        PropertyNode node = comp.getProperties().getChild("title");
        String componentName = "Компонент: ";
        if (node != null) {
            PropertyValue pv = comp.getPropertyValue(node);
            if (!pv.isNull()) {
                componentName = componentName + pv.stringValue(frame.getKernel());
            }
        }
        componentName = componentName + " [" + comp.getClass().getName().substring(
                Constants.COMPS_PACKAGE.length()) + "]";
        String mainMessage = message;
        if (comp instanceof WebComponent) {
            log.error(mainMessage + "\n" + componentName + "\n\n" + propertyName);
        }
    }

    public Color getBgColor(int index) {
        if (backColorRef != null) {
            OrRef.Item item = backColorRef.getItem(0, index);
            Object o = (item != null) ? item.getCurrent() : null;
            if (o instanceof Number) {
                return new Color(((Number)o).intValue());
            } else if (o instanceof String) {
                return kz.tamur.rt.Utils.getColorByName(o.toString());
            }
        }
        return defaultBgColor;
    }

    public Color getFontColor(int index) {
        if (fontColorRef != null) {
            Color col = null;
            OrRef.Item item = fontColorRef.getItem(0, index);
            Object o = (item != null) ? item.getCurrent() : null;
            if (o instanceof Number) {
                col = new Color(((Number)o).intValue());
            } else if (o instanceof String) {
                col = kz.tamur.rt.Utils.getColorByName(o.toString());
            }
            return col;
        }
        return defaultFontColor;
    }

    public void clearFilterParam() {
        if (dataRef == null) {
            updateParamFilters(null);
        }
    }

    //CheckContext

    public long getLangId() {
        return langId;
    }

    public int getReqGroup() {
        return reqGroup;
    }

    public int getEnterDB() {
        return enterDB;
    }

    public boolean isActive() {
        return (isVisible() && (isEnabled || checkDisabled));
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public boolean isVisible() {
    	if (isVisible && parentAdapter != null) {
    		return parentAdapter.isVisible();
    	}
        return isVisible;
    }

    public boolean isActivityEnabled() {
    	if (isEnabled && parentAdapter != null) {
    		return parentAdapter.isActivityEnabled();
    	}
        return isEnabled;
    }

    public OrRef getRef() {
        return dataRef;
    }

    public String getCExpr() {
        return cexpr;
    }

    public String getReqMsg() {
        return frame.getString(reqMsg);
    }

    public ASTStart getCTemplate() {
        return ctemplate;
    }

    public boolean isCheckConstr() {
        return checkConstr;
    }

    public boolean isCheckConstrValue() {
        return checkConstrValue;
    }

    public void setState(Integer index, Integer type) {
        states.put(index, type);
    }

    public void removeState(Integer index) {
        states.remove(index);
    }

    public void clearStates() {
        states.clear();
    }

    public Integer getState(Integer index) {
        return states.get(index);
    }

    public String getConstrMsg() {
        return frame.getString(constrMsg);
    }

    public boolean isEditor() {
    	return isEditor;
    }
    
    public Object changeValue(Object value) throws Exception {
        if (!selfChange) { 
            // Триггер "До модификации"
            value = doBeforeModification(value);
            if (isEditor || !Funcs.equals(value, this.value)) {
                boolean calcOwner = OrCalcRef.setCalculations();
                try {
	                if (dataRef != null) {
	                    OrRef.Item item = dataRef.getItem(langId);
	                    try {
	                        selfChange = true;
	                        if (item != null && value != null) {
	                            dataRef.changeItem(value, this, this);
	                        } else if (item != null && value == null) {
	                            dataRef.deleteItem(this, this);
	                        } else {
	                            dataRef.insertItem(0, value, this, this, false);
	                        }
	                    } finally {
	                        selfChange = false;
	                    }
	                }
	                updateParamFilters(value);
            	} catch (Exception e) {
            		log.error(e, e);
            	} finally {
    	            if (calcOwner)
    	            	OrCalcRef.makeCalculations();
            	}

                // Обновляем текущее значение
                this.value = value;
                if (comp instanceof OrWebTextField) {
                    ((OrWebTextField) comp).setValue(value);
                }
                // Триггер "После модификации"
                doAfterModification();
            }
        }
        return this.value;
    }

    private long getLangFromExpr(String expr) throws Exception {
        long res=-1;
        if(expr!=null && !"".equals(expr)){
            ClientOrLang orlang = new ClientOrLang(frame);
            Map<String, Object> vc = new HashMap<String, Object>();
            boolean calcOwner = OrCalcRef.setCalculations();
            orlang.evaluate(OrLang.createStaticTemplate(expr), vc, this, new Stack<String>());
			if (calcOwner)
				OrCalcRef.makeCalculations();
			Object lang_res=vc.get("RETURN");
			if(lang_res!=null && lang_res instanceof KrnObject)
				res=((KrnObject)lang_res).id;
        }
		return res;
    }
    protected void doAfterModification() throws Exception {
        if (afterModAction != null) {
            ClientOrLang orlang = new ClientOrLang(frame);
            Map<String, Object> vc = new HashMap<String, Object>();
            vc.put("OBJS", Funcs.makeList(((WebFrame)frame).getInitialObjs()));
            if (dataRef != null && dataRef.isColumn()) {
                OrRef p = dataRef;
                while (p!=null && p.isColumn()) {
                    p = p.getParent();
                }
                if (p!=null && p.getItem(0) != null) {
                    Object obj = p.getItem(0).getCurrent();
                    vc.put("SELOBJ", obj);
                }
            }
            boolean calcOwner = OrCalcRef.setCalculations();
            try {
                orlang.evaluate(afterModAction, vc, this, new Stack<String>());
            } catch(Exception ex) {
                log.error("|USER: " + ((WebFrame)frame).getSession().getUserName() 
                		+ "| interface id=" + ((WebFrame)frame).getObj().id
                		+ "| ref=" + getRef() 
                		+ "| value=" + value);

                Util.showErrorMessage(comp, ex.getMessage(), "Действие после модификации");
            	log.error("Ошибка при выполнении формулы 'Действие после модификации' компонента '" + (comp != null ? comp.getClass().getName() : "") + "', uuid: " + getUUID());
                log.error(ex, ex);
	        } finally {
				if (calcOwner)
					OrCalcRef.makeCalculations();
            }
        }
    }

    protected Object doBeforeModification(Object value) throws Exception {
        if (beforeModAction != null) {
            ClientOrLang orlang = new ClientOrLang(frame);
            Map<String, Object> vc = new HashMap<String, Object>();
            vc.put("VALUE", value);
            vc.put("OBJS", Funcs.makeList(((WebFrame)frame).getInitialObjs()));
            if (dataRef != null && dataRef.isColumn()) {
                OrRef p = dataRef;
                while (p!=null && p.isColumn()) {
                    p = p.getParent();
                }
                if (p!=null && p.getItem(0) != null) {
                    Object obj = p.getItem(0).getCurrent();
                    vc.put("SELOBJ", obj);
                }
            }
            boolean calcOwner = OrCalcRef.setCalculations();
            try {
                orlang.evaluate(beforeModAction, vc, this, new Stack<String>());
            } catch(Exception ex) {
                Util.showErrorMessage(comp, ex.getMessage(), "Действие перед модификацией");
            	log.error("Ошибка при выполнении формулы 'Действие перед модификацией' компонента '" + (comp != null ? comp.getClass().getName() : "") + "', uuid: " + getUUID());
                log.error(ex, ex);
	        } finally {
				if (calcOwner)
					OrCalcRef.makeCalculations();
            }
            return vc.get("RETURN");
        }
        return value;
    }

    protected Object doBeforeDelete(List<ComponentAdapter> values) throws Exception {
        if (beforeDelAction != null) {
            ClientOrLang orlang = new ClientOrLang(frame);
            Map<String, Object> vc = new HashMap<String, Object>();
            vc.put("SELOBJS", values);
            if (dataRef != null && dataRef.isColumn()) {
                OrRef p = dataRef;
                while (p!=null && p.isColumn()) {
                    p = p.getParent();
                }
                if (p!=null && p.getItem(0) != null) {
                    Object obj = p.getItem(0).getCurrent();
                    vc.put("SELOBJ", obj);
                }
            }
            boolean calcOwner = OrCalcRef.setCalculations();
            try {
                orlang.evaluate(beforeDelAction, vc, this, new Stack<String>());
            } catch(Exception ex) {
                Util.showErrorMessage(comp, ex.getMessage(), "Действие перед удалением");
            	log.error("Ошибка при выполнении формулы 'Действие перед удалением' компонента '" + (comp != null ? comp.getClass().getName() : "") + "', uuid: " + getUUID());
                log.error(ex, ex);
	        } finally {
				if (calcOwner)
					OrCalcRef.makeCalculations();
            }
            return vc.get("RETURN");
        }
        return value;
    }

    protected void doAfterDelete(Object value) throws Exception {
        if (beforeModAction != null) {
            ClientOrLang orlang = new ClientOrLang(frame);
            Map<String, Object> vc = new HashMap<String, Object>();
            vc.put("VALUE", value);
            if (dataRef != null && dataRef.isColumn()) {
                OrRef p = dataRef;
                while (p!=null && p.isColumn()) {
                    p = p.getParent();
                }
                if (p!=null && p.getItem(0) != null) {
                    Object obj = p.getItem(0).getCurrent();
                    vc.put("SELOBJ", obj);
                }
            }
            boolean calcOwner = OrCalcRef.setCalculations();
            try {
                orlang.evaluate(beforeModAction, vc, this, new Stack<String>());
            } catch(Exception ex) {
                Util.showErrorMessage(comp, ex.getMessage(), "Действие после удаления");
            	log.error("Ошибка при выполнении формулы 'Действие после удаления' компонента '" + (comp != null ? comp.getClass().getName() : "") + "', uuid: " + getUUID());
                log.error(ex, ex);
	        } finally {
				if (calcOwner)
					OrCalcRef.makeCalculations();
            }
        }
    }

    public OrGuiComponent getComponent() {
    	return comp;
    }
    
    public OrFrame getFrame() {
    	return frame;
    }

    public long getComponentLangId() {
        return langId;
    }

    public void setComponentLangId(long langId) {
        this.langId = langId;
    }

    public void filterParamChanged(String fuid, String pid, List<?> values) {
    	if (pid.equals(paramName)) {
	        value = values != null && values.size() > 0 ? values.get(0) : null;
	        if (comp instanceof OrWebDateField) {
	            ((OrWebDateField) comp).setValue(value);
	        } else if (comp instanceof OrWebComboBox) {
	            ((OrWebComboBox) comp).setValue(value);
	        } else if (comp instanceof OrTextComponent) {
	            ((OrTextComponent) comp).setValue(value);
	        }
        }
    }

    public void clearParam() {
    }

    public Log getLog() {
    	if (log == null) {
	    	if (frame != null)
	            this.log = WebSessionManager.getLog(frame.getKernel().getUserSession().dsName, frame.getKernel().getUserSession().logName);
	    	else
	    		this.log = WebSessionManager.getLog(null, "");
    	}
    	return log;
    }

    public void setValue(Object value) {
        this.value = value;
    }
    
    public String getUUID() {
    	return comp != null ? comp.getUUID() : null;
    }
    
    /**
     * Наследует ли компонент свойство активности родительского коспонента.
     * 
     * @return <code>true</code>, если наследует.
     */
    public boolean isInherit() {
        return inherit;
    }
}
