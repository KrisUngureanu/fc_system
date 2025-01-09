
package kz.tamur.or3.mtszn.igcvp;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for p06501Response complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="p06501Response">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="fio" type="{http://igcvp_ss_sysinfo.interfaces.allowance.mtszn.mtszn.allowance.library/}FIO" minOccurs="0"/>
 *         &lt;element name="birthDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="iin" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="socialStatuses" type="{http://igcvp_ss_sysinfo.interfaces.allowance.mtszn.mtszn.allowance.library/}socialStatus" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="ssAbleCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "p06501Response", propOrder = {
    "fio",
    "birthDate",
    "iin",
    "socialStatuses",
    "ssAbleCode"
})
public class P06501Response {

    protected FIO fio;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar birthDate;
    protected String iin;
    @XmlElement(nillable = true)
    protected List<SocialStatus> socialStatuses;
    protected String ssAbleCode;

    /**
     * Gets the value of the fio property.
     * 
     * @return
     *     possible object is
     *     {@link FIO }
     *     
     */
    public FIO getFio() {
        return fio;
    }

    /**
     * Sets the value of the fio property.
     * 
     * @param value
     *     allowed object is
     *     {@link FIO }
     *     
     */
    public void setFio(FIO value) {
        this.fio = value;
    }

    /**
     * Gets the value of the birthDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getBirthDate() {
        return birthDate;
    }

    /**
     * Sets the value of the birthDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setBirthDate(XMLGregorianCalendar value) {
        this.birthDate = value;
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
     * Gets the value of the socialStatuses property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the socialStatuses property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSocialStatuses().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SocialStatus }
     * 
     * 
     */
    public List<SocialStatus> getSocialStatuses() {
        if (socialStatuses == null) {
            socialStatuses = new ArrayList<SocialStatus>();
        }
        return this.socialStatuses;
    }

    /**
     * Gets the value of the ssAbleCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSsAbleCode() {
        return ssAbleCode;
    }

    /**
     * Sets the value of the ssAbleCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSsAbleCode(String value) {
        this.ssAbleCode = value;
    }

}
