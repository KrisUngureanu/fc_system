package kz.tamur.or3.client.props.inspector;

import com.cifs.or2.client.util.CnrBuilder;
import com.cifs.or2.client.ClassNode;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;

import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.guidesigner.filters.FilterItem;
import kz.tamur.or3.client.props.Inspectable;
import kz.tamur.or3.client.props.Property;
import kz.tamur.rt.Utils;
import kz.tamur.Or3Frame;
import kz.tamur.admin.ClassBrowser;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.StringTokenizer;

public class RefEditorDelegate extends JPanel implements EditorDelegate, RendererDelegate, ActionListener {

	private String value;
    private PropertyEditor editor;
    private String defaultClass = "";
    private String lastPath;

	private JTextField label;
	private JButton refBtn;

    public RefEditorDelegate(JTable table) {
        setLayout(new GridBagLayout());
        label = kz.tamur.comps.Utils.createEditor(this,table.getFont());
        refBtn = kz.tamur.comps.Utils.createBtnEditor(this);
        add(label, new CnrBuilder().x(1).wtx(1).fill(GridBagConstraints.HORIZONTAL).build());
        add(refBtn, new CnrBuilder().x(0).build());
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
		this.value = (String)value;
		if (value != null) {
			label.setText((String)value);
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
        if (e.getSource() == refBtn) {
            try {
            	if("".equals(defaultClass) && (value==null || "".equals(value))){
	            	Inspectable inode=(Inspectable)editor.getObject();
	            	Property dp;
	            	if(inode instanceof FilterItem)
		            	dp=inode.getProperties().getChild("attrFlr");
	            	else
		            	dp=inode.getProperties().getChild("ref").getChild("data");
	            	String path=(String)inode.getValue(dp);
                    if (path != null && !path.equals("")) {
	                    StringTokenizer st = new StringTokenizer(path, ".");
	                    defaultClass = st.nextToken();
                    }
            	}
            	Property prop=((PropertyTable)editor.getTable()).getCurentProperty();
                ClassBrowser cb = getClassBrowser(value);
                DesignerDialog dlg =
                        new DesignerDialog(Or3Frame.instance(),
                                "Выберите путь", cb);
                dlg.show();
                int res = dlg.getResult();
                if (res != ButtonsFactory.BUTTON_NOACTION
                        && res == ButtonsFactory.BUTTON_OK) {
                    String path = cb.getSelectedPath();
                    if (path != null && !path.equals("")) {
                        StringTokenizer st = new StringTokenizer(path, ".");
                        defaultClass = st.nextToken();
                    } else
                        defaultClass = "";
                    long typeClass=prop.getNode()==null?-1:prop.getNode().getTypeValue();
                    if(typeClass>0){
                    	KrnAttribute[] attrs=Utils.getAttributesForPath(path);
                    	if(attrs==null || (attrs.length>0 && attrs[attrs.length-1].typeClassId!=prop.getNode().getTypeValue())){
                    		String sattr="";
                    		if(typeClass==Kernel.IC_INTEGER)
                    			sattr=" числовым ";
                    		else if(typeClass==Kernel.IC_STRING)
                    			sattr=" строковым ";
                            MessagesFactory.showMessageDialog(Or3Frame.instance(),
                                    MessagesFactory.INFORMATION_MESSAGE, "\"" + path +
                                    "\" - ошибочное значение атрибута, он должен быть "+sattr+"!");
                    		
                            String selPath = cb.getSelectedPath();
                            if (selPath != null && !"".equals(selPath)) {
                                lastPath = selPath;
                            }
                            editor.cancelCellEditing();
                            return;
                    	}
                    		
                    }
                    value = path;
                    lastPath = path;
                    editor.stopCellEditing();
                } else {
                    String selPath = cb.getSelectedPath();
                    if (selPath != null && !"".equals(selPath)) {
                        lastPath = selPath;
                    }
                    editor.cancelCellEditing();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }else if(e.getSource() == label){
            value=label.getText();
            editor.stopCellEditing();
        }
    }
    private ClassBrowser getClassBrowser(String path) {
        final Kernel krn = Kernel.instance();
        ClassNode cls = null;
        String s = "";
        try {
            if ("".equals(value)) {
                if ("".equals(defaultClass)) {
                    cls = krn.getClassNodeByName("Объект");
                } else {
                    cls = krn.getClassNodeByName(defaultClass);
                }
            } else {
                try {
                    s = getClassNameFromPath(path);
                    // удалить доп. данные в круглых скобках
                    s = s.replaceAll("\\(.*?\\)", "");
                    cls = krn.getClassNodeByName(s);
                    if(cls==null){
                        cls = krn.getClassNodeByName("Объект");
                        path="";
                    	defaultClass = "";
                    }else
                    	defaultClass = s;
                } catch (KrnException e) {
                    MessagesFactory.showMessageDialog(Or3Frame.instance(),
                            MessagesFactory.ERROR_MESSAGE, "\"" + s +
                            "\" - ошибочное имя класса!");
                }
            }
            ClassBrowser classBrowser = new ClassBrowser(cls, true);
            Dimension cbDim = kz.tamur.comps.Utils.getMaxWindowSize();
            classBrowser.setPreferredSize(new Dimension(cbDim.width*3/5,cbDim.height*3/5));
            if (lastPath != null && !"".equals(lastPath) && "".equals(path)) {
                classBrowser.setSelectedPath(lastPath);
            } else if (path != null && !"".equals(path)) {
            	String path_=path;
            	while(true){
	            	try{
	            		classBrowser.setSelectedPath(path_);
	            		break;
	            	}catch(Exception ex){
	            		if(path_.lastIndexOf(".")<=0)
	            			break;
	            		path_=path_.substring(0,path_.lastIndexOf("."));
	            	}
            	}
            	if(!path.equals(path_))
                    MessagesFactory.showMessageDialog(Or3Frame.instance(), MessagesFactory.INFORMATION_MESSAGE,
                            "Ошибка в пути: \n"+path);
                lastPath = path_;
            }
            return classBrowser;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    private String getClassNameFromPath(String path) {
        StringTokenizer st = new StringTokenizer(path, ".");
        return st.nextToken();
    }

}
