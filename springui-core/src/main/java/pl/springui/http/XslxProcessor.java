package pl.springui.http;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import pl.springui.components.UiComponentI;
import pl.springui.components.XslXComponent;
import pl.springui.components.exceptions.UiException;
import pl.springui.components.exceptions.UserVisibleError;
import pl.springui.utils.Profiler;

/**
 * Builds CSV from a component that exists in a tree. The component needs to
 * implement CSVComponent interface.
 * 
 * @author dsu
 *
 */
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class XslxProcessor implements AjaxComponentsProcessor {

	@Autowired
	protected UiCtx ctx;

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	@Profiler
	public void process(UiAjaxRequest ajaxCtx) throws IOException {
		try {
			List<UiComponentI> components = ajaxCtx.restoreComponentsFromCtx(ctx);
			if (components.size() == 1) {

				XslXComponent c = (XslXComponent) components.get(0);
				setHttpHeaders(ajaxCtx.getResponse());
				ajaxCtx.getResponse().setHeader("Content-disposition", "attachment; filename=" + c.getXslFileName());
				Workbook book = c.getWorkbook();
				book.write(ajaxCtx.getResponse().getOutputStream());
			} else {
				throw new UiException("Invalid number of components parameter");
			}
		} catch (Exception e) {
			if (ctx.isProductionMode()) {
				logger.error("CSV component processing error", e);
				throw new UserVisibleError("500");
			} else {
				throw new UiException(e);
			}
		}
	}

	protected void setHttpHeaders(HttpServletResponse response) {
		response.setContentType("application/vnd.ms-excel;charset=UTF-8");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
	}

}
