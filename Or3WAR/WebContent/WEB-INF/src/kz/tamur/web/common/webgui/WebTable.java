package kz.tamur.web.common.webgui;

import static kz.tamur.web.common.WebUtils.colorToString;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import kz.tamur.comps.Constants;
import kz.tamur.comps.OrFrame;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.or3.client.comps.interfaces.OrColumnComponent;
import kz.tamur.rt.adapters.ColumnAdapter;
import kz.tamur.rt.adapters.ComboColumnAdapter;
import kz.tamur.web.common.JSONComponent;
import kz.tamur.web.common.ServletUtilities;
import kz.tamur.web.common.WebUtils;
import kz.tamur.web.common.table.WebTableModel;
import kz.tamur.web.component.OrWebCheckColumn;
import kz.tamur.web.component.OrWebComboColumn;
import kz.tamur.web.component.OrWebDateColumn;
import kz.tamur.web.component.OrWebDateField;
import kz.tamur.web.component.OrWebDocField;
import kz.tamur.web.component.OrWebDocFieldColumn;
import kz.tamur.web.component.OrWebFloatColumn;
import kz.tamur.web.component.OrWebFloatField;
import kz.tamur.web.component.OrWebHyperColumn;
import kz.tamur.web.component.OrWebHyperLabel;
import kz.tamur.web.component.OrWebHyperPopup;
import kz.tamur.web.component.OrWebImageColumn;
import kz.tamur.web.component.OrWebMemoColumn;
import kz.tamur.web.component.OrWebPopupColumn;
import kz.tamur.web.component.OrWebTableNavigator;
import kz.tamur.web.component.OrWebTreeColumn;
import kz.tamur.web.component.OrWebTreeField;
import kz.tamur.web.component.OrWebTreeTable2;

import org.jdom.Element;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

/**
 * Created by IntelliJ IDEA.
 * User: Erik
 * Date: 12.01.2007
 * Time: 11:05:27
 * To change this template use File | Settings | File Templates.
 */
public class WebTable extends WebComponent implements JSONComponent {
    protected WebTableModel model;
    protected WebTableCellRenderer renderer;
    private String className;
    private TableColumnModel columnModel;
    protected int[] selectedRows;
    protected boolean dataChanged;
    protected boolean sortChanged;
    protected int[] insertedRows;
    protected int[] deletedRows;
    protected java.util.List<Integer> updatedList;
    // protected int selectionMode = 0;
    protected boolean allSelected = false;
    protected int fill;
    protected boolean isSelected;
    
    String REQ_ERROR_COLOR = String.format("#%02x%02x%02x", 255, 204, 204);
    String EXPR_ERROR_COLOR = String.format("#%02x%02x%02x", 202, 247, 187);
    
    Color fontColor = null;
	private Map<String, String> colorsData;
    
    public WebTable(Element xml, int mode, OrFrame frame, String id) {
    	super(xml, mode, frame, id);
        this.columnModel = new DefaultTableColumnModel();
    }

    public String getData(Map<String, String> args) {
        int page = ServletUtilities.getInt("page", args, 1);
        int itemsOnPage = ServletUtilities.getInt("rp", args, 20);
        /*
         * String sortName = args.get("sortname");
         * String sortorder = args.get("sortorder");
         * String query = args.get("query");
         * String qtype = args.get("qtype");
         * 
         * System.out.printf("Params sortName: %s, sortorder: %s, page: %d", sortName, sortorder, page);
         */

        StringBuilder res = new StringBuilder("{");
        res.append("\"page\":").append(page);
        res.append(",\"total\":").append(100);
        res.append(",\"rows\": [");

        for (int i = 0; i < itemsOnPage; i++) {
            res.append(i == 0 ? "," : "");
            res.append("{\"id\":\"").append(i).append("\",\"cell\":[");
            for (int j = 0; j < model.getColumnCount(); j++) {
                res.append(j == 0 ? "," : "");
                res.append("\"c").append(i).append("_").append(j).append("\"");
            }
            res.append("]}");
        }
        res.append("]}");
        return res.toString();
    }

    public String getData(int page, int itemsOnPage, int viewType) {
    	int count = model.getRowCount();
    	
        int begin = page > 0 ? (page - 1) * itemsOnPage : 0;
        int end = itemsOnPage > 0 ? page * itemsOnPage : count;

        if (viewType == Constants.TABLE_VIEW_COMMON) {
	    	JsonObject res = new JsonObject();
	    	res.add("total", count);
	    	
	    	JsonArray arr = new JsonArray();
	    	res.add("rows", arr);
	    	
	        for (int i = begin; i < end && i<count; i++) {
	            JsonObject row = getRowData(i);
	            arr.add(row);
	        }
	
	        return res.toString();
    	} else {
    		StringBuilder res = new StringBuilder();
	        for (int i = begin; i < end && i<count; i++) {
	        	res.append(getIconRowData(i));
	        }
    		return res.toString();
    	}
    }

    public JsonObject getRowData(int r) {
        JsonObject row = new JsonObject();

        OrColumnComponent col;
        String uuid = null;
        for (int j = 0; j < model.getColumnCount(); j++) {
            boolean isMod = false;
            col = ((kz.tamur.or3.client.comps.interfaces.OrTableModel) model).getColumn(j);
            if (col == null || col.getUUID() == null) {
                continue;
            }
            uuid = col.getUUID();
            JsonObject clm = new JsonObject();
            JsonObject styleRow = new JsonObject();
            JsonArray data = new JsonArray();
            String background = "";
            String foreground = "";
            data.add(clm);
            styleRow.add("styleRow", data);
            ColumnAdapter adapter = col.getAdapter();
            if (adapter != null /*&& adapter.getDataRef() != null*/ && r > -1) {
                Integer state = adapter.getState(r);
                if (state == Constants.REQ_ERROR && adapter.getEnterDB() == Constants.BINDING) {
                    background = REQ_ERROR_COLOR;
                } else if (state == Constants.EXPR_ERROR) {
                    background = EXPR_ERROR_COLOR;
                } else if (adapter.isBackgroundColorSet()) {
                	background = adapter.getColumnBackgroundColorStr();
                } else if (adapter.isBackColorCalculated()) {
                    Color c = adapter.getColumnBackgroundColor(r, j);
                    if (!c.equals(Color.white)) {
                        background = colorToString(c);
                    }
                }

                if (adapter.isFontColorCalculated()) {
                    Color color = adapter.getColumnFontColor(r, j);
                    foreground = (color != null) ? colorToString(color) : "";
                } else if (model.isRowFontColorCalc()) {
                    Color color = model.getRowFontColor(r);
                    foreground = (color != null) ? colorToString(color) : "";
                } else if (adapter.isFontColorSet()) {
                	String color = adapter.getColumnFontColorStr();
                	foreground = (color != null && !"#000000".equals(color)) ? color : "";
                } else if (!adapter.isFontColorCalculated()) {
                    Color color = adapter.getColumnDefaultForegroundColor(j);
                    foreground = (color != null && !Color.black.equals(color)) ? colorToString(color) : "";
                }

                Font font = adapter.getColumnFont(r, j);
                if (font != null) {
                    WebUtils.appendFontStyle(font, clm);
                    isMod = true;
                } else if (adapter.getColumnFont() != null) {
                    WebUtils.appendFontStyle(adapter.getColumnFont(), clm);
                    isMod = true;
                }

                if (!background.isEmpty()) {
                    clm.add("bcg", background);
                    isMod = true;
                }
                if (!foreground.isEmpty()) {
                    clm.add("frg", foreground);
                    isMod = true;
                }
            } else if (colorsData != null) {
            	background = colorsData.get(r + "bcg"  + j);
                if (background != null) {
                    clm.add("bcg", background);
                    isMod = true;
                }
                foreground = colorsData.get(r + "frg"  + j);
                if (foreground != null) {
                    clm.add("frg", foreground);
                    isMod = true;
                }
            }

            Object value = model.getValueAt(r, j);
            if (col instanceof OrWebDateColumn) {
                value = ((OrWebDateField) ((OrWebDateColumn) col).getEditor()).toString(value);
            } else if (col instanceof OrWebCheckColumn) {
                value = (value instanceof Number) ? (((Number) value).intValue() == 1 ? "x" : "") : "";
            } else if (col instanceof OrWebTreeColumn) {
                value = ((OrWebTreeField) ((OrWebTreeColumn) col).getEditor()).valueToString(value);
                if (value == "" && model.getColumnCount() == 1) {
                	PropertyNode editableProperty = ((OrWebTreeColumn) col).getProperties().getChild("pov").getChild("activity").getChild("editable");
                	boolean editable = ((OrWebTreeColumn) col).getPropertyValue(editableProperty).booleanValue();
                	if (!editable) {
                		String tipForInput =  ((OrWebTreeColumn) col).getTipForInput();
                		if (tipForInput != null && tipForInput.length() > 0) {
                        	value = tipForInput;
                            clm.add("frg", colorToString(Color.WHITE));
                            isMod = true;
                		}
                	}
                } else {
					if (!isMod) {
						clm.add("frg", colorToString(Color.BLACK));
						isMod = true;
					}
                }
            } else if (col instanceof OrWebDocFieldColumn) {
                switch (((OrWebDocField) col.getEditor()).getAdapter().getAction()) {
                case Constants.DOC_UPDATE:
                    value = "<i class='fam-attach'></i>";
                    break;
                case Constants.DOC_EDIT: // TODO временно, пока не определились как в веб редактирвать файлы
                case Constants.DOC_VIEW:
                    if (value != null) {
                        StringBuilder v = new StringBuilder().append("<a onclick=\"downloadFile(event, '").append(uuid)
                                .append("',").append(r).append(",").append(j)
                                .append(");\"><img src=\"media/img/DocField.gif\" /></a>");
                        value = v.toString();
                    }
                    break;
                }
            } else if (col instanceof OrWebHyperColumn) {
                OrWebHyperLabel label = (OrWebHyperLabel) ((OrWebHyperColumn) col).getEditor();
                StringBuilder sb = new StringBuilder();
                sb.append("<a href='' class='hyper'");
                sb.append(" id='").append(label.getUUID()).append("'");
                sb.append(" row='").append(r).append("'");
                sb.append(" posIcon='").append(label.getPosIcon()).append("'");
                sb.append(">");

                if (label.isVisibleArrow()) {
                    String path =  label.getIconPath() != null && !label.getIconPath().isEmpty() ? label.getIconPath() : label.getIconFullPath();
                    if("VSlider".equals(path)) {
                        path+=".gif";  
                    }
                    sb.append("<img src=\"").append("media/img/").append(path).append("\"/>");
                }
                if (value != null)
                	sb.append(value);
                else if (label.getText() != null && label.getText().length() > 0)
                	sb.append(label.getText());
                else
                	sb.append("-");
                
                sb.append("</a>");
                value = sb.toString();
            }  else if (col instanceof OrWebFloatColumn) {
                OrWebFloatField editor = (OrWebFloatField) ((OrWebFloatColumn) col).getEditor();
                value = editor.getValueAsText(value instanceof Number ? ((Number)value).doubleValue() : null);
            } else if (col instanceof OrWebImageColumn) {
            	StringBuilder sb = new StringBuilder();
            	if (value instanceof byte[])
            		sb.append("<img src='data:image/png;base64,").append(kz.tamur.util.Base64.encodeBytes((byte[])value)).append("'/>");
            	else if (value instanceof String) {
            		sb.append("<img src='data:image/png;base64,").append(kz.tamur.util.Base64.encodeFromFile((String)value)).append("'/>");
            	} else if (value instanceof File) {
            		sb.append("<img src='data:image/png;base64,").append(kz.tamur.util.Base64.encodeFromFile(((File)value).getAbsolutePath())).append("'/>");
            	} else if (value instanceof StringBuilder) {
            		sb = (StringBuilder)value;
            	}
                
            	value = sb.toString();
            } else if (col instanceof OrWebPopupColumn) {
            	OrWebHyperPopup editor  = (OrWebHyperPopup) ((OrWebPopupColumn) col).getEditor();
            	if (editor.isIconVisible) {
                    StringBuilder sb = new StringBuilder();
	            	String base64Icon = editor.getBase64Icon();
	            	if (base64Icon == null) {
	            		sb.append("<i class='fam-bullet-green'></i>").append(value != null ? value.toString() : "");
	            	} else {
	            		sb.append("<img src='data:image/png;base64,").append(base64Icon).append("'/>").append(value);
	            	}
	                value = sb.toString();
            	}
            }
            
            String val = value != null ? value.toString() : "";
            val = val.replace("\\n", "\n");
            
            if (col instanceof OrWebMemoColumn && ((OrWebMemoColumn) col).isShowTextAsXML()) {
        		val = val.replace("<", "&lt;");
        		val = val.replace(">", "&gt;");
            }
            
            if (col instanceof OrWebComboColumn) {
                row.add(uuid + "-title", val);
                row.add(uuid, ((ComboColumnAdapter) col.getAdapter()).getIndexAt(r));
            } else {
                row.add(uuid, val);
            }

            if (isMod) {
            	clm.add("hsh", clm.hashCode());
                clm.add("index", r);
            	getWebSession().addChange(getInterfaceId(), uuid, styleRow);
            }
        }
        return row;
    }
    
    public String getIconRowData(int r) {
        StringBuilder row = new StringBuilder();
    	row.append("<div class=\"irow\">");

        OrColumnComponent col;
        String uuid = null;
        for (int j = 0; j < model.getColumnCount(); j++) {
            boolean isMod = false;
            col = ((kz.tamur.or3.client.comps.interfaces.OrTableModel) model).getColumn(j);
            uuid = col.getUUID();
            
            if (col == null || uuid == null) {
                continue;
            }
            String background = "";
            String foreground = "";
            
            StringBuilder style = new StringBuilder();
            
            ColumnAdapter adapter = col.getAdapter();
            if (adapter != null && adapter.getDataRef() != null && r > -1) {
                Integer state = adapter.getState(r);
                if (state == Constants.REQ_ERROR && adapter.getEnterDB() == Constants.BINDING) {
                    background = REQ_ERROR_COLOR;
                } else if (state == Constants.EXPR_ERROR) {
                    background = EXPR_ERROR_COLOR;
                } else if (adapter.isBackgroundColorSet()) {
                	background = adapter.getColumnBackgroundColorStr();
                } else if (adapter.isBackColorCalculated()) {
                    Color c = adapter.getColumnBackgroundColor(r, j);
                    if (!c.equals(Color.white)) {
                        background = colorToString(c);
                    }
                }

                if (adapter.isFontColorCalculated()) {
                    Color color = adapter.getColumnFontColor(r, j);
                    foreground = (color != null) ? colorToString(color) : "";
                } else if (model.isRowFontColorCalc()) {
                    Color color = model.getRowFontColor(r);
                    foreground = (color != null) ? colorToString(color) : "";
                } else if (adapter.isFontColorSet()) {
                	String color = adapter.getColumnFontColorStr();
                	foreground = (color != null && !"#000000".equals(color)) ? color : "";
                } else if (!adapter.isFontColorCalculated()) {
                    Color color = adapter.getColumnDefaultForegroundColor(j);
                    foreground = (color != null && !Color.black.equals(color)) ? colorToString(color) : "";
                }

                Font font = adapter.getColumnFont(r, j);
                if (font != null) {
                	if (!"dialog".equalsIgnoreCase(font.getName()))
                		style.append("font-family:").append(font.getName()).append(";");
		            if (font.getSize() > 0)
		            	style.append("font-size:").append(font.getSize()).append("px;");
		            if (font.isBold())
		            	style.append("font-weight:bold;");
		            if (font.isItalic())
		            	style.append("font-style:italic;");
                    isMod = true;
                } else if (adapter.getColumnFont() != null) {
                	font = adapter.getColumnFont();
                	if (!"dialog".equalsIgnoreCase(font.getName()))
                		style.append("font-family:").append(font.getName()).append(";");
		            if (font.getSize() > 0)
		            	style.append("font-size:").append(font.getSize()).append("px;");
		            if (font.isBold())
		            	style.append("font-weight:bold;");
		            if (font.isItalic())
		            	style.append("font-style:italic;");
                    isMod = true;
                }

                if (!background.isEmpty()) {
                	style.append("background-color:").append(background).append(";");
                    isMod = true;
                }
                if (!foreground.isEmpty()) {
                	style.append("color:").append(foreground).append(";");
                    isMod = true;
                }
            } else if (colorsData != null) {
            	background = colorsData.get(r + "bcg"  + j);
                if (background != null) {
                	style.append("background-color:").append(background).append(";");
                    isMod = true;
                }
                foreground = colorsData.get(r + "frg"  + j);
                if (foreground != null) {
                	style.append("color:").append(foreground).append(";");
                    isMod = true;
                }
            }

            Object value = model.getValueAt(r, j);
            if (col instanceof OrWebDateColumn) {
                value = ((OrWebDateField) ((OrWebDateColumn) col).getEditor()).toString(value);
            } else if (col instanceof OrWebCheckColumn) {
                value = (value instanceof Number) ? (((Number) value).intValue() == 1 ? "x" : "") : "";
            } else if (col instanceof OrWebTreeColumn) {
                value = ((OrWebTreeField) ((OrWebTreeColumn) col).getEditor()).valueToString(value);
            } else if (col instanceof OrWebDocFieldColumn) {
                switch (((OrWebDocField) col.getEditor()).getAdapter().getAction()) {
                case Constants.DOC_UPDATE:
                    value = "<i class='fam-attach'></i>";
                    break;
                case Constants.DOC_EDIT: // TODO временно, пока не определились как в веб редактирвать файлы
                case Constants.DOC_VIEW:
                    if (value != null) {
                        StringBuilder v = new StringBuilder().append("<a onclick=\"downloadFile(event, '").append(uuid)
                                .append("',").append(r).append(",").append(j)
                                .append(");\"><img src=\"media/img/DocField.gif\" /></a>");
                        value = v.toString();
                    }
                    break;
                }
            } else if (col instanceof OrWebHyperColumn) {
                OrWebHyperLabel label = (OrWebHyperLabel) ((OrWebHyperColumn) col).getEditor();
                StringBuilder sb = new StringBuilder();
                sb.append("<a href='' class='hyper'");
                sb.append(" id='").append(label.getUUID()).append("'");
                sb.append(" row='").append(r).append("'");
                sb.append(" posIcon='").append(label.getPosIcon()).append("'");
                sb.append(">");

                if (label.isVisibleArrow()) {
                    String path =  label.getIconPath() != null && !label.getIconPath().isEmpty() ? label.getIconPath() : label.getIconFullPath();
                    if("VSlider".equals(path)) {
                        path+=".gif";  
                    }
                    sb.append("<img src=\"").append("media/img/").append(path).append("\"/>");
                }
                if (value != null)
                	sb.append(value);
                else if (label.getText() != null && label.getText().length() > 0)
                	sb.append(label.getText());
                else
                	sb.append("-");
                
                sb.append("</a>");
                value = sb.toString();
            }  else if (col instanceof OrWebFloatColumn) {
                OrWebFloatField editor = (OrWebFloatField) ((OrWebFloatColumn) col).getEditor();
                value = editor.getValueAsText(value instanceof Number ? ((Number)value).doubleValue() : null);
            }  else if (col instanceof OrWebImageColumn) {
            	StringBuilder sb = new StringBuilder();
            	if (value instanceof byte[])
            		sb.append("<img src='data:image/png;base64,").append(kz.tamur.util.Base64.encodeBytes((byte[])value)).append("'/>");
            	else if (value instanceof String) {
            		sb.append("<img src='data:image/png;base64,").append(kz.tamur.util.Base64.encodeFromFile((String)value)).append("'/>");
            	} else if (value instanceof File) {
                		sb.append("<img src='data:image/png;base64,").append(kz.tamur.util.Base64.encodeFromFile(((File)value).getAbsolutePath())).append("'/>");
            	}
                
            	value = sb.toString();
            }  else if (col instanceof OrWebPopupColumn) {
            	StringBuilder sb = new StringBuilder();
            	sb.append("<a href=\"javascript:openPopup('").append(getUUID()).append("', '").append(r).append("', '").append(uuid).append("');\">").append(value).append("</a>");
            	value = sb.toString();
            }
            
            String val = value != null ? value.toString() : "";
            val = val.replace("\\n", "\n");
            
            if (col.getTitle().length() > 0)
            	val = col.getTitle() + ": " + value;

        	row.append("<div field='").append(uuid).append("'");
            if (style.length() > 0) {
            	row.append(" style=\"").append(style).append("\"");
            }
        	row.append(">").append(val).append("</div>");
        }
    	row.append("</div>");
        return row.toString();
    }

    public void setColorsData(Map<String, String> colorsData) {
		this.colorsData = colorsData;
	}

    protected void sortChanged() {
/*        JsonObject[] headers = new JsonObject[model.getColumnCount()];
        for (int i = 0; i < model.getColumnCount(); i++) {
            JsonObject header = new JsonObject();
            String colName = model.getColumnName(i);
            colName = colName.replaceAll("@", "<br/>");
            header.add("width", model.getColumnWidth(i));
            String icon = model.getColumnIconName(i);
            if (icon != null) {
                JsonObject img = new JsonObject();
                img.add("src", "images/" + icon + ".gif");
                header.add("img", img);
            }
            header.add("title", colName);
            headers[i] = header;
        }
        sendChangeProperty("headers", headers);
*/    }

    protected void dataChanged(String zebra1, String zebra2) {
        deletedRows = insertedRows = null;
        updatedList = null;

        String selRows = getSelectedRowsString();
        if (selRows != null) {
            sendChangeProperty("selectedRows", selRows);
        }
        
        if (this instanceof OrWebTreeTable2) {
            ((OrWebTreeTable2) this).setSingleClick(false);
        }

        JsonObject[] rows = new JsonObject[model.getRowCount()];
        for (int i = 0; i < model.getRowCount(); i++) {
            rows[i] = getRowJSON(i, zebra1, zebra2);
        }
        sendChangeProperty("rows", rows);
    }

    protected void updatedList(String zebra1, String zebra2) {
        deletedRows = insertedRows = null;
        updatedList = null;
        List<Integer> temp = updatedList != null ? new ArrayList<Integer>(updatedList) : new ArrayList<Integer>();
        updatedList = null;
        JsonArray rows = new JsonArray();
        for (Integer row : temp) {
            if (row > -1 && row < getRowCount())
                rows.add(getRowJSON(row, zebra1, zebra2));
        }
        sendChangeProperty("rows", rows);
    }

    protected void deletedRows(String zebra1, String zebra2) {
        deletedRows = null;
        sendChangeProperty("deletedRows", deletedRows);
    }

    protected void insertedRows(String zebra1, String zebra2) {
        JsonArray rows = new JsonArray();
        for (Integer row : insertedRows) {
            if (row > -1 && row < getRowCount())
                rows.add(getRowJSON(row, zebra1, zebra2));
        }
        insertedRows = null;
        sendChangeProperty("insertedRows", rows);
    }

    protected boolean isRowSelected(int row) {
        if (selectedRows != null) {
            for (int i : selectedRows) {
                if (row == i) {
                    return true;
                }
            }
        }
        return false;
    }

    public JsonObject getRowJSON(int index, String zebra1, String zebra2) {
        JsonObject row = new JsonObject();
        row.add("index", index);
        String zebra = index % 2 == 0 ? zebra1 : zebra2;
        boolean rowSelected = isRowSelected(index);
        row.add("selected", rowSelected);
        if (!rowSelected) {
            Color c = model.getRowBgColor(index);
            if (!c.equals(Color.white)) {
                row.add("backgroundColor", colorToString(c));
            } else if (zebra != null) {
                row.add("backgroundColor", zebra);
            }
        }
        if (model.isRowFontColorCalc()) {
            Color c = model.getRowFontColor(index);
            c = c != null ? c : Color.black;
            row.add("color", colorToString(c));
        }
        JsonObject[] columns = new JsonObject[model.getColumnCount()];
        boolean emptyRow = true;
        for (int j = 0; j < model.getColumnCount(); j++) {
            JsonObject column = new JsonObject();
            row.add("index", j);
            Object value = model.getValueAt(index, j);
            if (renderer != null) {
                boolean isSelected = rowSelected && model.getSelectedColumn() == j;
                WebComponent comp = renderer.getTableCellRenderer(this, value, isSelected, false, index, j);
                if (comp != null) {
                    Font font = model.getFont(index, j);
                    if (font != null)
                        comp.setFont(font);

                    if (model.isFontColorCalculated(j)) {
                        comp.setForeground(model.getColumnFontColor(index, j));
                    }
                }
                renderer.getTableCellRendererString(this, value, isSelected, false, index, j, column);
            } else {
                if (value != null) {
                    column.add("value", value);
                }
            }
            if (emptyRow && value != null && value.toString().length() > 0) {
                emptyRow = false;
            }
            columns[j] = column;
        }
        row.add("columns", columns);
        if (emptyRow) {
            row.add("height", 16);
        }
        return row;
    }

    public int getRowForObject(Object obj) {
        return model.getRowForObject(obj);
    }

    public WebTableCellRenderer getRenderer() {
        return renderer;
    }

    public void setRenderer(WebTableCellRenderer renderer) {
        this.renderer = renderer;
    }

    public TableModel getModel() {
        return (TableModel) model;
    }

    public void setModel(WebTableModel model) {
        this.model = model;
    }

    public void setSelectedObject(Object obj) {
        model.setSelectedObject(obj);
    }

    public void setClassName(String s) {
        this.className = s;
    }

    public TableColumnModel getColumnModel() {
        return columnModel;
    }

    public void setColumnModel(TableColumnModel columnModel) {
        this.columnModel = columnModel;
    }

    protected void setAutoResizeMode(int mode) {
    }

    protected void setAutoCreateColumnsFromModel(boolean b) {
    }

    protected void setGridColor(java.awt.Color c) {
    }

    public void setSelectedRow(int row) {
        selectedRows = new int[] { row };

/*        int indx = -1;
        if (selectedRows != null) {
            for (int i = 0; i < selectedRows.length; ++i) {
                if (selectedRows[i] == row) {
                    indx = i;
                    break;
                }
            }
        }
        isSelected = indx == -1 || selectedRows == null;
        if (isSelected) {
            selectedRows = new int[] { row };
        } else {
            if (selectedRows.length > 1) {
                int[] selectedRows_ = new int[selectedRows.length - 1];
                for (int i = 0; i < selectedRows_.length; ++i) {
                    if (i != indx) {
                        selectedRows_[i] = selectedRows[i];
                    }
                }
                selectedRows = Arrays.copyOf(selectedRows_, selectedRows_.length);
            } else {
                selectedRows = new int[] {};
            }
        }
*/        String selRows = getSelectedRowsString();
        if (selRows != null) {
            sendChangeProperty("selectedRows", selRows);
        }
    }

    public void setSelectedRows(int[] rows, boolean selfChange) {
        selectedRows = rows;
        String selRows = getSelectedRowsString();
        if (!selfChange && selRows != null) {
            sendChangeProperty("selectedRows", selRows);
        } else if (selfChange) {
        	removeChange("pr.selectedRows");
        }
    }

    public String getSelectedRowsString() {
        if (selectedRows != null && selectedRows.length > 0) {
            StringBuilder b = new StringBuilder();
            for (int row : selectedRows) {
                b.append(row).append(",");
            }
            return b.substring(0, b.length() - 1);
        } else
            return null;
    }

    public int[] getSelectedRows() {
        return selectedRows;
    }

    public int getRowCount() {
        return (model != null) ? model.getRowCount() : 0;
    }

    public int getColumnCount() {
        return (model != null) ? model.getColumnCount() : 0;
    }

    public Object getValueAt(int row, int col) {
        return (model != null) ? model.getValueAt(row, col) : null;
    }

    public void setSelectedColumn(int col) {
        model.setSelectedColumn(col);
        sendChangeProperty("selectedColumn", col);
    }

    // OrWebTableNavigator navi, String zebraColor1, String zebraColor2, StringBuilder out
    public JsonObject putJSON(boolean isSend) {
        JsonObject obj = getJSON(null, null, null);
        sendChange(obj, isSend);
        return obj;

    }

    public JsonObject getJSON(OrWebTableNavigator navi, String zebraColor1, String zebraColor2) {
        JsonObject obj = addJSON();
        JsonObject property = new JsonObject();

        String overflow = ("100%".equals(sWidth) && "100%".equals(sHeight)) ? "hidden" : "auto";

        int width = 0;
        for (int i = 0; i < model.getColumnCount(); i++) {
            width += Integer.parseInt(model.getColumnWidth(i));
        }

        if (navi != null) {
            navi.setRowCount(getRowCount());
            property.add("navi", navi.putJSON(false));
        }
        property.add("overflow", overflow);

        if (getWidth() > 0) {
            property.add("min-width", getWidth());
        }
        if (getHeight() > 0) {
            property.add("min-height", getHeight());
        }

        switch (fill) {
        case GridBagConstraints.NONE:
        default:
            if (getMaxWidth2() > 0) {
                property.add("max-width", getMaxWidth2());
            }
            if (getMaxHeight2() > 0) {
                property.add("max-height", getMaxHeight2());
            }
            break;
        case GridBagConstraints.HORIZONTAL:
            if (getMaxHeight2() > 0) {
                property.add("max-height", getMaxHeight2());
            }
            break;
        case GridBagConstraints.VERTICAL:
            if (getMaxWidth2() > 0) {
                property.add("max-width", getMaxWidth2());
            }
            break;
        case GridBagConstraints.BOTH:
            break;
        }

        String selRows = getSelectedRowsString();
        if (selRows != null) {
            property.add("selectedRows", selRows);
        }

        property.add("selectedCol", model.getSelectedColumn());
        property.add("width", width);
        if (property.size() > 0) {
            obj.add("pr", property);
        }
        return obj;
    }
}
