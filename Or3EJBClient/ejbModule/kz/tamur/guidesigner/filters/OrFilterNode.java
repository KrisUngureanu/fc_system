package kz.tamur.guidesigner.filters;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.util.KrnObjectItem;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;

import kz.tamur.Or3Frame;
import kz.tamur.comps.*;
import kz.tamur.comps.models.FilterNodePropertyRoot;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.or3.client.props.Expression;
import kz.tamur.rt.adapters.ComponentAdapter;
import kz.tamur.util.MapMap;
import org.jdom.Element;

import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.List;
import java.util.Vector;

/**
 * User: vital
 * Date: 06.12.2004
 * Time: 9:36:24
 */
public class OrFilterNode extends DefaultMutableTreeNode implements OrGuiComponent {
    
    protected String UUID;
    public static PropertyNode PROPS = new FilterNodePropertyRoot();
    private Element xml;
    private boolean isSelected;
    private long langId;
    private String title = "Не назначен";
    private String oldTitle;
    private boolean union;
    private OrFrame frame;
    private static KrnClass filterCls;   
    
    static {
        if (filterCls == null) {
            try {
                filterCls = Kernel.instance().getClassByName("Filter");
            } catch (KrnException e) {
                e.printStackTrace();
            }
        }
    }
    public OrFilterNode(Element xml, int langId, Factory fm, OrFrame frame) throws KrnException {
        this.xml = xml;
        this.langId = langId;
        this.frame = frame;
        UUID = PropertyHelper.getUUID(this);
        PropertyValue pv = getPropertyValue(PROPS.getChild("children"));
        if (!pv.isNull()) {
            java.util.List childrenEl = pv.elementValue().getChildren();
            for (int i = 0; i < childrenEl.size(); i++) {
                Element child = (Element) childrenEl.get(i);
                OrGuiComponent c = fm.create(child, Mode.PREVIEW, frame);
                addChild((OrFilterNode)c, true);
            }
        }
    }

    public void setNodeTitle(OrFilterNode parent){

        String pvl_s="";
        PropertyValue pvt = getPropertyValue(PROPS.getChild("title"));
        PropertyValue pvu = getPropertyValue(PROPS.getChild("unionFlr"));
        PropertyValue pvl = getPropertyValue(PROPS.getChild("attrFlr"));
        PropertyValue pvlnk = getPropertyValue(PROPS.getChild("linkFlr"));
        PropertyValue pvo = getPropertyValue(PROPS.getChild("operFlr"));
        PropertyValue pvro = getPropertyValue(PROPS.getChild("valFlr").getChild("krnObjFlr"));
        PropertyValue pvre = getPropertyValue(PROPS.getChild("valFlr").getChild("exprFlr"));
//        PropertyValue pvra = getPropertyValue(PROPS.getChild("valFlr").getChild("compAttrFlr"));
        if(!pvu.isNull())
        setModeIcon(pvu.stringValue());
        if(!pvl.isNull() && !pvl.stringValue().equals("")){
            pvl_s= pvl.stringValue();
            if(parent!=null){
                PropertyValue pvl_p = parent.getPropertyValue(parent.getProperties().getChild("attrFlr"));
                if(!pvl_p.isNull() && !pvl_p.stringValue().equals("")){
                    int pos=pvl_s.length()>pvl_p.stringValue().length()?pvl_p.stringValue().length()+1:0;
                    if(pvl_s.length()==pvl_p.stringValue().length()){
                         pvl_s=pvt.stringValue();
                    }else{
                         pvl_s=pvl_s.substring(pos);
                    }
                }
            }
            String pvr_s=(!pvro.isNull()?pvro.stringValue():"")+(!pvre.isNull()?pvre.stringValue():"");
            title=/*(isRoot()?pvt.stringValue()+":":"")+*/pvl_s+(!pvo.isNull()?" "+pvo.getProperty().getEnumValues()[pvo.intValue()]+" ":"")+pvr_s;
            PropertyValue pvjcls = getPropertyValue(PROPS.getChild("joinCls"));
            String pvj_s="";
            if(!pvjcls.isNull() && "true".equals(pvjcls.stringValue())){
                PropertyValue pvjp = getPropertyValue(PROPS.getChild("attrParent"));
                String pvjp_s=  pvjp.isNull()?"":pvjp.stringValue();
                PropertyValue pvjop = getPropertyValue(PROPS.getChild("operJoin"));
                String pvjop_s=  pvjop.isNull()?"":""+pvjop.getProperty().getEnumValues()[pvjop.intValue()];
                PropertyValue pvjc = getPropertyValue(PROPS.getChild("attrChild"));
                String pvjc_s=  pvjc.isNull()?"":pvjc.stringValue();
                pvj_s=":("+(!pvjp_s.equals("") && pvjp_s.indexOf(".")>0?pvjp_s.substring(pvjp_s.indexOf(".")+1):"")
                        +pvjop_s
                        +(!pvjc_s.equals("") && pvjc_s.indexOf(".")>0?pvjc_s.substring(pvjc_s.indexOf(".")+1):"")
                        +")";
                title+=pvj_s;
            }
            PropertyValue pvlp = getPropertyValue(PROPS.getChild("linkPar"));
            if(!pvlp.isNull()&& !"".equals(pvlp.stringValue())){
                title+="("+pvlp.stringValue()+")";
            }
            if (!pvlnk.isNull()) 
            title += "<-"+pvlnk.stringValue();
        }else if (!pvt.isNull()) {
            title = pvt.stringValue();
        }
        for(int i=0;i<getChildCount();++i){
            ((OrFilterNode)this.getChildAt(i)).setNodeTitle(this);
        }
        if(parent==null){
            oldTitle= pvt.stringValue();
            setEnable();
        }
    }
    public void insertChild(OrFilterNode node,int index) {
        PropertyHelper.insertProperty(new PropertyValue(node.getXml(), PROPS.getChild("children"),langId),index, xml);
        insert(node,index);
    }
    public void addChild(OrFilterNode node, boolean isLoaded) {
        if (!isLoaded) {
            PropertyHelper.addProperty(
                        new PropertyValue(node.getXml(), PROPS.getChild("children"),langId), xml);
        }
        add(node);
    }

    public void removeFilterNode(OrFilterNode child) {
        remove(child);
        PropertyHelper.removeProperty(
                new PropertyValue(child.getXml(), PROPS.getChild("children")), xml);
        
    }

    public String toString() {
        return title;
    }
    public String getTitle(){
        PropertyValue pvt = getPropertyValue(PROPS.getChild("title"));
        return pvt.isNull()?"Unnamed":pvt.stringValue();

    }
    public String getOldTitle(){
        return oldTitle;
    }
     public void setOldTitle(){
         PropertyValue pvt = getPropertyValue(PROPS.getChild("title"));
         oldTitle=pvt.isNull()?"":pvt.stringValue();
     }

    public void setEnable(){
        PropertyValue pvl = getPropertyValue(PROPS.getChild("attrFlr"));
        PROPS.getChild("valFlr").getChild("krnObjFlr").setTypeFlr(new Integer(1));
        PROPS.getChild("valFlr").getChild("exprFlr").setTypeFlr(new Integer(1));
        PROPS.getChild("language").setTypeFlr(new Integer(1));
//        PROPS.getChild("valFlr").getChild("compAttrFlr").setTypeFlr(new Integer(1));
        try{
         if(!pvl.isNull() && !pvl.stringValue().equals("")){
             PropertyValue pvt = getPropertyValue(PROPS.getChild("compFlr"));
             KrnAttribute[] attr=null;
             try{
              attr= kz.tamur.rt.Utils.getAttributesForPath(pvl.stringValue());
             }catch(Throwable te){
            	 te.printStackTrace();
             }
             if(attr!=null && attr.length>0){
                 if(attr[attr.length-1].typeClassId!=Kernel.IC_STRING
                 && attr[attr.length-1].typeClassId!=Kernel.IC_MEMO
                 && attr[attr.length-1].typeClassId!=Kernel.IC_INTEGER
                 && attr[attr.length-1].typeClassId!=Kernel.IC_FLOAT
                 && attr[attr.length-1].typeClassId!=Kernel.IC_BOOL
                 && attr[attr.length-1].typeClassId!=Kernel.IC_BLOB
                 && attr[attr.length-1].typeClassId!=Kernel.IC_DATE
                 && attr[attr.length-1].typeClassId!=Kernel.IC_TIME){
                    if(pvt.intValue()==Constants.COMPARE_VALUE || pvt.intValue()==Constants.COMPARE_ATTR){
                        KrnClass cls=Kernel.instance().getClassNode(attr[attr.length-1].typeClassId).getKrnClass();
                        PROPS.getChild("valFlr").getChild("krnObjFlr").setTypeFlr(null);
                        PROPS.getChild("valFlr").getChild("krnObjFlr").setKrnClass(cls.name,null);
                        setPropertyValue(new PropertyValue((Object)null,PROPS.getChild("valFlr").getChild("exprFlr")));
//                        setPropertyValue(new PropertyValue((Object)null,PROPS.getChild("valFlr").getChild("compAttrFlr")));
                    }else {
                        PROPS.getChild("valFlr").getChild("exprFlr").setTypeFlr(null);
                        setPropertyValue(new PropertyValue((Object)null,PROPS.getChild("valFlr").getChild("krnObjFlr")));
                    }
//                    if(pvt.intValue()==Constants.COMPARE_ATTR){
//                        PROPS.getChild("valFlr").getChild("compAttrFlr").setTypeFlr(null);
 //                   }else{
 //                       setPropertyValue(new PropertyValue((Object)null,PROPS.getChild("valFlr").getChild("compAttrFlr")));
//                    }
                }else{
                    PROPS.getChild("valFlr").getChild("exprFlr").setTypeFlr(null);
//                    if(pvt.intValue()==Constants.COMPARE_ATTR){
//                        PROPS.getChild("valFlr").getChild("compAttrFlr").setTypeFlr(null);
//                    }else{
//                        setPropertyValue(new PropertyValue((Object)null,PROPS.getChild("valFlr").getChild("compAttrFlr")));
//                    }
                }
                 if(attr[attr.length-1].isMultilingual){
                     PROPS.getChild("language").setTypeFlr(null);
                 }
            }else if(isRoot()){
                 KrnClass cls=Kernel.instance().getClassByName(pvl.stringValue());
				 if(pvt.intValue()==Constants.COMPARE_VALUE || pvt.intValue()==Constants.COMPARE_ATTR){
                 PROPS.getChild("valFlr").getChild("krnObjFlr").setTypeFlr(null);
                 PROPS.getChild("valFlr").getChild("krnObjFlr").setKrnClass(cls!=null?cls.name:pvl.stringValue(),null);
                 setPropertyValue(new PropertyValue((Object)null,PROPS.getChild("valFlr").getChild("exprFlr")));
				 }else{
					 PROPS.getChild("valFlr").getChild("exprFlr").setTypeFlr(null);
					 setPropertyValue(new PropertyValue((Object)null,PROPS.getChild("valFlr").getChild("krnObjFlr")));

				 }
			 }

        }else{
            setPropertyValue(new PropertyValue((Object)null,PROPS.getChild("valFlr").getChild("krnObjFlr")));
            setPropertyValue(new PropertyValue((Object)null,PROPS.getChild("valFlr").getChild("exprFlr")));
//            setPropertyValue(new PropertyValue((Object)null,PROPS.getChild("valFlr").getChild("compAttrFlr")));
        }
      } catch (KrnException ex) {
            ex.printStackTrace();
      }
    }
    public GridBagConstraints getConstraints() {
        return null;
    }

    @Override
    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public PropertyNode getProperties() {
        return PROPS;
    }

    public PropertyValue getPropertyValue(PropertyNode prop) {
        return PropertyHelper.getPropertyValue(prop, xml, frame);
    }
    public PropertyValue getPropertyValue(String propId){
        PropertyValue value;
        if("valFlr".equals(propId)){
            value = getPropertyValue(PROPS.getChild("valFlr").getChild("krnObjFlr"));
            if(value.getKrnClassName()==null || "".equals(value.getKrnClassName()))
                value = getPropertyValue(PROPS.getChild("valFlr").getChild("exprFlr"));
        }else
            value = getPropertyValue(PROPS.getChild(propId));
        return value;
    }
    public void setPropertyValue(PropertyValue value) {
        PropertyHelper.setPropertyValue(value, xml, frame);
        String prop_m= value.getProperty().getName();
        if ("title".equals(prop_m) || "attrFlr".equals(prop_m)
                || "operFlr".equals(prop_m) || "krnObjFlr".equals(prop_m)
                || "exprFlr".equals(prop_m) || "joinCls".equals(prop_m) || "linkFlr".equals(prop_m)
                || "attrParent".equals(prop_m) || "operJoin".equals(prop_m)|| "attrChild".equals(prop_m)) {
            String pvl_s="";
            PropertyValue pvt = getPropertyValue(PROPS.getChild("title"));
            PropertyValue pvl = getPropertyValue(PROPS.getChild("attrFlr"));
            PropertyValue pvlnk = getPropertyValue(PROPS.getChild("linkFlr"));
            PropertyValue pvo = getPropertyValue(PROPS.getChild("operFlr"));
            PropertyValue pvro = getPropertyValue(PROPS.getChild("valFlr").getChild("krnObjFlr"));
            PropertyValue pvre = getPropertyValue(PROPS.getChild("valFlr").getChild("exprFlr"));
//            PropertyValue pvra = getPropertyValue(PROPS.getChild("valFlr").getChild("compAttrFlr"));
            if(!pvl.isNull() && !pvl.stringValue().equals("")){
                pvl_s= pvl.stringValue();
                if(!isRoot()){
                    OrFilterNode parent_=(OrFilterNode)getParent();
                    PropertyValue pvl_p = parent_.getPropertyValue(parent_.getProperties().getChild("attrFlr"));
                    if(!pvl_p.isNull() && !pvl_p.stringValue().equals("")){
                        int pos=pvl_s.length()>pvl_p.stringValue().length()?pvl_p.stringValue().length()+1:0;
                        if(pvl_s.length()==pvl_p.stringValue().length()){
                             pvl_s=pvt.stringValue();
                        }else{
                             pvl_s=pvl_s.substring(pos);
                        }
                    }
                }
                String pvr_s=(!pvro.isNull()?pvro.stringValue():"")+(!pvre.isNull()?pvre.stringValue():"");
                title=/*(isRoot()?pvt.stringValue()+":":"")+*/pvl_s+(!pvo.isNull()?" "+pvo.getProperty().getEnumValues()[pvo.intValue()]+" ":"")+pvr_s;
                PropertyValue pvjcls = getPropertyValue(PROPS.getChild("joinCls"));
                String pvj_s="";
                if(!pvjcls.isNull() && "true".equals(pvjcls.stringValue())){
                    PropertyValue pvjp = getPropertyValue(PROPS.getChild("attrParent"));
                    String pvjp_s=  pvjp.isNull()?"":pvjp.stringValue();
                    PropertyValue pvjop = getPropertyValue(PROPS.getChild("operJoin"));
                    String pvjop_s=  pvjop.isNull()?"":""+pvjop.getProperty().getEnumValues()[pvjop.intValue()];
                    PropertyValue pvjc = getPropertyValue(PROPS.getChild("attrChild"));
                    String pvjc_s=  pvjc.isNull()?"":pvjc.stringValue();
                    pvj_s=":("+(!pvjp_s.equals("") && pvjp_s.indexOf(".")>0?pvjp_s.substring(pvjp_s.indexOf(".")+1):"")
                            +pvjop_s
                            +(!pvjc_s.equals("") && pvjc_s.indexOf(".")>0?pvjc_s.substring(pvjc_s.indexOf(".")+1):"")
                            +")";
                    title+=pvj_s;
                }
                PropertyValue pvlp = getPropertyValue(PROPS.getChild("linkPar"));
                if(!pvlp.isNull()&& !"".equals(pvlp.stringValue())){
                    title+="("+pvlp.stringValue()+")";
                }
                if (!pvlnk.isNull()) 
                	title += "<-"+pvlnk.stringValue();
            }else if (!pvt.isNull()) {
                title = pvt.stringValue();
            }
            if("attrFlr".equals(prop_m)){
                setEnable();
            }
        }else if("compFlr".equals(prop_m)){
              setEnable();
        }else if("unionFlr".equals(prop_m)){
            PropertyValue pvu = getPropertyValue(PROPS.getChild("unionFlr"));
            if(!pvu.isNull()){
               setModeIcon(pvu.stringValue());
            }
            title +="";
        }
    }
    public void setPropertyValue(String propId,Object value){
        if("valFlr".equals(propId)){
            if(value instanceof Vector  && ((Vector)value).size()>0){
                try {
                    String ids_="",titles_="";
                    for(int i=0;i<((Vector)value).size();i++){
                        titles_ += (i>0?",":"")+ ((KrnObjectItem)((Vector)value).get(i)).title;
                        ids_ += (i>0?",":"")+ ((KrnObjectItem)((Vector)value).get(i)).obj.id;
                    }
                    KrnClass cls= Kernel.instance().getClass(((KrnObjectItem)((Vector)value).get(0)).obj.classId);
                    setPropertyValue(new PropertyValue(ids_,cls.name,titles_,PROPS.getChild("valFlr").getChild("krnObjFlr")));
                    setPropertyValue(new PropertyValue((Object)null,PROPS.getChild("valFlr").getChild("exprFlr")));
                } catch (KrnException e) {
                    e.printStackTrace();
                }
            }else if(value instanceof Expression){
                setPropertyValue(new PropertyValue((Object)null,PROPS.getChild("valFlr").getChild("krnObjFlr")));
            	//все переносы строк заменяем на пробелы
                setPropertyValue(new PropertyValue(((Expression)value).text!=null?((Expression)value).text.replaceAll("\n", " "):((Expression)value).text
                		,PROPS.getChild("valFlr").getChild("exprFlr")));
            }else {
                setPropertyValue(new PropertyValue((Object)null,PROPS.getChild("valFlr").getChild("krnObjFlr")));
                setPropertyValue(new PropertyValue((Object)null,PROPS.getChild("valFlr").getChild("exprFlr")));
            }

        }else if(value instanceof Vector  && ((Vector)value).size()>0){
                try {
                    String ids_="",titles_="";
                    for(int i=0;i<((Vector)value).size();i++){
                        titles_ += (i>0?",":"")+ ((KrnObjectItem)((Vector)value).get(i)).title;
                        ids_ += (i>0?",":"")+ ((KrnObjectItem)((Vector)value).get(i)).obj.id;
                    }
                    KrnClass cls= Kernel.instance().getClass(((KrnObjectItem)((Vector)value).get(0)).obj.classId);
                    setPropertyValue(new PropertyValue(ids_,cls.name,titles_,PROPS.getChild(propId)));
                } catch (KrnException e) {
                    e.printStackTrace();
                }
        }else if("linkFlr".equals(propId)){
            try {
                String className="";
                String[] strs = Kernel.instance().getStrings(((KrnObjectItem)value).obj, "className", 0, 0);
                PropertyValue pvAttr=((OrFilterNode)this.getRoot()).getPropertyValue("attrFlr");
                if(!pvAttr.stringValue().equals(strs[0])) {
                    MessagesFactory.showMessageDialog(Or3Frame.instance(),
                            MessagesFactory.INFORMATION_MESSAGE,"Фильтр не подходит, он должен обрабатывать объекты класса:"+pvAttr.stringValue()
                            +"\nВыбранный фильтр обрабатывает объекты класса:"+strs[0]);
                	
                }else
                	setPropertyValue(new PropertyValue(value,PROPS.getChild(propId)));
            } catch (KrnException e) {
                e.printStackTrace();
            }
        }else
            setPropertyValue(new PropertyValue(value,PROPS.getChild(propId)));
    }
    public Element getXml() {
        return xml;
    }

    public int getComponentStatus() {
        return 0;
    }

    public void setLangId(long langId) {
        this.langId = langId;
    }

    public long getLangId() {
        return langId;
    }

    public void setModeIcon(String union_){
         union= (union_!=null && union_.equals("1"));
    }
    public boolean getModeIcon(){
        return union;
    }

    public int getMode() {
        return Mode.DESIGN;
    }

    //For copy process
    public boolean isCopy() {
        return false;
    }

    public void setCopy(boolean copy) {

    }

    public OrGuiContainer getGuiParent() {
        return null;
    }

    public void setGuiParent(OrGuiContainer parent) {

    }

    public void setXml(Element xml) {
        this.xml = xml;
    }

    public Dimension getPrefSize() {
        return null;
    }

    public Dimension getMaxSize() {
        return null;
    }

    public Dimension getMinSize() {
        return null;
    }

    public String getBorderTitleUID() {
        return null;
    }

    public void getStrings(MapMap strings) {

    }

    public void setStrings(MapMap strings) {

    }

    //
    public int getTabIndex() {
        return -1;
    }

    public boolean isSelected() {
        return isSelected;
    }

	public void setEnabled(boolean isEnabled) {
	}

	public boolean isEnabled() {
		return false;
	}

    public byte[] getDescription() {
        return new byte[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

	
	public ComponentAdapter getAdapter() {
		return null;
	}

    public FilterNode getFilterNode() {
        return (FilterNode) frame;
    }

	public String getVarName() {
		return null;
	}
	
    public boolean isLeafAlternative() {
        if (frame instanceof FilterNode) {
            return ((FilterNode) frame).getKrnObj().classId == filterCls.id;
        } else {
            return super.isLeaf();
        }
    }
    public String getUUID() {
        return UUID;
    }

    @Override
    public void setComponentChange(OrGuiComponent comp) {
    }
    
    @Override
    public void setListListeners(java.util.List<OrGuiComponent> listListeners,  java.util.List<OrGuiComponent> listForDel) {
    }  
    
    @Override
    public List<OrGuiComponent> getListListeners() {
        return null;
    }
    
    @Override
    public String getToolTip() {
        return null;
    }

    @Override
    public void updateDynProp() {
    }

    @Override
    public int getPositionOnTopPan() {
        return -1;
    }

    @Override
    public boolean isShowOnTopPan() {
        return false;
    }

    @Override
    public void setAttention(boolean attention) {}
}
