package kz.tamur.or3.client.props.inspector;

import static kz.tamur.comps.models.Types.FILTER;
import static kz.tamur.comps.models.Types.HTML_TEXT;
import static kz.tamur.comps.models.Types.INTEGER;
import static kz.tamur.comps.models.Types.KRNOBJECT_ID;
import static kz.tamur.comps.models.Types.RSTRING;
import static kz.tamur.comps.models.Types.STYLEDTEXT;

import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JTable;

import com.cifs.or2.client.util.KrnObjectItem;

import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.guidesigner.filters.FilterRecord;
import kz.tamur.or3.client.props.Property;
import kz.tamur.util.Pair;

/**
 * Класс делигат при вызове редактора выражений из таблицы с ошибками
 * 
 * */
public class ExprDelegate extends ExprEditorDelegate{
	private OrGuiComponent cmp;
	private Property pr;
	
	
	
	public ExprDelegate(JTable table, String id, OrGuiComponent cmp, Property pr) {
		super(table, id);
		this.cmp = cmp;
		this.pr = pr;
	}
	
	//Переопределяем метод для установки новой формулы напрямую в компонент
	public void setExpression(String expression) {
		//Expression value = new Expression(expression);
		setPropertyValue(cmp, getPath(pr), expression);
	}
	
	private String getPath(Property prop){
        if (prop==null) {
            return null;
        }
        String res= prop.getId();
        Property prop_=prop.getParent();
        while(prop_.getId()!=null && !"Root".equals(prop_.getId())){
             res=prop_.getId()+"."+res;
             prop_=prop_.getParent();
        }
        return res;
    }
	
	
	private void setPropertyValue(OrGuiComponent c, String propId, Object value) {
        StringTokenizer st = new StringTokenizer(propId, ".");
        PropertyNode pn = c.getProperties();
        // блокировка логических операторов
        boolean isBlock = false;
        while (pn != null && st.hasMoreTokens()) {
            pn = pn.getChild(st.nextToken());
        }
        if (pn != null) {
            Pair<String, Object> p;
            PropertyValue pv;
            switch (pn.getType()) {
            case FILTER:
                if (value != null) {
                    if (pn.isArray()) {
                        FilterRecord[] value_ = new FilterRecord[((Vector) value).size()];
                        for (int i = 0; i < value_.length; ++i) {
                            KrnObjectItem obj = (KrnObjectItem) ((Vector) value).get(i);
                            value_[i] = new FilterRecord(obj.obj, obj.title);
                        }
                        value = value_;
                    } else {
                        value = new FilterRecord(((KrnObjectItem) value).obj, ((KrnObjectItem) value).title);
                    }
                }
                break;
            case STYLEDTEXT:
            case RSTRING:
                pv = c.getPropertyValue(pn);
                p = pv.resourceStringValue();
                value = new Pair(p != null ? p.first : null, value);
                break;
            case HTML_TEXT:
                if (value instanceof byte[]) {
                    pv = c.getPropertyValue(pn);
                    p = pv.resourceStringValue();
                    value = new Pair(p != null ? p.first : null, value);
                }
                break;
            case KRNOBJECT_ID:
                if (value != null) {
                    isBlock = true;
                    // перенести идентификаторы объектов в вектор
                    Vector<KrnObjectItem> val = (Vector<KrnObjectItem>) value;
                    StringBuilder temp = new StringBuilder();
                    final int size = val.size() - 1;
                    // сконвертировать вектор в строку, разделяя элементы запятой
                    for (int i = 0;; ++i) {
                        temp.append((val.get(i)).obj.id);
                        if (i == size) {
                            break;
                        }
                        temp.append(",");
                    }
                    value = temp.toString();
                }
                break;
            }

            if (value instanceof Vector && !isBlock) {
                KrnObjectItem obj = (KrnObjectItem) ((Vector) value).get(0);
                c.setPropertyValue(new PropertyValue(String.valueOf(obj.obj.id), pn.getKrnClassName(), obj.title, pn));
            } else if (value instanceof KrnObjectItem) {
                c.setPropertyValue(new PropertyValue(String.valueOf(((KrnObjectItem) value).obj.id), pn.getKrnClassName(),
                        ((KrnObjectItem) value).title, pn));
            } else {
                if (value instanceof String && pn.getType() == INTEGER) {
                    if (!((String) value).isEmpty()) {
                        value = Integer.valueOf((String) value);
                    }
                }
                c.setPropertyValue(new PropertyValue(value, pn));
                
            }
        }
    }
	
}