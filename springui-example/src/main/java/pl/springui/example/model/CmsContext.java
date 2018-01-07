package pl.springui.example.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import pl.springui.components.ExampleComponent;
import pl.springui.example.service.DocumentService;

/**
 * Represents a CMS document that is assigned to current URI/request path
 * 
 * @author dsu
 *
 */
@Component
@ExampleComponent
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CmsContext {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private DocumentService service;
	private List<String> path = new ArrayList<>();

	private String alias;

	@Autowired
	public CmsContext(DocumentService service) {
		super();
		this.service = service;
		logger.debug("CmsContext created");
	}

	public String getAlias() {
		return alias;
	}

	public CmsDocument getDocument() {

		return service.getByAlias(alias);
	}

	public List<CmsDocument> getSubdocuments() {
		return service.getSubdocuments(alias);
	}

	public void setAlias(String alias) {
		logger.debug("CmsContext alias {}", alias);
		this.alias = alias;// TODO - tutaj ladownaie dokuemntu z bazy
	}

	public void setPatch(String stringPath) {
		if (stringPath != null) {
			String[] split = stringPath.split("\\/");
			path = Arrays.asList(split);
		}
		logger.debug("CMS Path : {}", stringPath);
	}

	@Override
	public String toString() {
		return "CmsContext [alias=" + alias + "]";
	}

}
