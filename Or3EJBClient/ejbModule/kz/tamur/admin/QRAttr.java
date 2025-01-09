package kz.tamur.admin;

import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.client.Utils;
import com.cifs.or2.client.Kernel;


/**
 * Created by IntelliJ IDEA.
 * User: Администратор
 * Date: 20.02.2004
 * Time: 16:23:19
 * To change this template use Options | File Templates.
 */
public class QRAttr {
        private String name;
        private String path;
        private long type;
        private String root;
        private String titles;
        private long depth;
        public KrnObject obj;
        private long langId = Utils.getDataLangId();
        private int operation;

        Kernel krn = Kernel.instance();
        KrnClass clsQRAttrs_ ;

        QRAttr(KrnObject c) {
            long langId = Utils.getDataLangId();
            obj = c;
            try {
                name = getStringValue("Наименование", langId);
                path = getStringValue("path", 0);
                type = getLongValue("Тип");
                root = getStringValue("root", 0);
                titles = getStringValue("titles", 0);
                depth = getLongValue("глубина");

            } catch (KrnException ex) {
                ex.printStackTrace();
            }
        }

        public String getStringValue(String attrName, long langId) throws KrnException {
            String res = null;
            String[] strs = krn.getStrings(obj, attrName, langId, 0);
            if (strs != null && strs.length > 0) res = strs[0];
            return res;
        }

        public long getLongValue(String attrName) throws KrnException {
            long res = 0;
            long[] ls = krn.getLongs(obj, attrName, 0);
            if (ls != null && ls.length > 0) res = ls[0];
            return res;
        }

        public String getName() {
            return name;
        }

        public String getPath() {
            return path;
        }

        public long getType() {
            return type;
        }

        public String getRoot() {
            return root;
        }

        public String getTitles() {
            return titles;
        }

        public long getDepth() {
            return depth;
        }

        public void setName(String name, boolean b) throws KrnException {
            this.name = name;
            if (b) setStringValue("Наименование", langId, name);
        }

        public void setPath(String path, boolean b) throws KrnException {
            this.path = path;
            if (b) setStringValue("path", 0, path);
        }

        public void setType(int type, boolean b) throws KrnException {
            this.type = type;
            if (b) setLongValue("Тип", type);
        }

        public void setRoot(String root, boolean b) throws KrnException {
            this.root = root;
            if (b) setStringValue("root", 0, root);
        }

        public void setTitles(String titles, boolean b) throws KrnException {
            this.titles = titles;
            if (b) setStringValue("titles", 0, titles);
        }

        public void setDepth(int depth, boolean b) throws KrnException {
            this.depth = depth;
            if (b) setLongValue("глубина", depth);
        }

        public int getOperation() {
            return operation;
        }

        public void setOperation(int operation) {
            this.operation = operation;
        }

        public void setStringValue(String attrName, long langId, String value) throws KrnException {
            if (value != null && value instanceof String)
            krn.setString(obj.id, getQRAttrClass().id, attrName, 0, langId, value, 0);
        }

        public void setLongValue(String attrName, int value) throws KrnException {
            krn.setLong(obj.id, getQRAttrClass().id, attrName, 0, value, 0);
        }

        private KrnClass getQRAttrClass() {
            if (clsQRAttrs_ == null) {
                try {
                    clsQRAttrs_ = Kernel.instance().getClassByName("QRAttrs");
                } catch (KrnException ex) {
                    ex.printStackTrace();
                }
            }
            return clsQRAttrs_;
        }
    }
