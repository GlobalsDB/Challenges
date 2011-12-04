package me.yonatan.globalsdb.c2;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Locale;

import org.apache.commons.lang3.ArrayUtils;
import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;

public class LogGenerator {

	private static int random(int max) {
		return (int) (Math.random() * max);
	}

	private static String generateIp() {
		return new StringBuilder().append((int) (Math.random() * 255)).append('.').append((int) (Math.random() * 255)).append('.')
				.append((int) (Math.random() * 255)).append('.').append((int) (Math.random() * 255)).toString();
	}

	private static <T> T getRandom(T[] arr) {
		if (ArrayUtils.isEmpty(arr))
			return null;
		return arr[random(arr.length)];
	}

	private static final String[] PATHS = { "dir", "locate", "file", "files", "test", "images", "users", "pictures", "docs", "documents",
			"app", "cgi-bin" };

	private static final String[] EXTS = { "html", "htm", "jsf", "jsp", "aspx", "asp", "php", "jpg", "jpeg", "gif" };

	private static final String[] CODES = { "200", "404", "403", "300", "500" };

	private static String generatePath() {
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
	public static void generate(int records, String filename) throws IOException {

		String[] ips = new String[records / 20];
		for (int i = 0; i < ips.length; i++) {
			ips[i] = generateIp();
		}

		String[] method = { "POST", "GET", "PUT", "DELETE" };

		MutableDateTime time = DateTime.now().toMutableDateTime();

		File file = new File(filename);
		FileWriter fw = new FileWriter(file);
		for (int i = 0; i < records; i++) {
			if (i > 0 && i % 50000 == 0)
				System.out.println("Already finished with " + i + " records...");
			StringBuilder sb = new StringBuilder();
			sb.append(getRandom(ips)).append(" - - [").append(time.toString("dd/MMM/yyyy:HH:mm:ss Z", Locale.US)).append("] \"")
					.append(getRandom(method)).append(" /").append(generatePath()).append("\" ").append(getRandom(CODES)).append(' ')
					.append(random(50000));
			time.addMillis(random(1000 * 50));

			fw.write(sb.toString() + "\n");
		}
		fw.close();
		System.out.println("Done!");

	}
}
