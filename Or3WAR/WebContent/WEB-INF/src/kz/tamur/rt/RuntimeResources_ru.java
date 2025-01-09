package kz.tamur.rt;

import java.util.ListResourceBundle;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 22.04.2005
 * Time: 15:19:18
 * To change this template use File | Settings | File Templates.
 */
public class RuntimeResources_ru extends ListResourceBundle {

    public static final Object[][] contents = {
            {"applyChangesShort", "Применить"},
            {"cancelChangesShort", "Отменить"},
            {"backPageShort", "Назад"},
            {"usersShort", "Пользователи"},
            {"helpShort", "Помощь"},
            {"deleteUser", "Отключить пользователя"},
            {"signShort", "Подписать"},
            //Buttons tooltip
            {"applyChanges", "Применить изменения"},
            {"cancelChanges", "Обновить с сервера"},
            {"backPage", "Предыдущая страница"},
            {"buttonRun", "Следующий шаг"},
            {"nextPage", "Следующая страница"},
            {"print", "Печать"},
            {"processMap", "Карта процесса"},
            {"superProcess", "Суперпроцесс"},
            {"subProcess", "Подпроцесс"},
            {"debugProcess", "Отладчик"},
            {"pwdChange", "Смена пароля"},
            {"exchange", "Отправить/получить"},
            {"createProcess", "Создается процесс!!!"},
            //Buttons description
            {"applyChangesDesc", "Сохраняет данные интерфейса ввода в области временного\n" +
                                 "хранения (запись данных в область постоянного хранения\n" +
                                 "производится после завершения процесса)"},
            {"cancelChangesDesc", "Восстанавливает первоначальные значения данных\n" +
                                  "в интерфейсе ввода. Первоначальные значения – это\n" +
                                  "значения данных, сохраненные при помощи кнопки\n" +
                                  "\"Применить изменения\""},
            {"backPageDesc", "Возвращает пользователя в предыдущее окно.\n" +
                             "Кнопка не доступна в Главном окне"},
            {"nextPageDesc", "Следующая страница"},
            {"printDesc", "Открывает ниспадающее меню печати выходных\n" +
                          "документов. Если вводная форма не содержит\n" +
                          "возможностей печати, кнопка недоступна"},
            {"processMapDesc", "Открывает окно просмотра карты процесса для\n" +
                               "выделенной строки монитора заданий"},
            {"superProcessDesc", "Возвращает в окно просмотра карты старшего\n" +
                                 "процесса. Если старший процесс отсутствует,\n" +
                                 "кнопка недоступна"},
            {"subProcessDesc", "Вызывает окно просмотра младшего процесса.\n" +
                               "Если младший процесс отсутствует, кнопка\n" +
                               "недоступна"},
            {"debugProcessDesc", "Вызывает окно просмотра значений переменных\n" +
                                 "процесса. Кнопка доступна Администратору Системы"},
            {"pwdChangeDesc", "Вызывает диалоговое окно для смены пароля"},
            {"exchangeDesc", "Инициирует действие получения/отсылки\n" +
                             "сообщения в активном (выполняющемся ) процессе"},
            {"dataLangDesc", "Смена языка данных"},
            {"btnSrvDesc", "Переход в режим управления процессами"},
            {"btnArhDesc", "Переход в режим просмотра базы данных\n" +
                           "и формирования отчетности"},
            {"btnSprDesc", "Переход в режим работы с\n" +
                           "нормативно-справочной информацией"},

            {"baseLabelDesc", "Наименование базы данных, с которой работает\n" +
                           "Система"},
            {"userLabelDesc", "Имя пользователя вошедшего в Систему"},
            {"taskTableDesc", "Таблица активных заданий, инициированных пользователем\n" +
                              "или смежной информационной системой"},
            {"taskTableHeaderDesc", "Заголовок таблицы активных заданий. Данные\n" +
                                    "в таблице можно отсортировать щелчком мыши\n" +
                                    "по названию столбца"},
            {"counterLabelDesc", "Номер выбранного задания и общее количество\n" +
                                 "активных заданий"},
            //Main menu
            //File
            {"file", "&aФайл"},
            {"interface", "Интерфейс"},
            {"exit", "Выход"},
            {"exitMenu", "&dВыход"},
            {"open", "Открыть"},
            {"openMenu", "&jОткрыть"},
            //Settings
            {"settings", "&yНастройка"},
            {"interfaceLang", "&zЯзык интерфейса"},
            {"editorSetup", "Шрифты и фон"},
            {"language", "Язык..."},
            //Window
            {"window", "О&rкна"},
            {"taskMenu", "З&fадачи"},
            {"archiveMenu", "А&hрхив"},
            {"catalogMenu", "&cСправочник"},
            //Help
            {"help", "&/?"},
            {"helpTip", "Кон&nтекстная справка"},
            {"helpTipShort", "Справка"},
            {"helpTipLong", "Контекстная справка"},
            {"helpTipDesc", "Выберите интересующий Вас<br/>элемент интерфейса"},
            {"helpMain", "Базовая справка"},
            {"helpMenu", "Помощь"},
            {"about", "О &gпрограмме"},
            //
            {"reports", "Отчёты"},
            {"beep", "&pЗвуковой сигнал"},
            {"scrollTabProc", "&pСкроллинг закладок"},
            //Dialog buttons
            {"ok", "Ok"},
            {"cancel", "Отмена"},
            {"refresh", "Обновить"},
            {"yes", "Да"},
            {"no", "Нет"},
            {"clear", "Очистить"},
            {"close", "Закрыть"},
            {"edit", "Редактировать"},
            {"continue", "Продолжить ввод"},
            {"save", "Сохранить"},
            {"ignore", "Игнорировать"},
            {"findBtn", "Найти"},
            {"deleteBtn", "Удалить значение"},
            {"create", "Создать"},
            {"toBackground", "В фоновом режиме"},

            //Application title
            {"app", "ГБД ЮЛ"},
            {"errors", "Результат ФЛК"},

            //Messages title
            {"error", "Ошибка"},
            {"confirmation", "Подтверждение"},
            {"message", "Сообщение"},
            {"alert", "Предупреждение"},
            {"option", "Выбор"},
            {"enterPassword", "Введите пароль"},

            //
            {"openInterfaceTip", "Открыть интерфейс"},
            {"openReportTip", "Открыть отчет"},
            {"nextStepTip", "Следующий шаг"},
            {"killProcessTip", "Удалить процесс"},
            {"processesTab", "Процессы"},

            //Messages
            {"exitMessage", "Вы уверены, что хотите выйти из программы?"},
            {"killProcMessage", "Удалить процесс?"},
            {"checkObjectMessage", "Выберите объект"},
            {"ifcNotExistMessage", "Не задан интерфейс"},
            {"nextStepMessage", "Следующий шаг процесса?"},
            {"startProcMessage", "Инициировать процесс"},

            //Labels
            {"datalang", " Язык данных:"},
            {"interfaceLangLabel", "Язык интерфейса:"},

            //RadioButtons
            {"rusShort", "рус"},
            {"kazShort", "каз"},

            //Смена пароля
            {"oldPass", "Старый пароль"},
            {"newPass", "Новый пароль"},
            {"confPass", "Подтверждение"},
            {"passChangeTitle", "Смена пароля"},
            {"errorLoginMessage", "Неверное имя пользователя\n" + "или пароль!"},
            {"completeMessage", "Пароль успешно изменен."},
            {"newPassInvalidMessage", "Не определен новый пароль!"},
            {"oldPassInvalidMessage", "Старый пароль введён неверно!"},
            {"passNotEqualsMessage", "Пароли не совпадают!"},
            {"notCompleteMessage", "Не заполнены необходимые поля!"},
            {"passwordTooShort", "Пароль не может быть\n меньше %1% символов!"},
            {"passwordNotChanged", "Пароль не был изменен!"},
            {"passwordExpired", "<html>Ваш пароль устарел!<br>Изменить пароль прямо сейчас?"},

            //LoginBox
            {"login", "Пользователь:"},
            {"password", "Пароль:"},
            {"loginTitle", "Авторизация"},
            //ServerParams
            {"srvName", "Наименование:"},
            {"host", "IP-адрес:"},
            {"port", "Порт:"},
            {"srvChange", "Смена сервера"},
            {"srvNoChanged", "Данные о сервере не изменились"},
            //FilterDates Dialog
            {"filterDatesTitle", "Введите временные параметры"},
            {"filterDatesBegin", "Начало периода"},
            {"filterDatesEnd", "Конец периода"},
            {"filterDatesCurrent", "Текущая дата"},
            {"formReport", "1"},
             //TableNavigator
            {"fastRep", "Быстрый отчёт"},
            {"consal", "Объединить записи"},
            {"add", "Добавить запись"},
            {"delete", "Удалить запись"},
            {"find", "Найти запись"},
            {"filter", "Применить фильтр"},
            {"copyRows", "Копирование строк"},
            {"goDown", "Направление движения курсора"},
            {"moveDown", "Переместить строку вниз"},
            {"moveUp", "Переместить строку вверх"},
            {"showDeleted", "Показать удаленные значения"},
            {"cancelApplyFilter","Отменить действие фильтров"},
            {"selectFilters","Выберите фильтр(ы)"},
            //login,password
            {"wrongLoginOrPassword","Неверное имя или пароль пользователя!"},
            {"userHasConnected", "Пользователь уже подключен к системе!"},
            {"userHasConnectedSameIP", "Пользователь уже подключен к системе! Продолжить вход в Систему?"},
            {"serverDisconnect", "Отсутствует связь с сервером!"},
            {"userIsBlocked", "Учетная запись заблокирована администратором!"},

            //Date formats and char
            {"mask", "дд.мм.гггг"},
            {"mask1", "дд.мм.гггг чч:ММ"},
            {"mask2", "дд.мм.гггг чч:ММ:сс"},
            {"mask3", "дд.мм.гггг чч:ММ:сс:ССС"},
            {"mask4", "чч:ММ:сс"},
            {"mask5", "чч:ММ"},
            {"mask6", "дд.мм"},
            {"charD", "д"},
            {"charM", "м"},
            {"charG", "г"},
            {"charCH", "ч"},
            {"charMM", "М"},
            {"charS", "с"},
            {"charSS", "С"},
            // FindRowPanel
            {"findTitle", "Найти запись"},
            {"searchComplete", "Поиск закончен.\r\nЗаписей не найдено!"},
            {"searchChooseColumn", "Выберите колонку для поиска"},
            {"findRowInColumn", "Найти запись в колонке"},
            {"case", "С учетом регистра"},
            {"fullAnalog", "Полное совпадение"},
            {"firstSymbol", "Начинается с"},
            {"containsStr", "Содержит"},

            {"copiesCount", "Количество копий"},
            {"beforeAddAction", "Действие перед добавлением"},
            {"afterAddAction", "Действие после добавления"},
            {"deleteRowsConfirm", "Подтвердите удаление %1% записей!\n Продолжить?"},
            {"deleteRowConfirm", "Подтвердите удаление записи № %1%!\n Продолжить?"},
            {"beforeDeleteAction", "Действие перед удалением"},
            {"afterDeleteAction", "Действие после удаления"},
            {"afterMoveAction", "Действие после перемещения"},
            // tree
            {"treeCollapse", "Свернуть дерево"},
            {"treeExpand", "Раскрыть дерево"},
            {"renameNode", "Переименовать узел"},
            {"changeNode", "Привязать к узлу другое значение"},
            {"createNode", "Создать внутри узла"},
            {"createNodeBefore", "Создать перед узлом"},
            {"createNodeAfter", "Создать после узла"},
            {"createNodeAndBind", "Создать узел и привязать значение"},
            {"deleteNode", "Удалить узел"},
            {"expandNode", "Раскрыть"},
            {"collapseNode", "Свернуть"},

            {"renameNodeTitle", "Переименование элемента"},
            {"bindNodeTitle", "Выберите значение"},
            {"createNodeTitle", "Создание нового объекта"},
            {"deleteNodeTitle", "Удалить элемент(ы)?"},

            {"duplicateData", "Внимание! Данные дублируются"},
            {"enterFolderName", "Введите наименование папки"},
            {"enterElementName", "Введите наименование элемента"},
            {"enterIntCopyName", "Введите наименование копии интерфейса"},
            {"download", "Скачать"},
            {"upload", "Загрузить"},
            {"view", "Просмотр"},
            {"fileNotFound", "Файл не найден"},
            {"fileNotAppend", "Файл не прикреплён"},
            {"fileUploader", "Загрузка файла"},

            {"upFolder", "На один уроверь вверх"},
            {"createFolder", "Создать папку"},
            {"list", "Список"},
            {"table", "Таблица"},
            {"findIn", "Поиск в:"},
            {"saveIn", "Сохранить в"},
            {"fileTypes", "Типы файлов"},
            {"fileName", "Имя файла"},
            {"fileSize", "Размер"},
            {"fileType", "Тип"},
            {"fileDate", "Дата"},
            {"fileAttr", "Атрибут"},
            {"desktop", "Рабочий стол"},
                            
            {"openFile", "Открыть файл"},
            {"openFileTitle", "Открытие файла"},
            {"docs", "Документы Word, Excel"},
            {"pictures", "Рисунки GIF, JPG"},

            {"noname", "Безымянный"},
            {"nocomment", "Без комментариев..."},
            {"doc_saved","Изменения успешно сохранены!"},

            {"sendToDeveloper", "Отправить разработчику"},
            
            //For Terminal use, rewrite it please
            {"run", "Выполнить"},
            {"resetData", "Сбросить"},
            {"clear2", "Очистить"},
            {"clearConsole", "Очистить консоль"},
            {"server", "Сервер"},
            {"client", "Клиент"},
            {"properties", "Свойство"},
            {"informations", "Сведения"},
            {"key", "ключ"},
            {"value", "значение"},
            {"type", "тип"},
            {"process", "Процессы"},
            {"setHotKey", "назначить горячую клавишу"},
            {"lastSrv", "Последние процессы"},
            {"runSrv", "Запустить процесс"},
            {"searchSrv", "Поиск процесса"},
            {"clickToSet", "Назначить нажатием"},
            {"description", "Описание"},
            {"next", "Далее"},
            {"pleaseInsertWord", "Пожалуйста введите слово"},
            {"withFolders", "с папками"},
            {"contain", "содержит"},
            {"startsWith", "начинается"},
            {"endsWith", "кончается"},
            {"hotKeys", "Горячие клавишы"},
            {"search", "Поиск"},
            {"not_found", "не найдено"},
            {"reloadProcess", "Перезагрузить процесс"},

            // верификация пароля
            {"validPwdMinLogin","Имя пользователя не может быть меньше X символов!"},
            {"validPwdMaxLogin","Имя пользователя не может быть больше X символов!"},
            {"validPwdmMinPass","Пароль не может быть меньше X символов!"},
            {"validPwdmMaxPass","Пароль не может быть больше X символов!"},
            {"validPwdMinPassAdm","Для администратора пароль не может быть меньше X символов!"},
            {"validPwdNoNumb","В пароле должны присутствовать цифры!"},
            {"validPwdNoAllNumb","В пароле не должны явно преобладать цифры!"},
            {"validPwdNoSymb","В пароле должны присутствовать буквы!"},
            {"validPwdNoReg","В пароле должны присутствовать буквы в различном регистре!"},
            {"validPwdNoSpec","В пароле должны присутствовать специальные символы!"},
            {"validPwdNotName","В пароле запрещено использовать имена!"},
            {"validPwdNotSurn","В пароле запрещено использовать фамилии!"},
            {"validPwdNotTel","В пароле запрещено использовать номера телефонов!"},
            {"validPwdNotWord","В пароле запрещено использовать частоупотребляемые слова!"},
            {"validPwdNotKeyboard","В пароле запрещено использовать клавиатурные выражения!"},
            {"validPwdNotRep","В пароле запрещено повторение первых трёх символов!"},
            {"validPwdNotRepAnyMoreTwo","В пароле запрещено повторение последовательности из более 2-х одинаковых символов подряд"},
            {"validPwdNotLogin","В пароле не должен употребляться логин пользователя!"},
            {"validPwdNotIdentificationData","В пароле запрещено использовать собственные идентификационные данные!"},
            
            // сообщения авторизации пользователя
            {"messAccessDenied","Доступ запрещен!\nВам необходимо сменить пароль!"},
            {"messNotBD","Вам не назначена рабочая база. Обратитесь к Вашему администратору."},
            {"messNotLangInf","Вам не назначен язык интерфейса. Обратитесь к Вашему администратору."},
            {"messNotLangData","Вам не назначен язык данных. Обратитесь к Вашему администратору."},
            {"messFirstLogin","<html>Это первая авторизация в системе.<br>Необходимо изменить пароль!"},
            {"messPassEnded","<html>Срок действия пароля закончился.<br>Пароль необходимо сменить!"},
            {"messMinPeriodPass","Пароль разрешено менять не чаще чем раз в X суток!"},
            {"messPassIdent","Пароли идентичны!"},
            {"messPassDupl","Пароль не должен повторять X предыдущих паролей!"},
            {"recordNotActiv","Доступ запрещен!\nИстекло время активации вашей учётной записи!"},

            // диаграмма
            {"notData","Нет данных для отображения!"},
            // Календарик
            {"okDate","Подтвердить выбор"},
            {"cancelDate","Отменить выбор"},
            {"todayDate","Установить текущую"},
            {"backMonth","Предыдущий месяц"},
            {"nextMonth","Следующий месяц"},
            {"backYear","Предыдущий год"},
            {"nextYear","Следующий год"},
            //
            {"delete2", "Удалить"},
            
            {"key-choose-dialog-title", "Укажите путь к ключевому файлу"},
            {"key-choose-button-title", "Выбрать"},
            {"key-choose-p12-desc", "Файлы ключей ЭЦП в формате PKCS12"},
            {"key-choose-label", "Ключевой файл"},
            {"key-choose-tooltip", "Выбрать файл с ключом ЭЦП"},

            {"firstPage", "Первая страница"},
            {"lastPage", "Последняя страница"},
            {"nextPage", "Следующая страница"},
            {"backPage", "Предыдущая страница"},
            {"increase", "Увеличить"},
            {"reduce", "Уменьшить"},
            
            {"send", "Отправить"},
            {"webSend", "Завершить"},
            {"webStartPage", "Главная"},
            {"webMyProfile", "Мое личное дело"},
            {"webMonitor", "Монитор процессов"},
            {"webOldFlows", "Давно запущенные процессы"},
            {"webProcesses", "Процессы"},
            {"webShtat", "Штатная расстановка"},
            {"webArchive", "Электронное хранилище"},
            {"webDicts", "Справочники"},
            {"webStat", "Статистика"},
            {"webAdmins", "Технологический блок"},
            {"webRights", "Права доступа"},
            {"webUserAct", "Мониторинг действия пользователей"},
            {"webTasksIn", "Входящие задачи"},
            {"webTasksOut", "Исходящие задачи"},
            {"webTasksMy", "Проекты документов"},
            {"webTasksOld", "Давно созданные проекты документов"},
            {"webTasksOldWarning", "Уведомление о наличии давно запущенных Вами процессов!"},
            {"webTasksOldText1", "Система предоставляет Вам список, давно запущенных вами процессов. Эти процессы занимают определенные ресурсы на сервере."},
            {"webTasksOldText2", "В целях экономного использования ресурсов сервера  Вам необходимо по каждому из запущенных процессов принять одно из следующих решений: удалить или завершить."},
            {"webTasksOldText3", "Период для принятия решений"},
            {"webTasksOldText4", "дней с начала получения уведомления."},
            {"webTooltips","Всплывающие подсказки"},
            {"webNoteSound", "Звук Уведомления"},
            {"webInstantECP","Хранить пароль ЭЦП во время сессии"},
            {"webOn","Вкл."},
            {"webOff","Выкл."},
            {"webSearchMain","Введите запрос для поиска"},
            {"webSearchTaskByName", "Введите название задачи"},
            
            {"webNotification", "Уведомления"},
            {"webNotification2", "Уведомления после входа"},
            {"webPeriodNotification","Период получения уведомлений"},
            {"webStartDayNotification","Дата начала"},
            {"webEndDayNotification","Дата конца"},
            {"webNotificationTitle","Сообщение"},
            {"webNotificationFrom","Отправитель"},
            {"webNotificationInDate","Получено"},
            {"webNotificationOpenDate","Открыто"},
            {"webNotificationAwereDate","Ознакомлено"},
            
            {"webAvailProc", "Список доступных процессов"},
            {"webNSI", "Нормативно-справочная информация"},
            {"webSessions", "Сессии пользователей"},
            {"webProfile", "Профиль пользователя"},
            {"webBirthday", "Дата рождения"},
            {"webDetails", "Подробнее"},
            {"webExit", "Выйти"},
            {"webPass", "Пароль"},
            {"webPassChange", "Изменить текущий пароль"},
            
            {"webPhotoLoad", "Загрузить фото"},
            {"webPhotoDelete", "Удалить фото"},
            {"webPhotoTake", "Взять из личного дела"},
            
            {"webRoles", "Роли"},
            {"webPosition", "Должность"},
            {"webGO", "Государственный орган, структурное подразделение"},
            {"webOrganization", "Организация"},
            {"webContacts", "Контакты"},
            {"webTel", "Тел. вн."},
            {"webBreadcrumbs", "Навигационная цепочка"},
            {"webLang", "Язык интерфейса"},
            {"webTheme", "Тема"},
            {"webExpand", "Расскрыть"},
            {"webExpandAll", "Расскрыть все"},
            {"webCollapse", "Свернуть"},
            {"webCollapseAll", "Свернуть все"},
            {"webMoveUp", "Вверх"},
            {"webMoveDown", "Вниз"},
            {"webFavoriteProcesses", "Избранные процессы"},
            {"webAddToFavorites", "Добавить в избранное"},
            {"webRemoveFromFavorites", "Удалить из избранного"},
 
            {"webWait", "Подождите..."},
            {"webPassChangeTitle", "Смена пароля"},
            {"webPassOld", "Старый пароль"},
            {"webPassNew", "Новый пароль"},
            {"webPassConfirm", "Повтор пароля"},
            {"webDlgOk", "OK"},
            {"webDlgClose", "Закрыть"},
            {"webDlgCancel", "Отмена"},
            {"webDlgChange", "Изменить"},
            {"weDlgSave", "Сохранить"},
            {"webDlgContinue2", "Продолжить ввод"},
            {"webDlgContinue", "Продолжить"},
            {"webDlgIgnore", "Игнорировать"},
            {"webErrors", "Ошибки заполнения данных"},
            {"webFilter", "Фильтрация"},
            {"webClear","Очистить"},
            {"webFeature","Признак"},
            {"webAll", "Все"},
            {"webViewed","Просмотренные"},
            {"webUnreviewed","Непросмотренные"},
            
            
            {"webSaving", "Сохранение данных..."},
            {"webCanceling", "Отмена внесенных изменений..."},
            {"webDeleting","Удаление..."},
            {"webAlert","Пользовательское сообщение"},
            {"webRemoveProcess","Вы уверены, что хотите удалить задачу?"},
            {"webRemoveProcess2","Вы уверены, что хотите удалить задачи"},
            {"webProcStop", "Остановить процесс?"},
            {"webAllCheckedProc", "Выделить все"},
            {"webDeleteProcess", "Удалить задачу"},
            {"webSort","Сортировка:"},
            {"webChronology","По хронологии"},
            {"webOverdue","По просроченности"},
            {"webInFireCount","Количество просроченных задач"},
            {"webEnableListUpdate", "Включить обновление списка"},
            {"webDisableListUpdate", "Выключить обновление списка"},
            {"webShowUsedMemory", "Показать используемую память"},
            {"webUsedMemory", "Используемая память"},

            {"webHelp", "Помощь"},
            {"webHelp1", "Что такое e-kyzmet?"},
            {"webHelp2", "Помощь в работе с Системой"},
            {"webHelp3", "Часто задаваемые вопросы"},
            {"webHelp4", "Нормативно-правовые акты"},
            {"webAskNextStep", "Вы уверены, что хотите передать данные в дальнейшую обработку?"},
            
            {"webDlgSign", "Подписать"},
            {"webKeyStore", "Ключевой контейнер"},
            {"webPKCS12Files", "Файлы ключей ЭЦП в формате PKCS12"},
            
            {"webLastSuccesProcessDef", "Последний раз процесс был Вами успешно запущен"},
            {"webLastSuccessfullTime", "Дата последнего успешного входа"},
            {"webLastUnsuccessfullTime", "Дата последней неуспешной попытки входа"},
            {"webRemoveSession","Вы уверены, что хотите удалить сессию?"},
            {"webRemoveSession2","Вы уверены, что хотите удалить сессии"},
            {"webDeleteSessionBtn","Отключить пользователей"},
            {"webSendMessageBtn","Отправить сообщение"},
            {"webSendMessageTitle","Отправка сообщения"},
                        
            {"ipAddress", "IP-адрес"},
            {"IIN", "ИИН"},
            {"rptGenerateMessage", "Отчет формируется"},
            {"procPerformedMessage", "Процедура выполняется"},

            {"supportTeam", "Служба поддержки"},
            {"pingTime", "Время отклика"},
            {"webHelpDocs", "Справочные материалы"},
            {"processEngaged", "Процесс находится на стадии обработки данных!"},
            {"processActive", "Выполняется предыдущее действие!"},
            {"processEngagedByUser", "Процесс находится на стадии обработки данных пользователем '{1}'!"},
            {"processStartTimeout", "Время ожидания открытия интерфейса истекло ({1} сек). Попробуйте позднее."},
            {"processStartError", "Ошибка при запуске процесса!"},
            {"processStepError", "Ошибка на шаге процесса!"},
            {"processStateError", "Процесс находится в состоянии ошибки! Обратитесь к разработчику!"},
            {"processNotFound", "Не найден запущенный процесс для данного задания! Обратитесь к разработчику!"},
            {"processManyFound", "Найдено {1} запущенных процессов для данного задания! Обратитесь к разработчику!"},
            {"olapAnalytic", "Аналитика"}
    };

    protected Object[][] getContents() {
        return contents;
    }
}