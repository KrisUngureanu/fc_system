
package kz.tamur.or3.mtszn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for socStatus complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="socStatus">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="socStatus" type="{http://services.sync.mtszn/}additionalName" minOccurs="0"/>
 *         &lt;element name="socStatusDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "socStatus", propOrder = {
    "socStatus",
    "socStatusDate"
})
public class SocStatus {

    protected AdditionalName socStatus;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar socStatusDate;

    /**
     * Gets the value of the socStatus property.
     * 
     * @return
     *     possible object is
     *     {@link AdditionalName }
     *     
     */
    public AdditionalName getSocStatus() {
        return socStatus;
    }

    /**
     * Sets the value of the socStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdditionalName }
     *     
     */
    public void setSocStatus(AdditionalName value) {
        this.socStatus = value;
    }

    /**
     * Gets the value of the socStatusDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getSocStatusDate() {
        return socStatusDate;
    }

    /**
     * Sets the value of the socStatusDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setSocStatusDate(XMLGregorianCalendar value) {
        this.socStatusDate = value;
    }

}
