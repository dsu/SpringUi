package pl.shredder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pl.shredder.consumers.AbstracConsumer;
import pl.shredder.consumers.H2Consumer;

public class Configuration {

	private static ExecutorService executor = Executors.newFixedThreadPool(1);

	public static void init() {
		AbstracConsumer consumer = new H2Consumer();
		executor.execute(consumer);
	}

	public static QueueI getQueue() {
		return new Queue();
	}
}
