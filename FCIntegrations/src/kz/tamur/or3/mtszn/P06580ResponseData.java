
package kz.tamur.or3.mtszn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for p06580ResponseData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="p06580ResponseData">
 *   &lt;complexContent>
 *     &lt;extension base="{http://services.sync.mtszn/}baseResponseData">
 *       &lt;sequence>
 *         &lt;element name="activityKName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="activityRName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="birthDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="callDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="categoryKname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="categoryRname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="decisionDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="decisionNum" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="decisionType" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="depAddressKname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="depKName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="depRName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="depUserName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="depaddressRname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="helpTypeKName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="helpTypeRName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="is_ok" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="msg_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="paySum" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="personAddressKname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="personAddressRname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="personMiddleName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="personName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="personSurName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="phoneNum" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="queue_id" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="refuseName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="regionKName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="regionRName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="serviceCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "p06580ResponseData", propOrder = {
    "activityKName",
    "activityRName",
    "birthDate",
    "callDate",
    "categoryKname",
    "categoryRname",
    "decisionDate",
    "decisionNum",
    "decisionType",
    "depAddressKname",
    "depKName",
    "depRName",
    "depUserName",
    "depaddressRname",
    "helpTypeKName",
    "helpTypeRName",
    "isOk",
    "msgId",
    "paySum",
    "personAddressKname",
    "personAddressRname",
    "personMiddleName",
    "personName",
    "personSurName",
    "phoneNum",
    "queueId",
    "refuseName",
    "regionKName",
    "regionRName",
    "serviceCode"
})
public class P06580ResponseData
    extends BaseResponseData
{

    protected String activityKName;
    protected String activityRName;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar birthDate;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar callDate;
    protected String categoryKname;
    protected String categoryRname;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar decisionDate;
    protected String decisionNum;
    protected int decisionType;
    protected String depAddressKname;
    protected String depKName;
    protected String depRName;
    protected String depUserName;
    protected String depaddressRname;
    protected String helpTypeKName;
    protected String helpTypeRName;
    @XmlElement(name = "is_ok")
    protected int isOk;
    @XmlElement(name = "msg_id")
    protected String msgId;
    protected double paySum;
    protected String personAddressKname;
    protected String personAddressRname;
    protected String personMiddleName;
    protected String personName;
    protected String personSurName;
    protected String phoneNum;
    @XmlElement(name = "queue_id")
    protected int queueId;
    protected String refuseName;
    protected String regionKName;
    protected String regionRName;
    protected String serviceCode;

    /**
     * Gets the value of the activityKName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getActivityKName() {
        return activityKName;
    }

    /**
     * Sets the value of the activityKName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setActivityKName(String value) {
        this.activityKName = value;
    }

    /**
     * Gets the value of the activityRName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getActivityRName() {
        return activityRName;
    }

    /**
     * Sets the value of the activityRName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setActivityRName(String value) {
        this.activityRName = value;
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
     * Gets the value of the callDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCallDate() {
        return callDate;
    }

    /**
     * Sets the value of the callDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCallDate(XMLGregorianCalendar value) {
        this.callDate = value;
    }

    /**
     * Gets the value of the categoryKname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCategoryKname() {
        return categoryKname;
    }

    /**
     * Sets the value of the categoryKname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCategoryKname(String value) {
        this.categoryKname = value;
    }

    /**
     * Gets the value of the categoryRname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCategoryRname() {
        return categoryRname;
    }

    /**
     * Sets the value of the categoryRname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCategoryRname(String value) {
        this.categoryRname = value;
    }

    /**
     * Gets the value of the decisionDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDecisionDate() {
        return decisionDate;
    }

    /**
     * Sets the value of the decisionDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDecisionDate(XMLGregorianCalendar value) {
        this.decisionDate = value;
    }

    /**
     * Gets the value of the decisionNum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDecisionNum() {
        return decisionNum;
    }

    /**
     * Sets the value of the decisionNum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDecisionNum(String value) {
        this.decisionNum = value;
    }

    /**
     * Gets the value of the decisionType property.
     * 
     */
    public int getDecisionType() {
        return decisionType;
    }

    /**
     * Sets the value of the decisionType property.
     * 
     */
    public void setDecisionType(int value) {
        this.decisionType = value;
    }

    /**
     * Gets the value of the depAddressKname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDepAddressKname() {
        return depAddressKname;
    }

    /**
     * Sets the value of the depAddressKname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDepAddressKname(String value) {
        this.depAddressKname = value;
    }

    /**
     * Gets the value of the depKName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDepKName() {
        return depKName;
    }

    /**
     * Sets the value of the depKName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDepKName(String value) {
        this.depKName = value;
    }

    /**
     * Gets the value of the depRName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDepRName() {
        return depRName;
    }

    /**
     * Sets the value of the depRName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDepRName(String value) {
        this.depRName = value;
    }

    /**
     * Gets the value of the depUserName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDepUserName() {
        return depUserName;
    }

    /**
     * Sets the value of the depUserName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDepUserName(String value) {
        this.depUserName = value;
    }

    /**
     * Gets the value of the depaddressRname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDepaddressRname() {
        return depaddressRname;
    }

    /**
     * Sets the value of the depaddressRname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDepaddressRname(String value) {
        this.depaddressRname = value;
    }

    /**
     * Gets the value of the helpTypeKName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHelpTypeKName() {
        return helpTypeKName;
    }

    /**
     * Sets the value of the helpTypeKName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHelpTypeKName(String value) {
        this.helpTypeKName = value;
    }

    /**
     * Gets the value of the helpTypeRName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHelpTypeRName() {
        return helpTypeRName;
    }

    /**
     * Sets the value of the helpTypeRName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHelpTypeRName(String value) {
        this.helpTypeRName = value;
    }

    /**
     * Gets the value of the isOk property.
     * 
     */
    public int getIsOk() {
        return isOk;
    }

    /**
     * Sets the value of the isOk property.
     * 
     */
    public void setIsOk(int value) {
        this.isOk = value;
    }

    /**
     * Gets the value of the msgId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMsgId() {
        return msgId;
    }

    /**
     * Sets the value of the msgId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMsgId(String value) {
        this.msgId = value;
    }

    /**
     * Gets the value of the paySum property.
     * 
     */
    public double getPaySum() {
        return paySum;
    }

    /**
     * Sets the value of the paySum property.
     * 
     */
    public void setPaySum(double value) {
        this.paySum = value;
    }

    /**
     * Gets the value of the personAddressKname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPersonAddressKname() {
        return personAddressKname;
    }

    /**
     * Sets the value of the personAddressKname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPersonAddressKname(String value) {
        this.personAddressKname = value;
    }

    /**
     * Gets the value of the personAddressRname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPersonAddressRname() {
        return personAddressRname;
    }

    /**
     * Sets the value of the personAddressRname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPersonAddressRname(String value) {
        this.personAddressRname = value;
    }

    /**
     * Gets the value of the personMiddleName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPersonMiddleName() {
        return personMiddleName;
    }

    /**
     * Sets the value of the personMiddleName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPersonMiddleName(String value) {
        this.personMiddleName = value;
    }

    /**
     * Gets the value of the personName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPersonName() {
        return personName;
    }

    /**
     * Sets the value of the personName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPersonName(String value) {
        this.personName = value;
    }

    /**
     * Gets the value of the personSurName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPersonSurName() {
        return personSurName;
    }

    /**
     * Sets the value of the personSurName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPersonSurName(String value) {
        this.personSurName = value;
    }

    /**
     * Gets the value of the phoneNum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhoneNum() {
        return phoneNum;
    }

    /**
     * Sets the value of the phoneNum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhoneNum(String value) {
        this.phoneNum = value;
    }

    /**
     * Gets the value of the queueId property.
     * 
     */
    public int getQueueId() {
        return queueId;
    }

    /**
     * Sets the value of the queueId property.
     * 
     */
    public void setQueueId(int value) {
        this.queueId = value;
    }

    /**
     * Gets the value of the refuseName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRefuseName() {
        return refuseName;
    }

    /**
     * Sets the value of the refuseName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRefuseName(String value) {
        this.refuseName = value;
    }

    /**
     * Gets the value of the regionKName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegionKName() {
        return regionKName;
    }

    /**
     * Sets the value of the regionKName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegionKName(String value) {
        this.regionKName = value;
    }

    /**
     * Gets the value of the regionRName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegionRName() {
        return regionRName;
    }

    /**
     * Sets the value of the regionRName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegionRName(String value) {
        this.regionRName = value;
    }

    /**
     * Gets the value of the serviceCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceCode() {
        return serviceCode;
    }

    /**
     * Sets the value of the serviceCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceCode(String value) {
        this.serviceCode = value;
    }

}
