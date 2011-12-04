package me.yonatan.globals.c2.parser;

import me.yonatan.globals.c2.entity.LogRecord;

public interface LogParser {

	public LogRecord parse(String line);
}
