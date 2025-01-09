package kz.tamur.shep.common;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kz.tamur.shep.synchronous.ErrorInfo;
import kz.tamur.shep.synchronous.ObjectFactory;
import kz.tamur.shep.synchronous.SendMessageSendMessageFaultMsg;

public enum ShepError {
	
	SCE001("Сообщение не соответствует формату"),
	SCE004("Сервис не существует"),
	SCE005("Транспортная подпись не актуальная"),
	SCE006("Не верная транспортная подпись"),
	SCE007("Сообщение не подписано"),
	
	SCE015("Подпись бизнес-данных не актуальная"),
	SCE016("Не верная подпись бизнес-данных"),
	SCE017("Бизнес-данные не подписаны"),
	
	SCE909("Внутренняя ошибка ИС УМОСК");
	
	private String message;
	
	private ShepError(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	private static Log log = LogFactory.getLog(ShepError.class);
	
    public static SendMessageSendMessageFaultMsg createFault(String msg, ShepError error, Throwable cause) {
    	SendMessageSendMessageFaultMsg fault = null;
    	if (cause != null)
    		fault = new SendMessageSendMessageFaultMsg(msg, createErrorInfo(msg, error, cause), cause);
    	else
    		fault = new SendMessageSendMessageFaultMsg(msg, createErrorInfo(msg, error, null));
    	
    	return fault;
    }

    public static ErrorInfo createErrorInfo(String msg, ShepError error, Throwable cause) {
    	ErrorInfo info = new ObjectFactory().createErrorInfo();
		info.setErrorCode(error.name());
		info.setErrorMessage(error.getMessage());
    	
		if (cause != null)
			info.setErrorData(cause.toString());
		else
			info.setErrorData(msg);

		try {
    		GregorianCalendar c = new GregorianCalendar();
    		c.setTime(new Date());
    		info.setErrorDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(c));
    	} catch (Exception e) {
    		log.error(e, e);
    	}

		return info;
    }
    
    public static kz.bee.bip.asyncchannel.v10.interfaces.client.SendMessageSendMessageFaultMsg createAsyncFault(String msg, ShepError error, Throwable cause) {
    	kz.bee.bip.asyncchannel.v10.interfaces.client.SendMessageSendMessageFaultMsg fault = null;
    	if (cause != null)
    		fault = new kz.bee.bip.asyncchannel.v10.interfaces.client.SendMessageSendMessageFaultMsg(msg, createAsyncErrorInfo(msg, error, cause), cause);
    	else
    		fault = new kz.bee.bip.asyncchannel.v10.interfaces.client.SendMessageSendMessageFaultMsg(msg, createAsyncErrorInfo(msg, error, null));
    	
    	return fault;
    }

    public static kz.bee.bip.asyncchannel.v10.interfaces.client.ChangeMassageStatusNotificationChangeMassageStatusNotificationFaultMsg createAsyncNotificationFault(String msg, ShepError error, Throwable cause) {
    	kz.bee.bip.asyncchannel.v10.interfaces.client.ChangeMassageStatusNotificationChangeMassageStatusNotificationFaultMsg fault = null;
    	if (cause != null)
    		fault = new kz.bee.bip.asyncchannel.v10.interfaces.client.ChangeMassageStatusNotificationChangeMassageStatusNotificationFaultMsg(msg, createAsyncErrorInfo(msg, error, cause), cause);
    	else
    		fault = new kz.bee.bip.asyncchannel.v10.interfaces.client.ChangeMassageStatusNotificationChangeMassageStatusNotificationFaultMsg(msg, createAsyncErrorInfo(msg, error, null));
    	
    	return fault;
    }

    public static kz.bee.bip.common.v10.types.ErrorInfo createAsyncErrorInfo(String msg, ShepError error, Throwable cause) {
    	kz.bee.bip.common.v10.types.ErrorInfo info = new kz.bee.bip.common.v10.types.ObjectFactory().createErrorInfo();
		info.setErrorCode(error.name());
		info.setErrorMessage(error.getMessage());
    	
		if (cause != null)
			info.setErrorData(cause.toString());
		else
			info.setErrorData(msg);

		try {
    		GregorianCalendar c = new GregorianCalendar();
    		c.setTime(new Date());
    		info.setErrorDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(c));
    	} catch (Exception e) {
    		e.printStackTrace();
    	}

		return info;
    }

}
