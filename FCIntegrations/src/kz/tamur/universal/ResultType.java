//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.10.10 at 07:45:01 PM ALMT 
//


package kz.tamur.universal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * ���, ����������� �������� � ���������� ��������� �������
 * 
 * <p>Java class for ResultType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ResultType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ResultCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ResultMessageRu" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ResultMessageKz" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ResultMessageEn" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResultType", propOrder = {
    "resultCode",
    "resultMessageRu",
    "resultMessageKz",
    "resultMessageEn"
})
public class ResultType {

    @XmlElement(name = "ResultCode", required = true)
    protected String resultCode;
    @XmlElement(name = "ResultMessageRu", required = true)
    protected String resultMessageRu;
    @XmlElement(name = "ResultMessageKz")
    protected String resultMessageKz;
    @XmlElement(name = "ResultMessageEn")
    protected String resultMessageEn;

    /**
     * Gets the value of the resultCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResultCode() {
        return resultCode;
    }

    /**
     * Sets the value of the resultCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResultCode(String value) {
        this.resultCode = value;
    }

    /**
     * Gets the value of the resultMessageRu property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResultMessageRu() {
        return resultMessageRu;
    }

    /**
     * Sets the value of the resultMessageRu property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResultMessageRu(String value) {
        this.resultMessageRu = value;
    }

    /**
     * Gets the value of the resultMessageKz property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResultMessageKz() {
        return resultMessageKz;
    }

    /**
     * Sets the value of the resultMessageKz property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResultMessageKz(String value) {
        this.resultMessageKz = value;
    }

    /**
     * Gets the value of the resultMessageEn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResultMessageEn() {
        return resultMessageEn;
    }

    /**
     * Sets the value of the resultMessageEn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResultMessageEn(String value) {
        this.resultMessageEn = value;
    }

}
