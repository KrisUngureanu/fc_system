package kz.tamur.web.common.table;

import kz.tamur.web.common.ServletUtilities;
import kz.tamur.web.common.TaskHelper;
import kz.tamur.web.common.TaskHelper.TaskTableModel;
import kz.tamur.web.common.WebSession;
import kz.tamur.web.controller.WebController;

import java.awt.Dimension;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cifs.or2.kernel.Activity;
import com.cifs.or2.util.Funcs;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

/**
 * Created by IntelliJ IDEA.
 * Date: 05.07.2006
 * Time: 10:52:45
 */
public class WebTable {
    private WebTableModel model;
    private WebTableCellRenderer renderer;
    private String className;
    private Log log;

    public void getJqHTML(StringBuilder out) {
    	out.append("<table id=\"taskTable\"></table>"); 
    	out.append("<div id=\"pager\"></div>");
    }

    public void getJqInit(StringBuilder out) {
    	String path = WebController.APP_PATH + "/main?trg=srv&cmd=xtsk&xml=1";
    	
    	out.append("$(\"#taskTable\").jqGrid({");
    	out.append("url:'").append(path).append("',");
    	out.append("datatype: 'xml',");
    	out.append("mtype: 'GET',");
    	
    	out.append("colNames:[");
        for (int i = 0; i < model.getColumnCount() - 1; i++) {
            String colName = model.getColumnName(i);
            out.append("'").append(colName).append("',");
        }
        String colName = model.getColumnName(model.getColumnCount() - 1);
        out.append("'").append(colName).append("'");
    	out.append("],");
    	
    	out.append("colModel :[");
        
    	for (int i = 0; i < model.getColumnCount() - 1; i++) {
            colName = model.getColumnName(i);
            out.append("{name:'").append(i)
            	.append("', index:'").append(i)
            	.append("', width:'").append(model.getColumnWidth(i));
        	if (model.getColumnAlign(i) != null && model.getColumnAlign(i).length() > 0)
        		out.append("', align:'").append(model.getColumnAlign(i));
            out.append("'},");
        }
        colName = model.getColumnName(model.getColumnCount() - 1);
        out.append("{name:'").append(model.getColumnCount() - 1)
	    	.append("', index:'").append(model.getColumnCount() - 1)
	    	.append("', width:'").append(model.getColumnWidth(model.getColumnCount() - 1));
    	if (model.getColumnAlign(model.getColumnCount() - 1) != null && model.getColumnAlign(model.getColumnCount() - 1).length() > 0)
    		out.append("', align:'").append(model.getColumnAlign(model.getColumnCount() - 1));

	    out.append("', align:'").append("right");
        out.append("'}");
    	out.append("],");
    	
    	out.append("pager: '#pager',");
    	out.append("rowNum:10,");
    	out.append("rowList:[10,20,30],");
    	out.append("sortname: '").append(model.getColumnName(1)).append("',");
    	out.append("sortorder: 'desc',");
    	out.append("viewrecords: true,");
    	out.append("height: '80%',");
    	out.append("caption: '������� �������'");
    	out.append("});");
    }

    public void getJqXML(Map<String, String> args, StringBuilder out) {
    	String page = args.get("page");
    	log.info("page: " + page);
    	String rows = args.get("rows"); 
    	log.info("rows: " + rows);
    	String sidx = args.get("sidx"); 
    	log.info("sidx: " + sidx);
    	String sord = args.get("sord"); 
    	log.info("sord: " + sord);
    	 
    	// if we not pass at first time index use the first column for the index or what you want
    	if(sidx == null) sidx = "1";
    	
    	int totalRecs = model.getRowCount();
    	int totalPgs = (totalRecs > 0 && rows != null) ? 1 + totalRecs / Integer.parseInt(rows) : 0;
    	
    	out.append("<rows>");
    	out.append("<page>").append(page).append("</page>");
    	out.append("<total>").append(totalPgs).append("</total>");
    	out.append("<records>").append(totalRecs).append("</records>");
    	 
    	int i0 = (Integer.parseInt(page) - 1) * Integer.parseInt(rows);
    	int i1 = i0 + Integer.parseInt(rows);
    	
    	// be sure to put text data in CDATA
    	for (int i = i0; i < i1 && i < model.getRowCount(); i++) {
        	out.append("<row id='").append(model.getRowId(i)).append("'>");
        	
            for (int c = 0; c < model.getColumnCount(); c++) {
                Object value = model.getValueAt(i, c);
                out.append("<cell>");
                
                if (renderer != null) {
                	//isSelected = rowSelected && model.getSelectedColumn() == j; 
                	out.append("<![CDATA[");
                    out.append(renderer.getTableCellRendererStringJq(
                            this, value, false, false, i, c
                    ));
                	out.append("]]>");
                } else {
                    if (value != null)
                        out.append(kz.tamur.util.Funcs.sanitizeHtml(value.toString()));
                }
            	out.append("</cell>");
            }
        	out.append("</row>");
    	}
    	out.append("</rows>");
    }
    
    public String getJSON(WebSession s) {
    	JsonObject res = new JsonObject();
    	JsonArray arr = new JsonArray();
    	res.add("processes", arr);

    	for (int i = model.getRowCount() - 1; i>=0; i--) {
            getRowJSON(i, arr, s);
        }
    	//общее количество заданий
    	res.add("tasksCount",s.getTaskHelper().getTasksCount());
        return res.toString();
    }

    public String getCountJSON(WebSession s) {
    	JsonObject res = new JsonObject();
    	res.add("total", model.getRowCount());
        return res.toString();
    }

    public void getRowJSON(int row, JsonArray arr, WebSession s) {
        TaskTableModel taskModel = (TaskTableModel) model;
        Activity act = taskModel.getActivityByRow(row);

        if (act != null) {
        	arr.add(s.getTaskHelper().activityAsJson(act));
        }
    }

    public void getHTML(StringBuilder out) {
        out.append("<table cellspacing=\"0\" class=\"").append(className).append("\" id=\"taskTable\"");
    	out.append(" selectedRow=\"").append(model.getSelectedRow()).append("\"");
    	out.append(" selectedCol=\"").append(model.getSelectedColumn()).append("\">").append(ServletUtilities.EOL);
    	out.append("<tr class=\"header\">").append(ServletUtilities.EOL);

        for (int i = 0; i < model.getColumnCount(); i++) {
            String colName = model.getColumnName(i);
            out.append("<td width=\"").append(model.getColumnWidth(i)).append("\">");
            out.append(colName);
            out.append("</td>").append(ServletUtilities.EOL);
        }

        out.append("</tr>").append(ServletUtilities.EOL);

        for (int i = 0; i < model.getRowCount(); i++) {
            getRowHTML(i, out);
        }

        out.append("</table>").append(ServletUtilities.EOL);
    }

    public void getRowHTML(int row, StringBuilder out) {
        getRowHTML(row, out, null);
    }
    
    public void getRowHTML(int row, StringBuilder out, Dimension size) {
        boolean isSelected = false;

        out.append("<tr id=\"");
        out.append(model.getRowId(row));
        out.append("\" onclick=\"selectRow(event, this);\" class=\"");
        boolean rowSelected = model.getSelectedRow() == row;
        if (rowSelected) {
            out.append("selected");
        } else {
            out.append("notselected");
        }
        out.append("\"");
        // задание размеров для модальных диалогов открываемых из данной строки таблицы
        if (size!=null) {
            out.append(" widthD='").append(size.width);
            out.append("' heightD='").append(size.height).append("'");
        } 
        out.append(" >").append(ServletUtilities.EOL);

        for (int j = 0; j < model.getColumnCount(); j++) {
            Object value = model.getValueAt(row, j);
            if (renderer != null) {
            	isSelected = rowSelected && model.getSelectedColumn() == j; 
                out.append(renderer.getTableCellRendererString(
                        this, value, isSelected, false, row, j
                ));
            } else {
                out.append("<td>");

                if (value != null)
                    out.append(kz.tamur.util.Funcs.sanitizeHtml(value.toString()));

                out.append("</td>").append(ServletUtilities.EOL);
            }
        }
        out.append("</tr>").append(ServletUtilities.EOL);
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

    public WebTableModel getModel() {
        return model;
    }

    public void setModel(WebTableModel model) {
        this.model = model;
    }

    public void setSelectedObject(Object obj) {
        model.setSelectedObject(obj);
    }

    public void setSelectedColumn(int col) {
        model.setSelectedColumn(col);
    }

    public void setClassName(String s) {
        this.className = s;
    }

    public List<Activity> getAutoStartedRows() {
        return ((TaskHelper.TaskTableModel)model).getAutoStartedRows();
    }

    public List<Activity> getOpenUIRows() {
        return ((TaskHelper.TaskTableModel)model).getOpenUIRows();
    }

	public void setLogPrefix(String prefix) {
		log = LogFactory.getLog(prefix + "." + getClass().getName());
	}
}
