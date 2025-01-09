
package kz.tamur.or3.mtszn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for lesson complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="lesson">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="date_open" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="eduOrgKName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="eduOrgRName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="edu_learn_code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="edu_learn_id" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="institutCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="institutKName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="institutRName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="learnFormCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="learnFormKName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="learnFormRName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="learnLangCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="learnLangKName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="learnLangRName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="prof_note" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="vacancy_note" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "lesson", propOrder = {
    "dateOpen",
    "eduOrgKName",
    "eduOrgRName",
    "eduLearnCode",
    "eduLearnId",
    "institutCode",
    "institutKName",
    "institutRName",
    "learnFormCode",
    "learnFormKName",
    "learnFormRName",
    "learnLangCode",
    "learnLangKName",
    "learnLangRName",
    "profNote",
    "vacancyNote"
})
public class Lesson {

    @XmlElement(name = "date_open")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateOpen;
    protected String eduOrgKName;
    protected String eduOrgRName;
    @XmlElement(name = "edu_learn_code")
    protected String eduLearnCode;
    @XmlElement(name = "edu_learn_id")
    protected int eduLearnId;
    protected String institutCode;
    protected String institutKName;
    protected String institutRName;
    protected String learnFormCode;
    protected String learnFormKName;
    protected String learnFormRName;
    protected String learnLangCode;
    protected String learnLangKName;
    protected String learnLangRName;
    @XmlElement(name = "prof_note")
    protected String profNote;
    @XmlElement(name = "vacancy_note")
    protected String vacancyNote;

    /**
     * Gets the value of the dateOpen property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateOpen() {
        return dateOpen;
    }

    /**
     * Sets the value of the dateOpen property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateOpen(XMLGregorianCalendar value) {
        this.dateOpen = value;
    }

    /**
     * Gets the value of the eduOrgKName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEduOrgKName() {
        return eduOrgKName;
    }

    /**
     * Sets the value of the eduOrgKName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEduOrgKName(String value) {
        this.eduOrgKName = value;
    }

    /**
     * Gets the value of the eduOrgRName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEduOrgRName() {
        return eduOrgRName;
    }

    /**
     * Sets the value of the eduOrgRName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEduOrgRName(String value) {
        this.eduOrgRName = value;
    }

    /**
     * Gets the value of the eduLearnCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEduLearnCode() {
        return eduLearnCode;
    }

    /**
     * Sets the value of the eduLearnCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEduLearnCode(String value) {
        this.eduLearnCode = value;
    }

    /**
     * Gets the value of the eduLearnId property.
     * 
     */
    public int getEduLearnId() {
        return eduLearnId;
    }

    /**
     * Sets the value of the eduLearnId property.
     * 
     */
    public void setEduLearnId(int value) {
        this.eduLearnId = value;
    }

    /**
     * Gets the value of the institutCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInstitutCode() {
        return institutCode;
    }

    /**
     * Sets the value of the institutCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInstitutCode(String value) {
        this.institutCode = value;
    }

    /**
     * Gets the value of the institutKName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInstitutKName() {
        return institutKName;
    }

    /**
     * Sets the value of the institutKName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInstitutKName(String value) {
        this.institutKName = value;
    }

    /**
     * Gets the value of the institutRName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInstitutRName() {
        return institutRName;
    }

    /**
     * Sets the value of the institutRName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInstitutRName(String value) {
        this.institutRName = value;
    }

    /**
     * Gets the value of the learnFormCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLearnFormCode() {
        return learnFormCode;
    }

    /**
     * Sets the value of the learnFormCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLearnFormCode(String value) {
        this.learnFormCode = value;
    }

    /**
     * Gets the value of the learnFormKName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLearnFormKName() {
        return learnFormKName;
    }

    /**
     * Sets the value of the learnFormKName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLearnFormKName(String value) {
        this.learnFormKName = value;
    }

    /**
     * Gets the value of the learnFormRName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLearnFormRName() {
        return learnFormRName;
    }

    /**
     * Sets the value of the learnFormRName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLearnFormRName(String value) {
        this.learnFormRName = value;
    }

    /**
     * Gets the value of the learnLangCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLearnLangCode() {
        return learnLangCode;
    }

    /**
     * Sets the value of the learnLangCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLearnLangCode(String value) {
        this.learnLangCode = value;
    }

    /**
     * Gets the value of the learnLangKName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLearnLangKName() {
        return learnLangKName;
    }

    /**
     * Sets the value of the learnLangKName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLearnLangKName(String value) {
        this.learnLangKName = value;
    }

    /**
     * Gets the value of the learnLangRName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLearnLangRName() {
        return learnLangRName;
    }

    /**
     * Sets the value of the learnLangRName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLearnLangRName(String value) {
        this.learnLangRName = value;
    }

    /**
     * Gets the value of the profNote property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProfNote() {
        return profNote;
    }

    /**
     * Sets the value of the profNote property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProfNote(String value) {
        this.profNote = value;
    }

    /**
     * Gets the value of the vacancyNote property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVacancyNote() {
        return vacancyNote;
    }

    /**
     * Sets the value of the vacancyNote property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVacancyNote(String value) {
        this.vacancyNote = value;
    }

}
