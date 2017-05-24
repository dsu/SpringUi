package pl.springui.example.pages;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import pl.springui.example.components.CMSDocumentView;
import pl.springui.http.UiCtx;

@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CMSPage extends DefaultPage {

	@Autowired
	CMSDocumentView cms;

	private String alias;

	@Autowired
	public CMSPage(UiCtx ctx) {
		super(ctx);
		logger.debug("CMS");
	}

	public String getAlias() {
		return alias;
	}

	@Override
	public void restoreView() {

		cms.setAlias(alias);
		layout.setContent(cms);

		super.restoreView();
		logger.debug("CMS view restored");
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

}
