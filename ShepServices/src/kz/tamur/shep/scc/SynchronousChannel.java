package kz.tamur.shep.scc;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;

import javax.annotation.Resource;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.servlet.ServletContext;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.MessageContext.Scope;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kz.tamur.admin.ErrorsNotification;
import kz.tamur.fc.common.SignatureCheckingResult;
import kz.tamur.or3ee.server.kit.SrvUtils;
import kz.tamur.shep.common.Response;
import kz.tamur.shep.common.ShepError;
import kz.tamur.shep.handler.DumpMessageHandler;
import kz.tamur.shep.synchronous.ISyncChannel;
import kz.tamur.shep.synchronous.ObjectFactory;
import kz.tamur.shep.synchronous.ResponseData;
import kz.tamur.shep.synchronous.SendMessageSendMessageFaultMsg;
import kz.tamur.shep.synchronous.SenderInfo;
import kz.tamur.shep.synchronous.SyncMessageInfo;
import kz.tamur.shep.synchronous.SyncMessageInfoResponse;
import kz.tamur.shep.synchronous.SyncSendMessageRequest;
import kz.tamur.shep.synchronous.SyncSendMessageResponse;
import kz.tamur.util.crypto.CheckSignResult;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.server.Context;
import com.cifs.or2.server.Session;
import com.cifs.or2.server.orlang.SrvOrLang;

@WebService
@HandlerChain(file = "chain.xml")
public class SynchronousChannel implements ISyncChannel {
	
	private static Log log = LogFactory.getLog(SynchronousChannel.class);

    @Resource
    WebServiceContext context;

    private Session getSession() throws Exception {
        ServletContext servletContext = (ServletContext) context.getMessageContext().get(MessageContext.SERVLET_CONTEXT);
        String dsName = servletContext.getInitParameter("dataSourceName");
        String user = servletContext.getInitParameter("user");
        String password = servletContext.getInitParameter("password");
        return SrvUtils.getSession(dsName, user, password);
    }

    public SyncSendMessageResponse sendMessage(SyncSendMessageRequest request) throws SendMessageSendMessageFaultMsg {
        Session s = null;
        try {
            log.info("START: messageId - " + request.getRequestInfo().getMessageId() + ", serviceId - " + request.getRequestInfo().getServiceId());
            s = getSession();
            KrnClass wsCls = s.getClassByName("уд::view::WsUtilNew");
            KrnClass reestrCls = s.getClassByName("Реестр электронных сервисов");
            KrnAttribute codeAttr = s.getAttributeByName(reestrCls, "код");
            KrnAttribute requestMethodAttr = s.getAttributeByName(reestrCls, "метод обработки запроса");
            KrnAttribute isAvailableAttr = s.getAttributeByName(reestrCls, "сервис доступен?");
            KrnAttribute serviceUsersAttr = s.getAttributeByName(reestrCls, "зап табл получателей сервиса");
            KrnAttribute isMessageDumpAttr = s.getAttributeByName(reestrCls, "логировать xml?");
            KrnAttribute isAddDataSignatureAttr = s.getAttributeByName(reestrCls, "подписывать бизнес-данные в хэндлере?");

            KrnClass yes_noCls = s.getClassByName("уд::спр::Да_нет");
            KrnAttribute yes_noCodeAttr = s.getAttributeByName(yes_noCls, "код");

            KrnClass serviceUsersCls = s.getClassByName("уд::осн::Зап_табл_получателей_сервиса");
            KrnAttribute senderIdAttr = s.getAttributeByName(serviceUsersCls, "senderId");
            KrnAttribute isAddDataNamespaceAttr = s.getAttributeByName(serviceUsersCls, "передавать неймспейс в теге data?");
            KrnAttribute isEmptyPrefixNamespaceAttr = s.getAttributeByName(serviceUsersCls, "передавать неймспейс без префикса?");
            KrnAttribute isUserAddDataSignatureAttr = s.getAttributeByName(serviceUsersCls, "подписывать бизнес-данные в хэндлере?");
            KrnAttribute isBusinessDataCDATAAttr = s.getAttributeByName(serviceUsersCls, "передавать бизнес-данные в CDATA?");
            KrnAttribute isBusinessDataSignatureAttr = s.getAttributeByName(serviceUsersCls, "подписывать корневой элемент сервиса ВИС?");
            KrnAttribute isValidateBodySignatureAttr = s.getAttributeByName(serviceUsersCls, "контроль ЭЦП сообщения_Да_нет");
            KrnAttribute isValidateBusinessSignatureAttr = s.getAttributeByName(serviceUsersCls, "выполнять проверку ЭЦП_Да_нет");
            KrnAttribute isValidateOr3Attr = s.getAttributeByName(serviceUsersCls, "выполнять проверки в or3?");
            KrnAttribute isUserMessageDumpAttr = s.getAttributeByName(serviceUsersCls, "логировать xml?");
            
            SyncMessageInfo messageInfo = request.getRequestInfo();
            String serviceId = messageInfo.getServiceId();
            //String correlationId = messageInfo.getCorrelationId();
            String messageId = messageInfo.getMessageId();
            String sessionId = messageInfo.getSessionId();

            Object data = request.getRequestData().getData();
            
            context.getMessageContext().put("SERVICE_ID", serviceId);
            context.getMessageContext().setScope("SERVICE_ID", Scope.APPLICATION);

            KrnObject[] objects = s.getObjectsByAttribute(reestrCls.id, codeAttr.id, 0, 0, serviceId, 0);
            if (objects.length > 0) {
                Context ctx = new Context(new long[0], 0, 0);
                ctx.langId = 0;
                ctx.trId = 0;
                s.setContext(ctx);
                
                KrnObject serviceUser = null;
                
                boolean isAddDataNamespace = false;
                boolean isEmptyPrefixNamespace = false;
                boolean isBusinessDataCDATA = false;
                boolean isBusinessDataSignature = false;
                
                SenderInfo senderInfo = messageInfo.getSender();
                String senderId = "null";
                if (senderInfo != null) {
                    senderId = senderInfo.getSenderId();
                }
                
                // Логировать xml сервиса по всем получателям?
                boolean isMessageDump = s.getLongsSingular(objects[0], isMessageDumpAttr, false) == 1;
                boolean isAddDataSignature = s.getLongsSingular(objects[0], isAddDataSignatureAttr, false) == 1;
                
                KrnObject[] serviceUsers = s.getObjects(objects[0].id, serviceUsersAttr.id, new long[0], 0);
                for (int i = 0; i < serviceUsers.length; i++) {
                	if (senderId.equals(s.getStringsSingular(serviceUsers[i].id, senderIdAttr.id, 0, false, false))) {
                		serviceUser = serviceUsers[i];
                		
                        // Передавать неймспейс в теге data? Обычно он передается в конверте ШЭП
                        isAddDataNamespace = s.getLongsSingular(serviceUser, isAddDataNamespaceAttr, false) == 1;
                        // Передавать неймспейс корневого элемента веб-сервиса ВИС без префикса
                        isEmptyPrefixNamespace = s.getLongsSingular(serviceUser, isEmptyPrefixNamespaceAttr, false) == 1;
                		// Передавать бизнес-данные в CDATA?
                		isBusinessDataCDATA = s.getLongsSingular(serviceUser, isBusinessDataCDATAAttr, false) == 1;
                        // Подписание не содержимое <data>, а содержимое корневого элемента веб-сервиса ВИС
                        isBusinessDataSignature = s.getLongsSingular(serviceUser, isBusinessDataSignatureAttr, false) == 1;
                        // Логировать xml?
                        if (!isMessageDump) {
                            isMessageDump = s.getLongsSingular(serviceUser, isUserMessageDumpAttr, false) == 1;
						}
                        // Подписывать бизнес-данные в хэндлере?
                        if (!isAddDataSignature) {
                        	isAddDataSignature = s.getLongsSingular(serviceUser, isUserAddDataSignatureAttr, false) == 1;
                        }
                	}
                }
                
                // Логировать xml?
                context.getMessageContext().put("MESSAGE_DUMP", isMessageDump);
                context.getMessageContext().setScope("MESSAGE_DUMP", Scope.APPLICATION);
                if (isMessageDump) {
                	DumpMessageHandler.dumpMessage(context.getMessageContext());
				}

                // Проверка доступности услуги
                boolean isAvailable = s.getLongsSingular(objects[0], isAvailableAttr, false) == 1;
                if (!isAvailable) {
                	throw ShepError.createFault("Услуга ИС УМОСК не доступна!", ShepError.SCE004, null);
                }
                
                // Поиск метода обработки
                String executionMethod = s.getStringsSingular(objects[0].id, requestMethodAttr.id, 0, false, false);
                if ("".equals(executionMethod) || s.getMethodByName(wsCls.id, executionMethod) == null) {
                	throw ShepError.createFault("Техническая ошибка ИС УМОСК. Метод обработки не найден!", ShepError.SCE004, null);
                }

                context.getMessageContext().put("BODY_SIGNATURE_EXIST", context.getMessageContext().get("BODY_SIGNATURE_EXIST"));
                context.getMessageContext().setScope("BODY_SIGNATURE_EXIST", Scope.APPLICATION);

                String uuid_1 = (String) context.getMessageContext().get("OR3_MSG_ID_1");
                CheckSignResult checkingResult_1 = SignatureCheckingResult.getCheckingResultMap().remove(uuid_1);
                String uuid_2 = (String) context.getMessageContext().get("OR3_MSG_ID_2");
                CheckSignResult checkingResult_2 = SignatureCheckingResult.getCheckingResultMap().remove(uuid_2);

                if (serviceUser != null) {
                	// Выполнять проверки в методах OR3, а не в исходниках
                	boolean isValidateOr3 = s.getLongsSingular(serviceUser, isValidateOr3Attr, false) == 1;
                	if (!isValidateOr3) {
                        // Анализ ЭЦП тела сообщения
                        KrnObject isValidateBodySignatureObj = s.getObjectsSingular(serviceUser.id, isValidateBodySignatureAttr.id, false);
                        if (isValidateBodySignatureObj != null) {
                            String isValidateHeaderSignature = s.getStringsSingular(isValidateBodySignatureObj.id, yes_noCodeAttr.id, 0, false, false);
                            if ("1".equals(isValidateHeaderSignature)) {
                                if (checkingResult_1 == null || !checkingResult_1.isDigiSignOK() || !checkingResult_1.isCertOK()) {
                                	ErrorsNotification.notifyErrors("TO_106", "SERVICE_" + serviceId + "_SENDER_" + senderId, "Проверка транспортной ЭЦП. " + (checkingResult_1 == null ? "ЭЦП не найдена!" : checkingResult_1.getErrorMessage(false)), null, null);
                                	
                                	if (checkingResult_1 == null)
                                		throw ShepError.createFault("Проверка транспортной ЭЦП. ЭЦП не найдена!", ShepError.SCE007, null);
                                	else if (!checkingResult_1.isDigiSignOK())
                                		throw ShepError.createFault("Проверка транспортной ЭЦП. " + checkingResult_1.getErrorMessage(false), ShepError.SCE006, null);
                                	else if (!checkingResult_1.isCertOK())
                                		throw ShepError.createFault("Проверка транспортной ЭЦП. " + checkingResult_1.getErrorMessage(false), ShepError.SCE005, null);
                                }
                            }
                        }
                        
                        // Анализ ЭЦП бизнес-данных
                        KrnObject isValidateBusinessSignatureObj = s.getObjectsSingular(serviceUser.id, isValidateBusinessSignatureAttr.id, false);
                        if (isValidateBusinessSignatureObj != null) {
                            String isValidateBusinessSignature = s.getStringsSingular(isValidateBusinessSignatureObj.id, yes_noCodeAttr.id, 0, false, false);
                            if ("1".equals(isValidateBusinessSignature)) {
                                if (checkingResult_2 == null || !checkingResult_2.isDigiSignOK() || !checkingResult_2.isCertOK()) {
                                	ErrorsNotification.notifyErrors("TO_106", "SERVICE_" + serviceId + "_SENDER_" + senderId, "Проверка ЭЦП бизнес-данных. " + (checkingResult_2 == null ? "ЭЦП не найдена!" : checkingResult_2.getErrorMessage(false)), null, null);
                                	
                                	if (checkingResult_2 == null)
                                		throw ShepError.createFault("Проверка ЭЦП бизнес-данных. ЭЦП не найдена!", ShepError.SCE017, null);
                                	else if (!checkingResult_2.isDigiSignOK())
                                		throw ShepError.createFault("Проверка ЭЦП бизнес-данных. " + checkingResult_2.getErrorMessage(false), ShepError.SCE016, null);
                                	else if (!checkingResult_2.isCertOK())
                                		throw ShepError.createFault("Проверка ЭЦП бизнес-данных. " + checkingResult_2.getErrorMessage(false), ShepError.SCE015, null);
                                }
                            }
                        }
                    }
                }

                // Обработка запроса
                List<Object> args = new ArrayList<Object>();
                args.add(messageInfo);
                args.add(data);
                Map<String, Object> vars = new HashMap<String, Object>();
                vars.put("SIGNRESULT_1", checkingResult_1);
                vars.put("SIGNRESULT_2", checkingResult_2);
                SrvOrLang orlang = s.getSrvOrLang();
                Response response = (Response) orlang.exec(wsCls, wsCls, executionMethod, args, new Stack<String>(), vars);
                List<String> errors = response.getErrors();
                if (errors.size() > 0) {
                    throw ShepError.createFault(errors.toString().substring(1, errors.toString().length() - 1), ShepError.SCE909, null);
                }
                
                String messageIdOut = response.getMessageId();
                if (messageIdOut == null || messageIdOut.isEmpty()) {
                	messageIdOut = UUID.randomUUID().toString();
				}
                
                // Подписывать бизнес-данные в хэндлере?
                log.info("Подписывать бизнес-данные в хэндлере: " + isAddDataSignature);
                context.getMessageContext().put("ADD_DATA_SIGNATURE", isAddDataSignature);
                context.getMessageContext().setScope("ADD_DATA_SIGNATURE", Scope.APPLICATION);
                
                // Передавать неймспейс в теге data? Обычно он передается в конверте ШЭП
                context.getMessageContext().put("ADD_DATA_NAMESPACE", isAddDataNamespace);
                context.getMessageContext().setScope("ADD_DATA_NAMESPACE", Scope.APPLICATION);
                
                // Передавать неймспейс корневого элемента веб-сервиса ВИС без префикса
                String elementNameEmptyPrefix = response.getElementNameEmptyPrefix();
                if (isEmptyPrefixNamespace && elementNameEmptyPrefix != null && !elementNameEmptyPrefix.isEmpty()) {
                	context.getMessageContext().put("ADD_EMPTY_PREFIX_NAMESPACE", elementNameEmptyPrefix);
                    context.getMessageContext().setScope("ADD_EMPTY_PREFIX_NAMESPACE", Scope.APPLICATION);
				}
                
                // Если необходимо передавать бизнес-данные в CDATA
                context.getMessageContext().put("ADD_BUSINESS_DATA_CDATA", isBusinessDataCDATA);
                context.getMessageContext().setScope("ADD_BUSINESS_DATA_CDATA", Scope.APPLICATION);
                
                // Подписание не содержимое <data>, а содержимое корневого элемента веб-сервиса ВИС
                String elementNameSignature = response.getElementNameSignature();
                if (isBusinessDataSignature && elementNameSignature != null && !elementNameSignature.isEmpty()) {
                	context.getMessageContext().put("ADD_BUSINESS_DATA_SIGNATURE", elementNameSignature);
                    context.getMessageContext().setScope("ADD_BUSINESS_DATA_SIGNATURE", Scope.APPLICATION);
				}

                // Формирование ответа
                ObjectFactory factory = new ObjectFactory();
                SyncSendMessageResponse res = factory.createSyncSendMessageResponse();
                SyncMessageInfoResponse responseInfo = factory.createSyncMessageInfoResponse();
                responseInfo.setMessageId(messageIdOut);
                responseInfo.setCorrelationId(messageId);
                GregorianCalendar calendar = new GregorianCalendar();
                calendar.setTime(new Date());
                try {
                    responseInfo.setResponseDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar));
                } catch (DatatypeConfigurationException e) {
                	log.error(e, e);
                }
                responseInfo.setStatus(response.getStatus());
                responseInfo.setSessionId(sessionId);
                res.setResponseInfo(responseInfo);
                ResponseData responseData = factory.createResponseData();
                responseData.setData(response.getResponse());
                res.setResponseData(responseData);
                s.commitTransaction();
                return res;
            } else {
                throw ShepError.createFault("По заданному значению ServiceId услуга не найдена!", ShepError.SCE004, null);
            }
        } catch (SendMessageSendMessageFaultMsg e) {
            printProcessInfo(request.getRequestInfo());
            log.error(e, e);
            throw e;
        } catch (Throwable e) {
            printProcessInfo(request.getRequestInfo());
            log.error(e, e);
            throw ShepError.createFault("Внутренняя ошибка ИС УМОСК", ShepError.SCE909, e);
        } finally {
            if (s != null) {
                s.release();
            }
            log.info("FINISH: messageId - " + request.getRequestInfo().getMessageId() + ", serviceId - " + request.getRequestInfo().getServiceId());
        }
    }

    private void printProcessInfo(SyncMessageInfo messageInfo) {
        String serviceId = null;
        String messageId = null;
        String senderId = null;
        if (messageInfo != null) {
            serviceId = messageInfo.getServiceId();
            messageId = messageInfo.getMessageId();
            SenderInfo senderInfo = messageInfo.getSender();
            if (senderInfo != null) {
                senderId = senderInfo.getSenderId();
            }
        }
        if (serviceId == null || serviceId.isEmpty()) {
            serviceId = "Unknown serviceId";
        }
        if (messageId == null || messageId.isEmpty()) {
            messageId = "Unknown messageId";
        }
        if (senderId == null || senderId.isEmpty()) {
            senderId = "Unknown senderId";
        }
        DateFormat format = new SimpleDateFormat("hh:mm:ss dd-MM-yyyyy");
        log.info("<<<<< " + serviceId + ": FINISH SEND ERR SENDER <" + senderId + "> msg=" + messageId + " " + format.format(new Date()) + " >>>>>");
    }
}