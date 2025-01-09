package kz.tamur.util;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.ClassNode;
import com.cifs.or2.kernel.KrnAttribute;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.or3.util.PathElement;

/**
 * User: Vital
 * Date: 09.02.2005
 * Time: 16:32:32
 */
public class AttributeTypeChecker {

    private static AttributeTypeChecker checker = null;

    public static final long STRING_TYPE = Kernel.IC_STRING;
    public static final long INTEGER_TYPE = Kernel.IC_INTEGER;
    public static final long DATE_TYPE = Kernel.IC_DATE;
    public static final long FLOAT_TYPE = Kernel.IC_FLOAT;
    public static final long BLOB_TYPE = Kernel.IC_BLOB;
    public static final long MEMO_TYPE = Kernel.IC_MEMO;
    public static final long BOOLEAN_TYPE = Kernel.IC_BOOL;

    public static AttributeTypeChecker instance() {
        if (checker == null) {
            checker = new AttributeTypeChecker();
        }
        return checker;
    }

    protected AttributeTypeChecker() {
    }

    public boolean check(PropertyValue value, long[] types) {
        PropertyNode prop = value.getProperty();
        if ("data".equals(prop.getName())) {
            if ("".equals(value.toString()) || value.isNull()) {
                return true;
            }
            boolean res = true;
            String msg="";
            try {
                System.out.println(value.toString());
                final Kernel krn = Kernel.instance();
                String[] names = value.toString().split("\\.");
                int p = names[0].indexOf('(');
                if (p != -1)
                	names[0] = names[0].substring(0, p);
                ClassNode type = krn.getClassNodeByName(names[0]);
                for (int i = 1; i < names.length; i++) {
                	PathElement pe = Funcs.parseAttrName(names[i]);
                	KrnAttribute attr = type.getAttribute(pe.name);
                    if(attr!=null){
                        if (pe.castClassName != null) {
                            type = krn.getClassNodeByName(pe.castClassName);
                        } else {
                            type = krn.getClassNode(attr.typeClassId);
                        }
                    }
                }
                for (int i = 0; i < types.length; i++) {
                    if (types[i] == type.getId()) {
                        res = true;
                        break;
                    } else {
                        res = false;
                        msg="type="+type+";attr.typeClassId=" + type.getId();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (res == false) {
                System.out.println(msg);
//                MessagesFactory.showMessageDialog(
//                        (Frame)DesignerFrame.instance().getTopLevelAncestor(),
//                        MessagesFactory.ERROR_MESSAGE, "�������������� ���� �������� � ���� ����!");
            }
            return res;
        }
        return true;
    }
}
