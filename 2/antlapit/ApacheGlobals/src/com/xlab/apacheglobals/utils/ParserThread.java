package com.xlab.apacheglobals.utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import com.intersys.globals.NodeReference;
import com.xlab.apacheglobals.GlobalsConnection;

public class ParserThread extends Thread {

	private boolean continueLoad;

	public ParserThread(boolean stay) {
		super();
		continueLoad = stay;
		if (!continueLoad) {
			NodeReference node;
			try {
				node = GlobalsConnection.getInstance().createNodeReference(
						Configuration.DATA_GLOBAL);
				node.kill();
				node = GlobalsConnection.getInstance().createNodeReference(
						Configuration.INDEX_GLOBAL);
				node.kill();
				Parser.recordImported = 0;
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	@Override
	public void run() {
		try {
			FileInputStream fstream = new FileInputStream(
					Configuration.logFileName);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine = "";
			int counter = 0;
			LogRecord record = null;
			while (continueLoad || ((strLine = br.readLine()) != null)) {
				if (strLine == null)
					sleep(5000);
				else {
					++counter;
					if (counter > Parser.recordImported) {
						record = new LogRecord(counter, strLine);
						record.save();
						Parser.recordImported = counter;
						Parser.saveImported();
					}
				}
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
