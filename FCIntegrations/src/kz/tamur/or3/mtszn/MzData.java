
package kz.tamur.or3.mtszn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for mzData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mzData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="diseaseName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="diseaseRegistrationDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="diseaseUnregistrationCause" type="{http://services.sync.mtszn/}name" minOccurs="0"/>
 *         &lt;element name="diseaseUnregistrationDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="hospitalName" type="{http://services.sync.mtszn/}additionalName" minOccurs="0"/>
 *         &lt;element name="sick" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mzData", propOrder = {
    "diseaseName",
    "diseaseRegistrationDate",
    "diseaseUnregistrationCause",
    "diseaseUnregistrationDate",
    "hospitalName",
    "sick"
})
public class MzData {

    protected String diseaseName;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar diseaseRegistrationDate;
    protected Name diseaseUnregistrationCause;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar diseaseUnregistrationDate;
    protected AdditionalName hospitalName;
    protected boolean sick;

    /**
     * Gets the value of the diseaseName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDiseaseName() {
        return diseaseName;
    }

    /**
     * Sets the value of the diseaseName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDiseaseName(String value) {
        this.diseaseName = value;
    }

    /**
     * Gets the value of the diseaseRegistrationDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDiseaseRegistrationDate() {
        return diseaseRegistrationDate;
    }

    /**
     * Sets the value of the diseaseRegistrationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDiseaseRegistrationDate(XMLGregorianCalendar value) {
        this.diseaseRegistrationDate = value;
    }

    /**
     * Gets the value of the diseaseUnregistrationCause property.
     * 
     * @return
     *     possible object is
     *     {@link Name }
     *     
     */
    public Name getDiseaseUnregistrationCause() {
        return diseaseUnregistrationCause;
    }

    /**
     * Sets the value of the diseaseUnregistrationCause property.
     * 
     * @param value
     *     allowed object is
     *     {@link Name }
     *     
     */
    public void setDiseaseUnregistrationCause(Name value) {
        this.diseaseUnregistrationCause = value;
    }

    /**
     * Gets the value of the diseaseUnregistrationDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDiseaseUnregistrationDate() {
        return diseaseUnregistrationDate;
    }

    /**
     * Sets the value of the diseaseUnregistrationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDiseaseUnregistrationDate(XMLGregorianCalendar value) {
        this.diseaseUnregistrationDate = value;
    }

    /**
     * Gets the value of the hospitalName property.
     * 
     * @return
     *     possible object is
     *     {@link AdditionalName }
     *     
     */
    public AdditionalName getHospitalName() {
        return hospitalName;
    }

    /**
     * Sets the value of the hospitalName property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdditionalName }
     *     
     */
    public void setHospitalName(AdditionalName value) {
        this.hospitalName = value;
    }

    /**
     * Gets the value of the sick property.
     * 
     */
    public boolean isSick() {
        return sick;
    }

    /**
     * Sets the value of the sick property.
     * 
     */
    public void setSick(boolean value) {
        this.sick = value;
    }

}
