package kz.bee.bip.asyncchannel.v10.interfaces.client.binding;

import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;
import kz.bee.bip.asyncchannel.v10.interfaces.client.IAsyncChannelClient;

/**
 * This class was generated by Apache CXF 2.7.7.redhat-1
 * 2014-09-02T16:37:26.273+06:00
 * Generated source version: 2.7.7.redhat-1
 * 
 */
@WebServiceClient(name = "AsyncChannelClientHttpService", wsdlLocation = "AsyncChannelClientHttp_Service.wsdl", targetNamespace = "http://bip.bee.kz/AsyncChannel/v10/Interfaces/Client/Binding")
public class AsyncChannelClientHttpService extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://bip.bee.kz/AsyncChannel/v10/Interfaces/Client/Binding",
            "AsyncChannelClientHttpService");
    public final static QName AsyncChannelClientHttpPort = new QName(
            "http://bip.bee.kz/AsyncChannel/v10/Interfaces/Client/Binding", "AsyncChannelClientHttpPort");
    static {
        URL url = AsyncChannelClientHttpService.class.getResource("AsyncChannelClientHttp_Service.wsdl");
        if (url == null) {
            url = AsyncChannelClientHttpService.class.getClassLoader().getResource("AsyncChannelClientHttp_Service.wsdl");
        }
        if (url == null) {
            java.util.logging.Logger.getLogger(AsyncChannelClientHttpService.class.getName()).log(java.util.logging.Level.INFO,
                    "Can not initialize the default wsdl from {0}", "AsyncChannelClientHttp_Service.wsdl");
        }
        WSDL_LOCATION = url;
    }

    public AsyncChannelClientHttpService(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public AsyncChannelClientHttpService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public AsyncChannelClientHttpService() {
        super(WSDL_LOCATION, SERVICE);
    }

    /**
     * 
     * @return
     *         returns IAsyncChannelClient
     */
    @WebEndpoint(name = "AsyncChannelClientHttpPort")
    public IAsyncChannelClient getAsyncChannelClientHttpPort() {
        return super.getPort(AsyncChannelClientHttpPort, IAsyncChannelClient.class);
    }

    /**
     * 
     * @param features
     *            A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy. Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *         returns IAsyncChannelClient
     */
    @WebEndpoint(name = "AsyncChannelClientHttpPort")
    public IAsyncChannelClient getAsyncChannelClientHttpPort(WebServiceFeature... features) {
        return super.getPort(AsyncChannelClientHttpPort, IAsyncChannelClient.class, features);
    }
}