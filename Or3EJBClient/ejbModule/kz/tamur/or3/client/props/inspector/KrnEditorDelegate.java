package kz.tamur.or3.client.props.inspector;

import com.cifs.or2.client.ClassNode;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.util.CnrBuilder;
import com.cifs.or2.client.util.KrnObjectItem;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;

import kz.tamur.Or3Frame;
import kz.tamur.admin.ClassBrowser;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.guidesigner.MultiEditor;
import kz.tamur.guidesigner.filters.FiltersTree;
import kz.tamur.guidesigner.filters.FilterNode;
import kz.tamur.guidesigner.filters.FiltersPanel;
import kz.tamur.guidesigner.serviceControl.ServicesControlTree;
import kz.tamur.guidesigner.users.HypersEditor;
import kz.tamur.util.DesignerTreeNode;
import kz.tamur.util.ObjectList;
import kz.tamur.util.ServiceControlNode;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class KrnEditorDelegate extends JPanel implements EditorDelegate, RendererDelegate, ActionListener {

	private Object value;
    private PropertyEditor editor;
    private String lastPath="";
    private String className;
    private String titleAttr;

    private JTextField label;
	private JButton krnBtn;

    public KrnEditorDelegate(JTable table, String className, String titleAttr) {
        this.className = className;
        this.titleAttr = titleAttr;
        setLayout(new GridBagLayout());
        label = kz.tamur.comps.Utils.createEditor(this,table.getFont());
        krnBtn = kz.tamur.comps.Utils.createBtnEditor(this);
        add(label, new CnrBuilder().x(1).wtx(1).fill(GridBagConstraints.HORIZONTAL).build());
        add(krnBtn, new CnrBuilder().x(0).build());
    }

	public int getClickCountToStart() {
		return 1;
	}

	public Component getEditorComponent() {
		return this;
	}

	public Object getValue() {
		return value;
	}

    public void setValue(Object value) {
        this.value = value;
        if (value instanceof Vector && ((Vector) value).size() > 0) {
            String label_ = "";
            for (int i = 0; i < ((Vector) value).size(); i++) {
                label_ += (i > 0 ? "," : "") + ((KrnObjectItem) ((Vector) value).get(i)).title;
            }
            label.setText(label_);
        }else if (value instanceof String) {
            label.setText(value.toString());
        } else {
            label.setText("");
        }
    }

	public Component getRendererComponent() {
		return this;
	}

	public void setPropertyEditor(PropertyEditor editor) {
		this.editor=editor;

	}
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == krnBtn) {
            if("Структура баз".equals(className)){

                MultiEditor me = new MultiEditor(MultiEditor.BASE_EDITOR, -1);
                Vector val = value instanceof Vector?(Vector)value:null;
                if (val != null) {
                    KrnObject[] oldVals = new KrnObject[val.size()];
                    for (int i = 0; i <oldVals.length; i++) {
                        oldVals[i] = ((KrnObjectItem)val.get(i)).obj;
                    }
                    me.setOldValue(oldVals);
                }
                DesignerDialog dlg = new DesignerDialog(Or3Frame.instance(),
                        "Выберите структуру баз", me,true);
                dlg.show();
                int res = dlg.getResult();
                if (res != ButtonsFactory.BUTTON_NOACTION
                    && res == ButtonsFactory.BUTTON_OK) {
                    DesignerTreeNode[] bases = me.getSelectedNodeValues();
                    if (bases != null && bases.length > 0) {
                        Vector<KrnObjectItem> value_=new Vector<KrnObjectItem>();
                        for (int i = 0; i < bases.length; i++) {
                            DesignerTreeNode base = bases[i];
                            KrnObject obj = base.getKrnObj();
                            String title = base.toString();
                            value_.add(new KrnObjectItem(obj,title));
                        }
                        value=value_;
                        editor.stopCellEditing();
                    }else{
                        value = null;
                        editor.stopCellEditing();
                    }
                } else if (dlg.getResult() == ButtonsFactory.BUTTON_CLEAR) {
                    value = null;
                    editor.stopCellEditing();
                }
            }else if("HyperTree".equals(className)){
                HypersEditor he = new HypersEditor();
                if (value instanceof Vector) {
                    KrnObject[] objs = new KrnObject[((Vector) value).size()];
                    int i=0;
                    for(KrnObjectItem obj:(Vector<KrnObjectItem>)value){
                       objs[i++]=obj.obj;
                    }
                    he.setOldValue(objs);
                } else
                	he.setOldValue(null);
                DesignerDialog dlg =
                        new DesignerDialog(Or3Frame.instance(),
                                "Выбор доступных пунктов", he);
                dlg.show();
                if (dlg.isOK()) {
                    Vector value_ = he.getSelectedItems();
                    if (value_ != null && value_.size() > 0) {
                        value=value_;
                        editor.stopCellEditing();
                    }else{
                        value = null;
                        editor.stopCellEditing();
                    }
                } else if (dlg.getResult() == ButtonsFactory.BUTTON_CLEAR) {
                    value = null;
                    editor.stopCellEditing();
                }else{
                    editor.cancelCellEditing();
                }
            }else if("Note".equals(className)){

                MultiEditor me = new MultiEditor(MultiEditor.NOTE_EDITOR, -1);
                Vector val = value instanceof Vector?(Vector)value:null;
                if (val != null) {
                    KrnObject[] oldVals = new KrnObject[val.size()];
                    for (int i = 0; i <oldVals.length; i++) {
                        oldVals[i] = ((KrnObjectItem)val.get(i)).obj;
                    }
                    me.setOldValue(oldVals);
                }
                DesignerDialog dlg = new DesignerDialog(Or3Frame.instance(),
                        "Выберите помощь", me,true);
                dlg.show();
                int res = dlg.getResult();
                if (res != ButtonsFactory.BUTTON_NOACTION
                    && res == ButtonsFactory.BUTTON_OK) {
                    DesignerTreeNode[] bases = me.getSelectedNodeValues();
                    if (bases != null && bases.length > 0) {
                        Vector<KrnObjectItem> value_=new Vector<KrnObjectItem>();
                        for (int i = 0; i < bases.length; i++) {
                            DesignerTreeNode base = bases[i];
                            KrnObject obj = base.getKrnObj();
                            String title = base.toString();
                            value_.add(new KrnObjectItem(obj,title));
                        }
                        value=value_;
                        editor.stopCellEditing();
                    }else{
                        value = null;
                        editor.stopCellEditing();
                    }
                } else if (dlg.getResult() == ButtonsFactory.BUTTON_CLEAR) {
                    value = null;
                    editor.stopCellEditing();
                }else{
                    editor.cancelCellEditing();
                }
            }else if("Filter".equals(className)){
                MultiEditor me = new MultiEditor(MultiEditor.FILTER_EDITOR,-1);
                Vector val = value instanceof Vector?(Vector)value:null;
                if (val != null) {
                    KrnObject[] oldVals = new KrnObject[val.size()];
                    for (int i = 0; i <oldVals.length; i++) {
                        oldVals[i] = ((KrnObjectItem)val.get(i)).obj;
                    }
                    me.setOldValue(oldVals);
                }
                DesignerDialog dlg = new DesignerDialog(Or3Frame.instance(),
                        "Выберите фильтры", me, false, false, true);
                dlg.setEditVisible(false);
                me.setAddBtnEnabled(!me.isListExists(me.initNode));
                me.setLastPath(lastPath);
                while(true) {
                	if (me.getTree().getSelectedNode() != null) {
                		DefaultTreeModel m = (DefaultTreeModel) me.getTree().getModel();
                		TreeNode[] path = m.getPathToRoot(me.getTree().getSelectedNode());
                		TreePath tpath = new TreePath(path);
                		me.getTree().setSelectionPath(tpath);
                		me.getTree().scrollPathToVisible(tpath);
                		DesignerTreeNode node = me.getTree().getSelectedNode();
                		if(node != null)
                			me.setSearchText(node.getKrnObj().uid);
                	} else {
                		me.setAddBtnEnabled(false);
                	}
                    dlg.show();
                    int res = dlg.getResult();
                    if (res != ButtonsFactory.BUTTON_NOACTION
                        && res == ButtonsFactory.BUTTON_OK) {
                        DesignerTreeNode[] bases = me.getSelectedNodeValues();
                        if (bases != null && bases.length > 0) {
                            Vector<KrnObjectItem> value_=new Vector<KrnObjectItem>();
                            for (int i = 0; i < bases.length; i++) {
                                DesignerTreeNode base = bases[i];
                                KrnObject obj = base.getKrnObj();
                                String title = base.toString();
                                value_.add(new KrnObjectItem(obj,title));
                            }
                            value=value_;
                            editor.stopCellEditing();
                            break;
                        }else{
                            value = null;
                            editor.stopCellEditing();
                            break;
                        }
                    } else if (res == ButtonsFactory.BUTTON_EDIT) {
                        FiltersTree filtersTree;
                        FilterNode fnode;
                        if (me.getTree() instanceof FiltersTree) {
                            filtersTree = (FiltersTree)me.getTree();
                            fnode = (FilterNode)me.getTree().getSelectedNode();
                        }else {
                           ServicesControlTree tree = (ServicesControlTree)me.getTree();
                           KrnObject object = ((ServiceControlNode)tree.getSelectedNode()).getValue();
                           filtersTree = kz.tamur.comps.Utils.getFiltersTree();
                           fnode = (FilterNode) filtersTree.getRoot().find(object).getLastPathComponent();
                        }
                        
                        if (fnode!=null && fnode.isLeaf()) {
                            FiltersPanel flrPanel = new FiltersPanel(true);
                            flrPanel.load(fnode,null);
                            DesignerDialog dlgEdit = new DesignerDialog(
                                    (Dialog)me.getTopLevelAncestor(),
                                        "Корректировка фильтра - " + fnode.toString(),
                                    flrPanel, false, false, false);
                            dlgEdit.setOnlyOkButton();
                            Insets in = Toolkit.getDefaultToolkit().getScreenInsets(
                                    me.getGraphicsConfiguration());
                            Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
                            dlgEdit.setLocation(in.left, in.top);
                            dlgEdit.setSize(ss.width - in.right, ss.height - in.bottom);
                            dlgEdit.show();
                            flrPanel.processExit();
                            filtersTree.renameFilter(fnode, fnode.toString());
                        }
                    } else if (dlg.getResult() == ButtonsFactory.BUTTON_CLEAR) {
                        value = null;
                        editor.stopCellEditing();
                        break;
                    }else{
                        editor.cancelCellEditing();
                        break;
                    }
                }
            }
            else{
                try {
                    KrnClass cls=null;
                    KrnAttribute attr=null;
                    Kernel krn=Kernel.instance();
                    if(className!=null && !"".equals(className)){
                        cls=krn.getClassByName(className);
                        attr=krn.getAttributeByName(cls,titleAttr);
                    }
                    if (cls == null && attr == null) {
                        ClassNode cnode = getClassNode();
                        ClassBrowser cb = new ClassBrowser(cnode, true);
                        DesignerDialog dlg = new DesignerDialog(Or3Frame.instance(),
                                "Выберите путь", cb,true);
                        dlg.show();
                        int res = dlg.getResult();
                        if (res != ButtonsFactory.BUTTON_NOACTION
                                && res == ButtonsFactory.BUTTON_OK) {
                            String spath = cb.getSelectedPath();
                            if (spath.length() > 0) {
                                lastPath = spath;
                            }
                            StringTokenizer st = new StringTokenizer(spath, ".");
                            int count = st.countTokens();
                                if (count > 0) {
                                    String head = st.nextToken();
                                    ClassNode classNode = krn.getClassNodeByName(head);
                                    for (int i = 0; i < count - 2; ++i) {
                                        String str = st.nextToken();
                                        attr = classNode.getAttribute(str);
                                        classNode = krn.getClassNode(attr.typeClassId);
                                    }
                                    attr = classNode.getAttribute(st.nextToken());
                                    cls = classNode.getKrnClass();
                                }
                        }
                    }
                    if (cls != null && attr != null) {
                        ObjectList oList = new ObjectList(cls, attr.name);
                        if(value instanceof Vector && ((Vector)value).size()>0){
                            int[] indexs=new int[((Vector)value).size()];
                            int i=0;
                            for(KrnObjectItem obj:((Vector<KrnObjectItem>)value)){
                                indexs[i++]=oList.getIndexById((int)obj.obj.id);
                            }
                            oList.setSelectedIndices(indexs);
                        }
                        JScrollPane sp = new JScrollPane(oList);
                        sp.setPreferredSize(new Dimension(600, 600));
                        DesignerDialog dlg = new DesignerDialog(
                                Or3Frame.instance(),
                                "Выберите объект", sp, true);
                        dlg.setDisposeOnClear(true);
                        dlg.show();
                        int res = dlg.getResult();
                        if (res == ButtonsFactory.BUTTON_OK) {
                            KrnObject[] objs = oList.getSelectedObjects();
                            String[] titles = oList.getSelectedTitles();
                            if (objs != null && objs.length>0) {
                                Vector<KrnObjectItem> value_=new Vector<KrnObjectItem>();
                                for(int i=0;i<objs.length;i++){
                                    value_.add(new KrnObjectItem(objs[i],titles[i]));
                                }
                                value=value_;
                            editor.stopCellEditing();
                            }
                        } else if (res == ButtonsFactory.BUTTON_CLEAR) {
                            value = null;
                            editor.stopCellEditing();
                        }else{
                            editor.cancelCellEditing();
                        }
                    }
                } catch (KrnException e1) {
                    e1.printStackTrace();
                }
            }
        } else if (e.getSource() == label) {
            String str = label.getText().trim();
            String[] objs = (str.replaceAll("\\d", "").isEmpty()) ? new String[] { str } : str.split("[^0-9]+");

            // валидация значения метки(ан соответствие типу KRNOBJECT_ID)
            if (objs.length==0) {
                editor.stopCellEditing();
                return;
            }
            for (String obj: objs) {
                if (!obj.replaceAll("\\d", "").isEmpty()) {
                    editor.stopCellEditing();
                    return;
                }   
            }
            
            // заполнения вектора значений
            Vector<KrnObjectItem> value_ = new Vector<KrnObjectItem>();
            KrnObject obj = null;
            for (String object: objs) {
                obj = new KrnObject();
                obj.id = Long.parseLong(object);
                value_.add(new KrnObjectItem(obj, null));
            }
            value = value_;
            editor.stopCellEditing();
        }
    }
    private ClassNode getClassNode() {
        ClassNode cls = null;
        final Kernel krn = Kernel.instance();
        String s = "";
        try {
            if ("".equals(lastPath)) {
                cls = krn.getClassNodeByName("Объект");
            } else {
                try {
                    StringTokenizer st = new StringTokenizer(lastPath, ".");
                    s = st.nextToken();
                    cls = krn.getClassNodeByName(s);
                } catch (KrnException e) {
                    MessagesFactory.showMessageDialog(Or3Frame.instance(),
                            MessagesFactory.ERROR_MESSAGE, "\"" + s +
                            "\" - ошибочное имя класса!");
                }
            }
        } catch (KrnException e) {
            e.printStackTrace();
        }

        return cls;
    }
}
