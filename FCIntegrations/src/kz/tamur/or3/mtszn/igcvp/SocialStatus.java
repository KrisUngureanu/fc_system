
package kz.tamur.or3.mtszn.igcvp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for socialStatus complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="socialStatus">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ssId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ssNameRu" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ssNameKz" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ssIssueDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="ssExpireDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "socialStatus", propOrder = {
    "ssId",
    "ssNameRu",
    "ssNameKz",
    "ssIssueDate",
    "ssExpireDate"
})
public class SocialStatus {

    protected String ssId;
    protected String ssNameRu;
    protected String ssNameKz;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar ssIssueDate;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar ssExpireDate;

    /**
     * Gets the value of the ssId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSsId() {
        return ssId;
    }

    /**
     * Sets the value of the ssId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSsId(String value) {
        this.ssId = value;
    }

    /**
     * Gets the value of the ssNameRu property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSsNameRu() {
        return ssNameRu;
    }

    /**
     * Sets the value of the ssNameRu property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSsNameRu(String value) {
        this.ssNameRu = value;
    }

    /**
     * Gets the value of the ssNameKz property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSsNameKz() {
        return ssNameKz;
    }

    /**
     * Sets the value of the ssNameKz property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSsNameKz(String value) {
        this.ssNameKz = value;
    }

    /**
     * Gets the value of the ssIssueDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getSsIssueDate() {
        return ssIssueDate;
    }

    /**
     * Sets the value of the ssIssueDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setSsIssueDate(XMLGregorianCalendar value) {
        this.ssIssueDate = value;
    }

    /**
     * Gets the value of the ssExpireDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getSsExpireDate() {
        return ssExpireDate;
    }

    /**
     * Sets the value of the ssExpireDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setSsExpireDate(XMLGregorianCalendar value) {
        this.ssExpireDate = value;
    }

}
