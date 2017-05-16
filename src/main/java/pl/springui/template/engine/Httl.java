package pl.springui.template.engine;

import java.io.Writer;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import httl.Engine;
import httl.Template;

@Component
@Lazy
public class Httl implements MapTemplateEngine {

	private Engine engine;

	@PostConstruct
	public void init() {

		/**
		 *
		 * import.packages + = com.xxx, message.basename = messages
		 */

		Properties p = new Properties();
		p.setProperty("input.encoding", "UTF-8");
		p.setProperty("output.encoding", "UTF-8");
		p.setProperty("reloadable", "true");
		p.setProperty("precompiled", "false");
		p.setProperty("template.directory", "/templates/httl/components/");

		engine = Engine.getEngine(p);

	}

	public void procesTemplate(Map<String, Object> model, String templatepath, Writer writer) {

		Template template;
		try {
			template = engine.getTemplate(templatepath);
			template.render(model, writer);
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

}
