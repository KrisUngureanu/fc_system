
package kz.tamur.or3.mtszn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for additionalData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="additionalData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="alone" type="{http://services.sync.mtszn/}p06558ResponseData" minOccurs="0"/>
 *         &lt;element name="cbdiData" type="{http://services.sync.mtszn/}cbdiData" minOccurs="0"/>
 *         &lt;element name="exMilitary" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="exPrisoner" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="married" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="poor" type="{http://services.sync.mtszn/}p06552ResponseData" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "additionalData", propOrder = {
    "alone",
    "cbdiData",
    "exMilitary",
    "exPrisoner",
    "married",
    "poor"
})
public class AdditionalData {

    protected P06558ResponseData alone;
    protected CbdiData cbdiData;
    protected int exMilitary;
    protected int exPrisoner;
    protected int married;
    protected P06552ResponseData poor;

    /**
     * Gets the value of the alone property.
     * 
     * @return
     *     possible object is
     *     {@link P06558ResponseData }
     *     
     */
    public P06558ResponseData getAlone() {
        return alone;
    }

    /**
     * Sets the value of the alone property.
     * 
     * @param value
     *     allowed object is
     *     {@link P06558ResponseData }
     *     
     */
    public void setAlone(P06558ResponseData value) {
        this.alone = value;
    }

    /**
     * Gets the value of the cbdiData property.
     * 
     * @return
     *     possible object is
     *     {@link CbdiData }
     *     
     */
    public CbdiData getCbdiData() {
        return cbdiData;
    }

    /**
     * Sets the value of the cbdiData property.
     * 
     * @param value
     *     allowed object is
     *     {@link CbdiData }
     *     
     */
    public void setCbdiData(CbdiData value) {
        this.cbdiData = value;
    }

    /**
     * Gets the value of the exMilitary property.
     * 
     */
    public int getExMilitary() {
        return exMilitary;
    }

    /**
     * Sets the value of the exMilitary property.
     * 
     */
    public void setExMilitary(int value) {
        this.exMilitary = value;
    }

    /**
     * Gets the value of the exPrisoner property.
     * 
     */
    public int getExPrisoner() {
        return exPrisoner;
    }

    /**
     * Sets the value of the exPrisoner property.
     * 
     */
    public void setExPrisoner(int value) {
        this.exPrisoner = value;
    }

    /**
     * Gets the value of the married property.
     * 
     */
    public int getMarried() {
        return married;
    }

    /**
     * Sets the value of the married property.
     * 
     */
    public void setMarried(int value) {
        this.married = value;
    }

    /**
     * Gets the value of the poor property.
     * 
     * @return
     *     possible object is
     *     {@link P06552ResponseData }
     *     
     */
    public P06552ResponseData getPoor() {
        return poor;
    }

    /**
     * Sets the value of the poor property.
     * 
     * @param value
     *     allowed object is
     *     {@link P06552ResponseData }
     *     
     */
    public void setPoor(P06552ResponseData value) {
        this.poor = value;
    }

}
