
package kz.tamur.fl.search;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
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
 *         &lt;element name="fio" type="{http://person.persistence.interactive.nat}Fio"/>
 *         &lt;element name="birthDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="deathDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="sex" type="{http://dictionaries.persistence.interactive.nat}Sex"/>
 *         &lt;element name="nationality" type="{http://dictionaries.persistence.interactive.nat}Nationality"/>
 *         &lt;element name="citizenship" type="{http://dictionaries.persistence.interactive.nat}Country"/>
 *         &lt;element name="personStatus" type="{http://dictionaries.persistence.interactive.nat}PersonStatus"/>
 *         &lt;element name="addDocs" type="{http://person.persistence.interactive.nat}AddDocs" minOccurs="0"/>
 *         &lt;element name="regAddress" type="{http://person.persistence.interactive.nat}RegAddress" minOccurs="0"/>
 *         &lt;element name="birthPlace" type="{http://person.persistence.interactive.nat}BirthPlace" minOccurs="0"/>
 *         &lt;element name="personCapableStatus" type="{http://person.persistence.interactive.nat}PersonCapableStatus" minOccurs="0"/>
 *         &lt;element name="missingStatus" type="{http://person.persistence.interactive.nat}MissingStatus" minOccurs="0"/>
 *         &lt;element name="absentStatus" type="{http://person.persistence.interactive.nat}AbsentStatus" minOccurs="0"/>
 *         &lt;element name="disappearStatus" type="{http://person.persistence.interactive.nat}DisappearStatus" minOccurs="0"/>
 *         &lt;element name="personExcludeStatus" type="{http://person.persistence.interactive.nat}PersonExcludeStatus" minOccurs="0"/>
 *         &lt;element name="frontierCrossings" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="frontierCrossing" type="{http://document.persistence.interactive.nat}FrontierCrossing" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
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
 *         &lt;element name="subscribe" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
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
    "fio",
    "birthDate",
    "deathDate",
    "sex",
    "nationality",
    "citizenship",
    "personStatus",
    "addDocs",
    "regAddress",
    "birthPlace",
    "personCapableStatus",
    "missingStatus",
    "absentStatus",
    "disappearStatus",
    "personExcludeStatus",
    "frontierCrossings",
    "documents",
    "addresses",
    "removed",
    "subscribe"
})
public class Person {

    @XmlElement(required = true)
    protected String iin;
    @XmlElement(required = true)
    protected Fio fio;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar birthDate;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar deathDate;
    @XmlElement(required = true)
    protected Sex sex;
    @XmlElement(required = true)
    protected Nationality nationality;
    @XmlElement(required = true)
    protected Country citizenship;
    @XmlElement(required = true)
    protected PersonStatus personStatus;
    protected AddDocs addDocs;
    protected RegAddress regAddress;
    protected BirthPlace birthPlace;
    protected PersonCapableStatus personCapableStatus;
    protected MissingStatus missingStatus;
    protected AbsentStatus absentStatus;
    protected DisappearStatus disappearStatus;
    protected PersonExcludeStatus personExcludeStatus;
    protected Person.FrontierCrossings frontierCrossings;
    protected Person.Documents documents;
    protected Person.Addresses addresses;
    protected Boolean removed;
    protected Boolean subscribe;

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
     * Gets the value of the fio property.
     * 
     * @return
     *     possible object is
     *     {@link Fio }
     *     
     */
    public Fio getFio() {
        return fio;
    }

    /**
     * Sets the value of the fio property.
     * 
     * @param value
     *     allowed object is
     *     {@link Fio }
     *     
     */
    public void setFio(Fio value) {
        this.fio = value;
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
     * Gets the value of the sex property.
     * 
     * @return
     *     possible object is
     *     {@link Sex }
     *     
     */
    public Sex getSex() {
        return sex;
    }

    /**
     * Sets the value of the sex property.
     * 
     * @param value
     *     allowed object is
     *     {@link Sex }
     *     
     */
    public void setSex(Sex value) {
        this.sex = value;
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
     * Gets the value of the personStatus property.
     * 
     * @return
     *     possible object is
     *     {@link PersonStatus }
     *     
     */
    public PersonStatus getPersonStatus() {
        return personStatus;
    }

    /**
     * Sets the value of the personStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonStatus }
     *     
     */
    public void setPersonStatus(PersonStatus value) {
        this.personStatus = value;
    }

    /**
     * Gets the value of the addDocs property.
     * 
     * @return
     *     possible object is
     *     {@link AddDocs }
     *     
     */
    public AddDocs getAddDocs() {
        return addDocs;
    }

    /**
     * Sets the value of the addDocs property.
     * 
     * @param value
     *     allowed object is
     *     {@link AddDocs }
     *     
     */
    public void setAddDocs(AddDocs value) {
        this.addDocs = value;
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
     * Gets the value of the absentStatus property.
     * 
     * @return
     *     possible object is
     *     {@link AbsentStatus }
     *     
     */
    public AbsentStatus getAbsentStatus() {
        return absentStatus;
    }

    /**
     * Sets the value of the absentStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link AbsentStatus }
     *     
     */
    public void setAbsentStatus(AbsentStatus value) {
        this.absentStatus = value;
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
     * Gets the value of the personExcludeStatus property.
     * 
     * @return
     *     possible object is
     *     {@link PersonExcludeStatus }
     *     
     */
    public PersonExcludeStatus getPersonExcludeStatus() {
        return personExcludeStatus;
    }

    /**
     * Sets the value of the personExcludeStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonExcludeStatus }
     *     
     */
    public void setPersonExcludeStatus(PersonExcludeStatus value) {
        this.personExcludeStatus = value;
    }

    /**
     * Gets the value of the frontierCrossings property.
     * 
     * @return
     *     possible object is
     *     {@link Person.FrontierCrossings }
     *     
     */
    public Person.FrontierCrossings getFrontierCrossings() {
        return frontierCrossings;
    }

    /**
     * Sets the value of the frontierCrossings property.
     * 
     * @param value
     *     allowed object is
     *     {@link Person.FrontierCrossings }
     *     
     */
    public void setFrontierCrossings(Person.FrontierCrossings value) {
        this.frontierCrossings = value;
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
     * Gets the value of the subscribe property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSubscribe() {
        return subscribe;
    }

    /**
     * Sets the value of the subscribe property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSubscribe(Boolean value) {
        this.subscribe = value;
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
     *         &lt;element name="frontierCrossing" type="{http://document.persistence.interactive.nat}FrontierCrossing" maxOccurs="unbounded" minOccurs="0"/>
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
        "frontierCrossing"
    })
    public static class FrontierCrossings {

        protected List<FrontierCrossing> frontierCrossing;

        /**
         * Gets the value of the frontierCrossing property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the frontierCrossing property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getFrontierCrossing().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link FrontierCrossing }
         * 
         * 
         */
        public List<FrontierCrossing> getFrontierCrossing() {
            if (frontierCrossing == null) {
                frontierCrossing = new ArrayList<FrontierCrossing>();
            }
            return this.frontierCrossing;
        }

    }

}
