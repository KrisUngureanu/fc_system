//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2024.12.27 at 06:54:50 PM GMT+05:00 
//


package kz.tamur.fc.iiscon.universalservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * Информация о доме
 * 
 * <p>Java class for Building complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Building">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="dom" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="corpus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="stroenie" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="vladenie" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Building", propOrder = {
    "dom",
    "corpus",
    "stroenie",
    "vladenie"
})
public class Building {

    protected String dom;
    protected String corpus;
    protected String stroenie;
    protected String vladenie;

    /**
     * Gets the value of the dom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDom() {
        return dom;
    }

    /**
     * Sets the value of the dom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDom(String value) {
        this.dom = value;
    }

    /**
     * Gets the value of the corpus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCorpus() {
        return corpus;
    }

    /**
     * Sets the value of the corpus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCorpus(String value) {
        this.corpus = value;
    }

    /**
     * Gets the value of the stroenie property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStroenie() {
        return stroenie;
    }

    /**
     * Sets the value of the stroenie property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStroenie(String value) {
        this.stroenie = value;
    }

    /**
     * Gets the value of the vladenie property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVladenie() {
        return vladenie;
    }

    /**
     * Sets the value of the vladenie property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVladenie(String value) {
        this.vladenie = value;
    }

}
