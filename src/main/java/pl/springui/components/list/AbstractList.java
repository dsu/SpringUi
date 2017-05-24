package pl.springui.components.list;

import java.util.ArrayList;
import java.util.List;

import pl.springui.components.UiComponent;
import pl.springui.http.UiCtx;


public abstract class AbstractList<T> extends UiComponent {

	protected class Page {
		public final int number;
		public final String link;
		public boolean active;

		public Page(int number, String link) {
			this.number = number;
			this.link = link;
		}

	}
	protected List<T> currentElements = new ArrayList<T>();
	/**
	 * Data source - use it only in process stage!
	 */
	protected PagedDataService<T> service;
	protected int currentPage = 1;
	protected int totalElementsCount = 0;
	protected int pageLimit = 5;
	private String pageParamterName = "page";

	private String emptyListMessage = "The list is empty";

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

	public int getPageLimit() {
		return pageLimit;
	}

	public List<Page> getPager() {
		List<Page> pager = new ArrayList<>();
		int total = getTotalElementsCount();
		int pages = total / getPageLimit();
		int extraPages = total % getPageLimit();

		logger.trace("not full pages: {}, full pages: {}", extraPages, pages);
		if (extraPages > 0) {
			pages = ++pages;
		}

		for (int nr = 1; nr <= pages; nr++) {
			Page p = new Page(nr, getPagerLink(nr));
			if (getCurrentPage() == nr) {
				p.active = true;
			}
			pager.add(p);
		}
		return pager;

	}

	protected String getPagerLink(int nr) {
		//return "?page=" + nr;
		return "Ui.load({ids:['"+getClientId()+"'],params:'page="+nr+"'});";
	}

	public PagedDataService<T> getService() {
		return service;
	}

	protected abstract String getTemplatePath();

	public int getTotalElementsCount() {
		return totalElementsCount;
	}

	@Override
	public void process() {
		setCurrentElements(service.geElements(currentPage, pageLimit));
		setTotalElementsCount(service.getAllCount());
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