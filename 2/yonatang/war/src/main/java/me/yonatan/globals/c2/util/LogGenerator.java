package me.yonatan.globals.c2.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

import lombok.Cleanup;

import org.apache.commons.lang3.ArrayUtils;
import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;

public class LogGenerator {

	public static int random(int max) {
		return (int) (Math.random() * max);
	}

	public static String generateIp() {
		return new StringBuilder().append((int) (Math.random() * 255)).append('.').append((int) (Math.random() * 255)).append('.')
				.append((int) (Math.random() * 255)).append('.').append((int) (Math.random() * 255)).toString();
	}

	public static <T> T getRandom(T[] arr) {
		if (ArrayUtils.isEmpty(arr))
			return null;
		return arr[random(arr.length)];
	}

	public static final String[] PATHS = { "dir", "locate", "file", "files", "test", "images", "users", "pictures", "docs", "documents",
			"app", "cgi-bin" };

	public static final String[] EXTS = { "html", "htm", "jsf", "jsp", "aspx", "asp", "php", "jpg", "jpeg", "gif" };

	public static final String[] CODES = { "200", "404", "403", "300", "500" };

	public static String generatePath() {
		int size = random(4) + 1;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < size; i++) {
			sb.append(getRandom(PATHS)).append('/');
		}
		sb.append(getRandom(PATHS)).append('.').append(getRandom(EXTS));
		return sb.toString();
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		int records;
		records = Integer.parseInt(args[0]);

		String filename = args[1];

		String[] ips = new String[records / 20];
		for (int i = 0; i < ips.length; i++) {
			ips[i] = generateIp();
		}

		String[] method = { "POST", "GET", "PUT", "DELETE" };

		MutableDateTime time = DateTime.now().toMutableDateTime();

		File file = new File(filename);
		@Cleanup
		FileWriter fw = new FileWriter(file);
		for (int i = 0; i < records; i++) {
			StringBuilder sb = new StringBuilder();
			sb.append(getRandom(ips)).append(" - - [").append(time.toString("dd/MMM/yyyy:HH:mm:ss Z", Locale.US)).append("] \"")
					.append(getRandom(method)).append(" /").append(generatePath()).append("\" ").append(getRandom(CODES)).append(' ')
					.append(random(50000));
			time.addMillis(random(1000 * 50));
			fw.write(sb.toString() + "\n");
		}

	}
}
