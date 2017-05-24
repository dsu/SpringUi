package pl.springui;

import java.io.IOException;

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
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pl.springui.components.UiComponent;
import pl.springui.example.pages.CMSPage;
import pl.springui.example.pages.UserAutoEdit;
import pl.springui.example.pages.UserCrud;
import pl.springui.example.pages.UserEdit;
import pl.springui.example.pages.UserList;
import pl.springui.http.AjaxProcessor;
import pl.springui.http.PageProcessor;

/**
 * Initialize Spring Boot Cache
 * (https://spring.io/blog/2015/06/15/cache-auto-configuration-in-spring-boot-1-3)
 * 
 * Problem with 404 when deploying as war:
 * https://stackoverflow.com/questions/39567434/spring-boot-application-gives-404-when-deployed-to-tomcat-but-works-with-embedde
 * https://stackoverflow.com/questions/3265544/how-can-i-change-the-war-name-generated-by-maven-assembly-plugin
 * https://docs.spring.io/spring-boot/docs/current/reference/html/howto-traditional-deployment.html
 * 
 * @author dsu
 *
 */
@SpringBootApplication
@EnableCaching
@EnableAutoConfiguration(exclude = { org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration.class,
		org.springframework.boot.actuate.autoconfigure.ManagementWebSecurityAutoConfiguration.class })
public class ExampleApp extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(ExampleApp.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(ExampleApp.class);
	}

}

@RestController
class ExampleController {

	@Autowired
	ServletContext context;

	@Autowired
	ApplicationContext ctx;

	@Lazy
	@Autowired
	UserList listPage;

	@Lazy
	@Autowired
	UserEdit userEdit;

	@Lazy
	@Autowired
	UserAutoEdit userAutoEdit;

	@Lazy
	@Autowired
	UserCrud userCrud;

	@Autowired
	PageProcessor pageProcessor;

	@Autowired
	AjaxProcessor ajaxProcessor;

	@Autowired
	CMSPage cms;

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * Na podstawie drzewa z sesji odświeża/przetwarza wybrane komponenty TODO -
	 * zrobić na podstawie jsona
	 * 
	 * @param request
	 * @param response
	 * @param componentIds
	 * @throws IOException
	 */
	@RequestMapping("/ajax")
	void ajax(HttpServletRequest request, HttpServletResponse response, @RequestParam("ids[]") String componentIds)
			throws IOException {
		ajaxProcessor.process(request, response, componentIds);
	}

	// eg. /bw/promocje/test or /bw/test - dla dokumentu
	// eg. /bw/promocje - dla kategorii
	@RequestMapping("/{profile:bw|test}/**/{alias}")
	void cms(@PathVariable String profile, @PathVariable String alias, HttpServletRequest request,
			HttpServletResponse response) {
		logger.debug("CMS alias: {}", alias);
		cms.setAlias(alias);
		pageProcessor.process(request, response, cms);
	}

	public <T extends UiComponent> T get(Class<T> type) {
		T bean = ctx.getBean(type);
		return bean;
	}

	@GetMapping("/500")
	public void showForm(HttpServletRequest request, HttpServletResponse response) {
		logger.debug("500 error page");
		throw new RuntimeException("A 500 error!");
	}

	@RequestMapping(value = { "/", "/crud" }, method = { RequestMethod.GET, RequestMethod.POST })
	public void tpage(HttpServletRequest request, HttpServletResponse response) {
		pageProcessor.process(request, response, userCrud);
	}

	/*
	 * private static final String PATH = "/error";
	 * 
	 * @RequestMapping(value = PATH) public String error(HttpServletRequest
	 * request, HttpServletResponse response) { response.setContentType("text");
	 * response.setHeader("Pragma", "no-cache");
	 * response.setHeader("Cache-Control", "no-cache"); return
	 * "                       |_|                       |_|\r\n                       | |         /^^^\\         | |\r\n                      _| |_      (| \"o\" |)      _| |_\r\n                    _| | | | _    (_---_)    _ | | | |_\r\n                   | | | | |' |    _| |_    | `| | | | |\r\n                   |          |   /     \\   |          |\r\n                    \\        /  / /(. .)\\ \\  \\        /\r\n                      \\    /  / /  | . |  \\ \\  \\    /\r\n                        \\  \\/ /    ||Y||    \\ \\/  /\r\n                         \\__/      || ||      \\__/\r\n                                   () ()\r\n                                   || ||\r\n                                  ooO Ooo"
	 * ; }
	 * 
	 * @Override public String getErrorPath() { return PATH; }
	 */

}