
package kz.tamur.or3.mtszn.bas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for ssifPartDocRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ssifPartDocRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="registerdate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="iin" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="subservicecode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ssifPartDocRequest", propOrder = {
    "registerdate",
    "iin",
    "subservicecode"
})
public class SsifPartDocRequest {

    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar registerdate;
    @XmlElement(required = true)
    protected String iin;
    @XmlElement(required = true)
    protected String subservicecode;

    /**
     * Gets the value of the registerdate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getRegisterdate() {
        return registerdate;
    }

    /**
     * Sets the value of the registerdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setRegisterdate(XMLGregorianCalendar value) {
        this.registerdate = value;
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
     * Gets the value of the subservicecode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubservicecode() {
        return subservicecode;
    }

    /**
     * Sets the value of the subservicecode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubservicecode(String value) {
        this.subservicecode = value;
    }

}
