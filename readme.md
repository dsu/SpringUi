# SpringUi 
![alt text](https://github.com//dsu/images/blob/master/logo.png?raw=true "logo")

## Motivations

To create component based UI framework on top of Spring Boot to be easily integrated with other servlet based frameworks.
Components are not meant to meet all the needs out of the box. It should be as easy as possible to write custom component that does what is needed in the current project.

### Requirements

* Server based rendering.
* Reusable UI components, or at least should allow to find code that can be reused and use it easily (for example creating XSL report, PDF, or JSON or file manager button).
* Component that can be tested separately.
* Should be easily integrated with Spring MVC.
* Is should allow to create simple CRUD web pages as fast as possible.
* It should allow to create component in any popular view technology (Thymeleaf, Freemarker, GSON, JSON.org, XSLT) to be able to migrate easily from any other server based rendering technology  used by a dev team.
* It should allow to optimize your web page by allowing to cache components (using Spring's @Cacheable).
* Is should support AJAX component refreshing - similar to JSF AJAX support.
* It should be possible to use it with jQuery and Bootstrap3.
* Component should be easy to write and integrate

### Evaluation of other frameworks

* Spring MVC
 - It's pure request based framework.
 - Spring MVC introduces ViewResolvers, views are decoupled from the controllers by them.
You need to configure your view resolvers first and then you can write your controller method. Your controller has not control over how it will be actually rendered. 
You can configure multiple view resolvers, but they are not independent. 
Resource can shadow another another by having the same logical name pointing to different resources given different view resolvers.
It seems to be too complicated and doesn't matches simplicity Spring Boot is trying to deliver. Spring Boot will configure ViewResolvers for you but it is still under the hood. 
SpringUi pages are build on top of RestControllers. RestController methods can return anything in request response - just as in pure servlets. 

* Tapestry
 - Tapestry allows to write your own component easily. Its very well designed framework but it uses its own IoC container and template engine. We wanted to be able to use Spring and Thymeleaf instead.

* Vaadin
 - components need to be compiled.
 - a little control over actual HTTP request and response. 

* JSF
 - It forces you to use provided grid system, css styles.
 - The existing components are complex and complicated - it is not practical to extend them on your own.

## DOC



You create a component by extending an abstract class *UiComponent*.
A component can have children components.
A parent is responsible for registering its children in the *UiContext*.
*UiContext* can be autowired to a component. It gives direct access to request and response. You can access any other component form context.
You can use context/request attributes to communicate with other component. Parent component can also pass data between components.
A component can render a whole page or only a part of it.

*PageProcessor* is responsible for rendering a HTML page based on any root component. There are also processors for CSV and XLS.
*AjaxProcessor* is responsible for preparing a JSON that is evaluated by ui.js on the client side. 

*UiComponent* renders its HTML code in *renderResponse* lifecycle method. It returns a *java.util.String*. It allows to embed XSLT component within Thymeleaf component and so forth.

The lifecycle methods are similar to JSF lifecycle, however you can use them as you like, but when you follow the convention other components can assume the state of other components and for example get a data from it. A parent method is responsible for executing a lifecycle methods of its children.

Lifecycle methods are:

* void restoreView() - register all child components
* void applyRequest() - store all required request parameter
* void process() - execute server logic
* String renderResponse(); - render HTML for the component

This JSON contains HTML and JS code of any number of components that needs to be rerendered.
*ui.js* contains utility functions to serialize and execute AJAX request. JQuery is required.
Any component has its own client id  - that can serve as DOM id. It can be set manually.

When component is rendered the whole *component tree* needs to be persisted (in the session or in a cache) so it can be referenced by any AJAX request and refreshed.
Each view tree has its own unique key - *viewGuid*.

Your controller can look like below. The root components are annotated as @Lazy so it won't create a component when it is not used within a request.

```java
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
	
	@Lazy
	@Autowired
	private CmsContext cmsContext;

	@RequestMapping(value = { "/cms/{alias}", }, method = { RequestMethod.GET, RequestMethod.POST })
	void cms(@PathVariable String alias, HttpServletRequest request, HttpServletResponse response) {h(searchTerm);
		pageProcessor.process(request, response, cms);
	}

	@RequestMapping(value = { "/crud", "/" }, method = { RequestMethod.GET, RequestMethod.POST })
	public void tpage(HttpServletRequest request, HttpServletResponse response) {
		pageProcessor.process(request, response, userCrud);
	}

}
```

An example of a simple component.

```java
@Component
@Scope("prototype")
public class SimpleThymeleafLayout extends UiComponent {

	private MapTemplateEngine engine;

	@Autowired
	private CachedFooter footer;

	@Autowired
	public SimpleThymeleafLayout(UiCtx ctx, @Qualifier("thymeleaf") MapTemplateEngine engine) {
		super(ctx);
		this.engine = engine;
	}

	@Profiler
	@Override
	public String renderResponse() {
		return engine.procesTemplateAsString(viewModel, "examples/layout.xhtml");
	}

	@Override
	public void restoreView() {
		addChild(footer);
		super.restoreView();
	}

}
```

Examples screenshoot:

![alt text](https://github.com//dsu/images/blob/master/crud.png?raw=true "CRUD")

![alt text](https://github.com//dsu/images/blob/master/crud-validation.png?raw=true "Validation")

![alt text](https://github.com//dsu/images/blob/master/cms.png?raw=true "Simple CMS ")