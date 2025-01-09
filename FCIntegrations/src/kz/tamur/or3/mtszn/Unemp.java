
package kz.tamur.or3.mtszn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for unemp complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="unemp">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="iin" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="dep_kname" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="dep_rname" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="fio" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="occup_type_code" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="occup_type_rname" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="registrationDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="unemployee" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "unemp", propOrder = {
    "iin",
    "depKname",
    "depRname",
    "fio",
    "occupTypeCode",
    "occupTypeRname",
    "registrationDate",
    "unemployee"
})
public class Unemp {

    @XmlElement(required = true)
    protected String iin;
    @XmlElement(name = "dep_kname", required = true)
    protected String depKname;
    @XmlElement(name = "dep_rname", required = true)
    protected String depRname;
    @XmlElement(required = true)
    protected String fio;
    @XmlElement(name = "occup_type_code", required = true)
    protected String occupTypeCode;
    @XmlElement(name = "occup_type_rname", required = true)
    protected String occupTypeRname;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar registrationDate;
    @XmlElement(required = true)
    protected String unemployee;

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
     * Gets the value of the depKname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDepKname() {
        return depKname;
    }

    /**
     * Sets the value of the depKname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDepKname(String value) {
        this.depKname = value;
    }

    /**
     * Gets the value of the depRname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDepRname() {
        return depRname;
    }

    /**
     * Sets the value of the depRname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDepRname(String value) {
        this.depRname = value;
    }

    /**
     * Gets the value of the fio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFio() {
        return fio;
    }

    /**
     * Sets the value of the fio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFio(String value) {
        this.fio = value;
    }

    /**
     * Gets the value of the occupTypeCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOccupTypeCode() {
        return occupTypeCode;
    }

    /**
     * Sets the value of the occupTypeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOccupTypeCode(String value) {
        this.occupTypeCode = value;
    }

    /**
     * Gets the value of the occupTypeRname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOccupTypeRname() {
        return occupTypeRname;
    }

    /**
     * Sets the value of the occupTypeRname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOccupTypeRname(String value) {
        this.occupTypeRname = value;
    }

    /**
     * Gets the value of the registrationDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getRegistrationDate() {
        return registrationDate;
    }

    /**
     * Sets the value of the registrationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setRegistrationDate(XMLGregorianCalendar value) {
        this.registrationDate = value;
    }

    /**
     * Gets the value of the unemployee property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUnemployee() {
        return unemployee;
    }

    /**
     * Sets the value of the unemployee property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUnemployee(String value) {
        this.unemployee = value;
    }

}
