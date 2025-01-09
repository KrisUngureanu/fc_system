
package kz.tamur.fc.bank.record;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * �������� ������ ��� ������������ ������� � ������ �����������
 * 
 * <p>Java class for TransferReestrType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TransferReestrType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="person" type="{http://record.bank.fc.tamur.kz}PersonType" form="qualified"/>
 *         &lt;element name="deposit" type="{http://record.bank.fc.tamur.kz}RecordType" form="qualified"/>
 *         &lt;element name="BINOO" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
 *         &lt;element name="foreign" type="{http://www.w3.org/2001/XMLSchema}boolean" form="qualified"/>
 *         &lt;element name="foreignName" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
 *         &lt;element name="error" type="{http://record.bank.fc.tamur.kz}DictionaryType" form="qualified"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransferReestrType", propOrder = {
    "person",
    "deposit",
    "binoo",
    "foreign",
    "foreignName",
    "error"
})
public class TransferReestrType {

    @XmlElement(required = true)
    protected PersonType person;
    @XmlElement(required = true)
    protected RecordType deposit;
    @XmlElement(name = "BINOO", required = true)
    protected String binoo;
    protected boolean foreign;
    @XmlElement(required = true)
    protected String foreignName;
    @XmlElement(required = true)
    protected DictionaryType error;

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
     * Gets the value of the binoo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBINOO() {
        return binoo;
    }

    /**
     * Sets the value of the binoo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBINOO(String value) {
        this.binoo = value;
    }

    /**
     * Gets the value of the foreign property.
     * 
     */
    public boolean isForeign() {
        return foreign;
    }

    /**
     * Sets the value of the foreign property.
     * 
     */
    public void setForeign(boolean value) {
        this.foreign = value;
    }

    /**
     * Gets the value of the foreignName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getForeignName() {
        return foreignName;
    }

    /**
     * Sets the value of the foreignName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setForeignName(String value) {
        this.foreignName = value;
    }

    /**
     * Gets the value of the error property.
     * 
     * @return
     *     possible object is
     *     {@link DictionaryType }
     *     
     */
    public DictionaryType getError() {
        return error;
    }

    /**
     * Sets the value of the error property.
     * 
     * @param value
     *     allowed object is
     *     {@link DictionaryType }
     *     
     */
    public void setError(DictionaryType value) {
        this.error = value;
    }

}
