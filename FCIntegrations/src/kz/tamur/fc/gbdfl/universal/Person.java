//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.01.10 at 12:17:42 PM ALMT 
//


package kz.tamur.fc.gbdfl.universal;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * �������� � ���������� ����
 * 
 * <p>Java class for Person complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Person">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="iin" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="surname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="patronymic" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="birthDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="deathDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="gender" type="{http://dictionaries.persistence.interactive.nat}Gender"/>
 *         &lt;element name="nationality" type="{http://dictionaries.persistence.interactive.nat}Nationality"/>
 *         &lt;element name="citizenship" type="{http://dictionaries.persistence.interactive.nat}Country"/>
 *         &lt;element name="lifeStatus" type="{http://dictionaries.persistence.interactive.nat}PersonStatus"/>
 *         &lt;element name="birthCertificate" type="{http://person.persistence.interactive.nat}Certificate" minOccurs="0"/>
 *         &lt;element name="deathCertificate" type="{http://person.persistence.interactive.nat}Certificate" minOccurs="0"/>
 *         &lt;element name="birthPlace" type="{http://person.persistence.interactive.nat}BirthPlace"/>
 *         &lt;element name="regAddress" type="{http://person.persistence.interactive.nat}RegAddress" minOccurs="0"/>
 *         &lt;element name="personCapableStatus" type="{http://person.persistence.interactive.nat}PersonCapableStatus" minOccurs="0"/>
 *         &lt;element name="missingStatus" type="{http://person.persistence.interactive.nat}MissingStatus" minOccurs="0"/>
 *         &lt;element name="disappearStatus" type="{http://person.persistence.interactive.nat}DisappearStatus" minOccurs="0"/>
 *         &lt;element name="excludeStatus" type="{http://person.persistence.interactive.nat}PersonExcludeStatus" minOccurs="0"/>
 *         &lt;element name="repatriationStatus" type="{http://person.persistence.interactive.nat}personRepatriationStatus" minOccurs="0"/>
 *         &lt;element name="documents" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="document" type="{http://document.persistence.interactive.nat}Document" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="addresses" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="address" type="{http://person.persistence.interactive.nat}Address" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="removed" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Person", namespace = "http://person.persistence.interactive.nat", propOrder = {
    "iin",
    "surname",
    "name",
    "patronymic",
    "birthDate",
    "deathDate",
    "gender",
    "nationality",
    "citizenship",
    "lifeStatus",
    "birthCertificate",
    "deathCertificate",
    "birthPlace",
    "regAddress",
    "personCapableStatus",
    "missingStatus",
    "disappearStatus",
    "excludeStatus",
    "repatriationStatus",
    "documents",
    "addresses",
    "removed"
})
public class Person {

    @XmlElement(required = true)
    protected String iin;
    protected String surname;
    protected String name;
    protected String patronymic;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar birthDate;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar deathDate;
    @XmlElement(required = true)
    protected Gender gender;
    @XmlElement(required = true)
    protected Nationality nationality;
    @XmlElement(required = true)
    protected Country citizenship;
    @XmlElement(required = true)
    protected PersonStatus lifeStatus;
    protected Certificate birthCertificate;
    protected Certificate deathCertificate;
    @XmlElement(required = true)
    protected BirthPlace birthPlace;
    protected RegAddress regAddress;
    protected PersonCapableStatus personCapableStatus;
    protected MissingStatus missingStatus;
    protected DisappearStatus disappearStatus;
    protected PersonExcludeStatus excludeStatus;
    protected PersonRepatriationStatus repatriationStatus;
    protected Person.Documents documents;
    protected Person.Addresses addresses;
    protected Boolean removed;

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
     * Gets the value of the surname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Sets the value of the surname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSurname(String value) {
        this.surname = value;
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
     * Gets the value of the patronymic property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPatronymic() {
        return patronymic;
    }

    /**
     * Sets the value of the patronymic property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPatronymic(String value) {
        this.patronymic = value;
    }

    /**
     * Gets the value of the birthDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getBirthDate() {
        return birthDate;
    }

    /**
     * Sets the value of the birthDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setBirthDate(XMLGregorianCalendar value) {
        this.birthDate = value;
    }

    /**
     * Gets the value of the deathDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDeathDate() {
        return deathDate;
    }

    /**
     * Sets the value of the deathDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDeathDate(XMLGregorianCalendar value) {
        this.deathDate = value;
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
     * Gets the value of the nationality property.
     * 
     * @return
     *     possible object is
     *     {@link Nationality }
     *     
     */
    public Nationality getNationality() {
        return nationality;
    }

    /**
     * Sets the value of the nationality property.
     * 
     * @param value
     *     allowed object is
     *     {@link Nationality }
     *     
     */
    public void setNationality(Nationality value) {
        this.nationality = value;
    }

    /**
     * Gets the value of the citizenship property.
     * 
     * @return
     *     possible object is
     *     {@link Country }
     *     
     */
    public Country getCitizenship() {
        return citizenship;
    }

    /**
     * Sets the value of the citizenship property.
     * 
     * @param value
     *     allowed object is
     *     {@link Country }
     *     
     */
    public void setCitizenship(Country value) {
        this.citizenship = value;
    }

    /**
     * Gets the value of the lifeStatus property.
     * 
     * @return
     *     possible object is
     *     {@link PersonStatus }
     *     
     */
    public PersonStatus getLifeStatus() {
        return lifeStatus;
    }

    /**
     * Sets the value of the lifeStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonStatus }
     *     
     */
    public void setLifeStatus(PersonStatus value) {
        this.lifeStatus = value;
    }

    /**
     * Gets the value of the birthCertificate property.
     * 
     * @return
     *     possible object is
     *     {@link Certificate }
     *     
     */
    public Certificate getBirthCertificate() {
        return birthCertificate;
    }

    /**
     * Sets the value of the birthCertificate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Certificate }
     *     
     */
    public void setBirthCertificate(Certificate value) {
        this.birthCertificate = value;
    }

    /**
     * Gets the value of the deathCertificate property.
     * 
     * @return
     *     possible object is
     *     {@link Certificate }
     *     
     */
    public Certificate getDeathCertificate() {
        return deathCertificate;
    }

    /**
     * Sets the value of the deathCertificate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Certificate }
     *     
     */
    public void setDeathCertificate(Certificate value) {
        this.deathCertificate = value;
    }

    /**
     * Gets the value of the birthPlace property.
     * 
     * @return
     *     possible object is
     *     {@link BirthPlace }
     *     
     */
    public BirthPlace getBirthPlace() {
        return birthPlace;
    }

    /**
     * Sets the value of the birthPlace property.
     * 
     * @param value
     *     allowed object is
     *     {@link BirthPlace }
     *     
     */
    public void setBirthPlace(BirthPlace value) {
        this.birthPlace = value;
    }

    /**
     * Gets the value of the regAddress property.
     * 
     * @return
     *     possible object is
     *     {@link RegAddress }
     *     
     */
    public RegAddress getRegAddress() {
        return regAddress;
    }

    /**
     * Sets the value of the regAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link RegAddress }
     *     
     */
    public void setRegAddress(RegAddress value) {
        this.regAddress = value;
    }

    /**
     * Gets the value of the personCapableStatus property.
     * 
     * @return
     *     possible object is
     *     {@link PersonCapableStatus }
     *     
     */
    public PersonCapableStatus getPersonCapableStatus() {
        return personCapableStatus;
    }

    /**
     * Sets the value of the personCapableStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonCapableStatus }
     *     
     */
    public void setPersonCapableStatus(PersonCapableStatus value) {
        this.personCapableStatus = value;
    }

    /**
     * Gets the value of the missingStatus property.
     * 
     * @return
     *     possible object is
     *     {@link MissingStatus }
     *     
     */
    public MissingStatus getMissingStatus() {
        return missingStatus;
    }

    /**
     * Sets the value of the missingStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link MissingStatus }
     *     
     */
    public void setMissingStatus(MissingStatus value) {
        this.missingStatus = value;
    }

    /**
     * Gets the value of the disappearStatus property.
     * 
     * @return
     *     possible object is
     *     {@link DisappearStatus }
     *     
     */
    public DisappearStatus getDisappearStatus() {
        return disappearStatus;
    }

    /**
     * Sets the value of the disappearStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link DisappearStatus }
     *     
     */
    public void setDisappearStatus(DisappearStatus value) {
        this.disappearStatus = value;
    }

    /**
     * Gets the value of the excludeStatus property.
     * 
     * @return
     *     possible object is
     *     {@link PersonExcludeStatus }
     *     
     */
    public PersonExcludeStatus getExcludeStatus() {
        return excludeStatus;
    }

    /**
     * Sets the value of the excludeStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonExcludeStatus }
     *     
     */
    public void setExcludeStatus(PersonExcludeStatus value) {
        this.excludeStatus = value;
    }

    /**
     * Gets the value of the repatriationStatus property.
     * 
     * @return
     *     possible object is
     *     {@link PersonRepatriationStatus }
     *     
     */
    public PersonRepatriationStatus getRepatriationStatus() {
        return repatriationStatus;
    }

    /**
     * Sets the value of the repatriationStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonRepatriationStatus }
     *     
     */
    public void setRepatriationStatus(PersonRepatriationStatus value) {
        this.repatriationStatus = value;
    }

    /**
     * Gets the value of the documents property.
     * 
     * @return
     *     possible object is
     *     {@link Person.Documents }
     *     
     */
    public Person.Documents getDocuments() {
        return documents;
    }

    /**
     * Sets the value of the documents property.
     * 
     * @param value
     *     allowed object is
     *     {@link Person.Documents }
     *     
     */
    public void setDocuments(Person.Documents value) {
        this.documents = value;
    }

    /**
     * Gets the value of the addresses property.
     * 
     * @return
     *     possible object is
     *     {@link Person.Addresses }
     *     
     */
    public Person.Addresses getAddresses() {
        return addresses;
    }

    /**
     * Sets the value of the addresses property.
     * 
     * @param value
     *     allowed object is
     *     {@link Person.Addresses }
     *     
     */
    public void setAddresses(Person.Addresses value) {
        this.addresses = value;
    }

    /**
     * Gets the value of the removed property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRemoved() {
        return removed;
    }

    /**
     * Sets the value of the removed property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRemoved(Boolean value) {
        this.removed = value;
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
     *         &lt;element name="address" type="{http://person.persistence.interactive.nat}Address" maxOccurs="unbounded" minOccurs="0"/>
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
        "address"
    })
    public static class Addresses {

        protected List<Address> address;

        /**
         * Gets the value of the address property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the address property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getAddress().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Address }
         * 
         * 
         */
        public List<Address> getAddress() {
            if (address == null) {
                address = new ArrayList<Address>();
            }
            return this.address;
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
     *         &lt;element name="document" type="{http://document.persistence.interactive.nat}Document" maxOccurs="unbounded" minOccurs="0"/>
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
        "document"
    })
    public static class Documents {

        protected List<Document> document;

        /**
         * Gets the value of the document property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the document property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getDocument().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Document }
         * 
         * 
         */
        public List<Document> getDocument() {
            if (document == null) {
                document = new ArrayList<Document>();
            }
            return this.document;
        }

    }

}
