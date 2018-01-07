package pl.springui;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.ServletWebRequest;

import pl.springui.components.list.AbstractList;
import pl.springui.components.list.PagedDataService;
import pl.springui.components.tree.MapViewTreeKeeper;
import pl.springui.http.Guid;
import pl.springui.http.PageProcessor;
import pl.springui.http.UiCtx;
import pl.springui.template.engine.Thymeleaf;

public class AbstractListTest {

	MockHttpServletRequest servletRequest;
	MockHttpServletResponse servletResponse;
	UiCtx ctx;
	PageProcessor pageProcessor;

	@Before
	public void initWebCtx() {
		servletRequest = new MockHttpServletRequest();
		servletResponse = new MockHttpServletResponse();
		ServletWebRequest request = new ServletWebRequest(servletRequest);

		Thymeleaf engine = new Thymeleaf();
		engine.init();
		MapViewTreeKeeper treeMapContainer = new MapViewTreeKeeper();
		ctx = new UiCtx(request, treeMapContainer, new Guid(servletRequest));
		pageProcessor = new PageProcessor(ctx);
	}

	@Test
	public void test() throws UnsupportedEncodingException {
		AbstractList<String> abstractList = new AbstractList<String>(ctx) {

			@Override
			public String renderResponse() {

				System.out.println("pages: " + getPager().getPages().size());
				System.out.println("all pages: " + getPager().getAllPages().size());

				assertTrue(getPager().getPages().size() < getPager().getAllPages().size());

				return Arrays.deepToString(getPager().getPages().toArray());
			}

			@Override
			public void restoreView() {

				PagedDataService<String> service = mock(PagedDataService.class);

				ArrayList<String> list = new ArrayList<>();
				for (int i = 0; i <= 100; i++) {
					list.add("page " + i + 1);
				}
				Mockito.when(service.getAllCount()).thenReturn(list.size());

				setService(service);
				super.restoreView();
			}
		};

		pageProcessor.process(servletRequest, servletResponse, abstractList);
		String response = servletResponse.getContentAsString();
		System.out.println("response:");
		System.out.println(response);

	}

}
