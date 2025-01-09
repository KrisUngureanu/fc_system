var nameX = "Category";
//const OLAP_URL = "http://localhost:8080/olap/rest/";
const OLAP_URL = "http://192.168.13.170:8081/analytic/rest/";

function processOut(hoveredSeries, chart) {
	chart.series.each(function (series) {
		series.segments.each(function (segment) {
			segment.setState("default");
		})
		series.bulletsContainer.setState("default");
	});
}

function processOver(hoveredSeries, chart) {
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
function createBarSeries(chart, nodeNameY, i) {
	let field = nodeNameY.id;
	let name = nodeNameY.name;


	// Set up series
	var series = chart.series.push(new am4charts.ColumnSeries());


	series.columns.template.propertyFields.fill = "color" + i;
	series.columns.template.propertyFields.stroke = "color" + i;

	series.name = name;
	series.dataFields.valueY = field;
	series.dataFields.categoryX = nameX;
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

function create3DBarSeries(chart, field, name, i) {
	var series = chart.series.push(new am4charts.ColumnSeries3D());
	series.columns.template.propertyFields.fill = "color" + i;
	series.columns.template.propertyFields.stroke = "color" + i;
	series.name = name;
	series.dataFields.valueY = field;
	series.dataFields.categoryX = nameX;
	series.clustered = false;
	series.columns.template.tooltipText = "[bold]{name}[/]\n[font-size:14px]{categoryX}: {valueY}";
	series.columns.template.fillOpacity = 0.9;
}

function createLineSeries(chart, field, name, i) {
	var series = chart.series.push(new am4charts.LineSeries());
	series.propertyFields.fill = "color" + i;
	series.propertyFields.stroke = "color" + i;
	series.dataFields.dateX = nameX;
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
		processOver(event.target.parent.parent.parent, chart);
	});

	segment.events.on("out", function (event) {
		processOut(event.target.parent.parent.parent, chart);
	});
}

function createDonutSeries(chart, field, name) {
	// Add and configure Series
	var pieSeries = chart.series.push(new am4charts.PieSeries());
	pieSeries.slices.template.propertyFields.fill = "color";
	pieSeries.slices.template.propertyFields.stroke = "color";
	pieSeries.name = name;
	pieSeries.dataFields.value = field;
	pieSeries.dataFields.category = nameX;
	pieSeries.slices.template.stroke = am4core.color("#fff");
	pieSeries.slices.template.tooltipText = "[bold]{name}[/]\n[font-size:14px]{category}: {value}";

	// Disabling labels and ticks on inner circle
	pieSeries.labels.template.disabled = true;
	pieSeries.ticks.template.disabled = true;

	// Disable sliding out of slices
	pieSeries.slices.template.states.getKey("hover").properties.shiftRadius = 0;
	pieSeries.slices.template.states.getKey("hover").properties.scale = 1.0;
}

function createPieSeries(chart, field, name) {
	// Add and configure Series
	var pieSeries = chart.series.push(new am4charts.PieSeries());
	pieSeries.slices.template.propertyFields.fill = "color";
	pieSeries.slices.template.propertyFields.stroke = "color";
	pieSeries.name = name;
	pieSeries.dataFields.value = field;
	pieSeries.dataFields.category = nameX;
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

function createLineLegend(chartContainer) {
	chartContainer.chart.legend = new am4charts.Legend();
	chartContainer.chart.legend.position = "bottom";
	chartContainer.chart.legend.scrollable = true;
	chartContainer.chart.legend.itemContainers.template.events.on("over", function (event) {
		processOver(event.target.dataItem.dataContext, chartContainer.chart);
	})

	chartContainer.chart.legend.itemContainers.template.events.on("out", function (event) {
		processOut(event.target.dataItem.dataContext, chartContainer.chart);
	})
}

function createLegend(chartContainer, nodesNamesY) {
	if (chartContainer.type === "LINE") {
		createLineLegend(chartContainer);
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
            let series = chart.series.values;
            chart.legend.data = series[series.length - 1].dataItems.values;
        });
    }
}

function fillinNodes(e) {
	if (!e.children) {
		return { id: e.id, text: e.text, state: e.state, checked: e._checked, color: e.color };
	}
	var childNodes = [];
	for (var i in e.children) {
		childNodes.push(fillinNodes(e.children[i]));
	}
	return { id: e.id, text: e.text, state: e.state, children: childNodes, checked: e._checked, color: e.color };
}

function addNode(tree, node, data) {
	var i;
	for (i in tree) {
		if (tree[i].children) addNode(tree[i].children, node, data);
		if (tree[i].id != node.id) continue;
		var childNodes = [];
		for (j in data) {
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

function loadNode(node, id, dimId, loading, fact, isCombo) {
	loading = true;
	let folder = $(node.target).find('span.tree-folder');
	folder.addClass('tree-loading');
	$.ajax({
		type: "GET",
		url: OLAP_URL + "dim/dimension/" + node.id + "?fact=" + fact,
		success: function (data) {
			var oldNodes;
			if (isCombo) {
				let tree = $(id + "_" + dimId).combotree('tree');
				oldNodes = tree.tree('getRoots');
			} else {
				oldNodes = $(id).tree('getRoots');
			}
			var newNodes = oldNodes.map(fillinNodes);
			addNode(newNodes, node, data)
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

function onNodeClick(node) {
	$(this).tree('toggle', node.target);
}

function initTree(id, dimId, data, fact) {
	var loading = false;
	var beforeExpand = function (node) {
		if (!loading && !node.children) {
			loadNode(node, id, dimId, loading, fact);
		}
		return true;
	}
	$(id).tree({ cascadeCheck: false, checkbox: true, onBeforeExpand: beforeExpand, onClick: onNodeClick });
	$(id).tree('loadData', data);
}

function initComboTree(id, dimId, data, fact) {
	var loading = false;
	var beforeExpand = function (node) {
		if (!loading && !node.children) {
			loadNode(node, id, dimId, loading, fact, true);
		}
		return true;
	}
	let ul = document.createElement('ul', {
		id: id + "_" + dimId
	});
	$(id).after(ul);
	$(ul).combotree({ cascadeCheck: false, checkbox: true, onBeforeExpand: beforeExpand, onClick: onNodeClick });
	$(ul).combotree('loadData', data);
}

function loadNodes(id, fact, nodesNames, treeId, prefix, isCombo) {
	var nodes = [];
	$.ajax({
		type: "GET",
		url: OLAP_URL + "dim/dimension/" + treeId + "?fact=" + fact,
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
		initComboTree("#" + prefix + "_" + id, treeId, nodes, fact);
	} else {
		initTree("#" + prefix + "_" + id, treeId, nodes, fact);
	}
}

function initCombobox(id, data, fact, nodesNames, prefix, parentNodeId) {
	var comb = $("#" + prefix + "axes_" + id);
	comb.combobox({
		onSelect: function (item) {
			parentNodeId.id = item.id;
			parentNodeId.name = item.value;
			loadNodes(id, fact, nodesNames, item.id, prefix);
		},
		formatter: function (value) {
			var width = $(this).width();
			return $('<div/>').append($('<div style="width:' + width + 'px;cursor:pointer;">' + value.text[0] + '</>')).html();
		},
		data: data
	});
	loadNodes(id, fact, nodesNames, parentNodeId.id, prefix);
}

function initDiagramType(id, chartContainer, chartData, nodesNamesX, nodesNamesY, nameX) {
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

	comb.combobox({
		onSelect: function (item) {
			chartContainer.chart.dispose();
			chartContainer.type = item.id
			drawChart(id, chartContainer, chartData, nodesNamesX, nodesNamesY, nameX);
		},
		formatter: function (value) {
			var width = $(this).width();
			return $('<div/>').append($('<div style="width:' + width + 'px;cursor:pointer;">' + value.text[0] + '</>')).html();
		},
		data: data
	});
}

function initAxesTitle(id, parentXId, parentYId) {
	$('#axes_title_' + id).html(parentXId.name + ' / ' + parentYId.name);
}

function setShowLegend(chartContainer, showLegend) {
	chartContainer.chart.legend.disabled = !showLegend;
}

function drawChart(id, chartContainer, chartData, nodesNamesX, nodesNamesY, nameX) {
	for (i in chartData) {
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
			for (j in nodesNamesY) {
				chartData[i]['color' + j] = nodesNamesY[j].color;
			}
		}
	}

	am4core.useTheme(am4themes_animated);
	if (chartContainer.type === "BAR") {
		// Create chart instance
		chartContainer.chart = am4core.create(id, am4charts.XYChart);

		// Create axes
		var categoryAxis = chartContainer.chart.xAxes.push(new am4charts.CategoryAxis());
		categoryAxis.dataFields.category = nameX;
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
			createBarSeries(chartContainer.chart, nodesNamesY[i], i);
		}
	} else if (chartContainer.type === "PIE") {
		chartContainer.chart = am4core.create(id, am4charts.PieChart);

		for (i in nodesNamesY) {
			createPieSeries(chartContainer.chart, nodesNamesY[i].id, nodesNamesY[i].name);
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
			createLineSeries(chartContainer.chart, nodesNamesY[i].id, nodesNamesY[i].name, i);
		}

		chartContainer.chart.cursor = new am4charts.XYCursor();
		chartContainer.chart.cursor.xAxis = xAxis;

		var scrollbarX = new am4core.Scrollbar();
		scrollbarX.marginBottom = 20;
		chartContainer.chart.scrollbarX = scrollbarX;
	} else if (chartContainer.type === "3DBAR") {
		chartContainer.chart = am4core.create(id, am4charts.XYChart3D);

		var categoryAxis = chartContainer.chart.xAxes.push(new am4charts.CategoryAxis());
		categoryAxis.dataFields.category = nameX;
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
			create3DBarSeries(chartContainer.chart, nodesNamesY[i].id, nodesNamesY[i].name, i);
		}
	} else if (chartContainer.type === "DONUT") {
		// Create chart instance
		chartContainer.chart = am4core.create(id, am4charts.PieChart);

		// Let's cut a hole in our Pie chart the size of 40% the radius
		chartContainer.chart.innerRadius = am4core.percent(40);

		for (i in nodesNamesY) {
			createDonutSeries(chartContainer.chart, nodesNamesY[i].id, nodesNamesY[i].name);
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

	createLegend(chartContainer, nodesNamesY);
	setShowLegend(chartContainer, chartContainer.showLegend);
}

function loadAnalytic(id, chartContainer, idX, idY, zAxis, nodesNamesX, nodesNamesY, selectedZIds, fact, aggType, aggField) {
	blockPage("Подождите...");
	var q = { "dim1": { "id": idX.id, "value": [] }, "dim2": { "id": idY.id, "value": [] }, "fact": fact, "agg": aggType, "value": aggField };
	for (var i in nodesNamesX) {
		q["dim1"].value.push(nodesNamesX[i].id);
	}
	for (i in nodesNamesY) {
		q["dim2"].value.push(nodesNamesY[i].id);
	}
	if (zAxis && selectedZIds.length) {
		q["dim3"] = { id: zAxis.id, value: [] };
		for (i in selectedZIds) {
			q["dim3"].value.push(selectedZIds[i].id);
		}
		
	}
	var chartData;
	$.ajax({
		type: "POST",
		url: OLAP_URL + "ds/data",
		data: JSON.stringify(q),
		success: function (data) {
			chartData = data;
		},
		dataType: 'json',
		async: false
	});

	var empty = true;
	for (i in chartData) {
		for (j in chartData[i])
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
	drawChart(id, chartContainer, chartData, nodesNamesX, nodesNamesY, nameX);
	initDiagramType(id, chartContainer, chartData, nodesNamesX, nodesNamesY, nameX);

	$('body').unblock();
}

function initBtn(id) {
	$(id).linkbutton({
		text: '<span style="font-size:15px;color:#fff;text-weight:normal;">Применить</span>'
	})
}

function initTglLegBtn(id, chartContainer) {
	$(id).linkbutton({
		text: '<span style="font-size:15px;color:#fff;text-weight:normal;">' + (chartContainer.showLegend ? "Скрыть" : "Показать") + ' легенду</span>'
	})
}

function hideBtn(id) {
	var content = $('#cnt0acc_' + id);
	$(content).removeClass('expanded-v');
	$('#t0acc_' + id).css('display', 'block');
	$('#t0acc_' + id).parent().parent().css('cursor', 'pointer');
	return false;
}

function clearNodes(e) {
	if (!e.children) {
		e.checked = false;
		e._checked = false;
		return e;
	}
	e.children.map(clearNodes);
	e.checked = false;
	e._checked = false;
	return e;
}

function selectNodes(e) {
	e.checked = true;
	e._checked = true;
	return e;
}

function clearDim(id) {
	var oldNodes = $(id).tree('getRoots');
	var newNodes = oldNodes.map(clearNodes);
	$(id).tree('loadData', newNodes);
}

function selectDim(id) {
	clearDim(id);
	var oldNodes = $(id).tree('getRoots');
	var newNodes = oldNodes.map(selectNodes);
	$(id).tree('loadData', newNodes);
}

function initSelectBtns(id) {
	$('#clear_dim_x_' + id).click(() => clearDim('#x_' + id));
	$('#select_dim_x_' + id).click(() => selectDim('#x_' + id));
	$('#clear_dim_y_' + id).click(() => clearDim('#y_' + id));
	$('#select_dim_y_' + id).click(() => selectDim('#y_' + id));
}

function analyticInit(id) {
	var xAxes;
	var yAxes;
	var zAxis;
	var nodesNamesX;
	var nodesNamesY;
	var nodesNamesZ;
	var parentXId;
	var parentYId;
	var selectedZIds = [];
	var fact;
	var aggType;
	var aggField;
	var chartContainer = {};

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
		url: OLAP_URL + "dim/dimension/" + parentXId.id + "?fact=" + fact,
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
		url: OLAP_URL + "dim/dimension/" + parentYId.id + "?fact=" + fact,
		success: function (data) {
			nodesNamesY = data;
		},
		dataType: 'json',
		async: false
	});

	zAxis.forEach(filter => {
		$.ajax({
			type: "GET",
			url: OLAP_URL + "dim/dimension/" + filter.id + "?fact=" + fact,
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
		let tree = $('#filter_' + id).combotree('tree');
		let checkedZ = tree.tree('getChecked');
		if (!checkedX.length || !checkedY.length) {
			alert("Выберите измерения!");
			return;
		}

		let newNodesNamesX = [];
		let newNodesNamesY = [];
		let newSelectedZIds = [];
		for (var i in checkedX) {
			newNodesNamesX.push({ id: checkedX[i].id, name: checkedX[i].text, isLeaf: $('#x_' + id).tree('isLeaf', checkedX[i].target), color: checkedX[i].color });
		}
		for (i in checkedY) {
			newNodesNamesY.push({ id: checkedY[i].id, name: checkedY[i].text, isLeaf: $('#y_' + id).tree('isLeaf', checkedY[i].target), color: checkedY[i].color });
		}
		for (i in checkedZ) {
			newSelectedZIds.push({ id: checkedZ[i].id, name: checkedZ[i].text, isLeaf: $('#filter_' + id).tree('isLeaf', checkedZ[i].target), color: checkedZ[i].color });
		}
		if (JSON.stringify(nodesNamesX) === JSON.stringify(newNodesNamesX)
			&& JSON.stringify(nodesNamesY) === JSON.stringify(newNodesNamesY)
			&& JSON.stringify(selectedZIds) === JSON.stringify(newSelectedZIds)) {
			return;
		}
		nodesNamesX = newNodesNamesX;
		nodesNamesY = newNodesNamesY;
		selectedZIds = newSelectedZIds;
		loadAnalytic(id, chartContainer, parentXId, parentYId, zAxis, nodesNamesX, nodesNamesY, selectedZIds, fact, aggType, aggField);
		initAxesTitle(id, parentXId, parentYId);
	}
	$('#btn_' + id).click(onSelect);

	initAxesTitle(id, parentXId, parentYId);
	initBtn('#btn_' + id);
	initTglLegBtn('#tgl_leg_' + id, chartContainer);
	$('#hide_btn_' + id).click(() => hideBtn(id));
	initCombobox(id, xAxes, fact, nodesNamesX, "x", parentXId);
	initCombobox(id, yAxes, fact, nodesNamesY, "y", parentYId);
	if (zAxis.length > 0) {
		zAxis.forEach(filter => {
			loadNodes(id, fact, nodesNamesZ, filter.id, "filter", true);
		});
	}
	initSelectBtns(id);

	loadAnalytic(id, chartContainer, parentXId, parentYId, zAxis, nodesNamesX, nodesNamesY, selectedZIds, fact, aggType, aggField);

	function toggleLegClick() {
		if (chartContainer.chart.legend.disabled) {
			chartContainer.showLegend = true;
			setShowLegend(chartContainer, true);
		}
		else {
			chartContainer.showLegend = false;
			setShowLegend(chartContainer, false);
		}
		initTglLegBtn('#tgl_leg_' + id, chartContainer);
	}
	$('#tgl_leg_' + id).click(toggleLegClick);

	$('#' + id).parent().click(() => hideBtn(id));
}