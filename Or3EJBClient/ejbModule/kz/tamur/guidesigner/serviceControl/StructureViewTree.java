package kz.tamur.guidesigner.serviceControl;

import static com.cifs.or2.client.Kernel.SC_FILTER;
import static com.cifs.or2.client.Kernel.SC_PROCESS_DEF;
import static com.cifs.or2.client.Kernel.SC_REPORT_PRINTER;
import static com.cifs.or2.client.Kernel.SC_UI;
import static kz.tamur.rt.Utils.getImageIconFull;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import kz.tamur.comps.Constants;
import kz.tamur.util.AbstractDesignerTreeCellRenderer;
import kz.tamur.util.DesignerTree;
import kz.tamur.util.ServiceControlNode;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.Utils;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.StringValue;

/**
 * Дерево просмотра структуры.
 * 
 * @author Lebedev Sergey
 */
public class StructureViewTree extends DesignerTree {

    /** The lang id. */
   private long langId;

    /**
     * Конструктор класса structure view tree.
     * 
     * @param root
     *            the root
     */
    public StructureViewTree(StructureViewNode root) {
        super(root);
        this.root = root;
        langId = Utils.getInterfaceLangId();

        setCellRenderer(new CellRenderer());
        if (isOpaque) {
            setBackground(kz.tamur.rt.Utils.getLightSysColor());
        }

        try {
            setCursor(Constants.WAIT_CURSOR);
            addSubElements(root.getKrnObj(), root);
            setCursor(Constants.DEFAULT_CURSOR);
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }

    /**
     * Конструктор класса structure view tree.
     * 
     * @param root
     *            the root
     * @param enableDragAndDrop
     *            the enable drag and drop
     */
    public StructureViewTree(TreeNode root, boolean enableDragAndDrop) {
        super(root, enableDragAndDrop);
    }

    @Override
    protected void defaultDeleteOperations() throws KrnException {
    }

    @Override
    protected void find() {
    }

    @Override
    protected void pasteElement() {
    }

    @Override
    protected void showPopup(MouseEvent e) {

    }

    /**
     * The Class CellRenderer.
     * 
     * @author Lebedev Sergey
     */
    private class CellRenderer extends AbstractDesignerTreeCellRenderer {

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf,
                int row, boolean hasFocus) {
            JLabel l = (JLabel) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            StructureViewNode node = (StructureViewNode) value;
            if (node != null) {
                if (node.isService()) {
                    l.setIcon(getImageIconFull("serviceNode.png"));
                } else if (node.isInterface()) {
                    l.setIcon(getImageIconFull("interfaceNode.png"));
                } else if (node.isFilter()) {
                    l.setIcon(getImageIconFull("filterNode.png"));
                } else if (node.isReport()) {
                    l.setIcon(getImageIconFull("reportNode.png"));
                }
                l.setForeground(Color.BLACK);
            }

            l.setBackground(Color.LIGHT_GRAY);

            l.setOpaque(selected || isOpaque);
            return l;
        }

    }

    /**
     * Adds the sub elements.
     * 
     * @param service
     *            the service
     * @param selNodeParent
     *            the sel node parent
     * @throws KrnException
     *             the krn exception
     */
    public void addSubElements(KrnObject service, StructureViewNode selNodeParent) throws KrnException {
        System.out.println("Добавляем суб элементы для узла: "+selNodeParent.getTitle());
        KrnObject serv = service;
        StructureViewNode selNode = selNodeParent;
        String elementTitle = "";
        KrnObject subElement = null;
        boolean isContains = false;
        String parentUID = "";
        Kernel krn = Kernel.instance();
        List<String> serviceElements = new ArrayList<String>();
        serviceElements = kz.tamur.util.XmlParserUtil.getObjectElements(serv);
        if (serviceElements.size() > 0 || serviceElements != null) {
            long id;
            KrnObject val;
            StructureViewNode selNodeChild;
            StringValue[] svs;
            StructureViewNode newNode;
            boolean dontRep;
            StructureViewNode supProcess;
            for (int i = 0; i < serviceElements.size(); i++) {
                isContains = false;
                subElement = krn.getObjectByUid(serviceElements.get(i), 0);
                if (subElement != null) {
                    id = subElement.classId;
                    if (id == SC_UI.id || id == SC_FILTER.id || id == SC_REPORT_PRINTER.id || id == SC_PROCESS_DEF.id) {
                        parentUID = " ";
                        for (int k = 1; k < selNode.getPath().length - 1; k++) {
                            val = ((StructureViewNode) selNode.getPath()[k]).getKrnObj();
                            if (val != null) {
                                parentUID = val.uid;
                                if (parentUID.equals(serv.uid)) {
                                    isContains = true;
                                }
                            }
                        }

                        if (isContains == false)
                            for (int j = 0; j < selNode.getChildCount(); j++) {
                                selNodeChild = (StructureViewNode) selNode.getChildAt(j);
                                if (subElement.uid.equals(selNodeChild.getKrnObj().uid)) {
                                    isContains = true;
                                }
                            }

                        if (!isContains && !subElement.uid.equals(serv.uid)) {
                            svs = krn.getStringValues(new long[]{ subElement.id }, id, "title", langId, false, 0);
                            elementTitle = "Не назначен заголовок";
                            if (svs.length > 0 && svs[0] != null) {
                                elementTitle = svs[0].value;
                            }

                            newNode = new StructureViewNode(subElement, elementTitle, langId);
                            ((DefaultTreeModel) getModel()).insertNodeInto(newNode, selNode, selNode.getChildCount());
                            dontRep = true;
                            // поиск дубликатов интефейсов в супер процессе
                            if (SC_UI.id == id) {
                                supProcess = getSuperProcess(newNode);
                                if (supProcess != null) {
                                    dontRep = !supProcess.findLoadedChildValue(newNode);
                                }
                            }
                            if (!getDeadLock(newNode, subElement) && dontRep) {
                                addSubElements(subElement, newNode);
                            }
                            
                        }

                    }
                }
            }
        }
    }
    
    /**
     * Получить супер-процесс элемента.
     * 
     * @param selNode
     *            узел, для которого производиться поиск супер-процесса
     * @return super process
     */
    public StructureViewNode getSuperProcess(StructureViewNode selNode) {
        // создать список для всех процессов-родителей
        ArrayList<StructureViewNode> list = new ArrayList<StructureViewNode>();
        getSuperProcess(selNode, list);
        if (list.size() == 0) {
            return null;
        } else {
            // верхний элемент списка и будет суперпроцессом для данного узла
            return list.get(list.size() - 1);
        }
    }

    /**
     * Получить список родительских процессов элемента.
     * 
     * Рекурсия.
     * 
     * @param selNode
     *            узел, для которого производиться поиск супер-процесса
     * @param list
     *            the list
     */
    public void getSuperProcess(StructureViewNode selNode, List<StructureViewNode> list) {
        StructureViewNode parent = (StructureViewNode) selNode.getParent();
        if (selNode != null && selNode.isService()) {
            list.add(selNode);
        }
        if (parent != null) {
            getSuperProcess(parent, list);
        }

    }
    
    public boolean getDeadLock(StructureViewNode selNode, KrnObject object) {
        StructureViewNode parent = (StructureViewNode) selNode.getParent();
        return parent == null ? false : parent.getKrnObj() != null && (parent.getKrnObj().equals(object) || getDeadLock(parent, parent.getKrnObj()));
    }
}
