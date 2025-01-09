package kz.tamur.ods;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.server.db.Database;

import kz.tamur.DriverException;
import kz.tamur.or3.util.PathElement;
import kz.tamur.or3.util.PathElement2;
import kz.tamur.util.Funcs;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 30.01.2006
 * Time: 10:19:07
 * To change this template use File | Settings | File Templates.
 */
public class Toolkit {

    public static KrnAttribute[] getAttributesForPath(String path, Driver drv)
            throws DriverException {
        if (path == null)
            return null;
        Database db = drv.getDatabase();
        StringTokenizer st = new StringTokenizer(path, ".");
        int count = st.countTokens();
        KrnAttribute[] res = new KrnAttribute[(count == 0) ? 0 : count - 1];
        if (count > 0) {
            KrnClass cls = db.getClassByName(st.nextToken());
            for (int i = 0; i < count - 1; ++i) {
                PathElement pe = Funcs.parseAttrName(st.nextToken());
                KrnAttribute attr = db.getAttributeByName(cls.id, pe.name);
                res[i] = attr;
                if (attr == null) return null;
                if (pe.castClassName != null) {
                    cls = db.getClassByName(pe.castClassName);
                } else {
                    cls = db.getClassById(attr.typeClassId);
                }
            }
        }
        return res;
    }
    public static PathElement2[] parsePath2(String path, Driver drv)
    throws DriverException {
        String[] strs = path.split("\\.");
        if (strs.length > 0) {
            Database db = drv.getDatabase();
            List<PathElement2> res = new ArrayList<PathElement2>(strs.length);
            PathElement pe = kz.tamur.util.Funcs.parseAttrName(strs[0]);
            KrnClass type = (pe.castClassName == null) ?
                    db.getClassByName(pe.name)
                    : db.getClassByName(pe.castClassName);
            res.add(new PathElement2(type, null, null));
            for (int i = 1; i < strs.length; i++) {
                pe = kz.tamur.util.Funcs.parseAttrName(strs[i]);
                KrnAttribute attr = db.getAttributeByName(type.id, pe.name);
                if (attr == null) {
                    break;
                }
                type = (pe.castClassName == null) ?
                        db.getClassById(attr.typeClassId)
                        : db.getClassByName(pe.castClassName);
                res.add(new PathElement2(type, attr, pe.index));
            }
            return res.toArray(new PathElement2[res.size()]);
        }
        return new PathElement2[0];
    }

}
