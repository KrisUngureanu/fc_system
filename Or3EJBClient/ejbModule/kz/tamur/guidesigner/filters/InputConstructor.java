package kz.tamur.guidesigner.filters;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import kz.tamur.Or3Frame;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.rt.Utils;
import kz.tamur.util.ObjectList;

import org.jdom.Element;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.util.KrnObjectItem;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;

public class InputConstructor implements ActionListener{
    JButton button = kz.tamur.comps.Utils.createBtnEditorIfc(this);
    KrnClass cls;
    JTextField field;
    List<Object> nonStringValues = new ArrayList<Object>();
    public InputConstructor (JPanel inputPanel, Element component, int y) {
        button.setVisible(false);
        
        GridBagConstraints c = new GridBagConstraints();
        JLabel errorMsg = new JLabel();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
       
            
            String paramName = component.getChild("valFlr").getChild("exprFlr").getValue();
            JLabel label = new JLabel(paramName);
            c.gridx = 0;
            c.gridy = y;
            inputPanel.add(label, c);

          
           
            c.gridx = 1;
            c.gridy = y;
            inputPanel.add(button, c);
           
            String path = component.getChild("attrFlr").getValue();
           cls= getClassFromAttrPath(path);
            c.gridx = 2;
            c.gridy = y;
            if(cls!=null) {
                if(cls.getName().equals( "date"))
                {

                     field  = new JFormattedTextField(dateFormat);
                    field.setColumns(10);
                    inputPanel.add(field, c);
                }
                else {
                    field = new JTextField();
                    inputPanel.add(field, c);
                }
            }
    final String s;
            if (cls!=null && cls.id>99) {
                button.setVisible(true);
            }
            else if(cls == null)
            {
                errorMsg.setText("Неправильно задан путь к фильтруемому аттрибуту");
                errorMsg.setVisible(true);
                c.gridx = 2;
                c.gridy = 0;
                inputPanel.add(errorMsg,c);
            }
        
    }
   
    @Override
    public void actionPerformed(ActionEvent e) {
       if(e.getSource()==button)
       {                Kernel krn = Kernel.instance();
       String attr_name = null;

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
             String  selectedObjects = objectsDialog(cls,attr_name,field);
             
               } else if (dlg.getResult() == ButtonsFactory.BUTTON_CLEAR) {
                   return;
               } else{
                   return;
               }
           }
       }}
    private KrnClass getClassFromAttrPath(String path)
    {
        KrnClass cls = null;
        KrnAttribute attr=null;
        Kernel krn = Kernel.instance();

        try {
            KrnAttribute[] attr_a=Utils.getAttributesForPath(path);
            if(attr_a!=null && attr_a.length>0)
                cls=krn.getClass((attr=attr_a[attr_a.length-1]).typeClassId);
            else if(path.indexOf(".")<0)
                cls=krn.getClassByName(path);

        } catch (KrnException e1) {

            return null;
        }
        return cls;
    }
    private String objectsDialog(KrnClass cls,String attr_name,JTextField field)
    {
        Object value = null;
        ObjectList oList = null;
        try {
            oList = new ObjectList(cls, attr_name);
        } catch (KrnException exception) {
            exception.printStackTrace();
        }
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
        String label_="";
        DesignerDialog dlg = new DesignerDialog(
                Or3Frame.instance(),
                "Выберите объект", sp,true);
        dlg.show();
        int res = dlg.getResult();
        if (res != ButtonsFactory.BUTTON_NOACTION
                && res == ButtonsFactory.BUTTON_OK) {
            KrnObject[] objs = oList.getSelectedObjects();


            if (objs != null && objs.length>0) {
                if(objs.length==1)
                {
                    nonStringValues.add(objs[0]);
                }
                else {
                    List<KrnObject> objsToList = Arrays.asList(objs);
                    nonStringValues.add(objsToList);
                }

                String[] titles = oList.getSelectedTitles();
                Vector<KrnObjectItem> value_=new Vector<KrnObjectItem>();
                for(int i=0;i<objs.length;i++){

                    value_.add(new KrnObjectItem(objs[i],titles[i]));
                }
               
                for(int i=0;i<((Vector)value_).size();i++){
                    label_ += (i>0?",":"")+ ((KrnObjectItem)((Vector)value_).get(i)).title;
                }
                field.setText(label_);
                //  value=value_;
            } /*else {
                value = null;
            }*/
        } /*else if (dlg.getResult() == ButtonsFactory.BUTTON_CLEAR) {
            value = null;
        }*/
        return label_;
    }

}
