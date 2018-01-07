package pl.springui.proxy.beans;

import org.springframework.aop.support.AopUtils;

public class BaseBean {

	private static volatile int counter = 0;

	public static String fixedLengthString(String string, int length) {
		return String.format("%1$-" + length + "s", string);
	}

	private static int getCounter() {
		counter = counter + 1;
		return counter;
	}

	public static void print(Object o) {
		System.out.println(o.toString() + "|" + fixedLengthString(o.getClass().getSimpleName(), 50));
	}

	public static void setCounter(int counter) {
		BaseBean.counter = counter;
	}

	private final int id;

	public BaseBean() {
		id = getCounter();
		System.out.println("NEW:" + display());
	}

	private String display() {
		return fixedLengthString(this.getClass().getSimpleName() + " ", 45)
				+ fixedLengthString(AopUtils.getTargetClass(this).getSimpleName(), 45)
				+ fixedLengthString(" [id=" + id + "]", 10);
	}

	public Object getCacheKey() {
		return "";
	}

	public int getId() {
		return id;
	}

	public String renderResponse() {
		return toString();
	}

	@Override
	public String toString() {
		return "    " + display();
	}

}
