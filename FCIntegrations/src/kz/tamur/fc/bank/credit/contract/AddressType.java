//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.07.24 at 11:13:30 AM ALMT 
//


package kz.tamur.fc.bank.credit.contract;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AddressType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AddressType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="StreetName" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Text" type="{http://www.datapump.cig.com}empty-TextType" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Streetnumber" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.datapump.cig.com}non-empty-string">
 *               &lt;maxLength value="10"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="PostBox" type="{http://www.datapump.cig.com}non-empty-string" minOccurs="0"/>
 *         &lt;element name="AdditionalInformation" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Text" type="{http://www.datapump.cig.com}TextType" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="PostalCode" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="typeId" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="locationId" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="katoId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AddressType", propOrder = {
    "streetName",
    "streetnumber",
    "postBox",
    "additionalInformation",
    "postalCode"
})
@XmlSeeAlso({
    kz.tamur.fc.bank.credit.contract.Records.Contract.General.Subjects.Subject.Entity.Individual.Addresses.Address.class,
    kz.tamur.fc.bank.credit.contract.Records.Contract.General.Subjects.Subject.Entity.Company.Addresses.Address.class,
    kz.tamur.fc.bank.credit.contract.SubjectType.Entity.Individual.Addresses.Address.class,
    kz.tamur.fc.bank.credit.contract.SubjectType.Entity.Company.Addresses.Address.class
})
public class AddressType {

    @XmlElementRef(name = "StreetName", namespace = "http://www.datapump.cig.com", type = JAXBElement.class, required = false)
    protected JAXBElement<AddressType.StreetName> streetName;
    @XmlElement(name = "Streetnumber")
    protected String streetnumber;
    @XmlElement(name = "PostBox")
    protected String postBox;
    @XmlElement(name = "AdditionalInformation")
    protected AddressType.AdditionalInformation additionalInformation;
    @XmlElement(name = "PostalCode")
    protected Integer postalCode;
    @XmlAttribute(name = "typeId", required = true)
    protected int typeId;
    @XmlAttribute(name = "locationId")
    protected Integer locationId;
    @XmlAttribute(name = "katoId")
    protected String katoId;

    /**
     * Gets the value of the streetName property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link AddressType.StreetName }{@code >}
     *     
     */
    public JAXBElement<AddressType.StreetName> getStreetName() {
        return streetName;
    }

    /**
     * Sets the value of the streetName property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link AddressType.StreetName }{@code >}
     *     
     */
    public void setStreetName(JAXBElement<AddressType.StreetName> value) {
        this.streetName = value;
    }

    /**
     * Gets the value of the streetnumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStreetnumber() {
        return streetnumber;
    }

    /**
     * Sets the value of the streetnumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStreetnumber(String value) {
        this.streetnumber = value;
    }

    /**
     * Gets the value of the postBox property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPostBox() {
        return postBox;
    }

    /**
     * Sets the value of the postBox property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPostBox(String value) {
        this.postBox = value;
    }

    /**
     * Gets the value of the additionalInformation property.
     * 
     * @return
     *     possible object is
     *     {@link AddressType.AdditionalInformation }
     *     
     */
    public AddressType.AdditionalInformation getAdditionalInformation() {
        return additionalInformation;
    }

    /**
     * Sets the value of the additionalInformation property.
     * 
     * @param value
     *     allowed object is
     *     {@link AddressType.AdditionalInformation }
     *     
     */
    public void setAdditionalInformation(AddressType.AdditionalInformation value) {
        this.additionalInformation = value;
    }

    /**
     * Gets the value of the postalCode property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getPostalCode() {
        return postalCode;
    }

    /**
     * Sets the value of the postalCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setPostalCode(Integer value) {
        this.postalCode = value;
    }

    /**
     * Gets the value of the typeId property.
     * 
     */
    public int getTypeId() {
        return typeId;
    }

    /**
     * Sets the value of the typeId property.
     * 
     */
    public void setTypeId(int value) {
        this.typeId = value;
    }

    /**
     * Gets the value of the locationId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getLocationId() {
        return locationId;
    }

    /**
     * Sets the value of the locationId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setLocationId(Integer value) {
        this.locationId = value;
    }

    /**
     * Gets the value of the katoId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKatoId() {
        return katoId;
    }

    /**
     * Sets the value of the katoId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKatoId(String value) {
        this.katoId = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="Text" type="{http://www.datapump.cig.com}TextType" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "text"
    })
    public static class AdditionalInformation {

        @XmlElement(name = "Text", required = true)
        protected List<TextType> text;

        /**
         * Gets the value of the text property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the text property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getText().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TextType }
         * 
         * 
         */
        public List<TextType> getText() {
            if (text == null) {
                text = new ArrayList<TextType>();
            }
            return this.text;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="Text" type="{http://www.datapump.cig.com}empty-TextType" maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "text"
    })
    public static class StreetName {

        @XmlElement(name = "Text")
        protected List<EmptyTextType> text;

        /**
         * Gets the value of the text property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the text property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getText().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link EmptyTextType }
         * 
         * 
         */
        public List<EmptyTextType> getText() {
            if (text == null) {
                text = new ArrayList<EmptyTextType>();
            }
            return this.text;
        }

    }

}
