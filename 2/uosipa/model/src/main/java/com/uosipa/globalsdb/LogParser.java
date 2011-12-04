package com.uosipa.globalsdb;

import com.uosipa.globalsdb.model.Log;
import com.uosipa.globalsdb.model.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LogParser {
    private static SimpleDateFormat httpdDateFormat = new SimpleDateFormat
            ("dd/MMM/yyyy:HH:mm:ss", Locale.US);
    private static SimpleDateFormat tomcatDateFormat = new SimpleDateFormat
            ("dd.MM.yyyy HH:mm:ss", Locale.US);

    public static boolean isNewLogStart(String logString, Service service) {
        switch (service) {
            case HTTPD:
                return true;
            case TOMCAT:
                return isTomcatLogStart(logString);
            default:
                throw new IllegalArgumentException("Unsupported log format.");
        }
    }

    private static boolean isTomcatLogStart(String logString) {
        try {
            getTomcatLogDate(logString);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public static Log parse(String rawLog, Service service) throws ParseException {
        Log log = new Log();
        log.setService(service);
        log.setSeverity(getSeverity(rawLog, service));
        log.setDate(getDate(rawLog, service));
        log.setMessage(getMessage(rawLog, service));

        return log;
    }

    private static String getMessage(String rawLog, Service service) {
        switch (service) {
            case HTTPD:
                return getHttpdLogMessage(rawLog);
            case TOMCAT:
                return getTomcatLogMessage(rawLog);
            default:
                throw new IllegalArgumentException("Unsupported log format.");
        }
    }

    private static String getTomcatLogMessage(String rawLog) {
        String dummy = rawLog.substring(rawLog.indexOf("\n") + 1);
        return dummy.substring(dummy.indexOf(": ") + 2);
    }

    private static String getHttpdLogMessage(String rawLog) {
        return rawLog.substring(rawLog.indexOf("] ") + 2);
    }

    private static Log.Severity getSeverity(String rawLog, Service service) {
        switch (service) {
            case HTTPD:
                return Log.Severity.UNKNOWN;
            case TOMCAT:
                return getTomcatLogSeverity(rawLog);
            default:
                throw new IllegalArgumentException("Unsupported log format.");
        }
    }

    private static Log.Severity getTomcatLogSeverity(String rawLog) {
        if (rawLog.contains("SEVERE")) {
            return Log.Severity.FATAL;
        } else if (rawLog.contains("WARNING")) {
            return Log.Severity.WARN;
        } else if (rawLog.contains("INFO")) {
            return Log.Severity.INFO;
        } else if (rawLog.contains("DEBUG")) {
            return Log.Severity.DEBUG;
        } else if (rawLog.contains("ERROR")) {
            return Log.Severity.ERROR;
        } else {
            return Log.Severity.UNKNOWN;
        }
    }

    public static Date getDate(String rawLog, Service service) throws ParseException {
        switch (service) {
            case HTTPD:
                return getHttpLogDate(rawLog);
            case TOMCAT:
                return getTomcatLogDate(rawLog);
            default:
                throw new IllegalArgumentException("Unsupported log format.");
        }
    }

    private static Date getTomcatLogDate(String rawLog) throws ParseException {
        String date = rawLog.substring(0, rawLog.indexOf(' '));
        String time = rawLog.substring(rawLog.indexOf(' ') + 1);
        time = time.substring(0, time.indexOf(' '));

        return tomcatDateFormat.parse(date + " " + time);
    }

    private static Date getHttpLogDate(String rawLog) throws ParseException {
        String date = rawLog.substring(rawLog.indexOf('[') + 1, rawLog.indexOf(']'));

        return httpdDateFormat.parse(date.substring(0, date.indexOf(" "))); //TODO fix
    }


}
