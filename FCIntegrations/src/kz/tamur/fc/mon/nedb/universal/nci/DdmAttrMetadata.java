//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.01.08 at 04:34:25 PM ALMT 
//


package kz.tamur.fc.mon.nedb.universal.nci;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ddmAttrMetadata complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ddmAttrMetadata">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="link" type="{http://ws.ddm.nedb.kz/}ddmLink" minOccurs="0"/>
 *         &lt;element name="list" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="type" type="{http://ws.ddm.nedb.kz/}ddmMetadataType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ddmAttrMetadata", propOrder = {
    "link",
    "list",
    "name",
    "type"
})
public class DdmAttrMetadata {

    protected DdmLink link;
    protected boolean list;
    protected String name;
    @XmlSchemaType(name = "string")
    protected DdmMetadataType type;

    /**
     * Gets the value of the link property.
     * 
     * @return
     *     possible object is
     *     {@link DdmLink }
     *     
     */
    public DdmLink getLink() {
        return link;
    }

    /**
     * Sets the value of the link property.
     * 
     * @param value
     *     allowed object is
     *     {@link DdmLink }
     *     
     */
    public void setLink(DdmLink value) {
        this.link = value;
    }

    /**
     * Gets the value of the list property.
     * 
     */
    public boolean isList() {
        return list;
    }

    /**
     * Sets the value of the list property.
     * 
     */
    public void setList(boolean value) {
        this.list = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link DdmMetadataType }
     *     
     */
    public DdmMetadataType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link DdmMetadataType }
     *     
     */
    public void setType(DdmMetadataType value) {
        this.type = value;
    }

}
