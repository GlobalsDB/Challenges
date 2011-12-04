package me.yonatan.globals.c2.entity;

import java.io.Serializable;

import javax.enterprise.inject.Model;

import lombok.Data;

import org.joda.time.DateTime;

@SuppressWarnings("serial")
@Data
@Model
public class LogRecord implements Serializable {

	private DateTime timestamp;

	private String ip;

	private String description;

	private long id;
}
