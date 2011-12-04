package me.yonatan.globals.c2.action;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.yonatan.globals.c2.entity.LogFile;
import me.yonatan.globals.c2.entity.LogRecord;
import me.yonatan.globals.c2.parser.LogParser;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jboss.logging.Logger;
import org.joda.time.DateTime;

import com.google.common.io.LineReader;
import com.google.gson.Gson;
import com.intersys.globals.Connection;
import com.intersys.globals.NodeReference;

@Named
@ApplicationScoped
public class DbManager {

	private final Object[] FILE_NAME = new Object[] { "file", "name" };
	private final Object[] FILE_TIMESTAMP = new Object[] { "file", "timestamp" };
	private final String LOGS = "logs";

	private int MAX_FILTERED_RESULTS = 5000;
	@Inject
	private Logger log;

	@Inject
	private Connection connection;

	@Inject
	private Gson gson;

	@Inject
	private LogParser logParser;

	public String importLocalFile(File file) throws IOException {
		log.infov("Opening file {0}", file);
		if (!file.isFile()) {
			throw new IOException("File " + file + " couldn't be used");
		}

		String handler = "l" + StringUtils.substring(DigestUtils.shaHex(file.getCanonicalPath()), 0, 29);
		System.out.println("Creating hadnler " + handler);
		@Cleanup
		NodeReference nr = connection.createNodeReference(handler);
		nr.kill();
		nr.set(file.getAbsolutePath(), FILE_NAME);
		nr.set(file.lastModified(), FILE_TIMESTAMP);
		nr.set(0, LOGS);
		nr.close();

		reloadFile(getFileInfo(handler));
		return handler;

	}

	public void reloadFile(LogFile logFile) {
		System.out.println("Loading file " + logFile);
		File file = logFile.getFile();
		// TODO
		if (file == null)
			return;

		@Cleanup
		NodeReference nr = connection.createNodeReference(logFile.getHandler());

		try {
			connection.startTransaction();
			nr.acquireLock(NodeReference.EXCLUSIVE_LOCK, NodeReference.LOCK_NON_INCREMENTALLY);
			nr.releaseLock(NodeReference.EXCLUSIVE_LOCK, NodeReference.RELEASE_AT_TRANSACTION_END);
			@Cleanup
			FileReader fileReader = new FileReader(file);
			LineReader lineReader = new LineReader(fileReader);

			String lastJson = nr.getString(LOGS, nr.getLong(LOGS));
			LogRecord lastRecord = gson.fromJson(lastJson, LogRecord.class);
			boolean updated = false;

			String line = null;
			while ((line = lineReader.readLine()) != null) {
				LogRecord lr = logParser.parse(line);
				if (lr == null)
					continue;
				if (updated || lastRecord == null || lr.getTimestamp().isAfter(lastRecord.getTimestamp())) {
					long id = nr.increment(1, LOGS);
					lr.setId(id);
					if (lr != null) {
						String json = gson.toJson(lr);
						nr.set(json, LOGS, id);

						// indices
						long timestamp = lr.getTimestamp().getMillis();
						if (StringUtils.isNotBlank(lr.getIp())) {
							nr.set(id, "i_ip", "~" + lr.getIp(), id);
							nr.set(id, "i_timestamp_ip", timestamp, "~" + lr.getIp(), id);
						}
						nr.set(id, "i_timestamp", timestamp, id);

					}
					updated = true;
				}
			}
			nr.set(file.lastModified(), FILE_TIMESTAMP);

			connection.commit();
			System.out.println("Updated!");
		} catch (Exception e) {
			log.errorv(e, "Can't load data");
			connection.rollback();
		}
	}

	public void removeLogfile(String handler) {
		@Cleanup
		NodeReference nr = connection.createNodeReference(handler);
		nr.kill();
	}

	public LogRecord getRecord(String handler, long id) {
		@Cleanup
		NodeReference nr = connection.createNodeReference(handler);
		if (nr.exists(LOGS, id)) {
			String json = nr.getString(LOGS, id);
			LogRecord lr = gson.fromJson(json, LogRecord.class);
			return lr;
		}
		return null;
	}

	public List<LogRecord> getRecords(String handler) {
		@Cleanup
		NodeReference nr = connection.createNodeReference(handler);
		String next = nr.nextSubscript(LOGS, "");
		ArrayList<LogRecord> records = new ArrayList<LogRecord>();
		while (StringUtils.isNotEmpty(next)) {
			String json = nr.getString(LOGS, next);
			LogRecord lr = gson.fromJson(json, LogRecord.class);
			records.add(lr);
			next = nr.nextSubscript(LOGS, next);
		}

		return records;
	}

	public Records getRecords(String handler, int from, int count) {
		@Cleanup
		NodeReference nr = connection.createNodeReference(handler);
		String next = nr.nextSubscript(LOGS, (from == 0) ? "" : from);
		ArrayList<LogRecord> recordList = new ArrayList<LogRecord>();
		while (StringUtils.isNotEmpty(next) && recordList.size() < count) {
			String json = nr.getString(LOGS, next);
			LogRecord lr = gson.fromJson(json, LogRecord.class);
			recordList.add(lr);
			next = nr.nextSubscript(LOGS, next);

		}
		Records records = new Records();
		records.setFrom(from);
		records.setPage(count);
		records.setRecords(recordList);

		records.setTotalResults(nr.getInt(LOGS));

		return records;
	}

	/**
	 * Filter by IP (starts with)
	 * 
	 * @param handler
	 * @param from
	 * @param count
	 * @param ip
	 * @return
	 */
	public Records getRecords(String handler, int from, int count, String ip) {

		@Cleanup
		NodeReference nr = connection.createNodeReference(handler);
		String modifiedIp = "~" + ip;
		String nextIp = null;

		if (nr.hasSubnodes("i_ip", modifiedIp)) {
			nextIp = modifiedIp;
		} else {
			nextIp = nr.nextSubscript("i_ip", modifiedIp);
		}
		int recordCount = 0;
		ArrayList<LogRecord> recordList = new ArrayList<LogRecord>();
		while (StringUtils.isNotEmpty(nextIp) && (nextIp.startsWith(modifiedIp)) && recordCount < MAX_FILTERED_RESULTS) {
			String nextId = nr.nextSubscript("i_ip", nextIp, "");
			while (StringUtils.isNotEmpty(nextId) && recordCount < MAX_FILTERED_RESULTS) {
				if (recordCount >= from && recordList.size() < count) {
					String json = nr.getString(LOGS, nextId);
					LogRecord lr = gson.fromJson(json, LogRecord.class);
					recordList.add(lr);
				}
				recordCount++;
				nextId = nr.nextSubscript("i_ip", nextIp, nextId);
			}
			nextIp = nr.nextSubscript("i_ip", nextIp);
		}

		Records records = new Records();
		records.setFrom(from);
		records.setPage(count);
		records.setRecords(recordList);
		records.setTotalResults(recordCount);
		return records;

	}

	/**
	 * Filter by timestamp (between range)
	 * 
	 * @param handler
	 * @param from
	 * @param count
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	public Records getRecords(String handler, int from, int count, long fromDate, long toDate) {

		@Cleanup
		NodeReference nr = connection.createNodeReference(handler);
		String nextTimestamp = null;

		if (nr.hasSubnodes("i_timestamp", fromDate)) {
			nextTimestamp = String.valueOf(fromDate);
		} else {
			nextTimestamp = nr.nextSubscript("i_timestamp", fromDate);
		}
		int recordCount = 0;

		ArrayList<LogRecord> recordList = new ArrayList<LogRecord>();
		while (StringUtils.isNotEmpty(nextTimestamp) && NumberUtils.toLong(nextTimestamp) < toDate && recordCount < MAX_FILTERED_RESULTS) {
			String nextId = nr.nextSubscript("i_timestamp", nextTimestamp, "");
			while (StringUtils.isNotEmpty(nextId) && recordCount < MAX_FILTERED_RESULTS) {
				if (recordCount >= from && recordList.size() < count) {
					String json = nr.getString(LOGS, nextId);
					LogRecord lr = gson.fromJson(json, LogRecord.class);
					recordList.add(lr);
				}
				recordCount++;
				nextId = nr.nextSubscript("i_timestamp", nextTimestamp, nextId);
			}
			nextTimestamp = nr.nextSubscript("i_timestamp", nextTimestamp);
		}

		Records records = new Records();
		records.setFrom(from);
		records.setPage(count);
		records.setRecords(recordList);
		records.setTotalResults(recordCount);
		return records;

	}

	/**
	 * Filter records by timestamp AND ip (starts-with)
	 * 
	 * @param handler
	 * @param from
	 * @param count
	 * @param ip
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	public Records getRecords(String handler, int from, int count, String ip, long fromDate, long toDate) {

		@Cleanup
		NodeReference nr = connection.createNodeReference(handler);
		String nextTimestamp = null;
		String modifiedIp = "~" + ip;
		String nextIp = null;
		if (nr.hasSubnodes("i_timestamp_ip", fromDate)) {
			nextTimestamp = String.valueOf(fromDate);
		} else {
			nextTimestamp = nr.nextSubscript("i_timestamp_ip", fromDate);
		}
		int recordCount = 0;

		ArrayList<LogRecord> recordList = new ArrayList<LogRecord>();
		while (StringUtils.isNotEmpty(nextTimestamp) && NumberUtils.toLong(nextTimestamp) < toDate && recordCount < MAX_FILTERED_RESULTS) {
			if (nr.hasSubnodes("i_timestamp_ip", nextTimestamp, modifiedIp)) {
				nextIp = modifiedIp;
			} else {
				nextIp = nr.nextSubscript("i_timestamp_ip", nextTimestamp, modifiedIp);
			}
			while (StringUtils.isNotEmpty(nextIp) && (nextIp.startsWith(modifiedIp)) && recordCount < MAX_FILTERED_RESULTS) {
				String nextId = nr.nextSubscript("i_timestamp_ip", nextTimestamp, nextIp, "");
				while (StringUtils.isNotEmpty(nextId) && recordCount < MAX_FILTERED_RESULTS) {
					if (recordCount >= from && recordList.size() < count) {
						String json = nr.getString(LOGS, nextId);
						LogRecord lr = gson.fromJson(json, LogRecord.class);
						recordList.add(lr);
					}
					recordCount++;
					nextId = nr.nextSubscript("i_timestamp_ip", nextTimestamp, nextIp, nextId);
				}
				nextIp = nr.nextSubscript("i_timestamp_ip", nextTimestamp, nextIp);
			}
			nextTimestamp = nr.nextSubscript("i_timestamp_ip", nextTimestamp);
		}

		Records records = new Records();
		records.setFrom(from);
		records.setPage(count);
		records.setRecords(recordList);
		records.setTotalResults(recordCount);
		return records;

	}

	public long getLogCount(String handler) {
		@Cleanup
		NodeReference nr = connection.createNodeReference(handler);
		return nr.getLong(LOGS);
	}

	public int getLogCountInt(String handler) {
		return (int) (Math.min(getLogCount(handler), Integer.MAX_VALUE));
	}

	public LogFile getFileInfo(String handler) {
		@Cleanup
		NodeReference nr = connection.createNodeReference(handler);
		return new LogFile(nr.getString(FILE_NAME), new DateTime(nr.getLong(FILE_TIMESTAMP)), handler);
	}

	@Getter
	@Setter(AccessLevel.PRIVATE)
	@ToString
	public class Records {
		private int from;
		private int page;
		private int totalResults;
		private List<LogRecord> records;
	}
}
