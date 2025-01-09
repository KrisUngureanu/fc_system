package com.cifs.or2.server.util;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import com.cifs.or2.kernel.*;
import com.cifs.or2.util.*;
import com.cifs.or2.server.Session;

import java.util.*;

import kz.tamur.util.Pair;

public class ImportXmlHandler extends DefaultHandler
{
	private static final boolean TEST_MODE = false;
	private Session s_;
	private Stack stack_ = new Stack();
	private Stack attrStack_ = new Stack();
	private Stack objectStack_ = new Stack();
	private Stack indexStack_ = new Stack();
	private final static Integer Z = new Integer(0);
	private int saveCounter_;
	private Map localObjects_ = new HashMap();
	private Map findDict_ = new HashMap();
	private Map findCash_ = new HashMap();
	private int oid_ = 100000;

	public ImportXmlHandler(Session s) {
		s_ = s;
	}

  public void startDocument() throws SAXException {
		stack_.clear();
		attrStack_.clear();
		objectStack_.clear();
		indexStack_.clear();
		localObjects_.clear();
		findDict_.clear();
		findCash_.clear();
		attrStack_.push(Z);
		indexStack_.push(Z);
		objectStack_.push(new KrnObject(0, "", 0));
		saveCounter_ = 0;
  }

  public void startElement(String uri, String localName, String qName,
                           Attributes attrs) throws SAXException {
    try {
      if (qName.equals("Object")) {
				final int cid = Integer.parseInt(attrs.getValue("class"));
				final KrnClass cls = s_.getClassById(cid);
				KrnObject obj = null;
				if (TEST_MODE)
					obj = new KrnObject(++oid_, "", cid);
				else
					obj = s_.createObject(cls, 0);
				long objId = getCurrentObjectId();
				int attrId = ((Integer) attrStack_.peek()).intValue();
				int index = incrementCurrentIndex();
             //val
                int langId = 0;
             //val

				if (objId > 0) {
					if (TEST_MODE)
						stack_.push(new Item(objId, attrId, index, obj));
					else
						setAttribute(objId, attrId, langId, index, obj);
				}
				String localId = attrs.getValue("id");
				if (localId != null)
					localObjects_.put(attrs.getValue("id"), obj);
				objectStack_.push(obj);
      }
      else if (qName.equals("Attr")) {
        final Integer aid = Integer.valueOf(attrs.getValue("id"));
				attrStack_.push(aid);
				indexStack_.push(Z);
      }
			else if (qName.equals("String") || qName.equals("Float")
					|| qName.equals("Date"))
			{

        final String value = attrs.getValue("value");
    //val
        String lang = attrs.getValue("lang");
		int langId = (lang != null) ? Integer.parseInt(lang) : 0;
    //val

		final long objId = getCurrentObjectId();
        if (objId > 0) {
          final long attrId = getCurrentAttrId();
          final int index = incrementCurrentIndex();
          if (TEST_MODE)
            stack_.push(new Item(objId, attrId, index, value));
          else
            setAttribute(objId, attrId, langId, index, value);
        }
      }
      else if (qName.equals("ObjectRef"))
      {
        final int id = Integer.parseInt(attrs.getValue("id"));
        final long objId = getCurrentObjectId();
        if (objId > 0) {
          final long attrId = getCurrentAttrId();
          final int index = incrementCurrentIndex();
          final KrnObject value = new KrnObject(id, "", 0);
      //val
          int langId = 0;
     //val
          if (TEST_MODE)
            stack_.push(new Item(objId, attrId, index, value));
          else
            setAttribute(objId, attrId, langId, index, value);
        }
      }
			else if (qName.equals("LRef")) {
				final long objId = getCurrentObjectId();
        final long attrId = getCurrentAttrId();
				final int index = incrementCurrentIndex();
				String localId = attrs.getValue("id");
				KrnObject obj = (KrnObject) localObjects_.get(localId);
        //val
                int langId = 0;
      //val
				if (obj != null) {
					if (TEST_MODE)
						stack_.push(new Item(objId, attrId, index, obj));
					else
						setAttribute(objId, attrId, langId, index, obj);
				}
      }
			else if (qName.equals("FRef")) {
				final long objId = getCurrentObjectId();
                final long attrId = getCurrentAttrId();
				final int index = incrementCurrentIndex();
				int classId = Integer.parseInt(attrs.getValue("class"));
				String path = attrs.getValue("path");
				String value = attrs.getValue("value");
				String lang = attrs.getValue("lang");
				int langId = (lang != null) ? Integer.parseInt(lang) : 0;
				Map objs = getFindObjects(classId, path, langId);
				KrnObject obj = (KrnObject) objs.get(trim(value));
				if (obj == null)
					System.out.println("FREF: " + path + " '" + value + "'");
				if (TEST_MODE)
					stack_.push(new Item(objId, attrId, index, obj));
				else if (obj != null)
					setAttribute(objId, attrId, langId, index, obj);
      }
      else if (qName.equals("FObject")) {
        final int cid = Integer.parseInt(attrs.getValue("class"));
        final KrnClass cls = s_.getClassById(cid);
        String path = attrs.getValue("path");
        String value = attrs.getValue("value");
        String lang = attrs.getValue("lang");
        int langId = (lang != null) ? Integer.parseInt(lang) : 0;
        Map objs = getFindObjects(cid, path, langId);
        KrnObject obj = (KrnObject) objs.get(trim(value));
        if (obj == null)
          System.out.println("FOBJECT: " + path + " '" + value + "'");
        else {
          long objId = getCurrentObjectId();
          long attrId = ((Long) attrStack_.peek()).longValue();
          int index = incrementCurrentIndex();
          if (objId > 0) {
            if (TEST_MODE)
              stack_.push(new Item(objId, attrId, index, obj));
            else
              setAttribute(objId, attrId, langId,  index, obj);
          }
        }
        objectStack_.push(obj);
      }
    } catch (KrnException e) {
      e.printStackTrace();
      throw new SAXException(e);
    } catch (Exception ex) {
			ex.printStackTrace();
		}

  }

  public void endElement(String uri, String localName, String qName)
  throws SAXException {
		if (qName.equals("Object")) {
			objectStack_.pop();
		}
		else if (qName.equals("Attr")) {
			attrStack_.pop();
			indexStack_.pop();
		}
    else if (qName.equals("FObject")) {
      objectStack_.pop();
    }
  }

	public void endDocument() throws SAXException {
		try {
		if (TEST_MODE) {
			for (Iterator it = stack_.iterator(); it.hasNext(); ) {
				Item i = (Item) it.next();
				if (i.value == null) {
					System.out.println("ERROR :" + i.objectId + " " + i.attrId + " " + i.index);
				}
				else {
				String str = (i.value instanceof KrnObject) ? "" + ((KrnObject) i.value).id
																										: i.value.toString();
				System.out.println(i.objectId + " " + i.attrId + " " + i.index + " " + str);
				}
			}
		}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private int incrementCurrentIndex() {
		int index = ((Integer) indexStack_.pop()).intValue();
		indexStack_.push(new Integer(index + 1));
		return index;
	}

	private long getCurrentAttrId() {
		return ((Integer) attrStack_.peek()).longValue();
	}

	private long getCurrentObjectId() {
		KrnObject obj = (KrnObject) objectStack_.peek();
    return (obj != null) ? obj.id : -1;
	}

	private void setAttribute(long objId, long attrId, long langId, int i, Object val)
	throws KrnException {
		++saveCounter_;
		if (val instanceof String)
			System.out.println(saveCounter_ + " " + val);
		final KrnAttribute attr = s_.getAttributeById(attrId);
		String tName = s_.getClassById(attr.typeClassId).name;
		if (tName.equals("string"))
			s_.setString(objId, attrId, i, 0, false, (String) val, 0);
		else if (tName.equals("memo"))
			s_.setString(objId, attrId, i, 0, true, (String) val, 0);
		else if (tName.equals("String"))
			s_.setString(objId, attrId, i, langId, false, (String) val, 0);
		else if (tName.equals("Memo"))
			s_.setString(objId, attrId, i, langId, true, (String) val, 0);
		else if (tName.equals("integer") || tName.equals("boolean")
				|| tName.equals("time"))
			s_.setLong(objId, attrId, i, Integer.parseInt((String) val), 0);
    else if (tName.equals("date")) {
      int date = (int)(Long.parseLong((String) val) / 1000);
      s_.setLong(objId, attrId, i, date, 0);
    }
		else if (tName.equals("float"))
			s_.setFloat(objId, attrId, i, Double.parseDouble((String) val), 0);
		else if (tName.equals("blob"))
			throw new KrnException(0, "не реализовано");
		else
			s_.setObject(objId, attrId, i, ((KrnObject) val).id, 0, false);

		if (saveCounter_ >= 2000) {
			System.out.print("COMMITTING....");
			s_.commitTransaction();
			System.out.println("Ok");
			saveCounter_ = 0;
		}
	}

/*
	private KrnObject findObject(int classId, String path, Object value)
	throws KrnException {
    KrnAttribute[] attrs = s_.getAttributesForPath(path);
		for (int i = attrs.length - 1; i >= 0 && value != null; --i) {
			KrnAttribute attr = attrs[i];
			if (attr.typeClassId == Session.IC_STRING.id) {
				KrnObject[] objs =
						s_.getObjectsByString(attr.classId, attr.id, 0, (String) value,
																	true, 0);
				if (objs.length > 0)
					value = objs[0];
				else
					value = null;
			}
			else if (attr.typeClassId > 100) {
				KrnObject[] objs =
						s_.getObjectsByLong(attr.classId, attr.id, 0,
																((KrnObject) value).id, 0);
				if (objs.length > 0)
					value = objs[0];
				else
					value = null;
			}
		}
		return (KrnObject) value;
	}
*/

	private Map getFindObjects(int classId, String path, int langId)
	throws KrnException {
		Map res = (Map) findDict_.get(path);
		if (res == null) {
			res = new HashMap();
			final KrnClass cls = s_.getClassById(classId);
            KrnObject[] objs = s_.getClassObjects(cls, new long[0], 0);
			int pos = 0;
			while (pos < objs.length) {
				int left = objs.length - pos;
				int n = (left > 100) ? 100 : left;
				KrnObject[] nObjs = new KrnObject[n];
				System.arraycopy(objs, pos, nObjs, 0, n);
				long[] objIds = Funcs.makeObjectIdArray(nObjs);
				Pair p = s_.getObjectAttr(objIds, path, langId, 0, new HashMap());
				for (int i = 0; i < nObjs.length; ++i) {
					Long oid = new Long(nObjs[i].id);
					ArrayList vals = (ArrayList) ((MultiMap) p.second).get(oid);
					if (vals != null && vals.size() > 0) {
            String key = trim(((StringValue) vals.get(vals.size() - 1)).value);
            if (res.containsKey(key))
              res.remove(key);
            else
			  res.put(key, nObjs[i]);
          }
				}
				pos += n;
			}
			findDict_.put(path, res);
		}
		return res;
	}

	private static final class Item {
		public long objectId;
		public long attrId;
		public int index;
		public Object value;

		public Item(long objectId, long attrId, int index, Object value) {
			this.objectId = objectId;
			this.attrId = attrId;
			this.index = index;
			this.value = value;
		}
	}

	private String trim(String str) {
		StringBuffer res = new StringBuffer(str.length());
		char[] chs = str.toCharArray();
		for (int i = 0; i < chs.length; i++) {
			if (!Character.isWhitespace(chs[i]))
				res.append(Character.toLowerCase(chs[i]));
		}
		return res.toString();
	}
}
