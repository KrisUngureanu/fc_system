//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.01.08 at 04:15:17 PM ALMT 
//


package kz.tamur.fc.gbdul.infobybin.v2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * ��� ����������� ����� ���������������  �� �������� ����������
 * 
 * <p>Java class for AddressStatType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AddressStatType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RKA" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="KATO" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="NameRu" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="NameKz" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AddressStatType", propOrder = {
    "rka",
    "kato",
    "nameRu",
    "nameKz"
})
public class AddressStatType {

    @XmlElement(name = "RKA")
    protected String rka;
    @XmlElement(name = "KATO")
    protected String kato;
    @XmlElement(name = "NameRu", required = true)
    protected String nameRu;
    @XmlElement(name = "NameKz", required = true)
    protected String nameKz;

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
     * Gets the value of the kato property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKATO() {
        return kato;
    }

    /**
     * Sets the value of the kato property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKATO(String value) {
        this.kato = value;
    }

    /**
     * Gets the value of the nameRu property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNameRu() {
        return nameRu;
    }

    /**
     * Sets the value of the nameRu property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNameRu(String value) {
        this.nameRu = value;
    }

    /**
     * Gets the value of the nameKz property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNameKz() {
        return nameKz;
    }

    /**
     * Sets the value of the nameKz property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNameKz(String value) {
        this.nameKz = value;
    }

}