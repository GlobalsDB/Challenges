package me.yonatan.globals.c2.view;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Instance;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;
import lombok.Setter;
import me.yonatan.globals.c2.action.DbManager;

import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;
import org.primefaces.context.RequestContext;

@SuppressWarnings("serial")
@Named
@SessionScoped
public class FileImportBean implements Serializable {

	@Inject
	private Logger log;

	@Inject
	private LogTableTabsBean tabs;

	@Inject
	private DbManager dbManager;

	@Getter
	@Setter
	private String localFileLocation;

	@Inject
	private Instance<LogTableBean> logTableBeanCreator;

	public void openLocal() {
		log.infov("opening using file {0}", localFileLocation);
		if (StringUtils.isBlank(localFileLocation)) {
			log.warn("No file was specified");
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "File error", "File is required."));
			return;
		}

		File file = new File(localFileLocation);
		if (!file.isFile()) {
			log.warnv("File {0} is missing", file);
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "File error", "File " + localFileLocation
					+ " not found."));
			return;
		}

		try {
			String handler = dbManager.importLocalFile(file);
			LogTableBean ltb = logTableBeanCreator.get();
			if (ltb.getLogFile() != null) {
				throw new IllegalStateException("Something very wierd happend " + ltb);
			}
			ltb.setLogFile(dbManager.getFileInfo(handler));
			int idx = tabs.addTab(ltb);
			log.infov("New tab index is {0}", idx);
			RequestContext.getCurrentInstance().execute("tabsWidget.select(" + idx + ");");

		} catch (IOException e) {
			log.errorv(e, "Problem opening file");
		}
	}

}
