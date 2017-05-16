package pl.shredder;

public interface QueueI {

	void add(BaseShred s);

	boolean isEmpty();

	BaseShred take() throws InterruptedException;

}
