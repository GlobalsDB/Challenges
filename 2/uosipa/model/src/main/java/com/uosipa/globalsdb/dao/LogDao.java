package com.uosipa.globalsdb.dao;

import com.intersys.globals.ValueList;
import com.uosipa.globalsdb.database.Database;
import com.uosipa.globalsdb.model.Log;
import com.uosipa.globalsdb.model.Service;
import com.uosipa.globalsdb.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class LogDao {
    private static final LogDao INSTANCE = new LogDao();

    public static LogDao getInstance() {
        return INSTANCE;
    }

    private LogDao() {
        // No operations.
    }

    public List<Log> findLogs(User user, Service service, Log.Severity... severities) {
        List<Log> result = new ArrayList<Log>();

        for (Log.Severity severity : severities) {
            List<String> subscripts = Database.getAllSubscripts(
                    user.getLogin(), service.toString(), severity.toString()
            );

            for (String subscript : subscripts) {
                ValueList logs = Database.getNodeValue(
                        user.getLogin(), service.toString(), severity.toString(), Long.parseLong(subscript)
                );

                for (int i = 0; i < logs.length(); ++i) {
                    Log log = new Log();
                    log.setService(service);
                    log.setSeverity(severity);
                    log.setDate(new Date(Long.parseLong(subscript)));

                    log.setMessage(logs.getNextString());

                    result.add(log);
                }
            }
        }

        return result;
    }

    public void addLogs(User user, Collection<Log> logs) {
        for (Log log : logs) {
            addLog(user, log);
        }
    }

    public void addLog(User user, Log log) {
        Database.addToNode(
                log.getMessage(), user.getLogin(), log.getService().toString(),
                log.getSeverity().toString(), log.getDate().getTime()
        );
    }
}
