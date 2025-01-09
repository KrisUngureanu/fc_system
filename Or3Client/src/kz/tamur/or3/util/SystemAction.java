package kz.tamur.or3.util;

import java.io.Serializable;

public class SystemAction implements Serializable {
	private int code;
	private String name;

	public static final SystemAction ACTION_LOGIN = new SystemAction(1, "Вход в систему");
	public static final SystemAction ACTION_START_PROCESS = new SystemAction(2, "Запуск процесса");
	public static final SystemAction ACTION_STOP_PROCESS = new SystemAction(3, "Остановка процесса");
	public static final SystemAction ACTION_VIEW_ARCHIVE = new SystemAction(4, "Просмотр архива");
	public static final SystemAction ACTION_EDIT_DICTIONARY = new SystemAction(5, "Редактирование записей НСИ");
	public static final SystemAction ACTION_EDIT_USER = new SystemAction(6, "Редактирование пользователей");
	public static final SystemAction ACTION_EDIT_ROLE = new SystemAction(7, "Редактирование ролей");
    
    public static final SystemAction[] SYSTEM_ACTIONS = {
    	ACTION_LOGIN, ACTION_START_PROCESS, ACTION_STOP_PROCESS, ACTION_VIEW_ARCHIVE,
    	ACTION_EDIT_DICTIONARY, ACTION_EDIT_USER, ACTION_EDIT_ROLE
    };
    
	public SystemAction(int code, String name) {
		super();
		this.code = code;
		this.name = name;
	}
	
	public int getCode() {
		return code;
	}
	
	public String getName() {
		return name;
	}
}
