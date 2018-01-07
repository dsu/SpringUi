package pl.springui.components.js;

import org.json.JSONArray;
import org.json.JSONException;

import pl.springui.components.UiComponentI;

/**
 * Generates JS to communicate with Client API
 * 
 * @author dsu
 *
 */
public class UiJavaScripts {

	public static String hide(UiComponentI c) {
		StringBuilder sb = new StringBuilder("$('#");
		sb.append(c.getClientId());
		sb.append("').hide()");
		return sb.toString();
	}

	public static String load(String[] ids, String params) {
		StringBuilder sb = new StringBuilder("Ui.load({ids:");
		try {
			sb.append(new JSONArray(ids));
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}

		if (params != null) {
			sb.append(",params:'");
			sb.append(params);
			sb.append("'");
		}
		sb.append("});");
		return sb.toString();
	}

	public static String load(UiComponentI list) {
		return load(new String[] { list.getClientId() }, null);
	}

	public static String refresh(String clientId, String params) {
		StringBuilder sb = new StringBuilder("Ui.load({refreshIds:");
		try {
			sb.append(new JSONArray("[" + clientId + "]"));
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
		if (params != null) {
			sb.append(",params:'");
			sb.append(params);
			sb.append("'");
		}
		sb.append("});");
		return sb.toString();
	}

	public static String show(UiComponentI c) {
		StringBuilder sb = new StringBuilder("$('#");
		sb.append(c.getClientId());
		sb.append("').show()");
		return sb.toString();
	}

}
