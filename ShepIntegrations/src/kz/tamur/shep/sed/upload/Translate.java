//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.11.07 at 12:28:25 PM ALMT 
//


package kz.tamur.shep.sed.upload;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * �������� ��������� �������
 * 
 * <p>Java class for Translate complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Translate">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="kk" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ru" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="en" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Translate", propOrder = {
    "kk",
    "ru",
    "en"
})
public class Translate {

    @XmlElement(required = true)
    protected String kk;
    @XmlElement(required = true)
    protected String ru;
    protected String en;

    /**
     * Gets the value of the kk property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKk() {
        return kk;
    }

    /**
     * Sets the value of the kk property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKk(String value) {
        this.kk = value;
    }

    /**
     * Gets the value of the ru property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRu() {
        return ru;
    }

    /**
     * Sets the value of the ru property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRu(String value) {
        this.ru = value;
    }

    /**
     * Gets the value of the en property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEn() {
        return en;
    }

    /**
     * Sets the value of the en property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEn(String value) {
        this.en = value;
    }

}
