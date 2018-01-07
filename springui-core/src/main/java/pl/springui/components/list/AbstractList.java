package pl.springui.components.list;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import pl.springui.components.UiComponent;
import pl.springui.http.UiCtx;

public abstract class AbstractList<T> extends UiComponent {

	protected class Page implements Serializable {
		public boolean active;
		public final String link;
		public final int number;

		public Page(int number, String link) {
			this.number = number;
			this.link = link;
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
			Page other = (Page) obj;
			if (!getOuterType().equals(other.getOuterType())) {
        return false;
      }
			if (number != other.number) {
        return false;
      }
			return true;
		}

		public String getNumber() {
			if (number > 0) {
				return String.valueOf(number);
			} else {
				return "..";
			}

		}

		private AbstractList getOuterType() {
			return AbstractList.this;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + number;
			return result;
		}

		@Override
		public String toString() {
			return "[" + number + ", link=" + link + ", active=" + active + "]";
		}

	}

	protected class Pager implements Serializable {

		int total = getTotalElementsCount();
		int currentPage = AbstractList.this.getCurrentPage();
		int extraPages = total % getPageLimit();
		int maxDisplayPages = 20;
		int nextPage = 0;
		int pageCount = total / getPageLimit();
		List<Page> pages = new ArrayList<>();

		public Pager() {

			if (extraPages > 0) {
				this.pageCount = this.pageCount + 1;
			}

			for (int nr = firstPageIndex; nr < (pageCount + firstPageIndex); nr++) {
				Page p = new Page(nr, getPagerLink(nr));
				if (getCurrentPage() == nr) {
					p.active = true;
				}
				pages.add(p);
			}

			if (currentPage < pages.size()) {
				nextPage = currentPage + 1;
			}

			logger.debug("not full pageCount: " + extraPages + ", full pageCount: " + pageCount + ", next :" + nextPage
					+ ", current:" + currentPage);
			logger.debug("not full pageCount: {}, full pageCount: {}", extraPages, pageCount);

		}

		public List<Page> getAllPages() {
			return pages;
		}

		public int getCurrentPage() {
			return currentPage;
		}

		/**
		 * Pages on last page or 0
		 * 
		 * @return
		 */
		public int getExtraPages() {
			return extraPages;
		}

		public int getNextPage() {
			return nextPage;
		}

		/**
		 * Pages to display
		 * 
		 * @return
		 */
		public List<Page> getPages() {

			int pagesCount = pages.size();
			int maxMidPages = maxDisplayPages / 2;
			if (pagesCount > maxDisplayPages) {
				ArrayList<AbstractList<T>.Page> pager = new ArrayList<Page>();

				int fromIndex = Math.max(0, getCurrentPage() - maxMidPages);
				int toExclusiveIndex = Math.min(pagesCount, getCurrentPage() + maxMidPages + 1);

				List<AbstractList<T>.Page> subList = pages.subList(fromIndex, toExclusiveIndex);

				if (fromIndex > 1 && pagesCount > 2) {
					List<AbstractList<T>.Page> firstPage = pages.subList(0, 1);
					pager.addAll(firstPage);
					pager.add(new Page(-1, "#"));
				}

				pager.addAll(subList);

				if (toExclusiveIndex < (pagesCount - 1) && pagesCount > 2) {
					List<AbstractList<T>.Page> lastPage = pages.subList(pagesCount - 1, pagesCount);
					pager.add(new Page(-1, "#"));
					pager.addAll(lastPage);
				}

				return pager;
			}

			return pages;
		}

		public int getTotal() {
			return total;
		}

	}

	protected List<T> currentElements = new ArrayList<T>();
	protected int currentPage = 0;
	private String emptyListMessage = "The list is empty";
	protected int firstPageIndex = 1;

	private PagerLinkGenerator<T> linkGenerator;

	protected int pageLimit = 5;
	protected String pageParamterName = "page";

	/**
	 * Data source - use it only in process stage!
	 */
	protected PagedDataService<T> service;

	protected int totalElementsCount = 0;

	public AbstractList(UiCtx ctx) {
		super(ctx);
	}

	public void addElement(T e) {
		currentElements.add(e);
	}

	@Override
	public void applyRequest() {
		String val = getCtx().getReq().getParameter(pageParamterName);
		if (val != null && val.length() > 0) {
			currentPage = Integer.parseInt(val);
		}
		if (currentPage < firstPageIndex) {
			currentPage = 1;
		}
		logger.debug("current page : " + currentPage);
		super.applyRequest();
	}

	public List<T> getCurrentElements() {
		return currentElements;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public String getEmptyListMessage() {
		return emptyListMessage;
	}

	public PagerLinkGenerator<T> getLinkGenerator() {
		return linkGenerator;
	}

	public int getPageLimit() {
		return pageLimit;
	}

	protected Pager getPager() {
		return new Pager();
	}

	protected String getPagerLink(int nr) {
		if (linkGenerator == null) {
			return "Ui.load({ids:['" + getClientId() + "'],params:'page=" + nr
					+ "','callbackAfterLoad':Ui.scrollTop});return false;";
		} else {
			return linkGenerator.generate(this, nr);
		}

	}

	public PagedDataService<T> getService() {
		return service;
	}

	public int getTotalElementsCount() {
		return totalElementsCount;
	}

	@Override
	public void process() {

		if (service == null) {
			logger.warn("List service is null");
		} else {
			setCurrentElements(service.geElements(getCurrentPage(), getPageLimit()));
			setTotalElementsCount(service.getAllCount());
		}
		super.process();
	}

	public void setCurrentElements(List<T> currentElements) {
		this.currentElements = currentElements;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public void setEmptyListMessage(String emptyListMessage) {
		this.emptyListMessage = emptyListMessage;
	}

	public void setLinkGenerator(PagerLinkGenerator<T> linkGenerator) {
		this.linkGenerator = linkGenerator;
	}

	public void setPageLimit(int pageLimit) {
		this.pageLimit = pageLimit;
	}

	public void setService(PagedDataService<T> service) {
		this.service = service;
	}

	public void setTotalElementsCount(int totalElementsCount) {
		this.totalElementsCount = totalElementsCount;
	}

}