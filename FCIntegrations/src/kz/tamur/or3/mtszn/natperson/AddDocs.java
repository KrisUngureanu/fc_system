
package kz.tamur.or3.mtszn.natperson;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for AddDocs complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AddDocs">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="birthSvidNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="birthSvidBeginDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="birthSvidIssueOrg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="deathSvidNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="deathSvidBeginDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="deathSvidIssueOrg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AddDocs", propOrder = {
    "birthSvidNumber",
    "birthSvidBeginDate",
    "birthSvidIssueOrg",
    "deathSvidNumber",
    "deathSvidBeginDate",
    "deathSvidIssueOrg"
})
public class AddDocs {

    protected String birthSvidNumber;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar birthSvidBeginDate;
    protected String birthSvidIssueOrg;
    protected String deathSvidNumber;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar deathSvidBeginDate;
    protected String deathSvidIssueOrg;

    /**
     * Gets the value of the birthSvidNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBirthSvidNumber() {
        return birthSvidNumber;
    }

    /**
     * Sets the value of the birthSvidNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBirthSvidNumber(String value) {
        this.birthSvidNumber = value;
    }

    /**
     * Gets the value of the birthSvidBeginDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getBirthSvidBeginDate() {
        return birthSvidBeginDate;
    }

    /**
     * Sets the value of the birthSvidBeginDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setBirthSvidBeginDate(XMLGregorianCalendar value) {
        this.birthSvidBeginDate = value;
    }

    /**
     * Gets the value of the birthSvidIssueOrg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBirthSvidIssueOrg() {
        return birthSvidIssueOrg;
    }

    /**
     * Sets the value of the birthSvidIssueOrg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBirthSvidIssueOrg(String value) {
        this.birthSvidIssueOrg = value;
    }

    /**
     * Gets the value of the deathSvidNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeathSvidNumber() {
        return deathSvidNumber;
    }

    /**
     * Sets the value of the deathSvidNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeathSvidNumber(String value) {
        this.deathSvidNumber = value;
    }

    /**
     * Gets the value of the deathSvidBeginDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDeathSvidBeginDate() {
        return deathSvidBeginDate;
    }

    /**
     * Sets the value of the deathSvidBeginDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDeathSvidBeginDate(XMLGregorianCalendar value) {
        this.deathSvidBeginDate = value;
    }

    /**
     * Gets the value of the deathSvidIssueOrg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeathSvidIssueOrg() {
        return deathSvidIssueOrg;
    }

    /**
     * Sets the value of the deathSvidIssueOrg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeathSvidIssueOrg(String value) {
        this.deathSvidIssueOrg = value;
    }

}
