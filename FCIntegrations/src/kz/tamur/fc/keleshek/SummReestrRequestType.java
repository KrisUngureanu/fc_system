//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2024.06.05 at 05:24:35 PM GMT+05:00 
//


package kz.tamur.fc.keleshek;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * Запрос  для  предоставление сумм начисления премии государства
 * 
 * <p>Java class for summReestrRequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="summReestrRequestType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="count" type="{http://www.w3.org/2001/XMLSchema}long" form="qualified"/>
 *         &lt;element name="moneyAmount" type="{http://www.w3.org/2001/XMLSchema}double" form="qualified"/>
 *         &lt;element name="date" type="{http://www.w3.org/2001/XMLSchema}date" form="qualified"/>
 *         &lt;element name="flag" type="{http://www.w3.org/2001/XMLSchema}boolean" form="qualified"/>
 *         &lt;element name="reestr" type="{http://keleshek.fc.tamur.kz}ReestrType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "summReestrRequestType", propOrder = {
    "count",
    "moneyAmount",
    "date",
    "flag",
    "reestr"
})
public class SummReestrRequestType {

    protected long count;
    protected double moneyAmount;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar date;
    protected boolean flag;
    protected List<ReestrType> reestr;

    /**
     * Gets the value of the count property.
     * 
     */
    public long getCount() {
        return count;
    }

    /**
     * Sets the value of the count property.
     * 
     */
    public void setCount(long value) {
        this.count = value;
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
     * Gets the value of the date property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDate() {
        return date;
    }

    /**
     * Sets the value of the date property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDate(XMLGregorianCalendar value) {
        this.date = value;
    }

    /**
     * Gets the value of the flag property.
     * 
     */
    public boolean isFlag() {
        return flag;
    }

    /**
     * Sets the value of the flag property.
     * 
     */
    public void setFlag(boolean value) {
        this.flag = value;
    }

    /**
     * Gets the value of the reestr property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the reestr property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReestr().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ReestrType }
     * 
     * 
     */
    public List<ReestrType> getReestr() {
        if (reestr == null) {
            reestr = new ArrayList<ReestrType>();
        }
        return this.reestr;
    }

}