
package kz.tamur.or3.mtszn;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import kz.tamur.or3.mtszn.epay.PEPIBANCheckResponse;


/**
 * <p>Java class for p06581RequestData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="p06581RequestData">
 *   &lt;complexContent>
 *     &lt;extension base="{http://services.sync.mtszn/}baseRequestData">
 *       &lt;sequence>
 *         &lt;element name="familyStatuses" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="familyStatus" type="{http://services.sync.mtszn/}familyStatus" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="gcvp_dep_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="help_type_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="memberList" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="member" type="{http://services.sync.mtszn/}member" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="pepIbanResponse" type="{http://epay.gov.kz/IBANService}PEP_IBANCheckResponse" minOccurs="0"/>
 *         &lt;element name="requestorIin" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="soc_card_num" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "p06581RequestData", propOrder = {
    "familyStatuses",
    "gcvpDepId",
    "helpTypeId",
    "memberList",
    "pepIbanResponse",
    "requestorIin",
    "socCardNum"
})
public class P06581RequestData
    extends BaseRequestData
{

    protected P06581RequestData.FamilyStatuses familyStatuses;
    @XmlElement(name = "gcvp_dep_id")
    protected String gcvpDepId;
    @XmlElement(name = "help_type_id")
    protected String helpTypeId;
    protected P06581RequestData.MemberList memberList;
    protected PEPIBANCheckResponse pepIbanResponse;
    protected String requestorIin;
    @XmlElement(name = "soc_card_num")
    protected String socCardNum;

    /**
     * Gets the value of the familyStatuses property.
     * 
     * @return
     *     possible object is
     *     {@link P06581RequestData.FamilyStatuses }
     *     
     */
    public P06581RequestData.FamilyStatuses getFamilyStatuses() {
        return familyStatuses;
    }

    /**
     * Sets the value of the familyStatuses property.
     * 
     * @param value
     *     allowed object is
     *     {@link P06581RequestData.FamilyStatuses }
     *     
     */
    public void setFamilyStatuses(P06581RequestData.FamilyStatuses value) {
        this.familyStatuses = value;
    }

    /**
     * Gets the value of the gcvpDepId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGcvpDepId() {
        return gcvpDepId;
    }

    /**
     * Sets the value of the gcvpDepId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGcvpDepId(String value) {
        this.gcvpDepId = value;
    }

    /**
     * Gets the value of the helpTypeId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHelpTypeId() {
        return helpTypeId;
    }

    /**
     * Sets the value of the helpTypeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHelpTypeId(String value) {
        this.helpTypeId = value;
    }

    /**
     * Gets the value of the memberList property.
     * 
     * @return
     *     possible object is
     *     {@link P06581RequestData.MemberList }
     *     
     */
    public P06581RequestData.MemberList getMemberList() {
        return memberList;
    }

    /**
     * Sets the value of the memberList property.
     * 
     * @param value
     *     allowed object is
     *     {@link P06581RequestData.MemberList }
     *     
     */
    public void setMemberList(P06581RequestData.MemberList value) {
        this.memberList = value;
    }

    /**
     * Gets the value of the pepIbanResponse property.
     * 
     * @return
     *     possible object is
     *     {@link PEPIBANCheckResponse }
     *     
     */
    public PEPIBANCheckResponse getPepIbanResponse() {
        return pepIbanResponse;
    }

    /**
     * Sets the value of the pepIbanResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link PEPIBANCheckResponse }
     *     
     */
    public void setPepIbanResponse(PEPIBANCheckResponse value) {
        this.pepIbanResponse = value;
    }

    /**
     * Gets the value of the requestorIin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestorIin() {
        return requestorIin;
    }

    /**
     * Sets the value of the requestorIin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestorIin(String value) {
        this.requestorIin = value;
    }

    /**
     * Gets the value of the socCardNum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSocCardNum() {
        return socCardNum;
    }

    /**
     * Sets the value of the socCardNum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSocCardNum(String value) {
        this.socCardNum = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="familyStatus" type="{http://services.sync.mtszn/}familyStatus" maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "familyStatus"
    })
    public static class FamilyStatuses {

        protected List<FamilyStatus> familyStatus;

        /**
         * Gets the value of the familyStatus property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the familyStatus property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getFamilyStatus().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link FamilyStatus }
         * 
         * 
         */
        public List<FamilyStatus> getFamilyStatus() {
            if (familyStatus == null) {
                familyStatus = new ArrayList<FamilyStatus>();
            }
            return this.familyStatus;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="member" type="{http://services.sync.mtszn/}member" maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "member"
    })
    public static class MemberList {

        protected List<Member> member;

        /**
         * Gets the value of the member property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the member property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getMember().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Member }
         * 
         * 
         */
        public List<Member> getMember() {
            if (member == null) {
                member = new ArrayList<Member>();
            }
            return this.member;
        }

    }

}
