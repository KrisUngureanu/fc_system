package kz.tamur;

import javax.xml.ws.WebServiceException;

import com.cifs.or2.kernel.KrnException;
import kz.tamur.common.ErrorCodes;
import kz.tamur.server.wf.WorkflowException;

/**
 * Created by IntelliJ IDEA.
 * User: ValeT
 * Date: 29.09.2006
 * Time: 10:09:54
 * To change this template use File | Settings | File Templates.
 */
public class OrException extends Exception {

    private int code=0;

    public OrException(Throwable cause) {
        super(cause);
//        checkCode("0");
    }

    public OrException(String message) {
        super(message);
//        checkCode("1");
    }

    public OrException(String message,int code) {
        super(message);
        this.code=code;
//        checkCode("2");
    }

    public OrException(String message, int code, Throwable cause) {
        super(message, cause);
        this.code=code;
    }

    public OrException(String message, Throwable cause) {
        super(message, cause);
        if (cause instanceof WorkflowException) {
            this.code = ((WorkflowException)cause).getErrorCode();
        }else if (cause instanceof OrException) {
            this.code = ((OrException)cause).getErrorCode();
        }else if (cause instanceof KrnException) {
            this.code = ((KrnException)cause).code;
        }else if (cause instanceof WebServiceException) {
            this.code = ErrorCodes.ER_WEB_SERICE_REQUEST;
        }else if (cause instanceof InterruptedException) {
            this.code = ErrorCodes.CANCEL_FLOW_BY_USER;
        }
        
//        checkCode("3");
    }

    public int getErrorCode(){
        return code;
    }

   private void checkCode(String mode) {
        System.out.println("@@@@@ Creating ("+mode+")" + this.getClass() + " with code " + this.code);
        if (this.code == 0) {
            Thread.dumpStack();
        }
    }
}
