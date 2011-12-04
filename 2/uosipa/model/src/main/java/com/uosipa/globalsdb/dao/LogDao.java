package com.uosipa.globalsdb.dao;

import com.intersys.globals.NodeReference;
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
            List<NodeReference> subnodes = Database.getAllSubnodes(
                    user.getLogin(), service.toString(), severity.toString()
            );

            for (NodeReference subnode : subnodes) {
                Log log = new Log();
                log.setService(service);
                log.setSeverity(severity);
                log.setDate(null/*TODO*/);
                log.setMessage(subnode.getString());

                result.add(log);
            }
        }

        return result;

        /*Collection<Log.Severity> severityCollection = Arrays.asList(severities);

        List<Log> testLogs = createTestLogs();
        List<Log> result = new ArrayList<Log>();
        for (Log testLog : testLogs) {
            if (severityCollection.contains(testLog.getSeverity())) {
                result.add(testLog);
            }
        }

        return result;
        */
    }

    public void addLogs(User user, Collection<Log> logs) {
        for (Log log : logs) {
            addLog(user, log);
        }
    }

    public static List<Log> createTestLogs() {
        Log log1 = new Log();
        log1.setDate(new Date());
        log1.setSeverity(Log.Severity.DEBUG);
        log1.setMessage("Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");

        Log log2 = new Log();
        log2.setDate(new Date());
        log2.setSeverity(Log.Severity.INFO);
        log2.setMessage("Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");

        Log log3 = new Log();
        log3.setDate(new Date());
        log3.setSeverity(Log.Severity.WARN);
        log3.setMessage("Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");

        Log log4 = new Log();
        log4.setDate(new Date());
        log4.setSeverity(Log.Severity.ERROR);
        log4.setMessage("Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");

        Log log5 = new Log();
        log5.setDate(new Date());
        log5.setSeverity(Log.Severity.FATAL);
        log5.setMessage("Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");

        List<Log> result = new ArrayList<Log>();
        result.add(log1);
        result.add(log2);
        result.add(log3);
        result.add(log4);
        result.add(log5);

        return result;
    }

    public void addLog(User user, Log log) {
        Database.addToNode(
                log.getMessage(), user.getLogin(), log.getService().toString(),
                log.getSeverity().toString(), log.getDate().toString()
        );
    }
}
