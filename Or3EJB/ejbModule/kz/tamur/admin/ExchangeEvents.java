package kz.tamur.admin;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 01.06.2005
 * Time: 12:13:20
 * To change this template use File | Settings | File Templates.
 */
public final class ExchangeEvents {
    public static final String info_="Уведомление";
    public static final String warn_="Предупреждение";
    public static final String error_="Ошибка";
    public static final String fatal_="Фатальная ошибка";
    //Timer
    public static final ExchangeEvents TMR_000 = new ExchangeEvents("TMR","TMR_000","Задание планировщиком выполнено успешно",info_);
    public static final ExchangeEvents TMR_001 = new ExchangeEvents("TMR","TMR_001","Ошибка при выполнении задания планировщиком",error_);
    public static final ExchangeEvents TMR_002 = new ExchangeEvents("TMR","TMR_002","Ошибка запуска задания планировщиком из-за простоя сервера",error_);
    public static final ExchangeEvents TMR_003 = new ExchangeEvents("TMR","TMR_003","Одноразовый запуск задания из планировщика",info_);
    //MailTransport
    public static final ExchangeEvents EML_000 = new ExchangeEvents("EML","EML_000","Связь установлена успешно (MailTransport)",info_);
    public static final ExchangeEvents EML_001 = new ExchangeEvents("EML","EML_001","Ошибка при установлении связи (MailTransport)",error_);
    public static final ExchangeEvents EML_1020= new ExchangeEvents("EML","EML_1020","Ошибка при получении сообщения (MailTransport)",error_);
    public static final ExchangeEvents EML_1021= new ExchangeEvents("EML","EML_1021","Ошибка записи в буфер при получении сообщения (MailTransport)",error_);
    public static final ExchangeEvents EML_0030= new ExchangeEvents("EML","EML_0030","Ошибка при отправке сообщения (MailTransport)",error_);
    public static final ExchangeEvents EML_0031= new ExchangeEvents("EML","EML_0031","Ошибка записи в буфер при отправке сообщения (MailTransport)",error_);
    //MqTransport
    public static final ExchangeEvents MQT_000 = new ExchangeEvents("MQT","MQT_000","Связь установлена успешно (MqTransport)",info_);
    public static final ExchangeEvents MQT_001 = new ExchangeEvents("MQT","MQT_001","Ошибка при установлении связи (MqTransport)",error_);
    public static final ExchangeEvents MQT_1020= new ExchangeEvents("MQT","MQT_1020","Ошибка при получении сообщения (MqTransport)",error_);
    public static final ExchangeEvents MQT_1021= new ExchangeEvents("MQT","MQT_1021","Ошибка записи в буфер при получении сообщения (MqTransport)",error_);
    public static final ExchangeEvents MQT_0030= new ExchangeEvents("MQT","MQT_0030","Ошибка при отправке сообщения (MqTransport)",error_);
    public static final ExchangeEvents MQT_0031= new ExchangeEvents("MQT","MQT_0031","Ошибка записи в буфер при отправке сообщения (MqTransport)",error_);

    //JmsTransport
    public static final ExchangeEvents JMST_000= new ExchangeEvents("JMST","JMST_000","Связь установлена успешно (JmsTransport)",info_);
    public static final ExchangeEvents JMST_001= new ExchangeEvents("JMST","JMST_001","Ошибка при установлении связи (JmsTransport)",error_);
    public static final ExchangeEvents JMST_1020= new ExchangeEvents("JMST","JMST_1020","Ошибка при получении сообщения (JmsTransport)",error_);
    public static final ExchangeEvents JMST_1021= new ExchangeEvents("JMST","JMST_1021","Ошибка записи в буфер при получении сообщения (JmsTransport)",error_);
    public static final ExchangeEvents JMST_0030= new ExchangeEvents("JMST","JMST_0030","Ошибка при отправке сообщения (JmsTransport)",error_);
    public static final ExchangeEvents JMST_0031= new ExchangeEvents("JMST","JMST_0031","Ошибка записи в буфер при отправке сообщения (JmsTransport)",error_);
    //MessageCash
    public static final ExchangeEvents MSC_000= new ExchangeEvents("MSC","MSC_000","Сообщение передано в транспортную систему",info_);
    public static final ExchangeEvents MSC_001= new ExchangeEvents("MSC","MSC_001","Сообщение отправлено успешно",info_);
    public static final ExchangeEvents MSC_100= new ExchangeEvents("MSC","MSC_100","Сообщение получено от транспортной системы",info_);
    public static final ExchangeEvents MSC_101= new ExchangeEvents("MSC","MSC_101","Сообщение принято в обработку",info_);
    public static final ExchangeEvents MSC_102= new ExchangeEvents("MSC","MSC_102","Сообщение обработано успешно",info_);
    public static final ExchangeEvents MSC_003= new ExchangeEvents("MSC","MSC_003","Ошибка при отправке сообщения",error_);
    public static final ExchangeEvents MSC_103= new ExchangeEvents("MSC","MSC_103","В сообщении обнаружена ошибка при разборе XML",error_);
    public static final ExchangeEvents MSC_104= new ExchangeEvents("MSC","MSC_104","Сообщение не принято ни одной из служб",error_);
    public static final ExchangeEvents MSC_005= new ExchangeEvents("MSC","MSC_005","Ошибка в заголовке отправляемого сообщения",error_);
    public static final ExchangeEvents MSC_106= new ExchangeEvents("MSC","MSC_106","Ошибка в заголовке полученного сообщения",error_);
    public static final ExchangeEvents MSC_107= new ExchangeEvents("MSC","MSC_107","Файл заблокирован и не может быть перемещен в архив",error_);
    public static final ExchangeEvents MSC_108= new ExchangeEvents("MSC","MSC_108","Файл заблокирован и не может быть перемещен в папку ошибок",error_);
    //WebService
    public static final ExchangeEvents WSC_000= new ExchangeEvents("WSC","WSC_000","Сообщение подготовлено к отправке",info_);
    public static final ExchangeEvents WSC_001= new ExchangeEvents("WSC","WSC_001","Сообщение обработано успешно",info_);
    public static final ExchangeEvents WSC_100= new ExchangeEvents("WSC","WSC_100","Ошибка при установлении связи",error_);
    public static final ExchangeEvents WSC_101= new ExchangeEvents("WSC","WSC_101","Другая ошибка",error_);
  
    private ExchangeEvents( String eventType,String event,String descript,String type ) {
        this.eventType=eventType;
        this.event = event;
        this.descript = descript;
        this.type = type;
      }
	public String getEventType() {
		return eventType;
	}
	public String getEvent() {
		return event;
	}
	public String getType() {
		return type;
	}
	public String getDescription() {
		return descript;
	}
	private String eventType;
    private String event;
    private String descript;
    private String type;
}
