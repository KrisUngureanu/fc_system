var or3web;
/******/ (() => { // webpackBootstrap
/******/ 	"use strict";
/******/ 	var __webpack_modules__ = ({

/***/ "./analytic.js":
/*!*********************!*\
  !*** ./analytic.js ***!
  \*********************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   "Analytic": () => (/* binding */ Analytic)
/* harmony export */ });
/* harmony import */ var _util_js__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./util.js */ "./util.js");


class Analytic {
//	OLAP_URL = "http://10.61.42.157:7303/analytic/rest/";
	// OLAP_URL = "http://192.168.13.170:8081/analytic/rest/";
//	OLAP_URL = "http://145.249.246.98:8081/analytic/rest/";
	OLAP_URL = "";
	nameX = "Category";
	
	constructor(app) {
		this.app = app;
		this.OLAP_URL = app.OLAP_URL;
		$.extend($.fn.combotree.defaults, {width: '260px', height: '22px'});
	}
	
	init(cont) {	
		console.log("analitic olap port " + this.OLAP_URL);
		let this_ = this;

		if (cont == null) cont = $('body');
		var analytics = cont.find('.or3-analytic');
		for (var i=0; i<analytics.length; i++) {
			var analytic = $(analytics.get(i));
			this_.analyticInit(analytic.attr("id"));
		}
	}

	getOlapPort() {
		let this_ = this;
		let par = {"sfunc":1,"cls":"MainPage","name":"otherMethods", "arg0":this_.app.userId, "arg1": "getOlapPort"};
		this_.app.query(par).then(response => {
			if (response.status === 200) {
				response.json().then(json => {
					console.log(json);
					if(json) {
						if(json.olapPort) {
							this_.OLAP_URL = json.olapPort;
						} 
					} 
				});
			}
		});
		
	}
	
	analyticInit(id) {
		let this_ = this;

		var xAxes;
		var yAxes;
		var zAxis;
		var nodesNamesX;
		var nodesNamesY;
		var nodesNamesZ;
		var parentXId;
		var parentYId;
		var selectedZIds = {};
		var fact;
		var aggType;
		var aggField;
		var chartContainer = {};
	
		$('.analytic-dimension').next().next().insertAfter($('.analytic-tree:last'));
		$('.analytic-dimension').next().insertAfter($('.analytic-tree:last'));
		$('.analytic-dimension').insertAfter($('.analytic-tree:last'));
		
		$('#type_' + id).next().remove();
		$('#type_' + id).remove();
		$('#xaxes_' + id).next().remove();
		$('#xaxes_' + id).remove();
		$('#yaxes_' + id).next().remove();
		$('#yaxes_' + id).remove();
		
		// удаляем линшие стрелочки
		$('.analytic-dimension-title:first p').text('Выберите тип диаграммы');
		$('.analytic-dimension-title .arrow-width').remove();
		$('.analytic-cnt-coll-pan .analytic-dimension-title').get(1).remove();
		$('.analytic-cnt-coll-pan hr').remove();
		$('#filter_' + id).next().remove();
		$('#filter_' + id).remove();
		
	
		let splitPane = $('<div id="analytic-split-pane" class="easyui-layout" data-options="fit:true">');
		let westDiv = $('<div id="analytic-params-pane" data-options="region:\'west\', collapsed:true, title: \'Параметры диаграммы\', split:true" style="width:400px;"></div>');
    	let centerDiv = $('<div id="analytic-chart-pane" data-options="region:\'center\', split:true"></div>');
		splitPane.append(westDiv);
		splitPane.append(centerDiv);
		westDiv.append($('#cnt0acc_' + id).removeClass('cnt-coll-pan-v'));
		centerDiv.append($('#axes_title_' + id));
		centerDiv.append($('#' + id));
		
		$('#ui-body > div').empty();
		$('#ui-body > div').append(splitPane);
		$('#analytic-split-pane').layout();
		
		$('.icon-clearAll').after('<span>Снять все</span>');
		$('.icon-selectAll').after('<span>Выделить все</span>');

		var combo1 = $('<input id="type_' + id + '" class="easyui-combobox" style="width:100%; height: 35px;"/>');
		combo1.insertAfter($('.analytic-dimension-title'));
		$('#type_' + id).combobox();
		combo1 = $('<input id="xaxes_' + id + '" class="easyui-combobox" style="width:100%; height: 35px;"/>');
		combo1.insertBefore($('#x_' + id));
		$('#xaxes_' + id).combobox();
		combo1 = $('<input id="yaxes_' + id + '" class="easyui-combobox" style="width:100%; height: 35px;"/>');
		combo1.insertBefore($('#y_' + id));
		$('#yaxes_' + id).combobox();

	
		$.ajax({
			type: "GET",
			url: window.mainUrl + "&cmd=getAnalyticProps&id=" + id,
			success: function (data) {
				xAxes = data.xAxisId;
				yAxes = data.yAxisId;
				zAxis = data.zAxisId;
				parentXId = data.firstXAxis;
				parentYId = data.firstYAxis;
				fact = data.fact;
				chartContainer.type = data.type;
				chartContainer.showLegend = data.showLegend;
				aggType = data.aggType;
				aggField = data.aggField;
			},
			dataType: 'json',
			async: false
		});
	
		$.ajax({
			type: "GET",
			url: this_.OLAP_URL + "dim/dimension/" + parentXId.id + "?fact=" + fact,
			success: function (data) {
				nodesNamesX = data;
			},
			error: function (ex) {
				console.log(ex);
			},
			dataType: 'json',
			async: false
		});
	
		$.ajax({
			type: "GET",
			url: this_.OLAP_URL + "dim/dimension/" + parentYId.id + "?fact=" + fact,
			success: function (data) {
				nodesNamesY = data;
			},
			dataType: 'json',
			async: false
		});
		
		zAxis.forEach(filter => {
			$.ajax({
				type: "GET",
				url: this_.OLAP_URL + "dim/dimension/" + filter.id + "?fact=" + fact,
				success: function (data) {
					nodesNamesZ = data;
				},
				dataType: 'json',
				async: false
			});
		});
		
		var onSelect = function () {
			let checkedX = $('#x_' + id).tree('getChecked');
			let checkedY = $('#y_' + id).tree('getChecked');
			let checkedZs = {};
			
			zAxis.forEach(filter => {
				let tree = $('#filter_' + id + '_' + filter.id).combotree('tree');
				let checkedZ = tree.tree('getChecked');
				checkedZs[filter.id] = checkedZ;
			});
			
			if (!checkedX.length || !checkedY.length) {
				_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.alert("Выберите измерения!", _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.WARNING);
				return;
			}
	
			let newNodesNamesX = [];
			let newNodesNamesY = [];
			let newSelectedZIds = {};
			
			for (var i in checkedX) {
				newNodesNamesX.push({ id: checkedX[i].id, name: checkedX[i].text, isLeaf: $('#x_' + id).tree('isLeaf', checkedX[i].target), color: checkedX[i].color });
			}
			for (i in checkedY) {
				newNodesNamesY.push({ id: checkedY[i].id, name: checkedY[i].text, isLeaf: $('#y_' + id).tree('isLeaf', checkedY[i].target), color: checkedY[i].color });
			}
			$.each(checkedZs, function(filterId, checkedZ) {
				let arr = [];
				for (i in checkedZ) {
					arr.push({ id: checkedZ[i].id, name: checkedZ[i].text, isLeaf: $('#filter_' + id + '_' + filterId).tree('isLeaf', checkedZ[i].target), color: checkedZ[i].color });
				}
				newSelectedZIds[filterId] = arr;
			});
			
			if (JSON.stringify(nodesNamesX) === JSON.stringify(newNodesNamesX)
				&& JSON.stringify(nodesNamesY) === JSON.stringify(newNodesNamesY)
				&& JSON.stringify(selectedZIds) === JSON.stringify(newSelectedZIds)) {
				return;
			}
			nodesNamesX = newNodesNamesX;
			nodesNamesY = newNodesNamesY;
			selectedZIds = newSelectedZIds;
			this_.loadAnalytic(id, chartContainer, parentXId, parentYId, zAxis, nodesNamesX, nodesNamesY, selectedZIds, fact, aggType, aggField);
			this_.initAxesTitle(id, parentXId, parentYId);
		}
		$('#btn_' + id).click(onSelect);
	
		this_.initAxesTitle(id, parentXId, parentYId);
		this_.initBtn('#btn_' + id);
		this_.initTglLegBtn('#tgl_leg_' + id, chartContainer);
		$('#hide_btn_' + id).click(() => this_.hideBtn(id));
		this_.initCombobox(id, xAxes, fact, nodesNamesX, "x", parentXId);
		this_.initCombobox(id, yAxes, fact, nodesNamesY, "y", parentYId);
		if (zAxis.length > 0) {
			zAxis.forEach(filter => {
				this_.loadNodes(id, fact, nodesNamesZ, filter.id, filter.value, "filter", true);
				selectedZIds[filter.id] = [];
			});
		}
		this_.initSelectBtns(id);
	
		this_.loadAnalytic(id, chartContainer, parentXId, parentYId, zAxis, nodesNamesX, nodesNamesY, selectedZIds, fact, aggType, aggField);
	
		function toggleLegClick() {
			if (chartContainer.chart.legend.disabled) {
				chartContainer.showLegend = true;
				this_.setShowLegend(chartContainer, true);
			}
			else {
				chartContainer.showLegend = false;
				this_.setShowLegend(chartContainer, false);
			}
			this_.initTglLegBtn('#tgl_leg_' + id, chartContainer);
		}
		$('#tgl_leg_' + id).click(toggleLegClick);
	
		$('#' + id).parent().click(() => this_.hideBtn(id));
	}
	
	processOut(hoveredSeries, chart) {
		chart.series.each(function (series) {
			series.segments.each(function (segment) {
				segment.setState("default");
			})
			series.bulletsContainer.setState("default");
		});
	}
	
	processOver(hoveredSeries, chart) {
		hoveredSeries.toFront();
	
		hoveredSeries.segments.each(function (segment) {
			segment.setState("hover");
		});
	
		chart.series.each(function (series) {
			if (series != hoveredSeries) {
				series.segments.each(function (segment) {
					segment.setState("dimmed");
				});
				series.bulletsContainer.setState("dimmed");
			}
		});
	}
	
	// Create series
	createBarSeries(chart, nodeNameY, i) {
		let field = nodeNameY.id;
		let name = nodeNameY.name;
	
	
		// Set up series
		var series = chart.series.push(new am4charts.ColumnSeries());
	
	
		series.columns.template.propertyFields.fill = "color" + i;
		series.columns.template.propertyFields.stroke = "color" + i;
	
		series.name = name;
		series.dataFields.valueY = field;
		series.dataFields.categoryX = this.nameX;
		series.sequencedInterpolation = true;
	
		// Make it stacked
		series.stacked = true;
	
		// Configure columns
		series.columns.template.width = am4core.percent(60);
		series.columns.template.tooltipText = "[bold]{name}[/]\n[font-size:14px]{categoryX}: {valueY}";
	
		// Add label
		var labelBullet = series.bullets.push(new am4charts.LabelBullet());
		labelBullet.label.text = "{valueY}";
		labelBullet.locationY = 0.5;
		labelBullet.label.hideOversized = true;
	
		return series;
	}
	
	create3DBarSeries(chart, field, name, i) {
		var series = chart.series.push(new am4charts.ColumnSeries3D());
		series.columns.template.propertyFields.fill = "color" + i;
		series.columns.template.propertyFields.stroke = "color" + i;
		series.name = name;
		series.dataFields.valueY = field;
		series.dataFields.categoryX = this.nameX;
		series.clustered = false;
		series.columns.template.tooltipText = "[bold]{name}[/]\n[font-size:14px]{categoryX}: {valueY}";
		series.columns.template.fillOpacity = 0.9;
	}
	
	createLineSeries(chart, field, name, i) {
		let this_ = this;
		var series = chart.series.push(new am4charts.LineSeries());
		series.propertyFields.fill = "color" + i;
		series.propertyFields.stroke = "color" + i;
		series.dataFields.dateX = this.nameX;
		series.dataFields.valueY = field;
		series.name = name;
		series.tooltipText = "{valueY}";
		series.tooltip.pointerOrientation = "vertical";
		series.tooltip.background.fillOpacity = 0.5;
		series.strokeWidth = 3;
	
		var segment = series.segments.template;
		segment.interactionsEnabled = true;
	
		var hoverState = segment.states.create("hover");
		hoverState.properties.strokeWidth = 3;
	
		var dimmed = segment.states.create("dimmed");
		dimmed.properties.stroke = am4core.color("#dadada");
	
		segment.events.on("over", function (event) {
			this_.processOver(event.target.parent.parent.parent, chart);
		});
	
		segment.events.on("out", function (event) {
			this_.processOut(event.target.parent.parent.parent, chart);
		});
	}
	
	createDonutSeries(chart, field, name) {
		// Add and configure Series
		var pieSeries = chart.series.push(new am4charts.PieSeries());
		pieSeries.slices.template.propertyFields.fill = "color";
		pieSeries.slices.template.propertyFields.stroke = "color";
		pieSeries.name = name;
		pieSeries.dataFields.value = field;
		pieSeries.dataFields.category = this.nameX;
		pieSeries.slices.template.stroke = am4core.color("#fff");
		pieSeries.slices.template.tooltipText = "[bold]{name}[/]\n[font-size:14px]{category}: {value}";
	
		// Disabling labels and ticks on inner circle
		pieSeries.labels.template.disabled = true;
		pieSeries.ticks.template.disabled = true;
	
		// Disable sliding out of slices
		pieSeries.slices.template.states.getKey("hover").properties.shiftRadius = 0;
		pieSeries.slices.template.states.getKey("hover").properties.scale = 1.0;
	}
	
	createPieSeries(chart, field, name) {
		// Add and configure Series
		var pieSeries = chart.series.push(new am4charts.PieSeries());
		pieSeries.slices.template.propertyFields.fill = "color";
		pieSeries.slices.template.propertyFields.stroke = "color";
		pieSeries.name = name;
		pieSeries.dataFields.value = field;
		pieSeries.dataFields.category = this.nameX;
		pieSeries.slices.template.stroke = am4core.color("#fff");
		pieSeries.slices.template.strokeWidth = 2;
		pieSeries.slices.template.strokeOpacity = 1;
		pieSeries.ticks.template.disabled = true;
		pieSeries.labels.template.disabled = true;
		pieSeries.slices.template.tooltipText = "[bold]{name}[/]\n[font-size:14px]{category}: {value}";
	
		// This creates initial animation
		pieSeries.hiddenState.properties.opacity = 1;
		pieSeries.hiddenState.properties.endAngle = -90;
		pieSeries.hiddenState.properties.startAngle = -90;
	}
	
	createLineLegend(chartContainer) {
		let this_ = this;
		chartContainer.chart.legend = new am4charts.Legend();
		chartContainer.chart.legend.position = "bottom";
		chartContainer.chart.legend.scrollable = true;
		chartContainer.chart.legend.itemContainers.template.events.on("over", function (event) {
			this_.processOver(event.target.dataItem.dataContext, chartContainer.chart);
		})
	
		chartContainer.chart.legend.itemContainers.template.events.on("out", function (event) {
			this_.processOut(event.target.dataItem.dataContext, chartContainer.chart);
		})
	}
	
	createLegend(chartContainer, nodesNamesY) {
		if (chartContainer.type === "LINE") {
			this.createLineLegend(chartContainer);
		} else {
			chartContainer.chart.legend = new am4charts.Legend();
			chartContainer.chart.legend.maxHeight = 150;
			chartContainer.chart.legend.scrollable = true;
		}
		
		let legenddata = chartContainer.chart.legend.data;
		if (legenddata.length) {
			let i;
			for (i in legenddata) {
				let nodeY = nodesNamesY.find(e => e.id === legenddata[i].dataFields.valueY);
				if (nodeY && nodeY.color)
					legenddata[i].fill = nodeY.color;
			}
		} else {
	        chartContainer.chart.events.on("ready", function (event) {
	        	let chart = event.target; 
	            let series = chart.series.values;
	            chart.legend.data = series[series.length - 1].dataItems.values;
	        });
	    }
	}
	
	fillinNodes(e) {
		if (!e.children) {
			return { id: e.id, text: e.text, state: e.state, checked: e._checked, color: e.color };
		}
		var childNodes = [];
		for (var i in e.children) {
			childNodes.push(this.fillinNodes(e.children[i]));
		}
		return { id: e.id, text: e.text, state: e.state, children: childNodes, checked: e._checked, color: e.color };
	}
	
	addNode(tree, node, data) {
		for (var i in tree) {
			if (tree[i].children) this.addNode(tree[i].children, node, data);
			if (tree[i].id != node.id) continue;
			var childNodes = [];
			for (var j in data) {
				var child = {};
				child.id = data[j].id;
				child.text = data[j].name;
				child.state = data[j].isLeaf ? 'open' : 'closed';
				child.color = data[j].color;
				childNodes.push(child);
			}
			tree[i].children = childNodes;
			tree[i].state = 'open';
			break;
		}
	}
	
	loadNode(node, id, dimId, loading, fact, isCombo) {
		let this_ = this;
		loading = true;
		let folder = $(node.target).find('span.tree-folder');
		folder.addClass('tree-loading');
		$.ajax({
			type: "GET",
			url: this_.OLAP_URL + "dim/dimension/" + node.id + "?fact=" + fact,
			success: function (data) {
				var oldNodes;
				if (isCombo) {
					let tree = $(id + "_" + dimId).combotree('tree');
					oldNodes = tree.tree('getRoots');
				} else {
					oldNodes = $(id).tree('getRoots');
				}
				var newNodes = oldNodes.map(this_.fillinNodes.bind(this_));
				this_.addNode(newNodes, node, data)
				if (isCombo) {
					$(id + "_" + dimId).combotree('loadData', newNodes);
				} else {
					$(id).tree('loadData', newNodes);
				}
			},
			dataType: 'json',
			async: false
		});
		loading = false;
		folder.removeClass('tree-loading');
	}
	
	onNodeClick(node) {
		$(this).tree('toggle', node.target);
	}
	
	initTree(id, dimId, data, fact) {
		let this_ = this;
		var loading = false;
		var beforeExpand = function (node) {
			if (!loading && !node.children) {
				this_.loadNode(node, id, dimId, loading, fact);
			}
			return true;
		}
		$(id).tree({ cascadeCheck: false, checkbox: true, onBeforeExpand: beforeExpand, onClick: this_.onNodeClick });
		$(id).tree('loadData', data);
	}
	
	initComboTree(id, prefix, dimId, dimName, data, fact) {
		let this_ = this;
		var loading = false;
		var beforeExpand = function (node) {
			if (!loading && !node.children) {
				this_.loadNode(node, '#' + prefix + '_' + id, dimId, loading, fact, true);
			}
			return true;
		}
		let ul = document.createElement('ul');
		ul.setAttribute('id', prefix + '_' + id + "_" + dimId);
		ul.setAttribute('style', 'width: 100%; height: 35px');
		
		let applyBtn = $('#btn_' + id);
		
		let dimDiv = $('<div class="dimension"></div>');
		let dimText = $('<p class="dimension-text">' + dimName + '</p>');
		let dimBtns = $('<div></div>');
		let clearBtn = $('<button title="Снять все" id="clear_dim_filter_'+id+'_'+dimId + '"><div class="icon-clearAll"></div><span>Снять все</span></button>');
		let selectBtn = $('<button title="Выбрать корневые" id="select_dim_filter_'+id+'_'+dimId + '"><div class="icon-selectAll"></div><span>Выделить все</span></button>');
		dimBtns.append(clearBtn);
		dimBtns.append(selectBtn);
		dimDiv.append(dimText);
		dimDiv.append(dimBtns);
		
		applyBtn.before(dimDiv);
		applyBtn.before(ul);
		$(ul).combotree({ cascadeCheck: false, multiple: true, checkbox: true, onBeforeExpand: beforeExpand, onClick: this_.onNodeClick });
		$(ul).combotree('loadData', data);
		
		this_.initFilterBtns(id, dimId);
	}
	
	loadNodes(id, fact, nodesNames, treeId, treeName, prefix, isCombo) {
		let this_ = this;
		var nodes = [];
		$.ajax({
			type: "GET",
			url: this_.OLAP_URL + "dim/dimension/" + treeId + "?fact=" + fact,
			success: function (data) {
				nodesNames = data;
				for (var i in data) {
					var node = {};
					node['id'] = data[i].id;
					node['text'] = data[i].name;
					node['value'] = data[i].name;
					node['state'] = data[i].isLeaf ? 'open' : 'closed';
					if (!isCombo)
						node['checked'] = true;
					node['color'] = data[i].color;
					nodes.push(node);
				}
			},
			dataType: 'json',
			async: false
		});
		if (isCombo) {
			this_.initComboTree(id, prefix, treeId, treeName, nodes, fact);
		} else {
			this_.initTree("#" + prefix + "_" + id, treeId, nodes, fact);
		}
	}
	
	initCombobox(id, data, fact, nodesNames, prefix, parentNodeId) {
		let this_ = this;
		var comb = $("#" + prefix + "axes_" + id);
		comb.combobox({
			onSelect: function (item) {
				parentNodeId.id = item.id;
				parentNodeId.name = item.value;
				this_.loadNodes(id, fact, nodesNames, item.id, '', prefix);
			},
			formatter: function (value) {
				var width = $(this).width();
				return $('<div/>').append($('<div style="width:' + width + 'px;cursor:pointer;">' + value.text[0] + '</>')).html();
			},
			data: data
		});
		this_.loadNodes(id, fact, nodesNames, parentNodeId.id, '', prefix);
	}
	
	initDiagramType(id, chartContainer, chartData, nodesNamesX, nodesNamesY, nameX) {
		var comb = $("#type_" + id);
		var data = [{ id: 'BAR', text: ['Столбчатая'], value: 'BAR' },
		{ id: 'PIE', text: ['Круговая'], value: 'PIE' },
		{ id: 'LINE', text: ['Линейная'], value: 'LINE' },
		{ id: 'DONUT', text: ['Кольцевая'], value: 'DONUT' },
		{ id: '3DBAR', text: ['Столбчатая 3D'], value: '3DBAR' }];
		for (let i in data) {
			if (data[i].id === chartContainer.type)
				data[i].selected = true;
		}
	
		let _this = this;
		comb.combobox({
			onSelect: function (item) {
				chartContainer.chart.dispose();
				chartContainer.type = item.id
				_this.drawChart(id, chartContainer, chartData, nodesNamesX, nodesNamesY, nameX);
			},
			formatter: function (value) {
				var width = $(this).width();
				return $('<div/>').append($('<div style="width:' + width + 'px;cursor:pointer;">' + value.text[0] + '</>')).html();
			},
			data: data
		});
	}
	
	initAxesTitle(id, parentXId, parentYId) {
		$('#axes_title_' + id).html(parentXId.name + ' / ' + parentYId.name);
	}
	
	setShowLegend(chartContainer, showLegend) {
		chartContainer.chart.legend.disabled = !showLegend;
	}
	
	drawChart(id, chartContainer, chartData, nodesNamesX, nodesNamesY, nameX) {
		for (var i in chartData) {
			var node = nodesNamesX[i]
			if (chartContainer.type === "LINE") {
				if (node.id[0] === 'y') {
					chartData[i][nameX] = new Date(node.name);
				} else if (node.id[0] === 'q') {
					chartData[i][nameX] = new Date(node.id.substring(1, 5), node.id.substring(5) - 1);
				} else {
					$('#' + id).html('<p style="font-size:24;font-weight:bold;text-align:center;padding:20% 0">В ЛИНЕЙНОЙ ДИАГРАММЕ ИЗМЕРИТЕЛЬ X ДОЛЖЕН БЫТЬ ПЕРИОДОМ</p>');
					return;
				}
			} else {
				chartData[i][nameX] = node.name;
			}
			if (chartContainer.type === "PIE" || chartContainer.type === "DONUT") {
				chartData[i]['color'] = node.color;
			}
			else {
				for (var j in nodesNamesY) {
					chartData[i]['color' + j] = nodesNamesY[j].color;
				}
			}
		}
	
		am4core.useTheme(am4themes_animated);
		let this_ = this;
		if (chartContainer.type === "BAR") {
			// Create chart instance
			chartContainer.chart = am4core.create(id, am4charts.XYChart);
	
			// Create axes
			var categoryAxis = chartContainer.chart.xAxes.push(new am4charts.CategoryAxis());
			categoryAxis.dataFields.category = this.nameX;
			categoryAxis.renderer.grid.template.location = 0;
	
			categoryAxis.renderer.minGridDistance = 30;
	
			// Configure axis label
			var label = categoryAxis.renderer.labels.template;
			label.wrap = true;
	
			categoryAxis.events.on("sizechanged", function (ev) {
				var axis = ev.target;
				var cellWidth = axis.pixelWidth / (axis.endIndex - axis.startIndex);
				axis.renderer.labels.template.maxWidth = cellWidth;
			});
	
			var valueAxis = chartContainer.chart.yAxes.push(new am4charts.ValueAxis());
			valueAxis.renderer.inside = true;
			valueAxis.renderer.labels.template.disabled = true;
			valueAxis.min = 0;
	
			for (i in nodesNamesY) {
				this_.createBarSeries(chartContainer.chart, nodesNamesY[i], i);
			}
		} else if (chartContainer.type === "PIE") {
			chartContainer.chart = am4core.create(id, am4charts.PieChart);
	
			for (i in nodesNamesY) {
				this_.createPieSeries(chartContainer.chart, nodesNamesY[i].id, nodesNamesY[i].name);
			}
		} else if (chartContainer.type === "LINE") {
			chartContainer.chart = am4core.create(id, am4charts.XYChart);
			chartContainer.chart.paddingRight = 20;
	
			var xAxis = chartContainer.chart.xAxes.push(new am4charts.DateAxis());
			xAxis.renderer.grid.template.location = 0;
			xAxis.minZoomCount = 5;
	
			// this makes the data to be grouped
			xAxis.groupData = true;
			xAxis.groupCount = 500;
	
			var yAxis = chartContainer.chart.yAxes.push(new am4charts.ValueAxis());
	
			for (i in nodesNamesY) {
				this_.createLineSeries(chartContainer.chart, nodesNamesY[i].id, nodesNamesY[i].name, i);
			}
	
			chartContainer.chart.cursor = new am4charts.XYCursor();
			chartContainer.chart.cursor.xAxis = xAxis;
	
			var scrollbarX = new am4core.Scrollbar();
			scrollbarX.marginBottom = 20;
			chartContainer.chart.scrollbarX = scrollbarX;
		} else if (chartContainer.type === "3DBAR") {
			chartContainer.chart = am4core.create(id, am4charts.XYChart3D);
	
			var categoryAxis = chartContainer.chart.xAxes.push(new am4charts.CategoryAxis());
			categoryAxis.dataFields.category = this.nameX;
			categoryAxis.renderer.grid.template.location = 0;
			categoryAxis.renderer.minGridDistance = 30;
	
			let label = categoryAxis.renderer.labels.template;
			label.wrap = true;
			categoryAxis.events.on("sizechanged", function (ev) {
				var axis = ev.target;
				var cellWidth = axis.pixelWidth / (axis.endIndex - axis.startIndex);
				axis.renderer.labels.template.maxWidth = cellWidth;
			});
	
			var valueAxis = chartContainer.chart.yAxes.push(new am4charts.ValueAxis());
	
			for (i in nodesNamesY) {
				this_.create3DBarSeries(chartContainer.chart, nodesNamesY[i].id, nodesNamesY[i].name, i);
			}
		} else if (chartContainer.type === "DONUT") {
			// Create chart instance
			chartContainer.chart = am4core.create(id, am4charts.PieChart);
	
			// Let's cut a hole in our Pie chart the size of 40% the radius
			chartContainer.chart.innerRadius = am4core.percent(40);
	
			for (i in nodesNamesY) {
				this_.createDonutSeries(chartContainer.chart, nodesNamesY[i].id, nodesNamesY[i].name);
			}
		}
	
		chartContainer.chart.data = chartData;
		chartContainer.chart.language.locale = am4lang_ru_RU;
		chartContainer.chart.exporting.menu = new am4core.ExportMenu();
		chartContainer.chart.exporting.filePrefix = 'Aналитика';
	
		let datelabel = chartContainer.chart.createChild(am4core.Label);
		datelabel.align = "right";
		datelabel.scale = 2;
		datelabel.disabled = true;
		chartContainer.chart.exporting.validateSprites.push(datelabel);
	
		let titlelabel = chartContainer.chart.createChild(am4core.Label);
		titlelabel.isMeasured = false;
		titlelabel.align = "center";
		titlelabel.verticalCenter = "top";
		titlelabel.scale = 2;
		titlelabel.disabled = true;
		chartContainer.chart.exporting.validateSprites.push(titlelabel);
	
		chartContainer.chart.exporting.events.on("exportstarted", function (ev) {
			datelabel.disabled = false;
			titlelabel.disabled = false;
			const d = new Date()
			const date = d.toISOString().split('T')[0];
			const time = d.toTimeString().split(' ')[0];
			datelabel.text = `${date} ${time}`;
			titlelabel.text = $('#axes_title_' + id).text();
		});
	
		chartContainer.chart.exporting.events.on("exportfinished", function (ev) {
			datelabel.disabled = true;
			titlelabel.disabled = true;
		});
	
		chartContainer.chart.exporting.menu.items = [{
			"label": "Экспортировать",
			"menu": [
				{
					"label": "Изображение",
					"menu": [
						{ "type": "png", "label": "PNG" },
						{ "type": "jpg", "label": "JPG" },
						{ "type": "svg", "label": "SVG" },
						{ "type": "pdf", "label": "PDF" }
					]
				}, {
					"label": "Данные",
					"menu": [
						{ "type": "json", "label": "JSON" },
						{ "type": "csv", "label": "CSV" },
						{ "type": "xlsx", "label": "XLSX" },
						{ "type": "html", "label": "HTML" }
					]
				}, {
					"label": "Печатать", "type": "print"
				}
			]
		}];
	
		chartContainer.chart.exporting.adapter.add("pdfmakeDocument", function (pdf, target) {
			pdf.doc.pageOrientation = "landscape";
			return pdf;
		});
	
		chartContainer.chart.exporting.formatOptions.getKey("pdf").addURL = false;
		chartContainer.chart.exporting.formatOptions.getKey("png").addURL = false;
		chartContainer.chart.exporting.formatOptions.getKey("jpg").addURL = false;
		chartContainer.chart.exporting.formatOptions.getKey("svg").addURL = false;
		chartContainer.chart.exporting.formatOptions.getKey("json").addURL = false;
		chartContainer.chart.exporting.formatOptions.getKey("csv").addURL = false;
		chartContainer.chart.exporting.formatOptions.getKey("xlsx").addURL = false;
		chartContainer.chart.exporting.formatOptions.getKey("html").addURL = false;
		chartContainer.chart.exporting.formatOptions.getKey("print").addURL = false;
	
		this_.createLegend(chartContainer, nodesNamesY);
		this_.setShowLegend(chartContainer, chartContainer.showLegend);
	}
	
	loadAnalytic(id, chartContainer, idX, idY, zAxis, nodesNamesX, nodesNamesY, selectedZIds, fact, aggType, aggField) {
		_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.blockPage("Подождите...");
		var q = { "dim1": { "id": idX.id, "value": [] }, "dim2": { "id": idY.id, "value": [] }, "fact": fact, "agg": aggType, "value": aggField };
		for (var i in nodesNamesX) {
			q["dim1"].value.push(nodesNamesX[i].id);
		}
		for (i in nodesNamesY) {
			q["dim2"].value.push(nodesNamesY[i].id);
		}
		
		zAxis.forEach(filter => {
			let ids = selectedZIds[filter.id];
			if (ids.length) {
				let dim = { id: filter.id, value: [] };
				for (i in ids) {
					dim.value.push(ids[i].id);
				}
				if (q["dim3"] == undefined || q["dim3"] == null)
					q["dim3"] = [];
				
				q["dim3"].push(dim);
			}
		});
		
		var chartData;
		let this_ = this;
		$.ajax({
			type: "POST",
			url: this_.OLAP_URL + "ds/data",
			data: JSON.stringify(q),
			success: function (data) {
				chartData = data;
			},
			dataType: 'json',
			async: false
		});
	
		var empty = true;
		for (i in chartData) {
			for (var j in chartData[i])
				if (chartData[i][j]) {
					empty = false;
					break;
				}
		}
		if (empty) {
			$('#' + id).find('div').remove();
			$('#' + id).html('<p style="font-size:24;font-weight:bold;text-align:center;padding:20% 0">НЕТ ДАННЫХ ДЛЯ ОТОБРАЖЕНИЯ</p>');
			$('body').unblock();
			return;
		}
	
		if (chartContainer.chart)
			chartContainer.chart.dispose();
		this_.drawChart(id, chartContainer, chartData, nodesNamesX, nodesNamesY, this.nameX);
		this_.initDiagramType(id, chartContainer, chartData, nodesNamesX, nodesNamesY, this.nameX);
	
		$('body').unblock();
	}
	
	initBtn(id) {
		$(id).linkbutton({
			text: '<span style="font-size:15px;color:#fff;text-weight:normal;">Применить</span>'
		})
	}
	
	initTglLegBtn(id, chartContainer) {
		$(id).linkbutton({
			text: '<span style="font-size:15px;color:#fff;text-weight:normal;">' + (chartContainer.showLegend ? "Скрыть" : "Показать") + ' легенду</span>'
		})
	}
	
	hideBtn(id) {
		var content = $('#cnt0acc_' + id);
		$(content).removeClass('expanded-v');
		$('#t0acc_' + id).css('display', 'block');
		$('#t0acc_' + id).parent().parent().css('cursor', 'pointer');
		return false;
	}
	
	clearNodes(e) {
		if (e.children) {
			e.children.map(this.clearNodes.bind(this));
		}
		e.checked = false;
		e._checked = false;
		e.checkState = 'unchecked';
		return e;
	}
	
	selectNodes(e) {
		e.checked = true;
		e._checked = true;
		e.checkState = 'checked';
		return e;
	}
	
	clearDim(id) {
		var oldNodes = $(id).tree('getRoots');
		var newNodes = oldNodes.map(this.clearNodes.bind(this));
		$(id).tree('loadData', newNodes);
	}
	
	selectDim(id) {
		this.clearDim(id);
		var oldNodes = $(id).tree('getRoots');
		var newNodes = oldNodes.map(this.selectNodes);
		$(id).tree('loadData', newNodes);
	}
	
	clearComboDim(id) {
		var oldNodes = $(id).combotree('tree').tree('getRoots');
		var newNodes = oldNodes.map(this.clearNodes.bind(this));
		$(id).combotree('clear');
		$(id).combotree('loadData', newNodes);
	}
	
	selectComboDim(id) {
		this.clearComboDim(id);
		var oldNodes = $(id).combotree('tree').tree('getRoots');
		var newNodes = oldNodes.map(this.selectNodes);
		$(id).combotree('clear');
		$(id).combotree('loadData', newNodes);
	}

	initSelectBtns(id) {
		let this_ = this;
		$('#clear_dim_x_' + id).click(() => this_.clearDim('#x_' + id));
		$('#select_dim_x_' + id).click(() => this_.selectDim('#x_' + id));
		$('#clear_dim_y_' + id).click(() => this_.clearDim('#y_' + id));
		$('#select_dim_y_' + id).click(() => this_.selectDim('#y_' + id));
	}
	
	initFilterBtns(id, dimId) {
		let this_ = this;
		$('#clear_dim_filter_' + id + '_' + dimId).click(() => this_.clearComboDim('#filter_' + id + '_' + dimId));
		$('#select_dim_filter_' + id + '_' + dimId).click(() => this_.selectComboDim('#filter_' + id + '_' + dimId));
	}
		
}

/***/ }),

/***/ "./archive.js":
/*!********************!*\
  !*** ./archive.js ***!
  \********************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   "Archives": () => (/* binding */ Archives)
/* harmony export */ });
/* harmony import */ var _util_js__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./util.js */ "./util.js");


class Archives {

	imgExt = ".png";
	
	constructor(app) {
		this.app = app;
	}
	
	init() {
		let this_ = this;
		$('#archiveList_Layout').layout();
		$('#archiveTree').tree({
			onClick: function(node){
				this_.selectArchive(node);
			}
		});
		
		$("body").on('click', '#archiveList_body .archive', function() {
			var procId = $(this).attr("procId");
			this_.app.nav.openArchive(procId);
		});
		
		$('#dictsList_Layout').layout();
		$('#dictsTree').tree({
			onClick: function(node){
				this_.selectDict(node);
			}
		});
		
		$("body").on('click', '#dictsList_body .dict', function() {
			var procId = $(this).attr("procId");
			this_.app.nav.openDict(procId);
		});
		
	}
	
	selectArchive(node) {
		let this_ = this;
		return new Promise((resolve, reject) => {
			let par = {"cmd": "getArchiveData", "leaf": 1, "id": node.id};
			
			this.app.query(par).then(response => {
				if (response.status === 200) {
					response.json().then(data => {
						$('#archiveList_body').html("");
						$.each(data, function(e, proc) {
							var u = proc.id.indexOf(".");
							var key = proc.id.substring(u+1);
							var fontWeight = proc.fontWeight ? "" : "font-weight:bold;";
							$('#archiveList_body').append("<div time = '" + proc.time + "' desc = '" + proc.procDesc + "'id='processess" + key 
								+ "' class='archive' procId='" + proc.id
								+ "'><span class='ico_proc ico_" + proc.id.replace('.', '_') + "'></span>" 
								+ "<a style='"+fontWeight+"'>" + proc.title + "</a></div> ");
								
							this_.addProcIcon('#archiveList_body .proc.ico_' + proc.id.replace('.', '_'), this_.app.restUrl + '/jsp/media/css/or3/'+proc.id.replace('.', '_')+this_.imgExt);
							
							this_.attachProcessTooltips('processess'+key);
							
						});
						resolve();
					});
				}
			});
		});
	}
	
	selectDict(node) {
		let this_ = this;
		return new Promise((resolve, reject) => {
			let par = {"cmd": "getDictData", "leaf": 1, "id": node.id};
			
			this.app.query(par).then(response => {
				if (response.status === 200) {
					response.json().then(data => {
						$('#dictsList_body').html("");
						$.each(data, function(e, proc) {
							var u = proc.id.indexOf(".");
							var key = proc.id.substring(u+1);
							var fontWeight = proc.fontWeight ? "" : "font-weight:bold;";
							$('#dictsList_body').append("<div time = '" + proc.time + "' desc = '" + proc.procDesc + "'id='dict" + key 
								+ "' class='dict' procId='" + proc.id
								+ "'><span class='ico_proc ico_" + proc.id.replace('.', '_') + "'></span>" 
								+ "<a style='"+fontWeight+"'>" + proc.title + "</a></div> ");
								
							this_.addProcIcon('#dictsList_body .proc.ico_' + proc.id.replace('.', '_'), this_.app.restUrl + '/jsp/media/css/or3/'+proc.id.replace('.', '_')+this_.imgExt);
							
							this_.attachProcessTooltips('dict'+key);
							
						});
						resolve();
					});
				}
			});
		});
	}
	
	addProcIcon(className, src) {
		let this_ = this;
		var img = new Image();
		img.onload = function() {
			this_.addStyle(className+' {background-image: url('+src+') !important}');
		};
		img.src = src;
	}
	
	addStyle(st) {
	    var style = document.createElement('style');
		style.type = 'text/css';
		style.innerHTML = st;
		document.getElementsByTagName('head')[0].appendChild(style);
	}
	
	attachProcessTooltips(proc_id) {
		
	}
}

/***/ }),

/***/ "./autocomplete.js":
/*!*************************!*\
  !*** ./autocomplete.js ***!
  \*************************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   "Autocomplete": () => (/* binding */ Autocomplete)
/* harmony export */ });
/* harmony import */ var _util_js__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./util.js */ "./util.js");


class Autocomplete {
	
	static autocomplete(inp) {
		if(!inp) return false;
	
		new AutoInput(inp);
	}
	
}

class AutoInput {
	static heightRange = 40;

	constructor(input) {
		this.inp = input;
		this.currentFocus = 0;
		this.init();
	}
	
	init() {
		let _this = this;
	
		this.inp.addEventListener("input", function(e){
			/*
				не показывать поиск по одной букве
				if(val.length < 2){
					_this.closeAllLists();
					return false;
			} */
			_this.getUserDeals(this.value);
		});
		
		this.inp.addEventListener("keydown", function(e){ 
			var x = document.getElementById(this.id + "autocomplete-list");
			if(x) x = x.getElementsByTagName("div");
			if(e.keyCode === 40){
				_this.currentFocus++;
				_this.addActive(x);
			} else if(e.keyCode === 38){
				_this.currentFocus--;
				_this.addActive(x);
			} else if(e.keyCode === 13){
				e.preventDefault();
				if(_this.currentFocus > -1) {
					if(x) x[_this.currentFocus].click();
				}
			}
		});
		
		document.addEventListener("keydown", function(e){
			if(e.keyCode === 27){
				_this.closeAllLists();
			}
				
		});
		
		document.addEventListener("click", function(e) {
			_this.closeAllLists(e.target);
		});
	
	}

	getUserDeals(val) {
		let _this = this;
	
		var par = {"cmd":"getUserPrivateDeal", "text": val};
		
		_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.post2(par).then(function(json){
			_util_js__WEBPACK_IMPORTED_MODULE_0__.DataChecker.checkData(json).then(data => {
			
				var nameArr = (data.iinNames) ? data.iinNames.substr(1).split(";") : null;
				var uidArr = (data.iinNames) ? data.uids.substr(1).split(";") : null;
				
				_this.closeAllLists();
				
				if (!val) {return false;}
				_this.currentFocus = 0;
				
				let a = document.createElement("div");
				a.setAttribute("id", _this.inp.id + "autocomplete-list");
				a.setAttribute("class", "autocomplete-items");
				
				a.style.top = $(_this.inp).offset().top + $(_this.inp).height() + 4;
				a.style.left = $(_this.inp).offset().left;
				a.style.right = $( window ).width() - $(_this.inp).offset().left - $(_this.inp).width() - 44;
				
				document.body.appendChild(a);
				
				if(!nameArr || nameArr.lengh == 0) {
					_this.closeAllLists();
					return false;}
				for(var i = 0; i<nameArr.length; i++){
					let k = nameArr[i].toUpperCase().indexOf(val.toUpperCase());
					if(k !== -1){
						let b = document.createElement("div");
						b.setAttribute("id", _this.inp.id + "autocomplete-list" + i);
						b.innerHTML = nameArr[i].substr(0, k);
						b.innerHTML += "<strong>" + nameArr[i].substr(k, val.length) + "</strong>";
						b.innerHTML += nameArr[i].substr(k + val.length);
						b.innerHTML += "<input type='hidden' value='" + nameArr[i] + "'>";
						b.innerHTML += "<input type='hidden' value='" + i + "'>";
						b.addEventListener("click", function(e){						
							var index = this.getElementsByTagName("input")[1].value;
							var uid = uidArr[index];
							_this.closeAllLists();
							_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.blockPage();
							document.location.hash = "cmd=openLDIfc&uid=" + uid;
						});
						a.appendChild(b);
					}
				}
				var x = document.getElementById(_this.inp.id + "autocomplete-list");
				if(x) x = x.getElementsByTagName("div");
				_this.addActive(x);			
			});
		});
	
	}

	addActive(x){
		if(!x) return false;
		this.removeActive(x);
		let currentFocus = this.currentFocus;
		
		if(currentFocus >= x.length) currentFocus = 0;
		if(currentFocus < 0) currentFocus = x.length - 1;
		
		x[currentFocus].classList.add("autocomplete-active");
		
		var divTop = $("#" + x[currentFocus].id).offset().top;
		var parBottom = $(".autocomplete-items").height() + $(".autocomplete-items").offset().top - this.heightRange;
		if(divTop > parBottom){
			$(".autocomplete-items").scrollTop(x[currentFocus].offsetTop - $(".autocomplete-items").height() + x[currentFocus].offsetHeight);
		}
		else if(divTop < $(".autocomplete-items").offset().top){
			$(".autocomplete-items").scrollTop(x[currentFocus].offsetTop);
		}
	}
	
	removeActive(x){
		if(!x) return false;
		for(var i = 0; i<x.length; i++){
			x[i].classList.remove("autocomplete-active");
		}
	}
	
	closeAllLists(elmnt){
		var x = document.getElementsByClassName("autocomplete-items");
		for(var i =0; i<x.length; i++){
			if(elmnt != x[i] && elmnt != this.inp){
				x[i].parentNode.removeChild(x[i]);
			}
		}
	}

}


/***/ }),

/***/ "./errors.js":
/*!*******************!*\
  !*** ./errors.js ***!
  \*******************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   "ErrorOps": () => (/* binding */ ErrorOps)
/* harmony export */ });
class ErrorOps {
	
	higlightErrors(ers) {
		let _this = this;
		var list = ["<ul class='error-red'>","<ul class='error-blue'>","<ul class='error-gray'>"];
		$.each(ers, function(i,er) {
			var rownum = -1;
			if ($("td[field="+ er.uuid +"]").length > 1) {
				rownum = er.msg.substring(er.msg.lastIndexOf('(')+1, er.msg.lastIndexOf(')')) - 1;
			}
			list[er.type] += "<li uuid='" + er.uuid + "'";
			if (rownum > -1) {
				list[er.type] += " row='" + rownum + "'";
			}
			list[er.type] += ">" + er.msg.replace(/\n/g, '<br/>') + "</li>";
			_this.higlightErrorComp(er, rownum);
		});
		list[0]+="</ul>";
		list[1]+="</ul>";
		list[2]+="</ul>";
		
		return list[0]+list[1]+list[2];
	}
	
	higlightErrorComp(error, rownum) {
		var e = $('#'+error.uuid);
	
		// подсветка ячейки в таблице
		if (rownum > -1) {
			var td_field = $("td[field="+ error.uuid +"]");
			if (td_field.length >= rownum) {
				$(td_field).parent().parent().find("[datagrid-row-index="+ rownum +"] > td[field="+ error.uuid +"]")
					.toggleClass("error-type-"+error.type, true);
			}
		// подсветка поля
		} else {
			this.setErrorType(e, error.type);
		}
	}
	
	setErrorType(e, errID) {
		let this_ = this;
		if ($(e).hasClass('easyui-datebox')) {
			if (!$(e).hasClass('datebox-f')) comp.datebox();
			e = $(e).datebox("textbox").parent();
			$(e).toggleClass("error-type-"+errID, true);
			return ;
		} else if ($(e).hasClass('easyui-datetimebox')) {
			if (!$(e).hasClass('datetimebox-f')) comp.datetimebox();
			e = $(e).datetimebox("textbox").parent();
			$(e).toggleClass("error-type-"+errID, true);
			return ;
		} else if ($(e).hasClass('easyui-combobox')) {
			if (!$(e).hasClass('combobox-f')) $(e).combobox();
			e = $(e).combobox("textbox").parent();
			$(e).toggleClass("error-type-"+errID, true);
			return;
		} else if ($(e).hasClass('easyui-numberbox')) {
			if (!$(e).hasClass('numberbox-f')) $(e).numberbox();
			e = $(e).numberbox("textbox").parent();
			$(e).toggleClass("error-type-"+errID, true);
			return;
		} else if ($(e).is("input") 
				|| $(e).is("select")
				|| $(e).is("textarea")
				|| $(e).hasClass("textbox-text") 
				|| $(e).hasClass("datebox")
				|| $(e).hasClass("datetimebox")){
			$(e).one("change", function () {
				this_.delErrorType($(this));
		    });
			$(e).toggleClass("error-type-"+errID, true);
			return ;
		}
	}

	delErrorType(e) {
		if ($(e).hasClass('easyui-datebox')) {
			if (!$(e).hasClass('datebox-f')) $(e).datebox();
			e = $(e).datebox("textbox").parent();
		}
		else if ($(e).hasClass('easyui-datetimebox')) {
			if (!$(e).hasClass('datetimebox-f')) $(e).datetimebox();
			e = $(e).datetimebox("textbox").parent();
		}
		else if ($(e).hasClass('easyui-combobox')) {
			if (!$(e).hasClass('combobox-f')) $(e).combobox();
			e = $(e).combobox("textbox").parent();
			
		}
		else if ($(e).hasClass('easyui-numberbox')) {
			if (!$(e).hasClass('numberbox-f')) $(e).numberbox();
			e = $(e).numberbox("textbox").parent();
		}
		var cl = $(e).attr('class');
		if (cl !== undefined){
			var classList = cl.split(/\s+/);
			$.each( classList, function(index, item){
				if (item.indexOf('error-type-') >= 0) {
					$(e).toggleClass(item, false);
				}
			});
		}
	}
	
	clearAllErrorType() {
		let this_ = this;
		$.each($("[class*=error-type-]"), function(i, em){
			this_.delErrorType(em);
		});
	}
}

/***/ }),

/***/ "./events.js":
/*!*******************!*\
  !*** ./events.js ***!
  \*******************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   "EventOps": () => (/* binding */ EventOps)
/* harmony export */ });
class EventOps {

	static ctrlPressed = false;
	static shiftPressed = false;
	
	static init(app) {
		let _this = this;
		$(window).keydown(function(e) {
			if (e.which == 16) {
				_this.shiftPressed = true;
			} else if (e.which == 17) {
				_this.ctrlPressed = true;
			}
			if (e.ctrlKey) {          
				if (e.keyCode == 65) {                         
					e.preventDefault();
					return false;
				}            
			}
		}).keyup(function(e) {
			if (e.which == 16) {
				_this.shiftPressed = false;
			} else if (e.which == 17) {
				_this.ctrlPressed = false;
			} else if (e.which == 114) {
				if (searchTreeId != null && searchTreeVal != null) {
					if ($('#_' + searchTreeId).hasClass('treetable-search'))
						app.ifcController.findTreeTableNode(searchTreeId, searchTreeVal);
					else if ($('#_' + searchTreeId).hasClass('table-search'))
						app.ifcController.findTableNode(searchTreeId, searchTreeVal);
		//			else if ($('#_' + searchTreeId).hasClass('treefield-search'))
		//				findNode(searchTreeId, searchTreeVal, true);
					else
						app.ifcController.findNode(searchTreeId, searchTreeVal, false);
				} 
			}
		});
		
		$(window).mousedown(function(e){
			if(e.shiftKey){
				e.preventDefault();
				return false;
			}
		});
		$("#ui-body").on("click", function() {
			$("input").off("keyup");
			$("input").keyup(function (e) {
				if (e.keyCode == 13) {
					console.log("input keyup events js")
					$(this).trigger('blur');
				}
			});

		})

		
		
	}
}

/***/ }),

/***/ "./help.js":
/*!*****************!*\
  !*** ./help.js ***!
  \*****************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   "Help": () => (/* binding */ Help)
/* harmony export */ });
/* harmony import */ var _util_js__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./util.js */ "./util.js");


class Help {

	constructor(app) {
		this.app = app;
	}
	
	init() {
		let _this = this;
		
		//$('#help-panel .portlet').css('height', ($(window).height()-160)+"px");
		//$('.easyui-panel:not(.tamur-tabs)').panel();
			
		this.loadHelp();
	}
	
	loadHelp() {
		let _this = this;
		var par = {"sfunc":1, "cls":"XmlUtil", "name":"getHelpTabs"};
		this.app.post(par).then(data => {
			_this.parseHelpTabs(data)
		});
	}
	
	parseHelpTabs(res) {
		let _this = this;
		
		if (res.tabs) {
			$.each(res.tabs, function(i, tab) {
				var html = '<div class="Ek-help" title="' + tab.title + '"';

				if (tab.pic && tab.pic.length > 10) {
					html += 'data-options="iconCls:\'icon-' + tab.id + '\'"';

					var css = '.icon-' + tab.id +' {background: url("' + tab.pic + '") no-repeat scroll center center}';
					$('style').first().append('\n' + css);
				}

				html += ' style="padding:20px;">';
				
				var par = {"sfunc":1,"cls":"XmlUtil", "name":"getHelpContent", "arg0":tab.id};
				_this.saveAjaxRequest('POST', window.mainUrl, par, function(content) {
					if (content)
						content = content.replace('name=getHelpFile&', 'guid=' + guid + '&name=getHelpFile&');
					
					html += content;
				}, 'html', false);
				html += '</div>';

				$('#help-tab-panel').append($(html));
			});
			
			$('#help-tab-panel').tabs({
			    border:false			    
			});
			
			$( ".aboutSys" ).click(function() {
				$(this).next().toggle();
			});
		}
	}
	
	saveAjaxRequest(type, url, params, success, dataType, async) {
		params["rnd"] = _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.rnd();
		params["guid"] = guid;
	
		$.ajax({
			type : type,
			url : url,
			data : params,
			success : success,
			dataType : dataType,
			async : async
		});
	}
		
}

/***/ }),

/***/ "./ifc.js":
/*!****************!*\
  !*** ./ifc.js ***!
  \****************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   "InterfaceController": () => (/* binding */ InterfaceController)
/* harmony export */ });
/* harmony import */ var _util_js__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./util.js */ "./util.js");
/* harmony import */ var _tables_js__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./tables.js */ "./tables.js");
/* harmony import */ var _errors_js__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./errors.js */ "./errors.js");
/* harmony import */ var _tooltip_js__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./tooltip.js */ "./tooltip.js");
/* harmony import */ var _translation_js__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ./translation.js */ "./translation.js");






class InterfaceController extends _tables_js__WEBPACK_IMPORTED_MODULE_1__.TableOps {
	
	dialogZindex = 345000;
	messagerZindex = 350000;

	lastShowDate = null;
	lastShowComboValue = null;
	focusedTable = undefined;
	
	popupcount = 0;
	dialogResult = [];
	
	multiSelection = {};
	dialogOpened = null;
	
	fatal = false;
	isDataIntegrityControl = false;
	opts = 0;

	// whats this
	data = 0;
	DLG_CHANGE_PD = 5; // создал Жаркын. Правильно ли сделал?

	lastDownloadTime = 0;
	waitTime = 1500;
	
	searchCount = 0;

	static popDlg = [];
	static popDlgType = [];
	static styles = {};
	static cssStyles = {};
	static openedDialogs = [];
	static attentionMap = {};
	hiddenPanels = {}
	
	constructor(app) {
		super(app);
		this.app = app;
		this.errorUtil = new _errors_js__WEBPACK_IMPORTED_MODULE_2__.ErrorOps();
	}
	
	init() {
		super.initDefaults(this);
		
		let _this = this;
		
		_this.setFocus();

		$("body").append($("<div></div>").attr("id", "trash").css("display", "none"));
		$("#trash").append($("<iframe></iframe>").attr("id", "report_frame").css("display", "none"));

		$('#ui-body').panel({
			onLoad: function() {
				if (typeof $("#ui-body .mainPanel").attr("data-uiTitle") !== "undefined") {
					$('.ui-title').text($("#ui-body .mainPanel").attr("data-uiTitle"));	
				}
				_this.preparefileUpload();
				
				_this.clickPopUpContent('ui-body');
				_this.showPopUpContent();
				_this.movePopUpContent();
				
				// удалить все предыдущие элементы
				$(".ui-toolbar").find("[onTop]").remove();
				
				_this.loadData({}, true).then(() => {
					$('#ui-header').show();
					_this.setElemForTopPane();
					_this.app.resizeHeight();
					$('#ui-body').panel('resize');
					_this.app.resize('#ui-body');
				});
				_this.loadReports();
				_this.app.analytic.init();
				
				$('.easyui-datebox').parent().find('.textbox-text').inputmask("d.m.y", {
					"placeholder": "дд.мм.гггг",
					"insertMode" : false,
				});
				$('.easyui-datetimebox.minutes').parent().find('.textbox-text').inputmask("d.m.y h:s", {
					"placeholder": "дд.мм.гггг чч:ММ",
					"insertMode" : false,
				});
				$('.easyui-datetimebox.seconds').parent().find('.textbox-text').inputmask("d.m.y h:s:s", {
					"placeholder": "дд.мм.гггг чч:ММ:сс",
					"insertMode" : false,
				});
			}
		});
		
		$(window).bind('resizeEnd', function() {
			$('#ui-body').find('.panel, .panel-body, .easyui-fluid, .easyui-panel, .easyui-tabs, .datagrid-view, .datagrid-view2, .datagrid-header, .datagrid-body, .datagrid-footer').width(0);
			$('#ui-body').width(0);
			$('#ui-body').panel('resize');
			_this.app.resize('#ui-body');
		});
		
		_this.initDefaults();
		
		// при изменении значения любого отправляем на сервер
		$("body").on('change', 'input, select, textarea, iframe', function() {
			var id = $(this).attr('id') || $(this).parent().prev().attr('id');
			var comboSearch = $(this).parent().prev().hasClass('easyui-combobox');
			if (id == 'cellDatabox') {
				_this.endEditing();
			} else if (id) {
				var val = "";
				if ($(this).attr('type') == 'checkbox') {
					val = $(this).prop('checked');
				} else 
					val = $(this).val();
				if (!comboSearch) {
					_this.setValue(id, val);
				}
			}
		});
		
		$("body").on('click', 'button.popup', function() {
			if (!$(this).hasClass('btn-disabled')) {
				let uid = $(this).attr("id");
				$('.menu-content,.menu-shadow').hide();
				_this.openPopup(uid);
			}
		});
		
		$("body").on('click', 'a.popup', function() {
			if (!$(this).hasClass('btn-disabled')) {
				let uid = $(this).attr("id");
				$('.menu-content,.menu-shadow').hide();
				_this.openPopup(uid);
			}
		});
		
		$("body").on('click', 'a.hyper', function() {
			if (!$(this).hasClass('btn-disabled')) {
				let uid = $(this).attr("id");
				_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.blockPage();
				document.location.hash = "cmd=openIfc&uid=" + uid + "&rnd=" + _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.rnd();
			}
			return false;
		});
		
		$("body").on('click', 'a.treeField', function() {
			if (!$(this).hasClass('btn-disabled')) {
				let uid = $(this).attr("id");
				_this.openTree(uid, $(this).attr("title"));
			}	
			return false;
		});

		$("body").on('click', '.clean-btn', function(e) {
			var par = {cmd: "clr", uid: $(this).attr('id').substring(3)}; // первые три символа это префикс "clr"
			_this.postAndParseData(par);
			return false;
		});

		$("body").on('click', 'a[reportid]', function() {
			_this.reportClick($(this));
		});

		$("body").on('click', '#langKz, #topLangKz', function(e) {
			_this.changeInterfaceLang('KZ', e);
			localStorage.setItem("EkyzmetLanguage", "KZ");
		});
		$("body").on('click', '#langRu, #topLangRu', function(e) {
			_this.changeInterfaceLang('RU', e);
			localStorage.setItem("EkyzmetLanguage", "RU");
		});
		
		$("body").on('keypress', 'span.numberbox input.textbox-text', function(e) {
			var maxlength = $(this).parent().prev().attr('maxlength');
			if (maxlength) {
				if (isCharacterKeyPress(e)) {
					var length = $(this).val().split(',')[0].length;
					if (length >= parseInt(maxlength)) {
						return false;
					}
				}
			}
			return true;
		});
		
		$("body").on('click', '.coll-pan .ttl-coll-pan', function() {
			// первый символ это префикс "t", cnt - префикс контента
			var content = $('#cnt'+ $(this).attr('id').substring(1));
		    if( $(content).hasClass('expanded') ){
		        $(content).removeClass('expanded');
		    } else {
		    	$(content).addClass("expanded");
		    }
		    return false;
		});
		
		$("body").on('click', '.coll-pan-v .ttl-coll-pan-v', function() {
			// первый символ это префикс "t", cnt - префикс контента
			var content = $('#cnt'+ $(this).attr('id').substring(1));
		    if( $(content).hasClass('expanded-v') ){
		        $(content).removeClass('expanded-v');
		    } else {
		    	$(content).addClass("expanded-v");
		    }
		    return false;
		});
		
		$("body").on('click', '.arr-parent', function() {
			var content = $('#cnt'+ $(this).attr('id').substring(1));
		    $(content).addClass("expanded-v");
		    if ($(this).css('display') == 'block')
				$(this).css('display', 'none');
		    return false;
		});
		
		$("body").on('click', '.accordion .ttl-coll-pan', function() {
			var parent = $(this).parent();
			// первый символ это префикс "t", cnt - префикс контента
			var content = $('#cnt'+ $(this).attr('id').substring(1));
			if ($(content).hasClass('accord')) {		
				var panID = $(content).attr('id');
				if($(parent).attr('multi') == '0'){
					var panels = $(parent).find('.cnt-coll-pan');
					for (var j = 0; j < panels.length; j++) {
						if($(panels[j]).attr('id') != panID && $(panels[j]).hasClass('expanded')){
							$(panels[j]).removeClass('expanded');
						}
					}
				}
			    if($(content).hasClass('expanded') ){
			        $(content).removeClass('expanded');
			    } else {
			    	$(content).addClass("expanded");
			    }
		    }
		    return false;
		});
		
		$("body").on('click', '.accordion-v .ttl-coll-pan-v', function() {
			var parent = $(this).parents('.accordion-v');
			// первый символ это префикс "t", cnt - префикс контента
			var content = $('#cnt'+ $(this).attr('id').substring(1));
			if($(content).hasClass('accord')) {
				var panID = $(content).attr('id');
				if($(parent).attr('multi') == '0'){
					var panels = $(parent).find('.cnt-coll-pan-v');
					for (var j = 0; j < panels.length; j++) {
						if($(panels[j]).attr('id') != panID && $(panels[j]).hasClass('expanded-v')){
							$(panels[j]).removeClass('expanded-v');
						}
					}
				}
			    if($(content).hasClass('expanded-v')){
			        $(content).removeClass('expanded-v');
			    } else {
			    	$(content).addClass("expanded-v");
			    }
		    }
		    return false;
		});
	}
	
	initDefaults() {
		let _this = this;
		$.blockUI.defaults.baseZ = 300000;

		$.extend($.fn.window.defaults, {zIndex: 110000, shadow: false});

		$.extend($.messager.defaults, {zIndex: this.messagerZindex, width: 450, minHeight: 100});
		
		$.fn.tabs.defaults.onSelect = function(title,index) {
			var tp = $(this);
			var par = {"cmd":"selTab","uid":tp.attr('id'),"indx":index};
			
			_this.displayEmptyParentPanels(tp.tabs('getSelected'));
			_this.app.post(par).then(data => {
				$.each(_this.tablesToReloadMap, function(key, value) {
					$.each($('#' + key).parents('.easyui-tabs'), function (i, tb) { 
						if ($(tb).attr('id') == tp.attr('id')) {
							$('#' + key).datagrid('load');
						}
					});
				});
				tp.find('.easyui-tabs').tabs('resize');
			});
		};
		
		$.extend($.fn.combo.defaults, {zIndex: 150000});
		
		$.extend($.fn.combobox.defaults, {panelHeight: 'auto', panelMaxHeight: 400});

		// поиск в комбобоксе с учетом регистра или без по флагу options.toUpperCase
		$.extend($.fn.combobox.defaults.filter = function(q,row){
			var opts = $(this).combobox('options');
			var toUpperCase = opts.toUpperCase;
			var s = row[opts.textField];
			if (toUpperCase) {
				for (var si in s) {
					if ((typeof s[si] == "string" && s[si].toUpperCase().indexOf(q.toUpperCase()) >= 0)
					 	|| (s[si].title && s[si].title.toUpperCase().indexOf(q.toUpperCase()) >= 0)) {
						return true;
					}
				}
				return false;
			} else {
				for (var si in s) {
					if ((typeof s[si] == "string" && s[si].indexOf(q) >= 0)
					 	|| (s[si].title && s[si].title.indexOf(q) >= 0)) {
						return true;
					}
				}
				return false;
			}
		});
		
		$.extend($.fn.menu.defaults, {
			zIndex: 120000,
			onHide: function() {
				if($('.calendar:visible').length > 0)
					$(this).menu('show');
			},
			onShow: function() {
				if ($(this).attr('id') != null && $(this).attr('id').indexOf('pop') === 0)
					$(this).unbind('mouseleave.menu').unbind('mousedown.menu');
			}
		});
		
		
		$.fn.datebox.defaults.onShowPanel = function() {
			_this.lastShowDate = $(this).datebox("getText");
		};
		$.fn.datebox.defaults.onHidePanel = function() {
			var id = $(this).attr('id');
			var date = $(this).datebox("getText");
			if (id == 'cellDatabox') {
				_this.endEditing();
			} else if (id && (date != _this.lastShowDate || date == "")) {
				_this.setValue(id, date);
				_this.focusNext($(this).datebox("textbox"));
			}
		};
		$.fn.datetimebox.defaults.onShowPanel = function() {
			_this.lastShowDate = $(this).datetimebox("getText");
		};
		$.fn.datetimebox.defaults.onHidePanel = function() {
			var id = $(this).attr('id');
			var date = $(this).datetimebox("getText");
			if (id == 'cellDatabox') {
				_this.endEditing();
			} else if (id && (date != lastShowDate || date == "")) {
				_this.setValue(id, date);
				_this.focusNext($(this).datetimebox("textbox"));
			}
		};
		
		$.fn.combobox.defaults.onShowPanel = function() {
			_this.lastShowComboValue = $(this).combobox("getValue");
			//var textboxCombo = $(this).combo("textbox");
			//var panelCombo = $(this).combo("panel");
			//panelCombo.children(".combobox-item").css({"font-size":textboxCombo.css("font-size"),"font-family":textboxCombo.css("font-family"),"font-style":textboxCombo.css("font-style"),"font-weight":textboxCombo.css("font-weight")});
			//panelCombo.css("background-color", "rgb(255, 222, 173)");
		};
		$.fn.combobox.defaults.onHidePanel = function() {
			var tableCombobox = $(this).hasClass("datagrid-editable-input");
			if (tableCombobox)
				_this.endEditing();
			else {
				var id = $(this).attr('id');
				var opts = $(this).combobox("options");
				var val = $(this).combobox("getValue");
				var data = $(this).combobox("getData");
				var cleanCombo = true;
				for(var i = 0; i<data.length; i++) {
					if(data[i][opts.valueField] === val && id && val != _this.lastShowComboValue && val != undefined) {
						_this.setValue(id,val);
						cleanCombo = false;
						break;
					}
				}
				if (cleanCombo && val != _this.lastShowComboValue && id)
					$(this).combobox('clear');
			}
		};
		$.fn.combobox.defaults.onChange = function(newValue, oldValue) {
			if (newValue != oldValue)
				_this.errorUtil.delErrorType($(this));
		}
		$.fn.datebox.defaults.onChange = function(newValue, oldValue) {
			if (newValue != oldValue)
				_this.errorUtil.delErrorType($(this));
		}
		$.fn.datetimebox.defaults.onChange = function(newValue, oldValue) {
			if (newValue != oldValue)
				_this.errorUtil.delErrorType($(this));
		}
		
		$.fn.numberbox.defaults.onChange = function(newValue, oldValue) {
			if (newValue != oldValue)
				_this.errorUtil.delErrorType($(this));
		}
		
		$("body").on('keyup', ".datagrid-editable-input, .numberbox .textbox-text", function(e) {
			if (e.which && e.which == 13)
				_this.endEditing();
		});
		
		$("body").on('click', '.ortable', function(e) {
			_this.endEditing();
		});
		
		$("body").on('click', '.or3-btn, .popup', function(e) {
			e.preventDefault();
			_this.endEditing();
		});
		
		$("body").on('keyup', ".datagrid-editable .datebox .textbox-text", function(event) {
			if (event.which && event.which == 13) {
				$(this).parent().prev().combo('hidePanel');
				$(this).blur();
				_this.endEditing();
			}
		});
		
		$("body").on('click', '.or3-btn:not(.view-file)', function(e) {
			e.preventDefault();
			if ($(this).attr('id') && !$(this).hasClass('btn-disabled')) {
				_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.blockPage();
				_this.setValue($(this).attr('id'), 1);
			}
		});
		
		$("body").on('click', '.view-file', function(e) {
			if (!$(this).hasClass('btn-disabled')&& $(this).attr('id')) {
				_this.downloadFile(e, $(this).attr('id'));
			} else if ($(this).parent().attr('id')) {
				_this.downloadFile(e, $(this).parent().attr('id'),$(this).parent().attr('index'));
			}
		});
		
		$("body").on('click', '.open-file', function(e) {
			if ($(this).attr('uid')) {
				var cls = $(this).attr('cls');
				var method = $(this).attr('method');
				var uid = $(this).attr('uid');
				var path = $(this).attr('path');
				var fn = $(this).attr('fn');
				var ext = $(this).attr('ext');
				var index = $(this).attr('index');
		
				var url = _this.app.restUrl + "&sfunc&cls=" + cls + "&name=" + method + "&ext=" + ext + "&fn=" + encodeURIComponent(fn) + "&arg0=" + uid + "&arg1=" + encodeURIComponent(path);
				if (index)
					url += "&arg2=" + index;
				$('#report_frame').attr('src', url);
			}
		});
		
		$("body").on('click', '.delete-file', function(e) {
			if ($(this).parent().attr('id')) {
			    var par = {};
				par["uid"] = $(this).parent().attr('id');
				par["cmd"] = "set";
				par["com"] = "del";
				par["ind"] = $(this).parent().attr('index');
		
				_this.loadData(par);
		
				return false;
			}
		});
	}
	
	setValue(id, val) {
		this.showChangeMsg();
		var par = {"uid":id,"val":(val != null ? val : ""),"cmd":"set"};
		this.loadData(par);
	}

	showChangeMsg() {
		$('#saveBtn').linkbutton('enable');
		$('#cancelBtn').linkbutton('enable');
	}
	hideAllChangeMsg() {
		$('#saveBtn').linkbutton('disable');
		$('#cancelBtn').linkbutton('disable');
	}
	hideChangeMsg() {
		$('#saveBtn').linkbutton('disable');
		$('#cancelBtn').linkbutton('disable');
	}

	loadData(par) {
		let _this = this;
		
		return new Promise((resolve, reject) => {
			par['getChange'] = '1';
			_this.app.query(par).then(response => {
				response.json().then(json => {
					_util_js__WEBPACK_IMPORTED_MODULE_0__.DataChecker.checkData(json).then(data => {
						if (data.changes && data.changes.length > 0)
							_this.parseData(data);
							
						$('body').unblock();
						resolve(data);
					});
				});
			});
		});
	}
	
	postAndParseData(par) {
		let _this = this;
		return new Promise((resolve, reject) => {
			_this.app.query(par).then(response => {
				response.json().then(json => {
					_util_js__WEBPACK_IMPORTED_MODULE_0__.DataChecker.checkData(json).then(data => {
						if (data.changes && data.changes.length > 0)
							_this.parseData(data);
						resolve(data);
					});
				});
			});
		});
	}

	parseData(data) {
		let _this = this; 
		if (data.changes) {
			// Пробегаем массив изменений
			$.each(data.changes, function(i, change) {
				// Каждое изменение - это отношенией id компонента - набор измененных свойств
				$.each(change, function(id, props) {
					var comp = null;
					// Пробегаемся по открытым диалогом в обратном порядке и ищем компонент
					for (var di = InterfaceController.openedDialogs.length - 1; di >= 0; di--) {
						var dialog = $('#' + InterfaceController.openedDialogs[di]);
						comp = dialog.find('#' + id);
						// Если компонент найден - выходим из цикла
						if (comp.length > 0) break; 
					}
					// Если компонент не найден в диалогах - ищем по всему окну
					if (comp == null || comp.length == 0) comp = $('#' + id);
					
					// Пробегаемся по всем измененным свойствам
					$.each(props, function(key, value) {
						_this.updateComponent(id, comp, key, value);
						
					});
				});
			});
		}
		_this.app.resize('#ui-body');
	}
	
	updateComponentStyle(comp, key, value, e_tagName, e_type) {
		delete value['min-width']; // for future may be fixed
		delete value['height'];
		delete value['min-height'];
		delete value['margin'];
		if (e_tagName=='TABLE') {
			delete value['margin-right'];
			delete value['margin-left'];
			delete value['margin-bottom'];
			delete value['margin-top'];
			delete value['width'];
		}
		if (value['background-color'] != null && value['background-color'].indexOf("background-color")>-1) {
			value['background-color'] = value['background-color'].replace("background-color","");
		}
		if (e_tagName == 'BUTTON' || e_tagName == 'A' || e_tagName=="TEXTAREA") {
			// do nothing
		} else if(e_tagName == 'INPUT' && e_type == "file") {
			comp.parent().css(value);
		} else if(e_tagName == 'INPUT') {
			if (comp.hasClass('easyui-combobox')) {
				if (!comp.hasClass('combobox-f')) comp.combobox();
				$.each(value, function (akey, aval){
					if (akey == "margin-top" || akey == "margin-bottom" || akey == "margin-left" || akey == "margin-right"){
						comp.next().css(akey, aval);
					} else if (akey == "font-family" || (akey == "font-size" && aval > 0)) {
						comp.combobox("textbox").css(akey, aval);
					} else if (akey == "font-style") {
						if (aval == 1) comp.combobox("textbox").css("font-weight","bold");
						if (aval == 2) comp.combobox("textbox").css("font-style","italic");
						if (aval == 3) comp.combobox("textbox").css("font-style","italic").css("font-weight","bold");
					}
				});
			}
		} else {
			comp.css(value);
		}
	}

	updateComponent(i, comp, key, value) {
		let _this = this; 

		let e_tagName = comp.prop("tagName");
		let e_type = comp.attr("type");
		let e_class = comp.attr("class");
		let e_id = comp.attr("id");
		
		if (key == 'st') {
			_this.updateComponentStyle(comp, key, value, e_tagName, e_type);
		} else if (key == 'pr') {
			var reloadRows = undefined;
			var comboValue = undefined;
			$.each(value, function(akey, aval) {
				if (akey == "heads") return;
				if (e_tagName == "SELECT" && akey == "size") return;
				if (e_tagName == "TEXTAREA" && akey == "size") return;
				if (e_tagName == "TABLE" && (akey == "height" || akey == "width") ) return;
				if (e_tagName == 'INPUT' && e_type=="file" && akey != "e" && akey != "v" && akey != "text" && akey != 'OrWebDocFieldProps' && akey != "reloadUpload") return;
				if (akey == 'text') {
					if (aval != null && $.trim(aval).length > 0) {
						_this.errorUtil.delErrorType(comp);
					}
					if (e_type == 'radio') {
						comp.find('input[type=radio][value="' + aval + '"]').prop('checked', true);
					} else if (e_tagName == 'INPUT' && e_type=="checkbox") {
						// Следующий элемент
						var ne = comp[0].nextSibling;
						if (ne == null || ne.nodeType != 3) {
							comp.after(document.createTextNode('-'));
							ne = comp[0].nextSibling;
						}
						ne.nodeValue = aval;
					} else if (e_tagName == 'INPUT' && e_type=="file") {
						comp.parent().find('span.btn-label').html(aval.replace(/@/g,'<br/>'));
					} else if (e_tagName == 'BUTTON') {
						$('#' + i+' span').html(aval.replace(/@/g,'<br/>'));
					} else if (e_tagName == 'INPUT') {
						comp.val(aval);
						if (comp.hasClass('easyui-datebox')) {
							if (!comp.hasClass('datebox-f')) comp.datebox();
							comp.datebox('setValue', aval); 
						} 
						else if (comp.hasClass('easyui-datetimebox')) {
							if (!comp.hasClass('datetimebox-f')) comp.datetimebox();
							comp.datetimebox('setValue', aval); 
						}
						else if (comp.hasClass('easyui-numberbox')) {
							comp.numberbox('setValue', aval);
						}
					} else if (e_tagName == 'TEXTAREA' && e_class == 'tinyMCE') {
						tinyMCE.get(e_id).setContent(aval);
					} else if (e_tagName == 'TEXTAREA' && e_class != 'tinyMCE') {
						comp.val(aval);
					} else if (comp.hasClass('easyui-linkbutton')) {
						comp.linkbutton({text:aval});
					} else if (e_tagName == 'A') {
						if (aval != null && aval.replace) {
							if (comp.hasClass("hyper")) {
								comp.html(aval.replace(/@/g,'<br/>'));
							} else if (comp.hasClass("treeField")) {
								while (comp.contents().filter(function() {return this.nodeType == 3;}).length > 0) {
									comp.contents().filter(function() {return this.nodeType == 3;}).eq(0).replaceWith('');
								}
								comp.find('br').remove();
								comp.append(document.createTextNode('-'));
								comp.contents().filter(function() {return this.nodeType == 3;}).eq(0).replaceWith(aval.replace(/@/g,'<br/>'));
							} else {
								comp.find('span.btn-label').html(aval.replace(/@/g,'<br/>'));
							}
						}
					} else {
						if (aval != null && aval.replace) {
							comp.html(aval.replace(/@/g,'<br/>'));
							if (comp.hasClass('nicEdit-main')) {
								var parent = comp.parents().eq(2).find('textarea#' + comp.attr('id')).html(aval.replace(/@/g,'<br/>'));
							}
						}
					}
				} else if (akey == 'OrWebDocFieldProps') {
					comp.parent().find('span.btn-label').html(aval.text.replace(/@/g,'<br/>'));
					var spanElement = document.getElementById(aval.id).parentNode;
					if (aval.mode == 0) {
						spanElement.children[1].removeAttribute("id");
						spanElement.children[1].removeAttribute("action");
						spanElement.children[1].className = "docField";
						document.getElementById(aval.id).style.display  = "block";
					} else {
						document.getElementById(aval.id).style.display  = "none";
						spanElement.children[1].setAttribute("id", aval.id);
						spanElement.children[1].setAttribute("action", "0");
						spanElement.children[1].className = "or3-btn view-file trBtn";
					}
					if (aval.iconBytes.length > 0) {
						spanElement.children[0].style.display  = "inline";
						spanElement.children[0].src = "data:image/png;base64," + aval.iconBytes; 
					} else {
						spanElement.children[0].style.display  = "none";
					}
				} else if (akey == 'rootPanelTitle') {
					$("#uiTitle").text(aval);
					$(".fullPath-l").text(aval);
				} else if (akey == "hyperPopupAttention") {
					if (aval == 1) {
						InterfaceController.attentionMap[i] = setInterval(function() {
							if ($('#' + i).length > 0) {										
								if (document.getElementById(i).style.backgroundColor == "palegreen") {
									document.getElementById(i).style.background  = "initial";
								} else {
									document.getElementById(i).style.background  = "palegreen";
								}
							}
						}, 1000);
					} else {
						clearInterval(InterfaceController.attentionMap[i]);
						delete InterfaceController.attentionMap[i];
						document.getElementById(i).style.background  = "initial";
					}
				} else if (akey == "buttonAttention") {
					if (aval == 1) {
						InterfaceController.attentionMap[i] = setInterval(function() {
							if ($('#' + i).length > 0) {										
								if (document.getElementById(i).style.backgroundColor == "rgb(79, 209, 197)") {
									document.getElementById(i).style.background  = "initial";
								} else {
									document.getElementById(i).style.background  = "#4fd1c5";
								}
							}
						}, 1000);
					} else {
						clearInterval(InterfaceController.attentionMap[i]);
						delete InterfaceController.attentionMap[i];
						document.getElementById(i).style.background  = "initial";
					}
				} else if (akey == "dateFieldAttention") {
					var tb = $('#' + i).datebox('textbox');
					if (aval == 1) {
						InterfaceController.attentionMap[i] = setInterval(function() {
							var color = tb.add(tb.parent()).css("background-color");
							if (color == "rgb(152, 251, 152)") {
								tb.add(tb.parent()).css("background-color","white");
							} else {
								tb.add(tb.parent()).css("background-color","palegreen");
							}
						}, 1000);
					} else {
						clearInterval(InterfaceController.attentionMap[i]);
						delete InterfaceController.attentionMap[i];
						tb.add(tb.parent()).css("background-color","white");
					}
				} else if (akey == "textFieldAttention") {
					if (aval == 1) {
						InterfaceController.attentionMap[i] = setInterval(function() {
							if (document.getElementById(i).style.backgroundColor == "palegreen") {
								document.getElementById(i).style.background  = "initial";
							} else {
								document.getElementById(i).style.background  = "palegreen";
							}
						}, 1000);
					} else {
						clearInterval(InterfaceController.attentionMap[i]);
						delete InterfaceController.attentionMap[i];
						document.getElementById(i).style.background  = "initial";
					}
				} else if (akey == "comboBoxAttention") {
					var tb = $('#' + i).combobox('textbox');
					if (aval == 1) {
						InterfaceController.attentionMap[i] = setInterval(function() {
							var color = tb.add(tb.parent()).css("background-color");
							if (color == "rgb(152, 251, 152)") {
								tb.add(tb.parent()).css("background-color","white");
							} else {
								tb.add(tb.parent()).css("background-color","palegreen");
							}
						}, 1000);
					} else {
						clearInterval(InterfaceController.attentionMap[i]);
						delete InterfaceController.attentionMap[i];
						tb.add(tb.parent()).css("background-color","white");
					}
				} else if (akey == "docFieldColumnAttention") {
					var dg = $('#' + aval.parent);
					var td = dg.datagrid('getPanel').find('div.datagrid-header td[field="' + i + '"]');
					if (aval.value == 1) {
						InterfaceController.attentionMap[i] = setInterval(function() {
							var color = td.css('background-color');
							if (color == "rgb(152, 251, 152)") {
								td.css('background-color','initial');
							} else {
								td.css('background-color','palegreen');
							}
						}, 1000);
					} else {
						clearInterval(InterfaceController.attentionMap[i]);
						delete InterfaceController.attentionMap[i];
						td.css('background-color','initial');
					}
				} else if (akey == "columnTitle") {
					var dg = $('#' + aval.parent);
					var span = dg.datagrid('getPanel').find('div.datagrid-header td[field="' + i + '"] div.datagrid-cell span:not(.datagrid-sort-icon)');
					span.html(aval.value);
				} else if (akey == "accordionPanelsVisible") {
					$.each(aval, function(i, c1) {
						$.each(c1, function(j, c2) {
							var visible = c2.visible;
							var spanElementId = c2.spanElementId;
							var divElementId = c2.divElementId;
							var spanElement = document.getElementById(spanElementId);
							var divElement = document.getElementById(divElementId);
							if (visible == 0) {
							    if ($('#' + divElementId).hasClass('expanded')) {
							    	$('#' + divElementId).removeClass('expanded');
							    }
								spanElement.style.visibility = "hidden";
								spanElement.style.height = "0px";
								divElement.style.visibility = "hidden";
							} else {
								spanElement.style.visibility = "visible";
								spanElement.style.height = "14px"
								divElement.style.visibility = "visible";
							}
						});
					});
				} else if (akey == "accordionPanelDynTitle") {
					var elementId = "t" + aval.index + i;
					var element = document.getElementById(elementId);
					element.textContent = aval.title;
				} else if (akey == "limitExceededMessage") {	
					var toolbarElement = document.getElementById("emt" + i);
					if (toolbarElement) {
						var html = toolbarElement.innerHTML;
						var i1 = html.indexOf('<td width="100%">');
						var i2 = html.indexOf('</td>', i1);
						var res = html.substring(i1, i2 + 5);
						if (aval.length > 0) {
							html = html.replace(res, '<td width="100%" style="color:red;padding-left:10;padding-right:10;">' + aval + '</td>');
						} else {
							html = html.replace(res, '<td width="100%"></td>');
						}
						toolbarElement.innerHTML = html;
					}
				} else if (akey == 'checked') {
					if (e_tagName == 'INPUT' && e_type == 'checkbox') {
						if(aval){
							comp.prop('checked', true);
						} else {
							comp.prop('checked', false);
						}
					}
				} else if (akey == 'value') {
					if (aval != null && $.trim(aval).length > 0) {
						_this.errorUtil.delErrorType(comp);
					}
					if (e_tagName == 'INPUT' ) {
						comp.val(aval);
						if (comp.hasClass('easyui-datebox')) {
							if (!comp.hasClass('datebox-f')) comp.datebox();
							comp.datebox('setValue', aval); 
						} else if (comp.hasClass('easyui-datetimebox')) {
							if (!comp.hasClass('datetimebox-f')) comp.datetimebox();
							comp.datetimebox('setValue', aval); 
						} else if (comp.hasClass('easyui-combobox')) {
							if (!comp.hasClass('combobox-f')) comp.combobox();
							comp.combobox('setValue', aval);
						} else if (comp.hasClass('easyui-numberbox')) {
							comp.numberbox('setValue', aval);
						}
					}
				} else if (akey == 'content') {
					if (comp.hasClass('easyui-combobox')) {
						if (!comp.hasClass('combobox-f')) comp.combobox();
						var options = [];
						var j = 0;
						$.each(aval, function(ok, ov) { // select
							// options
							var ti = i + '-title';
							options[j] = {};
							options[j][ti] = ov.o;
							options[j++][i] = ov.u;
						});
						var selectedValue = comp.combobox('getValue');
						comp.combobox('loadData', options);
						comp.combobox({
							formatter: comboTableFormat
						});
						comp.combobox('setValue', selectedValue);
					} else if(e_tagName == "SELECT") {
						var options = '';
						$.each(aval, function(ok, ov) { // select
							// options
							options += '<option value="' + ok + '">' + ov.o + '</option>';
						});
						comp.html(options);
					}
				} else if (akey == 'fFam'){
					if (e_tagName == "SELECT") {
						comp.children('option').css("font-family",aval);
					}
				} else if (akey == 'fSize'){
					if (e_tagName == "SELECT") {
						comp.children('option').css("font-size",aval);
					}
				} else if (akey == 'fStyle'){
					if (e_tagName == "SELECT") {
						if (aval == 1) comp.children('option').css("font-weight","bold");
						if (aval == 2) comp.children('option').css("font-style","italic");
						if (aval == 3) comp.children('option').css("font-style","italic").css("font-weight","bold");
					}
				} else if(akey == 'nocopy'){
					var showComp = comp;
					
					var Table = showComp.hasClass("easyui-datagrid");
					if(aval == 0) {
						if(Table){comp.prev().children(".datagrid-body").attr("oncopy","return false").attr("ondragstart","return false");}
					} else if(aval == 1){
						if(Table){comp.prev().children(".datagrid-body").removeAttr("oncopy").removeAttr("ondragstart");}
					}
				} else if (akey=='change') {
			    	_this.errorUtil.delErrorType(comp);
			    	comboValue = aval;
				} else if (akey == "img" && !comp.hasClass('staticImg')) {
					if (aval.src)
						comp.attr("src", window.contextName + "/images/foto/" + aval.src);
					else
						comp.attr("src", window.contextName + "/jsp/media/img/nofoto.png");
				} else if (akey == "e") {
					if (aval == 0) {
						if (comp.hasClass('treeField')||comp.hasClass('popup')){
							if($('#clr' + i)){
								$('#clr' + i).hide();
							}
						}
						if (e_tagName == 'INPUT' && comp.hasClass('easyui-datebox')) {
							if (!comp.hasClass('datebox-f')) comp.datebox();
							comp.datebox('disable');
							
							var fld = comp.parent().find('.textbox-text');
							var cal = comp.parent().find('.textbox-addon');
							fld.removeAttr("disabled").attr("readonly", "readonly");
							
							var hidePanel = comp.attr('hidePanel') == 'true';
							if (!hidePanel && cal.css('display') != "none") {
								cal.hide();
								fld.width(fld.width() + 18);
							}
						} else if (e_tagName == 'INPUT' && comp.hasClass('easyui-datetimebox')) {
							if (!comp.hasClass('datetimebox-f')) comp.datetimebox();
							comp.datetimebox('disable');

							var fld = comp.parent().find('.textbox-text');
							var cal = comp.parent().find('.textbox-addon');
							fld.removeAttr("disabled").attr("readonly", "readonly");
							
							var hidePanel = comp.attr('hidePanel') == 'true';
							if (!hidePanel && cal.css('display') != "none") {
								cal.hide();
								fld.width(fld.width() + 18);
							}
						} else if(e_tagName == 'INPUT' && comp.hasClass('easyui-combobox')){
							if(!comp.hasClass('combobox-f')) comp.combobox();
							comp.combobox('disable');
							comp.parent().find('.textbox-text').attr("disabled","disabled").attr("readonly", "readonly");
						} else if (e_tagName == 'INPUT' && e_type == 'file') {
							comp.attr("disabled", "disabled").attr("readonly", "readonly");
							comp.parent().addClass('btn-disabled');
						} else if (comp.hasClass('easyui-numberbox')) {
							comp.numberbox("readonly", true);
						} else if ((e_tagName == "INPUT" && e_type == 'text') || e_tagName == "TEXTAREA") {
							comp.attr("readonly", "readonly");
						} else if (comp.attr('wysiwyg') == '1' || comp.attr('wysiwyg') == 'true' || comp.attr('contentEditable') == 'true') {
							var tarea = $('textarea[id=' + i + ']');
							if (tarea.length > 0) {
								nicEditors.findEditor(tarea.get(0)).disable();
							}
						} else if (e_type == 'radio') {
							comp.find('input[type=radio]').attr("disabled", "disabled").attr("readonly", "readonly");
						} else if (e_tagName == "INPUT" || e_tagName == "BUTTON" || e_tagName == "SELECT") {
							comp.attr("disabled", "disabled").attr("readonly", "readonly");
						} else if (e_tagName == "A") {
							comp.addClass('btn-disabled');
						} else if (e_tagName == "IMG") {
							comp.unbind('contextmenu');
						}
					} else if (aval == 1) {
						if (comp.hasClass('treeField')||comp.hasClass('popup')){
							if($('#clr' + i)){
								$('#clr' + i).show();
							}
						}
						if (e_tagName == 'INPUT' && comp.hasClass('easyui-datebox')) {
							if (!comp.hasClass('datebox-f')) comp.datebox();
							comp.datebox('enable');
							
							var fld = comp.parent().find('.textbox-text');
							var cal = comp.parent().find('.textbox-addon');
							fld.removeAttr("readonly");

							var hidePanel = comp.attr('hidePanel') == 'true';
							if (!hidePanel && cal.css('display') == "none") {
								cal.show();
								fld.width(fld.width() - 18);
							}
						} else if (e_tagName == 'INPUT' && comp.hasClass('easyui-datetimebox')) {
							if (!comp.hasClass('datetimebox-f')) comp.datetimebox();
							comp.datetimebox('enable');

							var fld = comp.parent().find('.textbox-text');
							var cal = comp.parent().find('.textbox-addon');
							fld.removeAttr("readonly");

							var hidePanel = comp.attr('hidePanel') == 'true';
							if (!hidePanel && cal.css('display') == "none") {
								cal.show();
								fld.width(fld.width() - 18);
							}
						} else if(e_tagName == 'INPUT' && comp.hasClass('easyui-combobox')){
							if(!comp.hasClass('combobox-f')) comp.combobox();
							comp.combobox('enable');
							comp.parent().find('.textbox-text').removeAttr("disabled").removeAttr("readonly");
						} else if (e_tagName == 'INPUT' && e_type == 'file') {
							comp.removeAttr("disabled").removeAttr("readonly");
							comp.parent().removeClass('btn-disabled');
						} else if (comp.hasClass('easyui-numberbox')) {
							comp.numberbox("readonly", false);
						} else if ((e_tagName == "INPUT" && e_type == 'text') || e_tagName == "TEXTAREA") {
							comp.removeAttr("readonly");
						} else if (comp.attr('wysiwyg') == '1' || comp.attr('wysiwyg') == 'true' || comp.attr('contentEditable') == 'false') {
							var tarea = $('textarea[id=' + i + ']');
							if (tarea.length > 0) {
								nicEditors.findEditor(tarea.get(0)).elm.setAttribute('contentEditable', 'true');
							}
						} else if (e_type == 'radio') {
							comp.find('input[type=radio]').removeAttr("disabled").removeAttr("readonly");
						} else if (e_tagName == "INPUT" || e_tagName == "BUTTON" || e_tagName == "SELECT") {
							comp.removeAttr("disabled").removeAttr("readonly");
						} else if (e_tagName == "A") {
							comp.removeClass('btn-disabled');
						} else if (e_tagName == "IMG") {
							comp.bind('contextmenu', function(e) {
								e.preventDefault();
								$('#mm' + i).menu('show', {
									left: e.pageX,
									top: e.pageY
								});
							});
						}
					}
				} else if (akey == "ne") {
					// button enable for navi
					if (aval.length) {
						$.each(aval, function(nbi, nb) {
							var actionId = nb.actionId;
							var compb = $('#' + actionId);
							if (nb.e == 1)
								_this.enableNaviBtn(compb, true);
							else
								_this.enableNaviBtn(compb, false);
						});
					} else {
						var actionId = aval.actionId;
						var compb = $('#' + actionId);
						if (aval.e == 1)
							_this.enableNaviBtn(compb, true);
						else
							_this.enableNaviBtn(compb, false);
					}
				} else if (akey == "navi") { // toolbar for datagrid
					var toolbar = [];
					var btns='';
					$.each(aval.pr.toolBar.buttons, function(bi, btn) {
						if (btn.button && btn.menu){
						      toolbar.push({
						        iconCls:'icon-'+  btn.button.pr.actionId// ,
							 });
                           addStyle('.icon-'+  btn.button.pr.actionId+' {background: url("'+btn.button.pr.img.src+'")  no-repeat scroll center center / cover  transparent; height:26px;}');
						}else if (btn.pr && btn.pr.img && btn.pr.img.src != "separator.png") {
						  if (btn.pr.img.src.indexOf('addNavi')>0) {
						   toolbar.push({
							    text: btn.pr.textInfo,
								iconCls: btn.pr.img.bytes ? 'icon-'+btn.pr.actionId : 'icon-add',
                                id:btn.pr.actionId,
                                disabled:btn.pr.e==0,
                                handler:function(){
                                    dgBtnAction('add',i);
                                }
							});
						    if (btn.pr.img.bytes) {
								addStyle('.icon-' + btn.pr.actionId + ' {background: url("data:image/png;base64,' + btn.pr.img.bytes + '")}');
							}
							btns += '<td><a href="javascript:void(0)" class="l-btn l-btn-small l-btn-plain';
							
							if (btn.pr.e==0)
								btns += ' l-btn-disabled l-btn-plain-disabled';

							btns += '" group="" id='+btn.pr.actionId+' onclick="dgBtnAction2(\'add\',\''+i+'\', $(this))">'
								+'<span class="l-btn-left l-btn-icon-left">'
								+'<span class="l-btn-text l-btn-empty">&nbsp;</span>'
								+'<span class="l-btn-icon icon-add">&nbsp;</span></span>'
								+'</a></td>';
						  } else if (btn.pr.img.src.indexOf('delNavi')>0) {
						      toolbar.push({
							    text: btn.pr.textInfo,
						        iconCls: btn.pr.img.bytes ? 'icon-'+btn.pr.actionId : 'icon-remove',
                                id:btn.pr.actionId,
                                disabled:btn.pr.e==0,
                                handler:function(){
                                    dgBtnAction('del',i);
                                }
							});
						    if (btn.pr.img.bytes) {
								addStyle('.icon-' + btn.pr.actionId + ' {background: url("data:image/png;base64,' + btn.pr.img.bytes + '")}');
							}
							btns += '<td><a href="javascript:void(0)" class="l-btn l-btn-small l-btn-plain';
							
							if (btn.pr.e==0)
								btns += ' l-btn-disabled l-btn-plain-disabled';
								
							btns += '" group="" id='+btn.pr.actionId+' onclick="dgBtnAction2(\'del\',\''+i+'\', $(this))">'
								+'<span class="l-btn-left l-btn-icon-left">'
								+'<span class="l-btn-text l-btn-empty">&nbsp;</span>'
								+'<span class="l-btn-icon icon-remove">&nbsp;</span></span>'
								+'</a></td>';
						  } else if (btn.pr.img.src.indexOf('showDel')>-1) {
							  var un = btn.pr.img.src.indexOf('showDelUn')>-1;
						      toolbar.push({
						    	text: btn.pr.tt,
								iconCls: un ? 'icon-showDelUn' : 'icon-showDel',
                                uid:i,
                                handler:function(){
                                    dgBtnAction('showDel', i, $(this));
                                }
							});
						  }else{
						      toolbar.push({
							    text: btn.pr.textInfo,
						        iconCls:'icon-'+ btn.pr.actionId,
						        uid:i,
                                handler:function(){
                                    dgBtnAction(btn.pr.img.src,i);
                                }
							 });
							 if (btn.pr.img.bytes) {
								addStyle('.icon-' + btn.pr.actionId + ' {background: url("data:image/png;base64,' + btn.pr.img.bytes + '")}');
						     } else {
						    	addStyle('.icon-'+  btn.pr.actionId+' {background: url("'+btn.pr.img.src+'")  no-repeat scroll center center / cover  transparent;}');
						     }
						  }
						}
					});
					var toolbar_t = $("#emt" + i);
					var p_info=false;
					if(toolbar_t !=null && typeof toolbar_t.html() != 'undefined'){
						toolbar = $("#emt" + i);
						p_info=true;
					}
					if (comp.hasClass('or3-icon-table')) {
						comp.panel({
							tools: toolbar,
						});
					} else {
						comp.datagrid({
							toolbar: toolbar,
							loadFilter: function(data){
								var toolbar_rows = $(this).parents(".datagrid").find(".datagrid-toolbar:first .datagrid-count");
								$(toolbar_rows).text(data.total);
								return data;
							}
						});
						if(p_info){
							var dg_toolbar = comp.parents(".datagrid").find(".datagrid-toolbar:first");
							if (dg_toolbar.find(".datagrid-count").is("div") == false){
								if(btns!='')
									$(btns).insertBefore(dg_toolbar.find("table tr td"));
								$('<td style="width: 100%; display: flex;"><div class="datagrid-sel pagination-info" style="display: inline-block;"></div><div class="datagrid-count pagination-info" style="display: inline-block;"></div></td>').appendTo(dg_toolbar.find("table tr"));
							}
						}
					}
				} else if (akey == "tabs") { // tabs
					var firstShownIndex = -1;
					try {
						$.each(aval, function(ti, tab) {
							var tabIndex = tab.index;
							var tabVisible = tab.v;
							var tabSelect = tab.selected;
							
							if (tabVisible == 1) {
								if (firstShownIndex == -1)
									firstShownIndex = tabIndex;
								$("#" + i + " ul.tabs > li").eq(tabIndex).show();
								if (tabSelect) {
									comp.tabs('select', tabIndex);
								}
							} else
								$("#" + i + " ul.tabs > li").eq(tabIndex).hide();
						});
						var tab = comp.tabs('getSelected');
						var index = comp.tabs('getTabIndex',tab);
						if (firstShownIndex > -1 && !$("#" + i + " ul.tabs > li").eq(index).is(":visible"))
							comp.tabs('select', firstShownIndex);
							
						var totalWidth = 0;
						$.each($('#' + i + ' ul.tabs:first > li'), function(ind, li) {
							if ($(li).is(":visible")) {
								totalWidth += $(li).width();
								console.log(ind + ' = ' + $(li).width());
							}
						});
						console.log('total = ' + totalWidth);
						console.log('wrap = ' + $('#' + i + ' .tabs-header:first').width());
						if (totalWidth >= $('#' + i + ' .tabs-header:first').width()) {
							$('#' + i + ' .tabs-scroller-left:first').removeClass('or3-hide');
							$('#' + i + ' .tabs-scroller-right:first').removeClass('or3-hide');
						} else {
							$('#' + i + ' .tabs-scroller-left:first').addClass('or3-hide');
							$('#' + i + ' .tabs-scroller-right:first').addClass('or3-hide');
						}
						
					} catch (err) {
						_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.logErrorInfo("Error parsing 'tabs' property", err);
					}
				} else if (akey == "v") {
					var showComp = comp;
					
					if (showComp.length > 0) {
						if (e_tagName == 'INPUT' && (e_type == 'checkbox' || e_type == 'file'))
							showComp = comp.parent();
						else if (comp.parent().get(0) && comp.parent().get(0).tagName == 'FIELDSET' && e_type == 'radio')
							showComp = comp.parent();
						else if (comp.parent().parent().parent().get(0) && comp.parent().parent().parent().get(0).tagName == 'FIELDSET')
							showComp = comp.parent().parent().parent();

						// спрятать всю ячейку TD вместе с компонентом
						var hideTd = (
										(showComp.hasClass('treeField')
										|| showComp.hasClass('popup')
										|| showComp.hasClass('or3-btn')
										|| e_tagName == 'TEXTAREA'
										|| e_type == 'radio'
										|| showComp.hasClass('orpanel')
										|| showComp.hasClass('ordatatable')
										|| (showComp.get(0) && showComp.get(0).tagName == 'FIELDSET')
										) 
									&& (
										(showComp.parent().get(0) && showComp.parent().get(0).tagName == 'TD')
										|| (showComp.parent().parent().get(0) && showComp.parent().parent().get(0).tagName == 'TD')
										|| (showComp.hasClass('ordatatable') && showComp.parent().parent().parent().parent().get(0)
												&& showComp.parent().parent().parent().parent().get(0).tagName == 'TD')
										|| (showComp.hasClass('ordatatable') && showComp.parent().parent().parent().parent().parent().get(0)
												&& showComp.parent().parent().parent().parent().parent().get(0).tagName == 'TD')	
									   )
									) 
								|| (showComp.parent().hasClass('orpanel') && !showComp.parent().parent().hasClass('or3-popup-panel')) 
								|| (showComp.parent().hasClass('tabs-panels') && !showComp.parent().parent().hasClass('or3-popup-panel'));
						
						var hideDate = showComp.hasClass('easyui-datebox') || showComp.hasClass('easyui-datetimebox') || showComp.hasClass('easyui-combobox');
						
						if (aval == '1' || aval == 1) {
							if (hideDate) {showComp.next().show();}
							else {showComp.show();}
							if (hideTd) showComp.parents('td:first').show();
							if (hideDate) showComp.parents('td:first').show();
							_this.displayEmptyParentPanels(showComp);
							
							comp.find('.easyui-datagrid').datagrid('resize');
							
							if (comp.hasClass('easyui-tabs'))
								comp.tabs('resize');
							
							if (comp.hasClass('orpanel'))
								delete _this.hiddenPanels[i];
						} else {
							showComp.hide();
							if (showComp[0]) showComp[0].style.setProperty('display', 'none', 'important');
							if (hideTd) showComp.parents('td:first').hide();
							if (hideDate) showComp.parents('td:first').hide();
							_this.hideEmptyParentPanels(showComp);
							
							if (comp.hasClass('orpanel'))
								_this.hiddenPanels[i] = true;
						}
					}
				} else if (akey == "rv") {
					// видимость отчетов
					$.each(aval, function(rid, rv) {
						var rep = $("[reportid='" + rid + "']");
						console.log("Report visibility: " + rid + ", " + rv);
						
						if (rep.length > 0) {
							if (rep.attr('id')) {
								if (rv === 1)
									rep.show();
								else
									rep.hide();
							} else {
								if (rv === 1)
									rep.parent().parent().show();
								else
									rep.parent().parent().hide();
							}
						}
					});
				} else if (akey == "border") { // border temp fix
					
				} else if (akey == 'updateRow') {
					$.each(aval, function(ri, row) {
						_this.updateTreeTableRow(i, row.index, row.row);
					});
				} else if (akey == 'updateTblRow') {
					var t = comp;
			    	var edPars = _this.getEditing();
					$.each(aval, function(ri, row) {
						try {
							var count = t.datagrid('getRows').length;    // row count
						    var selected = t.datagrid('getSelected');
						    var selRow = 0;
						    if (selected) {
						    	selRow = t.datagrid('getRowIndex', selected);
						    }
						    
							var index = _this.pageIndex(t, row.index);
						    if (0 <= index && index < count) {
								t.datagrid('updateRow',{
									index: index,
									row: row.row
								});
								var tr = t.datagrid('getRow', index); 
								if (tr.hasClass("datagrid-row-editing"))
									tr.removeClass("datagrid-row-editing");
							}

						    if (selRow >= 0 && selRow < count) {
						        t.datagrid('selectRow', selRow);
						    }
						    if (_this.focusedTable != undefined) {
						    	var fTbl = $("#" + _this.focusedTable);
						    	if (fTbl != null && fTbl.length > 0) {
						    		var panel = fTbl.datagrid('getPanel').panel('panel');
						    		panel.attr('tabindex', 0);
						    		panel.focus();
						    	}
						    }
						} catch (err) {
							_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.logErrorInfo("Error parsing 'updateTblRow' property", err);
						}
					});
					var selRows = t.attr('selRows');
					var selCol = t.attr('selectedCol');
					if (selRows && selCol) {
						var rows = selRows.split(',');
						for (var ind=0; ind<rows.length; ind++) {
							rows[ind] = _this.pageIndex(t, rows[ind]);
							t.datagrid('selectCell', {index:rows[ind],field:selCol});
						}
					}
					if (edPars != null) {
						_this.restoreEditing(edPars);
					}
					_tooltip_js__WEBPACK_IMPORTED_MODULE_3__.TooltipOps.addDatagridTooltip(t);
				} else if (akey == 'reloadRow') {
					reloadRows = aval;
				} else if (akey == 'setNodeTitle') {
					$.each(aval, function(ri, row) {
						if (comp.hasClass('easyui-tree')) {
							var node = comp.tree('find', row.index);
							if (node != null) {
								comp.tree('update', {
									target: node.target,
									text: row.title
								});
							}
						}
					});
				} else if (akey == 'reloadNode') {
					$.each(aval, function(ri, row) {
						if (comp.hasClass('easyui-tree')) {
							var node = comp.tree('find', row.index);
							if (node != null) {
								if (comp.tree('isLeaf', node.target)) {
									var parent = comp.tree('getParent', node.target);
									comp.tree('reload', parent.target);
								} else 
									comp.tree('reload', node.target);
							
							}
						}
					});
				} else if (akey == 'selectNode') {
					if (aval != 0) {
						_this.nodeParent = false;
						_this.treeCallLater('select', comp, aval, 20);
					} else {
					    var nodes = comp.tree('getChecked');
					    _this.isOnCheck = false;
					    for (var k = 0; k<nodes.length; k++) {
					    	$(this).tree('uncheck', nodes[k].target);
					    }
					    _this.isOnCheck = true;
					}
				} else if (akey == 'checkboxTree') {
					if (comp.hasClass('easyui-tree')) {
						if (aval == 0) {
							comp.tree({
								checkbox: function(node){
										return true;
								}
							});
						} else {
							comp.tree({
								checkbox: function(node){
									if (node.id == aval) {
										return false;
									} else {
										return true;
									}
								}
							});
						}
					}
				} else if (akey == 'deleteRow') {
					$.each(aval, function(ri, row) {
						comp.treegrid('remove', row.index);
					});
				} else if (akey == 'cv') {
					$.each(aval, function(ri, row) {
						if (row.v == 1 || row.v == '1')
							comp.treegrid('showColumn', row.uuid);
						else
							comp.treegrid('hideColumn', row.uuid);
					});
				} else if (akey == 'reload') {
					comp.attr('reload', '1');
					if (comp.hasClass('or3-icon-table')) {
						comp.panel('refresh');
					} else if (comp.hasClass('or3-img-panel') || comp.hasClass('or3-img-panel-vertical')) {
						createImagePanel(comp);
					} else {
						comp.datagrid('reload');
					}
				} else if (akey == 'imageAdded') {
					if (comp.hasClass('or3-img-panel') || comp.hasClass('or3-img-panel-vertical')) {
						imageAdded(comp, aval);
					}
				} else if (akey == 'imageDeleted') {
					if (comp.hasClass('or3-img-panel') || comp.hasClass('or3-img-panel-vertical')) {
						imageDeleted(comp, aval);
					}
				} else if (akey == 'reloadUpload') {
					var pan = $('#uploaded' + i);
					if (pan.length > 0)
						pan.panel('refresh');
				} else if (akey == 'selectedRows') {
					if (comp.hasClass('easyui-datagrid')) {
						_this.selectTableRows(comp, aval);
					} else if (comp.hasClass('easyui-treegrid')){
						comp.attr('selRows', aval);
						_this.treeTableSetSelected(comp);
					}
				} else if (akey == 'selectedImg') {
					if (comp.hasClass('or3-img-panel') || comp.hasClass('or3-img-panel-vertical')) {
						selectImagePanel(comp, aval);
					}
				} else if (akey == 'imgTitleChanged') {
					if (comp.hasClass('or3-img-panel') || comp.hasClass('or3-img-panel-vertical')) {
						changeImageTitle(comp, aval.index, aval.title);
					}
				} else if (akey == 'imgFileChanged') {
					if (comp.hasClass('or3-img-panel') || comp.hasClass('or3-img-panel-vertical')) {
						changeImageFile(comp, aval.index, aval.file);
					}
				} else if (akey == 'reloadTreeTable') {
					comp.treegrid('reload');
				}  else if (akey == 'bg') {
					//comp.css({backgroundColor: aval});
				} else if (akey == 'fg') {
					comp.css({color: aval});
				} else if (akey == 'dialogs') {
					if (comp.hasClass('easyui-datagrid')) {
						$.each(aval, function(ok, ov) {
							if (ov.header){
								var td = comp.datagrid('getPanel').find('div.datagrid-header td[field="'+ov.header.uid+'"]');
								var hTitle = ov.header.title;
								if (hTitle)
									hTitle = hTitle.replace(/@/g,'<br/>');
								td.children().first().children().first().html(hTitle);
							}
						});
					}
				} else if (akey == 'zebra') {
					if (comp.hasClass('easyui-datagrid')) {
						$.each(aval, function(ok, ov){
							comp.datagrid({
								rowStyler: function(index,row) {
									if (index % 2 == 0){
										return 'background-color:'+ov.zebra1+';';
									} else {
										return 'background-color:'+ov.zebra2+';';
									}
								}
							});
						});
					}
				} else if (akey == 'bitSeparation') {
					$('#' + i).numberbox({
					    formatter:function(value) {
					    	if (value != null && value.length > 0) {
						    	var index = value.indexOf('.');
						    	var c = 0;
						    	if (index != -1) {
						    		var sub = value.substring(index + 1);
						    		var c = sub.length;
						    	} 
						    	var n = parseFloat(value);
						    	
						    	if (!isNaN(n))
						    		return n.toLocaleString('ru', { minimumFractionDigits: c });
					    	}
					    	return "";
					    }
					});
				} else if (akey == 'expanded') {
					$('#cnt' + i).addClass('expanded');
				} else {
					comp.attr(akey, aval);
				}
			});
			if (reloadRows != undefined) {
				$.each(reloadRows, function(ri, row) {
					var node = comp.treegrid('find', row.index);
					
					if (node != null) {
						if (node.children != null && node.children.length > 0) {
							comp.treegrid('reload', row.index);
						} else if (node.parent > 0) {
							comp.treegrid('update',{
								id: row.index,
								row: {
									children: [{
										parent: row.index,
									    id: -1000,
						                name: "untitled",
						            }]
								}
							});
							comp.treegrid('reload', row.index);
						} else {
							comp.treegrid('reload');
						}
					}
				});
			}
			if (comboValue != undefined) {
				if (comboValue > -1 ) {
                    $('#' + i + ' option:eq(' + comboValue + ')').prop('selected', true);
			    } else {
                    $('#' + i + ' option:eq(0)').prop('selected', true);
			    }
			}
		} else if (key == 'alert') {
			alert(value);
		} else if (key == 'autoRefresh') {
			if (comp.hasClass('easyui-datagrid')) {
				ifcRefreshers[ifcRefreshers.length] = setInterval(function() {
					comp.datagrid('reload');
				}, value);
			} else {
				ifcRefreshers[ifcRefreshers.length] = setInterval(function() {
					_this.setValue(i, 1);
				}, value);
			}
		} else if (key == 'styleRow') {
			var styleByRow = InterfaceController.styles[i];
			if (styleByRow == null) {
				styleByRow = {};
				InterfaceController.styles[i] = styleByRow;
			}
			var view = $('[field=' + i + ']').first().parents('.datagrid-view');
			var table = view.find('.easyui-datagrid');
			if (table.length == 0)
				table = view.find('.easyui-treegrid');
			
			if (table.length > 0) {
				var delta = _this.pageIndex(table, 0);
				var tds = view.find('[datagrid-row-index] [field=' + i + ']');

				$.each(value, function(k, row) {
					var pIndex = row.index + delta;
					var styleName = "st" + row.hsh;
					styleByRow[pIndex] = {'class': styleName};
					
					var style = InterfaceController.cssStyles[styleName];
					if (style == null) {
						style = '';
						var divStyle = '';
						if(row.ff){
							divStyle += "font-family:" + row.ff + ";";
						}
						if(row.fs){
							divStyle += "font-size:" + row.fs + "px;";
						}
						if(row.fw){
							divStyle += "font-weight:" + row.fw + ";";
						}
						if(row.fst){
							divStyle += "font-style:" + row.fst + ";";
						}
						if(row.frg){
							divStyle += "color:" + row.frg + ";";
						}
						if(row.bcg){
							style += "background-color:" + row.bcg + ";";
						}
						InterfaceController.cssStyles[styleName] = style;
						if (style.length > 0)
							$('style').first().append('\n.' + styleName +' {' + style + '}');
						if (divStyle.length > 0)
							$('style').first().append('\n.' + styleName +' div {' + divStyle + '}');
					}
					try {
						var td = $(tds.get(pIndex));
						td.removeClass();
						td.addClass(styleName);
					} catch (err) {
					}
				});
			}
		} else if (key == 'options') {
			if ( comp.attr('type')=='radio' && value.length!=0){
				// очистить таблицу
				comp.empty();
				var colCount = comp.attr("count");
				if (colCount > 0) {
					var rowCount = Math.ceil(value.length/colCount);
					// создать необходимое количество ячеек
					var items = {};
					var ij=0;
					// value массив кнопок
					
					var d,checked,color,font,fontSize,fontStyle;
					$.each(value, function(akey, aval) {
						d = aval.pr.v == '1' ? "display:inline;" : "display:none;";
						checked = aval.pr.checked == true ? "checked='checked'" : "";
						color = aval.st.color == null ? "" : "color:"+aval.st.color+";";
						font = aval.st.font == "Dialog" ? "font-family:Arial;" : "font-family:"+aval.st.font+";";
						fontSize = aval.st.fontSize > 0 ? "font-size:"+aval.st.fontSize+"px;" : "";
						if (aval.st.fontStyle == 1)
							fontStyle = "font-weight:bold;";
						else if (aval.st.fontStyle == 2)
							fontStyle = "font-style:italic;";
						else if (aval.st.fontStyle == 3)
							fontStyle = "font-style:italic; font-weight:bold;";
						else fontStyle = "";
						items[ij] = "<label><input type='radio' value='" + aval.pr.value + "' name='"+aval.pr.name+"' id='"+i+"' "+checked+" style='"+d+"'/> "
						+"<span style='"+d+color+font+fontSize+fontStyle+"'>"+aval.pr.text+"</span></label>";
						ij++;
					});
					var j=0;
					var str= '';
					for (var r = 0; r < rowCount; r++) {
						str+="<tr>";
						for (var c = 0; c < colCount; c++) {
							if(j<ij){
								str+="<td>"+items[j]+"</td>";
								j++;
							}
						}
						str+="</tr>";
					}
					comp.html(str);
				}
			}
		}
	}
	
	enableNaviBtn(btn, enable) {
		if (enable && btn.hasClass('l-btn-disabled')) {
			if (btn.attr('onclick') != null)
				btn.removeClass('l-btn-disabled').removeClass('l-btn-plain-disabled');
			else
				btn.linkbutton('enable');
		} else if (!enable && !btn.hasClass('l-btn-disabled')) {
			if (btn.attr('onclick') != null)
				btn.addClass('l-btn-disabled').addClass('l-btn-plain-disabled');
			else
				btn.linkbutton('disable');
		}
	}

	focusNext(elem) {
		let _this = this;
		
		var curElem = elem;
		var nextElem = elem.next('input:visible:enabled, select:visible:enabled, textarea:visible:enabled, button:visible:enabled, .easyui-datagrid');
		while (nextElem.length < 1 && !curElem.is('body')) {
			nextElem = curElem.nextAll().find('input, select, textarea, button').filter(':visible:enabled:first');
			curElem = curElem.parent();
		}
		if (nextElem.length < 1) {
			var elems = $('body').find('input:visible:enabled, select:visible:enabled, textarea:visible:enabled, button:visible:enabled, .easyui-datagrid');
			if (elems.length > 0)
				nextElem = $(elems[0]);
		}
		_this.focusedTable = undefined;
		if (nextElem != elem) {
			if (nextElem.hasClass('easyui-datagrid')) {
				var panel = nextElem.datagrid('getPanel').panel('panel');
				panel.attr('tabindex', 0);
				panel.focus();
	
				_this.focusedTable = nextElem.attr('id');
				panel.unbind('keydown').bind('keydown',function(event) {
					if (event.which && event.which == 38) {
				        return selectRow(nextElem, true);
					} else if (event.which && event.which == 40) {
				        return selectRow(nextElem, false);
					} else if (event.which && event.which == 37) {
				        return selectColumn(nextElem, true);
					} else if (event.which && event.which == 39) {
				        return selectColumn(nextElem, false);
					} else if (event.which && event.which == 32) {
				        return beginEditCell(nextElem);
					}
				});
			} else
				nextElem.focus();
		} else
			elem.blur();
	}

	setElemForTopPane() {
		let _this = this;

		let elements = $("[onTop]");
		// отсортировать новые данные
		elements.sort(function (a, b){
			var i = $(a).attr('onTop');
			var j = $(b).attr('onTop'); 
			return i < j ? -1 : i > j ? 1 : 0;
		});
		
		$.each(elements, function(i, element) {
			_this.hideEmptyParentPanels($(element));
		});
		
		$(".ui-toolbar").append(elements);
	}
	
	movePopUpContent(dialogId) {
		let panel = dialogId !== undefined ? $('#' + dialogId) : $('#ui-body');
		let elements = dialogId !== undefined ? $('#' + dialogId +' .popUpPanContent') : $('.popUpPanContent');
		panel.append(elements);
	}
	
	openPopup(uid, row, colUid) {
		let _this = this;
		
		_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.blockPage();
		var par = {"uid": uid, "cmd": "openPopup"};
		if (row != undefined) par["row"] = row;
		if (colUid != undefined) par["cuid"] = colUid;
		
		_this.app.query(par).then(response => {
			response.json().then(json => {
				_util_js__WEBPACK_IMPORTED_MODULE_0__.DataChecker.checkData(json).then(data => {
					_this.data = data;
					if (data.result == 'nop') {
						// no operation
						$('body').unblock();
					} else if (data.result == 'error') {
						_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.alert(data.message, _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.ERROR);
						$('body').unblock();
					} else {
						_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.blockPage();
						_this.selectedRow = [];
						var did = _this.popupcount;
						var dialogId = 'or3_popup' + did;
						
						$('#trash').append($("<div></div>").attr('id', dialogId));
						
						_this.dialogResult[dialogId] = '1';
						$('#' + dialogId).on('click', function (e) {
							// источник события
							var target = e && e.target || event.srcElement;
							return _this.hidePopUp(target, '#' + dialogId);
						});
						
						var w = data.w > $(window).width() + 50 ? $(window).width() + 50 : data.w;
						var h = data.h > ($(window).height() - 20) ? ($(window).height() - 20) : data.h;
			
						var buttons = (data.tv==0) ? [{
							id: "okPopupBtn",
							text: _translation_js__WEBPACK_IMPORTED_MODULE_4__.Translation.translation.ok,
							handler: function() {
								_this.setDialogBtnsEnabled(dialogId, false);
								_this.sendUserDecision("0").then(data => {
									_this.closePopup(dialogId);
								});
							}
						},
						{
							id: "cancelPopupBtn",
							text: _translation_js__WEBPACK_IMPORTED_MODULE_4__.Translation.translation.cancel,
							handler: function() {
								_this.setDialogBtnsEnabled(dialogId, false);
								_this.sendUserDecision("1").then(data => {
									_this.dialogResult[dialogId] = '1';
									$("#" + dialogId).dialog('destroy');
								});
							}
						}] : [];
						
						var toolbar = [{
							id: 'reportBtn' + did,
							text: _translation_js__WEBPACK_IMPORTED_MODULE_4__.Translation.translation.print,
							iconCls: 'icon-rept'
						}];
						
						var dialogOptions = {
							title: data.t,
							width: w,
							height: h,
							closed: false,
							cache: false,
							href: _this.app.restUrl + '&cmd=loadPopup',
							modal: true,
							uid: uid,
							row: row,
							cuid: colUid,
							onOpen: function() {
								$('#' + dialogId +' .panel').append($("<div class=\"glassDialog\"></div>"));
								$('#' + dialogId + ' .glassDialog').height($('#' + dialogId +' .panel').height()).width($('#' + dialogId +' .panel').width());
							},
							onLoad: function() {
								// Добавляем ID-окна в массив окон
								InterfaceController.openedDialogs.push(dialogId);
								InterfaceController.popDlg.push(dialogId);
								InterfaceController.popDlgType.push(_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.DLG_POPUP_IFC);
								_this.loadWindowTitle($('#' + dialogId));
								_this.loadDialogReports(did);
								
								$('.easyui-datebox, .easyui-datetimebox').each(function(i) {
									var db = $(this);
									var hidePanel = db.attr('hidePanel') == 'true';
									if (hidePanel) {
										let opts = $(this).datebox("panel").panel("options");
										opts.nopanel = true;
										
										var fld = $(this).parent().find('.textbox-text');
										var cal = $(this).parent().find('.textbox-addon');
										
										if (cal.css('display') != "none") {
											cal.hide();
											fld.width(fld.width() + 18);
										}
									}
								});
								
								$('.easyui-treegrid.datagrid-f').each(function(i) {
									console.log('workworkwork2');
									let treegridComp = $(this);
									let id = treegridComp.attr('id');
									let multi = _this.multiSelection[id];
									if (_this.multiSelection[id] !== undefined) {
										treegridComp.treegrid({
											checkbox: multi,
											onlyLeafCheck: true,
											singleSelect: !multi
										});
										_this.multiSelection[id] = undefined;
									}
								});
			
								_this.loadData({}, true);
								_this.loadData({}, true).then(() => {
									$('#' + dialogId).panel('resize');
									_this.app.resize('#' + dialogId);
								});
								
								_this.preparefileUpload();
								_this.clickPopUpContent(dialogId);
								_this.showPopUpContent();
								_this.movePopUpContent(dialogId);
							},
							onClose: function() {
								_this.dialogResult[dialogId] = '1';
								$("#" + dialogId).dialog('destroy');
							},
							onBeforeDestroy : function() {
								// Удаляем ID-окна из массива окон
								InterfaceController.openedDialogs.pop();
								if(dialogId == InterfaceController.popDlg[InterfaceController.popDlg.length-1]){
					        		InterfaceController.popDlg.pop();
						        	InterfaceController.popDlgType.pop();
					        	}
								
								if (_this.dialogResult[dialogId] == '1') {
									let opts = $('#' + dialogId).dialog('options');
									let par = {"cmd":"closePopup", "uid":opts.uid, "val":'1'};
						        	if (opts.row != undefined) par["row"] = opts.row;
						        	if (opts.cuid != undefined) par["cuid"] = opts.cuid;
			    		        	_this.loadData(par, true);
								}
							},
							buttons: buttons,
							resizable: true
						};
						dialogOptions.toolbar = toolbar;
						
						$('#' + dialogId).dialog(dialogOptions);
			
						_this.popupcount++;
					}
				});
			});
		});
	}
	
	hidePopUp(target, panelId) {
		let _this = this;
		// источник события
		target = $(target).closest('[id]');
		
		var isPopupOrButton = target != null && (target.hasClass('popup') || target.hasClass('trBtn'));
		var isOnPopupPanel = $(target).closest('.popUpPanContent').length > 0;
		
		if (!isOnPopupPanel || isPopupOrButton) {
			var panID = target != null ? ("pop" + $(target).attr('id')) : "";
			var panels = $(panelId).find(".popUpPanContent");
			for (var j = 0; j < panels.length; j++) {
				if($(panels[j]).attr('id') != panID && $(panels[j]).is(":visible")){
					// отправить на сервер событие закрытия панели Before
					var par = {};
					par["uid"] = $(panels[j]).attr('id').substring(3); // первые три символа это префикс "pop"
					par["cmd"] = "hidePopUp";
					par["t"] = "b";
					
					_this.postAndParseData(par);
					
					$(panels[j]).fadeOut();
					// отправить на сервер событие закрытия панели After
					par["t"] = "a";
					
					_this.postAndParseData(par);
				}
			}
		}
	    return true;
	}
	
	setDialogBtnsEnabled(did, enable) {
		if (enable)
			$("#" + did).parent().find(".dialog-button .l-btn").linkbutton('enable');
		else
			$("#" + did).parent().find(".dialog-button .l-btn").linkbutton('disable');
	}
	
	sendUserDecision(decision) {
		var par = {"cmd":'userDecision', "value":decision, "json":1};
		return this.postAndParseData(par);
	}
	
	destroyPopup(count) {
		let _this = this;
		var popups = $("div[id^='or3_popup']");
		for (var i=1; i<=count; i++) {
			var popup = $(popups.get(popups.size() - i));
			_this.dialogResult[popup.attr('id')] = '-1';
			popup.dialog('destroy');
		}
	}
	
	closePopup(did) {
		let _this = this;
	    _this.showChangeMsg();
	
		_this.dialogResult[did] = '0';
		var opts = $('#' + did).dialog('options');
	    
	    var par = {};
		par["uid"] = opts.uid ;
		par["val"] = 'CHECK';
		if (opts.row != undefined) par["row"] = opts.row;
		if (opts.cuid != undefined) par["cuid"] = opts.cuid;
		par["cmd"] = "closePopup";
	
		_this.loadData(par, true).then(data => {
			if (!data.result || data.result == 'success') {
				$("#" + did).dialog('destroy');
			} else {
				_this.showPopupErrors(data.errors, data.path, data.name, opts, $('#' + did), data.fatal, data.isDataIntegrityControl);
				_this.setDialogBtnsEnabled(did, true);
			}
		});
	}

	loadReports() {
		let _this = this;
		var par = {"getReports":"1"};
		
		_this.app.post(par).then(data => {
			if (data.children && data.children.length > 0) {
				$('#reports').remove();
				$('#reportBtn').removeAttr("reportid").unbind('click');
				$('#reportBtn').removeClass("m-btn").removeClass("m-btn-small").removeClass("l-btn").removeClass("l-btn-small");
				var text = $('#reportBtn').find('.l-btn-text').text();
				if (text != null && text.length > 0) {
					$('#reportBtn').empty();
					$('#reportBtn').text(text);
				}
				if (data.children.length > 1 || data.children[0].children != null) {
					var html = "<div id='reports' class='subReports'>";
					$.each(data.children, function(i, c) {
						var cStyle = (c.v == 0) ? " style='display:none;'" : "";
	
						if (c.children && c.children.length > 0) {
							html += "<div" + cStyle + "><span reportid='"+c.id+"'>" + c.name + "</span><div>";
							$.each(c.children, function(i1, c1) {
								var c1Style = (c1.v == 0) ? " style='display:none;'" : "";
								if (c1.children && c1.children.length > 0) {
									html += "<div" + c1Style + "><span reportid='"+c1.id+"'>" + c1.name + "</span><div>";
									$.each(c1.children, function(i2, c2) {
										var c2Style = (c2.v == 0) ? " style='display:none;'" : "";
										html += "<div" + c2Style + "><a reportid='"+c2.id+"'>"+c2.name+"</a></div>";
									});
									html += "</div></div>";
								} else {
									html += "<div" + c1Style + "><a reportid='"+c1.id+"'>"+c1.name+"</a></div>";
								}
							});
							html += "</div></div>";
						} else {
							html += "<div" + cStyle + "><a reportid='"+c.id+"'>"+c.name+"</a></div>";
						}
					});
					html+="</div>";
					$('#trash').append(html); 
					$('#reportBtn').show();
					$('#reportBtn').menubutton({
							menu: '#reports'
					});
				} else {
					$('#reportBtn').attr("reportid", data.children[0].id);
					$('#reportBtn').linkbutton();
					$('#reportBtn').bind('click', function() {});
					if (data.children[0].v == 0) {
						$('#reportBtn').hide();
					} else {
						$('#reportBtn').show();
					}
				}
			} else {
				$('#reportBtn').hide();
			}
		});
	}

	loadWindowTitle(dlg) {
		let _this = this;
		var par = {"getTitle":"1"};
		this.postAndParseData(par).then(data => {
			var titlePan = dlg.parent().find('.window-header .panel-title');
			$(titlePan).text(data.title).css('font-size','14px');
			_this.loadStack(titlePan);
		});
	}
		
	loadDialogReports(did) {
		let _this = this;
		var par = {"getReports":"1"};
		this.postAndParseData(par).then(data => {
			if (data.children && data.children.length>0) {
				$('#reports' + did).remove();
	
				if (data.children.length > 1 || data.children[0].children != null) {
					var html = "<div id='reports" + did + "' class='subReports'>";
					$.each(data.children, function(i, c) {
						var cStyle = (c.v == 0) ? " style='display:none;'" : "";
	
						if (c.children && c.children.length > 0) {
							html += "<div" + cStyle + "><span reportid='"+c.id+"'>" + c.name + "</span><div>";
							$.each(c.children, function(i1, c1) {
								var c1Style = (c1.v == 0) ? " style='display:none;'" : "";
								if (c1.children && c1.children.length > 0) {
									html += "<div" + c1Style + "><span reportid='"+c1.id+"'>" + c1.name + "</span><div>";
									$.each(c1.children, function(i2, c2) {
										var c2Style = (c2.v == 0) ? " style='display:none;'" : "";
										html += "<div" + c2Style + "><a reportid='"+c2.id+"'>"+c2.name+"</a></div>";
									});
									html += "</div></div>";
								} else {
									html += "<div" + c1Style + "><a reportid='"+c1.id+"'>"+c1.name+"</a></div>";
								}
							});
							html += "</div></div>";
						} else {
							html += "<div" + cStyle + "><a reportid='"+c.id+"'>"+c.name+"</a></div>";
						}
					});
					html+="</div>";
					$('#trash').append(html); 
					$('#reportBtn' + did).menubutton({
							menu: '#reports' + did
					});
					
					var text = $('#reportBtn' + did).find('.l-btn-left').find('.l-btn-left');
					if (text != null && text.length > 0) {
						$('#reportBtn' + did).empty();
						$('#reportBtn' + did).append(text);
					}
				} else {
					$('#reportBtn' + did).attr("reportid", data.children[0].id);
					$('#reportBtn' + did).find('.l-btn-text').text(data.children[0].name);
					if (data.children[0].v == 0) {
						$('#reportBtn' + did).hide();
						var shadow = $('#reportBtn' + did).parents('.panel').first().next();
						shadow.height(shadow.height() - 26);
					}
				}
			} else {
				$('#reportBtn' + did).hide();
				var shadow = $('#reportBtn' + did).parents('.panel').first().next();
				shadow.height(shadow.height() - 26);
			}
		});
	}
	
	loadStack(container) {
		let _this = this;
		var par = {"cmd":"getStack"};
		
		this.postAndParseData(par).then(data => {
			_this.showStack(data, container);
		});
	}
	
	showStack(stack, container) {
		if (container != null) {
			var dBody = $(container).parent().parent().find('.window-body');
			dBody.height(dBody.height());
		}
	}
	
	preparefileUpload(cont) {
		let _this = this;
		if (cont == null) cont = $('body');
		var uploads = cont.find('.or3-file-upload');
		for (var i=0; i<uploads.length; i++) {
			var upload = $(uploads.get(i));
			upload.fileupload({
				autoUpload: true,
				dropZone: upload.parent(),
				pasteZone: upload.parent(),
				sequentialUploads: true,
			    url: _this.app.restUrl + '&uid=' + upload.attr('id'),
		        dataType: 'json',
		        done: function (e, data) {
		        	if (data.result.result == 'success') {
			        	_this.endEditing();
		                _this.showChangeMsg();
			            _this.loadData({}, true);
			            
		        	} else {
		        		_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.alert(data.result.message, _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.ERROR);
		        		$('body').unblock();
		        	}	
		        },
		        add: function (e, data) {
		        	_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.blockPage();
		            data.submit();
		        },
		    });
		}
	}
	
	clickPopUpContent(parentId) {
		let _this = this;
		$('.popUpPan:not(.asMenu)').click(function(e) {
		    var popID = $(this).attr('rel'); 
		    var content = $(popID);
			var ppopID = $("#"+parentId).find(popID);
		    //Проверяем, пришло событие от компанента на котором оно вызвано или нет
			if(ppopID.length>0 || $(this).attr('sub')){
				//Если второй щелчек по кнопке, панель которой видима, то необходимо ее скрыть
			    if($(content).is(":visible")){
					// отправить на сервер событие закрытия панели Before
					var par = {};
					par["uid"] = $(content).attr('id').substring(3); // первые три символа это префикс "pop"
					par["cmd"] = "hidePopUp";
					par["t"] = "b";
					_this.postAndParseData(par);
					
					$(content).fadeOut();
					// отправить на сервер событие закрытия панели After
					par["t"] = "a";
					_this.postAndParseData(par);
			    }else{
				    // отправить на сервер событие открытия панели Before
					var par = {"cmd":"showPopUp","uid":$(this).attr('id'),"t":"b"};
					_this.postAndParseData(par);
			
					var left = $(this).attr('sub') ? $(this).offset().left + $(this).width() : $(this).offset().left;
					var top = $(this).attr('sub') ? $(this).offset().top : $(this).offset().top + $(this).height() + 2;

			    	//var left = $(this).attr('sub') ? $(this).width() : $(this).offset().left;
			    	//var top = $(this).attr('sub') ? -$(this).height() : $(this).offset().top + $(this).height() + 2;
			    	
			    	$(content).css('left', left);
			    	$(content).css('top', top);
					// скрыть предыдущие панели
					var parent = $(this).parent();
					while (!$(parent).hasClass('popUpPanContent') && $(parent).attr('id') != parentId && !$(parent).hasClass('ui-toolbar')) {
						parent = $(parent).parent();
					}
					var panels = $(parent).find(".popUpPanContent");
					for (var j = 0; j < panels.length; j++) {
						if($(panels[j]).attr('id') != popID && $(panels[j]).is(":visible")){
							// отправить на сервер событие закрытия панели Before
							var par = {};
							par["uid"] = $(panels[j]).attr('id').substring(3); // первые три символа это префикс "pop"
							par["cmd"] = "hidePopUp";
							par["t"] = "b";
							_this.postAndParseData(par);
							
							$(panels[j]).fadeOut();
							// отправить на сервер событие закрытия панели After
							par["t"] = "a";
							_this.postAndParseData(par);
						}
					} 
				    // показать панель
				    $(content).fadeIn();
				    // отправить на сервер событие открытия панели After
					var par = {"cmd":"showPopUp","uid":$(this).attr('id'),"t":"a"};
					_this.postAndParseData(par);
			    }
			}
		    // если панель является подпанелью, то запретить всплытие события
	    	return $(this).attr('sub') ? false : true;
		});
	}
	
	showPopUpContent(){
		let _this = this;
		$('.popUpPan.asMenu:not(.hover)').hover(
			function(e) {
				_this.showPopupPanel($(this));
			},
			function(e) {
				_this.hidePopupPanel($(this));
			}
		);
	}
	
	showPopupPanel(popup) {
		var popID = popup.attr('rel'); 
		var content = $(popID);
		if(!popup.hasClass('hover')){
			popup.addClass('hover');
		}
		//удалить класс если он есть(косяки при открытии)
		if(popup.hasClass('btn-disabled')){
			popup.removeClass('btn-disabled');
		}
		//добавить панель к обработчику события
		popup.append($(content));
		// отправить на сервер событие открытия панели Before
		var par = {"cmd":"showPopUp","uid":popup.attr('id'),"t":"b"};
		this.postAndParseData(par);
	
		var left = popup.attr('sub') ? popup.offset().left + popup.width() : popup.offset().left;
		var top = popup.attr('sub') ? popup.offset().top : popup.offset().top + popup.height() + 2;
		
		$(content).css('left', left);
		$(content).css('top', top);
		// показать панель
		$(content).fadeIn(500);
		// отправить на сервер событие открытия панели After
		var par = {"cmd":"showPopUp","uid":popup.attr('id'),"t":"a"};
		this.postAndParseData(par);
	}
	
	hidePopupPanel(popup) {
		var popID = popup.attr('rel'); 
		var content = $(popID);
	    // отправить на сервер событие скрыть панель Before
		var par = {"cmd":"hidePopUp","uid":popup.attr('id'),"t":"b"};
		this.postAndParseData(par);
	    // отправить на сервер событие скрыть панель After
		var par = {"cmd":"hidePopUp","uid":popup.attr('id'),"t":"a"};
		this.postAndParseData(par);
	    // скрыть панель
		$(content).fadeOut(100);
	}

	forseSaveChanges(initiator) {
		this.endEditing();
		var par = {"cmd":"fcommit"};
		return this.postAndParseData(par);
	}
	
	showErrors(errors, filePath, fileName, command, btn1show, btn1text, popup) {
		let _this = this;
		
		_this.errorUtil.clearAllErrorType();
	
		var content = this.errorUtil.higlightErrors(errors);
		
		var dialogId = 'or3_popup' + _this.popupcount;
		$('#trash').append($("<div></div>").attr('id', dialogId));
		_this.dialogResult[dialogId] = '1';
	
		var buttons = [];
	
		if (btn1show == true) {
			var button1 = {
				text: btn1text,
				handler: function() {
					_this.dialogResult[dialogId] = '0';
					$("#" + dialogId).dialog('destroy');
					
					if (popup)
						popup.dialog('destroy');
					
					if (popup == null && command == "nextStep")
						_this.nav.toActiveMain();
				}
			};
			buttons.push(button1);
		}	
		var button2 = {
			text: _translation_js__WEBPACK_IMPORTED_MODULE_4__.Translation.translation["continue"],
			handler: function() {
				_this.dialogResult[dialogId] = '1';
				$("#" + dialogId).dialog('destroy');
			}
		};
		buttons.push(button2);
	
		$('#' + dialogId).dialog({
			title: '<div style="font-size:14px; color: #777;">'+_translation_js__WEBPACK_IMPORTED_MODULE_4__.Translation.translation.errors+'</div>',
			width: 900,
			height: 550,
			resizable: true,
			style: {'background': 'rgba(0, 0, 0, 0) linear-gradient(to bottom, #eff5ff 0px, #e0ecff 20%) repeat-x scroll 0 0', 'border-color':'#95b8e7'},
			closed: false,
			cache: false,
			modal: true,
			content:content,
			tools: [{
					iconCls:'icon-rept',
					handler:function(){
						if (filePath != null) {
							var url = _this.app.restUrl + "&rnd=" + _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.rnd() + "&cmd=opf&fn=" + encodeURIComponent(filePath) + "&fr=" + encodeURIComponent(fileName);
							$('#report_frame').attr('src', url);
						}
					}
			}],
			onOpen: function() {
				InterfaceController.popDlg.push(dialogId);
				InterfaceController.popDlgType.push(_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.DLG_ERRORS);
				$("li[uuid]").on('click', function (e) {
					var uuid = $(this).attr('uuid');
					var rownum = -1;
					var row = $(this).attr('row');
					if (row != null && row.length > 0)
						rownum = parseInt(row);
						
					_this.focusComponent(uuid, rownum);
					
					_this.dialogResult[dialogId] = '1';
					$("#" + dialogId).dialog('destroy');
	
				});
			},
			onClose: function() {
				_this.dialogResult[dialogId] = '1';
				$("#" + dialogId).dialog('destroy');
			},
			onBeforeDestroy : function() {
				let opts = $(this).dialog('options');
	
	        	if (_this.dialogResult[dialogId] == '0') {
	    			var par = {};
	    			par["cmd"] = command;
	    			par["result"] = "save";
	    			_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.blockPage(_translation_js__WEBPACK_IMPORTED_MODULE_4__.Translation.translation.saving);
	    			
					_this.app.post(par).then(data => {
						if (data.result == "success") {
							_this.hideChangeMsg();
							_this.loadData({});
						} else {
							_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.alert(_translation_js__WEBPACK_IMPORTED_MODULE_4__.Translation.translation.error, _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.ERROR);
						}
	    				$('body').unblock();
					}, () => {
						$('body').unblock();
					});
	        	}
	        	if(dialogId == InterfaceController.popDlg[InterfaceController.popDlg.length-1]){
	        		InterfaceController.popDlg.pop();
		        	InterfaceController.popDlgType.pop();
	        	}
			},
			buttons: buttons
		});
		
		_this.popupcount++;
	}

	showForceErrors(errors, btn1show, filePath, fileName, btn1text, btn2text) {
		let _this = this;
		
		_this.errorUtil.clearAllErrorType();
	
		var content = this.errorUtil.higlightErrors(errors);
	
		var dialogId = 'or3_popup' + _this.popupcount;
		$('#trash').append($("<div></div>").attr('id', dialogId));
		_this.dialogResult[dialogId] = '1';
	
		var buttons = [];
	
		if (btn1show == true) {
			var button1 = {
				text: btn1text,
				handler: function() {
					_this.dialogResult[dialogId] = '0';
					$("#" + dialogId).dialog('destroy');
				}
			};
			buttons.push(button1);
		}	
		var text2 = btn2text == undefined ? _translation_js__WEBPACK_IMPORTED_MODULE_4__.Translation.translation["continue"] : btn2text;
		var button2 = {
			text: text2,
			handler: function() {
				_this.dialogResult[dialogId] = '1';
				$("#" + dialogId).dialog('destroy');
			}
		};
		buttons.push(button2);
	
		$('#' + dialogId).dialog({
			title: '<div style="font-size:14px; color: #777;">'+_translation_js__WEBPACK_IMPORTED_MODULE_4__.Translation.translation.errors+'</div>',
			width: 900,
			height: 550,
			resizable: true,
			style: {'background': 'rgba(0, 0, 0, 0) linear-gradient(to bottom, #eff5ff 0px, #e0ecff 20%) repeat-x scroll 0 0', 'border-color':'#95b8e7'},
			closed: false,
			cache: false,
			modal: true,
			content:content,
			tools: [{
				iconCls:'icon-rept',
				handler:function(){
					if (filePath != null) {
						var url = _this.app.restUrl + "&rnd=" + _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.rnd() + "&cmd=opf&fn=" + encodeURIComponent(filePath) + "&fr=" + encodeURIComponent(fileName);
						$('#report_frame').attr('src', url);
					}
				}
			}],
			onOpen: function() {
				InterfaceController.popDlg.push(dialogId);
				InterfaceController.popDlgType.push(_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.DLG_NO_SEND);
				$("li[uuid]").on('click', function (e) {
					var uuid = $(this).attr('uuid');
					var rownum = -1;
					var row = $(this).attr('row');
					if (row != null && row.length > 0)
						rownum = parseInt(row);
						
					_this.focusComponent(uuid, rownum);
					
					_this.dialogResult[dialogId] = '1';
					$("#" + dialogId).dialog('destroy');
	
				});
			},
			onClose: function() {
				_this.dialogResult[dialogId] = '1';
				$("#" + dialogId).dialog('destroy');
			},
			onBeforeDestroy : function() {
				_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.blockPage();
				
				let opts = $(this).dialog('options');
	
				var par = {};
	        	if (_this.dialogResult[dialogId] == '0') {
	        		par["commitResult"] = 1;
	        	} else {
	        		par["commitResult"] = 0;
	        	}
	        	_this.app.query(par).then(response => {
		        	if(dialogId == InterfaceController.popDlg[InterfaceController.popDlg.length-1]){
		        		InterfaceController.popDlg.pop();
			        	InterfaceController.popDlgType.pop();
		        	}
		        });
			},
			buttons: buttons
		});
		
		_this.popupcount++;
	}

	showPopupErrors(errors, filePath, fileName, opts, popup, fatal, isDataIntegrityControl) {
		
		let _this = this;
		var content = this.errorUtil.higlightErrors(errors);
		
		this.fatal = fatal;
		this.isDataIntegrityControl = isDataIntegrityControl;
		this.opts = opts;
	
		var dialogId = 'or3_popup' + this.popupcount;
		$('#trash').append($("<div></div>").attr('id', dialogId));
	
		var buttons = [];
	
		var button1 = {
			text: _translation_js__WEBPACK_IMPORTED_MODULE_4__.Translation.translation.continue2,
			handler: function() {
				$("#" + dialogId).dialog('destroy');
			}
		};
		buttons.push(button1);
		
		if (fatal && isDataIntegrityControl) {
			var button3 = {
				text: _translation_js__WEBPACK_IMPORTED_MODULE_4__.Translation.translation.cancel,
				handler: function() {
					$("#" + dialogId).dialog('destroy');
					var openedDialogs = InterfaceController.openedDialogs;
					if (openedDialogs.length > 0) {
						var dialogId1 = openedDialogs[openedDialogs.length - 1];
						_this.setDialogBtnsEnabled(dialogId1, false);
						_this.sendUserDecision("1");
						_this.dialogResult[dialogId1] = '1';
						$("#" + dialogId1).dialog('destroy');
					}
				}
			};
			buttons.push(button3);
		} else {
			var button2 = {
				text: _translation_js__WEBPACK_IMPORTED_MODULE_4__.Translation.translation.save,
				handler: function() {
					$("#" + dialogId).dialog('destroy');
					
					var par = {};
		        	par["uid"] = opts.uid ;
		        	par["val"] = '0';
		        	if (opts.row != undefined) par["row"] = opts.row;
		        	if (opts.cuid != undefined) par["cuid"] = opts.cuid;
		        	par["cmd"] = "closePopup";
		        	_this.loadData(par, true).then(() => {
						popup.dialog('destroy');
					});
				}
			};
			buttons.push(button2);
		}
	
		$('#' + dialogId).dialog({
			title: '<div style="font-size:14px; color: #777;">'+_translation_js__WEBPACK_IMPORTED_MODULE_4__.Translation.translation.errors+'</div>',
			width: 600,
			height: 400,
			style: {'background': 'rgba(0, 0, 0, 0) linear-gradient(to bottom, #eff5ff 0px, #e0ecff 20%) repeat-x scroll 0 0', 'border-color':'#95b8e7'},
			closed: false,
			cache: false,
			modal: true,
			content:content,
			tools: [{
				iconCls:'icon-rept',
				handler:function(){
					if (filePath != null) {
						var url = _this.app.restUrl + "&rnd=" + _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.rnd() + "&cmd=opf&fn=" + encodeURIComponent(filePath) + "&fr=" + encodeURIComponent(fileName);
						$('#report_frame').attr('src', url);
					}
				}
			}],
			onOpen: function() {
				InterfaceController.popDlg.push(dialogId);
				InterfaceController.popDlgType.push(_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.DLG_POPUP_ERRORS);
				$("li[uuid]").on('click', function (e) {
					var uuid = $(this).attr('uuid');
					var rownum = -1;
					var row = $(this).attr('row');
					if (row != null && row.length > 0)
						rownum = parseInt(row);
						
					_this.focusComponent(uuid, rownum);
					
					$("#" + dialogId).dialog('destroy');
	
				});
			},
			onClose: function() {
				$("#" + dialogId).dialog('destroy');
			},
			onBeforeDestroy : function() {
				if(dialogId == InterfaceController.popDlg[InterfaceController.popDlg.length-1]){
					InterfaceController.popDlg.pop();
					InterfaceController.popDlgType.pop();
				}		
			},
			buttons: buttons
		});
		
		_this.popupcount++;
	}
	
	focusComponent(uuid, rownum) {
		let _this = this;
		var e = $('#' + uuid);
		if (rownum > -1) {
			var td_fields = $(".datagrid-btable td[field="+ uuid +"]");
			if (td_fields.length >= rownum) {
				e = $(td_fields.get(rownum));
			}
		}
		
		if (e != null && e.length > 0) {
			// Если компонент лежит на табпанели, пробегаем по всем родителям - табпанелям
			$.each(e.parents('.tabs-panels > .panel > .panel-body'), function(i, selTab) {
				var tabPanel = $(selTab).parent().parent().parent();
				var index = parseInt($(selTab).attr('tabindex'));
				tabPanel.tabs('select', index);
			});
		
			if (rownum > -1) {
				e.click();
				var parent = e.parents('.datagrid-body');
				var table = parent.find('.datagrid-btable');
				
				parent.scrollTop(e.offset().top - table.offset().top);
				parent.scrollLeft(e.offset().left - table.offset().left);
				
				var openedDialogs = InterfaceController.openedDialogs;
				if (openedDialogs.length == 0) {
					$('#app').scrollTop(e.parents('.datagrid').offset().top - $('#app').children('div').offset().top);
				}
			} else {
				if (e.hasClass('easyui-datebox')) {
					if (!e.hasClass('datebox-f')) e.datebox();
					e = e.datebox("textbox")
				} else if (e.hasClass('easyui-datetimebox')) {
					if (!e.hasClass('datetimebox-f')) e.datetimebox();
					e = e.datetimebox("textbox");
				} else if (e.hasClass('easyui-combobox')) {
					if (!e.hasClass('combobox-f')) e.combobox();
					e = e.combobox("textbox");
				} else if (e.hasClass('easyui-numberbox')) {
					if (!e.hasClass('numberbox-f')) e.numberbox();
					e = e.numberbox("textbox");
				}
				e.focus();
			}
		}
	}
	
	downloadFile(e, id, row, col) {
		let _this = this;
		var curTime = (new Date).getTime();
		e.stopPropagation();
		e.preventDefault();
		
		if (_this.lastDownloadTime == 0 || curTime - _this.lastDownloadTime > _this.waitTime) {
			_this.lastDownloadTime = curTime;
			var par = {};
			par["uid"] = id;
			par["cmd"] = "viewFile";
			if (row != null)
				par["row"] = row;
			if (col != null)
				par["col"] = col;
			
			_this.app.post(par).then(data => {
				_this.loadData({}, true);
				if (data.result == "success") {
					var url = _this.app.restUrl + "&trg=frm&cmd=opf&fn=" + encodeURIComponent(data.file);
					if (url.indexOf("/") > 0) url = "/" + url;
					url += "&rnd=" + _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.rnd();
					
					if (data.action == "print") {
						var opts = {url: url};
						var btn = $("<button></button>").appendTo($('#trash'));
						btn.printPage(opts);
						btn.click();
					} else if (data.action == "view") {
						if (data.ext == "html") {
							window.open(url);
						} else {
							$('#report_frame').attr('src', url);
						}
					} else {
						$('#report_frame').attr('src', url);
					}
				}
			});
		}
		return false;
	}
	
	openStartDialog(uid) {
		let _this = this;
		_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.blockPage();
		var par = {};
		par["uid"] = uid;
		par["cmd"] = "openTask";
		par["size"] = 1;
		
		_this.app.post(par).then(data => {
			if (data.result == 'error') {
				_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.alert(data.message, _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.ERROR);
			} else {
				_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.blockPage();
				_this.selectedRow = [];
				var dialogId = 'or3_popup' + _this.popupcount;
				$('#trash').append($("<div></div>").attr('id', dialogId));
				
				_this.dialogResult[dialogId] = '1';
				_this.dialogOpened = dialogId;
				
				$('#' + dialogId).dialog({
					title: '<div style="font-size:14px; color:#777;">'+data.t+'</div>',
					width: data.w,
					height: data.h,
					closed: false,
					cache: false,
					href: _this.app.restUrl + '&cmd=openTask&uid=' + uid + "&rnd=" + _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.rnd(),
					modal: true,
					uid: uid,
					onOpen: function() {
						$('#' + dialogId +' .panel').append($("<div class=\"glassDialog\"></div>"));
						$('#' + dialogId + ' .glassDialog').height($('#' + dialogId +' .panel').height()).width($('#' + dialogId +' .panel').width());
					},
					onLoad: function() {
						// Добавляем ID-окна в массив окон
						InterfaceController.openedDialogs.push(dialogId);
						InterfaceController.popDlg.push(dialogId);
						InterfaceController.popDlgType.push(_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.DLG_OPEN_AT_START);
						_this.loadData({});
						
						$('.clean-btn').click(function(e) {
							var par = {};
							par["uid"] = $(this).attr('id').substring(3);
							par["cmd"] = "clr";
							_this.postAndParseData(par);
							return false;
						});
						
						_this.preparefileUpload();
					},
					onClose: function() {
						_this.dialogResult[dialogId] = '1';
						$('#' + dialogId).dialog('destroy');
					},
					onBeforeDestroy : function() {
						// Удаляем ID-окна из массива окон
						InterfaceController.openedDialogs.pop();
						_this.dialogOpened = null;
						
						if (_this.dialogResult[dialogId] == '1' || _this.dialogResult[dialogId] == 1) {
							_this.app.nav.cancelStart(uid);
						}
						if(dialogId == InterfaceController.popDlg[InterfaceController.popDlg.length-1]){
			        		InterfaceController.popDlg.pop();
				        	InterfaceController.popDlgType.pop();
			        	}
					},
					buttons: [{
						text: _translation_js__WEBPACK_IMPORTED_MODULE_4__.Translation.translation.ok,
						handler: function() {
							_this.setDialogBtnsEnabled(dialogId, false);
							_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.blockPage();
							_this.dialogResult[dialogId] = '0';
							_this.app.nav.nextStep(true, dialogId);
						}
					},
					{
						text: _translation_js__WEBPACK_IMPORTED_MODULE_4__.Translation.translation.close,
						handler: function() {
							_this.setDialogBtnsEnabled(dialogId, false);
							$('body').unblock();
							_this.dialogResult[dialogId] = '1';
							$('#' + dialogId).dialog('destroy');
						}
					}]
				});
				_this.popupcount++;
			}
		}, 'json');
	}

	sendFile() {
		var file = document.getElementById("chooser").files.item(0);
		$('#getFile').dialog("close");
		_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.blockPage();
		
	    var url = this.app.restUrl + "&rnd=" + _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.rnd();
	   
		if (file) {
			var formData = new FormData();                  
			formData.append('getFile', file);
			$.ajax({
				type : 'POST',
				url : url,
				cache : false,
				contentType : false,
				processData : false,
				data : formData,
				success : function(data) {},
				dataType : 'json',
			});
		} else {
			var par = {};
			par["cmd"] = 'getFileIsNull';
			par["json"] = 1;
			$.ajax({
				type : 'POST',
				url : url,
				data : par,
				success : function(data) {},
				dataType : 'json',
				async : false
			});
		}
	}
	
	reload(uid) {
		let _this = this;
		var par = {"cmd":"reload","uid":uid};
		this.app.query(par).then(content => {
			var parent = $('#' + uid).parent();
			parent.html(content);
			parent.find('.easyui-panel:not(.tamur-tabs)').panel();
			parent.find('.easyui-datagrid').datagrid();
			preparefileUpload(parent);
	
			var par = {};
			par["rnd"] = _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.rnd();
			par["getChange"] = "";
			_this.postAndParseData(par);
		});
	}
	
	closeIfc() {
		this.endEditing();
		_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.blockPage(_translation_js__WEBPACK_IMPORTED_MODULE_4__.Translation.translation.saving);
		var par = {"cmd":"closeIfc"};
		
		this.app.query(par).then(response => {
			$('body').unblock();
		});
	}

	resetChanges() {
		let _this = this;
		_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.blockPage(_translation_js__WEBPACK_IMPORTED_MODULE_4__.Translation.translation.canceling);
		var par = {"cmd":"rollback"};
		this.loadData(par).then(data => {
			_this.hideChangeMsg();
			InterfaceController.styles = {};
		});
	}
	
	saveChanges() {
		let _this = this;
		this.endEditing();
		_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.blockPage(_translation_js__WEBPACK_IMPORTED_MODULE_4__.Translation.translation.saving);
		var par = {"cmd":"commit"};
			
		this.app.post(par).then(data => {
			if (data.result == "success") {
				_this.hideChangeMsg();
			} else {
				if (data.result == "fatal") {
					$('body').unblock();
				}
				_this.showErrors(data.errors, data.path, data.name, "commit", true, _translation_js__WEBPACK_IMPORTED_MODULE_4__.Translation.translation.save);
			}
			$('body').unblock();
		});
	}

	dgBtnAction2(com, i, btn) {
		if (!btn.hasClass('l-btn-disabled')) {
			this.dgBtnAction(com, i, btn);
		}
	}
	
	dgBtnAction(com, i, btn) {
		let _this = this;
		this.endEditing().then(() => {
		    var par = {};
			par["uid"] = i;
			if(com == "del") {
				var objs = $('#'+i).datagrid('getSelections');
				if(objs.length > 0) {
					var indxs = [objs.length];
					for (var j = 0; j < objs.length; j++) {
						indxs[j] = _this.globalIndex($('#'+i), $('#'+i).datagrid('getRowIndex', objs[j]));
					}
					var idx = indxs.join(",");
					par["idx"] = idx;
				}
			}
			
			var st = com.indexOf('media/img/');
			if (st>-1) {
				var fn = com.indexOf('.gif', st);
				par["com"] = com.substring(st+10,fn);
			} else {
				par["com"] = com;
			}
			par["cmd"] = "set";
			
			if (btn && com == "showDel") {
				var span = btn.find('.icon-showDel');
				if (span.length > 0)
					span.removeClass('icon-showDel').addClass('icon-showDelUn');
				else
					btn.find('.icon-showDelUn').removeClass('icon-showDelUn').addClass('icon-showDel');
			}
			
			_this.app.post(par).then(data => {
				if (data.changes) {
					_this.parseData(data);
					_this.reloadTables();
				} else {
			        if (data.result == "success") {
			        	$.messager.confirm('', data.message, function(e){
			                if (e){
			                	par["sure"] = 1;
			        			_this.loadData(par).then(data => {
			        				_this.showChangeMsg();
			        			});
			                }
			            });
			        }
				}
			});
		});
	}
	
	reportClick(btn) {
		let _this = this;
		var par = {};
		par["id"] = btn.attr('reportid');
		par["cmd"] = "print";
		
		_this.app.post(par).then(data => {
			if (data.result == 'error') {
				_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.alert(data.message, _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.ERROR);
			} else if (data.file) {
				var url = _this.app.restUrl + "&rnd=" +_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.rnd() + "&cmd=opf&fn=" + encodeURIComponent(data.file);
				$('#report_frame').attr('src', url);
			}
		});
	}
	
	changeInterfaceLang(code, e) {
		e.preventDefault();
	
		_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.blockPage();
		this.app.query({"setLang":code}).then(data => {
			var url = "index.jsp?guid=" + guid + "&rnd=" + _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.rnd() + document.location.hash;
			window.location.href = url;
		});
	}
	
	showOptions(options) {
		let _this = this;
		var content = "<table class='options'><tr><td>";
		$.each(options, function(i, option) {
			content += "<tr><td><input type='radio' name='option' value='" + i + "'/>" + option.o + "</td></tr>";
		});
		content += "</table>";
	
		var dialogId = 'or3_popup' + _this.popupcount;
		$('#trash').append($("<div></div>").attr('id', dialogId));
	
		_this.dialogResult[dialogId] = '1';
	
		var buttons = [];
	
		var button1 = {
			text: 'Ok',
			handler: function() {
				_this.dialogResult[dialogId] = '0';
				$("#" + dialogId).dialog('destroy');
			}
		};
		buttons.push(button1);
		var button2 = {
			text: _translation_js__WEBPACK_IMPORTED_MODULE_4__.Translation.translation.cancel,
			handler: function() {
				_this.dialogResult[dialogId] = '1';
				$("#" + dialogId).dialog('destroy');
			}
		};
		buttons.push(button2);
	
		$('#' + dialogId).dialog({
			title: '<div style="font-size:14px; color:#777;">Выбор дальнейшей обработки</div>',
			width: 500,
			height: 250,
			closed: false,
			cache: false,
			modal: true,
			content:content,
			onOpen: function() {
				InterfaceController.popDlg.push(dialogId);
				InterfaceController.popDlgType.push(_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.DLG_NO_SEND);
			},
			onClose: function() {
				_this.dialogResult[dialogId] = '1';
				$("#" + dialogId).dialog('destroy');
			},
			onBeforeDestroy : function() {
				var par = {"optionResult": -1};
	        	if (_this.dialogResult[dialogId] == '0') {
	        		var checked = $('#' + dialogId).find('input:checked');
	        		if (checked.length > 0)
	        			par["optionResult"] = checked.val();
	        	}
	        	_this.app.query(par).then(response => {
		        	if(dialogId == InterfaceController.popDlg[InterfaceController.popDlg.length-1]){
		        		InterfaceController.popDlg.pop();
			        	InterfaceController.popDlgType.pop();
		        	}
		        });
			},
			buttons: buttons
		});
		
		_this.popupcount++;
	}
	
	openTree(uid, title, row, colUid) {
		let _this = this;
	
		var dialogId = 'or3_popup' + _this.popupcount;
		$('#trash').append($("<div></div>").attr('id', dialogId));
		_this.dialogResult[dialogId] = '1';
	
		$('#' + dialogId).dialog({
			title: '<div style="font-size:14px; color:#777;">'+title+'</div>',
			width: 800,
			height: 600,
			closed: false,
			cache: false,
			href: _this.app.restUrl + '&cmd=openTree&uid=' + uid + (row != undefined ? '&row=' + row + "&cuid=" + colUid : ""),
			modal: true,
			uid: uid,
			row: row,
			cuid: colUid,
			onLoad: function() {
				InterfaceController.popDlg.push(dialogId);
				InterfaceController.popDlgType.push(_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.DLG_TREE_FIELD);
			},
			onClose: function() {
				_this.dialogResult[dialogId] = '1';
				$("#" + dialogId).dialog('destroy');
			},
			onBeforeDestroy : function() {
				_this.searchCount = -1;
				let opts = $(this).dialog('options');
	        	if (_this.dialogResult[dialogId] == 0) {
	                var par = {};
	        		par["cmd"] = "set";
	            	par["uid"] = opts.uid;
	            	if (opts.row != undefined) par["row"] = opts.row;
	            	if (opts.cuid != undefined) par["cuid"] = opts.cuid;
	    			par["val"] = -1;
	        		var selNode = $(this).find('.easyui-tree').tree('getSelected');
	        		if (selNode) {
	        			par["val"] = selNode.id;
	        		}
	        		_this.loadData(par);
	        	}
	        	if(dialogId == InterfaceController.popDlg[InterfaceController.popDlg.length-1]){
	        		InterfaceController.popDlg.pop();
		        	InterfaceController.popDlgType.pop();
	        	}
			},
			buttons: [{
				text: _translation_js__WEBPACK_IMPORTED_MODULE_4__.Translation.translation.ok,
				handler: function() {
					_this.dialogResult[dialogId] = '0';
	                _this.showChangeMsg();
					$("#" + dialogId).dialog('destroy');
				}
			},
			{
				text: _translation_js__WEBPACK_IMPORTED_MODULE_4__.Translation.translation.cancel,
				handler: function() {
					_this.dialogResult[dialogId] = '1';
					$("#" + dialogId).dialog('destroy');
				}
			}]
		});
		
		_this.popupcount++;
	}

	uploadImage(uid) {
		let _this = this;
		$('#upload').fileupload({
			dropZone: $('#' + uid),
			pasteZone: $('#' + uid),
		    url: _this.app.restUrl + '&uid=' + uid,
		    dataType: 'json',
	        done: function (e, data) {
	        	if (data.result.result == 'success') {
	                _this.showChangeMsg();
	        		_this.loadData({}, true);
	        	} else
	        		_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.alert(data.result.message, _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.ERROR);
	        }
		}).fileupload('option', {acceptFileTypes: /(\.|\/)(gif|jpe?g|png)$/i}).click();
	}
	
	displayEmptyParentPanels(comp) {
		if (comp.parents('.popUpPanContent').length == 0) {
			let _this = this;
			let parentPanels = comp.parents('.orpanel');
			$.each(parentPanels, function(i, panel) {
				if ($(panel).attr('id') && !_this.hiddenPanels[$(panel).attr('id')]) {
					$(panel).show();
				}
			});
			
			let childPanels = comp.find('.orpanel');
			$.each(childPanels, function(i, panel) {
				if (!_this.hiddenPanels[$(panel).attr('id')]) { 
					let children = $(panel).find(':not(td, tr, tbody, [onTop], [onTop] *)')
					var show = false;
					$.each(children, function(k, child) {
						if ( $(child).css('display') != 'none' && $(child).css("visibility") != "hidden") {
							show = true;
						}
					});
						
					if (show) {
						$(panel).show();
					}
				}
			});
		}
	}			

	hideEmptyParentPanels(comp) {
		if (comp.parents('.popUpPanContent').length == 0) { 
			let parentPanels = comp.parents('.orpanel');
			$.each(parentPanels, function(i, panel) {
				if ($(panel).find(':not(td, tr, tbody, [onTop], [onTop] *):visible').length == 0)
					$(panel).hide();
			});
		}
	}

	setFocus() {
		// чтобы при заходе в систему курсор сразу встал на input(глобальный поиск)
		document.getElementById("privateDeal").focus();
	}

	changeTooltipPref(val) {
		// всплывающие подсказки вкл, выкл
		val == 'true' ? true : false;
		var par = {"setTooltipPref":val};

		this.app.query(par).then(response => {
			if (response.status === 200) {
				  console.log("success changeTooltipPref")
			}
		});
		
		// дописать остальную функцию 
		// attachProcessTooltips('processess');
	}

	changeNoteSoundPref(val) {
		// звук вкл, выкл
		val == 'true' ? true : false;
		var par = {"setNoteSoundPref":val};

		this.app.query(par).then(response => {
			if (response.status === 200) {
				  console.log("success changeNoteSoundPref")
			}
		});
	}

	changeInstantECPPref(val) {
		// Хранить пароль ЭЦП во время сессии
		var par = {"setInstantECPPref": val};
	
		this.app.query(par).then(response => {
			if (response.status === 200) {
				  console.log("success changeInstantECPPref")
			}
		});
	}

	changePwdDialog(obligatory) {
		let this_ = this;
		var dialogId = 'or3_popup' + this_.popupcount;
		$('#trash').append($("<div></div>").attr('id', dialogId));
		this_.dialogResult[dialogId] = '1';
	
		var buttonOk = {
			text: _translation_js__WEBPACK_IMPORTED_MODULE_4__.Translation.translation.change,
			handler: function() {
				this_.dialogResult[dialogId] = '0';
				var par = {};
				par["cmd"] = "changePass";
				par["oldPass"] = $('#' + dialogId).find('[uid="oldPass"]').val();
				par["newPass"] = $('#' + dialogId).find('[uid="newPass"]').val();
				par["confirmPass"] = $('#' + dialogId).find('[uid="confirmPass"]').val();
				$.post(window.mainUrl + "&rnd=" + _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.rnd(), par, function(json) {
					_util_js__WEBPACK_IMPORTED_MODULE_0__.DataChecker.checkData(json).then(data => {
						if (data.result == 'error') {
							alert(data.message, _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.ERROR);
						} else {
							$("#" + dialogId).dialog('destroy');
							alert(data.message);
						}
					});
				}, 'json');
	
			}
		};
		
		var buttonCancel = {
			text: _translation_js__WEBPACK_IMPORTED_MODULE_4__.Translation.translation.cancel,
			handler: function() {
				this_.dialogResult[dialogId] = '1';
				$("#" + dialogId).dialog('destroy');
			}
		};
		
		var buttons = (obligatory) ? [buttonOk] : [buttonOk, buttonCancel];
		
		$('#' + dialogId).dialog({
			title: _translation_js__WEBPACK_IMPORTED_MODULE_4__.Translation.translation.passChange,
			width: 300,
			height: 200,
			closed: false,
			cache: false,
			closable: obligatory != true,
			href: window.contextName + '/jsp/pwd.jsp?guid=' + guid + '&rnd=' + _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.rnd(),
			modal: true,
			onLoad: function() {
				InterfaceController.popDlg.push(dialogId);
				InterfaceController.popDlgType.push(this_.DLG_CHANGE_PD);
			},
			onClose: function() {
				this_.dialogResult[dialogId] = '1';
				$("#" + dialogId).dialog('destroy');
			},
			onBeforeDestroy : function() {
				if(dialogId == InterfaceController.popDlg[InterfaceController.popDlg.length-1]){
					InterfaceController.popDlg.pop();
					InterfaceController.popDlgType.pop();
				}
			},
			buttons: buttons
		});
		
		this_.popupcount++;
	}
}


/***/ }),

/***/ "./login.js":
/*!******************!*\
  !*** ./login.js ***!
  \******************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   "Login": () => (/* binding */ Login)
/* harmony export */ });
/* harmony import */ var _util_js__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./util.js */ "./util.js");
/* harmony import */ var _translation_js__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./translation.js */ "./translation.js");
/* harmony import */ var _nca_js__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./nca.js */ "./nca.js");



                         
class Login {

	sLogin = false;
	language = "KZ";

	constructor(contextName, mainUrl, lang) {
		this.guid = _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.generateGUID();

		this.contextName = contextName;
		_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.restUrl = mainUrl;
		this.lang = lang;
		this.language = localStorage.getItem("EkyzmetLanguage");
	}

	init() {
		let _this = this;
		
		_translation_js__WEBPACK_IMPORTED_MODULE_1__.Translation.init(this.lang);
		this.browser = this.testBrowser();
		
		this.ncaLayer = new _nca_js__WEBPACK_IMPORTED_MODULE_2__.NCA();
		
		this.getContactInfo();

		$('#loginBtn').click(function() {
			$('#loginBtn').css({"background-color": "#298F94"});
			return _this.login(false);
		});
		$('#password').keydown(function(event) {
			if(event.keyCode === 13) {
				$('#loginBtn').css({"background-color": "#298F94"});
				return _this.login(false);
			}
		});
		$('#user').keydown(function(event) {
		 	if(event.keyCode === 13) {
				$('#loginBtn').css({"background-color": "#298F94"});
				return _this.login(false);
			}
		});
		$("#topLangKz").on("click", function() {
			_this.setLangToolTip("KZ");
		});

		$("#topLangRu").on("click", function() {
			_this.setLangToolTip("RU");
		});

		$('#loginEcpBtn').click(function() {
			return _this.loginECP();
		});

		$('#clearBtn').click(function() {
			$('#user').val('');
			$('#password').val('');
			$(".login-error-text").html("");
		}).keydown(function(event) {
			if(event.keyCode === 13) {
				$('#user').val('');
				$('#password').val('');
				$(".login-error-text").html("");
			}
		});
		
		$('.btn-login-ecp').click(function(e) {
			$('#login-pass-form').hide();
			$('.btn-login-pass').removeClass('selected');
			$('#login-ecp-form').show();
			$('.btn-login-ecp').addClass('selected');
			$(".login-error-text").html("");
		});
		$('.btn-login-pass').click(function(e) {
			$('#login-pass-form').show();
			$('.btn-login-pass').addClass('selected');
			$('#login-ecp-form').hide();
			$('.btn-login-ecp').removeClass('selected');
			$(".login-error-text").html("");
		});
		
		$('.label-forgot-pass').click(function(e) {
			_this.forgotPD(0);
		});

		$('.tooltip').css({'width':'800px'});
	}
	
	testBrowser() {
		BrowserDetect.init();
	
		if (BrowserDetect.version === "an unknown version" || 
			(
				(BrowserDetect.browser !== 'Chrome' || BrowserDetect.version < 35) 
				&& (BrowserDetect.browser !== 'Firefox' || BrowserDetect.version < 69) 
				&& (BrowserDetect.browser !== 'Explorer' || BrowserDetect.version < 10)
				&& (BrowserDetect.browser !== 'Mozilla' || BrowserDetect.version < 11)
			)
		) {
			$('#loginForm').removeAttr('onsubmit');
			$('#loginECPForm').removeAttr('onsubmit');
			$('#loginBtn').removeAttr('onclick');
			$('#loginECPBtn').removeAttr('onclick');
			$('#resetBtn').removeAttr('onclick');
			$('#loginBtn').hide();
			$('#loginECPBtn').hide();
			$('#resetBtn').hide();
			$('#errmsg').show();
		}
			
		return BrowserDetect.browser+";"+BrowserDetect.version+";"
				+BrowserDetect.OS+";"+BrowserDetect.mobile;
	}

	login(force) {
		this.whatLanguageNow();
		let _this = this,
			wait = this.language == "KZ" ? "Күте тұрыңыз..." : "Подождите...",
			enterTheSystem = this.language == "KZ" ? "Жүйеге кіру" : "Войти в систему";
			
		if (force || !$('#loginBtn').hasClass('btn-disabled')) {
			$('#loginBtn').addClass('btn-disabled').attr("disabled", "disabled").html(`<i class="icon-refresh icon-white icon-refresh-animate"></i> ${wait}`);

			let par = {
				name: $('#user').val(),
				passwd: $('#password').val(),
				browser: this.browser,
				json: 1,
				force: (force ? 1 : 0),
				sLogin: (this.sLogin ? 1 : 0),
				rnd: _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.rnd(),
				lang: this.lang,
				guid: this.guid
			};
			
			_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.post2(par).then(data => {
				if (data.result == "success") {
					if (data.tempReg) {
						alert(data.tempReg);
					}
					window.location.href = _this.contextName + "/qyzmet/index.jsp?guid=" + _this.guid + "&rnd=" + _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.rnd();
				} else if (data.passChange == "1") {
					alert(data.message);
					$('.login-error-text').text(data.message);
					_this.changePwdDialog(false);
				}  else if (data.passChange == "2") {
					_this.sLogin = true;
					alert(data.message);
					$('.login-error-text').text(data.message);
					_this.changePwdDialog(false);
				} else if (data.reconnect == "1") {
					if (confirm(data.message)) {
						_this.login(true);
					} else {
						$('#loginBtn').removeClass("btn-disabled").removeAttr("disabled").html(enterTheSystem);
						$('#resetBtn').removeClass("btn-disabled").removeAttr("disabled");
					}
				} else {
					_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.alert(data.message);
					$('.login-error-text').text(data.message);
					$('#loginBtn').removeClass("btn-disabled").removeAttr("disabled").html(enterTheSystem);
					$('#resetBtn').removeClass("btn-disabled").removeAttr("disabled");
				}
			});
		}
		return false;
	}
	
	loginECP() {
		this.whatLanguageNow();
		let _this = this,
		wait = this.language == "KZ" ? "Күте тұрыңыз..." : "Подождите...",
		actionCanceledUser = this.language == "KZ" ? "Әрекетті пайдаланушы жойды" : "Действие отменено пользователем",
		mistakeCode = this.language == "KZ" ? "Қате коды: " : "Код ошибки: ",
		messageForUser = this.language == "KZ" ? " Хабар: " : " Сообщение: ",
		selectSertificate = this.language == "KZ" ? "Сертификатты таңдаңыз" : "Выбрать сертификат";


		if (!$('#loginEcpBtn').hasClass('btn-disabled')) {
			$('.login-error-text').empty();
			$('#loginEcpBtn').addClass('btn-disabled').attr("disabled", "disabled").html(`<i class="icon-refresh icon-white icon-refresh-animate"></i> ${wait}`);	
			
			var par = {cmd:'getSecret', rnd: _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.rnd(), guid: this.guid, json:1, lang: this.lang};

			_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.post2(par).then(data => {
				if (data.secret) {
					let xml = '<login><secret>' + secret + '</secret></login>';
					_this.ncaLayer.signXml(_nca_js__WEBPACK_IMPORTED_MODULE_2__.NCA.COMMON, _nca_js__WEBPACK_IMPORTED_MODULE_2__.NCA.AUTH, xml).then(data2 => {
						if (data2.responseObject) {
							let signedData = data2.responseObject;
							par = {
								secret: data.secret,
								signedData: signedData,
								browser: this.browser,
								rnd: _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.rnd(),
								guid: this.guid,
								json: 1,
								lang: this.lang
							};
							_this.sendLoginInfo(par);
						} else {
							let error = (data2.message === 'action.canceled')
								? actionCanceledUser
								: mistakeCode + data2.code + messageForUser + data2.message;
							console.error("msg text: " + error);
							$('.login-error-text').text(error);
							$('#loginEcpBtn').removeClass("btn-disabled").removeAttr("disabled").html(selectSertificate);
						}
					}, error => {
						console.error("error text: " + error);
						$('.login-error-text').text(error);
						$('#loginEcpBtn').removeClass("btn-disabled").removeAttr("disabled").html(selectSertificate);
					});
				} else {
					_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.alert(data.message);
					$('.login-error-text').text(data.message);
					$('#loginEcpBtn').removeClass("btn-disabled").removeAttr("disabled").html(selectSertificate);
				}
			});
		}
		return false;
	}
	
	sendLoginInfo(par) {
		this.whatLanguageNow();
		let _this = this,
			selectSertificate = this.language == "KZ" ? "Сертификатты таңдаңыз" : "Выбрать сертификат";

		_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.post2(par).then(data => {
			if (data.result == "success") {
				if (data.dl) {
					_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.alert(data.dl);
				}
				if (data.tempReg) {
					_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.alert(data.tempReg);
				}
				window.location.href = _this.contextName + "/qyzmet/index.jsp?guid=" + _this.guid + "&rnd=" + _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.rnd();
			} else if (data.reconnect == "1") {
				_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.confirmMessage(data.message, function(e) {
					if (e) {
						par["force"] = 1;
						_this.sendLoginInfo(par);
					} else {
						$('#loginEcpBtn').removeClass("btn-disabled").removeAttr("disabled").html(selectSertificate);
					}
				});
			} else {
				_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.alert(data.message);
				$('.login-error-text').text(data.message);
				$('#loginEcpBtn').removeClass("btn-disabled").removeAttr("disabled").html(selectSertificate);
			}
		});
	}
	
	reset() {
		if (!$('#resetBtn').hasClass('btn-disabled')) {
			$('#loginForm')[0].reset();
			$('#loginECPForm')[0].reset();
		}
	}
	
	forgotPD(purpose) {
		this.whatLanguageNow();
		let _this = this,
			enterUserName = this.language == "KZ" ? "Пайдаланушы атын енгізіңіз" : "Введите имя пользователя";

		var un = $('#user').val();
		if (un == null || un.length < 1) {
			_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.alert(enterUserName);
		} else {
			var par = {};
			par["arg0"] = $('#user').val();
			par["cmd"] = "execute";
			par["cls"] = "ВСПОМОГАТЕЛЬНЫЙ КЛАСС";
			par["name"] = "restoreAccess";
			par["arg1"] = purpose;
			par["json"] = 1;
			par["rnd"] = _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.rnd();
			par["arg2"] = this.language;
		  	_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.post2(par).then(data => {
				console.log(data);
				let msg = _this.language == "KZ" ? data.msgKz : data.msgRu;
		  		if (purpose == 0 && data.code == 0) {
		  			_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.confirmMessage(msg, function(e) {
		  				if (e)  
		  					_this.forgotPD(1);
		  			});
		  		} else {
		 			_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.alert(msg);
		  		}
			});
		}
	}
	
	getContactInfo() {
		this.whatLanguageNow();
		let _this = this;
		var par = {"cmd":'contactInfo', "additionalInfo": "true", "json":1};
		let consultant = this.language == "KZ" ? "Кеңесші" : "Консультант",
			supportService = this.language == "KZ" ? "Қолдау қызметі" : "Служба поддержки",
			downloadCryptoSocket = this.language == "KZ" ? "ЭЦҚ-мен жұмыс істеу үшін бағдарламаны жүктеу және орнату (CryptoSocket, 03.09.2021 жаңартылған)" : "Скачать и установить программу для работы с ЭЦП (CryptoSocket, обновлено 03.09.2021)",
			connectionCryptoSocket = this.language == "KZ" ? "Cryptosocket көмегімен бастапқы қосылымды орнатыңыз" : "Установить первичное соединение с CryptoSocket";

		_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.query(par).then(response => {
			response.json().then(data => {
				if (data.contacts) {		
					
					// var table = `<div style="overflow: auto;"><table class="contactInfoTable"><thead><tr><th>${consultant}</th><th>Телефон</th><th>E-mail</th></tr></thead><tbody>`;
					var table = `<div style="overflow: auto;"><table class="contactInfoTable"><thead><tr><th>Телефон</th><th>E-mail</th></tr></thead><tbody>`;
					for (var i = 0; i < data.contacts.length; i++) {
						var contact = data.contacts[i];
						// var row = "<tr><td align='center' width='500'>" + Util.encodeHTML(contact.person) + "</td><td align='center' width='500'>" + Util.encodeHTML(contact.telephone) + "</td><td align='center' width='350'><a href=\"mailto:" + Util.encodeHTML(contact.email) + "\">" + Util.encodeHTML(contact.email) + "</a></td></tr>";

						var row = "<tr><td align='center' width='250'>" + _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.encodeHTML(contact.telephone) + "</td><td align='center' width='150'><a href=\"mailto:" + _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.encodeHTML(contact.email) + "\">" + _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.encodeHTML(contact.email) + "</a></td></tr>";
						table = table + row;
					}
				  	table = table + "</tbody></table><div>";
				  	// var content = `<div class="help-name">${supportService}</div><br>` + table;
					var content = table;
				  	$('.tool-help').append($(content));
				}
				if (data.additionalInfo) {
					$('.tool-help').append("<br><div align='center' width='500' style='font-size: 10pt; font-weight: bold'>" + _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.encodeHTML(data.additionalInfo) + "</div>")
				}
		  		var content = `<br><a href="${_this.contextName}/jsp/media/files/SetupCSPv6.3_20220903.exe">${downloadCryptoSocket}</a>`;
				console.log(_this.contextName)
		  		$('.tool-help').append($(content));
		  		var content = `<br><a href="https://localhost:6127/tumarcsp">${connectionCryptoSocket}</a>`;
		  		$('.tool-help').append($(content));
		  		
		  		$('.label-show-support').tooltip({
					deltaX: -20,
				    content: $('.tool-help').html(),
				    showEvent: 'click',
				    onPosition: function() {
				    	//$(this).tooltip('tip').css('left', $(this).offset().left + $(this).width() - $(this).tooltip('tip').width() - 15);
				    	//$(this).tooltip('arrow').css('left', $(this).tooltip('tip').width() - 20);
			    	},
				    onShow: function(){
				        var t = $(this);
				        t.tooltip('tip').unbind().bind('mouseenter', function(){
				            t.tooltip('show');
				        }).bind('mouseleave', function() {
				            t.tooltip('hide');
				        }).css({
				        	width: '500px'
				        });
				    }
				});
			});
		});
	}
	
	changePwdDialog(obligatory) {
		let _this = this,
			change = this.language == "KZ" ? "Өзгерту" : "Изменить",
			passSuccessFullChanged = this.language == "KZ" ? "Құпия сөз сәтті өзгертілді!" : "Пароль успешно изменен!",
			cancelButton = this.language == "KZ" ? "Бас тарту" : "Отмена",
			enterTheSystem = this.language == "KZ" ? "Жүйеге кіру" : "Войти в систему",
			changePassword = this.language == "KZ" ? "Құпия сөзді өзгерту" : "Смена пароля";

		var dialogId = 'or3_popup';
		$('body').append($("<div></div>").attr('id', dialogId));
	
		var buttonOk = {
			text: change,
			handler: function() {
				$("#" + dialogId + " .dialog-button .l-btn").linkbutton('disable');
				var par = {json: 1, guid: _this.guid, browser: _this.browser, configNumber: 0};
	    		par["name"] = $('#user').val();
	        	par["passwd"] = $('#' + dialogId).find('[uid="oldPass"]').val();
	        	par["newPass"] = $('#' + dialogId).find('[uid="newPass"]').val();
	        	par["confirmPass"] = $('#' + dialogId).find('[uid="confirmPass"]').val();
	    		
	    		_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.post2(par).then(data => {
	        		if (data.result == 'error') {
	        			alert(data.message);
						$("#" + dialogId + " .dialog-button .l-btn").linkbutton('enable');
	        		} else {
	        			$("#" + dialogId).dialog('destroy');
	        			alert(passSuccessFullChanged);
	        			window.location.href = _this.contextName + "/qyzmet/index.jsp?guid=" + _this.guid + "&rnd=" + _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.rnd();
	        		}
	    		});
			}
		};
		
		var buttonCancel = {
			text: cancelButton,
			handler: function() {
				$("#" + dialogId).dialog('destroy');
				$('#loginBtn').removeClass("btn-disabled").removeAttr("disabled").html(enterTheSystem);
				$('#resetBtn').removeClass("btn-disabled").removeAttr("disabled");
				if(_this.sLogin){
					_this.login(false);
				}
			}
		};
		
		var buttons = (obligatory) ? [buttonOk] : [buttonOk, buttonCancel];
				
		$('#' + dialogId).dialog({
			title: changePassword,
			width: 300,
			height: 200,
			closed: false,
			cache: false,
			closable: obligatory != true,
			href: _this.contextName + '/qyzmet/pwd.jsp?guid=' + guid + '&rnd=' + _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.rnd(),
			modal: true,
			onClose: function() {
				$("#" + dialogId).dialog('destroy');
				$('#loginBtn').removeClass("btn-disabled").removeAttr("disabled").html(enterTheSystem);
				$('#resetBtn').removeClass("btn-disabled").removeAttr("disabled");
			},
			onBeforeDestroy : function() {
			},
			buttons: buttons
		});
	}

	whatLanguageNow() {
		this.language = localStorage.getItem("EkyzmetLanguage");
	}

	setLangToolTip(language) {
		let consultant = language == "KZ" ? "Кеңесші" : "Консультант",
			supportService = language == "KZ" ? "Қолдау қызметі" : "Служба поддержки",
			downloadCryptoSocket = language == "KZ" ? "ЭЦҚ-мен жұмыс істеу үшін бағдарламаны жүктеу және орнату (CryptoSocket, 03.09.2021 жаңартылған)" : "Скачать и установить программу для работы с ЭЦП (CryptoSocket, обновлено 03.09.2021)",
			connectionCryptoSocket = language == "KZ" ? "Cryptosocket көмегімен бастапқы қосылымды орнатыңыз" : "Установить первичное соединение с CryptoSocket";

		$(".help-name").html(supportService);
		$(".contactInfoTable thead th").each(function(index) {
			if(index == 3) {
				$(this).html(consultant);
			}
		});
		$(".contactInfoTable tbody tr td").each(function(index) {
			if($(this).html() == "Служба поддержки") {
				$(this).html(supportService)
			}
			if($(this).html() == "Қолдау қызметі") {
				$(this).html(supportService)
			}
		});

		$(".tooltip-content a").each(function(index) {
			if(index == 3) {
				$(this).html(downloadCryptoSocket);
			}
			if(index == 4) {
				$(this).html(connectionCryptoSocket);
			}
		});
	}
	
	changeLanguage(lang) {
		this.lang = lang;
		_translation_js__WEBPACK_IMPORTED_MODULE_1__.Translation.init(lang);
	}
}

/***/ }),

/***/ "./main.js":
/*!*****************!*\
  !*** ./main.js ***!
  \*****************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   "PortletApp": () => (/* binding */ PortletApp)
/* harmony export */ });
/* harmony import */ var _util_js__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./util.js */ "./util.js");
/* harmony import */ var _navigation_js__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./navigation.js */ "./navigation.js");
/* harmony import */ var _ifc_js__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./ifc.js */ "./ifc.js");
/* harmony import */ var _orders_js__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./orders.js */ "./orders.js");
/* harmony import */ var _notifications_js__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ./notifications.js */ "./notifications.js");
/* harmony import */ var _monitor_js__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ./monitor.js */ "./monitor.js");
/* harmony import */ var _processes_js__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! ./processes.js */ "./processes.js");
/* harmony import */ var _archive_js__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! ./archive.js */ "./archive.js");
/* harmony import */ var _translation_js__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! ./translation.js */ "./translation.js");
/* harmony import */ var _events_js__WEBPACK_IMPORTED_MODULE_9__ = __webpack_require__(/*! ./events.js */ "./events.js");
/* harmony import */ var _polling_js__WEBPACK_IMPORTED_MODULE_10__ = __webpack_require__(/*! ./polling.js */ "./polling.js");
/* harmony import */ var _analytic_js__WEBPACK_IMPORTED_MODULE_11__ = __webpack_require__(/*! ./analytic.js */ "./analytic.js");
/* harmony import */ var _nca_js__WEBPACK_IMPORTED_MODULE_12__ = __webpack_require__(/*! ./nca.js */ "./nca.js");
/* harmony import */ var _ping_js__WEBPACK_IMPORTED_MODULE_13__ = __webpack_require__(/*! ./ping.js */ "./ping.js");
/* harmony import */ var _personalPhoto_js__WEBPACK_IMPORTED_MODULE_14__ = __webpack_require__(/*! ./personalPhoto.js */ "./personalPhoto.js");
/* harmony import */ var _autocomplete_js__WEBPACK_IMPORTED_MODULE_15__ = __webpack_require__(/*! ./autocomplete.js */ "./autocomplete.js");
/* harmony import */ var _help_js__WEBPACK_IMPORTED_MODULE_16__ = __webpack_require__(/*! ./help.js */ "./help.js");



















class PortletApp {
	
	mainUrl = "";
	restUrl = "";
	// создал Жаркын
	centerPanelWidth = null;
	centerPanelLeft = null;
	OLAP_URL = "";
	translation = null;

	constructor(mainUrl, restUrl, userId, lang) {
		this.mainUrl = mainUrl;
		this.restUrl = restUrl;
		this.userId= userId;
		this.lang = lang;
		_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.restUrl = restUrl;
		this.OLAP_URL = `http://${new URL(document.location.href).host}/analytic/rest/`;
	}
	
	init() {
		let app = this;

		_translation_js__WEBPACK_IMPORTED_MODULE_8__.Translation.init(this.lang);
		this.translation = _translation_js__WEBPACK_IMPORTED_MODULE_8__.Translation.translation;

		this.polling = new _polling_js__WEBPACK_IMPORTED_MODULE_10__.Polling(app);
		this.polling.longPolling();
		
		let nav = new _navigation_js__WEBPACK_IMPORTED_MODULE_1__.Navigation(app);
		this.nav = nav;

		this.analytic = new _analytic_js__WEBPACK_IMPORTED_MODULE_11__.Analytic(app);
		// this.analytic.getOlapPort();

		let ifcController = new _ifc_js__WEBPACK_IMPORTED_MODULE_2__.InterfaceController(app);
		this.ifcController = ifcController;
		ifcController.init();

		let pingClass = new _ping_js__WEBPACK_IMPORTED_MODULE_13__.Ping();
		if ($('#ping').length > 0) pingClass.init();

		let localStorLanguage = localStorage.getItem("EkyzmetLanguage");

		if(localStorLanguage) {
			let tempLang = $('#topLangKz').hasClass("selected") ? "KZ" : "RU";

			if(localStorLanguage !== tempLang) {
				ifcController.changeInterfaceLang(localStorLanguage, event)
			}
		}

		this.monitor = new _monitor_js__WEBPACK_IMPORTED_MODULE_5__.Monitor(app);
		this.monitor.init();
		
		let orders = new _orders_js__WEBPACK_IMPORTED_MODULE_3__.Orders(app);
		this.orders = orders;
		this.orders.init();
		
		let notifications = new _notifications_js__WEBPACK_IMPORTED_MODULE_4__.Notifications(app);
		this.notifications = notifications;
		this.notifications.init();

		let processes = new _processes_js__WEBPACK_IMPORTED_MODULE_6__.Processes(app);
		this.processes = processes;
		this.processes.init();
		
		let archives = new _archive_js__WEBPACK_IMPORTED_MODULE_7__.Archives(app);
		this.archives = archives;
		this.archives.init();

		this.help = new _help_js__WEBPACK_IMPORTED_MODULE_16__.Help(app);
		this.help.init();
		
		this.ncaLayer = new _nca_js__WEBPACK_IMPORTED_MODULE_12__.NCA();
		
		let photo = new _personalPhoto_js__WEBPACK_IMPORTED_MODULE_14__.PersonalPhoto(app);
		photo.initProfile(app.userId);
		$("#change-my-photo").on("click", function() {
			photo.uploadYourImage();
		});
		
		$("#delete-my-photo").on("click", function() {
			photo.deleteImage();
		});
		
		$("#take-photo-from-sys").on("click", function() {
			photo.copyImageFromData();
		});
		_events_js__WEBPACK_IMPORTED_MODULE_9__.EventOps.init(this);

		$(window).on('hashchange', function() {
			var day = location.hash;
			nav.hashChanged(day);
		});
		nav.hashChanged(location.hash);

		$(document).ready(function() {
			let par = {"sfunc":1,"cls":"MainPage","name":"otherMethods", "arg0":app.userId, "arg1": "getPoruchWidth"};
			app.query(par).then(response => {
				if (response.status === 200) {
      				response.json().then(json => {
						if(json.width == 1) {
							app.pushAside();
						}
					});
				}
			});
		});

		$("body").on('click', '.order-type', function() {
			let jThis = $(this);
			if (jThis.hasClass('order-type-selected')) {
				console.log('no change');
			} else {
				var type = jThis.attr('code');
				location.hash = 'orders=' + (type ? type : 'all');
			}
		    return false;
		});

		$("body").on('click', '.order-block .title', function() {
			let jThis = $(this);
			if (jThis.attr('type') === 'out')
				nav.openOrder(null, jThis.attr('iuid'), jThis.attr('iter'), false);
			else
				nav.openOrder(jThis.attr('iter'), jThis.attr('proc'), jThis.attr('uid'), jThis.attr('type') !== 'my');			
		});
		
		$("body").on('click', '.collapse-portlet', function() {
			let jThis = $(this);
			if (jThis.hasClass('icon-arrows-up')) {
				jThis.parents('.portlet').find('.portletBody').hide();
				jThis.parents('.portlet').parent().css({
					"flex-basis": "content",
					"flex-grow": "0"
				});
				jThis.addClass('icon-arrows-down').removeClass('icon-arrows-up');
			} else {
				jThis.addClass('icon-arrows-up').removeClass('icon-arrows-down');
				jThis.parents('.portlet').parent().css({
					"flex-basis": "",
					"flex-grow": ""
				});
				jThis.parents('.portlet').find('.portletBody').show();
			}
		});

		$('.user-info').tooltip({
			deltaX: -20,
		    content: $('.popup_user_menu').html(),
		    showEvent: 'click',
		    onUpdate: function(content){
		    },
		    onPosition: function() {
		    	$(this).tooltip('tip').css('left', $(this).offset().left + $(this).width() - $(this).tooltip('tip').width() - 15);
		    	$(this).tooltip('arrow').css('left', $(this).tooltip('tip').width() - 20);
	    	},
		    onShow: function(){
		        var t = $(this);
		        t.tooltip('tip').unbind().bind('mouseenter', function(){
		            t.tooltip('show');
		        }).bind('mouseleave', function() {
		            t.tooltip('hide');
		        });
		    }
		});
		
		key('ctrl+shift+z', function() {
			var str = "";
			$('[data-uid]').each(function(i, p) {
				if (($(this).closest('.popUpPanContent').length == 0))
					str += $(this).attr('data-uid') + "\n";
			});
			_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.alert(str);
			return false;
		});

		document.addEventListener("click", function(e) {
			let arr = ['popUpPan', "popUpPanContent", "combo-p", "window-body", "panel-header", "dialog-button", "messager-window", "calendar-day"];
			let bool = false;
			for(let nameClass of arr) {
				if(e.target.closest("." + nameClass)) {
					bool = true;
					break;
				}
			}
			if(!bool) {
				app.hidePopUpPanContent();
			}
		});

		this.loadPortletsContent();
		
		_autocomplete_js__WEBPACK_IMPORTED_MODULE_15__.Autocomplete.autocomplete(document.getElementById("privateDeal"));
	}
	
	loadPortletsContent() {
		let app = this;
		$(".portlet, .widget").each(function(i) {
			var portlet = $(this);
			var clsName = portlet.attr('cls');
			var methodName = portlet.attr('method');
			
			app.loadPortletContent(clsName, methodName);
		});
	}
	
	loadPortletContent(clsName, methodName, arg="") { 
		// arg - передовать объект с аргументами {"arg1": "text1", "arg2":"text2"}. Можно и не передовать
		if (clsName && methodName) {
			if(arg !== "") {
				let mainSettings = {"sfunc":1,"cls":clsName,"name":methodName, "arg0":this.userId};
				var par = {...mainSettings, ...arg};
			} else {
				var par = {"sfunc":1,"cls":clsName,"name":methodName, "arg0":this.userId};
			}

			this.query(par).then(response => {
				if (response.status === 200) {
      				response.json().then(json => {
						this.parsePortletContent(json);
					});
				}
			});
		}
	}

	parsePortletContent(json) {
		if (json) {
			_util_js__WEBPACK_IMPORTED_MODULE_0__.DataChecker.checkData(json).then(res => {
				if (res.portletId) {
					var div = $('#' + res.portletId);
					if (div.length > 0) {
						if (res.title) {
								$('#' + res.portletId + ' .portletTitle span').html(res.title);				
						} else {
							// здесь код поменял Жаркын, чтобы узнать на каком языке показывать текст =========
							if($('#topLangKz').hasClass("selected")) {
								$('#' + res.portletId + ' .portletTitle span').html(res.titleKz);
							} else {
								$('#' + res.portletId + ' .portletTitle span').html(res.titleRu);
							}
							// ==========================================
						}
						if (div.find('.portletBody').length > 0)
							this.parseContentByType(res, div.find('.portletBody'));
						else
							this.parseContent(res, div);
					}
				}
			});
		}
	}
	
	parseContent(res, div) {
		// здесь код изменил Жаркын, чтобы выбрать язык
		if(!res.title) {
			if($('#topLangKz').hasClass("selected")) {
				res.title = res.titleKz;
				res.value = res.valueKz;
			} else {
				res.title = res.titleRu;
				res.value = res.valueRu;
			}
		}
		var html = '<img class="widget-img" src="css/img/' + res.image + '" /><div class="vert">';
		html += '<span class="widget-title">' + res.title + '</span>';
		html += '<span class="widget-value">' + res.value + '</span></div>';
		div.append($(html));
	}
	
	parseContentByType(res, div) {
		let app = this;
		
		// здесь код изменил Жаркын, чтобы выбрать язык
		if(!res.content) {
			res.content = $('#topLangKz').hasClass("selected") ? res.contentKz : res.contentRu;
		}
		if (res.contentType == 'startProcess') {
			$.each(res.content, function(i, content) {
				var html = '<div class="process" uid="' + content.uid + '">';
				html += '<i class="icon-left icon-' + content.icon + '"/>';
				html += '<span>' + content.title + '</span>';
				html += '<i class="icon-right icon-arrow-right" />';
				html += '</div>';
				div.append($(html));
			});
		}
		else if (res.contentType == 'openIfc') {
			$.each(res.content, function(i, content) {
				var html = '<div class="ifc">';
				html += app.parseJSONContent(content, 'ifc');
				html += '</div>';
				div.append($(html));
			});
		}
		else if (res.contentType == 'orders') {
			let params = _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.parseHash(encodeURI(location.hash));
			let tabSelected = params.orders ? params.orders : null;
		
			var html = '<div class="orders">';
			html += '<div class="order-types">';
			
			var counts = {};
			var ordersDivs = {};
			
			$.each(res.types, function(i, type) {
				var isSelected = (tabSelected) ? (type.code == tabSelected) : (type.selected != null && type.selected != undefined);
				html += '<span class="order-type' + (isSelected ? ' order-type-selected' : '') + '"' 
						+ ((type.code) ? ' code="' + type.code + '"' : '') 
						+ '>';
				if (type.code)
					counts[type.code] = 0;
				else
					counts.all = 0;
				html += type.title;
				html += '<span class="order-count">';
				html += '</span>';
				html += '</span>';
				
				ordersDivs[type.code] =  '<div class="order-blocks orders-' + type.code + (isSelected ? ' order-blocks-selected"' : '" style="display: none;"')
						+ ((type.code) ? ' code="' + type.code + '"' : '') 
						+ '>';
			});
			// здесь код поменял Жаркын 24.01.2023, создал кнопку, чтобы задачи расскрыть на всю страницу
			let language = "ru";
			$('#topLangKz').hasClass("selected") ? language = "kz" : language = "ru";
			let hint = language === "kz" ? "Кеңейту" : "Расширить";
			html += `<button class="tasksFullWidth" title="${hint}">&hoarr;</button>`; // только эти строки добавил

			html += '</div>';
			$.each(res.content, function(i, content) {
				if (content.type) {
					ordersDivs[content.type] += '<div uid="' + content.uid + '" class="order-block" type="' + content.type + '">';
					if (content.type == 'my')
						ordersDivs[content.type] += '<i class="delete-order icon-close" task="' + content.proc + '"/>';
					
					ordersDivs[content.type] += app.parseJSONContent(content, 'order');
					ordersDivs[content.type] += '</div>';
					counts[content.type]++;
					
				}
				counts.all++;
			});
			$.each(res.types, function(i, type) {
				ordersDivs[type.code] += '</div>';
				html += ordersDivs[type.code];
			});
			
			html += '</div>';
			div.append($(html));
			
			$('.tasksFullWidth').on('click', function() {
				app.pushAside();
			});  // и здесь

			$.each(counts, function(type, count) {
				if (type === 'all')
					div.find('.order-type:not([code]) .order-count').html(count);
				else
					div.find('.order-type[code=' + type + '] .order-count').html(count);
			});
		}
		else if (res.contentType == 'chart') {
			var canvas = $('<canvas id="myChart" width="100%" height="100%"></canvas>');
			div.append(canvas);
			
			var scales = res.scales ? res.scales : {
				r: {
					beginAtZero: true,
					min: 0,
			        max: 5,
					autoSkip: false,
			        stepSize: 1
				}
			};
			// здесь код поменял Жаркын для выбора языка паутины
			res.data = $('#topLangKz').hasClass("selected") ? res.dataKz : res.dataRu;

			const myChart = new Chart(canvas, {
	    		type: res.chartType,
	    		data: res.data,
				options: {
					maintainAspectRatio: false,
					elements: {
				    	line: {
				        	borderWidth: 3
				      	}
				    },
					scales: scales,
					plugins: {
						legend: {
							position: 'bottom'
						}
					}
				},
			});
		}
	}
	
	parseJSONContent(json, contentType) {
		var res = "";
		let orderStatus = ["label-success", "label-warning", "label-important", "label-info"];
		// здесь код изменил Жаркын, чтобы выбрать язык
		let lang = "ru";
		lang = $('#topLangKz').hasClass("selected") ? "kz" : "ru";
		
		for (var tag in json) {
			if (tag === 'line') {
				res += '<div class="content-row">';
				res += this.parseJSONContent(json[tag], contentType);
				if (json.ctrl) {
					let textTitle = lang == "kz" ? "Орындау мерзімі" : "Срок исполнения";
					res += `<div class="order-deadline ${orderStatus[json.status]}" title="${textTitle}">${json.ctrl}</div>`;
				} 
				res += '</div>';
			} else if (tag === 'col') {
				res += '<div class="content-column">';
				res += this.parseJSONContent(json[tag], contentType);
				res += '</div>';
			} else if (tag === 'views') {
				res += '<span class="views"><i class="icon-eye-opened"/>' + json.views + '</span>';
			} else if (tag === 'image') {
				res += '<span class="content-image"><img src="' + (json.image.length > 0 ? json.image : 'css/img/empty-avatar-30.png') + '" /></span>';
			} else if (tag === 'typeIcon') {
				// res += '<i class="icon-type icon-type-' + json.typeIcon + '"/>';
			} else if (tag === 'location') {
				res += '<span class="location"><i class="icon-location"/>' + json.location + '</span>';
			} else if (tag === 'linkRu') {
				if(!json.link) {json.link = lang == "kz" ? json.linkKz : json.linkRu;}
				res += '<a class="link" onclick="javascript:openIfc(\'' + json.ifc + '\', \'' + json.obj + '\')">' + json.link + '</a>';
			} else if (contentType === 'order' && tag === 'title') {
				let iuidHtml = (json.line && json.line.col && json.line.col.iuid) ? ('" iuid="' + json.line.col.iuid) : '';
				let orderColor = "";
				if(json.type == "in" || json.type == "out") {
					orderColor = json.status == 0 ? "#468847" : json.status == 1 ? "#F89406" : "#E05540";
				}
				res += '<span class="title" style="color:' + orderColor + ';" iter="' + json.iter + '" proc="' + json.proc + '" uid="' + json.uid + iuidHtml + '" type="' + json.type + '">' + json.title + '</span>';
			} else if (tag !== 'uid' && tag !== 'ifc' && tag !== 'obj' && tag !== 'type' && tag !== 'proc'  && tag !== 'iter' && tag !== 'linkKz' && tag !== "status" && tag !== "ctrl") {
				res += '<span class="' + tag + '">' + json[tag] + '</span>';
			}
		}
		return res;
	}

	query(params) {
		let url = (params.cmd) ? (this.restUrl + '&cmd=' + params.cmd) : this.restUrl;
		return fetch(url, {
			method: 'POST',
	    	credentials: 'include',
			body: $.param(params)
	    });
	}
	
	post(par) {
		let _this = this; 
		return new Promise((resolve, reject) => {
			_this.query(par).then(response => {
				response.json().then(json => {
					_util_js__WEBPACK_IMPORTED_MODULE_0__.DataChecker.checkData(json).then(data => {
						resolve(data);
					});
				});
			});
		});
	}
	
	logout() {
		let _this = this;
		_this.ifcController.forseSaveChanges().then(data => {
			//if (confirm('Вы действительно хотите выйти из Системы?')) { #1532
			let par = {"cmd":"ext", "json":1};
			_this.query(par).then(response => {
				if (response.status === 200) {
					response.json().then(data => {
						window.location.href = _this.mainUrl + "/qyzmet/login.jsp?rnd=" + _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.rnd();
					});
				}
			});
			//}
		});
	}

	resize(selector) {
		let app = this;
		var comp = $(selector);
		
		if (comp.is(":visible")) {
			if (comp.hasClass('easyui-panel')) {
				comp.panel();
				comp.panel('resize');
			} else if (comp.hasClass('easyui-tabs'))
				comp.tabs('resize');
			
			$.each(comp.children(), function(i, child) {
				app.resize(child);
			});
		}
	}

	resizeHeight() {
		var c = $('#ui-panel').layout();
		var pc = c.layout('panel','center');				// панель интерфейса
		var pn = c.layout('panel','north');					// панель заголовка, крошек и тулбар
		
		pn.panel('resize', {height:'auto'});				// подогнать высоту
		var newHeight = pn.panel('panel').outerHeight();	// считать новую высоту
		pc.height(c.height() - newHeight);					// уменьшить высоту панели интерфейса
		
		pc.panel('move',{									// и сместить ее
			left: 0,
			top: newHeight
		});
	
		$('#ui-panel .easyui-panel:not(.tamur-tabs)').panel();							// пересчитать высоты в интерфейсе
	}

	pushAside() {
		let this_ = this;
		// метод создал Жаркын, чтобы при нажатии кнопки(рядом с входящими), входящие открылись на полную
		let language = "ru";
		$('#topLangKz').hasClass("selected") ? language = "kz" : language = "ru";
		let hint = language === "kz" ? "Кеңейту" : "Расширить";
		let hint2 = language === "kz" ? "Кішірейту" : "Уменьшить";

		let centerPanel = $("#orders").parent().parent().parent(),
			bigWidth = centerPanel.parent().width(), //1200px
			centerPanelWidth = centerPanel.width(), // 550px
			firstChild = centerPanel.children(":first"),
			neighborAbove = centerPanel.prev(),
			neighborBelow = centerPanel.next();

		if(centerPanel.position().left !== 0) {
			this.centerPanelLeft = centerPanel.position().left;
			this.centerPanelWidth = centerPanelWidth;
		}
		
		let l = this.centerPanelLeft,
			w = this.centerPanelWidth,
			second = 500;
	
		let bigOrLittle = centerPanel.position().left == 0 ? true : false;

		if(bigOrLittle) {
			centerPanel.fadeOut(second, function() {
				firstChild.width(w);
				$(this).width(w);
				$(this).css({"left": `${l}px`});
				$(this).fadeIn(second);
				neighborAbove.fadeIn(second);
				neighborBelow.fadeIn(second);	

				if(!$(this).hasClass("panel")) {
					$(this).addClass("panel");
				}
				$(".tasksFullWidth").attr('title', hint).html("&hoarr;");
			});

			this_.loadPortletContent("MainPage", "otherMethods", {
				"arg1": "setPoruchWidth",
				"arg2":"0"
			})

		} else {
			neighborAbove.fadeOut(second);
			neighborBelow.fadeOut(second);

			centerPanel.fadeOut(second, function() {
				firstChild.width(bigWidth);
				$(this).width(bigWidth);
				$(this).css({"left": "0px"});
				$(this).fadeIn(second);

				if($(this).hasClass("panel")) {
					$(this).removeClass("panel");
				}

				$(".tasksFullWidth").attr('title', hint2).html("&roarr;");
			});

			this_.loadPortletContent("MainPage", "otherMethods", {
				"arg1": "setPoruchWidth",
				"arg2":"1"
			});
		}
	}

	hidePopUpPanContent() {
		$(".popUpPanContent").each(function() {
			if($(this).css("display") && $(this).css("display") != "none") {
				$(this).fadeOut();
			}
		})
	}
}

/***/ }),

/***/ "./monitor.js":
/*!********************!*\
  !*** ./monitor.js ***!
  \********************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   "Monitor": () => (/* binding */ Monitor)
/* harmony export */ });
/* harmony import */ var _util_js__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./util.js */ "./util.js");
/* harmony import */ var _translation_js__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./translation.js */ "./translation.js");



class Monitor {
	
	states = ["state-green", "state-yellow", "state-red"];
	statesColor = ["#468847", "#F89406", "#E05540"];
	monitorPageSize = 100;
	currentPageNumber = 1;
	total = 0;
	
	monitorSearch = 0;
	tasksLoaded = false;
	
	constructor(app) {
		this.app = app;
	}
	
	init() {
		let this_ = this;
		
		this.loadTasksCount().then(data => {
			this_.loadTasksContent();
		});
				
		$("body").on('click', '.task-line .task-title', function() {
			let jThis = $(this);
			let op = jThis.attr('op');
			
			if (op == "1")
				document.location.hash = "#cmd=openTask&mnt=1&uid=" + jThis.attr('uid');
			else if (op == "2")
				_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.alert(_translation_js__WEBPACK_IMPORTED_MODULE_1__.Translation.translation.procPerformedMessage, _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.WARNING);
			else if (op == "3")
				_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.alert(_translation_js__WEBPACK_IMPORTED_MODULE_1__.Translation.translation.rptGenerateMessage, _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.WARNING);
			else
				_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.alert(_translation_js__WEBPACK_IMPORTED_MODULE_1__.Translation.translation.ifcNotExistMessage, _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.WARNING);
			
		});

		$("body").on('click', '.task-line .icon-close', function() {
			let task = $(this).parent();
			this_.killTask(task.attr('id'));
		});
		
		$("#monitor-panel").on('keyup', "#monitor-search-text", function(event) {
			if(event.keyCode == 13){
				this_.loadTasksContent(true);
			}
		});
	}
	
	loadTasksContent() {
		let this_ = this;
	
		$('#monitor-pager').pagination({
		    pageSize: this_.monitorPageSize,
		    total: this_.total,
		    pageNumber: 1,
		    pageList: [100, 200, 500, 1000],
			layout:['list','sep','first','prev','sep','links','sep','next','last','refresh','sep','manual'],
		    showPageList: true,
	
			onSelectPage:function(pageNumber, pageSize){
				_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.blockPage();
				$(this).pagination('loading');
				
				this_.currentPageNumber = pageNumber;
				this_.monitorPageSize = pageSize;
				
				this_.loadTasks();
				
				$(this).pagination('loaded');
			}
		});
		
		$('#monitor-pager').pagination('select');
		this_.tasksLoaded = true;
	}
		
	loadTasksCount() {
		let _this = this; 
		return new Promise((resolve, reject) => {
			_this.app.post({cmd: "getTasksCount"}).then(data => {
				_this.setTasksCount(data.message);
				resolve(data);
			});	
		});
	}
	
	setTasksCount(count) {
		this.total = count;
		$('#monitor-total-counter').text(count);
	
		if (this.tasksLoaded) {
			$('#monitor-pager').pagination('refresh', {
				total: count
			});
		}
	}	
	
	loadTasks() {
		let this_ = this;
		
		var end = 0;
		if (this_.total == 0)
			end = this_.monitorPageSize;
		else if (this_.total <= this_.currentPageNumber * this_.monitorPageSize)
			end = this_.total
		else
			end = this_.currentPageNumber * this_.monitorPageSize;
		
				
		var par = {"cmd" : "loadTasks", "rowFirst": (this_.currentPageNumber-1) * this_.monitorPageSize, 
			"rowLast": end};
			
		var searchText = $('#monitor-search-text').val();
		if (searchText && searchText.length > 0) {
			par["searchText"] = searchText;
		}

		this.app.post(par).then(data => {

			this_.setTasksCount(data.tasksCount);

			var processList = data.processes;
			processList.reverse();
			
			$('#monitor-panel .monitor-table .pcontent').empty();
			
			$.each(processList, function(i, o) {
				$.each(o, function(key, task) {
					$('#monitor-panel .monitor-table .pcontent').append(this_.makeTask(key, task));
				});
			});
			
			$('body').unblock();
		});
	}
	
	makeTask(key, task) {
		let this_ = this;
		var orderDiv = $("<div class='task-line'></div>").attr('id', key);

		orderDiv.append('<div class="task-condition ' + this.states[task.c2] + '"></div>');
		
		var col1Div = $("<div class='task-col'></div>");
		var authorDiv = $("<div class='task-author'></div>").text(task.i);
		var dateDiv = $("<div class='task-date'></div>").text(task.d);
		col1Div.append(authorDiv).append(dateDiv);
		
		var col2Div = $("<div class='task-col'></div>");
		var pretitleDiv = $("<div class='task-pretitle'></div>").text(task.t);
		var titleDiv = $("<div class='task-title'></div>").text(task.p)
			.attr('uid', key).attr('op', task.op).css({"color" : this_.statesColor[task.c2]});
		var descDiv = $("<div class='task-desc'></div>").text(task.o);
		col2Div.append(pretitleDiv).append(titleDiv).append(descDiv);
			
		orderDiv.append(col1Div);
		orderDiv.append('<div class="task-delimeter"></div>');
		orderDiv.append(col2Div);
		
		if (task.k == 1) {
			var closeDiv = $('<i class="icon icon-close"></i>');
			orderDiv.append(closeDiv);
		}
		
		return orderDiv;
	}
	
	killTask(flowId) {
		_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.blockPage(_translation_js__WEBPACK_IMPORTED_MODULE_1__.Translation.translation.deleting);
		var par = {cmd: "kill", id: flowId};
		this.app.query(par).then(() => {
//			$('body').unblock();
		});
	}
	
	deleteTask(flowId) {
		let _this = this;
		if (this.total > -1) {
			$('#monitor-panel .monitor-table .pcontent #' + flowId).remove();
			$('body').unblock();
			
			_this.setTasksCount(_this.total - 1);
		}
	}
	
	addTask(process) {
		let _this = this; 
		if (this.total > -1) {
			var oldCount = this.total;
			$.each(process, function(key, task) {
				$('#monitor-panel .monitor-table .pcontent').prepend(_this.makeTask(key, task));
				oldCount++;
			});
			this.setTasksCount(oldCount);
		}
		var search = $('#monitor-search-text');
		if (search && search.val() && search.val().length > 0) { 
			_this.filterBySearchText(search.val());
		}
	}
	
	updateTask(process) {
		let _this = this;
		$.each(process, function(key, task) {
			var html = _this.makeTask(key, task);
			if ($('#monitor-panel .monitor-table .pcontent .task-line[id="' + key + '"]').length > 0)
				$('#monitor-panel .monitor-table .pcontent .task-line[id="' + key + '"]').replaceWith(html);
		});
		var search = $('#monitor-search-text');
		if (search && search.val() && search.val().length > 0) { 
			_this.filterBySearchText(search.val());
		}
	}
	
	filterBySearchText(search){
		let all = $('#monitor-panel .monitor-table .pcontent').find('.task-line').length;
		all.show();
		var a = all.length;
		
		if (search && search.length > 0){
			let toHide = $('#monitor-panel .monitor-table .pcontent').find('.task-line').not(":containsNoCase('" + search + "')");
			toHide.hide();
			var b = toHide.length;
			$('#monitor-total-counter').text(search +': '+(a-b+'/'+a));
		} else {
			$('#monitor-total-counter').text(tasksCount);
		}
	}
}


/***/ }),

/***/ "./navigation.js":
/*!***********************!*\
  !*** ./navigation.js ***!
  \***********************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   "Navigation": () => (/* binding */ Navigation)
/* harmony export */ });
/* harmony import */ var _util_js__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./util.js */ "./util.js");
/* harmony import */ var _translation_js__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./translation.js */ "./translation.js");



class Navigation {
	activeMenuId = 'main-page';
	
	constructor(app) {
		this.prevHash = "";
		this.app = app;
		
		$(".profile-tab").on('click', function() {
			var tab_id = $(this).attr("tab");
			document.location.hash = "#select=profile&profileTab="+tab_id;
		});

	}
	
	hashChanged(hash) {
		console.log('hash changed from ' + this.prevHash + ' to ' + hash);
		if (this.prevHash != hash) {
			this.prevHash = hash;
			if (hash == "")
				return false;
			
			this.loadUI(encodeURI(hash));
		}
	}
	
	loadUI(hash) {
		var params = _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.parseHash(hash);
		console.log("params = " + JSON.stringify(params));
		
		if (params.cmd == 'exit') {
			this.app.logout();
			return;
		}
		
		if (params.select !== null && params.select !== undefined && params.select.length > 0) {
			this.activeMenuId = params.select;
			$('.top-view-panel[id != "' + this.activeMenuId + '-panel"]').hide();
			$('#' + this.activeMenuId + '-panel').show();
			
	        $('.left-menu li.selected').removeClass('selected');
        	$('#menu-' + this.activeMenuId).parent().addClass("selected");

			$('#' + this.activeMenuId + '-panel').panel();
			this.app.resize('#' + this.activeMenuId + '-panel');
			
			console.log("show #" + this.activeMenuId + '-panel');
		}

		if (params.profileTab !== null && params.profileTab !== undefined && params.profileTab.length > 0) {
			$('.profile-body').hide();
			$("#profile-panel-"+params.profileTab).show();
	
			$('.profile-tab').removeClass("active");
			$('.profile-tab[tab="' + params.profileTab + '"]').addClass("active");
			
			console.log("show #profile-panel-" + params.profileTab);
		}
		
		if (params.orders) {
			this.showOrders(params.orders);
		} else if (params.cmd == 'openTask') {
			_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.blockPage();
			var url = _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.makeUrl(params);
			$('#ui-body').attr('init',"1");
			this.openTask(params.uid, url);
		} else if (params.cmd !== null && params.cmd !== undefined) {
			_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.blockPage();
			$('.top-view-panel').hide();
			$('#ui-body').attr('init',"1");
			$('#ui-panel').show();
			$('#ui-title').show();
			$('.ui-toolbar').show();
			$('#ui-breadcrump').show();
			$("#nextBtn").hide();
			
			this.app.resize('#ui-panel');
			
			var url = _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.makeUrl(params);
			$('#ui-body').panel('refresh', url);
			this.app.resizeHeight();
	        //clearTimeout(refresher);
		}
		$('#ui-panel .easyui-panel:not(.tamur-tabs)').panel();
	}

	showOrders(type) {
		// отменить выделение предыдущего типа поручений
		$('.order-type-selected').removeClass('order-type-selected');
		// выделяем нужный тип
		if (type === 'all')
			$('.order-type:not([code])').addClass(('order-type-selected'));
		else
			$('.order-type[code=' + type + ']').addClass(('order-type-selected'));
		// скрываем/отображаем поручения нужного типа
		if (type === 'all') {
			//$('.order-block').show();
		} else {
			$('.order-blocks[code=' + type + ']').show();
			$('.order-blocks[code!=' + type + ']').hide();
			
		}
	}

	openOrder(iter, proc, uid, startIfNotExists) {
		let _this = this;
		_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.blockPage();
		
		if (iter == null || iter == undefined || iter == "")
			document.location.hash = "cmd=openOrder&iuid=" + proc + "&ouid=" + uid;
		
		else {
			let par = {"uid": proc, "obj": iter, "cmd": "openProcess"};
					
			this.app.query(par).then(response => {
				if (response.status === 200) {
					response.json().then(data => {
				        if (data.result == "success" || data.acts == null || data.acts > 0 || (data.message != null && !startIfNotExists)) {
				     		_this.showProcessUI(data);
				        } else if (startIfNotExists) {
							par.cmd = "startProcess";
							par.obj = uid;
							_this.app.query(par).then(response => {
								if (response.status === 200) {
									response.json().then(data => {
										if (data.infMsg) {
											_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.alert(data.infMsg);
										}
										_this.showProcessUI(data);
									});
								}
							});				
				        } else if(data.result == "error" && data.message == null){
	        				$('body').unblock();
	            			_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.alert(_translation_js__WEBPACK_IMPORTED_MODULE_1__.Translation.translation.error, _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.ERROR);
						}
					});
				}
			});
		}
	}
	
	showProcessUI(data) {
		let _this = this;
		let ifc = this.app.ifcController;
		
		if (data.result == "success") {
			if (data.mode && data.mode=="dialog") {
				ifc.openStartDialog(data.uid);
			} else if (data.mode && data.mode=="no") {
				_this.toActiveMain();
				$('body').unblock();
			} else if (data.uid) {
				if (document.location.hash.indexOf("cmd=openTask&uid=" + data.uid) == -1)
					document.location.hash = "cmd=openTask&uid=" + data.uid + "&rnd=" + _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.rnd();
			} else {
				_this.toActiveMain();
				$('body').unblock();
			}
		} else {
			var message = data.message;
			var unblock = true;
			// здесь код поменял Жаркын, чтобы узнать на каком языке показывать текст =========
			let myMessage = " Открыть раннее запущенный процесс?";
			if($('#topLangKz').hasClass("selected")) {
				myMessage = " Бұрын іске қосылған процесті ашу?";
			} else {
				myMessage = " Открыть раннее запущенный процесс?";
			}
			if (message) {
				var flowIdText = "ID потока: ";
				var index = message.indexOf(flowIdText);
				if (index > 0) {
					var flowId = message.substring(index + flowIdText.length, message.length - 1);
					message = message.substring(0, index) + myMessage;
					$.messager.confirm('', message, function(e) {
						if (e) {
							document.location.hash = "cmd=openTask&uid=" + flowId + "&rnd=" + _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.rnd() + "&isPrevProc=1";
							unblock = false;
						}
					}); 
				} else {
					alert(message);
				}
			} else {
				_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.alert('error', _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.ERROR);
			}
			if (unblock)
				$('body').unblock();
		}
	}
	
	openTask(uid, url) {
		let par = {"uid": uid, "cmd": "taskType", "json": 1, "rnd": _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.rnd()};
		let nav = this;
		let ifc = this.app.ifcController;
			
		this.app.query(par).then(response => {
			if (response.status === 200) {
				response.json().then(data => {
					if (data.type == "report" || data.type == "fastreport") {
						$('#report_frame').attr('src', url);
						$('body').unblock();
					} else if (data.type == "htmlreport") {
						$('body').unblock();
						window.open(url);
					} else if (data.type == "undefined") {
						_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.delay(3000).then(() => nav.openTask(uid, url));
					} else if (data.type == "choose") {
						ifc.openStartDialog(uid);
					} else if (data.type == "dialog") {
						ifc.openStartDialog(uid);
					} else if (data.type == "option") {
						var par = {};
						par["cmd"] = "openTask";
						par["uid"] = uid;
						nav.app.query(par).then(response => {
							response.json().then(data => {
								if (data.message) {
									_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.alert(data.message, _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.INFO);
								}
							});
						});
					} else if (data.type == "error") {
			            _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.alert(data.msg, _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.ERROR);
						nav.toActiveMain();
						$('body').unblock();
					} else if (data.type == "noop") {
						nav.toActiveMain();
						$('body').unblock();
					} else {
						$('.top-view-panel').hide();
						$('#ui-panel').show();
						$('#ui-title').show();
						$('.ui-toolbar').show();
						$('#ui-breadcrump').show();
						$("#nextBtn").show();
						$("#nextBtn").addClass("active");

						$('#ui-body').panel('refresh', url);
						nav.app.resizeHeight();
						$('.easyui-panel:not(.tamur-tabs)').panel();
					}
				});
			}
		});
	}
	
	toActiveMain() {
		let a = $('#menu-' + this.activeMenuId);
		document.location.hash = a.attr('href');
		//a.click();
	}

	startProcess(pid) {
		let nav = this;
		
		_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.blockPage();
		
		let par = {"uid": pid, "cmd": "startProcess"};
				
		this.app.query(par).then(response => {
			if (response.status === 200) {
				response.json().then(data => {
					if (data.infMsg) {
						_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.alert(data.infMsg);
			   	 	}
					nav.showProcessUI(data);
				});
			}
		});
	}
	
	openArchive(pid) {
		$("#nextBtn").hide();
		document.location.hash = "cmd=openArch&uid=" + pid;
	}
	
	openDict(pid) {
		$("#nextBtn").hide();
		document.location.hash = "cmd=openDict&uid=" + pid;
	}
	
	openIfcWithObj(ui, obj) {
		$("#nextBtn").hide();
		document.location.hash = "cmd=openTaskIntf&intUid=" + ui + "&objUid=" + obj;
	}

	cancelStart(uid) {
		var par = {};
		par["cmd"] = "cancelProcess";
		par["uid"] = uid;
		this.app.query(par);
	}
	
	stepSelected(ignore, did) {
		var steps = document.getElementsByName("step");
		var transitionId;
		for (var i = 0; i < steps.length; i++) {
			if (steps[i].checked)
				transitionId = steps[i].value;
		}
		this.nextStep(ignore, did, transitionId);
		$('#stepSelectionDlg').dialog("close");
		$('body').unblock();
	}
	
	stepSelectionCanceled() {
		$('#stepSelectionDlg').dialog("close");
		$('body').unblock();
	}
	
	nextStep(ignore, did, transitionId) {
		let _this = this;
		let ifc = this.app.ifcController;
		let nav = this.app.nav;
		
		ifc.endEditing();
		if (ignore || $("#nextBtn").hasClass("active")) {
			if (transitionId || ignore != undefined || did != undefined || confirm(_translation_js__WEBPACK_IMPORTED_MODULE_1__.Translation.translation.askNextStep)) {
				var par = {};
				par["cmd"] = "nextStep";
				if (transitionId) {
					par["transitionId"] = transitionId;
				}
				_this.app.post(par).then(data => {
					if (data.result == "success") {
						ifc.hideChangeMsg();
						if (did == null)
							nav.toActiveMain();
						if (data.message) {
							_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.alert(data.message);
						}
						if (did)
							$('#' + did).dialog('destroy');
						$('body').unblock();
					} else if (data.result == "selectStep") {
						// Показать диалог с выбором шага
						var dialogDiv = document.getElementById('stepSelectionDlg');
						if (dialogDiv != null) {
							dialogDiv.parentNode.removeChild(dialogDiv);
						}
						dialogDiv = $('<div />', {'class': 'easyui-dialog', 'id': 'stepSelectionDlg'});
						var html = '<div style="padding: 20px">';
						for (var i = 0; i < data.transitions.length; i++) {
							html += '<input style="margin: 0px 10px ' + (i < data.transitions.length - 1 ? '10px' : '0px') + ' 0px;" type="radio" name="step" id="step_' + i + '" value="' + data.transitions[i].value + '"' + (i == 0 ? ' checked' : '') + '/>';
							html += '<label for="step_' + i + '">' + data.transitions[i].title + '</label>' + (i < data.transitions.length - 1 ? '<br>' : '');
						}
						html += '</div>';
						dialogDiv.html(html);
						dialogDiv.dialog({
							title:'Выбор шага',
							closed: true,
							draggable: false,
							resizable: false,
							closeOnEscape: false,
							modal:true,
							closable: false,
							buttons:[{
								text:'Принять',
								handler:function(){_this.stepSelected(ignore, did);}
							}, {
								text:'Отмена',
								handler:function(){_this.stepSelectionCanceled();}
							}]
						});
						$('#stepSelectionDlg').dialog("open");
					} else {
						if (data.message) {
							_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.alert(data.message);
						}
						if (did)
							ifc.setDialogBtnsEnabled(did, true);
						$('body').unblock();
					}
				}, 'json');
			}
		}
	}
	
	showStack(stack, container) {}
}


/***/ }),

/***/ "./nca.js":
/*!****************!*\
  !*** ./nca.js ***!
  \****************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   "NCA": () => (/* binding */ NCA)
/* harmony export */ });
/* harmony import */ var _util_js__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./util.js */ "./util.js");


class NCA {
	
	static APPLET = "kz.gov.pki.knca.applet.Applet";
	static COMMON = "kz.gov.pki.knca.commonUtils";
	static BASICS = "kz.gov.pki.knca.basics";
	
	static AUTH = 'AUTHENTICATION';
	static SIGN = 'SIGNATURE'; 
	
	tokenStoresMap = {
		'AKEToken5110Store': 'eToken',
		'AKKaztokenStore': 'Казтокен',
		'PKCS12': 'Персональный компьютер',
		'AKKZIDCardStore': 'Удостоверение личности',
	}; 
	
	webSocket = null;
	onMessage = null;
	onError = null;
	
	constructor() {
	}
	
	init() {
		let _this = this;
		
		if (_this.webSocket && _this.webSocket.readyState < 2) {
	        console.log("reusing the socket, state = " + _this.webSocket.readyState);
	        return Promise.resolve();
	    }
		
		return new Promise((resolve, reject)  => {
			_this.webSocket = new WebSocket('wss://127.0.0.1:13579/');
		
			_this.webSocket.onopen = function() {
				resolve();
			};
		
			_this.webSocket.onclose = function(event) {
				_this.webSocket = null;
				console.log("connection closed");
				console.log('Code: ' + event.code + ' Reason: ' + event.reason);
			};
		
			_this.webSocket.onmessage = function(event) {
				var dataStr = event.data;
				var data = JSON.parse(dataStr);
				console.log("event.data: " + data);
				
				if (data && data.result && data.result.version)
					return;
				
				if (_this.onMessage) {
					_this.onMessage(data);
				}
			};
			
			_this.webSocket.onerror = function(error) {
				reject(error);
			}
		});
	}
	
	showError() {
		if (mode == 0) {
			this.showErrorOnLoginPage("Ошибка соединения с NCALayer!");
		} else if (mode == 1 || mode == 2) {
			_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.alert('Ошибка соединения с NCALayer');
		}
		this.resetOperation(true);
	}
	
	sendToNCALayer(module, method, args) {
		let _this = this;
		
		return new Promise((resolve, reject) => {
			
			_this.init().then(() => {
				_this.onMessage = resolve;	
				console.log("sendToNCALayer: " + module + ", method: " + method + ", args: " + args);
			
				var json = {'module' : module, 'method' : method, 'args' : args};
				_this.webSocket.send(JSON.stringify(json));
			}, err => {
				console.error("socket error : ", err);
				reject("Ошибка соединения с NCALayer!");
			});
		}); 
	}
	
	chooseStorage(data) {
		let _this = this;
		if (data.responseObject) {
			if (data.responseObject.length == 0)
				return Promise.resolve('PKCS12');
			else
				return _this.showStorageSelectionDialog(data.responseObject);
		} else {
			return Promise.reject("Ошибка при получении доступных хранилищ ключей");
		}
	}
	
	getActiveTokens(module) {
		return this.sendToNCALayer(module, "getActiveTokens", []);
	}
	
	signXml(module, ecpType, xml) {
		let _this = this;

		if (module === NCA.COMMON) {
			return new Promise((resolve, reject) => {
				_this.getActiveTokens(module).then(data => {
					_this.chooseStorage(data).then(alias => {
						let args = [ alias, ecpType, xml, "", "" ];
						_this.sendToNCALayer(module, "signXml", args).then(resolve, reject);
					}, reject);
				}, reject);
			});
		} else if (module === NCA.BASICS) {
			return new Promise((resolve, reject) => {
				let args = {
					allowedStorages: "AKKaztokenStore,AKKZIDCardStore,AKEToken72KStore,AKJaCartaStore,PKCS12,JKS,AKAKEYStore,AKEToken5110Store,ABSIMCardStore,ABCloudServiceStore",
					format: "xml",
					data: xml,
					signingParams: {
						decode: false,
						digested: false,
						encapsulate: false,
						tsaProfile: null
					},
					signerParams: {
						// Проверка подлинности клиента (1.3.6.1.5.5.7.3.2) авторизация
						// Защищенная электронная почта (1.3.6.1.5.5.7.3.4) подписть
						extKeyUsageOids: ["1.3.6.1.5.5.7.3.2"] 
					},
					locale: "ru"
				};
				_this.sendToNCALayer(module, "sign", args).then(data => {
					if (data.status === true) {
						let response = (data.body.result) 
							?
						{
							responseObject: data.body.result[0]
						}
							:
						{
							message: 'action.canceled'
						};
						
						resolve(response);
					} else if (data.status === false) {
						let response = {
							code: data.code,
							message: data.message + ' (' + data.details + ')'
						};
						resolve(response);
					} else {
						reject('Неизвестная ошибка NCALayer!');
					}
				}, reject);
			});
		} else
			return Promise.reject("Неизвестный плагин NCALayer"); 
	}
	
	showStorageSelectionDialog(conteiners) {
		let _this = this;
	
		var options = '<option value="" disabled selected hidden>Выберите хранилище</option>' +
						'<option value="PKCS12">' + this.tokenStoresMap['PKCS12'] +'</option>';
		
		for (var ind = 0; ind < conteiners.length; ind++) {
			var contId  = conteiners[ind];
			options += '<option value="' + contId + '">' + this.tokenStoresMap[contId] + '</option>';
		}
		
		return new Promise((resolve, reject) => {
	
			var dialogDiv = $('<div />', {'class': 'easyui-dialog', 'id': 'storageInputDlg'}); 
			dialogDiv.html('<div style="padding: 20px">' +
					'<select id="keyStore1">' +
						options +
					'</select></div>');
			
			dialogDiv.find('#keyStore1').keyup(function(e) {
				if (e.which && e.which == 13)
					$('#storageInputDlgOkBtn').click();
			}).change(function (e) {
				$("#storageInputDlgOkBtn").linkbutton("enable");
			});
			
			dialogDiv.dialog({
				title:'Выбор хранилища ключей',
				closed: true,
				draggable: false,
				resizable: false,
				closeOnEscape: false,
				modal:true,
				closable: false,
				buttons:[
					{
						text:'Принять',
						id: 'storageInputDlgOkBtn',
						handler:function(){
							let storageAlias = $("#keyStore1 option:selected").val();
							$("#storageInputDlg").dialog('destroy');
							resolve(storageAlias);
						}
					}, 
					{
						text:'Отмена',
						handler:function(){
							$("#storageInputDlg").dialog('destroy');
							reject("Выбор хранилища отменен");
						}
					}
				],
				onOpen: function() {
					$('#keyStore1').focus();
				},
				onClose: function() {
					$("#storageInputDlg").dialog('destroy');
					reject("Окно выбора хранилища закрыто");
				}
			});
		
			$('body').unblock();
			var e = document.getElementById("keyStore1");
			if (e.options[e.selectedIndex].value.length == 0) {
				$("#storageInputDlgOkBtn").linkbutton("disable");
			} else {
				$("#storageInputDlgOkBtn").linkbutton("enable");
			}
			$('#storageInputDlg').dialog("open");
		});
	}
}


/***/ }),

/***/ "./notifications.js":
/*!**************************!*\
  !*** ./notifications.js ***!
  \**************************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   "Notifications": () => (/* binding */ Notifications)
/* harmony export */ });
/* harmony import */ var _util_js__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./util.js */ "./util.js");


class Notifications {
	
	orderStatus = ["label-success", "label-warning", "label-important", "label-info"];
	notificationPageSize = 50;
	currentPageNumber = 1;
	total = 0;
	
	sortColumn = "sortInDate_asc"
	statusReadingNotification = 0;
	dateParseN1 = 0;
	dateParseN2 = 0;
	notificationSearch = 0;
	
	constructor(app) {
		this.app = app;
	}
	
	init() {
		let this_ = this;
		
		$('#notifications-pager').pagination({
		    pageSize: this_.notificationPageSize,
		    pageNumber: 1,
		    pageList: [10, 50, 100, 150, 200],
		    layout: ['list','sep','first','prev','sep','links','sep','next','last','sep','manual'],
		    showPageList: true,

			onSelectPage:function(pageNumber, pageSize){
				_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.blockPage();
				$(this).pagination('loading');
				
				this_.currentPageNumber = pageNumber;
				this_.notificationPageSize = pageSize;
				
				this_.loadNotifications();
				
				$(this).pagination('loaded');
			}
		});
		
		this_.loadNotifications();
		
		$("body").on('click', '.notification-line .notification-title', function() {
			let jThis = $(this);
			this_.app.nav.openOrder(jThis.attr('iter'), jThis.attr('proc'), jThis.attr('uid'), jThis.attr('type') !== 'my');
		});
		
	}
	
	loadNotifications() {
		// если доступно меню Уведомления пользователю
		if ($('#notifications-select-type').length > 0) {
			try {
				_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.blockPage();
				let this_ = this;
				
				let index = parseInt($('#notifications-select-type').val());
				this_.statusReadingNotification = index;
		
				let dateN1 = $("#dateN1").datebox("getValue");
				this_.dateParseN1 = dateN1 != "" ? dateN1 + " 00:00:00" : 0;
				let dateN2 = $("#dateN2").datebox("getValue");
				this_.dateParseN2 = dateN2 != "" ? dateN2 + " 00:00:00" : 0;
		
				this_.notificationSearch = $('#notifications-search-text').val().length > 0
											? $('#notifications-search-text').val() : "0";
			
				var end = 0;
				if (this_.total == 0)
					end = this_.notificationPageSize;
				else if (this_.total <= this_.currentPageNumber * this_.notificationPageSize)
					end = this_.total
				else
					end = this_.currentPageNumber * this_.notificationPageSize;
		
				var par = {"sfunc" : 1, "cls": "XmlUtil", "name": "getNewNotificationOrders", 
					"arg0": 1, "arg1": (this_.currentPageNumber-1) * this_.notificationPageSize, "arg2": end,
					"arg3": this_.dateParseN1, "arg4": this_.dateParseN2, 
					"arg5": this_.notificationSearch, "arg6": this_.statusReadingNotification, 
					"arg7": this_.sortColumn};
			
				this.app.query(par).then(response => {
					if (response.status === 200) {
						response.json().then(json => {
							_util_js__WEBPACK_IMPORTED_MODULE_0__.DataChecker.checkData(json).then(data => {
								if (data.totalUnread > 0)
									$('#notifications-unread-counter').text(data.totalUnread);
									
								$('#notifications-pager').pagination({
									total: data.totalFilter ? data.totalFilter : data.total,
								});
								
								this_.total = data.total;
								
								$('#notifications-total-counter').text((data.totalFilter ? (data.totalFilter + '/') : '') + data.total);
								
								$('#notifications-panel .notifications-table .pcontent').empty();
								
								if (data.rows) {
									$.each(data.rows, function(i, o) {
										$.each(o, function(key, order) {
											$('#notifications-panel .notifications-table .pcontent').append(this_.makeNotification(key, order));
										});
									});
								}						
								$('body').unblock();
							});
						});
					}
				});
			} catch(e) {
				$('body').unblock();
				console.log("упал на notifications " + e)
			}
		}
	}
	
	makeNotification(key, order) {
		var orderDiv = $("<div class='notification-line'></div>").attr('id', key);
		if (order.openDate == "")
			orderDiv.addClass('notification-unread');
								
		var titleDiv = $("<div class='notification-title'></div>").html(order.title)
			.attr('iter', order.iter).attr('proc', order.proc).attr('uid', key).attr('type', 'notif');;
		
		var authorDiv = $("<div class='notification-author'></div>").text(order.from);
		var date1Div = $("<div class='notification-date'></div>").text(order.inDate);
		var date2Div = $("<div class='notification-date'></div>").text(order.openDate);
		var date3Div = $("<div class='notification-date'></div>").text(order.awereDate);
		
		orderDiv.append(titleDiv).append(authorDiv).append(date1Div).append(date2Div).append(date3Div);
		
		return orderDiv;
		//var func = order.iter == '' ? "href='javascript:openOrder(\""+order.proc+"\",\""+key+"\")'" : "href='javascript:startOrderIn(\""+order.iter+"\",\""+order.proc+"\",\""+key+"\")'";
	}
	
	deleteNotification(byType) {
		let _this = this;
		$.each(byType, function(type, o) {
			if (type == "notif") {
				$.each(o, function(i, uid) {
					let existOrderDiv = $('#notifications-panel .notification-line[id="' + uid + '"]');
					let isImportant = existOrderDiv.hasClass('notification-unread');
					existOrderDiv.remove();
					
					var tmp = $('#notifications-total-counter').text();
					var oldCount = (tmp.length > 0) ? parseInt(tmp) : 0;
					$('#notifications-total-counter').text(oldCount - 1);
					
					if (isImportant) {
						tmp = $('#notifications-unread-counter').text();
						oldCount = (tmp.length > 0) ? parseInt(tmp) : 0;
						if (oldCount == 1) {
							$('#notifications-unread-counter').hide();
						} else {
							$('#notifications-unread-counter').show().text(oldCount - 1);
						}
					}
				});
			}
		});
	}
	
	updateNotification(byType) {
		let _this = this;
		$.each(byType, function(type, o) {
			if (type == "notif") {
				$.each(o, function(i, uid) {
					let par = {"sfunc" : 1, "cls": "XmlUtil", "name": "getNewNotificationOrders", "arg0": uid};
					_this.app.post(par).then(data => {
						if (data.rows) {
							$.each(data.rows, function(type1, o1) {
								$.each(o1, function(key, order) {
									var delta = 0;
									let orderDiv = _this.makeNotification(key, order);
									delta += (orderDiv.hasClass('notification-unread')) ? 1 : 0;
									let existOrderDiv = $('#notifications-panel .notification-line[id="' + key + '"]');
									delta -= (existOrderDiv.hasClass('notification-unread')) ? 1 : 0;
									
									if (existOrderDiv.length > 0) {
										existOrderDiv.replaceWith(orderDiv);
									} else {
										var firstRow = $('#notifications-panel .notification-line:first');
										if (firstRow.length > 0)
											firstRow.before(orderDiv);
										else {
											$('#notifications-panel .notifications-table .pcontent').append(orderDiv);
										}
										
										var tmp = $('#notifications-total-counter').text();
										var oldCount = (tmp.length > 0) ? parseInt(tmp) : 0;
										$('#notifications-total-counter').text(oldCount + 1);
									}
									
									if (delta != 0) {
										var tmp = $('#notifications-unread-counter').text();
										var oldCount = (tmp.length > 0) ? parseInt(tmp) : 0;
										if (oldCount == 1) {
											$('#notifications-unread-counter').hide();
										} else {
											$('#notifications-unread-counter').show().text(oldCount + delta);
										}
									}
													
								});
							});
						}
					});
				});
			}
		});
	}
	
	notificationsProcessing(json) {
		console.log("notificationsProcessing = " + json);
	}
	
	deleteNotifications(json) {
		console.log("deleteNotifications = " + json);
	}
	
	loadNotifications2() {
		this.currentPageNumber = 1;
		$('#notifications-pager').pagination('refresh',{
			pageNumber: 1
		});
		this.loadNotifications();
	}

	cleanNotificationParams() {
		_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.blockPage();
		let this_ = this;
		let dateN1 = $("#dateN1").datebox("getValue"),
		    dateN2 = $("#dateN2").datebox("getValue");
		if (dateN1 != "" || dateN2 != "") {
			console.log("this3");
			$("#dateN1").datebox("clear");
			$("#dateN2").datebox("clear");
		}
		$('#notifications-select-type option').prop('selected', function() {
			console.log("this5");
			return this.defaultSelected;
		});
		$('#notifications-search-text').val("");
		this_.notificationSearch = 0;
		this_.statusReadingNotification = 0;
		this_.dateParseN1 = 0;
		this_.dateParseN2 = 0;
		this_.sortNotifColumn = "sortInDate_asc";
		// $('#toolNotif td a').removeClass('sortNotif asc desc');
		// $('#sortInDate').addClass('sortNotif').addClass('asc');
		this_.loadNotifications();
	}
}


/***/ }),

/***/ "./orders.js":
/*!*******************!*\
  !*** ./orders.js ***!
  \*******************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   "Orders": () => (/* binding */ Orders)
/* harmony export */ });
/* harmony import */ var _util_js__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./util.js */ "./util.js");
/* harmony import */ var _translation_js__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./translation.js */ "./translation.js");



class Orders {
	
	orderStatus = ["label-success", "label-warning", "label-important", "label-info"];

	constructor(app) {
		this.app = app;
	}
	
	init() {
		let this_ = this;
		$(".orders-tab").on('click', function() {
			var tab_id = $(this).attr("tab");
			this_.showTab(tab_id);
		});
		
		this_.loadOrdersMy();
		this_.loadOrdersIn();
		this_.loadOrdersOut();
		
		$("body").on('click', '.order-line .order-title', function() {
			let jThis = $(this);
			if (jThis.attr('type') === 'out')
				this_.app.nav.openOrder(null, jThis.attr('iuid'), jThis.attr('iter'), false);
			else
				this_.app.nav.openOrder(jThis.attr('iter'), jThis.attr('proc'), jThis.attr('uid'), jThis.attr('type') !== 'my');			
		});

		$("body").on('click', '.delete-order', function() {
			let jThis = $(this);
			var msg = _translation_js__WEBPACK_IMPORTED_MODULE_1__.Translation.translation.removeProcess;
			_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.confirmMessage(msg, function(e) {
				if (e) {
					let span = jThis.parent().find('span.title');
					this_.deleteOrder(span.attr('uid'), span.attr('iter'), span.attr('proc'));
				}
			});
		});
	}
	
	showTab(tabId) {
		let this_ = this;
		$('.orders-body').hide();
		$("#orders-panel-"+tabId).show();

		$('.orders-tab').removeClass("active");
		$('.orders-tab[tab="' + tabId + '"]').addClass("active");
		
		$("#orders-panel-"+tabId).find('.easyui-panel').panel();
	}
	
	loadOrdersMy() {
		this.loadOrders({"sfunc" : 1, "cls": "XmlUtil", "name": "getMyOrders", "arg0": 1}, 'my');
	}

	loadOrdersIn() {
		this.loadOrders({"sfunc" : 1, "cls": "XmlUtil", "name": "getOrders", "arg0": 1}, 'in');
	}

	loadOrdersOut() {
		this.loadOrders({"sfunc" : 1, "cls": "XmlUtil", "name": "getOrdersOut", "arg0": 1}, 'out');
	}

	loadOrders(par, tabId) {
		let this_ = this;
				
		this.app.post(par).then(data => {
			this_.showOrdersLater(data, tabId, 20);
		});
	}
	
	showOrdersLater(data, type, count) {
		let _this = this;
		var infire = 0;
		var found = $('#ordersList_' + type + '_count').length > 0 && $('#ordersList_' + type).length > 0;
		if (found) {
			$('#ordersList_' + type + '_count').text(data.total);

			if (data.rows) {
				$.each(data.rows, function(i, o) {
					$.each(o, function(key, order) {
						
						let orderDiv = _this.makeOrderDiv(order, key, type);
						//var stop = order.isStop=='1' ? "<td><input type='checkbox' class='my_check' key='" + key + "' task='" + order.proc + "' obj='"+order.iter+"'/></td>" : "";
						if (type == 'in' && order.status == 2) {
							infire++;
						}
						
						$('#ordersList_' + type).append(orderDiv);
					});
				});
			}
			
			//$('#ordersList_' + type).html(html);
			if (type == 'old') {
				$('#oldordersList_count').text(data.total);
				if(data.total == 0){
					$('#old_return').show();
				}
			}
			if (type == 'in') {
				if (infire > 0)
					$('#ordersList_in_fire_count').show().text(infire);
				else
					$('#ordersList_in_fire_count').hide();
			}
			return;
		} else if (count > 0) {
			count--;
			setTimeout(function(){this.showOrdersLater(data, type, count);}, 800);
		}
	}
	
	makeOrderDiv(order, key, type) {
		var orderDiv = $("<div uid='" + key + "' class='order-line'></div>");
						
		orderDiv.append('<div class="order-photo"><img src="'
			+ ((order.img && order.img.length > 0) ? order.img : 'css/img/empty-avatar-30.png')
			+ '" /></div>');
		
		var col1Div = $("<div class='order-col'></div>");
		var authorDiv = $("<div class='order-author'></div>").text(order.from);
		var dateDiv = $("<div class='order-date'></div>").text(order.inDate);
		col1Div.append(authorDiv).append(dateDiv);
		
		var titles = order.title.split(':');
		var col2Div = $("<div class='order-col'></div>");
		var pretitleDiv = $("<div class='order-pretitle'></div>").text(titles.length > 1 ? titles[0] : "");
		var titleDiv = $("<div class='order-title'></div>").text(titles.length > 1 ? titles[1] : titles[0])
			.attr('iter', order.iter).attr('proc', order.proc).attr('iuid', order.iuid).attr('uid', key).attr('type', type);
			
		if (type != 'my' && type !='old' && order.status == 2)
			titleDiv.addClass('order-important');
		col2Div.append(pretitleDiv).append(titleDiv);
		
		orderDiv.append(col1Div);
		orderDiv.append('<div class="order-delimeter"></div>');
		orderDiv.append(col2Div);
		if (order.ctrl) {
			var deadlineDiv = $('<div class="order-deadline"></div>').text(order.ctrl).addClass(this.orderStatus[order.status]).attr('title', 'Срок исполнения');
			orderDiv.append(deadlineDiv);
		}
		return orderDiv;
	}
	
	makeOrderBlock(order, key, type) {
		var orderDiv = $("<div uid='" + key + "' class='order-block' type='" + type + "'></div>");
		
		if (type == 'my')
			orderDiv.append('<i class="delete-order icon-close" task="' + order.proc + '"/>');
						
		var titles = order.title.split(':');
		var pretitleSpan = $("<span class='preTitle'></span>").text(titles.length > 1 ? titles[0] : "");

		var titleSpan = $("<span class='title'></span>").text(titles.length > 1 ? titles[1] : titles[0])
			.attr('iter', order.iter).attr('proc', order.proc).attr('iuid', order.iuid).attr('uid', key).attr('type', type);
		
		var rowDiv = $("<div class='content-row'></div>");
		rowDiv.append('<span class="content-image"><img src="'
			+ ((order.img && order.img.length > 0) ? order.img : 'css/img/empty-avatar-30.png')
			+ '" /></span>');
			
		var colDiv = $("<div class='content-column'></div>");
		colDiv.append("<span class='person'>От " + order.from + "</span>");
		colDiv.append("<span class='time'>" + order.inDate + "</span>");
		rowDiv.append(colDiv);
		rowDiv.append("<i class='icon-type icon-type-in'></i>");
		
		orderDiv.append(pretitleSpan);
		orderDiv.append(titleSpan);
		orderDiv.append(rowDiv);
		return orderDiv;
	}

	deleteOrders(byType) {
		let _this = this;
		$.each(byType, function(type, o) {
			if (type != "notif") {
				$.each(o, function(i, uid) {
					let existOrderDiv = $('#ordersList_' + type + ' .order-line[uid="' + uid + '"]');
					let isImportant = (type == 'in' && existOrderDiv.find('.label-important').length > 0);
					existOrderDiv.remove();
					
					var tmp = $('#ordersList_' + type + '_count').text();
					var oldCount = (tmp.length > 0) ? parseInt(tmp) : 0;
					$('#ordersList_' + type + '_count').text(oldCount - 1);
					
					if (isImportant) {
						tmp = $('#ordersList_in_fire_count').text();
						oldCount = (tmp.length > 0) ? parseInt(tmp) : 0;
						if (oldCount == 1) {
							$('#ordersList_in_fire_count').hide();
						} else {
							$('#ordersList_in_fire_count').show().text(oldCount - 1);
						}
					}
					
					
					let existOrderDiv2 = $('.orders-' + type + ' .order-block[uid="' + uid + '"]');
					existOrderDiv2.remove();
					
					tmp = $('.order-type[code=' + type + '] .order-count').text();
					oldCount = (tmp.length > 0) ? parseInt(tmp) : 0;
					$('.order-type[code=' + type + '] .order-count').text(oldCount - 1);
					
				});
			}
		});
	}
	
	updateOrders(byType) {
		let _this = this;
		$.each(byType, function(type, o) {
			if (type != "notif") {
				$.each(o, function(i, uid) {
					var method = (type == "in") ? "getOrders" : 
							 	(type == "out") ? "getOrdersOut" : 
				 			 	(type == "old") ? "getOldOrders" : "getMyOrders";
	
					let par = {"sfunc" : 1, "cls": "XmlUtil", "name": method, "arg0": uid};
					_this.app.post(par).then(data => {
						if (data.rows) {
							$.each(data.rows, function(type1, o1) {
								$.each(o1, function(key, order) {
									var delta = 0;
									let orderDiv = _this.makeOrderDiv(order, key, type);
									delta +=  (type == 'in' && orderDiv.find('.label-important').length > 0) ? 1 : 0;
									let existOrderDiv = $('#ordersList_' + type + ' .order-line[uid="' + key + '"]');
									delta -=  (type == 'in' && existOrderDiv.find('.label-important').length > 0) ? 1 : 0;
									
									if (existOrderDiv.length > 0) {
										existOrderDiv.replaceWith(orderDiv);
									} else {
										var firstRow = $('#ordersList_' + type + ' div:first');
										if (firstRow.length > 0)
											firstRow.before(orderDiv);
										else {
											$('#ordersList_' + type).append(orderDiv);
										}
										
										var tmp = $('#ordersList_' + type + '_count').text();
										var oldCount = (tmp.length > 0) ? parseInt(tmp) : 0;
										$('#ordersList_' + type + '_count').text(oldCount + 1);
									}
									
									if (delta != 0) {
										var tmp = $('#ordersList_in_fire_count').text();
										var oldCount = (tmp.length > 0) ? parseInt(tmp) : 0;
										if (oldCount == 1) {
											$('#ordersList_in_fire_count').hide();
										} else {
											$('#ordersList_in_fire_count').show().text(oldCount + delta);
										}
									}
									
									
									let orderDiv2 = _this.makeOrderBlock(order, key, type);
									let existOrderDiv2 = $('.orders-' + type + ' .order-block[uid="' + key + '"]');
									
									if (existOrderDiv2.length > 0) {
										existOrderDiv2.replaceWith(orderDiv2);
									} else {
										var firstRow = $('.orders-' + type + ' .order-block:first');
										if (firstRow.length > 0)
											firstRow.before(orderDiv2);
										else {
											var op = $('.orders-' + type);
											
											if (op.hasClass('mCustomScrollbar')) {
												op.find('.mCSB_container').append(orderDiv2);
												$('.order-blocks[code=' + type + ']').mCustomScrollbar("update");
											} else {
												op.append(orderDiv2);
											}
										}
										
										var tmp = $('.order-type[code=' + type + '] .order-count').text();
										var oldCount = (tmp.length > 0) ? parseInt(tmp) : 0;
										$('.order-type[code=' + type + '] .order-count').text(oldCount + 1);
									}
									
								});
							});
						}
					});
				});
			}
		});
	}
	updateTask(process) {}
	
	deleteOrder(uid, iter, proc) {
		_util_js__WEBPACK_IMPORTED_MODULE_0__.Util.blockPage(_translation_js__WEBPACK_IMPORTED_MODULE_1__.Translation.translation.deleting);
		var par = {};
		par["uid"] = proc;
		par["obj"] = iter;
		par["key"] = uid;
		par["cmd"] = "kill";
		this.app.query(par).then(() => {
			$('body').unblock();
		});
	}
}


/***/ }),

/***/ "./personalPhoto.js":
/*!**************************!*\
  !*** ./personalPhoto.js ***!
  \**************************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   "PersonalPhoto": () => (/* binding */ PersonalPhoto)
/* harmony export */ });
/* harmony import */ var _util_js__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./util.js */ "./util.js");
/* harmony import */ var _translation_js__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./translation.js */ "./translation.js");



class PersonalPhoto {

    constructor(app) {
        this.app = app;
    }
	
    initProfile() {    
        let par = {"objId":this.app.userId,"attr":'аватар'};
        $.post(window.mainUrl + "&getAttr&rnd=" + _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.rnd(), par, function(res) {
            _util_js__WEBPACK_IMPORTED_MODULE_0__.DataChecker.checkData(res).then(res1 => {
                if (res1.result) {
                    $('#my-image').attr('src', "data:image/png;base64," + res1.result);
                }
            })
        }, 'json');	
    }

    uploadYourImage() {
        let this_ = this;
        
        $('#yourUpload').fileupload({
            dropZone: $('#my-photo'),
            pasteZone: $('#my-photo'),
            url: window.mainUrl + '&width=150&height=180',
            dataType: 'json',
            done: function (e, data) {
                if (data.result.result == 'success') {
                    let par = {};
                    par["objId"] = this_.app.userId;
                    par["attr"] = 'аватар';
                    $.post(window.mainUrl + "&getAttr&rnd=" + _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.rnd(), par, function(res1) {
                        _util_js__WEBPACK_IMPORTED_MODULE_0__.DataChecker.checkData(res1).then(res => {
                            if (res.result) {
                                this_.deleteOldImage();
                                $('#my-image').attr('src', "data:image/png;base64," + res.result);
                                $('.user-image img').attr('src', "data:image/png;base64," + res.result);
                            } else {
                                alert(_translation_js__WEBPACK_IMPORTED_MODULE_1__.Translation.translation.error, _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.ERROR);
                            }
                        })
                    }, 'json');
                } else {
                    alert(data.result.message, _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.ERROR);
                }
            }
        }).click();
    }
    

    deleteImage() {
        let this_ = this;

        let par = {};
        par["obj"] = "USER";
        par["name"] = 'удалить фото из ЛД';
        $.post(window.mainUrl + "&sfunc&rnd=" + _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.rnd(), par, function(res1) {
            _util_js__WEBPACK_IMPORTED_MODULE_0__.DataChecker.checkData(res1).then(res => {
                if (res.result) {
                    this_.deleteOldImage();
                    $('#my-image').attr('src', '');
                    $('.user-image img').attr('src', "css/img/empty-avatar-34.png");
                    
                } else {
                    alert(_translation_js__WEBPACK_IMPORTED_MODULE_1__.Translation.translation.error);
                }
            })
        }, 'json');
    }

    copyImageFromData() {
        let this_ = this;

        let par = {};
        par["obj"] = "USER";
        par["name"] = 'взять фото из ЛД';
        $.post(window.mainUrl + "&sfunc&rnd=" + _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.rnd(), par, function(res1) {
            _util_js__WEBPACK_IMPORTED_MODULE_0__.DataChecker.checkData(res1).then(res2 => {
                if (res2.result) {
                    var par = {};
                    par["objId"] = this_.app.userId;
                    par["attr"] = 'аватар';
                    $.post(window.mainUrl + "&getAttr&rnd=" + _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.rnd(), par, function(res3) {
                        _util_js__WEBPACK_IMPORTED_MODULE_0__.DataChecker.checkData(res3).then(res4 => {
                            if (res4.result) {
                                this_.deleteOldImage();
                                $('#my-image').attr('src', "data:image/png;base64," + res4.result);
                                $('.user-image img').attr('src', "data:image/png;base64," + res4.result);
                            } else {
                                alert(_translation_js__WEBPACK_IMPORTED_MODULE_1__.Translation.translation.error);
                            }
                        })
                    }, 'json');
                } else {
                    alert(_translation_js__WEBPACK_IMPORTED_MODULE_1__.Translation.translation.error);
                }
            })
        }, 'json');
    }

    deleteOldImage() {
        let par = {"sfunc" : 1, "cls": "ImageUtil", "name": "deleteUserImage", "arg0": this.app.userId, "arg1": "34", "arg2": "0"};
    
        this.app.query(par).then(response => {
            if (response.status === 200) {
                response.json().then(json => {
                    _util_js__WEBPACK_IMPORTED_MODULE_0__.DataChecker.checkData(json).then(data => {
                        if(!data) {
                            console.log("1 ошибка при вызове метода deleteUserImage в классе ImageUtil");
                        }
                    });
                });
            } else {
                console.log("2 ошибка при вызове метода deleteUserImage в классе ImageUtil");
            }
        });
    }
}

/***/ }),

/***/ "./ping.js":
/*!*****************!*\
  !*** ./ping.js ***!
  \*****************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   "Ping": () => (/* binding */ Ping)
/* harmony export */ });
/* harmony import */ var _util_js__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./util.js */ "./util.js");


class Ping {
	pinger;
    mainUrl = "";
    breadcrumpsVisible = true;
    lostRequests = Array();
    editingTable = undefined;
    ifcController;
    editIndex = {};

	constructor() {
        // this.mainUrl = url;
        // this.ifcController = ifcController;
        // this.editingTable = editingTable;
	}
	
	init() {
        this.ping();
	}

    ping() {
        if (this.pinger != null) clearTimeout(this.pinger);
        this.pingServer();
        this.pinger = setTimeout(this.ping.bind(this), 30000);
    }

    pingServer() {
        var url = window.mainUrl + "&ping&rnd=" + _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.rnd();
        var beginTime = (new Date).getTime();
        $.post(url, function(data) {
            var pingTime = (new Date).getTime() - beginTime;
            $('#ping').text('' + pingTime);
        }, 'json');
    }

}

/***/ }),

/***/ "./polling.js":
/*!********************!*\
  !*** ./polling.js ***!
  \********************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   "Polling": () => (/* binding */ Polling),
/* harmony export */   "LostRequest": () => (/* binding */ LostRequest)
/* harmony export */ });
/* harmony import */ var _translation_js__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./translation.js */ "./translation.js");
/* harmony import */ var _util_js__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./util.js */ "./util.js");
/* harmony import */ var _nca_js__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./nca.js */ "./nca.js");




class Polling {
	
	app = null;
	noConnection = false;
	screenBlocked = false;
	reconnectTimeout = 500;
	lostRequests = Array();
	
	constructor(app) {
		this.app = app;
	}

	longPolling() {
		let _this = this;
		// Идентификатор таймаута, который должен разблокировать экран при появлении связи
		var timeoutId = null;
		// Если связи нет
		if (_this.noConnection) {
			// То запускаем таймер для разблокировки экрана
			timeoutId = setTimeout(function() {
				_this.noConnection = false;
				if (_this.screenBlocked) {
					$('body').unblock();
					_this.screenBlocked = false;
					_this.reconnectTimeout = 500;
				}
				var requests = [];
				for (var i=0; i<_this.lostRequests.length; i++) {
					requests.push(_this.lostRequests[i]);
				}
				_this.lostRequests = [];
				for (var i=0; i<requests.length; i++) {
					var req = requests[i];
					_this.saveAjaxRequest(req.type, req.url, req.params, req.success, req.dataType, true);
				}
				requests = null;
				timeoutId = null;
			}, 2000 + _this.reconnectTimeout);
		}
		
		var par = {"polling": _util_js__WEBPACK_IMPORTED_MODULE_1__.Util.rnd()};
		
		_this.app.post(par).then(data => {
			_this.processingPolling(data);
			setTimeout(function(){_this.longPolling();}, 500);
		}).catch(error => {
			console.error("Long polling error: " + error);
			if (error.stack)
				console.log(error.stack);
			// Отменяем разблокировку экрана, так как связь не появилась
			if (timeoutId != null) {
				clearTimeout(timeoutId);
				if (!_this.screenBlocked) {
					_this.screenBlocked = true;
					_this.reconnectTimeout = 2000;
					_util_js__WEBPACK_IMPORTED_MODULE_1__.Util.blockPage("Соединение с сервером утеряно.<br>Попытка возобновления связи!<br>Подождите...");
				}			
			}
			// Если связь была и вдруг пропала
			if (!_this.noConnection) {
				_this.noConnection = true;
			}			
	
			setTimeout(function(){_this.longPolling();}, _this.reconnectTimeout);
		});
	}
	
	processingPolling(data) {
		let _this = this;
		let ifc = this.app.ifcController;
		let nav = this.app.nav;
		let orders = this.app.orders;
		let notifs = this.app.notifications;
		let nca = this.app.ncaLayer;
		let monitor = this.app.monitor;
		
		if (data.commands && data.commands.length>0) {
			var cmd;
			for (var i=0; i<data.commands.length; i++) {
				cmd = data.commands[i];
				if (cmd) {
					if (cmd.refresh != null) {
						var par = {};
						par["rnd"] = _util_js__WEBPACK_IMPORTED_MODULE_1__.Util.rnd();
						par["getChange"] = "";
						ifc.postAndParseData(par);
					} else if (cmd.prevUI != null) {
						if (cmd.prevUI == 1)
							document.location.hash = "cmd=prevUI&rnd=" + _util_js__WEBPACK_IMPORTED_MODULE_1__.Util.rnd();
						else
							_this.app.nav.toActiveMain();
					} else if (cmd.main_ui != null) {
						_this.app.nav.toActiveMain();
					} else if (cmd.closePopupInterface != null) {
						if (_this.app.ifcController.popupcount > 0) {
							var did = 'or3_popup' + (_this.app.ifcController.popupcount - 1);
							ifc.showChangeMsg();
							ifc.dialogResult[did] = '0';
							var opts = $('#' + did).dialog('options');
						    
						    var par = {};
							par["uid"] = opts.uid ;
							par["val"] = cmd.closePopupInterface;
							par["cmd"] = "closePopup";
		
							ifc.loadData(par, true, function(data) {
								if (!data.result || data.result == 'success') {
									$("#" + did).dialog('destroy');
								} else {
									ifc.showPopupErrors(data.errors, data.path, data.name, opts, $('#' + did), data.fatal, data.isDataIntegrityControl);
									ifc.setDialogBtnsEnabled(did, true);
								}
							});
						}
					} else if (cmd.closeInterface != null) {
						ifc.closeIfc();
					} else if(cmd.startProcessWrp != null){
						_this.app.nav.startProcess(cmd.startProcessWrp);
					} else if (cmd.showProcessUI != null) {
						_util_js__WEBPACK_IMPORTED_MODULE_1__.Util.blockPage();
						if (data.infMsg) {
							_util_js__WEBPACK_IMPORTED_MODULE_1__.Util.alert(data.infMsg);
					 	}
						var message = JSON.parse(cmd.showProcessUI);
						setTimeout(function(){
							_this.app.nav.showProcessUI(message)
						}, message.waitTime);					
					} else if(cmd.keyPressed) {
						let command = cmd.keyPressed === 'Enter'? 13:cmd.keyPressed === 'Escape'? 27: 0;
						if(command != 0){
							var e = jQuery.Event("keydown");
							e.which = command;
							e.key = cmd.keyPressed;
							$(document).trigger(e);
						}					
						
					}
					else if (cmd.start_ui) {
						if (document.location.hash.indexOf("cmd=openTask&uid=" + cmd.start_ui) == -1) {
							document.location.hash = "cmd=openTask&uid=" + cmd.start_ui + "&id=" + _this.app.nav.activeMenu + "&rnd=" + _util_js__WEBPACK_IMPORTED_MODULE_1__.Util.rnd();
							if (ifc.dialogOpened) {
								ifc.dialogResult[ifc.dialogOpened] = '0';
								$('#' + ifc.dialogOpened).dialog('destroy');
							}
						}
					} else if (cmd.next_ui) {
						document.location.hash = "cmd=openTask&uid=" + cmd.next_ui + "&id=" + _this.app.nav.activeMenuId + "&rnd=" + _util_js__WEBPACK_IMPORTED_MODULE_1__.Util.rnd();
						if (ifc.dialogOpened) {
							ifc.dialogResult[ifc.dialogOpened] = '0';
							$('#' + ifc.dialogOpened).dialog('destroy');
						}
					} else if (cmd.open_report) {
						var url = _this.app.restUrl + "&rnd=" + _util_js__WEBPACK_IMPORTED_MODULE_1__.Util.rnd() + "&cmd=opf&fn=" + encodeURIComponent(cmd.open_report);
						$('#report_frame').attr('src', url);
					} else if (cmd.signString) {
						var str = cmd.signString.str;
						var path = cmd.signString.path;
						var pass = cmd.signString.pass;
						var cont = cmd.signString.cont;
						var auth = (cmd.signString.auth == "true") || (cmd.signString.auth == true);
	
						generateUcgoSign(str, path, pass, cont, auth);
					} else if (cmd.signTextWithNCA) {
						var text = cmd.signTextWithNCA.text;
						var path = cmd.signTextWithNCA.path;
						var pass = cmd.signTextWithNCA.pass;
						var cont = cmd.signTextWithNCA.cont;
	
						let xml = '<signTextWithNCA><signText>' + _util_js__WEBPACK_IMPORTED_MODULE_1__.Util.b64EncodeUnicode(text) + '</signText></signTextWithNCA>';
						nca.signXml(_nca_js__WEBPACK_IMPORTED_MODULE_2__.NCA.COMMON, _nca_js__WEBPACK_IMPORTED_MODULE_2__.NCA.SIGN, xml).then(data2 => {
							if (data2.responseObject) {
								let signedData = data2.responseObject;
								var par = {
									cmd: "signTexWithNCAResult",
									signedData: signedData,
									signText: text,
									signDate: (new Date).getTime(),
									rnd: _util_js__WEBPACK_IMPORTED_MODULE_1__.Util.rnd()
								};
								_this.app.post(par)
							} else {
								let error = (data2.message === 'action.canceled')
									? 'Действие отменено пользователем'
									: 'Код ошибки: ' + data2.code + ' Сообщение: ' + data2.message;
								console.error("msg text: " + error);
								_util_js__WEBPACK_IMPORTED_MODULE_1__.Util.alert(error, _util_js__WEBPACK_IMPORTED_MODULE_1__.Util.ERROR);
								
								var par = {cmd: "signTexWithNCAResult", error: "Подписание текста не выполнено!"};
						    	_this.app.post(par)
							}
						}, error => {
							console.error("error text: " + error);
							_util_js__WEBPACK_IMPORTED_MODULE_1__.Util.alert(error, _util_js__WEBPACK_IMPORTED_MODULE_1__.Util.ERROR);
							var par = {cmd: "signTexWithNCAResult", error: "Подписание текста не выполнено!"};
					    	_this.app.post(par)
						});
						
					} else if (cmd.getFile) {
						if (document.getElementById('getFile') == null) {
							var dialogDiv = $('<div />', {'class': 'easyui-dialog', 'id': 'getFile'}); 
							dialogDiv.html('<div style=\"padding: 20px\"><input id=\"chooser\" type=\"file\" onchange=\"sendFile();\" style=\"display:none;\"><input id=\"chooserBtn\" type=\"button\" value=\"Выбрать\" style=\"width:100px; height:25px;\"></div>');
							dialogDiv.dialog({
								title:'Выбор файла',
								closed: true,
								draggable: false,
								resizable: false,
								closeOnEscape: false,
								modal:true,
								closable: false,
								buttons:[{text:'Закрыть', id: 'closeDialog', handler:function(){ifc.sendFile();}}]
							});
							$("#chooserBtn").click(function() {
								$("#chooser").click();
							});
						}
						var chooserElement = $("#chooser");
						chooserElement.replaceWith(chooserElement.val('').clone(true));
						$('body').unblock();
						$('#getFile').dialog("open");
					} else if (cmd.generateUcgoPKCS10) {
						var str = cmd.generateUcgoPKCS10.str;
						var auth = (cmd.generateUcgoPKCS10.auth == "true") || (cmd.generateUcgoPKCS10.auth == true);
						generateUcgoPKCS10(str, auth);
					} else if (cmd.generateUcgoPKCS7) {
						var str = cmd.generateUcgoPKCS7.str;
						var auth = (cmd.generateUcgoPKCS7.auth == "true") || (cmd.generateUcgoPKCS7.auth == true);
						generateUcgoPKCS7(str, auth);
					} else if (cmd.saveUcgoCertificate) {
						var cert = cmd.saveUcgoCertificate.cert;
						var reader = cmd.saveUcgoCertificate.reader;
						var uid = cmd.saveUcgoCertificate.uid;
						var tokPD = cmd.saveUcgoCertificate.tokpd;
						saveUcgoCertificate(cert, reader, uid, tokPD);
					} else if (cmd.haveUcgoCertificate) {
						var reader = cmd.haveUcgoCertificate.reader;
						var uid = cmd.haveUcgoCertificate.uid;
						var tokPD = cmd.haveUcgoCertificate.tokpd;
						haveUcgoCertificate(reader, uid, tokPD);
					} else if (cmd.selectUcgoCertificate) {
						var iin = cmd.selectUcgoCertificate.iin;
						var bin = cmd.selectUcgoCertificate.bin;
						selectUcgoCertificate(iin, bin);
					} else if (cmd.deleteUcgoCertificate) {
						deleteUcgoCertificate(cmd.deleteUcgoCertificate);
				
					// подключение к веб-сокету для работы с ПО ЭЦП УЦГО
					} else if (cmd.connectUcgoWebsocket) {
						connectTamurSocket(true);
					} else if (cmd.showErrors) {
						$('body').unblock();
						if (ifc.dialogOpened){
							ifc.setDialogBtnsEnabled(ifc.dialogOpened, true);
						}
						
						if (cmd.showErrors.btnEdt && cmd.showErrors.btnIgn){
							ifc.showForceErrors(cmd.showErrors.errors, cmd.showErrors.fatal == 0, cmd.showErrors.path, cmd.showErrors.name, cmd.showErrors.btnIgn, cmd.showErrors.btnEdt);
						}else{
							ifc.showForceErrors(cmd.showErrors.errors, cmd.showErrors.fatal == 0, cmd.showErrors.path, cmd.showErrors.name, _translation_js__WEBPACK_IMPORTED_MODULE_0__.Translation.translation.ignore);
						}
					} else if (cmd.connectScanWebsocket) {
						scan_connectSocket(null);
					} else if (cmd.disconnectScanWebsocket) {
						scan_disconnectSocket();
					} else if (cmd.startScan) {
						var id = cmd.startScan.id;
						startScan(id);
					} else if (cmd.openClientFiles) {
		            	
						var id = cmd.openClientFiles.id;
						var windowTitle = cmd.openClientFiles.windowTitle;
						var buttonTitle = cmd.openClientFiles.buttonTitle;
						var dir = cmd.openClientFiles.dir;
						var extensions = cmd.openClientFiles.extensions;
						var description = cmd.openClientFiles.description;
	
						scan_openClientFiles(id, windowTitle, buttonTitle, dir, extensions, description);
					} else if (cmd.loadClientFile) {
						var id = cmd.loadClientFile.id;
						var path = cmd.loadClientFile.path;
						scan_loadClientFile(id, path);
					} else if (cmd.saveFileOnClient) {
						var id = cmd.saveFileOnClient.id;
						var path = cmd.saveFileOnClient.path;
						var data = cmd.saveFileOnClient.data;
						scan_saveFileOnClient(id, path, data);
					} else if (cmd.showOptions) {
						$('body').unblock();
						ifc.showOptions(cmd.showOptions.options);
					} else if (cmd.confirm) {
						_util_js__WEBPACK_IMPORTED_MODULE_1__.Util.confirmMessage(cmd.confirm);
					} else if (cmd.alert != null) {
						_util_js__WEBPACK_IMPORTED_MODULE_1__.Util.alert(cmd.alert, 0, false);
					} else if (cmd.logout) {
						_util_js__WEBPACK_IMPORTED_MODULE_1__.Util.logout();
					} else if (cmd.notification) {
						notifs.notificationsProcessing(cmd.notification);
						_util_js__WEBPACK_IMPORTED_MODULE_1__.Util.playNoteSound(); 
					} else if (cmd.updateNotifications) {
						notifs.loadNotifications2();
					} else if (cmd.deleteNotifications) {
						notifs.deleteNotifications(cmd.deleteNotifications.objids);
					} else if (cmd.alertAndDrop) {
						_util_js__WEBPACK_IMPORTED_MODULE_1__.Util.alert(cmd.alertAndDrop, 0, true, true);
					} else if (cmd.alertError != null) {
						_util_js__WEBPACK_IMPORTED_MODULE_1__.Util.alert(cmd.alertError, _util_js__WEBPACK_IMPORTED_MODULE_1__.Util.ERROR, false, false);
					} else if (cmd.alertWarning != null) {
						_util_js__WEBPACK_IMPORTED_MODULE_1__.Util.alert(cmd.alertWarning, _util_js__WEBPACK_IMPORTED_MODULE_1__.Util.WARNING, false, false);
					} else if (cmd.alertInfoFlow != null) {
						_util_js__WEBPACK_IMPORTED_MODULE_1__.Util.alert(cmd.alertInfoFlow, 0, true);
					} else if (cmd.alertErrorFlow != null) {
						_util_js__WEBPACK_IMPORTED_MODULE_1__.Util.alert(cmd.alertErrorFlow, _util_js__WEBPACK_IMPORTED_MODULE_1__.Util.ERROR, true, false);
					} else if (cmd.alertWarningFlow != null) {
						_util_js__WEBPACK_IMPORTED_MODULE_1__.Util.alert(cmd.alertWarningFlow, _util_js__WEBPACK_IMPORTED_MODULE_1__.Util.WARNING, true, false);
					} else if (cmd.slide) {
						_util_js__WEBPACK_IMPORTED_MODULE_1__.Util.slide(cmd.slide);
					} else if (cmd.showWaiting) {
						document.body.style.cursor = 'wait';
						_util_js__WEBPACK_IMPORTED_MODULE_1__.Util.blockPage(cmd.showWaiting);
					} else if (cmd.closeWaiting) {
						document.body.style.cursor = 'default';
						$('body').unblock();
					} else if (cmd.disableCancelBtn) {
						ifc.hideChangeMsg();
					} else if (cmd.nodeType) {
						$('#nextBtn').linkbutton({text: cmd.nodeType});
					} else if(cmd.makeTreeTable){
						ifc.makeTreeTable(cmd.makeTreeTable);
					} else if (cmd.cursor) {
						if (cmd.cursor == "0") {
							document.body.style.cursor = 'default';
							$('body').unblock();
						} else if (cmd.cursor == "1") {
							document.body.style.cursor = 'wait';
							_util_js__WEBPACK_IMPORTED_MODULE_1__.Util.blockPage();
						} else if (cmd.cursor == "2") {
							document.body.style.cursor = 'pointer';
						}
					} else if (cmd.stack) {
						nav.showStack(cmd.stack);
					} else if (cmd.hideFullPath) {
						$("#fullPath").hide();
					} else if (cmd.deleteTask) {
						monitor.deleteTask(cmd.deleteTask);
					} else if (cmd.addTask) {
						monitor.addTask(cmd.addTask);
					} else if (cmd.updateTask) {
						monitor.updateTask(cmd.updateTask);
					} else if (cmd.deleteOrders) {
						orders.deleteOrders(cmd.deleteOrders);
						notifs.deleteNotification(cmd.deleteOrders);
					} else if (cmd.updateOrders) {
						orders.updateOrders(cmd.updateOrders);
						notifs.updateNotification(cmd.updateOrders);
					} else if (cmd.reload) {
						ifc.reload(cmd.reload);
					} else if (cmd.hideSend) {
						if (cmd.hideSend == "0"){
							$('#nextBtn').hide();
						} else if (cmd.hideSend == "1") {
							$('#nextBtn').show();
						}
					} else if (cmd.hideSave) {
						if (cmd.hideSave == "0"){
							$('#saveBtn').hide();
						} else if (cmd.hideSave == "1"){
							$('#saveBtn').show();
						}
					} else if (cmd.hideCancel) {
						if (cmd.hideCancel == "0"){
							$('#cancelBtn').hide();
						} else if (cmd.hideCancel == "1") {
							$('#cancelBtn').show();
						} else if (cmd.hideCancel == "2") {
							$('#cancelBtn').show();
							$('#cancelBtn').linkbutton('enable');
						}
					//} else if (cmd.readIdCard) {
					//	readIdCard();
					//} else if (cmd.loadApplet) {
					//	loadApplet(cmd.loadApplet);
					} else if (cmd.openArh) {
						nav.openArchive(cmd.openArh);
					} else if (cmd.openDocument) {
						_util_js__WEBPACK_IMPORTED_MODULE_1__.Util.openDocument(cmd.openDocument);
					} else if (cmd.askPassword) {
						_util_js__WEBPACK_IMPORTED_MODULE_1__.Util.askPassword(function(res) {
							if (res != null)
								post({"promptRes":res, "promptAction":1});
							else
								post({"promptAction":0});
								
						}, cmd.askPassword);
					} else if(cmd.setSelectedTab){					
						var compUid = cmd.setSelectedTab.split(',')[0];
						var index = parseInt(cmd.setSelectedTab.split(',')[1]);
						if($('#'+compUid).hasClass('easyui-tabs')){
							$('#'+compUid).tabs('select', index);
						}
					} else if (cmd.setMultiSelection) {
						let compUid = cmd.setMultiSelection.split(',')[0];
						let multi = cmd.setMultiSelection.split(',')[1] === 'true';
						console.log("workworkwork1", compUid);
						if (ifc.multiSelection[compUid]) {
						    ifc.multiSelection[compUid] = multi;
						}
						ifc.multiSelection[compUid] = multi;
					}
				}
			}
		} 
	}
	
	saveAjaxRequest(type, url, params, success, dataType, async) {
		params["rnd"] = _util_js__WEBPACK_IMPORTED_MODULE_1__.Util.rnd();
		params["guid"] = guid;
	
		fetch(this.restUrl, {
			method: type,
	    	credentials: 'include',
			body: $.param(params)
	    }).then(response => {
			success(response);
		}, () => {
			console.error('Rejected');
			if (async)
				lostRequests.push(new LostRequest(type, url, params, success, dataType));
		}).catch(error => {
			console.error('Error:', error);
			if (async)
				lostRequests.push(new LostRequest(type, url, params, success, dataType));
		});
	}
}

class LostRequest {
	type = null;
	url = null;
	params = null;
	success = null;
	dataType = null;
	
	constructor(type, url, params, success, dataType) {
		this.type = type;
		this.url = url;
		this.params = params;
		this.success = success;
		this.dataType = dataType;
	}
}

/***/ }),

/***/ "./processes.js":
/*!**********************!*\
  !*** ./processes.js ***!
  \**********************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   "Processes": () => (/* binding */ Processes)
/* harmony export */ });
/* harmony import */ var _util_js__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./util.js */ "./util.js");



class Processes {
	searchProcess = "";
	SearchText;
    SearchCount = 0;
	heightRange = 40;
	styledInputElement;
	styledElement;

	orderStatus = ["label-success", "label-warning", "label-important", "label-info"];
	imgExt = ".png";

	constructor(app) {
		this.app = app;
	}
	
	findOfWordsAutocomplete() {
		console.log("findOfWordsAutocomplete");
	}

	init() {
		let this_ = this;
		$('#processesList_Layout').layout();

		// $( "#processSearchPage" ).autocomplete({
		// 	source:  this_.findOfWordsAutocomplete(),
		// });

		$("body").on('keypress', '#processSearchPage', function(e) {
			var key = e.which || e.keyCode;
			if (key === 13) {
				this_.searchProcess = $(this).val();
				if(this_.SearchText == this_.searchProcess){
					this_.SearchCount++;
				} else{
					this_.SearchText = this_.searchProcess;
					this_.SearchCount = 0;
				}
				var par = {"cmd":"searchProcess","text":this_.searchProcess, "index": this_.SearchCount};
				this_.post(par, function (data){
					_util_js__WEBPACK_IMPORTED_MODULE_0__.DataChecker.checkData(data).then(json => {
						if(data.parent) {
							var arr = data.parent.split(",").reverse();
							var procUid = arr.pop();
							var point = procUid.indexOf('.') + 1;
							var procElId = 'processess' + procUid.substring(point);
							var col = arr.length-1;
							var element;
							if(col == 1) {
								var node = $('#processTree').tree('find', arr[1]);
								if(node != null) {
									$('#processTree').tree('select', node.target);
									var nodeTopPos = node.target.offsetTop;
									var treeElement = document.getElementById('processesList_Tree');
									if(treeElement)
									treeElement.scrollTop = nodeTopPos - (treeElement.offsetHeight - this_.heightRange);
									this_.selectProcess(node, function() {
										element = document.getElementById(procElId);
										if(element){
											element.style.boxShadow = "0px 0px 5px blue";
											this_.styledElement = element;
											var atag = element.getElementsByTagName("A")[0];
											if(atag){
												atag.style.color = "blue";
												this_.styledInputElement = atag;
											}
											var topPos = element.offsetTop;
											var outerElement = document.getElementById('processesList_body');
											if(outerElement)
											outerElement.scrollTop = topPos - (outerElement.offsetHeight - element.offsetHeight);
										}
									});
								}
							} else {
								for(var i = 1; i < col; i++) {
									this_.searchProcCallLater('expand', arr[i], 5);
								}
								this_.searchProcCallLater('select', arr[col], 5, function(){
									element = document.getElementById(procElId);
									if(element){
										element.style.boxShadow = "0px 0px 5px blue";
										this_.styledElement = element;
										var atag = element.getElementsByTagName("A")[0];							
										if(atag){
											atag.style.color = "blue";
											this_.styledInputElement = atag;
										}
										var topPos = element.offsetTop;
										var outerElement = document.getElementById('processesList_body');
										if(outerElement)
										outerElement.scrollTop = topPos - (outerElement.offsetHeight - element.offsetHeight);
									}
								});
							}
						} else {
							this_.app.lang == "kz" ? alert("Еш нәрсе табылмады!") : alert("Ничего не найдено!");
						}
					})
				});
			}
		});

		$('#processTree').tree({
			onClick: function(node){
				this_.selectProcess(node);
			}
		});
				
		$("body").on('click', '#processesList_body .proc', function() {
			var procId = $(this).attr("procId");
			this_.app.nav.startProcess(procId);
		});

		$("body").on('click', '#freqUsedProcs .process', function() {
			var procId = $(this).attr("uid");
			this_.app.nav.startProcess(procId);
		});
		
		$("body").on('click', '#processesList_body .proc .icon-remove-favourite', function(e) {
			e.preventDefault();
			e.stopPropagation();
			
			this_.removeFromFavourite($(this));
		});
		
		$("body").on('click', '#processesList_body .proc .icon-add-favourite', function(e) {
			e.preventDefault();
			e.stopPropagation();
			
			this_.addToFavourite($(this));
		});
	}
	
	selectProcess(node, func) {
		let this_ = this;
		
		return new Promise((resolve, reject) => {
			let par = {"cmd": "getProcessData", "leaf": 1, "id": node.id};
			
			this.app.query(par).then(response => {
				if (response.status === 200) {
					response.json().then(data => {
						$('#processesList_body').html("");
						$.each(data, function(e, proc) {
							var u = proc.id.indexOf(".");
							var key = proc.id.substring(u+1);
							
							var fav = (proc.favourite)
										? "<i class='icon-remove-favourite' uid='" + key + "'></i>"
										: "<i class='icon-add-favourite' uid='" + key + "'></i>";
							
							var fontWeight = proc.fontWeight ? "font-weight:bold;" : "";
							$('#processesList_body').append("<div time = '" + proc.time + "' desc = '" + proc.procDesc + "'id='processess" + key 
								+ "' class='proc' procId='" + proc.id + "'>"
								+ fav
								+ "<span class='ico_proc ico_" + proc.id.replace('.', '_') + "'></span>"
								+ "<a style='"+fontWeight+"'>" + proc.title + "</a></div> ");
								
							this_.addProcIcon('#processesList_body .proc.ico_' + proc.id.replace('.', '_'), this_.app.restUrl + '/jsp/media/css/or3/'+proc.id.replace('.', '_')+this_.imgExt);
							
							this_.attachProcessTooltips('processess'+key);
							
						});
						if(func) {
							func();
						}
						resolve();
					});
				}
			});
		});
	}
	
	addProcIcon(className, src) {
		let this_ = this;
		var img = new Image();
		img.onload = function() {
			this_.addStyle(className+' {background-image: url('+src+') !important}');
		};
		img.src = src;
	}
	
	addStyle(st) {
	    var style = document.createElement('style');
		style.type = 'text/css';
		style.innerHTML = st;
		document.getElementsByTagName('head')[0].appendChild(style);
	}
	
	attachProcessTooltips(proc_id) {
		
	}
	
	addToFavourite(elem) {
		var procId = elem.parent().attr("procId");
		var par = {cmd: 'addToFavorites', processUID: procId,  json: 1};
		this.app.query(par);
		
		elem.removeClass('icon-add-favourite').addClass('icon-remove-favourite');
		
		var html = '<div class="process" uid="' + procId + '">';
		html += '<i class="icon-left icon-vacation"/>';
		html += '<span>' + elem.parent().find('a').text() + '</span>';
		html += '<i class="icon-right icon-arrow-right" />';
		html += '</div>';
		
		$('#freqUsedProcs .portletBody').append($(html));
				
	}
	
	removeFromFavourite(elem) {
		var procId = elem.parent().attr("procId");
		var par = {cmd: 'removeFromFavorites', processUID: procId,  json: 1};
		this.app.query(par);
		
		elem.removeClass('icon-remove-favourite').addClass('icon-add-favourite');
		$('#freqUsedProcs .process[uid="' + procId + '"]').remove();
	}


	// добавил Жаркын
	post(par, func) {
        this.saveAsyncPostRequest(window.mainUrl, par, func, func != null ? 'json' : null);
    }

    saveAsyncPostRequest(url, params, success, dataType) {
        params["rnd"] = _util_js__WEBPACK_IMPORTED_MODULE_0__.Util.rnd();
        params["guid"] = guid;
    
        $.post(url, params, success, dataType).fail(function() {
            // Добавляем запрос в хранилище необработанных запросов
            // lostRequests.push(new LostRequest("POST", url, params, success, dataType));
        });
    }

	searchProcCallLater(name, id, count, func) {
        //console.log("Count = " + count);
        var node = $('#processTree').tree('find', id);
        if (node != null) {
            $('#processTree').tree(name, node.target);
            if(name === 'select') {
                var nodeTopPos = node.target.offsetTop;
                var treeElement = document.getElementById('processesList_Tree');
                if(treeElement)
                treeElement.scrollTop = nodeTopPos - (treeElement.offsetHeight - this.heightRange);
                this.selectProcess(node, func);
            }
            return;
        } else if (count > 0) {
            let _this = this;
            count--;
            setTimeout(function(){_this.searchProcCallLater(name, id, count, func);}, 300);
        }
    
    }
}

/***/ }),

/***/ "./tables.js":
/*!*******************!*\
  !*** ./tables.js ***!
  \*******************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   "TableOps": () => (/* binding */ TableOps)
/* harmony export */ });
/* harmony import */ var _events_js__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./events.js */ "./events.js");
/* harmony import */ var _util_js__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./util.js */ "./util.js");
/* harmony import */ var _translation_js__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./translation.js */ "./translation.js");
/* harmony import */ var _tooltip_js__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./tooltip.js */ "./tooltip.js");





class TableOps {
	
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
			
			_tooltip_js__WEBPACK_IMPORTED_MODULE_3__.TooltipOps.addDatagridTooltip($(this));
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
						if (_events_js__WEBPACK_IMPORTED_MODULE_0__.EventOps.ctrlPressed) {
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
				
				if (_this.firstShiftIndex == -1 || !_events_js__WEBPACK_IMPORTED_MODULE_0__.EventOps.shiftPressed){
					_this.firstShiftIndex = _this.lastShiftIndex = clickIndex;
					if(!_events_js__WEBPACK_IMPORTED_MODULE_0__.EventOps.ctrlPressed)
						val = _this.lastShiftIndex + '';
				}
				if (indxs.length == 0){
					_this.firstShiftIndex = _this.lastShiftIndex = -1;
					val = '';
				}
				
				if (tbl.datagrid('getSelections').length == 0 && !_events_js__WEBPACK_IMPORTED_MODULE_0__.EventOps.ctrlPressed & !_events_js__WEBPACK_IMPORTED_MODULE_0__.EventOps.shiftPressed) {
					tbl.datagrid('selectRow', rIndex);
					_this.firstShiftIndex = _this.lastShiftIndex = _this.globalIndex(tbl, rIndex);
					val = _this.lastShiftIndex + '';
				}
				
				if(_events_js__WEBPACK_IMPORTED_MODULE_0__.EventOps.shiftPressed && _this.firstShiftIndex != -1 /*&& clickIndex != _this.firstShiftIndex*/){
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
					
					if (event.which && event.which == 38 && _events_js__WEBPACK_IMPORTED_MODULE_0__.EventOps.shiftPressed && _this.firstShiftIndex != -1) {
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
					} else if (event.which && event.which == 40 && _events_js__WEBPACK_IMPORTED_MODULE_0__.EventOps.shiftPressed && _this.firstShiftIndex != -1) {
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
					} else if (event.which && event.which == 38 && !_events_js__WEBPACK_IMPORTED_MODULE_0__.EventOps.shiftPressed) {
						if(_this.firstShiftIndex != _this.lastShiftIndex){
							tbl.datagrid('clearSelections');
							tbl.datagrid('selectRow', _this.lastShiftIndex);
						}
						_this.firstShiftIndex = _this.lastShiftIndex = tbl.datagrid('getRowIndex', tbl.datagrid('getSelected')) - 1;
						val = _this.lastShiftIndex + '';
						return selectRow($(this), true);
					} else if (event.which && event.which == 40 && !_events_js__WEBPACK_IMPORTED_MODULE_0__.EventOps.shiftPressed) {
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
						if (_events_js__WEBPACK_IMPORTED_MODULE_0__.EventOps.ctrlPressed) {
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
										text: _translation_js__WEBPACK_IMPORTED_MODULE_2__.Translation.translation.ok,
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
											InterfaceController.popDlgType.push(_util_js__WEBPACK_IMPORTED_MODULE_1__.Util.DLG_NO_SEND);
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
				_util_js__WEBPACK_IMPORTED_MODULE_1__.Util.alert('Ничего не найдено!');
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
	
						str = _util_js__WEBPACK_IMPORTED_MODULE_1__.Util.sanitizeHtml(str);
						
						$.each(tr.find('td'), function(i, td) {
							var tag = $(td).find('.tree-title');
							if (tag.length == 0) tag = $(td).find('div');
							
							var html = _util_js__WEBPACK_IMPORTED_MODULE_1__.Util.sanitizeHtml(tag.get(0).textContent);
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
					_util_js__WEBPACK_IMPORTED_MODULE_1__.Util.alert('Ничего не найдено!');
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
			
			text = _util_js__WEBPACK_IMPORTED_MODULE_1__.Util.sanitizeHtml(text);
			
			var colIdx = 0;
			$.each(tr.find('td'), function(i, td) {
				var tag = $(td).find('.tree-title');
				if (tag.length == 0) tag = $(td).find('div');
				var html = _util_js__WEBPACK_IMPORTED_MODULE_1__.Util.sanitizeHtml(tag.get(0).textContent);
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
		par["rnd"] = _util_js__WEBPACK_IMPORTED_MODULE_1__.Util.rnd();
		
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
					_util_js__WEBPACK_IMPORTED_MODULE_1__.Util.blockDialog(dlg);
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


/***/ }),

/***/ "./tooltip.js":
/*!********************!*\
  !*** ./tooltip.js ***!
  \********************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   "TooltipOps": () => (/* binding */ TooltipOps)
/* harmony export */ });
class TooltipOps {
	
	constructor() {
	}
	
	init() {
	}
	
	static addDatagridTooltip(tbl) {
		var headerCells = tbl.datagrid('getPanel').find('div.datagrid-header td[field] div.datagrid-cell:not(:empty)');
		headerCells.tooltip({
			position : 'top',
			content : function(){
				var content = this.getElementsByClassName('tableColumnTooltipText')[0];			
				if(content){
					var pElem = content.getElementsByTagName('p')[0];
					var fontElem = content.getElementsByTagName('font')[0];
					if(fontElem){
						var color = fontElem.color;
						setAllChildrenColor(pElem, color);					
					}
					return content.innerHTML;
				}
			},
			onShow : function(){
				var content = this.getElementsByClassName('tableColumnTooltipText')[0];
				if(content){
					var t = $(this); 
					$(this).tooltip('tip').css({
						backgroundColor: '#fafad2',
						borderColor: '#666',
					}).unbind().bind('mouseenter', function(){					            	   
						t.tooltip('show');
					}).bind('mouseleave', function() {
						t.tooltip('hide');	
					})
				} else {
					$(this).tooltip("destroy");
				}
				
			}
		});
		var cells = tbl.datagrid('getPanel').find('div.datagrid-body td[field] div.datagrid-cell:not(:empty)');
		cells.tooltip({
			position : 'top',
			content: function(){	
				var scrWidth = this.lastChild.parentNode.getElementsByTagName("INPUT")[0] ? this.lastChild.parentNode.getElementsByTagName("INPUT")[0].scrollWidth : this.lastChild.parentNode.scrollWidth;
				var clWidth = this.lastChild.parentNode.getElementsByTagName("INPUT")[0]  ? this.lastChild.parentNode.getElementsByTagName("INPUT")[0].clientWidth : this.lastChild.parentNode.clientWidth;
				if(scrWidth > clWidth){
					
					var tp = this.lastChild.textContent;
					if(!tp){
						tp = this.lastChild.getElementsByTagName("INPUT")[0].value;
					}	    				 
					return tp;
				}
			},
			onShow: function(){
				var scrWidth = this.lastChild.parentNode.getElementsByTagName("INPUT")[0] ? this.lastChild.parentNode.getElementsByTagName("INPUT")[0].scrollWidth : this.lastChild.parentNode.scrollWidth;
				var clWidth = this.lastChild.parentNode.getElementsByTagName("INPUT")[0]  ? this.lastChild.parentNode.getElementsByTagName("INPUT")[0].clientWidth : this.lastChild.parentNode.clientWidth;
				if(scrWidth > clWidth){
					var t = $(this); 
					$(this).tooltip('tip').css({
						backgroundColor: '#fafad2',
						borderColor: '#666',
					}).unbind().bind('mouseenter', function(){					            	   
						t.tooltip('show');
					}).bind('mouseleave', function() {
						t.tooltip('hide');
					});
				} else if($(this).tooltip){
					$(this).tooltip("destroy");
				}
				
			}
		});
	}
}


/***/ }),

/***/ "./translation.js":
/*!************************!*\
  !*** ./translation.js ***!
  \************************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   "Translation": () => (/* binding */ Translation)
/* harmony export */ });
class Translation {
	static translation = {};

	static init(lang) {
		if (lang == 'kz') {
			this.translation['ok'] = 'OK';
			this.translation['cancel'] = 'Болдырмау';
			this.translation['print'] = 'Баспаға';
			this.translation['continue'] = 'Жалғастыру';
			this.translation['continue2'] = 'Енгізуді жалғастыру';
			this.translation['save'] = 'Сақтау';
			this.translation['errors'] = 'Мәліметтерді енгізу қателері';
			this.translation['askNextStep'] = 'Мәліметтерді ары қарай өңдеуге жіберуге сенімдісіз бе?';
			this.translation['ignore'] = 'Елемеу';
			this.translation['error'] = 'Қате';
			this.translation['wait'] = 'Күте тұрыңыз...';
			this.translation['close'] = 'Жабу';
			this.translation['saving'] = 'Мәліметтерді сақтау...';
			this.translation['canceling'] = 'Өзгерiстерді болдырмау...';
			this.translation['removeProcess'] = 'Тапсырманы жоюға сенімдісіз бе?';
			this.translation['deleting'] = 'Жою...';
			this.translation['procPerformedMessage'] = 'Процедурасы орындалуда';
			this.translation['rptGenerateMessage'] = 'Есеп қалыптастырылуда';
			this.translation['ifcNotExistMessage'] = 'Интерфейс тағайындалмаған';
			this.translation['passChange'] = 'Құпия сөзді өзгерту';
			this.translation['change'] = 'Өзгерту';
		} else { 
			this.translation['ok'] = 'OK';
			this.translation['cancel'] = 'Отмена';
			this.translation['print'] = 'Печать';
			this.translation['continue'] = 'Продолжить';
			this.translation['continue2'] = 'Продолжить ввод';
			this.translation['save'] = 'Сохранить';
			this.translation['errors'] = 'Ошибки заполнения данных';
			this.translation['saving'] = 'Сохранение данных...';
			this.translation['askNextStep'] = 'Вы уверены, что хотите передать данные в дальнейшую обработку?';
			this.translation['ignore'] = 'Игнорировать';
			this.translation['error'] = 'Ошибка';
			this.translation['wait'] = 'Подождите...';
			this.translation['close'] = 'Закрыть';
			this.translation['canceling'] = 'Отмена внесенных изменений...';
			this.translation['removeProcess'] = 'Вы уверены, что хотите удалить задачу?';
			this.translation['deleting'] = 'Удаление...';
			this.translation['procPerformedMessage'] = 'Процедура выполняется';
			this.translation['rptGenerateMessage'] = 'Отчет формируется';
			this.translation['ifcNotExistMessage'] = 'Не задан интерфейс';
			this.translation['passChange'] = 'Сменя пароля';
			this.translation['change'] = 'Изменить';
		}
	}
	
	static get(key) {
		return translation[key];
	}
}

/***/ }),

/***/ "./util.js":
/*!*****************!*\
  !*** ./util.js ***!
  \*****************/
/***/ ((__unused_webpack_module, __webpack_exports__, __webpack_require__) => {

__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   "Util": () => (/* binding */ Util),
/* harmony export */   "DataChecker": () => (/* binding */ DataChecker)
/* harmony export */ });
/* harmony import */ var _ifc_js__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./ifc.js */ "./ifc.js");
/* harmony import */ var _translation_js__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./translation.js */ "./translation.js");



class Util {
	static INFO = 0;
	static ERROR = 1;
	static WARNING = 2;
	
	static DLG_ALERT = 0;
	static DLG_CONFIRM = 10;
	
	static DLG_NO_SEND = 1;
	static DLG_TREE_FIELD = 2;
	static DLG_OPEN_AT_START = 3;
	static DLG_POPUP_IFC = 4;
	static DLG_CHANGE_PD = 5;
	static DLG_ERRORS = 6;
	static DLG_POPUP_ERRORS = 7;
	
	static alertOldZindex;
	static dialogZindex = 345000;
	static messagerZindex = 350000;
	static alertCommand = false;
	static alertIsLogout = false;
	
	static restUrl = "";

	static rnd() {
		var array = new Uint32Array(1);
		var crypto = window.crypto || window.msCrypto;
		crypto.getRandomValues(array);
		return array[0];
	};
	
	static generateGUID() {
		var array = new Uint16Array(8);
		var crypto = window.crypto || window.msCrypto;
		crypto.getRandomValues(array);
		
		var res = ('000' + array[0].toString(16)).substr(-4) +
			('000' + array[1].toString(16)).substr(-4) + '-' +
			('000' + array[2].toString(16)).substr(-4) + '-' +
			('000' + array[3].toString(16)).substr(-4) + '-' +
			('000' + array[4].toString(16)).substr(-4) + '-' +
			('000' + array[5].toString(16)).substr(-4) +
			('000' + array[6].toString(16)).substr(-4) +
			('000' + array[7].toString(16)).substr(-4);
		
		return res;
	}
	
	static parseHash(url) {
		return (function() {
				var ret = {}, seg = url.replace(/^\#/, '').split('&');
				
				for (var i=0; i < seg.length; i++) {
					if (!seg[i]) {
						continue;
					}
					var s = seg[i].split('=');
					ret[s[0]] = s[1];
				}
				return ret;
			})();
	};
	
	static makeUrl(pars) {
		var url = window.mainUrl;
		for (var prop in pars) {
			url += "&" + encodeURIComponent(prop) + "=" + encodeURIComponent(pars[prop]);
		}
		return url;
	};
	
	static delay(ms) {
	  return new Promise((resolve, reject) => {
	    setTimeout(resolve, ms);
	  });
	};
	
	static slide(msg) {
		$.messager.show({
			msg: msg,
			timeout: 8000,
			showType:'slide'
		});
	}
	static alert(msg, type, command, isLogout) {
		let _this = this;
		let language = "ru";

		_this.alertCommand = command;
		_this.alertIsLogout = isLogout;
		
		// здесь код поменял Жаркын, чтобы узнать на каком языке показывать текст =========
		if($('#topLangKz').hasClass("selected")) {
			language = "kz";
		} else {
			language = "ru";
		}

		if (msg == null || msg.length == 0) msg = language == "ru" ? "-Пустое сообщение-" : "- Бос хабарлама-";
	
		var alertTitle = '';
		var icon='';
	
		if(type == _this.ERROR){
			alertTitle = language == "ru" ? 'Ошибка' : 'Қателік';
			icon='error';
		}
		else if(type == _this.WARNING){
			alertTitle = language == "ru" ? 'Предупреждение' : 'Ескерту';
			icon='warning';
		}
		else if(type == _this.INFO){
			alertTitle = language == "ru" ? 'Уведомление' : 'Хабарлама';
			icon='info';
		}
	
		var a = msg.length;
		var width = '200';
		if (a < 20){
			width = '200';
		} else if (a<61) {
			width = '450'
		} else {
			width = '600'
		}
		msg = "<div style='text-align:left'>" + (msg.replace ? msg.replace(/\n/g, '<br/>') : msg) + "</div>";
	
		var oldZindex = $.fn.window.defaults.zIndex;
		_this.alertOldZindex = oldZindex;
		$.fn.window.defaults.zIndex = _this.messagerZindex++;
		$.messager.defaults.zIndex = ++_this.messagerZindex;
	
		_ifc_js__WEBPACK_IMPORTED_MODULE_0__.InterfaceController.popDlg.push(0);
		_ifc_js__WEBPACK_IMPORTED_MODULE_0__.InterfaceController.popDlgType.push(_this.DLG_ALERT);
		$.messager.alert({
			closable: false,
			title: alertTitle,
	        msg:msg,
	        width: width,
	        height: a>3000 ? 500 : 'auto',
	        icon:icon,
	        showType:null,
	        fn: function(e){
	        	if(_ifc_js__WEBPACK_IMPORTED_MODULE_0__.InterfaceController.popDlgType[_ifc_js__WEBPACK_IMPORTED_MODULE_0__.InterfaceController.popDlgType.length-1] == _this.DLG_ALERT){
	        		_ifc_js__WEBPACK_IMPORTED_MODULE_0__.InterfaceController.popDlg.pop();
		        	_ifc_js__WEBPACK_IMPORTED_MODULE_0__.InterfaceController.popDlgType.pop();
	        	}
	        	$.fn.window.defaults.zIndex = oldZindex;
	        	if (command) {
	        		if (isLogout) {
	        			_this.logout();
	        		} else {
	        			_this.query({"alert":"0"});
	        		}
	        	}
	        }
	    });
	}
	
	static confirmMessage(msg, resolve) {
		let _this = this;
		let myTitle = "Сообщение",
			okay = "Да",
			cancel = "Отмена";

		var a = msg.length;
		var width = '200';
		if (a<20){
			width = '200';
			msg = "<div style='text-align:left'>"+msg+"</div>";
		} else if (a<61) {
			width = '450'
		} else {
			width = '600'
		}
		var oldZindex = $.fn.window.defaults.zIndex;
		_this.alertOldZindex = oldZindex;
	
		$.fn.window.defaults.zIndex = _this.messagerZindex++;
		$.messager.defaults.zIndex = ++_this.messagerZindex;
		
		if (typeof resolve !== "function") {
			_ifc_js__WEBPACK_IMPORTED_MODULE_0__.InterfaceController.popDlg.push(0);
			_ifc_js__WEBPACK_IMPORTED_MODULE_0__.InterfaceController.popDlgType.push(_this.DLG_CONFIRM);
		}
		// здесь код поменял Жаркын, чтобы узнать на каком языке показывать текст =========
		if($('#topLangKz').hasClass("selected")) {
			myTitle = "Хабарлама";
			okay = "Ия";
			cancel = "Болдырмау";
		} else {
			myTitle = "Сообщение";
			okay = "Да";
			cancel = "Отмена";
		}

		$.messager.confirm({
			title: myTitle,
			ok: okay,
			cancel: cancel,
	        msg:msg,
	        width: width,
	        height: a>3000 ? 500 : 'auto',
	        icon:'question',
	        showType:null,
	        fn: (typeof resolve === "function") ? resolve : function(e){
	        	$.fn.window.defaults.zIndex = oldZindex;
	        	if(_ifc_js__WEBPACK_IMPORTED_MODULE_0__.InterfaceController.popDlgType[_ifc_js__WEBPACK_IMPORTED_MODULE_0__.InterfaceController.popDlgType.length-1] == _this.DLG_CONFIRM){
	        		_ifc_js__WEBPACK_IMPORTED_MODULE_0__.InterfaceController.popDlg.pop();
		        	_ifc_js__WEBPACK_IMPORTED_MODULE_0__.InterfaceController.popDlgType.pop();
	        	}
	    		if (e) {
	    			_this.query({"confirm":"3"});
	    		} else {
	    			_this.query({"confirm":"4"});
	    		}
	    	}
	    });
    }
	
	static blockPage(title) {
		if (typeof title == 'undefined') {
			title = _translation_js__WEBPACK_IMPORTED_MODULE_1__.Translation.translation.wait;
		}
		$('body').block({
			message: '<img src="'+window.contextName+'/jsp/media/img/loader.gif"><h1 style="color:#fff;font-size:16px;margin-top:10px;">'+title+'</h1>', 
			overlayCSS: {
				backgroundColor: '#000',
				opacity: 0.3,
				cursor: 'wait'
			},
			css: {
				border: 'none',
				width: '10%',
				padding: '15px',
				backgroundColor: '#000',
				'border-radius': '10px',
				color: '#fff'
			},
		});
	};
	
	static blockDialog(dlg, title) {
		if (typeof title == 'undefined') {
			title = _translation_js__WEBPACK_IMPORTED_MODULE_1__.Translation.translation.wait;
		}
		dlg.block({
			message: '<img src="'+window.contextName+'/jsp/media/img/loader.gif"><h1 style="color:#fff;font-size:16px;margin-top:10px;">'+title+'</h1>', 
			overlayCSS: {
				backgroundColor: '#000',
				opacity: 0.3,
				cursor: 'wait'
			},
			css: {
				border: 'none',
				width: '10%',
				padding: '15px',
				backgroundColor: '#000',
				'border-radius': '10px',
				color: '#fff'
			},
		});
	};

	static logErrorInfo(msg, err) {
		console.log(msg);
		
		if (console.trace)
			console.trace();
		if (err.stack)
			console.log(err.stack);
	}

	static sanitizeHtml(s) {
		return this.encodeHTML(s).replace('/\r/g', '').replace(/\n/g, '');
	}
	
	static encodeHTML(s) {
	    return s.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;').replace(/'/g, '&apos;')
	    		.replace(/\//g,'&#47;').replace(/\\/g,'&#92;');
	}
	static openDocument(id, ext, fn) {
		var url = Util.restUrl;
		if (ext == null && fn == null) {
			url += "&trg=frm&cmd=opf&fn=" + encodeURIComponent(id);
			if (url.indexOf("/") > 0) url = "/" + url;
			url += "&rnd=" + Util.rnd();
		} else {
			url += "&sfunc&cls=XmlUtil&name=getHelpFile&ext=" + ext + "&fn=" + encodeURIComponent(fn) + "&arg0=" + id + "&rnd=" + Util.rnd();
		}
		$('#report_frame').attr('src', url);
	}

	static query(params) {
		return fetch(this.restUrl, {
			method: 'POST',
	    	credentials: 'include',
			body: $.param(params)
	    });
	}

	static post2(par) {
		let _this = this; 
		return new Promise((resolve, reject) => {
			$.post(_this.restUrl, par, resolve, 'json');
		});
	}

	
	static post(par) {
		let _this = this; 
		return new Promise((resolve, reject) => {
			_this.query(par).then(response => {
				response.json().then(json => {
					DataChecker.checkData(json).then(data => {
						resolve(data);
					});
				});
			});
		});
	}

	static logout() {
		this.post({"cmd":"ext", "json":1}).then(data => {
			window.location.href = window.contextName + "/qyzmet/login.jsp?rnd=" + Util.rnd();
		});
	}
	
	static playNoteSound() {
	}
	
	static b64DecodeUnicode(str) {
	    return decodeURIComponent(atob(str).split('').map(function(c) {
	        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
	    }).join(''));
	}
	
	static b64EncodeUnicode(str) {
	    return btoa(encodeURIComponent(str).replace(/%([0-9A-F]{2})/g,
	        function toSolidBytes(match, p1) {
	            return String.fromCharCode('0x' + p1);
	    }));
	}
}

class DataChecker {
	static checkData(data) {
		return new Promise((resolve, reject) => {
			if (data && data.session && data.session == "off") {
				window.location.href = window.contextName + "/qyzmet/login.jsp?rnd=" + Util.rnd();
				reject(data);
			}
			resolve(data);
		});
	}
}

/***/ })

/******/ 	});
/************************************************************************/
/******/ 	// The module cache
/******/ 	var __webpack_module_cache__ = {};
/******/ 	
/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {
/******/ 		// Check if module is in cache
/******/ 		var cachedModule = __webpack_module_cache__[moduleId];
/******/ 		if (cachedModule !== undefined) {
/******/ 			return cachedModule.exports;
/******/ 		}
/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = __webpack_module_cache__[moduleId] = {
/******/ 			// no module.id needed
/******/ 			// no module.loaded needed
/******/ 			exports: {}
/******/ 		};
/******/ 	
/******/ 		// Execute the module function
/******/ 		__webpack_modules__[moduleId](module, module.exports, __webpack_require__);
/******/ 	
/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}
/******/ 	
/************************************************************************/
/******/ 	/* webpack/runtime/define property getters */
/******/ 	(() => {
/******/ 		// define getter functions for harmony exports
/******/ 		__webpack_require__.d = (exports, definition) => {
/******/ 			for(var key in definition) {
/******/ 				if(__webpack_require__.o(definition, key) && !__webpack_require__.o(exports, key)) {
/******/ 					Object.defineProperty(exports, key, { enumerable: true, get: definition[key] });
/******/ 				}
/******/ 			}
/******/ 		};
/******/ 	})();
/******/ 	
/******/ 	/* webpack/runtime/hasOwnProperty shorthand */
/******/ 	(() => {
/******/ 		__webpack_require__.o = (obj, prop) => (Object.prototype.hasOwnProperty.call(obj, prop))
/******/ 	})();
/******/ 	
/******/ 	/* webpack/runtime/make namespace object */
/******/ 	(() => {
/******/ 		// define __esModule on exports
/******/ 		__webpack_require__.r = (exports) => {
/******/ 			if(typeof Symbol !== 'undefined' && Symbol.toStringTag) {
/******/ 				Object.defineProperty(exports, Symbol.toStringTag, { value: 'Module' });
/******/ 			}
/******/ 			Object.defineProperty(exports, '__esModule', { value: true });
/******/ 		};
/******/ 	})();
/******/ 	
/************************************************************************/
var __webpack_exports__ = {};
// This entry need to be wrapped in an IIFE because it need to be isolated against other modules in the chunk.
(() => {
/*!******************!*\
  !*** ./index.js ***!
  \******************/
__webpack_require__.r(__webpack_exports__);
/* harmony export */ __webpack_require__.d(__webpack_exports__, {
/* harmony export */   "PortletApp": () => (/* reexport safe */ _main_js__WEBPACK_IMPORTED_MODULE_0__.PortletApp),
/* harmony export */   "InterfaceController": () => (/* reexport safe */ _ifc_js__WEBPACK_IMPORTED_MODULE_1__.InterfaceController),
/* harmony export */   "Util": () => (/* reexport safe */ _util_js__WEBPACK_IMPORTED_MODULE_2__.Util),
/* harmony export */   "Login": () => (/* reexport safe */ _login_js__WEBPACK_IMPORTED_MODULE_3__.Login)
/* harmony export */ });
/* harmony import */ var _main_js__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./main.js */ "./main.js");
/* harmony import */ var _ifc_js__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./ifc.js */ "./ifc.js");
/* harmony import */ var _util_js__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./util.js */ "./util.js");
/* harmony import */ var _login_js__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./login.js */ "./login.js");







})();

or3web = __webpack_exports__;
/******/ })()
;
//# sourceMappingURL=or3-module.js.map