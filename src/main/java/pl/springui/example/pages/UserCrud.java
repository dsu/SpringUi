package pl.springui.example.pages;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import pl.springui.components.exceptions.UserVisibleError;
import pl.springui.components.form.AutoFormT;
import pl.springui.components.form.Message;
import pl.springui.components.form.MessageType;
import pl.springui.components.layouts.BootstrapGrid;
import pl.springui.components.list.AutoList;
import pl.springui.example.model.User;
import pl.springui.example.service.UserService;
import pl.springui.http.UiCtx;

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
		list.setOnSelectRefreshIds(form.getClientId());

		// form - only placeholder
		User user = new User();
		user.setName("Wpisz..");
		form.setFormBean(user);
		form.setVisible(false);

		form.setOnApplyRequest((caller) -> {

			// sprawdzanie wybranego elementu z listy
			// FIXME - przeniesc to do listy
			String key = ctx.getReq().getParameter("uiListSelectedKey");

			// ustawienie go w formularzu
			logger.debug("CRUD applyRequest key {}", key);
			if (key != null) {
				User selected = userService.getById(Integer.parseInt(key));
				if (selected == null) {
					throw new UserVisibleError("User doesn't exists!");
				}
				caller.setVisible(true);
				caller.setFormBean(selected);
			}
		});

		form.setOnProcess((caller) -> {
			if (caller.isValid()) {
				// TODO - persist changes, redirect?
				logger.debug("Saving {}", caller.getFormBean());
				caller.setMessage(new Message("User has been saved", MessageType.success));
			} else {
				// show a message
				caller.setMessage(new Message("Form data is not valid", MessageType.warning));
			}

		});

		grid.add(form);
		grid.add(list);
		layout.setContent(grid);

		super.restoreView();
		logger.debug("Restore editPage");

	}

}
