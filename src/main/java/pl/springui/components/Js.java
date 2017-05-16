package pl.springui.components;

/**
 * Klasa do robienia JS z Java - takie utilsy, wiekszosc JS z plikow JS
 * oczywiscie
 * 
 * @author dsu
 *
 */
@Deprecated
public class Js {

	public static String reloadComponent(String clientId) {
		return "$( '#" + clientId + "' ).load( 'ajax' , {'ids':'" + clientId + "'});";
	}



}
