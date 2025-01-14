//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.10.18 at 10:13:58 AM ALMT 
//


package kz.tamur.searchdata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * ���, ����������� �������� � ��������
 * 
 * <p>Java class for OrganizationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OrganizationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="BIN" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="RNN" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OKPO" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="RegNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="RegOrgan" type="{http://newshep.GbdulSearchData.gbdul.tamur.kz}DictionaryType"/>
 *         &lt;element name="Country" type="{http://newshep.GbdulSearchData.gbdul.tamur.kz}DictionaryType" minOccurs="0"/>
 *         &lt;element name="StatusCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="FormOrg" type="{http://newshep.GbdulSearchData.gbdul.tamur.kz}DictionaryType"/>
 *         &lt;element name="OrgLowForm" type="{http://newshep.GbdulSearchData.gbdul.tamur.kz}DictionaryType" minOccurs="0"/>
 *         &lt;element name="Commercial" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="PrivateEnterpriseType" type="{http://newshep.GbdulSearchData.gbdul.tamur.kz}DictionaryType" minOccurs="0"/>
 *         &lt;element name="StatusOO" type="{http://newshep.GbdulSearchData.gbdul.tamur.kz}DictionaryType" minOccurs="0"/>
 *         &lt;element name="PolitPart" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="Bank" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="CountryFac" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="ModelCharter" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="FullNameKZ" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="FullNameRU" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="FullNameEN" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="NameKZ" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="NameRU" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="NameEN" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="RegDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="ReestrDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="Leader" type="{http://newshep.GbdulSearchData.gbdul.tamur.kz}LeaderType" minOccurs="0"/>
 *         &lt;element name="Address" type="{http://newshep.GbdulSearchData.gbdul.tamur.kz}AddressType"/>
 *         &lt;element name="AuthCapital" type="{http://newshep.GbdulSearchData.gbdul.tamur.kz}AuthCapitalType" minOccurs="0"/>
 *         &lt;element name="Founders" type="{http://newshep.GbdulSearchData.gbdul.tamur.kz}FoundersType" minOccurs="0"/>
 *         &lt;element name="FounderCount" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="HeaderOrganization" type="{http://newshep.GbdulSearchData.gbdul.tamur.kz}HeadType" minOccurs="0"/>
 *         &lt;element name="Services" type="{http://newshep.GbdulSearchData.gbdul.tamur.kz}ServicesType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrganizationType", propOrder = {
    "bin",
    "rnn",
    "okpo",
    "regNumber",
    "regOrgan",
    "country",
    "statusCode",
    "formOrg",
    "orgLowForm",
    "commercial",
    "privateEnterpriseType",
    "statusOO",
    "politPart",
    "bank",
    "countryFac",
    "modelCharter",
    "fullNameKZ",
    "fullNameRU",
    "fullNameEN",
    "nameKZ",
    "nameRU",
    "nameEN",
    "regDate",
    "reestrDate",
    "leader",
    "address",
    "authCapital",
    "founders",
    "founderCount",
    "headerOrganization",
    "services"
})
public class OrganizationType {

    @XmlElement(name = "BIN")
    protected String bin;
    @XmlElement(name = "RNN")
    protected String rnn;
    @XmlElement(name = "OKPO")
    protected String okpo;
    @XmlElement(name = "RegNumber")
    protected String regNumber;
    @XmlElement(name = "RegOrgan", required = true)
    protected DictionaryType regOrgan;
    @XmlElement(name = "Country")
    protected DictionaryType country;
    @XmlElement(name = "StatusCode", required = true)
    protected String statusCode;
    @XmlElement(name = "FormOrg", required = true)
    protected DictionaryType formOrg;
    @XmlElement(name = "OrgLowForm")
    protected DictionaryType orgLowForm;
    @XmlElement(name = "Commercial")
    protected boolean commercial;
    @XmlElement(name = "PrivateEnterpriseType")
    protected DictionaryType privateEnterpriseType;
    @XmlElement(name = "StatusOO")
    protected DictionaryType statusOO;
    @XmlElement(name = "PolitPart")
    protected boolean politPart;
    @XmlElement(name = "Bank")
    protected boolean bank;
    @XmlElement(name = "CountryFac")
    protected boolean countryFac;
    @XmlElement(name = "ModelCharter")
    protected boolean modelCharter;
    @XmlElement(name = "FullNameKZ", required = true)
    protected String fullNameKZ;
    @XmlElement(name = "FullNameRU", required = true)
    protected String fullNameRU;
    @XmlElement(name = "FullNameEN")
    protected String fullNameEN;
    @XmlElement(name = "NameKZ")
    protected String nameKZ;
    @XmlElement(name = "NameRU")
    protected String nameRU;
    @XmlElement(name = "NameEN")
    protected String nameEN;
    @XmlElement(name = "RegDate", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar regDate;
    @XmlElement(name = "ReestrDate", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar reestrDate;
    @XmlElement(name = "Leader")
    protected LeaderType leader;
    @XmlElement(name = "Address", required = true)
    protected AddressType address;
    @XmlElement(name = "AuthCapital")
    protected AuthCapitalType authCapital;
    @XmlElement(name = "Founders")
    protected FoundersType founders;
    @XmlElement(name = "FounderCount")
    protected Integer founderCount;
    @XmlElement(name = "HeaderOrganization")
    protected HeadType headerOrganization;
    @XmlElement(name = "Services")
    protected ServicesType services;

    /**
     * Gets the value of the bin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBIN() {
        return bin;
    }

    /**
     * Sets the value of the bin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBIN(String value) {
        this.bin = value;
    }

    /**
     * Gets the value of the rnn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRNN() {
        return rnn;
    }

    /**
     * Sets the value of the rnn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRNN(String value) {
        this.rnn = value;
    }

    /**
     * Gets the value of the okpo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOKPO() {
        return okpo;
    }

    /**
     * Sets the value of the okpo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOKPO(String value) {
        this.okpo = value;
    }

    /**
     * Gets the value of the regNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegNumber() {
        return regNumber;
    }

    /**
     * Sets the value of the regNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegNumber(String value) {
        this.regNumber = value;
    }

    /**
     * Gets the value of the regOrgan property.
     * 
     * @return
     *     possible object is
     *     {@link DictionaryType }
     *     
     */
    public DictionaryType getRegOrgan() {
        return regOrgan;
    }

    /**
     * Sets the value of the regOrgan property.
     * 
     * @param value
     *     allowed object is
     *     {@link DictionaryType }
     *     
     */
    public void setRegOrgan(DictionaryType value) {
        this.regOrgan = value;
    }

    /**
     * Gets the value of the country property.
     * 
     * @return
     *     possible object is
     *     {@link DictionaryType }
     *     
     */
    public DictionaryType getCountry() {
        return country;
    }

    /**
     * Sets the value of the country property.
     * 
     * @param value
     *     allowed object is
     *     {@link DictionaryType }
     *     
     */
    public void setCountry(DictionaryType value) {
        this.country = value;
    }

    /**
     * Gets the value of the statusCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatusCode() {
        return statusCode;
    }

    /**
     * Sets the value of the statusCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatusCode(String value) {
        this.statusCode = value;
    }

    /**
     * Gets the value of the formOrg property.
     * 
     * @return
     *     possible object is
     *     {@link DictionaryType }
     *     
     */
    public DictionaryType getFormOrg() {
        return formOrg;
    }

    /**
     * Sets the value of the formOrg property.
     * 
     * @param value
     *     allowed object is
     *     {@link DictionaryType }
     *     
     */
    public void setFormOrg(DictionaryType value) {
        this.formOrg = value;
    }

    /**
     * Gets the value of the orgLowForm property.
     * 
     * @return
     *     possible object is
     *     {@link DictionaryType }
     *     
     */
    public DictionaryType getOrgLowForm() {
        return orgLowForm;
    }

    /**
     * Sets the value of the orgLowForm property.
     * 
     * @param value
     *     allowed object is
     *     {@link DictionaryType }
     *     
     */
    public void setOrgLowForm(DictionaryType value) {
        this.orgLowForm = value;
    }

    /**
     * Gets the value of the commercial property.
     * 
     */
    public boolean isCommercial() {
        return commercial;
    }

    /**
     * Sets the value of the commercial property.
     * 
     */
    public void setCommercial(boolean value) {
        this.commercial = value;
    }

    /**
     * Gets the value of the privateEnterpriseType property.
     * 
     * @return
     *     possible object is
     *     {@link DictionaryType }
     *     
     */
    public DictionaryType getPrivateEnterpriseType() {
        return privateEnterpriseType;
    }

    /**
     * Sets the value of the privateEnterpriseType property.
     * 
     * @param value
     *     allowed object is
     *     {@link DictionaryType }
     *     
     */
    public void setPrivateEnterpriseType(DictionaryType value) {
        this.privateEnterpriseType = value;
    }

    /**
     * Gets the value of the statusOO property.
     * 
     * @return
     *     possible object is
     *     {@link DictionaryType }
     *     
     */
    public DictionaryType getStatusOO() {
        return statusOO;
    }

    /**
     * Sets the value of the statusOO property.
     * 
     * @param value
     *     allowed object is
     *     {@link DictionaryType }
     *     
     */
    public void setStatusOO(DictionaryType value) {
        this.statusOO = value;
    }

    /**
     * Gets the value of the politPart property.
     * 
     */
    public boolean isPolitPart() {
        return politPart;
    }

    /**
     * Sets the value of the politPart property.
     * 
     */
    public void setPolitPart(boolean value) {
        this.politPart = value;
    }

    /**
     * Gets the value of the bank property.
     * 
     */
    public boolean isBank() {
        return bank;
    }

    /**
     * Sets the value of the bank property.
     * 
     */
    public void setBank(boolean value) {
        this.bank = value;
    }

    /**
     * Gets the value of the countryFac property.
     * 
     */
    public boolean isCountryFac() {
        return countryFac;
    }

    /**
     * Sets the value of the countryFac property.
     * 
     */
    public void setCountryFac(boolean value) {
        this.countryFac = value;
    }

    /**
     * Gets the value of the modelCharter property.
     * 
     */
    public boolean isModelCharter() {
        return modelCharter;
    }

    /**
     * Sets the value of the modelCharter property.
     * 
     */
    public void setModelCharter(boolean value) {
        this.modelCharter = value;
    }

    /**
     * Gets the value of the fullNameKZ property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFullNameKZ() {
        return fullNameKZ;
    }

    /**
     * Sets the value of the fullNameKZ property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFullNameKZ(String value) {
        this.fullNameKZ = value;
    }

    /**
     * Gets the value of the fullNameRU property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFullNameRU() {
        return fullNameRU;
    }

    /**
     * Sets the value of the fullNameRU property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFullNameRU(String value) {
        this.fullNameRU = value;
    }

    /**
     * Gets the value of the fullNameEN property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFullNameEN() {
        return fullNameEN;
    }

    /**
     * Sets the value of the fullNameEN property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFullNameEN(String value) {
        this.fullNameEN = value;
    }

    /**
     * Gets the value of the nameKZ property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNameKZ() {
        return nameKZ;
    }

    /**
     * Sets the value of the nameKZ property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNameKZ(String value) {
        this.nameKZ = value;
    }

    /**
     * Gets the value of the nameRU property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNameRU() {
        return nameRU;
    }

    /**
     * Sets the value of the nameRU property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNameRU(String value) {
        this.nameRU = value;
    }

    /**
     * Gets the value of the nameEN property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNameEN() {
        return nameEN;
    }

    /**
     * Sets the value of the nameEN property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNameEN(String value) {
        this.nameEN = value;
    }

    /**
     * Gets the value of the regDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getRegDate() {
        return regDate;
    }

    /**
     * Sets the value of the regDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setRegDate(XMLGregorianCalendar value) {
        this.regDate = value;
    }

    /**
     * Gets the value of the reestrDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getReestrDate() {
        return reestrDate;
    }

    /**
     * Sets the value of the reestrDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setReestrDate(XMLGregorianCalendar value) {
        this.reestrDate = value;
    }

    /**
     * Gets the value of the leader property.
     * 
     * @return
     *     possible object is
     *     {@link LeaderType }
     *     
     */
    public LeaderType getLeader() {
        return leader;
    }

    /**
     * Sets the value of the leader property.
     * 
     * @param value
     *     allowed object is
     *     {@link LeaderType }
     *     
     */
    public void setLeader(LeaderType value) {
        this.leader = value;
    }

    /**
     * Gets the value of the address property.
     * 
     * @return
     *     possible object is
     *     {@link AddressType }
     *     
     */
    public AddressType getAddress() {
        return address;
    }

    /**
     * Sets the value of the address property.
     * 
     * @param value
     *     allowed object is
     *     {@link AddressType }
     *     
     */
    public void setAddress(AddressType value) {
        this.address = value;
    }

    /**
     * Gets the value of the authCapital property.
     * 
     * @return
     *     possible object is
     *     {@link AuthCapitalType }
     *     
     */
    public AuthCapitalType getAuthCapital() {
        return authCapital;
    }

    /**
     * Sets the value of the authCapital property.
     * 
     * @param value
     *     allowed object is
     *     {@link AuthCapitalType }
     *     
     */
    public void setAuthCapital(AuthCapitalType value) {
        this.authCapital = value;
    }

    /**
     * Gets the value of the founders property.
     * 
     * @return
     *     possible object is
     *     {@link FoundersType }
     *     
     */
    public FoundersType getFounders() {
        return founders;
    }

    /**
     * Sets the value of the founders property.
     * 
     * @param value
     *     allowed object is
     *     {@link FoundersType }
     *     
     */
    public void setFounders(FoundersType value) {
        this.founders = value;
    }

    /**
     * Gets the value of the founderCount property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getFounderCount() {
        return founderCount;
    }

    /**
     * Sets the value of the founderCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setFounderCount(Integer value) {
        this.founderCount = value;
    }

    /**
     * Gets the value of the headerOrganization property.
     * 
     * @return
     *     possible object is
     *     {@link HeadType }
     *     
     */
    public HeadType getHeaderOrganization() {
        return headerOrganization;
    }

    /**
     * Sets the value of the headerOrganization property.
     * 
     * @param value
     *     allowed object is
     *     {@link HeadType }
     *     
     */
    public void setHeaderOrganization(HeadType value) {
        this.headerOrganization = value;
    }

    /**
     * Gets the value of the services property.
     * 
     * @return
     *     possible object is
     *     {@link ServicesType }
     *     
     */
    public ServicesType getServices() {
        return services;
    }

    /**
     * Sets the value of the services property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServicesType }
     *     
     */
    public void setServices(ServicesType value) {
        this.services = value;
    }

}
