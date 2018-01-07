package pl.springui;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.ServletWebRequest;

import pl.springui.components.form.NotificationProcessor;
import pl.springui.components.tree.MapViewTreeKeeper;
import pl.springui.example.components.SimpleThymeleafLayout;
import pl.springui.http.Guid;
import pl.springui.http.PageProcessor;
import pl.springui.http.UiCtx;
import pl.springui.template.engine.Thymeleaf;

public class LayoutTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testCache() {

		MockHttpServletRequest servletRequest = new MockHttpServletRequest();
		MockHttpServletResponse servletResponse = new MockHttpServletResponse();
		ServletWebRequest request = new ServletWebRequest(servletRequest);

		Thymeleaf engine = new Thymeleaf();
		engine.init();

		MapViewTreeKeeper treeMapContainer = new MapViewTreeKeeper();
		UiCtx ctx = new UiCtx(request, treeMapContainer, new Guid(servletRequest));
		PageProcessor pageProcessor = new PageProcessor(ctx);
		pageProcessor.setNotificationProcessor(new NotificationProcessor(ctx, engine));
		SimpleThymeleafLayout layout = new SimpleThymeleafLayout(ctx, engine);
		pageProcessor.process(servletRequest, servletResponse, layout);
		layout.clearPhases();

	}
}
