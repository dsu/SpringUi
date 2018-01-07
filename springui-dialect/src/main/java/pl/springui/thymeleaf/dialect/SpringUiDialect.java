package pl.springui.thymeleaf.dialect;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.thymeleaf.dialect.IProcessorDialect;
import org.thymeleaf.processor.IProcessor;

@Configuration
@Component
public class SpringUiDialect implements IProcessorDialect {

	private static final int PRECEDENCE = 1000;
	private static final String DIALECT_NAME = "springui";
	public static final String PREFIX = "sui";

	@Autowired
	ComponentAttributeTagProcessor processor;

	@Override
	public int getDialectProcessorPrecedence() {
		return PRECEDENCE;
	}

	@Override
	public String getName() {
		return DIALECT_NAME;
	}

	@Override
	public String getPrefix() {
		return PREFIX;
	}

	@Override
	public Set<IProcessor> getProcessors(String dialectPrefix) {
		final Set<IProcessor> processors = new HashSet<IProcessor>();
		processors.add(processor);
		return processors;
	}

}