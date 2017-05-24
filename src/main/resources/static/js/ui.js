//http://ourcodeworld.com/articles/read/37/how-to-create-your-own-javascript-library
(function(window) {

	console.log('spring ui...');


	_viewguid = Cookies.get('VIEW_GUID');
	Cookies.remove('VIEW_GUID'); //clear
	log('_viewguid = ' + _viewguid);

	function springUi() {
		var _springUiObject = {};


		//TODO osobno id do rerenderowania, applyRequest, processowania
		_springUiObject.load = function(options) {
			var defaults = {
				showLoading : false,
				ids : [], //id of elements that will be rerendered and serialized and processed if serializeIds, processIds are empty.
				serializeIds : null,
				params : null,
				serialize : function(id) {
					//serialization function
					return $('#' + id).find('select, textarea, input').serialize();
				},
				onElementLoaded : function(el) {
					log('onElementLoaded ids:' + el.ids)
					$('#' + el.ids).addClass('animated fadeInLeft')
				}
			}


			var settings = $.extend({}, defaults, options);



			log('settings:', settings);
			log('oroginal serializeIds:', settings.serializeIds);
			log('null arr:', checkNull([]));

			//serialize

			if (checkNull(settings.serializeIds)) {
				log('serializeIds defaults to ids')
				settings.serializeIds = settings.ids;
			}

			log('serializeIds:', settings.serializeIds);

			$.each(settings.serializeIds, function(i, item) {
				log("serializing item: ", item);
				settings.params = appendParam(settings.params, settings.serialize(item));
			});


			//append componet ids

			var idsParam = jQuery.param({
				"ids" : settings.ids
			});

			settings.params = appendParam(settings.params, idsParam);
			//append view id
			settings.params = appendParam(settings.params, 'viewguid=' + _viewguid);


			log(settings.params);
			//[{id:x,js:x, html:x}]

			$.ajax({
				type : 'POST',
				url : '/ajax',
				data : settings.params,
				dataType : 'json',
				success : function(data) {
					log("ajax resonse: ");
					log(data);
					$.each(data, function(index, element) {

						log("refreshing: ", element.ids);
						log("js: ", element.js);
						log("html: ", element.html);

						var el = $('#' + element.ids);

						if (el.length) {
							el.replaceWith(element.html);
							settings.onElementLoaded(element);
						} else {
							warn('No element of ' + element.ids);
						}


						eval(element.js); //?

					//$('body').append($('<div>', {
					//	text : element.name
					//}));
					});
				},

				error : function(request, status, error) {
					jsonValue = jQuery.parseJSON(request.responseText);
					alert(jsonValue.error + " : " + jsonValue.message);
				}
			});
		};

		return _springUiObject;
	}

	AMP = '&';

	if (typeof console === "undefined") {
		console = {};
		console.log = function() {
			return;
		}
	}

	function appendParam(string, param) {
		if (checkNull(string) || checkNull(string.trim()) || string.slice(-1) === AMP) {
			return string + param;
		} else {
			return string + AMP + param;
		}

	}

	function warn(msg, object) {
		console.log('WARN: ' + msg);
		if (!checkNull(object)) {
			console.log(object);
		}
	}

	function log(msg, object) {
		console.log(msg);
		if (!checkNull(object)) {
			console.log(object);
		}
	}

	function checkNull(s) {
		return (s === undefined || s === null || s === '');
	}

	// We need that our library is globally accesible, then we save in the window
	if (typeof (window.Ui) === 'undefined') {
		window.Ui = springUi();
	}
})(window);

//Ui.load();