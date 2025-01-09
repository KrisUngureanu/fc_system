package kz.tamur.util;

import com.cifs.or2.kernel.KrnAttribute;

public final class KrnUtil {
	
	public static KrnAttribute createAttribute(
			String uid,
			long id,
			String name,
			long classId,
			long typeId,
			int colType,
			boolean isUnique,
			boolean isMultilingual,
			boolean isIndexed,
			int size,
			long flags,
			boolean isRepl,
			long rAttrId,
			long sAttrId,
			boolean sDesc,
			String tname,
			byte[] beforeEventExpression,
			byte[] afterEventExpression,
			byte[] beforeDeleteEventExpression,
			byte[] afterDeleteEventExpression,
			int beforeEventTr,
			int afterEventTr,
			int beforeDeleteEventTr,
			int afterDeleteEventTr,
			int accessModifier,
			boolean isEncrypt) {
		return new KrnAttribute(uid, id, name, classId, typeId, colType, isUnique, isMultilingual, isIndexed, size, flags, isRepl, rAttrId, sAttrId,
				sDesc, tname, beforeEventExpression, afterEventExpression, beforeDeleteEventExpression, afterDeleteEventExpression, beforeEventTr,
				afterEventTr, beforeDeleteEventTr, afterDeleteEventTr, accessModifier, isEncrypt);
	}

	public static KrnAttribute createAttribute(
			String uid,
			long id,
			String name,
			long classId,
			long typeId,
			int colType,
			boolean isUnique,
			boolean isMultilingual,
			boolean isIndexed,
			int size,
			long flags,
			boolean isRepl,
			long rAttrId,
			long sAttrId,
			boolean sDesc,
			String tname,
			byte[] beforeEventExpression,
			byte[] afterEventExpression,
			byte[] beforeDeleteEventExpression,
			byte[] afterDeleteEventExpression,
			int beforeEventTr,
			int afterEventTr,
			int beforeDeleteEventTr,
			int afterDeleteEventTr,
			int accessModifier) {
		return new KrnAttribute(uid, id, name, classId, typeId, colType, isUnique, isMultilingual, isIndexed, size, flags, isRepl, rAttrId, sAttrId,
				sDesc, tname, beforeEventExpression, afterEventExpression, beforeDeleteEventExpression, afterDeleteEventExpression, beforeEventTr,
				afterEventTr, beforeDeleteEventTr, afterDeleteEventTr, accessModifier);
	}

	public static KrnAttribute createAttribute(
			String uid,
			long id,
			String name,
			long classId,
			long typeId,
			int colType,
			boolean isUnique,
			boolean isMultilingual,
			boolean isIndexed,
			int size,
			long flags,
			boolean isRepl,
			String tname,
			byte[] beforeEventExpression,
			byte[] afterEventExpression,
			byte[] beforeDeleteEventExpression,
			byte[] afterDeleteEventExpression,
			int beforeEventTr,
			int afterEventTr,
			int beforeDeleteEventTr,
			int afterDeleteEventTr,
			int accessModifier) {
		return new KrnAttribute(uid, id, name, classId, typeId, colType, isUnique, isMultilingual, isIndexed, size, flags, isRepl, 0, 0, false, tname,
				beforeEventExpression, afterEventExpression, beforeDeleteEventExpression, afterDeleteEventExpression, beforeEventTr, afterEventTr,
				beforeDeleteEventTr, afterDeleteEventTr, accessModifier);
	}
	
	public static KrnAttribute createAttribute(
			String uid,
			long id,
			String name,
			long classId,
			long typeId,
			int colType,
			boolean isUnique,
			boolean isMultilingual,
			boolean isIndexed,
			int size,
			long flags,
			boolean isRepl,
			String tname,
			byte[] beforeEventExpression,
			byte[] afterEventExpression,
			byte[] beforeDeleteEventExpression,
			byte[] afterDeleteEventExpression,
			int beforeEventTr,
			int afterEventTr,
			int beforeDeleteEventTr,
			int afterDeleteEventTr,
			int accessModifier,
			boolean isEncrypt) {
		return new KrnAttribute(uid, id, name, classId, typeId, colType, isUnique, isMultilingual, isIndexed, size, flags, isRepl, 0, 0, false, tname,
				beforeEventExpression, afterEventExpression, beforeDeleteEventExpression, afterDeleteEventExpression, beforeEventTr, afterEventTr,
				beforeDeleteEventTr, afterDeleteEventTr, accessModifier, isEncrypt);
	}

	public static KrnAttribute createDummyAttribute(String name, long typeId) {
		return new KrnAttribute(null, 0, name, 0, typeId, 0, false, false, false, 0, 0, false, 0, 0, false, null, null, null, null, null, 0, 0, 0, 0, 0);
	}
}