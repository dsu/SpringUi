package pl.springui.http;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import pl.springui.components.CSVComponent;
import pl.springui.components.UiComponentI;
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
public class CSVProcessor implements AjaxComponentsProcessor {

	private static final char SEPARATOR = ',';

	@Autowired
	protected UiCtx ctx;

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	@Profiler
	public void process(UiAjaxRequest ajaxCtx) throws IOException {
		try {
			List<UiComponentI> components = ajaxCtx.restoreComponentsFromCtx(ctx);
			if (components.size() == 1) {
				CSVComponent c = (CSVComponent) components.get(0);
				setCsvHeaders(ajaxCtx.getResponse());
				ajaxCtx.getResponse().setHeader("Content-disposition", "attachment; filename=" + c.getCsvFileName());

				PrintWriter writer = ajaxCtx.getResponse().getWriter();
				CSVPrinter csvFilePrinter = null;
				CSVFormat csvFileFormat = CSVFormat.DEFAULT.withDelimiter(SEPARATOR);

				try {
					csvFilePrinter = new CSVPrinter(writer, csvFileFormat);
					Iterable<?> records = c.getRecords();
					for (Object o : records) {
						logger.trace("Row {}", o);
					}
					csvFilePrinter.printRecords(c.getRecords());
					ajaxCtx.getResponse().getWriter().close();

				} finally {
					try {
						writer.flush();
						writer.close();
						csvFilePrinter.close();
					} catch (IOException e) {
						logger.warn("Cannot finalize CSV writer", e);
					}

				}
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

	protected void setCsvHeaders(HttpServletResponse response) {
		response.setContentType("text/csv;charset=UTF-8");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
	}

}
