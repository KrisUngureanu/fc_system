package kz.tamur.or3.util;

import java.io.Serializable;

public class SystemEvent implements Serializable {
	private int code;
	private String name;
	private int typeCode;

	public static final int ALL_EVENTS = 0;

    public static final String TYPE_NORMAL = "Уведомление";
    public static final String TYPE_WARNING = "Предупреждение";
    public static final String TYPE_ERROR = "Ошибка";
    public static final String TYPE_FATAL = "Фатальная ошибка";

    public static final String[] TYPES = { "", TYPE_NORMAL, TYPE_WARNING, TYPE_ERROR, TYPE_FATAL };

	public static final SystemEvent EVENT_DEBUG_MESSAGE = new SystemEvent(100, "Отладочное сообщение", 1);

	public static final SystemEvent EVENT_LOGIN = new SystemEvent(1, "Вход в систему", 1);
    public static final SystemEvent EVENT_LOGOUT = new SystemEvent(2, "Выход из системы", 1);
    public static final SystemEvent EVENT_CHANGE_PASSWORD = new SystemEvent(3, "Смена пароля", 1);
    public static final SystemEvent EVENT_REPL =new SystemEvent(4, "Успешное обновление системы", 1);
    public static final SystemEvent WARNING_REPL =new SystemEvent(5, "Обновление прошло с предупреждением", 2);
    public static final SystemEvent ERROR_REPL =new SystemEvent(6, "Ошибка обновления системы", 3);
    public static final SystemEvent WARNING_BLOCKED =new SystemEvent(7, "Попытка входа заблокированного пользователя", 2);
    public static final SystemEvent WARNING_PASSWSORD =new SystemEvent(8, "Неверный логин или пароль", 2);
    public static final SystemEvent WARNING_USER_CONNECTED =new SystemEvent(9, "Пользователь уже подключен к системе", 2);
    public static final SystemEvent WARNING_WRONG_TOKEN =new SystemEvent(10, "Неверный ключевой контейнер", 2);
    public static final SystemEvent WARNING_CANT_READ_CERT =new SystemEvent(11, "Ошибка при чтении сертификата", 2);
    public static final SystemEvent WARNING_MAX_CONNECTIONS =new SystemEvent(12, "Достигнуто максимальное количество пользователей", 2);
    public static final SystemEvent EVENT_SEND_MESSAGE =new SystemEvent(13, "Отправка сообщения", 1);
    public static final SystemEvent EVENT_SERVER_BLOCK =new SystemEvent(14, "Блокировка сервера", 1);
    public static final SystemEvent EVENT_SERVER_UNBLOCK =new SystemEvent(15, "Разблокировка сервера", 1);
    
    public static final SystemEvent EVENT_PROCESS_START = new SystemEvent(21, "Запуск процесса", 1);
    public static final SystemEvent EVENT_PROCESS_END = new SystemEvent(22, "Завершение процесса", 1);
    public static final SystemEvent EVENT_PROCESS_CANCEL = new SystemEvent(23, "Остановка процесса", 1);
    public static final SystemEvent EVENT_CHANGE_PROCESS =new SystemEvent(23, "Изменение процесса", 1);
    public static final SystemEvent EVENT_CREATE_PROCESS =new SystemEvent(24, "Создание процесса", 1);
    public static final SystemEvent EVENT_RENAME_PROCESS =new SystemEvent(25, "Переименование процесса", 1);
    public static final SystemEvent EVENT_MOVE_PROCESS =new SystemEvent(26, "Перемещение процесса", 1);
    public static final SystemEvent EVENT_COPY_PROCESS =new SystemEvent(27, "Копирование процесса", 1);
    public static final SystemEvent EVENT_DELETE_PROCESS =new SystemEvent(28, "Удаление процесса", 1);
    
    // 30 - 49 Операции с пользователями
    public static final SystemEvent EVENT_USER_CREATE = new SystemEvent(31, "Создание пользователя", 1);
    public static final SystemEvent EVENT_USER_BLOCK =new SystemEvent(32, "Блокировка пользователя", 1);
    public static final SystemEvent EVENT_USER_UNBLOCK =new SystemEvent(33, "Разблокировка пользователя", 1);
    public static final SystemEvent EVENT_USER_DELETE =new SystemEvent(34, "Удаление пользователя", 1);
    public static final SystemEvent EVENT_USER_CLOSE =new SystemEvent(35, "Закрытие сеанса пользователя", 1);
    public static final SystemEvent EVENT_USER_CHANGE =new SystemEvent(36, "Изменение данных пользователя", 1);
    public static final SystemEvent EVENT_USER_RIGHTS =new SystemEvent(37, "Изменение прав пользователя", 1);
    
    public static final SystemEvent EVENT_CHANGE_INTERFACE =new SystemEvent(51, "Изменение интерфейса", 1);
    public static final SystemEvent EVENT_CREATE_INTERFACE =new SystemEvent(52, "Создание интерфейса", 1);
    public static final SystemEvent EVENT_RENAME_INTERFACE =new SystemEvent(53, "Переименование интерфейса", 1);
    public static final SystemEvent EVENT_MOVE_INTERFACE =new SystemEvent(54, "Перемещение интерфейса", 1);
    public static final SystemEvent EVENT_COPY_INTERFACE =new SystemEvent(55, "Копирование интерфейса", 1);
    public static final SystemEvent EVENT_DELETE_INTERFACE =new SystemEvent(56, "Удаление интерфейса", 1);

    public static final SystemEvent EVENT_CHANGE_FILTER =new SystemEvent(61, "Изменение фильтра", 1);
    public static final SystemEvent EVENT_CREATE_FILTER =new SystemEvent(62, "Создание фильтра", 1);
    public static final SystemEvent EVENT_RENAME_FILTER =new SystemEvent(63, "Переименование фильтра", 1);
    public static final SystemEvent EVENT_MOVE_FILTER =new SystemEvent(64, "Перемещение фильтра", 1);
    public static final SystemEvent EVENT_COPY_FILTER =new SystemEvent(65, "Копирование фильтра", 1);
    public static final SystemEvent EVENT_DELETE_FILTER =new SystemEvent(66, "Удаление фильтра", 1);

    public static final SystemEvent EVENT_CHANGE_REPORT =new SystemEvent(71, "Изменение отчета", 1);
    public static final SystemEvent EVENT_CREATE_REPORT =new SystemEvent(72, "Создание отчета", 1);
    public static final SystemEvent EVENT_RENAME_REPORT =new SystemEvent(73, "Переименование отчета", 1);
    public static final SystemEvent EVENT_MOVE_REPORT =new SystemEvent(74, "Перемещение отчета", 1);
    public static final SystemEvent EVENT_COPY_REPORT =new SystemEvent(75, "Копирование отчета", 1);
    public static final SystemEvent EVENT_DELETE_REPORT =new SystemEvent(76, "Удаление отчета", 1);

    public static final SystemEvent EVENT_CHANGE_EXCHANGE =new SystemEvent(81, "Изменение почтового ящика", 1);
    
    public static final SystemEvent EVENT_CHANGE_PASSWORD_POLICY =new SystemEvent(91, "Изменение политики безопасности", 1);
    
    public static final SystemEvent EVENT_DELETE_DIRECTORY =new SystemEvent(110, "Удаление папки", 1);
    
    public static final SystemEvent EVENT_CLASS_CREATED =new SystemEvent(201, "Создание класса", 1);
    public static final SystemEvent EVENT_CLASS_DELETED =new SystemEvent(202, "Удаление класса", 1);
    public static final SystemEvent EVENT_CLASS_CHANGED =new SystemEvent(203, "Изменение класса", 1);
    public static final SystemEvent EVENT_CLASS_TRIGGER_CHANGED =new SystemEvent(223, "Изменение триггера класса", 1);

    public static final SystemEvent EVENT_ATTR_CREATED =new SystemEvent(204, "Создание атрибута", 1);
    public static final SystemEvent EVENT_ATTR_DELETED =new SystemEvent(205, "Удаление атрибута", 1);
    public static final SystemEvent EVENT_ATTR_CHANGED =new SystemEvent(206, "Изменение атрибута", 1);
    public static final SystemEvent EVENT_ATTR_TRIGGER_CHANGED =new SystemEvent(224, "Изменение триггера атрибута", 1);

    public static final SystemEvent EVENT_METHOD_CREATED =new SystemEvent(207, "Создание метода", 1);
    public static final SystemEvent EVENT_METHOD_DELETED =new SystemEvent(208, "Удаление метода", 1);
    public static final SystemEvent EVENT_METHOD_CHANGED =new SystemEvent(209, "Изменение метода", 1);
    public static final SystemEvent EVENT_METHOD_ROLLBACKED =new SystemEvent(225, "Откат метода", 1);

    public static final SystemEvent EVENT_OBJECT_CREATE =new SystemEvent(210, "Создание объекта", 1);
    public static final SystemEvent EVENT_OBJECT_DELETE =new SystemEvent(211, "Удаление объекта", 1);
    public static final SystemEvent EVENT_OBJECT_UPDATE =new SystemEvent(222, "Изменение объекта", 1);

    public static final SystemEvent EVENT_VALUE_SET =new SystemEvent(212, "Установка значения", 1);
    public static final SystemEvent EVENT_VALUE_DELETE =new SystemEvent(213, "Удаление значения", 1);
    
    public static final SystemEvent EVENT_SEND_PROCESS_TO_RECYCLE = new SystemEvent(214, "Перенос процесса в корзину", 1);
    public static final SystemEvent EVENT_RESTORE_PROCESS_FROM_RECYCLE = new SystemEvent(216, "Восстановление процесса из корзины", 1);
    public static final SystemEvent EVENT_SEND_UI_TO_RECYCLE = new SystemEvent(215, "Перенос интерфейса в корзину", 1);
    public static final SystemEvent EVENT_RESTORE_UI_FROM_RECYCLE = new SystemEvent(217, "Восстановление интерфейса из корзины", 1);
    public static final SystemEvent EVENT_SEND_FILTER_TO_RECYCLE = new SystemEvent(218, "Перенос фильтра в корзину", 1);
    public static final SystemEvent EVENT_RESTORE_FILTER_FROM_RECYCLE = new SystemEvent(220, "Восстановление фильтра из корзины", 1);
    public static final SystemEvent EVENT_SEND_REPORT_TO_RECYCLE = new SystemEvent(219, "Перенос отчета в корзину", 1);
    public static final SystemEvent EVENT_RESTORE_REPORT_FROM_RECYCLE = new SystemEvent(221, "Восстановление отчета из корзины", 1);
    public static final SystemEvent EVENT_CHANGE_SCHEDULER_TASK = new SystemEvent(222, "Изменение задачи планировщика", 1);

    public static final SystemEvent ERROR_EXECUTION_ENGINE = new SystemEvent(401, "Ошибка при выполнении шага процесса", 3);
    public static final SystemEvent ERROR_WF_EXPR = new SystemEvent(402, "Ошибка при выполнении формулы на шаге процесса", 3);
    public static final SystemEvent ERROR_COMMIT_LONG_TRANSACTION = new SystemEvent(403, "Ошибка при commitLongTransaction", 3);
    public static final SystemEvent ERROR_TRIGGER_CLS = new SystemEvent(404, "Ошибка при выполнении тригера класса", 3);
    public static final SystemEvent ERROR_TRIGGER_ATTR = new SystemEvent(405, "Ошибка при выполнении тригера атрибута", 3);


    //public static final SystemEvent EVENT_USER_ERROR =new SystemEvent(202, "Пользовательские ошибки", 3);

    public static final SystemEvent[] SYSTEM_EVENTS = {
    	EVENT_LOGIN, EVENT_LOGOUT, EVENT_CHANGE_PASSWORD, EVENT_REPL,
    	WARNING_REPL, ERROR_REPL, WARNING_BLOCKED, WARNING_PASSWSORD,
    	WARNING_USER_CONNECTED, WARNING_WRONG_TOKEN, WARNING_CANT_READ_CERT,
    	WARNING_MAX_CONNECTIONS, EVENT_SEND_MESSAGE, EVENT_SERVER_BLOCK, EVENT_SERVER_UNBLOCK,
    	EVENT_PROCESS_START, EVENT_PROCESS_END, EVENT_PROCESS_CANCEL, EVENT_CHANGE_PROCESS,
    	EVENT_CREATE_PROCESS, EVENT_RENAME_PROCESS, EVENT_MOVE_PROCESS, EVENT_COPY_PROCESS, EVENT_DELETE_PROCESS,
    	EVENT_USER_CREATE, EVENT_USER_BLOCK, EVENT_USER_UNBLOCK, EVENT_USER_DELETE, EVENT_USER_CLOSE, EVENT_USER_CHANGE, EVENT_USER_RIGHTS,
    	EVENT_CHANGE_INTERFACE, EVENT_CREATE_INTERFACE, EVENT_RENAME_INTERFACE, EVENT_MOVE_INTERFACE, EVENT_COPY_INTERFACE, EVENT_DELETE_INTERFACE,
    	EVENT_CHANGE_FILTER, EVENT_CREATE_FILTER, EVENT_RENAME_FILTER, EVENT_MOVE_FILTER, EVENT_COPY_FILTER, EVENT_DELETE_FILTER,
    	EVENT_CHANGE_REPORT, EVENT_CREATE_REPORT, EVENT_RENAME_REPORT, EVENT_MOVE_REPORT, EVENT_COPY_REPORT, EVENT_DELETE_REPORT,
    	EVENT_CHANGE_EXCHANGE, EVENT_DELETE_DIRECTORY,
    	EVENT_CLASS_CREATED, EVENT_CLASS_DELETED, EVENT_CLASS_CHANGED, EVENT_ATTR_CREATED, EVENT_ATTR_CHANGED, EVENT_ATTR_DELETED,
    	EVENT_METHOD_CREATED, EVENT_METHOD_CHANGED, EVENT_METHOD_DELETED, EVENT_OBJECT_CREATE, EVENT_OBJECT_DELETE, EVENT_VALUE_SET, EVENT_VALUE_DELETE,
    	EVENT_SEND_PROCESS_TO_RECYCLE, EVENT_RESTORE_PROCESS_FROM_RECYCLE, EVENT_SEND_UI_TO_RECYCLE, EVENT_RESTORE_UI_FROM_RECYCLE, EVENT_SEND_FILTER_TO_RECYCLE,
    	EVENT_RESTORE_FILTER_FROM_RECYCLE, EVENT_SEND_REPORT_TO_RECYCLE, EVENT_RESTORE_REPORT_FROM_RECYCLE,
    	ERROR_EXECUTION_ENGINE,ERROR_WF_EXPR,ERROR_COMMIT_LONG_TRANSACTION
    };
    
	public SystemEvent(int code, String name, int typeCode) {
		super();
		this.code = code;
		this.typeCode = typeCode;
		this.name = name;
	}
	
	public int getCode() {
		return code;
	}
	
	public String getName() {
		return name;
	}

	public int getTypeCode() {
		return typeCode;
	}
}
