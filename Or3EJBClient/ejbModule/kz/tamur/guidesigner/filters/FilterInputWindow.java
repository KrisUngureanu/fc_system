package kz.tamur.guidesigner.filters;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

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

public class FilterInputWindow extends JFrame implements ActionListener {
    JFrame ownerFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
    JButton prodolzhit ;
    JLabel errorMsg  = new JLabel();
    List<Element> nodesWithParams = new ArrayList<Element>();
    List<String> paramNames = new ArrayList<String>();
    List<Object> nonStringValues = new ArrayList<Object>();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    GridBagConstraints c = new GridBagConstraints();
    Element xml;
    JPanel inputPanel;
    KrnObject lastSelectFlt;
    JTextArea filterResultsArea;
    JScrollPane scrollPane;;
    JSplitPane rightSplitPane;
    //JFrame frame;

   
    public FilterInputWindow( Element xml, KrnObject lastSelectFlt)
    { 
        this.xml = xml;
        this.lastSelectFlt = lastSelectFlt;
        init();
        populateInputPanel();
    }

    public void init()
    {
      // frame  = new JFrame();
        filterResultsArea = new JTextArea();
        scrollPane = new JScrollPane(filterResultsArea);
        inputPanel = new JPanel();
        rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        rightSplitPane.setDividerLocation(50);
        rightSplitPane.setTopComponent(scrollPane);
        rightSplitPane.setBottomComponent(inputPanel);
        inputPanel.setLayout(new GridBagLayout());
        c.fill = GridBagConstraints.HORIZONTAL;
        prodolzhit = new JButton("Продолжить");
        prodolzhit.addActionListener(this);
        setContentPane(rightSplitPane);
       // frame.getContentPane().add(rightSplitPane, BorderLayout.CENTER);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setDefaultLookAndFeelDecorated(true);
       setLocationRelativeTo(this);
       pack();
       setSize(500, 800);
       
      setVisible(true);
    }
    private void listNodesWithParams(Element component)
    {
        Element children = component.getChild("children");

        if(children==null||children.getValue().equals(""))
        {
            Element compFlr = component.getChild("compFlr");
            if(compFlr!=null&& compFlr.getValue().equals("1"))
            {
                String exprFlr = component.getChild("valFlr").getChild("exprFlr").getValue();
                if(exprFlr.startsWith("%"))
                {
                    nodesWithParams.add(component);
                    paramNames.add(exprFlr);
                }
            }
        }
        else {

            List<Element> childComponents = children.getChildren("Component");
            for(Element childComponent : childComponents)
            {listNodesWithParams(childComponent);}
        }
    }
    private void populateInputPanel()
    {
        nonStringValues.clear();

        listNodesWithParams(xml);    

        int y = 0;
        for(Element component: nodesWithParams)
        {
            createInputRow(component, y);
            y++;
        }
        inputPanel.add(prodolzhit);

    }

    private void createInputRow(Element component, int y)
    {
        String paramName = component.getChild("valFlr").getChild("exprFlr").getValue();
        JLabel label = new JLabel(paramName);
        c.gridx = 0;
        c.gridy = y;
        inputPanel.add(label, c);

        JButton button = kz.tamur.comps.Utils.createBtnEditorIfc(this);
        button.setVisible(false);
        c.gridx = 1;
        c.gridy = y;
        inputPanel.add(button, c);
         JTextField field = null;
        String path = component.getChild("attrFlr").getValue();
        final KrnClass cls = getClassFromAttrPath(path);
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
                field = new JTextField(15);
                inputPanel.add(field, c);
            }
        }
        final InputField inputField = new InputField(field);
        
        final String s;
        if (cls!=null && cls.id>99) {
            button.setVisible(true);
            
            button.addActionListener(new ActionListener() {
                Kernel krn = Kernel.instance();
                String attr_name = null;

                public void actionPerformed(ActionEvent e)
                {
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
                        
                        DesignerDialog dlg = new DesignerDialog(ownerFrame,
                                "Выберите атрибут для отображения объектов", scroller,true);
                        dlg.show();
                        int res = dlg.getResult();
                        if (res != ButtonsFactory.BUTTON_NOACTION
                                && res == ButtonsFactory.BUTTON_OK) {
                            attr_name = (String) attr_list.getSelectedValue();
                      String  selectedObjects = objectsDialog(cls,attr_name);
                      inputField.setText(selectedObjects);
                        } else if (dlg.getResult() == ButtonsFactory.BUTTON_CLEAR) {
                            return;
                        } else{
                            return;
                        }
                    }
                }
            });  
            
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
    private String objectsDialog(KrnClass cls,String attr_name)
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
               ownerFrame,
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
            } 
        }
        return label_;
    }
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
    private void changefilterResultsArea(KrnObject filterObj, List<Object> paramsToSubmit)
    {
        Kernel krn  = Kernel.instance();
        int i = 0;
        List paramList = new ArrayList();
        for(String paramName: paramNames)
        {
            try {
                Object paramValue = paramsToSubmit.get(i);
                krn.setFilterParam(filterObj.uid, paramName, Collections.singletonList(paramValue));
            } catch (KrnException e) {
            }
            i++;
        }
        KrnObject[] krnObjects = null;
        try {
            krnObjects =  krn.getFilteredObjects(filterObj, 0, -1);
            filterResultsArea.append("Результатов: "+krnObjects.length+ " \n");
            for(KrnObject obj: krnObjects)
            {
                filterResultsArea.append(obj.toString()+"\n");
            }
        } catch (KrnException e) {
            e.printStackTrace();
        }

        paramsToSubmit.clear();
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if(src == prodolzhit)
        {
            List<Object> paramsToSubmit = new ArrayList<Object>();
                filterResultsArea.setText("");
                int nonStringCount = 0;
                int count = 2;
                int i = 0;
                while(i<3*paramNames.size()){
                    if(i==count){
                        if(inputPanel.getComponent(i-1).isVisible()==false) {
                            Component field = inputPanel.getComponent(i);
                            if(field instanceof JFormattedTextField)
                            {
                                String dateValue =((JFormattedTextField)field).getText();

                                Date date = null;
                                try {if(!dateValue.equals("")) {
                                    date = dateFormat.parse(dateValue); }
                                } catch (ParseException e1) {
                                    e1.printStackTrace();
                                }
                                paramsToSubmit.add(date);
                            }
                            else
                            {
                                paramsToSubmit.add(((JTextField)field).getText());
                            }
                            count+=3;
                        }
                        else
                        {
                            if(nonStringCount<nonStringValues.size()) {
                                Object obj =  nonStringValues.get(nonStringCount);
                                nonStringCount++;
                                paramsToSubmit.add(obj);}
                            else {
                                paramsToSubmit.add(null);
                            }
                            count+=3;
                        }

                    }
                    i++;  }

                if(i==3*paramNames.size()){
                    changefilterResultsArea(lastSelectFlt,paramsToSubmit);
                    paramNames.clear();
                }
            }
        }


    
        }
        
    
