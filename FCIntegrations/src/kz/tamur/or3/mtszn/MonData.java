
package kz.tamur.or3.mtszn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for monData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="monData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="birthDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="entData" type="{http://services.sync.mtszn/}monentData" minOccurs="0"/>
 *         &lt;element name="enterDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="graduateDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="iin" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="isStudent" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="middleName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="scholarshipAmount" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="scholarshipPaymentDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="schoolAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="schoolCategory" type="{http://services.sync.mtszn/}additionalName" minOccurs="0"/>
 *         &lt;element name="schoolName" type="{http://services.sync.mtszn/}additionalName" minOccurs="0"/>
 *         &lt;element name="speciality" type="{http://services.sync.mtszn/}additionalName" minOccurs="0"/>
 *         &lt;element name="surName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "monData", propOrder = {
    "birthDate",
    "entData",
    "enterDate",
    "graduateDate",
    "iin",
    "isStudent",
    "middleName",
    "name",
    "scholarshipAmount",
    "scholarshipPaymentDate",
    "schoolAddress",
    "schoolCategory",
    "schoolName",
    "speciality",
    "surName"
})
public class MonData {

    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar birthDate;
    protected MonentData entData;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar enterDate;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar graduateDate;
    protected String iin;
    protected String isStudent;
    protected String middleName;
    protected String name;
    protected double scholarshipAmount;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar scholarshipPaymentDate;
    protected String schoolAddress;
    protected AdditionalName schoolCategory;
    protected AdditionalName schoolName;
    protected AdditionalName speciality;
    protected String surName;

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
     * Gets the value of the entData property.
     * 
     * @return
     *     possible object is
     *     {@link MonentData }
     *     
     */
    public MonentData getEntData() {
        return entData;
    }

    /**
     * Sets the value of the entData property.
     * 
     * @param value
     *     allowed object is
     *     {@link MonentData }
     *     
     */
    public void setEntData(MonentData value) {
        this.entData = value;
    }

    /**
     * Gets the value of the enterDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEnterDate() {
        return enterDate;
    }

    /**
     * Sets the value of the enterDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEnterDate(XMLGregorianCalendar value) {
        this.enterDate = value;
    }

    /**
     * Gets the value of the graduateDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getGraduateDate() {
        return graduateDate;
    }

    /**
     * Sets the value of the graduateDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setGraduateDate(XMLGregorianCalendar value) {
        this.graduateDate = value;
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
     * Gets the value of the isStudent property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIsStudent() {
        return isStudent;
    }

    /**
     * Sets the value of the isStudent property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIsStudent(String value) {
        this.isStudent = value;
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
     * Gets the value of the scholarshipAmount property.
     * 
     */
    public double getScholarshipAmount() {
        return scholarshipAmount;
    }

    /**
     * Sets the value of the scholarshipAmount property.
     * 
     */
    public void setScholarshipAmount(double value) {
        this.scholarshipAmount = value;
    }

    /**
     * Gets the value of the scholarshipPaymentDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getScholarshipPaymentDate() {
        return scholarshipPaymentDate;
    }

    /**
     * Sets the value of the scholarshipPaymentDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setScholarshipPaymentDate(XMLGregorianCalendar value) {
        this.scholarshipPaymentDate = value;
    }

    /**
     * Gets the value of the schoolAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSchoolAddress() {
        return schoolAddress;
    }

    /**
     * Sets the value of the schoolAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSchoolAddress(String value) {
        this.schoolAddress = value;
    }

    /**
     * Gets the value of the schoolCategory property.
     * 
     * @return
     *     possible object is
     *     {@link AdditionalName }
     *     
     */
    public AdditionalName getSchoolCategory() {
        return schoolCategory;
    }

    /**
     * Sets the value of the schoolCategory property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdditionalName }
     *     
     */
    public void setSchoolCategory(AdditionalName value) {
        this.schoolCategory = value;
    }

    /**
     * Gets the value of the schoolName property.
     * 
     * @return
     *     possible object is
     *     {@link AdditionalName }
     *     
     */
    public AdditionalName getSchoolName() {
        return schoolName;
    }

    /**
     * Sets the value of the schoolName property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdditionalName }
     *     
     */
    public void setSchoolName(AdditionalName value) {
        this.schoolName = value;
    }

    /**
     * Gets the value of the speciality property.
     * 
     * @return
     *     possible object is
     *     {@link AdditionalName }
     *     
     */
    public AdditionalName getSpeciality() {
        return speciality;
    }

    /**
     * Sets the value of the speciality property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdditionalName }
     *     
     */
    public void setSpeciality(AdditionalName value) {
        this.speciality = value;
    }

    /**
     * Gets the value of the surName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSurName() {
        return surName;
    }

    /**
     * Sets the value of the surName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSurName(String value) {
        this.surName = value;
    }

}
