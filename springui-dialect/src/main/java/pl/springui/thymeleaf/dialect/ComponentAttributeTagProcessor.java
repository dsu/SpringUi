package pl.springui.thymeleaf.dialect;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.AttributeValueQuotes;
import org.thymeleaf.model.IAttribute;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

import pl.springui.components.UiComponentI;
import pl.springui.http.UiCtx;

@Component("prototype")
public class ComponentAttributeTagProcessor extends AbstractAttributeTagProcessor {

	private static final String ATTR_NAME = "component";
	private static final int PRECEDENCE = 1000;
	private static final String BEAN_NAME_KEY = "bean";

	@Autowired
	protected UiCtx ctx;

	@Autowired
	protected ApplicationContext appContext;

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	public ComponentAttributeTagProcessor() {
		super(TemplateMode.HTML, SpringUiDialect.PREFIX, null, false, ATTR_NAME, true, PRECEDENCE, true);
	}

	@Override
	protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName,
			String attributeValue, IElementTagStructureHandler structureHandler) {
		SpelExpressionParser spelParser = new SpelExpressionParser();
		logger.debug("attributeValue {}", attributeValue);
		Expression exp = spelParser.parseExpression(attributeValue);

		if (logger.isDebugEnabled() && exp.getValue() != null) {
			logger.debug("Expression {}, value: {}, value type: {}", exp, exp.getValue(), exp.getValue().getClass());
		} else if (logger.isDebugEnabled()) {
			logger.debug("Expression {}, value is null", exp);
		}

		Map<String, String> value = exp.getValue(Map.class);
		writeUiComponent(tag.getAttribute("id"), value, structureHandler);

	}

	protected void initializeComponentProperties(Map<String, String> attributes, UiComponentI bean) {
		try {
			Map<String, String> properties = BeanUtils.describe(bean);
			for (Entry<String, String> entry : attributes.entrySet()) {
				String name = entry.getKey();
				String v = entry.getValue();

				if (name.equals(BEAN_NAME_KEY)) {
					continue;
				}

				if (name.length() < 1) {
					logger.warn("Empty property name for {} tag", name);
					continue;
				}

				if (properties.containsKey(name)) {
					logger.debug("Set bean property {} to {}", name, v);
					try {
						BeanUtils.setProperty(bean, name, v);
					} catch (Exception e) {
						logger.warn("Error setting a property value", e);
					}
				}
			}
		} catch (Exception e) {
			logger.warn("Error getting properties", e);
		}
	}

	private void writeUiComponent(IAttribute idAttribute, Map<String, String> attributes,
			IElementTagStructureHandler structureHandler) {
		if (attributes != null && attributes.size() > 0) {
			String componentName = attributes.get(BEAN_NAME_KEY);

			// this can be executed multiple times during page generation, so it must allow
			// to reuse existsing tree
			if (!ctx.canRestoreView()) {
				ctx.createNewTree();
			}

			UiComponentI bean = appContext.getBean(componentName, UiComponentI.class);

			if (bean == null) {
				logger.error("Component {} doesn't exists! Try using a name with a lowercase.", componentName);
				return;
			}

			String clientId = bean.getClientId();

			if (idAttribute != null && idAttribute.getValue() != null) {
				clientId = idAttribute.getValue();
			}

			// special element to make view id available for the client side JS
			structureHandler.setAttribute("class", "ui-root", AttributeValueQuotes.DOUBLE);
			structureHandler.setAttribute("data-view-id", ctx.getViewGuid(), AttributeValueQuotes.DOUBLE);

			initializeComponentProperties(attributes, bean);
			ctx.registerUi(clientId, bean);
			logger.debug("Assigned client id is {}", clientId);

			String result = bean.executePhases();

			if (result != null && result.startsWith("redirect:")) {
				logger.warn("Invalid component response - redirect is not allowed");
				return;
			}

			// ignore?
			// ctx.checkForDanglingComponents();

			// TODO - output notifications

			structureHandler.setBody(result, false);
		} else {
			logger.warn("Empty component attribute map");
		}
	}

}
