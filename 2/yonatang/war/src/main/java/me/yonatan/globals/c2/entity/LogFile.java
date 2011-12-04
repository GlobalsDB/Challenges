package me.yonatan.globals.c2.entity;

import java.io.File;
import java.io.Serializable;

import javax.enterprise.inject.Model;

import org.apache.commons.io.FilenameUtils;
import org.joda.time.DateTime;

import lombok.Data;
import lombok.NonNull;

@SuppressWarnings("serial")
@Data
@Model
public class LogFile implements Serializable {

	@NonNull
	private String fileName;

	@NonNull
	private DateTime lastUpdated;

	@NonNull
	private String handler;

	public String getName() {
		return FilenameUtils.getName(fileName);
	}

	public File getFile() {
		File file = new File(fileName);
		if (file.isFile())
			return file;
		return null;
	}

}
