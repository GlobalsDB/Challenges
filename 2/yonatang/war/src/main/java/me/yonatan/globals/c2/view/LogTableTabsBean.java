package me.yonatan.globals.c2.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import me.yonatan.globals.c2.action.DbManager;

import org.primefaces.event.TabCloseEvent;

@SuppressWarnings("serial")
@Named
@SessionScoped
public class LogTableTabsBean implements Serializable {

	private List<LogTableBean> tabs = new ArrayList<LogTableBean>();

	private List<LogTableBean> unmodifableTabs = Collections.unmodifiableList(tabs);

	private HashMap<String, LogTableBean> tabSet = new HashMap<String, LogTableBean>();

	public List<LogTableBean> getTabs() {
		return unmodifableTabs;
	}

	@Inject
	private DbManager dbManager;

	public int addTab(LogTableBean ltb) {
		if (tabSet == null)
			tabSet = new HashMap<String, LogTableBean>();
		String key = ltb.getLogFile().getHandler();
		if (tabSet.containsKey(key)) {
			tabs.remove(tabSet.get(key));
		}
		tabs.add(ltb);
		tabSet.put(key, ltb);
		return tabs.size() - 1;
	}

	public void onTabClose(TabCloseEvent event) {
		Object data = event.getData();
		if (data != null && data instanceof LogTableBean) {
			LogTableBean ltb = (LogTableBean) data;
			tabs.remove(ltb);
			if (ltb.getLogFile() != null && ltb.getLogFile().getHandler() != null) {
				tabSet.remove(ltb.getLogFile().getHandler());
				dbManager.removeLogfile(ltb.getLogFile().getHandler());
			}
		}

	}

}
