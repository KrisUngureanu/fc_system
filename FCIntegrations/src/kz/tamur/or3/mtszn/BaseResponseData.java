
package kz.tamur.or3.mtszn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for baseResponseData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="baseResponseData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="iin" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "baseResponseData", propOrder = {
    "iin"
})
@XmlSeeAlso({
    P06557ResponseData.class,
    P06555ResponseData.class,
    P06551ResponseData.class,
    P06581ResponseData.class,
    P06580ResponseData.class,
    P06556ResponseData.class,
    P06590ResponseData.class,
    P06560ResponseData.class,
    P06559ResponseData.class,
    P06561ResponseData.class,
    P06554ResponseData.class,
    P06562ResponseData.class,
    P06553ResponseData.class,
    P06552ResponseData.class,
    P06558ResponseData.class
})
public abstract class BaseResponseData {

    protected String iin;

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

}