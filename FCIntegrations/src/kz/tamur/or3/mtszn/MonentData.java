
package kz.tamur.or3.mtszn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for monentData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="monentData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="districtName" type="{http://services.sync.mtszn/}additionalName" minOccurs="0"/>
 *         &lt;element name="entPoints" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="entSerial" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="middleName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="middleSchoolGraduationDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="middleSchoolName" type="{http://services.sync.mtszn/}additionalName" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="regionName" type="{http://services.sync.mtszn/}additionalName" minOccurs="0"/>
 *         &lt;element name="requestResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="subjectName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlType(name = "monentData", propOrder = {
    "districtName",
    "entPoints",
    "entSerial",
    "middleName",
    "middleSchoolGraduationDate",
    "middleSchoolName",
    "name",
    "regionName",
    "requestResult",
    "subjectName",
    "surName"
})
public class MonentData {

    protected AdditionalName districtName;
    protected int entPoints;
    protected String entSerial;
    protected String middleName;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar middleSchoolGraduationDate;
    protected AdditionalName middleSchoolName;
    protected String name;
    protected AdditionalName regionName;
    protected String requestResult;
    protected String subjectName;
    protected String surName;

    /**
     * Gets the value of the districtName property.
     * 
     * @return
     *     possible object is
     *     {@link AdditionalName }
     *     
     */
    public AdditionalName getDistrictName() {
        return districtName;
    }

    /**
     * Sets the value of the districtName property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdditionalName }
     *     
     */
    public void setDistrictName(AdditionalName value) {
        this.districtName = value;
    }

    /**
     * Gets the value of the entPoints property.
     * 
     */
    public int getEntPoints() {
        return entPoints;
    }

    /**
     * Sets the value of the entPoints property.
     * 
     */
    public void setEntPoints(int value) {
        this.entPoints = value;
    }

    /**
     * Gets the value of the entSerial property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEntSerial() {
        return entSerial;
    }

    /**
     * Sets the value of the entSerial property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEntSerial(String value) {
        this.entSerial = value;
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
     * Gets the value of the middleSchoolGraduationDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getMiddleSchoolGraduationDate() {
        return middleSchoolGraduationDate;
    }

    /**
     * Sets the value of the middleSchoolGraduationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setMiddleSchoolGraduationDate(XMLGregorianCalendar value) {
        this.middleSchoolGraduationDate = value;
    }

    /**
     * Gets the value of the middleSchoolName property.
     * 
     * @return
     *     possible object is
     *     {@link AdditionalName }
     *     
     */
    public AdditionalName getMiddleSchoolName() {
        return middleSchoolName;
    }

    /**
     * Sets the value of the middleSchoolName property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdditionalName }
     *     
     */
    public void setMiddleSchoolName(AdditionalName value) {
        this.middleSchoolName = value;
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
     * Gets the value of the regionName property.
     * 
     * @return
     *     possible object is
     *     {@link AdditionalName }
     *     
     */
    public AdditionalName getRegionName() {
        return regionName;
    }

    /**
     * Sets the value of the regionName property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdditionalName }
     *     
     */
    public void setRegionName(AdditionalName value) {
        this.regionName = value;
    }

    /**
     * Gets the value of the requestResult property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestResult() {
        return requestResult;
    }

    /**
     * Sets the value of the requestResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestResult(String value) {
        this.requestResult = value;
    }

    /**
     * Gets the value of the subjectName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubjectName() {
        return subjectName;
    }

    /**
     * Sets the value of the subjectName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubjectName(String value) {
        this.subjectName = value;
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
