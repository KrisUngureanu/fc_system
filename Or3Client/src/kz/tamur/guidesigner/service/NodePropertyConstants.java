package kz.tamur.guidesigner.service;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 15.09.2004
 * Time: 17:25:42
 * To change this template use File | Settings | File Templates.
 */
public interface NodePropertyConstants {
    NodeProperty NODE_ID = new NodeProperty("id", "Идентификатор");
    NodeProperty NODE_NAME = new NodeProperty("name", "Имя");
    NodeProperty NODE_TITLE = new NodeProperty("title", "Заголовок задачи");
    NodeProperty EDGE_JOIN = new NodeProperty("join", "Связь");
    NodeProperty NODE_DESCRIPTION = new NodeProperty("description", "Описание");
    NodeProperty RESPONSIBLE = new NodeProperty("responsible", "Инициатор процесса");
    NodeProperty CHOPPER = new NodeProperty("chopper", "Прерыватель процесса");
    NodeProperty CONFLICT_PROCESS = new NodeProperty("conflict", "Конфликтные службы");
    NodeProperty ENABLE_CHOPPER = new NodeProperty("enableChopper", "Доступ прерывателя");
    NodeProperty ASSIGNMENT = new NodeProperty("assignment", "Назначение роли");
    NodeProperty PROCESS = new NodeProperty("process", "Процесс");
    NodeProperty SUBPROCESS_TYPE = new NodeProperty("subprocessType", "Тип подпроцесса");
    NodeProperty SUB_ROLLBACK_TYPE = new NodeProperty("subRollbackType", "Продолжать при откате");
    NodeProperty RETURN_VAR_TYPE = new NodeProperty("returnVarType", "Не возвращать перем.");
    NodeProperty PROCESS_OBJECT = new NodeProperty("processObj", "Объекты обработки");
    NodeProperty OBJECT_TITLE = new NodeProperty("titleObj", "Заголовок объекта обработки");
    NodeProperty OBJECT_PARAM = new NodeProperty("paramObj", "Параметры обработки");
    NodeProperty INSPECTORS = new NodeProperty("inspectors", "Наблюдатели");
    NodeProperty ERROR_PROCESS = new NodeProperty("processError", "Действия при ошибке");
    NodeProperty UI_PROCESS = new NodeProperty("processUi", "Интерфейс обработки");
    NodeProperty UI_INF_PROCESS = new NodeProperty("processInfUi", "Интерфейс наблюдателя");
    NodeProperty PROCESS_OBJECT_INF = new NodeProperty("processObjInf", "Объекты наблюдателя");
    NodeProperty UI_TYPE_INF = new NodeProperty("processUiTypeInf", "Тип интерфейса наблюдателя");
    NodeProperty UI_TYPE = new NodeProperty("processUiType", "Тип интерфейса");
    NodeProperty DATE_ALARM = new NodeProperty("dateAlarm", "Дата завершения");
    NodeProperty DATE_ALERT = new NodeProperty("dateAlert", "Дата внимания");
    NodeProperty EXCH_BOX = new NodeProperty("exchangeBox", "Пункт обмена");
    NodeProperty TASK_COLOR = new NodeProperty("taskColor", "Цвет строки задания");
    NodeProperty SYNCH = new NodeProperty("synch", "Синхронность");
    NodeProperty ACT_AUTO_NEXT = new NodeProperty("isAutoNext", "Авто переход");
    NodeProperty ACT_REPORT_REQUIRE = new NodeProperty("isReportRequire", "Отчет по требованию");
    NodeProperty EVENT_BEFORE_COMMT = new NodeProperty("eventBeforeCommit", "Событие перед комитом");
    NodeProperty EVENT_AFTER_COMMT = new NodeProperty("eventAfterCommit", "Событие после комита");
}
