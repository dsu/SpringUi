package pl.springui.components.form;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
import pl.springui.components.exceptions.UiException;
import pl.springui.components.exceptions.UserVisibleError;
import pl.springui.components.js.UiJavaScripts;
import pl.springui.components.layouts.BootstrapGrid;
import pl.springui.components.list.AutoList;
import pl.springui.http.UiCtx;
import pl.springui.template.engine.MapTemplateEngine;
import pl.springui.utils.Profiler;

/**
 * Creates a HTML form based on a POJO with @FormInput annotation
 * 
 * @author dsu
 *
 */
@Component
@Scope("prototype")
public class AutoFormT<T> extends UiComponent {

  protected FormAction action = FormAction.cancel;

  protected String canceBtnlLabel = "Cancel";
  protected String deleteBtnLabel = "Delete";
  protected String saveBtnLabel = "Save";
  protected String newBtnLabel = "New";

  protected BtnT saveButton;
  protected BtnT deleteButton;
  protected BtnT cancelButton;
  protected BtnT newButton;

  protected MapTemplateEngine engine;
  protected List<AbstractInputField> fields = new ArrayList<AbstractInputField>();
  protected T formBean;
  /**
   * If request contains this value as a parameter, data should come from this form's submit.
   */
  protected String formGuid;
  protected BootstrapGrid grid;
  protected AutoList<T> list;

  protected boolean isValid = false;

  protected Message message;

  protected UiCallback<AutoFormT<T>> onApplyRequest;
  protected UiCallback<AutoFormT<T>> onDeleted;
  protected UiCallback<AutoFormT<T>> onProcess;
  protected UiCallback<AutoFormT<T>> onSaved;
  protected CRUDService<T> service;

  @Autowired
  SmartValidator validator;

  @Autowired
  public AutoFormT(UiCtx ctx, @Qualifier("thymeleaf") MapTemplateEngine engine,
      @Qualifier("bootstrapGrid") BootstrapGrid grid, BtnT cancelBtn, BtnT saveBtn, BtnT deleteBtn,
      BtnT newBtn) {
    super(ctx);
    this.engine = engine;
    this.grid = grid;
    this.cancelButton = cancelBtn;
    this.saveButton = saveBtn;
    this.deleteButton = deleteBtn;
    this.newButton = newBtn;

    this.formGuid = "form_" + UUID.randomUUID().toString().replaceAll("-", "");
  }

  protected void addToGrid(AbstractInputField field, int row, int col) {
    grid.add(field, row, col);
    fields.add(field);
  }

  /**
   * Annd button to next column
   * 
   * @param field
   */
  protected void addToNextRow(UiComponent field) {
    grid.add(field, grid.getRows());
  }

  @Override
  public void applyRequest() {

    String actionParameter = ctx.getReq().getParameter(getActionParameterName());

    logger.debug("Form action parameter is {}={}", getActionParameterName(), actionParameter);

    if (actionParameter == null || FormAction.cancel.equals(FormAction.valueOf(actionParameter))) {
      action = FormAction.cancel;
      setFormBean(null);
      setVisible(false);
      return;
    }

    action = FormAction.valueOf(actionParameter);
    isValid = false;

    if (isFormFeedback()) {
      logger.debug("Bind and validate form feedback");

      clearInputErrors();
      BindingResult results = bindAndValidateRequest();

      if (results.getAllErrors().size() == 0) {
        isValid = true;
      }

      updateInputFieldMessages(results);
      updateInputFieldValues(results);

      // wartosci ustawiane w super w apply Request - raczej źle!
      // super.applyRequest();
    } else {

      if (isVisible()) {
        BindingResult results = bindRequest();
        updateInputFieldValues(results);
      } else {
        logger.debug("Form is not visible, do not try to bind request");
      }
    }

    if (onApplyRequest != null) {
      onApplyRequest.callback(this);
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

    logger.debug("validators: {} , errors: {}, {}, {}", binder.getValidators().size(),
        results.getErrorCount(), results.getGlobalErrorCount(), results.getAllErrors().size());
    logger.debug("form bean after binding and validation : {} ", formBean);
    return results;
  }

  protected BindingResult bindRequest() {
    ServletRequestDataBinder binder = new ServletRequestDataBinder(formBean);
    binder.bind(ctx.getReq().getRequest());
    BindingResult results = binder.getBindingResult();
    logger.debug("form bean after  binding: {} ", formBean);
    return results;
  }

  private void buildFormFromBean() {
    try {

      logger.debug("Building form from a bean {}", formBean);

      clearChildren();
      addChild(grid);

      Class<?> clazz = formBean.getClass();
      BeanInfo info = Introspector.getBeanInfo(clazz);
      PropertyDescriptor[] props = info.getPropertyDescriptors();
      for (PropertyDescriptor prop : props) {

        logger.debug(prop.getDisplayName());

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
          String label = colMeta.label();
          String[] options = colMeta.options();
          String role = colMeta.securityRole();

          if (name == null || name.equals("")) {
            name = prop.getDisplayName();
          }

          if (label == null || label.equals("")) {
            label = name;
          }

          int col = colMeta.column();
          int row = colMeta.row();

          Object value = null;
          if (readMethod != null) {
            value = readMethod.invoke(formBean);
            logger.debug("field: {}, value: {}", name, value);
          }

          logger.debug("new auto-form field {}, {}", name, type);
          if (type.equals(InputType.MEDIACATALOG)) {
            MediaCatalogFieldT field = new MediaCatalogFieldT(ctx, engine);
            field.setLabel(label);
            field.setName(name);
            if (value != null) {
              field.setValue(String.valueOf(value));
            }

            addToGrid(field, row, col);
          } else if (type.equals(InputType.TEXTAREA)) {
            TextAreaT field = new TextAreaT(ctx, engine);
            field.setLabel(label);
            field.setName(name);
            if (value != null) {
              field.setValue(String.valueOf(value));
            }
            addToGrid(field, row, col);
          } else if (type.equals(InputType.DATETIME)) {
            DateTimeFieldT field = new DateTimeFieldT(ctx, engine);
            field.setLabel(label);
            field.setName(name);
            if (value != null) {
              field.setValue(String.valueOf(value));
            }
            addToGrid(field, row, col);
          } else if (type.equals(InputType.GROUPS)) {
            GroupsFieldT field = new GroupsFieldT(ctx, engine);
            field.setLabel(label);
            field.setName(name);
            if (value != null) {
              field.setValue(String.valueOf(value));
            }
            addToGrid(field, row, col);
          } else if (type.equals(InputType.SELECT)) {
            SelectFieldT field = new SelectFieldT(ctx, engine);
            field.setLabel(label);
            field.setName(name);
            field.setOptions(options);
            if (value != null) {
              field.setValue(String.valueOf(value));
            }
            addToGrid(field, row, col);
          } else {
            TextFieldT field = new TextFieldT(ctx, engine);
            field.setLabel(label);
            field.setName(name);
            if (value != null) {
              field.setValue(String.valueOf(value));
            }

            logger.debug("Add a field {} to grid pos {},{}", field, row, col);
            addToGrid(field, row, col);
          }
        }

      }

    } catch (IntrospectionException | IllegalAccessException | IllegalArgumentException |

        InvocationTargetException e) {
      logger.error("Error recreating a form", e);
    }

    saveButton.setOnClick("Ui.load({ids:['" + getClientId() + "'],params:'"
        + getActionParameterName() + "=" + FormAction.save + "'});");
    saveButton.setLabel(saveBtnLabel);
    saveButton.setClientId(getClientId() + "__confirm-btn");
    saveButton.setWrapperClass("text-left");
    saveButton.setClientClass("btn-lg btn-success");

    deleteButton.setOnClick("Ui.load({ids:['" + getClientId() + "'],params:'"
        + getActionParameterName() + "=" + FormAction.delete + "'});");
    deleteButton.setClientClass("btn-lg btn-warning");
    deleteButton.setWrapperClass("text-right");
    deleteButton.setLabel(deleteBtnLabel);

    newButton.setOnClick("Ui.load({ids:['" + getClientId() + "'],params:'"
        + getActionParameterName() + "=" + FormAction.add + "'});");
    newButton.setClientClass("btn-lg btn-primary");
    newButton.setWrapperClass("text-right");
    newButton.setLabel(newBtnLabel);

    cancelButton.setOnClick(
        UiJavaScripts.refresh(getClientId(), getActionParameterName() + "=" + FormAction.cancel));
    cancelButton.setLabel(canceBtnlLabel);
    cancelButton.setWrapperClass("text-left");
    cancelButton.setClientId(getClientId() + "__cancel-btn");
    cancelButton.setClientClass("btn-lg btn-default");

    grid.add(saveButton, 1, 0);
    grid.add(deleteButton, 1, 1);
    grid.add(cancelButton, 2, 0);
    grid.add(newButton, 2, 1);

  }

  @Override
  public void clearChildren() {
    fields = new ArrayList<AbstractInputField>();
    super.clearChildren();
  }

  protected void clearInputErrors() {
    for (AbstractInputField f : fields) {
      f.clearMessage();
    }

  }

  public FormAction getAction() {
    return action;
  }

  protected String getActionParameterName() {
    return getClientId() + ".action";
  }

  public String getCancelLabel() {
    return canceBtnlLabel;
  }

  public String getConfirmLabel() {
    return saveBtnLabel;
  }

  public T getFormBean() {
    return formBean;
  }

  public String getIdParameterName() {
    return "id";
  }

  public AutoList<T> getList() {
    return list;
  }

  public Message getMessage() {
    return message;
  }

  public UiCallback<AutoFormT<T>> getOnDeleted() {
    return onDeleted;
  }

  public UiCallback<AutoFormT<T>> getOnSaved() {
    return onSaved;
  }

  public CRUDService<T> getService() {
    return service;
  }

  public boolean isFormFeedback() {
    String requestFormGuid = ctx.getReq().getParameter("formguid");
    if (requestFormGuid != null) {
      if (formGuid.equals(requestFormGuid)) {
        return true;
      } else {
        throw new UserVisibleError("Invalid CSRF token");
      }
    } else {
      return false;
    }
  }

  public boolean isValid() {
    return isValid;
  }

  @Override
  public void process() {

    String idParam = ctx.getReq().getParameter(getIdParameterName());
    logger.debug("Form action : {}, id: {}", action, idParam);

    if (action.equals(FormAction.cancel)) {
      return;
    }

    if (action != null) {
      if (service == null) {
        throw new UiException("Form service is null");
      }
    }

    if (onProcess != null) {
      onProcess.callback(this);
    }

    if (action.equals(FormAction.cancel)) {
      logger.debug("Canceling form");

      visible = false;
      if (onApplyRequest != null) {
        onApplyRequest.callback(this);
      }

      return;
    } else if (action.equals(FormAction.add)) {

      setFormBean(service.getNew());
      buildFormFromBean();
      visible = true;
      logger.debug("New action");

    } else if (action.equals(FormAction.read)) {

      int id = -1;
      try {
        id = Integer.parseInt(idParam);
      } catch (Exception e) {
        throw new RuntimeException("Invalid id");
      }
      T bean = service.read(id);
      setFormBean(bean);
      buildFormFromBean();
      visible = true;
      logger.debug("Edit of {}", bean);

    } else if (action.equals(FormAction.save)) {

      if (isValid) {
        logger.debug("Saving a bean {}", getFormBean());
        Message save = service.save(getFormBean());
        ctx.addMessage(save);
        ctx.addToExtraProcess(list);

        if (onSaved != null) {
          onSaved.callback(this);
        }

        setVisible(false);

      } else {
        logger.warn("Bean is not valid");
        ctx.addMessage(Message.warn("Form is not valid"));
      }

    } else if (action.equals(FormAction.delete)) {
      logger.debug("Deleting a bean {}", getFormBean());

      Message msg = service.delete(getFormBean());
      if (msg != null) {
        ctx.addMessage(msg);
        ctx.addToExtraProcess(list);
      }
      if (onDeleted != null) {
        onDeleted.callback(this);
      }
      setVisible(false);

    }

    super.process();
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
    putStringToViewModel("formguid", formGuid);
    return engine.procesTemplateAsString(viewModel, "components/form.xhtml");
  }

  @Override
  public void restoreView() {

    if (formBean == null) {
      logger.debug("Form bean is not set");
      setVisible(false);
    } else {
      logger.debug("restoreView form bean: {}", formBean.getClass());
      buildFormFromBean();
    }
    super.restoreView();

  }

  public void setAction(FormAction action) {
    this.action = action;
  }

  public void setCancelLabel(String cancelLabel) {
    this.canceBtnlLabel = cancelLabel;
  }

  public void setConfirmLabel(String confirmLabel) {
    this.saveBtnLabel = confirmLabel;
  }

  public void setFormBean(T formBean) {
    this.formBean = formBean;
  }

  public void setList(AutoList<T> list) {
    this.list = list;
  }

  public void setMessage(Message message) {
    this.message = message;
  }

  /**
   * What should happen when component get feedback from the user
   * 
   * @param callback
   */
  public void setOnApplyRequest(UiCallback<AutoFormT<T>> callback) {
    onApplyRequest = callback;
  }

  public void setOnDeleted(UiCallback<AutoFormT<T>> onDeleted) {
    this.onDeleted = onDeleted;
  }

  public void setOnProcess(UiCallback<AutoFormT<T>> onProcess) {
    this.onProcess = onProcess;
  }

  public void setOnSaved(UiCallback<AutoFormT<T>> onSaved) {
    this.onSaved = onSaved;
  }

  public void setService(CRUDService<T> service) {
    this.service = service;
  }

  protected void updateInputFieldMessages(BindingResult results) {

    List<FieldError> fieldErrors = results.getFieldErrors();
    for (FieldError error : fieldErrors) {
      String name = error.getField();
      String msg = error.getDefaultMessage();

      logger.debug("Set error {} on field {}", msg, name);

      for (AbstractInputField field : fields) {
        if (field.getName().equals(name)) {
          field.setMessage(msg);
          break;
        }
      }
    }
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

}
