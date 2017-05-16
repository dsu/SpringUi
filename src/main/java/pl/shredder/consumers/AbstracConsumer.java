package pl.shredder.consumers;

import pl.shredder.BaseShred;
import pl.shredder.Configuration;
import pl.shredder.QueueI;
import pl.shredder.persistance.h2.utils.FailSafeLog;

public abstract class AbstracConsumer implements Runnable {

	private static final QueueI queue = Configuration.getQueue();

	private boolean active = true;

	private static int fails = 0;

	public static void queue(BaseShred s) {
		queue.add(s);
	}

	@Override
	public void run() {
		while (active) {

			while (!queue.isEmpty()) {
				BaseShred take;
				try {
					take = queue.take();

					if (!consume(take)) {
						FailSafeLog.log(take);
					}
				}
				catch (InterruptedException e) {
					FailSafeLog.log(e);
				}
			}
		}

	}

	protected abstract boolean consume(BaseShred take);

}