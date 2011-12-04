package com.uosipa.globalsdb.web.page.refreshable;

import com.uosipa.globalsdb.model.Service;
import com.uosipa.globalsdb.web.page.frame.ApplicationFrame;
import org.nocturne.link.Links;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Dmitry Levshunov (levshunov.d@gmail.com)
 */
class SectionMenuFrame extends ApplicationFrame {
    @Override
    public void action() {
        Collection<Link> links = new ArrayList<Link>();

        links.add(new Link(Links.getLink(RefreshableLogsPage.class), $(Service.HTTPD.toString()), true));

        put("links", links);
    }
}
