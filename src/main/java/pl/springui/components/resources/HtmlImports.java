package pl.springui.components.resources;

import java.util.SortedSet;
import java.util.TreeSet;

import pl.springui.components.UiComponent;
import pl.springui.http.UiCtx;

public abstract class HtmlImports extends UiComponent {

	protected static final String PATH_SEPARATOR = "/";
	protected SortedSet<Link> links = new TreeSet<Link>();

	public HtmlImports(UiCtx ctx) {
		super(ctx);
	}

	public void add(String href, int pos) {
		links.add(new Link(href, pos));
	}

	class Link implements Comparable<Link> {
		String link;
		int position;

		public Link(String link, int position) {
			super();
			this.link = link;
			this.position = position;
		}

		@Override
		public int compareTo(Link o) {

			int pos = position - o.position;
			if (pos == 0) {
				return link.compareTo(o.link);
			}
			return pos;
		}

		private HtmlImports getOuterType() {
			return HtmlImports.this;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((link == null) ? 0 : link.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Link other = (Link) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (link == null) {
				if (other.link != null)
					return false;
			} else if (!link.equals(other.link))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return link;
		}

	}

}