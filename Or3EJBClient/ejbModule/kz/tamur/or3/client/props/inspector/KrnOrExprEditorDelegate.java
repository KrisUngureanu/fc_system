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
import kz.tamur.rt.Utils;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.guidesigner.filters.FilterItem;
import kz.tamur.or3.client.props.Expression;
import kz.tamur.or3.client.props.Inspectable;
import kz.tamur.or3.client.props.Property;
import kz.tamur.util.ExpressionEditor;
import kz.tamur.util.ObjectList;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;


public class KrnOrExprEditorDelegate extends JPanel implements EditorDelegate, RendererDelegate, ActionListener {

	private Object value;

    private PropertyEditor editor;

    private JTextField label;
	private JButton exprBtn;
	private JButton krnBtn;
    private String lastPath="";

    public KrnOrExprEditorDelegate(JTable table) {
        setLayout(new GridBagLayout());
        label = kz.tamur.comps.Utils.createEditor(this,table.getFont());
        exprBtn = kz.tamur.comps.Utils.createBtnEditor(this);
        krnBtn = kz.tamur.comps.Utils.createBtnEditorIfc(this);
        add(label, new CnrBuilder().x(2).wtx(1).fill(GridBagConstraints.HORIZONTAL).build());
        add(krnBtn, new CnrBuilder().x(0).build());
        add(exprBtn, new CnrBuilder().x(1).build());
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
        if (value instanceof Expression ){
            label.setText(((Expression)value).text);
            krnBtn.setEnabled("".equals(((Expression)value).text));
            exprBtn.setEnabled(true);
            label.setEnabled(true);
        }else if(value instanceof Vector && ((Vector)value).size()>0){
            String label_="";
            for(int i=0;i<((Vector)value).size();i++){
                label_ += (i>0?",":"")+ ((KrnObjectItem)((Vector)value).get(i)).title;
            }
            label.setText(label_);
            krnBtn.setEnabled(true);
            exprBtn.setEnabled(false);
            label.setEnabled(false);
        }else{
            krnBtn.setEnabled(true);
            exprBtn.setEnabled(true);
            label.setEnabled(true);
        }
    }

	public Component getRendererComponent() {
		return this;
	}

	public void setPropertyEditor(PropertyEditor editor) {
        this.editor = editor;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == krnBtn) {
            Inspectable ins= editor.getObject();
            String path="";
            String attr_name="";
            Kernel krn=Kernel.instance();
            KrnAttribute attr=null;
            KrnClass cls=null;
            try {
            if(editor.getObject() instanceof FilterItem){
                Property prop= ins.getProperties().getChild("attrFlr");
                if(prop!=null) {
                    path = (String) ins.getValue(prop);
                    try {
                        KrnAttribute[] attr_a=Utils.getAttributesForPath(path);
                        if(attr_a!=null && attr_a.length>0)
                            cls=krn.getClass((attr=attr_a[attr_a.length-1]).typeClassId);
                        else if(path.indexOf(".")<0)
                            cls=krn.getClassByName(path);

                    } catch (KrnException e1) {
                        e1.printStackTrace();
                    }
                    try {
                        if (cls!=null && cls.id>99) {
                            Collection list_ = krn.getAttributes(cls);
                            Vector attr_l = new Vector();
                            for (Iterator it = list_.iterator(); it.hasNext();) {
                                KrnAttribute attr_ = (KrnAttribute) it.next();
                                if (attr_.typeClassId == Kernel.IC_STRING) {
                                    attr_l.add(attr_.name);
                                }
                            }
                            if (attr_l.size() > 0) {
                                JList attr_list = new JList(attr_l);
                                final JScrollPane scroller = new JScrollPane(attr_list);
                                scroller.setPreferredSize(new Dimension(400, 200));
                                DesignerDialog dlg = new DesignerDialog(Or3Frame.instance(),
                                        "Выберите атрибут для отображения объектов", scroller,true);
                                dlg.show();
                                int res = dlg.getResult();
                                if (res != ButtonsFactory.BUTTON_NOACTION
                                        && res == ButtonsFactory.BUTTON_OK) {
                                    attr_name = (String) attr_list.getSelectedValue();
                                } else if (dlg.getResult() == ButtonsFactory.BUTTON_CLEAR) {
                                    value = null;
                                    editor.stopCellEditing();
                                    return;
                                } else{
                                    editor.cancelCellEditing();
                                    return;
                                }
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }else{
                try {
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
                                attr_name=attr.name;
                            }
                    } else if (dlg.getResult() == ButtonsFactory.BUTTON_CLEAR) {
                        value = null;
                        editor.stopCellEditing();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (cls != null && cls.id>99) {
                ObjectList oList = new ObjectList(cls, attr_name);
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
                        "Выберите объект", sp,true);
                dlg.show();
                int res = dlg.getResult();
                if (res != ButtonsFactory.BUTTON_NOACTION
                        && res == ButtonsFactory.BUTTON_OK) {
                    KrnObject[] objs = oList.getSelectedObjects();
                    String[] titles = oList.getSelectedTitles();
                    if (objs != null && objs.length>0) {
                        Vector<KrnObjectItem> value_=new Vector<KrnObjectItem>();
                        for(int i=0;i<objs.length;i++){
                            value_.add(new KrnObjectItem(objs[i],titles[i]));
                        }
                        value=value_;
                    } else {
                        value = null;
                    }
                    editor.stopCellEditing();
                } else if (dlg.getResult() == ButtonsFactory.BUTTON_CLEAR) {
                    value = null;
                    editor.stopCellEditing();
                }
            }
            } catch (KrnException e1) {
                e1.printStackTrace();
            }
        } else if (e.getSource() == exprBtn ) {
            String value_="";
            if(value instanceof Expression){
               value_=((Expression)value).text;
            }else if(value!=null){
                value_=value.toString();
            }
            ExpressionEditor exprEditor = new ExpressionEditor(value_, KrnOrExprEditorDelegate.this);
            DesignerDialog dlg = new DesignerDialog(Or3Frame.instance(), "Выражение", exprEditor);
            Dimension dim =new Dimension(Utils.getScreenSize(Or3Frame.instance()));
            dlg.setSize(new Dimension(kz.tamur.comps.Utils.getMaxWindowSizeActDisplay()));
            dlg.setLocation(kz.tamur.comps.Utils.getCenterLocationPoint(dlg.getSize()));
            dlg.show();
            if (dlg.isOK()) {
            	setExpression(exprEditor.getExpression());
            } else if (dlg.getResult() == ButtonsFactory.BUTTON_CANCEL) {
                editor.cancelCellEditing();
            }
        } else if (e.getSource() == label ) {
            value = new Expression(label.getText());
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
    
    public void setExpression(String expression) {
   	 	value = new Expression(expression);
        editor.stopCellEditing();
   }
}
