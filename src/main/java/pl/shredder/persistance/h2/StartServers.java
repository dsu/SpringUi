package pl.shredder.persistance.h2;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.h2.tools.Csv;
import org.h2.tools.Server;
import org.h2.tools.SimpleResultSet;

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
public class StartServers {

	public static void main(String... args) throws IOException, SQLException {
		ConnectionFactory db = null;

		Server webServer = Server.createWebServer("-webAllowOthers", "-webPort", "8082").start(); // (4a)
		Server server = Server.createTcpServer("-tcpAllowOthers", "-tcpPort", "9092").start();

		try {
			System.out.println(server.getStatus());
			System.out.println(server.getURL());
			System.out.println(server.getPort());

			System.out.println(webServer.getStatus());
			System.out.println(webServer.getURL());
			System.out.println(webServer.getPort());

			System.out.println("Press any key ...");
			System.in.read();
			db.shutdown();
			System.out.println("shutdown");
		} finally {
			webServer.stop();
			server.stop();
			if (db != null) {
				db.close();
			}
		}
	}

	/**
	 * Test H2 CSV
	 *
	 * @throws SQLException
	 */
	public static void csvTest() throws SQLException {
		SimpleResultSet rs = new SimpleResultSet();
		rs.addColumn("NAME", Types.VARCHAR, 255, 0);
		rs.addColumn("EMAIL", Types.VARCHAR, 255, 0);
		rs.addRow("Bob Meier", "bob.meier@abcde.abc");
		rs.addRow("John Jones", "john.jones@abcde.abc");
		new Csv().write("data/test.csv", rs, null);
	}

	public static void compress(String f) throws FileNotFoundException, IOException, CompressorException {
		FileInputStream in = new FileInputStream(f);
		BufferedInputStream bin = new BufferedInputStream(in);
		CompressorOutputStream compressing = null;
		int buffersize = 8192;
		try {

			FileOutputStream fOut = new FileOutputStream(f + ".xz");
			BufferedOutputStream bout = new BufferedOutputStream(fOut);

			compressing = new CompressorStreamFactory().createCompressorOutputStream(CompressorStreamFactory.XZ, bout);

			final byte[] buffer = new byte[buffersize];
			int n = 0;
			while (-1 != (n = bin.read(buffer))) {
				compressing.write(buffer, 0, n);
			}
		} finally {
			in.close();
			bin.close();
			if (compressing != null) {
				compressing.close();
			}
		}

	}

}
