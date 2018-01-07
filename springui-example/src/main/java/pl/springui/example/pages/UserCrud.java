package pl.springui.example.pages;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import pl.springui.components.ExampleComponent;
import pl.springui.components.exceptions.UserVisibleError;
import pl.springui.components.form.AutoFormT;
import pl.springui.components.form.Message;
import pl.springui.components.form.MessageType;
import pl.springui.components.layouts.BootstrapGrid;
import pl.springui.components.list.AutoList;
import pl.springui.example.model.User;
import pl.springui.example.service.UserService;
import pl.springui.http.UiCtx;

@ExampleComponent
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserCrud extends DefaultPage {

	@Autowired
	UserService userService;

	@Autowired
	AutoFormT<User> form;

	@Autowired
	AutoList<User> list;

	@Autowired
	@Qualifier("bootstrapGrid")
	BootstrapGrid grid;

	@Autowired
	public UserCrud(UiCtx ctx) {
		super(ctx);
		logger.debug("New auto edit page");
	}

	@Override
	public void applyRequest() {
		super.applyRequest();
	}

	@Override
	public String renderResponse() {
		logger.debug("Render layout response:");
		return layout.renderResponse();
	}

	@Override
	public void restoreView() {
		logger.debug("RESTORE CRUD");
		// list
		list.setService(userService);
		list.setForm(form);
		list.setOnSelectRefreshIds(form.getClientId());

		form.setVisible(false); // hide form
		form.setList(list);
		form.setService(userService);
		form.setOnApplyRequest((caller) -> {
			logger.debug("On apply request form callback");
		});

		grid.add(form);
		grid.add(list);
		layout.setContent(grid);

		super.restoreView();
		logger.debug("Restore editPage");

	}

}
