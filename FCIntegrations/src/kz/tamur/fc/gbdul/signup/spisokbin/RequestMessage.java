//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.01.08 at 04:23:59 PM ALMT 
//


package kz.tamur.fc.gbdul.signup.spisokbin;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import kz.tamur.egov.signature.SignatureType;


/**
 * ������ �������
 * 
 * <p>Java class for RequestMessage complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RequestMessage">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RequestorBIN" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="BINList" type="{http://newshep.gbdulsignupspisokbin.gbdul.tamur.kz}BINListType" maxOccurs="unbounded"/>
 *         &lt;element ref="{http://www.w3.org/2000/09/xmldsig#}Signature" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RequestMessage", propOrder = {
    "requestorBIN",
    "binList",
    "signature"
})
public class RequestMessage {

    @XmlElement(name = "RequestorBIN", required = true)
    protected String requestorBIN;
    @XmlElement(name = "BINList", required = true)
    protected List<BINListType> binList;
    @XmlElement(name = "Signature", namespace = "http://www.w3.org/2000/09/xmldsig#")
    protected SignatureType signature;

    /**
     * Gets the value of the requestorBIN property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestorBIN() {
        return requestorBIN;
    }

    /**
     * Sets the value of the requestorBIN property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestorBIN(String value) {
        this.requestorBIN = value;
    }

    /**
     * Gets the value of the binList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the binList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBINList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BINListType }
     * 
     * 
     */
    public List<BINListType> getBINList() {
        if (binList == null) {
            binList = new ArrayList<BINListType>();
        }
        return this.binList;
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
