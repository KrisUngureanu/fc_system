//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.11.07 at 12:27:01 PM ALMT 
//


package kz.tamur.shep.sed.info;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * ���������� � ���������� ���������
 * 
 * <p>Java class for DocumentSort complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DocumentSort">
 *   &lt;complexContent>
 *     &lt;extension base="{http://egov.bee.kz/eds/common/v1/}Sortable">
 *       &lt;sequence>
 *         &lt;element name="key" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="direction" type="{http://egov.bee.kz/eds/scanneddocuments/info/v1/}DocumentSortDirection" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DocumentSort", namespace = "http://egov.bee.kz/eds/scanneddocuments/info/v1/", propOrder = {
    "key",
    "direction"
})
public class DocumentSort
    extends Sortable
{

    @XmlElement(required = true)
    protected String key;
    protected DocumentSortDirection direction;

    /**
     * Gets the value of the key property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the value of the key property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKey(String value) {
        this.key = value;
    }

    /**
     * Gets the value of the direction property.
     * 
     * @return
     *     possible object is
     *     {@link DocumentSortDirection }
     *     
     */
    public DocumentSortDirection getDirection() {
        return direction;
    }

    /**
     * Sets the value of the direction property.
     * 
     * @param value
     *     allowed object is
     *     {@link DocumentSortDirection }
     *     
     */
    public void setDirection(DocumentSortDirection value) {
        this.direction = value;
    }

}