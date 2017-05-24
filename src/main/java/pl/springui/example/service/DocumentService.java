package pl.springui.example.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import pl.springui.example.model.CmsDocument;

/**
 * Mockup
 * 
 * @author dsu
 *
 */
@Service
public class DocumentService {

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	protected List<CmsDocument> list = new ArrayList<>();

	{
		list.add(new CmsDocument("Promocje", "", "promocje", true));
		list.add(new CmsDocument("Promocja Specjalna",
				"Lorem ipsum dolor sit amet enim. Etiam ullamcorper. Suspendisse a pellentesque dui, non felis. Maecenas malesuada elit lectus felis, malesuada ultricies. Curabitur et ligula. Ut molestie a, ultricies porta urna. Vestibulum commodo volutpat a, convallis ac, laoreet enim. Phasellus fermentum in, dolor. Pellentesque facilisis. Nulla imperdiet sit amet magna. Vestibulum dapibus, mauris nec malesuada fames ac turpis velit, rhoncus eu, luctus et interdum adipiscing wisi. ",
				"promocja-specjalna", false));

		list.add(new CmsDocument("Testowy",
				"Lorem ipsum dolor sit amet enim. Etiam ullamcorper. Suspendisse a pellentesque dui, non felis. Maecenas malesuada elit lectus felis, malesuada ultricies. Curabitur et ligula. Ut molestie a, ultricies porta urna. Vestibulum commodo volutpat a, convallis ac, laoreet enim. Phasellus fermentum in, dolor. Pellentesque facilisis. Nulla imperdiet sit amet magna. Vestibulum dapibus, mauris nec malesuada fames ac turpis velit, rhoncus eu, luctus et interdum adipiscing wisi. ",
				"test", false));
	}

	public CmsDocument getByAlias(String alias) {
		for (CmsDocument d : list) {
			if (alias.equals(d.getAlias())) {
				return d;
			}
		}
		return null;
	}

	public List<CmsDocument> getSubdocuments(String alias) {
		List<CmsDocument> docs = new ArrayList<>();
		for (CmsDocument d : list) {
			if (!d.isCategory()) {
				docs.add(d);
			}
		}
		return docs;
	}

}
