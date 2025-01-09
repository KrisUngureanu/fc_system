lbl=Array();
lbl['beforePageText'] = 'Бет.';
lbl['afterPageText']= "ішінде {pages}";
lbl['displayMsg']= "{from}-{to}/{total}";
lbl['today']='Бүгін';
lbl['current']='Қазір';
lbl['close']='Жабу';
lbl['months'] = ['Қаң', 'Ақп', 'Наур', 'Сәур', 'Мам', 'Мау', 'Шіл', 'Там', 'Қырк', 'Қаз', 'Қар', 'Желт'];
lbl['weeks'] = ['Жс', 'Дc', 'Сc', 'Ср', 'Бc', 'Жм', 'Сб'];


$.fn.panel.defaults.loadingMessage = "Күте тұрыңыз, жүктеу жүріп жатыр...";
$.fn.datagrid.defaults.loadingMessage = "Күте тұрыңыз, жүктеу жүріп жатыр...";
$.fn.datagrid.defaults.loadMsg="Мағлұматтар жүктелуде...";
$.fn.treegrid.defaults.loadingMessage = "Күте тұрыңыз, жүктеу жүріп жатыр...";
$.fn.treegrid.defaults.loadMsg="Мағлұматтар жүктелуде...";
$.fn.pagination.defaults.displayMsg="{from}-{to} ішінде {total}";
$.fn.pagination.defaults.beforePageText="Бет.";
$.fn.pagination.defaults.afterPageText="ішінде {pages}";
$.fn.datagrid.defaults.orderNumber="р/б";
if ($.messager){
	$.messager.defaults.ok = 'Иә';
	$.messager.defaults.cancel = 'Болдырмау';
}
