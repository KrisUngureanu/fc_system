
package kz.tamur.fc.bank.record;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * ������ � �� ���� ��� ������� �������� ����� ������ �����������
 * 
 * <p>Java class for summRequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="summRequestType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="head" type="{http://record.bank.fc.tamur.kz}SystemInfo" form="qualified"/>
 *         &lt;element name="regRecordNumber" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
 *         &lt;element name="IIN" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
 *         &lt;element name="recordNumber" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
 *         &lt;element name="recordDate" type="{http://www.w3.org/2001/XMLSchema}date" form="qualified"/>
 *         &lt;element name="sumDepozit" type="{http://www.w3.org/2001/XMLSchema}double" form="qualified"/>
 *         &lt;element name="sumInvest" type="{http://www.w3.org/2001/XMLSchema}double" form="qualified"/>
 *         &lt;element name="sumEducation" type="{http://www.w3.org/2001/XMLSchema}double" form="qualified"/>
 *         &lt;element name="sumBalans" type="{http://www.w3.org/2001/XMLSchema}double" form="qualified"/>
 *         &lt;element name="type" type="{http://record.bank.fc.tamur.kz}DictionaryType" form="qualified"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "summRequestType", propOrder = {
    "head",
    "regRecordNumber",
    "iin",
    "recordNumber",
    "recordDate",
    "sumDepozit",
    "sumInvest",
    "sumEducation",
    "sumBalans",
    "type"
})
public class SummRequestType {

    @XmlElement(required = true)
    protected SystemInfo head;
    @XmlElement(required = true)
    protected String regRecordNumber;
    @XmlElement(name = "IIN", required = true)
    protected String iin;
    @XmlElement(required = true)
    protected String recordNumber;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar recordDate;
    protected double sumDepozit;
    protected double sumInvest;
    protected double sumEducation;
    protected double sumBalans;
    @XmlElement(required = true)
    protected DictionaryType type;

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
     * Gets the value of the sumDepozit property.
     * 
     */
    public double getSumDepozit() {
        return sumDepozit;
    }

    /**
     * Sets the value of the sumDepozit property.
     * 
     */
    public void setSumDepozit(double value) {
        this.sumDepozit = value;
    }

    /**
     * Gets the value of the sumInvest property.
     * 
     */
    public double getSumInvest() {
        return sumInvest;
    }

    /**
     * Sets the value of the sumInvest property.
     * 
     */
    public void setSumInvest(double value) {
        this.sumInvest = value;
    }

    /**
     * Gets the value of the sumEducation property.
     * 
     */
    public double getSumEducation() {
        return sumEducation;
    }

    /**
     * Sets the value of the sumEducation property.
     * 
     */
    public void setSumEducation(double value) {
        this.sumEducation = value;
    }

    /**
     * Gets the value of the sumBalans property.
     * 
     */
    public double getSumBalans() {
        return sumBalans;
    }

    /**
     * Sets the value of the sumBalans property.
     * 
     */
    public void setSumBalans(double value) {
        this.sumBalans = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link DictionaryType }
     *     
     */
    public DictionaryType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link DictionaryType }
     *     
     */
    public void setType(DictionaryType value) {
        this.type = value;
    }

}
