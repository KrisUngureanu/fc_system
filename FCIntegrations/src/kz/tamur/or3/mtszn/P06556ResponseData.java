
package kz.tamur.or3.mtszn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for p06556ResponseData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="p06556ResponseData">
 *   &lt;complexContent>
 *     &lt;extension base="{http://services.sync.mtszn/}baseResponseData">
 *       &lt;sequence>
 *         &lt;element name="decisionDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="decisionType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="depAddressKname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="depKName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="depRName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="depUserName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="depaddressRname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dirCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dirKName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dirNote" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dirRName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="empAddressKName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="empAddressRName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="empShortKName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="empShortRName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="is_ok" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="msg_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="personCardNum" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="personFIO" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="professionKName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="professionRName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="queue_id" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="refuseName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlType(name = "p06556ResponseData", propOrder = {
    "decisionDate",
    "decisionType",
    "depAddressKname",
    "depKName",
    "depRName",
    "depUserName",
    "depaddressRname",
    "dirCode",
    "dirKName",
    "dirNote",
    "dirRName",
    "empAddressKName",
    "empAddressRName",
    "empShortKName",
    "empShortRName",
    "isOk",
    "msgId",
    "personCardNum",
    "personFIO",
    "professionKName",
    "professionRName",
    "queueId",
    "refuseName",
    "serviceCode"
})
public class P06556ResponseData
    extends BaseResponseData
{

    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar decisionDate;
    protected String decisionType;
    protected String depAddressKname;
    protected String depKName;
    protected String depRName;
    protected String depUserName;
    protected String depaddressRname;
    protected String dirCode;
    protected String dirKName;
    protected String dirNote;
    protected String dirRName;
    protected String empAddressKName;
    protected String empAddressRName;
    protected String empShortKName;
    protected String empShortRName;
    @XmlElement(name = "is_ok")
    protected int isOk;
    @XmlElement(name = "msg_id")
    protected String msgId;
    protected String personCardNum;
    protected String personFIO;
    protected String professionKName;
    protected String professionRName;
    @XmlElement(name = "queue_id")
    protected int queueId;
    protected String refuseName;
    protected String serviceCode;

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
     * Gets the value of the decisionType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDecisionType() {
        return decisionType;
    }

    /**
     * Sets the value of the decisionType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDecisionType(String value) {
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
     * Gets the value of the dirCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDirCode() {
        return dirCode;
    }

    /**
     * Sets the value of the dirCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDirCode(String value) {
        this.dirCode = value;
    }

    /**
     * Gets the value of the dirKName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDirKName() {
        return dirKName;
    }

    /**
     * Sets the value of the dirKName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDirKName(String value) {
        this.dirKName = value;
    }

    /**
     * Gets the value of the dirNote property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDirNote() {
        return dirNote;
    }

    /**
     * Sets the value of the dirNote property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDirNote(String value) {
        this.dirNote = value;
    }

    /**
     * Gets the value of the dirRName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDirRName() {
        return dirRName;
    }

    /**
     * Sets the value of the dirRName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDirRName(String value) {
        this.dirRName = value;
    }

    /**
     * Gets the value of the empAddressKName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmpAddressKName() {
        return empAddressKName;
    }

    /**
     * Sets the value of the empAddressKName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmpAddressKName(String value) {
        this.empAddressKName = value;
    }

    /**
     * Gets the value of the empAddressRName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmpAddressRName() {
        return empAddressRName;
    }

    /**
     * Sets the value of the empAddressRName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmpAddressRName(String value) {
        this.empAddressRName = value;
    }

    /**
     * Gets the value of the empShortKName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmpShortKName() {
        return empShortKName;
    }

    /**
     * Sets the value of the empShortKName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmpShortKName(String value) {
        this.empShortKName = value;
    }

    /**
     * Gets the value of the empShortRName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmpShortRName() {
        return empShortRName;
    }

    /**
     * Sets the value of the empShortRName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmpShortRName(String value) {
        this.empShortRName = value;
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
     * Gets the value of the personCardNum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPersonCardNum() {
        return personCardNum;
    }

    /**
     * Sets the value of the personCardNum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPersonCardNum(String value) {
        this.personCardNum = value;
    }

    /**
     * Gets the value of the personFIO property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPersonFIO() {
        return personFIO;
    }

    /**
     * Sets the value of the personFIO property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPersonFIO(String value) {
        this.personFIO = value;
    }

    /**
     * Gets the value of the professionKName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProfessionKName() {
        return professionKName;
    }

    /**
     * Sets the value of the professionKName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProfessionKName(String value) {
        this.professionKName = value;
    }

    /**
     * Gets the value of the professionRName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProfessionRName() {
        return professionRName;
    }

    /**
     * Sets the value of the professionRName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProfessionRName(String value) {
        this.professionRName = value;
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
