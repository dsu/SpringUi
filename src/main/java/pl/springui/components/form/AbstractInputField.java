package pl.springui.components.form;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import lombok.EqualsAndHashCode;
import pl.springui.components.UiComponent;
import pl.springui.http.UiCtx;

@EqualsAndHashCode
public abstract class AbstractInputField extends UiComponent {

	protected String value;
	protected String label;

	/**
	 * Html name attribute
	 */
	protected String name;
	protected Message message;

	@Autowired
	public AbstractInputField(@Lazy UiCtx ctx) {
		super(ctx);
	}

	@Override
	public void applyRequest() {
		String val = getCtx().getReq().getParameter(getLabel());
		if (val != null && val.equals(getValue())) {
			setValue(val);
		}
	}

	public String getLabel() {

		if (label == null) {
			return getName();
		}

		return label;
	}

	public String getName() {

		if (name == null) {
			return getLabel();
		}

		return name;
	}

	public String getValue() {
		return value;
	}

	public void setMessage(String msg) {
		this.message = new Message(msg, MessageType.error);
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(String value) {
		logger.debug("set text value: {}", value);
		this.value = value;
	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public void clearMessage() {
		logger.debug("Clear message {}", message);
		this.message = null;

	}

}