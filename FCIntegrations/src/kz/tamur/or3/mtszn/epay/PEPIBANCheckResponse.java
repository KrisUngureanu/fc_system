
package kz.tamur.or3.mtszn.epay;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for PEP_IBANCheckResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PEP_IBANCheckResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="BranchName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OrderNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OrgOrderNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OrderRunDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="Status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OrgBIC" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OrgIIC" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OrgRNN" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OrgBIN" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="AccountCreditingType" type="{http://epay.gov.kz/IBANService}AccountCreditingType"/>
 *         &lt;element name="IBAN" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PEP_IBANCheckResponse", propOrder = {
    "branchName",
    "orderNumber",
    "orgOrderNumber",
    "orderRunDate",
    "status",
    "orgBIC",
    "orgIIC",
    "orgRNN",
    "orgBIN",
    "accountCreditingType",
    "iban"
})
public class PEPIBANCheckResponse {

    @XmlElement(name = "BranchName")
    protected String branchName;
    @XmlElement(name = "OrderNumber")
    protected String orderNumber;
    @XmlElement(name = "OrgOrderNumber")
    protected String orgOrderNumber;
    @XmlElement(name = "OrderRunDate", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar orderRunDate;
    @XmlElement(name = "Status")
    protected String status;
    @XmlElement(name = "OrgBIC")
    protected String orgBIC;
    @XmlElement(name = "OrgIIC")
    protected String orgIIC;
    @XmlElement(name = "OrgRNN")
    protected String orgRNN;
    @XmlElement(name = "OrgBIN")
    protected String orgBIN;
    @XmlElement(name = "AccountCreditingType", required = true)
    protected AccountCreditingType accountCreditingType;
    @XmlElement(name = "IBAN", required = true)
    protected String iban;

    /**
     * Gets the value of the branchName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBranchName() {
        return branchName;
    }

    /**
     * Sets the value of the branchName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBranchName(String value) {
        this.branchName = value;
    }

    /**
     * Gets the value of the orderNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrderNumber() {
        return orderNumber;
    }

    /**
     * Sets the value of the orderNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrderNumber(String value) {
        this.orderNumber = value;
    }

    /**
     * Gets the value of the orgOrderNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrgOrderNumber() {
        return orgOrderNumber;
    }

    /**
     * Sets the value of the orgOrderNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrgOrderNumber(String value) {
        this.orgOrderNumber = value;
    }

    /**
     * Gets the value of the orderRunDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getOrderRunDate() {
        return orderRunDate;
    }

    /**
     * Sets the value of the orderRunDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setOrderRunDate(XMLGregorianCalendar value) {
        this.orderRunDate = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatus(String value) {
        this.status = value;
    }

    /**
     * Gets the value of the orgBIC property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrgBIC() {
        return orgBIC;
    }

    /**
     * Sets the value of the orgBIC property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrgBIC(String value) {
        this.orgBIC = value;
    }

    /**
     * Gets the value of the orgIIC property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrgIIC() {
        return orgIIC;
    }

    /**
     * Sets the value of the orgIIC property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrgIIC(String value) {
        this.orgIIC = value;
    }

    /**
     * Gets the value of the orgRNN property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrgRNN() {
        return orgRNN;
    }

    /**
     * Sets the value of the orgRNN property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrgRNN(String value) {
        this.orgRNN = value;
    }

    /**
     * Gets the value of the orgBIN property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrgBIN() {
        return orgBIN;
    }

    /**
     * Sets the value of the orgBIN property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrgBIN(String value) {
        this.orgBIN = value;
    }

    /**
     * Gets the value of the accountCreditingType property.
     * 
     * @return
     *     possible object is
     *     {@link AccountCreditingType }
     *     
     */
    public AccountCreditingType getAccountCreditingType() {
        return accountCreditingType;
    }

    /**
     * Sets the value of the accountCreditingType property.
     * 
     * @param value
     *     allowed object is
     *     {@link AccountCreditingType }
     *     
     */
    public void setAccountCreditingType(AccountCreditingType value) {
        this.accountCreditingType = value;
    }

    /**
     * Gets the value of the iban property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIBAN() {
        return iban;
    }

    /**
     * Sets the value of the iban property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIBAN(String value) {
        this.iban = value;
    }

}
