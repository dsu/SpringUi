package pl.springui.components;

import java.util.List;

import pl.springui.http.UiCtx;

public interface UiComponentI extends HTMLRenderer {

	void applyRequest();

	void clearChildren();

	void clearPhases();

	String executePhases();

	List<UiComponentI> getChildren();

	/**
	 * Id in JS/CSS. Naming convention: http://getbem.com/introduction/ Block -
	 * parentPath component
	 */
	String getClientId();

	UiComponentI getParent();

	boolean isVisible();

	/**
	 * Application logic
	 */
	void process();

	/**
	 * If not null redirect to this URL before rendering anything (I will not work
	 * with Ajax)
	 * 
	 * @return
	 */
	String redirect();

	String renderPlaceHolder();

	/**
	 * Register children or load children from the tree
	 */
	void restoreView();

	void setClientId(String clientId);

	void setCtx(UiCtx ctx);

	void setParent(UiComponentI uiComponent);

	void setVisible(boolean visible);

}