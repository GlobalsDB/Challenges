package me.yonatan.globalsdb.c2;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;

public class Window extends JPanel {

	public Window(String url) {
		GridLayout gl = new GridLayout(1, 1);
		this.setLayout(new GridLayout(1, 1));
		JPanel webBrowserPanel = new JPanel(gl);
		JWebBrowser webBrowser = new JWebBrowser();
		webBrowserPanel.add(webBrowser);

		webBrowser.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		webBrowser.setMenuBarVisible(false);
		webBrowser.setLocationBarVisible(false);
		webBrowser.setButtonBarVisible(false);
		webBrowser.setStatusBarVisible(false);
		add(webBrowserPanel, BorderLayout.CENTER);
		webBrowser.navigate(url);

	}
}
