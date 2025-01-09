
package kz.tamur.or3.mtszn;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import kz.tamur.or3.mtszn.akimat.FarmInfoResponse;
import kz.tamur.or3.mtszn.akimat.GetGuardianshipResponse;
import kz.tamur.or3.mtszn.birth.BirthAktRecord;
import kz.tamur.or3.mtszn.natperson.Person;


/**
 * <p>Java class for member complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="member">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="eAkimatList" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="eAkimat" type="{http://types.efarm.service.akimat.shep.nit}FarmInfoResponse" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="ETrustee" type="{http://services.sync.mtszn/}eTrustee" minOccurs="0"/>
 *         &lt;element name="asuki" type="{http://helpers.guardianship.service.akimat.shep.nit}GetGuardianshipResponse" minOccurs="0"/>
 *         &lt;element name="cbdi" type="{http://services.sync.mtszn/}cbdi" minOccurs="0"/>
 *         &lt;element name="children" type="{http://birth.info.zags.kz}BirthAktRecord" minOccurs="0"/>
 *         &lt;element name="incomesList" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="ownIncome" type="{http://services.sync.mtszn/}ownIncome" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="monHigh" type="{http://services.sync.mtszn/}mon_high" minOccurs="0"/>
 *         &lt;element name="monSecondList" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="monSecond" type="{http://services.sync.mtszn/}mon_second" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="nkDataList" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="nkData" type="{http://services.sync.mtszn/}nkData" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="officialIncomesList" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="officialIncome" type="{http://services.sync.mtszn/}officialIncome" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="oralman" type="{http://services.sync.mtszn/}oralman" minOccurs="0"/>
 *         &lt;element name="participant2020" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="person" type="{http://person.persistence.interactive.nat}Person" minOccurs="0"/>
 *         &lt;element name="relationship" type="{http://services.sync.mtszn/}additionalName" minOccurs="0"/>
 *         &lt;element name="socStatus" type="{http://services.sync.mtszn/}socStatus" minOccurs="0"/>
 *         &lt;element name="unemp" type="{http://services.sync.mtszn/}unemp" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "member", propOrder = {
    "eAkimatList",
    "eTrustee",
    "asuki",
    "cbdi",
    "children",
    "incomesList",
    "monHigh",
    "monSecondList",
    "nkDataList",
    "officialIncomesList",
    "oralman",
    "participant2020",
    "person",
    "relationship",
    "socStatus",
    "unemp"
})
public class Member {

    protected Member.EAkimatList eAkimatList;
    @XmlElement(name = "ETrustee")
    protected ETrustee eTrustee;
    protected GetGuardianshipResponse asuki;
    protected Cbdi cbdi;
    protected BirthAktRecord children;
    protected Member.IncomesList incomesList;
    protected MonHigh monHigh;
    protected Member.MonSecondList monSecondList;
    protected Member.NkDataList nkDataList;
    protected Member.OfficialIncomesList officialIncomesList;
    protected Oralman oralman;
    protected int participant2020;
    protected Person person;
    protected AdditionalName relationship;
    protected SocStatus socStatus;
    protected Unemp unemp;

    /**
     * Gets the value of the eAkimatList property.
     * 
     * @return
     *     possible object is
     *     {@link Member.EAkimatList }
     *     
     */
    public Member.EAkimatList getEAkimatList() {
        return eAkimatList;
    }

    /**
     * Sets the value of the eAkimatList property.
     * 
     * @param value
     *     allowed object is
     *     {@link Member.EAkimatList }
     *     
     */
    public void setEAkimatList(Member.EAkimatList value) {
        this.eAkimatList = value;
    }

    /**
     * Gets the value of the eTrustee property.
     * 
     * @return
     *     possible object is
     *     {@link ETrustee }
     *     
     */
    public ETrustee getETrustee() {
        return eTrustee;
    }

    /**
     * Sets the value of the eTrustee property.
     * 
     * @param value
     *     allowed object is
     *     {@link ETrustee }
     *     
     */
    public void setETrustee(ETrustee value) {
        this.eTrustee = value;
    }

    /**
     * Gets the value of the asuki property.
     * 
     * @return
     *     possible object is
     *     {@link GetGuardianshipResponse }
     *     
     */
    public GetGuardianshipResponse getAsuki() {
        return asuki;
    }

    /**
     * Sets the value of the asuki property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetGuardianshipResponse }
     *     
     */
    public void setAsuki(GetGuardianshipResponse value) {
        this.asuki = value;
    }

    /**
     * Gets the value of the cbdi property.
     * 
     * @return
     *     possible object is
     *     {@link Cbdi }
     *     
     */
    public Cbdi getCbdi() {
        return cbdi;
    }

    /**
     * Sets the value of the cbdi property.
     * 
     * @param value
     *     allowed object is
     *     {@link Cbdi }
     *     
     */
    public void setCbdi(Cbdi value) {
        this.cbdi = value;
    }

    /**
     * Gets the value of the children property.
     * 
     * @return
     *     possible object is
     *     {@link BirthAktRecord }
     *     
     */
    public BirthAktRecord getChildren() {
        return children;
    }

    /**
     * Sets the value of the children property.
     * 
     * @param value
     *     allowed object is
     *     {@link BirthAktRecord }
     *     
     */
    public void setChildren(BirthAktRecord value) {
        this.children = value;
    }

    /**
     * Gets the value of the incomesList property.
     * 
     * @return
     *     possible object is
     *     {@link Member.IncomesList }
     *     
     */
    public Member.IncomesList getIncomesList() {
        return incomesList;
    }

    /**
     * Sets the value of the incomesList property.
     * 
     * @param value
     *     allowed object is
     *     {@link Member.IncomesList }
     *     
     */
    public void setIncomesList(Member.IncomesList value) {
        this.incomesList = value;
    }

    /**
     * Gets the value of the monHigh property.
     * 
     * @return
     *     possible object is
     *     {@link MonHigh }
     *     
     */
    public MonHigh getMonHigh() {
        return monHigh;
    }

    /**
     * Sets the value of the monHigh property.
     * 
     * @param value
     *     allowed object is
     *     {@link MonHigh }
     *     
     */
    public void setMonHigh(MonHigh value) {
        this.monHigh = value;
    }

    /**
     * Gets the value of the monSecondList property.
     * 
     * @return
     *     possible object is
     *     {@link Member.MonSecondList }
     *     
     */
    public Member.MonSecondList getMonSecondList() {
        return monSecondList;
    }

    /**
     * Sets the value of the monSecondList property.
     * 
     * @param value
     *     allowed object is
     *     {@link Member.MonSecondList }
     *     
     */
    public void setMonSecondList(Member.MonSecondList value) {
        this.monSecondList = value;
    }

    /**
     * Gets the value of the nkDataList property.
     * 
     * @return
     *     possible object is
     *     {@link Member.NkDataList }
     *     
     */
    public Member.NkDataList getNkDataList() {
        return nkDataList;
    }

    /**
     * Sets the value of the nkDataList property.
     * 
     * @param value
     *     allowed object is
     *     {@link Member.NkDataList }
     *     
     */
    public void setNkDataList(Member.NkDataList value) {
        this.nkDataList = value;
    }

    /**
     * Gets the value of the officialIncomesList property.
     * 
     * @return
     *     possible object is
     *     {@link Member.OfficialIncomesList }
     *     
     */
    public Member.OfficialIncomesList getOfficialIncomesList() {
        return officialIncomesList;
    }

    /**
     * Sets the value of the officialIncomesList property.
     * 
     * @param value
     *     allowed object is
     *     {@link Member.OfficialIncomesList }
     *     
     */
    public void setOfficialIncomesList(Member.OfficialIncomesList value) {
        this.officialIncomesList = value;
    }

    /**
     * Gets the value of the oralman property.
     * 
     * @return
     *     possible object is
     *     {@link Oralman }
     *     
     */
    public Oralman getOralman() {
        return oralman;
    }

    /**
     * Sets the value of the oralman property.
     * 
     * @param value
     *     allowed object is
     *     {@link Oralman }
     *     
     */
    public void setOralman(Oralman value) {
        this.oralman = value;
    }

    /**
     * Gets the value of the participant2020 property.
     * 
     */
    public int getParticipant2020() {
        return participant2020;
    }

    /**
     * Sets the value of the participant2020 property.
     * 
     */
    public void setParticipant2020(int value) {
        this.participant2020 = value;
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
     * Gets the value of the relationship property.
     * 
     * @return
     *     possible object is
     *     {@link AdditionalName }
     *     
     */
    public AdditionalName getRelationship() {
        return relationship;
    }

    /**
     * Sets the value of the relationship property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdditionalName }
     *     
     */
    public void setRelationship(AdditionalName value) {
        this.relationship = value;
    }

    /**
     * Gets the value of the socStatus property.
     * 
     * @return
     *     possible object is
     *     {@link SocStatus }
     *     
     */
    public SocStatus getSocStatus() {
        return socStatus;
    }

    /**
     * Sets the value of the socStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link SocStatus }
     *     
     */
    public void setSocStatus(SocStatus value) {
        this.socStatus = value;
    }

    /**
     * Gets the value of the unemp property.
     * 
     * @return
     *     possible object is
     *     {@link Unemp }
     *     
     */
    public Unemp getUnemp() {
        return unemp;
    }

    /**
     * Sets the value of the unemp property.
     * 
     * @param value
     *     allowed object is
     *     {@link Unemp }
     *     
     */
    public void setUnemp(Unemp value) {
        this.unemp = value;
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
     *         &lt;element name="eAkimat" type="{http://types.efarm.service.akimat.shep.nit}FarmInfoResponse" maxOccurs="unbounded" minOccurs="0"/>
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
        "eAkimat"
    })
    public static class EAkimatList {

        protected List<FarmInfoResponse> eAkimat;

        /**
         * Gets the value of the eAkimat property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the eAkimat property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEAkimat().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link FarmInfoResponse }
         * 
         * 
         */
        public List<FarmInfoResponse> getEAkimat() {
            if (eAkimat == null) {
                eAkimat = new ArrayList<FarmInfoResponse>();
            }
            return this.eAkimat;
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
     *         &lt;element name="ownIncome" type="{http://services.sync.mtszn/}ownIncome" maxOccurs="unbounded" minOccurs="0"/>
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
        "ownIncome"
    })
    public static class IncomesList {

        protected List<OwnIncome> ownIncome;

        /**
         * Gets the value of the ownIncome property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the ownIncome property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getOwnIncome().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link OwnIncome }
         * 
         * 
         */
        public List<OwnIncome> getOwnIncome() {
            if (ownIncome == null) {
                ownIncome = new ArrayList<OwnIncome>();
            }
            return this.ownIncome;
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
     *         &lt;element name="monSecond" type="{http://services.sync.mtszn/}mon_second" maxOccurs="unbounded" minOccurs="0"/>
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
        "monSecond"
    })
    public static class MonSecondList {

        protected List<MonSecond> monSecond;

        /**
         * Gets the value of the monSecond property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the monSecond property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getMonSecond().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link MonSecond }
         * 
         * 
         */
        public List<MonSecond> getMonSecond() {
            if (monSecond == null) {
                monSecond = new ArrayList<MonSecond>();
            }
            return this.monSecond;
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
     *         &lt;element name="nkData" type="{http://services.sync.mtszn/}nkData" maxOccurs="unbounded" minOccurs="0"/>
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
        "nkData"
    })
    public static class NkDataList {

        protected List<NkData> nkData;

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
     *         &lt;element name="officialIncome" type="{http://services.sync.mtszn/}officialIncome" maxOccurs="unbounded" minOccurs="0"/>
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
        "officialIncome"
    })
    public static class OfficialIncomesList {

        protected List<OfficialIncome> officialIncome;

        /**
         * Gets the value of the officialIncome property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the officialIncome property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getOfficialIncome().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link OfficialIncome }
         * 
         * 
         */
        public List<OfficialIncome> getOfficialIncome() {
            if (officialIncome == null) {
                officialIncome = new ArrayList<OfficialIncome>();
            }
            return this.officialIncome;
        }

    }

}
