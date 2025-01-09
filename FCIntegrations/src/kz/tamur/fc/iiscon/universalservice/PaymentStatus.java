//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2024.12.27 at 06:54:50 PM GMT+05:00 
//


package kz.tamur.fc.iiscon.universalservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Информация о статусе платежа за услугу
 * 
 * <p>Java class for PaymentStatus complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PaymentStatus">
 *   &lt;complexContent>
 *     &lt;extension base="{http://schemas.letograf.kz/iiscon/bus/v1}AbstractStatus">
 *       &lt;sequence>
 *         &lt;element name="paymentState">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="WAIT_PAYMENT"/>
 *               &lt;enumeration value="PAYMENT_DONE"/>
 *               &lt;enumeration value="PAYMENT_TIMEOUT"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="memo" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PaymentStatus", propOrder = {
    "paymentState",
    "memo"
})
public class PaymentStatus
    extends AbstractStatus
{

    @XmlElement(required = true)
    protected String paymentState;
    protected Object memo;

    /**
     * Gets the value of the paymentState property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaymentState() {
        return paymentState;
    }

    /**
     * Sets the value of the paymentState property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaymentState(String value) {
        this.paymentState = value;
    }

    /**
     * Gets the value of the memo property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getMemo() {
        return memo;
    }

    /**
     * Sets the value of the memo property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setMemo(Object value) {
        this.memo = value;
    }

}