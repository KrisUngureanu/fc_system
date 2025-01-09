package kz.tamur.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractListModel;

import org.jdom.Attribute;
import org.jdom.CDATA;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Text;
import org.jdom.input.SAXBuilder;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;

public class XmlParserUtil {
	
	Map<String, String> msgs = new HashMap<String, String>();
	Map<String, String> comps = new HashMap<String, String>();
	long langId = Kernel.instance().getUser().getIfcLang().id;
	private static Pattern uidInScriptPttrn = Pattern.compile("\\$Objects\\s*\\.\\s*getObject\\s*\\(\\s*\"([^\"]+)\"");

	/**
	 * Возвращает элементы, которые содержит объект или ссылается на них
	 * для построения дерева в "Управление процессами"
	 */
	public static List<String> getObjectElements(KrnObject krnObj) throws KrnException{
		Kernel krn = Kernel.instance();

		String elementUID = "";
		List<String> serviceElements = new ArrayList<String>();
		long langId = krn.getUser().getIfcLang().id;

		if(krnObj.classId == krn.getClassByName("ProcessDef").id){
			byte[] processConfig = krn.getBlob(krnObj, "config", 0, langId, 0);
			if (processConfig.length > 0) {
				try {
					ByteArrayInputStream is = new ByteArrayInputStream(processConfig);
					SAXBuilder saxBuild = new SAXBuilder();
					org.jdom.Element configRoot = saxBuild.build(is).getRootElement();
					is.close();

					org.jdom.filter.ElementFilter filter = new org.jdom.filter.ElementFilter("KRNprocess");
					org.jdom.Element krnProcess = null;
					for(Iterator it = configRoot.getDescendants(filter); it.hasNext();)	{
						krnProcess = (Element)it.next();
						elementUID = krnProcess.getText().trim();
						if(!serviceElements.contains(elementUID)) serviceElements.add(elementUID);
					}

					filter = new org.jdom.filter.ElementFilter("KRNprocessUi");
					krnProcess = null;
					for(Iterator it = configRoot.getDescendants(filter); it.hasNext();) {
						krnProcess = (Element)it.next();
						elementUID = krnProcess.getText().trim();
						if(!serviceElements.contains(elementUID)) serviceElements.add(elementUID);
					}

					//эементы, которые в формулах
					String str = new String(processConfig, "UTF-8").replaceFirst("(?s)<conflict>.*</conflict>", "");
					Matcher match = uidInScriptPttrn.matcher(str);
					while (match.find()) {
						if(!serviceElements.contains(match.group(1))) serviceElements.add(match.group(1));
					}					
				} catch (JDOMException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			byte [] processDiagram = krn.getBlob(krnObj, "diagram", 0, langId, 0);
			if(processDiagram.length > 0){
				try {
					ByteArrayInputStream is = new ByteArrayInputStream(processDiagram);
					SAXBuilder saxBuild = new SAXBuilder();
					org.jdom.Element diagramRoot = saxBuild.build(is).getRootElement();
					is.close();
					for (Object node : diagramRoot.getChildren("node")) {
						for (Object child : ((Element)node).getChildren()) {
							if(((Element)child).getAttributeValue("name") == "KRNprocess" || ((Element)child).getAttributeValue("name") == "KRNprocessUi"){ 
								if(!serviceElements.contains(((Element)child).getValue().trim()))
									serviceElements.add(((Element)child).getValue().trim());
							}
						}			
					}
					//элементы, которые в формулах
					String str = new String(processDiagram, "UTF-8").replaceFirst("(?s)<property name=\"conflict\">.*?</property>", "");
					Matcher match = uidInScriptPttrn.matcher(str);
					while (match.find()) {
						if(!serviceElements.contains(match.group(1))) serviceElements.add(match.group(1));
					}	
				} catch (JDOMException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}else if(krnObj.classId == krn.getClassByName("UI").id){
			byte[] interfaceConfig = krn.getBlob(krnObj, "config", 0, langId, 0);
			if (interfaceConfig.length > 0) {
				try {
					ByteArrayInputStream is = new ByteArrayInputStream(interfaceConfig);
					SAXBuilder saxBuild = new SAXBuilder();
					org.jdom.Element configRoot = saxBuild.build(is).getRootElement();
					is.close();

					org.jdom.filter.ElementFilter filter = new org.jdom.filter.ElementFilter("KrnObject");
					org.jdom.Element krnUI = null;
					for(Iterator it = configRoot.getDescendants(filter); it.hasNext();)	{
						krnUI = (Element)it.next();
						if(krnUI.getAttribute("class").getValue().trim().equals("UI")){
							elementUID = krnUI.getAttributeValue("id").trim();
							if(!serviceElements.contains(elementUID)) serviceElements.add(elementUID);
						}
					}

					filter = new org.jdom.filter.ElementFilter("Filter");
					krnUI = null;
					for(Iterator it = configRoot.getDescendants(filter); it.hasNext();) {
						krnUI = (Element)it.next();
						elementUID = krnUI.getAttributeValue("id").trim();
						if(!serviceElements.contains(elementUID)) serviceElements.add(elementUID);
					}

					filter = new org.jdom.filter.ElementFilter("Report");
					krnUI = null;
					for(Iterator it = configRoot.getDescendants(filter); it.hasNext();) {
						krnUI = (Element)it.next();
						elementUID = krnUI.getAttributeValue("id").trim();
						if(!serviceElements.contains(elementUID)) serviceElements.add(elementUID);
					}
					//элементы, которые в формулах					
					String str = new String(interfaceConfig, "UTF-8");
					Matcher match = uidInScriptPttrn.matcher(str);
					while (match.find()) {
						if(!serviceElements.contains(match.group(1))) serviceElements.add(match.group(1));
					}
				} catch (JDOMException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}else if(krnObj.classId == krn.getClassByName("ReportPrinter").id){
			byte[] data = krn.getBlob(krnObj, "data", 0, langId, 0);
			if(data.length > 0){
				try {
					ByteArrayInputStream is = new ByteArrayInputStream(data);
					SAXBuilder saxBuild = new SAXBuilder();
					org.jdom.Element diagramRoot = saxBuild.build(is).getRootElement();
					is.close();
					for (Object node1 : diagramRoot.getChildren()) {
						org.jdom.Attribute filter = ((Element)node1).getAttribute("filter");
							if(filter != null){ 
								if(!serviceElements.contains(filter.getValue()))
									serviceElements.add(filter.getValue());
							}
					}
				} catch (JDOMException e) {
					e.printStackTrace();
				}catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return serviceElements;
	}	

    
    
    
	public static Map<String, String> parseUIXml(KrnObject object, int lang, String pattern) throws Exception {
		
		byte [] config = Kernel.instance().getBlobs(object, "config", lang, 0)[0];
		byte [] strings = Kernel.instance().getBlobs(object, "strings", lang, 0)[0];
		
		ByteArrayInputStream is = new ByteArrayInputStream(config);
		SAXBuilder sb = new SAXBuilder();
		Element re = sb.build(is).getRootElement();
		XmlParserUtil xmlParserUtil = new XmlParserUtil();
		xmlParserUtil.loadMessages(strings);
		xmlParserUtil.parseUIElement(re, pattern);
		is.close();
		return xmlParserUtil.comps;
		
	}
	
	public static Map<String, String> parseDiagramXML(KrnObject object, int lang, String pattern) throws Exception {
		
		byte [] diagram = Kernel.instance().getBlobs(object, "diagram", lang, 0)[0];
		
		ByteArrayInputStream is = new ByteArrayInputStream(diagram);
		SAXBuilder sb = new SAXBuilder();
		Element re = sb.build(is).getRootElement();
		XmlParserUtil xmlParserUtil = new XmlParserUtil();
		xmlParserUtil.parseDiagramElement(re, pattern);
		is.close();
		return xmlParserUtil.comps;
		
	}
	
	public void parseUIElement(Element re, String pattern) {
		String title = re.getChild("title") == null ? "" : re.getChild("title").getValue();
		title = msgs.get(title) == null ? title : msgs.get(title);
		StringBuilder content = new StringBuilder();
		String elementName;
		for (Object child : re.getChildren()) {
			elementName = ((Element)child).getName();
			if (elementName.equals("children") 
					|| elementName.equals("viewComp")
						|| elementName.equals("columns")) continue;			
			content.append(getContent((Element)child));
			content.append(",");
		}
		elementName = null;
		if (Kernel.instance().stringSearch(title, pattern, langId)
				|| Kernel.instance().stringSearch(content.toString(), pattern, langId))
			comps.put(title, "Найдено в компоненте \"" + title + 
					"\", класс компонента \"" + re.getAttributeValue("class") + "\"");
        Element children;
            if ((children = re.getChild("children")) == null)
            if ((children = re.getChild("viewComp")) == null)
                if ((children = re.getChild("columns")) == null)
                    children = null;
        if (children != null) {
            List lre = children.getChildren("Component");
            for (Object cre : lre)
                parseUIElement((Element) cre, pattern);
        }
	}
	
	public void parseDiagramElement(Element re, String pattern) {		
		for (Object node : re.getChildren("node")) {
			String id = ((Element)node).getAttributeValue("id");
			StringBuilder content = new StringBuilder();
			content.append(((Element)node).getAttributeValue("class"));
			content.append(",");
			for (Object child : ((Element)node).getChildren()) {
				content.append(((Element)child).getAttributeValue("name") + "," + ((Element)child).getValue());
				content.append(",");
			}			
			if (Kernel.instance().stringSearch(id, pattern, langId)
					|| Kernel.instance().stringSearch(content.toString(), pattern, langId))
				comps.put(id, "Найдено в узле \"" + id + 
						"\", класс узла \"" 
						//+ ((Element)node).getAttributeValue("class")
						+ re.getAttributeValue("class") 
						+ "\"");
		}
		for (Object node : re.getChildren("edge")) {
			String id = ((Element)node).getAttributeValue("id");
			StringBuilder content = new StringBuilder();
			content.append(((Element)node).getAttributeValue("name"));
			if (Kernel.instance().stringSearch(id, pattern, langId)
					|| Kernel.instance().stringSearch(content.toString(), pattern, langId))
				comps.put(id, "Найдено в ребре \"" + id + 
						"\", класс ребра \"" + re.getAttributeValue("class") + "\"");
		}
	}
		
	public String getContent(Element re) {
		StringBuilder content = new StringBuilder();
		for (Object attrib : re.getAttributes())
			content.append(((Attribute)attrib).getValue());
		content.append(re.getValue());
		content.append(",");
		List children = re.getChildren();
		for (Object child : children) {
			if (((Element)child).getName().equals("message")) {
				content.append(msgs.get(((Element)child).getValue()));
				content.append(",");
				continue;
			}
			content.append(getContent((Element)child));
			content.append(",");
		}
		return content.toString();
	}
	
	public Map<String, String> loadMessages(byte [] strings) throws Exception {
        if (strings.length > 0) {
            ByteArrayInputStream is = new ByteArrayInputStream(strings);
            SAXBuilder b = new SAXBuilder();
            Element e = b.build(is).getRootElement();
            List chs = e.getChildren();
            for (int i = 0; i < chs.size(); i++) {
                Element ch = (Element) chs.get(i);
                String uid = ch.getAttributeValue("uid");
                if (ch.getContentSize() > 0) {
                    for (int j=0; j<ch.getContentSize(); j++) {
                        if (ch.getContent(j) instanceof CDATA) {
                            String s = ((CDATA)ch.getContent(j)).getText();
                            byte[] value = s.getBytes();
                            msgs.put(uid, new String(value));
                        } else if (ch.getContent(j) instanceof Text) {
                            String value = ch.getText();
                            if (!"Безымянный".equals(value))
                                msgs.put(uid, value);
                        }
                    }
                }
            }
        }
        return msgs;
	}
	
	public class ResultListModel extends AbstractListModel {

		private static final long serialVersionUID = 7750498138944280935L;
		private String [] keys;
		private String [] values;
		private int size;
		
		public ResultListModel(Map<String, String> comps) {
			keys = comps.keySet().toArray(new String[size]);
			values = comps.values().toArray(new String[size]);
			size = keys.length;
		}
		
		@Override
		public Object getElementAt(int index) {
			return values[index];
		}
		
		public String getIdAt(int index) {
			return keys[index];
		}

		@Override
		public int getSize() {
			return size;
		}
		
	}

}
