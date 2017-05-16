package pl.shredder;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import pl.shredder.consumers.AbstracConsumer;

@Component
public class BaseShred {

	public enum Level {
		ERROR, FATAL, INFO, OFF, TRACE, WARN
	}

	private static final StackTraceElement[] EMPTY_CALLER_DATA_ARRAY = new StackTraceElement[] {};

	private static long id = 1;

	public static final int LINE_NA = -1;

	static {
		Configuration.init();
	}

	public static BaseShred error() {
		BaseShred s = new BaseShred(Level.ERROR);
		return s;
	}

	public static StackTraceElement[] extractStackTraceElement(Throwable t, final int maxDepth) {
		if (t == null) {
			return null;
		}

		StackTraceElement[] steArray = t.getStackTrace();
		StackTraceElement[] callerDataArray;

		int found = LINE_NA;
		for (int i = 0; i < steArray.length; i++) {

			boolean isCodeOutside = steArray[i].getClassName().endsWith(BaseShred.class.getName());
			if (isCodeOutside) {
				found = i + 1;
			} else {
				if (found != LINE_NA) {

					break;
				}
			}
		}

		// we failed to extract caller data
		if (found == LINE_NA) {

			return EMPTY_CALLER_DATA_ARRAY;
		}

		int availableDepth = steArray.length - found;
		int desiredDepth = maxDepth < (availableDepth) ? maxDepth : availableDepth;

		callerDataArray = new StackTraceElement[desiredDepth];
		for (int i = 0; i < desiredDepth; i++) {
			callerDataArray[i] = steArray[found + i];

		}
		return callerDataArray;
	}

	public static long getId() {
		return id;
	}

	public static BaseShred ifo() {
		BaseShred s = new BaseShred(Level.INFO);
		return s;
	}

	public static BaseShred trace() {
		BaseShred s = new BaseShred(Level.TRACE);
		return s;
	}

	public static BaseShred warn() {
		BaseShred s = new BaseShred(Level.WARN);
		return s;
	}

	private String caller;

	private String clienthostinfo = null;

	private long elapsedNanos = 0;

	private String httpClientInfo;

	private final Level level;

	private String message;

	protected long startTime = 0;

	private Throwable throwable;

	/**
	 * Timestamp of log
	 */
	private final Timestamp ts;

	private String[] tags;

	private Object inputData;

	protected BaseShred(Level level) {
		super();
		startTime = System.nanoTime();
		this.ts = new Timestamp(new Date().getTime());
		id++;
		this.caller = printCaller();
		this.level = level;

	}

	public void flush() {
		setElapsedTime();
		AbstracConsumer.queue(this);

	}

	public String getCaller() {
		return caller;
	}

	public String getClienthostinfo() {
		return clienthostinfo;
	}

	public String getHttpClientInfo() {
		return httpClientInfo;
	}

	public String getLevel() {
		return level.name();
	}

	public String getMessage() {
		return message;
	}

	public Throwable getThrowable() {
		return throwable;
	}

	public Timestamp getTs() {
		return ts;
	}

	public void log(javax.servlet.http.HttpServletRequest request) {

		String remoteHost = request.getRemoteHost();
		String remoteAddr = request.getRemoteAddr();
		if (remoteAddr.equals("0:0:0:0:0:0:0:1")) {
			InetAddress localip;
			try {
				localip = java.net.InetAddress.getLocalHost();
				remoteAddr = localip.getHostAddress();
				remoteHost = localip.getHostName();
			} catch (UnknownHostException e) {
				// skip
			}

		}
		int remotePort = request.getRemotePort();
		String userAgent = request.getHeader("User-Agent");
		clienthostinfo = remoteHost + " (" + remoteAddr + " : " + remotePort + ") " + userAgent;
		this.httpClientInfo = clienthostinfo;

	}

	public BaseShred log(Throwable t) {
		if (t != null) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			t.printStackTrace(pw);
			this.throwable = t;
		}

		return this;
	}

	public BaseShred msg(String string) {
		this.message = string;
		return this;
	}

	public BaseShred msg(String string, Object... objects) {
		this.message = String.format(string, objects);
		return this;
	}

	public BaseShred input(Object o) {
		this.inputData = o;
		return this;
	}

	private String printCaller() {

		StackTraceElement[] cda = extractStackTraceElement(new Throwable(), 10);
		if (cda != null && cda.length > 0) {
			return cda[0].toString();
		} else {
			return null;
		}

	}

	protected void setElapsedTime() {
		long stopTime = System.nanoTime();
		elapsedNanos = stopTime - startTime;
		// this.elapsedMs = elapsedTime / 1000000f; // ms

	}

	public void setHttpClientInfo(String httpClientInfo) {
		this.httpClientInfo = httpClientInfo;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}

	public String getThrowableAsString() {

		if (throwable != null) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			throwable.printStackTrace(pw);
			return sw.toString();
		} else {
			return null;
		}
	}

	public void addTag(String... tag) {
		this.tags = tag;
	}

	public String[] getTags() {
		return tags;
	}

	public long getLogExecutionNanos() {
		return elapsedNanos;
	}

	public String getInputDataAsString() {
		if (this.inputData == null) {
			return null;
		} else {
			return this.inputData.toString();
		}
	}

}
