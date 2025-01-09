package kz.tamur.guidesigner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.tree.TreeNode;

import kz.tamur.util.AbstractDesignerTreeNode;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.LongValue;
import com.cifs.or2.kernel.StringValue;
import com.cifs.or2.kernel.TimeValue;

public class InterfaceActionsConteiner {
	private static Map<Long, InterfaceActions> editingInterfaces = new HashMap<Long, InterfaceActions>();
	private static String OBJECT_TYPE = "Интерфейс";
	private static DesignerFrame designerFrame;
	private static Kernel kernel = Kernel.instance();
	private static Comparator<Date> reverse = Collections.reverseOrder();
	
	// При открытии интерфейса создает объект InterfaceActions и добавляет его в контейнер
	public static void instance(long ifcId, String title) {
		// Потом надо удалить строки для сохранения xml
		InterfaceActions interfaceActions = new InterfaceActions(ifcId, title);
		editingInterfaces.put(ifcId, interfaceActions);
//		interfaceActions.writeXMLFile(node.getKrnObj().id, interfaceActions.getXMLDocumentToSave());
	}
	
	public static InterfaceActions getInterfaceActions(Long ifcId) {
		if (editingInterfaces.containsKey(ifcId)) {
			return editingInterfaces.get(ifcId);
		} else {
			try {
				// Этот код не должен вызываться по идее
				TreeNode node = kz.tamur.comps.Utils.getInterfaceTree().find(kernel.getObjectById(ifcId, 0));
				InterfaceActions interfaceActions = new InterfaceActions(ifcId, node.toString());
				editingInterfaces.put(ifcId, interfaceActions);
				return interfaceActions;
			} catch (KrnException e) {
				e.printStackTrace();
			}
		}
		return null;
	}		
	
	public static boolean isContein(Long Key) {
		return	editingInterfaces.containsKey(Key);
	}
	
	public static void removeFromConteiner(Long interfaceID) {
		editingInterfaces.remove(interfaceID);
	}
	
	public static SortedMap<Date, Map<Long, String>> getLastInterfaces() {		
		try {
			SortedMap<Date, Map<Long, String>> items = new TreeMap<Date, Map<Long, String>>(reverse);
			String user = kernel.getUser().getName();
			KrnClass actionClass = kernel.getClassByName("Action");
			KrnAttribute dateAttribute = kernel.getAttributeByName(actionClass, "editingDate");
			KrnObject[] actionObjects = kernel.getClassObjects(actionClass, 0);
			long[] actionObjectsID = new long[actionObjects.length]; 
			for (int i = 0; i < actionObjects.length; i++) {
				actionObjectsID[i] = actionObjects[i].id;
			}
			
			final StringValue[] massivUser = kernel.getStringValues(actionObjectsID, actionClass.id, "user", 0, false, 0);
			final StringValue[] massivType = kernel.getStringValues(actionObjectsID, actionClass.id, "type", 0, false, 0);
			final StringValue[] massivName = kernel.getStringValues(actionObjectsID, actionClass.id, "name", 0, false, 0);
			final TimeValue[] massivDateTime = kernel.getTimeValues(actionObjectsID, dateAttribute, 0);
            final LongValue[] massivID = kernel.getLongValues(actionObjectsID, actionClass.id, "id", 0);
            
            for (int i = 0; i < actionObjects.length; i++) {
				if (massivUser[i].value.equals(user)) {
					final int index = i;
					if (massivType[i].value.equals(OBJECT_TYPE)) {
						items.put(new Date(massivDateTime[i].value.year, massivDateTime[i].value.month, massivDateTime[i].value.day, massivDateTime[i].value.hour, massivDateTime[i].value.min, massivDateTime[i].value.sec),
								new HashMap<Long, String>() {{put(massivID[index].value, massivName[index].value);}});
					}
				}
			}
            return items;
		} catch (KrnException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static boolean getInterfacesMode() {
		if (kernel.checkExistenceClassByName("Action___")) {
			List<String> attributesNames = new ArrayList<String>(Arrays.asList("editingDate", "id", "log", "name", "type", "user"));
			List<String> actionAttributesNames = getAttributesNamesByClassName("Action");
			for (String attributeName: attributesNames) {
				if (!actionAttributesNames.contains(attributeName)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	private static List<String> getAttributesNamesByClassName(String className) {
		List<String> attributesNames = new ArrayList<String>();
		try {
			for (KrnAttribute attribute: kernel.getAttributes(kernel.getClassByName(className))) {
				attributesNames.add(attribute.name);
			}
		} catch (KrnException e) {
			e.printStackTrace();
		}
		return attributesNames;
	}
	
	public static void setDesignerFrame (DesignerFrame frame) {
		designerFrame = frame;
	}
	
	public static DesignerFrame getDesignerFrame () {
		return designerFrame;
	}
	
	public static void resetUndoRedoActivity() {
		designerFrame.undoItem.setEnabled(false);
		designerFrame.redoItem.setEnabled(false);			
	}
}
