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
 * Запрос к ИС ГОНС для передачи данных о закрытии депозита вкладчика ГОНС
 * 
 * <p>Java class for closeRecordRequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="closeRecordRequestType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="regRecordNumber" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
 *         &lt;element name="dataInfo" type="{http://keleshek.fc.tamur.kz}DataType" form="qualified"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "closeRecordRequestType", propOrder = {
    "regRecordNumber",
    "dataInfo"
})
public class CloseRecordRequestType {

    @XmlElement(required = true)
    protected String regRecordNumber;
    @XmlElement(required = true)
    protected DataType dataInfo;

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
     * Gets the value of the dataInfo property.
     * 
     * @return
     *     possible object is
     *     {@link DataType }
     *     
     */
    public DataType getDataInfo() {
        return dataInfo;
    }

    /**
     * Sets the value of the dataInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataType }
     *     
     */
    public void setDataInfo(DataType value) {
        this.dataInfo = value;
    }

}
