package com.uosipa.globalsdb.web.page.frame;

import com.uosipa.globalsdb.model.User;
import org.nocturne.main.Frame;

/**
 * @author Dmitry Levshunov (dlevshunov@uosipa.com)
 */
public abstract class ApplicationFrame extends Frame {
    protected static final String AUTHORIZED_USER_SESSION_KEY = "authorizedUser";

    protected User getUser() {
        return getSession(AUTHORIZED_USER_SESSION_KEY, User.class);
    }

    public static class Link {
        private final String address;
        private final String text;
        private final String title;
        private final boolean selected;

        public Link(String address, String text) {
            this(address, text, false);
        }

        public Link(String address, String text, boolean selected) {
            this(address, text, "", selected);
        }

        public Link(String address, String text, String title, boolean selected) {
            this.address = address;
            this.text = text;
            this.title = title;
            this.selected = selected;
        }

        public String getAddress() {
            return address;
        }

        public String getText() {
            return text;
        }

        public String getTitle() {
            return title;
        }

        public boolean isSelected() {
            return selected;
        }
    }
}
