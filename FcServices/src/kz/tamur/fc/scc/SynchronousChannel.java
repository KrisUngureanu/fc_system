package kz.tamur.fc.scc;

import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Stack;
import java.util.UUID;

import javax.annotation.Resource;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.servlet.ServletContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import kz.tamur.or3ee.server.kit.SrvUtils;
import kz.tamur.shep.synchronous.ErrorInfo;
import kz.tamur.shep.synchronous.ISyncChannel;
import kz.tamur.shep.synchronous.ObjectFactory;
import kz.tamur.shep.synchronous.SendMessageSendMessageFaultMsg;
import kz.tamur.shep.synchronous.SyncMessageInfo;
import kz.tamur.shep.synchronous.SyncMessageInfoResponse;
import kz.tamur.shep.synchronous.SyncSendMessageRequest;
import kz.tamur.shep.synchronous.SyncSendMessageResponse;

import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.signature.XMLSignature;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.server.Context;
import com.cifs.or2.server.Session;
import com.cifs.or2.server.orlang.SrvOrLang;

@WebService
@HandlerChain(file = "actionchain.xml")
public class SynchronousChannel implements ISyncChannel {
    
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
        SyncSendMessageResponse res = null;

        SyncMessageInfo messageInfo = request.getRequestInfo();
        String serviceId = messageInfo.getServiceId();
        String correlationId = messageInfo.getCorrelationId();
        String sessionId = messageInfo.getSessionId();
        ObjectFactory factory = new ObjectFactory();
        ErrorInfo ei = factory.createErrorInfo();

        try {
            s = getSession();
            KrnClass wsCls = s.getClassByName("уд::view::WsUtil");
            KrnClass reestrCls = s.getClassByName("Реестр электронных сервисов");
            KrnAttribute codeAttr = s.getAttributeByName(reestrCls, "код");
            KrnAttribute requestMethodAttr = s.getAttributeByName(reestrCls, "метод обработки запроса");
            KrnAttribute isAvailableAttr = s.getAttributeByName(reestrCls, "сервис доступен?");
            KrnAttribute isValidateSignatureAttr = s.getAttributeByName(reestrCls, "включить проверку ЭЦП?");
            
            KrnObject[] objects = s.getObjectsByAttribute(reestrCls.id, codeAttr.id, 0, 0, serviceId, 0);
            if(objects.length > 0) {
                Context ctx = new Context(new long[0], 0, 0);
                ctx.langId = 0;
                ctx.trId = 0;
                s.setContext(ctx);
                
                // Проверка доступности услуги
                long[] isAvailableObjs = s.getLongs(objects[0].id, isAvailableAttr.id, 0);
                if (isAvailableObjs != null && isAvailableObjs.length > 0) {
                    long isAvailable = isAvailableObjs[0];
                    if (isAvailable == 0) {
                        throw new Exception("Услуга в данный момент не доступна! Обратитесь к разработчику!");
                    }
                } else {
                    throw new Exception("Услуга в данный момент не доступна! Обратитесь к разработчику!");
                }
                
                // Поиск метода обработки
                String responseMethod = s.getStringsSingular(objects[0].id, requestMethodAttr.id, 0, false, false);
                if (responseMethod.equals("")) {
                    throw new Exception("Техническая ошибка. Метод обработки не найден!");
                }

                // Проверка ЭЦП
                long[] isValidateSignatureObjs = s.getLongs(objects[0].id, isValidateSignatureAttr.id, 0);
                if (isValidateSignatureObjs != null && isValidateSignatureObjs.length > 0) {
                    long isValidateSignature = isValidateSignatureObjs[0];
                    if (isValidateSignature == 1) {
                        Object data = request.getRequestData().getData();
                        if (data.getClass().getName().equals("java.lang.String")) {
                            
                        } else {
                            String className = data.getClass().getName();
                            String packageName = className.substring(0, className.lastIndexOf("."));
                            JAXBContext context = JAXBContext.newInstance(packageName);
                            Marshaller marshaller = context.createMarshaller();
                            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                            dbFactory.setNamespaceAware(true);
                            DocumentBuilder documentBuilder = dbFactory.newDocumentBuilder();
    
                            Document document = documentBuilder.newDocument();
                            marshaller.marshal(data, document);
                            Element signatureElement = (Element) document.getDocumentElement().getLastChild();
                            if (signatureElement == null) {
                                throw new Exception("ЭЦП не найдена!");
                            }
                            XMLSignature signature = new XMLSignature(signatureElement, "");
                            KeyInfo keyInfo = signature.getKeyInfo();
                            X509Certificate certKey = keyInfo.getX509Certificate();
                            
                            boolean result = false;
                            if (certKey != null) {
                                result = signature.checkSignatureValue(certKey);
                            }
                            if (!result) {
                                throw new Exception("ЭЦП невалидна!");
                            }
                        }
                    }
                }

                // Обработка запроса
                List<Object> args = new ArrayList<Object>();
                args.add(objects[0]);
                args.add(request.getRequestInfo());
                args.add(request.getRequestData().getData());
                args.add(new kz.tamur.fc.bank.record.ObjectFactory());
                args.add(new ObjectFactory());
                SrvOrLang orlang = s.getSrvOrLang();
                
                Object response = orlang.exec(wsCls, wsCls, responseMethod, args, new Stack<String>());
                if (response instanceof SyncSendMessageResponse) {
                	res = (SyncSendMessageResponse) response;
                } else if (response instanceof ErrorInfo) {
                	ei = (ErrorInfo) response;
                	throw new SendMessageSendMessageFaultMsg(ei.getErrorMessage(), ei);
                }
                
                // Формирование ответа
                SyncMessageInfoResponse responseInfo = res.getResponseInfo();
                if (responseInfo.getMessageId() == null)
                	responseInfo.setMessageId(UUID.randomUUID().toString());
                if (responseInfo.getCorrelationId() == null)
                	responseInfo.setCorrelationId(correlationId);
                GregorianCalendar calendar = new GregorianCalendar();
                calendar.setTime(new Date());
                try {
                    responseInfo.setResponseDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar));
                } catch (DatatypeConfigurationException e) {
                    e.printStackTrace();
                }
                responseInfo.setSessionId(sessionId);
                
                s.commitTransaction();
                return res;
            } else {
                ei.setErrorCode("999");
                ei.setErrorMessage("По заданному значению ServiceId услуга не найдена!");
                throw new SendMessageSendMessageFaultMsg(ei.getErrorMessage(), ei);
            }
        } catch (SendMessageSendMessageFaultMsg e) {
            e.getFaultInfo().setSessionId(sessionId);
            throw e;
        } catch (Throwable e) {
            e.printStackTrace();
            ei.setErrorCode("999");
            ei.setErrorMessage(e.getMessage());
            ei.setSessionId(sessionId);
            throw new SendMessageSendMessageFaultMsg(ei.getErrorMessage(), ei);
        } finally {
            if (s != null)
                s.release();
        }
    }
}