
package kz.tamur.gbdul.fl.dictionaries;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the kz.tamur.gbdul.fl.dictionaries package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _GpTerritorialChangeDate_QNAME = new QName("", "changeDate");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: kz.tamur.gbdul.fl.dictionaries
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GpTerritorial }
     * 
     */
    public GpTerritorial createGpTerritorial() {
        return new GpTerritorial();
    }

    /**
     * Create an instance of {@link MessageResult }
     * 
     */
    public MessageResult createMessageResult() {
        return new MessageResult();
    }

    /**
     * Create an instance of {@link PersonStatus }
     * 
     */
    public PersonStatus createPersonStatus() {
        return new PersonStatus();
    }

    /**
     * Create an instance of {@link DocumentOrganization }
     * 
     */
    public DocumentOrganization createDocumentOrganization() {
        return new DocumentOrganization();
    }

    /**
     * Create an instance of {@link Sex }
     * 
     */
    public Sex createSex() {
        return new Sex();
    }

    /**
     * Create an instance of {@link Country }
     * 
     */
    public Country createCountry() {
        return new Country();
    }

    /**
     * Create an instance of {@link District }
     * 
     */
    public District createDistrict() {
        return new District();
    }

    /**
     * Create an instance of {@link DocumentInvalidity }
     * 
     */
    public DocumentInvalidity createDocumentInvalidity() {
        return new DocumentInvalidity();
    }

    /**
     * Create an instance of {@link Region }
     * 
     */
    public Region createRegion() {
        return new Region();
    }

    /**
     * Create an instance of {@link Nationality }
     * 
     */
    public Nationality createNationality() {
        return new Nationality();
    }

    /**
     * Create an instance of {@link ExcludeReason }
     * 
     */
    public ExcludeReason createExcludeReason() {
        return new ExcludeReason();
    }

    /**
     * Create an instance of {@link Participant }
     * 
     */
    public Participant createParticipant() {
        return new Participant();
    }

    /**
     * Create an instance of {@link Court }
     * 
     */
    public Court createCourt() {
        return new Court();
    }

    /**
     * Create an instance of {@link CapableStatus }
     * 
     */
    public CapableStatus createCapableStatus() {
        return new CapableStatus();
    }

    /**
     * Create an instance of {@link DocumentType }
     * 
     */
    public DocumentType createDocumentType() {
        return new DocumentType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "changeDate", scope = GpTerritorial.class)
    public JAXBElement<XMLGregorianCalendar> createGpTerritorialChangeDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_GpTerritorialChangeDate_QNAME, XMLGregorianCalendar.class, GpTerritorial.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "changeDate", scope = Region.class)
    public JAXBElement<XMLGregorianCalendar> createRegionChangeDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_GpTerritorialChangeDate_QNAME, XMLGregorianCalendar.class, Region.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "changeDate", scope = PersonStatus.class)
    public JAXBElement<XMLGregorianCalendar> createPersonStatusChangeDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_GpTerritorialChangeDate_QNAME, XMLGregorianCalendar.class, PersonStatus.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "changeDate", scope = MessageResult.class)
    public JAXBElement<XMLGregorianCalendar> createMessageResultChangeDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_GpTerritorialChangeDate_QNAME, XMLGregorianCalendar.class, MessageResult.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "changeDate", scope = Nationality.class)
    public JAXBElement<XMLGregorianCalendar> createNationalityChangeDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_GpTerritorialChangeDate_QNAME, XMLGregorianCalendar.class, Nationality.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "changeDate", scope = ExcludeReason.class)
    public JAXBElement<XMLGregorianCalendar> createExcludeReasonChangeDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_GpTerritorialChangeDate_QNAME, XMLGregorianCalendar.class, ExcludeReason.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "changeDate", scope = Participant.class)
    public JAXBElement<XMLGregorianCalendar> createParticipantChangeDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_GpTerritorialChangeDate_QNAME, XMLGregorianCalendar.class, Participant.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "changeDate", scope = DocumentOrganization.class)
    public JAXBElement<XMLGregorianCalendar> createDocumentOrganizationChangeDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_GpTerritorialChangeDate_QNAME, XMLGregorianCalendar.class, DocumentOrganization.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "changeDate", scope = Court.class)
    public JAXBElement<XMLGregorianCalendar> createCourtChangeDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_GpTerritorialChangeDate_QNAME, XMLGregorianCalendar.class, Court.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "changeDate", scope = CapableStatus.class)
    public JAXBElement<XMLGregorianCalendar> createCapableStatusChangeDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_GpTerritorialChangeDate_QNAME, XMLGregorianCalendar.class, CapableStatus.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "changeDate", scope = Sex.class)
    public JAXBElement<XMLGregorianCalendar> createSexChangeDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_GpTerritorialChangeDate_QNAME, XMLGregorianCalendar.class, Sex.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "changeDate", scope = Country.class)
    public JAXBElement<XMLGregorianCalendar> createCountryChangeDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_GpTerritorialChangeDate_QNAME, XMLGregorianCalendar.class, Country.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "changeDate", scope = DocumentType.class)
    public JAXBElement<XMLGregorianCalendar> createDocumentTypeChangeDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_GpTerritorialChangeDate_QNAME, XMLGregorianCalendar.class, DocumentType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "changeDate", scope = District.class)
    public JAXBElement<XMLGregorianCalendar> createDistrictChangeDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_GpTerritorialChangeDate_QNAME, XMLGregorianCalendar.class, District.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "changeDate", scope = DocumentInvalidity.class)
    public JAXBElement<XMLGregorianCalendar> createDocumentInvalidityChangeDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_GpTerritorialChangeDate_QNAME, XMLGregorianCalendar.class, DocumentInvalidity.class, value);
    }

}
