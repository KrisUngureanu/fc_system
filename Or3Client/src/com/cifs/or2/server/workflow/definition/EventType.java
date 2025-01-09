package com.cifs.or2.server.workflow.definition;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 15.08.2004
 * Time: 16:26:48
 * To change this template use File | Settings | File Templates.
 */
public final class EventType implements Serializable, Comparable {

  private static List<EventType> eventsById = new ArrayList<EventType>();
  private static Map<String, EventType> eventsByText = new HashMap<String, EventType>();

  public static final EventType PROCESS_INSTANCE_START  = new EventType( "process-instance-start","начало-процесса","event" );
  public static final EventType PROCESS_INSTANCE_END    = new EventType( "process-instance-end","окончание-процесса","event" );
  public static final EventType PROCESS_INSTANCE_AFTER_END    = new EventType( "process-instance-after-end","после-окончание-процесса","event" );
  public static final EventType BEFORE_PROCESS_INSTANCE_CANCEL = new EventType( "before-process-instance-cancel" ,"перед-отменой-процесса","event");
  public static final EventType PROCESS_INSTANCE_CANCEL = new EventType( "process-instance-cancel" ,"отмена-процесса","event");

  public static final EventType FLOW_START  = new EventType( "flow-start" ,"начало-потока","event");
  public static final EventType FLOW_END    = new EventType( "flow-end" ,"окончание-потока","event");
  public static final EventType FLOW_CANCEL = new EventType( "subflow-cancel" ,"отмена-дочернего-потока", "event");

  public static final EventType BEFORE_FORK      = new EventType( "before-fork" ,"перед-разветвлением","event");
  public static final EventType FORK            = new EventType( "fork" ,"разветвление","event");
  public static final EventType AFTER_FORK      = new EventType( "after-fork" ,"после-разветвления","event");
  public static final EventType JOIN            = new EventType( "join" ,"слияние","event");
  public static final EventType AFTER_JOIN      = new EventType( "after-join" ,"после-слияния","event");
  public static final EventType TRANSITION      = new EventType( "transition","перемещение", "event");
  public static final EventType BEFORE_DECISION = new EventType( "before-decision" ,"перед-выбором","event");
  public static final EventType AFTER_DECISION  = new EventType( "after-decision" ,"после-выбора","event");

  public static final EventType BEFORE_PERFORM_XML = new EventType( "before-perform-xml" ,"перед-выполнением-xml","event");
  public static final EventType PERFORM_XML = new EventType( "perform-xml" ,"выполнение-xml","event");
  public static final EventType AFTER_PERFORM_XML = new EventType( "after-perform-xml" ,"после-выполнения-xml","event");
  public static final EventType CHECK_XML = new EventType( "check-xml" ,"проверка-xml","event");
  public static final EventType BEFORE_CHECK_XML = new EventType( "before-check-xml" ,"перед-проверкой-xml","event");
  public static final EventType PARS_XML = new EventType( "pars-xml" ,"разбор-xml","event");
  public static final EventType BEFORE_ACTIVITYSTATE_ASSIGNMENT = new EventType( "before-activitystate-assignment" ,"действие-перед-выбором-роли","event");
  public static final EventType AFTER_ACTIVITYSTATE_ASSIGNMENT  = new EventType( "after-activitystate-assignment" ,"действие-после-выбора-роли","event");
  public static final EventType BEFORE_PERFORM_OF_ACTIVITY      = new EventType( "before-perform-of-activity" ,"перед-выполнением-действия","event");
  public static final EventType BEFORE_OPEN_INTERFACE           = new EventType( "before-open-interface" ,"перед-открытием-интерфейса","event");
  public static final EventType PERFORM_OF_ACTIVITY             = new EventType( "perform-of-activity" ,"выполнение-действия","event");
  public static final EventType DEFERRED_PERFORM_OF_ACTIVITY             = new EventType( "deferred-perform-of-activity" ,"отсроченное-выполнение-действия","event");
  public static final EventType AFTER_PERFORM_OF_ACTIVITY       = new EventType( "after-perform-of-activity" ,"после-выполнения-действия","event");
  public static final EventType AFTER_ERROR_OF_ACTIVITY       = new EventType( "after-error-of-activity" ,"после-ошибки-действия","event");
  public static final EventType SUB_PROCESS_INSTANCE_START      = new EventType( "sub-process-instance-start" ,"начало-подпроцесса","event");
  public static final EventType SUB_PROCESS_INSTANCE_COMPLETION = new EventType( "sub-process-instance-completion" ,"подпроцесс-выполнен","event");
  public static final EventType SUB_PROCESS_INSTANCE_AFTER_COMPLETION = new EventType( "sub-process-instance-after-completion" ,"после-выполнения-подпроцесса","event");
  public static final EventType SUB_PROCESS_INSTANCE_AFTER_COMPLETION_2 = new EventType( "sub-process-instance-after-completion-2" ,"после-выполнения-подпроцесса-2","event");

  public static final EventType ACTION               = new EventType( "action" ,"действие","property");
  public static final EventType DELEGATION_EXCEPTION = new EventType( "delegation-exception","исключение","property" );

  public static final EventType ACT_USER_INTERFACE = new EventType( "user-intrface" ,"пользовательский-интерфейс","property");
  public static final EventType ACT_OBJ_EXPR = new EventType( "act-obj-expr" ,"обрабатываемый-объект-выражение","property");
  public static final EventType ACT_OBJ_TITLE_EXPR = new EventType( "act-obj-title-expr","заголовок-обрабатываемый-объект-выражение","property" );
  public static final EventType ACT_DATE_ALARM = new EventType( "act-date-alarm" ,"дата-внимания","property");
  public static final EventType ACT_DATE_ALERT = new EventType( "act-date-alert" ,"дата-тревоги","property");
  public static final EventType TITLE_EXPR = new EventType( "title-expr" ,"заголовок-выражение","property");
  public static final EventType RESPONSIBLE = new EventType( "responsible" ,"ответственный-за-инициализацию","property");
  public static final EventType CHOPPER = new EventType( "chopper" ,"ответственный-за-отмену","property");
  public static final EventType INSPECTORS = new EventType( "inspectors" ,"наблюдатели","property");
  public static final EventType UI_INF = new EventType( "ui_inf" ,"интерфейс-наблюдателя","property");
  public static final EventType CUT_OBJ_INF = new EventType( "cut_obj_inf" ,"интерфейс-объекта-обработки","property");
  public static final EventType CONFLICT_PROCESS = new EventType( "conflict" ,"конфликтные-службы","property");
  public static final EventType BOX_EXCHANGE = new EventType( "box-exchange" ,"пункт-объмена","property");
  public static final EventType SUB_PROCESS      = new EventType( "sub-process" ,"подпроцесс","property");
  public static final EventType EVENT_BEFORE_COMMIT      = new EventType( "before-commit" ,"процесс","property");
  public static final EventType EVENT_AFTER_COMMIT       = new EventType( "after-commit" ,"процесс","property");

  public static final EventType SYNC_BEFORE_START = new EventType( "synchronized-before-start","перед-блоком-синхронизации","event" );
  public static final EventType SYNC_WAIT_START = new EventType( "synchronized-wait-start" ,"ожидание-перед-стартом-блока-синхронизации","event");
  public static final EventType SYNC_START      = new EventType( "synchronized-start" ,"начало-блока-синхронизации","event");
  public static final EventType SYNC_STOP      = new EventType( "synchronized-stop" ,"завершение-блока-синхронизации","event");

  private EventType( String text,String text_ru,String type ) {
    this.id = eventsById.size();
    this.text = text;
    this.text_ru = text_ru;
    this.type = type;
    eventsById.add( id, this );
    eventsByText.put( text, this );
  }

  public static final EventType fromInt( int id ) {
    return eventsById.get( id );
  }

  public static final EventType fromText( String text ) {
    return eventsByText.get( text );
  }

    public String toType() {
      return type;
    }

  public int toInt() {
    return id;
  }

  public String toString() {
    return text;
  }

  public String toStringRu() {
	    return text_ru;
	  }

  public int compareTo(Object o) {
	  return text.compareTo(((EventType)o).text);
  }

  @Override
  public boolean equals(Object obj) {
	  // TODO Auto-generated method stub
	  return (obj instanceof EventType) && this.id==((EventType)obj).id;
  }
  
  private int id = -1;
  private String text = null;
  private String text_ru = null;
  private String type = null;
}
