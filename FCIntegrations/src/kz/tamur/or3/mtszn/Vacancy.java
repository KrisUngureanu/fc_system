
package kz.tamur.or3.mtszn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for vacancy complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="vacancy">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="age" type="{http://services.sync.mtszn/}textInfo" minOccurs="0"/>
 *         &lt;element name="date_f1tn" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="education" type="{http://services.sync.mtszn/}textInfo" minOccurs="0"/>
 *         &lt;element name="emp_f1tn_id" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="emp_kname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="emp_rname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="f1tn_code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="house" type="{http://services.sync.mtszn/}textInfo" minOccurs="0"/>
 *         &lt;element name="kindergart" type="{http://services.sync.mtszn/}textInfo" minOccurs="0"/>
 *         &lt;element name="prof_note" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="salary" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="vacancy_note" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="work_cond" type="{http://services.sync.mtszn/}textInfo" minOccurs="0"/>
 *         &lt;element name="work_oper" type="{http://services.sync.mtszn/}textInfo" minOccurs="0"/>
 *         &lt;element name="work_pay" type="{http://services.sync.mtszn/}textInfo" minOccurs="0"/>
 *         &lt;element name="work_spec" type="{http://services.sync.mtszn/}textInfo" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "vacancy", propOrder = {
    "age",
    "dateF1Tn",
    "education",
    "empF1TnId",
    "empKname",
    "empRname",
    "f1TnCode",
    "house",
    "kindergart",
    "profNote",
    "salary",
    "vacancyNote",
    "workCond",
    "workOper",
    "workPay",
    "workSpec"
})
public class Vacancy {

    protected TextInfo age;
    @XmlElement(name = "date_f1tn")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateF1Tn;
    protected TextInfo education;
    @XmlElement(name = "emp_f1tn_id")
    protected int empF1TnId;
    @XmlElement(name = "emp_kname")
    protected String empKname;
    @XmlElement(name = "emp_rname")
    protected String empRname;
    @XmlElement(name = "f1tn_code")
    protected String f1TnCode;
    protected TextInfo house;
    protected TextInfo kindergart;
    @XmlElement(name = "prof_note")
    protected String profNote;
    protected int salary;
    @XmlElement(name = "vacancy_note")
    protected String vacancyNote;
    @XmlElement(name = "work_cond")
    protected TextInfo workCond;
    @XmlElement(name = "work_oper")
    protected TextInfo workOper;
    @XmlElement(name = "work_pay")
    protected TextInfo workPay;
    @XmlElement(name = "work_spec")
    protected TextInfo workSpec;

    /**
     * Gets the value of the age property.
     * 
     * @return
     *     possible object is
     *     {@link TextInfo }
     *     
     */
    public TextInfo getAge() {
        return age;
    }

    /**
     * Sets the value of the age property.
     * 
     * @param value
     *     allowed object is
     *     {@link TextInfo }
     *     
     */
    public void setAge(TextInfo value) {
        this.age = value;
    }

    /**
     * Gets the value of the dateF1Tn property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateF1Tn() {
        return dateF1Tn;
    }

    /**
     * Sets the value of the dateF1Tn property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateF1Tn(XMLGregorianCalendar value) {
        this.dateF1Tn = value;
    }

    /**
     * Gets the value of the education property.
     * 
     * @return
     *     possible object is
     *     {@link TextInfo }
     *     
     */
    public TextInfo getEducation() {
        return education;
    }

    /**
     * Sets the value of the education property.
     * 
     * @param value
     *     allowed object is
     *     {@link TextInfo }
     *     
     */
    public void setEducation(TextInfo value) {
        this.education = value;
    }

    /**
     * Gets the value of the empF1TnId property.
     * 
     */
    public int getEmpF1TnId() {
        return empF1TnId;
    }

    /**
     * Sets the value of the empF1TnId property.
     * 
     */
    public void setEmpF1TnId(int value) {
        this.empF1TnId = value;
    }

    /**
     * Gets the value of the empKname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmpKname() {
        return empKname;
    }

    /**
     * Sets the value of the empKname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmpKname(String value) {
        this.empKname = value;
    }

    /**
     * Gets the value of the empRname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmpRname() {
        return empRname;
    }

    /**
     * Sets the value of the empRname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmpRname(String value) {
        this.empRname = value;
    }

    /**
     * Gets the value of the f1TnCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getF1TnCode() {
        return f1TnCode;
    }

    /**
     * Sets the value of the f1TnCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setF1TnCode(String value) {
        this.f1TnCode = value;
    }

    /**
     * Gets the value of the house property.
     * 
     * @return
     *     possible object is
     *     {@link TextInfo }
     *     
     */
    public TextInfo getHouse() {
        return house;
    }

    /**
     * Sets the value of the house property.
     * 
     * @param value
     *     allowed object is
     *     {@link TextInfo }
     *     
     */
    public void setHouse(TextInfo value) {
        this.house = value;
    }

    /**
     * Gets the value of the kindergart property.
     * 
     * @return
     *     possible object is
     *     {@link TextInfo }
     *     
     */
    public TextInfo getKindergart() {
        return kindergart;
    }

    /**
     * Sets the value of the kindergart property.
     * 
     * @param value
     *     allowed object is
     *     {@link TextInfo }
     *     
     */
    public void setKindergart(TextInfo value) {
        this.kindergart = value;
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
     * Gets the value of the salary property.
     * 
     */
    public int getSalary() {
        return salary;
    }

    /**
     * Sets the value of the salary property.
     * 
     */
    public void setSalary(int value) {
        this.salary = value;
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

    /**
     * Gets the value of the workCond property.
     * 
     * @return
     *     possible object is
     *     {@link TextInfo }
     *     
     */
    public TextInfo getWorkCond() {
        return workCond;
    }

    /**
     * Sets the value of the workCond property.
     * 
     * @param value
     *     allowed object is
     *     {@link TextInfo }
     *     
     */
    public void setWorkCond(TextInfo value) {
        this.workCond = value;
    }

    /**
     * Gets the value of the workOper property.
     * 
     * @return
     *     possible object is
     *     {@link TextInfo }
     *     
     */
    public TextInfo getWorkOper() {
        return workOper;
    }

    /**
     * Sets the value of the workOper property.
     * 
     * @param value
     *     allowed object is
     *     {@link TextInfo }
     *     
     */
    public void setWorkOper(TextInfo value) {
        this.workOper = value;
    }

    /**
     * Gets the value of the workPay property.
     * 
     * @return
     *     possible object is
     *     {@link TextInfo }
     *     
     */
    public TextInfo getWorkPay() {
        return workPay;
    }

    /**
     * Sets the value of the workPay property.
     * 
     * @param value
     *     allowed object is
     *     {@link TextInfo }
     *     
     */
    public void setWorkPay(TextInfo value) {
        this.workPay = value;
    }

    /**
     * Gets the value of the workSpec property.
     * 
     * @return
     *     possible object is
     *     {@link TextInfo }
     *     
     */
    public TextInfo getWorkSpec() {
        return workSpec;
    }

    /**
     * Sets the value of the workSpec property.
     * 
     * @param value
     *     allowed object is
     *     {@link TextInfo }
     *     
     */
    public void setWorkSpec(TextInfo value) {
        this.workSpec = value;
    }

}
