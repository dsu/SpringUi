package pl.springui.example.pages;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import pl.springui.components.list.AutoList;
import pl.springui.example.model.User;
import pl.springui.example.service.UserService;
import pl.springui.http.UiCtx;

@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserList extends DefaultPage {

	@Autowired
	AutoList<User> list;

	@Autowired
	UserService userService;

	@Autowired
	public UserList(UiCtx ctx) {
		super(ctx);
		logger.debug("New listPage");
	}

	@Override
	public void restoreView() {
		list.setService(userService);
		layout.setContent(list);

		super.restoreView();
		logger.debug("Restore listPage");
	}

}
