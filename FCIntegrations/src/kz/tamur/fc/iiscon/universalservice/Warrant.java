//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2024.12.27 at 06:54:50 PM GMT+05:00 
//


package kz.tamur.fc.iiscon.universalservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * Доверенность
 * 
 * <p>Java class for Warrant complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Warrant">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="warrantType" type="{http://schemas.letograf.kz/iiscon/bus/v1}WarrantType"/>
 *         &lt;element name="trustee" type="{http://schemas.letograf.kz/iiscon/bus/v1}Person"/>
 *         &lt;element name="docNum" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="docDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="expireDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="signedBy" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Warrant", propOrder = {
    "warrantType",
    "trustee",
    "docNum",
    "docDate",
    "expireDate",
    "signedBy"
})
public class Warrant {

    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected WarrantType warrantType;
    @XmlElement(required = true)
    protected Person trustee;
    @XmlElement(required = true)
    protected String docNum;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar docDate;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar expireDate;
    protected String signedBy;

    /**
     * Gets the value of the warrantType property.
     * 
     * @return
     *     possible object is
     *     {@link WarrantType }
     *     
     */
    public WarrantType getWarrantType() {
        return warrantType;
    }

    /**
     * Sets the value of the warrantType property.
     * 
     * @param value
     *     allowed object is
     *     {@link WarrantType }
     *     
     */
    public void setWarrantType(WarrantType value) {
        this.warrantType = value;
    }

    /**
     * Gets the value of the trustee property.
     * 
     * @return
     *     possible object is
     *     {@link Person }
     *     
     */
    public Person getTrustee() {
        return trustee;
    }

    /**
     * Sets the value of the trustee property.
     * 
     * @param value
     *     allowed object is
     *     {@link Person }
     *     
     */
    public void setTrustee(Person value) {
        this.trustee = value;
    }

    /**
     * Gets the value of the docNum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocNum() {
        return docNum;
    }

    /**
     * Sets the value of the docNum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocNum(String value) {
        this.docNum = value;
    }

    /**
     * Gets the value of the docDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDocDate() {
        return docDate;
    }

    /**
     * Sets the value of the docDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDocDate(XMLGregorianCalendar value) {
        this.docDate = value;
    }

    /**
     * Gets the value of the expireDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getExpireDate() {
        return expireDate;
    }

    /**
     * Sets the value of the expireDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setExpireDate(XMLGregorianCalendar value) {
        this.expireDate = value;
    }

    /**
     * Gets the value of the signedBy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSignedBy() {
        return signedBy;
    }

    /**
     * Sets the value of the signedBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSignedBy(String value) {
        this.signedBy = value;
    }

}
