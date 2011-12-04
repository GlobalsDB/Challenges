package me.yonatan.globals.c2.parser;

import java.util.Locale;

import me.yonatan.globals.c2.entity.LogRecord;

import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class ApacheLogParser implements LogParser {

	private Logger log = Logger.getLogger(this.getClass());

	private DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("dd/MMM/yyyy:HH:mm:ss Z").withLocale(Locale.US);

	@Override
	public LogRecord parse(String line) {
		String[] parts = StringUtils.splitByWholeSeparator(line, " - - ");
		if (parts.length < 2) {
			log.warnv("Line {0} is of wrong format", line);
			return null;
		}
		try {
			String ip = parts[0];
			String rest = parts[1];
			String time = StringUtils.substring(rest, StringUtils.indexOf(rest, "[") + 1, StringUtils.indexOf(rest, "]"));
			String description = StringUtils.substring(rest, StringUtils.indexOf(rest, "]") + 2);
			LogRecord logRecord = new LogRecord();
			logRecord.setDescription(description);
			logRecord.setIp(ip);
			logRecord.setTimestamp(DateTime.parse(time, dateTimeFormatter));
			return logRecord;
		} catch (Exception e) {
			log.warnv("Line {0} can''t be parsed due to {1}", line, e);
			return null;
		}

	}

}
