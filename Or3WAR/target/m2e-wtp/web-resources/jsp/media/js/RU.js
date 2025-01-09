lbl=Array();
lbl['beforePageText'] = 'Стр.';
lbl['afterPageText']= "из {pages}";
lbl['displayMsg']= "{from}-{to}/{total}";
lbl['today']='Сегодня';
lbl['current']='Сейчас';
lbl['close']='Закрыть';
lbl['months'] = ['Янв', 'Фев', 'Март', 'Апр', 'Май', 'Июнь', 'Июль', 'Авг', 'Сент', 'Окт', 'Нояб', 'Дек'];
lbl['weeks'] = ['Вс', 'Пн', 'Вт', 'Ср', 'Чт', 'Пт', 'Сб'];


$.fn.panel.defaults.loadingMessage = "Подождите, идет загрузка...";
$.fn.datagrid.defaults.loadingMessage = "Подождите, идет загрузка...";
$.fn.datagrid.defaults.loadMsg="Данные загружаются...";
$.fn.treegrid.defaults.loadingMessage = "Подождите, идет загрузка...";
$.fn.treegrid.defaults.loadMsg="Данные загружаются...";
$.fn.pagination.defaults.displayMsg="{from}-{to} из {total}";
$.fn.pagination.defaults.beforePageText="Стр.";
$.fn.pagination.defaults.afterPageText="из {pages}";
$.fn.datagrid.defaults.orderNumber="п/п";
if ($.messager){
	$.messager.defaults.ok = 'Да';
	$.messager.defaults.cancel = 'Отмена';
}
