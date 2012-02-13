package com.xlab.glodocs.api.journal;

import java.math.BigDecimal;
import java.text.Bidi;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.intersys.globals.NodeReference;
import com.intersys.globals.ValueList;
import com.xlab.glodocs.api.DBConnection;
import com.xlab.glodocs.api.utils.Constants;
import com.xlab.glodocs.api.utils.GlodocsException;

/**
 * Single history event in database
 * 
 * @author Lapitskiy Anton
 * 
 */
public class HistoryEvent {

	private BigDecimal id;

	/**
	 * Id of user that commited event
	 */
	private String userId;

	/**
	 * Operation type
	 * 
	 * @see JournalConstants
	 */
	private int operationType;

	/**
	 * Date of event
	 */
	private Date date;

	/**
	 * Collection's id associated with event
	 */
	private String collectionId;

	/**
	 * Documents's id associated with event <br />
	 * Can be null if event is only collection event: adding, removing,
	 * modifying and clearing collection
	 */
	private String docId;

	/**
	 * Getting HistoryEvent from database by {@link HistoryEvent#id}
	 * 
	 * @param id
	 *            Event's id
	 */
	public HistoryEvent(long id) {
		this.id = new BigDecimal(id);
		try {
			loadData();
		} catch (GlodocsException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Private loading of HistoryEvent from database <br />
	 * Event's parameters are {@link HistoryEvent#userId},
	 * {@link HistoryEvent#operationType} and others
	 * 
	 * @throws GlodocsException
	 * @throws ParseException
	 */
	private void loadData() throws GlodocsException, ParseException {
		DBConnection connection = DBConnection.getInstance();
		NodeReference node = connection
				.createNodeReference(Constants.GLOBAL_JOURNAL);
		node.appendSubscript(id.toString());
		ValueList list = node.getList();
		userId = list.getNextString();
		operationType = list.getNextInt();
		date = (new SimpleDateFormat(Constants.DATE_FORMAT).parse(list
				.getNextString()));
		collectionId = list.getNextString();
		if (operationType == JournalConstants.ADD_RECORD
				|| operationType == JournalConstants.MODIFY_RECORD
				|| operationType == JournalConstants.REMOVE_RECORD)
			docId = list.getNextString();

	}

	public String getCollectionId() {
		return collectionId;
	}

	public String getDocId() {
		return docId;
	}

	public BigDecimal getId() {
		return id;
	}

	public String getUserId() {
		return userId;
	}

	public int getOperationType() {
		return operationType;
	}

	public Date getDate() {
		return date;
	}

	/**
	 * Saving new event data to database <br />
	 * Use only for <b>collection's events</b>
	 * 
	 * @param id2
	 *            new event's id
	 * @param userId2
	 *            user that performed event
	 * @param operationType2
	 *            event's type
	 * @param collectId2
	 *            collection was modified
	 * @throws GlodocsException
	 */
	private static void saveData(BigDecimal id2, String userId2,
			int operationType2, String collectId2) throws GlodocsException {
		DBConnection connection = DBConnection.getInstance();
		NodeReference node = connection
				.createNodeReference(Constants.GLOBAL_JOURNAL);
		node.appendSubscript(id2.toString());
		ValueList list = connection.createList();
		list.append(userId2, operationType2,
				new SimpleDateFormat(Constants.DATE_FORMAT).format(Calendar
						.getInstance().getTime()), collectId2);
		node.set(list);
	}

	/**
	 * Saving new event data to datavase <br />
	 * Use only for <b>document's events</b>
	 * 
	 * @param id2
	 *            new event's id
	 * @param userId2
	 *            user that performed event
	 * @param operationType2
	 *            event's type
	 * @param collectId2
	 *            collection that was modified
	 * @param docId2
	 *            document that was modified
	 * @throws GlodocsException
	 */
	private static void saveData(BigDecimal id2, String userId2,
			int operationType2, String collectId2, String docId2)
			throws GlodocsException {
		DBConnection connection = DBConnection.getInstance();
		NodeReference node = connection
				.createNodeReference(Constants.GLOBAL_JOURNAL);
		node.appendSubscript(id2.toString());
		ValueList list = connection.createList();
		list.append(userId2, operationType2,
				new SimpleDateFormat(Constants.DATE_FORMAT).format(Calendar
						.getInstance().getTime()), collectId2, docId2);
		node.set(list);
	}

	/**
	 * Adding event to Journal <br />
	 * Use only for <b>collection's events</b>
	 * 
	 * @param userId
	 *            user that performed event
	 * @param operationType
	 *            event's type
	 * @param collectId
	 *            collection was modified
	 */
	public static void addEvent(String userId, int operationType,
			String collectId) {
		try {
			BigDecimal id = Journal.getMaxId();
			saveData(id, userId, operationType, collectId);
			Journal.increaseMaxId();
		} catch (GlodocsException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adding event to Journal <br />
	 * Use only for <b>document's events</b>
	 * 
	 * @param userId
	 *            user that performed event
	 * @param operationType
	 *            event's type
	 * @param string
	 *            collection was modified
	 * @param id2
	 *            document that was modified
	 */
	public static void addEvent(String userId, int operationType,
			String string, String id2) {
		try {
			BigDecimal id = Journal.getMaxId();
			saveData(id, userId, operationType, string, id2);
			Journal.increaseMaxId();
		} catch (GlodocsException e) {
			e.printStackTrace();
		}
	}

}
