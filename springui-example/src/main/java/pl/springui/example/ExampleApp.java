package pl.springui.example;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import pl.springui.components.ExampleComponent;
import pl.springui.components.UiComponent;
import pl.springui.components.exceptions.NotFoundException;
import pl.springui.example.model.CmsContext;
import pl.springui.example.pages.CMSPage;
import pl.springui.example.pages.UserCrud;
import pl.springui.http.PageProcessor;

/**
 * @author dsu
 *
 */
@SpringBootApplication
@EnableCaching
@ServletComponentScan
// excludeFilters = @Filter(ExampleComponent.class)
@ComponentScan(value = { "pl.springui" })
@EnableAutoConfiguration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class ExampleApp extends SpringBootServletInitializer {

	public static void main(String[] args) {
		System.out.println("Starting Spring Boot app ...");
		SpringApplication.run(ExampleApp.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(ExampleApp.class);
	}

}

@ExampleComponent
@RestController
class ExampleController {

	@Autowired
	ServletContext context;

	@Autowired
	ApplicationContext ctx;

	@Lazy
	@Autowired
	UserCrud userCrud;

	@Lazy
	@Autowired
	PageProcessor pageProcessor;

	@Lazy
	@Autowired
	CMSPage cms;

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Lazy
	@Autowired
	private CmsContext cmsContext;

	@RequestMapping(value = { "/cms/{alias}", }, method = { RequestMethod.GET, RequestMethod.POST })
	void cms(@PathVariable String alias, HttpServletRequest request, HttpServletResponse response) {

		// Don't repeat a pattern
		String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
		logger.debug("pattern {}, info: {}, servlet path: {}, context path: {}", pattern, request.getPathInfo(),
				request.getServletPath(), request.getContextPath());

		String searchTerm = new AntPathMatcher().extractPathWithinPattern(pattern, request.getServletPath());
		// /equest.getPathInfo() / getServletPath()

		if (alias == null) {
			throw new NotFoundException("Invalid document alias");
		}

		cmsContext.setAlias(alias);// init cmsContext before any other operation
		cmsContext.setPatch(searchTerm);

		pageProcessor.process(request, response, cms);
	}

	@RequestMapping(value = { "/crud", "/" }, method = { RequestMethod.GET, RequestMethod.POST })
	public void tpage(HttpServletRequest request, HttpServletResponse response) {
		pageProcessor.process(request, response, userCrud);
	}

}