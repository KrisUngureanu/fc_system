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
 * Ответ на запрос
 * 
 * <p>Java class for responsePermitType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="responsePermitType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="result" type="{http://keleshek.fc.tamur.kz}DictionaryType" form="qualified"/>
 *         &lt;element name="codeBank" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
 *         &lt;element name="regRecordNumber" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
 *         &lt;element name="schetNumber" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "responsePermitType", propOrder = {
    "result",
    "codeBank",
    "regRecordNumber",
    "schetNumber"
})
public class ResponsePermitType {

    @XmlElement(required = true)
    protected DictionaryType result;
    @XmlElement(required = true)
    protected String codeBank;
    @XmlElement(required = true)
    protected String regRecordNumber;
    @XmlElement(required = true)
    protected String schetNumber;

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
     * Gets the value of the schetNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSchetNumber() {
        return schetNumber;
    }

    /**
     * Sets the value of the schetNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSchetNumber(String value) {
        this.schetNumber = value;
    }

}
