
package kz.tamur.or3.mtszn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for p06556RequestData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="p06556RequestData">
 *   &lt;complexContent>
 *     &lt;extension base="{http://services.sync.mtszn/}baseRequestData">
 *       &lt;sequence>
 *         &lt;element name="dirType" type="{http://services.sync.mtszn/}additionalName" minOccurs="0"/>
 *         &lt;element name="emplName" type="{http://services.sync.mtszn/}additionalName" minOccurs="0"/>
 *         &lt;element name="fio" type="{http://services.sync.mtszn/}fio" minOccurs="0"/>
 *         &lt;element name="p_f1tn_id" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="profession" type="{http://services.sync.mtszn/}additionalName" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "p06556RequestData", propOrder = {
    "dirType",
    "emplName",
    "fio",
    "pf1TnId",
    "profession"
})
public class P06556RequestData
    extends BaseRequestData
{

    protected AdditionalName dirType;
    protected AdditionalName emplName;
    protected Fio fio;
    @XmlElement(name = "p_f1tn_id")
    protected int pf1TnId;
    protected AdditionalName profession;

    /**
     * Gets the value of the dirType property.
     * 
     * @return
     *     possible object is
     *     {@link AdditionalName }
     *     
     */
    public AdditionalName getDirType() {
        return dirType;
    }

    /**
     * Sets the value of the dirType property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdditionalName }
     *     
     */
    public void setDirType(AdditionalName value) {
        this.dirType = value;
    }

    /**
     * Gets the value of the emplName property.
     * 
     * @return
     *     possible object is
     *     {@link AdditionalName }
     *     
     */
    public AdditionalName getEmplName() {
        return emplName;
    }

    /**
     * Sets the value of the emplName property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdditionalName }
     *     
     */
    public void setEmplName(AdditionalName value) {
        this.emplName = value;
    }

    /**
     * Gets the value of the fio property.
     * 
     * @return
     *     possible object is
     *     {@link Fio }
     *     
     */
    public Fio getFio() {
        return fio;
    }

    /**
     * Sets the value of the fio property.
     * 
     * @param value
     *     allowed object is
     *     {@link Fio }
     *     
     */
    public void setFio(Fio value) {
        this.fio = value;
    }

    /**
     * Gets the value of the pf1TnId property.
     * 
     */
    public int getPF1TnId() {
        return pf1TnId;
    }

    /**
     * Sets the value of the pf1TnId property.
     * 
     */
    public void setPF1TnId(int value) {
        this.pf1TnId = value;
    }

    /**
     * Gets the value of the profession property.
     * 
     * @return
     *     possible object is
     *     {@link AdditionalName }
     *     
     */
    public AdditionalName getProfession() {
        return profession;
    }

    /**
     * Sets the value of the profession property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdditionalName }
     *     
     */
    public void setProfession(AdditionalName value) {
        this.profession = value;
    }

}
