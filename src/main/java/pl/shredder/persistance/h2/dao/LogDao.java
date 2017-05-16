package pl.shredder.persistance.h2.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import pl.shredder.persistance.h2.utils.FailSafeLog;

public class LogDao {

	private static final String dbName = "log";

	public static enum Fields implements FieldsI {

		ID("IDENTITY PRIMARY KEY", "id"), TIMESTAMP("TIMESTAMP", "ts"), LOCATION("TEXT", "location"), LEVEL("TEXT",
				"level"), MESSAGE("TEXT", "message"), STACKTRACE("TEXT", "stacktrace"), LOG_EXECUTION_NANO("LONG",
						"log_exec_nanos"), TAGS("ARRAY",
								"tags"), HTTP_CLIENT_INFO("TEXT", "http_client_info"), INPUT_DATA("TEXT", "input_data");

		private String dbType;
		private String name;

		Fields(String dbType, String name) {
			this.dbType = dbType;
			this.name = name;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see persistence.config.FieldsI#getDbType()
		 */
		@Override
		public String getDbType() {
			return dbType;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see persistence.config.FieldsI#getName()
		 */
		@Override
		public String getName() {
			return name;
		}
	}

	public static void insertStore(Connection connection, Map<String, Object> values) throws SQLException {
		PreparedStatement statement = null;
		try {
			ArrayList<String> keys = new ArrayList(values.keySet());

			StringBuffer names = new StringBuffer();
			StringBuffer blanks = new StringBuffer();

			int count = 0;
			for (String k : keys) {
				if (names.length() > 0) {
					names.append(",");
					blanks.append(",");
				}
				count++;
				blanks.append("?");
				names.append(k);
			}
			String q = "INSERT INTO " + getDbname() + " (" + names + " ) VALUES (" + blanks + ")";

			statement = connection.prepareStatement(q);

			int index = 1;
			for (String k : keys) {
				// Log.log("ADD:" + values.get(k));
				statement.setObject(index, values.get(k));
				index++;
			}

			// Log.log("INSERT: " + q);
			statement.executeUpdate();
			connection.commit();
		} catch (Exception ex) {
			FailSafeLog.log(ex);
			FailSafeLog.log(Arrays.toString(values.entrySet().toArray()));
		} finally {
			connection.close();
			statement.close();
		}

	}

	public static String getDbname() {
		return dbName;
	}

}
