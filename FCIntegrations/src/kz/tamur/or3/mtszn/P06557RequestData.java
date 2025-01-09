
package kz.tamur.or3.mtszn;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import kz.tamur.or3.mtszn.igcvp.P06501Response;
import kz.tamur.or3.mtszn.mvdvuinfo.GetVUInfoResponse;
import kz.tamur.or3.mtszn.natperson.Child;


/**
 * <p>Java class for p06557RequestData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="p06557RequestData">
 *   &lt;complexContent>
 *     &lt;extension base="{http://services.sync.mtszn/}baseRequestData">
 *       &lt;sequence>
 *         &lt;element name="addData" type="{http://services.sync.mtszn/}additionalData" minOccurs="0"/>
 *         &lt;element name="catType" type="{http://services.sync.mtszn/}catType" minOccurs="0"/>
 *         &lt;element name="children" type="{http://person.persistence.interactive.nat}Child" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="department" type="{http://services.sync.mtszn/}additionalName" minOccurs="0"/>
 *         &lt;element name="driverLicense" type="{http://MVD.ProxyServices/mvd/DriverCardInfo/VUInfoService}getVUInfoResponse" minOccurs="0"/>
 *         &lt;element name="educations" type="{http://services.sync.mtszn/}education" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="languages" type="{http://services.sync.mtszn/}languageName" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="lastPayment" type="{http://services.sync.mtszn/}gcvpPayment" minOccurs="0"/>
 *         &lt;element name="person" type="{http://services.sync.mtszn/}person" minOccurs="0"/>
 *         &lt;element name="socStatus" type="{http://igcvp_ss_sysinfo.interfaces.allowance.mtszn.mtszn.allowance.library/}p06501Response" minOccurs="0"/>
 *         &lt;element name="uldata" type="{http://services.sync.mtszn/}ulData" minOccurs="0"/>
 *         &lt;element name="workActivities" type="{http://services.sync.mtszn/}workActivity" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "p06557RequestData", propOrder = {
    "addData",
    "catType",
    "children",
    "department",
    "driverLicense",
    "educations",
    "languages",
    "lastPayment",
    "person",
    "socStatus",
    "uldata",
    "workActivities"
})
public class P06557RequestData
    extends BaseRequestData
{

    protected AdditionalData addData;
    protected CatType catType;
    @XmlElement(nillable = true)
    protected List<Child> children;
    protected AdditionalName department;
    protected GetVUInfoResponse driverLicense;
    @XmlElement(nillable = true)
    protected List<Education> educations;
    @XmlElement(nillable = true)
    protected List<LanguageName> languages;
    protected GcvpPayment lastPayment;
    protected Person person;
    protected P06501Response socStatus;
    protected UlData uldata;
    @XmlElement(nillable = true)
    protected List<WorkActivity> workActivities;

    /**
     * Gets the value of the addData property.
     * 
     * @return
     *     possible object is
     *     {@link AdditionalData }
     *     
     */
    public AdditionalData getAddData() {
        return addData;
    }

    /**
     * Sets the value of the addData property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdditionalData }
     *     
     */
    public void setAddData(AdditionalData value) {
        this.addData = value;
    }

    /**
     * Gets the value of the catType property.
     * 
     * @return
     *     possible object is
     *     {@link CatType }
     *     
     */
    public CatType getCatType() {
        return catType;
    }

    /**
     * Sets the value of the catType property.
     * 
     * @param value
     *     allowed object is
     *     {@link CatType }
     *     
     */
    public void setCatType(CatType value) {
        this.catType = value;
    }

    /**
     * Gets the value of the children property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the children property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getChildren().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Child }
     * 
     * 
     */
    public List<Child> getChildren() {
        if (children == null) {
            children = new ArrayList<Child>();
        }
        return this.children;
    }

    /**
     * Gets the value of the department property.
     * 
     * @return
     *     possible object is
     *     {@link AdditionalName }
     *     
     */
    public AdditionalName getDepartment() {
        return department;
    }

    /**
     * Sets the value of the department property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdditionalName }
     *     
     */
    public void setDepartment(AdditionalName value) {
        this.department = value;
    }

    /**
     * Gets the value of the driverLicense property.
     * 
     * @return
     *     possible object is
     *     {@link GetVUInfoResponse }
     *     
     */
    public GetVUInfoResponse getDriverLicense() {
        return driverLicense;
    }

    /**
     * Sets the value of the driverLicense property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetVUInfoResponse }
     *     
     */
    public void setDriverLicense(GetVUInfoResponse value) {
        this.driverLicense = value;
    }

    /**
     * Gets the value of the educations property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the educations property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEducations().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Education }
     * 
     * 
     */
    public List<Education> getEducations() {
        if (educations == null) {
            educations = new ArrayList<Education>();
        }
        return this.educations;
    }

    /**
     * Gets the value of the languages property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the languages property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLanguages().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LanguageName }
     * 
     * 
     */
    public List<LanguageName> getLanguages() {
        if (languages == null) {
            languages = new ArrayList<LanguageName>();
        }
        return this.languages;
    }

    /**
     * Gets the value of the lastPayment property.
     * 
     * @return
     *     possible object is
     *     {@link GcvpPayment }
     *     
     */
    public GcvpPayment getLastPayment() {
        return lastPayment;
    }

    /**
     * Sets the value of the lastPayment property.
     * 
     * @param value
     *     allowed object is
     *     {@link GcvpPayment }
     *     
     */
    public void setLastPayment(GcvpPayment value) {
        this.lastPayment = value;
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
     * Gets the value of the socStatus property.
     * 
     * @return
     *     possible object is
     *     {@link P06501Response }
     *     
     */
    public P06501Response getSocStatus() {
        return socStatus;
    }

    /**
     * Sets the value of the socStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link P06501Response }
     *     
     */
    public void setSocStatus(P06501Response value) {
        this.socStatus = value;
    }

    /**
     * Gets the value of the uldata property.
     * 
     * @return
     *     possible object is
     *     {@link UlData }
     *     
     */
    public UlData getUldata() {
        return uldata;
    }

    /**
     * Sets the value of the uldata property.
     * 
     * @param value
     *     allowed object is
     *     {@link UlData }
     *     
     */
    public void setUldata(UlData value) {
        this.uldata = value;
    }

    /**
     * Gets the value of the workActivities property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the workActivities property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getWorkActivities().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WorkActivity }
     * 
     * 
     */
    public List<WorkActivity> getWorkActivities() {
        if (workActivities == null) {
            workActivities = new ArrayList<WorkActivity>();
        }
        return this.workActivities;
    }

}
