
package kz.tamur.or3.mtszn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for p06551ResponseData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="p06551ResponseData">
 *   &lt;complexContent>
 *     &lt;extension base="{http://services.sync.mtszn/}baseResponseData">
 *       &lt;sequence>
 *         &lt;element name="dep_kname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dep_rname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="occup_type_code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="occup_type_kname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="occup_type_rname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="registrationDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="unemployee" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "p06551ResponseData", propOrder = {
    "depKname",
    "depRname",
    "fio",
    "occupTypeCode",
    "occupTypeKname",
    "occupTypeRname",
    "registrationDate",
    "unemployee"
})
public class P06551ResponseData
    extends BaseResponseData
{

    @XmlElement(name = "dep_kname")
    protected String depKname;
    @XmlElement(name = "dep_rname")
    protected String depRname;
    protected String fio;
    @XmlElement(name = "occup_type_code")
    protected String occupTypeCode;
    @XmlElement(name = "occup_type_kname")
    protected String occupTypeKname;
    @XmlElement(name = "occup_type_rname")
    protected String occupTypeRname;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar registrationDate;
    protected boolean unemployee;

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
     * Gets the value of the occupTypeKname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOccupTypeKname() {
        return occupTypeKname;
    }

    /**
     * Sets the value of the occupTypeKname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOccupTypeKname(String value) {
        this.occupTypeKname = value;
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
     */
    public boolean isUnemployee() {
        return unemployee;
    }

    /**
     * Sets the value of the unemployee property.
     * 
     */
    public void setUnemployee(boolean value) {
        this.unemployee = value;
    }

}
