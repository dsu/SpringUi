//http://ourcodeworld.com/articles/read/37/how-to-create-your-own-javascript-library

var localization = {
	reload : 'Ciasteczka są zablokowane, strona zostanie odświeżona'
};

(function(window) {

	AMP = '&';

	function replaceContent(index, element) {

		if (!checkNull(element.ids)) {
			log("Replacing content for element : " + element.ids);
			var el = $('#' + element.ids);
			if (el.length) {
				el.replaceWith(element.html);
			} else {
				warn('No element with id of ' + element.ids);
			}
		}else{
			log("The element from server is empty");
		}
	}

	function appendParam(string, param) {

		if (checkNull(string)) {
			string = "";
		}

		if (checkNull(string.trim()) || string.slice(-1) === AMP) {
			return string + param;
		} else {
			return string + AMP + param;
		}

	}

	function loaderOn() {
		$(".ui-ajax-loader").show();
	}

	function loaderOff() {
		$(".ui-ajax-loader").hide();
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

	function scrollTop() {
		$("html, body").animate({
			scrollTop : 0
		}, "slow");
	}

	function checkNull(s) {
		return (s === undefined || s === null || s === '' || s === 'undefined');
	}

	if (typeof Cookies != "undefined") {
		if (checkNull(Cookies.get())) {
			alert(localization.reload);
			location.reload();
			throw 'Cookies are not available';
		}
	} else {
		log('js-cookie not enabled')
	}

	if (typeof console === "undefined") {
		console = {};
		console.log = function() {
			return;
		}
	}

	function springUi() {
		var _springUiObject = {};

		$(function() {
			// --------------- LOAD ---------------
			_springUiObject.checkNull = checkNull;
			_springUiObject.scrollTop = scrollTop;
			_springUiObject.warn = warn;
			_springUiObject.log = log;
			_viewguid = undefined;

			if (typeof Cookies != "undefined") {
				_viewguid = Cookies.get('VIEW_GUID');
				log("cookie view id : " + _viewguid);
			}

			if (checkNull(_viewguid)) {
				_viewguid = $('body').attr('data-view-id');
			}

			if (checkNull(_viewguid)) {
				_viewguid = $('.ui-root').first().attr('data-view-id');
			}

			if (checkNull(_viewguid)) {
				alert(localization.reload);
				location.reload(true);
				throw 'View cookie is not available';
			}

			if (typeof Cookies != "undefined") {
				Cookies.remove('VIEW_GUID'); // clear
				log('_viewguid cleared = ' + _viewguid);
			}

		});

		// ----- other methods ------

		function appendContent(index, element) {

			log("appending: ", element.ids);

			var el = $('#' + element.ids + " .ui-appendto");

			if (el.length) {
				// ui-appendable w szablonie oznacz element ta klasa aby dopisac
				// tylko ta czesc template
				el.after($(element.html).find('.ui-appendable'));
			} else {
				warn('No element with id of ' + element.ids);
			}

			// additional element to replace
			var replaceEl = $('#' + element.ids + " .ui-replace");

			log("replace el: " + replaceEl.length)
			if (replaceEl.length) {
				// ui-appendable w szablonie oznacz element ta klasa aby dopisac
				// tylko ta czesc template replaceWith
				replaceEl.replaceWith($(element.html).find('.ui-replace'));
			} else {
				log('No element for replacement  ' + element.ids);
			}

		}

		// TODO osobno id do rerenderowania, applyRequest, processowania
		_springUiObject.load = function(options) {
			var defaults = {
				showLoading : false,
				ids : [], // id of elements that will be rerendered and
				// serialized and processed if serializeIds,
				// processIds are empty.

				// id of elements to re render
				refreshIds : [],
				listeners : [],
				actions : [],
				serializeIds : null,
				params : null,
				callbackBeforeLoad : null,
				callbackAfterLoad : null,
				withhtml : 'replace',
				serialize : function(id) {
					// serialization function
					return $('#' + id).find('select, textarea, input')
							.serialize();
				},
				onElementLoaded : function(el) {
					log('onElementLoaded ids:' + el.ids)
					$('#' + el.ids).addClass('animated fadeIn')
				}
			}

			var settings = $.extend({}, defaults, options);

			log('settings:', settings);
			log('oroginal serializeIds:', settings.serializeIds);
			log('null arr:', checkNull([]));

			// serialize

			if (checkNull(settings.serializeIds)) {
				log('serializeIds defaults to ids')
				settings.serializeIds = settings.ids;
			}

			// SERIALIZE
			log('serializeIds:', settings.serializeIds);

			$.each(settings.serializeIds, function(i, item) {
				log("serializing item: ", item);
				settings.params = appendParam(settings.params, settings
						.serialize(item));
			});

			// append componet ids

			var idsParam = jQuery.param({
				"ids" : settings.ids.concat(settings.refreshIds)
			});

			settings.params = appendParam(settings.params, jQuery.param({
				listeners : settings.listeners
			}));

			settings.params = appendParam(settings.params, jQuery.param({
				actions : settings.actions
			}));

			settings.params = appendParam(settings.params, idsParam);
			// append view id
			settings.params = appendParam(settings.params, 'viewguid='
					+ _viewguid);

			log(settings.params);
			// [{id:x,js:x, html:x}]

			$.ajax({
				type : 'POST',
				url : '/ajax',
				data : settings.params,
				dataType : 'json',
				success : function(data) {
					log("ajax resonse: ");

					if (!checkNull(settings.callbackBeforeLoad)) {
						settings.callbackBeforeLoad();
					}

					log(data);
					$.each(data,
							function(index, element) {

								log("response element: ", element);
								
								if (settings.withhtml == 'replace') {
									replaceContent(index, element);
								} else if (settings.withhtml == 'append') {
									appendContent(index, element);
								} else {
									log('uknown replace function: '
											+ settings.withhtml)
								}

								var el = $('#' + element.ids);
								if (el.length) {
									settings.onElementLoaded(element);
								}

								
								eval(element.js); 

							});

					if (!checkNull(settings.callbackAfterLoad)) {
						settings.callbackAfterLoad();
					}

				},
				beforeSend : function() {
					loaderOn();
				},
				complete : function() {
					loaderOff();
				},
				error : function(request, status, error) {
					log(request.status);
					if (request.status == 418) {
						location.reload(true);
						console.log('view has expired - reloading')
						return;
					}
					log(request);
					log(status);
					log(error);
					jsonValue = jQuery.parseJSON(request.responseText);
					alert(jsonValue.error + " : " + jsonValue.message);
				}
			});

			// for firefox
			return false;
		};

		// ----- other methods ------

		return _springUiObject;
	}

	// We need that our library is globally accesible, then we save in the
	// window
	if (typeof (window.Ui) === 'undefined') {
		window.Ui = springUi();
	}
	$(function() {
		// usuwanie pustych kolumn w menu
		$('.col-menu-links').each(function() {
			if ($(this).find('ul').children().length == 0) {
				var parent = $(this).parents('ul.dropdown-menu').first();
				if (parent) {
					parent.width(parent.width() - 150)
					$(this).remove();
				}
			}

		});
	})

	cscriptItemClicked = function(itemId, opinion) {
		var fb = $("#user_feedback_" + itemId).val();

		$.post('/cscript_click/' + itemId, "op=" + opinion + AMP + 'fb=' + fb,
				function(data, status, jhr) {

				});

		if (opinion != null) {
			if (1 == opinion || 2 == opinion) {
				$('#opinion_box_' + itemId).hide();
			}
		}
	}

	cscriptItemClickedPrompt = function(itemId, opinion) {
		$("#qwhy_" + itemId).show();
		$("#user_feedback_" + itemId).show();
		$("#swhy_" + itemId).show();
		$("#okbtn_" + itemId).hide();
		$("#nokbtn_" + itemId).hide();

	}

	clearButtonsAtLevel = function(levelId) {
		$('.citemlvl_' + levelId).css('border', '1px solid transparent');
	}

	clearNodesDeeperThen = function(d) {
		$('.alert_csc').each(function(idx, el) {
			if ($(el).attr('depth') > d) {
				$('#fin_txt_' + $(el).attr('csc_item_id')).hide();
				$('#fin_nok_txt_' + $(el).attr('csc_item_id')).hide();
				clearButtonsAtLevel($(el).attr('csc_item_id'));
				$(el).hide();
			}
		});
	}

	scriptItemClickVert = function(btn, targetItemId, levelId, depth, opinion) {
		// alert('depth: ' + depth + ' btn: ' + btn + ' click target: ' +
		// targetItemId + ' levelId: ' + levelId + ' opinion: ' + opinion);

		var itid = targetItemId;
		if ('finish' == targetItemId || 'finish_nok' == targetItemId) {
			itid = levelId;
		}
		$.post('/cscript_click/' + itid, "op=" + opinion + AMP + 'fb=',
				function(data, status, jhr) {
				});

		clearButtonsAtLevel(levelId);
		clearNodesDeeperThen(depth);
		$(btn).css('border', '4px solid #2e6da4');
		$('#fin_txt_' + levelId).hide();
		$('#fin_nok_txt_' + levelId).hide();
		$('#csc_item_' + targetItemId).show();
		if ('finish' == targetItemId) {
			$('#fin_txt_' + levelId).show();
		}
		if ('finish_nok' == targetItemId) {
			$('#fin_nok_txt_' + levelId).show();
		}
		$(document)
				.scrollTop($('#csc_item_' + targetItemId).offset().top - 200);
	}

	cscriptFeedback = function(itemId) {
		var fb = $("#user_feedback_" + itemId).val();
		$.post('/cscript_click/' + itemId, "op=0" + AMP + 'fb=' + fb, function(
				data, status, jhr) {
			if (status == 'success') {
				$('#user_feedback_' + itemId).hide();
				$('#sfeed_' + itemId).hide();
				$('#feed_txt_' + itemId).show();
			}
		});
	}

})(window);

// Ui.load();
