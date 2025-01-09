package kz.bee.bip.asyncchannel.v10.interfaces.client;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * Интерфейс для реализации сервиса на стороне клиентов ШЭП для работы с асинхронным каналом.
 * Сервис реализуется как на стороне провайдера сервиса, так и на стороне использующей сервис, в случае если во взаимодействии требуется что бы ШЭП доставлял сообщения методом вызова сервиса получателя сообщения (PUSH)
 *
 * This class was generated by Apache CXF 2.7.7.redhat-1
 * 2014-09-02T16:37:26.257+06:00
 * Generated source version: 2.7.7.redhat-1
 * 
 */
@WebService(targetNamespace = "http://bip.bee.kz/AsyncChannel/v10/Interfaces/Client", name = "IAsyncChannelClient")
@XmlSeeAlso({kz.bee.bip.asyncchannel.v10.types.client.ObjectFactory.class, kz.bee.bip.asyncchannel.v10.types.ObjectFactory.class,
	kz.bee.bip.common.v10.types.ObjectFactory.class, kz.bee.bip.asyncchannel.v10.itypes.ObjectFactory.class,
	kz.tamur.fc.gbdrn.report303.ObjectFactory.class, kz.tamur.fc.gbdrn.report305.ObjectFactory.class,
	kz.tamur.fc.nobd.actualization.ObjectFactory.class})
public interface IAsyncChannelClient {

    /**
     * Метод приема уведомлений об изменении статуса сообщения в ШЭП
     */
    @WebResult(name = "response", targetNamespace = "")
    @RequestWrapper(localName = "changeMassageStatusNotification", targetNamespace = "http://bip.bee.kz/AsyncChannel/v10/Types/Client", className = "kz.bee.bip.asyncchannel.v10.types.client.ChangeMassageStatusNotification")
    @WebMethod
    @ResponseWrapper(localName = "changeMassageStatusNotificationResponse", targetNamespace = "http://bip.bee.kz/AsyncChannel/v10/Types/Client", className = "kz.bee.bip.asyncchannel.v10.types.client.ChangeMassageStatusNotificationResponse")
    public kz.bee.bip.asyncchannel.v10.itypes.AsyncChangeStatusNotifyResponse changeMassageStatusNotification(
        @WebParam(name = "request", targetNamespace = "")
        kz.bee.bip.asyncchannel.v10.itypes.AsyncChangeStatusNotifyRequest request
    ) throws ChangeMassageStatusNotificationChangeMassageStatusNotificationFaultMsg;

    /**
     * Метод приема сообщений
     */
    @WebResult(name = "response", targetNamespace = "")
    @RequestWrapper(localName = "sendMessage", targetNamespace = "http://bip.bee.kz/AsyncChannel/v10/Types", className = "kz.bee.bip.asyncchannel.v10.types.SendMessage")
    @WebMethod
    @ResponseWrapper(localName = "sendMessageResponse", targetNamespace = "http://bip.bee.kz/AsyncChannel/v10/Types", className = "kz.bee.bip.asyncchannel.v10.types.SendMessageResponse")
    public kz.bee.bip.asyncchannel.v10.itypes.AsyncSendMessageResponse sendMessage(
        @WebParam(name = "request", targetNamespace = "")
        kz.bee.bip.asyncchannel.v10.itypes.AsyncSendMessageRequest request
    ) throws SendMessageSendMessageFaultMsg;
}