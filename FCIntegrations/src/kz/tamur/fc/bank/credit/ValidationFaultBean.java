
package kz.tamur.fc.bank.credit;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ValidationFaultBean complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ValidationFaultBean">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="constraintViolations" type="{http://data.chdb.scb.kz}constraintViolation" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="dto" type="{http://data.chdb.scb.kz}batchProvidedDto" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ValidationFaultBean", propOrder = {
    "constraintViolations",
    "dto"
})
public class ValidationFaultBean {

    @XmlElement(nillable = true)
    protected List<ConstraintViolation> constraintViolations;
    protected BatchProvidedDto dto;

    /**
     * Gets the value of the constraintViolations property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the constraintViolations property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getConstraintViolations().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ConstraintViolation }
     * 
     * 
     */
    public List<ConstraintViolation> getConstraintViolations() {
        if (constraintViolations == null) {
            constraintViolations = new ArrayList<ConstraintViolation>();
        }
        return this.constraintViolations;
    }

    /**
     * Gets the value of the dto property.
     * 
     * @return
     *     possible object is
     *     {@link BatchProvidedDto }
     *     
     */
    public BatchProvidedDto getDto() {
        return dto;
    }

    /**
     * Sets the value of the dto property.
     * 
     * @param value
     *     allowed object is
     *     {@link BatchProvidedDto }
     *     
     */
    public void setDto(BatchProvidedDto value) {
        this.dto = value;
    }

}
