package kz.tamur.rt;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;

import java.io.*;

import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;

/**
 * Вытаскивает нужные элементы из XMLа, XML -> Blob-byte[] -> Attribute-"quickList" -> Class-User
 * @author g009c1233
 * @version	0.1
 * @since	2011/05/05
 */
public class QuickSrvList{
	private KrnObject userObj;
	private Kernel k;
	private KrnClass kcls;
	private byte[] blob;
	private String attrName = "quickList";
	private String clsName = "User";
	private Element root;
	//---
	private boolean attrNotExistFlag;
	//---
	
	/**
	 * Constructor, вызывает BLOBа и создает XML-root.
	 * Если нет Аттрибута, создает его сам.
	 * @author g009c1233
	 * @since 2011/05/05
	 * @return Constructor! вызывает BLOBа и создает основной XML-root
	 */
	public QuickSrvList() {
		//May be need to insert attrName checker?!
		k = Kernel.instance();
		userObj= k.getUser().getObject();
		
		try{
			kcls = k.getClassByName(clsName);
			if(k.getAttributeByName(kcls, attrName) == null){
				//k.createAttribute(kcls, k.getClassByName("blob"), attrName, 0, false, false, false, false, 0, 0, 0, 0, false);
				//System.err.println("Warning! In class "+clsName+", Attribute \""+attrName+"\" not exist, but no problems");
				//----
				attrNotExistFlag = true;
				root = createXml();
				//----
				return;
			}
			blob = k.getBlob(userObj, attrName, 0, 0, 0);
			if(blob != null && blob.length >1) {
				try {
					root = getXmlFromBlob();
					if(root == null) {
						root = createXml(); 
						setXmlToBlob(root);
					}
				} catch (Exception e) {
					root = createXml();
					setXmlToBlob(root);
					e.printStackTrace();
				}
			} else {
				root = createXml();
				setXmlToBlob(root);
			}
		}catch(KrnException e) {
			System.err.println("KrnException on QuickSrvList: " + e);
		}catch(Exception e){
			System.err.println("Exception on QuickSrvList: " + e);
		}
	}
	
	/**
	 * Вытаскивает нужный элемент(XML-ку) из XML-rootа
	 * @param name - имя элемента(XMLа) который нужен
	 * @return Element часть XMLа
	 */
	public Element getXml(String name){
		return root.getChild(name);
	}
	
	/**
	 * Сначала переписывает старую часть в XML-rootе, записывает XMLку в BLOB и потом отправляет его
	 * @param el XML которую нужно вставить. Определяется по имени первого
	 */
	public void setXml(Element el){
		if(root.getChild(el.getName()) != null) root.removeChild(el.getName());
		root.addContent(el);
    	setXmlToBlob(root);
	}
	
	/**
	 * Создает рутовую ХМЛку
	 * @return XML-root типа шаблон
	 */
	private Element createXml(){
		Element xml = new Element("root");
		xml.setAttribute("name", k.getUser().getName());
		xml.setText("This is a rooot!!!");
		//xml.addContent(new Element("node"));
		return xml;
	}
	
	/**
	 * Вытаскивает рутовую ХМЛку из Блоба.
	 * @return XML from Blob-byte[]
	 */
	private Element getXmlFromBlob(){
		//---
		if(attrNotExistFlag) return null;
		//---
		try{
			ByteArrayInputStream iS = new ByteArrayInputStream(blob);
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(iS);
			return doc.getRootElement();
		} catch(Exception e){
			System.err.println("Exception on quickList.getXmlFromBlob");
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Пишет XML-root в Blob-bye[], потом отправляет
	 * @param xml рутовый
	 */
	private void setXmlToBlob(Element xml){
		//---
		if(attrNotExistFlag) return;
		//---
		XMLOutputter opr = new XMLOutputter();
        opr.setFormat(opr.getFormat().setEncoding("UTF-8"));
        xml.detach();
        try {
        	ByteArrayOutputStream bos = new ByteArrayOutputStream();
			opr.output(new Document(xml), bos);
			bos.close();
			blob = bos.toByteArray();
			//opr.output(xml, System.out);
			//May be need to insert attrName checker?! Can crash other's xml
			k.setBlob(userObj.id, k.getAttributeByName(k.getClassByName(clsName),attrName).id, 0, blob, 0, 0);
        }catch(Exception e) {
        	System.err.println("Exception on quickList.setXmlFromBlob");
        	e.printStackTrace();
        }
	}
}