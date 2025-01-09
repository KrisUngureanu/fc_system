package kz.tamur.util;

import java.util.Comparator;

import kz.tamur.comps.Constants;
import kz.tamur.rt.adapters.OrRef;

public class ReqMsgsList {

    public static int SORT_UP = 0;
    public static int SORT_DOWN = 1;

    private boolean hasFatalErrors = false;
    MsgListItem[] items = null;
    
    public ReqMsgsList() {
    }

    public int getListSize() {
        return items != null ? items.length : 0;
    }

    public boolean hasFatalErrors() {
        return hasFatalErrors;
    }

    public void addToList(MsgListItem[] items) {
        for (int i = 0; i < items.length; i++) {
            if (items[i].enterBDType_ == Constants.BINDING ||
                    items[i].enterBDType_ == Constants.CONSTRAINT) {
                hasFatalErrors = true;
                break;
            }
        }
        this.items = items;
    }

	public MsgListItem getElementAt(int i) {
		return items[i];
	}

    public static class MsgListItem {

        private String mes_;
        private OrRef ref;
        private int enterBDType_;
        private int index;
        private Pair[] loc;
        private long langId = 0;
		private String uuid = null;
        
        public MsgListItem(OrRef ref_, int index_, String mes, int enterBDType, Pair[] loc_, long langId, String uuid) {
            super();
            ref = ref_;
            mes_ = mes;
            enterBDType_ = enterBDType;
            index = index_;
            loc=loc_;
            this.langId = langId;
            this.uuid  = uuid;
        }
        
        public String getUUID() { //TODO: uuid
            return uuid;
        }

        public int getType() {
            return (enterBDType_ != -1) ? enterBDType_ : 0;
        }

        public String toString() {
            return (mes_ != null) ? mes_ : "";
        }

        public int getIndex() {
            return index;
        }

        public Pair[] getLoc(){
            return loc;
        }

        public OrRef getRef() {
            return ref;
        }

        public long getLangId() {
            return langId;
        }
    }

    public static class SortOnTitles implements Comparator<MsgListItem> {
        private int sortDirect_;

        public SortOnTitles(int sortDirect) {
            sortDirect_ = sortDirect;
        }

        public int compare(MsgListItem o1, MsgListItem o2) {
            if (sortDirect_ == SORT_UP) {
                return o1.toString().compareTo(o2.toString());
            } else {
                return o2.toString().compareTo(o1.toString());
            }
        }
    }

    public static class SortOnMessType implements Comparator<MsgListItem> {

        private int sortDirect_;

        public SortOnMessType(int sortDirect) {
            sortDirect_ = sortDirect;
        }

        public int compare(MsgListItem o1, MsgListItem o2) {
            Integer i1 = new Integer(((MsgListItem) o1).getType());
            Integer i2 = new Integer(((MsgListItem) o2).getType());
            if (sortDirect_ == SORT_DOWN) {
                return i1.compareTo(i2);
            } else {
                return i2.compareTo(i1);
            }
        }
    }
}
