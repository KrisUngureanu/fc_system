package com.cifs.or2.server.db;

import static kz.tamur.or3ee.common.SessionIds.*;

import com.cifs.or2.kernel.*;
import com.cifs.or2.server.Session;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.XMLOutputter;
import org.apache.commons.dbutils.DbUtils;

import java.io.IOException;
import java.sql.*;

public class Utils {
    private static final int CURR_DB_VERSION = 2;

    public static Long getLastId(String name, Connection conn)
            throws KrnException {
        long id = -1;
        PreparedStatement pst = null;
        ResultSet set = null;
        try {
            try {
                pst = conn.prepareStatement("SELECT c_last_id FROM t_ids WHERE c_name=?");
                pst.setString(1, name);
                set = pst.executeQuery();

                if (set.next()) {
                    id = set.getLong(1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw new KrnException(0, e.getMessage());
            }
        } finally {
            DbUtils.closeQuietly(set);
            DbUtils.closeQuietly(pst);
        }
        return new Long(id);
    }

    public static void setLastId(String name, Long id, Connection conn)
            throws KrnException {
        try {
            // Модифицируем запись в таблице t_ids
            PreparedStatement pst = conn.prepareStatement(
                    "UPDATE t_ids SET c_last_id=? WHERE c_name=?");
            pst.setLong(1, id.longValue());
            pst.setString(2, name);
            int res = pst.executeUpdate();
            pst.close();
            if (res == 0) {
                // Создаем запись в таблице t_ids
                pst = conn.prepareStatement(
                        "INSERT INTO t_ids (c_name,c_last_id) VALUES (?,?)");
                pst.setString(1, name);
                pst.setLong(2, id.longValue());
                pst.executeUpdate();
                pst.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new KrnException(0, e.getMessage());
        }
    }

    public static String ids2String(int[] ids) {
        StringBuffer res = new StringBuffer();
        if (ids.length > 0) {
            res.append(ids[0]);
            for (int i = 1; i < ids.length; ++i)
                res.append("," + ids[i]);
        }
        return res.toString();
    }

    public static int upgrade(int oldVersion, int dbType)
            throws KrnException {
        /*@todo Перенести в Driver
        try {
            switch (oldVersion) {
            case 0 :
                upgradeRevAttrs(dbType, conn);
            case 1 :
                upgradeExpressions(dbType, conn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new KrnException(0, e.getMessage());
        }
        */
        return CURR_DB_VERSION;
    }

    public static void xmlUI(Session s)
            throws KrnException, IOException, JDOMException {
        final KrnClass uiCls = s.getClassByName("UI");
        final KrnAttribute cattr = s.getAttributeByName(uiCls, "config");
        KrnObject[] uiObjs = s.getClassObjects(uiCls, new long[0], 0);
        for (int i = 0; i < uiObjs.length; i++) {
            String xml = toXml(uiObjs[i], s);
            s.setBlob(uiObjs[i].id, cattr.id, 0, xml.getBytes("UTF-8"), 0, 0);
            System.out.println("!!! " + i + "/" + uiObjs.length);
        }
        s.commitTransaction();
    }

    public static String toXml(KrnObject obj, Session s)
            throws KrnException, IOException, JDOMException {
        Element root = processObject(obj.classId, obj, s, "");
        Document doc = new Document(root);
        XMLOutputter out = new XMLOutputter();
        out.getFormat().setEncoding("UTF-8");
        out.getFormat().setLineSeparator("\n");
        return out.outputString(doc);
    }

    private static Element processObject(long classId, KrnObject obj, Session s,
                                         String path)
            throws KrnException, JDOMException, IOException {
        Config conf = Config.instance(s);
        KrnClass type = s.getClassById(obj.classId);
        if (conf.needsDeepProcessing(classId, type.id, s)
                || path.length() == 0) {
            path = path + "." + type.name;
            System.out.println(path);
            Element e = new Element("Object");
            e.setAttribute("type", type.name);
            KrnAttribute[] attrs = s.getAttributes(type);
            long[] objIds = {obj.id};
            for (int i = 0; i < attrs.length; i++) {
                KrnAttribute attr = attrs[i];
                if (!conf.isSkippingAttribute(attr.id, s)) {
                    Element child = new Element("Attr");
                    child.setAttribute("name", attr.name);
                    Element list = null;
                    if (attr.collectionType == 1) {
                        list = new Element("List");
                        child.addContent(list);
                    }
                    if (attr.typeClassId == CID_INTEGER) {
                        LongValue[] vs = s.getLongValues(objIds, attr.id, 0);
                        if (attr.collectionType == 1) {
                            for (int j = 0; j < vs.length; j++) {
                                Element item = createListItem(vs[j].index,
                                                              "" + vs[j].value);
                                list.addContent(item);
                            }
                        } else if (vs.length > 0) {
                            child.addContent("" + vs[0].value);
                        }
                    } else if (attr.typeClassId == CID_STRING) {
                        //@todo не понятно нихрена почему так язык передаётся
                        int langId = attr.isMultilingual ? 102 : 0;
                        StringValue[] vs = s.getStringValues(
                                objIds, attr.id, langId, false, 0);
                        if (attr.collectionType == 1) {
                            for (int j = 0; j < vs.length; j++) {
                                Element item = createListItem(vs[j].index,
                                                              "" + vs[j].value);
                                list.addContent(item);
                            }
                        } else if (vs.length > 0) {
                            child.addContent(vs[0].value);
                        }
                    } else if (attr.typeClassId == CID_MEMO) {
                        int langId = attr.isMultilingual ? 102 : 0;
                        StringValue[] vs = s.getStringValues(
                                objIds, attr.id, langId, true, 0);
                        if (attr.collectionType == 1) {
                            for (int j = 0; j < vs.length; j++) {
                                Element item = createListItem(vs[j].index,
                                                              "" + vs[j].value);
                                list.addContent(item);
                            }
                        } else if (vs.length > 0) {
                            child.addContent(vs[0].value);
                        }
                    } else if (attr.typeClassId > 100) {
                        ObjectValue[] vs =
                                s.getObjectValues(objIds, attr.id, new long[0], 0);
                        if (attr.collectionType == 1) {
                            for (int j = 0; j < vs.length; j++) {
                                Element item = new Element("Item");
                                item.setAttribute("index", "" + vs[j].index);
                                item.addContent(processObject(classId, vs[j].value,
                                                              s, path));
                                list.addContent(item);
                            }
                        } else if (vs.length > 0) {
                            Element item = processObject(classId, vs[0].value,
                                                         s, path);
                            child.addContent(item);
                        }
                    }
                    if (attr.collectionType == 1) {
                        if (list.getChildren().size() > 0) {
                            e.addContent(child);
                        }
                    } else if (child.getChildren().size() > 0
                            || child.getText().length() > 0) {
                        e.addContent(child);
                    }
                }
            }
            return e;
        } else {
            Element e = new Element("Object");
            e.setAttribute("type", type.name);

            e.setAttribute("id", "" + obj.id);
            KrnAttribute titleAttr = conf.getTitleAttrId(obj.classId);
            if (titleAttr != null) {
                int langId = 0;
                if (titleAttr.typeClassId == CID_STRING
                        && titleAttr.isMultilingual) {
                    langId = 102;
                }
                String[] strs = s.getStrings(obj.id, titleAttr.id, langId,
                                             false, 0);
                if (strs.length > 0) {
                    e.setAttribute("title", strs[0]);
                }
            }
            return e;
        }
    }

    private static Element createListItem(int i, String value) {
        Element item = new Element("Item");
        item.setAttribute("index", "" + i);
        item.addContent(value);
        return item;
    }
}
