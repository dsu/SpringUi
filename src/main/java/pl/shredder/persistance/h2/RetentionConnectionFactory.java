package pl.shredder.persistance.h2;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

import pl.shredder.persistance.h2.config.DbConfig;
import pl.shredder.persistance.h2.exceptions.ConfigurationException;
import pl.shredder.persistance.h2.utils.FailSafeLog;

public class RetentionConnectionFactory {

	private static final int SECS_TO_CHECK_STATUS = 30 * 1;

	private static final int MINS_TO_CHECK_OLD_FILES = 1;

	private static final String FILE_DATE_FORMAT = "yyyy-MM-dd";

	private volatile static String currentFolderName = null;

	private volatile static Calendar lastStatusChecked = null;

	private volatile static Calendar lastOldFilesChecked = null;

	private volatile static ConnectionFactory currentFactory = null;

	private static final Object factoryAccesMonitor = new Object();

	private static ExecutorService executor = Executors.newFixedThreadPool(1);

	static {
		executor.execute(new Runnable() {

			private boolean run = true;
			protected static final long SLEEP_MS = 30 * 1000;

			@Override
			public void run() {
				while (run) {

					try {
						Thread.sleep(SLEEP_MS);
					}
					catch (InterruptedException e) {

					}
					switchDbFolderIfNeeded();
				}

			}
		});
	}

	private static ConnectionFactory getFactory() {

		initFolderName();

		if (currentFolderName == null) {
			throw new ConfigurationException("DB folder name is not set");
		}

		if (currentFactory == null) {
			synchronized (factoryAccesMonitor) {
				if (currentFactory == null) {
					// create new factory
					FailSafeLog.log("Creating new DB factory ...");
					DbConfig conf = new DbConfig(currentFolderName);
					currentFactory = new ConnectionFactory(conf);
					FailSafeLog.log("Db factory created : " + conf);
				}
			}
		}

		return currentFactory;

	}

	private static Pattern DATE_PATTERN = Pattern.compile("\\d{4}-([0][1-9]|[1][0-2])-([0-2][0-9]|[3][0-2])");

	/**
	 * Parse yyyy-MM-dd from string
	 *
	 * @param mydata
	 */
	public static Calendar parseCalendar(String mydata) {

		Matcher matcher = DATE_PATTERN.matcher(mydata);
		if (matcher.find()) {
			SimpleDateFormat format = new SimpleDateFormat(FILE_DATE_FORMAT);
			try {

				Date date = format.parse(matcher.group(0));
				Calendar myCal = new GregorianCalendar();
				myCal.setTime(date);
				return myCal;
			}
			catch (ParseException e) {
				FailSafeLog.log(e);
			}

		}
		return null;
	}

	public static void removeOldFolders() {
		Calendar c = GregorianCalendar.getInstance();
		c.add(Calendar.MINUTE, -MINS_TO_CHECK_OLD_FILES);
		if (lastOldFilesChecked == null || lastOldFilesChecked.before(c)) {

			String d = DbConfig.getBaseDir();
			File dir = new File(d);
			Calendar gc = GregorianCalendar.getInstance();
			gc.add(Calendar.HOUR, -getHoursToDeleteOldFiles());

			if (dir.isDirectory()) {
				for (File f : dir.listFiles()) {
					Calendar parsedCal = parseCalendar(f.getName());

					if (parsedCal != null) {
						if (parsedCal.before(gc)) {
							FailSafeLog.log("DELETING OLD FILE BY NAME :" + f.getAbsolutePath());
							deleteDirectory(f);
						}
					}
					else {

						Date date = new Date(f.lastModified());
						Calendar modCal = new GregorianCalendar();
						modCal.setTime(date);
						if (modCal.before(gc)) {
							FailSafeLog.log("DELETING OLD FILE BY MOD TIME :" + date + ", " + f.getAbsolutePath());
							deleteDirectory(f);
						}
					}

				}
			}
			else {
				FailSafeLog.log(d + " is not a directory");
			}

			lastOldFilesChecked = GregorianCalendar.getInstance();
		}
	}

	private static int getHoursToDeleteOldFiles() {
		return 24 * 7;
	}

	public static void switchDbFolderIfNeeded() {

		Calendar c = GregorianCalendar.getInstance();
		c.add(Calendar.SECOND, -SECS_TO_CHECK_STATUS);

		if (lastStatusChecked != null) {
			FailSafeLog.log("STATUS CHANGED CALS :" + lastStatusChecked.getTime() + " < " + c.getTime());
		}

		if (lastStatusChecked == null || lastStatusChecked.before(c)) {
			FailSafeLog.log("switchDbFolderIfNeeded ...");

			String updated = updatedFolderName();
			if (updated != null) {

				if (!updated.equals(currentFolderName)) {

					synchronized (factoryAccesMonitor) {

						// change
						if (!updated.equals(currentFolderName)) {

							FailSafeLog.log("Changing db folder " + currentFolderName + " -> " + updated);

							currentFolderName = updated;

							try {
								archiveCurrentDb();
							}
							catch (SQLException e) {
								FailSafeLog.log(e);
							}
							removeOldFolders();

						}
					}

				}
			}
			lastStatusChecked = GregorianCalendar.getInstance();
		}

	}

	public static void initFolderName() {
		// init
		if (currentFolderName == null) {
			synchronized (factoryAccesMonitor) {
				if (currentFolderName == null) {

					String updated = updatedFolderName();
					if (currentFolderName == null) {
						FailSafeLog.log("Init folder name : " + updated);
						currentFolderName = updated;
					}
				}
			}
		}
	}

	/**
	 * Change name when date passes or database size exceeded
	 */
	private static String updatedFolderName() {
		String date = getDate();
		// String date = getDate() + System.currentTimeMillis(); for test
		return date;
	}

	private static String getDate() {
		// yyyy-MM-dd HH:mm:ss.SSS
		return new SimpleDateFormat(FILE_DATE_FORMAT).format(new Date());
	}

	/**
	 * in MB
	 *
	 * @param folder
	 * @return
	 */
	private static float getFileSize(File folder) {
		int totalFile = 0;
		long foldersize = 0;
		File[] filelist = folder.listFiles();
		for (int i = 0; i < filelist.length; i++) {
			if (filelist[i].isDirectory()) {
				foldersize += getFileSize(filelist[i]);
			}
			else {
				totalFile++;
				foldersize += filelist[i].length();
			}
		}
		return (float) foldersize / (1024 * 1024);
	}

	private static Object archivingMonitor = new Object();

	public static void archiveCurrentDb() throws SQLException {
		if (currentFactory != null) {
			synchronized (archivingMonitor) {
				long startTime = System.currentTimeMillis();

				File f = new File(currentFactory.getData().getFolderPatch());
				FailSafeLog.log("Archiving DB ...  directory: " + f.getAbsolutePath());
				String backupFilePatch = currentFactory.backup();
				try {
					compressFile(backupFilePatch);
					File bkFile = new File(backupFilePatch);
					bkFile.delete();
				}
				catch (Exception ex) {
					FailSafeLog.log(ex);
				}

				currentFactory.shutdown();
				currentFactory = null;

				if (f.exists()) {
					FailSafeLog.log("DB folder size: " + getFileSize(f));
					boolean deleteDirectory = deleteDirectory(f);
					FailSafeLog.log("DB folder deleted :" + deleteDirectory);
					currentFactory = null;
				}
				else {
					FailSafeLog.log("Cannot archive DB - Direcotory doesn't exists: " + f.getAbsolutePath());
				}

				FailSafeLog.log("Db archived in " + (System.currentTimeMillis() - startTime) + " ms");
			}
		}
		else {
			FailSafeLog.log("Cannot archive DB - database not initialized");
		}

	}

	public static boolean deleteDirectory(File directory) {
		if (directory.exists()) {
			if (directory.isDirectory()) {
				File[] files = directory.listFiles();
				if (null != files) {
					for (int i = 0; i < files.length; i++) {
						if (files[i].isDirectory()) {
							deleteDirectory(files[i]);
						}
						else {
							files[i].delete();
						}
					}
				}
			}

			return (directory.delete());

		}
		return false;

	}

	/**
	 * XZ compression - for single file only
	 *
	 * @param f
	 * @throws FileNotFoundException
	 * @throws CompressorException
	 * @throws IOException
	 */
	public static void compressFile(String f) throws FileNotFoundException, CompressorException, IOException {
		long startTime = System.currentTimeMillis();

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

			FailSafeLog.log(f + " compressed in " + (System.currentTimeMillis() - startTime) + " ms");
		}
		finally {
			in.close();
			bin.close();
			if (compressing != null) {
				compressing.close();
			}
		}

	}

	/**
	 * Factory may change, getConnection needs to be synchronized
	 *
	 * @return
	 * @throws SQLException
	 */
	public static Connection getConnection() throws SQLException {
		synchronized (factoryAccesMonitor) {
			return getFactory().getConnection();
		}
	}

}
