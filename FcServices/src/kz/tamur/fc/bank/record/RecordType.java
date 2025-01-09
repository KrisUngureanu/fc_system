
package kz.tamur.fc.bank.record;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * Данные депозита
 * 
 * <p>Java class for RecordType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RecordType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="dogovorNumber" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
 *         &lt;element name="recordNumber" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
 *         &lt;element name="recordDate" type="{http://www.w3.org/2001/XMLSchema}date" form="qualified"/>
 *         &lt;element name="codeBank" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
 *         &lt;element name="codeKato" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
 *         &lt;element name="district" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
 *         &lt;element name="moneyAmount" type="{http://www.w3.org/2001/XMLSchema}double" form="qualified"/>
 *         &lt;element name="term" type="{http://www.w3.org/2001/XMLSchema}long" form="qualified"/>
 *         &lt;element name="priority" type="{http://record.bank.fc.tamur.kz}DictionaryType" form="qualified"/>
 *         &lt;element name="closeDate" type="{http://www.w3.org/2001/XMLSchema}date" form="qualified"/>
 *         &lt;element name="closeReason" type="{http://record.bank.fc.tamur.kz}DictionaryType" form="qualified"/>
 *         &lt;element name="transitNumber" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
 *         &lt;element name="transitCurrNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="transitDate" type="{http://www.w3.org/2001/XMLSchema}date" form="qualified"/>
 *         &lt;element name="info" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
 *         &lt;element name="srokDog" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RecordType", propOrder = {
    "dogovorNumber",
    "recordNumber",
    "recordDate",
    "codeBank",
    "codeKato",
    "district",
    "moneyAmount",
    "term",
    "priority",
    "closeDate",
    "closeReason",
    "transitNumber",
    "transitCurrNumber",
    "transitDate",
    "info",
    "srokDog"
})
public class RecordType {

    @XmlElement(required = true)
    protected String dogovorNumber;
    @XmlElement(required = true)
    protected String recordNumber;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar recordDate;
    @XmlElement(required = true)
    protected String codeBank;
    @XmlElement(required = true)
    protected String codeKato;
    @XmlElement(required = true)
    protected String district;
    protected double moneyAmount;
    protected long term;
    @XmlElement(required = true)
    protected DictionaryType priority;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar closeDate;
    @XmlElement(required = true)
    protected DictionaryType closeReason;
    @XmlElement(required = true)
    protected String transitNumber;
    @XmlElement(required = true)
    protected String transitCurrNumber;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar transitDate;
    @XmlElement(required = true)
    protected String info;
    protected Long srokDog;

    /**
     * Gets the value of the dogovorNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDogovorNumber() {
        return dogovorNumber;
    }

    /**
     * Sets the value of the dogovorNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDogovorNumber(String value) {
        this.dogovorNumber = value;
    }

    /**
     * Gets the value of the recordNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRecordNumber() {
        return recordNumber;
    }

    /**
     * Sets the value of the recordNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRecordNumber(String value) {
        this.recordNumber = value;
    }

    /**
     * Gets the value of the recordDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getRecordDate() {
        return recordDate;
    }

    /**
     * Sets the value of the recordDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setRecordDate(XMLGregorianCalendar value) {
        this.recordDate = value;
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
     * Gets the value of the codeKato property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodeKato() {
        return codeKato;
    }

    /**
     * Sets the value of the codeKato property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodeKato(String value) {
        this.codeKato = value;
    }

    /**
     * Gets the value of the district property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDistrict() {
        return district;
    }

    /**
     * Sets the value of the district property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDistrict(String value) {
        this.district = value;
    }

    /**
     * Gets the value of the moneyAmount property.
     * 
     */
    public double getMoneyAmount() {
        return moneyAmount;
    }

    /**
     * Sets the value of the moneyAmount property.
     * 
     */
    public void setMoneyAmount(double value) {
        this.moneyAmount = value;
    }

    /**
     * Gets the value of the term property.
     * 
     */
    public long getTerm() {
        return term;
    }

    /**
     * Sets the value of the term property.
     * 
     */
    public void setTerm(long value) {
        this.term = value;
    }

    /**
     * Gets the value of the priority property.
     * 
     * @return
     *     possible object is
     *     {@link DictionaryType }
     *     
     */
    public DictionaryType getPriority() {
        return priority;
    }

    /**
     * Sets the value of the priority property.
     * 
     * @param value
     *     allowed object is
     *     {@link DictionaryType }
     *     
     */
    public void setPriority(DictionaryType value) {
        this.priority = value;
    }

    /**
     * Gets the value of the closeDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCloseDate() {
        return closeDate;
    }

    /**
     * Sets the value of the closeDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCloseDate(XMLGregorianCalendar value) {
        this.closeDate = value;
    }

    /**
     * Gets the value of the closeReason property.
     * 
     * @return
     *     possible object is
     *     {@link DictionaryType }
     *     
     */
    public DictionaryType getCloseReason() {
        return closeReason;
    }

    /**
     * Sets the value of the closeReason property.
     * 
     * @param value
     *     allowed object is
     *     {@link DictionaryType }
     *     
     */
    public void setCloseReason(DictionaryType value) {
        this.closeReason = value;
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
     * Gets the value of the transitCurrNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransitCurrNumber() {
        return transitCurrNumber;
    }

    /**
     * Sets the value of the transitCurrNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransitCurrNumber(String value) {
        this.transitCurrNumber = value;
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

    /**
     * Gets the value of the info property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInfo() {
        return info;
    }

    /**
     * Sets the value of the info property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInfo(String value) {
        this.info = value;
    }

    /**
     * Gets the value of the srokDog property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getSrokDog() {
        return srokDog;
    }

    /**
     * Sets the value of the srokDog property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setSrokDog(Long value) {
        this.srokDog = value;
    }

}
