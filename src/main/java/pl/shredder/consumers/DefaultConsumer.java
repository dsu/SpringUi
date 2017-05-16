package pl.shredder.consumers;

import pl.shredder.BaseShred;

public class DefaultConsumer extends AbstracConsumer {

	@Override
	protected boolean consume(BaseShred log) {
		System.out.println(log);
		return true;
	}

}
