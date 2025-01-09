package kz.tamur.util;

import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Comparator;

import kz.tamur.comps.*;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.adapters.OrRef;
import com.cifs.or2.kernel.KrnException;

public class ReqMsgsList extends JPanel {

    public static int SORT_UP = 0;
    public static int SORT_DOWN = 1;

    private JList list_ = new JList();
    private boolean hasFatalErrors = false;
    //private JDialog parent_;
    private Window parent_;
    protected boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;

    public ReqMsgsList() {
        super();
        setLayout(new BorderLayout());
        init();
    }
    void init() {
        setOpaque(isOpaque);
        list_.setBackground(Color.lightGray);
        list_.setCellRenderer(new MsgsListCellRenderer());
        list_.removeMouseListener(null);
        list_.removeKeyListener(null);
        list_.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.getClickCount() == 2) {
                MsgListItem selItem = (MsgListItem) list_.getSelectedValue();
                if (parent_ instanceof SortedFrame) {
                  //  ((SortedFrame) parent_).dispose();
                    ((SortedFrame) parent_).processOkClicked();
                } 
                try {
                    returnToSelectedField(selItem);
                } catch (KrnException e1) {
                    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }}
        });
        list_.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    MsgListItem selItem = (MsgListItem) list_.getSelectedValue();
                    if (parent_ instanceof SortedFrame) {
                      //  ((SortedFrame) parent_).dispose();
                        ((SortedFrame) parent_).processOkClicked();
                    } 
                    try {
                        returnToSelectedField(selItem);
                    } catch (KrnException e1) {
                        e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            }
        });
        
        JScrollPane scroll =  new JScrollPane(list_);
        list_.setOpaque(isOpaque);
        scroll.setOpaque(isOpaque);
        scroll.getViewport().setOpaque(isOpaque);
        add(scroll, BorderLayout.CENTER);
        setPreferredSize(new Dimension(800, 400));
    }

    public int getListSize() {
        return list_.getModel().getSize();
    }

    public boolean hasFatalErrors() {
        return hasFatalErrors;
    }

    public void setParent(JDialog dlg) {
        parent_ = dlg;
    }

    public void setParent(Window dlg) {
        parent_ = dlg;
    }

    public void addToList(MsgListItem[] items) {
        for (int i = 0; i < items.length; i++) {
            if (items[i].enterBDType_ == Constants.BINDING ||
                    items[i].enterBDType_ == Constants.CONSTRAINT) {
                hasFatalErrors = true;
                break;
            }
        }
        list_.setListData(items);
    }

    public JList getList() {
        return list_;
    }
    public void setList(JList list_) {
        this.list_= list_;
    }

    public static class MsgListItem extends Component {

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

    public static class SortOnMessType implements Comparator {

        private int sortDirect_;

        public SortOnMessType(int sortDirect) {
            sortDirect_ = sortDirect;
        }

        public int compare(Object o1, Object o2) {
            if (o1 instanceof MsgListItem && o2 instanceof MsgListItem) {
                Integer i1 = new Integer(((MsgListItem) o1).getType());
                Integer i2 = new Integer(((MsgListItem) o2).getType());
                if (sortDirect_ == SORT_DOWN) {
                    return i1.compareTo(i2);
                } else {
                    return i2.compareTo(i1);
                }
            } else {
                return -1;
            }

        }
    }

    class MsgsListCellRenderer extends JLabel implements ListCellRenderer {

        ImageIcon imCritical = kz.tamur.rt.Utils.getImageIcon("critical");

        ImageIcon imOptional = kz.tamur.rt.Utils.getImageIcon("optional");

        ImageIcon imConstraint = kz.tamur.rt.Utils.getImageIcon("constraint");

        public MsgsListCellRenderer() {
            // setOpaque(true);
            setIconTextGap(5);
        }

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                boolean cellHasFocus) {
            setFont(new Font("Dialog", Font.PLAIN, 12));
            // задать цвет строчек в таблице с описанием ошибки
            if (((MsgListItem) value).getType() == Constants.CONSTRAINT) {
                setForeground(new Color(37, 103, 12));
                setIcon(imConstraint);
            } else if (((MsgListItem) value).getType() == Constants.BINDING) {
               setForeground(new Color(145, 0, 0));
                setIcon(imCritical);
            } else {
                setForeground(Color.black);
                setIcon(imOptional);
            }
            setBackground(isSelected ? Color.white : Color.lightGray);

            setOpaque(isSelected || isOpaque);

            setText(value.toString());
            return this;
        }
    }

    private void returnToSelectedField(MsgListItem selItem) throws KrnException {
        if (selItem == null) {
            return;
        }
//        if (!selItem.ref.isColumn()) {
//            selItem.ref.fireSetFocusEvent(-1, null);
//        } else {
            Pair[] locs=selItem.getLoc();
            if(locs!=null && locs.length>0){
                for(Pair loc:locs){
                    OrRef ref_=(OrRef)loc.first;
                    int index_=(Integer)loc.second;
                    ref_.absolute(index_, null);
                }
            }
            selItem.ref.fireSetFocusEvent(selItem.index, selItem.langId, null);
 //       }
    }

    //Установка положения SplitPane
    private void setViewportPosition(JViewport view, JComponent comp) {
        if (view == null) {
            return;
        }
        Point pt = view.getViewPosition();
        pt.x = comp.getX();
        pt.y = comp.getY();
        int maxXExt = view.getView().getWidth() - view.getWidth();
        int maxYExt = view.getView().getHeight() - view.getHeight();
        pt.x = Math.max(0, pt.x);
        pt.x = Math.min((maxXExt < 0) ? 0 : maxXExt, pt.x);
        pt.y = Math.max(0, pt.y);
        pt.y = Math.min((maxYExt < 0) ? 0 : maxYExt, pt.y);
        view.setViewPosition(pt);
        view.setBackground(Color.BLUE);
    }

}
