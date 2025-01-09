
package kz.tamur.or3.mtszn.epay;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the kz.tamur.or3.mtszn.epay package. 
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


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: kz.tamur.or3.mtszn.epay
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link PEPIBANCheckResponse }
     * 
     */
    public PEPIBANCheckResponse createPEPIBANCheckResponse() {
        return new PEPIBANCheckResponse();
    }

    /**
     * Create an instance of {@link PEPIBANCheckRequest }
     * 
     */
    public PEPIBANCheckRequest createPEPIBANCheckRequest() {
        return new PEPIBANCheckRequest();
    }

}
