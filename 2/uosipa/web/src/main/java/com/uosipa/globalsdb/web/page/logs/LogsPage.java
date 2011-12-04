package com.uosipa.globalsdb.web.page.logs;

import com.uosipa.globalsdb.LogParser;
import com.uosipa.globalsdb.dao.LogDao;
import com.uosipa.globalsdb.model.Log;
import com.uosipa.globalsdb.model.Service;
import com.uosipa.globalsdb.util.StringUtil;
import com.uosipa.globalsdb.web.page.UserPage;
import com.uosipa.globalsdb.web.page.frame.MenuFrame;
import org.nocturne.annotation.Action;
import org.nocturne.annotation.Parameter;
import org.nocturne.link.Link;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dmitry Levshunov (levshunov.d@gmail.com)
 */
@Link("logs/{service}")
public class LogsPage extends UserPage {
    @Parameter
    private Service service;

    @Override
    public String getPageTitle() {
        return $(service.toString()) + " " + $("logs");
    }

    @Override
    public void initializeAction() {
        super.initializeAction();

        addCss("css/logs.css");
        addJs("js/logs.js");
    }

    @Override
    public void action() {
        Log.Severity[] severities = getSeveritiesFilter();

        put("logs", LogDao.getInstance().findLogs(getUser(), service, severities));
        put("showLogsConfig", new ShowLogsConfig(severities));
    }

    @Parameter(stripMode = Parameter.StripMode.NONE)
    private String logFile;

    @Action("uploadLogFile")
    public void uploadLogFile() {
        LogDao.getInstance().addLogs(getUser(), LogParser.parse(StringUtil.splitToLines(logFile), service));
        abortWithReload();
    }

    private Log.Severity[] getSeveritiesFilter() {
        Log.Severity[] severities = getSession("logs-" + service, Log.Severity[].class);
        if (severities == null) {
            severities = Log.Severity.values();
        }

        return severities;
    }

    @Action("applyFilter")
    public void applyFilter() {
        String[] checkedSeverities = (String[]) getRequest().getParameterMap().get("checkedSeverities[]");
        putSession("logs-" + service, parseSeveritiesList(checkedSeverities));
        skipTemplate();
    }

    private Log.Severity[] parseSeveritiesList(String[] severities) {
        if (severities == null) {
            return new Log.Severity[0];
        }

        List<Log.Severity> parsedSeverities = new ArrayList<Log.Severity>();

        for (String severity : severities) {
            if ("showDebug".equals(severity)) {
                parsedSeverities.add(Log.Severity.DEBUG);
            } else if ("showInfo".equals(severity)) {
                parsedSeverities.add(Log.Severity.INFO);
            } else if ("showWarn".equals(severity)) {
                parsedSeverities.add(Log.Severity.WARN);
            } else if ("showError".equals(severity)) {
                parsedSeverities.add(Log.Severity.ERROR);
            } else if ("showFatal".equals(severity)) {
                parsedSeverities.add(Log.Severity.FATAL);
            } else if ("showUnknown".equals(severity)) {
                parsedSeverities.add(Log.Severity.UNKNOWN);
            }
        }

        Log.Severity[] result = new Log.Severity[parsedSeverities.size()];
        for (int i = 0; i < parsedSeverities.size(); i++) {
            result[i] = parsedSeverities.get(i);
        }

        return result;
    }

    @Override
    public void finalizeAction() {
        parse("menuFrame", new MenuFrame(this.getClass()));
        parse("sectionMenuFrame", new SectionMenuFrame(service));

        super.finalizeAction();
    }

    public static class ShowLogsConfig {
        private boolean showDebug;
        private boolean showInfo;
        private boolean showWarn;
        private boolean showError;
        private boolean showFatal;
        private boolean showUnknown;

        private ShowLogsConfig() {
            // No operations.
        }

        public ShowLogsConfig(Log.Severity[] severities) {
            if (severities == null) {
                showDebug = true;
                showInfo = true;
                showWarn = true;
                showError = true;
                showFatal = true;
                showUnknown = true;
            } else {
                for (Log.Severity severity : severities) {
                    switch (severity) {
                        case DEBUG:
                            showDebug = true;
                            break;
                        case INFO:
                            showInfo = true;
                            break;
                        case WARN:
                            showWarn = true;
                            break;
                        case ERROR:
                            showError = true;
                            break;
                        case FATAL:
                            showFatal = true;
                            break;
                        case UNKNOWN:
                            showUnknown = true;
                            break;
                        default:
                            throw new IllegalStateException("Unexpected severity " + severity + ".");
                    }
                }
            }
        }

        public boolean isShowDebug() {
            return showDebug;
        }

        public boolean isShowInfo() {
            return showInfo;
        }

        public boolean isShowWarn() {
            return showWarn;
        }

        public boolean isShowError() {
            return showError;
        }

        public boolean isShowFatal() {
            return showFatal;
        }

        public boolean isShowUnknown() {
            return showUnknown;
        }
    }
}
