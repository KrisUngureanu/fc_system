package kz.tamur.rt.adapters;

import com.cifs.or2.client.FilterParamListener;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.util.expr.Editor;

import kz.tamur.comps.*;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.ui.button.OrButtonUI;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.guidesigner.filters.FilterRecord;
import kz.tamur.lang.OrLang;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.rt.CheckContext;
import kz.tamur.rt.InterfaceManager;
import kz.tamur.rt.InterfaceManagerFactory;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.orlang.ClientOrLang;
import kz.tamur.util.Funcs;
import kz.tamur.util.ReqMsgsList;
import kz.tamur.util.Pair;
import kz.tamur.rt.Utils;

import javax.swing.*;

import java.awt.*;
import java.util.*;
import java.util.List;

public abstract class ComponentAdapter implements OrRefListener, CheckContext, FilterParamListener {

    protected OrFrame frame;
    protected OrRef dataRef;
   
    protected OrCalcRef activityRef;
    protected OrCalcRef visibleRef;
    protected OrCalcRef calcRef;
    protected OrCalcRef constraintsRef;
    protected OrCalcRef constraintsValueRef;
    protected OrCalcRef backColorRef;
    protected OrCalcRef fontColorRef;
    protected OrCalcRef fontRef;
    protected OrCalcRef attentionRef;
    
    protected String radioGroup;
    protected int reqGroup = 0;
    protected boolean isEnabled = true;
    protected boolean isVisible = true;
    private OrGuiComponent comp;
    protected long langId;
    private int enterDB;
    private String reqMsg = "";
    private String cexpr;
    private ASTStart ctemplate;
    private boolean checkConstr = true;
    private boolean checkConstrValue = true;
    private String constrMsg;
    protected boolean editable = true;
    private JButton defaultBtn = new JButton();

    protected Color REQ_ERROR_COLOR = MainFrame.COLOR_FIELD_NO_FLC;
    protected Color EXPR_ERROR_COLOR = new Color(202, 247, 187);

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

        Kernel krn = Kernel.instance();

        PropertyNode pnode = c.getProperties().getChild("view");
        if (pnode != null) {
        	PropertyNode pnode_ = pnode.getChild("langExpr");
            PropertyValue pv = c.getPropertyValue(pnode_);
            if (!pv.isNull() && !pv.objectValue().equals(null)) {
            	try {
					langId=getLangFromExpr((String) pv.objectValue());
				} catch (Exception e) {
                    showErrorNessage(e.getMessage());
					e.printStackTrace();
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
        if (pnode != null && pnode.getChild("view") != null &&
                pnode.getChild("view").getChild("background") != null) {
            PropertyNode pnColor = pnode.getChild(
                    "view").getChild("background").getChild("backgroundColor");
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
                if (!pv.isNull()) {
                    FilterRecord[] filters = pv.filterValues();
                    paramFiltersUIDs = new String[filters.length];
                    for (int i = 0; i < filters.length; i++) {
                        FilterRecord filter = filters[i];
                        paramFiltersUIDs[i] = filter.getKrnObject().uid;
                    }
                }
                pv = c.getPropertyValue(pn1.getChild("paramName"));
                paramName = (!pv.isNull()) ? pv.stringValue() : "";
                
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

        // Настройка поведения компонента
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
                    checkDisabled = pv.isNull() ? false : pv.booleanValue();
                }
            }

            // Триггеры
            // До модификации
            PropertyNode beforeModNode = behavNode.getChild("beforeModAction");
            if (beforeModNode != null) {
                PropertyValue pv = c.getPropertyValue(beforeModNode);
                String expr = pv.isNull() ? "" : pv.stringValue();
                if (expr.trim().length() == 0) expr = "";
                if (expr.length() > 0) {
                    beforeModAction = OrLang.createStaticTemplate(expr);
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
                String expr = pv.isNull() ? "" : pv.stringValue();
                if (expr.trim().length() == 0) expr = "";
                if (expr.length() > 0) {
                    afterModAction = OrLang.createStaticTemplate(expr);
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
                String expr = pv.isNull() ? "" : pv.stringValue();
                if (expr.trim().length() == 0) expr = "";
                if (expr.length() > 0) {
                    beforeDelAction = OrLang.createStaticTemplate(expr);
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
                String expr = pv.isNull() ? "" : pv.stringValue();
                if (expr.trim().length() == 0) expr = "";
                if (expr.length() > 0) {
                    afterDelAction = OrLang.createStaticTemplate(expr);
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

        if ((c instanceof OrTabbedPane) || (c instanceof OrSplitPane) ||
                (c instanceof OrScrollPane) || (c instanceof OrLayoutPane)) {
            setActivityRef(c);
            setVisibleRef(c);
        } else {
            createDataRef(c);
            setActivityRef(c);
            setVisibleRef(c);
            setАttentionRef(c);
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
        if (c instanceof OrLabel) {
            return;
        }
        PropertyNode prop = c.getProperties();
        PropertyNode rprop = prop.getChild("ref").getChild("data");
        PropertyValue pv = c.getPropertyValue(rprop);
        if (!pv.isNull() && pv.stringValue().length() > 0) {
            try {
                propertyName = "Свойство: Данные";
                if (this instanceof TableAdapter) {
                    boolean hasParentRef = false;
                    Map<String, OrRef> refs = frame.getRefs();
                    Iterator<String> it = refs.keySet().iterator();
                    while (it.hasNext()) {
                        String key = it.next();
                        if (key != null && pv.stringValue().startsWith(key.toString())) {
                            hasParentRef = true;
                            break;
                        }
                    }
                    if (!hasParentRef)
                        dataRef = OrRef.createContentRef(pv.stringValue(),
                                Constants.RM_ALWAYS, Mode.RUNTIME,
                                frame.getTransactionIsolation(), frame);
                }

                if (dataRef == null)
                    dataRef = OrRef.createRef(pv.stringValue(), false, Mode.RUNTIME,
                                    frame.getRefs(), frame.getTransactionIsolation(), frame);
                if (!isEditor) {
                    dataRef.addCheckContext(this);
                    dataRef.addOrRefListener(this);
                }
            } catch (Exception e) {
                showErrorNessage(e.getMessage() + pv.stringValue());
                e.printStackTrace();
            }
            if (langId > 0) {
                dataRef.addLanguage(langId);
            }
        }
    }

    protected void setRadioGroup(OrGuiComponent c) {
        PropertyNode prop = c.getProperties();
        PropertyNode rprop = prop.getChild("pov");
        if (rprop != null) {
            PropertyNode c_rprop = rprop.getChild("radioGroup");
            if (c_rprop != null) {
                PropertyValue pv = c.getPropertyValue(c_rprop);
                if (pv != null && !pv.isNull()) {
                    radioGroup = pv.stringValue();
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
                    if (!pv.isNull() && !"".equals(pv.stringValue())) {
                        try {
                            propertyName = "Свойство: Активность";
                            fx = pv.stringValue();
                            if (fx.trim().length() > 0) {
                                activityRef = new OrCalcRef(fx, false, Mode.RUNTIME, frame.getRefs(),
                                        frame.getTransactionIsolation(), frame, c, propertyName, this);
                                activityRef.addOrRefListener(this);
                            }
                        } catch (Exception e) {
                            showErrorNessage(e.getMessage() + fx);
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    protected void setАttentionRef(OrGuiComponent c) {
        PropertyNode prop = c.getProperties();
        PropertyNode rprop = prop.getChild("pov");
        if (rprop != null) {
            PropertyNode pn = rprop.getChild("activity");
            if (pn != null) {
                PropertyNode  pn1 = pn.getChild("attention");
                if (pn1 != null) {
                    PropertyValue pv = c.getPropertyValue(pn1);
                    String fx = "";
                    if (!pv.isNull()) {
                        try {
                            propertyName = "Свойство: Внимание";
                            fx = pv.stringValue();
                            if (fx.trim().length() > 0) {
                                attentionRef = new OrCalcRef(fx, false, Mode.RUNTIME, frame.getRefs(), frame.getTransactionIsolation(), frame, c, propertyName, this);
                                attentionRef.addOrRefListener(this);
                            }
                        } catch (Exception e) {
                            showErrorNessage(e.getMessage() + fx);
                            e.printStackTrace();
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
                if (!pv.isNull() && !"".equals(pv.stringValue())) {
                    try {
                        propertyName = "Свойство: Видимость";
                        fx = pv.stringValue();
                        if (fx.trim().length() > 0) {
                            visibleRef = new OrCalcRef(fx, false, Mode.RUNTIME, frame.getRefs(),
                                    frame.getTransactionIsolation(), frame, c, propertyName, this);
                            visibleRef.addOrRefListener(this);
                        }
                    } catch (Exception e) {
                        showErrorNessage(e.getMessage() + fx);
                        e.printStackTrace();
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
            if (!pv.isNull() && !"".equals(pv.stringValue()))  {
                try {
                    propertyName = "Свойство: Обязательность-Формула";
                    fx = pv.stringValue();
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
                    e.printStackTrace();
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
            if (!pv.isNull() && !"".equals(pv.stringValue()))  {
                try {
                    propertyName = "Свойство: Обязательность-Формула";
                    fx = pv.stringValue();
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
                    e.printStackTrace();
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
                            String fx = pv.stringValue();
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
                                            ((Component) c).setBackground(new Color(backColor));
                                        } else if (val instanceof String) {
                                            ((Component) c).setBackground(Utils.getColorByName(val.toString()));
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            showErrorNessage(e.getMessage());
                            e.printStackTrace();
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
                            String fx = pv.stringValue();
                            if (fx.trim().length() > 0) {
                                boolean isColumn = c instanceof OrTableColumn;
                                fontColorRef = new OrCalcRef(fx, isColumn, Mode.RUNTIME, frame.getRefs(),
                                        frame.getTransactionIsolation(), frame, c, propertyName, this);
                                fontColorRef.addOrRefListener(this);
                                int fontColor = -1;
                                if (!fontColorRef.hasParents()) {
                                    fontColorRef.refresh(null);
                                    if (fontColorRef.getValue(0) != null) {
                                        Object val = fontColorRef.getValue(0);
                                        if (val instanceof Number) {
                                            fontColor = ((Number)val).intValue();
                                            ((Component)c).setForeground(new Color(fontColor));
                                        } else if (val instanceof String) {
                                            ((Component)c).setForeground(
                                                    Utils.getColorByName(val.toString()));
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            showErrorNessage(e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }


    protected void createCompFontRef(OrGuiComponent c) {
        PropertyNode pn = c.getProperties().getChild("view");
        if (pn == null)
        	return;
        pn = pn.getChild("font");
        if (pn == null)
        	return;
        pn = pn.getChild("fontExpr");
        if (pn == null)
        	return;
        PropertyValue pv = c.getPropertyValue(pn);
        if (!pv.isNull()) {
            try {
                propertyName = "Свойство: Шрифт (формула)";
                String fx = pv.stringValue().trim();
                if (fx.length() > 0) {
                    boolean isColumn = c instanceof OrTableColumn;
                    fontRef = new OrCalcRef(fx, isColumn, Mode.RUNTIME, frame.getRefs(),
                            frame.getTransactionIsolation(), frame, c, propertyName, this);
                    fontRef.addOrRefListener(this);
                }
            } catch (Exception e) {
                showErrorNessage(e.getMessage());
                e.printStackTrace();
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
                String expr = pv.stringValue();
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
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public OrRef getDataRef() {
        return dataRef;
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
        } else if (ref == backColorRef && !(comp instanceof OrTableColumn)) {
            Object backColor = null;
            if ( backColorRef.getValue(langId) != null) {
                backColor = backColorRef.getValue(langId);
                if (backColor instanceof Number) {
                    bgColor = new Color(((Number)backColor).intValue());
                } else if (backColor instanceof String) {
                    bgColor = Utils.getColorByName(backColor.toString());
                }
                ((Component)comp).setBackground(bgColor);
            }
        } else if (ref == fontColorRef && !(comp instanceof OrTableColumn)) {
            Object fColor = null;
            if (fontColorRef.getValue(langId) != null) {
                fColor = fontColorRef.getValue(langId);
                if (fColor instanceof Number) {
                    fontColor = new Color(((Number)fColor).intValue());
                } else if (fColor instanceof String) {
                    fontColor = Utils.getColorByName(fColor.toString());
                }
                ((Component)comp).setForeground(fontColor);
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
		        			|| comp instanceof OrComboBox
		        			|| comp instanceof OrDateField
		                    || comp instanceof OrIntField
		        			|| comp instanceof OrMemoField
		        			|| comp instanceof OrRichTextEditor
		        			|| comp instanceof OrMemoColumn
		        			|| comp instanceof OrHyperPopup
		        			|| comp instanceof OrBarcode) {
		        		// Пока реализовано только для след компонентов
		                update(value);
	        		}
	        	} catch (Exception ex) {
                    //@todo Непонятная ошибка при первом открытии комбоколонки
                    System.out.println(ex.toString());
                } finally {
	        		selfChange = false;
	        	}
        	}
        } else if (ref == visibleRef) {
    		if (visibleRef.getValue(langId) != null) {
                setVisible(((Number) visibleRef.getValue(langId)).intValue() == 1);
            }
        } else if (ref == attentionRef) {
            comp.setAttention(((Number) attentionRef.getValue(langId)).intValue() == 1);
        } 
    }

    public void update(Object value) {
        updateParamFilters(value);
        if (comp instanceof OrTextComponent) {
            ((OrTextComponent) comp).setValue(value);
        } else if (comp instanceof OrComboBox) {
            ((OrComboBox) comp).setValue(value);
        } else if (comp instanceof OrDateField) {
            ((OrDateField) comp).setValue(value);
        } else if (comp.getClass() == OrMemoField.class) {
            ((OrMemoField) comp).setValue(value);
        } else if (comp.getClass() == OrRichTextEditor.class) {
            ((OrRichTextEditor) comp).setValue(value);
        } else if (comp instanceof OrMemoColumn) {
            ((OrMemoField)((OrMemoColumn) comp).getEditor()).setValue(value);
        } else if (comp instanceof OrHyperPopup) {
            ((OrHyperPopup) comp).setValue(value);
        } else if (comp instanceof OrBarcode) {
        	((OrBarcode) comp).setValue(value);
        }
    }

    public boolean checkEnabled() {
        boolean isEnabled = true;
        if (dataRef != null && parentAdapter != null) 
        	isEnabled = parentAdapter.isActivityEnabled();
        //boolean isEnabled = (parentAdapter != null) ? parentAdapter.isSelfEnabled() : true;
        if (isEnabled && dataRef != null) {
            final InterfaceManager im =
                    InterfaceManagerFactory.instance().getManager();
            if (im != null) {
                int mode = im.getEvaluationMode();
                isEnabled = (mode != InterfaceManager.READONLY_MODE);
                if (isEnabled) {
                    isEnabled = (mode != InterfaceManager.SPR_RO_MODE);
                }
            }
        }
        if (this instanceof HyperPopupAdapter || this instanceof PopupColumnAdapter || this instanceof DocFieldAdapter) {
            isEnabled = editable;
        } else if (this instanceof ButtonAdapter) {
        	isEnabled = editable && (!((ButtonAdapter)this).hasSetAttr() || isEnabled);
        } else if (isEnabled) {
            isEnabled = editable;
        }
        if (comp instanceof OrPanel) {
            isEnabled = ((OrPanel)comp).isPanelEnabled();
        }
        if (comp instanceof OrHyperLabel) {
            isEnabled = ((OrHyperLabel)comp).isArchiv();
        }
        if (activityRef != null) {
            if (isEnabled || !inherit) {
                if (activityRef.getValue(langId) != null) {
                    isEnabled = (((Number) activityRef.getValue(langId)).intValue() == 1);
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
        if (comp instanceof JComponent && !(comp instanceof OrPanel) && !(comp instanceof OrCheckBox)
                && !(comp instanceof OrDocField)) {
            if ((!(comp instanceof OrTree) || !(comp instanceof OrTreeCtrl)) && (!((JComponent) comp).isEnabled() || !isEnabled)) {
                if (dataRef != null) {
                    dataRef.statesClear();
                }
            } else if ((comp instanceof OrTree || comp instanceof OrTreeCtrl) && ((OrTree) comp).getTreeFieldButton() != null) {
                JButton btn = ((OrTree) comp).getTreeFieldButton();
                if (!btn.isEnabled()) {
                    dataRef.statesClear();
                }
            }
            if (dataRef == null) {
                return;
            }
            Integer state = getState(new Integer(0));
            if (state == Constants.REQ_ERROR) {
                if ((comp instanceof OrTree || comp instanceof OrTreeCtrl) && ((OrTree) comp).getTreeFieldButton() != null) {
                    ((OrButtonUI) (((OrTree) comp).getTreeFieldButton()).getUI()).setStaticBottomBg(REQ_ERROR_COLOR);
                } else if (comp instanceof JButton) {
                    ((OrButtonUI) defaultBtn.getUI()).setStaticBottomBg(REQ_ERROR_COLOR);
                } else {
                    ((JComponent) comp).setBackground(REQ_ERROR_COLOR);
                }
            } else if (state == Constants.EXPR_ERROR) {
                ((JComponent) comp).setBackground(EXPR_ERROR_COLOR);
            } else {
                Color bgColor = getBgColor(0);
                Color fontColor = getFontColor(0);
                if ((comp instanceof OrTree || comp instanceof OrTreeCtrl) && ((OrTree) comp).getTreeFieldButton() != null) {
                    JButton btn = ((OrTree) comp).getTreeFieldButton();
                    Color back = ((OrButtonUI) defaultBtn.getUI()).getStaticBottomBg();
                    ((OrButtonUI) btn.getUI()).setStaticBottomBg(back);
                    if (bgColor != null) {
                        ((OrButtonUI) btn.getUI()).setStaticBottomBg(bgColor);
                    }
                } else {
                    if (comp instanceof JButton) {
                        Color back = ((OrButtonUI) defaultBtn.getUI()).getStaticBottomBg();
                        ((OrButtonUI) ((JButton) comp).getUI()).setStaticBottomBg(back);
                    } else {
                        if (bgColor != null) {
                            ((JComponent) comp).setBackground(bgColor);
                        }
                        if (fontColor != null) {
                            ((JComponent) comp).setForeground(fontColor);
                        }
                    }
                }
            }
        }
    }

    public void setFocus(int index, OrRefEvent e) {
        if (comp instanceof JComponent) {
            List l = kz.tamur.comps.Utils.parentComponentSetting((JComponent)comp, new ArrayList<Component>());
            for (int i = l.size() - 1; i >= 0; i--) {
                Object o =  l.get(i);
                if (o instanceof JComponent) {
                     ((JComponent)o).requestFocus();
                }
                if (o instanceof OrTabbedPane && i > 0) {
                    JComponent c = (JComponent)l.get(i - 1);
                    ((OrTabbedPane)o).setSelectedComponent(c);
                }
            }
            Container cont = ((JComponent)comp).getTopLevelAncestor();
            if (cont != null) {
            	((JComponent)comp).getTopLevelAncestor().validate();
            }
            ((JComponent) comp).requestFocus();
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
                    cexpr = pv.stringValue();
                    ctemplate = OrLang.createStaticTemplate(cexpr);
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

    	if (comp instanceof Component && !isEditor) {
	        Container cnt = ((Component)comp).getParent();
	        if (cnt instanceof OrTabbedPane) {
	            ((OrTabbedPane)cnt).setTabVisible((Component)comp, isVisible);
	        } else if (cnt == null) {
	            OrGuiComponent parentComp = parentAdapter.getComponent();
	            if (parentComp instanceof OrTabbedPane) {
	                ((OrTabbedPane)parentComp).setTabVisible((Component)comp, isVisible);
	            }
	        } else {
	            ((Component)comp).setVisible(isVisible);
	        }
	        if (cnt != null) {
	            cnt.validate();
	        }
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

    public void setParentAdapter(ContainerAdapter parentAdapter) {
        this.parentAdapter = parentAdapter;
    }

    protected void showErrorNessage(String message) {
        PropertyNode node = comp.getProperties().getChild("title");
        String componentName = "Компонент: ";
        if (node != null) {
            PropertyValue pv = comp.getPropertyValue(node);
            if (!pv.isNull()) {
                componentName = componentName + pv.stringValue();
            }
        }
        componentName = componentName + " [" + comp.getClass().getName().substring(
                Constants.COMPS_PACKAGE.length()) + "]";
        String mainMessage = message;
        Container container = (Frame)InterfaceManagerFactory.instance().getManager();
        MessagesFactory.showMessageDialogBig((Frame)container, MessagesFactory.ERROR_MESSAGE,
                    mainMessage + "\n" + componentName + "\n\n" + propertyName);
    }

    public Color getBgColor(int index) {
        if (backColorRef != null) {
            OrRef.Item item = backColorRef.getItem(0, index);
            Object o = (item != null) ? item.getCurrent() : null;
            if (o instanceof Number) {
                return new Color(((Number)o).intValue());
            } else if (o instanceof String) {
                return Utils.getColorByName(o.toString());
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
                col = Utils.getColorByName(o.toString());
            }
            if (comp instanceof OrHyperPopup) {
                ((OrHyperPopup)comp).setCalculatedColorFont(col);
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
            if (!Funcs.equals(value, this.value)) {
                boolean calcOwner = OrCalcRef.setCalculations();
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
                if (calcOwner) {
                    OrCalcRef.makeCalculations();
                }
                // Триггер "После модификации"
                doAfterModification();
            }
            // Обновить текущее значение
            this.value = value;
            if (comp instanceof OrTextField) {
                ((OrTextField) comp).setValue(value);
            }
        }
        return value;
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
            vc.put("OBJS", Funcs.makeList(((UIFrame)frame).getInitialObjs()));
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
            orlang.evaluate(afterModAction, vc, this, new Stack<String>());
			if (calcOwner)
				OrCalcRef.makeCalculations();
        }
    }
    
    protected Object doBeforeModification(Object value) throws Exception {
        if (beforeModAction != null) {
            ClientOrLang orlang = new ClientOrLang(frame);
            Map<String, Object> vc = new HashMap<String, Object>();
            vc.put("VALUE", value);
            vc.put("OBJS", Funcs.makeList(((UIFrame)frame).getInitialObjs()));
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
            orlang.evaluate(beforeModAction, vc, this, new Stack<String>());
			if (calcOwner)
				OrCalcRef.makeCalculations();
            return vc.get("RETURN");
        }
        return value;
    }
    
    protected Object doBeforeDelete(List values) throws Exception {
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
            orlang.evaluate(beforeDelAction, vc, this, new Stack<String>());
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
            orlang.evaluate(beforeModAction, vc, this, new Stack<String>());
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
    
    public boolean isOnlyChildren() {
        if (parentAdapter != null) {
            if (parentAdapter.getChildrenAdapters().size() > 1) {
                return false;
            }
            return parentAdapter.isOnlyChildren();
        }
        return true;
    }

    public void filterParamChanged(String fuid, String pid, List<?> values) {
        value = values != null && values.size() > 0 ? values.get(0) : null;
        if (comp instanceof OrTextComponent)
            ((OrTextComponent) comp).setValue(value);
        else if (comp instanceof OrComboBox)
            ((OrComboBox) comp).setValue(value);
    }

    public void clearParam() {
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getUUID() {
        return comp.getUUID();
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
