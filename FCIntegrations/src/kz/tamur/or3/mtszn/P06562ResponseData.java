
package kz.tamur.or3.mtszn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for p06562ResponseData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="p06562ResponseData">
 *   &lt;complexContent>
 *     &lt;extension base="{http://services.sync.mtszn/}baseResponseData">
 *       &lt;sequence>
 *         &lt;element name="birthDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="codeIIN" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="depCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="depId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="depKName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="depRName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="departReasonCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="departReasonId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="departReasonKName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="departReasonRName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="familyCardCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="familyDateFrom" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="familyDateTo" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="firstName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="is_ok" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="lastName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="middleName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "p06562ResponseData", propOrder = {
    "birthDate",
    "codeIIN",
    "depCode",
    "depId",
    "depKName",
    "depRName",
    "departReasonCode",
    "departReasonId",
    "departReasonKName",
    "departReasonRName",
    "familyCardCode",
    "familyDateFrom",
    "familyDateTo",
    "firstName",
    "isOk",
    "lastName",
    "middleName"
})
public class P06562ResponseData
    extends BaseResponseData
{

    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar birthDate;
    protected String codeIIN;
    protected String depCode;
    protected int depId;
    protected String depKName;
    protected String depRName;
    protected String departReasonCode;
    protected int departReasonId;
    protected String departReasonKName;
    protected String departReasonRName;
    protected String familyCardCode;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar familyDateFrom;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar familyDateTo;
    protected String firstName;
    @XmlElement(name = "is_ok")
    protected int isOk;
    protected String lastName;
    protected String middleName;

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
     * Gets the value of the codeIIN property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodeIIN() {
        return codeIIN;
    }

    /**
     * Sets the value of the codeIIN property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodeIIN(String value) {
        this.codeIIN = value;
    }

    /**
     * Gets the value of the depCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDepCode() {
        return depCode;
    }

    /**
     * Sets the value of the depCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDepCode(String value) {
        this.depCode = value;
    }

    /**
     * Gets the value of the depId property.
     * 
     */
    public int getDepId() {
        return depId;
    }

    /**
     * Sets the value of the depId property.
     * 
     */
    public void setDepId(int value) {
        this.depId = value;
    }

    /**
     * Gets the value of the depKName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDepKName() {
        return depKName;
    }

    /**
     * Sets the value of the depKName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDepKName(String value) {
        this.depKName = value;
    }

    /**
     * Gets the value of the depRName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDepRName() {
        return depRName;
    }

    /**
     * Sets the value of the depRName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDepRName(String value) {
        this.depRName = value;
    }

    /**
     * Gets the value of the departReasonCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDepartReasonCode() {
        return departReasonCode;
    }

    /**
     * Sets the value of the departReasonCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDepartReasonCode(String value) {
        this.departReasonCode = value;
    }

    /**
     * Gets the value of the departReasonId property.
     * 
     */
    public int getDepartReasonId() {
        return departReasonId;
    }

    /**
     * Sets the value of the departReasonId property.
     * 
     */
    public void setDepartReasonId(int value) {
        this.departReasonId = value;
    }

    /**
     * Gets the value of the departReasonKName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDepartReasonKName() {
        return departReasonKName;
    }

    /**
     * Sets the value of the departReasonKName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDepartReasonKName(String value) {
        this.departReasonKName = value;
    }

    /**
     * Gets the value of the departReasonRName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDepartReasonRName() {
        return departReasonRName;
    }

    /**
     * Sets the value of the departReasonRName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDepartReasonRName(String value) {
        this.departReasonRName = value;
    }

    /**
     * Gets the value of the familyCardCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFamilyCardCode() {
        return familyCardCode;
    }

    /**
     * Sets the value of the familyCardCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFamilyCardCode(String value) {
        this.familyCardCode = value;
    }

    /**
     * Gets the value of the familyDateFrom property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFamilyDateFrom() {
        return familyDateFrom;
    }

    /**
     * Sets the value of the familyDateFrom property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFamilyDateFrom(XMLGregorianCalendar value) {
        this.familyDateFrom = value;
    }

    /**
     * Gets the value of the familyDateTo property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFamilyDateTo() {
        return familyDateTo;
    }

    /**
     * Sets the value of the familyDateTo property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFamilyDateTo(XMLGregorianCalendar value) {
        this.familyDateTo = value;
    }

    /**
     * Gets the value of the firstName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the value of the firstName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFirstName(String value) {
        this.firstName = value;
    }

    /**
     * Gets the value of the isOk property.
     * 
     */
    public int getIsOk() {
        return isOk;
    }

    /**
     * Sets the value of the isOk property.
     * 
     */
    public void setIsOk(int value) {
        this.isOk = value;
    }

    /**
     * Gets the value of the lastName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the value of the lastName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastName(String value) {
        this.lastName = value;
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

}
