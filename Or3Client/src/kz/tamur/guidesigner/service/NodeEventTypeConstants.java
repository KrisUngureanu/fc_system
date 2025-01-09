package kz.tamur.guidesigner.service;

import com.cifs.or2.server.workflow.definition.EventType;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 15.09.2004
 * Time: 17:48:12
 * To change this template use File | Settings | File Templates.
 */
public interface NodeEventTypeConstants {
    NodeEventType BEFORE_ACTIVITYSTATE_ASSIGNMENT =
            new NodeEventType(EventType.BEFORE_ACTIVITYSTATE_ASSIGNMENT, "До назначения роли");
    NodeEventType AFTER_ACTIVITYSTATE_ASSIGNMENT =
            new NodeEventType(EventType.AFTER_ACTIVITYSTATE_ASSIGNMENT, "После назначения роли");
    NodeEventType BEFORE_PERFORM_OF_ACTIVITY =
            new NodeEventType(EventType.BEFORE_PERFORM_OF_ACTIVITY, "До перемещения");
    NodeEventType BEFORE_OPEN_INTERFACE =
            new NodeEventType(EventType.BEFORE_OPEN_INTERFACE, "Открытие интерфейса");
    NodeEventType PERFORM_OF_ACTIVITY =
            new NodeEventType(EventType.PERFORM_OF_ACTIVITY, "Перемещение");
    NodeEventType AFTER_PERFORM_OF_ACTIVITY =
            new NodeEventType(EventType.AFTER_PERFORM_OF_ACTIVITY, "После перемещения");
    NodeEventType PERFORM_XML = new NodeEventType(EventType.PERFORM_XML, "Формирование xml");
    NodeEventType CHECK_XML = new NodeEventType(EventType.CHECK_XML, "Проверка xml");
    NodeEventType PARS_XML = new NodeEventType(EventType.PARS_XML, "Разбор xml");
    NodeEventType PROCESS_INSTANCE_START  = new NodeEventType(EventType.PROCESS_INSTANCE_START, "Начало процесса" );
    NodeEventType PROCESS_INSTANCE_END    = new NodeEventType(EventType.PROCESS_INSTANCE_END, "Окончание процесса" );
    NodeEventType PROCESS_INSTANCE_AFTER_END    = new NodeEventType(EventType.PROCESS_INSTANCE_AFTER_END, "После окончания процесса" );
    NodeEventType BEFORE_PROCESS_INSTANCE_CANCEL = new NodeEventType(EventType.BEFORE_PROCESS_INSTANCE_CANCEL, "Перед отменой процесса" );
    NodeEventType PROCESS_INSTANCE_CANCEL = new NodeEventType(EventType.PROCESS_INSTANCE_CANCEL, "Отмена процесса" );

    NodeEventType FORK            = new NodeEventType( EventType.FORK,"Разветвление" );
    NodeEventType JOIN            = new NodeEventType( EventType.JOIN,"Объединение" );
    NodeEventType BEFORE_DECISION = new NodeEventType(EventType.BEFORE_DECISION, "Перед выбором" );
    NodeEventType AFTER_DECISION  = new NodeEventType(EventType.AFTER_DECISION, "После выбора" );

    NodeEventType SUB_PROCESS_INSTANCE_START      = new NodeEventType(EventType.SUB_PROCESS_INSTANCE_START, "Начало подпроцесса" );
    NodeEventType SUB_PROCESS_INSTANCE_COMPLETION = new NodeEventType(EventType.SUB_PROCESS_INSTANCE_COMPLETION, "Завершение подпроцесса" );
    NodeEventType SYNC_START = new NodeEventType(EventType.SYNC_START, "Начало синхронизации");
    NodeEventType SYNC_STOP = new NodeEventType(EventType.SYNC_STOP, "Завершение синхронизации");
}
