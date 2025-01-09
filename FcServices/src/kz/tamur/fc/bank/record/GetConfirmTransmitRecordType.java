
package kz.tamur.fc.bank.record;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * Запрос к ИС ГОНС на подтверждение перевода депозита в пользу третьего лица
 * 
 * <p>Java class for getConfirmTransmitRecordType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getConfirmTransmitRecordType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="head" type="{http://record.bank.fc.tamur.kz}SystemInfo" form="qualified"/>
 *         &lt;element name="IIN" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
 *         &lt;element name="regRecordNumberTo" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
 *         &lt;element name="codeBankTo" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
 *         &lt;element name="schetNumberTo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" form="qualified"/>
 *         &lt;element name="sumDepozi" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0" form="qualified"/>
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
@XmlType(name = "getConfirmTransmitRecordType", propOrder = {
    "head",
    "iin",
    "regRecordNumberTo",
    "codeBankTo",
    "schetNumberTo",
    "sumDepozi",
    "processDate"
})
public class GetConfirmTransmitRecordType {

    @XmlElement(required = true)
    protected SystemInfo head;
    @XmlElement(name = "IIN", required = true)
    protected String iin;
    @XmlElement(required = true)
    protected String regRecordNumberTo;
    @XmlElement(required = true)
    protected String codeBankTo;
    protected String schetNumberTo;
    protected Double sumDepozi;
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
     * Gets the value of the sumDepozi property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getSumDepozi() {
        return sumDepozi;
    }

    /**
     * Sets the value of the sumDepozi property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setSumDepozi(Double value) {
        this.sumDepozi = value;
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
