package kz.tamur.comps.models;

import java.util.Map;
import java.util.HashMap;


/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 04.05.2004
 * Time: 12:09:18
 * To change this template use File | Settings | File Templates.
 */
public class En2Ru {
    private static final Map<String, String> dict = new HashMap<String, String>() {{
        put("Root", "Элементы");
        put("varName", "Имя переменной");
        put("pos", "Позиция");
        put("ref", "Данные");
        put("deletedRef", "Признак удаления");
        put("width", "Ширина");
        put("height", "Высота");
        put("weightx", "Вес x");
        put("weighty", "Вес y");
        put("fill", "Заполнение");
        put("pref", "Предп");
        put("max", "Макс");
        put("min", "Мин");
        put("data", "Привязка к БД");
        put("contentSort", "Сортировка");
        put("tableRef", "Привязка к таблице");
        put("imageRef", "Привязка к картинке");
        put("titleRef", "Привязка к названию");
        
        put("imageUID", "UUID компонента");
        
        put("copy", "Копир");
        put("copyPath", "Данные");
        put("title", "Заголовок");
        put("titleBeforeAttaching", "Заголовок до прикрепления");
        put("titleAfterAttaching", "Заголовок после прикрепления");
        put("iconBeforeAttaching", "Иконка до прикрепления");
        put("iconAfterAttaching", "Иконка после прикрепления");
        put("title1", "Заголовок");
        put("dynamicTitle", "Динамический заголовок");
        put("titleAlign", "Ориентация заголовка");
        put("language", " Язык");
        put("langExpr", " Язык.Формула");
        put("font", "Шрифт");
        put("colors", "Цвета");
        put("fontColor", "Цвет");
        put("backgroundColor", "Цвет");
        put("borderColor", "Цвет бордюра");
        put("border", "Бордюр");
        put("borderType", "Стиль");
        put("borderTitle", "Заголовок");
        put("borderTitlePos", "Позиция заголовока");
        put("borderTitleAlign", "Выравн заголовока");
        put("borderThick", "Толщина");
        put("text", "Текст");
        put("constraints", "Ограничения");
        put("showTextAsXML", "Отображать текст как XML");
        put("formula", "Формула");
        put("expr", "Выражение");
        put("message", "Сообщение");
        put("obligation", "Обязательность");
        put("group", "Группа");
        put("input", "Ввод");
        put("activity", "Активность");
        put("include", "Включает");
        put("exclude", "Исключает");
        put("charsNumber", "Кол символов");
        put("left", "Слева");
        put("right", "Справа");
        put("orientation", "Ориентация");
        put("scrollPolicy", "Политика");
        put("tabPolicy", "Политика");
        put("tabOrientation", "Ориентация закладок");
        put("selected", "Выбран");
        put("combonotsorted", "Не сортировать содержимое");
        put("sortingDirection", "Направление сортировки");
        put("sortingIndex", "Индекс сортировки");
        put("canSort", "Возможность сортировки");
        put("image", "Рисунок");
        put("barCodeImg", "QR код");
        put("enabled", "Доступность");
        put("lightFontColor", "Подсветка");
        put("opaque", "Непрозрачность");
        put("showIcon", "Показывать иконку");
        put("fullPath", "Полный путь");
        put("editorType", "Тип редактора");
        put("header", "Заголовок");
        put("columncount","Кол-во колонок");
        put("content","Содержимое");
        put("contentCalc","Содержимое формула");
        put("styledtext","Текст");
        put("spravInterface","Справ. интерфейс");
        put("objClass","Класс обр. объекта");
        put("outputInterface","Интерфейс для вывода");
        put("moveDocToServ","Службы-приёмники");
        put("attrStateCtrl","Контроль сост. атр.");
        put("attrSetState","Установка сост. атр.");
        put("interface","Интерфейс");
        put("afterdot","Кол-во цифр после запятой");
        put("role","Роль");
        put("typeEmail","Тип как email");
        put("conditions","Условия");
        put("navi","Навигатор");
        put("show","Отображать");
        put("showTitle","Отображать заголовок таблицы");
        put("showPaging","Отображать переход по листам");
        put("showColHeader","Отображать наименования колонок");
        put("fitColumns","Подгонять ширину колонок");
        put("deleteRowColumn","Колонка - удалить строку");
        put("rowNowrap","Не переносить содержимое ячеек");
        put("wrapNodeContent","Переносить содержимое узлов");
        
        put("pageSize","Кол-во объектов на стр");
        put("pageList","Возможные кол-ва объектов на стр");
        put("childrenSize","Макс. кол-во детей в узле");
        
        put("addPan","Доп. панель");
        put("color1","Цвет зебры 1");
        put("color2","Цвет зебры 2");
        put("footer","Подвал");
        put("treeRef","Данные для дерева");
        put("rootRef","Корень (путь)");
        put("titlePath","Титулы");
        put("titlePathExpr","Титулы(Формула)");
        put("titlePath2","Титулы 2");
        put("sortPath","Атрибут сортировки");
        put("treeTitle","Название дерева");
        put("treeTitle1","Название дерева");
        put("treeWidth","Ширина дерева");
        put("summary","Итоги");
        put("filters","Фильтры");
        put("selectedRef","Выбираемый атрибут");
        put("treeDataRef","Данные для узла");
        put("clearBtnShow","Кнопка \"Удалить\"");
        put("sortTreeData","Сортировать узлы");
        put("editable","Запрет редакт.");
        put("nocopy","Запрет копирование");
        put("checkDisabled","Проверять неактивный?.");
        put("refreshMode","Обновление содержимого");
        put("copyTitle","Заголовок копира");
        put("autoset","Автоустановка");
        put("radioGroup","Радиогруппа");
        put("cashFlag","Кэш");
        put("actionJobBefore","Выполнить перед модиф.");
        put("actionJobAfter","Выполнить после модиф.");
        put("sorted","Сортировка");
        put("alignmentText","Выравнивание текста");
        put("showAllText","Отображение всего текста");
        put("appearance","Способ отображения");
        put("anchor","Положение");
        put("anchorImage","Положение рисунка");
        put("reports","Отчёты");
        put("buttons","Кнопки");
        put("autoresize","Авторазмер");
        put("unionFlr","Условие объединения узлов");
        put("attrFlr","Фильтруемый атрибут");
        put("linkFlr","Ссылка на фильтр");
        put("operFlr","Отношение");
        put("compFlr","Тип правой части отношения");
        put("valFlr","Правая часть отношения");
        put("krnObjFlr","Объект");
        put("exprFlr","Выражение");
        put("compAttrFlr","Атрибут");
        put("maxTrFlr","Максимальная транзакция");
        put("maxTrFlr","Максимальная транзакция");
        put("maxIndFlr","Максимальный индекс");
        put("kolOperFlr","Условие на количество объектов");
        put("kolObjFlr","Количество объектов");
        put("relativeFlr","Относительно");
        put("transFlr","Учет транзакций");
        put("maxTrFlr","Максимальная транзакция");
		put("excludeFlr","Отключить условие");
		put("arrayFlr","Массив");
        put("action","Действие");
        put("baseStructure", "Структура баз");
        put("bases", "Структуры баз");
        put("calc", "Формула проверять");
        put("calcValue", "Формула значение");
        put("groupType", "Одиночный");
        put("tabIndex", "Порядок перехода");
        put("access", "Доступ");
        put("dividerLocation", "Пол. разделителя (%)");
        put("dividerLocation1", "Пол. разделителя (знач.)");
        put("unique", "Уникальный");
        put("uniqueSelection", "Уникальность выбора");
        put("sequence", "Счётчик");
        put("sequences", "Последовательность");
        put("strikes", "Многократно");
        put("seqPrefix", "Префикс");
        put("pmenu", "Меню");
        put("defaultFilter", "Фильтр на содержимое");
        put("insets", "Отступы");
        put("topInsets", "Сверху");
        put("leftInsets", "Слева");
        put("bottomInsets", "Снизу");
        put("rightInsets", "Справа");
        put("slider", "Слайдер");
        put("sliderOrientation", "Положение");
        put("sliderLabels", "Подписи");
        put("sliderTicks", "Шкала");
        put("sliderSnapTicks", "Привязка к шкале");
        put("ticks", "Шкала");
        put("sliderMin", "Минимум");
        put("sliderMax", "Максимум");
        put("sliderStepMin", "Шаг втор.");
        put("sliderStepMax", "Шаг осн.");
        put("sliderTrack", "Направляющая");
        put("autoCreatePath","Автовставка");
        put("createCopy","Создать копию");
        put("actionJobBeforClear","Выполнить перед удалением");
        put("defaultRadioItem","Выбор по умолчанию");
        put("defSummary","Итог по умолчанию");
        put("sort","Сортировка");
        put("beforAdd","Перед добавл.");
        put("afterAdd","После добавл.");
        put("beforeDelete","Перед удалением");
        put("afterDelete","После удаления");
        put("afterCopy","После копирования");
        put("afterMove","После перемещения");
        put("dynamicIfc","Дин. интерфейс");
        put("dynamicIfc_expr","Дин. интерфейс (формула)");
        put("editIfc","Ред. вызыв. интерфейс");
        put("description","Краткая справка");
        put("multiselection","Мультивыбор");
        put("rootChecked","Мультивыбор галочка для корень");
        put("ifcLock","Блокировка диалога");
        put("showPrefix","Не отображать префикс");
        put("beforeOpenAction","Выполнить перед открытием");
        put("afterOpenAction","Выполнить после открытия");
        put("beforeModAction","Выполнить перед модиф.");
        put("afterModAction","Выполнить после модиф.");
        put("upperCase","Заглавная буква");
        put("formatPattern","Формат");
        put("paramFilters","Парам. фильтры");
        put("paramName","Имя параметра");
        put("isBlockErrors","Не показывать ошибки");
        put("backgroundColorExpr","Формула");
        put("fontColorExpr","Вычисл. цвет шрифта");
        put("lineWrap","Разрыв линии");
        put("wrapStyleWord","Перенос по словам");
        put("format","Формат даты");
        put("isArchiv","Запрет редакт.(без ссылок)");
        put("folderSelect","Выбор папки");
        put("folderAsLeaf","Папка как лист?");
        put("showSearchLine","Показать строку поиска?");
        put("rowBackColorExpr","Вычисл. цвет фона строки");
        put("rowFontColorExpr","Вычисл. цвет шрифта строки");
        put("upperAllChar","Верхний регистр");
        put("dialogSize","Размер диалога");
        put("dialogWidth","Ширина");
        put("dialogHeight","Высота");
        put("view","Вид");
        put("background","Фон");
        put("pov","Поведение");
        put("activExpr","Формула");
        put("activDelExpr","Формула удаление");
        put("fontExpr","Формула");
        put("fontG","Гарнитура");
        put("act","Действия");
        put("callDialog","Вызвать диалог");
        put("inCallDialog","В вызванном диалоге");
        put("afterCallDialog","После выбора в диалоге");
        put("charModification","Характер модиф. в интерф.");
        put("contentFilter","Фильтр на содержимое");
        put("fastRepBtn","Быстрый отчёт");
        put("consalBtn","Объединение строк");
        put("addBtn","Добавить строку");
        put("delBtn","Удалить строку");
        put("copyRowsBtn","Копировать строку");
        put("yesManBtn","Направление перехода");
        put("yesManDirection","Направление по умолчанию");
        put("comboSearch","Поиск с учетом регистра");
        put("findBtn","Поиск");
        put("filterBtn","Фильтры");
        put("downBtn","Переместить вниз");
        put("upBtn","Переместить вверх");
        put("showDelBtn","Показать удаленные значения");
        put("zebra","Зебра");
        put("rootRefUID","Корень (UID)");
        put("backgroundColorCol","Цвет фона");
        put("fontColorCol","Цвет шрифта");
        put("calcData","Формула");
        put("titleKaz","Заголовок (каз)");
        put("dynamicIfcExpr","Дин. интерфейс (выражение)");
        put("beforeOpen","Перед открытием");
        put("afterOpen","После открытия");
        put("beforeClose","Перед закрытием");
        put("afterClose","После закрытия");
        put("afterSave","После сохранения");
        put("afterTaskListUpdate","После обновления списка задач");
        put("onNotification","При получении уведомления");
        put("onMessageReceived","При получении сообщения");
        put("isVisible","Видимость");
        put("isVisibleNumRows","Видимость");
        put("createXml","В XML вид");
        put("maxObjectCount","Макс кол-во объектов");
        put("maxObjectCountMessage","Сообщение о макс кол-ве объектов");

        put("showEmpty","Отображать пустые узлы?");

        put("popup","Выпадающее меню");
        put("items","Пункты");
        put("renameNode","Переименовать");
        put("changeNode","Изменить");
        put("createNode","Создать");
        put("createAndBindNode","Создать и привязать");
        put("deleteNode","Удалить");
        put("expandNode","Раскрыть");
        put("collapseNode","Свернуть");
        put("treeFilter", "Фильтр для дерева");
        put("treeValueRef","Выбранное значение");

        put("valueRef","Значение");
        put("parentRef","Родитель");
        put("childrenRef","Дети");
        put("childrenExpr","Дети (формула)");
        put("hasChildrenRef","Атрибут есть дети?");
        put("rootExpr","Корень формула");
        put("rowNums","Номера строк");

        put("deleteOnType","Очищать старое значение");
        put("macros", "Запустить макрос");
        put("templatePassword", "Пароль для защиты документа");

        put("defaultButton", "Кнопка по умолчанию");
        put("showDateChooser", "Выбор даты из календаря");
        
        put("filterBtnAttr", "Формат задания фильтра");
        put("naviSeparator", "Разделитель");
        put("processes", "Процессы");
        put("hideRoot", "Скрыть корень");
        
        put("chart", "Диаграмма");
        put("typeChart", "Тип диаграммы");
        put("attr", "Атрибуты");
        // атрибуты для диаграмм: глобальные
        put("nameTaskSeries", "Наименование серии задач");
        put("leftVerticlLabel", "Левый вертикальный заголовок");
        put("upHorizontalLabel", "Верхний горизонтальный заголовок");
        put("dataChart", "Данные");
        put("isBtnSaveJPEG", "Кнопка сохранения");
        put("resolutionJPEG", "Разрешение картинки");
        // атрибуты для диаграмм: выделенные
        put("taskPath601", "Заголовок задачи"); // используется также в: GANTT_0
        put("startPath601", "Дата начала задачи"); // используется также в: GANTT_0
        put("endPath601", "Дата окончания задачи"); // используется также в: GANTT_0

        put("nameAttrSeries601", "Имя атрибута для наим. серии"); 
        put("countSeries601", "Количество серий задач"); 
        put("series", "Серии");
        put("taskSeries", "Атрибут серии");
        put("series1-601", "Заголовок 1 серии");
        put("series2-601", "Заголовок 2 серии");
        put("series3-601", "Заголовок 3 серии");
        put("series4-601", "Заголовок 4 серии");
        put("series5-601", "Заголовок 5 серии");
        put("series6-601", "Заголовок 6 серии");
        put("series7-601", "Заголовок 7 серии");
        put("series8-601", "Заголовок 8 серии");
        put("series9-601", "Заголовок 9 серии");
        put("series10-601", "Заголовок 10 серии");

        put("colors", "Цвет");
        put("colorControlPane", "Управляющая панель");
        put("colorChartPane", "Панель графика");
        put("colorChart", "Фон графика");
        put("colorCanvasChart", "Холст графика");
        put("transparencyChartBar", "Уровень прозрачности полос");
        put("completeFilling", "Размер холста по размеру панели");
        put("sizeG", "Размеры");
        put("sizeChart", "Размеры");
        put("extended", "Расширенные");
        put("gradient", "Градиентная заливка");
        put("transparent", "Прозрачность");
        put("countVisibleTask", "Кол-во отображ. задач");
        put("toolTip", "Всплывающая подсказка");
        put("hintTitle", "Подсказка");
                
        put("rotation", "Поворот");
        put("icon", "Иконка заголовка");
        put("formatting", "Разделение разрядов");
        put("editor", "Редактор");
        put("expandAll", "Развернуть при открытии");
        put("typeView", "Отобразить как");
        put("naviPane", "Панель навигации");
        put("naviPaneFilterPages", "Постраничная выборка");
        put("backgroundPict", "Фоновое изображение");
        put("positionPict", "Позиционирование фона");
        put("autoResizePict", "Автоподгонка размера фона");
        put("visibleArrow", "Отображать иконку");
        put("heightRow", "Высота строки");
        put("wClickAsOK", "Двойной клик как 'OK'");
        put("useCheck", "Использовать флажок выбора");
        put("useWYSIWYGforWEB", "Визуальный редактор в WEB");
        put("maxSize", "Максимальный размер файла");
        put("maxSize2", "Максимальный размер файла(Мб)");
        put("extensions", "Расширения файлов");
        put("inherit", "Наследовать");
        put("onlyLeaf", "Только листья");
        put("dontDependNull", "Не зависим от NULL");
        put("autoRefresh", "Автообновление");
        put("viewType", "Набор иконок");
        put("hideAfterClick", "Скрывать после нажатия ЛКМ");
        put("isShowAsMenu", "Показать как меню");
        put("web", "WEB");
        put("showOnTopPan", "Отобразить на верхней панели");
        put("positionOnTopPan", "Позиция на верхней панели");
        put("attention", "Внимание");
        put("countPanel", "Количество панелей"); 
        put("expandPanel", "Развернуть панель"); 
        
        put("showHeader", "Показать заголовок");
        put("collapsible", "Кнопка скрыть");
        put("refreshable", "Кнопка обновить");
        put("expandable", "Кнопка во весь экран");
        put("hideBreadCrumps", "Скрыть хлебные крошки");
        
        put("showUploaded", "Отображать загруженные файлы");
        put("tableViewType", "Вид таблицы");
        
        put("fastRepBtnProp","Быстрый отчёт");
        put("consalBtnProp","Объединение строк");
        put("addBtnProp","Добавить строку");
        put("delBtnProp","Удалить строку");
        put("copyRowsBtnProp","Копировать строку");
        put("yesManBtnProp","Направление перехода");
        put("yesManDirectionProp","Направление по умолчанию");
        put("findBtnProp","Поиск");
        put("filterBtnProp","Фильтры");
        put("downBtnProp","Переместить вниз");
        put("upBtnProp","Переместить вверх");
        put("showDelBtnProp","Показать удаленные значения");
        
        put("naviBtnText","Текст");
        put("naviBtnTooltip","Подсказка");
        put("naviBtnIcon","Иконка");
        put("tipForInput","Подсказка для ввода");
        
        put("linkPar","Зависимый параметр");
        put("attrParent","Родительский атрибут");
        put("attrChild","Дочерний атрибут");
        put("comment","Комментарий");
        put("map","Карта");
        put("layers","Описание слоев");
        put("bounds","Ограничения");
        put("selections","Выбор объектов");
        put("onSelect","Формула при выборе объекта");
        
        put("dataIntegrityControl","Контроль целостности данных");
        put("bitSeparation","Разделение разрядов");
        put("multipleFile","Мультивыбор файлов");
        
        put("analytic", "Аналитика");
        put("type", "Тип");
        put("axisnames", "Оси Формула");
        put("xAxis", "Измерители X");
        put("yAxis", "Измерители Y");
        put("zAxis", "Фильтр");
        put("firstXAxis", "Первый измеритель X");
        put("firstYAxis", "Первый измеритель Y");
        put("fact", "Таблица фактов");
        put("agg", "Агрегация");
        put("aggType", "Тип");
        put("aggField", "Поле");
        put("showLegend", "Показать легенду");
    }};

    public static String translate(String enStr) {
        // в регулярке идёт обработка аналогичных свойств с различными индексами
        String enStrP = enStr.replaceFirst("_\\d+$", "");
        String res = dict.get(enStrP);
        return (res != null) ? (enStr.length() == enStrP.length() ? res : res + enStr.replaceFirst("^.*_", " ")) : enStr;
    }
}
