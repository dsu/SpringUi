package pl.shredder.persistance.h2.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Timestamp;

/**
 * Fail safe log
 *
 * @author dsu
 *
 */
public class FailSafeLog {

	public static void log(Object obj) {
		if (obj == null) {
			obj = "NULL";
		}
		try {
			String s = obj.toString();

			if (obj instanceof Throwable) {
				((Throwable) obj).printStackTrace(System.out);
			}
			else {
				System.out.println(s);
			}

		}
		catch (Exception exc) {
			// skip
		}
	}

	public static void log(Object obj, String filename) {
		if (obj == null) {
			obj = "NULL";
		}

		try {
			String s = obj.toString();
			PrintWriter l = new PrintWriter(new BufferedWriter(new FileWriter(filename, true)), true);
			l.print(new Timestamp(System.currentTimeMillis()) + ": ");
			if (obj instanceof Throwable) {
				((Throwable) obj).printStackTrace(l);
			}
			else {
				l.println(s);
			}
			l.close();
		}

		catch (Exception exc) {
			log(obj);
		}
	}

}
