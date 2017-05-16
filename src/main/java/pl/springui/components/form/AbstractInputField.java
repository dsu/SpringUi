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

	@Autowired
	public AbstractInputField(@Lazy UiCtx ctx) {
		super(ctx);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public void applyRequest() {
		String val = getCtx().getReq().getParameter(getLabel());
		if (val != getValue()) {
			setValue(val);
		}
	}

}