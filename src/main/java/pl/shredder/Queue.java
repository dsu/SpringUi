package pl.shredder;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Queue implements QueueI {

	BlockingQueue<BaseShred> queue = new LinkedBlockingQueue<BaseShred>();

	@Override
	public void add(BaseShred s) {
		queue.add(s);

	}

	@Override
	public boolean isEmpty() {
		return queue.isEmpty();
	}

	@Override
	public BaseShred take() throws InterruptedException {
		return queue.take();
	}

}
