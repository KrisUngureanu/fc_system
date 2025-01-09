
package kz.tamur.or3.mtszn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for nkData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="nkData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="evidenceNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="evidenceSerial" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ipRegistrationDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="ipUnregistrationDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="rnn" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="taxRegime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="totalPeriodIncome" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "nkData", propOrder = {
    "evidenceNumber",
    "evidenceSerial",
    "ipRegistrationDate",
    "ipUnregistrationDate",
    "name",
    "rnn",
    "taxRegime",
    "totalPeriodIncome"
})
public class NkData {

    protected String evidenceNumber;
    protected String evidenceSerial;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar ipRegistrationDate;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar ipUnregistrationDate;
    protected String name;
    protected String rnn;
    protected String taxRegime;
    protected double totalPeriodIncome;

    /**
     * Gets the value of the evidenceNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEvidenceNumber() {
        return evidenceNumber;
    }

    /**
     * Sets the value of the evidenceNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEvidenceNumber(String value) {
        this.evidenceNumber = value;
    }

    /**
     * Gets the value of the evidenceSerial property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEvidenceSerial() {
        return evidenceSerial;
    }

    /**
     * Sets the value of the evidenceSerial property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEvidenceSerial(String value) {
        this.evidenceSerial = value;
    }

    /**
     * Gets the value of the ipRegistrationDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getIpRegistrationDate() {
        return ipRegistrationDate;
    }

    /**
     * Sets the value of the ipRegistrationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setIpRegistrationDate(XMLGregorianCalendar value) {
        this.ipRegistrationDate = value;
    }

    /**
     * Gets the value of the ipUnregistrationDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getIpUnregistrationDate() {
        return ipUnregistrationDate;
    }

    /**
     * Sets the value of the ipUnregistrationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setIpUnregistrationDate(XMLGregorianCalendar value) {
        this.ipUnregistrationDate = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the rnn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRnn() {
        return rnn;
    }

    /**
     * Sets the value of the rnn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRnn(String value) {
        this.rnn = value;
    }

    /**
     * Gets the value of the taxRegime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTaxRegime() {
        return taxRegime;
    }

    /**
     * Sets the value of the taxRegime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTaxRegime(String value) {
        this.taxRegime = value;
    }

    /**
     * Gets the value of the totalPeriodIncome property.
     * 
     */
    public double getTotalPeriodIncome() {
        return totalPeriodIncome;
    }

    /**
     * Sets the value of the totalPeriodIncome property.
     * 
     */
    public void setTotalPeriodIncome(double value) {
        this.totalPeriodIncome = value;
    }

}
