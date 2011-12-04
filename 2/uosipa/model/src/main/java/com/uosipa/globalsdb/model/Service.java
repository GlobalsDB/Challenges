package com.uosipa.globalsdb.model;

public enum Service {
    HTTPD,
    TOMCAT;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
