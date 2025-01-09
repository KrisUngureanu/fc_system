package kz.tamur.rt.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import kz.tamur.comps.Mode;
import kz.tamur.comps.OrFrame;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.lang.OrLang;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.or3.client.comps.interfaces.OrDateComponent;
import kz.tamur.rt.orlang.ClientOrLang;
import kz.tamur.web.common.webgui.WebComponent;
import kz.tamur.web.component.OrWebDateField;
import kz.tamur.web.component.WebFrame;

import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.util.expr.Editor;

public class DateFieldAdapter extends ComponentAdapter {

	private OrDateComponent dateField;
	private int langId;
	private OrRef copyRef, attentionRef;
	private ASTStart afterModAction;
	private boolean isModified = false;

	public DateFieldAdapter(OrFrame frame, OrDateComponent dateField, boolean isEditor) throws KrnException {
         super(frame, dateField, isEditor);
         PropertyNode proot = dateField.getProperties();
         this.dateField = dateField;
         // Копируемый атрибут
         String copyRefPath = dateField.getCopyRefPath();
         if (copyRefPath != null && !"".equals(copyRefPath)) {
             try {
                 propertyName = "Свойство: Копируемый атрибут";
                 copyRef = OrRef.createRef(copyRefPath, false, Mode.RUNTIME, frame.getRefs(), OrRef.TR_CLEAR, frame);
             } catch (Exception e) {
                 if (e instanceof RuntimeException) {
                     showErrorNessage(e.getMessage());
                 }
                 e.printStackTrace();
             }
         }
         PropertyValue pv = dateField.getPropertyValue(proot.getChild("pov").getChild("afterModAction"));
         String afterExpr = null;
         if (!pv.isNull()) {
             afterExpr = pv.stringValue(frame.getKernel());
         }
         if (afterExpr != null && afterExpr.length() > 0) {
			long ifcId = ((WebFrame)frame).getObj().id;
			String key = ((WebComponent)dateField).getId() + "_" + OrLang.AFTER_MODIF_TYPE;
			afterModAction = ClientOrLang.getStaticTemplate(ifcId, key, afterExpr, getLog());
             try {
                 Editor e = new Editor(afterExpr);
                 ArrayList<String> paths = e.getRefPaths();
                 for (int j = 0; j < paths.size(); ++j) {
                     String path = paths.get(j);
                     OrRef.createRef(path, false, Mode.RUNTIME, frame.getRefs(), OrRef.TR_CLEAR, frame);
                 }
             } catch (Exception ex) {
                 ex.printStackTrace();
             }
         }
         setАttentionRef(dateField);
         this.dateField.setXml(null);
     }

     public void clear() {}

     private void updateValue(Object value, boolean overwrite) {
    	 if (!selfChange) {
	         OrRef ref = dataRef;
	         if (ref != null) {
	             if (ref.getValue(langId) == null || overwrite) {
	                 OrRef.Item item = ref.getItem(langId);
	                 try {
	                	 selfChange = true;
	                	 if (item != null && value != null) {
	                		 ref.changeItem(value, this, this);
	                	 } else if (item != null && value == null) {
	                		 ref.deleteItem(this, this);
	                	 } else {
	                		 ref.insertItem(0, value, this, this, false);
	                	 }
	                 } catch (KrnException e) {
	                     e.printStackTrace();
	                 } finally {
	                	 selfChange = false;
	                 }
	                 dateField.setValue(value);
	             }
	         } else {
	             dateField.setValue(value);
	         }
    	 }
         updateParamFilters(value);
     }

     public void valueChanged() {
         try {
             updateValue(dateField.getValue(), true);
         } catch (Exception ex) {
             ex.printStackTrace();
         }
     }
     
     public void valueChanged(OrRefEvent e) {
      	OrRef ref = e.getRef();
         if (ref == attentionRef) {
 			((OrWebDateField) dateField).sendChangeProperty("dateFieldAttention", attentionRef.getValue(langId).toString());
         }
     	super.valueChanged(e);
     }    

     public void update() {
         if (isModified) {
             isModified = false;
             if (afterModAction != null) {
                 ClientOrLang orlang = new ClientOrLang(DateFieldAdapter.this.frame);
                 Map<String, Object> vc = new HashMap<String, Object>();
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
                     orlang.evaluate(afterModAction, vc, DateFieldAdapter.this, new Stack<String>());
                 } catch(Exception ex) {
                     Util.showErrorMessage(DateFieldAdapter.this.dateField, ex.getMessage(), "Действие после модификации");
                 	log.error("Ошибка при выполнении формулы 'Действие после модификации' компонента '" + (comp != null ? comp.getClass().getName() : "") + "', uuid: " + getUUID());
                    log.error(ex, ex);
                 } finally {
     				if (calcOwner)
     					OrCalcRef.makeCalculations();
                 }
             }
         }
     }

     public void setEnabled(boolean isEnabled) {
         super.setEnabled(isEnabled);
         dateField.setEnabled(isEnabled);
     }

	public void setАttentionRef(OrGuiComponent c) {
		String attentionExpr = ((OrWebDateField) c).getaAttentionExpr();
		if (attentionExpr != null && attentionExpr.length() > 0) {
			try {
				propertyName = "Свойство: Поведение.Активность.Внимание";
				attentionRef = new OrCalcRef(attentionExpr, false, Mode.RUNTIME, frame.getRefs(), frame.getTransactionIsolation(), frame, c, propertyName, this);
				attentionRef.addOrRefListener(this);
			} catch (Exception e) {
				showErrorNessage(e.getMessage() + attentionExpr);
				e.printStackTrace();
			}
		}
	}
     
     public void clearFilterParam() {
         super.clearFilterParam();
         if (dataRef == null) {
             dateField.setValue(null);
         }
     }

     public OrRef getCopyRef() {
         return copyRef;
     }

     public void setCopyRef(OrRef copyRef) {
         this.copyRef = copyRef;
     }

     public void setModified(boolean modified) {
         isModified = modified;
     }
 }