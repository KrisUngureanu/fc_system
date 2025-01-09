package kz.tamur.comps.models;

public class EnumValue {
    public final int code;
    public final String name;

    public EnumValue(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public String toString() {
        return name;
    }

    public boolean equals(Object obj) {
        if (obj instanceof EnumValue) {
            return code == ((EnumValue)obj).code;
        }
        return false;
    }
}
