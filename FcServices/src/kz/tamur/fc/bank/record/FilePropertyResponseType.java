
package kz.tamur.fc.bank.record;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * ����� � ����������� ������������ �����
 * 
 * <p>Java class for filePropertyResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="filePropertyResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="head" type="{http://record.bank.fc.tamur.kz}SystemInfo" form="qualified"/>
 *         &lt;element name="result" type="{http://record.bank.fc.tamur.kz}DictionaryType" form="qualified"/>
 *         &lt;element name="data" type="{http://record.bank.fc.tamur.kz}BaseDoc" form="qualified"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "filePropertyResponseType", propOrder = {
    "head",
    "result",
    "data"
})
public class FilePropertyResponseType {

    @XmlElement(required = true)
    protected SystemInfo head;
    @XmlElement(required = true)
    protected DictionaryType result;
    @XmlElement(required = true)
    protected BaseDoc data;

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
     * Gets the value of the result property.
     * 
     * @return
     *     possible object is
     *     {@link DictionaryType }
     *     
     */
    public DictionaryType getResult() {
        return result;
    }

    /**
     * Sets the value of the result property.
     * 
     * @param value
     *     allowed object is
     *     {@link DictionaryType }
     *     
     */
    public void setResult(DictionaryType value) {
        this.result = value;
    }

    /**
     * Gets the value of the data property.
     * 
     * @return
     *     possible object is
     *     {@link BaseDoc }
     *     
     */
    public BaseDoc getData() {
        return data;
    }

    /**
     * Sets the value of the data property.
     * 
     * @param value
     *     allowed object is
     *     {@link BaseDoc }
     *     
     */
    public void setData(BaseDoc value) {
        this.data = value;
    }

}
