//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2024.06.05 at 05:24:35 PM GMT+05:00 
//


package kz.tamur.fc.keleshek;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * Ответ на запрос  для  получения справки о сумме премии государства
 * 
 * <p>Java class for summResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="summResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="result" type="{http://keleshek.fc.tamur.kz}DictionaryType" form="qualified"/>
 *         &lt;element name="IIN" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
 *         &lt;element name="recordNumber" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
 *         &lt;element name="recordDate" type="{http://www.w3.org/2001/XMLSchema}date" form="qualified"/>
 *         &lt;element name="moneyAmount" type="{http://www.w3.org/2001/XMLSchema}double" form="qualified"/>
 *         &lt;element name="info" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" form="qualified"/>
 *         &lt;element name="dateFile" type="{http://keleshek.fc.tamur.kz}BaseDoc" minOccurs="0" form="qualified"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "summResponseType", propOrder = {
    "result",
    "iin",
    "recordNumber",
    "recordDate",
    "moneyAmount",
    "info",
    "dateFile"
})
public class SummResponseType {

    @XmlElement(required = true)
    protected DictionaryType result;
    @XmlElement(name = "IIN", required = true)
    protected String iin;
    @XmlElement(required = true)
    protected String recordNumber;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar recordDate;
    protected double moneyAmount;
    protected String info;
    protected BaseDoc dateFile;

    /**
     * Gets the value of the result property.
     * 
     * @return
     *     possible object is
     *     {@link DictionaryType }
     *     
     */
    public DictionaryType getResult() {
        return result;
    }

    /**
     * Sets the value of the result property.
     * 
     * @param value
     *     allowed object is
     *     {@link DictionaryType }
     *     
     */
    public void setResult(DictionaryType value) {
        this.result = value;
    }

    /**
     * Gets the value of the iin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIIN() {
        return iin;
    }

    /**
     * Sets the value of the iin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIIN(String value) {
        this.iin = value;
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
     * Gets the value of the dateFile property.
     * 
     * @return
     *     possible object is
     *     {@link BaseDoc }
     *     
     */
    public BaseDoc getDateFile() {
        return dateFile;
    }

    /**
     * Sets the value of the dateFile property.
     * 
     * @param value
     *     allowed object is
     *     {@link BaseDoc }
     *     
     */
    public void setDateFile(BaseDoc value) {
        this.dateFile = value;
    }

}
