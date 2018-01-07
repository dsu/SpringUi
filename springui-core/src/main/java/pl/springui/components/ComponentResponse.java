package pl.springui.components;

public class ComponentResponse {

	private String html;
	private String js;

	public ComponentResponse(String js, String html) {
		super();
		this.js = js;
		this.html = html;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
      return true;
    }
		if (obj == null) {
      return false;
    }
		if (getClass() != obj.getClass()) {
      return false;
    }
		ComponentResponse other = (ComponentResponse) obj;
		if (html == null) {
			if (other.html != null) {
        return false;
      }
		} else if (!html.equals(other.html)) {
      return false;
    }
		if (js == null) {
			if (other.js != null) {
        return false;
      }
		} else if (!js.equals(other.js)) {
      return false;
    }
		return true;
	}

	public String getHtml() {
		return html;
	}

	public String getJs() {
		return js;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((html == null) ? 0 : html.hashCode());
		result = prime * result + ((js == null) ? 0 : js.hashCode());
		return result;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public void setJs(String js) {
		this.js = js;
	}

}
