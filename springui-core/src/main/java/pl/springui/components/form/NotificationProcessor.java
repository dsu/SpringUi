package pl.springui.components.form;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.EqualsAndHashCode;
import pl.springui.components.UiComponent;
import pl.springui.http.UiCtx;
import pl.springui.template.engine.MapTemplateEngine;
import pl.springui.utils.Profiler;

@Primary
@Component
@EqualsAndHashCode
@Scope("prototype")
public class NotificationProcessor extends UiComponent implements NotificationProcessorI {

	protected MapTemplateEngine engine;
	protected String label;
	protected String onClick;

	@Autowired
	public NotificationProcessor(UiCtx ctx, @Qualifier("thymeleaf") MapTemplateEngine engine) {
		super(ctx);
		this.engine = engine;
	}

	public String getLabel() {
		return label;
	}

	public String getOnClick() {
		return onClick;
	}

	public String getTemplatePath() {
		return "components/btn.xhtml";
	}

	@Override
	public String processMessages(List<Message> messages) {

		if (messages != null && messages.size() > 0) {
			StringBuilder sb = new StringBuilder();
			for (Message m : messages) {
				String msg = m.getMessage();

				if (m.isDialog()) {
					sb.append("console.debug('sweetalert liblary is required');");

					sb.append("swal('");
					sb.append(msg);
					sb.append("'");

					if (m.getDescription() != null && m.getDescription().length() > 0) {
						sb.append(",'");
						sb.append(m.getDescription());
						sb.append("'");
					}
					sb.append(",'");
					sb.append(m.getType().toString());
					sb.append("');");

				} else {

					sb.append("console.debug('toastr liblary is required');");
					if (m.getType() == MessageType.success) {
						if (msg == null) {
							msg = "Success";
						}
						sb.append("toastr.success('");
						sb.append(msg);
						sb.append("');");

					} else if (m.getType() == MessageType.warning) {
						if (msg == null) {
							msg = "Warning";
						}
						sb.append("toastr.warning('");
						sb.append(msg);
						sb.append("');");

					} else {
						if (msg == null) {
							msg = "Error";
						}
						sb.append("toastr.error('");
						sb.append(msg);
						sb.append("');");
					}
				}
			}

			return sb.toString();
		}

		return "";
	}

	@Override
	@Profiler
	public String renderResponse() {
		Map<String, Object> ctx = new HashMap<String, Object>();
		ctx.put("label", label);
		ctx.put("onclick", onClick);
		StringWriter writer = new StringWriter();
		engine.procesTemplate(ctx, getTemplatePath(), writer);
		return writer.toString();
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setOnClick(String onClick) {
		this.onClick = onClick;
	}

}
