package pl.shredder.persistance.h2;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.compress.compressors.CompressorException;

import pl.shredder.persistance.h2.dao.LogDao;

/**
 * Add do web.xml to rub web serwer as servlet:
 *
 * <pre>
 	<!-- H2 Database Console for managing the app's database -->
	<servlet>
		<servlet-name>H2Console</servlet-name>
		<servlet-class>org.h2.server.web.WebServlet</servlet-class>
		<init-param>
			<param-name>-webAllowOthers</param-name>
			<param-value>true</param-value>
		</init-param>
		<load-on-startup>2</load-on-startup>
	</servlet>

	<servlet-mapping>
		<servlet-name>H2Console</servlet-name>
		<url-pattern>/h2/*</url-pattern>
	</servlet-mapping>
 * </pre>
 *
 * @author dsu
 *
 */
public class BasicTest {

	public static void main(String... args) throws IOException, SQLException, CompressorException {
		testdB();
	}

	public static void testdB() throws SQLException, IOException {

		HashMap<String, Object> values = new HashMap<String, Object>();
		int LIMIT = (24 * 60);
		int count = 0;
		System.out.println("INSERTING ...");
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < LIMIT; i++) {
			values.put(LogDao.Fields.TIMESTAMP.getName(), new Timestamp(new Date().getTime()));
			values.put(LogDao.Fields.MESSAGE.getName(), "COŚ " + Math.random());
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// System.out.println("INSERT :" + count);
			values.put(LogDao.Fields.TAGS.getName(), new String[] { "A", "B", "" + Math.random() });

			LogDao.insertStore(RetentionConnectionFactory.getConnection(), values);
			count++;
		}
		System.out.println("INSERTED:" + count);
		System.out.println("elapsed:" + (System.currentTimeMillis() - startTime));

		System.out.println("Press any key ...");
		System.in.read();
		// sdb.shutdown();
		System.out.println("shutdown");
	}

}
