package kz.tamur.rt.adapters;

import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.util.expr.Editor;
import kz.tamur.comps.*;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.lang.OrLang;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.rt.orlang.ClientOrLang;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class CheckBoxAdapter extends ComponentAdapter implements ActionListener {

    private OrCheckBox checkBox;
    private boolean selfChange = false;
    protected OrCellEditor editor_;
    private ASTStart afterModAction;

    public CheckBoxAdapter(UIFrame frame, OrCheckBox checkBox, boolean isEditor) throws KrnException {
        super(frame, checkBox, isEditor);
        this.checkBox = checkBox;
        this.checkBox.addActionListener(this);
        kz.tamur.rt.Utils.setComponentFocusCircle(this.checkBox);
        PropertyNode proot = checkBox.getProperties();
        PropertyValue pv = checkBox.getPropertyValue(proot.getChild("pov").getChild("afterModAction"));
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
                    OrRef.createRef(path, false, Mode.RUNTIME, frame.getRefs(), OrRef.TR_CLEAR, frame);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        this.checkBox.setXml(null);
    }

    // RefListener
    public void valueChanged(OrRefEvent e) {
        super.valueChanged(e);
        if (e.getOriginator() != this) {
            OrRef ref = e.getRef();
            if (ref == dataRef) {
                selfChange = true;
                Object item = ref.getValue(langId);
                Number value = (item != null) ? (Number) item : new Long(0);
                updateParamFilters(value);
                checkBox.setSelectedSuper(value != null && value.longValue() == 1);
                selfChange = false;
            }
        }
    }

    public void clear() {
    }
    
    class OrCheckCellEditor extends OrCellEditor {

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            valueChanged(new OrRefEvent(dataRef, 0, -1, null));
            checkBox.setHorizontalAlignment(SwingConstants.CENTER);
            checkBox.setForeground(Color.WHITE);
            checkBox.setText("");
            return checkBox;
        }

        public Object getValueFor(Object obj) {
            OrRef.Item item = (OrRef.Item) obj;
            if (item != null) {
                Number val = (Number) item.getCurrent();
                return new Boolean(val != null && val.longValue() == 1);
            }
            return null;
        }

        public Object getCellEditorValue() {
            return checkBox.isSelected() ? new Long(1) : new Long(0);
        }
    }

    public OrCellEditor getCellEditor() {
        if (editor_ == null) {
            editor_ = new OrCheckCellEditor();
            checkBox.addActionListener(editor_);
        }
        return editor_;
    }

    public void setEnabled(boolean isEnabled) {
        super.setEnabled(isEnabled);
        checkBox.setEnabled(isEnabled);
    }

    public void clearFilterParam() {
        super.clearFilterParam();
        if (dataRef == null) {
            checkBox.setSelectedSuper(false);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (!selfChange) {
                boolean calcOwner = OrCalcRef.setCalculations();
                Number value = (checkBox.isSelected()) ? new Long(1) : new Long(0);
                if (dataRef != null) {
                    OrRef.Item item = dataRef.getItem(langId);
                    if (item != null)
                        dataRef.changeItem(value, this, this);
                    else
                        dataRef.insertItem(0, value, this, this, false);
                }
                updateParamFilters(value.longValue() == 0 ? null : value);
                if (calcOwner)
                    OrCalcRef.makeCalculations();

                if (afterModAction != null) {
                    ClientOrLang orlang = new ClientOrLang(CheckBoxAdapter.this.frame);
                    Map<String, Object> vc = new HashMap();
                    vc.put("THIS", checkBox);

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
                    try {
                        calcOwner = OrCalcRef.setCalculations();
                        orlang.evaluate(afterModAction, vc, CheckBoxAdapter.this, new Stack<String>());
                        if (calcOwner)
                            OrCalcRef.makeCalculations();
                    } catch (Exception ex) {
                        Util.showErrorMessage(CheckBoxAdapter.this.checkBox, ex.getMessage(), "Действие после модификации");
                    }
                }
            }
        } catch (KrnException ex) {
            ex.printStackTrace();

        }
    }
}
