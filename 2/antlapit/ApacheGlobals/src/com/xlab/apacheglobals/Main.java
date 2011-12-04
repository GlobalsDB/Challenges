package com.xlab.apacheglobals;

import java.awt.EventQueue;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import com.xlab.apacheglobals.gui.ApacheGlobalsFrame;
import com.xlab.apacheglobals.utils.Configuration;
import com.xlab.apacheglobals.utils.Parser;
import com.xlab.apacheglobals.utils.ParserThread;
import com.xlab.apacheglobals.utils.SearchEngine;

public class Main {

	/**
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException {
		try {
			Configuration.loadConfig();
			Parser.load();

			// ParserThread pt = new ParserThread(false);
			// pt.run();

			/*
			 * EventQueue.invokeLater(new Runnable() { public void run() { try {
			 * ApacheGlobalsFrame frame = new ApacheGlobalsFrame();
			 * frame.setVisible(true); } catch (Exception e) {
			 * e.printStackTrace(); } } });
			 */

			ArrayList<Integer> list = SearchEngine.getRangeByOneField(
					"response", "200", "600");
			System.out.println(list);

			// ArrayList<Integer> al = SearchEngine.getRecordByOneField("ip",
			// "127.0.0.1");
			// System.out.println(al.size());
		} catch (Exception e) {
			// Catch exception if any
			e.printStackTrace();
			System.err.println("Error: " + e.getMessage());
		}
	}

}
