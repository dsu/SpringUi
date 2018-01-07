package pl.springui.components;

public interface UiPhaseListener {

	default void beforeAfterRenderResponse(UiComponentI c) {
	}

	default void beforeAjax(UiComponentI c) {
	}

	default void beforeApplyRequest(UiComponentI c) {
	}

	default void beforeProcess(UiComponentI c) {
	}

	default void beforeRenderResponse(UiComponentI c) {
	};

}
