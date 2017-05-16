package pl.shredder.consumers;

import java.sql.SQLException;
import java.util.HashMap;

import pl.shredder.BaseShred;
import pl.shredder.persistance.h2.RetentionConnectionFactory;
import pl.shredder.persistance.h2.dao.LogDao;
import pl.shredder.persistance.h2.utils.FailSafeLog;

public class H2Consumer extends AbstracConsumer {

	@Override
	protected boolean consume(BaseShred take) {
		if (take != null) {
			HashMap<String, Object> values = new HashMap<String, Object>();

			values.put(LogDao.Fields.TIMESTAMP.getName(), take.getTs());
			values.put(LogDao.Fields.MESSAGE.getName(), take.getMessage());
			values.put(LogDao.Fields.LEVEL.getName(), take.getLevel());
			values.put(LogDao.Fields.LOCATION.getName(), take.getCaller());
			values.put(LogDao.Fields.LOG_EXECUTION_NANO.getName(), take.getLogExecutionNanos());
			values.put(LogDao.Fields.STACKTRACE.getName(), take.getThrowableAsString());
			values.put(LogDao.Fields.TAGS.getName(), take.getTags());
			values.put(LogDao.Fields.HTTP_CLIENT_INFO.getName(), take.getHttpClientInfo());
			values.put(LogDao.Fields.INPUT_DATA.getName(), take.getInputDataAsString());

			try {
				LogDao.insertStore(RetentionConnectionFactory.getConnection(), values);
			}
			catch (SQLException e) {
				FailSafeLog.log(e);
				return false;
			}

		}

		return true;

	}

}
