
package kz.tamur.or3.mtszn.bas;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the kz.tamur.or3.mtszn.bas package. 
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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: kz.tamur.or3.mtszn.bas
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link IdentityDocument }
     * 
     */
    public IdentityDocument createIdentityDocument() {
        return new IdentityDocument();
    }

    /**
     * Create an instance of {@link SsifPartDocResponse }
     * 
     */
    public SsifPartDocResponse createSsifPartDocResponse() {
        return new SsifPartDocResponse();
    }

    /**
     * Create an instance of {@link SsifPartDocRequest }
     * 
     */
    public SsifPartDocRequest createSsifPartDocRequest() {
        return new SsifPartDocRequest();
    }

    /**
     * Create an instance of {@link P06563Response }
     * 
     */
    public P06563Response createP06563Response() {
        return new P06563Response();
    }

    /**
     * Create an instance of {@link P06563Request }
     * 
     */
    public P06563Request createP06563Request() {
        return new P06563Request();
    }

}
