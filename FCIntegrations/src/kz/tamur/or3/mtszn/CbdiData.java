
package kz.tamur.or3.mtszn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for cbdiData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cbdiData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="disabledEndDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="disabledPeriod" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="disabledStartDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="docNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cbdiData", propOrder = {
    "disabledEndDate",
    "disabledPeriod",
    "disabledStartDate",
    "docNumber"
})
public class CbdiData {

    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar disabledEndDate;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar disabledPeriod;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar disabledStartDate;
    protected String docNumber;

    /**
     * Gets the value of the disabledEndDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDisabledEndDate() {
        return disabledEndDate;
    }

    /**
     * Sets the value of the disabledEndDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDisabledEndDate(XMLGregorianCalendar value) {
        this.disabledEndDate = value;
    }

    /**
     * Gets the value of the disabledPeriod property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDisabledPeriod() {
        return disabledPeriod;
    }

    /**
     * Sets the value of the disabledPeriod property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDisabledPeriod(XMLGregorianCalendar value) {
        this.disabledPeriod = value;
    }

    /**
     * Gets the value of the disabledStartDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDisabledStartDate() {
        return disabledStartDate;
    }

    /**
     * Sets the value of the disabledStartDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDisabledStartDate(XMLGregorianCalendar value) {
        this.disabledStartDate = value;
    }

    /**
     * Gets the value of the docNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocNumber() {
        return docNumber;
    }

    /**
     * Sets the value of the docNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocNumber(String value) {
        this.docNumber = value;
    }

}
