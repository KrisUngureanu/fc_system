
package kz.tamur.or3.mtszn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for ownIncome complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ownIncome">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="datePeriod" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="incomeDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="incomeName" type="{http://services.sync.mtszn/}additionalName" minOccurs="0"/>
 *         &lt;element name="incomeValue" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ownIncome", propOrder = {
    "datePeriod",
    "incomeDate",
    "incomeName",
    "incomeValue"
})
public class OwnIncome {

    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar datePeriod;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar incomeDate;
    protected AdditionalName incomeName;
    protected double incomeValue;

    /**
     * Gets the value of the datePeriod property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDatePeriod() {
        return datePeriod;
    }

    /**
     * Sets the value of the datePeriod property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDatePeriod(XMLGregorianCalendar value) {
        this.datePeriod = value;
    }

    /**
     * Gets the value of the incomeDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getIncomeDate() {
        return incomeDate;
    }

    /**
     * Sets the value of the incomeDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setIncomeDate(XMLGregorianCalendar value) {
        this.incomeDate = value;
    }

    /**
     * Gets the value of the incomeName property.
     * 
     * @return
     *     possible object is
     *     {@link AdditionalName }
     *     
     */
    public AdditionalName getIncomeName() {
        return incomeName;
    }

    /**
     * Sets the value of the incomeName property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdditionalName }
     *     
     */
    public void setIncomeName(AdditionalName value) {
        this.incomeName = value;
    }

    /**
     * Gets the value of the incomeValue property.
     * 
     */
    public double getIncomeValue() {
        return incomeValue;
    }

    /**
     * Sets the value of the incomeValue property.
     * 
     */
    public void setIncomeValue(double value) {
        this.incomeValue = value;
    }

}
