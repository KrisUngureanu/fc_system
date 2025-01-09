//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.01.10 at 12:17:42 PM ALMT 
//


package kz.tamur.fc.gbdfl.universal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import kz.tamur.egov.signature.SignatureType;


/**
 * ��������� ������� �� ����� ����������� ����. ���� messageId, messageDate, senderCode ����������� ��� ����������. �������������� 3 ���� �������: ����� �� ���, �� ��� � ���� ��������, �� ������ ��������� ��������������� �������� (���). ��� ������ �� ��� ������� ��������� ���� iin. ��� ������ �� ��� � ���� �������� ������� ��������� ���� surname, firstname, secondname, birthDate. ��� ����� �������� ������ ������������� �������� ���� birthDate � ���� �� surname ��� firstname. ��� ����, ���� �� �������, ��������, ���� firstname, �� ����� �������������� ����� �������� � ��������� � �������� ������ ��������� � ��� �����, � �� ���� ���������� ��� � ����� ��������, �.�., ����� �������� ������ �������������� ������ ��� ������ ���������. ��� ������ �� ������ ��� ������� ��������� ���� documentNumber. ��� ������ ����������� ���� ������� ������� ��������� ������ ���� ��������� ������, ����������� ������ � ����� ����, ��������� �� �������� �������� �������.
 * 
 * <p>Java class for Request complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Request">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="messageId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="messageDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="senderCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="iin" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="surname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="patronymic" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="birthDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="documentNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlType(name = "Request", namespace = "http://message.persistence.interactive.nat", propOrder = {
    "messageId",
    "messageDate",
    "senderCode",
    "iin",
    "surname",
    "name",
    "patronymic",
    "birthDate",
    "documentNumber",
    "signature"
})
public class Request {

    @XmlElement(required = true)
    protected String messageId;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar messageDate;
    @XmlElement(required = true)
    protected String senderCode;
    protected String iin;
    protected String surname;
    protected String name;
    protected String patronymic;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar birthDate;
    protected String documentNumber;
    @XmlElement(name = "Signature", namespace = "http://www.w3.org/2000/09/xmldsig#")
    protected SignatureType signature;

    /**
     * Gets the value of the messageId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * Sets the value of the messageId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessageId(String value) {
        this.messageId = value;
    }

    /**
     * Gets the value of the messageDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getMessageDate() {
        return messageDate;
    }

    /**
     * Sets the value of the messageDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setMessageDate(XMLGregorianCalendar value) {
        this.messageDate = value;
    }

    /**
     * Gets the value of the senderCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSenderCode() {
        return senderCode;
    }

    /**
     * Sets the value of the senderCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSenderCode(String value) {
        this.senderCode = value;
    }

    /**
     * Gets the value of the iin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIin() {
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
    public void setIin(String value) {
        this.iin = value;
    }

    /**
     * Gets the value of the surname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Sets the value of the surname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSurname(String value) {
        this.surname = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the patronymic property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPatronymic() {
        return patronymic;
    }

    /**
     * Sets the value of the patronymic property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPatronymic(String value) {
        this.patronymic = value;
    }

    /**
     * Gets the value of the birthDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getBirthDate() {
        return birthDate;
    }

    /**
     * Sets the value of the birthDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setBirthDate(XMLGregorianCalendar value) {
        this.birthDate = value;
    }

    /**
     * Gets the value of the documentNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the value of the documentNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocumentNumber(String value) {
        this.documentNumber = value;
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
