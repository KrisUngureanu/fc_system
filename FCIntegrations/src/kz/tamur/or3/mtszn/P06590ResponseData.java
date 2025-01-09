
package kz.tamur.or3.mtszn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for p06590ResponseData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="p06590ResponseData">
 *   &lt;complexContent>
 *     &lt;extension base="{http://services.sync.mtszn/}baseResponseData">
 *       &lt;sequence>
 *         &lt;element name="address" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="birthDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="district" type="{http://services.sync.mtszn/}additionalName" minOccurs="0"/>
 *         &lt;element name="docUotNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="effectiveUotDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="expertOpinionUotDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="fio" type="{http://services.sync.mtszn/}fio" minOccurs="0"/>
 *         &lt;element name="mse" type="{http://services.sync.mtszn/}additionalName" minOccurs="0"/>
 *         &lt;element name="uotEndDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="uotPercent" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="uotPeriod" type="{http://services.sync.mtszn/}additionalName" minOccurs="0"/>
 *         &lt;element name="uotStartDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "p06590ResponseData", propOrder = {
    "address",
    "birthDate",
    "district",
    "docUotNumber",
    "effectiveUotDate",
    "expertOpinionUotDate",
    "fio",
    "mse",
    "uotEndDate",
    "uotPercent",
    "uotPeriod",
    "uotStartDate"
})
public class P06590ResponseData
    extends BaseResponseData
{

    protected String address;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar birthDate;
    protected AdditionalName district;
    protected String docUotNumber;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar effectiveUotDate;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar expertOpinionUotDate;
    protected Fio fio;
    protected AdditionalName mse;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar uotEndDate;
    protected double uotPercent;
    protected AdditionalName uotPeriod;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar uotStartDate;

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
     * Gets the value of the district property.
     * 
     * @return
     *     possible object is
     *     {@link AdditionalName }
     *     
     */
    public AdditionalName getDistrict() {
        return district;
    }

    /**
     * Sets the value of the district property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdditionalName }
     *     
     */
    public void setDistrict(AdditionalName value) {
        this.district = value;
    }

    /**
     * Gets the value of the docUotNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocUotNumber() {
        return docUotNumber;
    }

    /**
     * Sets the value of the docUotNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocUotNumber(String value) {
        this.docUotNumber = value;
    }

    /**
     * Gets the value of the effectiveUotDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEffectiveUotDate() {
        return effectiveUotDate;
    }

    /**
     * Sets the value of the effectiveUotDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEffectiveUotDate(XMLGregorianCalendar value) {
        this.effectiveUotDate = value;
    }

    /**
     * Gets the value of the expertOpinionUotDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getExpertOpinionUotDate() {
        return expertOpinionUotDate;
    }

    /**
     * Sets the value of the expertOpinionUotDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setExpertOpinionUotDate(XMLGregorianCalendar value) {
        this.expertOpinionUotDate = value;
    }

    /**
     * Gets the value of the fio property.
     * 
     * @return
     *     possible object is
     *     {@link Fio }
     *     
     */
    public Fio getFio() {
        return fio;
    }

    /**
     * Sets the value of the fio property.
     * 
     * @param value
     *     allowed object is
     *     {@link Fio }
     *     
     */
    public void setFio(Fio value) {
        this.fio = value;
    }

    /**
     * Gets the value of the mse property.
     * 
     * @return
     *     possible object is
     *     {@link AdditionalName }
     *     
     */
    public AdditionalName getMse() {
        return mse;
    }

    /**
     * Sets the value of the mse property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdditionalName }
     *     
     */
    public void setMse(AdditionalName value) {
        this.mse = value;
    }

    /**
     * Gets the value of the uotEndDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getUotEndDate() {
        return uotEndDate;
    }

    /**
     * Sets the value of the uotEndDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setUotEndDate(XMLGregorianCalendar value) {
        this.uotEndDate = value;
    }

    /**
     * Gets the value of the uotPercent property.
     * 
     */
    public double getUotPercent() {
        return uotPercent;
    }

    /**
     * Sets the value of the uotPercent property.
     * 
     */
    public void setUotPercent(double value) {
        this.uotPercent = value;
    }

    /**
     * Gets the value of the uotPeriod property.
     * 
     * @return
     *     possible object is
     *     {@link AdditionalName }
     *     
     */
    public AdditionalName getUotPeriod() {
        return uotPeriod;
    }

    /**
     * Sets the value of the uotPeriod property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdditionalName }
     *     
     */
    public void setUotPeriod(AdditionalName value) {
        this.uotPeriod = value;
    }

    /**
     * Gets the value of the uotStartDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getUotStartDate() {
        return uotStartDate;
    }

    /**
     * Sets the value of the uotStartDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setUotStartDate(XMLGregorianCalendar value) {
        this.uotStartDate = value;
    }

}
