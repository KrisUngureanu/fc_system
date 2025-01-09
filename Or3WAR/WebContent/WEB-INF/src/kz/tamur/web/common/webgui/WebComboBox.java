package kz.tamur.web.common.webgui;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ListSelectionModel;

import org.jdom.Element;

import kz.tamur.comps.Constants;
import kz.tamur.comps.OrFrame;
import kz.tamur.rt.adapters.ComboBoxAdapter;
import kz.tamur.rt.adapters.ComboBoxAdapter.OrComboItem;
import kz.tamur.util.Funcs;
import kz.tamur.web.common.JSONComponent;
import kz.tamur.web.controller.WebController;

import com.cifs.or2.kernel.KrnObject;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

/**
 * Created by IntelliJ IDEA.
 * User: Администратор
 * Date: 18.07.2006
 * Time: 18:46:35
 * To change this template use File | Settings | File Templates.
 */
public class WebComboBox extends WebComponent implements JSONComponent {

	private String text;
    private ComboBoxModel dataModel;
    private ListSelectionModel selectionModel;
    private boolean valueChanged = false;
    private boolean modelChanged = false;
	private int appearance = Constants.VIEW_SIMPLE_COMBO;
	private boolean comboSearch = true;

    public WebComboBox(Element xml, int mode, OrFrame frame, String id) {
		super(xml, mode, frame, id);
	}

    public void removeAllItems() {
    }

    public void setModel(ComboBoxModel aModel) {
        dataModel = aModel;
        modelChanged = true;
    }

    public void setSelectedItem(Object anObject) {
        if (dataModel != null) {
            dataModel.setSelectedItem(anObject);
        }
    }

    public void clearSelection() {
    	if (selectionModel != null) {
    		selectionModel.clearSelection();
    	}
    }
    
    public void setSelectedIndex(int anIndex) {
    	if (selectionModel != null) {
    		selectionModel.addSelectionInterval(anIndex, anIndex);
    	}
    	
        if (dataModel != null) {
            int size = dataModel.getSize();
            if (getSelectedIndex() != anIndex) {
                valueChanged = true;
                OrComboItem item = (OrComboItem)getItemAt(anIndex);
                sendChangeProperty("value", (item != null && item.getObject() != null) ? item.getObject().uid: "");
                sendChangeProperty("change", anIndex);
            }

            if (anIndex == -1) {
                setSelectedItem(null);
            } else if (anIndex < -1 || anIndex >= size) {
                throw new IllegalArgumentException("setSelectedIndex: " + anIndex + " out of bounds");
            } else {
                setSelectedItem(dataModel.getElementAt(anIndex));
            }
        }
    }

    public void setSelectedIndex2(int anIndex) {
    	if (selectionModel != null) {
    		selectionModel.addSelectionInterval(anIndex, anIndex);
    	}

    	if (dataModel != null) {
            int size = dataModel.getSize();
            if (getSelectedIndex() != anIndex) {
                sendChangeProperty("change", anIndex);
                if (WebController.EDITABLE_COMBO) {
                    int selectedIndex = getSelectedIndex();
                    ComboBoxAdapter.OrComboItem selItem = (ComboBoxAdapter.OrComboItem) getItemAt(selectedIndex);
                    String textVal = selItem != null ? selItem.toString() : "";
                    sendChangeProperty("value", Funcs.xmlQuote(textVal));
                }
            }
            if (anIndex == -1) {
                setSelectedItem(null);
            } else if (anIndex < -1 || anIndex >= size) {
                throw new IllegalArgumentException("setSelectedIndex: " + anIndex + " out of bounds");
            } else {
                setSelectedItem(dataModel.getElementAt(anIndex));
            }
        }
    }

    protected boolean setSelectedIndexDirectly(String value) {
    	if (dataModel != null) {
            int size = dataModel.getSize();
            int anIndex = (value != null && value.length() > 0) ? Integer.parseInt(value) : -1;
            
            boolean selected = selectionModel != null ? selectionModel.isSelectedIndex(anIndex) : false;
            
            if (selected) {
            	setSelectedItem(dataModel.getElementAt(anIndex));
            	return true;
            }
            
        	if (selectionModel != null) {
        		selectionModel.addSelectionInterval(anIndex, anIndex);
        		selectionModel.isSelectedIndex(anIndex);
        	}
            if (anIndex == -1) {
                setSelectedItem(null);
            } else if (anIndex < -1 || anIndex >= size) {
                throw new IllegalArgumentException("setSelectedIndex: " + anIndex + " out of bounds");
            } else {
                setSelectedItem(dataModel.getElementAt(anIndex));
            }
        }
    	return false;
    }

    protected void removeSelectedIndexDirectly(String value) {
        int anIndex = (value != null && value.length() > 0) ? Integer.parseInt(value) : -1;
        
    	if (selectionModel != null) {
    		selectionModel.removeSelectionInterval(anIndex, anIndex);
    	}
    }

    public int getItemCount() {
        return (dataModel != null) ? dataModel.getSize() : 0;
    }

    public Object getItemAt(int index) {
        return (dataModel != null) ? dataModel.getElementAt(index) : null;
    }

    public Object getSelectedItem() {
        return (dataModel != null) ? dataModel.getSelectedItem() : null;
    }

    public int getSelectedIndex() {
        if (dataModel == null)
            return -1;
        Object sObject = dataModel.getSelectedItem();
        int i, c;
        Object obj;

        for (i = 0, c = dataModel.getSize(); i < c; i++) {
            obj = dataModel.getElementAt(i);
            if (obj != null && obj.equals(sObject))
                return i;
        }
        return -1;
    }
    
    public int[] getSelectedIndicies() {
        ListSelectionModel sm = selectionModel;
        int iMin = sm.getMinSelectionIndex();
        int iMax = sm.getMaxSelectionIndex();

        if ((iMin < 0) || (iMax < 0)) {
            return new int[0];
        }

        int[] rvTmp = new int[1+ (iMax - iMin)];
        int n = 0;
        for(int i = iMin; i <= iMax; i++) {
            if (sm.isSelectedIndex(i)) {
                rvTmp[n++] = i;
            }
        }
        int[] rv = new int[n];
        System.arraycopy(rvTmp, 0, rv, 0, n);
        return rv;
    }

    protected int getIndexForObject(KrnObject o) {
        int count = getItemCount();
        if (count > 0) {
            if (o != null) {
                for (int i = 0; i < count; ++i) {
                    ComboBoxAdapter.OrComboItem item = (ComboBoxAdapter.OrComboItem) getItemAt(i);
                    KrnObject curr = item.getObject();
                    if (curr != null && o.id == curr.id) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    private int getIndexForTitle(String str) {
        int count = getItemCount();
        if (count > 0) {
            if (str != null) {
                for (int i = 0; i < count; ++i) {
                    ComboBoxAdapter.OrComboItem item = (ComboBoxAdapter.OrComboItem) getItemAt(i);
                    String curr = item.toString();
                    if (str.equals(curr)) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    public JsonObject getCellEditor(Object value, int row, int col, String tid, int width, JsonObject mainJSON)
            {
        JsonObject data = new JsonObject();
        mainJSON.add(tid, data);
        JsonObject style = new JsonObject();
        JsonObject property = new JsonObject();

        property.add("col", col);
        property.add("row", row);
        property.add("e", toInt(isEnabled()));
        if (width > 0) {
            style.add("width", width);
        }

        int selectedIndex = 0;
        if (value instanceof KrnObject) {
            selectedIndex = getIndexForObject((KrnObject) value);
        } else if (value instanceof String) {
            selectedIndex = getIndexForTitle((String) value);
        }
        JsonArray option = new JsonArray();
        for (int i = 0; i < getItemCount(); ++i) {
            ComboBoxAdapter.OrComboItem item = (ComboBoxAdapter.OrComboItem) getItemAt(i);

            JsonObject element = new JsonObject();
            element.add("value", i);
            if (i == selectedIndex) {
                element.add("selected", 1);
            }
            element.add("title", item.toString());
            option.add(element);

        }
        if (option.size() > 0) {
            property.add("option", option);
        }
      
        if (style.size() > 0) {
            data.add("st", style);
        }
        if (property.size() > 0) {
            data.add("pr", property);
        }
        return data;
    }

    public void setEnabled(boolean isEnabled) {
        super.setEnabled(isEnabled);
        if (WebController.EDITABLE_COMBO) {
            sendChangeProperty("enabled", isEnabled);
        }
    }

    public boolean getComboSeach() {
		return comboSearch;
	}
    
    public void setComboSearch(boolean comboSearch) {
		this.comboSearch = comboSearch;
	}
    
	public int getAppearance() {
		return appearance;
	}

	public void setAppearance(int a) {
		this.appearance = a;
		if (a == Constants.VIEW_LIST || a == Constants.VIEW_CHECKBOX_LIST) {
			selectionModel = new DefaultListSelectionModel();
			selectionModel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		}
	}
}
