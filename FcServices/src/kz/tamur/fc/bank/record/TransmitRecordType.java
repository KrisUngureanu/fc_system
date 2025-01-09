
package kz.tamur.fc.bank.record;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * Реквизиты для перевода  депозита из одного банка в другой
 * 
 * <p>Java class for TransmitRecordType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TransmitRecordType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="regRecordNumber" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
 *         &lt;element name="codeBank" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
 *         &lt;element name="schetNumber" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
 *         &lt;element name="currNumber" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
 *         &lt;element name="transitNumber" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
 *         &lt;element name="transitNumber_t" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="transitDate" type="{http://www.w3.org/2001/XMLSchema}date" form="qualified"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransmitRecordType", propOrder = {
    "regRecordNumber",
    "codeBank",
    "schetNumber",
    "currNumber",
    "transitNumber",
    "transitNumberT",
    "transitDate"
})
public class TransmitRecordType {

    @XmlElement(required = true)
    protected String regRecordNumber;
    @XmlElement(required = true)
    protected String codeBank;
    @XmlElement(required = true)
    protected String schetNumber;
    @XmlElement(required = true)
    protected String currNumber;
    @XmlElement(required = true)
    protected String transitNumber;
    @XmlElement(name = "transitNumber_t", required = true)
    protected String transitNumberT;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar transitDate;

    /**
     * Gets the value of the regRecordNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegRecordNumber() {
        return regRecordNumber;
    }

    /**
     * Sets the value of the regRecordNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegRecordNumber(String value) {
        this.regRecordNumber = value;
    }

    /**
     * Gets the value of the codeBank property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodeBank() {
        return codeBank;
    }

    /**
     * Sets the value of the codeBank property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodeBank(String value) {
        this.codeBank = value;
    }

    /**
     * Gets the value of the schetNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSchetNumber() {
        return schetNumber;
    }

    /**
     * Sets the value of the schetNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSchetNumber(String value) {
        this.schetNumber = value;
    }

    /**
     * Gets the value of the currNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCurrNumber() {
        return currNumber;
    }

    /**
     * Sets the value of the currNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCurrNumber(String value) {
        this.currNumber = value;
    }

    /**
     * Gets the value of the transitNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransitNumber() {
        return transitNumber;
    }

    /**
     * Sets the value of the transitNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransitNumber(String value) {
        this.transitNumber = value;
    }

    /**
     * Gets the value of the transitNumberT property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransitNumberT() {
        return transitNumberT;
    }

    /**
     * Sets the value of the transitNumberT property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransitNumberT(String value) {
        this.transitNumberT = value;
    }

    /**
     * Gets the value of the transitDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTransitDate() {
        return transitDate;
    }

    /**
     * Sets the value of the transitDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTransitDate(XMLGregorianCalendar value) {
        this.transitDate = value;
    }

}
