//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2024.06.05 at 05:24:35 PM GMT+05:00 
//


package kz.tamur.fc.keleshek;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Ответ на запрос об открытии депозита
 * 
 * <p>Java class for createRecordResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="createRecordResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="result" type="{http://keleshek.fc.tamur.kz}DictionaryType" form="qualified"/>
 *         &lt;element name="dataInfo" type="{http://keleshek.fc.tamur.kz}RecordResponseType" form="qualified"/>
 *         &lt;element name="dataFile" type="{http://keleshek.fc.tamur.kz}BaseDoc" form="qualified"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "createRecordResponseType", propOrder = {
    "result",
    "dataInfo",
    "dataFile"
})
public class CreateRecordResponseType {

    @XmlElement(required = true)
    protected DictionaryType result;
    @XmlElement(required = true)
    protected RecordResponseType dataInfo;
    @XmlElement(required = true)
    protected BaseDoc dataFile;

    /**
     * Gets the value of the result property.
     * 
     * @return
     *     possible object is
     *     {@link DictionaryType }
     *     
     */
    public DictionaryType getResult() {
        return result;
    }

    /**
     * Sets the value of the result property.
     * 
     * @param value
     *     allowed object is
     *     {@link DictionaryType }
     *     
     */
    public void setResult(DictionaryType value) {
        this.result = value;
    }

    /**
     * Gets the value of the dataInfo property.
     * 
     * @return
     *     possible object is
     *     {@link RecordResponseType }
     *     
     */
    public RecordResponseType getDataInfo() {
        return dataInfo;
    }

    /**
     * Sets the value of the dataInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link RecordResponseType }
     *     
     */
    public void setDataInfo(RecordResponseType value) {
        this.dataInfo = value;
    }

    /**
     * Gets the value of the dataFile property.
     * 
     * @return
     *     possible object is
     *     {@link BaseDoc }
     *     
     */
    public BaseDoc getDataFile() {
        return dataFile;
    }

    /**
     * Sets the value of the dataFile property.
     * 
     * @param value
     *     allowed object is
     *     {@link BaseDoc }
     *     
     */
    public void setDataFile(BaseDoc value) {
        this.dataFile = value;
    }

}
