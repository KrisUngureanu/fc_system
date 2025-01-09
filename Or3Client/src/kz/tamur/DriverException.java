package kz.tamur;


/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 04.12.2005
 * Time: 14:49:22
 * To change this template use File | Settings | File Templates.
 */
public class DriverException extends OrException {
    public DriverException(Throwable cause) {
        super(cause);
    }

    public DriverException(String message) {
        super(message);
    }
    public DriverException(String message,int code) {
        super(message,code);
    }

    public DriverException(String message,int code, Throwable cause) {
        super(message, code, cause);
    }
    public DriverException(String message, Throwable cause) {
        super(message,cause);
    }
}
