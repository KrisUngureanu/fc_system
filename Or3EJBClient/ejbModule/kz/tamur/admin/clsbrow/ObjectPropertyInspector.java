package kz.tamur.admin.clsbrow;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.util.MultiMap;

import kz.tamur.comps.Constants;
import kz.tamur.comps.Utils;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.SearchWindow;
import static kz.tamur.util.CollectionTypes.COLLECTION_SET;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.List;

public class ObjectPropertyInspector extends JPanel implements ActionListener {

    private ObjectPropertyTable table = new ObjectPropertyTable();
    private ObjectBrowser objectBrowser;
    private Stack<LinkObject> history_ = new Stack<LinkObject>();
    private ObjectInspectable obj;
    private JLabel pathAttrLabel = new JLabel();
    private SearchWindow searchWindow;
    private JScrollPane scroller;
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;

    public ObjectPropertyInspector(ObjectBrowser objectBrowser) {
        this.objectBrowser = objectBrowser;
        setLayout(new BorderLayout());
        JPanel pathAttr = new JPanel();
        add(pathAttr, BorderLayout.NORTH);
        pathAttr.add(pathAttrLabel);
        scroller = new JScrollPane(table);
        add(scroller, BorderLayout.CENTER);

        JToolBar toolBar = Utils.createDesignerToolBar();
        toolBar.setLayout(new BorderLayout());

        setOpaque(isOpaque);
        pathAttr.setOpaque(isOpaque);
        toolBar.setOpaque(isOpaque);
        table.setOpaque(isOpaque);
        scroller.setOpaque(isOpaque);
        scroller.getViewport().setOpaque(isOpaque);

        table.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if (shouldFind()) {
                    JTable table = (JTable) e.getSource();
                    table.editingCanceled(null);
                    if (e.getKeyChar() == KeyEvent.VK_ESCAPE || e.getKeyChar() == KeyEvent.VK_ENTER) {
                        if (searchWindow != null && searchWindow.isVisible()) {
                            searchWindow.setText("");
                            searchWindow.setVisible(false);
                        }
                    } else if (e.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
                        if (searchWindow != null && searchWindow.isVisible()) {
                            String text = searchWindow.deleteSymbol();
                            if (text.length() > 0) {
                                int foundRow = findRowByText(text);
                                searchWindow.setFound(foundRow > -1);
                            }
                        }
                    } else if (!e.isActionKey() && !e.isControlDown() && !e.isAltDown() && !(e.getKeyChar() == KeyEvent.VK_TAB)) {
                        String text = "" + e.getKeyChar();
                        if (searchWindow == null) {
                            Container c = table.getTopLevelAncestor();
                            if (c instanceof JDialog)
                                searchWindow = new SearchWindow((JDialog) c);
                            else
                                searchWindow = new SearchWindow((JFrame) c);
                        }
                        int row = table.getSelectedRow();
                        int col = table.getSelectedColumn();

                        if (searchWindow.isVisible())
                            text = searchWindow.addText(text);
                        else {
                            searchWindow.setText(text);
                            Rectangle rect = table.getCellRect(row, col, true);
                            Point locs = scroller.getLocationOnScreen();
                            Point loc = table.getLocationOnScreen();
                            searchWindow.setLocation(loc.x + rect.x + 1, locs.y - 21);
                        }
                        searchWindow.setVisible(true);

                        int foundRow = findRowByText(text);

                        searchWindow.setFound(foundRow > -1);

                    }
                } else {
                    super.keyTyped(e);
                }
            }
        });
    }

    public void setObjNull() {
        table.setNull();
    }

    public ObjectPropertyTable getObjectPropertyTable() {
        return table;
    }

    public void setObject(ObjectInspectable obj, boolean isHistory) {
        this.obj = obj;
        obj.setObjectInspector(this);
        table.setObject(obj);
        StringBuilder title = new StringBuilder(20);
        if (isHistory && history_.size() > 0) {
            for (int i = 0; i < history_.size(); i++) {
                if (i == 0) {
                    title.append(history_.get(i).obj.getTitle());
                }
                boolean par = history_.get(i).attr.collectionType > 0;
                title.append("." + history_.get(i).attr.name + (par ? "[" + history_.get(i).i + "]" : ""));
            }
            title.append(": ").append(obj.getKrnObject().id);
        } else if (history_.size() > 0) {
            history_.clear();
            objectBrowser.prevBtnManage(false);
            title.append(obj.getTitle());
            if(obj.getKrnObject() != null) {
                title.append(": ").append(obj.getKrnObject().id);  
            }
        } else if (obj != null) {
            title.append(obj.getTitle());
            if(obj.getKrnObject() != null) {
                title.append(": ").append(obj.getKrnObject().id);  
            }
        }

     pathAttrLabel.setText(title.toString());
    }

    public void setObjectHistory(KrnObject krnObj, KrnAttribute attr, int index) {
        ObjectInspectable obj_ = new KrnObjectNodeItem(krnObj, objectBrowser);
        history_.push(new LinkObject(obj, attr, index));
        setObject(obj_, true);
        objectBrowser.prevBtnManage(true);
    }

    public void setLabel(String label) {
        this.pathAttrLabel.setText(label);
    }

    public void actionPerformed(ActionEvent e) {
        try {
            Object src = e.getSource();
            Kernel krn = Kernel.instance();
            if (objectBrowser != null && src == objectBrowser.applyBtn) {
                MultiMap mmap = new MultiMap();
                obj.saveKrnObjectItem();
                // Применение
                Map objectArrayMap = obj.getObjectArray();
                for (Iterator it = objectArrayMap.values().iterator(); it.hasNext();) {
                    Vector pfs = (Vector) it.next();
                    for (Iterator i = pfs.iterator(); i.hasNext();) {
                        PropertyField pf = (PropertyField) i.next();
                        pf.save(mmap);
                    }
                    // Удаление
                    for (Iterator itt = mmap.keySet().iterator(); itt.hasNext();) {
                        Long aid = (Long) itt.next();
                        KrnAttribute attr = krn.getAttributeById(aid);
                        java.util.List<Object> indexes = (java.util.List<Object>) mmap.get(aid);
                        if (indexes.size() > 0) {
                            if (attr.collectionType == COLLECTION_SET) {
                                krn.deleteValue(obj.getKrnObject().id, aid, indexes, ObjectBrowser.transId);
                            } else {
                                int[] inds = new int[indexes.size()];
                                for (int i = 0; i < inds.length; ++i)
                                    inds[i] = ((Integer) indexes.get(i)).intValue();
                                Arrays.sort(inds);
                                System.out.println("" + obj.getKrnObject().id + "," + aid + "," + inds[0]);
                                krn.deleteValue(obj.getKrnObject().id, aid.intValue(), inds, ObjectBrowser.transId);
                            }
                        }
                    }
                }
                objectArrayMap.clear();
                enableTransactionButtons(false);
            } else if (objectBrowser != null && src == objectBrowser.rollbackBtn) {
                obj.fillObject(obj.getKrnObject());
                ((ObjectPropertyTableModel) table.getModel()).fireTableDataChanged();
                enableTransactionButtons(false);
            } else if (objectBrowser != null && src == objectBrowser.prevBtn) {
                // Возврат на уровень вверх
                setObject(history_.pop().obj, true);
                if (history_.size() == 0) {
                    objectBrowser.prevBtnManage(false);
                }
                enableTransactionButtons(false);
            }
        } catch (KrnException ex) {
            ex.printStackTrace();
            MessagesFactory.showMessageDialogBig(getTopLevelAncestor(), MessagesFactory.ERROR_MESSAGE, ex.getMessage());
        }
    }

    class LinkObject {
        public ObjectInspectable obj;
        public KrnAttribute attr;
        public int i;

        public LinkObject(ObjectInspectable obj, KrnAttribute attr, int index) {
            this.obj = obj;
            this.attr = attr;
            this.i = index;
        }
    }

    public void enableTransactionButtons(boolean isEnabled) {
        if (objectBrowser != null) {
            objectBrowser.enableTransactionButtons(isEnabled);
        }
    }

    public boolean shouldFind() {
        int col = table.getSelectedColumn();
        if (col > -1 && col < 3)
            return true;
        else
            return false;
    }

    public int findRowByText(String text) {
        int col = table.getSelectedColumn();
        List<ObjectProperty> ops = ((ObjectProperty) ((ObjectPropertyTableModel) table.getModel()).getRoot()).getChildren();
        int res = -1;
        for (int i = 0; i < ops.size(); i++) {
            String title = null;
            if (col == 0) {
                title = ops.get(i).getId();
            } else if (col == 1) {
                title = ops.get(i).getTtitle();
            } else if (col == 2) {
                title = ops.get(i).getType();
            }

            if (title != null && title.toLowerCase(Constants.OK).startsWith(text.toLowerCase(Constants.OK))) {
                table.getSelectionModel().setSelectionInterval(i, i);
                JViewport vp = scroller.getViewport();
                Point p = vp.getViewPosition();
                int lastRow = vp.getHeight() / table.getRowHeight() - 1;
                p.y = Math.max(0, (i - lastRow) * table.getRowHeight());
                vp.setViewPosition(p);
                res = i;
                break;
            }
        }
        return res;
    }
}
