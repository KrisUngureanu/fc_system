
package kz.tamur.fc.bank.record;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Передача данных для создания депозита
 * 
 * <p>Java class for DataType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DataType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="person" type="{http://record.bank.fc.tamur.kz}PersonType" form="qualified"/>
 *         &lt;element name="deposit" type="{http://record.bank.fc.tamur.kz}RecordType" form="qualified"/>
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
@XmlType(name = "DataType", propOrder = {
    "person",
    "deposit",
    "docums"
})
public class DataType {

    @XmlElement(required = true)
    protected PersonType person;
    @XmlElement(required = true)
    protected RecordType deposit;
    protected List<DictionaryType> docums;

    /**
     * Gets the value of the person property.
     * 
     * @return
     *     possible object is
     *     {@link PersonType }
     *     
     */
    public PersonType getPerson() {
        return person;
    }

    /**
     * Sets the value of the person property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonType }
     *     
     */
    public void setPerson(PersonType value) {
        this.person = value;
    }

    /**
     * Gets the value of the deposit property.
     * 
     * @return
     *     possible object is
     *     {@link RecordType }
     *     
     */
    public RecordType getDeposit() {
        return deposit;
    }

    /**
     * Sets the value of the deposit property.
     * 
     * @param value
     *     allowed object is
     *     {@link RecordType }
     *     
     */
    public void setDeposit(RecordType value) {
        this.deposit = value;
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
