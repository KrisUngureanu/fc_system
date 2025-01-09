package kz.tamur.ods;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;

/**
 * Created by IntelliJ IDEA. User: daulet Date: 23.05.2006 Time: 11:21:21 To
 * change this template use File | Settings | File Templates.
 */
public class AttributeChange extends ModelChange {
	public final String name;

	public final KrnClass cls;

	public final KrnClass type;

	public final int collectionType;

	public final boolean isUnique;

	public final boolean isIndexed;

	public final boolean isMultilingual;

	public final boolean isRepl;

	public final int size;

	public final long flags;

	public final KrnAttribute rAttr;

	public final KrnAttribute sAttr;
	
	public final boolean sDesc;

	public final String revIds;

	public String comment;
	
	public String tname;
	
	public int accessModifier;

	public AttributeChange(long id, int action, String entityId, String name,
			KrnClass cls, KrnClass type, int collectionType, boolean isUnique,
			boolean isIndexed, boolean isMultilingual, boolean isRepl,
			int size, long flags, KrnAttribute rAttr, KrnAttribute sAttr, boolean sDesc,
			String revIds, String comment, String tname, int accessModifier) {
		super(id, 1, action, entityId);
		this.name = name;
		this.cls = cls;
		this.type = type;
		this.collectionType = collectionType;
		this.isUnique = isUnique;
		this.isIndexed = isIndexed;
		this.isMultilingual = isMultilingual;
		this.isRepl = isRepl;
		this.size = size;
		this.flags = flags;
		this.rAttr = rAttr;
		this.sAttr = sAttr;
		this.sDesc = sDesc;
		this.revIds = revIds;
		this.comment = comment;
		this.tname = tname;
		this.accessModifier = accessModifier;
	}

	public AttributeChange(long id, int action, String entityId, String name,
			KrnClass cls, KrnClass type, int collectionType, boolean isUnique,
			boolean isIndexed, boolean isMultilingual, boolean isRepl,
			int size, long flags, KrnAttribute rAttr, KrnAttribute sAttr, boolean sDesc,
			String revIds, String tname, int accessModifier) {
		super(id, 1, action, entityId);
		this.name = name;
		this.cls = cls;
		this.type = type;
		this.collectionType = collectionType;
		this.isUnique = isUnique;
		this.isIndexed = isIndexed;
		this.isMultilingual = isMultilingual;
		this.isRepl = isRepl;
		this.size = size;
		this.flags = flags;
		this.rAttr = rAttr;
		this.sAttr = sAttr;
		this.sDesc = sDesc;
		this.revIds = revIds;
		this.tname = tname;
		this.accessModifier = accessModifier;
	}
}