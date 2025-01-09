
package kz.tamur.or3.mtszn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for mon_high complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mon_high">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="birthdate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="educationType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="iin" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="schoolName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="specialtyName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="studentStatus" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="surname" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="month1" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="month1amount" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="month2" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="month2amount" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="month3" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="month3amount" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mon_high", propOrder = {
    "birthdate",
    "educationType",
    "iin",
    "schoolName",
    "specialtyName",
    "studentStatus",
    "surname",
    "month1",
    "month1Amount",
    "month2",
    "month2Amount",
    "month3",
    "month3Amount"
})
public class MonHigh {

    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar birthdate;
    @XmlElement(required = true)
    protected String educationType;
    @XmlElement(required = true)
    protected String iin;
    @XmlElement(required = true)
    protected String schoolName;
    @XmlElement(required = true)
    protected String specialtyName;
    @XmlElement(required = true)
    protected String studentStatus;
    @XmlElement(required = true)
    protected String surname;
    @XmlElement(required = true)
    protected String month1;
    @XmlElement(name = "month1amount")
    protected float month1Amount;
    @XmlElement(required = true)
    protected String month2;
    @XmlElement(name = "month2amount")
    protected float month2Amount;
    @XmlElement(required = true)
    protected String month3;
    @XmlElement(name = "month3amount")
    protected float month3Amount;

    /**
     * Gets the value of the birthdate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getBirthdate() {
        return birthdate;
    }

    /**
     * Sets the value of the birthdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setBirthdate(XMLGregorianCalendar value) {
        this.birthdate = value;
    }

    /**
     * Gets the value of the educationType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEducationType() {
        return educationType;
    }

    /**
     * Sets the value of the educationType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEducationType(String value) {
        this.educationType = value;
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
     * Gets the value of the schoolName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSchoolName() {
        return schoolName;
    }

    /**
     * Sets the value of the schoolName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSchoolName(String value) {
        this.schoolName = value;
    }

    /**
     * Gets the value of the specialtyName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpecialtyName() {
        return specialtyName;
    }

    /**
     * Sets the value of the specialtyName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpecialtyName(String value) {
        this.specialtyName = value;
    }

    /**
     * Gets the value of the studentStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStudentStatus() {
        return studentStatus;
    }

    /**
     * Sets the value of the studentStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStudentStatus(String value) {
        this.studentStatus = value;
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
     * Gets the value of the month1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMonth1() {
        return month1;
    }

    /**
     * Sets the value of the month1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMonth1(String value) {
        this.month1 = value;
    }

    /**
     * Gets the value of the month1Amount property.
     * 
     */
    public float getMonth1Amount() {
        return month1Amount;
    }

    /**
     * Sets the value of the month1Amount property.
     * 
     */
    public void setMonth1Amount(float value) {
        this.month1Amount = value;
    }

    /**
     * Gets the value of the month2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMonth2() {
        return month2;
    }

    /**
     * Sets the value of the month2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMonth2(String value) {
        this.month2 = value;
    }

    /**
     * Gets the value of the month2Amount property.
     * 
     */
    public float getMonth2Amount() {
        return month2Amount;
    }

    /**
     * Sets the value of the month2Amount property.
     * 
     */
    public void setMonth2Amount(float value) {
        this.month2Amount = value;
    }

    /**
     * Gets the value of the month3 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMonth3() {
        return month3;
    }

    /**
     * Sets the value of the month3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMonth3(String value) {
        this.month3 = value;
    }

    /**
     * Gets the value of the month3Amount property.
     * 
     */
    public float getMonth3Amount() {
        return month3Amount;
    }

    /**
     * Sets the value of the month3Amount property.
     * 
     */
    public void setMonth3Amount(float value) {
        this.month3Amount = value;
    }

}
