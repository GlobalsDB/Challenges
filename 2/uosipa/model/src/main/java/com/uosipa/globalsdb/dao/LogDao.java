package com.uosipa.globalsdb.dao;

import com.uosipa.globalsdb.model.Log;
import com.uosipa.globalsdb.model.Service;
import com.uosipa.globalsdb.model.User;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class LogDao {
    private static final LogDao INSTANCE = new LogDao();

    public static LogDao getInstance() {
        return INSTANCE;
    }

    private LogDao() {
        throw new AssertionError();
    }

    public List<Log> findLogs(User user, Service service, Log.Severity... severities) {
        return Collections.emptyList();
        //TODO implement
    }

    public void addLogs(Collection<Log> logs) {
        //TODO implement
    }
}
