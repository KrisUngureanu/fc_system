
package kz.tamur.or3.mtszn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import kz.tamur.or3.mtszn.dict.Country;
import kz.tamur.or3.mtszn.dict.Nationality;
import kz.tamur.or3.mtszn.dict.Sex;
import kz.tamur.or3.mtszn.doc.Document;
import kz.tamur.or3.mtszn.natperson.RegAddress;


/**
 * <p>Java class for person complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="person">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="birthDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="citizenship" type="{http://dictionaries.persistence.interactive.nat}Country" minOccurs="0"/>
 *         &lt;element name="document" type="{http://document.persistence.interactive.nat}Document" minOccurs="0"/>
 *         &lt;element name="fio" type="{http://services.sync.mtszn/}fio" minOccurs="0"/>
 *         &lt;element name="iin" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="married" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="nationality" type="{http://dictionaries.persistence.interactive.nat}Nationality" minOccurs="0"/>
 *         &lt;element name="regAddress" type="{http://person.persistence.interactive.nat}RegAddress" minOccurs="0"/>
 *         &lt;element name="sex" type="{http://dictionaries.persistence.interactive.nat}Sex" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "person", propOrder = {
    "birthDate",
    "citizenship",
    "document",
    "fio",
    "iin",
    "married",
    "nationality",
    "regAddress",
    "sex"
})
public class Person {

    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar birthDate;
    protected Country citizenship;
    protected Document document;
    protected Fio fio;
    protected String iin;
    protected int married;
    protected Nationality nationality;
    protected RegAddress regAddress;
    protected Sex sex;

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
     * Gets the value of the document property.
     * 
     * @return
     *     possible object is
     *     {@link Document }
     *     
     */
    public Document getDocument() {
        return document;
    }

    /**
     * Sets the value of the document property.
     * 
     * @param value
     *     allowed object is
     *     {@link Document }
     *     
     */
    public void setDocument(Document value) {
        this.document = value;
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
     * Gets the value of the married property.
     * 
     */
    public int getMarried() {
        return married;
    }

    /**
     * Sets the value of the married property.
     * 
     */
    public void setMarried(int value) {
        this.married = value;
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

}
