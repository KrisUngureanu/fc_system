
package kz.tamur.or3.mtszn.systeminfo;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the kz.tamur.or3.mtszn.systeminfo package. 
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

    private final static QName _Sender_QNAME = new QName("http://v6.systeminfo.services.shep.nitec.kz", "Sender");
    private final static QName _MessageKz_QNAME = new QName("http://v6.systeminfo.services.shep.nitec.kz", "MessageKz");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: kz.tamur.or3.mtszn.systeminfo
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://v6.systeminfo.services.shep.nitec.kz", name = "Sender")
    public JAXBElement<String> createSender(String value) {
        return new JAXBElement<String>(_Sender_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://v6.systeminfo.services.shep.nitec.kz", name = "MessageKz")
    public JAXBElement<String> createMessageKz(String value) {
        return new JAXBElement<String>(_MessageKz_QNAME, String.class, null, value);
    }

}
