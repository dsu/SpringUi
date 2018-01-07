package pl.springui.example.model;

import java.util.HashMap;
import java.util.Map;

public class CmsDocument {

	private final String title;
	private String content;
	private final String alias;
	private boolean isCategory;

	public CmsDocument(String title, String alias) {
		super();
		this.title = title;
		this.alias = alias;
	}

	public CmsDocument(String title, String content, String alias, boolean isCategory) {
		super();
		this.title = title;
		this.content = content;
		this.alias = alias;
		this.isCategory = isCategory;
	}

	public Map<String, Object> asMap() {
		HashMap<String, Object> map = new HashMap<>();
		map.put("title", getTitle());
		map.put("content", getContent());
		map.put("alias", getAlias());
		return map;

	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CmsDocument other = (CmsDocument) obj;
		if (alias == null) {
			if (other.alias != null)
				return false;
		} else if (!alias.equals(other.alias))
			return false;
		return true;
	}

	public String getAlias() {
		return alias;
	}

	public String getContent() {
		return content;
	}

	public String getTitle() {
		return title;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((alias == null) ? 0 : alias.hashCode());
		return result;
	}

	public boolean isCategory() {
		return isCategory;
	}

	public void setCategory(boolean isCategory) {
		this.isCategory = isCategory;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
