
package kz.tamur.or3.mtszn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for cbdi complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cbdi">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="address" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="birthDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="disabledCauseKZ" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="disabledCauseRU" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="disabledEndDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="disabledGroup" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="disabledPeriod" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="disabledStartDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="docNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="expertOpinionDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="fio" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="issuerKZ" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="issuerRU" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ss_gr_code" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ss_rs_code" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cbdi", propOrder = {
    "address",
    "birthDate",
    "disabledCauseKZ",
    "disabledCauseRU",
    "disabledEndDate",
    "disabledGroup",
    "disabledPeriod",
    "disabledStartDate",
    "docNumber",
    "expertOpinionDate",
    "fio",
    "issuerKZ",
    "issuerRU",
    "ssGrCode",
    "ssRsCode"
})
public class Cbdi {

    @XmlElement(required = true)
    protected String address;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar birthDate;
    @XmlElement(required = true)
    protected String disabledCauseKZ;
    @XmlElement(required = true)
    protected String disabledCauseRU;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar disabledEndDate;
    @XmlElement(required = true)
    protected String disabledGroup;
    @XmlElement(required = true)
    protected String disabledPeriod;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar disabledStartDate;
    @XmlElement(required = true)
    protected String docNumber;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar expertOpinionDate;
    @XmlElement(required = true)
    protected String fio;
    @XmlElement(required = true)
    protected String issuerKZ;
    @XmlElement(required = true)
    protected String issuerRU;
    @XmlElement(name = "ss_gr_code", required = true)
    protected String ssGrCode;
    @XmlElement(name = "ss_rs_code", required = true)
    protected String ssRsCode;

    /**
     * Gets the value of the address property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the value of the address property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddress(String value) {
        this.address = value;
    }

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
     * Gets the value of the disabledCauseKZ property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDisabledCauseKZ() {
        return disabledCauseKZ;
    }

    /**
     * Sets the value of the disabledCauseKZ property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDisabledCauseKZ(String value) {
        this.disabledCauseKZ = value;
    }

    /**
     * Gets the value of the disabledCauseRU property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDisabledCauseRU() {
        return disabledCauseRU;
    }

    /**
     * Sets the value of the disabledCauseRU property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDisabledCauseRU(String value) {
        this.disabledCauseRU = value;
    }

    /**
     * Gets the value of the disabledEndDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDisabledEndDate() {
        return disabledEndDate;
    }

    /**
     * Sets the value of the disabledEndDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDisabledEndDate(XMLGregorianCalendar value) {
        this.disabledEndDate = value;
    }

    /**
     * Gets the value of the disabledGroup property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDisabledGroup() {
        return disabledGroup;
    }

    /**
     * Sets the value of the disabledGroup property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDisabledGroup(String value) {
        this.disabledGroup = value;
    }

    /**
     * Gets the value of the disabledPeriod property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDisabledPeriod() {
        return disabledPeriod;
    }

    /**
     * Sets the value of the disabledPeriod property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDisabledPeriod(String value) {
        this.disabledPeriod = value;
    }

    /**
     * Gets the value of the disabledStartDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDisabledStartDate() {
        return disabledStartDate;
    }

    /**
     * Sets the value of the disabledStartDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDisabledStartDate(XMLGregorianCalendar value) {
        this.disabledStartDate = value;
    }

    /**
     * Gets the value of the docNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocNumber() {
        return docNumber;
    }

    /**
     * Sets the value of the docNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocNumber(String value) {
        this.docNumber = value;
    }

    /**
     * Gets the value of the expertOpinionDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getExpertOpinionDate() {
        return expertOpinionDate;
    }

    /**
     * Sets the value of the expertOpinionDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setExpertOpinionDate(XMLGregorianCalendar value) {
        this.expertOpinionDate = value;
    }

    /**
     * Gets the value of the fio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFio() {
        return fio;
    }

    /**
     * Sets the value of the fio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFio(String value) {
        this.fio = value;
    }

    /**
     * Gets the value of the issuerKZ property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIssuerKZ() {
        return issuerKZ;
    }

    /**
     * Sets the value of the issuerKZ property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIssuerKZ(String value) {
        this.issuerKZ = value;
    }

    /**
     * Gets the value of the issuerRU property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIssuerRU() {
        return issuerRU;
    }

    /**
     * Sets the value of the issuerRU property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIssuerRU(String value) {
        this.issuerRU = value;
    }

    /**
     * Gets the value of the ssGrCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSsGrCode() {
        return ssGrCode;
    }

    /**
     * Sets the value of the ssGrCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSsGrCode(String value) {
        this.ssGrCode = value;
    }

    /**
     * Gets the value of the ssRsCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSsRsCode() {
        return ssRsCode;
    }

    /**
     * Sets the value of the ssRsCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSsRsCode(String value) {
        this.ssRsCode = value;
    }

}
