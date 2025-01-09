
package kz.tamur.fc.bank.record;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * Запрос  на передачу реквизитов перевода депозита в пользу третьего лица
 * 
 * <p>Java class for getTransmitRecordType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getTransmitRecordType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="head" type="{http://record.bank.fc.tamur.kz}SystemInfo" form="qualified"/>
 *         &lt;element name="regRecordNumberFrom" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
 *         &lt;element name="regRecordNumberTo" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
 *         &lt;element name="codeBankFrom" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
 *         &lt;element name="codeBankTo" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
 *         &lt;element name="schetNumberFrom" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
 *         &lt;element name="schetNumberTo" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
 *         &lt;element name="schetNumber" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
 *         &lt;element name="sumDepozitFrom" type="{http://www.w3.org/2001/XMLSchema}double" form="qualified"/>
 *         &lt;element name="processDate" type="{http://www.w3.org/2001/XMLSchema}date" form="qualified"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getTransmitRecordType", propOrder = {
    "head",
    "regRecordNumberFrom",
    "regRecordNumberTo",
    "codeBankFrom",
    "codeBankTo",
    "schetNumberFrom",
    "schetNumberTo",
    "schetNumber",
    "sumDepozitFrom",
    "processDate"
})
public class GetTransmitRecordType {

    @XmlElement(required = true)
    protected SystemInfo head;
    @XmlElement(required = true)
    protected String regRecordNumberFrom;
    @XmlElement(required = true)
    protected String regRecordNumberTo;
    @XmlElement(required = true)
    protected String codeBankFrom;
    @XmlElement(required = true)
    protected String codeBankTo;
    @XmlElement(required = true)
    protected String schetNumberFrom;
    @XmlElement(required = true)
    protected String schetNumberTo;
    @XmlElement(required = true)
    protected String schetNumber;
    protected double sumDepozitFrom;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar processDate;

    /**
     * Gets the value of the head property.
     * 
     * @return
     *     possible object is
     *     {@link SystemInfo }
     *     
     */
    public SystemInfo getHead() {
        return head;
    }

    /**
     * Sets the value of the head property.
     * 
     * @param value
     *     allowed object is
     *     {@link SystemInfo }
     *     
     */
    public void setHead(SystemInfo value) {
        this.head = value;
    }

    /**
     * Gets the value of the regRecordNumberFrom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegRecordNumberFrom() {
        return regRecordNumberFrom;
    }

    /**
     * Sets the value of the regRecordNumberFrom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegRecordNumberFrom(String value) {
        this.regRecordNumberFrom = value;
    }

    /**
     * Gets the value of the regRecordNumberTo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegRecordNumberTo() {
        return regRecordNumberTo;
    }

    /**
     * Sets the value of the regRecordNumberTo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegRecordNumberTo(String value) {
        this.regRecordNumberTo = value;
    }

    /**
     * Gets the value of the codeBankFrom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodeBankFrom() {
        return codeBankFrom;
    }

    /**
     * Sets the value of the codeBankFrom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodeBankFrom(String value) {
        this.codeBankFrom = value;
    }

    /**
     * Gets the value of the codeBankTo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodeBankTo() {
        return codeBankTo;
    }

    /**
     * Sets the value of the codeBankTo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodeBankTo(String value) {
        this.codeBankTo = value;
    }

    /**
     * Gets the value of the schetNumberFrom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSchetNumberFrom() {
        return schetNumberFrom;
    }

    /**
     * Sets the value of the schetNumberFrom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSchetNumberFrom(String value) {
        this.schetNumberFrom = value;
    }

    /**
     * Gets the value of the schetNumberTo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSchetNumberTo() {
        return schetNumberTo;
    }

    /**
     * Sets the value of the schetNumberTo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSchetNumberTo(String value) {
        this.schetNumberTo = value;
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
     * Gets the value of the sumDepozitFrom property.
     * 
     */
    public double getSumDepozitFrom() {
        return sumDepozitFrom;
    }

    /**
     * Sets the value of the sumDepozitFrom property.
     * 
     */
    public void setSumDepozitFrom(double value) {
        this.sumDepozitFrom = value;
    }

    /**
     * Gets the value of the processDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getProcessDate() {
        return processDate;
    }

    /**
     * Sets the value of the processDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setProcessDate(XMLGregorianCalendar value) {
        this.processDate = value;
    }

}
