package pl.springui.example.pages;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import pl.springui.components.form.BtnT;
import pl.springui.components.form.FormT;
import pl.springui.components.form.SubmitBtnT;
import pl.springui.components.form.TextFieldT;
import pl.springui.components.layouts.BootstrapGrid;
import pl.springui.example.service.UserService;
import pl.springui.http.UiCtx;

@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserEdit extends DefaultPage {

	@Autowired
	FormT form;

	@Autowired
	TextFieldT nameField;

	@Autowired
	TextFieldT ageField;

	@Autowired
	UserService userService;

	@Autowired
	SubmitBtnT formSubmitBtn;

	@Autowired
	BtnT ajaxBtn;

	@Autowired
	@Qualifier("bootstrapGrid")
	BootstrapGrid grid;

	@Autowired
	public UserEdit(UiCtx ctx) {
		super(ctx);
		logger.debug("New edit page");
	}

	@Override
	public String renderResponse() {
		logger.debug("Render layout response:");
		return layout.renderResponse();
	}

	@Override
	public void restoreView() {
		nameField.setLabel("name");
		ageField.setLabel("age");
		formSubmitBtn.setLabel("Submit");

		ajaxBtn.setOnClick("Ui.load({ids:['" + form.getClientId() + "']});");
		ajaxBtn.setLabel("Ajax");

		grid.add(nameField, 1);
		grid.add(ageField, 1);
		grid.add(formSubmitBtn, 2);
		grid.add(ajaxBtn, 2);
		form.add(grid);

		layout.setContent(form);

		super.restoreView();
		logger.debug("Restore editPage");
	}

}
