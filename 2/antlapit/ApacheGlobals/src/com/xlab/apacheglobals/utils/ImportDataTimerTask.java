package com.xlab.apacheglobals.utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.TimerTask;

public class ImportDataTimerTask extends TimerTask {

	@Override
	public void run() {
		try {
			FileInputStream fstream = new FileInputStream(
					Configuration.logFileName);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			int counter = 0;
			LogRecord record = null;
			while ((strLine = br.readLine()) != null) {
				++counter;
				if (counter > ImportDataTimer.recordImported) {
					record = new LogRecord(counter, strLine);
					record.save();
					ImportDataTimer.recordImported = counter;
				}
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
