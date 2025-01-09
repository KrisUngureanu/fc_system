package com.cifs.or2.server;

import com.cifs.or2.kernel.*;

import java.util.ArrayList;

public class Sequence {
    private KrnAttribute lastAttr_;
    private KrnAttribute skippedAttr_;
    private KrnAttribute usedAttr_;
    private KrnAttribute valueAttr;
    private KrnAttribute strValueAttr;
    private KrnAttribute trIdAttr_;

    private KrnObject obj_;
    private long lastValue_;
    private ArrayList skippedValues_ = new ArrayList();
    private ArrayList usedValues_ = new ArrayList();
    private ArrayList values = new ArrayList();
    private ArrayList usedStrValues = new ArrayList();
    private ArrayList trIds_ = new ArrayList();

    public Sequence(KrnObject obj, Session s) throws KrnException {
        KrnClass cls = s.getClassById(obj.classId);

        obj_ = obj;
        lastAttr_ = s.getAttributeByName(cls, "last");
        skippedAttr_ = s.getAttributeByName(cls, "skipped");
        usedAttr_ = s.getAttributeByName(cls, "used");
        //usedAttr_ = s.getAttributeByName(cls, "used");
        valueAttr = s.getAttributeByName(cls, "values");
        strValueAttr = s.getAttributeByName(cls, "usedValues");
        trIdAttr_ = s.getAttributeByName(cls, "transactionId");

        long[] lastVals = s.getLongs(obj.id, lastAttr_.id, 0);
        lastValue_ = (lastVals.length > 0) ? lastVals[0] : 0;

        long[] skipVals = s.getLongs(obj.id, skippedAttr_.id, 0);
        for (int i = 0; i < skipVals.length; ++i)
            skippedValues_.add(new Long(skipVals[i]));

        long[] usedVals = s.getLongs(obj.id, usedAttr_.id, 0);
        for (int i = 0; i < usedVals.length; ++i)
            usedValues_.add(new Long(usedVals[i]));

        String[] vals = s.getStrings(obj.id, valueAttr.id, 0, false, 0);
        for (int i = 0; i < vals.length; ++i)
            values.add(vals[i]);

        String[] usedStrVals = s.getStrings(obj.id, strValueAttr.id, 0, false, 0);
        for (int i = 0; i < usedStrVals.length; ++i)
            usedStrValues.add(usedStrVals[i]);

        long[] trIds = s.getLongs(obj.id, trIdAttr_.id, 0);
        for (int i = 0; i < trIds.length; ++i)
            trIds_.add(new Long(trIds[i]));
    }

    public synchronized long getNextValue(int tr_id, Session s) throws KrnException {
        long val = lastValue_ + 1;
        s.setLong(obj_.id, lastAttr_.id, 0, val, 0);
        lastValue_ = val;
        s.commitTransaction();
        return val;
    }

    public synchronized void stringValue(String value, int tr_id, Session s)
            throws KrnException {
        if (value.length() > 0) {
            s.setString(obj_.id, valueAttr.id, values.size(), 0, false, value, 0);
            values.add(value);
            s.commitTransaction();
        }
    }

    public synchronized void skipValue(int value, String strVal, int tr_id, Session s) throws KrnException {
        if (value != 0 && strVal.length() > 0) {
            s.setLong(obj_.id, skippedAttr_.id, skippedValues_.size(), value, 0);
            skippedValues_.add(new Integer(value));
            s.setString(obj_.id, valueAttr.id, values.size(), 0, false, strVal, 0);
            values.add(strVal);
            s.commitTransaction();
        }
    }

    public synchronized void useValue(int value, String strVal, int tr_id, Session s) throws KrnException {
        if (value != 0 && value != -1 && strVal.length() > 0) {
            try {
                int index = -1;
                for (int i = 0; i < skippedValues_.size(); i++) {
                    int val = ((Integer) skippedValues_.get(i)).intValue();
                    if (val == value) {
                        index = i;
                        break;
                    }
                }
                s.setLong(obj_.id, usedAttr_.id, usedValues_.size(), value, 0);
                usedValues_.add(new Integer(value));
                s.setString(obj_.id, strValueAttr.id, usedStrValues.size(), 0, false, strVal, 0);
                usedStrValues.add(strVal);
                s.setLong(obj_.id, trIdAttr_.id, trIds_.size(), tr_id, 0);
                trIds_.add(new Integer(tr_id));
                if (index != -1) {
                    skippedValues_.remove(index);
                    s.deleteValue(obj_.id, skippedAttr_.id, new int[] {index}, 0, 0);
                    values.remove(index);
                    s.deleteValue(obj_.id, valueAttr.id, new int[] {index}, 0, 0);
                    s.commitTransaction();
                }
            } catch (KrnException e) {
                s.rollbackTransaction();
                e.printStackTrace();
                throw new KrnException(0, "Ошибка в использовании значения!");
            }
        }
    }

    public synchronized void unuseValue(String oldStrValue, int newValue,
                                        String newStrValue, int tr_id, Session s)
            throws KrnException {
        if (oldStrValue.length() > 0) {
            try {
                int index = -1;
                for (int i = 0; i < usedStrValues.size(); i++) {
                    String val = (String) usedStrValues.get(i);
                    if (oldStrValue.equals(val)) {
                        index = i;
                        break;
                    }
                }
                if (index != -1) {
                    s.deleteValue(obj_.id, usedAttr_.id, new int[] {index}, 0, 0);
                    int usedOldVal = ((Integer)usedValues_.remove(index)).intValue();
                    s.deleteValue(obj_.id, strValueAttr.id, new int[] {index}, 0, 0);
                    usedStrValues.remove(oldStrValue);
                    s.deleteValue(obj_.id, trIdAttr_.id, new int[] {index}, 0, 0);
                    trIds_.remove(index);
                    skipValue(usedOldVal, oldStrValue, tr_id, s);
                }
                if (newStrValue.length() > 0) {
                    useValue(newValue, newStrValue, tr_id, s);
                }
                s.commitTransaction();
            } catch (Exception e) {
                s.rollbackTransaction();
                e.printStackTrace();
            }
        }
    }

    public synchronized int[] getSkippedValues() {
        int[] res = new int[skippedValues_.size()];
        for (int i = 0; i < skippedValues_.size(); i++) {
            res[i] = ((Integer)skippedValues_.get(i)).intValue();
        }
        return res;
    }

    public synchronized void commitValues(int tr_id, Session s) throws KrnException {
    }

    public synchronized void rollbackValues(int tr_id, Session s) throws KrnException {
        for (int i = 0; i < trIds_.size(); i++) {
            if (((Integer) trIds_.get(i)).intValue() == tr_id) {
                int val = ((Integer) usedValues_.remove(i)).intValue();
                String strVal = ((String) usedStrValues.remove(i));
                s.deleteValue(obj_.id, usedAttr_.id, new int[]{i}, 0, 0);
                s.deleteValue(obj_.id, strValueAttr.id, new int[]{i}, 0, 0);
                trIds_.remove(i);
                s.deleteValue(obj_.id, trIdAttr_.id, new int[]{i}, 0, 0);

                skippedValues_.add(new Integer(val));
                s.setLong(obj_.id, skippedAttr_.id, skippedValues_.size() - 1, val, 0);
                s.setString(obj_.id, valueAttr.id, values.size(), 0, false, strVal, 0);
                values.add(strVal);
            }
        }
    }

    public synchronized long getLastValue() {
        return lastValue_;
    }

}
