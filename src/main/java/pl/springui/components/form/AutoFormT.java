package pl.springui.components.form;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.SmartValidator;
import org.springframework.web.bind.ServletRequestDataBinder;

import pl.springui.components.UiCallback;
import pl.springui.components.UiComponent;
import pl.springui.components.layouts.BootstrapGrid;
import pl.springui.http.UiCtx;
import pl.springui.template.engine.MapTemplateEngine;
import pl.springui.utils.Profiler;

/**
 * TODO - dodac sprawdzanie unikalnosci nazw pol? Dodać wspolny interfejs dla
 * pol typu input. Przy prztwarzaniu requesta czytac komponenty z zapisanego
 * drzewa, czy odtwarzac w kodzie w locie? Inaczej moze byc w przypadku ajaxa -
 * wtedy zapisanie drzewa ma wiekszy sens. Dodac przydzielanie unikalnych id w
 * JS - client id.
 * 
 * @author dsu
 *
 */
@Component
@Scope("prototype")
public class AutoFormT<T> extends UiComponent {

	protected MapTemplateEngine engine;
	protected List<AbstractInputField> fields = new ArrayList<AbstractInputField>();
	protected T formBean;
	protected BootstrapGrid grid;
	protected BtnT button;
	protected boolean isValid = false;
	protected Message message;

	// @Autowired
	// @Qualifier("mvcValidator")
	// Validator validator;
	@Autowired
	SmartValidator validator;

	protected UiCallback<AutoFormT<T>> onApplyRequest;
	protected UiCallback<AutoFormT<T>> onProcess;

	@Autowired
	public AutoFormT(UiCtx ctx, @Qualifier("thymeleaf") MapTemplateEngine engine,
			@Qualifier("bootstrapGrid") BootstrapGrid grid, BtnT button) {
		super(ctx);
		this.engine = engine;
		this.grid = grid;
		this.button = button;
	}

	protected void add(AbstractInputField field, int row, int col) {
		addChild(field);
		grid.add(field, row, col);
		fields.add(field);
	}

	protected void addButton(UiComponent field) {
		addChild(field);
		grid.add(field, grid.getRows());
	}

	@Override
	public void applyRequest() {

		onApplyRequest.callback(this);

		boolean initalization = ctx.isViewInitialization();
		isValid = false;
		logger.debug("applyRequest");
		printRequest();
		logger.debug("isInitialization: {}, form bean: {} ", initalization, formBean);

		if (!initalization) {

			BindingResult results = bindAndValidateRequest();

			if (results.getAllErrors().size() == 0) {
				isValid = true;
			}

			updateInputFieldMessages(results);
			updateInputFieldValues(results);

			// wartosci ustawiane w super w apply Request - raczej źle!
			// super.applyRequest();
		}

	}

	/**
	 * Map request to a bean and validate it.
	 * 
	 * @return
	 */
	protected BindingResult bindAndValidateRequest() {
		ServletRequestDataBinder binder = new ServletRequestDataBinder(formBean);
		binder.setValidator(validator);
		binder.bind(ctx.getReq().getRequest());
		binder.validate();
		BindingResult results = binder.getBindingResult();

		logger.debug("validators: {} , errors: {}, {}, {}", binder.getValidators().size(), results.getErrorCount(),
				results.getGlobalErrorCount(), results.getAllErrors().size());
		logger.debug("form bean after : {} ", formBean);
		return results;
	}

	protected void updateInputFieldValues(BindingResult results) {
		for (AbstractInputField f : fields) {
			Object fieldValue = results.getFieldValue(f.getName());
			logger.debug("value of {} = {}", f.getName(), fieldValue);
			if (fieldValue != null) {
				f.setValue(String.valueOf(fieldValue));
			}
		}
	}

	protected void clearInputErrors() {
		for (AbstractInputField f : fields) {
			logger.debug("Clear message of {}", f);
			f.clearMessage();
		}

	}

	public T getFormBean() {
		return formBean;
	}

	/**
	 * What should happen when component get feedback from the user
	 * 
	 * @param callback
	 */
	public void setOnApplyRequest(UiCallback<AutoFormT<T>> callback) {
		onApplyRequest = callback;
	}

	@Profiler
	@Override
	public String renderResponse() {

		if (!isVisible()) {
			return renderPlaceHolder();
		}

		ArrayList<String> renderedGrid = new ArrayList<String>();
		renderedGrid.add(grid.renderResponse());
		putToViewModel("fields", renderedGrid);
		putToViewModel("message", getMessage());
		putStringToViewModel("viewguid", ctx.getViewGuid());
		return engine.procesTemplateAsString(viewModel, "components/form.xhtml");
	}

	@Override
	public void process() {
		onProcess.callback(this);
		super.process();
	}

	@Override
	public void restoreView() {

		boolean initalization = ctx.isViewInitialization();

		logger.debug("restoreView init: {}, form bean: {}", initalization, formBean.getClass());

		try {
			Class<?> clazz = formBean.getClass();

			BeanInfo info = Introspector.getBeanInfo(clazz);

			PropertyDescriptor[] props = info.getPropertyDescriptors();
			for (PropertyDescriptor prop : props) {

				System.out.println(prop.getDisplayName());

				Method readMethod = prop.getReadMethod();
				Method writeMethod = prop.getWriteMethod();
				FormInput colMeta = null;

				if (writeMethod != null && writeMethod.isAnnotationPresent(FormInput.class)) {
					colMeta = writeMethod.getAnnotation(FormInput.class);
				} else if (readMethod != null && readMethod.isAnnotationPresent(FormInput.class)) {
					colMeta = readMethod.getAnnotation(FormInput.class);
				}

				if (colMeta != null) {
					InputType type = colMeta.type();
					String name = colMeta.name();

					if (name == null || name.equals("")) {
						name = prop.getDisplayName();
					}
					int col = colMeta.column();
					int row = colMeta.row();

					Object value = null;
					if (readMethod != null) {
						value = readMethod.invoke(formBean);
						logger.debug("field: {}, value: {}", name, value);
					}

					logger.debug("new auto-form field {}, {}", name, type);

					switch (type) {
					case CHECKBOX:
						// TODO
						break;

					default:
						TextFieldT field = new TextFieldT(ctx, engine);
						field.setLabel(name);
						field.setName(name);
						if (value != null) {
							field.setValue(String.valueOf(value));
						}
						add(field, row, col);
						break;
					}
				}

			}

		} catch (IntrospectionException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			logger.error("Error recreating a form", e);
		}

		button.setOnClick("Ui.load({ids:['" + getClientId() + "']});");
		button.setLabel("Save");
		addButton(button);

		super.restoreView();

	}

	protected void updateInputFieldMessages(BindingResult results) {
		clearInputErrors();

		List<FieldError> fieldErrors = results.getFieldErrors();
		for (FieldError error : fieldErrors) {
			String name = error.getField();
			String msg = error.getDefaultMessage();

			logger.debug("Set error {} on field {}", msg, name);

			for (AbstractInputField field : fields) {
				if (field.getLabel().equals(name)) {
					field.setMessage(msg);
					break;
				}
			}
		}
	}

	public void setFormBean(T formBean) {
		this.formBean = formBean;
	}

	public void setOnProcess(UiCallback<AutoFormT<T>> onProcess) {
		this.onProcess = onProcess;
	}

	public boolean isValid() {
		return isValid;
	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

}
