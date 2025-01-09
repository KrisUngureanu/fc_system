//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2022.11.23 at 04:01:18 PM ALMT 
//


package kz.tamur.fc.el.licensesearch;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the kz.tamur.fc.el.licensesearch package. 
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

    private final static QName _UniversalLicenseResponse_QNAME = new QName("http://integration.elicense.kz/CustomServices/Egov/EgovLicenseSearchService", "UniversalLicenseResponse");
    private final static QName _UniversalLicenseRequest_QNAME = new QName("http://integration.elicense.kz/CustomServices/Egov/EgovLicenseSearchService", "UniversalLicenseRequest");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: kz.tamur.fc.el.licensesearch
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link UniversalLicenseResponse }
     * 
     */
    public UniversalLicenseResponse createUniversalLicenseResponse() {
        return new UniversalLicenseResponse();
    }

    /**
     * Create an instance of {@link UniversalLicenseRequest }
     * 
     */
    public UniversalLicenseRequest createUniversalLicenseRequest() {
        return new UniversalLicenseRequest();
    }

    /**
     * Create an instance of {@link ArrayOfTaxpayerLicense }
     * 
     */
    public ArrayOfTaxpayerLicense createArrayOfTaxpayerLicense() {
        return new ArrayOfTaxpayerLicense();
    }

    /**
     * Create an instance of {@link TaxpayerLicenseRequest }
     * 
     */
    public TaxpayerLicenseRequest createTaxpayerLicenseRequest() {
        return new TaxpayerLicenseRequest();
    }

    /**
     * Create an instance of {@link EgovCabinetLicense }
     * 
     */
    public EgovCabinetLicense createEgovCabinetLicense() {
        return new EgovCabinetLicense();
    }

    /**
     * Create an instance of {@link TaxpayerLicenseResponse }
     * 
     */
    public TaxpayerLicenseResponse createTaxpayerLicenseResponse() {
        return new TaxpayerLicenseResponse();
    }

    /**
     * Create an instance of {@link RequestBase }
     * 
     */
    public RequestBase createRequestBase() {
        return new RequestBase();
    }

    /**
     * Create an instance of {@link EgovCabinetLicenseRequest }
     * 
     */
    public EgovCabinetLicenseRequest createEgovCabinetLicenseRequest() {
        return new EgovCabinetLicenseRequest();
    }

    /**
     * Create an instance of {@link SystemInfo }
     * 
     */
    public SystemInfo createSystemInfo() {
        return new SystemInfo();
    }

    /**
     * Create an instance of {@link UniversalLicense }
     * 
     */
    public UniversalLicense createUniversalLicense() {
        return new UniversalLicense();
    }

    /**
     * Create an instance of {@link ArrayOfEgovCabinetLicense }
     * 
     */
    public ArrayOfEgovCabinetLicense createArrayOfEgovCabinetLicense() {
        return new ArrayOfEgovCabinetLicense();
    }

    /**
     * Create an instance of {@link EgovCabinetLicenseResponse }
     * 
     */
    public EgovCabinetLicenseResponse createEgovCabinetLicenseResponse() {
        return new EgovCabinetLicenseResponse();
    }

    /**
     * Create an instance of {@link DictionaryRecord }
     * 
     */
    public DictionaryRecord createDictionaryRecord() {
        return new DictionaryRecord();
    }

    /**
     * Create an instance of {@link ArrayOfUniversalLicense }
     * 
     */
    public ArrayOfUniversalLicense createArrayOfUniversalLicense() {
        return new ArrayOfUniversalLicense();
    }

    /**
     * Create an instance of {@link RequestPageBase }
     * 
     */
    public RequestPageBase createRequestPageBase() {
        return new RequestPageBase();
    }

    /**
     * Create an instance of {@link TaxpayerLicense }
     * 
     */
    public TaxpayerLicense createTaxpayerLicense() {
        return new TaxpayerLicense();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UniversalLicenseResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://integration.elicense.kz/CustomServices/Egov/EgovLicenseSearchService", name = "UniversalLicenseResponse")
    public JAXBElement<UniversalLicenseResponse> createUniversalLicenseResponse(UniversalLicenseResponse value) {
        return new JAXBElement<UniversalLicenseResponse>(_UniversalLicenseResponse_QNAME, UniversalLicenseResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UniversalLicenseRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://integration.elicense.kz/CustomServices/Egov/EgovLicenseSearchService", name = "UniversalLicenseRequest")
    public JAXBElement<UniversalLicenseRequest> createUniversalLicenseRequest(UniversalLicenseRequest value) {
        return new JAXBElement<UniversalLicenseRequest>(_UniversalLicenseRequest_QNAME, UniversalLicenseRequest.class, null, value);
    }

}
