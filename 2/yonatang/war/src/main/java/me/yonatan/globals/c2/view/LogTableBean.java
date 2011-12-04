package me.yonatan.globals.c2.view;

import java.io.File;
import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;
import lombok.Setter;
import lombok.Synchronized;
import me.yonatan.globals.c2.action.DbManager;
import me.yonatan.globals.c2.entity.LogFile;
import me.yonatan.globals.c2.entity.LogRecord;

import org.apache.commons.io.FileUtils;
import org.jboss.logging.Logger;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.data.PageEvent;

@SuppressWarnings("serial")
@Named
public class LogTableBean implements Serializable {

	@Inject
	private Logger log;

	@Inject
	private DbManager dbManager;

	@Inject
	@Getter
	private LogTableDataModel dataModel;

	private LogRecord selectedRowRecord;

	@Getter
	private LogFile logFile;

	@Getter
	@Setter
	private boolean autoRefresh = false;

	public void setLogFile(LogFile logFile) {
		String handler = logFile.getHandler();
		dataModel.setHandler(logFile.getHandler());
		dataModel.setRowCount(dbManager.getLogCountInt(handler));
		this.logFile = logFile;
	}

	@Synchronized
	public void pollForChanges() {
		if (FileUtils.isFileNewer(new File(logFile.getFileName()), logFile.getLastUpdated().toDate())) {
			System.out.println("AutoRefresh? " + autoRefresh);
			System.out.println("File has changed. Reloading.");
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Successful", "Hello "));
			dbManager.reloadFile(logFile);
			setLogFile(dbManager.getFileInfo(logFile.getHandler()));
			if (autoRefresh) {
				RequestContext requestContext = RequestContext.getCurrentInstance();
				requestContext.execute("refreshTable();");
			}

		}
	}

	public void onPage(PageEvent event) {
		DataTable dt = (DataTable) event.getSource();
		autoRefresh = (event.getPage() == dt.getPageCount() - 1);
		System.out.println(autoRefresh);
	}

	public void onSelect(SelectEvent event) {
		DataTable dt = (DataTable) event.getSource();
		selectedRowRecord = (LogRecord) dt.getSelection();
	}

	public void filterByIp(ActionEvent event) {
		dataModel.setIpFilter(selectedRowRecord.getIp());
	}

}
