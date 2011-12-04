package com.uosipa.globalsdb.model;

import java.util.Date;

public class Log implements Comparable<Log> {
    @Override
    public int compareTo(Log o) {
        return o.getDate().compareTo(date);
    }

    public enum Severity {
        DEBUG,
        INFO,
        WARN,
        ERROR,
        FATAL,
        UNKNOWN
    }

    private Service service;
    private Severity severity;
    private Date date;
    private String message;

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
