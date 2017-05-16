package pl.shredder.persistance.h2;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.h2.jdbcx.JdbcConnectionPool;

import pl.shredder.persistance.h2.config.DbConfig;
import pl.shredder.persistance.h2.dao.FieldsI;
import pl.shredder.persistance.h2.dao.LogDao;
import pl.shredder.persistance.h2.exceptions.DaoException;
import pl.shredder.persistance.h2.exceptions.IORuntimeException;
import pl.shredder.persistance.h2.utils.FailSafeLog;

public class ConnectionFactory {

	private JdbcConnectionPool cp = null;
	private DbConfig data = null;
	private boolean isShutdown;

	private static final int LOGIN_TIMEOUT_MS = 5000;
	private static final int MAX_CONNECTIONS = 1;

	public DbConfig getData() {
		return data;
	}

	protected ConnectionFactory(DbConfig data) {
		this.data = data;
		try {
			boolean initConnectionPool = initConnectionPool();
			if (initConnectionPool) {
				replaceDefaultUser();

				if (!areTablesCreated()) {
					createTables();
				}
			}

		}
		catch (SQLException ex) {
			throw new DaoException("Problem occured during creating database connection or tables", ex);
		}
	}

	private boolean initConnectionPool() {
		boolean initialized = false;
		if (cp == null) {
			synchronized (this.getClass()) {
				if (cp == null) {

					try {
						Class.forName("org.h2.Driver"); // loads driver class
					}
					catch (ClassNotFoundException ex) {
						throw new DaoException("Cannot load driver class", ex);
					}

					cp = JdbcConnectionPool.create(data.getDBPath(), data.getUserName(), data.getUserPasswd());
					cp.setMaxConnections(MAX_CONNECTIONS);
					cp.setLoginTimeout(LOGIN_TIMEOUT_MS);
					initialized = true;
				}
			}
		}
		return initialized;
	}

	public Connection getConnection() throws SQLException {

		if (!isShutdown) {
			return cp.getConnection();
		}
		else {
			throw new IORuntimeException("Database shutting down ...");
		}
	}

	public void replaceDefaultUser() {
		Connection connect = null;
		PreparedStatement statement = null;
		PreparedStatement deleteStatement = null;
		try {
			FailSafeLog.log("Creating new DB user, deleting old ...");
			connect = cp.getConnection();
			String modPasswd = "CREATE USER IF NOT EXISTS " + data.getUserName() + " PASSWORD '" + data.getUserPasswd()
					+ "'";
			statement = connect.prepareStatement(modPasswd);
			statement.executeUpdate();

			String deleteOldUser = "DROP USER IF EXISTS su";
			deleteStatement = connect.prepareStatement(deleteOldUser);
			statement.executeUpdate();

		}
		catch (Exception ex2) {
			FailSafeLog.log(ex2);
		}
		finally {
			close(statement);
			close(deleteStatement);
			close(connect);
		}
	}

	public void close(Connection conn) {
		try {
			if (conn != null) {
				conn.close();
			}
		}
		catch (SQLException sqle) {
			FailSafeLog.log(sqle);
		}
	}

	public void close() {

		if (cp != null) {
			cp.dispose();
		}
	}

	private boolean tablesCreated = false;

	private synchronized boolean areTablesCreated() throws SQLException, DaoException {
		if (!tablesCreated) {
			Connection connection = null;
			PreparedStatement statement = null;
			try {

				connection = getConnection();

				statement = connection.prepareStatement(
						"SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME='" + LogDao.getDbname() + "'");
				ResultSet rs = statement.executeQuery();

				while (rs.next()) {
					tablesCreated = rs.getInt("COUNT(*)") != 0;
				}

			}
			finally {
				close(statement);
				close(connection);
			}
		}

		return tablesCreated;

	}

	private synchronized void createTables() throws SQLException {
		Connection connect = cp.getConnection();
		connect.setAutoCommit(false);
		FailSafeLog.log("Creating tables ...");
		PreparedStatement statement = null;
		try {
			connect.setAutoCommit(false);
			StringBuilder sb = new StringBuilder();
			for (FieldsI f : LogDao.Fields.values()) {
				if (sb.length() > 0) {
					sb.append(",");
				}
				sb.append(f.getName());
				sb.append(" ");
				sb.append(f.getDbType());
			}

			String createStore = "CREATE TABLE IF NOT EXISTS " + LogDao.getDbname() + " (" + sb.toString() + ");";

			FailSafeLog.log("Creating tables:" + createStore);

			statement = connect.prepareStatement(createStore);
			statement.executeUpdate();

			connect.commit();

		}
		finally {
			close(statement);
			close(connect);
		}

	}

	public void close(PreparedStatement statement) {
		if (statement != null) {
			try {
				statement.close();
			}
			catch (SQLException e) {
				FailSafeLog.log(e);
			}
		}
	}

	/**
	 * http://h2-database.66688.n3.nabble.com/deleting-a-database-file-
	 * programmatically-td1021594.html
	 *
	 * @throws SQLException
	 */
	public synchronized void shutdown() throws SQLException {
		FailSafeLog.log("shutting down ... " + data.getFolderName());

		Connection connect = cp.getConnection();
		PreparedStatement statement = null;
		try {
			isShutdown = true;
			String q = "SHUTDOWN;";
			statement = connect.prepareStatement(q);
			boolean execute = statement.execute();
			FailSafeLog.log("Shutted down : " + execute + " , " + data.getFolderName());
		}
		finally {
			close(statement);
			close(connect);
		}

	}

	public void restore() throws SQLException {
		FailSafeLog.log(data.getDBPath() + "from CSV ... ");
		Connection connect = cp.getConnection();
		PreparedStatement statement = null;
		try {
			String fileName = DbConfig.getBaseDir() + File.separator + data.getFolderName();
			String q = "CREATE TABLE CSV AS SELECT * FROM CSVREAD( '" + fileName + ".csv')";
			statement = connect.prepareStatement(q);
			boolean execute = statement.execute();
			FailSafeLog.log("Restored  from CSV : " + execute + ", " + fileName);
		}
		finally {
			close(statement);
			close(connect);
		}

	}

	/**
	 * http://h2database.com/html/tutorial.html#csv
	 *
	 * @throws SQLException
	 */
	public String backup() throws SQLException {
		FailSafeLog.log(data.getFolderName() + " to CSV ... ");
		Connection connect = cp.getConnection();
		PreparedStatement statement = null;
		try {

			String fileName = DbConfig.getBaseDir() + File.separator + data.getFolderName() + ".csv";
			FailSafeLog.log("CSV patch : " + fileName);
			String q = "call CSVWRITE ( '" + fileName + "', 'SELECT * FROM " + LogDao.getDbname() + "' )";
			statement = connect.prepareStatement(q);
			boolean execute = statement.execute();
			FailSafeLog.log(" saved as CSV : " + execute);
			return fileName;
		}
		finally {
			close(statement);
			close(connect);
		}

	}

}
