package kz.tamur.rt.adapters;


public class OrRefEvent {
    public static final int ITERATING = 2;
    public static final int CHECKING  = 4;
    public static final int INSERTED  = 8;
    public static final int DELETED   = 16;
    public static final int UPDATED   = 32;
    public static final int CHANGED   = 64;
    public static final int ROOT_ITEM_CHANGED   = 128;

    private OrRef ref_;
    private Object originator_;
    private int reason_;
    private int index_;

    private int startIndex;
    private int endIndex;

    public OrRefEvent(OrRef ref, int reason, int index, Object originator) {
        ref_ = ref;
        reason_ = reason;
        originator_ = originator;
        index_ = index;
    }

    public OrRefEvent(OrRef ref, int reason, int startIndex, int endIndex, Object originator) {
        ref_ = ref;
        reason_ = reason;
        originator_ = originator;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public OrRef getRef() {
        return ref_;
    }

    public Object getOriginator() {
        return originator_;
    }

    public int getReason() {
        return reason_;
    }

    public int getIndex() {
        return index_;
    }
    
    public int getStartIndex() {
    	return startIndex;
    }
    
    public int getEndIndex() {
    	return endIndex;
    }
}