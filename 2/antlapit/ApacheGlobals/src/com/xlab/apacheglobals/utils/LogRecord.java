package com.xlab.apacheglobals.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.intersys.globals.NodeReference;
import com.intersys.globals.ValueList;
import com.xlab.apacheglobals.GlobalsConnection;

public class LogRecord {
	public static final String logEntryPattern = "^([\\d.]+) (\\S+) (\\S+) \\[([\\w:/]+\\s[+\\-]\\d{4})\\] \"(.+?)\" (\\d{3}) (\\d+|\\-)";
	public static final int NUM_FIELDS = 7;

	private String ip;
	private String datetime;
	private String method;
	private String request;
	private Date parsedDate;
	private int response;
	private long bytessend;
	private int id;

	public LogRecord(int id) {
		this.id = id;
		try {
			load();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void load() throws Exception {
		NodeReference node = GlobalsConnection.getInstance()
				.createNodeReference(Configuration.DATA_GLOBAL);
		node.appendSubscript(id);
		String[] values = node.getString().split("~~");
		//System.out.println(node.getString());
		node.setSubscriptCount(0);

		Calendar cal = Calendar.getInstance();
		cal.set(Integer.parseInt(values[0]), Integer.parseInt(values[1]),
				Integer.parseInt(values[2]), Integer.parseInt(values[3]),
				Integer.parseInt(values[4]), Integer.parseInt(values[5]));
		parsedDate = cal.getTime();

		ip = values[6];
		method = values[7];
		response = Integer.parseInt(values[8]);
		request = values[9];
		bytessend = Long.parseLong(values[10]);

	}

	public LogRecord(int counter, String value) {
		id = counter;
		try {
			parse(value);
			// ImportDataTimer.dataChanged = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void parse(String value) throws Exception {
		System.out.println(logEntryPattern);

		System.out.println(value);

		Pattern p = Pattern.compile(logEntryPattern);
		Matcher matcher = p.matcher(value);
		if (!matcher.matches() || NUM_FIELDS != matcher.groupCount()) {
			throw new Exception("Bad log entry");
		}
		ip = matcher.group(1);
		datetime = matcher.group(4);
		request = matcher.group(5);
		method = request.substring(0, request.indexOf(" "));
		request = request.substring(request.indexOf(" ") + 1);
		response = Integer.parseInt(matcher.group(6));
		try {
			bytessend = Integer.parseInt(matcher.group(7));
		} catch (NumberFormatException e) {
			bytessend = 0;
		}
	}

	/**
	 * Saves LogRecord into GlobalsDB
	 * 
	 * @throws Exception
	 */
	public void save() throws Exception {
		try {
			Date d = (new SimpleDateFormat(Configuration.dateFormat,
					Locale.ENGLISH)).parse(datetime);
			Calendar cal = Calendar.getInstance(Locale.ENGLISH);
			cal.setTime(d);

			NodeReference node = GlobalsConnection.getInstance()
					.createNodeReference(Configuration.DATA_GLOBAL);
			node.set(
					cal.get(Calendar.YEAR) + "~~" + cal.get(Calendar.MONTH)
							+ "~~" + cal.get(Calendar.DAY_OF_MONTH) + "~~"
							+ cal.get(Calendar.HOUR_OF_DAY) + "~~"
							+ cal.get(Calendar.MINUTE) + "~~"
							+ cal.get(Calendar.SECOND) + "~~" + ip + "~~"
							+ method + "~~" + response + "~~" + request + "~~"
							+ bytessend, id);

			node = GlobalsConnection.getInstance().createNodeReference(
					Configuration.INDEX_GLOBAL);
			node.appendSubscript("year");
			node.appendSubscript(cal.get(Calendar.YEAR));
			setIndex(node, id);

			node = GlobalsConnection.getInstance().createNodeReference(
					Configuration.INDEX_GLOBAL);
			node.appendSubscript("month");
			node.appendSubscript(cal.get(Calendar.MONTH));
			setIndex(node, id);

			node = GlobalsConnection.getInstance().createNodeReference(
					Configuration.INDEX_GLOBAL);
			node.appendSubscript("day");
			node.appendSubscript(cal.get(Calendar.DAY_OF_MONTH));
			setIndex(node, id);

			node = GlobalsConnection.getInstance().createNodeReference(
					Configuration.INDEX_GLOBAL);
			node.appendSubscript("hour");
			node.appendSubscript(cal.get(Calendar.HOUR_OF_DAY));
			setIndex(node, id);

			node = GlobalsConnection.getInstance().createNodeReference(
					Configuration.INDEX_GLOBAL);
			node.appendSubscript("minute");
			node.appendSubscript(cal.get(Calendar.MINUTE));
			setIndex(node, id);

			node = GlobalsConnection.getInstance().createNodeReference(
					Configuration.INDEX_GLOBAL);
			node.appendSubscript("second");
			node.appendSubscript(cal.get(Calendar.SECOND));
			setIndex(node, id);

			node = GlobalsConnection.getInstance().createNodeReference(
					Configuration.INDEX_GLOBAL);
			node.appendSubscript("ip");
			node.appendSubscript(ip);
			setIndex(node, id);

			node = GlobalsConnection.getInstance().createNodeReference(
					Configuration.INDEX_GLOBAL);
			node.appendSubscript("method");
			node.appendSubscript(method);
			setIndex(node, id);

			node = GlobalsConnection.getInstance().createNodeReference(
					Configuration.INDEX_GLOBAL);
			node.appendSubscript("response");
			node.appendSubscript(response);
			setIndex(node, id);

		} catch (ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setIndex(NodeReference node, int id) {
		try {
			ValueList list = node.getList();
			ValueList nlist = GlobalsConnection.getInstance().createList();
			int item = 0;

			if (list != null) {
				for (int i = 0; i < list.length(); ++i) {
					item = list.getNextInt();
					nlist.append(item);
				}
				list.close();
			}
			nlist.append(id);

			node.set(nlist);
			nlist.close();
			node.setSubscriptCount(0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getIp() {
		return ip;
	}

	public String getMethod() {
		return method;
	}

	public String getRequest() {
		return request;
	}

	public Date getParsedDate() {
		return parsedDate;
	}

	public int getResponse() {
		return response;
	}

	public long getBytessend() {
		return bytessend;
	}

	public int getId() {
		return id;
	}
}
