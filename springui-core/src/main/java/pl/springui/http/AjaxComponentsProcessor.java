package pl.springui.http;

import java.io.IOException;

public interface AjaxComponentsProcessor {

	void process(UiAjaxRequest ajaxCtx) throws IOException;

}