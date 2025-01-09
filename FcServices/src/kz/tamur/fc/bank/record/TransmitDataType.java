
package kz.tamur.fc.bank.record;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Передача данных для перевода депозита из одного банка в другой
 * 
 * <p>Java class for TransmitDataType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TransmitDataType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="depositTo" type="{http://record.bank.fc.tamur.kz}RecordType" form="qualified"/>
 *         &lt;element name="docums" type="{http://record.bank.fc.tamur.kz}DictionaryType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransmitDataType", propOrder = {
    "depositTo",
    "docums"
})
public class TransmitDataType {

    @XmlElement(required = true)
    protected RecordType depositTo;
    protected List<DictionaryType> docums;

    /**
     * Gets the value of the depositTo property.
     * 
     * @return
     *     possible object is
     *     {@link RecordType }
     *     
     */
    public RecordType getDepositTo() {
        return depositTo;
    }

    /**
     * Sets the value of the depositTo property.
     * 
     * @param value
     *     allowed object is
     *     {@link RecordType }
     *     
     */
    public void setDepositTo(RecordType value) {
        this.depositTo = value;
    }

    /**
     * Gets the value of the docums property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the docums property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDocums().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DictionaryType }
     * 
     * 
     */
    public List<DictionaryType> getDocums() {
        if (docums == null) {
            docums = new ArrayList<DictionaryType>();
        }
        return this.docums;
    }

}
