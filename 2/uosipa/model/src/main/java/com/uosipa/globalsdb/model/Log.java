package com.uosipa.globalsdb.model;

import java.util.Date;

public class Log {
    public enum Severity {
        DEBUG,
        INFO,
        WARN,
        ERROR,
        FATAL
    }

    private Severity severity;
    private Date date;
    private String message;

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
