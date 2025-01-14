//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2024.05.17 at 08:13:49 PM GMT+05:00 
//


package kz.tamur.fc.gbdfl.familyinfo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for divorceDTO complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="divorceDTO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="actDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="actNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="husbandBirthDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="husbandIIN" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="husbandLifeStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="husbandName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="husbandPatronymic" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="husbandSurName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="husbandSurnameBefore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="marriageActDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="marriageActNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="marriageActPlace" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="wifeBirthDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="wifeIIN" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="wifeLifeStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="wifeName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="wifePatronymic" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="wifeSurName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="wifeSurnameBefore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="zagsCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="zagsNameKZ" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="zagsNameRU" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "divorceDTO", namespace = "", propOrder = {
    "actDate",
    "actNumber",
    "husbandBirthDate",
    "husbandIIN",
    "husbandLifeStatus",
    "husbandName",
    "husbandPatronymic",
    "husbandSurName",
    "husbandSurnameBefore",
    "marriageActDate",
    "marriageActNumber",
    "marriageActPlace",
    "wifeBirthDate",
    "wifeIIN",
    "wifeLifeStatus",
    "wifeName",
    "wifePatronymic",
    "wifeSurName",
    "wifeSurnameBefore",
    "zagsCode",
    "zagsNameKZ",
    "zagsNameRU"
})
public class DivorceDTO {

    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar actDate;
    protected String actNumber;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar husbandBirthDate;
    protected String husbandIIN;
    protected String husbandLifeStatus;
    protected String husbandName;
    protected String husbandPatronymic;
    protected String husbandSurName;
    protected String husbandSurnameBefore;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar marriageActDate;
    protected String marriageActNumber;
    protected String marriageActPlace;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar wifeBirthDate;
    protected String wifeIIN;
    protected String wifeLifeStatus;
    protected String wifeName;
    protected String wifePatronymic;
    protected String wifeSurName;
    protected String wifeSurnameBefore;
    protected String zagsCode;
    protected String zagsNameKZ;
    protected String zagsNameRU;

    /**
     * Gets the value of the actDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getActDate() {
        return actDate;
    }

    /**
     * Sets the value of the actDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setActDate(XMLGregorianCalendar value) {
        this.actDate = value;
    }

    /**
     * Gets the value of the actNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getActNumber() {
        return actNumber;
    }

    /**
     * Sets the value of the actNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setActNumber(String value) {
        this.actNumber = value;
    }

    /**
     * Gets the value of the husbandBirthDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getHusbandBirthDate() {
        return husbandBirthDate;
    }

    /**
     * Sets the value of the husbandBirthDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setHusbandBirthDate(XMLGregorianCalendar value) {
        this.husbandBirthDate = value;
    }

    /**
     * Gets the value of the husbandIIN property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHusbandIIN() {
        return husbandIIN;
    }

    /**
     * Sets the value of the husbandIIN property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHusbandIIN(String value) {
        this.husbandIIN = value;
    }

    /**
     * Gets the value of the husbandLifeStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHusbandLifeStatus() {
        return husbandLifeStatus;
    }

    /**
     * Sets the value of the husbandLifeStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHusbandLifeStatus(String value) {
        this.husbandLifeStatus = value;
    }

    /**
     * Gets the value of the husbandName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHusbandName() {
        return husbandName;
    }

    /**
     * Sets the value of the husbandName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHusbandName(String value) {
        this.husbandName = value;
    }

    /**
     * Gets the value of the husbandPatronymic property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHusbandPatronymic() {
        return husbandPatronymic;
    }

    /**
     * Sets the value of the husbandPatronymic property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHusbandPatronymic(String value) {
        this.husbandPatronymic = value;
    }

    /**
     * Gets the value of the husbandSurName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHusbandSurName() {
        return husbandSurName;
    }

    /**
     * Sets the value of the husbandSurName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHusbandSurName(String value) {
        this.husbandSurName = value;
    }

    /**
     * Gets the value of the husbandSurnameBefore property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHusbandSurnameBefore() {
        return husbandSurnameBefore;
    }

    /**
     * Sets the value of the husbandSurnameBefore property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHusbandSurnameBefore(String value) {
        this.husbandSurnameBefore = value;
    }

    /**
     * Gets the value of the marriageActDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getMarriageActDate() {
        return marriageActDate;
    }

    /**
     * Sets the value of the marriageActDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setMarriageActDate(XMLGregorianCalendar value) {
        this.marriageActDate = value;
    }

    /**
     * Gets the value of the marriageActNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMarriageActNumber() {
        return marriageActNumber;
    }

    /**
     * Sets the value of the marriageActNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMarriageActNumber(String value) {
        this.marriageActNumber = value;
    }

    /**
     * Gets the value of the marriageActPlace property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMarriageActPlace() {
        return marriageActPlace;
    }

    /**
     * Sets the value of the marriageActPlace property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMarriageActPlace(String value) {
        this.marriageActPlace = value;
    }

    /**
     * Gets the value of the wifeBirthDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getWifeBirthDate() {
        return wifeBirthDate;
    }

    /**
     * Sets the value of the wifeBirthDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setWifeBirthDate(XMLGregorianCalendar value) {
        this.wifeBirthDate = value;
    }

    /**
     * Gets the value of the wifeIIN property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWifeIIN() {
        return wifeIIN;
    }

    /**
     * Sets the value of the wifeIIN property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWifeIIN(String value) {
        this.wifeIIN = value;
    }

    /**
     * Gets the value of the wifeLifeStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWifeLifeStatus() {
        return wifeLifeStatus;
    }

    /**
     * Sets the value of the wifeLifeStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWifeLifeStatus(String value) {
        this.wifeLifeStatus = value;
    }

    /**
     * Gets the value of the wifeName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWifeName() {
        return wifeName;
    }

    /**
     * Sets the value of the wifeName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWifeName(String value) {
        this.wifeName = value;
    }

    /**
     * Gets the value of the wifePatronymic property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWifePatronymic() {
        return wifePatronymic;
    }

    /**
     * Sets the value of the wifePatronymic property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWifePatronymic(String value) {
        this.wifePatronymic = value;
    }

    /**
     * Gets the value of the wifeSurName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWifeSurName() {
        return wifeSurName;
    }

    /**
     * Sets the value of the wifeSurName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWifeSurName(String value) {
        this.wifeSurName = value;
    }

    /**
     * Gets the value of the wifeSurnameBefore property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWifeSurnameBefore() {
        return wifeSurnameBefore;
    }

    /**
     * Sets the value of the wifeSurnameBefore property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWifeSurnameBefore(String value) {
        this.wifeSurnameBefore = value;
    }

    /**
     * Gets the value of the zagsCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getZagsCode() {
        return zagsCode;
    }

    /**
     * Sets the value of the zagsCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setZagsCode(String value) {
        this.zagsCode = value;
    }

    /**
     * Gets the value of the zagsNameKZ property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getZagsNameKZ() {
        return zagsNameKZ;
    }

    /**
     * Sets the value of the zagsNameKZ property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setZagsNameKZ(String value) {
        this.zagsNameKZ = value;
    }

    /**
     * Gets the value of the zagsNameRU property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getZagsNameRU() {
        return zagsNameRU;
    }

    /**
     * Sets the value of the zagsNameRU property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setZagsNameRU(String value) {
        this.zagsNameRU = value;
    }

}
