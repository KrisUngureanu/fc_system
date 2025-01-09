
package kz.tamur.fc.bank.record;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * Заголовок
 * 
 * <p>Java class for SystemInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SystemInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="messageID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="cheinID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="messageDateTime" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="messageType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="codeBank" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="responseCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="sysInfo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="digiSign" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SystemInfo", propOrder = {
    "messageID",
    "cheinID",
    "messageDateTime",
    "messageType",
    "codeBank",
    "responseCode",
    "sysInfo",
    "digiSign"
})
public class SystemInfo {

    @XmlElement(required = true)
    protected String messageID;
    @XmlElement(required = true)
    protected String cheinID;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar messageDateTime;
    @XmlElement(required = true)
    protected String messageType;
    @XmlElement(required = true)
    protected String codeBank;
    @XmlElement(required = true)
    protected String responseCode;
    @XmlElement(required = true)
    protected String sysInfo;
    protected byte[] digiSign;

    /**
     * Gets the value of the messageID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessageID() {
        return messageID;
    }

    /**
     * Sets the value of the messageID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessageID(String value) {
        this.messageID = value;
    }

    /**
     * Gets the value of the cheinID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCheinID() {
        return cheinID;
    }

    /**
     * Sets the value of the cheinID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCheinID(String value) {
        this.cheinID = value;
    }

    /**
     * Gets the value of the messageDateTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getMessageDateTime() {
        return messageDateTime;
    }

    /**
     * Sets the value of the messageDateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setMessageDateTime(XMLGregorianCalendar value) {
        this.messageDateTime = value;
    }

    /**
     * Gets the value of the messageType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessageType() {
        return messageType;
    }

    /**
     * Sets the value of the messageType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessageType(String value) {
        this.messageType = value;
    }

    /**
     * Gets the value of the codeBank property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodeBank() {
        return codeBank;
    }

    /**
     * Sets the value of the codeBank property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodeBank(String value) {
        this.codeBank = value;
    }

    /**
     * Gets the value of the responseCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResponseCode() {
        return responseCode;
    }

    /**
     * Sets the value of the responseCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResponseCode(String value) {
        this.responseCode = value;
    }

    /**
     * Gets the value of the sysInfo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSysInfo() {
        return sysInfo;
    }

    /**
     * Sets the value of the sysInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSysInfo(String value) {
        this.sysInfo = value;
    }

    /**
     * Gets the value of the digiSign property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getDigiSign() {
        return digiSign;
    }

    /**
     * Sets the value of the digiSign property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setDigiSign(byte[] value) {
        this.digiSign = ((byte[]) value);
    }

}
