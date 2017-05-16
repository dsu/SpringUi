package pl.shredder.persistance.h2;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Calendar;
import java.util.HashMap;

import org.h2.tools.Csv;
import org.h2.tools.SimpleResultSet;

import pl.shredder.BaseShred;
import pl.shredder.persistance.h2.dao.LogDao;

/**
 * TODO Write data to disk before trying to write to database TODO
 *
 * @author dsu
 *
 */
public class CommitLog {

	private static volatile int writeNr = 0;

	private final String name;

	public CommitLog(String v) {
		this.name = v;
	}

	public synchronized void write() {

	}

	public static CommitLog open() {
		long timeInMillis = Calendar.getInstance().getTimeInMillis();
		writeNr++;
		return new CommitLog(timeInMillis + "." + writeNr);
	}

	public void commit(BaseShred log) throws SQLException {

		SimpleResultSet rs = new SimpleResultSet();
		rs.addColumn("NAME", Types.VARCHAR, 255, 0);
		rs.addColumn("EMAIL", Types.VARCHAR, 255, 0);
		rs.addRow("Bob Meier", "bob.meier@abcde.abc");
		rs.addRow("John Jones", "john.jones@abcde.abc");
		new Csv().write(name, rs, null);

	}

	public void delete() {
		// TODO Auto-generated method stub

	}
}
