import {EventOps} from './events.js';
import {Util} from './util.js';
import {Translation} from './translation.js';
import {TooltipOps} from './tooltip.js';

export class TableOps {
	
	static heightRange = 40;
	
	editIndex = {};
	editingTable = undefined;
	focusedTable = undefined;
	
	selectedRow = [];
	clickedGrid = "";
	
	selectedTree = null;
	searchTreeId = null;
	searchTreeVal = null;
	foundNodes = null;
	blockDlg = false;
	
	firstShiftIndex = -1;
	lastShiftIndex = -1;

	_child = null;
	currentSelectedId = '';

	_searchText = null;
	_searchCount = 0;
	_lastId = -1;
	lastHiLitTreeElement = null;
	lastHiLitTreeTableElement = null;
	lastHighlightedNode = null;

	isOnCheck = true;
	nodeParent = false;
	tablesToReloadMap = {};

	constructor(app) {
		this.app = app;
	}
	
	initDefaults(ifc) {
		let _this = this;
		this._child = ifc;
		
		$("body").on('keypress', '.tree-search', function(e) {
			var key = e.which || e.keyCode;
			if (key === 13 || key === 10) {
				var treeId = $(this).attr('id').substring(1);
				var str = $(this).val();
				_this._lastId = -1;
				_this.searchTreeId = treeId;
				_this.searchTreeVal = str;
				_this.findNode(treeId, str, false);
			}
		});
		
		$("body").on('keypress', '.treefield-search', function(e) {
			var key = e.which || e.keyCode;
			if (key === 13 || key === 10) {
				if(!_this.blockDlg){
					var treeId = $(this).attr('id').substring(1);
					var str = $(this).val();
					_this._lastId = -1;
					_this.searchTreeId = treeId;
					_this.searchTreeVal = str;
					_this.findNode(treeId, str, true,  $(this).parent().parent());
				}
			}
		});

		$("body").on('keypress', '.treetable-search', function(e) {
			var key = e.which || e.keyCode;
			if (key === 13 || key === 10) {
				var treeId = $(this).attr('id').substring(1);
				var str = $(this).val();
				_this.lastFoundId = -1;
				_this.searchTreeId = treeId;
				_this.searchTreeVal = str;
				_this.findTreeTableNode(treeId, str);
			}
			
		});
		
		$("body").on('keyup', ".table-search", function(e) {
			if (e.which && e.which == 13) {
				var treeId = $(this).attr('id').substring(1);
				var str = $(this).val();
				_this.lastFoundId = -1;
				_this.searchTreeId = treeId;
				_this.searchTreeVal = str;
				_this.findTableNode(treeId, str);

				e.preventDefault();
				e.stopPropagation();

				return false;
			}
		});

		$.fn.datagrid.defaults.onLoadSuccess = function(rData) {
			var uid = $(this).attr("id");
			delete _this.tablesToReloadMap[uid];
			
			TooltipOps.addDatagridTooltip($(this));
		};
		
		$.fn.datagrid.defaults.onLoadError = function() {
			var uid = $(this).attr("id");
			delete _this.tablesToReloadMap[uid];
		};
		
		$.fn.datagrid.defaults.onBeforeLoad = function() {
			var isVisible = $(this).parents('.easyui-tabs').length == 0 // нету радительских табов
							|| ($(this).parents('.tabs-panels').length == $(this).parents('.easyui-tabs').length 				// или все табы распарсены 
							&& $(this).parents('.tabs-panels>.panel:visible').length == $(this).parents('.tabs-panels').length); // и видимы
		
			var uid = $(this).attr("id");
			
			if (isVisible && (_this.tablesToReloadMap[uid] == null || _this.tablesToReloadMap[uid] == 0)) {
				_this.tablesToReloadMap[uid] = 1;
				return true;
			} else {
				if (_this.tablesToReloadMap[uid] == null)
					_this.tablesToReloadMap[uid] = 0;
				return false;
			}
		};
		
		$.extend($.fn.datagrid.methods, {
			editCell: function(jq,p) {
				return jq.each(function(){
					var table = $(this);
					var realIndex = _this.globalIndex($(this), p.index);
					p.realIndex = realIndex;
					var fields = table.datagrid('getColumnFields',true).concat(table.datagrid('getColumnFields'));
					
					for (var i=0; i < fields.length; i++) {
						var colOpt = $(this).datagrid('getColumnOption', fields[i]);
						if (fields[i] == p.field) {
							var id = $(this).attr("id");

							var par = {};
							par["uid"] = id;
							par["row"] = realIndex;
							par["cuid"] = p.field;
							par["cmd"] = "set";
							par["com"] = "getEditor";
							
							_this.app.post(par).then(res => {
								if (res.tree) {
									_this.endEditing().then(() => {
										_this.openTree(id, res.title, realIndex, p.field);
									});
								} else if (res.popup) {
									_this.endEditing().then(() => {
										_this.openPopup(id, realIndex, p.field);
									});
								} else if (res.type) {
									let colOpt = table.datagrid('getColumnOption', p.field);
									colOpt.editor = res;

									table.datagrid('beginEdit', p.index);
									var col = $('[field="' + p.field + '"]');
									if (col.length > 0) {
										var ed = col.find('.numberbox .textbox-text');
										if (ed.length > 0) {
											ed.focusEnd();
										} else {
											ed = col.find('.datagrid-editable-input');
											if (ed.length > 0 && ed.attr('type') == 'checkbox') {
								                if (ed.prop("checked")) {
								                    ed.prop("checked", false);
								                } else
								                	ed.prop("checked", true);
								                _this.endEditing(id, p);
											} else if (ed.length > 0 && !ed.hasClass('combo-f') && (ed.attr('type') == 'text' || ed.prop("tagName") == 'TEXTAREA')) {
												ed.focusEnd();
											} else {
												ed = col.find('.combo-f');
												if (ed.length > 0) {
													col.find('.textbox-text').focus();
													ed.combo('showPanel');
												}
											}
										}
									}
								} else
		                			_this.endEditing();
							});
						} else {
							colOpt.editor1 = colOpt.editor;
							colOpt.editor = null;
						}
					}
				});
			},
			selectCell: function(jq, p){
				var panel = jq.datagrid('getPanel');
				$('.datagrid-body td.selected-cell', panel).removeClass('selected-cell');
				var td = $('.datagrid-body tr[datagrid-row-index='+p.index+'] td[field='+p.field+']',panel);
				td.addClass('selected-cell');
			},
			getCell: function(jq, p){
				var panel = jq.datagrid('getPanel');
				var td = $('.datagrid-body tr[datagrid-row-index='+p.index+'] td[field='+p.field+']',panel);
				return td;
			},
			getRow: function(jq, i){
				var panel = jq.datagrid('getPanel');
				var tr = $('.datagrid-body tr[datagrid-row-index='+i+']',panel);
				return tr;
			},
			getSelectedIndexes: function(jq, p){
				var panel = jq.datagrid('getPanel');
				var trs = $('.datagrid-body tr.datagrid-row-selected',panel);
				var indxs = [];
				$.each(trs, function(i, tr) {
					indxs[i] = parseInt($(tr).attr('datagrid-row-index'));
				});
				return indxs;
			}
		});
		
		$.extend($.fn.treegrid.methods, {
			editCell: function(jq,p){
				return jq.each(function() {
					var doEdit = false;
					var table = $(this);
					
					var fields = $(this).treegrid('getColumnFields', true)
							.concat($(this).treegrid('getColumnFields'));
					for (var i = 0; i < fields.length; i++) {
						var colOpt = $(this).treegrid('getColumnOption', fields[i]);
						if (fields[i] == p.field) {
							// если это первый столбец, то раскрыть дерево
							if (i == 0) {
								$(this).treegrid( p.state == 'open' ? 'collapse' : 'expand', p.index);
							} else {
								var id = $(this).attr("id");
								var par = {};
								par["uid"] = id;
								par["row"] = p.index;
								par["cuid"] = p.field;
								par["cmd"] = "set";
								par["com"] = "getEditor";
								
								_this.app.post(par).then(res => {
									if (res.tree) {
										_this.endEditing().then(() => {
											_this.openTree(id, res.title, p.index, p.field);
										});
									} else if (res.popup) {
										_this.endEditing().then(() => {
											_this.openPopup(id, p.index, p.field);
										});
									} else if (res.type) {
										colOpt = table.datagrid('getColumnOption', p.field);
										colOpt.editor = res;

										table.treegrid('beginEdit', p.index);
										var col = $('[field="' + p.field + '"]');
										if (col.length > 0) {
											var ed = col.find('.numberbox .textbox-text');
											if (ed.length > 0) {
												ed.focusEnd();
											} else {
												ed = col.find('.datagrid-editable-input');
												if (ed.length > 0 && ed.attr('type') == 'checkbox') {
									                if (ed.prop("checked")) {
									                    ed.prop("checked", false);
									                } else
									                	ed.prop("checked", true);
									                _this.endEditing();
												} else if (ed.length > 0 && !ed.hasClass('combo-f') && (ed.attr('type') == 'text' || ed.prop("tagName") == 'TEXTAREA')) {
													ed.focusEnd();
												} else {
													ed = col.find('.combo-f');
													if (ed.length > 0) {
														col.find('.textbox-text').focus();
														ed.combo('showPanel');
													}
												}
											}
										}
									} else
			                			_this.endEditing();
								});
							}
						} else {
							colOpt.editor1 = colOpt.editor;
							colOpt.editor = null;
						}

					}					
				});
			},
			getSelectedIndexes: function(jq, p){
				var panel = jq.treegrid('getPanel');
				var trs = $('.datagrid-view2 tr.datagrid-row-selected',panel);
				var indxs = [];
				$.each(trs, function(i, tr) {
					indxs[i] = $(tr).attr('node-id');
				});
				return indxs;
			},
			getCell: function(jq, p){
				var panel = jq.datagrid('getPanel');
				var td = $('.datagrid-body tr[node-id='+p.index+'] td[field='+p.field+']',panel);
				return td;
			},
			selectCell: function(jq, p){
				var panel = jq.datagrid('getPanel');
				$('.datagrid-body td.selected-cell', panel).removeClass('selected-cell');
				var td = $('.datagrid-body tr[node-id='+p.index+'] td[field='+p.field+']',panel);
				td.addClass('selected-cell');
				var par = {};
				par["uid"] = $(jq).attr('id');
				par["com"] = 'sct';
				par["cuid"] = p.field;
				par["cmd"] = "set";
				loadData(par, true);
			}
		});
		
		$.fn.datagrid.defaults.onClickRow = function(rIndex, rData) {
			ifc.hidePopUp($(this), "#app");
			var tbl = $(this);
			
			_this.endEditing().then(() => {
				var objs = tbl.datagrid('getSelections');
				var indxs = [];
				for (var i = 0; i < objs.length; i++) {
					indxs[i] = tbl.datagrid('getRowIndex', objs[i]);
				}
				
				var index = _this.globalIndex(tbl, rIndex);
				var clickIndex = index;
				var val = '';
				var rowUnselecting = true;
				for (var i=0; i<indxs.length; i++) {
					var indx = indxs[i];
					indxs[i] = _this.globalIndex(tbl, indxs[i]);
		
					if (indxs[i] != index) {
						if (EventOps.ctrlPressed) {
							val += ',' + indxs[i];
						} else {
							tbl.datagrid('unselectRow', indx);
						}
					} else {
						rowUnselecting = false;
					}
				}
				if (rowUnselecting && indxs.length > 0) {
					if (val.length > 0 && val != '') {
						val = val.substring(1);
					} else {
						val = index + '';
					}
					index = indxs[0];
				} else {
					val = index + val;
				}
				
				if (_this.firstShiftIndex == -1 || !EventOps.shiftPressed){
					_this.firstShiftIndex = _this.lastShiftIndex = clickIndex;
					if(!EventOps.ctrlPressed)
						val = _this.lastShiftIndex + '';
				}
				if (indxs.length == 0){
					_this.firstShiftIndex = _this.lastShiftIndex = -1;
					val = '';
				}
				
				if (tbl.datagrid('getSelections').length == 0 && !EventOps.ctrlPressed & !EventOps.shiftPressed) {
					tbl.datagrid('selectRow', rIndex);
					_this.firstShiftIndex = _this.lastShiftIndex = _this.globalIndex(tbl, rIndex);
					val = _this.lastShiftIndex + '';
				}
				
				if(EventOps.shiftPressed && _this.firstShiftIndex != -1 /*&& clickIndex != _this.firstShiftIndex*/){
					val = '';
					_this.lastShiftIndex = clickIndex;
					var i = _this.firstShiftIndex;
					while(_this.lastShiftIndex>_this.firstShiftIndex?i<=_this.lastShiftIndex:i>=_this.lastShiftIndex){
						tbl.datagrid('selectRow', i);
						val += ',' + i;				
						_this.lastShiftIndex>_this.firstShiftIndex?i++:i--;
					}
					if(index > -1){
						val = val.substring(1);
					}
				}
				
				_this.setSelection(tbl, val, clickIndex);
		
				var toolbar_sel = tbl.parents(".datagrid").find(".datagrid-toolbar:first .datagrid-sel");
				$(toolbar_sel).html((index+1)+"&nbsp;/");
		
				var panel = tbl.datagrid('getPanel').panel('panel');
				panel.attr('tabindex', 0);
				panel.focus();
				_this.focusedTable = tbl.attr('id');
				panel.unbind('keydown').bind('keydown',function(event) {
					
					if (event.which && event.which == 38 && EventOps.shiftPressed && _this.firstShiftIndex != -1) {
						if(_this.firstShiftIndex < _this.lastShiftIndex){
							tbl.datagrid('unselectRow', _this.lastShiftIndex);
							_this.lastShiftIndex--;
							val = val.substring(0, val.length - 2);
						} else if(_this.lastShiftIndex > 0){
							if(_this.firstShiftIndex == _this.lastShiftIndex){
								tbl.datagrid('clearSelections');
								tbl.datagrid('selectRow', _this.lastShiftIndex);
							}
							_this.lastShiftIndex--;
							tbl.datagrid('selectRow',_this.lastShiftIndex);
							val+= "," + _this.lastShiftIndex;
						}
						_this.setSelection(tbl, val, index);
						return false;
					} else if (event.which && event.which == 40 && EventOps.shiftPressed && _this.firstShiftIndex != -1) {
						var rowCount = tbl.datagrid('getRows').length;
						if(_this.firstShiftIndex > _this.lastShiftIndex){
							tbl.datagrid('unselectRow', _this.lastShiftIndex);
							_this.lastShiftIndex++;
							val = val.substring(0, val.length - 2);
							
						} else if(_this.lastShiftIndex < rowCount - 1){
							if(_this.firstShiftIndex == _this.lastShiftIndex){
								tbl.datagrid('clearSelections');
								tbl.datagrid('selectRow', _this.lastShiftIndex);
							}
							_this.lastShiftIndex++;
							tbl.datagrid('selectRow',_this.lastShiftIndex);
							val+= "," + _this.lastShiftIndex;
						}
						_this.setSelection(tbl, val, index);
						return false;
					} else if (event.which && event.which == 38 && !EventOps.shiftPressed) {
						if(_this.firstShiftIndex != _this.lastShiftIndex){
							tbl.datagrid('clearSelections');
							tbl.datagrid('selectRow', _this.lastShiftIndex);
						}
						_this.firstShiftIndex = _this.lastShiftIndex = tbl.datagrid('getRowIndex', tbl.datagrid('getSelected')) - 1;
						val = _this.lastShiftIndex + '';
						return selectRow($(this), true);
					} else if (event.which && event.which == 40 && !EventOps.shiftPressed) {
						if(_this.firstShiftIndex != _this.lastShiftIndex){
							tbl.datagrid('clearSelections');
							tbl.datagrid('selectRow', _this.lastShiftIndex);
						}
						_this.firstShiftIndex = _this.lastShiftIndex = tbl.datagrid('getRowIndex', tbl.datagrid('getSelected')) + 1;
						val = _this.lastShiftIndex + '';
						return _this.selectRow($(this), false);
					} else if (event.which && event.which == 37) {
						return _this.selectColumn($(this), true);
					} else if (event.which && event.which == 39) {
						return _this.selectColumn($(this), false);
					} else if (event.which && event.which == 32) {
						return _this.beginEditCell($(this));
					}
				});
			});
		};
		
		$.fn.treegrid.defaults.onClickRow = function(rIndex, rData) {
			_this.selectedTree = $(this);
			ifc.hidePopUp($(this),"#app");
			var tbl = $(this);
			
			var singlSel = tbl.treegrid('options').singleSelect;
			if(!singlSel){
				var checked = tbl.treegrid('getCheckedNodes');
				if ($.inArray(rIndex,checked) >= 0) {
					tbl.treegrid('uncheckNode', rIndex.id);
				} else {
					tbl.treegrid('checkNode', rIndex.id);
				}
				return;
			}

			_this.endEditing().then(() => {
				var indxs = tbl.treegrid('getSelectedIndexes');
				var index = rIndex.id;
				var val = '';
				var rowUnselecting = true;
				for (var i=0; i<indxs.length; i++) {
					if (indxs[i] != index) {
						if (EventOps.ctrlPressed) {
							val += ',' + indxs[i];
						} else {
							tbl.treegrid('unselectRow', indxs[i]);
						}
					} else {
						rowUnselecting = false;
					}
				}
				if (rowUnselecting && indxs.length > 0) {
					if (val.length > 0) val = val.substring(1);
					index = indxs[0];
				} else {
					val = index + val;
				}
				if (indxs.length == 0) {
					tbl.treegrid('selectRow', rIndex.id);
				}
		
				if (_this.selectedRow[tbl.attr("id")] != rIndex) {
					_this.clickedGrid = tbl.attr("id");
					_this.selectedRow[tbl.attr("id")] = rIndex;
				}
				var oldVal = tbl.attr('selRows');
				if (oldVal != val) {
					ifc.setValue(tbl.attr('id'), val);
					tbl.attr('selRows', val);
				} else {
					var id = tbl.attr('id');
					if (id != null && id != _this.currentSelectedId) {
						_this.currentSelectedId = id;
						var par = {"cmd":"fcs","uid":id};
						_this.app.post(par);
					}
				}
				
				var panel = tbl.datagrid('getPanel').panel('panel');
				panel.attr('tabindex', 0);
				panel.focus();
				_this.focusedTable = tbl.attr('id');
				panel.unbind('keydown').bind('keydown',function(event) {
					if (event.which && event.which == 38) {
				        return selectRowt($(this), true);
					} else if (event.which && event.which == 40) {
				        return selectRowt($(this), false);
					} else if (event.which && event.which == 37) {
				        return selectColumnt($(this), true);
					} else if (event.which && event.which == 39) {
				        return selectColumnt($(this), false);
					}
				});
			});		
		};

		$.fn.treegrid.defaults.onCheckNode = function(row, checked){
			var tbl = $(this);
			var singlSel = tbl.treegrid('options').singleSelect;
			if(!singlSel){
				_this.selectedTree = $(this);
				ifc.hidePopUp($(this), "#ui-panel");	
				
				_this.endEditing().then(() => {
					var index = row.id;
					if(checked){
						tbl.treegrid('selectRow', index);
					} else {
						tbl.treegrid('unselectRow', index);
					}
					
					if (_this.selectedRow[tbl.attr("id")] != row) {
						_this.clickedGrid = tbl.attr("id");
						_this.selectedRow[tbl.attr("id")] = row;
					}
					var val = '';
					var checkedNodes = tbl.treegrid('getCheckedNodes');
					for(var i = 0; i < checkedNodes.length; i++){
						val += ',' + checkedNodes[i].id;
					}
					val = val.substring(1);
					var oldVal = tbl.attr('selRows');
					if(oldVal != val){
						ifc.setValue(tbl.attr('id'), val);
						tbl.attr('selRows', val);
					} else {
						var id = tbl.attr('id');
						if (id != null && id != _this.currentSelectedId) {
							_this.currentSelectedId = id;
							var par = {"cmd":"fcs","uid":id};
							_this.app.post(par);
						}
					}
					var panel = tbl.datagrid('getPanel').panel('panel');
					panel.attr('tabindex', 0);
				});
			}
		}

		$.fn.treegrid.defaults.onLoadSuccess = function(rData) {
			if (rData == null) {
				var tbl = $(this);
				_this.paintTreegridRows($(this).attr('id'));
				var sel = tbl.attr('selRows');
				var singleSel = tbl.treegrid('options').singleSelect;
				if (sel && sel != 0) {
					var rows = sel.split(',');
					var root = tbl.treegrid('getRoot');
					if(rows.length > 1 || (root && rows[0] != root.id)){
						for (var ind = 0; ind < rows.length; ind++) {
							if ($(this).treegrid('find', rows[ind])) {
								$(this).treegrid('select', rows[ind]);
								if(!singleSel){
									tbl.treegrid('checkNode', rows[ind]);
								}
							}
						}
					}
				}
			}
		};
		
		$.fn.datagrid.defaults.onDblClickCell = function(index, field) {
			var cell = $(this).datagrid('getPanel').find('div.datagrid-body td[field = ' + field +'] div.datagrid-cell:not(:empty)')[index];
			$(cell).tooltip('destroy');
			var id = $(this).attr("id");
			var opt = $(this).datagrid('getColumnOption', field);
			var realIndex = _this.globalIndex($(this), index);
			if(opt && opt.editor && opt.editor == 'textarea') {
				var fields = $(this).datagrid('getColumnFields',true).concat($(this).datagrid('getColumnFields'));
				for(var i=0; i<fields.length; i++) {
					if (fields[i] == field) {
						var par = {"cmd":"set", "com":"getEditor"};
						par["uid"] = id;
						par["row"] = realIndex;
						par["cuid"] = field;
						
						_this.app.post(par).then(res => {
							var doTextareaColumn = false;
							var isShowTextAsXML = false; 
							if (res.type) {
								doTextareaColumn = false;
							} else {
								doTextareaColumn = true;
							}
							if (res.showTextAsXML) {
								isShowTextAsXML = res.showTextAsXML == "1";
							}
							
							if (doTextareaColumn) {
								var text = $('.datagrid-body tr[datagrid-row-index='+index+'] td[field='+field+']').children().text();
								if(text != "") {
									var dialogId = 'or3_popup' + _this.popupcount;
									$('#trash').append($("<div></div>").attr('id', dialogId));
									_this.dialogResult[dialogId] = '1';
						
									var buttonOk = {
										text: Translation.translation['ok'],
										handler: function() {
											_this.dialogResult[dialogId] = '0';
											$("#" + dialogId).dialog('destroy');
										}
									};
									
									var buttons = [buttonOk];
										
									var oldZindex = $.fn.window.defaults.zIndex;
									$.fn.window.defaults.zIndex = ifc.dialogZindex++;
						
									$('#' + dialogId).dialog({
										title: opt.title,
										width: 600,
										height: 400,
										closed: true,
										cache: false,
										closable: true,
										modal: true,
										onLoad: function() {
											$('#' + dialogId).parent().find('.panel-title').css({'height' : 'auto'});
											InterfaceController.popDlg.push(dialogId);
											InterfaceController.popDlgType.push(Util.DLG_NO_SEND);
										},
										onClose: function() {
											_this.dialogResult[dialogId] = '1';
											$("#" + dialogId).dialog('destroy');
										},
										onBeforeDestroy : function() {
								        	$.fn.window.defaults.zIndex = oldZindex;
								        	if(dialogId == InterfaceController.popDlg[InterfaceController.popDlg.length-1]){
								        		InterfaceController.popDlg.pop();
									        	InterfaceController.popDlgType.pop();
								        	}
										},
										buttons: buttons
									});
									if (isShowTextAsXML) {
										$('#' + dialogId).text(text.replace(/\n/g, '<br/>'));
									} else {
										$('#' + dialogId).html(text.replace(/\n/g, '<br/>'));
									}
									$('#' + dialogId).dialog("open");
									_this.popupcount++;
								}
							}
						});
					}
				}
			}
			if (opt && (opt.editor == null || opt.editor.type != 'checkbox')) {
				_this.endEditing().then(() => {
					_this.editingTable = id;
					_this.editIndex[id] = {index: index, field: field, realIndex: realIndex};
					$('#' + id).datagrid('editCell', {index:index,field:field});
				});
			}
		};
		
		$.fn.datagrid.defaults.onClickCell = function(index, field) {
			var realIndex = _this.globalIndex($(this), index);
			$(this).attr('selectedCol', field);
			$(this).datagrid('selectCell', {index:index,field:field});
			var id = $(this).attr("id");
			var opt = $(this).datagrid('getColumnOption', field);
			if (opt && opt.editor && opt.editor.type == 'checkbox') {
				_this.endEditing().then(() => {
					_this.editingTable = id;
					_this.editIndex[id] = {index: index, field: field, realIndex: realIndex};
					$('#' + id).datagrid('editCell', {index:index,field:field});
				});
			}
		};

		$.fn.treegrid.defaults.onDblClickCell = function(field, props) {
			var tbl = $(this);
			var id = tbl.attr("id");

			_this.endEditing().then(() => {
				_this.editingTable = id;
				_this.editIndex[id] = {index: props.id, field: field, realIndex: props.id};
				tbl.treegrid('editCell', {index: props.id, field: field, state: props.state});
			});
		};

		$.extend($.fn.datagrid.defaults.editors, {
		    file: {  
		        init: function(container, options){
		        	if (options != undefined && options['uid']) {
		        		if (options['action'] == "DOC_UPDATE") {
			        		var input = $('<span class="btn btn-small fileinput-button" style="margin-right:2px;margin-top:2px; ">'
				        		+'<i class="fam-attach"></i>'
				        		+'<span class="btn-label" style="font-size:12px;font-weight:bold;">Загрузить</span>'
				        		+'<input class="or3-file-upload" type="file" '
				        		+'data-url="' + _this.app.restUrl + '&uid='+options['uid']+'&row='+options['row']+'" name="file">'
				        		+'</span>')
				        		.appendTo(container);
			        		
			                ifc.preparefileUpload();
			                return input;
		        		} else if (options['action'] == "DOC_EDIT") {
		        		}
		        		return $('<span></span>').appendTo(container);
		        	} else {
		        		return $('<span></span>').appendTo(container);
		        	}
		        },
		        getValue: function(target){
		        },  
		        setValue: function(target, value){
		        }
		    },
		    checkbox: {  
		        init: function(container, options){  
		            var input = $('<input type="checkbox" class="datagrid-editable-input">').appendTo(container);  
		            input.data('checkbox',{options:options});
		            return input;  
		        },
		        getValue: function(target){  
		            var check = $(target).is(':checked');
		            if (typeof check !== 'undefined' && check !== false) {
		               
		                return "x";
		            } else {
		                return "";
		            }
		        },  
		        setValue: function(target, value){
		            var opts = $(target).data('checkbox').options;		
		            if (opts.callback) value = opts.callback.call(target, value);
		            if (value=="x") {
		                $(target).attr("checked","checked");
		            } else {
		                $(target).removeAttr("checked");
		            }
		            $(target).val(value);  
		        }
		    },
			combobox: {
				init: function(container, options){  
					options['formatter'] = comboTableFormat;
					options['width'] = container.parent().width();
					var input = $('<input type="text" class="datagrid-editable-input">').appendTo(container);
					return input.combobox(options);
		        },
		        getValue: function(target){
		            return $(target).combobox('getValue');
		        },  
		        setValue: function(target, value){
		            $(target).combobox('setValue', value);
		        }
			},
		    datehhmmEditor : {
		        init : function(container, opts) {
		            var input = $('<input  type="text" class="datagrid-editable-input" id="datehhmmEditor" >').appendTo(container);
		            return input;
		        },
		        setValue: function(target, value) {
		            $('#datehhmmEditor').inputmask("d.m.y h:s").val(value);
		        },
		        getValue: function(target) {
		            return $('#datehhmmEditor').val();
		        }
		    },
		    datehhmmssEditor : {
		        init : function(container, opts) {
		            var input = $('<input  type="text" class="datagrid-editable-input" id="datehhmmssEditor" >').appendTo(container);
		            return input;
		        },
		        setValue: function(target, value) {
		            $('#datehhmmssEditor').inputmask("d.m.y h:s:s").val(value);
		        },
		        getValue: function(target) {
		            return $('#datehhmmssEditor').val();
		        }
		    },
		    hhmmEditor : {
		        init : function(container, opts) {
		            var input = $('<input  type="text" class="datagrid-editable-input" id="hhmmEditor" >').appendTo(container);
		            return input;
		        },
		        setValue: function(target, value) {
		            $('#hhmmEditor').inputmask("h:s").val(value);
		        },
		        getValue: function(target) {
		            return $('#hhmmEditor').val();
		        }
		    },
		    datebox : {
		        init : function(container, opts) {
		            var input = $('<input type="text" id="cellDatabox" />').appendTo(container);
		            return input;
		        },
		        setValue: function(target, value) {
		            var dateParts = value.split(".");
		            var date = new Date(dateParts[2], (dateParts[1] - 1), dateParts[0]);
					$('#cellDatabox').datebox().combo('textbox').inputmask("d.m.y", {
						"placeholder": "дд.мм.гггг",
						"insertMode" : false
					});
		            if (isNaN(date.getDay())) {
		            	$('#cellDatabox').datebox('setValue', null);
		            } else {
		            	$('#cellDatabox').datebox('setValue',value);
		            }
		        },
		        getValue: function(target) {
		            return $('#cellDatabox').combo('textbox').val();
		        }
		    },
		    datetimebox : {
		    	secs : false,
		        init : function(container, opts) {
		            secs = opts != null && opts.showSeconds;
		            var input = $('<input type="text" id="cellDatabox" ' + (secs ? '' : 'data-options="showSeconds:false"') + '/>').appendTo(container);
		            return input;
		        },
		        setValue: function(target, value) {
		        	var date = null;
		    		var dateTimeParts = value.split(" ");
		    		if (dateTimeParts.length > 1) {
		    			var s1 = dateTimeParts[0];
		    			var s2 = dateTimeParts[1];
		    			var dateParts = s1.split(".");
		    			var y = -1;
		    			var m = -1;
		    			var d = -1;
		    			var h = 0;
		    			var min = 0;
		    			var sec = 0;
		    			if (dateParts.length > 2) {
		    				y = dateParts[2];
		    				m = dateParts[1] - 1;
		    				d = dateParts[0];
		    			}
		    			var timeParts = s2.split(":");
		    			if (timeParts.length > 0) {
		    				h = timeParts[0];
		    			}
		    			if (timeParts.length > 1) {
		    				min = timeParts[1];
		    			}
		    			if (timeParts.length > 2) {
		    				sec = timeParts[2];
		    			}
		    			date = new Date(y, m, d, h, min, sec);
		
		    			if (secs) {
							$('#cellDatabox').datetimebox().combo('textbox').inputmask("d.m.y h:s:s", {
								"placeholder": "дд.мм.гггг чч:ММ:сс",
								"insertMode" : false
							});
		    			} else {
							$('#cellDatabox').datetimebox().combo('textbox').inputmask("d.m.y h:s", {
								"placeholder": "дд.мм.гггг чч:ММ",
								"insertMode" : false
							});
		    			}
		    		}
		            if (date == null || isNaN(date.getDay())) {
		            	$('#cellDatabox').datetimebox('setValue', null);
		            } else {
		            	$('#cellDatabox').datetimebox('setValue',value);
		            }
		        },
		        getValue: function(target) {
		            return $('#cellDatabox').combo('textbox').val();
		        }
		    },
		    textareaNotReady : {
		    	 init : function(container, opts) {
		             var input = $('<input  type="text" class="datagrid-editable-input" id="hhmmEditor" >').appendTo(container);
		             return input;
		         },
		         setValue: function(target, value) {
		             $('#hhmmEditor').inputmask("h:s").val(value);
		         },
		         getValue: function(target) {
		             return $('#hhmmEditor').val();
		         }
		    },
		    intfield: {  
		        init: function(container, opts) {
		        	var w = container.innerWidth();
		            var input = $('<input type="text" class="datagrid-editable-input" />').appendTo(container);
		            input.width(w - 10);
		            
		            if (opts.maxlength != null && opts.maxlength > 0)
		            	input.attr("maxlength", opts.maxlength);
		            
		            input.attr("include", opts.include);
		            input.attr("exclude", opts.exclude);
		            
		            if ((opts.include != null && opts.include.length > 0) ||
		            		(opts.exclude != null && opts.exclude.length > 0)) {
			            input.bind('keypress', function(e) {
			            	return validChars(this, e);
			            });
		            }
		            return input;  
		        },
		        getValue: function(target){  
		            return $(target).val();
		        },  
		        setValue: function(target, value){
		            $(target).val(value);  
		        }
		    },
		    floatfield: {
		    	init: function(container, opts) {
		        	var w = container.innerWidth();
		            var input = $('<input type="text" class="datagrid-editable-input" />').appendTo(container);
		            input.width(w - 10);
		            
		            return input;  
		        },
		        getValue: function(target){  
		            return $(target).val();
		        },  
		        setValue: function(target, value){
		            $(target).val(value);  
		        }
		    },
		    text: {
		        init: function(container, options) {
		    		var upperAllChars = false;
		    		var upperCase = false;
		        	if (options != undefined) {
		        		if (options['upperAllChars']) {
		        			upperAllChars = options['upperAllChars'];
		        		}
		        		if (options['upperCase']) {
		        			upperCase = options['upperCase'];
		        		}
		        		
		        	}
		        	var w = container.innerWidth();
		            var input = $('<textarea type="text" class="datagrid-editable-input" id="txtEditor"' + ((upperAllChars || upperCase) ? ' onkeyup="changeToUpperCase(this, event, ' + upperAllChars + ', ' + upperCase + ')"' : '') + '>').appendTo(container);
		            input.width(w - 10);
		            return input;
		        },
		        getValue: function(target){
		            return $(target).val();
		        },  
		        setValue: function(target, value){
		            $(target).val(value);  
		        }
		    }
		});
		
		$.fn.tree.defaults.onSelect = function(node, checked) {
			var nodeCheck = true;
			if (!$(this).hasClass("nochange")) {
				var tree = $(this).tree('options');
				var multi2 = tree.multiSelect;
				var multi = _this.getOption($(this), 'checkbox');
				if(multi2 == true || multi == "true") {
					var nodes = $(this).tree('getChecked');
					_this.isOnCheck = true;
					if(node.parent != 0) {
					    for (var i = 0; i<nodes.length; i++) {
					    	if (node.target == nodes[i].target) {
					    		$(this).tree('uncheck', node.target);
					    		nodeCheck = false;
					    	}
					    }
					} else {
						if(!_this.nodeParent) {
							nodeCheck = true;
							_this.nodeParent = true;
						} else {
							$(this).tree('uncheck', node.target);
							nodeCheck = false;
				    		_this.nodeParent = false;
						}
					}
				    if (nodeCheck) $(this).tree('check', node.target);
				    _this.isOnCheck = false;
				} else {
					_this.setValue($(this).attr('id'), node.id);
				}
			}
			
			var tmp = $(this).attr('selectFolder');
			var canSelectFolder = tmp == null || tmp == 'true';
			
			if (!canSelectFolder) {
				var isFolder = $(node.target).find('.tree-folder').length > 0;
				var dlg = $(node.target).closest('.panel');
				
				if (isFolder) {
					dlg.find(".dialog-button .l-btn").first().linkbutton('disable');
				} else {
					dlg.find(".dialog-button .l-btn").first().linkbutton('enable');
				}
			}
		
		};
		$.fn.tree.defaults.onCheck = function(node, checked) {
			var tree = $(this).tree('options');
			
			var multi2 = tree.multiSelect;
			var multi = _this.getOption($(this), 'checkbox');
			if ((multi2 == true || multi == "true") && _this.isOnCheck){
			    var ids = "";
			    var nodes = $(this).tree('getChecked');
			    for (var i = 0; i<nodes.length; i++) {
			    	ids += nodes[i].id + ",";
			    }
			    if (ids.length > 0){
			    	ids = ids.substring(0, ids.length-1);
			    }
				_this.setValue($(this).attr('id'), ids);
			}
		};
		
		$.fn.treegrid.defaults.onContextMenu = function(e,node){
			e.preventDefault();
			_this.selectedTree = $(this);
			$(this).treegrid('select', node.id);
			$('#treeTableMenu').menu('show',{
				left: e.pageX,
				top: e.pageY
			});
		};
		$.fn.tree.defaults.onContextMenu = function(e,node){
			e.preventDefault();
			_this.selectedTree = $(this);
			_this.selectedTree.tree('select', node.target);
			var viewType=_this.selectedTree.attr('viewType');
			var sortType=_this.selectedTree.attr('sortType');
			var pUp=false;
			var pDown=false;
			if(sortType==1){
				var nodep=_this.selectedTree.tree('getParent',node.target);
				if (nodep!=null){
					var chs=_this.selectedTree.tree('getChildren',nodep.target);
					if (chs!=null && chs.length>0){
						var chFirst=chs[0];
						if(node.id!=chFirst.id)
							pUp=true;
						var chLast=chs[chs.length-1];
						if(node.id!=chLast.id)
							pDown=true;
					}
				}
			}
			if(pUp) 
				$('#treeMenu').menu('enableItem', $('#moveUp'));
			else
				$('#treeMenu').menu('disableItem', $('#moveUp'));
			if(pDown)
				$('#treeMenu').menu('enableItem', $('#moveDown'));
			else
				$('#treeMenu').menu('disableItem', $('#moveDown'));
			$('#treeMenu').menu('show',{
				left: e.pageX,
				top: e.pageY
			});
		};
	}
	
	selectTableRows(t, rs) {
		t.attr('selRows', rs);
	
		var count = t.datagrid('getRows') != null ? t.datagrid('getRows').length : 0;
		if (count > 0) {
			t.datagrid('clearSelections');
			if (rs) {
				var rows = rs.split(',');
				var count = t.datagrid('getRows').length;    // row count
				
				for (var ind=0; ind<rows.length; ind++) {
					rows[ind] = this.pageIndex(t, rows[ind]);
				    if (rows[ind] >= 0 && rows[ind] < count) {
				        t.datagrid('selectRow', rows[ind]);
				    }
				}
				var selCol = t.attr('selectedCol');
				if (selCol != null) {
					t.datagrid('selectCell', {index:rows[0],field:selCol});
				}
				var toolbar_sel = $(t).parents(".datagrid").find(".datagrid-toolbar:first .datagrid-sel");
				$(toolbar_sel).text((parseInt(rows[0])+1)+" /");
			}
		}
	}
	
	globalIndex(t, index) {
		var res = index;
		if (t.datagrid('getPager').length > 0) {
			res += (t.datagrid('getPager').find('.pagination-num').val() - 1) * t.datagrid('getPager').find('.pagination-page-list').val();
		}
		return res;
	}
	
	pageIndex(t, index) {
		var res = index;
		if (t.datagrid('getPager').length > 0) {
			res -= (t.datagrid('getPager').find('.pagination-num').val() - 1) * t.datagrid('getPager').find('.pagination-page-list').val();
		}
		return res;
	}
	
	getEditing() {
		if (this.editingTable == undefined) return null;
		
		var id = this.editingTable;
		if (this.editIndex[id] == undefined) return null;
	
		return {id: id, edIndex: this.editIndex[id]};
	}
	
	restoreEditing(pars) {
		var t = $("#" + pars.id);
	
		if (t.length > 0) {
			var ed = t.datagrid('getEditor', {index:pars.edIndex.index, field:pars.edIndex.field});
	
			if (ed == null || ed.target == null) {
				t.datagrid('editCell', {index:pars.edIndex.index, field:pars.edIndex.field});
			} 
		}
	}

	endEditing(tableId, columnId) {
		let _this = this;
		if ((tableId == undefined || tableId == null) && this.editingTable == undefined) 
			return new Promise((resolve, reject) => {resolve()});
		
		var id = tableId || this.editingTable;
		if ((columnId == undefined || columnId == null) && this.editIndex[id] == undefined)
			return new Promise((resolve, reject) => {resolve()});
	
		var edIndex = columnId || this.editIndex[id];
		
		this.editIndex[id] = undefined;
		this.editingTable = undefined;
	
		var t = $("#" + id);

		return new Promise((resolve, reject) => {
			if (t.length > 0) {
				var ed = t.datagrid('getEditor', {index:edIndex.index,field:edIndex.field});
				var val = "";
				if (ed && ed.type == 'combobox') {
					var title = $(ed.target).combobox('getText');
					if (t.datagrid('getRows').length > 0)
						t.datagrid('getRows')[edIndex.index][edIndex.field + '-title'] = title;
					else
						t.datagrid('getSelected')[edIndex.field + '-title'] = title;
					val = $(ed.target).combobox('getValue');
				} else if (ed && ed.type == 'datebox') {
					val = $(ed.target).datebox('getValue');
				} else if (ed && ed.type == 'datetimebox') {
					val = $(ed.target).datetimebox('getValue');
				} else if (ed && ed.type == 'checkbox') {
					val = $(ed.target).get(0).checked;
				} else if (ed && ed.type == 'or3checkbox') {
					val = $(ed.target).get(0).checked;
				} else if (ed && ed.type == 'numberbox') {
					val = $(ed.target).next().children().first().val();
				} else if (ed) {
					val = $(ed.target).val();
				}
				
				if (ed && ed.target) {
				    _this.showChangeMsg();
					var par = {};
					par["uid"] = id;
					par["val"] = val;
					par["row"] = edIndex.realIndex;
					par["cuid"] = edIndex.field;
					par["cmd"] = "set";
		
					var count = t.datagrid('getRows').length;
				    var selected = t.datagrid('getSelected');
				    var selRow = 0;
				    if (selected) {
				    	selRow = t.datagrid('getRowIndex', selected);
				    }
					t.datagrid('endEdit', edIndex.index);
				    if (selRow >= 0 && selRow < count) {
				        t.datagrid('selectRow', selRow);
				    }
					_this._child.loadData(par).then(data => {
						resolve(data);
					});
				} else
					resolve();
			}
			else
				resolve();
		});
	}

	treeTableSetSelected(tbl) {
		var sel = tbl.attr('selRows');
		var singleSel = tbl.treegrid('options').singleSelect;
		if (sel && sel != 0) {
			var rows = sel.split(',');
			var root = tbl.treegrid('getRoot');
			if(rows.length > 1 || (root && rows[0] != root.id)){
				for (var ind = 0; ind < rows.length; ind++) {
					var tr = tbl.treegrid('options').finder.getTr(tbl.get(0),rows[ind]);
					if (tr && tr.length > 0 && !tr.hasClass("datagrid-row-selected")) {
						tbl.treegrid('select', rows[ind]);
						if(!singleSel){
							tbl.treegrid('checkNode', rows[ind]);
						}
					}
				}
			}
		}
	}
	
	findTableNode(tableId, str) {
		if (str.length > 0) {
			// Находим таблицу
			var t = $('#' + tableId);
			var panel = t.datagrid('getPanel');
			
			// Находим ячейку содержащую текст
			var tds = $('.datagrid-cell:contains("' + str + '")', panel);
			
			var nextIndex = -1;
			
			var tr = null;
			for (i=0; i<tds.length; i++) {
				// Строки
				tr = $(tds.get(i)).parent().parent();
				// индекс строки
				var rIndex = parseInt(tr.attr('datagrid-row-index'));
				if (rIndex > this._lastId) {
					nextIndex = rIndex;
					break;
				}
			}
			
			if (nextIndex > -1) {
				this._lastId = nextIndex;
		        t.datagrid('clearSelections');
		        // выделяем строку
		        t.datagrid('selectRow', nextIndex);
	
		        // отправляем номер выделенной строки на сервер
		        var index = this.globalIndex(t, nextIndex);
		        this.makeSelection(t, index);
		        
				// Таблица и ее контейнер 
				var div = tr.closest('.datagrid-body');
				var table = div.children('.datagrid-btable');
				// скролируем контейнер до нужной строки
				div.scrollTop(tr.offset().top - table.offset().top);
			} else {
				Util.alert('Ничего не найдено!');
			}
		}
	}
	
	findTreeTableNode(treeId, str) {
		let _this = this;
		
		if (_this._searchText == str)
			_this._searchCount++;
		else {
			_this._searchText = str;
			_this._searchCount = 0;
		}
		
		if (str.length > 0) {
			var par = {
				"treeFind": treeId,
				"id":_this._lastId > -1 ? "" + _this._lastId : "",
				"title":str,
				"index":_this._searchCount
			};
			_this.app.post(par).then(data => {
				if (data.value) {
					var cellVal = data.value.split("##");
					var selCol = cellVal[1];
				}
				if(data.parentNodes)
					var parIds = data.parentNodes.split(","); 
				if (data.node) {
					var nodeId = data.node;
					_this._lastId = nodeId;
					
					var root = $('#' + treeId).treegrid('getRoot');
					
					if (root.id == nodeId) {
						var tr = $('.datagrid-view2 tr[node-id=' + root.id + ']');
						var div = tr.closest('.datagrid-body');
						var table = tr.closest('.datagrid-btable');
						_this.lastHiLitTreeTableElement = table;
						table.find('.higliht').removeClass('higliht').addClass('nohigliht');
	
						str = Util.sanitizeHtml(str);
						
						$.each(tr.find('td'), function(i, td) {
							var tag = $(td).find('.tree-title');
							if (tag.length == 0) tag = $(td).find('div');
							
							var html = Util.sanitizeHtml(tag.get(0).textContent);
							var ind = html.toUpperCase().indexOf(str.toUpperCase());
							if (ind > -1) {
								html = html.substring(0, ind) + '<span class="higliht">' + html.substring(ind, ind + str.length) + '</span>' + html.substring(ind + str.length);
								tag.html(html);
								$('#' + treeId).treegrid('select', root.id);
								div.scrollTop(tr.offset().top - table.offset().top);
							}
						});
					} else {
	//					var children = $('#' + treeId).treegrid('getChildren', root.id);
	//					for (var i = 0; i<children.length; i++) {
	//						if (root.id == children[i].parent)
	//							$('#' + treeId).treegrid('remove', children[i].id);
	//					}
	//					$('#' + treeId).treegrid('reload');
						$('#' + treeId).find('.higliht').removeClass('higliht').addClass('nohigliht');
						if(parIds){
							var col = parIds.length;
							for(var i = 0; i < col; i++){
								_this.treegridCallLater('expand', $('#' + treeId), parIds[i], 20);
							}
						}
						_this.treegridCallLater('select', $('#' + treeId), nodeId, 20);
						_this.treedridCallLaterHigliht($('#' + treeId), nodeId, str, 20, selCol);
					}
					$('#_' + treeId).focus();
				} else {
					$('#' + treeId).find('.higliht').removeClass('higliht').addClass('nohigliht');
					Util.alert('Ничего не найдено!');
				}
			});
		} else {
			$('#' + treeId).find('.higliht').removeClass('higliht').addClass('nohigliht');
		}
	}

	
	makeSelection(t, index) {
		if (this.selectedRow[t.attr("id")] != index) {
			this.clickedGrid = t.attr("id");
			this.selectedRow[t.attr("id")] = index;
			
			this._child.setValue(t.attr('id'), index);
			t.attr('selRows', index);
		}
		var selCol = t.attr('selectedCol');
		if (selCol) {
			var pIndex = this.pageIndex(t, index);
			t.datagrid('selectCell', {index:pIndex,field:selCol});
		}
		var toolbar_sel = $(t).parents(".datagrid").find(".datagrid-toolbar:first .datagrid-sel");
		$(toolbar_sel).text((index+1)+" /");
	}
	
	treeCallLater(name, comp, id, count) {
		let _this = this;
		var node = comp.tree('find', id);
		if (node != null) {
			comp.tree(name, node.target);
			return;
		} else if (count > 0) {
			count--;
			setTimeout(function(){_this.treeCallLater(name, comp, id, count);}, 800);
		}
	}
	
	treegridCallLater(name, comp, id, count) {
		let _this = this;
		//console.log("Count = " + count);
		var node = comp.treegrid('find', id);
		if (node != null) {
			comp.treegrid(name, id);
			return;
		} else if (count > 0) {
			count--;
			setTimeout(function(){_this.treegridCallLater(name, comp, id, count);}, 800);
		}
	}
	
	treedridCallLaterHigliht(comp, id, text, count, selCol) {
		let _this = this;
		//console.log("Count = " + count);
		var node = comp.treegrid('find', id);
		if (node != null) {
			var tr = $('.datagrid-view2 tr[node-id=' + id + ']');
			var div = tr.closest('.datagrid-body');
			var table = div.children('.datagrid-btable');
			_this.lastHiLitTreeTableElement = table;
			
			table.find('.higliht').removeClass('higliht').addClass('nohigliht');
			
			text = Util.sanitizeHtml(text);
			
			var colIdx = 0;
			$.each(tr.find('td'), function(i, td) {
				var tag = $(td).find('.tree-title');
				if (tag.length == 0) tag = $(td).find('div');
				var html = Util.sanitizeHtml(tag.get(0).textContent);
				var ind = html.toUpperCase().indexOf(text.toUpperCase());
				if (ind > -1) {
					if(selCol){
						if(colIdx == selCol)
							html = html.substring(0, ind) + '<span class="higliht">' + html.substring(ind, ind + text.length) + '</span>' + html.substring(ind + text.length);
					} else
						html = html.substring(0, ind) + '<span class="higliht">' + html.substring(ind, ind + text.length) + '</span>' + html.substring(ind + text.length);
					tag.html(html);
					div.scrollTop(tr.offset().top - (table.offset().top + _this.heightRange));
				}
				colIdx++;
			});
			return;
		} else if (count > 0) {
			count--;
			setTimeout(function(){_this.treedridCallLaterHigliht(comp, id, text, count, selCol);}, 800);
		}
	}

	setSelection(tbl, val, index){
		if(val.length > 0){
			if (this.selectedRow[tbl.attr("id")] != index) {
				this.clickedGrid = tbl.attr("id");
				this.selectedRow[tbl.attr("id")] = index;
			}
			var selIdx = this.selectedRow[tbl.attr("id")];
	
			var oldVal = tbl.attr('selRows');
			if (oldVal != val) {
				this._child.setValue(tbl.attr('id'), val);
				tbl.attr('selRows', val);
				this.currentSelectedId = id;
			} else {
				var id = tbl.attr('id');
				if (id != null && id != this.currentSelectedId) {
					this.currentSelectedId = id;
					var par = {"cmd":"fcs","uid":id};
					this.app.post(par);
				}
			}
		}
	}
	
	selectRow(elem, up){
	    var t = elem.find('.easyui-datagrid');
		
	    if (t.datagrid('getPanel').find('.datagrid-row-editing').length > 0) return true;
	
		var count = t.datagrid('getRows').length;    // row count
	    var selected = t.datagrid('getSelected');
	    if (selected){
	        var rIndex = t.datagrid('getRowIndex', selected);
	        rIndex = rIndex + (up ? -1 : 1);
	        if (rIndex < 0 || rIndex >= count) return true;
	        t.datagrid('clearSelections');
	        t.datagrid('selectRow', rIndex);
	
	        var index = this.globalIndex(t, rIndex);
	        this.makeSelection(t, index);
	    } else {
	    	var rIndex = up ? count-1 : 0;
	        t.datagrid('selectRow', rIndex);
	    	
	        var index = this.globalIndex(t, rIndex);
	        this.makeSelection(t, index);
	    }
	    return false;
	}
	
	selectColumn(elem, left){
	    var t = elem.find('.easyui-datagrid');
	    var selected = t.datagrid('getSelected');
		var selCol = t.attr('selectedCol');
	    if (selected && selCol) {
	        var index = t.datagrid('getRowIndex', selected);
	    	var td = t.datagrid('getCell', {index:index, field:selCol});
	    	if (td.parent().hasClass('datagrid-row-editing')) return true;
	    	var next = left ? td.prev('td') : td.next('td');
	    	if (!next || next.length == 0) {
	    		return true;
	    		//next = left ? td.parent().find('td:last') : td.parent().find('td:first'); 
	    	}
	    	var field = next.attr('field');
	    	t.datagrid('selectCell', {index:index, field:field});
	    	t.attr('selectedCol', field);
	    	return false;
	    }
	    return true;
	}
	
	beginEditCell(elem){
	    var t = elem.find('.easyui-datagrid');
	    var selected = t.datagrid('getSelected');
		var selCol = t.attr('selectedCol');
	    if (selected && selCol) {
	        var index = t.datagrid('getRowIndex', selected);
	    	var td = t.datagrid('getCell', {index:index, field:selCol});
	    	if (td.parent().hasClass('datagrid-row-editing')) return true;
			if (this.endEditing()) {
				var realIndex = this.globalIndex(t, index);
			
				var id = t.attr("id");
				this.editingTable = id;
				this.editIndex[id] = {index: index, field: selCol, realIndex: realIndex};
				t.datagrid('editCell', {index:index,field:selCol});
				return false;
			}
	    }
	    return true;
	}
	
	updateTreeTableRow(i, index, row) {
		let _this = this;
		if ($("#" + i).treegrid('find', index)) {
			$("#" + i).treegrid('update',{
				id: index,
				row: row
			});
			if (row.children) {
				$.each(row.children, function(ri, chrow) {
					_this.updateTreeTableRow(i, chrow.id, chrow);
				});
			}
		}
	}
	
	
	paintTreegridRows(uid) {
		let _this = this;
		var par = {"cmd":'treegridZebraColors'};
		par["uid"] = uid;
		par["json"] = 1;
		par["rnd"] = Util.rnd();
		
		_this.app.post(par).then(data => {
			if (data.zebra1 || data.zebra2) {
				let zebra1 = data.zebra1 ? data.zebra1 : "";
				let zebra2 = data.zebra2 ? data.zebra2 : "";
	
				if (zebra1 != "#FFFFFF" || zebra2 != "#FFFFFF") {
					var rootNode = $('#' + uid).treegrid('getRoot');
					if (rootNode != null) {
						var opts = $('#' + uid).treegrid('options');
						_this.viewChild($('#' + uid), opts, rootNode, zebra1, zebra2, 0);
					}
				}
			}
		});
	}

	viewChild(comp, opts, node, zebra1, zebra2, hiddenNodesCount) {
		var tr = opts.finder.getTr(comp[0], node[opts.idField]);
		var cell = tr.find('div.datagrid-cell-rownumber');
		var value = cell.text();
		var index = parseInt(value) - hiddenNodesCount;
		if (index % 2 == 0) {
			tr.css('background', zebra1);
		} else {
			tr.css('background', zebra2);
		}
		var children = this.getMyChildren(comp, node.id);
		if (children.length > 0) {
			if (node.state == "open") {
				for (var i = 0; i < children.length; i++) {
					hiddenNodesCount += this.viewChild(comp, opts, children[i], zebra1, zebra2, hiddenNodesCount);
				}
			} else {
				return comp.treegrid('getChildren', node.id).length;
			}
		}
		return 0;
	}
	
	getMyChildren(comp, parentId) {
		var children = comp.treegrid('getChildren', parentId);
		var myChildren = [];
		for (var i = 0; i < children.length; i++) {
			if (children[i].parent == parentId) {
				myChildren.push(children[i]);	
			}
		}
		return myChildren;
	}
	
	reloadTables() {
		this.clickedGrid = "";
	}
	
	makeTreeTable(operation) {
		if (this.selectedTree) {
			var node = this.selectedTree.treegrid('getSelected');
			this.selectedTree.treegrid(operation, node.id);
		}
	}
	
	getOption(elem, option){
		var out = null;
		var opt = $(elem).data('options');
		if (opt != null) {
			var opts = opt.split(',');
			$.each(opts, function(i, opt) {
				var p = opt.indexOf(':');  
				var param=$.trim(opt.substring(0,p));
				var val = opt.length>p+1?opt.substring(p+1):'';
				if(param == option){
					out = val;
					return out;
				}
			});
		}
		return out;
	}
	
	makeTree(operation) {
		if (this.selectedTree) {
			var node = this.selectedTree.tree('getSelected');
			this.selectedTree.tree(operation, node.target);
		}
	}
	
	findNode(treeId, str, trfld, dlg) {
		let _this = this;
		if (str.length > 0) {
			if(_this.searchText == str){
				_this.searchCount++;
	
				if (_this.lastHighlightedNode != null)
					_this.showPath(_this.lastHighlightedNode.parentNode, str, treeId, trfld, dlg, _this.foundNodes, _this.lastHighlightedNode.level, _this.lastHighlightedNode.index, _this.lastHighlightedNode.count);
				else
					_this.showPath(null, str, treeId, trfld, dlg, _this.foundNodes, 0, 0, 1);
			} else {
				_this.searchText = str;
				_this.searchCount = 0;
				_this.foundNodes = null;
				_this.lastHighlightedNode = null;
	
				if (dlg) {
					Util.blockDialog(dlg);
					_this.blockDlg = true;
					console.log("block on line 1992");
				}
	
				var par = {"treeFind":treeId, "title":str};
				
				_this.app.post(par).then(data => {
					if(data.foundNodes){
						_this.foundNodes = data.foundNodes;
						
						_this.lastHighlightedNode = null;
						_this.showPath(null, str, treeId, trfld, dlg, _this.foundNodes, 0, 0, 1);
					} else {
						$('#' + treeId).find('.higliht').removeClass('higliht').addClass('nohigliht');
						alert('Ничего не найдено!');
						if(dlg){
							dlg.unblock();
							_this.blockDlg = false;
						}
					}
				});
	
			}
		} else {
			$('#' + treeId).find('.higliht').removeClass('higliht').addClass('nohigliht');
		}
		$('#_' + treeId).focus();
	}
	
	getNode(treeId, parentNode, level, index, count) {
		if (parentNode == null) {
			if (index > 0)
				return null;
			else
				return {
					'node' : $('#' + treeId).tree('getRoot'),
					'parent' : null,
					'index' : 0,
					'level' : 0,
					'count' : 1
				};
		} else {
			if (index < count) {
				var children = this.getDirectTreeNodeChildren(treeId, parentNode);
				return {
					'node' : children[index],
					'parent' : parentNode,
					'index' : index,
					'level' : level,
					'count' : count
				};
			} else {
				var parentParentNode = $('#' + treeId).tree('getParent', parentNode.target);
				if (parentParentNode != null) {
					var children = this.getDirectTreeNodeChildren(treeId, parentParentNode);
					
					var parentIndex = -1;
					for(j = 0; j < children.length; j++) {
						if (parentNode.id == children[j].id) {
							parentIndex = j;
							break;
						}
					}
					
					return this.getNode(treeId, parentParentNode, level - 1, parentIndex + 1, children.length);
				} else 
					return null;
			}
		}
	}
	
	getDirectTreeNodeChildren(treeId, node) {
		var children = $('#' + treeId).tree('getChildren', node.target);
		var directChildren = [];
		for (var i=0; i<children.length; i++) {
			if (children[i].parent == node.id)
				directChildren[directChildren.length] = children[i];
		}
		return directChildren;
	}
	
	showPath(parentNode, str, treeId, trfld, dlg, foundNodes, level, indexInParent, count) {
		let _this = this;
		
		var fullTreeId = (trfld) ? ('trfld'+treeId) : treeId;
		var nodeStruct = this.getNode(fullTreeId, parentNode, level, indexInParent, count);
		
		
		if (nodeStruct != null) {
			parentNode = nodeStruct.parent;
			indexInParent = nodeStruct.index;
			count = nodeStruct.count;
			level = nodeStruct.level;
			var node = nodeStruct.node;
			
			var curData = null;
			
			for(i=0; i< foundNodes.length; i++){
				if(foundNodes[i].node == node.id && !foundNodes[i].done){
					foundNodes[i].done = true;
					curData = foundNodes[i];
				}
			}
			
			if (curData == null) {
				for(i=0; i< foundNodes.length; i++) {
					if (foundNodes[i].parentNodes && !foundNodes[i].done) {
						var parIds = foundNodes[i].parentNodes.split(","); 
			
						if(node.id == parIds[level]) {
							if (node.state == 'open') {
								var children = this.getDirectTreeNodeChildren(fullTreeId, node);
								setTimeout(function(){
									_this.showPath(node, str, treeId, trfld, dlg, foundNodes, level + 1, 0, children.length);
								}, 50);
							} else {
								onTreeExpand = function() {
									var children = _this.getDirectTreeNodeChildren(fullTreeId, node);
									_this.showPath(node, str, treeId, trfld, dlg, foundNodes, level + 1, 0, children.length);
								}
								
								_this.treeCallLater('expand', $('#' + fullTreeId), node.id, 20);
							}
							return;
						}
					}
				}
				
				setTimeout(function(){
					_this.showPath(parentNode, str, treeId, trfld, dlg, foundNodes, level, indexInParent + 1, count);
				}, 50);
				
				return;
			} else {
				_this.lastHighlightedNode = {
					'parentNode' : parentNode,
					'level' : level,
					'index' : indexInParent,
					'count' : count
				};
				var nodeId = curData.node;
				var root = $('#' + fullTreeId).tree('getRoot');
				var rootChildren = $('#' + fullTreeId).tree('getChildren', root.target);
				$('#' + fullTreeId).find('.higliht').removeClass('higliht').addClass('nohigliht');
				_this.treeCallLater('select', $('#' + fullTreeId), nodeId, 20);
				_this.treeCallLaterHigliht($('#' + fullTreeId), nodeId, str, 20, trfld, dlg);
	
				if(dlg){
					dlg.unblock();
					_this.blockDlg = false;
					console.log("unblock on line 2033");
				}
				
				$('#_' + treeId).focus();
			}
		} else {
			for(i=0;i<foundNodes.length; i++){
				delete foundNodes[i].done;
			}
			_this.lastHighlightedNode = null;
			
			_this.showPath(null, str, treeId, trfld, dlg, foundNodes, 0, 0, 1);
		}
	}		
}
