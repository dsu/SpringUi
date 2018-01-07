package pl.springui.components.exceptions;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.EqualsAndHashCode;
import pl.springui.components.HTMLRenderer;
import pl.springui.template.engine.MapTemplateEngine;

@Primary
@Component
@EqualsAndHashCode
@Scope("prototype")
public class ErrorComponent implements HTMLRenderer {

	protected MapTemplateEngine engine;

	Map<String, Object> errorAttributes;

	@Autowired
	public ErrorComponent(@Qualifier("thymeleaf") MapTemplateEngine engine) {
		this.engine = engine;
	}

	public Map<String, Object> getErrorAttributes() {
		return errorAttributes;
	}

	public String getTemplatePath() {
		return "components/error.html";
	}

	@Override
	public String renderResponse() {
		return engine.procesTemplateAsString(errorAttributes, getTemplatePath());

	}

	public void setErrorAttributes(Map<String, Object> errorAttributes) {
		this.errorAttributes = errorAttributes;
	}

}
