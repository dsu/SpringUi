package pl.shredder.persistance.h2;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Tylko przyklady
 *
 * @author dsu
 *
 */
@Deprecated
public class DaoHelper {

	private DaoHelper() {
	}

	public static int getFolderId(String name, Connection connection) throws SQLException {
		Integer id = null;
		PreparedStatement statement = null;
		statement = connection.prepareStatement("SELECT ID from FOLDER WHERE NAME=?", Statement.RETURN_GENERATED_KEYS);
		statement.setString(1, name);
		ResultSet resultSet = statement.executeQuery();
		while (resultSet.next()) {
			id = resultSet.getInt("ID");
		}
		if (id == null) {
			throw new SQLException("There is no matching folder!");
		}
		else {
			return id;
		}
	}

	public static int getEmailId(String name, Connection connection) throws SQLException {
		PreparedStatement statement = null;
		Integer id = null;

		statement = connection.prepareStatement("SELECT ID from STORE WHERE NAME=?");
		statement.setString(1, name);
		statement.execute();
		ResultSet resultSet = statement.executeQuery();
		while (resultSet.next()) {
			id = resultSet.getInt("ID");
		}
		if (id == null) {
			throw new SQLException("There is no matching email configuration for " + name + "!");
		}
		else {
			return id;
		}

	}

	public static void insertStore(Connection connection, Timestamp v1, String v2) throws SQLException {
		PreparedStatement statement = null;
		connection.setAutoCommit(false);
		statement = connection.prepareStatement("INSERT INTO STORE (ts , message ) VALUES (?,?)",
				Statement.RETURN_GENERATED_KEYS);
		statement.setTimestamp(1, v1);
		statement.setString(2, v2);
		statement.executeUpdate();
		ResultSet generatedKeys = statement.getGeneratedKeys();
		connection.commit();
		connection.close();
		statement.close();

	}

	public static void insertFolderWithStoreInfo(String name, String path, List<Long> emailIDs, Connection connection)
			throws SQLException {
		PreparedStatement statement = null;
		connection.setAutoCommit(false);
		statement = connection.prepareStatement("INSERT INTO FOLDER (NAME , PATH ) VALUES (?,?)",
				Statement.RETURN_GENERATED_KEYS);
		statement.setString(1, name);
		statement.setString(2, path);
		statement.executeUpdate();
		ResultSet generatedKeys = statement.getGeneratedKeys();
		if (generatedKeys.next()) {
			// generatedKeys.getLong(1) - generated key

			for (Long eID : emailIDs) {
				statement = connection.prepareStatement("INSERT INTO FOLDER_STORE (FOLDER_ID, STORE_ID) VALUES (?,?)",
						Statement.RETURN_GENERATED_KEYS);
				statement.setLong(1, generatedKeys.getLong(1));
				statement.setLong(2, eID);

				statement.executeUpdate();
				connection.commit();
			}
		}
		else {
			throw new SQLException("Creating user failed, no generated key obtained.");
		}

	}

	public static void close(PreparedStatement stmt) {
		try {
			if (stmt != null) {
				stmt.close();
			}
		}
		catch (SQLException sqle) {
			sqle.printStackTrace();
		}
	}

	public static void close(ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
		}
		catch (SQLException sqle) {
			sqle.printStackTrace();
		}

	}

	public static final String FILE_SEPARATOR = File.separator;
	public static final String ESCAPED_FILE_SEPARATOR = Pattern.quote(FILE_SEPARATOR);

	public static List<String> getDistinctFilePathLikeAndNotLike(String pattern, String notPattern, long folderId,
			Connection connection) throws SQLException, FileNotFoundException {

		LinkedList<String> list = new LinkedList<String>();
		PreparedStatement statement = null;
		statement = connection.prepareStatement(
				"SELECT DISTINCT PATH FROM FILE WHERE FOLDER_ID = ? AND PATH IS NOT NULL AND PATH LIKE ? AND PATH NOT LIKE '' AND PATH NOT LIKE ? ;");
		statement.setLong(1, folderId);
		statement.setString(2, pattern);
		statement.setString(3, notPattern);
		ResultSet rs = statement.executeQuery();
		while (rs.next()) {
			list.add(rs.getString((int) 1));
		}
		return list;

	}

}
