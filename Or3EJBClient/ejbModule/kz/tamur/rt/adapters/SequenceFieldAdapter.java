package kz.tamur.rt.adapters;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnException;
import kz.tamur.comps.OrCellEditor;
import kz.tamur.comps.OrSequenceField;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.lang.OrLang;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.rt.RadioGroupManager;
import kz.tamur.rt.orlang.ClientOrLang;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class SequenceFieldAdapter extends ComponentAdapter
        implements DocumentListener, ActionListener  {

    private OrSequenceField seqField;
    private boolean selfChange = false;
    private int langId;
    private RadioGroupManager groupManager = new RadioGroupManager();
    OrCellEditor editor_;
    private boolean isShowSkipped = true;

    ASTStart template;

    private int seqObjId;

    //Текущее числовое значение
    private long currentValue = -1;
    //Сохранённое текстовое значение
    private String usedStringVal = "";



    public SequenceFieldAdapter(UIFrame frame, OrSequenceField seqField, boolean isEditor)
            throws KrnException {
        super(frame, seqField, isEditor);
        this.seqField = seqField;
        PropertyNode proot = seqField.getProperties();
/*
        PropertyValue pv =
                seqField.getPropertyValue(proot.getChild("language"));
        if (!pv.isNull()) {
            langId = Integer.parseInt(pv.getKrnObjectId());
            dataRef.addLanguage(langId);
        }
*/
        PropertyValue pv = seqField.getPropertyValue(
                seqField.getProperties().getChild("pov").getChild(
                        "sequences").getChild("seqPrefix"));
        if (!pv.isNull()) {
            String expr = pv.stringValue();
            template = OrLang.createStaticTemplate(expr);
        }
        pv = seqField.getPropertyValue(
                seqField.getProperties().getChild("pov").getChild(
                        "sequences").getChild("showPrefix"));
        if (!pv.isNull()) {
            isShowSkipped = !pv.booleanValue();
        }
        this.seqField.addActionListener(this);
        this.seqField.addDocumentListener(this);
        pv = seqField.getPropertyValue(
                seqField.getProperties().getChild("pov").getChild(
                        "sequences").getChild("sequence"));
        if (!pv.isNull()) {
            seqObjId = Integer.valueOf((String)pv.objectValue()).intValue();
        }
        this.seqField.setXml(null);
    }

    // RefListener
    public void valueChanged(OrRefEvent e) {
        if (e.getOriginator() != this) {
            OrRef ref = e.getRef();
            if (ref == dataRef) {
                selfChange = true;
                Object value = ref.getValue(langId);
                seqField.setValue((value != null) ? "" + value : "");
                if (value != null) {
                    usedStringVal = "" + value;
                }
                if (!"".equals(seqField.getValue()) && !seqField.isStrikes()) {
                    seqField.nextBtn.setEnabled(false);
                } else if ("".equals(seqField.getValue())) {
                    seqField.nextBtn.setEnabled(true);
                }
                selfChange = false;
            } else if (ref == activityRef) {
/*
                OrCalcRef tmp = activityRef;
                boolean isActive = true;
                if (tmp.getValue(langId) != null) {
                    isActive = (((Number) tmp.getValue(langId)).intValue() == 1);
                } else {
                    isActive = false;
                }
*/
                seqField.setEnabled(checkEnabled());
            }
        }
        if (radioGroup != null) {
            groupManager.evaluate(frame, radioGroup);
        }
    }

    public void clear() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void changesCommitted(OrRefEvent e) {
        System.out.println("commit");
        try {
            Kernel krn = Kernel.instance();
            long tid = getRef().getCash().getTransactionId();
            if (usedStringVal.length() == 0 &&
                    seqField.getValue() != null && seqField.getValue().length() > 0) {
                krn.useValue(seqObjId, currentValue,  seqField.getValue(), tid);
                usedStringVal = seqField.getValue();
            } else {
                String val = generateNumber(currentValue);
                if (currentValue != -1 && seqField.getValue() != null &&
                        seqField.getValue().length() > 0) {
                    krn.unuseValue(seqObjId, usedStringVal, currentValue, val, tid);
                    usedStringVal = val;
                }
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public void changesRollbacked(OrRefEvent e) {
        System.out.println("rollback");
    }

    // DocumentListener
    public void insertUpdate(DocumentEvent e) {
        changedUpdate(e);
    }

    public void removeUpdate(DocumentEvent e) {
        changedUpdate(e);
    }

    public void changedUpdate(DocumentEvent e) {
        if (!selfChange) {
            try {
                OrRef.Item item = dataRef.getItem(langId);
                try {
                    selfChange = true;
                    if (item != null)
                        dataRef.changeItem(seqField.getValue(), this, this);
                    else
                        dataRef.insertItem(0, seqField.getValue(), this, this, false);
                } finally {
                    selfChange = false;
                }
            } catch (KrnException e1) {
                e1.printStackTrace();
            }
        }
    }

    public OrRef getRef() {
        return dataRef;
    }

    private String generateNumber(long value) {
        Map vc = new HashMap();
        vc.put("SEQNUM", new Long(value));
        ClientOrLang parser = new ClientOrLang(frame);
        try {
            parser.evaluate(template, vc, this, new Stack<String>());
        } catch(Exception e) {
            e.printStackTrace();
        }
        return (String)vc.get("RETURN");
    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (seqObjId != -1) {
            Kernel krn = Kernel.instance();
            long tid = getRef().getCash().getTransactionId();
            try {
                if (src == seqField.nextBtn) {
                    int val = krn.getNextValue(seqObjId, tid);
                    currentValue = val;
                    seqField.setValue(generateNumber(currentValue));
                    krn.skipValue(seqObjId, val, seqField.getValue(), tid);
                    if (!seqField.isStrikes()) {
                        seqField.nextBtn.setEnabled(false);
                    }
                } else if (src == seqField.skippedBtn) {
                    try {
                        JList skippedList = null;
                        long[] skippedValues = krn.getSkippedValues(seqObjId);
                        if (isShowSkipped) {
                            SkippedValueObject[] vals = new SkippedValueObject[skippedValues.length];
                            for (int i = 0; i < skippedValues.length; i++) {
                                long skippedValue = skippedValues[i];
                                vals[i] = new SkippedValueObject(skippedValue, generateNumber(skippedValue));
                            }
                            skippedList = new JList(vals);
                        } else {
                            Long[] vals = new Long[skippedValues.length];
                            for (int i = 0; i < skippedValues.length; i++) {
                                vals[i] = new Long(skippedValues[i]);
                            }
                            skippedList = new JList(vals);
                        }
                        skippedList.setFont(kz.tamur.rt.Utils.getDefaultFont());
                        JScrollPane sp = new JScrollPane(skippedList);
                        sp.setPreferredSize(new Dimension(400, 500));
                        sp.setVerticalScrollBarPolicy(
                                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                        sp.setHorizontalScrollBarPolicy(
                                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                        DesignerDialog dlg = null;
                        Container container = seqField.getTopLevelAncestor();
                        if (container instanceof Dialog) {
                            dlg = new DesignerDialog((Dialog)container, "Пропущенные номера", sp);
                        } else {
                            dlg = new DesignerDialog((Frame)container, "Пропущенные номера", sp);
                        }
                        dlg.show();
                        if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                            Object so = skippedList.getSelectedValue();
                            if (so != null) {
                                if (isShowSkipped) {
                                    SkippedValueObject o = (SkippedValueObject)so;
                                    currentValue = o.getValue();
                                    seqField.setValue(o.toString());
                                } else {
                                    Integer o = (Integer)so;
                                    currentValue = o.intValue();
                                    seqField.setValue(generateNumber(currentValue));
                                }
                            }
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }

                } else if (src == seqField.clearBtn) {
                    dataRef.deleteItem(this, this);
                    if (usedStringVal.length() > 0 &&
                            seqField.getValue().equals(usedStringVal)) {
                        krn.unuseValue(seqObjId, usedStringVal, 0, "", tid);
                    }
                    seqField.setValue("");
                }
            } catch (KrnException e1) {
                e1.printStackTrace();
            }
        }
    }

    public void setEnabled(boolean isEnabled) {

    }

    class SkippedValueObject {
        private long value;
        private String strValue;

        public SkippedValueObject(long value, String strValue) {
            this.value = value;
            this.strValue = strValue;
        }

        public String toString() {
            return strValue;
        }

        public long getValue() {
            return value;
        }
    }


}

