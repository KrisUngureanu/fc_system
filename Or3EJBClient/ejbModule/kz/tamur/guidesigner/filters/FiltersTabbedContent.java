package kz.tamur.guidesigner.filters;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.User;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;

import kz.tamur.comps.OrGuiComponent;
import kz.tamur.rt.Utils;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.ui.tabbedPane.OrBasicTabbedPane;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.guidesigner.PropertyListener;
import kz.tamur.guidesigner.users.Or3RightsNode;
import kz.tamur.or3.client.util.AllMouseEventProcessor;
import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import kz.tamur.guidesigner.serviceControl.ControlTabbedContent.FilterRecord;
import static kz.tamur.rt.Utils.createMenuItem;
public class FiltersTabbedContent extends OrBasicTabbedPane implements PropertyListener {

    private Map<Long, FilterRecord> filters = new TreeMap<Long, FilterRecord>();
    private java.util.List<Long> filterIds = new ArrayList<Long>();

    private OrFilterNode copyNode;
    private FiltersTree tree;

    private JPopupMenu pm = new JPopupMenu();
    private JMenuItem miSave = createMenuItem("Сохранить");
    private JMenuItem miClose = createMenuItem("Закрыть");
    private JMenuItem miDelete = createMenuItem("Удалить");

    private FiltersPanel mainAncestor;

    private ImageIcon iconNorm = kz.tamur.rt.Utils.getImageIcon("filterOpen");
    private ImageIcon iconMod = kz.tamur.rt.Utils.getImageIcon("FilterNodeMod");
    private ImageIcon iconRO = kz.tamur.rt.Utils.getImageIcon("FilterNodeRO");

    private boolean canEdit = false;
    private boolean canDelete = false;
    private FiltersPanel fp;

    public FiltersTabbedContent(FiltersPanel owner) {
        super();
        this.fp=owner;
        User user = Kernel.instance().getUser();
        canEdit = user.hasRight(Or3RightsNode.FILTERS_EDIT_RIGHT);
        canDelete = user.hasRight(Or3RightsNode.FILTERS_DELETE_RIGHT);

        setFont(Utils.getDefaultFont());
        setBorder(null);
        miSave.setIcon(kz.tamur.rt.Utils.getImageIcon("Save"));
        miSave.setEnabled(false);
        miSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveCurrent(null);
                setAllSaveEnabled();
            }
        });
        miClose.setEnabled(false);
        miClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String title = getTitleAt(getSelectedIndex());
                FilterRecord fr = filters.get(filterIds.get(getSelectedIndex()));
                if (!fr.isReadOnly() && fr.isModified()) {
                    String mes = "Фильтр \"" + title +
                            "\" был модифицирован!\n Сохранить изменения?";
                    int res = MessagesFactory.showMessageDialog(
                            (Frame)getTopLevelAncestor(),
                            MessagesFactory.CONFIRM_MESSAGE, mes);
                    if (res != ButtonsFactory.BUTTON_NOACTION) {
                        if (res == ButtonsFactory.BUTTON_YES) {
                            saveCurrent(null);
                        } else if (res == ButtonsFactory.BUTTON_CANCEL) {
                            return;
                        }
                    }
                }
                try {
                	removeSelected();
                } catch (KrnException ex) {
                	ex.printStackTrace();
                }
                setAllSaveEnabled();
            }
        });
/*
        miCopy.setEnabled(false);
        miCopy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //copyInterface();
            }
        });
*/
        miDelete.setEnabled(false);
        miDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteCurrent();
            }
        });
        pm.add(miSave);
        pm.add(miClose);
        //pm.addSeparator();
        //pm.add(miCopy);
        pm.addSeparator();
        pm.add(miDelete);
        setOpaque(isOpaque);
        
        addMouseListener(new AllMouseEventProcessor() {
        	@Override
            public void process(MouseEvent e) {
                if (e.isPopupTrigger() && filterIds!=null && filterIds.size()>0) {
                    FilterRecord f = getSelectedFilterRecord();
                    if (f != null && f.isReadOnly()) {
                        miSave.setEnabled(false);
                        miDelete.setEnabled(false);
                    } else {
                        if (isTabModified(filterIds.get(getSelectedIndex()))) {
                            miSave.setEnabled(canEdit);
                        } else {
                            miSave.setEnabled(false);
                        }
                        miDelete.setEnabled(canDelete);
                    }
                    miClose.setEnabled(true);
                    pm.show(FiltersTabbedContent.this, e.getX(), e.getY());
                }
            }
        });
    }
    
    private void setAllSaveEnabled() {
        if (getTabCount() > 0) {
            for (int i = 0; i < getTabCount(); i++) {
                FilterRecord filterRecord = filters.get(filterIds.get(i));
                if (!filterRecord.isReadOnly() && filterRecord.isModified()) {
                    mainAncestor.setSaveEnabled(true);
                    return;
                }
            }
        }
        mainAncestor.setSaveEnabled(false);
    }

    public void addFilterTab(String title, OrFilterTree tree, FilterNode parent, boolean readOnly) {
        JScrollPane sp = new JScrollPane(tree);
        sp.setOpaque(isOpaque);
        sp.getViewport().setOpaque(isOpaque);
        ImageIcon image = (readOnly) ? iconRO : iconNorm;
        filters.put(parent.getKrnObj().id, new FilterRecord(tree, parent, false, readOnly));
        filterIds.add(parent.getKrnObj().id);
        super.addTab(title, image, sp);
        setSelectedIndex(getComponentCount() - 1);
        tree.addPropertyListener(this);
    }

    public void removeSelected() throws KrnException {
        int idx = getSelectedIndex();
        Long objId = filterIds.remove(idx);
        FilterRecord f = filters.remove(objId);
        removeTabAt(idx);
        if (!f.readOnly)
            Kernel.instance().releaseEngagedObject(objId);
        fireChange();
    }

    public boolean isFilterOpened(Long id) {
        for (int i = 0; i < getTabCount(); i++) {
            if (id.equals(filterIds.get(i))) {
                setSelectedIndex(i);
                return true;
            }
        }
        return false;
    }

    public boolean isTabModified(Long id) {
        FilterRecord p = filters.get(id);
        return p.isModified;
    }

    public OrFilterNode getCopyNode() {
        return copyNode;
    }

    public void setCopyNode(OrFilterNode copyNode) {
        this.copyNode = copyNode;
    }



    public OrFilterTree getSelectedFilter() {
        int idx = getSelectedIndex();
        if (idx != -1) {
            FilterRecord p = filters.get(filterIds.get(idx));
            return p.o;
        } else {
            return null;
        }
    }

    public FilterRecord getSelectedFilterRecord() {
        int idx = getSelectedIndex();
        if (idx != -1) {
            return filters.get(filterIds.get(idx));
        } else {
            return null;
        }
    }

    private void setTabModified(boolean isModified) {
        if (getSelectedIndex() != -1) {
            FilterRecord f = filters.get(filterIds.get(getSelectedIndex()));
            f.setModified(isModified);
            if (!f.isReadOnly()) {
                if (isModified) {
                    setIconAt(getSelectedIndex(), iconMod);
                } else {
                    setIconAt(getSelectedIndex(), iconNorm);
                }
            }
        }
    }

    private void setTabModified(boolean isModified, FilterRecord f) {
        f.setModified(isModified);
        if (!f.isReadOnly()) {
            int index = filterIds.indexOf(f.parent.getKrnObj().id);
            if (isModified) {
                setIconAt(index, iconMod);
            } else {
                setIconAt(index, iconNorm);
            }
        }
    }

    private void deleteCurrent() {
        int idx = getSelectedIndex();
        Long objId = filterIds.get(idx);

        String mess = "Удалить фильтр '" + getTitleAt(idx) + "'?";
        int res = MessagesFactory.showMessageDialog((Frame)getTopLevelAncestor(),
                MessagesFactory.QUESTION_MESSAGE, mess);
        if (res == ButtonsFactory.BUTTON_YES) {
            try {
                if (tree != null) {
                    FilterRecord fr = filters.get(objId);
                    final FiltersTree.FilterTreeModel model =
                            (FiltersTree.FilterTreeModel)tree.getModel();
                    model.deleteNode(fr.parent, false);
                    removeSelected();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void propertyModified(OrGuiComponent c) {
        OrFilterNode fn = (OrFilterNode)c;
        FilterNode n = fn.getFilterNode();
        FilterRecord p = filters.get(n.getKrnObj().id);
        if (!p.isReadOnly()) {
            setTabModified(true, p);
            if (mainAncestor != null) {
                mainAncestor.setSaveEnabled(true);
            }
        }
    }

    public void propertyModified(OrGuiComponent c, PropertyNode property) {
        propertyModified(c);
        String prop_m= property.getName();
        if ("title".equals(prop_m) || "unionFlr".equals(prop_m) || "attrFlr".equals(prop_m)
                || "operFlr".equals(prop_m) || "krnObjFlr".equals(prop_m)
                || "exprFlr".equals(prop_m) || "compAttrFlr".equals(prop_m)) {
            OrFilterNode fn = (OrFilterNode)c;
            FilterNode n = fn.getFilterNode();
            FilterRecord p = filters.get(n.getKrnObj().id);
            OrFilterTree ftree = p.o;
            DefaultTreeModel model = (DefaultTreeModel)ftree.getModel();
            model.nodeChanged((OrFilterNode)c);
            //ftree.repaint();
/*
            if ("title".equals(prop_m) && c == ftree.getModel().getRoot()) {
                PropertyValue pv = c.getPropertyValue(property);
                setTitleAt(idx, pv.stringValue());
                tree.renameFilter(p.parent, pv.stringValue());
            }
*/
        }
    }

    public void propertyModified(OrGuiComponent c, int propertyEvent) {
    }

    public void setMainAncestor(FiltersPanel mainAncestor) {
        this.mainAncestor = mainAncestor;
    }

    private void saveCurrent(FilterRecord rec) {
        if (mainAncestor != null) {
            if (rec == null) {
                rec = filters.get(filterIds.get(getSelectedIndex()));
            }
            if (!rec.isReadOnly()) {
/*
                OrFilterTree ftree = rec.o;
                FilterNode node = rec.parent;
                String title = ftree.getRoot().getTitle();
                if (!title.equals(ftree.getRoot().getOldTitle())) {
                    Kernel krn = Kernel.instance();
                    try {
                        KrnObject obj = node.getKrnObj();
                        KrnObject lang = krn.getInterfaceLanguage();
                        long langId = (lang == null) ? 0 : lang.id;
                        krn.setString(obj.id,  obj.classId, "title", 0, langId, title, 0);
                        ftree.getRoot().setOldTitle();
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
*/
                mainAncestor.saveCurrent(rec.o);
                setTabModified(false);
            }
        }
    }

    public void saveAll() {
        for (int i = 0; i < getTabCount(); i++) {
            FilterRecord filterRecord = filters.get(filterIds.get(i));
            if (filterRecord.isModified()) {
                saveCurrent(filterRecord);
            }
        }
        if (getTabCount() == 1) {
            mainAncestor.setSaveEnabled(false);
        } else if (getTabCount() > 1) {
            for (int i = 0; i < getTabCount(); i++) {
                FilterRecord filterRecord = filters.get(filterIds.get(i));
                if (!filterRecord.isReadOnly() && filterRecord.isModified()) {
                    mainAncestor.setSaveEnabled(true);
                    break;
                }
            }
        }
        miSave.setEnabled(false);
    }
    public int getIndexFor(KrnObject obj){
        int res=-1;
        for (int i = 0; i < getTabCount(); i++) {
            FilterRecord rec =  filters.get(filterIds.get(i));
            if(rec.parent.getKrnObj().id==obj.id)
            return i;
        }
        return res;
    }
    public void setTree(FiltersTree tree) {
        this.tree = tree;
    }

    public String[] getModifiedTitles() {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < getTabCount(); i++) {
            FilterRecord rec =  filters.get(filterIds.get(i));
            if (!rec.isReadOnly() && rec.isModified()) {
                list.add(rec.parent.toString());
            }
        }
        String[] res = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            res[i] = list.get(i);
        }
        return res;
    }
    public FiltersPanel getOwner(){
           return fp;
    }
    public void fireChange() {
        fireStateChanged();
    }

    /**
     * @return the filters
     */
    public Map<Long, FilterRecord> getFilters() {
        return filters;
    }
}
