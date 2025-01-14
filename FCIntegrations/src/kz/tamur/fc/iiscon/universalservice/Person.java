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
 * Физическое лицо
 * 
 * <p>Java class for Person complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Person">
 *   &lt;complexContent>
 *     &lt;extension base="{http://schemas.letograf.kz/iiscon/bus/v1}AbstractParty">
 *       &lt;sequence>
 *         &lt;element name="iin" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="rnn" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lastName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="firstName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="middleName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="legalLocation" type="{http://schemas.letograf.kz/iiscon/bus/v1}Location" minOccurs="0"/>
 *         &lt;element name="actualLocation" type="{http://schemas.letograf.kz/iiscon/bus/v1}Location" minOccurs="0"/>
 *         &lt;element name="contacts" type="{http://schemas.letograf.kz/iiscon/bus/v1}Contacts" minOccurs="0"/>
 *         &lt;element name="identityDocument" type="{http://schemas.letograf.kz/iiscon/bus/v1}IdentityDocument" minOccurs="0"/>
 *         &lt;element name="dateOfBirth" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="placeOfBirth" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="gender" type="{http://schemas.letograf.kz/iiscon/bus/v1}Gender" minOccurs="0"/>
 *         &lt;element name="isForeigner" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="citizenship" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="citizenship2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Person", propOrder = {
    "iin",
    "rnn",
    "lastName",
    "firstName",
    "middleName",
    "legalLocation",
    "actualLocation",
    "contacts",
    "identityDocument",
    "dateOfBirth",
    "placeOfBirth",
    "gender",
    "isForeigner",
    "citizenship",
    "citizenship2"
})
public class Person
    extends AbstractParty
{

    protected String iin;
    protected String rnn;
    protected String lastName;
    protected String firstName;
    protected String middleName;
    protected Location legalLocation;
    protected Location actualLocation;
    protected Contacts contacts;
    protected IdentityDocument identityDocument;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateOfBirth;
    protected String placeOfBirth;
    @XmlSchemaType(name = "string")
    protected Gender gender;
    @XmlElement(defaultValue = "false")
    protected Boolean isForeigner;
    protected String citizenship;
    protected String citizenship2;

    /**
     * Gets the value of the iin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIin() {
        return iin;
    }

    /**
     * Sets the value of the iin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIin(String value) {
        this.iin = value;
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
     * Gets the value of the lastName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the value of the lastName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastName(String value) {
        this.lastName = value;
    }

    /**
     * Gets the value of the firstName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the value of the firstName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFirstName(String value) {
        this.firstName = value;
    }

    /**
     * Gets the value of the middleName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMiddleName() {
        return middleName;
    }

    /**
     * Sets the value of the middleName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMiddleName(String value) {
        this.middleName = value;
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
     * Gets the value of the identityDocument property.
     * 
     * @return
     *     possible object is
     *     {@link IdentityDocument }
     *     
     */
    public IdentityDocument getIdentityDocument() {
        return identityDocument;
    }

    /**
     * Sets the value of the identityDocument property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdentityDocument }
     *     
     */
    public void setIdentityDocument(IdentityDocument value) {
        this.identityDocument = value;
    }

    /**
     * Gets the value of the dateOfBirth property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * Sets the value of the dateOfBirth property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateOfBirth(XMLGregorianCalendar value) {
        this.dateOfBirth = value;
    }

    /**
     * Gets the value of the placeOfBirth property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

    /**
     * Sets the value of the placeOfBirth property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPlaceOfBirth(String value) {
        this.placeOfBirth = value;
    }

    /**
     * Gets the value of the gender property.
     * 
     * @return
     *     possible object is
     *     {@link Gender }
     *     
     */
    public Gender getGender() {
        return gender;
    }

    /**
     * Sets the value of the gender property.
     * 
     * @param value
     *     allowed object is
     *     {@link Gender }
     *     
     */
    public void setGender(Gender value) {
        this.gender = value;
    }

    /**
     * Gets the value of the isForeigner property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsForeigner() {
        return isForeigner;
    }

    /**
     * Sets the value of the isForeigner property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsForeigner(Boolean value) {
        this.isForeigner = value;
    }

    /**
     * Gets the value of the citizenship property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCitizenship() {
        return citizenship;
    }

    /**
     * Sets the value of the citizenship property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCitizenship(String value) {
        this.citizenship = value;
    }

    /**
     * Gets the value of the citizenship2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCitizenship2() {
        return citizenship2;
    }

    /**
     * Sets the value of the citizenship2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCitizenship2(String value) {
        this.citizenship2 = value;
    }

}
