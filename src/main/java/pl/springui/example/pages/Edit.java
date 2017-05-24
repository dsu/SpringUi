package pl.springui.example.pages;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.context.WebApplicationContext;

import pl.springui.components.UiComponent;
import pl.springui.components.form.BtnT;
import pl.springui.components.form.FormT;
import pl.springui.components.form.SubmitBtnT;
import pl.springui.components.form.TextFieldT;
import pl.springui.components.layouts.BootstrapGrid;
import pl.springui.components.layouts.Inspinia;
import pl.springui.components.layouts.VerticalMenu;
import pl.springui.components.list.AutoList;
import pl.springui.components.resources.Scripts;
import pl.springui.components.resources.Styles;
import pl.springui.example.model.User;
import pl.springui.example.service.UserService;
import pl.springui.http.UiCtx;
import pl.springui.utils.Profiler;

@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class Edit extends UiComponent {

	@Autowired
	Scripts scripts;

	@Autowired
	Styles styles;

	@Autowired
	FormT form;

	@Autowired
	Inspinia layout;

	@Autowired
	VerticalMenu menu;

	@Autowired
	TextFieldT nameField;

	@Autowired
	TextFieldT ageField;

	@Autowired
	AutoList<User> list;

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
	public Edit(UiCtx ctx) {
		super(ctx);
		logger.debug("New listPage");
	}

	@Profiler
	@Override
	public void applyRequest() {
		super.applyRequest();

		HttpServletRequest request = getCtx().getReq().getRequest();
		printRequest(getCtx().getReq().getParameterMap());

		// TODO
		if (request != null) {
			User user = new User();
			// DataBinder binder = new DataBinder(user);
			ServletRequestDataBinder binder = new ServletRequestDataBinder(user);

			// binder.setValidator(new FooValidator());

			// bind to the target object
			binder.bind(request);

			// validate the target object
			binder.validate();

			// get BindingResult that includes any validation errors
			BindingResult results = binder.getBindingResult();

			logger.debug("Bind result: " + results + " " + results.getFieldError());
		} else {
			logger.debug("Request is null");
		}

	}

	public boolean hasRequestParameters() {
		boolean has = getCtx().hasRequestParameters();
		logger.debug("has parmeters: {}", has);
		return has;
	}

	private void printRequest(Map<String, String[]> map) {
		logger.debug("REQUEST:");
		for (String key : map.keySet()) {
			logger.debug(getCtx().getReq().getParameter(key));
		}
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

		list.setService(userService);

		grid.add(list, 4);

		ajaxBtn.setOnClick("Ui.load({ids:['" + form.getClientId() + "']});");
		ajaxBtn.setLabel("Ajax");

		grid.add(nameField, 1);
		grid.add(ageField, 1);
		grid.add(formSubmitBtn, 2);
		grid.add(ajaxBtn, 2);
		form.add(grid);

		layout.setContent(form);
		layout.setHeaderJs(scripts);
		layout.setHeaderCss(styles);

		menu.addItem("Strona1", "fa-th-large", "#").addSubMenuItem("Podstrona1", "", "#").addSubMenuItem("Podstrona2",
				"fa-files-o", "#");
		menu.addItem("Strona2", "fa-files-o", "#");
		menu.setImage("/img/logo.png");
		layout.setMenu(menu);

		addChild(layout);
		super.restoreView();
		logger.debug("Restore listPage");
	}

	/**
	 * Caching when there is no request paramters - static pages onlny
	 */
	// @Override
	// @Cacheable(cacheNames = "pages", keyGenerator = "uiComponentKey", unless
	// = "@listPage.hasRequestParameters()")
	// public String executePhases() {
	// return super.executePhases();
	// }

}
