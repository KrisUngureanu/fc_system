
package kz.tamur.fc.bank.record;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * ������ � �� ���� � ��������� ���������� ����������� �����������
 * 
 * <p>Java class for educationOrgRequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="educationOrgRequestType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="head" type="{http://record.bank.fc.tamur.kz}SystemInfo" form="qualified"/>
 *         &lt;element name="IIN" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
 *         &lt;element name="regRecordNumber" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
 *         &lt;element name="BIN" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
 *         &lt;element name="IIKOO" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "educationOrgRequestType", propOrder = {
    "head",
    "iin",
    "regRecordNumber",
    "bin",
    "iikoo"
})
public class EducationOrgRequestType {

    @XmlElement(required = true)
    protected SystemInfo head;
    @XmlElement(name = "IIN", required = true)
    protected String iin;
    @XmlElement(required = true)
    protected String regRecordNumber;
    @XmlElement(name = "BIN", required = true)
    protected String bin;
    @XmlElement(name = "IIKOO", required = true)
    protected String iikoo;

    /**
     * Gets the value of the head property.
     * 
     * @return
     *     possible object is
     *     {@link SystemInfo }
     *     
     */
    public SystemInfo getHead() {
        return head;
    }

    /**
     * Sets the value of the head property.
     * 
     * @param value
     *     allowed object is
     *     {@link SystemInfo }
     *     
     */
    public void setHead(SystemInfo value) {
        this.head = value;
    }

    /**
     * Gets the value of the iin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIIN() {
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
    public void setIIN(String value) {
        this.iin = value;
    }

    /**
     * Gets the value of the regRecordNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegRecordNumber() {
        return regRecordNumber;
    }

    /**
     * Sets the value of the regRecordNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegRecordNumber(String value) {
        this.regRecordNumber = value;
    }

    /**
     * Gets the value of the bin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBIN() {
        return bin;
    }

    /**
     * Sets the value of the bin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBIN(String value) {
        this.bin = value;
    }

    /**
     * Gets the value of the iikoo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIIKOO() {
        return iikoo;
    }

    /**
     * Sets the value of the iikoo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIIKOO(String value) {
        this.iikoo = value;
    }

}
