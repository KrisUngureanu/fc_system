package com.cifs.or2.client.gui;

public class OrEnum extends Object {

    private String stringVal_;
    private int intVal_;

    public OrEnum(String stringVal, int intVal) {
        stringVal_ = stringVal;
        intVal_ = intVal;
    }

    public String toString() {
        return stringVal_;
    }

    public String getStringVal() {
        return stringVal_;
    }

    public int getIntVal() {
        return intVal_;
    }

    public boolean equals(Object obj) {
        if (obj instanceof OrEnum) {
            return intVal_ == ((OrEnum)obj).getIntVal();
        } else {
            return false;
        }
    }
}
