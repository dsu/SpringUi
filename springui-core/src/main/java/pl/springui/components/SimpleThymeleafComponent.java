package pl.springui.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.EqualsAndHashCode;
import pl.springui.http.UiCtx;
import pl.springui.template.engine.MapTemplateEngine;

/**
 * Displays a template
 * 
 * @author dsu
 *
 */
@Primary
@Component
@Scope("prototype")
@EqualsAndHashCode
public class SimpleThymeleafComponent extends AbstractThymeleafComponent {

	@Autowired
	public SimpleThymeleafComponent(UiCtx ctx, @Qualifier("thymeleaf") MapTemplateEngine engine) {
		super(ctx, engine);
	}

}
