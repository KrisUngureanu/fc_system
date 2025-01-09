
package kz.tamur.or3.mtszn.mvd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for tvuInfoResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tvuInfoResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="categoryA" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="categoryB" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="categoryC" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="categoryD" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="categoryE" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="categoryF" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="dateOfBirth" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="dateOperate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="drivingDocDateEnd" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="firstName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="iin" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lastName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="middleName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="num" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="serial" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tvuInfoResponse", propOrder = {
    "categoryA",
    "categoryB",
    "categoryC",
    "categoryD",
    "categoryE",
    "categoryF",
    "dateOfBirth",
    "dateOperate",
    "drivingDocDateEnd",
    "firstName",
    "iin",
    "lastName",
    "middleName",
    "num",
    "serial"
})
public class TvuInfoResponse {

    protected boolean categoryA;
    protected boolean categoryB;
    protected boolean categoryC;
    protected boolean categoryD;
    protected boolean categoryE;
    protected boolean categoryF;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateOfBirth;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateOperate;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar drivingDocDateEnd;
    protected String firstName;
    protected String iin;
    protected String lastName;
    protected String middleName;
    protected String num;
    protected String serial;

    /**
     * Gets the value of the categoryA property.
     * 
     */
    public boolean isCategoryA() {
        return categoryA;
    }

    /**
     * Sets the value of the categoryA property.
     * 
     */
    public void setCategoryA(boolean value) {
        this.categoryA = value;
    }

    /**
     * Gets the value of the categoryB property.
     * 
     */
    public boolean isCategoryB() {
        return categoryB;
    }

    /**
     * Sets the value of the categoryB property.
     * 
     */
    public void setCategoryB(boolean value) {
        this.categoryB = value;
    }

    /**
     * Gets the value of the categoryC property.
     * 
     */
    public boolean isCategoryC() {
        return categoryC;
    }

    /**
     * Sets the value of the categoryC property.
     * 
     */
    public void setCategoryC(boolean value) {
        this.categoryC = value;
    }

    /**
     * Gets the value of the categoryD property.
     * 
     */
    public boolean isCategoryD() {
        return categoryD;
    }

    /**
     * Sets the value of the categoryD property.
     * 
     */
    public void setCategoryD(boolean value) {
        this.categoryD = value;
    }

    /**
     * Gets the value of the categoryE property.
     * 
     */
    public boolean isCategoryE() {
        return categoryE;
    }

    /**
     * Sets the value of the categoryE property.
     * 
     */
    public void setCategoryE(boolean value) {
        this.categoryE = value;
    }

    /**
     * Gets the value of the categoryF property.
     * 
     */
    public boolean isCategoryF() {
        return categoryF;
    }

    /**
     * Sets the value of the categoryF property.
     * 
     */
    public void setCategoryF(boolean value) {
        this.categoryF = value;
    }

    /**
     * Gets the value of the dateOfBirth property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * Sets the value of the dateOfBirth property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateOfBirth(XMLGregorianCalendar value) {
        this.dateOfBirth = value;
    }

    /**
     * Gets the value of the dateOperate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateOperate() {
        return dateOperate;
    }

    /**
     * Sets the value of the dateOperate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateOperate(XMLGregorianCalendar value) {
        this.dateOperate = value;
    }

    /**
     * Gets the value of the drivingDocDateEnd property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDrivingDocDateEnd() {
        return drivingDocDateEnd;
    }

    /**
     * Sets the value of the drivingDocDateEnd property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDrivingDocDateEnd(XMLGregorianCalendar value) {
        this.drivingDocDateEnd = value;
    }

    /**
     * Gets the value of the firstName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the value of the firstName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFirstName(String value) {
        this.firstName = value;
    }

    /**
     * Gets the value of the iin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIin() {
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
    public void setIin(String value) {
        this.iin = value;
    }

    /**
     * Gets the value of the lastName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the value of the lastName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastName(String value) {
        this.lastName = value;
    }

    /**
     * Gets the value of the middleName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMiddleName() {
        return middleName;
    }

    /**
     * Sets the value of the middleName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMiddleName(String value) {
        this.middleName = value;
    }

    /**
     * Gets the value of the num property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNum() {
        return num;
    }

    /**
     * Sets the value of the num property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNum(String value) {
        this.num = value;
    }

    /**
     * Gets the value of the serial property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSerial() {
        return serial;
    }

    /**
     * Sets the value of the serial property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSerial(String value) {
        this.serial = value;
    }

}
