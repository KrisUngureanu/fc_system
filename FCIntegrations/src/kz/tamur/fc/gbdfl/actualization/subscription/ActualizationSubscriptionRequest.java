//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.01.08 at 04:07:08 PM ALMT 
//


package kz.tamur.fc.gbdfl.actualization.subscription;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import kz.tamur.egov.signature.SignatureType;


/**
 * ������ ������ ������ �������
 * 
 * <p>Java class for ActualizationSubscriptionRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ActualizationSubscriptionRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="senderCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="iin" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="unsubscribe" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element ref="{http://www.w3.org/2000/09/xmldsig#}Signature"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ActualizationSubscriptionRequest", propOrder = {
    "senderCode",
    "iin",
    "unsubscribe",
    "signature"
})
public class ActualizationSubscriptionRequest {

    @XmlElement(required = true)
    protected String senderCode;
    @XmlElement(required = true)
    protected String iin;
    protected boolean unsubscribe;
    @XmlElement(name = "Signature", namespace = "http://www.w3.org/2000/09/xmldsig#", required = true)
    protected SignatureType signature;

    /**
     * Gets the value of the senderCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSenderCode() {
        return senderCode;
    }

    /**
     * Sets the value of the senderCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSenderCode(String value) {
        this.senderCode = value;
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
     * Gets the value of the unsubscribe property.
     * 
     */
    public boolean isUnsubscribe() {
        return unsubscribe;
    }

    /**
     * Sets the value of the unsubscribe property.
     * 
     */
    public void setUnsubscribe(boolean value) {
        this.unsubscribe = value;
    }

    /**
     * Gets the value of the signature property.
     * 
     * @return
     *     possible object is
     *     {@link SignatureType }
     *     
     */
    public SignatureType getSignature() {
        return signature;
    }

    /**
     * Sets the value of the signature property.
     * 
     * @param value
     *     allowed object is
     *     {@link SignatureType }
     *     
     */
    public void setSignature(SignatureType value) {
        this.signature = value;
    }

}
