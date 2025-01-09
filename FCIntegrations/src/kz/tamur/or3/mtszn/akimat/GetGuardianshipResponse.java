
package kz.tamur.or3.mtszn.akimat;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for GetGuardianshipResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetGuardianshipResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="result_code" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="citizenship" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="req_iin" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="req_surname" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="req_name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="req_patronymic" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="req_birthday" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="req_documentnumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="req_issuer" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="req_issuedate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="req_address" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="child_iin" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="child_surname" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="child_name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="child_patronymic" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="child_birthday" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="child_documentnumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="child_issuer" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="child_issuedate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="decision_reason_kz" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="decision_reason_ru" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="decision_organization_kz" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="decision_organization_ru" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="decision_region_code" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="decision_number" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="decision_issuedate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="guardianship_end_number" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="guardianship_end_date" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="signedData" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetGuardianshipResponse", namespace = "http://helpers.guardianship.service.akimat.shep.nit", propOrder = {
    "resultCode",
    "id",
    "citizenship",
    "reqIin",
    "reqSurname",
    "reqName",
    "reqPatronymic",
    "reqBirthday",
    "reqDocumentnumber",
    "reqIssuer",
    "reqIssuedate",
    "reqAddress",
    "childIin",
    "childSurname",
    "childName",
    "childPatronymic",
    "childBirthday",
    "childDocumentnumber",
    "childIssuer",
    "childIssuedate",
    "decisionReasonKz",
    "decisionReasonRu",
    "decisionOrganizationKz",
    "decisionOrganizationRu",
    "decisionRegionCode",
    "decisionNumber",
    "decisionIssuedate",
    "guardianshipEndNumber",
    "guardianshipEndDate",
    "signedData"
})
public class GetGuardianshipResponse {

    @XmlElement(name = "result_code", required = true, nillable = true)
    protected String resultCode;
    protected long id;
    @XmlElement(required = true, nillable = true)
    protected String citizenship;
    @XmlElement(name = "req_iin", required = true, nillable = true)
    protected String reqIin;
    @XmlElement(name = "req_surname", required = true, nillable = true)
    protected String reqSurname;
    @XmlElement(name = "req_name", required = true, nillable = true)
    protected String reqName;
    @XmlElement(name = "req_patronymic", required = true, nillable = true)
    protected String reqPatronymic;
    @XmlElement(name = "req_birthday", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar reqBirthday;
    @XmlElement(name = "req_documentnumber", required = true, nillable = true)
    protected String reqDocumentnumber;
    @XmlElement(name = "req_issuer", required = true, nillable = true)
    protected String reqIssuer;
    @XmlElement(name = "req_issuedate", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar reqIssuedate;
    @XmlElement(name = "req_address", required = true, nillable = true)
    protected String reqAddress;
    @XmlElement(name = "child_iin", required = true, nillable = true)
    protected String childIin;
    @XmlElement(name = "child_surname", required = true, nillable = true)
    protected String childSurname;
    @XmlElement(name = "child_name", required = true, nillable = true)
    protected String childName;
    @XmlElement(name = "child_patronymic", required = true, nillable = true)
    protected String childPatronymic;
    @XmlElement(name = "child_birthday", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar childBirthday;
    @XmlElement(name = "child_documentnumber", required = true, nillable = true)
    protected String childDocumentnumber;
    @XmlElement(name = "child_issuer", required = true, nillable = true)
    protected String childIssuer;
    @XmlElement(name = "child_issuedate", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar childIssuedate;
    @XmlElement(name = "decision_reason_kz", required = true, nillable = true)
    protected String decisionReasonKz;
    @XmlElement(name = "decision_reason_ru", required = true, nillable = true)
    protected String decisionReasonRu;
    @XmlElement(name = "decision_organization_kz", required = true, nillable = true)
    protected String decisionOrganizationKz;
    @XmlElement(name = "decision_organization_ru", required = true, nillable = true)
    protected String decisionOrganizationRu;
    @XmlElement(name = "decision_region_code", required = true, nillable = true)
    protected String decisionRegionCode;
    @XmlElement(name = "decision_number", required = true, nillable = true)
    protected String decisionNumber;
    @XmlElement(name = "decision_issuedate", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar decisionIssuedate;
    @XmlElement(name = "guardianship_end_number", required = true, nillable = true)
    protected String guardianshipEndNumber;
    @XmlElement(name = "guardianship_end_date", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar guardianshipEndDate;
    @XmlElement(required = true, nillable = true)
    protected String signedData;

    /**
     * Gets the value of the resultCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResultCode() {
        return resultCode;
    }

    /**
     * Sets the value of the resultCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResultCode(String value) {
        this.resultCode = value;
    }

    /**
     * Gets the value of the id property.
     * 
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     */
    public void setId(long value) {
        this.id = value;
    }

    /**
     * Gets the value of the citizenship property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCitizenship() {
        return citizenship;
    }

    /**
     * Sets the value of the citizenship property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCitizenship(String value) {
        this.citizenship = value;
    }

    /**
     * Gets the value of the reqIin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReqIin() {
        return reqIin;
    }

    /**
     * Sets the value of the reqIin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReqIin(String value) {
        this.reqIin = value;
    }

    /**
     * Gets the value of the reqSurname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReqSurname() {
        return reqSurname;
    }

    /**
     * Sets the value of the reqSurname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReqSurname(String value) {
        this.reqSurname = value;
    }

    /**
     * Gets the value of the reqName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReqName() {
        return reqName;
    }

    /**
     * Sets the value of the reqName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReqName(String value) {
        this.reqName = value;
    }

    /**
     * Gets the value of the reqPatronymic property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReqPatronymic() {
        return reqPatronymic;
    }

    /**
     * Sets the value of the reqPatronymic property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReqPatronymic(String value) {
        this.reqPatronymic = value;
    }

    /**
     * Gets the value of the reqBirthday property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getReqBirthday() {
        return reqBirthday;
    }

    /**
     * Sets the value of the reqBirthday property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setReqBirthday(XMLGregorianCalendar value) {
        this.reqBirthday = value;
    }

    /**
     * Gets the value of the reqDocumentnumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReqDocumentnumber() {
        return reqDocumentnumber;
    }

    /**
     * Sets the value of the reqDocumentnumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReqDocumentnumber(String value) {
        this.reqDocumentnumber = value;
    }

    /**
     * Gets the value of the reqIssuer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReqIssuer() {
        return reqIssuer;
    }

    /**
     * Sets the value of the reqIssuer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReqIssuer(String value) {
        this.reqIssuer = value;
    }

    /**
     * Gets the value of the reqIssuedate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getReqIssuedate() {
        return reqIssuedate;
    }

    /**
     * Sets the value of the reqIssuedate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setReqIssuedate(XMLGregorianCalendar value) {
        this.reqIssuedate = value;
    }

    /**
     * Gets the value of the reqAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReqAddress() {
        return reqAddress;
    }

    /**
     * Sets the value of the reqAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReqAddress(String value) {
        this.reqAddress = value;
    }

    /**
     * Gets the value of the childIin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChildIin() {
        return childIin;
    }

    /**
     * Sets the value of the childIin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChildIin(String value) {
        this.childIin = value;
    }

    /**
     * Gets the value of the childSurname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChildSurname() {
        return childSurname;
    }

    /**
     * Sets the value of the childSurname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChildSurname(String value) {
        this.childSurname = value;
    }

    /**
     * Gets the value of the childName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChildName() {
        return childName;
    }

    /**
     * Sets the value of the childName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChildName(String value) {
        this.childName = value;
    }

    /**
     * Gets the value of the childPatronymic property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChildPatronymic() {
        return childPatronymic;
    }

    /**
     * Sets the value of the childPatronymic property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChildPatronymic(String value) {
        this.childPatronymic = value;
    }

    /**
     * Gets the value of the childBirthday property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getChildBirthday() {
        return childBirthday;
    }

    /**
     * Sets the value of the childBirthday property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setChildBirthday(XMLGregorianCalendar value) {
        this.childBirthday = value;
    }

    /**
     * Gets the value of the childDocumentnumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChildDocumentnumber() {
        return childDocumentnumber;
    }

    /**
     * Sets the value of the childDocumentnumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChildDocumentnumber(String value) {
        this.childDocumentnumber = value;
    }

    /**
     * Gets the value of the childIssuer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChildIssuer() {
        return childIssuer;
    }

    /**
     * Sets the value of the childIssuer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChildIssuer(String value) {
        this.childIssuer = value;
    }

    /**
     * Gets the value of the childIssuedate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getChildIssuedate() {
        return childIssuedate;
    }

    /**
     * Sets the value of the childIssuedate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setChildIssuedate(XMLGregorianCalendar value) {
        this.childIssuedate = value;
    }

    /**
     * Gets the value of the decisionReasonKz property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDecisionReasonKz() {
        return decisionReasonKz;
    }

    /**
     * Sets the value of the decisionReasonKz property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDecisionReasonKz(String value) {
        this.decisionReasonKz = value;
    }

    /**
     * Gets the value of the decisionReasonRu property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDecisionReasonRu() {
        return decisionReasonRu;
    }

    /**
     * Sets the value of the decisionReasonRu property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDecisionReasonRu(String value) {
        this.decisionReasonRu = value;
    }

    /**
     * Gets the value of the decisionOrganizationKz property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDecisionOrganizationKz() {
        return decisionOrganizationKz;
    }

    /**
     * Sets the value of the decisionOrganizationKz property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDecisionOrganizationKz(String value) {
        this.decisionOrganizationKz = value;
    }

    /**
     * Gets the value of the decisionOrganizationRu property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDecisionOrganizationRu() {
        return decisionOrganizationRu;
    }

    /**
     * Sets the value of the decisionOrganizationRu property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDecisionOrganizationRu(String value) {
        this.decisionOrganizationRu = value;
    }

    /**
     * Gets the value of the decisionRegionCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDecisionRegionCode() {
        return decisionRegionCode;
    }

    /**
     * Sets the value of the decisionRegionCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDecisionRegionCode(String value) {
        this.decisionRegionCode = value;
    }

    /**
     * Gets the value of the decisionNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDecisionNumber() {
        return decisionNumber;
    }

    /**
     * Sets the value of the decisionNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDecisionNumber(String value) {
        this.decisionNumber = value;
    }

    /**
     * Gets the value of the decisionIssuedate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDecisionIssuedate() {
        return decisionIssuedate;
    }

    /**
     * Sets the value of the decisionIssuedate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDecisionIssuedate(XMLGregorianCalendar value) {
        this.decisionIssuedate = value;
    }

    /**
     * Gets the value of the guardianshipEndNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGuardianshipEndNumber() {
        return guardianshipEndNumber;
    }

    /**
     * Sets the value of the guardianshipEndNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGuardianshipEndNumber(String value) {
        this.guardianshipEndNumber = value;
    }

    /**
     * Gets the value of the guardianshipEndDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getGuardianshipEndDate() {
        return guardianshipEndDate;
    }

    /**
     * Sets the value of the guardianshipEndDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setGuardianshipEndDate(XMLGregorianCalendar value) {
        this.guardianshipEndDate = value;
    }

    /**
     * Gets the value of the signedData property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSignedData() {
        return signedData;
    }

    /**
     * Sets the value of the signedData property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSignedData(String value) {
        this.signedData = value;
    }

}
