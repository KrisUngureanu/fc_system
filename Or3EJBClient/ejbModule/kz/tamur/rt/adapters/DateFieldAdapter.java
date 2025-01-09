package kz.tamur.rt.adapters;

import java.util.ArrayList;

import kz.tamur.comps.*;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.lang.OrLang;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.util.expr.Editor;

public class DateFieldAdapter extends ComponentAdapter {

    private OrDateField dateField;
    //private boolean selfChange = false;
    private int langId;
    //private final String MASK = "дд.мм.гггг";
    private OrRef copyRef;
    //private Border border;

    public DateFieldAdapter(OrFrame frame, OrDateField dateField, boolean isEditor)
            throws KrnException {
        super(frame, dateField, isEditor);
        PropertyNode proot = dateField.getProperties();
/*
        PropertyValue pv =
                dateField.getPropertyValue(proot.getChild("language"));
        if (!pv.isNull()) {
            langId = Integer.parseInt(pv.getKrnObjectId());
            dataRef.addLanguage(langId);
        }
*/
        this.dateField = dateField;
        if (dataRef != null && !dataRef.isColumn())
            kz.tamur.rt.Utils.setComponentFocusCircle(this.dateField);
        //Копируемый атрибут
        String copyRefPath = dateField.getCopyRefPath();
        if (copyRefPath != null && !"".equals(copyRefPath)) {
            try {
                propertyName = "Свойство: Копируемый атрибут";
                copyRef = OrRef.createRef(copyRefPath, false, Mode.RUNTIME, frame.getRefs(),
                        OrRef.TR_CLEAR, frame);
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
        this.dateField.setXml(null);
    }

    public void clear() {
    }

    public void setEnabled(boolean isEnabled) {
        super.setEnabled(isEnabled);
        dateField.setEnabled(isEnabled);
/*
        if (dataRef != null) {
            dataRef.setActive(isEnabled);
        }
*/
    }

    public void clearFilterParam() {
        super.clearFilterParam();
        if (dataRef == null) {
            dateField.setText(null);
        }
    }

    public OrRef getCopyRef() {
    	return copyRef;
    }
}
