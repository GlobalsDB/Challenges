package me.yonatan.globalsdb.c2;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.apache.catalina.Host;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Server;
import org.apache.catalina.core.AprLifecycleListener;
import org.apache.catalina.realm.MemoryRealm;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.math.NumberUtils;

import chrriis.common.UIUtils;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;

public class Main {

	public static void runTomcat() throws IOException {
		final Tomcat tomcat = new Tomcat();

		final int port = 2000 + (int) (Math.random() * (65500 - 2000));

		final File tempWar = File.createTempFile("war", ".war");

		InputStream warInputStream = ClassLoader.getSystemResourceAsStream("war.war");
		IOUtils.copy(warInputStream, new FileOutputStream(tempWar));

		final File tempUsers = File.createTempFile("tomcat-users", ".xml");
		InputStream usersInputStream = ClassLoader.getSystemResourceAsStream("tomcat-users.xml");
		IOUtils.copy(usersInputStream, new FileOutputStream(tempUsers));

		String tempWarName = tempWar.getAbsolutePath();
		String appBase = FilenameUtils.getName(tempWarName);
		String baseDir = FilenameUtils.getFullPath(tempWarName);

		tomcat.setPort(port);
		MemoryRealm r = new MemoryRealm();
		r.setPathname(tempUsers.getAbsolutePath());
		tomcat.setDefaultRealm(r);
		System.out.println("Setting tomcat-users.xml to " + tempUsers.getAbsolutePath());
		System.out.println("Setting baseDir to " + baseDir);
		System.out.println("Setting war to " + appBase);
		tomcat.setBaseDir(baseDir);
		tomcat.setHostname("127.0.0.1");

		Host host = tomcat.getHost();

		Server server = tomcat.getServer();
		AprLifecycleListener listener = new AprLifecycleListener();
		server.addLifecycleListener(listener);
		String contextPath = "/app";
		host.setAppBase("");
		try {

			tomcat.addWebapp(contextPath, appBase);
			tomcat.start();
			System.out.println("Starting desktop");
			UIUtils.setPreferredLookAndFeel();
			final JFrame frame = new JFrame("Logfile viewer");
			NativeInterface.open();
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					frame.getContentPane().add(new Window("http://localhost:" + port + "/app/viewer.jsf"), BorderLayout.CENTER);
					frame.setSize(800, 600);
					frame.setLocationByPlatform(true);
					frame.setVisible(true);
				}
			});

			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						tomcat.stop();
						tomcat.destroy();
					} catch (Exception e) {
						e.printStackTrace();
					}

					System.out.println("Deleteing files");
					FileUtils.deleteQuietly(tempWar);
					FileUtils.deleteQuietly(tempWar);
				}
			}));
			NativeInterface.runEventPump();

		} catch (LifecycleException e) {
			e.printStackTrace();
		} catch (ServletException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	@SuppressWarnings("static-access")
	public static void main(String[] args) throws IOException {
		Options options = new Options();
		options.addOption("h", "help", false, "print this message");
		options.addOption(OptionBuilder.hasArgs(3).withValueSeparator(' ').withDescription("Generate log file").withArgName("filename")
				.create("generate"));
		options.addOption(OptionBuilder.hasArg().withDescription("Amount of records (default value is 5000)").withArgName("records")
				.create("records"));

		CommandLineParser parser = new PosixParser();
		try {
			if (args.length == 0) {
				runTomcat();
			} else {
				CommandLine line = parser.parse(options, args);
				if (line.hasOption("generate")) {
					String filename = line.getOptionValue("generate");
					int records = 5000;
					if (line.hasOption("records")) {
						records = NumberUtils.toInt(line.getOptionValue("records"), 5000);
					}
					System.out.println("Generating to file " + filename + " " + records + " records");
					LogGenerator.generate(records, filename);
					System.exit(0);
				} else {
					HelpFormatter formatter = new HelpFormatter();
					formatter.printHelp("runner", options);
					System.exit(0);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
