
package pl.shredder.persistance.h2.config;

import java.io.File;

import pl.shredder.persistance.h2.utils.FailSafeLog;

public class DbConfig {

	private static final int version = 1;
	private String filePasswd = "";
	// FIXME
	private String userPasswd = "user";
	private String adminPasswd = "";
	private String userName = "user";
	private static final String adminName = "sa";
	private static final String DB_NAME = "logs" + version;
	private final String dbFolder;

	@Override
	public String toString() {
		return "DbConfig [getDBPath()=" + getDBPath() + ", getAdminName()=" + getAdminName() + ", getAdminPasswd()="
				+ getAdminPasswd() + ", getFilePasswd()=" + getFilePasswd() + ", getUserName()=" + getUserName()
				+ ", getUserPasswd()=" + getUserPasswd() + "]";
	}

	public DbConfig(String dbFolder) {

		this.dbFolder = dbFolder;
	}

	public String getFilePatch() {
		return getFolderPatch() + File.separator + DB_NAME;

	}

	public String getFolderName() {
		return dbFolder;
	}

	public String getFolderPatch() {
		return getBaseDir() + File.separator + dbFolder;
	}

	public static String getBaseDir() {
		return System.getProperty("user.dir") + File.separator + "h2files";
	}

	/**
	 * http://www.h2database.com/html/grammar.html?highlight=LOCK_MODE&search=
	 * LOCK_MODE#set_lock_mode
	 *
	 * @return
	 */
	public String getDBPath() {
		String dBPath = "jdbc:h2:" + getFilePatch()
				+ ";MULTI_THREADED=0;UNDO_LOG=0;LOG=1;LOCK_MODE=3;LOCK_TIMEOUT=5000;MVCC=FALSE;AUTO_SERVER=TRUE"; // AUTO_SERVER=TRUE"

		return dBPath;
	}

	public String getAdminName() {
		return adminName;
	}

	public String getAdminPasswd() {
		if (filePasswd.trim().length() > 1) {
			String pass = filePasswd + " " + adminPasswd;
			FailSafeLog.log(getAdminName() + " pass: " + pass);
			return pass;
		} else {
			return adminPasswd;
		}
	}

	public String getFilePasswd() {
		return filePasswd;
	}

	public String getUserName() {
		return userName;
	}

	public String getUserPasswd() {
		if (filePasswd.trim().length() > 1) {
			String pass = filePasswd + " " + userPasswd;
			FailSafeLog.log(getUserName() + " pass: " + pass);
			return pass;
		} else {
			return userPasswd;
		}
	}

}
