var vectorsMap = {};
var selectionsMap = {};

function createMap(uid, bounds, transform) {
	/*
	if (transform) {
	    delete selectionsMap[uid];
	    // Переводим координаты в другую систему
		bounds = ol.proj.transformExtent(bounds, 'EPSG:4326','EPSG:3857');
	}
	var mapElement = document.getElementById(uid);
	mapElement.innerHTML = '';
	
    // Слой адресов
    var addresses = new ol.layer.Tile({
    	name: 'Addresses',
		source: new ol.source.TileWMS({
        	url: gisServerUrl + '/nyc/wms',
	        params: {
    	      'FORMAT': 'image/png8', 
        	  'VERSION': '1.1.1',
	          tiled: true,
    	      STYLES: 'number',
        	  LAYERS: 'nyc:addrs'
    	    }
		})
    });

    // Слой спутник
    var satellite = new ol.layer.Tile({
    	name: 'Satellite',
		preload: Infinity,
		source: new ol.source.BingMaps({
        	key: 'AuydQjZggYuwKfCMY808Wmzcil6xXHWOkb-t20IZ8ivHXq6ZtaZ5DDZrbRJ6BQq4',
	        imagerySet: 'Aerial',
    	})
    });

    // Слой зданий
    var buildings = new ol.layer.Tile({
    	name: 'Buildings',
      	source: new ol.source.TileWMS({
        	url: gisServerUrl + '/nyc/wms',
	        params: {
				'FORMAT': 'image/png8', 
				'VERSION': '1.1.1',
				tiled: true,
				STYLES: 'buildings',
				LAYERS:'nyc:buildings'
        	}
      	})
    });

    // Слой земельных участков
    var lands = new ol.layer.Tile({
    	name: 'Lands',
		source: new ol.source.TileWMS({
			url: gisServerUrl + '/nyc/wms',
			params: {
				'FORMAT': 'image/png8', 
				'VERSION': '1.1.1',
				tiled: true,
				STYLES: 'lands',
				LAYERS:'nyc:lands'
        	}
      	})
    });
    
    // Слой улиц
    var roads = new ol.layer.Tile({
    	name: 'Roads',
      source: new ol.source.TileWMS({
        url: gisServerUrl + '/nyc/wms',
        params: {
          'FORMAT': 'image/png8', 
          'VERSION': '1.1.1',
          tiled: true,
          STYLES: 'roads',
          LAYERS: 'nyc:roads'
        }
      })
    });

    // Слой для редактирования
    var source = new ol.source.Vector({wrapX: false});
    var vector = new ol.layer.Vector({
      name: 'Editing',
      source: source
    });

    // Кнопка для включения/отключения режима рисования
    var DrawControl = function(opt_options) {

      var options = opt_options || {};

      var button = document.createElement('button');
      button.innerHTML = 'D';

      var this_ = this;
      var handleMode = function() {
        addDrawInteraction();
      };

      button.addEventListener('click', handleMode, false);
      button.addEventListener('touchstart', handleMode, false);

      var element = document.createElement('div');
      element.className = 'ol-custom-draw ol-unselectable ol-control';
      element.appendChild(button);

      ol.control.Control.call(this, {
        element: element,
        target: options.target
      });

    };
    ol.inherits(DrawControl, ol.control.Control);

    // Кнопка для включения/отключения режима редактирования
    var EditControl = (function(Control) {
      function EditControl(opt_options) {

        var options = opt_options || {};

        var button = document.createElement('button');
        button.innerHTML = 'M';

        var this_ = this;
        var handleMode = function() {
          addModifyInteraction();
        };

        button.addEventListener('click', handleMode, false);
        button.addEventListener('touchstart', handleMode, false);

        var element = document.createElement('div');
        element.className = 'ol-unselectable ol-control';
        element.appendChild(button);

        ol.control.Control.call(this, {
          element: element,
          target: options.target
        });

      };
      if ( Control ) EditControl.__proto__ = Control;
      EditControl.prototype = Object.create( Control && Control.prototype );
      EditControl.prototype.constructor = EditControl;

      return EditControl;
    }(ol.control.Control));

      var RotateNorthControl = (function (Control) {
        function RotateNorthControl(opt_options) {
          var options = opt_options || {};

          var button = document.createElement('button');
          button.innerHTML = 'N';

          var element = document.createElement('div');
          element.className = 'rotate-north ol-unselectable ol-control';
          element.appendChild(button);

          Control.call(this, {
            element: element,
            target: options.target
          });

          button.addEventListener('click', this.handleRotateNorth.bind(this), false);
        }
        if ( Control ) RotateNorthControl.__proto__ = Control;
        RotateNorthControl.prototype = Object.create( Control && Control.prototype );
        RotateNorthControl.prototype.constructor = RotateNorthControl;

        RotateNorthControl.prototype.handleRotateNorth = function handleRotateNorth () {
          this.getMap().getView().setRotation(0);
        };

        return RotateNorthControl;
      }(ol.control.Control));

    var map = new ol.Map({
      controls: ol.control.defaults().extend([ new RotateNorthControl() ]),
      target: uid,
      layers: [
        satellite,//new ol.layer.Tile({source: new ol.source.OSM()}),
        roads,
        lands,
        buildings,
        addresses,
        vector,
      ],
      view: new ol.View({
        center: [0, 0],
        zoom: 4,
        projection: new ol.proj.Projection({
          code: 'EPSG:3857',
          units: 'm',
          axisOrientation: 'neu'
        })
      })
    });
    
    if (bounds != null) {
    	map.getView().fit(bounds, map.getSize());
    	boundsMap[uid] = bounds;
	}
	gisMapsMap[uid] = map;
	vectorsMap[uid] = vector;
	
	if (bounds != null && gisMapsMap[uid].getSize()[0] > 0) {
		var bounds2 = gisMapsMap[uid].getView().calculateExtent(gisMapsMap[uid].getSize());
		var coeffs = [bounds2[0]/bounds[0], bounds2[1]/bounds[1], bounds2[2]/bounds[2], bounds2[3]/bounds[3]];
		gisCoeffsMap[uid] = coeffs;
	} else {
		delete gisCoeffsMap[uid];
	}

	if (selectionsMap[uid] != null) {
	    var feature = new ol.Feature({
	        geometry: new ol.geom.Polygon(selectionsMap[uid]),
	    });
	    vector.getSource().addFeature(feature);
	}

	map.on('singleclick', function(evt) {
	  var view = map.getView();
      var viewResolution = view.getResolution();
      var buildingsURL = buildings.getSource().getGetFeatureInfoUrl(evt.coordinate, viewResolution, view.getProjection(), {'INFO_FORMAT': 'application/json', 'FEATURE_COUNT': 50});
      var adressesURL = addresses.getSource().getGetFeatureInfoUrl(evt.coordinate, viewResolution, view.getProjection(), {'INFO_FORMAT': 'application/json', 'FEATURE_COUNT': 50});
      var landsURL = lands.getSource().getGetFeatureInfoUrl(evt.coordinate, viewResolution, view.getProjection(), {'INFO_FORMAT': 'application/json', 'FEATURE_COUNT': 50});
	  var par = {};
	  par["cmd"] = 'gisOnSelectObjects';
	  par["uid"] = uid;
	  par["json"] = 1;
	  par["buildingsURL"] = buildingsURL;
	  par["adressesURL"] = adressesURL;
	  par["landsURL"] = landsURL;
	  $.ajax({
			type : 'POST',
			url : window.contextName + "/main?guid=" + guid + "&rnd=" + rnd(),
			data : par,
			success : function(data) {},
			dataType : 'json',
			async : false
		});
    });

    var draw, select, modify;

    function addDrawInteraction() {
      map.removeInteraction(select);
      map.removeInteraction(modify);
      var value = 'Polygon';
      if (value !== 'None') {
        draw = new ol.interaction.Draw({
          source: source,
          type: (value) // @type {ol.geom.GeometryType} 
        });
        map.addInteraction(draw);
      }
    }

    function addModifyInteraction() {
      // remove draw interaction
      map.removeInteraction(draw);
      // create select interaction
      select = new ol.interaction.Select({
        layers: function(vector_layer) {
          return vector_layer.get('name') === 'Editing';
        }
      });
      map.addInteraction(select);

      var selected_features = select.getFeatures();
      // when a feature is selected...
      selected_features.on('add', function(event) {
      });

      modify = new ol.interaction.Modify({
         features: selected_features,
         // delete vertices by pressing the SHIFT key
         deleteCondition: function(event) {
           return ol.events.condition.shiftKeyOnly(event) &&
             ol.events.condition.singleClick(event);
         }
       });
       map.addInteraction(modify);

      var oldKeyUp = document.onkeyup;
      document.onkeyup = function(event) {
        if (event.keyCode == 46) {
          // remove all selected features from select_interaction and Editing
          selected_features.forEach(function(selected_feature) {
            var selected_feature_id = selected_feature.getId();
            // remove from select_interaction
            selected_features.remove(selected_feature);
            // features aus vectorlayer entfernen
            var vectorlayer_features = vector.getSource().getFeatures();
            vectorlayer_features.forEach(function(source_feature) {
              var source_feature_id = source_feature.getId();
              if (source_feature_id === selected_feature_id) {
                // remove from Editing
                vector.getSource().removeFeature(source_feature);
              }
            });
        });
        }
      }
    }*/
}

function setBounds(uid, bounds) {
	bounds = ol.proj.transformExtent(bounds, 'EPSG:4326','EPSG:3857');
	gisMapsMap[uid].getView().fit(bounds, map.getSize());
}

function setLayerVisible(uid, layerName, isVisible) {
	let map = window['map_' + uid];
	if (map != null) {
		map.showLayer(layerName, isVisible);
	}
}

function __setLayerVisible(uid, layerName, isVisible) {
	map = gisMapsMap[uid];
	layers = map.getLayers().getArray();
	$.each(layers, function(i, layer) {
		name = layer.get("name");
		if (name == layerName) {
			layer.setVisible(isVisible);
			return false;
		}
	});
}

function setLayerOpacity(uid, layerName, opacity) {
	map = gisMapsMap[uid];
	layers = map.getLayers().getArray();
	$.each(layers, function(i, layer) {
		name = layer.get("name");
		if (name == layerName) {
			layer.setOpacity(opacity);
			return false;
		}
	});
}


function setLayers(uid, newLayers) {
	map = gisMapsMap[uid];
	
	// Удаляем существующие слои
	curLayers = map.getLayers().getArray();
	$.each(curLayers, function(i, curLayer) {
		map.removeLayer(curLayer);
	});
	
	// Создаем новые слои
	$.each(newLayers, function(i, newLayer) {
		name = newLayer.name;
		type = newLayer.type;
		source = newLayer.source;
		if (name == "Satellite") {
			// Слой подложки
			satellite = new ol.layer.Tile({
		    	name: 'Satellite',
				preload: Infinity,
			    source: new ol.source.BingMaps({
			    	key: 'AuydQjZggYuwKfCMY808Wmzcil6xXHWOkb-t20IZ8ivHXq6ZtaZ5DDZrbRJ6BQq4',
			        imagerySet: 'Aerial',
			    })
			});
			map.addLayer(satellite);
		} else if (name == "Addresses") {
			// Слой адресов
			addresses = new ol.layer.Tile({
			   	name: 'Addresses',
				source: new ol.source.TileWMS({
					url: gisServerUrl + '/nyc/wms',
					params: {'FORMAT': 'image/png8', 'VERSION': '1.1.1', tiled: true, STYLES: 'number', LAYERS: 'nyc:astana_kazakhstan_osm_housenumbers', tilesOrigin: '7948101,6648097.5'}
			    })
			});
			map.addLayer(addresses);
		} else if (name == "Buildings") {
			// Слой зданий
			buildings = new ol.layer.Tile({
    			name: 'Buildings',
			    source: new ol.source.TileWMS({
		        	url: gisServerUrl + '/nyc/wms',
			        params: {
			          'FORMAT': 'image/png8', 
			          'VERSION': '1.1.1',
		    	      tiled: true,
		        	  STYLES: 'buildings',
			          LAYERS:'nyc:buildings',
			          tilesOrigin:'7945364.5,6639501.5'
			        }
		    	})
		    });
			map.addLayer(buildings);
		} else if (name == "Lands") {
			// Слой участков
			lands = new ol.layer.Tile({
		    	name: 'Lands',
		      	source: new ol.source.TileWMS({
			        url: gisServerUrl + '/nyc/wms',
			        params: {
			          'FORMAT': 'image/png8', 
			          'VERSION': '1.1.1',
			          tiled: true,
			          STYLES: 'lands',
			          LAYERS:'nyc:lands'
			        }
			    })
			});
			map.addLayer(lands);
		} else if (name == "Editing") {
			// Слой редактирования
			vector = new ol.layer.Vector({
			      name: 'Editing',
			      source: new ol.source.Vector({wrapX: false})
			    });
			map.addLayer(name);
		}
	});
}

function gisParseRequest(uid, json) {
	console.log('gisParseRequest: ' + uid + ', json: ' + json);

	let map = window['map_' + uid];
	if (map != null && json != null) {
		map.parseRequest(json);
	}
}

function requestFromGis(uid, json) {
	console.log('requestFromGis: ' + uid + ', json: ' + json);

	var par = {};
	par["cmd"] = 'requestFromGis';
	par["uid"] = uid;
	par["json"] = 1;
	par["body"] = JSON.stringify(json);
	$.ajax({
		type : 'POST',
		url : window.contextName + "/main?guid=" + guid + "&rnd=" + rnd(),
		data : par,
		success : function(data) {},
		dataType : 'json',
		async : false
	});
}
window['requestFromGis'] = requestFromGis;

/**
 * @param {*} wkt 
 * Например: 'POLYGON((680767.21487425 5793487.61627134,680804.708877024 5793447.73074242,680786.613906474 5793446.80150074,681059.569243689 5793176.6141648,682930.390545765 5791186.44744925,682460.663224626 5790753.40854621,682460.791333795 5790753.26356394,682460.757955872 5790753.23280712,682670.191791482 5790516.2423368,680702.773294162 5789155.94611479,680262.615644875 5789653.60993824,680515.174795243 5789850.50117689,680398.739846767 5790032.30452798,680145.329920468 5790271.98644112,679670.183020981 5789848.32776223,679245.613904825 5790207.46773992,678380.748149366 5790863.172772,678112.003281343 5793081.06219,678093.783462244 5793231.42064128,680744.98967984 5793485.66973821,680745.087548049 5793485.49424938,680767.21487425 5793487.61627134))'
 */
function setSelections(uid, wkt) {
	console.log('setSelections: ' + uid + ', vals: ' + wkt);

//	let map = window['map_' + uid];
//	if (map != null && wkt != null && wkt.length > 0) {
//		map.selectObject(wkt);
//	}
}

function __setSelections(uid, coords) {
	for (var i in coords) {
		for (var j in coords[i]) {
			var arr = [coords[i][j][0], coords[i][j][1], coords[i][j][0], coords[i][j][1]];
			arr = ol.proj.transformExtent(arr, 'EPSG:4326','EPSG:3857');
			coords[i][j] = [arr[0], arr[1]];
		}
	}
    var feature = new ol.Feature({
        geometry: new ol.geom.Polygon(coords),
    });
    vectorsMap[uid].getSource().addFeature(feature);
    selectionsMap[uid] = coords;
}