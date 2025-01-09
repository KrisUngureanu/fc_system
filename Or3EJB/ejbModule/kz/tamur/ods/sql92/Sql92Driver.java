package kz.tamur.ods.sql92;

import kz.tamur.DriverException;
import kz.tamur.ods.*;

import java.sql.Connection;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 06.12.2005
 * Time: 20:10:20
 * To change this template use File | Settings | File Templates.
 */
public class Sql92Driver { // implements Driver {

    private Connection conn;

    private long baseId;

    protected Map classById = Collections.synchronizedMap(new HashMap());
    protected Map classByName = Collections.synchronizedMap(new HashMap());

    protected Map attrById = Collections.synchronizedMap(new HashMap());
    protected Map attrByName = Collections.synchronizedMap(new HashMap());

    private static int MAX_ID_COUNT = 100;

    public Sql92Driver(Connection conn) throws DriverException {
        this.conn = conn;
        //this.baseId = getId("dbase_id");
    }
/*

    public KrnClass createClass(String name, long parentId, boolean isRepl, long id)
            throws DriverException {
        QueryRunner qr = new QueryRunner();
        try {
            // Если не задан конкретный id, то создаем новый
            if (id == -1) {
                id = getNextId("class_id");
            } else {
                updateId("class_id", id);
            }

            // Создаем запись в таблице t_classes
            Object[] params = {new Long(id), new Long(parentId), name};
            qr.update(conn,
                    "INSERT INTO t_classes (c_id,c_base_id,c_name)"
                    + " VALUES (?,?,?)",
                    params);

            // Создаем записи в таблице t_clinks
            List superClasses = getClasses(parentId, false, true);
            PreparedStatement pst = null;
            try {
                pst = conn.prepareStatement("INSERT INTO t_clinks (c_outer_class_id, c_inner_class_id)"
                        + " VALUES (?,?)");
                pst.setLong(2, id);
                for (int i = 0; i < superClasses.size(); i++) {
                    KrnClass scls = (KrnClass) superClasses.get(i);
                    pst.setLong(1, scls.id);
                    pst.executeUpdate();
                }
                pst.setLong(1, id);
                pst.executeUpdate();
            } finally {
                DbUtils.close(pst);
            }

            // Создаем записи в таблице t_changescls
            params = new Object[]{
                new Long(getNextId("change_cid")),
                new Integer(ENTITY_TYPE_CLASS),
                new Integer(ACTION_CREATE),
                new Long(id),
                new Long(parentId),
                name
            };
            qr.update(conn,
                    "INSERT INTO t_changescls(c_id,c_entity,c_action,"
                    + "c_entity_id,c_base_id,c_name) VALUES (?,?,?,?,?,?)",
                    params);

            KrnClass cls = new KrnClass(id, baseId, isRepl, name);
            putClass(cls);
            return cls;

        } catch (SQLException e) {
            throw new DriverException(e);
        }
    }

    public KrnClass changeClass(long id, long parentId, String name, boolean isRepl)
            throws DriverException {

        KrnClass cls = getClassById(id);
        try {
            if (cls.parentId != parentId) {
                // Вносим изменения в t_clinks
                // Удаляем рекурсивные ссылки для старого суперкласса
                List superClasses = getClasses(cls.parentId, false, true);
                List subClasses = getClasses(cls.id, true, true);
                PreparedStatement pst = null;
                try {
                    pst = conn.prepareStatement("DELETE FROM t_clinks WHERE c_inner_class_id=?"
                            + " AND c_outer_class_id=?");
                    for (int i = 0; i < subClasses.size(); i++) {
                        KrnClass subCls = (KrnClass) subClasses.get(i);
                        for (int j = 0; j < superClasses.size(); j++) {
                            KrnClass superCls = (KrnClass) superClasses.get(j);
                            pst.setLong(1, subCls.id);
                            pst.setLong(2, superCls.id);
                            pst.executeUpdate();
                        }
                    }
                } finally {
                    DbUtils.close(pst);
                }

                // Добавляем рекурсивные ссылки для нового суперкласса
                superClasses = getClasses(parentId, false, true);
                try {
                    pst = conn.prepareStatement("INSERT INTO t_clinks (c_inner_class_id,"
                            + "c_outer_class_id) VALUES (?,?)");
                    for (int i = 0; i < subClasses.size(); i++) {
                        KrnClass subCls = (KrnClass) subClasses.get(i);
                        for (int j = 0; j < superClasses.size(); j++) {
                            KrnClass superCls = (KrnClass) superClasses.get(j);
                            pst.setLong(1, subCls.id);
                            pst.setLong(2, superCls.id);
                            pst.executeUpdate();
                        }
                    }
                } finally {
                    DbUtils.close(pst);
                }
            }

            if (!cls.name.equals(name) || cls.parentId != parentId) {
                // Вносим изменения в таблицу t_classes
                QueryRunner qr = new QueryRunner();
                Object[] params = {
                    name, new Long(parentId), new Long(id)
                };
                qr.update(conn,
                        "UPDATE t_classes SET c_name=?, c_base_id=?"
                        + " WHERE c_id=?",
                        params);

                // Создаем записи в таблице t_changescls
                params = new Object[]{
                    new Long(getNextId("change_cid")),
                    new Integer(ENTITY_TYPE_CLASS),
                    new Integer(ACTION_MODIFY),
                    new Long(id),
                    new Long(parentId),
                    name
                };
                qr.update(conn,
                        "INSERT INTO t_changescls(c_id,c_entity,c_action,"
                        + "c_entity_id,c_base_id,c_name) VALUES (?,?,?,?,?,?)",
                        params);

                // Обновляем кэш классов
                removeClass(cls);
                cls = new KrnClass(id, parentId, isRepl, name);
                putClass(cls);
            }
            return cls;

        } catch (SQLException e) {
            throw new DriverException(e);
        }
    }

    public void deleteClass(long id) throws DriverException {
        List subClss = getClasses(id, true, true);
        for (int i = 0; i < subClss.size(); i++) {
            KrnClass subCls = (KrnClass) subClss.get(i);
            deleteClassImpl(subCls);
        }
    }

    protected void deleteClassImpl(KrnClass cls) throws DriverException {
        Long id = new Long(cls.id);
        try {
            // Удаление изменений всех объектов этого класса
            QueryRunner qr = new QueryRunner();
            ResultSetHandler rh = new LongListResultSetHandler();
            List objIds = (List) qr.query(conn,
                    "SELECT c_id FROM t_objects WHERE c_class_id=?",
                    id,
                    rh);
            if (objIds.size() > 0) {
                PreparedStatement pst = null;
                try {
                    pst = conn.prepareStatement("DELETE FROM t_changes WHERE c_object_id=?");
                    for (int i = 0; i < objIds.size(); i++) {
                        Long objId = (Long) objIds.get(i);
                        pst.setLong(1, objId.longValue());
                        pst.executeUpdate();
                    }
                } finally {
                    DbUtils.close(pst);
                }

                // Удаление всех объектов этого класса
                qr.update(conn,
                        "DELETE FROM t_objects WHERE c_class_id=?",
                        id);
            }

            // Удаление записей в таблце t_clinks
            qr.update(conn,
                    "DELETE FROM t_clinks WHERE c_outer_class_id=?"
                    + " OR c_inner_class_id=?",
                    new Object[]{id, id});

            // Удаление атрибутов
            List attrs = getAttributesByClassId(cls.id, false);
            for (int i = 0; i < attrs.size(); i++) {
                KrnAttribute attr = (KrnAttribute) attrs.get(i);
                deleteAttribute(attr.id);
            }
            attrs = getAttributesByTypeId(cls.id);
            for (int i = 0; i < attrs.size(); i++) {
                KrnAttribute attr = (KrnAttribute) attrs.get(i);
                deleteAttribute(attr.id);
            }

            // Удаление записей в таблце t_classes
            qr.update(conn,
                    "DELETE FROM t_classes WHERE c_id=?",
                    id);

            // Создание записи в таблице t_changescls
            Object[] params = new Object[]{
                new Long(getNextId("change_cid")),
                new Integer(ENTITY_TYPE_CLASS),
                new Integer(ACTION_DELETE),
                id,
                new Long(cls.parentId),
                cls.name
            };
            qr.update(conn,
                    "INSERT INTO t_changescls(c_id,c_entity,c_action,"
                    + "c_entity_id,c_base_id,c_name) VALUES (?,?,?,?,?,?)",
                    params);

            // Удаляем класс из кэша
            removeClass(cls);

        } catch (SQLException e) {
            throw new DriverException(e);
        }
    }

    public KrnClass getClassById(long id) throws DriverException {
        KrnClass cls = (KrnClass) classById.get(new Long(id));
        if (cls != null)
            return cls;

        QueryRunner qr = new QueryRunner();
        ResultSetHandler rh = new ClassResultSetHandler();
        try {
            List clss = (List) qr.query(conn,
                    "SELECT * FROM t_classes WHERE c_id=?",
                    new Long(id),
                    rh);
            if (clss.size() > 0) {
                cls = (KrnClass) clss.get(0);
            }
        } catch (SQLException e) {
            throw new DriverException(e);
        }

        if (cls == null) {
            throw new DriverException("Class with id=" + id + " not found");
        }

        putClass(cls);

        return cls;
    }

    public KrnClass getClassByName(String name) throws DriverException {
        KrnClass cls = (KrnClass) classByName.get(name);
        if (cls != null)
            return cls;

        QueryRunner qr = new QueryRunner();
        ResultSetHandler rh = new ClassResultSetHandler();
        try {
            List clss = (List) qr.query(conn,
                    "SELECT * FROM t_classes WHERE c_name=?",
                    name,
                    rh);
            if (clss.size() > 0) {
                cls = (KrnClass) clss.get(0);
            }
        } catch (SQLException e) {
            throw new DriverException(e);
        }

        if (cls == null) {
            throw new DriverException("Class with name='"
                    + name + "' not found");
        }

        putClass(cls);

        return cls;
    }

    public List getClasses(long classId, boolean subClasses, boolean recursive)
            throws DriverException {
        if (classId == -1) {
            return Collections.EMPTY_LIST;
        }
        try {
            QueryRunner qr = new QueryRunner();
            ResultSetHandler rh = new ClassResultSetHandler();
            String sql = null;
            if (subClasses) {
                if (recursive) {
                    sql = "SELECT c_id,c_name,c_base_id FROM t_classes, t_clinks"
                            + " WHERE c_inner_class_id=c_id AND c_outer_class_id=?";
                } else {
                    sql = "SELECT c_id,c_name,c_base_id FROM t_classes"
                            + " WHERE c_base_id=?";
                }
            } else {
                if (recursive) {
                    sql = "SELECT c_id,c_name,c_base_id FROM t_classes, t_clinks"
                            + " WHERE c_outer_class_id=c_id AND c_inner_class_id=?";
                } else {
                    KrnClass cls = getClassById(classId);
                    if (cls.parentId != -1) {
                        KrnClass pcls = getClassById(cls.parentId);
                        return Collections.singletonList(pcls);
                    }
                    return Collections.EMPTY_LIST;
                }
            }

            return (List) qr.query(conn, sql, new Long(classId), rh);

        } catch (SQLException e) {
            throw new DriverException(e);
        }
    }

    public KrnAttribute createAttribute(long id, long classId, long typeId,
                                        String name, boolean isArray,
                                        boolean isUnique, boolean isIndexed,
                                        boolean isMultilingual, int size)
            throws DriverException {

        if (id == -1) {
            id = getNextId("attr_id");
        } else {
            updateId("attr_id", id);
        }

        try {
            // Добавление записи в таблицу атрибутов
            QueryRunner qr = new QueryRunner();
            Object[] params = {
                new Long(id),
                new Long(classId),
                new Long(typeId),
                name,
                new Boolean(isArray),
                new Boolean(isUnique)
            };
            qr.update(conn,
                    " INSERT INTO t_attrs (c_id,c_class_id,c_type_id,c_name,c_is_array," +
                    " c_is_unique)" +
                    " VALUES (?,?,?,?,?,?)",
                    params);

            // Создаем записи в таблице t_changescls
            params = new Object[]{
                new Long(getNextId("change_cid")),
                new Integer(ENTITY_TYPE_ATTRIBUTE),
                new Integer(ACTION_CREATE),
                new Long(id),
                new Long(classId),
                new Long(typeId),
                name,
                new Boolean(isArray),
                new Boolean(isUnique)
            };
            qr.update(conn,
                    "INSERT INTO t_changescls (c_id,c_entity,c_action,"
                    + "c_entity_id,c_class_id,c_type_id,c_name,c_is_array,"
                    + "c_is_unique) VALUES (?,?,?,?,?,?,?,?,?)",
                    params);

            KrnAttribute attr = new KrnAttribute((int) id, name, (int) classId,
                    (int) typeId, isArray, isUnique, isMultilingual, isIndexed, size);
            putAttribute(attr);
            return attr;
        } catch (SQLException e) {
            throw new DriverException(e);
        }
    }

    public KrnAttribute changeAttribute(long id, long typeId,
                                        String name, boolean isArray,
                                        boolean isUnique, boolean isIndexed,
                                        boolean isMultilingual, int size)
            throws DriverException {

        KrnAttribute attr = getAttributeById(id);
        try {
            // Изменение записи в таблице атрибутов
            QueryRunner qr = new QueryRunner();
            Object[] params = {
                new Long(typeId),
                name,
                new Boolean(isArray),
                new Boolean(isUnique),
                new Long(id)
            };
            qr.update(conn,
                    "UPDATE t_attrs SET c_type_id=?,c_name=?,c_is_array=?,"
                    + "c_is_unique=? WHERE c_id=?",
                    params);

            // Создаем запись в таблице t_changescls
            params = new Object[]{
                new Long(getNextId("change_cid")),
                new Integer(ENTITY_TYPE_ATTRIBUTE),
                new Integer(ACTION_MODIFY),
                new Long(id),
                new Long(attr.classId),
                new Long(typeId),
                name,
                new Boolean(isArray),
                new Boolean(isUnique)
            };
            qr.update(conn,
                    "INSERT INTO t_changescls (c_id,c_entity,c_action,"
                    + "c_entity_id,c_class_id,c_type_id,c_name,c_is_array,"
                    + "c_is_unique) VALUES (?,?,?,?,?,?,?,?,?)",
                    params);

            removeAttribute(attr);
            attr = new KrnAttribute((int) id, name, attr.classId,
                    (int) typeId, isArray, isUnique, isMultilingual, isIndexed, size);
            putAttribute(attr);
            return attr;
        } catch (SQLException e) {
            throw new DriverException(e);
        }
    }

    public void deleteAttribute(long id) throws DriverException {

        KrnAttribute attr = getAttributeById(id);
        try {
            // Удаление записей в таблице t_changes
            QueryRunner qr = new QueryRunner();
            qr.update(conn,
                    "DELETE FROM t_changes WHERE c_attr_id=?",
                    new Long(id));

            // Удаление записи в таблице атрибутов
            qr.update(conn,
                    "DELETE FROM t_attrs WHERE c_id=?",
                    new Long(id));

            // Создание записи в таблице t_changescls
            Object[] params = new Object[]{
                new Long(getNextId("change_cid")),
                new Integer(ENTITY_TYPE_ATTRIBUTE),
                new Integer(ACTION_DELETE),
                new Long(id),
                new Long(attr.classId),
                new Long(attr.typeClassId),
                attr.name,
                new Boolean(attr.isArray),
                new Boolean(attr.isUnique)
            };
            qr.update(conn,
                    "INSERT INTO t_changescls (c_id,c_entity,c_action,"
                    + "c_entity_id,c_class_id,c_type_id,c_name,c_is_array,"
                    + "c_is_unique) VALUES (?,?,?,?,?,?,?,?,?)",
                    params);

            removeAttribute(attr);
        } catch (SQLException e) {
            throw new DriverException(e);
        }
    }

    public KrnAttribute getAttributeById(long id) throws DriverException {

        KrnAttribute attr = (KrnAttribute) attrById.get(new Long(id));
        if (attr != null) {
            return attr;
        }

        try {
            QueryRunner qr = new QueryRunner();
            ResultSetHandler rh = new AttrResultSetHandler();
            List res = (List) qr.query(conn,
                    "SELECT * FROM t_attrs WHERE c_id=?",
                    new Long(id), rh);
            if (res.size() > 0) {
                attr = (KrnAttribute) res.get(0);
                putAttribute(attr);
                return attr;
            } else {
                throw new DriverException("Attribute with id=" + id + " not found");
            }
        } catch (SQLException e) {
            throw new DriverException(e);
        }
    }

    public KrnAttribute getAttributeByName(long classId, String name) throws DriverException {

        KrnAttribute attr = (KrnAttribute) attrByName.get(classId + "@" + name);
        if (attr != null) {
            return attr;
        }

        try {
            QueryRunner qr = new QueryRunner();
            ResultSetHandler rh = new AttrResultSetHandler();
            List res = (List) qr.query(conn,
                    "SELECT a.* FROM t_attrs a,t_clinks l"
                    + " WHERE l.c_inner_class_id=?"
                    + " AND a.c_class_id=l.c_outer_class_id"
                    + " AND a.c_name=?",
                    new Object[]{new Long(classId), name},
                    rh);
            if (res.size() > 0) {
                attr = (KrnAttribute) res.get(0);
                putAttribute(attr);
                return attr;
            } else {
                throw new DriverException("Attribute with name=" + name + " in class with id="
                        + classId + " not found");
            }
        } catch (SQLException e) {
            throw new DriverException(e);
        }
    }

    public List getAttributesByClassId(long classId, boolean inherited)
            throws DriverException {
        String sql = null;
        if (inherited) {
            sql = "SELECT a.c_id,a.c_class_id,a.c_type_id,a.c_name,"
                    + "a.c_is_array,a.c_is_unique,a.c_level,a.c_relation,"
                    + "a.c_is_brief,a.c_flags,a.c_flag_del"
                    + " FROM t_clinks cl, t_attrs a" +
                    " WHERE a.c_class_id=cl.c_outer_class_id" +
                    "  AND cl.c_inner_class_id=?";
        } else {
            sql = "SELECT c_id,c_class_id,c_type_id,c_name,"
                    + "c_is_array,c_is_unique,c_level,c_relation,"
                    + "c_is_brief,c_flags,c_flag_del"
                    + " FROM t_attrs WHERE c_class_id=?";
        }
        ResultSetHandler rh = new AttrResultSetHandler();
        QueryRunner qr = new QueryRunner();
        try {
            return (List) qr.query(conn, sql, new Long(classId), rh);
        } catch (SQLException e) {
            throw new DriverException(e);
        }
    }

    public List getAttributesByTypeId(long typeId) throws DriverException {
        ResultSetHandler rh = new AttrResultSetHandler();
        QueryRunner qr = new QueryRunner();
        try {
            return (List) qr.query(conn,
                    "SELECT c_id,c_class_id,c_type_id,c_name,"
                    + "c_is_array,c_is_unique,c_level,c_relation,"
                    + "c_is_brief,c_flags,c_flag_del"
                    + " FROM t_attrs WHERE c_type_id=?",
                    new Long(typeId),
                    rh);
        } catch (SQLException e) {
            throw new DriverException(e);
        }
    }

    public long createLongTransaction() throws DriverException {
        return getNextId("transaction_id");
    }

    public KrnObject createObject(long classId, long trId, String uid)
            throws DriverException {

        try {
            if (uid == null) {
                uid = baseId + "." + getNextId("object_uid");
            }
            long id = getNextId("object_id");
            Object[] params = {
                uid,
                new Long(id),
                new Long(classId),
                new Long(trId)
            };
            QueryRunner qr = new QueryRunner();
            qr.update(conn,
                    "INSERT INTO t_objects(c_uid,c_id,c_class_id,c_creator_id,"
                    + "c_locker_id,c_is_deleted,c_tr_id)"
                    + " VALUES (?,?,?,0,0,0,?)",
                    params);

            //@todo Создание записи в таблице t_changes (после setLong)

            return new KrnObject(id, uid, classId);
        } catch (SQLException e) {
            throw new DriverException(e);
        }
    }

    public List getObjectsByUids(List uids) throws DriverException {
        List res = new ArrayList(uids.size());
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = conn.prepareStatement("SELECT c_id, c_class_id FROM t_objects WHERE c_uid=?");
            for (int i = 0; i < uids.size(); i++) {
                String uid = (String) uids.get(i);
                pst.setString(1, uid);
                rs = pst.executeQuery();
                if (rs.next()) {
                    res.add(new KrnObject(rs.getLong("c_id"),
                            uid,
                            rs.getLong("c_class_id")));
                }
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(pst);
        }
        return res;
    }

    public List getObjectsByIds(List ids) throws DriverException {
        List res = new ArrayList(ids.size());
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = conn.prepareStatement("SELECT c_class_id, c_uid FROM t_objects WHERE c_id=?");
            for (int i = 0; i < ids.size(); i++) {
                Number uid = (Number) ids.get(i);
                pst.setLong(1, uid.longValue());
                rs = pst.executeQuery();
                if (rs.next()) {
                    res.add(new KrnObject(uid.longValue(),
                            rs.getString("c_uid"),
                            rs.getLong("c_class_id")));
                }
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(pst);
        }
        return res;
    }

    public SortedSet getValues(long[] objIds, long attrId, long langId, long trId)
            throws DriverException {
        try {
            final KrnAttribute attr = getAttributeById(attrId);
            String tname = getTableName(attr.typeClassId);
            final Map values = new HashMap();
            QueryRunner qr = new QueryRunner();
            ResultSetHandler rh = new ResultSetHandler() {
                public Object handle(ResultSet rs) throws SQLException {
                    while (rs.next()) {
                        long objId = rs.getLong("c_object_id");
                        int i = rs.getInt("c_i");
                        long trId = rs.getLong("ch.c_tr_id");
                        Object value = getValue(rs, attr.typeClassId);
                        MultiKey key = new MultiKey(new Long(objId), new Long(i));
                        if (!values.containsKey(key) || trId > 0) {
                            values.put(key, new Value(objId, i, trId, value));
                        }
                    }
                    return null;
                }
            };
            for (int k = 0; k < objIds.length; k += MAX_ID_COUNT) {
                int len = Math.min(objIds.length - k, MAX_ID_COUNT);
                StringBuffer sql = new StringBuffer("SELECT c_object_id,c_i,ch.c_tr_id");
                if (tname != null) {
                    sql.append(",v.*");
                } else {
                    sql.append(",ch.c_val");
                }
                sql.append(" FROM t_changes ch");
                if (tname != null) {
                    sql.append("," + tname + " v");
                }
                sql.append(" WHERE c_object_id IN ("
                        + Funcs.ids2String(objIds, k, len)
                        + ") AND c_attr_id=" + attrId
                        + " AND c_lang_id=" + langId);
                if (tname != null) {
                    sql.append(" AND v.c_id=ch.c_val");
                }
                if (trId != -1) {
                    sql.append(" AND ch.c_tr_id IN (0," + trId + ")");
                }
                qr.query(conn, sql.toString(), rh);
            }
            SortedSet res = new TreeSet();
            for (Iterator it = values.values().iterator(); it.hasNext();) {
                Value v = (Value) it.next();
                if (v.value != null) {
                    res.add(v);
                }
            }
            return res;

        } catch (Exception e) {
            throw new DriverException(e);
        }
    }

    public void setValue(long objId,
                         long attrId,
                         int index,
                         long langId,
                         long trId,
                         Object value
                         ) throws DriverException {
    }

    private String getTableName(long typeId) {
        if (typeId == PC_STRING || typeId == PC_MSTRING) {
            return "t_strings";
        } else if (typeId == PC_INTEGER || typeId == PC_BOOL) {
            return null;
        } else if (typeId == PC_TIME) {
            return "t_times";
        } else if (typeId == PC_DATE) {
            return "t_dates";
        } else if (typeId == PC_MEMO || typeId == PC_MMEMO) {
            return "t_memo";
        } else if (typeId == PC_FLOAT) {
            return "t_floats";
        } else if (typeId == PC_BLOB || typeId == PC_MBLOB) {
            return "t_blobs";
        } else {
            return "t_objects";
        }
    }

    private Object getValue(ResultSet rs, long typeId) throws SQLException {
        if (typeId == PC_STRING || typeId == PC_MSTRING
                || typeId == PC_MEMO || typeId == PC_MMEMO) {
            String str = getString(rs, "v.c_val");
            return (str != null) ? str.trim() : null;
        } else if (typeId == PC_INTEGER || typeId == PC_BOOL) {
            long v = rs.getLong("ch.c_val");
            if (!rs.wasNull())
                return new Long(v);
            else
                return null;
        } else if (typeId == PC_TIME) {
            return rs.getTimestamp("v.c_val");
        } else if (typeId == PC_DATE) {
            return rs.getDate("v.c_val");
        } else if (typeId == PC_FLOAT) {
            return new Double(rs.getDouble("v.c_val"));
        } else if (typeId == PC_BLOB || typeId == PC_MBLOB) {
            InputStream is = rs.getBinaryStream("v.c_val");
            if (is != null) {
                byte[] buf = new byte[8 * 1024];
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                int n = 0;
                try {
                    while ((n = is.read(buf)) > 0)
                        os.write(buf, 0, n);
                    is.close();
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return os.toByteArray();
            } else {
                return null;
            }
        } else {
            int id = rs.getInt("v.c_id");
            if (id > 0) {
                int classId = rs.getInt("v.c_class_id");
                String uid = rs.getString("v.c_uid");
                return new KrnObject(id, uid, classId);
            }
            return null;
        }
    }

    public long getModelChanges(final long fromId, final ModelChangeProcessor p)
            throws DriverException {
        try {
            QueryRunner qr = new QueryRunner();
            ResultSetHandler rh = new ResultSetHandler() {
                public Object handle(ResultSet rs) throws SQLException {
                    long res = fromId;
                    while (rs.next()) {
                        long id = rs.getLong("c_id");
                        int entity = rs.getInt("c_entity");
                        int action = rs.getInt("c_action");
                        long entityId = rs.getLong("c_entity_id");
                        long baseId = rs.getLong("c_base_id");
                        long classId = rs.getLong("c_class_id");
                        long typeId = rs.getLong("c_type_id");
                        String name = rs.getString("c_name");
                        boolean isArray = rs.getBoolean("c_is_array");
                        boolean isUnique = rs.getBoolean("c_is_unique");
                        int level = rs.getInt("c_level");
                        int relation = rs.getInt("c_relation");
                        boolean isBrief = rs.getBoolean("c_is_brief");
                        long flags = rs.getLong("c_flags");
                        p.process(new ModelChange(id, entity, action, entityId, baseId, name,
                                classId, typeId, isArray, isUnique, level,
                                relation, isBrief, flags));
                        if (id > res) {
                            res = id;
                        }
                    }
                    return new Long(res);
                }
            };
            Long res = (Long) qr.query(conn,
                    "SELECT * FROM t_changescls WHERE c_id>?",
                    new Long(fromId),
                    rh);
            return res.longValue();
        } catch (SQLException e) {
            throw new DriverException(e);
        }
    }

    public Iterator getDataChanges(long fromId, LongHolder lastId)
            throws DriverException {

        throw new DriverException("not implemented");
    }

    public void release() throws DriverException {
        //@todo Пока не своя коннкция
    }

    protected static synchronized long getNextId(String name) throws DriverException {
        Connection conn = ConnectionManager.instance().getConnection();
        try {
            // Увеличиваем значение на 1
            QueryRunner qr = new QueryRunner();
            qr.update(conn,
                    "UPDATE t_ids SET c_last_id=c_last_id+1 WHERE c_name=?",
                    name);

            // Считываем значение
            ResultSetHandler h = new LongResultSetHandler();
            Long id = (Long) qr.query(conn,
                    "SELECT c_last_id FROM t_ids WHERE c_name=?",
                    name,
                    h);
            conn.commit();

            if (id == null) {
                throw new DriverException("Failed to get next id for '" + name + "'");
            }

            return id.longValue();

        } catch (SQLException e) {
            try {
                DbUtils.rollback(conn);
            } catch (SQLException e1) {
                // NOP
            }
            throw new DriverException(e);
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }

    protected static synchronized long getId(String name) throws DriverException {
        Connection conn = ConnectionManager.instance().getConnection();
        try {
            // Считываем значение
            QueryRunner qr = new QueryRunner();
            ResultSetHandler h = new LongResultSetHandler();
            Long id = (Long) qr.query(conn,
                    "SELECT c_last_id FROM t_ids WHERE c_name=?",
                    name,
                    h);

            if (id == null) {
                throw new DriverException("Failed to get id for '" + name + "'");
            }

            return id.longValue();

        } catch (SQLException e) {
            throw new DriverException(e);
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }

    protected static synchronized long updateId(String name, long value)
            throws DriverException {
        Connection conn = ConnectionManager.instance().getConnection();
        try {
            // Перезаписываем тек значение, для блокировки записи в таблице
            QueryRunner qr = new QueryRunner();
            qr.update(conn,
                    "UPDATE t_ids SET c_last_id=c_last_id WHERE c_name=?",
                    name);

            // Считываем значение
            ResultSetHandler h = new LongResultSetHandler();
            Long id = (Long) qr.query(conn,
                    "SELECT c_last_id FROM t_ids WHERE c_name=?",
                    name,
                    h);

            if (id == null) {
                conn.rollback();
                throw new DriverException("Failed to get next id for '" + name + "'");
            }

            // Если value > id то записываем value
            if (value > id.longValue()) {
                qr.update(conn,
                        "UPDATE t_ids SET c_last_id=? WHERE c_name=?",
                        new Object[]{new Long(value), name});
                conn.commit();
                return value;
            } else {
                conn.rollback();
                return id.longValue();
            }

        } catch (SQLException e) {
            try {
                DbUtils.rollback(conn);
            } catch (SQLException e1) {
                // NOP
            }
            throw new DriverException(e);
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }

    protected void putClass(KrnClass cls) {
        classByName.put(cls.name, cls);
        classById.put(new Long(cls.id), cls);
    }

    protected void removeClass(KrnClass cls) {
        classByName.remove(cls.name);
        classById.remove(new Long(cls.id));
    }

    protected void putAttribute(KrnAttribute attr) {
        attrByName.put(attr.classId + "@" + attr.name, attr);
        attrById.put(new Long(attr.id), attr);
    }

    protected void removeAttribute(KrnAttribute attr) {
        attrByName.remove(attr.classId + "@" + attr.name);
        attrById.remove(new Long(attr.id));
    }

    protected String getString(ResultSet rs, String name)
            throws SQLException {
        return rs.getString(name);
    }

*/
}
