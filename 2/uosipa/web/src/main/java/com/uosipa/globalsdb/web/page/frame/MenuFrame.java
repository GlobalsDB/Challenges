package com.uosipa.globalsdb.web.page.frame;

import com.uosipa.globalsdb.model.Service;
import com.uosipa.globalsdb.web.page.UserPage;
import com.uosipa.globalsdb.web.page.logs.LogsPage;
import com.uosipa.globalsdb.web.page.refreshable.RefreshableLogsPage;
import org.nocturne.link.Links;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dmitry Levshunov (levshunov.d@gmail.com)
 */
public class MenuFrame extends ApplicationFrame {
    private Class<? extends UserPage> currentPageClass;

    public MenuFrame(Class<? extends UserPage> currentPageClass) {
        this.currentPageClass = currentPageClass;
    }

    @Override
    public void action() {
        List<Link> links = new ArrayList<Link>();

        if (currentPageClass.equals(LogsPage.class)) {
            links.add(new Link(Links.getLink(
                    LogsPage.class, "service", Service.HTTPD
            ), $("Logs"), true));
        } else {
            links.add(new Link(Links.getLink(
                    LogsPage.class, "service", Service.HTTPD
            ), $("Logs"), false));
        }

        if (currentPageClass.equals(RefreshableLogsPage.class)) {
            links.add(new Link(Links.getLink(
                    RefreshableLogsPage.class, "service", Service.HTTPD
            ), $("Local service logs"), true));
        } else {
            links.add(new Link(Links.getLink(
                    RefreshableLogsPage.class, "service", Service.HTTPD
            ), $("Local service logs"), false));
        }

        put("links", links);
    }
}
