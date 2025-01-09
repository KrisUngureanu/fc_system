package kz.tamur.shep.acc;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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

import kz.bee.bip.asyncchannel.v10.interfaces.client.ChangeMassageStatusNotificationChangeMassageStatusNotificationFaultMsg;
import kz.bee.bip.asyncchannel.v10.interfaces.client.IAsyncChannelClient;
import kz.bee.bip.asyncchannel.v10.interfaces.client.SendMessageSendMessageFaultMsg;
import kz.bee.bip.asyncchannel.v10.itypes.AsyncChangeStatusNotifyRequest;
import kz.bee.bip.asyncchannel.v10.itypes.AsyncChangeStatusNotifyResponse;
import kz.bee.bip.asyncchannel.v10.itypes.AsyncMessageInfo;
import kz.bee.bip.asyncchannel.v10.itypes.AsyncSendMessageRequest;
import kz.bee.bip.asyncchannel.v10.itypes.AsyncSendMessageResponse;
import kz.bee.bip.common.v10.types.MessageData;
import kz.bee.bip.common.v10.types.SenderInfo;
import kz.tamur.admin.ErrorsNotification;
import kz.tamur.fc.common.SignatureCheckingResult;
import kz.tamur.or3ee.server.kit.SrvUtils;
import kz.tamur.shep.common.AsyncResponse;
import kz.tamur.shep.common.ShepError;
import kz.tamur.shep.handler.DumpMessageHandler;
import kz.tamur.util.crypto.CheckSignResult;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.server.Context;
import com.cifs.or2.server.Session;
import com.cifs.or2.server.orlang.SrvOrLang;

@WebService
@HandlerChain(file = "chain.xml")
public class AsynchronousChannel implements IAsyncChannelClient {
	
	private static Log log = LogFactory.getLog(AsynchronousChannel.class);
    
    @Resource
    WebServiceContext context;
    
    private String dsName;
    private String user;
    private String password;

    private static int maxAsyncCount = Integer.parseInt(System.getProperty("maxAsyncCount", "100"));

    private static Map<String, ThreadPoolExecutor> threadPools = java.util.Collections.synchronizedMap(new HashMap<String, ThreadPoolExecutor>()); 

    private Session getSession() throws Exception {
        ServletContext servletContext = (ServletContext) context.getMessageContext().get(MessageContext.SERVLET_CONTEXT);
        dsName = servletContext.getInitParameter("dataSourceName");
        user = servletContext.getInitParameter("user");
        password = servletContext.getInitParameter("password");
        return SrvUtils.getSession(dsName, user, password);
    }

    private Session getSession(String dsName, String user, String password) throws Exception {
        return SrvUtils.getSession(dsName, user, password);
    }

    public AsyncChangeStatusNotifyResponse changeMassageStatusNotification(AsyncChangeStatusNotifyRequest request) throws ChangeMassageStatusNotificationChangeMassageStatusNotificationFaultMsg {
        Session s = null;
        try {
            s = getSession();
            KrnClass wsCls = s.getClassByName("уд::view::WsUtilNew");
            Context ctx = new Context(new long[0], 0, 0);
            ctx.langId = 0;
            ctx.trId = 0;
            s.setContext(ctx);
            List<Object> args = new ArrayList<Object>();
            args.add(request);
            SrvOrLang orlang = s.getSrvOrLang();
            AsyncChangeStatusNotifyResponse res = (AsyncChangeStatusNotifyResponse) orlang.exec(wsCls, wsCls, "sys_asynchronous_input_changeMessageStatusNotification", args, new Stack<String>());
            s.commitTransaction();
            return res;
        } catch (Throwable e) {
        	log.error(e, e);
            throw ShepError.createAsyncNotificationFault("Внутренняя ошибка ИС УМОСК", ShepError.SCE909, e);
        } finally {
            if (s != null) {
                s.release();
            }
        }    
    }

    public AsyncSendMessageResponse sendMessage(AsyncSendMessageRequest request) throws SendMessageSendMessageFaultMsg {
        Session s = null;
        try {
            s = getSession();
            KrnClass wsCls = s.getClassByName("уд::view::WsUtilNew");
            KrnClass reestrCls = s.getClassByName("Реестр электронных сервисов");
            KrnAttribute codeAttr = s.getAttributeByName(reestrCls, "код");
            KrnAttribute requestMethodAttr = s.getAttributeByName(reestrCls, "метод обработки запроса");
            KrnAttribute isAvailableAttr = s.getAttributeByName(reestrCls, "сервис доступен?");
            KrnAttribute serviceUsersAttr = s.getAttributeByName(reestrCls, "зап табл получателей сервиса");
            KrnAttribute isMessageDumpAttr = s.getAttributeByName(reestrCls, "логировать xml?");

            KrnClass yes_noCls = s.getClassByName("уд::спр::Да_нет");
            KrnAttribute yes_noCodeAttr = s.getAttributeByName(yes_noCls, "код");

            KrnClass serviceUsersCls = s.getClassByName("уд::осн::Зап_табл_получателей_сервиса");
            KrnAttribute senderIdAttr = s.getAttributeByName(serviceUsersCls, "senderId");
            KrnAttribute isValidateBodySignatureAttr = s.getAttributeByName(serviceUsersCls, "контроль ЭЦП сообщения_Да_нет");
            KrnAttribute isValidateBusinessSignatureAttr = s.getAttributeByName(serviceUsersCls, "выполнять проверку ЭЦП_Да_нет");
            KrnAttribute isValidateOr3Attr = s.getAttributeByName(serviceUsersCls, "выполнять проверки в or3?");
            KrnAttribute isUserMessageDumpAttr = s.getAttributeByName(serviceUsersCls, "логировать xml?");
            
            AsyncMessageInfo messageInfo = request.getMessageInfo();
            MessageData messageData = request.getMessageData();
            String serviceId = messageInfo.getServiceId();
            String correlationId = messageInfo.getCorrelationId();
            String messageId = messageInfo.getMessageId();
            String sessionId = messageInfo.getSessionId();
            
            context.getMessageContext().put("SERVICE_ID", serviceId);
            context.getMessageContext().setScope("SERVICE_ID", Scope.APPLICATION);
            
            final KrnObject[] objects = s.getObjectsByAttribute(reestrCls.id, codeAttr.id, 0, 0, serviceId, 0);
            if(objects.length > 0) {
                Context ctx = new Context(new long[0], 0, 0);
                ctx.langId = 0;
                ctx.trId = 0;
                s.setContext(ctx);
                
                KrnObject serviceUser = null;
                
                SenderInfo senderInfo = messageInfo.getSender();
                String senderId = "null";
                if (senderInfo != null) {
                    senderId = senderInfo.getSenderId();
                    if (senderId == null) {
                    	senderId = "null";
                    }
                }
                
                // Логировать xml сервиса по всем получателям?
                boolean isMessageDump = s.getLongsSingular(objects[0], isMessageDumpAttr, false) == 1;
                
                KrnObject[] serviceUsers = s.getObjects(objects[0].id, serviceUsersAttr.id, new long[0], 0);
                for (int i = 0; i < serviceUsers.length; i++) {
                	if (senderId.equals(s.getStringsSingular(serviceUsers[i].id, senderIdAttr.id, 0, false, false))) {
                		serviceUser = serviceUsers[i];
                		// Логировать xml?
                        if (!isMessageDump) {
                            isMessageDump = s.getLongsSingular(serviceUser, isUserMessageDumpAttr, false) == 1;
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
                	throw ShepError.createAsyncFault("Услуга ИС УМОСК не доступна!", ShepError.SCE004, null);
                }
                
                // Поиск метода обработки
                String verificationMethod = s.getStringsSingular(objects[0].id, requestMethodAttr.id, 0, false, false);
                if (verificationMethod.equals("")) {
                	throw ShepError.createAsyncFault("Техническая ошибка ИС УМОСК. Метод обработки не найден!", ShepError.SCE909, null);
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
		                        		throw ShepError.createAsyncFault("Проверка транспортной ЭЦП. ЭЦП не найдена!", ShepError.SCE007, null);
		                        	else if (!checkingResult_1.isDigiSignOK())
		                        		throw ShepError.createAsyncFault("Проверка транспортной ЭЦП. " + checkingResult_1.getErrorMessage(false), ShepError.SCE006, null);
		                        	else if (!checkingResult_1.isCertOK())
		                        		throw ShepError.createAsyncFault("Проверка транспортной ЭЦП. " + checkingResult_1.getErrorMessage(false), ShepError.SCE005, null);
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
		                        		throw ShepError.createAsyncFault("Проверка ЭЦП бизнес-данных. ЭЦП не найдена!", ShepError.SCE017, null);
		                        	else if (!checkingResult_2.isDigiSignOK())
		                        		throw ShepError.createAsyncFault("Проверка ЭЦП бизнес-данных. " + checkingResult_2.getErrorMessage(false), ShepError.SCE016, null);
		                        	else if (!checkingResult_2.isCertOK())
		                        		throw ShepError.createAsyncFault("Проверка ЭЦП бизнес-данных. " + checkingResult_2.getErrorMessage(false), ShepError.SCE015, null);
                            	}
	                        }
                        }
                    }
                }
                
                // Проверка ошибок и подготовка данных предварительного ответа
                List<Object> verificationArgs = new ArrayList<Object>();
                verificationArgs.add(messageInfo);
                verificationArgs.add(messageData);
                Map<String, Object> vars = new HashMap<String, Object>();
                vars.put("SIGNRESULT_1", checkingResult_1);
                vars.put("SIGNRESULT_2", checkingResult_2);
                final SrvOrLang orlang = s.getSrvOrLang();
                AsyncResponse asyncResponse = (AsyncResponse) orlang.exec(wsCls, wsCls, verificationMethod, verificationArgs, new Stack<String>(), vars);
                final String messageType = asyncResponse.getMessageType();
                final String status = asyncResponse.getStatus();
                final Object paremetrs = asyncResponse.getParametrs();
                Object xml = asyncResponse.getXml();
                
                // Отправка предварительного и окончательного ответов в отдельном потоке
                final List<Object> notificationArgs = new ArrayList<Object>();
                
                if (messageType != null) {
                	notificationArgs.add(messageType);
				} else {
					notificationArgs.add("NOTIFICATION");
				}
                
                notificationArgs.add(xml);
                notificationArgs.add(messageInfo);
                
                String threadPoolKey = serviceId + "_" + senderId;
                ThreadPoolExecutor threadPool = threadPools.get(threadPoolKey);
                if (threadPool == null) {
	            	threadPool = new ThreadPoolExecutor(
	            			maxAsyncCount, maxAsyncCount, 0, TimeUnit.NANOSECONDS,
	                        new LinkedBlockingQueue<Runnable>());
	            	threadPools.put(threadPoolKey, threadPool);
                }
            	
                threadPool.submit(new Runnable() {
                    public void run()
                    {
                        Session s = null;
                        try {
                            //Thread.sleep(3000);
                            s = getSession(dsName, user, password);
                            Context ctx = new Context(new long[0], 0, 0);
                            ctx.langId = 0;
                            ctx.trId = 0;
                            s.setContext(ctx);
                            SrvOrLang orlang = s.getSrvOrLang();
                            // Предварительный ответ
                            if(!status.equals("1") && !status.equals("5")) {
                                orlang.exec(wsCls, wsCls, "sys_asynchronous_output_sendMessage", notificationArgs, new Stack<String>());
                            }
                            // Окончательный ответ
                            if(status.equals("1") || status.equals("4")) {
                                KrnAttribute responseMethodAttr = s.getAttributeByName(reestrCls, "метод обработки ответа");
                                final String responseMethod = s.getStringsSingular(objects[0].id, responseMethodAttr.id, 0, false, true);
                                if (responseMethod.equals("")) {
                                	log.info("Метод формирования окончательного ответа не задан!");
                                } else {
                                    List<Object> responseArgs = new ArrayList<Object>();
                                    responseArgs.add(paremetrs);
                                    orlang.exec(wsCls, wsCls, responseMethod, responseArgs, new Stack<String>());
                                }
                            }
                            s.commitTransaction();
                        } catch (Throwable e) {
                        	log.error(e, e);
                        } finally {
                            if (s != null) {
                                s.release();
                            }
                        }
                    }
                });
                
                int queueCount = 0;
                int activeCount = 0;
                int totalCount = 0;
                
                for (ThreadPoolExecutor pool : threadPools.values()) {
                	queueCount += (pool.getQueue() != null ? pool.getQueue().size() : 0);
                	activeCount += pool.getActiveCount();
                	totalCount += pool.getTaskCount();
                }
                
                log.info("ASYNC Active: " + activeCount + ", Idle: " + queueCount + ", Total: " + totalCount);
            } else {
            	throw ShepError.createAsyncFault("По заданному значению ServiceId услуга не найдена!", ShepError.SCE004, null);
            }
            
            // Формирование ответа главного запроса
            AsyncSendMessageResponse res = new AsyncSendMessageResponse();
            res.setCorrelationId(correlationId);
            res.setMessageId(messageId);  // Нам надо генерировать?
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(new Date());
            try {
                res.setResponseDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar));
            } catch (DatatypeConfigurationException e) {
            	log.error(e, e);
            }
            res.setSessionId(sessionId);
            s.commitTransaction();
            return res;
        } catch (SendMessageSendMessageFaultMsg e) {
            printProcessInfo(request.getMessageInfo());
            log.error(e, e);
            throw e;
        } catch (Throwable e) {
            printProcessInfo(request.getMessageInfo());
            log.error(e, e);
            throw ShepError.createAsyncFault("Внутренняя ошибка ИС УМОСК", ShepError.SCE909, e);
        } finally {
            if (s != null) {
                s.release();
            }
        }
    }
    
    private void printProcessInfo(AsyncMessageInfo messageInfo) {
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
        log.info("<<<<< " + serviceId + ": END SEND ERR SENDER <" + senderId + "> msg=" + messageId + " " + format.format(new Date()) + " >>>>>");
    }
}