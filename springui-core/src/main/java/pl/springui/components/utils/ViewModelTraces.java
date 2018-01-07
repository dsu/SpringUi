package pl.springui.components.utils;

import java.util.Queue;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.collect.EvictingQueue;

@Component
public class ViewModelTraces {
	protected static final int MAX_VIEWS = 10;

	/**
	 * http://blog.codeleak.pl/2015/09/placeholders-support-in-value.html
	 */
	@Value("${springui.traceoutput:false}")
	protected boolean enabled;

	private Queue<ViewModelTrace> traces = EvictingQueue.create(MAX_VIEWS);

	public void addTrace(ViewModelTrace trace) {
		traces.add(trace);
	}

	public Queue<ViewModelTrace> getTraces() {
		return traces;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
