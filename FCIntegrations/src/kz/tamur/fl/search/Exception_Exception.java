
package kz.tamur.fl.search;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 2.4.6
 * 2018-10-02T17:48:57.836+06:00
 * Generated source version: 2.4.6
 */

@WebFault(name = "Exception", targetNamespace = "http://webservice.request.universal.interactive.nat")
public class Exception_Exception extends java.lang.Exception {
    
    private kz.tamur.fl.search.Exception exception;

    public Exception_Exception() {
        super();
    }
    
    public Exception_Exception(String message) {
        super(message);
    }
    
    public Exception_Exception(String message, Throwable cause) {
        super(message, cause);
    }

    public Exception_Exception(String message, kz.tamur.fl.search.Exception exception) {
        super(message);
        this.exception = exception;
    }

    public Exception_Exception(String message, kz.tamur.fl.search.Exception exception, Throwable cause) {
        super(message, cause);
        this.exception = exception;
    }

    public kz.tamur.fl.search.Exception getFaultInfo() {
        return this.exception;
    }
}
