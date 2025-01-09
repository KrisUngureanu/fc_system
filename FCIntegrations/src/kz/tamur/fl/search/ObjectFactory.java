
package kz.tamur.fl.search;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the kz.tamur.fl.search package. 
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

    private final static QName _FindPersonByFioResponse_QNAME = new QName("http://webservice.request.universal.interactive.nat", "findPersonByFioResponse");
    private final static QName _FindPerson_QNAME = new QName("http://webservice.request.universal.interactive.nat", "findPerson");
    private final static QName _RequestIin_QNAME = new QName("http://webservice.request.universal.interactive.nat", "requestIin");
    private final static QName _FindPersonByDocResponse_QNAME = new QName("http://webservice.request.universal.interactive.nat", "findPersonByDocResponse");
    private final static QName _FindPersonByIin_QNAME = new QName("http://webservice.request.universal.interactive.nat", "findPersonByIin");
    private final static QName _Exception_QNAME = new QName("http://webservice.request.universal.interactive.nat", "Exception");
    private final static QName _FindPersonByFio_QNAME = new QName("http://webservice.request.universal.interactive.nat", "findPersonByFio");
    private final static QName _FindPersonByIinResponse_QNAME = new QName("http://webservice.request.universal.interactive.nat", "findPersonByIinResponse");
    private final static QName _FindPersonResponse_QNAME = new QName("http://webservice.request.universal.interactive.nat", "findPersonResponse");
    private final static QName _FindPersonByDoc_QNAME = new QName("http://webservice.request.universal.interactive.nat", "findPersonByDoc");
    private final static QName _RequestDoc_QNAME = new QName("http://webservice.request.universal.interactive.nat", "requestDoc");
    private final static QName _Response_QNAME = new QName("http://webservice.request.universal.interactive.nat", "response");
    private final static QName _Request_QNAME = new QName("http://webservice.request.universal.interactive.nat", "request");
    private final static QName _RequestSearchFio_QNAME = new QName("http://webservice.request.universal.interactive.nat", "requestSearchFio");
    private final static QName _DocumentOrganizationChangeDate_QNAME = new QName("", "changeDate");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: kz.tamur.fl.search
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Response }
     * 
     */
    public Response createResponse() {
        return new Response();
    }

    /**
     * Create an instance of {@link Person }
     * 
     */
    public Person createPerson() {
        return new Person();
    }

    /**
     * Create an instance of {@link CapableStatus }
     * 
     */
    public CapableStatus createCapableStatus() {
        return new CapableStatus();
    }

    /**
     * Create an instance of {@link GpTerritorial }
     * 
     */
    public GpTerritorial createGpTerritorial() {
        return new GpTerritorial();
    }

    /**
     * Create an instance of {@link Participant }
     * 
     */
    public Participant createParticipant() {
        return new Participant();
    }

    /**
     * Create an instance of {@link MessageResult }
     * 
     */
    public MessageResult createMessageResult() {
        return new MessageResult();
    }

    /**
     * Create an instance of {@link DocumentInvalidity }
     * 
     */
    public DocumentInvalidity createDocumentInvalidity() {
        return new DocumentInvalidity();
    }

    /**
     * Create an instance of {@link Country }
     * 
     */
    public Country createCountry() {
        return new Country();
    }

    /**
     * Create an instance of {@link Nationality }
     * 
     */
    public Nationality createNationality() {
        return new Nationality();
    }

    /**
     * Create an instance of {@link Region }
     * 
     */
    public Region createRegion() {
        return new Region();
    }

    /**
     * Create an instance of {@link District }
     * 
     */
    public District createDistrict() {
        return new District();
    }

    /**
     * Create an instance of {@link ExcludeReason }
     * 
     */
    public ExcludeReason createExcludeReason() {
        return new ExcludeReason();
    }

    /**
     * Create an instance of {@link PersonStatus }
     * 
     */
    public PersonStatus createPersonStatus() {
        return new PersonStatus();
    }

    /**
     * Create an instance of {@link AddressType }
     * 
     */
    public AddressType createAddressType() {
        return new AddressType();
    }

    /**
     * Create an instance of {@link DocumentType }
     * 
     */
    public DocumentType createDocumentType() {
        return new DocumentType();
    }

    /**
     * Create an instance of {@link Court }
     * 
     */
    public Court createCourt() {
        return new Court();
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
     * Create an instance of {@link DisappearStatus }
     * 
     */
    public DisappearStatus createDisappearStatus() {
        return new DisappearStatus();
    }

    /**
     * Create an instance of {@link PersonCapableStatus }
     * 
     */
    public PersonCapableStatus createPersonCapableStatus() {
        return new PersonCapableStatus();
    }

    /**
     * Create an instance of {@link PersonExcludeStatus }
     * 
     */
    public PersonExcludeStatus createPersonExcludeStatus() {
        return new PersonExcludeStatus();
    }

    /**
     * Create an instance of {@link ForeignData }
     * 
     */
    public ForeignData createForeignData() {
        return new ForeignData();
    }

    /**
     * Create an instance of {@link BirthPlace }
     * 
     */
    public BirthPlace createBirthPlace() {
        return new BirthPlace();
    }

    /**
     * Create an instance of {@link Fio }
     * 
     */
    public Fio createFio() {
        return new Fio();
    }

    /**
     * Create an instance of {@link Address }
     * 
     */
    public Address createAddress() {
        return new Address();
    }

    /**
     * Create an instance of {@link MissingStatus }
     * 
     */
    public MissingStatus createMissingStatus() {
        return new MissingStatus();
    }

    /**
     * Create an instance of {@link AddDocs }
     * 
     */
    public AddDocs createAddDocs() {
        return new AddDocs();
    }

    /**
     * Create an instance of {@link RegAddress }
     * 
     */
    public RegAddress createRegAddress() {
        return new RegAddress();
    }

    /**
     * Create an instance of {@link AbsentStatus }
     * 
     */
    public AbsentStatus createAbsentStatus() {
        return new AbsentStatus();
    }

    /**
     * Create an instance of {@link Exception }
     * 
     */
    public Exception createException() {
        return new Exception();
    }

    /**
     * Create an instance of {@link FindPersonByFio }
     * 
     */
    public FindPersonByFio createFindPersonByFio() {
        return new FindPersonByFio();
    }

    /**
     * Create an instance of {@link FindPersonResponse }
     * 
     */
    public FindPersonResponse createFindPersonResponse() {
        return new FindPersonResponse();
    }

    /**
     * Create an instance of {@link FindPersonByIinResponse }
     * 
     */
    public FindPersonByIinResponse createFindPersonByIinResponse() {
        return new FindPersonByIinResponse();
    }

    /**
     * Create an instance of {@link FindPerson }
     * 
     */
    public FindPerson createFindPerson() {
        return new FindPerson();
    }

    /**
     * Create an instance of {@link RequestIin }
     * 
     */
    public RequestIin createRequestIin() {
        return new RequestIin();
    }

    /**
     * Create an instance of {@link FindPersonByDocResponse }
     * 
     */
    public FindPersonByDocResponse createFindPersonByDocResponse() {
        return new FindPersonByDocResponse();
    }

    /**
     * Create an instance of {@link FindPersonByIin }
     * 
     */
    public FindPersonByIin createFindPersonByIin() {
        return new FindPersonByIin();
    }

    /**
     * Create an instance of {@link FindPersonByFioResponse }
     * 
     */
    public FindPersonByFioResponse createFindPersonByFioResponse() {
        return new FindPersonByFioResponse();
    }

    /**
     * Create an instance of {@link Request }
     * 
     */
    public Request createRequest() {
        return new Request();
    }

    /**
     * Create an instance of {@link RequestSearchFio }
     * 
     */
    public RequestSearchFio createRequestSearchFio() {
        return new RequestSearchFio();
    }

    /**
     * Create an instance of {@link FindPersonByDoc }
     * 
     */
    public FindPersonByDoc createFindPersonByDoc() {
        return new FindPersonByDoc();
    }

    /**
     * Create an instance of {@link RequestDoc }
     * 
     */
    public RequestDoc createRequestDoc() {
        return new RequestDoc();
    }

    /**
     * Create an instance of {@link Document }
     * 
     */
    public Document createDocument() {
        return new Document();
    }

    /**
     * Create an instance of {@link FrontierCrossing }
     * 
     */
    public FrontierCrossing createFrontierCrossing() {
        return new FrontierCrossing();
    }

    /**
     * Create an instance of {@link Response.Persons }
     * 
     */
    public Response.Persons createResponsePersons() {
        return new Response.Persons();
    }

    /**
     * Create an instance of {@link Person.FrontierCrossings }
     * 
     */
    public Person.FrontierCrossings createPersonFrontierCrossings() {
        return new Person.FrontierCrossings();
    }

    /**
     * Create an instance of {@link Person.Documents }
     * 
     */
    public Person.Documents createPersonDocuments() {
        return new Person.Documents();
    }

    /**
     * Create an instance of {@link Person.Addresses }
     * 
     */
    public Person.Addresses createPersonAddresses() {
        return new Person.Addresses();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindPersonByFioResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.request.universal.interactive.nat", name = "findPersonByFioResponse")
    public JAXBElement<FindPersonByFioResponse> createFindPersonByFioResponse(FindPersonByFioResponse value) {
        return new JAXBElement<FindPersonByFioResponse>(_FindPersonByFioResponse_QNAME, FindPersonByFioResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindPerson }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.request.universal.interactive.nat", name = "findPerson")
    public JAXBElement<FindPerson> createFindPerson(FindPerson value) {
        return new JAXBElement<FindPerson>(_FindPerson_QNAME, FindPerson.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RequestIin }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.request.universal.interactive.nat", name = "requestIin")
    public JAXBElement<RequestIin> createRequestIin(RequestIin value) {
        return new JAXBElement<RequestIin>(_RequestIin_QNAME, RequestIin.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindPersonByDocResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.request.universal.interactive.nat", name = "findPersonByDocResponse")
    public JAXBElement<FindPersonByDocResponse> createFindPersonByDocResponse(FindPersonByDocResponse value) {
        return new JAXBElement<FindPersonByDocResponse>(_FindPersonByDocResponse_QNAME, FindPersonByDocResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindPersonByIin }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.request.universal.interactive.nat", name = "findPersonByIin")
    public JAXBElement<FindPersonByIin> createFindPersonByIin(FindPersonByIin value) {
        return new JAXBElement<FindPersonByIin>(_FindPersonByIin_QNAME, FindPersonByIin.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Exception }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.request.universal.interactive.nat", name = "Exception")
    public JAXBElement<Exception> createException(Exception value) {
        return new JAXBElement<Exception>(_Exception_QNAME, Exception.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindPersonByFio }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.request.universal.interactive.nat", name = "findPersonByFio")
    public JAXBElement<FindPersonByFio> createFindPersonByFio(FindPersonByFio value) {
        return new JAXBElement<FindPersonByFio>(_FindPersonByFio_QNAME, FindPersonByFio.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindPersonByIinResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.request.universal.interactive.nat", name = "findPersonByIinResponse")
    public JAXBElement<FindPersonByIinResponse> createFindPersonByIinResponse(FindPersonByIinResponse value) {
        return new JAXBElement<FindPersonByIinResponse>(_FindPersonByIinResponse_QNAME, FindPersonByIinResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindPersonResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.request.universal.interactive.nat", name = "findPersonResponse")
    public JAXBElement<FindPersonResponse> createFindPersonResponse(FindPersonResponse value) {
        return new JAXBElement<FindPersonResponse>(_FindPersonResponse_QNAME, FindPersonResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindPersonByDoc }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.request.universal.interactive.nat", name = "findPersonByDoc")
    public JAXBElement<FindPersonByDoc> createFindPersonByDoc(FindPersonByDoc value) {
        return new JAXBElement<FindPersonByDoc>(_FindPersonByDoc_QNAME, FindPersonByDoc.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RequestDoc }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.request.universal.interactive.nat", name = "requestDoc")
    public JAXBElement<RequestDoc> createRequestDoc(RequestDoc value) {
        return new JAXBElement<RequestDoc>(_RequestDoc_QNAME, RequestDoc.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Response }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.request.universal.interactive.nat", name = "response")
    public JAXBElement<Response> createResponse(Response value) {
        return new JAXBElement<Response>(_Response_QNAME, Response.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Request }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.request.universal.interactive.nat", name = "request")
    public JAXBElement<Request> createRequest(Request value) {
        return new JAXBElement<Request>(_Request_QNAME, Request.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RequestSearchFio }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.request.universal.interactive.nat", name = "requestSearchFio")
    public JAXBElement<RequestSearchFio> createRequestSearchFio(RequestSearchFio value) {
        return new JAXBElement<RequestSearchFio>(_RequestSearchFio_QNAME, RequestSearchFio.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "changeDate", scope = DocumentOrganization.class)
    public JAXBElement<XMLGregorianCalendar> createDocumentOrganizationChangeDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_DocumentOrganizationChangeDate_QNAME, XMLGregorianCalendar.class, DocumentOrganization.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "changeDate", scope = Participant.class)
    public JAXBElement<XMLGregorianCalendar> createParticipantChangeDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_DocumentOrganizationChangeDate_QNAME, XMLGregorianCalendar.class, Participant.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "changeDate", scope = MessageResult.class)
    public JAXBElement<XMLGregorianCalendar> createMessageResultChangeDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_DocumentOrganizationChangeDate_QNAME, XMLGregorianCalendar.class, MessageResult.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "changeDate", scope = Nationality.class)
    public JAXBElement<XMLGregorianCalendar> createNationalityChangeDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_DocumentOrganizationChangeDate_QNAME, XMLGregorianCalendar.class, Nationality.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "changeDate", scope = ExcludeReason.class)
    public JAXBElement<XMLGregorianCalendar> createExcludeReasonChangeDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_DocumentOrganizationChangeDate_QNAME, XMLGregorianCalendar.class, ExcludeReason.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "changeDate", scope = Court.class)
    public JAXBElement<XMLGregorianCalendar> createCourtChangeDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_DocumentOrganizationChangeDate_QNAME, XMLGregorianCalendar.class, Court.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "changeDate", scope = Sex.class)
    public JAXBElement<XMLGregorianCalendar> createSexChangeDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_DocumentOrganizationChangeDate_QNAME, XMLGregorianCalendar.class, Sex.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "changeDate", scope = GpTerritorial.class)
    public JAXBElement<XMLGregorianCalendar> createGpTerritorialChangeDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_DocumentOrganizationChangeDate_QNAME, XMLGregorianCalendar.class, GpTerritorial.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "changeDate", scope = AddressType.class)
    public JAXBElement<XMLGregorianCalendar> createAddressTypeChangeDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_DocumentOrganizationChangeDate_QNAME, XMLGregorianCalendar.class, AddressType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "changeDate", scope = Region.class)
    public JAXBElement<XMLGregorianCalendar> createRegionChangeDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_DocumentOrganizationChangeDate_QNAME, XMLGregorianCalendar.class, Region.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "changeDate", scope = Country.class)
    public JAXBElement<XMLGregorianCalendar> createCountryChangeDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_DocumentOrganizationChangeDate_QNAME, XMLGregorianCalendar.class, Country.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "changeDate", scope = DocumentInvalidity.class)
    public JAXBElement<XMLGregorianCalendar> createDocumentInvalidityChangeDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_DocumentOrganizationChangeDate_QNAME, XMLGregorianCalendar.class, DocumentInvalidity.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "changeDate", scope = DocumentType.class)
    public JAXBElement<XMLGregorianCalendar> createDocumentTypeChangeDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_DocumentOrganizationChangeDate_QNAME, XMLGregorianCalendar.class, DocumentType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "changeDate", scope = CapableStatus.class)
    public JAXBElement<XMLGregorianCalendar> createCapableStatusChangeDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_DocumentOrganizationChangeDate_QNAME, XMLGregorianCalendar.class, CapableStatus.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "changeDate", scope = District.class)
    public JAXBElement<XMLGregorianCalendar> createDistrictChangeDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_DocumentOrganizationChangeDate_QNAME, XMLGregorianCalendar.class, District.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "changeDate", scope = PersonStatus.class)
    public JAXBElement<XMLGregorianCalendar> createPersonStatusChangeDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_DocumentOrganizationChangeDate_QNAME, XMLGregorianCalendar.class, PersonStatus.class, value);
    }

}
