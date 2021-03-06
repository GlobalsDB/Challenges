package com.uosipa.globalsdb.logspusher;

import com.uosipa.globalsdb.LogParser;
import com.uosipa.globalsdb.dao.LogDao;
import com.uosipa.globalsdb.database.Database;
import com.uosipa.globalsdb.model.Service;
import com.uosipa.globalsdb.model.User;
import org.apache.commons.cli.*;

import java.io.*;

public class Main {
    private static FileInputStream fileInputStream;
    private static Service service;
    private static User user;

    public static void main(String[] args) throws IOException, ParseException {
        try {
            parseArguments(args);
            run();
            //Sample.main(args);
        } finally {
            Database.closeConnection();
        }
    }

    private static void run() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));

        StringBuilder log = new StringBuilder();
        String logLine;
        //noinspection InfiniteLoopStatement
        //exit:
        while (true) {
            while ((logLine = reader.readLine()) == null) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ignored) {
                }

                //break exit;
            }

            if (LogParser.isNewLogStart(logLine, service)) {
                if (log.length() > 0) {
                    try {
                        LogDao.getInstance().addLog(user, LogParser.parse(log.toString(), service));
                    } catch (java.text.ParseException ignored) {
                        ignored.printStackTrace();
                    }
                }
                log.delete(0, log.length());
            }

            if (log.length() > 0) {
                log.append("\n");
            }
            log.append(logLine);
        }
/*
        for (Log logg : LogDao.getInstance().findLogs(user, service, Log.Severity.FATAL)) {
            System.out.println(logg.getDate() + " " + logg.getMessage());
        }*/
    }

    public static void parseArguments(String[] args) throws ParseException {
        Options options = new Options();

        options.addOption("h", "help", false, "print this message");
        options.addOption("lf", "log-file", true, "Path to log file to process.");
        options.addOption("s", "service", true, "Service name log file belongs to.");
        options.addOption("l", "login", true, "Login");
        options.addOption("pwd", "password", true, "Password");

        CommandLineParser parser = new PosixParser();
        CommandLine cmd = parser.parse(options, args);

        if (cmd.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("java -jar logpusher.jar", options, true);
            return;
        }

        if (!cmd.hasOption("log-file") || !cmd.hasOption("service") || !cmd.hasOption("login") || !cmd.hasOption("password")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("java -jar logpusher.jar", options, true);
            return;
        }

        try {
            fileInputStream = new FileInputStream(new File(cmd.getOptionValue("log-file")));
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("Can't read log file.");
        }

        if ("httpd".equals(cmd.getOptionValue("service"))) {
            service = Service.HTTPD;
        } else if ("tomcat".equals(cmd.getOptionValue("service"))) {
            service = Service.TOMCAT;
        } else {
            throw new IllegalArgumentException("Unsupported file format " + cmd.getOptionValue("service"));
        }

        user = new User(cmd.getOptionValue("login"), cmd.getOptionValue("password"));
    }
}
