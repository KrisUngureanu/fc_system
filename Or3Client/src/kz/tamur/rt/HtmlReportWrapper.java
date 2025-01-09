package kz.tamur.rt;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

import com.cifs.or2.client.SwingWorker;

import kz.tamur.util.Funcs;

public class HtmlReportWrapper extends SwingWorker {
    private byte[] xml;
    private byte[] html;
    private String title;
    private long flowId;
    private ReportObserver taskTable;
    private boolean showAfterComplete;
    private File dir;
    
    private static byte[] TR = new byte[] {'<', 't', 'r', '>'};
    private static byte[] TRC = new byte[] {'<', '/', 't', 'r', '>'};
    
    private static byte[] TD = new byte[] {'<', 't', 'd', '>'};
    private static byte[] TDC = new byte[] {'<', '/', 't', 'd', '>'};

    public HtmlReportWrapper(byte[] xml, byte[] html, String title, boolean showAfterComplete, 
    		ReportObserver taskTable, long flowId, File dir) {
        this.xml = xml;
        this.html = html;
        this.title = title;
        this.showAfterComplete = showAfterComplete;
        this.taskTable = taskTable;
        this.flowId = flowId;
        this.dir = dir;
    }

    public Object construct() {
        print();
        return null;
    }

    public File print() {
        long time = System.currentTimeMillis();
        
        File res = null;
        try {
            System.out.println("loading xml..." + new Date());
            InputStream is = new ByteArrayInputStream(xml);
            SAXBuilder builder = new SAXBuilder();
            Element xml = builder.build(is).getRootElement();
            is.close();
            
	        String html = new String(this.html, "UTF-8");;

            res = viewReport(html, xml, title);

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Forming report time: " + (System.currentTimeMillis() - time));
        if (taskTable != null && flowId > 0) {
            taskTable.setReportComplete(flowId);
        }
        return res;
    }

	private File viewReport(String html, Element xml, String title) {
        try {
			File docFile = Funcs.createTempFile("xxx", ".html", dir);
	        docFile.deleteOnExit();
	        FileOutputStream os = new FileOutputStream(docFile);
	
	        int beg = html.indexOf("<!--");
	        int end = 0;
	        while (beg > -1) {
		        os.write(html.substring(end, beg).getBytes("UTF-8"));
				
		        end = html.indexOf("-->", beg + 4);
		
		        int end1 = html.indexOf("|", beg);
		        int diff = end1 - beg;
		        
		        if (diff < 15) {
		        	String type = html.substring(beg + 4, end1);
		        	if ("Field".equals(type)) {
		        		String id = html.substring(end1 + 4, end);
		        		Element e = (Element) XPath.selectSingleNode(xml,  "./Field[@id='" + id + "']");
		
		        		if (e != null) {
		        			os.write(e.getAttributeValue("str").getBytes("UTF-8"));
		        		}
		        	} else if ("Table".equals(type)) {
		        		int beg1 = end1;
		                end1 = html.indexOf("|", beg1 + 1);
		        		String tableId = html.substring(beg1 + 4, end1);
		        		List<Element> els = XPath.selectNodes(xml,  "./Column[@tableId='" + tableId + "']");
		        		if (els != null && els.size() > 0) {
		        			List<Element> cols = new ArrayList<Element>();
		        			String comment = html.substring(end1 + 6, end);
		                	
		        			end1 = 0;
		                	beg1 = 0;
		                	while ((beg1 = comment.indexOf("[Column|id=", end1)) > -1) {
		                		end1 = comment.indexOf("]", beg1);
		                		String colId = comment.substring(beg1 + 11, end1);
		                		Element col = getColumnByAttribute(els, "id", colId);
		                		cols.add(col);
		                	}
		                    if (cols.size() > 0 && cols.get(0) != null) {
		                    	int size = cols.get(0).getContentSize();
		                        for (int j=0; j<size; j++) {
			                        os.write(TR);
			                        os.write(TD);
			                        os.write(String.valueOf(j + 1).getBytes());
			                        os.write(TDC);
		
		                        	for (Element col : cols) {
				                        os.write(TD);
		                        		os.write(((Element)col.getContent(j)).getAttributeValue("str").getBytes("UTF-8"));
				                        os.write(TDC);
			                        }
			                        os.write(TRC);
		                        }
		                    }
		        		}
		        	}
		        } else {
			        os.write(html.substring(beg, end + 3).getBytes("UTF-8"));
		        }
		        end = end + 3;
		        beg = html.indexOf("<!--", end);
	        }	
	        os.write(html.substring(end).getBytes("UTF-8"));
	        os.close();
	
	        if (showAfterComplete) {
	            Runtime r = Runtime.getRuntime();
	            r.exec("cmd /c \"" + docFile.getAbsolutePath() + "\"");
	        }
	        
	        return docFile;
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return null;
	}

    private static Element getColumnByAttribute (List<Element> list, String attr, String id) {
        for (Element e : list) {
            if (id.equals(e.getAttributeValue(attr)))
                return e;
        }
        return null;
    }
}
