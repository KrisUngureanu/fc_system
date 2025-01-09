//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.01.08 at 04:20:52 PM ALMT 
//


package kz.tamur.fc.gbdul.signup.actualdata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * ���, ����������� ������ �� ������
 * 
 * <p>Java class for AddressType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AddressType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PostCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="RKA" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ATE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="KatoCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="GeonimCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Building" type="{http://newshep.gbdulsignupactualdata.gbdul.tamur.kz}BuildingType" minOccurs="0"/>
 *         &lt;element name="Room" type="{http://newshep.gbdulsignupactualdata.gbdul.tamur.kz}RoomType" minOccurs="0"/>
 *         &lt;element name="AddressRU" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="AddressKZ" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Contacts" type="{http://newshep.gbdulsignupactualdata.gbdul.tamur.kz}ContactType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AddressType", propOrder = {
    "postCode",
    "rka",
    "ate",
    "katoCode",
    "geonimCode",
    "building",
    "room",
    "addressRU",
    "addressKZ",
    "contacts"
})
public class AddressType {

    @XmlElement(name = "PostCode")
    protected String postCode;
    @XmlElement(name = "RKA")
    protected String rka;
    @XmlElement(name = "ATE")
    protected String ate;
    @XmlElement(name = "KatoCode")
    protected String katoCode;
    @XmlElement(name = "GeonimCode")
    protected String geonimCode;
    @XmlElement(name = "Building")
    protected BuildingType building;
    @XmlElement(name = "Room")
    protected RoomType room;
    @XmlElement(name = "AddressRU", required = true)
    protected String addressRU;
    @XmlElement(name = "AddressKZ", required = true)
    protected String addressKZ;
    @XmlElement(name = "Contacts")
    protected ContactType contacts;

    /**
     * Gets the value of the postCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPostCode() {
        return postCode;
    }

    /**
     * Sets the value of the postCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPostCode(String value) {
        this.postCode = value;
    }

    /**
     * Gets the value of the rka property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRKA() {
        return rka;
    }

    /**
     * Sets the value of the rka property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRKA(String value) {
        this.rka = value;
    }

    /**
     * Gets the value of the ate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getATE() {
        return ate;
    }

    /**
     * Sets the value of the ate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setATE(String value) {
        this.ate = value;
    }

    /**
     * Gets the value of the katoCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKatoCode() {
        return katoCode;
    }

    /**
     * Sets the value of the katoCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKatoCode(String value) {
        this.katoCode = value;
    }

    /**
     * Gets the value of the geonimCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGeonimCode() {
        return geonimCode;
    }

    /**
     * Sets the value of the geonimCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGeonimCode(String value) {
        this.geonimCode = value;
    }

    /**
     * Gets the value of the building property.
     * 
     * @return
     *     possible object is
     *     {@link BuildingType }
     *     
     */
    public BuildingType getBuilding() {
        return building;
    }

    /**
     * Sets the value of the building property.
     * 
     * @param value
     *     allowed object is
     *     {@link BuildingType }
     *     
     */
    public void setBuilding(BuildingType value) {
        this.building = value;
    }

    /**
     * Gets the value of the room property.
     * 
     * @return
     *     possible object is
     *     {@link RoomType }
     *     
     */
    public RoomType getRoom() {
        return room;
    }

    /**
     * Sets the value of the room property.
     * 
     * @param value
     *     allowed object is
     *     {@link RoomType }
     *     
     */
    public void setRoom(RoomType value) {
        this.room = value;
    }

    /**
     * Gets the value of the addressRU property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddressRU() {
        return addressRU;
    }

    /**
     * Sets the value of the addressRU property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddressRU(String value) {
        this.addressRU = value;
    }

    /**
     * Gets the value of the addressKZ property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddressKZ() {
        return addressKZ;
    }

    /**
     * Sets the value of the addressKZ property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddressKZ(String value) {
        this.addressKZ = value;
    }

    /**
     * Gets the value of the contacts property.
     * 
     * @return
     *     possible object is
     *     {@link ContactType }
     *     
     */
    public ContactType getContacts() {
        return contacts;
    }

    /**
     * Sets the value of the contacts property.
     * 
     * @param value
     *     allowed object is
     *     {@link ContactType }
     *     
     */
    public void setContacts(ContactType value) {
        this.contacts = value;
    }

}
