//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.12.04 at 02:49:35 PM ALMT 
//


package kz.tamur.fc.iso20022;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Information that locates and identifies a specific address, as defined by postal services.
 * 
 * <p>Java class for PostalAddress24 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PostalAddress24">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AdrTp" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.001.11}AddressType3Choice" maxOccurs="0" minOccurs="0"/>
 *         &lt;element name="Dept" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.001.11}Max70Text" maxOccurs="0" minOccurs="0"/>
 *         &lt;element name="SubDept" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.001.11}Max70Text" maxOccurs="0" minOccurs="0"/>
 *         &lt;element name="StrtNm" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.001.11}Max70Text" maxOccurs="0" minOccurs="0"/>
 *         &lt;element name="BldgNb" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.001.11}Max16Text" maxOccurs="0" minOccurs="0"/>
 *         &lt;element name="BldgNm" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.001.11}Max35Text" maxOccurs="0" minOccurs="0"/>
 *         &lt;element name="Flr" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.001.11}Max70Text" maxOccurs="0" minOccurs="0"/>
 *         &lt;element name="PstBx" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.001.11}Max16Text" maxOccurs="0" minOccurs="0"/>
 *         &lt;element name="Room" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.001.11}Max70Text" maxOccurs="0" minOccurs="0"/>
 *         &lt;element name="PstCd" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.001.11}Max16Text" maxOccurs="0" minOccurs="0"/>
 *         &lt;element name="TwnNm" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.001.11}Max35Text" minOccurs="0"/>
 *         &lt;element name="TwnLctnNm" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.001.11}Max35Text" maxOccurs="0" minOccurs="0"/>
 *         &lt;element name="DstrctNm" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.001.11}Max35Text" maxOccurs="0" minOccurs="0"/>
 *         &lt;element name="CtrySubDvsn" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.001.11}Max35Text" maxOccurs="0" minOccurs="0"/>
 *         &lt;element name="Ctry" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{urn:iso:std:iso:20022:tech:xsd:pain.001.001.11}CountryCode">
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="AdrLine" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.001.11}Max70Text" maxOccurs="0" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PostalAddress24", propOrder = {
    "twnNm",
    "ctry"
})
public class PostalAddress24 {

    @XmlElement(name = "TwnNm")
    protected String twnNm;
    @XmlElement(name = "Ctry")
    protected String ctry;

    /**
     * Gets the value of the twnNm property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTwnNm() {
        return twnNm;
    }

    /**
     * Sets the value of the twnNm property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTwnNm(String value) {
        this.twnNm = value;
    }

    /**
     * Gets the value of the ctry property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCtry() {
        return ctry;
    }

    /**
     * Sets the value of the ctry property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCtry(String value) {
        this.ctry = value;
    }

}
