package kz.tamur.guidesigner;

import javax.swing.*;

import java.awt.*;

import kz.tamur.comps.Constants;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.QuickSrvList;

import org.jdom.*;

/**
 * Последние добавленные процессы.
 * @author g009c1233
 * @since 2011/05/05
 * @version 0.1
 */
public class QuickSrvListPanel extends JPanel{
	private JList list = new JList();
	private JScrollPane list2;
	private String[] textList;
	private String[] nameList;
	private long[] idList;
	private String[] pathList;
	public String selectedItemName;
	public String selectedItemPath;
	private QuickSrvList qlist;
	private boolean isClear = false;
	private String xmlName = "lastSrvList";
	private Element xml;
	private String elName = "lastAdded-";
	private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
	
	/**
	 * Constructor, вызывает write(params...) для записи последнего запущенного процесса
	 * @param name Имя процесса
	 * @param path Путь процесса
	 * @param id не UID, простой номер
	 */
	public QuickSrvListPanel(String name, String path, String id){
		write(name, path, id);
	}
	
	/**
	 * Записывает последний процесс
	 * @param name Имя процесса
	 * @param path Путь процесса
	 * @param id не UID, простой номер
	 */
	public void write(String name, String path, String id){
		qlist = new QuickSrvList();
		xml = qlist.getXml(xmlName);
		if(xml==null)
			xml = createXml();
		int count = Integer.valueOf(xml.getAttributeValue("items"));
		Element el;
		boolean have = false;
		for(int i = 1;i <= count; i++){
			if((el = xml.getChild(elName + i)) == null){
				xml = createXml();
				count = 0;
				break;
			}
			if(el.getAttributeValue("name").equals(name) && el.getAttributeValue("path").equals(path) && el.getAttributeValue("id").equals(id)){
				for(int j = i; j > 1; j--){
					(xml.getChild(elName + j)).setAttribute("name", ((xml.getChild(elName + (j - 1))).getAttributeValue("name")));
					(xml.getChild(elName + j)).setAttribute("path", ((xml.getChild(elName + (j - 1))).getAttributeValue("path")));
					(xml.getChild(elName + j)).setAttribute("id", ((xml.getChild(elName + (j - 1))).getAttributeValue("id")));
				}
				have = true;
				count--;
				break;
			}
		}
		if(count<15)count++;
		if(!have){
			Element el1, el2;
			if(count<16)xml.addContent(new Element(elName+count));
			for(int i = count; i > 1; i--) {
				el1 = xml.getChild(elName+i);
				el2 = xml.getChild(elName+(i-1));
				el1.setAttribute("name", el2.getAttributeValue("name"));
				el1.setAttribute("path", el2.getAttributeValue("path"));
				el1.setAttribute("id", el2.getAttributeValue("id"));
			}
		}
		xml.setAttribute("items", String.valueOf(count));
		if(xml.getChild(elName + "1") == null) xml.addContent(new Element(elName + "1"));
		xml.getChild(elName + "1").setAttribute("name", name);
		xml.getChild(elName + "1").setAttribute("path", path);
		xml.getChild(elName + "1").setAttribute("id", id);
		
		qlist.setXml(xml);
	}
	
	/**
	 * Constructor, Инициализация панельки
	 * Вызывает init();
	 */
	public QuickSrvListPanel() {
		super(new GridBagLayout());
        setPreferredSize(new Dimension(500, 280));
        setMinimumSize(new Dimension(500, 280));
        init();
	}
	
	/**
	 * Инициализация панельки
	 */
	public void init() {
		qlist = new QuickSrvList();
		xml = qlist.getXml(xmlName);
		if(xml == null) 
			xml = createXml();
		
		int items = Integer.valueOf(xml.getAttributeValue("items"));
		textList = new String[items];
		idList = new long[items];
		nameList = new String[items];
		pathList = new String[items];
		for(int i = 1; i <= items; i++){
			Element el;
			if((el = xml.getChild(elName + i)) == null){
				xml = createXml();
				//MayBeNeed to clear arrays?! Make some text or error
				idList = new long[0];
				nameList = new String[0];
				pathList = new String[0];
				textList = new String[0];
				break;
			}
			//textList[i-1] = new String("<html><font size=\"5\">" + nameList[i-1] + "</font><font color=#808080 size=\"2\"> \u2192 " + pathList[i-1] + "</font></html>");
			idList[i-1] = Long.valueOf(el.getAttributeValue("id"));
			nameList[i-1] = new String(el.getAttributeValue("name"));
			pathList[i-1] = new String(el.getAttributeValue("path"));
			textList[i-1] = new String("<html>" + nameList[i-1] + "<font color=#808080 size=\"2\"> \u2192 " + pathList[i-1] + "</font></html>");
		}
		
		this.list = new JList(textList);
		this.list.setLayoutOrientation(JList.VERTICAL);
		list2 = new JScrollPane(this.list);
		list2.setPreferredSize(new Dimension(500, 280));
		list2.setMinimumSize(new Dimension(500, 280));
		//Сам не знаю!!! Так что позже надо будет переделать! А вообще, надо все переделать.
		add(list2, new GridBagConstraints( 0, 0, 2, 2, GridBagConstraints.CENTER, GridBagConstraints.BOTH, GridBagConstraints.CENTER, GridBagConstraints.BOTH, Constants.INSETS_0, 0, 0));
		setOpaque(isOpaque);
		list2.setOpaque(isOpaque);
	}
	
	/**
	 * 
	 * @param b
	 */
	public QuickSrvListPanel(boolean b) {
		if(!b) return;
		qlist = new QuickSrvList();
		xml = qlist.getXml(xmlName);
		if(xml == null) 
			xml = createXml();
		
		int items = Integer.valueOf(xml.getAttributeValue("items"));
		textList = new String[items];
		idList = new long[items];
		nameList = new String[items];
		pathList = new String[items];
		for(int i = 1; i <= items; i++){
			Element el;
			if((el = xml.getChild(elName + i)) == null){
				xml = createXml();
				//MayBeNeed to clear arrays?! Make some text or error
				idList = new long[0];
				nameList = new String[0];
				pathList = new String[0];
				break;
			}
			//textList[i-1] = new String("<html><font size=\"5\">" + nameList[i-1] + "</font><font color=#808080 size=\"2\"> \u2192 " + pathList[i-1] + "</font></html>");
			idList[i-1] = Long.valueOf(el.getAttributeValue("id"));
			nameList[i-1] = new String(el.getAttributeValue("name"));
			pathList[i-1] = new String(el.getAttributeValue("path"));
		}
	}
	
	public String[] getNameList(){
		return nameList;
	}
	
	public long[] getIdList(){
		return idList;
	}
	
	public String[] getPathList(){
		return pathList;
	}
	
	public boolean isClear(){
		return isClear;
	}
	
	public JList getList() {
		return list;
	}
	
	/**
	 * Создает XMLку
	 * @return чистую xml
	 */
	private Element createXml(){
		Element xml = new Element(xmlName);
		xml.setAttribute("items", "0");
		xml.setText(" rooot!!! ");
		return xml;
	}
	
	/**
	 * Возвращает номер выбранного
	 * @return id
	 */
	public long getSelectedId(){
		if(list.getSelectedIndex() == -1) return -1;
		//need save()!!!
		selectedItemName = xml.getChild(elName+(list.getSelectedIndex()+1)).getAttributeValue("name");
		selectedItemPath = xml.getChild(elName+(list.getSelectedIndex()+1)).getAttributeValue("path");
		
		return idList[list.getSelectedIndex()];
	}
	
	/**
	 * Удаляет из xml, xml.Childа по  elName + id
	 * @param idd номер
	 */
	public void deleteById(long idd){
		String id = String.valueOf(idd);
		
		for(int i = 1; i <= Integer.valueOf(xml.getAttributeValue("items")); i++){
			if(xml.getChild(elName + i).getAttributeValue("id").equals(id)){
				xml.removeChild(elName + i);
				for(int j = i+1; j <= Integer.valueOf(xml.getAttributeValue("items")); j++){
					//xml.getChild(elName + j).setAttributes(xml.getChild(elName + (j+1)).getAttributes());
					xml.getChild(elName + j).setName(elName + (j-1));
				}
				xml.setAttribute("items", String.valueOf((Integer.valueOf(xml.getAttributeValue("items")) - 1)));
				qlist.setXml(xml);
				return;
			}
		}
	}
}