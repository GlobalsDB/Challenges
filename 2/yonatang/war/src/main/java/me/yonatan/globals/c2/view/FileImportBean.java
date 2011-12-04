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
import org.primefaces.context.RequestContext;

@SuppressWarnings("serial")
@Named
@SessionScoped
public class FileImportBean implements Serializable {

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
		System.out.println("Using file "+localFileLocation);
		if (StringUtils.isBlank(localFileLocation)) {
			System.out.println("Missing file!!");
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "File error", "File is required."));
			return; // TODO
		}

		File file = new File(localFileLocation);
		if (!file.isFile()) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "File error", "File "+localFileLocation+" not found."));
			System.out.println("File not exists!!");
			return; // TODO
		}

		System.out.println("Loading file " + logTableBeanCreator);
		try {
			String handler = dbManager.importLocalFile(file);
			LogTableBean ltb = logTableBeanCreator.get();
			if (ltb.getLogFile() != null) {
				throw new IllegalStateException("???" + ltb);
			}
			ltb.setLogFile(dbManager.getFileInfo(handler));
			int idx=tabs.addTab(ltb);
			System.out.println("New tab index is "+idx);
			RequestContext.getCurrentInstance().execute("tabsWidget.select("+idx+");");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
