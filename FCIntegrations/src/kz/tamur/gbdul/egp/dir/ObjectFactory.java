
package kz.tamur.gbdul.egp.dir;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the kz.tamur.gbdul.egp.dir package. 
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

    private final static QName _GetDirectoryResponse_QNAME = new QName("http://dir.egp.gbdul.tamur.kz/", "getDirectoryResponse");
    private final static QName _GetNodeByCode_QNAME = new QName("http://dir.egp.gbdul.tamur.kz/", "getNodeByCode");
    private final static QName _GetRecordsByName_QNAME = new QName("http://dir.egp.gbdul.tamur.kz/", "getRecordsByName");
    private final static QName _GetRecordsByNameResponse_QNAME = new QName("http://dir.egp.gbdul.tamur.kz/", "getRecordsByNameResponse");
    private final static QName _GetRegOrgByKatoResponse_QNAME = new QName("http://dir.egp.gbdul.tamur.kz/", "getRegOrgByKatoResponse");
    private final static QName _GetDirectories_QNAME = new QName("http://dir.egp.gbdul.tamur.kz/", "getDirectories");
    private final static QName _GetDirectoriesResponse_QNAME = new QName("http://dir.egp.gbdul.tamur.kz/", "getDirectoriesResponse");
    private final static QName _GetLevelContent_QNAME = new QName("http://dir.egp.gbdul.tamur.kz/", "getLevelContent");
    private final static QName _GetRegOrgByKato_QNAME = new QName("http://dir.egp.gbdul.tamur.kz/", "getRegOrgByKato");
    private final static QName _GetNodeByCodeResponse_QNAME = new QName("http://dir.egp.gbdul.tamur.kz/", "getNodeByCodeResponse");
    private final static QName _GetDirectory_QNAME = new QName("http://dir.egp.gbdul.tamur.kz/", "getDirectory");
    private final static QName _GetLevelContentResponse_QNAME = new QName("http://dir.egp.gbdul.tamur.kz/", "getLevelContentResponse");
    private final static QName _GetRecordByIdResponse_QNAME = new QName("http://dir.egp.gbdul.tamur.kz/", "getRecordByIdResponse");
    private final static QName _GetRecordById_QNAME = new QName("http://dir.egp.gbdul.tamur.kz/", "getRecordById");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: kz.tamur.gbdul.egp.dir
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link LevelContentResponse }
     * 
     */
    public LevelContentResponse createLevelContentResponse() {
        return new LevelContentResponse();
    }

    /**
     * Create an instance of {@link NodeByCodeRequest }
     * 
     */
    public NodeByCodeRequest createNodeByCodeRequest() {
        return new NodeByCodeRequest();
    }

    /**
     * Create an instance of {@link GetNodeByCode }
     * 
     */
    public GetNodeByCode createGetNodeByCode() {
        return new GetNodeByCode();
    }

    /**
     * Create an instance of {@link DirectoryResponse }
     * 
     */
    public DirectoryResponse createDirectoryResponse() {
        return new DirectoryResponse();
    }

    /**
     * Create an instance of {@link GetDirectory }
     * 
     */
    public GetDirectory createGetDirectory() {
        return new GetDirectory();
    }

    /**
     * Create an instance of {@link RecordsByNameResponse }
     * 
     */
    public RecordsByNameResponse createRecordsByNameResponse() {
        return new RecordsByNameResponse();
    }

    /**
     * Create an instance of {@link NodeByCodeResponse }
     * 
     */
    public NodeByCodeResponse createNodeByCodeResponse() {
        return new NodeByCodeResponse();
    }

    /**
     * Create an instance of {@link GetLevelContentResponse }
     * 
     */
    public GetLevelContentResponse createGetLevelContentResponse() {
        return new GetLevelContentResponse();
    }

    /**
     * Create an instance of {@link GetRegOrgByKatoResponse }
     * 
     */
    public GetRegOrgByKatoResponse createGetRegOrgByKatoResponse() {
        return new GetRegOrgByKatoResponse();
    }

    /**
     * Create an instance of {@link RecordsByNameRequest }
     * 
     */
    public RecordsByNameRequest createRecordsByNameRequest() {
        return new RecordsByNameRequest();
    }

    /**
     * Create an instance of {@link GetRegOrgByKato }
     * 
     */
    public GetRegOrgByKato createGetRegOrgByKato() {
        return new GetRegOrgByKato();
    }

    /**
     * Create an instance of {@link RegOrgByKatoRequest }
     * 
     */
    public RegOrgByKatoRequest createRegOrgByKatoRequest() {
        return new RegOrgByKatoRequest();
    }

    /**
     * Create an instance of {@link RegOrgByKatoResponse }
     * 
     */
    public RegOrgByKatoResponse createRegOrgByKatoResponse() {
        return new RegOrgByKatoResponse();
    }

    /**
     * Create an instance of {@link GetLevelContent }
     * 
     */
    public GetLevelContent createGetLevelContent() {
        return new GetLevelContent();
    }

    /**
     * Create an instance of {@link GetDirectoryResponse }
     * 
     */
    public GetDirectoryResponse createGetDirectoryResponse() {
        return new GetDirectoryResponse();
    }

    /**
     * Create an instance of {@link Record }
     * 
     */
    public Record createRecord() {
        return new Record();
    }

    /**
     * Create an instance of {@link Directory }
     * 
     */
    public Directory createDirectory() {
        return new Directory();
    }

    /**
     * Create an instance of {@link GetDirectories }
     * 
     */
    public GetDirectories createGetDirectories() {
        return new GetDirectories();
    }

    /**
     * Create an instance of {@link RecordByIdRequest }
     * 
     */
    public RecordByIdRequest createRecordByIdRequest() {
        return new RecordByIdRequest();
    }

    /**
     * Create an instance of {@link DirectoriesInfoRequest }
     * 
     */
    public DirectoriesInfoRequest createDirectoriesInfoRequest() {
        return new DirectoriesInfoRequest();
    }

    /**
     * Create an instance of {@link LevelContentRequest }
     * 
     */
    public LevelContentRequest createLevelContentRequest() {
        return new LevelContentRequest();
    }

    /**
     * Create an instance of {@link GetDirectoriesResponse }
     * 
     */
    public GetDirectoriesResponse createGetDirectoriesResponse() {
        return new GetDirectoriesResponse();
    }

    /**
     * Create an instance of {@link DirectoriesInfoResponse }
     * 
     */
    public DirectoriesInfoResponse createDirectoriesInfoResponse() {
        return new DirectoriesInfoResponse();
    }

    /**
     * Create an instance of {@link GetNodeByCodeResponse }
     * 
     */
    public GetNodeByCodeResponse createGetNodeByCodeResponse() {
        return new GetNodeByCodeResponse();
    }

    /**
     * Create an instance of {@link GetRecordByIdResponse }
     * 
     */
    public GetRecordByIdResponse createGetRecordByIdResponse() {
        return new GetRecordByIdResponse();
    }

    /**
     * Create an instance of {@link RecordByIdResponse }
     * 
     */
    public RecordByIdResponse createRecordByIdResponse() {
        return new RecordByIdResponse();
    }

    /**
     * Create an instance of {@link GetRecordsByName }
     * 
     */
    public GetRecordsByName createGetRecordsByName() {
        return new GetRecordsByName();
    }

    /**
     * Create an instance of {@link DirectoryRequest }
     * 
     */
    public DirectoryRequest createDirectoryRequest() {
        return new DirectoryRequest();
    }

    /**
     * Create an instance of {@link GetRecordById }
     * 
     */
    public GetRecordById createGetRecordById() {
        return new GetRecordById();
    }

    /**
     * Create an instance of {@link GetRecordsByNameResponse }
     * 
     */
    public GetRecordsByNameResponse createGetRecordsByNameResponse() {
        return new GetRecordsByNameResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDirectoryResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dir.egp.gbdul.tamur.kz/", name = "getDirectoryResponse")
    public JAXBElement<GetDirectoryResponse> createGetDirectoryResponse(GetDirectoryResponse value) {
        return new JAXBElement<GetDirectoryResponse>(_GetDirectoryResponse_QNAME, GetDirectoryResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetNodeByCode }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dir.egp.gbdul.tamur.kz/", name = "getNodeByCode")
    public JAXBElement<GetNodeByCode> createGetNodeByCode(GetNodeByCode value) {
        return new JAXBElement<GetNodeByCode>(_GetNodeByCode_QNAME, GetNodeByCode.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRecordsByName }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dir.egp.gbdul.tamur.kz/", name = "getRecordsByName")
    public JAXBElement<GetRecordsByName> createGetRecordsByName(GetRecordsByName value) {
        return new JAXBElement<GetRecordsByName>(_GetRecordsByName_QNAME, GetRecordsByName.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRecordsByNameResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dir.egp.gbdul.tamur.kz/", name = "getRecordsByNameResponse")
    public JAXBElement<GetRecordsByNameResponse> createGetRecordsByNameResponse(GetRecordsByNameResponse value) {
        return new JAXBElement<GetRecordsByNameResponse>(_GetRecordsByNameResponse_QNAME, GetRecordsByNameResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRegOrgByKatoResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dir.egp.gbdul.tamur.kz/", name = "getRegOrgByKatoResponse")
    public JAXBElement<GetRegOrgByKatoResponse> createGetRegOrgByKatoResponse(GetRegOrgByKatoResponse value) {
        return new JAXBElement<GetRegOrgByKatoResponse>(_GetRegOrgByKatoResponse_QNAME, GetRegOrgByKatoResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDirectories }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dir.egp.gbdul.tamur.kz/", name = "getDirectories")
    public JAXBElement<GetDirectories> createGetDirectories(GetDirectories value) {
        return new JAXBElement<GetDirectories>(_GetDirectories_QNAME, GetDirectories.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDirectoriesResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dir.egp.gbdul.tamur.kz/", name = "getDirectoriesResponse")
    public JAXBElement<GetDirectoriesResponse> createGetDirectoriesResponse(GetDirectoriesResponse value) {
        return new JAXBElement<GetDirectoriesResponse>(_GetDirectoriesResponse_QNAME, GetDirectoriesResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetLevelContent }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dir.egp.gbdul.tamur.kz/", name = "getLevelContent")
    public JAXBElement<GetLevelContent> createGetLevelContent(GetLevelContent value) {
        return new JAXBElement<GetLevelContent>(_GetLevelContent_QNAME, GetLevelContent.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRegOrgByKato }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dir.egp.gbdul.tamur.kz/", name = "getRegOrgByKato")
    public JAXBElement<GetRegOrgByKato> createGetRegOrgByKato(GetRegOrgByKato value) {
        return new JAXBElement<GetRegOrgByKato>(_GetRegOrgByKato_QNAME, GetRegOrgByKato.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetNodeByCodeResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dir.egp.gbdul.tamur.kz/", name = "getNodeByCodeResponse")
    public JAXBElement<GetNodeByCodeResponse> createGetNodeByCodeResponse(GetNodeByCodeResponse value) {
        return new JAXBElement<GetNodeByCodeResponse>(_GetNodeByCodeResponse_QNAME, GetNodeByCodeResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDirectory }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dir.egp.gbdul.tamur.kz/", name = "getDirectory")
    public JAXBElement<GetDirectory> createGetDirectory(GetDirectory value) {
        return new JAXBElement<GetDirectory>(_GetDirectory_QNAME, GetDirectory.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetLevelContentResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dir.egp.gbdul.tamur.kz/", name = "getLevelContentResponse")
    public JAXBElement<GetLevelContentResponse> createGetLevelContentResponse(GetLevelContentResponse value) {
        return new JAXBElement<GetLevelContentResponse>(_GetLevelContentResponse_QNAME, GetLevelContentResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRecordByIdResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dir.egp.gbdul.tamur.kz/", name = "getRecordByIdResponse")
    public JAXBElement<GetRecordByIdResponse> createGetRecordByIdResponse(GetRecordByIdResponse value) {
        return new JAXBElement<GetRecordByIdResponse>(_GetRecordByIdResponse_QNAME, GetRecordByIdResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRecordById }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://dir.egp.gbdul.tamur.kz/", name = "getRecordById")
    public JAXBElement<GetRecordById> createGetRecordById(GetRecordById value) {
        return new JAXBElement<GetRecordById>(_GetRecordById_QNAME, GetRecordById.class, null, value);
    }

}
