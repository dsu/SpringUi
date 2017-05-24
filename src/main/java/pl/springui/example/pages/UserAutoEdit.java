package pl.springui.example.pages;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import pl.springui.components.form.AutoFormT;
import pl.springui.example.model.User;
import pl.springui.example.service.UserService;
import pl.springui.http.UiCtx;

@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserAutoEdit extends DefaultPage {

	@Autowired
	UserService userService;

	@Autowired
	AutoFormT form;

	@Autowired
	public UserAutoEdit(UiCtx ctx) {
		super(ctx);
		logger.debug("New auto edit page");
	}

	@Override
	public String renderResponse() {
		logger.debug("Render layout response:");
		return layout.renderResponse();
	}

	@Override
	public void restoreView() {

		User user = new User();
		user.setName("Wpisz..");

		form.setFormBean(user);
		layout.setContent(form);

		super.restoreView();
		logger.debug("Restore editPage");
	}

}
