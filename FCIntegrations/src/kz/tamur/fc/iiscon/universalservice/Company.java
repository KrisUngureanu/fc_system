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


/**
 * Юридическое лицо
 * 
 * <p>Java class for Company complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Company">
 *   &lt;complexContent>
 *     &lt;extension base="{http://schemas.letograf.kz/iiscon/bus/v1}AbstractParty">
 *       &lt;sequence>
 *         &lt;element name="bin" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="rnn" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="okpo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="legalLocation" type="{http://schemas.letograf.kz/iiscon/bus/v1}Location" minOccurs="0"/>
 *         &lt;element name="actualLocation" type="{http://schemas.letograf.kz/iiscon/bus/v1}Location" minOccurs="0"/>
 *         &lt;element name="contacts" type="{http://schemas.letograf.kz/iiscon/bus/v1}Contacts" minOccurs="0"/>
 *         &lt;element name="headInfo" type="{http://schemas.letograf.kz/iiscon/bus/v1}Employee" minOccurs="0"/>
 *         &lt;element name="registrationDoc" type="{http://schemas.letograf.kz/iiscon/bus/v1}RegistrationDoc" minOccurs="0"/>
 *         &lt;element name="hasForeignParent" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="companyType" type="{http://schemas.letograf.kz/iiscon/bus/v1}CompanyType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Company", propOrder = {
    "bin",
    "rnn",
    "okpo",
    "name",
    "legalLocation",
    "actualLocation",
    "contacts",
    "headInfo",
    "registrationDoc",
    "hasForeignParent",
    "companyType"
})
public class Company
    extends AbstractParty
{

    protected String bin;
    protected String rnn;
    protected String okpo;
    @XmlElement(required = true)
    protected String name;
    protected Location legalLocation;
    protected Location actualLocation;
    protected Contacts contacts;
    protected Employee headInfo;
    protected RegistrationDoc registrationDoc;
    protected Boolean hasForeignParent;
    @XmlSchemaType(name = "string")
    protected CompanyType companyType;

    /**
     * Gets the value of the bin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBin() {
        return bin;
    }

    /**
     * Sets the value of the bin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBin(String value) {
        this.bin = value;
    }

    /**
     * Gets the value of the rnn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRnn() {
        return rnn;
    }

    /**
     * Sets the value of the rnn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRnn(String value) {
        this.rnn = value;
    }

    /**
     * Gets the value of the okpo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOkpo() {
        return okpo;
    }

    /**
     * Sets the value of the okpo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOkpo(String value) {
        this.okpo = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the legalLocation property.
     * 
     * @return
     *     possible object is
     *     {@link Location }
     *     
     */
    public Location getLegalLocation() {
        return legalLocation;
    }

    /**
     * Sets the value of the legalLocation property.
     * 
     * @param value
     *     allowed object is
     *     {@link Location }
     *     
     */
    public void setLegalLocation(Location value) {
        this.legalLocation = value;
    }

    /**
     * Gets the value of the actualLocation property.
     * 
     * @return
     *     possible object is
     *     {@link Location }
     *     
     */
    public Location getActualLocation() {
        return actualLocation;
    }

    /**
     * Sets the value of the actualLocation property.
     * 
     * @param value
     *     allowed object is
     *     {@link Location }
     *     
     */
    public void setActualLocation(Location value) {
        this.actualLocation = value;
    }

    /**
     * Gets the value of the contacts property.
     * 
     * @return
     *     possible object is
     *     {@link Contacts }
     *     
     */
    public Contacts getContacts() {
        return contacts;
    }

    /**
     * Sets the value of the contacts property.
     * 
     * @param value
     *     allowed object is
     *     {@link Contacts }
     *     
     */
    public void setContacts(Contacts value) {
        this.contacts = value;
    }

    /**
     * Gets the value of the headInfo property.
     * 
     * @return
     *     possible object is
     *     {@link Employee }
     *     
     */
    public Employee getHeadInfo() {
        return headInfo;
    }

    /**
     * Sets the value of the headInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link Employee }
     *     
     */
    public void setHeadInfo(Employee value) {
        this.headInfo = value;
    }

    /**
     * Gets the value of the registrationDoc property.
     * 
     * @return
     *     possible object is
     *     {@link RegistrationDoc }
     *     
     */
    public RegistrationDoc getRegistrationDoc() {
        return registrationDoc;
    }

    /**
     * Sets the value of the registrationDoc property.
     * 
     * @param value
     *     allowed object is
     *     {@link RegistrationDoc }
     *     
     */
    public void setRegistrationDoc(RegistrationDoc value) {
        this.registrationDoc = value;
    }

    /**
     * Gets the value of the hasForeignParent property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isHasForeignParent() {
        return hasForeignParent;
    }

    /**
     * Sets the value of the hasForeignParent property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setHasForeignParent(Boolean value) {
        this.hasForeignParent = value;
    }

    /**
     * Gets the value of the companyType property.
     * 
     * @return
     *     possible object is
     *     {@link CompanyType }
     *     
     */
    public CompanyType getCompanyType() {
        return companyType;
    }

    /**
     * Sets the value of the companyType property.
     * 
     * @param value
     *     allowed object is
     *     {@link CompanyType }
     *     
     */
    public void setCompanyType(CompanyType value) {
        this.companyType = value;
    }

}