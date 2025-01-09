package kz.tamur.guidesigner.filters;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.util.KrnObjectItem;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import kz.tamur.comps.Constants;
import kz.tamur.comps.PropertyValue;
import kz.tamur.or3.client.props.*;
import kz.tamur.or3.client.props.inspector.MemoProperty;
import kz.tamur.or3.client.props.inspector.RefProperty;

import java.util.StringTokenizer;
import java.util.Vector;

public class FilterItem implements Inspectable {
    private static Property proot;
    private Object item;
    private FiltersPanel fp;
    public FilterItem(Object item,FiltersPanel owner){
       this.item = item;
        this.fp=owner;
    }

    public Property getProperties() {
        proot = new FolderProperty(null, null, "Элементы");
        if (item != null) {
            if (item instanceof OrFilterNode) {
                new StringProperty(proot, "title", "Заголовок");
                new CheckProperty(proot, "inversFlr", "Обратный фильтр");
                ComboProperty joinCondition = new ComboProperty(proot, "unionFlr", "Условие объединения узлов");
                joinCondition.addItem("" + Constants.UNION_OR, "по или").addItem("" + Constants.UNION_AND, "по и");
                new RefProperty(proot, "attrFlr", "Фильтруемый атрибут");
                new TreeOrExprProperty(proot,"linkFlr", "Ссылка на фильтр","Filter");
                ComboProperty oper = new ComboProperty(proot, "operFlr", "Отношение");
                oper.addItem("" + Constants.OPER_ZERO, "").addItem("" + Constants.OPER_EQ, Constants.OP_EQ)
                        .addItem("" + Constants.OPER_NEQ, Constants.OP_NEQ).addItem("" + Constants.OPER_GT, Constants.OP_GT)
                        .addItem("" + Constants.OPER_LT, Constants.OP_LT).addItem("" + Constants.OPER_GEQ, Constants.OP_GEQ)
                        .addItem("" + Constants.OPER_LEQ, Constants.OP_LEQ)
                        .addItem("" + Constants.OPER_EXIST, Constants.OP_EXIST)
                        .addItem("" + Constants.OPER_NOT_EXIST, Constants.OP_NOT_EXIST)
                        .addItem("" + Constants.OPER_INCLUDE, Constants.OP_INCLUDE)
                        .addItem("" + Constants.OPER_EXCLUDE, Constants.OP_EXCLUDE)
                        .addItem("" + Constants.OPER_CONTAIN, Constants.OP_CONTAIN)
                        .addItem("" + Constants.OPER_NOT_CONTAIN, Constants.OP_NOT_CONTAIN)
                        .addItem("" + Constants.OPER_START_WITH, Constants.OP_START_WITH)
                        .addItem("" + Constants.OPER_ANOTHER, Constants.OP_ANOTHER)
                        .addItem("" + Constants.OPER_ASCEND, Constants.OP_ASCEND)
                        .addItem("" + Constants.OPER_DESCEND, Constants.OP_DESCEND)
                        .addItem("" + Constants.OPER_FINISH_ON, Constants.OP_FINISH_ON);
                ComboProperty rightType = new ComboProperty(proot, "compFlr", "Тип правой части отношения");
                rightType.addItem("" + Constants.COMPARE_VALUE, "значение атрибута")
                        .addItem("" + Constants.COMPARE_FUNC, "функция").addItem("" + Constants.COMPARE_ATTR, "атрибут")
                        .addItem("" + Constants.SQL_PAR, "параметр");
                new KrnOrExprProperty(proot, "valFlr", "Правая часть отношения");
                new StringProperty(proot, "linkPar", "Зависимый параметр");
                ComboProperty likeEscape = new ComboProperty(proot, "likeEscape","Символы как литералы");
                likeEscape.addItem(""+Constants.ESCAPE_NOT, "отсутствует")
                .addItem(""+Constants.ESCAPE_MANUAL, "вручную")
                .addItem(""+Constants.ESCAPE_DEFAULT, "по умолчанию");
                new KrnObjectProperty(proot, "language", "Язык", "Language", "code");
                new CheckProperty(proot, "independFlr", "Самостоятельный узел");
                new CheckProperty(proot, "maxIndFlr", "Максимальный индекс");
                new CheckProperty(proot, "relativeFlr", "Относительно");
                new CheckProperty(proot, "groupFlr", "Включить в группировку");
                ComboProperty grpFunc = new ComboProperty(proot, "grpFuncFlr", "Аггрегирующая функция");
                grpFunc.addItem("" + Constants.GROUP_ZERO, "").addItem("" + Constants.GROUP_COUNT, Constants.GR_COUNT)
                        .addItem("" + Constants.GROUP_SUM, Constants.GR_SUM).addItem("" + Constants.GROUP_MAX, Constants.GR_MAX)
                        .addItem("" + Constants.GROUP_MIN, Constants.GR_MIN).addItem("" + Constants.GROUP_AVG, Constants.GR_AVG);
                ComboProperty kolOper = new ComboProperty(proot, "kolOperFlr", "Условие на количество объектов");
                kolOper.addItem("" + Constants.OPER_ZERO, "").addItem("" + Constants.OPER_EQ, Constants.OP_EQ)
                        .addItem("" + Constants.OPER_NEQ, Constants.OP_NEQ).addItem("" + Constants.OPER_GT, Constants.OP_GT)
                        .addItem("" + Constants.OPER_LT, Constants.OP_LT).addItem("" + Constants.OPER_GEQ, Constants.OP_GEQ)
                        .addItem("" + Constants.OPER_LEQ, Constants.OP_LEQ);
                new StringProperty(proot, "kolObjFlr", "Количество объектов");
                ComboProperty transFlr = new ComboProperty(proot, "transFlr", "Учет транзакций");
                transFlr.addItem("" + Constants.TRANS_ZERO, "нулевая").addItem("" + Constants.TRANS_ALL, "все")
                        .addItem("" + Constants.TRANS_CUR, "текущая");
                new CheckProperty(proot, "maxTrFlr", "Максимальная транзакция");
                new CheckProperty(proot, "mandatoryFlr", "Обязательность");
                new CheckProperty(proot, "excludeFlr", "Отключить условие");
                new CheckProperty(proot, "joinCls", "Присоединенный класс");
                new RefProperty(proot, "attrParent", "Родительский атрибут");
                ComboProperty operJoin = new ComboProperty(proot, "operJoin", "Условие сравнения");
                operJoin.addItem("" + Constants.OPER_ZERO, "").addItem("" + Constants.OPER_EQ, Constants.OP_EQ)
                        .addItem("" + Constants.OPER_NEQ, Constants.OP_NEQ).addItem("" + Constants.OPER_GT, Constants.OP_GT)
                        .addItem("" + Constants.OPER_LT, Constants.OP_LT).addItem("" + Constants.OPER_GEQ, Constants.OP_GEQ)
                        .addItem("" + Constants.OPER_LEQ, Constants.OP_LEQ);
                new RefProperty(proot, "attrChild", "Дочерний атрибут");
                new MemoProperty(proot, "comment", "Комментарий");
                new CheckProperty(proot, "respReg", "Нечувствительность к регистру");
             // Если это папка
                if (!((OrFilterNode) item).isLeafAlternative()) {
                    new CheckProperty(proot, "isNodeMenu", "Использовать как пункт меню");
                }
            }
        }
        return proot;
    }

    public Object getValue(Property prop) {
        Object res="";
        if(item!=null && !(prop instanceof FolderProperty)){
            if(item instanceof OrFilterNode){
                PropertyValue value=((OrFilterNode)item).getPropertyValue(prop.getId());
                if(!value.isNull() ){
                    if(value.getKrnClassName()!=null && !"".equals(value.getKrnClassName())
                            && value.getKrnObjectId()!=null && !"".equals(value.getKrnObjectId())){
                        try {
                            StringTokenizer st_ids=new StringTokenizer(value.getKrnObjectId(),",");
                            StringTokenizer st_titles=new StringTokenizer(value.getTitle(),",");
                            Vector<KrnObjectItem> objs=new Vector<KrnObjectItem>();
                            while(st_ids.hasMoreTokens()){
                                String id_=st_ids.nextToken();
                                String title_="";
                                if(st_titles.hasMoreTokens()){
                                    title_=st_titles.nextToken();
                                }
                                KrnObject[] krn_obj= Kernel.instance().getObjectsByIds(new long[]{Long.valueOf(id_)},-1);
                                if(krn_obj.length>0){
                                    objs.add(new KrnObjectItem(krn_obj[0],title_));
                                }
                            }
                            res=objs;
                        } catch (KrnException e) {
                            e.printStackTrace();
                        }
                    }else
                        res=value.objectValue();
                }
            }
            if(res==null) res="";
            if(prop instanceof ComboProperty){
                res=((ComboProperty)prop).getItem(res!=null?res.toString():"");
            }else if(prop instanceof ExprProperty
                    ||(prop instanceof KrnOrExprProperty && res instanceof String)
                    ||(prop instanceof TreeOrExprProperty && res instanceof String)){
                res= new Expression((String)res);
            }
        }
        return res;
    }

    public void setValue(Property prop, Object value) {
        if(item!=null && item instanceof OrFilterNode && !(prop instanceof FolderProperty)){
            if(prop instanceof ComboProperty){
                value=((ComboPropertyItem)value).id;
            }
            if(item instanceof OrFilterNode){
                ((OrFilterNode)item).setPropertyValue(prop.getId(),value);
            }
            fp.setFilterModified((OrFilterNode)item);}
    }
    
	public void setValue(Property prop, Object value, Object oldValue) {
		setValue(prop, value);
	}

	public String getTitle(){
        String title="";
        if(item instanceof OrFilterNode){
            title=" - "+ ((OrFilterNode)item).getFilterNode().toString()+":" +item.toString();
        }
        return "Фильтры"+title;
    }

    @Override
    public Property getNewProperties() {
        return null;
    }
}
