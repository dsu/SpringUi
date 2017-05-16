package pl.shredder;

public class Test {
	public static void main(String... strings) throws InterruptedException {

		long start = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
			Exception e = new Exception("EX");
			BaseShred.trace().msg("EXCEPTION").log(e).flush();
		}
		long end = System.currentTimeMillis() - start;
		System.out.println("INSERTED ALL..." + end);

	}
}
