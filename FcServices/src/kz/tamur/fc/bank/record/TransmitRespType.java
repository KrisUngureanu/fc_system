
package kz.tamur.fc.bank.record;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for transmitRespType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="transmitRespType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="IIN" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="regRecordNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="type" type="{http://record.bank.fc.tamur.kz}DictionaryType"/>
 *         &lt;element name="fileId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="fileHash" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="fileType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="info" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="content" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "transmitRespType", propOrder = {
    "iin",
    "regRecordNumber",
    "type",
    "fileId",
    "fileHash",
    "fileType",
    "info",
    "content"
})
public class TransmitRespType {

    @XmlElement(name = "IIN", required = true)
    protected String iin;
    @XmlElement(required = true)
    protected String regRecordNumber;
    @XmlElement(required = true)
    protected DictionaryType type;
    @XmlElement(required = true)
    protected String fileId;
    @XmlElement(required = true)
    protected String fileHash;
    @XmlElement(required = true)
    protected String fileType;
    @XmlElement(required = true)
    protected String info;
    protected byte[] content;

    /**
     * Gets the value of the iin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIIN() {
        return iin;
    }

    /**
     * Sets the value of the iin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIIN(String value) {
        this.iin = value;
    }

    /**
     * Gets the value of the regRecordNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegRecordNumber() {
        return regRecordNumber;
    }

    /**
     * Sets the value of the regRecordNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegRecordNumber(String value) {
        this.regRecordNumber = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link DictionaryType }
     *     
     */
    public DictionaryType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link DictionaryType }
     *     
     */
    public void setType(DictionaryType value) {
        this.type = value;
    }

    /**
     * Gets the value of the fileId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFileId() {
        return fileId;
    }

    /**
     * Sets the value of the fileId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFileId(String value) {
        this.fileId = value;
    }

    /**
     * Gets the value of the fileHash property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFileHash() {
        return fileHash;
    }

    /**
     * Sets the value of the fileHash property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFileHash(String value) {
        this.fileHash = value;
    }

    /**
     * Gets the value of the fileType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFileType() {
        return fileType;
    }

    /**
     * Sets the value of the fileType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFileType(String value) {
        this.fileType = value;
    }

    /**
     * Gets the value of the info property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInfo() {
        return info;
    }

    /**
     * Sets the value of the info property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInfo(String value) {
        this.info = value;
    }

    /**
     * Gets the value of the content property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * Sets the value of the content property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setContent(byte[] value) {
        this.content = value;
    }

}