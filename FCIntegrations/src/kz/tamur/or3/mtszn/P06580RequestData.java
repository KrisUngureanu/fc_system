
package kz.tamur.or3.mtszn;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import kz.tamur.or3.mtszn.akimat.GetGuardianshipResponse;
import kz.tamur.or3.mtszn.birth.BirthAktRecord;
import kz.tamur.or3.mtszn.epay.PEPIBANCheckRequest;
import kz.tamur.or3.mtszn.epay.PEPIBANCheckResponse;
import kz.tamur.or3.mtszn.natperson.Person;


/**
 * <p>Java class for p06580RequestData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="p06580RequestData">
 *   &lt;complexContent>
 *     &lt;extension base="{http://services.sync.mtszn/}baseRequestData">
 *       &lt;sequence>
 *         &lt;element name="additionalData" type="{http://services.sync.mtszn/}p06580AdditionalData" minOccurs="0"/>
 *         &lt;element name="birthAktRecord" type="{http://birth.info.zags.kz}BirthAktRecord" minOccurs="0"/>
 *         &lt;element name="buildingPassport" type="{http://services.sync.mtszn/}buildingPassport" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="cbdi" type="{http://services.sync.mtszn/}p06560ResponseData" minOccurs="0"/>
 *         &lt;element name="flatPassport" type="{http://services.sync.mtszn/}flatPassport" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="monData" type="{http://services.sync.mtszn/}monData" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="monTrustee" type="{http://helpers.guardianship.service.akimat.shep.nit}GetGuardianshipResponse" minOccurs="0"/>
 *         &lt;element name="mzData" type="{http://services.sync.mtszn/}mzData" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="nkData" type="{http://services.sync.mtszn/}nkData" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="officialIncomesList" type="{http://services.sync.mtszn/}officialIncome" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="person" type="{http://person.persistence.interactive.nat}Person" minOccurs="0"/>
 *         &lt;element name="pshepRequestData" type="{http://epay.gov.kz/IBANService}PEP_IBANCheckRequest" minOccurs="0"/>
 *         &lt;element name="pshepResponseData" type="{http://epay.gov.kz/IBANService}PEP_IBANCheckResponse" minOccurs="0"/>
 *         &lt;element name="socStatus" type="{http://services.sync.mtszn/}p06552ResponseData" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "p06580RequestData", propOrder = {
    "additionalData",
    "birthAktRecord",
    "buildingPassport",
    "cbdi",
    "flatPassport",
    "monData",
    "monTrustee",
    "mzData",
    "nkData",
    "officialIncomesList",
    "person",
    "pshepRequestData",
    "pshepResponseData",
    "socStatus"
})
public class P06580RequestData
    extends BaseRequestData
{

    protected P06580AdditionalData additionalData;
    protected BirthAktRecord birthAktRecord;
    @XmlElement(nillable = true)
    protected List<BuildingPassport> buildingPassport;
    protected P06560ResponseData cbdi;
    @XmlElement(nillable = true)
    protected List<FlatPassport> flatPassport;
    @XmlElement(nillable = true)
    protected List<MonData> monData;
    protected GetGuardianshipResponse monTrustee;
    @XmlElement(nillable = true)
    protected List<MzData> mzData;
    @XmlElement(nillable = true)
    protected List<NkData> nkData;
    @XmlElement(nillable = true)
    protected List<OfficialIncome> officialIncomesList;
    protected Person person;
    protected PEPIBANCheckRequest pshepRequestData;
    protected PEPIBANCheckResponse pshepResponseData;
    protected P06552ResponseData socStatus;

    /**
     * Gets the value of the additionalData property.
     * 
     * @return
     *     possible object is
     *     {@link P06580AdditionalData }
     *     
     */
    public P06580AdditionalData getAdditionalData() {
        return additionalData;
    }

    /**
     * Sets the value of the additionalData property.
     * 
     * @param value
     *     allowed object is
     *     {@link P06580AdditionalData }
     *     
     */
    public void setAdditionalData(P06580AdditionalData value) {
        this.additionalData = value;
    }

    /**
     * Gets the value of the birthAktRecord property.
     * 
     * @return
     *     possible object is
     *     {@link BirthAktRecord }
     *     
     */
    public BirthAktRecord getBirthAktRecord() {
        return birthAktRecord;
    }

    /**
     * Sets the value of the birthAktRecord property.
     * 
     * @param value
     *     allowed object is
     *     {@link BirthAktRecord }
     *     
     */
    public void setBirthAktRecord(BirthAktRecord value) {
        this.birthAktRecord = value;
    }

    /**
     * Gets the value of the buildingPassport property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the buildingPassport property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBuildingPassport().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BuildingPassport }
     * 
     * 
     */
    public List<BuildingPassport> getBuildingPassport() {
        if (buildingPassport == null) {
            buildingPassport = new ArrayList<BuildingPassport>();
        }
        return this.buildingPassport;
    }

    /**
     * Gets the value of the cbdi property.
     * 
     * @return
     *     possible object is
     *     {@link P06560ResponseData }
     *     
     */
    public P06560ResponseData getCbdi() {
        return cbdi;
    }

    /**
     * Sets the value of the cbdi property.
     * 
     * @param value
     *     allowed object is
     *     {@link P06560ResponseData }
     *     
     */
    public void setCbdi(P06560ResponseData value) {
        this.cbdi = value;
    }

    /**
     * Gets the value of the flatPassport property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the flatPassport property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFlatPassport().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FlatPassport }
     * 
     * 
     */
    public List<FlatPassport> getFlatPassport() {
        if (flatPassport == null) {
            flatPassport = new ArrayList<FlatPassport>();
        }
        return this.flatPassport;
    }

    /**
     * Gets the value of the monData property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the monData property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMonData().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MonData }
     * 
     * 
     */
    public List<MonData> getMonData() {
        if (monData == null) {
            monData = new ArrayList<MonData>();
        }
        return this.monData;
    }

    /**
     * Gets the value of the monTrustee property.
     * 
     * @return
     *     possible object is
     *     {@link GetGuardianshipResponse }
     *     
     */
    public GetGuardianshipResponse getMonTrustee() {
        return monTrustee;
    }

    /**
     * Sets the value of the monTrustee property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetGuardianshipResponse }
     *     
     */
    public void setMonTrustee(GetGuardianshipResponse value) {
        this.monTrustee = value;
    }

    /**
     * Gets the value of the mzData property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the mzData property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMzData().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MzData }
     * 
     * 
     */
    public List<MzData> getMzData() {
        if (mzData == null) {
            mzData = new ArrayList<MzData>();
        }
        return this.mzData;
    }

    /**
     * Gets the value of the nkData property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the nkData property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNkData().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NkData }
     * 
     * 
     */
    public List<NkData> getNkData() {
        if (nkData == null) {
            nkData = new ArrayList<NkData>();
        }
        return this.nkData;
    }

    /**
     * Gets the value of the officialIncomesList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the officialIncomesList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOfficialIncomesList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OfficialIncome }
     * 
     * 
     */
    public List<OfficialIncome> getOfficialIncomesList() {
        if (officialIncomesList == null) {
            officialIncomesList = new ArrayList<OfficialIncome>();
        }
        return this.officialIncomesList;
    }

    /**
     * Gets the value of the person property.
     * 
     * @return
     *     possible object is
     *     {@link Person }
     *     
     */
    public Person getPerson() {
        return person;
    }

    /**
     * Sets the value of the person property.
     * 
     * @param value
     *     allowed object is
     *     {@link Person }
     *     
     */
    public void setPerson(Person value) {
        this.person = value;
    }

    /**
     * Gets the value of the pshepRequestData property.
     * 
     * @return
     *     possible object is
     *     {@link PEPIBANCheckRequest }
     *     
     */
    public PEPIBANCheckRequest getPshepRequestData() {
        return pshepRequestData;
    }

    /**
     * Sets the value of the pshepRequestData property.
     * 
     * @param value
     *     allowed object is
     *     {@link PEPIBANCheckRequest }
     *     
     */
    public void setPshepRequestData(PEPIBANCheckRequest value) {
        this.pshepRequestData = value;
    }

    /**
     * Gets the value of the pshepResponseData property.
     * 
     * @return
     *     possible object is
     *     {@link PEPIBANCheckResponse }
     *     
     */
    public PEPIBANCheckResponse getPshepResponseData() {
        return pshepResponseData;
    }

    /**
     * Sets the value of the pshepResponseData property.
     * 
     * @param value
     *     allowed object is
     *     {@link PEPIBANCheckResponse }
     *     
     */
    public void setPshepResponseData(PEPIBANCheckResponse value) {
        this.pshepResponseData = value;
    }

    /**
     * Gets the value of the socStatus property.
     * 
     * @return
     *     possible object is
     *     {@link P06552ResponseData }
     *     
     */
    public P06552ResponseData getSocStatus() {
        return socStatus;
    }

    /**
     * Sets the value of the socStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link P06552ResponseData }
     *     
     */
    public void setSocStatus(P06552ResponseData value) {
        this.socStatus = value;
    }

}
