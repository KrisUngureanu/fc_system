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
 * Ответ ИС ГОНС на получение статуса и реквизитов запрашиваемой информации
 * 
 * <p>Java class for statusResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="statusResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="result" type="{http://keleshek.fc.tamur.kz}DictionaryType" form="qualified"/>
 *         &lt;element name="info" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" form="qualified"/>
 *         &lt;element name="summ" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="data" type="{http://keleshek.fc.tamur.kz}BaseDoc" form="qualified"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "statusResponseType", propOrder = {
    "result",
    "info",
    "summ",
    "data"
})
public class StatusResponseType {

    @XmlElement(required = true)
    protected DictionaryType result;
    protected String info;
    protected Double summ;
    @XmlElement(required = true)
    protected BaseDoc data;

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
     * Gets the value of the summ property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getSumm() {
        return summ;
    }

    /**
     * Sets the value of the summ property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setSumm(Double value) {
        this.summ = value;
    }

    /**
     * Gets the value of the data property.
     * 
     * @return
     *     possible object is
     *     {@link BaseDoc }
     *     
     */
    public BaseDoc getData() {
        return data;
    }

    /**
     * Sets the value of the data property.
     * 
     * @param value
     *     allowed object is
     *     {@link BaseDoc }
     *     
     */
    public void setData(BaseDoc value) {
        this.data = value;
    }

}
