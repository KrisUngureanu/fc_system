
package kz.tamur.or3.mtszn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for workActivity complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="workActivity">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="branch" type="{http://services.sync.mtszn/}additionalName" minOccurs="0"/>
 *         &lt;element name="employeeDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="organization" type="{http://services.sync.mtszn/}name" minOccurs="0"/>
 *         &lt;element name="profession" type="{http://services.sync.mtszn/}additionalName" minOccurs="0"/>
 *         &lt;element name="unemployeeDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "workActivity", propOrder = {
    "branch",
    "employeeDate",
    "organization",
    "profession",
    "unemployeeDate"
})
public class WorkActivity {

    protected AdditionalName branch;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar employeeDate;
    protected Name organization;
    protected AdditionalName profession;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar unemployeeDate;

    /**
     * Gets the value of the branch property.
     * 
     * @return
     *     possible object is
     *     {@link AdditionalName }
     *     
     */
    public AdditionalName getBranch() {
        return branch;
    }

    /**
     * Sets the value of the branch property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdditionalName }
     *     
     */
    public void setBranch(AdditionalName value) {
        this.branch = value;
    }

    /**
     * Gets the value of the employeeDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEmployeeDate() {
        return employeeDate;
    }

    /**
     * Sets the value of the employeeDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEmployeeDate(XMLGregorianCalendar value) {
        this.employeeDate = value;
    }

    /**
     * Gets the value of the organization property.
     * 
     * @return
     *     possible object is
     *     {@link Name }
     *     
     */
    public Name getOrganization() {
        return organization;
    }

    /**
     * Sets the value of the organization property.
     * 
     * @param value
     *     allowed object is
     *     {@link Name }
     *     
     */
    public void setOrganization(Name value) {
        this.organization = value;
    }

    /**
     * Gets the value of the profession property.
     * 
     * @return
     *     possible object is
     *     {@link AdditionalName }
     *     
     */
    public AdditionalName getProfession() {
        return profession;
    }

    /**
     * Sets the value of the profession property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdditionalName }
     *     
     */
    public void setProfession(AdditionalName value) {
        this.profession = value;
    }

    /**
     * Gets the value of the unemployeeDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getUnemployeeDate() {
        return unemployeeDate;
    }

    /**
     * Sets the value of the unemployeeDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setUnemployeeDate(XMLGregorianCalendar value) {
        this.unemployeeDate = value;
    }

}
