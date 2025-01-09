
package kz.tamur.fc.bank.record;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * ������  ���  �������������� ���� ���������� ������ �����������
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
 *         &lt;element name="head" type="{http://record.bank.fc.tamur.kz}SystemInfo" form="qualified"/>
 *         &lt;element name="count" type="{http://www.w3.org/2001/XMLSchema}long" form="qualified"/>
 *         &lt;element name="moneyAmount" type="{http://www.w3.org/2001/XMLSchema}double" form="qualified"/>
 *         &lt;element name="date" type="{http://www.w3.org/2001/XMLSchema}date" form="qualified"/>
 *         &lt;element name="flag" type="{http://www.w3.org/2001/XMLSchema}boolean" form="qualified"/>
 *         &lt;element name="reestr" type="{http://record.bank.fc.tamur.kz}ReestrType" maxOccurs="unbounded" minOccurs="0"/>
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
    "head",
    "count",
    "moneyAmount",
    "date",
    "flag",
    "reestr"
})
public class SummReestrRequestType {

    @XmlElement(required = true)
    protected SystemInfo head;
    protected long count;
    protected double moneyAmount;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar date;
    protected boolean flag;
    protected List<ReestrType> reestr;

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
