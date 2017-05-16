package pl.springui;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pl.springui.components.UiComponent;
import pl.springui.example.model.User;
import pl.springui.example.pages.Tpage;
import pl.springui.http.AjaxProcessor;
import pl.springui.http.ComponentsProcessor;

/**
 * Initalize Spring Boot Initalize Spring Cache
 * (https://spring.io/blog/2015/06/15/cache-auto-configuration-in-spring-boot-1-3)
 * 
 * @author dsu
 *
 */
@SpringBootApplication
@EnableCaching
@EnableAutoConfiguration(exclude = { org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration.class,
		org.springframework.boot.actuate.autoconfigure.ManagementWebSecurityAutoConfiguration.class })
public class ExampleApp {

	public static void main(String[] args) {
		SpringApplication.run(ExampleApp.class, args);
	}
}

@RestController
class ExampleController {

	@Autowired
	ServletContext context;

	@Autowired
	ApplicationContext ctx;

	@Autowired
	Tpage tpage;

	@Autowired
	ComponentsProcessor pageProcessor;

	@Autowired
	AjaxProcessor ajaxProcessor;

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@GetMapping("/user")
	public String showForm(HttpServletRequest request, HttpServletResponse response, User user) {
		return pageProcessor.process(request, response, tpage);
	}

	@PostMapping("/user")
	public String checkPersonInfo(HttpServletRequest request, HttpServletResponse response) {
		return pageProcessor.process(request, response, tpage);
	}

	@PostMapping("/user-spring-validation")
	public String checkPersonInfo(HttpServletRequest request, HttpServletResponse response, @Valid User user,
			BindingResult bindingResult) {

		logger.debug("Result : " + user);
		if (bindingResult.hasErrors()) {
			return pageProcessor.process(request, response, tpage);
		}

		return pageProcessor.process(request, response, tpage);
	}

	@RequestMapping(value = { "/", "/tpage" })
	String tpage(HttpServletRequest request, HttpServletResponse response) {
		return pageProcessor.process(request, response, tpage);
	}

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
	void ajax(HttpServletRequest request, HttpServletResponse response, @RequestParam("id[]") String componentIds)
			throws IOException {
		ajaxProcessor.process(request, response, componentIds);
	}

	public <T extends UiComponent> T get(Class<T> type) {
		T bean = ctx.getBean(type);
		return bean;
	}

	@RequestMapping("/hello/{name}")
	String hello(@PathVariable String name) {
		return "Hello, " + name + "!";
	}

}