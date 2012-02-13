package com.xlab.glodocs.api.journal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import com.intersys.globals.NodeReference;
import com.intersys.globals.ValueList;
import com.xlab.glodocs.api.DBConnection;
import com.xlab.glodocs.api.utils.Constants;
import com.xlab.glodocs.api.utils.GlodocsException;

/**
 * Journal implementation in Glodocs <br />
 * Journal is a list of {@link HistoryEvent}
 * 
 * @author Lapitskiy Anton
 * 
 */
@SuppressWarnings("serial")
public class Journal extends HashMap<String, HistoryEvent> {

	/**
	 * Getting last HistoryEvent id
	 * 
	 * @return maximum id in journal
	 * @throws GlodocsException
	 */
	public static BigDecimal getMaxId() throws GlodocsException {
		DBConnection connection = DBConnection.getInstance();
		NodeReference node = connection
				.createNodeReference(Constants.GLOBAL_JOURNAL);
		ValueList list = node.getList();
		if (list == null) {
			list = connection.createList();
			list.append(0);
			node.set(list);
			return new BigDecimal(0);
		}

		return new BigDecimal(list.getNextLong());
	}

	/**
	 * Increasing last HistoryEvent id by 1
	 * 
	 * @throws GlodocsException
	 */
	public static void increaseMaxId() throws GlodocsException {
		DBConnection connection = DBConnection.getInstance();
		NodeReference node = connection
				.createNodeReference(Constants.GLOBAL_JOURNAL);
		ValueList list = node.getList();
		BigDecimal bd = new BigDecimal(list.getNextLong());
		bd = bd.add(new BigDecimal(1));
		list.clear();
		list.append(bd.toString());
		node.set(list);
	}

	/**
	 * Getting ordered list of {@link HistoryEvent}
	 * 
	 * @return list of HistoryEvent
	 * @throws GlodocsException
	 */
	public static ArrayList<HistoryEvent> getHistory() throws GlodocsException {
		ArrayList<HistoryEvent> list = new ArrayList<HistoryEvent>();
		long max = getMaxId().longValue();
		HistoryEvent event = null;
		for (long i = 0; i < max; ++i) {
			try {
				event = new HistoryEvent(i);
				list.add(event);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		return list;
	}

}
