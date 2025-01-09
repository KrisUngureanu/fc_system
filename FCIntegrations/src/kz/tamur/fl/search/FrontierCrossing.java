
package kz.tamur.fl.search;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for FrontierCrossing complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FrontierCrossing">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="operationType" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="operationDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="kppCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="kppName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FrontierCrossing", namespace = "http://document.persistence.interactive.nat", propOrder = {
    "operationType",
    "operationDate",
    "kppCode",
    "kppName"
})
public class FrontierCrossing {

    protected Long operationType;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar operationDate;
    protected String kppCode;
    protected String kppName;

    /**
     * Gets the value of the operationType property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getOperationType() {
        return operationType;
    }

    /**
     * Sets the value of the operationType property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setOperationType(Long value) {
        this.operationType = value;
    }

    /**
     * Gets the value of the operationDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getOperationDate() {
        return operationDate;
    }

    /**
     * Sets the value of the operationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setOperationDate(XMLGregorianCalendar value) {
        this.operationDate = value;
    }

    /**
     * Gets the value of the kppCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKppCode() {
        return kppCode;
    }

    /**
     * Sets the value of the kppCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKppCode(String value) {
        this.kppCode = value;
    }

    /**
     * Gets the value of the kppName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKppName() {
        return kppName;
    }

    /**
     * Sets the value of the kppName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKppName(String value) {
        this.kppName = value;
    }

}
